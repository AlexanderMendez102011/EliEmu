package variables.pelea

import java.util.ArrayList

class Luchador(pelea: Pelea, pre: PreLuchador, espectador: Boolean) {
    var _estaMuerto = false
    var _estaRetirado = false
    private var _puedeJugar = false
    private var _contaminado = false
    var _desconectado = false
    private var _estatico = false

    // FIXME aun no se para q sirve esto
    var sirveParaBuff = true
    private var _esBomba = false
    private var _esDoble = false
    private var _idReal = true
    var stop = false
    private var _listo = false
    private var _espectadorAdmin = false
    private var _esAbonado = false
    private var _saqueado = false
    var msjMuerto = false
    var updateGTM = false
    var direccion = 'b'
    private var _tipoLuch: Byte = 0

    // 0 = agresor
    // 1 = agredido
    var equipoBin: Byte = -2
    var turnosParaMorir: Byte = 0
        private set
    var atacoAOtro: Luchador? = null
    var turnosRestantes: Byte = 20
    var alineacion: Byte = Constantes.ALINEACION_NULL
    var ultimoElementoDaño: Int = Constantes.ELEMENTO_NULO
    var iD: Int
    var pDVMaxSinBuff: Int
        private set
    var pDVSinBuff: Int
        private set
    var gfxID: Int
    var penalizado = 0
    var realizaaccion = true
    var usoTP = false
    var nroInvocaciones = 0
    var iDHechizoLanzado = -1
    var iDCeldaInicioTurno = 0
    var luchQueAtacoUltimo = 0
    var prospeccionLuchador = 100
        private set
    private var _escudo = 0
    private var _colorNombre = -1
    var distMinAtq = -1
    var _PArestantes = 0
    var _PMrestantes = 0
    var pAUsados = 0
    var pMUsados = 0
    val nivelViejo: Int
    private var _kamasGanadas: Long = 0
    private var _xpGanada: Long = 0
    var bonusAlinExp = 0f
    var bonusAlinDrop = 0f
    var retrocediendo = false
    private val _pelea: Pelea
    private var _celda: Celda? = null
    var transportando: Luchador? = null
    var portador: Luchador? = null
        private set
    var invocador: Luchador? = null
    var muertoPor: Luchador? = null
    private var _preLuchador: PreLuchador? = null
    private val _totalStats: TotalStats
    private val _bombas: ArrayList<Luchador>? = null
    private val _hechizosLanzados: ArrayList<HechizoLanzado> = ArrayList<HechizoLanzado>()
    val _hechiLanzadosReto: ArrayList<Integer> = ArrayList<Integer>()
    private val _retoMobsAsesinados: ArrayList<Integer> = ArrayList<Integer>()
    private val _visibles: ArrayList<Integer> = ArrayList<Integer>()
    val _buffsPelea: CopyOnWriteArrayList<Buff> = CopyOnWriteArrayList<Buff>()

    // private final ArrayList<Buff> _buffsCond = new ArrayList<Buff>();
    val _estados: Map<Integer, Integer?> = TreeMap<Integer, Integer>()
    val _bonusCastigo: Map<Integer, Integer> = TreeMap<Integer, Integer>()
    private var _stringBuilderGTM: StringBuilder = StringBuilder()
    private var _IA: Inteligencia? = null
    var nombre // , _strGMLuchador;
            : String? = null
    private var _objDropeados: Map<Objeto, Boolean>? = null
    fun limitarVida() {
        if (MainServidor.LIMITE_STATS_PVP_PERSONALIZADOS && MainServidor.LIMITE_VIDA > 0 && _pelea.getTipoPelea() === Constantes.PELEA_TIPO_PVP) {
            var vida: Int = MainServidor.LIMITE_VIDA
            if (personaje != null && MainServidor.LIMITE_VIDA_CLASE.containsKey(personaje.getClaseID(true))) {
                vida = MainServidor.LIMITE_VIDA_CLASE.get(personaje.getClaseID(true))
            }
            pDVMaxSinBuff = Math.min(pDVMaxSinBuff, vida)
            pDVSinBuff = Math.min(pDVSinBuff, vida)
        } else if (MainServidor.LIMITE_STATS_KOLI_PERSONALIZADOS && MainServidor.LIMITE_VIDA_KOLISEO > 0 && _pelea.getTipoPelea() === Constantes.PELEA_TIPO_KOLISEO) {
            var vida: Int = MainServidor.LIMITE_VIDA_KOLISEO
            if (personaje != null && MainServidor.LIMITE_VIDA_CLASE_KOLISEO.containsKey(personaje.getClaseID(true))) {
                vida = MainServidor.LIMITE_VIDA_CLASE_KOLISEO.get(personaje.getClaseID(true))
            }
            pDVMaxSinBuff = Math.min(pDVMaxSinBuff, vida)
            pDVSinBuff = Math.min(pDVSinBuff, vida)
        } else if (MainServidor.LIMITE_STATS_PERCO_PERSONALIZADOS && MainServidor.LIMITE_VIDA_PERCO > 0 && _pelea.getTipoPelea() === Constantes.PELEA_TIPO_RECAUDADOR) {
            val vida: Int = MainServidor.LIMITE_VIDA_PERCO
            pDVMaxSinBuff = Math.min(pDVMaxSinBuff, vida)
            pDVSinBuff = Math.min(pDVSinBuff, vida)
        }
    }

