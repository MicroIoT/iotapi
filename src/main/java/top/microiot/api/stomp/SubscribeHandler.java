package top.microiot.api.stomp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

/**
 * websocket订阅处理抽象类，用于处理订阅信息。
 *
 * @author 曹新宇
 */
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
		String topic = getTopic() + "."+ deviceId;
		if(subscriber.isDurable()) {
			StompHeaders headers = new StompHeaders();
			headers.add(StompHeaders.DESTINATION, topic);
			headers.add("durable", "true");
			headers.add("auto-delete", "false");
			String queueId = subscriber.getSessionManager().getSession().getCurrentUser().getUsername() + topic;
			headers.add("x-queue-name", queueId);
			session.subscribe(headers, this);
		} else {
			synchronized(session) {
				session.subscribe(topic, this);
			}
		}
	}
	public abstract String getTopic();
	
	@Override
	public void handleFrame(StompHeaders headers, Object payload) {
		Object event = payload;
		subscriber.onEvent(event);
	}

	@Override
	public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload,
			Throwable exception) {
		logger.error(exception.getMessage());
	}
	
	@Override
	public void handleTransportError(StompSession session, Throwable exception) {
		logger.error("transport error: " + exception.getMessage());
	}
}
