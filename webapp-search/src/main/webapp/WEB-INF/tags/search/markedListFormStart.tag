<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>
		<c:set var="currurl" value="${currentRequest.originalRequest.targetURI}"/>
		<div class="updatemarkedlist">
			<input type="hidden" name="currentUrl" value="${fn:replace(currurl,'&','&amp;')}"/>                 
			<jp:link to="#"  title="Update My Marked List" class="btn-orange markedList"  ><span>Update My Marked List</span></jp:link>
		</div>
