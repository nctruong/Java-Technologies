<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<html>
<head>
	<title>Shopping Cart</title>
</head>
<body>
<h1>Your Shopping Cart</h1>
<c:set var="root" value="${pageContext.request.contextPath}"/>
<c:forEach items="${cart}" var="product">
  Name: ${product.name}, 
  price: <fmt:formatNumber value="${product.price}" maxFractionDigits="2"/> 
  <br/>
</c:forEach>
<h1>Add Product</h1>
<form:form action="${root}/addProduct" method="post" modelAttribute="product">
  Name: <form:input path="name" />
  Price: <form:input path="price" />
  <input type="submit" value="Add Product" />
</form:form>
</body>
</html>
