package top.microiot.api.device.stomp;

import java.lang.reflect.Type;

import org.springframework.messaging.simp.stomp.StompHeaders;

import top.microiot.domain.Action;
import top.microiot.domain.Topic;

/**
 * 设备端操作请求处理类，接收操作指示，返回操作响应。
 *
 * @author 曹新宇
 */
public class ActionSubscribeHandler extends RequestSubscribeHandler {

	public ActionSubscribeHandler(String deviceId, ActionRequestSubscriber subscriber) {
		super(deviceId, subscriber);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Type getPayloadType(StompHeaders headers) {
		return Action.class;
	}

	@Override
	public String getOperation() {
		return Topic.TOPIC_ACTION;
	}

}
