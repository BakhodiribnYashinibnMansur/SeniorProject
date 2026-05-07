# TEMPLATE — English Grammar Roadmap

> The contract for every page in this roadmap. If you (or a script) generates a file under `Roadmap/Language/English/`, it must conform to one of the templates below.

This file pins down four things:

1. The shape of an **entry** (the smallest unit — one EGP grammar point).
2. The shape of a **sub-topic file** (e.g. `01-tenses/01-present-simple.md`).
3. The shape of a **chapter `README.md`** (e.g. `01-tenses/README.md`).
4. The shape of a **`find-errors.md`** file (one per chapter) and a **`levels/<LEVEL>.md`** file (the inverse view).

A list of **writing rules** at the top governs all of them.

---

## Writing rules — non-negotiable

These apply to every entry, every README, every section.

### R1 · Title

- Every entry has a short, plain-English title that names the structure.
- Format: `### {LEVEL}.{i} — {Title in Sentence case}`.
  - `{LEVEL}` is the CEFR level (`A1`, `A2`, `B1`, `B2`, `C1`, `C2`).
  - `{i}` is a 1-based counter inside that level inside the same sub-topic file.
  - The title comes from the EGP `Title` field but **rewritten in Sentence case**, not SHOUTING. EGP says `FORM/USE: 'THE BEST' WITH NOUN AND PRESENT PERFECT`; we write *“`The best` with a noun and the present perfect”*.
- The title must let a reader scanning the page guess what the entry is about without reading further.
- Example: `### A1.2 — Present-simple affirmative form with common verbs` inside `01-tenses/01-present-simple.md`.

### R2 · Description — at least 5 sentences

- Every entry has a **Description** block. The description is **at least five complete sentences**, in this order:

  1. **What the structure is** — name the form and where it sits in a sentence.
  2. **What it means** — what the structure does to the meaning of the sentence.
  3. **When to use it** — the situation, register, or speaker intention that triggers this structure.
  4. **What contrasts with it** — the next-door structure(s) a learner might confuse it with, and the difference in one line.
  5. **A pitfall or note** — the most common learner mistake, a register caveat, or a BrE/AmE contrast.

- More sentences are welcome at C1/C2; five is a floor, not a ceiling.
- One sentence per idea. Do not stack three ideas into one comma-spliced sentence.

### R3 · Plain language

- Every grammatical term is **defined or shown** the first time it appears on the page. *Auxiliary*, *finite*, *perfective*, *cataphoric* are all fine — but introduce them: *“an auxiliary verb (`do`, `have`, `be`)”*.
- Prefer everyday words. Write *“the verb after `to`”*, not *“the post-`to` lexical verb”*, unless the technical term genuinely earns its place.
- No more than **25 words per sentence** on average. Break long sentences.

### R4 · Examples are mandatory and labelled

- Every entry has at least **two correct examples** and, where the EGP Incorrect field provides them or research warrants, at least **one common-mistake example** with a one-line explanation of *why* it is wrong.
- Examples are real sentences, not isolated fragments. Capitalise and punctuate them.
- Mark register where it matters: `🇬🇧 BrE`, `🇺🇸 AmE`, `(formal)`, `(informal)`, `(spoken)`, `(written)`.

### R5 · Sources

