package edu.msu.harr1332.project1.Cloud;

import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;

import edu.msu.harr1332.project1.Cloud.Models.Catalog;
import edu.msu.harr1332.project1.Cloud.Models.DeleteResult;
import edu.msu.harr1332.project1.Cloud.Models.Game;
import edu.msu.harr1332.project1.Cloud.Models.GetGameResult;
import edu.msu.harr1332.project1.Cloud.Models.GetResult;
import edu.msu.harr1332.project1.Cloud.Models.Item;
import edu.msu.harr1332.project1.Cloud.Models.JoinResult;
import edu.msu.harr1332.project1.Cloud.Models.RecieveResult;
import edu.msu.harr1332.project1.Cloud.Models.UpdateResult;
import edu.msu.harr1332.project1.Pipe;
import java.util.ArrayList;


import edu.msu.harr1332.project1.Cloud.Models.CreateResult;
import edu.msu.harr1332.project1.Cloud.Models.LoginResult;

import edu.msu.harr1332.project1.Cloud.Models.GameResult;
import edu.msu.harr1332.project1.R;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

@SuppressWarnings("deprecation")
public class Cloud {

    private static final String BASE_URL = "https://webdev.cse.msu.edu/~harr1332/cse476/project2/";
    public static final String LOGIN_PATH = "login.php";
    public static final String CREATE_PATH = "add-user.php";
    public static final String GAME_PATH = "create-game.php";
    public static final String CATALOG_PATH = "game-cat.php";
    public static final String JOIN_PATH = "join-game.php";
    public static final String GET_PATH = "get-game.php";
    public static final String UPDATE_PATH = "update-game.php";
    public static final String DELETE_PATH = "delete-game.php";
    public static final String GET_GAME_PATH = "get-game-path.php";
    public static final String RECIEVE_PATH = ""; // TODO : create script to send a turn back to user
    private static final String TEST = "test";

    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build();

    /**
     * Logs a use into the cloud
     * @param username the current username to log in with
     * @param password The password to log in with
     * @return boolean indicating success
     */
    public boolean loginFromCloud(String username, String password) {
        SteampunkedService service = retrofit.create(SteampunkedService.class);
        try {
            Response<LoginResult> response = service.login(username, password).execute();

            // check if request failed
            if (!response.isSuccessful()) {
                Log.e("OpenFromCloud", "Failed to login, response code is = " + response.code());
                return false;
            }

            LoginResult result = response.body();
            if (result.getStatus().equals("yes")) {
                return true;
            }

            Log.e("OpenFromCloud", "Failed to login, message is = '" + result.getMessage() + "'");
            return false;
        } catch (IOException e) {
            Log.e("OpenFromCloud", "Exception occurred while logging in!", e);
            return false;
        }
    }

    public boolean deleteFromCloud(int id) {
        SteampunkedService service = retrofit.create(SteampunkedService.class);
        try {
            Response<DeleteResult> response = service.delete(id).execute();

            // check if request failed
            if (!response.isSuccessful()) {
                Log.e("OpenFromCloud", "Failed to delete game, response code is = " + response.code());
                return false;
            }

            DeleteResult result = response.body();
            if (result.getStatus().equals("yes")) {
                return true;
            }

            Log.e("OpenFromCloud", "Failed to delete game, message is = '" + result.getMessage() + "'");
            return false;
        } catch (IOException e) {
            Log.e("OpenFromCloud", "Exception occurred while loading hat!", e);
            return false;
        }
    }

    /**
     * Creates a game in the database
     * @param player1 first player
     * @param player2 second player
     * @param pipes the pipes on screen
     * @param randomPipes the pipes to select frok
     * @param turn whose turn it is
     * @param gameSize the size of the game board
     * @return if game creation was successful
     */
    public int gameFromCloud(String player1, String player2, String pipes, String randomPipes, int turn, int gameSize) {
        SteampunkedService service = retrofit.create(SteampunkedService.class);
        try {
            Response<GameResult> response = service.updateGame(player1, player2, pipes, randomPipes, turn, gameSize).execute();

            // check if request failed
            if (!response.isSuccessful()) {
                Log.e("OpenFromCloud", "Failed to load hat, response code is = " + response.code());
                return 0;
            }

            GameResult result = response.body();
            if (result.getStatus().equals("yes")) {
                return result.getGameID();
            }

            Log.e("OpenFromCloud", "Failed to load hat, message is = '" + result.getMessage() + "'");
            return 0;
        } catch (IOException e) {
            Log.e("OpenFromCloud", "Exception occurred while loading hat!", e);
            return 0;
        }
    }


    /**
     * Sends the data from the previous turn to the cloud
     * @param id The game id
     *
     * @return If the connection was sucessful
     */
    public boolean recieveFromCloud(int id) {
        SteampunkedService service = retrofit.create(SteampunkedService.class);
        try {
            Response<GetGameResult> response = service.getGame(id).execute();

            // check if request failed
            if (!response.isSuccessful()) {
                Log.e("OpenFromCloud", "Failed to recieve turn, response code is = " + response.code());
                return false;
            }

            GetGameResult result = response.body();
            if (result.getStatus().equals("yes")) {
                return true;
            }

            Log.e("OpenFromCloud", "Failed to recieve turn, message is = '" + result.getMessage() + "'");
            return false;

        } catch (IOException e) {
            Log.e("OpenFromCloud", "Exception occurred while recieving turn!", e);
            return false;
        }
    }


