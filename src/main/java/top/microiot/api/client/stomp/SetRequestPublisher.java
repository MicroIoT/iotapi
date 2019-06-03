package top.microiot.api.client.stomp;

import top.microiot.api.dto.Set;
import top.microiot.domain.attribute.AttValueInfo;

/**
 * 客户端设置请求发布类。
 *
 * @author 曹新宇
 */
public class SetRequestPublisher implements RequestPublisher {
	private String attribute;
	private AttValueInfo value;
	
	public SetRequestPublisher(String attribute, AttValueInfo value) {
		this.attribute = attribute;
		this.value = value;
	}

	@Override
	public Set getRequest(String sessionId) {
		Set request = new Set(sessionId, attribute, value);
		return request;
	}

	@Override
	public String getTopic() {
		return "set";
	}

}
