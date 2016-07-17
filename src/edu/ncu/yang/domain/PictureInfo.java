package edu.ncu.yang.domain;

import com.google.gson.Gson;

/**
 * Created by Mr_Yang on 2016/7/1.
 */
public class PictureInfo {
    private String path;
    private String name;
    private long lastModified;
    private long size;

    public PictureInfo(String path, String name, long lastModified, long size) {
        this.path = path;
        this.name = name;
        this.lastModified = lastModified;
        this.size = size;
    }

    public PictureInfo() {

    }
    public PictureInfo(PictureInfo info) {
    	this.lastModified = info.getLastModified();
    	this.name = info.getName();
    	this.path = info.getPath();
    	this.size = info.getSize();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
    
    public String toJson(){
    	 return new Gson().toJson(this);
    }
    public static PictureInfo toImageInfo(String json){
    	return new Gson().fromJson(json, PictureInfo.class);
    }
}
