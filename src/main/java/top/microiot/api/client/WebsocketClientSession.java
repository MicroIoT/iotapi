package top.microiot.api.client;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.integration.stomp.WebSocketStompSessionManager;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import top.microiot.api.client.stomp.ActionAsyncHandler;
import top.microiot.api.client.stomp.ActionRequestPublisher;
import top.microiot.api.client.stomp.ActionResponseSubscriber;
import top.microiot.api.client.stomp.AlarmSubscribeHandler;
import top.microiot.api.client.stomp.AlarmSubscriber;
import top.microiot.api.client.stomp.GetAsyncHandler;
import top.microiot.api.client.stomp.GetRequestPublisher;
import top.microiot.api.client.stomp.GetResponseSubscriber;
import top.microiot.api.client.stomp.RequestPublishSyncHandler;
import top.microiot.api.client.stomp.SetAsyncHandler;
import top.microiot.api.client.stomp.SetRequestPublisher;
import top.microiot.api.client.stomp.SetResponseSubscriber;
import top.microiot.api.dto.Response;
import top.microiot.domain.ActionType;
import top.microiot.domain.Device;
import top.microiot.domain.attribute.AttValueInfo;
import top.microiot.domain.attribute.AttributeType;
import top.microiot.domain.attribute.DataType;
import top.microiot.exception.NotFoundException;
import top.microiot.exception.StatusException;
import top.microiot.exception.ValueException;

/**
 * 客户端与物联网平台的websocket会话类
 *
 * @author 曹新宇
 */
public class WebsocketClientSession  extends WebSocketStompSessionManager {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private HttpClientSession session;
	private long timeout;
	
	public HttpClientSession getSession() {
		return session;
	}

	/**
	 * 客户端与物联网平台websocket会话构造函数。
	 * @param session 客户端http会话。
	 * @param webSocketStompClient 客户端与物联网平台websocket底层连接。
	 * @param timeout 客户端与物联网平台websocket响应超时时长，单位为秒。
	 */
	public WebsocketClientSession(HttpClientSession session, WebSocketStompClient webSocketStompClient, long timeout) {
		super(webSocketStompClient, session.getWSUri());
		this.session = session;
		this.timeout = timeout;
	}

	/**
	 * 客户端设置收到告警后的告警处理。
	 * @param deviceId 被监控告警的设备。
	 * @param subscriber 收到告警后的处理。
	 * @return 返回告警处理。
	 */
	public AlarmSubscribeHandler subscribe(String deviceId, AlarmSubscriber subscriber) {
		Device device = session.getDevice(deviceId);
		if(device == null)
			throw new NotFoundException("device: " + deviceId);
		
		subscriber.init();
		subscriber.setWebsocketClientSession(this);
		AlarmSubscribeHandler sessionHandler = new AlarmSubscribeHandler(deviceId, subscriber);
        connect(sessionHandler);
        return sessionHandler;
	}
	
	/**
	 * 客户端同步获取设备属性值。
	 * @param deviceId 获取属性值的设备。
	 * @param attribute 属性名称。
	 * @param <T> 返回属性值类。
	 * @param responseType 返回属性值的类型。
	 * @return 返回属性值。
	 */
	public <T> T get(String deviceId, String attribute, Class<T> responseType) {
		GetHandler<T> handler = new GetHandler<T>(session, deviceId, attribute, responseType);
		return handler.get();
	}

