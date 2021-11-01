package variables.mapa.interactivo

import variables.zotros.Accion

class OtroInteractivo(val gfxID: Int, val mapaID: Short, val celdaID: Short, val accionID: Int, val args: String,
                      val condicion: String, val tiempoRecarga: Int) {
    private val _accion: Accion
    val accion: Accion
        get() = _accion

    init {
        _accion = Accion(accionID, args, "")
    }
}