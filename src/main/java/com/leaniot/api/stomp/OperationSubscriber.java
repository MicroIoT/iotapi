package com.leaniot.api.stomp;

import com.leaniot.api.device.WebsocketDeviceSession;
import com.leaniot.api.dto.Request;
import com.leaniot.api.dto.Response;
import com.leaniot.domain.Device;

public abstract class OperationSubscriber implements EventSubscriber {
	protected Request request;
	private Device device;
	private WebsocketDeviceSession websocketDeviceSession;
	
	public Device getDevice() {
		return device;
	}
	public void setDevice(Device device) {
		this.device = device;
	}
	public WebsocketDeviceSession getWebsocketDeviceSession() {
		return websocketDeviceSession;
	}
	public void setWebsocketDeviceSession(WebsocketDeviceSession websocketDeviceSession) {
		this.websocketDeviceSession = websocketDeviceSession;
	}
	public abstract Response getResponse();
	@Override
	public void onEvent(Object event) {
		request = (Request)event;
	}

}
