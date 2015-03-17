<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>

<%@ attribute name="constraintName" type="java.lang.String" required="true" description="The name of the facet" %>


	<c:set var="labelledFacets" value="${skin.checkLabelledSearchFacets}" />
	
	<c:set var="isLabelledFacet" value="${false}" scope="request" />
		
	<c:forTokens var="facet" items="${labelledFacets}" delims="," varStatus="status">
		<c:if test="${facet == constraintName}">
			<c:set var="isLabelledFacet" value="${true}" scope="request" />
		</c:if>
	</c:forTokens>
	
