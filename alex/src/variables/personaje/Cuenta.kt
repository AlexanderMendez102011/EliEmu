package variables.personaje

import java.util.ArrayList

class Cuenta(val iD: Int, val nombre: String) {
    private var _muteado = false
    var tiempoMuteado: Long = 0
    var horaMuteado: Long = 0
    var ultimoReporte: Long = 0
    private var _ultVotoMilis: Long = 0
    var actualIP = ""
    var ultimaIP = ""
    var ultimaConexion = ""
        private set
    private var _idioma = "es"
    private var _entradaPersonaje: ServidorSocket? = null
    private var _tempPerso: Personaje? = null
    private val _idsAmigos: ArrayList<Integer> = ArrayList<Integer>()
    private val _idsEnemigos: ArrayList<Integer> = ArrayList<Integer>()
    private val _personajes: Map<Integer, Personaje> = TreeMap<Integer, Personaje>()
    private val _idsReportes: Map<Byte, ArrayList<Integer>?> = TreeMap<Byte, ArrayList<Integer>>()
    private val _establo: ConcurrentHashMap<Integer, Montura> = ConcurrentHashMap<Integer, Montura>()
    private val _mensajes: ArrayList<String> = ArrayList<String>()
    private val _banco: Cofre = Cofre(-1.toShort(), -1.toShort(), 0.toShort(), 0.toShort(), 99999)
    var claveseguridad = ""
    var idioma = "es"
    fun addMensaje(str: String?, salvar: Boolean) {
        _mensajes.add(str)
        if (salvar) {
            GestorSQL.UPDATE_MENSAJES_CUENTA(nombre, stringMensajes())
        }
    }

    fun stringMensajes(): String {
        if (_mensajes.isEmpty()) {
            return ""
        }
        val str = StringBuilder()
        for (s in _mensajes) {
            if (str.length() > 0) str.append("&")
            str.append(s)
        }
        return str.toString()
    }

    val mensajes: ArrayList<String>
        get() = _mensajes
    val vip: Int
        get() = GestorSQL.GET_VIP(nombre)

    /*private void objeto_Desasociar_Mimobionte(Objeto objeto) {
		try {
			if(objeto.tieneStatTexto(Constantes.STAT_APARIENCIA_OBJETO)) {
				int regresar = Integer.parseInt(objeto.getParamStatTexto(Constantes.STAT_APARIENCIA_OBJETO, 3), 16);
				Objeto apariencia = Mundo.getObjetoModelo(regresar).crearObjeto(1,Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM);
				Mundo.addObjeto(apariencia, true);
				objeto.addStatTexto(Constantes.STAT_APARIENCIA_OBJETO, "");
				_banco.addObjetoRapido(apariencia);
				GestorSQL.SALVAR_OBJETO(objeto);
				System.out.println("Cofre: "+_id+" disasicion objeto: "+objeto.getObjModelo().getNombre());
			}
		} catch (Exception e) {
		}
	}*/
    fun cargarInfoServerPersonaje(banco: String, kamasBanco: Long, amigos: String,
                                  enemigos: String, establo: String, reportes: String, ultimaConexion: String, mensajes: String,
                                  ultimaIP: String) {
        addKamasBanco(kamasBanco)
        this.ultimaConexion = ultimaConexion
        this.ultimaIP = ultimaIP
        for (s in banco.split(Pattern.quote("|"))) {
            try {
                if (s.isEmpty()) {
                    continue
                }
                val obj: Objeto = Mundo.getObjeto(Integer.parseInt(s)) ?: continue
                //objeto_Desasociar_Mimobionte(obj);
                _banco.addObjetoRapido(obj)
            } catch (e: Exception) {
            }
        }
        for (s in amigos.split(";")) {
            try {
                if (s.isEmpty()) continue
                _idsAmigos.add(Integer.parseInt(s))
            } catch (e: Exception) {
            }
        }
        for (s in enemigos.split(";")) {
            try {
                if (s.isEmpty()) continue
                _idsEnemigos.add(Integer.parseInt(s))
            } catch (e: Exception) {
            }
        }
        _idsAmigos.trimToSize()
        _idsEnemigos.trimToSize()
        for (s in establo.split(";")) {
            try {
                if (s.isEmpty()) continue
                val montura: Montura = Mundo.getMontura(Integer.parseInt(s))
                if (montura != null) {
                    addMonturaEstablo(montura)
                }
            } catch (e: Exception) {
            }
        }
        var i: Byte = 0
        for (s in reportes.split(Pattern.quote("|"))) {
            val array: ArrayList<Integer> = ArrayList<Integer>()
            for (f in s.split(";")) {
                try {
                    if (f.isEmpty()) continue
                    array.add(Integer.parseInt(f))
                } catch (e: Exception) {
                }
            }
            _idsReportes.put(i, array)
            i++
        }
        for (s in mensajes.split("&")) {
            if (s.isEmpty()) continue
            _mensajes.add(s)
        }
        try {
            if (ultimaConexion.isEmpty() || !MainServidor.PARAM_BORRAR_CUENTAS_VIEJAS) {
                // return;
            } else {
                val array: Array<String> = ultimaConexion.split("~")
                val año: Int = Integer.parseInt(array[0])
                val mes: Int = Integer.parseInt(array[1])
                val dia: Int = Integer.parseInt(array[2])
                val hora: Int = Integer.parseInt(array[3])
                val minuto: Int = Integer.parseInt(array[4])
                val minutos: Long = Constantes.getTiempoFechaX(año, mes, dia, hora, minuto, 60 * 1000)
                if (Mundo.borrarLasCuentas(minutos)) {
                    Mundo.CUENTAS_A_BORRAR.add(this)
                }
            }
        } catch (e: Exception) {
        }
    }