- Every entry ends with a `**Sources:**` line listing **at least two** references — one of which is the EGP entry itself, and at least one of which is a Tier-1 source (Cambridge Dictionary Grammar, Swan, Murphy, Hewings, Oxford Learner's). See [`README.md`](README.md#sources--citation-policy).
- Cite specific URLs, page numbers, or unit numbers when possible. *“Cambridge Grammar”* alone is too vague — *“Cambridge Dictionary Grammar — `can`: ability”* (with URL) is right.

### R6 · Cross-references

- Every entry has a `**Related:**` line linking to neighbouring entries the learner is likely to compare with.
  - **Inside the same sub-topic file**: use anchor links — `[A2.4 — Indirect questions](#a2-4--indirect-questions-with-do-you-know)`.
  - **To a different sub-topic file**: use a relative path — `[6.2 Adverbs of frequency](../06-adverbs/02-types-and-meanings.md)`.
- If there is no clear related entry, write `**Related:** —`.

### R7 · One voice

- Second person, present tense, active voice. *“We use the present continuous to talk about an action happening now.”*
- Avoid hedging filler (*basically*, *kind of*, *generally speaking*). Be definite; if there's a real exception, name it.
- All content in English. No mixed-language explanations.

---

## 1 · Entry block

Every grammar point — at every level, in every sub-topic file — uses **exactly this** shape:

````markdown
### A1.1 — Present-simple affirmative form with common verbs

> **Can-do (CEFR A1):** Can use the affirmative form with a limited range of regular and irregular verbs. *(EGP, super-category PRESENT.)*

**Description**

The affirmative present simple is the verb in its base form, with `-s` added when the subject is `he`, `she`, or `it` — *I work · she works · they work*. It describes things that are true now and stay true: who you are, where you live, what you do, what you like. We use it for general facts and personal information — the kind of sentences that begin a self-introduction or describe an object. It contrasts with the present continuous (`am/is/are + -ing`), which describes an action happening **right at this moment**: *I work in a hospital* (general fact) vs. *I am working today* (right now). The single biggest A1 mistake is forgetting the third-person `-s` (*she play*, *he live*) — the `-s` is required even though it sounds redundant.

**Form**

- `subject + base verb` — *I learn · we learn · they learn*.
- For `he / she / it`, add `-s` to the base verb — *he learns · she lives · it works*.
- Spelling: verbs ending in `-y` after a consonant change to `-ies` (*study → studies*); verbs ending in `-s, -sh, -ch, -x, -o` add `-es` (*go → goes · watch → watches*).
- The verb `be` is irregular: `I am · you are · he/she/it is · we/you/they are`.

**Examples**

✅ Correct
1. Every day at college I **learn** new words.
2. She **plays** tennis and she **likes** going to the swimming pool.

❌ Common mistakes
1. *She play tennis.* → missing third-person `-s`; *She **plays** tennis*.
2. *He have a brother.* → wrong form for `he`; *He **has** a brother*.

**Related:** [A1.2 — Negative form](#a1-2--present-simple-negative-form-with-dont--doesnt) · [A1.3 — Habits and general facts](#a1-3--present-simple-for-habits-and-general-facts)

**Sources:** EGP entry *PRESENT · present simple · A1 · FORM: AFFIRMATIVE* · [Cambridge Dictionary Grammar — *Present simple*](https://dictionary.cambridge.org/grammar/british-grammar/present-simple-i-work) · Murphy, *Essential Grammar in Use* (4th ed.) Units 5–7.
````

### Notes on the entry block

- The `> **Can-do (CEFR …):**` blockquote contains the **EGP definition verbatim**. If the EGP wording is awkward, fix it in the Description, not in the can-do.
- **Description first, Form second.** A reader who does not yet know the form should still understand what the rule is about from the Description alone.
- The Description satisfies R2 (≥ 5 sentences). The Form section is bullet points; it does not count toward the sentence floor.
- The numbering uses the **CEFR level as the prefix** (`A1.1`, `A1.2`, …, `B2.3`) so an entry is self-locating inside its sub-topic file — no need to read the can-do to know the level.

---

## 2 · Sub-topic file

Path example: `Roadmap/Language/English/01-tenses/01-present-simple.md`.

A sub-topic file holds **all** EGP entries for one Type sub-topic across **all CEFR levels**. The whole topic — beginner to advanced — sits in one page.

````markdown
# 1.1 · Present simple

> **24 EGP entries · A1: 4 · A2: 9 · B1: 5 · B2: 3 · C1: 3 · C2: —**

[← back to chapter 1 · Tenses](README.md) · sibling sub-topics: [1.2 Present continuous](02-present-continuous.md) · [1.3 Past simple](03-past-simple.md)

*One short paragraph (2–3 sentences) introducing the sub-topic: what it is, why it matters, what the levels add.*

---

## A1 — 4 entries

*One short paragraph framing the level: what is new, what learners must master before moving on.*

### A1.1 — {Title in Sentence case}
*(full entry block — see template above)*

### A1.2 — {Title}
*(full entry block)*

…

---

## A2 — 9 entries

*level intro paragraph*

### A2.1 — {Title}
*(full entry block)*

…

---

## B1 — 5 entries
…

## B2 — 3 entries
…

## C1 — 3 entries
…

## C2 — no entries

EGP records no entries for this sub-topic at C2.

---

[← back to chapter 1 · Tenses](README.md) · next sub-topic: [1.2 Present continuous](02-present-continuous.md)
````

### Notes on sub-topic files

- File name: `<NN>-<kebab-case-sub-topic>.md` — the `NN` matches the sub-topic number from the chapter. So sub-topic 1.5 *Present perfect simple* lives at `01-tenses/05-present-perfect-simple.md`.
- The page is one self-contained learning unit; reading top to bottom is the recommended path.
- A level section is omitted if EGP has no entries at that level for this sub-topic — but the omission is acknowledged with a *“no entries”* note (as in the C2 example above) so the reader is never left wondering.
- The footer offers two onward paths: back to the chapter, and forward to the next sub-topic.

---

## 3 · Chapter `README.md`

Path example: `Roadmap/Language/English/01-tenses/README.md`.

````markdown
# 1 · Tenses

> **203 EGP entries · 17 sub-topics · A1: 22 · A2: 60 · B1: 47 · B2: 29 · C1: 22 · C2: 23**

## What this chapter covers

Tenses are how English marks **time** and **aspect** on a verb. This chapter merges three EGP SuperCategories — PRESENT, PAST, FUTURE — into a single learning thread, because that is how grammar books and learners actually treat them. By the end of this chapter you should be able to choose the right tense for any time reference (now, recently, before then, later) and any aspect (a habit, an ongoing action, a completed action, a sequence).

The seven core tenses worth mastering first, in this order: **present simple → present continuous → past simple → past continuous → present perfect simple → future with `will` → future with `be going to`**. Together they handle ~85 % of everyday English.

## Sub-topics

| # | Sub-topic | Total | A1 | A2 | B1 | B2 | C1 | C2 | File |
|:---:|:---|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---|
| 1.1 | Present simple | 24 | 4 | 9 | 5 | 3 | 3 | — | [`01-present-simple.md`](01-present-simple.md) |
| 1.2 | Present continuous | 14 | 2 | 5 | 3 | 1 | 1 | 2 | `02-present-continuous.md` *(coming soon)* |
| 1.3 | Past simple | 24 | 2 | 4 | 7 | 6 | 5 | — | `03-past-simple.md` *(coming soon)* |
| … | … | … | … | … | … | … | … | … | … |

## Tense matrix

| | Simple | Continuous | Perfect simple | Perfect continuous |
|---|---|---|---|---|
| **Present** | I work | I am working | I have worked | I have been working |
| **Past** | I worked | I was working | I had worked | I had been working |
| **Future** | I will work | I will be working | I will have worked | I will have been working |

## How tense interacts with time

*(Short prose: the difference between tense, aspect, and time. Two paragraphs maximum.)*

## Supplementary (beyond EGP)

- **Stative vs. dynamic verbs.** EGP does not list this contrast directly, but it controls when the continuous tenses are usable (*I love it*, not *I am loving it*). [BBC Learning English — Stative verbs](URL).
- **Sequence of tenses in narrative writing.** *(brief notes + sources)*
- *(other supplementary items, each with sources)*

## See also

- [`find-errors.md`](find-errors.md) — error-spotting exercises drawn from every level of this chapter.
- [`../cheatsheet.md`](../cheatsheet.md#tenses) — the tense matrix in one page.
- [`../levels/A1.md`](../levels/A1.md#1-tenses) — only the A1 entries from this chapter, beside other A1 chapters.
````

### Notes on the chapter README

- It is the **map** of the chapter, not a substitute for the sub-topic files. Keep prose short; let the table do the work.
- The sub-topic table is the canonical list — a reader should pick a sub-topic file from this table.
- The `Supplementary (beyond EGP)` section is the place for everything authoritative sources teach but EGP does not (punctuation rules, register, BrE/AmE contrasts, emerging usage).

---

## 4 · `find-errors.md`

One per chapter. Every Incorrect example from the chapter's apkg entries (across **all** sub-topics and **all** levels in this chapter) becomes a numbered exercise. The answer key is collapsed by default so the page is usable as a worksheet.

````markdown
# 1 · Tenses — Find the errors

Every numbered sentence below has **exactly one grammatical error** drawn from real learner writing (Cambridge Learner Corpus, via EGP). Identify the error, fix it, then expand the answer key to check.

## A1 — 6 exercises

1. *He live in London with his family.*
2. *She don't like fish.*
3. *They is happy today.*
4. … *(one numbered line per Incorrect example at this level in this chapter)*

<details>
<summary>Show A1 answer key</summary>

1. *He **lives** in London with his family.* — third-person singular `-s` is required (entry [A1.1](01-present-simple.md#a1-1)).
2. *She **doesn't** like fish.* — auxiliary for third-person singular is `does` (entry [A1.2](01-present-simple.md#a1-2)).
3. *They **are** happy today.* — `they` takes `are`, not `is` (sub-topic 1.2 *(coming soon)*).

</details>

## A2 — 14 exercises
*(same shape)*

## B1 — 17 exercises
*(same shape)*

*(Continue for B2, C1, C2 if entries exist.)*
````

### Notes on `find-errors.md`

- Group exercises by CEFR level so a learner can stop at their level.
- Every answer-key item links back to the entry that explains the rule, so wrong answers are self-correcting.
- Pull from every sub-topic in the chapter — `find-errors.md` is **chapter-wide**, not per-sub-topic.
- If the apkg has no Incorrect examples at a given level for this chapter, omit that level's section.

---

## 5 · `levels/<LEVEL>.md`

The inverse view. One file per CEFR level lists every entry at that level grouped by chapter and sub-topic — useful for exam preparation.

````markdown
# A1 — every entry at this level

> **109 EGP entries** at A1, across 17 chapters.

## 1 · Tenses — 22 entries

### 1.1 Present simple — 4 entries
- [A1.1 — Present simple with `really` as an intensifier](../01-tenses/01-present-simple.md#a1-1)
- [A1.2 — Present-simple affirmative form](../01-tenses/01-present-simple.md#a1-2)
- [A1.3 — Present-simple negative form](../01-tenses/01-present-simple.md#a1-3)
- [A1.4 — Present simple for habits and general facts](../01-tenses/01-present-simple.md#a1-4)

### 1.9 Future with `will` — 2 entries
- … *(every A1 entry in sub-topic 1.9, in numbered order)*

*(continue through every sub-topic of chapter 1 that has A1 entries; then move on to chapter 2 …)*

## 2 · Nouns — 13 entries
*(same shape)*

*(Continue through all 17 chapters; skip a chapter if it has no A1 entries.)*

---

**Next level:** [A2](A2.md)
````

### Notes on the level index

- Order matches the 17-chapter pedagogical order, not EGP frequency.
- Inside each chapter, sub-topics appear in their numbered order (1.1, 1.2, …).
- Entry titles match the sub-topic file's `###` headings exactly so a search reaches both views.
- Anchors use kebab-case (`#a1-1`, not `#A1.1`) — that is how Material for MkDocs renders them.

---

## Quick checklist for any new entry

Before committing, run through this in order:

- [ ] Title is in Sentence case and names the structure plainly.
- [ ] Heading uses the `LEVEL.i` numbering (e.g. `### A1.1`, `### B2.3`).
- [ ] Can-do block contains the EGP definition verbatim.
- [ ] Description has **≥ 5 sentences**, in the order: what · meaning · when · contrast · pitfall.
- [ ] Every grammatical term used is defined or shown the first time it appears.
- [ ] Form bullets cover affirmative, negative, question (where applicable) and any spelling rules.
- [ ] At least 2 Correct examples, ≥ 1 Common-mistake example with a one-line *why*.
- [ ] Register tags applied where they matter (`🇬🇧 BrE`, `🇺🇸 AmE`, *formal*, *informal*).
- [ ] `**Related:**` line filled in (or `—` if none).
- [ ] `**Sources:**` line lists ≥ 2 references, including EGP and ≥ 1 Tier-1.

If any box is unchecked, the entry is not done.
