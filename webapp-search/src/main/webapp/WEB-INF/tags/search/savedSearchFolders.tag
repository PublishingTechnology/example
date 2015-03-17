<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>

<%@ attribute name="savedSearchFolders" required="true" type="java.util.List" description="The list of saved search folders" %>
<%@ attribute name="defaultFolderName" required="true" type="java.lang.String" description="The name of the default folder where the individual items are stored" %>
                  
	<c:forEach items="${savedSearchFolders}" var="folder" varStatus="status">                       	  	
		<c:if test="${folder.name != defaultFolderName}">
			<search:savedSearchFolder savedSearchFolder="${folder}" />
		</c:if>
	</c:forEach>
					   	  
