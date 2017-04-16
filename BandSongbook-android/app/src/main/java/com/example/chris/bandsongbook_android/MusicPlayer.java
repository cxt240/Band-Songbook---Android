package com.example.chris.bandsongbook_android;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import java.util.Random;

/**
 * Created by Chris on 4/15/2017.
 */

public class MusicPlayer extends View{

    public MusicPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Random rand = new Random();
        canvas.drawRGB(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    }

    public void changeColor() {
        invalidate(); // redraws the view calling onDraw()
    }
}
