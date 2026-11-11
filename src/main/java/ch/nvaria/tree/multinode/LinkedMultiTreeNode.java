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

package ch.nvaria.tree.multinode;

import ch.nvaria.tree.TreeNode;
import ch.nvaria.tree.TreeNodeException;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of the K-ary (multi node) tree data structure,
 * based on the leftmost-child-right-sibling representation
 *
 * @author shell
 * @version 1.0.0
 * @since 1.0.0
 */
public class LinkedMultiTreeNode<T> extends MultiTreeNode<T> {

	/**
	 * Current UID of this object used for serialization
	 */
	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * A reference to the first subtree tree node of the current tree node
	 */
	private LinkedMultiTreeNode<T> leftMostNode;

	/**
	 * A reference to the right sibling tree node of the current tree node
	 */
	private LinkedMultiTreeNode<T> rightSiblingNode;

	/**
	 * A reference to the left sibling tree node of the current tree node, so that
	 * a subtree can be dropped in O(1) instead of scanning the list of siblings
	 */
	private LinkedMultiTreeNode<T> leftSiblingNode;

	/**
	 * A reference to the last subtree node of the current tree node
	 * <p>
	 * Used to avoid the discovery of the last subtree node. As a result
	 * significantly optimized such operations like addition etc.
	 */
	private LinkedMultiTreeNode<T> lastSubtreeNode;

	/**
	 * Creates an instance of this class
	 *
	 * @param data data to store in the current tree node
	 */
	public LinkedMultiTreeNode(T data) {
		super(data);
	}

	/**
	 * Returns the collection of the child nodes of the current node
	 * with all of its proper descendants, if any
	 * <p>
	 * Returns an empty list if the current node is leaf
	 *
	 * @return collection of the child nodes of the current node with
	 *         all of its proper descendants, if any;
	 *         an empty list if the current node is leaf
	 */
	@Override
	public List<TreeNode<T>> subtrees() {
		if (isLeaf()) {
			return List.of();
		}
		List<TreeNode<T>> subtrees = new ArrayList<>();
		for (LinkedMultiTreeNode<T> subtree = leftMostNode; subtree != null; subtree = subtree.rightSiblingNode) {
			subtrees.add(subtree);
		}
		return Collections.unmodifiableList(subtrees);
	}

	/**
	 * Adds the subtree with all of its descendants to the current tree node
	 * <p>
	 * {@code null} subtree cannot be added, in this case return result will
	 * be {@code false}
	 * <p>
	 * Checks whether this tree node was changed as a result of the call
	 *
	 * @param subtree subtree to add to the current tree node
	 * @return {@code true} if this tree node was changed as a
	 *         result of the call; {@code false} otherwise
	 */
	@Override
	public boolean add(TreeNode<T> subtree) {
		if (subtree == null) {
			return false;
		}
		// Cast before changing anything, so a subtree of another type leaves both trees intact
		LinkedMultiTreeNode<T> linkedSubtree = (LinkedMultiTreeNode<T>) subtree;
		prepareAttach(subtree);
		linkParent(subtree, this);
		if (isLeaf()) {
			leftMostNode = linkedSubtree;
		} else {
			lastSubtreeNode.rightSiblingNode = linkedSubtree;
			linkedSubtree.leftSiblingNode = lastSubtreeNode;
		}
		lastSubtreeNode = linkedSubtree;
		structureChanged();
		return true;
	}

