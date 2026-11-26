/* Lumen PWA — demo interactiva con Lumi. No modifica el dispositivo. */
'use strict';

const D = {
  es: {
    demoChip: 'Versión demo · Android', language: 'Idioma', greeting: '¡Hola! Soy Lumi 👋',
    subtitle: 'Pulsa el botón para ver cómo funciona Lumen. No cambio nada de tu móvil.',
    scanBtn: 'Revisar mi Android', talkBtn: '💬 Hablar con Lumi', estadoInicial: 'Preparado cuando quieras.',
    escaneando: 'Revisando… no toco nada de tu móvil.', listo: '¡Listo! Ya tengo el informe.',
    doneTitle: 'Esto es lo que Lumen encontró', itemsCleaned: 'elementos limpios', spaceFreed: 'espacio liberado',
    programas: 'programas revisados', demoWarning: 'Modo demostración: datos de ejemplo, sin borrar nada.',
    resumenTitle: 'Lo que Lumen haría por ti', resumen1: 'Quitar archivos que ya no sirven',
    resumen2: 'Quitar programas que sobran sin dejar restos', resumen3: 'Apagar anuncios y rastreos',
    resumen4: 'Preparar el equipo para jugar', cleanTitle: 'Limpieza', cleanDesc: 'Archivos que se pueden quitar con un toque (demo).',
    itemCache: 'Caché de aplicaciones', itemTemp: 'Archivos temporales', itemThumbs: 'Miniaturas antiguas',
    itemLogs: 'Registros antiguos', cleanBtn: 'Limpiar seleccionado', bootTitle: 'Arranque',
    bootDesc: 'Programas que se abren solos (demo).', boot1: 'Sincronización en segundo plano',
    boot2: 'Actualizador automático', boot3: 'Atajo de la nube', recomendado: 'Recomendado',
    importante: 'Importante', opcional: 'Opcional', applyBtn: 'Aplicar cambios', privacyTitle: 'Privacidad',
    privacyDesc: 'Ajustes explicados con palabras sencillas (demo).', privacy1: 'Anuncios personalizados',
    privacy2: 'Ubicación en segundo plano', privacy3: 'Registro de diagnóstico', off: 'Apagado',
    privacyNote: 'Lumen no envía datos: no hay telemetría ni anuncios.', settingsTitle: 'Ajustes',
    voice: 'Que Lumi hable en voz alta', voiceHint: 'Usa la voz de tu móvil. Necesitas pulsar a Lumi para activarla.',
    install: 'Instalar en el móvil', installHint: 'Abre esta página en Chrome → menú → «Añadir a pantalla de inicio».',
    openWeb: 'Ver la web completa', github: 'Código en GitHub', windowsVersion: 'Versión para Windows',
    credits: 'Lumen se basa en RetroLimpia, de Retropunk94 · GPL-3.0-or-later · No es la versión oficial.',
    tabInicio: 'Inicio', tabLimpieza: 'Limpieza', tabArranque: 'Arranque', tabPrivacidad: 'Privacidad', tabAjustes: 'Ajustes',
    lumiHola: '¡Hola! Soy Lumi. Tócame cuando quieras charlar.',
    lumi1: 'Aquí estoy, vigilando que todo brille. ✨',
    lumi2: 'No borro nada de verdad: esto es una demostración.',
    lumi3: '¿Quieres que revisemos tu móvil juntos?',
    lumiScan: 'Estoy mirando… tranquilo, no cambio nada.',
    lumiDone: 'Listo. Encontré cosas que se podrían limpiar.',
    lumiDemo: 'Esto es solo una demo: tu móvil está a salvo.',
    instalado: 'Se abrió el diálogo de instalación.'
  },
  it: {
    demoChip: 'Versione demo · Android', language: 'Lingua', greeting: 'Ciao! Sono Lumi 👋',
    subtitle: 'Premi il pulsante per vedere come funziona Lumen. Non cambio nulla sul tuo telefono.',
    scanBtn: 'Controlla il mio Android', talkBtn: '💬 Parla con Lumi', estadoInicial: 'Pronto quando vuoi.',
    escaneando: 'Sto controllando… non tocco niente del tuo telefono.', listo: 'Fatto! Ho il report.',
    doneTitle: 'Ecco cosa ha trovato Lumen', itemsCleaned: 'elementi puliti', spaceFreed: 'spazio liberato',
    programas: 'programmi controllati', demoWarning: 'Modalità demo: dati di esempio, nessuna cancellazione.',
    resumenTitle: 'Cosa farebbe Lumen per te', resumen1: 'Rimuovere i file inutili',
    resumen2: 'Disinstallare i programmi in eccesso senza residui', resumen3: 'Disattivare annunci e tracciamento',
    resumen4: 'Preparare il dispositivo per il gioco', cleanTitle: 'Pulizia', cleanDesc: 'File che si possono rimuovere con un tocco (demo).',
    itemCache: 'Cache delle app', itemTemp: 'File temporanei', itemThumbs: 'Miniature vecchie',
    itemLogs: 'Registri vecchi', cleanBtn: 'Pulisci la selezione', bootTitle: 'Avvio',
    bootDesc: 'Programmi che si aprono da soli (demo).', boot1: 'Sincronizzazione in background',
    boot2: 'Aggiornamento automatico', boot3: 'Scorciatoia del cloud', recomendado: 'Consigliato',
    importante: 'Importante', opcional: 'Opzionale', applyBtn: 'Applica le modifiche', privacyTitle: 'Privacy',
    privacyDesc: 'Impostazioni spiegate con parole semplici (demo).', privacy1: 'Annunci personalizzati',
    privacy2: 'Posizione in background', privacy3: 'Registro diagnostico', off: 'Disattivato',
    privacyNote: 'Lumen non invia dati: nessuna telemetria né annunci.', settingsTitle: 'Impostazioni',
    voice: 'Lumi parla ad alta voce', voiceHint: 'Usa la voce del telefono. Devi toccare Lumi per attivarla.',
    install: 'Installa sul telefono', installHint: 'Apri questa pagina in Chrome → menu → «Aggiungi a schermata Home».',
    openWeb: 'Vedi il sito completo', github: 'Codice su GitHub', windowsVersion: 'Versione per Windows',
    credits: 'Lumen deriva da RetroLimpia di Retropunk94 · GPL-3.0-or-later · Non è la versione ufficiale.',
    tabInicio: 'Inizio', tabLimpieza: 'Pulizia', tabArranque: 'Avvio', tabPrivacidad: 'Privacy', tabAjustes: 'Impostazioni',
    lumiHola: 'Ciao! Sono Lumi. Toccami quando vuoi parlare.',
    lumi1: 'Sono qui, controllo che tutto brilli. ✨',
    lumi2: 'Non cancello nulla davvero: è una dimostrazione.',
    lumi3: 'Vuoi che controlliamo insieme il tuo telefono?',
    lumiScan: 'Sto guardando… tranquillo, non cambio niente.',
    lumiDone: 'Fatto. Ho trovato alcune cose da pulire.',
    lumiDemo: 'Questa è solo una demo: il tuo telefono è al sicuro.',
    instalado: 'Si è aperta la finestra di installazione.'
  },
  en: {
    demoChip: 'Demo version · Android', language: 'Language', greeting: "Hi! I'm Lumi 👋",
    subtitle: "Press the button to see how Lumen works. I won't change anything on your phone.",
    scanBtn: 'Check my Android', talkBtn: '💬 Talk to Lumi', estadoInicial: 'Ready when you are.',
    escaneando: 'Checking… I am not touching your phone.', listo: "Done! I've got the report.",
    doneTitle: "Here's what Lumen found", itemsCleaned: 'items cleaned', spaceFreed: 'space freed',
    programas: 'apps reviewed', demoWarning: 'Demo mode: sample data, nothing is deleted.',
    resumenTitle: 'What Lumen would do for you', resumen1: "Remove files you don't need",
    resumen2: 'Remove leftover programs completely', resumen3: 'Turn off ads and tracking',
    resumen4: 'Get your device ready for gaming', cleanTitle: 'Cleanup', cleanDesc: 'Files you can remove with one tap (demo).',
    itemCache: 'App cache', itemTemp: 'Temporary files', itemThumbs: 'Old thumbnails',
    itemLogs: 'Old logs', cleanBtn: 'Clean selected', bootTitle: 'Startup',
    bootDesc: 'Programs that open on their own (demo).', boot1: 'Background sync',
    boot2: 'Auto updater', boot3: 'Cloud shortcut', recomendado: 'Recommended',
    importante: 'Important', opcional: 'Optional', applyBtn: 'Apply changes', privacyTitle: 'Privacy',
    privacyDesc: 'Settings explained in plain words (demo).', privacy1: 'Personalized ads',
    privacy2: 'Background location', privacy3: 'Diagnostic log', off: 'Off',
    privacyNote: 'Lumen sends no data: no telemetry, no ads.', settingsTitle: 'Settings',
    voice: 'Let Lumi speak out loud', voiceHint: "Uses your phone's voice. Tap Lumi to activate it.",
    install: 'Install on phone', installHint: "Open this page in Chrome → menu → 'Add to Home screen'.",
    openWeb: 'Open full website', github: 'Code on GitHub', windowsVersion: 'Windows version',
    credits: 'Lumen is based on RetroLimpia by Retropunk94 · GPL-3.0-or-later · Not the official version.',
    tabInicio: 'Home', tabLimpieza: 'Cleanup', tabArranque: 'Startup', tabPrivacidad: 'Privacy', tabAjustes: 'Settings',
    lumiHola: "Hi! I'm Lumi. Tap me whenever you want to chat.",
    lumi1: "I'm here, making sure everything shines. ✨",
    lumi2: "I don't really delete anything: this is a demo.",
    lumi3: 'Shall we check your phone together?',
    lumiScan: 'Taking a look… don\'t worry, I change nothing.',
    lumiDone: 'Done. I found a few things that could be cleaned.',
    lumiDemo: 'This is just a demo: your phone is safe.',
    instalado: 'The install dialog was opened.'
  }
};

