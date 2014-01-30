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
    <h3>Sentiment Analysis Results</h3>
    
    <p>Registered company: <span id="registered_company"><%= request.getParameter("company_name") %></span></p>
    
    <p>Begin Date: <span id="begin"><%= request.getParameter("begin") %></span></p>
    
    <p>End Date: <span id="end"><%= request.getParameter("end") %></span></p>
    
    <p>Current results of analysis:</p>
    
    <div id="results" style="font-size:25px">
    	Calculating...
    </div>
    
    <div>
    	<button id="renew_current_results">Renew Results</button>
    </div>
	
    <script type="text/javascript">
    	$( document ).ready(function() {
    		
			getCurrentResults();
			
			$('#renew_current_results').click(function() {
				getCurrentResults();
			});
    		
    	});
    	
    	function getCurrentResults() {
    		$.post( "calculate.jsp", { registered_company: "<%= request.getParameter("company_name") %>", begin: "<%= request.getParameter("begin") %>", end: "<%= request.getParameter("end") %>" }).done(function( data ) {
    		    $('#results').html(data);
    		  });
    	}
    </script>
	
  </body>
</html>
