/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webscape2.pkg0;

/**
 *
 * @author Dylan
 */
public class Team {

    String id;
    String name;
    String city;
    String wins;
    String loses;
    String conference;
    
    public Team(String id, String name, String city, String wins, String loses, String conference) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.wins = wins;
        this.loses = loses;
        this.conference = conference;
    }



    public static void updateTeam() {

    }
}
