package com.leaniot.api.test;

import org.springframework.data.mongodb.core.mapping.DBRef;

public class FloatScreen {
	@DBRef
	private Material material;
	private int x;
	private int y;
	private int width;
	private int height;
	
	public FloatScreen() {
		super();
		// TODO Auto-generated constructor stub
	}
	public FloatScreen(Material material, int x, int y, int width, int height) {
		super();
		this.material = material;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	public Material getMaterial() {
		return material;
	}
	public void setMaterial(Material material) {
		this.material = material;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
}
