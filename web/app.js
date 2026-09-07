const addons = [
  { name: 'Cinemeta', description: 'Official movie and series metadata and catalogs.', kind: 'direct', url: 'https://v3-cinemeta.strem.io/manifest.json' },
  { name: 'OpenSubtitles v3', description: 'Official multilingual subtitle add-on.', kind: 'direct', url: 'https://opensubtitles-v3.strem.io/manifest.json' },
  { name: 'WatchHub', description: 'Shows legal streaming availability by country.', kind: 'direct', url: 'https://watchhub.strem.io/manifest.json' },
  { name: 'YouTube Channels', description: 'YouTube channel catalogs in compatible clients.', kind: 'direct', url: 'https://v3-channels.strem.io/manifest.json' },
  { name: 'Anime Kitsu', description: 'Anime metadata, search, catalogs, and subtitles.', kind: 'direct', url: 'https://anime-kitsu.strem.fun/manifest.json' },
  { name: 'TMDB Addon', description: 'TMDB discovery, artwork, and metadata setup.', kind: 'setup', url: 'https://stremio-addons.net/addons?search=The%20Movie%20Database' },
  { name: 'Streaming Catalogs', description: 'Catalog rows for popular streaming services.', kind: 'setup', url: 'https://stremio-addons.net/addons/streaming-catalogs' },
  { name: 'USA TV', description: 'Community Live TV listing and current status.', kind: 'setup', url: 'https://stremio-addons.net/addons/usa-tv' },
  { name: 'TOP Streaming', description: 'Popular community streaming configuration page.', kind: 'setup', url: 'https://stremio-addons.net/addons/top-streaming' },
  { name: 'AIOStreams', description: 'One configurable entry point for supported sources.', kind: 'setup', url: 'https://stremio-addons.net/addons/aiostreams' },
  { name: 'Comet', description: 'Community provider with optional configuration.', kind: 'setup', url: 'https://stremio-addons.net/addons/comet' },
  { name: 'MediaFusion', description: 'Community catalogs and provider configuration.', kind: 'setup', url: 'https://stremio-addons.net/addons/mediafusion-elfhosted' },
  { name: 'Torrentio', description: 'Community source; configure only sources you may use.', kind: 'setup', url: 'https://stremio-addons.net/addons/torrentio' },
  { name: 'Anime Add-ons', description: 'Current popular anime add-ons and manifest health.', kind: 'setup', url: 'https://stremio-addons.net/addons?categories=anime&sort=popular' },
  { name: 'Community Catalog', description: 'Browse every category in the community directory.', kind: 'setup', url: 'https://stremio-addons.net/addons/stremio-addons.net' }
];

const demoManifest = 'https://aiometadata.elfhosted.com/stremio/7e1b6e37-b28d-4ecb-ab15-206d7f44d69f/manifest.json';

const state = { filter: 'all', query: '', qrUrl: '', deferredPrompt: null };
const $ = (selector, root = document) => root.querySelector(selector);
const $$ = (selector, root = document) => [...root.querySelectorAll(selector)];

function validHttps(value, manifestOnly = false) {
  try {
    const url = new URL(value);
    return url.protocol === 'https:' && (!manifestOnly || /\/manifest\.json(?:\?.*)?$/i.test(url.href));
  } catch { return false; }
}

