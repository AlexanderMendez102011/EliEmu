package variables.montura

import java.util.ArrayList

class Montura : Exchanger {
    enum class Ubicacion {
        ESTABLO, CERCADO, PERGAMINO, EQUIPADA, NULL
    }

    private var _salvaje: Boolean
    private val _sexo: Byte
    private var _orientacion: Byte = 1
    private var _talla: Byte = 100
    private var _reproducciones: Byte = 0
    private var _mapa: Mapa? = null
    private var _celda: Celda? = null
    val iD: Int
    val color: Int
    var dueñoID: Int
    var nivel = 1
        private set
    var parejaID = -1
        private set
    private var _certificadoID = -1
    var fatiga = 0
    private var _energia = 0
    var madurez = 0
        private set
    var serenidad = 0
        private set
    var amor = 0
    var resistencia = 0
    private var _semiPod = 0
    private var _maxPod = 0
    private var maxMadurez = 0
    private var _maxEnergia = 0
    var exp: Long = 0
        private set
    private var _tiempoInicioDescanso: Long = 0
    var tiempoFecundacion: Long
        private set
    private val _stats: Stats = Stats()
    private val _statsLocal: Map<Integer, Integer> = HashMap<Integer, Integer>()
    var ancestros = "?,?,?,?,?,?,?,?,?,?,?,?,?,?"
        private set
    var nombre = "Sin Nombre"
    private var _statsString = ""
    private val _objetos: Map<Integer, Objeto> = TreeMap<Integer, Objeto>()
    private val _habilidades: ArrayList<Byte> = ArrayList<Byte>(2)
    private var _ubicacion = Ubicacion.PERGAMINO // por defecto
    private var _monturaModelo: MonturaModelo?

    constructor(color: Int, dueño: Int, castrado: Boolean, salvaje: Boolean) {
        iD = Mundo.sigIDMontura()
        _sexo = SEXO_POSIBLES[Formulas.getRandomInt(0, SEXO_POSIBLES.size - 1)]
        this.color = color
        _monturaModelo = Mundo.getMonturaModelo(this.color)
        addExperiencia(Mundo.getExpMontura(MainServidor.INICIO_NIVEL_MONTURA))
        _energia = maxEnergia
        madurez = maxMadurez
        dueñoID = dueño
        if (castrado) {
            castrarPavo()
        }
        _salvaje = salvaje
        tiempoFecundacion = 0
        statsMontura
        maximos()
        Mundo.addMontura(this, true)
    }

    constructor(madre: Montura, padre: Montura?) {
        var padre = padre
        if (padre == null) {
            padre = madre
        }
        iD = Mundo.sigIDMontura()
        _sexo = SEXO_POSIBLES[Formulas.getRandomInt(0, SEXO_POSIBLES.size - 1)]
        color = Constantes.getColorCria(madre.color, padre.color, madre._habilidades.contains(
                Constantes.HABILIDAD_PREDISPUESTA), padre._habilidades.contains(Constantes.HABILIDAD_PREDISPUESTA))
        _monturaModelo = Mundo.getMonturaModelo(color)
        addExperiencia(Mundo.getExpMontura(MainServidor.INICIO_NIVEL_MONTURA))
        val papa: Array<String> = padre.ancestros.split(",")
        val mama: Array<String> = madre.ancestros.split(",")
        val primero_papa = papa[0] + "," + papa[1]
        val primera_mama = mama[0] + "," + mama[1]
        val segundo_papa = papa[2] + "," + papa[3] + "," + papa[4] + "," + papa[5]
        val segunda_mama = mama[2] + "," + mama[3] + "," + mama[4] + "," + mama[5]
        ancestros = (padre.color.toString() + "," + madre.color + "," + primero_papa + "," + primera_mama + ","
                + segundo_papa + "," + segunda_mama)
        for (i in 1..2) {
            val habilidad = Formulas.getRandomInt(1, 20) as Byte
            if (habilidad >= 9) {
                continue
            }
            addHabilidad(habilidad)
        }
        dueñoID = madre.dueñoID
        _talla = 50
        _salvaje = false
        tiempoFecundacion = 0
        statsMontura
        maximos()
        Mundo.addMontura(this, true)
    }

