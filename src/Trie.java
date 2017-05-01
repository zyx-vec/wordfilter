import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Trie {
	@SuppressWarnings("unused")
	private final String TAG = "TRIE";
	
	private static class Node {
		
		private static final char LEAF = 0;
		private char ch;
		private boolean isValid;
		private ArrayList<Node> next = new ArrayList<Node>();
		
		public Node() { 
			this.ch = LEAF;
			this.isValid = false;
		}
		
		public Node(char ch) {
			this.ch = ch;
		}
		
		public char getChar() {
			return this.ch;
		}
		
		public void setChar(char ch) {
			this.ch = ch;
		}
		
		public ArrayList<Node> getNexts() {
			return this.next;
		}
		
		public boolean isLeaf() {
			return this.ch == LEAF;
		}
		
		public Node setValid(boolean isValid) {
			this.isValid = isValid;
			return this;
		}
		
		public boolean isValid() {
			return this.isValid;
		}
		
		public Node find(char ch) {
			for(int i = 0; i < this.next.size(); i++) {
				if (this.next.get(i).getChar() == ch) {
					return this.next.get(i);
				}
			}
			return null;
		}
		
		public Node add(char ch) {
			Node node = new Node(ch);
			next.add(node);
			return node;
		}
		
		public boolean hasChild() {
			return next.size() != 0;
		}
		
		public void linkWith(Node n) {
			this.next.add(n);
		}
	}
	
	private Node head;
	
	public Trie() {
		head = new Node();
	}
	
	// record at each `leaf` node for an complete word! (add a member to Node to as indicator for valid or not.)
	public void insert(String s) {
		CodePointIterator cpi = new CodePointIterator(s);
		Node ref = this.head;
		boolean flag = true;
		while (cpi.hasNext()) {
			
			if (!ref.hasChild()) {
				Node n = createLinkedList(cpi);
				ref.linkWith(n);
				flag = false;
				break;
			}
			
			char ch = cpi.next();
			
			Node pos = null;
			if ((pos = ref.find(ch)) == null) {
				ref = ref.add(ch);
				if (!cpi.hasNext()) {
					ref.getNexts().add(new Node());	// add leaf node to the last added node, because no createLinkedList called.
					break;
				}
			} else {
				ref = pos;
			}
		}
		if (flag) {
			ref.setValid(true);
		}
	}
	
	public boolean find(String s) {
		boolean ret = true;
		CodePointIterator cpi = new CodePointIterator(s);
		char ch;
		Node ref = this.head;
		while(cpi.hasNext()) {
			ch = cpi.next();
			if ((ref = ref.find(ch)) == null) {	// search at current level
				ret = false;
				break;
			}
		}
		
		return ret && ref.isValid();
	}
	
	public String BFS() {
		ConcurrentLinkedQueue<Node> queue = new ConcurrentLinkedQueue<>();
		
		Iterator<Node> iterator = this.head.getNexts().iterator();
		while(iterator.hasNext()) {
			queue.add(iterator.next());
		}
		
		while(queue.size() != 0) {
			Node curr = queue.peek();
			
			iterator = curr.getNexts().iterator();
			while(iterator.hasNext()) {
				queue.add(iterator.next());
			}
			
			if (curr.isLeaf()) {
				queue.remove();
				continue;
			}
			
			System.out.print(curr.getChar());
			queue.remove();
		}
		return "";
	}
	
	// Code reuse
	private Node createLinkedList(CodePointIterator cpi) {
		Node head = new Node();
		Node curr = head;
		Node tmp = null;
		while(cpi.hasNext()) {
			char ch = cpi.next();
			curr.setChar(ch);
			tmp = curr;
			if (!cpi.hasNext()) {
				break;
			}
			curr.getNexts().add(new Node());
			curr = curr.getNexts().get(0);
		}
		
		tmp.setValid(true);
		return head;
	}
}
