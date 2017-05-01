import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

public class ACDoubleArraySkipListTrie implements Serializable {
	private static final long serialVersionUID = 1L;
	protected int check[];
	protected int base[];
	protected int fail[];
	protected int[][] output;
	protected int[] l;
	protected int size;
	
	public ArrayList<Range> coverText(String text) {
		ArrayList<Range> ranges = (ArrayList<Range>) parseText(text);
		
		ranges = (ArrayList<Range>) removeOverlaps(ranges);
		return ranges;
		
//		int[] result = new int[ranges.size() * 2];
//		Range cur = ranges.get(0);
//		result[0] = cur.getStart();
//		result[1] = cur.getEnd();
//		int index = 0;
//		for (int i = 1; i < ranges.size(); i++) {
//			cur = ranges.get(i);
//			if (result[index+1] >= cur.getStart()) {
//				result[index+1] = cur.getEnd();
//				if (result[index] > cur.getStart())
//					result[index] = cur.getStart();
//			} else {
//				index += 2;
//				result[index] = cur.getStart();
//				result[index+1] = cur.getEnd();
//			}
//		}
//
//		index += 2;
//		ArrayList<Integer> intList = new ArrayList<Integer>();
//		intList.ensureCapacity(index);
//		for(int i = 0; i < index; i++) {
//			intList.add(result[i]);
//		}
//		
//		return intList;
	}

	public List<Range> parseText(String text) {
		int position = 1;
		int currentState = 0;
		List<Range> collectedEmits = new ArrayList<Range>();
		for (int i = 0; i < text.length(); ++i) {
			currentState = getState(currentState, text.charAt(i));
			storeEmits(position, currentState, collectedEmits);
			++position;
	    }

		return collectedEmits;
	}

	public void parseText(String text, IHit processor) {
		int position = 1;
		int currentState = 0;
		for (int i = 0; i < text.length(); ++i) {
			currentState = getState(currentState, text.charAt(i));
			int[] hitArray = output[currentState];
			if (hitArray != null) {
				for (int hit : hitArray) {
					processor.hit(position - l[hit], position);
				}
			}
			++position;
		}
	}

	public void parseText(char[] text, IHit processor) {
		int position = 1;
		int currentState = 0;
		for (char c : text) {
			currentState = getState(currentState, c);
			int[] hitArray = output[currentState];
			if (hitArray != null) {
				for (int hit : hitArray) {
					processor.hit(position - l[hit], position);
				}
			}
			++position;
		}
	}

	public void parseText(char[] text, IHitFull processor) {
		int position = 1;
		int currentState = 0;
		for (char c : text) {
			currentState = getState(currentState, c);
			int[] hitArray = output[currentState];
			if (hitArray != null) {
				for (int hit : hitArray) {
					processor.hit(position - l[hit], position, hit);
				}
			}
			++position;
		}
	}

	public void save(ObjectOutputStream out) throws IOException {
		out.writeObject(base);
		out.writeObject(check);
		out.writeObject(fail);
		out.writeObject(output);
		out.writeObject(l);
	}

	public void load(ObjectInputStream in) throws IOException, ClassNotFoundException {
		base = (int[]) in.readObject();
		check = (int[]) in.readObject();
		fail = (int[]) in.readObject();
		output = (int[][]) in.readObject();
		l = (int[]) in.readObject();
	}

//	public V get(String key) {
//		int index = exactMatchSearch(key);
//		if (index >= 0) {
//			return v[index];
//		}
//
//		return null;
//	}

//	public V get(int index) {
//		return v[index];
//	}

	public interface IHit {
		void hit(int begin, int end);
	}

	public interface IHitFull {
		void hit(int begin, int end, int index);
	}

	@SuppressWarnings("hiding")
	public class Hit {
		public final int begin;
		public final int end;

		public Hit(int begin, int end) {
			this.begin = begin;
			this.end = end;
		}

		@Override
		public String toString() {
			return String.format("[%d:%d]", begin, end);
		}
	}

	private int getState(int currentState, char character) {
		int newCurrentState = transitionWithRoot(currentState, character);
		while (newCurrentState == -1) {
			currentState = fail[currentState];
			newCurrentState = transitionWithRoot(currentState, character);
		}
		return newCurrentState;
	}

