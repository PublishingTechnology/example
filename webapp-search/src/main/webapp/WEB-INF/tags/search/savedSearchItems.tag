<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>

<%@ attribute name="savedSearches" required="true" type="java.util.List" description="The list of saved searches" %>

 	<c:forEach items="${savedSearches}" var="savedSearch" varStatus="status">            	  	
		<li class="savedSearchContent ${status.count % 2 == 0 ? 'odd' : 'even'}">
			<div class="itemContent">
				<div class="savedsearchtitle"><span class="searchDetail hide" id="id">${savedSearch.identifier}</span><span class="savedsearchitemtitle"><s:message key="searchitemtitle" pageAgnostic="true" editable="true">Title:</s:message></span> 
				<span class="searchDetail" id="title"><search:termsFromExplanation  searchExplanations="${savedSearch.search.searchExplanation}" /></span></div>
					<div class="searchDetailbreakdown"><search:explanationTextProcessor searchExplanations="${savedSearch.search.searchExplanation}" /></div>
					<div class="searchDetailbreakdown">(<s:message key="runon" pageAgnostic="true" editable="true">run on</s:message> <span class="searchDetail" id="searchDate">${savedSearch.lastRunOn}</span>)</div>
			</div>
			<c:choose>			
				<c:when test="${savedSearch.alerted eq 'true' }">
					<c:set var="alerted" value="checked"/>
				</c:when>
				<c:otherwise>
					<c:set var="alerted" value=""/>
				</c:otherwise>
			</c:choose>
			<div class="itemAction alertAction"><input type="checkbox" name="alert" class="alertCheck" ${alerted}/></div>
			<div class="itemAction moveAction">                       
				<a class="addFolderButton" href="#"><img src="/images/jp/movetofolder.gif" alt="Move"  border="0" class="move" /></a>
				<input type="hidden" name="divname" value="folderChanger${savedSearch.folderName}${status.count}" class="divname" />
				<div id="folderChanger${savedSearch.folderName}${status.count}" class="folderChangerPopup" style="display:none; position:absolute; border:1px solid black; background-color: white; padding:5px; text-align:left;">
               <div><span class="searchDetail hide" id="id">${savedSearch.identifier}</span><s:message key="searchitemtitle" pageAgnostic="true" editable="true">Title:</s:message> <span class="searchDetail" id="title"><search:termsFromExplanation  searchExplanations="${savedSearch.search.searchExplanation}" /></span></div>
               <div><search:explanationTextProcessor searchExplanations="${savedSearch.search.searchExplanation}" /></div>
               <div>(<s:message key="runon" pageAgnostic="true" editable="true">run on</s:message> <span class="searchDetail" id="searchDate">${savedSearch.lastRunOn}</span>)</div>
					<div class="folderChangerDiv">
					</div>
					<span class="fleft"><a href="#" class="createNewFolderButton"><s:message key="createnewfolder" pageAgnostic="true" editable="true">Create new folder</s:message></a></span>
					<span class="fright"><a onClick="HideContent(this); return true;" href="#">[hide]</a></span>
				</div>                       		
			</div>
			<div class="itemAction deleteAction"><img src="/images/jp/delete.gif" alt="Delete"  border="0" class="delete" /></div>
			<div class="clearer">&nbsp;</div>
			<div class="itemLinks"><jp:link to="/runsavedsearch?ssid=${savedSearch.identifier}" class="searchLink"><s:message key="showresults" pageAgnostic="true" editable="true">show results</s:message></jp:link><%-- &nbsp;&nbsp;  <jp:link to="/mock/notimplemented" class="searchLink">search for updates</jp:link>&nbsp;&nbsp;<jp:link to="/mock/notimplemented" class="searchLink">modify search</jp:link> --%></div>
		</li>
	</c:forEach>
					   	  
