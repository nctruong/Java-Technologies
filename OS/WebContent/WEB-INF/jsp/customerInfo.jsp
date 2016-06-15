<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html" ; charset="UTF-8"">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<link rel="stylesheet" type="text/css"
	href='<c:url value="/resources/css/bootstrap.min.css"/>' />
<script type="text/javascript"
	src='<c:url value="/resources/js/bootstrap.min.js"/>'></script>

<link rel="stylesheet" type="text/css"
	href='<c:url value="/resources/css/studentInfo.css"/>' />
<script type="text/javascript"
	src='<c:url value="/resources/js/studentInfo.js"/>'></script>
<title>Spring Final Assignment</title>

  

</head>
<% String contextPath = request.getContextPath(); %>
<body>


	<nav class="navbar navbar-default navbar-static-top">
		<div class="container-fluid">
			<!-- Brand and toggle get grouped for better mobile display -->
			<div class="navbar-header">
				<button type="button"
					class="navbar-toggle navbar-toggle-sidebar collapsed">
					MENU</button>
				<button type="button" class="navbar-toggle collapsed"
					data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="#"> Administrator </a>
			</div>

			<!-- Collect the nav links, forms, and other content for toggling -->
			<div class="collapse navbar-collapse"
				id="bs-example-navbar-collapse-1">
				<form class="navbar-form navbar-left" method="GET" role="search">
					<div class="form-group">
						<input type="text" name="q" class="form-control"
							placeholder="Search">
					</div>
					<button type="submit" class="btn btn-default">
						<i class="glyphicon glyphicon-search"></i>
					</button>
				</form>
				<ul class="nav navbar-nav navbar-right">
					<li><a href="#" target="_blank">Visit Site</a></li>
					<li class="dropdown "><a href="#" class="dropdown-toggle"
						data-toggle="dropdown" role="button" aria-expanded="false">
							${student.username } <span class="caret"></span>
					</a>
						<ul class="dropdown-menu" role="menu">
							<li class="dropdown-header">SETTINGS</li>
							<li class=""><a href="#">Other Link</a></li>
							<li class=""><a href="#">Other Link</a></li>
							<li class=""><a href="#">Other Link</a></li>
							<li class="divider"></li>
							<li><a href="<%= contextPath %>/logout?userId=${student.id}">Logout</a></li>
						</ul>
				</ul>
			</div>
			<!-- /.navbar-collapse -->
		</div>
		<!-- /.container-fluid -->
	</nav>
	<div class="container-fluid main-container">
		<div class="col-md-2 sidebar">
			<div class="row">
				<!-- uncomment code for absolute positioning tweek see top comment in css -->
				<div class="absolute-wrapper"></div>
				<!-- Menu -->
				<div class="side-menu">
					<nav class="navbar navbar-default" role="navigation">
						<!-- Main Menu -->
						<div class="side-menu-container">
							<ul class="nav navbar-nav">
								<li class="active"><a href="#"><span
										class="glyphicon glyphicon-dashboard"></span> Dashboard</a></li>
								<li><a href="#"><span class="glyphicon glyphicon-plane"></span>
										Active Link</a></li>
								<li><a href="#"><span class="glyphicon glyphicon-cloud"></span>
										Link</a></li>

								<!-- Dropdown-->
								<li class="panel panel-default" id="dropdown"><a
									data-toggle="collapse" href="#dropdown-lvl1"> <span
										class="glyphicon glyphicon-user"></span> Sub Level <span
										class="caret"></span>
								</a> <!-- Dropdown level 1 -->
									<div id="dropdown-lvl1" class="panel-collapse collapse">
										<div class="panel-body">
											<ul class="nav navbar-nav">
												<li><a href="#">Link</a></li>
												<li><a href="#">Link</a></li>
												<li><a href="#">Link</a></li>

												<!-- Dropdown level 2 -->
												<li class="panel panel-default" id="dropdown"><a
													data-toggle="collapse" href="#dropdown-lvl2"> <span
														class="glyphicon glyphicon-off"></span> Sub Level <span
														class="caret"></span>
												</a>
													<div id="dropdown-lvl2" class="panel-collapse collapse">
														<div class="panel-body">
															<ul class="nav navbar-nav">
																<li><a href="#">Link</a></li>
																<li><a href="#">Link</a></li>
																<li><a href="#">Link</a></li>
															</ul>
														</div>
													</div></li>
											</ul>
										</div>
									</div></li>

								<li><a href="#"><span
										class="glyphicon glyphicon-signal"></span> Link</a></li>

							</ul>
						</div>
						<!-- /.navbar-collapse -->
					</nav>

				</div>
			</div>
		</div>

		<%-- <div class="col-md-10 content">
			<div class="panel panel-default">
				<div>${notify}</div>
				<div class="panel-heading">Dashboard</div>
				<div class="panel-body">
					<div class="container">
						<div class="row col-md-6 col-md-offset-2 custyle">
							<table class="table table-striped custab">

								<thead>
									<tr>
										<th colspan="4">
											<p>Full Name: <em>${student.firstName} ${student.lastName}</em></p>
											<p>Address: <em>${address.street}, ${address.city},
												${address.country}</em></p>
											<p>Age: <em>${student.age}</em></p>
											<p>Sex: <em>${student.sex}</em></p>
										</th>
									</tr>
									<a href="#" class="btn btn-primary btn-xs pull-right"  data-toggle="modal" data-target="#newScore"><b>+</b>
										Add new Subject</a>
									
								</thead>
								<tr>
									<th>No</th>
									<th>Subject</th>
									<th>Score</th>
									<th class="text-center">Action</th>
								</tr>

								<c:forEach var="score" items="${scores}">
									<tr>
										<td>${score.scoreId}</td>
										<td>${score.subject}</td>
										<td>${score.score}</td>
										<td class="text-center">
											<!-- a class='btn btn-info btn-xs' href="#" data-toggle="modal" data-target="#editScore"-->
											<a class='btn btn-info btn-xs' href="score/edit?studentId=${studentId}&scoreId=${score.scoreId}" >
												<span class="glyphicon glyphicon-edit"></span>
												Edit</a> <a href="score/askDelete?studentId=${studentId}&scoreId=${score.scoreId }" class="btn btn-danger btn-xs"><span
												class="glyphicon glyphicon-remove"></span> Del</a>
										</td>
									</tr>
								</c:forEach>


							</table>
						</div>
					</div>
				</div>
			</div>
		</div> --%>
		<!-- CREATE NEW SCORE -->
		<%-- <div class="modal fade" id="newScore" role="dialog">
			<div class="modal-dialog">

				<!-- Modal content-->
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal">&times;</button>
						<h4 class="modal-title">New Subject</h4>
					</div>
					<div class="modal-body">
						<form:form action="score/new" method="POST" modelAttribute="scoreForm">
							<p>Subject: <form:input type="text" path="subject" class="form-control" placeholder="subject"/></p>
							<p>Score: <form:input type="number" path="score" class="form-control" placeholder="score"/></p>
							<form:input path="student_id" type="hidden" value="${student.id}"/>
					</div>
					<div class="modal-footer">
						<input type="submit" class="btn btn-default" data-dismiss="submit" value="Submit"/>
						</form:form>
						<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
					</div>
				</div>

			</div>
		</div>
		
		<!-- EDIT SCORE -->
		<div class="modal fade" id="editScore" role="dialog">
			<div class="modal-dialog">

				<!-- Modal content-->
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal">&times;</button>
						<h4 class="modal-title">Edit Score</h4>
					</div>
					<div class="modal-body">
						<form:form action="score/edit" method="POST" modelAttribute="scoreForm">
							<p>Subject: <form:input type="text" path="subject" class="form-control" placeholder="${score.subject }"/></p>
							<p>Score: <form:input type="number" path="score" class="form-control" placeholder="${score.score }"/></p>
							<form:input path="student_id" type="hidden" value="${score.student_id}"/>
							<form:input path="scoreId" type="hidden" value="${score.scoreId}"/>
					</div>
					<div class="modal-footer">
						<input type="submit" class="btn btn-default" data-dismiss="submit" value="Submit"/>
						</form:form>
						<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
					</div>
				</div>

			</div>
		</div> --%>
		
		<footer class="pull-left footer">
			<p class="col-md-12">
			<hr class="divider">
			Copyright &COPY; 2015 <a href="#">Nguyen Cong Truong</a>
			</p>
		</footer>
	</div>
</body>
</html>