package com.leaniot.api.client;

import org.springframework.web.socket.messaging.WebSocketStompClient;

import com.leaniot.api.HttpSession;
import com.leaniot.domain.ActionType;
import com.leaniot.domain.Device;

public class HttpClientSession extends HttpSession {
	public WebsocketClientSession startWebsocket() {
		WebSocketStompClient client = getWebsocketClient();
		return new WebsocketClientSession(this, client);
	}
	public WebsocketClientSession startWebsocket(long timeout) {
		WebSocketStompClient client = getWebsocketClient();
		return new WebsocketClientSession(this, client, timeout);
	}
	public Device getDevice(String deviceId) {
		return getEntity("/device/" + deviceId, Device.class);
	}

	public ActionType getActionType(String name) {
		return getEntity("/actiontype/name/" + name, ActionType.class);
	}
}