	private void storeEmits(int position, int currentState, List<Range> collectedEmits) {
		int[] hitArray = output[currentState];
		if (hitArray != null) {
			for (int hit : hitArray) {
				collectedEmits.add(new Range(position - l[hit], position));
			}
		}
	}

	protected int transition(int current, char c) {	// Trans to next state
		int b = current;
		int p;

		p = b + c + 1;
		if (b == check[p])
			b = base[p];
		else
			return -1;

		p = b;
		return p;
	}

	protected int transitionWithRoot(int nodePos, char c) {
		int b = base[nodePos];
		int p;

		p = b + c + 1;
		if (b != check[p]) {
			if (nodePos == 0) return 0;
				return -1;
		}

		return p;
	}

	public void build(ArrayList<String> keys) {
		new Builder().build(keys);
	}

	public int exactMatchSearch(String key) {
		return exactMatchSearch(key, 0, 0, 0);
	}

	private int exactMatchSearch(String key, int pos, int len, int nodePos) {
		if (len <= 0)
			len = key.length();
		if (nodePos <= 0)
			nodePos = 0;

		int result = -1;

		char[] keyChars = key.toCharArray();

		int b = base[nodePos];
		int p;

		for (int i = pos; i < len; i++) {
			p = b + (int) (keyChars[i]) + 1;
			if (b == check[p])
				b = base[p];
			else
				return result;
		}

		p = b;
		int n = base[p];
		if (b == check[p] && n < 0) {
			result = -n - 1;
		}
		return result;
	}

//	public int size() {
//		return v.length;
//	}

	private class Builder {
		private State rootState = new State();
		private boolean used[];
		private int allocSize;
		private int progress;
		private int nextCheckPos;
		private int keySize;
		
		@SuppressWarnings("unchecked")
		public void build(ArrayList<String> keys) {
			l = new int[keys.size()];
			addAllKeyword(keys);
			buildDoubleArrayTrie(keys.size());
			used = null;
			// 构建failure表并且合并output表
			constructFailureStates();
			rootState = null;
			loseWeight();
		}

		private int fetch(State parent, List<Map.Entry<Integer, State>> siblings) {
			if (parent.isAcceptable()) {
				State fakeNode = new State(-(parent.getDepth() + 1));  // 此节点是parent的子节点，同时具备parent的输出
				fakeNode.addEmit(parent.getLargestValueId());
				siblings.add(new AbstractMap.SimpleEntry<Integer, State>(0, fakeNode));
			}
			for (Map.Entry<Character, State> entry : parent.getSuccess().entrySet()) {	// Output values
				siblings.add(new AbstractMap.SimpleEntry<Integer, State>(entry.getKey() + 1, entry.getValue()));
			}
			return siblings.size();
		}

		private void addKeyword(String keyword, int index) {
			State currentState = this.rootState;
			for (Character character : keyword.toCharArray()) {
				currentState = currentState.addState(character);
			}
			// Patterns when matched
			currentState.addEmit(index);	// index to value array
			l[index] = keyword.length();	// corresponding length
		}

		private void addAllKeyword(Collection<String> keywordSet) {
			int i = 0;
			for (String keyword : keywordSet) {
				addKeyword(keyword, i++);
			}
		}

		private void constructFailureStates() {
			fail = new int[size + 1];
			fail[1] = base[0];
			output = new int[size + 1][];
			Queue<State> queue = new LinkedBlockingDeque<State>();

			for (State depthOneState : this.rootState.getStates()) {
				depthOneState.setFailure(this.rootState, fail);
				queue.add(depthOneState);
				constructOutput(depthOneState);
			}

			while (!queue.isEmpty()) {
				State currentState = queue.remove();

				for (Character transition : currentState.getTransitions()) {
					State targetState = currentState.nextState(transition);
					queue.add(targetState);

					State traceFailureState = currentState.failure();
					while (traceFailureState.nextState(transition) == null) {
						traceFailureState = traceFailureState.failure();
					}
					State newFailureState = traceFailureState.nextState(transition);
					targetState.setFailure(newFailureState, fail);
					targetState.addEmit(newFailureState.emit());	// Suffixes, TODO: Fix SkipListTrie version
					constructOutput(targetState);
				}
			}
		}

