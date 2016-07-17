package edu.ncu.yang.engin;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import com.google.gson.Gson;

import edu.ncu.yang.dao.SmsDao;
import edu.ncu.yang.domain.Sms;
import edu.ncu.yang.domain.SmsInfo;

public class SmsService implements Backup<Sms>{
	private SmsDao dao;
	public SmsService(){
		dao = new SmsDao();
	}
	@Override
	public List<Sms> query(int uid, int offset, int number) {
		return dao.query(uid, offset, number, 0);
	}
	@Override
	public Sms add(Sms d) {
		return dao.add(d);
	}
	@Override
	public boolean delete(Sms d) {
		return dao.delete(d);
	}
	@Override
	public boolean delete(int uid, int id) {
		return dao.delete(uid,id);
	}
	@Override
	public boolean isExist(Sms d) {
		return dao.queryBy(d.getUid(), d.getAddress(), d.getDate(), d.getBody(), 0)!=null;
	}
	@Override
	public String queryToJson(int uid, int offset, int number) {
		List<Sms> list = query(uid, offset, number);
//		List<SmsInfo> infos = new ArrayList<SmsInfo>();
//		for(Sms sms: list){
//			infos.add(new SmsInfo(sms));
//		}
		JSONObject object = new JSONObject();
		object.put("numbers", list.size());
		object.put("data",new Gson().toJson(list));

		return object.toString();
	}
	
}
