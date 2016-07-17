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
		String temp = Property.getImageTemp(request); // ��ʱĿ¼
		String loadpath = Property.getIconFolder(request); // �ϴ��ļ����Ŀ¼
		System.out.println(temp);
		System.out.println(loadpath);
		File f = new File(temp);
		if (!f.exists()) {
			f.mkdirs();
		}

		DiskFileUpload fu = new DiskFileUpload();
		fu.setSizeMax(10 * 1024 * 1024); // ���������û��ϴ��ļ���С,��λ:�ֽ�
		fu.setSizeThreshold(4096); // �������ֻ�������ڴ��д洢������,��λ:�ֽ�
		fu.setRepositoryPath(temp); // ����һ���ļ���С����getSizeThreshold()��ֵʱ���ݴ����Ӳ�̵�Ŀ¼

		// ��ʼ��ȡ�ϴ���Ϣ
		try {
			List fileItems = fu.parseRequest(request);
			Iterator iter = fileItems.iterator(); // ���δ���ÿ���ϴ����ļ�
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();// �������������ļ�������б���Ϣ
				if (!item.isFormField()) {//icon
					filePath = storeIcon(item,loadpath);
				} else {// ȡ�������ļ�������б���Ϣ
					// �����������ӦдΪ��(תΪUTF-8����)
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
				writer.write(toRes(true, "ע��ɹ�"));
				writer.flush();
			} else {
				PrintWriter writer = response.getWriter();
				writer.write(toRes(false, "ע��ʧ�ܣ�����null"));
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
		// path��ʾ���������ļ���·��
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
		String name = item.getName();// ��ȡ�ϴ��ļ���,����·��
		String typeName = name.substring(name.lastIndexOf("."));// ��ȫ·������ȡ�ļ�����
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
