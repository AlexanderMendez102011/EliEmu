package estaticos

import java.util.Map
import java.util.TreeMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.regex.Pattern
import com.singularsys.jep.Jep
import variables.mapa.Mapa
import variables.mision.Mision
import variables.mob.GrupoMob
import variables.mob.MobGradoModelo
import variables.objeto.Objeto
import variables.pelea.Luchador
import variables.pelea.Pelea
import variables.personaje.Personaje
import variables.stats.Stats
import variables.stats.TotalStats

object Condiciones {
    fun validaCondiciones(perso: Personaje?, condiciones: String?, pelea: Pelea?): Boolean {
        var condiciones = condiciones
        return try {
            if (perso == null) {
                return false
            }
            if (condiciones == null || condiciones.isEmpty() || condiciones.equals("BN") || condiciones.equals("-1")
                    || condiciones.equalsIgnoreCase("ALL")) {
                return true
            }
            val jep = Jep()
            for (s in splittear(condiciones)) {
                try {
                    if (s.isEmpty()) {
                        continue
                    }
                    val cond: String = s.substring(0, 2)
                    when (cond) {
                        "-1", "PX", "MK", "PN", "Pz", "DF" -> condiciones = condiciones.replaceFirst(Pattern.quote(s), "true")
                        "CL" -> condiciones = condiciones.replaceFirst(Pattern.quote(s), puedeEntrarEnPelea(s, perso, pelea))
                        "CQ" -> condiciones = condiciones.replaceFirst(Pattern.quote(s), puedeEntrarEnPelea2(s, perso, pelea))
                        "IP" -> condiciones = condiciones.replaceFirst(Pattern.quote(s), puedeIrMapa(s, perso))
                        "XT" -> condiciones = condiciones.replaceFirst(Pattern.quote(s), puedeIrMapa2(s, perso))
                        "XY" -> condiciones = condiciones.replaceFirst(Pattern.quote(s), puedeIrMapa3(s, perso))
                        "BI" -> condiciones = condiciones.replaceFirst(Pattern.quote(s), "false")
                        "Pj" -> condiciones = condiciones.replaceFirst(Pattern.quote(s), tieneOficio(s, perso))
                        "PJ" -> condiciones = condiciones.replaceFirst(Pattern.quote(s), tieneNivelOficio(s, perso))
                        "OR" -> condiciones = condiciones.replaceFirst(Pattern.quote(s), tieneObjetoRecibidoDespuesDeHoras(s, perso))
                        "DH" -> condiciones = condiciones.replaceFirst(Pattern.quote(s), tieneObjetoRecibidoDespuesDeHoras(s, perso))
                        "DM" -> condiciones = condiciones.replaceFirst(Pattern.quote(s), tieneObjetoRecibidoDespuesDeMinutos(s, perso))
                        "DS" -> condiciones = condiciones.replaceFirst(Pattern.quote(s), tieneObjetoRecibidoDespuesDeSegundos(s, perso))
                        "TE" -> condiciones = condiciones.replaceFirst(Pattern.quote(s), tieneEtapa(s, perso))
                        "TO" -> condiciones = condiciones.replaceFirst(Pattern.quote(s), tieneEstadoObjetivo(s, perso))
                        "TM" -> condiciones = condiciones.replaceFirst(Pattern.quote(s), tieneEstadoMision(s, perso))
                        "QO" -> condiciones = condiciones.replaceFirst(Pattern.quote(s), confirmarObjetivoMision(s, perso))
                        "QE" -> condiciones = condiciones.replaceFirst(Pattern.quote(s), confirmarEtapaMision(s, perso))
                        "SO" -> condiciones = condiciones.replaceFirst(Pattern.quote(s), tieneStatObjetoLlaveoAlma(s, perso))
                        "So" -> condiciones = condiciones.replaceFirst(Pattern.quote(s), tieneStatObjeto(s, perso))
                        "PO" -> condiciones = condiciones.replaceFirst(Pattern.quote(s), tieneObjModeloNoEquip(s, perso))
                        "EQ" -> condiciones = condiciones.replaceFirst(Pattern.quote(s), tieneObjModeloEquipado(s, perso))
                        "EP" -> condiciones = condiciones.replaceFirst(Pattern.quote(s), tieneItemPos(s, perso))
                        "Pg" -> condiciones = condiciones.replaceFirst(Pattern.quote(s), tieneDon(s, perso))
                        "XO" -> condiciones = condiciones.replaceFirst(Pattern.quote(s), celdasOcupadasPersonajesOtroMapa(s, perso))
                        "CO" -> condiciones = condiciones.replaceFirst(Pattern.quote(s), celdasOcupadasPersonajes(s, perso))
                        "Co" -> condiciones = condiciones.replaceFirst(Pattern.quote(s), celdasOcupadasMob(s, perso))
                        "Cr" -> condiciones = condiciones.replaceFirst(Pattern.quote(s), celdasObjetoTirado(s, perso))
                        "PH" -> condiciones = condiciones.replaceFirst(Pattern.quote(s), tieneHechizo(s, perso))
                        "FM" -> condiciones = condiciones.replaceFirst(Pattern.quote(s), tieneMobsEnPelea(s, perso))
                        "MM" -> condiciones = condiciones.replaceFirst(Pattern.quote(s), tieneMobsEnMapa(s, perso))
                        "Is" -> condiciones = condiciones.replaceFirst(Pattern.quote(s), tieneObjetosSet(s, perso))
                        "IS" -> condiciones = condiciones.replaceFirst(Pattern.quote(s), tieneCantObjetosSet(s, perso))
                        "BS" -> condiciones = condiciones.replaceFirst(Pattern.quote(s), tieneCantBonusSet(s, perso))
                        "CI", "CV", "CA", "CW", "CC", "CS", "CM", "CP" -> {
                            val totalStas: TotalStats = perso.getTotalStats()
                            jep.addVariable("CI", totalStas.getTotalStatParaMostrar(Constantes.STAT_MAS_INTELIGENCIA, null, null))
                            jep.addVariable("CV", totalStas.getTotalStatParaMostrar(Constantes.STAT_MAS_VITALIDAD, null, null))
                            jep.addVariable("CA", totalStas.getTotalStatParaMostrar(Constantes.STAT_MAS_AGILIDAD, null, null))
                            jep.addVariable("CW", totalStas.getTotalStatParaMostrar(Constantes.STAT_MAS_SABIDURIA, null, null))
                            jep.addVariable("CC", totalStas.getTotalStatParaMostrar(Constantes.STAT_MAS_SUERTE, null, null))
                            jep.addVariable("CS", totalStas.getTotalStatParaMostrar(Constantes.STAT_MAS_FUERZA, null, null))
                            jep.addVariable("CM", totalStas.getTotalStatParaMostrar(Constantes.STAT_MAS_PM, null, null))
                            jep.addVariable("CP", totalStas.getTotalStatParaMostrar(Constantes.STAT_MAS_PA, null, null))
                        }
                        "Ci", "Cv", "Ca", "Cw", "Cc", "Cs" -> {
                            val statsBase: Stats = perso.getTotalStats().getStatsBase()
                            jep.addVariable("Ci", statsBase.getStatParaMostrar(Constantes.STAT_MAS_INTELIGENCIA))
                            jep.addVariable("Cv", statsBase.getStatParaMostrar(Constantes.STAT_MAS_VITALIDAD))
                            jep.addVariable("Ca", statsBase.getStatParaMostrar(Constantes.STAT_MAS_AGILIDAD))
                            jep.addVariable("Cw", statsBase.getStatParaMostrar(Constantes.STAT_MAS_SABIDURIA))
                            jep.addVariable("Cc", statsBase.getStatParaMostrar(Constantes.STAT_MAS_SUERTE))
                            jep.addVariable("Cs", statsBase.getStatParaMostrar(Constantes.STAT_MAS_FUERZA))
                        }
                        "PQ" -> jep.addVariable(cond, perso.getPreguntaID())
                        "PG" -> jep.addVariable(cond, perso.getClaseID(true))
                        "PD" -> jep.addVariable(cond, perso.getDeshonor())
                        "PK" -> jep.addVariable(cond, perso.getKamas())
                        "PF" -> jep.addVariable(cond, if (perso.getPelea() == null) -1 else perso.getPelea().getTipoPelea())
                        "AD" -> jep.addVariable(cond, perso.getAdmin())
                        "FP" -> jep.addVariable(cond, if (perso.getPelea() == null) -1 else perso.getPelea().getProspeccionEquipo())
                        "Fp" -> jep.addVariable(cond, if (perso.getPelea() == null) -1 else perso.getPelea().getLuchadorPorID(perso.getID()).getProspeccionLuchador())
                        "PL" -> jep.addVariable(cond, perso.getNivel())
                        "PP" -> jep.addVariable(cond, perso.getGradoAlineacion())
                        "Ps" -> jep.addVariable(cond, perso.getAlineacion())
                        "Pa" -> jep.addVariable(cond, perso.getOrdenNivel())
                        "Pr" -> jep.addVariable(cond, perso.getEspecialidad())
                        "PS" -> jep.addVariable(cond, perso.getSexo())
                        "PW" -> jep.addVariable(cond, perso.alasActivadas())
                        "PM" -> jep.addVariable(cond, perso.getGfxID(false))
                        "PR" -> jep.addVariable(cond, perso.getEsposoID() !== 0)
                        "PC" -> jep.addVariable(cond, perso.puedeCasarse())
                        "PZ" -> jep.addVariable(cond, if (perso.esAbonado()) 1 else 0)
                        "PV" -> jep.addVariable(cond, perso.esAbonado())
                        "GL" -> jep.addVariable(cond, perso.getNivelGremio())
                        "MA" -> jep.addVariable(cond, perso.realizoMisionDelDia())
                        "Mi" -> jep.addVariable(cond, perso.getID())
                        "NR" -> jep.addVariable(cond, perso.getResets())
                        "NO" -> jep.addVariable(cond, perso.getNivelOmega())
                        "mK" -> jep.addVariable(cond, perso.getMapa().getID())
                        "mC" -> jep.addVariable(cond, perso.getCelda().getID())
                        "Tc" -> jep.addVariable(cond, (System.currentTimeMillis() - perso.getCelda().getUltimoUsoTrigger()) / 1000)
                    }
                } catch (e: Exception) {
                    MainServidor.redactarLogServidorln("EXCEPTION condicion: $s validaCondiones(splittear) " + e
                            .toString())
                    e.printStackTrace()
                }
            }
            condiciones = condiciones.replace("&", "&&").replace("=", "==").replace("|", "||").replace("!", "!=")
            jep.parse(condiciones)
            // System.out.println("jep condition: " + jep.rootNodeToString());
            val resultado: Object = jep.evaluate()
            var ok = false
            if (resultado != null) {
                ok = Boolean.valueOf(resultado.toString())
            }
            if (perso.esMultiman()) {
                ok = true
                return ok
            }
            ok
        } catch (e: Exception) {
            MainServidor.redactarLogServidorln("EXCEPTION Condiciones: $condiciones, validaCondiciones" + e
                    .toString())
            e.printStackTrace()
            false
        }
    }

