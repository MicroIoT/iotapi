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

@Component
public class HttpDeviceSession extends HttpSession {
	private Device device;
	
	public Device getDevice() {
		return device;
	}
	public void setDevice(Device device) {
		this.device = device;
	}
	@Override
	public void start(String username, String password, String uri) {
		super.start(username, password, uri);
		this.device = getMyInfo();
	}
	public WebsocketDeviceSession startWebsocket() {
		WebSocketStompClient client = getWebsocketClient();
		return new WebsocketDeviceSession(this, client);
	}
	private Device getMyInfo() {
		return getEntity("/device/me", Device.class);
	}
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
