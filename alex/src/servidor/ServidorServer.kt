package servidor

import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.util.ArrayList
import java.util.Calendar
import java.util.Map
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import estaticos.Constantes
import estaticos.GestorSQL
import estaticos.GestorSalida
import estaticos.MainServidor
import estaticos.Mundo
import servidor.ServidorThread.*
import variables.personaje.Cuenta
import variables.personaje.Personaje

class ServidorServer : Thread() {
    fun run() {
        try {
            while (true) {
                _serverSocket.setPerformancePreferences(1, 2, 0)
                val socket: Socket = _serverSocket.accept()
                _conexiones[_j]++
                ServidorSocket(socket)
            }
        } catch (e: IOException) {
            MainServidor.redactarLogServidorln("EXCEPTION IO RUN SERVIDOR SERVER (FOR COMMAND EXIT)")
        } catch (e: Exception) {
            MainServidor.redactarLogServidorln("EXCEPTION GENERAL RUN SERVIDOR SERVER : " + e.toString())
            e.printStackTrace()
        } finally {
            try {
                if (!_serverSocket.isClosed()) {
                    _serverSocket.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            MainServidor.redactarLogServidorln("FINALLY SERVIDOR SERVER - CERRANDO SERVIDOR")
        }
    }

    companion object {
        private var _serverSocket: ServerSocket? = null
        private var segundero: Timer? = null
        private val _IpsClientes: Map<String, ArrayList<ServidorSocket>> = ConcurrentHashMap()
        private val _clientes: CopyOnWriteArrayList<ServidorSocket> = CopyOnWriteArrayList<ServidorSocket>()
        private val _cuentasEspera: CopyOnWriteArrayList<Cuenta> = CopyOnWriteArrayList<Cuenta>()
        private val _IpsEspera: CopyOnWriteArrayList<String> = CopyOnWriteArrayList<String>()
        var _j = 0
        var recordJugadores = 0
        var _conexiones = IntArray(5)
        var segundosON = 0
        var _eventoON = true
        private fun contador() {
            if (!MainServidor.MENSAJE_TIMER_REBOOT.isEmpty() && MainServidor.SEGUNDOS_REBOOT_SERVER > 0) {
                Mundo.SEG_CUENTA_REGRESIVA = MainServidor.SEGUNDOS_REBOOT_SERVER
                Mundo.MSJ_CUENTA_REGRESIVA = MainServidor.MENSAJE_TIMER_REBOOT
            }
            if (MainServidor.PARAM_LOTERIA) {
                IniciarLoteria()
            }
            segundero = Timer()
            segundero.schedule(object : TimerTask() {
                fun run() {
                    if (Mundo.SERVIDOR_ESTADO === Constantes.SERVIDOR_OFFLINE) {
                        this.cancel()
                        return
                    }
                    AumentarSegundos()
                    if (MainServidor.PARAM_STOP_SEGUNDERO) {
                        return
                    }
                    if (MainServidor.SEGUNDOS_REBOOT_SERVER > 0) {
                        MainServidor.SEGUNDOS_REBOOT_SERVER--
                        if (MainServidor.SEGUNDOS_REBOOT_SERVER === 0) {
                            Reiniciar(1)
                            this.cancel()
                            return
                        } else {
                            val segundosFaltan: Int = MainServidor.SEGUNDOS_REBOOT_SERVER
                            if (segundosFaltan % 60 == 0) {
                                val minutosFaltan = segundosFaltan / 60
                                if (minutosFaltan <= 60 && (minutosFaltan % 10 == 0 || minutosFaltan <= 5)) {
                                    MensajeReset("$minutosFaltan minutos")
                                }
                            }
                        }
                    }
                    if (MainServidor.NPC_RANKING_PVP > 0) {
                        if (segundosON % 600 == 0) {
                            ActualizarPVP()
                        }
                    }
                    if (MainServidor.NPC_RANKING_KOLISEO > 0) {
                        if (segundosON % 600 == 0) {
                            ActualizarKoliseo()
                        }
                    }
                    if (MainServidor.PARAM_ANTI_DDOS) {
                        AntiDDOS(segundosON)
                    }
                    if (MainServidor.PARAM_LADDER_NIVEL) {
                        if (segundosON % 60 == 0) {
                            ActualizarLadder()
                        }
                    }
                    /*if (MainServidor.SEGUNDOS_INACTIVIDAD > 0) {
					if (_segundosON % 3000 == 0) { // es 3000 para q se refresque rapido
						new ExpulsarInactivos();
					}
				}*/if (MainServidor.SEGUNDOS_SALVAR > 0) {
                        if (segundosON % MainServidor.SEGUNDOS_SALVAR === 0) {
                            SalvarServidor(false)
                        }
                    }
                    if (MainServidor.SEGUNDOS_RESET_RATES > 0) {
                        if (segundosON % MainServidor.SEGUNDOS_RESET_RATES === 0) {
                            ResetRates()
                        }
                    }
                    if (MainServidor.SEGUNDOS_LIVE_ACTION > 0) {
                        if (segundosON % MainServidor.SEGUNDOS_LIVE_ACTION === 0) {
                            LiveAction()
                        }
                    }
                    if (MainServidor.SEGUNDOS_LIMPIAR_MEMORIA > 0) {
                        if (segundosON % MainServidor.SEGUNDOS_LIMPIAR_MEMORIA === 0) {
                            GarbageCollector()
                        }
                    }
                    if (MainServidor.SEGUNDOS_ESTRELLAS_GRUPO_MOBS > 0) {
                        if (segundosON % MainServidor.SEGUNDOS_ESTRELLAS_GRUPO_MOBS === 0) {
                            SubirEstrellas()
                        }
                    }
                    if (MainServidor.SEGUNDOS_DETECTAR_DDOS > 0) {
                        if (segundosON % MainServidor.SEGUNDOS_DETECTAR_DDOS === 0) {
                            DetectarDDOS()
                        }
                    }
                    if (MainServidor.SEGUNDOS_MOVER_MONTURAS > 0) {
                        if (segundosON % MainServidor.SEGUNDOS_MOVER_MONTURAS === 0) {
                            MoverPavos()
                        }
                    }
                    if (MainServidor.SEGUNDOS_MOVER_GRUPO_MOBS > 0) {
                        if (segundosON % MainServidor.SEGUNDOS_MOVER_GRUPO_MOBS === 0) {
                            MoverMobs()
                        }
                    }
                    if (MainServidor.SEGUNDOS_PUBLICIDAD > 0) {
                        if (segundosON % MainServidor.SEGUNDOS_PUBLICIDAD === 0) {
                            Publicidad()
                        }
                    }
                    if (MainServidor.PARAM_LOTERIA) {
                        if (Mundo.SEG_CUENTA_REGRESIVA > 0) {
                            if (Mundo.SEG_CUENTA_REGRESIVA - 1 === 0) {
                                if (Mundo.MSJ_CUENTA_REGRESIVA.equalsIgnoreCase("LOTERIA")) {
                                    SortearLoteria()
                                } else {
                                    GestorSalida.ENVIAR_ÑL_BOTON_LOTERIA_TODOS(false)
                                }
                            }
                        } else if (segundosON % 3600 == 0) {
                            IniciarLoteria()
                        }
                    }
                    if (Mundo.SEG_CUENTA_REGRESIVA > 0) {
                        if (--Mundo.SEG_CUENTA_REGRESIVA === 0) {
                            BorrarCuentaRegresiva()
                        }
                    }
                    if (MainServidor.EVENTO_TIEMPO_SEGUNDO > 0 && segundosON % MainServidor.EVENTO_TIEMPO_SEGUNDO === 0) {
                        if (MainServidor.EVENTO_MOBS.length() > 0 && MainServidor.EVENTO_NPC > 0) {
                            if (_eventoON) {
                                AgregarNpcEvento()
                            } else {
                                AgregarMobEvento()
                            }
                            _eventoON = !_eventoON
                        } else if (MainServidor.EVENTO_NPC > 0) {
                            AgregarNpcEvento()
                        } else if (MainServidor.EVENTO_MOBS.length() > 0) {
                            AgregarMobEvento()
                        }
                    }
                    if (MainServidor.PORTAL_TIEMPO_SEGUNDO > 0 && segundosON % MainServidor.PORTAL_TIEMPO_SEGUNDO === 0) {
                        AgregarPortal()
                    }
                    if (MainServidor.INVASION_TIEMPO_SEGUNDO > 0 && segundosON % MainServidor.INVASION_TIEMPO_SEGUNDO === 0) {
                        AgregarMobInvasion()
                    }
                    if (MainServidor.PARAM_KOLISEO) {
                        Mundo.SEGUNDOS_INICIO_KOLISEO--
                        if (Mundo.SEGUNDOS_INICIO_KOLISEO === 60) {
                            GestorSalida.ENVIAR_Im_INFORMACION_KOLISEO("1KOLISEO_UN_MINUTO_INICIA")
                        } else if (Mundo.SEGUNDOS_INICIO_KOLISEO === 5) {
                            GestorSalida.ENVIAR_Im_INFORMACION_KOLISEO("1KOLISEO_5_SEGUNDOS_INICIA")
                        } else if (Mundo.SEGUNDOS_INICIO_KOLISEO === 0) {
                            Koliseo()
                        }
                    }

                    /*
				if (MainServidor.PARAM_KOLISEO) {
					new Koliseo();
				}*/
                    // new EmbarazoMonturas();
                    // cada segundo
                    DisminuirFatiga()
                    CheckearObjInteractivos()
                    MoverRecaudadores()
                    val dia: Int = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
                    if (Mundo.DIA_DEL_AÑO !== dia) {
                        Mundo.DIA_DEL_AÑO = dia
                        ResetExpDia()
                    }
                }
            }, 1000, 1000)
            val autoSelect = Timer()
            autoSelect.schedule(object : TimerTask() {
                fun run() {
                    if (Mundo.SERVIDOR_ESTADO === Constantes.SERVIDOR_OFFLINE) {
                        this.cancel()
                        return
                    }
                    GestorSQL.ES_IP_BANEADA("111.222.333.444") // para usar el sql y q no se crashee
                }
            }, 300000, 300000)
        }

        fun cerrarSocketServidor() {
            try {
                for (ep in _clientes) {
                    ep.cerrarSocket(false, "cerrarServidor()")
                }
                MainServidor.redactarLogServidor(" SE EXPULSARON LOS CLIENTES ")
                if (!_serverSocket.isClosed()) {
                    _serverSocket.close()
                }
            } catch (e: Exception) {
                MainServidor.redactarLogServidorln("EXCEPTION CERRAR SERVIDOR : " + e.toString())
                e.printStackTrace()
            }
        }

        val clientes: CopyOnWriteArrayList<ServidorSocket>
            get() = _clientes

        fun getCliente(b: Int): ServidorSocket {
            return _clientes.get(b)
        }

        fun addIPsClientes(s: ServidorSocket) {
            val ip: String = s.getActualIP()
            if (_IpsClientes[ip] == null) {
                _IpsClientes.put(ip, ArrayList<ServidorSocket>())
            }
            if (!_IpsClientes[ip].contains(s)) {
                _IpsClientes[ip].add(s)
            }
        }

        fun borrarIPsClientes(s: ServidorSocket) {
            val ip: String = s.getActualIP()
            if (_IpsClientes[ip] == null) {
                return
            }
            _IpsClientes[ip].remove(s)
        }

        val uniqueIP: Int
            get() {
                val IPTEMP: ArrayList<String> = ArrayList()
                for (socket in _clientes) {
                    if (socket != null) {
                        if (!IPTEMP.contains(socket.getActualIP())) {
                            IPTEMP.add(socket.getActualIP())
                        }
                    }
                }
                return IPTEMP.size()
            }

        fun getIPsClientes(ip: String?): Int {
            return if (_IpsClientes[ip!!] == null) {
                0
            } else _IpsClientes[ip].size()
        }

        fun addCliente(socket: ServidorSocket?) {
            if (socket == null) {
                return
            }
            addIPsClientes(socket)
            _clientes.add(socket)
            if (_clientes.size() > recordJugadores) {
                recordJugadores = _clientes.size()
            }
        }

        fun borrarCliente(socket: ServidorSocket?) {
            if (socket == null) {
                return
            }
            borrarIPsClientes(socket)
            _clientes.remove(socket)
        }

        fun nroJugadoresLinea(): Int {
            return _clientes.size()
        }

        fun delEsperandoCuenta(cuenta: Cuenta?) {
            if (cuenta == null) {
                return
            }
            _cuentasEspera.remove(cuenta)
        }

        fun addEsperandoCuenta(cuenta: Cuenta?) {
            if (cuenta != null) {
                _cuentasEspera.add(cuenta)
            }
        }

        fun getEsperandoCuenta(id: Int): Cuenta? {
            for (cuenta in _cuentasEspera) {
                if (cuenta.getID() === id) {
                    return cuenta
                }
            }
            return null
        }

        fun borrarIPEspera(ip: String?): Boolean {
            return _IpsEspera.remove(ip)
        }

        fun addIPEspera(ip: String?) {
            _IpsEspera.add(ip)
        }

        fun clientesDisponibles(): String {
            val IPs: ArrayList<String> = ArrayList()
            for (ep in _clientes) {
                try {
                    if (!IPs.contains(ep.getActualIP())) {
                        IPs.add(ep.getActualIP())
                    }
                } catch (e: Exception) {
                }
            }
            return "IP Availables for attack: " + IPs.size()
        }

        fun listaClientesBug(segundos: Int): String {
            val str = StringBuilder()
            for (ep in _clientes) {
                try {
                    if (ep.getPersonaje() != null) {
                        if (!ep.getPersonaje().enLinea()) {
                            ep.cerrarSocket(true, "listaClientesBug(1)")
                            str.append("""
    
    ${ep.getActualIP()}
    """.trimIndent())
                        }
                    }
                } catch (e: Exception) {
                }
            }
            return str.toString()
        }

        fun borrarClientesBug(segundos: Int): Int {
            var i = 0
            for (ep in _clientes) {
                try {
                    if (ep.getPersonaje() != null) {
                        if (!ep.getPersonaje().enLinea()) {
                            ep.cerrarSocket(true, "borrarClientesBug(1)")
                            i++
                        }
                    }
                } catch (e: Exception) {
                }
            }
            return i
        }

        fun getHoraHoy(perso: Personaje): String {
            val hoy: Calendar = Calendar.getInstance()
            if (perso.esDeDia()) {
                hoy.set(Calendar.HOUR_OF_DAY, MainServidor.HORA_DIA)
                hoy.set(Calendar.MINUTE, MainServidor.MINUTOS_DIA)
            } else if (perso.esDeNoche()) {
                hoy.set(Calendar.HOUR_OF_DAY, MainServidor.HORA_NOCHE)
                hoy.set(Calendar.MINUTE, MainServidor.MINUTOS_NOCHE)
            }
            return "BT" + hoy.getTimeInMillis() // + hoy.getTimeZone().getRawOffset()
        }

        val fechaHoy: String
            get() {
                val hoy: Calendar = Calendar.getInstance()
                var dia: String = hoy.get(Calendar.DAY_OF_MONTH).toString() + ""
                while (dia.length() < 2) {
                    dia = "0$dia"
                }
                var mes: String = hoy.get(Calendar.MONTH).toString() + ""
                while (mes.length() < 2) {
                    mes = "0$mes"
                }
                val año: Int = hoy.get(Calendar.YEAR)
                return "BD$año|$mes|$dia"
            }
    }

    init {
        try {
            contador()
            // muestra en el banner el tiempo para reboot
            _serverSocket = ServerSocket(MainServidor.PUERTO_SERVIDOR)
            val _thread = Thread(this)
            _thread.setDaemon(true)
            _thread.setPriority(MAX_PRIORITY)
            _thread.start()
            System.out.println("Aperturado el Servidor, PUERTO SERVIDOR " + MainServidor.PUERTO_SERVIDOR)
        } catch (e: Exception) {
            MainServidor.redactarLogServidorln("ERROR AL CREAR EL SERVIDOR PERSONAJE" + e.toString())
            e.printStackTrace()
            // System.exit(1);
        }
    }
}