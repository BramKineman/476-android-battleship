package edu.msu.roneyka1.project1_temp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    /**
     * The name of the bundle keys to save the game state
     */
    private final static String CURRENTPLAYER = "currentPlayer";
    private final static String PLAYERONETILES = "playerOneTiles";
    private final static String PLAYERTWOTILES = "playerTwoTiles";
    private final static String PLAYERONEPLACED = "playerOnePlaced";
    private final static String PLAYERTWOPLACED = "playerTwoPlaced";
    private final static String PLACEMENTMODE = "placementMode";
    private final static String HIT = "Hit!";
    private final static String MISS = "Miss!";
    private final static String TURN = "'s turn to place!";



    /**
     * Random number to determine who plays first
     */
    int currentPlayer;

    /**
     * The name of playerOne
     */
    private String playerOne = "One";

    /**
     * The name of playerTwo
     */
    private String playerTwo = "Two";

    /**
     * int array to represent player one's tiles
     */
    int[] playerOneTiles;

    /**
     * int array to represent player two's tiles
     */
    int[] playerTwoTiles;

    /**
     * int array to represent the current player's tiles
     */
    int[] currentTiles = new int[16];

    int oneRevealed;
    int twoRevealed;

    private Board board;

    // have player 1 or 2 finished placing
    private boolean player1Placed;
    private boolean player2Placed;

    // checker for if the current player has surrendered
    private boolean hasSurrendered = false;

    // Constants for intent key for routing to EndActivity
    // The values must be unique.
    public static final String ONE = "i.won";
    public static final String TWO = "i.lost";
    public static final String SURRENDERED = "player.surrender";

    Button doneButton;
    TextView info;
    TextView hitMiss;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);



        // code to get the strings for the players names from StartActivity
        Bundle bundle = getIntent().getExtras();
        playerOne = bundle.getString("one");
        playerTwo = bundle.getString("two");

        String s1;
        String s2;

        s1 = playerOne + "'s turn to place";
        s2 = playerTwo + "'s turn to place";

        oneRevealed = 0;
        twoRevealed = 0;

        if (Math.random() > 0.5) {
            currentPlayer = 1;
        } else {
            currentPlayer = 0;
        }

        BoardView boardView = (BoardView)findViewById(R.id.boardView);
        board = boardView.getBoard();

        board.setHitOrMissListener(new Board.hitOrMissListener() {
            public void setHitMiss(boolean hit) { setStringHitMiss(hit); }
        });

        info = (TextView)findViewById(R.id.textPlace);
        hitMiss = (TextView)findViewById(R.id.hitOrMiss);

        if(savedInstanceState != null) {
            // We have saved state

            BoardView view = (BoardView)this.findViewById(R.id.boardView);

            view.loadInstanceState(savedInstanceState);

            loadInstanceState(savedInstanceState);
        }

        // set info text (temporary)
        switch (currentPlayer) {
            case 0:
                info.setText(s1);
                break;
            case 1:
                info.setText(s2);
        }

        doneButton = (Button)findViewById(R.id.buttonDone);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if the current board has 4 ships
                if (board.placementDone() && board.inPlacementMode()) {
                    switch (currentPlayer) {
                        case 0:
                            if (!player1Placed) {
                                // get tiles from board
                                playerOneTiles = board.getTiles().clone();
                                player1Placed = true;
                                currentPlayer = 1 - currentPlayer;
                                board.clearTiles();
                                if (!player2Placed) {
                                    info.setText(s2);
                                }
                            }
                            break;

                        case 1:
                            if (!player2Placed) {
                                playerTwoTiles = board.getTiles().clone();
                                player2Placed = true;
                                currentPlayer = 1 - currentPlayer;
                                board.clearTiles();
                                if (!player1Placed) {
                                    info.setText(s1);
                                }
                            }
                            break;
                    }
                    // if both players have finished placing
                    if (player1Placed && player2Placed) {
                        info.setText(" ");
                        board.setPlacementMode(false);

                        // set tiles to the current player's tiles
                        switch (currentPlayer) {
                            case 0:
                                board.setTiles(playerTwoTiles);
                                info.setText(playerOne + TURN);
                                break;
                            case 1:
                                board.setTiles(playerOneTiles);
                                info.setText(playerTwo + TURN);
                                break;
                        }
                    }
                } else if (board.turnIsOver()){
                    board.EndTurn();
                    if(board.gameIsOver()) // call before playerSwitch so current player is the winner
                    {
                        EndGame();
                    }
                    playerSwitch();
                }
            }
        });
    }

    /**
     * Function to switch who's turn it is during gameplay
     */
    public void playerSwitch()
    {
        if(currentPlayer == 0) { //player 1 is current player
            playerTwoTiles = board.getTiles();
            board.setTiles(playerOneTiles);
            oneRevealed = board.getRevealed();
            board.setRevealed(twoRevealed);
            info.setText(playerTwo + TURN);
        }
        else { //player 2 is current player
            playerOneTiles = board.getTiles();
            board.setTiles(playerTwoTiles);
            twoRevealed = board.getRevealed();
            board.setRevealed(oneRevealed);
            info.setText(playerOne + TURN);
        }
        currentPlayer = 1 - currentPlayer; // 1 becomes 0 and 0 becomes 1
        hitMiss.setText("");
    }

    /**
     * Links to EndActivity when the game is over
     */
    public void EndGame() {
        Intent intent = new Intent(this, EndActivity.class);
        // ONE is winner as per code already in EndActivity
        // TWO is loser
        switch (currentPlayer) {
            case 0:
                intent.putExtra(ONE, playerOne);
                intent.putExtra(TWO, playerTwo);
                break;

            case 1:
                intent.putExtra(ONE, playerTwo);
                intent.putExtra(TWO, playerOne);
                break;
        }
        startActivity(intent);
    }


    /**
     * Links to EndActivity on surrender choice
     */
    public void OnSurrender(View view) {
        Intent intent = new Intent(this, EndActivity.class);
        hasSurrendered = true;
        switch (currentPlayer) {
            case 0:
                intent.putExtra(ONE, playerTwo);
                intent.putExtra(TWO, playerOne);
                break;

            case 1:
                intent.putExtra(ONE, playerOne);
                intent.putExtra(TWO, playerTwo);
                break;
        }
        intent.putExtra(SURRENDERED, hasSurrendered);
        startActivity(intent);
    }

    public void setStringHitMiss(boolean hit){
        if(hit)
            hitMiss.setText(HIT);
        else
            hitMiss.setText(MISS);
    }

    /**
     * Save the instance state
     * @param bundle bundle we save to
     */
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(CURRENTPLAYER, currentPlayer);
        bundle.putIntArray(PLAYERONETILES, playerOneTiles);
        bundle.putIntArray(PLAYERTWOTILES, playerTwoTiles);
        bundle.putBoolean(PLAYERONEPLACED, player1Placed);
        bundle.putBoolean(PLAYERTWOPLACED, player2Placed);
        bundle.putBoolean(PLACEMENTMODE, board.inPlacementMode());

        BoardView view = (BoardView)this.findViewById(R.id.boardView);
        view.saveInstanceState(bundle);
    }

    /**
     * Read the state from a bundle
     * @param bundle The bundle we save to
     */
    protected void loadInstanceState(Bundle bundle) {
        currentPlayer = bundle.getInt(CURRENTPLAYER);
        playerOneTiles = bundle.getIntArray(PLAYERONETILES);
        playerTwoTiles = bundle.getIntArray(PLAYERTWOTILES);
        player1Placed = bundle.getBoolean(PLAYERONEPLACED);
        player2Placed = bundle.getBoolean(PLAYERTWOPLACED);
        board.setPlacementMode(bundle.getBoolean(PLACEMENTMODE));
    }
}