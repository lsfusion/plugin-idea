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

Toolchain: Java 21 JDK (required by IntelliJ Platform 2025.3). Source and bytecode level are pinned to `javaVersion` in `build.gradle.kts` (currently 21) — check that file rather than hard-coding here.

After editing `LSF.bnf` or `LSF.flex`, regenerate before the next build or sandbox launch so `gen/` matches the sources. `gen/` is gitignored — don't stage it.

### Testing changes

`./gradlew runIde` launches an IntelliJ sandbox with the plugin loaded — open or create a `.lsf` file to exercise highlighting, references, completion, annotations. Sandbox state lives under `build/idea-sandbox/`; deleting it forces a clean re-init.

No headless test rig — UI verification is interactive in the sandbox. No `test` sourceSet is currently configured either: `main` maps everything under `src/` (and `gen/`) as production code, so a file at `src/test/...` would ship in the plugin artifact. To add unit tests (PSI walkers, reference resolution helpers), first wire a dedicated `test` sourceSet pointing at a directory that doesn't overlap with `src/` (e.g. a sibling `tests/`), then run `./gradlew test`.

## Commits

- Short imperative subject, ≤72 chars. Suffix `(closes #NN)` for plugin issues, or `(lsfusion/platform#NNNN)` when cross-linking to a platform issue that drove the change.
- Body explains **why**. The diff shows what.
- Never amend or force-push commits that may have been pulled by others. Always create a new commit on top.
- Never pass `--no-verify` unless the user explicitly requests it. If a pre-commit hook fails, fix the underlying issue and create a new commit.
- When the change is AI-assisted, include a `Co-Authored-By:` trailer naming the model.
- Stage new files (`git add <path>`) at creation, not at commit time. Pending work then shows as `A`/`M` in `git status` instead of `??`, and can't silently fall out of the commit. (`gen/` and `build/` are gitignored and stay unstaged.)
- Before each commit, run `git status`, `git diff`, and `git diff --cached`. Stage only files relevant to the change; never sweep up unrelated working-tree edits.
- For non-trivial changes, record in the commit body which verifications ran (parser/lexer regen, Gradle build, sandbox launch, manual highlighting/completion check) and which were skipped and why. Be precise: `./gradlew build` is a build/package verification — it becomes a test verification only if Gradle actually discovered and ran tests (and no `test` sourceSet is currently wired, so `./gradlew test` has nothing to run by default). Don't write "tests passed" unless JUnit tests actually ran.

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

No maintained version or backport branches; the release line is `master`. Feature branches for work-in-progress are fine and merge back into `master`.

## Releases

The plugin has two distribution channels, and they ship out of band:

