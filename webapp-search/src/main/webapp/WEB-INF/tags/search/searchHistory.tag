<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>

<%@ attribute name="history" type="java.util.List" description="The search history list" %>
<%@ attribute name="savedSearchFolders" type="java.util.List" description="The existing saved search folders" %>


	<c:choose>
		<c:when test="${fn:length(history) > 0}">


			<search:searchHistoryControls savedSearchFolders="${savedSearchFolders}" />
		
			<form method="post" action="<c:url value="/savedsearches"/>" id="searchHistoryForm">

				<input type="hidden" name="action" value="save" />
				<input type="hidden" name="folderName" value="" id="searchFolder" />
		
		
				<ul class="savedSearchWrapper searchHistoryWrapper">
					<c:forEach items="${history}" var="search" varStatus="status">
						<li class="searchHistoryItem">
							<div class="itemAction"><input type="checkbox" name="saveSearchIndex" value="${status.count -1}" /></div>						
							<div class="itemContent">
								<div>
									<s:message key="savedsearchesTitle" pageAgnostic="true" editable="true">Title:</s:message> <search:termsFromExplanation  searchExplanations="${search.search.searchExplanation}" />
								</div>
								<div>
									<search:explanationTextProcessor searchExplanations="${search.search.searchExplanation}" />
								</div>
								<div>
								 	(<s:message key="savedsearchesHits" pageAgnostic="true" editable="true">Hits</s:message> ${search.totalCount})
								</div>
							</div>
							<div class="clearer">&nbsp;</div>						
							<div class="itemLinks"><jp:link to="/runsavedsearch?searchIndex=${status.count -1}" class="searchLink"><s:message key="savedsearchesshowresults" pageAgnostic="true" editable="true">show results</s:message></jp:link><%--&nbsp;&nbsp;<jp:link to="/mock/notimplemented" class="searchLink">modify search</jp:link>--%></div>
						</li>
					</c:forEach>
				</ul>
			</form>
			<search:searchHistoryControls savedSearchFolders="${savedSearchFolders}" />
		</c:when>
		<c:otherwise>
			<p><s:message key="searchhistorynotrunanysearches" pageAgnostic="true" editable="true">You have not yet run any searches. Any searches that you run will be displayed on this page for the duration of this user session.</s:message></p>
		</c:otherwise>
	</c:choose>

		
			
	