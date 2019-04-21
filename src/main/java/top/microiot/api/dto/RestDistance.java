package top.microiot.api.dto;

import org.springframework.data.geo.Metrics;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RestDistance {
	private double value;
	private Metrics metric;
	
	@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
	public RestDistance(@JsonProperty("value") double value, 
			@JsonProperty("metric") Metrics metric) {
		super();
		this.value = value;
		this.metric = metric;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public Metrics getMetric() {
		return metric;
	}
	public void setMetric(Metrics metric) {
		this.metric = metric;
	}
}
