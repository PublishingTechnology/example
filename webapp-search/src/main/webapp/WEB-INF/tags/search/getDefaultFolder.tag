<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>

<%@ attribute name="savedSearchFolders" required="true" type="java.util.List" description="The list of saved search folders" %>
<%@ attribute name="defaultFolderName" required="true" type="java.lang.String" description="The name of the default folder where the individual items are stored" %>

	<c:set var="foundDefaultFolder" value="${false}" scope="request" />

                   
	<c:forEach items="${savedSearchFolders}" var="folder" varStatus="status">                       	  	
		<c:if test="${folder.name == defaultFolderName}">
			<c:set var="defaultFolder" value="${folder}" scope="request" />
			<c:set var="foundDefaultFolder" value="${true}" scope="request" />
		</c:if>
	</c:forEach>
					   	  
