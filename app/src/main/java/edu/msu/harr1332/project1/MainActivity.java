package edu.msu.harr1332.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.Serializable;


public class MainActivity extends AppCompatActivity {

    /**
     * The game parameters that will be used to load the game
     */
    private GameParams parameters = new GameParams();

    /**
     * The spinner object
     */
    Spinner sizeSpinner;

    /**
     * This class holds the data for the game to be created
     * Easily saved if game is destroyed
     */
    private static class GameParams implements Serializable {
        /**
         * The strings that represent the player names
         */
        public String player1 = "Player1";
        public String player2 = "Player2";

        /**
         * The size of the game
         */
        public int gameSize = 5;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Gets the spinner object
         sizeSpinner = (Spinner) findViewById(R.id.sizeSpinner);

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
                if (pos == 0) parameters.gameSize = 5;
                if (pos == 1) parameters.gameSize = 10;
                if (pos == 2) parameters.gameSize = 20;

            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {}
        });

        // Restores the current state if there is one
        if(savedInstanceState != null) {
            getFromBundle("GAME_PARAMS", savedInstanceState);
        }
    }

    /**
     * Save the view state to a bundle
     *
     * @param key key name to use in the bundle
     * @param bundle bundle to save to
     */
    public void putToBundle(String key, Bundle bundle) {
        bundle.putSerializable(key, parameters);
    }

    /**
     * Get the view state from a bundle
     *
     * @param key key name to use in the bundle
     * @param bundle bundle to load from
     */
    public void getFromBundle(String key, Bundle bundle) {
        parameters = (GameParams)bundle.getSerializable(key);
    }

    /**
     * Saves the instance of the Activity so that when it loads back up
     * it is restored
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        GetPlayers();

        // Saves the data in the bundle
        putToBundle("GAME_PARAMS", outState);
    }

    /**
     * This starts the game when the start button is pressed
     */
    public void OnStart(View view) {
        GetPlayers();

        // Passes the data onto the new activity
        Intent intent = new Intent(getApplicationContext(), GameBoardActivity.class);
        intent.putExtra("GAME_SIZE", parameters.gameSize);
        intent.putExtra("PLAYER1", parameters.player1);
        intent.putExtra("PLAYER2", parameters.player2);
        startActivity(intent);
    }

    /**
     * Sets the players to a new string
     */
    public void GetPlayers() {
        // Gets the player 1 text
        EditText player1Text = (EditText)findViewById(R.id.enterP1name);
        if (!player1Text.getText().toString().matches("")) {
            parameters.player1 = player1Text.getText().toString();
        }

        // Gets the player 2 text
        EditText player2Text = (EditText)findViewById(R.id.enterP2name);
        if (!player2Text.getText().toString().matches("")) {
            parameters.player2 = player2Text.getText().toString();
        }
    }

    /**
     * Displays a instruction dialog box
     * @param view The view we in
     */
    public void OnHelp(View view)
    {
        HelpDlg dlg2 = new HelpDlg();
        dlg2.show(getSupportFragmentManager(), "how-to-play");
    }

}
