package com.lumen.seguridad

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

data class DocInfo(
    val uri: Uri,
    val parentId: String,
    val nombre: String,
    val tamano: Long,
    val modificado: Long,
    val motivo: String
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
                    val base = DocInfo(docUri, padre, nombre, size, mod, "")
                    todos.add(base)
                    val m = motivo(nombre, size, mod)
                    if (m != null) candidatos.add(base.copy(motivo = m))
                }
            }
        }

        val duplicados = todos.filter { it.tamano > 0 }
            .groupBy { it.tamano to it.nombre.lowercase() }
            .values
            .filter { it.size > 1 }
        val ya = candidatos.map { it.uri.toString() }.toHashSet()
        duplicados.forEach { grupo ->
            grupo.sortedByDescending { it.modificado }.drop(1).forEach { doc ->
                if (ya.add(doc.uri.toString())) candidatos.add(doc.copy(motivo = "reason_duplicate"))
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
            val ok = try {
                DocumentsContract.moveDocument(cr, doc.uri, padre, padreCuarentena)
                true
            } catch (e: Exception) {
                try {
                    val copia = DocumentsContract.copyDocument(cr, doc.uri, padreCuarentena)
                    val borrado = DocumentsContract.deleteDocument(cr, doc.uri)
                    if (copia != null && borrado) {
                        fallback = true
                        true
                    } else false
                } catch (e2: Exception) { false }
            }
            if (ok) {
                guardados.add(
                    CuarentenaItem(
                        doc.uri.toString(), doc.nombre, doc.parentId,
                        idCuarentena, treeUri.toString()
                    )
                )
                movidos++
            }
        }
        escribirCuarentena(context, guardados)
        return movidos to fallback
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
