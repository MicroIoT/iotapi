package top.microiot.api.device.stomp;

import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import top.microiot.api.dto.Action;
import top.microiot.api.dto.Response;
import top.microiot.domain.ActionType;
import top.microiot.domain.Device;
import top.microiot.domain.User;
import top.microiot.domain.attribute.DataType;
import top.microiot.domain.attribute.DataValue;

/**
 * 设备端操作请求处理，设备收到操作请求后，将请求值转换为用户定义的类型，供用户处理操作请求，
 * 返回响应值，将响应值转换为底层响应的格式。
 *
 * @author 曹新宇
 */
@Component
public abstract class ActionRequestSubscriber extends RequestSubscriber {
	private Map<String, ActionType> actionTypes;
	
	/**
	 * 设备端操作请求处理构造函数。
	 */
	public ActionRequestSubscriber() {
		super();
	}

	/**
	 *  将操作请求中的请求值转变为用户的类型，调用设备的操作，
	 *  将返回的响应值转换为协议要求的格式，返回操作响应。
	 * @return 返回响应。
	 */
	@Override
	public Response getResponse() {
		this.actionTypes = this.getDevice().getDeviceType().getActionTypes();
		Action req = (Action) request;
		try {
			ActionType actType = this.actionTypes.get(req.getAction());
			Object requestValue = null;
			if(actType.getRequest() != null) {
				DataType requestType = actType.getRequestAttributeType().getDataType();
				Object type = getType(req);
				if(type instanceof Class<?>) {
					Class<?> t = (Class<?>) type;
					requestValue = requestType.getValue(req.getValue(), t);
				}
				else if(type instanceof ParameterizedTypeReference<?>) {
					ParameterizedTypeReference<?> t = (ParameterizedTypeReference<?>) type;
					requestValue = requestType.getValue(req.getValue(), t);
				}
			}
			Object res = action(req.getRequester(), this.getDevice(), req.getAction(), requestValue);
			DataValue responseValue = null;
			if(actType.getResponse() != null) {
				DataType resType = actType.getResponseAttributeType().getDataType();
				responseValue = resType.getAttData(res);
			}
			return new Response(true, null, responseValue);
		} catch(Throwable e) {
			return new Response(false, e.getMessage(), null);
		}
	}

	private Object getType(Action req) {
		return types.get(req.getAction());
	}

	/**
	 * 不同设备的具体操作的实现。
	 * @param device 操作的设备。
	 * @param action 操作的名称。
	 * @param request 操作的请求值。
	 * @return 返回响应值。
	 */
	public abstract Object action(User requester, Device device, String action, Object request);
}
