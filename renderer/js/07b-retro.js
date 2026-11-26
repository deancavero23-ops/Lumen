'use strict';
// ============================================================ Lumi, la mascota + modo bajos recursos
// Lumi es el ojo del logo. Reacciona a lo que pasa y da consejos cortos.
// Modos (Ajustes): activa | tranquila (icono quieto, sin globitos) | off.
// En modo bajos recursos siempre va en "tranquila".

function modoLigero() {
  const m = S.settings.modoLigero || 'auto';
  return m === 'on' || (m !== 'off' && !!S.info.equipoLimitado);
}
function modoMascota() {
  const m = S.settings.mascota || 'activa';
  if (m === 'off') return 'off';
  return modoLigero() ? 'tranquila' : m;
}
function aplicarModoLigero() { document.body.classList.toggle('ligero', modoLigero()); }

const RETRO = { flash: null, flashHasta: 0, globo: null, globoHasta: 0, ultimoUso: Date.now(), dicho: {} };

// Dibujo: Lumi, la mascota. Ojos grandes, brillitos, mejillas y una chispa.
const LUMI_OJOS = (cls = '') => `<g class="${cls}"><ellipse cx="37" cy="49" rx="15" ry="17" fill="#fff"/><ellipse cx="63" cy="49" rx="15" ry="17" fill="#fff"/><circle class="rt-pupila" cx="37" cy="49" r="9" fill="#0e0e10"/><circle class="rt-pupila" cx="63" cy="49" r="9" fill="#0e0e10"/><circle cx="33" cy="44" r="3" fill="#fff"/><circle cx="59" cy="44" r="3" fill="#fff"/><circle cx="41" cy="54" r="1.6" fill="#fff"/><circle cx="67" cy="54" r="1.6" fill="#fff"/></g>`;
const LUMI_SONROSA = '<ellipse cx="24" cy="65" rx="6" ry="3.5" fill="#ff9a8f" opacity=".85"/><ellipse cx="76" cy="65" rx="6" ry="3.5" fill="#ff9a8f" opacity=".85"/>';
const LUMI_SONRISA = (d = 'M42 68 Q50 75 58 68') => `<path d="${d}" stroke="#fff" stroke-width="3" fill="none" stroke-linecap="round"/>`;
function retroSVG(animo) {
  const caras = {
    normal: LUMI_OJOS() + LUMI_SONROSA + LUMI_SONRISA(),
    piensa: LUMI_OJOS('rt-mira') + LUMI_SONROSA + LUMI_SONRISA('M44 69 Q50 72 56 69'),
    feliz: '<path d="M25 52 Q37 36 49 52" stroke="#fff" stroke-width="4" fill="none" stroke-linecap="round"/><path d="M51 52 Q63 36 75 52" stroke="#fff" stroke-width="4" fill="none" stroke-linecap="round"/>' + LUMI_SONROSA + LUMI_SONRISA('M38 65 Q50 79 62 65'),
    guino: LUMI_OJOS() + '<ellipse cx="37" cy="49" rx="16" ry="18" fill="#fff"/><path d="M26 49 Q37 39 48 49" stroke="#e5322d" stroke-width="4" fill="none" stroke-linecap="round"/>' + LUMI_SONROSA + LUMI_SONRISA('M40 67 Q50 76 60 67'),
    preocupado: LUMI_OJOS() + LUMI_SONROSA + '<path d="M24 36 L42 30 M58 30 L76 36" stroke="#fff" stroke-width="4" stroke-linecap="round"/>' + LUMI_SONRISA('M44 71 Q50 67 56 71'),
    duerme: '<path d="M25 51 Q37 60 49 51 M51 51 Q63 60 75 51" stroke="#fff" stroke-width="4" fill="none" stroke-linecap="round"/>' + LUMI_SONROSA + '<text x="74" y="30" font-size="16" font-weight="700" fill="#fff">z</text><text x="84" y="18" font-size="11" font-weight="700" fill="#fff">z</text>',
    gafas: LUMI_OJOS() + LUMI_SONROSA + '<g fill="none" stroke="#0e0e10" stroke-width="3"><rect x="17" y="35" width="30" height="26" rx="10"/><rect x="53" y="35" width="30" height="26" rx="10"/><path d="M47 46h6"/></g>' + LUMI_SONRISA(),
    casco: LUMI_OJOS() + LUMI_SONROSA + LUMI_SONRISA() + '<path d="M18 34 Q50 8 82 34 Z" fill="#a1a1aa"/><rect x="15" y="31" width="70" height="7" rx="3.5" fill="#71717a"/><rect x="45" y="16" width="10" height="16" rx="3" fill="#e5322d"/>'
  };
  const cara = caras[animo] || caras.normal;
  return `<svg viewBox="0 0 100 100" aria-hidden="true"><defs><linearGradient id="rtg" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stop-color="#ff5a52"/><stop offset=".55" stop-color="#e5322d"/><stop offset="1" stop-color="#8f1d19"/></linearGradient></defs><rect x="5" y="5" width="90" height="90" rx="24" fill="url(#rtg)"/><path d="M79 6c.7 4.8 2.7 6.8 7.5 7.5-4.8.7-6.8 2.7-7.5 7.5-.7-4.8-2.7-6.8-7.5-7.5 4.8-.7 6.8-2.7 7.5-7.5z" fill="#fff"/>${cara}</svg>`;
}

function animoActual() {
  if (Date.now() < RETRO.flashHasta && RETRO.flash) return RETRO.flash;
  if (S.juego && S.juego.activo) return 'gafas';
  if (S.busy) return /actualiz|instal|update/i.test(S.busy.label || '') ? 'casco' : 'piensa';
  if (Date.now() - RETRO.ultimoUso > 3 * 60 * 1000) return 'duerme';
  return 'normal';
}

