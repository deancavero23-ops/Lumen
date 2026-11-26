package com.lumen.seguridad

import android.app.Activity
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.net.VpnService
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private lateinit var contenedor: LinearLayout
    private lateinit var salida: TextView
    private lateinit var acciones: LinearLayout
    private var apps: List<AppRiesgo> = emptyList()
    private var documentos: List<DocInfo> = emptyList()
    private var monitorActivo = false

    private val monitor = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val paquete = intent.data?.schemeSpecificPart ?: return
            if (paquete == packageName) return
            Thread {
                val app = Analizador.analizarUno(applicationContext, paquete)
                val actualizar = ::salida.isInitialized && salida.text.toString().contains(t("apps_header"))
                val lista = if (actualizar) Analizador.analizar(applicationContext) else emptyList()
                runOnUiThread {
                    app?.let { toast("${t("new_app")}: ${it.nombre} · ${t(it.nivel)}") }
                    if (lista.isNotEmpty()) {
                        apps = lista
                        mostrarApps()
                    }
                }
            }.start()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contenedor = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#0B0B0C"))
            setPadding(dp(18), dp(18), dp(18), dp(28))
        }
        setContentView(ScrollView(this).apply { addView(contenedor) })
        registrarMonitor()
        hablar("lumiHola")
        render()
    }

    override fun onStart() {
        super.onStart()
        registrarMonitor()
    }

    override fun onStop() {
        desregistrarMonitor()
        super.onStop()
    }

    override fun onDestroy() {
        desregistrarMonitor()
        super.onDestroy()
    }

    private fun t(clave: String) = Textos.t(this, clave)
    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
    private fun hablar(clave: String) {
        if (prefs().getBoolean("voz", true)) Voz.hablar(this, clave, t(clave))
    }

    private fun registrarMonitor() {
        if (monitorActivo) return
        val filtro = IntentFilter(Intent.ACTION_PACKAGE_ADDED).apply { addDataScheme("package") }
        try {
            @Suppress("DEPRECATION")
            registerReceiver(monitor, filtro)
            monitorActivo = true
        } catch (e: Exception) { monitorActivo = false }
    }

    private fun desregistrarMonitor() {
        if (!monitorActivo) return
        try {
            @Suppress("DEPRECATION")
            unregisterReceiver(monitor)
        } catch (e: Exception) { }
        monitorActivo = false
    }

    private fun toast(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    private fun fondoBoton(primario: Boolean): GradientDrawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = dp(12).toFloat()
        setColor(Color.parseColor(if (primario) "#E11D2E" else "#24262B"))
    }

    private fun boton(clave: String, primario: Boolean = true, onClick: () -> Unit): Button =
        Button(this).apply {
            text = t(clave)
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            background = fondoBoton(primario)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(8) }
            setOnClickListener { onClick() }
        }

    private fun texto(contenido: String, gris: Boolean = false, mono: Boolean = false): TextView =
        TextView(this).apply {
            text = contenido
            setTextColor(Color.parseColor(if (gris) "#9AA0A6" else "#FFFFFF"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, if (gris) 13f else 15f)
            if (mono) typeface = android.graphics.Typeface.MONOSPACE
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(10) }
        }

    private fun render() {
        contenedor.removeAllViews()

        val cabecera = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
        }
        val lumi = LumiView(this).apply {
            layoutParams = LinearLayout.LayoutParams(dp(64), dp(64))
        }
        val titulos = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        titulos.addView(TextView(this).apply {
            text = t("app_name")
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        })
        titulos.addView(TextView(this).apply {
            text = t("chip")
            setTextColor(Color.parseColor("#E11D2E"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
        })
        cabecera.addView(lumi)
        cabecera.addView(titulos)
        contenedor.addView(cabecera)

        contenedor.addView(texto(t("limits"), gris = true))
        contenedor.addView(texto(t("monitor_on"), gris = true))
        contenedor.addView(texto(""))

        contenedor.addView(boton("scan_apps") { escanearApps() })
        contenedor.addView(boton("choose_folder", false) { elegirCarpeta() })
        contenedor.addView(boton("quarantine", false) { mostrarCuarentena() })
        contenedor.addView(boton("battery", false) { mostrarBateria() })
        contenedor.addView(boton("speak", false) { hablar("lumiHola") })
        contenedor.addView(boton("settings", false) { mostrarAjustes() })

        salida = texto(t("lumiHola"))
        salida.setTextIsSelectable(true)
        contenedor.addView(salida)

        acciones = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        contenedor.addView(acciones)

        contenedor.addView(texto("${t("version")} · ${t("credits")}", gris = true))
    }

    private fun limpiarAcciones() {
        if (::acciones.isInitialized) acciones.removeAllViews()
    }

    private fun accion(clave: String, onClick: () -> Unit) {
        acciones.addView(boton(clave, false, onClick))
    }

    private fun volver() {
        salida.text = t("lumiHola")
        limpiarAcciones()
    }

    private fun escanearApps() {
        salida.text = t("scanning_apps")
        limpiarAcciones()
        Thread {
            val resultado = Analizador.analizar(applicationContext)
            runOnUiThread {
                apps = resultado
                mostrarApps()
            }
        }.start()
    }

    private fun mostrarApps() {
        hablar("lumiDone")
        val sb = StringBuilder(t("apps_header")).append('\n').append('\n')
        apps.take(40).forEach { a ->
            sb.append("${a.nombre} — ${t(a.nivel)} (${a.puntuacion})\n")
            sb.append("  ${a.paquete} v${a.version}\n")
            sb.append("  ${t("sha256")}: ${a.sha.take(16)}${if (a.sha.length > 16) "…" else ""}\n")
            sb.append("  ${t("signer")}: ${a.firmante}\n")
            sb.append("  ${t("installer")}: ${a.instalador}\n")
            if (a.permisos.isNotEmpty()) sb.append("  ${t("perms")}: ${a.permisos.joinToString(", ")}\n")
            if (a.accesibilidad) sb.append("  ⚠ ${t("accessibility")}\n")
            if (a.instalador == "desconocida") sb.append("  ⚠ ${t("unknown_source")}\n")
            sb.append('\n')
        }
        if (apps.isEmpty()) sb.append(t("apps_empty"))
        salida.text = sb.toString()
        limpiarAcciones()
        accion("details") { elegirApp() }
        accion("open_play_protect") { abrirPlayProtect() }
        accion("back") { volver() }
    }

    private fun elegirApp() {
        if (apps.isEmpty()) return
        val nombres = apps.take(30).map { "${it.nombre} · ${t(it.nivel)}" }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle(t("apps_header"))
            .setItems(nombres) { _, which -> detalleApp(apps[which]) }
            .show()
    }

    private fun detalleApp(app: AppRiesgo) {
        val texto = """
            ${app.nombre} (${app.paquete})
            ${app.version}
            ${t("risk")}: ${t(app.nivel)} (${app.puntuacion})
            ${t("sha256")}: ${app.sha.ifEmpty { "—" }}
            ${t("signer")}: ${app.firmante}
            ${t("installer")}: ${app.instalador}
            ${t("perms")}: ${app.permisos.joinToString(", ").ifEmpty { "—" }}
        """.trimIndent()
        AlertDialog.Builder(this)
            .setTitle(app.nombre)
            .setMessage(texto)
            .setNeutralButton(t("open_play_protect")) { _, _ -> abrirPlayProtect() }
            .setPositiveButton(t("uninstall")) { _, _ ->
                try {
                    startActivity(Intent(Intent.ACTION_UNINSTALL_PACKAGE, Uri.parse("package:${app.paquete}")))
                } catch (e: Exception) { toast(t("error")) }
            }
            .setNegativeButton(t("cancel"), null)
            .show()
    }

    private fun abrirPlayProtect() {
        val intents = listOf(
            Intent("com.google.android.gms.security.settings.CHECK_APPS"),
            Intent(android.provider.Settings.ACTION_SECURITY_SETTINGS)
        )
        for (i in intents) {
            try {
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(i)
                return
            } catch (e: Exception) { }
        }
        abrirUrl("https://support.google.com/googleplay/answer/2812853")
    }

    private fun elegirCarpeta() {
        salida.text = t("choose_folder_hint")
        limpiarAcciones()
        startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), 42)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 43) {
            if (resultCode == RESULT_OK) iniciarVpn() else toast(t("vpn_denied"))
            return
        }
        if (requestCode != 42 || resultCode != RESULT_OK) return
        val uri = data?.data ?: return
        Limpieza.guardarArbol(this, uri)
        toast(t("folder_saved"))
        salida.text = t("scanning_files")
        Thread {
            val lista = Limpieza.escanear(applicationContext, uri)
            runOnUiThread { documentos = lista; mostrarArchivos() }
        }.start()
    }

    private fun mostrarArchivos() {
        hablar("lumiDone")
        val sb = StringBuilder(t("files_header")).append('\n').append('\n')
        documentos.forEach { d ->
            sb.append("${t(d.motivo)} · ${d.nombre} · ${Utilidades.tamano(d.tamano)} · ${Utilidades.fecha(d.modificado)}\n")
        }
        if (documentos.isEmpty()) sb.append(t("files_empty"))
        salida.text = sb.toString()
        limpiarAcciones()
        if (documentos.isNotEmpty()) {
            accion("move_quarantine") {
                AlertDialog.Builder(this)
                    .setTitle(t("move_quarantine"))
                    .setMessage(t("confirm_move"))
                    .setPositiveButton(t("ok")) { _, _ -> moverArchivos() }
                    .setNegativeButton(t("cancel"), null)
                    .show()
            }
        }
        accion("back") { volver() }
    }

    private fun moverArchivos() {
        val uri = Limpieza.arbol(this) ?: return
        salida.text = t("scanning_files")
        Thread {
            val (n, fallback) = Limpieza.mover(applicationContext, uri, documentos)
            runOnUiThread {
                toast(if (n > 0) t("moved") else t("error"))
                if (fallback) toast(t("no_permission"))
                documentos = emptyList()
                mostrarArchivos()
            }
        }.start()
    }

    private fun mostrarCuarentena() {
        val items = Limpieza.leerCuarentena(this)
        val sb = StringBuilder(t("quarantine")).append('\n').append('\n')
        items.forEach { sb.append("${it.nombre}\n") }
        if (items.isEmpty()) sb.append(t("quarantine_empty"))
        salida.text = sb.toString()
        limpiarAcciones()
        items.take(8).forEach { item ->
            acciones.addView(boton("restore", false) {
                Thread {
                    val ok = Limpieza.restaurar(applicationContext, item)
                    runOnUiThread {
                        toast(if (ok) t("restored") else t("error"))
                        mostrarCuarentena()
                    }
                }.start()
            })
        }
        accion("back") { volver() }
    }

    private fun mostrarBateria() {
        @Suppress("DEPRECATION")
        val b = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val nivel = b?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val escala = b?.getIntExtra(BatteryManager.EXTRA_SCALE, 100) ?: 100
        val porcentaje = if (nivel >= 0 && escala > 0) nivel * 100 / escala else -1
        val cargando = (b?.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) ?: 0) != 0
        val ahorro = prefs().getBoolean("ahorro", false)
        salida.text = """
            ${t("battery_header")}
            ${t("battery_level")}: ${if (porcentaje >= 0) "$porcentaje%" else t("unknown")}
            ${if (cargando) t("charging") else t("discharging")}
            ${if (ahorro) t("saving_on") else t("saving_off")}

            ${t("battery_note")}
        """.trimIndent()
        hablar("lumiDemo")
        limpiarAcciones()
        accion("saving_toggle") {
            prefs().edit().putBoolean("ahorro", !ahorro).apply()
            mostrarBateria()
        }
        accion("back") { volver() }
    }

    private fun prefs() = getSharedPreferences(Textos.PREFS, Context.MODE_PRIVATE)

    private fun mostrarAjustes() {
        val voz = prefs().getBoolean("voz", true)
        val vpn = prefs().getBoolean("vpn", false)
        val opciones = arrayOf(
            t("language"),
            "${t("voice_on")}: ${if (voz) t("ok") else t("cancel")}",
            "${t("vpn")}: ${if (vpn) t("vpn_on") else t("vpn_off")}",
            t("web"), t("apk"), t("qr"), t("windows"),
            t("rules_note"), t("back")
        )
        AlertDialog.Builder(this)
            .setTitle(t("settings"))
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> elegirIdioma()
                    1 -> prefs().edit().putBoolean("voz", !voz).apply()
                    2 -> configurarVpn()
                    3 -> abrirUrl("https://deancavero23-ops.github.io/Lumen/")
                    4 -> abrirUrl("https://github.com/deancavero23-ops/Lumen/releases/download/v1.0.0/Lumen-1.0.0.apk")
                    5 -> mostrarQr()
                    6 -> abrirUrl("https://github.com/deancavero23-ops/Lumen/releases/latest")
                    7 -> toast(t("rules_note"))
                    else -> volver()
                }
            }
            .show()
    }

    private fun configurarVpn() {
        val activo = prefs().getBoolean("vpn", false)
        AlertDialog.Builder(this)
            .setTitle(t("vpn"))
            .setMessage(t("vpn_note"))
            .setPositiveButton(if (activo) t("vpn_disable") else t("vpn_enable")) { _, _ ->
                if (activo) detenerVpn() else pedirPermisoVpn()
            }
            .setNegativeButton(t("cancel"), null)
            .show()
    }

    private fun pedirPermisoVpn() {
        val permiso = VpnService.prepare(this)
        if (permiso != null) startActivityForResult(permiso, 43) else iniciarVpn()
    }

    private fun iniciarVpn() {
        try {
            val servicio = Intent(this, BloqueoVpn::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(servicio)
            } else {
                @Suppress("DEPRECATION")
                startService(servicio)
            }
            prefs().edit().putBoolean("vpn", true).apply()
            toast(t("vpn_active"))
        } catch (e: Exception) {
            toast(t("vpn_error"))
        }
    }

    private fun detenerVpn() {
        stopService(Intent(this, BloqueoVpn::class.java))
        prefs().edit().putBoolean("vpn", false).apply()
        toast(t("vpn_stopped"))
    }

    private fun elegirIdioma() {
        val actual = Textos.idioma(this)
        val nombres = arrayOf(t("lang_system"), "Español", "Italiano", "English")
        val seleccion = when (actual) { "es" -> 1; "it" -> 2; "en" -> 3; else -> 0 }
        AlertDialog.Builder(this)
            .setTitle(t("language"))
            .setSingleChoiceItems(nombres, seleccion) { dialog, which ->
                val lang = when (which) { 1 -> "es"; 2 -> "it"; 3 -> "en"; else -> "" }
                prefs().edit().putString(Textos.LANG, lang).apply()
                dialog.dismiss()
                hablar("lumiHola")
                render()
            }
            .show()
    }

    private fun mostrarQr() {
        val bitmap = try {
            assets.open("qr-android-apk.png").use { BitmapFactory.decodeStream(it) }
        } catch (e: Exception) { null }
        if (bitmap == null) {
            abrirUrl("https://deancavero23-ops.github.io/Lumen/img/qr-android-apk.png")
            return
        }
        val img = ImageView(this).apply {
            setImageBitmap(bitmap)
            adjustViewBounds = true
            layoutParams = ViewGroup.LayoutParams(dp(300), dp(300))
        }
        AlertDialog.Builder(this)
            .setTitle(t("qr"))
            .setView(img)
            .setPositiveButton(t("ok"), null)
            .show()
    }

    private fun abrirUrl(url: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (e: Exception) { toast(t("error")) }
    }
}
