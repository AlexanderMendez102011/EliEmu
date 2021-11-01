package variables.objeto

import java.util.ArrayList
import java.util.regex.Pattern
import estaticos.Mundo
import estaticos.Mundo.Duo

class MascotaModelo(val iD: Int, val maxStats: Int, statsPorEfecto: String, comidas: String,
                    devorador: Int, val fantasma: Int, statsPorEfectoMejorado: String, val maxStatsMejorado: Int, val tiempoAlimentacion: Int, val tiempoAlimentacionMaximo: Int) {
    class Comida(val iDComida: Int, val cantidad: Int, val iDStat: Int)

    private val _statsPorEfecto: ArrayList<Duo<Integer, Integer>> = ArrayList<Duo<Integer, Integer>>()
    private val _statsPorEfectoMejorado: ArrayList<Duo<Integer, Integer>> = ArrayList<Duo<Integer, Integer>>()
    private val _comidas: ArrayList<Comida> = ArrayList<Comida>()
    private val _esDevorador: Boolean
    var strComidas = ""
    fun getComida(idModComida: Int): Comida? {
        for (comi in _comidas) {
            if (comi.iDComida < 0) {
                if (Math.abs(comi.iDComida) === Mundo.getObjetoModelo(idModComida).getTipo()) {
                    return comi
                }
            } else {
                if (comi.iDComida == idModComida) {
                    return comi
                }
            }
        }
        return null
    }

    fun esDevoradorAlmas(): Boolean {
        return _esDevorador
    }

    fun getStatsPorEfectoMejorado(stat: Int): Int {
        for (duo in _statsPorEfectoMejorado) {
            if (duo._primero === stat) {
                return duo._segundo
            }
        }
        return 0
    }

    fun getStatsPorEfecto(stat: Int): Int {
        for (duo in _statsPorEfecto) {
            if (duo._primero === stat) {
                return duo._segundo
            }
        }
        return 0
    }

    init {
        if (!comidas.isEmpty()) {
            for (comida in comidas.split(Pattern.quote("|"))) {
                try {
                    val str: Array<String> = comida.split(";")
                    _comidas.add(Comida(Integer.parseInt(str[0]), Integer.parseInt(str[1]), Integer.parseInt(str[2])))
                } catch (e: Exception) {
                }
            }
        }
        strComidas = "comidas: $comidas statsPorEfecto: $statsPorEfecto maxStats: $maxStats"
        val stats: Array<String> = statsPorEfecto.split(Pattern.quote("|"))
        for (s in stats) {
            try {
                _statsPorEfecto.add(Duo<Integer, Integer>(Integer.parseInt(s.split(";").get(0)), Integer
                        .parseInt(s.split(";").get(1))))
            } catch (e: Exception) {
            }
        }
        val stats2: Array<String> = statsPorEfectoMejorado.split(Pattern.quote("|"))
        for (s in stats2) {
            try {
                _statsPorEfectoMejorado.add(Duo<Integer, Integer>(Integer.parseInt(s.split(";").get(0)), Integer
                        .parseInt(s.split(";").get(1))))
            } catch (e: Exception) {
            }
        }
        _esDevorador = devorador == 1
    }
}