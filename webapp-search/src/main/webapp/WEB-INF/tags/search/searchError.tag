<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>

	<c:if test="${not empty error}">
		<c:set var="invalidsearch" value="${true}" />
			<div class="searchError">
				${error}
			</div>
	</c:if>
