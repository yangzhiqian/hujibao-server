package edu.ncu.yang.engin;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

import edu.ncu.yang.dao.ContactsDao;
import edu.ncu.yang.domain.Contacts;
import edu.ncu.yang.domain.ContactsInfo;

public class ContactsService implements Backup<Contacts>{
	private ContactsDao dao;
	public ContactsService(){
		this.dao = new ContactsDao();
	}

	@Override
	public List<Contacts> query(int uid, int offset, int number) {
		return dao.query(uid, offset, number, 0);
	}

	@Override
	public Contacts add(Contacts d) {
		return dao.add(d);
	}

	@Override
	public boolean delete(Contacts d) {
		return dao.delete(d);
	}
	
	@Override
	public boolean delete(int uid, int id) {
		return dao.delete(uid,id);
	}

	@Override
	public boolean isExist(Contacts d) {
		return dao.queryBy(d.getUid(), d.getName(), d.getPhoneNumber(), 0)!=null;
	}

	@Override
	public String queryToJson(int uid, int offset, int number) {
		List<Contacts> list = query(uid, offset, number);
//		List<ContactsInfo> infos = new ArrayList<ContactsInfo>();
//		for(Contacts contacts: list){
//			infos.add(new ContactsInfo(contacts));
//		}
		JSONObject object = new JSONObject();
		object.put("numbers", list.size());
		object.put("data",new Gson().toJson(list));

		return object.toString();
	}
	

}
