package com.lumen.seguridad

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.os.Build
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.ByteBuffer

/**
 * Bloqueo global opcional de anuncios. No crea un túnel completo: solo intercepta las consultas DNS
 * que Android envía a la IP de este VPN y responde 0.0.0.0 a los dominios publicitarios. El resto se
 * reenvía a un DNS público desde la propia app (excluida del VPN). No lee el contenido del tráfico.
 */
class BloqueoVpn : VpnService() {

    companion object {
        const val CANAL = "lumen_vpn"
        var activo = false
    }

    private var salir = false
    private val anuncios = Regex(
        "(^|\\.)(doubleclick\\.net|googleadservices\\.com|googlesyndication\\.com|" +
            "adservice\\.google\\.[a-z.]+|google-analytics\\.com|analytics\\.google\\.com|" +
            "googletagmanager\\.com|connect\\.facebook\\.net|facebook\\.net|" +
            "amazon-adsystem\\.com|adnxs\\.com|criteo\\.com|taboola\\.com|outbrain\\.com|" +
            "scorecardresearch\\.com|quantserve\\.com|pubmatic\\.com|rubiconproject\\.com|" +
            "openx\\.net)(:|$|/)?",
        RegexOption.IGNORE_CASE
    )

    private fun t(clave: String) = Textos.t(this, clave)

    override fun onCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(CANAL, t("vpn_notificacion_titulo"), NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
        }
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (activo) return START_STICKY
        startForeground(1, notificacion())

        val fd = try {
            Builder()
                .setSession(t("app_name"))
                .addAddress("10.0.0.2", 24)
                .addDnsServer("10.0.0.1")
                .setMtu(1500)
                .addDisallowedApplication(packageName)
                .establish()
        } catch (e: Exception) {
            null
        }

        if (fd == null) {
            getSharedPreferences(Textos.PREFS, MODE_PRIVATE).edit().putBoolean("vpn", false).apply()
            stopSelf()
            return START_NOT_STICKY
        }

