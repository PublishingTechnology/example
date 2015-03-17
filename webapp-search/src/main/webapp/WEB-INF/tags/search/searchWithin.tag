<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>

	<jp:check_rdf_type />
		
	<c:choose>
		<c:when test="${isJournal}">
			<c:set var="acs_parents" value="${fn:substring(item.webId, 1, fn:length(item.webId))}" />		
			<c:set var="pubtitle" value="${item.properties.dcterms_title}" />
			<c:set var="pubtype"><jp:rdf_type item="${item}" /></c:set>
		</c:when>
		<c:when test="${isIssue}">
			<c:set var="journal" value="${item.properties.dcterms_isPartOf}" />				
			<c:set var="acs_parents" value="${fn:substring(journal.webId, 1, fn:length(item.webId))}" />		
			<c:set var="pubtitle" value="${journal.properties.dcterms_title}" />		
			<c:set var="pubtype"><jp:rdf_type item="${journal}" /></c:set>
		</c:when>
		<c:when test="${isArticle}">
			<c:set var="issue" value="${item.properties.dcterms_isPartOf}" />				
			<c:set var="journal" value="${issue.properties.dcterms_isPartOf}" />				
			<c:set var="acs_parents" value="${fn:substring(journal.webId, 1, fn:length(item.webId))}" />		
			<c:set var="pubtitle" value="${journal.properties.dcterms_title}" />		
			<c:set var="pubtype"><jp:rdf_type item="${journal}" /></c:set>
		</c:when>
		<c:when test="${isBook}">
			<c:set var="acs_parents" value="${fn:substring(item.webId, 1, fn:length(item.webId))}" />		
			<c:set var="pubtitle" value="${item.properties.dcterms_title}" />		
			<c:set var="pubtype"><jp:rdf_type item="${item}" /></c:set>
		</c:when>
		<c:when test="${isChapter}">
			<c:set var="book" value="${item.properties.dcterms_isPartOf}" />				
			<c:set var="acs_parents" value="${fn:substring(book.webId, 1, fn:length(item.webId))}" />		
			<c:set var="pubtitle" value="${book.properties.dcterms_title}" />		
			<c:set var="pubtype"><jp:rdf_type item="${book}" /></c:set>
		</c:when>
		<c:when test="${isCollection}">
			<c:set var="acs_parents" value="${fn:substring(item.webId, 1, fn:length(item.webId))}" />		
			<c:set var="pubtitle" value="${item.properties.dcterms_title}" />		
			<c:set var="pubtype"><jp:rdf_type item="${item}" /></c:set>
		</c:when>
		<c:when test="${isBookCollection}">
			<c:set var="acs_parents" value="${fn:substring(item.webId, 1, fn:length(item.webId))}" />		
			<c:set var="pubtitle" value="${item.properties.dcterms_title}" />		
			<c:set var="pubtype"><jp:rdf_type item="${item}" /></c:set>
		</c:when>
		<c:when test="${isBookSeries}">
			<c:set var="acs_parents" value="${fn:substring(item.webId, 1, fn:length(item.webId))}" />		
			<c:set var="pubtitle" value="${item.properties.dcterms_title}" />		
			<c:set var="pubtype"><jp:rdf_type item="${item}" /></c:set>
		</c:when>
	</c:choose>

	<c:if test="${(not empty acs_parents) and (not empty pubtitle) and (page.showSearchWithin)}">
		<form action="<c:url value="/search"/>">
			<div class="searchwrapperbox">
				<input type="text" name="value1" value="Search this ${pubtype}" id="searchWithinField" class="searchWithinField" />
				<input type="image" class="searchwithinbutton" src="${skin.searchWithinButton}" />
				<input type="hidden" name="option1" value="fulltext" />
				<input type="hidden" name="operator2" value="AND" />
				<input type="hidden" name="value2" value="${acs_parents}" />
				<input type="hidden" name="option2" value="acs_parents" />
				<input type="hidden" value="${pubtitle}" name="searchWithin" />
			</div>
		</form>
	</c:if>