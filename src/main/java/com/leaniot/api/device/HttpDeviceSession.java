package com.leaniot.api.device;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import com.leaniot.api.HttpSession;
import com.leaniot.domain.Alarm;
import com.leaniot.domain.AlarmType;
import com.leaniot.domain.Device;
import com.leaniot.domain.attribute.AttValueInfo;
import com.leaniot.domain.attribute.AttributeType;
import com.leaniot.dto.AlarmInfo;
import com.leaniot.dto.EventInfo;
import com.leaniot.exception.NotFoundException;

/**
 * 设备端与物联网平台的http会话
 *
 * @author 曹新宇
 */
@Component
public class HttpDeviceSession extends HttpSession {
	private Device device;
	
	public Device getDevice() {
		return device;
	}
	public void setDevice(Device device) {
		this.device = device;
	}
	/**
	 * 建立http会话。
	 * @param username http会话用户名。
	 * @param password http会话密码。
	 * @param uri http会话uri，格式为：iotp://host:port或者iotps://host:port。
	 */
	@Override
	public void start(String username, String password, String uri) {
		super.start(username, password, uri);
		this.device = getMyInfo();
	}
	/**
	 * 建立设备端与物联网平台websocket会话。
	 * 设置心跳为10000,10000。
	 * @return 返回设备端websocket会话。
	 * @see WebsocketDeviceSession
	 */
	public WebsocketDeviceSession startWebsocket() {
		WebSocketStompClient client = getWebsocketClient(new long[] {10000, 10000});
		return new WebsocketDeviceSession(this, client);
	}
	/**
	 * 建立设备端与物联网平台websocket会话。
	 * @param heartbeat websocket心跳。
	 * @return 返回设备端websocket会话。
	 * @see WebsocketDeviceSession
	 */
	public WebsocketDeviceSession startWebsocket(long[] heartbeat) {
		WebSocketStompClient client = getWebsocketClient(heartbeat);
		return new WebsocketDeviceSession(this, client);
	}
	/**
	 * 获取设备本身信息。
	 */
	private Device getMyInfo() {
		return getEntity("/device/me", Device.class);
	}
	/**
	 * 设备端向物联网平台上报事件信息。可同时上报多个属性的值。
	 * @param events 设备的多个属性的值。
	 */
	public void reportEvents(Map<String, Object> events) {
		Map<String, AttValueInfo> values = new HashMap<String, AttValueInfo>();
		Map<String, AttributeType> types = device.getDeviceType().getAttDefinition();
		for(String attribute : events.keySet()) {
			AttributeType type = types.get(attribute);
			if(type == null)
				throw new NotFoundException("attribute [" + attribute + "]");
			Object eventValue = events.get(attribute);
			AttValueInfo value = type.getAttValue(eventValue);
			values.put(attribute, value);
		}
		EventInfo info = new EventInfo();
		info.setValues(values);
		info.setReportTime(new Date());
		postEntity("/event", info, null);
	}
	/**
	 * 设备端向物联网平台上报告警信息。
	 * @param alarmType 告警类型名称。
	 * @param alarmInfo 告警详细信息，一个key对应一个报警属性，一个value对应一个告警属性值。
	 */
	public void reportAlarm(String alarmType, Map<String, Object> alarmInfo) {
		List<AlarmType> types = device.getDeviceType().getAlarmTypes();
		AlarmType type = null;
		for(AlarmType aType : types) {
			if(aType.getName().equals(alarmType))
				type = aType;
		}
		if(type == null)
			throw new NotFoundException("alarm type [" + alarmType + "]");
		Map<String, AttributeType> alarmAttr = type.getAttDefinition();
		Map<String, AttValueInfo> values = new HashMap<String, AttValueInfo>();
		for(String attribute : alarmInfo.keySet()) {
			AttributeType attrType = alarmAttr.get(attribute);
			if(attrType == null)
				throw new NotFoundException("attribute [" + attribute + "]");
			Object alarmValue = alarmInfo.get(attribute);
			AttValueInfo value = attrType.getAttValue(alarmValue);
			values.put(attribute, value);
		}
		
		AlarmInfo info = new AlarmInfo();
		info.setAlarmType(alarmType);
		info.setAlarmInfo(values);
		info.setReportTime(new Date());
		postEntity("/alarm", info, Alarm.class);
	}
}
