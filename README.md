# Arej AI Summarizer

Arej AI Summarizer is a production-ready, responsive web app for summarizing PDF files and long text directly in the browser.

## Quick run

Open `/home/runner/work/dist-lab/dist-lab/index.html` in any modern browser.

## Features

- Home view with:
  - title **Arej AI Summarizer**
  - subtitle **Summarize PDFs and long documents instantly using AI.**
  - PDF upload section
  - long-text input section
  - **Sample PDF** button
  - **Summarize** button
- Structured summary output sections:
  - Executive Summary
  - Key Points
  - Important Dates
  - Important Numbers
  - Action Items
  - Frequently Asked Questions
- Summary actions:
  - Copy Summary
  - Download Summary (`.md`)
  - Start New Summary
- Sidebar with persisted Recent Summaries and Saved Documents
- Dark mode toggle persisted in `localStorage`
- Live character counter
- Loading animation and polished empty/fallback states
- Deterministic local summarization fallback (no API key and no external AI dependency)
- Responsive layout with rounded cards, gradients, and smooth transitions

## Notes

- The summarization flow always returns a complete structured result, even without external AI services.
- PDF extraction is performed locally in-browser with robust fallback behavior when the PDF has limited extractable text.
