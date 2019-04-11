package com.leaniot.api.dto;

public class GetRequest extends Request{
	private String attribute;

	public GetRequest() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GetRequest(String requestId, String attribute) {
		super(requestId);
		this.attribute = attribute;
	}
	
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString() + " get: " + attribute;
	}
}
