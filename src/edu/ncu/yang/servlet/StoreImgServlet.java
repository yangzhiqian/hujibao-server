package edu.ncu.yang.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import edu.ncu.yang.domain.Picture;
import edu.ncu.yang.domain.User;
import edu.ncu.yang.engin.Backup;
import edu.ncu.yang.engin.BackupFactory;
import edu.ncu.yang.engin.PictureService;
import edu.ncu.yang.external.MySessionContext;
import edu.ncu.yang.utils.Property;

@WebServlet("/StoreImgServlet")
public class StoreImgServlet extends HttpServlet {
	public static final int CODE_SUCCEED = 0;
	public static final int CODE_ERROR_PARAMETER = 1;
	public static final int CODE_ERROR_NOIMG = 2;
	public static final int CODE_ERROR_NOSESSION = 3;
	public static final int CODE_ERROR_SESSION_EXPIRED = 4;
	public static final int CODE_ERROR_IMGEXIST = 5;
	public static final int CODE_ERROR_STORE = 6;
	public static final int CODE_ERROR_DATABASE_SOTRE = 7;

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		String temp = Property.getImageTemp(request);
		String message = null;
		FileItem imgFileItem = null;

		File f = new File(temp);
		if (!f.exists()) {
			f.mkdirs();
		}

		DiskFileUpload fu = new DiskFileUpload();
		fu.setSizeMax(10 * 1024 * 1024); // 设置允许用户上传文件大小,单位:字节
		fu.setSizeThreshold(4096); // 设置最多只允许在内存中存储的数据,单位:字节
		fu.setRepositoryPath(temp); // 设置一旦文件大小超过getSizeThreshold()的值时数据存放在硬盘的目录
		try {
			List list = fu.parseRequest(request);
			Iterator it = list.iterator();
			while (it.hasNext()) {
				FileItem next = (FileItem) it.next();
				if (next.isFormField()) {// 参数
					message = new String(next.getString().getBytes(), "UTF-8");
				} else {// 图片
					imgFileItem = next;
				}
			}
		} catch (FileUploadException e) {
			e.printStackTrace();
			PrintWriter writer = response.getWriter();
			writer.write(toRes(CODE_ERROR_PARAMETER, false, "上传出现异常！"));
			writer.flush();
			return;
		}
		// 没有照片
		if (imgFileItem == null) {
			PrintWriter writer = response.getWriter();
			writer.write(toRes(CODE_ERROR_NOIMG, false, "服务器未收到图片信息！"));
			writer.flush();
			return;
		}
		// 没有登录凭证
		if (message == null) {
			PrintWriter writer = response.getWriter();
			writer.write(toRes(CODE_ERROR_NOSESSION, false, "服务器未收到请求参数！"));
			writer.flush();
			return;
		}

		JSONObject object = JSONObject.fromObject(message);
		String token = object.optString("token", "");
		String fileName = object.optString("filename", "");
		long lastUpdate = object.optLong("lastupdate",
				System.currentTimeMillis());

		if ("".endsWith(token)) {
			PrintWriter writer = response.getWriter();
			writer.write(toRes(CODE_ERROR_NOSESSION, false, "服务器未收到令牌参数！"));
			writer.flush();
			return;
		}

		if ("".endsWith(fileName)) {
			PrintWriter writer = response.getWriter();
			writer.write(toRes(CODE_ERROR_NOSESSION, false, "服务器未收到文件名参数！"));
			writer.flush();
			return;
		}

		HttpSession session = MySessionContext.getInstance().getSession(token);
		// 登录凭证已过期
		if (session == null) {
			PrintWriter writer = response.getWriter();
			writer.write(toRes(CODE_ERROR_SESSION_EXPIRED, false, "登录凭证已过期！"));
			writer.flush();
			return;
		}
		User user = (User) session.getAttribute("user");
		Backup<Picture> backup = BackupFactory.createPictureBackup();
		Picture p = new Picture();
		p.setUid(user.getUid());
		p.setName(fileName);
		if (backup.isExist(p)) {
			PrintWriter writer = response.getWriter();
			writer.write(toRes(CODE_ERROR_IMGEXIST, false, "图片已存在！"));
			writer.flush();
			return;
		}
		String loadpath = Property.getImageFolder(request) + "/"
				+ user.getName();
		f = new File(loadpath);
		if (!f.exists()) {
			f.mkdirs();
		}
		try {
			storeIcon(imgFileItem, loadpath, fileName);
		} catch (Exception e) {
			PrintWriter writer = response.getWriter();
			writer.write(toRes(CODE_ERROR_STORE, false, "图片存储出错！"));
			writer.flush();
			return;
		}
		f = new File(loadpath, fileName);
		Picture pic = new Picture(user.getUid(), 0, System.currentTimeMillis(),
				0, 0, 0, lastUpdate, (int) f.length(), "", fileName);
		try {
			backup.add(pic);
		} catch (Exception e) {
			PrintWriter writer = response.getWriter();
			writer.write(toRes(CODE_ERROR_DATABASE_SOTRE, false, e.getMessage()));
			writer.flush();
		}
		PrintWriter writer = response.getWriter();
		writer.write(toRes(CODE_SUCCEED, true, "上传成功"));
		writer.flush();
	}

	private String storeIcon(FileItem item, String loadpath, String fileName)
			throws IOException {
		File file = createFile(loadpath, fileName);
		InputStream is = item.getInputStream();
		FileOutputStream fos = new FileOutputStream(file);
		IOUtils.copy(is, fos);
		fos.flush();
		fos.close();
		return fileName;
	}

	public File createFile(String path, String fileName) {
		// path表示你所创建文件的路径
		File f = new File(path);
		if (!f.exists()) {
			f.mkdirs();
		}
		File file = new File(f, fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	private String toRes(int code, boolean isSuccess, String message) {
		JSONObject object = new JSONObject();
		object.put("code", code);
		object.put("succeed", isSuccess);
		object.put("message", message);
		return object.toString();
	}
}
