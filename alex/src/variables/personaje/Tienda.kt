package variables.personaje

import java.util.ArrayList

class Tienda : Exchanger {
    private val _tienda: ArrayList<Objeto> = ArrayList<Objeto>()
    fun addObjeto(objeto: Objeto) {
        if (objeto.getID() === 0) {
            Mundo.addObjeto(objeto, false)
        }
        if (_tienda.contains(objeto)) {
            return
        }
        _tienda.add(objeto)
    }

    fun borrarObjeto(obj: Objeto?) {
        _tienda.remove(obj)
    }

    operator fun contains(obj: Objeto?): Boolean {
        return _tienda.contains(obj)
    }

    val objetos: ArrayList<Objeto>
        get() = _tienda

    @Override
    fun addKamas(kamas: Long, perso: Personaje?) {
    }

    @get:Override
    val kamas: Long
        get() = 0

    fun clear() {
        _tienda.clear()
    }

    fun estaVacia(): Boolean {
        return _tienda.isEmpty()
    }

    @Override
    @Synchronized
    fun addObjetoExchanger(objeto: Objeto, cantidad: Long, perso: Personaje, precio: Int) {
        var cantidad = cantidad
        if (!_tienda.contains(objeto)) { // si no lo tiene en la tienda
            if (cantidad == 0L) {
                GestorSalida.ENVIAR_BN_NADA(perso)
                return
            }
            if (!perso.tieneObjetoID(objeto.getID()) || objeto.getPosicion() !== Constantes.OBJETO_POS_NO_EQUIPADO) {
                GestorSalida.ENVIAR_Im_INFORMACION(perso, "1OBJECT_DONT_EXIST")
                return
            }
            if (_tienda.size() >= perso.getNivel()) {
                GestorSalida.ENVIAR_Im_INFORMACION(perso, "166")
                return
            }
            if (cantidad > objeto.getCantidad()) {
                cantidad = objeto.getCantidad()
            }
            val nuevaCantidad: Long = objeto.getCantidad() - cantidad
            if (nuevaCantidad >= 1) {
                val nuevoObj: Objeto = objeto.clonarObjeto(nuevaCantidad, Constantes.OBJETO_POS_NO_EQUIPADO)
                perso.addObjetoConOAKO(nuevoObj, true)
                objeto.setCantidad(cantidad)
                GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(perso, objeto)
            }
            perso.borrarOEliminarConOR(objeto.getID(), false)
            objeto.setPrecio(precio)
            addObjeto(objeto)
        } else { // si lo tiene en la tienda
            cantidad = objeto.getCantidad()
            objeto.setPrecio(precio)
        }
        GestorSalida.ENVIAR_EiK_MOVER_OBJETO_TIENDA(perso, '+', "", objeto.getID().toString() + "|" + cantidad + "|" + objeto
                .getObjModeloID() + "|" + objeto.convertirStatsAString(false) + "|" + precio)
    }

    @Override
    @Synchronized
    fun remObjetoExchanger(objeto: Objeto, cantidad: Long, perso: Personaje, precio: Int) {
        if (_tienda.remove(objeto)) {
            if (!perso.addObjIdentAInventario(objeto, true)) {
                objeto.setPrecio(0)
            }
            GestorSalida.ENVIAR_EiK_MOVER_OBJETO_TIENDA(perso, '-', "", objeto.getID().toString() + "")
        }
    }

    @Override
    fun cerrar(perso: Personaje, exito: String?) {
        perso.cerrarVentanaExchange(exito)
    }

    fun botonOK(perso: Personaje?) {}
    @Override
    fun getListaExchanger(perso: Personaje?): String? {
        // TODO Auto-generated method stub
        return null
    }
}