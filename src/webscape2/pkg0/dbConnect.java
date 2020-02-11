/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webscape2.pkg0;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author Dylan
 */
public class dbConnect {

    String url;//Database -> db_java
    String user;
    String pass;
    Connection conn;

    // public dbConnect(String gameId, String wTeam, String wScore, String lTeam, String lScore) {
    public dbConnect() {
        try {
            url = "jdbc:mysql://localhost:3306/NBA_APP"; //Database -> db_java
            user = "root";
            pass = "";

            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, pass);
            System.out.println("Connection Successful");

        } catch (Exception ex) {
            System.out.println(" \nDatabase connection Error:" + ex);
        }
    }
    
    
public void oldChampStuff(String champId, String team, String date){

        Statement stmt;
        try {
            stmt = conn.createStatement();

            String query = "UPDATE champdata2019 SET toDate='" + date + "'WHERE champId='" + champId + "'";
                  //  + "VALUES ('" + champId + "','" + date + "')";
            int d = stmt.executeUpdate(query);
            System.out.println(query);
            
        } catch (Exception ex) {
            System.out.println(" \nDatabase Import Error:" + ex);
        }
    }



    public void importGames(String gameId, String wTeam, String wScore, String lTeam, String lScore) {

        Statement stmt;
        try {
            stmt = conn.createStatement();

            String query = "INSERT INTO gameinfo ( GameID, winnerTeam, loserTeam, winnerScore, loserScore ) "
                    + "VALUES ('" + gameId + "', '" + wTeam + "', '" + lTeam + "', '" + wScore + "', '" + lScore + "')";
            int d = stmt.executeUpdate(query);
            System.out.println(query);

        } catch (Exception ex) {
            System.out.println(" \nDatabase Import Error:" + ex);
        }
    }
    
    public void importnewChampData(String champId, String team, String date) {

        Statement stmt;
        try {
            stmt = conn.createStatement();

            String query = "INSERT INTO champdata2019 (champId, team, fromDate) "
                    + "VALUES ('" + champId + "','" + team + "', '" + date + "')";
            int d = stmt.executeUpdate(query);
            System.out.println(query);

        } catch (Exception ex) {
            System.out.println(" \nDatabase Import Error:" + ex);
        }
    }

    public void disconnect() {
        try {
            conn.close();

        } catch (Exception ex) {
            System.out.println(" \nDatabase Disconnect Error:" + ex);
        }
    }

}
