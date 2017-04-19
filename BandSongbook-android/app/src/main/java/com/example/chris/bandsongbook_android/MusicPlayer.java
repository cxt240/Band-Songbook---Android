package com.example.chris.bandsongbook_android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.icu.text.MessagePattern;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Chris on 4/15/2017.
 */

public class MusicPlayer extends View{

    public int current;
    public int current_end;
    public int height;
    public int width;
    private Paint paint;

    public static PartInfo PartMeasures;
    public MusicXmlParser parser;

    public MusicPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        parser = new MusicXmlParser();
        current = -1000;
        current_end = 2000;
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

    public void songChanged(String s, int PartNo) {

        MusicXmlParser parser = new MusicXmlParser();
        parser.parser(s);
        PartMeasures = parser.PartMeasures.get(PartNo);
        current = -1000;
        current_end = 2000;
    }
}