	/**
	 * 客户端异步获取设备属性值。
	 * @param deviceId 获取属性值的设备。
	 * @param attribute 属性名称。
	 * @param <T> 返回属性值类。
	 * @param responseType 返回属性值的类型。
	 * @param subscriber 获取设备属性值处理方法。
	 */
	public <T> void getAsync(String deviceId, String attribute, Class<T> responseType, GetResponseSubscriber subscriber) {
		GetHandler<T> handler = new GetHandler<T>(session, deviceId, attribute, responseType, subscriber);
		handler.getAsync();
	}
	/**
	 * 客户端同步获取设备属性值。
	 * @param deviceId 获取属性值的设备。
	 * @param attribute 属性名称。
	 * @param <T> 返回属性值参数化类。
	 * @param responseType 返回属性值的参数化类型。
	 * @return 返回属性值。
	 */
	public <T> T get(String deviceId, String attribute, ParameterizedTypeReference<T> responseType) {
		GetHandler<T> handler = new GetHandler<T>(session, deviceId, attribute, responseType);
		return handler.get();
	}
	/**
	 * 客户端异步获取设备属性值。
	 * @param deviceId 获取属性值的设备。
	 * @param attribute 属性名称。
	 * @param <T> 返回属性值类。
	 * @param responseType 返回属性值的参数化类型。
	 * @param subscriber 获取设备属性值处理方法。
	 */
	public <T> void getAsync(String deviceId, String attribute, ParameterizedTypeReference<T> responseType, GetResponseSubscriber subscriber) {
		GetHandler<T> handler = new GetHandler<T>(session, deviceId, attribute, responseType, subscriber);
		handler.getAsync();
	}
	
	/**
	 * 客户端设置设备属性值。
	 * @param deviceId 设置属性值的设备。
	 * @param attribute 属性名称。
	 * @param value 属性值。
	 */
	public void set(String deviceId, String attribute, Object value) {
		SetHandler handler = new SetHandler(session, deviceId, attribute, value);
		handler.set();
	}
	
	/**
	 * 客户端异步设置设备属性值。
	 * @param deviceId 设置属性值的设备。
	 * @param attribute 属性名称。
	 * @param value 属性值。
	 * @param subscriber 设置设备属性值处理方法。
	 */
	public void setAsync(String deviceId, String attribute, Object value, SetResponseSubscriber subscriber) {
		SetHandler handler = new SetHandler(session, deviceId, attribute, value, subscriber);
		handler.setAsync();
	}
	
	/**
	 * 客户端调用设备操作。
	 * @param deviceId 被调用的设备。
	 * @param action 操作名称。
	 * @param request 操作请求值。
	 * @param <T> 返回响应值类。
	 * @param responseType 返回响应值的类型。
	 * @return 返回操作响应。
	 */
	public <T> T action(String deviceId, String action, Object request, Class<T> responseType) {
		ActionHandler<T> a = new ActionHandler<T>(session, deviceId, action, request, responseType);
		return a.action();
	}
	
	/**
	 * 客户端异步调用设备操作。
	 * @param deviceId 被调用的设备。
	 * @param action 操作名称。
	 * @param request 操作请求值。
	 * @param <T> 返回响应值类。
	 * @param responseType 返回响应值的类型。
	 * @param subscriber 操作设备处理方法。
	 */
	public <T> void actionAsync(String deviceId, String action, Object request, Class<T> responseType, ActionResponseSubscriber subscriber) {
		ActionHandler<T> handler = new ActionHandler<T>(session, deviceId, action, request, responseType, subscriber);
		handler.actionAsync();
	}
	
	/**
	 * 客户端调用设备action操作。
	 * @param deviceId 被调用的设备。
	 * @param action 操作名称。
	 * @param request 操作请求值。
	 * @param <T> 返回响应值参数化类。
	 * @param responseType 返回响应值的参数化类型。
	 * @return 返回操作响应。
	 */
	public <T> T action(String deviceId, String action, Object request, ParameterizedTypeReference<T> responseType) {
		ActionHandler<T> a = new ActionHandler<T>(session, deviceId, action, request, responseType);
		return a.action();
	}
	
