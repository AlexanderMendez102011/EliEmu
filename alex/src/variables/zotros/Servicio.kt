package variables.zotros

import java.util.ArrayList
import java.util.regex.Pattern
import variables.gremio.Gremio
import variables.objeto.Objeto
import variables.objeto.ObjetoModelo.CAPACIDAD_STATS
import variables.personaje.Personaje
import estaticos.MainServidor
import estaticos.Constantes
import estaticos.Encriptador
import estaticos.GestorSQL
import estaticos.GestorSalida
import estaticos.Mundo

class Servicio(val iD: Int, private val _creditosSinAbono: Int, private val _ogrinasSinAbono: Long, private val _activado: Boolean, private val _creditosAbonado: Int, private val _ogrinasAbonado: Long) {
    private val _biPagoSinAbono: Boolean
    private val _biPagoAbonado: Boolean
    fun estaActivo(): Boolean {
        return _activado
    }

    fun string(abonado: Boolean): String {
        if (!_activado) {
            return ""
        }
        return if (abonado) {
            "$iD;$_creditosAbonado;$_ogrinasAbonado"
        } else {
            "$iD;$_creditosSinAbono;$_ogrinasSinAbono"
        }
    }

    private fun puede(_perso: Personaje): Boolean {
        if (!_activado) {
            return false
        }
        if (_perso.getCuenta().esAbonado()) {
            if (_biPagoAbonado) {
                when (_perso.getMedioPagoServicio()) {
                    1 -> {
                        return if (!GestorSQL.RESTAR_CREDITOS(_perso.getCuenta(), _creditosAbonado, _perso)) {
                            false
                        } else true
                    }
                    2 -> {
                        return if (!GestorSQL.RESTAR_OGRINAS1(_perso.getCuenta(), _ogrinasAbonado, _perso)) {
                            false
                        } else true
                    }
                }
            }
            if (_creditosAbonado > 0) {
                if (!GestorSQL.RESTAR_CREDITOS(_perso.getCuenta(), _creditosAbonado, _perso)) {
                    return false
                }
            } else if (_ogrinasAbonado > 0) {
                if (!GestorSQL.RESTAR_OGRINAS1(_perso.getCuenta(), _ogrinasAbonado, _perso)) {
                    return false
                }
            }
        } else {
            if (_biPagoSinAbono) {
                when (_perso.getMedioPagoServicio()) {
                    1 -> {
                        return if (!GestorSQL.RESTAR_CREDITOS(_perso.getCuenta(), _creditosSinAbono, _perso)) {
                            false
                        } else true
                    }
                    2 -> {
                        return if (!GestorSQL.RESTAR_OGRINAS1(_perso.getCuenta(), _ogrinasSinAbono, _perso)) {
                            false
                        } else true
                    }
                }
            }
            if (_creditosSinAbono > 0) {
                if (!GestorSQL.RESTAR_CREDITOS(_perso.getCuenta(), _creditosSinAbono, _perso)) {
                    return false
                }
            } else if (_ogrinasSinAbono > 0) {
                if (!GestorSQL.RESTAR_OGRINAS1(_perso.getCuenta(), _ogrinasSinAbono, _perso)) {
                    return false
                }
            }
        }
        return true
    }

