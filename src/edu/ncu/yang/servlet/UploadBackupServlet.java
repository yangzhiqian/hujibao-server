package edu.ncu.yang.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import edu.ncu.yang.domain.Contacts;
import edu.ncu.yang.domain.Sms;
import edu.ncu.yang.domain.User;
import edu.ncu.yang.engin.Backup;
import edu.ncu.yang.engin.BackupFactory;
import edu.ncu.yang.external.MySessionContext;

@WebServlet("/UploadBackupServlet")
public class UploadBackupServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int SUCCEED = 0;
	private static final int ERROR_NO_PARAMENT = 1;
	private static final int ERROR_NO_SESSION = 2;
	private static final int ERROR = 3;

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		PrintWriter writer = response.getWriter();

		try {
			String message = request.getParameter("message");
			if (message == null) {
				writer.write(toRes(ERROR_NO_PARAMENT, false, "未找到参数"));
				writer.flush();
				return;
			}
			JSONObject object = JSONObject.fromObject(message);
			String token = object.optString("token", "");
			if ("".equals(token)) {
				writer.write(toRes(ERROR_NO_PARAMENT, false, "未找到令牌参数"));
				writer.flush();
				return;
			}
			HttpSession session = MySessionContext.getInstance().getSession(token);
			if (session == null) {
				writer.write(toRes(ERROR_NO_SESSION, false, "您还未登录！"));
				writer.flush();
				return;
			}
			User user = (User) session.getAttribute("user");
			if (user == null) {
				writer.write(toRes(ERROR_NO_SESSION, false, "令牌已过期！"));
				writer.flush();
				return;
			}
			int type = object.optInt("type", -1);
			JSONArray array = object.optJSONArray("data");
			boolean[] res = new boolean[array.size()];
			String[] strMsg = new String[array.size()];
			switch (type) {
			case 1:// 消息
				Backup<Sms> smsBackup = BackupFactory.createMessageBackup();
				Sms sms;
				for (int i = 0; i < array.size(); i++) {
					try {
						sms = Sms.toSms(array.getJSONObject(i).toString());
						sms.setUid(user.getUid());
						if (smsBackup.add(sms) != null){
							res[i] = true;
							strMsg[i] = "插入成功";
						}
					} catch (Exception e) {
						System.out.println(e.getMessage());
						res[i] = false;
						strMsg[i] = e.getMessage();
					}
				}
				break;
			case 2:// 联系人
				Backup<Contacts> contactsBackup = BackupFactory
						.createContactsBackup();
				Contacts contacts;
				for (int i = 0; i < array.size(); i++) {
					try {
						contacts = Contacts.toContacts(array.getJSONObject(i)
								.toString());
						contacts.setUid(user.getUid());
						if (contactsBackup.add(contacts) != null){
							res[i] = true;
							strMsg[i] = "插入成功";
						}
					} catch (Exception e) {
						System.out.println(e.getMessage());
						res[i] = false;
						strMsg[i] = e.getMessage();
					}
				}
				break;
			default:
				writer.write(toRes(ERROR_NO_PARAMENT, false, "类型错误"));
				writer.flush();
				return;
			}
			message = toJsonMessage(res,strMsg);
			writer.write(toRes(SUCCEED, true, message));
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
			writer.write(toRes(ERROR, false, e.getMessage()));
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

	private String toJsonMessage(boolean[] bs,String[] strMsg) {
		JSONObject object = new JSONObject();
		object.put("number", bs.length);
		object.put("succeed", new Gson().toJson(bs));
		object.put("msg", new Gson().toJson(strMsg));
		
		return object.toString();
	}

}
