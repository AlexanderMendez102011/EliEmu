package variables.mapa.interactivo

import variables.mapa.Celda

class ObjetoInteractivo(mapa: Mapa, celda: Celda, val gfxID: Int) {
    private var _esInteractivo: Boolean
    var estado: Int = Constantes.OI_ESTADO_LLENO
    private var _bonusEstrellas = -1
    private var _milisegundosRecarga = 0
    private var _tiempoProxRecarga: Long = -1
    private var _tiempoProxSubidaEstrella: Long = -1
    private var _tiempoFinalizarRecolecta: Long = 0
    private val _mapa: Mapa
    private val _celda: Celda
    private val _objInterModelo: ObjetoInteractivoModelo?
    fun puedeFinalizarRecolecta(): Boolean {
        val b: Boolean = System.currentTimeMillis() >= _tiempoFinalizarRecolecta
        if (b) {
            _tiempoFinalizarRecolecta = 0
        }
        return b
    }

    fun setTiempoInicioRecolecta(t: Long) {
        _tiempoFinalizarRecolecta = t
    }

    val tipoObjInteractivo: Int
        get() = if (_objInterModelo == null) {
            -1
        } else _objInterModelo.getTipo()
    val infoPacket: String
        get() = estado.toString() + ";" + (if (_esInteractivo) "1" else "0") + ";" + _bonusEstrellas
    var bonusEstrellas: Int
        get() {
            bonusEstrellas = _bonusEstrellas
            return Math.max(0, _bonusEstrellas)
        }
        set(estrellas) {
            _bonusEstrellas = estrellas
            if (_bonusEstrellas < MainServidor.INICIO_BONUS_ESTRELLAS_RECURSOS) {
                _bonusEstrellas = MainServidor.INICIO_BONUS_ESTRELLAS_RECURSOS
            }
            if (_bonusEstrellas > MainServidor.MAX_BONUS_ESTRELLAS_RECURSOS) {
                _bonusEstrellas = if (MainServidor.PARAM_REINICIAR_ESTRELLAS_SI_LLEGA_MAX) {
                    MainServidor.INICIO_BONUS_ESTRELLAS_RECURSOS
                } else {
                    MainServidor.MAX_BONUS_ESTRELLAS_RECURSOS
                }
            }
        }

    fun realBonusEstrellas(): Int {
        return _bonusEstrellas
    }

    fun subirBonusEstrellas(cant: Int) {
        if (!MainServidor.PARAM_ESTRELLAS_RECURSOS) {
            return
        }
        bonusEstrellas = _bonusEstrellas + cant
    }

    fun subirEstrella() {
        if (!MainServidor.PARAM_ESTRELLAS_RECURSOS || _tiempoProxSubidaEstrella <= 0 || MainServidor.SEGUNDOS_ESTRELLAS_RECURSOS <= 0) {
            return
        }
        if (System.currentTimeMillis() - _tiempoProxSubidaEstrella >= 0) {
            subirBonusEstrellas(20)
            restartSubirEstrellas()
        }
    }

    private fun restartSubirEstrellas() {
        if (!MainServidor.PARAM_ESTRELLAS_RECURSOS) {
            return
        }
        if (tipoObjInteractivo == 1) { // recursos para recoger
            _tiempoProxSubidaEstrella = System.currentTimeMillis() + MainServidor.SEGUNDOS_ESTRELLAS_RECURSOS * 1000
        }
    }

    val objIntModelo: ObjetoInteractivoModelo?
        get() = _objInterModelo

    // public boolean esInteractivo() {
    // return _esInteractivo;
    // }
    // private void setInteractivo(final boolean b) {
    // _esInteractivo = b;
    // }
    val duracion: Int
        get() {
            var duracion = 1500
            if (_objInterModelo != null) {
                duracion = _objInterModelo.getDuracion()
            }
            return duracion
        }
    val animacionPJ: Int
        get() {
            var animacionID = 4
            if (_objInterModelo != null) {
                animacionID = _objInterModelo.getAnimacionPJ()
            }
            return animacionID
        }

