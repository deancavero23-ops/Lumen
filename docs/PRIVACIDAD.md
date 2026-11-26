# Política de privacidad de Lumen 🛡️

**Última actualización: 26 de noviembre de 2026 · Aplica a Lumen Seguridad 1.2.4 y a la web/app de Lumen.**

Lumen respeta tu privacidad. No hay cuentas, no hay telemetría y no hay servidor propio de Lumen. Todo el análisis se hace en tu dispositivo. Lumi y su voz funcionan con archivos de audio locales.

## Qué datos procesa Lumen Seguridad

| Dato | Se queda en el dispositivo | Se envía a algún sitio |
|---|---|---|
| Lista de apps instaladas, SHA-256 de cada APK, certificado firmante y permisos | Sí, solo en pantalla mientras usas la app | **No**. Nunca se sube. |
| Archivos que eliges con el selector de carpetas (SAF) | Sí. La cuarentena se guarda en el almacenamiento de la propia app | **No**. |
| Nivel y estado de la batería | Sí, se lee con `BatteryManager` | **No**. |
| Consultas DNS cuando activas el bloqueo de anuncios | No se guardan | Solo se reenvían a un resolvedor público (Cloudflare `1.1.1.1` o Google `8.8.8.8`) **mientras la VPN está activa**. A los dominios publicitarios se les responde `0.0.0.0` localmente. |
| Reglas de seguridad | Se guardan en el almacenamiento privado de la app | Petición HTTPS anónima a GitHub Releases (`reglas.json` y su SHA-256), automática una vez al día si está activa o manual cuando pulsas **Actualizar reglas**. No se envía tu lista de apps. |
| Voz de Lumi | Archivos WAV incluidos en la app | **No**. Si faltan, se usa el TTS del sistema. |

No recopilamos ubicación, contactos, SMS, fotos, identificadores publicitarios ni historial de navegación. No mostramos anuncios dentro de la app nativa.

## Permisos y por qué los pide

- `QUERY_ALL_PACKAGES`: necesario para enumerar las apps instaladas y poder analizarlas. Es la función central del analizador. La lista no sale del móvil.
- `INTERNET`: para reenviar DNS permitido cuando tú activas la VPN y para comprobar reglas y versiones en GitHub (una vez al día si lo dejas activado en **🔄 Auto**, o cuando pulsas **Actualizar reglas**). Nunca se usa para telemetría.
- `FOREGROUND_SERVICE` y `FOREGROUND_SERVICE_SPECIAL_USE`: para mantener el filtro DNS con una notificación persistente mientras tú lo dejes encendido.
- Acceso a archivos: se concede **carpeta por carpeta** mediante el selector del sistema (SAF). No se pide `MANAGE_EXTERNAL_STORAGE`.
- VPN: Android muestra su propio diálogo. Si lo rechazas, el bloqueo no se activa. Puedes desactivarlo cuando quieras desde Ajustes o desde la notificación.

## Retención y seguridad

- Lumen no almacena tus datos en servidores propios, así que no hay plazos de retención ni copias de seguridad en la nube.
- Los archivos en cuarentena permanecen en tu dispositivo hasta que los restauras o los borras tú.
- La VPN es un filtro DNS local, no un túnel completo: no lee páginas web, contraseñas ni mensajes. No cubre DNS sobre TLS (DoT).
- El APK/AAB está firmado con una clave propia (`lumen-seguridad.keystore`). La huella SHA-256 de cada versión se publica en GitHub Releases para que puedas verificar la descarga.

## Terceros

- **Cloudflare / Google**: reciben consultas DNS transientemente solo si activas el bloqueo de anuncios. No están vinculadas a una identidad de Lumen y no hay acuerdo de compartir datos con ellos.
- **GitHub**: aloja el código, las Release y las páginas. Si abres un reporte, GitHub mostrará lo que tú escribas; no enviamos nada automáticamente.
- No hay SDK de publicidad, analítica, crash reporting ni compras dentro de la app.

## Niños

Lumen no está dirigido a menores de 13 años y no recopila datos de ellos de forma consciente.

## Cambios y contacto

Si cambia esta política, actualizaremos esta página y la versión de la app. Para preguntas o reportes, abre un issue en <https://github.com/deancavero23-ops/Lumen/issues>. No hay Discord, donaciones ni servidor propio de reportes.

## English summary

Lumen Seguridad processes everything on your device: installed app list, APK hashes, signing certificates, permissions, chosen folders and battery state are never uploaded. There are no accounts, telemetry or Lumen servers. The optional VPN is a local DNS filter: ad domains are answered with `0.0.0.0` and other DNS queries are forwarded to Cloudflare or Google only while the VPN is on. Rules update daily from GitHub Releases (opt-out in "Auto") after SHA-256 verification; a daily check also alerts you to new versions. No web traffic, passwords or messages are read. Contact: GitHub issues at the address above.

## Sintesi italiana

Lumen Seguridad elabora tutto sul dispositivo: elenco app installate, hash APK, certificati, autorizzazioni, cartelle scelte e batteria non vengono mai caricati. Non ci sono account, telemetria o server Lumen. La VPN opzionale è un filtro DNS locale: i domini pubblicitari ricevono `0.0.0.0` e le altre query vengono inoltrate a Cloudflare o Google solo con la VPN attiva. Le regole si aggiornano ogni giorno da GitHub Releases (disattivabile in «Auto») dopo verifica SHA-256; lo stesso controllo avvisa delle nuove versioni. Non legge traffico web, password o messaggi. Contatti: issue su GitHub all'indirizzo sopra.
