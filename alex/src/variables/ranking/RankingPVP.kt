package variables.ranking

import estaticos.Mundo

class RankingPVP(val iD: Int, victorias: Int, derrotas: Int,
                 gradoAlineacion: Int) {
    var victorias = 0
        private set
    var derrotas = 0
        private set
    var gradoAlineacion = 1
    fun aumentarVictoria() {
        victorias += 1
    }

    fun aumentarDerrota() {
        derrotas += 1
    }

    val nombre: String
        get() = if (Mundo.getPersonaje(iD) == null) "Ninguno" else Mundo.getPersonaje(iD).getNombre()

    init {
        this.victorias = victorias
        this.derrotas = derrotas
        this.gradoAlineacion = gradoAlineacion
    }
}