package edu.ncu.yang.test;

import java.sql.SQLException;
import java.util.List;

import junit.framework.TestCase;
import edu.ncu.yang.dao.ContactsDao;
import edu.ncu.yang.dao.PictureDao;
import edu.ncu.yang.dao.SmsDao;
import edu.ncu.yang.dao.UserDao;
import edu.ncu.yang.domain.Contacts;
import edu.ncu.yang.domain.Picture;
import edu.ncu.yang.domain.Sms;
import edu.ncu.yang.domain.User;
import edu.ncu.yang.engin.BackupFactory;

public class test extends TestCase{
	public void testConn() throws SQLException {
//		Connection conn = JDBCUtils.getConnection();
//		String sql = "select * from _user";
//		Statement statement = conn.createStatement();
//		ResultSet resultSet = statement.executeQuery(sql);
//		while(resultSet.next()){
//			System.out.println(resultSet.getInt("uid"));
//			System.out.println(resultSet.getString("name"));
//			System.out.println(resultSet.getString("pwd"));
//			System.out.println(resultSet.getString("icon"));
//			System.out.println(resultSet.getInt("usertype"));
//		}
//		JDBCUtils.close(conn, statement, resultSet);
		UserDao dao = new UserDao();
		User user = dao.queryUserByName("yang0");
		System.out.println(user.toJson());
	}
	
	public void test2(){
		PictureDao dao = new PictureDao();
		List<Picture> query = dao.query(1, 12, 10, 0);
		for(Picture p: query){
			System.out.println(p.toJson());
		}
	}
	
	public void test3(){
		PictureDao dao = new PictureDao();
		Picture query = dao.queryByName(1, "dfgsdgfasd",0);
		System.out.println(query.toJson());
	}
	public void test4(){
		PictureDao dao = new PictureDao();
		Picture p = new Picture(1, 0, 0, 0, 0, 0, System.currentTimeMillis(), 12345, "","yang");
		Picture query = dao.add(p);
		System.out.println(query.toJson());
	}
	
	public void test5(){
		PictureDao dao = new PictureDao();
		if(dao.delete(1, "yang")){
			System.out.println("ok");
		}else{
			System.out.println("no");
		}
	}
	public void test6(){
		SmsDao dao = new SmsDao();
		List<Sms> query = dao.query(1, 0, 20, 0);
		for(Sms m: query){
			System.out.println(m.toJson());
		}
	}
	
	public void test7(){
		SmsDao dao = new SmsDao();
		Sms query = dao.queryBy(1, "110", 123, "1122",0);
		System.out.println(query.toJson());
	}
	
	public void test8(){
		SmsDao dao = new SmsDao();
		Sms msg = new Sms(1,0,542452345,0,0,0,"1234567890",878675663456546l,1,"akghasdfiofghasiofgasdifgajhsiopfg");
		Sms query = dao.add(msg);
		System.out.println(query.toJson());
	}
	
	public void test9(){
		SmsDao dao = new SmsDao();
		Sms msg = new Sms(1,0,542452345,0,0,0,"1234567890",878675663456546l,1,"akghasdfiofghasiofgasdifgajhsiopfg");
		
		Sms query = dao.add(msg);
		System.out.println(query.toJson());
		System.out.println( dao.delete(msg));
	}
	
	public void test10(){
		ContactsDao dao = new ContactsDao();
		List<Contacts> query = dao.query(1, 5, 50, 1);
		System.out.println(query.size());
		for(Contacts m: query){
			System.out.println(m.toJson());
		}
	}
	
	public void test11(){
		ContactsDao dao = new ContactsDao();
		Contacts query = dao.queryBy(1, "yzq", "12345678790", 1);
		System.out.println(query);
		System.out.println(query.toJson());
	}
	
	public void test12(){
		ContactsDao dao = new ContactsDao();
		Contacts contacts = new Contacts(11, 0, 0, 0, 0, 0, "aaa", "110");
		Contacts c = dao.add(contacts);
		System.out.println(c.toJson());
	}
	
	public void test13(){
		ContactsDao dao = new ContactsDao();
		Contacts contacts = new Contacts(11, 0, 0, 0, 0, 0, "aaa", "110");
//		Contacts c = dao.add(contacts);
//		System.out.println(c.toJson());
		System.out.println(dao.delete(contacts));
	}
	
	public void test14(){
		System.out.println(BackupFactory.createContactsBackup().queryToJson(1, 0, 20));
		
	}
}
