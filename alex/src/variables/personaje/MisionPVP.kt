package variables.personaje

class MisionPVP(val tiempoInicio: Long, victima: Personaje, kamas: Long, exp: Long, craneo: Int) {
    private val _victimaPVP: Personaje
    val nombreVictima: String
    val kamasRecompensa: Long
    val expMision: Long
    private val _cazaCabezas: Boolean
    val craneo: Int
    fun esCazaCabezas(): Boolean {
        return _cazaCabezas
    }

    val pjMision: Personaje
        get() = _victimaPVP

    init {
        nombreVictima = victima.getNombre()
        kamasRecompensa = kamas
        expMision = exp
        this.craneo = if (expMision <= 0) 0 else craneo
        _cazaCabezas = this.craneo != 0
        _victimaPVP = victima
    }
}