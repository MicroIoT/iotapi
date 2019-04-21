package top.microiot.api.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RestGeoResults<T> {
	private List<RestGeoResult<T>> content;
	private RestDistance averageDistance;

	@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
	public RestGeoResults(@JsonProperty("content") List<RestGeoResult<T>> content, 
			@JsonProperty("averageDistance") RestDistance averageDistance) {
		super();
		this.content = content;
		this.averageDistance = averageDistance;
	}
	public List<RestGeoResult<T>> getContent() {
		return content;
	}
	public void setContent(List<RestGeoResult<T>> content) {
		this.content = content;
	}
	public RestDistance getAverageDistance() {
		return averageDistance;
	}
	public void setAverageDistance(RestDistance averageDistance) {
		this.averageDistance = averageDistance;
	}
}
