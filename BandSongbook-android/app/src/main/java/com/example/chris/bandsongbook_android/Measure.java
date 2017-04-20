package com.example.chris.bandsongbook_android;

import java.util.ArrayList;

public class Measure implements Comparable<Measure>{

    public ArrayList<Pair> notes;
    public int number;

    public Measure(int number) {
        this.number = number;
        notes = new ArrayList<Pair>();
    }

    public void add(int string, int fret, int time) {
        Pair adder = new Pair(string, fret, time);
        System.out.println("Measure " + number + " " + string + " " + fret + " " + time);
        notes.add(adder);
    }

    public int compareTo(Measure measure) {
        return this.number - measure.number;
    }
}
