package com.lumen.seguridad

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.DocumentsContract
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.security.MessageDigest

data class DocInfo(
    val uri: Uri,
    val parentId: String,
    val nombre: String,
    val tamano: Long,
    val modificado: Long,
    val motivo: String,
    val mime: String = ""
)

data class CuarentenaItem(
    val uri: String,
    val nombre: String,
    val padreOriginal: String,
    val cuarentenaPadre: String,
    val arbol: String
)

object Limpieza {
    private const val ARBOL = "arbol_carpeta"
    private const val ARCHIVO_CUARENTENA = "cuarentena.json"
    private const val CARPETA_CUARENTENA = "LumenCuarentena"
    private const val LIMITE = 5000

    fun guardarArbol(context: Context, uri: Uri) {
        val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        try {
            context.contentResolver.takePersistableUriPermission(uri, flags)
        } catch (e: Exception) {
            // Algunos proveedores no conceden permiso persistente; se mantiene solo para la sesión.
        }
        context.getSharedPreferences(Textos.PREFS, Context.MODE_PRIVATE)
            .edit().putString(ARBOL, uri.toString()).apply()
    }

    fun arbol(context: Context): Uri? {
        val s = context.getSharedPreferences(Textos.PREFS, Context.MODE_PRIVATE)
            .getString(ARBOL, null) ?: return null
        return try { Uri.parse(s) } catch (e: Exception) { null }
    }

    private fun motivo(nombre: String, tamano: Long, modificado: Long): String? {
        val n = nombre.lowercase()
        return when {
            n.endsWith(".tmp") || n.endsWith(".temp") || n == "thumbs.db" || n == ".ds_store" -> "reason_temp"
            n.endsWith(".log") -> "reason_log"
            tamano == 0L -> "reason_empty"
            n.endsWith(".apk") && modificado > 0 &&
                System.currentTimeMillis() - modificado > 30L * 24 * 60 * 60 * 1000 -> "reason_old_apk"
            else -> null
        }
    }

    /** Hash del contenido real: detecta fotos duplicadas aunque tengan distinto nombre. */
    private fun sha256(uri: Uri, cr: ContentResolver): String? {
        val digest = MessageDigest.getInstance("SHA-256")
        val buffer = ByteArray(8192)
        var total = 0L
        try {
            cr.openInputStream(uri)?.use { entrada ->
                while (true) {
                    val n = entrada.read(buffer)
                    if (n < 0) break
                    total += n
                    if (total > 80L * 1024 * 1024) return null
                    digest.update(buffer, 0, n)
                }
            } ?: return null
        } catch (e: Exception) {
            return null
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    /** Duración en milisegundos; 0 si el proveedor no la expone. */
    private fun duracion(context: Context, uri: Uri): Long = try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        val valor = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        retriever.release()
        valor?.toLongOrNull() ?: 0L
    } catch (e: Exception) {
        0L
    }

    fun escanear(context: Context, treeUri: Uri): List<DocInfo> {
        val cr = context.contentResolver
        val projection = arrayOf(
            DocumentsContract.Document.COLUMN_DOCUMENT_ID,
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_MIME_TYPE,
            DocumentsContract.Document.COLUMN_SIZE,
            DocumentsContract.Document.COLUMN_LAST_MODIFIED
        )
        val raiz = DocumentsContract.getTreeDocumentId(treeUri)
        val cola = ArrayDeque<String>()
        cola.add(raiz)
        val candidatos = mutableListOf<DocInfo>()
        val todos = mutableListOf<DocInfo>()
        var vistos = 0

        while (cola.isNotEmpty() && vistos < LIMITE) {
            val padre = cola.removeFirst()
            val uri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, padre)
            val cursor = try {
                cr.query(uri, projection, null, null, null)
            } catch (e: Exception) { null } ?: continue
            cursor.use { c ->
                while (c.moveToNext() && vistos < LIMITE) {
                    vistos++
                    val id = c.getString(0) ?: continue
                    val nombre = c.getString(1) ?: id
                    val mime = c.getString(2) ?: ""
                    val size = if (c.isNull(3)) 0L else c.getLong(3)
                    val mod = if (c.isNull(4)) 0L else c.getLong(4)
                    if (mime == DocumentsContract.Document.MIME_TYPE_DIR) {
                        if (nombre != CARPETA_CUARENTENA) cola.addLast(id)
                        continue
                    }
                    val docUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, id)
                    val base = DocInfo(docUri, padre, nombre, size, mod, "", mime)
                    todos.add(base)
                    val m = motivo(nombre, size, mod)
                    if (m != null) candidatos.add(base.copy(motivo = m))
                }
            }
        }

