package pushnotifications.implementation.apn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import pushnotifications.proxies.APNSettings;
import pushnotifications.proxies.AppleMessage;
import pushnotifications.proxies.Device;
import pushnotifications.proxies.constants.Constants;

import com.mendix.core.Core;
import com.mendix.core.CoreException;
import com.mendix.logging.ILogNode;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.systemwideinterfaces.core.IMendixObject;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import com.notnoop.apns.PayloadBuilder;

import encryption.proxies.microflows.Microflows;

public class APNConnection {
	private static APNConnection theConnection;
	private ILogNode logger;
	private ApnsService service;
	
	private APNConnection() {
		logger = Core.getLogger(Constants.getLogNode());
	}
	
	public void stop() {
		
		try {
			service.stop();
		} catch (Exception e) {
			logger.error("APN: Error while stopping APN: " + e.toString(), e);
		}
	}
	
	public void start(APNSettings settings) throws CoreException {
		File cert;
		IContext sysContext = Core.createSystemContext();
		
		try {
			cert = File.createTempFile("cert", ".p12");
			IOUtils.copy(Core.getFileDocumentContent(sysContext, settings.getAPNSettings_APNCertificate().getMendixObject()), 
					new FileOutputStream(cert));
		} catch (IOException e) {
			logger.error("APN: Error while creating temp file for certificate: " + e.toString(), e);
			return;
		}
		
		String passcode = Microflows.decrypt(sysContext, 
				settings.getAPNSettings_APNCertificate().getPassCode());
		
		try {
			service = APNS.newService().withGatewayDestination(
					settings.getServer(), settings.getPort())
					.withFeedbackDestination(settings.getFeedbackServer(), settings.getFeedbackPort())
					.withCert(new FileInputStream(cert), passcode).build();
		} catch (Exception e) {
			logger.error("APN: Error while building APNS service: " + e.toString(), e);
			return;
		}
	}
	
	public synchronized void sendMessages(List<AppleMessage> messages) throws CoreException {
		for (AppleMessage message : messages) {
			try {
				PayloadBuilder builder = APNS.newPayload().alertBody(
						message.getMessage()).sound(message.getSound());
				if (message.getBadge() == null) {
					builder = builder.badge(0);
				} else {
					builder = builder.badge(message.getBadge());
				}
						
				if (message.getActionKey() != null && !message.getActionKey().trim().isEmpty()) {
					builder.actionKey(message.getActionKey());
				}
				
				if (message.getLaunchImage() != null && !message.getLaunchImage().trim().isEmpty()) {
					builder.launchImage(message.getLaunchImage());
				}
				
				builder.shrinkBody(message.getResizeAlertBodyPostfix());
				
				String payload = builder.build();
				String token = message.getTo();
				service.push(token, payload);
				logger.info("APN: Successfully sent message to: " + message.getTo());
				message.delete();
			} catch (Exception e) {
				if (message.getFailedCount() > Constants.getMaxFailedCount()) {
					logger.error("APN: Message to " + message.getTo() + " failed: " + e.toString(), e);
					message.delete();
				} else {
					message.setFailed(true);
					message.setFailedReason(e.toString());
					message.setFailedCount(message.getFailedCount() + 1);
					message.setQueued(true);
					message.setNextTry(new Date(
							System.currentTimeMillis() + (60000 * (message.getFailedCount() * 5) )));
					message.commit();
					logger.warn("APN: Message to " + message.getTo() + " failed: " + e.toString(), e);
				}
			}
		}
	}
	
	public static APNConnection getConnection() {
		if (theConnection == null) {
			theConnection = new APNConnection();
		}
		return theConnection;
	}
	
	public synchronized void pollFeedbackService() {
		logger.info("Getting inactive devices.");
		IContext sysContext = Core.createSystemContext();
		Map<String, Date> inactiveDevices = service.getInactiveDevices();
		logger.info("Found " + inactiveDevices.size() + " inactive devices.");
		if (inactiveDevices.size() < 1) {
			return;
		}
		
		StringBuffer queryString = new StringBuffer();
		String[] registrationIds = new String[inactiveDevices.size()];
		int counter = 0;
		
		for (String registrationId : inactiveDevices.keySet()) {
			registrationIds[counter] = registrationId;
			if (counter != 0) {
				queryString.append(" or ");
			}
			queryString.append(Device.MemberNames.RegistrationID.toString());
			queryString.append(" = '%s'");
			counter++;
		}
		
		try {
			List<IMendixObject> devices = Core.retrieveXPathQueryEscaped(
					sysContext, 
					"//" + Device.entityName + "[" + queryString + "]", 
					registrationIds.length, 
					0, new HashMap<String, String>(), 0, registrationIds);
			Core.delete(sysContext, devices);
		} catch (CoreException e) {
			logger.error("Unable to remove devices " + registrationIds + " : " + 
					e.toString(), e);
		}
	}

}
