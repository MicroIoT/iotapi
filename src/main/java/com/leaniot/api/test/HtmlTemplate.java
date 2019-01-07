package com.leaniot.api.test;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="material")
public class HtmlTemplate extends Material {
	public static String PM25Male = "pm25_male";
	public static String PM10Male = "pm10_male";
	public static String NH3Male = "nh3_male";
	public static String H2SMale = "h2s_male";
	public static String TempMale = "temp_male";
	public static String HumiMale = "humi_male";
	public static String OccupyMale = "occupy_male";
	public static String VipOccupyMale = "vip_occupy_male";
	public static String PM25Female = "pm25_female";
	public static String PM10Female  = "pm10_female";
	public static String NH3Female  = "nh3_female";
	public static String H2SFemale  = "h2s_female";
	public static String TempFemale  = "temp_female";
	public static String HumiFemale  = "humi_female";
	public static String OccupyFemale  = "occupy_female";
	public static String VipOccupyFemale  = "vip_occupy_female";
	public static String Water  = "water";
	public static String Energy  = "energy";
	
	private String url;
	private Period period;
	private int number;
	private List<String> tags;
	
	public HtmlTemplate() {
		super();
		this.setType(Material.HTML_TEMPLATE);
	}
	public HtmlTemplate(String name, String url, Period period, int number, List<String> tags) {
		super(name);
		this.url = url;
		this.period = period;
		this.number = number;
		this.tags = tags;
		this.setType(Material.HTML_TEMPLATE);
	}
	public HtmlTemplate(String name, String url, Period period, int number) {
		super(name);
		this.url = url;
		this.period = period;
		this.number = number;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Period getPeriod() {
		return period;
	}
	public void setPeriod(Period period) {
		this.period = period;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	
	
}
