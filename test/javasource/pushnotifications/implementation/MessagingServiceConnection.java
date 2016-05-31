package pushnotifications.implementation;

import java.util.List;

import pushnotifications.proxies.Message;
import pushnotifications.proxies.MessagingServiceSettings;

public interface MessagingServiceConnection<S extends MessagingServiceSettings, M extends Message> {

	void stop();

	void sendMessages(List<M> messages);
	
	void sendMessage(M message);

	void start (S settings);

}	