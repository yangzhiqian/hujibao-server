package edu.ncu.yang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.ncu.yang.domain.User;
import edu.ncu.yang.utils.JDBCUtils;

public class UserDao {
	public static final String DEFAULT_ICONPATH = "";
	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_ADMIN = 1;

	public static final int STATE_ACTIVITY = 0;
	public static final int STATE_INACTIVITY = 1;
	public static final int STATE_DELETE = 2;

	public User add(User user) {
		Connection conn = null;
		PreparedStatement st = null;
		User tu = null;
		try {tu = queryUserByName(user.getName());} catch (RuntimeException e) {
			tu = null;
		}
		if (tu != null) {
			throw new RuntimeException("用户名已存在");
		}
		try {
			// 添加用户表信息
			conn = JDBCUtils.getConnection();
			String sql = "insert into _user(name,pwd,icon,phone,email,usertype,state) values(?,?,?,?,?,?,?);";
			st = conn.prepareStatement(sql);
			st.setString(1, user.getName());
			st.setString(2, user.getPwd());
			st.setString(3, user.getIconUrl());
			st.setString(4, user.getPhone());
			st.setString(5, user.getEmail());
			st.setInt(6, TYPE_NORMAL);
			st.setInt(7, STATE_ACTIVITY);
			st.execute();
			return queryUserByName(user.getName());
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("注册失败:" + e.getMessage());
		} finally {
			JDBCUtils.close(conn, st, null);
		}
	}

	public User setUserActivitivable(String name, int state) {
		User user = queryUserByName(name);
		if (user.getState() == state) {
			throw new RuntimeException("不能切换到当前的状态");
		}

		Connection conn = null;
		PreparedStatement st = null;
		try {
			conn = JDBCUtils.getConnection();
			String sql = "update _user set state =? where name =?";
			st = conn.prepareStatement(sql);
			st.setInt(1, state);
			st.setString(2, name);
			st.executeUpdate();
			return queryUserByName(name);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("切换用户状态失败:" + e.getMessage());
		} finally {
			JDBCUtils.close(conn, st, null);
		}
	}

	public User updateUserInfo(String name, User user) {
		User oldUser = queryUserByName(name);
		if (oldUser == null) {
			throw new RuntimeException("用户未找到");
		}
		if (isNull(user.getName())) {
			// 不需要修改用户名，直接使用原来的用户名
			user.setName(name);
		} else {
			if (!user.getName().equals(name)) {
				// 要修改name字段
				if (queryUserByName(user.getName()) != null) {
					// 用户名已经存在
					throw new RuntimeException("用户已经存在");
				}
			}
		}

		if (isNull(user.getPwd())) {
			// 不需要修改密码，直接使用原来的密码
			user.setPwd(oldUser.getPwd());
		} else {
			if (user.getPwd().length() < 6) {
				// 密码太短
				throw new RuntimeException("密码不能小于6个字符");
			}
		}

		if (isNull(user.getIconUrl())) {
			user.setIconUrl(oldUser.getIconUrl());
		}
		if (isNull(user.getPhone())) {
			user.setPhone(oldUser.getPhone());
		}
		if (isNull(user.getEmail())) {
			user.setEmail(oldUser.getEmail());
		}

		Connection conn = null;
		PreparedStatement st = null;
		try {
			conn = JDBCUtils.getConnection();
			String sql = "update _user set name=?,pwd=?,icon=?,phone=?,email=? where name=?;";
			st = conn.prepareStatement(sql);
			st.setString(1, user.getName());
			st.setString(2, user.getPwd());
			st.setString(3, user.getIconUrl());
			st.setString(4, user.getPhone());
			st.setString(5, user.getEmail());
			st.setString(6, name);
			st.executeUpdate();
			return queryUserByName(user.getName());
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("修改数据异常:" + e.getMessage());
		} finally {
			JDBCUtils.close(conn, st, null);
		}
	}

	public User queryUserByName(String name) {
		User user = null;
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet cursor = null;
		try {
			conn = JDBCUtils.getConnection();
			String sql = "select * from _user,_memory where name=? and _user.uid=_memory.uid;";
			st = conn.prepareStatement(sql);
			st.setString(1, name);
			cursor = st.executeQuery();
			if (cursor.next()) {
				user = new User();
				user.setUid(cursor.getInt("_user.uid"));
				user.setName(cursor.getString("name"));
				user.setPwd(cursor.getString("pwd"));
				user.setIconUrl(cursor.getString("icon"));
				user.setPhone(cursor.getString("phone"));
				user.setEmail(cursor.getString("email"));
				user.setUserType(cursor.getInt("usertype"));
				user.setState(cursor.getInt("state"));

				user.setTotal(cursor.getLong("total"));
				user.setUsed(cursor.getLong("used"));
				return user;
			} else {
				throw new RuntimeException("未找到该用户");

			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("获取信息异常:" + e.toString());
		} finally {
			JDBCUtils.close(conn, st, cursor);
		}
	}

	private boolean isNull(String str) {
		if (str == null || "".equals(str)) {
			return true;
		}
		return false;
	}
}