    /**
     * Sends the data from the previous turn to the cloud
     * @param id The game id
     * @param player1 The first player
     * @param player2 The second player
     * @param pipes The current pipes on screen
     * @param randomPipes The pipes to choose from
     * @param turn whose turn it is
     * @return If the connection was sucessful
     */
    public boolean updateFromCloud(int id, String player1, String player2, String pipes, String randomPipes, int turn) {
        SteampunkedService service = retrofit.create(SteampunkedService.class);
        try {
            Response<UpdateResult> response = service.updateValues(id, player1, player2, pipes, randomPipes, turn).execute();

            // check if request failed
            if (!response.isSuccessful()) {
                Log.e("OpenFromCloud", "Failed to send turn, response code is = " + response.code());
                return false;
            }

            UpdateResult result = response.body();
            if (result.getStatus().equals("yes")) {
                return true;
            }

            Log.e("OpenFromCloud", "Failed to send turn, message is = '" + result.getMessage() + "'");
            return false;
        } catch (IOException e) {
            Log.e("OpenFromCloud", "Exception occurred while sending turn!", e);
            return false;
        }
    }

    /**
     *
     * @param id
     * @return
     */
    public Game getFromCloud(int id) {
        SteampunkedService service = retrofit.create(SteampunkedService.class);
        try {
            Response<GetResult> response = service.loadGame(id).execute();

            // check if request failed
            if (!response.isSuccessful()) {
                Log.e("OpenFromCloud", "Failed to load hat, response code is = " + response.code());
                return null;
            }

            GetResult result = response.body();
            if (result.getStatus().equals("yes")) {
                return result.getGame();
            }

            Log.e("OpenFromCloud", "Failed to load hat, message is = '" + result.getMessage() + "'");
            return null;
        } catch (IOException e) {
            Log.e("OpenFromCloud", "Exception occurred while loading hat!", e);
            return null;
        }
    }

    /**
     * Creates a user account for the game
     * @param username The new username for the account to be created
     * @param password the password to use for the new account
     * @return boolean indicating success
     */
    public boolean createFromCloud(String username, String password) {
        SteampunkedService service = retrofit.create(SteampunkedService.class);
        try {
            Response<CreateResult> response = service.createAccount(username, password).execute();

            // check if request failed
            if (!response.isSuccessful()) {
                Log.e("OpenFromCloud", "Failed to create account, response code is = " + response.code());
                return false;
            }

            CreateResult result = response.body();
            if (result.getStatus().equals("yes")) {
                return true;
            }

            Log.e("OpenFromCloud", "Failed to create account, message is = '" + result.getMessage() + "'");
            return false;
        } catch (IOException e) {
            Log.e("OpenFromCloud", "Exception occurred while creating account!", e);
            return false;
        }

    }

    /**
     * Joins a game in the cloud
     * @param playerName The player's
     * @param id The id of the game to join
     * @return
     */
    public boolean joinFromCloud(String playerName, int id) {
        SteampunkedService service = retrofit.create(SteampunkedService.class);
        try {
            Response<JoinResult> response = service.joinGame(id, playerName).execute();

            // check if request failed
            if (!response.isSuccessful()) {
                Log.e("OpenFromCloud", "Failed to join game, response code is = " + response.code());
                return false;
            }

            JoinResult result = response.body();
            if (result.getStatus().equals("yes")) {
                return true;
            }

            Log.e("joinFromCloud", "Failed to join game, message is = '" + result.getMessage() + "'");
            return false;
        } catch (IOException e) {
            Log.e("joinFromCloud", "Exception occurred while joining game!", e);
            return false;
        }
    }


    /**
     * An adapter so that list boxes can display a list of filenames from
     * the cloud server.
     */
    public static class CatalogAdapter extends BaseAdapter {
        /**
         * The items we display in the list box. Initially this is
         * null until we get items from the server.
         */
        private Catalog catalog = new Catalog("", new ArrayList(), "");

        private Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        /**
         * Constructor
         */
        public CatalogAdapter(final View view) {
            // Create a thread to load the catalog
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        catalog = getCatalog();

                        if (catalog.getStatus().equals("no")) {
                            String msg = "Loading catalog returned status 'no'! Message is = '" + catalog.getMessage() + "'";
                            throw new Exception(msg);
                        }

                        view.post(new Runnable() {

                            @Override
                            public void run() {
                                // Tell the adapter the data set has been changed
                                notifyDataSetChanged();
                            }

                        });
                    } catch (Exception e) {
                        // Error condition! Somethign went wrong
                        Log.e("CatalogAdapter", "Something went wrong when loading the catalog", e);
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(view.getContext(), R.string.catalog_fail, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).start();
        }

        @Override
        public int getCount() {
            if (catalog.getItems() != null) {
                return catalog.getItems().size();
            }
            return 0;
        }

        @Override
        public Item getItem(int position) {
            return catalog.getItems().get(position);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if(view == null) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game, parent, false);
            }

            TextView tv = (TextView)view.findViewById(R.id.gameUser);
            tv.setText(catalog.getItems().get(position).getName());


            return view;
        }
        public Catalog getCatalog() throws IOException{
            SteampunkedService service = retrofit.create(SteampunkedService.class);
            return service.getCatalog(TEST).execute().body();


        }
        public String getId(int position) {
            return catalog.getItems().get(position).getId();
        }
    }
}
