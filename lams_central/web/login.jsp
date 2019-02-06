<%@page import="org.springframework.web.context.request.SessionScope"%>
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="tags-fmt" prefix="fmt"%>
<%@ taglib uri="tags-core" prefix="c"%>
<%@ taglib uri="tags-lams" prefix="lams"%>
<%@ page import="org.lamsfoundation.lams.util.Configuration"%>
<%@ page import="org.lamsfoundation.lams.util.ConfigurationKeys"%>
<%@ page import="org.lamsfoundation.lams.web.session.SessionManager"%>
<%@ page import="org.lamsfoundation.lams.usermanagement.dto.UserDTO"%>

<%-- Optional Module Placeholder - do not remove --%>

<c:if test="${empty requestScope.login}">
	<c:set var="login" value="${sessionScope.login}" />
	<c:set var="password" value="${sessionScope.password}" />
</c:if>
<c:set var="isForgotYourPasswordEnabled"><%=Configuration.get(ConfigurationKeys.FORGOT_YOUR_PASSWORD_LINK_ENABLE)%></c:set>

<!DOCTYPE html>
<lams:html>

<%-- If login param is empty, this is a regular, manual login page.
	 Otherwise it is a just an almost empty redirect page for integrations and LoginAs authentication.
 --%>
<c:choose>
	<c:when test="${empty login}">
		<lams:head>
			<title><fmt:message key="title.login.window" /></title>
			<lams:css/>
			<link rel="icon" href="/lams/favicon.ico" type="image/x-icon" />
			<link rel="shortcut icon" href="/lams/favicon.ico" type="image/x-icon" />
			<link rel="apple-touch-icon" href="/lams/favicon.ico"   type="image/x-icon"  sizes="76x76">

			<script type="text/javascript" src="/lams/includes/javascript/browser_detect.js"></script>
			<script type="text/javascript" src="/lams/includes/javascript/jquery.js"></script>
			<script type="text/javascript" src="/lams/includes/javascript/popper.min.js"></script>
			<script type="text/javascript" src="/lams/includes/javascript/bootstrap-material-design.min.js"></script>

			<script type="text/javascript">
				function submitForm() {
					$('#loginButton').addClass('disabled');
					$('#loginForm').submit();
				}

				function onEnter(event) {
					intKeyCode = event.keyCode;
					if (intKeyCode == 13) {
						submitForm();
					}
				}
				
				function isBrowserCompatible() {
					return Modernizr.atobbtoa && Modernizr.checked && Modernizr.cookies && Modernizr.nthchild && Modernizr.opacity &&
						   Modernizr.svg && Modernizr.todataurlpng && Modernizr.websockets && Modernizr.xhrresponsetypetext;
					// Modernizr.datauri - should be included, it's a async test though
					// Modernizr.time - should be included, fails in Chrome for an unknown reason (reported)
					// Modernizr.xhrresponsetypejson - should be included, fails in IE 11 for an unknown reason (reported)
				}

				$(document).ready(function() {
					$('html').addClass('index-page');
					if (!isBrowserCompatible()) {
						$('#browserNotCompatible').show();
					}
					$('#j_username').focus();
/* 					$('#news').load('/lams/www/news.html');
 */
					//make a POST call to ForgotPasswordRequest
					$("#forgot-password-link").click(function() {
						var $form=$(document.createElement('form'))
							.css({display:'none'})
							.attr("method","POST")
							.attr("action","<lams:LAMSURL/>ForgotPasswordRequest?method=showForgotYourPasswordPage");
						$("body").append($form);
						$form.submit();
					});
				});
			</script>
		</lams:head>
		<body class="index-page">
		<div class="login-content">
		
    <!-- Fixed navbar -->
    <nav class="navbar navbar-color-on-scroll fixed-top navbar-expand-lg navbar-transparent navbar-login">
		<div class="container">
	        <div class="navbar-header">
	          <a class="navbar-brand navbar-brand-login" href="#"><%=Configuration.get(ConfigurationKeys.SITE_NAME)%></a>
	        </div>
			<div class="navbar-collapse collapse navbar-right">
				<div class="login-logo" title="LAMS - Learning Activity Management System"></div>
			</div>
		</div>	
    </nav>
		<!-- Close navbar -->

		<!-- Start content  -->
	<div class="page-header header-filter" style="background-image: url('<lams:LAMSURL/>www/images/login_background.jpg'); background-size: cover; background-position: top center;">
     <div class="container">
      <div class="row">
        <div class="col-lg-4 col-md-6 ml-auto mr-auto">
          <div class="card card-login">
              <div class="card-header card-header-primary text-center">
                <h4 class="card-title"><img file="<lams:LAMSURL/>images/svg/lams_logo_white.svg"/> <fmt:message key="button.login" /></h4>
              </div>
  
			<div id="browserNotCompatible" class="card card-danger" style="display: none">
				 <p class="description text-center"><fmt:message key="msg.browser.compat"/></p>
            </div>
			<c:if test="${!empty param.failed}">
				<p class="description text-center text-danger"><fmt:message key="error.login" /></p>
			</c:if>
			<c:if test="${!empty param.lockedOut}">
				<p class="description text-center text-danger"><fmt:message key="error.lockedout" /></p>
			</c:if>

			<form action="/lams/j_security_check" method="POST" name="loginForm" role="form" class="form-horizontal" id="loginForm" autocomplete="off">
			<input type="hidden" name="redirectURL" value='<c:out value="${param.redirectURL}" escapeXml="true" />' />
			
              <div class="card-body">
                <div class="input-group">
                  <div class="input-group-prepend">
                    <span class="input-group-text">
                      <i class="material-icons">face</i>
                    </span>
                  </div>
                  <input id="j_username" type="text" class="form-control" autocapitalize="off" name="j_username" value="" placeholder="<fmt:message key='label.username' />" onkeypress="onEnter(event)" tabindex="1">
                </div>
                <div class="input-group">
                  <div class="input-group-prepend">
                    <span class="input-group-text">
                      <i class="material-icons">lock_outline</i>
                    </span>
                  </div>
                  <input id="j_password" type="password" class="form-control" name="j_password" placeholder="<fmt:message key='label.password' />" onkeypress="onEnter(event)" tabindex="2">
                </div>
              </div>
				<c:if test="${isForgotYourPasswordEnabled}">
			   		<p class="description text-center"><a id="forgot-password-link" href="#nogo"> <fmt:message key="label.forgot.password" /></a></p>
			    </c:if>
              <div class="footer text-center">
              	<a id="loginButton" href="javascript:submitForm()" class="btn btn-primary  btn-wd  btn-lg" tabindex="3"><fmt:message key="button.login" /></a>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
    </div>
    		<!--closes content-->

		<!-- starts footer -->
    <footer class="footer">
      <div class="container">
 		  	<p>
					<fmt:message key="msg.LAMS.version" />:  <%=Configuration.get(ConfigurationKeys.VERSION)%>
					<a href="/lams/www/copyright.jsp" target='copyright' onClick="openCopyRight()"> &copy; <fmt:message key="msg.LAMS.copyright.short" /></a>
				</p>
		<!--closes footer-->
		</div>
	</footer>
	
		</div> <!--  close login-content -->
		</body>
	</c:when>

	<%-- This is version for integrations and LoginAs authentication. --%>

	<c:otherwise>
		<lams:head>
			<lams:css />
			<link rel="icon" href="<lams:LAMSURL/>favicon.ico" type="image/x-icon" />
			<link rel="shortcut icon" href="<lams:LAMSURL/>favicon.ico" type="image/x-icon" />
			<link rel="apple-touch-icon" href="/lams/favicon.ico"   type="image/x-icon"  sizes="76x76">
			
		</lams:head>
		<body class="stripes">
			<!-- A bit of content so the page is not completely blank -->
			<lams:Page type="admin">

				<div class="text-center" style="margin-top: 20px; margin-bottom: 20px;">
					<i class="fa fa-2x fa-refresh fa-spin text-primary"></i>
					<h4>
						<fmt:message key="msg.loading" />
					</h4>
				</div>


				<form style="display: none" method="POST" action="j_security_check">
					<input type="hidden" name="j_username" value="${login}" /> <input type="hidden" name="j_password"
						value="${password}" /> <input type="hidden" name="redirectURL"
						value='<c:out value="${empty param.redirectURL ? redirectURL : param.redirectURL}" escapeXml="true" />' />
				</form>
				<div id="footer"></div>
			</lams:Page>
			<%
				// invalidate session so a new user can be logged in
				HttpSession hs = SessionManager.getSession();
				if (hs != null) {
					UserDTO userDTO = (UserDTO) hs.getAttribute("user");
					if (userDTO != null) {
					    // remove session from mapping
					    SessionManager.removeSessionByLogin(userDTO.getLogin(), false);
					}
					hs.invalidate();
				}
			%>
			<script type="text/javascript">
				// submit the hidden form
				document.forms[0].submit();
			</script>
		</body>
	</c:otherwise>
</c:choose>

</lams:html>
