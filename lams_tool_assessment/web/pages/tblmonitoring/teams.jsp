<%@ include file="/common/taglibs.jsp"%>

<c:forEach var="questionResult" items="${assessmentResult.questionResults}" varStatus="i">
	<c:set var="question" value="${questionResult.assessmentQuestion}"/>
	
	<div class="card card-plain">
		<div class="card-header">
			<h4 class="card-title">
				<span class="float-left space-right">Q${i.index+1})</span> 
				<c:out value="${questionResult.assessmentQuestion.title}" escapeXml="false"/>
			</h4> 
		</div>
	              
		<div class="card-body">
			<div class="table-responsive">
				<table class="table table-striped">
					<tbody>
						<tr>
							<td>
								<%@ include file="userresponse.jsp"%>
												
								<!--<c:choose>
									<c:when test="${userAttempt.mcOptionsContent.correctOption}">
										<i class="fa fa-check-square"></i>
									</c:when>
									<c:otherwise>
										<i class="fa fa-minus-square"></i>
									</c:otherwise>
								</c:choose>-->
							</td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</c:forEach>
