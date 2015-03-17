<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>

<%@ attribute name="searchExplanations" type="java.util.List" description="The list of explanation text objects" %>

            <c:if test="${empty niceNames}">
                <search:niceNames />
            </c:if>

            <c:forEach items="${searchExplanations}" var="searchExplanation" varStatus="status">
                <c:if test="${!status.first}"><br />AND<br /></c:if>                
                <c:forEach items="${searchExplanation}" var="searchExplanationText" varStatus="innerStatus">
                    <c:if test="${searchExplanationText.prettyFieldName eq 'year_from'}">
                        <c:set var="yearFrom" value="${fn:trim(searchExplanationText.searchTerm)}" scope="request" />
                    </c:if>
                    <c:if test="${searchExplanationText.prettyFieldName eq 'year_to'}">
                        <c:set var="yearTo" value="${fn:trim(searchExplanationText.searchTerm)}" scope="request" />
                    </c:if>
                    <c:if test="${searchExplanationText.prettyFieldName eq 'date_from'}">
                        <c:set var="dateFrom" value="${fn:trim(searchExplanationText.searchTerm)}" scope="request" />
                    </c:if>
                    <c:if test="${searchExplanationText.prettyFieldName eq 'date_to'}">
                        <c:set var="dateTo" value="${fn:trim(searchExplanationText.searchTerm)}" scope="request" />
                    </c:if>
                
                    <c:choose>
                        <c:when test="${(searchExplanationText.fieldName == 'acs_parents') and (not empty param.searchWithin)}">
                            <s:message key="within" pageAgnostic="true" editable="true">within</s:message> &lsquo;<c:out value="${param.searchWithin}" />&rsquo;
                        </c:when>
                        <c:otherwise>                   
                            <c:if test="${not ((searchExplanationText.prettyFieldName eq 'year_from') || (searchExplanationText.prettyFieldName eq 'year_to') ||
                                     (searchExplanationText.prettyFieldName eq 'date_from') || (searchExplanationText.prettyFieldName eq 'date_to') )}">
                                <c:if test="${!innerStatus.first && searchExplanationText.operator != null}"><b>${searchExplanationText.prettyOperator}</b></c:if> 
                                (<s:message key="${niceNames[searchExplanationText.prettyFieldName]}" pageAgnostic="true" editable="true">${niceNames[searchExplanationText.prettyFieldName]}</s:message>
                                <s:message key="${niceNames[searchExplanationText.prettyPredicate]}" pageAgnostic="true" editable="true">${niceNames[searchExplanationText.prettyPredicate]} </s:message>
                                <search:checkLabelledFacet constraintName="${searchExplanationText.prettyFieldName}" />
                                <c:set var="termId">${skin.metastoreprefix}/content/${searchExplanationText.searchTerm}</c:set>
                                <c:choose>
                                    <c:when test="${(isLabelledFacet) and (not empty niceTermNames) and (not empty niceTermNames[termId])}">
                                        &lsquo;${niceTermNames[termId].properties.rdfs_label}&rsquo;)
                                    </c:when>
                                    <c:when test="${not empty fn:trim(niceNames[searchExplanationText.searchTerm])}">
                                        &lsquo;<span class="${searchExplanationText.searchTerm}">${fn:trim(niceNames[searchExplanationText.searchTerm])}</span>&rsquo;)
                                    </c:when>      
                                    <c:otherwise>
                                        &lsquo;${fn:trim(searchExplanationText.searchTerm)}&rsquo;)
                                    </c:otherwise>
                                </c:choose>
                            </c:if>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
                    <c:if test="${(not empty yearFrom) and (not empty yearTo)}">
                        <s:message key="published_between" pageAgnostic="true" editable="true">published between</s:message> ${yearFrom} <s:message key="and" pageAgnostic="true" editable="true">and</s:message> ${yearTo}
                    </c:if>
                    <c:if test="${(not empty dateFrom) and (not empty dateTo)}">
                        <s:message key="published_between" pageAgnostic="true" editable="true">published between</s:message> ${dateFrom} <s:message key="and" pageAgnostic="true" editable="true">and</s:message> ${dateTo}
                    </c:if>
                    <c:if test="${not empty param.subscribed}">
                        <b><a href="<c:url value='/subscribedtitles'/>"><s:message key="within_subscribed_titles" pageAgnostic="true" editable="true">within the publications that you subscribe to</s:message></a></b>
                    </c:if>
                    
            </c:forEach>
