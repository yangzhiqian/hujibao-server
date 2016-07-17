package edu.ncu.yang.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import com.google.gson.Gson;

import edu.ncu.yang.domain.User;
import edu.ncu.yang.engin.UserService;
import edu.ncu.yang.external.MySessionContext;
import edu.ncu.yang.utils.TokenProccessor;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		User user = new User();
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		user.setName(request.getParameter("name"));
		user.setPwd(request.getParameter("password"));

		UserService service = new UserService();
		User u = null;
		try {
			u = service.Login(user);
		} catch (RuntimeException e) {
			PrintWriter writer = response.getWriter();
			writer.write(toRes(false, "登录失败:" + e.getMessage(), null));
			writer.flush();
			return;
		}
		HttpSession session = request.getSession(true);
		session.setAttribute("user", u);
		session.setMaxInactiveInterval(60 * 60 * 24 * 7);// 设置7天过期时间
		u.setToken(session.getId());
		System.out.println("新的session:"+session.getId());
		try {
			PrintWriter writer = response.getWriter();
			writer.write(toRes(true, "登录成功", u));
			writer.flush();
		} catch (JSONException e) {
			e.printStackTrace();
			PrintWriter writer = response.getWriter();
			writer.write(toRes(false, "登录失败:" + e.getMessage(), null));
			writer.flush();
		}
	}

	/**
	 * 转换成对应的json格式，用户发送给客户端
	 * 
	 * @param isSuccess
	 * @param message
	 * @param token
	 * @param user
	 * @return
	 */
	private String toRes(boolean isSuccess, String message, User user) {
		JSONObject object = new JSONObject();
		object.put("succeed", isSuccess);
		object.put("message", message);
		if (user != null) {
			user.setPwd("");
		}
		object.put("user", new Gson().toJson(user));
		return object.toString();
	}

}
