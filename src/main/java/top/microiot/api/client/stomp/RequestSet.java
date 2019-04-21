package top.microiot.api.client.stomp;

import top.microiot.api.dto.SetRequest;
import top.microiot.api.stomp.PublishHandler;
import top.microiot.domain.attribute.AttValueInfo;

public class RequestSet extends PublishHandler {
	private String attribute;
	private AttValueInfo value;
	
	public RequestSet(String deviceId, String attribute, AttValueInfo value) {
		super(deviceId);
		this.topic = "set";
		this.attribute = attribute;
		this.value = value;
	}

	@Override
	protected SetRequest getRequest(String sessionId) {
		SetRequest request = new SetRequest(sessionId, attribute, value);
		return request;
	}

}
