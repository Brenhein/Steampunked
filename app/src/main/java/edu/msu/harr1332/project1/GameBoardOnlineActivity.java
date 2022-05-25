package edu.msu.harr1332.project1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import edu.msu.harr1332.project1.Cloud.Cloud;
import edu.msu.harr1332.project1.Cloud.Models.Game;

public class GameBoardOnlineActivity extends AppCompatActivity implements View.OnClickListener{

    /*Member Variables*/
    private static final int GAME_OVER = 1;
    private SteampunkedViewOnline steampunkedViewOnline;
    private short winner = 0; // 1 for player1 and 2 for player2
    private int gameSize;
    final int turn = 1;
    private boolean myTurn = false;

    private int gameId = 0;

    private float scale = 1f;
    private ScaleGestureDetector detector;
    Intent intent;
    Pipe[][] pipes = null;

    Pipe[] randomPipes = null;

    String pipesStr = "";

    // Players
    Player player1 = new Player();
    Player player2 = new Player();

    Button btnOnInstall, btnOnDiscard, btnOnOpen, btnOnSurrender;


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
        setContentView(R.layout.activity_game_board_online);

        // Gets the data from the previous activity
        intent = getIntent();
        gameId = intent.getIntExtra("GAME_ID", 0);
        int flag = intent.getIntExtra("entry_code", 0);
        String player1Name = intent.getStringExtra("PLAYER1");
        String player2Name = intent.getStringExtra("PLAYER2");
        int gameSize = intent.getIntExtra("GAME_SIZE", 5);

        // Creates the players
        player1.name = player1Name;
        player2.name = player2Name;
        //player1.turn = true; //player 1 always goes first
        //player2.turn = false;
        this.gameSize = gameSize;

        final Integer gameSize2 = gameSize;
        steampunkedViewOnline = getSteampunkedViewOnline();
        //steampunkedViewOnline.startGame(gameSize, this);
        //pipes = steampunkedViewOnline.getPipes();
        //randomPipes = steampunkedViewOnline.getRandomPipes();

        //Intent intent = new Intent(getApplicationContext(), GameBoardOnlineActivity.class);
        if (flag == 0) {
            steampunkedViewOnline.startGame(gameSize, this);
            pipes = steampunkedViewOnline.getPipes();
            randomPipes = steampunkedViewOnline.getRandomPipes();
            myTurn = true;
            player1.turn = true;
            player2.turn = false;
            new Thread(new Runnable() { // This thread creates the game in the database

                @Override
                public void run() {
                    Cloud cloud = new Cloud();
                    final int ok = cloud.gameFromCloud(player1.name, player2.name, "test", "test", 1, gameSize2);
                    if (ok == 0) {
                        /*
                         * If we fail to save, display a toast
                         */
                        // Please fill this in...
                        getSteampunkedViewOnline().post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getSteampunkedViewOnline().getContext(), "game create failure", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                    else {
                        //gameId = ok;
                        // TODO: Handle checking the server every second
                        Intent intent = new Intent(getApplicationContext(), WaitActivity.class);
                        gameId = ok;
                        intent.putExtra("NAME1", player1.name);
                        intent.putExtra("CREATE_FLAG", 1);
                        intent.putExtra("NAME2", player2.name);
                        intent.putExtra("GAMESIZE", gameSize2);
                        intent.putExtra("GAME_ID", ok);
                        intent.putExtra("TURN", player1.name);
                        startActivity(intent);
                    }
                }
            }).start();
        }
        else if (flag == 1) {
            //gameId = getIntent().getIntExtra("GAME_ID", 0);
            gameId = intent.getIntExtra("GAME_ID", 0);
            Intent intent2 = new Intent(getApplicationContext(), WaitActivity.class);
            steampunkedViewOnline.startGame(gameSize, this);
            pipes = steampunkedViewOnline.getPipes();
            randomPipes = steampunkedViewOnline.getRandomPipes();
            myTurn = false;
            player1.turn = true;
            player2.turn = false;
            // Calls the wait activity to wait for the other persons turn

            intent2.putExtra("NAME1", player1.name);
            intent2.putExtra("NAME2", player2.name);
            intent2.putExtra("GAMESIZE", gameSize2);
            intent2.putExtra("GAME_ID", gameId);
            intent2.putExtra("SWITCH_TURN", 1);
            intent2.putExtra("TURN", player2.name);
            startActivity(intent2);

            //getGameState();
        }
        else if (flag == 2) {
            gameId = intent.getIntExtra("GAME_ID", 0);
            //Intent intent2 = new Intent(getApplicationContext(), WaitActivity.class);
            final int turn = intent.getIntExtra("CURRENT_TURN", 1);
            steampunkedViewOnline.startGame(gameSize, this);
            PlayingArea test = (PlayingArea) intent.getSerializableExtra("PLAYING_AREA");
            if (test != null) {
                getSteampunkedViewOnline().getPlayingArea().setPipes(test.getPipes());
            }
            pipesStr = intent.getStringExtra("PLACED_PIPE");
            if (pipesStr.charAt(0) == 't' && pipesStr.length() > 4) {
                pipesStr = pipesStr.substring(4);
            }
            decodePipe(pipesStr);
            pipes = steampunkedViewOnline.getPipes();
            randomPipes = steampunkedViewOnline.getRandomPipes();
            myTurn = false;
            if (turn == 1) {
                player1.turn = true;
                player2.turn = false;
            }
            else {
                player1.turn = false;
                player2.turn = true;
            }

        }

        super.onCreate(savedInstanceState);

        btnOnInstall = findViewById(R.id.buttonInstall);
        btnOnDiscard = findViewById(R.id.buttonDiscard);
        btnOnOpen = findViewById(R.id.buttonOpen);
        btnOnSurrender = findViewById(R.id.buttonSurrender);

        btnOnInstall.setOnClickListener(this);
        btnOnDiscard.setOnClickListener(this);
        btnOnOpen.setOnClickListener(this);
        btnOnSurrender.setOnClickListener(this);
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSteampunkedViewOnline().putToBundle(outState);
    }

