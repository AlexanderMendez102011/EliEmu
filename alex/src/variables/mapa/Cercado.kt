package variables.mapa

import java.util.ArrayList

class Cercado(mapa: Mapa?, var capacidadMax: Byte, var cantObjMax: Byte, celdaID: Short,
              celdaPuerta: Short, celdaMontura: Short, celdasObjetos: String, precioOriginal: Int) : Exchanger {
    val celdaID: Short = -1
    var celdaMontura: Short
        private set
    val celdaPuerta: Short
    var dueñoID = 0
    var precioPJ = 0
    private var _precioOriginal: Int
    private var _gremio: Gremio? = null
    private val _mapa: Mapa?

    // private final Map<Short, Map<Integer, Objeto>> _objCrianzaConDueño = new HashMap<Short,
    // Map<Integer, Objeto>>();
    private val _objCrianza: Map<Short, Objeto> = HashMap<Short, Objeto>()
    private val _criando: ConcurrentHashMap<Integer, Montura> = ConcurrentHashMap<Integer, Montura>()
    private val _celdasObjeto: ArrayList<Short> = ArrayList<Short>()
    fun actualizarCercado(dueñoID: Int, gremio: Int, precio: Int, objCrianza: String, criando: String) {
        this.dueñoID = dueñoID
        _gremio = null
        if (this.dueñoID > 0) {
            val dueño: Personaje = Mundo.getPersonaje(this.dueñoID)
            if (dueño == null) {
                this.dueñoID = 0
            } else {
                _gremio = dueño.getGremio()
            }
            precioPJ = precio
            for (str in objCrianza.split(Pattern.quote("|"))) {
                try {
                    val infos: Array<String> = str.split(";")
                    if (Integer.parseInt(infos[1]) === 0) {
                        _objCrianza.put(Short.parseShort(infos[0]), null)
                        continue
                    }
                    val objeto: Objeto = Mundo.getObjeto(Integer.parseInt(infos[1]))
                    if (objeto == null || objeto.getDurabilidad() <= 0) {
                        continue
                    }
                    _objCrianza.put(Short.parseShort(infos[0]), objeto)
                } catch (e: Exception) {
                }
            }
        }
        for (montura in criando.split(";")) {
            try {
                val DP: Montura = Mundo.getMontura(Integer.parseInt(montura))
                if (DP.getCelda() == null) {
                    continue
                }
                _criando.put(DP.getID(), DP)
            } catch (e: Exception) {
            }
        }
    }

    fun resetear() {
        dueñoID = 0
        precioPJ = 3000000
        _gremio = null
        _objCrianza.clear()
        _criando.clear()
    }

    @Synchronized
    fun startMoverMontura() {
        for (montura in _criando.values()) {
            val dir: Int = Formulas.getRandomInt(0, 3) * 2 + 1
            montura.moverMontura(null, dir, 3, false)
            try {
                Thread.sleep(300)
            } catch (e: Exception) {
            }
        }
    }

    fun strObjCriaParaBD(): String {
        if (_objCrianza.isEmpty()) {
            return ""
        }
        val str = StringBuilder()
        for (entry in _objCrianza.entrySet()) {
            if (str.length() > 0) {
                str.append("|")
            }
            str.append(entry.getKey().toString() + ";" + if (esPublico()) 0 else entry.getValue().getID())
        }
        return str.toString()
    }

    val objetosParaBD: ArrayList<Objeto>
        get() {
            val objetos: ArrayList<Objeto> = ArrayList<Objeto>()
            for (obj in _objCrianza.values()) {
                if (obj == null) {
                    continue
                }
                objetos.add(obj)
            }
            return objetos
        }
    val objetosCrianza: Map<Short, Any>
        get() = _objCrianza

    fun setTamañoyObjetos(tamaño: Byte, objetos: Byte) {
        capacidadMax = tamaño
        cantObjMax = objetos
    }

    fun addObjetoCria(celda: Short, objeto: Objeto?, dueño: Int) {
        // final Map<Integer, Objeto> otro = new TreeMap<Integer, Objeto>();
        // otro.put(dueño, objeto);
        _objCrianza.put(celda, objeto)
        // _objCrianzaConDueño.put(celda, otro);
    }

    fun retirarObjCria(celda: Short, perso: Personaje?): Boolean {
        if (!_objCrianza.containsKey(celda)) {
            return false
        }
        if (perso != null) { // si el jugador lo retira intencionalmente
            perso.addObjIdentAInventario(_objCrianza[celda], true)
        } else { // si se elimnia por desgaste
            Mundo.eliminarObjeto(_objCrianza[celda].getID())
        }
        // _objCrianzaConDueño.remove(celda);
        _objCrianza.remove(celda)
        return true
    }

    val cantObjColocados: Int
        get() = _objCrianza.size()
    val stringCeldasObj: String
        get() {
            if (_celdasObjeto.isEmpty()) {
                return ""
            }
            val str = StringBuilder()
            for (celda in _celdasObjeto) {
                if (str.length() > 0) {
                    str.append(";")
                }
                str.append(celda)
            }
            return str.toString()
        }
    val celdasObj: ArrayList<Short>
        get() = _celdasObjeto

    fun addCeldaObj(celda: Short) {
        if (_celdasObjeto.contains(celda) || celda <= 0) {
            return
        }
        _celdasObjeto.add(celda)
        _celdasObjeto.trimToSize()
    }

    fun addCeldaMontura(celda: Short) {
        celdaMontura = celda
    }

    fun strPavosCriando(): String {
        if (_criando.isEmpty()) {
            return ""
        }
        val str = StringBuilder()
        for (entry in _criando.entrySet()) {
            if (str.length() > 0) {
                str.append(";")
            }
            str.append(entry.getKey())
        }
        return str.toString()
    }

    fun addCriando(montura: Montura) {
        _criando.put(montura.getID(), montura)
        montura.setUbicacion(Ubicacion.CERCADO)
    }

    fun puedeAgregar(): Boolean {
        return _criando.size() < capacidadMax
    }

    val criando: ConcurrentHashMap<Integer, Montura>
        get() = _criando

    fun borrarMonturaCercado(id: Int): Boolean {
        return _criando.remove(id) != null
    }

    fun esPublico(): Boolean {
        return dueñoID == -1
    }

    var gremio: Gremio?
        get() = _gremio
        set(gremio) {
            _gremio = gremio
        }
    val mapa: Mapa?
        get() = _mapa
    val precio: Int
        get() = if (dueñoID > 0) precioPJ else _precioOriginal

    fun informacionCercado(): String {
        return "Rp" + dueñoID + ";" + precio + ";" + capacidadMax + ";" + cantObjMax + ";" + if (_gremio == null) ";" else _gremio.getNombre().toString() + ";" + _gremio.getEmblema()
    }

    @Override
    fun addKamas(kamas: Long, perso: Personaje?) {
        // TODO Auto-generated method stub
    }

    // TODO Auto-generated method stub
    @get:Override
    val kamas: Long
        get() =// TODO Auto-generated method stub
            0

    @Override
    @Synchronized
    fun addObjetoExchanger(objeto: Objeto?, cantidad: Long, perso: Personaje?, precio: Int) {
    }

    @Override
    @Synchronized
    fun remObjetoExchanger(objeto: Objeto?, cantidad: Long, perso: Personaje?, precio: Int) {
    }

    @Override
    fun cerrar(perso: Personaje, exito: String?) {
        perso.cerrarVentanaExchange(exito)
    }

    fun botonOK(perso: Personaje?) {}
    @Override
    fun getListaExchanger(perso: Personaje?): String? {
        // TODO Auto-generated method stub
        return null
    }

    // private static String CERCADO_8848 =
    // "305;0|171;0|308;0|311;0|413;0|470;0|228;0|527;0|194;0|254;0|117;0|251;0|365;0";
    // private static String CERCADO_8744 =
    // "550;0|304;0|474;0|337;0|545;0|400;0|394;0|213;0|453;0|270;0|451;0|420;0|361;0";
    // private static String CERCADO_8743 =
    // "305;0|272;0|413;0|470;0|522;0|319;0|359;0|601;0|416;0|215;0|421;0|362;0|211;0";
    // private static String CERCADO_8747 =
    // "476;0|415;0|234;0|432;0|438;0|358;0|325;0|291;0|486;0|301;0|637;0|268;0|211;0";
    // private static String CERCADO_8746 =
    // "513;0|559;0|380;0|527;0|377;0|193;0|288;0|488;0|323;0|355;0|635;0|454;0|603;0";
    // private static String CERCADO_8745 =
    // "307;0|341;0|512;0|581;0|505;0|231;0|471;0|383;0|587;0|395;0|429;0|417;0|301;0";
    // private static String CERCADO_8752 =
    // "304;0|476;0|474;0|65;0|544;0|472;0|232;0|381;0|468;0|228;0|253;0|156;0|396;0";
    // private static String CERCADO_8750 =
    // "100;0|472;0|172;0|197;0|400;0|324;0|252;0|320;0|248;0|396;0|213;0|244;0|121;0";
    // private static String CERCADO_8851 =
    // "544;0|472;0|400;0|286;0|379;0|358;0|190;0|217;0|430;0|82;0|247;0|214;0|328;0";
    // private static String CERCADO_8749 =
    // "342;0|580;0|504;0|475;0|432;0|471;0|465;0|392;0|253;0|250;0|528;0|418;0|177;0";
    // private static String CERCADO_8748 =
    // "343;0|137;0|308;0|402;0|436;0|393;0|564;0|80;0|397;0|560;0|321;0|267;0|451;0";
    // private static String CERCADO_8751 =
    // "342;0|504;0|472;0|567;0|493;0|495;0|290;0|251;0|396;0|176;0|419;0|385;0|542;0";
    init {
        _mapa = mapa
        this.celdaID = celdaID
        this.celdaMontura = celdaMontura
        this.celdaPuerta = celdaPuerta
        _precioOriginal = precioOriginal
        for (celda in celdasObjetos.split(";")) {
            try {
                _celdasObjeto.add(Short.parseShort(celda))
            } catch (e: Exception) {
            }
        }
        _celdasObjeto.trimToSize()
        if (_mapa != null) {
            _mapa.setCercado(this)
        }
        if (_mapa != null) {
            var publico = true
            var objCrianza = ""
            when (_mapa.getID()) {
                8848 -> objCrianza = "305;0|171;0|308;0|311;0|413;0|470;0|228;0|527;0|194;0|254;0|117;0|251;0|365;0"
                8744 -> objCrianza = "550;0|304;0|474;0|337;0|545;0|400;0|394;0|213;0|453;0|270;0|451;0|420;0|361;0"
                8743 -> objCrianza = "305;0|272;0|413;0|470;0|522;0|319;0|359;0|601;0|416;0|215;0|421;0|362;0|211;0"
                8747 -> objCrianza = "476;0|415;0|234;0|432;0|438;0|358;0|325;0|291;0|486;0|301;0|637;0|268;0|211;0"
                8746 -> objCrianza = "513;0|559;0|380;0|527;0|377;0|193;0|288;0|488;0|323;0|355;0|635;0|454;0|603;0"
                8745 -> objCrianza = "307;0|341;0|512;0|581;0|505;0|231;0|471;0|383;0|587;0|395;0|429;0|417;0|301;0"
                8752 -> objCrianza = "304;0|476;0|474;0|65;0|544;0|472;0|232;0|381;0|468;0|228;0|253;0|156;0|396;0"
                8750 -> objCrianza = "100;0|472;0|172;0|197;0|400;0|324;0|252;0|320;0|248;0|396;0|213;0|244;0|121;0"
                8851 -> objCrianza = "544;0|472;0|400;0|286;0|379;0|358;0|190;0|217;0|430;0|82;0|247;0|214;0|328;0"
                8749 -> objCrianza = "342;0|580;0|504;0|475;0|432;0|471;0|465;0|392;0|253;0|250;0|528;0|418;0|177;0"
                8748 -> objCrianza = "343;0|137;0|308;0|402;0|436;0|393;0|564;0|80;0|397;0|560;0|321;0|267;0|451;0"
                8751 -> objCrianza = "342;0|504;0|472;0|567;0|493;0|495;0|290;0|251;0|396;0|176;0|419;0|385;0|542;0"
                else -> publico = false
            }
            if (publico) {
                dueñoID = -1
                precioPJ = 0
                _precioOriginal = precioPJ
                for (str in objCrianza.split(Pattern.quote("|"))) {
                    try {
                        val infos: Array<String> = str.split(";")
                        if (Integer.parseInt(infos[1]) === 0) {
                            _objCrianza.put(Short.parseShort(infos[0]), null)
                            continue
                        }
                        val objeto: Objeto = Mundo.getObjeto(Integer.parseInt(infos[1]))
                        if (objeto == null || objeto.getDurabilidad() <= 0) {
                            continue
                        }
                        _objCrianza.put(Short.parseShort(infos[0]), objeto)
                    } catch (e: Exception) {
                    }
                }
            }
        }
    }
}