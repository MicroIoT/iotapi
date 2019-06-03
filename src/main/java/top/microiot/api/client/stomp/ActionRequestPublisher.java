package top.microiot.api.client.stomp;

import top.microiot.api.dto.Action;
import top.microiot.domain.attribute.AttValueInfo;

/**
 * 客户端操作请求发布类。
 *
 * @author 曹新宇
 */
public class ActionRequestPublisher implements RequestPublisher {
	private String action; 
	private AttValueInfo value;
	
	public ActionRequestPublisher(String action, AttValueInfo value) {
		this.action = action;
		this.value = value;
	}

	@Override
	public Action getRequest(String sessionId) {
		Action request = new Action(sessionId, this.action, this.value);
		return request;
	}

	@Override
	public String getTopic() {
		return "action";
	}

}
