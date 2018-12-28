package com.leaniot.api.test;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Screen {
	@Id
    private String id;
	@Indexed(unique=true)
	private String name;
	private FixScreen fix;
	private List<FloatScreen> floats;
	
	public Screen() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Screen(String name, FixScreen fix, List<FloatScreen> floats) {
		super();
		this.name = name;
		this.fix = fix;
		this.floats = floats;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FixScreen getFix() {
		return fix;
	}

	public void setFix(FixScreen fix) {
		this.fix = fix;
	}

	public List<FloatScreen> getFloats() {
		return floats;
	}

	public void setFloats(List<FloatScreen> floats) {
		this.floats = floats;
	}
}
