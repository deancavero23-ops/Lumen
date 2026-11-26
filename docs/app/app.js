/* Lumen PWA — demo interactiva con Lumi. No modifica el dispositivo. */
'use strict';

const D = {
  es: {
    demoChip: '🛡️ Demo · no limpia tu móvil', language: 'Idioma', greeting: '🛡️ ¡Hola! Soy Lumi',
    subtitle: 'Pulsa para ver una simulación. No borro ni cambio nada de tu móvil.',
    scanBtn: 'Simular revisión', talkBtn: 'Hablar con Lumi', estadoInicial: 'Preparado cuando quieras.',
    escaneando: 'Revisando… no toco nada de tu móvil.', listo: 'Listo. Ya tengo el informe.',
    doneTitle: 'Simulación: esto es lo que Lumen encontró', itemsCleaned: 'elementos limpios', spaceFreed: 'espacio liberado',
    programas: 'programas revisados', demoWarning: 'Modo demostración: datos de ejemplo, sin borrar nada.',
    resumenTitle: 'Lo que Lumen haría en Windows', resumen1: 'Quitar archivos que ya no sirven',
    resumen2: 'Quitar programas que sobran sin dejar restos', resumen3: 'Apagar anuncios y rastreos',
    resumen4: 'Preparar el equipo para jugar', cleanTitle: 'Limpieza', cleanDesc: 'Archivos que se pueden quitar con un toque (demo).',
    itemCache: 'Caché de aplicaciones', itemTemp: 'Archivos temporales', itemThumbs: 'Miniaturas antiguas',
    itemLogs: 'Registros antiguos', cleanBtn: 'Limpiar seleccionado', bootTitle: 'Arranque',
    bootDesc: 'Programas que se abren solos (demo).', boot1: 'Sincronización en segundo plano',
    boot2: 'Actualizador automático', boot3: 'Atajo de la nube', recomendado: 'Recomendado',
    importante: 'Importante', opcional: 'Opcional', applyBtn: 'Aplicar cambios', privacyTitle: 'Privacidad',
    privacyDesc: 'Ajustes explicados con palabras sencillas (demo).', privacy1: 'Anuncios personalizados',
    privacy2: 'Ubicación en segundo plano', privacy3: 'Registro de diagnóstico', off: 'Apagado',
    privacyNote: 'Lumen no envía datos: no hay telemetría ni anuncios.',
    anunciosTitle: 'Anuncios y rastreo', anunciosDesc: 'Bloqueo real dentro de Lumen: sin scripts ni conexiones de publicidad.',
    bloquearAnuncios: 'Bloquear anuncios en Lumen', bloquearAnunciosHint: 'Se aplica con una política de contenido y un filtro del service worker. No afecta a otras apps.',
    anunciosActivo: 'Bloqueo activo dentro de Lumen', anunciosInactivo: 'Bloqueo desactivado dentro de Lumen',
    probarBloqueo: 'Probar el bloqueo', bloqueoProbado: 'Bloqueo comprobado: Lumen no carga anuncios.',
    anunciosAviso: 'Una PWA no puede quitar los anuncios del navegador ni de otras aplicaciones: eso solo lo hace el sistema o la app correspondiente.',
    bateriaTitle: 'Batería', bateriaDesc: 'Estado del móvil y consejos para cuidarlo.',
    bateriaNoDisp: 'Estado no disponible', bateriaHint: 'Los navegadores pueden no mostrar el nivel exacto.',
    bateriaCargando: 'Cargando', bateriaDescargando: 'Con batería', bateriaBaja: 'Batería baja: modo ahorro sugerido',
    ahorro: 'Modo ahorro de batería', ahorroHint: 'Reduce animaciones, parpadeos y trabajo en segundo plano. También se activa solo con poca batería o con la app en segundo plano.',
    ahorroActivo: 'Modo ahorro activado', ahorroOff: 'Modo ahorro desactivado',
    consejosTitle: 'Consejos de Lumi', consejo1: 'Baja el brillo o usa el modo oscuro.',
    consejo2: 'Desactiva la sincronización que no uses.', consejo3: 'Limitar la ubicación ahorra batería.',
    consejo4: 'Evita dejar juegos pesados en segundo plano.',
    bateriaAviso: 'Lumen solo lee el estado de la batería: no cambia ajustes del sistema.',
    settingsTitle: 'Ajustes', voice: 'Que Lumi hable en voz alta', voiceHint: 'Usa una voz femenina del móvil si la tiene. Pulsa a Lumi o «Hablar» para activarla.',
    install: 'Instalar en el móvil', installHint: 'Si la abriste desde la cámara, pulsa ⋮ → Abrir en Chrome. Luego ⋮ → «Añadir a pantalla de inicio» / «Instalar app».',
    noApk: 'Demo: ni la PWA ni el APK limpian Android. Las funciones reales están en Windows.',
    demoAviso: 'Aviso: es una demo. No limpia ni borra nada en Android; las funciones reales están en la versión de Windows.',
    sinVoz: 'Lumi te escribió el mensaje: tu móvil no tiene voces instaladas.',
    openWeb: 'Ver la web completa', apkDownload: 'Descargar el APK', github: 'Código en GitHub', windowsVersion: 'Versión para Windows',
    credits: 'Lumen se basa en RetroLimpia, de Retropunk94 · GPL-3.0-or-later · No es la versión oficial.',
    tabInicio: 'Inicio', tabLimpieza: 'Limpieza', tabArranque: 'Arranque', tabPrivacidad: 'Privacidad',
    tabBateria: 'Batería', tabAjustes: 'Ajustes',
    lumiHola: '¡Hola! Soy Lumi. Tócame cuando quieras charlar.',
    lumi1: 'Aquí estoy, vigilando que todo brille.',
    lumi2: 'No borro nada de verdad: esto es una demostración.',
    lumi3: '¿Quieres que revisemos tu móvil juntos?',
    lumiScan: 'Estoy mirando… tranquilo, no cambio nada.',
    lumiDone: 'Listo. Encontré cosas que se podrían limpiar.',
    lumiDemo: 'Esto es solo una demo: tu móvil está a salvo.',
    instalado: 'Se abrió el diálogo de instalación.'
  },
  it: {
    demoChip: '🛡️ Demo · non pulisce il telefono', language: 'Lingua', greeting: '🛡️ Ciao! Sono Lumi',
    subtitle: 'Premi per vedere una simulazione. Non cancello né modifico nulla sul tuo telefono.',
    scanBtn: 'Simula controllo', talkBtn: 'Parla con Lumi', estadoInicial: 'Pronto quando vuoi.',
    escaneando: 'Sto controllando… non tocco niente del tuo telefono.', listo: 'Fatto. Ho il report.',
    doneTitle: 'Simulazione: ecco cosa ha trovato Lumen', itemsCleaned: 'elementi puliti', spaceFreed: 'spazio liberato',
    programas: 'programmi controllati', demoWarning: 'Modalità demo: dati di esempio, nessuna cancellazione.',
    resumenTitle: 'Cosa farebbe Lumen su Windows', resumen1: 'Rimuovere i file inutili',
    resumen2: 'Disinstallare i programmi in eccesso senza residui', resumen3: 'Disattivare annunci e tracciamento',
    resumen4: 'Preparare il dispositivo per il gioco', cleanTitle: 'Pulizia', cleanDesc: 'File che si possono rimuovere con un tocco (demo).',
    itemCache: 'Cache delle app', itemTemp: 'File temporanei', itemThumbs: 'Miniature vecchie',
    itemLogs: 'Registri vecchi', cleanBtn: 'Pulisci la selezione', bootTitle: 'Avvio',
    bootDesc: 'Programmi che si aprono da soli (demo).', boot1: 'Sincronizzazione in background',
    boot2: 'Aggiornamento automatico', boot3: 'Scorciatoia del cloud', recomendado: 'Consigliato',
    importante: 'Importante', opcional: 'Opzionale', applyBtn: 'Applica le modifiche', privacyTitle: 'Privacy',
    privacyDesc: 'Impostazioni spiegate con parole semplici (demo).', privacy1: 'Annunci personalizzati',
    privacy2: 'Posizione in background', privacy3: 'Registro diagnostico', off: 'Disattivato',
    privacyNote: 'Lumen non invia dati: nessuna telemetria né annunci.',
    anunciosTitle: 'Annunci e tracciamento', anunciosDesc: 'Blocco reale dentro Lumen: nessuno script o connessione pubblicitaria.',
    bloquearAnuncios: 'Blocca gli annunci in Lumen', bloquearAnunciosHint: 'Si applica con una policy dei contenuti e un filtro del service worker. Non riguarda le altre app.',
    anunciosActivo: 'Blocco attivo dentro Lumen', anunciosInactivo: 'Blocco disattivato dentro Lumen',
    probarBloqueo: 'Prova il blocco', bloqueoProbado: 'Blocco verificato: Lumen non carica annunci.',
    anunciosAviso: 'Una PWA non può rimuovere gli annunci del browser o di altre app: può farlo solo il sistema o l’app corrispondente.',
    bateriaTitle: 'Batteria', bateriaDesc: 'Stato del telefono e consigli per custodirla.',
    bateriaNoDisp: 'Stato non disponibile', bateriaHint: 'I browser potrebbero non mostrare il livello esatto.',
    bateriaCargando: 'In carica', bateriaDescargando: 'Con batteria', bateriaBaja: 'Batteria bassa: consigliato il risparmio',
    ahorro: 'Modalità risparmio batteria', ahorroHint: 'Riduce animazioni, lampeggi e lavoro in background. Si attiva anche da sola con poca batteria o con l’app in secondo piano.',
    ahorroActivo: 'Risparmio attivato', ahorroOff: 'Risparmio disattivato',
    consejosTitle: 'Consigli di Lumi', consejo1: 'Abbassa la luminosità o usa la modalità scura.',
    consejo2: 'Disattiva la sincronizzazione che non usi.', consejo3: 'Limitare la posizione risparmia batteria.',
    consejo4: 'Evita di lasciare giochi pesanti in background.',
    bateriaAviso: 'Lumen legge solo lo stato della batteria: non modifica le impostazioni del sistema.',
    settingsTitle: 'Impostazioni', voice: 'Lumi parla ad alta voce', voiceHint: 'Usa una voce femminile del telefono, se presente. Tocca Lumi o «Parla» per attivarla.',
    install: 'Installa sul telefono', installHint: 'Se hai aperto la pagina dalla fotocamera, premi ⋮ → Apri in Chrome. Poi ⋮ → «Aggiungi a schermata Home» / «Installa app».',
    noApk: 'Demo: né la PWA né l’APK puliscono Android. Le funzioni reali sono su Windows.',
    demoAviso: 'Attenzione: è una demo. Non pulisce né cancella nulla su Android; le funzioni reali sono nella versione Windows.',
    sinVoz: 'Lumi ti ha scritto il messaggio: il telefono non ha voci installate.',
    openWeb: 'Vedi il sito completo', apkDownload: 'Scarica l’APK', github: 'Codice su GitHub', windowsVersion: 'Versione per Windows',
    credits: 'Lumen deriva da RetroLimpia di Retropunk94 · GPL-3.0-or-later · Non è la versione ufficiale.',
    tabInicio: 'Inizio', tabLimpieza: 'Pulizia', tabArranque: 'Avvio', tabPrivacidad: 'Privacy',
    tabBateria: 'Batteria', tabAjustes: 'Impostazioni',
    lumiHola: 'Ciao! Sono Lumi. Toccami quando vuoi parlare.',
    lumi1: 'Sono qui, controllo che tutto brilli.',
    lumi2: 'Non cancello nulla davvero: è una dimostrazione.',
    lumi3: 'Vuoi che controlliamo insieme il tuo telefono?',
    lumiScan: 'Sto guardando… tranquillo, non cambio niente.',
    lumiDone: 'Fatto. Ho trovato alcune cose da pulire.',
    lumiDemo: 'Questa è solo una demo: il tuo telefono è al sicuro.',
    instalado: 'Si è aperta la finestra di installazione.'
  },
  en: {
    demoChip: '🛡️ Demo · does not clean your phone', language: 'Language', greeting: "🛡️ Hi! I'm Lumi",
    subtitle: "Tap to see a simulation. I don't delete or change anything on your phone.",
    scanBtn: 'Run demo scan', talkBtn: 'Talk to Lumi', estadoInicial: 'Ready when you are.',
    escaneando: 'Checking… I am not touching your phone.', listo: "Done. I've got the report.",
    doneTitle: "Simulation: here's what Lumen found", itemsCleaned: 'items cleaned', spaceFreed: 'space freed',
    programas: 'apps reviewed', demoWarning: 'Demo mode: sample data, nothing is deleted.',
    resumenTitle: 'What Lumen would do on Windows', resumen1: "Remove files you don't need",
    resumen2: 'Remove leftover programs completely', resumen3: 'Turn off ads and tracking',
    resumen4: 'Get your device ready for gaming', cleanTitle: 'Cleanup', cleanDesc: 'Files you can remove with one tap (demo).',
    itemCache: 'App cache', itemTemp: 'Temporary files', itemThumbs: 'Old thumbnails',
    itemLogs: 'Old logs', cleanBtn: 'Clean selected', bootTitle: 'Startup',
    bootDesc: 'Programs that open on their own (demo).', boot1: 'Background sync',
    boot2: 'Auto updater', boot3: 'Cloud shortcut', recomendado: 'Recommended',
    importante: 'Important', opcional: 'Optional', applyBtn: 'Apply changes', privacyTitle: 'Privacy',
    privacyDesc: 'Settings explained in plain words (demo).', privacy1: 'Personalized ads',
    privacy2: 'Background location', privacy3: 'Diagnostic log', off: 'Off',
    privacyNote: 'Lumen sends no data: no telemetry, no ads.',
    anunciosTitle: 'Ads and tracking', anunciosDesc: 'Real blocking inside Lumen: no ad scripts or connections.',
    bloquearAnuncios: 'Block ads in Lumen', bloquearAnunciosHint: 'Applied with a content policy and a service worker filter. It does not affect other apps.',
    anunciosActivo: 'Blocking active inside Lumen', anunciosInactivo: 'Blocking off inside Lumen',
    probarBloqueo: 'Test the block', bloqueoProbado: 'Block verified: Lumen loads no ads.',
    anunciosAviso: 'A PWA cannot remove ads from the browser or other apps: only the system or the matching app can do that.',
    bateriaTitle: 'Battery', bateriaDesc: 'Phone status and tips to take care of it.',
    bateriaNoDisp: 'Status unavailable', bateriaHint: 'Browsers may not show the exact level.',
    bateriaCargando: 'Charging', bateriaDescargando: 'On battery', bateriaBaja: 'Low battery: power saving suggested',
    ahorro: 'Battery saver mode', ahorroHint: 'Reduces animations, blinking and background work. It also turns on automatically on low battery or when the app is in the background.',
    ahorroActivo: 'Power saving on', ahorroOff: 'Power saving off',
    consejosTitle: "Lumi's tips", consejo1: 'Lower brightness or use dark mode.',
    consejo2: 'Turn off sync you do not use.', consejo3: 'Limiting location saves battery.',
    consejo4: 'Avoid leaving heavy games in the background.',
    bateriaAviso: 'Lumen only reads battery status: it does not change system settings.',
    settingsTitle: 'Settings', voice: 'Let Lumi speak out loud', voiceHint: "Uses a female voice from your phone when available. Tap Lumi or 'Talk' to activate it.",
    install: 'Install on phone', installHint: "If you opened it from the camera, tap ⋮ → Open in Chrome. Then ⋮ → 'Add to Home screen' / 'Install app'.",
    noApk: 'Demo: neither the PWA nor the APK clean Android. Real features are on Windows.',
    demoAviso: 'Notice: this is a demo. It does not clean or delete anything on Android; real features are in the Windows version.',
    sinVoz: 'Lumi wrote the message: your phone has no installed voices.',
    openWeb: 'Open full website', apkDownload: 'Download the APK', github: 'Code on GitHub', windowsVersion: 'Windows version',
    credits: 'Lumen is based on RetroLimpia by Retropunk94 · GPL-3.0-or-later · Not the official version.',
    tabInicio: 'Home', tabLimpieza: 'Cleanup', tabArranque: 'Startup', tabPrivacidad: 'Privacy',
    tabBateria: 'Battery', tabAjustes: 'Settings',
    lumiHola: "Hi! I'm Lumi. Tap me whenever you want to chat.",
    lumi1: "I'm here, making sure everything shines.",
    lumi2: "I don't really delete anything: this is a demo.",
    lumi3: 'Shall we check your phone together?',
    lumiScan: "Taking a look… don't worry, I change nothing.",
    lumiDone: 'Done. I found a few things that could be cleaned.',
    lumiDemo: 'This is just a demo: your phone is safe.',
    instalado: 'The install dialog was opened.'
  }
};

