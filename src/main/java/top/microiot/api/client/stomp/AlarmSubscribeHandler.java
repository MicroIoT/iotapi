package top.microiot.api.client.stomp;

import java.lang.reflect.Type;

import org.springframework.messaging.simp.stomp.StompHeaders;

import top.microiot.api.stomp.SubscribeHandler;
import top.microiot.domain.Alarm;

/**
 * 客户端告警接收处理类。
 *
 * @author 曹新宇
 */
public class AlarmSubscribeHandler extends SubscribeHandler {
	public AlarmSubscribeHandler(String deviceId, AlarmSubscriber subscriber) {
		super(deviceId, subscriber);
	}

	@Override
	public Type getPayloadType(StompHeaders headers) {
		return Alarm.class;
	}

	@Override
	public String getTopic() {
		// TODO Auto-generated method stub
		return "/topic/alarm";
	}
}
