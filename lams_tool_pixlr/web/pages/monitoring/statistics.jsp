<%@ include file="/common/taglibs.jsp"%>
<c:set var="dto" value="${pixlrDTO}" />

<c:if test="${empty dto.sessionDTOs}">
	<lams:Alert type="info" id="no-session-summary" close="false">
		<fmt:message key="message.summary" />
	</lams:Alert>
</c:if>

<c:forEach var="session" items="${dto.sessionDTOs}">

	<c:if test="${isGroupedActivity}">
		<div class="card card-plain" >
			<div class="card-header">
				<span class="card-title">
					<fmt:message key="heading.group">
						<fmt:param>${session.sessionName}</fmt:param>
					</fmt:message>
				</span>
			</div>
		</div>
	</c:if>

	<div class="card-body">
		<table class="table table-condensed">
			<tr>
				<td width="40%">
					<fmt:message>heading.totalLearnersInGroup</fmt:message>
				</td>
				<td width="70%">
					${session.numberOfLearners}
				</td>
			</tr>
			<tr>
				<td>
					<fmt:message>heading.totalFinishedLearnersInGroup</fmt:message>
				</td>
				<td>
					${session.numberOfFinishedLearners}
				</td>
			</tr>
		</table>
	</div>
</c:forEach>
