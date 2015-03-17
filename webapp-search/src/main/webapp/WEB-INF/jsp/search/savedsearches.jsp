<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>

<c:set var="savedSearchesTitle"><s:message key="savedsearchespagetitle" pageAgnostic="true" editable="true">Saved Searches</s:message></c:set>

<p:body title="${savedSearchesTitle}" page="search.savedsearches" scripts="jsonsearches json2 divpopup savedsearches" stylesheets="tabset rummage" id="savedsearchespage">

<c:choose>
	<c:when test="${param.action == 'history'}">
		<c:set var="isHistory" value="${true}" />
	</c:when>
	<c:otherwise>
		<c:set var="isHistory" value="${false}" />
	</c:otherwise>
</c:choose>


	<c:url value="savedsearches?action=history" var="shurl" />
	<c:url value="savedsearches?action=savedSearches" var="ssurl" />
	<ui:tabset>
		<c:choose>
		<c:when test="${isHistory}">
			<ui:tab title="Saved Searches" blurbKey="savedsearches" link="${ssurl}" />
		</c:when>
		<c:otherwise>
			<ui:tab title="Saved Searches" blurbKey="savedsearches" link="${ssurl}" active="true" />
		</c:otherwise>
		</c:choose>
		<c:choose>
		<c:when test="${isHistory}">
			<ui:tab title="Search History" blurbKey="searchhistory" link="${shurl}" active="true" />
		</c:when>
		<c:otherwise>
			<ui:tab title="Search History" blurbKey="searchhistory" link="${shurl}" />
		</c:otherwise>
		</c:choose>
	</ui:tabset>

	<div id="searchContent" class="savedSearchesDiv">
	<s:message key="savedsearchesexplanation" pageAgnostic="true" editable="true">
	<p>Please select your folder to save the selected search(es)</p>
	</s:message>

		<%-- ${savedSearches} --%>
		<%-- ${history} --%>
		<c:if test="${not empty error}">
			<c:set var="invalidsearch" value="${true}" />
				<div class="error">
					<h2>${error}</h2>
				</div>
		</c:if>
	
		<c:choose>
			<c:when test="${isHistory}">

				<search:searchHistory history="${history}" savedSearchFolders="${savedSearches.allFolders}" />
		
			</c:when>
			<c:otherwise>	

				<search:savedSearches savedSearchFolders="${savedSearches.allFolders}" />     

			</c:otherwise>
		</c:choose>       
       
	</div>

</p:body>

<%-- /body>

</html --%>