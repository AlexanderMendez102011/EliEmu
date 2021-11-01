package variables.mob

import java.util.ArrayList
import java.util.HashMap
import java.util.List
import java.util.Map
import java.util.Timer
import java.util.TimerTask
import java.util.regex.Pattern
import variables.mapa.Celda
import variables.mapa.Mapa
import variables.mob.AparecerMobs.Aparecer
import variables.mob.MobModelo.TipoGrupo
import variables.objeto.Objeto
import variables.pelea.Pelea
import variables.personaje.Personaje
import estaticos.Camino
import estaticos.Constantes
import estaticos.MainServidor
import estaticos.Encriptador
import estaticos.Formulas
import estaticos.GestorSQL
import estaticos.GestorSalida
import estaticos.Mundo
import estaticos.Mundo.Duo

class GrupoMob {
    private val _tipoGrupo: TipoGrupo
    private var _muerto = false
    var fijo = false
        private set
    private var _movible = false
    private var _evento = false
    private var _orientacion: Byte = 3
    var alineacion: Byte = Constantes.ALINEACION_NULL
        private set
    var distAgresion: Byte = 0
        private set
    var celdaID: Short
    private var _bonusEstrellas: Int = MainServidor.INICIO_BONUS_ESTRELLAS_MOBS
    var jefe = 0
    private var _hechizoJefe = 0
    var iD: Int
    var segundosRespawn = 0
    private val _mobsGradoModelo: ArrayList<MobGradoModelo> = ArrayList<MobGradoModelo>()
    private val _almas: HashMap<Integer, Integer> = HashMap<Integer, Integer>()
    var condInicioPelea = ""
        private set
    var strGrupoMob = ""
        private set
    var condUnirsePelea = ""
    var iodoloJefe = ""
        private set
    private var _timer: Timer? = null
    private var _pelea: Pelea? = null
    private val _objetosHeroico: ArrayList<Integer> = ArrayList<Integer>()
    var kamasHeroico: Long = 0
    private var _mapasRandom: ArrayList<Mapa>? = null

    constructor(posiblesMobs: ArrayList<MobPosible?>?, mapa: Mapa, celdaID: Short,
                maxMobsPorGrupo: Int, minMobsPorGrupo: Int) {
        _tipoGrupo = TipoGrupo.NORMAL
        if (posiblesMobs == null || posiblesMobs.isEmpty()) {
            return
        }
        if (maxMobsPorGrupo < minMobsPorGrupo) {
            return
        }
        val nroMobs: Int = Formulas.getRandomInt(minMobsPorGrupo, maxMobsPorGrupo)
        // if (nroMobs > 8) {
        // nroMobs = 8;
        // }
        this.celdaID = if (celdaID.toInt() == -1) mapa.getRandomCeldaIDLibre() else celdaID
        if (celdaID.toInt() == 0) {
            return
        }
        set_movible(true)
        var maxNivel = 0
        var archi = false
        val str = StringBuilder()
        val mobsEscogidos: ArrayList<MobGradoModelo> = ArrayList()
        val tempPosibles: ArrayList<MobPosible> = ArrayList()
        for (i in 0..7) {
            tempPosibles.clear()
            tempPosibles.addAll(posiblesMobs)
            var nivelTotal = 0
            while (mobsEscogidos.size() < nroMobs && !tempPosibles.isEmpty()) {
                val mp: MobPosible = tempPosibles.get(Formulas.getRandomInt(0, tempPosibles.size() - 1))
                if (mp.getCantMax() > 0) {
                    var cantidad = 0
                    for (m in mobsEscogidos) {
                        if (mp.tieneMob(m)) {
                            cantidad++
                        }
                    }
                    if (cantidad >= mp.getCantMax()) {
                        tempPosibles.remove(mp)
                        continue
                    }
                }
                if (!mp.pasoProbabilidad()) {
                    continue
                }
                val mob: MobGradoModelo = mp.getRandomMob()
                nivelTotal += mob.getNivel()
                mobsEscogidos.add(mob)
            }
            if (mapa.esNivelGrupoMobPermitido(nivelTotal)) {
                break
            }
        }
        for (mg in mobsEscogidos) {
            var mobGrado: MobGradoModelo = mg
            val idMobModelo: Int = mobGrado.getIDModelo()
            if (!archi && !mapa.esMazmorra()) {
                val archiMob: MobModelo = mobGrado.getMobModelo().getArchiMob()
                if (archiMob != null) {
                    if (archiMob.puedeSubArea(mapa.getSubArea().getID())) {
                        val prob: Int = archiMob.getProbabilidadAparecer()
                        if (prob == -1 || prob >= 100 || prob >= Formulas.getRandomInt(1, 100)) {
                            mobGrado = archiMob.getGradoPorGrado(mg.getGrado())
                            archi = true
                        }
                    }
                }
            }
            if (Mundo.MOB_EVENTO > 0) {
                val array: ArrayList<Duo<Integer, Integer>> = Mundo.getMobsEventoDelDia()
                if (array != null && !array.isEmpty()) {
                    for (duo in array) {
                        if (duo._primero === idMobModelo) {
                            try {
                                mobGrado = Mundo.getMobModelo(duo._segundo).getRandomGrado()
                            } catch (e: Exception) {
                            }
                        }
                    }
                }
            }
            if (mobGrado.getNivel() > maxNivel) {
                maxNivel = mobGrado.getNivel()
            }
            if (_almas.containsKey(idMobModelo)) {
                val valor: Int = _almas.get(idMobModelo)
                _almas.remove(idMobModelo)
                _almas.put(idMobModelo, valor + 1)
            } else {
                _almas.put(idMobModelo, 1)
            }
            if (alineacion == Constantes.ALINEACION_NULL) {
                alineacion = mobGrado.getMobModelo().getAlineacion()
            }
            if (distAgresion < mobGrado.getMobModelo().getDistAgresion()) {
                distAgresion = mobGrado.getMobModelo().getDistAgresion()
            }
            if (str.length() > 0) {
                str.append(";")
            }
            str.append(mobGrado.getIDModelo().toString() + "," + mobGrado.getNivel() + "," + mobGrado.getNivel())
            _mobsGradoModelo.add(mobGrado)
        }
        if (_mobsGradoModelo.isEmpty()) {
            return
        }
        iD = mapa.sigIDGrupoMob()
        if (distAgresion.toInt() == 0) {
            distAgresion = Constantes.distAgresionPorNivel(maxNivel)
        }
        if (alineacion == Constantes.ALINEACION_BONTARIANO || alineacion == Constantes.ALINEACION_BRAKMARIANO) {
            distAgresion = 10
        } else if (alineacion == Constantes.ALINEACION_NEUTRAL) {
            distAgresion = 30
        }
        _orientacion = (Formulas.getRandomInt(0, 3) * 2 + 1)
        strGrupoMob = str.toString()
    }