	/**
	 * 客户端异步调用设备action操作。
	 * @param deviceId 被调用的设备。
	 * @param action 操作名称。
	 * @param request 操作请求值。
	 * @param <T> 返回响应值参数化类。
	 * @param responseType 返回响应值的参数化类型。
	 * @param subscriber 操作设备处理方法。
	 */
	public <T> void actionAsync(String deviceId, String action, Object request, ParameterizedTypeReference<T> responseType, ActionResponseSubscriber subscriber) {
		ActionHandler<T> handler = new ActionHandler<T>(session, deviceId, action, request, responseType, subscriber);
		handler.actionAsync();
	}
	
	/**
	 * 客户端调用设备action操作。
	 * @param deviceId 被调用的设备。
	 * @param action 操作名称。
	 * @param request 操作请求值。
	 */
	@SuppressWarnings("rawtypes")
	public void action(String deviceId, String action, Object request) {
		ActionHandler a = new ActionHandler(session, deviceId, action, request);
		a.action();
	}
	
	/**
	 * 客户端异步调用设备action操作。
	 * @param deviceId 被调用的设备。
	 * @param action 操作名称。
	 * @param request 操作请求值。
	 * @param subscriber 操作设备处理方法。
	 */
	@SuppressWarnings("rawtypes")
	public void actionAsync(String deviceId, String action, Object request, ActionResponseSubscriber subscriber) {
		ActionHandler handler = new ActionHandler(session, deviceId, action, request, subscriber);
		handler.actionAsync();
	}
	public void stop() {
		destroy();
		session.stop();
	}

	private class GetHandler<T> {
		private HttpClientSession session;
		private String deviceId;
		private Device device;
		private String attribute;
		private Response response;
		private DataType responseDataType;
		
		private Class<T> responseTypeClass = null;
		private ParameterizedTypeReference<T> responseType = null;
		
		private GetResponseSubscriber subscriber;
		
		public GetHandler(HttpClientSession session, String deviceId, String attribute, Class<T> responseTypeClass) {
			super();
			this.session = session;
			this.deviceId = deviceId;
			this.attribute = attribute;
			this.responseTypeClass = responseTypeClass;
			this.subscriber.setResponseTypeClass(responseTypeClass);
		}
		
		public GetHandler(HttpClientSession session, String deviceId, String attribute, ParameterizedTypeReference<T> responseType) {
			super();
			this.session = session;
			this.deviceId = deviceId;
			this.attribute = attribute;
			this.responseType = responseType;
			this.subscriber.setResponseType(responseType);
		}
		
		public GetHandler(HttpClientSession session, String deviceId, String attribute, Class<T> responseTypeClass, GetResponseSubscriber subscriber) {
			super();
			this.session = session;
			this.deviceId = deviceId;
			this.attribute = attribute;
			this.responseTypeClass = responseTypeClass;
			this.subscriber = subscriber;
			this.subscriber.setResponseTypeClass(responseTypeClass);
		}
		
		public GetHandler(HttpClientSession session, String deviceId, String attribute, ParameterizedTypeReference<T> responseType, GetResponseSubscriber subscriber) {
			super();
			this.session = session;
			this.deviceId = deviceId;
			this.attribute = attribute;
			this.responseType = responseType;
			this.subscriber = subscriber;
			this.subscriber.setResponseType(responseType);
		}
		
		public T get() {
			init();
			try {
				response = get(deviceId, attribute);
				if(!response.isSuccess())
					throw new StatusException(response.getError());
				else
					return getResponse();
			} catch(Throwable e) {
				logger.error("get attribute [" + attribute + "] error: ", e);
				throw new ValueException("get attribute [" + attribute + "] error: " + e.getMessage());
			}
		}

		public void getAsync() {
			init();
			GetRequestPublisher request = new GetRequestPublisher(attribute);
			subscriber.setAttribute(attribute);
			subscriber.setDevice(device);
			subscriber.setResponseDataType(responseDataType);
			GetAsyncHandler handler = new GetAsyncHandler(deviceId, request, subscriber);
			connect(handler);
		}
		
