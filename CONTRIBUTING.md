# Contributing to Senior Project

Thanks for considering a contribution. This site is a structured field guide
for senior software engineering. Contributions can be:

- Fixes (typos, broken examples, dead links)
- Improvements (clearer explanations, better worked examples)
- New roadmap leaves (10-file structure)
- New Leetcode solutions
- New System Design pages

## Editorial rules

- **Language:** English only. No mixed Uzbek/English content in educational
  pages. (Internal specs and READMEs are flexible; learner-facing content is
  not.)
- **Code:** examples must compile. Pin the language version where it matters.
- **Specificity:** behaviour claims must be tied to a function, type, or
  documented guarantee. No hand-waving.
- **Tone:** direct, technical, worked examples over abstract description.

## Roadmap leaves — 10-file structure

Every leaf folder under `Roadmap/<Domain>/<topic>/` must contain exactly
these files:

| File | Purpose |
|------|---------|
| `index.md` | Topic overview and navigation |
| `junior.md` | Foundations a junior developer needs |
| `middle.md` | Mid-level depth and patterns |
| `senior.md` | Senior-level mastery |
| `professional.md` | Expert-level production knowledge |
| `specification.md` | Reference / spec details |
| `interview.md` | Interview-style questions |
| `tasks.md` | Practical exercises |
| `find-bug.md` | Debugging scenarios |
| `optimize.md` | Performance optimization patterns |

See `TEMPLATE.md` files inside the repo for shape.

## Leetcode solutions

Under `Leetcode/<NNNN>-<slug>/solution.md`. Include:

- Problem statement and constraints
- 2+ approach progressions (brute force → optimized)
- Time and space complexity per approach
- Edge cases and common mistakes
- Code in **Go**, **Java**, and **Python** (all three required)
- Visual animation if helpful

## Local preview

```bash
pip install -r requirements-docs.txt
mkdocs serve
```

Open http://127.0.0.1:8000/.

Before submitting a PR:

```bash
mkdocs build --strict
```

This must pass — `--strict` rejects warnings (unresolved links, missing
files referenced from nav, etc).

## Pull request workflow

1. Fork, branch from `main`.
2. Make your changes; run `mkdocs build --strict` locally.
3. Open a PR. Fill out the template — particularly the editorial checklist.
4. Maintainers review and merge.

## Questions?

Use **Discussions** for general questions. Use **Issues** for concrete bugs
or content corrections (templates are provided).

- Discussions: https://github.com/BakhodiribnYashinibnMansur/SeniorProject/discussions
- Issues: https://github.com/BakhodiribnYashinibnMansur/SeniorProject/issues
