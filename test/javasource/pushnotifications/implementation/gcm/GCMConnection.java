package pushnotifications.implementation.gcm;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.SSLSocketFactory;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.PacketInterceptor;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.json.JSONObject;
import org.json.simple.JSONValue;
import org.xmlpull.v1.XmlPullParser;

import pushnotifications.implementation.MessagingServiceConnection;
import pushnotifications.proxies.Device;
import pushnotifications.proxies.GCMSettings;
import pushnotifications.proxies.GoogleMessage;
import pushnotifications.proxies.constants.Constants;

import com.mendix.core.Core;
import com.mendix.core.CoreException;
import com.mendix.logging.ILogNode;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.systemwideinterfaces.core.IMendixObject;

/**
 * Implemented the GCMConnection as a singleton!
 * 
 * @author alm
 * @author res
 */
public class GCMConnection implements MessagingServiceConnection<GCMSettings, GoogleMessage> {
	private static GCMConnection theConnection;

	static final String GCM_ELEMENT_NAME = "gcm";
	static final String GCM_NAMESPACE = "google:mobile:data";

	private XMPPTCPConnection connection;
	private ILogNode logger;

	/**
	 * Indicates whether the connection is in draining state, which means that
	 * it will not accept any new downstream messages.
	 */
	protected volatile boolean connectionDraining = false;

	static {

		ProviderManager.addExtensionProvider(GCM_ELEMENT_NAME, GCM_NAMESPACE,
				new PacketExtensionProvider() {
					@Override
					public PacketExtension parseExtension(XmlPullParser parser)
							throws Exception {
						String json = parser.getText();
						return new GcmPacketExtension(json);
					}
				});
	}

	public void stop() {
		try {
			this.connection.disconnect();
		} catch (Exception e) {
		}
	}
	
	private GCMConnection() {
		logger = Core.getLogger(Constants.getLogNode());
	}

	@Override
	public void start(GCMSettings settings) {
		ConnectionConfiguration config = new ConnectionConfiguration(
				settings.getXMPPServer(), settings.getXMPPPort());
		config.setSecurityMode(SecurityMode.enabled);
		config.setReconnectionAllowed(true);
		config.setRosterLoadedAtLogin(false);
		config.setSendPresence(false);
		config.setSocketFactory(SSLSocketFactory.getDefault());

		connection = new XMPPTCPConnection(config);
		try {
			connection.connect();
		} catch (Exception e) {
			logger.error("GCM: Error while connecting: " + e.toString(), e);
			return;
		}
		
		connection.addConnectionListener(new LoggingConnectionListener());
		connection.addPacketListener(new GCMPacketListener(this), new PacketTypeFilter(Message.class));
		
		// Log all outgoing packets
        connection.addPacketInterceptor(new PacketInterceptor() {
            @Override
                public void interceptPacket(Packet packet) {
                    logger.trace("Sent: " + packet.toXML());
                }
            }, new PacketTypeFilter(Message.class));

        try {
			connection.login(settings.getSenderId() + "@gcm.googleapis.com", settings.getAPIKey());
		} catch (Exception e) {
			logger.error("GCM: Error logging in at Google XMPP: " + e.toString(), e);
		}
        
        
        logger.info("GCM: Connected to GCM.");
        
	}

	public void sendMessages(List<GoogleMessage> messages) {
		messages.forEach(this::sendMessage);
	}
	
	public static GCMConnection getConnection() {
		if (theConnection == null) {
			theConnection = new GCMConnection();
		}
		return theConnection;
	}

	void send(String jsonRequest) throws NotConnectedException {
		Packet request = new GcmPacketExtension(jsonRequest).toPacket();
		connection.sendPacket(request);
	}

	String nextMessageId() {
		return "m-" + UUID.randomUUID().toString();
	}

	boolean sendDownstreamMessage(String jsonRequest)
			throws NotConnectedException {
		if (!connectionDraining) {
			send(jsonRequest);
			return true;
		}
		logger.info("GCM: Dropping downstream message since the connection is draining");
		return false;
	}

	/**
	 * Handles an upstream data message from a device application.
	 * 
	 * <p>
	 * This sample echo server sends an echo message back to the device.
	 * Subclasses should override this method to properly process upstream
	 * messages.
	 */
	protected void handleUpstreamMessage(Map<String, Object> jsonObject) {
        // PackageName of the application that sent this message.
        String category = (String) jsonObject.get("category");
        String from = (String) jsonObject.get("from");
        @SuppressWarnings("unchecked")
        Map<String, String> payload = (Map<String, String>) jsonObject.get("data");
        payload.put("ECHO", "Application: " + category);

        // Send an ECHO response back
        String echo = createJsonMessage(from, nextMessageId(), payload,
                "echo:CollapseKey", null, false);

        try {
            sendDownstreamMessage(echo);
        } catch (NotConnectedException e) {
            logger.info("GCM: Not connected anymore, echo message is not sent", e);
        }
    }

	/**
	 * Handles an ACK.
	 * 
	 * <p>
	 * Logs a INFO message, but subclasses could override it to properly handle
	 * ACKs.
	 */
	protected void handleAckReceipt(Map<String, Object> jsonObject) {
        String messageId = (String) jsonObject.get("message_id");
        String from = (String) jsonObject.get("from");
        logger.info("GCM: handleAckReceipt() from: " + from + ",messageId: " + messageId);
    }

