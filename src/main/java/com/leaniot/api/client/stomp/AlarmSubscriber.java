package com.leaniot.api.client.stomp;

import java.util.Date;
import java.util.Map;

import com.leaniot.api.stomp.EventSubscriber;
import com.leaniot.domain.Alarm;
import com.leaniot.domain.AlarmType;
import com.leaniot.domain.attribute.StructType;
import com.leaniot.domain.attribute.StructValue;
import com.leaniot.exception.NotFoundException;

/**
 * 客户端告警处理，客户端收到告警通知后，将告警通知转换为用户定义的类型，供用户处理告警。
 *
 * @author 曹新宇
 */
public abstract class AlarmSubscriber implements EventSubscriber{
	private Map<String, Class<?>> attrType;
	
	/**
	 * 设备端告警处理操作构造函数。
	 * @param attrType 每个key代表一个告警类型，每个value代表告警类型的类型。
	 */
	public AlarmSubscriber(Map<String, Class<?>> attrType) {
		super();
		this.attrType = attrType;
		this.attrType.put(AlarmType.CONNECTED, null);
		this.attrType.put(AlarmType.DISCONNECTED, null);
	}

	/**
	 * 不同客户端的具体告警处理的实现。
	 * @param alarmType 告警类型名称。
	 * @param alarmInfo 告警具体信息。
	 * @param reportTime 告警上报时间。
	 * @param receiveTime 告警在平台上接收到的时间。
	 */
	public abstract void onAlarm(AlarmType alarmType, Object alarmInfo, Date reportTime, Date receiveTime);

	/**
	 * 将告警信息转变为用户的类型，调用设备的告警处理。
	 * @param event 告警通知。
	 */
	@Override
	public void onEvent(Object event) {
		Alarm alarm = (Alarm)event;
		Object info = null;
		if(alarm.getAlarmInfo() != null && !alarm.getAlarmInfo().isEmpty()) {
			StructType type = new StructType(alarm.getAlarmType().getAttDefinition());
			
			StructValue value = new StructValue();
			value.setValue(alarm.getAlarmInfo());
			Class<?> t = attrType.get(alarm.getAlarmType().getName());
			if(t == null)
				throw new NotFoundException(alarm.getAlarmType().getName() + " converter");
			
			info = type.getData(value, t);
		}
		
		onAlarm(alarm.getAlarmType(), info, alarm.getReportTime(), alarm.getReceiveTime());
	}
}
