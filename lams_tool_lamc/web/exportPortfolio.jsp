<%-- 
Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
License Information: http://lamsfoundation.org/licensing/lams/2.0/

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License version 2 as 
  published by the Free Software Foundation.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
  USA

  http://www.gnu.org/licenses/gpl.txt
--%>


<%@ taglib uri="tags-bean" prefix="bean"%> 
<%@ taglib uri="tags-html" prefix="html"%>
<%@ taglib uri="tags-logic" prefix="logic" %>
<%@ taglib uri="tags-logic-el" prefix="logic-el" %>
<%@ taglib uri="tags-core" prefix="c"%>
<%@ taglib uri="tags-fmt" prefix="fmt" %>
<%@ taglib uri="fck-editor" prefix="FCK" %>
<%@ taglib uri="tags-lams" prefix="lams" %>

<c:set var="lams"><lams:LAMSURL/></c:set>
<c:set var="tool"><lams:WebAppURL/></c:set>

	<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
	<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
	
	<html:html locale="true">
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
	<title> <bean:message key="label.exportPortfolio"/> </title>
	
	 <lams:css/>
	<!-- depending on user / site preference this will get changed probably use passed in variable from flash to select which one to use-->

 	<!-- ******************** FCK Editor related javascript & HTML ********************** -->
    <script type="text/javascript" src="${lams}fckeditor/fckeditor.js"></script>
    <script type="text/javascript" src="${lams}includes/javascript/fckcontroller.js"></script>
    <link href="${lams}css/fckeditor_style.css" rel="stylesheet" type="text/css">

	<script language="JavaScript" type="text/JavaScript">
    	var imgRoot="${lams}images/";
	    var themeName="aqua";
	</script>
	
	<script type="text/javascript" src="<c:out value="${lams}"/>includes/javascript/tabcontroller.js"></script>    
	<script type="text/javascript" src="<c:out value="${lams}"/>includes/javascript/common.js"></script>
	
	</head>

	<body>

   		<h1><bean:message key="label.exportPortfolio.simple"/></h1>
		<h2><bean:message key="label.tool.shortname"/></h2>
		<br>
		<div id="datatablecontainer">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td align="center">
					<c:out value="${McExportForm.title}" escapeXml="false"/>
					</td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td align="left">
						<c:out value="${McExportForm.content}" escapeXml="false"/>
					</td>
				</tr>
			</table>
		</div>

	</body>
</html:html>