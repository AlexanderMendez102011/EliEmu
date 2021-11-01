package variables.mapa

import java.awt.event.ActionEvent

class Mapa {
    private var _sigIDNpc: Byte = -51
    var muteado = false
    var x: Short
        private set
    var y: Short
        private set
    private var _capabilities: Short = 0
    private var _musicID: Short = 0
    private var _ambienteID: Short = 0
    var ancho: Byte
        private set
    var alto: Byte
        private set
    var maxGrupoDeMobs: Byte = 5
    private var _minMobsPorGrupo: Byte = 1
    var maxMobsPorGrupo: Byte = 8
    var maxMercantes: Byte = 5
    private var _maxPeleas: Byte = 99
    private var _outDoor: Byte = 0
    var fecha: String
    var mapData = ""
        private set
    var _celdasPelea = ""
    private var _peleas: Map<Short, Pelea?>? = null
    var iD: Int
        private set
    private var _npcs: Map<Integer, NPC>? = null
    private var _grupoMobsTotales: Map<Integer, GrupoMob?>? = null
    private var _grupoMobsEnCola: CopyOnWriteArrayList<GrupoMob>? = null
    private var _personajes: CopyOnWriteArrayList<Personaje>? = null
    private var _accionFinPelea: Map<Integer, ArrayList<Accion>?>? = null
    private var _mercantes: Map<Integer, Personaje>? = null
    private var _mobPosibles: ArrayList<MobPosible>? = null
    private var _trabajosRealizar: ArrayList<Integer>? = null
    private var _objInteractivos: ArrayList<ObjetoInteractivo>? = null
    private val _celdas: Map<Short, Celda?> = TreeMap<Short, Celda>()
    private val _celdasEvento: Map<Short, Celda?> = TreeMap<Short, Celda>()
    private val _posPeleaRojo1: ArrayList<Short> = ArrayList<Short>()
    private val _posPeleaAzul2: ArrayList<Short> = ArrayList<Short>()
    var celdasTPs: StringBuilder = StringBuilder()
    var colorCeldasAtacante = ""
    var _subArea: SubArea? = null
    var places: String? = null
    var _personaliza: Map<Integer, String> = HashMap<Integer, String>()
    var _limpieza: Map<Integer, String> = HashMap<Integer, String>()
    private var _cercado: Cercado? = null
    private var _prisma: Prisma? = null
    private var _recaudador: Recaudador? = null
    var minNivelGrupoMob = 0
        private set
    var bgID = 0
        private set
    var maxNivelGrupoMob = 0
        private set
    val prePelea: Boolean = Boolean.TRUE

    constructor(id: Int, fecha: String, ancho: Byte, alto: Byte, posDePelea: String,
                mapData: String?, key: String, mobs: String, X: Short, Y: Short, subArea: Short,
                maxGrupoDeMobs: Byte, minMobsPorGrupo: Byte, maxMobsPorGrupo: Byte, maxMercantes: Byte, parametros: Short,
                maxPeleas: Byte, bgID: Int, musicID: Short, ambienteID: Short, outDoor: Byte, minNivelGrupoMob: Int,
                maxNivelGrupoMob: Int, personalizado: String, limpieza: String) {
        var mapData = mapData
        mapaNormal()
        iD = id
        this.fecha = fecha
        this.ancho = ancho
        this.alto = alto
        x = X
        y = Y
        if (MainServidor.MODO_DEBUG) {
            System.out.println("  --> Descifrando MapData ID " + iD + " con key " + key)
        }
        if (!key.trim().isEmpty()) {
            mapData = Encriptador.decifrarMapData(key, mapData)
        }
        for (pers in personalizado.split(";")) {
            if (pers.isEmpty()) continue
            val celdaid: Int = Integer.parseInt(pers.split(",").get(0))
            val objid: String = pers.split(",").get(1)
            val origin: String = pers.split(",").get(2)
            val objdev: Int = Integer.parseInt(pers.split(",").get(3))
            _personaliza.put(celdaid, "$objid,$origin,$objdev")
        }
        for (pers in limpieza.split(";")) {
            if (pers.isEmpty()) continue
            val celdaid: Int = Integer.parseInt(pers.split(",").get(0))
            val modif: String = pers.split(",").get(1)
            val origin: String = pers.split(",").get(2)
            _limpieza.put(celdaid, "$modif,$origin")
        }
        places = posDePelea
        this.mapData = Encriptador.decompilarMapaDataOBJ(mapData, _personaliza, _limpieza, iD, this)
        this.bgID = bgID
        _musicID = musicID
        _ambienteID = ambienteID
        _outDoor = outDoor
        _capabilities = parametros
        this.maxGrupoDeMobs = maxGrupoDeMobs
        this.maxMobsPorGrupo = maxMobsPorGrupo
        _minMobsPorGrupo = minMobsPorGrupo
        this.maxMercantes = maxMercantes
        _maxPeleas = maxPeleas
        this.minNivelGrupoMob = minNivelGrupoMob
        this.maxNivelGrupoMob = maxNivelGrupoMob
        try {
            _subArea = Mundo.getSubArea(subArea)
            _subArea.addMapa(this)
        } catch (e: Exception) {
            MainServidor.redactarLogServidorln("Error al cargar el mapa ID $id subAreaID $subArea no existe")
            System.exit(1)
            return
        }
        if (MainServidor.MODO_DEBUG) {
            System.out.println("  --> Decompilando MapData ID " + iD)
        }
        Encriptador.decompilarMapaData(this)
        _trabajosRealizar.trimToSize()
        if (MainServidor.MODO_DEBUG) {
            System.out.println("  --> MobPosibles en mapaID " + iD + " mobs " + mobs)
        }
        mobPosibles(mobs)
        if (MainServidor.MODO_DEBUG) {
            System.out.println("  --> Agregando Mobs mapaID " + iD)
        }
        if (MainServidor.PARAM_PERMITIR_MOBS) {
            agregarMobsInicioServer()
        }
        decodificarPosPelea(posDePelea)
        // corregirPosPelea();
        if (colorCeldasAtacante.isEmpty() && (esMazmorra() || esArena())) {
            colorCeldasAtacante = "red"
        }
        if (colorCeldasAtacante.isEmpty()) {
            colorCeldasAtacante = MainServidor.COLOR_CELDAS_PELEA_AGRESOR
        }
        convertCeldasPelea
        if (posDePelea === "" || !esMazmorra() || iD != 11095) {
            prepararCeldasPelea(1, 1)
        }
    }

    private fun agregarMobsInicioServer() {
        val s1: ArrayList<Integer> = Mundo.getMapaEstrellas(iD)
        val s2: ArrayList<String> = Mundo.getMapaHeroico(iD)
        if (!_mobPosibles.isEmpty()) {
            for (i in 0 until maxGrupoDeMobs) {
                try {
                    var estrellas = -1
                    var heroico = ""
                    if (s1 != null && !s1.isEmpty()) {
                        estrellas = s1.get(0)
                    }
                    if (s2 != null && !s2.isEmpty()) {
                        heroico = s2.get(0)
                    }
                    if (MainServidor.MODO_DEBUG) {
                        System.out.println("  --> Agregando grupoMob mapaID: " + iD + ", estrellas: " + estrellas + ", heroico: "
                                + heroico)
                    }
                    val grupoMob: GrupoMob = getGrupoMobInicioServer((-1.toShort()).toShort(), heroico, estrellas)
                            ?: break // neutral
                    if (s1 != null && !s1.isEmpty()) {
                        s1.remove(0)
                    }
                    if (s2 != null && !s2.isEmpty()) {
                        s2.remove(0)
                    }
                } catch (e: Exception) {
                }
            }
        }
        if (s2 != null) {
            for (s in s2) {
                try {
                    if (s.isEmpty()) {
                        continue
                    }
                    val strMob: String = s.split(Pattern.quote("|")).get(0)
                    val grupoMob = GrupoMob(this, -1.toShort(), strMob, TipoGrupo.HASTA_QUE_MUERA, "", true, 0.toByte(), 0, "", 0)
                    if (grupoMob.getMobs().isEmpty()) {
                        continue
                    }
                    grupoMob.addObjetosKamasInicioServer(s)
                    addUltimoGrupoMob(grupoMob)
                } catch (e: Exception) {
                }
            }
        }
    }