		private void init() {
			device = session.getDevice(deviceId);
			if(device == null)
				throw new NotFoundException("device: " + deviceId);
			responseDataType = device.getDeviceType().getAttDefinition().get(attribute).getDataType();
			if(responseDataType == null)
				throw new NotFoundException("attribute: " + attribute);
		}
		
		@SuppressWarnings("unchecked")
		private T getResponse() {
			if(responseType == null)
				return (T) responseDataType.getData(response.getValue(), responseTypeClass);
			else
				return (T) responseDataType.getData(response.getValue(), responseType);
		}
		
		private Response get(String deviceId, String attribute) {
			RequestPublishSyncHandler request = new RequestPublishSyncHandler(deviceId, new GetRequestPublisher(attribute));
	        
	        connect(request);
			
	        try {
				return request.get(timeout, TimeUnit.SECONDS);
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				throw new StatusException(e.getMessage());
			}
		}
	}
	
	private class SetHandler{
		private HttpClientSession session;
		private String deviceId;
		private Device device;
		private String attribute;
		private Object value;
		private AttValueInfo attributeValue;
		
		private SetResponseSubscriber subscriber;
		
		public SetHandler(HttpClientSession session, String deviceId, String attribute, Object value) {
			super();
			this.session = session;
			this.deviceId = deviceId;
			this.attribute = attribute;
			this.value = value;
		}
		
		public SetHandler(HttpClientSession session, String deviceId, String attribute, Object value, SetResponseSubscriber subscriber) {
			super();
			this.session = session;
			this.deviceId = deviceId;
			this.attribute = attribute;
			this.value = value;
			this.subscriber = subscriber;
		}
		
		public void set() {
			try{
				init();
				set(deviceId, attribute, attributeValue);
			} catch(Throwable e) {
				logger.error("set attribute [" + attribute + "] error: ", e);
				throw new ValueException("set attribute [" + attribute + "] error: " + e.getMessage());
			}
		}

		private void init() {
			device = session.getDevice(deviceId);
			if(device == null)
				throw new NotFoundException("device: " + deviceId);
			AttributeType attType = device.getDeviceType().getAttDefinition().get(attribute);
			if(attType == null)
				throw new NotFoundException("attribute: " + attribute);
			attributeValue = attType.getAttValue(value);
		}
		
		public void setAsync() {
			try{
				init();
				SetRequestPublisher request = new SetRequestPublisher(attribute, attributeValue);
				subscriber.setDevice(device);
				subscriber.setAttribute(attribute);
				subscriber.setValue(value);
				SetAsyncHandler handler = new SetAsyncHandler(deviceId, request, subscriber);
				connect(handler);
			} catch(Throwable e) {
				logger.error("set attribute [" + attribute + "] error: ", e);
				throw new ValueException("set attribute [" + attribute + "] error: " + e.getMessage());
			}
		}
		
		private void set(String deviceId, String attribute, AttValueInfo value) {
			RequestPublishSyncHandler request = new RequestPublishSyncHandler(deviceId, new SetRequestPublisher(attribute, value));
	        
	        connect(request);
			
	        try {
	        	Response response = request.get(timeout, TimeUnit.SECONDS);
	        	if(!response.isSuccess())
					throw new StatusException(response.getError());
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				throw new StatusException(e.getMessage());
			}
		}
	}
	
	private class ActionHandler<T> {
		private HttpClientSession session;
		private String deviceId;
		private Device device;
		private String action;
		private Object request;
		private Response response;
		private ActionType actionType;
		private DataType responseDataType;
		
		private Class<T> responseTypeClass = null;
		private ParameterizedTypeReference<T> responseType = null;
		
		private ActionResponseSubscriber subscriber;
		
		public ActionHandler(HttpClientSession session, String deviceId, String action, Object request) {
			super();
			this.session = session;
			this.deviceId = deviceId;
			this.action = action;
			this.request = request;
		}
		
		public ActionHandler(HttpClientSession session, String deviceId, String action, Object request, Class<T> responseTypeClass) {
			super();
			this.session = session;
			this.deviceId = deviceId;
			this.action = action;
			this.request = request;
			this.responseTypeClass = responseTypeClass;
		}

