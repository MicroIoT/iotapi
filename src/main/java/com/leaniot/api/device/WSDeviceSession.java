package com.leaniot.api.device;

import com.leaniot.api.WSSession;
import com.leaniot.api.device.stomp.ActionSubscriber;
import com.leaniot.api.device.stomp.GetSubscriber;
import com.leaniot.api.device.stomp.SetSubscriber;
import com.leaniot.api.device.stomp.SubscribeAction;
import com.leaniot.api.device.stomp.SubscribeGet;
import com.leaniot.api.device.stomp.SubscribeSet;
import com.leaniot.domain.Device;

public class WSDeviceSession extends WSSession {
	public WSDeviceSession(DeviceSession session, long timeout) {
		super(session, timeout);
		// TODO Auto-generated constructor stub
	}
	public WSDeviceSession(DeviceSession session) {
		super(session);
		// TODO Auto-generated constructor stub
	}
	public SubscribeGet subscribe(GetSubscriber subscriber) {
		SubscribeGet sessionHandler = new SubscribeGet(this, subscriber);
        connect(sessionHandler);
        return sessionHandler;
	}
	public SubscribeSet subscribe(SetSubscriber subscriber) {
		SubscribeSet sessionHandler = new SubscribeSet(this, subscriber);
        connect(sessionHandler);
        return sessionHandler;
	}
	public SubscribeAction subscribe(ActionSubscriber subscriber) {
		SubscribeAction sessionHandler = new SubscribeAction(this, subscriber);
        connect(sessionHandler);
        return sessionHandler;
	}
	public Device getDevice() {
		return ((DeviceSession) session).getDevice();
	}
}
