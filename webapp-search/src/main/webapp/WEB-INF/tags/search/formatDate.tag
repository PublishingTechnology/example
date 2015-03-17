<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ attribute name="date" type="java.lang.String" required="false" description="" %>
<%@ attribute name="pattern" type="java.lang.String" required="false" description="" %>

<c:catch var="dateError">
	<fmt:parseDate value="${date}" pattern="yyyyMMdd" type="both" var="pubDate" dateStyle="short"/>
	<c:set var="pattern" value="${not empty pattern ? pattern : 'MMMM yyyy'}" />
	<fmt:formatDate value="${pubDate}" pattern="${pattern}" var="displayDate"/>
	${displayDate}
</c:catch>
<c:if test="${not empty dateError}">
	<!-- error: ${dateError} -->
</c:if>