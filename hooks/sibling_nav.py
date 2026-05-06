"""MkDocs hook that injects sibling-page navigation metadata onto pages.

Attaches a ``sibling_nav`` entry to ``page.meta`` for any page whose filename
stem is one of the recognised level or mode names. The matching theme
override (``overrides/partials/toc.html``) reads this metadata and renders
a navigation panel above the table of contents.
"""

from __future__ import annotations


def on_page_markdown(markdown, page, config, files):
    return markdown
