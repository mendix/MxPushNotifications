package pushnotifications.implementation.gcm;

import org.jivesoftware.smack.packet.DefaultPacketExtension;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

public class GcmPacketExtension extends DefaultPacketExtension {
	private final String json;

    
    
    public GcmPacketExtension(String json) {
        super(GCMConnection.GCM_ELEMENT_NAME, GCMConnection.GCM_NAMESPACE);
        this.json = json;
    }

    public String getJson() {
        return json;
    }

    @Override
    public String toXML() {
        return String.format("<%s xmlns=\"%s\">%s</%s>",
        		GCMConnection.GCM_ELEMENT_NAME, GCMConnection.GCM_NAMESPACE,
                StringUtils.escapeForXML(json), GCMConnection.GCM_ELEMENT_NAME);
    }

    public Packet toPacket() {
        Message message = new Message();
        message.addExtension(this);
        return message;
    }

}
