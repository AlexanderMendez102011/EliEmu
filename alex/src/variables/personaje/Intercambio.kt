package variables.personaje

import java.util.ArrayList

class Intercambio(p1: Personaje, p2: Personaje) : Exchanger {
    private val _perso1: Personaje
    private val _perso2: Personaje
    private var _kamas1: Long = 0
    private var _kamas2: Long = 0
    private var ogrinas1: Long = 0
    private var ogrinas2: Long = 0
    private val _objetos1: ArrayList<Duo<Integer, Long>> = ArrayList<Duo<Integer, Long>>()
    private val _objetos2: ArrayList<Duo<Integer, Long>> = ArrayList<Duo<Integer, Long>>()
    private var _ok1 = false
    private var _ok2 = false
    @Synchronized
    fun botonOK(perso: Personaje) {
        if (_perso1.getID() === perso.getID()) {
            _ok1 = !_ok1
            GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_perso1, _ok1, perso.getID())
            GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_perso2, _ok1, perso.getID())
        } else if (_perso2.getID() === perso.getID()) {
            _ok2 = !_ok2
            GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_perso1, _ok2, perso.getID())
            GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_perso2, _ok2, perso.getID())
        }
        if (_ok1 && _ok2) {
            aplicar()
        }
    }

    fun desCheck() {
        _ok1 = false
        _ok2 = false
        GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_perso1, _ok1, _perso1.getID())
        GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_perso2, _ok1, _perso1.getID())
        GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_perso1, _ok2, _perso2.getID())
        GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_perso2, _ok2, _perso2.getID())
    }

    /*public synchronized long getKamas(final int id) {
		if (_perso1.getID() == id) {
			return _kamas1;
		}
		if (_perso2.getID() == id) {
			return _kamas2;
		}
		return 0;
	}*/
    @Synchronized
    fun getOgrinas(id: Int): Long {
        if (_perso1.getID() === id) {
            return ogrinas1
        }
        return if (_perso2.getID() === id) {
            ogrinas2
        } else 0
    }

    fun cerrar(perso: Personaje?, exito: String?) {
        _perso1.cerrarVentanaExchange(exito)
        GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso1)
        _perso2.cerrarVentanaExchange(exito)
        GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso2)
    }

    @Synchronized
    fun aplicar() {
        _kamas1 = Math.min(_kamas1, _perso1.getKamas())
        _kamas2 = Math.min(_kamas2, _perso2.getKamas())
        _perso1.addKamas(-_kamas1 + _kamas2, true, true)
        _perso2.addKamas(-_kamas2 + _kamas1, true, true)
        val str = StringBuilder()
        str.append(_perso1.getNombre().toString() + " (" + _perso1.getID() + ") >> ")
        str.append("[$_kamas1 KAMAS]")
        for (duo in _objetos1) {
            try {
                val obj1: Objeto = _perso1.getObjeto(duo._primero)
                val cantidad: Long = duo._segundo
                str.append(", ")
                if (obj1 == null) {
                    str.append("[NO TIENE - ID " + duo._primero.toString() + " CANT " + duo._segundo)
                    continue
                }
                str.append("[" + obj1.getObjModelo().getNombre().toString() + " ID:" + obj1.getID().toString() + " Mod:" + obj1.getObjModeloID()
                        .toString() + " Cant:" + cantidad.toString() + "]")
                val nuevaCantidad: Long = obj1.getCantidad() - cantidad
                if (nuevaCantidad >= 1) {
                    val nuevoObj: Objeto = obj1.clonarObjeto(cantidad, Constantes.OBJETO_POS_NO_EQUIPADO)
                    _perso2.addObjIdentAInventario(nuevoObj, false)
                    obj1.setCantidad(obj1.getCantidad() - cantidad)
                    GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso1, obj1)
                } else {
                    _perso1.borrarOEliminarConOR(obj1.getID(), false)
                    _perso2.addObjIdentAInventario(obj1, true)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        str.append(" ### ")
        str.append(_perso2.getNombre().toString() + " (" + _perso2.getID() + ") >> ")
        str.append("[$_kamas2 KAMAS]")
        for (duo in _objetos2) {
            try {
                val obj2: Objeto = _perso2.getObjeto(duo._primero)
                val cantidad: Long = duo._segundo
                str.append(", ")
                if (obj2 == null) {
                    str.append("[NO TIENE - ID " + duo._primero.toString() + " CANT " + duo._segundo)
                    continue
                }
                str.append("[" + obj2.getObjModelo().getNombre().toString() + " ID:" + obj2.getID().toString() + " Mod:" + obj2.getObjModeloID()
                        .toString() + " Cant:" + cantidad.toString() + "]")
                val nuevaCantidad: Long = obj2.getCantidad() - cantidad
                if (nuevaCantidad >= 1) {
                    val nuevoObj: Objeto = obj2.clonarObjeto(cantidad, Constantes.OBJETO_POS_NO_EQUIPADO)
                    _perso1.addObjIdentAInventario(nuevoObj, false)
                    obj2.setCantidad(nuevaCantidad)
                    GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso2, obj2)
                } else {
                    _perso2.borrarOEliminarConOR(obj2.getID(), false)
                    _perso1.addObjIdentAInventario(obj2, true)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (MainServidor.PARAM_GUARDAR_LOGS_INTERCAMBIOS) {
            GestorSQL.INSERT_INTERCAMBIO(str.toString())
        }
        cerrar(null, "a")
    }

    @Override
    @Synchronized
    fun addObjetoExchanger(objeto: Objeto, cantidad: Long, perso: Personaje, precio: Int) {
        var cantidad = cantidad
        if (!perso.tieneObjetoID(objeto.getID()) || objeto.getPosicion() !== Constantes.OBJETO_POS_NO_EQUIPADO) {
            GestorSalida.ENVIAR_Im_INFORMACION(perso, "1OBJECT_DONT_EXIST")
            return
        }
        val cantInter = getCantObjeto(objeto.getID(), perso.getID())
        if (cantidad > objeto.getCantidad() - cantInter) {
            cantidad = objeto.getCantidad() - cantInter
        }
        if (cantidad < 1) {
            return
        }
        desCheck()
        val objetoID: Int = objeto.getID()
        val str = "$objetoID|$cantidad"
        val add = "|" + objeto.getObjModeloID().toString() + "|" + objeto.convertirStatsAString(false)
        if (_perso1.getID() === perso.getID()) {
            val duo: Duo<Integer, Long> = Mundo.getDuoPorIDPrimero1(_objetos1, objetoID)
            if (duo != null) {
                duo._segundo += cantidad
                GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_perso1, 'O', "+", objetoID.toString() + "|" + duo._segundo)
                GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_perso2, 'O', "+", objetoID.toString() + "|" + duo._segundo + add)
            } else {
                GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_perso1, 'O', "+", str)
                GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_perso2, 'O', "+", str + add)
                _objetos1.add(Duo<Integer, Long>(objetoID, cantidad))
            }
        } else if (_perso2.getID() === perso.getID()) {
            val duo: Duo<Integer, Long> = Mundo.getDuoPorIDPrimero1(_objetos2, objetoID)
            if (duo != null) {
                duo._segundo += cantidad
                GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_perso2, 'O', "+", objetoID.toString() + "|" + duo._segundo)
                GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_perso1, 'O', "+", objetoID.toString() + "|" + duo._segundo + add)
            } else {
                GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_perso2, 'O', "+", str)
                GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_perso1, 'O', "+", str + add)
                _objetos2.add(Duo<Integer, Long>(objetoID, cantidad))
            }
        }
    }

    @Override
    @Synchronized
    fun remObjetoExchanger(objeto: Objeto, cantidad: Long, perso: Personaje, precio: Int) {
        var cantidad = cantidad
        val cantInter = getCantObjeto(objeto.getID(), perso.getID())
        if (cantidad > cantInter) {
            cantidad = cantInter
        }
        if (cantidad < 1) {
            return
        }
        desCheck()
        if (_perso1.getID() === perso.getID()) {
            val duo: Duo<Integer, Long> = Mundo.getDuoPorIDPrimero1(_objetos1, objeto.getID())
            if (duo != null) {
                val nuevaCantidad: Long = duo._segundo - cantidad
                if (nuevaCantidad <= 0) {
                    _objetos1.remove(duo)
                    GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_perso1, 'O', "-", objeto.getID().toString() + "")
                    GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_perso2, 'O', "-", objeto.getID().toString() + "")
                } else {
                    duo._segundo = nuevaCantidad
                    GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_perso1, 'O', "+", objeto.getID().toString() + "|" + nuevaCantidad)
                    GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_perso2, 'O', "+", objeto.getID().toString() + "|" + nuevaCantidad + "|"
                            + objeto.getObjModeloID() + "|" + objeto.convertirStatsAString(false))
                }
            }
        } else if (_perso2.getID() === perso.getID()) {
            val duo: Duo<Integer, Long> = Mundo.getDuoPorIDPrimero1(_objetos2, objeto.getID())
            if (duo != null) {
                val nuevaCantidad: Long = duo._segundo - cantidad
                if (nuevaCantidad <= 0) {
                    _objetos2.remove(duo)
                    GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_perso1, 'O', "-", objeto.getID().toString() + "")
                    GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_perso2, 'O', "-", objeto.getID().toString() + "")
                } else {
                    duo._segundo = nuevaCantidad
                    GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_perso1, 'O', "+", objeto.getID().toString() + "|" + nuevaCantidad + "|"
                            + objeto.getObjModeloID() + "|" + objeto.convertirStatsAString(false))
                    GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_perso2, 'O', "+", objeto.getID().toString() + "|" + nuevaCantidad)
                }
            }
        }
    }

    @Synchronized
    fun getCantObjeto(objetoID: Int, persoID: Int): Long {
        val objetos: ArrayList<Duo<Integer, Long>>
        objetos = if (_perso1.getID() === persoID) {
            _objetos1
        } else {
            _objetos2
        }
        for (duo in objetos) {
            if (duo._primero === objetoID) {
                return duo._segundo
            }
        }
        return 0
    }

    @Override
    fun addKamas(kamas: Long, perso: Personaje) {
        desCheck()
        if (kamas < 0) {
            return
        }
        if (_perso1.getID() === perso.getID()) {
            _kamas1 = kamas
            GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_perso1, 'G', "", kamas.toString() + "")
            GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_perso2, 'G', "", kamas.toString() + "")
        } else if (_perso2.getID() === perso.getID()) {
            _kamas2 = kamas
            GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_perso2, 'G', "", kamas.toString() + "")
            GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_perso1, 'G', "", kamas.toString() + "")
        }
    }

    @Override
    fun getListaExchanger(perso: Personaje?): String? {
        // TODO Auto-generated method stub
        return null
    }

    fun addOgrinas(kamas: Long, perso: Personaje) {
        desCheck()
        if (kamas < 0) {
            return
        }
        if (_perso1.getID() === perso.getID()) {
            ogrinas1 = kamas
            GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_perso1, 'S', "", kamas.toString() + "")
            GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_perso2, 'S', "", kamas.toString() + "")
        } else if (_perso2.getID() === perso.getID()) {
            ogrinas2 = kamas
            GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_perso2, 'S', "", kamas.toString() + "")
            GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_perso1, 'S', "", kamas.toString() + "")
        }
    }

    // TODO Auto-generated method stub
    @get:Override
    val kamas: Long
        get() =// TODO Auto-generated method stub
            0

    init {
        _perso1 = p1
        _perso2 = p2
    }
}