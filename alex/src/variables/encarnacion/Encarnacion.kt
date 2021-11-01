package variables.encarnacion

import java.util.HashMap
import java.util.Map
import java.util.Map.Entry
import variables.hechizo.StatHechizo
import variables.objeto.Objeto
import variables.personaje.Personaje
import variables.stats.Stats
import variables.stats.TotalStats
import estaticos.GestorSalida
import estaticos.MainServidor
import estaticos.Mundo

class Encarnacion(objeto: Objeto, exp: Int, encarnacion: EncarnacionModelo) {
    private val _encarnacionModelo: EncarnacionModelo
    var exp = 0
        private set
    var nivel = 0
        private set
    private var _noLevel = false
    private var _totalStats: TotalStats? = null
    private val _objeto: Objeto
    var ultEquipada: Long = 0
        private set
    private val _hechizos: Map<Integer, StatHechizo> = HashMap()
    private val _posicionHechizos: Map<Integer, Character?> = HashMap()
    fun addExperiencia(exp: Long, perso: Personaje?) {
        if (_noLevel) {
            return
        }
        if (this.exp >= Mundo.getExpEncarnacion(MainServidor.NIVEL_MAX_ENCARNACION)) {
            return
        }
        this.exp += exp.toInt()
        val nivel = nivel
        while (this.exp >= Mundo.getExpEncarnacion(this.nivel + 1) && this.nivel < MainServidor.NIVEL_MAX_ENCARNACION) {
            subirNivel()
        }
        if (nivel != this.nivel && perso != null) {
            if (perso.enLinea()) {
                perso.refrescarStuff(true, false, false)
                GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(perso, _objeto)
            }
        }
    }

    private fun subirNivel() {
        nivel++
        refrescarStatsItem()
        refrescarHechizos()
    }

    fun refrescarHechizos() {
        val nivel = nivel / 10
        for (hechizoID in _posicionHechizos.keySet()) {
            _hechizos.put(hechizoID, Mundo.getHechizo(hechizoID).getStatsPorNivel(nivel + 1))
        }
    }

    fun refrescarStatsItem() {
        if (!_encarnacionModelo.getStatsPorNivel().isEmpty()) {
            for (entry in _encarnacionModelo.getStatsPorNivel().entrySet()) {
                val valor = (entry.getValue() * nivel) as Int
                if (valor > 0) {
                    _objeto.getStats().fijarStatID(entry.getKey(), valor)
                }
            }
        }
    }

    fun stringListaHechizos(): String {
        val nivel = nivel / 10
        val str = StringBuilder()
        for (SH in _posicionHechizos.entrySet()) {
            str.append(SH.getKey().toString() + "~" + (nivel + 1) + "~" + SH.getValue() + ";")
        }
        return str.toString()
    }

    fun tieneHechizoID(hechizoID: Int): Boolean {
        return _posicionHechizos[hechizoID] != null
    }

    val statHechizos: Map<Any, Any>
        get() = _hechizos

    fun getStatsHechizo(hechizoID: Int): StatHechizo? {
        return _hechizos[hechizoID]
    }

    fun setEquipado() {
        ultEquipada = System.currentTimeMillis()
    }

    val iD: Int
        get() = _objeto.getID()
    val totalStats: TotalStats?
        get() = _totalStats
    val gfxID: Int
        get() = _encarnacionModelo.getGfxID()

    fun setPosHechizo(hechizoID: Int, pos: Char, perso: Personaje?) {
        if (pos == 'a') {
            GestorSalida.ENVIAR_BN_NADA(perso, "SET POS HECHIZO - POS INVALIDA")
            return
        }
        if (!tieneHechizoID(hechizoID)) {
            GestorSalida.ENVIAR_BN_NADA(perso, "SET POS HECHIZO - NO TIENE HECHIZO")
            return
        }
        var exID = -1
        if (pos != '_') {
            for (SH in _posicionHechizos.entrySet()) {
                if (SH.getValue() === pos) {
                    exID = SH.getKey()
                    break
                }
            }
        }
        if (exID != -1) {
            _posicionHechizos.put(exID, '_')
        }
        _posicionHechizos.put(hechizoID, pos)
        GestorSalida.ENVIAR_BN_NADA(perso)
    }

    init {
        _objeto = objeto
        _encarnacionModelo = encarnacion
        if (!_encarnacionModelo.getStatsBase().isEmpty()) {
            val statsBase = Stats(_encarnacionModelo.getStatsBase())
            _totalStats = TotalStats(statsBase, Stats(), Stats(), Stats(), 1)
            _noLevel = true
            nivel = 40
        }
        _posicionHechizos.putAll(encarnacion.getPosicionsHechizos())
        addExperiencia(exp.toLong(), null)
        refrescarHechizos()
    }
}