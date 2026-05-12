/* ================================================================
   Senior Stack — e-ink toggle (manual only)
   Activation paths:
     1. Floating "eink mode" button (bottom-right)
     2. Keyboard shortcut: Shift+E
     3. URL param: ?eink=1 / ?eink=0  → force, persisted to storage
   Persisted in localStorage; defaults to OFF until the user opts in.
   Survives Material's instant navigation via document$ subscription.
   ================================================================ */
(function () {
  "use strict";

  const STORAGE_KEY = "sp-eink-mode";

  // ---------- Mode resolution ------------------------------------
  function readStoredMode() {
    try {
      const v = localStorage.getItem(STORAGE_KEY);
      if (v === "on" || v === "off") return v;
    } catch (e) {}
    return "off";
  }

  function writeStoredMode(mode) {
    try { localStorage.setItem(STORAGE_KEY, mode); } catch (e) {}
  }

  function readUrlOverride() {
    try {
      const p = new URLSearchParams(window.location.search);
      const v = p.get("eink");
      if (v === "1" || v === "on" || v === "true") return "on";
      if (v === "0" || v === "off" || v === "false") return "off";
    } catch (e) {}
    return null;
  }

  // ---------- Apply / remove ------------------------------------
  function setEink(on) {
    const html = document.documentElement;
    if (on) {
      html.setAttribute("data-eink", "1");
    } else {
      html.removeAttribute("data-eink");
    }
    updateToggleLabel();
  }

  function isEinkOn() {
    return document.documentElement.getAttribute("data-eink") === "1";
  }

  // ---------- Toggle button --------------------------------------
  function ensureToggle() {
    let btn = document.querySelector(".sp-eink-toggle");
    if (btn) return btn;
    btn = document.createElement("button");
    btn.type = "button";
    btn.className = "sp-eink-toggle";
    btn.setAttribute("aria-label", "Toggle e-ink reading mode");
    btn.title = "Toggle e-ink reading mode (Shift+E)";
    btn.addEventListener("click", function () {
      const next = isEinkOn() ? "off" : "on";
      writeStoredMode(next);
      setEink(next === "on");
    });
    document.body.appendChild(btn);
    return btn;
  }

  function updateToggleLabel() {
    const btn = document.querySelector(".sp-eink-toggle");
    if (!btn) return;
    btn.textContent = isEinkOn() ? "eink mode: on" : "eink mode";
  }

  // ---------- Init flow -----------------------------------------
  function init() {
    if (!document.body) return; // wait for body
    ensureToggle();
    // URL param overrides storage and is persisted
    const url = readUrlOverride();
    if (url) writeStoredMode(url);
    const mode = url || readStoredMode();
    setEink(mode === "on");
  }

  // Keyboard shortcut: Shift+E
  document.addEventListener("keydown", function (e) {
    if (e.shiftKey && (e.key === "E" || e.key === "e") &&
        !e.ctrlKey && !e.metaKey && !e.altKey) {
      const tag = (e.target && e.target.tagName) || "";
      if (/^(INPUT|TEXTAREA|SELECT)$/.test(tag)) return;
      const next = isEinkOn() ? "off" : "on";
      writeStoredMode(next);
      setEink(next === "on");
      e.preventDefault();
    }
  });

  // Material instant navigation: re-run on each page swap if possible
  if (typeof window.document$ !== "undefined" && window.document$.subscribe) {
    window.document$.subscribe(function () { init(); });
  } else if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
  } else {
    init();
  }
})();
