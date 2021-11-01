package variables.mapa

import java.util.ArrayList

class SuperArea(val iD: Int) {
    private val _areas: ArrayList<Area> = ArrayList<Area>()
    fun addArea(area: Area?) {
        _areas.add(area)
    }
}