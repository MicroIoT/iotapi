package com.leaniot.api.test;

import java.util.Date;
import java.util.Map;

import com.leaniot.api.client.stomp.AlarmSubscriber;
import com.leaniot.domain.AlarmType;

public class MyAlarm extends AlarmSubscriber {
	
	public MyAlarm(Map<String, Class<?>> attrType) {
		super(attrType);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onAlarm(AlarmType alarmType, Object alarmInfo, Date reportTime, Date receiveTime) {
		System.out.println(alarmType.getDescription());
	}
	

}
