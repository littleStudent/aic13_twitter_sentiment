<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Analysis" %>
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Sentiment Analysis Application</title>
  </head>
  <body>
    <h3>Register:</h3>
    
    <form name="register_company" method="post" action="datespan.jsp">
    <p>
    	<label>Company name:</label> <input type="text" name="company_name" value="" />
    </p>
    
    <p>
    	<input type="submit" value="Register" />
    </p>
    
    </form>
  </body>
</html>