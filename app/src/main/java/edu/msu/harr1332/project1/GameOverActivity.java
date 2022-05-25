package edu.msu.harr1332.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class GameOverActivity extends AppCompatActivity {

    /*Member Variables*/
    String winner;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_game_over);

        // Sets the name of the winner
        winner = getIntent().getStringExtra("WINNER");
        TextView winnerText = (TextView)findViewById(R.id.textViewWinner);
        winnerText.append("\n");
        winnerText.append(winner);
    }

    /*
     * handle the end game screen
     * */
    public void onNewGame(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
