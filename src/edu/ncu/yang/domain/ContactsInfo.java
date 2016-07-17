package edu.ncu.yang.domain;

import com.google.gson.Gson;

public class ContactsInfo {
	private String name;
	private String phoneNumber;
	public ContactsInfo(){}
	public ContactsInfo(ContactsInfo info){
		this.name = info.getName();
		this.phoneNumber = info.getPhoneNumber();
	}
	public ContactsInfo(String name, String phoneNumber) {
		this.name = name;
		this.phoneNumber = phoneNumber;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public String toJson(){
		return new Gson().toJson(this);
	}
	public static ContactsInfo toContactsInfo(String json){
		return new Gson().fromJson(json, ContactsInfo.class);
	}
}
