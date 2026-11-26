# Lumen para Android (PWA)

Es una **PWA instalable**: se abre en Chrome y se añade a la pantalla de inicio como una app más, sin APK ni Google Play.

**URL:** https://deancavero23-ops.github.io/Lumen/app/

## QR para abrirla al instante

![QR para abrir Lumen Android](img/qr-android-pwa.png)

Escanea el código con la cámara del móvil o con Chrome. Se abrirá directamente la app; luego usa el menú de Chrome → **Añadir a pantalla de inicio**. El mismo QR está publicado en la web principal.

## Instalarla en el móvil

1. Abre Chrome en el móvil y entra en la URL de arriba.
2. Pulsa el menú de Chrome (⋮) → **Añadir a pantalla de inicio** / **Instalar app**.
3. Aparece el icono rojo de Lumen en la pantalla de inicio. Ábrelo: funciona a pantalla completa y sin conexión.

Dentro de la app, en **Ajustes → Instalar en el móvil**, también se ofrece el diálogo de instalación cuando Chrome lo permite.

## Si el QR no te instala nada

El QR **no descarga un APK**: abre la página de la PWA. Para que aparezca la opción de instalar:

1. Si la abrió la cámara, WhatsApp, Instagram u otra app, pulsa el menú **⋮ → Abrir en Chrome**.
2. Espera un segundo y pulsa de nuevo **⋮ → Instalar app** o **Añadir a pantalla de inicio**.
3. Confirma **Instalar / Añadir**. Aparecerá el icono rojo de Lumen en la pantalla de inicio.

Si sigue sin aparecer, actualiza Chrome, sal del modo incógnito y comprueba que la dirección termina exactamente en `/app/`. El QR verificado apunta a `https://deancavero23-ops.github.io/Lumen/app/`.

## Qué hace

- Misma identidad: logo rojo con “L” negra y chispa blanca, colores negro/rojo/gris/blanco.
- **Lumi interactúa**: toca la mascota y aparece un globo con mensajes; cambia de expresión mientras revisa y al terminar.
- El botón **“Hablar con Lumi”** (y tocar la mascota) siempre intenta hablar con la voz del móvil. Si activas **“Que Lumi hable en voz alta”** en Ajustes, también hablan los mensajes automáticos. Si no hay voces instaladas, Lumi muestra el mensaje escrito y un aviso.
- Idiomas **español, italiano e inglés**, detectados desde el móvil y cambiables en Ajustes.
- Pantallas de Inicio, Limpieza, Arranque, Privacidad, Batería y Ajustes con datos de ejemplo.
- **Cuidado de batería:** lee el estado real del móvil cuando el navegador lo permite y activa un modo ahorro que reduce animaciones, parpadeos y trabajo en segundo plano.
- **Bloqueo de anuncios real dentro de Lumen:** una política de contenido impide scripts y conexiones de terceros, y el service worker descarta dominios publicitarios conocidos. Se puede activar o desactivar en **Privacidad**.
- Funciona **sin conexión** gracias al service worker (`docs/sw.js`).

## Límite importante

Es una **demo**: no limpia el móvil, no desinstala apps, no toca permisos ni actualiza nada. Tampoco puede quitar los anuncios del navegador ni de otras aplicaciones: el bloqueo actúa **solo dentro de Lumen**. Android no permite ese tipo de acciones desde una PWA (y Google Play las prohíbe salvo casos muy concretos). Las funciones reales de Lumen siguen siendo exclusivas de la versión de Windows.

## Archivos

- `docs/app/index.html` — interfaz y política de contenido (bloqueo de scripts y conexiones externas)
- `docs/app/styles.css` — estilos y modo ahorro
- `docs/app/app.js` — idiomas, Lumi, voz, batería, bloqueo de anuncios y demo
- `docs/manifest.webmanifest` — definición de la PWA
- `docs/sw.js` — caché offline y filtro de dominios publicitarios
- `docs/img/pwa-*.png` — iconos 192, 512 y maskable

Créditos: Lumen se basa en RetroLimpia, de Retropunk94 (GPL-3.0-or-later). No es la versión oficial.
