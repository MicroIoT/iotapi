package com.leaniot.api.device.stomp;

import java.util.Map;

import com.leaniot.api.dto.Response;
import com.leaniot.api.dto.SetRequest;
import com.leaniot.api.stomp.OperationSubscriber;
import com.leaniot.domain.attribute.AttributeType;
import com.leaniot.exception.NotFoundException;

/**
 * 设备端set操作处理，设备收到set操作请求后，将请求的属性值转换为用户定义的类型，供用户处理set请求。
 *
 * @author 曹新宇
 */
public abstract class SetSubscriber extends OperationSubscriber {
	private Map<String, Class<?>> attType;
	private Map<String, AttributeType> attDefinition;
	
	/**
	 * 设备端set处理操作构造函数。
	 * @param attType 每个key代表一个属性，每个value代表属性值的类型。
	 */
	public SetSubscriber(Map<String, Class<?>> attType) {
		super();
		this.attType = attType;
	}

	/**
	 * 将set请求中的属性值转变为用户的类型，调用设备的set操作，返回操作Response。
	 * @return 返回Response。
	 */
	@Override
	public Response getResponse() {
		this.attDefinition = this.getDevice().getDeviceType().getAttDefinition();
		SetRequest req = (SetRequest) request;
		try {
			AttributeType type = this.attDefinition.get(req.getAttribute());
			Class<?> t = attType.get(req.getAttribute());
			if(t == null)
				throw new NotFoundException(req.getAttribute() + " converter");
			Object value = type.getValue(req.getValue(), t);
			setAttribute(req.getAttribute(), value);
			return new Response(true, null, null);
		} catch(Throwable e) {
			return new Response(false, e.getMessage(), null);
		}
	}

	/**
	 * 不同设备的具体set操作的实现。
	 * @param attribute set操作的属性名称。
	 * @param value set操作的属性值。
	 */
	public abstract void setAttribute(String attribute, Object value);
}
