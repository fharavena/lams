<%@ include file="/common/taglibs.jsp"%>

<!-- ========== Advanced Tab ========== -->
<lams:SimplePanel titleKey="label.chat.options">
<div class="togglebutton">
	<label for="filteringEnabled">
	<form:checkbox path="filteringEnabled" value="1" id="filteringEnabled"/>
	<span class="toggle"></span>
	<fmt:message key="advanced.filteringEnabled" />
	</label>
</div>
<div class="form-group">
	<textarea name="filterKeywords" rows="3" class="form-control">${authoringForm.filterKeywords}</textarea>
</div>
</lams:SimplePanel>

<lams:OutcomeAuthor toolContentId="${sessionMap.toolContentID}" />

<lams:SimplePanel titleKey="label.activity.completion">
<div class="togglebutton">
	<label for="lockOnFinished">
	<form:checkbox path="lockOnFinished" value="1" id="lockOnFinished"/>
	<span class="toggle"></span>
	<fmt:message key="advanced.lockOnFinished" />
	</label>
</div>

<div class="togglebutton">
	<label for="reflectOnActivity">
	<form:checkbox path="reflectOnActivity" value="1" id="reflectOnActivity"/>
	<span class="toggle"></span>
	<fmt:message key="advanced.reflectOnActivity" />
	</label>
</div>

<div class="form-group">
	<textarea name="reflectInstructions" rows="3" id="reflectInstructions" class="form-control">${authoringForm.reflectInstructions}</textarea>
</div>
</lams:SimplePanel>

<script type="text/javascript">
<!--
//automatically turn on refect option if there are text input in refect instruction area
	var ra = document.getElementById("reflectInstructions");
	var rao = document.getElementById("reflectOnActivity");
	function turnOnRefect(){
		if(isEmpty(ra.value)){
		//turn off	
			rao.checked = false;
		}else{
		//turn on
			rao.checked = true;		
		}
	}

	ra.onkeyup=turnOnRefect;
//-->
</script>

