<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'index.jsp' starting page</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
  </head>
  <script type="text/javascript">
  	function fun1(){
  		//1 创建出想要添加的行
  		var tr = document.createElement("tr");
  		tr.innerHTML = "<td><input type='file' name='photo' /></td><td><input type='button' value='删除' onclick='fun2(this)'  /></td>";
  		//2 找到表格
  		var table = document.getElementById("one");
  		//3 找到表格最后一行
  		var lastRow = table.rows[table.rows.length-1];
  		//4 insertBefore
  		lastRow.parentNode.insertBefore(tr, lastRow);
  	}
  	//参数: 要删除行中的删除按钮对象
  function fun2(obj){
	  obj.parentNode.parentNode.parentNode.removeChild(obj.parentNode.parentNode);
  }
  </script>
  <body>
  <form action="/androidsafe/StoreImgServlet"  method="post" encType="multipart/form-data" >
    <table border="1" id="one" >
		<tr>
			<th colspan="2"  >照片上传</th>
		</tr>
    	<tr>
    		<td><input type="file" name="photo" /></td>
    		<td><input type="button" value="添加" onclick="fun1()"  /></td>
    		<td><input type="text" name="token" /></td>
    		<
    	</tr>
    	<tr>
    		<td colspan="2" align="center" ><input type="submit" value="上传" /></td>
    	</tr>
    </table>
  </form>
  </body>
</html>
