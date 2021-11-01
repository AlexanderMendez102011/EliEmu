package variables.mob

import java.util.Map
import sprites.PreLuchador
import variables.hechizo.StatHechizo
import variables.mapa.Celda
import variables.pelea.Pelea
import variables.stats.Stats
import variables.stats.TotalStats

class MobGrado(mobGrado: MobGradoModelo, mobModelo: MobModelo, grado: Byte, nivel: Short,
               pdvMax: Int, stats: Stats?) : PreLuchador {
    private var _grupoMob: GrupoMob? = null
    private val _mobGradoModelo: MobGradoModelo
    private val _mobModelo: MobModelo
    private val _grado: Byte
    private val _nivel: Short
    var iD = 0
        private set
    var pDV: Int
    var pDVMax: Int
        private set
    private var _celdaPelea: Celda? = null
    private val _stats: Stats
    private val _totalStats: TotalStats
    fun setIDPersonal(id: Int) {
        iD = id
    }

    fun setGrupoMob(gm: GrupoMob?) {
        _grupoMob = gm
    }

    val baseXp: Long
        get() = _mobGradoModelo.getBaseXp()
    val hechizos: Map<Any, Any>
        get() = _mobGradoModelo.getHechizos()
    val stats: Stats
        get() = _stats
    val iDModelo: Int
        get() = _mobModelo.getID()
    val mobGradoModelo: MobGradoModelo
        get() = _mobGradoModelo
    val mobModelo: MobModelo
        get() = _mobModelo
    var celdaPelea: Celda?
        get() = _celdaPelea
        set(celda) {
            _celdaPelea = celda
        }
    val grado: Int
        get() = _grado.toInt()

    fun setPDVMAX(pdv: Int) {
        pDVMax = pdv
    }

    val nivel: Int
        get() = _nivel.toInt()

    fun getGfxID(buff: Boolean): Int {
        return _mobGradoModelo.getGfxID()
    }

    val totalStatsPelea: TotalStats
        get() = _totalStats
    val alineacion: Byte
        get() = _mobModelo.getAlineacion()

    fun stringGMLuchador(): String {
        val str = StringBuilder()
        str.append("-2;")
        str.append(_mobGradoModelo.getGfxID().toString() + "^" + _mobModelo.getTalla() + ";")
        str.append("$_grado;")
        str.append(_mobModelo.getColores().replace(",", ";").toString() + ";")
        str.append(_mobModelo.getStringAccesorios().toString() + ";")
        return str.toString()
    }

    val deshonor: Int
        get() = 0
    val honor: Int
        get() = 0
    val gradoAlineacion: Int
        get() = 1

    fun addHonor(honor: Int) {}
    fun addDeshonor(honor: Int): Boolean {
        return false
    }

    fun addXPGanada(exp: Long) {}
    fun setPelea(pelea: Pelea?) {}
    fun actualizarAtacantesDefensores() {}
    fun murio() {}
    fun sobrevivio() {}
    @Override
    fun addKamasGanada(kamas: Double) {
        if (_grupoMob != null) _grupoMob.addKamasHeroico(kamas.toLong())
    }

    // public void destruir() {
    // try {
    // this.finalize();
    // } catch (Throwable e) {}
    // }
    init {
        _mobGradoModelo = mobGrado
        _mobModelo = mobModelo
        _stats = Stats(stats)
        _totalStats = TotalStats(_stats, null, Stats(), null, 2)
        _grado = grado
        _nivel = nivel
        pDVMax = pdvMax
        pDV = pDVMax
    }
}