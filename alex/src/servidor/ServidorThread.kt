package servidor

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import java.net.URLConnection
import java.net.UnknownHostException
import java.util.Date
import java.util.HashMap
import estaticos.Constantes
import estaticos.GestorSQL
import estaticos.GestorSalida
import estaticos.MainServidor
import estaticos.Mundo
import sincronizador.SincronizadorSocket

abstract class ServidorThread : Thread() {
    private val _nameClass: String
    fun load(priority: Int) {
        if (ACTIVOS.containsKey(_nameClass) && ACTIVOS.get(_nameClass)) {
            return
        }
        ACTIVOS.put(_nameClass, true)
        this.setDaemon(true)
        this.setPriority(priority)
        this.start()
    }

    abstract fun exec()
    fun run() {
        exec()
        ACTIVOS.put(_nameClass, false)
    }

    class Reiniciar(i: Int) : ServidorThread() {
        private var _i = 0
        override fun exec() {
            System.exit(_i)
        }

        init {
            _i = 0
            load(10)
        }
    }

    class SalvarServidor(inclusoOffline: Boolean) : ServidorThread() {
        private var _inclusoOffline = false
        override fun exec() {
            if (Mundo.SERVIDOR_ESTADO === Constantes.SERVIDOR_SALVANDO) {
                MainServidor.redactarLogServidorln(
                        "Se esta intentando salvar el servidor, cuando este ya se esta salvando (MUNDO DOFUS)")
            } else {
                GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS("1164")
                Mundo.salvarServidor(_inclusoOffline)
                GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS("1165")
            }
        }

        init {
            _inclusoOffline = inclusoOffline
            load(9)
        }
    }

    class RefrescarTodosMobs : ServidorThread() {
        override fun exec() {
            Mundo.refrescarTodosMobs()
        }

        init {
            load(5)
        }
    }

    class ResetRates : ServidorThread() {
        override fun exec() {
            MainServidor.resetRates()
            GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS("1FINISH_SUPER_RATES")
            MainServidor.SEGUNDOS_RESET_RATES = 0
        }

        init {
            load(5)
        }
    }

    class DisminuirFatiga : ServidorThread() {
        override fun exec() {
            Mundo.disminuirFatigaMonturas()
        }

        init {
            load(5)
        }
    }

    class CheckearObjInteractivos : ServidorThread() {
        override fun exec() {
            Mundo.checkearObjInteractivos()
        }

        init {
            load(5)
        }
    }

    class MoverPavos : ServidorThread() {
        override fun exec() {
            Mundo.moverMonturas()
        }

        init {
            load(5)
        }
    }

    class MoverRecaudadores : ServidorThread() {
        override fun exec() {
            Mundo.moverRecaudadores()
        }

        init {
            load(5)
        }
    }

    class MoverMobs : ServidorThread() {
        override fun exec() {
            Mundo.moverMobs()
        }

        init {
            load(5)
        }
    }

    class ResetExpDia : ServidorThread() {
        override fun exec() {
            Mundo.resetExpDia()
        }

        init {
            load(5)
        }
    }

    class SubirEstrellas : ServidorThread() {
        override fun exec() {
            Mundo.subirEstrellasMobs(1)
        }

        init {
            load(5)
        }
    }

    class IniciarLoteria : ServidorThread() {
        override fun exec() {
            Mundo.iniciarLoteria()
        }

        init {
            load(5)
        }
    }

    class AumentarSegundos : ServidorThread() {
        override fun exec() {
            SincronizadorSocket.sendPacket("C" + ServidorServer.nroJugadoresLinea(), false)
        }

        init {
            ServidorServer._segundosON++
            load(5)
        }
    }

    class SortearLoteria : ServidorThread() {
        override fun exec() {
            Mundo.sortearBoletos()
        }

        init {
            load(5)
        }
    }

