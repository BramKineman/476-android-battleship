package edu.msu.roneyka1.project1_temp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class Board {

    private final static String TILES = "Board.tiles";
    private final static String PLACEDSHIPS = "Board.placedShips";
    private final static String TURNTAKEN = "Board.turnTaken";


    // tiles can either be revealed or not revealed, and have a ship or no ship
    // tiles are ints: first bit represents revealed status, second bit represents ship status
    // 00 = not revealed, no ship, 10 = revealed, no ship, 01 = not revealed, has ship, etc.
    private int[] tiles;

    private BoardView boardView;
    private float width;
    private float height;
    private float scaleFactor;
    private ScaleGestureDetector scaleDetector;
    private float dx;
    private float dy;
    private float previousX;
    private float previousY;
    private float angle;
    private Paint linePaint;
    private Paint fillPaint;
    private boolean placementMode;
    private boolean turnTaken;
    private int placedShips;
    private int revealedShips;
    private Context mContext;
    private Bitmap shipDrawable;
    private Bitmap missMarker;
    private Bitmap hitMarker;
    private boolean hit;
    private hitOrMissListener listener;

    public Board(Context context) {

        tiles = new int[16];
        mContext = context;
        placementMode = true;
        placedShips = 0;
        revealedShips = 0;

        turnTaken = false;
        hit = false;
        listener = null;


        // initialize all tiles not revealed, no ship (00)
        for (int i = 0; i < 16; i += 1) {
            tiles[i] = 0;
        }

        // Paint for drawing lines of the board
        linePaint = new Paint();
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(5);
        linePaint.setStyle(Paint.Style.STROKE);

        // Paint for filling board with color
        fillPaint = new Paint();
        fillPaint.setColor(context.getResources().getColor(R.color.teal_200));
        fillPaint.setStyle(Paint.Style.FILL);

        angle = 0;
        dx = 0;
        dy = 0;
        scaleFactor = 1.f;
        previousX = 0;
        previousY = 0;

        missMarker = BitmapFactory.decodeResource(context.getResources(), R.drawable.miss_marker);
        shipDrawable = BitmapFactory.decodeResource(context.getResources(),
                                                    R.drawable.patrolboat_1x1);
        hitMarker = BitmapFactory.decodeResource(context.getResources(), R.drawable.hit_marker);


        scaleDetector = new ScaleGestureDetector(context, new Board.ScaleListener());
    }

    public interface hitOrMissListener{
        public void setHitMiss(boolean hit);
    }

    public void setHitOrMissListener(hitOrMissListener input) {
        listener = input;
    }

    public void saveInstanceState(Bundle bundle) {
        bundle.putIntArray(TILES, tiles);
        bundle.putInt(PLACEDSHIPS, placedShips);
        bundle.putBoolean(TURNTAKEN, turnTaken);
    }

    public void loadInstanceState(Bundle bundle) {
        tiles = bundle.getIntArray(TILES);
        placedShips = bundle.getInt(PLACEDSHIPS);
        turnTaken = bundle.getBoolean(TURNTAKEN);
    }

    public void setBoardView(BoardView v) {
        boardView = v;
    }

    public int[] getTiles() {
        return tiles;
    }

    public void setTiles(int[] tiles) {
        this.tiles = tiles;
        boardView.invalidate();
    }

    // set mode to placementMode or not
    public void setPlacementMode(boolean newMode) {
        this.placementMode = newMode;
    }

    public boolean inPlacementMode() {
        return this.placementMode;
    }

    // add ship to tile if no ship, else remove ship
    // only used during placement
    public void toggleShipAtTile(int tile) {
        if (placedShips < 4 || hasShip(tile)) {
            if (hasShip(tile)) {
                placedShips -= 1;
            } else {
                placedShips += 1;
            }
            tiles[tile] = tiles[tile] ^ 1;
        }
    }

    // reveal tile
    public void revealTile(int tile) {
        if (hasShip(tile)) {
            revealedShips += 1;
            hit = true;
            listener.setHitMiss(true);
        }
        else {
            listener.setHitMiss(false); }
        hit = false;
        tiles[tile] = tiles[tile] | 2;
    }

    // return if move was a hit
    public boolean isHit() { return hit; }

    // hide tile
    public void hideTile(int tile) {
        tiles[tile] = tiles[tile] & 2;
    }

    // check if tile is revealed
    public boolean isRevealed(int tile) {
        return (tiles[tile] & 2) != 0;
    }

    // set number of revealed tiles
    public void setRevealed(int revealed) { revealedShips = revealed;}

    // get number of revealed tiles
    public int getRevealed() { return revealedShips; }

    // check if tile has ship
    public boolean hasShip(int tile) {
        return (tiles[tile] & 1) != 0;
    }

    // check if placement is done
    public boolean placementDone() {return placedShips == 4;}

    // check if game is over
    public boolean gameIsOver() {return revealedShips >= 4;}

    public boolean turnIsOver() {
        return turnTaken;
    }

    public void EndTurn() {
        turnTaken = false;
    }

    // clear tiles off board
    public void clearTiles() {

        for (int i = 0; i < 16; i += 1) {
            tiles[i] = 0;
        }
        boardView.invalidate();
        placedShips = 0;
    }

    void draw(Canvas canvas) {
        width = canvas.getWidth();
        height = canvas.getHeight();


        canvas.save();
        canvas.translate(dx, dy);
        canvas.translate(width/2, height/2);
        canvas.scale(scaleFactor, scaleFactor);
        canvas.translate(-width/2, -height/2);
        canvas.rotate(angle, width/2, height/2);

        // width of each square in the board
        float tileWidth = width / 4;
        float tileHeight = height / 4;

        // draw the vertical lines of the board
        for (int i = 0; i <= 4; i += 1) {
            canvas.drawLine(tileWidth * i, 0, tileWidth * i, height, linePaint);
        }

        // draw the horizontal lines of the board
        for (int i = 0; i <= 4; i += 1) {
            canvas.drawLine(0, tileHeight * i, width, tileHeight * i, linePaint);
        }


        for (int tile = 0; tile < 16; tile += 1){
            float tileLeft = (tile % 4) * tileWidth;
            float tileTop = (tile / 4) * tileHeight;

            switch (tiles[tile]) {

                // not revealed, no ship
                case 0:
                    break;

                // not revealed, has ship
                case 1:
                    if (placementMode) {
                        canvas.drawBitmap(
                                shipDrawable,
                                null,
                                GetScaledRect(shipDrawable, tileWidth, tileHeight, tileLeft, tileTop),
                                null);
                    }
                    break;

                // revealed, no ship
                case 2:
                    if (!placementMode) {
                        canvas.drawBitmap(
                                missMarker,
                                null,
                                GetScaledRect(missMarker, tileWidth, tileHeight, tileLeft, tileTop),
                                null);
                    }
                    break;

                // revealed, hit ship
                case 3:
                    canvas.drawBitmap(
                            shipDrawable,
                            null,
                            GetScaledRect(shipDrawable, tileWidth, tileHeight, tileLeft, tileTop),
                            null);

                    if (!placementMode) {
                        canvas.drawBitmap(
                                hitMarker,
                                null,
                                GetScaledRect(missMarker, tileWidth, tileHeight, tileLeft, tileTop),
                                null);
                    }
                    break;
            }
        }
        canvas.restore();
    }

    private RectF GetScaledRect(Bitmap src, float tileWidth, float tileHeight, float tileLeft, float tileTop) {
        float aspect = (float)src.getWidth() / (float)src.getHeight();
        float newWidth;
        float newHeight;
        float newTop;
        float newLeft;

        if (src.getWidth() > src.getHeight()) {
            newWidth = tileWidth;
            newHeight = newWidth / aspect;
            newTop = tileTop + (tileHeight - newHeight)/2;
            newLeft = tileLeft;

        } else {
            newHeight = tileHeight;
            newWidth = newHeight * aspect;
            newTop = tileTop;
            newLeft = tileLeft + (tileWidth - newWidth)/2;
        }

        return new RectF(newLeft, newTop, newLeft + newWidth, newTop + newHeight);
    }

    // determine which square was hit based on relative x and y value
    private int hitWhichSquare(float testX, float testY) {
        int row = 0;
        int col = 0;

        // width of each square in the board
        float tileWidth = scaleFactor * width / 4;
        float tileHeight = scaleFactor * height / 4;

        // where is the top and left of the board right now
        float leftX = dx + (1-scaleFactor) * width / 2;
        float topY = dy + (1-scaleFactor) * height / 2;


        for (int i = 0; i < 4; i += 1) {
            // test if we are in column i (between the i line and i+1 line)
            // leftmost vertical line = line 0
            // example: if we are between line 2 and line 3, we are in row 2
            if (testX < (i+1) * tileWidth + leftX && i * tileWidth + leftX < testX) {
                col = i;
            }

            // test if we are in row i (between the i line and i+1 line)
            // topmost horizontal line = line 0
            // example: if we are between line 0 and line 1, we are in row 0
            if (testY < (i+1) * tileHeight + topY && i * tileHeight + topY < testY) {
                row = i;
            }
        }
        return row * 4 + col;
    }


    public boolean onTouchEvent(View v, MotionEvent ev) {
        // Let the ScaleGestureDetector inspect all events.
        scaleDetector.onTouchEvent(ev);

        float x = ev.getX();
        float y = ev.getY();

        int action = ev.getAction();

        switch (action) {
            case MotionEvent.ACTION_MOVE:

                // process translation of view
                dx += x - previousX;
                dy += y - previousY;
                previousX = x;
                previousY = y;
                v.invalidate();
                return true;

            case MotionEvent.ACTION_DOWN:

                // just to show which square was hit, make a toast
                int hit = hitWhichSquare(x, y);
                if (placementMode) {
                    toggleShipAtTile(hit);
                    v.invalidate();

                } else if (!turnTaken && !isRevealed(hit)) {
                    revealTile(hit);
                    boardView.invalidate();
                    turnTaken = true;
                }

                previousX = x;
                previousY = y;


                Toast.makeText(mContext, Integer.toString(hit), Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f));

            boardView.invalidate();
            return true;
        }
    }
}
