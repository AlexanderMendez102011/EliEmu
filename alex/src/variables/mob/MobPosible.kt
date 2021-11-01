package variables.mob

import java.util.ArrayList
import estaticos.Formulas

class MobPosible(val cantMax: Int, private val _probabilidad: Int) {
    private var _mobs: ArrayList<MobGradoModelo>? = null
    fun addMobPosible(mob: MobGradoModelo?) {
        if (_mobs == null) {
            _mobs = ArrayList()
        }
        _mobs.add(mob)
    }

    fun tieneMob(mob: MobGradoModelo?): Boolean {
        return if (_mobs == null) {
            false
        } else _mobs.contains(mob)
    }

    val randomMob: MobGradoModelo?
        get() = if (_mobs == null) {
            null
        } else _mobs.get(Formulas.getRandomInt(0, _mobs.size() - 1))

    fun pasoProbabilidad(): Boolean {
        return if (_probabilidad < 0 || _probabilidad >= 100) {
            true
        } else Formulas.getRandomInt(1, 100) <= _probabilidad
    }
}