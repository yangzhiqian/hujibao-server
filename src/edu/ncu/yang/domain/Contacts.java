package edu.ncu.yang.domain;

import com.google.gson.Gson;

public class Contacts extends ContactsInfo {
	private int uid;
	private int cid;
	private long updatetime;
	private long deletetime;
	private int state;
	private int ccid;
	
	
	public Contacts() {
		super();
	}


	public Contacts(int uid, int cid, long updatetime, long deletetime,
			int state, int ccid,String name, String phoneNumber) {
		super(name,phoneNumber);
		this.uid = uid;
		this.cid = cid;
		this.updatetime = updatetime;
		this.deletetime = deletetime;
		this.state = state;
		this.ccid = ccid;
	}


	public int getUid() {
		return uid;
	}


	public void setUid(int uid) {
		this.uid = uid;
	}


	public int getCid() {
		return cid;
	}


	public void setCid(int cid) {
		this.cid = cid;
	}


	public long getUpdatetime() {
		return updatetime;
	}


	public void setUpdatetime(long updatetime) {
		this.updatetime = updatetime;
	}


	public long getDeletetime() {
		return deletetime;
	}


	public void setDeletetime(long deletetime) {
		this.deletetime = deletetime;
	}


	public int getState() {
		return state;
	}


	public void setState(int state) {
		this.state = state;
	}


	public int getCcid() {
		return ccid;
	}


	public void setCcid(int ccid) {
		this.ccid = ccid;
	}
	
	public String toJson(){
		return new Gson().toJson(this);
	}
	public static Contacts toContacts(String json){
		return new Gson().fromJson(json, Contacts.class);
	}
	
	
	
}
