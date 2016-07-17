package edu.ncu.yang.engin;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import com.google.gson.Gson;

import edu.ncu.yang.dao.MemoryDao;
import edu.ncu.yang.dao.PictureDao;
import edu.ncu.yang.domain.Contacts;
import edu.ncu.yang.domain.ContactsInfo;
import edu.ncu.yang.domain.Picture;
import edu.ncu.yang.domain.PictureInfo;

public class PictureService implements Backup<Picture>{
	private PictureDao dao ;
	private MemoryDao memoryDao;
	public PictureService(){
		this.dao = new PictureDao();
		memoryDao = new MemoryDao();
	}
	
	@Override
	public List<Picture> query(int uid, int offset, int number) {
		return dao.query(uid, offset, number, 0);
	}
	@Override
	public Picture add(Picture d) {
		memoryDao.add(d.getUid(), d.getSize());
		return dao.add(d);
	}
	@Override
	public boolean delete(Picture d) {
		memoryDao.delete(d.getUid(), d.getSize());
		return dao.delete(d.getUid(), d.getName());
	}
	@Override
	public boolean delete(int uid, int id) {
		Picture picture = dao.queryByName(uid, id, 0);
		return delete(picture);
	}
	@Override
	public boolean isExist(Picture d) {
		return dao.queryByName(d.getUid(), d.getName(),0)!=null;
	}

	@Override
	public String queryToJson(int uid, int offset, int number) {
		List<Picture> list = query(uid, offset, number);
//		List<PictureInfo> infos = new ArrayList<PictureInfo>();
//		for(Picture picture: list){
//			infos.add(new PictureInfo(picture));
//		}
		JSONObject object = new JSONObject();
		object.put("numbers", list.size());
		object.put("data",new Gson().toJson(list));

		return object.toString();
	}

}
