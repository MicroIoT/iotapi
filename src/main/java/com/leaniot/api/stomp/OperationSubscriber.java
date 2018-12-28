package com.leaniot.api.stomp;

import com.leaniot.api.dto.Request;
import com.leaniot.api.dto.Response;
import com.leaniot.domain.Device;

public abstract class OperationSubscriber implements EventSubscriber {
	protected Request request;
	private Device device;
	
	public Device getDevice() {
		return device;
	}
	public void setDevice(Device device) {
		this.device = device;
	}
	public abstract Response getResponse();
	@Override
	public void onEvent(Object event) {
		request = (Request)event;
	}

}
