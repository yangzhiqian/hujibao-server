package edu.ncu.yang.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

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

@WebServlet("/LoadBackupServlet")
public class LoadBackupServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int SUCCEED = 0;
	private static final int ERROR_PARAMTER = 1;
	private static final int ERROR_NOSESSION = 2;
	private static final int ERROR_SESSION_EXPIRED = 3;
	private static final int ERROR_UNKNOW = 0;

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// token type offset number
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		PrintWriter writer = response.getWriter();

		try {
			String token = request.getParameter("token");
			String strType = request.getParameter("type");
			String strOffset = request.getParameter("offset");
			String strNumber = request.getParameter("number");
			if (token == null || strType == null || strOffset == null
					|| strNumber == null) {
				writer.write(toRes(ERROR_PARAMTER, false, "参数错误"));
				writer.flush();
				return;
			}
			HttpSession session = MySessionContext.getInstance().getSession(token);
			if (session == null) {
				writer.write(toRes(ERROR_NOSESSION, false, "您还没有登录"));
				writer.flush();
				return;
			}
			User user = (User) session.getAttribute("user");
			if (user == null) {
				writer.write(toRes(ERROR_SESSION_EXPIRED, false, "登录凭证已过期"));
				writer.flush();
				return;
			}

			int type = Integer.parseInt(strType);
			int offset = Integer.parseInt(strOffset);
			int number = Integer.parseInt(strNumber);
			int code = SUCCEED;
			String message="";
			switch (type) {
			case 0://照片
				message = BackupFactory.createPictureBackup().queryToJson(user.getUid(), offset, number);
				break;
			case 1://消息
				message = BackupFactory.createMessageBackup().queryToJson(user.getUid(), offset, number);
				break;
			case 2://联系人
				message = BackupFactory.createContactsBackup().queryToJson(user.getUid(), offset, number);
				break;
			default:
				code = ERROR_PARAMTER;
				message="参数错误";
				break;
			}
			System.out.println(message);
			writer.write(toRes(code, SUCCEED==code, message));
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
			writer.write(toRes(ERROR_UNKNOW, false, e.getMessage()));
			writer.flush();
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