    constructor(mapa: Mapa, celdaID: Short, strGrupoMob: String, tipo: TipoGrupo, condiciones: String,
                movible: Boolean, agresion: Byte, jefe: Int, idolo: String, hechizoJefe: Int) {
        this.celdaID = if (celdaID.toInt() == -1) mapa.getRandomCeldaIDLibre() else celdaID
        _tipoGrupo = tipo
        if (celdaID.toInt() == 0) {
            return
        }
        distAgresion = agresion
        fijo = true
        this.jefe = jefe
        iodoloJefe = idolo
        _hechizoJefe = hechizoJefe
        this.strGrupoMob = strGrupoMob
        var maxNivel = 0
        set_movible(movible)
        val grados: List<Byte> = ArrayList<Byte>()
        for (data in strGrupoMob.split(";")) {
            try {
                val infos: Array<String> = data.split(",")
                val idMobModelo: Int = Integer.parseInt(infos[0])
                val mobModelo: MobModelo = Mundo.getMobModelo(idMobModelo)
                var min = 0
                var max = 0
                try {
                    min = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                }
                try {
                    max = Integer.parseInt(infos[2])
                } catch (e: Exception) {
                }
                grados.clear()
                for (mob in mobModelo.getGrados().values()) {
                    if (mob.getNivel() >= min && mob.getNivel() <= max) {
                        grados.add(mob.getGrado())
                    }
                }
                var mob: MobGradoModelo
                mob = if (grados.isEmpty()) {
                    mobModelo.getRandomGrado()
                } else {
                    val grado = grados[Formulas.getRandomInt(0, grados.size() - 1)]
                    mobModelo.getGradoPorGrado(grado)
                }
                if (mob.getNivel() > maxNivel) {
                    maxNivel = mob.getNivel()
                }
                if (_almas.containsKey(idMobModelo)) {
                    val valor: Int = _almas.get(idMobModelo)
                    _almas.remove(idMobModelo)
                    _almas.put(idMobModelo, valor + 1)
                } else {
                    _almas.put(idMobModelo, 1)
                }
                if (alineacion == Constantes.ALINEACION_NULL) {
                    alineacion = mobModelo.getAlineacion()
                }
                if (distAgresion < mobModelo.getDistAgresion()) {
                    distAgresion = mobModelo.getDistAgresion()
                }
                _mobsGradoModelo.add(mob)
            } catch (e: Exception) {
            }
        }
        if (_mobsGradoModelo.isEmpty()) {
            return
        }
        iD = mapa.sigIDGrupoMob()
        condInicioPelea = condiciones
        condUnirsePelea = condiciones
        if (distAgresion.toInt() == 0) {
            distAgresion = Constantes.distAgresionPorNivel(maxNivel)
        }
        if (alineacion == Constantes.ALINEACION_BONTARIANO || alineacion == Constantes.ALINEACION_BRAKMARIANO) {
            distAgresion = 10
        } else if (alineacion == Constantes.ALINEACION_NEUTRAL) {
            distAgresion = 30
        }
        _orientacion = (Formulas.getRandomInt(0, 3) * 2 + 1)
    }

