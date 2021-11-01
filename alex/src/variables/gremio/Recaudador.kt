package variables.gremio

import java.util.ArrayList

class Recaudador(val iD: Int, mapa: Int, celdaID: Short, orientacion: Byte, gremioID: Int,
                 N1: String, N2: String, objetos: String, kamas: Long, xp: Long, tiempoProteccion: Long,
                 tiempoCreacion: Long, dueño: Int) : PreLuchador, Exchanger, Preguntador {
    val dueño: Int
    private var _direccion: Byte
    var kamas: Long = 0
        private set
    var exp: Long
        private set
    private var _proxMovimiento: Long = -1
    var tiempoProteccion: Long
        private set
    val tiempoCreacion: Long

    //
    // public byte getEstadoPelea() {
    // try {
    // return _pelea.getFase();
    // } catch (Exception e) {
    // return 0;
    // }
    // }
    var enRecolecta = false
    var n1 = ""
    var n2 = ""
    private val _objetos: Map<Integer, Objeto> = TreeMap<Integer, Objeto>()
    private val _objModeloID: Map<Integer, Long?> = TreeMap<Integer, Long>()
    private var _pelea: Pelea? = null
    private val _gremio: Gremio?
    private val _mapa: Mapa
    private var _celda: Celda
    private var _totalStats: TotalStats? = null
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

    private fun restarMovimiento() {
        _proxMovimiento = System.currentTimeMillis() + MainServidor.SEGUNDOS_MOVER_RECAUDADOR * 1000
    }

    var pelea: Pelea?
        get() = _pelea
        set(pelea) {
            _pelea = pelea
        }
    val peleaID: Short
        get() = if (_pelea == null) {
            -1
        } else _pelea.getID()
    val podsActuales: Int
        get() {
            var pods = 0
            for (entry in _objetos.entrySet()) {
                pods += entry.getValue().getObjModelo().getPeso() * entry.getValue().getCantidad()
            }
            return pods
        }

    fun addKamas(kamas: Long, perso: Personaje?) {
        if (kamas == 0L) {
            return
        }
        this.kamas += kamas
        if (this.kamas < 0) {
            this.kamas = 0
        }
    }

    fun addExp(xp: Float) {
        exp += xp.toLong()
    }

    val gremio: Gremio?
        get() = _gremio
    val orientacion: Int
        get() = _direccion.toInt()
    val mapa: Mapa
        get() = _mapa

    fun puedeMoverRecaudador() {
        if (_proxMovimiento <= 0) {
            return
        }
        if (System.currentTimeMillis() - _proxMovimiento >= 0) {
            moverRecaudador()
            restarMovimiento()
        }
    }

    fun moverRecaudador() {
        if (enRecolecta || _pelea != null) {
            return
        }
        val celdaDestino: Short = Camino.celdaMoverSprite(_mapa, _celda.getID())
        if (celdaDestino.toInt() == -1) {
            return
        }
        val pathCeldas: Duo<Integer, ArrayList<Celda>> = Camino.getPathPelea(_mapa, _celda.getID(), celdaDestino, -1, null,
                false) ?: return
        val celdas: ArrayList<Celda> = pathCeldas._segundo
        val pathStr: String = Camino.getPathComoString(_mapa, celdas, _celda.getID(), false)
        if (pathStr.isEmpty()) {
            MainServidor.redactarLogServidorln("Fallo de desplazamiento de mob grupo: camino vacio - MapaID: " + _mapa.getID()
                    .toString() + " - CeldaID: " + _celda.getID())
            return
        }
        try {
            Thread.sleep(100)
        } catch (e: Exception) {
        }
        GestorSalida.ENVIAR_GA_MOVER_SPRITE_MAPA(_mapa, 0, 1, iD.toString() + "", Encriptador.getValorHashPorNumero(_direccion)
                + Encriptador.celdaIDAHash(_celda.getID()) + pathStr)
        _direccion = Camino.getIndexPorDireccion(pathStr.charAt(pathStr.length() - 3))
        _celda = _mapa.getCelda(celdaDestino)
    }

    fun borrarRecaudador() {
        GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(_mapa, iD)
        for (obj in _objetos.values()) {
            Mundo.eliminarObjeto(obj.getID())
        }
        Mundo.eliminarRecaudador(this)
    }

    val objetos: Collection<Any>
        get() = _objetos.values()

    fun clearObjetos() {
        _objetos.clear()
    }

    fun addObjAInventario(objeto: Objeto): Boolean {
        if (_objetos.containsKey(objeto.getID())) {
            return false
        }
        // tipo piedra de alma y mascota
        if (objeto.puedeTenerStatsIguales()) {
            for (obj in _objetos.values()) {
                if (Constantes.esPosicionEquipamiento(obj.getPosicion())) {
                    continue
                }
                if (objeto.getID() !== obj.getID() && obj.getObjModeloID() === objeto.getObjModeloID() && obj.sonStatsIguales(
                                objeto)) {
                    obj.setCantidad(obj.getCantidad() + objeto.getCantidad())
                    if (objeto.getID() > 0) {
                        Mundo.eliminarObjeto(objeto.getID())
                    }
                    return true
                }
            }
        }
        addObjeto(objeto)
        return false
    }

    private fun addObjeto(objeto: Objeto) {
        try {
            if (objeto.getID() === 0) {
                Mundo.addObjeto(objeto, false)
            } else {
                GestorSQL.SALVAR_OBJETO(objeto)
            }
            _objetos.put(objeto.getID(), objeto)
        } catch (e: Exception) {
        }
    }

    fun stringListaObjetosBD(): String {
        val str = StringBuilder()
        for (obj in _objetos.values()) {
            str.append(obj.getID().toString() + "|")
        }
        return str.toString()
    }

    private fun stringRecolecta(): String {
        val str = StringBuilder("|" + exp)
        for (entry in _objModeloID.entrySet()) {
            str.append(";" + entry.getKey().toString() + "," + entry.getValue())
        }
        // for (final Objeto obj : _objetos.values()) {
        // str.append(";" + obj.getIDObjModelo() + "," + obj.getCantidad());
        // }
        return str.toString()
    }

    fun stringPanelInfo(perso: Personaje): String {
        return n1 + "," + n2 + "|" + _mapa.getID() + "|" + _mapa.getX() + "|" + _mapa.getY() + "|" + perso
                .getNombre()
    }

    fun stringGM(): String {
        if (_pelea != null || _gremio == null) {
            return ""
        }
        val str = StringBuilder()
        str.append(_celda.getID().toString() + ";")
        str.append("$_direccion;")
        str.append("0;")
        str.append("$iD;")
        str.append(n1 + "," + n2 + ";")
        str.append("-6;") // tipo
        str.append("6000^100;") // gfxID ^ talla
        str.append(_gremio.getNivel().toString() + ";")
        str.append(_gremio.getNombre().toString() + ";" + _gremio.getEmblema())
        return str.toString()
    }

    fun mensajeDeAtaque(): String {
        return "A" + n1 + "," + n2 + "|.|" + _mapa.getID() + "|" + _celda.getID()
    }

    fun atacantesAlGremio(): String {
        val str = StringBuilder("+" + Integer.toString(iD, 36))
        try {
            for (luchador in _pelea.luchadoresDeEquipo(1)) {
                val perso: Personaje = luchador.getPersonaje() ?: continue
                str.append("|" + Integer.toString(perso.getID(), 36).toString() + ";")
                str.append(perso.getNombre().toString() + ";")
                str.append(perso.getNivel().toString() + ";")
            }
        } catch (e: Exception) {
        }
        return str.toString()
    }

    fun defensoresDelGremio(): String {
        val str = StringBuilder("+" + Integer.toString(iD, 36))
        try {
            val stra = StringBuilder("-")
            for (luchador in _pelea.luchadoresDeEquipo(2)) {
                val perso: Personaje = luchador.getPersonaje() ?: continue
                str.append("|" + Integer.toString(perso.getID(), 36).toString() + ";")
                str.append(perso.getNombre().toString() + ";")
                str.append(perso.getGfxID(false).toString() + ";")
                str.append(perso.getNivel().toString() + ";")
            }
            stra.append(str.substring(1))
            _pelea.setListaDefensores(stra.toString())
        } catch (e: Exception) {
        }
        return str.toString()
    }

    fun getListaExchanger(perso: Personaje?): String {
        val str = StringBuilder()
        for (obj in _objetos.values()) {
            str.append("O" + obj.stringObjetoConGuiño())
        }
        if (kamas > 0) {
            str.append("G" + kamas)
        }
        return str.toString()
    }

    fun actualizarAtacantesDefensores() {
        if (_pelea == null) return
        val str = defensoresDelGremio()
        val str2 = atacantesAlGremio()
        for (p in _gremio.getMiembros()) {
            if (p.enLinea()) {
                GestorSalida.ENVIAR_gITP_INFO_DEFENSORES_RECAUDADOR(p, str)
                GestorSalida.ENVIAR_gITp_INFO_ATACANTES_RECAUDADOR(p, str2)
            }
        }
    }

    fun sobrevivio() {
        val str = "S" + n1 + "," + n2 + "|.|" + _mapa.getID() + "|" + _celda.getID()
        val str2: String = _gremio.analizarRecaudadores()
        for (pj in _gremio.getMiembros()) {
            if (pj.enLinea()) {
                GestorSalida.ENVIAR_gITM_GREMIO_INFO_RECAUDADOR(pj, str2)
                GestorSalida.ENVIAR_gA_MENSAJE_SOBRE_RECAUDADOR(pj, str)
            }
        }
        pelea = null
        GestorSalida.ENVIAR_GM_RECAUDADOR_A_MAPA(_mapa, "+" + stringGM())
    }

    fun murio() {
        val str = "D" + n1 + "," + n2 + "|.|" + _mapa.getID() + "|" + _celda.getID()
        val str2: String = _gremio.analizarRecaudadores()
        for (pj in _gremio.getMiembros()) {
            if (pj.enLinea()) {
                GestorSalida.ENVIAR_gITM_GREMIO_INFO_RECAUDADOR(pj, str2)
                GestorSalida.ENVIAR_gA_MENSAJE_SOBRE_RECAUDADOR(pj, str)
            }
        }
        borrarRecaudador()
    }

    val celda: Celda
        get() = _celda

    fun getGfxID(buff: Boolean): Int {
        return 6000 // recaudador
    }

    val pDVMax: Int
        get() = _gremio.getNivel() * MainServidor.RECAUDADOR_VIDA_X_NIVEL
    val pDV: Int
        get() = pDVMax
    val totalStatsPelea: TotalStats?
        get() = _totalStats
    val nivel: Int
        get() = _gremio.getNivel()
    val alineacion: Byte
        get() = Constantes.ALINEACION_NULL

    fun stringGMLuchador(): String {
        return "-6;6000^100;" + _gremio.getNivel().toString() + ";"
    }

    val hechizos: Map<Any, Any>
        get() = _gremio.getHechizos()
    val gradoAlineacion: Int
        get() = 0
    val deshonor: Int
        get() = 0
    val honor: Int
        get() = 0

    fun addHonor(honor: Int) {}
    fun addDeshonor(honor: Int): Boolean {
        return false
    }

    @Override
    fun addKamasGanada(kamas: Double) {
    }

    @Override
    fun addXPGanada(exp: Long) {
    }

    @Override
    @Synchronized
    fun addObjetoExchanger(objeto: Objeto?, cantidad: Long, perso: Personaje?, precio: Int) {
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
        if (nuevaCant < 1) {
            perso.addObjIdentAInventario(objeto, true)
            _objetos.remove(objeto.getID())
            GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(perso, "O-" + objeto.getID())
        } else {
            val persoObj: Objeto = objeto.clonarObjeto(cantidad, Constantes.OBJETO_POS_NO_EQUIPADO)
            perso.addObjIdentAInventario(persoObj, true)
            objeto.setCantidad(nuevaCant)
            GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(perso, "O+" + objeto.getID().toString() + "|" + objeto.getCantidad().toString() + "|"
                    + objeto.getObjModeloID().toString() + "|" + objeto.convertirStatsAString(false))
        }
        if (_objModeloID[objeto.getObjModeloID()] == null) {
            _objModeloID.put(objeto.getObjModeloID(), 0L)
        }
        _objModeloID.put(objeto.getObjModeloID(), _objModeloID[objeto.getObjModeloID()]!! + cantidad)
    }

    @Override
    fun cerrar(perso: Personaje, exito: String?) {
        val s: String = _gremio.analizarRecaudadores()
        val str = StringBuilder()
        str.append(stringPanelInfo(perso))
        str.append(stringRecolecta())
        for (miembro in _gremio.getMiembros()) {
            if (miembro.enLinea()) {
                GestorSalida.ENVIAR_gITM_GREMIO_INFO_RECAUDADOR(miembro, s)
                GestorSalida.ENVIAR_gT_PANEL_RECAUDADORES_GREMIO(miembro, 'G', str.toString())
            }
        }
        _gremio.addExperiencia(exp, false)
        borrarRecaudador()
        perso.cerrarVentanaExchange(exito)
    }

    fun botonOK(perso: Personaje?) {}
    fun getArgsDialogo(args: String?): String {
        return ";" + _gremio.getInfoGremio()
    }

    val infoPanel: String
        get() {
            val str = StringBuilder()
            str.append(Integer.toString(iD, 36).toString() + ";")
            str.append(n1 + "," + n2 + ",")
            val dueño: Personaje = Mundo.getPersonaje(dueño)
            if (dueño != null) {
                str.append(dueño.getNombre())
            }
            str.append("," + tiempoCreacion + ",,100000000,100000000")
            str.append(";" + Integer.toString(_mapa.getID(), 36).toString() + "," + _mapa.getX().toString() + "," + _mapa.getY().toString() + ";")
            var estadoR = 0
            if (_pelea != null) {
                when (_pelea.getFase()) {
                    Constantes.PELEA_FASE_INICIO, Constantes.PELEA_FASE_POSICION -> estadoR = 1
                    Constantes.PELEA_FASE_COMBATE -> estadoR = 2
                }
            }
            str.append("$estadoR;")
            if (estadoR == 1) {
                str.append(_pelea.getTiempoFaltInicioPelea().toString() + ";")
            } else {
                str.append("0;")
            }
            str.append((MainServidor.SEGUNDOS_INICIO_PELEA * 1000).toString() + ";")
            str.append((if (_pelea == null) 0 else _pelea.getPosPelea(2) - 1).toString() + ";")
            return str.toString()
        }

    init {
        _mapa = Mundo.getMapa(mapa)
        _celda = _mapa.getCelda(celdaID)
        _direccion = orientacion
        n1 = N1
        n2 = N2
        for (str in objetos.split(Pattern.quote("|"))) {
            try {
                val obj: Objeto = Mundo.getObjeto(Integer.parseInt(str)) ?: continue
                _objetos.put(obj.getID(), obj)
            } catch (e: Exception) {
            }
        }
        exp = xp
        this.tiempoProteccion = tiempoProteccion
        this.tiempoCreacion = tiempoCreacion
        this.dueño = dueño
        addKamas(kamas, null)
        restarMovimiento()
        _gremio = Mundo.getGremio(gremioID)
        try {
            _gremio.addRecaudador(this)
            if (MainServidor.RECAUDADOR_STATS.isEmpty()) {
                _totalStats = TotalStats(_gremio.getStatsPelea(), null, Stats(), null, 3)
            } else {
                _totalStats = TotalStats(_gremio.getStatsPelea(), Stats(MainServidor.RECAUDADOR_STATS), Stats(), null, 3)
            }
        } catch (e: Exception) {
            MainServidor.redactarLogServidorln("Recaudador con gremio inexistente $gremioID")
            GestorSQL.DELETE_RECAUDADOR(iD)
        }
    }
}