	/**
	 * Drops the first occurrence of the specified subtree from the current
	 * tree node
	 * <p>
	 * Checks whether the current tree node was changed as a result of
	 * the call
	 *
	 * @param subtree subtree to drop from the current tree node
	 * @return {@code true} if the current tree node was changed as a result
	 *         of the call; {@code false} otherwise
	 */
	@Override
	public boolean dropSubtree(TreeNode<T> subtree) {
		if (subtree == null
				|| subtree.parent() != this) {
			return false;
		}
		LinkedMultiTreeNode<T> linkedSubtree = (LinkedMultiTreeNode<T>) subtree;
		LinkedMultiTreeNode<T> left = linkedSubtree.leftSiblingNode;
		LinkedMultiTreeNode<T> right = linkedSubtree.rightSiblingNode;
		if (left == null) {
			leftMostNode = right;
		} else {
			left.rightSiblingNode = right;
		}
		if (right == null) {
			lastSubtreeNode = left;
		} else {
			right.leftSiblingNode = left;
		}
		linkedSubtree.leftSiblingNode = null;
		linkedSubtree.rightSiblingNode = null;
		unlinkParent(subtree);
		structureChanged();
		return true;
	}

	/**
	 * Removes all the subtrees with all of its descendants from the current
	 * tree node
	 */
	@Override
	public void clear() {
		if (!isLeaf()) {
			LinkedMultiTreeNode<T> nextNode = leftMostNode;
			while (nextNode != null) {
				unlinkParent(nextNode);
				LinkedMultiTreeNode<T> nextNodeRightSiblingNode = nextNode.rightSiblingNode;
				nextNode.rightSiblingNode = null;
				nextNode.leftSiblingNode = null;
				nextNode = nextNodeRightSiblingNode;
			}
			leftMostNode = null;
			lastSubtreeNode = null;
			structureChanged();
		}
	}

	/**
	 * Returns an iterator over the elements in this tree in proper sequence
	 * <p>
	 * The returned iterator is <b>fail-fast</b>
	 *
	 * @return an iterator over the elements in this tree in proper sequence
	 */
	@Override
	public LinkedMultiTreeNode<T> clone() {
		return (LinkedMultiTreeNode<T>) super.clone();
	}

	@Override
	protected LinkedMultiTreeNode<T> detachedCopy() {
		LinkedMultiTreeNode<T> copy = (LinkedMultiTreeNode<T>) super.detachedCopy();
		copy.leftMostNode = null;
		copy.rightSiblingNode = null;
		copy.leftSiblingNode = null;
		copy.lastSubtreeNode = null;
		return copy;
	}

	/**
	 * Checks whether the current tree node is a leaf, e.g. does not have any
	 * subtrees
	 *
	 * @return {@code true} if the current tree node is a leaf, e.g. does not
	 *         have any subtrees; {@code false} otherwise
	 */
	@Override
	public boolean isLeaf() {
		return leftMostNode == null;
	}

	/**
	 * Checks whether among the current tree node subtrees there is
	 * a specified subtree
	 * <p>
	 * Overridden to work directly on the sibling links
	 *
	 * @param subtree subtree whose presence within the current tree
	 *                node children is to be checked
	 * @return {@code true} if among the current tree node subtrees
	 *         there is a specified subtree; {@code false} otherwise
	 */
	@Override
	public boolean hasSubtree(TreeNode<T> subtree) {
		return subtree != null
				&& subtree.parent() == this;
	}

	/**
	 * Returns the collection of nodes, which have the same parent
	 * as the current node; an empty list if the current
	 * tree node is root or if the current tree node has no subtrees
	 * <p>
	 * Overridden to work directly on the sibling links
	 *
	 * @return collection of nodes, which have the same parent as
	 *         the current node; an empty list if the
	 *         current tree node is root or if the current tree node has
	 *         no subtrees
	 */
	@Override
	public List<MultiTreeNode<T>> siblings() {
		if (isRoot()) {
			throw new TreeNodeException("Unable to find the siblings. The tree node " + describe(this) + " is root");
		}
		List<MultiTreeNode<T>> siblings = new ArrayList<>();
		LinkedMultiTreeNode<T> sibling = ((LinkedMultiTreeNode<T>) parent()).leftMostNode;
		for (; sibling != null; sibling = sibling.rightSiblingNode) {
			if (sibling != this) {
				siblings.add(sibling);
			}
		}
		return Collections.unmodifiableList(siblings);
	}

}
