---
name: changelog-generator
description: Generate changelog from git history since the last tag and writes to CHANGELOG.md.
---

# Changelog Generator

Generate a user-facing changelog from the git commit history since the latest tag and write it to `CHANGELOG.md`.

## Instructions

### 1. Get the latest tag

Run:

```bash
git describe --tags --abbrev=0
```

Use the returned tag as the starting point for the changelog.

### 2. Retrieve commit history

Run:

```bash
git log <tag>..HEAD --pretty=format:"%h %s%n%b" --no-merges
```

Include both commit subjects and descriptions when analyzing changes.

### 3. Rewrite commit messages

Rewrite commits into clear, user-friendly descriptions:

* Begin each sentence with a capital letter.
* Use concise and easy-to-understand language.
* End each description with proper punctuation.
* Minimize technical jargon when possible.
* Do not include commit hashes.

### 4. Categorize changes

Group the rewritten commits into the following sections:

* **New Features**

    * User-visible functionality and additions.

* **Improvements**

    * Enhancements, optimizations, UI refinements, performance improvements, and refactors.

* **Bug Fixes**

    * Fixes for crashes, incorrect behavior, and UI issues.

#### Exclude

Do not include:

* Chores
* CI/CD changes
* Build configuration changes
* Dependency updates
* Developer-only changes that do not affect users

### 5. Determine the new version

Extract the current app version from the `versionName` property in:

```text
app/build.gradle.kts
```

### 6. Generate the changelog

Overwrite `CHANGELOG.md` using the following format:

```markdown
# Version <new_version>

## New Features
- <feature 1>
- <feature 2>

## Improvements
- <improvement 1>
- <improvement 2>

## Bug Fixes
- <bug fix 1>
- <bug fix 2>
```

## Rules

* Remove any section that has no entries.
* Preserve the order of changes within each section.
* Overwrite the existing `CHANGELOG.md` file.