const $ = (q) => document.querySelector(q);
const $$ = (q) => Array.from(document.querySelectorAll(q));
const idiomas = { es: 'es-ES', it: 'it-IT', en: 'en-US' };
const CLAVES_AUDIO = new Set([
  'lumiHola', 'lumi1', 'lumi2', 'lumi3', 'lumiScan', 'lumiDone', 'lumiDemo',
  'anunciosActivo', 'anunciosInactivo', 'bloqueoProbado', 'ahorroActivo', 'ahorroOff', 'sinVoz'
]);
let audioActual = null;
let lang = localStorage.getItem('lumen-idioma') || (navigator.language || '').slice(0, 2);
if (!D[lang]) lang = 'es';
let burbujaTimer = null;
let promptInstalar = null;
let blinkTimer = null;
let ahorroManual = localStorage.getItem('lumen-ahorro') === '1';
const bateriaInfo = { supported: false, level: 1, charging: true };
let bateria = null;

function t(k) { return (D[lang] && D[lang][k]) || D.es[k] || k; }

function ahorroActivo() {
  return ahorroManual || document.hidden || (bateriaInfo.supported && !bateriaInfo.charging && bateriaInfo.level <= 0.2);
}

function aplicarAhorro() { document.body.classList.toggle('ahorro', ahorroActivo()); }

