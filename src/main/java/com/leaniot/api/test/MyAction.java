package com.leaniot.api.test;

import java.util.Map;

import com.leaniot.api.device.stomp.ActionSubscriber;

public class MyAction extends ActionSubscriber {

	public MyAction(Map<String, Class<?>> actionType) {
		super(actionType);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object action(String actionName, Object request) {
		// TODO Auto-generated method stub
		return null;
	}

}
