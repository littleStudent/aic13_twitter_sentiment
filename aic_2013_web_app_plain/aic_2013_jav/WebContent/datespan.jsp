<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Analysis" %>
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Sentiment Analysis Application</title>
    
	<link href="css/ui-lightness/jquery-ui-1.10.3.custom.css" rel="stylesheet">
	<script src="js/jquery-1.9.0.js"></script>
	<script src="js/jquery-ui-1.10.0.custom.js"></script>
    
  </head>
  <body>
    <h3>Registered company: "<%= request.getParameter("company_name") %>":</h3>
    
    <p>Enter begin/end of the period you want to analyse:</p>
    
    <div id="choose_datespan">
    	<form name="register_company" method="post" action="results.jsp">
		    <p>
		    	<label>Begin: </label> <input type="text" id="begin" name="begin" class="datepicker" value="" />
		    </p>
		    
		    <p>
		    	<label>End: </label> <input type="text" id="end" name="end" class="datepicker" value="" />
		    </p>
		    <input type="hidden" name="company_name" value="<%= request.getParameter("company_name") %>" />
		    <input type="submit" value="Start Period Analysis" />
	    </form>
    </div>

    <script type="text/javascript">
    	$( document ).ready(function() {
    		$( ".datepicker" ).datepicker();
    	});
    </script>

  </body>
</html>

