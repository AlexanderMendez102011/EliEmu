package sincronizador

import servidor.ServidorServer
import variables.personaje.Cuenta
import estaticos.MainServidor
import estaticos.Encriptador
import estaticos.GestorSQL
import estaticos.GestorSalida
import estaticos.Mundo

class SincronizadorSocket : Runnable {
    private var _socket: Socket? = null
    private var _in: BufferedInputStream? = null
    private var _out: PrintWriter? = null
    private var _thread: Thread? = null
    private val _puerto: Int
    private val _IP: String
    fun run() {
        try {
            val IP: String = MainServidor.IP_PUBLICA_SERVIDOR
            System.out.println("<<<--- INICIANDO SINCRONIZADOR, IP: $_IP PUERTO: $_puerto --->>>")
            sendPacket("D" + MainServidor.SERVIDOR_ID.toString() + ";" + MainServidor.PUERTO_SERVIDOR.toString() + ";"
                    + MainServidor.SERVIDOR_PRIORIDAD.toString() + ";" + Mundo.SERVIDOR_ESTADO
                    .toString() + if (IP.isEmpty()) "" else ";$IP", true)
            var c = -1
            var lenght = -1
            var index = 0
            var bytes = ByteArray(1)
            while (_in.read().also { c = it } != -1) {
                if (lenght == -1) {
                    lenght = _in.available()
                    bytes = ByteArray(lenght + 1)
                    index = 0
                }
                bytes[index++] = c.toByte()
                if (bytes.size == index) {
                    val tempPacket = String(bytes, "UTF-8")
                    for (packet in tempPacket.split("[\u0000\n\r]")) {
                        if (packet.isEmpty()) {
                            continue
                        }
                        analizarPackets(packet)
                    }
                    lenght = -1
                }
            }
        } catch (e: Exception) {
            // System.out.println("EXCEPTION RUN CONECTOR, IP: " + _IP + " PUERTO: " + _puerto);
        } finally {
            System.out.println("<<<--- CERRANDO SINCRONIZADOR, IP: $_IP PUERTO: $_puerto --->>>")
            try {
                //ServidorGeneral.packetClientesEscogerServer("AH" + Mundo.strParaAH());
            } catch (e: Exception) {
            }
            desconectar()
        }
    }

    fun analizarPackets(packet: String) {
        try {
            when (packet.charAt(0)) {
                'I' -> try {
                    val infos: Array<String> = packet.substring(1).split(";")
                    val ip = infos[0]
                    val cantidad: Int = ServidorServer.getIPsClientes(ip)
                    sendPacket("I$ip;$cantidad", false)
                } catch (e: Exception) {
                }
                'A' -> {
                    val t = Thread(object : Runnable() {
                        fun run() {
                            try {
                                val infos: Array<String> = packet.substring(1).split(";")
                                val id: Int = Integer.parseInt(infos[0])
                                var cuenta: Cuenta = Mundo.getCuenta(id)
                                if (cuenta == null) {
                                    GestorSQL.CARGAR_CUENTA_POR_ID(id) // cuenta nueva
                                    cuenta = Mundo.getCuenta(id)
                                }
                                if (cuenta == null) {
                                    MainServidor.redactarLogServidorln("SE QUIERE REGISTRAR CUENTA FALSA: $packet")
                                    return
                                }
                                try {
                                    if (cuenta.getSocket() != null) {
                                        GestorSalida.ENVIAR_AlEd_DESCONECTAR_CUENTA_CONECTADA(cuenta.getSocket())
                                        GestorSalida.ENVIAR_M0_MENSAJE_BASICOS_SVR_MUESTRA_DISCONNECT(cuenta.getSocket(), "45",
                                                "OTHER PLAYER CONNECTED WITH YOUR ACCOUNT", "")
                                        cuenta.getSocket().cerrarSocket(true, "analizarPackets()")
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                if (MainServidor.PARAM_SISTEMA_IP_ESPERA) {
                                    ServidorServer.addIPEspera(infos[1])
                                }
                                ServidorServer.addEsperandoCuenta(cuenta)
                                sendPacket("A" + id + ";" + cuenta.getPersonajes().size(), true)
                            } catch (e: Exception) {
                                MainServidor.redactarLogServidorln(" EXPCETION AL CARGAR CUENTA: " + e.toString())
                            }
                        }
                    })
                    t.setDaemon(true)
                    t.start()
                }
            }
        } catch (e: Exception) {
        }
    }

    private fun desconectar() {
        try {
            if (_socket != null && !_socket.isClosed()) {
                _socket.close()
            }
            if (_in != null) {
                _in.close()
            }
            if (_out != null) {
                _out.close()
            }
        } catch (e: Exception) {
            // nada
        } finally {
            CONECTOR = null
            SincronizadorSocket()
        }
    }

    companion object {
        var CONECTOR: SincronizadorSocket?
        var INDEX_IP = 0
        fun sendPacket(packet: String, imprimir: Boolean) {
            var packet = packet
            if (CONECTOR == null) {
                return
            }
            if (CONECTOR!!._out != null && !packet.isEmpty() && !packet.equals("" + 0x00.toChar())) {
                packet = Encriptador.aUTF(packet)
                if (imprimir && MainServidor.MOSTRAR_SINCRONIZACION) {
                    System.out.println("ENVIAR PACKET MULTISERVIDOR >> $packet")
                }
                CONECTOR!!._out.print(packet + 0x00.toChar())
                CONECTOR!!._out.flush()
            }
        }
    }

    init {
        _IP = MainServidor.IP_MULTISERVIDOR.get(MainServidor.INDEX_IP)
        if (!MainServidor.ES_LOCALHOST) MainServidor.ES_LOCALHOST = _IP.equals("127.0.0.1")
        MainServidor.INDEX_IP = (MainServidor.INDEX_IP + 1) % MainServidor.IP_MULTISERVIDOR.size()
        _puerto = MainServidor.PUERTO_SINCRONIZADOR
        if (MainServidor.MOSTRAR_SINCRONIZACION) {
            System.out.println("INTENTO SINCRONIZAR IP: $_IP - PUERTO: $_puerto")
        }
        CONECTOR = this
        try {
            _socket = Socket(_IP, _puerto)
            _in = BufferedInputStream(_socket.getInputStream())
            _out = PrintWriter(_socket.getOutputStream())
            _thread = Thread(this)
            _thread.setDaemon(true)
            _thread.start()
        } catch (e: Exception) {
            if (MainServidor.MOSTRAR_SINCRONIZACION) {
                System.out.println("INTENTO FALLIDO -> " + e.toString())
            }
            try {
                Thread.sleep(10000L)
                desconectar()
            } catch (e1: InterruptedException) {
                // TODO Auto-generated catch block
                e1.printStackTrace()
            }
        }
    }
}