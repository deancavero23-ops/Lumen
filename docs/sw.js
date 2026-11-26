/* Lumen PWA: caché de la app para que funcione sin conexión. */
const CACHE = 'lumen-app-v1-0-0';
// El scope del service worker es /Lumen/ en GitHub Pages y / en una prueba local.
const BASE = self.registration.scope;
const RUTAS = [
  '', 'app/', 'app/index.html', 'app/styles.css', 'app/app.js',
  'manifest.webmanifest', 'img/pwa-192.png', 'img/pwa-512.png',
  'img/pwa-maskable-512.png', 'img/icon-180.png', 'img/audiowide.woff2',
  'logo.svg', 'index.html'
];
const ARCHIVOS = RUTAS.map((r) => new URL(r, BASE).href);

self.addEventListener('install', (e) => {
  e.waitUntil(caches.open(CACHE).then((c) => c.addAll(ARCHIVOS)).then(() => self.skipWaiting()));
});

self.addEventListener('activate', (e) => {
  e.waitUntil(caches.keys().then((keys) => Promise.all(keys.filter((k) => k !== CACHE).map((k) => caches.delete(k)))).then(() => self.clients.claim()));
});

self.addEventListener('fetch', (e) => {
  const req = e.request;
  if (req.method !== 'GET' || new URL(req.url).origin !== self.location.origin) return;
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
