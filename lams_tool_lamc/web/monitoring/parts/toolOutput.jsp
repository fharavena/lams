<%@ include file="/common/taglibs.jsp"%>

<div id="accordion-tool-output" class="accordian voffset20" role="tablist" aria-multiselectable="true"> 
    <div class="card card-plain">
        <div class="card-header collapsable-icon-left" id="heading-tool-output">
        	<span class="card-title">
		    	<a class="collapsed" role="button" data-toggle="collapse" href="#tool-output" aria-expanded="true" aria-controls="tool-output">
	          		<fmt:message key="label.tool.output" />
	          	</a>
      		</span>
        </div>

		<div aria-expanded="false" id="tool-output" class="card-body card-collapse collapse" role="tabcard" aria-labelledby="heading-tool-output">
		
			<select name="activityEvaluation" id="activity-evaluation" autocomplete="off">
				<option value=""><fmt:message key="output.desc.none" /></option>
				
				<c:forEach var="toolOutputDefinition" items="${toolOutputDefinitions}" varStatus="firstGroup">
				
					<option value="${toolOutputDefinition}"
							<c:if test="${toolOutputDefinition == activityEvaluation}">selected="selected"</c:if>>
						<fmt:message key="output.desc.${toolOutputDefinition}" />
					</option>
					
				</c:forEach>
			</select>

		</div>
	</div>
</div>
