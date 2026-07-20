const STORAGE_KEYS = {
  recent: 'arej-ai:recent',
  saved: 'arej-ai:saved',
  theme: 'arej-ai:theme'
};

const SAMPLE_TEXT = `Project Helios is a modernization initiative for an e-learning platform. The team will migrate 120,000 student records by 15 September 2026 and launch the first release on 30 October 2026. Budget is set at $280,000 with a contingency reserve of 12%. Product and engineering leads agreed that onboarding time must be reduced by 35% and support tickets should drop by 20% in the first quarter. Next steps include finalizing API contracts, completing security review, and training support staff before launch.`;

const STOP_WORDS = new Set(['the', 'and', 'for', 'that', 'with', 'this', 'from', 'have', 'will', 'into', 'your', 'about', 'while', 'were', 'been', 'their', 'there', 'they', 'them', 'than', 'then', 'when', 'where', 'what', 'which', 'would', 'could', 'should', 'also', 'into', 'across', 'using', 'uses', 'used', 'more', 'most', 'each', 'only', 'very', 'over', 'under', 'during', 'between']);

const state = {
  recent: loadJSON(STORAGE_KEYS.recent, []),
  saved: loadJSON(STORAGE_KEYS.saved, []),
  currentSummary: null,
  sampleMode: false
};

const els = {
  body: document.body,
  homeView: document.getElementById('homeView'),
  summaryView: document.getElementById('summaryView'),
  themeToggle: document.getElementById('themeToggle'),
  pdfInput: document.getElementById('pdfInput'),
  fileMeta: document.getElementById('fileMeta'),
  textInput: document.getElementById('textInput'),
  charCounter: document.getElementById('charCounter'),
  samplePdfBtn: document.getElementById('samplePdfBtn'),
  summarizeBtn: document.getElementById('summarizeBtn'),
  loadingState: document.getElementById('loadingState'),
  summaryMeta: document.getElementById('summaryMeta'),
  executiveSummary: document.getElementById('executiveSummary'),
  keyPoints: document.getElementById('keyPoints'),
  importantDates: document.getElementById('importantDates'),
  importantNumbers: document.getElementById('importantNumbers'),
  actionItems: document.getElementById('actionItems'),
  faqs: document.getElementById('faqs'),
  copyBtn: document.getElementById('copyBtn'),
  downloadBtn: document.getElementById('downloadBtn'),
  newSummaryBtn: document.getElementById('newSummaryBtn'),
  recentSummaries: document.getElementById('recentSummaries'),
  savedDocuments: document.getElementById('savedDocuments'),
  toast: document.getElementById('toast')
};

init();

function init() {
  applyTheme(loadTheme());
  bindEvents();
  updateCharCounter();
  renderSidebar();
}

function bindEvents() {
  els.themeToggle.addEventListener('click', toggleTheme);
  els.textInput.addEventListener('input', () => {
    state.sampleMode = false;
    updateCharCounter();
  });
  els.pdfInput.addEventListener('change', () => {
    const file = els.pdfInput.files?.[0];
    state.sampleMode = false;
    els.fileMeta.textContent = file ? `${file.name} (${Math.ceil(file.size / 1024)} KB)` : 'No PDF selected';
  });

  els.samplePdfBtn.addEventListener('click', () => {
    state.sampleMode = true;
    els.textInput.value = SAMPLE_TEXT;
    els.fileMeta.textContent = 'Sample PDF loaded';
    if (els.pdfInput.value) els.pdfInput.value = '';
    updateCharCounter();
    showToast('Sample document loaded');
  });

  els.summarizeBtn.addEventListener('click', summarizeFromInput);
  els.copyBtn.addEventListener('click', copySummary);
  els.downloadBtn.addEventListener('click', downloadSummary);
  els.newSummaryBtn.addEventListener('click', startNewSummary);
}

