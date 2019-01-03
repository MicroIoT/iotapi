package com.leaniot.api.device.stomp;

import java.util.List;
import java.util.Map;

import com.leaniot.api.dto.ActionRequest;
import com.leaniot.api.dto.Response;
import com.leaniot.api.stomp.OperationSubscriber;
import com.leaniot.domain.ActionType;
import com.leaniot.domain.attribute.DataValue;
import com.leaniot.domain.attribute.StructType;
import com.leaniot.exception.NotFoundException;

public abstract class ActionSubscriber extends OperationSubscriber {
	private Map<String, Class<?>> actionType;
	private List<ActionType> actionTypes;
	
	public ActionSubscriber(Map<String, Class<?>> actionType) {
		super();
		this.actionType = actionType;
	}

	@Override
	public Response getResponse() {
		this.actionTypes = this.getDevice().getDeviceType().getActionTypes();
		ActionRequest req = (ActionRequest) request;
		try {
			ActionType aType = getActionType(req.getAction());
			Object value = null;
			if(aType.getRequest() != null) {
				StructType reqType = new StructType(aType.getRequest());
				Class<?> t = actionType.get(req.getAction());
				if(t == null)
					throw new NotFoundException(req.getAction() + " converter");
				value = reqType.getValue(req.getValue(), t);
			}
			Object res = action(req.getAction(), value);
			DataValue data = null;
			if(aType.getResponse() != null) {
				StructType resType = new StructType(aType.getResponse());
				data = resType.getAttData(res);
			}
			return new Response(true, null, data);
		} catch(Throwable e) {
			return new Response(false, e.getMessage(), null);
		}
	}

	public abstract Object action(String actionName, Object request);
	
	private ActionType getActionType(String actionName) {
		for(ActionType actionType : this.actionTypes) {
			if(actionType.getName().equals(actionName))
				return actionType;
		}
		throw new NotFoundException("action : " + actionName);
	}
}
