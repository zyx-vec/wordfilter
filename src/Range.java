
public class Range {
	
	private int mStart;
	private int mEnd;
	
	public Range(int start, int end) {
		this.mStart = start;
		this.mEnd = end;
	}
	
	public void setStart(int start) {
		this.mStart = start;
	}
	
	public int getStart() {
		return this.mStart;
	}
	
	public void setEnd(int end) {
		this.mEnd = end;
	}
	
	public int getEnd() {
		return this.mEnd;
	}
}
