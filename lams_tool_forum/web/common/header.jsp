<%@ include file="/common/taglibs.jsp"%>
<c:set var="lams">
	<lams:LAMSURL />
</c:set>
<c:set var="tool">
	<lams:WebAppURL />
</c:set>
<c:set var="ctxPath" value="${pageContext.request.contextPath}" scope="request" />

<!-- ********************  css and javascript ********************** -->
<lams:headItems minimal="true"/>
<script type="text/javascript" src="${tool}includes/javascript/message.js"></script>
<script type="text/javascript" src="${lams}includes/javascript/jquery.timeago.js"></script>