const $ = (q) => document.querySelector(q);
const $$ = (q) => Array.from(document.querySelectorAll(q));
const idiomas = { es: 'es-ES', it: 'it-IT', en: 'en-US' };
let lang = localStorage.getItem('lumen-idioma') || (navigator.language || '').slice(0, 2);
if (!D[lang]) lang = 'es';
let burbujaTimer = null;
let promptInstalar = null;

function t(k) { return (D[lang] && D[lang][k]) || D.es[k] || k; }

function aplicarIdioma() {
  document.documentElement.lang = lang;
  $$('[data-i18n]').forEach((el) => { el.textContent = t(el.dataset.i18n); });
  $('#idioma').value = lang;
  $('#idioma2').value = lang;
  localStorage.setItem('lumen-idioma', lang);
}

function cambiarIdioma(l) { lang = D[l] ? l : 'es'; aplicarIdioma(); }

function toast(texto) {
  const el = $('#toast');
  el.textContent = texto;
  el.classList.add('ver');
  clearTimeout(el._t);
  el._t = setTimeout(() => el.classList.remove('ver'), 2800);
}

function hablar(texto, ms = 3600) {
  const b = $('#burbuja');
  const lumi = $('#lumi');
  b.textContent = texto;
  b.hidden = false;
  lumi.classList.add('hablando');
  clearTimeout(burbujaTimer);
  burbujaTimer = setTimeout(() => { b.hidden = true; lumi.classList.remove('hablando'); }, ms);

  if ($('#voz').checked && 'speechSynthesis' in window) {
    try {
      speechSynthesis.cancel();
      const u = new SpeechSynthesisUtterance(texto);
      u.lang = idiomas[lang];
      u.rate = 0.98;
      const voz = speechSynthesis.getVoices().find((v) => v.lang.toLowerCase().startsWith(lang));
      if (voz) u.voice = voz;
      speechSynthesis.speak(u);
    } catch (_) { /* el móvil puede no tener voces instaladas */ }
  }
}