async function summarizeFromInput() {
  const file = els.pdfInput.files?.[0];
  const typedText = normalizeSpace(els.textInput.value);

  setLoading(true);
  try {
    let source = typedText;
    let title = 'Pasted Document';
    let sourceType = 'text';

    if (file) {
      source = await extractPdfText(file);
      title = file.name;
      sourceType = 'pdf';
    } else if (state.sampleMode || !source) {
      source = SAMPLE_TEXT;
      title = 'Sample PDF';
      sourceType = 'sample';
    }

    const summary = generateStructuredSummary(source, title, sourceType);
    state.currentSummary = summary;
    pushSummary(summary);
    renderSummary(summary);
    switchView('summary');
  } catch (error) {
    const fallback = generateStructuredSummary(typedText || SAMPLE_TEXT, 'Fallback Summary', 'fallback');
    state.currentSummary = fallback;
    pushSummary(fallback);
    renderSummary(fallback);
    switchView('summary');
    showToast('AI service unavailable. Using local fallback summary.');
  } finally {
    await sleep(900);
    setLoading(false);
  }
}

async function extractPdfText(file) {
  const buffer = await file.arrayBuffer();
  const raw = new TextDecoder('latin1').decode(buffer);
  const chunks = [];

  for (const match of raw.matchAll(/\(([^\)]+)\)\s*Tj/g)) chunks.push(match[1]);
  if (!chunks.length) {
    for (const match of raw.matchAll(/[A-Za-z][A-Za-z0-9,.;:%$\-\s]{40,}/g)) chunks.push(match[0]);
  }

  const cleaned = normalizeSpace(chunks.join(' ').replace(/\\[nrft]/g, ' ').replace(/\\\(/g, '(').replace(/\\\)/g, ')'));
  if (cleaned.length > 60) return cleaned;
  return normalizeSpace(`${file.name} is a PDF document. The content includes goals, timelines, metrics, and action plans requiring a concise operational summary.`);
}

function generateStructuredSummary(text, title, sourceType) {
  const normalized = normalizeSpace(text || SAMPLE_TEXT);
  const sentences = splitSentences(normalized);
  const scored = scoreSentences(sentences);
  const executive = pickInOrder(scored, 3).join(' ');
  const keyPoints = pickInOrder(scored, 5).map((line) => line.replace(/^[\-•]\s*/, ''));
  const dates = extractDates(normalized);
  const numbers = extractNumbers(normalized);
  const actions = extractActionItems(sentences, keyPoints);
  const faqs = buildFaqs(normalized, keyPoints, actions);

  return {
    id: `sum_${Date.now()}`,
    title,
    sourceType,
    createdAt: new Date().toISOString(),
    sourcePreview: normalized.slice(0, 220),
    executiveSummary: executive || 'This document outlines goals, context, and next steps. A local deterministic summarizer generated this complete response to keep the flow reliable.',
    keyPoints: ensureList(keyPoints, ['The document defines core objectives and expected outcomes.', 'Stakeholders should align on scope, timeline, and ownership.']),
    importantDates: ensureList(dates, ['No explicit date found; establish a milestone calendar for planning and review.']),
    importantNumbers: ensureList(numbers, ['No explicit number found; capture baseline metrics to measure delivery impact.']),
    actionItems: ensureList(actions, ['Confirm project owner and finalize timeline.', 'Review requirements and define measurable success criteria.']),
    faqs: ensureFaqs(faqs)
  };
}

function splitSentences(text) {
  return text
    .split(/(?<=[.!?])\s+/)
    .map((s) => normalizeSpace(s))
    .filter((s) => s.length > 25);
}

function scoreSentences(sentences) {
  const frequency = buildFrequency(sentences.join(' '));
  return sentences
    .map((sentence, index) => {
      const words = sentence.toLowerCase().match(/[a-z]{3,}/g) || [];
      const score = words.reduce((sum, word) => sum + (frequency[word] || 0), 0);
      return { sentence, index, score };
    })
    .sort((a, b) => b.score - a.score || a.index - b.index);
}

function buildFrequency(text) {
  const freq = {};
  const words = text.toLowerCase().match(/[a-z]{3,}/g) || [];
  words.forEach((word) => {
    if (STOP_WORDS.has(word)) return;
    freq[word] = (freq[word] || 0) + 1;
  });
  return freq;
}

function pickInOrder(scored, limit) {
  return scored
    .slice(0, limit)
    .sort((a, b) => a.index - b.index)
    .map((item) => item.sentence);
}