function aplicarIdioma() {
  document.documentElement.lang = lang;
  $$('[data-i18n]').forEach((el) => { el.textContent = t(el.dataset.i18n); });
  $('#idioma').value = lang;
  $('#idioma2').value = lang;
  localStorage.setItem('lumen-idioma', lang);
  const chkAnuncios = $('#bloqueoAnuncios');
  if (chkAnuncios && $('#estadoBloqueo')) {
    $('#estadoBloqueo').textContent = t(chkAnuncios.checked ? 'anunciosActivo' : 'anunciosInactivo');
  }
  pintarBateria();
}

function cambiarIdioma(l) { lang = D[l] ? l : 'es'; aplicarIdioma(); }

function toast(texto) {
  const el = $('#toast');
  el.textContent = texto;
  el.classList.add('ver');
  clearTimeout(el._t);
  el._t = setTimeout(() => el.classList.remove('ver'), 2800);
}

function elegirVoz() {
  if (!('speechSynthesis' in window)) return null;
  const voces = speechSynthesis.getVoices();
  const prefijo = idiomas[lang].slice(0, 2).toLowerCase();
  const delIdioma = voces.filter((v) => v.lang.toLowerCase().startsWith(prefijo));
  const femeninas = ['female', 'femin', 'mujer', 'woman', 'samantha', 'victoria', 'zira', 'karen', 'moira', 'tessa', 'fiona', 'serena', 'allison', 'ava', 'susan', 'lucia', 'paola', 'chiara'];
  return delIdioma.find((v) => femeninas.some((n) => v.name.toLowerCase().includes(n)))
    || delIdioma[0]
    || null;
}

