<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>

<c:set var="pageTitle"><s:message key="advancedsearchpagetitle" pageAgnostic="true" editable="true">Advanced Search</s:message></c:set>

<p:body title="${pageTitle}" page="search.advancedsearch" stylesheets="rummage" id="advancedsearchpage" scripts="/jp/jquery.validate">

	<c:if test="${skin.howtoSearchexplanation}">
	<div class="usesearchexplanation">
	<s:message key="howtoSearchexplanation" pageAgnostic="true" editable="true">
		<p>
			Enter one or more search criteria below. Boolean AND, OR and NOT are supported (e.g. jewish AND history).Use quotation marks (" ") to find an exact phrase (e.g. "jewish history"). Use asterisks to match partial words in fields (e.g. hammadi*).		
		</p>
	</s:message>
	</div>
	</c:if>
	
<div id="searchContent">

	<c:if test="${not empty fn:escapeXml(param.refined)}">
		<div class="explanationText">
			<s:message key="refinesearchwithin" pageAgnostic="true" editable="true">Refine search within:<br /></s:message>
			<search:explanationTextProcessor searchExplanations="${searchHistory[0].search.searchExplanation}" />
		</div>
	</c:if>
   <c:if test="${invalid}">
      <div class="searcherror">
         <s:message key="invalidsearch" pageAgnostic="true" editable="true">Please check your search terms<br /></s:message>
      </div>
   </c:if>
	
	<%@ include file="/WEB-INF/jspf/search/search_form.jspf" %>
	
</div>

</p:body>