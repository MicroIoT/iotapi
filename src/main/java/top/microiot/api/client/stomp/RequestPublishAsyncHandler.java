package top.microiot.api.client.stomp;

import org.springframework.messaging.simp.stomp.StompHeaders;

import top.microiot.api.client.WebsocketClientSession;
import top.microiot.api.dto.Response;

public abstract class RequestPublishAsyncHandler extends RequestPublishHandler {
	private ResponseSubscriber subscriber;
	private WebsocketClientSession session;
	
	public RequestPublishAsyncHandler(WebsocketClientSession session, String deviceId, RequestPublisher publisher, ResponseSubscriber subscriber) {
		super(deviceId, publisher);
		this.session = session;
		this.subscriber = subscriber;
	}

	@Override
	public void handleFrame(StompHeaders headers, Object payload) {
		session.disconnect(this);
		subscriber.onResponse((Response)payload);
	}
}
