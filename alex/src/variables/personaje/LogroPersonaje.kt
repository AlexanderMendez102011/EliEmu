package variables.personaje

import java.math.BigDecimal
import java.math.BigInteger
import java.util.Map
import java.util.Map.Entry
import estaticos.Constantes
import estaticos.GestorSalida
import estaticos.MainServidor
import estaticos.Mundo
import variables.objeto.ObjetoModelo.CAPACIDAD_STATS
import variables.zotros.Logro

class LogroPersonaje(logro: Logro, lleva: Int, estado: Int) {
    private var logro: Logro
    var lleva: Int
    var estado: Int
    fun getLogro(): Logro {
        return logro
    }

    fun setLogro(logro: Logro) {
        this.logro = logro
    }

    fun confirmarMision(mobs: Map<Integer?, Integer?>, idObjeto: String, perso: Personaje) {
        if (estado == 1) return
        var Logro: String = logro.getTitulo()
        var tipo = 2
        when (logro.getTipo()) {
            1 -> {
                val mob: Int = Integer.parseInt(logro.getArgs().split(",").get(0))
                val cant: Int = Integer.parseInt(logro.getArgs().split(",").get(1))
                for (entry in mobs.entrySet()) {
                    if (entry.getKey() === mob) {
                        if (entry.getValue() > cant - lleva) {
                            entry.setValue(cant - lleva)
                        }
                        lleva += entry.getValue()
                        break
                    }
                }
                if (lleva >= cant) {
                    estado = 1
                    lleva = cant
                    entregarRecompensa(perso)
                    GestorSalida.ENVIAR_wl_NOTIFYCACION(perso, Logro, tipo, 0)
                }
            }
            2 -> {
                val item: Int = Integer.parseInt(logro.getArgs().split(",").get(0))
                cant = Integer.parseInt(logro.getArgs().split(",").get(1))
                val itemRecolecta: Int = Integer.parseInt(idObjeto.split(",").get(0))
                var cantRecolecta: Int = Integer.parseInt(idObjeto.split(",").get(1))
                if (item != itemRecolecta) break
                if (cantRecolecta > cant - lleva) {
                    cantRecolecta = cant - lleva
                }
                lleva += cantRecolecta
                if (lleva >= cant) {
                    estado = 1
                    lleva = cant
                    entregarRecompensa(perso)
                    GestorSalida.ENVIAR_wl_NOTIFYCACION(perso, Logro, tipo, 0)
                }
            }
            3 -> {
                item = Integer.parseInt(logro.getArgs().split(",").get(0))
                cant = Integer.parseInt(logro.getArgs().split(",").get(1))
                if (perso.tenerYEliminarObjPorModYCant(item, cant)) {
                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "022;$cant~$item")
                    estado = 1
                    lleva = cant
                    entregarRecompensa(perso)
                    GestorSalida.ENVIAR_wl_NOTIFYCACION(perso, Logro, tipo, 0)
                } else {
                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "14|45")
                }
            }
            4 -> if (logro.getArgs().equalsIgnoreCase(idObjeto)) {
                estado = 1
                lleva = 1
                entregarRecompensa(perso)
                Logro = logro.getTitulo()
                tipo = 2
                GestorSalida.ENVIAR_wl_NOTIFYCACION(perso, Logro, tipo, 0)
            }
            5 -> {
                cant = Integer.parseInt(logro.getArgs())
                if (idObjeto.equals("1")) {
                    lleva++
                    if (lleva >= cant) {
                        estado = 1
                        lleva = cant
                        entregarRecompensa(perso)
                        GestorSalida.ENVIAR_wl_NOTIFYCACION(perso, Logro, tipo, 0)
                    }
                }
            }
            6 -> {
                cant = Integer.parseInt(logro.getArgs())
                lleva++
                if (lleva >= cant) {
                    estado = 1
                    lleva = cant
                    entregarRecompensa(perso)
                    GestorSalida.ENVIAR_wl_NOTIFYCACION(perso, Logro, tipo, 0)
                }
            }
        }
    }

    fun entregarRecompensa(perso: Personaje) {
        perso.PuntosPrestigio += MainServidor.ptsDailyQuest
        perso.sendMessage("Has ganado <b>" + MainServidor.ptsDailyQuest.toString() + "</b> Pts.por obj Diarios")
        when (logro.getRecompensa()) {
            1 -> {
                val expTotal: BigInteger = Mundo.getExpPersonajeReset(perso.getNivel(), perso.getNivelOmega())
                val b = BigDecimal("0.02")
                val expNecesaria = BigDecimal(expTotal)
                val XpAdd: BigDecimal = expNecesaria.multiply(b)
                perso.addExperiencia(Math.round(XpAdd.longValue()), true)
            }
            2 -> perso.addKamas(Long.parseLong(logro.getCantidad()), true, true)
            3 -> {
                val obj: Int = Integer.parseInt(logro.getCantidad().split(",").get(0))
                val cant: Int = Integer.parseInt(logro.getCantidad().split(",").get(1))
                perso.addObjIdentAInventario(Mundo.getObjetoModelo(obj).crearObjeto(cant, Constantes.OBJETO_POS_NO_EQUIPADO,
                        CAPACIDAD_STATS.RANDOM), false)
                GestorSalida.ENVIAR_Im_INFORMACION(perso, "021;$cant~$obj")
            }
        }
    }

    init {
        this.logro = logro
        this.lleva = lleva
        this.estado = estado
    }
}