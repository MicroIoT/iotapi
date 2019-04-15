package com.leaniot.api.device.stomp;

import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import com.leaniot.api.dto.ActionRequest;
import com.leaniot.api.dto.Response;
import com.leaniot.api.stomp.OperationSubscriber;
import com.leaniot.domain.ActionType;
import com.leaniot.domain.attribute.DataType;
import com.leaniot.domain.attribute.DataValue;

@Component
public abstract class ActionSubscriber extends OperationSubscriber {
	private Map<String, ActionType> actionTypes;
	
	public ActionSubscriber() {
		super();
	}

	@Override
	public Response getResponse() {
		this.actionTypes = this.getDevice().getDeviceType().getActionTypes();
		ActionRequest req = (ActionRequest) request;
		try {
			ActionType actType = this.actionTypes.get(req.getAction());
			Object requestValue = null;
			if(actType.getRequest() != null) {
				DataType requestType = actType.getRequestAttributeType().getDataType();
				Object type = getType(req);
				if(type instanceof Class<?>) {
					Class<?> t = (Class<?>) type;
					requestValue = requestType.getValue(req.getValue(), t);
				}
				else if(type instanceof ParameterizedTypeReference<?>) {
					ParameterizedTypeReference<?> t = (ParameterizedTypeReference<?>) type;
					requestValue = requestType.getValue(req.getValue(), t);
				}
			}
			Object res = action(req.getAction(), requestValue);
			DataValue responseValue = null;
			if(actType.getResponse() != null) {
				DataType resType = actType.getResponseAttributeType().getDataType();
				responseValue = resType.getAttData(res);
			}
			return new Response(true, null, responseValue);
		} catch(Throwable e) {
			return new Response(false, e.getMessage(), null);
		}
	}

	private Object getType(ActionRequest req) {
		return types.get(req.getAction());
	}

	public abstract Object action(String action, Object request);
}
