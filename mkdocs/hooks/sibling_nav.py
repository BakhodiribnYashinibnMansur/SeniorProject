"""MkDocs hook that injects sibling-page navigation metadata onto pages.

Attaches a ``sibling_nav`` entry to ``page.meta`` for any page whose filename
stem is one of the recognised level or mode names. The matching theme
override (``overrides/partials/toc.html``) reads this metadata and renders
a navigation panel above the table of contents.
"""

from __future__ import annotations

from posixpath import dirname, splitext

LEVELS = ("junior", "middle", "senior", "professional")
MODES = ("specification", "interview", "tasks", "find-bug", "optimize")
TRACKED = frozenset(LEVELS + MODES)


def _stem(src_uri: str) -> str:
    return splitext(src_uri.rsplit("/", 1)[-1])[0]


def _siblings_in(directory: str, files):
    found = {}
    for file in files:
        if not file.is_documentation_page():
            continue
        if dirname(file.src_uri) != directory:
            continue
        stem = _stem(file.src_uri)
        if stem in TRACKED:
            found[stem] = file
    return found


def _group(order, found, current_stem):
    items = []
    for name in order:
        file = found.get(name)
        if file is None:
            continue
        items.append(
            {
                "name": name,
                "url": file.url,
                "current": name == current_stem,
            }
        )
    return items


def on_page_markdown(markdown, page, config, files):
    src_uri = page.file.src_uri
    stem = _stem(src_uri)
    if stem not in TRACKED:
        return markdown

    directory = dirname(src_uri)
    found = _siblings_in(directory, files)
    if len(found) < 2:
        return markdown

    levels = _group(LEVELS, found, stem)
    modes = _group(MODES, found, stem)

    page.meta["sibling_nav"] = {"levels": levels, "modes": modes}
    return markdown
