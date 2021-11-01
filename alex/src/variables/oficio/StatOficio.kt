package variables.oficio

import java.util.ArrayList

class StatOficio(val posicion: Byte, oficio: Oficio, exp: Int) {
    var slotsPublico: Byte
    var nivel = 0
        private set
    var adicional = 0
        private set
    private var _experiencia = 0
    private var _trabajosPoderRealizar: ArrayList<Trabajo> = ArrayList<Trabajo>()
    private var _esPagable = false
    private var _gratisSiFalla = false
    private var _noProporcRecurso = false

    // FIXME solo para q se vean todos
    var libroArtesano: Boolean
    private val _oficio: Oficio
    private var _tempTrabajo: Trabajo? = null
    fun trabajosARealizar(): ArrayList<Trabajo> {
        return _trabajosPoderRealizar
    }

    fun esPagable(): Boolean {
        return _esPagable
    }

    fun esGratisSiFalla(): Boolean {
        return _gratisSiFalla
    }

    fun noProveerRecuerso(): Boolean {
        return _noProporcRecurso
    }

    fun subirNivel() {
        if (posicion.toInt() == 7) {
            return
        }
        nivel++
        _trabajosPoderRealizar = Constantes.getTrabajosPorOficios(_oficio.getID(), nivel, this)
        _trabajosPoderRealizar.trimToSize()
        adicional = Math.sqrt(nivel / 2)
    }

    fun stringSKillsOficio(): String {
        val str = StringBuilder()
        for (trabajo in _trabajosPoderRealizar) {
            if (str.length() > 0) {
                str.append(",")
            }
            str.append(trabajo.getTrabajoID().toString() + "~")
            if (trabajo.esCraft()) {
                str.append(trabajo.getCasillasMax().toString() + "~" + "0~0~" + trabajo.getSuerte())
            } else {
                str.append(trabajo.getCasillasMin().toString() + "~" + trabajo.getCasillasMax() + "~0~" + trabajo.getTiempo())
            }
        }
        return "|" + _oficio.getID().toString() + ";" + str.toString()
    }

    val exp: Long
        get() = _experiencia.toLong()

    @Synchronized
    fun iniciarTrabajo(trabajoID: Int, perso: Personaje?, OI: ObjetoInteractivo?,
                       idUnica: AccionDeJuego?, celda: Celda?): Boolean {
        for (trabajo in _trabajosPoderRealizar) {
            if (trabajo.getTrabajoID() === trabajoID) {
                _tempTrabajo = trabajo
                // perso.setTrabajo(_tempTrabajo);
                //System.out.println(7);
                trabajo.iniciarAccionTrabajo(perso, OI, idUnica, celda)
            }
        }
        // no puede realizara el trabajo
        return false
    }

    @Synchronized
    fun finalizarTrabajo(perso: Personaje): Boolean {
        if (_tempTrabajo == null) {
            return false
        }
        var r = true
        if (!_tempTrabajo.esCraft()) {
            // recolecta
            perso.setIsOnAction(false)
            perso.Marche = false
            r = finalizarRecoleccion(perso)
        }
        if (r) {
            _tempTrabajo = null
        }
        return r
        // perso.setTrabajo(_tempTrabajo);
    }

    @Synchronized
    private fun finalizarRecoleccion(perso: Personaje): Boolean {
        if (_tempTrabajo == null) {
            return false
        }
        if (!_tempTrabajo.puedeFinalizarRecolecta()) {
            return false
        }
        perso.setIsOnAction(false)
        perso.Marche = false
        val protector: Int = Constantes.getProtectorRecursos(_tempTrabajo.getTrabajoID(), _oficio.getID())
        val bProtector = (Formulas.getRandomInt(1, 100) < MainServidor.PROBABILIDAD_PROTECTOR_RECURSOS
                && protector != 0 && nivel >= 20)
        val experiencia: Int = _tempTrabajo.getExpFinalizarRecoleccion()
        addExperiencia(perso, experiencia, 2)
        if (bProtector && perso.enLinea()) {
            val nivel: Int = Constantes.getNivelProtector(nivel)
            val grupoMob = GrupoMob(perso.getMapa(), perso.getCelda().getID(), protector.toString() + "," + nivel + ","
                    + nivel, TipoGrupo.SOLO_UNA_PELEA, "", true, 0.toByte(), 0, "", 0)
            perso.getMapa().iniciarPelea(perso, null, perso.getCelda().getID(), -1.toShort(),
                    Constantes.PELEA_TIPO_PVM_NO_ESPADA, grupoMob, false)
        } else {
            _tempTrabajo.recogerRecolecta()
        }
        return true
    }

