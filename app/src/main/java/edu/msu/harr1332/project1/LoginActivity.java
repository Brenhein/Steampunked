package edu.msu.harr1332.project1;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import edu.msu.harr1332.project1.Cloud.Cloud;

public class LoginActivity extends AppCompatActivity {
    /**
     * CONSTANTS
     */
    private final static String PREFERENCES = "LOGINDETAILS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);

        // Gets preferences
        SharedPreferences settings = getSharedPreferences(PREFERENCES, MODE_PRIVATE);

        // If the user wanted the login details saved on the device
        if (settings.getBoolean("REMEMBER", false))
        {
            // Gets the username and password from preferences
            String user = settings.getString("USERNAME", "");
            String pw = settings.getString("PASSWORD","");

            // Gets the username and password edit text boxes
            EditText username = (EditText)findViewById(R.id.userName);
            EditText password = (EditText)findViewById(R.id.password);

            // Writes the login details to the text boxes
            username.setText(user);
            password.setText(pw);

            // Checks the box
            CheckBox cb = (CheckBox)findViewById(R.id.Remember);
            cb.setChecked(true);
        }

        super.onCreate(savedInstanceState);
    }

    public void onCreateAccount(View view) {
        Intent intent = new Intent(this, CreateAccount.class);
        startActivity(intent);
    }

    public void onLogin(View view) {
        /*
         * Create a thread
         */
        final View login = findViewById(R.id.loginView);
        final EditText editName = (EditText)login.findViewById(R.id.userName);
        final EditText editPassword = (EditText)login.findViewById(R.id.password);

        new Thread(new Runnable() {

            @Override
            public void run() {
                Cloud cloud = new Cloud();
                final boolean ok = cloud.loginFromCloud(editName.getText().toString(), editPassword.getText().toString());
                if(!ok) {
                        /*
                         * If we fail to save, display a toast
                         */
                        // Please fill this in...
                        login.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(login.getContext(), "failure", Toast.LENGTH_SHORT).show();
                            }
                        });

                }
                else {
                    Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);

                    // Puts the username in a bundle
                    EditText username = (EditText)findViewById(R.id.userName);
                    intent.putExtra("NAME", username.getText().toString());
                    startActivity(intent);
                }
            }
        }).start();

        /*
        // Create a cloud object and get the XML
        Cloud cloud = new Cloud();
        boolean success = cloud.loginFromCloud("harr1332", "testpassword");
        if (!success) {

        }
        else {

        }*/
    }

    /**
     * Dialog box to explain the game instructions
     * @param view The view representing the display
     */
    public void OnHelp(View view)
    {
        HelpDlg dlg2 = new HelpDlg();
        dlg2.show(getSupportFragmentManager(), "how-to-play");
    }

    /**
     * Calls the main activity for single player
     * @param view The view we currently on
     */
    public void OnMain(View view)
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    /**
     * Saves the username and password as preferences
     * @param view The view of the display
     */
    public void OnRememberMe(View view)
    {
        // Gets the editor
        SharedPreferences settings = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        CheckBox cb = (CheckBox)findViewById(R.id.Remember);
        if (cb.isChecked()) // User wants remembered username and password
        {
            // Gets the username and password
            EditText username = (EditText)findViewById(R.id.userName);
            EditText password = (EditText)findViewById(R.id.password);

            // Adds the username and password
            editor.putString("USERNAME", username.getText().toString());
            editor.putString("PASSWORD", password.getText().toString());
            editor.putBoolean("REMEMBER", true);
        }
        else // Users wants username and password to be removed
        {
            editor.remove("USERNAME");
            editor.remove("PASSWORD");
            editor.putBoolean("REMEMBER", false);
        }

        editor.apply(); // Adds changes
    }
}