1. **`lsfusion.org` download / IDE auto-update channel.** A Jenkins pipeline publishes here automatically on every push to `master` that bumps `<version>`. This is what `lsfusion.org` users get and what `IDE → Settings → Plugins → Manage Plugin Repositories → https://www.lsfusion.org/...` pulls.
2. **JetBrains Marketplace** ([plugin/7601](https://plugins.jetbrains.com/plugin/7601)). Upload here is **manual** — the JetBrains API `curl` is checked-in but currently commented out in `buildAndUploadPluginManually`, so Marketplace lags behind the lsfusion.org channel until someone runs it by hand.

The lsfusion.org auto-publish is the one a version bump triggers. Pushing a `<version>` bump *is* a release for that channel; the Marketplace listing is a follow-up.

### Pipeline mechanics (lsfusion.org channel)

- GitHub push webhook → Jenkins job `buildAndUploadPluginTrigger` (freestyle, watches any branch) → triggers the `buildAndUploadPlugin` workflow.
- The workflow reads `<version>` from `META-INF/plugin.xml`, and `curl`s `https://plugins.jetbrains.com/api/plugins/7601/updates` purely to learn what the *Marketplace* lists as the most-recently-published version. (The Marketplace listing is treated as the canonical "what's currently published anywhere" baseline.)
- If the two match, the pipeline logs `Version <X.Y.Z> matches the latest version in Marketplace. Skipping build.` and exits successfully — pushes that don't bump are no-ops.
- If they differ, the pipeline builds the plugin and uploads the artifact (with its embedded `<change-notes>` HTML) over FTP to `lsfusion.org`'s `exe/ext/lsfusion-idea-plugin.zip` — the file the IDE's update-checker downloads.

Slack #jenkins gets a colored notification (green / red) on completion.

### Bumping the version

1. Open `META-INF/plugin.xml` and patch-bump `<version>X.Y.Z</version>` to the value that already appears in the `<b>Version X.Y.Z</b>` header inside the current `<change-notes>` block — those two MUST agree at upload time so the bundled release notes match the version users see.
2. Verify every user-visible change since the last release has a `<li>` bullet in the block (see *Release notes* below). Add any that are missing.
3. Commit. Subject like `Release X.Y.Z` is fine; the body should list what's shipping, mirroring the change-notes bullets. Don't squash this with unrelated work — the release commit should diff cleanly to `plugin.xml` (and only `plugin.xml` unless the rollout literally needs another file).
4. Push to `master`. Jenkins picks up the webhook, sees the version diff against Marketplace, builds, and FTP-publishes the new artifact for the lsfusion.org channel.
5. If you also want the Marketplace listing updated — and not just the lsfusion.org channel — run the JetBrains upload separately (the `buildAndUploadPluginManually` script has the commented-out `curl -F pluginId=7601 -F file=@lsfusion-idea-plugin.zip https://plugins.jetbrains.com/plugin/uploadPlugin` recipe). This is a deliberate two-step so the Marketplace listing can be vetted before exposing it to JetBrains' broader audience.

The change-notes block is intentionally **not** reset by the release commit — it keeps showing what shipped in `X.Y.Z` until the next contributor with a user-visible change does the rollover (see below).

### Release notes (`<change-notes>`)

The CDATA block in `META-INF/plugin.xml` is the **only** source of release notes; both the lsfusion.org channel and (when uploaded) the Marketplace render it verbatim, and there is no parallel CHANGELOG.md. The block is HTML — basic tags only (`<b>`, `<br>`, `<ul>`, `<li>`, `<code>`).

**What to write.** One terse `<li>` bullet per user-visible improvement, in past-or-imperative tense, framed as what the developer-of-lsFusion sees in the IDE — not as what the plugin internally does. Mention concrete syntax (`<code>NEWEXECUTOR ... CLIENT conn</code>`), inspection names, settings paths. Skip plugin-internal mechanics (BNF rule names, PSI class names, mixin wiring) — those belong in the commit body, not in the marketplace listing.

**When to write.** Append a bullet to the change-notes block in the **same commit** that introduces the user-visible change.

If the `<b>Version X.Y.Z</b>` header in the block still matches the currently-published `<version>` (i.e. you are the first contributor with a user-visible change after a release), do the rollover in your commit:

1. Bump the header to the next patch version: `<b>Version X.Y.Z+1</b>`.
2. Empty the `<ul>` list of the previous release's bullets.
3. Add your new `<li>` as the first entry.

If the header is already ahead of `<version>` (a previous contributor has already rolled over), just append your `<li>` to the existing list.

This way release notes are maintained lazily — no dedicated cleanup commit, and at any point in time the block accurately reflects "what's queued for the next release."

Examples of changes that warrant an entry:

- New / changed syntax support in the grammar (parser, lexer, annotator).
- New inspection, intention, completion behavior, or gutter icon.
- A deprecation warning the developer will start seeing.
- Changes to the MCP / AI-assistant tool surface the plugin exposes.
- Behavior changes in Go To Definition, Find Usages, Refactor, or any IDE action.

Examples of changes that do **not** warrant an entry:

- Internal refactors with no observable effect.
- Build / Gradle / CI tweaks.
- Regenerated `gen/` after a `.bnf` / `.flex` edit when the user-visible effect is already covered by another bullet for that edit.
- Test infrastructure.

If a release accidentally landed with a bullet that turned out to be wrong, fix it in a follow-up `<version>` bump rather than amending the published one — both channels cache the published notes externally.

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
- Anything that publishes outside the local machine: GitHub PR/issue creation, comments, releases, JetBrains Marketplace uploads (the latter is a deliberate manual step; see *Releases*).
- Bumping `META-INF/plugin.xml` `<version>` — only when explicitly preparing a release (see *Releases* section: the bump auto-publishes to the lsfusion.org channel).

Local, reversible work — editing files, running tests, regenerating `gen/` via `./gradlew generate*` — doesn't need per-action confirmation.

## Per-agent extension files

- `CLAUDE.md`, `GEMINI.md` at the repo root are thin wrappers that import this file (Claude Code and Gemini CLI don't auto-read `AGENTS.md` out of the box).
- `.github/copilot-instructions.md` — if added, GitHub Copilot surfaces that don't read `AGENTS.md` will pick it up. Keep canonical guidance here.
