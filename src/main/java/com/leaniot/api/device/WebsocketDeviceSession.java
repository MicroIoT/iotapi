package com.leaniot.api.device;

import org.springframework.integration.stomp.WebSocketStompSessionManager;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import com.leaniot.api.device.stomp.ActionSubscriber;
import com.leaniot.api.device.stomp.GetSubscriber;
import com.leaniot.api.device.stomp.SetSubscriber;
import com.leaniot.api.device.stomp.SubscribeAction;
import com.leaniot.api.device.stomp.SubscribeGet;
import com.leaniot.api.device.stomp.SubscribeSet;
import com.leaniot.domain.Device;

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
