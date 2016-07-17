package edu.ncu.yang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import edu.ncu.yang.domain.Contacts;
import edu.ncu.yang.domain.Sms;
import edu.ncu.yang.utils.JDBCUtils;

public class ContactsDao {
	public List<Contacts> query(int uid,int offset,int nums,int state){
		List<Contacts> infos = new ArrayList<Contacts>();
		Connection conn = JDBCUtils.getConnection();
		String sql = "select _contacts.*,_contactscontent.* from _contacts,_contactscontent where _contacts.uid=? and _contacts.cid=_contactscontent.cid and _contacts.state=? order by updatetime desc limit ?,?;";
		PreparedStatement st = null;
		ResultSet rt = null;
		try {
			st = conn.prepareStatement(sql);
			st.setInt(1, uid);
			st.setInt(2, state);
			st.setInt(3, offset);
			st.setInt(4, nums);
			rt = st.executeQuery();
			while (rt.next()) {
				int u = rt.getInt("uid");
				int cid = rt.getInt("cid");
				long updatetime = rt.getLong("updatetime");
				long deletetime = rt.getLong("deletetime");
				int s = rt.getInt("state");
				int ccid = rt.getInt("ccid");
				String name = rt.getString("name");
				String number = rt.getString("number");
				infos.add(new Contacts(u, cid, updatetime, deletetime, state, ccid,name,number));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("查询数据出错！");
		} finally {
			JDBCUtils.close(conn, st, rt);
		}
		return infos;
	}
	
	public Contacts queryBy(int uid,String name,String number,int state){
		Contacts contacts = null;
		String sql ="select _contacts.*,_contactscontent.* from _contacts,_contactscontent where _contacts.uid=? and _contacts.state=? and _contacts.cid=_contactscontent.cid and _contactscontent.name=? and _contactscontent.number=?;";
		Connection conn = JDBCUtils.getConnection();
		PreparedStatement ps = null;
		ResultSet rt = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, uid);
			ps.setInt(2, state);
			ps.setString(3, name);
			ps.setString(4, number);
			rt = ps.executeQuery();
			if(rt.next()){
				int cid = rt.getInt("cid");
				long updatetime = rt.getLong("updatetime");
				long deletetime = rt.getLong("deletetime");
				int ccid = rt.getInt("ccid");
				contacts = new Contacts(uid, cid, updatetime, deletetime, state, ccid,name,number);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("查询数据出错！");
		}finally{
			JDBCUtils.close(conn, ps, rt);
		}
		return contacts;
	}
	
	public Contacts queryBy(int uid,int cid,int state){
		Contacts contacts = null;
		String sql ="select _contacts.*,_contactscontent.* from _contacts,_contactscontent where _contacts.uid=? and _contacts.state=? and _contacts.cid=? and _contacts.cid=_contactscontent.cid;";
		Connection conn = JDBCUtils.getConnection();
		PreparedStatement ps = null;
		ResultSet rt = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, uid);
			ps.setInt(2, state);
			ps.setInt(3, cid);
			rt = ps.executeQuery();
			if(rt.next()){
				long updatetime = rt.getLong("updatetime");
				long deletetime = rt.getLong("deletetime");
				int ccid = rt.getInt("ccid");
				String name = rt.getString("name");
				String number = rt.getString("number");
				contacts = new Contacts(uid, cid, updatetime, deletetime, state, ccid,name,number);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("查询数据出错！");
		}finally{
			JDBCUtils.close(conn, ps, rt);
		}
		return contacts;
	}
	
	
	public Contacts add(Contacts contacts){
		if(null!=queryBy(contacts.getUid(),contacts.getName(),contacts.getPhoneNumber(),0)){
			throw new RuntimeException("信息已存在");
		}
		String sql = "insert into _contacts(uid,updatetime,deletetime) values(?,?,?);";
		Connection conn = JDBCUtils.getConnection();
		PreparedStatement st=null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(sql);
			st.setInt(1, contacts.getUid());
			st.setLong(2, System.currentTimeMillis());
			st.setLong(3, 0);
			st.execute();
			
			sql = "select last_insert_id();";
			rs = st.executeQuery(sql);
			rs.next();
			int cid = rs.getInt("last_insert_id()");
			
			sql="insert into _contactscontent(cid,name,number) values(?,?,?);";
			st = conn.prepareStatement(sql);
			st.setInt(1, cid);
			st.setString(2, contacts.getName());
			st.setString(3, contacts.getPhoneNumber());
			st.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("插入数据库出错");
		}finally{
			JDBCUtils.close(conn, st, rs);
		}
		return queryBy(contacts.getUid(),contacts.getName(),contacts.getPhoneNumber(),0);
	}
	
	public boolean delete(Contacts contacts){
		contacts = queryBy(contacts.getUid(),contacts.getName(),contacts.getPhoneNumber(),0);
		if(null==contacts){
			throw new RuntimeException("信息不存在");
		}
		String sql ="update _contacts set _contacts.state=1 where _contacts.cid="+contacts.getCid();
		Connection conn = JDBCUtils.getConnection();
		Statement st=null;
		try {
			st = conn.createStatement();
			st.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("执行删除操作异常！");
		}finally{
			JDBCUtils.close(conn, st, null);
		}
		return queryBy(contacts.getUid(),contacts.getName(),contacts.getPhoneNumber(),0)==null;
	}	
	
	
	public boolean delete(int uid,int cid){
		return delete(queryBy(uid,cid,0));
	}	
}
