
<%@ include file="/common/taglibs.jsp"%>

<div class="card card-plain">
	<c:out value="${scribeDTO.instructions}" escapeXml="false" />
</div>

<%@include file="/pages/parts/voteDisplay.jsp"%>

<h4>
	<fmt:message key="heading.report" />
</h4>

<c:forEach var="reportDTO" items="${scribeSessionDTO.reportDTOs}">

	<div class="row">
		<div class="col-xs-12">
			<div class="card card-plain">
				<div class="card-header card-title">
					<c:out value="${reportDTO.headingDTO.headingText}" escapeXml="false" />
				</div>
				<div class="card-body">
					<c:if test="${not empty reportDTO.entryText}">
						<c:set var="entry">
							<lams:out value="${reportDTO.entryText}" escapeHtml="true" />
						</c:set>
						<c:out value="${entry}" escapeXml="false" />
					</c:if>
				</div>
			</div>
		</div>
	</div>
</c:forEach>

<c:if test="${scribeUserDTO.finishedActivity and scribeDTO.reflectOnActivity}">
	<div class="row">
		<div class="col-xs-12">
			<div class="card card-plain">
				<div class="card-header card-title">
					<fmt:message key="heading.reflection" />
				</div>
				<div class="card-body">
					<div class="card card-plain">
						<lams:out value="${scribeDTO.reflectInstructions}" escapeHtml="true" />
					</div>

					<div class="card-body bg-warning voffset10">
						<lams:out value="${scribeUserDTO.notebookEntry}" escapeHtml="true" />
					</div>
				</div>
			</div>
		</div>
	</div>
</c:if>


<c:if test="${not empty otherScribeSessions}">
	<h4>
		<fmt:message key="heading.other.group.reports" />
	</h4>

	<c:set var="sessNumber" value="0"/>
	<c:forEach var="scribeSessionDTO" items="${otherScribeSessions}">
		<c:set var="sessNumber" value="${sessNumber +1 }"/>
		<div class="row">
			<div class="col-xs-12">
				<div class="card card-plain">
					<div class="card-header card-title">
					<a data-toggle="collapse" data-target="#card-${sessNumber}" href="#card-${sessNumber}">${scribeSessionDTO.sessionName}</a>
					</div>
					<div id="card-${sessNumber}" class="card-body card-collapse collapse in">

						<c:forEach var="reportDTO" items="${scribeSessionDTO.reportDTOs}">
							<div class="card card-info">
								<div class="card-header card-title">
									<c:out value="${reportDTO.headingDTO.headingText}" escapeXml="false" />
								</div>
								<div class="card-body">
									<c:if test="${not empty reportDTO.entryText}">
										<c:set var="entry">
											<lams:out value="${reportDTO.entryText}" escapeHtml="true" />
										</c:set>
										<c:out value="${entry}" escapeXml="false" />
									</c:if>
								</div>
							</div>

						</c:forEach>


					</div>
				</div>
			</div>
		</div>


	</c:forEach>
</c:if>

