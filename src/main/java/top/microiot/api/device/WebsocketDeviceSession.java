package top.microiot.api.device;

import org.springframework.web.socket.messaging.WebSocketStompClient;

import top.microiot.api.client.WebsocketClientSession;
import top.microiot.api.device.stomp.ActionRequestSubscriber;
import top.microiot.api.device.stomp.ActionSubscribeHandler;
import top.microiot.api.device.stomp.GetRequestSubscriber;
import top.microiot.api.device.stomp.GetSubscribeHandler;
import top.microiot.api.device.stomp.SetRequestSubscriber;
import top.microiot.api.device.stomp.SetSubscribeHandler;
import top.microiot.domain.Device;

/**
 * 设备端与物联网平台的websocket会话类
 *
 * @author 曹新宇
 */
public class WebsocketDeviceSession extends WebsocketClientSession {
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
		super(session, webSocketStompClient);
		this.session = session;
	}
	
	/**
	 * 设备端设置收到获取请求后的处理操作。
	 * @param subscriber 收到获取请求后的处理。
	 * @return 返回获取请求处理。
	 */
	public GetSubscribeHandler subscribe(GetRequestSubscriber subscriber) {
		Device device = getDevice();
		return subscribeGet(subscriber, device);
	}
	
	public GetSubscribeHandler subscribe(GetRequestSubscriber subscriber, Device device) {
		return subscribeGet(subscriber, device);
	}
	
	private GetSubscribeHandler subscribeGet(GetRequestSubscriber subscriber, Device device) {
		subscriber.init();
		subscriber.setWebsocketDeviceSession(this);
		subscriber.setDevice(device);
		GetSubscribeHandler sessionHandler = new GetSubscribeHandler(device.getId(), subscriber);
        connect(sessionHandler);
        handlers.add(sessionHandler);
        return sessionHandler;
	}
	
	/**
	 * 设备端设置收到设置请求后的处理操作。
	 * @param subscriber 收到设置请求后的处理。
	 * @return 返回设置请求处理。
	 */
	public SetSubscribeHandler subscribe(SetRequestSubscriber subscriber) {
		Device device = getDevice();
		return subscribeSet(subscriber, device);
	}
	
	public SetSubscribeHandler subscribe(SetRequestSubscriber subscriber, Device device) {
		return subscribeSet(subscriber, device);
	}
	
	private SetSubscribeHandler subscribeSet(SetRequestSubscriber subscriber, Device device) {
		subscriber.init();
		subscriber.setWebsocketDeviceSession(this);
		subscriber.setDevice(device);
		SetSubscribeHandler sessionHandler = new SetSubscribeHandler(device.getId(), subscriber);
        connect(sessionHandler);
        handlers.add(sessionHandler);
        return sessionHandler;
	}
	
	/**
	 * 设备端设置收到操作请求后的处理操作。
	 * @param subscriber 收到操作请求后的处理。
	 * @return 返回操作请求处理。
	 */
	public ActionSubscribeHandler subscribe(ActionRequestSubscriber subscriber) {
		Device device = getDevice();
		return subscribeAction(subscriber, device);
	}
	
	public ActionSubscribeHandler subscribe(ActionRequestSubscriber subscriber, Device device) {
		return subscribeAction(subscriber, device);
	}

	private ActionSubscribeHandler subscribeAction(ActionRequestSubscriber subscriber, Device device) {
		subscriber.init();
		subscriber.setWebsocketDeviceSession(this);
		subscriber.setDevice(device);
		ActionSubscribeHandler sessionHandler = new ActionSubscribeHandler(device.getId(), subscriber);
        connect(sessionHandler);
        handlers.add(sessionHandler);
        return sessionHandler;
	}
	
	public Device getDevice() {
		return session.getDevice();
	}
}
