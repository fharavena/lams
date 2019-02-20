<%@ page contentType="text/html; charset=utf-8" language="java"%>
<%@ taglib uri="tags-lams" prefix="lams"%>
<%@ taglib uri="tags-fmt" prefix="fmt"%>
<%@ taglib uri="tags-core" prefix="c"%>
<%@ taglib uri="tags-function" prefix="fn"%>

<div id="favorite-organisations-container" class="tour-organisations-favorites">

	<c:if test="${not empty favoriteOrganisations}">
		<div class="list-group" id="favorite-organisations">
			<c:forEach var="favoriteOrganisation" items="${favoriteOrganisations}">
				<a data-id="${favoriteOrganisation.organisationId}" href="#nogo" id="favorite-li-${favoriteOrganisation.organisationId}"  
					class="list-group-item <c:if test="${favoriteOrganisation.organisationId == activeOrgId}">active"</c:if>"
						onClick="javascript:selectOrganisation(${favoriteOrganisation.organisationId})">
					${favoriteOrganisation.name}
					<span class="pull-right"><i class="fa fa-star"></i></span>
				</a>
			</c:forEach>
		</div>
	</c:if>
</div>

