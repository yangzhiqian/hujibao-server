package edu.ncu.yang.engin;

import java.util.List;

public interface Backup<D> {
	List<D> query(int uid,int offset,int number);
	D add(D d);
	boolean delete(D d);
	boolean delete(int uid,int id);
	boolean isExist(D d);
	String queryToJson(int uid,int offset,int number);
}
