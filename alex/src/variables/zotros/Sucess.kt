package variables.zotros

import java.math.BigDecimal
import java.math.BigInteger
import java.util.Map
import java.util.TreeMap
import java.util.concurrent.CopyOnWriteArrayList
import estaticos.Constantes
import estaticos.GestorSQL
import estaticos.GestorSalida
import estaticos.Mundo
import variables.objeto.ObjetoModelo
import variables.objeto.ObjetoModelo.CAPACIDAD_STATS
import variables.personaje.Personaje
import variables.ranking.RankingKoliseo
import java.util.Map.Entry

/** @author Orion
 */
class Sucess {
    var id = 0
    var type: Byte = 0
    var args: String? = null

    // Variables
    var valeur = 0
    var recompense = 0
    var points = 0
    var art = 0
    var categoria = 0
    var name: String? = null
    var recompenseArgs: String? = null

    // public Map<Integer, Integer> listMobsWin = new TreeMap<Integer, Integer>();
    constructor(id: Int) {
        id = id
    }

    constructor(id: Int, name: String?, type: Byte, args: String?, recompense: Int, recompenseArgs: String?, points: Int,
                art: Int, categoria: Int) {
        this.id = id
        this.name = name
        this.type = type
        this.args = args
        this.recompense = recompense
        this.recompenseArgs = recompenseArgs
        this.points = points
        this.art = art
        this.categoria = categoria
    }

