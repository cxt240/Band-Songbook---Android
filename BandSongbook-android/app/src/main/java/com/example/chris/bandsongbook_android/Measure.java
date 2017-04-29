package com.example.chris.bandsongbook_android;

import java.util.ArrayList;

/**
 * Measure class which contains notes
 * @author Chris Tsuei
 */
public class Measure implements Comparable<Measure>{

    public ArrayList<Pair> notes;
    public int number;

    /**
     * constructor for the class
     * @param number measure number
     */
    public Measure(int number) {
        this.number = number;
        notes = new ArrayList<Pair>();
    }

    /**
     * adds a note to the notes arrayList
     * @param string string of the note in tab
     * @param fret fret of the note in the tab
     * @param time time the note should be played (relative to the start of the measure)
     */
    public void add(int string, int fret, int time) {
        Pair adder = new Pair(string, fret, time);
     //   System.out.println("Measure " + number + " " + string + " " + fret + " " + time);
        notes.add(adder);
    }

    /**
     * comparesTo override
     * @param measure measure this object is to be compared to
     * @return comparison between measure numbers
     */
    @Override
    public int compareTo(Measure measure) {
        return this.number - measure.number;
    }
}
