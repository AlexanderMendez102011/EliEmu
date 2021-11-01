package variables.npc

import java.util.ArrayList
import variables.gremio.Gremio
import variables.mision.Mision
import variables.mision.MisionModelo
import variables.mision.MisionPregunta
import variables.objeto.ObjetoModelo
import variables.personaje.Personaje
import estaticos.Condiciones
import estaticos.GestorSQL
import estaticos.MainServidor
import estaticos.Mundo

class NPCModelo(val iD: Int, var gfxID: Int, escalaX: Short, escalaY: Short, sexo: Byte,
                color1: Int, color2: Int, color3: Int, foto: Int, preguntaID: Int, objVender: String,
                nombre: String, arma: Int, sombrero: Int, capa: Int, mascota: Int, escudo: Int) {
    var sexo: Byte
        private set
    var tallaX: Int
        private set
    var tallaY: Int
        private set
    val foto: Int
    var color1: Int
        private set
    var color2: Int
        private set
    var color3: Int
        private set
    private var _arma = 0
    private var _sombrero = 0
    private var _capa = 0
    private var _mascota = 0
    private var _escudo = 0
    private var _preguntaID: Int
    var accesoriosHex: String? = null
        private set
    val nombre: String
    private var _listaObjetos = ""
    private var _gremio: Gremio? = null
    private val _objVender: ArrayList<ObjetoModelo> = ArrayList<ObjetoModelo>()
    private val _misiones: ArrayList<MisionModelo> = ArrayList<MisionModelo>()
    fun setAccesorios(accesorios: String?) {
        accesoriosHex = accesorios
    }

    fun setAccesorios(arma: Int, sombrero: Int, capa: Int, mascota: Int, escudo: Int) {
        _arma = arma
        _sombrero = sombrero
        _capa = capa
        _mascota = mascota
        _escudo = escudo
        accesoriosHex = (Integer.toHexString(arma).toString() + "," + Integer.toHexString(sombrero) + "," + Integer.toHexString(capa)
                + "," + Integer.toHexString(mascota) + "," + Integer.toHexString(escudo))
    }

    val accesoriosInt: String
        get() = "$_arma,$_sombrero,$_capa,$_mascota,$_escudo"

    fun addMision(mision: MisionModelo?) {
        if (!_misiones.contains(mision)) {
            _misiones.add(mision)
        }
    }

    fun getExtraClip(perso: Personaje?): String {
        if (perso == null) {
            return ""
        }
        for (mision in _misiones) {
            if (perso.tieneMision(mision.getID())) {
                continue
            }
            return "4" // signo de admiracion
        }
        return ""
    }

    fun setPreguntaID(pregunta: Int) {
        _preguntaID = pregunta
    }

    fun getPreguntaID(perso: Personaje?): Int {
        if (perso != null) {
            var completado = -1
            var noTiene = -1
            var incompleto = -1
            var preg: MisionPregunta
            for (misionMod in _misiones) {
                when (perso.getEstadoMision(misionMod.getID())) {
                    Mision.ESTADO_COMPLETADO -> {
                        if (misionMod.getPuedeRepetirse()) {
                            perso.borrarMision(misionMod.getID())
                        }
                        preg = misionMod.getMisionPregunta(Mision.ESTADO_COMPLETADO)
                        if (preg.getNPCID() !== iD || preg.getPreguntaID() === 0) {
                            continue
                        }
                        if (Condiciones.validaCondiciones(perso, preg.getCondicion(), null)) {
                            completado = preg.getPreguntaID()
                        }
                    }
                    Mision.ESTADO_INCOMPLETO -> {
                        preg = misionMod.getMisionPregunta(Mision.ESTADO_INCOMPLETO)
                        if (preg.getNPCID() !== iD || preg.getPreguntaID() === 0) {
                            continue
                        }
                        if (Condiciones.validaCondiciones(perso, preg.getCondicion(), null)) {
                            incompleto = preg.getPreguntaID()
                        }
                    }
                    Mision.ESTADO_NO_TIENE -> {
                        preg = misionMod.getMisionPregunta(Mision.ESTADO_NO_TIENE)
                        if (preg.getNPCID() !== iD || preg.getPreguntaID() === 0) {
                            continue
                        }
                        if (Condiciones.validaCondiciones(perso, preg.getCondicion(), null)) {
                            noTiene = preg.getPreguntaID()
                        }
                    }
                }
            }
            if (incompleto != -1) {
                return incompleto
            }
            if (noTiene != -1) {
                return noTiene
            }
            if (completado != -1) {
                return completado
            }
        }
        return _preguntaID
    }

    fun modificarNPC(sexo: Byte, escalaX: Short, escalaY: Short, gfxID: Int,
                     color1: Int, color2: Int, color3: Int, gremio: Gremio?) {
        this.sexo = sexo
        tallaX = escalaX.toInt()
        tallaY = escalaY.toInt()
        this.gfxID = gfxID
        this.color1 = color1
        this.color2 = color2
        this.color3 = color3
        _gremio = gremio

        // GestorSQL.ACTUALIZAR_NPC_COLOR_SEXO(this);
    }

    fun actualizarObjetosAVender() {
        if (_objVender.isEmpty()) {
            _listaObjetos = ""
        }
        val objetos = StringBuilder()
        for (obj in _objVender) {
            if (MainServidor.OBJETOS_OGRINAS_NPC.containsKey(obj.getID())) {
                if (MainServidor.OBJETOS_OGRINAS_NPC.get(obj.getID()) === iD) {
                    objetos.append(obj.stringDeStatsParaTienda(true).toString() + "|")
                } else {
                    objetos.append(obj.stringDeStatsParaTienda(false).toString() + "|")
                }
            } else {
                objetos.append(obj.stringDeStatsParaTienda(false).toString() + "|")
            }
        }
        _listaObjetos = objetos.toString()
    }

    val objAVender: ArrayList<ObjetoModelo>
        get() = _objVender

    fun actualizarStringBD(): String {
        if (_objVender.isEmpty()) {
            return ""
        }
        val objetos = StringBuilder()
        for (obj in _objVender) {
            objetos.append(obj.getID().toString() + ",")
        }
        return objetos.toString()
    }

    fun listaObjetosAVender(): String {
        return _listaObjetos
    }

    val gremio: Gremio?
        get() = if (_gremio == null) {
            null
        } else _gremio

    fun addObjetoAVender(objetos: ArrayList<ObjetoModelo?>) {
        var retorna = false
        for (obj in objetos) {
            if (_objVender.contains(obj)) {
                continue
            }
            _objVender.add(obj)
            retorna = true
        }
        if (!retorna) {
            return
        }
        actualizarObjetosAVender()
        GestorSQL.ACTUALIZAR_NPC_VENTAS(this)
    }

    fun borrarObjetoAVender(objetos: ArrayList<ObjetoModelo?>) {
        var retorna = false
        for (obj in objetos) {
            if (_objVender.remove(obj)) {
                retorna = true
            }
        }
        if (!retorna) {
            return
        }
        actualizarObjetosAVender()
        GestorSQL.ACTUALIZAR_NPC_VENTAS(this)
    }

    fun borrarTodosObjVender() {
        _objVender.clear()
        actualizarObjetosAVender()
        GestorSQL.ACTUALIZAR_NPC_VENTAS(this)
    }

    fun tieneObjeto(idModelo: Int): Boolean {
        for (OM in _objVender) {
            if (OM.getID() === idModelo) {
                return true
            }
        }
        return false
    }

    init {
        // super();
        tallaX = escalaX.toInt()
        tallaY = escalaY.toInt()
        this.sexo = sexo
        this.color1 = color1
        this.color2 = color2
        this.color3 = color3
        setAccesorios(arma, sombrero, capa, mascota, escudo)
        this.foto = foto
        _preguntaID = preguntaID
        this.nombre = nombre
        if (!objVender.isEmpty()) {
            var objModelo: ObjetoModelo
            for (obj in objVender.split(",")) {
                try {
                    objModelo = Mundo.getObjetoModelo(Integer.parseInt(obj))
                    if (objModelo == null) {
                        continue
                    }
                    _objVender.add(objModelo)
                } catch (e: Exception) {
                }
            }
            actualizarObjetosAVender()
        }
    }
}