function hablarLimpio(texto) {
  // Los motores TTS leen emoji y comillas como palabras raras; Lumen los muestra pero no los pronuncia.
  return texto.replace(/[^\p{L}\p{N}\s¿?¡!.,;:-]/gu, ' ').replace(/\s+/g, ' ').trim();
}

function rutaAudio(texto) {
  if (!D[lang]) return null;
  for (const clave of CLAVES_AUDIO) {
    if (D[lang][clave] === texto) return `audio/${lang}-${clave}.wav`;
  }
  return null;
}

function hablarTts(texto, lumi) {
  if (!('speechSynthesis' in window)) { toast(t('sinVoz')); return; }
  try {
    speechSynthesis.cancel();
    const u = new SpeechSynthesisUtterance(hablarLimpio(texto));
    u.lang = idiomas[lang];
    u.rate = 0.98;
    const voces = speechSynthesis.getVoices();
    const voz = elegirVoz();
    if (voz) { u.voice = voz; u.lang = voz.lang; }
    u.pitch = 1.15;
    u.onend = () => lumi.classList.remove('hablando');
    speechSynthesis.speak(u);
    if (!voces.length) toast(t('sinVoz'));
  } catch (_) { toast(t('sinVoz')); }
}

function hablar(texto, ms, forzarVoz) {
  const b = $('#burbuja');
  const lumi = $('#lumi');
  const duracion = ms || (ahorroActivo() ? 2400 : 3600);
  b.textContent = texto;
  b.hidden = false;
  lumi.classList.add('hablando');
  clearTimeout(burbujaTimer);
  burbujaTimer = setTimeout(() => { b.hidden = true; lumi.classList.remove('hablando'); }, duracion);

  const quiereVoz = $('#voz').checked || forzarVoz;
  if (!quiereVoz) return;
  if (audioActual) { audioActual.pause(); audioActual = null; }

  const ruta = rutaAudio(texto);
  if (ruta) {
    const audio = new Audio(ruta);
    audioActual = audio;
    audio.onended = () => { lumi.classList.remove('hablando'); audioActual = null; };
    const usarTts = () => { audioActual = null; hablarTts(texto, lumi); };
    audio.onerror = usarTts;
    audio.play().catch(usarTts);
    return;
  }
  hablarTts(texto, lumi);
}

