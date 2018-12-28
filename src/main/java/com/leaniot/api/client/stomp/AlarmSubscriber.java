package com.leaniot.api.client.stomp;

import java.util.Date;
import java.util.Map;

import com.leaniot.api.stomp.EventSubscriber;
import com.leaniot.domain.Alarm;
import com.leaniot.domain.AlarmType;
import com.leaniot.domain.attribute.StructType;
import com.leaniot.domain.attribute.StructValue;
import com.leaniot.exception.NotFoundException;

public abstract class AlarmSubscriber implements EventSubscriber{
	private Map<String, Class<?>> attrType;
	
	public AlarmSubscriber(Map<String, Class<?>> attrType) {
		super();
		this.attrType = attrType;
		this.attrType.put(AlarmType.CONNECTED, null);
		this.attrType.put(AlarmType.DISCONNECTED, null);
	}

	public abstract void onAlarm(AlarmType alarmType, Object alarmInfo, Date reportTime, Date receiveTime);

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
