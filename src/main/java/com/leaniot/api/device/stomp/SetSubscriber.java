package com.leaniot.api.device.stomp;

import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import com.leaniot.api.dto.Response;
import com.leaniot.api.dto.SetRequest;
import com.leaniot.api.stomp.OperationSubscriber;
import com.leaniot.domain.attribute.DataType;
import com.leaniot.domain.attribute.DeviceAttributeType;
import com.leaniot.exception.NotFoundException;

/**
 * 设备端set操作处理，设备收到set操作请求后，将请求的属性值转换为用户定义的类型，供用户处理set请求。
 *
 * @author 曹新宇
 */
@Component
public abstract class SetSubscriber extends OperationSubscriber {
	private Map<String, DeviceAttributeType> attDefinition;
	
	/**
	 * 设备端set处理操作构造函数。
	 */
	public SetSubscriber() {
		super();
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
			DataType type = this.attDefinition.get(req.getAttribute()).getDataType();
			Object attributeValue;
			Object t = getType(req);
			if(t instanceof Class<?>) {
				Class<?> tclass = (Class<?>) t;
				attributeValue = type.getValue(req.getValue(), tclass);
			}
			else if(t instanceof ParameterizedTypeReference<?>) {
				ParameterizedTypeReference<?> tclass = (ParameterizedTypeReference<?>) t;
				attributeValue = type.getValue(req.getValue(), tclass);
			}
			else
				throw new NotFoundException(req.getAttribute() + " converter");
			
			setAttribute(req.getAttribute(), attributeValue);
			return new Response(true, null, null);
		} catch(Throwable e) {
			return new Response(false, e.getMessage(), null);
		}
	}

	private Object getType(SetRequest req) {
		return types.get(req.getAttribute());
	}

	/**
	 * 不同设备的具体set操作的实现。
	 * @param attribute set操作的属性名称。
	 * @param value set操作的属性值。
	 */
	public abstract void setAttribute(String attribute, Object value);
}
