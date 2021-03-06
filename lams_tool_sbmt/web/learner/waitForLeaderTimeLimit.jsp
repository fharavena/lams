<!DOCTYPE html>
<%@ include file="/common/taglibs.jsp"%>

<lams:html>
<lams:head>
	<title><fmt:message key="activity.title" /></title>

	<lams:css />
	<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/jquery.js"></script>

	<script type="text/javascript">
		function refresh() {
			location.reload(true);
		}
		
		//refresh page every 30 sec
		setTimeout("refresh();",30000);
    </script>
</lams:head>
<body class="stripes">

	<lams:Page type="learner" title="${content.title}">
				
		<c:if test="${isLeadershipEnabled}">
			<lams:LeaderDisplay username="${sessionMap.groupLeader.firstName} ${sessionMap.groupLeader.lastName}" userId="${sessionMap.groupLeader.userId}"/>
		</c:if>
	
		
		<h4>
			<fmt:message key="${waitingMessageKey}" />
		</h4>
		
		<button name="refreshButton" onclick="refresh();" class="btn btn-sm btn-primary pull-right">
			<fmt:message key="label.refresh" />
		</button>

	</lams:Page>

</body>
</lams:html>
