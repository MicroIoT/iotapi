package com.leaniot.api.device.stomp;

import java.lang.reflect.Type;

import org.springframework.messaging.simp.stomp.StompHeaders;

import com.leaniot.api.device.WSDeviceSession;
import com.leaniot.api.dto.GetRequest;
import com.leaniot.api.stomp.OperationHandler;

public class SubscribeGet extends OperationHandler {

	public SubscribeGet(WSDeviceSession wsSession, GetSubscriber subscriber) {
		super(wsSession, subscriber);
	}

	@Override
	public Type getPayloadType(StompHeaders headers) {
		return GetRequest.class;
	}

	@Override
	public String getOperation() {
		return "get";
	}

}
