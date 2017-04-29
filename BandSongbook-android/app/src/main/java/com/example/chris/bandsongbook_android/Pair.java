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
	}

	/**
	 * overriding compareTo
	 * @param compare the note that needs to be compared
	 * @return int describing the relation to the other pair
	 */
	public int compareTo(Pair compare) {
		return (this.time - compare.time);
	}
}
