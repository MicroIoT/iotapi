package top.microiot.api.client.stomp;

import top.microiot.domain.User;

public abstract class AbstractRequestPublier implements RequestPublisher{
	private User requester;

	public AbstractRequestPublier(User requester) {
		super();
		this.requester = requester;
	}

	public User getRequester() {
		return requester;
	}

	public void setRequester(User requester) {
		this.requester = requester;
	}
	
}