// Reacción breve (p. ej. guiño al terminar una limpieza)
function retroReacciona(animo, msg, ms = 4000) {
  RETRO.flash = animo; RETRO.flashHasta = Date.now() + ms;
  if (msg) retroDice(msg, ms + 1500);
  renderLumi();
  setTimeout(renderLumi, ms + 50);
}
// Globito. clave: si se da, el consejo solo sale una vez por sesión
function retroDice(msg, ms = 6000, clave) {
  if (modoMascota() !== 'activa') return;
  if (clave) { if (RETRO.dicho[clave]) return; RETRO.dicho[clave] = true; }
  RETRO.globo = msg; RETRO.globoHasta = Date.now() + ms;
  renderLumi();
  setTimeout(renderLumi, ms + 50);
}

function renderLumi() {
  let el = document.getElementById('retro');
  const modo = modoMascota();
  if (modo === 'off') { if (el) el.remove(); return; }
  if (!el) { el = document.createElement('div'); el.id = 'retro'; document.body.appendChild(el); }
  const animo = modo === 'tranquila' ? 'normal' : animoActual();
  const presentar = !S.settings.mascotaPresentada && modo === 'activa' && S.settings.tourVisto && $('#tour').classList.contains('hidden');
  const globo = presentar
    ? `<div class="rt-globo"><b>${L('¡Hola! Soy Lumi 👋', "Hi! I'm Lumi 👋")}</b><span>${L('Te acompaño mientras cuidas tu PC: te aviso cómo va todo y te doy consejos. Si te molesto, me escondo.', 'I keep you company while you look after your PC: I show how things are going and give tips. If I bother you, I can hide.')}</span><div class="rt-btns"><button class="btn small primary" data-act="retroOk">${L('¡Hola, Lumi!', 'Hi, Lumi!')}</button><button class="btn small ghost" data-act="retroTranquila">${L('Solo quieto', 'Just stay still')}</button><button class="btn small ghost" data-act="retroOff">${L('Esconder a Lumi', 'Hide Lumi')}</button></div></div>`
    : (modo === 'activa' && RETRO.globo && Date.now() < RETRO.globoHasta ? `<div class="rt-globo corto" role="status">${esc(RETRO.globo)}</div>` : '');
  // Solo se redibuja si algo cambió (así no gasta nada en cada repintado de la ventana)
  const clave = modo + animo + globo + S.settings.idioma;
  if (el.dataset.k === clave) return;
  el.dataset.k = clave;
  el.className = 'retro ' + (modo === 'tranquila' ? 'quieto' : 'vivo') + ' a-' + animo;
  el.innerHTML = `<button class="rt-cara" data-act="retroMenu" title="${L('Lumi · clic para opciones', 'Lumi · click for options')}" aria-label="${L('Lumi, la mascota. Opciones', 'Lumi, the mascot. Options')}">${retroSVG(animo)}</button>` + globo;
}

function retroMenu() {
  $('#modal').innerHTML = `<div class="modal" role="dialog" aria-modal="true" style="width:min(460px,92vw)">
    <h2>${L('Lumi, tu acompañante', 'Lumi, your companion')}</h2>
    <p class="lead">${L('Elige cómo quieres a Lumi. Puedes cambiarlo cuando quieras en Ajustes.', 'Choose how you want Lumi. You can change it any time in Settings.')}</p>
    <div class="rt-opciones">
      <button class="btn ${S.settings.mascota === 'activa' || !S.settings.mascota ? 'primary' : ''}" data-act="retroActiva">😊 ${L('Activa: reacciona y da consejos', 'Active: reacts and gives tips')}</button>
      <button class="btn ${S.settings.mascota === 'tranquila' ? 'primary' : ''}" data-act="retroTranquila">😐 ${L('Tranquila: solo un icono quieto', 'Calm: just a still icon')}</button>
      <button class="btn ${S.settings.mascota === 'off' ? 'primary' : ''}" data-act="retroOff">🙈 ${L('Esconder a Lumi', 'Hide Lumi')}</button>
    </div>
    ${modoLigero() ? `<p class="meta">${L('Tu PC va en modo bajos recursos, así que Lumi se queda quieta para no gastar.', 'Your PC is in low-resource mode, so Lumi stays still to save resources.')}</p>` : ''}
    <div class="foot"><button class="btn ghost" data-act="modalClose">${L('Cerrar', 'Close')}</button></div></div>`;
  $('#modal').classList.remove('hidden');
}
async function setMascota(m) {
  S.settings = await rl.setSettings({ mascota: m, mascotaPresentada: true });
  $('#modal').classList.add('hidden');
  renderLumi(); render();
  if (m === 'off') toast(L('Lumi se escondió. Puedes traerla de vuelta en Ajustes.', 'Lumi is hidden. You can bring it back in Settings.'));
}

// Actividad: Lumi se despierta al usar el programa (sin trabajo continuo: solo marca la hora)
['mousedown', 'keydown'].forEach((ev) => document.addEventListener(ev, () => {
  const dormia = Date.now() - RETRO.ultimoUso > 3 * 60 * 1000;
  RETRO.ultimoUso = Date.now();
  if (dormia) renderLumi();
}, true));
// Un repaso lento (cada 30 s) para que se duerma si no se usa; nada si la ventana está oculta
setInterval(() => { if (!document.hidden && modoMascota() === 'activa') renderLumi(); }, 30000);
