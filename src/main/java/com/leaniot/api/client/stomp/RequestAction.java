package com.leaniot.api.client.stomp;

import com.leaniot.api.client.WSClientSession;
import com.leaniot.api.dto.ActionRequest;
import com.leaniot.api.stomp.PublishHandler;
import com.leaniot.domain.attribute.AttValueInfo;

public class RequestAction extends PublishHandler {
	private String action; 
	private AttValueInfo value;
	
	public RequestAction(WSClientSession wsSession, String deviceId, String action, AttValueInfo value) {
		super(wsSession, deviceId);
		this.topic = "action";
		this.action = action;
		this.value = value;
	}

	@Override
	protected ActionRequest getRequest(String sessionId) {
		ActionRequest request = new ActionRequest(sessionId, this.action, this.value);
		return request;
	}

}
