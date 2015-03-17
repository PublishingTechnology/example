<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>

<%@ attribute name="savedSearchFolders" type="java.util.List" description="The list of saved search folders" %>

	<c:choose>
		<c:when test="${fn:length(savedSearchFolders) == 0}">
			<p><s:message key="nosavedsearch" pageAgnostic="true" editable="true">You currently have no saved searches. Searches can be saved via the search history tab above.</s:message></p>
		</c:when>
		<c:otherwise>
		    <label class="deletewarning">In order to delete that item permanently you'll need to save your changes before leaving this page.</label>
			<search:getDefaultFolder savedSearchFolders="${savedSearchFolders}" defaultFolderName="_default" />
	
			<%-- ${foundDefaultFolder} --%>
		
			<form id="manageSavedSearches" action="<c:url value="/managesavedsearches" />" >
		
				<div class="sscontrol">
					<div class="sssuccess"><s:message key="updatesavedsuccessfully" pageAgnostic="true" editable="true">Your updates were saved successfully</s:message></div><div style="text-align:right; margin-bottom:10px float:right;"><jp:link to="#" title="Save changes" class="btn-orange saveChanges" id="saveSearches"><span><s:message key="savechangesbutton" pageAgnostic="true" editable="true">Save changes</s:message></span></jp:link></div>
					<div class="clearer">&nbsp;</div>
				</div>
				
				<c:set var="individualItems"><s:message key="individualItems" pageAgnostic="true" editable="true">Individual Items</s:message></c:set>

				<div class="savedSearchHeadings">
					<div class="itemContent">${foundDefaultFolder?individualItems:'&nbsp;'}</div>
					<div class="itemAction alertAction"><s:message key="receiveemailalert" pageAgnostic="true" editable="true">Receive email alert?</s:message></div>
					<div class="itemAction deleteAction"><s:message key="addmoveitemtofolder" pageAgnostic="true" editable="true">Add/move item to folder</s:message></div>
					<div class="itemAction deleteAction"><s:message key="deleteItemorFolder" pageAgnostic="true" editable="true">Delete Item or Folder</s:message></div>
					<div class="clearer">&nbsp;</div>
				</div>

				<ul class="savedSearchFolders">

					<c:choose>
						<c:when test="${foundDefaultFolder}">
							<search:savedSearchFolder savedSearchFolder="${defaultFolder}" isDefaultFolder="${true}" defaultFolderExists="${true}" defaultFolderName="_default" />
						</c:when>
						<c:otherwise>
							<search:savedSearchFolder isDefaultFolder="${true}" defaultFolderExists="${false}" defaultFolderName="_default" />
						</c:otherwise>
					</c:choose>			
		
					<search:savedSearchFolders savedSearchFolders="${savedSearchFolders}" defaultFolderName="_default" />
		
				</ul>
		
				<div class="sscontrol">
					<div class="sssuccess"><s:message key="updatesavedsuccessfully" pageAgnostic="true" editable="true">Your updates were saved successfully</s:message></div><div style="text-align:right; margin-bottom:10px float:right;"><jp:link to="#" title="Save changes" class="btn-orange saveChanges" id="saveSearches"><span><s:message key="savechangesbutton" pageAgnostic="true" editable="true">Save changes</s:message></span></jp:link></div>
					<div class="clearer">$nbsp;</div>
				</div>

			</form>
		</c:otherwise>
	</c:choose>