package variables.mapa

import java.util.Arrays

// public void destruir() {
// try {
// // for (Celda celda : _celdas.values()) {
// // celda.destruir();
// // }
// // this.finalize();
// } catch (Throwable e) {
// Bustemu.escribirLog("Throwable destruir mapa " + e.toString());
// e.printStackTrace();
// }
// }
class Celda(mapa: Mapa, id: Short, activo: Boolean, movimiento: Byte, level: Byte,
            slope: Byte, lineaDeVista: Boolean, objID: Int) {
    var iD: Short
    private val _mapaID: Int
    var _mapa: Mapa
    var ultimoUsoTrigger: Long = 0
        private set
    private var _personajes: CopyOnWriteArrayList<Personaje>? = null
    private var _trampas: CopyOnWriteArrayList<Trampa>? = null
    private var _glifos: CopyOnWriteArrayList<Glifo>? = null
    private var _luchadores: CopyOnWriteArrayList<Luchador>? = null
    var _acciones: Map<Integer, Accion>? = null
    val activo: Boolean
    private var _esCaminableLevel: Boolean
    private var _lineaDeVista = true
    private var _conGDF = false
    val level: Byte
    var movimiento: Byte
        private set
    val coordX: Byte
    val coordY: Byte
    var estado: Byte
        private set
    private val _movimientoInicial: Byte
    val slope: Byte
    private var _objetoInterac: ObjetoInteractivo? = null
    private var _objetoTirado: Objeto? = null
    fun celdaNornmal() {
        _personajes = CopyOnWriteArrayList<Personaje>()
        _acciones = TreeMap<Integer, Accion>()
    }

    val mapa: Mapa
        get() = _mapa

    fun celdaPelea() {
        _luchadores = CopyOnWriteArrayList()
    }

    val alto: Float
        get() {
            val a: Float = if (slope.toInt() == 1) 0 else 0.5f
            val b = level - 7
            return a + b
        }
    val objetoInteractivo: ObjetoInteractivo?
        get() = _objetoInterac

    // private int getTipoObjInterac() {
    // if (_objetoInterac == null) {
    // return -1;
    // }
    // return _objetoInterac.getTipoObjInteractivo();
    // }
    var objetoTirado: Objeto?
        get() = _objetoTirado
        set(obj) {
            _objetoTirado = obj
        }

    fun aplicarAccion(perso: Personaje?) {
        if (_acciones == null || _acciones!!.isEmpty()) {
            return
        }
        if (MainServidor.SON_DE_LUCIANO) {
            for (accion in _acciones!!.values()) {
                if (Condiciones.validaCondiciones(perso, accion.getCondicion(), null)) {
                    accion.realizarAccion(perso, null, -1, -1.toShort())
                }
            }
        } else {
            for (accion in _acciones!!.values()) {
                if (!Condiciones.validaCondiciones(perso, accion.getCondicion(), null)) {
                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "119|45")
                    return
                }
            }
            var tieneCondicion = false
            for (accion in _acciones!!.values()) {
                if (!accion.getCondicion().isEmpty()) {
                    tieneCondicion = true
                }
                accion.realizarAccion(perso, null, -1, -1.toShort())
            }
            if (tieneCondicion) {
                ultimoUsoTrigger = System.currentTimeMillis()
            }
        }
    }

    fun addAccion(idAccion: Int, args: String?, condicion: String?) {
        if (_acciones == null) {
            return
        }
        val accion = Accion(idAccion, args, condicion)
        _acciones.put(idAccion, accion)
    }

    fun eliminarAcciones() {
        if (_acciones == null) {
            return
        }
        _acciones.clear()
    }

    fun accionesIsEmpty(): Boolean {
        return if (_acciones == null) {
            true
        } else _acciones!!.isEmpty()
    }

    val acciones: Map<Any, Any>?
        get() = _acciones

    fun librerParaMercante(): Boolean {
        if (_personajes == null) {
            return false
        }
        return if (_mapa.mercantesEnCelda(iD) > 0) {
            false
        } else _personajes.size() <= 1
    }

    val primerPersonaje: Personaje?
        get() {
            if (_personajes == null) {
                return null
            }
            return if (_personajes.isEmpty()) {
                null
            } else try {
                _personajes.get(0)
            } catch (e: Exception) {
                primerPersonaje
            }
        }

    fun addPersonaje(perso: Personaje?, aMapa: Boolean) {
        if (_personajes == null) {
            return
        }
        if (!_personajes.contains(perso)) {
            _personajes.add(perso)
        }
        if (aMapa) {
            _mapa.addPersonaje(perso, true)
        }
    }

    fun removerPersonaje(perso: Personaje?, aMapa: Boolean) {
        if (_personajes == null) {
            return
        }
        _personajes.remove(perso)
        if (aMapa) {
            _mapa.removerPersonaje(perso)
        }
    }

    fun esCaminable(pelea: Boolean): Boolean {
        return if (!activo || movimiento.toInt() == 0 || movimiento.toInt() == 1) {
            false
        } else _esCaminableLevel
    }

    val luchadores: CopyOnWriteArrayList<Luchador>?
        get() = _luchadores

    fun lineaDeVista(): Boolean {
        return _lineaDeVista
    }

    // public boolean lineaDeVistaLibre(int idLuch) {
    // if (!_activo) {
    // return false;
    // }
    // if (_luchadores == null || _luchadores.isEmpty() || !_lineaDeVista) {
    // return _lineaDeVista;
    // }
    // for (final Luchador luch : _luchadores) {
    // if (luch.getID() == idLuch) {
    // continue;
    // }
    // if (!luch.esInvisible(0)) {
    // return false;
    // }
    // }
    // return _lineaDeVista;
    // }
    fun tieneSprite(idLuch: Int, suponiendo: Boolean): Boolean {
        if (_luchadores == null || _luchadores.isEmpty()) {
            return false
        }
        try {
            for (luch in _luchadores) {
                if (luch.getID() === idLuch && suponiendo) {
                    continue
                }
                if (!luch.esInvisible(idLuch)) {
                    return true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            System.out.println(e.getMessage())
        }
        return false
    }

    fun moverLuchadoresACelda(celdaNew: Celda) {
        if (_luchadores == null || celdaNew.iD == iD) {
            return
        }
        for (luch in _luchadores) {
            celdaNew.addLuchador(luch)
            luch.setCeldaPelea(celdaNew)
        }
        // limpia al final a los luchadores
        _luchadores.clear()
    }

    fun addLuchador(luchador: Luchador?) {
        if (_luchadores == null) {
            return
        }
        if (!_luchadores.contains(luchador)) {
            _luchadores.add(luchador)
        }
    }

    fun removerLuchador(luchador: Luchador?) {
        if (_luchadores == null) {
            return
        }
        _luchadores.remove(luchador)
    }

    fun limpiarLuchadores() {
        if (_luchadores == null) {
            return
        }
        _luchadores.clear()
    }

    val primerLuchador: Luchador?
        get() {
            if (_luchadores == null) {
                return null
            }
            return if (_luchadores.isEmpty()) {
                null
            } else _luchadores.get(0)
        }

    fun addGlifo(glifo: Glifo?) {
        if (_glifos == null) {
            _glifos = CopyOnWriteArrayList()
        }
        if (!_glifos.contains(glifo)) {
            _glifos.add(glifo)
        }
    }

    fun borrarGlifo(glifo: Glifo?): Boolean {
        return if (_glifos == null) {
            false
        } else _glifos.remove(glifo)
    }

    fun tieneGlifo(): Boolean {
        return if (_glifos == null) {
            false
        } else !_glifos.isEmpty()
    }

    val glifos: CopyOnWriteArrayList<Glifo>?
        get() = _glifos

    fun esGlifo(): Boolean {
        if (_glifos == null) {
            return false
        }
        for (glifo in _glifos) {
            if (glifo.getCelda().getID() === iD) {
                return true
            }
        }
        return false
    }

    @SuppressWarnings("unchecked")
    fun addTrampa(trampa: Trampa?) {
        if (_trampas == null) {
            _trampas = CopyOnWriteArrayList()
        }
        if (!_trampas.contains(trampa)) {
            _trampas.add(trampa)
            @SuppressWarnings("rawtypes") val arrayList: List = Arrays.asList(_trampas.toArray())
            Collections.sort(arrayList)
            _trampas.clear()
            _trampas.addAll(arrayList)
        }
    }

    fun borrarTrampa(trampa: Trampa?): Boolean {
        return if (_trampas == null) {
            false
        } else _trampas.remove(trampa)
    }

    fun tieneTrampa(): Boolean {
        return if (_trampas == null) {
            false
        } else !_trampas.isEmpty()
    }

    val trampas: CopyOnWriteArrayList<Trampa>?
        get() = _trampas

    fun esTrampa(): Boolean {
        if (_trampas == null) {
            return false
        }
        for (trampa in _trampas) {
            if (trampa.getCelda().getID() === iD) {
                return true
            }
        }
        return false
    }

    @Synchronized
    fun activarCelda(conGDF: Boolean, milisegundos: Long) {
        if (estado != Constantes.CI_ESTADO_LLENO) {
            return
        }
        val t = Thread(object : Runnable() {
            @Override
            fun run() {
                movimiento = 4 // caminable
                _conGDF = conGDF
                estado = Constantes.CI_ESTADO_VACIANDO
                val permisos = BooleanArray(16)
                val valores = IntArray(16)
                permisos[11] = true
                valores[11] = movimiento
                GestorSalida.ENVIAR_GDC_ACTUALIZAR_CELDA_MAPA(_mapa, iD, Encriptador.stringParaGDC(permisos, valores),
                        false)
                if (_conGDF) {
                    GestorSalida.ENVIAR_GDF_ESTADO_OBJETO_INTERACTIVO(_mapa, _mapa.getCelda(iD))
                    try {
                        Thread.sleep(2000)
                    } catch (e: Exception) {
                    }
                }
                if (milisegundos > 0) {
                    estado = Constantes.CI_ESTADO_VACIO
                    try {
                        Thread.sleep(milisegundos) // hace de timer;
                    } catch (e: Exception) {
                    }
                    movimiento = _movimientoInicial
                    estado = Constantes.CI_ESTADO_LLENANDO
                    if (_conGDF) {
                        GestorSalida.ENVIAR_GDF_ESTADO_OBJETO_INTERACTIVO(_mapa, _mapa.getCelda(iD))
                        try {
                            Thread.sleep(2000)
                        } catch (e: Exception) {
                        }
                    }
                    estado = Constantes.CI_ESTADO_LLENO
                    valores[11] = movimiento
                    GestorSalida.ENVIAR_GDC_ACTUALIZAR_CELDA_MAPA(_mapa, iD, Encriptador.stringParaGDC(permisos, valores),
                            false)
                } else {
                    movimiento = _movimientoInicial
                    estado = Constantes.CI_ESTADO_LLENO
                    if (_conGDF) {
                        GestorSalida.ENVIAR_GDF_ESTADO_OBJETO_INTERACTIVO(_mapa, _mapa.getCelda(iD))
                    }
                }
            }
        })
        t.setDaemon(true)
        t.start()
    }

    fun puedeHacerAccion(skillID: Int, pescarKuakua: Boolean): Boolean {
        if (_objetoInterac == null) {
            return false
        }
        if (_objetoInterac.getObjIntModelo().tieneSkill(skillID)) {
            return if (skillID == Constantes.SKILL_PESCAR_KUAKUA) {
                pescarKuakua
            } else if (_objetoInterac.getObjIntModelo().getTipo() === 1) {
                // trigo, cereal, flores
                _objetoInterac.getEstado() === Constantes.OI_ESTADO_LLENO
            } else {
                true
            }
        }
        MainServidor.redactarLogServidorln("Bug al verificar si se puede realizar el skill ID = $skillID")
        return false
    }

    fun iniciarAccion(perso: Personaje, GA: AccionDeJuego?) {
        //System.out.println(3);
        var accionID = -1
        var celdaID: Short = -1
        try {
            accionID = Integer.parseInt(GA._packet.split(";").get(1))
            celdaID = Short.parseShort(GA._packet.split(";").get(0))
        } catch (e: Exception) {
        }
        if (accionID == -1) return
        if (Constantes.esTrabajo(accionID)) {
            perso.puedeIniciarTrabajo(accionID, _objetoInterac, GA, this)
            return
        }
        when (accionID) {
            62 -> {
                if (perso.getPDV() < perso.getPDVMax() && perso.getNivel() < 16) {
                    perso.fullPDV()
                    GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso)
                } else {
                    GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Acción imposible, tienes la vida entera o un nivel superior a 15!", "ff0000")
                    return
                }
                if (GA != null) perso.borrarGA(GA)
            }
            44 -> {
                //String str = _mapaID + "," + _celdaID;
                //perso.setPuntoSalvada(str);
                GestorSalida.ENVIAR_Im_INFORMACION(perso, "06")
                if (GA != null) perso.borrarGA(GA)
            }
            102 -> {
                if (_objetoInterac == null) return
                if (!_objetoInterac.esInteractivo() || _objetoInterac.getEstado() !== Constantes.OI_ESTADO_LLENO) return
                perso.setOcupado(true)
                _objetoInterac.setEstado(Constantes.OI_ESTADO_ESPERA)
                _objetoInterac.setInteractivo(false)
                GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA(perso.getMapa(), GA._idUnica, 501, perso.getID().toString() + "", iD.toString() + "," + _objetoInterac.getDuracion() + "," + _objetoInterac.getAnimacionPJ())
                GestorSalida.ENVIAR_GDF_ESTADO_OBJETO_INTERACTIVO(perso.getMapa(), this)
                if (GA != null) perso.borrarGA(GA)
            }
            114 -> {
                perso.abrirMenuZaap()
                if (GA != null) perso.borrarGA(GA)
            }
            152 -> {
                if (!_objetoInterac.esInteractivo() || _objetoInterac.getEstado() !== Constantes.OI_ESTADO_LLENO) return
                perso.setPescarKuakua(false)
                _objetoInterac.setEstado(Constantes.OI_ESTADO_ESPERA)
                _objetoInterac.setInteractivo(false)
                GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA(perso.getMapa(), GA._idUnica, 501, perso.getID().toString() + "", iD.toString() + "," + _objetoInterac.getDuracion() + "," + _objetoInterac.getAnimacionPJ())
                GestorSalida.ENVIAR_GDF_ESTADO_OBJETO_INTERACTIVO(perso.getMapa(), this)
                if (GA != null) perso.borrarGA(GA)
            }
            157 -> {
                perso.abrirMenuZaapi()
                if (GA != null) perso.borrarGA(GA)
            }
            175 -> {
                perso.abrirCercado()
                if (GA != null) perso.borrarGA(GA)
            }
            176 -> {
                val cercado: Cercado = perso.getMapa().getCercado()
                if (cercado.getDueñoID() === -1) { // Publico
                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "196")
                    return
                }
                if (cercado.getPrecio() === 0) { // no en venta
                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "197")
                    return
                }
                if (perso.getGremio() == null) { // para el gremio
                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "1135")
                    return
                }
                if (perso.getMiembroGremio().getRango() !== 1) { // no miembros
                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "198")
                    return
                }
                GestorSalida.ENVIAR_RD_COMPRAR_CERCADO(perso, cercado.getPrecio().toString() + "|" + cercado.getPrecio())
                if (GA != null) perso.borrarGA(GA)
            }
            177, 178 -> {
                val cercado1: Cercado = perso.getMapa().getCercado()
                if (cercado1.getDueñoID() === -1) {
                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "194")
                    return
                }
                if (cercado1.getDueñoID() !== perso.getID()) {
                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "195")
                    return
                }
                GestorSalida.ENVIAR_RD_COMPRAR_CERCADO(perso, cercado1.getPrecio().toString() + "|" + cercado1.getPrecio())
                if (GA != null) perso.borrarGA(GA)
            }
            183 -> {
                /*if (perso.getNivel() > 15) {
					GestorSalida.ENVIAR_Im_INFORMACION(perso, "1127");
					perso.getCuenta().getEntradaPersonaje().borrarGA(GA);
					return;
				}*/
                val mapa: Array<String> = Constantes.getMapaInicioIncarnam(perso.getClaseID(true)).split(",")
                val celdaId: Short = 314
                perso.teleport(Short.parseShort(mapa[0]), celdaId)
                if (GA != null) perso.borrarGA(GA)
            }
            81 -> {
                val casa1: Casa = Mundo.getCasaPorUbicacion(perso.getMapa().getID(), celdaID) ?: return
                perso.setCasa(casa1)
                casa1.ponerClave(perso, true)
                if (GA != null) perso.borrarGA(GA)
            }
            84 -> {
                val casa2: Casa = Mundo.getCasaPorUbicacion(perso.getMapa().getID(), celdaID) ?: return
                perso.setCasa(casa2)
                casa2.intentarAcceder(perso, "")
                if (GA != null) perso.borrarGA(GA)
            }
            97 -> {
                val casa3: Casa = Mundo.getCasaPorUbicacion(perso.getMapa().getID(), celdaID) ?: return
                perso.setCasa(casa3)
                casa3.abrirVentanaCompraVentaCasa(perso)
                if (GA != null) perso.borrarGA(GA)
            }
            104 -> {
                if (_mapaID == 7442) {
                    GestorSalida.ENVIAR_BN_NADA(perso)
                    break
                }
                var cofre2: Cofre = Mundo.getCofrePorUbicacion(_mapaID, celdaID)
                if (cofre2 == null) {
                    cofre2 = Cofre.insertarCofre(_mapaID, iD)
                }
                if (cofre2 == null) {
                    GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso,
                            "Este cofre no sirve, debes reportarlo.")
                    break
                }
                cofre2.intentarAcceder(perso, "")
                if (GA != null) perso.borrarGA(GA)
            }
            105 -> {
                if (_mapaID == 7442) {
                    GestorSalida.ENVIAR_BN_NADA(perso)
                    break
                }
                var cofre: Cofre = Mundo.getCofrePorUbicacion(_mapaID, celdaID)
                if (cofre == null) {
                    cofre = Cofre.insertarCofre(_mapaID, iD)
                }
                if (cofre == null) {
                    break
                }
                cofre.ponerClave(perso, true)
                if (GA != null) perso.borrarGA(GA)
            }
            153 -> {
                val basura: Cofre = Mundo.getCofrePorUbicacion(0.toShort(), 0.toShort()) ?: break
                basura.intentarAcceder(perso, "")
                if (GA != null) perso.borrarGA(GA)
            }
            98, 108 -> {
                val casa4: Casa = Mundo.getCasaPorUbicacion(perso.getMapa().getID(), celdaID) ?: break
                casa4.abrirVentanaCompraVentaCasa(perso)
                if (GA != null) perso.borrarGA(GA)
            }
            170 -> {
                GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(perso, Constantes.INTERCAMBIO_TIPO_LIBRO_ARTESANOS,
                        Mundo.getLibroArtesano(perso.getMapa().getID()))
                perso.setTipoExchange(Constantes.INTERCAMBIO_TIPO_LIBRO_ARTESANOS)
                if (GA != null) perso.borrarGA(GA)
            }
            181, 121 -> {
                GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Deshabilitado temporalmente!!!", Constantes.COLOR_ROJO)
                /*
			if (!perso.puedeAbrir) {
				GestorSalida.ENVIAR_BN_NADA(perso.getCuenta().getEntradaPersonaje());
				return;
			}
			if (!perso.getRompiendo())
				GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(perso, 3, "2;181");
			perso.setRompiendo(true);
			if (perso.continua) {
				GestorSalida.enviar(perso, "cMK|-99|Zino|Ahora combina el Casco y el Hierro|");
			}*/if (GA != null) perso.borrarGA(GA)
            }
            Constantes.SKILL_ACCIONAR_PALANCA -> {
                perso.realizarOtroInteractivo(this, _objetoInterac)
                if (GA != null) perso.borrarGA(GA)
            }
            150 -> {
                _esCaminableLevel = false
                _objetoInterac.setEstado(Constantes.OI_ESTADO_ESPERA)
                _objetoInterac.setInteractivo(false)
                GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA(perso.getMapa(), GA._idUnica, 501, perso.getID().toString() + "", iD.toString() + "," + _objetoInterac.getDuracion() + "," + _objetoInterac.getAnimacionPJ())
                GestorSalida.ENVIAR_GDF_FORZADO_PERSONAJE(perso, "$celdaID;3;0")
                if (GA != null) perso.borrarGA(GA)
            }
            else -> {
                System.out.println("Bug al iniciar la accion ID = $accionID")
                if (GA != null) perso.borrarGA(GA)
            }
        }
    }

    /*public boolean puedeIniciarAccion(final Personaje perso, final AccionDeJuego AJ) {
        try {
            if (perso.getPelea() != null) {
                return false;
            }
            if (AJ == null) {
                return false;
            }
            short celdaID = -1;
            int skillID = -1;
            try {
                celdaID = Short.parseShort(AJ.getPathPacket().split(";")[0]);
                skillID = Integer.parseInt(AJ.getPathPacket().split(";")[1]);
            } catch (final Exception e) {
                return false;
            }
            if (Constantes.esTrabajo(skillID)) {
                boolean resultado = perso.puedeIniciarTrabajo(skillID, _objetoInterac, AJ.getIDUnica(), this);
                return resultado;
            } else {
                final Casa casa1;
                switch (skillID) {
                    case Constantes.SKILL_PELAR_PATATAS :
                        break;
                    case Constantes.SKILL_GUARDAR_POSICION :// zaap , punto salvada
                        perso.setPuntoSalvada(_mapaID + "," + _celdaID);
                        GestorSalida.ENVIAR_Im_INFORMACION(perso, "06");
                        break;
                    case Constantes.SKILL_REGENERARSE :// fuente de rejuvenecimiento
                        perso.fullPDV();
                        GestorSalida.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(perso);
                        break;
                    case Constantes.SKILL_UTILIZAR_ZAAP :// zaap
                        perso.abrirMenuZaap();
                        break;
                    case 152 :// kua kua
                        perso.setPescarKuakua(false);
                    case Constantes.SKILL_SACAR_AGUA :// pozo de agua
                    case Constantes.SKILL_JUGAR_MAQUINA_FUERZA :// jugar maquina de fuerza (feria trool)
                        if (!_objetoInterac.puedeIniciarRecolecta()) {
                        break;
                        }
                        _objetoInterac.iniciarRecolecta(_objetoInterac.getDuracion());
                        GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA(perso.getMapa(), AJ.getIDUnica(), 501, perso.getID() + "",
                        _celdaID + "," + _objetoInterac.getDuracion() + "," + _objetoInterac.getAnimacionPJ());
                        return true;
                    case 157 :// zaapi
                        perso.abrirMenuZaapi();
                        break;
                    case 175 :// cercado
                        perso.abrirCercado();
                        break;
                    case 176 :// comprar cercado
                        final Cercado cercado = perso.getMapa().getCercado();
                        if (cercado.esPublico()) {
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "196");
                            break;
                        }
                        if (cercado.getPrecio() <= 0) {
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "197");
                            break;
                        }
                        if (perso.getGremio() == null) {
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "1135");
                            break;
                        }
                        // if (perso.getMiembroGremio().getRango() != 1) {
                        // GestorSalida.ENVIAR_Im_INFORMACION(perso, "198");
                        // break;
                        // }
                        GestorSalida.ENVIAR_RD_COMPRAR_CERCADO(perso, cercado.getPrecio() + "|" + cercado.getPrecio());
                        break;
                    case 177 :// comprar cercado
                    case 178 :
                        final Cercado cercado1 = perso.getMapa().getCercado();
                        if (cercado1.esPublico()) {
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "194");
                            break;
                        }
                        if (cercado1.getDueñoID() != perso.getID()) {
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "195");
                            break;
                        }
                        GestorSalida.ENVIAR_RD_COMPRAR_CERCADO(perso, cercado1.getPrecio() + "|" + cercado1.getPrecio());
                        break;
                    case Constantes.SKILL_ACCIONAR_PALANCA : // palanca 179
                        perso.realizarOtroInteractivo(this, _objetoInterac);
                        // System.out.println("ENVIO LA ACCION 2");
                        break;
                    case 183 :// estatua ir a incarnam
                        if (perso.getNivel() > 15) {
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "1127");
                        } else {
                            GestorSalida.ENVIAR_GA2_CINEMATIC(perso, "5");
                            final String[] mapa = Constantes.getMapaInicioIncarnam(perso.getClaseID(true)).split(",");
                            perso.teleport(Short.parseShort(mapa[0]), Short.parseShort(mapa[1]));
                        }
                        break;
                    case 81 :// poner el cerrojo
                        casa1 = Mundo.getCasaPorUbicacion(_mapaID, celdaID);
                        if (casa1 == null) {
                            break;
                        }
                        casa1.ponerClave(perso, true);
                        break;
                    case 100 :// quitar el cerrojo
                        casa1 = Mundo.getCasaPorUbicacion(_mapaID, celdaID);
                        if (casa1 == null) {
                            break;
                        }
                        // perso.setConsultarCasa(casa1);
                        casa1.quitarCerrojo(perso);
                        break;
                    case 84 :// entrar
                        casa1 = Mundo.getCasaPorUbicacion(_mapaID, celdaID);
                        if (casa1 == null) {
                            break;
                        }
                        casa1.intentarAcceder(perso, "");
                        break;
                    case 97 :// comprar
                    case 98 :// vender
                    case 108 :// modificar el precio de venta
                        casa1 = Mundo.getCasaPorUbicacion(_mapaID, celdaID);
                        if (casa1 == null) {
                            break;
                        }
                        casa1.abrirVentanaCompraVentaCasa(perso);
                        break;
                    case 104 :// abrir cofre
                        if (_mapaID == 7442) {
                            GestorSalida.ENVIAR_BN_NADA(perso);
                            break;
                        }
                        Cofre cofre2 = Mundo.getCofrePorUbicacion(_mapaID, celdaID);
                        if (cofre2 == null) {
                            cofre2 = Cofre.insertarCofre(_mapaID, _celdaID);
                        }
                        if (cofre2 == null) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso,
                                    "Este cofre no sirve, debes reportarlo.");
                            break;
                        }
                        cofre2.intentarAcceder(perso, "");
                        break;
                    case 105 :// poner el cerrojo cofre
                        if (_mapaID == 7442) {
                            GestorSalida.ENVIAR_BN_NADA(perso);
                            break;
                        }
                        Cofre cofre = Mundo.getCofrePorUbicacion(_mapaID, celdaID);
                        if (cofre == null) {
                            cofre = Cofre.insertarCofre(_mapaID, _celdaID);
                        }
                        if (cofre == null) {
                            break;
                        }
                        cofre.ponerClave(perso, true);
                        break;
                    case 153 :// basura
                        final Cofre basura = Mundo.getCofrePorUbicacion((short) 0, (short) 0);
                        if (basura == null) {
                            break;
                        }
                        basura.intentarAcceder(perso, "");
                        break;
                    case 170 :// libro artesanos
                        GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(perso, Constantes.INTERCAMBIO_TIPO_LIBRO_ARTESANOS,
                        Mundo.getLibroArtesano(perso.getMapa().getID()));
                        perso.setTipoExchange(Constantes.INTERCAMBIO_TIPO_LIBRO_ARTESANOS);
                        break;
                    case 181 :// romper objetos
                    case 121 :// machacar recursos
                        if (!perso.estaDisponible(false, true)) {
                            GestorSalida.ENVIAR_BN_NADA(perso);
                            break;
                        }
                        break;
                    default :
                        MainServidor.redactarLogServidorln("Bug al iniciar la skill ID = " + skillID);
                        break;
                }
            }
        } catch (final Exception e) {
            String error = "EXCEPTION iniciarAccion AJ.getPacket(): " + AJ.getPathPacket() + " e: " + e.toString();
            GestorSalida.ENVIAR_BN_NADA(perso, error);
            MainServidor.redactarLogServidorln(error);
        }
        return false;
    }*/
    fun finalizarAccion(perso: Personaje, AJ: AccionDeJuego?): Boolean {
        try {
            if (AJ == null) {
                return false
            }
            var accionID = -1
            accionID = try {
                Integer.parseInt(AJ.getPathPacket().split(";").get(1))
            } catch (e: Exception) {
                return false
            }
            if (Constantes.esTrabajo(accionID)) { // es de oficio
                if (AJ != null) perso.borrarGA(AJ)
                return perso.finalizarTrabajo(accionID)
            } else {
                when (accionID) {
                    Constantes.SKILL_PELAR_PATATAS, Constantes.SKILL_GUARDAR_POSICION, 81, 84, 97, 98, 104, 105, 108, 114, Constantes.SKILL_MACHACAR_RECURSOS, 157, 170, 175, 176, 177, 178, 181, 183, 153, Constantes.SKILL_ACCIONAR_PALANCA -> return true
                    Constantes.SKILL_JUGAR_MAQUINA_FUERZA, Constantes.SKILL_SACAR_AGUA, Constantes.SKILL_PESCAR_KUAKUA -> {
                        if (!_objetoInterac.puedeFinalizarRecolecta()) {
                            return false
                        }
                        _objetoInterac.activandoRecarga(Constantes.OI_ESTADO_VACIANDO, Constantes.OI_ESTADO_VACIO)
                        when (accionID) {
                            Constantes.SKILL_SACAR_AGUA -> {
                                val cantidad: Int = Formulas.getRandomInt(1, 10)
                                perso.addObjIdentAInventario(Mundo.getObjetoModelo(311).crearObjeto(cantidad,
                                        Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM), false)
                                GestorSalida.ENVIAR_IQ_NUMERO_ARRIBA_PJ(perso, perso.getID(), cantidad)
                                GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso)
                            }
                            Constantes.SKILL_PESCAR_KUAKUA -> {
                                val x: Int = Formulas.getRandomInt(0, 5)
                                if (x == 5) {
                                    GestorSalida.ENVIAR_cS_EMOTICON_MAPA(perso.getMapa(), perso.getID(), 11)
                                    perso.addObjIdentAInventario(Mundo.getObjetoModelo(6659).crearObjeto(1,
                                            Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM), false)
                                    GestorSalida.ENVIAR_IQ_NUMERO_ARRIBA_PJ(perso, perso.getID(), 1)
                                } else {
                                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "1TRY_OTHER")
                                    GestorSalida.ENVIAR_cS_EMOTICON_MAPA(perso.getMapa(), perso.getID(), 12)
                                }
                                GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso)
                            }
                            Constantes.SKILL_JUGAR_MAQUINA_FUERZA -> {
                            }
                        }
                    }
                    else -> MainServidor.redactarLogServidorln("Bug al finalizar la accion ID = $accionID")
                }
                if (AJ != null) perso.borrarGA(AJ)
            }
        } catch (e: Exception) {
        }
        return true
    } // public void destruir() {

    // try {
    // this.finalize();
    // } catch (Throwable e) {
    // Bustemu.escribirLog("Throwable destruir celda " + e.toString());
    // e.printStackTrace();
    // }
    // }
    init {
        _mapa = mapa
        _mapaID = mapa.getID()
        iD = id
        this.activo = activo
        this.level = level
        this.movimiento = movimiento
        _lineaDeVista = lineaDeVista
        estado = Constantes.CI_ESTADO_LLENO
        _movimientoInicial = this.movimiento
        this.slope = slope
        val ancho: Byte = mapa.getAncho()
        val _loc5 = Math.floor(iD / (ancho * 2 - 1)) as Int
        val _loc6 = iD - _loc5 * (ancho * 2 - 1)
        val _loc7 = _loc6 % ancho
        coordY = (_loc5 - _loc7).toByte()
        // es en plano inclinado, solo Y es negativo partiendo del 0 arriba negativo, abajo positivo
        coordX = ((iD - (ancho - 1) * coordY) / ancho).toByte()
        if (objID == -1) {
            _objetoInterac = null
        } else {
            _objetoInterac = ObjetoInteractivo(mapa, this, objID)
            Mundo.addObjInteractivo(_objetoInterac)
        }
        val tempD = ((coordX + coordY - 1) * 13.5f).toInt()
        val tempL = (this.level - 7) * 20
        _esCaminableLevel = tempD - tempL >= 0
    }
}