    class BorrarCuentaRegresiva : ServidorThread() {
        override fun exec() {
            if (MainServidor.SEGUNDOS_REBOOT_SERVER > 0) {
                Mundo.SEG_CUENTA_REGRESIVA = MainServidor.SEGUNDOS_REBOOT_SERVER
            }
            Mundo.MSJ_CUENTA_REGRESIVA = MainServidor.MENSAJE_TIMER_REBOOT
            GestorSalida.ENVIAR_bRS_PARAR_CUENTA_REGRESIVA_TODOS()
        }

        init {
            load(5)
        }
    }

    class AgregarMobInvasion : ServidorThread() {
        override fun exec() {
            Mundo.agregarMobIvancion()
        }

        init {
            load(5)
        }
    }

    class AgregarPortal : ServidorThread() {
        override fun exec() {
            Mundo.agregarPortal()
        }

        init {
            load(5)
        }
    }

    class AgregarNpcEvento : ServidorThread() {
        override fun exec() {
            Mundo.agregarNpcEvento()
        }

        init {
            load(5)
        }
    }

    class Koliseo : ServidorThread() {
        override fun exec() {
            Mundo.iniciarKoliseo()
            if (MainServidor.CANTIDAD_MIEMBROS_EQUIPO_KOLISEO_VS1 > 0) Mundo.iniciarKoliseo1()
            if (MainServidor.CANTIDAD_MIEMBROS_EQUIPO_KOLISEO_VS2 > 0) Mundo.iniciarKoliseo2()
        }

        init {
            load(5)
        }
    }

    class AgregarMobEvento : ServidorThread() {
        override fun exec() {
            Mundo.agregarMobEvento()
        }

        init {
            load(5)
        }
    }

    class ActualizarLadder : ServidorThread() {
        override fun exec() {
            Mundo.actualizarRankings()
        }

        init {
            load(5)
        }
    }

    class ActualizarPVP : ServidorThread() {
        override fun exec() {
            Mundo.actualizarLiderPVP()
        }

        init {
            load(5)
        }
    }

    class ActualizarKoliseo : ServidorThread() {
        override fun exec() {
            Mundo.actualizarLiderKoliseo()
        }

        init {
            load(5)
        }
    }

    /*
    public static class Koliseo extends ServidorThread {
        public Koliseo() {
            Mundo.SEGUNDOS_INICIO_KOLISEO--;
            load(5);
        }

        public void exec() {
            if (Mundo.SEGUNDOS_INICIO_KOLISEO == 60) {
                GestorSalida.ENVIAR_Im_INFORMACION_KOLISEO("1KOLISEO_1_MINUTO_INICIA");
            } else if (Mundo.SEGUNDOS_INICIO_KOLISEO == 5) {
                GestorSalida.ENVIAR_Im_INFORMACION_KOLISEO("1KOLISEO_5_SEGUNDOS_INICIA");
            } else if (Mundo.SEGUNDOS_INICIO_KOLISEO == 0) {
                Mundo.iniciarKoliseo();
            }
        }
    }*/
    class LiveAction : ServidorThread() {
        override fun exec() {
            GestorSQL.CARGAR_LIVE_ACTION()
            GestorSQL.VACIAR_LIVE_ACTION()
        }

        init {
            load(5)
        }
    }

    /*public static class ExpulsarInactivos extends ServidorThread {
        public ExpulsarInactivos() {
            load(5);
        }

        public void exec() {
            Mundo.expulsarInactivos();
        }
    }*/
    class GarbageCollector : ServidorThread() {
        override fun exec() {
            System.gc()
        }

        init {
            load(8)
        }
    }

    class MensajeReset(private val _str: String) : ServidorThread() {
        override fun exec() {
            GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS("115;$_str")
        }

        init {
            load(5)
        }
    }

    class Publicidad : ServidorThread() {
        private val _str: String
        override fun exec() {
            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(_str)
        }

        companion object {
            private var _nroPub = 0
        }

        init {
            if (MainServidor.PUBLICIDAD.isEmpty()) {
                return
            }
            _str = MainServidor.PUBLICIDAD.get(_nroPub)
            _nroPub++
            if (_nroPub >= MainServidor.PUBLICIDAD.size()) {
                _nroPub = 0
            }
            load(5)
        }
    }

