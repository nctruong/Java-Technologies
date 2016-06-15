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
							<li><a href="${request.contextPath}/logout?userId=${student.id}">Logout</a></li>
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

		<div class="col-md-10 content">
			<div class="panel panel-default">
				<div>${notify}</div>
				<div class="panel-heading">Dashboard</div>
				<div class="panel-body">
					Message : ${message}
					<br/>
					<br/>
					Go back to <a href="<c:url value='/student/info?student_id=${studentId}' />">list Students</a>
</div>
			</div>
		</div>
		
		<footer class="pull-left footer">
			<p class="col-md-12">
			<hr class="divider">
			Copyright &COPY; 2015 <a href="#">Nguyen Cong Truong</a>
			</p>
		</footer>
	</div>
</body>
</html>