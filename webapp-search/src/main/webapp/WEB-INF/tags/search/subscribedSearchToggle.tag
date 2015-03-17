<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>

<%@ attribute name="href" required="false" description="the link" %>

<c:if test="${not empty param.subscribed}">
	<c:set 	var="subscribed" value="true"/>
</c:if>


<c:url value="${href}">
        <c:forEach var="p" items="${param}">
		<c:if test="${p.key  != 'subscribed'}">		
	                <c:param name="${p.key}" value="${p.value}" />
		</c:if>
        </c:forEach>
	<c:choose>
		<c:when test="${not subscribed}">
			<c:param name="subscribed" value="true" />
		</c:when>
	</c:choose>
</c:url>

