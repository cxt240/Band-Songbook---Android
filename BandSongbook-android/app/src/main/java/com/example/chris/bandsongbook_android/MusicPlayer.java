package com.example.chris.bandsongbook_android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
    public static int divisions;
    public static int measure;
    public static int height;
    public static int width;
    public static int pixels;
    public static double divSeconds;
    public static double[] lines;
    private Paint paint;

    public static PartInfo PartMeasures;

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
        paint.setTextSize(42);
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
                   // canvas.drawText(Integer.toString(linesAbove - i), width/2, (int)(height / 2 - (100 * i)), paint);
                    lines[linesAbove - i - 1] = (height / 2 - (100 * i));
                }
                for (int i = 1; i < linesAbove + 1; i++) {
                    canvas.drawLine(0, (height / 2 + (100 * i)), width, (height / 2 + (100 * i)), paint);
                    //canvas.drawText(Integer.toString(linesAbove + i), width/2, (int)(height / 2 + (100 * i)), paint);
                    lines[linesAbove + i] = (double)(height / 2 + (100 * i));
                }
            }
            else {
                paint.setStrokeWidth(7);
                int linesAbove = strings / 2;
                double height_above = height / 2 - 50;
                double height_below = height / 2 + 50;
                for(int i = 0; i < linesAbove; i++) {
                    canvas.drawLine(0, (float)(height_above - (100 * i)), width, (float)(height_above - (100 * i)), paint);
//                    canvas.drawText(Integer.toString(linesAbove - i -1), width/2, (int)(height_above - (100 * i)), paint);
                    lines[linesAbove - i - 1] =  (height_above - (100 * i));
                }
                for(int i = 0; i < linesAbove; i++) {
                    canvas.drawLine(0, (float)(height_below + (100 * i)), width, (float)(height_below + (100 * i)), paint);
                    lines[linesAbove + i] =  (height_below + (100 * i));
//                    canvas.drawText(Integer.toString(linesAbove + i), width/2, (int)(height_below + (100 * i)), paint);
                }
            }

            ArrayList<Measure> display = display();
            for(int i = 0; i < display.size(); i++) {
                Measure draw = display.get(i);
                int measure_time = draw.number * divisions;
                int line = (pixels * (measure_time - current));
                canvas.drawLine(line, 0, line, height, paint);
                for(int j = 0; j < draw.notes.size(); j++) {
                    int time = (measure_time) + draw.notes.get(j).time;
                    if(time > current && time < current_end) {
                        int string = draw.notes.get(j).string - 1;
                        int fret = draw.notes.get(j).fret;
                        canvas.drawText(Integer.toString(fret), (int)(pixels * (time - current)), (float)lines[string], paint);
                    }
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

        // infornation from the MusicXml header
        measure = parser.measures;
        divisions = parser.divisions;

        // setting up the player from the beginning
        divSeconds = divisions * (parser.tempo / 60);
        current_end = divisions * 2;
        current = -1 * divisions;
        pixels = width / (3 * divisions);
    }

    public static ArrayList<Measure> display() {
        int currMeasure = current - (current % divisions);
        int endMeasure = (int) Math.ceil(current_end / divisions);
        ArrayList<Measure> display = new ArrayList<Measure>();
        for(int i = 0; i < PartInfo.notes.size(); i++) {
            Measure current = PartInfo.notes.get(i);
            if(current.number > currMeasure && current.number < endMeasure) {
                display.add(PartInfo.notes.get(i));
            }
        }
        return display;
    }

    public static void speedChanged(int speed) {
        if(speed == 2) {

        }
    }
}
