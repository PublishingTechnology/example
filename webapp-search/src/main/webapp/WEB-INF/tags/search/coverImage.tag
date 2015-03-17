<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>

<%@ attribute name="item" type="java.lang.Object" required="false" description="The content item for which the cover image is to be shown" %>
<%@ attribute name="cssClass" type="java.lang.String" required="false" description="CSS class to be assigned to the image tag"%>

            <c:choose>
                <c:when test="${not empty item.properties.pub_coverImage}">
                    <c:set var="coverImagePath" value="${skin.defaultCoverImagePath}${item.properties.pub_coverImage}" />
                </c:when>
                <c:when test="${not empty item.properties.pub_parentCoverImage}">
                    <c:set var="coverImagePath" value="${skin.defaultCoverImagePath}${item.properties.pub_parentCoverImage}" />
                 </c:when>
                <c:when test="${not empty item.properties.pub_grandparentCoverImage}">
                    <c:set var="coverImagePath" value="${skin.defaultCoverImagePath}${item.properties.pub_grandparentCoverImage}" />
                 </c:when>
            </c:choose>

            <c:choose>
                <c:when test="${not empty coverImagePath}">
                    <img class="cover ${cssClass}" src="${coverImagePath}" alt="image of <c:out value="${item.properties.dcterms_title}"/>" title="image of <c:out value="${item.properties.dcterms_title}"/>" />
                </c:when>
                <c:otherwise>
                    <jp:_helpers name="coverImagePlaceholder" item="${item}" class="${cssClass}" alt="Cover Image Placeholder" />
                </c:otherwise>
            </c:choose>

