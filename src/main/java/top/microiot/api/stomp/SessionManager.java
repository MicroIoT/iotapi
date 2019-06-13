package top.microiot.api.stomp;

import org.springframework.integration.stomp.WebSocketStompSessionManager;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import top.microiot.api.HttpSession;

public class SessionManager extends WebSocketStompSessionManager {
    private HttpSession session;
    public SessionManager(HttpSession session, WebSocketStompClient webSocketStompClient, String url) {
        super(webSocketStompClient, url);
        this.session = session;
    }

    @Override
    protected ListenableFuture<StompSession> doConnect(StompSessionHandler handler) {
        restart();
        setHandshakeHeaders(new WebSocketHttpHeaders(session.getSessionHeader()));
        return super.doConnect(handler);
    }

    public void restart(){
        session.stop();
        session.start();
    }

    public void stop() {
        destroy();
        session.stop();
    }
}
