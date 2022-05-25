package edu.msu.harr1332.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import edu.msu.harr1332.project1.Cloud.Cloud;
import edu.msu.harr1332.project1.Cloud.Models.Game;

public class WaitActivity extends AppCompatActivity {
    /**
     * MEMBER VARIABLES
     */
    String currentTurn;
    int nextTurn;
    int curTurn;
    private int gameID;
    int startingTurn;
    int GameId;
    //PlayingArea playingArea = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_wait);

        final SteampunkedViewOnline view = (SteampunkedViewOnline)findViewById(R.id.steamPunkedViewOnline);
        final int entryCode = getIntent().getIntExtra("CREATE_FLAG", 0);
        final int gameid = getIntent().getIntExtra("GAME_ID", 0);

        GameId = gameid;

        if (entryCode == 1) {
            currentTurn = getIntent().getStringExtra("TURN");
            TextView turnText = (TextView)findViewById(R.id.currentTurn);
            turnText.append("Currently waiting on another player to join");
        }
        else {
            currentTurn = getIntent().getStringExtra("TURN");
            if (currentTurn.equals(getIntent().getStringExtra("NAME1"))) {
                currentTurn = getIntent().getStringExtra("NAME2");
            }
            else {
                currentTurn = getIntent().getStringExtra("NAME1");
            }
            TextView turnText = (TextView) findViewById(R.id.currentTurn);
            turnText.append("Currently waiting on ");
            turnText.append(currentTurn);
            turnText.append(" to make a move...");
        }

        // Gets the intent data
        Intent intent = getIntent();
        final String player1Name = intent.getStringExtra("NAME1");
        final String player2Name = intent.getStringExtra("NAME2");

        final String player = intent.getStringExtra("TURN");
        if (player.equals(player1Name)) {
            curTurn = 1;
        }
        else if (player.equals(player2Name)){
            curTurn = 0;
        }


        int gameSize = intent.getIntExtra("GAMESIZE", 5);
        if (entryCode == 1){
            onWaitForOpponent(gameid); // waits for an opponent to join the game
        }
        else {
            //curTurn = intent.getIntExtra("CURR_TURN",1);

            //playingArea = (PlayingArea) intent.getSerializableExtra("PLAYING_AREA");
            onWait(gameid);  // waits for an opponent to complete a turn
        }

        super.onCreate(savedInstanceState);
    }

    /**
     *
     * @param id
     */
    private void onWaitForOpponent(int id) {
        final int id2 = id;

        // TODO: Handle checking the server every second
        final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                // do stuff
                Cloud cloud = new Cloud();
                Game game = cloud.getFromCloud(id2);
                boolean fail = game == null;
                if(!fail) {
                    if (!game.getPlayer2().equals("waiting...")) {
                        // TODO: put values stored in game variable into intent and start gameboardonline activity using
                        // TODO: add boolean representing player 1 turn, next turn gets overwritten each time
                        Intent intent = new Intent(getApplicationContext(), GameBoardOnlineActivity.class);
                        intent.putExtra("entry_code", 2);
                        intent.putExtra("PLAYER1", game.getPlayer1());
                        intent.putExtra("PLAYER2", game.getPlayer2());
                        intent.putExtra("GAME_SIZE", game.getGameSize());
                        intent.putExtra("PLACED_PIPE", "test");
                        intent.putExtra("GAME_ID", id2);
                        intent.putExtra("CURRENT_TURN", 1);
                        //ntent.putExtra("PLAYING_AREA", playingArea);
                        startActivity(intent);
                        exec.shutdown();
                    }
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * Disables the back button on the wait screen
     */
    @Override
    public void onBackPressed() { }

    /**
     * Ends the game and sends the user back to the login screen
     * @param view The wait activitiy view
     */
    public void onEnd(final View view)
    {
        // Destroys the game from the database
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cloud cloud = new Cloud();
                final boolean ok = cloud.deleteFromCloud(GameId);
                if (!ok) { // Could not delete the game from the database
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(view.getContext(), "Game deletion failure", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();

        // Goes back to the login screen as the game is now over
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }


    /**
     * Handles waiting where it checks the server every second to see if the player
     * has finshed their turn.  Calls either on opponentturnend or ontimeout, depeneding
     * on if the user waited over a minute for the opponent to make a move
     */
    public void onWait(int id)
    {
        final int id2 = id;
        final int turn = curTurn;

        final View view = findViewById(R.id.waitActivity).getRootView();
        final boolean turnThread = false;

        // Timer thread that runs after a minute if no response from server
        final Handler handler = new Handler();
        final Runnable runnable  = new Runnable() {
            @Override
            public void run() {
                onEnd(view);
            }
        };

        handler.postDelayed(runnable, 60000);

        final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                // do stuff
                Cloud cloud = new Cloud();
                Game game = cloud.getFromCloud(id2);
                boolean fail = game == null;
                if(!fail) {
                    if (game.getTurn() == turn) {
                        // TODO: put values stored in game variable into intent and start gameboardonline activity using
                        // TODO: add boolean representing player 1 turn, next turn gets overwritten each time
                        if (game.getRandomPipes().equals(game.getPlayer1())) {
                            // Calls the wait activity to wait for the other persons turn
                            Intent intent = new Intent(getApplicationContext(), GameOverActivity.class);
                            intent.putExtra("WINNER", game.getPlayer1());
                            startActivity(intent);
                        }
                        else if (game.getRandomPipes().equals(game.getPlayer2())) {
                            // Calls the wait activity to wait for the other persons turn
                            Intent intent = new Intent(getApplicationContext(), GameOverActivity.class);
                            intent.putExtra("WINNER", game.getPlayer2());
                            startActivity(intent);
                        }
                        else {
                            Intent intent = new Intent(getApplicationContext(), GameBoardOnlineActivity.class);
                            intent.putExtra("entry_code", 2);
                            intent.putExtra("PLAYER1", game.getPlayer1());
                            intent.putExtra("PLAYER2", game.getPlayer2());
                            intent.putExtra("GAME_SIZE", game.getGameSize());
                            intent.putExtra("PLACED_PIPE", game.getPipes());
                            intent.putExtra("CURRENT_TURN", turn);
                            intent.putExtra("GAME_ID", id2);
                            startActivity(intent);
                        }
                        //intent.putExtra("PLAYING_AREA", playingArea);

                        exec.shutdown();

                        // Cancels thread if player responds before a minute is up
                        handler.removeCallbacks(runnable);
                    }


                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * This deletes the game from the database
     * @param gameid
     */
    private void finish(int gameid) {
    }

    /**
     * When the other player ends their turn, this function will be called to start the
     * game board activity, updated with the other players new move
     */
    public void onOpponentTurnEnd()
    {
        // TODO: Handle a change back to user that was waiting
    }

    /**
     * Handles a timeout when the current player waits for longer than a minute
     */
    public void onTimeOut()
    {
        // TODO: Handle a timeout
    }
}