    fun esNivelGrupoMobPermitido(nivel: Int): Boolean {
        var min = 0
        var max = 0
        if (_subArea.getMinNivelGrupoMob() > 0) {
            min = _subArea.getMinNivelGrupoMob()
        }
        if (_subArea.getMaxNivelGrupoMob() > 0) {
            max = _subArea.getMaxNivelGrupoMob()
        }
        if (minNivelGrupoMob > 0) {
            min = minNivelGrupoMob
        }
        if (maxNivelGrupoMob > 0) {
            max = maxNivelGrupoMob
        }
        return if (min == 0 && max == 0) {
            true
        } else nivel >= min && nivel <= max
    }

    constructor(mapa: Mapa, posDePelea: String) {
        iD = mapa.iD
        fecha = mapa.fecha
        ancho = mapa.ancho
        alto = mapa.alto
        x = mapa.x
        y = mapa.y
        _celdas.clear()
        _celdasEvento.clear()
        for (newCelda in mapa._celdas.values()) {
            val celda = Celda(this, newCelda.getID(), newCelda.getActivo(), newCelda.getMovimiento(), newCelda
                    .getLevel(), newCelda.getSlope(), newCelda.lineaDeVista(), -1)
            _celdas.put(newCelda.getID(), celda)
            celda.celdaPelea()
        }
        for (newCelda in mapa._celdasEvento.values()) {
            val celda = Celda(this, newCelda.getID(), newCelda.getActivo(), newCelda.getMovimiento(), newCelda
                    .getLevel(), newCelda.getSlope(), newCelda.lineaDeVista(), -1)
            _celdasEvento.put(newCelda.getID(), celda)
            celda.celdaPelea()
        }
        decodificarPosPelea(posDePelea)
        convertCeldasPelea
    }

    fun mapaNormal() {
        _peleas = HashMap<Short, Pelea>()
        _npcs = HashMap<Integer, NPC>()
        _grupoMobsTotales = HashMap<Integer, GrupoMob>()
        // _grupoMobsFix = new HashMap<Integer, GrupoMob>();
        _grupoMobsEnCola = CopyOnWriteArrayList<GrupoMob>()
        _personajes = CopyOnWriteArrayList<Personaje>()
        _accionFinPelea = HashMap<Integer, ArrayList<Accion>>()
        _mercantes = TreeMap<Integer, Personaje>()
        _mobPosibles = ArrayList<MobPosible>()
        _trabajosRealizar = ArrayList<Integer>()
        _objInteractivos = ArrayList<ObjetoInteractivo>()
    }

    fun getPuedoEntrar(ip: String?, perso2: Personaje?): Boolean {
        for (pelea in _peleas!!.values()) {
            for (lucha in pelea.luchadoresDeEquipo(1)) {
                if (lucha.getPersonaje() == null) continue
                if (lucha.getPersonaje().esMultiman()) continue
                if (!lucha.getPersonaje().equals(perso2)) if (lucha.getPersonaje().getCuenta().getActualIP().equals(ip)) return false
            }
            for (lucha in pelea.luchadoresDeEquipo(2)) {
                if (lucha.getPersonaje() == null) continue
                if (!lucha.getPersonaje().equals(perso2)) if (lucha.getPersonaje().getCuenta().getActualIP().equals(ip)) return false
            }
        }
        for (perso in _personajes) {
            if (!perso.equals(perso2)) if (perso.getCuenta().getActualIP().equals(ip)) return false
        }
        return true
    }

    fun getPuedoEntrarCant(ip: String?, perso2: Personaje?): Int {
        var cant = 0
        for (pelea in _peleas!!.values()) {
            for (lucha in pelea.luchadoresDeEquipo(1)) {
                if (lucha.getPersonaje() == null) continue
                if (lucha.getPersonaje().esMultiman()) continue
                if (!lucha.getPersonaje().equals(perso2)) if (lucha.getPersonaje().getCuenta().getActualIP().equals(ip)) cant = cant + 1
            }
            for (lucha in pelea.luchadoresDeEquipo(2)) {
                if (lucha.getPersonaje() == null) continue
                if (!lucha.getPersonaje().equals(perso2)) if (lucha.getPersonaje().getCuenta().getActualIP().equals(ip)) cant = cant + 1
            }
        }
        for (perso in _personajes) {
            if (!perso.equals(perso2)) if (perso.getCuenta().getActualIP().equals(ip)) cant = cant + 1
        }
        return cant
    }

    fun copiarMapa(posPelea: String): Mapa {
        return Mapa(this, posPelea)
    }

    val objetosInteractivos: ArrayList<ObjetoInteractivo>?
        get() = _objInteractivos

    fun setStrCeldasPelea(posDePelea: String) {
        _celdasPelea = posDePelea
        colorCeldasAtacante = ""
        decodificarPosPelea(_celdasPelea)
    }

    fun decodificarPosPelea(posDePelea: String) {
        try {
            _posPeleaRojo1.clear()
            _posPeleaAzul2.clear()
            // _colorCeldaAtacante = "";
            val str: Array<String> = posDePelea.split(Pattern.quote("|"))
            if (str.size > 0 && !str[0].isEmpty()) {
                Encriptador.analizarCeldasDeInicio(str[0], _posPeleaRojo1)
            }
            if (str.size > 1 && !str[1].isEmpty()) {
                Encriptador.analizarCeldasDeInicio(str[1], _posPeleaAzul2)
            }
            if (str.size > 2 && !str[2].isEmpty()) {
                colorCeldasAtacante = str[2]
            }
        } catch (e: Exception) {
        }
    }

    fun addCeldaPelea(equipo: Int, celdaID: Short) {
        _posPeleaRojo1.remove(celdaID as Object)
        _posPeleaAzul2.remove(celdaID as Object)
        if (equipo == 1) {
            _posPeleaRojo1.add(celdaID)
        } else if (equipo == 2) {
            _posPeleaAzul2.add(celdaID)
        }
    }

    fun borrarCeldasPelea(celdaID: Short) {
        _posPeleaRojo1.remove(celdaID as Object)
        _posPeleaAzul2.remove(celdaID as Object)
    }

    val convertCeldasPelea: String
        get() {
            var vacio = true
            val str = StringBuilder()
            for (s in _posPeleaRojo1) {
                vacio = false
                str.append(Encriptador.celdaIDAHash(s))
            }
            str.append("|")
            for (s in _posPeleaAzul2) {
                vacio = false
                str.append(Encriptador.celdaIDAHash(s))
            }
            _celdasPelea = str.toString()
            if (!vacio && !colorCeldasAtacante.isEmpty()) {
                vacio = false
                str.append("|")
                str.append(colorCeldasAtacante)
            }
            return str.toString()
        }

    private fun prepararCeldasPelea(cant1: Int, cant2: Int): Boolean {
        var cant1 = cant1
        cant1 = if (_posPeleaAzul2.isEmpty() && _posPeleaRojo1.isEmpty()) 8 else cant1
        val pos = aptoParaPelea(cant1, cant2)
        when (pos) {
            0 -> return true
            1 -> getPosicionesAleatorias(cant1, 0)
            2 -> getPosicionesAleatorias(0, cant2)
            -1 -> getPosicionesAleatorias(10, 10)
        }
        return aptoParaPelea(cant1, cant2) == 0
    }

    private fun aptoParaPelea(cant1: Int, cant2: Int): Int {
        when (colorCeldasAtacante) {
            "red" -> {
                if (_posPeleaRojo1.size() < cant1) {
                    return if (_posPeleaAzul2.size() < cant2) {
                        -1
                    } else {
                        1
                    }
                }
                if (_posPeleaAzul2.size() < cant2) {
                    return 2
                }
            }
            "blue" -> {
                if (_posPeleaAzul2.size() < cant1) {
                    return if (_posPeleaRojo1.size() < cant2) {
                        -1
                    } else {
                        2
                    }
                }
                if (_posPeleaRojo1.size() < cant2) {
                    return 1
                }
            }
            else -> {
                if (_posPeleaAzul2.size() < cant1 || _posPeleaRojo1.size() < cant1) {
                    return -1
                }
                if (_posPeleaRojo1.size() < cant2 || _posPeleaAzul2.size() < cant2) {
                    return -1
                }
            }
        }
        return 0
    }

