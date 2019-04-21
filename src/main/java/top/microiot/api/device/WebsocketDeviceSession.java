package top.microiot.api.device;

import org.springframework.integration.stomp.WebSocketStompSessionManager;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import top.microiot.api.device.stomp.ActionSubscriber;
import top.microiot.api.device.stomp.GetSubscriber;
import top.microiot.api.device.stomp.SetSubscriber;
import top.microiot.api.device.stomp.SubscribeAction;
import top.microiot.api.device.stomp.SubscribeGet;
import top.microiot.api.device.stomp.SubscribeSet;
import top.microiot.domain.Device;

public class WebsocketDeviceSession extends WebSocketStompSessionManager {
	private HttpDeviceSession session;
	
	public HttpDeviceSession getSession() {
		return session;
	}

	public WebsocketDeviceSession(HttpDeviceSession session, WebSocketStompClient webSocketStompClient) {
		super(webSocketStompClient, session.getWSUri());
		this.session = session;
	}
	
	public SubscribeGet subscribe(GetSubscriber subscriber) {
		subscriber.init();
		subscriber.setWebsocketDeviceSession(this);
		SubscribeGet sessionHandler = new SubscribeGet(this, subscriber);
        connect(sessionHandler);
        return sessionHandler;
	}
	
	public SubscribeSet subscribe(SetSubscriber subscriber) {
		subscriber.init();
		subscriber.setWebsocketDeviceSession(this);
		SubscribeSet sessionHandler = new SubscribeSet(this, subscriber);
        connect(sessionHandler);
        return sessionHandler;
	}
	
	public SubscribeAction subscribe(ActionSubscriber subscriber) {
		subscriber.init();
		subscriber.setWebsocketDeviceSession(this);
		SubscribeAction sessionHandler = new SubscribeAction(this, subscriber);
        connect(sessionHandler);
        return sessionHandler;
	}
	
	public Device getDevice() {
		return session.getDevice();
	}
	
	public void stop() {
		destroy();
		session.stop();
	}

}
