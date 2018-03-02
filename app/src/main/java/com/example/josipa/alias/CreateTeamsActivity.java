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
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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


       // get new team information from activty_create_teams and create a new team

        EditText input1 = (EditText) findViewById(R.id.team_name);
        String name = input1.getText().toString();
        EditText input2 = (EditText) findViewById(R.id.first_player);
        String first = input2.getText().toString();
        EditText input3 = (EditText) findViewById(R.id.second_player);
        String second = input3.getText().toString();

        //check ih strings are empty

        if(name.isEmpty() || first.isEmpty() || second.isEmpty() )
        {
            Toast.makeText(this, "Popunite sva polja!", Toast.LENGTH_LONG).show();

        }

        else
        {
            Team team = new Team(name, first, second);
            gameTeams.add(team);

            input1.setText("");
            input2.setText("");
            input3.setText("");
            input1.requestFocus();

            TextView teams = (TextView) findViewById(R.id.show_teams_list);
            teams.setText("");
            for( Team teamItem : gameTeams)
                show(teamItem, teams);

        }


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
        if(gameTeams.isEmpty())
        {
            Toast.makeText(this, "Niste dodali timove!", Toast.LENGTH_LONG).show();
        }
        else
        {
            Intent intent = new Intent(this, PlayGameActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("teams_list", gameTeams);
            intent.putExtras(bundle);
            this.startActivity(intent);
        }

    }

    // delete teams list
    public void deleteTeams(View view)
    {
        TextView teams1 = (TextView) findViewById(R.id.show_teams_list);
        teams1.setText("");
        gameTeams.clear();
    }

}
