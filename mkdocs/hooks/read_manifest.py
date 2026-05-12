"""MkDocs hook that builds the read-progress section manifest.

Walks the build's documentation files, groups each page by its top-level
section, and attaches the result to ``config['extra']['read_manifest']``.
The theme template inlines the resulting dict as ``window.SP_READ_MANIFEST``,
so the client-side reading-comfort pack can compute per-section read counts
without a separate fetch.

Section keys:
  * Pages under ``Roadmap/<X>/...`` group by ``<X>`` (AI, Backend, Data, ...).
  * Pages under ``Leetcode/...`` group as ``Leetcode``.
  * Pages under ``System Design/...`` group as ``System Design``.
  * Everything else (root landing pages, assets) is skipped.
"""

from __future__ import annotations

from datetime import datetime, timezone

TOP_LEVEL_SECTIONS = {"Leetcode", "System Design"}
ROADMAP_TOP = "Roadmap"
SKIP_LEAF_FILES = {"index.md", "TEMPLATE.md"}


def _section_key(src_uri: str) -> str | None:
    parts = src_uri.split("/")
    if not parts:
        return None
    top = parts[0]
    if top == "assets":
        return None
    if len(parts) == 1:
        return None
    if parts[-1] in SKIP_LEAF_FILES:
        return None
    if top == ROADMAP_TOP:
        return parts[1] if len(parts) >= 3 else None
    if top in TOP_LEVEL_SECTIONS:
        return top
    return None


def _normalised_url(page_url: str) -> str:
    u = page_url if page_url.startswith("/") else "/" + page_url
    if not u.endswith("/"):
        u += "/"
    while "//" in u:
        u = u.replace("//", "/")
    return u


def _label_for(key: str) -> str:
    return key.replace("-", " ")


def on_files(files, config):
    sections: dict[str, list[str]] = {}
    for f in files:
        if not f.is_documentation_page():
            continue
        key = _section_key(f.src_uri)
        if key is None:
            continue
        sections.setdefault(key, []).append(_normalised_url(f.url))

    manifest_sections = []
    for key in sorted(sections):
        manifest_sections.append({
            "key": key,
            "label": _label_for(key),
            "paths": sorted(sections[key]),
        })

    manifest = {
        "generated": datetime.now(timezone.utc).replace(microsecond=0).isoformat(),
        "sections": manifest_sections,
    }

    extra = config.setdefault("extra", {})
    extra["read_manifest"] = manifest
    return files
