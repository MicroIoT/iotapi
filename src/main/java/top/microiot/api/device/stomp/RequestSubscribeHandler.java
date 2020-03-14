package top.microiot.api.device.stomp;

import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;

import top.microiot.api.stomp.SubscribeHandler;
import top.microiot.domain.Response;
import top.microiot.domain.Topic;

/**
 * 设备端请求处理抽象类。
 *
 * @author 曹新宇
 */
public abstract class RequestSubscribeHandler extends SubscribeHandler {
	private StompSession session;
	
	public RequestSubscribeHandler(String deviceId, RequestSubscriber subscriber) {
		super(deviceId, subscriber);
	}

	@Override
	public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
		this.session = session;
		super.afterConnected(session, connectedHeaders);
	}

	@Override
	public void handleFrame(StompHeaders headers, Object payload) {
		super.handleFrame(headers, payload);
		Response response = ((RequestSubscriber)subscriber).getResponse();
		String topic = Topic.TOPIC_RESULT + getOperation() + "." + deviceId + "." + ((RequestSubscriber)subscriber).request.getRequestId();
		synchronized(session) {
			session.send(topic, response);
		}
	}

	@Override
	public String getTopic() {
		return Topic.TOPIC_OPERATION + getOperation();
	}

	public abstract String getOperation();
}
