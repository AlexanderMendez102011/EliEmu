package variables.gremio

import java.util.ArrayList
import java.util.HashMap
import java.util.Map
import java.util.Map.Entry
import java.util.TreeMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.regex.Pattern
import variables.casa.Casa
import variables.hechizo.Hechizo
import variables.hechizo.StatHechizo
import variables.mapa.Cercado
import variables.montura.Montura
import variables.personaje.Personaje
import variables.stats.Stats
import estaticos.Constantes
import estaticos.GestorSQL
import estaticos.GestorSalida
import estaticos.MainServidor
import estaticos.Mundo

class Gremio {
    var nroMaxRecau = 0
    var nivel: Short = 1
        private set
    private var _capital: Short = 0
    val iD: Int
    var experiencia: Long = 0
        private set
    var nombre = ""
    var emblema = ""
    var anuncio = ""
        private set
    private val _hechizos: Map<Integer, StatHechizo> = hechizosPrimarios(
            "462;0|461;0|460;0|459;0|458;0|457;0|456;0|455;0|454;0|453;0|452;0|451;0")
    private val _statsRecolecta: Map<Integer, Integer?> = HashMap<Integer, Integer>()
    private val _tiempoMapaRecolecta: Map<Integer, Long> = HashMap<Integer, Long>()
    private val _miembros: Map<Integer, MiembroGremio> = HashMap<Integer, MiembroGremio>()
    private val _recaudadores: CopyOnWriteArrayList<Recaudador> = CopyOnWriteArrayList()
    private val _statsPelea: Stats = Stats()

    constructor(dueño: Personaje?, nombre: String, emblema: String) {
        iD = Mundo.sigIDGremio()
        this.nombre = nombre
        this.emblema = emblema
        experiencia = 0
        decompilarStats("176;100|158;1000|124;0")
    }

    constructor(id: Int, nombre: String, emblema: String, nivel: Short, xp: Long,
                capital: Short, nroMaxRecau: Byte, hechizos: String, stats: String, anuncio: String) {
        iD = id
        addExperiencia(xp, true)
        this.nombre = nombre
        this.emblema = emblema
        _capital = capital
        this.nroMaxRecau = nroMaxRecau.toInt()
        decompilarHechizos(hechizos)
        decompilarStats(stats)
        this.anuncio = anuncio
    }

    fun addMiembro(id: Int, rango: Int, expDonada: Long, porcXPDonar: Byte,
                   derechos: Int): MiembroGremio {
        val miembro = MiembroGremio(id, this, rango, expDonada, porcXPDonar, derechos)
        _miembros.put(id, miembro)
        return miembro
    }

    fun SetAnuncio(anuncion: String) {
        anuncio = anuncion
    }

    val cantRecaudadores: Int
        get() = _recaudadores.size()

    fun addRecaudador(r: Recaudador?) {
        _recaudadores.add(r)
    }

    fun delRecaudador(r: Recaudador?) {
        _recaudadores.remove(r)
    }

    fun eliminarTodosRecaudadores() {
        for (r in _recaudadores) {
            r.borrarRecaudador()
        }
    }

    val infoGremio: String
        get() = nombre + "," + getStatRecolecta(Constantes.STAT_MAS_PODS) + "," + getStatRecolecta(
                Constantes.STAT_MAS_PROSPECCION) + "," + getStatRecolecta(Constantes.STAT_MAS_SABIDURIA) + "," + _recaudadores
                .size()
    val capital: Int
        get() = _capital.toInt()

    fun addCapital(nro: Int) {
        (_capital += nro.toShort()).toShort()
    }

    val hechizos: Map<Any, Any>
        get() = _hechizos

    fun olvidarHechizo(hechizoID: Int, porCompleto: Boolean): Boolean {
        val h: StatHechizo = _hechizos[hechizoID] ?: return false
        for (i in 1 until h.getGrado()) {
            _capital += i.toShort()
        }
        return fijarNivelHechizoOAprender(hechizoID, if (porCompleto) 0 else 1, false)
    }