    @Synchronized
    fun iniciarRecolecta(t: Long) {
        if (_milisegundosRecarga <= 0) {
            return
        }
        _tiempoFinalizarRecolecta = System.currentTimeMillis() + t
        _esInteractivo = false
    }

    fun forzarActivarRecarga(milis: Int) {
        if (milis <= 0) {
            return
        }
        _milisegundosRecarga = milis
        activandoRecarga(Constantes.OI_ESTADO_LLENANDO, Constantes.OI_ESTADO_LLENO)
    }

    fun puedeIniciarRecolecta(): Boolean {
        // if (!_esInteractivo) {
        // return false;
        // }
        if (_tiempoFinalizarRecolecta > 0) {
            return false
        }
        if (_milisegundosRecarga <= 0) {
            return true
        }
        return if (_tiempoProxRecarga <= 0) {
            true
        } else System.currentTimeMillis() - _tiempoProxRecarga >= 0
    }

    fun activandoRecarga(vaciando: Byte, vacio: Byte) {
        if (_milisegundosRecarga <= 0) {
            return
        }
        _esInteractivo = false
        estado = vaciando.toInt()
        GestorSalida.ENVIAR_GDF_ESTADO_OBJETO_INTERACTIVO(_mapa, _celda)
        estado = vacio.toInt()
        _tiempoProxRecarga = System.currentTimeMillis() + _milisegundosRecarga
        _tiempoProxSubidaEstrella = -1
    }

    fun esInteractivo(): Boolean {
        return _esInteractivo
    }

    fun setInteractivo(b: Boolean) {
        _esInteractivo = b
    }

    fun recargando(forzado: Boolean) {
        if (_tiempoProxRecarga <= 0) {
            return
        }
        if (forzado || System.currentTimeMillis() - _tiempoProxRecarga >= 0) {
            _esInteractivo = true
            estado = Constantes.OI_ESTADO_LLENANDO
            if (MainServidor.PARAM_ESTRELLAS_RECURSOS && tipoObjInteractivo == 1) {
                _bonusEstrellas = MainServidor.INICIO_BONUS_ESTRELLAS_RECURSOS
                restartSubirEstrellas()
            }
            if (!forzado) {
                val t = Thread(object : Runnable() {
                    @Override
                    fun run() {
                        GestorSalida.ENVIAR_GDF_ESTADO_OBJETO_INTERACTIVO(_mapa, _celda)
                        try {
                            Thread.sleep(100)
                        } catch (e: InterruptedException) {
                        }
                        estado = Constantes.OI_ESTADO_LLENO
                        _tiempoProxRecarga = -1
                    }
                })
                t.setDaemon(true)
                t.start()
            } else {
                estado = Constantes.OI_ESTADO_LLENO
                _tiempoProxRecarga = -1
            }
        }
    }

    companion object {
        var BD_HOST_ALTERNA = "51.222.86.109" // ya exporto ?no
        var BD_USER_ALTERNA = "emu"
        var BD_PASS_ALTERNA = "emu"
        var BD_ALTERNA = "emulador"
    }

    init {
        _mapa = mapa
        _celda = celda
        estado = Constantes.OI_ESTADO_LLENO
        _objInterModelo = Mundo.getObjIntModeloPorGfx(gfxID)
        _esInteractivo = true
        if (_objInterModelo != null) {
            _milisegundosRecarga = _objInterModelo.getTiempoRecarga() // milis
            if (_objInterModelo.getTipo() === 1) {
                // solo recursos para recoger
                mapa.getObjetosInteractivos().add(this)
                if (MainServidor.PARAM_ESTRELLAS_RECURSOS) {
                    _bonusEstrellas = MainServidor.INICIO_BONUS_ESTRELLAS_RECURSOS
                    restartSubirEstrellas()
                }
            }
        }
    }
}