    private fun splittear(cond: String?): Array<String> {
        return cond.replaceAll("[ ()]", "").split("[|&]")
        // FIXME los corchetes en un split, quiere decir q cada simbolo sera un split para el string
    }

    private fun tieneObjetosSet(s: String, perso: Personaje): String {
        try {
            val args: Array<String> = s.substring(3).split(",")
            val setID: Int = Integer.parseInt(args[0])
            val cant: Int = Integer.parseInt(args[1])
            val tiene: Int = perso.getNroObjEquipadosDeSet(setID)
            return tiene.toString() + "" + s.charAt(2) + "" + cant
        } catch (e: Exception) {
        }
        return "false"
    }

    private fun tieneCantBonusSet(s: String, perso: Personaje): String {
        try {
            val args: Array<String> = s.substring(3).split(",")
            val cant: Int = Integer.parseInt(args[0])
            val map: Map<Integer, Integer> = TreeMap()
            for (pos in Constantes.POSICIONES_EQUIPAMIENTO) {
                val obj: Objeto = perso.getObjPosicion(pos) ?: continue
                val setID: Int = obj.getObjModelo().getSetID()
                if (setID < 1) {
                    continue
                }
                var v = 1
                if (map.containsKey(setID)) {
                    v = map[setID] + 1
                }
                map.put(setID, v)
            }
            var tiene = 0
            for (v in map.values()) {
                tiene += v - 1
            }
            return tiene.toString() + "" + s.charAt(2) + "" + cant
        } catch (e: Exception) {
        }
        return "false"
    }

