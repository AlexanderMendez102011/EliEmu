package variables.npc

import estaticos.MainServidor

class NPC(npcModelo: NPCModelo, id: Int, celda: Short, o: Byte, condiciones: String) : Exchanger {
    private val _npcModelo: NPCModelo?
    var orientacion: Byte
    var celdaID: Short
    val iD: Int
    val condiciones: String
    private var _evento = false
    var isPortal = false
    val modeloID: Int
        get() = _npcModelo.getID()
    val modelo: NPCModelo?
        get() = _npcModelo

    fun getPreguntaID(perso: Personaje?): Int {
        return if (_npcModelo == null) {
            0
        } else _npcModelo.getPreguntaID(perso)
    }

    fun strinGM(perso: Personaje?): String {
        val str = StringBuilder()
        str.append("$celdaID;")
        str.append("$orientacion;")
        str.append("0" + ";")
        str.append("$iD;")
        str.append(_npcModelo.getID().toString() + ";")
        str.append("-4" + ";") // tipo = NPC
        str.append(_npcModelo.getGfxID().toString() + "^" + _npcModelo.getTallaX() + "x" + _npcModelo.getTallaY() + ";")
        str.append(_npcModelo.getSexo().toString() + ";")
        str.append((if (_npcModelo.getColor1() !== -1) Integer.toHexString(_npcModelo.getColor1()) else "-1") + ";")
        str.append((if (_npcModelo.getColor2() !== -1) Integer.toHexString(_npcModelo.getColor2()) else "-1") + ";")
        str.append((if (_npcModelo.getColor3() !== -1) Integer.toHexString(_npcModelo.getColor3()) else "-1") + ";")
        str.append(_npcModelo.getAccesoriosHex().toString() + ";")
        str.append(_npcModelo.getExtraClip(perso).toString() + ";")
        str.append(_npcModelo.getFoto().toString() + ";")
        return str.toString()
    }

    @Override
    fun addKamas(k: Long, perso: Personaje?) {
    }

    @get:Override
    val kamas: Long
        get() = 0

    @Override
    @Synchronized
    fun addObjetoExchanger(objeto: Objeto?, cantidad: Long, perso: Personaje?, precio: Int) {
    }

    @Override
    @Synchronized
    fun remObjetoExchanger(objeto: Objeto?, cantidad: Long, perso: Personaje?, precio: Int) {
    }

    @Override
    fun cerrar(perso: Personaje, exito: String?) {
        perso.cerrarVentanaExchange(exito)
    }

    fun botonOK(perso: Personaje?) {}
    @Override
    fun getListaExchanger(perso: Personaje?): String {
        return _npcModelo.listaObjetosAVender()
    }

    fun is_evento(): Boolean {
        return _evento
    }

    fun set_evento(_evento: Boolean) {
        this._evento = _evento
    }

    init {
        _npcModelo = npcModelo
        set_evento(npcModelo.getID() === MainServidor.EVENTO_NPC || npcModelo.getID() === MainServidor.EVENTO_NPC_2)
        isPortal = npcModelo.getID() === MainServidor.PORTAL_NPC
        iD = id
        celdaID = celda
        orientacion = o
        this.condiciones = condiciones
    }
}