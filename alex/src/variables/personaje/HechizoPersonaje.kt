package variables.personaje

import variables.hechizo.Hechizo
import variables.hechizo.StatHechizo

class HechizoPersonaje(var posicion: Char, _hechizo: Hechizo?, _nivel: Int) {
    private val _hechizo: Hechizo?
    var nivel: Int
    val hechizo: Hechizo?
        get() = _hechizo
    val statHechizo: StatHechizo?
        get() = if (_hechizo == null) {
            null
        } else _hechizo.getStatsPorNivel(nivel)

    init {
        this._hechizo = _hechizo
        nivel = _nivel
    }
}