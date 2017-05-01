package com.example.chris.bandsongbook_android;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * MusicXML displayer
 * @author Chris Tsuei
 */
public class MusicPlayer extends View{

    public static double current;
    public static double current_end;
    public static double divisions;
    public static int measure;
    public static int height;
    public static int width;
    public static double divSeconds;
    public static double[] lines;
    private Paint paint;
    public static PartInfo PartMeasures;

    /**
     * constructor for the musicXML player
     * @param context current context
     * @param attrs attributes to be applied
     */
    public MusicPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
    }

    /**
     * drawing to the screen (on the canvas object)
     * @param canvas to be drawn on
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // red "current" line
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);
        paint.setTextSize(50);
        canvas.drawLine(width/3, 0, width/3, height, paint); // play bar
        paint.setColor(Color.BLACK);
        if(PartMeasures != null) { // parts to be drawn
            lines = new double[PartMeasures.lines];
            int strings = PartMeasures.lines;
            if(strings % 2 == 1) {  // odd number of strings
                paint.setStrokeWidth(7);
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
            else {  // even number of strings
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

            int pixels = width / 3;
            ArrayList<Measure> display = display();
            Log.v("times: ", current + " " + current_end);
            Log.v("dim: ", height + " " + width);
            for(int i = 0; i < display.size(); i++) {
                Measure draw = display.get(i);

                double measure_time = divisions * draw.number;
                if (measure_time > current && measure_time < current_end) { //drawing the measure seperators
                    paint.setColor(Color.GRAY);
                    double distance = measure_time - current;
                    double line = (distance / (3 * divisions));
                    int y = (int)(line * width);
                    if (y <= width / 3) { // change color if past play bar
                        paint.setColor(Color.GRAY);
                    }
                    else {
                        paint.setColor(Color.BLUE);
                    }
                    canvas.drawText(Integer.toString(draw.number), y, 0, paint);
                    canvas.drawLine(y, 0, y, height, paint);
//                    Log.v("time: ", measure_time + " " + i);
//                    Log.v("size: ", " " + line + " " + distance + " ");
                }

                for(int j = 0; j < draw.notes.size(); j++) { // drawing the notes to the screen
                    double time = measure_time + (double) draw.notes.get(j).time;
                    int string = draw.notes.get(j).string;
                    int fret = draw.notes.get(j).fret;
                    double distance = time - current;
                    double spot = (distance / (3 * divisions)) * (double) width;
                    Log.v("measureSize ", draw.number + " " + draw.notes.size() + " ");
                    Log.v("time ", " " + draw.notes.get(j).time);
                    Log.v("info: ", fret + " " + string + " " + time);
                    if(spot <= width / 3) { // change color if behind play bar
                        paint.setColor(Color.BLACK);
                    }
                    else {
                        paint.setColor(Color.BLUE);
                    }
                    canvas.drawText(Integer.toString(fret), (int) spot, (float)lines[string], paint);
                }
            }
        }
        
    }

    /**
     * if canvas changed size recently
     * @param w new width
     * @param h new height
     * @param oldw old width
     * @param oldh old height
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.width = w;
        this.height = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * forces the view to redraw
     */
    public void updateView() {
        invalidate(); // redraws the view calling onDraw()
    }

    /**
     * if the song has changed (or part change)
     * @param s the name of the song
     * @param PartNo the part to change to
     * @param context the current context of the activity
     */
    public static void songChanged(String s, int PartNo, Context context) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String xmlString = sharedPref.getString(s, "Not available");
        MusicXmlParser parser = new MusicXmlParser();
        parser.parser(xmlString);
        PartMeasures = parser.PartMeasures.get(PartNo);
        lines = new double[PartMeasures.lines];

        // information from the MusicXml header
        measure = parser.measures;
        divisions = (parser.num + 1) * parser.divisions * (4 / parser.denom);

        // setting up the player from the beginning
        divSeconds = divisions * (parser.tempo / 60);
        current_end = divisions * 2;
        current = divisions * -1;
    }

    /**
     * gets the measures that can be displayed on the canvas
     * @return an Arraylist of the measures that can be displayed
     */
    public static ArrayList<Measure> display() {
        int currMeasure = (int) (current / divisions); // lower measure number bound
        double endMeasure = (int) Math.ceil(current_end / divisions); // upper measure bound
        ArrayList<Measure> display = new ArrayList<Measure>();
        for(int i = 0; i < PartInfo.notes.size(); i++) {
            Measure current = PartInfo.notes.get(i);
            if(current.number > currMeasure && current.number < endMeasure) {
                display.add(PartInfo.notes.get(i));
            }
        }
        return display;
    }
}
