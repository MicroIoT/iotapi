package top.microiot.api.client.stomp;

import java.lang.reflect.Type;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSession.Subscription;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import top.microiot.api.dto.Response;

public abstract class RequestPublishHandler extends StompSessionHandlerAdapter {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private String deviceId;
	private RequestPublisher publisher;

	protected Subscription subscription;
	
	public RequestPublishHandler(String deviceId, RequestPublisher publisher) {
		super();
		this.deviceId = deviceId;
		this.publisher = publisher;
	}

	@Override
	public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
		String requestId = UUID.randomUUID().toString();
		String opTopic = "/topic/operation."+ this.publisher.getTopic() + "."  + deviceId;
		String resultTopic = "/topic/result." + this.publisher.getTopic() + "." + deviceId + "." + requestId;
		RequestPublisher publish = this.publisher;
		synchronized(session) {
			subscription = session.subscribe(resultTopic, this);
			subscription.addReceiptTask(new Runnable() {
				@Override
				public void run() {
					session.send(opTopic, publish.getRequest(requestId));
				}
			});
		}
	}
	
	@Override
	public Type getPayloadType(StompHeaders headers) {
		return Response.class;
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
