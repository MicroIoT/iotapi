package top.microiot.api.device.stomp;

import java.lang.reflect.Type;

import org.springframework.messaging.simp.stomp.StompHeaders;

import top.microiot.domain.Set;
import top.microiot.domain.Topic;

/**
 * 设备端设置请求处理类，接收设置指示，返回设置响应。
 *
 * @author 曹新宇
 */
public class SetSubscribeHandler extends RequestSubscribeHandler {

	public SetSubscribeHandler(String deviceId, SetRequestSubscriber subscriber) {
		super(deviceId, subscriber);
	}

	@Override
	public String getOperation() {
		return Topic.TOPIC_SET;
	}

	@Override
	public Type getPayloadType(StompHeaders headers) {
		return Set.class;
	}

}