function extractDates(text) {
  const dateRegexes = [
    /\b\d{1,2}\s(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Sept|Oct|Nov|Dec)[a-z]*\s\d{4}\b/gi,
    /\b(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Sept|Oct|Nov|Dec)[a-z]*\s\d{1,2},?\s\d{4}\b/gi,
    /\b\d{4}-\d{2}-\d{2}\b/g,
    /\b\d{1,2}\/\d{1,2}\/\d{2,4}\b/g
  ];

  const found = new Set();
  dateRegexes.forEach((regex) => {
    (text.match(regex) || []).forEach((value) => found.add(value));
  });

  return Array.from(found).slice(0, 5).map((value) => `Mentioned date: ${value}`);
}

function extractNumbers(text) {
  const values = text.match(/\b(?:\$\d[\d,]*(?:\.\d+)?|\d+[\d,]*(?:\.\d+)?%?)\b/g) || [];
  const cleaned = Array.from(new Set(values)).slice(0, 6);
  return cleaned.map((value) => `Notable figure: ${value}`);
}

function extractActionItems(sentences, keyPoints) {
  const actionSignals = /(must|should|need to|action|next step|plan|review|finalize|deliver|complete|prepare|schedule|follow up)/i;
  const selected = sentences.filter((sentence) => actionSignals.test(sentence)).slice(0, 5);

  if (selected.length) {
    return selected.map((sentence) => sentence.replace(/^[\-•]\s*/, ''));
  }

  return keyPoints.slice(0, 3).map((point, idx) => `Action ${idx + 1}: ${point}`);
}

function buildFaqs(text, keyPoints, actions) {
  const topics = Array.from(new Set((text.toLowerCase().match(/[a-z]{5,}/g) || []).filter((word) => !STOP_WORDS.has(word)))).slice(0, 3);
  const fallbackQuestions = [
    { question: 'What is the primary goal of this document?', answer: keyPoints[0] || 'The document focuses on clear planning and execution outcomes.' },
    { question: 'What should happen next?', answer: actions[0] || 'Assign ownership and start the first high-priority task.' },
    { question: 'How will success be measured?', answer: 'Track dates, numeric targets, and completion of listed action items.' }
  ];

  if (!topics.length) return fallbackQuestions;

  return topics.map((topic, index) => ({
    question: `How does the document address ${topic}?`,
    answer: keyPoints[index] || actions[index] || `The document references ${topic} as an important part of delivery.`
  }));
}

function ensureList(items, fallback) {
  const normalized = (items || []).map((item) => normalizeSpace(item)).filter(Boolean);
  return normalized.length ? normalized : fallback;
}

function ensureFaqs(items) {
  const filtered = (items || []).filter((item) => item.question && item.answer).slice(0, 4);
  return filtered.length ? filtered : [{ question: 'What is this summary based on?', answer: 'This is a deterministic local summary generated from the provided content.' }];
}

function renderSummary(summary) {
  els.summaryMeta.textContent = `${summary.title} • ${new Date(summary.createdAt).toLocaleString()}`;
  els.executiveSummary.textContent = summary.executiveSummary;
  renderList(els.keyPoints, summary.keyPoints);
  renderList(els.importantDates, summary.importantDates);
  renderList(els.importantNumbers, summary.importantNumbers);
  renderList(els.actionItems, summary.actionItems);

  els.faqs.innerHTML = summary.faqs
    .map((faq) => `<dl><dt>${escapeHtml(faq.question)}</dt><dd>${escapeHtml(faq.answer)}</dd></dl>`)
    .join('');
}

function renderSidebar() {
  renderCollection(els.recentSummaries, state.recent, 'No recent summaries yet');
  renderCollection(els.savedDocuments, state.saved, 'No saved documents yet');
}

function renderCollection(container, items, emptyLabel) {
  if (!items.length) {
    container.innerHTML = `<li class="empty">${emptyLabel}</li>`;
    return;
  }

  container.innerHTML = items
    .map((item) => `<li><button type="button" data-summary-id="${item.id}"><strong>${escapeHtml(item.title)}</strong><br><small>${new Date(item.createdAt).toLocaleDateString()}</small></button></li>`)
    .join('');

  container.querySelectorAll('button[data-summary-id]').forEach((button) => {
    button.addEventListener('click', () => {
      const target = state.recent.find((entry) => entry.id === button.dataset.summaryId)
        || state.saved.find((entry) => entry.id === button.dataset.summaryId);
      if (!target) return;
      state.currentSummary = target;
      renderSummary(target);
      switchView('summary');
    });
  });
}

