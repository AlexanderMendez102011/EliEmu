package variables.zotros

import estaticos.Mundo.Duo

class Almanax(val iD: Int, val tipo: Int, val bonus: Int, ofrenda: String) {
    private val _ofrenda: Duo<Integer, Integer>
    val ofrenda: Duo<Integer, Integer>
        get() = _ofrenda

    init {
        val idObjeto: Int = Integer.parseInt(ofrenda.split(",").get(0))
        val cantidad: Int = Integer.parseInt(ofrenda.split(",").get(1))
        _ofrenda = Duo<Integer, Integer>(idObjeto, cantidad)
    }
}