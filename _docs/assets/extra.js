/* ================================================================
   Senior Project — small client-side enhancements
   - Inject "Report a problem" link at the end of every content page
   - Render Mermaid diagrams (Material 9.7 + Mermaid 11 native integration
     is broken — we load Mermaid 10 ourselves and call run())
   - Re-runs on Material's instant navigation (document$)
   ================================================================ */
(function () {
  const REPO = "BakhodiribnYashinibnMansur/SeniorProject";
  const REPORT_CLASS = "sp-report";

  // ---- Mermaid bootstrap ------------------------------------------------
  // pymdownx.superfences with fence_div_format produces <div class="mermaid">
  // containing the diagram source as text. We load Mermaid 10 and call run()
  // on every page (including SPA instant nav).
  let mermaidReady = null;
  function loadMermaid() {
    if (mermaidReady) return mermaidReady;
    mermaidReady = new Promise((resolve, reject) => {
      const s = document.createElement("script");
      s.src = "https://unpkg.com/mermaid@10/dist/mermaid.min.js";
      s.onload = () => {
        if (typeof mermaid === "undefined") {
          reject(new Error("mermaid global missing after script load"));
          return;
        }
        mermaid.initialize({
          startOnLoad: false,
          theme: "dark",
          securityLevel: "loose",
          themeVariables: {
            background: "#0a0a0a",
            primaryColor: "#0a0a0a",
            primaryTextColor: "#d4d4d4",
            primaryBorderColor: "#39ff7a",
            lineColor: "#39ff7a",
            secondaryColor: "#1a1a1a",
            tertiaryColor: "#0e0e0e",
            fontFamily: "JetBrains Mono, ui-monospace, monospace",
          },
        });
        resolve(mermaid);
      };
      s.onerror = () => reject(new Error("failed to load mermaid"));
      document.head.appendChild(s);
    });
    return mermaidReady;
  }

  function fixMermaidSize(block) {
    // Mermaid hard-codes width="100%" + max-width on the SVG, which squashes
    // wide graphs. Use the inline style's max-width as the natural width and
    // set explicit width/height attributes so the parent .mermaid container
    // can scroll horizontally.
    const svg = block.querySelector("svg");
    if (!svg) return;
    const styleMaxW = svg.style.maxWidth;
    if (styleMaxW && styleMaxW.endsWith("px")) {
      const naturalW = parseFloat(styleMaxW);
      const vb = svg.viewBox && svg.viewBox.baseVal;
      const naturalH = vb && vb.height ? naturalW * (vb.height / vb.width) : naturalW;
      svg.setAttribute("width", naturalW);
      svg.setAttribute("height", naturalH);
      svg.style.maxWidth = "none";
    }
  }

  function renderMermaid() {
    // Find unprocessed mermaid blocks. We skip ones already rendered
    // (have a child SVG) so SPA navigation re-render is idempotent.
    const blocks = Array.from(document.querySelectorAll(".mermaid")).filter(
      (b) => !b.querySelector("svg") && b.textContent.trim().length > 0
    );
    if (blocks.length === 0) return;
    loadMermaid()
      .then((m) => m.run({ nodes: blocks }))
      .then(() => {
        blocks.forEach(fixMermaidSize);
      })
      .catch((err) => {
        // Hide blocks we can't render rather than showing raw source
        blocks.forEach((b) => {
          b.style.display = "none";
        });
        console.warn("[sp] mermaid render failed:", err);
      });
  }

  function inject() {
    // Skip the home page (it has its own custom footer)
    if (document.querySelector("[data-home]")) return;

    const content = document.querySelector(".md-content__inner");
    if (!content) return;

    // Avoid double-injection on SPA navigation
    if (content.querySelector("." + REPORT_CLASS)) return;

    const url = location.href;
    const title = document.title || "Senior Project";
    const issueTitle = encodeURIComponent("[doc] " + title);
    const issueBody = encodeURIComponent(
      "**Page URL:** " + url + "\n\n" +
      "**Problem description:**\n" +
      "<!-- describe the issue here -->\n\n" +
      "**Expected:**\n\n" +
      "**Actual:**\n"
    );
    const issueUrl =
      "https://github.com/" + REPO +
      "/issues/new?title=" + issueTitle +
      "&body=" + issueBody +
      "&labels=docs";

    const editUrl =
      "https://github.com/" + REPO + "/edit/main/" +
      // best-effort path from URL
      location.pathname.replace(/^\//, "").replace(/\/$/, "/index.md");

    const wrap = document.createElement("aside");
    wrap.className = REPORT_CLASS;
    wrap.innerHTML =
      '<hr class="sp-report-rule">' +
      '<div class="sp-report-row">' +
        '<span class="sp-report-prompt">$&nbsp;</span>' +
        '<span class="sp-report-text">found a problem on this page?</span>' +
        '<a class="sp-report-link" href="' + issueUrl + '" target="_blank" rel="noopener">' +
          '<span class="sp-report-arrow">&gt;</span>&nbsp;report it' +
        '</a>' +
        '<span class="sp-report-sep">·</span>' +
        '<a class="sp-report-link sp-report-link--soft" href="' + editUrl + '" target="_blank" rel="noopener">' +
          '<span class="sp-report-arrow">&gt;</span>&nbsp;edit on github' +
        '</a>' +
      '</div>';
    content.appendChild(wrap);
  }

  function run() {
    inject();
    renderMermaid();
  }

  // Initial run + re-run on Material instant nav
  if (typeof document$ !== "undefined" && document$.subscribe) {
    document$.subscribe(run);
  } else {
    if (document.readyState === "loading") {
      document.addEventListener("DOMContentLoaded", run);
    } else {
      run();
    }
  }
})();
