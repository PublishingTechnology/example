<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>

<c:set var="pageTitle"><s:message key="searchresultspagetitle" pageAgnostic="true" editable="true">Search Results</s:message></c:set>

<p:body title="${pageTitle}" page="search.searchResults" stylesheets="rummage" id="searchresultspage" >

	<search:searchError />	
 
 	<search:didYouMean />	
 	 	
	<%-- Call showFacets and store output for later use, in case explanation text needs the facet labels --%>
	<c:set var="facetsoutput"><search:showFacets /></c:set>

	<c:choose>
		<c:when test="${searchResult.totalCount == 0}">
			<search:explanationText />
			<div id="searchContent">			
		    <stats:log eventType="SEARCH_INVALID_SEARCH" />
		    <c:set var="invalidsearch" value="${true}" />
			<%@ include file="/WEB-INF/jspf/search/search_form.jspf" %>
			</div>
		</c:when>
		<c:otherwise>

			<div class="resultsbuttons">
				<c:if test="${empty param.refined}"><a href="<search:modifySearchParams href="/search/advancedsearch" />" title="<s:message key="modify_this_search" pageAgnostic="true" editable="false">Modify this search</s:message>"><span><s:message key="modify_this_search" pageAgnostic="true" editable="true">Modify this search</s:message></span></a></c:if>
				<c:if test="${skin.showRelatedDatabasesLink}">
					<br /><a href="<search:modifySearchParams href="/search/thirdparty/arxiv" />" title="<s:message key="related_database_search" pageAgnostic="true" editable="false">Search within Related databases</s:message>"><span><s:message key="related_database_search" pageAgnostic="true" editable="true">Search within Related databases</s:message></span></a>
				</c:if>
			</div>
         
         <search:savedSearchMessage />

			<search:subscribedTitles />			
					
			<search:explanationText />
		<div class="browsecontent">
		 	${facetsoutput}	
			
			<div id="searchContent" class="publistwrapper${empty searchResult.facets ? 'nofacetmargin' : ''}">

				<search:showHideAbstract />								
				<search:sortBy />
            
            <c:forEach var="result" items="${searchResult.results}" varStatus="status">
               <c:set var="ids" value="${ids}${result.identifier}${status.last?'':','}" />
               <c:set var="inList">${marklistutilities:isItemInMarkedList(markedList,result.identifier)}  </c:set>
               <c:if test="${inList}"><c:set var="markedIds" value="${markedIds}${result.identifier}${status.last?'':','}" /> </c:if>
            </c:forEach>
            
            <input type="hidden" name="markedIds" value="${markedIds}"/>
            
            <ul class="flat separated-list paginated">
               <c:set var="oddoreven" value="odd" />
               <auth:licence resourceCollection="${listings}" varStatus="status">
                  <c:set var="count" value="${status.count}"/>
                  <c:remove var="item"/>
                  <c:set var="item" value="${listings[status.count - 1]}"/>
                  
                  <li class="${oddoreven}">
                     <jp:browseItem item="${item}"
                           showFullText="${not empty allLicences and allLicences[0].type == 'FREE'}"
                           showType="${true}"
                      />
                  </li>
                  <c:set var="oddoreven" value="${oddoreven eq 'odd' ? 'even' : 'odd'}" />
               </auth:licence>
            </ul>
            
            <search:pager includePerPage="${true}" />
            
            <script type="text/javascript" charset="utf-8">
				/* <![CDATA[ */
			        $(document).ready(function() {
			            $("#sortRelevance").click(function() {
			            	$("#sortForm input").each(function(){
			            		if(($(this).attr("name") == "sortDescending") || ($(this).attr("name") == "sortField"))
			            			$(this).remove();
			            	});
			                $("#sortForm").submit();
			            });
			            $("#sortOldest").click(function() {
			                if ($("input[name=sortDescending]").size() == 0)
			                    $("#sortForm").append("<input type='hidden' name='sortDescending' value='false' />");
			                else
			                    $("input[name=sortDescending]").val('false');
		                    $("#sortForm").append("<input type='hidden' name='sortField' value='prism_publicationDate' />");
			                $("#sortForm").submit();
			                return false;
			            });
			            $("#sortNewest").click(function() {
			                if ($("input[name=sortDescending]").size() == 0)
			                    $("#sortForm").append("<input type='hidden' name='sortDescending' value='true' />");
			                else
			                    $("input[name=sortDescending]").val('true');
		                    $("#sortForm").append("<input type='hidden' name='sortField' value='prism_publicationDate' />");
			                $("#sortForm").submit();
			                return false;			                
			            });       

                        $(".markedList").click(function() {
                            $("#markListForm").submit();                                
                        });  
                       /* $(".seeMoreFacetsLink a").click(function() {
                            $(this).parent("li").parent("ul").find(".hidden").show();
                            $(this).hide();
                            return false;  
                            This is already in JP site.js
                        });*/ 
                        
                       /* Note that I am aware that .live is deprecated in newer versions of jQuery but find no otherway to achieve this */    
                      $(".toggleajaxfacetitem a").live("click", function(){
                        	$(this).parent("li").parent("ul").find(".ajaxhidden").toggle();
							$(this).parent("li").find("a").toggleClass("hiddenprop");
								return false;
                        });
                        
						$(".seeAhahFacetsLink a").each(function(){
							var t = $(this), parent = t.parent(".seeAhahFacetsLink"), ahahids = parent.parent("ul").find(".ahahids"), ahahnames = parent.parent("ul").find(".ahahnames"), ahahvalues = parent.parent("ul").find(".ahahvalues"), ahahurl = parent.parent("ul").find(".ahahurl"), ahahsearchurl = parent.parent("ul").find(".ahahsearchurl");
							t.unbind();
							t.click(function(){
								t.css({"cursor":"wait"});
								var url = $(ahahurl).html();
								var ids = $(ahahids).html();
								var names = $(ahahnames).html();
								var values = $(ahahvalues).html();								
								var searchurl = $(ahahsearchurl).html();								
								var data = {'ids': ids, 'names': names, 'values': values, 'searchurl': searchurl};	
								$.post(url, data, function(resp) {	
									try{
										parent.after(resp);
										parent.hide();
									} catch(e) {
										alert("More terms request failed: " + e );
									}
								});
								return false;
							});	
						});
                         
 			        });
 			        /* ]]> */
			    </script>    
			  	</div>
			</div>
      </c:otherwise>
   </c:choose>		
</p:body>
