# Lumen Seguridad en Google Play

> **Estado:** el AAB firmado se genera en GitHub Actions, pero **no lo subimos a Play Console**. Necesitas tu cuenta de desarrollador para publicarlo. El APK de sideload sigue disponible en la [Release de seguridad](https://github.com/deancavero23-ops/Lumen/releases/tag/seguridad-v1.2.5). La ficha de texto y el material gráfico están en [PLAY-FICHA.md](PLAY-FICHA.md).

## Descargar el AAB

**[Descargar Lumen-Seguridad-1.2.5-play.aab](https://github.com/deancavero23-ops/Lumen/releases/download/play-v1.2.5/Lumen-Seguridad-1.2.5-play.aab)** · [SHA-256](https://github.com/deancavero23-ops/Lumen/releases/download/play-v1.2.5/SHA256-PLAY-AAB.txt)

- Huella SHA-256 del AAB publicado: `095f71fade6dfa75abc7472531381a36318f9064c17b082ebe85a39eea7ca3e5` (4.305.985 bytes).
- Package: `com.lumen.seguridad`.
- Firma: la misma que la APK (`android-native/lumen-seguridad.keystore`, alias `lumen`). Si vas a subir una versión nueva más adelante, **usa el mismo keystore** o Play no aceptará la actualización.
- Antes de crear la app en Play Console, comprueba que `com.lumen.seguridad` esté disponible en tu cuenta. Si ya existe una app tuya con otro package, dime cuál es y ajusto el `applicationId`.
- `versionCode` = 8, `versionName` = `1.2.5`. Si ya subiste una versión anterior, este AAB es la actualización. Cada nueva versión en Play debe subir el `versionCode`.

## Publicarlo

1. En [Play Console](https://play.google.com/console) pulsa **Crear app** y elige español como idioma principal.
2. Cumplimenta la ficha: nombre **Lumen Seguridad**, categoría **Herramientas** o **Seguridad**, y el logo rojo con la L negra y chispa blanca.
3. Ve a **Producción → Crear versión** y sube el `.aab`.
4. En el formulario de permisos sensibles declara `QUERY_ALL_PACKAGES` con el texto de abajo.
5. En **Privacidad y protección de datos (Data safety)** usa las respuestas de abajo.
6. En el campo **URL de la política de privacidad** pon: `https://deancavero23-ops.github.io/Lumen/PRIVACIDAD.md`.
7. Publica primero en un **test interno** para comprobar el diálogo de VPN en un móvil real y después pasa a producción.

## Declaración de `QUERY_ALL_PACKAGES`

Texto sugerido (en inglés, como pide el formulario):

> Lumen Seguridad is a local security analyzer. It uses QUERY_ALL_PACKAGES as its core functionality to enumerate installed apps, compute each APK's SHA-256, read its signing certificate and requested permissions, and show a risk score on device. The list of installed apps is never uploaded to our servers: the app has no telemetry and no backend. We do not use this permission for advertising, analytics, or app recommendations.

Justificación adicional si el revisor pide más:

> Without this permission, the app cannot read the APK of other installed apps and the antivirus feature would be impossible. Users can see every analyzed package on screen and no automated action is taken: uninstalling always opens the Android system dialog.

## Declaración de `PACKAGE_USAGE_STATS` (opcional)

> Lumen Seguridad can optionally show which installed apps have not been opened in 90 days. The user must enable Usage access for Lumen in Android Settings; the app never requests it programmatically or sends usage data anywhere. When permission is not granted, this advice is simply hidden and the rest of the app works normally. No app is uninstalled by Lumen: it only opens the system Settings screen for the selected package.

## Privacidad y protección de datos

- **Datos recopilados por Lumen:** ninguno. No hay cuentas, telemetría ni servidor propio.
- **Datos compartidos:** el análisis de apps y archivos se queda en el dispositivo. Hay dos salidas a Internet: (1) si activa el bloqueo de anuncios por VPN, las consultas DNS de los dominios permitidos se reenvían a Cloudflare (`1.1.1.1`) o Google (`8.8.8.8`); (2) una petición HTTPS anónima a GitHub Releases, automática una vez al día para reglas y versiones (se puede desactivar en «Auto») o manual al pulsar «Actualizar reglas». No se envía la lista de apps, archivos ni identificadores. Lumen no recibe esos datos.
- Si el formulario obliga a marcar una categoría, elige **Actividad de red / Información de navegación** y marca **no vinculado a la identidad**, **solo mientras la VPN está activa** y **finalidad: bloquear dominios publicitarios**.
- **Permisos que más llaman la atención:** `QUERY_ALL_PACKAGES` (analizador de seguridad), `INTERNET` (reenvío DNS y comprobación de reglas/versiones en GitHub), `FOREGROUND_SERVICE_SPECIAL_USE` (filtro DNS con notificación) y `PACKAGE_USAGE_STATS` (opcional: el usuario lo activa en Ajustes del sistema para ver apps sin usar; no se envía ningún dato). No se pide almacenamiento amplio ni accesibilidad. En el AAB de Play, la app detecta que fue instalada por Play Store y no muestra el aviso de APK: Play gestiona las actualizaciones.

## Uso de `VpnService`

Texto sugerido para la ficha o para el revisor:

> The optional ad blocker uses Android's VpnService only as a local DNS filter. The user must grant permission through the system VPN dialog. The app responds with 0.0.0.0 to known ad domains and forwards other DNS queries to a public resolver. It does not build a full tunnel, does not inspect web traffic, passwords or messages, and can be disabled at any time from Settings or the persistent notification.

## Texto corto de la ficha

**Español:** Analiza las apps instaladas, revisa permisos y firmas, escanea las carpetas que eliges y pone en cuarentena los archivos candidatos con opción de restaurar. Bloqueo de anuncios por DNS opcional y reglas actualizables desde GitHub. Sin telemetría.

**Italiano:** Analizza le app installate, controlla permessi e firme, scansiona le cartelle che scegli e mette in quarantena i file candidati con ripristino. Blocco annunci DNS opzionale e regole aggiornabili da GitHub. Nessuna telemetria.

**English:** Scans installed apps, checks permissions and signatures, scans the folders you choose, and quarantines candidate files with restore. Optional DNS ad blocking and rules updatable from GitHub. No telemetry.

## Compilar el AAB

```bash
cd android-native
./gradlew bundleRelease
```

El workflow [`.github/workflows/aab-play.yml`](../.github/workflows/aab-play.yml) hace lo mismo en CI al publicar una etiqueta `play-v*` y adjunta el AAB y su SHA-256 a la Release. Para probar sin publicar: `workflow_dispatch` en Actions.

## Créditos

Lumen se basa en [RetroLimpia](https://github.com/retropunk94/RetroLimpia), de Retropunk94 (GPL-3.0-or-later). No es la versión oficial. Lumen Seguridad es una implementación independiente, sin código de CCleaner. Web: https://deancavero23-ops.github.io/Lumen/ · Repositorio: https://github.com/deancavero23-ops/Lumen.
