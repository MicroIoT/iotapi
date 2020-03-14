package top.microiot.api.device.stomp;

import java.lang.reflect.Type;

import org.springframework.messaging.simp.stomp.StompHeaders;

import top.microiot.domain.Get;
import top.microiot.domain.Topic;

/**
 * 设备端获取请求处理类，接收获取指示，返回获取响应。
 *
 * @author 曹新宇
 */
public class GetSubscribeHandler extends RequestSubscribeHandler {

	public GetSubscribeHandler(String deviceId, GetRequestSubscriber subscriber) {
		super(deviceId, subscriber);
	}

	@Override
	public Type getPayloadType(StompHeaders headers) {
		return Get.class;
	}

	@Override
	public String getOperation() {
		return Topic.TOPIC_GET;
	}

}