		private void constructOutput(State targetState) {
			Collection<Integer> emit = targetState.emit();
			if (emit == null || emit.size() == 0) return;
			int output[] = new int[emit.size()];
			Iterator<Integer> it = emit.iterator();
			for (int i = 0; i < output.length; ++i) {
				output[i] = it.next();
			}
			ACDoubleArraySkipListTrie.this.output[targetState.getIndex()] = output;
		}

		private void buildDoubleArrayTrie(int keySize) {
			progress = 0;
			this.keySize = keySize;
			resize(65536 * 32);

			base[0] = 1;
			nextCheckPos = 0;

			State root_node = this.rootState;

			List<Map.Entry<Integer, State>> siblings = new ArrayList<Map.Entry<Integer, State>>(root_node.getSuccess().entrySet().size());
			fetch(root_node, siblings);
			insert(siblings);
		}

		private int resize(int newSize) {
			int[] base2 = new int[newSize];
			int[] check2 = new int[newSize];
			boolean used2[] = new boolean[newSize];
			if (allocSize > 0) {
				System.arraycopy(base, 0, base2, 0, allocSize);
				System.arraycopy(check, 0, check2, 0, allocSize);
				System.arraycopy(used, 0, used2, 0, allocSize);
			}

			base = base2;
			check = check2;
			used = used2;

			return allocSize = newSize;
		}

		private int insert(List<Map.Entry<Integer, State>> siblings) {
			int begin = 0;
			int pos = Math.max(siblings.get(0).getKey() + 1, nextCheckPos) - 1;
			int nonzero_num = 0;
			int first = 0;

			if (allocSize <= pos)
				resize(pos + 1);

			outer: while (true) {	// Find free slots
				pos++;

				if (allocSize <= pos)
					resize(pos + 1);

				if (check[pos] != 0) {
					nonzero_num++;
					continue;
				}
				else if (first == 0) {
					nextCheckPos = pos;
					first = 1;
				}

				begin = pos - siblings.get(0).getKey(); // 当前位置离第一个兄弟节点的距离
				if (allocSize <= (begin + siblings.get(siblings.size() - 1).getKey())) {
					double l = (1.05 > 1.0 * keySize / (progress + 1)) ? 1.05 : 1.0 * keySize / (progress + 1);
					resize((int) (allocSize * l));
				}

				if (used[begin])
					continue;

				for (int i = 1; i < siblings.size(); i++)
					if (check[begin + siblings.get(i).getKey()] != 0)
						continue outer;

				break;
			}
			

			if (1.0 * nonzero_num / (pos - nextCheckPos + 1) >= 0.95)
				nextCheckPos = pos;
			used[begin] = true;

			size = (size > begin + siblings.get(siblings.size() - 1).getKey() + 1) ? size :
				begin + siblings.get(siblings.size() - 1).getKey() + 1;

			// State transitions added to array, info embeded in array indexes
			for (Map.Entry<Integer, State> sibling : siblings) {
				check[begin + sibling.getKey()] = begin;	// Integer as char value
			}

			for (Map.Entry<Integer, State> sibling : siblings) {
				// Trans to next level
				List<Map.Entry<Integer, State>> new_siblings = 
						new ArrayList<Map.Entry<Integer, State>>(sibling.getValue().getSuccess().entrySet().size() + 1);

				if (fetch(sibling.getValue(), new_siblings) == 0) {
					base[begin + sibling.getKey()] = (-sibling.getValue().getLargestValueId() - 1);
					progress++;
				}
				else {
					int h = insert(new_siblings);	// recursion
					base[begin + sibling.getKey()] = h;
				}
				sibling.getValue().setIndex(begin + sibling.getKey());
			}
			return begin;
		}

		private void loseWeight() {
			int nbase[] = new int[size + 65535];
			System.arraycopy(base, 0, nbase, 0, size);
			base = nbase;

			int ncheck[] = new int[size + 65535];
			System.arraycopy(check, 0, ncheck, 0, size);
			check = ncheck;
		}
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
}
