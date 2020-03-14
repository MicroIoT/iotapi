package top.microiot.api.device;

import java.util.Map;

import top.microiot.api.client.stomp.AlarmSubscriber;
import top.microiot.api.device.stomp.ActionRequestSubscriber;
import top.microiot.api.device.stomp.GetRequestSubscriber;
import top.microiot.api.device.stomp.SetRequestSubscriber;
import top.microiot.domain.Device;

public class IoTSubDevice {
	private WebsocketDeviceSession deviceSession;
	private Device device;
	
	public IoTSubDevice(WebsocketDeviceSession deviceSession, Device device, GetRequestSubscriber getHandler,
			SetRequestSubscriber setHandler, ActionRequestSubscriber actionHandler) {
		super();
		this.deviceSession = deviceSession;
		this.device = device;
		initDevice(getHandler, setHandler, actionHandler);
	}

	public Device getDevice() {
		return device;
	}

	public WebsocketDeviceSession getDeviceSession() {
		return deviceSession;
	}

	public void setDeviceSession(WebsocketDeviceSession deviceSession) {
		this.deviceSession = deviceSession;
	}

	protected void initDevice(GetRequestSubscriber getHandler, SetRequestSubscriber setHandler,
			ActionRequestSubscriber actionHandler) {
		if(getHandler != null)
			this.getDeviceSession().subscribe(getHandler, getDevice());
		if(setHandler != null)
			this.getDeviceSession().subscribe(setHandler, getDevice());
		if(actionHandler != null)
			this.getDeviceSession().subscribe(actionHandler, getDevice());
	}

	public void subscribeAlarm(String deviceId, AlarmSubscriber alarmHandler) {
		this.deviceSession.subscribe(deviceId, alarmHandler);
	}
	
	public void reportAlarm(String alarmType, Object alarmInfo) {
		deviceSession.getSession().reportAlarm(alarmType, alarmInfo, device);
	}
	
	public void reportEvent(Map<String, Object> events) {
		deviceSession.getSession().reportEvents(events, device);
	}
}
