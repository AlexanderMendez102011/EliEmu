package variables.npc

import java.util.ArrayList
import estaticos.Condiciones
import estaticos.MainServidor
import variables.personaje.Personaje
import variables.zotros.Accion

class RespuestaNPC(val iD: Int) {
    private val _acciones: ArrayList<Accion> = ArrayList<Accion>()
    var condicion = ""
        private set

    //
    // public void setCondicion(String cond) {
    // _condicion = cond;
    // }
    fun borrarAcciones() {
        _acciones.clear()
        condicion = ""
    }

    val acciones: ArrayList<Accion>
        get() = _acciones

    fun addAccion(accion: Accion) {
        val c: ArrayList<Accion> = ArrayList<Accion>()
        c.addAll(_acciones)
        var condicion: String? = accion.getCondicion()
        if (condicion.isEmpty()) {
            condicion = null
        } else if (condicion!!.equals("BN")) {
            condicion = ""
        }
        if (!MainServidor.SON_DE_LUCIANO && !MainServidor.PARAM_VARIAS_ACCIONES) {
            for (a in c) {
                if (a.getID() === accion.getID()) {
                    _acciones.remove(a)
                } else if (condicion != null) {
                    a.setCondicion(condicion)
                }
            }
            if (condicion != null) {
                accion.setCondicion(condicion)
                this.condicion = condicion
            }
        }
        _acciones.add(accion)
    }

    fun aplicar(perso: Personaje) {
        perso.setPreguntaID(0)
        for (accion in _acciones) {
            if (Condiciones.validaCondiciones(perso, accion.getCondicion(), null)) accion.realizarAccion(perso, null, -1, -1.toShort())
        }
    }

    fun mostrar(perso: Personaje?): Boolean {
        for (accion in _acciones) {
            if (Condiciones.validaCondiciones(perso, accion.getCondicion(), null)) {
                return true
            }
        }
        return false
    }
}