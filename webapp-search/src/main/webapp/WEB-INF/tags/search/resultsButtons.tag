<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>

	<ul class="buttons">
		<li><%-- jp:link to="/testsavedsearches?action=history&TB_iframe=true&height=500&width=600" title="Save this search" class="searchButton thickbox" --%><jp:link to="/savedsearches?action=history"  title="Save this search" class="btn-orange"><span>Save this search</span></jp:link></li>
		<c:if test="${empty param.refined}"><li><a href="<search:modifySearchParams href="/search/advancedsearch" />" title="Modify this search" class="btn-orange"><span>Modify this search</span></a></li></c:if>
		<c:choose>
			<c:when test="${not empty param.refineLevel }">
				<c:set var="refineLevel" value="${param.refineLevel + 1}"/>
			</c:when>
			<c:otherwise>
				<c:set var="refineLevel" value="1"/>
			</c:otherwise>
		</c:choose>					
		<li><jp:link to="/search/advancedsearch?refined=true&refineLevel=${refineLevel}" title="Refine this search" class="btn-orange"><span>Refine this search</span></jp:link></li>
	</ul>
