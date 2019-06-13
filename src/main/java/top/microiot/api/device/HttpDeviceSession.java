package top.microiot.api.device;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import top.microiot.api.HttpSession;
import top.microiot.domain.Alarm;
import top.microiot.domain.Device;
import top.microiot.domain.attribute.AttValueInfo;
import top.microiot.domain.attribute.AttributeType;
import top.microiot.domain.attribute.DeviceAttributeType;
import top.microiot.dto.AlarmInfo;
import top.microiot.dto.EventInfo;
import top.microiot.exception.NotFoundException;

/**
 * 设备端与物联网平台的http会话类
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
	 */
	@Override
	public void start() {
		super.start();
		this.device = getDeviceInfo();
	}
	
	/**
	 * 获取设备本身信息。
	 */
	private Device getDeviceInfo() {
		return getEntity("/device/me", null, Device.class);
	}
	
	/**
	 * 设备端向物联网平台上报事件信息。可同时上报多个属性的值。
	 * @param events 设备的多个属性的值。
	 */
	public void reportEvents(Map<String, Object> events) {
		Map<String, AttValueInfo> values = new HashMap<String, AttValueInfo>();
		Map<String, DeviceAttributeType> types = device.getDeviceType().getAttDefinition();
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
	 * @param alarmInfo 告警详细信息。
	 */
	public void reportAlarm(String alarmType, Object alarmInfo) {
		Map<String, AttributeType> types = device.getDeviceType().getAlarmTypes();
		AttributeType type = types.get(alarmType);
		if(type == null)
			throw new NotFoundException("alarm type [" + alarmType + "]");
		
		AttValueInfo values = type.getDataType().getAttValue(alarmInfo);
		
		AlarmInfo info = new AlarmInfo();
		info.setAlarmType(alarmType);
		info.setAlarmInfo(values);
		info.setReportTime(new Date());
		postEntity("/alarm", info, Alarm.class);
	}
}