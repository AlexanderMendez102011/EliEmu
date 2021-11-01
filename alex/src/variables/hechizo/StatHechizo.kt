package variables.hechizo

import java.util.ArrayList
import java.util.regex.Pattern
import estaticos.Constantes
import estaticos.Formulas
import estaticos.Inteligencia.Accion
import estaticos.MainServidor
import estaticos.Mundo
import variables.mapa.Mapa
import variables.pelea.Luchador

// private final ArrayList<Integer> _afectados = new ArrayList<Integer>();
class StatHechizo(hechizoID: Int, grado: Int, costePA: Byte, minAlc: Byte, maxAlc: Byte,
                  probGC: Short, probFC: Short, lanzarLinea: Boolean, lineaDeVista: Boolean, esCeldaVacia: Boolean,
                  esModifAlc: Boolean, maxLanzPorTurno: Byte, maxLanzPorObjetivo: Byte, sigLanzamiento: Byte,
                  reqLevel: Int, esFinTurnoSiFC: Boolean, estadosProhibidos: String, estadosNecesarios: String,
                  tipoHechizo: Byte, necesitaObjetivo: Boolean) {
    val grado // nivel
            : Int
    val costePA // coste de PA
            : Byte
    val minAlc // minimo alcance
            : Byte
    val maxAlc // maximo alcance
            : Byte
    val probabilidadGC // probabilidad de golpe critico
            : Short
    val probabilidadFC // probabilidad de fallo critico
            : Short
    private val _lanzarLinea // lanzar en linea
            : Boolean
    private val _lineaDeVista // linea de vuelo
            : Boolean
    private val _necesarioCeldaLibre // celda vacia
            : Boolean
    private val _alcModificable // alcance modificalble
            : Boolean
    private var _necesitaObjetivo // alcance modificalble
            = false
    val maxLanzPorTurno // cantidad de veces por turno
            : Byte
    val maxLanzPorObjetivo // cantidad de veces por objetivo
            : Byte
    val sigLanzamiento // cantidad de turnos para volver a lanzar el hechizo
            : Byte
    val nivelRequerido // nivel requerido
            : Int
    private val _esFinTurnoSiFC // si falla, es final del turno
            : Boolean
    val _efectosNormales: ArrayList<EfectoHechizo?> = ArrayList<EfectoHechizo>()
    private val _efectosCriticos: ArrayList<EfectoHechizo?>? = ArrayList<EfectoHechizo>()
    private val _ordenadoNormales: ArrayList<EfectoHechizo> = ArrayList<EfectoHechizo>()
    private val _ordenadoCriticos: ArrayList<EfectoHechizo> = ArrayList<EfectoHechizo>()

    // private final String _areaEfecto;// genera un estado, tipo portador
    private val _estadosProhibidos: ArrayList<Integer> = ArrayList<Integer>()
    private val _estadosNecesarios: ArrayList<Integer> = ArrayList<Integer>()
    private val _hechizo: Hechizo
    val tipo // 0 normal, 1 pergamino, 2 invocacion, 3 dominios, 4 de clase, 5
            : Byte
    var efectosID: ArrayList<Integer> = ArrayList<Integer>()

    // de recaudador
    private var _trampa = false
    private var _intercambioPos = false
    private var _soloMover = false
    private var _automaticoAlFinalTurno = false

    // public boolean esHechizoParaAliados() {
    // return _hechizo.getValorIA() != 2;
    // }
    //
    // public boolean esHechizoParaEnemigos() {
    // return _hechizo.getValorIA() != 1;
    // }
    fun filtroValorIA(tipo: Accion?, c: Char): Int {
        val valorIA: Int = _hechizo.getValorIA()
        if (valorIA == 0) {
            return 0
        }
        var v = 0
        when (tipo) {
            ATACAR -> v = 1
            BOOSTEAR -> v = 2
            CURAR -> v = 3
            TRAMPEAR -> v = 4
            INVOCAR -> v = 5
            TELEPORTAR -> v = 6
            NADA -> v = 0
        }
        if (Math.abs(valorIA) !== v) {
            return -1
        }
        if (c != ' ') {
            if (c == '+' && valorIA < 0) {
                return -1
            } else if (c == '-' && valorIA > 0) {
                return -1
            }
        }
        return 1
    }

    fun esTrampa(): Boolean {
        return _trampa
    }

    fun esSoloMover(): Boolean {
        return _soloMover
    }

    fun esIntercambioPos(): Boolean {
        return _intercambioPos
    }

    fun esAutomaticoAlFinalTurno(): Boolean {
        return _automaticoAlFinalTurno
    }

    private fun fijarEfectos(efectoID: Int) {
        if (efectoID == 8) {
            _intercambioPos = true
        }
        _soloMover = if (efectoID == 5 || efectoID == 6) {
            true
        } else {
            false
        }
        if (efectoID == 400) {
            _trampa = true
        }
        if (efectoID == 300) {
            _automaticoAlFinalTurno = true
        }
    }

    fun analizarEfectos(efectosN: String, efectosC: String, zonaEfecto: String, hechizoID: Int) {
        var num = 0
        var splt: ArrayList<String?> = Constantes.convertirStringArray(efectosN)
        for (a in splt) {
            try {
                if (a.equals("null") || a.isEmpty()) {
                    continue
                }
                a = a.replace('[', ' ').replace(']', ' ').replace(" ", "")
                val efectoID: Int = Integer.parseInt(a.split(",").get(0))
                val args: String = a.split(",", 2).get(1)
                val eh = EfectoHechizo(efectoID, args, hechizoID, grado, zonaEfecto.substring(num * 2, num * 2
                        + 2))
                _efectosNormales.add(eh)
                fijarEfectos(efectoID)
                num++
            } catch (e: Exception) {
                MainServidor.redactarLogServidorln("[BUG HECHIZO ID] $hechizoID : $efectosN")
                e.printStackTrace()
                System.exit(1)
                return
            }
        }
        _efectosNormales.trimToSize()
        splt = Constantes.convertirStringArray(efectosC)
        for (a in splt) {
            try {
                if (a.equals("null") || a.isEmpty()) {
                    continue
                }
                a = a.replace('[', ' ').replace(']', ' ').replace(" ", "")
                val efectoID: Int = Integer.parseInt(a.split(",").get(0))
                val args: String = a.split(",", 2).get(1)
                val eh = EfectoHechizo(efectoID, args, hechizoID, grado, zonaEfecto.substring(num * 2, num * 2
                        + 2))
                _efectosCriticos.add(eh)
                fijarEfectos(efectoID)
                num++
            } catch (e: Exception) {
                MainServidor.redactarLogServidorln("[BUG HECHIZO ID] $hechizoID : $efectosC")
                e.printStackTrace()
                System.exit(1)
                return
            }
        }
        _efectosCriticos.trimToSize()
        ordenar()
    }

    private fun setValorAfectado(eh: EfectoHechizo, normales: Array<String>, i: Int) {
        var afectado = 0
        var afectadoCond = 0
        if (i < normales.size && !normales[i].isEmpty()) {
            val s: Array<String> = normales[i].toUpperCase().split(Pattern.quote("*"))
            var a = ""
            if (s.size > 1) {
                a = s[1]
            }
            try {
                afectado = Integer.parseInt(s[0])
            } catch (e: Exception) {
                if (a.isEmpty()) {
                    a = s[0]
                }
            }
            try {
                if (a.contains("D_")) {
                    val ele: Array<String> = a.replace("D_", "").split("")
                    for (e in ele) {
                        when (e) {
                            "A" -> afectadoCond = afectadoCond or (1 shl Constantes.ELEMENTO_AIRE)
                            "W" -> afectadoCond = afectadoCond or (1 shl Constantes.ELEMENTO_AGUA)
                            "F" -> afectadoCond = afectadoCond or (1 shl Constantes.ELEMENTO_FUEGO)
                            "E" -> afectadoCond = afectadoCond or (1 shl Constantes.ELEMENTO_TIERRA)
                            "N" -> afectadoCond = afectadoCond or (1 shl Constantes.ELEMENTO_NEUTRAL)
                        }
                    }
                }
            } catch (e: Exception) {
            }
        }
        eh.setAfectados(afectado)
        eh.setAfectadosCond(afectadoCond)
    }

    fun setAfectados(normales: Array<String>, criticos: Array<String>) {
        for (i in 0 until _efectosNormales.size()) {
            val eh: EfectoHechizo = _efectosNormales.get(i)
            setValorAfectado(eh, normales, i)
        }
        for (i in 0 until _efectosCriticos.size()) {
            val eh: EfectoHechizo = _efectosCriticos.get(i)
            setValorAfectado(eh, criticos, i)
        }
    }

    fun setCondiciones(normales: Array<String>, criticos: Array<String>) {
        for (i in 0 until _efectosNormales.size()) {
            val eh: EfectoHechizo = _efectosNormales.get(i)
            if (i < normales.size && !normales[i].isEmpty()) {
                eh.setCondicion(normales[i])
            }
        }
        for (i in 0 until _efectosCriticos.size()) {
            val eh: EfectoHechizo = _efectosCriticos.get(i)
            if (i < criticos.size && !criticos[i].isEmpty()) {
                eh.setCondicion(criticos[i])
            }
        }
    }

    private fun ordenar() {
        _ordenadoNormales.clear()
        _ordenadoNormales.addAll(_efectosNormales)
        _ordenadoNormales.trimToSize()
        _ordenadoCriticos.clear()
        _ordenadoCriticos.addAll(_efectosCriticos)
        _ordenadoCriticos.trimToSize()
    }

    val hechizo: Hechizo
        get() = _hechizo
    val hechizoID: Int
        get() = _hechizo.getID()
    val spriteID: Int
        get() = _hechizo.getSpriteID()
    val spriteInfos: String
        get() = _hechizo.getSpriteInfos()
    val estadosProhibido: ArrayList<Integer>
        get() = _estadosProhibidos
    val estadosNecesario: ArrayList<Integer>
        get() = _estadosNecesarios

    fun esLanzarLinea(): Boolean {
        return _lanzarLinea
    }

    fun esLineaVista(): Boolean {
        return _lineaDeVista
    }

    fun esNecesarioCeldaLibre(): Boolean {
        return _necesarioCeldaLibre
    }

    fun esAlcanceModificable(): Boolean {
        return _alcModificable
    }

    fun esNecesarioObjetivo(): Boolean {
        return _necesitaObjetivo
    }

    fun esFinTurnoSiFC(): Boolean {
        return _esFinTurnoSiFC
    }

    val efectosNormales: ArrayList<EfectoHechizo>
        get() = _ordenadoNormales
    val efectosCriticos: ArrayList<EfectoHechizo>
        get() = _ordenadoCriticos

    fun beneficio(lanzador: Luchador, mapa: Mapa?, idCeldaObjetivo: Short, objetivo: Luchador?): Int {
        var efectos: ArrayList<EfectoHechizo?> = _efectosNormales
        if (_efectosCriticos != null && !_efectosCriticos.isEmpty()) {
            efectos = _efectosCriticos
        }
        var suerte = 0
        var suerteMax = 0
        var azar = 0
        var cantidad = 0
        var tiene666 = false
        var filtrarSuerte = false
        for (EH in efectos) {
            if (EH.getEfectoID() === 666 && EH.getSuerte() > 0) {
                tiene666 = true
            }
            if (EH.getSuerte() === 0) {
                filtrarSuerte = true
            }
            suerteMax += EH.getSuerte()
        }
        if (suerteMax > 0) {
            azar = Formulas.getRandomInt(1, suerteMax)
        }
        for (EH in efectos) {
            if (EH.getSuerte() > 0) {
                if (filtrarSuerte || tiene666) {
                    continue
                }
                if (azar < suerte || azar >= EH.getSuerte() + suerte) {
                    suerte += EH.getSuerte()
                    continue
                }
                suerte += EH.getSuerte()
            }
            val listaLuchadores: ArrayList<Luchador> = Hechizo.getObjetivosEfecto(mapa, lanzador, EH, idCeldaObjetivo)
            val estima: Int = Constantes.getInflDañoPorEfecto(EH.getEfectoID(), lanzador, objetivo, EH.getValorParaPromediar(),
                    idCeldaObjetivo, this)
            for (L in listaLuchadores) {
                if (estima > 0) {
                    if (L.getEquipoBin() !== lanzador.getEquipoBin()) {
                        cantidad++
                    } else {
                        cantidad--
                    }
                } else if (estima < 0) {
                    if (L.getEquipoBin() === lanzador.getEquipoBin()) {
                        cantidad++
                    } else {
                        cantidad--
                    }
                }
            }
        }
        return cantidad
    }

    fun listaObjetivosAfectados(lanzador: Luchador?, mapa: Mapa?,
                                celdaObjetivoID: Short): ArrayList<Luchador> {
        val objetivos: ArrayList<Luchador> = ArrayList()
        var efectos: ArrayList<EfectoHechizo?> = _efectosNormales
        if (_efectosCriticos != null && !_efectosCriticos.isEmpty()) {
            efectos = _efectosCriticos
        }
        var suerte = 0
        var suerteMax = 0
        var azar = 0
        var tiene666 = false
        var filtrarSuerte = false
        for (EH in efectos) {
            if (EH.getEfectoID() === 666 && EH.getSuerte() > 0) {
                tiene666 = true
            }
            if (EH.getSuerte() === 0) {
                filtrarSuerte = true
            }
            suerteMax += EH.getSuerte()
        }
        if (suerteMax > 0) {
            azar = Formulas.getRandomInt(1, suerteMax)
        }
        for (EH in efectos) {
            if (EH.getSuerte() > 0) {
                if (filtrarSuerte || tiene666) {
                    continue
                }
                if (azar < suerte || azar >= EH.getSuerte() + suerte) {
                    suerte += EH.getSuerte()
                    continue
                }
                suerte += EH.getSuerte()
            }
            val objs: ArrayList<Luchador> = Hechizo.getObjetivosEfecto(mapa, lanzador, EH, celdaObjetivoID)
            for (o in objs) {
                if (!objetivos.contains(o)) {
                    objetivos.add(o)
                }
            }
        }
        return objetivos
    }

    fun estaDentroAfectados(lanzador: Luchador?, objetivo: Luchador?, mapa: Mapa?,
                            celdaObjetivoID: Short): Boolean {
        var efectos: ArrayList<EfectoHechizo?> = _efectosNormales
        if (_efectosCriticos != null && !_efectosCriticos.isEmpty()) {
            efectos = _efectosCriticos
        }
        var suerte = 0
        var suerteMax = 0
        var azar = 0
        var tiene666 = false
        var filtrarSuerte = false
        for (EH in efectos) {
            if (EH.getEfectoID() === 666 && EH.getSuerte() > 0) {
                tiene666 = true
            }
            if (EH.getSuerte() === 0) {
                filtrarSuerte = true
            }
            suerteMax += EH.getSuerte()
        }
        if (suerteMax > 0) {
            azar = Formulas.getRandomInt(1, suerteMax)
        }
        for (EH in efectos) {
            if (EH.getSuerte() > 0) {
                if (filtrarSuerte || tiene666) {
                    continue
                }
                if (azar < suerte || azar >= EH.getSuerte() + suerte) {
                    suerte += EH.getSuerte()
                    continue
                }
                suerte += EH.getSuerte()
            }
            val objetivos: ArrayList<Luchador> = Hechizo.getObjetivosEfecto(mapa, lanzador, EH, celdaObjetivoID)
            if (objetivos.contains(objetivo)) {
                return true
            }
        }
        return false
    } /*
	private static class CompPrioridad implements Comparator<EfectoHechizo> {
		@Override
		public int compare(EfectoHechizo p1, EfectoHechizo p2) {
			if (Constantes.prioridadEfecto(p1.getEfectoID()) < Constantes.prioridadEfecto(p2.getEfectoID()))
				return -1;
			if (Constantes.prioridadEfecto(p1.getEfectoID()) > Constantes.prioridadEfecto(p2.getEfectoID()))
				return 1;
			return 0;
		}
	}*/

    init {
        var esCeldaVacia = esCeldaVacia
        this.grado = grado // nivel
        this.costePA = costePA // coste de PA
        this.minAlc = minAlc // minimo alcance
        this.maxAlc = maxAlc // maximo alcance
        probabilidadGC = probGC // tasa/probabilidad de golpe critico
        probabilidadFC = probFC // tasa/probabilidad de fallo critico
        _lanzarLinea = lanzarLinea // lanzado en linea
        _lineaDeVista = lineaDeVista // linea de vuelo
        if (necesitaObjetivo.also { _necesitaObjetivo = it }) {
            esCeldaVacia = false
        }
        _necesarioCeldaLibre = esCeldaVacia // celda libre
        _alcModificable = esModifAlc // alcance modificable
        this.maxLanzPorTurno = maxLanzPorTurno // cantidad de veces por turno
        this.maxLanzPorObjetivo = maxLanzPorObjetivo // cantidad de veces por objetivo
        this.sigLanzamiento = sigLanzamiento // cantidad de turnos para volver a lanzar el hechizo
        nivelRequerido = reqLevel // nivel requerido
        _esFinTurnoSiFC = esFinTurnoSiFC // si es fallo critico , final de turno
        tipo = tipoHechizo
        _hechizo = Mundo.getHechizo(hechizoID)
        if (!estadosProhibidos.isEmpty()) {
            val estados: ArrayList<String> = Constantes.convertirStringArray(estadosProhibidos)
            for (esta in estados) {
                if (esta.isEmpty()) {
                    continue
                }
                _estadosProhibidos.add(Integer.parseInt(esta))
            }
        }
        if (!estadosNecesarios.isEmpty()) {
            val estados: ArrayList<String> = Constantes.convertirStringArray(estadosNecesarios)
            for (esta in estados) {
                if (esta.isEmpty()) {
                    continue
                }
                _estadosNecesarios.add(Integer.parseInt(esta))
            }
        }
    }
}