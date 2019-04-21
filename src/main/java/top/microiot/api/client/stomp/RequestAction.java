package top.microiot.api.client.stomp;

import top.microiot.api.dto.ActionRequest;
import top.microiot.api.stomp.PublishHandler;
import top.microiot.domain.attribute.AttValueInfo;

public class RequestAction extends PublishHandler {
	private String action; 
	private AttValueInfo value;
	
	public RequestAction(String deviceId, String action, AttValueInfo value) {
		super(deviceId);
		this.topic = "action";
		this.action = action;
		this.value = value;
	}

	@Override
	protected ActionRequest getRequest(String sessionId) {
		ActionRequest request = new ActionRequest(sessionId, this.action, this.value);
		return request;
	}

}