    var pARestantes: Int
        get() = _PArestantes
        private set(p) {
            _PArestantes = p
            updateGTM = true
        }
    var pMRestantes: Int
        get() = _PMrestantes
        private set(p) {
            val oldPM = _PMrestantes
            _PMrestantes = p
            updateGTM = true
            if (oldPM != _PMrestantes) {
                if (_IA != null) {
                    _IA.forzarRefrescarMov()
                }
            }
        }

    fun addPARestantes(p: Int): Int {
        var r = p
        if (r > 0) {
            if (_PArestantes < 0) {
                r += _PArestantes
            }
        }
        pARestantes = _PArestantes + p
        return r
    }

    fun addPMRestantes(p: Int): Int {
        var r = p
        if (r > 0) {
            if (_PMrestantes < 0) {
                r += _PMrestantes
            }
        }
        pMRestantes = _PMrestantes + p
        return r
    }

    fun addPAUsados(p: Int) {
        pAUsados += p
        if (pAUsados < 0) {
            pAUsados = 0
        }
    }

    fun addPMUsados(p: Int) {
        pMUsados += p
        if (pMUsados < 0) {
            pMUsados = 0
        }
    }

    fun resetPuntos() {
        val statsLuch: TotalStats = totalStats
        pARestantes = statsLuch.getTotalStatParaMostrar(Constantes.STAT_MAS_PA, pelea, null)
        pMRestantes = statsLuch.getTotalStatParaMostrar(Constantes.STAT_MAS_PM, pelea, null)
        pAUsados = 0
        pMUsados = 0
    }

    val comandoPasarTurno: Boolean
        get() = if (personaje != null) {
            personaje.getComandoPasarTurno()
        } else false

    fun setSaqueado(b: Boolean) {
        _saqueado = b
    }

    fun fueSaqueado(): Boolean {
        return _saqueado
    }

    private fun limpiarStatsBuffs() {
        _totalStats.getStatsBuff().clear()
    }

    val pelea: Pelea
        get() = _pelea

    fun addKamasGanadas(kamas: Long) {
        kamasGanadas = kamasGanadas + kamas
        preLuchador.addKamasGanada(kamas)
    }

    fun addXPGanada(xp: Long) {
        expGanada = expGanada + xp
        preLuchador.addXPGanada(xp)
    }

    fun esNoIA(): Boolean {
        return iA == null
    }

    fun estaListo(): Boolean {
        return _listo
    }

    fun setListo(listo: Boolean) {
        _listo = listo
    }

    fun esIDReal(): Boolean {
        return _idReal
    }

    fun esMultiman(): Boolean {
        return if (personaje == null) {
            false
        } else personaje.esMultiman()
    }

    fun setIDReal(b: Boolean) {
        _idReal = b
    }

    fun esIAChafer(): Boolean {
        if (iA == null) {
            return false
        }
        return if (iA.getTipoIA() === 11) {
            true
        } else false
    }

    var preLuchador: PreLuchador?
        get() = _preLuchador
        set(_preLuchador) {
            this._preLuchador = _preLuchador
        }
    val tipo: Int
        get() = _tipoLuch.toInt()

    // bandera desafio
    // bandera de recaudador
    // bandera mobs
    // bandera pj
    val flag: Byte
        get() = when (_tipoLuch) {
            0 -> 2 // bandera desafio
            5 -> 3 // bandera de recaudador
            4 -> 1 // bandera mobs
            else -> 0 // bandera pj
        }
    val stringBuilderGTM: StringBuilder
        get() = _stringBuilderGTM

    fun resetStringBuilderGTM() {
        _stringBuilderGTM = StringBuilder()
    }

    fun borrarBomba(bomba: Luchador?) {
        _bombas.remove(bomba)
    }

    fun addBomba(bomba: Luchador?) {
        if (_bombas.size() < 3) {
            _bombas.add(bomba)
        }
    }

    fun esBomba(): Boolean {
        return _esBomba
    }

    fun setBomba(bomba: Boolean) {
        _esBomba = bomba
    }

    fun addEscudo(escudo: Int) {
        _escudo += escudo
        if (_escudo < 0) {
            _escudo = 0
        }
    }

    fun esInvisible(idMirador: Int): Boolean {
        if (idMirador != 0) {
            if (idMirador == iD || _visibles.contains(idMirador)) {
                return false
            }
        }
        return if (tieneBuff(150)) {
            true
        } else false
    }

