package top.microiot.api.client.stomp;

import top.microiot.api.dto.Action;
import top.microiot.domain.User;
import top.microiot.domain.attribute.AttValueInfo;

/**
 * 客户端操作请求发布类。
 *
 * @author 曹新宇
 */
public class ActionRequestPublisher extends AbstractRequestPublier {
	private String action; 
	private AttValueInfo value;
	
	public ActionRequestPublisher(User requester, String action, AttValueInfo value) {
		super(requester);
		this.action = action;
		this.value = value;
	}

	@Override
	public Action getRequest(String sessionId) {
		Action request = new Action(getRequester(), sessionId, this.action, this.value);
		return request;
	}

	@Override
	public String getTopic() {
		return "action";
	}

}
