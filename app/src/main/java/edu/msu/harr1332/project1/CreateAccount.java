package edu.msu.harr1332.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import edu.msu.harr1332.project1.Cloud.Cloud;

public class CreateAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
    }

    public void onCreateAccount(View view) {
        final View create = findViewById(R.id.createView);
        final EditText username = (EditText)create.findViewById(R.id.usernameCreate);
        final EditText password = (EditText)create.findViewById(R.id.passwordCreate);
        final EditText pw2 = (EditText)create.findViewById(R.id.passwordConfirm);
        if (!(password.getText().toString().equals(pw2.getText().toString()))) {
            create.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(create.getContext(), "Passwords do not match.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    Cloud cloud = new Cloud();
                    final boolean ok = cloud.createFromCloud(username.getText().toString(), password.getText().toString());
                    if (!ok) {
                        /*
                         * If we fail to save, display a toast
                         */
                        // Please fill this in...
                        create.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(create.getContext(), "failed to create account.", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    }
                }
            }).start();
        }
    }
}