    var mapasRandom: ArrayList<Mapa>?
        get() = _mapasRandom
        set(mapas) {
            _mapasRandom = mapas
        }

    fun gethechizoJefe(): Int {
        return _hechizoJefe
    }

    fun moverGrupoMob(mapa: Mapa) {
        if (_pelea != null) {
            return
        }
        val celdaDestino: Short = Camino.celdaMoverSprite(mapa, celdaID)
        if (celdaDestino.toInt() == -1) {
            return
        }
        val pathCeldas: Duo<Integer, ArrayList<Celda>> = Camino.getPathPelea(mapa, celdaID, celdaDestino, -1, null,
                false) ?: return
        val celdas: ArrayList<Celda> = pathCeldas._segundo
        val pathStr: String = Camino.getPathComoString(mapa, celdas, celdaID, false)
        if (pathStr.isEmpty()) {
            MainServidor.redactarLogServidorln(("Fallo de desplazamiento de mob grupo: camino vacio - MapaID: "
                    + mapa.getID()) + " - CeldaID: " + celdaID)
            return
        }
        try {
            Thread.sleep(100)
        } catch (e: Exception) {
        }
        GestorSalida.ENVIAR_GA_MOVER_SPRITE_MAPA(mapa, 0, 1, iD.toString() + "",
                Encriptador.getValorHashPorNumero(_orientacion) + Encriptador.celdaIDAHash(celdaID) + pathStr)
        _orientacion = Camino.getIndexPorDireccion(pathStr.charAt(pathStr.length() - 3))
        celdaID = celdaDestino
    }

    fun tieneMobModeloID(id: Int, lvlMin: Int, lvlMax: Int): Boolean {
        for (m in _mobsGradoModelo) {
            if (m.getIDModelo() === id) {
                if (m.getNivel() >= lvlMin && m.getNivel() <= lvlMax) {
                    return true
                }
            }
        }
        return false
    }

    fun addObjetosKamasInicioServer(heroico: String) {
        val infos: Array<String> = heroico.split(Pattern.quote("|"))
        if (infos.size > 1 && !infos[1].isEmpty()) {
            for (s in infos[1].split(",")) {
                try {
                    if (s.isEmpty()) continue
                    addIDObjeto(Integer.parseInt(s))
                } catch (e: Exception) {
                }
            }
        }
        if (infos.size > 2 && !infos[2].isEmpty()) {
            var kamas: Long = 0
            try {
                kamas = Long.parseLong(infos[1])
            } catch (e: Exception) {
            }
            addKamasHeroico(kamas)
        }
    }

    fun addKamasHeroico(kamas: Long) {
        if (kamas < 1) {
            return
        }
        kamasHeroico += kamas
        Math.max(0, kamasHeroico)
    }

    fun esHeroico(): Boolean {
        return kamasHeroico > 0 || !_objetosHeroico.isEmpty()
    }

    private fun addIDObjeto(id: Int) {
        if (!_objetosHeroico.contains(id)) _objetosHeroico.add(id)
    }

    val iDsObjeto: String
        get() {
            val str = StringBuilder()
            for (i in _objetosHeroico) {
                if (str.length() > 0) {
                    str.append(",")
                }
                str.append(i)
            }
            return str.toString()
        }

    fun cantObjHeroico(): Int {
        return _objetosHeroico.size()
    }

    val objetosHeroico: ArrayList<Integer>
        get() = _objetosHeroico

    fun borrarObjetosHeroico() {
        _objetosHeroico.clear()
    }