    private fun tieneCantObjetosSet(s: String, perso: Personaje): String {
        try {
            val args: Array<String> = s.substring(3).split(",")
            val cant: Int = Integer.parseInt(args[0])
            val map: Map<Integer, Integer> = TreeMap()
            for (pos in Constantes.POSICIONES_EQUIPAMIENTO) {
                val obj: Objeto = perso.getObjPosicion(pos) ?: continue
                val setID: Int = obj.getObjModelo().getSetID()
                if (setID < 1) {
                    continue
                }
                var v = 1
                if (map.containsKey(setID)) {
                    v = map[setID] + 1
                }
                map.put(setID, v)
            }
            var tiene = 0
            for (v in map.values()) {
                if (v > tiene) {
                    tiene = v
                }
            }
            return tiene.toString() + "" + s.charAt(2) + "" + cant
        } catch (e: Exception) {
        }
        return "false"
    }

    private fun tieneMobsEnMapa(s: String, perso: Personaje): String {
        var b = false
        try {
            val ss: Array<String> = s.substring(3).split(";")
            val args: Array<String> = ss[1].split(",")
            val mobID: Int = Integer.parseInt(args[0])
            var lvlMin = 0
            var lvlMax = 99999
            try {
                if (args.size > 1) {
                    lvlMin = Integer.parseInt(args[1])
                    lvlMax = Integer.parseInt(args[2])
                }
            } catch (e: Exception) {
            }
            for (m in ss[0].split(",")) {
                if (m.isEmpty()) {
                    continue
                }
                try {
                    val mapa: Mapa = Mundo.getMapa(Short.parseShort(m)) ?: continue
                    for (gm in mapa.getGrupoMobsTotales().values()) {
                        if (gm.tieneMobModeloID(mobID, lvlMin, lvlMax)) {
                            b = true
                            break
                        }
                    }
                } catch (e: Exception) {
                }
            }
            if (s.contains("!")) {
                b = !b
            }
        } catch (e: Exception) {
        }
        return b.toString() + ""
    }

