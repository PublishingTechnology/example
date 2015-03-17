<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>
	<form method="get" action="<c:url value='/search'/>" id="sortForm">
		<div class="sortBy">
			<search:excludeInputParam property="sortField" />	
			<span class="sortByLabel"><s:message key="sort_results_by" pageAgnostic="true" editable="true">Sort results by</s:message> </span>							
			
				<jp:link to="#" id="sortRelevance" test="${(not empty param.sortField) and (param.sortField != 'default')}"><s:message key="relevance" pageAgnostic="true" editable="true">Relevance</s:message></jp:link> |
				<jp:link to="#" id="sortNewest" test="${(empty param.sortField) or (param.sortField == 'default') or ((param.sortDescending == 'false') and (param.sortField != 'default'))}"><s:message key="newest_titles_first" pageAgnostic="true" editable="true">Newest titles first</s:message></jp:link> |
				<jp:link to="#" id="sortOldest" test="${(empty param.sortField) or (param.sortField == 'default') or ((param.sortDescending == 'true') and (param.sortField != 'default'))}"><s:message key="oldest_titles_first" pageAgnostic="true" editable="true">Oldest titles first</s:message></jp:link>
		</div>
	</form>
	
