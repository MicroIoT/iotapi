package top.microiot.api.dto;

import top.microiot.domain.attribute.DataValue;

public class Response{
	private boolean success;
	private String error;
	private DataValue value;
	
	public Response() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Response(boolean success, String error, DataValue value) {
		super();
		this.success = success;
		this.error = error;
		this.value = value;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public DataValue getValue() {
		return value;
	}
	public void setValue(DataValue value) {
		this.value = value;
	}
}
