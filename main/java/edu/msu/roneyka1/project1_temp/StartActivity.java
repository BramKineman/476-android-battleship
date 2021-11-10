package edu.msu.roneyka1.project1_temp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class StartActivity extends AppCompatActivity {

    /**
     * Text input for users
     */
    EditText playerOne, playerTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    //Button that leads to dialog text
    public void onHelp(View view){
        Intent intent = new Intent(this, StartActivity.class);
        AlertDialog.Builder builder =
                new AlertDialog.Builder(view.getContext());
        builder.setTitle(R.string.helpTitle);
        builder.setMessage(R.string.helpText);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        //startActivity(intent);
    }

    // Button handler for the Start Button (routes to GameActivity)
    public void onStartGameActivity(View view) {
        playerOne = findViewById(R.id.playerOneName);
        playerTwo = findViewById(R.id.playerTwoName);
        Intent intent = new Intent(this, GameActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("one", playerOne.getText().toString());
        bundle.putString("two", playerTwo.getText().toString());
        intent.putExtras(bundle);

        startActivity(intent);
    }


}