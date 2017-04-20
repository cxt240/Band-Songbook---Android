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
	public ArrayList<Measure> notes;

	public PartInfo (int lines, int measures, String partName) {
		this.lines = lines;
		this.measures = measures;
		this.partName = partName;
		notes = new ArrayList<Measure>();
	}

	public void add(Measure measure) {
		notes.add(measure);
	}
}
