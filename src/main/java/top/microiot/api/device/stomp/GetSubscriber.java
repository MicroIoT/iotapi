package top.microiot.api.device.stomp;

import java.util.Map;

import org.springframework.stereotype.Component;

import top.microiot.api.dto.GetRequest;
import top.microiot.api.dto.Response;
import top.microiot.api.stomp.OperationSubscriber;
import top.microiot.domain.attribute.AttributeType;
import top.microiot.domain.attribute.DataValue;
import top.microiot.domain.attribute.DeviceAttributeType;

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
