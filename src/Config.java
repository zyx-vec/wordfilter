public class Config {
	// TODO: Finish unimplemented functions
	private final static String TAG = "CONFIG";
	
	private boolean allowOverlaps = true;
	
	private boolean wholeWords = false;
	
	private boolean caseSensitive = true;
	
	public boolean isAllowOverlaps() {
		return this.allowOverlaps;
	}
	
	public void setAllowOverlaps(boolean allowOverlaps) {
		this.allowOverlaps = allowOverlaps;
	}
	
	public boolean isOnlyWholeWords() {
		return this.wholeWords;
	}
	
	public void setOnlyWholeWords(boolean wholeWords) {
		this.wholeWords = wholeWords;
	}
	
	public boolean isCaseSensitive() {
		return this.caseSensitive;
	}
	
	public void setCaseSensitive(boolean caseSenstive) {
		this.caseSensitive = caseSenstive;
	}
}