    companion object {
        var sucess: Map<Integer, Sucess> = TreeMap()
        var suc: CopyOnWriteArrayList<Sucess> = CopyOnWriteArrayList()
        fun launch(p: Personaje?, type: Byte, mobs: Map<Integer?, Integer?>?, luchadores: Int) {
            if (p == null) return
            var e: Sucess? = null
            var args = 0
            var cant = 0
            for (s in Mundo.getSucess().entrySet()) {
                if (s.getValue().getType() === type) {
                    if (suc.contains(s.getValue())) {
                        continue
                    }
                    suc.add(s.getValue())
                }
            }
            when (type) {
                1 -> {
                    if (suc == null) return
                    for (s in suc) {
                        args = try {
                            Integer.parseInt(s.args)
                        } catch (m: Exception) {
                            continue
                        }
                        if (e != null) {
                            e = null
                        }
                        if (args <= p.getNivelOmega() && s.type == type) {
                            e = Sucess(s.id, s.name, s.type, s.args, s.recompense,
                                    s.recompenseArgs, s.points, s.art, s.categoria)
                            if (e != null) {
                                if (p.getAllSucess().contains(e.id)) {
                                    continue
                                }
                                p.getAllSucess().add(e.id)
                                giveRecompense(e, p)
                                p.setPointsSucces(p.getPointsSucces() + e.points)
                                GestorSQL.SALVAR_PERSONAJE(p, true)
                                return
                            }
                        }
                    }
                    return
                }
                2 -> {
                    if (suc == null) return
                    for (s in suc) {
                        args = try {
                            Integer.parseInt(s.args)
                        } catch (m: Exception) {
                            continue
                        }
                        if (e != null) {
                            e = null
                        }
                        if (args == p.getNbFightWin() && s.type == type) {
                            e = Sucess(s.id, s.name, s.type, s.args, s.recompense,
                                    s.recompenseArgs, s.points, s.art, s.categoria)
                            if (e != null) {
                                if (p.getAllSucess().contains(e.id)) continue
                                p.getAllSucess().add(e.id)
                                giveRecompense(e, p)
                                p.setPointsSucces(p.getPointsSucces() + e.points)
                                GestorSQL.SALVAR_PERSONAJE(p, true)
                            }
                        }
                    }
                    return
                }
                3 -> {
                    if (suc == null) return
                    for (s in suc) {
                        args = try {
                            Integer.parseInt(s.args)
                        } catch (m: Exception) {
                            continue
                        }
                        if (e != null) {
                            e = null
                        }
                        if (args == p.getNbQuests() && s.type == type) {
                            e = Sucess(s.id, s.name, s.type, s.args, s.recompense,
                                    s.recompenseArgs, s.points, s.art, s.categoria)
                            if (e != null) {
                                if (p.getAllSucess().contains(e.id)) continue
                                p.getAllSucess().add(e.id)
                                giveRecompense(e, p)
                                p.setPointsSucces(p.getPointsSucces() + e.points)
                                GestorSQL.SALVAR_PERSONAJE(p, true)
                            }
                        }
                    }
                    return
                }
                4 -> {
                    if (suc == null) return
                    for (s in suc) {
                        args = try {
                            Integer.parseInt(s.args)
                        } catch (m: Exception) {
                            continue
                        }
                        if (e != null) {
                            e = null
                        }
                        if (args <= p.getGradoAlineacion() && s.type == type) {
                            e = Sucess(s.id, s.name, s.type, s.args, s.recompense,
                                    s.recompenseArgs, s.points, s.art, s.categoria)
                            if (e != null) {
                                if (p.getAllSucess().contains(e.id)) continue
                                p.getAllSucess().add(e.id)
                                giveRecompense(e, p)
                                p.setPointsSucces(p.getPointsSucces() + e.points)
                                GestorSQL.SALVAR_PERSONAJE(p, true)
                            }
                        }
                    }
                    return
                }
                5 -> {
                    if (suc == null) {
                        return
                    }
                    for (s in suc) {
                        try {
                            args = Integer.parseInt(s.args.split(",").get(0))
                            cant = Integer.parseInt(s.args.split(",").get(1))
                        } catch (m: Exception) {
                            continue
                        }
                        if (e != null) {
                            e = null
                        }
                        for (entryX in p.getAlmasMobs().entrySet()) {
                            if (args == entryX.getKey()) {
                                if (cant == entryX.getValue()) {
                                    e = Sucess(s.id, s.name, s.type, s.args, s.recompense,
                                            s.recompenseArgs, s.points, s.art, s.categoria)
                                }
                                if (e != null) {
                                    if (p.getAllSucess().contains(e.id)) continue
                                    p.getAllSucess().add(e.id)
                                    giveRecompense(e, p)
                                    p.setPointsSucces(p.getPointsSucces() + e.points)
                                    GestorSQL.SALVAR_PERSONAJE(p, true)
                                }
                            }
                        }
                    }
                    return
                }
                6 -> {
                    if (suc == null) return
                    for (s in suc) {
                        args = try {
                            Integer.parseInt(s.args)
                        } catch (m: Exception) {
                            continue
                        }
                        if (e != null) {
                            e = null
                        }
                        if (args <= p.getNbStalkWin() && s.type == type) {
                            e = Sucess(s.id, s.name, s.type, s.args, s.recompense,
                                    s.recompenseArgs, s.points, s.art, s.categoria)
                            if (e != null) {
                                if (p.getAllSucess().contains(e.id)) continue
                                p.getAllSucess().add(e.id)
                                giveRecompense(e, p)
                                p.setPointsSucces(p.getPointsSucces() + e.points)
                                GestorSQL.SALVAR_PERSONAJE(p, true)
                            }
                        }
                    }
                }
                7 -> {
                    if (suc == null) return
                    for (s in suc) {
                        args = try {
                            Integer.parseInt(s.args)
                        } catch (m: Exception) {
                            continue
                        }
                        if (e != null) {
                            e = null
                        }
                        if (args == p.getNbFight() && s.type == type) {
                            e = Sucess(s.id, s.name, s.type, s.args, s.recompense,
                                    s.recompenseArgs, s.points, s.art, s.categoria)
                            if (e != null) {
                                if (p.getAllSucess().contains(e.id)) continue
                                p.getAllSucess().add(e.id)
                                giveRecompense(e, p)
                                p.setPointsSucces(p.getPointsSucces() + e.points)
                                GestorSQL.SALVAR_PERSONAJE(p, true)
                            }
                        }
                    }
                    return
                }
                8 -> {
                    if (suc == null) return
                    var argumentos: Long = 0
                    for (s in suc) {
                        try {
                            argumentos = Long.parseLong(s.args)
                        } catch (m: Exception) {
                            continue
                        }
                        if (e != null) {
                            e = null
                        }
                        if (argumentos <= p.getOgGanadas() && s.type == type) {
                            e = Sucess(s.id, s.name, s.type, s.args, s.recompense,
                                    s.recompenseArgs, s.points, s.art, s.categoria)
                            if (e != null) {
                                if (p.getAllSucess().contains(e.id)) continue
                                p.getAllSucess().add(e.id)
                                giveRecompense(e, p)
                                p.setPointsSucces(p.getPointsSucces() + e.points)
                                GestorSQL.SALVAR_PERSONAJE(p, true)
                            }
                        }
                    }
                    return
                }
                9 -> {
                    if (suc == null) return
                    for (s in suc) {
                        args = try {
                            Integer.parseInt(s.args)
                        } catch (m: Exception) {
                            continue
                        }
                        if (e != null) {
                            e = null
                        }
                        if (args <= p.getOgGastadas() && s.type == type) {
                            e = Sucess(s.id, s.name, s.type, s.args, s.recompense,
                                    s.recompenseArgs, s.points, s.art, s.categoria)
                            if (e != null) {
                                if (p.getAllSucess().contains(e.id)) continue
                                p.getAllSucess().add(e.id)
                                giveRecompense(e, p)
                                p.setPointsSucces(p.getPointsSucces() + e.points)
                                GestorSQL.SALVAR_PERSONAJE(p, true)
                            }
                        }
                    }
                    return
                }
                10 -> {
                    if (suc == null) return
                    for (s in suc) {
                        if (p.tieneObjModeloEquipado(Integer.parseInt(s.args.split(",").get(0)))
                                && p.tieneObjModeloEquipado(Integer.parseInt(s.args.split(",").get(1)))
                                && p.tieneObjModeloEquipado(Integer.parseInt(s.args.split(",").get(2)))
                                && p.tieneObjModeloEquipado(Integer.parseInt(s.args.split(",").get(3)))
                                && p.tieneObjModeloEquipado(Integer.parseInt(s.args.split(",").get(4)))
                                && p.tieneObjModeloEquipado(Integer.parseInt(s.args.split(",").get(5)))
                                && p.tieneObjModeloEquipado(Integer.parseInt(s.args.split(",").get(6)))) {
                            e = Sucess(s.id, s.name, s.type, s.args, s.recompense,
                                    s.recompenseArgs, s.points, s.art, s.categoria)
                            if (e != null) {
                                if (p.getAllSucess().contains(e.id)) continue
                                p.getAllSucess().add(e.id)
                                giveRecompense(e, p)
                                p.setPointsSucces(p.getPointsSucces() + e.points)
                                GestorSQL.SALVAR_PERSONAJE(p, true)
                            }
                        }
                    }
                    return
                }
                11 -> {
                    if (suc == null) return
                    for (s in suc) {
                        args = try {
                            Integer.parseInt(s.args)
                        } catch (m: Exception) {
                            continue
                        }
                        if (e != null) {
                            e = null
                        }
                        for (rank in Mundo._RANKINGS_KOLISEO.values()) {
                            if (p != null) if (p.getNombre().equals(rank.getNombre())) {
                                if (args == rank.getVictorias() && s.type == type) {
                                    e = Sucess(s.id, s.name, s.type, s.args, s.recompense,
                                            s.recompenseArgs, s.points, s.art, s.categoria)
                                    if (e != null) {
                                        if (p.getAllSucess().contains(e.id)) continue
                                        p.getAllSucess().add(e.id)
                                        giveRecompense(e, p)
                                        p.setPointsSucces(p.getPointsSucces() + e.points)
                                        GestorSQL.SALVAR_PERSONAJE(p, true)
                                    }
                                }
                            } else {
                                continue
                            }
                        }
                    }
                    return
                }
                12 -> {
                    if (suc == null) return
                    for (s in suc) {
                        try {
                            args = Integer.parseInt(s.args.split(",").get(0))
                            cant = Integer.parseInt(s.args.split(",").get(1))
                        } catch (m: Exception) {
                            continue
                        }
                        if (e != null) {
                            e = null
                        }
                        for (entry in mobs.entrySet()) {
                            if (entry.getKey() === args && cant <= p.getBonusIdolo() && s.type == type) {
                                e = Sucess(s.id, s.name, s.type, s.args, s.recompense,
                                        s.recompenseArgs, s.points, s.art, s.categoria)
                                if (e != null) {
                                    if (p.getAllSucess().contains(e.id)) continue
                                    p.getAllSucess().add(e.id)
                                    giveRecompense(e, p)
                                    p.setPointsSucces(p.getPointsSucces() + e.points)
                                    GestorSQL.SALVAR_PERSONAJE(p, true)
                                    return
                                }
                            }
                        }
                    }
                    return
                }
                13 -> {
                    if (suc == null) return
                    for (s in suc) {
                        args = try {
                            Integer.parseInt(s.args)
                        } catch (m: Exception) {
                            continue
                        }
                        if (e != null) {
                            e = null
                        }
                        if (args == p.getNObjDiarios() && s.type == type) {
                            e = Sucess(s.id, s.name, s.type, s.args, s.recompense,
                                    s.recompenseArgs, s.points, s.art, s.categoria)
                            if (e != null) {
                                if (p.getAllSucess().contains(e.id)) continue
                                p.getAllSucess().add(e.id)
                                giveRecompense(e, p)
                                p.setPointsSucces(p.getPointsSucces() + e.points)
                                GestorSQL.SALVAR_PERSONAJE(p, true)
                                return
                            }
                        }
                    }
                    return
                }
                14 -> {
                    if (suc == null) return
                    for (s in suc) {
                        try {
                            args = Integer.parseInt(s.args.split(",").get(0))
                            cant = Integer.parseInt(s.args.split(",").get(1))
                        } catch (m: Exception) {
                            continue
                        }
                        if (e != null) {
                            e = null
                        }
                        for (entry in mobs.entrySet()) {
                            if (entry.getKey() === args && cant == luchadores && s.type == type) {
                                e = Sucess(s.id, s.name, s.type, s.args, s.recompense,
                                        s.recompenseArgs, s.points, s.art, s.categoria)
                                if (e != null) {
                                    if (p.getAllSucess().contains(e.id)) continue
                                    p.getAllSucess().add(e.id)
                                    giveRecompense(e, p)
                                    p.setPointsSucces(p.getPointsSucces() + e.points)
                                    GestorSQL.SALVAR_PERSONAJE(p, true)
                                    return
                                }
                            }
                        }
                    }
                    return
                }
                15 -> {
                    if (suc == null) return
                    var lista = ""
                    for (s in suc) {
                        lista = try {
                            s.args
                        } catch (m: Exception) {
                            continue
                        }
                        var puede = false
                        var logro = 0
                        while (logro < lista.split(",").length) {
                            puede = if (p.getAllSucess().contains(Integer.parseInt(lista.split(",").get(logro)))) {
                                true
                            } else {
                                false
                            }
                            if (puede) {
                                e = Sucess(s.id, s.name, s.type, s.args, s.recompense,
                                        s.recompenseArgs, s.points, s.art, s.categoria)
                                if (e != null) {
                                    if (p.getAllSucess().contains(e.id)) {
                                        logro++
                                        continue
                                    }
                                    p.getAllSucess().add(e.id)
                                    giveRecompense(e, p)
                                    p.setPointsSucces(p.getPointsSucces() + e.points)
                                    GestorSQL.SALVAR_PERSONAJE(p, true)
                                    return
                                }
                            }
                            logro++
                        }
                    }
                    return
                }
            }
        }

        fun giveRecompense(s: Sucess, p: Personaje?) {
            val recompenseId = s.recompense
            GestorSalida.GAME_SEND_MESSAGE(p, """Has completado el logro: ${s.name}' !
Actualmente tienes: ${p.getAllSucess().size()} logros.""")
            when (recompenseId) {
                1 -> try {
                    val titleID: Int = Integer.parseInt(s.recompenseArgs)
                    if (titleID < 0) return  // Ne devrait pas arriver
                    val t: Titulo = Mundo.getTitulo(titleID)
                    if (t != null) if (!p.getTitulos().containsKey(titleID)) {
                        p.setTitulo(titleID)
                        p.addTitulo(titleID, 0)
                        GestorSalida.GAME_SEND_MESSAGE(p, "<b>[Logro: " + s.name
                                + "]</b> : Has obtenido el titulo : <b>" + t.get_nombre() + "</b> !")
                    }
                    return
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                2 -> {
                    try {
                        val itemID: Int = Integer.parseInt(s.recompenseArgs.split(",").get(0))
                        val quantity: Int = Integer.parseInt(s.recompenseArgs.split(",").get(1))
                        if (itemID < 0 || quantity < 0) return  // Ne devrait pas arriver
                        if (p != null && p.enLinea()) {
                            val objWin: ObjetoModelo = Mundo.getObjetoModelo(itemID) ?: return
                            p.addObjIdentAInventario(
                                    objWin.crearObjeto(quantity, Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM),
                                    false)
                            GestorSalida.GAME_SEND_MESSAGE(p,
                                    "<b>[Logro: " + s.name + "]</b> : has obtenido : <b>" + objWin.getNombre() + "</b>.")
                            return
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                3 -> {
                    try {
                        val auraId: Int = Integer.parseInt(s.recompenseArgs)
                        if (auraId < 0) return  // Ne devrait pas arriver
                        if (!p.getAllAura(auraId)) {
                            p.setCurAura(auraId)
                            p.allAura.add(auraId)
                            p.refrescarEnMapa()
                            GestorSalida.GAME_SEND_MESSAGE(p,
                                    "<b>[Logro: " + s.name + "]</b> : has obtenido : <b> un aura </b>.")
                        }
                        return
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                4 -> {
                    try {
                        val ogrinas: Long = Long.parseLong(s.recompenseArgs)
                        p.addKamas(ogrinas, true, true)
                        GestorSalida.GAME_SEND_MESSAGE(p,
                                "<b>[Logro: " + s.name + "]</b> : has obtenido : <b>" + ogrinas + "</b> de ogrinas.")
                        return
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                5 -> try {
                    val expTotal: BigInteger = Mundo.getExpPersonajeReset(p.getNivel(), p.getNivelOmega())
                    val expNecesaria = BigDecimal(expTotal)
                    var XpAdd = BigDecimal("0")
                    val b = BigDecimal("0.02")
                    XpAdd = expNecesaria.multiply(b)
                    p.addExperiencia(Math.round(XpAdd.longValue()), true)
                    GestorSalida.GAME_SEND_MESSAGE(p, "<b>[Logro:  " + s.name + "]</b> : has obtenido : <b>"
                            + Math.round(XpAdd.longValue()) + "</b> puntos de experiencia.")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                6 -> try {
                    val Ornamento: Int = Integer.parseInt(s.recompenseArgs)
                    if (Ornamento < 0) return  // Ne devrait pas arriver
                    val t: Ornamento = Mundo.getOrnamento(Ornamento)
                    if (t != null) if (!p.getOrnamentos().contains(Ornamento)) {
                        p.setOrnamento(Ornamento)
                        p.addOrnamento(Ornamento)
                        GestorSalida.GAME_SEND_MESSAGE(p, "<b>[Logro: " + s.name
                                + "]</b> : Has obtenido el ornamento : <b>" + t.get_nombre() + "</b> !")
                    }
                    return
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            launch(p, 15.toByte(), null, 0)
        }
    }
}