function parpadear() {
  if (ahorroActivo() || document.hidden) return;
  const l = $('#lumi');
  l.classList.add('parpadeo');
  setTimeout(() => l.classList.remove('parpadeo'), 220);
}

function gestionarParpadeo() {
  if (document.hidden) {
    clearInterval(blinkTimer);
    blinkTimer = null;
  } else if (!blinkTimer) {
    blinkTimer = setInterval(parpadear, 5200);
  }
}

function fraseAleatoria() {
  const claves = ['lumiHola', 'lumi1', 'lumi2', 'lumi3', 'lumiDemo'];
  return t(claves[Math.floor(Math.random() * claves.length)]);
}

function inicializarTabs() {
  $$('.bottom button').forEach((btn) => btn.addEventListener('click', () => {
    $$('.bottom button').forEach((b) => b.classList.remove('activo'));
    btn.classList.add('activo');
    $$('.tab').forEach((s) => s.classList.remove('activa'));
    $('#tab-' + btn.dataset.tab).classList.add('activa');
    window.scrollTo({ top: 0, behavior: ahorroActivo() ? 'auto' : 'smooth' });
  }));
}

function inicializarLumi() {
  const l = $('#lumi');
  const pupilas = l.querySelector('.pupilas');
  const hablarAhora = () => hablar(fraseAleatoria(), null, true);

  l.addEventListener('pointermove', (e) => {
    if (ahorroActivo()) return;
    const r = l.getBoundingClientRect();
    const dx = Math.max(-6, Math.min(6, ((e.clientX - (r.left + r.width / 2)) / r.width) * 14));
    const dy = Math.max(-5, Math.min(5, ((e.clientY - (r.top + r.height / 2)) / r.height) * 12));
    pupilas.style.transform = `translate(${dx}px, ${dy}px)`;
  });
  l.addEventListener('pointerleave', () => { pupilas.style.transform = ''; });
  l.addEventListener('click', hablarAhora);
  l.addEventListener('keydown', (e) => { if (e.key === 'Enter' || e.key === ' ') { e.preventDefault(); hablarAhora(); } });
  $('#hablar').addEventListener('click', hablarAhora);

  if (!ahorroActivo()) setTimeout(() => hablar(t('lumiHola'), 4200), 900);
}

