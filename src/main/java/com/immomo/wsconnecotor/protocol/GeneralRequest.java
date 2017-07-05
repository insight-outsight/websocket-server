package com.immomo.wsconnecotor.protocol;

import java.util.Map;

public class GeneralRequest{
	private String bt;
	private String _;
	private String id;
	private String momoId;
	private String to ;
	private String type;
	private String text;
	private Map<String,String> data;
	
	public GeneralRequest() {
		super();
	}
	
	public GeneralRequest(String bt, String _) {
		this.bt = bt;
		this._ = _;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMomoId() {
		return momoId;
	}
	public void setMomoId(String momoId) {
		this.momoId = momoId;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}
	
	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}

	public String getBt() {
		return bt;
	}

	public void setBt(String bt) {
		this.bt = bt;
	}

	public String get_() {
		return _;
	}

	public void set_(String _) {
		this._ = _;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return "GeneralRequest [bt=" + bt + ", _=" + _ + ", id=" + id + ", momoId=" + momoId + ", to=" + to + ", type="
				+ type + ", text=" + text + ", data=" + data + "]";
	}


	
	
	
}
