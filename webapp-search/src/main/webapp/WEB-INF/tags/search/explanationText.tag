<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>

	<div class="explanationText">
	
		<s:message key="found_for" global="true" editable="true">Found</s:message>
		<fmt:formatNumber value="${searchResult.totalCount}"/>
		<s:message key="search_result_found" global="true" editable="true">result(s)</s:message>
		<search:explanationTextProcessor searchExplanations="${searchResult.searchExplanation}" />
	
	</div>
	
