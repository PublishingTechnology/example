<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>
<%@ taglib tagdir="/WEB-INF/tags/navigation" prefix="navigation-ui" %>

<%@ attribute name="includePerPage" type="java.lang.Boolean" required="false" description="Indicates if the per page dropown is to be shown" %>
<%@ attribute name="searchURL" type="java.lang.String" required="false" description="Used to set the URL values for search or saved search" %>

<c:set var="includePerPage" value="${not empty includePerPage ? includePerPage : false}" />
<c:set var="searchURL" value="${!empty savedSearch ? '/runsavedsearch':'/search'}" />
	<div>
      <div class="paginator"><navigation:paginationLinks listSize="${searchResult.totalCount}"/></div>
		<c:if test="${includePerPage}">
         <div class="perpageoptions"><navigation-ui:pageSizeOptionLinks/></div>
		</c:if>
		<div class="clearer">&nbsp;</div>
	</div>