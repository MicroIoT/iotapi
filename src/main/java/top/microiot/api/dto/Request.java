package top.microiot.api.dto;

import top.microiot.domain.User;

/**
 * 请求类。
 *
 * @author 曹新宇
 */

public class Request {
	private User requester;
	private String requestId;

	public Request() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Request(User requester, String requestId) {
		super();
		this.requester = requester;
		this.requestId = requestId;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public User getRequester() {
		return requester;
	}

	public void setRequester(User requester) {
		this.requester = requester;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "request: " + requestId;
	}
}
