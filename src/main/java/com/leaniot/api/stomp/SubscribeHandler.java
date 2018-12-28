package com.leaniot.api.stomp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.ConnectionLostException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import com.leaniot.api.WSSession;

public abstract class SubscribeHandler extends StompSessionHandlerAdapter {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private WSSession wsSession;
	protected String deviceId;
	private boolean connecting;
	protected EventSubscriber subscriber;
	
	public SubscribeHandler(WSSession wsSession, String deviceId, EventSubscriber subscriber) {
		super();
		this.wsSession = wsSession;
		this.deviceId = deviceId;
		this.connecting = true;
		this.subscriber = subscriber;
		wsSession.setHeartBeat(new long[] {10000, 10000});
	}

	@Override
	public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
		String topic = "/topic/" + getTopic() + "."+ deviceId;
		this.connecting = false;
		session.subscribe(topic, this);
	}
	public abstract String getTopic();
	
	@Override
	public void handleFrame(StompHeaders headers, Object payload) {
		Object event = payload;
		subscriber.onEvent(event);
	}

	@Override
	public void handleTransportError(StompSession session, Throwable exception) {
		if (exception instanceof ConnectionLostException && !connecting) {
			connecting = true;
			wsSession.connect(this);
        }
	}

	@Override
	public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload,
			Throwable exception) {
		logger.error(exception.getMessage());
	}
}
