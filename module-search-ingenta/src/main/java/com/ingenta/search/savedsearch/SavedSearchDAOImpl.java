package com.ingenta.search.savedsearch;

import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import com.ingenta.search.domain.ExplanationTextImpl;
import com.ingenta.search.domain.Search;
import com.ingenta.search.xml.XmlSerializer;
import com.ingenta.search.xml.XmlSerializerFactory;

public class SavedSearchDAOImpl implements SavedSearchDAO {

    private static final String SQL_CREATE = "INSERT INTO savedsearch " +
    "(identityid, foldername, searchname, lastrunon, search, createdon, createdby) " +
    "VALUES (?,?,?,?,?,?,?)";

    private static final String SQL_RETRIEVE_ID = 
        "SELECT searchId, identityid, foldername, searchname, lastrunon, search " +
        "FROM savedsearch " +
        "WHERE searchid = ?";

    private static final String SQL_DELETE = "DELETE FROM savedsearch WHERE searchid = ?";

    private static final String SQL_UPDATE = "UPDATE savedsearch " +
    "SET search = ?, lastrunon = ?, searchname = ?, foldername = ?, updatedon = ?, updatedby = ? " +
    "WHERE identityid = ? AND searchid = ?";

    private static final String SQL_UPDATE_RUN = "UPDATE savedsearch SET lastrunon = ? WHERE searchid = ?";

    private static final String SQL_FIND_ALL = 
        "SELECT searchid, foldername, identityid, searchname, lastrunon, search " +
        "FROM savedsearch " +
        "WHERE identityid = ?";

    private static final String COLUMN_SEARCH_ID = "searchid";
    private static final String COLUMN_FOLDER = "foldername";
    private static final String COLUMN_IDENTITYID = "identityid";
    private static final String COLUMN_LAST_RUN = "lastrunon";
    private static final String COLUMN_SEARCH = "search";
    private static final String COLUMN_SEARCH_NAME = "searchname";

    private static final Logger log = Logger.getLogger(SavedSearchDAOImpl.class);

    private final DataSource ds;
    
    /**
     * Creates an instance which will use the given database connection.
     * @param ds A connection to the saved search database.
     */
    public SavedSearchDAOImpl(DataSource ds) {
        this.ds = ds;
    }

    public SavedSearch saveSearch(SavedSearch search)throws DaoRuntimeException{
        log.debug("saveSearch: " + search);

        Connection connection = null;

        try {
            connection = ds.getConnection();
            saveSearch(search, connection);
            return search;
        }catch (SQLException e){
            throw new DaoRuntimeException("Error saving search!", e);
        }finally {
            closeConnection(connection);
        }
    }


    public void saveSearches(List<SavedSearch> searches) throws DaoRuntimeException{
        log.debug("saveSearches: " + searches);
        Connection connection = null;

        try {
            connection = ds.getConnection();

            for (SavedSearch search : searches) {
                saveSearch(search, connection);
            }
        }catch(SQLException e) {
            throw new DaoRuntimeException("Error preparing statement: " + SQL_DELETE, e);
        }finally{
            closeConnection(connection);
        }
    }

    private SavedSearch saveSearch(SavedSearch search, Connection connection) throws DaoRuntimeException{
        log.debug("saveSearch: " + search);
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL_CREATE, new String[]{COLUMN_SEARCH_ID});
            int index = 1;
            statement.setString(index++, search.getIdentityId());
            statement.setString(index++, search.getFolderName());
            statement.setString(index++, search.getSearchName());
            statement.setTimestamp(index++, new Timestamp(search.getLastRunOn().getTime()));
            if (System.getProperties().containsKey("search.savedsearch.base64")){                
                statement.setString(index++, encodeBase64(search.getSearchAsXml()));
            }
            else{
                statement.setString(index++, search.getSearchAsXml());
            }
            statement.setTimestamp(index++, new Timestamp(System.currentTimeMillis()));
            statement.setString(index++, search.getIdentityId());

            statement.executeUpdate();
            ResultSet generatedKey = statement.getGeneratedKeys();
            generatedKey.next();
            search.setSearchId(generatedKey.getLong(1));