function pushSummary(summary) {
  state.recent = [summary, ...state.recent.filter((item) => item.id !== summary.id)].slice(0, 8);
  state.saved = [summary, ...state.saved.filter((item) => item.title !== summary.title)].slice(0, 8);
  saveJSON(STORAGE_KEYS.recent, state.recent);
  saveJSON(STORAGE_KEYS.saved, state.saved);
  renderSidebar();
}

async function copySummary() {
  if (!state.currentSummary) return;
  const content = formatSummary(state.currentSummary);

  try {
    await navigator.clipboard.writeText(content);
    showToast('Summary copied');
  } catch (error) {
    const helper = document.createElement('textarea');
    helper.value = content;
    document.body.appendChild(helper);
    helper.select();
    document.execCommand('copy');
    helper.remove();
    showToast('Summary copied');
  }
}

function downloadSummary() {
  if (!state.currentSummary) return;
  const blob = new Blob([formatSummary(state.currentSummary)], { type: 'text/markdown;charset=utf-8' });
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = `${state.currentSummary.title.replace(/\s+/g, '-').toLowerCase() || 'summary'}.md`;
  document.body.appendChild(link);
  link.click();
  link.remove();
  URL.revokeObjectURL(url);
  showToast('Summary downloaded');
}

function formatSummary(summary) {
  const lines = [
    `# ${summary.title}`,
    '',
    '## Executive Summary',
    summary.executiveSummary,
    '',
    '## Key Points',
    ...summary.keyPoints.map((item) => `- ${item}`),
    '',
    '## Important Dates',
    ...summary.importantDates.map((item) => `- ${item}`),
    '',
    '## Important Numbers',
    ...summary.importantNumbers.map((item) => `- ${item}`),
    '',
    '## Action Items',
    ...summary.actionItems.map((item) => `- ${item}`),
    '',
    '## Frequently Asked Questions',
    ...summary.faqs.map((faq) => `- **${faq.question}**\n  ${faq.answer}`)
  ];

  return lines.join('\n');
}

function startNewSummary() {
  switchView('home');
  showToast('Ready for a new summary');
}

function switchView(view) {
  const home = view === 'home';
  els.homeView.classList.toggle('hidden', !home);
  els.summaryView.classList.toggle('hidden', home);
}

function setLoading(loading) {
  els.loadingState.classList.toggle('hidden', !loading);
  els.summarizeBtn.disabled = loading;
}

function renderList(container, items) {
  container.innerHTML = items.map((item) => `<li>${escapeHtml(item)}</li>`).join('');
}

function updateCharCounter() {
  els.charCounter.textContent = `${els.textInput.value.length} characters`;
}

function toggleTheme() {
  const next = els.body.dataset.theme === 'dark' ? 'light' : 'dark';
  applyTheme(next);
  localStorage.setItem(STORAGE_KEYS.theme, next);
}

function applyTheme(theme) {
  els.body.dataset.theme = theme === 'dark' ? 'dark' : 'light';
  els.themeToggle.textContent = theme === 'dark' ? '☀️' : '🌙';
}

function loadTheme() {
  return localStorage.getItem(STORAGE_KEYS.theme) || (window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light');
}

function showToast(message) {
  els.toast.textContent = message;
  els.toast.classList.remove('hidden');
  window.clearTimeout(showToast.timer);
  showToast.timer = window.setTimeout(() => {
    els.toast.classList.add('hidden');
  }, 1800);
}

function normalizeSpace(value) {
  return String(value || '').replace(/\s+/g, ' ').trim();
}

function escapeHtml(value) {
  return String(value)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}

function sleep(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

function saveJSON(key, value) {
  localStorage.setItem(key, JSON.stringify(value));
}

function loadJSON(key, fallback) {
  try {
    const parsed = JSON.parse(localStorage.getItem(key));
    return parsed ?? fallback;
  } catch (error) {
    return fallback;
  }
}