        val ya = candidatos.map { it.uri.toString() }.toHashSet()
        fun marcar(doc: DocInfo, motivo: String) {
            if (ya.add(doc.uri.toString())) candidatos.add(doc.copy(motivo = motivo))
        }

        // Duplicados por nombre y tamaño (rápido).
        todos.filter { it.tamano > 0 }
            .groupBy { it.tamano to it.nombre.lowercase() }
            .values
            .filter { it.size > 1 }
            .forEach { grupo ->
                grupo.sortedByDescending { it.modificado }.drop(1).forEach {
                    marcar(it, "reason_duplicate")
                }
            }

        // Duplicados reales por contenido (fotos, vídeos y música con distinto nombre).
        val media = todos.filter {
            it.tamano in 1024L..(200L * 1024 * 1024) &&
                (it.mime.startsWith("image/") || it.mime.startsWith("video/") ||
                    it.mime.startsWith("audio/"))
        }.take(120)
        val porHash = HashMap<String, MutableList<DocInfo>>()
        media.forEach { doc ->
            val hash = sha256(doc.uri, cr) ?: return@forEach
            porHash.getOrPut(hash) { mutableListOf() }.add(doc)
        }
        porHash.values.filter { it.size > 1 }.forEach { grupo ->
            val motivo = when {
                grupo.first().mime.startsWith("image/") -> "reason_duplicate_photo"
                grupo.first().mime.startsWith("video/") -> "reason_duplicate_video"
                else -> "reason_duplicate_music"
            }
            grupo.sortedByDescending { it.modificado }.drop(1).forEach { marcar(it, motivo) }
        }

