package com.leaniot.api.dto;

public class Request {
	private String requestId;

	public Request() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Request(String requestId) {
		super();
		this.requestId = requestId;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
}
