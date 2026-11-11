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

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * This interface represents the basic tree data structure
 * <h2>Definition</h2>
 * A tree data structure can be defined recursively (locally) as a collection of nodes
 * (starting at a root node), where each node is a data structure consisting of a value,
 * together with a list of references to nodes (the children), with the constraints that
 * no reference is duplicated, and none points to the root
 * <p>
 * A tree is a (possibly non-linear) data structure made up of nodes or vertices and edges
 * without having any cycle. The tree with no nodes is called the <b>null</b> or
 * <b>empty</b> tree. A tree that is not empty consists of a root node and potentially many
 * levels of additional nodes that form a hierarchy
 * <h2>Terminology</h2>
 * <ul>
 *     <li><b>Node</b> - a single point of a tree</li>
 *     <li><b>Edge</b> - line, which connects two distinct nodes</li>
 *     <li><b>Root</b> - top node of the tree, which has no parent</li>
 *     <li><b>Parent</b> - a node, other than the root, which is connected to other successor
 *                         nodes</li>
 *     <li><b>Child</b> - a node, other than the root, which is connected to predecessor</li>
 *     <li><b>Leaf</b> - a node without children</li>
 *     <li><b>Path</b> - a sequence of nodes and edges connecting a node with a
 *                       descendant</li>
 *     <li><b>Path Length</b> - number of nodes in the path - 1</li>
 *     <li><b>Ancestor</b> - the top parent node of the path</li>
 *     <li><b>Descendant</b> - the bottom child node of the path</li>
 *     <li><b>Siblings</b> - nodes, which have the same parent</li>
 *     <li><b>Subtree</b> - a node in a tree with all of its proper descendants, if any</li>
 *     <li><b>Node Height</b> - the number of edges on the longest downward path between that
 *                              node and a leaf</li>
 *     <li><b>Tree Height</b> - the number of edges on the longest downward path between the
 *                              root and a leaf (root height)</li>
 *     <li><b>Depth (Level)</b> - the path length between the root and the current node</li>
 *     <li><b>Ordered Tree</b> - tree in which nodes has the children ordered</li>
 *     <li><b>Labeled Tree</b> - tree in which a label or value is associated with each node
 *                               of the tree</li>
 *     <li><b>Expression Tree</b> - tree which specifies the association of an expression�s
 *                                  operands and its operators in a uniform way, regardless
 *                                  of whether the association is required by the placement
 *                                  of parentheses in the expression or by the precedence and
 *                                  associativity rules for the operators involved</li>
 *     <li><b>Branching Factor</b> - maximum number of children a node can have</li>
 *     <li><b>Pre order</b> - a form of tree traversal, where the action is called firstly on
 *                           the current node, and then the pre order function is called again
 *                           recursively on each of the subtree from left to right</li>
 *     <li><b>Post order</b> - a form of tree traversal, where the post order function is called
 *                            recursively on each subtree from left to right and then the
 *                            action is called</li>
 * </ul>
 *
 * @author shell
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class TreeNode<T> implements Iterable<TreeNode<T>>, Serializable, Cloneable {

	/**
	 * Current UID of this object used for serialization
	 */
	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * Identifier generator, used to get a unique id for each created tree node
	 */
	private static final AtomicLong ID_GENERATOR = new AtomicLong(0);

	/**
	 * A unique identifier, used to distinguish or compare the tree nodes
	 */
	private long id = ID_GENERATOR.getAndIncrement();

	/**
	 * Reference to the parent tree node. Is {@code null} if the current tree node is root
	 */
	protected TreeNode<T> parent;

	/**
	 * Data store in the current tree node
	 */
	protected T data;

	/**
	 * Number of structural changes of the subtrees of this tree node
	 */
	private transient int modCount;

	/**
	 * Creates an instance of this class
	 *
	 * @param data data to store in the current tree node
	 */
	public TreeNode(T data) {
		this.data = data;
	}

	/**
	 * Creates an instance of this class without setting the {@link #data}
	 */
	public TreeNode() {
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
	public abstract List<TreeNode<T>> subtrees();

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
	public abstract boolean add(TreeNode<T> subtree);

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
	public abstract boolean dropSubtree(TreeNode<T> subtree);

	/**
	 * Removes all the subtrees with all of its descendants from the current
	 * tree node
	 */
	public abstract void clear();

	/**
	 * Returns an iterator over the current tree node and all of its descendants, in pre-order
	 * <p>
	 * The iterator is <b>fail-fast</b> on a best-effort basis: when the iteration continues
	 * from a tree node whose subtrees were structurally changed other than through the
	 * iterator's own {@link Iterator#remove()}, it throws {@link ConcurrentModificationException}
	 * <p>
	 * {@link Iterator#remove()} removes the last returned tree node together with its
	 * descendants, which are then skipped. It can remove neither the root tree node nor the
	 * tree node the iteration started from
	 *
	 * @return an iterator over the current tree node and all of its descendants, in pre-order
	 */
	@Override
	public Iterator<TreeNode<T>> iterator() {
		return new PreOrderIterator<>(this);
	}

	/**
	 * Returns the data object stored in the current tree node
	 *
	 * @return data object stored in the current tree node
	 */
	public T data() {
		return data;
	}

	/**
	 * Stores the data object into the current tree node
	 *
	 * @param data data object to store into the current tree node
	 */
	public void setData(T data) {
		this.data = data;
	}

	/**
	 * Checks whether the current tree node is the root of the tree
	 *
	 * @return {@code true} if the current tree node is root of the tree;
	 *         {@code false} otherwise
	 */
	public boolean isRoot() {
		return parent == null;
	}

	/**
	 * Returns the root node of the current node
	 * <p>
	 * Returns itself if the current node is root
	 *
	 * @return root node of the current node; itself,
	 *         if the current node is root
	 */
	public TreeNode<T> root() {
		TreeNode<T> node = this;
		while (!node.isRoot()) {
			node = node.parent();
		}
		return node;
	}

	/**
	 * Returns the parent node of the current node
	 * <p>
	 * Returns {@code null} if the current node is root
	 *
	 * @return parent node of the current node; {@code null}
	 *         if the current node is root
	 */
	public TreeNode<T> parent() {
		return parent;
	}

	/**
	 * Returns the parent node of the current node, if any
	 *
	 * @return parent node of the current node; {@link Optional#empty()}
	 *         if the current node is root
	 */
	public Optional<TreeNode<T>> optionalParent() {
		return Optional.ofNullable(parent());
	}

	/**
	 * Returns the proper ancestors of the current node, starting with
	 * its parent and ending with the root
	 * <p>
	 * Returns an empty stream if the current node is root
	 *
	 * @return stream of the proper ancestors of the current node, nearest first
	 */
	public Stream<TreeNode<T>> ancestors() {
		return Stream.iterate(parent(), Objects::nonNull, TreeNode::parent);
	}

	/**
	 * Returns a sequential stream of the current tree node and all of its
	 * descendants, in pre-order. The stream is lazy and fails fast with
	 * {@link ConcurrentModificationException} if the tree is structurally
	 * changed while it is consumed
	 *
	 * @return stream of the current tree node and all of its descendants, in pre-order
	 */
	public Stream<TreeNode<T>> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

	/**
	 * Creates a spliterator over the current tree node and all of its
	 * descendants, in pre-order
	 *
	 * @return spliterator over the current tree node and all of its descendants
	 */
	@Override
	public Spliterator<TreeNode<T>> spliterator() {
		return Spliterators.spliteratorUnknownSize(iterator(),
				Spliterator.ORDERED | Spliterator.DISTINCT | Spliterator.NONNULL);
	}

	/**
	 * Checks whether the current tree node is a leaf, e.g. does not have any
	 * subtrees
	 *
	 * @return {@code true} if the current tree node is a leaf, e.g. does not
	 *         have any subtrees; {@code false} otherwise
	 */
	public boolean isLeaf() {
		return subtrees().isEmpty();
	}

	/**
	 * Searches the tree node within the tree, which has the specified data,
	 * starting from the current tree node and returns the first occurrence of it
	 *
	 * @param data data to find the tree node with
	 * @return first occurrence of the searched tree node with data specified;
	 *         {@code null} if there is none
	 * @see #findFirst(Object)
	 */
	public TreeNode<T> find(T data) {
		return findFirst(data).orElse(null);
	}

	/**
	 * Searches the tree node within the tree, which has the specified data,
	 * starting from the current tree node and returns the first occurrence of it
	 * in pre-order
	 *
	 * @param data data to find the tree node with
	 * @return first occurrence of the searched tree node with data specified;
	 *         {@link Optional#empty()} if there is none
	 */
	public Optional<TreeNode<T>> findFirst(T data) {
		return stream()
				.filter(node -> Objects.equals(node.data(), data))
				.findFirst();
	}

	/**
	 * Searches the tree nodes within the tree, which have the specified data,
	 * starting from the current tree node and returns the collection of the found
	 * tree nodes
	 *
	 * @param data data to find the tree nodes with
	 * @return collection of the searched tree nodes with data specified, in pre-order
	 */
	public List<TreeNode<T>> findAll(T data) {
		return stream()
				.filter(node -> Objects.equals(node.data(), data))
				.toList();
	}

	/**
	 * Checks whether among the current tree node subtrees there is
	 * a specified subtree
	 *
	 * @param subtree subtree whose presence within the current tree
	 *                node children is to be checked
	 * @return {@code true} if among the current tree node subtrees
	 *         there is a specified subtree; {@code false} otherwise
	 */
	public boolean hasSubtree(TreeNode<T> subtree) {
		return subtree != null
				&& !subtree.isRoot()
				&& this.equals(subtree.parent());
	}

	/**
	 * Checks whether the current tree node with all of its descendants
	 * (entire tree) contains the specified node
	 *
	 * @param node node whose presence within the current tree node with
	 *             all of its descendants (entire tree) is to be checked
	 * @return {@code true} if the current node with all of its descendants
	 *         (entire tree) contains the specified node; {@code false}
	 *         otherwise
	 */
	public boolean contains(TreeNode<T> node) {
		return node != null
				&& !node.isRoot()
				&& node.ancestors().anyMatch(this::equals);
	}

	/**
	 * Checks whether the current tree node with all of its descendants
	 * (entire tree) contains all of the nodes from the specified collection
	 * (the place of nodes within a tree is not important)
	 *
	 * @param nodes collection of nodes to be checked for containment
	 *              within the current tree node with all of its descendants
	 *              (entire tree)
	 * @return {@code true} if the current tree node with all of its
	 *         descendants (entire tree) contains all of the nodes from the
	 *         specified collection; {@code false} otherwise
	 */
	public boolean containsAll(Collection<? extends TreeNode<T>> nodes) {
		if (isLeaf()
				|| areAllNulls(nodes)) {
			return false;
		}
		return nodes.stream().allMatch(this::contains);
	}

	/**
	 * Removes the first occurrence of the specified node from the entire tree,
	 * starting from the current tree node and traversing in a pre order manner
	 * <p>
	 * Checks whether the current tree node was changed as a result of the call
	 *
	 * @param node node to remove from the entire tree
	 * @return {@code true} if the current tree node was changed as a result of
	 *         the call; {@code false} otherwise
	 */
	public boolean remove(TreeNode<T> node) {
		return contains(node) && node.parent().dropSubtree(node);
	}

	/**
	 * Removes all of the collection's nodes from the entire tree, starting from
	 * the current tree node and traversing in a pre order manner
	 * <p>
	 * Checks whether the current tree node was changed as a result of the call
	 *
	 * @param nodes collection containing nodes to be removed from the entire tree
	 * @return {@code true} if the current tree node was changed as a result
	 *         of the call; {@code false} otherwise
	 */
	public boolean removeAll(Collection<? extends TreeNode<T>> nodes) {
		if (isLeaf()
				|| areAllNulls(nodes)) {
			return false;
		}
		boolean result = false;
		for (TreeNode<T> node : nodes) {
			result |= remove(node);
		}
		return result;
	}

	/**
	 * Traverses the tree in a pre ordered manner starting from the
	 * current tree node and performs the traversal action on each
	 * traversed tree node
	 *
	 * @param action action, which is to be performed on each tree
	 *               node, while traversing the tree
	 */
	public void traversePreOrder(TraversalAction<TreeNode<T>> action) {
		// Explicit stack instead of recursion, so deep trees do not overflow the call stack.
		// The completion check runs where the recursive version entered a node: a completed
		// traversal skips the node together with its whole subtree
		Deque<TreeNode<T>> stack = new ArrayDeque<>();
		stack.push(this);
		while (!stack.isEmpty()) {
			TreeNode<T> node = stack.pop();
			if (action.isCompleted()) {
				continue;
			}
			action.perform(node);
			for (TreeNode<T> subtree : node.subtrees().reversed()) {
				stack.push(subtree);
			}
		}
	}

	/**
	 * Traverses the tree in a post ordered manner starting from the
	 * current tree node and performs the traversal action on each
	 * traversed tree node
	 *
	 * @param action action, which is to be performed on each tree
	 *               node, while traversing the tree
	 */
	public void traversePostOrder(TraversalAction<TreeNode<T>> action) {
		// Explicit stack instead of recursion, so deep trees do not overflow the call stack.
		// As in the recursive version, completion is checked when entering a node; a node that
		// was entered is still performed after its subtrees, even if they completed the traversal
		if (action.isCompleted()) {
			return;
		}
		Deque<Frame<T>> stack = new ArrayDeque<>();
		stack.push(new Frame<>(this, this.subtrees().iterator()));
		while (!stack.isEmpty()) {
			Frame<T> frame = stack.peek();
			if (frame.subtrees().hasNext()) {
				TreeNode<T> subtree = frame.subtrees().next();
				if (!action.isCompleted()) {
					stack.push(new Frame<>(subtree, subtree.subtrees().iterator()));
				}
			} else {
				stack.pop();
				action.perform(frame.node());
			}
		}
	}

	/**
	 * A tree node together with the iterator over its not yet visited subtrees
	 */
	private record Frame<T>(TreeNode<T> node, Iterator<TreeNode<T>> subtrees) {
	}

	/**
	 * Returns the pre ordered collection of nodes of the current tree
	 * starting from the current tree node
	 *
	 * @return pre ordered collection of nodes of the current tree starting
	 *         from the current tree node
	 */
	public List<TreeNode<T>> preOrdered() {
		List<TreeNode<T>> preOrdered = new ArrayList<>();
		traversePreOrder(preOrdered::add);
		return preOrdered;
	}

	/**
	 * Returns the post ordered collection of nodes of the current tree
	 * starting from the current tree node
	 *
	 * @return post ordered collection of nodes of the current tree starting
	 *         from the current tree node
	 */
	public List<TreeNode<T>> postOrdered() {
		List<TreeNode<T>> postOrdered = new ArrayList<>();
		traversePostOrder(postOrdered::add);
		return postOrdered;
	}

	/**
	 * Returns the collection of nodes, which connect the current node
	 * with its descendants
	 *
	 * @param descendant the bottom child node for which the path is calculated
	 * @return collection of nodes, which connect the current node with its descendants
	 * @throws TreeNodeException exception that may be thrown in case if the
	 *                           current node does not have such descendant or if the
	 *                           specified tree node is root
	 */
	public List<TreeNode<T>> path(TreeNode<T> descendant) {
		if (descendant == null
				|| isLeaf()
				|| this.equals(descendant)) {
			return List.of(this);
		}
		String errorMessage = "Unable to build the path between tree nodes. ";
		if (descendant.isRoot()) {
			String message = String.format(errorMessage + "Current node %1$s is root", descendant);
			throw new TreeNodeException(message);
		}
		LinkedList<TreeNode<T>> path = new LinkedList<>();
		path.addFirst(descendant);
		for (TreeNode<T> node = descendant.parent(); node != null; node = node.parent()) {
			path.addFirst(node);
			if (this.equals(node)) {
				return path;
			}
		}
		String message = String.format(errorMessage +
				"The specified tree node %1$s is not the descendant of tree node %2$s", descendant, this);
		throw new TreeNodeException(message);
	}

	/**
	 * Returns the common ancestor of the current node and the node specified,
	 * which is the nearest node that is a proper ancestor of both. A node is
	 * not considered its own ancestor, so for a node and one of its descendants
	 * the parent of that node is returned
	 *
	 * @param node node, which the common ancestor is determined for,
	 *             along with the current node
	 * @return common ancestor of the current node and the node specified
	 * @throws TreeNodeException exception that may be thrown in case if the
	 *                          specified tree node is null or the specified tree node
	 *                          does not belong to the current tree or if any of the tree
	 *                          nodes either the current one or the specified one is root
	 */
	public TreeNode<T> commonAncestor(TreeNode<T> node) {
		String errorMessage = "Unable to find the common ancestor between tree nodes. ";
		if (node == null) {
			String message = errorMessage + "The specified tree node is null";
			throw new TreeNodeException(message);
		}
		if (this.root() != node.root()) {
			String message = String.format(errorMessage +
					"The specified tree node %1$s was not found in the current tree node %2$s", node, this);
			throw new TreeNodeException(message);
		}
		if (this.isRoot()
				|| node.isRoot()) {
			String message = String.format(errorMessage + "The tree node %1$s is root", this.isRoot() ? this : node);
			throw new TreeNodeException(message);
		}
		Set<TreeNode<T>> nodeAncestors = node.ancestors()
				.collect(Collectors.toCollection(() -> Collections.newSetFromMap(new IdentityHashMap<>())));
		return ancestors()
				.filter(nodeAncestors::contains)
				.findFirst()
				.orElseThrow();
	}

	/**
	 * Checks whether the current tree node is a sibling of the specified node,
	 * e.g. whether the current tree node and the specified one both have the
	 * same parent
	 *
	 * @param node node, which sibling with the current tree node is to be checked
	 * @return {@code true} if the current tree node is a sibling of the specified
	 *         node, e.g. whether the current tree node and the specified one both
	 *         have the same parent; {@code false} otherwise
	 */
	public boolean isSiblingOf(TreeNode<T> node) {
		return node != null
				&& !isRoot()
				&& !node.isRoot()
				&& this.parent().equals(node.parent());
	}

	/**
	 * Checks whether the current tree node is the ancestor of the node specified
	 *
	 * @param node node, which is checked to be the descendant of the current tree
	 *             node
	 * @return {@code true} if the current tree node is the ancestor of the node
	 *         specified; {@code false} otherwise
	 */
	public boolean isAncestorOf(TreeNode<T> node) {
		if (node == null
				|| isLeaf()
				|| node.isRoot()
				|| this.equals(node)) {
			return false;
		}
		return node.ancestors().anyMatch(this::equals);
	}

	/**
	 * Checks whether the current tree node is the descendant of the node specified
	 *
	 * @param node node, which is checked to be the ancestor of the current tree
	 *             node
	 * @return {@code true} if the current tree node is the descendant of the node
	 *         specified; {@code false} otherwise
	 */
	public boolean isDescendantOf(TreeNode<T> node) {
		if (node == null
				|| this.isRoot()
				|| node.isLeaf()
				|| this.equals(node)) {
			return false;
		}
		return ancestors().anyMatch(node::equals);
	}

	/**
	 * Returns the number of nodes in the entire tree, including the current tree node
	 *
	 * @return number of nodes in the entire tree, including the current tree node
	 */
	public long size() {
		if (isLeaf()) {
			return 1;
		}
		long[] count = {0};
		traversePreOrder(node -> count[0]++);
		return count[0];
	}

	/**
	 * Returns the height of the current tree node, e.g. the number of edges
	 * on the longest downward path between that node and a leaf
	 *
	 * @return height of the current tree node, e.g. the number of edges
	 * on the longest downward path between that node and a leaf
	 */
	public int height() {
		// Level by level instead of recursion, so deep trees do not overflow the call stack
		int height = 0;
		List<TreeNode<T>> level = subtrees();
		while (!level.isEmpty()) {
			height++;
			List<TreeNode<T>> nextLevel = new ArrayList<>();
			for (TreeNode<T> node : level) {
				nextLevel.addAll(node.subtrees());
			}
			level = nextLevel;
		}
		return height;
	}

	/**
	 * Returns the depth (level) of the current tree node within the entire tree,
	 * e.g. the number of edges between the root tree node and the current one
	 *
	 * @return depth (level) of the current tree node within the entire tree,
	 *         e.g. the number of edges between the root tree node and the current
	 *         one
	 */
	public int level() {
		return (int) ancestors().count();
	}

	/**
	 * Creates a deep copy of the current tree node and all of its descendants.
	 * The copy is a root tree node with its own identity, so it is not equal
	 * to the original. The data objects are not copied, but shared
	 * <p>
	 * Implementations storing subtrees must override {@link #detachedCopy()}
	 *
	 * @return a deep copy of the current tree node
	 */
	@Override
	public TreeNode<T> clone() {
		// Built bottom-up from a post-order traversal: each copy is attached to its parent's copy
		// while that one is still a root, so attaching is O(1) and deep trees do not recurse
		Map<TreeNode<T>, TreeNode<T>> copies = new IdentityHashMap<>();
		traversePostOrder(node -> {
			TreeNode<T> copy = node.detachedCopy();
			for (TreeNode<T> subtree : node.subtrees()) {
				copy.add(copies.remove(subtree));
			}
			copies.put(node, copy);
		});
		return copies.get(this);
	}

	/**
	 * Creates a copy of the current tree node only: same data, but its own identity,
	 * no parent and no subtrees. Used by {@link #clone()}
	 * <p>
	 * Implementations storing subtrees must override this method and reset the
	 * subtree storage of the copy
	 *
	 * @return a detached copy of the current tree node without subtrees
	 */
	@SuppressWarnings("unchecked")
	protected TreeNode<T> detachedCopy() {
		try {
			TreeNode<T> copy = (TreeNode<T>) super.clone();
			copy.id = ID_GENERATOR.getAndIncrement();
			copy.parent = null;
			copy.modCount = 0;
			return copy;
		} catch (CloneNotSupportedException e) {
			String message = "Unable to clone the current tree node";
			throw new TreeNodeException(message, e);
		}
	}

	/**
	 * Indicates whether some object equals to this one
	 *
	 * @param obj the reference object with which to compare
	 * @return {@code true} if this object is the same as the obj
	 *         argument; {@code false} otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		return this == obj
				|| obj instanceof TreeNode<?> that
				&& getClass() == that.getClass()
				&& this.id == that.id;
	}

	/**
	 * Returns the hash code value of this object
	 *
	 * @return hash code value of this object
	 */
	@Override
	public int hashCode() {
		return Long.hashCode(id);
	}

	/**
	 * Returns the string representation of this object
	 *
	 * @return string representation of this object
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("\n");
		Deque<Map.Entry<TreeNode<T>, Integer>> stack = new ArrayDeque<>();
		stack.push(Map.entry(this, 0));
		while (!stack.isEmpty()) {
			Map.Entry<TreeNode<T>, Integer> entry = stack.pop();
			TreeNode<T> node = entry.getKey();
			int depth = entry.getValue();
			builder.append("|  ".repeat(depth))
					.append("+- ")
					.append(node.data())
					.append("\n");
			for (TreeNode<T> subtree : node.subtrees().reversed()) {
				stack.push(Map.entry(subtree, depth + 1));
			}
		}
		return builder.toString();
	}

	/**
	 * Populates the input collection with the tree nodes, while traversing the tree
	 *
	 * @param collection input collection to populate
	 * @param <T> type of the tree node
	 * @return traversal action, which populates the input collection with the tree nodes
	 */
	protected static <T> TraversalAction<TreeNode<T>> populateAction(Collection<TreeNode<T>> collection) {
		return collection::add;
	}

	/**
	 * Links the specified parent tree node reference as the parent to the
	 * specified tree node
	 *
	 * @param node tree node to assign the parent tree node reference to
	 * @param parent tree node to assign as a parent reference
	 * @param <T> type of the data stored in the tree nodes
	 */
	protected static <T> void linkParent(TreeNode<T> node, TreeNode<T> parent) {
		if (node != null) {
			node.parent = parent;
		}
	}

	/**
	 * Removes the parent tree node reference link from the specified tree node
	 *
	 * @param node tree node to remove the parent tree node reference assignment from
	 * @param <T> type of the data store in the tree node
	 */
	/**
	 * Records a structural change of the subtrees of the current tree node, such as
	 * adding or removing a subtree. Must be called by implementations after each
	 * structural change, so that iterators fail fast with
	 * {@link ConcurrentModificationException}
	 */
	protected void structureChanged() {
		modCount++;
	}

	/**
	 * Prepares the specified subtree for being attached to the current tree node:
	 * detaches it from its current parent, if any
	 *
	 * @param subtree subtree to be attached to the current tree node
	 * @throws TreeNodeException if the subtree is the current tree node or one of
	 *                           its ancestors, since attaching it would create a cycle
	 */
	protected void prepareAttach(TreeNode<T> subtree) {
		// Every ancestor has at least one subtree, so a leaf other than the current tree node
		// can't be one of them: the walk up to the root is only needed for non-leaf subtrees
		if (subtree == this || !subtree.isLeaf() && ancestors().anyMatch(ancestor -> ancestor == subtree)) {
			String message = String.format(
					"Unable to add the tree node %1$s. It is the current tree node or one of its ancestors", subtree);
			throw new TreeNodeException(message);
		}
		if (subtree.parent != null) {
			subtree.parent.dropSubtree(subtree);
		}
	}

	protected static <T> void unlinkParent(TreeNode<T> node) {
		node.parent = null;
	}

	/**
	 * Checks whether there is at least one not {@code null} element within
	 * the input collection
	 * <pre>
	 *     Validator.isAnyNotNull(Arrays.asList("foo", null))   = true
	 *     Validator.isAnyNotNull(null)                         = false
	 *     Validator.isAnyNotNull(Collections.emptyList())      = false
	 *     Validator.isAnyNotNull(Arrays.asList(null, null))    = false
	 * </pre>
	 *
	 * @param collection input collection to check
	 * @param <T> type of the data, which parametrises collection
	 * @return {@code true} if there is at least one not {@code null} element within
	 *         the input collection; {@code false} otherwise
	 */
	protected static <T> boolean isAnyNotNull(Collection<T> collection) {
		return collection != null && collection.stream().anyMatch(Objects::nonNull);
	}

	/**
	 * Checks whether the specified collection is @{code null}, empty or if
	 * all of its elements are {@code null}
	 * <pre>
	 *     areAllNulls(null)                          = true
	 *     areAllNulls(Collections.emptyList())       = true
	 *     areAllNulls(Arrays.asList(null, null))     = true
	 *     areAllNulls(Arrays.asList("foo", null))    = false
	 * </pre>
	 *
	 * @param collection input collection to check
	 * @param <T> type of the data, which parametrises collection
	 * @return {@code true} if the specified collection is {@code null}, empty
	 *         or if all of its elements are {@code null}; {@code false} otherwise
	 */
	protected static <T> boolean areAllNulls(Collection<T> collection) {
		return !isAnyNotNull(collection);
	}

	/**
	 * Pre-order iterator using an explicit stack of levels, so it neither recurses nor
	 * walks the tree to find the next node: each step is amortized O(1)
	 */
	private static final class PreOrderIterator<T> implements Iterator<TreeNode<T>> {

		/**
		 * A tree node returned by the iterator, with a snapshot of its subtrees
		 * still to visit and its modification count at the time of the snapshot
		 */
		private static final class Level<T> {

			private final TreeNode<T> node;

			private final Iterator<TreeNode<T>> subtrees;

			private int expectedModCount;

			private Level(TreeNode<T> node) {
				this.node = node;
				this.expectedModCount = node.modCount;
				this.subtrees = node.subtrees().iterator();
			}

			private boolean isModified() {
				return node.modCount != expectedModCount;
			}

			private void checkForConcurrentModification() {
				if (isModified()) {
					throw new ConcurrentModificationException();
				}
			}

		}

		private final TreeNode<T> start;

		private final Deque<Level<T>> stack = new ArrayDeque<>();

		private boolean started;

		private TreeNode<T> lastReturned;

		private PreOrderIterator(TreeNode<T> start) {
			this.start = start;
		}

		@Override
		public boolean hasNext() {
			if (!started) {
				return true;
			}
			while (!stack.isEmpty()) {
				Level<T> top = stack.peek();
				// A modified level reports a next element, so that next() fails fast
				if (top.isModified() || top.subtrees.hasNext()) {
					return true;
				}
				stack.pop();
			}
			return false;
		}

		@Override
		public TreeNode<T> next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			TreeNode<T> node;
			if (started) {
				Level<T> top = stack.peek();
				top.checkForConcurrentModification();
				node = top.subtrees.next();
			} else {
				started = true;
				node = start;
			}
			stack.push(new Level<>(node));
			lastReturned = node;
			return node;
		}

		@Override
		public void remove() {
			String errorMessage = "Failed to remove the tree node. ";
			if (lastReturned == null) {
				throw new IllegalStateException(errorMessage
						+ "next() has not been called, or remove() has already been called after the last call to next()");
			}
			if (lastReturned.isRoot()) {
				String message = String.format(errorMessage + "The tree node %1$s is root", lastReturned);
				throw new TreeNodeException(message);
			}
			if (lastReturned == start) {
				throw new TreeNodeException(errorMessage + "The starting node can't be removed");
			}
			TreeNode<T> parent = lastReturned.parent();
			Level<T> parentLevel = null;
			for (Level<T> level : stack) {
				if (level.node == parent) {
					parentLevel = level;
					break;
				}
				if (level.node != lastReturned) {
					break;
				}
			}
			if (parentLevel != null) {
				parentLevel.checkForConcurrentModification();
			}
			// Skip the descendants of the removed tree node
			if (!stack.isEmpty() && stack.peek().node == lastReturned) {
				stack.pop();
			}
			parent.dropSubtree(lastReturned);
			if (parentLevel != null) {
				parentLevel.expectedModCount = parent.modCount;
			}
			lastReturned = null;
		}

	}

}
