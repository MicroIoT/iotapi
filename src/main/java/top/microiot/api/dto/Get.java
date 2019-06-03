package top.microiot.api.dto;

/**
 * 获取请求类。
 *
 * @author 曹新宇
 */
public class Get extends Request{
	private String attribute;

	public Get() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Get(String requestId, String attribute) {
		super(requestId);
		this.attribute = attribute;
	}
	
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString() + " get: " + attribute;
	}
}
