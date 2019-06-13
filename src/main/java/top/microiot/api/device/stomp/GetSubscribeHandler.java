package top.microiot.api.device.stomp;

import java.lang.reflect.Type;

import org.springframework.messaging.simp.stomp.StompHeaders;

import top.microiot.api.device.WebsocketDeviceSession;
import top.microiot.api.dto.Get;

/**
 * 设备端获取请求处理类，接收获取指示，返回获取响应。
 *
 * @author 曹新宇
 */
public class GetSubscribeHandler extends RequestSubscribeHandler {

	public GetSubscribeHandler(WebsocketDeviceSession wsSession, GetRequestSubscriber subscriber) {
		super(wsSession, subscriber);
	}

	@Override
	public Type getPayloadType(StompHeaders headers) {
		return Get.class;
	}

	@Override
	public String getOperation() {
		return "get";
	}

}