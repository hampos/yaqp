package org.opentox.database;

import org.opentox.auth.Priviledges;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.opentox.OpenToxApplication;


/**
 *
 * @author chung
 */
public class UsersDB implements DataBaseAccess{

    protected final static String USER_ACCOUNTS_TABLE = "USERS";

    /**
     * Generates the table which contains a/a data. The structure of this table is
     * the following:
     *
     * <pre>
     *
     * |                       USERS                        |
     * |====================================================|
     * | USER_NAME (TXT) | USER_PASSWORD (TXT) | AUTH (TXT) |
     * |-----------------|---------------------|------------|
     * |        *        |           *         |      *     |
     * |        *        |           *         |      *     |
     * </pre>
     *
     * TODO: Use MD5s....
     */
    @CreateTable
    protected static void createUsersTable(){
        OpenToxApplication.opentoxLogger.info("Creating table: " + USER_ACCOUNTS_TABLE);
        String CreateTable = "create table " + USER_ACCOUNTS_TABLE + "(" +
                "USER_NAME VARCHAR(20), " +
                "USER_PASSWORD VARCHAR(20)," +
                "AUTH VARCHAR(20))";
        try {
            Statement stmt = InHouseDB.connection.createStatement();
            stmt.executeUpdate(CreateTable);
        } catch (SQLException ex) {
            OpenToxApplication.opentoxLogger.log(Level.SEVERE, null, ex);
        }
    }


    /**
     * This method is used to add a new user to the USERS table. The username,
     * the password
     * @param UserName username
     * @param PassWord password
     */
    @Registration
    public static void addUser(String UserName, String PassWord, Priviledges priviledges){
        String addUser = "INSERT INTO "+USER_ACCOUNTS_TABLE+ " VALUES ('"+UserName+"' , '"+
                PassWord+"' , '"+priviledges.getLevel()+"' )";
        try {
            Statement stmt = InHouseDB.connection.createStatement();
            stmt.executeUpdate(addUser);
        } catch (SQLException ex) {
            OpenToxApplication.opentoxLogger.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Delete a user.
     * @param UserName
     */
    @Removal
    public static void removeUser(String UserName){
        String removeUser = "DELETE FROM "+USER_ACCOUNTS_TABLE+" WHERE USER_NAME = '"+UserName+"'";
        Statement stmt;
        try {
            stmt = InHouseDB.connection.createStatement();
            stmt.executeUpdate(removeUser);
            OpenToxApplication.opentoxLogger.info("The user '"+UserName+"' was deleted!");
        } catch (SQLException ex) {
            OpenToxApplication.opentoxLogger.log(Level.SEVERE, null, ex);
        }
    }

    public static Priviledges getAuthorizationForUser(String username){
        Priviledges priviledges = Priviledges.GUEST;
        String dbQuery = "SELECT * FROM "+USER_ACCOUNTS_TABLE+" WHERE USER_NAME = '"+username+"'";
        ResultSet rs = null;
        try {
            Statement stmt = InHouseDB.connection.createStatement();
            rs = stmt.executeQuery(dbQuery);
            if (rs.next())
            priviledges = new Priviledges(rs.getString("AUTH"));
        } catch (SQLException ex) {
            OpenToxApplication.opentoxLogger.log(Level.SEVERE, null, ex);
        }

        return priviledges;
    }


    /**
     * Verify if a given pair of user name and password are registered in the
     * database. If yes, returns true, else returns false. Both username
     * and password are case sensitive.<br/><br/>
     * <b>Note:</b> If some credentials correspond to an administrator, then
     * <tt>verifyCredentials(userName,pass,Privilegdges.ADMIN)</tt>, returns true
     * while <tt>verifyCredentials(userName,pass,Privilegdges.USER)</tt> returns
     * false. Of course an administrator has all the authorization that a simple
     * user has, but this method verifies just if a given triplet of username, password
     * and given provilegdes is valid.
     * @param userName The user name.
     * @param password character array of the password.
     * @return true/false.
     */
    public static boolean verifyCredentials(String userName, String password){
        String dbQuery = "SELECT * FROM "+USER_ACCOUNTS_TABLE+ " WHERE USER_NAME='"+userName+
                "' AND USER_PASSWORD ='"+password+"'";
        System.out.println(dbQuery);
        ResultSet rs = null;
        boolean verify = false;
        try {
            Statement stmt = InHouseDB.connection.createStatement();
            rs = stmt.executeQuery(dbQuery);
            if (rs.next())
                    verify = true;

        } catch (SQLException ex) {
            OpenToxApplication.opentoxLogger.log(Level.SEVERE, null, ex);
        }
        return verify;

    }


    /**
     * Returns a Map&lt;String,char[]&gt; for the credentials of a given authorization level.
     * @param priviledges
     * @return All credentials as a Map.
     */
    public static Map<String,char[] > getCredentialsAsMap(Priviledges priviledges){
        Map<String, char[]> secret = new HashMap<String, char[]>();
        String getCredentials = "SELECT * FROM "+USER_ACCOUNTS_TABLE+" WHERE AUTH LIKE '%"+
                priviledges.getLevel()+"%'";
        ResultSet rs = null;
        try {
            Statement stmt = InHouseDB.connection.createStatement();
            rs = stmt.executeQuery(getCredentials);
            while (rs.next()) {
                secret.put(rs.getString("USER_NAME"),rs.getString("USER_PASSWORD").toCharArray());
            }

        } catch (SQLException ex) {
            OpenToxApplication.opentoxLogger.log(Level.SEVERE, null, ex);
        }
        return secret;
    }


}
