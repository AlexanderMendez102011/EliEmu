package variables.mob

import java.util.ArrayList
import java.util.Map
import java.util.TreeMap
import java.util.regex.Pattern
import variables.hechizo.Hechizo
import variables.hechizo.StatHechizo
import variables.pelea.DropMob
import estaticos.Constantes
import estaticos.Formulas
import estaticos.GestorSQL
import estaticos.Mundo

class MobModelo(val iD: Int, val nombre: String, val gfxID: Short, val alineacion: Byte, var colores: String,
                grados: String, hechizos: String, stats: String, pdvs: String, puntos: String,
                strIniciativa: String, mK: String?, MK: String?, exps: String, var tipoIA: Byte,
                private val _esCapturable: Boolean, var talla: Short, var distAgresion: Byte, val tipoMob: Byte,
                private val _esKickeable: Boolean, private val _sombrero: Int, private val _capa: Int, private val _escudo: Int, private val _mascota: Int) {
    enum class TipoGrupo {
        FIJO, NORMAL, SOLO_UNA_PELEA, HASTA_QUE_MUERA
    }

    var archiMob: MobModelo? = null
    var probabilidadAparecer = -1
        private set
    private val _subAreasAparecer: ArrayList<Integer> = ArrayList()
    private val _grados: Map<Byte, MobGradoModelo> = TreeMap<Byte, MobGradoModelo>()
    private val _drops: ArrayList<DropMob> = ArrayList<DropMob>()
    fun setDataExtra(probabilidad: Int, subareas: String) {
        probabilidadAparecer = probabilidad
        for (s in subareas.split(",")) {
            if (s.isEmpty()) {
                continue
            }
            try {
                _subAreasAparecer.add(Integer.parseInt(s))
            } catch (e: Exception) {
            }
        }
    }

    fun puedeSubArea(subarea: Int): Boolean {
        return _subAreasAparecer.isEmpty() || _subAreasAparecer.contains(subarea)
    }

    fun modificarStats(grado: Byte, packet: String): Boolean {
        try {
            val mob: MobGradoModelo = _grados[grado] ?: return false
            val split: Array<String> = packet.split(Pattern.quote("|"))
            val stats: Array<String> = split[0].split(",")
            for (i in stats.indices) {
                try {
                    val a: Array<String> = stats[i].split(":")
                    mob.getStats().fijarStatID(Integer.parseInt(a[0]), Integer.parseInt(a[1]))
                } catch (e: Exception) {
                }
            }
            mob.setPDVMAX(Integer.parseInt(split[1]))
            mob.setBaseXp(Long.parseLong(split[2]))
            mob.setMinKamas(Long.parseLong(split[3]))
            mob.setMaxKamas(Long.parseLong(split[4]))
            val s: Array<String> = strStatsTodosMobs().split("~")
            GestorSQL.UPDATE_STATS_PUNTOS_PDV_XP_MOB(iD, s[0], s[1], s[2], split[3], split[4])
            return true
        } catch (e: Exception) {
        }
        return false
    }

    fun strStatsTodosMobs(): String {
        // se usa mas q todo para el panel mobs, pero minkamas y maxkamas son int para sql
        val strStats = StringBuilder()
        val strPDV = StringBuilder()
        val strExp = StringBuilder()
        val strMinKamas = StringBuilder()
        val strMaxKamas = StringBuilder()
        var e = false
        for (i in 1.._grados.size()) {
            val mob: MobGradoModelo = _grados[i.toByte()] ?: break
            if (e) {
                strStats.append("|")
                strPDV.append("|")
                strExp.append("|")
                strMinKamas.append("|")
                strMaxKamas.append("|")
            }
            strStats.append(mob.stringStatsActualizado())
            strPDV.append(mob.getPDVMAX())
            strExp.append(mob.getBaseXp())
            strMinKamas.append(mob.getMinKamas())
            strMaxKamas.append(mob.getMaxKamas())
            e = true
        }
        return (strStats.toString().toString() + "~" + strPDV.toString() + "~" + strExp.toString() + "~" + strMinKamas.toString() + "~"
                + strMaxKamas.toString())
    }

    // public String testDaño(byte grado, String s) {
    // MobGradoModelo mg = getGradoPorGrado(grado);
    // int[] stats = getStatsParaCalculo(grado, s);
    // if (stats == null) {
    // return "MOB NO EXISTE";
    // }
    // StringBuilder str = new StringBuilder("");
    // for (StatHechizo sh : mg.getHechizos().values()) {
    // if (str.length() > 0) {
    // str.append("|");
    // }
    // str.append(Hechizo.strDañosStats2(sh, stats));
    // }
    // return str.toString();
    // }
    fun calculoDaño(grado: Byte, s: String): String {
        val mg: MobGradoModelo? = getGradoPorGrado(grado)
        val stats = getStatsParaCalculo(grado, s) ?: return ""
        val str = StringBuilder()
        // str.append("\nCalculo de daño del mob " + _nombre + ":");
        var paso = false
        for (sh in mg.getHechizos().values()) {
            if (paso) {
                str.append("|")
            }
            paso = true
            str.append(Hechizo.strDañosStats(sh, stats))
        }
        return str.toString()
    }

    fun getStatsParaCalculo(grado: Byte, s: String): IntArray? {
        val mg: MobGradoModelo = getGradoPorGrado(grado) ?: return null
        val stats = IntArray(5)
        try {
            for (s2 in s.split(";")) {
                val statID: Int = Integer.parseInt(s2.split(",").get(0))
                val valor: Int = Integer.parseInt(s2.split(",").get(1))
                when (statID) {
                    Constantes.STAT_MAS_FUERZA -> {
                        stats[1] = valor
                        stats[0] = stats[1]
                    }
                    Constantes.STAT_MAS_INTELIGENCIA -> stats[2] = valor
                    Constantes.STAT_MAS_SUERTE -> stats[3] = valor
                    Constantes.STAT_MAS_AGILIDAD -> stats[4] = valor
                }
            }
        } catch (e: Exception) {
            stats[0] = mg.getStats().getStatParaMostrar(Constantes.STAT_MAS_FUERZA)
            stats[1] = mg.getStats().getStatParaMostrar(Constantes.STAT_MAS_FUERZA)
            stats[2] = mg.getStats().getStatParaMostrar(Constantes.STAT_MAS_INTELIGENCIA)
            stats[3] = mg.getStats().getStatParaMostrar(Constantes.STAT_MAS_SUERTE)
            stats[4] = mg.getStats().getStatParaMostrar(Constantes.STAT_MAS_AGILIDAD)
        }
        return stats
    }

    fun detalleMob(): String {
        val str = StringBuilder()
        str.append("$tipoMob|")
        var str2 = StringBuilder()
        for (drop in _drops) {
            if (str2.length() > 0) {
                str2.append(";")
            }
            str2.append(drop.getIDObjModelo().toString() + "," + drop.getProspeccion() + "#" + drop.getPorcentaje() * 1000 + "#"
                    + drop.getMaximo())
        }
        str.append(str2.toString().toString() + "|")
        str2 = StringBuilder()
        for (mob in _grados.values()) {
            if (str2.length() > 0) {
                str2.append("|")
            }
            str2.append(mob.getPDVMAX().toString() + "~" + mob.getPA() + "~" + mob.getPM() + "~" + mob.getResistencias() + "~" + mob
                    .getSpells().replace(";", ",") + "~" + mob.getBaseXp())
            str2.append("~" + mob.getMinKamas().toString() + " - " + mob.getMaxKamas())
        }
        str.append(str2.toString())
        return str.toString()
    }

    fun listaNiveles(): String {
        val str = StringBuilder()
        for (mob in _grados.values()) {
            if (str.length() > 0) {
                str.append(", ")
            }
            str.append(mob.getNivel())
        }
        return str.toString()
    }

    fun esKickeable(): Boolean {
        return _esKickeable
    }

    fun addDrop(drop: DropMob) {
        borrarDrop(drop.getIDObjModelo())
        Mundo.getObjetoModelo(drop.getIDObjModelo()).addMobQueDropea(iD)
        _drops.add(drop)
        _drops.trimToSize()
    }

    fun borrarDrop(id: Int) {
        var remove: DropMob? = null
        for (d in _drops) {
            if (d.getIDObjModelo() === id) {
                remove = d
                break
            }
        }
        if (remove != null) {
            Mundo.getObjetoModelo(id).delMobQueDropea(iD)
            _drops.remove(remove)
        }
    }

    val drops: ArrayList<DropMob>
        get() = _drops
    val grados: Map<Byte, Any>
        get() = _grados

    fun getGradoPorNivel(nivel: Int): MobGradoModelo? {
        for (grado in _grados.values()) {
            if (grado.getNivel() === nivel) {
                return grado
            }
        }
        return null
    }

    fun getGradoPorGrado(pos: Byte): MobGradoModelo? {
        return _grados[pos]
    }

    val randomGrado: MobGradoModelo?
        get() = _grados[Formulas.getRandomInt(1, _grados.size())]

    fun esCapturable(): Boolean {
        return _esCapturable
    }

    //System.out.println("Creo multi acc");
    val stringAccesorios: // arma
    // sombrero
    // capa
    // mascota
    // escudo
            String
        get() {
            //System.out.println("Creo multi acc");
            val str = StringBuilder()
            str.append("0,") // arma
            str.append(Integer.toHexString(_sombrero).toString() + ",") // sombrero
            str.append(Integer.toHexString(_capa).toString() + ",") // capa
            str.append(Integer.toHexString(_mascota).toString() + ",") // mascota
            str.append(Integer.toHexString(_escudo)) // escudo
            return str.toString()
        }

    init {
        val aGrados: Array<String> = grados.split(Pattern.quote("|"))
        val aStats: Array<String> = stats.split(Pattern.quote("|"))
        val aHechizos: Array<String> = hechizos.split(Pattern.quote("|"))
        val aPuntos: Array<String> = puntos.split(Pattern.quote("|"))
        val aExp: Array<String> = exps.split(Pattern.quote("|"))
        val aPDV: Array<String> = pdvs.split(Pattern.quote("|"))
        val aIniciativa: Array<String> = strIniciativa.split(Pattern.quote("|"))
        //	final String[] aMinKamas = mK.split(Pattern.quote("|"));
        //final String[] aMaxKamas = MK.split(Pattern.quote("|"));
        var grado: Byte = 1
        var PA: Byte = 6
        var PM: Byte = 3
        var tempPDV = 1
        var tempIniciativa = 0
        var tempMinKamas = 0
        var tempMaxKamas = 0
        var tempExp: Long = 0
        var tempHechizo = ""
        var tempResistNivel = ""
        var tempStats = ""
        for (n in aGrados.indices) {
            tempResistNivel = try {
                aGrados[n].split("@").get(1)
            } catch (e: Exception) {
                continue
            }
            if (tempResistNivel.isEmpty()) {
                continue
            }
            try {
                tempExp = Long.parseLong(aExp[n])
            } catch (e: Exception) {
            }
            try {
                tempStats = aStats[n]
            } catch (e: Exception) {
            }
            try {
                tempHechizo = aHechizos[n]
            } catch (e: Exception) {
            }
            try {
                tempPDV = Integer.parseInt(aPDV[n])
            } catch (e: Exception) {
            }
            try {
                tempIniciativa = Integer.parseInt(aIniciativa[n])
            } catch (e: Exception) {
            }
            try {
                PA = Byte.parseByte(aPuntos[n].split(";").get(0))
            } catch (e: Exception) {
            }
            try {
                PM = Byte.parseByte(aPuntos[n].split(";").get(1))
            } catch (e: Exception) {
            }
            try {
                tempMinKamas = Integer.parseInt(mK)
            } catch (e: Exception) {
            }
            try {
                tempMaxKamas = Integer.parseInt(MK)
            } catch (e: Exception) {
            }
            _grados.put(grado, MobGradoModelo(this, grado, PA, PM, tempResistNivel, tempStats, tempHechizo, tempPDV,
                    tempIniciativa, tempExp, tempMinKamas, tempMaxKamas))
            grado++
        }
        if (!stats.isEmpty() && !stats.contains(":")) {
            val strStats = StringBuilder()
            var e = false
            for (i in 1..11) {
                val mob: MobGradoModelo = _grados[i.toByte()] ?: break
                if (e) {
                    strStats.append("|")
                }
                strStats.append(mob.stringStatsActualizado())
                e = true
            }
            GestorSQL.UPDATE_STATS_MOB(iD, strStats.toString())
        }
    }
}