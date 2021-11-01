package variables.zotros

import java.util.Map
import java.util.TreeMap

class Crafeo(var base: Int, var statsString: String, val cantidad: Int) {
    private var stats: Map<Integer, Integer> = TreeMap()
    fun getStats(): Map<Integer, Integer> {
        return stats
    }

    fun setStats(stats: Map<Integer, Integer>) {
        this.stats = stats
    }

    fun getStatMaximo(stats: Int): Int {
        return if (this.stats.containsKey(stats)) {
            this.stats[stats]
        } else {
            0
        }
    }

    init {
        for (texto in statsString.split(";")) {
            val stat: Array<String> = texto.split(",")
            try {
                stats.put(Integer.parseInt(stat[0]), Integer.parseInt(stat[1]))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}