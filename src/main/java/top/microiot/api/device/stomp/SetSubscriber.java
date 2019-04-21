package top.microiot.api.device.stomp;

import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import top.microiot.api.dto.Response;
import top.microiot.api.dto.SetRequest;
import top.microiot.api.stomp.OperationSubscriber;
import top.microiot.domain.attribute.DataType;
import top.microiot.domain.attribute.DeviceAttributeType;
import top.microiot.exception.NotFoundException;

@Component
public abstract class SetSubscriber extends OperationSubscriber {
	private Map<String, DeviceAttributeType> attDefinition;
	
	public SetSubscriber() {
		super();
	}

	@Override
	public Response getResponse() {
		this.attDefinition = this.getDevice().getDeviceType().getAttDefinition();
		SetRequest req = (SetRequest) request;
		try {
			DataType type = this.attDefinition.get(req.getAttribute()).getDataType();
			Object attributeValue;
			Object t = getType(req);
			if(t instanceof Class<?>) {
				Class<?> tclass = (Class<?>) t;
				attributeValue = type.getValue(req.getValue(), tclass);
			}
			else if(t instanceof ParameterizedTypeReference<?>) {
				ParameterizedTypeReference<?> tclass = (ParameterizedTypeReference<?>) t;
				attributeValue = type.getValue(req.getValue(), tclass);
			}
			else
				throw new NotFoundException(req.getAttribute() + " converter");
			
			setAttribute(req.getAttribute(), attributeValue);
			return new Response(true, null, null);
		} catch(Throwable e) {
			return new Response(false, e.getMessage(), null);
		}
	}

	private Object getType(SetRequest req) {
		return types.get(req.getAttribute());
	}

	public abstract void setAttribute(String attribute, Object value);
}
