<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
        "http://www.w3.org/TR/html4/strict.dtd">
<%@ include file="/common/taglibs.jsp"%>
<html>
	<head>
		<title>${instructions.title}</title>

		<%@ include file="/common/header.jsp"%>
		<script language="JavaScript" type="text/JavaScript">
		<!--
				function finishIns(){
					if(${param.mode == "learner"}){
					   var reqIDVar = new Date();
					   //if auto run mode, the opener will be null
					   if(window.parent.opener != null) 
						    window.parent.opener.location.href="<c:url value="/learning/completeItem.do"/>?itemUid=${param.itemUid}&reqID="+reqIDVar.getTime();
					   else{
					  		//set complete flag and finish this activity as well.
					        window.parent.location.href='<c:url value="/learning/finish.do?toolSessionID=${param.toolSessionID}&itemUid=${param.itemUid}"/>';
					   }
					}
				   if(window.parent.opener != null) {
						window.parent.opener=null;
						window.parent.close();
					}
				}
				function nextIns(currIns){
					document.location.href="<c:url value='/nextInstruction.do'/>?insIdx=" + currIns + "&itemUid=${param.itemUid}&itemIndex=${param.itemIndex}&mode=${param.mode}";
				}
		//-->
		</script>
	</head>
	<body>
		<div id="content" style="padding-bottom: 0px;float:none;margin: auto;">
			<table cellpadding="0">
				<tr valign="top">
					<td width="75%" valign="top">
						<h2>
							Step ${instructions.current} of ${instructions.total}
						</h2>
						<P>
							<span style="align:center"> <c:choose>
									<c:when test="${instructions.instruction == null}">
										<fmt:message key="msg.no.instruction" />
									</c:when>
									<c:otherwise>
							${instructions.instruction.description}
						</c:otherwise>
								</c:choose> </span>
					</td>
					<td width="25%" valign="middle">
						<c:choose>
							<c:when test="${instructions.current < instructions.total}">
								<a id="NextInstruction" onClick="javascript:nextIns(${instructions.current})" class="button">
									<fmt:message key='label.next.instruction'/>
								</a>
							</c:when>
							<c:otherwise>
								<a id="FinishInstruction" onClick="javascript:finishIns()" class="button">
									<fmt:message key='label.finish'/>
								</a>
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
			</table>
		</div>
		<div id="footer" style="float:none;margin: auto;"></div>
	</body>
</html>
