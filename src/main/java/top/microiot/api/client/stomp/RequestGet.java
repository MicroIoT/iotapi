package top.microiot.api.client.stomp;

import top.microiot.api.dto.GetRequest;
import top.microiot.api.stomp.PublishHandler;

/**
 * 客户端获取请求处理类。
 *
 * @author 曹新宇
 */
public class RequestGet extends PublishHandler {
	private String attribute;
	
	public RequestGet(String deviceId, String attribute) {
		super(deviceId);
		this.topic = "get";
		this.attribute = attribute;
	}

	@Override
	protected GetRequest getRequest(String sessionId) {
		GetRequest request = new GetRequest(sessionId, this.attribute);
		return request;
	}

}