    constructor(id: Int, color: Int, sexo: Byte, amor: Int, resistencia: Int, nivel: Int,
                exp: Long, nombre: String, fatiga: Int, energia: Int, reprod: Byte, madurez: Int,
                serenidad: Int, objetos: String, anc: String, habilidad: String, talla: Byte,
                celda: Short, mapa: Short, dueño: Int, orientacion: Byte, fecundada: Long, pareja: Int,
                salvaje: Byte, stats: String) {
        iD = id
        this.color = color
        _monturaModelo = Mundo.getMonturaModelo(color)
        _sexo = sexo
        addExperiencia(exp)
        this.amor = amor
        this.resistencia = resistencia
        this.nombre = nombre
        this.fatiga = fatiga
        _energia = energia
        _reproducciones = reprod
        this.madurez = madurez
        this.serenidad = serenidad
        ancestros = anc
        _talla = talla
        _mapa = Mundo.getMapa(mapa)
        if (_mapa != null) {
            _celda = _mapa.getCelda(celda)
            if (_celda != null) {
                ubicacion = Ubicacion.CERCADO
            }
        }
        dueñoID = dueño
        _orientacion = orientacion
        tiempoFecundacion = fecundada
        parejaID = pareja
        _salvaje = salvaje.toInt() == 1
        for (s in habilidad.split(",")) {
            try {
                if (s.isEmpty()) {
                    continue
                }
                _habilidades.add(Byte.parseByte(s))
            } catch (e: Exception) {
            }
        }
        objetos.replaceAll(";", ",")
        for (str in objetos.split(",")) {
            try {
                if (str.isEmpty()) {
                    continue
                }
                val obj: Objeto = Mundo.getObjeto(Integer.parseInt(str))
                if (obj != null) {
                    //objeto_Desasociar_Mimobionte(obj);
                    _objetos.put(Integer.parseInt(str), obj)
                }
            } catch (e: Exception) {
            }
        }
        _statsString = stats
        agregarStatsLocal(stats)
        statsMontura
        maximos()
    }

    /*private void objeto_Desasociar_Mimobionte(Objeto objeto) {
		try {
			if(objeto.tieneStatTexto(Constantes.STAT_APARIENCIA_OBJETO)) {
				int regresar = Integer.parseInt(objeto.getParamStatTexto(Constantes.STAT_APARIENCIA_OBJETO, 3), 16);
				Objeto apariencia = Mundo.getObjetoModelo(regresar).crearObjeto(1,Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM);
				Mundo.addObjeto(apariencia, true);
				objeto.addStatTexto(Constantes.STAT_APARIENCIA_OBJETO, "");
				_objetos.put(apariencia.getID(),apariencia);
				GestorSQL.SALVAR_OBJETO(objeto);
				System.out.println("Cofre: "+_id+" disasicion objeto: "+objeto.getObjModelo().getNombre());
			}
		} catch (Exception e) {
		}
	}*/
    val statsMontura: Unit
        get() {
            if (_monturaModelo == null) {
                return
            }
            _stats.clear()
            if (_statsString.length() > 0) {
                for (stat in _statsLocal.entrySet()) {
                    val valor: Int = stat.getValue() * nivel / MainServidor.NIVEL_MAX_MONTURA
                    if (valor > 0) _stats.addStatID(stat.getKey(), valor)
                }
            } else {
                for (stat in _monturaModelo.getStats().entrySet()) {
                    val valor: Int = stat.getValue() * nivel / MainServidor.NIVEL_MAX_MONTURA
                    if (valor > 0) _stats.addStatID(stat.getKey(), valor)
                }
            }
            return
        }
    val objModCertificado: ObjetoModelo?
        get() = if (_monturaModelo == null) {
            null
        } else Mundo.getObjetoModelo(_monturaModelo.getCertificadoModeloID())

    fun disminuirFatiga() {
        if (_tiempoInicioDescanso == 0L || fatiga == 0) {
            return
        }
        if (System.currentTimeMillis() - _tiempoInicioDescanso >= 60 * 60 * 1000) {
            _tiempoInicioDescanso = System.currentTimeMillis()
            restarFatiga()
        }
    }

    var ubicacion: Ubicacion
        get() = _ubicacion
        set(ubicacion) {
            _ubicacion = ubicacion
            _tiempoInicioDescanso = if (_ubicacion == Ubicacion.ESTABLO) {
                System.currentTimeMillis()
            } else {
                0
            }
        }