    fun usarServicio(_perso: Personaje, packet: String) {
        if (!_activado) {
            return
        }
        try {
            when (iD) {
                Constantes.SERVICIO_CAMBIO_NOMBRE -> {
                    if (!_perso.estaDisponible(false, true)) {
                        return
                    }
                    if (!packet.isEmpty()) {
                        val params: Array<String> = packet.substring(3).split(";")
                        var nombre = params[0]
                        var colorN = 0
                        try {
                            colorN = Integer.parseInt(params[1])
                            if (colorN > 16777215) {
                                colorN = 0
                            }
                        } catch (e: Exception) {
                            return
                        }
                        if (nombre.equalsIgnoreCase(_perso.getNombre())) {
                            // si tiene el mismo nombre y diferente color
                            if (colorN == _perso.getColorNombre()) {
                                return
                            }
                        }
                        nombre = Personaje.nombreValido(nombre, false)
                        if (nombre == null) {
                            GestorSalida.ENVIAR_AAE_ERROR_CREAR_PJ(_perso, "a")
                            return
                        }
                        if (nombre.isEmpty()) {
                            GestorSalida.ENVIAR_AAE_ERROR_CREAR_PJ(_perso, "n")
                            return
                        }
                        if (!puede(_perso)) {
                            return
                        }
                        if (!MainServidor.MODO_IMPACT) _perso.setColorNombre(colorN)
                        _perso.cambiarNombre(nombre)
                    } else {
                        GestorSalida.enviar(_perso, "bN" + _perso.getColorNombre(), true)
                    }
                }
                Constantes.SERVICIO_CAMBIO_COLOR -> {
                    if (!_perso.estaDisponible(false, true)) {
                        return
                    }
                    if (!packet.isEmpty()) {
                        if (!puede(_perso)) {
                            return
                        }
                        val colores: Array<String> = packet.substring(3).split(";")
                        _perso.setColores(Integer.parseInt(colores[0]), Integer.parseInt(colores[1]), Integer.parseInt(colores[2]))
                        _perso.refrescarEnMapa()
                        GestorSalida.ENVIAR_bV_CERRAR_PANEL(_perso)
                    } else {
                        GestorSalida.enviar(_perso, "bC", true)
                    }
                }
                Constantes.SERVICIO_CAMBIO_SEXO -> {
                    if (!_perso.estaDisponible(false, true)) {
                        return
                    }
                    if (!puede(_perso)) {
                        return
                    }
                    _perso.cambiarSexo()
                    _perso.deformar()
                    _perso.refrescarEnMapa()
                    GestorSQL.CAMBIAR_SEXO_CLASE(_perso)
                    GestorSalida.ENVIAR_bV_CERRAR_PANEL(_perso)
                }
                Constantes.SERVICIO_REVIVIR -> {
                    if (_perso.getPelea() != null) {
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Estas em pelea en estos momentos")
                        return
                    }
                    if (!puede(_perso)) {
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Estas vivo, no puedes revivir")
                        return
                    }
                    _perso.revivir(true)
                    GestorSalida.ENVIAR_bV_CERRAR_PANEL(_perso)
                }
                Constantes.SERVICIO_TITULO_PERSONALIZADO -> if (!packet.isEmpty()) {
                    if (packet.substring(3).isEmpty()) {
                        _perso.setTituloVIP("")
                    } else {
                        val str: Array<String> = packet.substring(3).split(";")
                        val titulo = str[0]
                        var colorT = 0
                        try {
                            colorT = Integer.parseInt(str[1])
                            if (colorT > 16777215) {
                                colorT = 0
                            }
                        } catch (e: Exception) {
                            return
                        }
                        if (titulo.isEmpty() || titulo.length() > 25) {
                            return
                        }
                        val plantilla: String = (Encriptador.NUMEROS + Encriptador.ABC_MIN + Encriptador.ABC_MAY
                                + Encriptador.ESPACIO + Encriptador.GUIONES)
                        for (letra in titulo.toCharArray()) {
                            if (!plantilla.contains(letra.toString() + "")) {
                                return
                            }
                        }
                        if (!puede(_perso)) {
                            return
                        }
                        _perso.setTituloVIP("$titulo*$colorT")
                    }
                    _perso.refrescarEnMapa()
                    GestorSalida.ENVIAR_bV_CERRAR_PANEL(_perso)
                } else {
                    GestorSalida.enviar(_perso, "bÑ", true)
                }
                Constantes.SERVICIO_MIMOBIONTE -> if (!packet.isEmpty()) {
                    val split: Array<String> = packet.substring(3).split(Pattern.quote("|"))
                    val huesped: Objeto = _perso.getObjeto(Integer.parseInt(split[0]))
                    val mascara: Objeto = _perso.getObjeto(Integer.parseInt(split[1]))
                    if (huesped == null || mascara == null) {
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1OBJECT_DONT_EXIST")
                        return
                    }
                    if (huesped.getObjevivoID() !== 0 || mascara.getObjevivoID() !== 0) {
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1MIMOBIONTE_ERROR_TYPES")
                        return
                    }
                    if (huesped.getID() === mascara.getID()) {
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1MIMOBIONTE_ERROR_IDS")
                        return
                    }
                    val tipos = intArrayOf(Constantes.OBJETO_TIPO_AMULETO, Constantes.OBJETO_TIPO_ARCO, Constantes.OBJETO_TIPO_VARITA,
                            Constantes.OBJETO_TIPO_BASTON, Constantes.OBJETO_TIPO_DAGAS, Constantes.OBJETO_TIPO_ESPADA,
                            Constantes.OBJETO_TIPO_MARTILLO, Constantes.OBJETO_TIPO_PALA, Constantes.OBJETO_TIPO_ANILLO,
                            Constantes.OBJETO_TIPO_CINTURON, Constantes.OBJETO_TIPO_BOTAS, Constantes.OBJETO_TIPO_SOMBRERO,
                            Constantes.OBJETO_TIPO_CAPA, Constantes.OBJETO_TIPO_MASCOTA, Constantes.OBJETO_TIPO_HACHA,
                            Constantes.OBJETO_TIPO_PICO, Constantes.OBJETO_TIPO_GUADAÑA, Constantes.OBJETO_TIPO_MOCHILA,
                            Constantes.OBJETO_TIPO_ESCUDO)
                    var esTipo = false
                    for (t in tipos) {
                        if (t == huesped.getObjModelo().getTipo()) {
                            esTipo = true
                            break
                        }
                    }
                    if (!esTipo || huesped.getObjModelo().getTipo() !== mascara.getObjModelo().getTipo()) {
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1MIMOBIONTE_ERROR_TYPES")
                        return
                    }
                    if (huesped.getObjModelo().getNivel() < mascara.getObjModelo().getNivel() && MainServidor.MIMOVIOENTE_RESTRICION_NIVEL) {
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1MIMOBIONTE_ERROR_LEVELS")
                        return
                    }
                    if (MainServidor.ID_MIMOBIONTE !== -1) {
                        if (!_perso.tenerYEliminarObjPorModYCant(MainServidor.ID_MIMOBIONTE, 1)) {
                            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "14|43")
                            return
                        }
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "022;" + 1 + "~" + MainServidor.ID_MIMOBIONTE)
                    } else if (!puede(_perso)) {
                        GestorSalida.ENVIAR_BN_NADA(_perso, "MIMOBIONTE DESHABILITADO")
                        return
                    }
                    if (!_perso.restarCantObjOEliminar(mascara.getID(), 1, true)) {
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "14|43")
                        return
                    }
                    if (mascara.tieneStatTexto(Constantes.STAT_OBJETO_OCULTO_1)) {
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Recuerda que este objto perdera todos sus stats base.")
                    }
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "022;" + 1 + "~" + mascara.getObjModeloID())
                    val nuevaCantidad: Long = huesped.getCantidad() - 1
                    if (nuevaCantidad >= 1) {
                        val nuevo: Objeto = huesped.clonarObjeto(nuevaCantidad, Constantes.OBJETO_POS_NO_EQUIPADO)
                        _perso.addObjetoConOAKO(nuevo, true)
                        huesped.setCantidad(1)
                        GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, huesped)
                    }
                    huesped.addStatTexto(Constantes.STAT_APARIENCIA_OBJETO, "0#0#" + Integer.toHexString(mascara
                            .getObjModeloID()))
                    GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(_perso, huesped)
                } else {
                    GestorSalida.enviar(_perso, "bM", true)
                }
                Constantes.SERVICIO_CREA_TU_ITEM -> GestorSalida.ENVIAR_bB_PANEL_CREAR_ITEM(_perso)
                Constantes.SERVICIO_SISTEMA_ITEMS -> GestorSalida.ENVIAR_bSP_PANEL_ITEMS(_perso)
                Constantes.SERVICIO_CAMBIO_EMBLEMA -> {
                    if (!_perso.estaDisponible(false, true)) {
                        return
                    }
                    if (_perso.getMiembroGremio() == null || _perso.getMiembroGremio().getRango() !== 1) {
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1YOU_DONT_HAVE_GUILD")
                        return
                    }
                    if (!packet.isEmpty()) {
                        val infos: Array<String> = packet.substring(3).split(Pattern.quote("|"))
                        val escudoID: String = Integer.toString(Integer.parseInt(infos[0]), 36)
                        val colorEscudo: String = Integer.toString(Integer.parseInt(infos[1]), 36)
                        val emblemaID: String = Integer.toString(Integer.parseInt(infos[2]), 36)
                        val colorEmblema: String = Integer.toString(Integer.parseInt(infos[3]), 36)
                        val nombreGremio: String = infos[4].substring(0, 1).toUpperCase() + infos[4].substring(1).toLowerCase()
                        if (nombreGremio.length() < 2 || nombreGremio.length() > 20 || !_perso.getGremio().getNombre()
                                        .equalsIgnoreCase(nombreGremio) && Mundo.nombreGremioUsado(nombreGremio)) {
                            GestorSalida.ENVIAR_gC_CREAR_PANEL_GREMIO(_perso, "Ean")
                            return
                        }
                        var esValido = true
                        val abcMin = "abcdefghijklmnopqrstuvwxyz- '"
                        var cantSimbol: Byte = 0
                        var cantLetras: Byte = 0
                        var letra_A = ' '
                        var letra_B = ' '
                        for (letra in nombreGremio.toLowerCase().toCharArray()) {
                            if (!abcMin.contains(letra.toString() + "")) {
                                esValido = false
                                break
                            }
                            if (letra == letra_A && letra == letra_B) {
                                esValido = false
                                break
                            }
                            if (letra != '-') {
                                letra_A = letra_B
                                letra_B = letra
                                cantLetras++
                            } else {
                                if (cantLetras.toInt() == 0 || cantSimbol > 0) {
                                    esValido = false
                                    break
                                }
                                cantSimbol++
                            }
                        }
                        if (!esValido) {
                            GestorSalida.ENVIAR_gC_CREAR_PANEL_GREMIO(_perso, "Ean")
                            return
                        }
                        val emblema = "$escudoID,$colorEscudo,$emblemaID,$colorEmblema"
                        if (Mundo.emblemaGremioUsado(emblema)) {
                            GestorSalida.ENVIAR_gC_CREAR_PANEL_GREMIO(_perso, "Eae")
                            return
                        }
                        if (!puede(_perso)) {
                            return
                        }
                        val gremio: Gremio = _perso.getGremio()
                        gremio.setNombre(nombreGremio)
                        gremio.setEmblema(emblema)
                        if (_perso.getPelea() == null) {
                            GestorSalida.ENVIAR_Oa_CAMBIAR_ROPA_MAPA(_perso.getMapa(), _perso)
                        }
                        _perso.refrescarEnMapa()
                        GestorSalida.ENVIAR_bV_CERRAR_PANEL(_perso)
                    } else {
                        GestorSalida.enviar(_perso, "bG", true)
                    }
                }
                Constantes.SERVICIO_ESCOGER_NIVEL -> {
                    if (!_perso.estaDisponible(false, true)) {
                        return
                    }
                    if (!packet.isEmpty()) {
                        if (!puede(_perso)) {
                            return
                        }
                        val split: Array<String> = packet.substring(2).split(Pattern.quote("|"))
                        val nivel: Int = Integer.parseInt(split[0])
                        val alineacion: Byte = Byte.parseByte(split[1])
                        _perso.cambiarNivelYAlineacion(nivel, alineacion)
                    } else {
                        GestorSalida.ENVIAR_bA_ESCOGER_NIVEL(_perso)
                    }
                }
                Constantes.SERVICIO_TRANSFORMAR_MONTURA -> {
                    if (!_perso.estaDisponible(false, true)) {
                        return
                    }
                    if (_perso.getMontura() == null) {
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1104")
                        return
                    }
                    if (!packet.isEmpty()) {
                        val statsMontura: String = _perso.getMontura().getStats().convertirStatsAString()
                        var mascota: Objeto = Mundo.getObjetoModelo(Integer.parseInt(packet.substring(3))).crearObjeto(1,
                                Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.MAXIMO)
                        if (mascota.getObjModelo().getTipo() !== Constantes.OBJETO_TIPO_MASCOTA) {
                            _perso.getCuenta().setBaneado(true, -1)
                            _perso.getServidorSocket().cerrarSocket(true, "command EXPULSAR")
                            return
                        }
                        if (!puede(_perso)) {
                            return
                        }
                        mascota = _perso.getObjPosicion(Constantes.OBJETO_POS_MASCOTA)
                        if (mascota == null) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "No tiene una mascota puesta para asignarle los stats de la montura.")
                            return
                        }
                        mascota.convertirStringAStats(statsMontura)
                        GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(_perso, mascota)
                        GestorSQL.SALVAR_OBJETO(mascota)
                        GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso)
                        if (_perso.estaMontando()) {
                            _perso.subirBajarMontura(false)
                        }
                        Mundo.eliminarMontura(_perso.getMontura())
                        _perso.setMontura(null)
                    } else {
                        GestorSalida.ENVIAR_bm_TRANSFORMAR_MONTURA(_perso)
                    }
                }
                Constantes.SERVICIO_ALINEACION_MERCENARIO -> {
                    if (!_perso.estaDisponible(false, true)) {
                        return
                    }
                    if (_perso.getAlineacion() === Constantes.ALINEACION_MERCENARIO) {
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Ya eres un mercenario actualmente")
                        return
                    }
                    if (_perso.getDeshonor() >= 2) {
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "183")
                        return
                    }
                    if (!puede(_perso)) {
                        return
                    }
                    _perso.cambiarAlineacion(Constantes.ALINEACION_MERCENARIO, false)
                    GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Cambio de alineacion exitoso")
                }
                Constantes.SERVICIO_MONTURA_CAMALEON -> {
                    if (!_perso.estaDisponible(false, true)) {
                        return
                    }
                    if (_perso.getMontura() == null) {
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "No tienes una monturas equipada")
                        return
                    }
                    if (!puede(_perso)) {
                        return
                    }
                    _perso.getMontura().addHabilidad(Constantes.HABILIDAD_CAMALEON)
                    GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Se a aplicado la habilidad camaleon a tu montura")
                    GestorSalida.ENVIAR_Re_DETALLES_MONTURA(_perso, "+", _perso.getMontura())
                    GestorSQL.REPLACE_MONTURA(_perso.getMontura(), false)
                }
                Constantes.SERVICIO_RESET_NIVEL -> {
                    if (_perso.getEncarnacionN() != null) {
                        GestorSalida.ENVIAR_BN_NADA(_perso)
                        return
                    }
                    if (_perso.getResets() >= 15) {
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Un upsilon no puede usar este servico.")
                        return
                    }
                    if (_perso.getResets() <= 11) {
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Solo puedes descender hasta omega")
                        return
                    }
                    if (!_perso.estaDisponible(false, false)) {
                        GestorSalida.ENVIAR_BN_NADA(_perso)
                        return
                    }
                    if (!puede(_perso)) {
                        return
                    }
                    _perso.reiniciarReset()
                    GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Tu personaje a perdido 1 de sus resets")
                }
                Constantes.SERVICIO_ABONO_DIA, Constantes.SERVICIO_ABONO_SEMANA, Constantes.SERVICIO_ABONO_MES, Constantes.SERVICIO_ABONO_TRES_MESES -> {
                    if (!puede(_perso)) {
                        return
                    }
                    var dias = 1
                    dias = when (iD) {
                        Constantes.SERVICIO_ABONO_DIA -> 1
                        Constantes.SERVICIO_ABONO_SEMANA -> 7
                        Constantes.SERVICIO_ABONO_MES -> 30
                        Constantes.SERVICIO_ABONO_TRES_MESES -> 90
                        else -> return
                    }
                    var abonoD: Long = Math.max(GestorSQL.GET_ABONO(_perso.getCuenta().getNombre()), System.currentTimeMillis())
                    abonoD += dias * 24 * 3600 * 1000L
                    abonoD = Math.max(abonoD, System.currentTimeMillis() - 1000)
                    GestorSQL.SET_ABONO(abonoD, _perso.getCuentaID())
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1NUEVO_ABONO;$dias")
                }
                Constantes.SERVICIO_TRANSFORMAR_MASCOTA -> {
                    if (_perso.getObjPosicion(Constantes.OBJETO_POS_MASCOTA) == null) {
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "No tiene la mascota puesta que vas a convertir")
                        return
                    }
                    if (_perso.getMontura() == null) {
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "No tienes puesta la montura a la cual le asignaras los stats")
                        return
                    }
                    if (!_perso.getMontura().get_statsString().isEmpty()) {
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Ya esta montura fue transformada")
                        return
                    }
                    if (!puede(_perso)) {
                        return
                    }
                    if (_perso.convertirMascota()) {
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Mascota convertia con exito")
                    }
                }
                101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118 -> {
                    if (!_perso.estaDisponible(false, true)) {
                        return
                    }
                    if (_perso.getEncarnacionN() != null) {
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1NO_PUEDES_CAMBIAR_CLASE")
                        return
                    }
                    if (!puede(_perso)) {
                        return
                    }
                    val clase = (iD - 100).toByte()
                    val hechizosAprender: ArrayList<Integer> = ArrayList<Integer>()
                    for (hechizo in MainServidor.HECHIZOS_NO_BORRAR) {
                        if (_perso.tieneHechizoID(hechizo)) {
                            hechizosAprender.add(hechizo)
                        }
                    }
                    for (hechizo in MainServidor.HECHIZOS_CLASE_UNICOS.values()) {
                        if (_perso.tieneHechizoID2(hechizo)) {
                            hechizosAprender.add(hechizo)
                        }
                    }
                    _perso.cambiarClase(clase)
                    for (hechizo in hechizosAprender) {
                        _perso.fijarNivelHechizoOAprender(hechizo, 1, true)
                    }
                    GestorSalida.ENVIAR_bV_CERRAR_PANEL(_perso)
                    GestorSalida.ENVIAR_bOC_ABRIR_PANEL_ROSTRO(_perso)
                }
            }
        } catch (e: Exception) {
            GestorSalida.ENVIAR_BN_NADA(_perso, "EXCEPTION USAR SERVICIO")
            MainServidor.redactarLogServidorln("EXCEPTION Packet " + packet + ", usarServicios " + e.toString())
            e.printStackTrace()
        }
    }

    init {
        _biPagoSinAbono = _creditosSinAbono > 0 && _ogrinasSinAbono > 0
        _biPagoAbonado = _creditosAbonado > 0 && _ogrinasAbonado > 0
    }
}