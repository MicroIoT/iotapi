package top.microiot.api.client.stomp;

import top.microiot.api.client.WebsocketClientSession;
import top.microiot.domain.Response;
import top.microiot.domain.Device;
import top.microiot.domain.attribute.DataValue;

public abstract class ResponseSubscriber {
	private WebsocketClientSession websocketClientSession;
	protected Device device;
	
	public void setDevice(Device device) {
		this.device = device;
	}

	public WebsocketClientSession getWebsocketClientSession() {
		return websocketClientSession;
	}

	public void setWebsocketClientSession(WebsocketClientSession websocketClientSession) {
		this.websocketClientSession = websocketClientSession;
	}

	public void onResponse(Response response) {
		if(response.isSuccess())
			onSuccess(response.getValue());
		else
			onError(response.getError());
	}

	public abstract void onSuccess(DataValue value);
	public abstract void onError(String error);
}
