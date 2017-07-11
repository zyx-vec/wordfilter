import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

import javax.security.auth.x500.X500Principal;

public class SkipList<T extends Comparable<T>> implements Iterable<T> {
	
	private final String TAG = "SKIPLIST";
	private final static int INIT_HEIGHT = 1;
	
	private Node<T> head;
	private int height;
	private int size;
	
	private static class Node<T> {
		
		private int mHeight;
		private ArrayList<Node<T>> mNext;
		
		private T mVal;
		
		public Node(int height) {
			mHeight = height;
			mNext = new ArrayList<>(mHeight);
			for (int i = 0; i < mHeight; i++)
				mNext.add(null);
		}
		
		public void setValue(T val) {
			mVal = val;
		}
		
		public T getValue() {
			return mVal;
		}
		
		public void setHeight(int height) {
			mHeight = height;
		}
		
		public int getHeight() {
			return mHeight;
		}
		
		public void setNext(int index, Node<T> next) {
			mNext.set(index, next);
		}
		
		public void addNext(Node<T> next) {
			mNext.add(next);
		}
		
		public Node<T> getNext(int index) {
			return mNext.get(index);
		}
		
		public ArrayList<Node<T>> getNexts() {
			return mNext;
		}
	}
	
	public SkipList(T headVal) {
		size = 0;
		height = INIT_HEIGHT;
		head = new Node<T>(height);
		head.setValue(headVal);
	}
	
	public T fisrtNode() {
		if (head.getNext(0) != null) {
			return head.getNext(0).getValue();
		} else {
			return null;
		}
	}
	
	public int size() {
		return size;
	}
	
	public int getMaxHeight() {
		return this.height;
	}
	
	public T find(T val) {
		if (this.size == 0) {
			return null;
		}
		Node<T> cur = head;
		int level = height - 1;
		
		while (level >= 0 && cur != null) {
			if (cur.getNext(level) == null || cur.getNext(level).getValue().compareTo(val) > 0) {
				// move to next level
				level--;
			} else {
				cur = cur.getNext(level);	// move forward.
				if (cur == null)
					break;
				while (cur.getNext(level) != null && cur.getNext(level).getValue().compareTo(val) < 0) {
					cur = cur.getNext(level);
					if (cur == null)
						break;
				}
			}
		}
		
		if (cur == null || cur.getValue() == null)
			return null;
		else 
			return (cur.getValue().compareTo(val) == 0) ? cur.getValue() : null;
	}
	
	public Node<T> insert(T val) {
		return delegate01(val);	// faster version
//		return delegate02(val);
	}
	
	public Node<T> delegate01(T val) {
		// Firstly find the inserting position.
		ArrayList<ArrayList<Node<T>>> prevs = new ArrayList<>();
		
		Node<T> cur = head;
		int level = height - 1;
		boolean flag = true;
		
		while (level >= 0 && cur != null) {
			int ret = 0;
			if (cur.getNext(level) == null || ((ret = cur.getNext(level).getValue().compareTo(val)) > 0)) {
				if (flag)
					prevs.add(cur.getNexts());
				flag  = true;
				level -= 1;
			} else if (ret < 0) {
				flag = false;
				cur = cur.getNext(level);
				if (cur == null)
					break;
				prevs.add(cur.getNexts());
				int len = prevs.size();
				while (cur.getNext(level) != null && cur.getNext(level).getValue().compareTo(val) < 0) {
					cur = cur.getNext(level);
					prevs.set(len-1, cur.getNexts());
				}
					
			} else {
				cur = cur.getNext(level);
				break;
			}
		}
		
		// Found it
		if (cur != null && cur.getValue() != null && cur.getValue().compareTo(val) == 0)
			return cur;
		
		// Insert a new node
		size += 1;
		int newHeight = RandInt.randInt(1, height+1);
		Node<T> node = new Node<T>(newHeight);
		node.setValue(val);
		
		level = newHeight < height ? newHeight - 1 : height - 1;
		while (level >= 0) {
			node.setNext(level, prevs.get(height-level-1).get(level));
			prevs.get(height-level-1).set(level, node);
			level -= 1;
		}
		for (int i = height; i < newHeight; i++) {
			head.addNext(node);
			node.setNext(i, null);
		}
		
		if (newHeight > height) {
			height = newHeight;
			head.setHeight(height);
		}
		
		return node;
	}
	