    fun hacerseVisible() {
        for (buff in _buffsPelea) {
            if (!buff.getCondicionBuff().isEmpty()) {
                continue
            }
            if (buff.getEfectoID() === 150) {
                removeBuff(buff)
            }
        }
        GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, 7, 150, iD.toString() + "", "$iD,0")
        GestorSalida.ENVIAR_GIC_APARECER_LUCHADORES_INVISIBLES(_pelea, 7, this)
    }

    fun aparecer(mostrar: Luchador) {
        _visibles.add(mostrar.iD)
        GestorSalida.ENVIAR_GA_ACCION_PELEA_LUCHADOR(mostrar, 150, iD.toString() + "", "$iD,0")
        GestorSalida.ENVIAR_GIC_APARECER_LUCHADORES_INVISIBLES(mostrar, iD.toString() + ";" + _celda.getID())
    }

    fun vaciarVisibles() {
        _visibles.clear()
    }

    fun aplicarBuffInicioTurno(pelea: Pelea?) {
        for (buff in _buffsPelea) {
            if (!buff.getCondicionBuff().isEmpty()) {
                continue
            }
            buff.aplicarBuffDeInicioTurno(pelea, this)
        }
    }

    fun getBuff(id: Int): Buff? {
        for (buff in _buffsPelea) {
            if (!buff.getCondicionBuff().isEmpty()) {
                continue
            }
            if (buff.getEfectoID() === id) {
                return buff
            }
        }
        return null
    }

    fun tieneBuff(id: Int): Boolean {
        return getBuff(id) != null
    }

    fun getBuffsPorEfectoID(efectotID: Int): ArrayList<Buff> {
        val buffs: ArrayList<Buff> = ArrayList()
        for (buff in _buffsPelea) {
            if (!buff.getCondicionBuff().isEmpty()) {
                continue
            }
            if (buff.getEfectoID() === efectotID) {
                buffs.add(buff)
            }
        }
        return buffs
    }

    fun getBuffPorHechizoYEfecto(hechizoID: Int, efectoID: Int): Buff? {
        for (buff in _buffsPelea) {
            if (!buff.getCondicionBuff().isEmpty()) {
                continue
            }
            if (buff.getHechizoID() === hechizoID && (efectoID == 0 || efectoID == buff.getEfectoID())) {
                return buff
            }
        }
        return null
    }

    fun tieneBuffPorHechizoYEfecto(hechizoID: Int, efectoID: Int): Boolean {
        return getBuffPorHechizoYEfecto(hechizoID, efectoID) != null
    }

    fun getValorPorBuffsID(efectoID: Int): Int {
        var valor = 0
        for (buff in _buffsPelea) {
            if (!buff.getCondicionBuff().isEmpty()) {
                continue
            }
            if (buff.getEfectoID() === efectoID) {
                if (efectoID == 106 || efectoID == 750) {
                    // reenvio de hechizo y efecto de captura de almas
                    if (buff.getPrimerValor() > valor) {
                        valor = buff.getPrimerValor()
                    }
                } else {
                    valor += buff.getPrimerValor()
                }
            }
        }
        return valor
    }

    fun getValorPorPrimerYEfectoID(efectoID: Int, primerValor: Int): Int {
        var valor = 0
        for (buff in _buffsPelea) {
            if (!buff.getCondicionBuff().isEmpty()) {
                continue
            }
            if (buff.getEfectoID() === efectoID && buff.getPrimerValor() === primerValor) {
                valor += buff.getSegundoValor()
            }
        }
        return valor
    }

    // la conmbinacion de GTF , GTM y GTS realiza la disminucion de turnos de los buffs
    fun disminuirBuffsPelea() {
        disminuirEstados()
        if (!_buffsPelea.isEmpty()) {
            for (buff in _buffsPelea) {
                val turnosRestantes: Int = buff.disminuirTurnosRestantes()
                if (turnosRestantes <= -1) {
                    continue
                }
                if (turnosRestantes == 0) {
                    when (buff.getEfectoID()) {
                        422 -> addEscudo(-buff.getPrimerValor())
                        150 -> hacerseVisible()
                    }
                    removeBuff(buff)
                }
            }
            actualizarBuffStats()
            if (pDVConBuff <= 0) {
                _pelea.addMuertosReturnFinalizo(this, null)
            }
        }
    }

    fun resetearBuffs(buffs: ArrayList<Buff>) {
        addNuevosBuffs(buffs)
        for (buff in _buffsPelea) {
            if (!buff.getCondicionBuff().isEmpty()) {
                continue
            }
            GestorSalida.ENVIAR_GA998_AGREGAR_BUFF_PELEA(_pelea, 7, Pelea.getStrParaGA998(buff.getEfectoID(), iD, buff
                    .getTurnosRestantes(false), buff.getHechizoID(), buff.getArgs()))
        }
    }

    private fun addNuevosBuffs(buffs: ArrayList<Buff>) {
        updateGTM = true
        _buffsPelea.clear()
        _buffsPelea.addAll(buffs)
        actualizarBuffStats()
    }

    private fun addBuff(buff: Buff?) {
        updateGTM = true
        _buffsPelea.add(buff)
        actualizarBuffStats()
    }

    private fun removeBuff(buff: Buff) { // solo lo usa para quitar invisbilidad
        updateGTM = true
        _buffsPelea.remove(buff)
    }

    fun actualizarBuffStats() { // refresh buffs, refrescar buffs
        limpiarStatsBuffs()
        for (buff in _buffsPelea) {
            if (!buff.getCondicionBuff().isEmpty()) {
                continue
            }
            _totalStats.getStatsBuff().addStatID(buff.getEfectoID(), buff.getPrimerValor())
        }
    }

    fun paraDeshechizar(equipoBin: Int): Boolean {
        var i = 0
        for (buff in _buffsPelea) {
            if (!buff.getCondicionBuff().isEmpty()) {
                continue
            }
            i += Constantes.estimaDaño(buff.getEfectoID())
        }
        if (equipoBin != equipoBin) {
            i = -i
        }
        return i > 0
    }

    fun clonarLuchador(id: Int): Luchador? {
        var ret: Luchador? = null
        ret = if (preLuchador.getClass() === Personaje::class.java) {
            Luchador(_pelea, Personaje.crearClon(personaje, id), false)
        } else if (preLuchador.getClass() === MobGrado::class.java) {
            Luchador(_pelea, mob.getMobGradoModelo().invocarMob(id, true, this), false)
        } else {
            return null
        }
        ret._esDoble = true
        ret.setInteligenciaArtificial(Inteligencia(ret, _pelea))
        return ret
    }

    var iniciativa = 0
        get() {
            if (tipo == 1 || tipo == 0) return if (field == 0) {
                field = personaje.getIniciativa()
                field
            } else field
            if (tipo == 2) return if (field == 0) {
                field = mob.getMobGradoModelo().getIniciativa()
                field
            } else field
            if (tipo == 5) return if (field == 0) {
                field = Mundo.getGremio(recaudador.getGremio().getID()).getNivel()
                field
            } else field
            if (tipo == 7) return 0
            return if (tipo == 10) if (field == 0) {
                if (esDoble()) field = personaje.getIniciativa()
                field
            } else field else 0
        }
        private set
    val hechizos: Map<Any, Any>
        get() = preLuchador.getHechizos()

    fun setBonusCastigo(bonus: Int, stat: Int) {
        bonusCastigo.put(stat, bonus)
    }

    fun getBonusCastigo(stat: Int): Int {
        var bonus = 0
        if (_bonusCastigo.containsKey(stat)) {
            bonus = _bonusCastigo[stat]
        }
        return bonus
    }

    // public int getTipo() {
    // return _tipo;
    // }
    fun setEstatico(estatico: Boolean) {
        _estatico = estatico
    }

    fun esEstatico(): Boolean {
        return _estatico
    }

    val hechizosLanzados: ArrayList<HechizoLanzado>
        get() = _hechizosLanzados

    fun actualizaHechizoLanzado() {
        val copia: ArrayList<HechizoLanzado> = ArrayList<HechizoLanzado>()
        copia.addAll(_hechizosLanzados)
        for (HL in copia) {
            HL.actuSigLanzamiento()
            if (HL.getSigLanzamiento() <= 0) {
                _hechizosLanzados.remove(HL as Object)
            }
        }
        copia.clear()
    }

    fun addHechizoLanzado(lanzador: Luchador?, hechizo: StatHechizo?, objetivo: Luchador?) {
        _hechizosLanzados.add(HechizoLanzado(lanzador, hechizo, objetivo?.iD ?: 0))
    }

    val tipoIA: Int
        get() = if (iA == null) -1 else iA.getTipoIA()

    fun setTransportadoPor(transportadoPor: Luchador?) {
        portador = transportadoPor
    }

    var celdaPelea: Celda?
        get() = _celda
        set(celda) {
            _celda = celda
            if (_celda != null) {
                _celda.addLuchador(this)
            }
            for (l in _pelea.luchadoresDeEquipo(3)) {
                if (l.iA != null) {
                    l.iA.forzarRefrescarMov()
                }
            }
        }

    fun estaMuerto(): Boolean {
        return _estaMuerto
    }

    fun setEstaMuerto(m: Boolean) {
        _estaMuerto = m
        if (m && personaje != null && esInvocacion() && personaje.getCompañeros().get(0) != null) {
            personaje.getCompañeros().get(0).getCompañeros().remove(personaje)
        }
    }

    fun estaRetirado(): Boolean {
        return _estaRetirado
    }

    fun puedeGolpeCritico(SH: StatHechizo?): Boolean { // formula de golpes criticos
        var probGC: Int = SH.getProbabilidadGC()
        if (probGC < 2) {
            return false
        }
        if (tieneBuff(781)) { // mala sombra
            return false
        }
        val statsConBuff: TotalStats = totalStats
        var agilidad: Int = statsConBuff.getTotalStatParaMostrar(Constantes.STAT_MAS_AGILIDAD, pelea, null)
        if (agilidad < 0) {
            agilidad = 0
        }
        if (SH != null && personaje != null) {
            if (personaje.tieneModfiSetClase(SH.getHechizoID())) {
                val modi: Int = personaje.getModifSetClase(SH.getHechizoID(), 287)
                probGC -= modi
            }
        }
        probGC = ((probGC - statsConBuff.getTotalStatParaMostrar(Constantes.STAT_MAS_GOLPES_CRITICOS, pelea, null)) * (1.1 * Math.E
                / Math.log(agilidad + 12)))
        if (probGC < 2) {
            probGC = 2
        }
        val jet: Int = Formulas.getRandomInt(1, probGC)
        return jet == probGC
    }

    fun puedeFalloCritico(SH: StatHechizo?): Boolean {
        /*int probFC = SH.getProbabilidadFC();
		if (probFC < 2) {
			return false;
		}
		final TotalStats statsConBuff = getTotalStats();
		probFC = probFC - statsConBuff.getTotalStatParaMostrar(Constantes.STAT_MAS_FALLOS_CRITICOS,this.getPelea());
		if (probFC < 2) {
			probFC = 2;
		}
		final int jet = Formulas.getRandomInt(1, probFC);
		return jet == probFC;*/
        return false
    }

    val totalStats: TotalStats
        get() = _totalStats
    val baseStats: Stats
        get() = _totalStats.getStatsBase()
    val objetosStats: Stats
        get() = _totalStats.getStatsObjetos()
    val buffsStats: Stats
        get() = _totalStats.getStatsBuff()
    val buffsPelea: CopyOnWriteArrayList<Buff>
        get() = _buffsPelea

    fun stringGM(idMirador: Int): String {
        val str = StringBuilder()
        str.append((if (idMirador != 0 && esInvisible(idMirador)) 0 else _celda.getID()).toString() + ";")
        str.append(Camino.getIndexPorDireccion(direccion).toString() + ";") // direccion
        str.append("0^$_esAbonado;") // estrellas bonus
        str.append("$iD;")
        str.append(nombre.toString() + "^" + _colorNombre + ";")
        str.append(preLuchador.stringGMLuchador()) // ex _strGMLuchador
        str.append("$pDVConBuff;")
        str.append(_PArestantes).append(";")
        str.append(_PMrestantes).append(";")
        var resist = ""
        resist = when (_pelea.getTipoPelea()) {
            Constantes.PELEA_TIPO_DESAFIO, Constantes.PELEA_TIPO_KOLISEO, Constantes.PELEA_TIPO_PVP, Constantes.PELEA_TIPO_RECAUDADOR -> Constantes.STAT_MAS_RES_PORC_PVP_NEUTRAL.toString() + "," + Constantes.STAT_MAS_RES_PORC_PVP_TIERRA + ","
            +Constantes.STAT_MAS_RES_PORC_PVP_FUEGO + "," + Constantes.STAT_MAS_RES_PORC_PVP_AGUA + ","
                    + Constantes.STAT_MAS_RES_PORC_PVP_AIRE
            else -> Constantes.STAT_MAS_RES_PORC_NEUTRAL.toString() + "," + Constantes.STAT_MAS_RES_PORC_TIERRA + ","
            +Constantes.STAT_MAS_RES_PORC_FUEGO + "," + Constantes.STAT_MAS_RES_PORC_AGUA + ","
                    + Constantes.STAT_MAS_RES_PORC_AIRE
        }
        var perso: Personaje? = null
        if (preLuchador.getClass() === Personaje::class.java) {
            perso = preLuchador as Personaje?
        }
        resist += "," + Constantes.STAT_MAS_ESQUIVA_PERD_PA.toString() + "," + Constantes.STAT_MAS_ESQUIVA_PERD_PM
        for (r in resist.split(",")) {
            val statID: Int = Integer.parseInt(r)
            val total: Int = _totalStats.getTotalStatConComplemento(statID, pelea, personaje)
            str.append("$total;")
        }
        str.append("$equipoBin;")
        if (perso != null) {
            if (perso.estaMontando() && perso.getMontura() != null) {
                str.append(perso.getMontura().getStringColor(perso.stringColor()))
            }
            str.append(";")
        }
        str.append(_totalStats.getTotalStatConComplemento(Constantes.STAT_MAS_HUIDA, pelea, null).toString() + ";")
        str.append(_totalStats.getTotalStatConComplemento(Constantes.STAT_MAS_PLACAJE, pelea, null).toString() + ";")
        return str.toString()
    }

    val pDVMaxConBuff: Int
        get() = pDVMaxSinBuff + buffsStats.getStatParaPelea(Constantes.STAT_MAS_VITALIDAD, pelea)
    val pDVConBuff: Int
        get() = pDVSinBuff + buffsStats.getStatParaPelea(Constantes.STAT_MAS_VITALIDAD, pelea)
    val pMConBuff: Int
        get() = buffsStats.getStatParaPelea(Constantes.STAT_MAS_PM, pelea)
    val pAConBuff: Int
        get() = buffsStats.getStatParaPelea(Constantes.STAT_MAS_PA, pelea)

    fun restarPDV(pdv: Int) {
        // positivo = restar vida, negativo = curar}
        var pdv = pdv
        if (pdv > 0) {
            if (_escudo > 0) {
                val escudo = _escudo
                addEscudo(-pdv)
                pdv -= escudo
                if (pdv < 0) {
                    return
                }
            }
        }
        setPDV(pDVSinBuff - pdv)
        if (pdv > 0) {
            var pdvMax = pDVMaxSinBuff
            pdvMax -= Math.floor(pdv * MainServidor.PORCENTAJE_DAÑO_NO_CURABLE / 100)
            if (pdvMax < 1) {
                pdvMax = 1
            }
            setPDVMAX(pdvMax, false, false)
            if (_pelea.getRetos() != null && !_esDoble && esNoIA()) {
                for (entry in _pelea.getRetos().entrySet()) {
                    val reto: Reto = entry.getValue()
                    val retoID: Byte = entry.getKey()
                    val exitoReto: EstReto = reto.getEstado()
                    if (exitoReto !== Reto.EstReto.EN_ESPERA) {
                        continue
                    }
                    when (retoID) {
                        Constantes.RETO_CONTAMINACION -> setContaminado(true)
                    }
                    reto.setEstado(exitoReto)
                }
            }
        }
    }

    fun setPDV(pdv: Int) {
        pDVSinBuff = pdv
        if (pDVSinBuff > pDVMaxSinBuff) {
            pDVSinBuff = pDVMaxSinBuff
        }
    }

    fun setPDVMAX(pdvMax: Int, conPorc: Boolean, reevivir: Boolean) {
        var porc = 0
        if (pDVMaxSinBuff != 0) {
            porc = pDVSinBuff * 100 / pDVMaxSinBuff
        }
        val max = pdvMax >= pDVMaxSinBuff
        pDVMaxSinBuff = pdvMax
        if (pDVSinBuff > pDVMaxSinBuff) {
            pDVSinBuff = pDVMaxSinBuff
        }
        if (!conPorc) {
            return
        }
        if (Constantes.PELEA_FASE_INICIO === pelea.getFase() || reevivir) {
            limitarVida()
        }
        var newPDV = pDVMaxSinBuff * porc / 100
        newPDV = if (max) {
            Math.max(pDVSinBuff, newPDV)
        } else {
            Math.min(pDVSinBuff, newPDV)
        }
        setPDV(newPDV)
    }

    fun setPDVMAX(pdvMax: Int, conPorc: Boolean) {
        var porc = 0
        if (pDVMaxSinBuff != 0) {
            porc = pDVSinBuff * 100 / pDVMaxSinBuff
        }
        val max = pdvMax >= pDVMaxSinBuff
        pDVMaxSinBuff = pdvMax
        if (pDVSinBuff > pDVMaxSinBuff) {
            pDVSinBuff = pDVMaxSinBuff
        }
        if (!conPorc) {
            return
        }
        if (Constantes.PELEA_FASE_INICIO === pelea.getFase()) {
            limitarVida()
        }
        var newPDV = pDVMaxSinBuff * porc / 100
        newPDV = if (max) {
            Math.max(pDVSinBuff, newPDV)
        } else {
            Math.min(pDVSinBuff, newPDV)
        }
        setPDV(newPDV)
    }

    val porcPDV: Float
        get() {
            val vitalidad: Int = buffsStats.getStatParaPelea(Constantes.STAT_MAS_VITALIDAD, pelea)
            if (pDVMaxSinBuff + vitalidad <= 0) {
                return 0
            }
            var porc = (pDVSinBuff + vitalidad) * 100f / (pDVMaxSinBuff + vitalidad)
            porc = Math.max(0, porc)
            porc = Math.min(100, porc)
            return porc
        }

    fun setEstado(estado: Int, turnos: Int) {
        if (!sirveParaBuff) {
            return
        }
        if (turnos != 0) {
            if (_estados[estado] != null) {
                if (_estados[estado] === -1 || _estados[estado] > turnos) {
                    // no hace nada, porq es infinito o mayor al actual
                    return
                } else {
                    _estados.put(estado, turnos)
                }
            } else {
                _estados.put(estado, turnos)
            }
        } else {
            if (_estados[estado] == null) {
                return
            }
            _estados.remove(estado)
        }
        GestorSalida.ENVIAR_GA950_ACCION_PELEA_ESTADOS(_pelea, 7, iD, estado, turnos != 0)
    }

    val estado: Map<Any, Any?>
        get() = _estados

    fun tieneEstado(id: Int): Boolean {
        // return _estados.get(id) != null;
        return if (_estados[id] == null) {
            false
        } else _estados[id] !== 0
    }

    private fun disminuirEstados() {
        val copia: Map<Integer, Integer> = TreeMap<Integer, Integer>()
        for (est in _estados.entrySet()) {
            if (est.getValue() <= 0) {
                copia.put(est.getKey(), est.getValue())
                continue
            }
            val nVal: Int = est.getValue() - 1
            if (nVal == 0) {
                GestorSalida.ENVIAR_GA950_ACCION_PELEA_ESTADOS(_pelea, 7, iD, est.getKey(), false)
                continue
            }
            copia.put(est.getKey(), nVal)
        }
        _estados.clear()
        _estados.putAll(copia)
    }

    @Synchronized
    fun deshechizar(luchador: Luchador?, desbuffTodo: Boolean) { // desbuffear
        // if idLanzador es 0, deshechiza normal
        if (!_buffsPelea.isEmpty()) {
            var tiene = false
            val nuevosBuffs: ArrayList<Buff> = ArrayList()
            for (buff in _buffsPelea) {
                if (!buff.esDesbufeable()) {
                    nuevosBuffs.add(buff)
                    continue
                }
                if (Mundo.IDOLOS_HECHIZOS.contains(buff.getHechizoID())) {
                    nuevosBuffs.add(buff)
                    continue
                }
                if (!buff.getCondicionBuff().isEmpty()) {
                    continue
                }
                if (!desbuffTodo) {
                    if (luchador != null && buff.getLanzador().getID() !== luchador.iD) {
                        nuevosBuffs.add(buff)
                        continue
                    }
                }
                tiene = true
                var valor: Int = buff.getPrimerValor()
                when (buff.getEfectoID()) {
                    111, 120 -> {
                        valor = addPARestantes(-valor)
                        if (valor < 0) {
                            GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, 7, Constantes.STAT_MAS_PA, iD.toString() + "", iD.toString() + ","
                                    + valor)
                        }
                    }
                    101, 168, 84 -> {
                        valor = addPARestantes(valor)
                        if (valor > 0) {
                            GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, 7, Constantes.STAT_MAS_PA, iD.toString() + "", iD.toString() + ","
                                    + valor)
                        }
                    }
                    78, 128 -> {
                        valor = addPMRestantes(-valor)
                        if (valor < 0) {
                            GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, 7, Constantes.STAT_MAS_PM, iD.toString() + "", iD.toString() + ","
                                    + valor)
                        }
                    }
                    127, 169, 77 -> {
                        valor = addPMRestantes(valor)
                        if (valor > 0) {
                            GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, 7, Constantes.STAT_MAS_PM, iD.toString() + "", iD.toString() + ","
                                    + valor)
                        }
                    }
                    422 -> addEscudo(-buff.getPrimerValor())
                    150 -> hacerseVisible()
                }
            }
            // acaba el for
            if (!desbuffTodo && luchador != null) {
                if (!tiene) {
                    return
                } else {
                    GestorSalida.ENVIAR_GIe_QUITAR_BUFF(_pelea, 7, iD)
                }
            }
            resetearBuffs(nuevosBuffs)
            if (pDVConBuff <= 0) {
                _pelea.addMuertosReturnFinalizo(this, luchador)
            } else if (puedeJugar() && !estaRetirado() && personaje != null) {
                GestorSalida.ENVIAR_As_STATS_DEL_PJ(personaje)
            }
        }
    }

    fun addBuffConGIE(efectoID: Int, valor: Int, turnosRestantes: Int, hechizoID: Int,
                      args: String?, lanzador: Luchador?, conGIE: Boolean, tipo: TipoDaño, condicion: String): Duo<Boolean, Buff> {
        return addBuffConGIE(efectoID, valor, turnosRestantes, hechizoID, args, lanzador, conGIE, tipo, condicion, true)
    }

    fun addBuffConGIE(efectoID: Int, valor: Int, turnosRestantes: Int,
                      hechizoID: Int, args: String?, lanzador: Luchador?, conGIE: Boolean, tipo: TipoDaño,
                      condicionHechizo: String, inicioBuff: Boolean): Duo<Boolean, Buff> {
        var lanzador = lanzador
        var buff: Buff? = null
        var variosGIE = false
        if (sirveParaBuff) {
            variosGIE = true
            var desbufeable = true
            var tempTurnos = turnosRestantes
            if (inicioBuff && tipo !== TipoDaño.TRAMPA && puedeJugar()) {
                variosGIE = false
                tempTurnos++
            }
            if (tempTurnos == 0) {
                when (efectoID) {
                    81, 108, 82, 90, 275, 276, 277, 278, 279, 85, 86, 87, 88, 89, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 512 -> {
                    }
                    else -> {
                        variosGIE = false
                        tempTurnos = 1
                    }
                }
            }
            when (efectoID) {
                293, 294, 788, Constantes.STAT_MENOS_PORC_PDV_TEMPORAL, Constantes.STAT_DAR_ESTADO -> desbufeable = false
            }
            if (MainServidor.HECHIZOS_NO_DESECHIZABLE.contains(hechizoID) || Mundo.IDOLOS_HECHIZOS.contains(hechizoID)) {
                desbufeable = false
            }
            when (hechizoID) {
                413, 414, 108 -> desbufeable = false
            }
            var estado = false
            if (condicionHechizo.isEmpty()) {
                when (efectoID) {
                    Constantes.STAT_MAS_PM, Constantes.STAT_MAS_PM_2 -> addPMRestantes(valor)
                    Constantes.STAT_MAS_PA, Constantes.STAT_MAS_PA_2 -> addPARestantes(valor)
                    Constantes.STAT_MENOS_PM_FIJO, Constantes.STAT_MENOS_PM -> addPMRestantes(-valor)
                    Constantes.STAT_MENOS_PA_FIJO, Constantes.STAT_MENOS_PA -> addPARestantes(-valor)
                    Constantes.STAT_QUITAR_ESTADO -> {
                        tempTurnos = 0
                        setEstado(valor, tempTurnos)
                        estado = true
                    }
                    Constantes.STAT_DAR_ESTADO -> {
                        setEstado(valor, tempTurnos)
                        estado = true
                    }
                }
            }
            if (Mundo.IDOLOS_HECHIZOS.contains(hechizoID)) {
                lanzador = this
            }
            if (!estado) {
                buff = Buff(if (efectoID == 424) 153 else efectoID, hechizoID, desbufeable, tempTurnos, lanzador, args, tipo)
                addBuff(buff)
            }
            if (!condicionHechizo.isEmpty()) {
                if (buff != null) {
                    buff.setCondBuff(condicionHechizo)
                }
            } else if (conGIE || !variosGIE) {
                GestorSalida.ENVIAR_GA998_AGREGAR_BUFF_PELEA(_pelea, 7, Pelea.getStrParaGA998(efectoID, iD, tempTurnos,
                        hechizoID, args))
            }
        }
        return Duo<Boolean, Buff>(variosGIE, buff)
    }

    fun esDoble(): Boolean {
        return _esDoble
    }

    val nivel: Int
        get() = preLuchador.getNivel()
    val nivelAlineacion: Int
        get() = preLuchador.getGradoAlineacion()

    fun xpStringLuch(str: String): String {
        return if (preLuchador.getClass() === Personaje::class.java) {
            (preLuchador as Personaje?).stringExperiencia(str)
        } else "0" + str + "0" + str + "0"
    }

    fun addKamasLuchador() {
        if (esInvocacion()) {
            try {
                invocador!!.personaje.addKamas(kamasGanadas, false, false)
            } catch (e: Exception) {
            }
        } else if (personaje != null) {
            personaje.addKamas(kamasGanadas, false, false)
        } else if (recaudador != null) {
            // nada
        } else if (mob != null) {
            if (equipoBin.toInt() == 1) {
                if (_pelea.getMobGrupo() != null) {
                    _pelea.getMobGrupo().addKamasHeroico(kamasGanadas)
                    _pelea.setSalvarMobHeroico(true)
                }
            }
        }
    }

    fun addObjetoAInventario(obj: Objeto?) {
        if (esInvocacion()) {
            try {
                invocador!!.personaje.addObjDropPelea(obj, true)
            } catch (e: Exception) {
            }
        } else if (personaje != null) {
            personaje.addObjDropPelea(obj, true)
        } else if (recaudador != null) {
            recaudador.addObjAInventario(obj)
        } else if (mob != null) {
            if (equipoBin.toInt() == 1) {
                if (_pelea.getMobGrupo() != null) {
                    _pelea.getMobGrupo().addObjAInventario(obj)
                    _pelea.setSalvarMobHeroico(true)
                }
            }
        }
    }

    fun addDropLuchador(objeto: Objeto, addInventario: Boolean) {
        if (_objDropeados == null) {
            _objDropeados = HashMap<Objeto, Boolean>()
        }
        // tipo piedra de alma y mascota
        if (objeto.puedeTenerStatsIguales()) {
            for (obj in _objDropeados.keySet()) {
                if (obj == null) {
                    continue
                }
                if (Constantes.esPosicionEquipamiento(obj.getPosicion())) {
                    continue
                }
                if (obj.getObjModeloID() === objeto.getObjModeloID() && obj.sonStatsIguales(objeto)) {
                    obj.setCantidad(obj.getCantidad() + objeto.getCantidad())
                    if (objeto.getID() > 0) {
                        Mundo.eliminarObjeto(objeto.getID())
                    }
                    return
                }
            }
        }
        _objDropeados.put(objeto, addInventario)
    }

    val personaje: Personaje?
        get() {
            if (_esDoble) {
                return null
            }
            return if (preLuchador.getClass() === Personaje::class.java) {
                preLuchador as Personaje?
            } else null
        }
    val mob: MobGrado?
        get() = if (preLuchador.getClass() === MobGrado::class.java) {
            preLuchador as MobGrado?
        } else null
    val recaudador: Recaudador?
        get() = if (preLuchador.getClass() === Recaudador::class.java) {
            preLuchador as Recaudador?
        } else null
    val prisma: Prisma?
        get() = if (preLuchador.getClass() === Prisma::class.java) {
            preLuchador as Prisma?
        } else null
    val paramEquipoAliado: Byte
        get() = _pelea.getParamMiEquipo(iD)
    val paramEquipoEnemigo: Byte
        get() = _pelea.getParamEquipoEnemigo(iD)

    fun puedeJugar(): Boolean {
        return _puedeJugar
    }

    var isInvocacion = false
    var invocacionNoLatigo = false
    var isRevivido = false
    fun esInvocacion(): Boolean {
        return invocador != null && !_pelea.esLuchInicioPelea(this) || isInvocacion
    }

    fun addNroInvocaciones(add: Int) {
        nroInvocaciones += add
    }

    fun fullPDV() {
        pDVSinBuff = pDVMaxSinBuff
    }

    fun setTurnosRestantes(_turnosRestantes: Int) {
        turnosRestantes = _turnosRestantes.toByte()
    }

    fun estaDesconectado(): Boolean {
        return _desconectado
    }

    fun setDesconectado(_desconectado: Boolean) {
        this._desconectado = _desconectado
    }

    fun esEspectadorAdmin(): Boolean {
        return _espectadorAdmin
    }

    fun setEspectadorAdmin(_espectadorAdmin: Boolean) {
        this._espectadorAdmin = _espectadorAdmin
    }

    fun setPuedeJugar(_puedeJugar: Boolean) {
        this._puedeJugar = _puedeJugar
    }

    val iA: Inteligencia?
        get() = _IA

    fun setInteligenciaArtificial(_IA: Inteligencia?) {
        this._IA = _IA
    }

    fun addTurnosParaMorir() {
        turnosParaMorir++
    }

    fun estaContaminado(): Boolean {
        return _contaminado
    }

    fun setContaminado(_contaminado: Boolean) {
        this._contaminado = _contaminado
    }

    val bonusCastigo: Map<Any, Any>
        get() = _bonusCastigo
    val hechizosLanzadosReto: ArrayList<Integer>
        get() = _hechiLanzadosReto
    val mobsAsesinadosReto: ArrayList<Integer>
        get() = _retoMobsAsesinados
    var expGanada: Long
        get() = _xpGanada
        set(_expGanada) {
            var _expGanada = _expGanada
            if (_expGanada <= 0) {
                _expGanada = 0
            }
            _xpGanada = _expGanada
        }
    var kamasGanadas: Long
        get() = _kamasGanadas
        set(_kamasGanadas) {
            var _kamasGanadas = _kamasGanadas
            if (_kamasGanadas <= 0) {
                _kamasGanadas = 0
            }
            this._kamasGanadas = _kamasGanadas
        }

    fun setEstaRetirado(b: Boolean) {
        _estaRetirado = b
    }

    val objDropeados: Map<Any, Boolean>?
        get() = _objDropeados

    fun setProspeccion(_prospeccion: Int) {
        prospeccionLuchador = _prospeccion
    }

    init {
        preLuchador = pre
        _pelea = pelea
        iD = pre.getID()
        if (pre.getClass() === Personaje::class.java) {
            _tipoLuch = 1
            nombre = (pre as Personaje).getNombre()
            _colorNombre = (pre as Personaje).getColorNombre()
            _esAbonado = (pre as Personaje).esAbonado()
        } else if (pre.getClass() === MobGrado::class.java) {
            _tipoLuch = 4
            // final int IA = ((MobGrado) pre).getMobModelo().getTipoIA();
            // if (IA == 0 || IA == 9 || IA == 6) {
            // _sirveParaBuff = true;
            // }
            setInteligenciaArtificial(Inteligencia(this, _pelea))
            nombre = (pre as MobGrado).getIDModelo().toString() + ""
        } else if (pre.getClass() === Recaudador::class.java) {
            _tipoLuch = 5
            setInteligenciaArtificial(Inteligencia(this, _pelea))
            nombre = (pre as Recaudador).getN1().toString() + "," + (pre as Recaudador).getN2()
        } else if (pre.getClass() === Prisma::class.java) {
            _tipoLuch = 2
            setInteligenciaArtificial(Inteligencia(this, _pelea))
            nombre = (if (pre.getAlineacion() === Constantes.ALINEACION_BONTARIANO) 1111 else 1112).toString() + ""
        }
        // _strGMLuchador = pre.stringGMLuchador();
        nivelViejo = pre.getNivel()
        _totalStats = preLuchador.getTotalStatsPelea()
        limpiarStatsBuffs()
        resetPuntos()
        if (espectador) {
            return
        }
        alineacion = pre.getAlineacion()
        pDVMaxSinBuff = pre.getPDVMax()
        pDVSinBuff = pre.getPDV()
        limitarVida()
        gfxID = pre.getGfxID(false)
    }
}