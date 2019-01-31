package com.leaniot.api.client;

import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import com.leaniot.api.HttpSession;
import com.leaniot.domain.ActionType;
import com.leaniot.domain.Device;

/**
 * 客户端与物联网平台的http会话
 *
 * @author 曹新宇
 */
@Component
public class HttpClientSession extends HttpSession {
	/**
	 *  建立客户端与物联网平台websocket会话。
	 * 设置超时时长为10秒，心跳为10000,10000。
	 * @return 返回客户端websocket会话。
	 * @see com.leaniot.api.client.WebsocketClientSession
	 */
	public WebsocketClientSession initWebsocketSession() {
		WebSocketStompClient client = getWebsocketClient(new long[] {10000, 10000});
		return new WebsocketClientSession(this, client);
	}
	
	/**
	 * 建立客户端与物联网平台websocket会话。
	 * @param timeout 超时时长，单位为秒。
	 * @param heartbeat websocket心跳。
	 * @return 返回客户端websocket会话。
	 * @see com.leaniot.api.client.WebsocketClientSession
	 */
	public WebsocketClientSession initWebsocketSession(long timeout, long[] heartbeat) {
		WebSocketStompClient client = getWebsocketClient(heartbeat);
		return new WebsocketClientSession(this, client, timeout);
	}
	
	/**
	 * 获取设备信息。
	 * @param deviceId 设备标识符。
	 * @return 返回设备信息。
	 */
	public Device getDevice(String deviceId) {
		return getEntity("/device/" + deviceId, Device.class);
	}

	/**
	 * 获取操作类型信息。
	 * @param name 操作类型名称。
	 * @return 返回操作类型信息。
	 */
	public ActionType getActionType(String name) {
		return getEntity("/actiontype/name/" + name, ActionType.class);
	}
	
	/**
	 * 查询符合条件的设备列表。
	 * @param queryParams 查询条件。
	 * @return 设备列表。
	 */
	public List<Device> queryDeviceList(Map<String, String> queryParams){
		return getEntity("/devices/list", queryParams, new ParameterizedTypeReference<List<Device>>() {});
	}
}
