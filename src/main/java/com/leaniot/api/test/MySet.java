package com.leaniot.api.test;

import java.util.Date;
import java.util.Map;

import com.leaniot.api.device.stomp.SetSubscriber;
import com.leaniot.exception.ValueException;

public class MySet extends SetSubscriber {

	public MySet(Map<String, Class<?>> attType) {
		super(attType);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setAttribute(String attribute, Object value) {
		System.out.println("attribute: " + attribute);
		if(attribute.equals("screen")) {
			Screen s = (Screen)value;
			if(s == null)
				System.out.println(new Date() + ": set null");
			else
				System.out.println(new Date() + ": screen layout: " + s.getFix().getLayout());
		} else if(attribute.equals("marquee")) {
			Marquee m = (Marquee)value;
			if(m == null)
				System.out.println("set marquee null");
			else
				System.out.println("marquee: " + m.getText());
		} else {
			throw new ValueException("unknonw attribute");
		}
	}

}
