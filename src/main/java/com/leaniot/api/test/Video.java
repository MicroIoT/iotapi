package com.leaniot.api.test;

import java.util.Set;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="material")
public class Video extends Material {
	private Set<String> urls;

	public Video() {
		super();
		this.setType(Material.VIDEO);
	}

	public Video(String name, Set<String> urls) {
		super(name);
		this.urls = urls;
		this.setType(Material.VIDEO);
	}

	public Set<String> getUrls() {
		return urls;
	}

	public void setUrls(Set<String> urls) {
		this.urls = urls;
	}
}
