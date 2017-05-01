import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;


public class Node implements Comparable<Node> {
		
	private static final char LEAF = 0;
	private char ch;
//	private boolean isValid;
	private SkipList<Node> nexts = new SkipList<Node>(null);
	private Set<Integer> outputs = null;
	
	private Node failure = null;
	
	public Node() {
		this.ch = LEAF;
		this.failure = null;
//		this.isValid = false;
	}
	
	public Node(char ch) {
		this.ch = ch;
		this.failure = null;
//		this.isValid = false;
	}
	
	public Node(Character ch) {
		this.ch = ch.charValue();
	}
	
	public char getChar() {
		return this.ch;
	}
	
	public void setChar(char ch) {
		this.ch = ch;
	}
	
	public SkipList<Node> getNexts() {
		return this.nexts;
	}
	
	public void setNexts(SkipList<Node> nexts) {
		this.nexts = nexts;
	}
	
	public boolean isLeaf() {
		return this.ch == LEAF;
	}
	
	public Node setValid(boolean isValid) {
//		this.isValid = isValid;
		return this;
	}
	
	public boolean isValid() {
//		return this.isValid;
		return false;
	}
	
	public Node find(char ch) {
		return nexts.find(new Node(ch));
	}
	
	public Node nextState(char ch) {
		return nexts.find(new Node(ch));
	}
	
	public Node add(char ch) {
		Node node = new Node(ch);
		nexts.insert(node);
		return node;
	}
	
	public boolean hasChild() {
		return nexts.size() != 0;
	}
	
	public void linkWith(Node n) {
		this.nexts.insert(n);
	}
	
	public void setFailure(Node failure) {
		this.failure = failure;
	}
	
	public Node getFailure() {
		return this.failure;
	}
	
	public char[] getTransitions() {
		if (this.nexts == null)
			return null;
		char[] ret = new char[this.nexts.size()];
		
		int i = 0;
		Iterator<Node> iterator = this.nexts.iterator();
		while (iterator.hasNext()) {
			ret[i] = iterator.next().getChar();
			i++;
		}
		
		return ret;
	}
	
	public Collection<Integer> getOutputs() {
		return this.outputs == null ? Collections.<Integer>emptyList() : this.outputs;
	}
	
	public void addOutput(int index) {
		if (this.outputs == null) {
			this.outputs = new TreeSet<>(Collections.reverseOrder());
		}
		this.outputs.add(index);
	}
	
	public void addOutputs(Collection<Integer> outputs) {
		for (Integer output: outputs) {
			addOutput(output);
		}
	}

	@Override
	public int compareTo(Node o) {
		// TODO Auto-generated method stub
		if (this.ch < o.ch) {
			return -1;
		} else if (this.ch > o.ch) {
			return 1;
		} else {
			return 0;
		}
	}
	
	@Override
	public String toString() {
		return this.ch + " " + ((true) ? "true" : "false");
	}
}