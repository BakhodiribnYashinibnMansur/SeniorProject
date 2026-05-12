/* ================================================================
   Senior Stack — e-ink detection & toggle
   Strategy (3 layers, cheapest first):
     1. URL param: ?eink=1 / ?eink=0  → force, persisted to storage
     2. localStorage: einkMode = "on" | "off" | "auto" (default)
     3. Auto-detect when "auto":
        a. CSS media queries: (monochrome) or (update: slow)
        b. User-Agent sniff: Kindle / Kobo / Onyx (Boox) / reMarkable
        c. Runtime FPS probe (deferred 1.5s after load): < 20 FPS → e-ink
   Also injects a small floating toggle button. Survives Material's
   instant navigation by re-running on document$ subscription if
   available, otherwise on DOMContentLoaded.
   ================================================================ */
(function () {
  "use strict";

  const STORAGE_KEY = "sp-eink-mode";
  const FPS_THRESHOLD = 20;
  const FPS_SAMPLE_MS = 1000;
  const FPS_DELAY_MS = 1500;

  // ---------- Mode resolution ------------------------------------
  function readStoredMode() {
    try {
      const v = localStorage.getItem(STORAGE_KEY);
      if (v === "on" || v === "off" || v === "auto") return v;
    } catch (e) {}
    return "auto";
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

  // ---------- Auto-detection signals -----------------------------
  function mediaQuerySignal() {
    try {
      if (window.matchMedia("(monochrome)").matches) return true;
      if (window.matchMedia("(update: slow)").matches) return true;
    } catch (e) {}
    return false;
  }

  function userAgentSignal() {
    const ua = (navigator.userAgent || "").toLowerCase();
    // Kindle: "Kindle", "Silk" (Amazon Silk on Kindle Fire is NOT e-ink, skip Silk)
    if (/kindle/.test(ua)) return true;
    // Kobo
    if (/kobo/.test(ua)) return true;
    // Onyx Boox (Android-based, but ships "Onyx" in UA on some firmwares)
    if (/onyx|boox/.test(ua)) return true;
    // reMarkable browser (qutebrowser / Surf based)
    if (/remarkable/.test(ua)) return true;
    // PocketBook
    if (/pocketbook/.test(ua)) return true;
    return false;
  }

  function fpsProbe(callback) {
    if (typeof requestAnimationFrame !== "function") {
      callback(false);
      return;
    }
    if (document.visibilityState && document.visibilityState !== "visible") {
      // Background tabs throttle rAF to ~1 FPS — would false-positive.
      callback(false);
      return;
    }
    let frames = 0;
    const start = performance.now();
    function tick(now) {
      frames++;
      if (now - start >= FPS_SAMPLE_MS) {
        const fps = (frames * 1000) / (now - start);
        callback(fps < FPS_THRESHOLD);
        return;
      }
      requestAnimationFrame(tick);
    }
    requestAnimationFrame(tick);
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
  function decideInitialMode() {
    // URL override wins and is persisted
    const url = readUrlOverride();
    if (url) {
      writeStoredMode(url);
      return { mode: url, allowAuto: false };
    }
    const stored = readStoredMode();
    if (stored === "on" || stored === "off") {
      return { mode: stored, allowAuto: false };
    }
    return { mode: "auto", allowAuto: true };
  }

  function runAutoDetect() {
    if (mediaQuerySignal() || userAgentSignal()) {
      setEink(true);
      return;
    }
    // Defer FPS probe so it doesn't race with initial paint
    setTimeout(function () {
      fpsProbe(function (slow) {
        if (slow && !isEinkOn()) setEink(true);
      });
    }, FPS_DELAY_MS);
  }

  function init() {
    if (!document.body) return; // wait for body
    ensureToggle();
    const decision = decideInitialMode();
    if (decision.mode === "on") {
      setEink(true);
    } else if (decision.mode === "off") {
      setEink(false);
    } else {
      // auto
      runAutoDetect();
      updateToggleLabel();
    }
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
