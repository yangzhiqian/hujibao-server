package edu.ncu.yang.domain;

import com.google.gson.Gson;

public class SmsInfo {

	public static final int IN = 0;
	public static final int OUT = 1;
	private String address;
	private long date;
	private int type;
	private String body;

	public SmsInfo() {

	}

	public SmsInfo(SmsInfo info) {
		this.address = info.getAddress();
		this.body = info.getBody();
		this.date = info.getDate();
		this.type = info.getType();
	}

	public SmsInfo(String address, long date, int type, String body) {
		this.address = address;
		this.date = date;
		this.type = type;
		this.body = body;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String toJson() {
		return new Gson().toJson(this);
	}

	public static SmsInfo toSmsInfo(String json) {
		return new Gson().fromJson(json, SmsInfo.class);
	}
}
