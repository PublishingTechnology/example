/*
 * AutosuggestJsonResult
 *
 * Copyright 2011 Publishing Technology plc
 */
package com.pub2web.search.ejb.solr.autosuggest;

import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pub2web.search.api.autosuggest.AutosuggestResult;
import com.pub2web.search.api.autosuggest.AutosuggestTerm;

/**
 * JSON implementation of the <code>AutosuggestResult</code>.
 * <p>
 * Uses the JSON format returned by SOLR, which has a single JSON array named "terms",
 * which contains two elements, the string "autosuggest" and another array of
 * comma-separated list of pairs of terms and the term frequencies,
 * e.g. {"terms":["autosuggest",["asthma",167,"allergic rhinitis",129,"allergy",69]]}
 * </p>
 * @author jbeard
 */
class AutosuggestJsonResult implements AutosuggestResult {

    private static final String JSON_TERMS_ARRAY = "terms";

   private final SortedSet<AutosuggestTerm> terms = new TreeSet<AutosuggestTerm>(
         new AutosuggestTermCountComparator());

    /**
     * Constructs an autosuggest result from the given JSON.
     * @param fields the fields which have been searched, usually "autosuggest"
     * @param json json given in the above format
     * @throws JSONException if the JSON is invalid or not in expected format
     */
    public AutosuggestJsonResult(List<String> fields, JSONObject json) throws JSONException {
        init(fields, json);
    }

    /**
     * Gets a list of the autosuggest terms.
     * @return a list of the autosuggest terms
     */
    @Override
    public SortedSet<AutosuggestTerm> getTerms() {
        return Collections.unmodifiableSortedSet(terms);
    }

    /**
     * Initialises from the given JSON
     * @param fields the fields which have been searched, usually "autosuggest"
     * @param json json containing the terms and counts
     * @throws JSONException if the JSON is invalid or not in expected format
     */
	private void init(List<String> fields, JSONObject json)
			throws JSONException {
		
		//Solr 4
		try {
			JSONObject termsObj = json.getJSONObject(JSON_TERMS_ARRAY);
			for (String field : fields) {
				parseArray(termsObj.getJSONArray(field));
			}
		} catch (JSONException e) {
        //Solr 3			
			JSONArray termsArr = json.getJSONArray(JSON_TERMS_ARRAY);
			for (int i = 0; i < termsArr.length(); i += 2) {
				String autosuggestStr = termsArr.getString(i);
				if (fields == null || !fields.contains(autosuggestStr)) {
					throw new JSONException(
							"JSON has incorrect format for autosuggest: "
									+ json.toString());
				}
				parseArray(termsArr.getJSONArray(i + 1));
			}
		}
	}

	private int parseArray(JSONArray autosuggestArr) throws JSONException {
		int length = autosuggestArr.length();
		for (int inx = 0; inx < length; inx += 2) {
			String name = autosuggestArr.getString(inx);
			Integer count = autosuggestArr.getInt(inx + 1);
			add(terms, new AutosuggestTermImpl(name, count));
		}
		return length;
	}

   /**
     * Adds the term to the set - merging the term with an existing term if required
     *
     * @param terms the set of results
     * @param term the term to merge in
     */
    private void add(SortedSet<AutosuggestTerm> terms, AutosuggestTermImpl term) {
        // As this is a tree set basically indexed by count we have no choice but to iterate through
        for (AutosuggestTerm t : terms) {
           if (match(t.getName(), term.getName())) {
              terms.remove(t);
              term = new AutosuggestTermImpl(removeNots(getName(t.getName(), term.getName())), term.getCount() + t.getCount());
              break;
           }
        }
        terms.add(new AutosuggestTermImpl(removeNots(term.getName()), term.getCount()));
    }

    /**
     * Our usual schema is happy to generate autosuggest terms which start with a '-' this means something else to our
     * searching - so we don't want to suggest them.
     *
     * @param term the term
     * @return the sanitised term
     */
   private String removeNots(String term) {
      return term.replaceAll("([^\\p{Alnum}])-", "$1 ");
   }

   private boolean match(String a, String b) {
      // We're the same term if we're the same with any punctuation removed
      return removePunctuation(a).equals(removePunctuation(b));
   }

   private String removePunctuation(String value) {
      // We need to make sure we exclude all punctuation wherever it appears in the unicode space
      StringBuilder sb = new StringBuilder(value.length());
      for (char c : value.toCharArray()) {
         if (Character.isLetterOrDigit(c)) {
            sb.append(c);
         }
      }
      return sb.toString();
   }

   private String getName(String a, String b) {
      // And the preferred term is the one with the least punctuation
      return a.length() < b.length() ? a : b;
   }
}

