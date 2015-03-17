<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>

<p:body page="search.morefacet" title="Search Results ahah" stylesheets="">
	<facet:itemList lens="http://pub2web.metastore.ingenta.com/views/item-label-view" item="${param.ids}" />		

	<c:set var="facetcounter" value="0"/>
    <c:forEach items="${param.ids}" var="facetid">
		<c:set var="facetValues" value="${itemtag[facetid]}"/>
   		<c:set var="facetLabel" value="${facetValues.properties.rdfs_label}" />
		<c:forEach items="${param.values}" var="facetval" begin="${0}" end="${facetcounter}" varStatus="status">
			<c:if test="${status.last}">
				<c:set var="facetvalues" value="${facetval}" />
			</c:if>
 		</c:forEach>   		
		<c:forEach items="${param.names}" var="facetnme" begin="${0}" end="${facetcounter}" varStatus="status">
			<c:if test="${status.last}">
				<c:set var="facetname" value="${facetnme}" />
			</c:if>
 		</c:forEach>   		
		<li class="facetitem ajaxhidden">
			<c:set var="facetUrl">${param.searchurl}${facetname}</c:set>
			<a href="${facetUrl}">${facetLabel} ${facetvalues}</a>
		</li>   		    	
		<c:set var="facetcounter" value="${facetcounter + 1}"/>
	</c:forEach>
         <li class="toggleajaxfacetitem">
            <jp:link to="#" class="facetshowtext hiddenprop"><s:message key="facetSeeMore" pageAgnostic="true" editable="true">See more...</s:message></jp:link>
            <jp:link to="#" class="facethidetext "><s:message key="facetHide" pageAgnostic="true" editable="true">Hide...</s:message></jp:link>
        </li>
</p:body>