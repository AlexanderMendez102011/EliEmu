package variables.mercadillo

import java.util.ArrayList

class Mercadillo(val iD: Int, mapaID: String, tasa: Int, tiempoVenta: Short,
                 maxObjCuenta: Short, nivelMax: Short, tipoObj: String) : Exchanger {
    val porcentajeImpuesto: Int

    //
    // public float getImpuesto() {
    // return _porcMercadillo / 100f;
    // }
    val tiempoVenta: Short = 0
    val maxObjCuenta: Short
    val nivelMax: Short
    val tipoObjPermitidos: String
    private val _mapas: ArrayList<Integer> = ArrayList<Integer>()
    private val _objMercadillos: CopyOnWriteArrayList<ObjetoMercadillo> = CopyOnWriteArrayList()
    private val _tipoObjetos: Map<Integer, TipoObjetos?> = HashMap<Integer, TipoObjetos>()
    private val _lineas: Map<Integer, Duo<Integer, Integer>> = HashMap<Integer, Duo<Integer, Integer>>()
    val mapas: ArrayList<Integer>
        get() = _mapas

    fun getListaExchanger(perso: Personaje): String {
        val packet = StringBuilder()
        for (objMerca in objetosMercadillos) {
            if (objMerca == null) {
                continue
            }
            if (objMerca.getCuentaID() !== perso.getCuentaID()) {
                continue
            }
            if (packet.length() > 0) {
                packet.append("|")
            }
            packet.append(objMerca.analizarParaEL())
        }
        return packet.toString()
    }

    fun strListaLineasPorModelo(modeloID: Int): String {
        return try {
            val tipo: Int = Mundo.getObjetoModelo(modeloID).getTipo()
            _tipoObjetos[tipo].getModelo(modeloID).strLineasPorObjMod()
        } catch (e: Exception) {
            ""
        }
    }

    fun hayModeloEnEsteMercadillo(tipo: Int, modeloID: Int): Boolean {
        return _tipoObjetos[tipo].getModelo(modeloID) != null
    }

    fun stringModelo(tipoObj: Int): String {
        return _tipoObjetos[tipoObj].stringModelo()
    }

    fun esTipoDeEsteMercadillo(tipoObj: Int): Boolean {
        return _tipoObjetos[tipoObj] != null
    }

    fun getLinea(lineaID: Int): LineaMercadillo? {
        return try {
            val tipoObj: Int = _lineas[lineaID]._primero
            val modeloID: Int = _lineas[lineaID]._segundo
            _tipoObjetos[tipoObj].getModelo(modeloID).getLinea(lineaID)
        } catch (e: Exception) {
            MainServidor.redactarLogServidorln("EXCEPTION getLinea linea: $lineaID")
            e.printStackTrace()
            null
        }
    }

    val objetosMercadillos: CopyOnWriteArrayList<ObjetoMercadillo>
        get() = _objMercadillos

    fun addObjMercaAlPuesto(objMerca: ObjetoMercadillo) {
        if (objMerca.getObjeto() == null) {
            MainServidor.redactarLogServidorln("Objeto del mercadillo no tiene objeto, linea: " + objMerca.getLineaID())
            return
        }
        val tipoObj: Int = objMerca.getObjeto().getObjModelo().getTipo()
        val modeloID: Int = objMerca.getObjeto().getObjModeloID()
        if (_tipoObjetos[tipoObj] == null) {
            MainServidor.redactarLogServidorln("Bug Objeto del mercadillo " + iD + " , objetoID: " + objMerca.getObjeto()
                    .getID() + ", objetoTipo: " + tipoObj)
            return
        }
        // objMerca.setMercadilloID(_id);
        _tipoObjetos[tipoObj].addModeloVerificacion(objMerca)
        _lineas.put(objMerca.getLineaID(), Duo<Integer, Integer>(tipoObj, modeloID))
        _objMercadillos.add(objMerca)
    }

    fun borrarPath(linea: Int) {
        _lineas.remove(linea)
    }

    fun borrarObjMercaDelPuesto(objMerca: ObjetoMercadillo, perso: Personaje?): Boolean {
        return try {
            val tipo: Int = objMerca.getObjeto().getObjModelo().getTipo()
            val borrable: Boolean = _tipoObjetos[tipo].borrarObjMercaDeModelo(objMerca, perso, this)
            if (borrable) {
                _objMercadillos.remove(objMerca)
                GestorSQL.DELETE_OBJ_MERCADILLO(objMerca.getObjeto().getID())
            }
            borrable
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    @Synchronized
    fun comprarObjeto(lineaID: Int, cant: Int, precio: Long,
                      nuevoDueño: Personaje): Boolean {
        try {
            if (MainServidor.PARAM_MERCADILLO_OGRINAS) {
                if (GestorSQL.RESTAR_OGRINAS1(nuevoDueño.getCuenta(), precio.toInt(), nuevoDueño)) {
                    val linea: LineaMercadillo? = getLinea(lineaID)
                    val objAComprar: ObjetoMercadillo = linea.tuTienes(cant, precio)
                    val objeto: Objeto = objAComprar.getObjeto()
                    if (objeto == null || !borrarObjMercaDelPuesto(objAComprar, nuevoDueño)) {
                        MainServidor.redactarLogServidorln("Bug objeto mercadillo " + objeto.getID())
                        return false
                    }
                    nuevoDueño.addObjIdentAInventario(objeto, true)
                    objeto.getObjModelo().nuevoPrecio(objAComprar.getTipoCantidad(true), precio)
                    val viejoProp: Cuenta = Mundo.getCuenta(objAComprar.getCuentaID())
                    if (viejoProp != null) {
                        if (viejoProp.getTempPersonaje() != null) {
                            GestorSalida.ENVIAR_Im_INFORMACION(viejoProp.getTempPersonaje(), "065;$precio~" + objeto
                                    .getObjModeloID() + "~" + objeto.getObjModeloID() + "~" + objeto.getCantidad())
                        } else {
                            viejoProp.addMensaje("Im073;" + precio + "~" + objeto.getObjModeloID() + "~" + objeto.getObjModeloID() + "~"
                                    + objeto.getCantidad(), false)
                        }
                    }
                    try {
                        GestorSQL.ADD_OGRINAS_CUENTA(precio.toInt(), viejoProp.getID(), viejoProp.getSocket().getPersonaje())
                    } catch (e: Exception) {
                        GestorSQL.ADD_OGRINAS_CUENTA(precio.toInt(), viejoProp.getID(), null)
                    }
                } else {
                    return false
                }
            } else if (MainServidor.ID_VENTA_MERCANTE > 0) {
                if (!nuevoDueño.tenerYEliminarObjPorModYCant(MainServidor.ID_VENTA_MERCANTE, precio.toInt())) {
                    GestorSalida.ENVIAR_Im_INFORMACION(nuevoDueño, "1128;$precio")
                    return false
                }
                val linea: LineaMercadillo? = getLinea(lineaID)
                val objAComprar: ObjetoMercadillo = linea.tuTienes(cant, precio)
                val objeto: Objeto = objAComprar.getObjeto()
                if (objeto == null || !borrarObjMercaDelPuesto(objAComprar, nuevoDueño)) {
                    val tempObjMod: ObjetoModelo = Mundo.getObjetoModelo(MainServidor.ID_VENTA_MERCANTE)
                    nuevoDueño.addObjIdentAInventario(tempObjMod.crearObjeto(precio.toInt(), Constantes.OBJETO_POS_NO_EQUIPADO,
                            CAPACIDAD_STATS.RANDOM), false)
                    MainServidor.redactarLogServidorln("Bug objeto mercadillo " + objeto.getID())
                    return false
                }
                nuevoDueño.addObjIdentAInventario(objeto, true)
                objeto.getObjModelo().nuevoPrecio(objAComprar.getTipoCantidad(true), precio)
                //nuevoDue�o.restarCantObjOEliminar(MainServidor.ID_VENTA_MERCANTE, (int)precio, true);
                val viejoProp: Cuenta = Mundo.getCuenta(objAComprar.getCuentaID())
                if (viejoProp != null) {
                    val valor: Objeto = Mundo.getObjetoModelo(MainServidor.ID_VENTA_MERCANTE).crearObjeto(precio.toInt(), Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM)
                    viejoProp.addObjetoAlBanco(valor)
                    if (viejoProp.getTempPersonaje() != null) {
                        GestorSalida.ENVIAR_Im_INFORMACION(viejoProp.getTempPersonaje(), "065;$precio~" + objeto
                                .getObjModeloID() + "~" + objeto.getObjModeloID() + "~" + objeto.getCantidad())
                    } else {
                        viejoProp.addMensaje("Im073;" + precio + "~" + objeto.getObjModeloID() + "~" + objeto.getObjModeloID() + "~"
                                + objeto.getCantidad(), false)
                    }
                    GestorSQL.REPLACE_CUENTA_SERVIDOR(viejoProp, 0.toByte())
                }
            } else {
                if (nuevoDueño.getKamas() < precio) {
                    GestorSalida.ENVIAR_Im_INFORMACION(nuevoDueño, "1128;$precio")
                    return false
                }
                val linea: LineaMercadillo? = getLinea(lineaID)
                val objAComprar: ObjetoMercadillo = linea.tuTienes(cant, precio)
                val objeto: Objeto = objAComprar.getObjeto()
                if (objeto == null || !borrarObjMercaDelPuesto(objAComprar, nuevoDueño)) {
                    MainServidor.redactarLogServidorln("Bug objeto mercadillo " + objeto.getID())
                    return false
                }
                nuevoDueño.addObjIdentAInventario(objeto, true)
                objeto.getObjModelo().nuevoPrecio(objAComprar.getTipoCantidad(true), precio)
                nuevoDueño.addKamas(-precio, true, true)
                val viejoProp: Cuenta = Mundo.getCuenta(objAComprar.getCuentaID())
                if (viejoProp != null) {
                    viejoProp.addKamasBanco(precio)
                    if (viejoProp.getTempPersonaje() != null) {
                        GestorSalida.ENVIAR_Im_INFORMACION(viejoProp.getTempPersonaje(), "065;$precio~" + objeto
                                .getObjModeloID() + "~" + objeto.getObjModeloID() + "~" + objeto.getCantidad())
                    } else {
                        viejoProp.addMensaje("Im073;" + precio + "~" + objeto.getObjModeloID() + "~" + objeto.getObjModeloID() + "~"
                                + objeto.getCantidad(), false)
                    }
                    GestorSQL.REPLACE_CUENTA_SERVIDOR(viejoProp, 0.toByte())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    @Override
    fun addKamas(kamas: Long, perso: Personaje?) {
    }

    @get:Override
    val kamas: Long
        get() = 0

    fun cantObjMercaEnPuesto(cuentaID: Int): Int {
        var i = 0
        for (objMerca in _objMercadillos) {
            if (objMerca.getCuentaID() === cuentaID) {
                i++
            }
        }
        return i
    }

    @Override
    @Synchronized
    fun addObjetoExchanger(objeto: Objeto, cantPow: Long, perso: Personaje, precio: Int) {
        var cantPow = cantPow
        if (precio <= 1) {
            return
        }
        if (!perso.tieneObjetoID(objeto.getID()) || objeto.getPosicion() !== Constantes.OBJETO_POS_NO_EQUIPADO) {
            GestorSalida.ENVIAR_Im_INFORMACION(perso, "1OBJECT_DONT_EXIST")
            return
        }
        if (cantObjMercaEnPuesto(perso.getCuentaID()) >= maxObjCuenta) {
            GestorSalida.ENVIAR_Im_INFORMACION(perso, "166")
            return
        }
        val restPrecio = (precio * porcentajeImpuesto / 100).toLong()
        if (perso.getKamas() < restPrecio) {
            GestorSalida.ENVIAR_Im_INFORMACION(perso, "176")
            return
        }
        if (cantPow > 3) {
            cantPow = 3
        } else if (cantPow < 1) {
            cantPow = 1
        }
        val cantReal = Math.pow(10, cantPow - 1) as Int
        val nuevaCantidad: Long = objeto.getCantidad() - cantReal
        if (nuevaCantidad >= 1) {
            val nuevoObj: Objeto = objeto.clonarObjeto(nuevaCantidad, Constantes.OBJETO_POS_NO_EQUIPADO)
            perso.addObjetoConOAKO(nuevoObj, true)
            objeto.setCantidad(cantReal)
            // GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(perso, objeto);
        }
        perso.borrarOEliminarConOR(objeto.getID(), false)
        val objMerca = ObjetoMercadillo(precio, cantPow, perso.getCuentaID(), objeto, iD)
        if (!GestorSQL.REPLACE_OBJETO_MERCADILLO(objMerca)) {
            return
        }
        if (!MainServidor.MODO_ALL_OGRINAS) {
            perso.addKamas(-restPrecio, true, true)
        } else {
            if (MainServidor.PARAM_MERCA_OGRINAS) {
                perso.addKamas(-restPrecio, true, true)
            }
        }
        addObjMercaAlPuesto(objMerca)
        GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(perso, '+', "", objMerca.analizarParaEmK())
    }

    @Override
    @Synchronized
    fun remObjetoExchanger(objeto: Objeto, cantidad: Long, perso: Personaje, precio: Int) {
        var objMerca: ObjetoMercadillo? = null
        try {
            for (temp in _objMercadillos) {
                if (temp.getObjeto().getID() === objeto.getID()) {
                    objMerca = temp
                    break
                }
            }
        } catch (e: Exception) {
            return
        }
        if (objMerca == null) {
            return
        }
        perso.addObjIdentAInventario(objMerca.getObjeto(), true)
        borrarObjMercaDelPuesto(objMerca, null)
        GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(perso, '-', "", objeto.getID().toString() + "")
    }

    @Override
    fun cerrar(perso: Personaje, exito: String?) {
        perso.cerrarVentanaExchange(exito)
    }

    fun botonOK(perso: Personaje?) {}

    init {
        val mapas: Array<String> = mapaID.split(",")
        for (str in mapas) {
            _mapas.add(Integer.parseInt(str))
        }
        porcentajeImpuesto = tasa
        this.maxObjCuenta = maxObjCuenta
        tipoObjPermitidos = tipoObj
        this.nivelMax = nivelMax
        for (tipo in tipoObj.split(",")) {
            val tipoID: Int = Integer.parseInt(tipo)
            _tipoObjetos.put(tipoID, TipoObjetos(tipoID))
        }
    }
}