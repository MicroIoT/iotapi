package top.microiot.api;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import top.microiot.api.client.HttpClientSession;
import top.microiot.api.client.WebsocketClientSession;
import top.microiot.api.device.HttpDeviceSession;
import top.microiot.api.device.WebsocketDeviceSession;

@Configuration
@EnableConfigurationProperties(WebsocketProperties.class)
public class WebsocketSessionConfig {
	@Autowired
    private WebsocketProperties p;
	@Autowired
    private HttpSessionConfig httpSessionConfig;
	
	@Bean
	@Scope("prototype")
	public WebsocketClientSession websocketClientSession() {
		HttpClientSession httpClientSession = httpSessionConfig.httpClientSession();
		WebsocketClientSession websocketClientSession = new WebsocketClientSession(httpClientSession, websocketStompClient(), p.getTimeout());
		websocketClientSession.setHandshakeHeaders(new WebSocketHttpHeaders(httpClientSession.getSessionHeader()));
		websocketClientSession.setAutoReceipt(true);
		websocketClientSession.setAutoStartup(true);
		
		return websocketClientSession;
	}
	
	@Bean
	@Scope("prototype")
	public WebsocketDeviceSession websocketDeviceSession() {
		HttpDeviceSession httpDeviceSession = httpSessionConfig.httpDeviceSession();
		WebsocketDeviceSession websocketDeviceSession = new WebsocketDeviceSession(httpDeviceSession, websocketStompClient());
		websocketDeviceSession.setHandshakeHeaders(new WebSocketHttpHeaders(httpDeviceSession.getSessionHeader()));
		websocketDeviceSession.setAutoReceipt(true);
		websocketDeviceSession.setAutoStartup(true);
		
		return websocketDeviceSession;
	}

	@Bean
	@Scope("prototype")
	public WebSocketStompClient websocketStompClient() {
		WebSocketContainer container = ContainerProvider.getWebSocketContainer();
		container.setDefaultMaxTextMessageBufferSize(p.getMessageBufferSize());
		WebSocketClient client = new StandardWebSocketClient(container);
		WebSocketStompClient stompClient = new WebSocketStompClient(client);
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());
		ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
	    taskScheduler.afterPropertiesSet();
		stompClient.setTaskScheduler(taskScheduler);
		stompClient.setReceiptTimeLimit(p.getTimeout()*1000);
		stompClient.setInboundMessageSizeLimit(p.getMessageBufferSize());
		stompClient.setDefaultHeartbeat(p.getHeartbeat());
		
		return stompClient;
	}
}
