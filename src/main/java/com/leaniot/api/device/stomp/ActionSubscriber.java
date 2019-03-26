package com.leaniot.api.device.stomp;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.leaniot.api.dto.ActionRequest;
import com.leaniot.api.dto.Response;
import com.leaniot.api.stomp.OperationSubscriber;
import com.leaniot.domain.ActionType;
import com.leaniot.domain.attribute.DataValue;
import com.leaniot.domain.attribute.StructType;
import com.leaniot.exception.NotFoundException;

/**
 * 设备端action操作处理，设备收到action操作请求后，将请求值转换为用户定义的类型，供用户处理action请求，
 * 返回响应值，将响应值转换为底层响应的格式。
 *
 * @author 曹新宇
 */
@Component
public abstract class ActionSubscriber extends OperationSubscriber {
	private Map<String, Class<?>> actionType;
	private Map<String, ActionType> actionTypes;
	
	/**
	 * 设备端action处理操作构造函数。
	 */
	public ActionSubscriber() {
		super();
	}

	/**
	 * 设置action操作的请求类型信息。
	 * @param actionType 每个key代表一个action操作，每个value代表action操作的请求的类型。
	 */
	public void setActionType(Map<String, Class<?>> actionType) {
		this.actionType = actionType;
	}

	/**
	 *  将action请求中的请求值转变为用户的类型，调用设备的action操作，
	 *  将返回的响应值转换为协议要求的格式，返回操作Response。
	 * @return 返回Response。
	 */
	@Override
	public Response getResponse() {
		this.actionTypes = this.getDevice().getDeviceType().getActionTypes();
		ActionRequest req = (ActionRequest) request;
		try {
			ActionType aType = this.actionTypes.get(req.getAction());
			Object value = null;
			if(aType.getRequest() != null) {
				StructType reqType = new StructType(aType.getRequest());
				Class<?> t = actionType.get(req.getAction());
				if(t == null)
					throw new NotFoundException(req.getAction() + " converter");
				value = reqType.getValue(req.getValue(), t);
			}
			Object res = action(req.getAction(), value);
			DataValue data = null;
			if(aType.getResponse() != null) {
				StructType resType = new StructType(aType.getResponse());
				data = resType.getAttData(res);
			}
			return new Response(true, null, data);
		} catch(Throwable e) {
			return new Response(false, e.getMessage(), null);
		}
	}

	/**
	 * 不同设备的具体action操作的实现。
	 * @param actionName action操作的名称。
	 * @param request action操作的请求值。
	 * @return 返回响应值。
	 */
	public abstract Object action(String actionName, Object request);
}
