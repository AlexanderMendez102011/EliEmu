package variables.zotros

import estaticos.Formulas

class Logro {
    var id = 0
    var titulo: String? = null
    var descripcion: String? = null
    var recompensa = 0
    var cantidad: String? = null
    var tipo = 0
    var args: String? = null
    var isActivo = false
    val pos: Int = Formulas.getRandomInt(0, 1000)
}