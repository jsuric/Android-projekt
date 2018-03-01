package com.example.josipa.alias;

import android.content.Context;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;

/**
 * Created by Josipa on 28.2.2018..
 */

public class Team implements Serializable {
    String teamName="";
    String firstPlayer="";
    String secondPlayer="";
    int score = 0;



    public Team(String name, String first, String second)
    {
        this.teamName = name;
        this.firstPlayer = first;
        this.secondPlayer = second;
    }

   /* public void showTeam()
    {
        //TextView teams = (TextView) findViewById(R.id.show_teams_list);
        //team.showTeam();
        String teamPrikaz = this.teamName + " : "
                + this.firstPlayer + ", " +
                this.secondPlayer + "\n";
        //teams.setText(teamPrikaz);
        Toast.makeText(getApplicationContext(), teamPrikaz, Toast.LENGTH_LONG).show();

    } */
}
