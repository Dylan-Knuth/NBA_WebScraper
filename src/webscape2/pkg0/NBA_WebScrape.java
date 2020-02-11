/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webscape2.pkg0;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
//import com.mysql.jdbc.Driver;

/**
 *
 * @author Dylan
 */
public class NBA_WebScrape {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        updateStandings();

        int dayCounter = 1;
        ArrayList<LinkedHashMap> games = new ArrayList();
        /*
         for (dayCounter = 8; dayCounter <= 8; dayCounter++) {

         String url = "https://www.basketball-reference.com/boxscores/?month=2&day=" + dayCounter + "&year=2020";
         String gameDate = fixGameDate(scrapeGameDate(url)).trim();

         games = scrapeNBAGames(url, gameDate);
         LinkedHashMap<String, String> winnerInfo = new LinkedHashMap<>();
         LinkedHashMap<String, String> loserInfo = new LinkedHashMap<>();

         winnerInfo = games.get(0);
         loserInfo = games.get(1);
         importGameData(gameDate, winnerInfo, loserInfo);

         }
         */
    }

    public static String scrapeGameDate(String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        String gameDate = "";
        for (Element dateInfo : document.select("div.prevnext")) {

            if (dateInfo.text().equals("")) {
                continue;
            } else {
                gameDate = dateInfo.select("span.button2.index").text();

            }
        }
        return gameDate;
    }

    public static ArrayList scrapeNBAGames(String url, String gameDate) throws IOException {

        ArrayList<LinkedHashMap> games = new ArrayList();

        try {
            boolean stayChamp = false;
            boolean champLost = false;
            String champ = "";
            Document document = Jsoup.connect(url).get();
            String champId;

            LinkedHashMap<String, String> winnerInfo = new LinkedHashMap<>();
            LinkedHashMap<String, String> loserInfo = new LinkedHashMap<>();

            String[] winner;
            String[] loser;
            Boolean champDone = false;

            for (Element gameTable : document.select("table.teams")) {

                winner = doWinnerStuff(gameTable);
                loser = doLoserStuff(gameTable);

                winnerInfo.put(winner[0], winner[1]);
                loserInfo.put(loser[0], loser[1]);

                champ = getChamp().trim();

                stayChamp = winnerInfo.containsKey(champ);
                champLost = loserInfo.containsKey(champ);

                if (champLost && !champDone) {
                    doOldChampStuff(gameDate);
                    champId = getChampId(gameDate, winner[0]);
                    doChampionStuff(champId, winner[0], gameDate);
                    System.out.println("Champ from file: " + champ);
                    writeChampFile(champId, winner[0]);
                    System.out.printf("New champion: %s \n", winner[0]);

                    champDone = true;
                }

            }

            if (champLost) {
                System.out.println("Still Champ " + champ);
            }

            games.add(winnerInfo);
            games.add(loserInfo);
        } catch (Exception ex) {
            System.out.println("Scrape game ERROR: " + ex);
        }
        return games;

//dbConnect conn = new dbConnect();
    }

    public static void updateStandings() throws IOException {
        System.out.println("---------Standings--------------");

        String url = "https://www.basketball-reference.com/leagues/NBA_2020_standings.html";
        try {
            Document document = Jsoup.connect(url).get();

            for (Element eastStandings : document.select("table#confs_standings_E")) {

                if (eastStandings.text().equals("")) {
                    System.out.println("---------empty--------------");

                    continue;
                } else {
                    for (Element teamInfo : eastStandings.select("tr.full_table[data-row='0']")) {
                        String teamName = teamInfo.select("th.left[data-stat='team_name']").text();
                        String wins = teamInfo.select("th.right[data-stat='wins']").text();
                        String loses = teamInfo.select("th.right[data-stat='losses']").text();
                        System.out.printf("%s has %s wins and %s loses \n", teamName, wins, loses);

                        System.out.printf("%s \n", teamInfo.text());

                    }
                }
            }

            for (Element westStandings : document.select("div#div_confs_standings_W").select("table#confs_standings_W")) {

                if (westStandings.text().equals("")) {
                    continue;
                } else {
                    for (Element teamInfo : westStandings.select("tr.full_table[data-row='0']")) {
                        String teamName = teamInfo.select("th.left[data-stat='team_name']").text();
                        String wins = teamInfo.select("th.right[data-stat='wins']").text();
                        String loses = teamInfo.select("th.right[data-stat='losses']").text();

                        System.out.printf("%s has %s wins and %s loses", teamName, wins, loses);

                    }
                }
            }

        } catch (Exception ex) {

            System.out.printf("Standing Error: %s", ex);

        }
        /*
         String url = "https://www.basketball-reference.com/leagues/NBA_2020_standings.html";
         Document document = Jsoup.connect(url).get();

         List<String> teams = new ArrayList<String>();
         List<String> wins = new ArrayList<String>();
         List<String> loses = new ArrayList<String>();

         Boolean champDone = false;
         try {
         // Western Stangings
         for (Element westStandings : document.select("table#confs_standings_W.suppress_all.sortable.stats_table.now_sortable")) {

         if (westStandings.text().equals("")) {
         continue;
         } else {
         System.out.println(westStandings);
         //tr.full_table:nth-of-type(1) > .left
         //      tr.full_table:nth-of-type(1) > td.right:nth-of-type(1)
         //            tr.full_table:nth-of-type(1) > td.right:nth-of-type(2)
         wins.add(westStandings.select("tr.full_table").select("td:nth-of-type(1)").text());

         }

         }
         } catch (Exception ex) {
         System.out.println("MAIN ERROR: " + ex);
         }
         System.out.println(wins);
         //dbConnect conn = new dbConnect();
         */
    }

    public static void doOldChampStuff(String gameDate) throws IOException {
        String fileName = "champ.txt";

        BufferedReader buff = new BufferedReader(new FileReader(fileName));
        StringBuffer sb = new StringBuffer("");

        String line = buff.readLine();
        if (line != null) {
            sb.append(line);
        }

        String champData = sb.toString();
        String[] dateTeam = champData.split("\\*");

        String team, date;
        date = dateTeam[0];
        team = dateTeam[2];

        String id = date + "*" + getTeamAbbreviation(team);
        dbConnect conn = new dbConnect();

        conn.oldChampStuff(id, team, gameDate);

    }

    public static String getChamp() throws IOException {
        String champ = "";
        String fileName = "champ.txt";
        //InputStream is = new FileInputStream(fileName);
        BufferedReader buff = new BufferedReader(new FileReader(fileName));
        StringBuffer sb = new StringBuffer("");

        String line = buff.readLine();
        if (line != null) {
            sb.append(line);
        }

        String champData = sb.toString();
        String[] dateTeam = champData.split("\\*");

        champ = dateTeam[2];

        return champ;
    }

    public static void writeChampFile(String champId, String team) throws IOException {
        String fileName = "champ.txt";
        FileWriter fileWriter = new FileWriter(fileName);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        String fileStuff = champId + "*" + team;
        printWriter.print(fileStuff);
        printWriter.close();

    }

    public static String getChampId(String gameDate, String wTeam) {
        String wAbrv = getTeamAbbreviation(wTeam);
        String champId = gameDate + "*" + wAbrv;
        return champId;
    }

    public static void doChampionStuff(String champId, String wTeam, String gameDate) {
        dbConnect conn = new dbConnect();
        conn.importnewChampData(champId, wTeam, gameDate);
    }

    public static String[] doWinnerStuff(Element game) {
        // LinkedHashMap<String, String> winner = new LinkedHashMap<>();

        String team = game.select(".winner").select("td:first-child").text().trim();
        String score = game.select(".winner").select(".right").first().text().trim();
        String[] winner = {team, score};

        return winner;

    }

    public static String[] doLoserStuff(Element game) {
        //LinkedHashMap<String, String> loser = new LinkedHashMap<>();
        String team = game.select(".loser").select("td:first-child").text().trim();
        String score = game.select(".loser").select(".right").first().text().trim();
        String[] loser = {team, score};

        return loser;
    }

    public static void importGameData(String gameDate, LinkedHashMap winnerInfo, LinkedHashMap loserInfo) {

        //System.out.println(winnerInfo);
        //System.out.println(loserInfo);
        dbConnect conn = new dbConnect();

        Iterator winnerIterator = winnerInfo.entrySet().iterator();
        Iterator loserIterator = loserInfo.entrySet().iterator();
        try {

            while (winnerIterator.hasNext()) {
                Map.Entry wEntry = (Map.Entry) winnerIterator.next();

                String wTeam = (String) wEntry.getKey();
                String wScore = (String) wEntry.getValue();

                Map.Entry lEntry = (Map.Entry) loserIterator.next();
                String lTeam = (String) lEntry.getKey();
                String lScore = (String) lEntry.getValue();

                String gameId = getGameId(gameDate, wTeam, lTeam);

                System.out.println("\nGameid: " + gameId);
                //  System.out.println(wScore + "*" + lScore + "\n");                
                conn.importGames(gameId, wTeam, wScore, lTeam, lScore);

            }
            conn.disconnect();

        } catch (Exception ex) {
            System.out.println(ex);
        }

    }

    public static String getGameId(String gameDate, String wTeam, String lTeam) {
        // System.out.println("Processing Game ID................");
        String gameId = gameDate + "*";
        gameId = gameId + getTeamAbbreviation(wTeam) + "*";
        gameId = gameId + getTeamAbbreviation(lTeam);
        return gameId;
    }

    public static String getTeamAbbreviation(String team) {
        //System.out.println("Processing Team abrv................");
        String abrv = "";

        switch (team) {
            case "Atlanta":
                abrv = "ATL";
                break;
            case "Brooklyn":
                abrv = "BKN";
                break;
            case "Boston":
                abrv = "BOS";
                break;
            case "Charlotte":
                abrv = "CHA";
                break;
            case "Chicago":
                abrv = "CHI";
                break;
            case "Cleveland":
                abrv = "CLE";
                break;
            case "Dallas":
                abrv = "DAL";
                break;
            case "Denver":
                abrv = "DEN";
                break;
            case "Detroit":
                abrv = "DET";
                break;
            case "Golden State":
                abrv = "GSW";
                break;
            case "Houston":
                abrv = "HOU";
                break;
            case "Indiana":
                abrv = "IND";
                break;
            case "LA Clippers":
                abrv = "LAC";
                break;
            case "LA Lakers":
                abrv = "LAL";
                break;
            case "Memphis":
                abrv = "MEM";
                break;
            case "Miami":
                abrv = "MIA";
                break;
            case "Milwaukee":
                abrv = "MIL";
                break;
            case "Minnesota":
                abrv = "MIN";
                break;
            case "New Orleans":
                abrv = "NOP";
                break;
            case "New York":
                abrv = "NYK";
                break;
            case "Oklahoma City":
                abrv = "OKC";
                break;
            case "Orlando":
                abrv = "ORL";
                break;
            case "Philadelphia":
                abrv = "PHI";
                break;
            case "Phoenix":
                abrv = "PHX";
                break;
            case "Portland":
                abrv = "POR";
                break;
            case "Sacramento":
                abrv = "SAC";
                break;
            case "San Antonio":
                abrv = "SAS";
                break;
            case "Toronto":
                abrv = "TOR";
                break;
            case "Utah":
                abrv = "UTA";
                break;
            case "Washington":
                abrv = "WAS";
                break;
        }
        return abrv;

    }

    public static String fixGameDate(String gameDate) {
        //System.out.println("Fixing Game Date................");
        String[] tokens = gameDate.split(" ");
        String month = tokens[0];
        String day = tokens[1];
        day = day.replace(",", "");
        String year = tokens[2];

        switch (month) {
            case "Jan":
                month = "01";
                break;
            case "Feb":
                month = "02";
                break;
            case "Mar":
                month = "03";
                break;
            case "Apr":
                month = "04";
                break;
            case "May":
                month = "05";
                break;
            case "Jun":
                month = "06";
                break;
            case "Jul":
                month = "07";
                break;
            case "Aug":
                month = "08";
                break;
            case "Sep":
                month = "09";
                break;
            case "Oct":
                month = "10";
                break;
            case "Nov":
                month = "11";
                break;
            case "Dec":
                month = "12";
                break;
        }

        if (Integer.parseInt(day) < 10) {
            day = "0" + day;
        }

        gameDate = year + month + day;

        //  System.out.println(gameDate);
        return gameDate;

    }
}
