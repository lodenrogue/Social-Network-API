<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>
<head>
<title>Register</title>

<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="resources/css/bootstrap.min.css" />
<link rel="stylesheet" href="resources/css/style.css" />
</head>
<body>
	<div class="container">
		<div class="row">
			<div class="col-xs-12">
				<div id="sign-up-form">
					<h2>Sign up</h2>
					<p id="have-account">
						Have an account? <a href="login"> Sign in. </a>
					</p>
					<form:form action="register" method="post" commandName="registrationForm">
						<!-- Email -->
						<form:input class="input-box" data-validate="required,email" path="email" placeholder="Email" />
						<form:errors class="error" path="email" />

						<!-- Username -->
						<form:input class="input-box" data-validate="required,min(3)" path="username" placeholder="Username" />
						<form:errors class="error" path="username" />

						<!-- Password -->
						<form:password class="input-box" data-validate="required,min(6)" path="password" placeholder="Password" />
						<form:errors class="error" path="password" />
						<p class="form-info" id="password-info">Must be at least 6 characters long</p>

						<!-- Sign up button -->
						<input class="button" id="sign-up-btn" type="submit" value="Get started!">
					</form:form>
				</div>
			</div>
		</div>
	</div>

	<script src="resources/jquery/jquery.min.js"></script>
	<script src="resources/js/bootstrap.min.js"></script>
	<script src="resources/js/verify.notify.min.js"></script>
	<script src="resources/js/notify-config.js"></script>
</body>
</html>