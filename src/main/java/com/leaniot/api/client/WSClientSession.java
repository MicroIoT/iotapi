package com.leaniot.api.client;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.leaniot.api.WSSession;
import com.leaniot.api.client.stomp.AlarmSubscriber;
import com.leaniot.api.client.stomp.RequestAction;
import com.leaniot.api.client.stomp.RequestGet;
import com.leaniot.api.client.stomp.RequestSet;
import com.leaniot.api.client.stomp.SubscribeAlarm;
import com.leaniot.api.dto.Response;
import com.leaniot.domain.ActionType;
import com.leaniot.domain.Device;
import com.leaniot.domain.attribute.AttValueInfo;
import com.leaniot.domain.attribute.AttributeType;
import com.leaniot.domain.attribute.Location;
import com.leaniot.domain.attribute.StructType;
import com.leaniot.exception.NotFoundException;
import com.leaniot.exception.StatusException;
import com.leaniot.exception.ValueException;

public class WSClientSession extends WSSession {
	
	public WSClientSession(ClientSession session, long timeout) {
		super(session, timeout);
		// TODO Auto-generated constructor stub
	}

	public WSClientSession(ClientSession session) {
		super(session);
		// TODO Auto-generated constructor stub
	}

	public SubscribeAlarm subscribe(String deviceId, AlarmSubscriber subscriber) {
		SubscribeAlarm sessionHandler = new SubscribeAlarm(this, deviceId, subscriber);
        connect(sessionHandler);
        return sessionHandler;
	}
	
	public int getInt(String deviceId, String attribute) {
		int value = (Integer)get(deviceId, attribute, Integer.class);
		return value;
	}
	
	public double getDouble(String deviceId, String attribute) {
		double value = (Double)get(deviceId, attribute, Double.class);
		return value;
	}
	
	public String getString(String deviceId, String attribute) {
		String value = (String)get(deviceId, attribute, String.class);
		return value;
	}
	
	public Date getDate(String deviceId, String attribute) {
		Date value = (Date)get(deviceId, attribute, Date.class);
		return value;
	}
	
	public String getEnum(String deviceId, String attribute) {
		String value = (String)get(deviceId, attribute, String.class);
		return value;
	}
	
	public Location getLocation(String deviceId, String attribute) {
		Location value = (Location)get(deviceId, attribute, Location.class);
		return value;
	}
	
	public boolean getBool(ClientSession session, String deviceId, String attribute) {
		Boolean value = (Boolean)get(deviceId, attribute, Boolean.class);
		return value;
	}
	
	public Object get(String deviceId, String attribute, Class<?> type) {
		Device device = ((ClientSession) session).getDevice(deviceId);
		if(device == null)
			throw new NotFoundException("device: " + deviceId);
		AttributeType attType = device.getDeviceType().getAttDefinition().get(attribute);
		if(attType == null)
			throw new NotFoundException("attribute: " + attribute);
		try {
			Response response = get(deviceId, attribute);
			if(!response.isSuccess())
				throw new StatusException(response.getError());
			else 
				return attType.getData(response.getValue(), type);
		} catch(Throwable e) {
			throw new ValueException("get attribute [" + attribute + "] error: " + e.getMessage());
		}
	}
	
	private Response get(String deviceId, String attribute) {
		RequestGet request = new RequestGet(this, deviceId, attribute);
        
        connect(request);
		
        try {
			return request.get(timeout, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new StatusException(e.getMessage());
		}
	}

	public void set(String deviceId, String attribute, Object object) {
		Device device = ((ClientSession) session).getDevice(deviceId);
		if(device == null)
			throw new NotFoundException("device: " + deviceId);
		AttributeType attType = device.getDeviceType().getAttDefinition().get(attribute);
		if(attType == null)
			throw new NotFoundException("attribute: " + attribute);
		try{
			AttValueInfo value = attType.getAttValue(object);
			Response response = set(deviceId, attribute, value);
			if(!response.isSuccess())
				throw new StatusException(response.getError());
		} catch(Throwable e) {
			throw new ValueException("set attribute [" + attribute + "] error: " + e.getMessage());
		}
	}
	
	private Response set(String deviceId, String attribute, AttValueInfo value) {
		RequestSet request = new RequestSet(this, deviceId, attribute, value);
        
        connect(request);
		
        try {
			return request.get(timeout, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new StatusException(e.getMessage());
		}
	}
	
	public int actionInt(String deviceId, String action, Object request) {
		int value = (Integer)action(deviceId, action, request, Integer.class);
		return value;
	}
	
	public double actionDouble(String deviceId, String action, Object request) {
		double value = (Double)action(deviceId, action, request, Double.class);
		return value;
	}
	
	public String actionString(String deviceId, String action, Object request) {
		String value = (String)action(deviceId, action, request, String.class);
		return value;
	}
	
	public Date actionDate(String deviceId, String action, Object request) {
		Date value = (Date)action(deviceId, action, request, Date.class);
		return value;
	}
	
	public String actionEnum(String deviceId, String action, Object request) {
		String value = (String)action(deviceId, action, request, String.class);
		return value;
	}
	
	public Location actionLocation(String deviceId, String action, Object request) {
		Location value = (Location)action(deviceId, action, request, Location.class);
		return value;
	}
	
	public boolean actionBool(String deviceId, String action, Object request) {
		Boolean value = (Boolean)action(deviceId, action, request, Boolean.class);
		return value;
	}
	
	public Object action(String deviceId, String action, Object request, Class<?> type) {
		Device device = ((ClientSession) session).getDevice(deviceId);
		if(device == null)
			throw new NotFoundException("device: " + deviceId);
		ActionType actionType = ((ClientSession) session).getActionType(action);
		if(actionType == null)
			throw new NotFoundException("action: " + action);
		StructType requestType = new StructType(actionType.getRequest());
		AttValueInfo requestValue;
		try{
			requestValue = requestType.getAttValue(request);
		} catch(Throwable e) {
			throw new ValueException("action [" + action + "] request error: " + e.getMessage());
		}	
		
		try {
			Response response = action(deviceId, action, requestValue);
			if(!response.isSuccess())
				throw new StatusException(response.getError());
			else {
				StructType responseType = new StructType(actionType.getResponse());
				return responseType.getData(response.getValue(), type);
			}
		} catch(Throwable e) {
			throw new ValueException("action [" + action + "] response error: " + e.getMessage());
		}
	}
	
	private Response action(String deviceId, String action, AttValueInfo value) {
		RequestAction request = new RequestAction(this, deviceId, action, value);
        
        connect(request);
		
        try {
			return request.get(timeout, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new StatusException(e.getMessage());
		}
	}
}
