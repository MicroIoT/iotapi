package top.microiot.api.device;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import top.microiot.api.HttpSession;
import top.microiot.api.HttpSessionProperties;
import top.microiot.api.client.HttpClientSession;
import top.microiot.domain.Device;
import top.microiot.domain.DeviceGroup;
import top.microiot.domain.User;
import top.microiot.domain.attribute.AttValueInfo;
import top.microiot.domain.attribute.AttributeType;
import top.microiot.domain.attribute.DeviceAttributeType;
import top.microiot.dto.AlarmInfo;
import top.microiot.dto.EventInfo;
import top.microiot.dto.SubDeviceAlarmInfo;
import top.microiot.dto.SubDeviceEventInfo;
import top.microiot.exception.NotFoundException;

/**
 * 设备端与物联网平台的http会话类
 *
 * @author 曹新宇
 */
@Component
public class HttpDeviceSession extends HttpSession {
	public HttpDeviceSession(HttpSessionProperties httpSessionProperties) {
		super(httpSessionProperties);
	}

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
		if(device == null)
			return getEntity(HttpClientSession.deviceUrl + "/me", null, HttpClientSession.deviceType);
		else
			return device;
	}
	
	/**
	 * 设备端向物联网平台上报事件信息。可同时上报多个属性的值。
	 * @param events 设备的多个属性的值。
	 */
	public void reportEvents(Map<String, Object> events) {
		Map<String, AttValueInfo> values = getEventInfo(events, device);
		EventInfo info = new EventInfo();
		info.setValues(values);
		info.setReportTime(new Date());
		postEntity(HttpClientSession.eventUrl, info, null);
	}
	public void reportEvents(Map<String, Object> events, Device device) {
		Map<String, AttValueInfo> values = getEventInfo(events, device);
		SubDeviceEventInfo info = new SubDeviceEventInfo();
		info.setValues(values);
		info.setReportTime(new Date());
		info.setDeviceId(device.getId());
		postEntity(HttpClientSession.eventUrl+"/subdevice", info, null);
	}
	private Map<String, AttValueInfo> getEventInfo(Map<String, Object> events, Device mo) {
		Map<String, DeviceAttributeType> types = mo.getDeviceType().getAttDefinition();
		Map<String, AttValueInfo> values = new HashMap<String, AttValueInfo>();
		for(String attribute : events.keySet()) {
			AttributeType type = types.get(attribute);
			if(type == null)
				throw new NotFoundException("attribute [" + attribute + "]");
			Object eventValue = events.get(attribute);
			AttValueInfo value = type.getAttValue(eventValue);
			values.put(attribute, value);
		}
		
		return values;
	}
	
	/**
	 * 设备端向物联网平台上报告警信息。
	 * @param alarmType 告警类型名称。
	 * @param alarmInfo 告警详细信息。
	 */
	public void reportAlarm(String alarmType, Object alarmInfo) {
		AttValueInfo values = getAlarmInfo(alarmType, alarmInfo, device);
		AlarmInfo info = new AlarmInfo();
		info.setAlarmType(alarmType);
		info.setAlarmInfo(values);
		info.setReportTime(new Date());
		postEntity(HttpClientSession.alarmUrl, info, null);
	}
	public void reportAlarm(String alarmType, Object alarmInfo, Device device) {
		AttValueInfo values = getAlarmInfo(alarmType, alarmInfo, device);
		SubDeviceAlarmInfo info = new SubDeviceAlarmInfo();
		info.setAlarmType(alarmType);
		info.setAlarmInfo(values);
		info.setReportTime(new Date());
		info.setDeviceId(device.getId());
		postEntity(HttpClientSession.alarmUrl + "/subdevice", info, null);
	}
	private AttValueInfo getAlarmInfo(String alarmType, Object alarmInfo, Device device) {
		Map<String, AttributeType> types = device.getDeviceType().getAlarmTypes();
		if(types == null)
			throw new NotFoundException("alarm type [" + alarmType + "]");
		AttributeType type = types.get(alarmType);
		if(type == null)
			throw new NotFoundException("alarm type [" + alarmType + "]");
		
		AttValueInfo values = type.getDataType().getAttValue(alarmInfo);

		return values;
	}
	
	/**
	 * 获取设备相关的设备组信息
	 * @return 返回设备组列表
	 */
	public List<DeviceGroup> getMyDeviceGroup(){
		return getEntity(HttpClientSession.deviceGroupUrl + "/me", null, new ParameterizedTypeReference<List<DeviceGroup>>() {});
	}
	/**
	 * 获取网关设备的所有子设备信息
	 * @return 返回子设备列表
	 */
	public List<Device> getMySubDevice(){
		return getEntity(HttpClientSession.deviceUrl + "/subdevice", null, new ParameterizedTypeReference<List<Device>>() {});
	}
	@Override
	public User getCurrentUser() {
		return getDeviceInfo().getDeviceAccount();
	}
}
