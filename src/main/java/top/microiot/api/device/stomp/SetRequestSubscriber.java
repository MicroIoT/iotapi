package top.microiot.api.device.stomp;

import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import top.microiot.domain.Device;
import top.microiot.domain.Response;
import top.microiot.domain.Set;
import top.microiot.domain.User;
import top.microiot.domain.attribute.DataType;
import top.microiot.domain.attribute.DeviceAttributeType;
import top.microiot.exception.ValueException;

/**
 * 设备端设置请求处理，设备收到设置请求后，将请求的属性值转换为用户定义的类型，供用户处理设置请求。
 *
 * @author 曹新宇
 */
@Component
public abstract class SetRequestSubscriber extends RequestSubscriber {
	private Map<String, DeviceAttributeType> attDefinition;
	
	/**
	 * 设备端设置请求处理构造函数。
	 */
	public SetRequestSubscriber() {
		super();
	}

	/**
	 * 将设置请求中的属性值转变为用户的类型，调用设备的设置方法，返回操作响应。
	 * @return 返回响应。
	 */
	@Override
	public Response getResponse() {
		this.attDefinition = this.getDevice().getDeviceType().getAttDefinition();
		Set req = (Set) request;
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
				throw new ValueException("attribute: " + req.getAttribute() + " can't be converted, please add its convert class");
			
			setAttribute(req.getRequester(), this.getDevice(), req.getAttribute(), attributeValue);
			return new Response(true, null, null);
		} catch(Throwable e) {
			return new Response(false, e.getMessage(), null);
		}
	}

	private Object getType(Set req) {
		return types.get(req.getAttribute());
	}

	/**
	 * 不同设备的具体设置的实现。
	 * @param requester 设置的请求者。
	 * @param device 设置的设备。
	 * @param attribute 设置的属性名称。
	 * @param value 设置的属性值。
	 */
	public abstract void setAttribute(User requester, Device device, String attribute, Object value);
}