    fun boostHechizo(hechizoID: Int): Boolean {
        if (!_hechizos.containsKey(hechizoID)) {
            return false
        }
        val SH: StatHechizo? = _hechizos[hechizoID]
        if (SH != null && SH.getGrado() >= 5) {
            return false
        }
        val hechizo: Hechizo = Mundo.getHechizo(hechizoID) ?: return false
        val nivel = if (SH == null) 1 else SH.getGrado() + 1
        return fijarNivelHechizoOAprender(hechizoID, nivel, false)
    }

    fun fijarNivelHechizoOAprender(hechizoID: Int, nivel: Int, mensaje: Boolean): Boolean {
        if (nivel > 0) {
            val hechizo: Hechizo = Mundo.getHechizo(hechizoID) ?: return false
            val statHechizo: StatHechizo = hechizo.getStatsPorNivel(nivel)
            if (statHechizo == null || statHechizo.getNivelRequerido() > this.nivel) {
                return false
            }
            _hechizos.put(hechizoID, statHechizo)
        } else {
            _hechizos.put(hechizoID, null)
        }
        return true
    }

    val statsPelea: Stats
        get() = _statsPelea
    val cantidadMiembros: Int
        get() = _miembros.size()

    fun infoPanelGremio(): String {
        val xpMin: Long = Mundo.getExpGremio(nivel)
        val xpMax: Long = Mundo.getExpGremio(nivel + 1)
        return ("gIG" + (if (cantidadMiembros >= 10) 1 else 0) + "|" + nivel + "|" + xpMin + "|"
                + experiencia + "|" + xpMax)
    }

    fun analizarMiembrosGM(): String {
        val str = StringBuilder()
        for (miembro in _miembros.values()) {
            if (miembro.getPersonaje() == null) {
                continue
            }
            if (str.length() > 0) {
                str.append("|")
            }
            var hola: Int = miembro.getNivel()
            var esOmega = 0
            if (miembro.getPersonaje().getNivelOmega() > 0) {
                hola = miembro.getPersonaje().getNivelOmega()
                esOmega = miembro.getPersonaje().getNivelOmega()
            } else {
                hola = miembro.getNivel()
                esOmega = 0
            }
            str.append(miembro.getID().toString() + ";")
            str.append(miembro.getNombre().toString() + ";")
            str.append("$hola;")
            str.append(miembro.getGfx().toString() + ";")
            str.append(miembro.getRango().toString() + ";")
            str.append(miembro.getXpDonada().toString() + ";")
            str.append(miembro.getPorcXpDonada().toString() + ";")
            str.append(miembro.getDerechos().toString() + ";")
            str.append((if (miembro.getPersonaje().enLinea()) 1 else 0).toString() + ";")
            str.append(miembro.getPersonaje().getAlineacion().toString() + ";")
            str.append(miembro.getHorasDeUltimaConeccion().toString() + ";")
            str.append(esOmega)
        }
        return str.toString()
    }

    val miembros: ArrayList<Personaje>
        get() {
            val a: ArrayList<Personaje> = ArrayList<Personaje>()
            for (miembro in _miembros.values()) {
                a.add(miembro.getPersonaje())
            }
            return a
        }

    // public Collection<MiembroGremio> getMiembros() {
    // return _miembros.values();
    // }
    fun getMiembro(idMiembro: Int): MiembroGremio? {
        return _miembros[idMiembro]
    }

    fun expulsarTodosMiembros() {
        val a: ArrayList<MiembroGremio> = ArrayList<MiembroGremio>()
        a.addAll(_miembros.values())
        for (miembro in a) {
            expulsarMiembro(miembro.getID())
        }
    }

    fun expulsarMiembro(persoID: Int) {
        val casa: Casa = Mundo.getCasaDePj(persoID)
        if (casa != null) {
            casa.nullearGremio()
            casa.actualizarDerechos(0)
        }
        _miembros.remove(persoID)
        GestorSQL.DELETE_MIEMBRO_GREMIO(persoID)
        val perso: Personaje = Mundo.getPersonaje(persoID)
        if (perso != null) {
            perso.setMiembroGremio(null)
            if (perso.enLinea() && perso.getPelea() == null) {
                GestorSalida.ENVIAR_Oa_CAMBIAR_ROPA_MAPA(perso.getMapa(), perso)
            }
        }
    }

