<%@ include file="/common/taglibs.jsp"%>

<c:set var="dto" value="${requestScope.monitoringDTO}" />


<c:forEach var="session" items="${dto.sessionDTOs}">

	<c:if test="${isGroupedActivity}">	
	    <div class="card card-statistics" >
        <div class="card-header" id="heading${session.sessionID}">
			<span class="card-title">
				<c:out value="${session.sessionName}" />
			</span>
        </div>
        <div class="card-body">
	</c:if>

	<table class="table table-sm table-no-border">
		<tbody>
			<tr>
				<td class="field-name" style="width: 30%;">
					<fmt:message>heading.totalLearners</fmt:message>
				</td>
				<td>
					${session.numberOfLearners}
				</td>
			</tr>

			<tr>
				<td class="field-name" style="width: 30%;">
					<fmt:message>heading.totalMessages</fmt:message>
				</td>
				<td>
					${session.numberOfPosts}
				</td>
			</tr>

			<tr>
				<th>
					<fmt:message>heading.learner</fmt:message>
				</th>
				<th>
					<fmt:message>heading.numPosts</fmt:message>
				</th>
			</tr>

			<c:forEach var="user" items="${session.userDTOs}">
				<tr>
					<td>
						${user.nickname}
					</td>
					<td>
						${user.postCount}
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	
	<c:if test="${isGroupedActivity}">	
		</div>
	</c:if>
	
</c:forEach>
