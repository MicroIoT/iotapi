package com.leaniot.api.test;

import java.util.Date;
import java.util.Map;

import com.leaniot.api.device.stomp.ActionSubscriber;
import com.leaniot.exception.ValueException;

public class MyAction extends ActionSubscriber {

	public MyAction(Map<String, Class<?>> actionType) {
		super(actionType);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object action(String actionName, Object request) {
		if(actionName.equals("update_tag")) {
			Tags tags = (Tags)request;
			System.out.println(new Date() + ": update " + tags.getScreen() + tags.getId());
			for(Tag tag : tags.getTags()) {
				System.out.println(tag.getTag() + ":" + tag.getValue());
			}
			return null;
		}else {
			throw new ValueException("unknonw action: " + actionName);
		}
	}

}
