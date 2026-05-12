/* ================================================================
   Senior Stack — reader font-size control (A− / A / A+)
   - Scales the root font-size; Material is rem-based so everything
     (sidebar, content, code, headings) scales together.
   - 5 discrete steps: 0.85, 1.0, 1.15, 1.3, 1.5
   - Persisted in localStorage as sp-font-scale (numeric string).
   - Keyboard: Ctrl/Cmd + Shift + (=/+) / (-) / (0)
   - Re-renders on Material instant nav.
   ================================================================ */
(function () {
  "use strict";

  const STORAGE_KEY = "sp-font-scale";
  const SCALES = [0.85, 1.0, 1.15, 1.3, 1.5];
  const DEFAULT_SCALE = 1.0;
  const BASE_FONT_PX = 16;

  function readScale() {
    try {
      const v = parseFloat(localStorage.getItem(STORAGE_KEY));
      if (!isNaN(v) && SCALES.includes(v)) return v;
    } catch (e) {}
    return DEFAULT_SCALE;
  }

  function writeScale(scale) {
    try { localStorage.setItem(STORAGE_KEY, String(scale)); } catch (e) {}
  }

  function applyScale(scale) {
    document.documentElement.style.fontSize = (BASE_FONT_PX * scale) + "px";
    updateButtonStates(scale);
  }

  function currentIndex(scale) {
    const i = SCALES.indexOf(scale);
    return i < 0 ? SCALES.indexOf(DEFAULT_SCALE) : i;
  }

  function step(delta) {
    const cur = readScale();
    const i = currentIndex(cur);
    const next = SCALES[Math.max(0, Math.min(SCALES.length - 1, i + delta))];
    writeScale(next);
    applyScale(next);
  }

  function reset() {
    writeScale(DEFAULT_SCALE);
    applyScale(DEFAULT_SCALE);
  }

  function updateButtonStates(scale) {
    const widget = document.querySelector(".sp-fontsize");
    if (!widget) return;
    const i = currentIndex(scale);
    const minus = widget.querySelector(".sp-fontsize__btn--minus");
    const plus  = widget.querySelector(".sp-fontsize__btn--plus");
    if (minus) minus.disabled = (i <= 0);
    if (plus)  plus.disabled  = (i >= SCALES.length - 1);
  }

  function bindWidget() {
    const w = document.querySelector(".sp-fontsize");
    if (!w || w.dataset.spBound === "1") return w;
    const minus = w.querySelector(".sp-fontsize__btn--minus");
    const reset_ = w.querySelector(".sp-fontsize__btn--reset");
    const plus  = w.querySelector(".sp-fontsize__btn--plus");
    if (minus)  minus.addEventListener("click",  function () { step(-1); });
    if (reset_) reset_.addEventListener("click", reset);
    if (plus)   plus.addEventListener("click",   function () { step(+1); });
    w.dataset.spBound = "1";
    updateButtonStates(readScale());
    return w;
  }

  // Exposed so the reader-settings panel (reading.js) can bind buttons
  // immediately after it injects the .sp-fontsize markup, regardless
  // of script load order.
  window.SP_FontSize = { bindWidget: bindWidget, apply: function () { applyScale(readScale()); } };

  function init() {
    if (!document.body) return;
    bindWidget();
    applyScale(readScale());
  }

  // Keyboard shortcuts (Ctrl/Cmd + Shift + +/-/0)
  document.addEventListener("keydown", function (e) {
    if (!(e.ctrlKey || e.metaKey) || !e.shiftKey) return;
    const tag = (e.target && e.target.tagName) || "";
    if (/^(INPUT|TEXTAREA|SELECT)$/.test(tag)) return;
    if (e.key === "+" || e.key === "=") { step(+1); e.preventDefault(); }
    else if (e.key === "_" || e.key === "-") { step(-1); e.preventDefault(); }
    else if (e.key === "0" || e.key === ")") { reset(); e.preventDefault(); }
  });

  if (typeof window.document$ !== "undefined" && window.document$.subscribe) {
    window.document$.subscribe(function () { init(); });
  } else if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
  } else {
    init();
  }
})();
