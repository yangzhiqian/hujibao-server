package edu.ncu.yang.domain;

import com.google.gson.Gson;

public class Picture extends PictureInfo{
	private int uid;
	private int pid;
	private long updatetime;
	private long deletetime;
	private int state;
	private int ppid;
	public Picture() {
		super();
	}
	public Picture(int uid, int pid, long updatetime, long deletetime,
			int state, int ppid, long lastmodifytime, int picturesize,
			String path,String filename) {
		super(path,filename,lastmodifytime,picturesize);
		this.uid = uid;
		this.pid = pid;
		this.updatetime = updatetime;
		this.deletetime = deletetime;
		this.state = state;
		this.ppid = ppid;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
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
	public int getPpid() {
		return ppid;
	}
	public void setPpid(int ppid) {
		this.ppid = ppid;
	}
	public String toJson(){
		return new Gson().toJson(this);
	}
	public static Picture toPicture(String json){
		return new Gson().fromJson(json, Picture.class);
	}

	
}
