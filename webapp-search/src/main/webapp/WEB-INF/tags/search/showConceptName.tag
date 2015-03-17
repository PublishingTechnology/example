<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>
<%@ attribute name="concept" type="com.ingenta.facet.content.ContentItem" required="true" description="The concept whose name is to be shown" %>

		<%-- c:choose>
			<c:when test="${not empty concept.properties.dcterms_title}">
				<c:set var="itemname" value="${concept.properties.dcterms_title}"/>   								
			</c:when>
			<c:when test="${not empty concept.properties.dc_title}">
				<c:set var="itemname" value="${concept.properties.dc_title}"/>
			</c:when>
			<c:when test="${not empty concept.properties.pub_seeAlsoLabel}">
				<c:set var="itemname" value="${concept.properties.pub_seeAlsoLabel}"/>
			</c:when>
			<c:otherwise>
				<c:set var="itemname" value="${concept.properties.rdfs_label}"/>
			</c:otherwise>
		</c:choose --%>
		${concept.properties.rdfs_label}

