package top.microiot.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import top.microiot.api.client.HttpClientSession;
import top.microiot.api.client.WebsocketClientSession;
import top.microiot.api.device.HttpDeviceSession;
import top.microiot.api.device.WebsocketDeviceSession;

/**
 * websocket会话配置类
 *
 * @author 曹新宇
 */
@Configuration
public class WebsocketSessionConfig {
	@Bean
	@Primary
	@Scope("prototype")
	public WebsocketClientSession websocketClientSession(HttpClientSession httpClientSession, WebSocketStompClient websocketStompClient) {
		return new WebsocketClientSession(httpClientSession, websocketStompClient);
	}
	
	@Bean("websocketDeviceSession")
	@Scope("prototype")
	public WebsocketDeviceSession websocketDeviceSession(HttpDeviceSession httpDeviceSession, WebSocketStompClient websocketStompClient) {
		return new WebsocketDeviceSession(httpDeviceSession, websocketStompClient);
	}
}
