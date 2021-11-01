package variables.oficio

import java.util.ArrayList
import java.util.Map
import java.util.TreeMap
import java.util.regex.Pattern

class Oficio(val iD: Int, recetas: String) {
    private val _recetas: Map<Integer, ArrayList<Integer>?> = TreeMap<Integer, ArrayList<Integer>>()
    fun listaRecetaPorTrabajo(trabajo: Int): ArrayList<Integer>? {
        return _recetas[trabajo]
    }

    fun puedeReceta(trabajo: Int, modelo: Int): Boolean {
        if (_recetas[trabajo] != null) {
            for (a in _recetas[trabajo]) {
                if (a == modelo) {
                    return true
                }
            }
        }
        return false
    }

    init {
        if (!recetas.isEmpty()) {
            for (str in recetas.split(Pattern.quote("|"))) {
                try {
                    val trabajoID: Int = Integer.parseInt(str.split(";").get(0))
                    val list: ArrayList<Integer> = ArrayList<Integer>()
                    for (str2 in str.split(";").get(1).split(",")) {
                        list.add(Integer.parseInt(str2))
                    }
                    _recetas.put(trabajoID, list)
                } catch (e: Exception) {
                }
            }
        }
    }
}