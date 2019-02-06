<%@ include file="/common/taglibs.jsp"%>
<c:set var="dto" value="${requestScope.monitoringDTO}" />

<c:forEach var="session" items="${dto.sessionDTOs}">

<c:if test="${isGroupedActivity}">
	<div class="card card-plain" >
       <div class="card-header" id="stats${session.sessionID}">
  	    	<span class="card-title">
			${session.sessionName}</a>
			</span>
      	</div>
</c:if>

	<table class="table table-condensed">
		<tr>
			<td><strong> <fmt:message key="heading.totalLearners" /> </strong></td>
			<td>${session.numberOfLearners}	</td>
		</tr>
		<tr>
			<td><strong> <fmt:message key="heading.numberOfVotes" /> </strong></td>
			<td>${session.numberOfVotes}</td>
		</tr>
	</table>

<c:if test="${isGroupedActivity}">
	</div>
</c:if>

</c:forEach>

