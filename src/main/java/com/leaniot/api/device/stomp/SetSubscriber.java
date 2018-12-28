package com.leaniot.api.device.stomp;

import java.util.Map;

import com.leaniot.api.dto.Response;
import com.leaniot.api.dto.SetRequest;
import com.leaniot.api.stomp.OperationSubscriber;
import com.leaniot.domain.attribute.AttributeType;
import com.leaniot.exception.NotFoundException;

public abstract class SetSubscriber extends OperationSubscriber {
	private Map<String, Class<?>> attType;
	private Map<String, AttributeType> attDefinition;
	public SetSubscriber(Map<String, Class<?>> attType) {
		super();
		this.attType = attType;
	}

	@Override
	public Response getResponse() {
		this.attDefinition = this.getDevice().getDeviceType().getAttDefinition();
		SetRequest req = (SetRequest) request;
		try {
			AttributeType type = this.attDefinition.get(req.getAttribute());
			Class<?> t = attType.get(req.getAttribute());
			if(t == null)
				throw new NotFoundException(req.getAttribute() + " converter");
			Object value = type.getValue(req.getValue(), t);
			setAttribute(req.getAttribute(), value);
			return new Response(true, null, null);
		} catch(Throwable e) {
			return new Response(false, e.getMessage(), null);
		}
	}

	public abstract void setAttribute(String attribute, Object value);
}
