# Pull Request Review Checklist

Status: ⏳ PENDING

What to look for as a **reviewer**. Goes beyond the [pre-commit checklist](pre-commit.md) — focuses on design and cross-cutting concerns rather than line-level cleanliness (which the author has already passed).

## Design ([Chapter 09](../09-classes/README.md), [Chapter 10](../10-emergence/README.md))
- [ ] Single Responsibility — each class/module has one reason to change
- [ ] No new God objects introduced
- [ ] No speculative generality — abstractions earn their existence with two real consumers
- [ ] Public interfaces minimal — anything not needed by callers is private

## Boundaries ([Chapter 07](../07-boundaries/README.md))
- [ ] Third-party types don't leak across module/package boundaries
- [ ] New dependency has a learning test
- [ ] SDK or library wrapped in a thin layer the rest of the codebase calls

## Cohesion & Coupling
- [ ] Related code is close; unrelated code is separate
- [ ] No circular dependencies introduced
- [ ] Module/package dependencies still flow in one direction

## Tests ([Chapter 08](../08-unit-tests/README.md))
- [ ] Tests cover the new **behaviour**, not just the new lines
- [ ] Failure modes considered, not only happy paths
- [ ] Test names readable as English sentences
- [ ] Mocks are used only for collaborators the team owns

## Errors & Edge Cases ([Chapter 06](../06-error-handling/README.md))
- [ ] Nil / empty / boundary inputs handled at the right layer
- [ ] No new silent failures
- [ ] Errors carry enough context for triage (chained / wrapped)

## Concurrency ([Chapter 11](../11-concurrency/README.md))
- [ ] No new shared mutable state without locks
- [ ] Race conditions considered for any new goroutine/thread/async
- [ ] Cancellation / shutdown path handled

## Performance
- [ ] No O(n²) hidden inside "clean" abstractions on a hot path
- [ ] No new allocations in hot paths without justification
- [ ] No N+1 queries introduced

## Documentation
- [ ] Public APIs documented
- [ ] CHANGELOG / migration notes if a public API changed
- [ ] README updated if developer setup changed

## Security
- [ ] No secrets in code, config, or commit messages
- [ ] User input validated at the trust boundary
- [ ] No new SQL/HTML/shell concatenation that could lead to injection

## Reviewer Discipline
- [ ] Comments explain *why* a change is needed, with a suggested alternative
- [ ] Distinguish blocking issues from preferences ("nit:" prefix)
- [ ] Approve or block clearly — no ambiguous reviews
