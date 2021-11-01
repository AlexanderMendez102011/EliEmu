package variables.mapa

import java.util.ArrayList
import variables.zotros.Prisma
import estaticos.Constantes
import estaticos.Mundo

class SubArea(val iD: Int, areaID: Short, val nombre: String, conquistable: Boolean,
              minNivelGrupoMob: Int, maxNivelGrupoMob: Int, cementerio: String) {
    private val _mapas: ArrayList<Mapa> = ArrayList<Mapa>()
    private val _conquistable: Boolean
    private var _alineacion: Byte = Constantes.ALINEACION_NEUTRAL
    private val _area: Area?
    private var _prisma: Prisma? = null
    val minNivelGrupoMob = 0
    val maxNivelGrupoMob = 0
    val cementerio = ""
    var prisma: Prisma?
        get() = _prisma
        set(prisma) {
            _prisma = prisma
            alineacion = if (prisma == null) Constantes.ALINEACION_NEUTRAL else prisma.getAlineacion()
        }

    fun esConquistable(): Boolean {
        return _conquistable
    }

    val area: Area?
        get() = _area
    var alineacion: Byte
        get() {
            if (_area != null) {
                if (_area.getID() === 7) {
                    return Constantes.ALINEACION_BONTARIANO
                }
                if (_area.getID() === 11) {
                    return Constantes.ALINEACION_BRAKMARIANO
                }
            }
            return _alineacion
        }
        private set(alin) {
            if (_alineacion == alin) {
                return
            }
            if (_alineacion == Constantes.ALINEACION_BONTARIANO) {
                BONTAS--
            }
            if (_alineacion == Constantes.ALINEACION_BRAKMARIANO) {
                BRAKMARS--
            }
            if (alin == Constantes.ALINEACION_BONTARIANO) {
                BONTAS++
            }
            if (alin == Constantes.ALINEACION_BRAKMARIANO) {
                BRAKMARS++
            }
            _alineacion = alin
        }
    val mapas: ArrayList<Mapa>
        get() = _mapas

    fun addMapa(mapa: Mapa?) {
        _mapas.add(mapa)
    }

    companion object {
        var BONTAS = 0
        var BRAKMARS = 0
        fun subareasBontas(): Int {
            return BONTAS
        }

        fun subareasBrakmars(): Int {
            return BRAKMARS
        }
    }

    init {
        _area = Mundo.getArea(areaID)
        _conquistable = conquistable
        this.minNivelGrupoMob = minNivelGrupoMob
        this.maxNivelGrupoMob = maxNivelGrupoMob
        this.cementerio = cementerio
    }
}