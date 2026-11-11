/*
 * Copyright 2016 Scalified <http://www.scalified.com>
 * Modifications Copyright 2026 Nvaria GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.nvaria.tree;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author shell
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class TreeNodeTest {

	/*
	 * Test tree structure
	 *
	 *   +- root(ROOT_DATA)
	 *      +- node_1(NODE_DATA_1)
	 *      +- node_2(NODE_DATA_2)
	 *      |  +- node_3(NODE_DATA_3)
	 *      |  |  +- node_4(NODE_DATA_4)
	 *      |  |  +- node_5(NODE_DATA_1)
	 *      |  |  |  +- node6 (NODE_DATA4)
	 *      |  +- node_7(null)
	 *      |  +- node_8(NODE_DATA_1)
	 *      +- node9(NODE_DATA_4)
	 *      |  +- node10(null)
	 *
	 */

	protected static final String ROOT_DATA = "ROOT_DATA";
	protected static final String NODE_DATA_1 = "DATA_1";
	protected static final String NODE_DATA_2 = "DATA_2";
	protected static final String NODE_DATA_3 = "DATA_3";
	protected static final String NODE_DATA_4 = "DATA_4";

	protected static final String ANOTHER_NODE_DATA = "ANOTHER_NODE_DATA";

	protected TreeNode<String> root;
	protected TreeNode<String> node1;
	protected TreeNode<String> node2;
	protected TreeNode<String> node3;
	protected TreeNode<String> node4;
	protected TreeNode<String> node5;
	protected TreeNode<String> node6;
	protected TreeNode<String> node7;
	protected TreeNode<String> node8;
	protected TreeNode<String> node9;
	protected TreeNode<String> node10;

	protected final TreeNode<String> anotherNode = createTreeNode(ANOTHER_NODE_DATA);

	protected abstract <T> TreeNode<T> createTreeNode(T data);

	@BeforeEach
	public void buildTree() {
		// Create tree nodes
		root = createTreeNode(ROOT_DATA);
		node1 = createTreeNode(NODE_DATA_1);
		node2 = createTreeNode(NODE_DATA_2);
		node3 = createTreeNode(NODE_DATA_3);
		node4 = createTreeNode(NODE_DATA_4);
		node5 = createTreeNode(NODE_DATA_1);
		node6 = createTreeNode(NODE_DATA_4);
		node7 = createTreeNode(null);
		node8 = createTreeNode(NODE_DATA_1);
		node9 = createTreeNode(NODE_DATA_4);
		node10 = createTreeNode(null);

		// Populate tree
		root.add(node1);
		root.add(node2);
		node2.add(node3);
		node3.add(node4);
		node3.add(node5);
		node5.add(node6);
		node2.add(node7);
		node2.add(node8);
		root.add(node9);
		node9.add(node10);
	}

	@Test
	public void testData() {
		// Test data is correctly populated
		String message = "Data object stored in tree node and data object queried from tree node are different";
		assertEquals(ROOT_DATA, root.data(), message);
		assertEquals(NODE_DATA_1, node1.data(), message);
		assertEquals(NODE_DATA_4, node4.data(), message);
		assertNull(node7.data(), message);
	}

	@Test
	public void testSetData() {
		// Test data is correctly set
		String message = "Data object stored in tree node was expected to be equal to the data object queried from " +
				"tree node, but actually was not";
		assertEquals(ROOT_DATA, root.data());
		root.setData(ANOTHER_NODE_DATA);
		assertEquals(ANOTHER_NODE_DATA, root.data(), message);
	}

	@Test
	public void testRoot() {
		// Test root tree node is correctly determined
		String message = "Root node was incorrectly determined";
		assertEquals(root, root, message);
		assertEquals(root, node1.root(), message);
		assertEquals(root, node4.root(), message);
		assertEquals(root, node7.root(), message);
		assertEquals(root, node10.root(), message);
	}

	@Test
	public void testIsRoot() {
		// Test if root tree node is root tree node
		String messageRootExpected = "The tree node was expected to be root, but actually was not";
		assertTrue(root.isRoot(), messageRootExpected);

		// Test if non root tree node is non root tree node
		String messageRootNotExpected = "The tree node was not expected to be root, but actually was";
		assertFalse(node2.isRoot(), messageRootNotExpected);
		assertFalse(node6.isRoot(), messageRootNotExpected);
	}

	@Test
	public void testParent() {
		// Test if parent tree node is correctly determined
		String message = "Parent node was incorrectly determined";

		assertNull(root.parent(), message);
		assertEquals(root, node1.parent(), message);
		assertEquals(node3, node4.parent(), message);
		assertEquals(node5, node6.parent(), message);
		assertEquals(node9, node10.parent(), message);

		// Test if non parent tree node is non parent tree node
		assertNotEquals(node2, node5.parent(), message);
		assertNotEquals(root, node3.parent(), message);
		assertNotEquals(node7, root.parent(), message);
	}

	@Test
	public void testSubtrees() {
		// Test if subtrees are correctly determined
		String message = "Subtrees collection was incorrectly determined";

		Collection<TreeNode<String>> mSubtreesLevel1 = new ArrayList<>(3);
		mSubtreesLevel1.add(node1);
		mSubtreesLevel1.add(node2);
		mSubtreesLevel1.add(node9);
		assertEquals(mSubtreesLevel1, root.subtrees(), message);

		Collection<TreeNode<String>> mSubtreesLevel2 = new ArrayList<>(3);
		mSubtreesLevel2.add(node3);
		mSubtreesLevel2.add(node7);
		mSubtreesLevel2.add(node8);
		assertEquals(mSubtreesLevel2, node2.subtrees(), message);

		Collection<TreeNode<String>> mSubtreesLevel3 = List.of(node6);
		assertEquals(mSubtreesLevel3, node5.subtrees(), message);

		assertEquals(List.of(), node6.subtrees(), message);
	}

	@Test
	public void testIsLeaf() {
		// Test if tree node with no subtrees is a leaf
		String messageLeafExpected = "The tree node expected to be a leaf, but actually was not";
		assertTrue(node1.isLeaf(), messageLeafExpected);

		// Test if tree node with subtrees is not a leaf
		String messageNotLeafExpected = "The tree node was not expected to be a leaf, but actually was";
		assertFalse(root.isLeaf(), messageNotLeafExpected);
	}

	@Test
	public void testFind() {
		// Test if the tree node has the searched data
		String messageTreeNodeFoundExpected = "The tree expected to have the searched data, but actually was not";
		assertEquals(node6, node6.find(NODE_DATA_4), messageTreeNodeFoundExpected);
		assertEquals(node7, node2.find(null), messageTreeNodeFoundExpected);
		assertEquals(node7, node7.find(null), messageTreeNodeFoundExpected);
		assertEquals(node5, node3.find(NODE_DATA_1), messageTreeNodeFoundExpected);
		assertEquals(node4, root.find(NODE_DATA_4), messageTreeNodeFoundExpected);

		// Test if the tree node does not have the searched data
		String messageTreeNodeNotFoundExpected =
				"The tree with the searched data was not expected to be found, but actually was";
		assertNull(node6.find("data"), messageTreeNodeNotFoundExpected);
		assertNull(root.find("data"), messageTreeNodeNotFoundExpected);
	}

	@Test
	public void testFindAll() {
		// Test if the tree has a single tree node with the searched data
		String messageSingletonCollectionFoundExpected =
				"The single tree node was expected to have the searched data, but actually was not";
		assertEquals(List.of(node6), node6.findAll(NODE_DATA_4), messageSingletonCollectionFoundExpected);
		assertEquals(List.of(node7), node7.findAll(null), messageSingletonCollectionFoundExpected);
		assertEquals(List.of(node2), root.findAll(NODE_DATA_2), messageSingletonCollectionFoundExpected);

		// Test if the tree has more than one node with the searched data
		String messageCollectionFoundExpected =
				"The tree nodes was expected to have the searched data, but actually was not";
		Collection<TreeNode<String>> expectedMany = new ArrayList<>();
		expectedMany.add(node4);
		expectedMany.add(node6);
		expectedMany.add(node9);
		assertEquals(expectedMany, root.findAll(NODE_DATA_4), messageCollectionFoundExpected);


		// Test if the tree does not have any tree node with the searched data
		String messageEmptyCollectionFoundExpected =
				"Three tree nodes with the searched data was not expected to be found, but actually was";
		assertEquals(List.of(), node10.findAll("data"), messageEmptyCollectionFoundExpected);
	}

	@Test
	public void testHasSubtree() {
		// Test if tree node has the specified subtree
		String messageSubtreeExpected =
				"The tree node was expected to have the specified subtree, but actually was not";
		assertTrue(root.hasSubtree(node1), messageSubtreeExpected);
		assertTrue(root.hasSubtree(node9), messageSubtreeExpected);
		assertTrue(node2.hasSubtree(node7), messageSubtreeExpected);
		assertTrue(node5.hasSubtree(node6), messageSubtreeExpected);

		// Test if tree node does not have the specified subtree
		String messageSubtreeNotExpected =
				"The tree node was not expected to have the specified subtree, but actually was";
		assertFalse(node1.hasSubtree(node10), messageSubtreeNotExpected);
		assertFalse(node5.hasSubtree(null), messageSubtreeNotExpected);
		assertFalse(node2.hasSubtree(root), messageSubtreeNotExpected);
		assertFalse(root.hasSubtree(node7), messageSubtreeNotExpected);
	}

	@Test
	public void testDropSubtree() {
		// Test if subtree is dropped
		String messageDropExpected = "The subtree was expected to be dropped, but actually was not";
		String messageDropResultTrueExpected =
				"The subtree drop result was expected to be true, but actually was false";
		assertTrue(node3.dropSubtree(node5), messageDropResultTrueExpected);
		assertFalse(node3.contains(node5), messageDropExpected);
		assertFalse(node3.contains(node6), messageDropExpected);
		assertTrue(root.dropSubtree(node9), messageDropResultTrueExpected);
		assertFalse(root.contains(node9), messageDropExpected);
		assertFalse(root.contains(node10), messageDropExpected);

		// Test if subtree is not dropped
		String messageDropNotExpected = "The subtree was not expected to be dropped, but actually was";
		String messageDropResultFalseExpected =
				"The subtree drop result was expected to be false, but actually was true";
		assertFalse(node1.dropSubtree(node2), messageDropResultFalseExpected);
		assertTrue(root.contains(node1), messageDropNotExpected);
		assertTrue(root.contains(node2), messageDropNotExpected);
		assertFalse(node2.dropSubtree(null), messageDropResultFalseExpected);
		assertTrue(root.contains(node2), messageDropNotExpected);
		assertFalse(node1.dropSubtree(root), messageDropResultFalseExpected);
		assertTrue(root.contains(node1), messageDropNotExpected);

		// Test if there are equal subtrees only first is dropped
		TreeNode<String> mAnotherNode1 = createTreeNode(ANOTHER_NODE_DATA);
		TreeNode<String> mAnotherNode2 = createTreeNode(ANOTHER_NODE_DATA);
		node1.add(mAnotherNode1);
		node1.add(mAnotherNode2);
		assertTrue(node1.remove(mAnotherNode1), messageDropExpected);
		assertFalse(node1.contains(mAnotherNode1), messageDropNotExpected);
		assertTrue(node1.contains(mAnotherNode2), messageDropNotExpected);
	}

	@Test
	public void testContains() {
		// Test if tree node contains the specified tree node
		String messageContainExpected =
				"The tree node was expected to contain the specified node, but actually was not";

		assertTrue(root.contains(node2), messageContainExpected);
		assertTrue(root.contains(node6), messageContainExpected);
		assertTrue(node3.contains(node4), messageContainExpected);
		assertTrue(node2.contains(node5), messageContainExpected);

		// Test if tree node does not contain the specified tree node
		String messageNotContainExpected =
				"The tree node was not expected to contain the specified tree node, but actually was";

		assertFalse(root.contains(root), messageNotContainExpected);
		assertFalse(node4.contains(node3), messageNotContainExpected);
		assertFalse(node2.contains(null), messageNotContainExpected);
		assertFalse(node3.contains(root), messageNotContainExpected);
		assertFalse(root.contains(anotherNode), messageNotContainExpected);
	}

	@Test
	public void testContainsAll() {
		// Test if tree node contains all of the specified tree nodes
		String messageContainExpected =
				"The tree was expected to contain all of the specified nodes, but actually was not";

		Collection<TreeNode<String>> mTreeNodesContain1 = new ArrayList<>(2);
		mTreeNodesContain1.add(node3);
		mTreeNodesContain1.add(node6);
		assertTrue(node2.containsAll(mTreeNodesContain1), messageContainExpected);

		Collection<TreeNode<String>> mTreeNodesContain2 = new ArrayList<>(6);
		mTreeNodesContain2.add(node8);
		mTreeNodesContain2.add(node7);
		mTreeNodesContain2.add(node1);
		mTreeNodesContain2.add(node9);
		mTreeNodesContain2.add(node10);
		mTreeNodesContain2.add(node6);
		assertTrue(root.containsAll(mTreeNodesContain2), messageContainExpected);

		List<TreeNode<String>> mTreeNodesContain3 = Collections.singletonList(node3);
		assertTrue(node2.containsAll(mTreeNodesContain3), messageContainExpected);

		// Test if tree node does not contain at least one of the specified tree nodes
		String messageContainNotExpected =
				"The tree was not expected to contain all of the specified nodes, but actually was";

		Collection<TreeNode<String>> mTreeNodesNotContain1 = new ArrayList<>(3);
		mTreeNodesNotContain1.add(node5);
		mTreeNodesNotContain1.add(node10);
		mTreeNodesNotContain1.add(anotherNode);
		assertFalse(node1.containsAll(mTreeNodesNotContain1), messageContainNotExpected);
		assertFalse(root.containsAll(mTreeNodesNotContain1), messageContainNotExpected);

		Collection<TreeNode<String>> mTreeNodesNotContain2 = new ArrayList<>(2);
		mTreeNodesNotContain2.add(root);
		mTreeNodesNotContain2.add(node2);
		assertFalse(root.containsAll(mTreeNodesNotContain2), messageContainNotExpected);

		Collection<TreeNode<String>> mTreeNodesNotContain3 = new ArrayList<>(2);
		mTreeNodesNotContain3.add(node6);
		mTreeNodesNotContain3.add(anotherNode);
		assertFalse(node3.containsAll(mTreeNodesNotContain3), messageContainNotExpected);

		assertFalse(root.containsAll(null), messageContainNotExpected);
		assertFalse(node3.containsAll(Collections.<TreeNode<String>>emptyList()), messageContainNotExpected);
	}

	@Test
	public void testAdd() {
		// Test tree node is successfully added
		String messageAddExpected = "The tree node was expected to be added, but actually was not";
		String messageAddResultTrueExpected = "The addition result was expected to be true, but actually was false";
		assertTrue(node1.add(anotherNode), messageAddResultTrueExpected);
		assertTrue(node1.contains(anotherNode), messageAddExpected);

		// Test tree node is not added
		String messageAddResultFalseExpected = "The addition result was expected to be false, but actually was true";
		assertFalse(node2.add(null), messageAddResultFalseExpected);
	}

	@Test
	public void testClear() {
		// Test tree node is successfully cleared
		String messageClearedExpected = "The tree node was expected to be cleared, but actually was not";

		node6.clear();
		assertTrue(node6.isLeaf(), messageClearedExpected);

		assertFalse(node3.isLeaf());
		node3.clear();
		assertTrue(node3.isLeaf());
		assertNull(node4.parent());
		assertNull(node5.parent());

		assertFalse(node2.isLeaf());
		node2.clear();
		assertTrue(node2.isLeaf());
		assertNull(node3.parent());
		assertNull(node7.parent());
		assertNull(node8.parent());

		assertFalse(root.isLeaf());
		root.clear();
		assertTrue(root.isLeaf());
		assertNull(node1.parent());
		assertNull(node2.parent());
		assertNull(node9.parent());
	}

	@Test
	public void testRemove() {
		// Test tree node is successfully removed
		String messageRemoveExpected = "The tree node was expected to be removed, but actually was not";
		String messageRemoveResultTrueExpected =
				"The tree node remove result was expected to be true, but actually was false";

		assertTrue(node3.remove(node4), messageRemoveResultTrueExpected);
		assertFalse(node3.contains(node4), messageRemoveExpected);
		assertTrue(root.remove(node9), messageRemoveResultTrueExpected);
		assertFalse(root.contains(node9), messageRemoveExpected);
		assertFalse(root.contains(node10), messageRemoveExpected);

		// Test nonexistent tree node is not removed
		String messageRemoveNotExpected = "The tree node was not expected to be removed, but actually was";
		String messageRemoveResultFalseExpected =
				"The tree node remove result was expected to be false, but actually was true";

		assertFalse(root.contains(anotherNode));
		assertFalse(root.remove(anotherNode), messageRemoveResultFalseExpected);
		assertFalse(root.contains(anotherNode), messageRemoveNotExpected);
		assertFalse(node7.remove(node6), messageRemoveResultFalseExpected);
		assertFalse(node1.remove(null), messageRemoveResultFalseExpected);
		assertFalse(node1.remove(root), messageRemoveResultFalseExpected);

		// Test if there are equal nodes only first is removed
		TreeNode<String> mNodeToRemove1 = createTreeNode(ANOTHER_NODE_DATA);
		TreeNode<String> mNodeToRemove2 = createTreeNode(ANOTHER_NODE_DATA);
		node1.add(mNodeToRemove1);
		node1.add(mNodeToRemove2);
		assertTrue(root.remove(mNodeToRemove1), messageRemoveResultTrueExpected);
		assertFalse(root.contains(mNodeToRemove1), messageRemoveExpected);
		assertTrue(root.contains(mNodeToRemove2), messageRemoveNotExpected);
		assertTrue(node1.contains(mNodeToRemove2), messageRemoveNotExpected);
	}

	@Test
	public void testRemoveAll() {
		// Test all tree nodes are successfully removed
		String messageRemoveExpected = "The tree node was expected to be removed, but actually was not";
		String messageRemoveResultTrueExpected =
				"The tree node remove result was expected to be true, but actually was false";

		Collection<TreeNode<String>> mNodesToRemove1 = new ArrayList<>(3);
		mNodesToRemove1.add(node6);
		mNodesToRemove1.add(node7);
		mNodesToRemove1.add(node10);
		assertTrue(root.removeAll(mNodesToRemove1), messageRemoveResultTrueExpected);
		assertFalse(root.contains(node6), messageRemoveExpected);
		assertFalse(root.contains(node7), messageRemoveExpected);
		assertFalse(root.contains(node10), messageRemoveExpected);

		Collection<TreeNode<String>> mNodesToRemove2 = Collections.singletonList(node4);
		assertTrue(node2.removeAll(mNodesToRemove2), messageRemoveResultTrueExpected);
		assertFalse(node2.contains(node4), messageRemoveExpected);
		assertFalse(node3.contains(node4), messageRemoveExpected);

		// Test only the tree nodes, which belongs to the current tree are removed
		Collection<TreeNode<String>> mNodesToRemove3 = new ArrayList<>(2);
		mNodesToRemove3.add(node8);
		mNodesToRemove3.add(anotherNode);
		assertTrue(node2.removeAll(mNodesToRemove3), messageRemoveResultTrueExpected);
		assertFalse(node2.contains(node8), messageRemoveResultTrueExpected);
		assertFalse(root.contains(node8), messageRemoveResultTrueExpected);

		// Test nonexistent tree node is not removed
		String messageRemoveResultFalseExpected =
				"The tree node remove result was expected to be false, but actually was true";

		assertFalse(root.contains(node7));
		Collection<TreeNode<String>> mNodesToRemove4 = new ArrayList<>(2);
		mNodesToRemove4.add(node7);
		mNodesToRemove4.add(anotherNode);
		assertFalse(root.removeAll(mNodesToRemove4), messageRemoveResultFalseExpected);
		assertFalse(node1.removeAll(mNodesToRemove4), messageRemoveResultFalseExpected);
		assertFalse(node2.removeAll(null), messageRemoveResultFalseExpected);
		assertFalse(root.removeAll(Collections.<TreeNode<String>>emptyList()), messageRemoveResultFalseExpected);
	}

	@Test
	public void testTraversePreOrder() {
		// Test tree pre order traversal is correct
		String message = "Tree was incorrectly traversed in a pre ordered manner";
		final Collection<TreeNode<String>> mPreOrderedActual = new ArrayList<>(10);
		TraversalAction<TreeNode<String>> action = populateCollectionAction(mPreOrderedActual);
		root.traversePreOrder(action);
		assertEquals(preOrderedExpected(), mPreOrderedActual, message);
	}

	private Collection<TreeNode<String>> preOrderedExpected() {
		// Populate collection with the tree nodes in a pre ordered manner
		Collection<TreeNode<String>> mPreOrderedExpected = new ArrayList<>(10);
		mPreOrderedExpected.add(root);
		mPreOrderedExpected.add(node1);
		mPreOrderedExpected.add(node2);
		mPreOrderedExpected.add(node3);
		mPreOrderedExpected.add(node4);
		mPreOrderedExpected.add(node5);
		mPreOrderedExpected.add(node6);
		mPreOrderedExpected.add(node7);
		mPreOrderedExpected.add(node8);
		mPreOrderedExpected.add(node9);
		mPreOrderedExpected.add(node10);
		return mPreOrderedExpected;
	}

	@Test
	public void testTraversePostOrder() {
		// Test tree post order traversal is correct
		String message = "Tree was incorrectly traversed in a post ordered manner";
		final Collection<TreeNode<String>> mPostOrderedActual = new ArrayList<>(10);
		TraversalAction<TreeNode<String>> action = populateCollectionAction(mPostOrderedActual);
		root.traversePostOrder(action);
		assertEquals(postOrderedExpected(), mPostOrderedActual, message);
	}

	private Collection<TreeNode<String>> postOrderedExpected() {
		// Populate collection with the tree nodes in a post ordered manner
		Collection<TreeNode<String>> mPostOrderedExpected = new ArrayList<>(10);
		mPostOrderedExpected.add(node1);
		mPostOrderedExpected.add(node4);
		mPostOrderedExpected.add(node6);
		mPostOrderedExpected.add(node5);
		mPostOrderedExpected.add(node3);
		mPostOrderedExpected.add(node7);
		mPostOrderedExpected.add(node8);
		mPostOrderedExpected.add(node2);
		mPostOrderedExpected.add(node10);
		mPostOrderedExpected.add(node9);
		mPostOrderedExpected.add(root);
		return mPostOrderedExpected;
	}

	private TraversalAction<TreeNode<String>> populateCollectionAction(final Collection<TreeNode<String>> collection) {
		// Populate input collection while traversing the tree
		return new TraversalAction<TreeNode<String>>() {
			@Override
			public void perform(TreeNode<String> node) {
				collection.add(node);
			}

			@Override
			public boolean isCompleted() {
				return false;
			}
		};
	}

	@Test
	public void testPreOrdered() {
		// Test tree pre ordered collection is correct
		String message = "Tree was incorrectly pre ordered";
		assertEquals(preOrderedExpected(), root.preOrdered(), message);
		assertEquals(List.of(node10), node10.preOrdered(), message);
	}

	@Test
	public void testPostOrdered() {
		// Test tree post ordered collection is correct
		String message = "Tree was incorrectly post ordered";
		assertEquals(postOrderedExpected(), root.postOrdered(), message);
		assertEquals(List.of(node1), node1.postOrdered(), message);
	}

	@Test
	public void testPath() {
		// Test path is correctly determined
		String message = "The path between nodes was incorrectly determined";

		Collection<TreeNode<String>> mPath1 = new LinkedList<>();
		mPath1.add(root);
		mPath1.add(node2);
		mPath1.add(node3);
		mPath1.add(node5);
		mPath1.add(node6);
		assertEquals(mPath1, root.path(node6), message);

		Collection<TreeNode<String>> mPath2 = new LinkedList<>();
		mPath2.add(node2);
		mPath2.add(node8);
		assertEquals(mPath2, node2.path(node8), message);

		assertEquals(Collections.singletonList(node10), node10.path(node9), message);
		assertEquals(Collections.singletonList(node9), node9.path(null), message);
		assertEquals(Collections.singletonList(node2), node2.path(node2), message);
	}

	@Test
	public void testPathToRootException() {
		assertThrows(TreeNodeException.class, () -> {
			// Test path to root tree node throws exception
			node3.path(root);
		});
	}

	@Test
	public void testPathToNonExistentNodeException() {
		assertThrows(TreeNodeException.class, () -> {
			// Test path to non descendant / non existent tree node throws exception
			node2.path(node1);
		});
	}

	@Test
	public void testCommonAncestor() {
		// Test common ancestor tree node was incorrectly determined
		String message = "The common ancestor between nodes was incorrectly determined";
		assertEquals(root, node2.commonAncestor(node10), message);
		assertEquals(node2, node6.commonAncestor(node8), message);
		assertEquals(node3, node4.commonAncestor(node5), message);
		assertEquals(root, node1.commonAncestor(node2), message);
		assertEquals(root, node2.commonAncestor(node2), message);
	}

	@Test
	public void testCommonAncestorNullNodeException() {
		assertThrows(TreeNodeException.class, () -> {
			// Test common ancestor of the current tree node and null node throws exception
			node1.commonAncestor(null);
		});
	}

	@Test
	public void testCommonAncestorNonExistentNodeException() {
		assertThrows(TreeNodeException.class, () -> {
			// Test common ancestor of the current tree node and non existent tree node throws exception
			assertFalse(root.contains(anotherNode));
			node2.commonAncestor(anotherNode);
		});
	}

	@Test
	public void testCommonAncestorCurrentNodeIsRootException() {
		assertThrows(TreeNodeException.class, () -> {
			// Test common ancestor of the root tree node and non root tree node trows exception
			root.commonAncestor(node1);
		});
	}

	@Test
	public void testCommonAncestorNodeIsRootException() {
		assertThrows(TreeNodeException.class, () -> {
			// Test common ancestor of the non root tree node and the root tree node trows exception
			node2.commonAncestor(root);
		});
	}

	@Test
	public void testIsSiblingOf() {
		// Test the specified tree node is the sibling of the current tree node
		String messageTrue =
				"The specified tree node was expected to be the sibling of the current tree node, " +
						"but actually was not";
		assertTrue(node1.isSiblingOf(node9), messageTrue);
		assertTrue(node1.isSiblingOf(node2), messageTrue);
		assertTrue(node5.isSiblingOf(node4), messageTrue);

		// Test the specified tree node is not the sibling of the current tree node
		String messageFalse =
				"The specified tree node was not expected to be the sibling of the current tree node, " +
						"but actually was";
		assertFalse(node2.isSiblingOf(null), messageFalse);
		assertFalse(root.isSiblingOf(node5), messageFalse);
		assertFalse(node4.isSiblingOf(root), messageFalse);
		assertFalse(node5.isSiblingOf(node10), messageFalse);
	}

	@Test
	public void testIsAncestorOf() {
		// Test the specified tree node is the ancestor of the current tree node
		String messageTrue =
				"The specified tree node was expected to be the ancestor of the current tree node, " +
						"but actually was not";
		assertTrue(root.isAncestorOf(node1), messageTrue);
		assertTrue(root.isAncestorOf(node8), messageTrue);
		assertTrue(node2.isAncestorOf(node6), messageTrue);
		assertTrue(node9.isAncestorOf(node10), messageTrue);

		// Test the specified tree node is not the ancestor of the current tree node
		String messageFalse =
				"The specified tree node was not expected to be the ancestor of the current tree node, " +
						"but actually was";
		assertFalse(node10.isAncestorOf(node9), messageFalse);
		assertFalse(node9.isAncestorOf(null), messageFalse);
		assertFalse(node2.isAncestorOf(root), messageFalse);
		assertFalse(node1.isAncestorOf(node1), messageFalse);
		assertFalse(node3.isAncestorOf(anotherNode), messageFalse);
	}

	@Test
	public void testIsDescendantOf() {
		// Test the specified tree node is the descendant of the current tree node
		String messageTrue =
				"The specified tree node was expected to be the descendant of the current tree node, " +
						"but actually was not";
		assertTrue(node6.isDescendantOf(root), messageTrue);
		assertTrue(node3.isDescendantOf(root), messageTrue);
		assertTrue(node5.isDescendantOf(node2), messageTrue);
		assertTrue(node10.isDescendantOf(node9), messageTrue);

		// Test the specified tree node is not the descendant of the current tree node
		String messageFalse =
				"The specified tree node was not expected to be the descendant of the current tree node, " +
						"but actually was";
		assertFalse(node3.isDescendantOf(node6), messageFalse);
		assertFalse(node2.isDescendantOf(null), messageFalse);
		assertFalse(root.isDescendantOf(node3), messageFalse);
		assertFalse(node2.isDescendantOf(node1), messageFalse);
		assertFalse(node10.isDescendantOf(node10), messageFalse);
	}

	@Test
	public void testSize() {
		// Test the specified tree node size is correctly calculated
		String message = "Tree node size was incorrectly calculated";
		assertSame(1L, node6.size(), message);
		assertSame(2L, node9.size(), message);
		assertSame(4L, node3.size(), message);
		assertSame(11L, root.size(), message);
	}

	@Test
	public void testHeight() {
		// Test the specified tree node height is correctly calculated
		String message = "Tree node height was incorrectly calculated";
		assertSame(0, node6.height(), message);
		assertSame(1, node5.height(), message);
		assertSame(3, node2.height(), message);
		assertSame(2, node3.height(), message);
		assertSame(4, root.height(), message);
	}

	@Test
	public void testLevel() {
		// Test the specified tree node level is correctly calculated
		String message = "Tree node level was incorrectly calculated";
		assertSame(0, root.level(), message);
		assertSame(2, node10.level(), message);
		assertSame(4, node6.level(), message);
		assertSame(3, node4.level(), message);
	}

	@Test
	public void testClone() {
		// Test the tree node is deeply copied and the copy has its own identity
		TreeNode<String> clonedRoot = root.clone();
		assertNotEquals(root, clonedRoot);
		assertTrue(clonedRoot.isRoot());
		assertEquals(root.size(), clonedRoot.size());
		assertEquals(root.height(), clonedRoot.height());
		assertEquals(root.toString(), clonedRoot.toString());
		for (TreeNode<String> clonedNode : clonedRoot) {
			assertFalse(root.equals(clonedNode) || root.contains(clonedNode),
					"The clone must not share tree nodes with the original");
		}
	}

	@Test
	public void testCloneIsIndependentOfOriginal() {
		TreeNode<String> clonedNode2 = node2.clone();
		assertTrue(clonedNode2.isRoot());
		assertEquals(node2.size(), clonedNode2.size());
		clonedNode2.clear();
		clonedNode2.add(createTreeNode(ANOTHER_NODE_DATA));
		assertEquals(3, node2.subtrees().size(), "Changing the clone must not change the original");
		assertEquals(11, root.size());
		assertTrue(root.hasSubtree(node2));
	}

	@Test
	public void testCloneOfLeaf() {
		TreeNode<String> clonedNode1 = node1.clone();
		assertTrue(clonedNode1.isRoot());
		assertTrue(clonedNode1.isLeaf());
		assertEquals(NODE_DATA_1, clonedNode1.data());
		assertTrue(clonedNode1.add(createTreeNode(ANOTHER_NODE_DATA)));
		assertTrue(node1.isLeaf());
	}

	@Test
	public void testEquals() {
		// Test data equality between the current tree node and the specified tree node
		String messageEqual =
				"The specified tree node was expected to be equal to the current tree node, but actually was not";
		String messageNotEqual =
				"The specified tree node was not expected to be equal to the current tree node, but actually was";

		assertEquals(root, root, messageEqual);
		assertEquals(node1, node1, messageEqual);
		assertEquals(node7, node7, messageEqual);

		assertNotEquals(root, node1, messageNotEqual);
		assertNotEquals(root, node7, messageNotEqual);
		assertNotEquals(node9, node10, messageNotEqual);

		// Test the created tree nodes with the same data are not equal
		TreeNode<String> mAnotherNode1 = createTreeNode(ANOTHER_NODE_DATA);
		TreeNode<String> mAnotherNode2 = createTreeNode(ANOTHER_NODE_DATA);
		assertNotEquals(mAnotherNode1, mAnotherNode2, messageNotEqual);
	}

	@Test
	public void testIteratorNext() {
		// Test iterator next returns the correct tree node
		String message = "Iterator returned incorrect tree node";
		Iterator<TreeNode<String>> mIterator1 = root.iterator();
		assertEquals(root, mIterator1.next(), message);
		assertEquals(node1, mIterator1.next(), message);
		assertEquals(node2, mIterator1.next(), message);
		assertEquals(node3, mIterator1.next(), message);
		assertEquals(node4, mIterator1.next(), message);
		assertEquals(node5, mIterator1.next(), message);
		assertEquals(node6, mIterator1.next(), message);
		assertEquals(node7, mIterator1.next(), message);
		assertEquals(node8, mIterator1.next(), message);
		assertEquals(node9, mIterator1.next(), message);
		assertEquals(node10, mIterator1.next(), message);

		// Test iterator works correctly for one node
		TreeNode<String> mNode = createTreeNode(ANOTHER_NODE_DATA);
		Iterator<TreeNode<String>> mIterator2 = mNode.iterator();
		assertEquals(mNode, mIterator2.next(), message);

		// Test iterator next returns the correct tree node if there are equal subtree nodes
		TreeNode<String> mAnotherNode1 = createTreeNode(ANOTHER_NODE_DATA);
		TreeNode<String> mAnotherNode2 = createTreeNode(ANOTHER_NODE_DATA);
		node1.add(mAnotherNode1);
		node1.add(mAnotherNode2);
		Iterator<TreeNode<String>> mIterator3 = node1.iterator();
		assertEquals(node1, mIterator3.next(), message);
		assertEquals(mAnotherNode1, mIterator3.next(), message);
		assertEquals(mAnotherNode2, mIterator3.next(), message);
	}

	@Test
	public void testIteratorHasNext() {
		// Test iterator must have the next tree node
		String messageHasNextResultTrueExpected =
				"The iterator was expected to have the next tree node, but actually was not";
		Iterator<TreeNode<String>> mIterator1 = node3.iterator();
		assertTrue(mIterator1.hasNext(), messageHasNextResultTrueExpected);
		mIterator1.next();
		mIterator1.next();
		mIterator1.next();
		assertTrue(mIterator1.hasNext(), messageHasNextResultTrueExpected);

		// Test iterator must not have the next tree node
		String messageHasNextResultFalseExpected =
				"The iterator was not expected to have the next tree node, but actually was";
		Iterator<TreeNode<String>> mIterator2 = node9.iterator();
		mIterator2.next();
		mIterator2.next();
		assertFalse(mIterator2.hasNext(), messageHasNextResultFalseExpected);

		// Test the iterator common use case
		Iterator<TreeNode<String>> mIterator3 = root.iterator();
		assertTrue(mIterator3.hasNext(), messageHasNextResultFalseExpected);
	}

	@Test
	public void testIteratorNextNoSuchElementException() {
		assertThrows(NoSuchElementException.class, () -> {
			// Test exception is thrown if there are no tree nodes
			Iterator<TreeNode<String>> mIterator = root.iterator();
			while (mIterator.hasNext()) {
				mIterator.next();
			}
			mIterator.next();
		});
	}

	@Test
	public void testIteratorNextConcurrentModificationException() {
		assertThrows(ConcurrentModificationException.class, () -> {
			// Test exception is thrown if tree is changed during the iteration without iterator
			for (TreeNode<String> mNode : root) {
				if (mNode.equals(node3)) {
					root.remove(node3);
				}
			}
		});
	}

	@Test
	public void testIteratorRemove() {
		// Test iterator removes tree nodes
		String messageRemoveExpected = "The iterator was expected to remove the tree node, but actually was not";
		Iterator<TreeNode<String>> mIterator1 = root.iterator();
		do {
			if (mIterator1.next().equals(node3)) {
				break;
			}
		} while (true);
		mIterator1.remove();
		assertFalse(node2.hasSubtree(node3), messageRemoveExpected);
		assertFalse(root.contains(node4), messageRemoveExpected);
		assertFalse(root.contains(node5), messageRemoveExpected);
		assertFalse(root.contains(node6), messageRemoveExpected);
		assertEquals(node7, mIterator1.next());
		mIterator1.remove();
		assertFalse(node2.contains(node7), messageRemoveExpected);
		assertEquals(node8, mIterator1.next());
		assertEquals(node9, mIterator1.next());
		assertEquals(node10, mIterator1.next());
		mIterator1.remove();
		assertFalse(node9.contains(node10), messageRemoveExpected);
		mIterator1.remove();
		assertFalse(root.contains(node9), messageRemoveExpected);
	}

	@Test
	public void testIteratorRemoveIllegalStateException() {
		assertThrows(IllegalStateException.class, () -> {
			// Test exception is thrown if the iterator remove was called prior the iteration start
			Iterator<TreeNode<String>> iterator = node1.iterator();
			iterator.remove();
		});
	}

	@Test
	public void testIteratorRemoveOnCurrentNodeTreeNodeException() {
		assertThrows(TreeNodeException.class, () -> {
			// Test exception is thrown if iterator remove was called on the top tree node
			Iterator<TreeNode<String>> iterator = node2.iterator();
			iterator.next();
			iterator.remove();
		});
	}

	@Test
	public void testIteratorRemoveOnRootTreeNodeException() {
		assertThrows(TreeNodeException.class, () -> {
			// Test exception is thrown if iterator remove was called on the root tree node
			Iterator<TreeNode<String>> iterator = root.iterator();
			iterator.next();
			iterator.remove();
		});
	}

	@Test
	public void testIteratorRemoveConcurrentModificationException() {
		assertThrows(ConcurrentModificationException.class, () -> {
			// Test exception is thrown if tree was changed while calling iterator remove
			Iterator<TreeNode<String>> iterator = root.iterator();
			do {
				if (iterator.next().equals(node7)) {
					break;
				}
			} while (true);
			iterator.remove();
			node2.dropSubtree(node8);
			iterator.remove();
		});
	}

	@Test
	public void testCommonAncestorAcrossBranches() {
		// Nodes at different depths in different branches share only the root
		assertEquals(root, node6.commonAncestor(node10));
		assertEquals(root, node10.commonAncestor(node6));
		assertEquals(root, node4.commonAncestor(node10));
		// Nodes at different depths in the same branch
		assertEquals(node2, node6.commonAncestor(node7));
		assertEquals(node2, node7.commonAncestor(node6));
		// A node and its descendant: the node's parent, a node is not its own ancestor
		assertEquals(node2, node3.commonAncestor(node6));
	}

	@Test
	public void testAddAfterDroppingLastSubtree() {
		assertTrue(node2.dropSubtree(node8));
		TreeNode<String> added = createTreeNode(ANOTHER_NODE_DATA);
		assertTrue(node2.add(added));
		assertTrue(node2.hasSubtree(added));
		assertEquals(3, node2.subtrees().size());
	}

	@Test
	public void testAddAfterDroppingAllSubtrees() {
		assertTrue(node9.dropSubtree(node10));
		assertTrue(node9.add(createTreeNode(ANOTHER_NODE_DATA)));
		assertTrue(node9.add(createTreeNode(ANOTHER_NODE_DATA)));
		assertEquals(2, node9.subtrees().size());
	}

	@Test
	public void testAddToFormerSubtreeAfterClear() {
		root.clear();
		assertTrue(node3.add(createTreeNode(ANOTHER_NODE_DATA)));
		assertEquals(3, node3.subtrees().size());
		assertTrue(node2.add(createTreeNode(ANOTHER_NODE_DATA)));
		assertEquals(4, node2.subtrees().size());
	}

	@Test
	public void testAddReparentsSubtree() {
		assertTrue(node9.add(node3));
		assertFalse(node2.hasSubtree(node3), "The subtree must be detached from its former parent");
		assertTrue(node9.hasSubtree(node3));
		assertEquals(node9, node3.parent());
		assertEquals(2, node2.subtrees().size());
		assertEquals(11, root.size());
		// The former parent keeps working
		assertTrue(node2.add(createTreeNode(ANOTHER_NODE_DATA)));
		assertEquals(3, node2.subtrees().size());
	}

	@Test
	public void testAddExistingSubtreeMovesItToTheEnd() {
		assertTrue(node2.add(node3));
		assertEquals(Arrays.asList(node7, node8, node3), new ArrayList<>(node2.subtrees()));
		assertEquals(11, root.size());
	}

	@Test
	public void testAddSelfThrowsException() {
		assertThrows(TreeNodeException.class, () -> node2.add(node2));
	}

	@Test
	public void testAddAncestorThrowsException() {
		assertThrows(TreeNodeException.class, () -> node5.add(node2));
		assertThrows(TreeNodeException.class, () -> node6.add(root));
		// The tree is left unchanged
		assertTrue(root.hasSubtree(node2));
		assertEquals(11, root.size());
	}

	@Test
	public void testIteratorDetectsSizePreservingModification() {
		Iterator<TreeNode<String>> iterator = root.iterator();
		iterator.next();
		TreeNode<String> added = createTreeNode(ANOTHER_NODE_DATA);
		node9.add(added);
		node9.dropSubtree(added);
		assertThrows(ConcurrentModificationException.class, iterator::next);
	}

	@Test
	public void testIteratorIgnoresModificationOutsideIteratedSubtree() {
		Iterator<TreeNode<String>> iterator = node2.iterator();
		iterator.next();
		node9.add(createTreeNode(ANOTHER_NODE_DATA));
		assertDoesNotThrow(iterator::next);
	}

	@Test
	public void testIteratorOnLargeTreeIsLinear() {
		// Used to recompute the tree size on every step, which made iteration quadratic
		TreeNode<String> wide = createTreeNode(ROOT_DATA);
		for (int i = 0; i < 1_000; i++) {
			TreeNode<String> child = createTreeNode(NODE_DATA_1);
			wide.add(child);
			for (int j = 0; j < 100; j++) {
				child.add(createTreeNode(NODE_DATA_2));
			}
		}
		long[] count = {0};
		assertTimeoutPreemptively(Duration.ofSeconds(10), () -> wide.forEach(node -> count[0]++));
		assertEquals(101_001, count[0]);
	}

	@Test
	public void testStreamIsPreOrder() {
		assertEquals(new ArrayList<>(root.preOrdered()), root.stream().toList());
		assertEquals(List.of(node1), node1.stream().toList());
		assertEquals(new ArrayList<>(node2.preOrdered()), node2.stream().toList());
	}

	@Test
	public void testStreamIsLazy() {
		assertEquals(Optional.of(node2), root.stream().filter(node -> NODE_DATA_2.equals(node.data())).findFirst());
		assertEquals(3, root.stream().limit(3).count());
	}

	@Test
	public void testStreamFailsFastOnModification() {
		assertThrows(ConcurrentModificationException.class,
				() -> root.stream().forEach(node -> {
					if (node.equals(node3)) {
						root.remove(node9);
					}
				}));
	}

	@Test
	public void testFindFirst() {
		assertEquals(Optional.of(node1), root.findFirst(NODE_DATA_1));
		assertEquals(Optional.of(node7), root.findFirst(null));
		assertEquals(Optional.empty(), root.findFirst(ANOTHER_NODE_DATA));
		assertEquals(Optional.of(node5), node3.findFirst(NODE_DATA_1));
		assertEquals(Optional.empty(), node1.findFirst(NODE_DATA_2));
	}

	@Test
	public void testFindAllIsPreOrder() {
		assertEquals(List.of(node1, node5, node8), new ArrayList<>(root.findAll(NODE_DATA_1)));
	}

	@Test
	public void testOptionalParent() {
		assertEquals(Optional.empty(), root.optionalParent());
		assertEquals(Optional.of(node3), node5.optionalParent());
	}

	@Test
	public void testAncestors() {
		assertEquals(List.of(), root.ancestors().toList());
		assertEquals(List.of(root), node1.ancestors().toList());
		assertEquals(List.of(node5, node3, node2, root), node6.ancestors().toList());
	}

	@Test
	public void testTraversalActionAsLambda() {
		List<TreeNode<String>> visited = new ArrayList<>();
		root.traversePreOrder(visited::add);
		assertEquals(new ArrayList<>(root.preOrdered()), visited);
		visited.clear();
		root.traversePostOrder(visited::add);
		assertEquals(new ArrayList<>(root.postOrdered()), visited);
	}

	@Test
	public void testDeepTreeDoesNotOverflowStack() throws Throwable {
		// A chain deep enough to overflow a small thread stack with recursive traversals
		int depth = 10_000;
		TreeNode<String> deepRoot = createTreeNode(ROOT_DATA);
		TreeNode<String> current = deepRoot;
		for (int i = 0; i < depth; i++) {
			TreeNode<String> next = createTreeNode(NODE_DATA_1);
			current.add(next);
			current = next;
		}
		TreeNode<String> deepLeaf = current;
		Throwable[] failure = {null};
		Thread thread = new Thread(null, () -> {
			try {
				assertEquals(depth + 1, deepRoot.size());
				assertEquals(depth, deepRoot.height());
				assertEquals(depth, deepLeaf.level());
				assertTrue(deepRoot.contains(deepLeaf));
				assertEquals(depth + 1, deepRoot.preOrdered().size());
				assertEquals(depth + 1, deepRoot.postOrdered().size());
				assertEquals(depth + 1, deepRoot.findAll(NODE_DATA_1).size() + 1);
				assertFalse(deepRoot.toString().isEmpty());
				TreeNode<String> deepClone = deepRoot.clone();
				assertEquals(depth + 1, deepClone.size());
				assertEquals(depth, deepClone.height());
				assertTrue(deepRoot.remove(deepLeaf));
				assertEquals(depth, deepRoot.size());
			} catch (Throwable e) {
				failure[0] = e;
			}
		}, "small-stack", 256 * 1024);
		thread.start();
		thread.join();
		if (failure[0] != null) {
			throw failure[0];
		}
	}

	@Test
	public void testPreOrderTraversalStopsWhenCompleted() {
		List<TreeNode<String>> visited = new ArrayList<>();
		root.traversePreOrder(new TraversalAction<>() {
			@Override
			public void perform(TreeNode<String> node) {
				visited.add(node);
			}

			@Override
			public boolean isCompleted() {
				return visited.size() == 3;
			}
		});
		assertEquals(List.of(root, node1, node2), visited);
	}

	@Test
	public void testPostOrderTraversalFinishesEnteredNodesWhenCompleted() {
		// Completion is checked when entering a node, so ancestors of the completing node are
		// still performed, while not yet entered subtrees are skipped
		List<TreeNode<String>> visited = new ArrayList<>();
		root.traversePostOrder(new TraversalAction<>() {
			@Override
			public void perform(TreeNode<String> node) {
				visited.add(node);
			}

			@Override
			public boolean isCompleted() {
				return visited.contains(node4);
			}
		});
		assertEquals(List.of(node1, node4, node3, node2, root), visited);
	}

}
