<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>
<%@ taglib prefix="s2" uri="/struts-tags" %>

<s2:set name="ss" value="action.suggestedSearch"/>
<c:forEach items="${currentSearch.paramMap}" var="mapsuggestion" varStatus="status">
	<c:if test="${not empty ss.paramMap[mapsuggestion.key] and (fn:trim(ss.paramMap[mapsuggestion.key]) != fn:trim(mapsuggestion.value))}">	
	
		<c:url var ="suggestionurl" value="/search">
			<c:forEach var="p" items="${param}">
				<c:choose>
					<c:when test="${fn:length(paramValues[p.key]) > 1}">
						<c:forEach var="val" items="${paramValues[p.key]}" varStatus="status">
							<c:param name="${p.key}" value="${val}" />
						</c:forEach>
					</c:when>			
					<c:otherwise>
						<c:choose>
							<c:when test="${p.key == mapsuggestion.key}">
								<c:param name="${p.key}" value="${ss.paramMap[mapsuggestion.key]}" />
							</c:when>
							<c:otherwise>
								<c:param name="${p.key}" value="${p.value}" />
							</c:otherwise>
						</c:choose>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</c:url>
		
        <search:didYouMeanLink to="${suggestionurl}" title="${ss.paramMap[mapsuggestion.key]}"/>
			
	</c:if>
</c:forEach>          