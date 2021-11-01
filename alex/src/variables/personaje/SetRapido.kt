package variables.personaje

import estaticos.Constantes

class SetRapido(val iD: Int, val nombre: String, private val _icono: Int, data: String) {
    val objetos = IntArray(18)
    fun actualizarObjetos(oldID: Int, newID: Int, oldPos: Byte, newPos: Byte): Boolean {
        var b = false
        for (i in objetos.indices) {
            if ((oldPos == Constantes.OBJETO_POS_NO_EQUIPADO || oldPos == i) && objetos[i.toInt()] == oldID) {
                if (newPos != i) {
                    b = true
                    objetos[i.toInt()] = newID
                }
            }
        }
        return b
    }

    val string: String
        get() {
            val data = StringBuilder()
            for (i in objetos.indices) {
                if (objetos[i.toInt()] <= 0) {
                    continue
                }
                if (data.length() > 0) {
                    data.append(";")
                }
                data.append(objetos[i.toInt()].toString() + "," + i)
            }
            return iD.toString() + "|" + nombre + "|" + _icono + "|" + data.toString()
        }

    init {
        for (s in data.split(";")) {
            if (s.isEmpty()) {
                continue
            }
            val idObjeto: Int = Integer.parseInt(s.split(",").get(0))
            val posObjeto: Int = Integer.parseInt(s.split(",").get(1))
            try {
                objetos[posObjeto] = idObjeto
            } catch (e: Exception) {
            }
        }
    }
}