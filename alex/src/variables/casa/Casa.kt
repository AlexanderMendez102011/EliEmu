package variables.casa

import java.util.ArrayList
import java.util.HashMap
import java.util.Map
import variables.gremio.Gremio
import variables.mapa.Mapa
import variables.personaje.Personaje
import estaticos.Constantes
import estaticos.GestorSQL
import estaticos.GestorSalida
import estaticos.MainServidor
import estaticos.Mundo

class Casa(val iD: Int, val mapaIDFuera: Short, val celdaIDFuera: Short, var mapaIDDentro: Short,
           var celdaIDDentro: Short, precio: Long, mapasContenidos: String) {
    var actParametros = false
        private set
    private var _dueñoID: Personaje? = null
    private var _gremio: Gremio? = null
    var derechosGremio = 0
        private set
    var kamasVenta: Long = 1000000
        private set
    var clave = "-"
        private set
    private val _tieneDerecho: Map<Integer, Boolean> = HashMap<Integer, Boolean>()
    private val _mapasContenidos: ArrayList<Integer> = ArrayList<Integer>()
    fun actualizarCasa(dueño: Int, precio: Long, bloqueado: Byte, clave: String,
                       derechos: Int) {
        _dueñoID = Mundo.getPersonaje(dueño)
        kamasVenta = precio
        this.clave = clave
        actParametros = bloqueado.toInt() == 1
        if (actParametros && _dueñoID != null) {
            _gremio = _dueñoID.getGremio()
            if (_gremio == null) {
                actParametros = false
            }
        }
        derechosGremio = derechos
        analizarDerechos(derechosGremio)
    }

    val mapasContenidos: ArrayList<Integer>
        get() = _mapasContenidos
    val dueño: Personaje?
        get() = _dueñoID

    fun esSuCasa(id: Int): Boolean {
        return if (_dueñoID != null && _dueñoID.getID() === id) {
            true
        } else false
    }

    fun resetear() {
        _dueñoID = null
        _gremio = null
        kamasVenta = 1000000
        clave = "-"
        actParametros = false
        actualizarDerechos(0)
    }

    val gremioID: Int
        get() = if (_gremio == null) 0 else _gremio.getID()

    fun nullearGremio() {
        _gremio = null
    }

    fun tieneDerecho(derecho: Int): Boolean {
        return _tieneDerecho[derecho]!!
    }

    fun iniciarDerechos() {
        _tieneDerecho.clear()
        _tieneDerecho.put(Constantes.C_VISIBLE_PARA_GREMIO, false)
        _tieneDerecho.put(Constantes.C_ESCUDO_VISIBLE_MIEMBROS, false)
        _tieneDerecho.put(Constantes.C_ESCUDO_VISIBLE_PARA_TODOS, false)
        _tieneDerecho.put(Constantes.C_ACCESOS_MIEMBROS_SIN_CODIGO, false)
        _tieneDerecho.put(Constantes.C_ACCESO_PROHIBIDO_NO_MIEMBROS, false)
        _tieneDerecho.put(Constantes.C_ACCESOS_COFRES_MIEMBROS_SIN_CODIGO, false)
        _tieneDerecho.put(Constantes.C_ACCESO_PROHIBIDO_COFRES_NO_MIEMBROS, false)
        _tieneDerecho.put(Constantes.C_TELEPORT_GREMIO, false)
        _tieneDerecho.put(Constantes.C_DESCANSO_GREMIO, false)
    }

    fun analizarDerechos(derechos: Int) {
        iniciarDerechos()
        for (i in 0..7) {
            val exp = Math.pow(2, i) as Int
            if (exp and derechos == exp) {
                _tieneDerecho.put(exp, true)
            }
        }
    }

    fun intentarAcceder(perso: Personaje, clave: String): Boolean {
        if (!perso.estaDisponible(false, true)) {
            GestorSalida.ENVIAR_BN_NADA(perso)
            return false
        }
        val esDelGremio = perso.getGremio() != null && perso.getGremio().getID() === gremioID
        if (actParametros && !esDelGremio && tieneDerecho(Constantes.C_ACCESO_PROHIBIDO_NO_MIEMBROS)) { //
            GestorSalida.ENVIAR_Im_INFORMACION(perso, "1101")
            return false
        } else if (clave.isEmpty()) {
            if (esSuCasa(perso.getID()) || clave.equals("-") || actParametros && esDelGremio && tieneDerecho(
                            Constantes.C_ACCESOS_MIEMBROS_SIN_CODIGO)) {
                perso.teleport(mapaIDDentro, celdaIDDentro)
                return false
            } else {
                ponerClave(perso, false)
            }
        } else {
            if (clave.equals(this.clave)) {
                cerrarVentanaClave(perso)
                perso.teleport(mapaIDDentro, celdaIDDentro)
            } else {
                GestorSalida.ENVIAR_KKE_ERROR_CLAVE(perso)
            }
        }
        return true
    }

    fun expulsar(perso: Personaje, packet: String?) {
        if (!esSuCasa(perso.getID())) {
            GestorSalida.ENVIAR_BN_NADA(perso)
        } else {
            try {
                val objetivo: Personaje = Mundo.getPersonaje(Integer.parseInt(packet))
                if (objetivo == null || !objetivo.enLinea() || objetivo.getPelea() != null || objetivo.getMapa()
                                .getID() !== perso.getMapa().getID()) {
                    return
                }
                objetivo.teleport(mapaIDFuera, celdaIDFuera)
                GestorSalida.ENVIAR_Im_INFORMACION(objetivo, "018;" + perso.getNombre())
            } catch (e: Exception) {
            }
        }
    }

    fun quitarCerrojo(perso: Personaje) {
        if (esSuCasa(perso.getID())) {
            clave = "-"
            // _actParametros = false;
            GestorSalida.ENVIAR_hL_INFO_CASA(perso, informacionCasa(perso.getID()))
        } else {
            GestorSalida.ENVIAR_BN_NADA(perso)
        }
    }

    fun ponerClave(perso: Personaje, modificarClave: Boolean) {
        perso.setConsultarCasa(this)
        GestorSalida.ENVIAR_KCK_VENTANA_CLAVE(perso, modificarClave, 8.toByte()) // para bloquear clave
    }

    fun cerrarVentanaClave(perso: Personaje) {
        perso.setConsultarCasa(null)
        GestorSalida.ENVIAR_KV_CERRAR_VENTANA_CLAVE(perso)
    }

    fun modificarClave(perso: Personaje, packet: String) {
        if (packet.isEmpty()) {
            return
        }
        if (esSuCasa(perso.getID())) {
            clave = packet
            // _actParametros = false;
            if (packet.length() > 8) {
                clave = packet.substring(0, 8)
            }
            GestorSalida.ENVIAR_hL_INFO_CASA(perso, informacionCasa(perso.getID()))
        }
        cerrarVentanaClave(perso)
    }

    fun comprarCasa(perso: Personaje) {
        if (esSuCasa(perso.getID()) || Mundo.getCasaDePj(perso.getID()) != null) {
            GestorSalida.ENVIAR_Im_INFORMACION(perso, "132;1")
            return
        }
        if (kamasVenta <= 0 || perso.getKamas() < kamasVenta) {
            GestorSalida.ENVIAR_Im_INFORMACION(perso, "1CANT_BUY_HOUSE;" + kamasVenta)
            return
        }
        perso.addKamas(-kamasVenta, true, true)
        var kamasCofre: Long = 0
        for (cofre in Mundo.getCofresPorCasa(this)) {
            try {
                cofre.moverCofreABanco(_dueñoID.getCuenta())
            } catch (e: Exception) {
            }
            kamasCofre += cofre.getKamas()
            cofre.setKamasCero()
            cofre.setClave("-")
            cofre.setDueñoID(perso.getID())
            GestorSQL.REPLACE_COFRE(cofre, false)
        }
        try {
            _dueñoID.addKamasBanco(kamasVenta + kamasCofre)
            val tempPerso: Personaje = _dueñoID.getCuenta().getTempPersonaje()
            if (tempPerso != null) {
                GestorSalida.ENVIAR_M1_MENSAJE_SERVER_SVR_MUESTRA_INSTANTANEO(tempPerso, 5, "$kamasVenta;" + perso
                        .getNombre(), "")
            } else {
                _dueñoID.getCuenta().addMensaje("M15|" + kamasVenta + ";" + perso.getNombre() + "|", true)
            }
        } catch (e: Exception) {
        }
        _dueñoID = perso
        kamasVenta = 0
        clave = "-"
        actParametros = false
        _gremio = null
        actualizarDerechos(0)
        cerrarVentanaCompra(perso)
        GestorSalida.ENVIAR_hL_INFO_CASA(perso, informacionCasa(perso.getID()))
        for (p in Mundo.getMapa(mapaIDFuera).getArrayPersonajes()) {
            GestorSalida.ENVIAR_hP_PROPIEDADES_CASA(p, propiedadesPuertaCasa(p))
        }
    }

    fun modificarPrecioVenta(perso: Personaje, packet: String?) {
        if (esSuCasa(perso.getID())) {
            val precio: Int = Integer.parseInt(packet)
            if (precio < 0) {
                GestorSalida.ENVIAR_BN_NADA(perso)
                return
            }
            kamasVenta = precio.toLong()
            GestorSalida.ENVIAR_hV_CERRAR_VENTANA_COMPRA_CASA(perso)
            GestorSalida.ENVIAR_hSK_FIJAR_PRECIO_CASA(perso, iD.toString() + "|" + kamasVenta)
            for (p in Mundo.getMapa(mapaIDFuera).getArrayPersonajes()) {
                GestorSalida.ENVIAR_hP_PROPIEDADES_CASA(p, propiedadesPuertaCasa(p))
            }
            GestorSalida.ENVIAR_hL_INFO_CASA(perso, informacionCasa(perso.getID()))
        }
    }

    fun abrirVentanaCompraVentaCasa(perso: Personaje) {
        perso.setConsultarCasa(this)
        GestorSalida.ENVIAR_hCK_VENTANA_COMPRA_VENTA_CASA(perso, iD.toString() + "|" + kamasVenta)
    }

    fun cerrarVentanaCompra(perso: Personaje) {
        GestorSalida.ENVIAR_hV_CERRAR_VENTANA_COMPRA_CASA(perso)
        perso.setConsultarCasa(null)
    }

    fun analizarCasaGremio(perso: Personaje, packet: String) {
        if (!esSuCasa(perso.getID())) {
            GestorSalida.ENVIAR_BN_NADA(perso)
            return
        }
        try {
            when (packet) {
                "+" -> {
                    val gremio: Gremio = _dueñoID.getGremio() ?: return
                    if (Mundo.cantCasasGremio(gremio.getID()) >= Math.ceil(gremio.getNivel() / 10f) as Byte) {
                        GestorSalida.ENVIAR_Im_INFORMACION(perso, "1151")
                        return
                    } else if (gremio.getCantidadMiembros() < 10) {
                        GestorSalida.ENVIAR_Im_INFORMACION(perso, "1NOT_ENOUGHT_MEMBERS_IN_GUILD")
                        return
                    }
                    _gremio = gremio
                    actParametros = true
                }
                "-", "0" -> {
                    _gremio = null
                    actParametros = false
                }
                "" -> {
                }
                else -> try {
                    actualizarDerechos(Integer.parseInt(packet))
                } catch (e: Exception) {
                }
            }
            GestorSalida.ENVIAR_hG_DERECHOS_GREMIO_CASA(perso, iD.toString() + if (actParametros && _gremio != null) ";" + _gremio.getNombre().toString() + ";" + _gremio.getEmblema().toString() + ";" + derechosGremio else "")
        } catch (e: Exception) {
            GestorSalida.ENVIAR_BN_NADA(perso, "EXCEPTION ANALIZAR CASA GREMIO")
            MainServidor.redactarLogServidorln("EXCEPTION Packet " + packet + ", analizarCasaGremio " + e.toString())
            e.printStackTrace()
        }
    }

    fun actualizarDerechos(derechos: Int) {
        derechosGremio = derechos
        analizarDerechos(derechosGremio)
    }

    // poner el mensaje de condicin cuando el mob tiene condicino
    // probar los canales en reconeccion
    // poner finalize al map q se crea para la pelea
    //
    fun propiedadesPuertaCasa(perso: Personaje): String {
        val packet = StringBuilder("$iD|")
        try {
            packet.append(_dueñoID.getNombre())
        } catch (e: Exception) {
        }
        packet.append(";" + if (kamasVenta > 0) 1 else 0)
        val esDelGremio = perso.getGremio() != null && perso.getGremio().getID() === gremioID
        if (_gremio != null) {
            if (_gremio.getCantidadMiembros() < 10) {
                _gremio = null
            } else if (tieneDerecho(Constantes.C_ESCUDO_VISIBLE_PARA_TODOS) || tieneDerecho(
                            Constantes.C_ESCUDO_VISIBLE_MIEMBROS) && esDelGremio) {
                packet.append(";" + _gremio.getNombre().toString() + ";" + _gremio.getEmblema())
            }
        }
        return packet.toString()
    }

    fun informacionCasa(id: Int): String {
        return ((if (esSuCasa(id)) "+" else "-") + "|" + iD + ";" + (if (clave.equals("-")) 0 else 1) + ";" + (if (kamasVenta > 0) 1 else 0)
                + ";" + if (derechosGremio > 0) 1 else 0)
    }

    companion object {
        fun stringCasaGremio(gremioID: Int): String {
            val packet = StringBuilder()
            for (casa in Mundo.getCasas().values()) {
                if (casa.gremioID == gremioID && casa.derechosGremio > 0) {
                    if (packet.length() > 0) {
                        packet.append("|")
                    }
                    packet.append(casa.iD.toString() + ";")
                    try {
                        packet.append(casa.dueño.getNombre().toString() + ";")
                    } catch (e: Exception) {
                        packet.append("?;")
                    }
                    val mapa: Mapa = Mundo.getMapa(casa.mapaIDDentro)
                    packet.append(mapa.getX().toString() + "," + mapa.getY() + ";")
                    packet.append("0;")
                    packet.append(casa.derechosGremio)
                }
            }
            return if (packet.length() === 0) {
                ""
            } else "+" + packet.toString()
        }
    }

    init {
        kamasVenta = precio
        for (str in mapasContenidos.split(";")) {
            try {
                _mapasContenidos.add(Integer.parseInt(str))
            } catch (e: Exception) {
            }
        }
        _mapasContenidos.trimToSize()
    }
}