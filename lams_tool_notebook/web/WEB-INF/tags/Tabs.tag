<%/****************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * License Information: http://lamsfoundation.org/licensing/lams/2.0/
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2.0
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */

/**
 * Tabs.tag
 *	Author: Fiona Malikoff
 *	Description: Create a hybrid panel header that contains a nav bar that acts like tabs.
 */

%>
<%@ attribute name="control" required="false" rtexprvalue="true"%>
<%@ attribute name="title" required="false" rtexprvalue="true"%>
<%@ attribute name="refreshOnClickAction" required="false" rtexprvalue="true"%>
<%@ attribute name="helpPage" required="false" rtexprvalue="true"%>
<%@ attribute name="helpToolSignature" required="false" rtexprvalue="true"%>
<%@ attribute name="helpModule" required="false" rtexprvalue="true"%>
<%@ attribute name="extraControl" required="false" rtexprvalue="true"%>

<%@ taglib uri="tags-core" prefix="c"%>
<%@ taglib uri="tags-lams" prefix="lams"%>

<c:set var="useActions" value="false" scope="request" />
<c:if test="${not empty helpToolSignature or not empty helpModule or not empty helpPage or not empty refreshOnClickAction or not empty extraControl}">
	<c:set var="useActions" value="true" scope="request" />
</c:if>

<c:set var="dControl" value="false" scope="request" />
<c:if test="${control}">
	<c:set var="dControl" value="${control}" scope="request" />
</c:if>

<!-- navbar combined with tabs -->
<div class="card card-default card-monitor-page">
<div class="card-heading navbar-heading">
	<nav class="navbar navbar-expand-md bg-primary">
 		<div class="container-fluid" style="flex-wrap: nowrap;">
       		<span class="navbar-brand"><c:if test="${not empty title}">${title}</c:if></span> <%-- Need span to keep the correct layout for small windows --%>
       		
	 		<div class="navbar-translate">
       		<button class="ml-auto navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
      					<span class="sr-only">Toggle navigation</span>
      					<span class="navbar-toggler-icon"></span>
      					<span class="navbar-toggler-icon"></span>
      					<span class="navbar-toggler-icon"></span>
            		</button>
	       	</div>
	       	
            <div class="collapse navbar-collapse" id="navbarNav">
            	<ul class="nav navbar-nav nav-tabs"  id="page-tabs" role="tablist">
                	<jsp:doBody />
                 </ul>
  	        	<c:if test="${useActions}">
		         <ul class="navbar-nav nav-tabs ml-md-auto" id="page-actions">  <%-- Pretend tabs to get same format --%>
				     <c:if test="${not empty refreshOnClickAction}">
		             <li class="nav-item" ><span class="nav-link" onclick="${refreshOnClickAction}"><i class="fa fa-refresh"></i></span></li>
		             </c:if>
		             <c:if test="${not empty helpToolSignature or not empty helpModule}">
		             <li class="nav-item" ><lams:help toolSignature="${helpToolSignature}" module="${helpModule}" style="small"/></li>
		             </c:if>
		             <c:if test="${not empty helpPage}">
		             <li class="nav-item" ><lams:help page="${helpPage}" style="small"/></li>
		             </c:if>
		             <c:if test="${not empty extraControl}">
		             <li class="nav-item" ><div class="nav-link" style="display:inline">${extraControl}<div></div></li>
		             </c:if>
		         </ul>
    	     	</c:if>
			</div>
             </div>
	 </nav>
     <!-- /top nav -->
</div>
<!-- panel div closed by TabBodyArea -->