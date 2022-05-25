package edu.msu.harr1332.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import edu.msu.harr1332.project1.Cloud.Cloud;
import edu.msu.harr1332.project1.Cloud.Models.Game;

public class LobbyActivity extends AppCompatActivity {
    /**
     * MEMBER VARIABLES
     */
    /// The game size
    private int gameSize;

    /// The player name
    private String playerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_lobby);

        // Gets the spinner object
        Spinner sizeSpinner = (Spinner) findViewById(R.id.sizeSpinnerOnline);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gameSize, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        sizeSpinner.setAdapter(adapter);

        sizeSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View view,
                                       int pos, long id) {
                if (pos == 0) gameSize = 5;
                if (pos == 1) gameSize = 10;
                if (pos == 2) gameSize = 20;

            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {}
        });

        // Gets the player name from the intent
        Intent intent = getIntent();
        playerName = intent.getStringExtra("NAME");

        super.onCreate(savedInstanceState);
    }

    /**
     * Handles a create game button press
     */
    public void OnCreateGame(View view)
    {
        // TODO : Creates a game and updates the database with a new user
        final View lobby = findViewById(R.id.gameLobby);
        Intent intent = new Intent(getApplicationContext(), GameBoardOnlineActivity.class);
        String player1 = getIntent().getStringExtra("NAME");
        String player2 = "waiting...";
        intent.putExtra("PLAYER1", player1);
        intent.putExtra("PLAYER2", player2);
        intent.putExtra("GAME_SIZE", this.gameSize);
        final String player1copy = player1;
        final String player2copy = player2;

        startActivity(intent);
    }

    /**
     * Handles a join game button press
     */
    public void OnJoinGame(View view)
    {
        // Checks the data base for a new user
        GameDlg dlg2 = new GameDlg();
        dlg2.setUsername(playerName);
        dlg2.show(getSupportFragmentManager(), "load");

    }

    public void finishJoin(int id) {
        final int id2 = id;

        new Thread(new Runnable() {

            @Override
            public void run() {
                Cloud cloud = new Cloud();
                Game game = cloud.getFromCloud(id2);
                boolean fail = game == null;
                if(!fail) {
                    testFinish(game, id2);
                }
            }
        }).start();
    }

    public void testFinish(Game game, int id) {
        Intent intent = new Intent(this, GameBoardOnlineActivity.class);
        intent.putExtra("entry_code", 1);
        intent.putExtra("PLAYER1", game.getPlayer1());
        intent.putExtra("PLAYER2", game.getPlayer2());
        intent.putExtra("GAME_SIZE", game.getGameSize());
        intent.putExtra("GAME_ID", id);
        startActivity(intent);
    }

}
