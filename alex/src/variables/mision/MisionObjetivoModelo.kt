package variables.mision

import java.util.ArrayList
import java.util.Map
import java.util.Map.Entry
import java.util.regex.Pattern
import variables.npc.NPC
import variables.objeto.Objeto
import variables.personaje.Personaje
import estaticos.Condiciones
import estaticos.Constantes
import estaticos.GestorSalida
import estaticos.MainServidor

class MisionObjetivoModelo(val iD: Int, val tipo: Byte, var args: String, val esalHablar: Boolean, condicion: String, esOculto: Boolean) {
    val condicion: String
    private var _esOculto = false
    fun confirmar(perso: Personaje, mobs: Map<Integer?, Integer?>, preConfirma: Boolean,
                  idObjeto: Int): Boolean {
        // preconfirma no borra nada solo confirma
        var b = false
        if (!Condiciones.validaCondiciones(perso, condicion, null)) {
            return false
        }
        var npc: NPC? = null
        if (perso.getConversandoCon() < 0 && perso.getConversandoCon() > -100) {
            npc = perso.getMapa().getNPC(perso.getConversandoCon())
        }
        when (tipo) {
            NULL -> b = true
            VOLVER_VER_NPC, HABLAR_CON_NPC -> try {
                if (npc == null) {
                    break
                }
                val args = argsPreparados()
                val idNPC: Int = Integer.parseInt(args[0])
                b = idNPC == npc.getModeloID()
                if (args.size >= 3) {
                    val x: Int = Integer.parseInt(args[1].split(":").get(1))
                    val y: Int = Integer.parseInt(args[2].split(":").get(1))
                    b = b and ((perso.getMapa().getX() === x) and (perso.getMapa().getY() === y))
                }
            } catch (e: Exception) {
            }
            ENSEÑAR_OBJETO_NPC -> try {
                if (npc == null) {
                    break
                }
                val args = argsPreparados()
                val req: Array<String> = args[0].split(",")
                val idNPC: Int = Integer.parseInt(req[0])
                val idObjModelo: Int = Integer.parseInt(req[1])
                val cantObj: Int = Integer.parseInt(req[2])
                b = idNPC == npc.getModeloID()
                if (args.size >= 3) {
                    val x: Int = Integer.parseInt(args[1].split(":").get(1))
                    val y: Int = Integer.parseInt(args[2].split(":").get(1))
                    b = b and ((perso.getMapa().getX() === x) and (perso.getMapa().getY() === y))
                }
                if (b) {
                    b = b and perso.tieneObjPorModYCant(idObjModelo, cantObj)
                }
            } catch (e: Exception) {
            }
            ENTREGAR_OBJETO_NPC -> try {
                if (npc == null) {
                    break
                }
                val args = argsPreparados()
                val req: Array<String> = args[0].split(",")
                val idNPC: Int = Integer.parseInt(req[0])
                val idObjModelo: Int = Integer.parseInt(req[1])
                val cantObj: Int = Integer.parseInt(req[2])
                b = idNPC == npc.getModeloID()
                if (args.size >= 3) {
                    val x: Int = Integer.parseInt(args[1].split(":").get(1))
                    val y: Int = Integer.parseInt(args[2].split(":").get(1))
                    b = b and ((perso.getMapa().getX() === x) and (perso.getMapa().getY() === y))
                }
                if (b) {
                    if (preConfirma) {
                        b = perso.tieneObjPorModYCant(idObjModelo, cantObj)
                    } else if (perso.tenerYEliminarObjPorModYCant(idObjModelo, cantObj).also { b = it }) {
                        GestorSalida.ENVIAR_Im_INFORMACION(perso, "022;$cantObj~$idObjModelo")
                    }
                }
            } catch (e: Exception) {
            }
            DESCUBRIR_MAPA -> try {
                b = Short.parseShort(args) === perso.getMapa().getID()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            DESCUBRIR_ZONA -> try {
                b = Short.parseShort(args) === perso.getMapa().getSubArea().getArea().getID()
            } catch (e: Exception) {
            }
            VENCER_AL_MOB, VENCER_MOBS_UN_COMBATE -> try {
                b = true
                val args = argsPreparados()
                val req: Array<String> = args[0].split(",")
                var i = 0
                while (i < req.size) {
                    val idMob: Int = Integer.parseInt(req[i])
                    val cant: Int = Integer.parseInt(req[i + 1])
                    var t = false
                    for (entry in mobs.entrySet()) {
                        if (entry.getKey() === idMob && entry.getValue() >= cant) {
                            t = true
                            break
                        }
                    }
                    b = b and t
                    i += 2
                }
                if (args.size >= 3) {
                    val x: Int = Integer.parseInt(args[1].split(":").get(1))
                    val y: Int = Integer.parseInt(args[2].split(":").get(1))
                    b = b and ((perso.getMapa().getX() === x) and (perso.getMapa().getY() === y))
                }
            } catch (e: Exception) {
            }
            UTILIZAR_OBJETO -> try {
                val args: Array<String> = args.replace("[", "").replace("]", "").replace(" ", "").split(",")
                val idObj: Int = Integer.parseInt(args[0])
                b = idObj == idObjeto
            } catch (e: Exception) {
            }
            10, 11, 13 -> b = true
            ENTREGAR_ALMAS_NPC -> {
                val args: Array<String> = args.replace("[", "").replace("]", "").replace(" ", "").split(",")
                val alma: String = Integer.toHexString(Integer.parseInt(args[1]))
                val cantidad: Int = Integer.parseInt(args[2])
                var van = 0
                val o: ArrayList<Objeto> = ArrayList()
                for (obj in perso.getObjetosTodos()) {
                    if (van >= cantidad) {
                        break
                    }
                    when (obj.getObjModeloID()) {
                        7010, 9720, 10417, 10418, 10254, 10268, 10269 -> {
                        }
                        else -> continue
                    }
                    val stats: Array<String> = obj.convertirStatsAString(true).split(",")
                    var c = false
                    for (st in stats) {
                        try {
                            val statID: Int = Integer.parseInt(st.split("#").get(0), 16)
                            if (statID != Constantes.STAT_INVOCA_MOB) {
                                continue
                            }
                            if (van >= cantidad) {
                                continue
                            }
                            if (st.split("#").get(3).equalsIgnoreCase(alma)) {
                                van++
                                c = true
                            }
                        } catch (e: Exception) {
                        }
                    }
                    if (c) {
                        o.add(obj)
                    }
                }
                if (van >= cantidad) {
                    b = true
                    if (!preConfirma) {
                        van = 0
                        for (obj in o) {
                            val stats: Array<String> = obj.convertirStatsAString(true).split(",")
                            var nuevo = ""
                            for (st in stats) {
                                if (!nuevo.isEmpty()) {
                                    nuevo += ","
                                }
                                val statID: Int = Integer.parseInt(st.split("#").get(0), 16)
                                if (statID == Constantes.STAT_INVOCA_MOB && st.split("#").get(3).equalsIgnoreCase(alma) && van < cantidad) {
                                    van++
                                } else {
                                    nuevo += st
                                }
                            }
                            if (nuevo.isEmpty() || MainServidor.PARAM_BORRAR_PIEDRA_MISION) {
                                perso.borrarOEliminarConOR(obj.getID(), true)
                            } else {
                                obj.convertirStringAStats(nuevo)
                                GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(perso, obj)
                            }
                        }
                    }
                }
            }
        }
        return b
    }

    private fun argsPreparados(): Array<String> {
        return prepararArgs(args, ',', '|').replace(" ", "").split(Pattern.quote("|"))
    }

    fun is_esOculto(): Boolean {
        return _esOculto
    }

    fun set_esOculto(_esOculto: Boolean) {
        this._esOculto = _esOculto
    }

    companion object {
        // public final static int SIN_CUMPLIR = 0, CUMPLIDO = 1;
        const val NULL: Byte = 0
        const val HABLAR_CON_NPC: Byte = 1
        const val ENSEÑAR_OBJETO_NPC: Byte = 2
        const val ENTREGAR_OBJETO_NPC: Byte = 3
        const val DESCUBRIR_MAPA: Byte = 4
        const val DESCUBRIR_ZONA: Byte = 5
        const val VENCER_MOBS_UN_COMBATE: Byte = 6
        const val VENCER_AL_MOB: Byte = 7
        const val UTILIZAR_OBJETO: Byte = 8
        const val VOLVER_VER_NPC: Byte = 9
        const val ENTREGAR_ALMAS_NPC: Byte = 12
        private fun prepararArgs(args: String, buscar: Char, reemplazar: Char): String {
            val s = StringBuilder()
            var corchetes = 0
            for (a in args.toCharArray()) {
                when (a) {
                    '[' -> {
                        corchetes++
                        if (corchetes > 1) {
                            s.append(a)
                        }
                    }
                    ']' -> {
                        if (corchetes > 1) {
                            s.append(a)
                        }
                        corchetes--
                    }
                    else -> if (corchetes == 0 && a == buscar) {
                        s.append(reemplazar)
                    } else {
                        s.append(a)
                    }
                }
            }
            return s.toString()
        }
    }

    init {
        set_esOculto(esOculto)
        this.condicion = condicion
    }
}