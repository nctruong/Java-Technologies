<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
	<link rel="stylesheet" type="text/css" href='<c:url value="/resources/css/bootstrap.css"/>' />
	
	<script type="text/javascript" src='<c:url value="/resources/css/bootstrap.js"/>' ></script>
	<title>Student Registration Form</title>

<style>

	.error {
		color: #ff0000;
	}
	.form-control{
		
	}
	#register{
		margin:10% auto;
		width:30%;
		border-collapse: separate;
		border-spacing: 0;
		min-width: 350px;
		box-shadow: 10px 10px 10px gray;
		padding: 5px 5px;
	}
	.ftitle{
		background-color:blue;
		color:white;
		text-align:center;
	}
	.fbody{
		
	}
</style>

</head>

<body>

<div id="register">
<div class="ftitle">Registration Form</div>
<br>
<div class="fbody">
<form class="form-horizontal" role="form" modelAttribute="student">
    <div class="form-group">
      <label class="control-label col-sm-2" for="email">Name:</label>
      <div class="col-sm-10">
        <input type="text" class="form-control" name="fullName" id="fullName"/>
      </div>
    </div>
    <div class="form-group">
      <label class="control-label col-sm-2" for="pwd">Sex:</label>
      <div class="col-sm-10">          
        <input type="text" class="form-control" name="sex" id="sex"/>
      </div>
    </div>
    <div class="form-group">
      <label class="control-label col-sm-2" for="pwd">Address:</label>
      <div class="col-sm-10">          
        <input type="text" class="form-control" name="address" id="address"/>
      </div>
    </div>
    <div class="form-group">        
      <div class="col-sm-offset-2 col-sm-10">
  
					<c:choose>
						<c:when test="${edit}">
							<button type="submit" class="btn btn-primary">Update</button>
						</c:when>
						<c:otherwise>
							<button type="submit" class="btn btn-primary">Register</button>
						</c:otherwise>
					</c:choose>
		</div>
      </div>
    
  </form>
  </div>
</div>	
 
	
	<br/>
	<br/>
	Go back to <a href="<c:url value='/list' />">List of All Students</a>
</body>
</html>