import java.util.*;

/**
 * BinaryTree.java
 * 
 * A simple binary tree class to demonstrate serialization and deserialization
 * of a binary tree
 * 
 * @author Harrison DiStefano
 */

public class BinaryTree {
	// Marker constants used for serialization
	private static final String NULLMARKER = "-"; // marks a null child in the tree
	private static final String SEPERATOR = "#";  // seperates the data between nodes

	// Variables
	private String data;		// Data stored in the node
	private BinaryTree left;	// Left child of the node
	private BinaryTree right;	// Right child of the node
	private String serialized;  // Serialized representation of the binary tree
	
	// Constructors
	public BinaryTree(String data, BinaryTree left, BinaryTree right) {
		this.data 		= data;
		this.left 		= left;
		this.right 		= right;
		this.serialized = null;
	}
	
	public BinaryTree(String data) {
		this(data, null, null);
	}
	
	// Getters and setters
	public String getSerialized() 		{ return this.serialized; }
	public String getData()		  		{ return this.data;		}
	public BinaryTree getLeft()	  		{ return this.left;		}
	public BinaryTree getRight()  		{ return this.right;		}
	
	public void setSerialized(String s) { this.serialized = s; }
	public void setData(String data)	{ this.data = data; }
	public void setLeft(BinaryTree t)	{ this.left = t; }
	public void setRight(BinaryTree t)  { this.right = t; }
	
	
	/**
	 * Serializes this {@code BinaryTree} instance
	 * NOTE: Will not work properly if a node contains a String data value 
	 * containing the NULLMARKER or SEPERATOR constants.
	 * 
	 * 
	 * @serialData The data stored in each node of the binary tree is stored
	 * 			   in pre-order as a String with null nodes marked such that
	 * 			   the in-order traversal can be derived
	 */
	public String serialize() {
		if (this.data == null) { // Empty tree
			return null;
		}
		if (this.serialized != null) {
			return this.serialized;
		}
		StringBuilder s = new StringBuilder();
		this.serialized = serializeTree(this, s);
		return this.serialized;
	}

	/**
	 * Helper function for serialize that returns the string containing the
	 * node data in pre-order with null nodes marked
	 * 
	 * @param n the root node at which to serialize
	 * @param s the StringBuilder to use
	 * @return  the string containing the node data starting at n and
	 * 			all nodes below n in pre-order
	 */
	private String serializeTree(BinaryTree n, StringBuilder s) {
		if (n == null) { // n is a null leaf node
			s.append(NULLMARKER);
			s.append(SEPERATOR);
			return null;
		}
		s.append(n.data);
		s.append(SEPERATOR);
		serializeTree(n.left, s);
		serializeTree(n.right, s);
		return s.toString();
	}
	
	/**
	 * Deserializes the binary tree back into its original state if it has 
	 * already been serialized, restoring structure and all original node
	 * data 
	 * 
	 * @return the root Node of the deserialized binary tree
	 */
	public BinaryTree deserialize(String serialized) {
		if (serialized == null) {
			return null;
		}
		// Parse serialization (could be put into helper method)
		String[] parsedSerial = serialized.split(SEPERATOR);
		ArrayList<String> preOrder = 
				new ArrayList<String>(Arrays.asList(parsedSerial));
		
		// Derive in-order from parsed list
		ArrayList<String> inOrder = getInOrderFromPreOrder(preOrder);
		
		// Rebuild tree
		return buildFromInAndPre(inOrder, preOrder);

	}
	
	/**
	 * Builds a complete binary tree given the in-order and pre-order
	 * traversals of that binary tree
	 * 
	 * @param inOrder  the in-order traversal of a binary tree
	 * @param preOrder the pre-order traversal of the same binary tree
	 * @return		   the root Node of the deserialized binary tree
	 */
	private BinaryTree buildFromInAndPre(ArrayList<String> inOrder, 
									   ArrayList<String> preOrder) {
		// Keeps track of which values are visited in inOrder in case
		// of duplicate values
		ArrayList<Boolean> visitedInOrder = new ArrayList<Boolean>();
		for (int i=0; i < inOrder.size(); i++) {
			visitedInOrder.add(false); // mark each index as unvisited
		}
		// Uses a queue to simplify indexing of preOrder
		Deque<String> preOrderQueue = new LinkedList<String>();
		for (String data : preOrder) {
			preOrderQueue.add(data);
		}
		return buildTree(inOrder, visitedInOrder, preOrderQueue, 0, 
						 preOrder.size() - 1);
	}
	
