<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>

<%@ attribute name="savedSearchFolder" required="false" type="java.lang.Object" description="The Folder" %>
<%@ attribute name="isDefaultFolder" required="false" type="java.lang.Boolean" description="Indicates we are processing the default folder" %>
<%@ attribute name="defaultFolderExists" required="false" type="java.lang.Boolean" description="Indicates there are no individual items" %>
<%@ attribute name="defaultFolderName" required="false" type="java.lang.String" description="The name of the default folder" %>

	<li class="savedSearchFolder ${isDefaultFolder?'default':''}">
		<div class="folderDetails">
			<div class="itemContent">
				<img src="/images/jp/closedfolder.gif" alt="Folder" class="closedFolderButton folderButton" /><img src="/images/jp/openfolder.gif" alt="Folder" class="openFolderButton folderButton" /> <span class="folderName">${isDefaultFolder?'_default':savedSearchFolder.name}</span>
				<c:if test="${savedSearchFolder.name != skin.defaultSavedSearchFolderName}">
					<input class="newName" name="editName" type="text" />
				</c:if>
			</div>
			<div class="itemAction alertAction">&nbsp;</div>
			<div class="itemAction deleteAction">&nbsp;</div>
			<div class="itemAction deleteAction"><img src="/images/jp/delete.gif" alt="Delete" border="0" class="deleteFolder" /></div>
			<div class="clearer">&nbsp;</div>
		</div>

		<ul class="savedSearchWrapper">

			<c:if test="${(!isDefaultFolder) || (isDefaultFolder && defaultFolderExists)}">
				<search:savedSearchItems savedSearches="${savedSearchFolder.searches}" />
			</c:if>
		</ul>
	</li>
