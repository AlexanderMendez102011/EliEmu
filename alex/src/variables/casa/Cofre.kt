package variables.casa

import java.util.ArrayList

class Cofre(/*private void objeto_Desasociar_Mimobionte(Objeto objeto) {
           try {
               if(objeto.tieneStatTexto(Constantes.STAT_APARIENCIA_OBJETO)) {
                   int regresar = Integer.parseInt(objeto.getParamStatTexto(Constantes.STAT_APARIENCIA_OBJETO, 3), 16);
                   Objeto apariencia = Mundo.getObjetoModelo(regresar).crearObjeto(1,Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM);
                   Mundo.addObjeto(apariencia, true);
                   objeto.addStatTexto(Constantes.STAT_APARIENCIA_OBJETO, "");
                   _objetos.put(apariencia.getID(), apariencia);
                   GestorSQL.SALVAR_OBJETO(objeto);
                   System.out.println("Cofre: "+_id+" disasicion objeto: "+objeto.getObjModelo().getNombre());
               }
           } catch (Exception e) {
           }
       }*/val iD: Int, val casaID: Int, val mapaID: Int, val celdaID: Short, limite: Int) : Exchanger {

    var dueñoID = 0
    private val _limite: Int
    var kamas: Long = 0
        private set
    var clave = "-"
    private val _objetos: Map<Integer, Objeto> = HashMap<Integer, Objeto>()
    private val _consultores: ArrayList<Personaje> = ArrayList()
    fun actualizarCofre(objetos: String, kamas: Long, clave: String, dueñoID: Int) {
        for (str in objetos.split(Pattern.quote("|"))) {
            try {
                if (str.isEmpty()) {
                    continue
                }
                val infos: Array<String> = str.split(":")
                val objetoID: Int = Integer.parseInt(infos[0])
                val objeto: Objeto = Mundo.getObjeto(objetoID) ?: continue
                //objeto_Desasociar_Mimobionte(objeto);
                _objetos.put(objetoID, objeto)
            } catch (e: Exception) {
            }
        }
        addKamas(kamas, null)
        this.clave = clave
        this.dueñoID = dueñoID
    }

    fun setKamasCero() {
        kamas = 0
    }

    fun addKamas(kamas: Long, perso: Personaje?) {
        if (kamas == 0L) {
            return
        }
        this.kamas += kamas
        if (this.kamas < 0) {
            this.kamas = 0
        }
    }

    fun intentarAcceder(perso: Personaje, clave: String): Boolean {
        if (!perso.estaDisponible(false, true)) {
            GestorSalida.ENVIAR_BN_NADA(perso, "INTENTA COFRE 1")
            return false
        }
        val casa: Casa = Mundo.getCasa(casaID)
        if (casa == null) {
            abrirCofre(perso)
        } else {
            val esDelGremio = perso.getGremio() != null && perso.getGremio().getID() === casa.getGremioID()
            if (casa.getActParametros() && !esDelGremio && casa.tieneDerecho(
                            Constantes.C_ACCESO_PROHIBIDO_COFRES_NO_MIEMBROS)) {
                GestorSalida.ENVIAR_Im_INFORMACION(perso, "1101")
                return false
            } else if (clave.isEmpty()) {
                if (esSuCofreOPublico(perso) || clave.equals("-") || casa.getActParametros() && esDelGremio && casa
                                .tieneDerecho(Constantes.C_ACCESOS_COFRES_MIEMBROS_SIN_CODIGO)) {
                    abrirCofre(perso)
                } else {
                    ponerClave(perso, false) // para insertar clave
                }
            } else {
                if (clave.equals(this.clave)) {
                    cerrarVentanaClave(perso)
                    abrirCofre(perso)
                } else {
                }
                GestorSalida.ENVIAR_KKE_ERROR_CLAVE(perso)
            }
        }
        return true
    }

    fun modificarClave(perso: Personaje, packet: String) {
        if (packet.isEmpty()) {
            return
        }
        if (dueñoID == perso.getID()) {
            clave = packet
        }
        cerrarVentanaClave(perso)
    }

    fun ponerClave(perso: Personaje, modificarClave: Boolean) {
        perso.setConsultarCofre(this)
        GestorSalida.ENVIAR_KCK_VENTANA_CLAVE(perso, modificarClave, 8.toByte()) // para bloquear
    }

    fun cerrarVentanaClave(perso: Personaje) {
        perso.setConsultarCofre(null)
        GestorSalida.ENVIAR_KV_CERRAR_VENTANA_CLAVE(perso)
    }

    fun abrirCofre(perso: Personaje) {
        if (!_consultores.contains(perso)) {
            _consultores.add(perso)
        }
        perso.setExchanger(this)
        perso.setTipoExchange(Constantes.INTERCAMBIO_TIPO_COFRE)
        GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(perso, Constantes.INTERCAMBIO_TIPO_COFRE, "")
        GestorSalida.ENVIAR_EL_LISTA_EXCHANGER(perso, this)
    }

    fun esSuCofreOPublico(perso: Personaje): Boolean {
        return dueñoID == perso.getID() || dueñoID == -1
    }

    val objetos: Collection<Any>
        get() = _objetos.values()

    fun getListaExchanger(perso: Personaje?): String {
        val packet = StringBuilder()
        for (objeto in _objetos.values()) {
            if (objeto == null) {
                continue
            }
            packet.append("O" + objeto.stringObjetoConGuiño())
        }
        if (kamas > 0) {
            packet.append("G$kamas")
        }
        return packet.toString()
    }

    fun analizarObjetoCofreABD(): String {
        val str = StringBuilder()
        for (objeto in _objetos.values()) {
            if (objeto == null) {
                continue
            }
            if (str.length() > 0) {
                str.append("|")
            }
            str.append(objeto.getID())
        }
        return str.toString()
    }

    fun addObjetoRapido(obj: Objeto?) {
        if (obj == null) {
            return
        }
        _objetos.put(obj.getID(), obj)
    }

    @Override
    @Synchronized
    fun addObjetoExchanger(objeto: Objeto, cantidad: Long, perso: Personaje, precio: Int) {
        var cantidad = cantidad
        if (casaID != -1) {
            if (objeto.tieneStatTexto(Constantes.STAT_LIGADO_A_CUENTA)) {
                GestorSalida.ENVIAR_BN_NADA(perso, "INTERCAMBIO MOVER LIGADO")
                return
            }
            if (!objeto.pasoIntercambiableDesde()) {
                GestorSalida.ENVIAR_BN_NADA(perso, "INTERCAMBIO MOVER NO INTERCAMBIABLE")
                return
            }
        }
        if (_objetos.size() >= _limite) {
            GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Llegaste al máximo de objetos que puede soportar este cofre",
                    Constantes.COLOR_ROJO)
            return
        }
        if (!perso.tieneObjetoID(objeto.getID()) || objeto.getPosicion() !== Constantes.OBJETO_POS_NO_EQUIPADO) {
            GestorSalida.ENVIAR_Im_INFORMACION(perso, "1OBJECT_DONT_EXIST")
            return
        }
        if (cantidad > objeto.getCantidad()) {
            cantidad = objeto.getCantidad()
        }
        var str = ""
        var cofreObj: Objeto? = objetoSimilarEnElCofre(objeto)
        val nuevaCant: Long = objeto.getCantidad() - cantidad
        if (cofreObj == null) {
            if (nuevaCant <= 0) {
                perso.borrarOEliminarConOR(objeto.getID(), false)
                _objetos.put(objeto.getID(), objeto)
                str = "O+" + objeto.getID().toString() + "|" + objeto.getCantidad().toString() + "|" + objeto.getObjModeloID().toString() + "|" + objeto
                        .convertirStatsAString(false)
            } else {
                cofreObj = objeto.clonarObjeto(cantidad, Constantes.OBJETO_POS_NO_EQUIPADO)
                Mundo.addObjeto(cofreObj, false)
                _objetos.put(cofreObj.getID(), cofreObj)
                objeto.setCantidad(nuevaCant)
                str = "O+" + cofreObj.getID().toString() + "|" + cofreObj.getCantidad().toString() + "|" + cofreObj.getObjModeloID().toString() + "|" + cofreObj
                        .convertirStatsAString(false)
                GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(perso, objeto)
            }
        } else {
            if (nuevaCant <= 0) {
                perso.borrarOEliminarConOR(objeto.getID(), true)
                cofreObj.setCantidad(cofreObj.getCantidad() + objeto.getCantidad())
                str = "O+" + cofreObj.getID().toString() + "|" + cofreObj.getCantidad().toString() + "|" + cofreObj.getObjModeloID().toString() + "|" + cofreObj
                        .convertirStatsAString(false)
            } else {
                objeto.setCantidad(nuevaCant)
                cofreObj.setCantidad(cofreObj.getCantidad() + cantidad)
                str = "O+" + cofreObj.getID().toString() + "|" + cofreObj.getCantidad().toString() + "|" + cofreObj.getObjModeloID().toString() + "|" + cofreObj
                        .convertirStatsAString(false)
                GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(perso, objeto)
            }
        }
        for (pj in _consultores) {
            GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(pj, str)
        }
    }

    @Override
    @Synchronized
    fun remObjetoExchanger(objeto: Objeto, cantidad: Long, perso: Personaje, precio: Int) {
        var cantidad = cantidad
        if (!_objetos.containsKey(objeto.getID())) {
            GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(perso, "O-" + objeto.getID())
            return
        }
        if (cantidad > objeto.getCantidad()) {
            cantidad = objeto.getCantidad()
        }
        val str: String
        val nuevaCant: Long = objeto.getCantidad() - cantidad
        str = if (nuevaCant < 1) {
            _objetos.remove(objeto.getID())
            perso.addObjIdentAInventario(objeto, true)
            "O-" + objeto.getID()
        } else {
            val nuevoObj: Objeto = objeto.clonarObjeto(cantidad, Constantes.OBJETO_POS_NO_EQUIPADO)
            perso.addObjIdentAInventario(nuevoObj, true)
            objeto.setCantidad(nuevaCant)
            "O+" + objeto.getID().toString() + "|" + objeto.getCantidad().toString() + "|" + objeto.getObjModeloID().toString() + "|" + objeto
                    .convertirStatsAString(false)
        }
        for (pj in _consultores) {
            GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(pj, str)
        }
    }

    @Synchronized
    private fun objetoSimilarEnElCofre(objeto: Objeto): Objeto? {
        if (objeto.puedeTenerStatsIguales()) {
            for (obj in _objetos.values()) {
                if (Constantes.esPosicionEquipamiento(obj.getPosicion())) {
                    continue
                }
                if (objeto.getID() !== obj.getID() && obj.getObjModeloID() === objeto.getObjModeloID() && obj.sonStatsIguales(
                                objeto)) {
                    return obj
                }
            }
        }
        return null
    }

    @Synchronized
    fun limpiarCofre() {
        for (obj in _objetos.entrySet()) {
            Mundo.eliminarObjeto(obj.getKey())
        }
        _objetos.clear()
    }

    @Synchronized
    fun moverCofreABanco(cuenta: Cuenta) {
        for (obj in _objetos.values()) {
            cuenta.addObjetoAlBanco(obj)
        }
        _objetos.clear()
    }

    @Override
    fun cerrar(perso: Personaje, exito: String?) {
        _consultores.remove(perso)
        perso.cerrarVentanaExchange(exito)
    }

    @Override
    fun botonOK(perso: Personaje?) {
    }

    companion object {
        fun insertarCofre(mapaID: Int, celdaID: Short): Cofre? {
            return try {
                val casa: Casa = Mundo.getCasaDentroPorMapa(mapaID) ?: return null
                val c: Cofre = Mundo.getCofrePorUbicacion(mapaID, celdaID)
                if (c != null) {
                    return null
                }
                if (Mundo.getMapa(mapaID).getCelda(celdaID).getObjetoInteractivo() == null) {
                    return null
                }
                var id: Int = GestorSQL.GET_COFRE_POR_MAPA_CELDA(mapaID, celdaID)
                if (id == -1) {
                    GestorSQL.INSERT_COFRE_MODELO(casa.getID(), mapaID, celdaID)
                    id = GestorSQL.GET_COFRE_POR_MAPA_CELDA(mapaID, celdaID)
                    if (id == -1) {
                        return null
                    }
                }
                val cofre = Cofre(id, casa.getID(), mapaID, celdaID, MainServidor.LIMITE_OBJETOS_COFRE)
                cofre.actualizarCofre("", 0, "-", if (casa.getDueño() != null) casa.getDueño().getID() else 0)
                Mundo.addCofre(cofre)
                GestorSQL.REPLACE_COFRE(cofre, false)
                cofre
            } catch (e: Exception) {
                null
            }
        }
    }

    init {
        if (iD <= 0) {
            dueñoID = -1
        }
        _limite = limite
    }
}