    fun addObjAInventario(objeto: Objeto): Boolean {
        if (_objetosHeroico.contains(objeto.getID())) {
            return false
        }
        // tipo piedra de alma y mascota
        if (objeto.puedeTenerStatsIguales()) {
            for (id in _objetosHeroico) {
                val obj: Objeto = Mundo.getObjeto(id) ?: continue
                if (Constantes.esPosicionEquipamiento(obj.getPosicion())) {
                    continue
                }
                if (objeto.getID() !== obj.getID() && obj.getObjModeloID() === objeto.getObjModeloID() && obj.sonStatsIguales(objeto)) {
                    obj.setCantidad(obj.getCantidad() + objeto.getCantidad())
                    if (objeto.getID() > 0) {
                        Mundo.eliminarObjeto(objeto.getID())
                    }
                    return true
                }
            }
        }
        if (objeto.getID() === 0) {
            Mundo.addObjeto(objeto, false)
        } else {
            GestorSQL.SALVAR_OBJETO(objeto)
        }
        addIDObjeto(objeto.getID())
        return false
    }

    fun puedeTimerReaparecer(mapa: Mapa, grupoMob: GrupoMob?, i: Aparecer?): Boolean {
        if (_tipoGrupo === TipoGrupo.SOLO_UNA_PELEA) {
            return false
        }
        when (i) {
            INICIO_PELEA -> mapa.addSiguienteGrupoMob(grupoMob, true)
            FINAL_PELEA -> mapa.addUltimoGrupoMob(grupoMob, true)
        }
        return true
    }

    fun enPelea(): Boolean {
        return _pelea != null
    }

    val tipo: TipoGrupo
        get() = _tipoGrupo

    fun estaMuerto(): Boolean {
        return _muerto
    }

    fun setMuerto(muerto: Boolean) {
        _muerto = muerto
    }

    var bonusEstrellas: Int
        get() {
            bonusEstrellas = _bonusEstrellas
            return Math.max(0, _bonusEstrellas)
        }
        set(estrellas) {
            _bonusEstrellas = estrellas
            if (_bonusEstrellas < MainServidor.INICIO_BONUS_ESTRELLAS_MOBS) {
                _bonusEstrellas = MainServidor.INICIO_BONUS_ESTRELLAS_MOBS
            }
            if (_bonusEstrellas > MainServidor.MAX_BONUS_ESTRELLAS_MOBS) {
                _bonusEstrellas = if (MainServidor.PARAM_REINICIAR_ESTRELLAS_SI_LLEGA_MAX) {
                    MainServidor.INICIO_BONUS_ESTRELLAS_MOBS
                } else {
                    MainServidor.MAX_BONUS_ESTRELLAS_MOBS
                }
            }
        }

    fun realBonusEstrellas(): Int {
        return _bonusEstrellas
    }

    fun subirBonusEstrellas(cant: Int) {
        bonusEstrellas = _bonusEstrellas + cant
    }

    var orientacion: Byte
        get() = _orientacion
        set(o) {
            _orientacion = 0
        }

    // public MobGrado getMobGradoPorID(final int id) {
    // return _mobsGradoMod.get(id);
    // }
    val cantMobs: Int
        get() = _mobsGradoModelo.size()

