public class MatchToken extends Token {
	
	private final Range mRange;

	public MatchToken(String fragment, Range range) {
		super(fragment);
		mRange = range;
	}

	@Override
	public boolean isMatch() {
		return true;
	}

	@Override
	public Range getMatchRange() {
		return mRange;
	}
	
}