package com.leaniot.api.client;

import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

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
	 * 获取设备信息。
	 * @param deviceId 设备标识符。
	 * @return 返回设备信息。
	 */
	public Device getDevice(String deviceId) {
		return getEntity("/device/" + deviceId, null, Device.class);
	}

	/**
	 * 获取操作类型信息。
	 * @param name 操作类型名称。
	 * @return 返回操作类型信息。
	 */
	public ActionType getActionType(String name) {
		return getEntity("/actiontype/name/" + name, null, ActionType.class);
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
