<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>
<%@ attribute name="facetTitle" type="java.lang.String" required="false" description="Display a title for the facets" %>

<c:set var="maxItemsToDisplay" value="${skin.maxItemsToDisplay }"/>
<c:set var="maxFacetCounter" value="${skin.maxItemsToDisplay - 1}"/>

<c:if test="${not empty searchResult.facets}">

    <c:if test="${empty niceNames}">
        <search:niceNames />
    </c:if>

    <c:set var="facet_ids" value="" />
    <c:forEach var="facetlist" items="${searchResult.facets}" varStatus="status">
        <c:if test="${(not empty niceNames[facetlist.key]) and (not empty facetlist.value) and (fn:length(facetlist.value.facetItems)>0)}">
            <search:checkLabelledFacet constraintName="${facetlist.key}" />
            <c:if test="${isLabelledFacet}">
                <c:if test="${not empty facet_ids}"><c:set var="facet_ids">${facet_ids},</c:set></c:if>
                <c:set var="facetidcounter" value="0"/>
                <c:forEach var="facet" items="${facetlist.value.facetItems}" varStatus="status">
                    <c:if test="${(facet.values ne '[0]') && (facetidcounter <= maxFacetCounter)}">
                        <c:set var="facet_ids">${facet_ids}${skin.metastoreprefix}/content/${facet.name}${status.last?'':','}</c:set>
                        <c:set var="facetidcounter" value="${facetidcounter + 1}"/>
                    </c:if>
                </c:forEach>
            </c:if>
        </c:if>
    </c:forEach>

    <c:if test="${not empty facet_ids}">
        <facet:itemList lens="http://pub2web.metastore.ingenta.com/views/item-label-view" item="${facet_ids}" />
        <c:set var="niceTermNames" value="${itemtag}" scope="request" />
    </c:if>

    <div class="facets">
        <c:if test="${not empty facetTitle}">
            <h2>${facetTitle}</h2>
        </c:if>
        <c:forEach var="facetlist" items="${searchResult.facets}" varStatus="status">
            <c:set var="needsAhah" value="${false}" />
            <c:if test="${(not empty niceNames[facetlist.key]) and (not empty facetlist.value) and (fn:length(facetlist.value.facetItems)>0)}">
                <search:checkLabelledFacet constraintName="${facetlist.key}" />
                <c:if test="${(isLabelledFacet) and (fn:length(facetlist.value.facetItems)>maxItemsToDisplay)}">
                    <c:set var="needsAhah" value="${true}" />
                </c:if>

                <h3><s:message key="facetFilterBy" pageAgnostic="true" editable="true">Filter by </s:message><s:message key="facetFilterBy_${fn:toLowerCase(niceNames[facetlist.key])}" pageAgnostic="true" editable="true">${fn:toLowerCase(niceNames[facetlist.key])}:</s:message></h3>

                <c:if test="${fn:contains(param.facetNames, facetlist.key)}">
                    <p><jp:link to="${facetlist.key}" class="anysearchfacetlink">&lt; Any ${fn:toLowerCase(niceNames[facetlist.key])}</jp:link></p>
                </c:if>

                <c:set var="facetUrl"><search:facetUrl facetname="${facetlist.key}" />&amp;operator${lastOption + 1}=AND&amp;option${lastOption + 1}=${facetlist.key}&amp;value${lastOption + 1}=</c:set>

                <ul>
                    <c:set var="facetcounter" value="0"/>
                    <c:set var="ahahids" value="" />
                    <c:set var="ahahnames" value="" />
                    <c:set var="ahahvalues" value="" />
                    <c:forEach var="facet" items="${facetlist.value.facetItems}" varStatus="status">
                        <c:if test="${facet.values ne '[0]'}">
                            <c:choose>
                                <c:when test="${isLabelledFacet}">
                                    <c:set var="facetId" value="${skin.metastoreprefix}/content/${facet.name}"/>
                                    <c:set var="facetValues" value="${itemtag[facetId]}"/>
                                    <c:set var="facetLabel" value="${facetValues.properties.rdfs_label}" />
                                </c:when>
                                <c:otherwise>
                                    <c:set var="facetLabel" value="${facet.name}" />
                                </c:otherwise>
                            </c:choose>
                            <c:choose>
                                <c:when test="${(needsAhah) and (facetcounter > maxItemsToDisplay)}">
                                    <c:set var="ahahids">${ahahids}${empty ahahids ? '' : ','}${facetId}</c:set>
                                    <c:set var="ahahnames">${ahahnames}${empty ahahnames ? '' : ','}${facet.name}</c:set>
                                    <c:set var="ahahvalues">${ahahvalues}${empty ahahvalues ? '' : ','}${facet.values}</c:set>
                                </c:when>
                                <c:otherwise>
                                    <li class="facetitem${facetcounter<=maxFacetCounter ? '' : ' hidden'} facet_${facetlist.key}_${fn:replace(facet.name,' ','')}">
                                        <%-- <c:set var="thisFacetUrl">${facetUrl}${skin.doubleQuoteFacetValue}${facet.name}${skin.doubleQuoteFacetValue}</c:set> --%>
                                        <c:set var="customFacetLabel" value="facetLabel${facetLabel}"/>
                                        <c:set var="customFacetLabelValue" value="${skin[customFacetLabel]}" />
                                        <c:set var="alreadyclicked" value="${false}" />
                                        <c:set var="labeltouse"><s:message key="facetLabel_${facetLabel}" pageAgnostic="true" editable="true"><c:out value="${not empty customFacetLabelValue ? customFacetLabelValue : facetLabel}" /></s:message></c:set>
                                        <c:forEach var="p" items="${param}">
                                            <c:set var="unescaped_param"><c:out value="${p.value}" /></c:set>
                                            <c:if test="${fn:trim(unescaped_param) == fn:trim(facet.name)}">
                                                <c:set var="alreadyclicked" value="${true}" />
                                            </c:if>
                                        </c:forEach>
                                        <c:choose>
                                            <c:when test="${not alreadyclicked}">
                                                <c:set var="newValue" value="'${facet.name}'" />
                                                <c:set var="encodednewValue"><%= java.net.URLEncoder.encode(jspContext.getAttribute("newValue").toString()) %></c:set>

                                                <a href="${facetUrl}${encodednewValue}" title="">
                                                    ${labeltouse} ${facet.values}
                                                </a>
                                            </c:when>
                                            <c:otherwise>
                                                ${labeltouse} ${facet.values}
                                            </c:otherwise>
                                        </c:choose>
                                    </li>
                                </c:otherwise>
                            </c:choose>
                            <c:set var="facetcounter" value="${facetcounter + 1}"/>
                        </c:if>
                    </c:forEach>
                    <c:if test="${isLabelledFacet and needsAhah}">
                        <li class="ahahids hidden">${ahahids}</li>
                        <li class="ahahnames hidden">${ahahnames}</li>
                        <li class="ahahvalues hidden">${ahahvalues}</li>
                        <li class="ahahurl hidden"><c:url value="/search/morefacet" /></li>
                        <li class="ahahsearchurl hidden">${facetUrl}</li>
                    </c:if>
                    <c:if test="${(facetcounter > maxFacetCounter) and (fn:length(facetlist.value.facetItems) > maxItemsToDisplay)}">
                        <li class="${needsAhah ? 'seeAhahFacetsLink' : 'seeMoreFacetsLink'}">
                              <jp:link to="#"><s:message key="facetSeeMore" pageAgnostic="true" editable="true">See more...</s:message></jp:link>
                              <jp:link to="#" class="facethidetext hiddenprop"><s:message key="facetHide" pageAgnostic="true" editable="true">Hide...</s:message></jp:link>
                           </li>
                    </c:if>
                </ul>
            </c:if>
        </c:forEach>
    </div>

</c:if>