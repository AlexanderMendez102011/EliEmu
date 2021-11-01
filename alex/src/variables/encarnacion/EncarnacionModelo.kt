package variables.encarnacion

import java.util.HashMap
import java.util.Map
import estaticos.Mundo

class EncarnacionModelo(val gfxID: Int, statsBase: String, statsPorNivel: String, strHechizos: String) {
    private val _posicionHechizos: Map<Integer, Character> = HashMap()
    private val _statsBase: Map<Integer, Integer> = HashMap()
    private val _statsPorNivel: Map<Integer, Float> = HashMap()
    private fun analizarStatsBase(statsBase: String) {
        for (s in statsBase.split("\\|")) {
            try {
                if (s.isEmpty()) {
                    continue
                }
                val statID: Int = Integer.parseInt(s.split(",").get(0))
                val valor: Int = Integer.parseInt(s.split(",").get(1))
                _statsBase.put(statID, valor)
            } catch (e1: Exception) {
            }
        }
    }

    private fun analizarStatsPorNivel(statsPorNivel: String) {
        for (s in statsPorNivel.split("\\|")) {
            try {
                if (s.isEmpty()) {
                    continue
                }
                val statID: Int = Integer.parseInt(s.split(",").get(0))
                val valor: Float = Float.parseFloat(s.split(",").get(1))
                _statsPorNivel.put(statID, valor)
            } catch (e1: Exception) {
            }
        }
    }

    private fun analizarPosHechizos(str: String) {
        val hechizos: Array<String> = str.split(";")
        for (s in hechizos) {
            try {
                if (s.isEmpty()) {
                    continue
                }
                val id: Int = Integer.parseInt(s.split(",").get(0))
                if (Mundo.getHechizo(id) == null) {
                    continue
                }
                val pos: Char = s.split(",").get(1).charAt(0)
                _posicionHechizos.put(id, pos)
            } catch (e1: Exception) {
            }
        }
    }

    val posicionsHechizos: Map<Any, Any>
        get() = _posicionHechizos
    val statsBase: Map<Any, Any>
        get() = _statsBase
    val statsPorNivel: Map<Any, Float>
        get() = _statsPorNivel

    init {
        analizarPosHechizos(strHechizos)
        analizarStatsBase(statsBase)
        analizarStatsPorNivel(statsPorNivel)
    }
}