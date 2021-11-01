package variables.mapa

import java.util.ArrayList

class Area(val iD: Int, superArea: Short, val nombre: String) {
    private val _subAreas: ArrayList<SubArea> = ArrayList<SubArea>()
    private var _alineacion: Byte = Constantes.ALINEACION_NEUTRAL
    private var _superArea: SuperArea?
    private var _prisma: Prisma? = null
    var prisma: Prisma?
        get() = _prisma
        set(prisma) {
            _prisma = prisma
            alineacion = if (prisma == null) Constantes.ALINEACION_NEUTRAL else prisma.getAlineacion()
        }
    var alineacion: Byte
        get() {
            if (iD == 7) {
                return Constantes.ALINEACION_BONTARIANO
            }
            return if (iD == 11) {
                Constantes.ALINEACION_BRAKMARIANO
            } else _alineacion
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
    val superArea: SuperArea?
        get() = _superArea

    fun addSubArea(sa: SubArea?) {
        _subAreas.add(sa)
    }

    val subAreas: ArrayList<SubArea>
        get() = _subAreas
    val mapas: ArrayList<Mapa>
        get() {
            val mapas: ArrayList<Mapa> = ArrayList<Mapa>()
            for (SA in _subAreas) {
                mapas.addAll(SA.getMapas())
            }
            return mapas
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
        _superArea = Mundo.getSuperArea(superArea)
        if (_superArea == null) {
            _superArea = SuperArea(superArea)
            Mundo.addSuperArea(_superArea)
        }
    }
}