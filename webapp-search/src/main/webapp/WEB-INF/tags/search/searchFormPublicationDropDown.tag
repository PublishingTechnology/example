<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>

<%@ attribute name="item" type="java.lang.Object" required="true" description="The content item"%>

<div class="searchWithinInput">
    <div class="searchWithinInputLabel"><s:message key="thispublication" pageAgnostic="true" editable="true">This publication:</s:message></div>
    <input type="hidden" name="operator8" value="AND" />
    <input type="hidden" name="option8" value="journalbooktitle" />
    <select name="value8" id="publication"  ${skin.searchMultipleSelect}  ${skin.searchMultipleSelectSize}>
        <option value=""><s:message key="searchallpublications" pageAgnostic="true" editable="true">All Publications</s:message></option>
        <c:forEach var="publication" items="${item.properties.pub_publications}" varStatus="status">
            <c:set var="publicationName" value="${publication.properties.dcterms_title}" />
            <option id ="${publication.webId}" ${(fn:escapeXml(param.value8) == publicationName) ? 'selected="selected"' : ''} value="${publicationName}">${publication.properties.dcterms_title}</option>
        </c:forEach>
    </select>
</div>
