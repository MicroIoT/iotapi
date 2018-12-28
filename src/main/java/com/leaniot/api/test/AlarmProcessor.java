package com.leaniot.api.test;

import java.util.Date;
import java.util.Map;

import com.leaniot.api.client.stomp.AlarmSubscriber;
import com.leaniot.domain.AlarmType;

public class AlarmProcessor extends AlarmSubscriber {
	
	public AlarmProcessor(Map<String, Class<?>> attrType) {
		super(attrType);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onAlarm(AlarmType alarmType, Object alarmInfo, Date reportTime, Date receiveTime) {
		System.out.println(alarmType.getDescription());
	}
	

}