    private fun tieneMobsEnPelea(s: String, perso: Personaje): String {
        var b = false
        try {
            if (perso.getPelea() != null) {
                val mobs: CopyOnWriteArrayList<MobGradoModelo> = CopyOnWriteArrayList()
                mobs.addAll(perso.getPelea().getMobGrupo().getMobs())
                val ss: Array<String> = s.substring(3).split(";")
                for (a in ss) {
                    val args: Array<String> = a.split(",")
                    val mobID: Int = Integer.parseInt(args[0])
                    var lvlMin = 0
                    var lvlMax = 99999
                    try {
                        if (args.size > 1) {
                            lvlMin = Integer.parseInt(args[1])
                            lvlMax = Integer.parseInt(args[2])
                        }
                    } catch (e: Exception) {
                    }
                    var tiene = false
                    for (gm in mobs) {
                        if (gm.getIDModelo() === mobID) {
                            if (gm.getNivel() >= lvlMin && gm.getNivel() <= lvlMax) {
                                mobs.remove(gm)
                                tiene = true
                                b = true
                                break
                            }
                        }
                    }
                    if (!tiene) {
                        b = false
                        break
                    }
                    if (s.contains("!")) {
                        b = !b
                    }
                }
            }
        } catch (e: Exception) {
        }
        return b.toString() + ""
    }

    private fun celdasObjetoTirado(s: String, perso: Personaje): String {
        var b = false
        try {
            val args: Array<String> = s.substring(3).split(",")
            val id: Int = Integer.parseInt(args[0])
            val objetoID: Int = Integer.parseInt(args[1])
            val obj: Objeto = perso.getMapa().getCelda(id.toShort()).getObjetoTirado()
            if (obj != null) {
                b = objetoID == obj.getObjModeloID()
            }
            if (s.contains("!")) {
                b = !b
            }
        } catch (e: Exception) {
        }
        return b.toString() + ""
    }

