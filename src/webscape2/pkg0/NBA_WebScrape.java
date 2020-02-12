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
 * @author Dylan testing github
 */
public class NBA_WebScrape {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        updateStandings();

        int dayCounter = 1;
        ArrayList<LinkedHashMap> games = new ArrayList();
        /*
         for (dayCounter = 10; dayCounter <= 10; dayCounter++) {

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
                    System.out.println(ANSI_RED + "Losing Champ: " + champ + ANSI_RESET);
                    writeChampFile(champId, winner[0]);
                    System.out.printf(ANSI_GREEN + "New champion: %s \n" + ANSI_RESET, winner[0]);

                    champDone = true;
                }

            }

            if (!champLost) {
                System.out.println(ANSI_GREEN + "Still Champ " + champ + ANSI_RESET);
            }

            games.add(winnerInfo);
            games.add(loserInfo);
        } catch (Exception ex) {
            System.out.println(ANSI_RED + "Scrape game ERROR: " + ex + ANSI_RESET);
        }
        return games;

//dbConnect conn = new dbConnect();
    }

    public static void updateStandings() throws IOException {
        System.out.println("---------Standings--------------");
        ArrayList<Team> teams = new ArrayList();

        String url = "https://www.basketball-reference.com/leagues/NBA_2020_standings.html";
        try {
            Document document = Jsoup.connect(url).get();

            for (Element eastStandings : document.select("table#confs_standings_E")) {

                if (eastStandings.text().equals("")) {
                    System.out.println("---------empty--------------");

                    continue;
                } else {

                    for (Element teamInfo : eastStandings.select("tr.full_table")) {
                        // Get teamInfo
                        String teamName = teamInfo.select("th.left[data-stat='team_name']").select("a[href]").text();
                        String teamCity = "";
                        String[] teamString = teamName.split("\\s+");

                        if (teamString.length == 3) {

                            teamCity = teamString[0] + " " + teamString[1];
                            teamName = teamString[2];

                        } else {
                            teamCity = teamString[0];
                            teamName = teamString[1];
                        }

                        String wins = teamInfo.select("td.right[data-stat='wins']").text();
                        String loses = teamInfo.select("td.right[data-stat='losses']").text();
                        //System.out.printf("%s has %s wins and %s loses \n", teamName, wins, loses);
                        String teamid = getTeamAbbreviation(teamName);
                        String conference = "East";

                        //Create team Object
                        Team eastTeam = new Team(teamid, teamName, teamCity, wins, loses, conference);

                        // Add team to list
                        teams.add(eastTeam);
                    }
                }
            }

            for (Element westStandings : document.select("table#confs_standings_W")) {

                if (westStandings.text().equals("")) {
                    continue;
                } else {
                    for (Element teamInfo : westStandings.select("tr.full_table")) {
                        String teamName = teamInfo.select("th.left[data-stat='team_name']").select("a[href]").text();
                        String[] teamString = teamName.split("\\s+");
                        String teamCity = " ";

                        if (teamString.length == 3) {
                            teamCity = teamString[0] + " " + teamString[1];
                            teamName = teamString[2];
                            if (teamName.contains("Blazers")) {
                                teamName = teamString[1] + " " + teamString[2];
                                teamCity = teamString[0];
                            }
                        } else {
                            teamCity = teamString[0];
                            teamName = teamString[1];
                        }

                        String wins = teamInfo.select("td.right[data-stat='wins']").text();
                        String loses = teamInfo.select("td.right[data-stat='losses']").text();
                        //  System.out.printf("%s record is %s - %s \n", teamName, wins, loses);
                        String teamid = getTeamAbbreviation(teamName);
                        String conference = "West";

                        //Create team Object
                        Team westTeam = new Team(teamid, teamName, teamCity, wins, loses, conference);

                        // Add team to list
                        teams.add(westTeam);
                    }
                }
            }

        } catch (Exception ex) {

            System.out.printf(ANSI_RED + "Scraping Standing Error: %s" + ANSI_RESET, ex);

        }
          dbConnect conn = new dbConnect();

        for (Team t : teams) {
            //System.out.printf("name: %s city: %s conference: %s \n", t.name, t.city, t.conference);
            //System.out.printf("id: %s wins: %s loses: %s \n", t.id, t.wins, t.loses);
            //conn.importTeamData(t.id, t.name, t.city, t.conference);
            conn.importStandings(t.id, t.wins, t.loses);
        }
        conn.disconnect();
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
            case "Hawks":
                abrv = "ATL";
                break;
            case "Brooklyn":
            case "Nets":
                abrv = "BKN";
                break;
            case "Boston":
            case "Celtics":
                abrv = "BOS";
                break;
            case "Charlotte":
            case "Hornets":
                abrv = "CHA";
                break;
            case "Chicago":
            case "Bulls":
                abrv = "CHI";
                break;
            case "Cleveland":
            case "Cavaliers":
                abrv = "CLE";
                break;
            case "Dallas":
            case "Mavericks":
                abrv = "DAL";
                break;
            case "Denver":
            case "Nuggets":
                abrv = "DEN";
                break;
            case "Detroit":
            case "Pistons":
                abrv = "DET";
                break;
            case "Golden State":
            case "Warriors":
                abrv = "GSW";
                break;
            case "Houston":
            case "Rockets":
                abrv = "HOU";
                break;
            case "Indiana":
            case "Pacers":
                abrv = "IND";
                break;
            case "LA Clippers":
            case "Clippers":
                abrv = "LAC";
                break;
            case "LA Lakers":
            case "Lakers":
                abrv = "LAL";
                break;
            case "Memphis":
            case "Grizzlies":
                abrv = "MEM";
                break;
            case "Miami":
            case "Heat":
                abrv = "MIA";
                break;
            case "Milwaukee":
            case "Bucks":
                abrv = "MIL";
                break;
            case "Minnesota":
            case "Timberwolves":
                abrv = "MIN";
                break;
            case "New Orleans":
            case "Pelicans":
                abrv = "NOP";
                break;
            case "New York":
            case "Knicks":
                abrv = "NYK";
                break;
            case "Oklahoma City":
            case "Thunder":
                abrv = "OKC";
                break;
            case "Orlando":
            case "Magic":
                abrv = "ORL";
                break;
            case "Philadelphia":
            case "76ers":
                abrv = "PHI";
                break;
            case "Phoenix":
            case "Suns":
                abrv = "PHX";
                break;
            case "Trail Blazers":
            case "Blazers":
                abrv = "POR";
                break;
            case "Sacramento":
            case "Kings":
                abrv = "SAC";
                break;
            case "San Antonio":
            case "Spurs":
                abrv = "SAS";
                break;
            case "Toronto":
            case "Raptors":
                abrv = "TOR";
                break;
            case "Utah":
            case "Jazz":
                abrv = "UTA";
                break;
            case "Washington":
            case "Wizards":
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
