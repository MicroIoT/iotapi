package top.microiot.api.stomp;

public interface EventSubscriber {
	public void onEvent(Object event);
	public void init();
	public SessionManager getSessionManager();
	public boolean isDurable();
}
