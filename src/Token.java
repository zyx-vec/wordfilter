public abstract class Token {
	
	private String mFragment;
	
	public Token(String fragment) {
		this.mFragment = fragment;
	}
	
	public void setFragment(String fragment) {
		this.mFragment = fragment;
	}
	
	public String getFragment() {
		return this.mFragment;
	}
	
	public abstract boolean isMatch();
	
	public abstract Range getMatchRange();
}