    //@RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonInstall:
                onInstall(v);
//                Log.d("Buttons","Button Install");
                break;

            case R.id.buttonDiscard:
//                Log.d("Buttons","Button Discard");
                onDiscard(v);
                break;

            case R.id.buttonOpen:
//                Log.d("Buttons","Button Open");
                onOpen(v);
                break;

            case R.id.buttonSurrender:
//                Log.d("Buttons","Button Surrender");
                onSurrender(v);
                break;

            default:
                break;

        }

    }

    /**
     * Figures out who surrendered and pass that to gameover
     * @param view The view we are currently in
     */
    public void onSurrender(View view) {
        if (player1.turn) {
            winner = 2;
        }
        else if(player2.turn){
            winner = 1;
        }

        onGameOver(); // finishes the game
    }

    private SteampunkedViewOnline getSteampunkedViewOnline() {
        return (SteampunkedViewOnline) findViewById(R.id.steamPunkedViewOnline);
    }

    /**
     * Represents the end of the game where everything is tied up
     */
    public void onGameOver() {
        //finish(gameId); // first, deletes the game from the database

        // Figures out who won to send to game over
        String name;
        int updatedTurn = intent.getIntExtra("CURRENT_TURN", 1);
        int waitingPlayer;
        if (updatedTurn == 1) {
            updatedTurn = 0;
            name = player1.name;
            waitingPlayer = 1;
        }
        else {
            updatedTurn = 1;
            name = player2.name;
            waitingPlayer = 0;
        }
        final int turn2 = updatedTurn;
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
        final String winner = winnerName;
        new Thread(new Runnable() { // This thread creates the game in the database

            @Override
            public void run() {
                Cloud cloud = new Cloud();
                final boolean ok = cloud.updateFromCloud(gameId, player1.name, player2.name, pipesStr, winner, turn2);
                if (!ok) {
                    /*
                     * If we fail to save, display a toast
                     */
                    // Please fill this in...
                    getSteampunkedViewOnline().post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getSteampunkedViewOnline().getContext(), "game update failure", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                else {
                    // Calls the wait activity to wait for the other persons turn
                    Intent intent = new Intent(getApplicationContext(), GameOverActivity.class);
                    intent.putExtra("WINNER", winner);
                    startActivity(intent);
                }

            }
        }).start();


    }


    /**
     * This deletes the game from the database, posting an error message if it fails
     * @param gameID The id for the current game beinng played on the device
     */
    private void finish(final int gameID) {
        // Destroys the game from the database
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cloud cloud = new Cloud();
                final boolean ok = cloud.deleteFromCloud(gameID);
                if (!ok) { // Could not delete the game from the database
                    steampunkedViewOnline.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(steampunkedViewOnline.getContext(), "Game deletion failure", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * This function calls a discard handler to remove a piece
     * @param view the current view we are delaing with
     */
    public void onOpen(View view) {
        if (player1.turn) {
            boolean win = getSteampunkedViewOnline().handleOpen(1);
            if (!win) {
                winner = 2;
            } else if (player2.turn){
                winner = 1;
            }
        }
        else {
            boolean win = getSteampunkedViewOnline().handleOpen(2);
            if (!win) {
                winner = 1;
            } else {
                winner = 2;
            }
        }

        onGameOver();
    }



    public void getGameState() {
        getSteampunkedViewOnline().setActivity(this);
        new Thread(new Runnable() {

            @Override
            public void run() {
                Cloud cloud = new Cloud();
                Game game = cloud.getFromCloud(gameId);
                boolean fail = game == null;
                if(!fail) {
                    //getSteampunkedViewOnline().loadHat(hat);
                    return;
                }
            }
        }).start();
    }

    /**
     * Called when the user closes the app
     */
    @Override
    protected void onStop() {
        super.onStop();
        // TODO: Maybe delete game from database when user closes the app, ie the game is over??
    }

    //@RequiresApi(api = Build.VERSION_CODES.O)
    public void onDiscard(View view) {
        getSteampunkedViewOnline().handleDiscard();
        //player1.turn = !player1.turn;
        //player2.turn = !player2.turn;
        myTurn = !myTurn;
        int turn=0;

        if (player1.turn) {
            turn = 0;
        }
        else if (player2.turn){
            turn = 1;
        }
        String name;
        int updatedTurn = intent.getIntExtra("CURRENT_TURN", 1);
        int waitingPlayer;
        if (updatedTurn == 1) {
            updatedTurn = 0;
            name = player1.name;
            waitingPlayer = 1;
        }
        else {
            updatedTurn = 1;
            name = player2.name;
            waitingPlayer = 0;
        }

        //String ser = toString(steampunkedViewOnline.getPlayingArea());

        final int turn2 = updatedTurn;
        final String name2 = name;
        final int waiting2 = waitingPlayer;
        new Thread(new Runnable() { // This thread creates the game in the database

            @Override
            public void run() {
                Cloud cloud = new Cloud();
                final boolean ok = cloud.updateFromCloud(gameId, player1.name, player2.name, pipesStr, "", turn2);
                if (!ok) {
                    /*
                     * If we fail to save, display a toast
                     */
                    // Please fill this in...
                    getSteampunkedViewOnline().post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getSteampunkedViewOnline().getContext(), "game update failure", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                else {
                    // Calls the wait activity to wait for the other persons turn
                    Intent intent = new Intent(getApplicationContext(), WaitActivity.class);

                    intent.putExtra("NAME1", player1.name);
                    intent.putExtra("NAME2", player2.name);
                    intent.putExtra("GAMESIZE", gameSize);
                    intent.putExtra("GAME_ID", gameId);
                    intent.putExtra("TURN", name2);
                    intent.putExtra("CURR_TURN",waiting2);
                    startActivity(intent);
                }

            }
        }).start();

        //TODO: Serialize the pipes and putExtra as string
    }

    public void decodePipe(String encoded) {
        for (int i = 0; i < encoded.length(); i += 8) {
            if (encoded.charAt(i) == 't') {
                return;
            }
            int x = Integer.parseInt(String.valueOf(encoded.charAt(i)));
            int y = Integer.parseInt(String.valueOf(encoded.charAt(i+1)));
            Bitmap bitmap = null;
            float angle = 0;
            String bitVal = String.valueOf(encoded.charAt(i+2));
            String angleVal = String.valueOf(encoded.charAt(i+3));
            switch (bitVal) {
                case "1":
                    bitmap = BitmapFactory.decodeResource(getSteampunkedViewOnline().getContext().getResources(), R.drawable.straight);
                    break;
                case "2":
                    bitmap = BitmapFactory.decodeResource(getSteampunkedViewOnline().getContext().getResources(), R.drawable.bend);
                    break;
                case "3":
                    bitmap = BitmapFactory.decodeResource(getSteampunkedViewOnline().getContext().getResources(), R.drawable.tee);
                    break;
                case "4":
                    bitmap = BitmapFactory.decodeResource(getSteampunkedViewOnline().getContext().getResources(), R.drawable.cap);
                    break;
            }

            switch (angleVal) {
                case "1":
                    angle = (float) 90;
                    break;
                case "2":
                    angle = (float) 180;
                    break;
                case "3":
                    angle = (float) 270;
                    break;
                case "4":
                    angle = (float) 0;
                    break;
            }
            boolean[] connections = {false, false, false, false};
            if (String.valueOf(encoded.charAt(i+4)).equals("1")) {
                connections[0] = true;
            } else {
                connections[0] = false;
            }
            if (String.valueOf(encoded.charAt(i+5)).equals("1")) {
                connections[1] = true;
            } else {
                connections[1] = false;
            }
            if (String.valueOf(encoded.charAt(i+6)).equals("1")) {
                connections[2] = true;
            } else {
                connections[2] = false;
            }
            if (String.valueOf(encoded.charAt(i+7)).equals("1")) {
                connections[3] = true;
            } else {
                connections[3] = false;
            }

            Pipe pipe = new Pipe(connections[0], connections[1], connections[2], connections[3]);
            pipe.setPipe(bitmap);
            //pipe.setConnections(connections[0], connections[1], connections[2], connections[3]);
            pipe.set(getSteampunkedViewOnline().getPlayingArea(), x, y);
            pipe.setAngle(angle);
            Pipe[][] pipes;
            pipes = getSteampunkedViewOnline().getPlayingArea().getPipes();
            pipes[x][y] = pipe;
            getSteampunkedViewOnline().getPlayingArea().setPipes(pipes);
        }
    }


    /**
     * This function calls a install handler to add a piece
     * @param view the current view we are delaing with
     */
    public void onInstall(View view) {
        Pipe installed = null;
        if (player1.turn) {
            installed = getSteampunkedViewOnline().handleInstall((short)1);
        } else if (player2.turn) {
            installed = getSteampunkedViewOnline().handleInstall((short)2);
        }
        int x = 0;
        int y = 0;
        String bitVal = "1";
        String angleVal = "1";
        float angle = 0;
        String bitmap = null;
        String encoded = "";
        if (installed != null) {
            x = installed.getxIndex();
            y = installed.getyIndex();

            encoded += x;
            encoded += y;

            bitmap = installed.getBitmapName();
            angle = installed.getAngle();

            switch (bitmap) {
                case "straight":
                    bitVal = "1";
                    break;
                case "bend":
                    bitVal = "2";
                    break;
                case "tee":
                    bitVal = "3";
                    break;
                case "cap":
                    bitVal = "4";
                    break;
            }
            encoded += bitVal;

            if (angle == (float)0) {
                angleVal = "1";
            }
            else if (angle == (float)90) {
                angleVal = "2";
            }
            else if (angle == (float)180) {
                angleVal = "3";
            }
            else {
                angleVal = "4"; // 270
            }
            encoded += angleVal;

            boolean[] connections = installed.getConnections();
            if (connections[0]) {
                encoded += "1";
            }
            else {
                encoded += "0";
            }
            if (connections[1]) {
                encoded += "1";
            }
            else {
                encoded += "0";
            }
            if (connections[2]) {
                encoded += "1";
            }
            else {
                encoded += "0";
            }
            if (connections[3]) {
                encoded += "1";
            }
            else {
                encoded += "0";
            }
        }

        final String encoded2 = pipesStr + encoded;

        String name;
        int updatedTurn = intent.getIntExtra("CURRENT_TURN", 1);
        int waitingPlayer;
        if (updatedTurn == 1) {
            updatedTurn = 0;
            name = player1.name;
            waitingPlayer = 1;
        }
        else {
            updatedTurn = 1;
            name = player2.name;
            waitingPlayer = 0;
        }

        final int turn2 = updatedTurn;
        final String name2 = name;
        final int waiting2 = waitingPlayer;
        new Thread(new Runnable() { // This thread creates the game in the database

            @Override
            public void run() {
                Cloud cloud = new Cloud();
                final boolean ok = cloud.updateFromCloud(gameId, player1.name, player2.name, encoded2, "", turn2);
                if (!ok) {
                    /*
                     * If we fail to save, display a toast
                     */
                    // Please fill this in...
                    getSteampunkedViewOnline().post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getSteampunkedViewOnline().getContext(), "game update failure", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                else {
                    // Calls the wait activity to wait for the other persons turn
                    Intent intent = new Intent(getApplicationContext(), WaitActivity.class);

                    intent.putExtra("NAME1", player1.name);
                    intent.putExtra("NAME2", player2.name);
                    intent.putExtra("GAMESIZE", gameSize);
                    intent.putExtra("GAME_ID", gameId);
                    intent.putExtra("TURN", name2);
                    intent.putExtra("CURR_TURN",waiting2);
                    //intent.putExtra("PLAYING_AREA", getSteampunkedViewOnline().getPlayingArea());
                    startActivity(intent);
                }

            }
        }).start();
    }

}
