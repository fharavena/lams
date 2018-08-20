<%-- 
Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
License Information: http://lamsfoundation.org/licensing/lams/2.0/

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License version 2 as 
  published by the Free Software Foundation.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
  USA

  http://www.gnu.org/licenses/gpl.txt
--%>

<!DOCTYPE html>

<%@ include file="/common/taglibs.jsp"%>

<lams:html>

<lams:head>
	<title><fmt:message key="learner.title" />
	</title>

	<lams:css />
	<c:set var="lams">
		<lams:LAMSURL />
	</c:set>

	<script type="text/javascript" src="${lams}includes/javascript/jquery.js"></script>
	<script type="text/javascript"
		src="${lams}includes/javascript/common.js"></script>
</lams:head>

			<body class="stripes">
			<c:set var="learnerAppUrl">
				<lams:WebAppURL />
			</c:set>
			<lams:css/>
			<%-- nprogress.css contents in learner.css --%>
			<script type="text/javascript" src="<lams:LAMSURL />includes/javascript/jquery.js"></script>
			<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/nprogress.js"></script>
			
			<c:if test="${!empty activityForm.activityURLs}">
			
				<script type="text/javascript">
					function redirectPage() {
						NProgress.configure({
							easing : 'ease',
							speed : 30
						});
						NProgress.configure({
							showSpinner: false, 
							trickleRate : 0.2,
							trickleSpeed : 110
						});
						NProgress.start();
						setTimeout("doRedirect()", 1000);
			
					}
			
					function doRedirect() {
						var url = "<c:out value='${activityForm.activityURLs[0].url}' escapeXml="false" />";
						if (url.substring(0, 4) != "http") {
							if (url.substring(0, 1) == "/") {
								url = "${learnerAppUrl}.." + url;
							} else {
								url = "${learnerAppUrl}../" + url;
							}
						}
						window.location.href = url;
					}
				</script>
			
				<script language="JavaScript" type="text/JavaScript">
					$(document).ready(function(){
						redirectPage();
					});
				</script>
			
				<lams:Page type="admin">
					<div class="text-center" style="margin-top: 10px; margin-bottom: 15px;">
						<i class="fa fa-2x fa-refresh fa-spin text-primary"></i>
						<p class="voffset5">
							<fmt:message key="message.activity.loading" />
						</p>
					</div>
				</lams:Page>
				 
			
			</c:if>
		</body>

</lams:html>
