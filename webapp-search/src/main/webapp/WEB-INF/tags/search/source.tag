<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>

<%@ attribute name="result" type="java.lang.Object" required="true" description="The search result item" %>
<%@ attribute name="storeResult" type="com.ingenta.facet.content.ContentItem" required="false" description="The corresponding content item from the facet call" %>

<c:set var="parent" value="${storeResult.properties.dcterms_isPartOf}" />
<c:if test="${not empty parent}">
   <div class="source">
      <span class="label"><s:message key="source_label" pageAgnostic="true" editable="true">Source</s:message></span><span class="sourceLabelColon">:</span> 
      <c:choose>
         <c:when test="${not empty result.properties.pub_serialTitle}">
            <c:forEach items="${result.properties.pub_serialTitle}" var="journal">                           
               <c:choose>
                  <c:when test="${not empty parent.properties.dcterms_isPartOf}" >
                     <%-- If we're a book then our parent's parent is an index of all publishers, we don't want to show that so we check if it a user visible type --%>
                     <c:set var="type"><cu:formatContentType contentTypes="${parent.properties.dcterms_isPartOf.properties['rdf_type']}"/></c:set>
                     <c:if test="${!empty type}" >
                        <jp:link to="${parent.properties.dcterms_isPartOf.webId}" test="${not empty parent.properties.dcterms_isPartOf.webId}">${journal}</jp:link>,
                     </c:if>
                     <jp:link to="${parent.webId}">${parent.properties.dcterms_title}</jp:link>
                  </c:when>
                  <c:otherwise>
                     <jp:link to="${parent.webId}">${journal}</jp:link>
                     <c:if test="${fn:length(result.properties.prism_volume) > 0}">                          
                        <c:forEach items="${result.properties.prism_volume}" var="volume">
                           <s:message key="volume_label" pageAgnostic="true" editable="true">Volume</s:message> ${volume},
                        </c:forEach>
                     </c:if>
                     <c:if test="${fn:length(result.properties.prism_number) > 0}">                          
                        <c:forEach items="${result.properties.prism_number}" var="issue">
                           <s:message key="issue_label" pageAgnostic="true" editable="true">Issue</s:message> ${issue},
                        </c:forEach>
                     </c:if>
                  </c:otherwise>
               </c:choose>                              
            </c:forEach>
            <span class="sourcePublicationDate"><search:sourcePublicationDate result="${result}" parentItem="${parent}" /></span>
            <c:if test="${fn:length(result.properties.pages) > 0}">                     
               <c:forEach items="${result.properties.pages}" var="pages">
                  <s:message key="pp" pageAgnostic="true" editable="true">pp</s:message> ${pages},
               </c:forEach>
            </c:if>
            <c:if test="${(fn:length(result.properties.prism_startingPage) > 0) or (fn:length(result.properties.prism_endingPage) > 0)}">                     
               <s:message key="page_label_plural" pageAgnostic="true" editable="true">pages</s:message> 
               <c:forEach items="${result.properties.prism_startingPage}" var="startingPage">
                  ${startingPage} -
               </c:forEach>
               <c:forEach items="${result.properties.prism_endingPage}" var="endingPage">
                  ${endingPage}
               </c:forEach>                        
            </c:if>
         </c:when>
         <c:otherwise>
            <jp:link to="${parent.webId}">${parent.properties.dcterms_title}</jp:link>
         </c:otherwise>
      </c:choose>
   </div>                        
</c:if>