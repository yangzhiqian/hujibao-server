package edu.ncu.yang.utils;

import javax.servlet.http.HttpServletRequest;

public class Property {
	
	public static String getIconFolder(HttpServletRequest request){
		return request.getSession().getServletContext()
				.getRealPath("/")
				+ "WEB-INF/icon";
	}
	
	public static String getImageTemp(HttpServletRequest request){
		return request.getSession().getServletContext()
				.getRealPath("/")
				+ "WEB-INF/temp";
	}
	
	public static String getImageFolder(HttpServletRequest request){
		return request.getSession().getServletContext()
				.getRealPath("/")
				+ "WEB-INF/image";
	}

}
