package top.microiot.api;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * websocket 配置属性类
 *
 * @author 曹新宇
 */
@ConfigurationProperties(prefix = "microiot.websocket")
public class WebsocketProperties {
    /**
     * 请求响应的超时时间，单位为秒
     */
	private long timeout = 100;
	/**
     * 进入和出去的心跳间隔时间，单位为毫秒
     */
    private long[] heartbeat = {10000, 10000};
    /**
     * 文本消息的最大缓存
     */
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