		public ActionHandler(HttpClientSession session, String deviceId, String action, Object request, ParameterizedTypeReference<T> responseType) {
			super();
			this.session = session;
			this.deviceId = deviceId;
			this.action = action;
			this.request = request;
			this.responseType = responseType;
		}
		
		public ActionHandler(HttpClientSession session, String deviceId, String action, Object request, ActionResponseSubscriber subscriber) {
			super();
			this.session = session;
			this.deviceId = deviceId;
			this.action = action;
			this.request = request;
			this.subscriber = subscriber;
		}
		
		public ActionHandler(HttpClientSession session, String deviceId, String action, Object request, Class<T> responseTypeClass, ActionResponseSubscriber subscriber) {
			super();
			this.session = session;
			this.deviceId = deviceId;
			this.action = action;
			this.request = request;
			this.responseTypeClass = responseTypeClass;
			this.subscriber = subscriber;
			this.subscriber.setResponseTypeClass(responseTypeClass);
		}

		public ActionHandler(HttpClientSession session, String deviceId, String action, Object request, ParameterizedTypeReference<T> responseType, ActionResponseSubscriber subscriber) {
			super();
			this.session = session;
			this.deviceId = deviceId;
			this.action = action;
			this.request = request;
			this.responseType = responseType;
			this.subscriber = subscriber;
			this.subscriber.setResponseType(responseType);
		}
		public T action() {
			AttValueInfo requestValue = init();
			
			try {
				response = action(deviceId, action, requestValue);
				if(!response.isSuccess())
					throw new StatusException(response.getError());
				else {
					if(actionType.getResponse() != null) {
						responseDataType = actionType.getResponseAttributeType().getDataType();
						return getResponse();
					}
					else
						return null;
				}
			} catch(Throwable e) {
				logger.error("action [" + action + "] response error: ", e);
				throw new ValueException("action [" + action + "] response error: " + e.getMessage());
			}
		}

		public void actionAsync() {
			AttValueInfo requestValue = init();
			ActionRequestPublisher request = new ActionRequestPublisher(action, requestValue);
			subscriber.setAction(action);
			subscriber.setDevice(device);
			subscriber.setResponseDataType(actionType.getResponseAttributeType().getDataType());
			subscriber.setRequest(request);
			ActionAsyncHandler handler = new ActionAsyncHandler(deviceId, request, subscriber);
			connect(handler);
		}
		
		private AttValueInfo init() {
			device = ((HttpClientSession) session).getDevice(deviceId);
			if(device == null)
				throw new NotFoundException("device: " + deviceId);
			actionType = device.getDeviceType().getActionTypes().get(action);
			if(actionType == null)
				throw new NotFoundException("action: " + action);
			AttValueInfo requestValue = null;
			if(actionType.getRequest() != null) {
				DataType requestType = actionType.getRequestAttributeType().getDataType();
				
				try{
					requestValue = requestType.getAttValue(request);
				} catch(Throwable e) {
					logger.error("action [" + action + "] request error: ", e);
					throw new ValueException("action [" + action + "] request error: " + e.getMessage());
				}	
			}
			return requestValue;
		}
		
		@SuppressWarnings("unchecked")
		protected T getResponse() {
			if(responseTypeClass != null)
				return  (T) responseDataType.getData(response.getValue(), responseTypeClass);
			else if(responseType != null)
				return  (T) responseDataType.getData(response.getValue(), responseType);
			else
				return null;
		}
		
		private Response action(String deviceId, String action, AttValueInfo value) {
			RequestPublishSyncHandler request = new RequestPublishSyncHandler(deviceId, new ActionRequestPublisher(action, value));
	        
	        connect(request);
			
	        try {
				return request.get(timeout, TimeUnit.SECONDS);
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				throw new StatusException(e.getMessage());
			}
		}
	}
}
