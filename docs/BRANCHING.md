# Branching and commit policy

## Branches
| Branch | Purpose | Who changes it |
|---|---|---|
| `main` | Stable, release-ready code. Every release is tagged here. | Only by pull request from `develop` or `hotfix/*` |
| `develop` | Integration branch. All finished features are merged here first. | Only by pull request from `feature/*` or `bugfix/*` |
| `feature/<issue>-<short-name>` | One new feature or user story | Developer |
| `bugfix/<issue>-<short-name>` | A fix for a defect found on `develop` | Developer |
| `hotfix/<short-name>` | An urgent fix on `main` | Developer |
| `release/<version>` | Final checks before tagging a version | Developer |

## Naming rules
- Lower case, words separated by hyphens, no spaces.
- Start with the type, then the issue number, then a short name.
- Examples: `feature/5-create-note`, `feature/12-search-notes`, `bugfix/20-search-case`, `release/1.0.0`.

## Commit message convention
Format: `type: short summary in the present tense` (maximum about 70 characters).

| Type | Use for |
|---|---|
| `feat` | A new feature |
| `fix` | A bug fix |
| `docs` | Documentation only |
| `test` | Adding or changing tests |
| `build` | Maven, Docker or dependency changes |
| `ci` | Jenkins pipeline changes |
| `chore` | Housekeeping (ignore files, formatting) |

Examples: `feat: add note search by title and subject`, `test: add repository search test`.

## Pull request rules
1. Open a pull request from the feature branch into `develop`, and link the issue (`Closes #n`).
2. The build (`mvn clean package`) must pass before merging.
3. At least one review comment is recorded before the merge, even for a one-person project.
4. Merge with a merge commit so the history of the branch stays visible.
5. Delete the feature branch after the merge.

## Releases
- When `develop` is stable, open a pull request from `develop` to `main`.
- Tag the merge on `main` with a version, for example `v0.1.0`, using `git tag -a v0.1.0 -m "message"`.
- Version format is `vMAJOR.MINOR.PATCH`.

## Rules for everyone
- Never commit directly to `main` or `develop`.
- Never commit `target/`, passwords, tokens or personal files.
- Pull the latest `develop` before starting a new branch.
