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
	public static ArrayList<Measure> notes;

	/**
	 * constructor for the PartInfo class
	 * @param lines number of lines in the part
	 * @param measures number of measures in part
	 * @param partName name of the part
	 */
	public PartInfo (int lines, int measures, String partName) {
		this.lines = lines;
		this.measures = measures;
		this.partName = partName;
		notes = new ArrayList<Measure>();
	}

	/**
	 * add a measure to the notes arraylist
	 * @param measure the measure containing notes to be added
	 */
	public void add(Measure measure) {
		notes.add(measure);
	}
}