    private fun celdasOcupadasPersonajesOtroMapa(s: String, perso: Personaje): String {
        var b = false
        try {
            val args: Array<String> = s.substring(3).split(",")
            val mapaID: Int = Integer.parseInt(args[0])
            val celdaID: Int = Integer.parseInt(args[1])
            var clase: Byte = -1
            if (args.size > 2) {
                clase = Byte.parseByte(args[2])
            }
            var sexo: Byte = -1
            if (args.size > 2) {
                sexo = Byte.parseByte(args[3])
            }
            val p: Personaje = Mundo.getMapa(mapaID.toShort()).getCelda(celdaID.toShort()).getPrimerPersonaje()
            b = p != null
            if (b && sexo.toInt() != -1) {
                b = p.getSexo() === sexo
            }
            if (b && clase.toInt() != -1) {
                b = p.getClaseID(true) === clase
            }
            if (s.contains("!")) {
                b = !b
            }
        } catch (e: Exception) {
        }
        return b.toString() + ""
    }

    private fun celdasOcupadasPersonajes(s: String, perso: Personaje): String {
        var b = false
        try {
            val id: Int = Integer.parseInt(s.substring(3))
            b = perso.getMapa().getCelda(id.toShort()).getPrimerPersonaje() != null
            if (s.contains("!")) {
                b = !b
            }
        } catch (e: Exception) {
        }
        return b.toString() + ""
    }

    private fun celdasOcupadasMob(s: String, perso: Personaje): String {
        var b = false
        try {
            val args: Array<String> = s.substring(3).split(",")
            val celdaID: Int = Integer.parseInt(args[0])
            val mobID: Int = Integer.parseInt(args[1])
            for (gm in perso.getMapa().getGrupoMobsTotales().values()) {
                if (gm.getCeldaID() === celdaID) {
                    if (gm.tieneMobModeloID(mobID, 0, 99999)) {
                        b = true
                        break
                    }
                }
            }
            if (s.contains("!")) {
                b = !b
            }
        } catch (e: Exception) {
        }
        return b.toString() + ""
    }

    private fun tieneStatObjeto(s: String, perso: Personaje): String {
        var b = false
        try {
            val args: Array<String> = s.substring(3).split(",")
            val objetoID: Int = Integer.parseInt(args[0])
            val statVerificar: Int = Integer.parseInt(args[1])
            for (obj in perso.getObjetosTodos()) {
                if (obj.getObjModeloID() !== objetoID) {
                    continue
                }
                val stats: Array<String> = obj.convertirStatsAString(true).split(",")
                for (st in stats) {
                    val statID: Int = Integer.parseInt(st.split("#").get(0), 16)
                    if (statID == statVerificar) {
                        b = true
                    }
                }
                if (b) {
                    break
                }
            }
            if (s.contains("!")) {
                b = !b
            }
        } catch (e: Exception) {
        }
        return b.toString() + ""
    }

    private fun tieneStatObjetoLlaveoAlma(s: String, perso: Personaje): String {
        var b = false
        try {
            val args: Array<String> = s.substring(3).split(",")
            val objetoID: Int = Integer.parseInt(args[0])
            val solicitaID: Int = Integer.parseInt(args[1])
            var statVerificar: Int = Constantes.STAT_LLAVE_MAZMORRA
            for (obj in perso.getObjetosTodos()) {
                if (objetoID == 7010) {
                    statVerificar = when (obj.getObjModeloID()) {
                        7010, 9720, 10417, 10418, 10254, 10268, 10269 -> Constantes.STAT_INVOCA_MOB
                        else -> continue
                    }
                } else if (obj.getObjModeloID() !== objetoID) {
                    continue
                }
                val stats: Array<String> = obj.convertirStatsAString(true).split(",")
                for (st in stats) {
                    val statID: Int = Integer.parseInt(st.split("#").get(0), 16)
                    if (statID == Constantes.STAT_LLAVE_MAZMORRA_OCULTO) {
                        continue
                    }
                    if (statID != statVerificar) {
                        continue
                    }
                    val tSolicitaID: Int = Integer.parseInt(st.split("#").get(3), 16)
                    if (tSolicitaID == solicitaID) {
                        b = true
                    }
                }
                if (b) {
                    break
                }
            }
            if (s.contains("!")) {
                b = !b
            }
        } catch (e: Exception) {
        }
        return b.toString() + ""
    }

    private fun tieneObjModeloNoEquip(s: String, perso: Personaje): String {
        var b = false
        try {
            val args: Array<String> = s.substring(3).split(",")
            val id: Int = Integer.parseInt(args[0])
            var cant = 1
            try {
                cant = Integer.parseInt(args[1])
            } catch (e: Exception) {
            }
            cant = Math.max(1, cant)
            b = perso.getObjModeloNoEquipado(id, cant) != null
            if (s.contains("!")) {
                b = !b
            }
        } catch (e: Exception) {
        }
        return b.toString() + ""
    }

