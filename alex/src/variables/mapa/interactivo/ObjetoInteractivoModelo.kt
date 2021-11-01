package variables.mapa.interactivo

import java.util.ArrayList

class ObjetoInteractivoModelo(val iD: Int, val tiempoRecarga: Int, val duracion: Int, private val _animacionnPJ: Byte,
                              @field:SuppressWarnings("unused") private val _caminable: Byte, // 1 recursos para recoger
                              val tipo: Byte, gfx: String, skill: String) {
    private val _gfx: ArrayList<Integer> = ArrayList<Integer>()
    private val _skills: ArrayList<Integer> = ArrayList<Integer>()
    val skills: ArrayList<Integer>
        get() = _skills
    val gfxs: ArrayList<Integer>
        get() = _gfx
    val animacionPJ: Int
        get() = _animacionnPJ.toInt()

    // @SuppressWarnings("unused")
    // private boolean acercarse() {
    // return (_caminable & 1) == 1;
    // }
    //
    // @SuppressWarnings("unused")
    // private boolean esCaminable() {
    // return (_caminable & 2) == 2;
    // }
    fun tieneSkill(skillID: Int): Boolean {
        return _skills.contains(skillID)
    }

    init {
        for (str in gfx.split(",")) {
            if (str.isEmpty()) {
                continue
            }
            try {
                _gfx.add(Integer.parseInt(str))
            } catch (e: Exception) {
            }
        }
        for (str in skill.split(",")) {
            if (str.isEmpty()) {
                continue
            }
            try {
                _skills.add(Integer.parseInt(str))
            } catch (e: Exception) {
            }
        }
    }
}