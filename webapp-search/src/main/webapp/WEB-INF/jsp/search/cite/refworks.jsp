<%@ include file="/WEB-INF/jspf/taglibs.jspf" %><%-- 
--%><%@ taglib tagdir="/WEB-INF/tags/models/journal/article/cite" prefix="article" %><%--
--%><%@ taglib tagdir="/WEB-INF/tags/models/book/book/cite" prefix="book" %><%--
--%><%@ taglib tagdir="/WEB-INF/tags/models/book/chapter/cite" prefix="chapter" %><%--
--%><%@ page contentType="application/x-refworks;charset=UTF-8" %><%--
--%><cu:attachment name="search-results" ext="ref"/><%--
--%><c:forEach var="item" items="${listings}">
<c:choose><%--
   --%><c:when test="${cu:isA(item, 'http://pub2web.metastore.ingenta.com/ns/Article')}"><%--
      --%><article:refworks item="${item}"/><%--
   --%></c:when><%--
   --%><c:when test="${cu:isA(item, 'http://pub2web.metastore.ingenta.com/ns/Book')}"><%--
      --%><book:refworks item="${item}"/><%--
   --%></c:when><%--
   --%><c:when test="${cu:isA(item, 'http://pub2web.metastore.ingenta.com/ns/Chapter')}"><%--
      --%><chapter:refworks item="${item}"/><%--
   --%></c:when><%--
--%></c:choose>
</c:forEach>