    private fun getPosicionesAleatorias(cant1: Int, cant2: Int) {
        val celdaLibres: ArrayList<Short> = ArrayList<Short>()
        for (entry in _celdas.entrySet()) {
            if (!entry.getValue().esCaminable(true)) {
                continue
            }
            celdaLibres.add(entry.getKey())
        }
        if (celdaLibres.isEmpty() || celdaLibres.size() < cant1 + cant2) {
            return
        }
        var temp: Short = -1
        if (cant1 >= 1) {
            _posPeleaRojo1.clear()
            while (_posPeleaRojo1.size() < cant1 && !celdaLibres.isEmpty()) {
                val rand: Int = Formulas.getRandomInt(0, celdaLibres.size() - 1)
                val t: Short = celdaLibres.get(rand)
                if (temp.toInt() == -1) {
                    _posPeleaRojo1.add(t)
                    temp = t
                } else {
                    val path: Duo<Integer, ArrayList<Celda>> = Camino.getPathPelea(this, temp, t, -1, null, true)
                    if (path != null && !path._segundo.isEmpty()) {
                        if (path._segundo.get(path._segundo.size() - 1).getID() === t) {
                            _posPeleaRojo1.add(t)
                        }
                    }
                }
                celdaLibres.remove(rand)
            }
        }
        if (cant2 >= 1) {
            _posPeleaAzul2.clear()
            while (_posPeleaAzul2.size() < cant2 && !celdaLibres.isEmpty()) {
                val rand: Int = Formulas.getRandomInt(0, celdaLibres.size() - 1)
                val t: Short = celdaLibres.get(rand)
                if (temp.toInt() == -1) {
                    _posPeleaAzul2.add(t)
                    temp = t
                } else {
                    val path: Duo<Integer, ArrayList<Celda>> = Camino.getPathPelea(this, temp, t, -1, null, true)
                    if (path != null && !path._segundo.isEmpty()) {
                        if (path._segundo.get(path._segundo.size() - 1).getID() === t) {
                            _posPeleaAzul2.add(t)
                        }
                    }
                }
                celdaLibres.remove(rand)
            }
        }
        convertCeldasPelea
    }

    fun panelPosiciones(perso: Personaje?, mostrar: Boolean) {
        val str = StringBuilder()
        val signo = if (mostrar) "+" else "-"
        for (s in _posPeleaRojo1) {
            str.append("|$signo$s;0;4")
        }
        for (s in _posPeleaAzul2) {
            str.append("|$signo$s;0;11")
        }
        if (_cercado != null) {
            for (s in _cercado.getCeldasObj()) {
                str.append("|$signo$s;0;5")
            }
        }
        if (str.length() === 0) {
            return
        }
        GestorSalida.enviar(perso, "GDZ" + str.toString(), false)
    }

    val musicID: Int
        get() = _musicID.toInt()
    val ambienteID: Int
        get() = _ambienteID.toInt()
    val outDoor: Int
        get() = _outDoor.toInt()
    val capabilities: Int
        get() = _capabilities.toInt()

    fun strCeldasPelea(): String {
        return _celdasPelea
    }

    val posTeamRojo1: ArrayList<Short>
        get() = _posPeleaRojo1
    val posTeamAzul2: ArrayList<Short>
        get() = _posPeleaAzul2

    fun setMaxPeleas(max: Byte) {
        _maxPeleas = max
    }

    val maxNumeroPeleas: Int
        get() = _maxPeleas.toInt()

    private fun mobPosibles(mobs: String) {
        if (_mobPosibles == null) {
            return
        }
        _mobPosibles.clear()
        val ids: ArrayList<Integer> = ArrayList<Integer>()
        for (str in mobs.split(Pattern.quote("|"))) {
            try {
                var mobID = 0
                val split: Array<String> = str.split(",")
                try {
                    mobID = Integer.parseInt(split[0])
                } catch (e: Exception) {
                }
                mobID = Constantes.getMobSinHalloween(mobID)
                if (ids.contains(mobID)) {
                    continue
                }
                val mobModelo: MobModelo = Mundo.getMobModelo(mobID)
                if (mobModelo == null || mobModelo.getTipoMob() === Constantes.MOB_TIPO_LOS_ARCHIMONSTRUOS) {
                    continue
                }
                ids.add(mobID)
                if (!mobModelo.puedeSubArea(subArea.getID())) {
                    continue
                }
                var minLvl = 0
                var maxLvl = 0
                var cantidad = 0
                var probabilidad: Int = mobModelo.getProbabilidadAparecer()
                try {
                    minLvl = Integer.parseInt(split[1])
                    maxLvl = Integer.parseInt(split[2])
                    cantidad = Integer.parseInt(split[3])
                    probabilidad = Integer.parseInt(split[4])
                } catch (e: Exception) {
                }
                val mobP = MobPosible(cantidad, probabilidad)
                for (mob in Mundo.getMobModelo(mobID).getGrados().values()) {
                    if (minLvl > 0 && mob.getNivel() < minLvl) {
                        continue
                    }
                    if (maxLvl > 0 && mob.getNivel() > maxLvl) {
                        continue
                    }
                    mobP.addMobPosible(mob)
                    addMobPosibles(mobP)
                }
            } catch (e: Exception) {
            }
        }
        _mobPosibles.trimToSize()
    }

    fun setKeyMapData(fecha: String, key: String, mapData: String) {
        var mapData = mapData
        this.fecha = fecha
        if (!key.trim().isEmpty()) {
            mapData = Encriptador.decifrarMapData(key, mapData)
        }
        this.mapData = mapData
        actualizarCasillas()
        GestorSQL.CARGAR_TRIGGERS_POR_MAPA(iD)
    }

    // public void setKeyMap(final String key) {
    // _key = key;
    // }
    val key: String
        get() = ""
    val trabajos: ArrayList<Integer>?
        get() = _trabajosRealizar

    fun setMinMobsPorGrupo(id: Byte) {
        _minMobsPorGrupo = id
    }

    private fun addMobPosibles(mob: MobPosible) {
        if (_mobPosibles == null) {
            return
        }
        if (_mobPosibles.contains(mob)) {
            return
        }
        _mobPosibles.add(mob)
    }

    fun agregarEspadaPelea(perso: Personaje?) {
        for (pelea in _peleas!!.values()) {
            pelea.infoEspadaPelea(perso)
        }
    }

    fun insertarMobs(mobs: String) {
        if (_mobPosibles == null) {
            return
        }
        mobPosibles(mobs)
        if (_mobPosibles.isEmpty()) {
            return
        }
        for (i in 0 until maxGrupoDeMobs) {
            getGrupoMobInicioServer((-1.toShort()).toShort(), "", MainServidor.INICIO_BONUS_ESTRELLAS_MOBS)
        }
    }

    // public void addCeldaObjInteractivo(final Celda celda) {
    // _celdasObjInterac.add(celda);
    // _celdasObjInterac.trimToSize();
    // }
    // public ArrayList<Celda> getCeldasObjInter() {
    // return _celdasObjInterac;
    // }
    fun setParametros(d: Short) {
        _capabilities = d
        _capabilities = capabilitiesCompilado
        if (colorCeldasAtacante.isEmpty() && (esMazmorra() || esArena())) {
            colorCeldasAtacante = "red"
        }
    }

    fun mapaNoAgresion(): Boolean {
        return _capabilities and 1 == 1
    }

    fun esArena(): Boolean {
        return _capabilities and 2 == 2
    }

    fun esMazmorra(): Boolean {
        return _capabilities and 4 == 4
    }

    fun mapaNoDesafio(): Boolean {
        return _capabilities and 8 == 8
    }

    fun mapaNoRecaudador(): Boolean {
        return _capabilities and 16 == 16
    }

    fun mapaNoMercante(): Boolean {
        return _capabilities and 32 == 32
    }

    fun mapaAbonado(): Boolean {
        return _capabilities and 64 == 64
    }

    fun mapaNoPrisma(): Boolean {
        return if (esMazmorra() || esArena() || Mundo.getCasaDentroPorMapa(iD) != null || !trabajos.isEmpty()) {
            true
        } else _capabilities and 128 == 128
    }

    fun mapaNoPuedeSalvarTeleport(): Boolean {
        return _capabilities and 256 == 256
    }

    fun mapaNoPuedeTeleportarse(): Boolean {
        return _capabilities and 512 == 512
    }

