package edu.ncu.yang.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;

import edu.ncu.yang.domain.User;
import edu.ncu.yang.engin.UserService;
import edu.ncu.yang.utils.Property;

/**
 * Servlet implementation class RegistServlet
 */
@WebServlet("/RegistServlet")
public class RegistServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");

		String filePath = "";
		String fieldvalue="";
		String temp = Property.getImageTemp(request); // 临时目录
		String loadpath = Property.getIconFolder(request); // 上传文件存放目录
		System.out.println(temp);
		System.out.println(loadpath);
		File f = new File(temp);
		if (!f.exists()) {
			f.mkdirs();
		}

		DiskFileUpload fu = new DiskFileUpload();
		fu.setSizeMax(10 * 1024 * 1024); // 设置允许用户上传文件大小,单位:字节
		fu.setSizeThreshold(4096); // 设置最多只允许在内存中存储的数据,单位:字节
		fu.setRepositoryPath(temp); // 设置一旦文件大小超过getSizeThreshold()的值时数据存放在硬盘的目录

		// 开始读取上传信息
		try {
			List fileItems = fu.parseRequest(request);
			Iterator iter = fileItems.iterator(); // 依次处理每个上传的文件
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();// 忽略其他不是文件域的所有表单信息
				if (!item.isFormField()) {//icon
					filePath = storeIcon(item,loadpath);
				} else {// 取出不是文件域的所有表单信息
					// 如果包含中文应写为：(转为UTF-8编码)
					fieldvalue = new String(item.getString().getBytes(),
							"UTF-8");
					System.out.println( fieldvalue);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			PrintWriter writer = response.getWriter();
			writer.write(toRes(false, e.getMessage()));
			writer.flush();
			return;
		}
		
		
		User user = User.toUser(fieldvalue);
		user.setIconUrl(filePath);
		
		UserService service = new UserService();
		try {
			if (service.Regist(user) != null) {
				PrintWriter writer = response.getWriter();
				writer.write(toRes(true, "注册成功"));
				writer.flush();
			} else {
				PrintWriter writer = response.getWriter();
				writer.write(toRes(false, "注册失败，返回null"));
				writer.flush();
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
			PrintWriter writer = response.getWriter();
			writer.write(toRes(false, e.getMessage()));
			writer.flush();
		}
	}

	private String toRes(boolean isSuccess, String message) {
		JSONObject object = new JSONObject();
		object.put("succeed", isSuccess);
		object.put("message", message);
		return object.toString();
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
	
	private String storeIcon(FileItem item,String loadpath) throws IOException{
		String name = item.getName();// 获取上传文件名,包括路径
		String typeName = name.substring(name.lastIndexOf("."));// 从全路径中提取文件类型
		System.out.println(name);
		System.out.println(typeName);
		long size = item.getSize();
		if ((name == null || name.equals("")) && size == 0)
			return null;

		String fileName = UUID.randomUUID().toString() + typeName;
		File file = createFile(loadpath, fileName);
		InputStream is = item.getInputStream();
		FileOutputStream fos = new FileOutputStream(file);
		IOUtils.copy(is, fos);
		fos.flush();
		fos.close();
		System.out.println(file.getAbsolutePath());
		System.out.println(file.length());

		return fileName;
	}
	
}
