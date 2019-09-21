package top.microiot.api.device;

import java.util.List;
import java.util.Map;

import top.microiot.api.client.stomp.AlarmSubscriber;
import top.microiot.api.device.stomp.ActionRequestSubscriber;
import top.microiot.api.device.stomp.GetRequestSubscriber;
import top.microiot.api.device.stomp.SetRequestSubscriber;
import top.microiot.domain.Device;
import top.microiot.domain.DeviceGroup;

/**
 * 物联网设备类
 *
 * @author 曹新宇
 */
public class IoTDevice {
	private WebsocketDeviceSession deviceSession;
	
	public IoTDevice(WebsocketDeviceSession deviceSession, GetRequestSubscriber getHandler,
			SetRequestSubscriber setHandler, ActionRequestSubscriber actionHandler) {
		super();
		this.deviceSession = deviceSession;
		if(getHandler != null)
			this.deviceSession.subscribe(getHandler);
		if(setHandler != null)
			this.deviceSession.subscribe(setHandler);
		if(actionHandler != null)
			this.deviceSession.subscribe(actionHandler);
	}
	
	public WebsocketDeviceSession getDeviceSession() {
		return deviceSession;
	}

	public void setDeviceSession(WebsocketDeviceSession deviceSession) {
		this.deviceSession = deviceSession;
	}

	public void subscribeAlarm(String deviceId, AlarmSubscriber alarmHandler) {
		this.deviceSession.subscribe(deviceId, alarmHandler);
	}
	
	public void reportAlarm(String alarmType, Object alarmInfo) {
		deviceSession.getSession().reportAlarm(alarmType, alarmInfo);
	}
	
	public void reportEvent(Map<String, Object> events) {
		deviceSession.getSession().reportEvents(events);
	}
	
	public Device getDevice() {
		return deviceSession.getSession().getDevice();
	}
	
	public List<DeviceGroup> getDeviceGroup() {
		return deviceSession.getSession().getMyDeviceGroup();
	}
	
	public void stop() {
		deviceSession.stop();
	}
}
