# Lumen Seguridad (Android nativo)

> **Aplicación nueva, escrita desde cero en Kotlin.** No contiene código de CCleaner ni de RetroLimpia, no es la versión oficial de CCleaner y **no reemplaza a Play Protect**. Es un proyecto *clean-room* publicado bajo GPL-3.0-or-later, con los créditos de RetroLimpia en `CRÉDITOS.md`.

Esta APK **sí hace cosas reales**, a diferencia de la demo TWA descrita en [ANDROID.md](ANDROID.md). Las dos conviven: la demo sirve para probar la interfaz y esta para analizar el móvil dentro de los límites que Android permite a una app sin root.

## Descargar

**[Descargar Lumen-Seguridad-1.2.2.apk](https://github.com/deancavero23-ops/Lumen/releases/download/seguridad-v1.2.2/Lumen-Seguridad-1.2.2.apk)** · [SHA-256 publicado](https://github.com/deancavero23-ops/Lumen/releases/download/seguridad-v1.2.2/SHA256-SEGURIDAD.txt)

![QR para descargar Lumen Seguridad](img/qr-android-seguridad.png)

Si prefieres que el QR abra la página con las instrucciones en vez del APK directo, usa este otro: ![QR de la página de descarga](img/qr-descarga-android.png)

- Huella SHA-256 del APK publicado: pendiente de la compilación 1.2.2 (se publica en `SHA256-SEGURIDAD.txt`).
- Requiere **Android 7.0 (API 24) o superior**.
- Se instala por **sideload**: al abrir la APK, Android pedirá permitir **fuentes desconocidas**. Acepta solo si confías en este repositorio.
- La firma es propia (`android-native/lumen-seguridad.keystore`, alias `lumen`). **No es la misma firma** que la APK demo, por lo que Android las tratará como apps distintas y podrás tener las dos instaladas.
- Para verificar la descarga: `certutil -hashfile Lumen-Seguridad-1.2.2.apk SHA256` en Windows o `sha256sum Lumen-Seguridad-1.2.2.apk` en Linux.
- **¿Te sale «demo»?** En Ajustes debe poner `v1.2.2`. Si pone `v1.0.0` o el texto dice «Demo · no limpia tu móvil», instalaste la APK demo TWA (solo interfaz; ahora también lleva 🛡️). Descarga `Lumen-Seguridad-1.2.2.apk` desde el enlace de arriba o su QR.

## Lo que sí hace

| Función | Cómo funciona | Límite real |
|---|---|---|
| **Análisis de apps instaladas** | Lee el APK de cada app, calcula su SHA-256, obtiene el certificado firmante, el instalador y sus permisos sensibles. Ordena por riesgo (mayor puntuación primero). | No tiene una base de virus: las reglas son una lista editable de hashes y firmantes. Un hash desconocido **no significa** que sea malware. |
| **Señales de riesgo** | Cuenta permisos como SMS, contactos, ubicación, cámara, micrófono, `SYSTEM_ALERT_WINDOW`, `REQUEST_INSTALL_PACKAGES`; detecta servicios de accesibilidad e instalación fuera de tienda. | Es una orientación, no un veredicto. Muchas apps legítimas piden esos permisos. |
| **Aviso de nuevas apps** | Mientras Lumen está abierto, avisa cuando se instala un paquete y lo analiza. | Android 8+ no permite escuchar ese evento en segundo plano desde el manifiesto; el aviso solo está activo con la app abierta. |
| **Escaneo de archivos** | Con el selector de carpetas del sistema (SAF), recorre hasta 5.000 archivos y marca temporales, `.log`, vacíos, APK con más de 30 días y duplicados por nombre y tamaño. | Lee **solo la carpeta que eliges**. No toca los datos privados de otras apps. |
| **Cuarentena restaurable** | Mueve los archivos marcados a `LumenCuarentena` dentro de esa misma carpeta y guarda su ruta original en `cuarentena.json`. Cada elemento se puede restaurar con un toque. | Si el proveedor de archivos no permite mover, intenta copiar y borrar. Nada se elimina de forma definitiva. |
| **Batería real** | Lee nivel y estado de carga con `BatteryManager`. El "modo ahorro de Lumi" es una preferencia de la app. | No cambia ajustes del sistema ni limita procesos de otras apps. |
| **Botones con estado** | Inicio en dos columnas, con 🛡️ e iconos: 🔎 Escanear, 🧹 Liberar espacio, 📦 Cuarentena, ⚡ Rendimiento, 🌡️ Temperatura, 🔄 Actualizar, 🔋 Batería, 🗣️ Hablar, 💬 Chat y ⚙️ Ajustes. Verde = correcto, ámbar = revisar, rojo = urgente; el botón de Hablar se apaga si silencias a Lumi. | El color viene de datos reales (batería, temperatura, memoria y espacio), no de una puntuación inventada. |
| **Rendimiento y temperatura** | ⚡ muestra memoria disponible y espacio libre reales (`ActivityManager` y `StatFs`). 🌡️ lee la temperatura de la batería con `BatteryManager.EXTRA_TEMPERATURE`. | Ninguna app puede "acelerar" Android ni enfriar el móvil: Lumen no mata procesos ajenos. Si la temperatura es alta, muestra consejos (quitar funda, dejar de cargar, cerrar apps pesadas). |
| **Soluciones al analizar una app** | Tras el escaneo, «Qué hacer con una app» permite abrirla en Play Store, abrir sus permisos del sistema, marcarla como conocida (lista local), pedir su desinstalación con el diálogo del sistema y pasar Play Protect. | No elimina malware por sí mismo. Un hash desconocido es una señal, no una condena; la lista de conocidas solo cambia la presentación local. |
| **Actualizar apps** | 🔄 abre la sección de actualizaciones de Google Play (`market://details?id=com.android.vending`). | Lumen no descarga ni instala actualizaciones: lo hace Play Store. |
| **Bloqueo global de anuncios (opcional)** | VPN local que responde `0.0.0.0` a los dominios publicitarios en las consultas DNS de **todas las apps**. Se activa solo desde **Ajustes → Bloqueo global de anuncios** y Android muestra su diálogo de permiso. | Filtra **solo DNS**: no abre ni lee el resto del tráfico, no funciona con DNS sobre TLS (DoT) y cualquiera puede desactivarlo desde Ajustes o desde la notificación. |
| **Desinstalar con confirmación** | Abre el diálogo del sistema con `ACTION_UNINSTALL_PACKAGE`. | La app nunca desinstala por sí sola; el usuario confirma siempre. |
| **Lumi, voces y chat** | Mascota dibujada en código y 15 audios femeninos incluidos (`es`, `it`, `en`). Si falla el audio, usa TTS prefiriendo voces femeninas. **«Escribir a Lumi» abre un chat local** con respuestas sobre apps, archivos, anuncios, batería y reglas. | El audio se prepara de forma asíncrona para no bloquear la interfaz. Se puede silenciar en Ajustes. El chat no consulta Internet ni modelos externos. |
| **Idiomas** | Español, italiano e inglés, detectados del sistema y cambiables en Ajustes. | — |
| **Privacidad** | Sin telemetría, sin servidor propio, sin accesibilidad y sin permisos de almacenamiento amplio. | Las reglas se guardan solo en el dispositivo. La descarga es manual y no envía nada a Lumen. |
| **Reglas actualizables** | **Ajustes → Actualizar reglas desde GitHub** descarga `reglas.json` y su `.sha256` desde la Release `reglas-v1`, comprueba la huella y la guarda dentro de la app. **Ajustes → Reglas de seguridad** muestra el origen y permite volver a las incluidas. | No se descarga nada automáticamente. Si falla la red o el SHA-256 no coincide, se conservan las reglas anteriores. La fuente de confianza es este repositorio de GitHub; el hash evita cambios en tránsito, no compromisos del repositorio. |

## Lo que NO hace (y no puede hacer sin root)

- **No reemplaza a Play Protect.** Google ya analiza apps en la nube y en el dispositivo; esta app es una segunda opinión local.
- No limpia la caché de otras apps: Android aísla los datos de cada aplicación.
- No borra archivos directamente con `MANAGE_EXTERNAL_STORAGE`: usa el selector del sistema.
- No desinstala apps del sistema ni las de otros usuarios.
- No bloquea anuncios dentro de la PWA ni de la app demo: ese bloqueo actúa solo dentro de Lumen. El bloqueo global es **opcional**, se basa solo en DNS y no inspecciona el contenido de las conexiones.
- No escanea en segundo plano ni envía resultados a ningún servidor.
- No garantiza la ausencia de malware: solo compara contra reglas locales y muestra señales de riesgo.

## Permisos

- `QUERY_ALL_PACKAGES`: imprescindible para listar y analizar las apps instaladas, que es la función central de un analizador de seguridad. Al distribuirse por sideload no necesita el formulario de permisos sensibles de Google Play. Si algún día se publica en Play, habrá que justificarlo ante Google.
- `INTERNET`: se usa para reenviar a un DNS público (Cloudflare o Google) las consultas que no son publicidad mientras la VPN está activa, y para descargar las reglas **solo cuando tú pulsas Actualizar reglas**. La app excluye su propio tráfico del VPN para evitar bucles. No hay telemetría ni servidor de Lumen.
- `FOREGROUND_SERVICE` y `FOREGROUND_SERVICE_SPECIAL_USE`: necesarios para mantener el filtro DNS activo con notificación persistente mientras tú lo dejes encendido.
- No pide `READ/WRITE_EXTERNAL_STORAGE`, `MANAGE_EXTERNAL_STORAGE`, accesibilidad ni permisos de SMS/contactos. El acceso a archivos se concede carpeta por carpeta desde el selector del sistema.
- Android muestra un **diálogo propio de VPN** la primera vez. Si lo rechazas, el bloqueo queda desactivado y el resto de la app sigue funcionando.

## Bloqueo global de anuncios (VPN opcional)

En **Ajustes → Bloqueo global de anuncios** aparece el estado y un diálogo con la explicación. Al pulsar **Activar bloqueo global (VPN)**:

1. Android muestra su diálogo de sistema «Permitir conexión VPN». Sin ese permiso no se activa nada.
2. Lumen crea una interfaz VPN local (`10.0.0.2/24`) y establece `10.0.0.1` como DNS del dispositivo.
3. Las consultas DNS llegan a Lumen: si el dominio pertenece a una lista publicitaria conocida, responde `0.0.0.0`; si no, las reenvía a `1.1.1.1` o `8.8.8.8`.
4. Una notificación persistente permite volver a la app. En Ajustes se puede **Desactivar bloqueo global** en cualquier momento.

Es un filtro DNS, **no un túnel completo**: no lee páginas web, contraseñas ni mensajes. No bloquea anuncios servidos desde el mismo dominio que el contenido, ni los que usen DNS sobre TLS. Tampoco sustituye a un bloqueador de contenidos dentro del navegador.

## Actualizar reglas desde GitHub

Las reglas incluidas (`android-native/app/src/main/assets/reglas.json`) son una base de ejemplo vacía: añade tus propios hashes o firmantes si quieres. Desde **Ajustes → Actualizar reglas desde GitHub**:

1. La app muestra qué reglas están activas y pide confirmación.
2. Descarga `reglas.json` y `reglas.json.sha256` desde la Release [`reglas-v1`](https://github.com/deancavero23-ops/Lumen/releases/tag/reglas-v1).
3. Calcula el SHA-256 local y lo compara con el publicado. Solo si coinciden valida el formato y guarda el archivo en el almacenamiento privado de la app.
4. **Ajustes → Reglas de seguridad** muestra el origen, versión, fecha y número de hashes/firmantes, y permite **Restablecer incluidas**.

No hay descargas en segundo plano, ni comprobación de actualizaciones al abrir la app, ni envío de la lista de apps instaladas. El límite de descarga es 1 MB y solo se aceptan URLs de GitHub. La huella publicada de esta primera versión de las reglas es `b09901e3dab6de16ea5e47de9b1683556bff1e304d6f6587313fc401686214a9`.

## Compilar

```bash
cd android-native
./gradlew assembleRelease
```

El workflow [`.github/workflows/apk-seguridad.yml`](../.github/workflows/apk-seguridad.yml) compila y firma la APK con el certificado del repositorio al publicar una etiqueta `seguridad-v*`, y sube el APK junto con `SHA256-SEGURIDAD.txt` a la Release. Para probar sin publicar: `workflow_dispatch` en Actions.

Estructura:

```
android-native/app/src/main/java/com/lumen/seguridad/
  Analizador.kt   paquetes, hashes, certificados y puntuación de riesgo
  Limpieza.kt     escaneo SAF, cuarentena y restauración
  Voz.kt          audios femeninos + TTS de respaldo
  BloqueoVpn.kt   filtro DNS opcional con VpnService
  Reglas.kt       descarga manual y verificación SHA-256
  Textos.kt       es / it / en
  LumiView.kt     mascota dibujada
  MainActivity.kt interfaz y flujos
android-native/app/src/main/assets/
  reglas.json     base local de hashes, firmantes y permisos
  audio/          15 WAV femeninos
```

## Créditos

Lumen se basa en [RetroLimpia](https://github.com/retropunk94/RetroLimpia), de Retropunk94 (GPL-3.0-or-later). No es la versión oficial. Lumen Seguridad es una implementación independiente inspirada en las *funciones* de los limpiadores de Android, sin copiar código de CCleaner ni de ningún producto propietario. Enlaces: [web](https://deancavero23-ops.github.io/Lumen/) · [repositorio](https://github.com/deancavero23-ops/Lumen) · [releases](https://github.com/deancavero23-ops/Lumen/releases).
