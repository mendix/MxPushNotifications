package pushnotifications.implementation.apns;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

import com.mendix.core.Core;
import com.mendix.core.CoreException;
import com.mendix.systemwideinterfaces.core.IContext;

import com.turo.pushy.apns.ApnsClient;
import com.turo.pushy.apns.ApnsClientBuilder;
import com.turo.pushy.apns.PushNotificationResponse;
import com.turo.pushy.apns.auth.ApnsSigningKey;
import com.turo.pushy.apns.util.ApnsPayloadBuilder;
import com.turo.pushy.apns.util.SimpleApnsPushNotification;
import com.turo.pushy.apns.util.TokenUtil;
import com.turo.pushy.apns.util.concurrent.PushNotificationFuture;
import encryption.proxies.microflows.Microflows;
import io.netty.util.concurrent.Future;
import org.apache.commons.io.IOUtils;
import pushnotifications.proxies.*;

public class APNSConnection {
    private static APNSConnection instance = null;
	private ApnsClient apnsClient;

    public static APNSConnection getInstance() {
        if (instance == null) {
            // Thread Safe. Might be costly operation in some case
            synchronized (APNSConnection.class) {
                if (instance == null) {
                    instance = new APNSConnection();
                }
            }
        }
        return instance;
    }

	public void start(APNSSettings settings) throws CoreException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoAuthenticationTypeException, InterruptedException {
		IContext sysContext = Core.createSystemContext();

		APNSAuthenticationType authType = settings.getAuthenticationType();

		APNSStage apnsStage = settings.getStage();

        if (apnsClient != null) stop();

		if (authType == APNSAuthenticationType.Certificate) {
            APNSCertificate apnsCertificate = settings.getAPNSSettings_APNSCertificate();

            File cert = File.createTempFile("cert", ".p12");
            IOUtils.copy(Core.getFileDocumentContent(sysContext, settings.getAPNSSettings_APNSCertificate().getMendixObject()),
                    new FileOutputStream(cert));

            String passcode = Microflows.decrypt(sysContext, apnsCertificate.getPasscode());

            apnsClient = new ApnsClientBuilder()
                    .setApnsServer(apnsStage == APNSStage.Production ?
                            ApnsClientBuilder.PRODUCTION_APNS_HOST :
                            ApnsClientBuilder.DEVELOPMENT_APNS_HOST)
                    .setClientCredentials(cert, passcode)
                    .build();
        } else if (authType == APNSAuthenticationType.Token) {
            APNSToken apnsToken = settings.getAPNSSettings_APNSToken();

            File token = File.createTempFile("token", ".p8");
            IOUtils.copy(Core.getFileDocumentContent(sysContext, settings.getAPNSSettings_APNSToken().getMendixObject()),
                    new FileOutputStream(token));

            apnsClient = new ApnsClientBuilder()
                    .setApnsServer(apnsStage == APNSStage.Production ?
                            ApnsClientBuilder.PRODUCTION_APNS_HOST :
                            ApnsClientBuilder.DEVELOPMENT_APNS_HOST)
                    .setSigningKey(ApnsSigningKey.loadFromPkcs8File(token,
                            apnsToken.getTeamId(), apnsToken.getKeyId()))
                    .build();
        } else {
			throw new NoAuthenticationTypeException();
		}
	}

	public void stop() throws InterruptedException {
	    if (apnsClient != null) {
            final Future<Void> closeFuture = apnsClient.close();
            closeFuture.await();
            apnsClient = null;
        }
	}

	public void sendMessage(APNSSettings settings, Message message) throws ExecutionException, InterruptedException, MessageRejectedException, DeviceTokenInvalidException {
        final SimpleApnsPushNotification pushNotification;

        {
            final ApnsPayloadBuilder payloadBuilder = new ApnsPayloadBuilder();
            payloadBuilder.setAlertBody(message.getBody());
            payloadBuilder.setSound("default");

            if (message.getBadge() != null) {
                payloadBuilder.setBadgeNumber(message.getBadge().intValue());
            }

            if (message.getActionName() != null && !message.getActionName().trim().isEmpty()) {
                payloadBuilder.addCustomProperty("actionName", message.getActionName());

                if (message.getContextObjectGuid() != null) {
                    payloadBuilder.addCustomProperty("guid", ""+message.getContextObjectGuid());
                }
            }

            final String payload = payloadBuilder.buildWithDefaultMaximumLength();
            final String token = TokenUtil.sanitizeTokenString(message.getTo());

            pushNotification = new SimpleApnsPushNotification(token, settings.getDefaultTopic(), payload);
        }

        final PushNotificationFuture<
                SimpleApnsPushNotification,
                PushNotificationResponse<SimpleApnsPushNotification>
            > sendNotificationFuture = apnsClient.sendNotification(pushNotification);

        final PushNotificationResponse<SimpleApnsPushNotification> pushNotificationResponse = sendNotificationFuture.get();

        if (!pushNotificationResponse.isAccepted()) {
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append(pushNotificationResponse.getRejectionReason());

            if (pushNotificationResponse.getTokenInvalidationTimestamp() != null) {
                stringBuilder.append(" and the token is invalid as of ");
                stringBuilder.append(pushNotificationResponse.getTokenInvalidationTimestamp());

                throw new DeviceTokenInvalidException(stringBuilder.toString());
            } else {
                throw new MessageRejectedException(stringBuilder.toString());
            }
        }
    }
}
