package com.leaniot.api.test;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.DBRef;

public class FixScreen {
	private Layout layout;
	private String hWeight;
	private String vWeight;
	@DBRef
	private List<Material> materials;
	public FixScreen() {
		super();
		// TODO Auto-generated constructor stub
	}
	public FixScreen(Layout layout, String hWeight, String vWeight, List<Material> materials) {
		super();
		this.layout = layout;
		this.hWeight = hWeight;
		this.vWeight = vWeight;
		this.materials = materials;
	}
	public Layout getLayout() {
		return layout;
	}
	public void setLayout(Layout layout) {
		this.layout = layout;
	}
	public String gethWeight() {
		return hWeight;
	}
	public void sethWeight(String hWeight) {
		this.hWeight = hWeight;
	}
	public String getvWeight() {
		return vWeight;
	}
	public void setvWeight(String vWeight) {
		this.vWeight = vWeight;
	}
	public List<Material> getMaterials() {
		return materials;
	}
	public void setMaterials(List<Material> materials) {
		this.materials = materials;
	}
}