function inicializarDemo() {
  const boton = $('#escanear');
  boton.addEventListener('click', () => {
    if (boton.disabled) return;
    boton.disabled = true;
    $('#resultado').hidden = true;
    $('#progreso').hidden = false;
    $('#barra').style.width = '0%';
    $('#estado').textContent = t('escaneando');
    $('#lumi').classList.add('scanning');
    hablar(t('lumiScan'), 2600);

    const retardo = ahorroActivo() ? 320 : 130;
    const salto = ahorroActivo() ? 14 : 4;
    let p = 0;
    const tmr = setInterval(() => {
      if (document.hidden) return;
      p += salto + Math.floor(Math.random() * 7);
      if (p >= 100) {
        p = 100;
        clearInterval(tmr);
        setTimeout(() => {
          $('#lumi').classList.remove('scanning');
          $('#lumi').classList.add('happy');
          $('#estado').textContent = t('listo');
          $('#nArchivos').textContent = String(18 + Math.floor(Math.random() * 40));
          $('#nEspacio').textContent = (80 + Math.floor(Math.random() * 260)) + ' MB';
          $('#nApps').textContent = String(30 + Math.floor(Math.random() * 50));
          $('#resultado').hidden = false;
          boton.disabled = false;
          hablar(t('lumiDone'), 4200);
          setTimeout(() => $('#lumi').classList.remove('happy'), 1200);
        }, 350);
      }
      $('#barra').style.width = p + '%';
    }, retardo);
  });

  $$('[data-demo]').forEach((b) => b.addEventListener('click', () => {
    toast(t('demoWarning'));
    hablar(t('lumiDemo'));
  }));
}

