package com.example.josipa.alias;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.app.PendingIntent.getActivity;

public class CreateTeamsActivity extends AppCompatActivity {

    //a list of teams
    ArrayList<Team> gameTeams = new ArrayList<Team>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_teams);
    }

    public void showAddedTeam(View view)
    {
        //tray of showing alerDialog for adding a new team
       /* AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.add_new_team, null))
                // Add action buttons
                .setPositiveButton(R.string.createTeam, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // add teams
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getLayoutInflater().inflate(R.layout.activity_create_teams, null);
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show(); */

       // get new team information from activty_create_teams and create a new team

        EditText input1 = (EditText) findViewById(R.id.team_name);
        String name = input1.getText().toString();
        EditText input2 = (EditText) findViewById(R.id.first_player);
        String first = input2.getText().toString();
        EditText input3 = (EditText) findViewById(R.id.second_player);
        String second = input3.getText().toString();

        Team team = new Team(name, first, second);
        gameTeams.add(team);

        TextView teams = (TextView) findViewById(R.id.show_teams_list);
        teams.setText("");
        for( Team teamItem : gameTeams)
            show(teamItem, teams);

        //team.showTeam();
    }

    // help function for showing team
    public void show(Team team, TextView teams)
    {
        String teamPrikaz = team.teamName + " : "
                + team.firstPlayer + ", " +
                team.secondPlayer + "\n";
        teams.append(teamPrikaz);
    }

    // call the activity that starts a game
    public void startGame (View view)
    {
        Intent intent = new Intent(this, PlayGameActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("teams_list", gameTeams);
        intent.putExtras(bundle);
        this.startActivity(intent);
    }

}
