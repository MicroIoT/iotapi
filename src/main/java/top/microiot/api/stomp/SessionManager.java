package top.microiot.api.stomp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.integration.stomp.WebSocketStompSessionManager;
import org.springframework.web.socket.messaging.WebSocketStompClient;

public class SessionManager extends WebSocketStompSessionManager {
	protected List<SubscribeHandler> handlers = new ArrayList<SubscribeHandler>();
	private WebSocketStompClient webSocketStompClient;
	
	public SessionManager(WebSocketStompClient webSocketStompClient, String url) {
		super(webSocketStompClient, url);
		this.webSocketStompClient = webSocketStompClient;
	}

	public void stop() {
		for(SubscribeHandler handler : handlers) {
			disconnect(handler);
		}
		webSocketStompClient.stop();
		super.stop();
	}
}
