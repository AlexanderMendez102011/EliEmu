package variables.objeto

import java.util.ArrayList
import java.util.Map
import java.util.TreeMap
import variables.stats.Stats
import estaticos.Constantes
import estaticos.MainServidor
import estaticos.Mundo

class ObjetoSet(val iD: Int, val nombre: String, objetos: String) {
    private val _objetosModelos: ArrayList<ObjetoModelo> = ArrayList<ObjetoModelo>()
    private val _bonus: Map<Integer, Stats> = TreeMap()
    fun setStats(str: String, cantObjetos: Int) {
        if (str.isEmpty()) {
            return
        }
        val stats = Stats()
        convertirStringAStatsSet(stats, str)
        _bonus.put(cantObjetos, stats)
    }

    val objetosModelos: ArrayList<ObjetoModelo>
        get() = _objetosModelos

    fun getBonusStatPorNroObj(numero: Int): Stats? {
        return try {
            _bonus[numero]
        } catch (e: Exception) {
            _bonus[1]
        }
    }

    companion object {
        private fun convertirStringAStatsSet(stats: Stats, strStats: String) {
            for (str in strStats.split(",")) {
                if (str.isEmpty()) {
                    continue
                }
                try {
                    val splitStats: Array<String> = str.split("#")
                    val statID: Int = ObjetoModelo.statSimiliar(Integer.parseInt(splitStats[0], 16))
                    if (Constantes.esStatHechizo(statID)) {
                        stats.addStatHechizo(str)
                    } else if (Constantes.esStatRepetible(statID)) {
                        stats.addStatRepetido(str)
                    } else if (Constantes.esStatTexto(statID)) {
                        stats.addStatTexto(statID, str, true)
                    } else if (Constantes.esEfectoHechizo(statID)) {
                        // no da efectos de da�o
                    } else {
                        val valor: Int = Integer.parseInt(splitStats[1], 16)
                        stats.addStatID(statID, valor)
                    }
                } catch (e: Exception) {
                }
            }
        }
    }

    init {
        _bonus.clear()
        _bonus.put(1, Stats())
        for (s in objetos.split(",")) {
            try {
                val idMod: Int = Integer.parseInt(s.trim())
                val objMod: ObjetoModelo = Mundo.getObjetoModelo(idMod)
                if (objMod != null) {
                    objMod.setSetID(iD)
                    _objetosModelos.add(objMod)
                }
            } catch (e: Exception) {
                MainServidor.redactarLogServidor("El objeto modelo " + s
                        + " no existe y no se le puede asignar a un objeto set")
            }
        }
    }
}