    fun tieneReporte(tipo: Byte, id: Int): Boolean {
        return try {
            _idsReportes[tipo].contains(id)
        } catch (e: Exception) {
            false
        }
    }

    fun listaReportes(): String {
        val str = StringBuilder()
        for (b in 0..3) {
            if (str.length() > 0) {
                str.append("|")
            }
            val str2 = StringBuilder()
            try {
                for (f in _idsReportes[b.toByte()]) {
                    if (str2.length() > 0) {
                        str2.append(";")
                    }
                    str2.append(f)
                }
            } catch (e: Exception) {
            }
            str.append(str2.toString())
        }
        return str.toString()
    }

    fun addIDReporte(tipo: Byte, id: Int) {
        try {
            if (_idsReportes[tipo] == null) {
                _idsReportes.put(tipo, ArrayList<Integer>())
            }
            if (!_idsReportes[tipo].contains(id)) {
                _idsReportes[tipo].add(id)
            }
        } catch (e: Exception) {
        }
    }

    fun setUltimaConexion() {
        val hoy: Calendar = Calendar.getInstance()
        val año: Int = hoy.get(Calendar.YEAR)
        val dia: Int = hoy.get(Calendar.DAY_OF_MONTH)
        val mes: Int = hoy.get(Calendar.MONTH) + 1
        val hora: Int = hoy.get(Calendar.HOUR_OF_DAY)
        val minutos: Int = hoy.get(Calendar.MINUTE)
        val segundos: Int = hoy.get(Calendar.SECOND)
        ultimaConexion = "$año~$mes~$dia~$hora~$minutos~$segundos"
    }

    fun validaEntregaRegalo() {
        if (MainServidor.HORAS_MINIMO_RECLAMAR_PREMIO > 0) {
            val premios: Array<String> = regalo.split(",")
            val cuentaPremio = LoginPremio(premios)
            val fecha = Date()
            if (cuentaPremio.getFecha() < fecha.getTime()) {
                if (MainServidor.HORAS_MAXIMO_RECLAMAR_PREMIO > 0) {
                    if (fecha.getTime() - cuentaPremio.getFecha() > MainServidor.HORAS_MAXIMO_RECLAMAR_PREMIO) {
                        cuentaPremio.reiniciarEstado()
                    }
                    val tempPremio: LoginPremio = Mundo.LOGINPREMIOS.get(cuentaPremio.toString())
                    if (tempPremio != null) {
                        regalo = tempPremio.getPremio().toString() + ""
                        //setLoginPremio(cuentaPremio.guardarEstado());
                    }
                } else {
                    val tempPremio: LoginPremio = Mundo.LOGINPREMIOS.get(cuentaPremio.toString())
                    if (tempPremio != null) {
                        regalo = tempPremio.getPremio().toString() + ""
                        //setLoginPremio(cuentaPremio.guardarEstado());
                    }
                }
            }
        }
    }

