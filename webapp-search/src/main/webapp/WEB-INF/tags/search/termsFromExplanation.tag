<%@ include file="/WEB-INF/jspf/taglibs.jspf" %><%--


--%><%@ attribute name="searchExplanations" type="java.util.List" description="The list of explanation text objects" %><%--


			--%><c:forEach items="${searchExplanations}" var="searchExplanation" varStatus="status"><%--
				--%><c:set var="firstTerm" value="${true}" /><%--		
				--%><c:forEach items="${searchExplanation}" var="searchExplanationText"><%--				
				    --%><c:if test="${not ((searchExplanationText.prettyFieldName eq 'year_from') || (searchExplanationText.prettyFieldName eq 'year_to') || (searchExplanationText.prettyFieldName eq ''))}"><%--
						--%><c:if test="${not firstTerm}">, </c:if>${fn:trim(searchExplanationText.searchTerm)}<%--
						--%><c:set var="firstTerm" value="${false}" /><%--								
					--%></c:if><%--
				--%></c:forEach><%--
			--%></c:forEach><%--
--%>