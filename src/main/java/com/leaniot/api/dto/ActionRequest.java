package com.leaniot.api.dto;

import com.leaniot.domain.attribute.AttValueInfo;

public class ActionRequest extends Request{
	private String action;
	private AttValueInfo value;
	
	public ActionRequest() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ActionRequest(String requestId, String action, AttValueInfo value) {
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
}
