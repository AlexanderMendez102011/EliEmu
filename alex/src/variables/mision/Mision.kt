package variables.mision

import java.util.Map
import java.util.Map.Entry
import java.util.TreeMap
import java.util.concurrent.ConcurrentHashMap
import variables.personaje.Personaje
import estaticos.Mundo

class Mision(val iDModelo: Int, estado: Int, etapaID: Int, nivelEtapa: Int, objetivos: String) {
    var etapaID = 0
        private set
    var nivelEtapa = 0
        private set
    var estadoMision = ESTADO_NO_TIENE
        private set
    private val _estadoObjetivos: ConcurrentHashMap<Integer, Integer> = ConcurrentHashMap()
    fun estaCompletada(): Boolean {
        return estadoMision == ESTADO_COMPLETADO
    }

    fun confirmarEtapaActual(perso: Personaje, preConfirma: Boolean): Boolean {
        var p = false
        try {
            val copia: Map<Integer, Integer> = TreeMap()
            copia.putAll(_estadoObjetivos)
            for (e in copia.entrySet()) {
                if (e.getValue() === ESTADO_INCOMPLETO) {
                    val mObjMod: MisionObjetivoModelo = Mundo.getMisionObjetivoModelo(e.getKey())
                    perso.confirmarObjetivo(this, mObjMod, perso, null, preConfirma, 0)
                }
            }
            p = verificaSiCumplioEtapa()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return p
    }

    fun setObjetivoCompletado(objetivoID: Int) {
        if (_estadoObjetivos.get(objetivoID) != null) {
            _estadoObjetivos.put(objetivoID, ESTADO_COMPLETADO)
        }
    }

    val objetivos: ConcurrentHashMap<Integer, Integer>
        get() = _estadoObjetivos

    private fun setNuevosObjetivos() {
        _estadoObjetivos.clear()
        val etapa: MisionEtapaModelo = Mundo.getEtapa(etapaID) ?: return
        for (objMod in etapa.getObjetivosPorNivel(nivelEtapa).values()) {
            _estadoObjetivos.put(objMod.getID(), ESTADO_INCOMPLETO)
        }
    }

    fun verificaSiCumplioEtapa(): Boolean {
        var cumplioLosObjetivos = true
        for (estado in _estadoObjetivos.values()) {
            if (estado == ESTADO_INCOMPLETO) {
                cumplioLosObjetivos = false
                break
            }
        }
        return cumplioLosObjetivos && !verificaSiHayOtroNivelEtapa()
    }

    private fun verificaSiHayOtroNivelEtapa(): Boolean {
        val etapa: MisionEtapaModelo = Mundo.getEtapa(etapaID)
        val obj: TreeMap<Integer, MisionObjetivoModelo> = etapa.getObjetivosPorNivel(nivelEtapa + 1) ?: return false
        nivelEtapa++
        setNuevosObjetivos()
        return true
    }

    fun verificaFinalizoMision(): Boolean {
        val sigEtapa: Int = Mundo.getMision(iDModelo).siguienteEtapa(etapaID)
        etapaID = sigEtapa
        if (sigEtapa == -1) {
            estadoMision = ESTADO_COMPLETADO
            return true
        }
        nivelEtapa = 0
        setNuevosObjetivos()
        return false
    }

    companion object {
        const val ESTADO_COMPLETADO = 1
        const val ESTADO_INCOMPLETO = 2
        const val ESTADO_NO_TIENE = 0
    }

    init {
        estadoMision = estado
        if (!estaCompletada()) {
            this.etapaID = etapaID
            this.nivelEtapa = nivelEtapa
            setNuevosObjetivos()
            if (!objetivos.isEmpty()) {
                for (str in objetivos.split(";")) {
                    try {
                        if (str.isEmpty()) {
                            continue
                        }
                        val duo: Array<String> = str.split(",")
                        val objetivoID: Int = Integer.parseInt(duo[0])
                        val estadoObj: Int = Integer.parseInt(duo[1])
                        if (Mundo.getMisionObjetivoModelo(objetivoID) == null) {
                            continue
                        }
                        _estadoObjetivos.put(objetivoID, estadoObj)
                    } catch (e: Exception) {
                    }
                }
            }
        }
    }
}