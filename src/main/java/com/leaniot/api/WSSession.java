package com.leaniot.api;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@Component
public abstract class WSSession {
	private static final int MAX_TEXT_MESSAGE_BUFFER_SIZE = 20*1024*1024;

	protected long timeout;
	
	private WebSocketClient client;
	private WebSocketStompClient stompClient;
	protected Session session;
	
	public WSSession(Session session) {
		super();
		init(session);
		this.timeout = 10;
	}

	public WSSession(Session session, long timeout) {
		this.init(session);
		this.timeout = timeout;
	}

	protected void init(Session session) {
		this.session = session;
		WebSocketContainer container = ContainerProvider.getWebSocketContainer();
		container.setDefaultMaxTextMessageBufferSize(MAX_TEXT_MESSAGE_BUFFER_SIZE);
		this.client = new StandardWebSocketClient(container);
		this.stompClient = new WebSocketStompClient(client);
		this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
	}
	
	public void connect(StompSessionHandler sessionHandler) {
		WebSocketHttpHeaders handshakeHeaders = new WebSocketHttpHeaders(session.getSessionHeader());
        stompClient.connect(session.getWSUri(), handshakeHeaders, sessionHandler);
	}
	
	public void disconnect() {
		stompClient.stop();
	}
	
	public void setHeartBeat(long[] heartbeat) {
		ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
	    taskScheduler.afterPropertiesSet();
		stompClient.setDefaultHeartbeat(heartbeat);
		stompClient.setTaskScheduler(taskScheduler);
	}
}
