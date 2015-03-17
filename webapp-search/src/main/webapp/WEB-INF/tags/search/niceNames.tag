<%@ include file="/WEB-INF/jspf/taglibs.jspf" %>

<jsp:useBean id="niceNames" class="java.util.HashMap" scope="request" />
<% 
	niceNames.put("titleabs","Title, Keywords or Abstract");
	niceNames.put("title","Title");
	niceNames.put("author","Authors");
	niceNames.put("all","All Fields (excluding fulltext)");
	niceNames.put("issnisbndoi","ISSN/ISBN/DOI");
	niceNames.put("fulltext","All Fields including Full Text");
	niceNames.put("journalbooktitle","Journal or Book title");
	niceNames.put("subjectarea","Subject");
	niceNames.put("buildingdesign_nonstructural","Building Design (non-structural)");
	niceNames.put("earthquakeengineering","Earthquake Engineering");
	niceNames.put("energy","Energy");
	niceNames.put("innovationresearch","Innovation Research");
	niceNames.put("sitework","Sitework");
	niceNames.put("structuresandbuildings","Structures and Buildings");
	niceNames.put("contains","contains");	
	niceNames.put("contenttype","Content Type");	
	niceNames.put("author_facet","Authors");
	niceNames.put("contentType_facet","Content type");
	niceNames.put("dcterms_subject_facet","Subject");
	niceNames.put("dcterms_type","Type");
	niceNames.put("pub_year_facet","Publication date");
%>
