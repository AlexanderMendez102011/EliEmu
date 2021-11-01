package variables.montura

import java.util.HashMap
import java.util.Map

class MonturaModelo(val colorID: Int, stats: String, color: String, val certificadoModeloID: Int, generacion: Byte) {
    var strColor = ""
    val generacionID: Byte
    private val _stats: Map<Integer, Integer> = HashMap<Integer, Integer>()
    val stats: Map<Any, Any>
        get() = _stats

    init {
        strColor = color
        generacionID = generacion
        if (stats.isEmpty()) {
            return
        }
        for (str in stats.split(";")) {
            try {
                val s: Array<String> = str.split(",")
                _stats.put(Integer.parseInt(s[0]), Integer.parseInt(s[1]))
            } catch (e: Exception) {
            }
        }
    }
}