package variables.ranking

import estaticos.Mundo

class RankingKoliseo(val iD: Int, victorias: Int, derrotas: Int) {
    var victorias = 0
        private set
    var derrotas = 0
        private set

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
    }
}