    fun stringGM(perso: Personaje?): String {
        if (_mobsGradoModelo.isEmpty()) {
            return ""
        }
        val mobIDs = StringBuilder()
        val mobGFX = StringBuilder()
        val mobNiveles = StringBuilder()
        val colorAccesorios = StringBuilder()
        val forma = if (Formulas.getRandomBoolean()) "," else ":"
        var totalExp: Long = 0
        var NivelViejo = 0
        var NivelGrupo = 0
        var NivelesMob: Long = 0
        var maxNivelMob: Long = 0
        var coefEstrellas = 0f
        var coefNivel = 0.8f
        var coefMobLuch = 0.2f
        var coefMult = 1f
        var coefSab = 0f
        var bonusIdolo = 0
        for (mob in _mobsGradoModelo) {
            if (mobIDs.length() > 0) {
                mobIDs.append(",")
                mobGFX.append(forma)
                mobNiveles.append(",")
            }
            mobIDs.append(mob.getMobModelo().getID())
            mobGFX.append(mob.getMobModelo().getGfxID().toString() + "^" + mob.getMobModelo().getTalla())
            mobNiveles.append(mob.getNivel())
            totalExp += mob.getBaseXp()
            if (perso != null) {
                if (!perso.getIdolos().isEmpty()) {
                    bonusIdolo = perso.getBonusIdolo()
                }
                NivelesMob += mob.getNivel()
                if (maxNivelMob < mob.getNivel()) {
                    maxNivelMob = mob.getNivel()
                }
                NivelViejo = perso.getNivel()
                coefSab = (perso.getTotalStats().getTotalStatParaMostrar(Constantes.STAT_MAS_SABIDURIA, perso.getPelea(), null) + perso
                        .getTotalStats().getTotalStatParaMostrar(Constantes.STAT_MAS_PORC_EXP, perso.getPelea(), null) + 100) / 100f
                if (perso.getGrupoParty() != null) {
                    if (!perso.getGrupoParty().getLiderGrupo().getIdolos().isEmpty()) {
                        bonusIdolo = perso.getGrupoParty().getLiderGrupo().getBonusIdolo()
                    }
                    NivelGrupo = perso.getGrupoParty().getNivelGrupo()
                    coefMobLuch = Math.min(NivelViejo, Math.round(2.5f * maxNivelMob)) / NivelGrupo.toFloat()
                    if (NivelGrupo - 5 > NivelesMob) {
                        coefNivel = (NivelesMob / NivelGrupo).toFloat()
                    } else if (NivelGrupo + 10 < NivelesMob) {
                        coefNivel = ((NivelGrupo + 10) / NivelesMob).toFloat()
                    }
                    if (coefNivel > 1.2f) {
                        coefNivel = 1.2f
                    } else if (coefNivel < 0.8f) {
                        coefNivel = 0.8f
                    }
                    for (perxo in perso.getGrupoParty().getMiembros()) {
                        if (perxo == null) {
                            continue
                        }
                        if (perxo.getID() === perso.getID()) {
                            continue
                        }
                    }
                    coefMult = when (perso.getGrupoParty().getMiembros().size()) {
                        0 -> 1f
                        1 -> 1f
                        2 -> 1.1f
                        3 -> 1.5f
                        4 -> 2.3f
                        5 -> 3.1f
                        6 -> 3.6f
                        7 -> 4.2f
                        8 -> 4.7f
                        else -> 4.7f
                    }
                }
                if (coefMobLuch > 1) {
                    coefMobLuch = 1f
                } else if (coefMobLuch < 0.2f) {
                    coefMobLuch = 0.2f
                }
            }
        }
        totalExp = (totalExp * coefMobLuch * coefMult * coefNivel).toLong()
        var xp = (totalExp * coefSab).toLong()
        coefEstrellas = bonusEstrellas / 100f + bonusIdolo / 15f
        xp = (xp * (coefEstrellas + MainServidor.RATE_XP_PVM)) as Long
        for (mob in _mobsGradoModelo) {
            if (colorAccesorios.length() > 0) {
                colorAccesorios.append(";")
            }
            colorAccesorios.append(mob.getMobModelo().getColores())
            colorAccesorios.append(";")
            colorAccesorios.append(mob.getMobModelo().getStringAccesorios())
        }
        val s = StringBuilder()
        s.append(celdaID.toString() + ";" + _orientacion + ";" + bonusEstrellas + ";" + iD + ";" + mobIDs.toString()
                + ";-3;" + mobGFX.toString() + ";" + mobNiveles.toString() + ";")
        if (MainServidor.PARAM_MOSTRAR_EXP_MOBS) {
            s.append(xp)
        }
        s.append(";" + colorAccesorios.toString())
        return s.toString()
    }

    val mobs: ArrayList<MobGradoModelo>
        get() = _mobsGradoModelo
    val almasMobs: Map<Any, Any>
        get() = _almas

    fun startTiempoCondicion() {
        _timer = Timer()
        _timer.schedule(object : TimerTask() {
            fun run() {
                _timer.cancel()
                condInicioPelea = ""
                condUnirsePelea = ""
            }
        }, MainServidor.SEGUNDOS_ARENA * 1000)
    }

    // public void startTimerRespawn(final Mapa mapa) {
    // final GrupoMob g = this;
    // _timer = new Timer();
    // _timer.schedule(new TimerTask() {
    // public void run() {
    // _timer.cancel();
    // mapa.addSigGrupoMobRespawn(g);
    // }
    // }, _segundosRespawn * 1000);
    // }
    fun setPelea(pelea: Pelea?) {
        _pelea = pelea
    }

    fun is_movible(): Boolean {
        return _movible
    }

    fun set_movible(_movible: Boolean) {
        this._movible = _movible
    }

    fun is_evento(): Boolean {
        return _evento
    }

    fun set_evento(_evento: Boolean) {
        this._evento = _evento
    }
}