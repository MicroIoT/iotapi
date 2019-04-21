package top.microiot.api.device.stomp;

import java.lang.reflect.Type;

import org.springframework.messaging.simp.stomp.StompHeaders;

import top.microiot.api.device.WebsocketDeviceSession;
import top.microiot.api.dto.SetRequest;
import top.microiot.api.stomp.OperationHandler;

public class SubscribeSet extends OperationHandler {

	public SubscribeSet(WebsocketDeviceSession wsSession, SetSubscriber subscriber) {
		super(wsSession, subscriber);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getOperation() {
		return "set";
	}

	@Override
	public Type getPayloadType(StompHeaders headers) {
		return SetRequest.class;
	}

}
