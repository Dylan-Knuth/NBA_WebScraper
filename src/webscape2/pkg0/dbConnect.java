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

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
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
            System.out.println(ANSI_RED + " \nDatabase connection Error:" + ex + ANSI_RESET);
        }
    }

    public void importTeamData(String teamid, String team, String city, String conference) {

        Statement stmt;
        try {
            stmt = conn.createStatement();

            String query = "INSERT INTO teams (teamId, name, city, conference) "
                    + "VALUES ('" + teamid + "', '" + team + "', '" + city + "', '" + conference + "')";
            int d = stmt.executeUpdate(query);
            System.out.println(ANSI_GREEN + query + ANSI_RESET);

        } catch (Exception ex) {
            System.out.println(ANSI_RED + " \nDatabase Standings Import Error:" + ex + ANSI_RESET);
        }
    }

    public void updateTeamData(String teamid, String team, String city, String conference) {

        Statement stmt;
        try {
            stmt = conn.createStatement();
            String query = "UPDATE teams SET teamName='" + team + "', teamCity='" + city + "', conference='" + conference + "' WHERE teamId='" + teamid + "'";
            int d = stmt.executeUpdate(query);
            System.out.println(ANSI_GREEN + query + ANSI_RESET);

        } catch (Exception ex) {
            System.out.println(ANSI_RED + " \nDatabase Standings Import Error:" + ex + ANSI_RESET);
        }
    }

    public void updateStandings(String teamid, String wins, String loses) {

        Statement stmt;
        try {
            stmt = conn.createStatement();

            String query = "UPDATE standings SET wins='" + wins + "', loses='" + loses + "' WHERE teamId='" + teamid + "'";

            int d = stmt.executeUpdate(query);
            System.out.println(ANSI_GREEN + query + ANSI_RESET);

        } catch (Exception ex) {
            System.out.println(ANSI_RED + " \nDatabase Standings Import Error:" + ex + ANSI_RESET);
        }
    }

    public void importStandings(String teamid, String wins, String loses) {

        Statement stmt;
        try {
            stmt = conn.createStatement();

            String query = "INSERT INTO standings (teamId, wins, loses) "
                    + "VALUES ('" + teamid + "', '" + wins + "', '" + loses + "')";
            int d = stmt.executeUpdate(query);
            System.out.println(ANSI_GREEN + query + ANSI_RESET);

        } catch (Exception ex) {
            System.out.println(ANSI_RED + " \nDatabase Standings Import Error:" + ex + ANSI_RESET);
        }
    }

    public void oldChampStuff(String champId, String team, String date) {

        Statement stmt;
        try {
            stmt = conn.createStatement();

            String query = "UPDATE champdata2019 SET toDate='" + date + "'WHERE champId='" + champId + "'";
            //  + "VALUES ('" + champId + "','" + date + "')";
            int d = stmt.executeUpdate(query);
            System.out.println(ANSI_GREEN + query + ANSI_RESET);

        } catch (Exception ex) {
            System.out.println(ANSI_RED + " \nDatabase Import Error:" + ex + ANSI_RESET);
        }
    }

    public void importGames(String gameId, String wTeam, String wScore, String lTeam, String lScore) {

        Statement stmt;
        try {
            stmt = conn.createStatement();

            String query = "INSERT INTO gameinfo ( GameID, winnerTeam, loserTeam, winnerScore, loserScore ) "
                    + "VALUES ('" + gameId + "', '" + wTeam + "', '" + lTeam + "', '" + wScore + "', '" + lScore + "')";
            int d = stmt.executeUpdate(query);
            System.out.println(ANSI_GREEN + query + ANSI_RESET);

        } catch (Exception ex) {
            System.out.println(ANSI_RED + " \nDatabase Import Error:" + ex + ANSI_RESET);
        }
    }

    public void importnewChampData(String champId, String team, String date) {

        Statement stmt;
        try {
            stmt = conn.createStatement();

            String query = "INSERT INTO champdata2019 (champId, team, fromDate) "
                    + "VALUES ('" + champId + "','" + team + "', '" + date + "')";
            int d = stmt.executeUpdate(query);
            System.out.println(ANSI_GREEN + query + ANSI_RESET);

        } catch (Exception ex) {
            System.out.println(ANSI_RED + " \nDatabase Import Error:" + ex + ANSI_RESET);
        }
    }

    public void disconnect() {
        try {
            conn.close();

        } catch (Exception ex) {
            System.out.println(ANSI_RED + " \nDatabase Disconnect Error:" + ex + ANSI_RESET);
        }
    }

}
