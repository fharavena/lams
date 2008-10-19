<%@ include file="/common/taglibs.jsp"%>

<c:set var="ctxPath" value="${pageContext.request.contextPath}"
	scope="request" />

<c:set var="tool">
	<lams:WebAppURL />
</c:set>

<div id="itemList">
	<h2 class="spacer-left">
		<fmt:message key="label.authoring.basic.image.list" />
		<img src="${ctxPath}/includes/images/indicator.gif"
			style="display:none" id="imageGalleryListArea_Busy" />
	</h2>
	
	<table class="alternative-color" id="itemTable" cellspacing="0">
		<c:set var="sessionMap" value="${sessionScope[sessionMapID]}" />

		<c:forEach var="image" items="${sessionMap.imageGalleryList}" varStatus="status">
			<tr>
				<td class="field-name align-center" width="4%">
					<c:set var="thumbnailPath">
					   	<html:rewrite page='/download/?uuid='/>${image.thumbnailFileUuid}&preferDownload=false
					</c:set>
				 	<c:set var="mediumImagePath">
	   					<html:rewrite page='/download/?uuid='/>${image.mediumFileUuid}&preferDownload=false
					</c:set>					
					<a href="${mediumImagePath}" rel="lightbox" title="Enlarge image"><img src="${thumbnailPath}" /></a>
				</td>
				
				<td>
					<a href="${mediumImagePath}" rel="lightbox" title="Enlarge image">${image.title}</a>
				</td>

<%--				
				<td>
						<a href="#"
							onclick="previewItem(${status.index},'${sessionMapID}')"> <fmt:message
								key="label.authoring.basic.resource.preview" /> </a>
				</td>
--%>
				
				<td width="40px" style="valign:bottom;">
					<c:if test="${not status.first}">
						<img src="<html:rewrite page='/includes/images/uparrow.gif'/>"
							border="0" title="<fmt:message key="label.authoring.up"/>"
							onclick="upImage(${status.index},'${sessionMapID}')">
						<c:if test="${status.last}">
							<img
								src="<html:rewrite page='/includes/images/downarrow_disabled.gif'/>"
								border="0" title="<fmt:message key="label.authoring.down"/>">
						</c:if>
					</c:if>

					<c:if test="${not status.last}">
						<c:if test="${status.first}">
							<img
								src="<html:rewrite page='/includes/images/uparrow_disabled.gif'/>"
								border="0" title="<fmt:message key="label.authoring.up"/>">
						</c:if>

						<img src="<html:rewrite page='/includes/images/downarrow.gif'/>"
							border="0" title="<fmt:message key="label.authoring.down"/>"
							onclick="downImage(${status.index},'${sessionMapID}')">
					</c:if>
				</td>
				
				<td width="20px" >
					<img src="${tool}includes/images/edit.gif"
						title="<fmt:message key="label.authoring.basic.resource.edit" />"
						onclick="editItem(${status.index},'${sessionMapID}')" />
                </td>
                
				<td width="20px">
					<img src="${tool}includes/images/cross.gif"
						title="<fmt:message key="label.authoring.basic.resource.delete" />"
						onclick="deleteItem(${status.index},'${sessionMapID}')" />
				</td>
			</tr>
		</c:forEach>
	</table>
</div>

<%-- This script will works when a new resoruce item submit in order to refresh "ImageGallery List" panel. --%>
<script lang="javascript">

	if(window.top != null){
		window.top.hideMessage();
		var obj = window.top.document.getElementById('imageGalleryListArea');
		obj.innerHTML= document.getElementById("itemList").innerHTML;
	}
</script>
