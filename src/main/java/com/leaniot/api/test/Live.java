package com.leaniot.api.test;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="material")
public class Live extends Material {
	private String url;

	public Live() {
		super();
		this.setType(Material.LIVE);
	}

	public Live(String name, String url) {
		super(name);
		this.url = url;
		this.setType(Material.LIVE);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	
}
