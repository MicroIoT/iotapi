package top.microiot.api.client.stomp;

import top.microiot.domain.Device;
import top.microiot.domain.attribute.DataValue;

public abstract class SetResponseSubscriber extends ResponseSubscriber {
	private Device device;
	private String attribute;
	private Object value;
	
	public void setDevice(Device device) {
		this.device = device;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public void onSuccess(DataValue value) {
		onSetResult(device, attribute, this.value);
	}

	@Override
	public void onError(String error) {
		onSetError(device, attribute, value, error);
	}

	public abstract void onSetResult(Device device, String attribute, Object value);
	public void onSetError(Device device, String attribute, Object value, String error) {
		
	}
}
