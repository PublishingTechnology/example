<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>

<%@ attribute name="facetname" required="false" description="the new facet being added" %>
<%@ attribute name="additionalurl" required="false" description="search url" %>

	<c:set var="lastOption" value="0" scope="request" />
    <c:forEach var="p" items="${param}">
    	<c:if test="${fn:startsWith(p.key,'option')}">
    		<c:set var="thisOption">${fn:substringAfter(p.key, 'option')}</c:set>
			<c:if test="${(thisOption-1) > (lastOption-1)}">
				<c:set var="lastOption" value="${thisOption}" scope="request"/>
			</c:if>			
    	</c:if>
	</c:forEach>
    
    <c:choose>
  		<c:when test="${not empty additionalurl}">
    		<c:set var="url"><search:modifySearchParams currentOption="${lastOption + 1}" currentFacetName="${facetname}" href="${additionalurl}" firstPage="${true}" /></c:set>
    	</c:when>
  		<c:otherwise>
			<c:set var="url"><search:modifySearchParams currentOption="${lastOption + 1}" currentFacetName="${facetname}" href="/search" firstPage="${true}" /></c:set>
		</c:otherwise>
	</c:choose>
		
   	${url}