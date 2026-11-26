# Lumen para Android (PWA)

Es una **PWA instalable**: se abre en Chrome y se añade a la pantalla de inicio como una app más, sin APK ni Google Play.

**URL:** https://deancavero23-ops.github.io/Lumen/app/

## Instalarla en el móvil

1. Abre Chrome en el móvil y entra en la URL de arriba.
2. Pulsa el menú de Chrome (⋮) → **Añadir a pantalla de inicio** / **Instalar app**.
3. Aparece el icono rojo de Lumen en la pantalla de inicio. Ábrelo: funciona a pantalla completa y sin conexión.

Dentro de la app, en **Ajustes → Instalar en el móvil**, también se ofrece el diálogo de instalación cuando Chrome lo permite.

## Qué hace

- Misma identidad: logo rojo con “L” negra y chispa blanca, colores negro/rojo/gris/blanco.
- **Lumi interactúa**: toca la mascota y aparece un globo con mensajes; cambia de expresión mientras revisa y al terminar.
- Si activas **“Que Lumi hable en voz alta”** en Ajustes, los mensajes se dicen con la voz del móvil (Web Speech API; depende de las voces instaladas).
- Idiomas **español, italiano e inglés**, detectados desde el móvil y cambiables en Ajustes.
- Pantallas de Inicio, Limpieza, Arranque, Privacidad y Ajustes con datos de ejemplo.
- Funciona **sin conexión** gracias al service worker (`docs/sw.js`).

## Límite importante

Es una **demo**: no limpia el móvil, no desinstala apps, no toca permisos ni actualiza nada. Android no permite ese tipo de acciones desde una PWA (y Google Play las prohíbe salvo casos muy concretos). Las funciones reales de Lumen siguen siendo exclusivas de la versión de Windows.

## Archivos

- `docs/app/index.html` — interfaz
- `docs/app/styles.css` — estilos
- `docs/app/app.js` — idiomas, Lumi, voz y demo
- `docs/manifest.webmanifest` — definición de la PWA
- `docs/sw.js` — caché offline
- `docs/img/pwa-*.png` — iconos 192, 512 y maskable

Créditos: Lumen se basa en RetroLimpia, de Retropunk94 (GPL-3.0-or-later). No es la versión oficial.
