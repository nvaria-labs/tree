# 0.4.0

1. Forked from Scalified/tree; Maven group changed to `ch.nvaria`
2. Requires Java 21
3. Build: Gradle 9.9, version catalog, single-module layout, Gitea workflows (Travis removed)
4. Tests migrated from JUnit 4 to JUnit 6
5. Added NOTICE crediting Scalified
6. **Breaking:** packages renamed from `com.scalified.tree` to `ch.nvaria.tree`; JPMS module `ch.nvaria.tree`
7. **Breaking:** ordered results are returned as `List`: `subtrees()`, `preOrdered()`, `postOrdered()`,
   `path()`, `findAll()`, `siblings()`

### Additions

1. `stream()` and `spliterator()`: lazy pre-order stream over a node and its descendants
2. `ancestors()`: stream of proper ancestors, nearest first
3. `findFirst(T)` and `optionalParent()`: `Optional` variants of `find(T)` and `parent()`
4. `TraversalAction` is a `@FunctionalInterface`; `isCompleted()` defaults to `false`, so
   whole-tree traversals can be written as lambdas
5. `containsAll` / `removeAll` accept `Collection<? extends TreeNode<T>>`
6. `findAll` and `siblings` return their results in tree order

### Fixes

1. `commonAncestor` returned wrong results for nodes in different branches at different depths
2. `add` now detaches the node from its former parent instead of leaving it under two parents
3. `add` throws `TreeNodeException` when the node is the current node or one of its ancestors,
   instead of creating a cycle
4. `LinkedMultiTreeNode`: adding after dropping the last subtree silently lost the new subtree;
   adding to a former subtree after `clear` failed with `NullPointerException`
5. `ArrayMultiTreeNode.addSubtrees` could insert `null` elements
6. Iteration was quadratic: the tree size was recomputed on every step. Concurrent modification
   is now detected with a modification counter, which also catches size-preserving changes
7. `clone` is now a deep copy with its own identity. **Behavior change:** the clone is no longer
   equal to the original
8. `ArrayMultiTreeNode` is backed by an `ArrayList`; serialized form changed (`serialVersionUID` 2)
9. Deep trees no longer throw `StackOverflowError` ([Scalified/tree#6](https://github.com/Scalified/tree/issues/6),
   [#13](https://github.com/Scalified/tree/issues/13)): traversals, `size`, `height`, `toString` and `clone` use an
   explicit stack; `contains` and `remove(node)` walk up from the node in O(depth) instead of searching the tree.
   New protected hook `detachedCopy()` replaces overriding `clone()` in implementations
10. `add` is O(1) for leaves: it no longer walks all ancestors twice (100k-deep chain: 130 s -> 40 ms)
11. New pre-order iterator with an explicit stack: iteration is linear (wide `ArrayMultiTreeNode`s were
    quadratic); `Iterator.remove` no longer leaves the subtree the iteration started from.
    **Behavior change:** a second `remove()` without `next()` throws `IllegalStateException` instead of
    removing the parent; concurrent modification is detected when the iteration continues from a
    modified tree node (best effort, as in `java.util` collections)
12. **Breaking:** `iterator()` returns `Iterator<TreeNode<T>>` and is implemented in `TreeNode`;
    `TreeNodeIterator` was removed (it was protected, so the result of `iterator()` was not usable
    outside the package)
13. Exception messages describe nodes by their data (abbreviated to 100 characters) instead of
    `toString()` of the whole subtree, which put every data value of the tree into logs
14. All returned lists are unmodifiable snapshots (`subtrees` of `LinkedMultiTreeNode` used to be
    modifiable without effect)
15. `LinkedMultiTreeNode`: left sibling pointer, so `dropSubtree` and `hasSubtree` are O(1) instead of
    scanning the siblings; adding a node of another type throws before changing either tree
16. `ArrayMultiTreeNode`: the child list is allocated on the first `add` (leaves allocate nothing);
    default initial branching factor lowered from 10 to 2. `dropSubtree`/`hasSubtree` compare by identity

# 0.2.4

1. Fixed broken ordering

# 0.2.3

1. Enhancement - [Offer default constructors for Jackson serialization #1](https://github.com/Scalified/tree/issues/1)

# 0.2.0

1. Changed main interfaces to classes
2. Added LinkedMultiTreeNode - MultiTreeNode implementation based on the leftmost-child-right-sibling representation
3. Added subtrees clearing logic and tests
4. New find and findAll methods - allows to find nodes within the tree with the specified data

# 0.1.1

1. Changed java major version to 51 (Java 1.7)
2. Removed unnecessary util package

# 0.1.0

1. Initial release. There is only one implementation of the K-ary (multi node) tree data structure, 
   based on the resizable array
