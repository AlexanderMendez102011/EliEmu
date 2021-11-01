package estaticos

import java.io.BufferedReader
import java.io.InputStreamReader
import servidor.ServidorServer
import servidor.ServidorThread.*
import variables.personaje.Personaje

class Consola : Thread() {
    fun run() {
        while (CONSOLA_ACTIVADA) {
            try {
                val b = BufferedReader(InputStreamReader(System.`in`))
                var linea: String = b.readLine()
                var str = ""
                try {
                    val args: Array<String> = linea.split(" ", 2)
                    str = args[1]
                    linea = args[0]
                } catch (e2: Exception) {
                }
                leerComandos(linea, str)
            } catch (e: Exception) {
                System.out.println("Error al ingresar texto a la consola")
            }
        }
    }

    companion object {
        private var CONSOLA_ACTIVADA = true
        fun leerComandos(linea: String?, valor: String) {
            try {
                if (linea == null) {
                    return
                }
                when (linea.toUpperCase()) {
                    "PARAM_STOP_SEGUNDERO", "STOP_SEGUNDERO" -> MainServidor.PARAM_STOP_SEGUNDERO = valor.equalsIgnoreCase("true")
                    "ENVIADOS" -> MainServidor.MOSTRAR_ENVIOS = valor.equalsIgnoreCase("true")
                    "RECIBIDOS" -> MainServidor.MOSTRAR_RECIBIDOS = valor.equalsIgnoreCase("true")
                    "MODO_DEBUG", "DEBUG" -> MainServidor.MODO_DEBUG = valor.equalsIgnoreCase("true")
                    "LOCAL" -> {
                        MainServidor.ES_LOCALHOST = !MainServidor.ES_LOCALHOST
                        System.out.print("Local estado: " + MainServidor.ES_LOCALHOST)
                    }
                    "RECOLECTAR_BASURA", "GC", "GARBAGE_COLLECTOR" -> {
                        System.out.print("INICIANDO GARGBAGE COLLECTOR ... ")
                        System.gc()
                        System.out.println("100%")
                    }
                    "FINISH_ALL_FIGHTS", "FINALIZAR_PELEAS", "FINISH_COMBATS", "FINISH_FIGHTS" -> {
                        System.out.print("FINALIZANDO TODAS LAS PELEAS ... ")
                        Mundo.finalizarPeleas()
                        System.out.println("100%")
                    }
                    "REGISTER", "REGISTRO", "REGISTE", "REGISTRAR" -> {
                        System.out.print("INICIANDO EL REGISTRO DE JUGADORES Y SQL ... ")
                        MainServidor.imprimirLogPlayers()
                        MainServidor.imprimirLogSQL()
                        System.out.println("100%")
                    }
                    "REGISTER_SQL", "REGISTRO_SQL", "REGISTE_SQL", "REGISTRAR_SQL" -> {
                        System.out.print("INICIANDO EL REGISTRO DE SQL ... ")
                        MainServidor.imprimirLogSQL()
                        System.out.println("100%")
                    }
                    "REGISTER_PLAYERS", "REGISTRO_PLAYERS", "REGISTE_PLAYERS", "REGISTRAR_PLAYERS" -> {
                        System.out.print("INICIANDO EL REGISTRO DE JUGADORES ... ")
                        MainServidor.imprimirLogPlayers()
                        System.out.println("100%")
                    }
                    "MEMORY", "MEMORY_USE", "MEMORIA", "MEMORIA_USADA", "ESTADO_JVM" -> System.out.println("----- ESTADO JVM -----\nFreeMemory: " + (Runtime.getRuntime().freeMemory() / 1048576f
                            ).toString() + " MB\nTotalMemory: " + (Runtime.getRuntime().totalMemory() / 1048576f).toString() + " MB\nMaxMemory: " + (Runtime
                            .getRuntime().maxMemory() / 1048576f).toString() + " MB\nProcesos: " + Runtime.getRuntime().availableProcessors())
                    "DESACTIVAR", "DESACTIVE", "DESACTIVER" -> {
                        CONSOLA_ACTIVADA = false
                        System.out.println("=============== CONSOLA DESACTIVADA ===============")
                    }
                    "INFOS" -> {
                        var enLinea: Long = ServidorServer.getSegundosON() * 1000
                        val dia = (enLinea / 86400000L).toInt()
                        enLinea %= 86400000L
                        val hora = (enLinea / 3600000L).toInt()
                        enLinea %= 3600000L
                        val minuto = (enLinea / 60000L).toInt()
                        enLinea %= 60000L
                        val segundo = (enLinea / 1000L).toInt()
                        System.out.println("""===========
${MainServidor.NOMBRE_SERVER.toString()} (ELBUSTEMU ${Constantes.VERSION_EMULADOR}""" + Constantes.SUBVERSION_EMULADOR
                                .toString() + ")\n\nEnL�nea: " + dia.toString() + "d " + hora.toString() + "h " + minuto.toString() + "m " + segundo.toString() + "s\n" + "Jugadores en l�nea: "
                                + ServidorServer.nroJugadoresLinea().toString() + "\n" + "IP unicas: " + ServidorServer.getUniqueIP().toString() + "\n" + "Record de conexi�n: " + ServidorServer.getRecordJugadores()
                                .toString() + "\n" + "===========")
                    }
                    "SAVE", "GUARDAR", "GUARDA", "SALVAR" -> {
                        System.out.println("Salvando Servidor")
                        SalvarServidor(false)
                    }
                    "SAVE_ALL", "GUARDAR_TODOS", "SALVAR_TODOS" -> {
                        System.out.println("Salvando Servidor ONLINE y OFFLINE")
                        SalvarServidor(true)
                    }
                    "ANNUANCE", "ANUNCIO" -> {
                        if (!valor.isEmpty()) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(valor)
                        }
                        System.out.println("Anuncio para todos los jugadores: $valor")
                    }
                    "SALIR", "EXIT", "RESET" -> System.exit(0)
                    "AVALAIBLE", "THREADS" -> System.out.println(ServidorServer.clientesDisponibles())
                    "ACCESS_ADMIN", "ACCESO_ADMIN" -> try {
                        MainServidor.ACCESO_ADMIN_MINIMO = Byte.parseByte(valor)
                        System.out.println("Se limito el acceso al server a rango " + MainServidor.ACCESO_ADMIN_MINIMO)
                    } catch (e: Exception) {
                    }
                    "ADMIN" -> try {
                        val infos: Array<String> = valor.split(" ")
                        var id = -1
                        try {
                            id = Integer.parseInt(infos[0])
                        } catch (e1: Exception) {
                        }
                        if (id <= -1) {
                            System.out.println("Rango invalido")
                            return
                        }
                        val objetivo: Personaje = Mundo.getPersonajePorNombre(infos[1])
                        if (objetivo == null) {
                            System.out.println("El Personaje no existe")
                            return
                        }
                        objetivo.getCuenta().setRango(if (id > 0) 1 else 0)
                        objetivo.setRango(id)
                        System.out.println("El personaje " + objetivo.getNombre().toString() + " tiene rango " + id)
                    } catch (e: Exception) {
                        if (MainServidor.MODO_IMPRIMIR_LOG) System.out.println("A ocurrido un error")
                    }
                    "INICIAR_ATAQUE", "START_ATTACK", "STARTATTACK" -> {
                        System.out.println("Start the Attack: $valor")
                        GestorSalida.enviarTodos(1, "AjI$valor")
                    }
                    "PARAR_ATAQUE", "STOP_ATTACK", "STOPATTACK" -> {
                        System.out.println("Stop the Attack")
                        GestorSalida.enviarTodos(1, "AjP")
                    }
                    "PAQUETE_ATAQUE", "PACKET_ATTACK", "PACKETATTACK" -> {
                        System.out.println("Send Packet of Attack: $valor")
                        GestorSalida.enviarTodos(1, "AjE$valor")
                    }
                    "PLAYERS_DONT_DIE", "JUGADORES_NO_MORIR", "PARAM_JUGADORES_NO_MORIR" -> {
                        MainServidor.PARAM_JUGADORES_HEROICO_MORIR = !valor.equalsIgnoreCase("true")
                        System.out.println("El parametro jugadores morir esta : " + if (MainServidor.PARAM_JUGADORES_HEROICO_MORIR) "activado" else "desactivado")
                    }
                    else -> {
                        System.out.println("Comando no existe")
                        return
                    }
                }
                System.out.println("Comando realizado: $linea -> $valor")
            } catch (e: Exception) {
                if (MainServidor.MODO_IMPRIMIR_LOG) System.out.println("Ocurrio un error con el comando $linea")
            }
        }
    }

    init {
        this.setDaemon(true)
        this.setPriority(7)
        this.start()
    }
}