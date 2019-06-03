package top.microiot.api.client.stomp;

import top.microiot.api.dto.Request;

public interface RequestPublisher {
	public String getTopic();
	public Request getRequest(String sessionId);
}
