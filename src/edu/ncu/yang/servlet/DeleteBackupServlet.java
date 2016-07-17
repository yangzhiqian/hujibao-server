package edu.ncu.yang.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;
import edu.ncu.yang.domain.Picture;
import edu.ncu.yang.domain.User;
import edu.ncu.yang.engin.Backup;
import edu.ncu.yang.engin.BackupFactory;
import edu.ncu.yang.external.MySessionContext;

/**
 * Servlet implementation class DeleteBackupServlet
 */
@WebServlet("/DeleteBackupServlet")
public class DeleteBackupServlet extends HttpServlet {
	public static final int SUCCEED = 0;
	public static final int ERROR_NO_TOKEN = 1;
	public static final int ERROR_NO_SESSION = 2;
	public static final int ERROR_PARAM = 3;
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		PrintWriter writer = response.getWriter();

		try {
			// token type id
			String token = request.getParameter("token");
			if (token == null) {
				writer.write(toRes(ERROR_NO_TOKEN, false, "服务器未收到令牌参数！"));
				writer.flush();
				return;
			}
			HttpSession session = MySessionContext.getInstance().getSession(token);
			if (session == null) {
				writer.write(toRes(ERROR_NO_SESSION, false, "令牌已过期！"));
				writer.flush();
				return;
			}
			User user = (User) session.getAttribute("user");

			int id = Integer.parseInt(request.getParameter("id"));
			int type = Integer.parseInt(request.getParameter("type"));
			switch (type) {
			case 0:
				BackupFactory.createPictureBackup().delete(user.getUid(), id);
				break;
			case 1:
				BackupFactory.createMessageBackup().delete(user.getUid(), id);
				break;
			case 2:
				BackupFactory.createContactsBackup().delete(user.getUid(), id);
				break;
			default:
				writer.write(toRes(ERROR_PARAM, false, "类型错误！"));
				writer.flush();
				return;
			}
			writer.write(toRes(SUCCEED, true, "删除成功！"));
			writer.flush();
			return;
		} catch (Exception e) {
			e.printStackTrace();
			writer.write(toRes(ERROR_PARAM, false, e.getMessage()));
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
