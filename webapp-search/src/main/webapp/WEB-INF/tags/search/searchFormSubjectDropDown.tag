<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>

<%@ attribute name="item" type="java.lang.Object" required="true" description="The content item"%>

<div class="searchWithinInput">
    <div class="searchWithinInputLabel"><s:message key="thissubject" pageAgnostic="true" editable="true">This subject:</s:message></div>
    <input type="hidden" name="operator6" value="AND" />
    <input type="hidden" name="option6" value="subjectarea" />
    <select name="value6" id="subjectarea"  ${skin.searchMultipleSelect}  ${skin.searchMultipleSelectSize}>
        <option value=""><s:message key="searchallsubjects" pageAgnostic="true" editable="true">All Subjects</s:message></option>
        <c:forEach var="subject" items="${subjectcollection}" varStatus="status">
            <c:set var="subjectName" value="${fn:replace(subject.webId, '/content/subject/', '')}" />
            <option ${(fn:escapeXml(param.value6) eq subjectName) ? 'selected="selected"' : ''} value="${subjectName}">${fn:replace(subject.properties.dcterms_title, '&', '&amp;')}</option>
        </c:forEach>
    </select>
</div>