    class AntiDDOS(segundos: Int) : ServidorThread() {
        private val _minConexionesXSeg = 5
        override fun exec() {
            if (!Mundo.BLOQUEANDO) {
                var ataque = true
                for (i in 0 until ServidorServer._conexiones.length) {
                    ataque = ataque and (ServidorServer._conexiones.get(i) > _minConexionesXSeg)
                }
                if (ataque) {
                    Mundo.BLOQUEANDO = true
                    MainServidor.redactarLogServidorln("SE ACTIVO EL BLOQUEO AUTOMATICO CONTRA ATAQUES")
                }
            } else if (Mundo.BLOQUEANDO) {
                var ataque = true
                for (i in 0 until ServidorServer._conexiones.length) {
                    ataque = ataque and (ServidorServer._conexiones.get(i) < _minConexionesXSeg)
                }
                if (ataque) {
                    try {
                        for (ss in ServidorServer.getClientes()) {
                            if (ss.getPersonaje() == null) {
                                // expulsa a los q no tienen personajes
                                GestorSalida.ENVIAR_M0_MENSAJE_BASICOS_SVR_MUESTRA_DISCONNECT(ss, "16", "", "")
                                ss.cerrarSocket(true, "Antiddos.run()")
                            }
                        }
                    } catch (e: Exception) {
                    }
                    Mundo.BLOQUEANDO = false
                    MainServidor.redactarLogServidorln("SE DESACTIVO EL BLOQUEO AUTOMATICO CONTRA ATAQUES")
                }
            }
            ServidorServer._j = (ServidorServer._j + 1) % 5
            ServidorServer._conexiones.get(ServidorServer._j) = 0
        }

        init {
            load(5)
        }
    }

    class DetectarDDOS : ServidorThread() {
        override fun exec() {
            try {
                if (MainServidor.URL_DETECTAR_DDOS.isEmpty()) {
                    return
                }
                val obj = URL(MainServidor.URL_DETECTAR_DDOS)
                val con: URLConnection = obj.openConnection()
                con.setRequestProperty("Content-type", "charset=Unicode")
                val `in` = BufferedReader(InputStreamReader(con.getInputStream()))
                while (`in`.readLine() != null) {
                    Thread.sleep(1)
                }
                `in`.close()
                if (!MainServidor.PARAM_JUGADORES_HEROICO_MORIR) {
                    MainServidor.redactarLogServidorln("============= SE HA FINALIZADO ATAQUE DDOS (" + Date()
                            + ") =============")
                    MainServidor.PARAM_JUGADORES_HEROICO_MORIR = true
                    GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS("1SERVER_RESTORING_ATTACK")
                }
            } catch (e1: MalformedURLException) {
                if (MainServidor.PARAM_JUGADORES_HEROICO_MORIR) {
                    MainServidor.redactarLogServidorln("============= SE DETECTO ATAQUE DDOS (" + Date() + ") =============")
                    MainServidor.PARAM_JUGADORES_HEROICO_MORIR = false
                    GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS("1SERVER_IS_BEING_ATTACKED")
                    Mundo.salvarServidor(false)
                }
            } catch (e1: UnknownHostException) {
                if (MainServidor.PARAM_JUGADORES_HEROICO_MORIR) {
                    MainServidor.redactarLogServidorln("============= SE DETECTO ATAQUE DDOS (" + Date() + ") =============")
                    MainServidor.PARAM_JUGADORES_HEROICO_MORIR = false
                    GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS("1SERVER_IS_BEING_ATTACKED")
                    Mundo.salvarServidor(false)
                }
            } catch (e: Exception) {
                MainServidor.redactarLogServidorln("EXCEPTION DE DETECTAR DDOS: " + e.toString())
                e.printStackTrace()
            }
        }

        init {
            load(5)
        }
    }

    companion object {
        private val ACTIVOS: HashMap<String, Boolean> = HashMap()
    }

    init {
        _nameClass = this.getClass().getSimpleName()
    }
}