            return search;
        }catch (SQLException e){
            throw new DaoRuntimeException("Error saving search!", e);
        } finally {
           closeStatement(statement);
        }
    }

    public SavedSearch getSavedSearch(String searchId)throws DaoRuntimeException, SavedSearchNotFoundException{
       try {
          return getSavedSearch(Long.parseLong(searchId));
       } catch (NumberFormatException e) {
          throw new SavedSearchNotFoundException(searchId + " is not a valid id", e);
       }
    }

    public SavedSearch getSavedSearch(Long searchId)throws DaoRuntimeException, SavedSearchNotFoundException{
        log.debug("getSavedSearch: " + searchId);
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = ds.getConnection();
            statement = connection.prepareStatement(SQL_RETRIEVE_ID);
            statement.setLong(1, searchId);

            ResultSet results = statement.executeQuery();

            if(results.next()){
                return readSearchFromResults(results, XmlSerializerFactory.getSerializer());
            }
            throw new SavedSearchNotFoundException("No search found with ID: " + searchId);
        }catch(SQLException e) {
            throw new DaoRuntimeException("Error retrieving search!", e);
        }finally {
            closeStatementAndConnection(connection, statement);
        }
    }

    /* (non-Javadoc)
     * @see com.ingenta.search.savedsearch.SavedSearchDAO#deleteSearch(com.ingenta.search.savedsearch.SavedSearch)
     */
    public void deleteSearch(SavedSearch search)throws DaoRuntimeException{
        log.debug("deleteSearch: " + search);
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = ds.getConnection();
            statement = connection.prepareStatement(SQL_DELETE);
            deleteSearch(search.getSearchId(), statement);
        }catch (SQLException e) {
            throw new DaoRuntimeException("Error preparing statement: " + SQL_DELETE, e);
        }finally {
            closeStatementAndConnection(connection, statement);
        }
    }

    /* (non-Javadoc)
     * @see com.ingenta.search.savedsearch.SavedSearchDAO#deleteFolder(com.ingenta.search.savedsearch.SavedSearchFolder)
     */
    public void deleteFolder(SavedSearchFolder folder)throws DaoRuntimeException{
        log.debug("deleteFolder: " + folder.getName());
        deleteSearches(folder.getSearches());
    }

    /* (non-Javadoc)
     * @see com.ingenta.search.savedsearch.SavedSearchDAO#deleteSearches(java.util.List)
     */
    public void deleteSearches(List<SavedSearch> searches)throws DaoRuntimeException{
        log.debug("deleteSearches: " + searches.size());
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = ds.getConnection();
            statement = connection.prepareStatement(SQL_DELETE);

            for (SavedSearch search : searches) {
                deleteSearch(search.getSearchId(), statement);
            }
        }catch(SQLException e) {
            throw new DaoRuntimeException("Error preparing statement: " + SQL_DELETE, e);
        }finally{
            closeStatementAndConnection(connection, statement);
        }
    }

    /**
     * Deletes the search with the given ID using the given statement.
     * @param searchId The ID of the search to be deleted.
     * @param statement The prepared statement to do the deletion
     * @throws DaoRuntimeException If there is an error deleting the search.
     */
    private void deleteSearch(Long searchId, PreparedStatement statement)throws DaoRuntimeException{
        log.debug("deleteSearch: " + searchId);

        try {
            statement.setLong(1, searchId);

            statement.executeUpdate();
        }catch (SQLException e) {
            throw new DaoRuntimeException("Error deleting search!", e);
        }
    }

    /* (non-Javadoc)
     * @see com.ingenta.search.savedsearch.SavedSearchDAO#updateSearch(com.ingenta.search.savedsearch.SavedSearch)
     */
    public void updateSearch(SavedSearch search)throws DaoRuntimeException{
        log.debug("updateSearch");
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = ds.getConnection();
            statement = connection.prepareStatement(SQL_UPDATE);
            updateSavedSearch(search, statement);
        }
        catch (SQLException e) {
            throw new DaoRuntimeException("Error updating search!", e);
        }finally {
            closeStatementAndConnection(connection, statement);
        }
    }

    @Override
   public void updateLastRun(SavedSearch savedSearch) throws DaoRuntimeException {
       log.debug("updateLastRun");
       Connection connection = null;
       PreparedStatement statement = null;

       try {
           connection = ds.getConnection();
           statement = connection.prepareStatement(SQL_UPDATE_RUN);
           statement.setTimestamp(1, new Timestamp(savedSearch.getLastRunOn().getTime()));
           statement.setLong(2, savedSearch.getSearchId());
           
           statement.executeUpdate();
       }
       catch (SQLException e) {
           throw new DaoRuntimeException("Error updating search!", e);
       }finally {
           closeStatementAndConnection(connection, statement);
       }
   }
    
    /**
     * Updates the given search in the database.
     * @param search The search to be updated.
     * @param statement The SQL statement to be used for the update.
     * @throws java.sql.SQLException If there is an error interacting with the database.
     */
    private void updateSavedSearch(SavedSearch search, PreparedStatement statement)throws SQLException{
        log.debug("updateSavedSearch: "  + search);

        int index = 1;
        if (System.getProperties().containsKey("search.savedsearch.base64")){                        
            statement.setString(index++, encodeBase64(search.getSearchAsXml()));
        }
        else{
            statement.setString(index++, search.getSearchAsXml());            
        }
        statement.setTimestamp(index++, new Timestamp(search.getLastRunOn().getTime()));
        statement.setString(index++, search.getSearchName());
        statement.setString(index++, search.getFolderName());
        statement.setTimestamp(index++, new Timestamp(System.currentTimeMillis()));
        statement.setString(index++, search.getIdentityId());
        statement.setString(index++, search.getIdentityId());
        statement.setLong(index++, search.getSearchId());
        
        statement.executeUpdate();
    }

    public void updateSearches(List<SavedSearch> searches)throws DaoRuntimeException{
        log.debug("updateSearches");
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = ds.getConnection();
            statement = connection.prepareStatement(SQL_UPDATE);

            for (SavedSearch search : searches) {
                updateSavedSearch(search, statement);
            }
        }
        catch (SQLException e) {
            throw new DaoRuntimeException("Error updating search!", e);
        }finally{
            closeStatementAndConnection(connection, statement);
        }
    }

    public List<SavedSearch> getAllSearches(String identityId) throws DaoRuntimeException {
        log.debug("getAllSearches: " + identityId);
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = ds.getConnection();
            statement = connection.prepareStatement(SQL_FIND_ALL);
            statement.setString(1, identityId);
            ResultSet results = statement.executeQuery();

            List<SavedSearch> searches = new ArrayList<SavedSearch>();
            XmlSerializer xmlSerializer = XmlSerializerFactory.getSerializer();

            while(results.next()){
                searches.add(readSearchFromResults(results, xmlSerializer));
            }

            return searches;
        }
        catch (SQLException e) {
            throw new DaoRuntimeException("Error finding searches!", e);
        }finally{
            closeStatementAndConnection(connection, statement);
        }
    }

    /**
     * Reads the current row of the given ResultSet and constructs a SavedSearch.
     * @param results The results to have its current row read.
     * @throws java.sql.SQLException  If there is an error reading the results.
     */
    private SavedSearch readSearchFromResults(ResultSet results, XmlSerializer xmlSerializer)
    throws SQLException {

        String folderName = results.getString(COLUMN_FOLDER);
        String searchName = results.getString(COLUMN_SEARCH_NAME);
        String identityId = results.getString(COLUMN_IDENTITYID);
        Date lastRunOn = results.getTimestamp(COLUMN_LAST_RUN);
        Long searchId = results.getLong(COLUMN_SEARCH_ID);
        String searchXml = results.getString(COLUMN_SEARCH);
        Search search;
        if (System.getProperties().containsKey("search.savedsearch.base64")){
            search = xmlSerializer.readSearchFromXml(decodeBase64(searchXml));
        }
        else{
            search = xmlSerializer.readSearchFromXml(searchXml);            
        }
        search.setSearchExplanation(new ExplanationTextImpl(search));

        return new SavedSearch(folderName, identityId, searchId, lastRunOn, search, searchName);
    }

    /**
     * Closes the given prepared statement AND the connection to the database.
     * Any errors on close are logged but no exception is thrown.
     * 
     * @param preparedStatement The prepared statement to close.
     */
    private void closeStatementAndConnection(Connection connection, PreparedStatement preparedStatement) {
        closeStatement(preparedStatement);
        closeConnection(connection);
    }

    /**
     * Closes the given prepared statement BUT NOT the connection to the
     * database. Any errors on close are logged but no exception is thrown.
     * 
     * @param statement The prepared statement to close.
     */
    private void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            }catch (SQLException e) {
                log.warn("Unable to close prepared statement", e);
            }
        }
    }

    /**
     * Closes this instance's database connection. Any errors on close are logged
     * but no exception is thrown.
     */
    private void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            }catch (SQLException e) {
                log.warn("Unable to close connection", e);
            }
        }
    }
    
    private String encodeBase64(String toEncode){
        System.out.println("Encoding to base64 " + toEncode);
        String result = null;
            result = new String(Base64.encodeBase64(toEncode.getBytes()));
            
        System.out.println("Encoded to base64 " + result);
        return result;
    }
    
    private String decodeBase64(String toDecode){
        String result = null;
            result = new String(Base64.decodeBase64(toDecode.getBytes()));
        System.out.println("base64 Decoded [" + toDecode + "] to : " + result);
        return result;
    }
    
    
}

