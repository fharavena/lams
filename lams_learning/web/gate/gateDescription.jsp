<%@ taglib uri="tags-core" prefix="c"%>
<div class="card card-plain">
    <c:if test="${!empty gateForm.gate.title}">
        <div class="card-header">
         <div class="card-title">
            <lams:out value="${gateForm.gate.title}" escapeHtml="true" />
         </div>
        </div>
    </c:if>
    <c:if test="${!empty gateForm.gate.description}">
        <div class="card-body">
            <lams:out value="${gateForm.gate.description}" escapeHtml="true" />
        </div>
    </c:if>
</div>
