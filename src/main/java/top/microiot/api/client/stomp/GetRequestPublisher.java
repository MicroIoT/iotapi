package top.microiot.api.client.stomp;

import top.microiot.api.dto.Get;

/**
 * 客户端获取请求发布类。
 *
 * @author 曹新宇
 */
public class GetRequestPublisher implements RequestPublisher {
	private String attribute;
	
	public GetRequestPublisher(String attribute) {
		this.attribute = attribute;
	}

	@Override
	public Get getRequest(String sessionId) {
		Get request = new Get(sessionId, this.attribute);
		return request;
	}

	@Override
	public String getTopic() {
		return "get";
	}

}
