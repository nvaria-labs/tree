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
	private static final int DEFAULT_BRANCHING_FACTOR = 2;

	/**
	 * The subtrees of the current tree node, in insertion order; {@code null} while the
	 * current tree node is a leaf, so that leaves, the majority of nodes, allocate no array
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
		return isLeaf() ? List.of() : List.copyOf(subtrees);
	}

	@Override
	public boolean add(TreeNode<T> subtree) {
		if (subtree == null) {
			return false;
		}
		prepareAttach(subtree);
		linkParent(subtree, this);
		if (subtrees == null) {
			subtrees = new ArrayList<>(branchingFactor);
		}
		subtrees.add(subtree);
		structureChanged();
		return true;
	}

	@Override
	public boolean dropSubtree(TreeNode<T> subtree) {
		if (subtree == null
				|| subtree.parent() != this) {
			return false;
		}
		subtrees.remove(indexOf(subtree));
		if (subtrees.isEmpty()) {
			subtrees = null;
		}
		unlinkParent(subtree);
		structureChanged();
		return true;
	}

	/**
	 * Returns the index of the specified subtree, compared by identity, so that subclasses
	 * overriding {@link Object#equals(Object)} can't make the tree find another subtree
	 *
	 * @param subtree subtree of the current tree node
	 * @return index of the subtree; {@code -1} if it is not a subtree of the current tree node
	 */
	private int indexOf(TreeNode<T> subtree) {
		for (int i = 0; i < subtrees.size(); i++) {
			if (subtrees.get(i) == subtree) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public void clear() {
		if (!isLeaf()) {
			subtrees.forEach(TreeNode::unlinkParent);
			subtrees = null;
			structureChanged();
		}
	}

	@Override
	public ArrayMultiTreeNode<T> clone() {
		return (ArrayMultiTreeNode<T>) super.clone();
	}

	@Override
	protected ArrayMultiTreeNode<T> detachedCopy() {
		ArrayMultiTreeNode<T> copy = (ArrayMultiTreeNode<T>) super.detachedCopy();
		copy.subtrees = null;
		return copy;
	}

	@Override
	public boolean isLeaf() {
		return subtrees == null;
	}

	@Override
	public boolean hasSubtree(TreeNode<T> subtree) {
		return subtree != null
				&& subtree.parent() == this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MultiTreeNode<T>> siblings() {
		if (isRoot()) {
			throw new TreeNodeException("Unable to find the siblings. The tree node " + describe(this) + " is root");
		}
		List<MultiTreeNode<T>> siblings = new ArrayList<>();
		for (TreeNode<T> sibling : ((ArrayMultiTreeNode<T>) parent()).subtrees) {
			if (sibling != this) {
				siblings.add((MultiTreeNode<T>) sibling);
			}
		}
		return Collections.unmodifiableList(siblings);
	}

}