	public Node<T> delegate02(T val) {
		// Firstly find the inserting position.
		ArrayList<ArrayList<Node<T>>> prevs = new ArrayList<>();
		Node<T> cur = findGreaterOrEqual(val, prevs);
		
		// Found it
		if (cur != null && cur.getValue() != null && cur.getValue().compareTo(val) == 0)
			return cur;
		
		// Insert a new node
		size += 1;
		int newHeight = RandInt.randInt(1, height+1);
		Node<T> node = new Node<T>(newHeight);
		node.setValue(val);
		
		int level = newHeight < height ? newHeight - 1 : height - 1;
		while (level >= 0) {
			node.setNext(level, prevs.get(height-level-1).get(level));
			prevs.get(height-level-1).set(level, node);
			level -= 1;
		}
		for (int i = height; i < newHeight; i++) {
			head.addNext(node);
			node.setNext(i, null);
		}
		
		if (newHeight > height) {
			height = newHeight;
			head.setHeight(height);
		}
		
		return node;
	}
	
	public void delete(T val) {
		// No element
		if (size == 0)
			return;
		
		Node<T> cur = head;
		int level = height - 1;
		boolean flag = false;
		
		while (level >= 0 && cur != null) {
			while (cur.getNext(level) != null && cur.getNext(level).getValue().compareTo(val) < 0)
				cur = cur.getNext(level);
			
			if (cur.getNext(level) != null && cur.getNext(level).getValue().compareTo(val) == 0) {
				cur.setNext(level, cur.getNext(level).getNext(level));
				flag = true;
			}
			
			level -= 1;
		}
		
		size -= (flag) ? 1 : 0;
	}
	
	public Node<T> findGreaterOrEqual(final T key, ArrayList<ArrayList<Node<T>>> prev) {
		int level = height - 1;
		Node<T> cur = head;
		while (true) {
			Node<T> next = cur.getNext(level);
			if (next != null && key.compareTo(next.getValue()) > 0) {
				cur = next;
			} else {
				if (prev != null) {
					if ((level + prev.size()) < height) {
						prev.add(cur.getNexts());
					} else {
						prev.set(level, cur.getNexts());
					}
				}
				if (level == 0) {
					return next;
				} else {
					// Switch to next list
					level--;
				}
			}
		}
	}
	
	public Node<T> fineLessThan(final T key) {
		Node<T> cur = this.head;
		int level = height - 1;
		while (true) {
			Node<T> next = cur.getNext(level);
			if (next == null || key.compareTo(next.getValue()) < 0) {
				if (level == 0) {
					return cur;
				} else {
					// Switch to next list
					level--;
				}
			} else {
				cur = next;
			}
		}
	}
	
	public Node<T> findLast() {
		Node<T> cur = head;
		int level = height - 1;
		while (true) {
			Node<T> next = cur.getNext(level);
			if (next == null) {
				if (level == 0) {
					return cur;
				} else {
					// Switch to next level
					level--;
				}
			} else {
				cur = next;
			}
		}
	}
	
	public boolean contains(T val) {
		Node<T> cur = findGreaterOrEqual(val, null);
		if (cur != null && val.compareTo(cur.getValue()) == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() {
		String ret = "Size: " + size + '\n';
		for (int i = height-1; i >= 0; i--) {
			ret += "level " + i + ": ";
			ret += printLevel(i);
			ret += "\n";
		}
		return ret;
	}
	
	private String printLevel(int i) {
		Node<T> cur = head.getNext(i);
		String ret = "";
		while (cur != null) {
			ret += cur.getValue() + "->";
			cur = cur.getNext(i);
		}
		return ret;
	}
	
	public ArrayList<T> nodes() {
		ArrayList<T> ret = new ArrayList<>();
		Node<T> cur = head;
		while (cur.getNext(0) != null) {
			ret.add(cur.getNext(0).getValue());
			cur = cur.getNext(0);
		}
		return ret;
	}

	@Override
	public Iterator<T> iterator() {
		// TODO Auto-generated method stub
		Iterator<T> iterator = new Iterator<T>() {
			private int level = 0;
			private Node<T> cur = head/*.getNext(level)*/;
			@Override
			public boolean hasNext() {
				// TODO Auto-generated method stub
				if (cur.getNext(level) != null) {
					return true;
				} else {
					return false;
				}
			}

			@Override
			public T next() {
				// TODO Auto-generated method stub
				cur = cur.getNext(level);
				return cur.getValue();
			}
		};
		return iterator;
	}
}