package top.microiot.api.stomp;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;

public abstract class AbstractEventSubscriber implements EventSubscriber{
	protected Map<String, Object> types = new HashMap<String, Object>();
	private boolean durable;
	
	public AbstractEventSubscriber() {
		super();
		this.durable = false;
	}

	public boolean isDurable() {
		return durable;
	}

	public void setDurable(boolean durable) {
		this.durable = durable;
	}

	public void init() {
		
	}
	
	protected  void addType(String name, Class<?> type) {
		types.put(name, type);
	}
	protected  void addType(String name, ParameterizedTypeReference<?> type) {
		types.put(name, type);
	}
}
