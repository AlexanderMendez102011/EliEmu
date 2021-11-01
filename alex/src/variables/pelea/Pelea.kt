package variables.pelea
//import estaticos.Colores;
import java.awt.event.ActionEvent

class Pelea {
    private var numeroLanzamientos = 4
    private val turnosLanzamientos = 4
    private var turnosJefe = 0
    private var _tacleado = false
    private var _cerrado1 = false
    private var _soloGrupo1 = false
    private var _cerrado2 = false
    private var _soloGrupo2 = false
    private var _sinEspectador = false
    private var _ayuda1 = false
    private var _ayuda2 = false
    private var _deshonor = false
    private var entregoLogro = false
    private var _byteColor2: Byte = 0
    private var _byteColor1: Byte = 0
    private var _cantUltAfec: Byte = 0
    private var _fase: Byte = 0
    private var _tipo: Byte = -1
    private var _vecesQuePasa: Byte = 0
    private var _alinPelea: Byte = Constantes.ALINEACION_NEUTRAL
    private var _cantCAC: Byte = 0
    private var _nroOrdenLuc: Byte = -1
    var iD: Short = 0
        private set
    private var _celdaID1: Short = 0
    private var _celdaID2: Short = 0
    private var _bonusEstrellas = -1
    private var _tiempoHechizo = 0
    private var _idLuchInit1 = 0
    private var _idLuchInit2 // _idMobReto,
            = 0
    private var _ultimaInvoID = 0
    private var _idUnicaAccion = -1
    private var _tiempoPreparacion: Long = 0
    private var _tiempoCombate: Long = 0
    private var _tiempoTurno: Long = 0
    private var _kamasRobadas: Long = 0
    private var _expRobada: Long = 0
    var _tempAccion: String? = ""
    private var _listadefensores: String? = ""
    private val _asesinos: StringBuilder? = StringBuilder()
    var _mapaCopia: Mapa? = null
    private var _mapaReal: Mapa? = null
    private var _luchInit1: Luchador? = null
    private var _luchInit2: Luchador? = null
    private var _luchadorDeTurno: Luchador? = null
    private var _mobGrupo: GrupoMob? = null
    private var _rebootTurno: Timer? = null
    val _equipo1: ConcurrentHashMap<Integer?, Luchador?>? = ConcurrentHashMap<Integer?, Luchador?>()
    val _equipo2: ConcurrentHashMap<Integer?, Luchador?>? = ConcurrentHashMap<Integer?, Luchador?>()
    val _espectadores: ConcurrentHashMap<Integer?, Luchador?>? = ConcurrentHashMap<Integer?, Luchador?>()
    private val _celdasPos1: ArrayList<Celda?>? = ArrayList<Celda?>()
    private val _celdasPos2: ArrayList<Celda?>? = ArrayList<Celda?>()
    val _inicioLuchEquipo1: List<Luchador?>? = ArrayList(9)
    val _inicioLuchEquipo2: List<Luchador?>? = ArrayList(9)
    val _ordenLuchadores: List<Luchador?>? = ArrayList()
    private val _listaMuertos: List<Luchador?>? = ArrayList()
    private val _IAs: List<Inteligencia?>? = ArrayList()
    private val _posInicialLuch: Map<Integer?, Celda?>? = TreeMap<Integer?, Celda?>()
    var _glifos: CopyOnWriteArrayList<Glifo?>? = CopyOnWriteArrayList<Glifo?>()
    var _trampas: List<Trampa?>? = ArrayList<Trampa?>()
    private var _capturadores: List<Luchador?>? = null
    private var _domesticadores: List<Luchador?>? = null
    private val _retos: ConcurrentHashMap<Byte?, Reto?>? = null
    private var _objetosRobados: ArrayList<Objeto?>? = null
    private var _acciones: ArrayList<Accion?>? = null
    private var _posiblesBotinPelea: ArrayList<Botin?>? = null
    private var _salvarMobHeroico = false
    private var _1vs1 = false
    var enTorneo = false
    var prospeccionEquipo = 0
        private set
    private var _luchMenorNivelReto = 0
    var ultimoElementoReto: Int = Constantes.ELEMENTO_NULO
    private var _ultimoMovedorIDReto = 0
    private var _ultimoTipoDañoReto: TipoDaño? = TipoDaño.NULL
    private var jefe: Luchador? = null
    var idolos: Map<Integer?, ArrayList<Idolo?>?>? = TreeMap()
    private var _timerPelea: Timer? = Timer(1, object : ActionListener() {
        fun actionPerformed(e: ActionEvent?) {
            actionListener()
        }
    })
    var peleaEmpezada = false
    fun setEmpezoPelea(fightStarted: Boolean) {
        peleaEmpezada = fightStarted
    }

    constructor(id: Short, mapa: Mapa?, pre1: PreLuchador?, pre2: PreLuchador?, celda1: Short,
                celda2: Short, tipo: Byte, grupoMob: GrupoMob?, posPelea: String?) {
        try {
            pre1.setPelea(this)
            pre2.setPelea(this)
            _fase = Constantes.PELEA_FASE_POSICION
            _celdaID1 = celda1
            _celdaID2 = celda2
            if (_celdaID1 == _celdaID2) {
                var nCelda: Short = -1
                for (i in 1..4) {
                    for (c in Camino.celdasPorDistancia(mapa.getCelda(_celdaID2), mapa, i)) {
                        val celda: Celda = mapa.getCelda(c)
                        if (celda.getID() === _celdaID1 || !celda.esCaminable(false)) {
                            continue
                        }
                        nCelda = celda.getID()
                        break
                    }
                    if (nCelda.toInt() != -1) {
                        break
                    }
                }
                _celdaID2 = nCelda
            }
            _tiempoPreparacion = System.currentTimeMillis()
            _tiempoCombate = _tiempoPreparacion
            _tipo = tipo
            iD = id
            _mapaCopia = mapa.copiarMapa(posPelea)
            _mapaReal = mapa
            _alinPelea = _mapaReal.getSubArea().getAlineacion()
            _luchInit1 = Luchador(this, pre1, false)
            _idLuchInit1 = _luchInit1.getID()
            _equipo1.put(_idLuchInit1, _luchInit1)
            _luchInit2 = Luchador(this, pre2, false)
            _idLuchInit2 = _luchInit2.getID()
            _equipo2.put(_idLuchInit2, _luchInit2)
            if (grupoMob != null) {
                if ((pre2 as MobGrado?).getIDModelo() === grupoMob.getJefe()) {
                    jefe = _luchInit2
                }
                if (!grupoMob.getIodoloJefe().isEmpty()) {
                    val idolosTemp: Array<String?> = grupoMob.getIodoloJefe().split(";")
                    for (idolo in idolosTemp) {
                        val ido: Idolo = Mundo.getIdolo(Integer.parseInt(idolo)) ?: continue
                        if (!idolos!!.containsKey(ido.getEjecucion())) {
                            idolos.put(ido.getEjecucion(), ArrayList())
                        }
                        idolos!![ido.getEjecucion()].add(ido)
                    }
                }
            }
            if (_tipo != Constantes.PELEA_TIPO_DESAFIO) {
                startTimerInicioPelea()
                if (_tipo != Constantes.PELEA_TIPO_PVP && _luchInit1.getAlineacion() !== Constantes.ALINEACION_NULL && _luchInit2.getAlineacion() !== Constantes.ALINEACION_NULL && _luchInit1.getAlineacion() !== _luchInit2.getAlineacion()) {
                    _tipo = Constantes.PELEA_TIPO_PRISMA
                }
            }
            GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(this, 3, _fase, _tipo == Constantes.PELEA_TIPO_DESAFIO, true, false,
                    if (_tipo == Constantes.PELEA_TIPO_DESAFIO) 0 else MainServidor.SEGUNDOS_INICIO_PELEA * 1000 - 1500,
                    _tipo)
            definirCeldasPos()
            if (pre1.getClass() === Personaje::class.java) {
                if (tipo == Constantes.PELEA_TIPO_PVM) {
                    for (idol in (pre1 as Personaje?).getIdolos().values()) {
                        if (!idolos!!.containsKey(idol.get(0).getEjecucion())) {
                            idolos.put(idol.get(0).getEjecucion(), idol)
                        } else {
                            idolos!![idol.get(0).getEjecucion()].addAll(idol)
                        }
                    }
                }
                GestorSalida.ENVIAR_ILF_CANTIDAD_DE_VIDA(pre1 as Personaje?, 0)
                (pre1 as Personaje?).setCelda(null)
                (pre1 as Personaje?).refrescarStuff(true, true, true)
            }
            if (pre2.getClass() === Personaje::class.java) {
                GestorSalida.ENVIAR_ILF_CANTIDAD_DE_VIDA(pre2 as Personaje?, 0)
                (pre2 as Personaje?).refrescarStuff(true, true, true)
            }
            GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(_mapaReal, _idLuchInit1)
            GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(_mapaReal, _idLuchInit2)
            _luchInit1.setCeldaPelea(getCeldaRandom(_celdasPos1))
            _luchInit2.setCeldaPelea(getCeldaRandom(_celdasPos2))
            _luchInit1.setEquipoBin(0.toByte())
            _luchInit2.setEquipoBin(1.toByte())
            if (_tipo != Constantes.PELEA_TIPO_PVM_NO_ESPADA) {
                GestorSalida.ENVIAR_Gc_MOSTRAR_ESPADA_EN_MAPA(_mapaReal, mostrarEspada())
                GestorSalida.ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(_mapaReal, _idLuchInit1, _luchInit1)
                GestorSalida.ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(_mapaReal, _idLuchInit2, _luchInit2)
            }
            GestorSalida.ENVIAR_GM_LUCHADORES_A_PELEA(this, 7, _mapaCopia)
            if (pre2.getClass() === Prisma::class.java) {
                val str: String = _mapaReal.getID().toString() + "|" + _mapaReal.getX() + "|" + _mapaReal.getY()
                for (p in Mundo.getPersonajesEnLinea()) {
                    if (p.getAlineacion() !== pre2.getAlineacion()) {
                        continue
                    }
                    GestorSalida.ENVIAR_CA_MENSAJE_ATAQUE_PRISMA(p, str)
                }
            } else if (pre2.getClass() === Recaudador::class.java) {
                val recaudador: Recaudador? = pre2 as Recaudador?
                recaudador.actualizarAtacantesDefensores()
                val str: String = recaudador.mensajeDeAtaque()
                for (p in recaudador.getGremio().getMiembros()) {
                    if (p.enLinea()) {
                        GestorSalida.ENVIAR_gA_MENSAJE_SOBRE_RECAUDADOR(p, str)
                    }
                }
            }
            setGrupoMob(grupoMob)
            if (_luchInit1.getPersonaje() != null) {
                cargarMultiman(_luchInit1.getPersonaje())
            }
            if (_luchInit2.getPersonaje() != null) {
                cargarMultiman(_luchInit2.getPersonaje())
            }
            GestorSalida.ENVIAR_fL_LISTA_PELEAS_AL_MAPA(_mapaReal)
        } catch (e: Exception) {
            e.printStackTrace()
            pre1.setPelea(null)
            pre2.setPelea(null)
        }
    }

    constructor(id: Short, mapa: Mapa?, grupo1: GrupoKoliseo?, grupo2: GrupoKoliseo?,
                posPelea: String?) {
        _fase = Constantes.PELEA_FASE_POSICION
        _tiempoPreparacion = System.currentTimeMillis()
        _tiempoCombate = _tiempoPreparacion
        _tipo = Constantes.PELEA_TIPO_KOLISEO
        iD = id
        _mapaCopia = mapa.copiarMapa(posPelea)
        _mapaReal = mapa
        _alinPelea = _mapaReal.getSubArea().getAlineacion()
        var b = false
        for (perso in grupo1.getMiembros()) {
            if (perso.enTorneo !== 0) {
                ServidorSocket.perderTorneo(perso)
            }
            GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(perso.getMapa(), perso.getID())
            perso.setCelda(null)
            if (perso.getMapa().getID() !== mapa.getID()) {
                perso.teleportSinTodos(mapa.getID(), 100.toShort())
            }
            perso.setPelea(this)
            perso.refrescarStuff(true, true, true)
            val l = Luchador(this, perso, false)
            _equipo1.put(perso.getID(), l)
            if (!b) {
                _luchInit1 = l
                _idLuchInit1 = _luchInit1.getID()
                b = true
            }
            GestorSalida.ENVIAR_ILF_CANTIDAD_DE_VIDA(perso, 0)
        }
        b = false
        for (perso in grupo2.getMiembros()) {
            GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(perso.getMapa(), perso.getID())
            perso.setCelda(null)
            if (perso.getMapa().getID() !== mapa.getID()) {
                perso.teleportSinTodos(mapa.getID(), 100.toShort())
            }
            perso.setPelea(this)
            perso.refrescarStuff(true, true, true)
            val l = Luchador(this, perso, false)
            l.setEquipoBin(1.toByte())
            _equipo2.put(perso.getID(), l)
            if (!b) {
                _luchInit2 = l
                _idLuchInit2 = _luchInit2.getID()
                b = true
            }
            GestorSalida.ENVIAR_ILF_CANTIDAD_DE_VIDA(perso, 0)
        }
        try {
            Thread.sleep(1500)
        } catch (e: Exception) {
        }
        GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(this, 3, 2, false, true, false,
                MainServidor.SEGUNDOS_INICIO_PELEA * 1000 - 1500, _tipo)
        startTimerInicioPelea()
        val equipo1: List<Entry<Integer?, Luchador?>?> = ArrayList<Entry<Integer?, Luchador?>?>()
        equipo1.addAll(_equipo1.entrySet())
        val equipo2: List<Entry<Integer?, Luchador?>?> = ArrayList<Entry<Integer?, Luchador?>?>()
        equipo2.addAll(_equipo2.entrySet())
        definirCeldasPos()
        val gm1 = StringBuilder("GM")
        val gm2 = StringBuilder("GM")
        for (entry in equipo1) {
            val lucha: Luchador = entry.getValue()
            val celdaRandom: Celda? = getCeldaRandom(_celdasPos1)
            if (celdaRandom == null) {
                _equipo1.remove(lucha.getID())
                continue
            }
            lucha.setCeldaPelea(celdaRandom)
            gm1.append("|+" + lucha.stringGM(0))
        }
        for (entry in equipo2) {
            val lucha: Luchador = entry.getValue()
            val celdaRandom: Celda? = getCeldaRandom(_celdasPos2)
            if (celdaRandom == null) {
                _equipo2.remove(lucha.getID())
                continue
            }
            lucha.setCeldaPelea(celdaRandom)
            gm2.append("|+" + lucha.stringGM(0))
        }
        for (persos in grupo1.getMiembros()) {
            GestorSalida.enviar(persos, gm1.toString(), true)
            if (MainServidor.PARAM_VER_JUGADORES_KOLISEO) {
                GestorSalida.enviar(persos, gm2.toString(), true)
            }
        }
        if (MainServidor.MOSTRAR_ENVIOS) {
            System.out.println("GM LUCHADORES TEAM 1: KOLISEO>> " + gm1.toString())
        }
        for (persos in grupo2.getMiembros()) {
            GestorSalida.enviar(persos, gm2.toString(), true)
            if (MainServidor.PARAM_VER_JUGADORES_KOLISEO) {
                GestorSalida.enviar(persos, gm1.toString(), true)
            }
        }
        GestorSalida.ENVIAR_fL_LISTA_PELEAS_AL_MAPA(_mapaReal)
        if (MainServidor.MOSTRAR_ENVIOS) {
            System.out.println("GM LUCHADORES TEAM 2: KOLISEO>> " + gm2.toString())
        }
    }

    private fun actionListener() {
        when (_fase) {
            Constantes.PELEA_FASE_INICIO, Constantes.PELEA_FASE_POSICION -> try {
                iniciarPelea()
            } catch (e1: Exception) {
                acaboPelea(2.toByte())
            }
            Constantes.PELEA_FASE_COMBATE -> preFinTurno(null)
            Constantes.PELEA_FASE_FINALIZADO -> {
            }
        }
    }

    private fun startTimerInicioPelea() {
        if (_timerPelea != null) {
            _timerPelea.setRepeats(false)
            _timerPelea.setInitialDelay(MainServidor.SEGUNDOS_INICIO_PELEA * 1000)
            _timerPelea.setDelay(MainServidor.SEGUNDOS_INICIO_PELEA * 1000)
            _timerPelea.restart()
        }
    }

    private fun startTimerInicioTurno() {
        if (_timerPelea != null) {
            _timerPelea.stop()
            _timerPelea.setRepeats(false)
            _timerPelea.setInitialDelay(MainServidor.SEGUNDOS_TURNO_PELEA * 1000)
            _timerPelea.setDelay(MainServidor.SEGUNDOS_TURNO_PELEA * 1000)
            _timerPelea.restart()
        }
    }

    fun getPosPelea(color: Int): Int {
        when (color) {
            1 -> return _celdasPos1.size()
            2 -> return _celdasPos2.size()
        }
        return 0
    }

    fun setUltimoTipoDaño(t: TipoDaño?) {
        set_ultimoTipoDañoReto(t)
    }

    private fun addDropPelea(dropM: DropMob?) {
        if (_posiblesBotinPelea == null) {
            _posiblesBotinPelea = ArrayList()
        }
        for (dropP in _posiblesBotinPelea) {
            if (dropM.esIdentico(dropP.getDrop())) {
                dropP.addBotinMaximo(dropM.getMaximo())
                return
            }
        }
        _posiblesBotinPelea.add(Botin(dropM))
    }

    val mapaCopia: Mapa?
        get() = _mapaCopia
    val mapaReal: Mapa?
        get() = _mapaReal
    val inicioLuchEquipo1: List<Any?>?
        get() = _inicioLuchEquipo1
    val inicioLuchEquipo2: List<Any?>?
        get() = _inicioLuchEquipo2

    // private void checkeaInicioPelea() {
    // switch (_estadoPelea) {
    // case Informacion.PELEA_ESTADO_INICIO :
    // case Informacion.PELEA_ESTADO_POSICION :
    // if (getTiempoFaltInicioPelea() <= 0) {
    // try {
    // iniciarPelea();
    // } catch (final Exception e) {
    // e.printStackTrace();
    // acaboPelea((byte) 2);
    // }
    // }
    // break;
    // }
    // }
    val tiempoFaltInicioPelea: Long
        get() = Math.max(0,
                MainServidor.SEGUNDOS_INICIO_PELEA * 1000 - (System.currentTimeMillis() - _tiempoPreparacion))

    fun setDeshonor(d: Boolean) {
        _deshonor = d
    }

    fun setUltAfec(afec: Byte) {
        _cantUltAfec = afec
    }

    fun getRetos(): ConcurrentHashMap<Byte?, Reto?>? {
        return _retos
    }

    fun setListaDefensores(str: String?) {
        _listadefensores = str
    }

    fun getPosInicial(): Map<Integer?, Celda?>? {
        return _posInicialLuch
    }

    // public void setIDMobReto(final int mob) {
    // _idMobReto = mob;
    // }
    //
    // public int getIDMobReto() {
    // return _idMobReto;
    // }
    private fun setGrupoMob(gm: GrupoMob?) {
        setMobGrupo(gm)
        if (_mobGrupo == null) {
            return
        }
        _mobGrupo.setPelea(this)
        if (_tipo == Constantes.PELEA_TIPO_PVM) {
            _bonusEstrellas = _mobGrupo.getBonusEstrellas()
        }
    }

    @Synchronized
    private fun cargarMultiman(perso: Personaje?) {
        if (!MainServidor.PERMITIR_MULTIMAN_TIPO_COMBATE.contains(_tipo)) {
            return
        }
        if (perso == null) {
            return
        }
        val obj: Objeto = perso.getObjPosicion(MainServidor.OBJETOS_POS_AYUDANTE) ?: return
        val stats: Array<String?> = obj.convertirStatsAString(true).split(",")
        for (st in stats) {
            val statID: Int = Integer.parseInt(st.split("#").get(0), 16)
            val mobMultiman: Int = Integer.parseInt(st.split("#").get(3), 16)
            if (statID != Constantes.STAT_MAS_COMPAÑERO) {
                continue
            }
            val mobModelo: MobModelo = Mundo.getMobModelo(mobMultiman) ?: continue
            val idMultiman = sigIDLuchadores()
            val multiman: Personaje = Personaje.crearMultiman(idMultiman, perso.getNivel(),
                    perso.getTotalStatsPelea().getTotalStatParaMostrar(Constantes.STAT_MAS_INICIATIVA, this, null),
                    mobModelo)
            multiman.setResets(perso.getResets())
            multiman.setMapa(_mapaCopia)
            multiman.setCelda(perso.getCelda())
            if (unirsePelea(multiman, perso.getID())) {
                perso.addCompañero(multiman)
                multiman.addCompañero(perso)
            }
        }
    }

    @Synchronized
    fun sigIDLuchadores(): Int {
        var id = 0
        for (luchador in luchadoresDeEquipo(3)) {
            if (luchador.getID() < id) {
                id = luchador.getID()
            }
        }
        return --id
    }

    private fun definirCeldasPos() {
        val b: Boolean = Formulas.getRandomBoolean()
        _byteColor1 = (if (b) 1 else 0).toByte()
        _byteColor2 = (if (_byteColor1.toInt() == 1) 0 else 1).toByte()
        when (_mapaReal.getColorCeldasAtacante().toLowerCase()) {
            "red" -> {
                _byteColor1 = 0
                _byteColor2 = 1
            }
            "blue" -> {
                _byteColor1 = 1
                _byteColor2 = 0
            }
        }
        if (MainServidor.SON_DE_LUCIANO) {
            when (_tipo) {
                2, 3, 4, 5, 7 -> {
                    _byteColor1 = 0
                    _byteColor2 = 1
                }
            }
        }
        analizarPosiciones(_byteColor1, _celdasPos1)
        analizarPosiciones(_byteColor2, _celdasPos2)
        GestorSalida.ENVIAR_GP_POSICIONES_PELEA(this, 1, _mapaCopia.strCeldasPelea(), _byteColor1)
        GestorSalida.ENVIAR_GP_POSICIONES_PELEA(this, 2, _mapaCopia.strCeldasPelea(), _byteColor2)
    }

