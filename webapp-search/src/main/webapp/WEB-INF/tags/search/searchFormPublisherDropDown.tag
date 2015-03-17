<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>

<%@ attribute name="item" type="java.lang.Object" required="true" description="The content item"%>

<div class="searchWithinInput">
    <div class="searchWithinInputLabel"><s:message key="thispublisher" pageAgnostic="true" editable="true">This Publisher:</s:message></div>
    <input type="hidden" name="operator9" value="AND" />            
    <input type="hidden" name="option9" value="pub_publisherId" />
    <select name="value9" id="publisher" ${skin.searchMultipleSelect}  ${skin.searchMultipleSelectSize}>
        <option value=""><s:message key="searchallpublishers" pageAgnostic="true" editable="true">All Publishers</s:message></option>   
        <c:forEach var="publisher" items="${item.properties.pub_publishers}" varStatus="status">
            <c:set var="publisherName" value="${fn:substring(publisher.webId, 1, fn:length(publisher.webId))}" />
            <option id ="${publisher.webId}" ${(fn:escapeXml(param.value9) eq publisherName) ? 'selected="selected"' : ''} value="${publisherName}">${publisher.properties.dcterms_title}</option>
        </c:forEach>
    </select>
</div>
