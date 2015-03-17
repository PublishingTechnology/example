/*
 * SearchSuggestion
 *
 * Copyright: 2011 PublishingTechnology plc
 */
package com.ingenta.search.domain;

import java.util.Map;

/**This object represents suggested search returned from
 * solr spell checker component.
 * JAVADOC for SearchSuggestion class
 *
 * @author pnema
 * @since search [version]
 */
public class SearchSuggestion {
   private Map<String, String> suggestions;
   private String collatedSearchText;
   /**
    * @return the collatedSearchText
    */
   public String getCollatedSearchText() {
      return collatedSearchText;
   }
   /**
    * @param collatedSearchText the collatedSearchText to set
    */
   public void setCollatedSearchText(String collatedSearchText) {
      this.collatedSearchText = collatedSearchText;
   }
   /**
    * @return the suggestions
    */
   public Map<String, String> getSuggestions() {
      return suggestions;
   }
   /**
    * @param suggestions the suggestions to set
    */
   public void setSuggestions(Map<String, String> suggestions) {
      this.suggestions = suggestions;
   }

}
