# AGENTS.md — lsFusion IntelliJ plugin

General engineering and process notes for AI coding agents (Codex, Cursor, Claude Code, Gemini CLI, GitHub Copilot) working on the lsFusion IntelliJ plugin. Feature-level lore stays in commit messages, code, and per-agent memory — this file is for durable cross-cutting process knowledge.

## Repo layout

Single-module Gradle project (Kotlin DSL).

- `src/` — plugin sources (Java + Kotlin).
- `src/com/lsfusion/lang/LSF.bnf` — Grammar-Kit BNF defining the lsFusion language parser.
- `src/com/lsfusion/lang/LSF.flex` — JFlex lexer source.
- `src/com/lsfusion/lang/psi/references/` — PSI reference contracts; `references/impl/` holds implementations.
- `gen/` — auto-generated parser, lexer, and PSI classes. Never edited by hand.
- `META-INF/plugin.xml` — plugin manifest; the `<version>` element is the source of truth for the artifact version.
- `resources/` — bundled icons, message bundles, etc.

## Sister repository

The lsFusion platform (Java server, ANTLR3 grammar, docs) lives at `../platform/`. A platform syntax change usually requires more than one plugin edit:

- **`LSF.bnf`** — add or update the parser rules.
- **`LSF.flex`** — when the change introduces a new keyword or literal form, add it to the lexer's keyword/literal list (otherwise the lexer won't recognize the new token even if the BNF accepts it).
- **Parser/lexer regeneration** via `./gradlew generateLsfParser generateLsfLexer` so `gen/` matches the new sources.
- **Annotator / inspection support** — wire into `LSFReferenceAnnotator` per the Code conventions section, when the change adds new references, deprecates an old form, or warrants a highlight.

Coordinate commits across the two repos (cross-link in commit messages, or close the same GitHub issue from both sides).

## Build and run

```
./gradlew build                  # full build; generates parser/lexer first
./gradlew generateLsfParser      # regenerate just the parser from LSF.bnf
./gradlew generateLsfLexer       # regenerate just the lexer from LSF.flex
./gradlew generateMigrationParser
./gradlew generateMigrationLexer
./gradlew runIde                 # launch IntelliJ sandbox with the plugin loaded
```

Toolchain: Java 21 JDK (required by IntelliJ Platform 2025.3). Source and bytecode level are both pinned to the same `javaVersion` value in `build.gradle.kts` (currently 21); check that file for the authoritative target rather than hard-coding a number here.

After editing `LSF.bnf` or `LSF.flex`, regenerate before the next build or sandbox launch so `gen/` matches the source grammar. `gen/` itself is gitignored — don't stage generated files in commits.

### Testing changes

`./gradlew runIde` launches an IntelliJ sandbox with the plugin loaded against a fresh project — open or create a `.lsf` file to exercise highlighting, references, completion, and annotations. The sandbox state lives under `build/idea-sandbox/`; deleting it forces a clean re-init on the next launch.

There's no headless test rig for the plugin in this repo — UI verification is interactive in the sandbox. There is also no `test` sourceSet currently configured in `build.gradle.kts`: the existing `main` sourceSet maps everything under `src/` (and `gen/`) as production code, so a file placed at `src/test/...` would compile into the plugin artifact. To add unit tests for pure logic (PSI walkers, reference resolution helpers), first wire a dedicated `test` sourceSet pointing at a directory that does not overlap with `src/` (e.g. a sibling `tests/`), then run `./gradlew test`.

## Commits

- Short imperative subject, ≤72 chars. Suffix `(closes #NN)` for plugin issues, or `(lsfusion/platform#NNNN)` when cross-linking to a platform issue that drove the change.
- Body explains **why**. The diff shows what.
- Never amend or force-push commits that may have been pulled by others. Always create a new commit on top.
- Never pass `--no-verify` unless the user explicitly requests it. If a pre-commit hook fails, fix the underlying issue and create a new commit.
- When the change is AI-assisted, include a `Co-Authored-By:` trailer naming the model.
- When a new file is created as part of the change, stage it (`git add <path>`) immediately at creation, not deferred to commit time. This keeps `git status` honest throughout the session — pending work shows up as `A`/`M` (intended) rather than `??` (forgettable), and the file can't silently fall out of the final commit. (Generated artifacts under `gen/` or `build/` are gitignored and stay unstaged.)
- Before each commit, run `git status`, `git diff` (unstaged hunks), and `git diff --cached` (staged hunks). Stage only files relevant to the change at hand; never sweep up unrelated edits the user happens to have in the working tree.
- For non-trivial changes, record in the commit body which verifications actually ran (parser/lexer regen, Gradle build, sandbox launch, manual highlighting/completion check) and which were skipped and why. Be precise: `./gradlew build` in this repo is a build/package verification — it counts as a test verification only when Gradle actually discovered and executed tests (and as noted in Testing changes, no `test` sourceSet is currently configured, so `./gradlew test` has nothing to run by default). Don't write "tests passed" unless JUnit tests actually ran. The next reader needs to know what is confirmed vs. assumed.

