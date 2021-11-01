package variables.hechizo

import java.util.ArrayList
import variables.mapa.Celda
import variables.pelea.Luchador
import variables.pelea.Pelea
import estaticos.MainServidor
import estaticos.Mundo

class Buff(efectoID: Int, hechizoID: Int, desbufeable: Boolean, turnos: Int,
           lanzador: Luchador, args: String, tipo: TipoDaño) : EfectoHechizo(hechizoID) {
    private val _lanzador: Luchador
    var turnosRestantesOriginal: Int
        private set
    private var _desbuffeable: Boolean
    var condicionBuff = ""
        private set

    // por el momento las condiciones son
    // SOIN, BN,DN,DE,DA,DW,DF,-PA,-PM,PA,PM
    private var _tipo: TipoDaño? = null
    @Override
    fun setArgs(args: String) {
        _args = args
        val split: Array<String> = _args.split(",")
        try {
            _primerValor = Integer.parseInt(split[0]) // valor
        } catch (e: Exception) {
        }
        try {
            _segundoValor = Integer.parseInt(split[1]) // valor max
        } catch (e: Exception) {
        }
        try {
            _tercerValor = Integer.parseInt(split[2])
        } catch (e: Exception) {
        }
    }

    fun setCondBuff(condicion: String) {
        condicionBuff = condicion.toUpperCase()
    }

    fun getTurnosRestantes(puedeJugar: Boolean): Int {
        return if (!puedeJugar || turnosRestantesOriginal <= -1) {
            turnosRestantesOriginal
        } else turnosRestantesOriginal - 1
    }

    fun disminuirTurnosRestantes(): Int {
        if (turnosRestantesOriginal > 0) {
            turnosRestantesOriginal--
        }
        return turnosRestantesOriginal
    }

    fun esDesbufeable(): Boolean {
        return _desbuffeable
    }

    fun setDesbufeable(b: Boolean) {
        _desbuffeable = b
    }

    val lanzador: Luchador
        get() = _lanzador

    fun aplicarBuffDeInicioTurno(pelea: Pelea?, objetivo: Luchador) {
        try {
            val obj2: ArrayList<Luchador> = ArrayList<Luchador>()
            obj2.add(objetivo)
            when (_efectoID) {
                85, 86, 87, 88, 89 -> {
                    efecto_Daños_Porc_Elemental(obj2, pelea, _lanzador, _tipo, false)
                    return
                }
                91, 92, 93, 94, 95 -> {
                    efecto_Roba_PDV_Elemental(obj2, pelea, _lanzador, _tipo, false)
                    return
                }
                96, 97, 98, 99, 100 -> {
                    efecto_Daños_Elemental(obj2, pelea, _lanzador, _tipo, false)
                    return
                }
                81, 108 -> {
                    efecto_Cura(obj2, pelea, _lanzador, _tipo)
                    return
                }
                301 -> {
                    aplicarHechizoDeBuff(pelea, objetivo, objetivo.getCeldaPelea())
                    return
                }
                787 -> {
                    aplicarHechizoDeBuff(pelea, objetivo, objetivo.getCeldaPelea())
                    return
                }
            }
        } catch (e: Exception) {
            MainServidor.redactarLogServidorln("EXCEPTION BUFF INICIO, HECHIZO:" + _hechizoID.toString() + ", ARGS:" + _args)
            e.printStackTrace()
        }
    }

    fun aplicarBuffCondicional(objetivo: Luchador) {
        val objetivos: ArrayList<Luchador> = ArrayList<Luchador>()
        objetivos.add(objetivo)
        val c = condicionBuff
        val turnos: Int = _duracion
        condicionBuff = ""
        _duracion = 2
        aplicarEfecto(objetivo.getPelea(), objetivo, objetivos, objetivo.getCeldaPelea(), _tipo, false)
        condicionBuff = c
        _duracion = turnos
    }

    fun aplicarHechizoDeBuff(pelea: Pelea?, objetivo: Luchador?, celdaObjetivo: Celda?) {
        val hechizo: Hechizo = Mundo.getHechizo(_primerValor) ?: return
        val sh: StatHechizo = hechizo.getStatsPorNivel(_segundoValor) ?: return
        Hechizo.aplicaHechizoAPelea(pelea, objetivo, celdaObjetivo, sh.getEfectosNormales(), TipoDaño.NORMAL, false)
    }

    init {
        _efectoID = efectoID
        _desbuffeable = desbufeable
        turnosRestantesOriginal = if (turnos <= -1) -3 else turnos
        _duracion = 0
        _lanzador = lanzador
        _tipo = if (tipo === TipoDaño.GLIFO || tipo === TipoDaño.TRAMPA) {
            tipo
        } else {
            TipoDaño.POST_TURNOS
        }
        setArgs(args)
    }
}