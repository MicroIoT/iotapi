package top.microiot.api.dto;

import top.microiot.domain.attribute.AttValueInfo;

public class SetRequest extends Request{
	private String attribute;
	private AttValueInfo value;
	
	public SetRequest() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SetRequest(String requestId, String attribute, AttValueInfo value) {
		super(requestId);
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