    /*public String getLoginPremio() {
		return GestorSQL.GET_LOGIN_PREMIO(_nombre);
	}*/
    /*public void setLoginPremio(String premio) {
              GestorSQL.SET_LOGIN_PREMIO(_nombre, premio);
          }*/
    var regalo: String?
        get() {
            validaEntregaRegalo()
            return GestorSQL.GET_REGALO(nombre)
        }
        set(regalo) {
            GestorSQL.SET_REGALO(nombre, regalo)
        }

    fun addRegalo(regalo: String?) {
        val r = StringBuilder()
        r.append(regalo)
        if (r.length() > 0) {
            r.append(",")
        }
        r.append(regalo)
        regalo = r.toString()
    }

    fun setIdioma(idioma: String) {
        _idioma = idioma
    }

    fun setPrimeraVez() {
        GestorSQL.SET_PRIMERA_VEZ_CERO(nombre)
    }

    val primeraVez: Byte
        get() = GestorSQL.GET_PRIMERA_VEZ(nombre)
    val contraseña: String
        get() = GestorSQL.GET_CONTRASEÑA_CUENTA(nombre)
    val apodo: String
        get() = GestorSQL.GET_APODO(nombre)
    val pregunta: String
        get() = GestorSQL.GET_PREGUNTA_SECRETA(nombre)
    val respuesta: String
        get() = GestorSQL.GET_RESPUESTA_SECRETA(nombre)
    val admin: Int
        get() = GestorSQL.GET_RANGO(nombre)
    val ultimoSegundosVoto: Long
        get() = GestorSQL.GET_ULTIMO_SEGUNDOS_VOTO(actualIP, iD)
    val nombreReal: String
        get() = GestorSQL.GET_NOMBRE(nombre)
    val apellidoReal: String
        get() = GestorSQL.GET_APELLIDO(nombre)
    val correoReal: String
        get() = GestorSQL.GET_CORREO(nombre)
    val votos: Int
        get() = GestorSQL.GET_VOTOS(nombre)

    fun setRango(rango: Int) {
        GestorSQL.SET_RANGO(nombre, rango)
    }

    fun getuuid(): String {
        return GestorSQL.GET_UUID(nombre)
    }

    fun tiempoRestanteParaVotar(): Int {
        val resta: Long = Constantes.getTiempoActualEscala(1000 * 60) - minutosUltimoVoto
        return if (resta < 0 || resta >= MainServidor.MINUTOS_VALIDAR_VOTO) {
            0
        } else {
            (MainServidor.MINUTOS_VALIDAR_VOTO - resta)
        }
    }

    @get:Synchronized
    private val minutosUltimoVoto: Long
        private get() = ultimoSegundosVoto / 60

    @Synchronized
    fun puedeVotar(): Boolean {
        return if (_ultVotoMilis + 3000 > System.currentTimeMillis()) {
            false
        } else tiempoRestanteParaVotar() == 0
    }

    @Synchronized
    fun darOgrinasPorVoto() {
        val votos = votos
        val ogrinasXVotos: Int = Constantes.getOgrinasPorVotos(votos)
        _ultVotoMilis = System.currentTimeMillis()
        GestorSQL.SET_ULTIMO_SEGUNDOS_VOTO(iD, actualIP, System.currentTimeMillis() / 1000)
        GestorSQL.SET_VOTOS(iD, votos + 1)
        try {
            GestorSQL.ADD_OGRINAS_CUENTA(ogrinasXVotos, iD, socket.getPersonaje())
        } catch (e: Exception) {
            GestorSQL.ADD_OGRINAS_CUENTA(ogrinasXVotos, iD, null)
        }
        GestorSalida.ENVIAR_Im_INFORMACION(_tempPerso, "1THANKS_FOR_VOTE;$ogrinasXVotos")
    }

    fun getIdioma(): String {
        return _idioma
    }

    val banco: Cofre
        get() = _banco
    val kamasBanco: Long
        get() = _banco.getKamas()

    fun addKamasBanco(kamas: Long) {
        if (kamas == 0L) {
            return
        }
        _banco.addKamas(kamas, null)
        // if (_kamas >= MainServidor.LIMITE_DETECTAR_FALLA_KAMAS) {
        // GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_ADMINS(0, "[EMULADOR-ELBUSTEMU]", "La cuenta " + _nombre
        // + " (" + _id
        // + ") posee " + _kamas + " en el banco.");
        // MainServidor.redactarLogServidorln("LA CUENTA " + _nombre + " (" + _id + ") POSSE " +
        // _kamas);
        // if (!ServidorSocket.JUGADORES_REGISTRAR.contains(_nombre)) {
        // ServidorSocket.JUGADORES_REGISTRAR.add(_nombre);
        // }
        // }
    }