    fun addExperiencia(xp: Long, sinPuntos: Boolean) {
        experiencia += xp
        val nivelAnt = nivel.toInt()
        while (experiencia >= Mundo.getExpGremio(nivel + 1) && nivel < MainServidor.NIVEL_MAX_GREMIO) {
            subirNivel(sinPuntos)
        }
        if (!sinPuntos) {
            if (nivel.toInt() != nivelAnt) {
                refrescarStatsPelea()
            }
        }
    }

    fun subirNivel(sinPuntos: Boolean) {
        (nivel += 1).toShort()
        if (!sinPuntos) {
            (_capital += 5).toShort()
        }
    }

    fun refrescarStatsPelea() {
        val stats: Map<Integer, Integer> = TreeMap<Integer, Integer>()
        stats.put(Constantes.STAT_MAS_PA, 6)
        stats.put(Constantes.STAT_MAS_PM, 5)
        stats.put(Constantes.STAT_MAS_SABIDURIA, getStatRecolecta(Constantes.STAT_MAS_SABIDURIA))
        stats.put(Constantes.STAT_MAS_DAÑOS, nivel.toInt())
        val statsIDs = intArrayOf(Constantes.STAT_MAS_RES_PORC_NEUTRAL, Constantes.STAT_MAS_RES_PORC_TIERRA,
                Constantes.STAT_MAS_RES_PORC_FUEGO, Constantes.STAT_MAS_RES_PORC_AIRE, Constantes.STAT_MAS_RES_PORC_AGUA,
                Constantes.STAT_MAS_ESQUIVA_PERD_PA, Constantes.STAT_MAS_ESQUIVA_PERD_PM)
        val resistencia: Int = Math.min(50, nivel)
        for (s in statsIDs) {
            stats.put(s, resistencia)
        }
        _statsPelea.nuevosStats(stats)
    }

    fun addUltRecolectaMapa(mapaID: Int) {
        _tiempoMapaRecolecta.put(mapaID, System.currentTimeMillis())
    }

    fun puedePonerRecaudadorMapa(mapaID: Int): Boolean {
        if (MainServidor.HORAS_VOLVER_A_PONER_RECAUDADOR_MAPA < 1) {
            return true
        }
        if (_tiempoMapaRecolecta.containsKey(mapaID)) {
            val tiempoM: Int = 60 * 60 * 1000 * nivel / MainServidor.HORAS_VOLVER_A_PONER_RECAUDADOR_MAPA
            return _tiempoMapaRecolecta[mapaID]!! + tiempoM <= System.currentTimeMillis()
        }
        return true
    }

    fun analizarInfoCercados(): String {
        val maxCercados: Byte = Math.floor(nivel / 10) as Int.toByte()
        val str = StringBuilder(maxCercados)
        for (cercados in Mundo.CERCADOS.values()) {
            if (cercados.getGremio() === this) {
                str.append("|" + cercados.getMapa().getID().toString() + ";" + cercados.getCapacidadMax().toString() + ";" + cercados
                        .getCantObjMax())
                if (cercados.getCriando().size() > 0) {
                    str.append(";")
                    var primero = false
                    for (DP in cercados.getCriando().values()) {
                        if (DP == null) {
                            continue
                        }
                        if (primero) {
                            str.append(",")
                        }
                        str.append(DP.getColor().toString() + "," + DP.getNombre() + ",")
                        if (Mundo.getPersonaje(DP.getDueñoID()) == null) {
                            str.append("SIN DUEÑO")
                        } else {
                            str.append(Mundo.getPersonaje(DP.getDueñoID()).getNombre())
                        }
                        primero = true
                    }
                }
            }
        }
        return str.toString()
    }

