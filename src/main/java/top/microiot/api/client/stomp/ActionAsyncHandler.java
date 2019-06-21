package top.microiot.api.client.stomp;

import top.microiot.api.client.WebsocketClientSession;

public class ActionAsyncHandler extends RequestPublishAsyncHandler {

	public ActionAsyncHandler(WebsocketClientSession session, String deviceId, ActionRequestPublisher publisher, ActionResponseSubscriber subscriber) {
		super(session, deviceId, publisher, subscriber);
		// TODO Auto-generated constructor stub
	}

}
