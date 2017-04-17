package xmlParser;

/**
 * simple pair class holding the string and the fret for a note
 * @author Chris Tsuei
 */
public class Pair {

	public int string;
	public int fret;
	public int time;
	public Pair(int string, int fret, int time) {
		this.string = string;
		this.fret = fret;
	}
	
	/**
	 * overriding compareTo
	 * @param compare
	 * @return
	 */
	public int compareTo(Pair compare) {
		return (this.time - compare.time);
	}
}
