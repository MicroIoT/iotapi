package top.microiot.api.client.stomp;

import org.springframework.messaging.simp.stomp.StompHeaders;

import top.microiot.api.dto.Response;

public abstract class RequestPublishAsyncHandler extends RequestPublishHandler {
	private ResponseSubscriber subscriber;
	
	public RequestPublishAsyncHandler(String deviceId, RequestPublisher publisher, ResponseSubscriber subscriber) {
		super(deviceId, publisher);
		this.subscriber = subscriber;
	}

	@Override
	public void handleFrame(StompHeaders headers, Object payload) {
		subscriber.onResponse((Response)payload);
	}
}
