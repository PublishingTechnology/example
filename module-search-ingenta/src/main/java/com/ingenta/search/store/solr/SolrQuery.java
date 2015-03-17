package com.ingenta.search.store.solr;

import com.ingenta.search.store.Query;

public class SolrQuery implements Query {

    private String[] fields;
    private String[] highLights;
    private String query;
    private int rows;
    private int start;
    private boolean isHighlighting = true;

    /* (non-Javadoc)
     * @see com.ingenta.search.store.Query#getFields()
     */
    public String[] getFields() {
        return fields;
    }

    /* (non-Javadoc)
     * @see com.ingenta.search.store.Query#getHighlights()
     */
    public String[] getHighlights() {
        return highLights;
    }

    /* (non-Javadoc)
     * @see com.ingenta.search.store.Query#getQuery()
     */
    public String getQuery() {
        return query;
    }

    /* (non-Javadoc)
     * @see com.ingenta.search.store.Query#getRows()
     */
    public int getRows() {
        return rows;
    }

    /* (non-Javadoc)
     * @see com.ingenta.search.store.Query#getStart()
     */
    public int getStart() {
        return start;
    }

    /* (non-Javadoc)
     * @see com.ingenta.search.store.Query#setFields(java.lang.String[])
     */
    public void setFields(String[] fields) {
        this.fields = fields;
    }

    /* (non-Javadoc)
     * @see com.ingenta.search.store.Query#setHighlights(java.lang.String[])
     */
    public void setHighlights(String[] highLights) {
        this.highLights = highLights;
    }

    /* (non-Javadoc)
     * @see com.ingenta.search.store.Query#setQuery(java.lang.String)
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /* (non-Javadoc)
     * @see com.ingenta.search.store.Query#setRows(int)
     */
    public void setRows(int rows) {
        this.rows = rows;
    }

    /* (non-Javadoc)
     * @see com.ingenta.search.store.Query#setStart(int)
     */
    public void setStart(int start) {
        this.start = start;
    }

    /* (non-Javadoc)
     * @see com.ingenta.search.store.Query#isHighlighting()
     */
    public boolean isHighlighting() {
        return isHighlighting;
    }

    /* (non-Javadoc)
     * @see com.ingenta.search.store.Query#setHighlighting(boolean)
     */
    public void setHighlighting(boolean isHighlighting) {
        this.isHighlighting = isHighlighting;
    }
}
