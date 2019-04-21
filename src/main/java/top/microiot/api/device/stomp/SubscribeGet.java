package top.microiot.api.device.stomp;

import java.lang.reflect.Type;

import org.springframework.messaging.simp.stomp.StompHeaders;

import top.microiot.api.device.WebsocketDeviceSession;
import top.microiot.api.dto.GetRequest;
import top.microiot.api.stomp.OperationHandler;

public class SubscribeGet extends OperationHandler {

	public SubscribeGet(WebsocketDeviceSession wsSession, GetSubscriber subscriber) {
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