	/**
	 * Handles a NACK.
	 * 
	 * <p>
	 * Logs a INFO message, but subclasses could override it to properly handle
	 * NACKs.
	 */
	protected void handleNackReceipt(Map<String, Object> jsonObject) {
        String messageId = (String) jsonObject.get("message_id");
        String from = (String) jsonObject.get("from");
        logger.info("GCM: handleNackReceipt() from: " + from + ",messageId: " + messageId);
        logger.trace("Nack contents: " + jsonObject.toString());
        
        IContext sysContext = Core.createSystemContext();
        
        /**
         * Remove the device from the list when the NACK error code is:
         *  - BAD_REGISTRATION
         *  - DEVICE_UNREGISTERED
         */
        if (jsonObject.containsKey("error")) {
        	String errorCode = (String) jsonObject.get("error");
        	if (errorCode.equals("BAD_REGISTRATION") || errorCode.equals("DEVICE_UNREGISTERED")) {
        		logger.info("Removing device with registration " + from + " because it's unregistered.");
        		
        		try {
					List<IMendixObject> devices = Core.retrieveXPathQueryEscaped(sysContext, 
							"//" + Device.entityName + "[" + 
								   Device.MemberNames.RegistrationID.toString() +
								   "='%s']", 1, 0, new HashMap<String, String>(), 0, from);
					if (devices.size() < 1) {
						logger.error("Expected to find device " + from + " but not found?");
					} else {
						Core.delete(sysContext, devices);
					}
				} catch (CoreException e) {
					logger.error("Unable to remove device " + from + " : " + 
							e.toString(), e);
				}
        						
        	}
        }
    }

	void handleControlMessage(Map<String, Object> jsonObject) {
        logger.info("GCM: handleControlMessage(): " + jsonObject);
        String controlType = (String) jsonObject.get("control_type");
        if ("CONNECTION_DRAINING".equals(controlType)) {
            connectionDraining = true;
        } else {
            logger.info("GCM: Unrecognized control type: " + controlType + ". This could " + 
                    "happen if new features are added to the CCS protocol.");
        }
    }

	/**
	 * Creates a JSON encoded GCM message.
	 * 
	 * @param to
	 *            RegistrationId of the target device (Required).
	 * @param messageId
	 *            Unique messageId for which CCS will send an "ack/nack"
	 *            (Required).
	 * @param payload
	 *            Message content intended for the application. (Optional).
	 * @param collapseKey
	 *            GCM collapse_key parameter (Optional).
	 * @param timeToLive
	 *            GCM time_to_live parameter (Optional).
	 * @param delayWhileIdle
	 *            GCM delay_while_idle parameter (Optional).
	 * @return JSON encoded GCM message.
	 */
	String createJsonMessage(String to, String messageId,
			Map<String, String> payload, String collapseKey, Long timeToLive,
			Boolean delayWhileIdle) {
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("to", to);
		if (collapseKey != null) {
			message.put("collapse_key", collapseKey);
		}
		if (timeToLive != null) {
			message.put("time_to_live", timeToLive);
		}
		if (delayWhileIdle != null && delayWhileIdle) {
			message.put("delay_while_idle", true);
		}
		message.put("message_id", messageId);
		message.put("data", payload);
		return JSONObject.valueToString(message);
	}

	/**
	 * Creates a JSON encoded ACK message for an upstream message received from
	 * an application.
	 * 
	 * @param to
	 *            RegistrationId of the device who sent the upstream message.
	 * @param messageId
	 *            messageId of the upstream message to be acknowledged to CCS.
	 * @return JSON encoded ack.
	 */
	String createJsonAck(String to, String messageId) {
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("message_type", "ack");
		message.put("to", to);
		message.put("message_id", messageId);
		return JSONValue.toJSONString(message);
	}

	@Override
	public synchronized void sendMessage(GoogleMessage message) {
		try {
			String messageId = this.nextMessageId();
	        Map<String, String> payload = new HashMap<String, String>();
	        payload.put("message", message.getMessage());
	        payload.put("embeddedMessageId", messageId);
	        if (message.getTitle() != null && !message.getTitle().trim().isEmpty()) {
	        	payload.put("title", message.getTitle());
	        }
	        
	        String messageJson = createJsonMessage(
	        		message.getTo(), messageId, payload,
	                null, message.getTimeToLive(), true);

	        
			if(!this.sendDownstreamMessage(messageJson)) {
				/*if downstream fails need to add to fail counter else constant loop*/
				if (message.getFailedCount() >= Constants.getMaxFailedCount()) {
					message.delete();
				}
				else{
					message.setFailed(true);
					message.setFailedReason(messageJson);
					message.setFailedCount(message.getFailedCount() + 1);
					message.setQueued(true);
					message.setNextTry(new Date(
					System.currentTimeMillis() + (60000 * (message.getFailedCount() * 5) )));
					message.commit();
				}
				throw new Exception("Message not delivered, view log.");
				
			}
			
			logger.info("GCM: Successfully sent message to: " + message.getTo());
			message.delete();
		} catch (Exception e) {
			if (message.getFailedCount() > Constants.getMaxFailedCount()) {
				logger.error("GCM: Message to " + message.getTo() + " failed: " + e.toString(), e);
				message.delete();
			} else {
				message.setFailed(true);
				message.setFailedReason(e.toString());
				message.setFailedCount(message.getFailedCount() + 1);
				message.setQueued(true);
				message.setNextTry(new Date(
						System.currentTimeMillis() + (60000 * (message.getFailedCount() * 5) )));
				try {
					message.commit();
				} catch (CoreException ce) {
					logger.error(String.format("Commiting failed GCM Message with message ID %s to database failed.", message.getMessageId()), ce);
				}
				logger.warn("GCM: Message to " + message.getTo() + " failed: " + e.toString(), e);
			}
		}
		
	}
}
