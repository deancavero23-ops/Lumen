# Ficha de Lumen Seguridad para Google Play 🛡️

Textos listos para copiar en Play Console. Mantienen el nombre **Lumen Seguridad**, el logo rojo con la L negra y chispa blanca, a Lumi y el emoji 🛡️. El AAB es [`Lumen-Seguridad-1.2.4-play.aab`](https://github.com/deancavero23-ops/Lumen/releases/download/play-v1.2.4/Lumen-Seguridad-1.2.4-play.aab) y la guía de permisos está en [GOOGLE-PLAY.md](GOOGLE-PLAY.md).

## Datos base

- **Título:** Lumen Seguridad
- **Categoría:** Herramientas (o Seguridad, si prefieres destacar el análisis)
- **Etiqueta:** Sin violencia, sin anuncios, sin compras dentro de la app
- **Package:** `com.lumen.seguridad`
- **Política de privacidad:** `https://deancavero23-ops.github.io/Lumen/PRIVACIDAD.md`
- **Sitio web:** `https://deancavero23-ops.github.io/Lumen/`
- **Contacto del desarrollador:** abre un issue en `https://github.com/deancavero23-ops/Lumen/issues` (no hay Discord ni servidor propio de reportes).
- **Accuracy / exactitud de la ficha:** describe solo lo que la app hace; no prometas eliminar virus ni limpiar otras apps.

## Descripción corta (80 caracteres máximo)

**Español**
> Analiza apps, limpia carpetas elegidas, bloquea anuncios y cuida tu batería.

**Italiano**
> Analizza app, pulisci le cartelle scelte, blocca gli annunci e la batteria.

**English**
> Scans apps, cleans chosen folders, blocks ads and watches your battery.

## Descripción completa

### Español

Lumen Seguridad es una segunda opinión local para tu Android. No sustituye a Play Protect: te muestra señales claras para que decidas tú, sin root, sin telemetría y sin servidor propio.

🔎 **Análisis de apps instaladas** — Calcula el SHA-256 de cada APK, lee su certificado firmante, el instalador y los permisos sensibles (SMS, contactos, ubicación, cámara, micrófono, accesibilidad...). Ordena los resultados por riesgo y abre el diálogo del sistema si pides desinstalar algo.

🧹 **Limpieza que tú controlas** — Elige una carpeta con el selector de Android. Lumen marca temporales, `.log`, archivos vacíos, APK antiguos y duplicados; los mueve a una cuarentena dentro de esa carpeta y los restaura con un toque. Nada se borra sin tu confirmación.

🛡️ **Bloqueo global de anuncios (opcional)** — Filtro DNS local con `VpnService`: a los dominios publicitarios responde `0.0.0.0` y reenvía el resto a un DNS público. Android pide permiso y tú puedes desactivarlo cuando quieras. No lee páginas, contraseñas ni mensajes.

🔋 **Batería real y Lumi** — Lee el nivel y el estado de carga con `BatteryManager`, y la mascota Lumi te habla con voces femeninas grabadas en español, italiano e inglés (con TTS de respaldo). Con **«Escribir a Lumi»** puedes hacerle preguntas y obtener respuestas locales sobre apps, archivos, anuncios, batería y reglas.

📄 **Reglas actualizables** — Desde Ajustes puedes descargar hashes y firmantes desde la Release pública de GitHub, con verificación SHA-256, o volver a las reglas incluidas. Nunca se descarga nada automáticamente.

Disponible en español, italiano e inglés. Funciona en Android 7.0 o superior. Gratis, de código abierto (GPL-3.0-or-later) y basado en RetroLimpia de Retropunk94. No es la versión oficial. Más información: https://deancavero23-ops.github.io/Lumen/

### Italiano

Lumen Seguridad es una seconda opinione locale per Android. Non sostituisce Play Protect: mostra segnali chiari e lasci decidere a te, senza root, telemetria o server propri.

🔎 **Analisi delle app installate** — Calcola lo SHA-256 di ogni APK, legge il certificato firmatario, l'installatore e i permessi sensibili (SMS, contatti, posizione, fotocamera, microfono, accessibilità...). Ordina per rischio e apre la finestra di sistema se chiedi di disinstallare.

🧹 **Pulizia sotto controllo** — Scegli una cartella con il selettore di Android. Lumen segnala temporanei, `.log`, file vuoti, APK vecchi e duplicati; li sposta in quarantena nella stessa cartella e li ripristina con un tocco. Nulla viene cancellato senza conferma.

🛡️ **Blocco annunci globale (opzionale)** — Filtro DNS locale con `VpnService`: ai domini pubblicitari risponde `0.0.0.0` e inoltra il resto a un DNS pubblico. Android chiede il permesso e puoi disattivarlo quando vuoi. Non legge pagine, password o messaggi.

🔋 **Batteria reale e Lumi** — Legge livello e stato di carica con `BatteryManager`; Lumi parla con voci femminili registrate in spagnolo, italiano e inglese (con TTS di riserva). Con **«Scrivi a Lumi»** puoi farle domande e ricevere risposte locali su app, file, annunci, batteria e regole.

📄 **Regole aggiornabili** — Da Impostazioni puoi scaricare hash e firmatari dalla Release pubblica su GitHub con verifica SHA-256, oppure tornare alle regole incluse. Nessun download automatico.

Disponibile in spagnolo, italiano e inglese. Android 7.0 o superiore. Gratis, open source (GPL-3.0-or-later) e basato su RetroLimpia di Retropunk94. Non è la versione ufficiale. Info: https://deancavero23-ops.github.io/Lumen/

### English

Lumen Seguridad is a local second opinion for Android. It does not replace Play Protect: it shows clear signals and leaves the decision to you, with no root, telemetry or Lumen server.

🔎 **Installed app analysis** — Computes each APK's SHA-256, reads its signing certificate, installer and sensitive permissions (SMS, contacts, location, camera, microphone, accessibility...). Sorts by risk and opens the system dialog if you request an uninstall.

🧹 **Cleanup you control** — Pick a folder with Android's picker. Lumen flags temporary files, `.log`, empty files, old APKs and duplicates; moves them to quarantine in that same folder and restores them with one tap. Nothing is deleted without your confirmation.

🛡️ **Optional global ad blocking** — Local DNS filter using `VpnService`: known ad domains get `0.0.0.0` and everything else is forwarded to a public resolver. Android asks for permission and you can switch it off anytime. It does not read pages, passwords or messages.

🔋 **Real battery info and Lumi** — Reads level and charging state with `BatteryManager`. Lumi speaks with recorded female voices in Spanish, Italian and English (TTS fallback). With **"Chat with Lumi"** you can type questions and get local answers about apps, files, ads, battery and rules.

📄 **Updatable rules** — In Settings you can download hashes and signers from the public GitHub Release with SHA-256 verification, or restore the bundled rules. Nothing is downloaded automatically.

Available in Spanish, Italian and English. Android 7.0 or newer. Free, open source (GPL-3.0-or-later) and based on RetroLimpia by Retropunk94. Not the official version. More info: https://deancavero23-ops.github.io/Lumen/

## Novedades de la versión 1.2.4 (cuadro «Qué hay de nuevo»)

**Español:** Actualizaciones automáticas: reglas verificadas con SHA-256 una vez al día y aviso de nueva versión, controlables en «🔄 Auto». Nuevo «💡 Consejos» con avisos reales de batería, temperatura y espacio. Escudo 🛡️ dibujado que abre el chat, voces femeninas que no leen emoji y borrado seguro con consejos antes de eliminar.

**Italiano:** Aggiornamenti automatici: regole verificate con SHA-256 una volta al giorno e avviso di nuova versione, gestibili in «🔄 Auto». Nuovi «💡 Consigli» con dati reali su batteria, temperatura e spazio. Scudo 🛡️ disegnato che apre la chat, voci femminili che non leggono gli emoji e cancellazione sicura con consigli prima di eliminare.

**English:** Automatic updates: SHA-256-verified rules once a day and a new-version alert, both controllable in "Auto". New "Advice" screen with real battery, temperature and storage warnings. Drawn 🛡️ shield opens the chat, female voices skip emoji, and safe deletion now shows advice before removing files.

## Material gráfico

- **Imagen de características (1024 × 500):** https://deancavero23-ops.github.io/Lumen/img/play-feature.png
- **Icono (512 × 512, PNG):** https://deancavero23-ops.github.io/Lumen/img/play-icon-512.png
- Ambos usan el logo rojo con la L negra y la chispa blanca, sobre negro/rojo/gris/blanco.

## Capturas de pantalla

Play pide al menos 2; recomiendo 4 o 5. **Deben ser capturas reales del móvil**, no montajes: abre la app instalada desde el AAB y captura estas pantallas con el selector del sistema (16:9, entre 320 y 3840 px de ancho):

1. Inicio con Lumi y el botón «Escanear apps instaladas».
2. Lista de apps analizadas con colores de riesgo.
3. Carpeta escaneada y archivos candidatos.
4. Cuarentena con un elemento restaurable.
5. Ajustes mostrando «Bloqueo global de anuncios» y «Actualizar reglas desde GitHub».

Si el móvil está en italiano o inglés, las capturas siguen siendo válidas: la app detecta el idioma del sistema y también se puede cambiar en Ajustes.

## Revisión: respuestas rápidas

- «¿La app recopila datos?» → No. Sin cuentas ni telemetría. El análisis se queda en el móvil.
- «¿Por qué pide ver todas las apps?» → Es su función principal: calcular hash, firmante y permisos de cada APK. Nada se sube.
- «¿La VPN lee mi tráfico?» → No. Es un filtro DNS local; el usuario da permiso y puede desactivarlo.
- «¿Sustituye a Play Protect?» → No. Es una segunda opinión local, no un antivirus en la nube.

## Créditos

Lumen se basa en [RetroLimpia](https://github.com/retropunk94/RetroLimpia), de Retropunk94 (GPL-3.0-or-later). No es la versión oficial. Lumen Seguridad es una implementación independiente y no contiene código de CCleaner. Repositorio: https://github.com/deancavero23-ops/Lumen
