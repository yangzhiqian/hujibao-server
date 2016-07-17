package edu.ncu.yang.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;

import net.sf.json.JSONObject;
import edu.ncu.yang.domain.User;
import edu.ncu.yang.external.MySessionContext;
import edu.ncu.yang.utils.Property;

/**
 * Servlet implementation class DownloadIconServlet
 */

@WebServlet("/DownloadIconServlet")
public class DownloadIconServlet extends HttpServlet {
	public static final int CODE_SUCCEED = 0;
	public static final int CODE_ERROR_PARAMETER = 1;
	public static final int CODE_ERROR_NOIMG = 2;
	public static final int CODE_ERROR_NOSESSION = 3;
	public static final int CODE_ERROR_SESSION_EXPIRED = 4;
	public static final int CODE_ERROR_IMGEXIST = 5;
	public static final int CODE_ERROR_STORE = 6;
	public static final int CODE_ERROR_DATABASE_SOTRE = 7;

	private String toRes(int code, boolean isSuccess, String message) {
		JSONObject object = new JSONObject();
		object.put("code", code);
		object.put("succeed", isSuccess);
		object.put("message", message);
		return object.toString();
	}
	
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		
		String token = request.getParameter("token");
		
		if(token==null){
			PrintWriter writer = response.getWriter();
			writer.write(toRes(CODE_ERROR_PARAMETER, false, "参数错误"));
			writer.flush();
			return;
		}
		System.out.println(token);
		HttpSession session = MySessionContext.getInstance().getSession(token);
		if(session==null){
			PrintWriter writer = response.getWriter();
			writer.write(toRes(CODE_ERROR_NOSESSION, false, "令牌已过期！"));
			writer.flush();
			return;
		}
		User user = (User) session.getAttribute("user");
		String fileName = user.getIconUrl();
		String iconFolder = Property.getIconFolder(request);
		File file = new File(iconFolder,fileName);
		System.out.println(fileName);
		if(!file.exists()){
			PrintWriter writer = response.getWriter();
			writer.write(toRes(CODE_ERROR_NOIMG, false, "icon未找到"));
			writer.flush();
			return;
		}
		IOUtils.copy(new FileInputStream(file), response.getOutputStream());	
	}

}
