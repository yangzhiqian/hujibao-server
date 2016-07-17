package edu.ncu.yang.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.ncu.yang.external.MySessionContext;

@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		
		PrintWriter writer = response.getWriter();
		HttpSession session = request.getSession(false);
		if(session==null){
			writer.write("request.getSession为空！");
			String sessionid = request.getParameter("sessionid");
			session = MySessionContext.getInstance().getSession(sessionid);
			if(session==null){
				writer.write("MySessionContext.getInstance().getSession为空！");
			}else{
				session.setMaxInactiveInterval(0);
				writer.write("使用MySessionContext注销！");
			}
		}else{
			session.setMaxInactiveInterval(0);
			writer.write("已经成功使用session注销");
		}
		writer.flush();
	}
}
