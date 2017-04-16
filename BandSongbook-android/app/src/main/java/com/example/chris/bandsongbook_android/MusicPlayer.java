package com.example.chris.bandsongbook_android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import java.util.Random;

/**
 * Created by Chris on 4/15/2017.
 */

public class MusicPlayer extends View{

    public int current;
    public int height;
    public int width;
    private Paint paint;

    public MusicPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // red "current" line
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);
        canvas.drawLine(width/3, 0, width/3, height, paint);

        paint.setColor(Color.BLACK);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w;
        height = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }
    public void updateView() {
        invalidate(); // redraws the view calling onDraw()
    }
}
