package edu.ncu.yang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.ncu.yang.utils.JDBCUtils;

public class MemoryDao {
	public boolean enoughAdd(int uid,long size){
		Connection conn = JDBCUtils.getConnection();
		String sql = "select * from _memory where uid = "+uid;
		Statement st = null;
		ResultSet set = null;
		try {
			st = conn.createStatement();
			set = st.executeQuery(sql);
			if(set.next()){
				long total = set.getLong("total");
				long used = set.getLong("used");
				if(used+size<total){
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			JDBCUtils.close(conn, st, set);
		}
		throw new RuntimeException("�ƿռ䲻��");
	}
	public void add(int uid,long size){
		if(enoughAdd(uid,size)){
			//���㹻�Ĵ洢�ռ�
			Connection conn = JDBCUtils.getConnection();
			String sql = "update _memory set used = used+? where uid =?";
			PreparedStatement st = null;
			ResultSet set = null;
			try {
				st = conn.prepareStatement(sql);
				st.setLong(1, size);
				st.setInt(2, uid);
				st.execute();
				return;
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				JDBCUtils.close(conn, st, set);
			}
		}
		throw new RuntimeException("ִ������ʹ����������");
		
	}
	
	public void delete(int uid,long size){
		//���㹻�Ĵ洢�ռ�
		Connection conn = JDBCUtils.getConnection();
		String sql = "update _memory set used = used-? where uid =?";
		PreparedStatement st = null;
		ResultSet set = null;
		try {
			st = conn.prepareStatement(sql);
			st.setLong(1, size);
			st.setInt(2, uid);
			st.execute();
			return;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			JDBCUtils.close(conn, st, set);
		}
	}
}