    fun estaMuteado(): Boolean {
        return _muteado
    }

    fun mutear(b: Boolean, tiempo: Int) {
        _muteado = b
        if (tiempo == 0) {
            return
        } else {
            GestorSalida.ENVIAR_Im_INFORMACION(_tempPerso, "1124;$tiempo")
        }
        tiempoMuteado = (tiempo * 1000).toLong()
        horaMuteado = System.currentTimeMillis()
    }

    fun stringBancoObjetosBD(): String {
        return _banco.analizarObjetoCofreABD()
    }

    val objetosBanco: Collection<Any>
        get() = _banco.getObjetos()

    fun addObjetoAlBanco(obj: Objeto?) {
        _banco.addObjetoRapido(obj)
    }

    fun setEntradaPersonaje(t: ServidorSocket?) {
        _entradaPersonaje = t
    }

    val socket: ServidorSocket?
        get() = _entradaPersonaje
    val tempPersonaje: Personaje?
        get() = _tempPerso

    fun enLinea(): Boolean {
        return _entradaPersonaje != null || _tempPerso != null
    }

    fun setBaneado(baneado: Boolean, minutos: Int) {
        if (baneado) {
            var tiempoBaneo: Long = -1
            if (minutos > 0) {
                tiempoBaneo = System.currentTimeMillis() + minutos * 60 * 1000
            }
            GestorSQL.SET_BANEADO(nombre, tiempoBaneo)
            if (_entradaPersonaje != null) {
                if (tiempoBaneo <= -1) {
                    GestorSalida.ENVIAR_AlEb_CUENTA_BANEADA_DEFINITIVO(_entradaPersonaje)
                } else if (tiempoBaneo > System.currentTimeMillis()) {
                    GestorSalida.ENVIAR_AlEk_CUENTA_BANEADA_TIEMPO(_entradaPersonaje, tiempoBaneo)
                }
            }
        } else {
            GestorSQL.SET_BANEADO(nombre, 0)
        }
    }

    fun esAbonado(): Boolean {
        return tiempoAbono > 0
    }

    val tiempoAbono: Long
        get() = Math.max(0, GestorSQL.GET_ABONO(nombre) - System.currentTimeMillis())

    fun crearPersonaje(nombre: String?, clase: Byte, sexo: Byte, color1: Int,
                       color2: Int, color3: Int, gfx: Int): Personaje? {
        val perso: Personaje = Personaje.crearPersonaje(nombre, sexo, clase, color1, color2, color3, gfx, this)
                ?: return null
        _personajes.put(perso.getID(), perso)
        return perso
    }

    fun eliminarPersonaje(id: Int) {
        if (!_personajes.containsKey(id)) {
            return
        }
        val perso: Personaje = _personajes[id] ?: return
        Mundo.eliminarPersonaje(perso, true)
        _personajes.remove(id)
    }

    fun addPersonaje(perso: Personaje) {
        if (_personajes.containsKey(perso.getID())) {
            MainServidor.redactarLogServidorln("Se esta intentado volver agregar a la cuenta, al personaje " + perso
                    .getNombre())
            return
        }
        _personajes.put(perso.getID(), perso)
    }

    val personajes: Collection<Any>
        get() = _personajes.values()

    fun getPersonaje(id: Int): Personaje? {
        return _personajes[id]
    }

    fun setTempPerso(perso: Personaje?) {
        _tempPerso = perso
    }

    @Synchronized
    fun desconexion() {
        if (_tempPerso != null) {
            _tempPerso.desconectar(true)
        }
        _entradaPersonaje = null
        if (Mundo.SERVIDOR_ESTADO !== Constantes.SERVIDOR_OFFLINE) {
            _tempPerso = null
            GestorSQL.REPLACE_CUENTA_SERVIDOR(this, GestorSQL.GET_PRIMERA_VEZ(nombre))
            // GestorSQL.UPDATE_CUENTA_LOGUEADO(_id, (byte) 0);
        }
    }

    fun analizarListaAmigosABD(): String {
        val str = StringBuilder()
        for (i in _idsAmigos) {
            if (!str.toString().isEmpty()) {
                str.append(";")
            }
            str.append(i)
        }
        return str.toString()
    }