	/**
	 * Recursively builds a BinaryTree subtree starting at the index start and
	 * ending at the index end of the given in-order representation. Both the
	 * in-order and the pre-order collection should be read from left to right.
	 * 
	 * @param inOrder		 the in-order ArrayList
	 * @param visitedInOrder an array keeping track of visited inOrder indexes
	 * @param preOrder		 the pre-order Queue
	 * @param start	   		 the in-order index to start at 
	 * @param end	   		 the in-order index to end at
	 * @return		   		 the root node of the subtree using the nodes from
	 * 						 start to end of the in-order representation
	 */
	private BinaryTree buildTree(ArrayList<String> inOrder, 
								 ArrayList<Boolean> visitedInOrder,
								 Deque<String> preOrder,
							     int start, int end) {
		if (start > end) { // Null node
			return null;
		}
		// Create a new BinaryTree node with the next pre-order value
		BinaryTree newNode = new BinaryTree(preOrder.remove());

		// Find the first unvisited index of the data and update visited
		int inOrderIndex = findIndex(inOrder, visitedInOrder, newNode.data);
		
		if (start == end) { // External node
			return newNode;
		}
		
		// Build left subtree
		newNode.left  = buildTree(inOrder, visitedInOrder, 
								  preOrder, start, inOrderIndex - 1);
		// Build right subtree
		newNode.right = buildTree(inOrder, visitedInOrder, 
								  preOrder, inOrderIndex + 1, end);

		return newNode;
	}
	
	/**
	 * Finds and returns the first unvisited index of data in arr as marked
	 * in visited
	 * 
	 * @param arr	  the array to find the index of data in
	 * @param visited the array storing which indexes have been visited
	 * @param data	  the data being searched for in arr
	 * @return		  the first index of data in which the corresponding index
	 * 				  of visited is unvisited (false) from left to right
	 */
	private int findIndex(ArrayList<String> arr, ArrayList<Boolean> visited,
						  String data) {
		// Note this returns the index of the first occurrence of the data
		int inOrderIndex = arr.indexOf(data);
		// Search for first unvisited index of the data and update index
		while (visited.get(inOrderIndex)) {  // While visited
			for (int i=inOrderIndex + 1; i < arr.size(); i++) {
				if (arr.get(i).equals(data)) {
					inOrderIndex = i;
				}
			}
		}
		// Update visited
		visited.set(inOrderIndex, true);
		return inOrderIndex;
	}
	
	/**
	 * Takes a pre-order representation of a binary tree and derives and
	 * returns the in-order traversal of that binary tree. Also removes
	 * the null markers from the given pre-order for consistency between
	 * the two representations.
	 * 
	 * @param preOrder  the pre-order traversal of the tree with null markers
	 * @return			the in-order traversal of the tree	
	 */
	private ArrayList<String> getInOrderFromPreOrder(ArrayList<String> preOrder) {
		Deque<String> stack = new LinkedList<String>();
		ArrayList<String> inOrder = new ArrayList<String>();
		String s;
		for (Iterator<String> iter = preOrder.iterator(); iter.hasNext(); ) {
			s = iter.next();
			if (s.equals(NULLMARKER)) {
				iter.remove(); // null marker is no longer needed
				if (!stack.isEmpty()) {
					inOrder.add(stack.pop());
				}
			} else {
				stack.push(s);
			}
		}
		return inOrder;
	}
	
	// FOR TESTING
	/**
	 * Prints the in-order traversal of the tree
	 * @param n the root node to begin the traversal at
	 */
	private void printInOrder(BinaryTree n) {
		if (n == null) {
			return;
		}
		
		printInOrder(n.left);
		System.out.printf("%s ", n.data);
		printInOrder(n.right);
	}
	
	/**
	 * Prints the pre-order traversal of the tree
	 * @param n the root node to begin the traversal at
	 */
	private void printPreOrder(BinaryTree n) {
		if (n == null) {
			return;
		}
		
		System.out.printf("%s ", n.data);
		printPreOrder(n.left);
		printPreOrder(n.right);
	}
	
	/**
	 * @see java.lang.Object#equals(Object)
	 * @return true if o is a binary tree and has equivalent data and structure
	 * 		   as this binary tree i.e. each node has the same values for
	 * 		   data, its left child, and its right child
	 */
	@Override public boolean equals(Object o) {
		if (!(o instanceof BinaryTree)) {
			return false;
		}
		BinaryTree otherBinaryTree = (BinaryTree) o;
		
		return equalTo(this.data, otherBinaryTree.data)
			&& equalTo(this.left, otherBinaryTree.left)
			&& equalTo(this.right, otherBinaryTree.right);
	}
	
	public boolean equalTo(Object x, Object y) {
		if (x == null) {
			return y == null; // to avoid null pointer exception
		}
		return x.equals(y);
	}
}