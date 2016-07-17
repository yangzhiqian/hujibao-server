package edu.ncu.yang.engin;

import edu.ncu.yang.dao.UserDao;
import edu.ncu.yang.domain.User;

public class UserService {
	private UserDao dao = new UserDao();
	
	public User Regist(User user){
		return dao.add(user);
	}
	public User Login(User user){
		User u =  dao.queryUserByName(user.getName());
		if(user.getPwd().equals(u.getPwd())){
			return u;
		}else{
			throw new RuntimeException("用户名密码不正确");
		}
	}
	public User activityUser(String name,int state){
		 return dao.setUserActivitivable(name, state);
	}
	public User modifyUserInfo(String name,User user){
		return dao.updateUserInfo(name, user);
	}
}
