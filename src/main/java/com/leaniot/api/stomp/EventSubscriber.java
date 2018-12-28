package com.leaniot.api.stomp;

public interface EventSubscriber {
	public void onEvent(Object event);
}
