package xmlParser;

/**
 * all of the information needed for a single part
 * @author Chris Tsuei
 */
public class PartInfo {

	public int lines;
	public int measures;
	public String partName;
	public MeasureInfo[] notes;
	
	public PartInfo (int lines, int measures, String partName) {
		this.lines = lines;
		this.measures = measures;
		this.partName = partName;
		
		notes = new MeasureInfo[this.measures];
	}
}
