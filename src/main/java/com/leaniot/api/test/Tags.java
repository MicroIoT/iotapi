package com.leaniot.api.test;


import java.util.List;

public class Tags {
	private ScreenType screen;
	private int id;
	private List<Tag> tags;
	
	public Tags() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Tags(ScreenType screen, int id, List<Tag> tags) {
		super();
		this.screen = screen;
		this.id = id;
		this.tags = tags;
	}
	public ScreenType getScreen() {
		return screen;
	}
	public void setScreen(ScreenType screen) {
		this.screen = screen;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<Tag> getTags() {
		return tags;
	}
	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}
}
