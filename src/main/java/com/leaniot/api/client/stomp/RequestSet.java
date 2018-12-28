package com.leaniot.api.client.stomp;

import com.leaniot.api.client.WSClientSession;
import com.leaniot.api.dto.SetRequest;
import com.leaniot.api.stomp.PublishHandler;
import com.leaniot.domain.attribute.AttValueInfo;

public class RequestSet extends PublishHandler {
	private String attribute;
	private AttValueInfo value;
	
	public RequestSet(WSClientSession wsSession, String deviceId, String attribute, AttValueInfo value) {
		super(wsSession, deviceId);
		this.topic = "set";
		this.attribute = attribute;
		this.value = value;
	}

	@Override
	protected SetRequest getRequest(String sessionId) {
		SetRequest request = new SetRequest(sessionId, attribute, value);
		return request;
	}

}
