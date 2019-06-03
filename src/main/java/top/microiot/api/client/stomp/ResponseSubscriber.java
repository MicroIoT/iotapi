package top.microiot.api.client.stomp;

import top.microiot.api.dto.Response;
import top.microiot.domain.Device;
import top.microiot.domain.attribute.DataValue;

public abstract class ResponseSubscriber {
	protected Device device;
	
	public void setDevice(Device device) {
		this.device = device;
	}

	public void onResponse(Response response) {
		if(response.isSuccess())
			onSuccess(response.getValue());
		else
			onError(response.getError());
	}

	public abstract void onSuccess(DataValue value);
	public abstract void onError(String error);
}
