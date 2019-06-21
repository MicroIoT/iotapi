package top.microiot.api.client.stomp;

import top.microiot.api.client.WebsocketClientSession;

public class SetAsyncHandler extends RequestPublishAsyncHandler {

	public SetAsyncHandler(WebsocketClientSession session, String deviceId, SetRequestPublisher publisher, SetResponseSubscriber subscriber) {
		super(session, deviceId, publisher, subscriber);
		// TODO Auto-generated constructor stub
	}

}
