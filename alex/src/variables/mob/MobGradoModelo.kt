package variables.mob

import java.util.Map
import java.util.TreeMap
import java.util.Map.Entry
import variables.hechizo.Hechizo
import variables.hechizo.StatHechizo
import variables.pelea.Luchador
import variables.stats.Stats
import estaticos.Constantes
import estaticos.MainServidor
import estaticos.Mundo

class MobGradoModelo(modelo: MobModelo, grado: Byte, PA: Int, PM: Int, resist: String,
                     stats: String, hechizos: String, pdvMax: Int, iniciativa: Int, exp: Long, minKamas: Int,
                     maxKamas: Int) {
    val grado: Byte
    var nivel: Short = 0
    var pDVMAX: Int
    var minKamas: Long
    var maxKamas: Long
    var baseXp: Long
    val resistencias: String
    var spells: String? = null
    private val _mobModelo: MobModelo
    val iniciativa: Int
    private val _stats: Stats = Stats()
    private val _hechizos: Map<Integer, StatHechizo> = TreeMap<Integer, StatHechizo>()
    fun setHechizos(hechizos: String) {
        val aHechizo: Array<String> = hechizos.split(";")
        for (str in aHechizo) {
            if (str.isEmpty()) {
                continue
            }
            val hechizoInfo: Array<String> = str.split("@")
            var hechizoID = 0
            var hechizoNivel = 0
            try {
                hechizoID = Integer.parseInt(hechizoInfo[0])
                hechizoNivel = Integer.parseInt(hechizoInfo[1])
            } catch (e: Exception) {
                continue
            }
            if (hechizoID <= 0 || hechizoNivel <= 0) {
                continue
            }
            val hechizo: Hechizo = Mundo.getHechizo(hechizoID) ?: continue
            val hechizoStats: StatHechizo = hechizo.getStatsPorNivel(hechizoNivel) ?: continue
            _hechizos.put(hechizoID, hechizoStats)
        }
    }

    fun invocarMob(id: Int, clon: Boolean, invocador: Luchador?): MobGrado {
        var invocador: Luchador? = invocador
        val copia = MobGrado(this, _mobModelo, grado, nivel, pDVMAX, _stats)
        if (clon) {
            copia.getStats().fijarStatID(Constantes.STAT_MAS_CRIATURAS_INVO, 0)
            if (invocador != null) {
                copia.setPDVMAX(invocador.getPDVMaxSinBuff())
                copia.setPDV(invocador.getPDVSinBuff())
            }
        } else if (invocador != null) {
            var coefStats = 1f
            var coefVita = 1f
            val stats: Stats = copia.getStats()
            while (invocador.esInvocacion()) {
                invocador = invocador.getInvocador()
                // coefStats -= 0.3f;
                stats.fijarStatID(Constantes.STAT_MAS_CRIATURAS_INVO, 0)
            }
            if (invocador.getPersonaje() != null) {
                coefVita += invocador.getNivel() / 100f
                coefStats += invocador.getNivel() / 100f
            }
            for (i in MainServidor.STATS_HEREDADOS_INVOS) {
                var stat = (stats.getStatParaMostrar(i) * coefStats) as Int
                if (invocador.getPersonaje() != null) {
                    if (MainServidor.STATS_POR_NIVEL.size() > 0) {
                        if (MainServidor.STATS_POR_NIVEL.containsKey(grado)) {
                            stat = invocador.getPersonaje().getStatsObjEquipados().getStatParaMostrar(i) * MainServidor.STATS_POR_NIVEL.get(grado) / 100
                        }
                    }
                    if (MainServidor.MOB_AUMENTO_RESET > 0) {
                        if (invocador.getPersonaje() != null) {
                            val reset: Int = invocador.getPersonaje().getResets()
                            stat += stat * (MainServidor.MOB_AUMENTO_RESET * reset) / 100
                        }
                    }
                }
                if (MainServidor.LIMITE_STATS_PVP_PERSONALIZADOS && MainServidor.LIMITE_STATS_PVP_INVO.containsKey(i) && invocador.getPelea() != null && invocador.getPelea().getTipoPelea() === Constantes.PELEA_TIPO_PVP) {
                    if (MainServidor.LIMITE_STATS_PVP_INVO.get(i) < stat) {
                        stat = MainServidor.LIMITE_STATS_PVP_INVO.get(i)
                    }
                }
                if (MainServidor.LIMITE_STATS_KOLI_PERSONALIZADOS && MainServidor.LIMITE_STATS_INVO.containsKey(i) && invocador.getPelea() != null && invocador.getPelea().getTipoPelea() === Constantes.PELEA_TIPO_KOLISEO) {
                    if (MainServidor.LIMITE_STATS_INVO.get(i) < stat) {
                        stat = MainServidor.LIMITE_STATS_INVO.get(i)
                    }
                }
                stats.fijarStatID(i, stat)
            }
            copia.setPDVMAX((pDVMAX * coefVita).toInt()) // + Math.sqrt(invocador.getPDVMaxSinBuff())
            if (invocador.getPersonaje() != null) {
                if (MainServidor.MOB_AUMENTO_RESET > 0) {
                    val reset: Int = invocador.getPersonaje().getResets()
                    var PDVMAX: Int = copia.getPDVMax()
                    PDVMAX += PDVMAX * (MainServidor.MOB_AUMENTO_RESET * reset) / 390
                    copia.setPDVMAX(PDVMAX)
                }
                /*if(MainServidor.STATS_POR_NIVEL.size()>0) {
					if(MainServidor.STATS_POR_NIVEL.containsKey(_grado)) {
						copia.setPDVMAX(invocador.getPersonaje().getPDVMax()*50/100);
					}*
				}*/
            }
            if (MainServidor.LIMITE_STATS_PVP_PERSONALIZADOS && invocador.getPelea() != null && invocador.getPelea().getTipoPelea() === Constantes.PELEA_TIPO_PVP) {
                if (MainServidor.LIMITE_VIDA_INVO_OSA > 0 && invocador.getPersonaje() != null && invocador.getPersonaje().getClaseID(true) === Constantes.CLASE_OSAMODAS) {
                    copia.setPDVMAX(Math.min(copia.getPDVMax(), MainServidor.LIMITE_VIDA_INVO_OSA))
                } else if (MainServidor.LIMITE_VIDA_INVO > 0) {
                    copia.setPDVMAX(Math.min(copia.getPDVMax(), MainServidor.LIMITE_VIDA_INVO))
                }
            }
            if (MainServidor.LIMITE_STATS_KOLI_PERSONALIZADOS && invocador.getPelea() != null && invocador.getPelea().getTipoPelea() === Constantes.PELEA_TIPO_KOLISEO) {
                if (MainServidor.LIMITE_VIDA_INVO_KOLI_OSA > 0 && invocador.getPersonaje() != null && invocador.getPersonaje().getClaseID(true) === Constantes.CLASE_OSAMODAS) {
                    copia.setPDVMAX(Math.min(copia.getPDVMax(), MainServidor.LIMITE_VIDA_INVO_KOLI_OSA))
                } else if (MainServidor.LIMITE_VIDA_INVO_KOLI > 0) {
                    copia.setPDVMAX(Math.min(copia.getPDVMax(), MainServidor.LIMITE_VIDA_INVO_KOLI))
                }
            }
            copia.setPDV(copia.getPDVMax())
        }
        copia.setIDPersonal(id)
        return copia
    }

    fun stringStatsActualizado(): String {
        val strStats = StringBuilder()
        for (entry in _stats.getEntrySet()) {
            when (entry.getKey()) {
                Constantes.STAT_MAS_PA, Constantes.STAT_MAS_PM, Constantes.STAT_MAS_CRIATURAS_INVO, Constantes.STAT_MAS_SUERTE, Constantes.STAT_MAS_AGILIDAD, Constantes.STAT_MAS_FUERZA, Constantes.STAT_MAS_INTELIGENCIA, Constantes.STAT_MAS_INICIATIVA -> {
                    if (strStats.length() > 0) {
                        strStats.append(",")
                    }
                    strStats.append(entry.getKey().toString() + ":" + entry.getValue())
                }
            }
        }
        return strStats.toString()
    }

    val stats: Stats
        get() = _stats
    val pA: Int
        get() = _stats.getStatParaMostrar(Constantes.STAT_MAS_PA)
    val pM: Int
        get() = _stats.getStatParaMostrar(Constantes.STAT_MAS_PM)
    val hechizos: Map<Any, Any>
        get() = _hechizos
    val mobModelo: MobModelo
        get() = _mobModelo
    val iDModelo: Int
        get() = _mobModelo.getID()
    val gfxID: Short
        get() = _mobModelo.getGfxID()

    companion object {
        private val ORDEN_RESISTENCIAS = intArrayOf(Constantes.STAT_MAS_RES_PORC_NEUTRAL, Constantes.STAT_MAS_RES_PORC_TIERRA,
                Constantes.STAT_MAS_RES_PORC_FUEGO, Constantes.STAT_MAS_RES_PORC_AGUA, Constantes.STAT_MAS_RES_PORC_AIRE,
                Constantes.STAT_MAS_ESQUIVA_PERD_PA, Constantes.STAT_MAS_ESQUIVA_PERD_PM)
    }

    init {
        _mobModelo = modelo
        this.grado = grado
        pDVMAX = pdvMax
        baseXp = exp
        resistencias = resist
        if (!hechizos.equals("-1")) {
            spells = hechizos
        }
        this.iniciativa = iniciativa
        this.minKamas = minKamas.toLong()
        this.maxKamas = maxKamas.toLong()
        val mapStats: Map<Integer, Integer?> = TreeMap<Integer, Integer>()
        mapStats.put(Constantes.STAT_MAS_PA, PA)
        mapStats.put(Constantes.STAT_MAS_PM, PM)
        var i = -1
        for (sValor in resist.split(",")) {
            try {
                if (sValor.isEmpty()) {
                    continue
                }
                when (i) {
                    -1 -> nivel = Short.parseShort(sValor)
                    else -> mapStats.put(ORDEN_RESISTENCIAS[i], Integer.parseInt(sValor))
                }
                i++
            } catch (e: Exception) {
            }
        }
        // STATS
        i = 0
        for (s in stats.split(",")) {
            try {
                if (s.isEmpty()) {
                    continue
                }
                if (s.contains(":")) {
                    val s2: Array<String> = s.split(":")
                    mapStats.put(Integer.parseInt(s2[0]), Integer.parseInt(s2[1]))
                } else {
                    i++
                    var idStat = 0
                    idStat = when (i) {
                        1 -> Constantes.STAT_MAS_FUERZA
                        2 -> Constantes.STAT_MAS_INTELIGENCIA
                        3 -> Constantes.STAT_MAS_SUERTE
                        4 -> Constantes.STAT_MAS_AGILIDAD
                        else -> continue
                    }
                    mapStats.put(idStat, Integer.parseInt(s))
                }
            } catch (e: Exception) {
            }
        }
        if (mapStats[Constantes.STAT_MAS_CRIATURAS_INVO] == null) {
            mapStats.put(Constantes.STAT_MAS_CRIATURAS_INVO, 1)
        }
        if (mapStats[Constantes.STAT_MAS_INICIATIVA] == null) {
            mapStats.put(Constantes.STAT_MAS_INICIATIVA, iniciativa)
        }
        _stats.nuevosStats(mapStats)
        setHechizos(hechizos)
    }
}