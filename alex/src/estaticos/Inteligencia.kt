package estaticos

import java.util.ArrayList

class Inteligencia(atacante: Luchador?, p: Pelea?) : Runnable {
    private var _lanzador: Luchador? = null
    private var _pelea: Pelea? = null
    private val _t: Thread
    private var _resetearCeldasHechizos = false
    private var _resetearInfluencias = false
    private var _refrescarMov = false
    private val _celdasHechizos: ConcurrentHashMap<Short, Map<StatHechizo, Map<Celda, ArrayList<Luchador>>>> = ConcurrentHashMap()
    private val _influencias: ConcurrentHashMap<Luchador, Map<EfectoHechizo, Integer>> = ConcurrentHashMap()
    private var _celdasMovimiento: ArrayList<Short> = ArrayList()

    enum class EstadoLanzHechizo {
        PODER, NO_TIENE_PA, NO_TIENE_ALCANCE, OBJETIVO_NO_PERMITIDO, NO_PODER, FALLAR, COOLDOWN, NO_OBJETIVO
    }

    enum class EstadoDistancia {
        TACLEADO, ACERCARSE, NO_PUEDE, INVISIBLE
    }

    enum class EstadoMovAtq {
        SE_MOVIO, NO_HIZO_NADA, LANZO_HECHIZO, TACLEADO, NO_PUEDE_MOVERSE, NO_TIENE_HECHIZOS, TIENE_HECHIZOS_SIN_LANZAR, NULO
    }

    enum class Orden {
        PDV_MENOS_A_MAS, PDV_MAS_A_MENOS, NADA, NIVEL_MENOS_A_MAS, NIVEL_MAS_A_MENOS, INVOS_PRIMEROS, INVOS_ULTIMOS
    }

    enum class Accion {
        ATACAR, BOOSTEAR, CURAR, TRAMPEAR, INVOCAR, TELEPORTAR, NADA
    }

    // sorts ordena siempre en orden ascendente
    private class CompPDVMenosMas : Comparator<Luchador?> {
        @Override
        fun compare(p1: Luchador, p2: Luchador): Int {
            return Integer.compare(p1.getPDVSinBuff(), p2.getPDVSinBuff())
        }
    }

    class CompPDVMasMenos : Comparator<Luchador?> {
        @Override
        fun compare(p1: Luchador, p2: Luchador): Int {
            return Integer.compare(p2.getPDVSinBuff(), p1.getPDVSinBuff())
        }
    }

    private class CompNivelMenosMas : Comparator<Luchador?> {
        @Override
        fun compare(p1: Luchador, p2: Luchador): Int {
            return Integer.compare(p1.getNivel(), p2.getNivel())
        }
    }

    private class CompNivelMasMenos : Comparator<Luchador?> {
        @Override
        fun compare(p1: Luchador, p2: Luchador): Int {
            return Integer.compare(p2.getNivel(), p1.getNivel())
        }
    }

    private class CompInvosUltimos : Comparator<Luchador?> {
        @Override
        fun compare(p1: Luchador, p2: Luchador?): Int {
            return if (!p1.esInvocacion()) {
                -1
            } else 1
        }
    }

    private class CompInvosPrimeros : Comparator<Luchador?> {
        @Override
        fun compare(p1: Luchador, p2: Luchador?): Int {
            return if (p1.esInvocacion()) {
                -1
            } else 1
        }
    }

    val thread: Thread
        get() = _t
    val tipoIA: Int
        get() {
            if (_lanzador == null) {
                return -1
            }
            try {
                if (_lanzador.esDoble()) {
                    return 5
                } else if (_lanzador.getRecaudador() != null) {
                    return 21
                } else if (_lanzador.getPrisma() != null) {
                    return 20
                } else if (_lanzador.getMob() != null) {
                    return _lanzador.getMob().getMobModelo().getTipoIA()
                }
            } catch (e: Exception) {
                MainServidor.redactarLogServidorln("EXCEPTION getTipoIA " + e.toString())
                e.printStackTrace()
            }
            return -1
        }

    fun forzarRefrescarMov() {
        _refrescarMov = true
    }

    fun nullear() {
        _resetearCeldasHechizos = true
        _resetearInfluencias = true
    }

    @Synchronized
    fun arrancar() {
        try {
            Thread { correr() }.start()
        } catch (e: Exception) {
            MainServidor
                    .redactarLogServidorln(
                            "Exception ARRANCAR IA tipo: " + tipoIA + ", atacante: "
                                    + (if (_lanzador == null) "Null" else _lanzador.getNombre()) + ", "
                                    + (if (_pelea == null) " pelea Null" else "pMapa: " + _pelea.getMapaCopia().getID().toString() + " pID: " + _pelea.getID()
                                    .toString() + " pEstado: " + _pelea.getFase())
                                    + ", Exception " + e.toString())
        }
    }

