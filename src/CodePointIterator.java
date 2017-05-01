public class CodePointIterator {
	
	private final String sequence;
	private int index = 0;
	
	public CodePointIterator(String sequence) {
		this.sequence = sequence;
	}
	
	public boolean hasNext() {
		final int len = this.sequence.length();
		return index < len;
	}
	
	public char next() {
		char codePoint = this.sequence.charAt(index);
		index += Character.charCount(codePoint);
		return codePoint;
	}
}