/*Charles Letonnellier de Breteuil
 *Binary tree implementation
 * */
public abstract class BinaryTree {
	Node root;

	BinaryTree() {
		this.root = null;
	}

	protected class Node {
		int data;
		Node right;
		Node left;

		public Node() {
		}

		Node(int data) {
			this.data = data;
		}
	}
}
