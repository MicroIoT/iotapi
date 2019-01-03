package com.leaniot.api.client.stomp;

import com.leaniot.api.dto.GetRequest;
import com.leaniot.api.stomp.PublishHandler;

public class RequestGet extends PublishHandler {
	private String attribute;
	
	public RequestGet(String deviceId, String attribute) {
		super(deviceId);
		this.topic = "get";
		this.attribute = attribute;
	}

	@Override
	protected GetRequest getRequest(String sessionId) {
		GetRequest request = new GetRequest(sessionId, this.attribute);
		return request;
	}

}