    val capabilitiesCompilado: Short
        get() {
            var parametros: Short = 0
            if (mapaNoAgresion()) {
                (parametros += 1).toShort()
            }
            if (esArena()) {
                (parametros += 2).toShort()
            }
            if (esMazmorra()) {
                (parametros += 4).toShort()
            }
            if (mapaNoDesafio()) {
                (parametros += 8).toShort()
            }
            if (mapaNoRecaudador()) {
                (parametros += 16).toShort()
            }
            if (mapaNoMercante()) {
                (parametros += 32).toShort()
            }
            if (mapaAbonado()) {
                (parametros += 64).toShort()
            }
            if (mapaNoPrisma()) {
                (parametros += 128).toShort()
            }
            return parametros
        }

    fun addAccionFinPelea(tipoPelea: Int, accion: Accion) {
        delAccionFinPelea(tipoPelea, accion.getID(), accion.getCondicion())
        if (_accionFinPelea!![tipoPelea] == null) {
            _accionFinPelea.put(tipoPelea, ArrayList<Accion>())
        }
        _accionFinPelea!![tipoPelea].add(accion)
    }

    private fun delAccionFinPelea(tipoPelea: Int, tipoAccion: Int, condicion: String) {
        if (_accionFinPelea!![tipoPelea] == null) {
            return
        }
        val copy: ArrayList<Accion> = ArrayList<Accion>()
        copy.addAll(_accionFinPelea!![tipoPelea])
        for (acc in copy) {
            if (acc.getID() === tipoAccion && acc.getCondicion().equals(condicion)) {
                _accionFinPelea!![tipoPelea].remove(acc)
            }
        }
    }

    fun borrarAccionesPelea() {
        _accionFinPelea.clear()
    }

    fun aplicarAccionFinPelea(tipo: Int, ganadores: Collection<Luchador>,
                              acciones: ArrayList<Accion?>?) {
        val acc: ArrayList<Accion> = ArrayList()
        if (acciones != null) {
            acc.addAll(acciones)
        }
        if (_accionFinPelea!![tipo] != null) {
            acc.addAll(_accionFinPelea!![tipo])
        }
        for (accion in acc) {
            for (ganador in ganadores) {
                if (ganador.estaRetirado()) {
                    continue
                }
                val perso: Personaje = ganador.getPersonaje()
                if (perso != null) {
                    if (!Condiciones.validaCondiciones(perso, accion.getCondicion(), null)) {
                        GestorSalida.ENVIAR_Im_INFORMACION(perso, "119|45")
                        continue
                    }
                    accion.realizarAccion(perso, null, -1, -1.toShort())
                }
            }
        }
    }

    var cercado: Cercado?
        get() = _cercado
        set(cercado) {
            _cercado = cercado
        }
    val subArea: SubArea?
        get() = _subArea
    var recaudador: Recaudador?
        get() = _recaudador
        set(recaudador) {
            _recaudador = recaudador
        }
    var prisma: Prisma?
        get() = _prisma
        set(prisma) {
            _prisma = prisma
        }

    fun actualizarCasillas() {
        if (!mapData.isEmpty()) {
            Encriptador.decompilarMapaData(this)
        }
    }

    // public void setMapData(final String mapdata) {
    // _mapData = mapdata;
    // }
    fun panelTriggers(perso: Personaje?, mostrar: Boolean) {
        val str = StringBuilder()
        for (c in _celdas.values()) {
            if (c.getAcciones() == null) {
                return
            }
            if (c.getAcciones().containsKey(0)) {
                str.append("|" + (if (mostrar) "+" else "-") + c.getID() + ";0;11")
            }
        }
        if (str.length() === 0) {
            return
        }
        GestorSalida.enviar(perso, "GDZ" + str.toString(), false)
    }

    val celdas: Map<Short, Any?>
        get() = _celdas
    val celdasEvento: Map<Short, Any?>
        get() = _celdasEvento

    fun getCelda(id: Short): Celda? {
        return _celdas[Short.valueOf(id)]
    }

    fun getCeldaEvento(id: Short): Celda? {
        return if (_celdasEvento[id] != null) _celdasEvento[id] else _celdasEvento[randomCeldaIDLibreEvento]
    }

    val nPCs: Map<Any, Any>?
        get() = _npcs

    private fun sigIDNPC(): Int {
        return if (_sigIDNpc <= -100) {
            -51
        } else _sigIDNpc--.toInt()
    }

    fun addNPC(npcModelo: NPCModelo?, celdaID: Short, dir: Byte, condicion: String?): NPC {
        val npc = NPC(npcModelo, sigIDNPC(), celdaID, dir, condicion)
        _npcs.put(npc.getID(), npc)
        return npc
    }

    fun borrarNPC(id: Int) {
        _npcs.remove(id)
    }

    fun getNPC(id: Int): NPC? {
        return _npcs!![id]
    }

    val arrayPersonajes: CopyOnWriteArrayList<Personaje>?
        get() = _personajes

    fun addPersonaje(perso: Personaje?, muestra: Boolean) {
        if (_personajes == null) {
            return
        }
        if (!_personajes.contains(perso)) {
            _personajes.add(perso)
        }
        if (muestra) GestorSalida.ENVIAR_GM_AGREGAR_PJ_A_TODOS(this, perso)
    }

    fun removerPersonaje(perso: Personaje?) {
        if (_personajes == null) {
            return
        }
        _personajes.remove(perso)
    }

    fun expulsarMercanterPorCelda(celda: Short) {
        for (perso in _mercantes!!.values()) {
            if (perso.getCelda().getID() === celda) {
                removerMercante(perso.getID())
                perso.setMercante(false)
                GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(perso.getMapa(), perso.getID())
                return
            }
        }
    }

    fun removerMercante(id: Int): Boolean {
        return _mercantes.remove(id) != null
    }

    fun mercantesEnCelda(celda: Short): Int {
        var i = 0
        for (perso in _mercantes!!.values()) {
            if (perso.getCelda().getID() === celda) {
                i++
            }
        }
        return i
    }

    fun addMercante(perso: Personaje): Boolean {
        if (_mercantes!!.size() >= maxMercantes) {
            return false
        }
        _mercantes.put(perso.getID(), perso)
        return true
    }

    fun cantMercantes(): Int {
        return _mercantes!!.size()
    }

    fun cantPersonajes(): Int {
        return if (_personajes == null) 0 else _personajes.size()
    }

    fun cantNpcs(): Int {
        return _npcs!!.size()
    }

    fun cantMobs(): Int {
        return _grupoMobsTotales!!.size()
    }

    fun puedeAgregarOtraPelea(): Boolean {
        return _peleas!!.size() < _maxPeleas
    }

    val gMPrisma: String
        get() = if (_prisma == null) {
            ""
        } else "GM|+" + _prisma.stringGM()
    val gMRecaudador: String
        get() = if (_recaudador == null) {
            ""
        } else "GM|+" + _recaudador.stringGM()

    fun getGMsPersonajes(perso: Personaje): String {
        val str = StringBuilder("GM")
        try {
            val i: Boolean = !perso.esIndetectable()
            for (p in arrayPersonajes) {
                if (i && p.esIndetectable()) {
                    continue
                }
                if (removePersonajeBug(p)) {
                    continue
                }
                if (p.getPelea() == null) {
                    str.append("|+" + p.stringGM())
                }
            }
        } catch (e: Exception) {
            return getGMsPersonajes(perso)
        }
        return if (str.length() < 3) {
            ""
        } else str.toString()
    }

    private fun removePersonajeBug(perso: Personaje): Boolean {
        if (!perso.enLinea()) {
            try {
                perso.setCelda(null)
            } catch (e: Exception) {
            }
            removerPersonaje(perso)
            return true
        }
        return false
    }

    fun getGMsLuchadores(luch: Luchador): String {
        val str = StringBuilder("GM")
        str.append("|+").append(luch.stringGM(luch.getID()))
        return str.toString()
    }

    fun getGMsLuchadores(idMirador: Int): String {
        val str = StringBuilder("GM")
        for (celda in _celdas.values()) {
            try {
                if (celda.getLuchadores() == null) return ""
                for (luchador in celda.getLuchadores()) {
                    str.append("|+" + luchador.stringGM(idMirador))
                }
            } catch (Exception: Exception) {
            }
        }
        return if (str.length() < 3) {
            ""
        } else str.toString()
    }

