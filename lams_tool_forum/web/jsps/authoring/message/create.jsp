<%@ include file="/includes/taglibs.jsp" %>
<script type="text/javascript" src="<html:rewrite page='/includes/scripts.jsp'/>"></script>

<html:errors property="error" />
<div align="center">
<html:form action="/authoring/createTopic.do" focus="message.subject"
	onsubmit="return validateMessageForm(this);"  enctype="multipart/form-data">
<fieldset>
<%@ include file="/jsps/message/topiclist.jsp" %>
<%@ include file="/jsps/message/form.jsp" %>
 </fieldset>
 </html:form>
</div>
