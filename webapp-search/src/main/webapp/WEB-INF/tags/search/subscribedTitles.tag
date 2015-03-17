<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>
	<c:if test="${(fn:length(subscribedTitles) > 0)}">
		<div class="contain">
			<c:if test="${empty param.subscribed}"><a href="<search:subscribedSearchToggle />" title="<s:message key="repeat_in_subscribed_titles" pageAgnostic="true" editable="false">Repeat this search in subscribed titles</s:message>" class="btn-orange styledbutton"><span><s:message key="repeat_in_subscribed_titles" pageAgnostic="true" editable="true">Repeat this search in subscribed titles</s:message></span></a></c:if>
			<c:if test="${not empty param.subscribed}"><a href="<search:subscribedSearchToggle />" title="Repeat this search in ALL titles" class="btn-orange styledbutton"><span><s:message key="repeat_in_all_titles" pageAgnostic="true" editable="true">Repeat this search in ALL titles</s:message></span></a></c:if>
		</div>
	</c:if>
