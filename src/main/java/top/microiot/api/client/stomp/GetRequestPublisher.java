package top.microiot.api.client.stomp;

import top.microiot.api.dto.Get;
import top.microiot.domain.User;

/**
 * 客户端获取请求发布类。
 *
 * @author 曹新宇
 */
public class GetRequestPublisher extends AbstractRequestPublier {
	private String attribute;
	
	public GetRequestPublisher(User requester, String attribute) {
		super(requester);
		this.attribute = attribute;
	}

	@Override
	public Get getRequest(String sessionId) {
		Get request = new Get(getRequester(), sessionId, this.attribute);
		return request;
	}

	@Override
	public String getTopic() {
		return "get";
	}

}
