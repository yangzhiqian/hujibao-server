package edu.ncu.yang.domain;

import com.google.gson.Gson;

public class Sms extends SmsInfo {

	private int uid;
	private int mid;
	private long updateTime;
	private long deleteTime;
	private int state;
	private int mmid;
	public Sms(){
		super();
	}
	public Sms(int uid, int mid, long updateTime, long deleteTime,
			int state, int mmid,String address, long date, int type, String body) {
		super(address,date,type,body);
		this.uid = uid;
		this.mid = mid;
		this.updateTime = updateTime;
		this.deleteTime = deleteTime;
		this.state = state;
		this.mmid = mmid;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public int getMid() {
		return mid;
	}
	public void setMid(int mid) {
		this.mid = mid;
	}
	public long getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}
	public long getDeleteTime() {
		return deleteTime;
	}
	public void setDeleteTime(long deleteTime) {
		this.deleteTime = deleteTime;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getMmid() {
		return mmid;
	}
	public void setMmid(int mmid) {
		this.mmid = mmid;
	}
	
	
	public String toJson(){
		return new Gson().toJson(this);
	}
	public static Sms toSms(String json){
		return new Gson().fromJson(json,Sms.class);
	}
}