    fun getGMsGrupoMobs(perso: Personaje?): String {
        if (_grupoMobsTotales!!.isEmpty()) {
            return ""
        }
        val str = StringBuilder("GM")
        var GM: String
        for (grupoMob in _grupoMobsTotales!!.values()) {
            try {
                GM = grupoMob.stringGM(perso)
                if (GM.isEmpty()) {
                    continue
                }
                str.append("|+$GM")
            } catch (Exception: Exception) {
            }
        }
        return str.toString()
    }

    fun getGMsNPCs(perso: Personaje?): String {
        if (_npcs!!.isEmpty()) {
            return ""
        }
        val str = StringBuilder("GM")
        for (npc in _npcs!!.values()) {
            try {
                if (Condiciones.validaCondiciones(perso, npc.getCondiciones(), null)) {
                    str.append("|+" + npc.strinGM(perso))
                }
            } catch (Exception: Exception) {
            }
        }
        return str.toString()
    }

    val gMsMercantes: String
        get() {
            if (_mercantes!!.isEmpty()) {
                return ""
            }
            val str = StringBuilder("GM")
            for (perso in _mercantes!!.values()) {
                try {
                    str.append("|+" + perso.stringGMmercante())
                } catch (Exception: Exception) {
                }
            }
            return str.toString()
        }

    fun getGMsMonturas(perso: Personaje): String {
        if (_cercado == null || _cercado.getCriando().isEmpty()) {
            return ""
        }
        val str = StringBuilder("GM")
        val esPublico: Boolean = _cercado.esPublico()
        for (montura in _cercado.getCriando().values()) {
            if (esPublico && montura.getDueñoID() !== perso.getID() && !MainServidor.SON_DE_LUCIANO) {
                continue
            }
            str.append("|+" + montura.stringGM())
        }
        return str.toString()
    }

