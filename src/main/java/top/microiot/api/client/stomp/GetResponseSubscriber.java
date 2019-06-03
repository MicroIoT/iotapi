package top.microiot.api.client.stomp;

import org.springframework.core.ParameterizedTypeReference;

import top.microiot.domain.Device;
import top.microiot.domain.attribute.DataType;
import top.microiot.domain.attribute.DataValue;

public abstract class GetResponseSubscriber extends ResponseSubscriber {
	private String attribute;
	
	private DataType responseDataType;
	
	private Class<?> responseTypeClass = null;
	private ParameterizedTypeReference<?> responseType = null;

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public void setResponseTypeClass(Class<?> responseTypeClass) {
		this.responseTypeClass = responseTypeClass;
	}

	public void setResponseType(ParameterizedTypeReference<?> responseType) {
		this.responseType = responseType;
	}

	public void setResponseDataType(DataType responseDataType) {
		this.responseDataType = responseDataType;
	}

	@Override
	public void onSuccess(DataValue value) {
		onGetResult(device, attribute, getResponse(value));
	}

	private Object getResponse(DataValue value) {
		if(responseType == null)
			return  responseDataType.getData(value, responseTypeClass);
		else
			return  responseDataType.getData(value, responseType);
	}
	@Override
	public void onError(String error) {
		onGetError(device, attribute, error);
	}

	public abstract void onGetResult(Device device, String attribute, Object value);
	public void onGetError(Device device, String attribute, String error) {
		
	}

}
