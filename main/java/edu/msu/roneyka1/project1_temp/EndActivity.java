package edu.msu.roneyka1.project1_temp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class EndActivity extends AppCompatActivity {
    // Member variables
    private String winner;

    private String loser;

    private boolean hasSurrendered;

    // Class functions

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        // Get the player names from GameActivity
        Intent intent = getIntent();
        winner = intent.getStringExtra(GameActivity.ONE);
        loser = intent.getStringExtra(GameActivity.TWO);
        hasSurrendered = intent.getBooleanExtra(GameActivity.SURRENDERED, false);

        // Set the text for the winner and loser
        TextView text1 = findViewById(R.id.textWinner);
        TextView text2 = findViewById(R.id.textLoser);
        String s1 = winner + " " + getString(R.string.win);
        String s2;

        if (hasSurrendered) {
            s2 = loser + " " + getString(R.string.playerSurrender);
        } else {
            s2 = loser + " " + getString(R.string.lose);
        }

        text1.setText(s1);
        text2.setText(s2);
    }

    // Button handler to new game.
    public void onStartGame(View view) {
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
    }
}