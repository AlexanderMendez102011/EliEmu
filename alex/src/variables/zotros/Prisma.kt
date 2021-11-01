package variables.zotros

import java.util.Map
import java.util.TreeMap
import sprites.PreLuchador
import variables.hechizo.StatHechizo
import variables.mapa.Area
import variables.mapa.Celda
import variables.mapa.Mapa
import variables.mapa.SubArea
import variables.mob.MobGradoModelo
import variables.pelea.Luchador
import variables.pelea.Pelea
import variables.personaje.Personaje
import variables.stats.Stats
import variables.stats.TotalStats
import estaticos.Constantes
import estaticos.MainServidor
import estaticos.GestorSalida
import estaticos.Mundo

class Prisma(val iD: Int, val alineacion: Byte, nivel: Byte, mapaID: Int, celdaID: Short,
             honor: Int, area: Int, subArea: Int, tiempoProteccion: Long) : PreLuchador {
    var honor = 0
        private set
    var pDV = 0
        private set
        get() = field
    set
    private val _dir: Byte
    var gradoAlineacion: Int
        private set
        get() = field
    set
    var tiempoProteccion: Long
        private set
    private val _mapa: Mapa
    private val _celda: Celda
    private var _idMob: Short = 0
    private val _gfx: Short
    private val _area: Area?
    private val _subArea: SubArea
    private var _pelea: Pelea? = null
    private val _stats: Stats = Stats()
    private val _totalStats: TotalStats = TotalStats(_stats, null, Stats(), null, 4)
    private val _hechizos: Map<Integer, StatHechizo> = TreeMap<Integer, StatHechizo>()
    fun addTiempProtecion(segundos: Int) {
        var l: Long = Math.max(System.currentTimeMillis(), tiempoProteccion)
        l += segundos * 1000L
        if (l < System.currentTimeMillis()) {
            l = 0
        }
        tiempoProteccion = l
    }

    val tiempoRestProteccion: Long
        get() {
            var l: Long = tiempoProteccion - System.currentTimeMillis()
            if (l < 0) {
                l = 0
            }
            return l
        }
    val area: Area?
        get() = _area
    val subArea: SubArea
        get() = _subArea
    val hechizos: Map<Any, Any>
        get() = _hechizos

    private fun actualizarStats(mob: MobGradoModelo) {
        _stats.nuevosStats(mob.getStats())
        _stats.fijarStatID(Constantes.STAT_MAS_INICIATIVA, gradoAlineacion * 1000)
        _hechizos.putAll(mob.getHechizos())
        pDV = mob.getPDVMAX()
    }

    val estadoPelea: Int
        get() {
            if (_pelea == null) {
                return -1
            }
            if (_pelea.getFase() === Constantes.PELEA_FASE_POSICION) {
                return 0
            }
            return if (_pelea.getFase() === Constantes.PELEA_FASE_COMBATE) {
                -2
            } else 4
        }
    var pelea: Pelea?
        get() = _pelea
        set(pelea) {
            _pelea = pelea
        }

    fun addHonor(honor: Int) {
        this.honor += honor
        val nivel = gradoAlineacion
        if (this.honor < 0) {
            this.honor = 0
        } else if (this.honor >= Mundo.getExpAlineacion(MainServidor.NIVEL_MAX_ALINEACION)) {
            gradoAlineacion = MainServidor.NIVEL_MAX_ALINEACION
            this.honor = Mundo.getExpAlineacion(MainServidor.NIVEL_MAX_ALINEACION)
        }
        for (n in 1..MainServidor.NIVEL_MAX_ALINEACION) {
            if (this.honor < Mundo.getExpAlineacion(n)) {
                gradoAlineacion = (n - 1).toByte().toInt()
                break
            }
        }
        if (nivel != gradoAlineacion) {
            val mob: MobGradoModelo = Mundo.getMobModelo(_idMob).getGradoPorNivel(Math.ceil(gradoAlineacion / 2f) as Int)
            actualizarStats(mob)
        }
    }

    fun stringGM(): String {
        return if (_pelea != null) {
            ""
        } else _celda.getID().toString() + ";" + _dir + ";0;" + iD + ";" + _idMob + ";-10;" + _gfx + "^100;" + gradoAlineacion + ";" + gradoAlineacion
                + ";" + alineacion
    }

    fun atacantesDePrisma(): String {
        val str = StringBuilder("+" + Integer.toString(iD, 36))
        for (luchador in _pelea.luchadoresDeEquipo(1)) {
            val perso: Personaje = luchador.getPersonaje() ?: continue
            str.append("|" + Integer.toString(perso.getID(), 36).toString() + ";")
            str.append(perso.getNombre().toString() + ";")
            str.append(perso.getNivel().toString() + ";")
        }
        return str.toString()
    }

    fun defensoresDePrisma(): String {
        val str = StringBuilder("+" + Integer.toString(iD, 36))
        val stra = StringBuilder("-")
        for (luchador in _pelea.luchadoresDeEquipo(2)) {
            val perso: Personaje = luchador.getPersonaje() ?: continue
            str.append("|" + Integer.toString(perso.getID(), 36).toString() + ";")
            str.append(perso.getNombre().toString() + ";")
            str.append(perso.getGfxID(false).toString() + ";")
            str.append(perso.getNivel().toString() + ";")
            if (_pelea.cantLuchDeEquipo(2) >= 8) {
                str.append("1;")
            } else {
                str.append("0;")
            }
        }
        stra.append(str.substring(1))
        _pelea.setListaDefensores(stra.toString())
        return str.toString()
    }

    fun actualizarAtacantesDefensores() {
        val str = atacantesDePrisma()
        val str2 = defensoresDePrisma()
        for (perso in Mundo.getPersonajesEnLinea()) {
            if (perso.getAlineacion() === alineacion) {
                GestorSalida.ENVIAR_CP_INFO_DEFENSORES_PRISMA(perso, str2)
                GestorSalida.ENVIAR_Cp_INFO_ATACANTES_PRISMA(perso, str)
            }
        }
    }

    fun analizarPrismas(alineacion: Byte): String {
        return if (alineacion != this.alineacion) {
            "-3"
        } else if (estadoPelea == 0) {
            "0;" + pelea.getTiempoFaltInicioPelea().toString() + ";" + (MainServidor.SEGUNDOS_INICIO_PELEA * 1000).toString() + ";7"
        } else {
            estadoPelea.toString() + ""
        }
    }

    val mapa: Mapa
        get() = _mapa
    val celda: Celda
        get() = _celda

    fun getGfxID(buff: Boolean): Int {
        return _gfx.toInt()
    }

    val totalStatsPelea: TotalStats
        get() = _totalStats

    fun stringGMLuchador(): String {
        val str = StringBuilder()
        str.append("-2;")
        str.append((if (alineacion.toInt() == 1) 8101 else 8100).toString() + "^100;")
        str.append("$gradoAlineacion;")
        str.append("-1;-1;-1;")
        str.append("0,0,0,0;")
        return str.toString()
    }

    fun sobrevivio() {
        val str: String = _mapa.getID().toString() + "|" + _mapa.getX() + "|" + _mapa.getY()
        for (pj in Mundo.getPersonajesEnLinea()) {
            if (pj.getAlineacion() === alineacion) {
                GestorSalida.ENVIAR_CS_MENSAJE_SOBREVIVIO_PRISMA(pj, str)
            }
        }
        pelea = null
        GestorSalida.ENVIAR_GM_PRISMA_A_MAPA(_mapa, "+" + stringGM())
    }

    fun murio() {
        val str: String = _mapa.getID().toString() + "|" + _mapa.getX() + "|" + _mapa.getY()
        for (pj in Mundo.getPersonajesEnLinea()) {
            if (pj.getAlineacion() === alineacion) {
                GestorSalida.ENVIAR_CD_MENSAJE_MURIO_PRISMA(pj, str)
            }
            if (_area != null) {
                GestorSalida.ENVIAR_aM_CAMBIAR_ALINEACION_AREA(pj, _area.getID(), -1.toByte())
            }
            GestorSalida.ENVIAR_am_CAMBIAR_ALINEACION_SUBAREA(pj, _subArea.getID(), Constantes.ALINEACION_NULL, true)
            GestorSalida.ENVIAR_am_CAMBIAR_ALINEACION_SUBAREA(pj, _subArea.getID(), Constantes.ALINEACION_NEUTRAL, false)
        }
        GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(_mapa, iD)
        Mundo.eliminarPrisma(this)
    }

    val deshonor: Int
        get() = 0

    fun addDeshonor(honor: Int): Boolean {
        return false
    }

    @Override
    fun addXPGanada(exp: Long) {
        // TODO Auto-generated method stub
    }

    @Override
    fun addKamasGanada(kamas: Double) {
        // TODO Auto-generated method stub
    }

    // public void destruir() {
    // try {
    // this.finalize();
    // } catch (Throwable e) {}
    // }
    //
    init {
        gradoAlineacion = nivel.toInt()
        _mapa = Mundo.getMapa(mapaID)
        _celda = _mapa.getCelda(celdaID)
        _dir = 1
        _idMob = if (alineacion == Constantes.ALINEACION_BONTARIANO) {
            1111
        } else {
            1112
        }
        val mob: MobGradoModelo = Mundo.getMobModelo(_idMob).getGradoPorNivel(Math.ceil(gradoAlineacion / 2f) as Int)
        actualizarStats(mob)
        _gfx = mob.getGfxID()
        this.honor = honor
        _subArea = Mundo.getSubArea(subArea)
        _area = Mundo.getArea(area)
        this.tiempoProteccion = tiempoProteccion
    }
}