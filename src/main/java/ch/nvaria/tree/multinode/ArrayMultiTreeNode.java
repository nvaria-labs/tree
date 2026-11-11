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

import ch.nvaria.tree.TraversalAction;
import ch.nvaria.tree.TreeNode;
import ch.nvaria.tree.TreeNodeException;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the K-ary (multi node) tree data structure,
 * based on the resizable array representation
 *
 * @author shell
 * @version 1.0.0
 * @since 1.0.0
 */
public class ArrayMultiTreeNode<T> extends MultiTreeNode<T> {

	/**
	 * Current UID of this object used for serialization
	 */
	@Serial
	private static final long serialVersionUID = 2L;

	/**
	 * Default initial branching factor, that is the number of subtrees
	 * this node can have before getting resized
	 */
	private static final int DEFAULT_BRANCHING_FACTOR = 10;

	/**
	 * The subtrees of the current tree node, in insertion order
	 */
	private ArrayList<TreeNode<T>> subtrees;

	/**
	 * Current branching factor of the current tree node
	 */
	private final int branchingFactor;

	/**
	 * Constructs the {@link ArrayMultiTreeNode} instance
	 *
	 * @param data data to store in the current tree node
	 */
	public ArrayMultiTreeNode(T data) {
		this(data, DEFAULT_BRANCHING_FACTOR);
	}

	/**
	 * Constructs the {@link ArrayMultiTreeNode} instance
	 *
	 * @param data data to store in the current tree node
	 * @param branchingFactor initial branching factor, that is the number
	 *                        of subtrees the current tree node can have
	 *                        before getting resized
	 */
	public ArrayMultiTreeNode(T data, int branchingFactor) {
		super(data);
		if (branchingFactor < 0) {
			throw new IllegalArgumentException("Branching factor can not be negative");
		}
		this.branchingFactor = branchingFactor;
		this.subtrees = new ArrayList<>(branchingFactor);
	}

	/**
	 * Returns a snapshot of the subtrees of the current tree node, in insertion order
	 * <p>
	 * Returns an empty collection if the current node is leaf
	 *
	 * @return snapshot of the subtrees of the current tree node
	 */
	@Override
	public List<TreeNode<T>> subtrees() {
		return List.copyOf(subtrees);
	}

	@Override
	public boolean add(TreeNode<T> subtree) {
		if (subtree == null) {
			return false;
		}
		prepareAttach(subtree);
		linkParent(subtree, this);
		subtrees.add(subtree);
		structureChanged();
		return true;
	}

	@Override
	public boolean dropSubtree(TreeNode<T> subtree) {
		if (subtree == null
				|| subtree.isRoot()
				|| !subtrees.remove(subtree)) {
			return false;
		}
		unlinkParent(subtree);
		structureChanged();
		return true;
	}

	@Override
	public void clear() {
		if (!isLeaf()) {
			subtrees.forEach(TreeNode::unlinkParent);
			subtrees = new ArrayList<>(branchingFactor);
			structureChanged();
		}
	}

	@Override
	public ArrayMultiTreeNode<T> clone() {
		ArrayMultiTreeNode<T> copy = (ArrayMultiTreeNode<T>) super.clone();
		copy.subtrees = new ArrayList<>(Math.max(branchingFactor, subtrees.size()));
		subtrees.forEach(subtree -> copy.add(subtree.clone()));
		return copy;
	}

	/**
	 * Returns the iterator over the current tree node and all of its descendants, in pre-order
	 *
	 * @return iterator over the current tree node and all of its descendants
	 */
	@Override
	public TreeNodeIterator iterator() {
		return new TreeNodeIterator() {

			@Override
			protected TreeNode<T> leftMostNode() {
				return subtrees.getFirst();
			}

			@Override
			protected TreeNode<T> rightSiblingNode() {
				List<TreeNode<T>> siblings = ((ArrayMultiTreeNode<T>) parent()).subtrees;
				int rightSiblingNodeIndex = siblings.indexOf(ArrayMultiTreeNode.this) + 1;
				return rightSiblingNodeIndex < siblings.size() ? siblings.get(rightSiblingNodeIndex) : null;
			}

		};
	}

	@Override
	public boolean isLeaf() {
		return subtrees.isEmpty();
	}

	@Override
	public boolean hasSubtree(TreeNode<T> subtree) {
		return subtree != null
				&& !subtree.isRoot()
				&& subtrees.contains(subtree);
	}

	@Override
	public boolean contains(TreeNode<T> node) {
		if (node == null
				|| isLeaf()
				|| node.isRoot()) {
			return false;
		}
		for (TreeNode<T> subtree : subtrees) {
			if (subtree.equals(node) || subtree.contains(node)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean remove(TreeNode<T> node) {
		if (node == null
				|| isLeaf()
				|| node.isRoot()) {
			return false;
		}
		if (dropSubtree(node)) {
			return true;
		}
		for (TreeNode<T> subtree : subtrees) {
			if (subtree.remove(node)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void traversePreOrder(TraversalAction<TreeNode<T>> action) {
		if (!action.isCompleted()) {
			action.perform(this);
			for (TreeNode<T> subtree : subtrees) {
				subtree.traversePreOrder(action);
			}
		}
	}

	@Override
	public void traversePostOrder(TraversalAction<TreeNode<T>> action) {
		if (!action.isCompleted()) {
			for (TreeNode<T> subtree : subtrees) {
				subtree.traversePostOrder(action);
			}
			action.perform(this);
		}
	}

	@Override
	public int height() {
		return isLeaf() ? 0 : 1 + subtrees.stream().mapToInt(TreeNode::height).max().orElse(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MultiTreeNode<T>> siblings() {
		if (isRoot()) {
			String message = String.format("Unable to find the siblings. The tree node %1$s is root", root());
			throw new TreeNodeException(message);
		}
		List<MultiTreeNode<T>> siblings = new ArrayList<>();
		for (TreeNode<T> sibling : ((ArrayMultiTreeNode<T>) parent()).subtrees) {
			if (!sibling.equals(this)) {
				siblings.add((MultiTreeNode<T>) sibling);
			}
		}
		return siblings;
	}

}
