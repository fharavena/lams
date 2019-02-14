<%@ include file="/common/taglibs.jsp"%>
<c:set var="dto" value="${mindmapDTO}" />

<c:if test="${empty dto.sessionDTOs}">
	<lams:Alert type="info" id="no-session-summary" close="false">
		<fmt:message key="label.nogroups" />
	</lams:Alert>
</c:if>

<c:forEach var="session" items="${dto.sessionDTOs}">

	<c:if test="${isGroupedActivity}">
		<div class="card card-statistics" >
			<div class="card-header">
				<span class="card-title">
					<fmt:message key="monitoring.label.group" />&nbsp;${session.sessionName}
				</span>
			</div>
		</div>
	</c:if>

	<div class="card-body">
		<table class="table table-sm">
			<tr>
				<td width="40%">
					<fmt:message>heading.totalLearnersInGroup</fmt:message>
				</td>
				<td width="70%">
					${session.numberOfLearners}
				</td>
			</tr>
			<tr>
				<td width="40%">
					<fmt:message>heading.totalFinishedLearnersInGroup</fmt:message>
				</td>
				<td width="70%">
					${session.numberOfFinishedLearners}
				</td>
			</tr>
		</table>
	</div>
</c:forEach>
