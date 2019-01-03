package com.leaniot.api.test;

public class Marquee {
	private String text;
	private int size;
	private String color;
	private String font;
	private Position position;
	public Marquee() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Marquee(String text, int size, String color, String font, Position position) {
		super();
		this.text = text;
		this.size = size;
		this.color = color;
		this.font = font;
		this.position = position;
	}

	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getFont() {
		return font;
	}
	public void setFont(String font) {
		this.font = font;
	}
	public Position getPosition() {
		return position;
	}
	public void setPosition(Position position) {
		this.position = position;
	}
}