function parpadear() {
  const l = $('#lumi');
  l.classList.add('parpadeo');
  setTimeout(() => l.classList.remove('parpadeo'), 220);
}
setInterval(parpadear, 5200);

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
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }));
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

    let p = 0;
    const tmr = setInterval(() => {
      p += 4 + Math.floor(Math.random() * 7);
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
    }, 130);
  });

  $$('[data-demo]').forEach((b) => b.addEventListener('click', () => {
    toast(t('demoWarning'));
    hablar(t('lumiDemo'));
  }));
}

function inicializarLumi() {
  const l = $('#lumi');
  const hablarAhora = () => { hablar(fraseAleatoria()); };
  l.addEventListener('click', hablarAhora);
  l.addEventListener('keydown', (e) => { if (e.key === 'Enter' || e.key === ' ') { e.preventDefault(); hablarAhora(); } });
  $('#hablar').addEventListener('click', hablarAhora);
  setTimeout(() => hablar(t('lumiHola'), 4200), 900);
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

function registrarSW() {
  if ('serviceWorker' in navigator) {
    navigator.serviceWorker.register('../sw.js').catch(() => {});
  }
}

document.addEventListener('DOMContentLoaded', () => {
  aplicarIdioma();
  $$('#idioma,#idioma2').forEach((s) => s.addEventListener('change', () => cambiarIdioma(s.value)));
  $('#voz').checked = localStorage.getItem('lumen-voz') === '1';
  $('#voz').addEventListener('change', () => localStorage.setItem('lumen-voz', $('#voz').checked ? '1' : '0'));
  inicializarTabs();
  inicializarLumi();
  inicializarDemo();
  inicializarInstalacion();
  registrarSW();
});