    fun addExperiencia(perso: Personaje?, exp: Int, tipo: Int) {
        var exp = exp
        if (posicion.toInt() == 7 || nivel >= MainServidor.NIVEL_MAX_OFICIO) {
            return
        }
        when (tipo) {
            Constantes.OFICIO_EXP_TIPO_RECOLECCION -> if (perso.realizoMisionDelDia()) {
                val almanax: Almanax = Mundo.getAlmanaxDelDia()
                if (almanax != null && almanax.getTipo() === Constantes.ALMANAX_BONUS_EXP_OFICIO_RECOLECCION) {
                    exp += exp * almanax.getBonus() / 100
                }
            }
            Constantes.OFICIO_EXP_TIPO_CRAFT -> if (perso.realizoMisionDelDia()) {
                val almanax: Almanax = Mundo.getAlmanaxDelDia()
                if (almanax != null && almanax.getTipo() === Constantes.ALMANAX_BONUS_EXP_OFICIO_CRAFT) {
                    exp += exp * almanax.getBonus() / 100
                }
            }
        }
        val exNivel = nivel
        _experiencia += exp
        // _exp = Math.min(_exp + exp, Mundo.getExpOficio()(Bustemu.NIVEL_MAX_OFICIO)._oficio);
        while (_experiencia >= Mundo.getExpOficio(nivel + 1) && nivel < MainServidor.NIVEL_MAX_OFICIO) {
            subirNivel()
        }
        if (perso != null && perso.enLinea()) {
            if (nivel > exNivel) {
                GestorSalida.ENVIAR_JS_SKILL_DE_OFICIO(perso, this)
                GestorSalida.ENVIAR_JN_OFICIO_NIVEL(perso, _oficio.getID(), nivel)
                GestorSalida.ENVIAR_JO_OFICIO_OPCIONES(perso, this)
                GestorSalida.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(perso)
                GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso)
            }
            GestorSalida.ENVIAR_JX_EXPERINENCIA_OFICIO(perso, this)
        }
    }

    fun getExpString(s: String): String {
        return Mundo.getExpOficio(nivel) + s + _experiencia + s + Mundo.getExpOficio(nivel + 1)
    }

    val oficio: Oficio
        get() = _oficio

    fun esValidoTrabajo(id: Int): Boolean {
        for (AT in _trabajosPoderRealizar) {
            if (AT.getTrabajoID() === id) {
                return true
            }
        }
        return false
    }

    val opcionBin: Int
        get() {
            var nro = 0
            nro += if (_noProporcRecurso) 4 else 0
            nro += if (_gratisSiFalla) 2 else 0
            nro += if (_esPagable) 1 else 0
            return nro
        }

    fun setOpciones(bin: Int) {
        _noProporcRecurso = bin and 4 == 4
        _gratisSiFalla = bin and 2 == 2
        _esPagable = bin and 1 == 1
    }

    init {
        _oficio = oficio
        addExperiencia(null, exp, 0)
        if (_trabajosPoderRealizar.isEmpty()) {
            _trabajosPoderRealizar = Constantes.getTrabajosPorOficios(oficio.getID(), nivel, this)
            _trabajosPoderRealizar.trimToSize()
        }
        slotsPublico = Constantes.getIngMaxPorNivel(nivel)
        libroArtesano = false
    }
}