    private fun hechizosPrimarios(strHechizo: String): Map<Integer, StatHechizo> {
        val hechizos: TreeMap<Integer, StatHechizo> = TreeMap<Integer, StatHechizo>()
        for (split in strHechizo.split(Pattern.quote("|"))) {
            try {
                val id: Int = Integer.parseInt(split.split(";").get(0))
                val nivel: Int = Integer.parseInt(split.split(";").get(1))
                if (Mundo.getHechizo(id) == null) {
                    continue
                }
                hechizos.put(id, Mundo.getHechizo(id).getStatsPorNivel(nivel))
            } catch (e: Exception) {
            }
        }
        return hechizos
    }

    fun decompilarHechizos(strHechizo: String) {
        for (split in strHechizo.split(Pattern.quote("|"))) {
            try {
                val id: Int = Integer.parseInt(split.split(";").get(0))
                val nivel: Int = Integer.parseInt(split.split(";").get(1))
                _hechizos.put(id, Mundo.getHechizo(id).getStatsPorNivel(nivel))
            } catch (e: Exception) {
            }
        }
    }

    fun decompilarStats(statsStr: String) {
        for (split in statsStr.split(Pattern.quote("|"))) {
            try {
                val stat: Int = Integer.parseInt(split.split(";").get(0))
                val cant: Int = Integer.parseInt(split.split(";").get(1))
                _statsRecolecta.put(stat, cant)
            } catch (e: Exception) {
            }
        }
        refrescarStatsPelea()
    }

    fun compilarHechizo(): String {
        val str = StringBuilder()
        for (statHechizo in _hechizos.entrySet()) {
            if (str.length() > 0) {
                str.append("|")
            }
            str.append(statHechizo.getKey().toString() + ";" + if (statHechizo.getValue() == null) 0 else statHechizo.getValue().getGrado())
        }
        return str.toString()
    }

    fun compilarStats(): String {
        val str = StringBuilder()
        for (stats in _statsRecolecta.entrySet()) {
            if (str.length() > 0) {
                str.append("|")
            }
            str.append(stats.getKey().toString() + ";" + stats.getValue())
        }
        return str.toString()
    }

    fun getStatRecolecta(id: Int): Int {
        var stats = if (_statsRecolecta[id] != null) _statsRecolecta[id] else 0
        if (MainServidor.RECAUDADOR_STATS_GREMIO.containsKey(id)) {
            stats += MainServidor.RECAUDADOR_STATS_GREMIO.get(id)
        }
        return stats
    }

    fun addStat(id: Int, add: Int) {
        try {
            _statsRecolecta.put(id, _statsRecolecta[id] + add)
        } catch (e: Exception) {
            _statsRecolecta.put(id, add)
        }
    }

    fun analizarRecauAGremio(): String {
        return (nroMaxRecau.toString() + "|" + _recaudadores.size() + "|" + MainServidor.RECAUDADOR_VIDA_X_NIVEL * nivel + "|" + nivel + "|"
                + getStatRecolecta(Constantes.STAT_MAS_PODS) + "|" + getStatRecolecta(Constantes.STAT_MAS_PROSPECCION) + "|"
                + getStatRecolecta(Constantes.STAT_MAS_SABIDURIA) + "|" + nroMaxRecau + "|" + _capital + "|" + (1000 + (10
                * nivel)) + "|" + compilarHechizo())
    }

    fun analizarRecaudadores(): String {
        val str = StringBuilder()
        for (r in _recaudadores) {
            if (str.length() > 0) {
                str.append("|")
            }
            str.append(r.getInfoPanel())
        }
        return if (str.length() === 0) {
            ""
        } else "+" + str.toString()
    }

    fun actualizarAtacantesDefensores() {
        for (re in _recaudadores) {
            re.actualizarAtacantesDefensores()
        }
    }

    val maxMiembros: Int
        get() {
            var maxMiembros = 40 + nivel * 4
            if (MainServidor.LIMITE_MIEMBROS_GREMIO > 0) {
                maxMiembros = MainServidor.LIMITE_MIEMBROS_GREMIO
            }
            return maxMiembros
        }
}