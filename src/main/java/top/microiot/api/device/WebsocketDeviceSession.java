package top.microiot.api.device;

import org.springframework.integration.stomp.WebSocketStompSessionManager;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import top.microiot.api.device.stomp.ActionSubscriber;
import top.microiot.api.device.stomp.GetSubscriber;
import top.microiot.api.device.stomp.SetSubscriber;
import top.microiot.api.device.stomp.SubscribeAction;
import top.microiot.api.device.stomp.SubscribeGet;
import top.microiot.api.device.stomp.SubscribeSet;
import top.microiot.domain.Device;

/**
 * 设备端与物联网平台的websocket会话类
 *
 * @author 曹新宇
 */
public class WebsocketDeviceSession extends WebSocketStompSessionManager {
	private HttpDeviceSession session;
	
	public HttpDeviceSession getSession() {
		return session;
	}

	/**
	 * 设备端与物联网平台websocket会话构造函数。
	 * @param session 设备端http会话。
	 * @param webSocketStompClient 设备端与物联网平台websocket底层连接。
	 */
	public WebsocketDeviceSession(HttpDeviceSession session, WebSocketStompClient webSocketStompClient) {
		super(webSocketStompClient, session.getWSUri());
		this.session = session;
	}
	
	/**
	 * 设备端设置收到获取请求后的处理操作。
	 * @param subscriber 收到获取请求后的处理。
	 * @return 返回获取请求处理。
	 */
	public SubscribeGet subscribe(GetSubscriber subscriber) {
		subscriber.init();
		subscriber.setWebsocketDeviceSession(this);
		SubscribeGet sessionHandler = new SubscribeGet(this, subscriber);
        connect(sessionHandler);
        return sessionHandler;
	}
	
	/**
	 * 设备端设置收到设置请求后的处理操作。
	 * @param subscriber 收到设置请求后的处理。
	 * @return 返回设置请求处理。
	 */
	public SubscribeSet subscribe(SetSubscriber subscriber) {
		subscriber.init();
		subscriber.setWebsocketDeviceSession(this);
		SubscribeSet sessionHandler = new SubscribeSet(this, subscriber);
        connect(sessionHandler);
        return sessionHandler;
	}
	
	/**
	 * 设备端设置收到操作请求后的处理操作。
	 * @param subscriber 收到操作请求后的处理。
	 * @return 返回操作请求处理。
	 */
	public SubscribeAction subscribe(ActionSubscriber subscriber) {
		subscriber.init();
		subscriber.setWebsocketDeviceSession(this);
		SubscribeAction sessionHandler = new SubscribeAction(this, subscriber);
        connect(sessionHandler);
        return sessionHandler;
	}
	
	public Device getDevice() {
		return session.getDevice();
	}
	
	public void stop() {
		destroy();
		session.stop();
	}

}
