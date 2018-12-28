package com.leaniot.api.test;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ 
	@Type(value = Live.class, name = Material.LIVE), 
	@Type(value = HtmlTemplate.class, name = Material.HTML_TEMPLATE),
	@Type(value = Picture.class, name = Material.PICTURE), 
	@Type(value = Video.class, name =Material. VIDEO) })
public abstract class Material {
	public static final String VIDEO = "Video";
	public static final String PICTURE = "Picture";
	public static final String HTML_TEMPLATE = "HtmlTemplate";
	public static final String LIVE = "Live";
	@Id
    private String id;
	@Indexed(unique=true)
	private String name;
	private String type;
	public Material() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Material(String name) {
		super();
		this.name = name;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
