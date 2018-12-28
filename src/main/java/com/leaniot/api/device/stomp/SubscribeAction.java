package com.leaniot.api.device.stomp;

import java.lang.reflect.Type;

import org.springframework.messaging.simp.stomp.StompHeaders;

import com.leaniot.api.device.WSDeviceSession;
import com.leaniot.api.dto.ActionRequest;
import com.leaniot.api.stomp.OperationHandler;

public class SubscribeAction extends OperationHandler {

	public SubscribeAction(WSDeviceSession wsSession, ActionSubscriber subscriber) {
		super(wsSession, subscriber);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Type getPayloadType(StompHeaders headers) {
		return ActionRequest.class;
	}

	@Override
	public String getOperation() {
		return "action";
	}

}
