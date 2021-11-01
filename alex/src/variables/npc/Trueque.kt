package variables.npc

import java.util.ArrayList

class Trueque(perso: Personaje, resucitar: Boolean, npcID: Int) : Exchanger {
    private val _perso: Personaje
    private val _entregar: ArrayList<Duo<Integer, Long>> = ArrayList<Duo<Integer, Long>>()
    private var _dar: Map<Integer, Long> = HashMap<Integer, Long>()
    private val _objetosModelo: Map<Integer, Long?> = HashMap<Integer, Long>()
    private var _ok = false
    private val _resucitar: Boolean
    private var _polvo = false
    private var _idMascota = 0
    private var _npcID = 0
    @Synchronized
    fun botonOK(perso: Personaje?) {
        _ok = !_ok
        GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_perso, _ok, _perso.getID())
        if (_ok) {
            aplicar()
        }
    }

    @Synchronized
    fun cerrar(perso: Personaje?, exito: String?) {
        _perso.cerrarVentanaExchange(exito)
    }

    @Synchronized
    fun aplicar() {
        var mascota: Objeto? = null
        if (_resucitar && _polvo) {
            for (duo in _entregar) {
                val cant: Long = duo._segundo
                if (cant == 0L) {
                    continue
                }
                val obj: Objeto = _perso.getObjeto(duo._primero)
                if (obj != null) {
                    if (obj.getObjModelo().getTipo() === Constantes.OBJETO_TIPO_FANTASMA_MASCOTA) {
                        GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_perso, duo._primero)
                        mascota = obj
                    }
                }
            }
            if (mascota != null) {
                for (duo in _entregar) {
                    val cant: Long = duo._segundo
                    if (cant == 0L) {
                        continue
                    }
                    val obj: Objeto = _perso.getObjeto(duo._primero)
                    if (obj != null) {
                        val nuevaCant: Long = obj.getCantidad() - cant
                        if (nuevaCant <= 0) {
                            _perso.borrarOEliminarConOR(duo._primero, true)
                        } else {
                            obj.setCantidad(nuevaCant)
                            GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, obj)
                        }
                    }
                }
                mascota.setPDV(1)
                mascota.setCantidad(1)
                mascota.setIDOjbModelo(Mundo.getMascotaPorFantasma(mascota.getObjModeloID()))
                _perso.addObjetoConOAKO(mascota, true)
                cerrar(_perso, "a")
            }
        } else if (!_dar.isEmpty()) {
            for (duo in _entregar) {
                val cant: Long = duo._segundo
                if (cant == 0L) {
                    continue
                }
                val obj: Objeto = _perso.getObjeto(duo._primero)
                if (obj != null) {
                    val nuevaCant: Long = obj.getCantidad() - cant
                    if (nuevaCant <= 0) {
                        _perso.borrarOEliminarConOR(duo._primero, true)
                    } else {
                        obj.setCantidad(nuevaCant)
                        GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, obj)
                    }
                }
            }
            for (entry in _dar.entrySet()) {
                try {
                    val idObjModelo: Int = entry.getKey()
                    val cantidad: Long = entry.getValue()
                    _perso.addObjIdentAInventario(Mundo.getObjetoModelo(idObjModelo).crearObjeto(cantidad,
                            Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM), false)
                } catch (e: Exception) {
                }
            }
            cerrar(_perso, "a")
        }
        //cerrar(_perso, "a");
    }

    @Override
    @Synchronized
    fun addObjetoExchanger(objeto: Objeto, cantidad: Long, perso: Personaje, precio: Int) {
        var cantidad = cantidad
        if (!perso.tieneObjetoID(objeto.getID()) || objeto.getPosicion() !== Constantes.OBJETO_POS_NO_EQUIPADO) {
            GestorSalida.ENVIAR_Im_INFORMACION(perso, "1OBJECT_DONT_EXIST")
            return
        }
        if (_ok) {
            _ok = false
            GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_perso, _ok, _perso.getID())
        }
        val idModelo: Int = objeto.getObjModeloID()
        val cantInter = getCantObjeto(objeto.getID())
        if (cantidad > objeto.getCantidad() - cantInter) {
            cantidad = objeto.getCantidad() - cantInter
        }
        if (cantidad <= 0) {
            return
        }
        var duo: Duo<Integer?, Long?>? = Mundo.getDuoPorIDPrimero1(_entregar, objeto.getID())
        if (_objetosModelo[idModelo] != null) {
            _objetosModelo.put(idModelo, _objetosModelo[idModelo]!! + cantidad)
        } else {
            _objetosModelo.put(idModelo, cantidad)
        }
        if (duo != null) {
            duo._segundo += cantidad
        } else {
            duo = Duo<Integer, Long>(objeto.getID(), cantidad)
            _entregar.add(duo)
        }
        GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_perso, 'O', "+", objeto.getID().toString() + "|" + duo._segundo)
        refrescar()
    }

    @Override
    @Synchronized
    fun remObjetoExchanger(objeto: Objeto, cantidad: Long, perso: Personaje?, precio: Int) {
        val idModelo: Int = objeto.getObjModeloID()
        //final int cantInter = getCantObjeto(objeto.getID());
        /*if (cantidad > objeto.getCantidad() - cantInter) {
			cantidad = objeto.getCantidad() - cantInter;
		}*/if (cantidad <= 0) {
            return
        }
        if (_ok) {
            _ok = false
            GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_perso, _ok, _perso.getID())
        }
        val duo: Duo<Integer, Long> = Mundo.getDuoPorIDPrimero1(_entregar, objeto.getID()) ?: return
        try {
            _objetosModelo.put(idModelo, _objetosModelo[idModelo]!! - cantidad)
            if (_objetosModelo[idModelo]!! <= 0) {
                _objetosModelo.remove(idModelo)
            }
        } catch (e: Exception) {
        }
        duo._segundo -= cantidad
        if (duo._segundo <= 0) {
            _entregar.remove(duo)
            GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_perso, 'O', "-", objeto.getID().toString() + "")
        } else {
            GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_perso, 'O', "+", objeto.getID().toString() + "|" + duo._segundo)
        }
        refrescar()
    }

    private fun refrescar() {
        if (!_resucitar) {
            var i = 100000000
            for (xx in 0 until _dar.size()) {
                GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_perso, 'O', "-", "" + i++)
            }
        } else {
            GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_perso, 'O', "-", "" + _idMascota)
        }
        var mascota: Objeto? = null
        _polvo = false
        _idMascota = 0
        for (duo in _entregar) {
            val objModelo: ObjetoModelo = Mundo.getObjeto(duo._primero).getObjModelo()
            if (_resucitar) {
                if (objModelo.getTipo() === Constantes.OBJETO_TIPO_FANTASMA_MASCOTA) { // fantasma
                    mascota = Mundo.getObjeto(duo._primero)
                    _idMascota = duo._primero
                }
                if (objModelo.getID() === 8012) { // polvo de resurreccion
                    _polvo = true
                }
            }
        }
        if (_resucitar) {
            if (mascota != null && _polvo) {
                GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_perso, 'O', "+", mascota.getID().toString() + "|1|" + Mundo
                        .getMascotaPorFantasma(mascota.getObjModeloID()) + "|" + mascota.convertirStatsAString(false))
            }
        } else {
            var i = 100000000
            _dar = Mundo.listaObjetosTruequePor(_objetosModelo, _npcID)
            for (entry in _dar.entrySet()) {
                GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_perso, 'O', "+", i++.toString() + "|" + entry.getValue() + "|" + entry
                        .getKey() + "|" + Mundo.getObjetoModelo(entry.getKey()).stringStatsModelo())
            }
        }
    }

    @Synchronized
    fun getCantObjeto(objetoID: Int): Long {
        for (duo in _entregar) {
            if (duo._primero === objetoID) {
                return duo._segundo
            }
        }
        return 0
    }

    @Override
    fun addKamas(k: Long, perso: Personaje?) {
    }

    @get:Override
    val kamas: Long
        get() = 0

    @Override
    fun getListaExchanger(perso: Personaje?): String? {
        // TODO Auto-generated method stub
        return null
    }

    init {
        _perso = perso
        _resucitar = resucitar
        _npcID = npcID
    }
}