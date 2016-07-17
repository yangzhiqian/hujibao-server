package edu.ncu.yang.utils;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class JDBCUtils {
	private static String driver;
	private static String url;
	private static String user;
	private static String password;
	static{
		try {
			Properties prop  = new Properties();
			InputStream is = JDBCUtils.class.getResourceAsStream("/db.properties");
			prop.load(is);
			is.close();
			driver = prop.getProperty("driver");
			url = prop.getProperty("url");
			user = prop.getProperty("user");
			password = prop.getProperty("password");
			Class.forName(driver);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static Connection getConnection(){
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("连接数据库异常!");
		}
		
		return conn;
	}
	
	public  static void  close(Connection conn , Statement st , ResultSet rs){
		
		try {
			if(rs!=null){
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				if(st!=null){
				st.close();	
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				try {
					if(conn!=null){
						conn.close();	
						}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}
	
	
	public static void main(String[] args) {
		System.out.println(getConnection());
	}
}
