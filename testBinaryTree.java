import static org.junit.Assert.*;
import org.junit.*;


public class testBinaryTree {
	private BinaryTree empty;
	private BinaryTree oneNode;
	private BinaryTree bt;
	private BinaryTree duplicates;
	
	@Before
	public void setUp() {
		empty = new BinaryTree(null);

		oneNode = new BinaryTree("1");

		bt = new BinaryTree("1");
		bt.setLeft(new BinaryTree("aa"));
		bt.setRight(new BinaryTree("abcdefghijklmnopqrstuvwxyz"));
		bt.getLeft().setLeft(new BinaryTree("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
		bt.getLeft().setRight(new BinaryTree("+=_~!@$%^&*()_+\"<>?:,./;'|[]{}"));
		bt.getRight().setLeft(new BinaryTree("Hello!"));
		
		duplicates = new BinaryTree("1");
		duplicates.setLeft(new BinaryTree("2"));
		duplicates.setRight(new BinaryTree("2"));
		duplicates.getLeft().setLeft(new BinaryTree("4"));
		duplicates.getLeft().setRight(new BinaryTree("5"));
		duplicates.getRight().setRight(new BinaryTree("1"));
	}

	@Test
	public void testSerialization() {
		assertNull( empty.serialize() );
		assertTrue( oneNode.equals(oneNode.deserialize(oneNode.serialize())) );
		assertTrue( bt.equals(bt.deserialize(bt.serialize())) );
		assertTrue( duplicates.equals(
						duplicates.deserialize(duplicates.serialize())) );
	}
	
	@Test
	public void testEquals() {
		BinaryTree diffOneNode = new BinaryTree("b");

		BinaryTree same = new BinaryTree("1");
		same.setLeft(new BinaryTree("2"));
		same.setRight(new BinaryTree("2"));
		same.getLeft().setLeft(new BinaryTree("4"));
		same.getLeft().setRight(new BinaryTree("5"));
		same.getRight().setRight(new BinaryTree("1"));

		assertTrue( oneNode.equals(oneNode) );
		assertTrue( bt.equals(bt) );
		assertTrue( duplicates.equals(same) );
		assertTrue( same.equals(duplicates) );
		
		assertFalse( oneNode.equals(diffOneNode) );
		assertFalse( oneNode.equals(bt) );
		assertFalse( bt.equals(duplicates) );

		same.getRight().setRight(new BinaryTree("a"));
		assertFalse( bt.equals(same) );
	}

}