        // Vídeos largos/pesados y música muy grande: solo se recomienda revisar.
        todos.forEach { doc ->
            when {
                doc.mime.startsWith("video/") && doc.tamano > 20L * 1024 * 1024 &&
                    (doc.tamano > 150L * 1024 * 1024 || duracion(context, doc.uri) > 600_000L) ->
                    marcar(doc, "reason_long_video")
                doc.mime.startsWith("audio/") && doc.tamano > 25L * 1024 * 1024 ->
                    marcar(doc, "reason_large_music")
            }
        }
        return candidatos.take(200)
    }

    private fun crearCuarentena(context: Context, treeUri: Uri): Uri? {
        val cr = context.contentResolver
        val projection = arrayOf(
            DocumentsContract.Document.COLUMN_DOCUMENT_ID,
            DocumentsContract.Document.COLUMN_DISPLAY_NAME
        )
        val cursor = try {
            cr.query(
                DocumentsContract.buildChildDocumentsUriUsingTree(
                    treeUri,
                    DocumentsContract.getTreeDocumentId(treeUri)
                ), projection, null, null, null
            )
        } catch (e: Exception) { null }
        cursor?.use { c ->
            while (c.moveToNext()) {
                if (c.getString(1) == CARPETA_CUARENTENA) {
                    val id = c.getString(0)
                    return DocumentsContract.buildDocumentUriUsingTree(treeUri, id)
                }
            }
        }
        val padre = DocumentsContract.buildDocumentUriUsingTree(
            treeUri, DocumentsContract.getTreeDocumentId(treeUri)
        )
        return try {
            DocumentsContract.createDocument(
                cr,
                padre,
                DocumentsContract.Document.MIME_TYPE_DIR,
                CARPETA_CUARENTENA
            )
        } catch (e: Exception) { null }
    }

    private fun archivoCuarentena(context: Context) = File(context.filesDir, ARCHIVO_CUARENTENA)

    fun leerCuarentena(context: Context): MutableList<CuarentenaItem> {
        val f = archivoCuarentena(context)
        if (!f.exists()) return mutableListOf()
        return try {
            val arr = JSONArray(f.readText())
            (0 until arr.length()).mapNotNull { i ->
                val o = arr.getJSONObject(i)
                CuarentenaItem(
                    o.getString("uri"),
                    o.getString("nombre"),
                    o.getString("padre"),
                    o.optString("cuarentena", ""),
                    o.getString("arbol")
                )
            }.toMutableList()
        } catch (e: Exception) { mutableListOf() }
    }

    private fun escribirCuarentena(context: Context, items: List<CuarentenaItem>) {
        val arr = JSONArray()
        items.forEach {
            arr.put(JSONObject()
                .put("uri", it.uri)
                .put("nombre", it.nombre)
                .put("padre", it.padreOriginal)
                .put("cuarentena", it.cuarentenaPadre)
                .put("arbol", it.arbol))
        }
        archivoCuarentena(context).writeText(arr.toString())
    }

    fun mover(context: Context, treeUri: Uri, docs: List<DocInfo>): Pair<Int, Boolean> {
        val cr = context.contentResolver
        val destino = crearCuarentena(context, treeUri) ?: return 0 to false
        val idCuarentena = DocumentsContract.getDocumentId(destino)
        val padreCuarentena = DocumentsContract.buildDocumentUriUsingTree(treeUri, idCuarentena)
        val guardados = leerCuarentena(context)
        var movidos = 0
        var fallback = false

        docs.forEach { doc ->
            val padre = DocumentsContract.buildDocumentUriUsingTree(treeUri, doc.parentId)
            var nuevo: Uri? = null
            val ok = try {
                nuevo = DocumentsContract.moveDocument(cr, doc.uri, padre, padreCuarentena)
                nuevo != null
            } catch (e: Exception) {
                try {
                    val copia = DocumentsContract.copyDocument(cr, doc.uri, padreCuarentena)
                    val borrado = DocumentsContract.deleteDocument(cr, doc.uri)
                    if (copia != null && borrado) {
                        nuevo = copia
                        fallback = true
                        true
                    } else false
                } catch (e2: Exception) { false }
            }
            if (ok) {
                guardados.add(
                    CuarentenaItem(
                        (nuevo ?: doc.uri).toString(), doc.nombre, doc.parentId,
                        idCuarentena, treeUri.toString()
                    )
                )
                movidos++
            }
        }
        escribirCuarentena(context, guardados)
        return movidos to fallback
    }

    /** Borrado definitivo, siempre después de confirmación explícita del usuario. */
    fun eliminar(context: Context, docs: List<DocInfo>): Int {
        val cr = context.contentResolver
        var borrados = 0
        docs.forEach { doc ->
            try {
                if (DocumentsContract.deleteDocument(cr, doc.uri)) borrados++
            } catch (e: Exception) { }
        }
        return borrados
    }

    fun restaurar(context: Context, item: CuarentenaItem): Boolean {
        val cr = context.contentResolver
        return try {
            val treeUri = Uri.parse(item.arbol)
            val docUri = Uri.parse(item.uri)
            val padreCuarentena = if (item.cuarentenaPadre.isNotBlank()) {
                DocumentsContract.buildDocumentUriUsingTree(treeUri, item.cuarentenaPadre)
            } else {
                crearCuarentena(context, treeUri) ?: return false
            }
            val padreOriginal = DocumentsContract.buildDocumentUriUsingTree(treeUri, item.padreOriginal)
            var ok = try {
                DocumentsContract.moveDocument(cr, docUri, padreCuarentena, padreOriginal)
                true
            } catch (e: Exception) { false }
            if (!ok) {
                val copia = DocumentsContract.copyDocument(cr, docUri, padreOriginal)
                val borrado = DocumentsContract.deleteDocument(cr, docUri)
                ok = copia != null && borrado
            }
            if (ok) {
                escribirCuarentena(context, leerCuarentena(context).filter { it.uri != item.uri })
            }
            ok
        } catch (e: Exception) { false }
    }
}
