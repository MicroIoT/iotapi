package top.microiot.api.dto;

import top.microiot.domain.User;
import top.microiot.domain.attribute.AttValueInfo;

/**
 * 设置请求类。
 *
 * @author 曹新宇
 */
public class Set extends Request{
	private String attribute;
	private AttValueInfo value;
	
	public Set() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Set(User requester, String requestId, String attribute, AttValueInfo value) {
		super(requester, requestId);
		this.attribute = attribute;
		this.value = value;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
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
		return super.toString() + " set: " + attribute;
	}
}
