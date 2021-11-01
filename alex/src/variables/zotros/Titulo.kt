package variables.zotros

import estaticos.GestorSQL
import estaticos.GestorSalida
import variables.personaje.Personaje

class Titulo(val iD: Int, private var _nombre: String, private var _creditos: Int, var precioStr: Int, private var _kamas: Int, private val _vender: Boolean, private val _valido: Boolean) {
    private var _vip = false
    fun adquirirTitulo(_perso: Personaje): Boolean {
        if (!_valido) {
            return false
        }
        if (_perso.tieneTitulo(iD)) {
            return true
        }
        if (_vender) {
            if (_creditos > 0) {
                if (!GestorSQL.RESTAR_CREDITOS(_perso.getCuenta(), _creditos, _perso)) {
                    return false
                }
            } else if (precioStr > 0) {
                if (!GestorSQL.RESTAR_OGRINAS1(_perso.getCuenta(), precioStr, _perso)) {
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
        return precioStr
    }

    fun set_ogrinas(_ogrinas: Int) {
        precioStr = _ogrinas
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

    fun esVip(): Boolean {
        return _vip
    }

    fun set_vip(_vip: Boolean) {
        this._vip = _vip
    }
}