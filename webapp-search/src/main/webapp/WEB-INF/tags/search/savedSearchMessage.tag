<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>
<div class="hide savedsearchsuccess" id="savedSearchMessage">
   <s:message key="search_addded_to" global="true" editable="true">This search has been added to your</s:message>
   <jp:link to="/savedsearches?action=savedSearches">
      <s:message key="saved_search_list" global="true" editable="true">saved search list</s:message>
   </jp:link>
   <jp:link to="/savedsearches?action=delete&ajax=true&searchId=0" id="savedSearchUndoLink">
      <s:message key="undo_question" global="true" editable="true">(Undo?)</s:message>
   </jp:link> 
</div>
