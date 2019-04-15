package com.leaniot.api;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "leaniot.websocket")
public class WebsocketProperties {
    private long timeout = 100;
    private long[] heartbeat = {10000, 10000};
    private int messageBufferSize = 20 * 1024 * 1024;
    
	public long getTimeout() {
		return timeout;
	}
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	public long[] getHeartbeat() {
		return heartbeat;
	}
	public void setHeartbeat(long[] heartbeat) {
		this.heartbeat = heartbeat;
	}
	public int getMessageBufferSize() {
		return messageBufferSize;
	}
	public void setMessageBufferSize(int messageBufferSize) {
		this.messageBufferSize = messageBufferSize;
	}
}
