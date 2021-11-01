package variables.personaje

import java.util.ArrayList
import estaticos.GestorSalida
import estaticos.MainServidor

class GrupoKoliseo(koli1: Personaje?) {
    private val _kolis: ArrayList<Personaje> = ArrayList<Personaje>()

    //
    val puntuacion: Int
        get() {
            var punt = 0
            for (p in _kolis) {
                punt += p.getPuntoKoli()
            } //
            return punt
        }

    fun dejarGrupo(p: Personaje) {
        if (!_kolis.contains(p)) {
            return
        }
        p.setGrupoKoliseo(null)
        _kolis.remove(p)
        try {
            if (_kolis.size() === 1) {
                _kolis.get(0).setGrupoKoliseo(null)
                GestorSalida.ENVIAR_kV_DEJAR_KOLISEO(_kolis.get(0))
            } else {
                GestorSalida.ENVIAR_kM_EXPULSAR_PJ_KOLISEO(this, p.getID())
            }
        } catch (e: Exception) {
        }
    }

    fun limpiarGrupo() {
        for (p in _kolis) {
            p.setGrupoKoliseo(null)
            GestorSalida.ENVIAR_kV_DEJAR_KOLISEO(p)
        }
        _kolis.clear()
    }

    val cantPjs: Int
        get() = _kolis.size()

    fun addPersonaje1(koli: Personaje): Boolean {
        if (_kolis.size() >= MainServidor.CANTIDAD_MIEMBROS_EQUIPO_KOLISEO_VS1) {
            return false
        }
        if (MainServidor.RANGO_NIVEL_KOLISEO > 0) {
            for (p in _kolis) {
                if (p.getNivel() > koli.getNivel() + MainServidor.RANGO_NIVEL_KOLISEO) {
                    return false
                }
                if (p.getNivel() < koli.getNivel() - MainServidor.RANGO_NIVEL_KOLISEO) {
                    return false
                }
            }
        }
        GestorSalida.ENVIAR_kM_AGREGAR_PJ_KOLISEO(this, koli.stringInfoGrupo())
        _kolis.add(koli)
        return true
    }

    fun addPersonaje2(koli: Personaje): Boolean {
        if (_kolis.size() >= MainServidor.CANTIDAD_MIEMBROS_EQUIPO_KOLISEO_VS2) {
            return false
        }
        if (MainServidor.RANGO_NIVEL_KOLISEO > 0) {
            for (p in _kolis) {
                if (p.getNivel() > koli.getNivel() + MainServidor.RANGO_NIVEL_KOLISEO) {
                    return false
                }
                if (p.getNivel() < koli.getNivel() - MainServidor.RANGO_NIVEL_KOLISEO) {
                    return false
                }
            }
        }
        GestorSalida.ENVIAR_kM_AGREGAR_PJ_KOLISEO(this, koli.stringInfoGrupo())
        _kolis.add(koli)
        return true
    }

    fun addPersonaje(koli: Personaje): Boolean {
        if (_kolis.size() >= MainServidor.CANTIDAD_MIEMBROS_EQUIPO_KOLISEO) {
            return false
        }
        if (MainServidor.RANGO_NIVEL_KOLISEO > 0) {
            for (p in _kolis) {
                if (p.getNivel() > koli.getNivel() + MainServidor.RANGO_NIVEL_KOLISEO) {
                    return false
                }
                if (p.getNivel() < koli.getNivel() - MainServidor.RANGO_NIVEL_KOLISEO) {
                    return false
                }
            }
        }
        GestorSalida.ENVIAR_kM_AGREGAR_PJ_KOLISEO(this, koli.stringInfoGrupo())
        _kolis.add(koli)
        return true
    }

    val miembros: ArrayList<Personaje>
        get() {
            val grupo: ArrayList<Personaje> = ArrayList<Personaje>()
            grupo.addAll(_kolis)
            return grupo
        }

    fun contieneIPOtroGrupo(grupo: GrupoKoliseo): Boolean {
        if (MainServidor.PARAM_PERMITIR_MULTICUENTA_PELEA_KOLISEO) {
            return false
        }
        for (p in _kolis) {
            for (p2 in grupo._kolis) {
                if (p.getCuenta().getActualIP().equalsIgnoreCase(p2.getCuenta().getActualIP())) {
                    return true
                }
            }
        }
        return false
    }

    init {
        _kolis.add(koli1)
    }
}