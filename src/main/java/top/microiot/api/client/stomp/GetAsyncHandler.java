package top.microiot.api.client.stomp;

import top.microiot.api.client.WebsocketClientSession;

public class GetAsyncHandler extends RequestPublishAsyncHandler {

	public GetAsyncHandler(WebsocketClientSession session, String deviceId, GetRequestPublisher publisher, GetResponseSubscriber subscriber) {
		super(session, deviceId, publisher, subscriber);
		// TODO Auto-generated constructor stub
	}

}