    private fun maximos() {
        if (_monturaModelo == null) {
            return
        }
        val _generacion: Int = _monturaModelo.getGeneracionID()
        _semiPod = (_generacion + 1) / 2 * 5
        _maxPod = 50 + 50 * _generacion
        maxMadurez = 1000 * _generacion
        _maxEnergia = 1000 + (_generacion - 1) * 100
    }

    var pergamino: Int
        get() = _certificadoID
        set(pergamino) {
            _certificadoID = pergamino
            if (_certificadoID > 0) {
                ubicacion = Ubicacion.PERGAMINO
            }
        }

    fun esSalvaje(): Boolean {
        return _salvaje
    }

    fun setSalvaje(s: Boolean) {
        _salvaje = s
    }

    val sexo: Int
        get() = _sexo.toInt()
    val pods: Int
        get() {
            var pods = 0
            for (obj in _objetos.values()) {
                pods += obj.getObjModelo().getPeso() * obj.getCantidad()
            }
            return pods
        }

    fun getListaExchanger(perso: Personaje?): String {
        val objetos = StringBuilder()
        for (obj in _objetos.values()) {
            objetos.append("O" + obj.stringObjetoConGuiño())
        }
        return objetos.toString()
    }

    private fun getSimilarObjeto(objeto: Objeto): Objeto? {
        if (objeto.puedeTenerStatsIguales()) {
            for (obj in _objetos.values()) {
                if (Constantes.esPosicionEquipamiento(obj.getPosicion())) {
                    continue
                }
                if (objeto.getID() !== obj.getID() && obj.getObjModeloID() === objeto.getObjModeloID() && obj.sonStatsIguales(
                                objeto)) {
                    return obj
                }
            }
        }
        return null
    }

    @Override
    @Synchronized
    fun addObjetoExchanger(objeto: Objeto, cantidad: Long, perso: Personaje, precio: Int) {
        var cantidad = cantidad
        if (pods >= totalPods) {
            return
        }
        if (!perso.tieneObjetoID(objeto.getID()) || objeto.getPosicion() !== Constantes.OBJETO_POS_NO_EQUIPADO) {
            GestorSalida.ENVIAR_Im_INFORMACION(perso, "1OBJECT_DONT_EXIST")
            return
        }
        if (cantidad > objeto.getCantidad()) {
            cantidad = objeto.getCantidad()
        }
        var str = ""
        var objMontura: Objeto? = getSimilarObjeto(objeto)
        val nuevaCant: Long = objeto.getCantidad() - cantidad
        if (objMontura == null) {
            if (nuevaCant <= 0) {
                perso.borrarOEliminarConOR(objeto.getID(), false)
                _objetos.put(objeto.getID(), objeto)
                str = "O+" + objeto.getID().toString() + "|" + objeto.getCantidad().toString() + "|" + objeto.getObjModeloID().toString() + "|" + objeto
                        .convertirStatsAString(false)
            } else {
                objMontura = objeto.clonarObjeto(cantidad, Constantes.OBJETO_POS_NO_EQUIPADO)
                Mundo.addObjeto(objMontura, false)
                _objetos.put(objMontura.getID(), objMontura)
                objeto.setCantidad(nuevaCant)
                str = ("O+" + objMontura.getID().toString() + "|" + objMontura.getCantidad().toString() + "|" + objMontura.getObjModeloID().toString() + "|"
                        + objMontura.convertirStatsAString(false))
                GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(perso, objeto)
            }
        } else {
            if (nuevaCant <= 0) {
                perso.borrarOEliminarConOR(objeto.getID(), true)
                objMontura.setCantidad(objMontura.getCantidad() + objeto.getCantidad())
                str = ("O+" + objMontura.getID().toString() + "|" + objMontura.getCantidad().toString() + "|" + objMontura.getObjModeloID().toString() + "|"
                        + objMontura.convertirStatsAString(false))
            } else {
                objeto.setCantidad(nuevaCant)
                objMontura.setCantidad(objMontura.getCantidad() + cantidad)
                str = ("O+" + objMontura.getID().toString() + "|" + objMontura.getCantidad().toString() + "|" + objMontura.getObjModeloID().toString() + "|"
                        + objMontura.convertirStatsAString(false))
                GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(perso, objeto)
            }
        }
        GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(perso, str)
    }

