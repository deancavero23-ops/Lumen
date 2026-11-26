# Publicar Lumen y hacer que aparezca en Google

Ya está configurado para tu cuenta:

- Usuario: `deancavero23-ops`
- Repo: https://github.com/deancavero23-ops/Lumen
- Web: https://deancavero23-ops.github.io/Lumen/

El repositorio local ya está creado en esta carpeta, con rama `main` y un primer commit.

## 1) Subir el código a GitHub desde GitHub Desktop

1. Abre **GitHub Desktop**.
2. `File` → **Add Local Repository…**
3. Selecciona esta carpeta:

   `C:\Users\Pc1\OneDrive\Documentos\Proyecto predeterminado\Lumen`

4. Pulsa **Add repository**.
5. Pulsa **Publish repository**.
6. Nombre: `Lumen`. Descripción, por ejemplo: *Limpia y revisa tu PC con Lumen; fork de RetroLimpia bajo GPL-3.0*.
7. Deja marcado **Keep this code private** SIN marcar (debe ser público para GitHub Pages y Google).
8. Pulsa **Publish repository**.

### Alternativa desde terminal

Si prefieres hacerlo con comandos, desde esta carpeta:

```bash
git remote add origin https://github.com/deancavero23-ops/Lumen.git
git push -u origin main
```

Si el repo ya existe en GitHub, crea primero el repo **vacío** en https://github.com/new (sin README ni licencia, porque ya los tenemos aquí).

## 2) Activar la web en GitHub Pages

1. Entra a https://github.com/deancavero23-ops/Lumen
2. **Settings** → **Pages** (en el menú lateral).
3. En *Build and deployment*:
   - Source: **Deploy from a branch**
   - Branch: **main**  y  carpeta **/docs**
4. Pulsa **Save**.
5. Espera 1–2 minutos y abre:

   https://deancavero23-ops.github.io/Lumen/

Ya incluimos `docs/.nojekyll`, `docs/robots.txt` y `docs/sitemap.xml`.

## 3) Registrar el sitio en Google

1. Entra con tu Google a https://search.google.com/search-console
2. **Add property** → modo **URL prefix** y pega:

   `https://deancavero23-ops.github.io/Lumen/`

3. Verifica la propiedad. La forma más fácil es **HTML file upload**:
   - Google te dará un archivo tipo `googleXXXXXXXX.html`.
   - Descárgalo y cópialo dentro de la carpeta `docs/` del proyecto.
   - Haz commit y push (o desde GitHub Desktop: *Commit to main* → *Push origin*).
   - Vuelve a Search Console y pulsa **Verify**.
4. En el menú **Sitemaps**, escribe `sitemap.xml` y pulsa **Submit**.
5. Opcional: *URL Inspection* → pega la URL → **Request indexing** para pedir que la revisen antes.

Google puede tardar **días o semanas** en mostrar el sitio en resultados. Esto es normal.

## 4) Descargas y actualizaciones

Cuando publiques una versión:

1. Cambia `version` en `package.json`.
2. Haz commit y crea una etiqueta `vX.Y.Z`.
3. GitHub Actions compilará el instalador y creará la Release.
4. El programa podrá actualizarse solo desde ese repo.

Hasta que el repo exista, el actualizador no encontrará versiones; eso no afecta al uso local.