    private fun tieneObjModeloEquipado(s: String, perso: Personaje): String {
        var b = false
        try {
            val id: Int = Integer.parseInt(s.substring(3))
            b = perso.tieneObjModeloEquipado(id)
            if (s.contains("!")) {
                b = !b
            }
        } catch (e: Exception) {
        }
        return b.toString() + ""
    }

    private fun tieneHechizo(s: String, perso: Personaje): String {
        var b = false
        try {
            val args: Array<String> = s.substring(3).split(",")
            val id: Int = Integer.parseInt(args[0])
            b = perso.tieneHechizoID(id)
            if (s.contains("!")) {
                b = !b
            }
        } catch (e: Exception) {
        }
        return b.toString() + ""
    }

    private fun tieneDon(s: String, perso: Personaje): String {
        var b = false
        try {
            val args: Array<String> = s.substring(3).split(",")
            val id: Int = Integer.parseInt(args[0])
            var nivel = 1
            try {
                nivel = Integer.parseInt(args[1])
            } catch (e: Exception) {
            }
            nivel = Math.max(1, nivel)
            b = perso.tieneDon(id, nivel)
            if (s.contains("!")) {
                b = !b
            }
        } catch (e: Exception) {
        }
        return b.toString() + ""
    }

    private fun tieneObjetoRecibidoDespuesDeHoras(s: String, perso: Personaje): String {
        try {
            var dHoras: Long = -1
            val args: Array<String> = s.substring(3).split(",")
            val objetoID: Int = Integer.parseInt(args[0])
            val horas: Int = Math.max(0, Integer.parseInt(args[1]))
            for (obj in perso.getObjetosTodos()) {
                if (obj == null) {
                    continue
                }
                if (obj.getObjModeloID() !== objetoID) {
                    continue
                }
                if (!obj.tieneStatTexto(Constantes.STAT_RECIBIDO_EL)) {
                    continue
                }
                val tHoras: Long = obj.getDiferenciaTiempo(Constantes.STAT_RECIBIDO_EL, 60 * 60 * 1000)
                if (tHoras > dHoras) {
                    dHoras = tHoras
                }
            }
            if (dHoras > -1) {
                return dHoras.toString() + "" + s.charAt(2) + "" + horas
            }
        } catch (e: Exception) {
        }
        return "false"
    }

    private fun tieneObjetoRecibidoDespuesDeMinutos(s: String, perso: Personaje): String {
        try {
            var dMinutos: Long = -1
            val args: Array<String> = s.substring(3).split(",")
            val objetoID: Int = Integer.parseInt(args[0])
            val minutos: Int = Math.max(0, Integer.parseInt(args[1]))
            for (obj in perso.getObjetosTodos()) {
                if (obj == null) {
                    continue
                }
                if (obj.getObjModeloID() !== objetoID) {
                    continue
                }
                if (!obj.tieneStatTexto(Constantes.STAT_RECIBIDO_EL)) {
                    continue
                }
                val tMinutos: Long = obj.getDiferenciaTiempo(Constantes.STAT_RECIBIDO_EL, 60 * 1000)
                if (tMinutos > dMinutos) {
                    dMinutos = tMinutos
                }
            }
            if (dMinutos > -1) {
                return dMinutos.toString() + "" + s.charAt(2) + "" + minutos
            }
        } catch (e: Exception) {
        }
        return "false"
    }

    private fun tieneObjetoRecibidoDespuesDeSegundos(s: String, perso: Personaje): String {
        try {
            var dSegundos: Long = -1
            val args: Array<String> = s.substring(3).split(",")
            val objetoID: Int = Integer.parseInt(args[0])
            val segundos: Int = Math.max(0, Integer.parseInt(args[1]))
            for (obj in perso.getObjetosTodos()) {
                if (obj == null) {
                    continue
                }
                if (obj.getObjModeloID() !== objetoID) {
                    continue
                }
                if (!obj.tieneStatTexto(Constantes.STAT_RECIBIDO_EL)) {
                    continue
                }
                val tSegundos: Long = obj.getDiferenciaTiempo(Constantes.STAT_RECIBIDO_EL, 1000)
                if (tSegundos > dSegundos) {
                    dSegundos = tSegundos
                }
            }
            if (dSegundos > -1) {
                return dSegundos.toString() + "" + s.charAt(2) + "" + segundos
            }
        } catch (e: Exception) {
        }
        return "false"
    }

