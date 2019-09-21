package top.microiot.api.client.stomp;

import top.microiot.domain.Get;

/**
 * 客户端获取请求发布类。
 *
 * @author 曹新宇
 */
public class GetRequestPublisher extends AbstractRequestPublier {
	private String attribute;
	
	public GetRequestPublisher(String attribute) {
		super();
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
