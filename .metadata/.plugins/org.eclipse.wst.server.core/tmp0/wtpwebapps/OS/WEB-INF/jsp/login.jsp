<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>


<html>
<head>
	<meta http-equiv="Content-Type" content="text/html"; charset="UTF-8"">
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
	<link rel="stylesheet" type="text/css" href='<c:url value="/resources/css/bootstrap.css"/>' />
	<script type="text/javascript" src='<c:url value="/resources/css/bootstrap.js"/>' ></script>
	
	<link rel="stylesheet" type="text/css" href='<c:url value="/resources/css/login.css"/>' />
	<script type="text/javascript" src='<c:url value="/resources/css/login.js"/>' ></script>
	<title>Spring Final Assignment</title>

</head>
<body>

<h1> OS </h1>
<h3><em>Nguyen Cong Truong</em></h3>
<div class="container">
        <div class="card card-container">
            
            <img id="profile-img" class="profile-img-card" src="//ssl.gstatic.com/accounts/ui/avatar_2x.png" />
            <p id="profile-name" class="profile-name-card"></p>
            <form:form class="form-signin" action="login" commandName="loginForm" method="post">
                <span id="reauth-email" class="reauth-email"></span>
                <form:input type="text" path="username" id="inputEmail" class="form-control" placeholder="Username"/>
                <form:input type="password" path="password" id="inputPassword" class="form-control" placeholder="Password"/>
                <div id="remember" class="checkbox">
                    <label>
                        <input type="checkbox" value="remember-me"> I'm Awesome, Remember Me!
                    </label>
                </div>
                <button class="btn btn-lg btn-primary btn-block btn-signin" type="submit">Let's Go!</button>
            </form:form><!-- /form -->
            <a href="#" class="forgot-password">
                Forgot your password?
            </a>
        </div><!-- /card-container -->
 </div><!-- /container -->
    

</body>
</html>		  