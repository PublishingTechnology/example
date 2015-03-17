<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>

<%@ attribute name="savedSearchFolders" type="java.util.List" description="The existing saved search folders" %>

	<div class="searchHistoryControls">		
	   <div class="folderSelector">
			<select name="searchFolder" class="savedSearchFolderSelector">
				<option value="_unknown"><s:message key="savedsearchesSave" pageAgnostic="true" editable="false">Save ...</s:message></option>
				<option value="_default"><s:message key="savedsearchesIndividualItem" pageAgnostic="true" editable="false">as individual item</s:message></option>
				<c:forEach items="${savedSearchFolders}" var="folder">
					<c:if test="${folder.name != '_default'}">
						<option value="${folder.name}"><s:message key="savedsearchesto" pageAgnostic="true" editable="false">to</s:message> ${folder.name}</option>
					</c:if>
				</c:forEach>
				<option value="_new"><s:message key="savedsearchesNewFolderspecify" pageAgnostic="true" editable="false">to a new folder</s:message></option>
			</select>
		</div>
		<div class="searchgo" >
		<jp:link to="#" class="btn-orange savedSearchGo styledbutton" ><span><s:message key="savedsearchesgo" pageAgnostic="true" editable="true">go</s:message></span></jp:link>
		</div>		
	</div>
	<div class="clearer"></div>

	
