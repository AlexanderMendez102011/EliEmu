package variables.hechizo

import variables.pelea.Luchador

class HechizoLanzado(lanzador: Luchador, sHechizo: StatHechizo, val objetivo: Int) {
    val hechizoID: Int
    var sigLanzamiento = 0
        private set

    fun actuSigLanzamiento() {
        sigLanzamiento -= 1
    }

    companion object {
        fun poderSigLanzamiento(lanzador: Luchador, idHechizo: Int): Int {
            for (HL in lanzador.getHechizosLanzados()) {
                if (HL.hechizoID == idHechizo && HL.sigLanzamiento > 0) {
                    return HL.sigLanzamiento
                }
            }
            return 0
        }

        fun getNroLanzamientos(lanzador: Luchador, idHechizo: Int): Int {
            var nro = 0
            for (HL in lanzador.getHechizosLanzados()) {
                if (HL.hechizoID == idHechizo) {
                    nro++
                }
            }
            return nro
        }

        fun getNroLanzPorObjetivo(lanzador: Luchador, idObjetivo: Int, idHechizo: Int): Int {
            var nro = 0
            if (idObjetivo != 0) {
                for (HL in lanzador.getHechizosLanzados()) {
                    if (HL.hechizoID == idHechizo && HL.objetivo == idObjetivo) {
                        nro++
                    }
                }
            }
            return nro
        }

        fun puedeLanzPorObjetivo(lanzador: Luchador, idObjetivo: Int, SH: StatHechizo): Boolean {
            if (SH.getMaxLanzPorObjetivo() <= 0) {
                return true
            }
            var nro = 0
            if (idObjetivo != 0) {
                for (HL in lanzador.getHechizosLanzados()) {
                    if (HL.hechizoID == SH.getHechizoID() && HL.objetivo == idObjetivo) {
                        nro++
                    }
                }
            }
            return nro < SH.getMaxLanzPorObjetivo() // 0 < 1
        }
    }

    init {
        hechizoID = sHechizo.getHechizoID()
        if (lanzador.getPersonaje() != null && lanzador.getPersonaje().tieneModfiSetClase(hechizoID)) {
            sigLanzamiento = sHechizo.getSigLanzamiento() - lanzador.getPersonaje().getModifSetClase(hechizoID, 286)
        } else {
            sigLanzamiento = sHechizo.getSigLanzamiento()
        }
    }
}