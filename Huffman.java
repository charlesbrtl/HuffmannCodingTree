/*Charles Letonnellier de Breteuil
 * implementation of :
 * Huffman Tree
 * Huffman Node
 * MinHeap
 * Pathstack 
 * 
 * */
import java.io.File;
import java.util.Scanner;
public class Huffman extends BinaryTree {
	static String[] codearray = new String[256];
	static int stringsize;

	public Huffman(HuffNode node) {
		this.root = node;
	}

	public Huffman(String filename) {
		root = BuildTree(filename);
	}

	public String encode(String toencode) {
		PathStack mystack = new PathStack(15);
		traversal((HuffNode) root, mystack);
		StringBuffer writencoded = new StringBuffer();
		Scanner scan = new Scanner(toencode);
		scan.useDelimiter("");
		try {
			while (scan.hasNext()) {
				String current=scan.next();
				writencoded.append(codearray[current.charAt(0)]);
				stringsize += codearray[current.charAt(0)].length();
			}
			scan.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return writencoded.toString();
	}

	private HuffNode BuildTree(String filename) {
		HuffNode[] characters = new HuffNode[256];
		int[][] indexarray = new int[256][2];
		File file = new File(new File(filename).getAbsolutePath());
		int order=0;

		try {
			Scanner scan = new Scanner(file);
			scan.useDelimiter("");
			while (scan.hasNext()) {
				char c = scan.next().charAt(0);
				if(indexarray[(int) c][0]==0){
					indexarray[(int) c][1]=order;
					order++;
				}
				indexarray[(int) c][0]++;
			}
			scan.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		MinHeap minheap = new MinHeap(256);
		for (int i = 0; i < indexarray.length; i++) {
			if (indexarray[i][0] != 0) {
				HuffNode pointer = new HuffNode((char) i, indexarray[i][0],indexarray[i][1]);
				characters[i] = pointer;
				minheap.add(pointer);
			}

		}

		HuffNode TreeMaker = null;

		while (true) {
			if (minheap.length() == 1)
				break;

			TreeMaker = merge(minheap.remove(), minheap.remove());
			minheap.add(TreeMaker);
		}

		return TreeMaker;
	}

	private HuffNode merge(HuffNode HuffNode1, HuffNode HuffNode2) {
		HuffNode internal = new HuffNode(HuffNode1.data + HuffNode2.data);
		if (HuffNode1.compareTo(HuffNode2)<0) {
			internal.left = HuffNode1;
			internal.right = HuffNode2;
		} else if (HuffNode1.compareTo(HuffNode2)>0){
			internal.left = HuffNode2;
			internal.right = HuffNode1;
		}
		else {
			//if two characters have equal weight they're compared with appearance order
			//two subtrees with the same height will have appearance order 0;
			// The first to be removed of the queue will be left child
			//because we call this method with arguments MinHeap.remove(),MinHeap.remove()
			if(HuffNode1.appearanceorder>HuffNode2.appearanceorder){
				internal.left = HuffNode2;
				internal.right = HuffNode1;	
			}
			else{

				internal.left = HuffNode1;
				internal.right = HuffNode2;
			}
		}
	
		return internal;
	}

	//this method looks rather odd, but it took me a lot of time to figure how to make recursion work well
// with the stack operations, and this is the way to go
	private void traversal(HuffNode node, PathStack bytestack) {
		if (node.left != null) {
			bytestack.push(0);
			traversal((HuffNode) node.left, bytestack);
		}
		if (node.right == null) {
			codearray[(int) node.character] = bytestack.toString();
			//to print each character code 
			//System.out.println((node.character+" freq: "+node.data+" codes "+bytestack.toString()));
			bytestack.pop();
		}
		if (node.right != null) {
			bytestack.push(1);
			traversal((HuffNode) node.right, bytestack);

		}
		if (node.left != null) {
			bytestack.pop();
		}
	}

	private class HuffNode extends BinaryTree.Node {
		char character;
		int appearanceorder;

		HuffNode(char character, int data,int appearanceorder) {
			super();
			this.character = character;
			this.data = data;
		}

		HuffNode(int data) {
			super(data);
		}

		public int compareTo(HuffNode o) {

			return this.data - o.data;
		}

	}

	private class MinHeap {
		private HuffNode[] heap;
		private int length;

		public MinHeap(int maxsize) {
			heap = new HuffNode[maxsize + 1];
			length = 0;

		}

		public void add(HuffNode value) {


			length++;
			heap[length] = value;
			Lift();
		}

		public HuffNode remove() {
			HuffNode result = peek();

			swap(1, length);
			heap[length] = null;
			length--;

			Sink();

			return result;
		}

		public boolean isEmpty() {
			return length == 0;
		}

		public HuffNode peek() {
			if (isEmpty())
				throw new IllegalStateException();
			return heap[1];
		}

		public int length() {
			return length;
		}

		private void Lift() {
			int index = length;
			while (hasParent(index) && (parent(index).compareTo(heap[index]) > 0)) {
				swap(index, parentIndex(index));
				index = parentIndex(index);
			}
		}

		private void Sink() {
			int index = 1;
			while (hasLeftChild(index)) {
				int smaller = leftIndex(index);
				if (hasRightChild(index) && heap[leftIndex(index)].compareTo(heap[rightIndex(index)]) > 0) {
					smaller = rightIndex(index);
				}
				if (heap[index].compareTo(heap[smaller]) > 0) {
					swap(index, smaller);
				} else
					break;

				index = smaller;
			}

		}

		private boolean hasParent(int i) {
			return i > 1;
		}

		private int leftIndex(int i) {
			return i * 2;
		}

		private int rightIndex(int i) {
			return i * 2 + 1;
		}

		private boolean hasLeftChild(int i) {
			return leftIndex(i) <= length;
		}

		private boolean hasRightChild(int i) {
			return rightIndex(i) <= length;
		}

		private int parentIndex(int i) {
			return i / 2;
		}

		private HuffNode parent(int i) {
			return heap[parentIndex(i)];
		}

		private void swap(int index1, int index2) {
			HuffNode temp = heap[index1];
			heap[index1] = heap[index2];
			heap[index2] = temp;
		}

	}

	private class PathStack {
		private int size;
		private int[] PathArray;
		private int top;

		public PathStack(int size) {
			this.size = size;
			PathArray = new int[size];
			top = -1;
		}

		public void push(int topush) {
			PathArray[++top] = topush;
		}

		public int pop() {
			try {
				return PathArray[top--];
			} catch (ArrayIndexOutOfBoundsException e) {
			}
			return 0;
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i <= top; i++) {
				sb.append(PathArray[i]);
			}
			return sb.toString();
		}
	}

	public static void main(String[] args) {
	
String tocode;
do{
		Scanner key=new Scanner(System.in);
		Huffman tree = new Huffman(args[0].substring(1, args[0].length()-1));
		System.out.println("Enter the phrase you wish to encode (enter 0 to quit):");
		tocode=key.nextLine();
		if (tocode.equals("0"))
		break;
		System.out.println("The String : \"" + tocode + "\" \ncodes:");
		System.out.println(tree.encode(tocode));
		System.out.println(
				"contains " + stringsize + " bits, compared to the original " + tocode.getBytes().length * 8+" for fixed ASCII");
		stringsize=0;}
while(true);
	}
}
