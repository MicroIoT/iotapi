package com.leaniot.api.client.stomp;

import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.leaniot.api.stomp.EventSubscriber;
import com.leaniot.domain.Alarm;
import com.leaniot.domain.AlarmType;
import com.leaniot.domain.Device;
import com.leaniot.domain.attribute.StructType;
import com.leaniot.domain.attribute.StructValue;
import com.leaniot.exception.NotFoundException;

/**
 * 客户端告警处理，客户端收到告警通知后，将告警通知转换为用户定义的类型，供用户处理告警。
 *
 * @author 曹新宇
 */
@Component
public abstract class AlarmSubscriber implements EventSubscriber{
	private Map<String, Class<?>> alarmInfoType;
	
	/**
	 * 客户端告警处理构造函数。
	 */
	public AlarmSubscriber() {
		super();
	}

	/**
	 * 设置告警类型的告警信息类型。
	 * @param alarmInfoType 每个key代表一个告警类型，每个value代表告警类型的类型。
	 */
	public void setAlarmInfoType(Map<String, Class<?>> alarmInfoType) {
		this.alarmInfoType = alarmInfoType;
		this.alarmInfoType.put(AlarmType.CONNECTED, null);
		this.alarmInfoType.put(AlarmType.DISCONNECTED, null);
	}

	/**
	 * 不同客户端的具体告警处理的实现。
	 * @param alarmType 告警类型名称。
	 * @param alarmInfo 告警具体信息。
	 * @param reportTime 告警上报时间。
	 * @param receiveTime 告警在平台上接收到的时间。
	 */
	public abstract void onAlarm(Device device, String alarmType, Object alarmInfo, Date reportTime, Date receiveTime);

	/**
	 * 将告警信息转变为用户的类型，调用设备的告警处理。
	 * @param event 告警通知。
	 */
	@Override
	public void onEvent(Object event) {
		Alarm alarm = (Alarm)event;
		Object info = null;
		if(alarm.getAlarmInfo() != null && !alarm.getAlarmInfo().isEmpty()) {
			StructType type = new StructType(alarm.getDevice().getDeviceType().getAlarmTypes().get(alarm.getAlarmType()).getAttDefinition());
			
			StructValue value = new StructValue();
			value.setValue(alarm.getAlarmInfo());
			Class<?> t = alarmInfoType.get(alarm.getAlarmType());
			if(t == null)
				throw new NotFoundException(alarm.getAlarmType() + " converter");
			
			info = type.getData(value, t);
		}
		
		onAlarm(alarm.getDevice(), alarm.getAlarmType(), info, alarm.getReportTime(), alarm.getReceiveTime());
	}
}
