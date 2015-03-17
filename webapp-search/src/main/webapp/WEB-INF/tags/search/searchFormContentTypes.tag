<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>

<div class="searchWithinInput">
	<div class="searchWithinInputLabel"><s:message key="thispublicationtype" pageAgnostic="true" editable="true">This publication type:</s:message></div>
	<input type="hidden" name="operator7" value="AND" />			
	<input type="hidden" name="option7" value="contenttype" />
	<select name="value7" id="contenttype"  ${skin.searchMultipleSelect}  ${skin.searchMultipleSelectSize}>
		<option ${(fn:escapeXml(param.value7) == 'all') ? 'selected="selected"' : ''} value=""><s:message key="searchallcontent" pageAgnostic="true" editable="true">All</s:message></option>
		<option ${(fn:escapeXml(param.value7) == 'Book') ? 'selected="selected"' : ''} value="Book"><s:message key="searchbooks" pageAgnostic="true" editable="true">Books</s:message></option>
		<option ${(fn:escapeXml(param.value7) == 'Article') ? 'selected="selected"' : ''} value="Article"><s:message key="searchjournals" pageAgnostic="true" editable="true">Journals</s:message></option>
	</select>
</div>