function pintarBateria() {
  const nivel = $('#bateriaNivel');
  const barra = $('#bateriaBarra');
  const detalle = $('#bateriaDetalle');
  if (!nivel || !barra || !detalle) return;
  if (!bateriaInfo.supported) {
    nivel.textContent = t('bateriaNoDisp');
    detalle.textContent = t('bateriaHint');
    barra.style.width = '0%';
    return;
  }
  const pct = Math.round(bateriaInfo.level * 100);
  nivel.textContent = pct + '%';
  barra.style.width = pct + '%';
  detalle.textContent = bateriaInfo.charging ? t('bateriaCargando') : (pct <= 20 ? t('bateriaBaja') : t('bateriaDescargando'));
}

async function inicializarBateria() {
  const chk = $('#ahorro');
  chk.checked = ahorroManual;
  chk.addEventListener('change', () => {
    ahorroManual = chk.checked;
    localStorage.setItem('lumen-ahorro', ahorroManual ? '1' : '0');
    aplicarAhorro();
    gestionarParpadeo();
    toast(t(ahorroActivo() ? 'ahorroActivo' : 'ahorroOff'));
  });

  if (navigator.getBattery) {
    try {
      bateria = await navigator.getBattery();
      bateriaInfo.supported = true;
      const actualizar = () => {
        bateriaInfo.level = bateria.level;
        bateriaInfo.charging = bateria.charging;
        pintarBateria();
        aplicarAhorro();
      };
      ['levelchange', 'chargingchange', 'chargingtimechange', 'dischargingtimechange'].forEach((ev) => bateria.addEventListener(ev, actualizar));
      actualizar();
    } catch (_) { pintarBateria(); }
  } else { pintarBateria(); }
  aplicarAhorro();
}

function inicializarInstalacion() {
  window.addEventListener('beforeinstallprompt', (e) => { e.preventDefault(); promptInstalar = e; });
  $('#instalar').addEventListener('click', async () => {
    if (promptInstalar) {
      await promptInstalar.prompt();
      await promptInstalar.userChoice;
      promptInstalar = null;
      toast(t('instalado'));
    } else {
      toast(t('installHint'));
      hablar(t('installHint'), 5000);
    }
  });
  window.addEventListener('appinstalled', () => toast(t('instalado')));
}

function configurarSW() {
  if (!('serviceWorker' in navigator)) return;
  navigator.serviceWorker.ready.then((reg) => {
    if (reg.active) reg.active.postMessage({ tipo: 'config', bloquearAnuncios: $('#bloqueoAnuncios').checked });
  }).catch(() => {});
}

function inicializarAnuncios() {
  const chk = $('#bloqueoAnuncios');
  const estado = $('#estadoBloqueo');
  chk.checked = localStorage.getItem('lumen-anuncios') !== '0';
  estado.textContent = t(chk.checked ? 'anunciosActivo' : 'anunciosInactivo');

  chk.addEventListener('change', () => {
    localStorage.setItem('lumen-anuncios', chk.checked ? '1' : '0');
    estado.textContent = t(chk.checked ? 'anunciosActivo' : 'anunciosInactivo');
    configurarSW();
    toast(t(chk.checked ? 'anunciosActivo' : 'anunciosInactivo'));
    hablar(t(chk.checked ? 'anunciosActivo' : 'anunciosInactivo'));
  });

  $('#probarBloqueo').addEventListener('click', () => {
    toast(t('bloqueoProbado'));
    hablar(t('bloqueoProbado'));
  });
  configurarSW();
}

function registrarSW() {
  if ('serviceWorker' in navigator) {
    navigator.serviceWorker.register('../sw.js').then(configurarSW).catch(() => {});
  }
}

document.addEventListener('DOMContentLoaded', () => {
  aplicarIdioma();
  $$('#idioma,#idioma2').forEach((s) => s.addEventListener('change', () => cambiarIdioma(s.value)));
  $('#voz').checked = localStorage.getItem('lumen-voz') === '1';
  $('#voz').addEventListener('change', () => localStorage.setItem('lumen-voz', $('#voz').checked ? '1' : '0'));
  document.addEventListener('visibilitychange', () => { aplicarAhorro(); gestionarParpadeo(); });
  inicializarTabs();
  inicializarLumi();
  inicializarDemo();
  inicializarBateria();
  inicializarAnuncios();
  inicializarInstalacion();
  gestionarParpadeo();
  registrarSW();
});
