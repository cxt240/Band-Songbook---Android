package com.example.chris.bandsongbook_android;

import java.util.ArrayList;

/**
 * all of the information needed for a single part
 * @author Chris Tsuei
 */
public class PartInfo {

	public int lines;
	public int measures;
	public String partName;
	public ArrayList<Pair> notes;
	
	public PartInfo (int lines, int measures, String partName) {
		this.lines = lines;
		this.measures = measures;
		this.partName = partName;		
		notes = new ArrayList<Pair>();
	}
	
	public void add(int string, int fret, int time) {
		Pair adder = new Pair(string, fret, time);
		System.out.println(string + " " + fret + " " + time);
		notes.add(adder);
	}
}
