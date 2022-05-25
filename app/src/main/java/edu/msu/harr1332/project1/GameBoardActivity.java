package edu.msu.harr1332.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class GameBoardActivity extends AppCompatActivity {

    /*Member Variables*/
    private static final int GAME_OVER = 1;
    private SteampunkedView steampunkedView;
    private short winner = 0; // 1 for player1 and 2 for player2
    private int gameSize;

    private float scale = 1f;
    private ScaleGestureDetector detector;

    // Players
    Player player1 = new Player();
    Player player2 = new Player();

    /*Nested Player class*/
    private class Player {
        /*Member Variables*/
        public String name;
        public boolean turn;

        // Get/set name
        String GetName() {return name;}
        void SetName(String name) {this.name = name;}

        // Get/set turn
        Boolean GetTurn() {return turn;}
        void SetTurn(boolean turn) {this.turn = turn;}
    }

    // Get Player1 and Player2
    public String GetPlayer1Name() {return player1.name;}
    public String GetPlayer2Name() {return player2.name;}
    public boolean GetPlayer1Turn() {return player1.turn;}
    public boolean GetPlayer2Turn() {return player2.turn;}
    public int GetGameSize() {return gameSize;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_game_board);

        // Gets the data from the previous activity
        Intent intent = getIntent();
        String player1Name = intent.getStringExtra("PLAYER1");
        String player2Name = intent.getStringExtra("PLAYER2");
        int gameSize = intent.getIntExtra("GAME_SIZE", 5);

        // Creates the players
        player1.name = player1Name;
        player2.name = player2Name;
        player1.turn = true; //player 1 always goes first
        player2.turn = false;
        this.gameSize = gameSize;

        steampunkedView = getSteamPunkedView();
        steampunkedView.startGame(gameSize, this);

        super.onCreate(savedInstanceState);
    }

    private SteampunkedView getSteamPunkedView() {
        return (SteampunkedView) findViewById(R.id.steamPunkedView);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSteamPunkedView().putToBundle(outState);
    }

    /**
     * Figures out who surrendered and pass that to gameover
     * @param view The view we are currently in
     */
    public void onSurrender(View view) {
        if (player1.turn) {
            winner = 2;
        }
        else if (player2.turn) {
            winner = 1;
        }

        onGameOver(); // finishes the game
    }

    /*
     * handle the end game screen
     * */
    public void onGameOver() {
        // Figures out who won to send to game over
        String winnerName;
        if (winner == 1) {
            winnerName = player1.GetName();
        }
        else if (winner == 2) {
            winnerName = player2.GetName();
        }
        else { // ERROR
            winnerName = "None";
        }

        Intent intent = new Intent(getApplicationContext(), GameOverActivity.class);
        intent.putExtra("WINNER", winnerName);
        startActivity(intent);
    }

    /**
     * This function calls a discard handler to remove a piece
     * @param view the current view we are delaing with
     */
    public void onOpen(View view) {
        if (player1.turn) {
            boolean win = getSteamPunkedView().handleOpen(1);
            if (!win) {
                winner = 2;
            } else {
                winner = 1;
            }
        }
        else if (player2.turn) {
            boolean win = getSteamPunkedView().handleOpen(2);
            if (!win) {
                winner = 1;
            } else {
                winner = 2;
            }
        }

        onGameOver();
    }

    public void onDiscard(View view) {
        getSteamPunkedView().handleDiscard();
        player1.turn = !player1.turn;
        player2.turn = !player2.turn;
    }

    /**
     * This function calls a install handler to add a piece
     * @param view the current view we are delaing with
     */
    public void onInstall(View view) {
        Pipe installed = null;
        if (player1.turn) {
            installed = getSteamPunkedView().handleInstall((short)1);
        } else if (player2.turn) {
            installed = getSteamPunkedView().handleInstall((short)2);
        }

        player1.turn = !player1.turn;
        player2.turn = !player2.turn;
    }

}
