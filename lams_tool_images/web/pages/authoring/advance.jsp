<%@ include file="/common/taglibs.jsp"%>

<script lang="javascript">
	/**
	 * Processes mouse click event on allowRatingsOrVote ckeckbox
	 */
	function allowRatingsOrVoteClicked() {
		document.imageGalleryForm.allowVote.disabled = ! eval(document.imageGalleryForm.allowVote.disabled); 
		document.imageGalleryForm.allowRank.disabled = ! eval(document.imageGalleryForm.allowRank.disabled);
		if (document.imageGalleryForm.allowRatingsOrVote.checked) {
			document.imageGalleryForm.allowVote.checked = true;
		} else {
			document.imageGalleryForm.allowVote.checked = false;
			document.imageGalleryForm.allowRank.checked = false;
		}		
	}
</script>
	
<c:set var="formBean"
	value="<%=request.getAttribute(org.apache.struts.taglib.html.Constants.BEAN_KEY)%>" />

<!-- Advance Tab Content -->

<p class="small-space-top">
	<html:checkbox property="imageGallery.lockWhenFinished"	styleClass="noBorder" styleId="lockWhenFinished">
	</html:checkbox>

	<label for="lockWhenFinished">
		<fmt:message key="label.authoring.advance.lock.on.finished" />
	</label>
</p>

<p>
	<html:checkbox property="imageGallery.allowShareImages" styleClass="noBorder" styleId="allowShareImages">
	</html:checkbox>
	<label for="allowShareImages">
		<fmt:message key="label.authoring.advance.allow.learner.share.images" />
	</label>
</p>

<p>
	<html:checkbox property="imageGallery.allowCommentImages" styleClass="noBorder" styleId="allowCommentImages">
	</html:checkbox>
	<label for="allowCommentImages">
		<fmt:message key="label.authoring.advance.allow.learner.comment.images" />
	</label>
</p>

<p class="small-space-top">
	<html:checkbox property="allowRatingsOrVote" styleClass="noBorder" styleId="allowRatingsOrVote"
		onclick="allowRatingsOrVoteClicked();"
	>
	</html:checkbox>
	<label for="allowRatingsOrVote">
		<fmt:message key="label.authoring.advance.allow.learner.ratings.or.vote" />
	</label>
	<br><br>
	
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<input type="radio" name="imageGallery.allowVote" value="${true}" id="allowVote"
		<c:if test="${formBean.imageGallery.allowVote}">checked="checked"</c:if>
		<c:if test="${not (formBean.imageGallery.allowVote or formBean.imageGallery.allowRank)}">disabled="disabled"</c:if> 
	/>
	<label for="allowVote">
		<fmt:message key="label.authoring.advance.allow.learner.vote" />
	</label>
	<br><br>

	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<input type="radio" name="imageGallery.allowVote" value="${false}" id="allowRank"
		<c:if test="${formBean.imageGallery.allowRank}">checked="checked"</c:if> 
		<c:if test="${not (formBean.imageGallery.allowVote or formBean.imageGallery.allowRank)}">disabled="disabled"</c:if>		
	/>
	<label for="allowRank">
		<fmt:message key="label.authoring.advance.allow.learner.rank" />
	</label>
</p>

<p>
	<html:select property="imageGallery.numberColumns"	styleId="numberColumns" style="width:50px">
		<c:forEach begin="1" end="6" varStatus="status">
			<c:choose>
				<c:when
					test="${formBean.imageGallery.numberColumns == status.index}">
					<option value="${status.index}" selected="true">
						${status.index}
					</option>
				</c:when>
				<c:otherwise>
					<option value="${status.index}">
						${status.index}
					</option>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</html:select>

	<fmt:message key="label.authoring.advance.number.columns" />
</p>

<p>
	<html:checkbox property="imageGallery.reflectOnActivity"
		styleClass="noBorder" styleId="reflectOn">
	</html:checkbox>
	<label for="reflectOn">
		<fmt:message key="label.authoring.advanced.reflectOnActivity" />
	</label>
</p>

<p>
	<html:textarea property="imageGallery.reflectInstructions"
		styleId="reflectInstructions" cols="30" rows="3" />
</p>
<script type="text/javascript">
<!--
//automatically turn on refect option if there are text input in refect instruction area
	var ra = document.getElementById("reflectInstructions");
	var rao = document.getElementById("reflectOn");
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
