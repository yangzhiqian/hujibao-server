package edu.ncu.yang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import edu.ncu.yang.domain.Sms;
import edu.ncu.yang.utils.JDBCUtils;

public class SmsDao {
	public List<Sms> query(int uid,int offset,int nums,int state){
		List<Sms> infos = new ArrayList<Sms>();
		Connection conn = JDBCUtils.getConnection();
		String sql = "select _message.*,_messagecontent.* from _message,_messagecontent where _message.uid=? and _message.mid=_messagecontent.mid and _message.state=? order by updatetime desc limit ?,?;";
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
				int mid = rt.getInt("mid");
				long updatetime = rt.getLong("updatetime");
				long deletetime = rt.getLong("deletetime");
				int s = rt.getInt("state");
				int mmid = rt.getInt("mmid");
				String address = rt.getString("address");
				long messagetime  = rt.getLong("messagetime");
				int messagetype = rt.getInt("messagetype");
				String messagebody = rt.getString("messagebody");
			    infos.add(new Sms(u,mid,updatetime,deletetime,s,mmid,address,messagetime,messagetype,messagebody));	
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("查询数据出错！");
		} finally {
			JDBCUtils.close(conn, st, rt);
		}
		return infos;
	}
	
	public Sms queryBy(int uid,String address,long messageTime,String body,int state){
		Sms message = null;
		String sql ="select _message.*,_messagecontent.* from _message,_messagecontent where _message.uid=? and _message.state=? and _message.mid=_messagecontent.mid and _messagecontent.address=? and _messagecontent.messagetime=? and _messagecontent.messagebody=?;";
		Connection conn = JDBCUtils.getConnection();
		PreparedStatement ps = null;
		ResultSet rt = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, uid);
			ps.setInt(2, state);
			ps.setString(3, address);
			ps.setLong(4, messageTime);
			ps.setString(5, body);
			rt = ps.executeQuery();
			if(rt.next()){
				int u = rt.getInt("uid");
				int mid = rt.getInt("mid");
				long updatetime = rt.getLong("updatetime");
				long deletetime = rt.getLong("deletetime");
				int s = rt.getInt("state");
				int mmid = rt.getInt("mmid");
				address = rt.getString("address");
				long messagetime  = rt.getLong("messagetime");
				int messagetype = rt.getInt("messagetype");
				String messagebody = rt.getString("messagebody");
				message = new Sms(u,mid,updatetime,deletetime,s,mmid,address,messagetime,messagetype,messagebody);	
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("查询数据出错！");
		}finally{
			JDBCUtils.close(conn, ps, rt);
		}
		return message;
	}
	
	public Sms queryBy(int uid,int mid,int state){
		Sms message = null;
		String sql ="select _message.*,_messagecontent.* from _message,_messagecontent where _message.uid=? and _message.state=? and _message.mid=_messagecontent.mid and _message.mid=?;";
		Connection conn = JDBCUtils.getConnection();
		PreparedStatement ps = null;
		ResultSet rt = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, uid);
			ps.setInt(2, state);
			ps.setInt(3, mid);
			rt = ps.executeQuery();
			if(rt.next()){
				int u = rt.getInt("uid");
				mid = rt.getInt("mid");
				long updatetime = rt.getLong("updatetime");
				long deletetime = rt.getLong("deletetime");
				int s = rt.getInt("state");
				int mmid = rt.getInt("mmid");
				String address = rt.getString("address");
				long messagetime  = rt.getLong("messagetime");
				int messagetype = rt.getInt("messagetype");
				String messagebody = rt.getString("messagebody");
				message = new Sms(u,mid,updatetime,deletetime,s,mmid,address,messagetime,messagetype,messagebody);	
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("查询数据出错！");
		}finally{
			JDBCUtils.close(conn, ps, rt);
		}
		return message;
	}
	
	
	public Sms add(Sms message){
		if(null!=queryBy(message.getUid(),message.getAddress(),message.getDate(),message.getBody(),0)){
			throw new RuntimeException("信息已存在");
		}
		String sql = "insert into _message(uid,updatetime,deletetime) values(?,?,?);";
		Connection conn = JDBCUtils.getConnection();
		PreparedStatement st=null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(sql);
			st.setInt(1, message.getUid());
			st.setLong(2, System.currentTimeMillis());
			st.setLong(3, 0);
			st.execute();
			
			sql = "select last_insert_id();";
			rs = st.executeQuery(sql);
			rs.next();
			int mid = rs.getInt("last_insert_id()");
			
			sql="insert into _messagecontent(mid,address,messagetime,messagetype,messagebody) values(?,?,?,?,?);";
			st = conn.prepareStatement(sql);
			st.setInt(1, mid);
			st.setString(2, message.getAddress());
			st.setLong(3, message.getDate());
			st.setInt(4,message.getType());
			st.setString(5, message.getBody());
			st.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("插入数据库出错");
		}finally{
			JDBCUtils.close(conn, st, rs);
		}
		return queryBy(message.getUid(),message.getAddress(),message.getDate(),message.getBody(),0);
	}
	
	public boolean delete(Sms message){
		message = queryBy(message.getUid(),message.getAddress(),message.getDate(),message.getBody(),0);
		if(null==message){
			throw new RuntimeException("信息不存在");
		}
		String sql ="update _message set _message.state=1 where _message.mid="+message.getMid();
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
		return queryBy(message.getUid(),message.getAddress(),message.getDate(),message.getBody(),0)==null;
	}
	
	public boolean delete(int uid,int mid){
		return delete(queryBy(uid, mid, 0));
	}
	
}
