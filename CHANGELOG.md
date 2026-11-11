# 0.3.0

1. Forked from Scalified/tree; Maven group changed to `ch.nvaria`
2. Requires Java 21
3. Build: Gradle 9.9, version catalog, single-module layout, Gitea workflows (Travis removed)
4. Tests migrated from JUnit 4 to JUnit 6
5. Added NOTICE crediting Scalified

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
