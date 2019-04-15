package com.leaniot.api.device.stomp;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.leaniot.api.dto.GetRequest;
import com.leaniot.api.dto.Response;
import com.leaniot.api.stomp.OperationSubscriber;
import com.leaniot.domain.attribute.AttributeType;
import com.leaniot.domain.attribute.DataValue;
import com.leaniot.domain.attribute.DeviceAttributeType;

@Component
public abstract class GetSubscriber extends OperationSubscriber {
	private Map<String, DeviceAttributeType> attDefinition;
	
	public GetSubscriber() {
		super();
	}

	@Override
	public Response getResponse() {
		this.attDefinition = this.getDevice().getDeviceType().getAttDefinition();
		GetRequest req = (GetRequest) request;
		try {
			Object res = getAttributeValue(req.getAttribute());
			AttributeType type = this.attDefinition.get(req.getAttribute());
			DataValue responseValue = type.getAttData(res);
			return new Response(true, null, responseValue);
		} catch(Throwable e) {
			return new Response(false, e.getMessage(), null);
		}
		
	}

	public abstract Object getAttributeValue(String attribute);
}