    private fun tieneNivelOficio(s: String, perso: Personaje): String {
        try {
            val a: Array<String> = s.substring(3).split(",")
            return if (a.size > 1) {
                perso.getNivelStatOficio(Integer.parseInt(a[0])).toString() + "" + s.charAt(2) + "" + a[1]
            } else {
                (perso.getStatOficioPorID(Integer.parseInt(a[0])) != null).toString() + ""
            }
        } catch (e: Exception) {
        }
        return "false"
    }

    private fun tieneOficio(s: String, perso: Personaje): String {
        var b = false
        try {
            val id: Int = Integer.parseInt(s.substring(3))
            b = perso.getStatOficioPorID(id) != null
            if (s.contains("!")) {
                b = !b
            }
        } catch (e: Exception) {
        }
        return b.toString() + ""
    }

    private fun puedeIrMapa(s: String, perso: Personaje): String {
        var b = false
        try {
            val id: Short = Short.parseShort(s.substring(3))
            if (perso.esMultiman()) {
                b = true
                return b.toString() + ""
            }
            val mapa: Mapa = Mundo.getMapa(id) ?: return b.toString() + ""
            b = mapa.getPuedoEntrar(perso.getCuenta().getActualIP(), perso)
            if (s.contains("!")) {
                b = !b
            }
        } catch (e: Exception) {
        }
        return b.toString() + ""
    }

    private fun puedeIrMapa2(s: String, perso: Personaje): String {
        var b = false
        try {
            val args: Array<String> = s.substring(3).split(",")
            val id: Short = Short.parseShort(args[0])
            val cant: Int = Integer.parseInt(args[1])
            if (perso.esMultiman()) {
                b = true
                return b.toString() + ""
            }
            val mapa: Mapa = Mundo.getMapa(id) ?: return b.toString() + ""
            val cantTemp: Int = mapa.getPuedoEntrarCant(perso.getCuenta().getActualIP(), perso)
            if (cantTemp < cant) b = true
            if (s.contains("!")) {
                b = !b
            }
        } catch (e: Exception) {
            if (MainServidor.MODO_IMPRIMIR_LOG) System.out.println(e.getMessage())
        }
        return b.toString() + ""
    }

    private fun puedeIrMapa3(s: String, perso: Personaje): String {
        var b = false
        try {
            val cant: Int = Integer.parseInt(s.substring(3))
            if (perso.esMultiman()) {
                b = true
                return b.toString() + ""
            }
            val mapa: Mapa = perso.getMapa() ?: return b.toString() + ""
            val cantTemp: Int = mapa.getPuedoEntrarCant(perso.getCuenta().getActualIP(), perso)
            if (cantTemp < cant) b = true
            if (s.contains("!")) {
                b = !b
            }
        } catch (e: Exception) {
            if (MainServidor.MODO_IMPRIMIR_LOG) System.out.println(e.getMessage())
        }
        return b.toString() + ""
    }

    private fun tieneItemPos(s: String, perso: Personaje): String {
        var b = false
        try {
            val args: Array<String> = s.substring(3).split(",")
            val posAMover: Byte = Byte.parseByte(args[0])
            val exObj: Objeto = perso.getObjPosicion(posAMover)
            if (exObj != null) {
                b = true
            }
            if (s.contains("!")) {
                b = !b
            }
        } catch (e: Exception) {
            if (MainServidor.MODO_IMPRIMIR_LOG) System.out.println(e.getMessage())
        }
        return b.toString() + ""
    }

    private fun tieneEtapa(s: String, perso: Personaje): String {
        var b = false
        try {
            val args: Array<String> = s.substring(3).split(",")
            val id: Int = Integer.parseInt(args[0])
            b = perso.tieneEtapa(id)
            if (s.contains("!")) {
                b = !b
            }
        } catch (e: Exception) {
        }
        return b.toString() + ""
    }

