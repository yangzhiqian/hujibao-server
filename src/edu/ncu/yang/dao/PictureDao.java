package edu.ncu.yang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;

import edu.ncu.yang.domain.Picture;
import edu.ncu.yang.utils.JDBCUtils;

public class PictureDao {
	public List<Picture> query(int uid, int offset, int nums, int state) {
		List<Picture> ps = new ArrayList<Picture>();
		Connection conn = JDBCUtils.getConnection();
		String sql = "select _picture.*,_picturecontent.* from _picture,_picturecontent where _picture.uid=? and _picture.pid=_picturecontent.pid and _picture.state=? order by updatetime desc limit ?,?;";
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
				int pid = rt.getInt("pid");
				long updatetime = rt.getLong("updatetime");
				long deletetime = rt.getLong("deletetime");
				int s = rt.getInt("state");
				int ppid = rt.getInt("ppid");
				long lastmodifytime = rt.getLong("lastmodifytime");
				int picturesize = rt.getInt("picturesize");
				String filename = rt.getString("filename");
				ps.add(new Picture(u, pid, updatetime, deletetime, s, ppid,
						lastmodifytime, picturesize, "",filename));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("查询数据出错！");
		} finally {
			JDBCUtils.close(conn, st, rt);
		}
		return ps;
	}

	public Picture queryByName(int uid, String name,int state) {
		Connection conn = JDBCUtils.getConnection();
		String sql = "select _picture.*,_picturecontent.* from _picture,_picturecontent where _picture.uid=? and _picture.state=? and _picture.pid=_picturecontent.pid and _picturecontent.filename=?";
		PreparedStatement st = null;
		ResultSet rt = null;
		try {
			st = conn.prepareStatement(sql);
			st.setInt(1, uid);
			st.setInt(2, state);
			st.setString(3, name);
			rt = st.executeQuery();
			if (rt.next()) {
				int u = rt.getInt("uid");
				int pid = rt.getInt("pid");
				long updatetime = rt.getLong("updatetime");
				long deletetime = rt.getLong("deletetime");
				int s = rt.getInt("state");
				int ppid = rt.getInt("ppid");
				long lastmodifytime = rt.getLong("lastmodifytime");
				int picturesize = rt.getInt("picturesize");
				String filename = rt.getString("filename");
				return new Picture(u, pid, updatetime, deletetime, s, ppid,
						lastmodifytime, picturesize, "",filename);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("查询数据出错！");
		} finally {
			JDBCUtils.close(conn, st, rt);
		}
		return null;
	}
	
	public Picture queryByName(int uid,int pid,int state){
		Connection conn = JDBCUtils.getConnection();
		String sql = "select _picture.*,_picturecontent.* from _picture,_picturecontent where _picture.uid=? and _picture.state=? and _picture.pid=_picturecontent.pid and _picture.pid=?";
		PreparedStatement st = null;
		ResultSet rt = null;
		try {
			st = conn.prepareStatement(sql);
			st.setInt(1, uid);
			st.setInt(2, state);
			st.setInt(3, pid);
			rt = st.executeQuery();
			if (rt.next()) {
				int u = rt.getInt("uid");
				pid = rt.getInt("pid");
				long updatetime = rt.getLong("updatetime");
				long deletetime = rt.getLong("deletetime");
				int s = rt.getInt("state");
				int ppid = rt.getInt("ppid");
				long lastmodifytime = rt.getLong("lastmodifytime");
				int picturesize = rt.getInt("picturesize");
				String filename = rt.getString("filename");
				return new Picture(u, pid, updatetime, deletetime, s, ppid,
						lastmodifytime, picturesize, "",filename);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("查询数据出错！");
		} finally {
			JDBCUtils.close(conn, st, rt);
		}
		return null;
	}

	public Picture add(Picture pic) {
		// 判断该用户下是否已经存在本次上传的图片；
		if (!(null == queryByName(pic.getUid(), pic.getName(),0))) {
			throw new RuntimeException("该图片已经存在！");
		}

		Connection conn = JDBCUtils.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "insert into _picture(uid,updatetime,deletetime) values (?,?,?);";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, pic.getUid());
			ps.setLong(2, System.currentTimeMillis());
			ps.setLong(3, 0);
			ps.execute();
			sql = "select last_insert_id();";
			rs = ps.executeQuery(sql);
			rs.next();
			int pid = rs.getInt("last_insert_id()");
			sql = "insert into _picturecontent(pid,lastmodifytime, picturesize, filename) values(?,?,?,?);";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, pid);
			ps.setLong(2, pic.getLastModified());
			ps.setInt(3, (int)pic.getSize());
			ps.setString(4, pic.getName());
			ps.execute();
			return queryByName(pic.getUid(), pic.getName(),0);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("数据库添加数据错误！");
		} finally {
			JDBCUtils.close(conn, ps, rs);
		}
	}

	public boolean delete(int uid, String name) {
		// 判断该用户下是否已经存在该的图片；
		Picture pic = queryByName(uid, name,0);
		if (null == pic) {
			throw new RuntimeException("该图片已经不存在！");
		}
		// update操作原始表不能出现在where 后面第一层的子查询当中,所以下面的写法不通过
		// String sql =
		// "update _picture set state=1 where uid=? and pid in (select pid from _picture,_picturecontent where _picture.pid=_picturecontent.pid and filename=?);";

		String sql = "update _picture set state=1 where pid=" + pic.getPid();

		Connection conn = JDBCUtils.getConnection();
		Statement st = null;
		try {
			st = conn.createStatement();
			st.execute(sql);
			if(queryByName(uid, name,0)==null){
				return true;
			}else{
				throw new RuntimeException("删除失败！");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("数据库删除数据失败！");
		} finally {
			JDBCUtils.close(conn, st, null);

		}
	}
	
	public boolean delete(int uid,int pid){
		return delete(uid, queryByName(uid, pid, 0).getName());
	}
}
