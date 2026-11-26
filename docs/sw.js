/* Lumen PWA: caché offline y filtro de publicidad dentro de la app. */
const CACHE = 'lumen-app-v3-0-0';
// El scope del service worker es /Lumen/ en GitHub Pages y / en una prueba local.
const BASE = self.registration.scope;
const RUTAS = [
  '', 'app/', 'app/index.html', 'app/styles.css', 'app/app.js',
  'manifest.webmanifest', 'img/pwa-192.png', 'img/pwa-512.png',
  'img/pwa-maskable-512.png', 'img/icon-180.png', 'img/audiowide.woff2',
  'img/qr-android-pwa.png', 'logo.svg', 'index.html'
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
