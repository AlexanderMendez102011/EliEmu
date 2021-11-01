package variables.personaje

import java.util.ArrayList
import java.util.regex.Pattern
import variables.stats.Stats
import estaticos.Mundo

class Especialidad(val iD: Int, val orden: Int, val nivel: Int, dones: String) {
    private val _dones: ArrayList<Don> = ArrayList()
    val dones: ArrayList<Don>
        get() = _dones

    inner class Don(val iD: Int, val nivel: Int, statID: Int, valor: Int) {
        private val _stats: Stats = Stats()
        val stat: Stats
            get() = _stats

        init {
            if (statID > 0 && valor > 0) {
                _stats.addStatID(statID, valor)
            }
        }
    }

    init {
        for (s in dones.split(Pattern.quote("|"))) {
            if (s.isEmpty()) {
                continue
            }
            try {
                val args: Array<String> = s.split(",")
                val donID: Int = Integer.parseInt(args[0])
                val donNivel: Int = Integer.parseInt(args[1])
                val donStat: Int = Mundo.getDonStat(donID)
                var valor = 0
                if (args.size > 2) {
                    valor = Integer.parseInt(args[2])
                }
                _dones.add(Don(donID, donNivel, donStat, valor))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}