    /*
	 * private void actionListener() { switch (_fase) { case
	 * Constantes.PELEA_FASE_INICIO: case Constantes.PELEA_FASE_POSICION: try {
	 * iniciarPelea(); } catch (final Exception e1) { acaboPelea((byte) 2); } break;
	 * case Constantes.PELEA_FASE_COMBATE: preFinTurno(null); break; case
	 * Constantes.PELEA_FASE_FINALIZADO: break; } }
	 */
    @Synchronized
    fun puedeUnirsePelea(pre: PreLuchador?, idInitAUnir: Int): Celda? {
        var perso: Personaje? = null
        try {
            if (pre.getClass() === Personaje::class.java) {
                perso = pre as Personaje?
            }
        } catch (e: Exception) {
        }
        if (_fase > Constantes.PELEA_FASE_POSICION) {
            GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 'l')
            return null
        }
        if (perso != null) {
            if (perso.getCalabozo() || perso.estaInmovil()) {
                GestorSalida.ENVIAR_Im_INFORMACION(perso, "1DONT_MOVE_TEMP")
                return null
            }
            if (_mobGrupo != null && !Condiciones.validaCondiciones(perso, _mobGrupo.getCondUnirsePelea(), this)) {
                GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 'i')
                return null
            }
        }
        if (_equipo1.containsKey(idInitAUnir)) {
            if (MainServidor.MAPAS_PERSONAJE.containsKey(mapaReal.getID()) && perso != null) {
                if (_equipo1.size() >= MainServidor.MAPAS_PERSONAJE.get(mapaReal.getID())) {
                    GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 't')
                    return null
                }
            } else if (_equipo1.size() >= 8) {
                GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 't')
                return null
            }
            if (_soloGrupo1) {
                val grupo: Grupo = _luchInit1.getPersonaje().getGrupoParty()
                if (grupo != null && !grupo.getIDsPersos().contains(pre.getID())) {
                    GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 'f')
                    return null
                }
            }
            if (_cerrado1) {
                GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 'f')
                return null
            }
            if (_tipo == Constantes.PELEA_TIPO_PVP || _tipo == Constantes.PELEA_TIPO_PRISMA) {
                if (_luchInit1.getAlineacion() !== pre.getAlineacion()) {
                    GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 'a')
                    return null
                }
                if (perso != null) {
                    if (!MainServidor.PARAM_PERMITIR_MULTICUENTA_PELEA_PVP) {
                        for (luch in _equipo1.values()) {
                            try {
                                if (luch.getPersonaje() == null || luch.getPersonaje().getCuenta() == null) {
                                    continue
                                }
                                if (luch.getPersonaje().getCuenta().getActualIP()
                                                .equalsIgnoreCase(perso.getCuenta().getActualIP())) {
                                    GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 'f')
                                    return null
                                }
                            } catch (e: Exception) {
                            }
                        }
                    }
                }
            } else if (_tipo == Constantes.PELEA_TIPO_RECAUDADOR) {
                if (perso != null) {
                    if (perso.getGremio() != null
                            && _luchInit2.getRecaudador().getGremio().getID() === perso.getGremio().getID()) {
                        GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 'g')
                        return null
                    }
                    if (!MainServidor.PARAM_PERMITIR_MULTICUENTA_PELEA_RECAUDADOR) {
                        for (luch in _equipo1.values()) {
                            try {
                                if (luch.getPersonaje() == null || luch.getPersonaje().getCuenta() == null) {
                                    continue
                                }
                                if (luch.getPersonaje().getCuenta().getActualIP()
                                                .equalsIgnoreCase(perso.getCuenta().getActualIP())) {
                                    GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 'f')
                                    return null
                                }
                            } catch (e: Exception) {
                            }
                        }
                    }
                    if (MainServidor.CLASES_PELEA_RECAUDADOR.containsKey(perso.getClaseID(true))) {
                        var cant = 0
                        for (luch in _equipo1.values()) {
                            try {
                                if (luch.getPersonaje() == null || luch.getPersonaje().getCuenta() == null) {
                                    continue
                                }
                                if (luch.getPersonaje().getClaseID(true) === perso.getClaseID(true)) {
                                    cant++
                                }
                            } catch (e: Exception) {
                            }
                        }
                        if (cant >= MainServidor.CLASES_PELEA_RECAUDADOR.get(perso.getClaseID(true))) {
                            GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 'f')
                            return null
                        }
                    }
                }
            }
        } else if (_equipo2.containsKey(idInitAUnir)) {
            if (MainServidor.MAPAS_PERSONAJE.containsKey(mapaReal.getID()) && perso != null) {
                if (_equipo2.size() >= MainServidor.MAPAS_PERSONAJE.get(mapaReal.getID())) {
                    GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 't')
                    return null
                }
            } else if (_equipo2.size() >= 8) {
                GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 't')
                return null
            }
            if (_soloGrupo2) {
                val grupo: Grupo = _luchInit2.getPersonaje().getGrupoParty()
                if (grupo != null && !grupo.getIDsPersos().contains(pre.getID())) {
                    GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 'f')
                    return null
                }
            }
            if (_cerrado2) {
                GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 'f')
                return null
            }
            if (_tipo == Constantes.PELEA_TIPO_PVP || _tipo == Constantes.PELEA_TIPO_PRISMA) {
                if (_luchInit2.getAlineacion() !== pre.getAlineacion()) {
                    GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 'a')
                    return null
                }
                if (perso != null) {
                    if (!MainServidor.PARAM_PERMITIR_MULTICUENTA_PELEA_PVP) {
                        for (luch in _equipo2.values()) {
                            try {
                                if (luch.getPersonaje() == null || luch.getPersonaje().getCuenta() == null) {
                                    continue
                                }
                                if (luch.getPersonaje().getCuenta().getActualIP()
                                                .equalsIgnoreCase(perso.getCuenta().getActualIP())) {
                                    GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 'f')
                                    return null
                                }
                            } catch (e: Exception) {
                            }
                        }
                    }
                }
            } else if (_tipo == Constantes.PELEA_TIPO_RECAUDADOR) {
                if (perso != null) {
                    if (perso.getGremio() != null
                            && _luchInit2.getRecaudador().getGremio().getID() !== perso.getGremio().getID()) {
                        GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 'g')
                        return null
                    }
                    if (!MainServidor.PARAM_PERMITIR_MULTICUENTA_PELEA_RECAUDADOR) {
                        for (luch in _equipo2.values()) {
                            try {
                                if (luch.getPersonaje() == null || luch.getPersonaje().getCuenta() == null) {
                                    continue
                                }
                                if (luch.getPersonaje().getCuenta().getActualIP()
                                                .equalsIgnoreCase(perso.getCuenta().getActualIP())) {
                                    GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 'f')
                                    return null
                                }
                            } catch (e: Exception) {
                            }
                        }
                    }
                    if (MainServidor.CLASES_PELEA_RECAUDADOR.containsKey(perso.getClaseID(true))) {
                        var cant: Byte = 0
                        for (luch in _equipo2.values()) {
                            try {
                                if (luch.getPersonaje() == null || luch.getPersonaje().getCuenta() == null) {
                                    continue
                                }
                                if (luch.getPersonaje().getClaseID(true) === perso.getClaseID(true)) {
                                    cant++
                                }
                            } catch (e: Exception) {
                            }
                        }
                        if (cant >= MainServidor.CLASES_PELEA_RECAUDADOR.get(perso.getClaseID(true))) {
                            GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 'f')
                            return null
                        }
                    }
                }
            }
        }
        var celda: Celda? = null
        if (_equipo1.containsKey(idInitAUnir)) {
            celda = getCeldaRandom(_celdasPos1)
            if (celda == null) {
                return null
            }
        } else if (_equipo2.containsKey(idInitAUnir)) {
            celda = getCeldaRandom(_celdasPos2)
            if (celda == null) {
                return null
            }
        }
        if (perso != null && !perso.esMultiman()) {
            if (perso.getMapa().getID() !== _mapaCopia.getID()) {
                when (_tipo) {
                    Constantes.PELEA_TIPO_PRISMA, Constantes.PELEA_TIPO_RECAUDADOR -> if (perso.getPrePelea() == null
                            || perso.getPrePelea().getMapaCopia().getID() !== _mapaCopia.getID()) {
                        perso.setPrePelea(this, idInitAUnir)
                        perso.teleport(_mapaCopia.getID(), celda.getID())
                        return null
                    }
                    else -> {
                        GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 'p')
                        return null
                    }
                }
            }
        }
        return celda
    }

    @Synchronized
    fun unirsePelea(pre: PreLuchador?, idInitAUnir: Int): Boolean {
        val celda: Celda = puedeUnirsePelea(pre, idInitAUnir) ?: return false
        var perso: Personaje? = null
        try {
            if (pre.getClass() === Personaje::class.java) {
                perso = pre as Personaje?
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
                perso.refrescarStuff(true, true, true)
                if ((pre as Personaje?).getGrupoParty() != null) {
                    if ((pre as Personaje?).getGrupoParty().esLiderGrupo(pre as Personaje?)) {
                        for (idol in (pre as Personaje?).getIdolos().values()) {
                            if (!idolos!!.containsKey(idol.get(0).getEjecucion())) {
                                idolos.put(idol.get(0).getEjecucion(), idol)
                            } else {
                                idolos!![idol.get(0).getEjecucion()].addAll(idol)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
        }
        try {
            if (_mobGrupo != null) {
                if (pre.getClass() === MobGrado::class.java) {
                    if ((pre as MobGrado?).getIDModelo() === _mobGrupo.getJefe()) {
                        jefe = _luchInit2
                    }
                }
            }
        } catch (e: Exception) {
            // TODO: handle exception
        }
        pre.setPelea(this)
        val luchadorAUnirse = Luchador(this, pre, false)
        if (perso != null) {
            perso.setPrePelea(null, 0)
            if (perso.esMultiman()) {
                luchadorAUnirse.setIDReal(false)
            } else {
                GestorSalida.ENVIAR_ILF_CANTIDAD_DE_VIDA(perso, 0)
                GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(perso.getMapa(), perso.getID())
                perso.setCelda(null)
            }
            if (_tipo == Constantes.PELEA_TIPO_PVP || _tipo == Constantes.PELEA_TIPO_PRISMA) {
                perso.botonActDesacAlas('+')
            }
        }
        if (_equipo1.containsKey(idInitAUnir)) {
            luchadorAUnirse.setEquipoBin(0.toByte())
            _equipo1.put(pre.getID(), luchadorAUnirse)
        } else if (_equipo2.containsKey(idInitAUnir)) {
            luchadorAUnirse.setEquipoBin(1.toByte())
            _equipo2.put(pre.getID(), luchadorAUnirse)
        }
        /*
		 * if (enTorneo) { if (_equipo1.size() >= 1 && _equipo2.size() >= 1) {
		 * GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
		 * variables.zotros.Idiomas.getTexto(perso.getCuenta().idioma, 158),
		 * Colores.ROJO); return true; } } else { if (perso.enTorneo != 0) {
		 * GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
		 * "No puedes unirte a una pelea mientras estás inscrito en un Torneo",
		 * Colores.ROJO); return false; } }
		 */luchadorAUnirse.setCeldaPelea(celda)
        GestorSalida.ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(_mapaReal,
                (if (luchadorAUnirse.getEquipoBin() === 0) _luchInit1 else _luchInit2).getID(), luchadorAUnirse)
        seUnioAPelea(pre)
        if (_deshonor && perso != null) {
            if (perso.addDeshonor(1)) {
                GestorSalida.ENVIAR_Im_INFORMACION(perso, "084;" + perso.getDeshonor())
            }
        }
        if (luchadorAUnirse.getPersonaje() != null) {
            cargarMultiman(luchadorAUnirse.getPersonaje())
        }
        return true
    }

    fun seUnioAPelea(pre: PreLuchador?) {
        var perso: Personaje? = null
        try {
            if (pre.getClass() === Personaje::class.java) {
                perso = pre as Personaje?
            }
        } catch (e: Exception) {
        }
        if (perso != null && !perso.esMultiman()) {
            if (_tipo == Constantes.PELEA_TIPO_DESAFIO || _fase == Constantes.PELEA_FASE_COMBATE) {
                GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(perso, _fase, true, true, false, 0, _tipo)
            } else {
                GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(perso, _fase, false, true, false,
                        Math.max(tiempoFaltInicioPelea - 1500, 0), _tipo)
            }
        }
        when (_fase) {
            Constantes.PELEA_FASE_POSICION -> {
                if (perso != null && !perso.esMultiman()) {
                    if (_equipo1.containsKey(perso.getID())) {
                        GestorSalida.ENVIAR_GP_POSICIONES_PELEA(perso, _mapaCopia.strCeldasPelea(), _byteColor1)
                    } else if (_equipo2.containsKey(perso.getID())) {
                        GestorSalida.ENVIAR_GP_POSICIONES_PELEA(perso, _mapaCopia.strCeldasPelea(), _byteColor2)
                    }
                    GestorSalida.ENVIAR_GM_LUCHADORES_A_PERSO(this, _mapaCopia, perso)
                }
                _luchInit2.getPreLuchador().actualizarAtacantesDefensores()
            }
            Constantes.PELEA_FASE_COMBATE -> if (perso != null && !perso.esMultiman()) {
                GestorSalida.ENVIAR_GM_LUCHADORES_A_PERSO(this, _mapaCopia, perso)
                GestorSalida.ENVIAR_GS_EMPEZAR_COMBATE(perso)
                try {
                    Thread.sleep(500)
                } catch (e: Exception) {
                }
                GestorSalida.ENVIAR_GTL_ORDEN_JUGADORES(perso, this)
                GestorSalida.ENVIAR_GTM_INFO_STATS_TODO_LUCHADORES_A_PERSO(perso, this)
            }
            else -> return
        }
        GestorSalida.ENVIAR_GM_JUGADOR_UNIRSE_PELEA(this, 7, getLuchadorPorID(pre.getID()))
    }

    private fun enviarRetosPersonaje(perso: Personaje?) {
        if (_retos == null) {
            return
        }
        if (_tipo == Constantes.PELEA_TIPO_PVM || _tipo == Constantes.PELEA_TIPO_PVM_NO_ESPADA) {
            for (reto in _retos.values()) {
                GestorSalida.ENVIAR_Gd_RETO_A_PERSONAJE(perso, reto.infoReto())
            }
        }
    }

    fun desconectarLuchador(perso: Personaje?) {
        try {
            if (perso == null) {
                return
            }
            val luchador: Luchador = getLuchadorPorID(perso.getID()) ?: return
            perso.primerRefresh = true
            luchador.setDesconectado(true)
            GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 7,
                    "1182;" + luchador.getNombre().toString() + "~" + luchador.getTurnosRestantes())
            if (!perso.esMultiman()) {
                for (compa in perso.getCompañeros()) {
                    desconectarLuchador(compa)
                }
            }
            if (luchador.puedeJugar()) {
                luchador.setTurnosRestantes((luchador.getTurnosRestantes() - 1) as Byte)
                pasarTurno(luchador)
            }
        } catch (e: Exception) {
            MainServidor.redactarLogServidorln("Exception desconectarLuchador " + e.toString())
            e.printStackTrace()
        }
    }

    /*
	 * public void unirseEspectador(final Personaje perso, final boolean siOsi ) {
	 * if (_fase != Constantes.PELEA_FASE_COMBATE) {
	 * GestorSalida.ENVIAR_Im_INFORMACION(perso, "157"); return; } if (!siOsi &&
	 * !perso.esIndetectable()) { if (perso.esFantasma() || perso.getPelea() !=
	 * null) { GestorSalida.ENVIAR_Im_INFORMACION(perso, "1116"); return; } if
	 * (_sinEspectador) { GestorSalida.ENVIAR_Im_INFORMACION(perso, "157"); return;
	 * } } perso.setPelea(this); Luchador espectador = new Luchador(this, perso,
	 * true); _espectadores.put(perso.getID(), espectador); if (!siOsi) {
	 * GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 7, "036;" +
	 * perso.getNombre()); } else { espectador.setEspectadorAdmin(true); }
	 * reconectandoMostrandoInfo(espectador, true); // actualizarNumTurnos(perso); }
	 */
    fun unirseEspectador(perso: Personaje?, siOsi: Boolean) {
        if (_luchadorDeTurno == null) {
            GestorSalida.ENVIAR_BN_NADA(perso)
            return
        }
        if (perso == null) {
            GestorSalida.ENVIAR_BN_NADA(perso)
            return
        }
        if (perso != null && perso.esFantasma()) {
            GestorSalida.ENVIAR_Im_INFORMACION(perso, "1116")
            return
        }
        if (_sinEspectador && perso.getCuenta().getAdmin() === 0 || _fase.toInt() != 3) {
            GestorSalida.ENVIAR_Im_INFORMACION(perso, "157")
            return
        }
        val tiempoRestante = (MainServidor.SEGUNDOS_TURNO_PELEA * 1000
                - (System.currentTimeMillis() - _tiempoTurno)) as Int
        perso.setPelea(this)
        _mapaReal.removerPersonaje(perso)
        GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(perso, _fase, false, false, true, 0, _tipo)
        GestorSalida.ENVIAR_GS_EMPEZAR_COMBATE(perso)
        GestorSalida.ENVIAR_GTL_ORDEN_JUGADORES(perso, this)
        GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(perso.getMapa(), perso.getID())
        GestorSalida.ENVIAR_GM_LUCHADORES_A_PERSO(this, _mapaCopia, perso)
        GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 7, "036;" + perso.getNombre())
        if (_tipo.toInt() == 4 || _tipo.toInt() == 3) {
            enviarRetosPersonaje(perso)
        }
        GestorSalida.ENVIAR_GTS_INICIO_TURNO_PELEA(perso, _luchadorDeTurno.getID(), tiempoRestante)
        val espectador = Luchador(this, perso, true)
        _espectadores.put(perso.getID(), espectador)
        if (!siOsi) {
            GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 7, "036;" + perso.getNombre())
        } else {
            espectador.setEspectadorAdmin(true)
        }
        GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(perso.getMapa(), perso.getID())
        mostrarBuffsDeTodosAPerso(perso, false)
    }

    fun reconectarLuchador(perso: Personaje?) {
        if (_fase.toInt() != 3 || perso == null) return
        if (_luchadorDeTurno == null) {
            retirarsePelea(perso.getID(), -1, false)
            return
        }
        val luc: Luchador? = getLuchadorPorID(perso.getID())
        if (luc == null || !luc.estaDesconectado()) {
            return
        }
        val tiempoRestante = (MainServidor.SEGUNDOS_TURNO_PELEA * 1000
                - (System.currentTimeMillis() - _tiempoTurno)) as Int
        _mapaReal.removerPersonaje(perso)
        GestorSalida.ENVIAR_GP_POSICIONES_PELEA(perso, _mapaCopia.strCeldasPelea(), luc.getEquipoBin())
        GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(perso, _fase, false, true, false, 0, _tipo)
        GestorSalida.ENVIAR_GM_LUCHADORES_A_PERSO(this, _mapaCopia, perso)
        GestorSalida.ENVIAR_GS_EMPEZAR_COMBATE(perso)
        if (getTipoPelea() == Constantes.PELEA_TIPO_PVM) {
            if (!idolos!!.isEmpty()) {
                val idolosTest = StringBuilder()
                for (idoloss in idolos!!.values()) {
                    for (idolo in idoloss!!) {
                        if (idolosTest.length() > 0) {
                            idolosTest.append(",")
                        }
                        idolosTest.append(idolo.getIdolo())
                    }
                }
                GestorSalida.ENVIAR_wA_IDOLOS(perso, idolosTest.toString())
            }
        }
        GestorSalida.ENVIAR_GTS_INICIO_TURNO_PELEA(perso, _luchadorDeTurno.getID(), tiempoRestante)
        GestorSalida.ENVIAR_GTL_ORDEN_JUGADORES(perso, this)
        GestorSalida.ENVIAR_GTM_INFO_STATS_TODO_LUCHADORES_A_PERSO(perso, this)
        try {
            Thread.sleep(200L)
        } catch (e: Exception) {
        }
        if (_tipo.toInt() == 4 || _tipo.toInt() == 3) {
            enviarRetosPersonaje(perso)
        }
        try {
            luc._desconectado = false
            if (!perso.esMultiman()) {
                for (compa in perso.getCompañeros()) {
                    GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 7, "1184;" + compa.getNombre())
                    try {
                        getLuchadorPorID(compa.getID()).setDesconectado(false)
                    } catch (e: Exception) {
                    }
                }
            }
            GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 7, "1184;" + perso.getNombre())
        } catch (e: Exception) {
        }
        mostrarBuffsDeTodosAPerso(perso, true)
    }

    var UshGalesh = false
    var UshGaleshSegundo = false

    /*
	 * public void reconectarLuchador(final Personaje perso) { if (perso == null) {
	 * return; } final Luchador luchador = getLuchadorPorID(perso.getID()); if
	 * (luchador == null || !luchador.estaDesconectado()) { return; }
	 * perso.mostrarGrupo(); reconectandoMostrandoInfo(luchador, false); }
	 */
    /*
	 * private void reconectandoMostrandoInfo(Luchador luchador, boolean espectador)
	 * { float linea = 1; try { Personaje perso = luchador.getPersonaje(); linea =
	 * 2; perso.setCelda(null); if (espectador) {
	 * GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(perso.getMapa(), perso.getID()); }
	 * linea = 3; try { Thread.sleep(500); } catch (InterruptedException e1) { }
	 * linea = 4; if (_tipo == Constantes.PELEA_TIPO_DESAFIO || _fase ==
	 * Constantes.PELEA_FASE_COMBATE) { GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(perso,
	 * _fase, true, !espectador, espectador, 0, _tipo); } else {
	 * GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(perso, _fase, false, !espectador,
	 * espectador, Math.max(getTiempoFaltInicioPelea() - 1500, 0), _tipo); } linea =
	 * 5; if (!espectador) { GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 7,
	 * "1184;" + perso.getNombre()); luchador.setDesconectado(false); if
	 * (!perso.esMultiman()) { for (Personaje compa : perso.getCompañeros()) {
	 * GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 7, "1184;" +
	 * compa.getNombre()); try {
	 * getLuchadorPorID(compa.getID()).setDesconectado(false); } catch (Exception e)
	 * { } } } } linea = 6; switch (_fase) { case Constantes.PELEA_FASE_POSICION:
	 * linea = 7; if (perso != null) { if (_equipo1.containsKey(perso.getID())) {
	 * GestorSalida.ENVIAR_GP_POSICIONES_PELEA(perso, _mapaCopia.strCeldasPelea(),
	 * _byteColor1); } else if (_equipo2.containsKey(perso.getID())) {
	 * GestorSalida.ENVIAR_GP_POSICIONES_PELEA(perso, _mapaCopia.strCeldasPelea(),
	 * _byteColor2); } GestorSalida.ENVIAR_GM_LUCHADORES_A_PERSO(this, _mapaCopia,
	 * perso); } linea = 8; break; case Constantes.PELEA_FASE_COMBATE: linea = 9;
	 * final int tiempoRestante = (int) ((MainServidor.SEGUNDOS_TURNO_PELEA * 1000)
	 * - (System.currentTimeMillis() - _tiempoTurno)); linea = 10;
	 * GestorSalida.ENVIAR_GM_LUCHADORES_A_PERSO(this, _mapaCopia, perso);
	 * GestorSalida.ENVIAR_GS_EMPEZAR_COMBATE(perso); if (this.getTipoPelea() ==
	 * Constantes.PELEA_TIPO_PVM) { if (!idolos.isEmpty()) { StringBuilder
	 * idolosTest = new StringBuilder(); for (List<Idolo> idoloss : idolos.values())
	 * { for (Idolo idolo : idoloss) { if (idolosTest.length() > 0) {
	 * idolosTest.append(","); } idolosTest.append(idolo.getIdolo()); } }
	 * GestorSalida.ENVIAR_wA_IDOLOS(perso, idolosTest.toString()); } } linea = 11;
	 * try { Thread.sleep(500); } catch (InterruptedException e) { MainServidor.
	 * redactarLogServidorln("Exception mostrarTodaInfo Error en la linea: " +
	 * linea); } linea = 18; try { GestorSalida.ENVIAR_GTL_ORDEN_JUGADORES(perso,
	 * this); } catch (Exception e) { MainServidor.
	 * redactarLogServidorln("Exception mostrarTodaInfo Error en la linea: " +
	 * linea); } linea = 19; try {
	 * GestorSalida.ENVIAR_GTM_INFO_STATS_TODO_LUCHADORES_A_PERSO(perso, this); }
	 * catch (Exception e) { MainServidor.
	 * redactarLogServidorln("Exception mostrarTodaInfo Error en la linea: " +
	 * linea); } linea = 12; try { Thread.sleep(500); } catch (InterruptedException
	 * e) { } if (_luchadorDeTurno != null) {
	 * GestorSalida.ENVIAR_GTS_INICIO_TURNO_PELEA(perso, _luchadorDeTurno.getID(),
	 * tiempoRestante); } linea = 13; enviarRetosPersonaje(perso); linea = 14;
	 * if(espectador) { mostrarBuffsDeTodosAPerso(perso, false); } linea = 15; new
	 * Thread(new Runnable() {
	 *
	 * @Override public void run() { try { Thread.sleep(700l); for (HechizoLanzado
	 * hechizo : luchador.getHechizosLanzados()) {
	 * GestorSalida.ENVIAR_HECHIZO_LANZADO(perso, hechizo.getHechizoID(),
	 * hechizo.getSigLanzamiento(), hechizo.getObjetivo()); } if
	 * (luchador.tieneBuff(149)) { Buff buff = luchador.getBuff(149);
	 * GestorSalida.ENVIAR_GA_ACCION_PELEA(luchador.getPelea(), 7, 149,
	 * luchador.getID() + "", luchador.getID() + "," + luchador.getGfxID() + "," +
	 * buff.getPrimerValor() + "," + buff.getTurnosRestantes(false)); } } catch
	 * (InterruptedException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }
	 *
	 * } }).start();
	 *
	 * linea = 16; mostrarGlifos(luchador); linea = 17; mostrarTrampas(luchador); }
	 * linea = 17; } catch (Exception e) {
	 * MainServidor.redactarLogServidorln("Exception mostrarTodaInfo " +
	 * e.toString()); e.printStackTrace();
	 * MainServidor.redactarLogServidorln("Error en la linea: " + linea); } }
	 */
    fun mostrarGlifos(luchador: Luchador?) {
        if (_glifos != null) {
            for (glifo in _glifos) {
                GestorSalida.ENVIAR_GDZ_COLOREAR_ZONA_A_LUCHADOR(luchador, "+", glifo.getCelda().getID(),
                        glifo.getTamaño(), glifo.getColor(), glifo.getForma())
            }
        }
    }

    fun mostrarTrampas(luchador: Luchador?) {
        if (_trampas != null) {
            for (trampa in _trampas!!) {
                if (!trampa.esInvisiblePara(luchador.getID())) {
                    GestorSalida.ENVIAR_GDZ_COLOREAR_ZONA_A_LUCHADOR(luchador, "+", trampa.getCelda().getID(),
                            trampa.getTamaño(), trampa.getColor(), ' ')
                    val permisos = BooleanArray(16)
                    val valores = IntArray(16)
                    permisos[2] = true
                    permisos[0] = true
                    valores[2] = 25
                    valores[0] = 1
                    GestorSalida.ENVIAR_GDC_ACTUALIZAR_CELDA_A_LUCHADOR(luchador, trampa.getCelda().getID(),
                            Encriptador.stringParaGDC(permisos, valores), false)
                }
            }
        }
    }

    /*
	 * public CopyOnWriteArrayList<Trampa> getTrampas() { return _trampas; }
	 */
    fun getTrampas(): List<Trampa?>? {
        val sinRep: List<Trampa?> = ArrayList<Trampa?>()
        val conRep: List<Trampa?> = ArrayList<Trampa?>()
        val trampas: List<Trampa?> = ArrayList<Trampa?>()
        trampas.addAll(_trampas)
        for (tr in trampas) {
            if (tr == null) continue
            var repulsiva = false
            if (tr._trampaSH.efectosID.contains(5)) repulsiva = true
            if (repulsiva) {
                conRep.add(tr)
            } else {
                sinRep.add(tr)
            }
        }
        sinRep.addAll(conRep)
        _trampas.clear()
        _trampas.addAll(sinRep)
        return _trampas
    }

    fun addTrampa(trampa: Trampa?) {
        if (_trampas == null) {
            _trampas = CopyOnWriteArrayList()
        }
        if (!_trampas!!.contains(trampa)) _trampas.add(trampa)
    }

    fun borrarTrampa(trampa: Trampa?) {
        if (_trampas == null) {
            return
        }
        _trampas.remove(trampa)
    }

    fun addGlifo(glifo: Glifo?) {
        if (_glifos == null) {
            _glifos = CopyOnWriteArrayList()
        }
        if (!_glifos.contains(glifo)) _glifos.add(glifo)
    }

    fun borrarGlifo(glifo: Glifo?) {
        if (_glifos == null) {
            return
        }
        _glifos.remove(glifo)
    }

    private fun getCeldaRandom(celdas: List<Celda?>?): Celda? {
        if (celdas!!.isEmpty()) {
            return null
        }
        val celdas2: ArrayList<Celda?> = ArrayList<Celda?>()
        for (c in celdas) {
            if (c == null || c.getPrimerLuchador() != null) {
                continue
            }
            celdas2.add(c)
        }
        return if (celdas2.isEmpty()) {
            null
        } else celdas2.get(Formulas.getRandomInt(0, celdas2.size() - 1))
    }

    private fun analizarPosiciones(color: Byte, celdas: ArrayList<Celda?>?) {
        if (color.toInt() == 0) {
            for (s in _mapaCopia.getPosTeamRojo1()) {
                celdas.add(_mapaCopia.getCelda(s))
            }
        } else if (color.toInt() == 1) {
            for (s in _mapaCopia.getPosTeamAzul2()) {
                celdas.add(_mapaCopia.getCelda(s))
            }
        }
    }

    fun luchadoresDeEquipo(equipos: Int): ArrayList<Luchador?>? {
        var equipos = equipos
        return try {
            val luchadores: ArrayList<Luchador?> = ArrayList<Luchador?>()
            if (equipos - 4 >= 0) {
                luchadores.addAll(_espectadores.values())
                equipos -= 4
            }
            if (equipos - 2 >= 0) {
                luchadores.addAll(_equipo2.values())
                equipos -= 2
            }
            if (equipos - 1 >= 0) {
                luchadores.addAll(_equipo1.values())
            }
            luchadores
        } catch (e: Exception) {
            e.printStackTrace()
            luchadoresDeEquipo(equipos)
        }
    }

    fun cantLuchDeEquipo(equipos: Int): Int {
        var equipos = equipos
        return try {
            var luchadores = 0
            if (equipos - 4 >= 0) {
                luchadores += _espectadores.size()
                equipos -= 4
            }
            if (equipos - 2 >= 0) {
                luchadores += _equipo2.size()
                equipos -= 2
            }
            if (equipos - 1 >= 0) {
                luchadores += _equipo1.size()
            }
            luchadores
        } catch (e: Exception) {
            cantLuchDeEquipo(equipos)
        }
    }

    @Synchronized
    fun cambiarPosPerso(perso: Personaje?, idLuch: Int) {
        if (_fase != Constantes.PELEA_FASE_POSICION) {
            return
        }
        val dueño: Luchador? = getLuchadorPorID(perso.getID())
        if (dueño == null || dueño.estaListo()) {
            return
        }
        val luch2: Luchador? = getLuchadorPorID(idLuch)
        if (luch2 == null || luch2.getID() !== idLuch) {
            return
        }
        val cMultiman: Celda = luch2.getCeldaPelea()
        val cDueño: Celda = dueño.getCeldaPelea()
        cMultiman.limpiarLuchadores()
        cDueño.limpiarLuchadores()
        dueño.setCeldaPelea(cMultiman)
        luch2.setCeldaPelea(cDueño)
        GestorSalida.ENVIAR_GIC_CAMBIAR_POS_PELEA(this, 3, _mapaCopia, luch2.getID(), cDueño.getID())
        GestorSalida.ENVIAR_GIC_CAMBIAR_POS_PELEA(this, 3, _mapaCopia, dueño.getID(), cMultiman.getID())
    }

    @Synchronized
    fun cambiarPosMultiman(perso: Personaje?, idMultiman: Int) {
        if (_fase != Constantes.PELEA_FASE_POSICION) {
            return
        }
        val dueño: Luchador? = getLuchadorPorID(perso.getID())
        if (dueño == null || dueño.estaListo()) {
            return
        }
        if (perso.getCompañeros().isEmpty()) {
            return
        }
        var multiman: Luchador? = null
        for (multi in perso.getCompañeros()) {
            multiman = getLuchadorPorID(multi.getID())
            multiman = if (multiman != null && multiman.getID() === idMultiman) {
                break
            } else {
                null
            }
        }
        if (multiman == null) {
            return
        }
        val cMultiman: Celda = multiman.getCeldaPelea()
        val cDueño: Celda = dueño.getCeldaPelea()
        cMultiman.limpiarLuchadores()
        cDueño.limpiarLuchadores()
        dueño.setCeldaPelea(cMultiman)
        multiman.setCeldaPelea(cDueño)
        GestorSalida.ENVIAR_GIC_CAMBIAR_POS_PELEA(this, 3, _mapaCopia, multiman.getID(), cDueño.getID())
        GestorSalida.ENVIAR_GIC_CAMBIAR_POS_PELEA(this, 3, _mapaCopia, dueño.getID(), cMultiman.getID())
    }

    @Synchronized
    fun cambiarPosicion(id: Int, celda: Short) {
        if (_fase != Constantes.PELEA_FASE_POSICION) {
            return
        }
        if (_mapaCopia.getCelda(celda) == null) {
            return
        }
        val luchador: Luchador? = getLuchadorPorID(id)
        val equipo = getParamMiEquipo(id).toInt()
        if (luchador == null || _mapaCopia.getCelda(celda).getPrimerLuchador() != null || luchador.estaListo()
                || equipo == 1 && !grupoCeldasContiene(_celdasPos1, celda.toInt())
                || equipo == 2 && !grupoCeldasContiene(_celdasPos2, celda.toInt())) {
            return
        }
        luchador.getCeldaPelea().moverLuchadoresACelda(_mapaCopia.getCelda(celda))
        GestorSalida.ENVIAR_GIC_CAMBIAR_POS_PELEA(this, 3, _mapaCopia, id, celda)
    }

    private fun grupoCeldasContiene(celdas: ArrayList<Celda?>?, celda: Int): Boolean {
        for (c in celdas) {
            if (c.getID() === celda) {
                return true
            }
        }
        return false
    }

    fun verificaTodosListos() { // yo creo que el problema esta aqui por lo de "no iniciar combate hasta que
        // termine el contador"
        if (_tipo == Constantes.PELEA_TIPO_RECAUDADOR || _tipo == Constantes.PELEA_TIPO_PRISMA) {
            return
        }
        var listo = true
        for (luch in _equipo1.values()) {
            if (luch.getPersonaje() == null || luch.getPersonaje().esMultiman()) {
                continue
            }
            if (!luch.estaListo()) {
                listo = false
                break
            }
        }
        if (!listo) {
            return
        }
        for (luch in _equipo2.values()) {
            if (luch.getPersonaje() == null || luch.getPersonaje().esMultiman()) {
                continue
            }
            if (!luch.estaListo()) {
                listo = false
                break
            }
        }
        if (!listo) {
            return
        }
        iniciarPelea()
    }

    private fun antesIniciarPelea() {
        var linea = 0f
        try {
            linea = 1f
            _luchInit2.getPreLuchador().actualizarAtacantesDefensores()
            linea = 2f
            if (_tipo == Constantes.PELEA_TIPO_PVM_NO_ESPADA) {
                // vacio
            } else if (_tipo == Constantes.PELEA_TIPO_PVP) {
                linea = 3f
                // milicianos
                if (MainServidor.PARAM_PERMITIR_MILICIANOS_EN_PELEA
                        && _luchInit1.getAlineacion() !== Constantes.ALINEACION_NEUTRAL && _luchInit2.getAlineacion() === Constantes.ALINEACION_NEUTRAL && _alinPelea == Constantes.ALINEACION_NEUTRAL) {
                    linea = 4f
                    val str = StringBuilder()
                    for (l in _equipo1.values()) {
                        if (l.estaRetirado()) {
                            continue
                        }
                        if (str.length() > 0) {
                            str.append(";")
                        }
                        str.append(394.toString() + "," + Constantes.getNivelMiliciano(l.getNivel()) + ","
                                + Constantes.getNivelMiliciano(l.getNivel()))
                    }
                    linea = 5f
                    val gm = GrupoMob(_mapaReal, 1.toShort(), str.toString(), TipoGrupo.SOLO_UNA_PELEA, "", true, 0.toByte(), 0, "", 0)
                    for (mobG in gm.getMobs()) {
                        val mob: MobGrado = mobG.invocarMob(sigIDLuchadores(), false, null)
                        unirsePelea(mob, _idLuchInit2)
                    }
                    linea = 6f
                }
            } else if (_mobGrupo != null) {
                linea = 7f
                _mobGrupo.puedeTimerReaparecer(_mapaReal, _mobGrupo, Aparecer.INICIO_PELEA)
            }
            linea = 8f
            _inicioLuchEquipo1.addAll(luchadoresDeEquipo(1))
            _inicioLuchEquipo2.addAll(luchadoresDeEquipo(2))
            linea = 9f
            if (_tipo == Constantes.PELEA_TIPO_PVP) {
                for (luch in luchadoresDeEquipo(3)) {
                    val perso: Personaje = luch.getPersonaje()
                    if (perso == null || perso.esMultiman()) {
                        continue
                    }
                    if (Mundo.getRankingPVP(perso.getID()) == null) {
                        val rank = RankingPVP(perso.getID(), 0, 0, perso.getAlineacion())
                        Mundo.addRankingPVP(rank)
                    }
                }
                if (_inicioLuchEquipo1!!.size() === 1 && _inicioLuchEquipo2!!.size() === 1) {
                    val p1: Personaje = _luchInit1.getPersonaje()
                    val p2: Personaje = _luchInit2.getPersonaje()
                    if (p1 != null && p2 != null) {
                        p1.addAgredirA(p2.getNombre())
                        p2.addAgredidoPor(p1.getNombre())
                        _1vs1 = true
                    }
                }
            }
            linea = 10f
        } catch (e: Exception) {
            MainServidor.redactarLogServidorln("Exception antesIniciarPelea - Mapa: " + _mapaCopia.getID()
                    .toString() + " PeleaID: " + iD.toString() + ", Exception: " + e.toString())
            MainServidor.redactarLogServidorln("Error en la linea: $linea")
            e.printStackTrace()
        }
    }

    fun getIDLuchInit2(): Int {
        return if (_luchInit2 == null) {
            -1
        } else _luchInit2.getID()
    }

    private fun iniciarPelea() {
        GestorSalida.ENVIAR_Gc_BORRAR_ESPADA_EN_MAPA(_mapaReal, iD)
        try {
            Thread.sleep(200)
        } catch (e: InterruptedException) {
        }
        setEmpezoPelea(true)
        antesIniciarPelea()
        acaboPelea(3.toByte())
        if (_fase > Constantes.PELEA_FASE_POSICION) {
            return
        }
        _tiempoCombate = System.currentTimeMillis()
        _fase = Constantes.PELEA_FASE_COMBATE
        if (_luchInit1.getPersonaje() != null) {
            if (_luchInit1.getPersonaje().liderMaitre) {
                for (pjx in _luchInit1.getPersonaje().getGrupoParty().getPersos()) {
                    if (pjx === _luchInit1.getPersonaje()) {
                        _luchInit1.getPersonaje().liderMaitre = false
                        continue
                    }
                    pjx.esMaitre = false
                }
            }
        }
        GestorSalida.ENVIAR_fL_LISTA_PELEAS_AL_MAPA(_mapaReal)
        val gm = StringBuilder("GM")
        for (luchador in luchadoresDeEquipo(3)) {
            if (luchador.getID() < _ultimaInvoID) {
                _ultimaInvoID = luchador.getID()
            }
        }
        if (!MainServidor.PARAM_VER_JUGADORES_KOLISEO && _tipo == Constantes.PELEA_TIPO_KOLISEO) {
            val gm1 = StringBuilder("GM")
            val gm2 = StringBuilder("GM")
            for (l in _equipo1.values()) {
                if (l != null) gm1.append("|+" + l.stringGM(0))
            }
            for (l in _equipo2.values()) {
                if (l != null) gm2.append("|+" + l.stringGM(0))
            }
            for (l in _equipo1.values()) {
                if (l != null) GestorSalida.enviar(l.getPersonaje(), gm2.toString(), true)
            }
            for (l in _equipo2.values()) {
                if (l != null) GestorSalida.enviar(l.getPersonaje(), gm1.toString(), true)
            }
        } else if (_tipo != Constantes.PELEA_TIPO_KOLISEO) {
            if (gm.length() > 2) {
                for (l in _equipo1.values()) {
                    if (l != null) {
                        GestorSalida.enviar(l.getPersonaje(), gm.toString(), true)
                    }
                }
                for (l in _equipo2.values()) {
                    if (l != null) {
                        GestorSalida.enviar(l.getPersonaje(), gm.toString(), true)
                    }
                }
            }
        }
        try {
            Thread.sleep(500)
        } catch (e: Exception) {
        }
        GestorSalida.ENVIAR_GIC_UBICACION_LUCHADORES_INICIAR(this, 7)
        GestorSalida.ENVIAR_GS_EMPEZAR_COMBATE_EQUIPOS(this, 7)
        if (getTipoPelea() == Constantes.PELEA_TIPO_PVM) {
            if (!idolos!!.isEmpty()) {
                val idolosTest = StringBuilder()
                for (idoloss in idolos!!.values()) {
                    for (idolo in idoloss!!) {
                        if (_mobGrupo != null) {
                            if (!_mobGrupo.getIodoloJefe().isEmpty()) {
                                if (_mobGrupo.getIodoloJefe().contains(idolo.getIdolo().toString() + "")) {
                                    continue
                                }
                            }
                        }
                        if (idolosTest.length() > 0) {
                            idolosTest.append(",")
                        }
                        idolosTest.append(idolo.getIdolo())
                    }
                }
                GestorSalida.ENVIAR_wA_IDOLOS_COMBATE(this, 7, idolosTest.toString())
            }
        }
        iniciarOrdenLuchadores()
        try {
            Thread.sleep(500)
        } catch (e: Exception) {
        }
        GestorSalida.ENVIAR_GTL_ORDEN_JUGADORES(this, 7)
        GestorSalida.ENVIAR_GTM_INFO_STATS_TODO_LUCHADORES_A_TODOS(this, 7, false)
        var nivel = 0
        for (luchador in luchadoresDeEquipo(3)) {
            if (luchador == null) {
                continue
            }
            _posInicialLuch.put(luchador.getID(), luchador.getCeldaPelea())
            val perso: Personaje = luchador.getPersonaje() ?: continue
            if (perso.getNivel() > nivel) nivel = perso.getNivel()
            if (perso.estaMontando()) {
                GestorSalida.ENVIAR_GA950_ACCION_PELEA_ESTADOS(this, 3, perso.getID(), Constantes.ESTADO_CABALGANDO,
                        true)
            }
        }
        try {
            Thread.sleep(500)
        } catch (e: Exception) {
        }
        /*
		 * if (nivel > 5) { if (_tipo == Constantes.PELEA_TIPO_PVM || _tipo ==
		 * Constantes.PELEA_TIPO_PVM_NO_ESPADA) { _retos = new ConcurrentHashMap<Byte,
		 * Reto>(); final ArrayList<Byte> retosPosibles = new ArrayList<Byte>(); for
		 * (byte retoID = 1; retoID <= 50; retoID++) { switch (retoID) { case 13:// no
		 * tienen nada case 26: case 27: case 17: // intocable continue; default: if
		 * (Constantes.esRetoPosible1(retoID, this)) { retosPosibles.add(retoID); }
		 * break; } } byte retoID = retosPosibles.get(Formulas.getRandomInt(0,
		 * retosPosibles.size() - 1)); _retos.put(retoID, Constantes.getReto(retoID,
		 * this)); if (_mapaReal.esArena() || _mapaReal.esMazmorra()) { retoID =
		 * retosPosibles.get(Formulas.getRandomInt(0, retosPosibles.size() - 1));
		 * boolean repetir = true; while (repetir) { repetir = false; retoID =
		 * retosPosibles.get(Formulas.getRandomInt(0, retosPosibles.size() - 1)); for
		 * (Entry<Byte, Reto> entry : _retos.entrySet()) { if
		 * (Constantes.esRetoPosible2(entry.getKey(), retoID) && !repetir) { repetir =
		 * false; } else { repetir = true; } } } _retos.put(retoID,
		 * Constantes.getReto(retoID, this)); } ordenarRetos(); } / * for (Luchador
		 * luchador : luchadoresDeEquipo(3)) { if (luchador.getPersonaje() != null) { if
		 * (luchador.getPersonaje().getSubclase() == 3) { luchador.addBuffConGIE(112,
		 * 15, 100, 126, EfectoHechizo.convertirArgs(15, 112,
		 * "15,null,null,100,0,0d0+15"), luchador,true, TipoDaño.NORMAL, ""); } if
		 * (luchador.getPersonaje().getSubclase() == 2) { luchador.addBuffConGIE(178,
		 * 30, 100, 126, EfectoHechizo.convertirArgs(30, 178,
		 * "30,null,null,100,0,0d0+30"), luchador,true, TipoDaño.NORMAL, ""); } if
		 * (luchador.getPersonaje().getSubclase() == 1) { luchador.addBuffConGIE(125,
		 * 100, 100, 126, EfectoHechizo.convertirArgs(100, 125,
		 * "100,null,null,100,0,0d0+100"), luchador,true, TipoDaño.NORMAL, ""); } } }
		 */
        // }
        if (idolos!!.containsKey(Constantes.IDOLO_INICIO_PELEA)) {
            for (idolo in idolos!![Constantes.IDOLO_INICIO_PELEA]) {
                val trampa: Hechizo = Mundo.getHechizo(idolo.getHechizo())
                var EffectoId = 0
                var Duracion = 0
                var argumentos = ""
                var golpe = 0
                for (EH in trampa.getStatsPorNivel(idolo.getNivel()).getEfectosNormales()) {
                    EffectoId = EH.getEfectoID()
                    argumentos = EH.getArgs()
                    golpe = EH.getPrimerValor()
                    Duracion = EH.getDuracion()
                }
                for (luch in _equipo2.values()) {
                    if (luch == null) continue
                    luch.addBuffConGIE(EffectoId, golpe, Duracion, idolo.getHechizo(), EfectoHechizo.convertirArgs(golpe, EffectoId, argumentos), luch, true, TipoDaño.NORMAL, "")
                }
            }
        }
        iniciarTurno()
    }

    /*
	 * private void ordenarRetos() { if (_retos == null) { return; } for (final
	 * Entry<Byte, Reto> entry : _retos.entrySet()) { final Reto reto =
	 * entry.getValue(); final byte retoID = entry.getKey(); EstReto exitoReto =
	 * reto.getEstado(); if (exitoReto != Reto.EstReto.EN_ESPERA) { continue; } int
	 * nivel = 10000; switch (retoID) { case Constantes.RETO_LOS_PEQUEÑOS_ANTES://
	 * los pequeños antes for (final Luchador luch : _equipo1.values()) { if
	 * (luch.getNivel() < nivel) { _luchMenorNivelReto = luch.getID(); nivel =
	 * luch.getNivel(); } } break; case Constantes.RETO_ELEGIDO_VOLUNTARIO: case
	 * Constantes.RETO_APLAZAMIENTO: case Constantes.RETO_ELITISTA: case
	 * Constantes.RETO_ASESINO_A_SUELDO: Luchador mob = null; try {
	 * ArrayList<Luchador> equipo2 = new ArrayList<>(); for (Luchador luch :
	 * _equipo2.values()) { if (luch.estaMuerto() || luch.esInvocacion()) {
	 * continue; } equipo2.add(luch); } if (!equipo2.isEmpty()) { final int azar =
	 * Formulas.getRandomInt(0, equipo2.size() - 1); mob = equipo2.get(azar); } }
	 * catch (Exception e) { } if (mob != null) {
	 * GestorSalida.ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(this, 5, mob.getID(),
	 * mob.getCeldaPelea().getID()); reto.setMob(mob); } break; }
	 * GestorSalida.ENVIAR_Gd_RETO_A_LOS_LUCHADORES(this, reto.infoReto()); } }
	 */
    fun hechizoDisponible(luchador: Luchador?, idHechizo: Int): Boolean {
        var ver = false
        if (luchador.getPersonaje().tieneHechizoID(idHechizo)) {
            ver = true
            for (hl in luchador.getHechizosLanzados()) {
                if (hl.getHechizoID() === idHechizo && hl.getSigLanzamiento() > 0) {
                    ver = false
                }
            }
        }
        return ver
    }

    private fun iniciarOrdenLuchadores() {
        var cantLuch = 0
        val iniLuch: ArrayList<Duo<Integer?, Luchador?>?> = ArrayList<Duo<Integer?, Luchador?>?>()
        for (luch in luchadoresDeEquipo(3)) {
            luch.resetPuntos()
            iniLuch.add(
                    Duo<Integer?, Luchador?>(Formulas.getIniciativa(luch.getTotalStats(), luch.getPorcPDV()), luch))
            cantLuch++
        }
        var equipo1 = 0
        var equipo2 = 0
        var primero = 0
        var luchMaxIni: Luchador? = null
        var ultLuch: Luchador? = null
        while (_ordenLuchadores!!.size() < cantLuch) {
            var tempIni = 0
            for (entry in iniLuch) {
                if (_ordenLuchadores.contains(entry._segundo)) {
                    continue
                }
                if (primero == 0 || equipo1 == _equipo1.size() || equipo2 == _equipo2.size() || ultLuch.getEquipoBin() !== entry._segundo.getEquipoBin()) {
                    if (tempIni <= entry._primero) {
                        luchMaxIni = entry._segundo
                        tempIni = entry._primero
                    }
                }
            }
            ultLuch = luchMaxIni
            _ordenLuchadores.add(luchMaxIni)
            if (_equipo1.containsValue(luchMaxIni)) {
                equipo1++
            } else {
                equipo2++
            }
            primero++
        }
    }

    fun botonBloquearMasJug(id: Int) {
        if (_luchInit1 != null && _idLuchInit1 == id) {
            _cerrado1 = !_cerrado1
            GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA(_mapaReal, if (_cerrado1) '+' else '-', 'A', id)
            GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 1, if (_cerrado1) "095" else "096")
        } else if (_luchInit2 != null && _idLuchInit2 == id) {
            _cerrado2 = !_cerrado2
            GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA(_mapaReal, if (_cerrado2) '+' else '-', 'A', id)
            GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 2, if (_cerrado2) "095" else "096")
        }
    }

    fun botonSoloGrupo(idInit: Int) {
        var expulsadoID: Int
        var luch: Luchador
        if (_luchInit1 != null && _idLuchInit1 == idInit) {
            _soloGrupo1 = !_soloGrupo1
            if (_soloGrupo1) {
                val lista: ArrayList<Integer?> = ArrayList<Integer?>()
                val expulsar: ArrayList<Integer?> = ArrayList<Integer?>()
                try {
                    lista.addAll(_luchInit1.getPersonaje().getGrupoParty().getIDsPersos())
                } catch (e: Exception) {
                }
                for (entry in _equipo1.entrySet()) {
                    try {
                        luch = entry.getValue()
                        expulsadoID = entry.getKey()
                        if (!lista.contains(expulsadoID)) {
                            expulsar.add(expulsadoID)
                            GestorSalida.ENVIAR_GM_BORRAR_LUCHADOR(this, expulsadoID, 3)
                            luchadorSalirPelea(luch, 0)
                            GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(luch.getPersonaje())
                            luch.getPersonaje().retornoMapa()
                            luch.getCeldaPelea().removerLuchador(luch)
                            GestorSalida.ENVIAR_Gt_BORRAR_NOMBRE_ESPADA(_mapaReal, _idLuchInit1, luch)
                        }
                    } catch (e: Exception) {
                    }
                }
                for (ID in expulsar) {
                    _equipo1.remove(ID)
                }
            }
            GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA(_mapaReal, if (_soloGrupo1) '+' else '-', 'P', idInit)
            GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 1, if (_soloGrupo1) "093" else "094")
        } else if (_luchInit2 != null && _idLuchInit2 == idInit) {
            _soloGrupo2 = !_soloGrupo2
            if (_soloGrupo2) {
                val lista: ArrayList<Integer?> = ArrayList<Integer?>()
                val expulsar: ArrayList<Integer?> = ArrayList<Integer?>()
                try {
                    lista.addAll(_luchInit2.getPersonaje().getGrupoParty().getIDsPersos())
                } catch (e: Exception) {
                }
                for (entry in _equipo2.entrySet()) {
                    try {
                        luch = entry.getValue()
                        expulsadoID = entry.getKey()
                        if (!lista.contains(expulsadoID)) {
                            expulsar.add(expulsadoID)
                            GestorSalida.ENVIAR_GM_BORRAR_LUCHADOR(this, expulsadoID, 3)
                            luchadorSalirPelea(luch, 0)
                            GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(luch.getPersonaje())
                            luch.getPersonaje().retornoMapa()
                            luch.getCeldaPelea().removerLuchador(luch)
                            GestorSalida.ENVIAR_Gt_BORRAR_NOMBRE_ESPADA(_mapaReal, _idLuchInit2, luch)
                        }
                    } catch (e: Exception) {
                    }
                }
                for (ID in expulsar) {
                    _equipo2.remove(ID)
                }
            }
            GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA(_mapaReal, if (_soloGrupo2) '+' else '-', 'P', idInit)
            GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 2, if (_soloGrupo2) "095" else "096")
        }
    }

    fun botonBloquearEspect(id: Int) {
        if (_luchInit1 != null && _idLuchInit1 == id || _luchInit2 != null && _idLuchInit2 == id) {
            _sinEspectador = !_sinEspectador
            GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA(_mapaReal, if (_sinEspectador) '+' else '-', 'S', id)
            GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 7, if (_sinEspectador) "039" else "040")
        }
        if (_sinEspectador) {
            val espectadores: Map<Integer?, Luchador?> = TreeMap<Integer?, Luchador?>()
            espectadores.putAll(_espectadores)
            for (espectador in espectadores.values()) {
                try {
                    if (espectador.esEspectadorAdmin()) {
                        continue
                    }
                    _espectadores.remove(espectador.getID())
                    luchadorSalirPelea(espectador, 0)
                    GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(espectador.getPersonaje())
                    espectador.getPersonaje().retornoMapa()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun botonAyuda(id: Int) {
        if (_luchInit1 != null && _idLuchInit1 == id) {
            _ayuda1 = !_ayuda1
            GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA(_mapaReal, if (_ayuda1) '+' else '-', 'H', id)
            GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 1, if (_ayuda1) "0103" else "0104")
        } else if (_luchInit2 != null && _idLuchInit2 == id) {
            _ayuda2 = !_ayuda2
            GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA(_mapaReal, if (_ayuda2) '+' else '-', 'H', id)
            GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 2, if (_ayuda2) "0103" else "0104")
        }
    }

    fun getFase(): Byte {
        return _fase
    }

    fun getTipoPelea(): Int {
        return _tipo.toInt()
    }

    fun getOrdenLuchadores(): List<Luchador?>? {
        return _ordenLuchadores
    }

    fun finAccion(perso: Personaje?) {
        val luchador: Luchador? = getLuchadorPorID(perso.getID())
        finAccion(luchador)
    }

    private fun refrescarCeldas(luchador: Luchador?) {
        for (a in mapaCopia.getCeldas().values()) {
            if (a.getPrimerLuchador() != null) {
                if (a.getPrimerLuchador().esInvisible(0)) continue
                val permisos = BooleanArray(16)
                val valores = IntArray(16)
                permisos[11] = false
                valores[11] = 0
                GestorSalida.ENVIAR_GDC_ACTUALIZAR_CELDA_A_LUCHADOR(luchador, a.getID(),
                        Encriptador.stringParaGDC(permisos, valores), false)
            }
        }
    }

    private fun finAccion(luchador: Luchador?) {
        for (luch in luchadoresDeEquipo(7)) {
            if (luch.estaMuerto()) {
                pasarTurno(luch)
            }
        }
        if (_luchadorDeTurno.getPARestantes() < 2 && !_luchadorDeTurno.esNoIA()) {
            pasarTurno(_luchadorDeTurno)
        }
        refrescarCeldas(luchador)
        if (luchador == null || luchador.getPersonaje() == null || !luchador.puedeJugar()) {
            return
        }
        GestorSalida.ENVIAR_GM_LUCHADORES_A_PERSO(this, mapaCopia, luchador.getPersonaje())
        val perso: Personaje = luchador.getPersonaje()
        // try {
        // Thread.sleep(500);
        // } catch (Exception e) {}
        GestorSalida.ENVIAR_GAF_FINALIZAR_ACCION(perso, luchador.getID(), _idUnicaAccion)
        if (_idUnicaAccion != -1) {
            _idUnicaAccion = -1
            return
        }
        GestorSalida.ENVIAR_GAC_LIMPIAR_ACCION(perso)
        _tempAccion = ""
        // GestorSalida.ENVIAR_GAC_LIMPIAR_ACCION(perso);
        // if (!MainServidor.PARAM_JUGAR_RAPIDO)
        // GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso, "El tempAccion fue
        // limpiado, ahora puedes seguir jugando.");
    }

    fun esLuchInicioPelea(luch: Luchador?): Boolean {
        return _inicioLuchEquipo1!!.contains(luch) || _inicioLuchEquipo2!!.contains(luch)
    }

    // este metodo es recibido por el comando jugador .turno
    @Synchronized
    fun checkeaPasarTurno() {
        when (_fase) {
            Constantes.PELEA_FASE_COMBATE -> {
                if (_tiempoTurno <= 0) {
                    break
                }
                if (System.currentTimeMillis() - _tiempoTurno >= MainServidor.SEGUNDOS_TURNO_PELEA * 1000 - 5000) {
                    preFinTurno(null)
                }
            }
        }
    }

    @Synchronized
    fun pasarTurnoBoton(perso: Personaje?) {
        var luchador: Luchador? = getLuchadorPorID(perso.getID()) ?: return
        if (!luchador.puedeJugar()) {
            for (compa in perso.getCompañeros()) {
                val luchTemp: Luchador = getLuchadorPorID(compa.getID()) ?: continue
                if (luchTemp.puedeJugar()) {
                    luchador = luchTemp
                    break
                }
            }
            if (!luchador.puedeJugar()) {
                return
            }
        }
        if (!_tempAccion.isEmpty()) {
            if (!MainServidor.PARAM_JUGAR_RAPIDO) {
                GestorSalida.ENVIAR_Im_INFORMACION(perso, "1REALIZANDO_TEMP_ACCION;$_tempAccion")
            } else {
                // finAccion(luchador);
            }
            return
        }
        pasarTurno(luchador)
    }

    private fun eliminarTimer() {
        if (_timerPelea != null) {
            _timerPelea.stop()
        }
        _timerPelea = null
    }

    var esperaMove = false
    var esperahechizo = false
    var intentos = 0
    @Synchronized
    private fun iniciarTurno() {
        acaboPelea(3.toByte())
        if (_fase.toInt() == 4) {
            return
        }
        esperaMove = false
        esperahechizo = false
        startTimerInicioTurno()
        _ultimoTipoDañoReto = EfectoHechizo.TipoDaño.NULL
        _tiempoHechizo = 0
        _idUnicaAccion = -1
        _nroOrdenLuc = (_nroOrdenLuc + 1).toByte()
        _tempAccion = ""
        _cantUltAfec = 0
        _cantCAC = 0
        _luchadorDeTurno = getLuchadorOrden()
        if (_luchadorDeTurno === getOrdenLuchadores()) if (_luchadorDeTurno == null) {
            return
        }
        _luchadorDeTurno.setDistMinAtq(-1)
        if (idolos!!.containsKey(Constantes.IDOLO_INICIO_TURNO)) {
            if (_luchadorDeTurno.getEquipoBin() === 1) {
                for (idolo in idolos!![Constantes.IDOLO_INICIO_TURNO]) {
                    val trampa: Hechizo = Mundo.getHechizo(idolo.getHechizo())
                    var EffectoId = 0
                    var Duracion = 0
                    var argumentos = ""
                    var golpe = 0
                    for (EH in trampa.getStatsPorNivel(idolo.getNivel()).getEfectosNormales()) {
                        EffectoId = EH.getEfectoID()
                        argumentos = EH.getArgs()
                        golpe = EH.getPrimerValor()
                        Duracion = EH.getDuracion()
                    }
                    _luchadorDeTurno.addBuffConGIE(EffectoId, golpe, Duracion, idolo.getHechizo(), EfectoHechizo.convertirArgs(golpe, EffectoId, argumentos), _luchadorDeTurno, true, TipoDaño.NORMAL, "")
                }
            }
        }
        if (jefe != null) {
            if (_luchadorDeTurno.getID() === jefe.getID()) {
                if (idolos!!.containsKey(Constantes.IDOLO_INICIO_TURNO_SOLO_JEFE)) {
                    if (jefe.getEquipoBin() === 1) {
                        for (idolo in idolos!![Constantes.IDOLO_INICIO_TURNO_SOLO_JEFE]) {
                            val trampa: Hechizo = Mundo.getHechizo(idolo.getHechizo())
                            var EffectoId = 0
                            var Duracion = 0
                            var argumentos = ""
                            var golpe = 0
                            for (EH in trampa.getStatsPorNivel(idolo.getNivel()).getEfectosNormales()) {
                                EffectoId = EH.getEfectoID()
                                argumentos = EH.getArgs()
                                golpe = EH.getPrimerValor()
                                Duracion = EH.getDuracion()
                            }
                            _luchadorDeTurno.addBuffConGIE(EffectoId, golpe, Duracion, idolo.getHechizo(), EfectoHechizo.convertirArgs(golpe, EffectoId, argumentos), _luchadorDeTurno, true, TipoDaño.NORMAL, "")
                        }
                    }
                }
            }
            if (_luchadorDeTurno.getID() === jefe.getID()) {
                turnosJefe++
                if (turnosJefe == turnosLanzamientos) {
                    if (idolos!!.containsKey(Constantes.IDOLO_TURNO_O_MUERTOS) && numeroLanzamientos > 0) {
                        if (jefe.getEquipoBin() === 1) {
                            for (idolo in idolos!![Constantes.IDOLO_TURNO_O_MUERTOS]) {
                                val trampa: Hechizo = Mundo.getHechizo(idolo.getHechizo())
                                _luchadorDeTurno.setPuedeJugar(true)
                                try {
                                    intentarLanzarHechizo1(jefe, trampa.getStatsPorNivel(idolo.getNivel()),
                                            _luchadorDeTurno.getCeldaPelea(), true)
                                    numeroLanzamientos--
                                    turnosJefe = 0
                                    _luchadorDeTurno.setPuedeJugar(false)
                                    for (lucha in if (jefe.getEquipoBin() === 1) _equipo2.values() else _equipo1.values()) {
                                        if (lucha.getMob() != null) {
                                            if (lucha.getMob().getMobModelo().getID() === 40545) {
                                                lucha.setPuedeJugar(true)
                                                val trampa2: Hechizo = Mundo.getHechizo(7188)
                                                try {
                                                    intentarLanzarHechizo1(lucha, trampa2.getStatsPorNivel(6),
                                                            lucha.getCeldaPelea(), true)
                                                    lucha.setPuedeJugar(false)
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                            } else {
                                                continue
                                            }
                                        } else {
                                            continue
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                }
            }
        }
        _luchadorDeTurno.setPuedeJugar(true)
        if (_luchadorDeTurno.estaMuerto()) {
            preFinTurno(_luchadorDeTurno)
            return
        }
        if (_glifos != null) {
            for (glifo in _glifos) {
                if (_fase.toInt() == 4) {
                    return
                }
                if (glifo.getLanzador().getID() === _luchadorDeTurno.getID() && glifo.disminuirDuracion() === 0) {
                    glifo.desaparecer()
                }
            }
        }
        if (_luchadorDeTurno.getPersonaje() != null) {
            try {
                val perso: Personaje = _luchadorDeTurno.getPersonaje()
                var lider: Personaje? = null
                for (l in luchadoresDeEquipo(7)) {
                    if (l.getPersonaje() == null) {
                        continue
                    } else {
                        val lperso: Personaje = l.getPersonaje()
                        if (perso.getCuenta().getActualIP().equals(lperso.getCuenta().getActualIP()) || perso.getCuenta().getUltimaIP().equals(lperso.getCuenta().getActualIP())) {
                            if (lperso.EsliderIP && lperso.enLinea()) {
                                try {
                                    lider = lperso
                                    if (lider.getCuenta().getSocket().getPersonaje() !== perso) {
                                        lider.getCuenta().getSocket().setPersonaje(perso)
                                    }
                                } catch (e: Exception) {
                                }
                            }
                        }
                    }
                }
                if (lider != null) {
                    lider.Multi = perso
                    GestorSalida.ENVIAR_AI_CAMBIAR_ID(lider, _luchadorDeTurno.getID())
                    GestorSalida.ENVIAR_SL_LISTA_HECHIZOS_A_LIDER(perso, lider)
                    GestorSalida.ENVIAR_As_STATS_DEL_PJ_LIDER(perso, lider)
                    //GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso)
                    GestorSalida.ENVIAR_GAC_LIMPIAR_ACCION(perso)
                    //	                    getLuchadorPorID(lider.id)
                }
            } catch (e: Exception) {
            }
            if (_luchadorDeTurno.getPersonaje().getServidorSocket() != null) {
                _luchadorDeTurno.getPersonaje().getServidorSocket().limpiarAcciones(true)
            }
            /*
			 * Personaje compañero = this._luchadorDeTurno.getPersonaje().getCompañero(); if
			 * (compañero != null && !this._luchadorDeTurno.esIDReal()) {
			 * this._luchadorDeTurno.setIDReal(true);
			 * getLuchadorPorID(compañero.getID()).setIDReal(false);
			 * GestorSalida.ENVIAR_AI_CAMBIAR_ID(this._luchadorDeTurno.getPersonaje(),
			 * this._luchadorDeTurno.getID());
			 * GestorSalida.ENVIAR_SL_LISTA_HECHIZOS(this._luchadorDeTurno.getPersonaje());
			 * }else { if(!this._luchadorDeTurno.esIDReal()) {
			 * this._luchadorDeTurno.setIDReal(true); for(Personaje perso:
			 * _luchadorDeTurno.getPersonaje().invocacionControlable) {
			 * getLuchadorPorID(perso.getID()).setIDReal(false); }
			 * GestorSalida.ENVIAR_AI_CAMBIAR_ID(this._luchadorDeTurno.getPersonaje(),
			 * this._luchadorDeTurno.getID());
			 * GestorSalida.ENVIAR_SL_LISTA_HECHIZOS(this._luchadorDeTurno.getPersonaje());
			 * } }
			 */GestorSalida.ENVIAR_AI_CAMBIAR_ID(_luchadorDeTurno.getPersonaje(), _luchadorDeTurno.getID())
            GestorSalida.ENVIAR_SL_LISTA_HECHIZOS(_luchadorDeTurno.getPersonaje())
        }
        try {
            Thread.sleep(250L)
        } catch (exception: Exception) {
        }
        GestorSalida.ENVIAR_GTS_INICIO_TURNO_PELEA(this, 7, _luchadorDeTurno.getID(),
                MainServidor.SEGUNDOS_TURNO_PELEA * 1000)
        try {
            Thread.sleep(250L)
        } catch (exception: Exception) {
        }
        _luchadorDeTurno.aplicarBuffInicioTurno(this)
        _luchadorDeTurno.getBonusCastigo().clear()
        _luchadorDeTurno.actualizaHechizoLanzado()
        if (_luchadorDeTurno.estaMuerto()) {
            return
        }
        if (_luchadorDeTurno.getCeldaPelea().getGlifos() != null) {
            for (glifo in _luchadorDeTurno.getCeldaPelea().getGlifos()) {
                if (_fase.toInt() == 4) {
                    return
                }
                if (!glifo.esInicioTurno()) {
                    continue
                }
                glifo.activarGlifo(_luchadorDeTurno)
                try {
                    Thread.sleep(500L)
                } catch (exception: Exception) {
                }
            }
        }
        if (_luchadorDeTurno.getPDVConBuff() <= 0) {
            addMuertosReturnFinalizo(_luchadorDeTurno, null)
            return
        }
        if (_luchadorDeTurno.estaMuerto()) {
            return
        }
        if (_luchadorDeTurno.tieneBuff(140) || _luchadorDeTurno.getComandoPasarTurno()) {
            pasarTurno(_luchadorDeTurno)
            return
        }
        if (_luchadorDeTurno.estaDesconectado()) {
            _luchadorDeTurno.setTurnosRestantes(_luchadorDeTurno.getTurnosRestantes() - 1)
            if (_luchadorDeTurno.getTurnosRestantes() <= 0) {
                _luchadorDeTurno.setDesconectado(false)
                retirarsePelea(_luchadorDeTurno.getID(), -1, true)
            } else {
                GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 7,
                        "0162;" + _luchadorDeTurno.getNombre().toString() + "~" + _luchadorDeTurno.getTurnosRestantes())
                pasarTurno(_luchadorDeTurno)
            }
            return
        }
        if (_luchadorDeTurno.estaMuerto()) {
            pasarTurno(_luchadorDeTurno)
            return
        }
        if (MainServidor.MODO_DEBUG) {
            System.out.println("_tempLuchadorPA es " + _luchadorDeTurno.getPARestantes())
            System.out.println("_tempLuchadorPM es " + _luchadorDeTurno.getPMRestantes())
        }
        if (_luchadorDeTurno.getPersonaje() != null) {
            GestorSalida.ENVIAR_GAC_LIMPIAR_ACCION(_luchadorDeTurno.getPersonaje())
            GestorSalida.ENVIAR_As_STATS_DEL_PJ(_luchadorDeTurno.getPersonaje())
        }
        _tiempoTurno = System.currentTimeMillis()
        if ((_tipo.toInt() == 4 || _tipo.toInt() == 3) && _retos != null) {
            for (entry in _retos.entrySet()) {
                val reto: Reto = entry.getValue()
                val retoID: Byte = (entry.getKey() as Byte).byteValue()
                var exitoReto: Reto.EstReto = reto.getEstado()
                if (exitoReto !== Reto.EstReto.EN_ESPERA) {
                    continue
                }
                if (_luchadorDeTurno.esNoIA()) {
                    var mobsVivos: ArrayList<Luchador?>?
                    when (retoID) {
                        2 -> _luchadorDeTurno.setIDCeldaInicioTurno(_luchadorDeTurno.getCeldaPelea().getID())
                        6 -> _luchadorDeTurno.getHechizosLanzadosReto().clear()
                        34 -> {
                            mobsVivos = ArrayList()
                            for (luch in _inicioLuchEquipo2!!) {
                                if (luch.estaMuerto()) {
                                    continue
                                }
                                mobsVivos.add(luch)
                            }
                            if (!mobsVivos.isEmpty()) {
                                val mob: Luchador = mobsVivos.get(Formulas.getRandomInt(0, mobsVivos.size() - 1))
                                if (mob != null) {
                                    reto.setMob(mob)
                                    GestorSalida.ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(this, 5, mob.getID(),
                                            mob.getCeldaPelea().getID())
                                }
                            }
                        }
                        47 -> {
                            if (!_luchadorDeTurno.estaContaminado()) {
                                break
                            }
                            _luchadorDeTurno.addTurnosParaMorir()
                            if (_luchadorDeTurno.getTurnosParaMorir() <= 3) {
                                break
                            }
                            exitoReto = Reto.EstReto.FALLADO
                        }
                    }
                } else if (retoID.toInt() == 38 && _luchadorDeTurno.getEquipoBin() === 1 && reto.getLuchMob() != null && _luchadorDeTurno.getID() === reto.getLuchMob().getID()) {
                    exitoReto = Reto.EstReto.FALLADO
                }
                reto.setEstado(exitoReto)
            }
        }
        if (_luchadorDeTurno.getPersonaje() != null
                && _luchadorDeTurno.getPersonaje().get_Refrescarmobspelea()) {
            try {
                Thread.sleep(2000L)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            finAccion(_luchadorDeTurno)
        }
        /*
		 * // si esto te vota error usa esta if(_luchadorDeTurno.getPersonaje()!=null &&
		 * !_luchadorDeTurno.isInvocacion()){
		 * GestorSalida.ENVIAR_GM_LUCHADORES_A_PERSO2(getMapaCopia(), _luchadorDeTurno);
		 * }
		 */if (_luchadorDeTurno.getPersonaje() != null && !_luchadorDeTurno.getPersonaje().getCompañeros().isEmpty()) {
            GestorSalida.ENVIAR_GM_LUCHADORES_A_PERSO2(mapaCopia, _luchadorDeTurno)
        }
        try {
            if (_luchadorDeTurno.getIA() != null) {
                _luchadorDeTurno.getIA().arrancar()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun mostrarObjetivoReto(retoID: Byte, perso: Personaje?) {
        if (_retos == null) {
            return
        }
        for (reto in _retos.values()) {
            if (reto.getID() !== retoID || reto.getLuchMob() == null) {
                continue
            }
            GestorSalida.ENVIAR_Gf_MOSTRAR_CELDA(perso, reto.getLuchMob().getID(),
                    reto.getLuchMob().getCeldaPelea().getID())
        }
    }

    fun mostrarBuffsDeTodosAPerso(perso: Personaje?, esreconectado: Boolean) {
        for (luch in luchadoresDeEquipo(3)) {
            for (buff in luch.getBuffsPelea()) {
                if (!buff.getCondicionBuff().isEmpty()) {
                    continue
                }
                GestorSalida.ENVIAR_GA998_AGREGAR_BUFF(perso, getStrParaGA998(buff.getEfectoID(), luch.getID(),
                        buff.getTurnosRestantes(false), buff.getHechizoID(), buff.getArgs()))
            }
            for (estado in luch.getEstado().entrySet()) {
                GestorSalida.ENVIAR_GA950_ACCION_PELEA_ESTADOS(this, 7, luch.getID(), estado.getKey(),
                        estado.getValue() !== 0)
            }
        }
        if (esreconectado) {
            Thread(object : Runnable() {
                @Override
                fun run() {
                    val packet2 = StringBuilder()
                    val lux: Luchador? = getLuchadorPorID(perso.getID())
                    if (lux == null) {
                        Thread.interrupted()
                        return
                    }
                    val hecenv: ArrayList<Integer?> = ArrayList<Integer?>()
                    if (lux.getHechizosLanzados() != null) {
                        if (lux.getHechizosLanzados().size() > 0) {
                            for (hec in lux.getHechizosLanzados()) { // enviar GA;518;13;88,255,2,//
                                // hechizoid,celda,valorturno
                                if (hec.getSigLanzamiento() > 0) {
                                    hecenv.add(hec.getHechizoID())
                                    packet2.append("GA;" + 518 + ";" + lux.getID() + ";" + hec.getHechizoID() + ","
                                            + lux.getCeldaPelea().getID() + "," + hec.getSigLanzamiento() + ",")
                                            .append(0x00.toChar())
                                }
                            }
                        }
                        if (lux.tieneBuff(149)) {
                            val buff: Buff = lux.getBuff(149)
                            GestorSalida.ENVIAR_GA_ACCION_PELEA(lux.getPelea(), 7, 149, lux.getID().toString() + "",
                                    lux.getID().toString() + "," + lux.getGfxID() + "," + buff.getPrimerValor() + ","
                                            + buff.getTurnosRestantes(false))
                        }
                    }
                    if (lux._buffsPelea.size() > 0) {
                        for (luc in lux._buffsPelea) {
                            if (_fase.toInt() == 4) break
                            if (luc.getDuracion() > 0 && luc.getEfectoID() === 950 && !hecenv.contains(luc.getHechizoID())) {
                                packet2.append("GA;" + 518 + ";" + lux.getID() + ";" + luc.getHechizoID() + ","
                                        + lux.getCeldaPelea().getID() + "," + luc.getDuracion() + ",")
                                        .append(0x00.toChar())
                            }
                        }
                    }
                    if (lux._estados.size() > 0) {
                        for (luc in lux._estados.entrySet()) {
                            packet2.append("GA;" + 519 + ";" + lux.getID() + ";" + luc.getKey() + ",")
                                    .append(0x00.toChar())
                        }
                    }
                    if (!packet2.toString().isEmpty()) {
                        GestorSalida.enviar(perso, packet2.toString(), true)
                    }
                }
            }).start()
        }
    }

    fun refrescarBuffsPorMuerte(luchador: Luchador?) {
        try {
            for (luch in luchadoresDeEquipo(3)) {
                if (luch.getID() === luchador.getID()) {
                    continue
                }
                luch.deshechizar(luchador, false)
            }
        } catch (e: Exception) {
            // TODO: handle exception
            // System.out.println("Si ves esto no la pare bola: "+e.getMessage());
        }
    }

    fun cantLuchIniMuertos(equipo: Int): Int {
        var i = 0
        for (muerto in _listaMuertos!!) {
            if (equipo == 1) {
                if (_inicioLuchEquipo1!!.contains(muerto)) {
                    i++
                }
            } else if (equipo == 2) {
                if (_inicioLuchEquipo2!!.contains(muerto)) {
                    i++
                }
            }
        }
        return i
    }

    fun addMuertosReturnFinalizo(victima: Luchador?, asesino: Luchador?): Boolean {
        var linea = 0f
        try {
            var celdaBlancoHechizo: Celda? = null
            var celdaDeLanzador: Short = 0
            if (jefe != null) {
                if (victima.getEquipoBin() === jefe.getEquipoBin() && !victima.isRevivido()) {
                    celdaBlancoHechizo = victima.getCeldaPelea()
                    celdaDeLanzador = jefe.getCeldaPelea().getID()
                }
            }
            linea = 1f
            if (victima.estaMuerto() || _fase == Constantes.PELEA_FASE_FINALIZADO) {
                return false
            }
            linea = 2f
            victima.setEstaMuerto(true)
            val portador: Luchador = victima.getPortador()
            if (portador != null) {
                quitarTransportados(portador)
            }
            linea = 3f
            victima.setMuertoPor(asesino)
            victima.setPDV(1)
            victima.getCeldaPelea().removerLuchador(victima)
            linea = 4f
            val victimaID: Int = victima.getID()
            if (!victima.estaRetirado()) {
                if (!_listaMuertos!!.contains(victima)) {
                    _listaMuertos.add(victima)
                }
            }
            victima.getCeldaPelea().removerLuchador(victima)
            linea = 5f
            if (_luchadorDeTurno == null) {
                return false
            }
            victima.getCeldaPelea().removerLuchador(victima)
            linea = 6f
            for (l in luchadoresDeEquipo(3)) {
                if (l.getIA() != null) {
                    l.getIA().forzarRefrescarMov()
                    l.getIA().nullear()
                }
            }
            victima.getCeldaPelea().removerLuchador(victima)
            linea = 8f
            GestorSalida.ENVIAR_GA103_JUGADOR_MUERTO(this, 7, victimaID)
            linea = 9f
            _tiempoHechizo += EfectoHechizo.TIEMPO_POR_LUCHADOR_MUERTO
            if (victima.getTransportando() != null) {
                quitarTransportados(victima)
            } else if (victima.getPortador() != null) {
                quitarTransportados(victima.getPortador())
            }
            linea = 10f
            victima.getCeldaPelea().removerLuchador(victima)
            val team: TreeMap<Integer?, Luchador?> = TreeMap<Integer?, Luchador?>()
            var equipo = true
            if (victima.getEquipoBin() === 0) {
                team.putAll(_equipo1)
            } else if (victima.getEquipoBin() === 1) {
                equipo = false
                team.putAll(_equipo2)
            }
            linea = 11f
            for (luch in team.values()) {
                if (luch.estaMuerto() || luch.estaRetirado() || luch.getInvocador() == null || luch.getInvocador().getID() !== victimaID) {
                    continue
                }
                addMuertosReturnFinalizo(luch, asesino)
            }
            linea = 12f
            if (victima.esInvocacion() && !victima.esEstatico()) {
                victima.getInvocador().addNroInvocaciones(-1)
                if (!_ordenLuchadores!!.isEmpty()) {
                    val index = _ordenLuchadores.indexOf(victima)
                    if (index > -1) {
                        if (_nroOrdenLuc >= index && _nroOrdenLuc > 0) {
                            _nroOrdenLuc--
                        }
                        _ordenLuchadores.remove(victima)
                    }
                    if (_nroOrdenLuc < 0) {
                        return false
                    }
                    if (_equipo1.containsKey(victimaID)) {
                        _equipo1.remove(victimaID)
                    } else if (_equipo2.containsKey(victimaID)) {
                        _equipo2.remove(victimaID)
                    }
                    if (victima.esInvocacion()) {
                        victima.getCeldaPelea().removerLuchador(victima)
                    }
                    GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 999, victimaID.toString() + "", stringOrdenJugadores())
                    try {
                        Thread.sleep(100L)
                    } catch (e: Exception) {
                    }
                }
            }
            if (idolos!!.containsKey(Constantes.IDOLO_TURNO_O_MUERTOS) && numeroLanzamientos > 0) {
                if (victima.getEquipoBin() === 1) {
                    var cont = 0
                    for (lucha in if (equipo) _equipo1.values() else _equipo2.values()) {
                        if (lucha === jefe || lucha.getMob().getMobModelo().getID() === 40545 || lucha.esInvocacion() && !lucha.invocacionNoLatigo
                                || lucha.estaMuerto()) {
                            continue
                        }
                        cont++
                    }
                    if (cont == 0) {
                        for (idolo in idolos!![Constantes.IDOLO_TURNO_O_MUERTOS]) {
                            val trampa: Hechizo = Mundo.getHechizo(idolo.getHechizo())
                            jefe.setPuedeJugar(true)
                            try {
                                intentarLanzarHechizo1(jefe, trampa.getStatsPorNivel(idolo.getNivel()),
                                        jefe.getCeldaPelea(), true)
                                numeroLanzamientos--
                                turnosJefe = 0
                                jefe.setPuedeJugar(false)
                                for (lucha in if (equipo) _equipo1.values() else _equipo2.values()) {
                                    if (lucha.getMob() != null) {
                                        if (lucha.getMob().getMobModelo().getID() === 40545) {
                                            lucha.setPuedeJugar(true)
                                            val trampa2: Hechizo = Mundo.getHechizo(7188)
                                            try {
                                                intentarLanzarHechizo1(lucha, trampa2.getStatsPorNivel(6),
                                                        lucha.getCeldaPelea(), true)
                                                lucha.setPuedeJugar(false)
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        } else {
                                            continue
                                        }
                                    } else {
                                        continue
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
            linea = 13f
            if (_glifos != null) {
                for (glifo in _glifos) {
                    if (glifo.getLanzador().getID() === victimaID) {
                        glifo.desaparecer()
                    }
                }
            }
            linea = 14f
            val trampas: ArrayList<Trampa?> = ArrayList<Trampa?>()
            trampas.addAll(getTrampas())
            for (trampa in trampas) {
                if (trampa == null) continue
                if (trampa.getLanzador().getID() === victimaID) {
                    trampa.activarTrampa(null)
                }
            }
            linea = 15f
            if (victima != null) {
                refrescarBuffsPorMuerte(victima)
                if (idolos!!.containsKey(Constantes.IDOLO_ENEMIGO_MUERTO)) {
                    if (victima.getEquipoBin() === 1) {
                        for (idolo in idolos!![Constantes.IDOLO_ENEMIGO_MUERTO]) {
                            val trampa: Hechizo = Mundo.getHechizo(idolo.getHechizo())
                            try {
                                var EffectoId = 0
                                var Duracion = 0
                                var argumentos = ""
                                var golpe = 0
                                for (EH in trampa.getStatsPorNivel(idolo.getNivel()).getEfectosNormales()) {
                                    EffectoId = EH.getEfectoID()
                                    argumentos = EH.getArgs()
                                    golpe = EH.getPrimerValor()
                                    Duracion = EH.getDuracion()
                                }
                                for (luch in if (victima.getEquipoBin() === 1) _equipo2.values() else _equipo1.values()) {
                                    if (luch == null) continue
                                    luch.addBuffConGIE(EffectoId, golpe, Duracion, idolo.getHechizo(), EfectoHechizo.convertirArgs(golpe, EffectoId, argumentos), luch, true, TipoDaño.NORMAL, "")
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
            linea = 16f
            if (victima.getRecaudador() != null || victima.getPrisma() != null) {
                acaboPelea(2.toByte())
                return true
            } else if (cantLuchIniMuertos(1) == _inicioLuchEquipo1!!.size()
                    || cantLuchIniMuertos(2) == _inicioLuchEquipo2!!.size()) {
                acaboPelea(if (cantLuchIniMuertos(1) == _inicioLuchEquipo1.size()) 1.toByte() else 2.toByte())
                return true
            }
            linea = 17f
            if (celdaBlancoHechizo != null && celdaDeLanzador > 0) {
                if (_mobGrupo.gethechizoJefe() > 0) {
                    val trampa: Hechizo = Mundo.getHechizo(_mobGrupo.gethechizoJefe())
                    jefe.setPuedeJugar(true)
                    intentarLanzarHechizo1(jefe, trampa.getStatsPorNivel(6), celdaBlancoHechizo, true)
                    jefe.setPuedeJugar(false)
                }
            }
            try {
                Thread.sleep(100L)
            } catch (e: Exception) {
                // TODO: handle exception
            }
            comprobarPasarTurnoDespuesMuerte(victima)
            linea = 18f
            try {
                GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 999, victima.getID().toString() + "", stringOrdenJugadores())
            } catch (e: Exception) {
            }
            linea = 19f
        } catch (e: Exception) {
            MainServidor.redactarLogServidorln("Exception en addMuertosReturnFinalizo " + e.toString())
            e.printStackTrace()
            MainServidor.redactarLogServidorln("El error ocurrio en la linea: $linea")
        }
        return false
    }

    private fun comprobarPasarTurnoDespuesMuerte(victima: Luchador?) {
        if (victima.puedeJugar() && victima.estaMuerto()) {
            preFinTurno(victima)
        }
    }

    fun quitarTransportados(portador: Luchador?) {
        if (portador == null) {
            return
        }
        // if (nuevaCelda != null && reubicar != null){
        // reubicar.getCeldaPelea().removerLuchador(reubicar);
        // reubicar.setCeldaPelea(nuevaCelda);
        // }
        val transportado: Luchador = portador.getTransportando()
        portador.setEstado(Constantes.ESTADO_PORTADOR, 0)
        portador.setTransportando(null)
        if (transportado != null && !transportado.estaMuerto()) {
            transportado.setEstado(Constantes.ESTADO_TRANSPORTADO, 0)
            transportado.setTransportadoPor(null)
        }
        try {
            Thread.sleep(500)
        } catch (e: Exception) {
        }
    }

    fun setTempAccion(str: String?) {
        _tempAccion = str
        if (_tempAccion.equalsIgnoreCase("pasar")) {
            pasarTurno(null)
        }
    }

    fun getPMLuchadorTurno(): Int {
        return if (_luchadorDeTurno != null) {
            _luchadorDeTurno.getPMRestantes()
        } else {
            0
        }
    }

    // private void tiempoParaPasarTurno() {
    // if (_fase == Constantes.PELEA_FASE_COMBATE && System.currentTimeMillis() -
    // _ultQping > 500 &&
    // _cantTurnos > 1) {
    // _ultQping = System.currentTimeMillis();
    // checkeaPasarTurno(getLuchadorTurno());
    // }
    // }
    /*
	 * private void preFinTurno(final Luchador victima) { if (_vecesQuePasa >= 10) {
	 * _tempAccion = ""; } // System.out.println("victima preFinTurno " + (victima
	 * == null ? victima : // victima.getID())); if (!_tempAccion.isEmpty() ||
	 * (System.currentTimeMillis() < (_tiempoTurno + _tiempoHechizo))) { if
	 * (_rebootTurno == null) { _rebootTurno = new Timer(500, new ActionListener() {
	 * public void actionPerformed(final ActionEvent e) { _vecesQuePasa++;
	 * preFinTurno(victima); } }); } _rebootTurno.setRepeats(false);
	 * _rebootTurno.restart(); } else { pasarTurno(victima); } }
	 */
    fun preFinTurno(victima: Luchador?) {
        if (_vecesQuePasa >= 10) {
            _tempAccion = ""
        }
        if (!_tempAccion.isEmpty() || System.currentTimeMillis() < _tiempoTurno + _tiempoHechizo) {
            if (_rebootTurno == null) {
                _rebootTurno = Timer(20) { e ->
                    _vecesQuePasa = (_vecesQuePasa + 1).toByte()
                    preFinTurno(victima)
                }
            }
            _rebootTurno.setRepeats(false)
            _rebootTurno.restart()
        } else {
            pasarTurno(victima)
        }
    }

    @Synchronized
    fun pasarTurno(pasoTurno: Luchador?): String? {
        if (_fase.toInt() == 4) {
            return "Pelea finalizada"
        }
        if (pasoTurno != null) {
            if (_luchadorDeTurno.getID() !== pasoTurno.getID()) {
                return "No es mismo luchador de turno"
            }
        } else if (System.currentTimeMillis() - _tiempoTurno < MainServidor.SEGUNDOS_TURNO_PELEA * 1000 - 5000) {
            return "Evitar doble pasar turno"
        }
        if (_rebootTurno != null) {
            _rebootTurno.stop()
        }
        if (_luchadorDeTurno == null) {
            return "luchador null"
        }
        if (!_luchadorDeTurno.puedeJugar()) {
            return "El luchador no puede jugar"
        }
        if (!_luchadorDeTurno.estaMuerto()) {
            if (_luchadorDeTurno.esMultiman() && _luchadorDeTurno.getHechizos() != null) {
                for (sh in _luchadorDeTurno.getHechizos().values()) {
                    if (sh == null) {
                        continue
                    }
                    if (sh.esAutomaticoAlFinalTurno()) {
                        intentarLanzarHechizo1(_luchadorDeTurno, sh, _luchadorDeTurno.getCeldaPelea(), true)
                    }
                }
            }
        }
        intentos = 0
        _tempAccion = ""
        _vecesQuePasa = 0
        UshGalesh = false
        UshGaleshSegundo = false
        _tiempoTurno = System.currentTimeMillis()
        _luchadorDeTurno.setPuedeJugar(false)
        _luchadorDeTurno.setUltimoElementoDaño(-1)
        try {
            if (!_luchadorDeTurno.estaMuerto()) {
                GestorSalida.ENVIAR_GTF_FIN_DE_TURNO(this, 7, _luchadorDeTurno.getID())
                try {
                    Thread.sleep(250L)
                } catch (exception: Exception) {
                }
                val luchTurno: Luchador? = _luchadorDeTurno
                EfectoHechizo.buffFinTurno(luchTurno)
                if (!luchTurno.estaMuerto() && luchTurno.getCeldaPelea().getGlifos() != null) {
                    for (glifo in luchTurno.getCeldaPelea().getGlifos()) {
                        if (_fase.toInt() == 4) {
                            return "Se finalizó la pelea en glifos"
                        }
                        if (luchTurno.estaMuerto()) {
                            continue
                        }
                        if (glifo.esInicioTurno()) {
                            continue
                        }
                        glifo.activarGlifo(luchTurno)
                        if (luchTurno.getPDVConBuff() <= 0) {
                            addMuertosReturnFinalizo(luchTurno, glifo.getLanzador())
                        }
                    }
                }
                if (!luchTurno.estaMuerto()) {
                    luchTurno.disminuirBuffsPelea()
                }
                if (luchTurno.getPersonaje() != null && luchTurno.getPersonaje().enLinea()) {
                    GestorSalida.ENVIAR_As_STATS_DEL_PJ(luchTurno.getPersonaje())
                }
                luchTurno.resetPuntos()
                GestorSalida.ENVIAR_GTM_INFO_STATS_TODO_LUCHADORES_A_TODOS(this, 7, true)
                Thread.sleep(250L)
            }
            if (idolos!!.containsKey(Constantes.IDOLO_FIN_TURNO) && !_luchadorDeTurno.estaMuerto()) {
                if (_luchadorDeTurno.getEquipoBin() === 1) {
                    for (idolo in idolos!![Constantes.IDOLO_FIN_TURNO]) {
                        val trampa: Hechizo = Mundo.getHechizo(idolo.getHechizo())
                        val objetivos: ArrayList<Luchador?> = Hechizo.getObjetivosEfecto(mapaCopia,
                                _luchadorDeTurno,
                                trampa.getStatsPorNivel(idolo.getNivel()).getEfectosNormales().get(0),
                                _luchadorDeTurno.getCeldaPelea().getID())
                        if (objetivos.size() > 0) {
                            _luchadorDeTurno.setPuedeJugar(true)
                            try {
                                intentarLanzarHechizo1(_luchadorDeTurno, trampa.getStatsPorNivel(idolo.getNivel()),
                                        _luchadorDeTurno.getCeldaPelea(), true)
                                esperaMove = false
                                esperahechizo = false
                            } catch (e: Exception) {
                                e.printStackTrace()
                            } finally {
                                _luchadorDeTurno.setPuedeJugar(false)
                            }
                        }
                    }
                }
            }
            iniciarTurno()
        } catch (e: Exception) {
            MainServidor.redactarLogServidorln("Excepcion de fin de turno " + e.toString())
            e.printStackTrace()
            pasarTurno(null)
        }
        return "Return GOOD !!"
    }

    var tienePasar: Long = 0
    var timeActual: Long = 0
    fun intentarMoverse(movedor: Luchador?, path: String?, idUnica: Int,
                        AJ: AccionDeJuego?): String? {
        if (movedor == null || !movedor.puedeJugar()) {
            return "no"
        }
        _tacleado = false
        if (_luchadorDeTurno == null || !_tempAccion.isEmpty() || _fase != Constantes.PELEA_FASE_COMBATE || path.isEmpty()) {
            if (movedor.getPersonaje() != null) {
                if (path.isEmpty()) {
                    GestorSalida.ENVIAR_Im_INFORMACION(movedor.getPersonaje(), "1102")
                } else if (!_tempAccion.isEmpty()) {
                    if (MainServidor.PARAM_JUGAR_RAPIDO) {
                        finAccion(movedor)
                    } else {
                        GestorSalida.ENVIAR_Im_INFORMACION(movedor.getPersonaje(),
                                "1REALIZANDO_TEMP_ACCION;$_tempAccion")
                    }
                }
            }
            return "no"
        }
        if (_luchadorDeTurno == null || _luchadorDeTurno.getID() !== movedor.getID()) {
            if (movedor.getPersonaje() != null) {
                GestorSalida.ENVIAR_Im_INFORMACION(movedor.getPersonaje(), "1NO_ES_TU_TURNO")
            }
            return "no"
        }
        val persoM: Personaje = movedor.getPersonaje()
        if (movedor.tieneEstado(Constantes.ESTADO_ARRAIGADO) && !MainServidor.MODO_IMPACT || movedor.esInvisible(0)) {
        } else {
            // esto es para ser tacleado
            var porcHuida = 100
            var agiTac = 0
            var paso = false
            val tacleadores: ArrayList<Integer?> = ArrayList<Integer?>()
            for (i in 0..3) {
                val tacleador: Luchador = Camino.getEnemigoAlrededor(movedor.getCeldaPelea().getID(), _mapaCopia,
                        tacleadores, movedor.getEquipoBin())
                if (tacleador != null) {
                    if (!tacleador.esEstatico() && !tacleador.esInvisible(0)) {
                        tacleadores.add(tacleador.getID())
                        // no puede placar con estado arraigado
                        if (!tacleador.tieneEstado(Constantes.ESTADO_ARRAIGADO)) {
                            paso = true
                            if (MainServidor.PARAM_FORMULA_TIPO_OFICIAL) {
                                agiTac += (tacleador.getTotalStats()
                                        .getTotalStatParaMostrar(Constantes.STAT_MAS_AGILIDAD, this, null)
                                        + tacleador.getTotalStats()
                                        .getTotalStatParaMostrar(Constantes.STAT_MAS_PLACAJE, this, null) * 10)
                            } else {
                                porcHuida = EfectoHechizo.getPorcHuida(movedor, tacleador) * porcHuida / 100
                            }
                        }
                        if (MainServidor.MODO_IMPACT) {
                            if (tacleador.tieneEstado(Constantes.ESTADO_ARRAIGADO)) {
                                porcHuida = 0
                                paso = true
                            }
                            if (movedor.tieneEstado(Constantes.ESTADO_ARRAIGADO)) {
                                paso = false
                            }
                        }
                    }
                } else {
                    break
                }
            }
            if (paso) {
                if (MainServidor.PARAM_FORMULA_TIPO_OFICIAL) {
                    var agiMov: Int = (movedor.getTotalStats().getTotalStatParaMostrar(Constantes.STAT_MAS_AGILIDAD, this,
                            null)
                            + (movedor.getTotalStats().getTotalStatParaMostrar(Constantes.STAT_MAS_HUIDA, this, null)
                            * 10))
                    agiTac = Math.max(0, agiTac)
                    agiMov = Math.max(0, agiMov)
                    porcHuida = EfectoHechizo.getPorcHuida2(agiMov, agiTac)
                }
                val random: Int = Formulas.getRandomInt(1, 100)
                if (MainServidor.PARAM_MOSTRAR_PROBABILIDAD_TACLEO) {
                    GestorSalida.ENVIAR_cs_CHAT_MENSAJE_A_PELEA(this,
                            "% FUITE: <b>$porcHuida</b>, RANDOM: <b>$random</b>",
                            Constantes.COLOR_NARANJA)
                }
                if (random > porcHuida) {
                    GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 104, movedor.getID().toString() + ";", "")
                    // PA
                    var pierdePA: Int = Math.max(0, movedor.getPARestantes())
                    pierdePA = Math.rint(pierdePA * (100 - porcHuida) / 100f)
                    pierdePA = Math.abs(pierdePA)
                    // PM
                    var pierdePM: Int = Math.max(0, movedor.getPMRestantes())
                    if (!MainServidor.PARAM_FORMULA_TIPO_OFICIAL) {
                        pierdePM = Math.rint(pierdePM * (100 - porcHuida) / 100f)
                        pierdePM = Math.abs(pierdePM)
                        pierdePM = Math.max(1, pierdePM)
                    }
                    pierdePM = movedor.addPMRestantes(-pierdePM)
                    if (pierdePM != 0) {
                        GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 129, movedor.getID().toString() + "",
                                movedor.getID().toString() + "," + pierdePM)
                    }
                    pierdePA = movedor.addPARestantes(-pierdePA)
                    if (pierdePA != 0) {
                        GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 102, movedor.getID().toString() + "",
                                movedor.getID().toString() + "," + pierdePA)
                    }
                    _tacleado = true
                    return "tacleado"
                }
            }
        }
        if (persoM == null) esperaMove = true
        if (_luchadorDeTurno != null) if (_luchadorDeTurno.esDoble()) esperaMove = true
        var moverse = "ok"
        val pathRef: AtomicReference<String?> = AtomicReference<String?>(path)
        var ultimaCelda: Short = -1
        try {
            ultimaCelda = Encriptador.hashACeldaID(path.substring(path!!.length() - 2))
        } catch (e: Exception) {
        }
        var nroCeldasMov: Short = Camino.nroCeldasAMover(_mapaCopia, this, pathRef, movedor.getCeldaPelea().getID(),
                ultimaCelda, null)
        // System.out.println("celdas " + nroCeldasMov);
        if (nroCeldasMov >= 10000) {
            moverse = "stop"
            if (nroCeldasMov >= 20000) {
                // invisible
                GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 151, movedor.getID().toString() + "", "-1")
                GestorSalida.ENVIAR_GAF_FINALIZAR_ACCION(movedor.getPersonaje(), movedor.getID(), _idUnicaAccion)
                (nroCeldasMov -= 10000).toShort()
            }
            (nroCeldasMov -= 10000).toShort()
        }
        // System.out.println("nroCeldasMov " + nroCeldasMov);
        if (nroCeldasMov <= 0 || nroCeldasMov > movedor.getPMRestantes() || nroCeldasMov.toInt() == -1000) {
            GestorSalida.ENVIAR_GAF_FINALIZAR_ACCION(movedor.getPersonaje(), movedor.getID(), _idUnicaAccion)
            return "no"
        }
        if (AJ != null) {
            AJ.setCeldas(nroCeldasMov)
        }
        movedor.addPMRestantes(-nroCeldasMov)
        movedor.addPMUsados(nroCeldasMov)
        if ((_tipo == Constantes.PELEA_TIPO_PVM || _tipo == Constantes.PELEA_TIPO_PVM_NO_ESPADA) && _retos != null && movedor.getPersonaje() != null) {
            for (entry in _retos.entrySet()) {
                val reto: Reto = entry.getValue()
                val retoID: Byte = entry.getKey()
                var exitoReto: EstReto = reto.getEstado()
                if (exitoReto !== Reto.EstReto.EN_ESPERA) {
                    continue
                }
                when (retoID) {
                    Constantes.RETO_ZOMBI -> {
                        if (movedor.getPMUsados() === 1) {
                            break
                        }
                        exitoReto = Reto.EstReto.FALLADO
                    }
                }
                reto.setEstado(exitoReto)
            }
        }
        val nuevoPath: String = pathRef.get()
        val ultPathMov: String = nuevoPath.substring(nuevoPath.length() - 3)
        val sigCeldaID: Short = Encriptador.hashACeldaID(ultPathMov.substring(1))
        val nuevaCelda: Celda = _mapaCopia.getCelda(sigCeldaID)
        if (nuevaCelda == null) {
            if (movedor.getPersonaje() != null) {
                GestorSalida.ENVIAR_Im_INFORMACION(movedor.getPersonaje(), "1102")
                GestorSalida.ENVIAR_GAF_FINALIZAR_ACCION(movedor.getPersonaje(), movedor.getID(), _idUnicaAccion)
            }
            return "no"
        }
        movedor.setDireccion(ultPathMov.charAt(0))
        if (persoM != null) { // confirma el inicio de una accion
            GestorSalida.ENVIAR_GAS_INICIO_DE_ACCION(persoM, movedor.getID())
        }
        _idUnicaAccion = idUnica
        // confirma q se movio
        GestorSalida.ENVIAR_GA_ACCION_PELEA_MOVERSE(this, movedor, 7, _idUnicaAccion, 1, movedor.getID().toString() + "",
                "a" + Encriptador.celdaIDAHash(movedor.getCeldaPelea().getID()) + nuevoPath)
        val portador: Luchador = movedor.getPortador()
        if (portador != null && nuevaCelda !== portador.getCeldaPelea()) {
            movedor.getCeldaPelea().removerLuchador(movedor)
            movedor.setCeldaPelea(nuevaCelda)
            quitarTransportados(portador)
            if (movedor.getIA() != null) {
                movedor.getIA().nullear()
            }
        } else {
            movedor.getCeldaPelea().moverLuchadoresACelda(nuevaCelda)
        }
        // final Luchador transportado = movedor.getTransportando();
        // if (transportado != null) {
        // transportado.setCeldaPelea(movedor.getCeldaPelea());
        // }
        _ultimoMovedorIDReto = movedor.getID()
        _tempAccion = "Moverse"
        GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 129, movedor.getID().toString() + "",
                movedor.getID().toString() + "," + -nroCeldasMov)
        if (persoM == null) {
            var bucle = 0
            while (esperaMove && bucle < 15) {
                try {
                    Thread.sleep(400L)
                } catch (e: InterruptedException) {
                }
                bucle += 1
            }
            try {
                Thread.sleep(200L)
            } catch (e: InterruptedException) {
            }
            // GestorSalida.ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(this, 7, movedor.getID(),
            // movedor.getCeldaPelea().getID());
            _tempAccion = ""
            EfectoHechizo.verificaTrampas(movedor)
        } else {
            persoM.addGA(AJ)
            if (!persoM.getCompañeros().isEmpty()) {
                for (perso in persoM.getCompañeros()) {
                    perso.addGA(AJ)
                }
            }
        }
        return moverse
    }

    fun finalizarMovimiento(perso: Personaje?): Boolean {
        var perso: Personaje? = perso
        if (_tacleado) {
            GestorSalida.ENVIAR_BN_NADA(perso, "FIN MOVIMIENTO TACLEADO")
            return false
        }
        if (_luchadorDeTurno == null || _tempAccion.isEmpty() || _fase != Constantes.PELEA_FASE_COMBATE) {
            GestorSalida.ENVIAR_BN_NADA(perso, "FIN MOVIMIENTO OTROS")
            return false
        }
        var idLuch: Int = perso.getID()
        if (idLuch != _ultimoMovedorIDReto) {
            for (compa in perso.getCompañeros()) {
                if (compa.getID() === _ultimoMovedorIDReto) {
                    idLuch = compa.getID()
                    perso = compa
                    break
                }
            }
            if (idLuch != _ultimoMovedorIDReto) {
                return false
            }
        }
        val luchador: Luchador? = getLuchadorPorID(idLuch)
        if (luchador == null) {
            GestorSalida.ENVIAR_BN_NADA(perso, "FIN MOVIMIENTO LUCHADOR NULL")
            return false
        }
        // GestorSalida.ENVIAR_GA_PERDER_PM_PELEA(this, 7, _tempAccion);
        // eso puede ser opcional si borro el GAC
        _idUnicaAccion = -1
        _tempAccion = ""
        EfectoHechizo.verificaTrampas(luchador)
        GestorSalida.ENVIAR_GAF_FINALIZAR_ACCION(perso, idLuch, -1)
        GestorSalida.ENVIAR_GAs_PARAR_MOVIMIENTO_SPRITE(perso, idLuch)
        return true
    }

    fun intentarLanzarHechizo(lanzador: Luchador?, sHechizo: StatHechizo?, celdaID: Short): Int {
        if (_fase.toInt() != 3) return 0
        val perso: Personaje = lanzador.getPersonaje()
        if (!_tempAccion.isEmpty() && perso != null || sHechizo == null || !lanzador.puedeJugar()) if (perso != null) {
            GestorSalida.ENVIAR_Im_INFORMACION(perso, "1175")
            GestorSalida.ENVIAR_GAF_FINALIZAR_ACCION(perso, lanzador.getID(), -1)
            return 0
        }
        if (perso != null) {
            lanzador.penalizado = 0
            lanzador.realizaaccion = true
        }
        _tempAccion = "lanzando hechizo"
        val celda: Celda = _mapaCopia.getCelda(celdaID)
        val esFC = (sHechizo.getProbabilidadFC() !== 0
                && Formulas.getRandomValor(1, sHechizo.getProbabilidadFC()) === sHechizo.getProbabilidadFC())
        if (puedeLanzarHechizo(lanzador, sHechizo, celda, (-1.toShort()).toShort()) === EstadoLanzHechizo.PODER) {
            if (perso == null) esperahechizo = true
            val list: List<String?> = ArrayList<String?>()
            list.add("#0000FF")
            list.add("#FF4000")
            list.add("#8A0829")
            list.add("#0B610B")
            list.add("#8B1EDE")
            list.add("#61210B")
            val random = list[Random().nextInt(list.size())]
            GestorSalida.GAME_SEND_MESSAGE3(this, random)
            if (perso != null) GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso)
            if (lanzador.getTipo() === 1 && perso.getHechizosSetClase().containsKey(sHechizo.getHechizoID())) {
                val modi: Int = perso.getModifSetClase(sHechizo.getHechizoID(), 285)
                _luchadorDeTurno._PArestantes -= sHechizo.getCostePA() - modi
                _luchadorDeTurno._PAusados += sHechizo.getCostePA() - modi
            } else {
                _luchadorDeTurno._PArestantes -= sHechizo.getCostePA()
                _luchadorDeTurno._PAusados += sHechizo.getCostePA()
            }
            if (lanzador.esInvisible(0)) {
                GestorSalida.ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(this, 7, lanzador.getID(),
                        lanzador.getCeldaPelea().getID())
            }
            GestorSalida.ENVIAR_GAS_INICIO_DE_ACCION(perso, lanzador.getID())
            if (esFC) {
                GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 302, lanzador.getID().toString() + "", sHechizo.getHechizoID().toString() + "")
                GestorSalida.ENVIAR_GAF_FINALIZAR_ACCION(perso, lanzador.getID(), -1)
            } else {
                if ((_tipo == Constantes.PELEA_TIPO_PVM || _tipo == Constantes.PELEA_TIPO_PVM_NO_ESPADA)
                        && _retos != null && lanzador.esNoIA()) {
                    for (entry in _retos.entrySet()) {
                        val reto: Reto = entry.getValue()
                        val retoID: Byte = entry.getKey()
                        var exitoReto: EstReto = reto.getEstado()
                        if (exitoReto !== Reto.EstReto.EN_ESPERA) {
                            continue
                        }
                        when (retoID) {
                            Constantes.RETO_AHORRADOR -> if (lanzador.getHechizosLanzadosReto().contains(sHechizo.getHechizoID())) {
                                exitoReto = Reto.EstReto.FALLADO
                            } else {
                                lanzador.getHechizosLanzadosReto().add(sHechizo.getHechizoID())
                            }
                            Constantes.RETO_VERSATIL -> if (lanzador.getHechizosLanzadosReto().contains(sHechizo.getHechizoID())) {
                                exitoReto = Reto.EstReto.FALLADO
                            } else {
                                lanzador.getHechizosLanzadosReto().add(sHechizo.getHechizoID())
                            }
                            Constantes.RETO_LIMITADO -> {
                                val hechizoID: Int = sHechizo.getHechizoID()
                                if (lanzador.getIDHechizoLanzado() === -1) {
                                    lanzador.setIDHechizoLanzado(hechizoID)
                                } else if (lanzador.getIDHechizoLanzado() !== hechizoID) {
                                    exitoReto = Reto.EstReto.FALLADO
                                }
                            }
                        }
                        reto.setEstado(exitoReto)
                    }
                }
                var esGC: Boolean = lanzador.puedeGolpeCritico(sHechizo)
                val efectos: ArrayList<EfectoHechizo?> = if (esGC) sHechizo.getEfectosCriticos() else sHechizo.getEfectosNormales()
                val hechizoStr: String = (sHechizo.getHechizoID().toString() + "," + celdaID + "," + sHechizo.getSpriteID() + ","
                        + sHechizo.getGrado() + "," + sHechizo.getSpriteInfos())
                GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 300, lanzador.getID().toString() + "", hechizoStr)
                if (esGC) {
                    if (perso != null) GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 301, lanzador.getID().toString() + "", hechizoStr)
                }
                if (lanzador.esInvisible(0) && sHechizo.getHechizoID() === 446) lanzador.hacerseVisible()
                // if(lanzador.esInvisible())//TODO: METER ESTA VARIABLE
                // showCaseToAll(lanzador.getID(), lanzador.getCeldaPelea().getID());
                if (sHechizo.getHechizoID() === 59) esGC = false
                val dir: Char = Camino.getDirEntreDosCeldas(_mapaCopia, lanzador.getCeldaPelea().getID(), celda.getID())
                if (dir.code != 0) {
                    lanzador.setDireccion(dir)
                    GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 11, lanzador.getID().toString() + "",
                            lanzador.getID().toString() + "," + Camino.getIndexDireccion(dir))
                }
                Hechizo.aplicaHechizoAPeleaSinGTM(this, lanzador, celda, efectos, TipoDaño.NORMAL, esGC)
            }
            if (lanzador.getTipo() === 1 && perso.getHechizosSetClase().containsKey(sHechizo.getHechizoID())) {
                val modi: Int = perso.getModifSetClase(sHechizo.getHechizoID(), 285)
                GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 102, lanzador.getID().toString() + "",
                        lanzador.getID().toString() + ",-" + (sHechizo.getCostePA() - modi))
            } else {
                GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 102, lanzador.getID().toString() + "",
                        lanzador.getID().toString() + ",-" + sHechizo.getCostePA())
            }
            if (!esFC) {
                // if (lanzador.getMob() != null) {
                lanzador.addHechizoLanzado(lanzador, sHechizo, celda.getPrimerLuchador())
                // }
            }
            if (lanzador.getMob() != null || lanzador.getRecaudador() != null || lanzador.getPrisma() != null) {
                var bucle = 0
                while (esperahechizo && bucle < 15) {
                    try {
                        Thread.sleep(400L)
                        bucle += 1
                    } catch (e: Exception) {
                    }
                }
            }
            if (lanzador.getPersonaje() != null) {
                try {
                    Thread.sleep(400L)
                } catch (e: Exception) {
                }
            }
            when (sHechizo.getHechizoID()) {
                1676, 198, 542, 552, 578, 579, 605, 630, 701, 772, 838, 869, 699, 881, 896, 898, 899, 900, 901, 434, 67, 696 -> {
                    tienePasar = 2000
                    timeActual = System.currentTimeMillis()
                }
                99 -> {
                    tienePasar = 3000
                    timeActual = System.currentTimeMillis()
                }
                else -> {
                    tienePasar = 1300
                    timeActual = System.currentTimeMillis()
                }
            }
            if (esFC && sHechizo.esFinTurnoSiFC() || sHechizo.getHechizoID() === 1735) {
                _tempAccion = ""
                if (lanzador.getMob() != null || lanzador.esInvocacion()) {
                    preFinTurno(lanzador)
                } else {
                    preFinTurno(lanzador)
                }
                return 5
            }
            GestorSalida.ENVIAR_GAF_FINALIZAR_ACCION(perso, lanzador.getID(), 0)
            if (!acaboPelea(lanzador.getEquipoBin())) {
                _tempAccion = ""
                return 10
            }
        } else if (lanzador.getMob() != null || lanzador.esInvocacion()) {
            _tempAccion = ""
            return 0
        }
        if (lanzador.getPersonaje() != null) {
            if (lanzador.getTransportando() == null && lanzador.getTransportando() == null && sHechizo.getHechizoID() === 696) {
                GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 950, lanzador.getID().toString() + "",
                        lanzador.getID().toString() + "," + 3 + ",0")
                GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 950, lanzador.getID().toString() + "",
                        lanzador.getID().toString() + "," + 8 + ",0")
                lanzador.setEstado(3, 0)
                lanzador.setEstado(8, 0)
            }
            GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 102, lanzador.getID().toString() + "", lanzador.getID().toString() + ",-0") // annonce
            // le
            // coût
            // en PA
            if (lanzador.getPDVConBuff() <= 0) addMuertosReturnFinalizo(lanzador, lanzador)
        }
        _tempAccion = ""
        return if (esFC) 5 else 10
    }

    @Synchronized
    fun intentarLanzarHechizo1(lanzador: Luchador?, SH: StatHechizo?,
                               celdaObjetivo: Celda?, obligaLanzar: Boolean): EstadoLanzHechizo? {
        if (lanzador == null) {
            return EstadoLanzHechizo.NO_PODER
        }
        val perso: Personaje = lanzador.getPersonaje()
        if (!lanzador.puedeJugar()) {
            if (lanzador.esNoIA()) {
                GestorSalida.ENVIAR_Im_INFORMACION(perso, "1NO_ES_TU_TURNO")
                GestorSalida.ENVIAR_GAC_LIMPIAR_ACCION(perso)
            }
            if (MainServidor.MODO_DEBUG) {
                System.out.println("intentarLanzarHechizo() Hechizo: " + SH.getHechizo().getNombre().toString() + " ("
                        + SH.getHechizoID().toString() + ") Estado: NO PODER")
            }
            return EstadoLanzHechizo.NO_PODER
        }
        if (!_tempAccion.isEmpty() && perso != null || SH == null) {
            if (lanzador.esNoIA()) {
                if (!_tempAccion.isEmpty()) {
                    if (!MainServidor.PARAM_JUGAR_RAPIDO) {
                        GestorSalida.ENVIAR_Im_INFORMACION(perso, "1REALIZANDO_TEMP_ACCION;$_tempAccion")
                    } else {
                        finAccion(lanzador)
                        if (MainServidor.MODO_DEBUG) {
                            System.out.println("intentarLanzarHechizo() Hechizo: " + SH.getHechizo().getNombre().toString() + " ("
                                    + SH.getHechizoID().toString() + ") Estado: NO PODER")
                        }
                        return EstadoLanzHechizo.NO_PODER
                    }
                } else {
                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "1169")
                }
                GestorSalida.ENVIAR_GAC_LIMPIAR_ACCION(perso)
            }
            if (MainServidor.MODO_DEBUG) {
                System.out.println("intentarLanzarHechizo() Hechizo: " + SH.getHechizo().getNombre().toString() + " ("
                        + SH.getHechizoID().toString() + ") Estado: NO PODER")
            }
            return EstadoLanzHechizo.NO_PODER
        }
        if (perso != null) {
            if (MainServidor.HECHIZOS_CLASE_UNICOS2.containsKey(SH.getHechizoID())) {
                if (perso.getClaseID(true) !== MainServidor.HECHIZOS_CLASE_UNICOS2.get(SH.getHechizoID())) {
                    GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso,
                            "No tiene la clase permitida para lanzar este hechizo")
                    return EstadoLanzHechizo.NO_PODER
                }
            }
        }
        if (perso == null || !perso.getCompañeros().isEmpty()) {
            esperahechizo = true
        }
        _tempAccion = "lanzando hechizo"
        _tiempoHechizo = EfectoHechizo.TIEMPO_POR_LANZAR_HECHIZO
        var puede: EstadoLanzHechizo? = EstadoLanzHechizo.PODER
        if (obligaLanzar
                || puedeLanzarHechizo(lanzador, SH, celdaObjetivo, (-1.toShort()).toShort()).also { puede = it } === EstadoLanzHechizo.PODER) {
            var costePA: Byte = SH.getCostePA()
            if (perso != null && perso.tieneModfiSetClase(SH.getHechizoID())) {
                costePA -= perso.getModifSetClase(SH.getHechizoID(), 285)
                if (costePA < 0) {
                    costePA = 0
                }
            }
            val list: List<String?> = ArrayList<String?>()
            list.add("#0000FF")
            list.add("#FF4000")
            list.add("#8A0829")
            list.add("#0B610B")
            list.add("#8B1EDE")
            list.add("#61210B")
            val random = list[Random().nextInt(list.size())]
            GestorSalida.GAME_SEND_MESSAGE3(this, random)
            if (perso != null) {
                GestorSalida.ENVIAR_GAS_INICIO_DE_ACCION(perso, lanzador.getID()) // inicia la accion
            }
            lanzador.addPARestantes(-costePA)
            lanzador.addPAUsados(costePA)
            // try {
            // aun cuando el socket se pierde (desconexion del jugador) el thread continua
            // su curso hasta
            // terminar su metodo.
            // Thread.sleep(10000);
            // } catch (InterruptedException e) {
            // e.printStackTrace();
            // }
            val esFC: Boolean = lanzador.puedeFalloCritico(SH)
            var cantObjetivos = 0
            GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 102, lanzador.getID().toString() + "",
                    lanzador.getID().toString() + "," + -costePA)
            if (esFC) {
                GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 302, lanzador.getID().toString() + "", SH.getHechizoID().toString() + "")
            } else { // es golpe normal
                if ((_tipo == Constantes.PELEA_TIPO_PVM || _tipo == Constantes.PELEA_TIPO_PVM_NO_ESPADA)
                        && _retos != null && lanzador.esNoIA()) {
                    for (entry in _retos.entrySet()) {
                        val reto: Reto = entry.getValue()
                        val retoID: Byte = entry.getKey()
                        var exitoReto: EstReto = reto.getEstado()
                        if (exitoReto !== Reto.EstReto.EN_ESPERA) {
                            continue
                        }
                        when (retoID) {
                            Constantes.RETO_AHORRADOR -> if (lanzador.getHechizosLanzadosReto().contains(SH.getHechizoID())) {
                                exitoReto = Reto.EstReto.FALLADO
                            } else {
                                lanzador.getHechizosLanzadosReto().add(SH.getHechizoID())
                            }
                            Constantes.RETO_VERSATIL -> if (lanzador.getHechizosLanzadosReto().contains(SH.getHechizoID())) {
                                exitoReto = Reto.EstReto.FALLADO
                            } else {
                                lanzador.getHechizosLanzadosReto().add(SH.getHechizoID())
                            }
                            Constantes.RETO_LIMITADO -> {
                                val hechizoID: Int = SH.getHechizoID()
                                if (lanzador.getIDHechizoLanzado() === -1) {
                                    lanzador.setIDHechizoLanzado(hechizoID)
                                } else if (lanzador.getIDHechizoLanzado() !== hechizoID) {
                                    exitoReto = Reto.EstReto.FALLADO
                                }
                            }
                        }
                        reto.setEstado(exitoReto)
                    }
                }
                lanzador.addHechizoLanzado(lanzador, SH, celdaObjetivo.getPrimerLuchador())
                val esGC: Boolean = lanzador.puedeGolpeCritico(SH)
                val efectos: ArrayList<EfectoHechizo?> = if (esGC) SH.getEfectosCriticos() else SH.getEfectosNormales()
                val hechizoStr: String = (SH.getHechizoID().toString() + "," + celdaObjetivo.getID() + "," + SH.getSpriteID() + ","
                        + SH.getGrado() + "," + SH.getSpriteInfos())
                GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 300, lanzador.getID().toString() + "", hechizoStr)
                if (esGC) {
                    GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 301, lanzador.getID().toString() + "", hechizoStr)
                }
                // el PA cambiado , hace un setSpellStateOnAllContainers a todos los hechizos, y
                // asi
                // actualiza los estados
                if (costePA > 0 && lanzador.esInvisible(0)) {
                    GestorSalida.ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(this, 7, lanzador.getID(),
                            lanzador.getCeldaPelea().getID())
                }
                cantObjetivos = Hechizo.aplicaHechizoAPeleaSinGTM(this, lanzador, celdaObjetivo, efectos,
                        TipoDaño.NORMAL, esGC)
            }
            // salio del fallo o lanz normal
            if (perso != null) {
                GestorSalida.ENVIAR_GAF_FINALIZAR_ACCION(perso, lanzador.getID(), -1)
            }
            if (cantObjetivos > 0) {
                GestorSalida.ENVIAR_GTM_INFO_STATS_TODO_LUCHADORES_A_TODOS(this, 7, true)
            }
            if (lanzador.getIA() != null || !lanzador.getPersonaje().getCompañeros().isEmpty()) {
                var bucle = 0
                while (esperahechizo && bucle < 15) {
                    try {
                        Thread.sleep(150L)
                        bucle += 1
                    } catch (e: Exception) {
                    }
                }
            }
            if (esFC && (lanzador.getIA() != null || SH.esFinTurnoSiFC())) {
                puede = EstadoLanzHechizo.FALLAR
                pasarTurno(lanzador)
            }
        } else if (lanzador.esNoIA()) {
            GestorSalida.ENVIAR_GAC_LIMPIAR_ACCION(perso)
        }
        _tempAccion = ""
        if (lanzador.getIA() != null) {
            lanzador.getIA().nullear()
        }
        comprobarPasarTurnoDespuesMuerte(lanzador)
        if (MainServidor.MODO_DEBUG) {
            System.out.println("intentarLanzarHechizo() Hechizo: " + SH.getHechizo().getNombre().toString() + " ("
                    + SH.getHechizoID().toString() + ") Estado: " + puede)
        }
        return puede
    }

    @Synchronized
    fun intentarCAC(perso: Personaje?, idCeldaObj: Short) {
        val lanzador: Luchador? = getLuchadorPorID(perso.getID())
        if (lanzador == null) {
            GestorSalida.ENVIAR_GAC_LIMPIAR_ACCION(perso)
            return
        }
        if (!lanzador.puedeJugar()) {
            if (lanzador.esNoIA()) {
                GestorSalida.ENVIAR_Im_INFORMACION(perso, "1175")
                GestorSalida.ENVIAR_GAC_LIMPIAR_ACCION(perso)
            }
            return
        }
        if (!_tempAccion.isEmpty()) {
            if (lanzador.esNoIA()) {
                if (!_tempAccion.isEmpty()) {
                    if (!MainServidor.PARAM_JUGAR_RAPIDO) {
                        GestorSalida.ENVIAR_Im_INFORMACION(perso, "1REALIZANDO_TEMP_ACCION;$_tempAccion")
                    } else {
                        finAccion(lanzador)
                        return
                    }
                }
                GestorSalida.ENVIAR_GAC_LIMPIAR_ACCION(perso)
            }
            return
        }
        if (lanzador.esInvocacion()) {
            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso,
                    "Las invocaciones y revividos no pueden usar ataques CAC")
            GestorSalida.ENVIAR_GAC_LIMPIAR_ACCION(perso)
            return
        }
        if ((_tipo == Constantes.PELEA_TIPO_PVM || _tipo == Constantes.PELEA_TIPO_PVM_NO_ESPADA) && _retos != null) { // mobs
            for (entry in _retos.entrySet()) {
                val reto: Reto = entry.getValue()
                val retoID: Byte = entry.getKey()
                var exitoReto: EstReto = reto.getEstado()
                if (exitoReto !== Reto.EstReto.EN_ESPERA) {
                    continue
                }
                when (retoID) {
                    Constantes.RETO_AHORRADOR, Constantes.RETO_VERSATIL -> if (lanzador.getHechizosLanzadosReto().contains(0)) {
                        exitoReto = Reto.EstReto.FALLADO
                    } else {
                        lanzador.getHechizosLanzadosReto().add(0)
                    }
                    Constantes.RETO_MISTICO -> exitoReto = Reto.EstReto.FALLADO
                    Constantes.RETO_LIMITADO -> {
                        val hechizoID = 0
                        if (lanzador.getIDHechizoLanzado() === -1) {
                            lanzador.setIDHechizoLanzado(hechizoID)
                        } else if (lanzador.getIDHechizoLanzado() !== hechizoID) {
                            exitoReto = Reto.EstReto.FALLADO
                        }
                    }
                }
                reto.setEstado(exitoReto)
            }
        }
        var SH: StatHechizo = Mundo.getHechizo(0).getStatsPorNivel(1)
        var eNormales: ArrayList<EfectoHechizo?> = SH.getEfectosNormales()
        var eCriticos: ArrayList<EfectoHechizo?> = SH.getEfectosCriticos()
        var arma: Objeto = perso.getObjPosicion(Constantes.OBJETO_POS_ARMA)
        if (MainServidor.PARAMS_HECHIZO_CAC_MULTIMAN > 0 && lanzador.esMultiman()) {
            arma = perso.getCompañeros().get(0).getObjPosicion(Constantes.OBJETO_POS_ARMA)
        }
        if (arma != null) {
            SH = arma.getObjModelo().getStatHechizo()
            eNormales = arma.getEfectosNormales()
            eCriticos = arma.getEfectosCriticos()
            val costePA = arma.getObjModelo().getCostePA() as Int
            if (MainServidor.GOLPES_MAPAS.containsKey(mapaReal.getID())) {
                val maximo: Int = MainServidor.GOLPES_MAPAS.get(mapaReal.getID())
                if (maximo <= _cantCAC) {
                    GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso,
                            "En este mapa solo puedes usar el arma $maximo veces")
                    GestorSalida.ENVIAR_GAC_LIMPIAR_ACCION(perso)
                    return
                }
            } else if (MainServidor.MAX_GOLPES_CAC_PVM.get(costePA) != null) {
                if (_tipo == Constantes.PELEA_TIPO_PVM || _tipo == Constantes.PELEA_TIPO_PVM_NO_ESPADA) {
                    if (MainServidor.MAX_GOLPES_CAC_PVM.get(costePA) != null) {
                        val maximo: Int = MainServidor.MAX_GOLPES_CAC_PVM.get(costePA)
                        if (maximo <= _cantCAC) {
                            GestorSalida.ENVIAR_GAC_LIMPIAR_ACCION(perso)
                            return
                        }
                    }
                } else if (MainServidor.MAX_GOLPES_CAC.get(costePA) != null) {
                    val maximo: Int = MainServidor.MAX_GOLPES_CAC.get(costePA)
                    if (maximo <= _cantCAC) {
                        GestorSalida.ENVIAR_GAC_LIMPIAR_ACCION(perso)
                        return
                    }
                }
            } else {
                if (MainServidor.MAX_GOLPES_CAC.get(costePA) != null) {
                    val maximo: Int = MainServidor.MAX_GOLPES_CAC.get(costePA)
                    if (maximo <= _cantCAC) {
                        GestorSalida.ENVIAR_GAC_LIMPIAR_ACCION(perso)
                        return
                    }
                }
            }
        }
        val celdaObjetivo: Celda = _mapaCopia.getCelda(idCeldaObj)
        val puede: EstadoLanzHechizo? = puedeLanzarHechizo(lanzador, SH, celdaObjetivo, (-1.toShort()).toShort())
        if (puede !== EstadoLanzHechizo.PODER) {
            GestorSalida.ENVIAR_GAC_LIMPIAR_ACCION(perso)
            return
        }
        if (MainServidor.MAX_CAC_POR_TURNO > 0 && _cantCAC >= MainServidor.MAX_CAC_POR_TURNO) {
            GestorSalida.ENVIAR_GAC_LIMPIAR_ACCION(perso)
            return
        }
        _cantCAC++
        if (lanzador.esInvisible(0)) {
            lanzador.hacerseVisible()
        }
        GestorSalida.ENVIAR_GAS_INICIO_DE_ACCION(lanzador.getPersonaje(), lanzador.getID())
        val costePA: Byte = SH.getCostePA()
        lanzador.addPARestantes(-costePA)
        lanzador.addPAUsados(costePA)
        GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 102, perso.getID().toString() + "", perso.getID().toString() + "," + -costePA)
        val esFC: Boolean = lanzador.puedeFalloCritico(SH)
        if (esFC) {
            GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 305, perso.getID().toString() + "", "")
        } else {
            _tempAccion = "CAC"
            val esGC: Boolean = lanzador.puedeGolpeCritico(SH)
            GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 303, perso.getID().toString() + "", idCeldaObj.toString() + "")
            if (esGC) {
                GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 301, perso.getID().toString() + "", "0")
            }
            Hechizo.aplicaHechizoAPelea(this, lanzador, celdaObjetivo, if (esGC) eCriticos else eNormales, TipoDaño.CAC,
                    esGC)
            _tempAccion = ""
        }
        GestorSalida.ENVIAR_GAF_FINALIZAR_ACCION(perso, perso.getID(), -1)
        /*
		 * if (esFC) { pasarTurno(lanzador); } else {
		 */comprobarPasarTurnoDespuesMuerte(lanzador)
        // }
    }

    // ataque cuerpo a cuerpo CAC
    fun getEspectadores(): ConcurrentHashMap<Integer?, Luchador?>? {
        return _espectadores
    }

    fun puedeLanzarHechizo(lanzador: Luchador?, SH: StatHechizo?,
                           celdaBlancoHechizo: Celda?, celdaDeLanzador: Short): EstadoLanzHechizo? {
        if (_luchadorDeTurno == null) {
            return EstadoLanzHechizo.NO_PODER
        }
        if (lanzador.esNoIA()) {
            when (_tipo) {
                Constantes.PELEA_TIPO_PVP, Constantes.PELEA_TIPO_KOLISEO -> if (MainServidor.HECHIZOS_NO_KOLI_PVP.contains(SH.getHechizoID())) {
                    finAccion(lanzador)
                    return EstadoLanzHechizo.NO_PODER
                }
            }
        }
        val perso: Personaje = lanzador.getPersonaje()
        if (celdaBlancoHechizo == null) {
            if (perso != null) {
                GestorSalida.ENVIAR_Im_INFORMACION(perso, "1172")
            }
            if (MainServidor.MODO_DEBUG) {
                System.out.println("puedeLanzarHechizo() -> La celda blanco hechizo es nula")
            }
            return EstadoLanzHechizo.NO_PODER
        }
        if (SH == null) {
            if (perso != null) {
                GestorSalida.ENVIAR_Im_INFORMACION(perso, "1169")
            }
            if (MainServidor.MODO_DEBUG) {
                System.out.println("puedeLanzarHechizo() -> El hechizo es nulo")
            }
            return EstadoLanzHechizo.NO_PODER
        }
        if (SH.esAutomaticoAlFinalTurno()) {
            if (perso != null) {
                GestorSalida.ENVIAR_Im_INFORMACION(perso, "1IS_AUTOMATIC_END_TURN")
            }
            if (MainServidor.MODO_DEBUG) {
                System.out.println("puedeLanzarHechizo() -> El hechizo es para lanzamiento automatico")
            }
            return EstadoLanzHechizo.NO_PODER
        }
        val objetivo: Luchador = celdaBlancoHechizo.getPrimerLuchador()
        val filtro: EstadoLanzHechizo? = filtraHechizoDisponible(lanzador, SH, if (objetivo == null) 0 else objetivo.getID())
        if (filtro !== EstadoLanzHechizo.PODER) {
            return filtro
        }
        if (perso == null) {
            if ((SH.esIntercambioPos() || SH.esSoloMover()) && objetivo != null && objetivo.tieneEstado(Constantes.ESTADO_ARRAIGADO)) {
                return EstadoLanzHechizo.OBJETIVO_NO_PERMITIDO
            }
        }
        return if (!dentroDelRango(lanzador, SH, celdaDeLanzador, celdaBlancoHechizo.getID())) {
            EstadoLanzHechizo.NO_TIENE_ALCANCE
        } else EstadoLanzHechizo.PODER
    }

    fun dentroDelRango(lanzador: Luchador?, SH: StatHechizo?, celdaIDLanzador: Short, celdaIDBlanco: Short): Boolean {
        if (SH == null) {
            if (MainServidor.MODO_DEBUG) {
                System.out.println("dentroDelRango() -> El hechizo es nulo")
            }
            return false
        }
        val mapa: Mapa? = _mapaCopia
        if (mapa.getCelda(celdaIDBlanco) == null) {
            return false
        }
        val perso: Personaje = lanzador.getPersonaje()
        if (SH.esTrampa() && mapa.getCelda(celdaIDBlanco).esTrampa()) {
            if (perso != null) {
                GestorSalida.ENVIAR_Im_INFORMACION(perso, "1229")
            }
            return false
        }
        val tempCeldaIDLanzador: Short
        tempCeldaIDLanzador = if (celdaIDLanzador <= -1) {
            lanzador.getCeldaPelea().getID()
        } else {
            celdaIDLanzador
        }
        return if (!Camino.celdasPosibleLanzamiento(SH, lanzador, _mapaCopia, tempCeldaIDLanzador, celdaIDBlanco)
                        .contains(mapa.getCelda(celdaIDBlanco))) {
            false
        } else true
        // if (SH.esNecesarioCeldaLibre() &&
        // mapa.getCelda(celdaIDBlanco).getPrimerLuchador() != null) {
        // if (perso != null) {
        // GestorSalida.ENVIAR_Im_INFORMACION(perso, "1173");
        // }
        // if (Bustemu.MODO_DEBUG) {
        // System.out.println("dentroDelRango() -> El hechizo " +
        // SH.getHechizo().getNombre() +
        // " necesita celda libre");
        // }
        // return false;
        // }
        // boolean modif = false;
        // final int hechizoID = SH.getHechizoID();
        // if (perso != null && perso.tieneModfiSetClase(hechizoID)) {
        // modif = perso.getModifSetClase(hechizoID, 288) == 1;
        // }
        // if (SH.esLanzarLinea() && !modif
        // && !Camino.siCeldasEstanEnMismaLinea(mapa,
        // mapa.getCelda(tempCeldaIDLanzador),
        // mapa.getCelda(celdaIDBlanco))) {
        // if (perso != null) {
        // GestorSalida.ENVIAR_Im_INFORMACION(perso, "1173");
        // }
        // if (Bustemu.MODO_DEBUG) {
        // System.out.println("dentroDelRango() -> El hechizo " +
        // SH.getHechizo().getNombre() +
        // " no esta en Linea");
        // }
        // return false;
        // }
        // modif = false;
        // if (perso != null && perso.tieneModfiSetClase(hechizoID)) {
        // modif = perso.getModifSetClase(hechizoID, 289) == 1;
        // }
        // if (!modif && SH.esLineaVista()
        // && !Camino.lineaDeVistaPelea(mapa, tempCeldaIDLanzador, celdaIDBlanco,
        // lanzador.getID())) {
        // if (perso != null) {
        // GestorSalida.ENVIAR_Im_INFORMACION(perso, "1174");
        // }
        // if (Bustemu.MODO_DEBUG) {
        // System.out.println("dentroDelRango() -> El hechizo " +
        // SH.getHechizo().getNombre() +
        // " tiene linea de vista");
        // }
        // return false;
        // }
        // byte maxAlc = SH.getMaxAlc();
        // final byte minAlc = SH.getMinAlc();
        // modif = false;
        // if (perso != null && perso.tieneModfiSetClase(hechizoID)) {
        // maxAlc += perso.getModifSetClase(hechizoID, 281);
        // modif = perso.getModifSetClase(hechizoID, 282) == 1;
        // }
        // if (modif || SH.esAlcanceModificable()) {
        // maxAlc +=
        // lanzador.getTotalStats().getStatParaMostrar(Constantes.STAT_MAS_ALCANCE);
        // }
        // if (maxAlc < minAlc) {
        // maxAlc = minAlc;
        // }
        // final int dist = Camino.distanciaDosCeldas(mapa, tempCeldaIDLanzador,
        // celdaIDBlanco);
        // if (dist < minAlc || dist > maxAlc) {
        // if (perso != null) {
        // GestorSalida.ENVIAR_Im_INFORMACION(perso, "1171;" + minAlc + "~" + maxAlc +
        // "~" + dist);
        // }
        // if (Bustemu.MODO_DEBUG) {
        // System.out.println("dentroDelRango() -> El hechizo " +
        // SH.getHechizo().getNombre() +
        // " esta fuera del alcance");
        // }
        // return false;
        // }
    }

    fun filtraHechizoDisponible(lanzador: Luchador?, SH: StatHechizo?, idObjetivo: Int): EstadoLanzHechizo? {
        val hechizoID: Int = SH.getHechizoID()
        val perso: Personaje = lanzador.getPersonaje()
        if (SH.esNecesarioObjetivo() && idObjetivo == 0) {
            if (perso != null) {
                GestorSalida.ENVIAR_Im_INFORMACION(perso, "1NEED_A_TARGET")
            }
            if (MainServidor.MODO_DEBUG) {
                System.out.println("filtrarHechizo() -> Necesita un objetivo")
            }
            return EstadoLanzHechizo.NO_OBJETIVO
        }
        for (estado in SH.getEstadosProhibido()) {
            if (lanzador.tieneEstado(estado)) {
                if (perso != null) {
                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "1IN_FORBIDDEN_STATE;$estado")
                }
                if (MainServidor.MODO_DEBUG) {
                    System.out.println("filtrarHechizo() -> Tiene el estado prohibido " + estado + " para lanzar "
                            + SH.getHechizo().getNombre())
                }
                return EstadoLanzHechizo.NO_PODER
            }
        }
        for (estado in SH.getEstadosNecesario()) {
            if (!lanzador.tieneEstado(estado)) {
                if (perso != null) {
                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "1NOT_IN_REQUIRED_STATE;$estado")
                }
                if (MainServidor.MODO_DEBUG) {
                    System.out.println("filtrarHechizo() -> No tiene el estado necesario " + estado + " para lanzar "
                            + SH.getHechizo().getNombre())
                }
                return EstadoLanzHechizo.NO_PODER
            }
        }
        var costePA: Byte = SH.getCostePA()
        if (perso != null && perso.tieneModfiSetClase(hechizoID)) {
            costePA -= perso.getModifSetClase(hechizoID, 285)
        }
        val PA: Int = lanzador.getPARestantes()
        if (PA < costePA) {
            if (perso != null) {
                GestorSalida.ENVIAR_Im_INFORMACION(perso, "1170;" + PA + "~" + SH.getCostePA())
            }
            if (MainServidor.MODO_DEBUG) {
                System.out.println(
                        "filtrarHechizo() -> No tiene suficientes PA para lanzar " + SH.getHechizo().getNombre())
            }
            return EstadoLanzHechizo.NO_TIENE_PA
        }
        val sigTurnoLanz: Int = HechizoLanzado.poderSigLanzamiento(lanzador, hechizoID)
        if (sigTurnoLanz > 0) {
            if (perso != null) {
                GestorSalida.ENVIAR_Im_INFORMACION(perso, "1CANT_LAUNCH_BEFORE;$sigTurnoLanz")
            }
            if (MainServidor.MODO_DEBUG) {
                System.out.println("filtrarHechizo() -> Falta " + sigTurnoLanz + " turnos para lanzar "
                        + SH.getHechizo().getNombre())
            }
            return EstadoLanzHechizo.COOLDOWN
        }
        var nroLanzTurno: Byte = SH.getMaxLanzPorTurno()
        if (perso != null && perso.tieneModfiSetClase(hechizoID)) {
            nroLanzTurno += perso.getModifSetClase(hechizoID, 290)
        }
        if (nroLanzTurno > 0 && nroLanzTurno - HechizoLanzado.getNroLanzamientos(lanzador, hechizoID) <= 0) {
            if (perso != null) {
                GestorSalida.ENVIAR_Im_INFORMACION(perso, "1CANT_LAUNCH_MORE;$nroLanzTurno")
            }
            if (MainServidor.MODO_DEBUG) {
                System.out.println("filtrarHechizo() -> El nroLanzTurno es " + nroLanzTurno
                        + ", por lo tanto no se puede lanzar " + SH.getHechizo().getNombre())
            }
            return EstadoLanzHechizo.COOLDOWN
        }
        if (idObjetivo != 0) {
            var nroLanzMaxObjetivo: Byte = SH.getMaxLanzPorObjetivo()
            if (perso != null && perso.tieneModfiSetClase(hechizoID)) {
                nroLanzMaxObjetivo += perso.getModifSetClase(hechizoID, 291)
            }
            if (nroLanzMaxObjetivo >= 1
                    && HechizoLanzado.getNroLanzPorObjetivo(lanzador, idObjetivo, hechizoID) >= nroLanzMaxObjetivo) {
                if (perso != null) {
                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "1CANT_ON_THIS_PLAYER")
                }
                if (MainServidor.MODO_DEBUG) {
                    System.out.println("filtrarHechizo() -> El nroMaxObjetivo " + nroLanzMaxObjetivo
                            + " por lo tanto no se puede lanzar " + SH.getHechizo().getNombre())
                }
                return EstadoLanzHechizo.OBJETIVO_NO_PERMITIDO
            }
        }
        return EstadoLanzHechizo.PODER
    }

    private fun robarPersonajePerdedor(luch: Luchador?) {
        if (!MainServidor.PARAM_JUGADORES_HEROICO_MORIR) {
            return
        }
        val pjPerdedor: Personaje = luch.getPersonaje() ?: return
        if (luch.fueSaqueado()) {
            return
        }
        imprimiAsesinos(luch)
        if (esMapaHeroico()) {
            when (_tipo) {
                Constantes.PELEA_TIPO_DESAFIO, Constantes.PELEA_TIPO_KOLISEO -> {
                }
                else -> {
                    luch.setSaqueado(true)
                    val montura: Montura = pjPerdedor.getMontura()
                    if (montura != null) {
                        pjPerdedor.setMontura(null)
                        if (montura.getPergamino() <= 0) {
                            try {
                                val obj1: Objeto = montura.getObjModCertificado().crearObjeto(1,
                                        Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM)
                                obj1.fijarStatValor(Constantes.STAT_CONSULTAR_MONTURA, Math.abs(montura.getID()))
                                obj1.addStatTexto(Constantes.STAT_PERTENECE_A, "0#0#0#" + pjPerdedor.getNombre())
                                obj1.addStatTexto(Constantes.STAT_NOMBRE, "0#0#0#" + montura.getNombre())
                                pjPerdedor.addObjetoConOAKO(obj1, true)
                                montura.setPergamino(obj1.getID())
                            } catch (e: Exception) {
                            }
                        }
                    }
                    val kamas: Long = pjPerdedor.getKamas()
                    _kamasRobadas += kamas
                    // _expRobada = pjPerdedor.getExperiencia() / 10;
                    _expRobada = BigInteger(pjPerdedor.getExperiencia()).divide(BigInteger.TEN).longValue()
                    pjPerdedor.addKamas(-kamas, false, false)
                    val objPerder: ArrayList<Objeto?> = ArrayList<Objeto?>()
                    objPerder.addAll(pjPerdedor.getObjetosTodos())
                    for (obj in objPerder) {
                        if (robarObjPersonaje(obj, pjPerdedor)) {
                            pjPerdedor.borrarOEliminarConOR(obj.getID(), false)
                        }
                    }
                    objPerder.clear()
                    objPerder.addAll(pjPerdedor.getObjetosTienda())
                    for (obj in objPerder) {
                        if (robarObjPersonaje(obj, pjPerdedor)) {
                            pjPerdedor.borrarObjTienda(obj)
                        }
                    }
                    pjPerdedor.convertirseTumba()
                    GestorSQL.INSERT_CEMENTERIO(pjPerdedor.getNombre(), pjPerdedor.getNivel(), pjPerdedor.getSexo(),
                            pjPerdedor.getClaseID(true), _asesinos.toString(), _mapaReal.getSubArea().getID())
                    GestorSQL.SALVAR_PERSONAJE(pjPerdedor, true)
                }
            }
        }
    }

    // private int getPorcFinal(int porcentaje, float coef) {
    // // formulda para drop porcentaje
    // int f = (int) ((1 - (Math.pow(1 - (porcentaje / 100000f), coef))) * 100000);
    // return Math.max(1, f);
    // }
    private fun robarObjPersonaje(objeto: Objeto?, pjPerd: Personaje?): Boolean {
        if (!objeto.pasoIntercambiableDesde()) {
            pjPerd.addObjetoAlBanco(objeto)
            return false
        }
        if (objeto.tieneStatTexto(Constantes.STAT_LIGADO_A_CUENTA)) {
            pjPerd.addObjetoAlBanco(objeto)
            return false
        }
        if (!MainServidor.PARAM_HEROICO_PIERDE_ITEMS_VIP && objeto.getObjModelo().getOgrinas() > 0) {
            return false
        }
        if (objeto.getPosicion() >= 20 && objeto.getPosicion() <= 27) {
            return false
        }
        if (objeto.getObjModelo().getTipo() === Constantes.OBJETO_TIPO_OBJETO_DE_BUSQUEDA) {
            return false
        }
        objeto.setPosicion(Constantes.OBJETO_POS_NO_EQUIPADO, pjPerd, false)
        addObjetosRobados(objeto)
        return true
    }

    private fun getMisionPVPPorEquipo(equipo: Int): MisionPVP? {
        if (MainServidor.MODO_EXILED && !MainServidor.MAPAS_PVP.contains(mapaReal.getID())) {
            return null
        }
        if (equipo == 1 && _inicioLuchEquipo1!!.size() === 1) {
            try {
                val init: Personaje = _luchInit1.getPersonaje()
                if (init != null && init.getMisionPVP() != null) {
                    val victima: String = init.getMisionPVP().getNombreVictima()
                    for (luchador in _inicioLuchEquipo2!!) {
                        val p: Personaje = luchador.getPersonaje() ?: continue
                        if (p.getNombre().equalsIgnoreCase(victima)) {
                            return init.getMisionPVP()
                        }
                        if (p.getMisionPVP() != null) {
                            try {
                                if (p.getMisionPVP().getNombreVictima().equalsIgnoreCase(init.getNombre())) {
                                    return p.getMisionPVP()
                                }
                            } catch (e: Exception) {
                            }
                        }
                    }
                }
            } catch (e: Exception) {
            }
        }
        if (equipo == 2 && _inicioLuchEquipo2!!.size() === 1) {
            try {
                val init: Personaje = _luchInit2.getPersonaje()
                if (init != null && init.getMisionPVP() != null) {
                    val victima: String = init.getMisionPVP().getNombreVictima()
                    for (luchador in _inicioLuchEquipo1!!) {
                        val p: Personaje = luchador.getPersonaje() ?: continue
                        if (p.getNombre().equalsIgnoreCase(victima)) {
                            return init.getMisionPVP()
                        }
                        if (p.getMisionPVP() != null) {
                            try {
                                if (p.getMisionPVP().getNombreVictima().equalsIgnoreCase(init.getNombre())) {
                                    return p.getMisionPVP()
                                }
                            } catch (e: Exception) {
                            }
                        }
                    }
                }
            } catch (e: Exception) {
            }
        }
        return null
    }

    private fun recompensaMision(luchGanador: Luchador?, ganador: Boolean, ganadores: ArrayList<Luchador?>?,
                                 perdedores: ArrayList<Luchador?>?) {
        val mision: MisionPVP = getMisionPVPPorEquipo(luchGanador.getParamEquipoAliado()) ?: return
        /*
		 * if (!entregoLogro) { for (Luchador luch : ganadores) { if
		 * (luch.getPersonaje() == null) continue;
		 * luch.getPersonaje().confirmarPeleaPVP("1"); } for (Luchador luch :
		 * perdedores) {// prueba if (luch.getPersonaje() == null) continue;
		 * luch.getPersonaje().confirmarPeleaPVP("0"); } entregoLogro = true; }
		 */if (ganador) {
            luchGanador.getPersonaje().setNbStalkWin(luchGanador.getPersonaje().getNbStalkWin() + 1)
            Sucess.launch(luchGanador.getPersonaje(), 6.toByte(), null, 0)
            luchGanador.getPersonaje().PuntosPrestigio += MainServidor.ptsPvP
            luchGanador.getPersonaje().sendMessage("Has ganado <b>" + MainServidor.ptsPvP.toString() + "</b> Pts. por pelea PvP")
            val objetos = StringBuilder()
            if (mision.esCazaCabezas()) {
                objetos.append(MainServidor.MISION_PVP_OBJETOS)
                val craneo: Int = mision.getCraneo()
                if (craneo != 0) {
                    if (objetos.length() > 0) {
                        objetos.append(";")
                    }
                    objetos.append("$craneo,1")
                }
            }
            for (s in objetos.toString().split(";")) {
                try {
                    if (s.isEmpty()) {
                        continue
                    }
                    val id: Int = Integer.parseInt(s.split(",").get(0))
                    val cant: Int = Integer.parseInt(s.split(",").get(1))
                    val obj: Objeto = Mundo.getObjetoModelo(id).crearObjeto(cant, Constantes.OBJETO_POS_NO_EQUIPADO,
                            CAPACIDAD_STATS.RANDOM)
                    luchGanador.addDropLuchador(obj, true)
                } catch (e: Exception) {
                }
            }
            if (MainServidor.PARAM_GANAR_KAMAS_PVP) {
                val kamas: Long = mision.getKamasRecompensa()
                luchGanador.addKamasGanadas(kamas)
            }
            if (MainServidor.PARAM_GANAR_EXP_PVP) {
                val expPorMision: Long = mision.getExpMision()
                luchGanador.addXPGanada(expPorMision)
                GestorSalida.ENVIAR_Im_INFORMACION(luchGanador.getPersonaje(), "08;$expPorMision")
            }
        } else {
            for (lucha in ganadores) {
                if (lucha.getPersonaje() == null) continue
                if (lucha.getPersonaje().getNombre().equalsIgnoreCase(mision.getPjMision().getNombre())) {
                    val objetos = StringBuilder()
                    if (mision.esCazaCabezas()) {
                        objetos.append(MainServidor.MISION_PVP_OBJETOS)
                        val craneo: Int = mision.getCraneo()
                        if (craneo != 0) {
                            if (objetos.length() > 0) {
                                objetos.append(";")
                            }
                            objetos.append("$craneo,1")
                        }
                    }
                    for (s in objetos.toString().split(";")) {
                        try {
                            if (s.isEmpty()) {
                                continue
                            }
                            val id: Int = Integer.parseInt(s.split(",").get(0))
                            val cant: Int = Integer.parseInt(s.split(",").get(1))
                            val obj: Objeto = Mundo.getObjetoModelo(id).crearObjeto(cant, Constantes.OBJETO_POS_NO_EQUIPADO,
                                    CAPACIDAD_STATS.RANDOM)
                            lucha.addDropLuchador(obj, true)
                        } catch (e: Exception) {
                        }
                    }
                    if (MainServidor.PARAM_GANAR_KAMAS_PVP) {
                        val kamas: Long = mision.getKamasRecompensa()
                        lucha.addKamasGanadas(kamas)
                    }
                    if (MainServidor.PARAM_GANAR_EXP_PVP) {
                        val expPorMision: Long = mision.getExpMision()
                        lucha.addXPGanada(expPorMision)
                        GestorSalida.ENVIAR_Im_INFORMACION(lucha.getPersonaje(), "08;$expPorMision")
                    }
                }
            }
        }
        if (mision === luchGanador.getPersonaje().getMisionPVP()) {
            luchGanador.getPersonaje().eliminarPorObjModeloRecibidoDesdeMinutos(10085, 0) // pergamino
            luchGanador.getPersonaje().setMisionPVP(null)
        }
    }

    private fun addObjetosRobados(obj: Objeto?) {
        if (_objetosRobados == null) {
            _objetosRobados = ArrayList<Objeto?>()
        }
        _objetosRobados.add(obj)
    }

    fun acaboPelea(equipoMuerto: Byte): Boolean {
        // equipoMuero = 3, verifica si acabo el combate
        var linea = 0
        try {
            if (_fase == Constantes.PELEA_FASE_FINALIZADO || Mundo.SERVIDOR_ESTADO === Constantes.SERVIDOR_OFFLINE) {
                return false
            }
            var equipo1Muerto = true
            var equipo2Muerto = true
            linea = 1
            if (equipoMuerto.toInt() == 3) {
                linea = 8
                for (luch in _equipo1.values()) {
                    if (luch.esInvocacion()) {
                        continue
                    }
                    if (!luch.estaMuerto()) {
                        equipo1Muerto = false
                        break
                    }
                }
                linea = 9
                for (luch in _equipo2.values()) {
                    if (luch.esInvocacion()) {
                        continue
                    }
                    if (!luch.estaMuerto()) {
                        equipo2Muerto = false
                        break
                    }
                }
            } else {
                linea = 10
                equipo1Muerto = equipoMuerto.toInt() == 1
                linea = 11
                equipo2Muerto = equipoMuerto.toInt() == 2
            }
            linea = 2
            if (equipo1Muerto || equipo2Muerto) {
                linea = 3
                if (_fase == Constantes.PELEA_FASE_POSICION) {
                    antesIniciarPelea()
                }
                linea = 4
                val packet = getPanelResultados(if (equipo1Muerto) 2 else 1)
                if (equipo1Muerto) {
                    _luchInit2.getPreLuchador().sobrevivio()
                } else {
                    _luchInit2.getPreLuchador().murio()
                }
                linea = 5
                mostrarResultados(packet)
                GestorSalida.ENVIAR_fC_CANTIDAD_DE_PELEAS(_mapaReal)
                linea = 6
                if (_salvarMobHeroico) {
                    _mapaReal.salvarMapaHeroico()
                }
                linea = 7
            }
            return equipo1Muerto || equipo2Muerto
        } catch (e: Exception) {
            MainServidor.redactarLogServidorln("Exception acaboPelea - Mapa: " + _mapaCopia.getID().toString() + " PeleaID: " + iD
                    .toString() + " Linea: " + linea.toString() + ", Exception: " + e.toString())
            e.printStackTrace()
        }
        return false
    }

    fun cancelarPelea() {
        try {
            if (_fase == Constantes.PELEA_FASE_FINALIZADO || Mundo.SERVIDOR_ESTADO === Constantes.SERVIDOR_OFFLINE) {
                return
            }
            if (_fase == Constantes.PELEA_FASE_POSICION) {
                antesIniciarPelea()
            }
            val packet = getPanelResultados(3)
            _luchInit2.getPreLuchador().sobrevivio()
            mostrarResultados(packet)
        } catch (e: Exception) {
            MainServidor.redactarLogServidorln("Exception cancelarPelea - Mapa: " + _mapaCopia.getID().toString() + " PeleaID: "
                    + iD.toString() + ", Exception: " + e.toString())
            e.printStackTrace()
        }
    }

    private fun mostrarResultados(packet: String?) {
        try {
            if (MainServidor.SALVAR_LOGS_TIPO_COMBATE.contains(_tipo)) {
                LOG_COMBATES.append(Date().toString() + "\t" + _tipo + "\t" + _mapaCopia.getID() + "\t" + packet + "\n")
            }
            Thread.sleep(100 + 300 * _cantUltAfec)
            GestorSalida.ENVIAR_GE_PANEL_RESULTADOS_PELEA(this, 7, packet)
            GestorSalida.ENVIAR_fL_LISTA_PELEAS_AL_MAPA(_mapaReal)
            eliminarTimer()
        } catch (e: Exception) {
            MainServidor.redactarLogServidorln("Exception mostrarResultados - Mapa: " + _mapaCopia.getID()
                    .toString() + " PeleaID: " + iD.toString() + ", Exception: " + e.toString())
            e.printStackTrace()
        }
    }

    private fun getPanelResultados(equipoGanador: Int): String? {
        return try {
            // equipoGanador 3 = cancelar la pelea
            if (Mundo.SERVIDOR_ESTADO === Constantes.SERVIDOR_OFFLINE) {
                return ""
            }
            if (_fase < Constantes.PELEA_FASE_COMBATE) {
                GestorSalida.ENVIAR_Gc_BORRAR_ESPADA_EN_MAPA(_mapaReal, iD)
            }
            _fase = Constantes.PELEA_FASE_FINALIZADO
            _mapaReal.borrarPelea(iD)
            val tiempo: Long = System.currentTimeMillis() - _tiempoCombate
            val initID = _idLuchInit1
            var tipoX: Byte = 0
            when (_tipo) {
                Constantes.PELEA_TIPO_PVP, Constantes.PELEA_TIPO_PRISMA, Constantes.PELEA_TIPO_KOLISEO -> tipoX = 1
                Constantes.PELEA_TIPO_RECAUDADOR -> if (equipoGanador == 1) {
                    _kamasRobadas += _luchInit2.getRecaudador().getKamas()
                    _expRobada = _luchInit2.getRecaudador().getExp()
                    if (_objetosRobados == null) {
                        _objetosRobados = ArrayList<Objeto?>()
                    }
                    _objetosRobados.addAll(_luchInit2.getRecaudador().getObjetos())
                    _luchInit2.getRecaudador().clearObjetos()
                }
                Constantes.PELEA_TIPO_PVM -> {
                    if (_mobGrupo != null) {
                        _mobGrupo.setPelea(null)
                        if (equipoGanador == 1) {
                            _mobGrupo.setBonusEstrellas(MainServidor.INICIO_BONUS_ESTRELLAS_MOBS)
                            _mobGrupo.setMuerto(true)
                            if (_mobGrupo.esHeroico()) {
                                for (id in _mobGrupo.getObjetosHeroico()) {
                                    val obj: Objeto = Mundo.getObjeto(id) ?: continue
                                    addObjetosRobados(obj)
                                }
                                _kamasRobadas += _mobGrupo.getKamasHeroico()
                                _mobGrupo.borrarObjetosHeroico()
                                _mobGrupo.setKamasHeroico(0)
                                _mapaReal.salvarMapaHeroico()
                            }
                        }
                    }
                    if (_retos != null) {
                        if (equipoGanador == 1) {
                            for (entry in _retos.entrySet()) {
                                val reto: Reto = entry.getValue()
                                val retoID: Byte = entry.getKey()
                                var exitoReto: EstReto = reto.getEstado()
                                if (exitoReto !== Reto.EstReto.EN_ESPERA) {
                                    continue
                                }
                                when (retoID) {
                                    Constantes.RETO_SUPERVIVIENTE -> for (luchador in _inicioLuchEquipo1!!) {
                                        if (luchador.estaMuerto()) {
                                            exitoReto = Reto.EstReto.FALLADO
                                            break
                                        }
                                    }
                                    Constantes.RETO_REPARTO, Constantes.RETO_CADA_UNO_CON_SU_MONSTRUO -> for (luchador in _inicioLuchEquipo1!!) {
                                        if (!luchador.getMobsAsesinadosReto().isEmpty()) {
                                            exitoReto = Reto.EstReto.FALLADO
                                            break
                                        }
                                    }
                                }
                                if (exitoReto === Reto.EstReto.EN_ESPERA) {
                                    exitoReto = Reto.EstReto.REALIZADO
                                }
                                reto.setEstado(exitoReto)
                            }
                        } else if (equipoGanador == 2) {
                            for (entry in _retos.entrySet()) {
                                val reto: Reto = entry.getValue()
                                val exitoReto: EstReto = reto.getEstado()
                                if (exitoReto !== Reto.EstReto.EN_ESPERA) {
                                    continue
                                }
                                reto.setEstado(Reto.EstReto.FALLADO)
                            }
                        }
                    }
                }
                Constantes.PELEA_TIPO_PVM_NO_ESPADA -> if (_retos != null) {
                    if (equipoGanador == 1) {
                        for (entry in _retos.entrySet()) {
                            val reto: Reto = entry.getValue()
                            val retoID: Byte = entry.getKey()
                            var exitoReto: EstReto = reto.getEstado()
                            if (exitoReto !== Reto.EstReto.EN_ESPERA) {
                                continue
                            }
                            when (retoID) {
                                Constantes.RETO_SUPERVIVIENTE -> for (luchador in _inicioLuchEquipo1!!) {
                                    if (luchador.estaMuerto()) {
                                        exitoReto = Reto.EstReto.FALLADO
                                        break
                                    }
                                }
                                Constantes.RETO_REPARTO, Constantes.RETO_CADA_UNO_CON_SU_MONSTRUO -> for (luchador in _inicioLuchEquipo1!!) {
                                    if (!luchador.getMobsAsesinadosReto().isEmpty()) {
                                        exitoReto = Reto.EstReto.FALLADO
                                        break
                                    }
                                }
                            }
                            if (exitoReto === Reto.EstReto.EN_ESPERA) {
                                exitoReto = Reto.EstReto.REALIZADO
                            }
                            reto.setEstado(exitoReto)
                        }
                    } else if (equipoGanador == 2) {
                        for (entry in _retos.entrySet()) {
                            val reto: Reto = entry.getValue()
                            val exitoReto: EstReto = reto.getEstado()
                            if (exitoReto !== Reto.EstReto.EN_ESPERA) {
                                continue
                            }
                            reto.setEstado(Reto.EstReto.FALLADO)
                        }
                    }
                }
            }
            for (luch in _espectadores.values()) {
                if (luch.estaRetirado()) {
                    continue
                }
                luchadorSalirPelea(luch, 0)
            }
            val packet = StringBuilder("GE")
            packet.append(tiempo).append(";").append(_bonusEstrellas).append("|").append(initID).append("|")
                    .append(tipoX).append("|")
            if (equipoGanador == 3) {
                // cancelar la pelea
                val cancelados: ArrayList<Luchador?> = ArrayList<Luchador?>()
                cancelados.addAll(_equipo1.values())
                cancelados.addAll(_equipo2.values())
                for (luch in cancelados) {
                    val pjGanador: Personaje = luch.getPersonaje()
                    if (luch.estaRetirado()) {
                        continue
                    }
                    // salen todos porq se cancelo
                    luchadorSalirPelea(luch, 0)
                    if (luch.esInvocacion()) {
                        continue
                    }
                    if (_tipo != Constantes.PELEA_TIPO_DESAFIO) {
                        if (pjGanador != null) {
                            pjGanador.setPDV(Math.max(1, luch.getPDVSinBuff()), false)
                        }
                    }
                    if (tipoX.toInt() == 0) { // PVM -> SIN HONOR
                        packet.append("2;" + luch.getID().toString() + ";" + luch.getNombre().toString() + ";" + luch.getNivel().toString() + ";" + (if (luch.estaMuerto()) "1" else "0").toString() + ";")
                        packet.append(luch.xpStringLuch(";").toString() + ";")
                        packet.append((if (luch.getExpGanada() === 0) "" else luch.getExpGanada()) + ";")
                        packet.append("" + ";")
                        packet.append("" + ";")
                        packet.append("" + ";")
                        packet.append((if (luch.getKamasGanadas() === 0) "" else luch.getKamasGanadas()) + "|")
                    } else { // PVP -> CON HONOR
                        packet.append("2;" + luch.getID().toString() + ";" + luch.getNombre().toString() + ";" + luch.getNivel())
                        packet.append(";" + (if (luch.estaMuerto()) 1 else 0) + ";")
                        packet.append(stringHonor(luch).toString() + ";")
                        packet.append(0.toString() + ";")
                        packet.append(luch.getNivelAlineacion().toString() + ";")
                        packet.append(luch.getPreLuchador().getDeshonor().toString() + ";")
                        packet.append(0.toString() + ";")
                        packet.append("" + ";")
                        packet.append(luch.getKamasGanadas().toString() + ";")
                        packet.append(luch.xpStringLuch(";").toString() + ";")
                        packet.append(luch.getExpGanada().toString() + "|")
                    }
                }
                return packet.toString()
            }
            // si la pelea no se cancela
            val ganadores: ArrayList<Luchador?> = ArrayList<Luchador?>()
            val perdedores: ArrayList<Luchador?> = ArrayList<Luchador?>()
            if (equipoGanador == 1) {
                ganadores.addAll(_equipo1.values())
                perdedores.addAll(_equipo2.values())
            } else if (equipoGanador == 2) {
                ganadores.addAll(_equipo2.values())
                perdedores.addAll(_equipo1.values())
            }
            ganadores.removeIf(Luchador::isInvocacion)
            perdedores.removeIf(Luchador::isInvocacion)
            _mapaReal.aplicarAccionFinPelea(_tipo, ganadores, _acciones)
            for (luch in ganadores) {
                if (luch.estaRetirado() || luch.esInvocacion()) {
                    continue
                }
                val perso: Personaje = luch.getPersonaje()
                if (perso != null) {
                    if (_asesinos.length() > 0) {
                        _asesinos.append("~")
                    }
                    _asesinos.append(perso.getNombre())
                } else if (luch.getMob() != null && !luch.esInvocacion()) {
                    if (_asesinos.length() > 0) {
                        _asesinos.append("~")
                    }
                    _asesinos.append(luch.getMob().getIDModelo())
                }
                if (perso == null) {
                    continue
                }
                when (_tipo) {
                    Constantes.PELEA_TIPO_DESAFIO -> if (enTorneo) {
                        ServidorSocket.ganadorTorneo(perso)
                    }
                    Constantes.PELEA_TIPO_KOLISEO -> if (perso != null) {
                        val rank: RankingKoliseo = Mundo.getRankingKoliseo(perso.getID())
                        if (rank != null) {
                            rank.aumentarVictoria()
                        }
                        if (perso.getGrupoKoliseo() != null) {
                            perso.getGrupoKoliseo().dejarGrupo(perso)
                        }
                    }
                    Constantes.PELEA_TIPO_PVP -> {
                        if (perso != null) {
                            val rank: RankingPVP = Mundo.getRankingPVP(perso.getID())
                            if (rank != null) {
                                rank.aumentarVictoria()
                            }
                        }
                        recompensaMision(luch, true, ganadores, perdedores)
                    }
                    Constantes.PELEA_TIPO_PVM -> if (perso != null) {
                        val balance: Float = Mundo.getBalanceMundo(perso)
                        val bonusExp: Float = Mundo.getBonusAlinExp(perso)
                        luch.setBonusAlinExp(balance * bonusExp)
                        val bonusDrop: Float = Mundo.getBonusAlinDrop(perso)
                        luch.setBonusAlinDrop(balance * bonusDrop)
                        if (MainServidor.PARAM_BESTIARIO) {
                            for (luchPerdedor in perdedores) {
                                if (luchPerdedor.esInvocacion() && luchPerdedor.getMob() == null) {
                                    continue
                                }
                                perso.addCardMob(luchPerdedor.getMob().getIDModelo())
                            }
                        }
                    }
                    Constantes.PELEA_TIPO_PRISMA -> GestorSalida.ENVIAR_CP_INFO_DEFENSORES_PRISMA(perso, _listadefensores)
                    Constantes.PELEA_TIPO_RECAUDADOR -> GestorSalida.ENVIAR_gITP_INFO_DEFENSORES_RECAUDADOR(perso, _listadefensores)
                }
                if (_tipo != Constantes.PELEA_TIPO_DESAFIO) {
                    if (perso != null) {
                        perso.setPDV(Math.max(1, luch.getPDVSinBuff()), false)
                    }
                }
            }
            for (luch in perdedores) {
                if (luch.estaRetirado() || luch.esInvocacion()) {
                    continue
                }
                robarPersonajePerdedor(luch)
                val perso: Personaje = luch.getPersonaje() ?: continue
                when (_tipo) {
                    Constantes.PELEA_TIPO_DESAFIO -> if (enTorneo) {
                        ServidorSocket.perderTorneo(perso)
                    }
                    Constantes.PELEA_TIPO_KOLISEO -> if (perso != null) {
                        val rank: RankingKoliseo = Mundo.getRankingKoliseo(perso.getID())
                        if (rank != null) {
                            rank.aumentarDerrota()
                        }
                        if (perso.getGrupoKoliseo() != null) {
                            perso.getGrupoKoliseo().dejarGrupo(perso)
                        }
                    }
                    Constantes.PELEA_TIPO_PVP -> {
                        if (perso != null) {
                            val rank: RankingPVP = Mundo.getRankingPVP(perso.getID())
                            if (rank != null) {
                                rank.aumentarDerrota()
                            }
                        }
                        recompensaMision(luch, false, ganadores, perdedores)
                    }
                    Constantes.PELEA_TIPO_PRISMA -> GestorSalida.ENVIAR_CP_INFO_DEFENSORES_PRISMA(perso, _listadefensores)
                    Constantes.PELEA_TIPO_RECAUDADOR -> GestorSalida.ENVIAR_gITP_INFO_DEFENSORES_RECAUDADOR(perso, _listadefensores)
                }
            }
            var minkamas: Long = 0
            var maxkamas: Long = 0
            var coefEstrellas = 0f
            var coefRetoDrop = 0f
            var coefRetoXP = 0f
            var bonusIdolo = 0
            if (!idolos!!.isEmpty()) {
                for (idolostemp in idolos!!.values()) {
                    for (idolo in idolostemp!!) {
                        bonusIdolo += idolo.getBonus()
                    }
                }
            }
            if (MainServidor.PARAM_PERMITIR_BONUS_ESTRELLAS && (_bonusEstrellas > 0 || bonusIdolo > 0)) {
                coefEstrellas = _bonusEstrellas / 100f + bonusIdolo / 15f
            }
            var lucConMaxPP: Luchador? = null
            var dropRobado: ArrayList<Objeto?>? = null
            var mobCapturable = true
            var monturaSalvaje = false
            // piedras
            //
            for (luchGanador in ganadores) {
                if (luchGanador.esDoble()) {
                    continue
                }
                if (luchGanador.esInvocacion()) {
                    if (luchGanador.getMob() != null && luchGanador.getMob().getIDModelo() !== 285) { // cofre
                        continue
                    }
                }
                var prospeccionLuchador: Int = luchGanador.getTotalStats()
                        .getTotalStatConComplemento(Constantes.STAT_MAS_PROSPECCION, this, null)
                var coefPPLuchador = 1f
                val pjGanador: Personaje = luchGanador.getPersonaje()
                if (pjGanador != null) {
                    if (_tipo == Constantes.PELEA_TIPO_PVM || _tipo == Constantes.PELEA_TIPO_PVM_NO_ESPADA) {
                        val mascObj: Objeto = pjGanador.getObjPosicion(Constantes.OBJETO_POS_MASCOTA)
                        var comio = false
                        if (mascObj != null && mascObj.esDevoradorAlmas()) {
                            for (entry in _mobGrupo.getAlmasMobs().entrySet()) {
                                try {
                                    if (Mundo.getMascotaModelo(mascObj.getObjModeloID())
                                                    .getComida(entry.getKey()) != null) {
                                        comio = true
                                        mascObj.comerAlma(entry.getKey(), entry.getValue())
                                    }
                                } catch (e: Exception) {
                                }
                            }
                            if (comio) {
                                GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(pjGanador, mascObj)
                            }
                        }
                        val tt = intArrayOf(MisionObjetivoModelo.VENCER_AL_MOB, MisionObjetivoModelo.VENCER_MOBS_UN_COMBATE)
                        pjGanador.verificarMisionesTipo(tt, _mobGrupo.getAlmasMobs(), false, 0)
                        pjGanador.confirmarLogrosPelea(_mobGrupo.getAlmasMobs())
                        pjGanador.verificarPeleasLogros(_mobGrupo.getAlmasMobs(), mapaReal.esMazmorra())
                        pjGanador.verificarPeleasPase(_mobGrupo.getAlmasMobs(), mapaReal)
                        if (_mapaReal.getID() === 29931 || _mapaReal.getID() === 29932 || _mapaReal.getID() === 29933 || _mapaReal.getID() === 29934) {
                            pjGanador.setMushinPiso(_mapaReal.getID() + 1)
                        } else if (_mapaReal.getID() === 29935) {
                            pjGanador.setMushinPiso(29978)
                        }
                        if (mapaReal.esMazmorra()) {
                            Sucess.launch(pjGanador, 5.toByte(), null, 0)
                            Sucess.launch(pjGanador, 14.toByte(), _mobGrupo.getAlmasMobs(), ganadores.size())
                            Sucess.launch(pjGanador, 12.toByte(), _mobGrupo.getAlmasMobs(), 0)
                        }
                    }
                    if (pjGanador.realizoMisionDelDia()) {
                        val almanax: Almanax = Mundo.getAlmanaxDelDia()
                        if (almanax != null && almanax.getTipo() === Constantes.ALMANAX_BONUS_DROP) {
                            coefPPLuchador += almanax.getBonus() / 100f
                        }
                    }
                    if (pjGanador.alasActivadas() && _alinPelea == pjGanador.getAlineacion()) {
                        coefPPLuchador += luchGanador.getBonusAlinDrop() / 100f
                    }
                    if (MainServidor.RATE_DROP_ABONADOS > 1) {
                        if (pjGanador.esAbonado()) {
                            coefPPLuchador += MainServidor.RATE_DROP_ABONADOS / 2f
                        }
                    }
                }
                if (MainServidor.PARAM_PERMITIR_BONUS_PELEA_AFECTEN_PROSPECCION) {
                    coefPPLuchador += coefEstrellas + coefRetoDrop
                }
                prospeccionLuchador *= coefPPLuchador.toInt()
                luchGanador.setProspeccion(prospeccionLuchador)
                prospeccionEquipo += prospeccionLuchador
                // luchGanador.setPorcAdicDrop((int) (prospeccionLuchador * coefDropBonus));
            }
            if (prospeccionEquipo < 1) {
                prospeccionEquipo = 1
            }
            if (equipoGanador == 1) {
                if (_tipo == Constantes.PELEA_TIPO_PVM) {
                    for (luchPerdedor in perdedores) {
                        try {
                            if (luchPerdedor.getMob() == null) {
                                mobCapturable = false
                                break
                            }
                            val mobModelo: MobModelo = luchPerdedor.getMob().getMobModelo()
                            if (mobModelo.getID() === 171 || mobModelo.getID() === 200 || mobModelo.getID() === 666) {
                                monturaSalvaje = true
                            }
                            mobCapturable = mobCapturable and mobModelo.esCapturable()
                        } catch (e: Exception) {
                            mobCapturable = false
                        }
                    }
                    if ((monturaSalvaje || mobCapturable) && !_mapaReal.esArena()) {
                        var maxNivel = 0
                        val piedraStats = StringBuilder()
                        var monturaID = 0
                        for (luchGanador in ganadores) {
                            if (luchGanador.tieneEstado(Constantes.ESTADO_CAPT_ALMAS)) {
                                if (_capturadores == null) {
                                    _capturadores = ArrayList(8)
                                }
                                _capturadores.add(luchGanador)
                            }
                            if (luchGanador.tieneEstado(Constantes.ESTADO_DOMESTICACIÓN)) {
                                if (_domesticadores == null) {
                                    _domesticadores = ArrayList(8)
                                }
                                _domesticadores.add(luchGanador)
                            }
                        }
                        if (_capturadores == null || _capturadores!!.isEmpty()) {
                            mobCapturable = false
                        }
                        if (_domesticadores == null || _domesticadores!!.isEmpty()) {
                            monturaSalvaje = false
                        }
                        var objPiedraModID = 7010
                        if (monturaSalvaje || mobCapturable) {
                            for (luchPerdedor in perdedores) {
                                try {
                                    val mob: MobGrado = luchPerdedor.getMob()
                                    if (luchPerdedor.getMob() == null) {
                                        continue
                                    }
                                    val m: Int = luchPerdedor.getMob().getIDModelo()
                                    if (monturaSalvaje) {
                                        if (m == 171 || m == 200 || m == 666) {
                                            if (monturaID == 0 || Formulas.getRandomBoolean()) {
                                                monturaID = m
                                            }
                                        }
                                    }
                                    if (mobCapturable) {
                                        when (mob.getMobModelo().getTipoMob()) {
                                            Constantes.MOB_TIPO_LOS_ARCHIMONSTRUOS -> if (objPiedraModID == 7010) {
                                                objPiedraModID = 10418
                                            }
                                        }
                                        if (mob.getMobModelo().getID() === 423) {
                                            objPiedraModID = 9720
                                        }
                                        if (piedraStats.length() > 0) {
                                            piedraStats.append(",")
                                        }
                                        piedraStats.append(Integer.toHexString(Constantes.STAT_INVOCA_MOB).toString() + "#"
                                                + Integer.toHexString(luchPerdedor.getNivel()) + "#0#"
                                                + Integer.toHexString(m))
                                        if (luchPerdedor.getNivel() > maxNivel) {
                                            maxNivel = luchPerdedor.getNivel()
                                        }
                                    }
                                } catch (e: Exception) {
                                }
                            }
                        }
                        if (monturaSalvaje) {
                            for (luchCapt in _domesticadores!!) {
                                try {
                                    val persoCapt: Personaje = luchCapt.getPersonaje()
                                    val redCapt: Objeto = persoCapt.getObjPosicion(Constantes.OBJETO_POS_ARMA)
                                    if (redCapt != null && redCapt.getObjModelo().getTipo() === Constantes.OBJETO_TIPO_RED_CAPTURA && persoCapt.getMontura() == null) {
                                        val suerteCaptura: Int = (luchCapt.getValorPorBuffsID(751)
                                                + (MainServidor.RATE_CAPTURA_MONTURA
                                                * redCapt.getStatValor(Constantes.STAT_DOMESTICAR_MONTURA)))
                                        if (Formulas.getRandomInt(1, 100) <= suerteCaptura) {
                                            persoCapt.borrarOEliminarConOR(redCapt.getID(), false)
                                            val color: Int = Constantes.getColorMonturaPorMob(monturaID)
                                            val montura = Montura(color, luchCapt.getID(), false, true)
                                            val pergamino: Objeto = montura.getObjModCertificado().crearObjeto(1,
                                                    Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM)
                                            if (MainServidor.PARAM_CAPTURAR_MONTURA_COMO_PERGAMINO) {
                                                pergamino.fijarStatValor(Constantes.STAT_CONSULTAR_MONTURA,
                                                        Math.abs(montura.getID()))
                                                pergamino.addStatTexto(Constantes.STAT_PERTENECE_A,
                                                        "0#0#0#" + luchCapt.getNombre())
                                                pergamino.addStatTexto(Constantes.STAT_NOMBRE,
                                                        "0#0#0#" + montura.getNombre())
                                                montura.setMapaCelda(null, null)
                                            } else {
                                                persoCapt.setMontura(montura)
                                            }
                                            luchCapt.addDropLuchador(pergamino,
                                                    MainServidor.PARAM_CAPTURAR_MONTURA_COMO_PERGAMINO)
                                            break
                                        }
                                    }
                                } catch (e: Exception) {
                                }
                            }
                        }
                        if (mobCapturable) {
                            for (luchCapt in _capturadores!!) {
                                try { // falta agregar al azar
                                    val persoCapt: Personaje = luchCapt.getPersonaje()
                                    val piedra: Objeto = persoCapt.getObjPosicion(Constantes.OBJETO_POS_ARMA)
                                    if (piedra != null && piedra.getObjModelo()
                                                    .getTipo() === Constantes.OBJETO_TIPO_PIEDRA_DEL_ALMA) {
                                        val nivelPiedra: Int = Integer.parseInt(
                                                piedra.getParamStatTexto(Constantes.STAT_POTENCIA_CAPTURA_ALMA, 3), 16)
                                        if (nivelPiedra >= maxNivel) {
                                            val sPiedra: Int = Integer.parseInt(
                                                    piedra.getParamStatTexto(Constantes.STAT_POTENCIA_CAPTURA_ALMA, 1),
                                                    16)
                                            val suerte: Int = luchCapt
                                                    .getValorPorBuffsID(Constantes.STAT_BONUS_CAPTURA_ALMA) + sPiedra
                                            if (suerte >= Formulas.getRandomInt(1, 100)) {
                                                persoCapt.borrarOEliminarConOR(piedra.getID(), false)
                                                luchCapt.addDropLuchador(Objeto(0, objPiedraModID, 1,
                                                        Constantes.OBJETO_POS_NO_EQUIPADO, piedraStats.toString(), 0,
                                                        0), true)
                                                break
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                }
                            }
                        }
                    }
                }
                if ((_tipo == Constantes.PELEA_TIPO_PVM || _tipo == Constantes.PELEA_TIPO_PVM_NO_ESPADA)
                        && _retos != null) {
                    for (entry in _retos.entrySet()) {
                        val reto: Reto = entry.getValue()
                        if (reto.getEstado() === Reto.EstReto.REALIZADO) {
                            if (MainServidor.PARAM_PERMITIR_BONUS_DROP_RETOS) {
                                coefRetoDrop += reto.bonusDrop() / 100f
                            }
                            if (MainServidor.PARAM_PERMITIR_BONUS_EXP_RETOS) {
                                coefRetoXP += reto.bonusXP() / 100f
                            }
                        }
                    }
                }
                try {
                    var nivelPromMobs = 0
                    if (_tipo == Constantes.PELEA_TIPO_PVM || _tipo == Constantes.PELEA_TIPO_PVM_NO_ESPADA) {
                        var mob: MobGrado?
                        var cant = 0
                        for (luchPerdedor in perdedores) {
                            if (luchPerdedor.esInvocacion() || luchPerdedor.getMob().also { mob = it } == null) {
                                continue
                            }
                            if (MainServidor.PARAM_SISTEMA_ORBES) {
                                if (MainServidor.MOBS_NO_ORBES.contains(mob.getIDModelo())) {
                                    continue
                                }
                                var cantidad = 0
                                val nivel: Int = mob.getNivel()
                                if (nivel >= 1 && nivel <= 50) {
                                    cantidad += Math.floor(nivel * 0.1)
                                } else if (nivel >= 51 && nivel <= 100) {
                                    cantidad += Math.floor(nivel * 0.13)
                                } else if (nivel >= 101 && nivel <= 130) {
                                    cantidad += Math.floor(nivel * 0.16)
                                } else if (nivel >= 131 && nivel <= 150) {
                                    cantidad += Math.floor(nivel * 0.19)
                                } else if (nivel >= 151 && nivel <= 200) {
                                    cantidad += Math.floor(nivel * 0.21)
                                } else {
                                    cantidad += Math.floor(nivel * 0.25)
                                }
                                if (MainServidor.MOBS_DOBLE_ORBES.contains(mob.getIDModelo())) {
                                    cantidad *= 2
                                }
                                val drop = DropMob(MainServidor.ID_ORBE, 0, 99.99f, cantidad, "", false)
                                addDropPelea(drop)
                            } else {
                                // drops de recursos, objetos, etc normales
                                cant++
                                nivelPromMobs += mob.getNivel()
                                minkamas += mob.getMobGradoModelo().getMinKamas()
                                maxkamas += mob.getMobGradoModelo().getMaxKamas()
                                for (drop in mob.getMobModelo().getDrops()) {
                                    if (drop.getProspeccion() === 0 || drop.getProspeccion() <= prospeccionEquipo) {
                                        addDropPelea(drop)
                                    }
                                }
                            }
                        }
                        if (cant > 0) {
                            nivelPromMobs = nivelPromMobs / cant
                        }
                    }
                    if (_tipo == Constantes.PELEA_TIPO_PVM) { // drops fijos
                        for (drop in Mundo.listaDropsFijos()) {
                            // armas etereas, materias, dominios
                            if (drop.getNivelMin() <= nivelPromMobs && nivelPromMobs <= drop.getNivelMax()) {
                                addDropPelea(drop)
                            }
                        }
                    }
                } catch (e: Exception) {
                }
                // hasta aqui acaba todo lo q tiene q ver con ganadores
            }
            val todosConPP: Map<Integer?, Luchador?> = TreeMap<Integer?, Luchador?>()
            var prospTemp: Int
            var tempPP: Int
            val dropeadores: ArrayList<Luchador?> = ArrayList()
            val ordenLuchMasAMenosPP: ArrayList<Luchador?> = ArrayList<Luchador?>()
            for (luchGanador in ganadores) {
                prospTemp = luchGanador.getProspeccionLuchador()
                while (todosConPP.containsKey(prospTemp)) {
                    prospTemp += 1
                }
                todosConPP.put(prospTemp, luchGanador)
            }
            while (ordenLuchMasAMenosPP.size() < ganadores.size()) {
                tempPP = -1
                for (entry in todosConPP.entrySet()) {
                    if (entry.getKey() > tempPP && !ordenLuchMasAMenosPP.contains(entry.getValue())) {
                        lucConMaxPP = entry.getValue()
                        tempPP = entry.getKey()
                    }
                }
                ordenLuchMasAMenosPP.add(lucConMaxPP)
            }
            if (_objetosRobados != null) {
                for (obj in _objetosRobados) {
                    if (dropRobado == null) {
                        dropRobado = ArrayList()
                    }
                    if (obj.getCantidad() > 1) {
                        for (i in 1..obj.getCantidad()) {
                            dropRobado.add(obj.clonarObjeto(1, Constantes.OBJETO_POS_NO_EQUIPADO))
                        }
                    } else {
                        dropRobado.add(obj)
                    }
                }
                _objetosRobados.clear()
            }
            // solo para pjs o mobs en heroico
            for (luch in ordenLuchMasAMenosPP) {
                if (luch.esDoble()) {
                    continue
                }
                if (luch.getPersonaje() != null && luch.getPersonaje().esMultiman()) {
                    continue
                }
                if (luch.esInvocacion()) {
                    if (luch.getMob() == null || luch.getMob().getIDModelo() !== 285) { // cofre
                        continue
                    }
                }
                if (luch.getMob() != null) {
                    if (luch.getMob().getIDModelo() === 394) { // caballero
                        continue
                    }
                }
                dropeadores.add(luch)
            }
            var ganarHonor = 0
            val deshonor = 0
            var xpParaGremio: Long = 0
            var xpParaMontura: Long = 0
            val cantGanadores: Int = dropeadores.size()
            var strDrops: StringBuilder? = StringBuilder()
            // DROPEANDO LOS OBJETOS DE SERVER HEROICO O RECAUDADOR
            // if (MainServidor.MODO_HEROICO ||
            // MainServidor.MAPAS_MODO_HEROICO.contains(_mapaReal.getID())) {
            repartirDropRobado(dropRobado, dropeadores)
            // }
            var tempCantDropeadores: Int = dropeadores.size()
            val recaudador: Recaudador = _mapaReal.getRecaudador()
            if (equipoGanador == 1) {
                if (_posiblesBotinPelea != null) {
                    if (MainServidor.MODO_DEBUG) {
                        System.out.println("========== START DROPS PLAYERS ===========")
                        System.out.println("PROSPECCION DEL EQUIPO ES " + prospeccionEquipo)
                    }
                    for (drop in _posiblesBotinPelea) {
                        if (MainServidor.MODO_DEBUG) {
                            System.out.println("===========================================")
                            System.out.println("Posibilidad Drop (" + drop.getIDObjModelo().toString() + ") "
                                    + Mundo.getObjetoModelo(drop.getIDObjModelo()).getNombre().toString() + " , MaximoDrop: "
                                    + drop.getBotinMaximo().toString() + " , DropFijo: " + drop.esDropFijo())
                        }
                        val maxDrop: Int = drop.getBotinMaximo()
                        if (maxDrop <= 0) {
                            continue
                        }
                        //boolean repartido = false;
                        if (drop.getProspeccionBotin() === 0 && drop.getPorcentajeBotin() >= 100) {
                            System.out.println("100%")
                            // cada jugador dropea la maxima cantidad
                            for (lucha in dropeadores) {
                                var dropTemp: Luchador? = null
                                if (lucha.getPersonaje() == null && lucha.getInvocador().getPersonaje() != null) {
                                    dropTemp = lucha.getInvocador()
                                } else if (lucha.getPersonaje() != null) {
                                    dropTemp = lucha
                                }
                                if (dropTemp != null) {
                                    if (Condiciones.validaCondiciones(dropTemp.getPersonaje(), drop.getCondicionBotin(), null)) {
                                        for (i in 0 until maxDrop) {
                                            lucha.addDropLuchador(Mundo.getObjetoModelo(drop.getIDObjModelo()).crearObjeto(
                                                    1, Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM), true)
                                        }
                                    }
                                } else {
                                    if (drop.getCondicionBotin().isEmpty()) {
                                        for (i in 0 until maxDrop) {
                                            lucha.addDropLuchador(Mundo.getObjetoModelo(drop.getIDObjModelo()).crearObjeto(
                                                    1, Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM), true)
                                        }
                                    }
                                }
                            }
                        } else if (drop.esUnico()) {
                            var nuevoMaxDrop = maxDrop
                            if (drop.getProspeccionBotin() > 0) {
                                nuevoMaxDrop = 0
                                for (m in 1..maxDrop) {
                                    val fSuerte: Float = Formulas.getRandomDecimal(3) // 0.001 - 100.000
                                    // si es drop etero o no
                                    val fPorc: Float = Formulas.getPorcParaDropAlEquipo(prospeccionEquipo, coefEstrellas,
                                            coefRetoDrop, drop, dropeadores.size())
                                    if (MainServidor.MODO_DEBUG) {
                                        System.out.println(" -> DropItem: " + drop.getIDObjModelo().toString() + " , %Drop: "
                                                + drop.getPorcentajeBotin().toString() + " , %TeamDrop: " + fPorc.toString() + " , RandValue: "
                                                + fSuerte)
                                    }
                                    if (fPorc >= fSuerte) {
                                        nuevoMaxDrop++
                                    }
                                }
                            }
                            if (MainServidor.MODO_DEBUG) {
                                System.out.println(
                                        "Repartiendo drop " + Mundo.getObjetoModelo(drop.getIDObjModelo()).getNombre()
                                                .toString() + " (" + drop.getIDObjModelo().toString() + ") cantidad " + nuevoMaxDrop)
                            }
                            var dropsGanados = 0
                            while (dropsGanados < nuevoMaxDrop) {
                                if (!drop.getCondicionBotin().isEmpty()) {
                                    var gano = false
                                    var pasoCondicion = false
                                    for (j in 1..dropeadores.size()) {
                                        val k: Int = (tempCantDropeadores + j) % dropeadores.size()
                                        val posibleDropeador: Luchador = dropeadores.get(k)
                                        if (posibleDropeador.getPersonaje() == null
                                                || !Condiciones.validaCondiciones(posibleDropeador.getPersonaje(),
                                                        drop.getCondicionBotin(), null)) {
                                            // si no es personaje y si no cumple la condicion
                                            if (drop.getProspeccionBotin() === 0 && drop.getPorcentajeBotin() >= 100) {
                                                dropsGanados++
                                            }
                                            continue
                                        }
                                        pasoCondicion = true
                                        val nPorcAzar: Float = Formulas.getRandomDecimal(3)
                                        val porcDropFinal: Float = Formulas.getPorcDropLuchador(drop.getPorcentajeBotin(),
                                                posibleDropeador, drop.getProspeccionBotin())
                                        if (porcDropFinal >= nPorcAzar) {
                                            posibleDropeador.addDropLuchador(
                                                    Mundo.getObjetoModelo(drop.getIDObjModelo()).crearObjeto(1,
                                                            Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM),
                                                    true)
                                            tempCantDropeadores = k
                                            gano = true
                                            break
                                        }
                                    }
                                    if (!pasoCondicion) {
                                        break
                                    }
                                    if (gano) {
                                        dropsGanados++
                                    }
                                } else { // si es etereo o no tiene condicion
                                    val nPorcAzar: Int = Formulas.getRandomInt(1, prospeccionEquipo)
                                    var suma = 0
                                    var dropeador: Luchador? = null
                                    for (l in dropeadores) {
                                        suma += l.getProspeccionLuchador()
                                        if (suma >= nPorcAzar) {
                                            dropeador = l
                                            break
                                        }
                                    }
                                    if (dropeador != null) {
                                        dropeador.addDropLuchador(Mundo.getObjetoModelo(drop.getIDObjModelo()).crearObjeto(
                                                1, Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM), true)
                                        dropsGanados++
                                    }
                                }
                            }
                            drop.addBotinMaximo(-nuevoMaxDrop)
                        } else {
                            var posiblesDropMaximos = 0
                            for (m in 1..maxDrop) {
                                val fSuerte: Float = Formulas.getRandomDecimal(3) // 0.001 - 100.000
                                // si es drop etero o no
                                val fPorc: Float = Formulas.getPorcParaDropAlEquipo(prospeccionEquipo, coefEstrellas,
                                        coefRetoDrop, drop, dropeadores.size())
                                if (MainServidor.MODO_DEBUG) {
                                    System.out.println(" -> DropItem: " + drop.getIDObjModelo().toString() + " , %Drop: "
                                            + drop.getPorcentajeBotin().toString() + " , %TeamDrop: " + fPorc.toString() + " , RandValue: "
                                            + fSuerte)
                                }
                                if (fPorc >= fSuerte) {
                                    posiblesDropMaximos++
                                }
                            }
                            for (lucha in dropeadores) {
                                var dropTemp: Luchador? = null
                                if (lucha.getPersonaje() == null && lucha.getInvocador().getPersonaje() != null) {
                                    dropTemp = lucha.getInvocador()
                                } else if (lucha.getPersonaje() != null) {
                                    dropTemp = lucha
                                }
                                if (dropTemp != null) {
                                    if (Condiciones.validaCondiciones(dropTemp.getPersonaje(), drop.getCondicionBotin(), null)) {
                                        /*slucha.addDropLuchador(
												Mundo.getObjetoModelo(drop.getIDObjModelo()).crearObjeto(1,
														Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM),
												true);*/
                                        for (i in 0 until posiblesDropMaximos) {
                                            val nPorcAzar: Float = Formulas.getRandomDecimal(3)
                                            val porcDropFinal: Float = Formulas.getPorcDropLuchador(drop.getPorcentajeBotin(), lucha, drop.getProspeccionBotin())
                                            //System.out.println("Drop: "+drop.getIDObjModelo()+" % drop: "+drop.getPorcentajeBotin()+" Porcentaje de drop pelea: "+porcDropFinal);
                                            if (porcDropFinal >= nPorcAzar) {
                                                lucha.addDropLuchador(
                                                        Mundo.getObjetoModelo(drop.getIDObjModelo()).crearObjeto(1,
                                                                Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM),
                                                        true)
                                            }
                                        }
                                    }
                                } else {
                                    if (drop.getCondicionBotin().isEmpty()) {
                                        for (i in 0 until posiblesDropMaximos) {
                                            val nPorcAzar: Float = Formulas.getRandomDecimal(3)
                                            val porcDropFinal: Float = Formulas.getPorcDropLuchador(drop.getPorcentajeBotin(), lucha, drop.getProspeccionBotin())
                                            //System.out.println("Drop: "+drop.getIDObjModelo()+" % drop: "+drop.getPorcentajeBotin()+" Porcentaje de drop pelea: "+porcDropFinal);
                                            if (porcDropFinal >= nPorcAzar) {
                                                lucha.addDropLuchador(
                                                        Mundo.getObjetoModelo(drop.getIDObjModelo()).crearObjeto(1,
                                                                Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM),
                                                        true)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (MainServidor.MODO_DEBUG) {
                        System.out.println("========== FINISH DROPS PLAYERS ===========")
                    }
                }
                if (_tipo == Constantes.PELEA_TIPO_PVM && recaudador != null) {
                    strDrops = StringBuilder()
                    val luchRecau = Luchador(this, recaudador, true)
                    val gremio: Gremio = recaudador.getGremio()
                    val ppRecau: Int = gremio.getStatRecolecta(Constantes.STAT_MAS_PROSPECCION)
                    val expGanada: Long = Math.round(Formulas.getXPOficial(ganadores, perdedores, luchRecau, coefEstrellas,
                            coefRetoXP, equipoGanador == 1))
                    val kamasGanadas: Long = Formulas.getKamasGanadas(minkamas, maxkamas, null)
                    luchRecau.setKamasGanadas(kamasGanadas)
                    luchRecau.setExpGanada(expGanada)
                    recaudador.addExp(expGanada)
                    recaudador.addKamas(kamasGanadas, null)
                    // SI AUN PUEDE DROPEAR
                    if (gremio.getStatRecolecta(Constantes.STAT_MAS_PODS) > recaudador.getPodsActuales()) {
                        if (_posiblesBotinPelea != null) {
                            if (MainServidor.MODO_DEBUG) {
                                System.out.println("========== START DROPS RECAUDADOR ===========")
                                System.out.println("PROSPECCION RECAUDADOR: $ppRecau")
                            }
                            for (drop in _posiblesBotinPelea) {
                                val maxDrop: Int = drop.getBotinMaximo()
                                if (maxDrop <= 0) {
                                    continue
                                }
                                if (!drop.getCondicionBotin().isEmpty()) {
                                    continue
                                }
                                if (drop.getProspeccionBotin() > 0) {
                                    for (m in 1..maxDrop) {
                                        val fSuerte: Float = Formulas.getRandomDecimal(3)
                                        val fPorc: Float = Formulas.getPorcParaDropAlEquipo(ppRecau, coefEstrellas,
                                                coefRetoDrop, drop, 1)
                                        if (MainServidor.MODO_DEBUG) {
                                            System.out.println("DropItem: " + drop.getIDObjModelo().toString() + " , %Drop: "
                                                    + drop.getPorcentajeBotin().toString() + " , %TeamDrop: " + fPorc
                                                    .toString() + " , RandValue: " + fSuerte)
                                        }
                                        if (fPorc >= fSuerte) {
                                            val objModelo: ObjetoModelo = Mundo.getObjetoModelo(drop.getIDObjModelo())
                                            luchRecau.addDropLuchador(objModelo.crearObjeto(1,
                                                    Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM), true)
                                        }
                                    }
                                }
                            }
                            if (MainServidor.MODO_DEBUG) {
                                System.out.println("========== FIN DROPS RECAUDADOR ===========")
                            }
                        }
                        if (luchRecau.getObjDropeados() != null) {
                            for (entry in luchRecau.getObjDropeados().entrySet()) {
                                val obj: Objeto = entry.getKey()
                                if (strDrops.length() > 0) {
                                    strDrops.append(",")
                                }
                                strDrops.append(obj.getObjModeloID().toString() + "~" + obj.getCantidad())
                                if (entry.getValue()) {
                                    luchRecau.addObjetoAInventario(obj)
                                }
                            }
                        }
                    }
                    packet.append("5;" + luchRecau.getID().toString() + ";" + luchRecau.getNombre().toString() + ";" + luchRecau.getNivel()
                            .toString() + ";" + (if (luchRecau.estaMuerto()) "1" else "0").toString() + ";")
                    packet.append(luchRecau.xpStringLuch(";").toString() + ";")
                    packet.append((if (luchRecau.getExpGanada() === 0) "" else luchRecau.getExpGanada()) + ";")
                    packet.append((if (xpParaGremio == 0L) "" else xpParaGremio).toString() + ";")
                    packet.append((if (xpParaMontura == 0L) "" else xpParaMontura).toString() + ";")
                    packet.append(strDrops.toString().toString() + ";")
                    packet.append((if (luchRecau.getKamasGanadas() === 0) "" else luchRecau.getKamasGanadas()) + "|")
                }
            }
            var cuentas_g: StringBuilder? = null
            var personajes_g: StringBuilder? = null
            var ips_g: StringBuilder? = null
            var puntos_g: StringBuilder? = null
            var cuentas_p: StringBuilder? = null
            var personajes_p: StringBuilder? = null
            var ips_p: StringBuilder? = null
            var puntos_p: StringBuilder? = null
            if (MainServidor.PARAM_SALVAR_LOGS_AGRESION_SQL && _tipo == Constantes.PELEA_TIPO_PVP) {
                cuentas_g = StringBuilder()
                personajes_g = StringBuilder()
                ips_g = StringBuilder()
                puntos_g = StringBuilder()
                cuentas_p = StringBuilder()
                personajes_p = StringBuilder()
                ips_p = StringBuilder()
                puntos_p = StringBuilder()
            }
            for (luchGanador in ganadores) {
                if (luchGanador.esDoble()) {
                    continue
                }
                if (luchGanador.esInvocacion()) {
                    if (luchGanador.getMob() != null && luchGanador.getMob().getIDModelo() !== 285) { // cofre
                        continue
                    }
                }
                strDrops = StringBuilder()
                val pjGanador: Personaje = luchGanador.getPersonaje()
                ganarHonor = 0
                xpParaMontura = ganarHonor.toLong()
                xpParaGremio = xpParaMontura
                if (pjGanador != null && pjGanador.esMultiman()) {
                    // multiman no reciven ningun bonus
                } else if (!luchGanador.estaRetirado()) {
                    when (_tipo) {
                        Constantes.PELEA_TIPO_PVP, Constantes.PELEA_TIPO_PRISMA -> {
                            ganarHonor = Formulas.getHonorGanado(ganadores, perdedores, luchGanador, _mobGrupo != null)
                            if (_1vs1) {
                                var div = 0
                                if (luchGanador.getID() === _luchInit1.getID()) {
                                    // mientras mas se agrade menos honor se ganar
                                    try {
                                        for (t in luchGanador.getPersonaje().getAgredirA(_luchInit2.getNombre())) {
                                            if (t + MainServidor.MISION_PVP_TIEMPO > System.currentTimeMillis()) {
                                                div++
                                            }
                                        }
                                    } catch (e: Exception) {
                                    }
                                } else if (luchGanador.getID() === _luchInit2.getID()) {
                                    try {
                                        for (t in luchGanador.getPersonaje().getAgredidoPor(_luchInit1.getNombre())) {
                                            if (t + MainServidor.MISION_PVP_TIEMPO > System.currentTimeMillis()) {
                                                div++
                                            }
                                        }
                                    } catch (e: Exception) {
                                    }
                                }
                                if (div < 1) {
                                    div = 1
                                }
                                ganarHonor = if (div > 3) {
                                    0
                                } else {
                                    ganarHonor / div
                                }
                            }
                            if (ganarHonor > 0) {
                                luchGanador.getPreLuchador().addDeshonor(-1)
                                luchGanador.getPreLuchador().addHonor(ganarHonor)
                                if (MainServidor.MODO_SOLO_PVP) {
                                    val objetos = StringBuilder()
                                    objetos.append(MainServidor.MISION_PVP_OBJETOS)
                                    for (s in objetos.toString().split(";")) {
                                        try {
                                            if (s.isEmpty()) {
                                                continue
                                            }
                                            val id: Int = Integer.parseInt(s.split(",").get(0))
                                            val cant: Int = Integer.parseInt(s.split(",").get(1))
                                            val obj: Objeto = Mundo.getObjetoModelo(id).crearObjeto(cant,
                                                    Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM)
                                            luchGanador.addDropLuchador(obj, true)
                                        } catch (e: Exception) {
                                        }
                                    }
                                }
                            } else if (ganarHonor < 0) {
                                ganarHonor = 0
                            }
                        }
                        Constantes.PELEA_TIPO_PVM -> {
                            if (pjGanador != null) {
                                val arma: Objeto = pjGanador.getObjPosicion(Constantes.OBJETO_POS_ARMA)
                                val oficio: StatOficio = pjGanador.getStatOficioPorID(Constantes.OFICIO_CAZADOR)
                                if (arma != null && oficio != null) {
                                    val nivelOficio: Int = oficio.getNivel()
                                    for (mob in perdedores) {
                                        try {
                                            if (mob.esInvocacion() || mob.getMob() == null) continue
                                            val carne: Int = Constantes.getCarnePorMob(mob.getMob().getIDModelo(), nivelOficio)
                                            if (carne != -1) {
                                                val cant: Int = Formulas.getRandomInt(1,
                                                        Math.max(1, luchGanador.getProspeccionLuchador() / 100))
                                                luchGanador.addDropLuchador(
                                                        Mundo.getObjetoModelo(carne).crearObjeto(cant,
                                                                Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM),
                                                        true)
                                            }
                                        } catch (e: Exception) {
                                        }
                                    }
                                }
                            }
                            luchGanador.setKamasGanadas(luchGanador.getKamasGanadas()
                                    + Formulas.getKamasGanadas(minkamas, maxkamas, luchGanador.getPersonaje()))
                            if (pjGanador != null) {
                                val expGanada: Long = Formulas.getXPOficial(ganadores, perdedores, luchGanador, coefEstrellas,
                                        coefRetoXP, equipoGanador == 1)
                                luchGanador.setExpGanada(luchGanador.getExpGanada() + expGanada)
                                if (pjGanador.realizoMisionDelDia()) {
                                    val almanax: Almanax = Mundo.getAlmanaxDelDia()
                                    if (almanax != null) {
                                        if (almanax.getTipo() === Constantes.ALMANAX_BONUS_KAMAS) {
                                            luchGanador.setKamasGanadas(luchGanador.getKamasGanadas()
                                                    + luchGanador.getKamasGanadas() * almanax.getBonus() / 100)
                                        }
                                        if (almanax.getTipo() === Constantes.ALMANAX_BONUS_EXP_PJ) {
                                            val expBonus: Float = luchGanador.getExpGanada() * almanax.getBonus() / 100
                                            luchGanador.setExpGanada((luchGanador.getExpGanada() + expBonus) as Long)
                                        }
                                    }
                                }
                                if (pjGanador.alasActivadas() && _alinPelea == pjGanador.getAlineacion()) {
                                    val expBonus = (luchGanador.getExpGanada() * luchGanador.getBonusAlinExp()
                                            / 100) as Long
                                    luchGanador.setExpGanada(luchGanador.getExpGanada() + expBonus)
                                    val kamasBonus = (luchGanador.getKamasGanadas() * luchGanador.getBonusAlinDrop()
                                            / 100) as Long
                                    luchGanador.setKamasGanadas(luchGanador.getKamasGanadas() + kamasBonus)
                                }
                                if (pjGanador.getMiembroGremio() != null) {
                                    xpParaGremio = (luchGanador.getExpGanada()
                                            * pjGanador.getMiembroGremio().getPorcXpDonada() / 100f)
                                    luchGanador.setExpGanada(luchGanador.getExpGanada() - xpParaGremio)
                                    if (xpParaGremio > 0) {
                                        xpParaGremio = Formulas.getXPDonada(pjGanador.getNivel(),
                                                pjGanador.getMiembroGremio().getGremio().getNivel(), xpParaGremio)
                                        pjGanador.getMiembroGremio().darXpAGremio(xpParaGremio)
                                    }
                                }
                                if (pjGanador.getMontura() != null) {
                                    xpParaMontura = (luchGanador.getExpGanada() * pjGanador.getPorcXPMontura()
                                            / 100)
                                    xpParaMontura = xpParaMontura * pjGanador.getMontura().velocidadAprendizaje() / 100
                                    luchGanador.setExpGanada(luchGanador.getExpGanada() - xpParaMontura)
                                    if (xpParaMontura > 0) {
                                        xpParaMontura = (Formulas.getXPDonada(pjGanador.getNivel(),
                                                pjGanador.getMontura().getNivel(), xpParaMontura)
                                                * MainServidor.RATE_XP_MONTURA)
                                        pjGanador.getMontura().addExperiencia(xpParaMontura)
                                    }
                                    GestorSalida.ENVIAR_Re_DETALLES_MONTURA(pjGanador, "+", pjGanador.getMontura())
                                }
                            }
                        }
                        Constantes.PELEA_TIPO_PVM_NO_ESPADA -> {
                            luchGanador.setKamasGanadas(luchGanador.getKamasGanadas()
                                    + Formulas.getKamasGanadas(minkamas, maxkamas, luchGanador.getPersonaje()))
                            if (pjGanador != null) {
                                val expGanada: Long = Formulas.getXPOficial(ganadores, perdedores, luchGanador, coefEstrellas,
                                        coefRetoXP, equipoGanador == 1)
                                luchGanador.setExpGanada(luchGanador.getExpGanada() + expGanada)
                                if (pjGanador.realizoMisionDelDia()) {
                                    val almanax: Almanax = Mundo.getAlmanaxDelDia()
                                    if (almanax != null) {
                                        if (almanax.getTipo() === Constantes.ALMANAX_BONUS_KAMAS) {
                                            luchGanador.setKamasGanadas(luchGanador.getKamasGanadas()
                                                    + luchGanador.getKamasGanadas() * almanax.getBonus() / 100)
                                        }
                                        if (almanax.getTipo() === Constantes.ALMANAX_BONUS_EXP_PJ) {
                                            val expBonus: Float = luchGanador.getExpGanada() * almanax.getBonus() / 100
                                            luchGanador.setExpGanada((luchGanador.getExpGanada() + expBonus) as Long)
                                        }
                                    }
                                }
                                if (pjGanador.alasActivadas() && _alinPelea == pjGanador.getAlineacion()) {
                                    val expBonus = (luchGanador.getExpGanada() * luchGanador.getBonusAlinExp()
                                            / 100) as Long
                                    luchGanador.setExpGanada(luchGanador.getExpGanada() + expBonus)
                                    val kamasBonus = (luchGanador.getKamasGanadas() * luchGanador.getBonusAlinDrop()
                                            / 100) as Long
                                    luchGanador.setKamasGanadas(luchGanador.getKamasGanadas() + kamasBonus)
                                }
                                if (pjGanador.getMiembroGremio() != null) {
                                    xpParaGremio = (luchGanador.getExpGanada()
                                            * pjGanador.getMiembroGremio().getPorcXpDonada() / 100f)
                                    luchGanador.setExpGanada(luchGanador.getExpGanada() - xpParaGremio)
                                    if (xpParaGremio > 0) {
                                        xpParaGremio = Formulas.getXPDonada(pjGanador.getNivel(),
                                                pjGanador.getMiembroGremio().getGremio().getNivel(), xpParaGremio)
                                        pjGanador.getMiembroGremio().darXpAGremio(xpParaGremio)
                                    }
                                }
                                if (pjGanador.getMontura() != null) {
                                    xpParaMontura = (luchGanador.getExpGanada() * pjGanador.getPorcXPMontura()
                                            / 100)
                                    xpParaMontura = xpParaMontura * pjGanador.getMontura().velocidadAprendizaje() / 100
                                    luchGanador.setExpGanada(luchGanador.getExpGanada() - xpParaMontura)
                                    if (xpParaMontura > 0) {
                                        xpParaMontura = (Formulas.getXPDonada(pjGanador.getNivel(),
                                                pjGanador.getMontura().getNivel(), xpParaMontura)
                                                * MainServidor.RATE_XP_MONTURA)
                                        pjGanador.getMontura().addExperiencia(xpParaMontura)
                                    }
                                    GestorSalida.ENVIAR_Re_DETALLES_MONTURA(pjGanador, "+", pjGanador.getMontura())
                                }
                            }
                        }
                        Constantes.PELEA_TIPO_RECAUDADOR -> if (pjGanador != null) {
                            luchGanador.setExpGanada(0)
                            /*
								 * luchGanador.getPersonaje().PuntosPrestigio += MainServidor.ptsRecaudador;
								 * luchGanador.getPersonaje().sendMessage("Has ganado <b>" +
								 * MainServidor.ptsRecaudador + "</b> Pts. por pelea Recaudador");
								 */if (pjGanador.getMiembroGremio() != null) {
                                xpParaGremio = luchGanador.getExpGanada()
                                if (xpParaGremio > 0) {
                                    xpParaGremio = Formulas.getXPDonada(pjGanador.getNivel(),
                                            pjGanador.getMiembroGremio().getGremio().getNivel(), xpParaGremio)
                                    pjGanador.getMiembroGremio().darXpAGremio(xpParaGremio)
                                }
                            }
                        }
                        Constantes.PELEA_TIPO_KOLISEO -> {
                            if (!entregoLogro) {
                                for (luch in ganadores) {
                                    if (luch.getPersonaje() == null) continue
                                    val random: Int = Formulas.getRandomValor(5, 10)
                                    luch.getPersonaje().addPuntos(random)
                                    Sucess.launch(luch.getPersonaje(), 11.toByte(), null, 0)
                                    luch.getPersonaje().confirmarPeleaPVP("1")
                                }
                                for (luch in perdedores) { // prueba
                                    if (luch.getPersonaje() == null) continue
                                    luch.getPersonaje().restarPuntos(8)
                                    luch.getPersonaje().confirmarPeleaPVP("0")
                                }
                                entregoLogro = true
                            }
                            if (pjGanador != null) {
                                // luchGanador.setExpGanada(luchGanador.getExpGanada() +
                                // Formulas.getXPMision(pjGanador.getNivel()) /
                                // MainServidor.KOLISEO_DIVISOR_XP);
                                if (MainServidor.KOLISEO_PREMIO_KAMAS > 0) {
                                    luchGanador.setKamasGanadas(
                                            luchGanador.getKamasGanadas() + MainServidor.KOLISEO_PREMIO_KAMAS)
                                }
                                for (s in MainServidor.KOLISEO_PREMIO_OBJETOS.split(";")) {
                                    try {
                                        val objID: Int = Integer.parseInt(s.split(",").get(0))
                                        val cant: Int = Integer.parseInt(s.split(",").get(1))
                                        luchGanador.addDropLuchador(Mundo.getObjetoModelo(objID).crearObjeto(cant,
                                                Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM), true)
                                    } catch (e: Exception) {
                                    }
                                }
                                if (MainServidor.KOLISEO_PREMIO_OBJETOS_ALEATORIOS.length() > 0) {
                                    try {
                                        val objetos: Array<String?> = MainServidor.KOLISEO_PREMIO_OBJETOS_ALEATORIOS.split(";")
                                        val indice: Int = Formulas.getRandomInt(0, objetos.size - 1)
                                        val s = objetos[indice]
                                        val objID: Int = Integer.parseInt(s.split(",").get(0))
                                        val cant: Int = Integer.parseInt(s.split(",").get(1))
                                        luchGanador.addDropLuchador(Mundo.getObjetoModelo(objID).crearObjeto(cant,
                                                Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM), true)
                                    } catch (e: Exception) {
                                    }
                                }
                            }
                        }
                        Constantes.PELEA_TIPO_CACERIA -> if (pjGanador != null) {
                            try {
                                val str: Array<String?> = Mundo.KAMAS_OBJ_CACERIA.split(Pattern.quote("|"))
                                luchGanador.setKamasGanadas(
                                        luchGanador.getKamasGanadas() + Integer.parseInt(str[0]) / cantGanadores)
                                if (str.size > 1) {
                                    for (s in str[1].split(";")) {
                                        try {
                                            val objID: Int = Integer.parseInt(s.split(",").get(0))
                                            val cant: Int = Integer.parseInt(s.split(",").get(1))
                                            luchGanador.addDropLuchador(
                                                    Mundo.getObjetoModelo(objID).crearObjeto(cant,
                                                            Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM),
                                                    true)
                                        } catch (e: Exception) {
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                            }
                        }
                        Constantes.PELEA_TIPO_DESAFIO -> if (MainServidor.PARAMS_XP_DESAFIO) {
                            if (pjGanador != null) {
                                val experiencia: Int = Formulas.getExperienciaGanada(ganadores, perdedores, pjGanador)
                                luchGanador.setExpGanada(experiencia)
                            }
                        }
                    }
                    // AQUI CONVIERTE LOS OBJ DROPS AL INVENTARIO DE CADA GANADOR
                    // iddrod

                    //
                    if (luchGanador.getObjDropeados() != null) {
                        for (entry in luchGanador.getObjDropeados().entrySet()) {
                            val obj: Objeto = entry.getKey()
                            if (strDrops.length() > 0) {
                                strDrops.append(",")
                            }
                            strDrops.append(obj.getObjModeloID().toString() + "~" + obj.getCantidad())
                            if (entry.getValue()) {
                                luchGanador.addObjetoAInventario(obj)
                            }
                        }
                        var recibidor: Personaje = pjGanador
                        if (luchGanador.esInvocacion()) {
                            recibidor = luchGanador.getInvocador().getPersonaje()
                        }
                        if (recibidor != null) {
                            if (recibidor.enLinea()) {
                                val oako = StringBuilder()
                                for (entry in recibidor.getDropsPelea().entrySet()) {
                                    if (entry.getValue()) {
                                        oako.append(entry.getKey().stringObjetoConGuiño())
                                    } else {
                                        GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(recibidor, entry.getKey())
                                    }
                                }
                                if (oako.length() > 0) {
                                    GestorSalida.ENVIAR_OAKO_APARECER_MUCHOS_OBJETOS(recibidor, oako.toString())
                                }
                                GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(recibidor)
                            }
                            recibidor.getDropsPelea().clear()
                        }
                    }
                    if (pjGanador != null) {
                        if (esMapaHeroico()) {
                            if (pjGanador.getNivel() < pjGanador.getUltimoNivel()) {
                                luchGanador.setExpGanada(luchGanador.getExpGanada() * 2)
                            }
                        }
                        if (MainServidor.BONUS_UNICA_IP > 0) {
                            val ips: ArrayList<String?> = ArrayList<String?>()
                            for (a in getOrdenLuchadores()!!) {
                                if (a.getEquipoBin() === luchGanador.getEquipoBin() && !a.esMultiman()
                                        && !a.estaRetirado() && a.getPersonaje() != null) {
                                    if (!ips.contains(a.getPersonaje().getCuenta().getActualIP())) {
                                        ips.add(a.getPersonaje().getCuenta().getActualIP())
                                    }
                                }
                            }
                            if (ips.size() > 1) {
                                luchGanador.setExpGanada(luchGanador.getExpGanada()
                                        * (1 + MainServidor.BONUS_UNICA_IP * ips.size()) as Long)
                                luchGanador.setKamasGanadas(luchGanador.getKamasGanadas()
                                        * (1 + MainServidor.BONUS_UNICA_IP * ips.size()) as Long)
                                GestorSalida.ENVIAR_cs_CHAT_MENSAJE(pjGanador,
                                        "Se detectaron: " + ips.size()
                                                .toString() + " ips diferentes en su equipo, se le ha otorgado un bonus de: "
                                                + (ips.size() * MainServidor.BONUS_UNICA_IP).toString() + "% de exp y kamas extra",
                                        Constantes.COLOR_NEGRO)
                            }
                        }
                        val exp: Long = luchGanador.getExpGanada()
                        pjGanador.addExperiencia(exp, tipoX.toInt() == 1)
                    }
                    luchGanador.addKamasLuchador()
                }
                var hola: Int = luchGanador.getNivel()
                var esOmega = 0
                var Experiencia: Long = 0
                var BonusMono: Long = 0
                if (luchGanador.getPersonaje() != null) {
                    Experiencia = if (luchGanador.getPersonaje().bloquearXP) {
                        0
                    } else if (luchGanador.getPersonaje().getResets() < 14) {
                        0
                    } else {
                        luchGanador.getExpGanada()
                    }
                    if (luchGanador.getPersonaje().getNivelOmega() > 0) {
                        hola = luchGanador.getPersonaje().getNivelOmega()
                        esOmega = luchGanador.getPersonaje().getNivelOmega()
                    }
                    luchGanador.getPersonaje().setNbFightWin(luchGanador.getPersonaje().getNbFightWin() + 1)
                    Sucess.launch(luchGanador.getPersonaje(), 2.toByte(), null, 0)
                    BonusMono = luchGanador.getPersonaje().getExpMono()
                } else {
                    hola = luchGanador.getNivel()
                    esOmega = 0
                }
                packet.append("2;").append(luchGanador.getID()).append(";").append(luchGanador.getNombre()).append(";")
                        .append(hola).append(";").append(esOmega).append(";").append(BonusMono)
                packet.append(";").append(if (luchGanador.estaMuerto()) 1 else 0).append(";")
                if (tipoX.toInt() == 0) { // PVM -> SIN HONOR
                    packet.append(luchGanador.xpStringLuch(";")).append(";")
                    packet.append(if (luchGanador.getExpGanada() === 0) "" else Experiencia).append(";")
                    packet.append(if (xpParaGremio == 0L) "" else xpParaGremio).append(";")
                    packet.append(if (xpParaMontura == 0L) "" else xpParaMontura).append(";")
                    packet.append(strDrops.toString()).append(";")
                    packet.append(luchGanador.getKamasGanadas()).append("|")
                } else { // PVP -> CON HONOR
                    if (MainServidor.PARAM_SALVAR_LOGS_AGRESION_SQL && _tipo == Constantes.PELEA_TIPO_PVP) {
                        if (luchGanador.getPersonaje() != null && luchGanador.getPersonaje().getCuenta() != null) {
                            if (cuentas_g.length() > 0) {
                                cuentas_g.append(",")
                                personajes_g.append(",")
                                ips_g.append(",")
                                puntos_g.append(",")
                            }
                            cuentas_g.append(luchGanador.getPersonaje().getCuentaID())
                            personajes_g.append(luchGanador.getID())
                            ips_g.append(luchGanador.getPersonaje().getCuenta().getActualIP())
                            puntos_g.append(ganarHonor)
                        }
                    }
                    packet.append(stringHonor(luchGanador).toString() + ";")
                    packet.append("$ganarHonor;")
                    packet.append(luchGanador.getNivelAlineacion().toString() + ";")
                    packet.append(luchGanador.getPreLuchador().getDeshonor().toString() + ";")
                    packet.append("$deshonor;")
                    packet.append(strDrops.toString().toString() + ";")
                    packet.append(luchGanador.getKamasGanadas().toString() + ";")
                    packet.append(luchGanador.xpStringLuch(";").toString() + ";")
                    packet.append(luchGanador.getExpGanada().toString() + "|")
                }
            }
            // -----------
            // PERDODORES
            // -----------
            for (luchPerdedor in perdedores) {
                if (luchPerdedor.esDoble()) {
                    continue
                }
                if (luchPerdedor.esInvocacion()) {
                    if (luchPerdedor.getMob() != null && luchPerdedor.getMob().getIDModelo() !== 285) { // cofre
                        continue
                    }
                }
                strDrops = StringBuilder()
                val pjPerdedor: Personaje = luchPerdedor.getPersonaje()
                ganarHonor = 0
                xpParaMontura = ganarHonor.toLong()
                xpParaGremio = xpParaMontura
                if (pjPerdedor != null) {
                    if (pjPerdedor.esMultiman()) {
                        // multiman no reciven ningun bonus
                    } else if (!luchPerdedor.estaRetirado()) {
                        when (_tipo) {
                            Constantes.PELEA_TIPO_PVP, Constantes.PELEA_TIPO_PRISMA -> {
                                ganarHonor = Formulas.getHonorGanado(ganadores, perdedores, luchPerdedor,
                                        _mobGrupo != null)
                                if (_1vs1) {
                                    var div = 0
                                    if (luchPerdedor.getID() === _luchInit1.getID()) {
                                        try {
                                            for (t in luchPerdedor.getPersonaje().getAgredirA(_luchInit2.getNombre())) {
                                                if (t + 1000 * 60 * 60 * 24 > System.currentTimeMillis()) {
                                                    div++
                                                }
                                            }
                                        } catch (e: Exception) {
                                        }
                                    } else if (luchPerdedor.getID() === _luchInit2.getID()) {
                                        try {
                                            for (t in luchPerdedor.getPersonaje()
                                                    .getAgredidoPor(_luchInit1.getNombre())) {
                                                if (t + 1000 * 60 * 60 * 24 > System.currentTimeMillis()) {
                                                    div++
                                                }
                                            }
                                        } catch (e: Exception) {
                                        }
                                    }
                                    if (div < 1) {
                                        div = 1
                                    }
                                    ganarHonor = ganarHonor / div
                                }
                                if (ganarHonor < 0) {
                                    luchPerdedor.getPreLuchador().addHonor(ganarHonor)
                                }
                            }
                            Constantes.PELEA_TIPO_PVM -> {
                                var expGanada: Long
                                expGanada = try {
                                    Formulas.getXPOficial(perdedores, ganadores, luchPerdedor, coefEstrellas,
                                            coefRetoXP, equipoGanador == 1)
                                } catch (e: Exception) {
                                    100
                                }
                                luchPerdedor.setExpGanada(expGanada)
                            }
                        }
                        if (pjPerdedor.getMiembroGremio() != null) {
                            xpParaGremio = (luchPerdedor.getExpGanada()
                                    * pjPerdedor.getMiembroGremio().getPorcXpDonada() / 100f)
                            luchPerdedor.setExpGanada(luchPerdedor.getExpGanada() - xpParaGremio)
                            if (xpParaGremio > 0) {
                                xpParaGremio = Formulas.getXPDonada(pjPerdedor.getNivel(),
                                        pjPerdedor.getMiembroGremio().getGremio().getNivel(), xpParaGremio)
                                pjPerdedor.getMiembroGremio().darXpAGremio(xpParaGremio)
                            }
                        }
                        if (pjPerdedor.getMontura() != null) {
                            xpParaMontura = (luchPerdedor.getExpGanada() * pjPerdedor.getPorcXPMontura() / 100f)
                            xpParaMontura = xpParaMontura * pjPerdedor.getMontura().velocidadAprendizaje() / 100
                            luchPerdedor.setExpGanada(luchPerdedor.getExpGanada() - xpParaMontura)
                            if (xpParaMontura > 0) {
                                xpParaMontura = (Formulas.getXPDonada(pjPerdedor.getNivel(),
                                        pjPerdedor.getMontura().getNivel(), xpParaMontura)
                                        * MainServidor.RATE_XP_MONTURA)
                                pjPerdedor.getMontura().addExperiencia(xpParaMontura)
                            }
                            GestorSalida.ENVIAR_Re_DETALLES_MONTURA(pjPerdedor, "+", pjPerdedor.getMontura())
                        }
                        if (esMapaHeroico()) {
                            if (pjPerdedor.getNivel() < pjPerdedor.getUltimoNivel()) {
                                luchPerdedor.setExpGanada(luchPerdedor.getExpGanada() * 2)
                            }
                        }
                        val exp: Long = luchPerdedor.getExpGanada()
                        pjPerdedor.addExperiencia(exp, tipoX.toInt() == 1)
                        pjPerdedor.addKamas(luchPerdedor.getKamasGanadas(), false, false)
                    }
                }
                var hola: Int = luchPerdedor.getNivel()
                var esOmega = 0
                if (luchPerdedor.getPersonaje() != null) {
                    if (luchPerdedor.getPersonaje().getNivelOmega() > 0) {
                        hola = luchPerdedor.getPersonaje().getNivelOmega()
                        esOmega = luchPerdedor.getPersonaje().getNivelOmega()
                    }
                    luchPerdedor.getPersonaje().setNbFightLose(luchPerdedor.getPersonaje().getNbFightLose() + 1)
                } else {
                    hola = luchPerdedor.getNivel()
                    esOmega = 0
                }
                packet.append("0;" + luchPerdedor.getID().toString() + ";" + luchPerdedor.getNombre().toString() + ";" + hola.toString() + ";" + esOmega
                        .toString() + ";" + 0)
                packet.append(";" + (if (luchPerdedor.estaMuerto()) 1 else 0) + ";")
                if (tipoX.toInt() == 0) { // PVM -> SIN HONOR
                    packet.append(luchPerdedor.xpStringLuch(";").toString() + ";")
                    packet.append((if (luchPerdedor.getExpGanada() === 0) "" else luchPerdedor.getExpGanada()) + ";")
                    packet.append((if (xpParaGremio == 0L) "" else xpParaGremio).toString() + ";")
                    packet.append((if (xpParaMontura == 0L) "" else xpParaMontura).toString() + ";")
                    packet.append(strDrops.toString().toString() + ";")
                    packet.append((if (luchPerdedor.getKamasGanadas() === 0) "" else luchPerdedor.getKamasGanadas()) + "|")
                } else { // PVP -> CON HONOR
                    if (MainServidor.PARAM_SALVAR_LOGS_AGRESION_SQL && _tipo == Constantes.PELEA_TIPO_PVP) {
                        if (luchPerdedor.getPersonaje() != null && luchPerdedor.getPersonaje().getCuenta() != null) {
                            if (cuentas_p.length() > 0) {
                                cuentas_p.append(",")
                                personajes_p.append(",")
                                ips_p.append(",")
                                puntos_p.append(",")
                            }
                            cuentas_p.append(luchPerdedor.getPersonaje().getCuentaID())
                            personajes_p.append(luchPerdedor.getID())
                            ips_p.append(luchPerdedor.getPersonaje().getCuenta().getActualIP())
                            puntos_p.append(ganarHonor)
                        }
                    }
                    packet.append(stringHonor(luchPerdedor).toString() + ";")
                    packet.append("$ganarHonor;")
                    packet.append(luchPerdedor.getNivelAlineacion().toString() + ";")
                    packet.append(luchPerdedor.getPreLuchador().getDeshonor().toString() + ";")
                    packet.append("$deshonor;")
                    packet.append(strDrops.toString())
                    packet.append(";" + luchPerdedor.getKamasGanadas().toString() + ";")
                    packet.append(luchPerdedor.xpStringLuch(";").toString() + ";")
                    packet.append(luchPerdedor.getExpGanada().toString() + "|")
                }
            }
            if (MainServidor.PARAM_SALVAR_LOGS_AGRESION_SQL && _tipo == Constantes.PELEA_TIPO_PVP) {
                GestorSQL.INSERT_LOG_PELEA(cuentas_g.toString(), personajes_g.toString(), ips_g.toString(),
                        puntos_g.toString(), cuentas_p.toString(), personajes_p.toString(), ips_p.toString(),
                        puntos_p.toString(), tiempo / 1000, _luchInit1.getID(), _luchInit2.getID(), _mapaCopia.getID())
            }
            if (_tipo == Constantes.PELEA_TIPO_PVM) {
                _mobGrupo.puedeTimerReaparecer(_mapaReal, _mobGrupo, Aparecer.FINAL_PELEA)
            }
            if (equipoGanador == 1) {
                if (_tipo == Constantes.PELEA_TIPO_CACERIA) {
                    Mundo.NOMBRE_CACERIA = ""
                    Mundo.KAMAS_OBJ_CACERIA = ""
                    if (MainServidor.SEGUNDOS_REBOOT_SERVER > 0) {
                        Mundo.SEG_CUENTA_REGRESIVA = MainServidor.SEGUNDOS_REBOOT_SERVER
                    }
                    Mundo.MSJ_CUENTA_REGRESIVA = MainServidor.MENSAJE_TIMER_REBOOT
                    GestorSalida.ENVIAR_bRS_PARAR_CUENTA_REGRESIVA_TODOS()
                }
            }
            var cantidadpjss = 0
            for (luch in ganadores) {
                if (luch.estaRetirado() || luch.esInvocacion()) {
                    continue
                }
                if (luch.getPersonaje() != null) cantidadpjss += 1
                luchadorSalirPelea(luch, cantidadpjss)
            }
            var cantidadpjs = 0
            for (luch in perdedores) {
                if (luch.estaRetirado() || luch.esInvocacion()) {
                    continue
                }
                if (luch.getPersonaje() != null) cantidadpjs += 1
                consecuenciasPerder(luch, cantidadpjs)
            }
            packet.toString()
        } catch (e: Exception) {
            MainServidor.redactarLogServidorln("Exception PanelResultados - Mapa: " + _mapaCopia.getID().toString() + " PeleaID: "
                    + iD.toString() + " e -> " + e.toString())
            e.printStackTrace()
            "EXCEPTION"
        }
    }

    private fun repartirDropRobado(dropRobado: ArrayList<Objeto?>?, dropeadores: ArrayList<Luchador?>?) {
        when (_tipo) {
            Constantes.PELEA_TIPO_DESAFIO, Constantes.PELEA_TIPO_KOLISEO -> {
            }
            else -> {
                // REPARTIR ITEMS ROBADOS A LOS JUGADORES
                if (dropRobado != null) {
                    for (obj in dropRobado) {
                        val nPorcAzar: Int = Formulas.getRandomInt(1, prospeccionEquipo)
                        var suma = 0
                        var dropeador: Luchador? = null
                        for (l in dropeadores) {
                            suma += l.getProspeccionLuchador()
                            if (suma >= nPorcAzar) {
                                dropeador = l
                                break
                            }
                        }
                        if (dropeador != null) {
                            dropeador.addDropLuchador(obj, true)
                        }
                    }
                }
                for (luchGanador in dropeadores) {
                    // esto es solo para el grupo de mobs y otros
                    var fParte: Float = luchGanador.getProspeccionLuchador() / prospeccionEquipo.toFloat()
                    fParte = Math.min(1, Math.max(0, fParte))
                    if (_kamasRobadas > 0) {
                        luchGanador.addKamasGanadas((_kamasRobadas * fParte).toLong())
                    }
                    if (_expRobada > 0) {
                        luchGanador.addXPGanada((_expRobada * fParte).toLong())
                    }
                }
            }
        }
    }

    private fun stringHonor(luchGanador: Luchador?): String? {
        return when (luchGanador.getAlineacion()) {
            Constantes.ALINEACION_BONTARIANO, Constantes.ALINEACION_BRAKMARIANO, Constantes.ALINEACION_MERCENARIO -> {
                val nivelA: Int = luchGanador.getNivelAlineacion()
                (Mundo.getExpAlineacion(nivelA).toString() + ";" + luchGanador.getPreLuchador().getHonor() + ";"
                        + Mundo.getExpAlineacion(nivelA + 1))
            }
            else -> "0;0;0"
        }
    }

    fun addIA(IA: Inteligencia?) {
        _IAs.add(IA)
    }

    fun addAccion(accion: Accion?) {
        if (_acciones == null) {
            _acciones = ArrayList<Accion?>()
        }
        _acciones.add(accion)
    }

    fun getStrAcciones(): String? {
        if (_acciones == null) {
            return ""
        }
        val str = StringBuilder()
        for (accion in _acciones) {
            str.append("""
    
    Accion ID: ${accion.getID().toString()}, Arg: ${accion.getArgs()}
    """.trimIndent())
        }
        return str.toString()
    }

    fun getParamMiEquipo(id: Int): Byte {
        if (_equipo1.containsKey(id)) {
            return 1
        }
        if (_equipo2.containsKey(id)) {
            return 2
        }
        return if (_espectadores.containsKey(id)) {
            4
        } else -1
    }

    fun getParamEquipoEnemigo(id: Int): Byte {
        if (_equipo1.containsKey(id)) {
            return 2
        }
        return if (_equipo2.containsKey(id)) {
            1
        } else -1
    }

    fun getLuchadorPorPJ(perso: Personaje?): Luchador? {
        var luchador: Luchador? = null
        if (_equipo1.get(perso.getID()) != null) luchador = _equipo1.get(perso.getID()) else if (_equipo2.get(perso.getID()) != null) luchador = _equipo2.get(perso.getID())
        return luchador
    }

    // incluye espectadores
    fun getLuchadorPorID(id: Int): Luchador? {
        var luchador: Luchador? = null
        if (_equipo1.get(id) != null) {
            luchador = _equipo1.get(id)
        } else if (_equipo2.get(id) != null) {
            luchador = _equipo2.get(id)
        } else if (_espectadores.get(id) != null) {
            luchador = _espectadores.get(id)
        }
        return luchador
    }

    fun esEspectador(id: Int): Boolean {
        return _espectadores.containsKey(id)
    }

    fun getLuchadorTurno(): Luchador? {
        return _luchadorDeTurno
    }

    private fun getLuchadorOrden(): Luchador? {
        return try {
            if (_nroOrdenLuc < 0 || _nroOrdenLuc >= _ordenLuchadores!!.size()) {
                _nroOrdenLuc = 0
            }
            _ordenLuchadores!![_nroOrdenLuc.toInt()]
        } catch (e: Exception) {
            _nroOrdenLuc++
            getLuchadorOrden()
        }
    }

    private fun luchadorSalirPelea(luch: Luchador?, cant: Int) {
        if (luch == null) {
            return
        }
        luch.getTotalStats().clearBuffStats()
        val perdedor: Personaje = luch.getPersonaje() ?: return
        for (compa in perdedor.getCompañeros()) {
            compa.getCompañeros().clear()
        }
        perdedor.salirPelea(false, false, acabaMaitre, cant)
    }

    private fun esMapaHeroico(): Boolean {
        return MainServidor.MODO_HEROICO || MainServidor.MAPAS_MODO_HEROICO.contains(_mapaReal.getID())
    }

    private fun consecuenciasPerder(luch: Luchador?, cant: Int) {
        if (luch == null) {
            return
        }
        luch.getTotalStats().clearBuffStats()
        val pjPerdedor: Personaje = luch.getPersonaje() ?: return
        if (esMapaHeroico()) {
            // switch (_tipo) {
            // // case CentroInfo.PELEA_TIPO_DESAFIO :
            // case Constantes.PELEA_TIPO_KOLISEO :
            // break;
            // default :
            // pjPerdedor.salirPelea(false, false);
            // robarPersonajePerdedor(luch);
            // break;
            // }
            pjPerdedor.salirPelea(false, false, acabaMaitre, cant)
            robarPersonajePerdedor(luch)
        } else {
            when (_tipo) {
                Constantes.PELEA_TIPO_DESAFIO -> {
                }
                Constantes.PELEA_TIPO_PVP -> {
                    pjPerdedor.addEnergiaConIm(-10 * pjPerdedor.getNivel(), true)
                    pjPerdedor.setPDV(1, false)
                    imprimiAsesinos(luch)
                }
                Constantes.PELEA_TIPO_RECAUDADOR -> {
                    pjPerdedor.addEnergiaConIm(-3000, true)
                    pjPerdedor.setPDV(1, false)
                }
                Constantes.PELEA_TIPO_KOLISEO -> {
                    pjPerdedor.setPDV(1, false)
                    imprimiAsesinos(luch)
                }
                else -> {
                    if (_mapaReal.getID() === 29932 || _mapaReal.getID() === 29933 || _mapaReal.getID() === 29934 || _mapaReal.getID() === 29935 || _mapaReal.getID() === 29978) {
                        pjPerdedor.setPDV(1, false)
                        pjPerdedor.salirPeleaTorre()
                        return
                    }
                    // pjPerdedor.addEnergiaConIm(-10 * pjPerdedor.getNivel(), true);
                    pjPerdedor.setPDV(1, false)
                }
            }
            pjPerdedor.salirPelea(false, false, acabaMaitre, cant)
        }
    }

    private fun imprimiAsesinos(luch: Luchador?) {
        val pjPerdedor: Personaje = luch.getPersonaje()
        if (pjPerdedor == null || !luch.getMsjMuerto()) {
            return
        }
        if (esMapaHeroico()) {
            if (!MainServidor.PARAM_MENSAJE_ASESINOS_HEROICO) {
                return
            }
        } else {
            when (_tipo) {
                Constantes.PELEA_TIPO_KOLISEO -> if (!MainServidor.PARAM_MENSAJE_ASESINOS_KOLISEO) {
                    return
                }
                Constantes.PELEA_TIPO_PVP -> if (!MainServidor.PARAM_MENSAJE_ASESINOS_PVP) {
                    return
                }
                else -> return
            }
        }
        luch.setMsjMuerto(true)
        if (_asesinos.toString().isEmpty()) {
            GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS("1776;" + pjPerdedor.getNombre())
        } else {
            GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS("1777;" + pjPerdedor.getNombre().toString() + "~" + _asesinos.toString())
        }
    }

    var acabaMaitre: Personaje? = null
    @Synchronized
    fun retirarsePelea(idRetirador: Int, idExpulsado: Int, obligado: Boolean) {
        val luchRetirador: Luchador? = getLuchadorPorID(idRetirador)
        val luchExpulsado: Luchador? = getLuchadorPorID(idExpulsado)
        var linea = 0f
        if (Mundo.SERVIDOR_ESTADO === Constantes.SERVIDOR_OFFLINE || luchRetirador == null) {
            return
        }
        linea = 1f
        _cantUltAfec = 1
        if (luchRetirador != null) {
            if (luchRetirador.getPersonaje().liderMaitre) {
                for (pjx in luchRetirador.getPersonaje().getGrupoParty().getPersos()) {
                    if (pjx === luchRetirador.getPersonaje()) {
                        luchRetirador.getPersonaje().liderMaitre = false
                        continue
                    }
                    pjx.esMaitre = false
                }
            }
        }
        if (!esEspectador(idRetirador)) {
            linea = 2f
            if (MainServidor.MODO_PVP_AGRESIVO && getTipoPelea() == Constantes.PELEA_TIPO_PVP) {
                return
            }
            linea = 3f
            when (_fase) {
                Constantes.PELEA_FASE_COMBATE -> {
                    linea = 4f
                    val persoRetirador: Personaje = luchRetirador.getPersonaje()
                    if (!obligado) {
                        // 5 segundos para q se pueda retirar
                        if (System.currentTimeMillis() - _tiempoCombate < 5000) {
                            GestorSalida.ENVIAR_BN_NADA(persoRetirador, "ESPERAR 5 SEG")
                            return
                        }
                    }
                    linea = 5f
                    if (luchRetirador.estaRetirado()) {
                        _espectadores.remove(idRetirador)
                        luchadorSalirPelea(luchRetirador, 0)
                        GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(persoRetirador)
                        return
                    }
                    linea = 6f
                    if (!persoRetirador.getCompañeros().isEmpty() && !persoRetirador.esMultiman()) {
                        try {
                            for (compa in persoRetirador.getCompañeros()) {
                                val idMultiman: Int = compa.getID()
                                retirarsePelea(idMultiman, idMultiman, true)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    linea = 7f
                    addMuertosReturnFinalizo(luchRetirador, null)
                    linea = 8f
                    luchRetirador.setEstaRetirado(true)
                    if (luchRetirador != null) {
                        _listaMuertos.remove(luchRetirador)
                    }
                    linea = 9f
                    consecuenciasPerder(luchRetirador, 0)
                    if (_tipo == Constantes.PELEA_TIPO_PVP || _tipo == Constantes.PELEA_TIPO_PRISMA) {
                        luchRetirador.getPreLuchador().addHonor(-500)
                    }
                    linea = 10f
                    // GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(_mapaCopia, idRetirador);
                    if (!persoRetirador.esMultiman()) {
                        GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(persoRetirador)
                    }
                    linea = 11f
                    if (_tipo == Constantes.PELEA_TIPO_KOLISEO) {
                        persoRetirador.setPenalizarKoliseo()
                    }
                }
                Constantes.PELEA_FASE_POSICION -> {
                    linea = 12f
                    if (_tipo == Constantes.PELEA_TIPO_PVP && !MainServidor.PARAM_EXPULSAR_PREFASE_PVP) {
                        GestorSalida.ENVIAR_BN_NADA(luchRetirador.getPersonaje(), "NO SE PUEDE EXPULSAR PREFASE PVP")
                        return
                    }
                    linea = 13f
                    if (_mobGrupo != null) {
                        if (_mobGrupo.is_evento()) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(luchRetirador.getPersonaje(),
                                    "No puedes expulsar gente en invasion Covid")
                            return
                        }
                    }
                    linea = 14f
                    if (!obligado) {
                        if (System.currentTimeMillis() - _tiempoCombate < 3000) {
                            GestorSalida.ENVIAR_BN_NADA(luchRetirador.getPersonaje(), "ESPERAR MINIMO 3 SEG")
                            return
                        }
                    }
                    linea = 15f
                    var puedeExpulsar = false
                    if (idRetirador == _idLuchInit1 || idRetirador == _idLuchInit2) {
                        puedeExpulsar = true
                    }
                    linea = 16f
                    var luchASalir: Luchador? = luchRetirador
                    if (enTorneo) {
                        if (luchRetirador.getPersonaje().enTorneo === 1) ServidorSocket.perderTorneo(luchRetirador.getPersonaje())
                    }
                    if (puedeExpulsar) {
                        linea = 17f
                        if (luchExpulsado != null && luchExpulsado.getID() !== luchRetirador.getID()) {
                            // si puede expulsar, y expulsa a otro jugador
                            linea = 18f
                            if (luchExpulsado.getEquipoBin() === luchRetirador.getEquipoBin()) {
                                linea = 19f
                                val persoExpulsado: Personaje = luchExpulsado.getPersonaje()
                                linea = 20f
                                if (persoExpulsado != null && !persoExpulsado.getCompañeros().isEmpty()
                                        && !persoExpulsado.esMultiman()) {
                                    for (compa in persoExpulsado.getCompañeros()) {
                                        val idMultiman: Int = compa.getID()
                                        retirarsePelea(idMultiman, idMultiman, true)
                                    }
                                }
                                linea = 21f
                                luchadorSalirPelea(luchExpulsado, 0)
                                linea = 22f
                                luchASalir = luchExpulsado
                            } else {
                                return
                            }
                        } else {
                            // si puede expulsar y se expulsa a si mismo
                            when (_tipo) {
                                Constantes.PELEA_TIPO_DESAFIO -> {
                                    linea = 23f
                                    for (luch in luchadoresDeEquipo(3)) {
                                        luchadorSalirPelea(luch, 0)
                                        if (luch.getPersonaje() != null && !luch.getPersonaje().esMultiman()) {
                                            GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(luch.getPersonaje())
                                        }
                                    }
                                    linea = 24f
                                    _fase = Constantes.PELEA_FASE_FINALIZADO
                                    _mapaReal.borrarPelea(iD)
                                    linea = 25f
                                    GestorSalida.ENVIAR_Gc_BORRAR_ESPADA_EN_MAPA(_mapaReal, iD)
                                    GestorSalida.ENVIAR_fC_CANTIDAD_DE_PELEAS(_mapaReal)
                                    return
                                }
                                else -> {
                                    linea = 26f
                                    val persoExpulsado: Personaje = luchRetirador.getPersonaje()
                                    if (enTorneo) {
                                        if (luchRetirador.getPersonaje().enTorneo === 1) ServidorSocket.perderTorneo(luchRetirador.getPersonaje())
                                    }
                                    if (persoExpulsado != null && !persoExpulsado.getCompañeros().isEmpty()
                                            && !persoExpulsado.esMultiman()) {
                                        for (compa in persoExpulsado.getCompañeros()) {
                                            val luc: Luchador = getLuchadorPorID(compa.getID()) ?: continue
                                            if (luc.estaRetirado()) continue
                                            val idMultiman: Int = compa.getID()
                                            retirarsePelea(idRetirador, idMultiman, true)
                                        }
                                    }
                                    linea = 27f
                                    consecuenciasPerder(luchRetirador, 0)
                                    if (_tipo == Constantes.PELEA_TIPO_PVP || _tipo == Constantes.PELEA_TIPO_PRISMA) {
                                        luchRetirador.getPreLuchador().addHonor(-500)
                                    }
                                    linea = 28f
                                }
                            }
                        }
                    } else {
                        linea = 29f
                        if (luchExpulsado != null) {
                            GestorSalida.ENVIAR_BN_NADA(luchRetirador.getPersonaje(), "NO HAY LUCH A EXPULSAR")
                            return
                        }
                        linea = 30f
                        val persoExpulsado: Personaje = luchRetirador.getPersonaje()
                        linea = 31f
                        when (_tipo) {
                            Constantes.PELEA_TIPO_DESAFIO -> {
                                linea = 32f
                                if (persoExpulsado != null && !persoExpulsado.getCompañeros().isEmpty()
                                        && !persoExpulsado.esMultiman()) {
                                    for (compa in persoExpulsado.getCompañeros()) {
                                        val idMultiman: Int = compa.getID()
                                        retirarsePelea(idRetirador, idMultiman, true)
                                    }
                                }
                                linea = 33f
                                luchadorSalirPelea(luchRetirador, 0)
                                linea = 34f
                            }
                            else -> {
                                linea = 35f
                                if (persoExpulsado != null && !persoExpulsado.getCompañeros().isEmpty()
                                        && !persoExpulsado.esMultiman()) {
                                    for (compa in persoExpulsado.getCompañeros()) {
                                        val idMultiman: Int = compa.getID()
                                        retirarsePelea(idRetirador, idMultiman, true)
                                    }
                                }
                                linea = 36f
                                consecuenciasPerder(luchRetirador, 0)
                                if (_tipo == Constantes.PELEA_TIPO_PVP || _tipo == Constantes.PELEA_TIPO_PRISMA) {
                                    luchRetirador.getPreLuchador().addHonor(-500)
                                }
                                linea = 37f
                            }
                        }
                    }
                    linea = 38f
                    GestorSalida.ENVIAR_GM_BORRAR_LUCHADOR(this, luchASalir.getID(), 3)
                    GestorSalida.ENVIAR_Gt_BORRAR_NOMBRE_ESPADA(_mapaReal,
                            if (_equipo1.containsKey(luchASalir.getID())) _idLuchInit1 else _idLuchInit2, luchASalir)
                    linea = 39f
                    if (_equipo1.containsKey(luchASalir.getID())) {
                        luchASalir.getCeldaPelea().removerLuchador(luchASalir)
                        _equipo1.remove(luchASalir.getID()) // en estado de posiciones
                    } else if (_equipo2.containsKey(luchASalir.getID())) {
                        luchASalir.getCeldaPelea().removerLuchador(luchASalir)
                        _equipo2.remove(luchASalir.getID())
                    }
                    linea = 40f
                    luchASalir.setEstaMuerto(true)
                    linea = 41f
                    val portador: Luchador = luchASalir.getPortador()
                    linea = 42f
                    if (portador != null) {
                        quitarTransportados(portador)
                    }
                    linea = 43f
                    if (luchASalir.getPersonaje() != null && !luchASalir.getPersonaje().esMultiman()) {
                        GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(luchASalir.getPersonaje())
                        if (_tipo == Constantes.PELEA_TIPO_KOLISEO) {
                            luchASalir.getPersonaje().setPenalizarKoliseo()
                        }
                    }
                    linea = 44f
                    if (!acaboPelea(3.toByte()) && _tipo == Constantes.PELEA_TIPO_DESAFIO) {
                        verificaTodosListos()
                    }
                    linea = 45f
                }
                else -> {
                    if (_fase == Constantes.PELEA_FASE_FINALIZADO) {
                        acaboPelea(3.toByte())
                    }
                    if (MainServidor.MODO_IMPRIMIR_LOG) System.out.println("ERROR RETIRARSE, estado de combate: " + _fase + " tipo de combate:" + _tipo
                            + " LuchadorExp:" + luchExpulsado + " LuchadorRet:" + luchRetirador.getNombre()
                            + " mapaID: " + _mapaCopia.getID() + " peleaID: " + iD + " l: " + linea)
                }
            }
        } else {
            _espectadores.remove(luchRetirador.getID())
            luchadorSalirPelea(luchRetirador, 0)
            GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(luchRetirador.getPersonaje())
        }
    }

    fun stringOrdenJugadores(): String? {
        val packet = StringBuilder("GTL")
        for (luchador in _ordenLuchadores!!) {
            if (MainServidor.ELIMINAR_MUERTO) {
                if (!luchador.estaMuerto()) {
                    packet.append("|" + luchador.getID())
                }
            } else {
                packet.append("|" + luchador.getID())
            }
        }
        return packet.toString()
    }

    fun getSigIDLuchador(): Int {
        return --_ultimaInvoID
    }

    fun addLuchadorEnEquipo(luchador: Luchador?, equipo: Int) {
        if (equipo == 0) {
            _equipo1.put(luchador.getID(), luchador)
        } else if (equipo == 1) {
            _equipo2.put(luchador.getID(), luchador)
        }
    }

    fun strParaListaPelea(): String? {
        if (_fase == Constantes.PELEA_FASE_FINALIZADO) {
            _mapaReal.borrarPelea(iD)
            return ""
        }
        val infos = StringBuilder()
        infos.append("$iD;")
        infos.append((if (_fase <= Constantes.PELEA_FASE_POSICION) "-1" else _tiempoCombate).toString() + ";")
        var jugEquipo1 = 0
        var jugEquipo2 = 0
        for (l in _equipo1.values()) {
            if (l == null || l.esInvocacion()) {
                continue
            }
            jugEquipo1++
        }
        for (l in _equipo2.values()) {
            if (l == null || l.esInvocacion()) {
                continue
            }
            jugEquipo2++
        }
        if (jugEquipo1 == 0 || jugEquipo2 == 0) {
            acaboPelea(3.toByte())
            return ""
        }
        infos.append(_luchInit1.getFlag().toString() + ",")
        infos.append((if (_tipo == Constantes.PELEA_TIPO_PVP || _tipo == Constantes.PELEA_TIPO_PRISMA) _luchInit1.getAlineacion() else 0).toString() + ",")
        infos.append("$jugEquipo1;")
        infos.append(_luchInit2.getFlag().toString() + ",")
        infos.append((if (_tipo == Constantes.PELEA_TIPO_PVP || _tipo == Constantes.PELEA_TIPO_PRISMA) _luchInit2.getAlineacion() else 0).toString() + ",")
        infos.append("$jugEquipo2;")
        return infos.toString()
    }

    fun continuaPelea(): Boolean {
        var equipo1Vivo = false
        var equipo2Vivo = false
        for (luchador in _equipo1.values()) {
            if (luchador.esInvocacion()) {
                continue
            }
            if (!luchador.estaMuerto()) {
                equipo1Vivo = true
                break
            }
        }
        for (luchador in _equipo2.values()) {
            if (luchador.esInvocacion()) {
                continue
            }
            if (!luchador.estaMuerto()) {
                equipo2Vivo = true
                break
            }
        }
        return equipo1Vivo && equipo2Vivo
    }

    fun cuantosQuedanDelEquipo(id: Int): Int {
        var num = 0
        if (_equipo1.containsKey(id)) {
            for (luchador in _equipo1.values()) {
                if (luchador.estaMuerto() || luchador.esInvocacion()) {
                    continue
                }
                num++
            }
        } else if (_equipo2.containsKey(id)) {
            for (luchador in _equipo2.values()) {
                if (luchador.estaMuerto() || luchador.esInvocacion()) {
                    continue
                }
                num++
            }
        }
        return num
    }

    private fun mostrarEspada(): String? {
        // final String packet = "Gc+" + idPelea + ";" + tipoPelea + "|" + id1 + ";" +
        // celda1 + ";" +
        // flag1 + ";" + alin1
        // + "|" + id2 + ";" + celda2 + ";" + flag2 + ";" + alin2;
        val p = StringBuilder("Gc+")
        p.append("$iD;")
        if (_tipo == Constantes.PELEA_TIPO_CACERIA) {
            p.append(0)
        } else {
            p.append(_tipo)
        }
        p.append("|" + _idLuchInit1 + ";" + _celdaID1 + ";" + _luchInit1.getFlag() + ";")
        if (_tipo == Constantes.PELEA_TIPO_PVP || _tipo == Constantes.PELEA_TIPO_PRISMA) {
            p.append(_luchInit1.getAlineacion())
        } else {
            p.append(Constantes.ALINEACION_NULL)
        }
        p.append("|" + _idLuchInit2 + ";" + _celdaID2 + ";" + _luchInit2.getFlag() + ";")
        if (_tipo == Constantes.PELEA_TIPO_PVP || _tipo == Constantes.PELEA_TIPO_PRISMA) {
            p.append(_luchInit2.getAlineacion())
        } else {
            p.append(Constantes.ALINEACION_NULL)
        }
        return p.toString()
    }

    fun infoEspadaPelea(perso: Personaje?) {
        try {
            if (_fase != Constantes.PELEA_FASE_POSICION || _tipo == Constantes.PELEA_TIPO_PVM_NO_ESPADA) return
            GestorSalida.ENVIAR_Gc_MOSTRAR_ESPADA_A_JUGADOR(perso, mostrarEspada())
            var enviar: StringBuilder? = StringBuilder()
            for (entry in _equipo1.entrySet()) {
                val luchador: Luchador = entry.getValue()
                if (!enviar.toString().isEmpty()) {
                    enviar.append("|+")
                }
                enviar.append(luchador.getID().toString() + ";" + luchador.getNombre() + ";" + luchador.getNivel())
            }
            GestorSalida.ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(perso, _idLuchInit1, enviar.toString())
            GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA(perso, if (_cerrado1) '+' else '-', 'A', _idLuchInit1)
            GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA(perso, if (_ayuda1) '+' else '-', 'H', _idLuchInit1)
            GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA(perso, if (_soloGrupo1) '+' else '-', 'P', _idLuchInit1)
            GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA(perso, if (_sinEspectador) '+' else '-', 'S', _idLuchInit1)
            enviar = StringBuilder()
            for (entry in _equipo2.entrySet()) {
                val luchador: Luchador = entry.getValue()
                if (!enviar.toString().isEmpty()) {
                    enviar.append("|+")
                }
                enviar.append(luchador.getID().toString() + ";" + luchador.getNombre() + ";" + luchador.getNivel())
            }
            GestorSalida.ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(perso, _idLuchInit2, enviar.toString())
            GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA(perso, if (_cerrado2) '+' else '-', 'A', _idLuchInit2)
            GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA(perso, if (_ayuda2) '+' else '-', 'H', _idLuchInit2)
            GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA(perso, if (_soloGrupo2) '+' else '-', 'P', _idLuchInit2)
            GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA(perso, if (_sinEspectador) '+' else '-', 'S', _idLuchInit2)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getListaMuertos(): List<Luchador?>? {
        return _listaMuertos
    }

    fun addBuffLuchadores(objetivos: ArrayList<Luchador?>?, efectoID: Int, valor: Int,
                          turnosRestantes: Int, hechizoID: Int, args: String?, lanzador: Luchador?, tipo: TipoDaño?,
                          condicionHechizo: String?) {
        if (objetivos.isEmpty()) {
            return
        }
        val str = StringBuilder()
        for (luch in objetivos) {
            // aqui no envia el GIE, lo envia abajo
            if (!luch.addBuffConGIE(efectoID, valor, turnosRestantes, hechizoID, args, lanzador, false, tipo,
                            condicionHechizo)._primero) {
                continue
            }
            if (!condicionHechizo.isEmpty()) {
                continue
            }
            if (str.length() > 0) {
                str.append(",")
            }
            str.append(luch.getID())
        }
        if (str.length() > 0) {
            val gie = getStrParaGIE(efectoID, str.toString(), turnosRestantes, hechizoID, args)
            GestorSalida.ENVIAR_GIE_AGREGAR_BUFF_PELEA(this, 7, gie)
        }
    }

    fun getSalvarMobHeroico(): Boolean {
        return _salvarMobHeroico
    }

    fun setSalvarMobHeroico(_salvarMobHeroico: Boolean) {
        this._salvarMobHeroico = _salvarMobHeroico
    }

    fun getMobGrupo(): GrupoMob? {
        return _mobGrupo
    }

    fun setMobGrupo(_mobGrupo: GrupoMob?) {
        this._mobGrupo = _mobGrupo
    }

    fun getJefe(): Luchador? {
        return jefe
    }

    fun get_luchMenorNivelReto(): Int {
        return _luchMenorNivelReto
    }

    fun set_luchMenorNivelReto(_luchMenorNivelReto: Int) {
        this._luchMenorNivelReto = _luchMenorNivelReto
    }

    fun get_ultimoTipoDañoReto(): TipoDaño? {
        return _ultimoTipoDañoReto
    }

    fun set_ultimoTipoDañoReto(_ultimoTipoDañoReto: TipoDaño?) {
        this._ultimoTipoDañoReto = _ultimoTipoDañoReto
    }

    companion object {
        var LOG_COMBATES: StringBuilder? = StringBuilder()
        private fun getStrParaGIE(efectoID: Int, objetivos: String?, turnos: Int,
                                  hechizoID: Int, args: String?): String? {
            // para varios
            val s: Array<String?> = args.replaceAll("-1", "null").split(",")
            val mParam1 = s[0]
            val valMax = s[1]
            val mParam3 = s[2]
            val suerte = s[4]
            return (efectoID.toString() + ";" + objetivos + ";" + mParam1 + ";" + valMax + ";" + mParam3 + ";" + suerte
                    + ";" + turnos + ";" + hechizoID)
        }

        fun getStrParaGA998(efectoID: Int, objetivo: Int, turnos: Int, hechizoID: Int,
                            args: String?): String? {
            // para uno solo
            val s: Array<String?> = args.replaceAll("-1", "null").split(",")
            val mParam1 = s[0]
            val valMax = s[1]
            val mParam3 = s[2]
            val suerte = s[4]
            return (objetivo.toString() + "," + efectoID + "," + mParam1 + "," + valMax + "," + mParam3 + "," + suerte
                    + "," + turnos + "," + hechizoID)
        }
    }
}