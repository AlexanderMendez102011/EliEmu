package estaticos

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object TextoAVoz {
    private const val LANG = "es-MX"
    private const val TEXT_TO_SPEECH_SERVICE = ("http://translate.google.com/translate_tts?ie=UTF-8&tl=" + LANG
            + "&client=tw-ob&q=")
    private const val USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:11.0) Gecko/20100101 Firefox/11.0"
    private const val LETRAS = "abcdefghijklmnopqrstuvwxyz����������"
    private const val SIGNOS = ",.-�!�?=$* "
    private const val NUMEROS = "0123456789"
    fun crearMP3(texto: String, idioma: String): String? {
        var texto = texto
        try {
            texto = filtrarTexto(texto).toLowerCase()
            if (contarLetras(texto) > MainServidor.MAX_CARACTERES_SONIDO) {
                return ""
            }
            val nTexto = convertirFile(texto)
            val output = File(MainServidor.DIRECTORIO_LOCAL_MP3 + nTexto + ".mp3")
            if (!output.exists()) {
                var url = TEXT_TO_SPEECH_SERVICE
                if (!idioma.isEmpty()) {
                    url = url.replace("&tl=" + LANG + "&", "&tl=$idioma&")
                }
                url += URLEncoder.encode(texto, "UTF-8")
                val cUrl = URL(url)
                val connection: HttpURLConnection = cUrl.openConnection() as HttpURLConnection
                connection.setRequestMethod("GET")
                connection.addRequestProperty("User-Agent", USER_AGENT)
                connection.connect()
                val bufIn = BufferedInputStream(connection.getInputStream())
                val buffer = ByteArray(1024)
                var n: Int
                val bufOut = ByteArrayOutputStream()
                while (bufIn.read(buffer).also { n = it } > 0) {
                    bufOut.write(buffer, 0, n)
                }
                // Done, save data
                val out = BufferedOutputStream(FileOutputStream(output))
                out.write(bufOut.toByteArray())
                out.flush()
                out.close()
            }
            return nTexto
        } catch (e: Exception) {
            if (MainServidor.ES_LOCALHOST) {
                e.printStackTrace()
            }
        }
        return null
    }

    fun convertirFile(texto: String): String {
        var texto = texto
        texto = texto.replace("?", "#" + '?'.code)
        texto = texto.replace("*", "#" + '*'.code)
        texto = texto.replace(":", "#" + ':'.code)
        return texto
    }

    fun contarLetras(texto: String): Int {
        var cantidad = 0
        for (a in texto.toCharArray()) {
            val b: String = (a.toString() + "").toLowerCase()
            if (LETRAS.contains(b) || NUMEROS.contains(b)) {
                cantidad++
            }
        }
        return cantidad++
    }

    fun filtrarTexto(texto: String): String {
        var texto = texto
        val nuevo = StringBuilder()
        for (a in texto.toCharArray()) {
            val b: String = (a.toString() + "").toLowerCase()
            if (LETRAS.contains(b) || SIGNOS.contains(b) || NUMEROS.contains(b)) {
                nuevo.append(a)
            }
        }
        texto = nuevo.toString()
        for (a in SIGNOS.toCharArray()) {
            while (texto.contains(a.toString() + "" + a)) {
                texto = texto.replace(a.toString() + "" + a, a.toString() + "")
            }
        }
        texto = texto.replace("-", " - ")
        texto = texto.replace(".", ". ")
        texto = texto.replace(",", ", ")
        texto = texto.replace("=", " = ")
        while (texto.contains("  ")) {
            texto = texto.replace("  ", " ")
        }
        return texto
    }
}