function toStremio(url) { return url.replace(/^https:\/\//i, 'stremio://'); }
function notify(message) {
  const toast = $('#toast');
  toast.textContent = message;
  toast.classList.add('show');
  clearTimeout(notify.timer);
  notify.timer = setTimeout(() => toast.classList.remove('show'), 2200);
}

function openManifest(url, messageEl) {
  if (!validHttps(url, true)) {
    if (messageEl) { messageEl.textContent = 'Use a secure HTTPS URL ending in manifest.json.'; messageEl.classList.add('error'); }
    return;
  }
  if (messageEl) { messageEl.textContent = 'Opening Stremio…'; messageEl.classList.remove('error'); }
  window.location.href = toStremio(url);
  setTimeout(() => notify('If Stremio did not open, use Copy or QR.'), 1300);
}

async function copyText(value) {
  try { await navigator.clipboard.writeText(value); notify('Link copied'); }
  catch { notify('Press and hold the link to copy it'); }
}

function showQr(url, label = 'Add-on link') {
  if (!validHttps(url)) { notify('Enter a secure HTTPS link first'); return; }
  state.qrUrl = url;
  $('#qr-label').textContent = label;
  const dialog = $('#qr-dialog');
  dialog.showModal();
  const canvas = $('#qr-canvas');
  if (window.QRCode) {
    window.QRCode.toCanvas(canvas, url, { width: 280, margin: 1, color: { dark: '#07111f', light: '#ffffff' }, errorCorrectionLevel: 'M' }, error => {
      if (error) notify('QR could not be created');
    });
  } else {
    const ctx = canvas.getContext('2d'); ctx.fillStyle = '#fff'; ctx.fillRect(0, 0, 280, 280); ctx.fillStyle = '#07111f'; ctx.font = '700 16px sans-serif'; ctx.textAlign = 'center'; ctx.fillText('QR is loading. Try again.', 140, 142);
  }
}

function renderAddons() {
  const list = $('#addon-list');
  const filtered = addons.filter(a => (state.filter === 'all' || a.kind === state.filter) && `${a.name} ${a.description}`.toLowerCase().includes(state.query));
  list.replaceChildren(...filtered.map((addon) => {
    const index = addons.indexOf(addon) + 1;
    const row = document.createElement('article');
    row.className = 'addon-row';
    row.innerHTML = `<div class="addon-num">${String(index).padStart(2, '0')}</div><div class="addon-copy"><h2>${addon.name}</h2><span class="kind ${addon.kind}">${addon.kind === 'direct' ? 'Direct install' : 'Setup page'}</span><p>${addon.description}</p></div><div class="row-actions"></div>`;
    const actions = $('.row-actions', row);
    const main = document.createElement(addon.kind === 'direct' ? 'button' : 'a');
    main.className = addon.kind === 'direct' ? 'primary' : 'secondary';
    main.textContent = addon.kind === 'direct' ? 'Open' : 'Configure';
    if (addon.kind === 'direct') main.addEventListener('click', () => openManifest(addon.url));
    else { main.href = addon.url; main.target = '_blank'; main.rel = 'noopener noreferrer'; }
    const qr = document.createElement('button');
    qr.className = 'secondary'; qr.textContent = 'QR'; qr.type = 'button';
    qr.addEventListener('click', () => showQr(addon.url, addon.name));
    actions.append(main, qr);
    return row;
  }));
  $('#addon-empty').hidden = filtered.length !== 0;
  list.hidden = filtered.length === 0;
}

function showRoute() {
  const route = location.hash.slice(1) || 'addons';
  const safeRoute = ['addons', 'live-tv', 'community'].includes(route) ? route : 'addons';
  $$('[data-view]').forEach(view => { view.hidden = view.dataset.view !== safeRoute; });
  $$('[data-route]').forEach(link => link.classList.toggle('active', link.dataset.route === safeRoute));
  window.scrollTo({ top: 0, behavior: 'instant' });
}

$('#manifest-form').addEventListener('submit', event => { event.preventDefault(); openManifest($('#custom-manifest').value.trim(), $('#manifest-message')); });
$('#open-demo-manifest').addEventListener('click', () => openManifest(demoManifest));
$('#qr-demo-manifest').addEventListener('click', () => showQr(demoManifest, 'StreamVerse AIOMetadata demo'));
$('#addon-search').addEventListener('input', event => { state.query = event.target.value.trim().toLowerCase(); renderAddons(); });
$$('.filter').forEach(button => button.addEventListener('click', () => {
  state.filter = button.dataset.filter;
  $$('.filter').forEach(item => item.classList.toggle('active', item === button));
  renderAddons();
}));

const liveFields = ['live-manifest', 'm3u', 'xmltv'];
function loadLive() {
  try { const saved = JSON.parse(localStorage.getItem('streamverse-live-tv') || '{}'); liveFields.forEach(id => { $(`#${id}`).value = saved[id] || ''; }); } catch {}
}
$('#live-form').addEventListener('submit', event => {
  event.preventDefault();
  const data = Object.fromEntries(liveFields.map(id => [id, $(`#${id}`).value.trim()]));
  const invalid = Object.entries(data).find(([, value]) => value && !validHttps(value));
  const message = $('#live-message');
  if (invalid) { message.textContent = 'Every saved link must use HTTPS.'; message.classList.add('error'); return; }
  localStorage.setItem('streamverse-live-tv', JSON.stringify(data));
  message.textContent = 'Saved privately on this device.'; message.classList.remove('error');
});
$('#install-live').addEventListener('click', () => openManifest($('#live-manifest').value.trim(), $('#live-message')));
$('#qr-live').addEventListener('click', () => showQr($('#live-manifest').value.trim(), 'Live TV manifest'));
$$('.qr-trigger').forEach(button => button.addEventListener('click', () => showQr($(`#${button.dataset.input}`).value.trim())));

$('#qr-dialog .dialog-close').addEventListener('click', () => $('#qr-dialog').close());
$('#qr-dialog').addEventListener('click', event => { if (event.target === $('#qr-dialog')) $('#qr-dialog').close(); });
$('#copy-qr-url').addEventListener('click', () => copyText(state.qrUrl));

window.addEventListener('beforeinstallprompt', event => { event.preventDefault(); state.deferredPrompt = event; $('.install-app').hidden = false; });
$('.install-app').addEventListener('click', async () => { if (!state.deferredPrompt) return; state.deferredPrompt.prompt(); await state.deferredPrompt.userChoice; state.deferredPrompt = null; $('.install-app').hidden = true; });
window.addEventListener('hashchange', showRoute);
window.addEventListener('appinstalled', () => notify('StreamVerse Setup installed'));

if ('serviceWorker' in navigator) navigator.serviceWorker.register('sw.js').catch(() => {});
loadLive(); renderAddons(); showRoute();