    fun stringListaEnemigosABD(): String {
        val str = StringBuilder()
        for (i in _idsEnemigos) {
            if (!str.toString().isEmpty()) {
                str.append(";")
            }
            str.append(i)
        }
        return str.toString()
    }

    fun stringListaAmigos(): String {
        val str = StringBuilder("")
        for (i in _idsAmigos) {
            val C: Cuenta = Mundo.getCuenta(i) ?: continue
            str.append("|" + C.apodo)
            if (!C.enLinea()) continue
            val P: Personaje = C.tempPersonaje ?: continue
            str.append(P.analizarListaAmigos(iD))
        }
        return str.toString()
    }

    fun stringListaEnemigos(): String {
        val str = StringBuilder()
        for (i in _idsEnemigos) {
            val cuenta: Cuenta = Mundo.getCuenta(i) ?: continue
            str.append("|" + cuenta.apodo)
            if (!cuenta.enLinea()) {
                continue
            }
            val perso: Personaje = cuenta.tempPersonaje ?: continue
            str.append(perso.analizarListaAmigos(iD))
        }
        return str.toString()
    }

    fun addAmigo(id: Int) {
        if (iD == id) {
            GestorSalida.ENVIAR_FA_AGREGAR_AMIGO(_tempPerso, "Ey")
            return
        }
        if (_idsEnemigos.contains(id)) {
            GestorSalida.ENVIAR_iA_AGREGAR_ENEMIGO(_tempPerso, "Ea")
            return
        }
        if (!_idsAmigos.contains(id)) {
            _idsAmigos.add(id)
            val amigo: Cuenta = Mundo.getCuenta(id)
            if (amigo == null) {
                GestorSalida.ENVIAR_BN_NADA(_tempPerso)
                return
            }
            GestorSalida.ENVIAR_FA_AGREGAR_AMIGO(_tempPerso, "K" + amigo.apodo + amigo.tempPersonaje
                    .analizarListaAmigos(iD))
        } else {
            GestorSalida.ENVIAR_FA_AGREGAR_AMIGO(_tempPerso, "Ea")
        }
    }

    fun addEnemigo(packet: String?, id: Int) {
        if (iD == id) {
            GestorSalida.ENVIAR_iA_AGREGAR_ENEMIGO(_tempPerso, "Ey")
            return
        }
        if (_idsAmigos.contains(id)) {
            GestorSalida.ENVIAR_FA_AGREGAR_AMIGO(_tempPerso, "Ea")
            return
        }
        if (!_idsEnemigos.contains(id)) {
            _idsEnemigos.add(id)
            val enemigo: Cuenta = Mundo.getCuenta(id)
            if (enemigo == null) {
                GestorSalida.ENVIAR_BN_NADA(_tempPerso)
                return
            }
            GestorSalida.ENVIAR_iA_AGREGAR_ENEMIGO(_tempPerso, "K" + enemigo.apodo + enemigo.tempPersonaje
                    .analizarListaEnemigos(iD))
        } else {
            GestorSalida.ENVIAR_iA_AGREGAR_ENEMIGO(_tempPerso, "Ea")
        }
    }

    fun borrarAmigo(id: Int) {
        try {
            _idsAmigos.remove(_idsAmigos.indexOf(id))
            GestorSalida.ENVIAR_FD_BORRAR_AMIGO(_tempPerso, "K")
        } catch (e: Exception) {
        }
    }

    fun borrarEnemigo(id: Int) {
        try {
            _idsEnemigos.remove(_idsEnemigos.indexOf(id))
            GestorSalida.ENVIAR_iD_BORRAR_ENEMIGO(_tempPerso, "K")
        } catch (e: Exception) {
        }
    }

    fun esAmigo(id: Int): Boolean {
        return _idsAmigos.contains(id)
    }

    fun esEnemigo(id: Int): Boolean {
        return _idsEnemigos.contains(id)
    }

    fun stringIDsEstablo(): String {
        val str = StringBuilder()
        for (DP in _establo.values()) {
            GestorSQL.REPLACE_MONTURA(DP, false)
            if (str.length() > 0) {
                str.append(";")
            }
            str.append(DP.getID())
        }
        return str.toString()
    }

    val establo: ConcurrentHashMap<Integer, Montura>
        get() = _establo

    fun addMonturaEstablo(montura: Montura) {
        _establo.put(montura.getID(), montura)
        montura.setUbicacion(Ubicacion.ESTABLO)
    }

    fun borrarMonturaEstablo(id: Int): Boolean {
        return _establo.remove(id) != null
    }
}