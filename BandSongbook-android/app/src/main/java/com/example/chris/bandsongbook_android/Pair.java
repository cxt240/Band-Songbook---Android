package com.example.chris.bandsongbook_android;

/**
 * simple pair class holding the string and the fret for a note
 * @author Chris Tsuei
 */
public class Pair implements Comparable<Pair> {

	public int string;
	public int fret;
	public int time;
	public Pair(int string, int fret, int time) {
		this.string = string;
		this.fret = fret;
        this.time = time;
	}

	public int compareTo(Pair pair2) {

        return this.time - pair2.time;
    }
}
