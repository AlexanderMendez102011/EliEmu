package variables.objeto

import java.util.Map
import java.util.Map.Entry
import java.util.TreeMap

class CreaTuItem(val iD: Int, _statsMaximos: String, _maxOgrinas: Int, precioBase: Int) {
    private val _statsMaximos: Map<Integer, Integer?> = TreeMap()
    val maxOgrinas: Int
    val precioBase: Int
    fun getMaximoStat(stat: Int): Int {
        return if (_statsMaximos[stat] == null) {
            0
        } else _statsMaximos[stat]
    }

    val maximosStats: String
        get() {
            val s = StringBuilder()
            for (entry in _statsMaximos.entrySet()) {
                if (s.length() > 0) {
                    s.append(",")
                }
                s.append(entry.getKey().toString() + "*" + entry.getValue())
            }
            return s.toString()
        }

    companion object {
        var PRECIOS: Map<Integer, Float> = TreeMap()
    }

    init {
        for (s in _statsMaximos.split("\\|")) {
            try {
                this._statsMaximos.put(Integer.parseInt(s.split(",").get(0)), Integer.parseInt(s.split(",").get(1)))
            } catch (e: Exception) {
            }
        }
        maxOgrinas = _maxOgrinas
        this.precioBase = precioBase
    }
}