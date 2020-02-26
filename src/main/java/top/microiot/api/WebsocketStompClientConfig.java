package top.microiot.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;

@Configuration
@EnableConfigurationProperties(WebsocketProperties.class)
public class WebsocketStompClientConfig {
    private WebsocketProperties websocketProperties;

    @Autowired
    public void setWebsocketProperties(WebsocketProperties websocketProperties) {
		this.websocketProperties = websocketProperties;
	}

	@Bean
    @Scope("prototype")
    public WebSocketStompClient websocketStompClient() {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.setDefaultMaxTextMessageBufferSize(websocketProperties.getMessageBufferSize());
        WebSocketClient client = new StandardWebSocketClient(container);
        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.afterPropertiesSet();
        stompClient.setTaskScheduler(taskScheduler);
        stompClient.setReceiptTimeLimit(websocketProperties.getTimeout()*1000);
        stompClient.setInboundMessageSizeLimit(websocketProperties.getMessageBufferSize());
        stompClient.setDefaultHeartbeat(websocketProperties.getHeartbeat());

        return stompClient;
    }

}
