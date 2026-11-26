/* Lumen PWA: caché offline y filtro de publicidad dentro de la app. */
const CACHE = 'lumen-app-v20-0-0';
// El scope del service worker es /Lumen/ en GitHub Pages y / en una prueba local.
const BASE = self.registration.scope;
const RUTAS = [
  '', 'app/', 'app/index.html', 'app/styles.css', 'app/app.js',
  'manifest.webmanifest', 'img/pwa-192.png', 'img/pwa-512.png',
  'img/pwa-maskable-512.png', 'img/icon-180.png', 'img/audiowide.woff2',
  'img/qr-android-pwa.png', 'img/qr-android-apk.png', 'img/qr-android-seguridad.png', 'img/qr-descarga-android.png', 'logo.svg', 'index.html',
  'SEGURIDAD-ANDROID.md', 'GOOGLE-PLAY.md', 'PLAY-FICHA.md', 'ANDROID.md', 'PRIVACIDAD.md',
  'app/audio/en-ahorroActivo.mp3', 'app/audio/en-ahorroOff.mp3', 'app/audio/en-anunciosActivo.mp3',
  'app/audio/en-anunciosInactivo.mp3', 'app/audio/en-bloqueoProbado.mp3', 'app/audio/en-lumi1.mp3',
  'app/audio/en-lumi2.mp3', 'app/audio/en-lumi3.mp3', 'app/audio/en-lumiDemo.mp3', 'app/audio/en-lumiDone.mp3',
  'app/audio/en-lumiHola.mp3', 'app/audio/en-lumiScan.mp3', 'app/audio/en-sinVoz.mp3',
  'app/audio/es-ahorroActivo.mp3', 'app/audio/es-ahorroOff.mp3', 'app/audio/es-anunciosActivo.mp3',
  'app/audio/es-anunciosInactivo.mp3', 'app/audio/es-bloqueoProbado.mp3', 'app/audio/es-lumi1.mp3',
  'app/audio/es-lumi2.mp3', 'app/audio/es-lumi3.mp3', 'app/audio/es-lumiDemo.mp3', 'app/audio/es-lumiDone.mp3',
  'app/audio/es-lumiHola.mp3', 'app/audio/es-lumiScan.mp3', 'app/audio/es-sinVoz.mp3',
  'app/audio/it-ahorroActivo.mp3', 'app/audio/it-ahorroOff.mp3', 'app/audio/it-anunciosActivo.mp3',
  'app/audio/it-anunciosInactivo.mp3', 'app/audio/it-bloqueoProbado.mp3', 'app/audio/it-lumi1.mp3',
  'app/audio/it-lumi2.mp3', 'app/audio/it-lumi3.mp3', 'app/audio/it-lumiDemo.mp3', 'app/audio/it-lumiDone.mp3',
  'app/audio/it-lumiHola.mp3', 'app/audio/it-lumiScan.mp3', 'app/audio/it-sinVoz.mp3'
];
const ARCHIVOS = RUTAS.map((r) => new URL(r, BASE).href);

let bloquearAnuncios = true;
const ANUNCIOS = /(^|\.)(doubleclick\.net|googleadservices\.com|googlesyndication\.com|adservice\.google\.[a-z.]+|google-analytics\.com|analytics\.google\.com|googletagmanager\.com|connect\.facebook\.net|facebook\.net|amazon-adsystem\.com|adnxs\.com|criteo\.com|taboola\.com|outbrain\.com|scorecardresearch\.com|quantserve\.com|pubmatic\.com|rubiconproject\.com|openx\.net)(:|$|\/)/i;

self.addEventListener('message', (e) => {
  if (e.data && e.data.tipo === 'config') bloquearAnuncios = e.data.bloquearAnuncios !== false;
});

self.addEventListener('install', (e) => {
  e.waitUntil(caches.open(CACHE).then((c) => c.addAll(ARCHIVOS)).then(() => self.skipWaiting()));
});

self.addEventListener('activate', (e) => {
  e.waitUntil(caches.keys().then((keys) => Promise.all(keys.filter((k) => k !== CACHE).map((k) => caches.delete(k)))).then(() => self.clients.claim()));
});

self.addEventListener('fetch', (e) => {
  const req = e.request;
  if (req.method !== 'GET') return;
  const url = new URL(req.url);

  if (bloquearAnuncios && ANUNCIOS.test(url.hostname)) {
    e.respondWith(new Response('', { status: 204, statusText: 'Bloqueado por Lumen' }));
    return;
  }
  if (url.origin !== self.location.origin) return;

  e.respondWith(
    caches.match(req).then((guardado) => {
      const red = fetch(req).then((res) => {
        if (res && res.status === 200) {
          const copia = res.clone();
          caches.open(CACHE).then((c) => c.put(req, copia));
        }
        return res;
      }).catch(() => guardado);
      return guardado || red;
    })
  );
});