    @Override
    @Synchronized
    fun remObjetoExchanger(objeto: Objeto, cantidad: Long, perso: Personaje, precio: Int) {
        var cantidad = cantidad
        if (!_objetos.containsKey(objeto.getID())) {
            return
        }
        if (cantidad > objeto.getCantidad()) {
            cantidad = objeto.getCantidad()
        }
        val nuevaCant: Long = objeto.getCantidad() - cantidad
        val str: String
        str = if (nuevaCant < 1) {
            _objetos.remove(objeto.getID())
            perso.addObjIdentAInventario(objeto, true)
            "O-" + objeto.getID()
        } else {
            val nuevoObj: Objeto = objeto.clonarObjeto(cantidad, Constantes.OBJETO_POS_NO_EQUIPADO)
            perso.addObjIdentAInventario(nuevoObj, true)
            objeto.setCantidad(nuevaCant)
            "O+" + objeto.getID().toString() + "|" + objeto.getCantidad().toString() + "|" + objeto.getObjModeloID().toString() + "|" + objeto
                    .convertirStatsAString(false)
        }
        GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(perso, str)
    }

    fun estaCriando(): Boolean {
        return _celda != null
    }

    val fecundadaHaceMinutos: Int
        get() {
            if (esCastrado() || _reproducciones >= 20 || tiempoFecundacion <= 0) {
                tiempoFecundacion = 0
                return -1
            }
            val minutos = ((System.currentTimeMillis() - tiempoFecundacion) / (60 * 1000)) as Int
            return minutos + 1
        }

    private fun disponibleParaFecundar(): Boolean {
        if (esCastrado() || _reproducciones >= 20 || tiempoFecundacion > 0) {
            return false
        }
        return if (amor >= 7500 && resistencia >= 7500 && (_salvaje || nivel >= 5)) {
            true
        } else false
    }

    fun setMapaCelda(mapa: Mapa?, celda: Celda?) {
        _mapa = mapa
        _celda = celda
    }

    val mapa: Mapa?
        get() = _mapa
    val celda: Celda?
        get() = _celda
    val talla: Int
        get() = _talla.toInt()
    val energia: Int
        get() = if (MainServidor.PARAM_PERDER_ENERGIA) _energia else maxEnergia
    val reprod: Int
        get() = _reproducciones.toInt()
    val stats: Stats
        get() = _stats
    val objetos: Collection<Any>
        get() = _objetos.values()
    val capacidades: ArrayList<Byte>
        get() = _habilidades

    fun castrarPavo() {
        _reproducciones = -1
    }

    fun quitarCastrado() {
        _reproducciones = 0
    }

    fun strCapacidades(): String {
        val s = StringBuilder()
        for (b in _habilidades) {
            if (s.length() > 0) {
                s.append(",")
            }
            s.append(b)
        }
        return s.toString()
    }

    fun detallesMontura(): String {
        val str = StringBuilder("$iD:")
        str.append("$color:")
        str.append("$ancestros:")
        str.append(",," + strCapacidades() + ":")
        str.append("$nombre:")
        str.append("$_sexo:")
        str.append(stringExp() + ":")
        str.append("$nivel:")
        str.append((if (esMontable()) "1" else "0") + ":")
        str.append("$totalPods:")
        str.append((if (_salvaje) 1 else 0).toString() + ":") // salvaje
        str.append("$resistencia,10000:")
        str.append("$madurez,$maxMadurez:")
        str.append("$energia,$maxEnergia:")
        str.append("$serenidad,-10000,10000:")
        str.append("$amor,10000:")
        str.append("$fecundadaHaceMinutos:")
        str.append((if (disponibleParaFecundar()) 10 else 0).toString() + ":")
        str.append(convertirStringAStats() + ":")
        str.append("$fatiga,240:")
        str.append("$_reproducciones,20:")
        return str.toString()
    }

    private fun convertirStringAStats(): String {
        val stats = StringBuilder()
        for (entry in _stats.getEntrySet()) {
            if (stats.length() > 0) {
                stats.append(",")
            }
            stats.append(Integer.toHexString(entry.getKey()).toString() + "#" + Integer.toHexString(entry.getValue()) + "#0#0")
        }
        return stats.toString()
    }