    val objetosCria: String
        get() {
            if (_cercado == null || _cercado.getObjetosCrianza().isEmpty()) {
                return ""
            }
            val str = StringBuilder()
            for (entry in _cercado.getObjetosCrianza().entrySet()) {
                if (str.length() > 0) {
                    str.append("|")
                }
                if (_cercado.getDueñoID() === -1) {
                    str.append(entry.getKey().toString() + ";" + Constantes.getObjCriaPorMapa(iD) + ";1;1000;1000")
                } else {
                    str.append(entry.getKey().toString() + ";" + entry.getValue().getObjModeloID() + ";1;" + entry.getValue().getDurabilidad()
                            + ";" + entry.getValue().getDurabilidadMax())
                }
            }
            return "GDO+" + str.toString()
        }
    val objetosInteracGDF: String
        get() {
            val str = StringBuilder("GDC")
            val str2 = StringBuilder("GDF")
            for (celda in _celdas.values()) {
                if (celda.getObjetoInteractivo() != null) {
                    str2.append("|" + celda.getID().toString() + ";" + celda.getObjetoInteractivo().getInfoPacket())
                } else if (celda.getEstado() !== 1) {
                    str2.append("|" + celda.getID().toString() + ";" + celda.getEstado())
                    str.append(celda.getID().toString() + ";aaVaaaaaaa800|")
                }
            }
            if (str.length() === 3 && str2.length() === 3) {
                return ""
            }
            if (str.length() === 3) {
                return str2.toString()
            }
            return if (str2.length() === 3) {
                str.toString()
            } else str.toString() + 0x00.toChar() + str2.toString()
        }
    val randomCeldaIDLibrePelea: Celda?
        get() {
            val celdaLibre: ArrayList<Short> = ArrayList<Short>()
            for (celda in _celdas.values()) {
                if (celda.esCaminable(true) && celda.getPrimerPersonaje() == null) {
                    celdaLibre.add(Short.valueOf(celda.getID()))
                }
            }
            if (celdaLibre.isEmpty()) {
                System.out.println("No hay celda libre en el mapa " + iD)
                return null
            }
            val rand: Int = Formulas.getRandomValor(0, celdaLibre.size() - 1)
            return getCelda(celdaLibre.get(rand).shortValue())
        }
    val randomCeldaIDLibre: Short
        get() {
            val celdaLibre: ArrayList<Short> = ArrayList<Short>()
            for (entry in _celdas.entrySet()) {
                val celda: Celda = entry.getValue()
                if (celda.esCaminable(true) && celda.getPrimerPersonaje() == null) {
                    var ok = true
                    for (grupoMob in _grupoMobsTotales.entrySet()) {
                        if (grupoMob.getValue().getCeldaID() === entry.getValue().getID()) ok = false
                    }
                    if (!ok) continue
                    if (ok) {
                        ok = true
                        for (npc in _npcs.entrySet()) {
                            if (npc.getValue().getCeldaID() === celda.getID()) ok = false
                        }
                        if (ok) {
                            celdaLibre.add(Short.valueOf(celda.getID()))
                        }
                    }
                }
            }
            if (celdaLibre.isEmpty()) {
                System.out.println("No hay celda libre en el mapa " + iD)
                return -1
            }
            if (celdaLibre.size() <= 0) return 0
            val rand: Int = Formulas.getRandomValor(0, celdaLibre.size() - 1)
            return celdaLibre.get(rand).shortValue()
        }
    val randomCeldaIDLibreEvento: Short
        get() {
            val celdaLibre: ArrayList<Short> = ArrayList<Short>()
            for (celda in _celdasEvento.values()) {
                if (!celda.esCaminable(true) || celda.getPrimerPersonaje() != null) {
                    continue
                }
                if (_cercado != null && _cercado.getCeldasObj().contains(celda.getID())) continue
                celdaLibre.add(celda.getID())
            }
            try {
                for (grupoMob in _grupoMobsTotales!!.values()) {
                    celdaLibre.remove(grupoMob.getCeldaID() as Object)
                }
                for (npc in _npcs!!.values()) {
                    celdaLibre.remove(npc.getCeldaID() as Object)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return if (celdaLibre.isEmpty()) {
                -1
            } else celdaLibre.get(Formulas.getRandomInt(0, celdaLibre.size() - 1))
        }

    fun refrescarGrupoMobs() {
        if (MainServidor.MODO_HEROICO || MainServidor.MAPAS_MODO_HEROICO.contains(iD)) {
            return
        }
        val idsBorrar: ArrayList<Integer> = ArrayList()
        for (gm in _grupoMobsTotales!!.values()) {
            if (gm.getTipo() !== TipoGrupo.NORMAL) {
                continue
            }
            val id: Int = gm.getID()
            idsBorrar.add(id)
            GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(this, id)
        }
        for (id in idsBorrar) {
            _grupoMobsTotales.remove(id)
        }
        if (_mobPosibles == null) {
            return
        }
        if (_mobPosibles.isEmpty()) {
            return
        }
        for (i in 1..maxGrupoDeMobs) {
            getGrupoMobInicioServer((-1.toShort()).toShort(), "", MainServidor.INICIO_BONUS_ESTRELLAS_MOBS)
        }
    }

    fun moverGrupoMobs(mover: Int) {
        // String str = "";
        try {
            for (grupoMob in _grupoMobsTotales!!.values()) {
                if (grupoMob.enPelea()) {
                    continue
                }
                /*
				if ((!MainServidor.PARAM_MOVER_MOBS_FIJOS && grupoMob.getTipo() == TipoGrupo.FIJO)) {
					continue;
				}
				*/if (!grupoMob.is_movible()) continue
                if (Formulas.getRandomBoolean()) {
                    grupoMob.moverGrupoMob(this)
                }
            }
        } catch (e: Exception) {
        }
        // return str;
    }

    fun subirEstrellasOI(cant: Int) {
        for (oi in _objInteractivos) {
            oi.subirBonusEstrellas(cant * 20)
        }
    }

    fun subirEstrellasMobs(cant: Int) {
        for (grupoMob in _grupoMobsTotales!!.values()) {
            grupoMob.subirBonusEstrellas(cant * 20) // * (mapaMazmorra() ? 45 ):
        }
    }

    fun jugadorLLegaACelda(perso: Personaje, celdaID: Short) {
        if (_celdas[celdaID] == null) return
        val obj: Objeto = _celdas[celdaID].getObjetoTirado()
        if (obj != null) {
            _celdas[celdaID].setObjetoTirado(null)
            perso.addObjIdentAInventario(obj, true)
            GestorSalida.ENVIAR_GDO_OBJETO_TIRAR_SUELO(this, '-', _celdas[celdaID].getID(), 0, false, "")
            GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso)
        }
        if (!perso.esMaitre) _celdas[celdaID].aplicarAccion(perso)
        if (perso.getMapa().getID() !== iD || perso.esFantasma() || perso.esTumba()) return
        if (System.currentTimeMillis() - perso.ultimaPelea < 1000) return
        for (grupo in _grupoMobsTotales!!.values()) {
            if (Camino.distanciaDosCeldas(this, celdaID, grupo.getCeldaID() as Short) <= grupo.getDistAgresion()) {
                if (grupo.getAlineacion() === -1 || (perso.getAlineacion() === 1 || perso.getAlineacion() === 2) && perso.getAlineacion() !== grupo.getAlineacion()) {
                    if (!Constantes.puedeIniciarPelea(perso, grupo, this, _celdas[celdaID])) {
                        // System.out.println("no cumple las condiciones para iniciar pelea");
                        continue
                    }
                    if (perso.tieneObjModeloEquipado(101610)) {
                        if (!perso.getMapa().esMazmorra()) {
                            if (!perso.tieneMision(561) || perso.getEstadoMision(561) === 1) {
                                val Ayudante: Objeto = perso.getObjPosicion(MainServidor.OBJETOS_POS_AYUDANTE)
                                perso.borrarOEliminarConOR(Ayudante.getID(), true)
                            }
                        }
                    } else if (perso.tieneObjModeloEquipado(101721)) {
                        if (!perso.getMapa().esMazmorra()) {
                            if (!perso.tieneMision(563) || perso.getEstadoMision(563) === 1) {
                                val Ayudante: Objeto = perso.getObjPosicion(MainServidor.OBJETOS_POS_AYUDANTE)
                                perso.borrarOEliminarConOR(Ayudante.getID(), true)
                            }
                        }
                    }
                    if (perso.esMaitre) return
                }
                if (!perso.esMaitre) {
                    val pelea: Pelea? = iniciarPelea(perso, null, perso.getCelda().getID(), (-1.toShort()).toShort(), Constantes.PELEA_TIPO_PVM,
                            grupo, false)
                    if (perso.liderMaitre) {
                        val timer = Timer(500, object : ActionListener() {
                            @Override
                            fun actionPerformed(e: ActionEvent) {
                                (e.getSource() as Timer).stop()
                                for (pjx in perso.getGrupoParty().getPersos()) {
                                    if (pjx === perso) continue
                                    if (!pjx.enLinea()) continue
                                    if (pjx.getPelea() != null) continue
                                    if (perso.getPelea() == null) continue
                                    val packet = "GA903" + pelea.getID().toString() + ";" + perso.getID()
                                    pjx.getCuenta().getSocket().analizar_Packets(packet, true)
                                }
                            }
                        })
                        timer.start()
                    }
                }
                return
            }
        }
    }

    /*public synchronized boolean jugadorLLegaACelda(final Personaje perso, short celdaIDDestino, short celdaIDPacket,
	boolean ok) {
		byte bug = 0;
		try {
			Celda celdaDestino = getCelda(celdaIDDestino);
			if (celdaDestino == null) {
				GestorSalida.ENVIAR_BN_NADA(perso, " FINALIZAR DESPLAZAMIENTO CELDA NULA");
				return false;
			}
			bug = 1;
			if (perso.getMapa().getID() != _id || !perso.estaDisponible(false, true)) {
				return false;
			}
			bug = 2;
			perso.setCelda(celdaDestino);
			Objeto objTirado = celdaDestino.getObjetoTirado();
			bug = 3;
			if (objTirado != null) {
				celdaDestino.setObjetoTirado(null);
				perso.addObjIdentAInventario(objTirado, true);
				GestorSalida.ENVIAR_GDO_OBJETO_TIRAR_SUELO(this, '-', celdaDestino.getID(), 0, false, "");
				GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso);
			}
			bug = 4;
			boolean activoInt = false;
			if (celdaIDPacket != celdaIDDestino) {
				// cuando no se puede llegar a cierta celda por un stop:
				final Celda celdaObjetivo = getCelda(celdaIDPacket);
				if (celdaObjetivo != null) {
					ObjetoInteractivo objInteractivo = celdaObjetivo.getObjetoInteractivo();
					if (objInteractivo != null && objInteractivo.getObjIntModelo() == null) {
						activoInt = true;
						perso.realizarOtroInteractivo(celdaObjetivo, objInteractivo);
					}
				}
			}
			bug = 5;
			if (!activoInt) {
				if (ok) {
					if (!perso.estaDisponible(true, true)) {
						bug = 6;
					} else {
						bug = 7;
						for (Personaje p : getArrayPersonajes()) {
							if (removePersonajeBug(p)) {
								continue;
							}
							if (!Constantes.puedeIniciarPelea(perso, p, this, celdaDestino)) {
								continue;
							}
							int agroP = p.getStatsObjEquipados().getStatParaMostrar(Constantes.STAT_AGREDIR_AUTOMATICAMENTE);
							int agroPerso = perso.getStatsObjEquipados().getStatParaMostrar(Constantes.STAT_AGREDIR_AUTOMATICAMENTE);
							Personaje agresor = agroPerso >= agroP ? perso : p;
							Personaje agredido = agroPerso >= agroP ? p : perso;
							iniciarPeleaPVP(agresor, agredido, false);
							return true;
						}
						bug = 8;
						for (final GrupoMob grupoMob : _grupoMobsTotales.values()) {
							if(perso.tieneObjModeloEquipado(101610)) {
								if(!perso.getMapa().esMazmorra()) {
								if(!perso.tieneMision(561) || perso.getEstadoMision(561) == 1) {
										Objeto Ayudante = perso.getObjPosicion(MainServidor.OBJETOS_POS_AYUDANTE);
										perso.borrarOEliminarConOR(Ayudante.getID(), true);
								}
							}
							}else if(perso.tieneObjModeloEquipado(101721)) {
								if(!perso.getMapa().esMazmorra()) {
									if(!perso.tieneMision(563) || perso.getEstadoMision(563) == 1) {
											Objeto Ayudante = perso.getObjPosicion(MainServidor.OBJETOS_POS_AYUDANTE);
											perso.borrarOEliminarConOR(Ayudante.getID(), true);
									}
								}
							}
							if (!Constantes.puedeIniciarPelea(perso, grupoMob, this, celdaDestino)) {
								// System.out.println("no cumple las condiciones para iniciar pelea");
								continue;
							}
							Pelea pelea = iniciarPelea(perso, null, perso.getCelda().getID(), (short) -1, Constantes.PELEA_TIPO_PVM,
							grupoMob, false);
							return true;
						}
					}
				}
				bug = 9;
				celdaDestino.aplicarAccion(perso);
			}
		} catch (Exception e) {
			String error = "EXCEPTION jugadorLLegaACelda bug: " + bug + " e:" + e.toString();
			GestorSalida.ENVIAR_BN_NADA(perso, error);
			MainServidor.redactarLogServidorln(error);
		}
		return ok;
	}*/
    private fun addSigGrupoMobEnCola(grupoMob: GrupoMob): Boolean {
        var gm: GrupoMob? = null
        if (_grupoMobsEnCola != null) {
            for (g in _grupoMobsEnCola) {
                if (g.getTipo() !== grupoMob.getTipo()) {
                    continue
                }
                if (g.getStrGrupoMob().equalsIgnoreCase(grupoMob.getStrGrupoMob())) {
                    gm = g
                    break
                }
            }
        }
        if (gm != null) {
            _grupoMobsEnCola.remove(gm)
            addGrupoMobSioSi(gm)
            return true
        }
        return false
    }

    // public synchronized void addSigGrupoMobRespawn(GrupoMob grupoMob) {
    // GrupoMob gm = null;
    // if (grupoMob.getTipo() == TipoGrupo.NORMAL) {
    // gm = addGrupoMobPosible(grupoMob.getCeldaID());
    // } else if (grupoMob.getTipo() == TipoGrupo.FIJO) {
    // gm = addGrupoMobPorTipo(grupoMob.getCeldaID(), grupoMob.getStrGrupoMob(), grupoMob.getTipo(),
    // grupoMob
    // .getCondInicioPelea());
    // } else if (grupoMob.getTipo() == TipoGrupo.HASTA_QUE_MUERA) {
    // addGrupoMobSioSi(grupoMob);
    // }
    // }
    @Synchronized
    fun addSiguienteGrupoMob(grupoMob: GrupoMob?, filtro: Boolean) {
        if (grupoMob == null) {
            return
        }
        when (grupoMob.getTipo()) {
            HASTA_QUE_MUERA, SOLO_UNA_PELEA, NORMAL -> return
            FIJO -> {
                if (filtro) {
                    if (!addSigGrupoMobEnCola(grupoMob)) {
                        AparecerMobs(this, grupoMob, Aparecer.INICIO_PELEA)
                    }
                    return
                }
                val gm: GrupoMob? = addGrupoMobPorTipo(grupoMob.getCeldaID(), grupoMob.getStrGrupoMob(), TipoGrupo.FIJO, grupoMob
                        .getCondInicioPelea(), grupoMob.getMapasRandom(), grupoMob.is_movible(), grupoMob.getJefe(), grupoMob.getIodoloJefe(), grupoMob.gethechizoJefe())
                if (gm != null) {
                    gm.setSegundosRespawn(grupoMob.getSegundosRespawn())
                }
            }
        }
    }

    @Synchronized
    fun addUltimoGrupoMob(grupoMob: GrupoMob?, filtro: Boolean) {
        if (grupoMob == null) {
            return
        }
        when (grupoMob.getTipo()) {
            SOLO_UNA_PELEA -> return
            HASTA_QUE_MUERA -> {
                if (!grupoMob.estaMuerto()) {
                    addGrupoMobSioSi(grupoMob)
                }
                return
            }
            NORMAL -> {
                // System.out.println("estaMuerto: " + grupoMob.estaMuerto());
                // System.out.println("esHeroico: " + grupoMob.esHeroico());
                if (filtro) {
                    // if (addSigGrupoMobEnCola()) {
                    // return;
                    // }
                    if (!grupoMob.estaMuerto() && grupoMob.esHeroico()) {
                        addGrupoMobSioSi(grupoMob)
                    } else {
                        AparecerMobs(this, grupoMob, Aparecer.FINAL_PELEA)
                    }
                    return
                }
                var celdaID: Short = grupoMob.getCeldaID()
                if (MainServidor.PARAM_MOBS_RANDOM_REAPARECER_OTRA_CELDA) {
                    celdaID = -1
                }
                var gm: GrupoMob? = null
                gm = if (grupoMob.getFijo()) {
                    // esta en duda, porq en gestorsql, al normal se le cambia a fijo
                    addGrupoMobPorTipo(celdaID, grupoMob.getStrGrupoMob(), TipoGrupo.NORMAL, grupoMob.getCondInicioPelea(),
                            grupoMob.getMapasRandom(), grupoMob.is_movible(), grupoMob.getJefe(), grupoMob.getIodoloJefe(), grupoMob.gethechizoJefe())
                } else {
                    addGrupoMobPosible(celdaID)
                }
                if (gm != null) {
                    gm.setSegundosRespawn(grupoMob.getSegundosRespawn())
                }
            }
            FIJO -> if (!grupoMob.estaMuerto() && grupoMob.esHeroico()) {
                addUltimoGrupoMob(grupoMob)
            }
        }
    }

    private fun addUltimoGrupoMob(grupoMob: GrupoMob) {
        if (grupoMob.estaMuerto()) {
            return
        }
        if (MainServidor.MODO_HEROICO || MainServidor.MAPAS_MODO_HEROICO.contains(iD)) {
            _grupoMobsEnCola.add(grupoMob)
        }
    }

    // --------------------------------
    @Synchronized
    fun sigIDGrupoMob(): Int {
        for (id in -1 downTo -50) {
            if (_grupoMobsTotales!![id] == null) {
                var usado = false
                for (pelea in _peleas!!.values()) {
                    if (pelea.getIDLuchInit2() === id) {
                        usado = true
                        break
                    }
                }
                if (usado) {
                    continue
                }
                return id
            }
        }
        return -1
    }

    @Synchronized
    private fun getGrupoMobInicioServer(celdaID: Short, heroico: String, estrellas: Int): GrupoMob? {
        if (_mobPosibles == null || _mobPosibles.isEmpty() || _grupoMobsTotales!!.size() >= maxGrupoDeMobs) {
            return null
        }
        var grupoMob: GrupoMob? = null
        if (!heroico.isEmpty()) {
            val strMob: String = heroico.split(Pattern.quote("|")).get(0)
            grupoMob = addGrupoMobPorTipo(celdaID, strMob, TipoGrupo.NORMAL, "", null, true, 0, "", 0)
            grupoMob.addObjetosKamasInicioServer(heroico)
        } else {
            grupoMob = addGrupoMobPosible(celdaID)
        }
        if (grupoMob == null) {
            return null
        }
        grupoMob.setBonusEstrellas(estrellas)
        return grupoMob
    }

    // --------------------------
    // AQUI ES PARA MOSTRAR LOS GRUPOS DE MOBS
    @Synchronized
    fun addGrupoMobSioSi(grupoMob: GrupoMob): GrupoMob {
        grupoMob.setID(sigIDGrupoMob())
        _grupoMobsTotales.put(grupoMob.getID(), grupoMob)
        if (cantPersonajes() > 0) {
            GestorSalida.ENVIAR_GM_GRUPOMOB_A_MAPA(this, "+" + grupoMob.stringGM(null))
        }
        return grupoMob
    }

    @Synchronized
    private fun addGrupoMobPosible(celdaID: Short): GrupoMob? {
        if (_mobPosibles == null || _mobPosibles.isEmpty()) {
            return null
        }
        val grupoMob = GrupoMob(_mobPosibles, this, celdaID, maxMobsPorGrupo, _minMobsPorGrupo)
        if (grupoMob.getMobs().isEmpty()) {
            return null
        }
        _grupoMobsTotales.put(grupoMob.getID(), grupoMob)
        if (cantPersonajes() > 0) {
            GestorSalida.ENVIAR_GM_GRUPOMOB_A_MAPA(this, "+" + grupoMob.stringGM(null))
        }
        return grupoMob
    }

    @Synchronized
    fun addGrupoMobPorTipoAgresion(celdaID: Short, strGrupoMob: String?, tipo: TipoGrupo?,
                                   condicion: String?, mapas: ArrayList<Mapa?>?, movible: Boolean, agresion: Byte, jefe: Int, idolos: String?, hechizoJefe: Int): GrupoMob? {
        var mapas: ArrayList<Mapa?>? = mapas
        var mapa = this
        if (mapas != null && mapas.size() > 1) {
            mapa = mapas.get(Formulas.getRandomInt(0, mapas.size() - 1))
        } else {
            mapas = null
        }
        val grupoMob = GrupoMob(mapa, celdaID, strGrupoMob, tipo, condicion, movible, agresion, jefe, idolos, hechizoJefe)
        if (grupoMob.getMobs().isEmpty()) {
            return null
        }
        grupoMob.setMapasRandom(mapas)
        mapa._grupoMobsTotales.put(grupoMob.getID(), grupoMob)
        if (mapa.cantPersonajes() > 0) {
            GestorSalida.ENVIAR_GM_GRUPOMOB_A_MAPA(mapa, "+" + grupoMob.stringGM(null))
        }
        return grupoMob
    }

    @Synchronized
    fun addGrupoMobPorTipo(celdaID: Short, strGrupoMob: String?, tipo: TipoGrupo?,
                           condicion: String?, mapas: ArrayList<Mapa?>?, movible: Boolean, jefe: Int, idolos: String?, hechizoJefe: Int): GrupoMob? {
        var mapas: ArrayList<Mapa?>? = mapas
        var mapa = this
        if (mapas != null && mapas.size() > 1) {
            mapa = mapas.get(Formulas.getRandomInt(0, mapas.size() - 1))
        } else {
            mapas = null
        }
        val grupoMob = GrupoMob(mapa, celdaID, strGrupoMob, tipo, condicion, movible, 0.toByte(), jefe, idolos, hechizoJefe)
        if (grupoMob.getMobs().isEmpty()) {
            return null
        }
        grupoMob.setMapasRandom(mapas)
        mapa._grupoMobsTotales.put(grupoMob.getID(), grupoMob)
        if (mapa.cantPersonajes() > 0) {
            GestorSalida.ENVIAR_GM_GRUPOMOB_A_MAPA(mapa, "+" + grupoMob.stringGM(null))
        }
        return grupoMob
    }

    @Synchronized
    fun iniciarPeleaPVP(agresor: Personaje, agredido: Personaje, deshonor: Boolean) {
        agresor.botonActDesacAlas('+')
        agredido.setAgresion(true)
        agresor.setAgresion(true)
        GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA(this, -1, 906, agresor.getID().toString() + "", agredido.getID().toString() + "")
        iniciarPelea(agresor, agredido, agresor.getCelda().getID(), agredido.getCelda().getID(), Constantes.PELEA_TIPO_PVP,
                null, false)
        agresor.getPelea().setDeshonor(deshonor)
        agredido.setAgresion(false)
        agresor.setAgresion(false)
    }

    @Synchronized
    fun iniciarPelea(pre1: PreLuchador?, pre2: PreLuchador?, celda1: Short, celda2: Short,
                     tipo: Byte, grupoMob: GrupoMob?, torneo: Boolean): Pelea? {
        var pre2: PreLuchador? = pre2
        var celda2 = celda2
        try {
            if (!puedeAgregarOtraPelea()) {
                return null
            }
            val cant = if (grupoMob != null) grupoMob.getCantMobs() else 1
            if (!prepararCeldasPelea(1, cant)) {
                return null
            }
            if (grupoMob != null) {
                if (grupoMob.enPelea()) {
                    return null
                }
                pre2 = grupoMob.getMobs().get(0).invocarMob(grupoMob.getID(), false, null)
                celda2 = Camino.getCeldaIDCercanaLibre(_celdas[grupoMob.getCeldaID()], this)
            }
            val id = sigIDPelea()
            val pelea = Pelea(id, this, pre1, pre2, celda1, celda2, tipo, grupoMob, strCeldasPeleaPosAtacante())
            pelea.enTorneo = torneo
            _peleas.put(id, pelea)
            if (grupoMob != null) {
                _grupoMobsTotales.remove(grupoMob.getID())
                // _grupoMobsFix.remove(grupoMob.getID());
                for (i in 1 until grupoMob.getMobs().size()) {
                    val mob: MobGrado = grupoMob.getMobs().get(i).invocarMob(pelea.sigIDLuchadores(), false, null)
                    if (mob.getID() === pre2.getID()) continue
                    pelea.unirsePelea(mob, pre2.getID())
                }
            }
            GestorSalida.ENVIAR_fC_CANTIDAD_DE_PELEAS(this)
            return if (!esMazmorra() || iD == 11095) {
                pelea
            } else {
                setStrCeldasPelea("")
                prepararCeldasPelea(1, 1)
                pelea
            }
        } catch (e: Exception) {
            MainServidor.redactarLogServidorln("EXCEPTION iniciarPelea " + e.toString())
            e.printStackTrace()
        }
        return null
    }

    @Synchronized
    fun iniciarPeleaKoliseo(init1: GrupoKoliseo?, init2: GrupoKoliseo?): Boolean {
        if (!prepararCeldasPelea(MainServidor.CANTIDAD_MIEMBROS_EQUIPO_KOLISEO,
                        MainServidor.CANTIDAD_MIEMBROS_EQUIPO_KOLISEO)) {
            return false
        }
        val id = sigIDPelea()
        val pelea = Pelea(id, this, init1, init2, strCeldasPeleaPosAtacante())
        _peleas.put(id, pelea)
        GestorSalida.ENVIAR_fC_CANTIDAD_DE_PELEAS(this)
        return true
    }

    fun borrarJugador(perso: Personaje?) {
        if (_personajes == null) {
            return
        }
        _personajes.remove(perso)
    }

    private fun strCeldasPeleaPosAtacante(): String {
        return _celdasPelea + "|" + colorCeldasAtacante
    }

    @Synchronized
    fun sigIDPelea(): Short {
        var id: Short = 1
        while (true) {
            if (_peleas!![id] == null) {
                return id
            }
            id++
        }
    }

    val numeroPeleas: Int
        get() = _peleas!!.size()
    val peleas: Map<Short, Any?>?
        get() = _peleas

    fun borrarPelea(id: Short) {
        _peleas.remove(id)
    }

    fun getPelea(id: Short): Pelea? {
        return _peleas!![id]
    }

    val puertaCercado: ObjetoInteractivo?
        get() {
            for (c in _celdas.values()) {
                try {
                    if (c.getObjetoInteractivo().getObjIntModelo().getID() === 120) {
                        return c.getObjetoInteractivo()
                    }
                } catch (e: Exception) {
                }
            }
            return null
        }
    val grupoMobsTotales: Map<Any, Any?>?
        get() = _grupoMobsTotales

    // public CopyOnWriteArrayList<GrupoMob> getGrupoMobsHeroicos() {
    // return _grupoMobsHeroicos;
    // }
    fun borrarGrupoMob(id: Int) {
        _grupoMobsTotales.remove(id)
    }

    fun borrarTodosMobsNoFijos() {
        _mobPosibles.clear()
        val idsBorrar: ArrayList<Integer> = ArrayList()
        for (gm in _grupoMobsTotales!!.values()) {
            if (gm.getTipo() === TipoGrupo.FIJO) {
                continue
            }
            val id: Int = gm.getID()
            idsBorrar.add(id)
            GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(this, id)
        }
        for (id in idsBorrar) {
            _grupoMobsTotales.remove(id)
        }
        GestorSQL.UPDATE_SET_MOBS_MAPA(iD, "")
    }

    fun borrarTodosMobsFijos() {
        val idsBorrar: ArrayList<Integer> = ArrayList()
        for (gm in _grupoMobsTotales!!.values()) {
            if (gm.getTipo() !== TipoGrupo.FIJO) {
                continue
            }
            val id: Int = gm.getID()
            idsBorrar.add(id)
            GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(this, id)
        }
        for (id in idsBorrar) {
            _grupoMobsTotales.remove(id)
        }
        GestorSQL.DELETE_MOBS_FIX_MAPA(iD)
    }

    fun borrarMobsEvento() {
        val idsBorrar: ArrayList<Integer> = ArrayList()
        for (gm in _grupoMobsTotales!!.values()) {
            if (gm.is_evento()) {
                val id: Int = gm.getID()
                idsBorrar.add(id)
                GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(this, id)
            }
        }
        for (id in idsBorrar) {
            _grupoMobsTotales.remove(id)
        }
    }

    @Synchronized
    fun salvarMapaHeroico() {
        if (_grupoMobsTotales!!.isEmpty() && _grupoMobsEnCola.isEmpty()) {
            GestorSQL.DELETE_MAPA_HEROICO(iD)
            return
        }
        val mobs = StringBuilder()
        val objetos = StringBuilder()
        val kamas = StringBuilder()
        val grupoMobs: ArrayList<GrupoMob> = ArrayList()
        grupoMobs.addAll(_grupoMobsTotales!!.values())
        grupoMobs.addAll(_grupoMobsEnCola)
        var paso = false
        for (g in grupoMobs) {
            if (g.getKamasHeroico() <= 0 && g.cantObjHeroico() === 0) {
                continue
            }
            if (paso) {
                mobs.append("|")
                objetos.append("|")
                kamas.append("|")
            }
            mobs.append(g.getStrGrupoMob())
            objetos.append(g.getIDsObjeto())
            kamas.append(g.getKamasHeroico())
            paso = true
        }
        if (!paso) {
            GestorSQL.DELETE_MAPA_HEROICO(iD)
            return
        }
        GestorSQL.REPLACE_MAPAS_HEROICO(iD, mobs.toString(), objetos.toString(), kamas.toString())
    }

    fun objetosTirados(perso: Personaje?) {
        for (c in _celdas.values()) {
            if (c.getObjetoTirado() != null) {
                GestorSalida.ENVIAR_GDO_OBJETO_TIRAR_SUELO(perso, '+', c.getID(), c.getObjetoTirado().getObjModelo().getID(),
                        false, "")
            }
        }
    }

    fun getCeldaPorPos(x: Byte, y: Byte): Celda? {
        for (c in _celdas.values()) {
            if (c.getCoordX() === x && c.getCoordY() === y) {
                return c
            }
        }
        return null
    }
}