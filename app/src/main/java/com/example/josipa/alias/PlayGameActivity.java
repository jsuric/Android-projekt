package com.example.josipa.alias;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayGameActivity extends MainActivity {

    int scores = 0;
    int lap = 0;
    ArrayList<Team> teamsList = new ArrayList<Team>();
    CountDownTimer timer;
    AlertDialog dialog;

    TextView playingTeam;
    TextView readingPlayer;
    TextView score;

    int currentTeam =0;
    int numberOfTeams =0;
    private boolean lastRound = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_play_game);

    Bundle bundle = getIntent().getExtras();
    // lista timova koju smo dobili od create team activity
    teamsList = (ArrayList<Team>) bundle.getSerializable("teams_list");
    lap++;
    DBAdapter db = new DBAdapter(this);

    numberOfTeams = teamsList.size();

    playingTeam = (TextView)findViewById(R.id.playing_team);
    readingPlayer = (TextView)findViewById(R.id.reading_player);
    score = (TextView)findViewById(R.id.bodovi);

    String tim=teamsList.get(currentTeam).teamName.toString();
    playingTeam.setText(tim);
    String player = teamsList.get(currentTeam).firstPlayer.toString();
    readingPlayer.setText(player);
    score.setText("Bodovi: "+String.valueOf(scores));

    waitForPlayerToBeReady();


    timer = new CountDownTimer(10000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            TextView tim = (TextView)findViewById(R.id.timer);
            tim.setText(" "+millisUntilFinished/1000);
        }

        @Override
        public void onFinish() {
            addScoresToTeam(teamsList.get(currentTeam).teamName);
            scores = 0;
            currentTeam++;

            if (currentTeam < numberOfTeams)
            {
                setCurrentTeamAndPlayer(playingTeam, readingPlayer, score);
                waitForPlayerToBeReady();
            }
            else if (currentTeam == numberOfTeams && lap!=4){
                if (lastRound) {
                    calculateWinner(teamsList);
                } else {
                    lap++;
                    currentTeam = 0;
                    setCurrentTeamAndPlayer(playingTeam, readingPlayer, score);
                    waitForPlayerToBeReady();
                }
            }
            else {
                calculateWinner(teamsList);
            }
        }
    };
        fillDatabase(db);
        getRandomWord();
    }

    private void calculateWinner(List<Team> teamsList) {
        Team bestTeam = teamsList.get(0);
        int i = 0;
        for (Team team : teamsList) {
            if (team.score >= bestTeam.score) {
                if (team.score == bestTeam.score &&
                        !bestTeam.teamName.equals(teamsList.get(0).teamName)) i++;
                else i=0;
                bestTeam = team;
            }
        }
        if (i > 0 && !lastRound) {
            playAnotherLapAndAnnounceWinner();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("(: POBJEDNIK :)")
                    .setMessage("Pobjednik jeeee     \n" +bestTeam.teamName+ "\n sa "+bestTeam.score+" bodova! " +
                    "\n ČESTITKE! :) \n Želite li ponovno igrati u istom sastavu?" )
                    .setPositiveButton("DA", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            resetValues();
                            waitForPlayerToBeReady();
                        }
                    })
                    .setNegativeButton("NE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(PlayGameActivity.this, CreateTeamsActivity.class);
                            startActivity(intent);
                        }
                    });
            dialog = builder.create();
            dialog.show();
        }

    }

    private void resetValues() {
        scores = 0;
        lap = 1;
        currentTeam = 0;
        setCurrentTeamAndPlayer(playingTeam, readingPlayer, score);
        getRandomWord();

    }

    private void playAnotherLapAndAnnounceWinner() {
            scores = 0;
            currentTeam = 0;
            lap=1;
            setCurrentTeamAndPlayer(playingTeam,readingPlayer,score);
            getRandomWord();
            waitForPlayerToBeReady();
            lastRound = true;
    }

    private void setCurrentTeamAndPlayer(TextView playingTeam, TextView readingPlayer, TextView score) {
        String tim;
        String player;
        tim=teamsList.get(currentTeam).teamName;
        playingTeam.setText(tim);
        if (lap == 1 || lap == 3)
            player = teamsList.get(currentTeam).firstPlayer;
        else
            player = teamsList.get(currentTeam).secondPlayer;
        readingPlayer.setText(player);
        score.setText("Bodovi: "+String.valueOf(scores));
    }

    private void addScoresToTeam(String teamName) {
        for (Team team : teamsList) {
            if (team.teamName.equals(teamName))
            {
                team.score += scores;
            }
        }
    }

    public void displayWords(Cursor c)
    {
        TextView t = (TextView)findViewById(R.id.rijec);
        t.setText(c.getString(1)+"\n");

        TextView t2 = (TextView)findViewById(R.id.zabranjene);
        t2.setText(c.getString(2));

    }

    private void getRandomWord() {
        Random r=new Random();
        int n=(r.nextInt(121));
        DBAdapter db = new DBAdapter(this);
        db.open();
        Cursor cu = db.getWord(n);
        if (cu.moveToFirst())
            displayWords(cu);
        db.close();
    }

    public void pogodi(View view) {
        scores++;
        TextView t = (TextView)findViewById(R.id.bodovi);
        t.setText("Bodovi: "+String.valueOf(scores));
        getRandomWord();
    }

    public void preskoci(View view) {
        scores--;
        TextView t = (TextView)findViewById(R.id.bodovi);
        t.setText("Bodovi: "+String.valueOf(scores));
        getRandomWord();
    }

    private void waitForPlayerToBeReady() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Klikni za početak odbrojavanja.")
                .setPositiveButton("KRENI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        timer.start();
                    }
                });
        dialog = builder.create();
        dialog.show();
    }

    public void fillDatabase(DBAdapter db)
    {
        db.open();
        long id = db.insertWord("Nogometaš", "Nogomet, Lopta, Manekenka");
        id = db.insertWord("Matematika", "Škola, Predmet, Brojevi");
        id = db.insertWord("Skijanje", "Snijeg, Zima, Skijaška oprema");
        id = db.insertWord("Prsten", "Prst, Zaručnički, Dragi kamen");
        id = db.insertWord("Bolnica", "Liječnik, Pacijent, Liječiti");
        id = db.insertWord("Odvjetnik", "Parnica, Sud, Odijelo");
        id = db.insertWord("Plaža", "Pijesak, Ljeto, Ležati");
        id = db.insertWord("Knjižnica", "Čitati, Knjiga, Zakasnina");
        id = db.insertWord("Čaj", "Kava, Voćni, Hladnoća");
        id = db.insertWord("Glavobolja", "Glava, Migrena, Mamurluk");
        id = db.insertWord("Kino", "Film, Kokice, Ljubavno sjedalo");
        id = db.insertWord("Plitvička jezera", "Lika, Jezero, Nacionalni park");
        id = db.insertWord("Češalj", "Češljati, Kosa, Četka");
        id = db.insertWord("Gusar", "Kapetan Kuka, Blago, Papiga");
        id = db.insertWord("Palma", "Tropska biljka, Plaža, Kokos");
        id = db.insertWord("Sendvič", "Kruh, Užina, Salama");
        id = db.insertWord("Čekić", "Čavao, Alat, Udarati");
        id = db.insertWord("Korijen", "Stablo, Zub, Zemlja");
        id = db.insertWord("Krava", "Mlijeko, Sir, Trava");
        id = db.insertWord("Proljeće", "Cvijeće, Ptice, Sunce");

        id = db.insertWord("Perilica posuđa", "Posuđe, Prati, Deterdžent");
        id = db.insertWord("Perilica odjeće", "Odjeća, Deterdžent, Prati");
        id = db.insertWord("Mlijeko", "Krava, Kakao, Piti");
        id = db.insertWord("Tost", "Toster, Doručak, Sendvič");
        id = db.insertWord("Odijelo", "Vjenčanje, Smoking, Posao");
        id = db.insertWord("Kamion", "Auto, Veliko, Autocesta");
        id = db.insertWord("Srijeda", "Dan, Utorak, Četvrtak");
        id = db.insertWord("Časopis", "Čitati, Magazin, Pretplata");
        id = db.insertWord("Tradicija", "Ritual, Kultura, Ponoviti");
        id = db.insertWord("Slavina", "Pipa, Voda, Kapati");
        id = db.insertWord("Stjuardesa", "Avion, Posluživati, Putnici");
        id = db.insertWord("Žlica", "Vilica, Nož, Žličica");
        id = db.insertWord("Polo", "Konj, Majica, Jahanje");
        id = db.insertWord("Bogat", "Siromašan, Bill Gates, Milijunaš");
        id = db.insertWord("Meksiko", "Tortilje, Rio Grande, Španjolski jezik");
        id = db.insertWord("Televizor", "Gledati, Program, Film");
        id = db.insertWord("Panda", "Medvjedić, Bambus, Kina");
        id = db.insertWord("Koala", "Sisavac, Eukaliptus, Australija");
        id = db.insertWord("Krumpir", "Pomfrit, Pire, Jesti");
        id = db.insertWord("Kovanica", "Sitno, Lipa, Ostatak");

        id = db.insertWord("Parfem", "Miris, Vrat, Špricati");
        id = db.insertWord("Superman", "Strip, Clark Kent, Kriptonit");
        id = db.insertWord("Spiderman", "Strip, Pauk, Tobey Maguire");
        id = db.insertWord("Dodir", "Ruka, Prst, Osjećaj");
        id = db.insertWord("Novine", "Članak, Naslovnica, Papir");
        id = db.insertWord("Kupaonica", "Pločice, Kada, Umivaonik");
        id = db.insertWord("Nindža", "Ratnik, Karate, Kornjače");
        id = db.insertWord("Cancun", "Meksiko, Odmor, More");
        id = db.insertWord("Pasta za zube", "Četkica, Plidenta, Čistiti");
        id = db.insertWord("Mobitel", "Pozivi, Poruke, Džep");
        id = db.insertWord("Otok", "Plaža, Pijesak, Blago");
        id = db.insertWord("E-mail", "Pošta, Online, Slati");
        id = db.insertWord("Rokovnik", "Planer, Datumi, Obaveze");
        id = db.insertWord("Sef", "Zlato, Šifra, Sakriti");
        id = db.insertWord("Torba", "Putna, Kožna, Laptop");
        id = db.insertWord("Kofer", "Putovanje, Avion, Ljetovanje");
        id = db.insertWord("Zviždati", "Puhati, Usne, Pas");
        id = db.insertWord("Tenisice", "Trčati, Stopala, Sport");
        id = db.insertWord("Maksimum", "Vrh, Minimum, Dosegnuti");
        id = db.insertWord("Kraljica", "Kralj, Dvorac, Princeza");

        id = db.insertWord("Mehaničar", "Auto, Ulje, Kvar");
        id = db.insertWord("Sirena", "Uzbuna, More, Mala");
        id = db.insertWord("Plakati", "Tuga, Oči, Suza");
        id = db.insertWord("Dijamant", "Prsten, Karat, Nakit");
        id = db.insertWord("Čaša", "Staklo, Piće, Šalica");
        id = db.insertWord("Vic", "Šala, Pričati, Smijati se");
        id = db.insertWord("Kofein", "Kava, Coca-Cola, Spavati");
        id = db.insertWord("Limun", "Agrum, Žuto, Sok");
        id = db.insertWord("Rođenje", "Božić, Trudnica, Beba");
        id = db.insertWord("Sok od naranče", "Naranča, Cijediti, Vitamin C");
        id = db.insertWord("Vitamin C", "Limun, Naranča, Zdravlje");
        id = db.insertWord("E-mail", "Pošta, Online, Slati");
        id = db.insertWord("Rokovnik", "Planer, Datumi, Obaveze");
        id = db.insertWord("Sef", "Zlato, Šifra, Sakriti");
        id = db.insertWord("Torba", "Putna, Kožna, Laptop");
        id = db.insertWord("Kofer", "Putovanje, Avion, Ljetovanje");
        id = db.insertWord("Zviždati", "Puhati, Usne, Pas");
        id = db.insertWord("Tenisice", "Trčati, Stopala, Sport");
        id = db.insertWord("Maksimum", "Vrh, Minimum, Dosegnuti");
        id = db.insertWord("Kraljica", "Kralj, Dvorac, Princeza");

        id = db.insertWord("Popaj", "Crtić, Lula, Špinat");
        id = db.insertWord("Marker", "Flomaster, Trajno, Pisati");
        id = db.insertWord("Anegdota", "Priča, Događaj, Prepričati");
        id = db.insertWord("Hobotnica", "Krakovi, More, Jelo");
        id = db.insertWord("Maslinovo ulje", "Maslina, Dalmacija, Salata");
        id = db.insertWord("Hladnjak", "Kuhinja, Hladno, Hrana");
        id = db.insertWord("Zaposliti", "Radnik, Intervju, Šef");
        id = db.insertWord("Puknuti", "Staklo, Krhko, Razbiti");
        id = db.insertWord("Pretplatiti", "Novine, Časopis, Mjesečno");
        id = db.insertWord("Rođak", "Obitelj, Bratić, Rod");
        id = db.insertWord("Maramica", "Brisati, Papirnata, Nos");
        id = db.insertWord("Dolar", "Novac, SAD, Kanada");
        id = db.insertWord("Diploma", "Fakultet, Učenje, Titula");
        id = db.insertWord("Spajalica", "Metal, Ured, Spojiti");
        id = db.insertWord("Neboder", "Grad, Visoko, Uredi");
        id = db.insertWord("Oktoberfest", "Pivo, Kobasice, Munchen");
        id = db.insertWord("Morski pas", "Ralje, Ocean, Opasan");
        id = db.insertWord("Britvica", "Brijati, Oštrica, Lice");
        id = db.insertWord("Cirkus", "Klaun, Trapez, Šator");
        id = db.insertWord("Spužva", "Prati, Suđe, Deterdžent");

        id = db.insertWord("Orijentacija", "Kompas, Prostor, Priroda");
        id = db.insertWord("Pljačka", "Banka, Maska, Ukrasti");
        id = db.insertWord("Pustinja", "Gobi, Deva, Oaza");
        id = db.insertWord("Desert", "Ručak, Slatko, Jesti");
        id = db.insertWord("Zamrzivač", "Led, Hladno, Škrinja");
        id = db.insertWord("Grmljavina", "Kiša, Munja, Grom");
        id = db.insertWord("Traperice", "Jeans, Plavo, Levi's");
        id = db.insertWord("Vampir", "Zub, Krv, Noć");
        id = db.insertWord("Oblak", "Kiša, Sunce, Zakloniti");
        id = db.insertWord("Bicikl", "Pedala, Kotači, Volan");
        id = db.insertWord("Medeni mjesec", "Odmor, Vjenčanje, Putovanje");
        id = db.insertWord("Eskim", "Led, Pecanje, Iglu");
        id = db.insertWord("Jastuk", "Krevet, Glava, Perje");
        id = db.insertWord("Rođendan", "Proslava, Svjećice, Pokloni");
        id = db.insertWord("Lozinka", "Šifra, Prijava, Korisnik");
        id = db.insertWord("Rukavica", "Dlanovi, Hladnoća, Snijeg");
        id = db.insertWord("Ilegalno", "Legalno, Policija, Kazna");
        id = db.insertWord("Početak", "Kraj, Start, Krenuti");
        id = db.insertWord("Učionica", "Učenici, Stolovi, Stolice");
        id = db.insertWord("Autocesta", "Cesta, Voziti, Brzo");
        db.close();
    }

}

