package edu.ncu.yang.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;

import net.sf.json.JSONObject;

import com.google.gson.JsonObject;

import edu.ncu.yang.domain.Picture;
import edu.ncu.yang.domain.User;
import edu.ncu.yang.engin.Backup;
import edu.ncu.yang.engin.BackupFactory;
import edu.ncu.yang.engin.PictureService;
import edu.ncu.yang.external.MySessionContext;
import edu.ncu.yang.utils.BitmapUtil;
import edu.ncu.yang.utils.Property;

@WebServlet("/DownloadImageServlet")
public class DownloadImageServlet extends HttpServlet {
	public static final int SUCCEED = 0;
	public static final int ERROR_NO_TOKEN = 1;
	public static final int ERROR_NO_SESSION = 2;
	public static final int ERROR_PARAM = 3;

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");

		String token = request.getParameter("token");
		if (token == null) {
			PrintWriter writer = response.getWriter();
			writer.write(toRes(ERROR_NO_TOKEN, false, "服务器未收到令牌参数！"));
			writer.flush();
			return;
		}
		HttpSession session = MySessionContext.getInstance().getSession(token);
		if (session == null) {
			PrintWriter writer = response.getWriter();
			writer.write(toRes(ERROR_NO_SESSION, false, "令牌已过期！"));
			writer.flush();
			return;
		}
		User user = (User) session.getAttribute("user");
		String fileName = request.getParameter("filename");
		if (fileName == null) {
			PrintWriter writer = response.getWriter();
			writer.write(toRes(ERROR_PARAM, false, "参数错误！"));
			writer.flush();
			return;
		}
		Backup<Picture> backup = BackupFactory.createPictureBackup();
		Picture p = new Picture();
		p.setUid(user.getUid());
		p.setName(fileName);
		if (!backup.isExist(p)) {
			PrintWriter writer = response.getWriter();
			writer.write(toRes(ERROR_PARAM, false, "未查询到该图片！"));
			writer.flush();
			return;
		}
		try {
			int type = Integer.parseInt(request.getParameter("type"));
			int width=-1;
			int height=-1;
			File oldFile = new File(Property.getImageFolder(request)+"/"+user.getName(),fileName);
			File newFile = new File(Property.getImageTemp(request),type+"_"+fileName);
			switch(type){
			case 0://小
				width=100;
				height=100;
				break;
			case 1://中
				width=400;
				height=600;
				break;
			case 2://原图
				break;
			default:
				PrintWriter writer = response.getWriter();
				writer.write(toRes(ERROR_PARAM, false, "参数错误！"));
				writer.flush();
				return;
			}
			if(width==-1&&height==-1){
				newFile = oldFile;
			}else{
				BitmapUtil.toSmallImage(oldFile, newFile, width, height, 1);
			}
			response.setContentLength((int)(newFile.length()));
			IOUtils.copy(new FileInputStream(newFile), response.getOutputStream());	
//			PrintWriter writer = response.getWriter();
//			writer.write(toRes(SUCCEED, true, "图片"+fileName+"获取成功！"));
//			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
			PrintWriter writer = response.getWriter();
			writer.write(toRes(ERROR_PARAM, false, "参数错误！"));
			writer.flush();
			return;
		}
	}

	private String toRes(int code, boolean isSuccess, String message) {
		JSONObject object = new JSONObject();
		object.put("code", code);
		object.put("succeed", isSuccess);
		object.put("message", message);
		return object.toString();
	}

}
