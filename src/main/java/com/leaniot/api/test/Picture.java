package com.leaniot.api.test;

import java.util.Set;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="material")
public class Picture extends Material {
	private Set<String> urls;

	public Picture() {
		super();
		this.setType(Material.PICTURE);
	}

	public Picture(String name, Set<String> urls) {
		super(name);
		this.urls = urls;
		this.setType(Material.PICTURE);
	}

	public Set<String> getUrls() {
		return urls;
	}

	public void setUrls(Set<String> urls) {
		this.urls = urls;
	}
}
