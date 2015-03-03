package pushnotifications.implementation.gcm;

import java.util.Map;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import com.mendix.core.Core;
import com.mendix.logging.ILogNode;

public class GCMPacketListener implements PacketListener {
	private ILogNode logger = Core.getLogger(GCMConnection.class
			.getSimpleName());

	private GCMConnection connection;
	
	public GCMPacketListener(GCMConnection connection) {
		this.connection = connection;
	}
	
	@Override
	public void processPacket(Packet packet) {
		logger.debug("Received: " + packet.toXML());
		Message incomingMessage = (Message) packet;
		GcmPacketExtension gcmPacket = (GcmPacketExtension) incomingMessage
				.getExtension(GCMConnection.GCM_NAMESPACE);
		String json = gcmPacket.getJson();
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> jsonObject = (Map<String, Object>) JSONValue
					.parseWithException(json);

			// present for "ack"/"nack", null otherwise
			Object messageType = jsonObject.get("message_type");

			if (messageType == null) {
				// Normal upstream data message
				connection.handleUpstreamMessage(jsonObject);

				// Send ACK to CCS
				String messageId = (String) jsonObject.get("message_id");
				String from = (String) jsonObject.get("from");
				String ack = connection.createJsonAck(from, messageId);
				connection.send(ack);
			} else if ("ack".equals(messageType.toString())) {
				// Process Ack
				connection.handleAckReceipt(jsonObject);
			} else if ("nack".equals(messageType.toString())) {
				// Process Nack
				connection.handleNackReceipt(jsonObject);
			} else if ("control".equals(messageType.toString())) {
				// Process control message
				connection.handleControlMessage(jsonObject);
			} else {
				logger.info("Unrecognized message type: " +
						messageType.toString());
			}
		} catch (ParseException e) {
			logger.error("Error parsing JSON " + json, e);
		} catch (Exception e) {
			logger.error("Failed to process packet", e);
		}
	}
}
