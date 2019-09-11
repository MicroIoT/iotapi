package top.microiot.api.device.stomp;

import java.util.Map;

import org.springframework.stereotype.Component;

import top.microiot.api.dto.Get;
import top.microiot.api.dto.Response;
import top.microiot.domain.Device;
import top.microiot.domain.User;
import top.microiot.domain.attribute.AttributeType;
import top.microiot.domain.attribute.DataValue;
import top.microiot.domain.attribute.DeviceAttributeType;

/**
 * 设备端获取请求处理，设备收到获取请求后，返回属性值，将属性值转换为底层响应的格式。
 *
 * @author 曹新宇
 */
@Component
public abstract class GetRequestSubscriber extends RequestSubscriber {
	private Map<String, DeviceAttributeType> attDefinition;
	
	/**
	 * 设备端获取请求处理构造函数。
	 */
	public GetRequestSubscriber() {
		super();
	}

	/**
	 * 调用设备的获取方法，将返回的属性值转换为协议要求的格式，返回操作响应。
	 * @return 返回响应。
	 */
	@Override
	public Response getResponse() {
		this.attDefinition = this.getDevice().getDeviceType().getAttDefinition();
		Get req = (Get) request;
		try {
			Object res = getAttributeValue(req.getRequester(), this.getDevice(), req.getAttribute());
			AttributeType type = this.attDefinition.get(req.getAttribute());
			DataValue responseValue = type.getAttData(res);
			return new Response(true, null, responseValue);
		} catch(Throwable e) {
			return new Response(false, e.getMessage(), null);
		}
		
	}

	/**
	 * 不同设备的具体获取的实现。
	 * @param device 获取的设备。
	 * @param attribute 获取的属性名称。
	 * @return 返回属性值。
	 */
	public abstract Object getAttributeValue(User requester, Device device, String attribute);
}
