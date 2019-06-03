package top.microiot.api.dto;

import top.microiot.domain.attribute.AttValueInfo;

/**
 * 操作请求类。
 *
 * @author 曹新宇
 */
public class Action extends Request{
	private String action;
	private AttValueInfo value;
	
	public Action() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Action(String requestId, String action, AttValueInfo value) {
		super(requestId);
		this.action = action;
		this.value = value;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public AttValueInfo getValue() {
		return value;
	}

	public void setValue(AttValueInfo value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString() + " action: " + action;
	}
}