    private fun tieneEstadoObjetivo(s: String, perso: Personaje): String {
        var b = false
        try {
            val args: Array<String> = s.substring(3).split(",")
            val id: Int = Integer.parseInt(args[0])
            var realizado: Int = Mision.ESTADO_NO_TIENE
            try {
                realizado = Integer.parseInt(args[1])
                b = perso.getEstadoObjetivo(id) === realizado
            } catch (e: Exception) {
                b = perso.getEstadoObjetivo(id) !== realizado
            }
            if (s.contains("!")) {
                b = !b
            }
        } catch (e: Exception) {
        }
        return b.toString() + ""
    }

    private fun tieneEstadoMision(s: String, perso: Personaje): String {
        var b = false
        try {
            val args: Array<String> = s.substring(3).split(",")
            val id: Int = Integer.parseInt(args[0])
            var realizado: Int = Mision.ESTADO_NO_TIENE
            try {
                realizado = Integer.parseInt(args[1])
                b = perso.getEstadoMision(id) === realizado
            } catch (e: Exception) {
                b = perso.getEstadoMision(id) !== realizado
            }
            if (s.contains("!")) {
                b = !b
            }
        } catch (e: Exception) {
        }
        return b.toString() + ""
    }

    private fun confirmarObjetivoMision(s: String, perso: Personaje): String {
        var b = false
        try {
            val args: Array<String> = s.substring(3).split(",")
            val id: Int = Integer.parseInt(args[0])
            var preConfirma = true
            try {
                preConfirma = !args[1].equals("1")
            } catch (e: Exception) {
            }
            b = Mundo.getMisionObjetivoModelo(id).confirmar(perso, null, preConfirma, 0)
            if (s.contains("!")) {
                b = !b
            }
        } catch (e: Exception) {
        }
        return b.toString() + ""
    }

    private fun confirmarEtapaMision(s: String, perso: Personaje): String {
        var b = false
        try {
            val args: Array<String> = s.substring(3).split(",")
            val id: Int = Integer.parseInt(args[0])
            var preConfirma = true
            try {
                preConfirma = !args[1].equals("1")
            } catch (e: Exception) {
            }
            b = perso.confirmarEtapa(id, preConfirma)
            if (s.contains("!")) {
                b = !b
            }
        } catch (e: Exception) {
        }
        return b.toString() + ""
    }

    private fun puedeEntrarEnPelea2(s: String, perso: Personaje, pelea: Pelea): String {
        var b = true
        try {
            val args: Array<String> = s.substring(3).split(",")
            val reset: Int = Integer.parseInt(args[0])
            var cant: Int = Integer.parseInt(args[1])
            cant--
            var cantTemp = 0
            if (perso.getResets() !== reset) {
                b = true
            } else {
                for (lucha in pelea.luchadoresDeEquipo(1)) {
                    if (lucha.getPersonaje() == null) continue
                    if (lucha.getPersonaje().getResets() === reset) {
                        cantTemp++
                    }
                }
                for (lucha in pelea.luchadoresDeEquipo(2)) {
                    if (lucha.getPersonaje() == null) continue
                    if (lucha.getPersonaje().getResets() === reset) {
                        cantTemp++
                    }
                }
                if (cantTemp > cant) {
                    b = false
                }
            }
        } catch (e: Exception) {
        }
        return b.toString() + ""
    }

    private fun puedeEntrarEnPelea(s: String, perso: Personaje, pelea: Pelea): String {
        var b = true
        try {
            val args: Array<String> = s.substring(3).split(",")
            val clase: Int = Integer.parseInt(args[0])
            var cant: Int = Integer.parseInt(args[1])
            cant--
            var cantTemp = 0
            if (perso.getClase().getID() !== clase) {
                b = true
            } else {
                for (lucha in pelea.luchadoresDeEquipo(1)) {
                    if (lucha.getPersonaje() == null) continue
                    if (lucha.getPersonaje().getClase().getID() === clase) {
                        cantTemp++
                    }
                }
                for (lucha in pelea.luchadoresDeEquipo(2)) {
                    if (lucha.getPersonaje() == null) continue
                    if (lucha.getPersonaje().getClase().getID() === clase) {
                        cantTemp++
                    }
                }
                if (cantTemp > cant) {
                    b = false
                }
            }
        } catch (e: Exception) {
        }
        return b.toString() + ""
    }
}