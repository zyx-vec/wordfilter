public class UnMatchToken extends Token {

	public UnMatchToken(String fragment) {
		super(fragment);
	}

	@Override
	public boolean isMatch() {
		return false;
	}

	@Override
	public Range getMatchRange() {
		return null;
	}
	
}