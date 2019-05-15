package top.microiot.api.stomp;

import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;

import top.microiot.api.device.WebsocketDeviceSession;
import top.microiot.api.dto.Response;

/**
 * 设备端请求处理抽象类。
 *
 * @author 曹新宇
 */
public abstract class OperationHandler extends SubscribeHandler {
	private StompSession session;
	
	public OperationHandler(WebsocketDeviceSession wsSession, OperationSubscriber subscriber) {
		super(wsSession.getDevice().getId(), subscriber);
		subscriber.setDevice(wsSession.getDevice());
	}

	@Override
	public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
		this.session = session;
		super.afterConnected(session, connectedHeaders);
	}

	@Override
	public void handleFrame(StompHeaders headers, Object payload) {
		super.handleFrame(headers, payload);
		Response response = ((OperationSubscriber)subscriber).getResponse();
		String topic = "/topic/result." + getOperation() + "." + deviceId + "." + ((OperationSubscriber)subscriber).request.getRequestId();
		synchronized(session) {
			session.send(topic, response);
		}
	}

	@Override
	public String getTopic() {
		return "operation." + getOperation();
	}

	public abstract String getOperation();
}
