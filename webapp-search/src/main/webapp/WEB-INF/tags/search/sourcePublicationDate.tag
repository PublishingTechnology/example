<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>
<%@ attribute name="result" type="java.lang.Object" required="true" description="The search result item" %>
<%@ attribute name="parentItem" type="com.ingenta.facet.content.ContentItem" required="false" description="Parent content item from the metastore" %>
<%@ attribute name="showTrailingSeparator" type="java.lang.Boolean" required="false" description="show Trailing Separator indicator" %>
<%@ attribute name="trailingSeparator" type="java.lang.Object" required="false" description="Trailing Separator" %>
<%@ attribute name="datePattern" type="java.lang.Object" required="false" description="datePattern" %>


 <c:set var="showTrailingSeparator" value="${not empty showTrailingSeparator ? showTrailingSeparator : true}" />
 <c:set var="trailingSeparator" value="${not empty skin.trailingSeparator ? skin.trailingSeparator : ','}" />
 <c:set var="datePattern" value="${not empty datePattern ? datePattern : 'dd MMMM yyyy'}" />
 
<c:forEach items="${result.properties.prism_publicationDate}" var="publicationdate">
    <c:set var="pubDate"><search:formatDate date="${publicationdate}" /></c:set>
    <c:if test="${not empty pubDate }">${fn:trim(pubDate)}</c:if>
    <c:if test="${showTrailingSeparator and (not empty pubDate) and ((fn:length(result.properties.prism_startingPage) > 0) or (fn:length(result.properties.prism_endingPage) > 0))}">${trailingSeparator}</c:if>
</c:forEach>

<c:if test="${fn:length(result.properties.prism_publicationDate) == 0 and not empty parentItem}">
    <c:set var="pubDate"><jp:date item="${parentItem}" pattern="${datePattern }" /></c:set>
    ${fn:trim(pubDate)}<c:if test="${showTrailingSeparator }">${trailingSeparator}</c:if>
</c:if>