    private val maxEnergia: Int
        private get() = _maxEnergia + _maxPod / 10 * nivel

    // portadora
    val totalPods: Int
        get() =// portadora
            (if (_habilidades.contains(Constantes.HABILIDAD_PORTADORA)) 2 else 1) * (_maxPod + _semiPod * nivel)

    private fun stringExp(): String {
        return exp.toString() + "," + Mundo.getExpMontura(nivel) + "," + Mundo.getExpMontura(nivel + 1)
    }

    fun esMontable(): Boolean {
        if (MainServidor.PARAM_MONTURA_SIEMPRE_MONTABLES) {
            return true
        }
        if (!MainServidor.PARAM_CRIAR_MONTURA || _monturaModelo != null && _monturaModelo.getColorID() === 88) {
            return true
        }
        return if (_salvaje || energia < 10 || madurez < maxMadurez || fatiga >= 240 || (MainServidor.MODO_ANKALIKE
                        && nivel < 5)) {
            false
        } else true
    }

    fun setMaxMadurez() {
        madurez = maxMadurez
    }

    fun setMaxEnergia() {
        _energia = maxEnergia
    }

    private fun restarFatiga() {
        if (!MainServidor.PARAM_CRIAR_MONTURA) {
            return
        }
        fatiga -= 10
        if (fatiga < 0) {
            fatiga = 0
        }
    }

    fun restarAmor(amor: Int) {
        if (!MainServidor.PARAM_CRIAR_MONTURA) {
            return
        }
        this.amor -= amor
        if (this.amor < 0) {
            this.amor = 0
        }
    }

    fun restarResistencia(resistencia: Int) {
        if (!MainServidor.PARAM_CRIAR_MONTURA) {
            return
        }
        this.resistencia -= resistencia
        if (this.resistencia < 0) {
            this.resistencia = 0
        }
    }

    private fun restarSerenidad() {
        if (!MainServidor.PARAM_CRIAR_MONTURA) {
            return
        }
        serenidad -= 100 * MainServidor.RATE_CRIANZA_MONTURA
        if (serenidad < -10000) {
            serenidad = -10000
        }
    }

    private fun aumentarMadurez() {
        val maxMadurez = maxMadurez
        if (madurez < maxMadurez) {
            madurez += 100 * MainServidor.RATE_CRIANZA_MONTURA
            if (_habilidades.contains(Constantes.HABILIDAD_PRECOZ)) {
                madurez += 100 * MainServidor.RATE_CRIANZA_MONTURA
            }
            if (_talla < 100) {
                val talla = _talla
                if (maxMadurez / madurez <= 1) {
                    _talla = 100
                } else if (_talla < 75 && maxMadurez / madurez == 2) {
                    _talla = 75
                } else if (_talla < 50 && maxMadurez / madurez >= 3) {
                    _talla = 50
                }
                if (talla != _talla) GestorSalida.ENVIAR_GM_DRAGOPAVO_A_MAPA(_mapa, "~", this)
            }
        }
        if (madurez > maxMadurez) {
            madurez = maxMadurez
        }
    }

    private fun aumentarAmor() { // enamorada
        if (!MainServidor.PARAM_CRIAR_MONTURA) {
            return
        }
        amor += 100 * MainServidor.RATE_CRIANZA_MONTURA
        if (amor > 10000) {
            amor = 10000
        }
    }

    private fun aumentarResistencia() { // resistente
        if (!MainServidor.PARAM_CRIAR_MONTURA) {
            return
        }
        resistencia += ((if (_habilidades.contains(Constantes.HABILIDAD_RESISTENTE)) 2 else 1) * 100
                * MainServidor.RATE_CRIANZA_MONTURA)
        if (resistencia > 10000) {
            resistencia = 10000
        }
    }

    private fun aumentarFatiga() { // infatigable
        if (!MainServidor.PARAM_CRIAR_MONTURA) {
            return
        }
        fatiga += if (_habilidades.contains(Constantes.HABILIDAD_INFATIGABLE)) 1 else 2
        if (fatiga > 240) {
            fatiga = 240
        }
    }

