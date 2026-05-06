/* ================================================================
   Senior Project — small client-side enhancements
   - Inject "Report a problem" link at the end of every content page
   - Re-runs on Material's instant navigation (document$)
   ================================================================ */
(function () {
  const REPO = "BakhodiribnYashinibnMansur/SeniorProject";
  const REPORT_CLASS = "sp-report";

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

  // Initial run + re-run on Material instant nav
  if (typeof document$ !== "undefined" && document$.subscribe) {
    document$.subscribe(inject);
  } else {
    if (document.readyState === "loading") {
      document.addEventListener("DOMContentLoaded", inject);
    } else {
      inject();
    }
  }
})();
