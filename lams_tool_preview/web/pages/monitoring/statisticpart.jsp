<%@ include file="/common/taglibs.jsp"%>
<c:set var="sessionMap" value="${sessionScope[sessionMapID]}"/>

	<c:if test="${empty summaryList}">
		<lams:Alert type="info" id="no-session-summary" close="false">
			<fmt:message key="message.monitoring.summary.no.session" />
		</lams:Alert>	
	</c:if>
	
	<c:if test="${sessionMap.isGroupedActivity}">
	<div class="card-group" id="accordionSessions" role="tablist" aria-multiselectable="true"> 
	</c:if>
	
	<c:forEach var="groupSummary" items="${summaryList}" varStatus="status">
		<c:if test="${sessionMap.isGroupedActivity}">	
		    <div class="card card-plain" >
	        <div class="card-header" id="headingStats${groupSummary.sessionId}">
				<span class="card-title">
					<fmt:message key="monitoring.label.group" />: <c:out value="${groupSummary.sessionName}" />
				</span>
	        </div>
	
	        <div class="card-body">
		</c:if>
	
		<table class="table table-sm table-no-border">
			<tr>
				<td class="field-name" width="30%">
					<fmt:message key="label.monitoring.number.learners"/>
				</td>
				<td>
					${groupSummary.numLearnersInSession}
				</td>
			</tr>
			<tr>
				<td class="field-name" width="30%">
					<fmt:message key="label.monitoring.number.learners.finished"/>
				</td>
				<td>
					${groupSummary.numLearnersComplete}
				</td>
			</tr>
		</table>

		<c:if test="${sessionMap.isGroupedActivity}">	
			</div>
			</div>
		</c:if>
	</c:forEach>

	<c:if test="${sessionMap.isGroupedActivity}">
	</div>
	</c:if>
	