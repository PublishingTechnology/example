<%-- RefWorks --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="p" uri="http://engineering.ingenta.com/taglibs/page" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<p:body page="search.results">
<c:forEach items="${searchResult.results}" var="result" varStatus="status">
ID ${status.count}
<c:forEach items="${result.properties.pub_author}" var="author">
AU ${author}
</c:forEach>
TI ${result.properties.dcterms_title[0]}
JN ${result.properties.pub_serialTitle[0]}
VL ${result.properties.prism_volume[0]}
PG ${result.properties.prism_endingPage[0] - result.properties.prism_startingPage[0]}
UL http://tt.ottone.ingenta.com:9901/pub2web/${fn:substringAfter(result.identifier,'http://pub2web.metastore.ingenta.com')}
<c:if test="${not empty result.properties.prism_doi[0] and result.properties.prism_doi[0] != 'NO_DOI'}">
UL doi:${result.properties.prism_doi[0]}
</c:if>
</c:forEach>
</p:body>