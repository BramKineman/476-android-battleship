package edu.msu.roneyka1.project1_temp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.view.MotionEventCompat;

public class BoardView extends View {

    Paint linePaint;
    Paint fillPaint;
    private ScaleGestureDetector scaleDetector;
    private float scaleFactor;
    private float angle;
    private float dx;
    private float dy;
    private float width;
    private float height;
    private float previousX;
    private float previousY;
    private Context mContext;
    private Board board;

    public BoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public Board getBoard() {return board;}

    private void init () {
        board = new Board(getContext());
        board.setBoardView(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return board.onTouchEvent(this, ev);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        board.draw(canvas);
    }

    public void saveInstanceState(Bundle bundle) {board.saveInstanceState(bundle);}
    public void loadInstanceState(Bundle bundle) {board.loadInstanceState(bundle);}
}