    private fun aumentarSerenidad() {
        if (!MainServidor.PARAM_CRIAR_MONTURA) {
            return
        }
        serenidad += 100 * MainServidor.RATE_CRIANZA_MONTURA
        if (serenidad > 10000) {
            serenidad = 10000
        }
    }

    private fun aumentarEnergia() {
        if (!MainServidor.PARAM_CRIAR_MONTURA) {
            return
        }
        _energia += 10 * MainServidor.RATE_CRIANZA_MONTURA
        val maxEnergia = maxEnergia
        if (_energia > maxEnergia) {
            _energia = maxEnergia
        }
    }

    fun aumentarEnergia(valor: Int, veces: Long) {
        _energia += (valor * veces).toInt()
        val maxEnergia = maxEnergia
        if (_energia > maxEnergia) {
            _energia = maxEnergia
        }
    }

    fun energiaPerdida(energia: Int) {
        _energia -= energia
        if (_energia < 0) {
            _energia = 0
        }
    }

    fun aumentarReproduccion() {
        if (esCastrado()) {
            return
        }
        (_reproducciones += 1).toByte()
    }

    fun stringObjetosBD(): String {
        val str = StringBuilder()
        for (id in _objetos.keySet()) {
            str.append((if (str.length() > 0) "," else "") + id)
        }
        return str.toString()
    }

    fun addExperiencia(exp: Long) {
        var exp = exp
        if (_habilidades.contains(Constantes.HABILIDAD_SABIA)) {
            exp *= 2
        }
        val nivel = nivel
        this.exp += exp
        while (this.exp >= Mundo.getExpMontura(this.nivel + 1) && this.nivel < MainServidor.NIVEL_MAX_MONTURA) {
            this.nivel++
        }
        if (nivel != this.nivel) {
            statsMontura
        }
    }

    fun getStringColor(colorDueñoPavo: String): String {
        return color.toString() + if (_habilidades.contains(Constantes.HABILIDAD_CAMALEON)) ",$colorDueñoPavo" else ""
    }

    fun addHabilidad(habilidad: Byte) {
        if (habilidad >= 1 && habilidad <= 9) {
            _habilidades.add(habilidad)
        }
    }

    val orientacion: Int
        get() = _orientacion.toInt()

    fun setFecundada(b: Boolean) {
        if (esCastrado() || _sexo == Constantes.SEXO_MASCULINO) {
            tiempoFecundacion = 0
            return
        }
        tiempoFecundacion = if (b) System.currentTimeMillis() else 0
    }

    fun setFecundada(minutos: Int) {
        if (_sexo == Constantes.SEXO_MASCULINO) {
            return
        }
        tiempoFecundacion = System.currentTimeMillis() - minutos * 60 * 1000
    }

    fun esCastrado(): Boolean {
        return _reproducciones.toInt() == -1
    }

    fun stringGM(): String {
        val str = StringBuilder("")
        if (_celda == null) {
            str.append(_mapa.getCercado().getCeldaMontura())
        } else {
            str.append(_celda.getID())
        }
        str.append(";")
        str.append(_orientacion.toString() + ";0;" + iD + ";" + nombre + ";-9;")
        if (color == 88) {
            str.append(7005)
        } else {
            str.append(7002)
        }
        str.append("^$_talla;")
        try {
            str.append(Mundo.getPersonaje(dueñoID).getNombre())
        } catch (e: Exception) {
            str.append("Sin Dueño")
        }
        str.append(";" + nivel + ";" + color)
        return str.toString()
    }

