# Contributing to PL-2016

When contributing to the PL-2016 project, please keep in mind the following rules and guidelines.

## Branches
When committing to the PL2-2016 project, any changes should be made on a separate branch and, when finished, merged against the appropriate branch.

#### dev
The `dev` branch is the default branch. Pull requests containing new features should be based off this branch and made against this branch.

#### next
The `next` branch is the stabilization branch. Pull requests that fix bugs in the existing codebase should be made against this branch. These pull requests should be based off `dev` or `next`, depending on the source of the bug.

### Branch naming
Branchesmade based off the PL-2016 project should follow the the naming convention specified below.

#### Branch grouping
Branches should be grouped in one of the following groups

- `feat/`
- `bug/`
- `doc/`
- `misc/`

Furthermore, the name of the branch should reflect at goal of the branch as much as possible. The name should conform to the `lisp-case` naming format (which is `snake_case` with normal dashes)

##### Example branch names
- `doc/add-bugreport`
- `feat/new-dropdown-menu`
- `misc/add-some-binary-files`
- `bug/fix-segfault-on-zoom`
