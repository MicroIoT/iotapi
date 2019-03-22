package com.leaniot.api.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RestGeoResult<T> {
	private T content;
	private RestDistance distance;
	
	@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
	public RestGeoResult(@JsonProperty("content") T content, 
			@JsonProperty("distance")RestDistance distance) {
		super();
		this.content = content;
		this.distance = distance;
	}
	public T getContent() {
		return content;
	}
	public void setContent(T content) {
		this.content = content;
	}
	public RestDistance getDistance() {
		return distance;
	}
	public void setDistance(RestDistance distance) {
		this.distance = distance;
	}
}
