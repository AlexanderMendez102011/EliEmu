package variables.zotros

import variables.personaje.Personaje
import estaticos.GestorSQL
import estaticos.GestorSalida

class Ornamento(val iD: Int, private var _nombre: String, private var _creditos: Int, private var _ogrinas: Int, private var _kamas: Int, private val _vender: Boolean, private val _valido: Boolean) {
    fun adquirirOrnamento(_perso: Personaje): Boolean {
        if (!_valido) {
            return false
        }
        if (_perso.tieneOrnamento(iD)) {
            return true
        }
        if (_vender) {
            if (_creditos > 0) {
                if (!GestorSQL.RESTAR_CREDITOS(_perso.getCuenta(), _creditos, _perso)) {
                    return false
                }
            } else if (_ogrinas > 0) {
                if (!GestorSQL.RESTAR_OGRINAS1(_perso.getCuenta(), _ogrinas, _perso)) {
                    return false
                }
            } else if (_kamas > 0) {
                if (_perso.getKamas() < _kamas) {
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1128;$_kamas")
                    return false
                }
                _perso.addKamas(-_kamas, true, true)
            }
        }
        return true
    }

    val precioStr: Long
        get() = _ogrinas.toLong()

    fun get_nombre(): String {
        return _nombre
    }

    fun set_nombre(_nombre: String) {
        this._nombre = _nombre
    }

    fun get_creditos(): Int {
        return _creditos
    }

    fun set_creditos(_creditos: Int) {
        this._creditos = _creditos
    }

    fun get_ogrinas(): Int {
        return _ogrinas
    }

    fun set_ogrinas(_ogrinas: Int) {
        this._ogrinas = _ogrinas
    }

    fun get_kamas(): Int {
        return _kamas
    }

    fun set_kamas(_kamas: Int) {
        this._kamas = _kamas
    }

    fun esParaVender(): Boolean {
        return _vender
    }

    fun esValido(): Boolean {
        return _valido
    }
}