package variables.pelea

import estaticos.Mundo

class DropMob {
    val iDObjModelo: Int
    val prospeccion: Int
    var maximo: Int
        private set
    var nivelMin = 0
        private set
    var nivelMax = 0
        private set
    var porcentaje: Float
        private set
    private var _unicItem = false
    var condicion = ""
        private set
    private val _esDropFijo: Boolean

    constructor(objeto: Int, prospeccion: Int, porcentaje: Float, max: Int,
                condicion: String, unicItem: Boolean) {
        iDObjModelo = objeto
        this.prospeccion = prospeccion
        this.porcentaje = Math.max(0.001, Math.min(100, porcentaje))
        maximo = Math.max(max, 1)
        this.condicion = condicion
        _esDropFijo = false
        _unicItem = unicItem
    }

    constructor(objeto: Int, porcentaje: Float, nivelMin: Int, nivelMax: Int) {
        iDObjModelo = objeto
        prospeccion = 1
        this.porcentaje = Math.max(0.001, Math.min(100, porcentaje))
        this.nivelMin = nivelMin
        this.nivelMax = nivelMax
        maximo = 1
        _esDropFijo = true
    }

    fun esUnico(): Boolean {
        return _unicItem
    }

    fun esDropFijo(): Boolean {
        return _esDropFijo
    }

    fun esIdentico(d: DropMob): Boolean {
        if (d._esDropFijo != _esDropFijo) {
            return false
        }
        if (!d.condicion.equals(condicion)) {
            return false
        }
        if (d.porcentaje != porcentaje) {
            return false
        }
        if (d.prospeccion != prospeccion) {
            return false
        }
        if (d.iDObjModelo != iDObjModelo) {
            return false
        }
        if (d.nivelMax != nivelMax) {
            return false
        }
        return if (d.nivelMin != nivelMin) {
            false
        } else true
    }

    val string: String
        get() = (this.toString() + " [_objModeloID=" + iDObjModelo + " " + Mundo.getObjetoModelo(iDObjModelo).getNombre()
                + ", _prospeccion=" + prospeccion + ", _maximo=" + maximo + ", _nivelMin=" + nivelMin + ", _nivelMax="
                + nivelMax + ", _porcentaje=" + porcentaje + ", _condicion=" + condicion + ", _esEtereo=" + _esDropFijo + "]")
}