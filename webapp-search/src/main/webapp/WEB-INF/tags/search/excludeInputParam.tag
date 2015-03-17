<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/ui" %>

<%@ attribute name="property" required="false" description="the property to be changed" %>
<c:set var="single_quote" value="'"/>
<c:set var="double_quote" value='"'/>

<c:forEach var="p" items="${param}" varStatus="status">
	<c:if test="${property ne p.key}">
		<c:choose>
			<c:when test="${fn:length(paramValues[p.key]) > 1}">
				<c:forEach var="val" items="${paramValues[p.key]}" varStatus="status">
					<input type="hidden" name="${p.key}" value="${fn:replace(val,double_quote,single_quote)}" />
				</c:forEach>
			</c:when>			
			<c:otherwise>
				<input type="hidden" name="${p.key}" value="${fn:replace(p.value,double_quote,single_quote)}" />
			</c:otherwise>
		</c:choose>
	</c:if>
</c:forEach>

