import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SkipListTrie extends Trie {
	@SuppressWarnings("unused")
	private final String TAG = "TRIE";
	
	private Node head;
	private short[] lengths;
	
	public SkipListTrie() {
		head = new Node();
	}
	
	public void build(List<String> keys) {
		lengths = new short[keys.size()];
		Iterator<String> iterator = keys.iterator();
		
		int i = 0;
		while (iterator.hasNext()) {
			this.insert(iterator.next(), i++);
		}
		
		constructFailureTransition();
	}
	
	// record at each `leaf` node for an complete word! (add a member to Node to as indicator for valid or not.)
	// TODO: public -> private
	public void insert(String s, int index) {
		CodePointIterator cpi = new CodePointIterator(s);
		Node ref = this.head;
		boolean flag = true;
		
		while (cpi.hasNext()) {
			
			if (!ref.hasChild()) {
				Node n = createLinkedList(cpi, index);
				ref.linkWith(n);
				flag = false;
				break;
			}
			
			char ch = cpi.next();
			
			Node pos = null;
			if ((pos = ref.find(ch)) == null) {
				ref = ref.add(ch);
			} else {
				ref = pos;
			}
		}
		if (flag) {
			ref.setValid(true);
			ref.addOutput(index);
		}
		this.lengths[index] = (short)s.length();
	}
	
	private Node createLinkedList(CodePointIterator cpi, int index) {
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
			curr.getNexts().insert(new Node());
			curr = curr.getNexts().fisrtNode();
		}
		// curr.setNexts(leaf);	// remove unneeded leaf node
		tmp.setValid(true);
		tmp.addOutput(index);
		return head;
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
	
//	public ArrayList<Integer> coverText(String text) {
//		CodePointIterator codePointInterator = new CodePointIterator(text);
//		char ch;
//		Node cur = this.head;
//		Node tmp;
//		int position = 0;
//		ArrayList<Integer> ranges = new ArrayList<>();
//		while (codePointInterator.hasNext()) {
//			ch = codePointInterator.next();
//			if ((tmp = cur.nextState(ch)) == null) {	// Jump to upper level
//				cur = cur.getFailure();
//				Node afterTrans = cur.nextState(ch);
//				while (afterTrans == null) {
//					cur = cur.getFailure();
//					afterTrans = cur.nextState(ch);
//					if (cur == this.head) {
//						break;
//					}
//				}
//				if (afterTrans != null) {
//					cur = afterTrans;
//				}
//			} else {
//				cur = tmp;
//			}
//			
//			position++;
//			Collection<Integer> outputs = cur.getOutputs();
//			for (Integer output: outputs) {
//				ranges.add(position-this.lengths[output]);
//				ranges.add(position);
//			}
//		}
//		
//		return ranges;
//	}
	
	public ArrayList<Range> coverText(String text) {
		ArrayList<Range> ranges = (ArrayList<Range>) parseText(text);
		
		ranges = (ArrayList<Range>) removeOverlaps(ranges);
		return ranges;
	}
	
	public Collection<Range> parseText(String text) {
		CodePointIterator codePointInterator = new CodePointIterator(text);
		char ch;
		Node cur = this.head;
		Node tmp;
		int position = 0;
		ArrayList<Range> ranges = new ArrayList<>();
		while (codePointInterator.hasNext()) {
			ch = codePointInterator.next();
			if ((tmp = cur.nextState(ch)) == null) {	// Jump to upper level
				cur = cur.getFailure();
				Node afterTrans = cur.nextState(ch);
				while (afterTrans == null) {
					cur = cur.getFailure();
					afterTrans = cur.nextState(ch);
					if (cur == this.head) {
						break;
					}
				}
				if (afterTrans != null) {
					cur = afterTrans;
				}
			} else {
				cur = tmp;
			}
			
			position++;
			Collection<Integer> outputs = cur.getOutputs();
			for (Integer output: outputs) {
				ranges.add(new Range(position-this.lengths[output], position));
			}
		}
		
		return ranges;
	}
	
	public Collection<Token> tokenize(String text) {
		ArrayList<Token> tokens = new ArrayList<>();
		Collection<Range> ranges = parseText(text);
		
		int prev = -1;
		for (Range range: ranges) {
			if (range.getStart() - prev > 1) {
				tokens.add(createUnMatchToken(range, text, prev));
			}
			
			tokens.add(createMatchToken(range, text));
			prev = range.getEnd();
		}
		
		if (text.length() - prev > 1) {
			tokens.add(createUnMatchToken(null, text, prev));
		}
		
		return tokens;
	}
	
	public int match(String text) throws FileNotFoundException, UnsupportedEncodingException {
		int count = 0;
		CodePointIterator codePointInterator = new CodePointIterator(text);
		char ch;
		Node cur = this.head;
		Node tmp;
		
		while (codePointInterator.hasNext()) {
			ch = codePointInterator.next();
			if ((tmp = cur.nextState(ch)) == null) {
				cur = cur.getFailure();
				Node afterTrans = cur.nextState(ch);
				while (afterTrans == null) {
					cur = cur.getFailure();
					afterTrans = cur.nextState(ch);
					if (cur == this.head) {
						break;
					}
				}
				if (afterTrans != null) {
					cur = afterTrans;
				}
			} else {
				cur = tmp;
			}
			
			count += cur.getOutputs().size();
		}
		
		return count;
	}
	
	private void constructFailureTransition() {
		Queue<Node> queue = new LinkedBlockingQueue<>();
		
		this.head.setFailure(this.head);
		
		for (Node firstLevel: this.head.getNexts()) {
			firstLevel.setFailure(this.head);
			queue.add(firstLevel);
		}
		
		while (!queue.isEmpty()) {
			Node cur = queue.remove();
			
			char[] transitions = cur.getTransitions();
			
			for (char ch: transitions) {
				Node targetNode = cur.nextState(ch);
				queue.add(targetNode);
				
				Node failureNode = cur.getFailure();
				while (failureNode.nextState(ch) == null) {
					failureNode = failureNode.getFailure();
					if (failureNode == this.head) {
						break;
					}
				}
				
				if (failureNode == this.head && failureNode.nextState(ch) == null) {
					failureNode = this.head;
				} else {
					failureNode = failureNode.nextState(ch);
				}
				
				targetNode.setFailure(failureNode);
				targetNode.addOutputs(failureNode.getOutputs());
			}
		}
	}
	
	private Token createUnMatchToken(Range range, String text, int prev) {
		return new UnMatchToken(text.substring(prev+1, range != null ? range.getStart(): text.length()));
	}
	
	private Token createMatchToken(Range range, String text) {
		return new MatchToken(text.substring(range.getStart(), range.getEnd() + 1), range);
	}
	
	// Original ranges not usable any more!
	private Collection<Range> removeOverlaps(Collection<Range> ranges) {
		if (ranges.size() == 0)
			return null;
		
		ArrayList<Range> res = new ArrayList<>();
		res.ensureCapacity(ranges.size());
		Range cur = ((ArrayList<Range>) ranges).get(0);
		
		int index = 0;
		res.add(cur);
		for (int i = 1; i < ranges.size(); i++) {
			cur = ((ArrayList<Range>) ranges).get(i);
			if (res.get(index).getEnd() >= cur.getStart()) {
				res.get(index).setEnd(cur.getEnd());
				if (res.get(index).getStart() > cur.getStart())
					res.get(index).setStart(cur.getStart());
			} else {
				index++;
				res.add(cur);
			}
		}
		
		return res;
	}
	
	public String BFS() {
		System.out.println(head.getNexts());
		
		SkipList<Node> cur = head.getNexts();
		ArrayList<Node> ns = cur.nodes();
		
		ConcurrentLinkedQueue<SkipList<Node>> queue = new ConcurrentLinkedQueue<>();
		
		for (int i = 0; i < ns.size(); i++) {
			System.out.println(ns.get(i).getNexts());
			queue.add(ns.get(i).getNexts());
		}
		
		
		while(queue.size() != 0) {
			SkipList<Node> curr = queue.peek();
		
			ArrayList<Node> ns1 = curr.nodes();
			for (int i = 0; i < ns1.size(); i++) {
				System.out.println(ns1.get(i).getNexts());
				if (!ns1.get(i).isLeaf())
					queue.add(ns1.get(i).getNexts());
			}
			
			if (curr.size() == 0) {
				queue.remove();
				continue;
			}
			
			queue.remove();
		}
		
		return "";
	}
	
	@Override
	public String toString() {
		return this.BFS();
	}
}