Use a HEREDOC so newlines and trailers are preserved:

```
git commit -m "$(cat <<'EOF'
Subject line (lsfusion/platform#NNNN)

Body paragraph explaining the why.

Co-Authored-By: <model name> <agent-noreply-email>
EOF
)"
```

## Branches

No maintained version or backport branches; the release line is `master`. Feature branches for work-in-progress are fine and merge back into `master`. Releases are tagged on `master` and published to JetBrains Marketplace via a separate process.

## GitHub issues

GitHub issues are **usually not created** for plugin changes — the plugin's release notes aren't generated from `closes #N` references, and most plugin work either tracks a platform-side issue or is pure IDE tooling. Reference the corresponding platform issue in the commit subject when applicable (e.g. `(lsfusion/platform#NNNN)`); that's enough for traceability.

If a plugin-only issue is genuinely warranted (e.g. a long-standing IDE-tooling bug that needs its own tracking), use the same template as the platform repo:

```
### Title
<short imperative title>

### Description
<observed IDE behavior, minimal .lsf reproducer, expected vs. actual outcome>

### Reason
<motivation: why this matters to plugin users>
```

Frame the Description in terms of what a developer sees in the IDE — wrong highlighting, missing completion, broken Go To Definition, false-positive inspection — and include the smallest `.lsf` snippet that triggers it. Class names, PSI types, and BNF rule names belong in the `### Fix` section (for bug reports) or in the commit body, not in the user-facing Description.

## Code conventions

- PSI mixins/implements for grammar-driven references go under `references/` and `references/impl/`, not `declarations/`. New `formExtendXxx`-style rules in the BNF wire `mixin` to `references.impl.*ReferenceImpl` and `implements` to `references.*Reference`.
- After adding a new BNF rule that needs validation or highlighting, wire it into `LSFReferenceAnnotator` (visitor pattern). Most references plug in via `visitXxxUsage` → `checkReference(o)`.
- Don't add error handling for impossible PSI states. Trust the generated grammar — for elements whose BNF rule guarantees presence, let `null` propagate without defensive checks; for optional elements, check explicitly.
- Minimize call-tree depth and inter-function coupling. A helper called from only one place, doing one obvious thing, is usually clearer inlined; delegation chains where each level just forwards arguments make code harder to follow than one self-contained function. Exception: genuinely primitive or broadly-reused utilities (PSI traversal helpers, well-known infrastructure) — coupling to those is cheap and welcome.
- Don't comment what the code already says. Add a comment only when **why** is non-obvious — an IDE platform quirk, a workaround, a subtle invariant.
- No emojis in source, commits, or comments unless the user explicitly asks.
- Prefer editing existing files to creating new ones.

## Risk discipline

Treat the following as requiring explicit user authorization each time, even if a similar action was authorized earlier in the session:

- `git push` and any remote-affecting operation; force-pushes are never automatic.
- Destructive operations: `git reset --hard`, `git clean -fd`, branch deletion, force pushes, `rm -rf` of working trees or generated directories outside the explicit build outputs (`build/`, `gen/`).
- Anything that publishes outside the local machine: GitHub PR/issue creation, comments, releases, JetBrains Marketplace uploads.
- Bumping `META-INF/plugin.xml` `<version>` — only when explicitly preparing a release.

Local, reversible work — editing files, running tests, regenerating `gen/` via `./gradlew generate*` — doesn't need per-action confirmation.

## Per-agent extension files

- `CLAUDE.md`, `GEMINI.md` at the repo root are thin wrappers that import this file (Claude Code and Gemini CLI don't auto-read `AGENTS.md` out of the box).
- `.github/copilot-instructions.md` — if added, GitHub Copilot surfaces that don't read `AGENTS.md` will pick it up. Keep canonical guidance here.
