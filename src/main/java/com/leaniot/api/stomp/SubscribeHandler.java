package com.leaniot.api.stomp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

public abstract class SubscribeHandler extends StompSessionHandlerAdapter {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected String deviceId;
	protected EventSubscriber subscriber;
	
	public SubscribeHandler(String deviceId, EventSubscriber subscriber) {
		super();
		this.deviceId = deviceId;
		this.subscriber = subscriber;
	}

	@Override
	public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
		String topic = "/topic/" + getTopic() + "."+ deviceId;
		session.subscribe(topic, this);
	}
	public abstract String getTopic();
	
	@Override
	public void handleFrame(StompHeaders headers, Object payload) {
		synchronized(payload) {
			Object event = payload;
			subscriber.onEvent(event);
		}
	}

	@Override
	public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload,
			Throwable exception) {
		logger.error(exception.getMessage());
	}
}
