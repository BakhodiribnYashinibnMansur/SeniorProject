# Pre-Commit Checklist

Status: ⏳ PENDING

A short list to walk through **before staging a commit** — distilled from all chapters. Designed to be runnable in a few minutes, not a full review.

## Names ([Chapter 01](../01-meaningful-names/junior.md))
- [ ] Every new identifier reveals intent without needing a comment
- [ ] No single-letter names except loop counters and very short scopes
- [ ] Booleans named `is*`, `has*`, or `can*`
- [ ] No `Manager`, `Processor`, `Helper`, `Data`, `Info` noise words

## Functions ([Chapter 02](../02-functions/junior.md))
- [ ] No function over ~20 lines (or has a clear reason for being longer)
- [ ] Each function does one thing at one level of abstraction
- [ ] No more than 3 parameters (otherwise group into a struct/object)
- [ ] No flag arguments
- [ ] No mutated output arguments

## Comments ([Chapter 03](../03-comments/junior.md))
- [ ] No commented-out code
- [ ] Every remaining comment explains *why*, not *what*
- [ ] No journal/attribution/closing-brace comments

## Errors ([Chapter 06](../06-error-handling/junior.md))
- [ ] No empty catch blocks
- [ ] No returned `null` (use Optional/Result/empty collection)
- [ ] All third-party errors wrapped at the boundary with context

## Tests ([Chapter 08](../08-unit-tests/junior.md))
- [ ] New behaviour has a test
- [ ] Tests are fast (no network/disk in the unit suite)
- [ ] Tests pass in isolation and in any order
- [ ] No `console.log` / `fmt.Println` / `print` left in production code

## General
- [ ] No dead code
- [ ] Formatter run (`gofmt`, `black`, `prettier`, etc.)
- [ ] Linter passing
- [ ] No commented-out imports
- [ ] No secrets, API keys, or credentials in the diff

## Commit Message
- [ ] Subject line ≤ 70 chars, imperative mood
- [ ] Body explains *why*, not *what* (the diff already shows *what*)
- [ ] No "fix stuff" / "wip" / "asdf" left over

See [PR review checklist](pr-review.md) for the next layer beyond this.
