<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>

<%@ attribute name="href" required="false" description="the link" %>
<%@ attribute name="currentOption" required="false" description="the new option being added (faceted search)" %>
<%@ attribute name="currentFacetName" required="false" description="the new facet being added (faceted search)" %>
<%@ attribute name="firstPage" type="java.lang.Boolean" required="false" description="Indicates if page parameter should be removed" %>

<c:set var="includePage" value="${(empty firstPage) || (!firstPage) ? true : false}" />

	<c:set var="url">
		<c:url value="${href}">
			<c:forEach var="p" items="${param}">
				<c:if test="${(p.key ne 'page') or ((p.key == 'page') and (includePage))}">
					<c:choose>
						<c:when test="${fn:length(paramValues[p.key]) > 1}">
							<c:forEach var="val" items="${paramValues[p.key]}" varStatus="status">
								<c:param name="${p.key}" value="${val}" />
							</c:forEach>
						</c:when>			
						<c:otherwise>
							<c:choose>
								<c:when test="${(not empty currentOption) and (p.key == 'facetOptions')}">
									<c:param name="${p.key}" value="${p.value} ${currentOption}" />
								</c:when>
								<c:when test="${(not empty currentFacetName) and (p.key == 'facetNames')}">
									<c:param name="${p.key}" value="${p.value} ${currentFacetName}" />
								</c:when>
								<c:otherwise>
									<c:param name="${p.key}" value="${p.value}" />
								</c:otherwise>
							</c:choose>
						</c:otherwise>
					</c:choose>
				</c:if>
			</c:forEach>
			<c:if test="${(not empty currentOption) and (empty param.facetOptions)}">
				<c:param name="facetOptions" value="${currentOption}" />
			</c:if>
			<c:if test="${(not empty currentFacetName) and (empty param.facetNames)}">
				<c:param name="facetNames" value="${currentFacetName}" />
			</c:if>
		</c:url>
	</c:set>
	
	${fn:replace(url,'&','&amp;')}