    fun moverMontura(dueño: Personaje?, dir: Int, celdasAMover: Int, alejar: Boolean) {
        val cercado: Cercado = _mapa.getCercado()
        if (_mapa == null || _celda == null || cercado == null) {
            return
        }
        var direccion: Int
        val celdaInicio: Short = _celda.getID()
        direccion = if (dir == -1) {
            if (dueño == null || dueño.getCelda().getID() === celdaInicio) {
                return
            }
            Camino.direccionEntreDosCeldas(_mapa, celdaInicio, dueño.getCelda().getID(), true)
        } else {
            dir
        }
        if (alejar) {
            direccion = Camino.getDireccionOpuesta(direccion)
        }
        val cDir: Char = Camino.getDireccionPorIndex(direccion)
        var accion = 0
        var celdasMovidas = 0
        val path = StringBuilder()
        var tempCeldaID = celdaInicio
        var celdaPrueba = celdaInicio
        var golpeoObjetoCrianza = false
        for (i in 0 until celdasAMover) {
            celdaPrueba = Camino.getSigIDCeldaMismaDir(celdaPrueba, direccion, _mapa, false)
            if (_mapa.getCelda(celdaPrueba) == null) {
                return
            }
            if (cercado.getObjetosCrianza().containsKey(celdaPrueba)) {
                val objeto: Objeto = cercado.getObjetosCrianza().get(celdaPrueba)
                if (objeto == null && !cercado.esPublico()) {
                    break
                }
                golpeoObjetoCrianza = true
                val caract: Int = Constantes.getCaracObjCria(if (objeto == null) Constantes.getObjCriaPorMapa(_mapa.getID()) else objeto.getObjModeloID())
                when (caract) {
                    1 -> if (serenidad <= 2000 && serenidad >= -2000) {
                        aumentarMadurez()
                    }
                    2 -> if (serenidad < 0) {
                        aumentarResistencia()
                    }
                    3 -> if (serenidad > 0) {
                        aumentarAmor()
                    }
                    4 -> restarSerenidad()
                    5 -> aumentarSerenidad()
                    6 -> {
                        restarFatiga()
                        aumentarEnergia()
                    }
                }
                aumentarFatiga()
                if (!cercado.esPublico()) {
                    if (objeto.addDurabilidad(-MainServidor.DURABILIDAD_REDUCIR_OBJETO_CRIA)) {
                        if (cercado.retirarObjCria(celdaPrueba, null)) {
                            GestorSalida.ENVIAR_GDO_OBJETO_TIRAR_SUELO(_mapa, '-', celdaPrueba, 0, false, "")
                        }
                    } else {
                        GestorSalida.ENVIAR_GDO_OBJETO_TIRAR_SUELO(_mapa, '+', celdaPrueba, objeto.getObjModeloID(), true, objeto
                                .getDurabilidad().toString() + ";" + objeto.getDurabilidadMax())
                    }
                }
                break
            }
            if (!_mapa.getCelda(celdaPrueba).esCaminable(false) || cercado.getCeldaPuerta() === celdaPrueba || Camino
                            .celdaSalienteLateral(_mapa.getAncho(), _mapa.getAlto(), tempCeldaID, celdaPrueba)) {
                break
            }
            tempCeldaID = celdaPrueba
            path.append(cDir + Encriptador.celdaIDAHash(tempCeldaID))
            celdasMovidas++
        }
        if (tempCeldaID != celdaInicio) {
            GestorSalida.ENVIAR_GA_MOVER_SPRITE_MAPA(_mapa, 0, 1, iD.toString() + "", Encriptador.getValorHashPorNumero(_orientacion)
                    + Encriptador.celdaIDAHash(celdaInicio) + path)
            _celda = _mapa.getCelda(tempCeldaID)
            val azar: Int = Formulas.getRandomInt(1, 10)
            if (azar == 5) {
                accion = 8
            }
            if (cercado.getCriando().size() > 1) {
                for (montura in cercado.getCriando().values()) {
                    if (puedeFecundar(montura)) {
                        accion = 4 // accion de aparearse
                        break
                    }
                }
            }
        } else {
            GestorSalida.ENVIAR_eD_CAMBIAR_ORIENTACION(_mapa, iD, Encriptador.getNumeroPorValorHash(cDir))
        }
        if (_ubicacion == Ubicacion.NULL) {
            return
        }
        _orientacion = Encriptador.getNumeroPorValorHash(cDir)
        try {
            Thread.sleep(celdasMovidas * 250 + 1)
        } catch (e: Exception) {
        }
        when (accion) {
            4 -> GestorSalida.ENVIAR_eUK_EMOTE_MAPA(_mapa, iD, accion, 0)
            0 -> if (golpeoObjetoCrianza) {
                GestorSalida.ENVIAR_GDE_FRAME_OBJECT_EXTERNAL(_mapa, "$celdaPrueba;4")
            }
            else -> {
                GestorSalida.ENVIAR_eUK_EMOTE_MAPA(_mapa, iD, accion, 0)
                if (golpeoObjetoCrianza) {
                    GestorSalida.ENVIAR_GDE_FRAME_OBJECT_EXTERNAL(_mapa, "$celdaPrueba;4")
                }
            }
        }
    }

