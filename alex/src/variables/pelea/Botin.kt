package variables.pelea

class Botin(drop: DropMob) {
    private val _drop: DropMob
    var botinMaximo: Int
        private set
    val drop: DropMob
        get() = _drop

    fun addBotinMaximo(cant: Int) {
        botinMaximo += cant
    }

    fun esUnico(): Boolean {
        return _drop.esUnico()
    }

    val iDObjModelo: Int
        get() = _drop.getIDObjModelo()
    val prospeccionBotin: Int
        get() = _drop.getProspeccion()
    val porcentajeBotin: Float
        get() = _drop.getPorcentaje()
    val condicionBotin: String
        get() = _drop.getCondicion()

    fun esDropFijo(): Boolean {
        return _drop.esDropFijo()
    }

    init {
        _drop = drop
        botinMaximo = drop.getMaximo()
    }
}