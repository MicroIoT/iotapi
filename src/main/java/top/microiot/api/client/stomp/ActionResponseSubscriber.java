package top.microiot.api.client.stomp;

import org.springframework.core.ParameterizedTypeReference;

import top.microiot.domain.Device;
import top.microiot.domain.attribute.DataType;
import top.microiot.domain.attribute.DataValue;

public abstract class ActionResponseSubscriber extends ResponseSubscriber {
	private String action;
	private Object request;
	
	private DataType responseDataType;
	
	private Class<?> responseTypeClass = null;
	private ParameterizedTypeReference<?> responseType = null;
	
	public void setAction(String action) {
		this.action = action;
	}

	public void setRequest(Object request) {
		this.request = request;
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
		onActionResult(device, action, request, getResponse(value));
	}

	private Object getResponse(DataValue value) {
		if(responseType == null && responseTypeClass == null)
			return null;
		else if(responseType == null)
			return  responseDataType.getData(value, responseTypeClass);
		else
			return  responseDataType.getData(value, responseType);
	}
	
	@Override
	public void onError(String error) {
		onActionError(device, action, request, error);
	}

	public abstract void onActionResult(Device device, String action, Object request, Object response) ;
	public void onActionError(Device device, String action, Object request, String error) {
		
	}
}