    private fun puedeFecundar(montura: Montura): Boolean {
        if (montura.iD == iD) {
            return false
        }
        if (montura.celda !== _celda) { // diferente celdas
            return false
        }
        if (montura.sexo == _sexo.toInt() || !montura.disponibleParaFecundar() || !disponibleParaFecundar() || montura
                        .esCastrado() || esCastrado()) {
            return false
        }
        if (_mapa.getCercado().esPublico() && montura.dueñoID != dueñoID) {
            return false
        }
        if (montura.capacidades.contains(Constantes.HABILIDAD_ENAMORADA) || _habilidades.contains(
                        Constantes.HABILIDAD_ENAMORADA) || Formulas.getRandomInt(0, 5) === 2) {
            var madre: Montura? = null
            var padre: Montura? = null
            if (_sexo == Constantes.SEXO_FEMENINO) {
                madre = this
                padre = montura
            } else if (montura.sexo == Constantes.SEXO_FEMENINO) {
                padre = this
                madre = montura
            }
            // madre
            madre!!.setFecundada(true)
            madre.parejaID = padre!!.iD
            // padre
            padre.aumentarReproduccion()
            padre.restarAmor(7500)
            padre.restarResistencia(7500)
            if (padre.pudoEscapar()) {
                GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(_mapa, iD)
            }
            return true
        }
        return false
    }

    fun pudoEscapar(): Boolean {
        if (esSalvaje()) {
            val prob: Int = Formulas.getRandomInt(1, 100)
            if (prob <= MainServidor.PROBABILIDAD_ESCAPAR_MONTURA_DESPUES_FECUNDAR) {
                val dueñoOtro: Personaje = Mundo.getPersonaje(dueñoID)
                if (dueñoOtro != null) {
                    if (dueñoOtro.enLinea()) {
                        GestorSalida.ENVIAR_Im_INFORMACION(dueñoOtro, "0111; <b>" + nombre + "</b>~" + mapa.getID())
                    } else {
                        dueñoOtro.getCuenta().addMensaje("0111; <b>" + nombre + "</b>~" + mapa.getID(), true)
                    }
                }
                Mundo.eliminarMontura(this)
                return true
            }
        }
        return false
    }

    fun velocidadAprendizaje(): Byte {
        if (color == 18) { // dragopavo dorado
            return 20
        }
        return if (_monturaModelo == null) {
            0
        } else when (_monturaModelo.getGeneracionID()) {
            1 -> 100
            2, 3, 4 -> 80
            5, 6, 7 -> 60
            8, 9 -> 40
            10 -> 20
            else -> 100
        }
    }

    fun minutosParir(): Int {
        return MainServidor.MINUTOS_GESTACION_MONTURA + ((_monturaModelo.getGeneracionID() - 1)
                * (MainServidor.MINUTOS_GESTACION_MONTURA / 4)) + ((_reproducciones - 1) * MainServidor.MINUTOS_GESTACION_MONTURA
                / 8)
    }

    @Override
    fun addKamas(k: Long, perso: Personaje?) {
    }

    @get:Override
    val kamas: Long
        get() = 0

    @Override
    fun cerrar(perso: Personaje, exito: String?) {
        perso.cerrarVentanaExchange(exito)
    }

    fun botonOK(perso: Personaje?) {}
    fun get_statsString(): String {
        return _statsString
    }

    fun set_statsString(_statsString: String) {
        this._statsString = _statsString
        agregarStatsLocal(_statsString)
        statsMontura
        GestorSQL.REPLACE_MONTURA(this, false)
    }

    fun agregarStatsLocal(stats: String) {
        if (stats.isEmpty()) {
            _statsLocal.clear()
            return
        }
        for (str in stats.split(";")) {
            try {
                val s: Array<String> = str.split(",")
                _statsLocal.put(Integer.parseInt(s[0]), Integer.parseInt(s[1]))
            } catch (e: Exception) {
            }
        }
    }

    companion object {
        var SEXO_POSIBLES = byteArrayOf(0, 0, 1) // 0 = macho , 1 = hembra
    }
}