package variables.npc

import java.util.ArrayList
import java.util.Map
import java.util.TreeMap

class ObjetoTrueque(val iD: Int, necesita: String, prioridad: Int, npcs: String) : Comparable<ObjetoTrueque?> {
    private val _necesita: Map<Integer, Integer> = TreeMap<Integer, Integer>()
    val prioridad: Int
    private var _npcs: ArrayList<Integer>? = null
    val necesita: Map<Any, Any>
        get() = _necesita

    fun permiteNPC(id: Int): Boolean {
        return _npcs == null || _npcs.isEmpty() || _npcs.contains(id)
    }

    operator fun compareTo(obj: ObjetoTrueque): Int {
        val otro = obj.prioridad.toLong()
        if (otro > prioridad) {
            return 1
        }
        if (otro == prioridad.toLong()) {
            return 0
        }
        return if (otro < prioridad) {
            -1
        } else 0
    }

    init {
        for (s in necesita.split(";")) {
            try {
                _necesita.put(Integer.parseInt(s.split(",").get(0)), Integer.parseInt(s.split(",").get(1)))
            } catch (e: Exception) {
            }
        }
        this.prioridad = prioridad
        if (!npcs.isEmpty()) {
            for (s in npcs.split(",")) {
                if (s.isEmpty()) {
                    continue
                }
                try {
                    if (_npcs == null) {
                        _npcs = ArrayList()
                    }
                    _npcs.add(Integer.parseInt(s))
                } catch (e: Exception) {
                }
            }
        }
    }
}