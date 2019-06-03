package top.microiot.api.device.stomp;

import java.lang.reflect.Type;

import org.springframework.messaging.simp.stomp.StompHeaders;

import top.microiot.api.device.WebsocketDeviceSession;
import top.microiot.api.dto.Set;

/**
 * 设备端设置请求处理类，接收设置指示，返回设置响应。
 *
 * @author 曹新宇
 */
public class SetSubscribeHandler extends RequestSubscribeHandler {

	public SetSubscribeHandler(WebsocketDeviceSession wsSession, SetRequestSubscriber subscriber) {
		super(wsSession, subscriber);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getOperation() {
		return "set";
	}

	@Override
	public Type getPayloadType(StompHeaders headers) {
		return Set.class;
	}

}