        activo = true
        Thread { escuchar(fd) }.start()
        return START_STICKY
    }

    override fun onDestroy() {
        salir = true
        activo = false
        getSharedPreferences(Textos.PREFS, MODE_PRIVATE).edit().putBoolean("vpn", false).apply()
        super.onDestroy()
    }

    private fun notificacion(): Notification {
        val abrir = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val constructor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CANAL)
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
        }
        return constructor
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(t("app_name"))
            .setContentText(t("vpn_notificacion_texto"))
            .setContentIntent(abrir)
            .setOngoing(true)
            .build()
    }

    private fun escuchar(fd: android.os.ParcelFileDescriptor) {
        try {
            val entrada = FileInputStream(fd.fileDescriptor)
            val salida = FileOutputStream(fd.fileDescriptor)
            val buf = ByteArray(32_767)
            while (!salir) {
                val n = try {
                    entrada.read(buf)
                } catch (e: Exception) {
                    -1
                }
                if (n <= 0) break
                val respuesta = procesar(buf.copyOf(n)) ?: continue
                salida.write(respuesta)
                salida.flush()
            }
        } catch (e: Exception) {
            // El sistema cerró el túnel; la preferencia se limpia en onDestroy.
        }
    }

    private fun procesar(packet: ByteArray): ByteArray? {
        if (packet.size < 28) return null
        if (((packet[0].toInt() and 0xF0) shr 4) != 4) return null
        val ihl = (packet[0].toInt() and 0x0F) * 4
        if (ihl < 20 || packet.size < ihl + 8) return null
        if ((packet[9].toInt() and 0xFF) != 17) return null // UDP

        val puertoOrigen = ((packet[ihl].toInt() and 0xFF) shl 8) or (packet[ihl + 1].toInt() and 0xFF)
        val puertoDestino = ((packet[ihl + 2].toInt() and 0xFF) shl 8) or (packet[ihl + 3].toInt() and 0xFF)
        if (puertoDestino != 53) return null

        val dns = packet.copyOfRange(ihl + 8, packet.size)
        val pregunta = extraerDominio(dns) ?: return null
        val respuesta = if (anuncios.containsMatchIn(pregunta.nombre)) {
            bloquear(dns, pregunta.fin)
        } else {
            reenviar(dns) ?: return null
        }
        return construirPaquete(packet, ihl, puertoOrigen, puertoDestino, respuesta)
    }

    private data class Dominio(val nombre: String, val fin: Int)

    private fun extraerDominio(dns: ByteArray): Dominio? {
        if (dns.size < 17) return null
        val preguntas = ((dns[4].toInt() and 0xFF) shl 8) or (dns[5].toInt() and 0xFF)
        if (preguntas < 1) return null
        var pos = 12
        val nombre = StringBuilder()
        while (pos < dns.size) {
            val longitud = dns[pos].toInt() and 0xFF
            if (longitud == 0) {
                pos++
                if (pos + 4 > dns.size) return null
                return Dominio(nombre.toString().lowercase(), pos + 4)
            }
            if ((longitud and 0xC0) != 0 || longitud > 63 || pos + 1 + longitud > dns.size) return null
            if (nombre.isNotEmpty()) nombre.append('.')
            nombre.append(String(dns, pos + 1, longitud, Charsets.US_ASCII))
            pos += 1 + longitud
        }
        return null
    }

    /** Respuesta NOERROR con un registro A a 0.0.0.0 para que la app no insista con el dominio. */
    private fun bloquear(dns: ByteArray, fin: Int): ByteArray {
        val pregunta = dns.copyOfRange(12, fin)
        val out = ByteBuffer.allocate(12 + pregunta.size + 16)
        out.put(dns.copyOfRange(0, 2)) // ID
        out.put(byteArrayOf(0x81.toByte(), 0x80.toByte())) // respuesta, sin error
        out.put(byteArrayOf(0x00, 0x01)) // una pregunta
        out.put(byteArrayOf(0x00, 0x01)) // una respuesta
        out.put(byteArrayOf(0x00, 0x00)) // autoridad
        out.put(byteArrayOf(0x00, 0x00)) // adicionales
        out.put(pregunta)
        out.put(byteArrayOf(0xC0.toByte(), 0x0C.toByte())) // puntero a la pregunta
        out.put(byteArrayOf(0x00, 0x01)) // tipo A
        out.put(byteArrayOf(0x00, 0x01)) // clase IN
        out.put(byteArrayOf(0x00, 0x00, 0x00, 0x00)) // TTL
        out.put(byteArrayOf(0x00, 0x04)) // longitud del registro
        out.put(byteArrayOf(0x00, 0x00, 0x00, 0x00)) // 0.0.0.0
        return out.array()
    }

    private fun reenviar(dns: ByteArray): ByteArray? {
        for (servidor in arrayOf("1.1.1.1", "8.8.8.8")) {
            val socket = DatagramSocket()
            try {
                socket.soTimeout = 3000
                socket.send(DatagramPacket(dns, dns.size, InetAddress.getByName(servidor), 53))
                val buffer = ByteArray(4096)
                val paquete = DatagramPacket(buffer, buffer.size)
                socket.receive(paquete)
                return buffer.copyOf(paquete.length)
            } catch (e: Exception) {
                // Se prueba el siguiente servidor.
            } finally {
                socket.close()
            }
        }
        return null
    }

    private fun construirPaquete(
        original: ByteArray,
        ihl: Int,
        puertoOrigen: Int,
        puertoDestino: Int,
        dns: ByteArray
    ): ByteArray {
        val total = 20 + 8 + dns.size
        val ip = ByteArray(total)
        ip[0] = 0x45.toByte()
        ip[1] = 0
        ip[2] = (total shr 8).toByte()
        ip[3] = total.toByte()
        ip[4] = 0
        ip[5] = 0
        ip[6] = 0
        ip[7] = 0
        ip[8] = 64
        ip[9] = 17

        // La respuesta va de DNS (puerto destino original) al solicitante.
        System.arraycopy(original, 16, ip, 12, 4) // IP de origen = DNS consultado
        System.arraycopy(original, 12, ip, 16, 4) // IP de destino = solicitante
        val suma = checksum(ip, 0, 20)
        ip[10] = (suma shr 8).toByte()
        ip[11] = suma.toByte()

        val longitudUdp = 8 + dns.size
        ip[20] = (puertoDestino shr 8).toByte()
        ip[21] = puertoDestino.toByte()
        ip[22] = (puertoOrigen shr 8).toByte()
        ip[23] = puertoOrigen.toByte()
        ip[24] = (longitudUdp shr 8).toByte()
        ip[25] = longitudUdp.toByte()
        ip[26] = 0 // checksum UDP opcional en IPv4
        ip[27] = 0
        System.arraycopy(dns, 0, ip, 28, dns.size)
        return ip
    }

    private fun checksum(buf: ByteArray, inicio: Int, longitud: Int): Int {
        var suma = 0
        var i = inicio
        while (i + 1 < inicio + longitud) {
            suma += ((buf[i].toInt() and 0xFF) shl 8) or (buf[i + 1].toInt() and 0xFF)
            i += 2
        }
        if (i < inicio + longitud) suma += (buf[i].toInt() and 0xFF) shl 8
        while ((suma shr 16) > 0) suma = (suma and 0xFFFF) + (suma shr 16)
        return suma.inv() and 0xFFFF
    }
}
