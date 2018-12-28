package com.leaniot.api.client;

import com.leaniot.api.Session;
import com.leaniot.domain.ActionType;
import com.leaniot.domain.Device;

public class ClientSession extends Session {
	public Device getDevice(String deviceId) {
		return getEntity("/device/" + deviceId, Device.class);
	}

	public ActionType getActionType(String name) {
		return getEntity("/actiontype/name/" + name, ActionType.class);
	}
}
