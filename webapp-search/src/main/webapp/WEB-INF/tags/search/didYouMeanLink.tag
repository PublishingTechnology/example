<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>
<%@ attribute name="to" type="java.lang.String" required="true" description="The link" %>
<%@ attribute name="title" type="java.lang.String" required="true" description="The name of the link" %>


<h3>
	<jp:link to="${to}" escape="${false}"><s:message key="did_you_mean" pageAgnostic="true" editable="true">Did you mean</s:message>
			${title}				
	<s:message key="did_you_mean_?" pageAgnostic="true" editable="true">?</s:message></jp:link>
</h3>