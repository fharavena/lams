<%@ include file="/includes/taglibs.jsp" %>

<html:form action="authoring/update" method="post">
		<c_rt:set var="formBean" value="<%= request.getAttribute(org.apache.struts.taglib.html.Constants.BEAN_KEY) %>" />
		<html:hidden property="toolContentID"/>
		
		<%@ include file="../authoring/basic.jsp"%>
		
</html:form>