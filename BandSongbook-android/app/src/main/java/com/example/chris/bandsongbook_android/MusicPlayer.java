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

    public static int current;
    public static int current_end;
    public static int height;
    public static int width;
    public static double[] lines;
    private Paint paint;

    public static PartInfo PartMeasures;

    public MusicPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
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
        if(PartMeasures != null) {
            lines = new double[PartMeasures.lines];
            int strings = PartMeasures.lines;
            if(strings % 2 == 1) {
                paint.setStrokeWidth(5);
                canvas.drawLine(0, height/2, width, height / 2, paint);
                int linesAbove = (strings - 1) / 2;
                lines[linesAbove] = (double)height / 2;
                for(int i = 1; i < linesAbove + 1; i++) { //drawing above the middle line
                    canvas.drawLine(0, (height / 2 - (100 * i)), width, (height / 2 - (100 * i)), paint);
                    lines[linesAbove - i] = (height / 2 - (25 * i));
                }
                for (int i = 1; i < linesAbove + 1; i++) {
                    canvas.drawLine(0, (height / 2 + (100 * i)), width, (height / 2 + (100 * i)), paint);
                    lines[linesAbove + i] = (double)(height / 2 + (25 * i));
                }
            }
            else {
                paint.setStrokeWidth(7);
                int linesAbove = strings / 2;
                double height_above = height / 2 - 50;
                double height_below = height / 2 + 50;
                for(int i = 0; i < linesAbove; i++) {
                    canvas.drawLine(0, (float)(height_above - (100 * i)), width, (float)(height_above - (100 * i)), paint);
                    lines[linesAbove - i] =  (height_above - (25 * i));
                }
                for(int i = 0; i < linesAbove; i++) {
                    canvas.drawLine(0, (float)(height_below + (100 * i)), width, (float)(height_below + (100 * i)), paint);
                    lines[linesAbove] =  (height_below + (25 * i));
                }
            }
        }
        
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

    public static void songChanged(String s, int PartNo) {

        MusicXmlParser parser = new MusicXmlParser();
        parser.parser(s);
        PartMeasures = parser.PartMeasures.get(PartNo);
        lines = new double[PartMeasures.lines];
        current = -1000;
        current_end = 2000;
    }
}