    fun correr() {
        var sTipo = "ninguna"
        try {
            var fin = false
            while (!fin && Mundo.SERVIDOR_ESTADO !== Constantes.SERVIDOR_OFFLINE && _pelea.getFase() !== Constantes.PELEA_FASE_FINALIZADO) {
                val tipo = tipoIA
                sTipo = "" + tipo
                nullear()
                when (tipo) {
                    0 -> tipo_0()
                    1 -> tipo_1()
                    2 -> tipo_2()
                    3 -> tipo_3()
                    4 -> tipo_4()
                    5 -> tipo_5()
                    6 -> tipo_6()
                    7 -> tipo_7()
                    8 -> tipo_8()
                    9 -> tipo_9()
                    10 -> tipo_10()
                    11 -> tipo_11()
                    12 -> tipo_12()
                    13 -> tipo_13()
                    14 -> tipo_14()
                    15 -> tipo_15()
                    16 -> tipo_16()
                    20 -> tipo_20()
                    21 -> tipo_21()
                }
                if (_lanzador != null) {
                    if (!_lanzador.estaMuerto()) {
                        if (_lanzador.puedeJugar() && _pelea != null) {
                            _pelea.pasarTurno(_lanzador)
                        }
                    }
                    if (!_lanzador.puedeJugar()) {
                        fin = true
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            MainServidor
                    .redactarLogServidorln(
                            "EXCEPTION run IA tipo: " + sTipo + ", atacante: "
                                    + (if (_lanzador == null) "Null" else _lanzador.getNombre()) + ", "
                                    + (if (_pelea == null) " pelea Null" else "pMapa: " + _pelea.getMapaCopia().getID().toString() + " pID: "
                                    + _pelea.getID().toString() + " pEstado: " + _pelea.getFase())
                                    + ", Exception " + e.toString())
            try {
                if (_pelea != null) {
                    MainServidor.redactarLogServidorln("ELMINANDO A LA IA DEL MOB")
                    _pelea.addMuertosReturnFinalizo(_lanzador, null)
                }
            } catch (exception: Exception) {
            }
        }
    }

    private fun clearInfluencias() {
        if (_resetearInfluencias) {
            _influencias.clear()
            _resetearInfluencias = false
        }
    }

    private fun clearCeldasHechizos() {
        if (_resetearCeldasHechizos) {
            _celdasHechizos.clear()
            _resetearCeldasHechizos = false
        }
    }

    private fun setCeldasHechizoCeldaLanz() {
        clearCeldasHechizos()
        val celdaLanzID: Short = _lanzador.getCeldaPelea().getID()
        if (!_celdasHechizos.containsKey(celdaLanzID)) {
            val a: Map<StatHechizo, Map<Celda, ArrayList<Luchador>>> = getObjHechDesdeCeldaLanz(celdaLanzID)
            if (a == null || a.isEmpty()) {
                return
            }
            _celdasHechizos.put(celdaLanzID, a)
        }
    }

    private fun getObjetivosGuardado(celdaPosibleLanzamiento: Celda, SH: StatHechizo): ArrayList<Luchador>? {
        for (a in _celdasHechizos.values()) {
            if (a[SH] != null) {
                val luchadors: ArrayList<Luchador> = a[SH]!![celdaPosibleLanzamiento]
                if (luchadors != null) return luchadors
            }
        }
        return null
    }

    private fun getObjHechDesdeCeldaLanz(celdaLanzador: Short): Map<StatHechizo, Map<Celda, ArrayList<Luchador>>> {
        val map: Map<StatHechizo, Map<Celda, ArrayList<Luchador>>> = HashMap()
        for (SH2 in hechizosLanzables()) {
            val celdas: ArrayList<Celda> = Camino.celdasPosibleLanzamiento(SH2, _lanzador, _pelea.getMapaCopia(),
                    celdaLanzador, -1.toShort())
            for (celdaPosibleLanzamiento in celdas) {
                if (_pelea.puedeLanzarHechizo(_lanzador, SH2, celdaPosibleLanzamiento,
                                celdaLanzador) !== EstadoLanzHechizo.PODER) {
                    continue
                }
                var o: ArrayList<Luchador?>? = getObjetivosGuardado(celdaPosibleLanzamiento, SH2)
                if (o == null) {
                    o = SH2.listaObjetivosAfectados(_lanzador, _pelea.getMapaCopia(), celdaPosibleLanzamiento.getID())
                }
                // if (!o.isEmpty()) {
                map.computeIfAbsent(SH2) { k -> HashMap<Celda, ArrayList<Luchador>>() }
                map[SH2].put(celdaPosibleLanzamiento, o)
                // }
            }
        }
        return map
    }

    private fun tipo_0() {
        if (!_lanzador.puedeJugar()) {
            return
        }
        val amigos: ArrayList<Luchador> = ordenLuchadores(_lanzador.getParamEquipoAliado(), arrayOf(Orden.NIVEL_MAS_A_MENOS))
        val enemigos: ArrayList<Luchador> = ordenLuchadores(_lanzador.getParamEquipoEnemigo(), arrayOf(Orden.PDV_MENOS_A_MAS, Orden.INVOS_ULTIMOS))
        val porcPDV: Float = _lanzador.getPorcPDV()
        if (porcPDV > 50.0f) {
            var movAtq: EstadoMovAtq
            while (buffeaSiEsPosible(amigos));
            while (trampearSiEsPosible());
            invocarSiEsPosible(enemigos)
            fullAtaqueSioSi(enemigos)
            do {
                movAtq = moverYLanzarAlgo(enemigos, Accion.ATACAR, false)
            } while (movAtq == EstadoMovAtq.TACLEADO || movAtq == EstadoMovAtq.SE_MOVIO || movAtq == EstadoMovAtq.LANZO_HECHIZO)
            teleportSiEsPosible(enemigos)
            while (curaSiEsPosible(amigos));
            var acercarse = EstadoDistancia.NO_PUEDE
            do {
                if (movAtq == EstadoMovAtq.NO_TIENE_HECHIZOS) continue
                fullAtaqueSioSi(enemigos)
            } while (movAtq != EstadoMovAtq.NO_PUEDE_MOVERSE
                    && acercarseA(enemigos, true, true).also { acercarse = it } == EstadoDistancia.ACERCARSE)
            while (trampearSiEsPosible());
            if (!invocarSiEsPosible(enemigos) && !fullAtaqueSioSi(enemigos)) {
                _pelea.esperahechizo = true
                _pelea.esperaMove = true
            }
            siEsInvisible(acercarse)
        } else {
            var movAtq: EstadoMovAtq
            while (curaSiEsPosible(amigos));
            while (buffeaSiEsPosible(amigos));
            while (trampearSiEsPosible());
            invocarSiEsPosible(enemigos)
            fullAtaqueSioSi(enemigos)
            do {
                movAtq = moverYLanzarAlgo(enemigos, Accion.ATACAR, false)
            } while (movAtq == EstadoMovAtq.TACLEADO || movAtq == EstadoMovAtq.SE_MOVIO || movAtq == EstadoMovAtq.LANZO_HECHIZO)
            var acercarse = EstadoDistancia.NO_PUEDE
            do {
                if (movAtq == EstadoMovAtq.NO_TIENE_HECHIZOS) continue
                fullAtaqueSioSi(enemigos)
            } while (movAtq != EstadoMovAtq.NO_PUEDE_MOVERSE
                    && acercarseA(enemigos, true, true).also { acercarse = it } == EstadoDistancia.ACERCARSE)
            siEsInvisible(acercarse)
        }
    }

    private fun tipo_1() {
        if (!_lanzador.puedeJugar()) {
            return
        }
        val amigos: ArrayList<Luchador> = ordenLuchadores(_lanzador.getParamEquipoAliado(), Orden.NIVEL_MAS_A_MENOS)
        val enemigos: ArrayList<Luchador> = ordenLuchadores(_lanzador.getParamEquipoEnemigo(), Orden.PDV_MENOS_A_MAS,
                Orden.INVOS_ULTIMOS)
        val porcPDV: Float = _lanzador.getPorcPDV()
        val azar: Boolean = Formulas.getRandomBoolean()
        if (porcPDV > 50 || azar) {
            var movAtq: EstadoMovAtq
            while (buffeaSiEsPosible(amigos)) {
            }
            if (azar) {
                fullAtaqueSioSi(enemigos)
                invocarSiEsPosible(enemigos)
            } else {
                invocarSiEsPosible(enemigos)
                fullAtaqueSioSi(enemigos)
            }
            do {
                movAtq = moverYLanzarAlgo(enemigos, Accion.ATACAR, false)
            } while (movAtq == EstadoMovAtq.TACLEADO || movAtq == EstadoMovAtq.SE_MOVIO || movAtq == EstadoMovAtq.LANZO_HECHIZO)
            teleportSiEsPosible(enemigos)
            while (curaSiEsPosible(amigos)) {
            }
            var acercarse = EstadoDistancia.NO_PUEDE
            do {
                if (movAtq != EstadoMovAtq.NO_TIENE_HECHIZOS) {
                    fullAtaqueSioSi(enemigos)
                }
            } while (movAtq != EstadoMovAtq.NO_PUEDE_MOVERSE
                    && acercarseA(enemigos, true, true).also { acercarse = it } == EstadoDistancia.ACERCARSE)
            while (trampearSiEsPosible()) {
            }
            siEsInvisible(acercarse)
        } else {
            while (curaSiEsPosible(amigos)) {
            }
            while (buffeaSiEsPosible(amigos)) {
            }
            while (trampearSiEsPosible()) {
            }
            var movAtq: EstadoMovAtq
            if (azar) {
                fullAtaqueSioSi(enemigos)
                invocarSiEsPosible(enemigos)
            } else {
                invocarSiEsPosible(enemigos)
                fullAtaqueSioSi(enemigos)
            }
            do {
                movAtq = moverYLanzarAlgo(enemigos, Accion.ATACAR, false)
            } while (movAtq == EstadoMovAtq.TACLEADO || movAtq == EstadoMovAtq.SE_MOVIO || movAtq == EstadoMovAtq.LANZO_HECHIZO)
            var acercarse = EstadoDistancia.NO_PUEDE
            do {
                if (movAtq != EstadoMovAtq.NO_TIENE_HECHIZOS) {
                    fullAtaqueSioSi(enemigos)
                }
            } while (movAtq != EstadoMovAtq.NO_PUEDE_MOVERSE
                    && acercarseA(enemigos, true, true).also { acercarse = it } == EstadoDistancia.ACERCARSE)
            siEsInvisible(acercarse)
        }
    }

    private fun tipo_2() { // esfera xelor
        if (!_lanzador.puedeJugar()) {
            return
        }
        val enemigos: ArrayList<Luchador> = ordenLuchadores(_lanzador.getParamEquipoEnemigo(), Orden.PDV_MENOS_A_MAS,
                Orden.INVOS_ULTIMOS)
        fullAtaqueSioSi(enemigos)
    }

    private fun tipo_3() {
        // mobs salas de entrenamiento
        if (!_lanzador.puedeJugar()) {
            return
        }
        val enemigos: ArrayList<Luchador> = ordenLuchadores(_lanzador.getParamEquipoEnemigo(), Orden.PDV_MENOS_A_MAS,
                Orden.INVOS_ULTIMOS)
        var acercarse = EstadoDistancia.NO_PUEDE
        do {
            fullAtaqueSioSi(enemigos)
        } while (acercarseA(enemigos, true, true).also { acercarse = it } == EstadoDistancia.ACERCARSE)
        siEsInvisible(acercarse)
    }

    private fun tipo_4() { // tofu, prespic
        if (!_lanzador.puedeJugar()) {
            return
        }
        val amigos: ArrayList<Luchador> = ordenLuchadores(_lanzador.getParamEquipoAliado(), Orden.NIVEL_MAS_A_MENOS)
        val enemigos: ArrayList<Luchador> = ordenLuchadores(_lanzador.getParamEquipoEnemigo(), Orden.PDV_MENOS_A_MAS,
                Orden.INVOS_ULTIMOS)
        var tempMovAtq = EstadoMovAtq.NULO
        // aqui comienza todo
        var movAtq: EstadoMovAtq
        do {
            movAtq = moverYLanzarAlgo(amigos, Accion.BOOSTEAR, false)
            val eEnemigos = moverYLanzarAlgo(enemigos, Accion.BOOSTEAR, false)
            if (eEnemigos == EstadoMovAtq.LANZO_HECHIZO) {
                tempMovAtq = EstadoMovAtq.LANZO_HECHIZO
            }
            if (eEnemigos == EstadoMovAtq.TACLEADO || eEnemigos == EstadoMovAtq.SE_MOVIO || eEnemigos == EstadoMovAtq.LANZO_HECHIZO) {
                movAtq = eEnemigos
            }
        } while (movAtq == EstadoMovAtq.TACLEADO || movAtq == EstadoMovAtq.SE_MOVIO || movAtq == EstadoMovAtq.LANZO_HECHIZO)
        do {
            movAtq = moverYLanzarAlgo(enemigos, Accion.ATACAR, false)
            if (movAtq == EstadoMovAtq.LANZO_HECHIZO) {
                tempMovAtq = EstadoMovAtq.LANZO_HECHIZO
            }
        } while (movAtq == EstadoMovAtq.TACLEADO || movAtq == EstadoMovAtq.SE_MOVIO || movAtq == EstadoMovAtq.LANZO_HECHIZO)
        if (tempMovAtq == EstadoMovAtq.NULO) {
            if (acercarseA(enemigos, true, true) == EstadoDistancia.ACERCARSE) {
                if (movAtq != EstadoMovAtq.NO_TIENE_HECHIZOS) {
                    if (fullAtaqueSioSi(enemigos)) {
                        tempMovAtq = EstadoMovAtq.LANZO_HECHIZO
                    }
                }
            }
        }
        while (buffeaSiEsPosible(amigos)) {
        }
        if (tempMovAtq == EstadoMovAtq.LANZO_HECHIZO) {
            while (alejarseDeEnemigo()) {
            }
        }
    }

    private fun tipo_5() { // la bloqueadora
        if (!_lanzador.puedeJugar()) {
            return
        }
        val enemigos: ArrayList<Luchador> = ordenLuchadores(_lanzador.getParamEquipoEnemigo(), Orden.PDV_MENOS_A_MAS,
                Orden.INVOS_ULTIMOS)
        var acercarse = EstadoDistancia.NO_PUEDE
        while (acercarseA(enemigos, false, false).also { acercarse = it } == EstadoDistancia.ACERCARSE) {
        }
        siEsInvisible(acercarse)
    }

    private fun tipo_6() { // la hinchable, conejo
        if (!_lanzador.puedeJugar()) {
            return
        }
        val amigos: ArrayList<Luchador> = ordenLuchadores(_lanzador.getParamEquipoAliado(), Orden.NIVEL_MAS_A_MENOS)
        val enemigos: ArrayList<Luchador> = ordenLuchadores(_lanzador.getParamEquipoEnemigo(), Orden.PDV_MENOS_A_MAS,
                Orden.INVOS_ULTIMOS)
        invocarSiEsPosible(enemigos)
        do {
            var movAtq: EstadoMovAtq
            do {
                movAtq = moverYLanzarAlgo(amigos, Accion.BOOSTEAR, false)
            } while (movAtq == EstadoMovAtq.TACLEADO || movAtq == EstadoMovAtq.SE_MOVIO || movAtq == EstadoMovAtq.LANZO_HECHIZO)
            do {
                movAtq = moverYLanzarAlgo(amigos, Accion.CURAR, false)
            } while (movAtq == EstadoMovAtq.TACLEADO || movAtq == EstadoMovAtq.SE_MOVIO || movAtq == EstadoMovAtq.LANZO_HECHIZO)
        } while (acercarseA(amigos, false, false) == EstadoDistancia.ACERCARSE)
    }

    private fun tipo_7() { // gatake, pala animada, jabali, crujidor
        if (!_lanzador.puedeJugar()) {
            return
        }
        val enemigos: ArrayList<Luchador> = ordenLuchadores(_lanzador.getParamEquipoEnemigo(), Orden.PDV_MENOS_A_MAS,
                Orden.INVOS_ULTIMOS)
        var acercarse = EstadoDistancia.NO_PUEDE
        do {
            while (buffeaSiEsPosible(null)) {
            }
            var movAtq: EstadoMovAtq
            do {
                movAtq = moverYLanzarAlgo(enemigos, Accion.ATACAR, false)
            } while (movAtq == EstadoMovAtq.TACLEADO || movAtq == EstadoMovAtq.SE_MOVIO || movAtq == EstadoMovAtq.LANZO_HECHIZO)
            while (buffeaSiEsPosible(enemigos)) {
            }
        } while (acercarseA(enemigos, false, false).also { acercarse = it } == EstadoDistancia.ACERCARSE)
        siEsInvisible(acercarse)
    }

    private fun tipo_8() { // mochila animada
        if (!_lanzador.puedeJugar()) {
            return
        }
        val amigos: ArrayList<Luchador> = ordenLuchadores(_lanzador.getParamEquipoAliado(), Orden.NIVEL_MAS_A_MENOS)
        var movAtq: EstadoMovAtq
        do {
            movAtq = moverYLanzarAlgo(amigos, Accion.BOOSTEAR, false)
        } while (movAtq == EstadoMovAtq.TACLEADO || movAtq == EstadoMovAtq.SE_MOVIO || movAtq == EstadoMovAtq.LANZO_HECHIZO)
        if (movAtq == EstadoMovAtq.TIENE_HECHIZOS_SIN_LANZAR) {
            while (acercarseA(amigos, false, false) == EstadoDistancia.ACERCARSE) {
            }
        } else {
            while (alejarseDeEnemigo()) {
            }
        }
    }

    private fun tipo_9() { // cofre animado, arbol de vida
        if (!_lanzador.puedeJugar()) {
            return
        }
        while (lanzaHechizoAlAzar(null, Accion.BOOSTEAR)) {
        }
    }

    private fun tipo_10() { // cascara explosiva
        if (!_lanzador.puedeJugar()) {
            return
        }
        val enemigos: ArrayList<Luchador> = ordenLuchadores(_lanzador.getParamEquipoEnemigo(), Orden.PDV_MENOS_A_MAS,
                Orden.INVOS_ULTIMOS)
        var movAtq: EstadoMovAtq
        do {
            movAtq = moverYLanzarAlgo(enemigos, Accion.ATACAR, false)
        } while (movAtq == EstadoMovAtq.TACLEADO || movAtq == EstadoMovAtq.SE_MOVIO || movAtq == EstadoMovAtq.LANZO_HECHIZO)
        while (buffeaSiEsPosible(null)) {
        }
    }

    private fun tipo_11() { // chafer y chaferloko
        if (!_lanzador.puedeJugar()) {
            return
        }
        val todos: ArrayList<Luchador> = ordenLuchadores(3, Orden.PDV_MENOS_A_MAS, Orden.NADA)
        val enemigos: ArrayList<Luchador> = ordenLuchadores(_lanzador.getParamEquipoEnemigo(), Orden.PDV_MENOS_A_MAS,
                Orden.INVOS_ULTIMOS)
        while (buffeaSiEsPosible(null)) {
        } // auto-buff
        var acercarse = EstadoDistancia.NO_PUEDE
        do {
            fullAtaqueSioSi(todos)
        } while (acercarseA(enemigos, false, true).also { acercarse = it } == EstadoDistancia.ACERCARSE)
        siEsInvisible(acercarse)
    }

    private fun tipo_12() { // kralamar
        if (!_lanzador.puedeJugar()) {
            return
        }
        val enemigos: ArrayList<Luchador> = ordenLuchadores(_lanzador.getParamEquipoEnemigo(), Orden.PDV_MENOS_A_MAS,
                Orden.INVOS_ULTIMOS)
        invocarSiEsPosible(enemigos)
        buffeaSiEsPosible(null)
        fullAtaqueSioSi(enemigos)
        if (!invocarSiEsPosible(enemigos) && !buffeaSiEsPosible(null) && !fullAtaqueSioSi(enemigos)) {
            _pelea.esperahechizo = true
            _pelea.esperaMove = true
        }
    }

    private fun tipo_13() { // vasija
        if (!_lanzador.puedeJugar()) {
            return
        }
        while (lanzaHechizoAlAzar(null, Accion.BOOSTEAR)) {
        } // auto boost
        val enemigos: ArrayList<Luchador> = ordenLuchadores(_lanzador.getParamEquipoEnemigo(), Orden.PDV_MENOS_A_MAS,
                Orden.INVOS_ULTIMOS)
        fullAtaqueSioSi(enemigos)
    }

    private fun tipo_14() { // aguja buscadora
        if (!_lanzador.puedeJugar()) {
            return
        }
        var acercarse = EstadoDistancia.NO_PUEDE
        val enemigos: ArrayList<Luchador> = ordenLuchadores(_lanzador.getParamEquipoEnemigo(), Orden.PDV_MENOS_A_MAS,
                Orden.INVOS_ULTIMOS)
        do {
            while (lanzaHechizoAlAzar(enemigos, Accion.ATACAR)) {
            } // ataca
        } while (acercarseA(enemigos, true, true).also { acercarse = it } == EstadoDistancia.ACERCARSE)
        siEsInvisible(acercarse)
    }

    private fun tipo_15() { // IA para @fox discord
        if (!_lanzador.puedeJugar()) {
            return
        }
        val amigos: ArrayList<Luchador> = ordenLuchadores(_lanzador.getParamEquipoAliado(), Orden.NIVEL_MAS_A_MENOS)
        val enemigos: ArrayList<Luchador> = ordenLuchadores(_lanzador.getParamEquipoEnemigo(), Orden.PDV_MENOS_A_MAS,
                Orden.INVOS_ULTIMOS)
        while (buffeaSiEsPosible(amigos)) {
        }
        invocarSiEsPosible(enemigos)
        while (buffeaSiEsPosible(enemigos)) {
        }
        var ataco = false
        if (fullAtaqueSioSi(enemigos)) {
            ataco = true
        } else {
            if (acercarseA(enemigos, true, true) == EstadoDistancia.ACERCARSE) {
                if (fullAtaqueSioSi(enemigos)) {
                    ataco = true
                }
            }
        }
        while (buffeaSiEsPosible(amigos)) {
        }
        if (ataco) {
            while (alejarseDeEnemigo()) {
            }
        }
    }

    private fun tipo_16() { // tentaculos
        if (!_lanzador.puedeJugar()) {
            return
        }
        val amigos: ArrayList<Luchador> = ordenLuchadores(_lanzador.getParamEquipoAliado(), Orden.NIVEL_MAS_A_MENOS)
        val enemigos: ArrayList<Luchador> = ordenLuchadores(_lanzador.getParamEquipoEnemigo(), Orden.PDV_MENOS_A_MAS,
                Orden.INVOS_ULTIMOS)
        var movAtq: EstadoMovAtq
        invocarSiEsPosible(enemigos)
        fullAtaqueSioSi(enemigos)
        while (buffeaSiEsPosible(amigos)) {
        }
        do {
            movAtq = moverYLanzarAlgo(enemigos, Accion.ATACAR, false)
        } while (movAtq == EstadoMovAtq.TACLEADO || movAtq == EstadoMovAtq.SE_MOVIO || movAtq == EstadoMovAtq.LANZO_HECHIZO)
        while (curaSiEsPosible(amigos)) {
        }
        var acercarse = EstadoDistancia.NO_PUEDE
        do {
            if (movAtq != EstadoMovAtq.NO_TIENE_HECHIZOS) {
                fullAtaqueSioSi(enemigos)
            }
        } while (movAtq != EstadoMovAtq.NO_PUEDE_MOVERSE
                && acercarseA(enemigos, true, true).also { acercarse = it } == EstadoDistancia.ACERCARSE)
        siEsInvisible(acercarse)
    }

    private fun tipo_20() { // Prisma
        if (!_lanzador.puedeJugar()) {
            return
        }
        val amigos: ArrayList<Luchador> = ordenLuchadores(_lanzador.getParamEquipoAliado(), Orden.NIVEL_MAS_A_MENOS)
        val enemigos: ArrayList<Luchador> = ordenLuchadores(_lanzador.getParamEquipoEnemigo(), Orden.PDV_MENOS_A_MAS,
                Orden.INVOS_ULTIMOS)
        curaSiEsPosible(amigos)
        buffeaSiEsPosible(amigos)
        fullAtaqueSioSi(enemigos)
    }

    private fun tipo_21() { // recaudador
        if (!_lanzador.puedeJugar()) {
            return
        }
        val amigos: ArrayList<Luchador> = ordenLuchadores(_lanzador.getParamEquipoAliado(), Orden.NIVEL_MAS_A_MENOS)
        val enemigos: ArrayList<Luchador> = ordenLuchadores(_lanzador.getParamEquipoEnemigo(), Orden.PDV_MENOS_A_MAS,
                Orden.INVOS_ULTIMOS)
        val porcPDV: Float = _lanzador.getPorcPDV()
        if (porcPDV > 50) {
            var movAtq: EstadoMovAtq
            buffeaSiEsPosible(amigos)
            fullAtaqueSioSi(enemigos)
            do {
                movAtq = moverYLanzarAlgo(enemigos, Accion.ATACAR, false)
            } while (movAtq == EstadoMovAtq.TACLEADO || movAtq == EstadoMovAtq.SE_MOVIO || movAtq == EstadoMovAtq.LANZO_HECHIZO)
            while (curaSiEsPosible(amigos)) {
            }
            var acercarse = EstadoDistancia.NO_PUEDE
            do {
                if (movAtq != EstadoMovAtq.NO_TIENE_HECHIZOS) {
                    fullAtaqueSioSi(enemigos)
                }
            } while (movAtq != EstadoMovAtq.NO_PUEDE_MOVERSE
                    && acercarseA(enemigos, true, true).also { acercarse = it } == EstadoDistancia.ACERCARSE)
            siEsInvisible(acercarse)
        } else {
            var movAtq: EstadoMovAtq
            while (curaSiEsPosible(null)) {
            }
            buffeaSiEsPosible(amigos)
            fullAtaqueSioSi(enemigos)
            do {
                movAtq = moverYLanzarAlgo(enemigos, Accion.ATACAR, false)
            } while (movAtq == EstadoMovAtq.TACLEADO || movAtq == EstadoMovAtq.SE_MOVIO || movAtq == EstadoMovAtq.LANZO_HECHIZO)
            var acercarse = EstadoDistancia.NO_PUEDE
            do {
                if (movAtq != EstadoMovAtq.NO_TIENE_HECHIZOS) {
                    fullAtaqueSioSi(enemigos)
                }
            } while (movAtq != EstadoMovAtq.NO_PUEDE_MOVERSE
                    && acercarseA(enemigos, true, true).also { acercarse = it } == EstadoDistancia.ACERCARSE)
            siEsInvisible(acercarse)
        }
    }

    private fun siEsInvisible(acercarse: EstadoDistancia) {
        if (acercarse == EstadoDistancia.INVISIBLE) {
            if (Formulas.getRandomInt(1, 3) === 2) {
                while (acercarseAInvis()) {
                }
            } else {
                while (alejarseDeEnemigo()) {
                }
            }
        }
    }

    private fun objetivoApto(objetivo: Luchador?, pInvi: Boolean): Boolean {
        if (objetivo == null) {
            return true
        }
        return if (objetivo.estaMuerto() || pInvi && objetivo.esInvisible(_lanzador.getID())) {
            false
        } else true
    }

    private fun hechizosLanzables(): ArrayList<StatHechizo> {
        val disponibles: ArrayList<StatHechizo> = ArrayList()
        if (_lanzador.getHechizos() == null) {
            return disponibles
        }
        for (SH in _lanzador.getHechizos().values()) {
            if (SH == null) {
                continue
            }
            try {
                if (MainServidor.MODO_DEBUG) {
                    System.out.println("hechizosLanzables() -> Hechizo " + SH.getHechizo().getNombre())
                }
                // filtra los hechizos q esten con tiempo o faltos de PA o algo por el estilo
                if (_pelea.filtraHechizoDisponible(_lanzador, SH, 0) !== EstadoLanzHechizo.PODER) {
                    continue
                }
                disponibles.add(SH)
                for (EH in SH.getEfectosNormales()) {
                    if (Constantes.estimaDaño(EH.getEfectoID()) === 1) {
                        if (_lanzador.getDistMinAtq() === -1) {
                            _lanzador.setDistMinAtq(SH.getMinAlc())
                        }
                        _lanzador.setDistMinAtq(Math.min(SH.getMinAlc(), _lanzador.getDistMinAtq()))
                        break
                    }
                }
            } catch (ignored: Exception) {
            }
        }
        return disponibles
    }

    private fun ordenLuchadores(equipo: Int, vararg orden: Orden): ArrayList<Luchador> {
        val temporales: ArrayList<Luchador> = ArrayList()
        for (luch in _pelea.luchadoresDeEquipo(equipo)) {
            if (!objetivoApto(luch, true)) {
                continue
            }
            temporales.add(luch)
        }
        ordena(temporales, *orden)
        return temporales
    }

    private fun ordenarLuchMasCercano(preLista: ArrayList<Luchador>, celdas: ArrayList<Short>, vararg orden: Orden) {
        var alejados: ArrayList<Luchador>? = ArrayList()
        var cercanos: ArrayList<Luchador>? = ArrayList()
        for (objetivo in preLista) {
            if (!objetivoApto(objetivo, true)) {
                continue
            }
            if (celdas.contains(objetivo.getCeldaPelea().getID())) {
                cercanos.add(objetivo)
            } else {
                alejados.add(objetivo)
            }
        }
        ordena(cercanos, *orden)
        ordena(alejados, *orden)
        preLista.clear()
        preLista.addAll(cercanos)
        preLista.addAll(alejados)
        alejados = null
        cercanos = null
    }

    private fun ordenarLuchVulnerables(preLista: ArrayList<Luchador>) {
        var vulnerables: ArrayList<Luchador?>? = ArrayList()
        var invulnerables: ArrayList<Luchador?>? = ArrayList()
        for (objetivo in preLista) {
            if (!objetivoApto(objetivo, true)) {
                continue
            }
            if (esInvulnerable(objetivo)) {
                invulnerables.add(objetivo)
            } else {
                vulnerables.add(objetivo)
            }
        }
        preLista.clear()
        preLista.addAll(vulnerables)
        preLista.addAll(invulnerables)
        vulnerables = null
        invulnerables = null
    }

    private fun enemigoMasCercano(objetivos: ArrayList<Luchador>): Luchador? {
        var dist = 1000
        var tempObjetivo: Luchador? = null
        for (objetivo in objetivos) {
            if (!objetivoApto(objetivo, true)) {
                continue
            }
            try {
                val d: Int = Camino.distanciaDosCeldas(_pelea.getMapaCopia(), _lanzador.getCeldaPelea().getID(),
                        objetivo.getCeldaPelea().getID())
                if (d < dist) {
                    dist = d
                    tempObjetivo = objetivo
                }
            } catch (ignored: Exception) {
            }
        }
        return tempObjetivo
    }

    private fun alejarseDeEnemigo(): Boolean {
        if (!_lanzador.puedeJugar()) {
            return false
        }
        if (_lanzador.getPMRestantes() <= 0) {
            return false
        }
        val celdaIDLanzador: Short = _lanzador.getCeldaPelea().getID()
        val celdasMovimiento: ArrayList<Short> = Camino.celdasDeMovimiento(_pelea, _lanzador.getCeldaPelea(), true,
                true, null)
        celdasMovimiento.add(celdaIDLanzador)
        val enemigos: ArrayList<Luchador> = ArrayList()
        for (blanco in _pelea.luchadoresDeEquipo(_lanzador.getParamEquipoEnemigo())) {
            if (!objetivoApto(blanco, false)) {
                continue
            }
            enemigos.add(blanco)
        }
        val mapa: Mapa = _pelea.getMapaCopia()
        var distEntreTodos = -1
        var celdaIdeal: Short = -1
        for (celdaTemp in celdasMovimiento) {
            var distTemp = 0
            for (blanco in enemigos) {
                distTemp += Camino.distanciaDosCeldas(mapa, celdaTemp, blanco.getCeldaPelea().getID())
            }
            if (distTemp >= distEntreTodos) {
                distEntreTodos = distTemp
                celdaIdeal = celdaTemp
            }
        }
        if (celdaIdeal.toInt() == -1 || celdaIdeal == celdaIDLanzador) {
            return false
        }
        val pathCeldas: Duo<Integer, ArrayList<Celda>> = Camino.getPathPelea(mapa, celdaIDLanzador, celdaIdeal, -1,
                null, false) ?: return false
        val path: ArrayList<Celda> = pathCeldas._segundo
        val finalPath: ArrayList<Celda> = ArrayList()
        for (a in 0 until _lanzador.getPMRestantes()) {
            if (path.size() === a || path.get(a).getPrimerLuchador() != null) {
                break
            }
            finalPath.add(path.get(a))
        }
        val pathStr: String = Camino.getPathComoString(mapa, finalPath, celdaIDLanzador, true)
        if (pathStr.isEmpty()) {
            return false
        }
        val resultado: String = _pelea.intentarMoverse(_lanzador, pathStr, 0, null)
        return if (resultado.equals("stop")) {
            false
        } else resultado.equals("ok")
    }

    // private static boolean mueveLoMasLejosPosible(final Pelea pelea, final
    // Luchador lanzador) {
    // if (!lanzador.puedeJugar()) {
    // return false;
    // }
    // final int PM = pelea.getTempPM();
    // if (PM <= 0) {
    // return false;
    // }
    //
    // final short celdaIDLanzador = lanzador.getCeldaPelea().getID();
    // final Mapa mapa = pelea.getMapaCopia();
    // final short dist[] = {1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000,
    // 1000}, celda[] =
    // {0, 0, 0, 0, 0, 0, 0,
    // 0, 0, 0};
    // for (int i = 0; i < 10; i++) {
    // for (final Luchador blanco :
    // pelea.luchadoresDeEquipo(lanzador.getParamEquipoEnemigo())) {
    // if (blanco.estaMuerto()) {
    // continue;
    // }
    // final short celdaEnemigo = blanco.getCeldaPelea().getID();
    // if (celdaEnemigo == celda[0] || celdaEnemigo == celda[1] || celdaEnemigo ==
    // celda[2]
    // || celdaEnemigo == celda[3] || celdaEnemigo == celda[4] || celdaEnemigo ==
    // celda[5] ||
    // celdaEnemigo == celda[6]
    // || celdaEnemigo == celda[7] || celdaEnemigo == celda[8] || celdaEnemigo ==
    // celda[9]) {
    // continue;
    // }
    // short d = 0;
    // d = Camino.distanciaDosCeldas(mapa, celdaIDLanzador, celdaEnemigo);
    // if (d == 0) {
    // continue;
    // }
    // if (d < dist[i]) {
    // dist[i] = d;
    // celda[i] = celdaEnemigo;
    // }
    // if (dist[i] == 1000) {
    // dist[i] = 0;
    // celda[i] = celdaIDLanzador;
    // }
    // }
    // }
    // if (dist[0] == 0) {
    // return false;
    // }
    // final int dist2[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    // final byte ancho = mapa.getAncho(), alto = mapa.getAlto();
    // short celdaInicio = celdaIDLanzador;
    // short celdaDestino = celdaIDLanzador;
    // final short ultCelda = Camino.ultimaCeldaID(mapa);
    // final int valor = Formulas.getRandomValor(0, 3);
    // int[] movidas;
    // if (valor == 0) {
    // movidas = new int[]{0, 1, 2, 3};
    // } else if (valor == 1) {
    // movidas = new int[]{1, 2, 3, 0};
    // } else if (valor == 2) {
    // movidas = new int[]{2, 3, 0, 1};
    // } else {
    // movidas = new int[]{3, 0, 1, 2};
    // }
    // for (int i = 0; i <= PM; i++) {
    // if (celdaDestino > 0) {
    // celdaInicio = celdaDestino;
    // }
    // short celdaTemporal = celdaInicio;
    // int infl = 0, inflF = 0;
    // for (final int x : movidas) {
    // switch (x) {
    // case 0 :
    // celdaTemporal = (short) (celdaTemporal + ancho);
    // break;
    // case 1 :
    // celdaTemporal = (short) (celdaInicio + (ancho - 1));
    // break;
    // case 2 :
    // celdaTemporal = (short) (celdaInicio - ancho);
    // break;
    // case 3 :
    // celdaTemporal = (short) (celdaInicio - (ancho - 1));
    // break;
    // }
    // infl = 0;
    // for (int a = 0; a < 10 && dist[a] != 0; a++) {
    // dist2[a] = Camino.distanciaDosCeldas(mapa, celdaTemporal, celda[a]);
    // if (dist2[a] > dist[a]) {
    // infl++;
    // }
    // }
    // if (infl > inflF && celdaTemporal > 0 && celdaTemporal < ultCelda
    // && !Camino.celdaSalienteLateral(ancho, alto, celdaDestino, celdaTemporal)
    // && mapa.getCelda(celdaTemporal).esCaminable(true)) {
    // inflF = infl;
    // celdaDestino = celdaTemporal;
    // }
    // }
    // }
    // if (celdaDestino < 0 || celdaDestino > ultCelda || celdaDestino ==
    // celdaIDLanzador
    // || !mapa.getCelda(celdaDestino).esCaminable(true)) {
    // return false;
    // }
    // final ArrayList<Celda> path = Camino.pathMasCortoEntreDosCeldas(mapa,
    // celdaIDLanzador,
    // celdaDestino, 0);
    // if (path == null) {
    // return false;
    // }
    // final ArrayList<Celda> finalPath = new ArrayList<Celda>();
    // for (int a = 0; a < pelea.getTempPM(); a++) {
    // if (path.size() == a) {
    // break;
    // }
    // finalPath.add(path.get(a));
    // }
    // final StringBuilder pathStr = new StringBuilder();
    // try {
    // short tempCeldaID = celdaIDLanzador;
    // for (final Celda c : finalPath) {
    // final char d = Camino.dirEntreDosCeldas(mapa, tempCeldaID, c.getID(), true);
    // if (d == 0) {
    // return false;
    // }
    // if (finalPath.indexOf(c) != 0) {
    // pathStr.append(Encriptador.celdaIDACodigo(tempCeldaID));
    // }
    // pathStr.append(d);
    // tempCeldaID = c.getID();
    // }
    // if (tempCeldaID != celdaIDLanzador) {
    // pathStr.append(Encriptador.celdaIDACodigo(tempCeldaID));
    // }
    // } catch (final Exception e) {
    // e.printStackTrace();
    // }
    // String resultado = pelea.intentaMoverseLuchador(lanzador, pathStr.toString(),
    // 0);
    // if (resultado.equals("stop")) {
    // return mueveLoMasLejosPosible(pelea, lanzador);
    // }
    // return resultado.equals("ok");
    // }
    //
    private fun acercarseAInvis(): Boolean {
        if (!_lanzador.puedeJugar()) {
            return false
        }
        if (_lanzador.getPMRestantes() <= 0) {
            return false
        }
        val enemigos: ArrayList<Luchador> = ArrayList()
        for (blanco in _pelea.luchadoresDeEquipo(_lanzador.getParamEquipoEnemigo())) {
            if (!objetivoApto(blanco, false)) {
                continue
            }
            enemigos.add(blanco)
        }
        val mapa: Mapa = _pelea.getMapaCopia()
        val celdaIDLanzador: Short = _lanzador.getCeldaPelea().getID()
        val celdasMovimiento: ArrayList<Short> = Camino.celdasDeMovimiento(_pelea, _lanzador.getCeldaPelea(), false,
                false, null)
        if (celdasMovimiento.isEmpty()) {
            return false
        }
        celdasMovimiento.add(_lanzador.getCeldaPelea().getID())
        //
        val tempObjetivos: ArrayList<Luchador> = ArrayList()
        tempObjetivos.addAll(enemigos)
        var tempCeldaID: Short = -1
        var dist = 1000
        var repeticiones = 100
        val path: ArrayList<Celda> = ArrayList()
        for (objetivo in tempObjetivos) {
            if (!objetivoApto(objetivo, false)) {
                continue
            }
            if (objetivo === _lanzador) {
                continue
            }
            if (objetivo.esEstatico() && objetivo.getEquipoBin() === _lanzador.getEquipoBin()) {
                continue
            }
            var celdaTempObj: Short = objetivo.getCeldaPelea().getID()
            val pathTemp: Duo<Integer, ArrayList<Celda>> = Camino.getPathPelea(mapa, celdaIDLanzador, celdaTempObj, -1,
                    null, false)
            if (pathTemp == null || pathTemp._segundo.isEmpty()) {
                tempCeldaID = -2
                continue
            } else if (pathTemp._primero < repeticiones) {
                celdaTempObj = pathTemp._segundo.get(pathTemp._segundo.size() - 1).getID()
                if (celdasMovimiento.contains(objetivo.getCeldaPelea().getID())
                        && pathTemp._segundo.size() <= _lanzador.getPMRestantes() && pathTemp._primero === 0) {
                    path.clear()
                    path.addAll(pathTemp._segundo)
                    tempCeldaID = celdaTempObj
                    // break;
                } else {
                    val d: Int = Camino.distanciaDosCeldas(mapa, celdaIDLanzador, celdaTempObj)
                    if (d < dist || pathTemp._primero < repeticiones) {
                        path.clear()
                        path.addAll(pathTemp._segundo)
                        tempCeldaID = celdaTempObj
                        dist = d
                    }
                }
                repeticiones = pathTemp._primero
                break
            }
        }
        if (tempCeldaID.toInt() == -1) {
            return false
        } else if (tempCeldaID.toInt() == -2) { // (-2) el path es nulo porq no hay camino
            return false
        }
        val finalPath: ArrayList<Celda> = ArrayList<Celda>()
        for (a in 0 until _lanzador.getPMRestantes()) {
            if (path.size() === a || path.get(a).getPrimerLuchador() != null) {
                break
            }
            finalPath.add(path.get(a))
        }
        val pathStr: String = Camino.getPathComoString(mapa, finalPath, celdaIDLanzador, true)
        if (pathStr.isEmpty()) {
            return false
        }
        val resultado: String = _pelea.intentarMoverse(_lanzador, pathStr, 0, null)
        when (resultado) {
            "stop", "ok" -> return true
            "tacleado" -> return true
        }
        return false
    }

    private fun acercarseA(objetivos: ArrayList<Luchador>, masCercano: Boolean,
                           paraAtacar: Boolean): EstadoDistancia {
        if (!_lanzador.puedeJugar()) {
            return EstadoDistancia.NO_PUEDE
        }
        if (_lanzador.getPMRestantes() <= 0) {
            return EstadoDistancia.NO_PUEDE
        }
        if (objetivos.isEmpty()) {
            return EstadoDistancia.INVISIBLE
        }
        val mapa: Mapa = _pelea.getMapaCopia()
        val celdaIDLanzador: Short = _lanzador.getCeldaPelea().getID()
        val celdasMovimiento: ArrayList<Short> = Camino.celdasDeMovimiento(_pelea, _lanzador.getCeldaPelea(), false,
                false, null)
        if (celdasMovimiento.isEmpty()) {
            return EstadoDistancia.NO_PUEDE
        }
        celdasMovimiento.add(_lanzador.getCeldaPelea().getID())
        //
        val tempObjetivos: ArrayList<Luchador> = ArrayList()
        tempObjetivos.addAll(objetivos)
        if (masCercano) {
            ordenarLuchMasCercano(tempObjetivos, celdasMovimiento, Orden.PDV_MENOS_A_MAS)
        }
        if (paraAtacar) {
            ordenarLuchVulnerables(tempObjetivos)
        }
        var tempCeldaID: Short = -1
        var celdaLuchObjetivo: Short = -1
        var dist = 1000
        var repeticiones = 100
        val path: ArrayList<Celda> = ArrayList()
        for (objetivo in tempObjetivos) {
            if (!objetivoApto(objetivo, true)) {
                continue
            }
            if (objetivo === _lanzador) {
                continue
            }
            if (objetivo.esEstatico() && objetivo.getEquipoBin() === _lanzador.getEquipoBin()) {
                continue
            }
            var celdaTempObj: Short = objetivo.getCeldaPelea().getID()
            val pathTemp: Duo<Integer, ArrayList<Celda>> = Camino.getPathPelea(mapa, celdaIDLanzador, celdaTempObj, -1,
                    null, false)
            if (pathTemp == null || pathTemp._segundo.isEmpty()) {
                tempCeldaID = -2
                continue
            } else if (pathTemp._primero < repeticiones) {
                celdaTempObj = pathTemp._segundo.get(pathTemp._segundo.size() - 1).getID()
                if (celdasMovimiento.contains(objetivo.getCeldaPelea().getID())
                        && pathTemp._segundo.size() <= _lanzador.getPMRestantes() && pathTemp._primero === 0) {
                    path.clear()
                    path.addAll(pathTemp._segundo)
                    tempCeldaID = celdaTempObj
                    celdaLuchObjetivo = objetivo.getCeldaPelea().getID()
                    // break;
                } else {
                    val d: Int = Camino.distanciaDosCeldas(mapa, celdaIDLanzador, celdaTempObj)
                    if (d < dist || pathTemp._primero < repeticiones) {
                        path.clear()
                        path.addAll(pathTemp._segundo)
                        tempCeldaID = celdaTempObj
                        celdaLuchObjetivo = objetivo.getCeldaPelea().getID()
                        dist = d
                    }
                }
                repeticiones = pathTemp._primero
                break
            }
        }
        if (tempCeldaID.toInt() == -1) {
            return EstadoDistancia.NO_PUEDE
        } else if (tempCeldaID.toInt() == -2) { // (-2) el path es nulo porq no hay camino
            return EstadoDistancia.NO_PUEDE
        }
        val finalPath: ArrayList<Celda> = AstarPathfinding(mapa, _pelea, _lanzador.getCeldaPelea().getID(), celdaLuchObjetivo).getShortestPath(-1.toShort())
        for (a in 0 until _lanzador.getPMRestantes()) {
            if (path.size() === a || path.get(a).getPrimerLuchador() != null) {
                break
            }
            if (paraAtacar) {
                val d: Int = Camino.distanciaDosCeldas(mapa, path.get(a).getID(), celdaLuchObjetivo)
                if (_lanzador.getDistMinAtq() !== -1 && d < _lanzador.getDistMinAtq()) {
                    break
                }
            }
            finalPath.add(path.get(a))
        }
        val pathStr: String = Camino.getPathComoString(mapa, finalPath, celdaIDLanzador, true)
        if (pathStr.isEmpty()) {
            return EstadoDistancia.NO_PUEDE
        }
        val resultado: String = _pelea.intentarMoverse(_lanzador, pathStr, 0, null)
        when (resultado) {
            "stop", "ok" -> return EstadoDistancia.ACERCARSE
            "tacleado" -> return EstadoDistancia.TACLEADO
        }
        return EstadoDistancia.NO_PUEDE
    }

    private fun lanzaHechizoAlAzar(objetivos: ArrayList<Luchador>?, accion: Accion): Boolean {
        var objetivos: ArrayList<Luchador>? = objetivos
        if (!_lanzador.puedeJugar()) {
            return false
        }
        if (objetivos == null || objetivos.isEmpty()) {
            objetivos = ArrayList()
            objetivos.add(_lanzador)
        }
        for (objetivo in objetivos) {
            if (!objetivoApto(objetivo, true)) {
                continue
            }
            val SH: StatHechizo = hechizoAlAzar(objetivo) ?: continue
            if (accion == Accion.ATACAR && tieneReenvio(_lanzador, objetivo, SH)) {
                continue
            }
            if (_pelea.intentarLanzarHechizo1(_lanzador, SH, objetivo.getCeldaPelea(),
                            true) !== EstadoLanzHechizo.PODER) {
                continue
            }
            return true
        }
        return false
    }

    private fun setCeldasMovimiento() {
        if (_refrescarMov) {
            _celdasMovimiento = Camino.celdasDeMovimiento(_pelea, _lanzador.getCeldaPelea(), true, true, _lanzador)
            _refrescarMov = false
        }
    }

    private fun moverYLanzarAlgo(objetivos: ArrayList<Luchador>?, buffInvoAtaq: Accion,
                                 obviarTacle: Boolean): EstadoMovAtq {
        if (objetivos == null || !_lanzador.puedeJugar()) {
            return EstadoMovAtq.NO_HIZO_NADA
        }
        var filtroPorHechizo: StatHechizo? = null
        for (SH2 in hechizosLanzables()) {
            for (objetivo in objetivos) {
                val filtroIA = getFiltroIA(SH2, objetivo, buffInvoAtaq)
                if (filtroIA < 0) {
                    continue
                }
                filtroPorHechizo = SH2
                break
            }
            if (filtroPorHechizo != null) {
                break
            }
        }
        if (filtroPorHechizo == null) {
            return EstadoMovAtq.NO_TIENE_HECHIZOS
        }
        val tempObjetivos: ArrayList<Luchador> = ArrayList()
        tempObjetivos.addAll(objetivos)
        if (buffInvoAtaq == Accion.ATACAR) {
            Collections.sort(tempObjetivos, CompPDVMenosMas())
        }
        Collections.sort(tempObjetivos, CompInvosUltimos())
        // -----
        val mapa: Mapa = _pelea.getMapaCopia()
        var SH: StatHechizo? = null
        var celdaObjetivoLanz: Celda? = null
        val celdaIDLanzador: Short = _lanzador.getCeldaPelea().getID()
        var celdaDestinoMov: Short = 0
        var influenciaMax = -1000000000
        var distancia = 10000
        val tempCeldasMovPrioridad: ArrayList<Short> = ArrayList()
        tempCeldasMovPrioridad.add(_lanzador.getCeldaPelea().getID())
        setCeldasHechizoCeldaLanz()
        setCeldasMovimiento()
        // ordena por prioridad las celdas segun los objetivos
        if (MainServidor.NIVEL_INTELIGENCIA_ARTIFICIAL > INTELIGENCIA_ORDENAR_PRIORIDAD_OBJETIVOS) {
            for (objetivo in tempObjetivos) {
                if (!objetivoApto(objetivo, true)) {
                    continue
                }
                if (buffInvoAtaq == Accion.CURAR && objetivo.getPorcPDV() > PDV_MINIMO_CURAR) {
                    continue
                }
                val pathTemp: Duo<Integer, ArrayList<Celda>> = Camino.getPathPelea(mapa, _lanzador.getCeldaPelea().getID(),
                        objetivo.getCeldaPelea().getID(), -1, _lanzador, true) ?: continue
                for (c in pathTemp._segundo) {
                    if (!_celdasMovimiento.contains(c.getID()) || tempCeldasMovPrioridad.contains(c.getID())) {
                        continue
                    }
                    tempCeldasMovPrioridad.add(c.getID())
                }
            }
        }
        for (c in _celdasMovimiento) {
            if (tempCeldasMovPrioridad.contains(c)) {
                continue
            }
            tempCeldasMovPrioridad.add(c)
        }
        for (tempCeldaLanz in tempCeldasMovPrioridad) {
            var map: Map<StatHechizo?, Map<Celda?, ArrayList<Luchador?>?>?> = _celdasHechizos.get(tempCeldaLanz)
            if (map == null) {
                map = getObjHechDesdeCeldaLanz(tempCeldaLanz)
                if (map == null || map.isEmpty()) {
                    continue
                }
                _celdasHechizos.put(tempCeldaLanz, map)
            }
            // }
            // if (_celdasHechizos.isEmpty()) {
            // return EstadoMovAtq.NO_TIENE_HECHIZOS;
            // }
            // for (short tempCeldaLanz : tempCeldasMovPrioridad) {
            var prioridadObj: Int = tempObjetivos.size()
            for (objetivo in tempObjetivos) {
                prioridadObj--
                if (!objetivoApto(objetivo, true)) {
                    continue
                }
                var duo: Duo<Integer?, Duo<StatHechizo?, Celda?>?>? = null
                if (buffInvoAtaq == Accion.ATACAR) {
                    duo = mejorAtaque(tempCeldaLanz, objetivo, map)
                } else if (buffInvoAtaq == Accion.BOOSTEAR) {
                    duo = mejorBuff(tempCeldaLanz, objetivo, map)
                } else if (buffInvoAtaq == Accion.CURAR) {
                    duo = mejorCura(tempCeldaLanz, objetivo, map)
                }
                if (duo == null || duo._primero <= 0) {
                    continue
                }
                val tempInf: Int = duo._primero + prioridadObj
                // esto era un antiguo codigo
                if (SH == null || tempInf > influenciaMax) {
                    influenciaMax = tempInf
                    SH = duo._segundo._primero
                    celdaObjetivoLanz = duo._segundo._segundo
                    celdaDestinoMov = tempCeldaLanz
                    if (MainServidor.NIVEL_INTELIGENCIA_ARTIFICIAL <= INTELIGENCIA_COMPARAR_INF_OBJETIVOS) {
                        break
                    }
                    if (MainServidor.NIVEL_INTELIGENCIA_ARTIFICIAL > INTELIGENCIA_COMPARAR_INF_DIST_OBJETIVOS) {
                        distancia = Camino.distanciaDosCeldas(mapa, celdaIDLanzador, tempCeldaLanz)
                    }
                    //
                    // objetivo = tempObjetivo;
                }
                if (MainServidor.NIVEL_INTELIGENCIA_ARTIFICIAL > INTELIGENCIA_COMPARAR_INF_DIST_OBJETIVOS) {
                    if (tempInf == influenciaMax
                            && Camino.distanciaDosCeldas(mapa, celdaIDLanzador, tempCeldaLanz) < distancia) {
                        influenciaMax = tempInf
                        SH = duo._segundo._primero
                        celdaObjetivoLanz = duo._segundo._segundo
                        celdaDestinoMov = tempCeldaLanz
                        distancia = Camino.distanciaDosCeldas(mapa, celdaIDLanzador, tempCeldaLanz)
                        // objetivo = tempObjetivo;
                    }
                }
                // else
            }
            if (MainServidor.NIVEL_INTELIGENCIA_ARTIFICIAL <= INTELIGENCIA_COMPARAR_INF_DIST_OBJETIVOS) {
                if (SH != null) {
                    break
                }
            }
        }
        if (SH == null) {
            return EstadoMovAtq.TIENE_HECHIZOS_SIN_LANZAR
        }
        if (celdaDestinoMov.toInt() == 0 || celdaDestinoMov == celdaIDLanzador) {
            // si no hay necesidad de moverse y solo se lanza el hechizo sobre la ubicacion
            val i: EstadoLanzHechizo = _pelea.intentarLanzarHechizo1(_lanzador, SH, celdaObjetivoLanz, false)
            return if (i == EstadoLanzHechizo.PODER) {
                EstadoMovAtq.LANZO_HECHIZO
            } else when (i) {
                EstadoLanzHechizo.PODER -> EstadoMovAtq.LANZO_HECHIZO
                EstadoLanzHechizo.NO_TIENE_ALCANCE -> EstadoMovAtq.TIENE_HECHIZOS_SIN_LANZAR
                else -> EstadoMovAtq.NO_HIZO_NADA
            }
        }
        val pathCeldas: Duo<Integer, ArrayList<Celda>> = Camino.getPathPelea(mapa, celdaIDLanzador, celdaDestinoMov,
                -1, null, false) ?: return EstadoMovAtq.TIENE_HECHIZOS_SIN_LANZAR
        val path: ArrayList<Celda> = pathCeldas._segundo
        val finalPath: ArrayList<Celda> = AstarPathfinding(_pelea.getMapaCopia(), _pelea, _lanzador.getCeldaPelea().getID(), celdaDestinoMov).getShortestPath(-1.toShort())
        var a = 0
        while (a < _lanzador.getPMRestantes() && a < path.size()) {
            if (path.get(a).getPrimerLuchador() != null) {
                break
            }
            // int d = Camino.distanciaDosCeldas(mapa, path.get(a).getID(),
            // celdaDestinoMov);
            // if (lanzador._distMinAtq != -1 && d < lanzador._distMinAtq) {
            // break;
            // }
            finalPath.add(path.get(a))
            a++
        }
        val pathStr: String = Camino.getPathComoString(mapa, finalPath, celdaIDLanzador, true)
        if (pathStr.isEmpty()) {
            return EstadoMovAtq.TIENE_HECHIZOS_SIN_LANZAR
        }
        val resultado: String = _pelea.intentarMoverse(_lanzador, pathStr, 0, null)
        when (resultado) {
            "ok" -> {
                val i: EstadoLanzHechizo = _pelea.intentarLanzarHechizo1(_lanzador, SH, celdaObjetivoLanz, false)
                return if (i == EstadoLanzHechizo.PODER) {
                    EstadoMovAtq.LANZO_HECHIZO
                } else EstadoMovAtq.SE_MOVIO
            }
            "stop" -> return EstadoMovAtq.SE_MOVIO
        }
        return EstadoMovAtq.TIENE_HECHIZOS_SIN_LANZAR
    }

    private fun fullAtaqueSioSi(enemigos: ArrayList<Luchador>?): Boolean {
        if (enemigos == null || !_lanzador.puedeJugar()) {
            return false
        }
        var ataco = false
        var objetivos: CopyOnWriteArrayList<Luchador>? = CopyOnWriteArrayList(enemigos)
        objetivos.sort(CompPDVMenosMas())
        objetivos.sort(CompInvosUltimos())
        while (atacaSiEsPosible(objetivos) == EstadoLanzHechizo.PODER) {
            ataco = true
        }
        objetivos = null
        return ataco
    }

    private fun atacaSiEsPosible(objetivos: CopyOnWriteArrayList<Luchador>?): EstadoLanzHechizo {
        if (objetivos == null || !_lanzador.puedeJugar()) {
            return EstadoLanzHechizo.NO_PODER
        }
        setCeldasHechizoCeldaLanz()
        // objetivos = listaLuchadoresMasCercano(pelea, lanzador, objetivos,
        // Orden.PDV_MENOS_A_MAS);
        for (objetivo in objetivos) {
            if (!objetivoApto(objetivo, true)) {
                continue
            }
            val celdaLanzID: Short = _lanzador.getCeldaPelea().getID()
            val duo: Duo<Integer, Duo<StatHechizo, Celda>>? = mejorAtaque(celdaLanzID, objetivo,
                    _celdasHechizos.get(celdaLanzID))
            if (duo != null) {
                return _pelea.intentarLanzarHechizo1(_lanzador, duo._segundo._primero, duo._segundo._segundo, true)
            }
        }
        return EstadoLanzHechizo.NO_PODER // no pudo lanzar
    }

    private fun buffeaSiEsPosible(objetivos: ArrayList<Luchador>?): Boolean {
        var objetivos: ArrayList<Luchador>? = objetivos
        if (!_lanzador.puedeJugar()) {
            return false
        }
        setCeldasHechizoCeldaLanz()
        if (objetivos == null || objetivos.isEmpty()) {
            objetivos = ArrayList()
            objetivos.add(_lanzador)
        }
        // Collections.sort(objetivos, new CompNivelMasMenos());
        for (objetivo in objetivos) {
            if (!objetivoApto(objetivo, true)) {
                continue
            }
            val celdaLanzID: Short = _lanzador.getCeldaPelea().getID()
            val duo: Duo<Integer, Duo<StatHechizo, Celda>>? = mejorBuff(celdaLanzID, objetivo,
                    _celdasHechizos.get(celdaLanzID))
            if (duo != null) {
                return _pelea.intentarLanzarHechizo1(_lanzador, duo._segundo._primero, duo._segundo._segundo,
                        true) === EstadoLanzHechizo.PODER
            }
        }
        return false
    }

    private fun curaSiEsPosible(objetivos: ArrayList<Luchador>?): Boolean {
        if (!_lanzador.puedeJugar()) {
            return false
        }
        setCeldasHechizoCeldaLanz()
        val paraCurar: ArrayList<Luchador> = ArrayList()
        if (objetivos == null || objetivos.isEmpty()) {
            paraCurar.add(_lanzador)
        } else {
            paraCurar.addAll(objetivos)
        }
        paraCurar.sort(CompPDVMenosMas())
        for (objetivo in paraCurar) {
            if (!objetivoApto(objetivo, true)) {
                continue
            }
            if (objetivo.getPorcPDV() > PDV_MINIMO_CURAR) {
                continue
            }
            val celdaLanzID: Short = _lanzador.getCeldaPelea().getID()
            val duo: Duo<Integer, Duo<StatHechizo, Celda>>? = mejorCura(celdaLanzID, objetivo,
                    _celdasHechizos.get(celdaLanzID))
            if (duo != null) {
                return _pelea.intentarLanzarHechizo1(_lanzador, duo._segundo._primero, duo._segundo._segundo,
                        true) === EstadoLanzHechizo.PODER
            }
        }
        return false
    }

    private fun invocarSiEsPosible(objetivos: ArrayList<Luchador>?): Boolean {
        if (objetivos == null || !_lanzador.puedeJugar()) {
            return false
        }
        if (_lanzador.getNroInvocaciones() >= _lanzador.getTotalStats()
                        .getTotalStatParaMostrar(Constantes.STAT_MAS_CRIATURAS_INVO, _lanzador.getPelea(), null)) {
            return false
        }
        val enemigoCercano: Luchador = enemigoMasCercano(objetivos) ?: return false
        val hechizo: Duo<Celda, StatHechizo> = mejorInvocacion(enemigoCercano) ?: return false
        return _pelea.intentarLanzarHechizo1(_lanzador, hechizo._segundo, hechizo._primero,
                true) === EstadoLanzHechizo.PODER
    }

    private fun teleportSiEsPosible(objetivos: ArrayList<Luchador>?): Boolean {
        if (objetivos == null || !_lanzador.puedeJugar()) {
            return false
        }
        if (_lanzador.tieneEstado(Constantes.ESTADO_PESADO) || _lanzador.tieneEstado(Constantes.ESTADO_ARRAIGADO)
                || _lanzador.tieneEstado(Constantes.ESTADO_PORTADOR)
                || _lanzador.tieneEstado(Constantes.ESTADO_TRANSPORTADO) || objetivos.isEmpty()) {
            return false
        }
        if (Camino.getEnemigoAlrededor(_lanzador.getCeldaPelea().getID(), _pelea.getMapaCopia(), null,
                        _lanzador.getEquipoBin()) != null) {
            return false
        }
        val enemigoCercano: Luchador = objetivos.get(0) ?: return false
        val hechizo: Duo<Celda, StatHechizo> = mejorTeleport(enemigoCercano) ?: return false
        return _pelea.intentarLanzarHechizo1(_lanzador, hechizo._segundo, hechizo._primero,
                true) === EstadoLanzHechizo.PODER
    }

    private fun trampearSiEsPosible(): Boolean {
        if (!_lanzador.puedeJugar()) {
            return false
        }
        val hechizo: Duo<Celda, StatHechizo> = mejorGlifoTrampa() ?: return false
        return _pelea.intentarLanzarHechizo1(_lanzador, hechizo._segundo, hechizo._primero,
                true) === EstadoLanzHechizo.PODER
    }

    private fun hechizoAlAzar(objetivo: Luchador?): StatHechizo? {
        if (!_lanzador.puedeJugar() || objetivo == null) {
            return null
        }
        val hechizos: ArrayList<StatHechizo> = ArrayList()
        for (SH in hechizosLanzables()) {
            if (!_pelea.dentroDelRango(_lanzador, SH, _lanzador.getCeldaPelea().getID(),
                            objetivo.getCeldaPelea().getID())) {
                continue
            }
            hechizos.add(SH)
        }
        return if (hechizos.isEmpty()) {
            null
        } else hechizos.get(Formulas.getRandomInt(0, hechizos.size() - 1))
    }

    private fun getFiltroIA(SH2: StatHechizo, objetivo: Luchador?, accion: Accion): Int {
        var c = ' '
        if (objetivo != null) {
            c = if (_lanzador.getEquipoBin() === objetivo.getEquipoBin()) {
                '+'
            } else {
                '-'
            }
        }
        return SH2.filtroValorIA(accion, c)
    }

    private fun mejorAtaque(celdaLanzador: Short, objetivo: Luchador,
                            map: Map<StatHechizo?, Map<Celda?, ArrayList<Luchador?>?>?>?): Duo<Integer, Duo<StatHechizo, Celda>>? {
        if (!_lanzador.puedeJugar()) {
            return null
        }
        if (map == null) {
            return null
        }
        var menorCostePA = 1000
        var influenciaMax = 0
        var SH: StatHechizo? = null
        var celdaObjetivo: Celda? = null
        if (!objetivoApto(objetivo, true)) {
            return null
        }
        clearInfluencias()
        for (e in map.entrySet()) {
            val SH2: StatHechizo = e.getKey()
            val filtroIA = getFiltroIA(SH2, objetivo, Accion.ATACAR)
            if (filtroIA < 0) {
                continue
            }
            for (f in e.getValue().entrySet()) {
                val celdaPosibleObj: Celda = f.getKey()
                if (!estaDentroObjetivos(objetivo, f.getValue(), celdaPosibleObj)) {
                    continue
                }
                if (_pelea.puedeLanzarHechizo(_lanzador, SH2, celdaPosibleObj,
                                celdaLanzador) !== EstadoLanzHechizo.PODER) {
                    continue
                }
                val influencia = calculaInfluenciaDaño(_pelea.getMapaCopia(), SH2, celdaPosibleObj.getID(),
                        celdaLanzador, filtroIA)
                if (influencia <= 0) {
                    continue
                }
                if (influencia > influenciaMax || influencia == influenciaMax && SH2.getCostePA() < menorCostePA) {
                    SH = SH2
                    celdaObjetivo = celdaPosibleObj
                    menorCostePA = SH2.getCostePA()
                    influenciaMax = influencia
                }
            }
        }
        if (celdaObjetivo == null || SH == null) {
            return null
        }
        val a: Duo<StatHechizo, Celda> = Duo(SH, celdaObjetivo)
        if (MainServidor.MODO_DEBUG) {
            System.out.println("mejorAtaque() Hechizo: " + SH.getHechizo().getNombre().toString() + " (" + SH.getHechizoID()
                    .toString() + ") Celda: " + celdaObjetivo.getID().toString() + " Inf: " + influenciaMax)
        }
        return Duo(influenciaMax, a)
    }

    private fun mejorBuff(celdaLanzador: Short, objetivo: Luchador,
                          map: Map<StatHechizo?, Map<Celda?, ArrayList<Luchador?>?>?>?): Duo<Integer, Duo<StatHechizo, Celda>>? {
        if (!_lanzador.puedeJugar()) {
            return null
        }
        if (map == null) {
            return null
        }
        var menorCostePA = 1000
        var influenciaMax = 0
        var SH: StatHechizo? = null
        var celdaObjetivo: Celda? = null
        if (!objetivoApto(objetivo, true)) {
            return null
        }
        clearInfluencias()
        for (e in map.entrySet()) {
            val SH2: StatHechizo = e.getKey()
            val filtroIA = getFiltroIA(SH2, objetivo, Accion.BOOSTEAR)
            if (filtroIA < 0) {
                continue
            }
            for (f in e.getValue().entrySet()) {
                val celdaPosibleObj: Celda = f.getKey()
                if (!estaDentroObjetivos(objetivo, f.getValue(), celdaPosibleObj)) {
                    continue
                }
                if (_pelea.puedeLanzarHechizo(_lanzador, SH2, celdaPosibleObj,
                                celdaLanzador) !== EstadoLanzHechizo.PODER) {
                    continue
                }
                val influencia = calculaInfluenciaBuff(_pelea.getMapaCopia(), SH2, celdaPosibleObj.getID(),
                        celdaLanzador, filtroIA)
                if (influencia <= 0) {
                    continue
                }
                if (influencia > influenciaMax || influencia == influenciaMax && SH2.getCostePA() < menorCostePA) {
                    SH = SH2
                    celdaObjetivo = celdaPosibleObj
                    menorCostePA = SH2.getCostePA()
                    influenciaMax = influencia
                }
            }
        }
        if (celdaObjetivo == null || SH == null) {
            return null
        }
        val a: Duo<StatHechizo, Celda> = Duo(SH, celdaObjetivo)
        if (MainServidor.MODO_DEBUG) {
            System.out.println("mejorBuff() Hechizo: " + SH.getHechizo().getNombre().toString() + " (" + SH.getHechizoID()
                    .toString() + ") Celda: " + celdaObjetivo.getID().toString() + " Inf: " + influenciaMax)
        }
        return Duo(influenciaMax, a)
    }

    private fun mejorCura(celdaLanzador: Short, objetivo: Luchador,
                          map: Map<StatHechizo?, Map<Celda?, ArrayList<Luchador?>?>?>?): Duo<Integer, Duo<StatHechizo, Celda>>? {
        if (!_lanzador.puedeJugar()) {
            return null
        }
        if (map == null) {
            return null
        }
        var menorCostePA = 1000
        var influenciaMax = 0
        var SH: StatHechizo? = null
        var celdaObjetivo: Celda? = null
        if (!objetivoApto(objetivo, true)) {
            return null
        }
        clearInfluencias()
        for (e in map.entrySet()) {
            val SH2: StatHechizo = e.getKey()
            val filtroIA = getFiltroIA(SH2, objetivo, Accion.CURAR)
            if (filtroIA < 0) {
                continue
            }
            for (f in e.getValue().entrySet()) {
                val celdaPosibleObj: Celda = f.getKey()
                if (!estaDentroObjetivos(objetivo, f.getValue(), celdaPosibleObj)) {
                    continue
                }
                if (_pelea.puedeLanzarHechizo(_lanzador, SH2, celdaPosibleObj,
                                celdaLanzador) !== EstadoLanzHechizo.PODER) {
                    continue
                }
                val influencia = calculaInfluenciaCura(_pelea.getMapaCopia(), SH2, celdaPosibleObj.getID(),
                        celdaLanzador, filtroIA)
                if (influencia <= 0) {
                    continue
                }
                if (influencia > influenciaMax || influencia == influenciaMax && SH2.getCostePA() < menorCostePA) {
                    SH = SH2
                    celdaObjetivo = celdaPosibleObj
                    menorCostePA = SH2.getCostePA()
                    influenciaMax = influencia
                }
            }
        }
        if (celdaObjetivo == null || SH == null) {
            return null
        }
        val a: Duo<StatHechizo, Celda> = Duo(SH, celdaObjetivo)
        if (MainServidor.MODO_DEBUG) {
            System.out.println("mejorCura() Hechizo: " + SH.getHechizo().getNombre().toString() + " (" + SH.getHechizoID()
                    .toString() + ") Celda: " + celdaObjetivo.getID().toString() + " Inf: " + influenciaMax)
        }
        return Duo(influenciaMax, a)
    }

    private fun mejorInvocacion(objetivo: Luchador?): Duo<Celda, StatHechizo>? {
        if (!_lanzador.puedeJugar()) {
            return null
        }
        if (objetivo == null) {
            return null
        }
        for (SH in hechizosLanzables()) {
            val filtroIA = getFiltroIA(SH, objetivo, Accion.INVOCAR)
            if (filtroIA < 0) {
                continue
            }
            if (filtroIA == 0) {
                var esInvocacion = false
                for (EH in SH.getEfectosNormales()) {
                    when (EH.getEfectoID()) {
                        180, 181, 185, 780 -> esInvocacion = true
                    }
                }
                if (!esInvocacion) {
                    continue
                }
            }
            var distancia = 1000
            var celdaObjetivo: Celda? = null
            val celdas: ArrayList<Celda> = Camino.celdasPosibleLanzamiento(SH, _lanzador, _pelea.getMapaCopia(),
                    _lanzador.getCeldaPelea().getID(), -1.toShort())
            for (celda in celdas) {
                val dist: Int = Camino.distanciaDosCeldas(_pelea.getMapaCopia(), celda.getID(),
                        objetivo.getCeldaPelea().getID())
                if (dist < distancia) {
                    celdaObjetivo = celda
                    distancia = dist
                }
            }
            if (celdaObjetivo == null) {
                continue
            }
            if (MainServidor.MODO_DEBUG) {
                System.out.println("mejorInvocacion() Hechizo: " + SH.getHechizo().getNombre().toString() + " ("
                        + SH.getHechizoID().toString() + ") Celda: " + celdaObjetivo.getID())
            }
            return Duo<Celda, StatHechizo>(celdaObjetivo, SH)
        }
        return null
    }

    private fun mejorGlifoTrampa(): Duo<Celda, StatHechizo>? {
        if (!_lanzador.puedeJugar()) {
            return null
        }
        for (SH in hechizosLanzables()) {
            var tamaño = 0
            var trampa = 0
            var daño = false
            val filtroIA = getFiltroIA(SH, null, Accion.TRAMPEAR)
            if (filtroIA < 0) {
                continue
            }
            if (filtroIA == 0) {
                for (EH in SH.getEfectosNormales()) {
                    if (trampa == 3) {
                        break
                    }
                    when (EH.getEfectoID()) {
                        82, 85, 86, 87, 88, 89, 91, 92, 93, 94, 95, 96, 97, 98, 99, 512, 100, 275, 276, 277, 278, 279 -> trampa = 3
                        400, 401, 402 -> {
                            trampa = if (EH.getEfectoID() === 400) {
                                1
                            } else {
                                2
                            }
                            tamaño = Encriptador.getNumeroPorValorHash(EH.getZonaEfecto().charAt(1))
                            val sh: StatHechizo = Mundo.getHechizo(EH.getPrimerValor())
                                    .getStatsPorNivel(EH.getSegundoValor())
                            for (eh in sh.getEfectosNormales()) {
                                daño = Constantes.estimaDaño(eh.getEfectoID()) === 1
                            }
                        }
                    }
                }
                if (trampa == 3 || trampa == 0) {
                    continue
                }
            }
            var distancia = 10000
            val objetivos: ArrayList<Luchador> = ArrayList()
            objetivos.addAll(ordenLuchadores(
                    if (daño) _lanzador.getParamEquipoEnemigo() else _lanzador.getParamEquipoAliado(), Orden.NADA))
            var celdaObjetivo: Celda? = null
            for (celda in Camino.celdasPosibleLanzamiento(SH, _lanzador, _pelea.getMapaCopia(),
                    _lanzador.getCeldaPelea().getID(), -1.toShort())) {
                if (trampa == 1) {
                    if (celda.esTrampa()) {
                        continue
                    }
                } else if (trampa == 2) {
                    if (celda.esGlifo()) {
                        continue
                    }
                }
                for (objetivo in objetivos) {
                    val dist: Int = Camino.distanciaDosCeldas(_pelea.getMapaCopia(), celda.getID(),
                            objetivo.getCeldaPelea().getID())
                    if (dist - tamaño > 3) {
                        continue
                    }
                    if (dist - tamaño < distancia) {
                        celdaObjetivo = celda
                        distancia = dist
                        if (dist == 0) {
                            break
                        }
                    }
                }
            }
            if (celdaObjetivo == null) {
                continue
            }
            if (MainServidor.MODO_DEBUG) {
                System.out.println("mejorGlifoTrampa() Hechizo: " + SH.getHechizo().getNombre().toString() + " ("
                        + SH.getHechizoID().toString() + ") Celda: " + celdaObjetivo.getID())
            }
            return Duo<Celda, StatHechizo>(celdaObjetivo, SH)
        }
        return null
    }

    private fun mejorTeleport(objetivo: Luchador?): Duo<Celda, StatHechizo>? {
        if (!_lanzador.puedeJugar()) {
            return null
        }
        if (objetivo == null) {
            return null
        }
        for (SH in hechizosLanzables()) {
            val filtroIA = getFiltroIA(SH, objetivo, Accion.TELEPORTAR)
            if (filtroIA < 0) {
                continue
            }
            if (filtroIA == 0) {
                var esTeleport = false
                var esDaño = false
                for (EH in SH.getEfectosNormales()) {
                    when (EH.getEfectoID()) {
                        4 -> esTeleport = true
                        82, 85, 86, 87, 88, 89, 91, 92, 93, 94, 95, 96, 97, 98, 99, 512, 100, 275, 276, 277, 278, 279 -> esDaño = true
                    }
                }
                if (!esTeleport || esDaño) {
                    continue
                }
            }
            var distancia = 1000
            var celdaObjetivo: Celda? = null
            val celdas: ArrayList<Celda> = Camino.celdasPosibleLanzamiento(SH, _lanzador, _pelea.getMapaCopia(),
                    _lanzador.getCeldaPelea().getID(), -1.toShort())
            for (celda in celdas) {
                val dist: Int = Camino.distanciaDosCeldas(_pelea.getMapaCopia(), celda.getID(),
                        objetivo.getCeldaPelea().getID())
                if (dist < distancia) {
                    celdaObjetivo = celda
                    distancia = dist
                }
            }
            if (celdaObjetivo == null) {
                continue
            }
            if (MainServidor.MODO_DEBUG) {
                System.out.println("mejorTeleport() Hechizo: " + SH.getHechizo().getNombre().toString() + " (" + SH.getHechizoID()
                        .toString() + ") Celda: " + celdaObjetivo.getID())
            }
            return Duo<Celda, StatHechizo>(celdaObjetivo, SH)
        }
        return null
    }

    private fun calculaInfluenciaBuff(mapa: Mapa, SH: StatHechizo?, celdaLanzamientoID: Short,
                                      celdaLanzadorID: Short, filtroIA: Int): Int {
        if (SH == null) {
            return -1
        }
        var obligarUsar = false
        var influenciaTotal = 0
        // int suerte = 0, suerteMax = 0, azar = 0;
        // boolean tiene666 = false;
        var efectos: ArrayList<EfectoHechizo?> = SH.getEfectosNormales()
        if (SH.getEfectosCriticos() != null && !SH.getEfectosCriticos().isEmpty()) {
            efectos = SH.getEfectosCriticos()
        }
        var matanza = false
        if (filtroIA == 0) {
            var retorna: Byte = 0
            for (EH in efectos) {
                // if (EH.getEfectoID() == 666)
                // tiene666 = true;
                // suerteMax += EH.getSuerte();
                when (EH.getEfectoID()) {
                    108, 81 -> if (retorna.toInt() == 0) {
                        retorna = 1
                    }
                    4, 5, 6, 82, 85, 86, 87, 88, 89, 91, 92, 93, 94, 95, 96, 97, 98, 99, 512, 100, 180, 181, 185, 275, 276, 277, 278, 279, 780 -> return -1
                    141, 405 -> {
                        matanza = true
                        retorna = 3
                    }
                    else -> retorna = 3
                }
            }
            if (retorna < 2) {
                return -1
            }
        }
        // if (suerteMax > 0) {
        // azar = Formulas.getRandomValor(1, suerteMax);
        // }
        for (EH in efectos) {
            val listaLuchadores: ArrayList<Luchador> = Hechizo.getObjetivosEfecto(mapa, _lanzador, EH, celdaLanzamientoID)
            when (EH.getEfectoID()) {
                141, 405 -> {
                    matanza = true
                    if (listaLuchadores.isEmpty()) {
                        return -1
                    }
                }
            }
            val max: Int = EH.getValorParaPromediar()
            for (objetivo in listaLuchadores) {
                if (!HechizoLanzado.puedeLanzPorObjetivo(_lanzador, objetivo.getID(), SH)) {
                    continue
                }
                if (objetivo.estaMuerto()) {
                    continue
                }
                var influencia = 0
                if (_influencias.containsKey(objetivo) && _influencias.get(objetivo).containsKey(EH)) {
                    influencia = _influencias.get(objetivo).get(EH)
                } else {
                    influencia = Constantes.getInflBuffPorEfecto(EH.getEfectoID(), _lanzador, objetivo, max,
                            celdaLanzamientoID, SH)
                    if (!_influencias.containsKey(objetivo)) {
                        _influencias.put(objetivo, HashMap<EfectoHechizo, Integer>())
                    }
                    _influencias.get(objetivo).put(EH, influencia)
                }
                if (influencia == 0) {
                    continue
                }
                if (EH.getSuerte() === 0) {
                    if (!objetivo.esInvocacion()) {
                        if (influencia > 0) {
                            influencia += 1000
                        } else if (influencia < 0) {
                            influencia -= 1000
                        }
                    }
                }
                when (EH.getEfectoID()) {
                    77, 84, 266, 267, 268, 269, 270 -> {
                        if (matanza) {
                            influenciaTotal += influencia * max
                            break
                        }
                        var preTotal = influencia * max
                        if (_lanzador.getEquipoBin() === objetivo.getEquipoBin()) {
                            preTotal = -preTotal
                            if (influencia < 0) {
                                preTotal += objetivo.getNivel()
                            }
                        }
                        if (preTotal > 0) {
                            obligarUsar = true
                        }
                        influenciaTotal += preTotal
                    }
                    else -> {
                        var preTotal = influencia * max
                        if (_lanzador.getEquipoBin() === objetivo.getEquipoBin()) {
                            preTotal = -preTotal
                            if (influencia < 0) {
                                preTotal += objetivo.getNivel()
                            }
                        }
                        if (preTotal > 0) {
                            obligarUsar = true
                        }
                        influenciaTotal += preTotal
                    }
                }
            }
        }
        if (filtroIA == 1 && obligarUsar && influenciaTotal <= 0) {
            influenciaTotal = 1
        }
        return influenciaTotal
    }

    private fun calculaInfluenciaCura(mapa: Mapa, SH: StatHechizo?, celdaLanzamientoID: Short,
                                      celdaLanzadorID: Short, filtroIA: Int): Int {
        if (SH == null) {
            return -1
        }
        var obligarUsar = false
        var influenciaTotal = 0
        // int suerte = 0, suerteMax = 0, azar = 0;
        // boolean tiene666 = false;
        var efectos: ArrayList<EfectoHechizo?> = SH.getEfectosNormales()
        if (SH.getEfectosCriticos() != null && !SH.getEfectosCriticos().isEmpty()) {
            efectos = SH.getEfectosCriticos()
        }
        if (filtroIA == 0) {
            var retorna: Byte = 0
            for (EH in efectos) {
                // if (EH.getEfectoID() == 666)
                // tiene666 = true;
                // suerteMax += EH.getSuerte();
                when (EH.getEfectoID()) {
                    108, 81 -> retorna = 3
                    4, 5, 6, 82, 85, 86, 87, 88, 89, 91, 92, 93, 94, 95, 96, 97, 98, 99, 512, 100, 180, 181, 185, 275, 276, 277, 278, 279, 780 -> return -1
                    141, 405 -> {
                    }
                    else -> {
                    }
                }
            }
            if (retorna < 2) {
                return -1
            }
        }
        // if (suerteMax > 0) {
        // azar = Formulas.getRandomValor(1, suerteMax);
        // }
        for (EH in efectos) {
            val listaLuchadores: ArrayList<Luchador> = Hechizo.getObjetivosEfecto(mapa, _lanzador, EH, celdaLanzamientoID)
            when (EH.getEfectoID()) {
                141, 405 -> if (listaLuchadores.isEmpty()) {
                    return -1
                }
            }
            val max: Int = EH.getValorParaPromediar()
            for (objetivo in listaLuchadores) {
                if (!HechizoLanzado.puedeLanzPorObjetivo(_lanzador, objetivo.getID(), SH)) {
                    continue
                }
                if (objetivo.estaMuerto()) {
                    continue
                }
                var influencia = 0
                if (_influencias.containsKey(objetivo) && _influencias.get(objetivo).containsKey(EH)) {
                    influencia = _influencias.get(objetivo).get(EH)
                } else {
                    influencia = Constantes.getInflDañoPorEfecto(EH.getEfectoID(), _lanzador, objetivo, max,
                            celdaLanzamientoID, SH)
                    if (!_influencias.containsKey(objetivo)) {
                        _influencias.put(objetivo, HashMap())
                    }
                    _influencias.get(objetivo).put(EH, influencia)
                }
                if (influencia == 0) {
                    continue
                }
                if (EH.getSuerte() === 0) {
                    if (!objetivo.esInvocacion()) {
                        if (influencia > 0) {
                            influencia += 1000
                        } else if (influencia < 0) {
                            influencia -= 1000
                        }
                    }
                }
                var preTotal = influencia * max
                if (_lanzador.getEquipoBin() === objetivo.getEquipoBin()) {
                    preTotal = -preTotal
                }
                if (preTotal > 0) {
                    obligarUsar = true
                }
                influenciaTotal += preTotal
            }
        }
        if (filtroIA == 1 && obligarUsar && influenciaTotal <= 0) {
            influenciaTotal = 1
        }
        return influenciaTotal
    }

    private fun calculaInfluenciaDaño(mapa: Mapa, SH: StatHechizo?, celdaLanzamientoID: Short,
                                      celdaLanzadorID: Short, filtroIA: Int): Int {
        if (SH == null) {
            return -1
        }
        var obligarUsar = false
        var influenciaTotal = 0
        // int suerte = 0, suerteMax = 0, azar = 0;
        // boolean tiene666 = false;
        var efectos: ArrayList<EfectoHechizo?> = SH.getEfectosNormales()
        if (SH.getEfectosCriticos() != null && !SH.getEfectosCriticos().isEmpty()) {
            efectos = SH.getEfectosCriticos()
        }
        var matanza = false
        var retorna: Byte = 0
        if (filtroIA == 0) {
            for (EH in efectos) {
                // if (EH.getEfectoID() == 666)
                // tiene666 = true;
                // suerteMax += EH.getSuerte();
                when (EH.getEfectoID()) {
                    4 -> continue
                    180, 181, 185, 780 ->                         // System.out.println("calculaInf efectos -1");
                        return -1
                    5, 6, 8, 77, 82, 84, 85, 86, 87, 88, 89, 91, 92, 93, 94, 95, 96, 97, 98, 99, 512, 100, 101, 116, 127, 131, 132, 140, 145, 152, 153, 154, 155, 156, 157, 162, 163, 168, 169, 215, 216, 217, 218, 219, 266, 267, 268, 269, 270, 271, 275, 276, 277, 278, 279 -> retorna = 3
                    141, 405 -> {
                        matanza = true
                        retorna = 3
                    }
                }
            }
            if (retorna < 2) {
                return -1
            }
            retorna = 0
        }
        // if (suerteMax > 0) {
        // azar = Formulas.getRandomValor(1, suerteMax);
        // }
        for (EH in efectos) {
            val listaLuchadores: ArrayList<Luchador> = Hechizo.getObjetivosEfecto(mapa, _lanzador, EH, celdaLanzamientoID)
            var esDaño = false
            when (EH.getEfectoID()) {
                4 -> continue
                141, 405 -> {
                    if (listaLuchadores.isEmpty()) {
                        return -1
                    }
                    esDaño = true
                }
                5, 6, 8, 77, 82, 84, 85, 86, 87, 88, 89, 91, 92, 93, 94, 95, 96, 97, 98, 99, 512, 100, 101, 116, 127, 131, 132, 140, 145, 152, 153, 154, 155, 156, 157, 162, 163, 168, 169, 215, 216, 217, 218, 219, 266, 267, 268, 269, 270, 271, 275, 276, 277, 278, 279 -> esDaño = true
            }
            val max: Int = EH.getValorParaPromediar()
            for (objetivo in listaLuchadores) {
                if (!HechizoLanzado.puedeLanzPorObjetivo(_lanzador, objetivo.getID(), SH)) {
                    continue
                }
                if (objetivo.estaMuerto()) {
                    continue
                }
                var influencia = 0
                if (_influencias.containsKey(objetivo) && _influencias.get(objetivo).containsKey(EH)) {
                    influencia = _influencias.get(objetivo).get(EH)
                } else {
                    influencia = Constantes.getInflDañoPorEfecto(EH.getEfectoID(), _lanzador, objetivo, max,
                            celdaLanzamientoID, SH)
                    if (!_influencias.containsKey(objetivo)) {
                        _influencias.put(objetivo, HashMap())
                    }
                    _influencias.get(objetivo).put(EH, influencia)
                }
                if (influencia == 0) {
                    continue
                }
                if (EH.getSuerte() === 0) {
                    if (!objetivo.esInvocacion()) {
                        if (influencia > 0) {
                            influencia += 1000
                        } else if (influencia < 0) {
                            influencia -= 1000
                        }
                    }
                }
                when (EH.getEfectoID()) {
                    77, 84, 266, 267, 268, 269, 270 -> {
                        if (matanza) {
                            influenciaTotal += influencia * max
                            break
                        }
                        var preTotal = influencia * max
                        if (_lanzador.esIAChafer() || _lanzador.getEquipoBin() !== objetivo.getEquipoBin()) {
                            if (esDaño && influencia > 0) {
                                retorna = 3
                            }
                        } else {
                            preTotal = -preTotal
                        }
                        if (preTotal > 0) {
                            obligarUsar = true
                        }
                        influenciaTotal += preTotal
                    }
                    else -> {
                        var preTotal = influencia * max
                        if (_lanzador.esIAChafer() || _lanzador.getEquipoBin() !== objetivo.getEquipoBin()) {
                            if (esDaño && influencia > 0) {
                                retorna = 3
                            }
                        } else {
                            preTotal = -preTotal
                        }
                        if (preTotal > 0) {
                            obligarUsar = true
                        }
                        influenciaTotal += preTotal
                    }
                }
                break
            }
        }
        if (filtroIA == 1 && obligarUsar && influenciaTotal <= 0) {
            influenciaTotal = 1
        } else if (retorna < 2) {
            return -1
        }
        return influenciaTotal
    }

    @Override
    fun run() {
        // TODO Auto-generated method stub
    }

    companion object {
        private const val PDV_MINIMO_CURAR = 99
        private const val INTELIGENCIA_COMPARAR_INF_OBJETIVOS = 9
        private const val INTELIGENCIA_COMPARAR_INF_DIST_OBJETIVOS = 11
        private const val INTELIGENCIA_ORDENAR_PRIORIDAD_OBJETIVOS = 5
        private const val INTELIGENCIA_SOLO_CELDAS_CON_LUCHADOR = 7
        private fun estaDentroObjetivos(objetivo: Luchador?, objetivos: ArrayList<Luchador>, celda: Celda): Boolean {
            if (objetivo != null) {
                if (!objetivos.contains(objetivo)) {
                    return false
                }
                if (MainServidor.NIVEL_INTELIGENCIA_ARTIFICIAL <= INTELIGENCIA_SOLO_CELDAS_CON_LUCHADOR) {
                    if (objetivo.getCeldaPelea() !== celda) {
                        return false
                    }
                }
            }
            return true
        }

        fun tieneReenvio(lanzador: Luchador, objetivo: Luchador?, SH: StatHechizo): Boolean {
            if (objetivo == null) {
                return false
            }
            if (objetivo === lanzador) {
                return false
            }
            return if (objetivo.getValorPorBuffsID(106) >= SH.getGrado()) {
                true
            } else false
        }

        private fun ordena(lista: ArrayList<Luchador>?, vararg orden: Orden) {
            for (o in orden) {
                if (o == Orden.PDV_MENOS_A_MAS) {
                    lista.sort(CompPDVMenosMas())
                }
                if (o == Orden.PDV_MAS_A_MENOS) {
                    lista.sort(CompPDVMasMenos())
                }
                if (o == Orden.NIVEL_MENOS_A_MAS) {
                    lista.sort(CompNivelMenosMas())
                }
                if (o == Orden.NIVEL_MAS_A_MENOS) {
                    lista.sort(CompNivelMasMenos())
                }
                if (o == Orden.INVOS_PRIMEROS) {
                    lista.sort(CompInvosPrimeros())
                }
                if (o == Orden.INVOS_ULTIMOS) {
                    lista.sort(CompInvosUltimos())
                }
            }
        }

        private fun esInvulnerable(objetivo: Luchador?): Boolean {
            if (objetivo == null) {
                return false
            }
            val stats: TotalStats = objetivo.getTotalStats()
            return if (stats.getTotalStatParaMostrar(Constantes.STAT_REDUCCION_FISICA, objetivo.getPelea(), null) > 100 || stats.getTotalStatParaMostrar(Constantes.STAT_REDUCCION_MAGICA, objetivo.getPelea(), null) > 100 || stats.getTotalStatParaMostrar(Constantes.STAT_MAS_DAÑOS_REDUCIDOS_NO_FECA,
                            objetivo.getPelea(), null) > 100) true else false
        }
    }

    init {
        _lanzador = atacante
        _pelea = p
        _pelea.addIA(this)
        _t = Thread(this)
        _t.setDaemon(true)
        _t.start()
    }
}