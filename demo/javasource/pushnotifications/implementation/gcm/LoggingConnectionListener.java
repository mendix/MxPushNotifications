package pushnotifications.implementation.gcm;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

import com.mendix.core.Core;
import com.mendix.logging.ILogNode;

class LoggingConnectionListener implements ConnectionListener {
	
	private ILogNode logger = Core.getLogger(GCMConnection.class.getSimpleName());
	
	@Override
	public void authenticated(XMPPConnection xmppConnection) {
	    logger.info("Authenticated.");
	}
	
	@Override
	public void reconnectionSuccessful() {
	    logger.info("Reconnecting..");
	}
	
	@Override
	public void reconnectionFailed(Exception e) {
	    logger.error("Reconnection failed.. ", e);
	}
	
	@Override
	public void reconnectingIn(int seconds) {
	    logger.info("Reconnecting in " + seconds + " seconds.");
	}
	
	@Override
	public void connectionClosedOnError(Exception e) {
	    logger.info("Connection closed on error.");
	}
	
	@Override
	public void connectionClosed() {
	    logger.info("Connection closed.");
	}

	@Override
	public void connected(XMPPConnection arg0) {
		logger.info("Connected.");
	}

}
