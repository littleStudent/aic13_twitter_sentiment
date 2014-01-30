<%@ page import="model.Analysis" %>
<%
	Analysis jb = new Analysis();

String company_name = request.getParameter("company_name");
String begin = request.getParameter("begin");
String end = request.getParameter("end");

if(company_name==null) {
	company_name = "";
}

if(begin==null) {
	begin = "";
}

if(end==null) {
	end = "";
}

 out.println( "<p>" + jb.result(company_name,begin,end) + "</p>" );
%>