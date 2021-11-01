package variables.personaje

import java.util.ArrayList
import java.util.HashMap
import java.util.Map
import java.util.Map.Entry
import estaticos.Constantes

class Clase(val iD: Int, gfxs: String, tallas: String, val mapaInicio: Short, val celdaInicio: Short, PDV: Int, boostVitalidad: String,
            boostSabiduria: String, boostFuerza: String, boostInteligencia: String, boostAgilidad: String, boostSuerte: String,
            stats: String, hechizos: String, _craneo: Int) {
    var pDV = 50
    private val _craneo: Int
    private val _gfxs: ArrayList<Integer> = ArrayList<Integer>(3)
    private val _tallas: ArrayList<Integer> = ArrayList<Integer>(3)
    private val _boostFuerza: ArrayList<BoostStat> = ArrayList()
    private val _boostInteligencia: ArrayList<BoostStat> = ArrayList()
    private val _boostVitalidad: ArrayList<BoostStat> = ArrayList()
    private val _boostSabiduria: ArrayList<BoostStat> = ArrayList()
    private val _boostAgilidad: ArrayList<BoostStat> = ArrayList()
    private val _boostSuerte: ArrayList<BoostStat> = ArrayList()
    private val _stats: Map<Integer, Integer> = HashMap()
    private val _hechizos: Map<Integer, Integer> = HashMap()
    private fun addBoostStat(sBoost: String, boost: ArrayList<BoostStat>) {
        for (s in sBoost.split("\\|")) {
            try {
                val ss: Array<String> = s.split(",")
                val inicio: Int = Integer.parseInt(ss[0])
                val coste: Int = Integer.parseInt(ss[1])
                var puntos = 1
                try {
                    puntos = Integer.parseInt(ss[2])
                } catch (e: Exception) {
                }
                boost.add(BoostStat(inicio, coste, puntos))
            } catch (e: Exception) {
            }
        }
    }

    fun getBoostStat(statID: Int, valorStat: Int): BoostStat {
        val boosts: ArrayList<BoostStat>
        boosts = when (statID) {
            Constantes.STAT_MAS_VITALIDAD -> _boostVitalidad
            Constantes.STAT_MAS_FUERZA -> _boostFuerza
            Constantes.STAT_MAS_INTELIGENCIA -> _boostInteligencia
            Constantes.STAT_MAS_AGILIDAD -> _boostAgilidad
            Constantes.STAT_MAS_SUERTE -> _boostSuerte
            else -> _boostSabiduria
        }
        var boost = BoostStat.BoostDefecto
        var temp = -1
        for (b in boosts) {
            if (b._inicio <= valorStat && b._inicio > temp) {
                temp = b._inicio
                boost = b
            }
        }
        return boost
    }

    fun aprenderHechizo(perso: Personaje, nivel: Int) {
        for (entry in _hechizos.entrySet()) {
            if (entry.getValue() === nivel) {
                perso.fijarNivelHechizoOAprender(entry.getKey(), 1, false)
            }
        }
    }

    val stats: Map<Any, Any>
        get() = _stats

    fun getGfxs(index: Int): Int {
        return try {
            _gfxs.get(index)
        } catch (e: Exception) {
            iD * 10 + 3
        }
    }

    fun getTallas(index: Int): Int {
        return try {
            _tallas.get(index)
        } catch (e: Exception) {
            100
        }
    }

    class BoostStat(private val _inicio: Int, val coste: Int, val puntos: Int) {

        companion object {
            var BoostDefecto = BoostStat(0, 1, 1)
        }
    }

    fun get_craneo(): Int {
        return _craneo
    }

    init {
        pDV = PDV
        for (s in gfxs.split(",")) {
            try {
                _gfxs.add(Integer.parseInt(s))
            } catch (e: Exception) {
            }
        }
        for (s in tallas.split(",")) {
            try {
                _tallas.add(Integer.parseInt(s))
            } catch (e: Exception) {
            }
        }
        addBoostStat(boostVitalidad, _boostVitalidad)
        addBoostStat(boostSabiduria, _boostSabiduria)
        addBoostStat(boostFuerza, _boostFuerza)
        addBoostStat(boostInteligencia, _boostInteligencia)
        addBoostStat(boostAgilidad, _boostAgilidad)
        addBoostStat(boostSuerte, _boostSuerte)
        for (s in stats.split("\\|")) {
            try {
                _stats.put(Integer.parseInt(s.split(",").get(0)), Integer.parseInt(s.split(",").get(1)))
            } catch (e: Exception) {
            }
        }
        for (s in hechizos.split("\\|")) {
            try {
                _hechizos.put(Integer.parseInt(s.split(",").get(1)), Integer.parseInt(s.split(",").get(0)))
            } catch (e: Exception) {
            }
        }
        this._craneo = _craneo
    }
}