package top.microiot.api.device.stomp;

import java.lang.reflect.Type;

import org.springframework.messaging.simp.stomp.StompHeaders;

import top.microiot.api.device.WebsocketDeviceSession;
import top.microiot.api.dto.ActionRequest;
import top.microiot.api.stomp.OperationHandler;

public class SubscribeAction extends OperationHandler {

	public SubscribeAction(WebsocketDeviceSession wsSession, ActionSubscriber subscriber) {
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
