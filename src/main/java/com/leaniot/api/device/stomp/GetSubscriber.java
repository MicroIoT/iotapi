package com.leaniot.api.device.stomp;

import java.util.Map;

import com.leaniot.api.dto.GetRequest;
import com.leaniot.api.dto.Response;
import com.leaniot.api.stomp.OperationSubscriber;
import com.leaniot.domain.attribute.AttributeType;
import com.leaniot.domain.attribute.DataValue;

/**
 * 设备端get操作处理，设备收到get操作请求后，返回属性值，将属性值转换为底层响应的格式。
 *
 * @author 曹新宇
 */
public abstract class GetSubscriber extends OperationSubscriber {
	private Map<String, AttributeType> attDefinition;
	public GetSubscriber() {
		super();
	}

	/**
	 * 调用设备的get操作，将返回的属性值转换为协议要求的格式，返回操作Response。
	 * @return 返回Response。
	 */
	@Override
	public Response getResponse() {
		this.attDefinition = this.getDevice().getDeviceType().getAttDefinition();
		GetRequest req = (GetRequest) request;
		try {
			Object res = getAttributeValue(req.getAttribute());
			AttributeType type = this.attDefinition.get(req.getAttribute());
			DataValue data = type.getAttData(res);
			return new Response(true, null, data);
		} catch(Throwable e) {
			return new Response(false, e.getMessage(), null);
		}
		
	}

	/**
	 * 不同设备的具体get操作的实现。
	 * @param attribute get操作的属性名称。
	 * @return 返回属性值。
	 */
	public abstract Object getAttributeValue(String attribute);
}
