package variables.objeto

import java.util.ArrayList
import java.util.Calendar
import java.util.Map
import java.util.Map.Entry
import java.util.TreeMap
import variables.hechizo.EfectoHechizo
import variables.hechizo.StatHechizo
import variables.personaje.Personaje
import variables.stats.Stats
import variables.zotros.Accion
import estaticos.Constantes
import estaticos.Formulas
import estaticos.GestorSQL
import estaticos.GestorSalida
import estaticos.MainServidor
import estaticos.Mundo.Duo

class ObjetoModelo(val iD: Int, statsModelo: String?, val nombre: String, val tipo: Short, private var _nivel: Short,
                   val peso: Short, private var _kamas: Long, // es negativo// filtra si es statDePelea// HECHIZO ID = 0 (3 param)
                   val condiciones: String, infoArma: String, var vendidos: Int,
                   var precioPromedio: Long, private var _ogrinas: Long, private val _forjaMagueable: Boolean, private var _gfx: Int, var nivelModifi: Boolean,
                   private val _etereo: Boolean, private val _diasIntercambio: Int, val precioPanelOgrinas: Int, val precioPanelKamas: Int,
                   itemPago: String, private val _mimo: Boolean) {
    enum class CAPACIDAD_STATS {
        RANDOM, MINIMO, MAXIMO
    }

    private var _esDosManos = false
    var bonusGC: Byte = 0
    var costePA: Byte = 0
    var probabilidadGC: Short = 0
    var setID = 0
    private var _statHechizo: StatHechizo? = null
    private var _statsModelo: String? = null
    private val _accionesDeUso: Map<Integer, Accion> = TreeMap<Integer, Accion>()
    private val _efectosModelo: ArrayList<EfectoHechizo> = ArrayList<EfectoHechizo>()
    private val _statsIniciales: Map<Integer, Duo<Integer, Integer>?> = TreeMap<Integer, Duo<Integer, Integer>>()
    private var _itemPago: Duo<Integer, Integer>? = null
    private val _mobsQueDropean: ArrayList<Integer> = ArrayList()
    fun addMobQueDropea(id: Int) {
        if (!_mobsQueDropean.contains(id)) {
            _mobsQueDropean.add(id)
        }
    }

    val mobsQueDropean: ArrayList<Integer>
        get() = _mobsQueDropean

    fun delMobQueDropea(id: Int) {
        _mobsQueDropean.remove(id as Object)
    }

    fun esEtereo(): Boolean {
        return _etereo
    }

    fun esMimo(): Boolean {
        return _mimo
    }

    val statHechizo: StatHechizo?
        get() = _statHechizo
    var gFX: Int
        get() = _gfx
        set(gfx) {
            _gfx = gfx
            GestorSQL.UPDATE_GFX_OBJMODELO(iD, _gfx)
        }
    val statsIniciales: Map<Any, Any?>
        get() = _statsIniciales

    fun getDuoInicial(statID: Int): Duo<Integer, Integer>? {
        return _statsIniciales[statID]
    }

    fun tieneStatInicial(statID: Int): Boolean {
        return _statsIniciales[statID] != null
    }

    fun addAccion(accion: Accion) {
        _accionesDeUso.put(accion.getID(), accion)
    }

    val accion: Map<Any, Any>
        get() = _accionesDeUso

    fun cantAcciones(): Int {
        return _accionesDeUso.size()
    }

    fun borrarAcciones() {
        _accionesDeUso.clear()
    }

    fun esForjaMagueable(): Boolean {
        return _forjaMagueable
    }

    fun esDosManos(): Boolean {
        return _esDosManos
    }

    var ogrinas: Long
        get() = _ogrinas
        set(ogrinas) {
            _ogrinas = ogrinas
            GestorSQL.UPDATE_PRECIO_OBJETO_MODELO(iD, ogrinas, true)
        }
    val itemPago: Duo<Integer, Integer>?
        get() = _itemPago

    fun stringStatsModelo(): String? {
        return _statsModelo
    }

    var nivel: Short
        get() = _nivel
        set(nivel) {
            _nivel = nivel
            nivelModifi = true
            GestorSQL.UPDATE_NIVEL_OBJMODELO(iD, nivel)
        }
    var kamas: Long
        get() = _kamas
        set(kamas) {
            _kamas = kamas
            GestorSQL.UPDATE_PRECIO_OBJETO_MODELO(iD, kamas, false)
        }

    // aqui convierte los stats raros a stats normales, para q trabaje bien
    var statsModelo: String?
        get() = _statsModelo
        set(nuevosStats) {
            _statsModelo = nuevosStats
            _statsIniciales.clear()
            if (_statsModelo.isEmpty()) {
                return
            }
            for (stat in _statsModelo.split(",")) {
                if (stat.isEmpty()) {
                    continue
                }
                val stats: Array<String> = stat.split("#")
                var statID: Int = Integer.parseInt(stats[0], 16)
                if (statID != statSimiliar(statID)) {
                    // aqui convierte los stats raros a stats normales, para q trabaje bien
                    statID = statSimiliar(statID)
                    _statsModelo = _statsModelo.replaceFirst(stat, stat.replaceFirst(stats[0], Integer.toHexString(statID)))
                }
                var esEfecto = false
                for (a in Constantes.BUFF_ARMAS) {
                    if (a == statID) {
                        // HECHIZO ID = 0 (3 param)
                        val eh = EfectoHechizo(statID, stats[1] + "," + stats[2] + ",-1,0,0," + stats[4], 0, -1,
                                Constantes.getZonaEfectoArma(tipo))
                        eh.setAfectados(2)
                        _efectosModelo.add(eh)
                        esEfecto = true
                        break
                    }
                }
                if (esEfecto) {
                    continue
                }
                val statPositivo: Int = Constantes.getStatPositivoDeNegativo(statID)
                if (Constantes.esStatDePelea(statPositivo)) {
                    // filtra si es statDePelea
                    var min: Int = Integer.parseInt(stats[1], 16)
                    var max: Int = Integer.parseInt(stats[2], 16)
                    if (max <= 0) {
                        max = min
                    }
                    if (statPositivo != statID) {
                        // es negativo
                        min = -min
                        max = -max
                    }
                    val duo: Duo<Integer, Integer> = Duo<Integer, Integer>(Math.min(min, max), Math.max(min, max))
                    _statsIniciales.put(statPositivo, duo)
                }
            }
        }

    fun stringDeStatsParaTienda(ogrinas: Boolean): String {
        val str = StringBuilder()
        str.append("$iD;$_statsModelo")
        if (ogrinas && _ogrinas > 0) {
            if (_statsModelo!!.length() > 0) {
                str.append(",")
            }
            str.append(Integer.toHexString(Constantes.STAT_COLOR_NOMBRE_OBJETO).toString() + "#1")
            str.append(";")
            str.append(_ogrinas)
        } else {
            if (_itemPago != null) {
                // no pasa nada
            } else if (_ogrinas > 0) {
                if (_statsModelo!!.length() > 0) {
                    str.append(",")
                }
                str.append(Integer.toHexString(Constantes.STAT_COLOR_NOMBRE_OBJETO).toString() + "#1")
            }
            str.append(";")
            if (_itemPago != null) {
                str.append(_itemPago._segundo.toString() + ";" + _itemPago._primero)
            } else if (_ogrinas > 0) {
                str.append(_ogrinas)
            } else {
                str.append(_kamas)
            }
        }
        return str.toString()
    }

    fun aplicarAccion2(perso: Personaje, objetivo: Personaje?, objID: Int, celda: Short, cant: Short) {
        var cant = cant
        var b = false
        for (accion in _accionesDeUso.values()) {
            when (accion.getID()) {
                104, 5 -> if (accion.realizarAccion(perso, objetivo, objID, cant)) b = true
                else -> {
                    var i = 0
                    while (i < Math.abs(cant)) {
                        if (accion.realizarAccion(perso, objetivo, objID, celda)) {
                            b = true
                        } else {
                            cant = i.toShort()
                            break
                        }
                        i++
                    }
                }
            }
        }
        if (b) {
            perso.restarCantObjOEliminar(objID, Math.abs(cant), true)
            GestorSalida.ENVIAR_Im_INFORMACION(perso, "022;" + Math.abs(cant).toString() + "~" + iD)
        }
    }

    fun aplicarAccion(perso: Personaje, objetivo: Personaje?, objID: Int, celda: Short) {
        var b = false
        for (accion in _accionesDeUso.values()) {
            if (accion.realizarAccion(perso, objetivo, objID, celda)) b = true
        }
        if (b) {
            perso.restarCantObjOEliminar(objID, 1, true)
            GestorSalida.ENVIAR_Im_INFORMACION(perso, "022;" + 1 + "~" + iD)
        }
    }

    fun nuevoPrecio(cantidad: Long, precio: Long) {
        val viejaVenta = vendidos
        vendidos += cantidad.toInt()
        precioPromedio = (precioPromedio * viejaVenta + precio) / vendidos
        GestorSQL.UPDATE_PRECIO_MEDIO_OBJETO_MODELO(this)
    }

    fun crearObjeto(cantidad: Long, pos: Byte, capStats: CAPACIDAD_STATS): Objeto {
        // capStats => 0 = random, 1 = maximo, 2 = minimio
        var cantidad = cantidad
        var pos = pos
        if (cantidad < 1) {
            cantidad = 1
        }
        val stats = StringBuilder()
        // Calendar actual = Calendar.getInstance();
        // if (_ogrinas > 0) {
        // stats.append("325#" + Integer.toHexString(actual.get(1)) + "#"
        // + Integer.toHexString(actual.get(2) * 100 + actual.get(5)) + "#"
        // + Integer.toHexString(actual.get(11) * 100 + actual.get(12)) + ",");
        // }
        // stats.append("3d7#" + Integer.toHexString((actual.get(2) + 3) / 12 + actual.get(1)) + "#"
        // + Integer.toHexString(((actual.get(2) + 3) % 12) * 100 + actual.get(5)) + "#"
        // + Integer.toHexString(actual.get(11) * 100 + (actual.get(12)));
        if (_diasIntercambio > 0) {
            stats.append(Integer.toHexString(Constantes.STAT_INTERCAMBIABLE_DESDE).toString() + "#" + stringFechaIntercambiable(
                    _diasIntercambio))
        }
        if (tipo == Constantes.OBJETO_TIPO_OBJETO_MUTACION) { // objeto de mutacion
            pos = Constantes.OBJETO_POS_OBJ_MUTACION
        } else if (tipo == Constantes.OBJETO_TIPO_ALIMENTO_BOOST) { // alimento boost
            pos = Constantes.OBJETO_POS_BOOST
        } else if (tipo == Constantes.OBJETO_TIPO_BENDICION) { // maldicion
            pos = Constantes.OBJETO_POS_BENDICION
        } else if (tipo == Constantes.OBJETO_TIPO_MALDICION) { // bendicion
            pos = Constantes.OBJETO_POS_MALDICION
        } else if (tipo == Constantes.OBJETO_TIPO_ROLEPLAY_BUFF) { // role play
            pos = Constantes.OBJETO_POS_ROLEPLAY
        } else if (tipo == Constantes.OBJETO_TIPO_PJ_SEGUIDOR) { // personaje seguidor
            pos = Constantes.OBJETO_POS_PJ_SEGUIDOR
        }
        if (tipo == Constantes.OBJETO_TIPO_MASCOTA && MainServidor.PARAM_ALIMENTAR_MASCOTAS) { // mascotas
            if (stats.length() > 0) {
                stats.append(",")
            }
            stats.append("320#0#0#a")
            if (capStats == CAPACIDAD_STATS.MAXIMO) { // maximo stats
                if (stats.length() > 0) {
                    stats.append(",")
                }
                stats.append(generarStatsModelo(capStats))
            }
        } else if (tipo == Constantes.OBJETO_TIPO_CERTIFICADO_DE_LA_PETRERA
                || tipo == Constantes.OBJETO_TIPO_CERTIFICADO_DE_MONTURA) { // certificados
            // nada
        } else if (getTipoConStatsModelo(tipo.toInt())) {
            // pocima, perga exp, pan, golosina, pescado, carne
            if (stats.length() > 0) {
                stats.append(",")
            }
            stats.append(_statsModelo)
        } else {
            if (stats.length() > 0) {
                stats.append(",")
            }
            stats.append(generarStatsModelo(capStats))
        }
        return Objeto(0, iD, cantidad, pos, stats.toString(), 0, 0)
    }

    fun convertirStatsPerfecto(cantMod: Int, stats: Stats): Boolean {
        try {
            val tempStats: Map<Integer, Integer> = TreeMap<Integer, Integer>()
            for (entry in _statsIniciales.entrySet()) {
                val statID: Int = entry.getKey()
                val valor: Int = entry.getValue()._segundo
                if (stats.getStatParaMostrar(statID) < valor) {
                    tempStats.put(statID, valor)
                }
            }
            if (tempStats.isEmpty()) {
                return false
            }
            for (x in 1..cantMod) {
                if (tempStats.isEmpty()) {
                    break
                }
                val i = tempStats.keySet().toArray().get(Formulas.getRandomInt(0, tempStats.size() - 1)) as Int
                stats.fijarStatID(i, tempStats[i])
                tempStats.remove(i)
            }
            return true
        } catch (e: Exception) {
        }
        return false
    }

    fun generarStatsModelo(capStats: CAPACIDAD_STATS): String {
        val statsObjeto = StringBuilder()
        for (s in _statsModelo.split(",")) {
            try {
                if (s.isEmpty()) {
                    continue
                }
                val stats: Array<String> = s.split("#")
                val statID: Int = Integer.parseInt(stats[0], 16)
                if (statsObjeto.length() > 0) {
                    statsObjeto.append(",")
                }
                /*if(MainServidor.PARAMS_BORRAR_STATS_COLOR && statID == Constantes.STAT_COLOR_NOMBRE_OBJETO) {
					continue;
				}*/if (Constantes.STAT_RECIBIDO_EL === statID) {
                    val actual: Calendar = Calendar.getInstance()
                    statsObjeto.append(stats[0] + "#" + (Integer.toHexString(actual.get(Calendar.YEAR)).toString() + "#" + Integer
                            .toHexString(actual.get(Calendar.MONTH) * 100 + actual.get(Calendar.DAY_OF_MONTH)) + "#" + Integer
                            .toHexString(actual.get(Calendar.HOUR_OF_DAY) * 100 + actual.get(Calendar.MINUTE))))
                    continue
                }
                if (Constantes.esStatRepetible(statID) || Constantes.esStatTexto(statID) || Constantes.esStatHechizo(statID)
                        || statID == Constantes.STAT_RESISTENCIA) {
                    statsObjeto.append(s)
                    continue
                }
                if (statID == Constantes.STAT_TURNOS || statID == Constantes.STAT_PUNTOS_VIDA) {
                    statsObjeto.append(stats[0] + "#0#0#" + stats[3])
                    continue
                }
                var esEfecto = false
                for (a in Constantes.BUFF_ARMAS) {
                    if (a == statID) {
                        statsObjeto.append(stats[0] + "#" + stats[1] + "#" + stats[2] + "#0#" + stats[4])
                        esEfecto = true
                        break
                    }
                }
                if (esEfecto) {
                    continue
                }
                val esNegativo = Constantes.getStatPositivoDeNegativo(statID) !== statID
                var valor = 1
                var min = -1
                var max = -1
                try {
                    try {
                        min = Integer.parseInt(stats[1], 16)
                    } catch (e: Exception) {
                    }
                    try {
                        max = Integer.parseInt(stats[2], 16)
                    } catch (e: Exception) {
                    }
                    if (max <= 0) {
                        max = min
                    }
                    valor = if (capStats == CAPACIDAD_STATS.MAXIMO) {
                        // stas maximos
                        if (esNegativo) {
                            Math.min(min, max)
                        } else {
                            Math.max(min, max)
                        }
                    } else if (capStats == CAPACIDAD_STATS.MINIMO) {
                        // stats minimos
                        if (esNegativo) {
                            Math.max(min, max)
                        } else {
                            Math.min(min, max)
                        }
                    } else {
                        // random
                        Formulas.getRandomInt(min, max)
                    }
                    if (valor < 0) {
                        valor = 0
                    }
                } catch (e: Exception) {
                }
                statsObjeto.append(stats[0] + "#" + Integer.toHexString(valor) + "#0#" + stats[3] + "#0d0+" + valor)
            } catch (e: Exception) {
            }
        }
        return statsObjeto.toString()
    }

    companion object {
        fun statSimiliar(statID: Int): Int {
            when (statID) {
                Constantes.STAT_MAS_PA_2 -> return Constantes.STAT_MAS_PA
                Constantes.STAT_MAS_DAÑOS_2 -> return Constantes.STAT_MAS_DAÑOS
                Constantes.STAT_MAS_PM_2 -> return Constantes.STAT_MAS_PM
                Constantes.STAT_DAÑOS_DEVUELTOS -> return Constantes.STAT_REENVIA_DAÑOS
            }
            return statID
        }

        fun stringFechaIntercambiable(dias: Int): String {
            val actual: Calendar = Calendar.getInstance()
            actual.add(Calendar.DAY_OF_YEAR, dias)
            return getStatSegunFecha(actual)
        }

        fun getStatSegunFecha(actual: Calendar): String {
            val año: Int = actual.get(Calendar.YEAR)
            val mes: Int = actual.get(Calendar.MONTH)
            val dia_del_mes: Int = actual.get(Calendar.DAY_OF_MONTH)
            val hora_del_dia: Int = actual.get(Calendar.HOUR_OF_DAY)
            val minuto_de_hora: Int = actual.get(Calendar.MINUTE)
            return Integer.toHexString(año).toString() + "#" + Integer.toHexString(mes * 100 + dia_del_mes) + "#" + Integer.toHexString(
                    hora_del_dia * 100 + minuto_de_hora)
        }

        fun getTipoConStatsModelo(tipo: Int): Boolean {
            when (tipo) {
                Constantes.OBJETO_TIPO_POCION, Constantes.OBJETO_TIPO_PERGAMINO_EXP, Constantes.OBJETO_TIPO_PAN, Constantes.OBJETO_TIPO_GOLOSINA, Constantes.OBJETO_TIPO_PESCADO_COMESTIBLE, Constantes.OBJETO_TIPO_PIEDRA_DEL_ALMA, Constantes.OBJETO_TIPO_CARNE_COMESTIBLE, Constantes.OBJETO_TIPO_OBJETO_CRIA -> return true
            }
            return false
        }
    }

    init {
        statsModelo = statsModelo
        if (!_statsModelo.isEmpty()) {
            try {
                if (!infoArma.isEmpty()) {
                    val infos: Array<String> = infoArma.split(",")
                    bonusGC = Byte.parseByte(infos[0])
                    costePA = Byte.parseByte(infos[1])
                    val minAlc: Byte = Byte.parseByte(infos[2])
                    val maxAlc: Byte = Byte.parseByte(infos[3])
                    probabilidadGC = Short.parseShort(infos[4])
                    val porcFC: Short = Short.parseShort(infos[5])
                    val lanzarLinea: Boolean = infos[6].equalsIgnoreCase("true")
                    val lineaVista: Boolean = infos[7].equalsIgnoreCase("true")
                    _esDosManos = infos[8].equalsIgnoreCase("true")
                    _statHechizo = StatHechizo(0, 1, costePA, minAlc, maxAlc, probabilidadGC, porcFC, lanzarLinea, lineaVista,
                            false, false, 0.toByte(), 0.toByte(), 0.toByte(), 0.toByte(), true, "[18, 19, 1, 3, 41, 42]", "", -1.toByte(), false)
                }
            } catch (e: Exception) {
                MainServidor.redactarLogServidorln("Objeto Modelo " + iD + " tiene bug en infosArma")
                e.printStackTrace()
                System.exit(1)
            }
        }
        if (!itemPago.isEmpty()) {
            try {
                val idItemPago: Int = Integer.parseInt(itemPago.split(",").get(0))
                val cantItemPago: Int = Integer.parseInt(itemPago.split(",").get(1))
                _itemPago = Duo<Integer, Integer>(idItemPago, cantItemPago)
            } catch (e: Exception) {
            }
        }
    }
}