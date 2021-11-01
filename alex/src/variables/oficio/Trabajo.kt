package variables.oficio

import java.util.ArrayList

class Trabajo(// private static float TOLERANCIA_NORMAL = 1.0f, TOLERANCIA_VIP = 1.8f;
        val trabajoID: Int, min: Int, max: Int, esCraft: Boolean, nSuerteTiempo: Int,
        xpGanada: Int, oficio: StatOficio?) : Runnable, Exchanger {
    var casillasMin = 1
    var casillasMax = 1
    var suerte = 100
    var tiempo = 0
    private var _xpGanadaRecoleccion = 0
    private var _cuantasRepeticiones = 0
    private var _esCraft = false
    private val _esForjaMagia: Boolean
    private var _varios = false
    private var _interrumpir: Byte = 0
    var _ingredientes: Map<Integer, Long?>? = null
    var _ultimosIngredientes: Map<Integer, Long>? = null
    private var _artesano: Personaje? = null
    private var _cliente: Personaje? = null
    private var _celda: Celda? = null
    private val _statOficio: StatOficio?
    private var _finThread = true

    // taller
    var kamasPaga: Long = 0
        private set
    var kamasSiSeConsigue: Long = 0
        private set
    private var _ok1 = false
    private var _ok2 = false
    private var _objArtesano: ArrayList<Duo<Integer?, Long?>?>? = null
    private var _objCliente: ArrayList<Duo<Integer?, Long?>?>? = null
    private var _objetosPago: ArrayList<Duo<Integer, Long>>? = null
    private var _objetosSiSeConsegui: ArrayList<Duo<Integer, Long>>? = null
    @Synchronized
    fun recogerRecolecta() {
        if (_celda.getObjetoInteractivo() == null) {
            return
        }
        _artesano.setIsOnAction(false)
        val estrellas: Int = _celda.getObjetoInteractivo().getBonusEstrellas()
        val especial = Formulas.getRandomInt(0, 100 - MainServidor.PROBABILIDAD_RECURSO_ESPECIAL) === 0
        var cantidad = if (casillasMax > casillasMin) Formulas.getRandomInt(casillasMin, casillasMax) else casillasMin
        if (especial) {
            cantidad = 1
        }
        var cantidadTotal = cantidad
        if (_artesano.alasActivadas() && _artesano.getMapa().getSubArea().getAlineacion() === _artesano.getAlineacion()) {
            val balance: Float = Mundo.getBalanceMundo(_artesano)
            val bonusExp: Float = Mundo.getBonusAlinExp(_artesano)
            cantidadTotal += (cantidad * balance * bonusExp / 100).toInt()
        }
        cantidadTotal += cantidad * estrellas / 100
        if (cantidadTotal > 0) {
            val OM: ObjetoModelo = Mundo.getObjetoModelo(Constantes.getObjetoPorRecurso(trabajoID, especial))
            if (OM != null) {
                _artesano.addObjIdentAInventario(OM.crearObjeto(cantidadTotal, Constantes.OBJETO_POS_NO_EQUIPADO,
                        CAPACIDAD_STATS.RANDOM), false)
                _artesano.confirmarLogrosRecolecta(OM.getID().toString() + "," + cantidadTotal)
                GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_artesano)
            } else {
                MainServidor.redactarLogServidorln("El idTrabajoMod " + trabajoID + " no tiene objeto para recolectar: " + OM)
            }
            GestorSalida.ENVIAR_IQ_NUMERO_ARRIBA_PJ(_artesano, _artesano.getID(), cantidadTotal)
        }
    }

    fun iniciarAccionTrabajo(perso: Personaje, OI: ObjetoInteractivo?, AJ: AccionDeJuego, celda: Celda) {
        if (perso.isOnAction()) {
            GestorSalida.ENVIAR_GA_ACCION_DE_JUEGO(perso, 0, "", "")
            GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Debe acabar primero la acción actual!", Constantes.COLOR_ROJO)
            perso.borrarGA(AJ)
            return
        }
        if (OI != null) {
            if (OI.getEstado() === Constantes.OI_ESTADO_VACIO) {
                GestorSalida.ENVIAR_GA_ACCION_DE_JUEGO(perso, 0, "", "")
                GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "No puedes realizar esta acción", Constantes.COLOR_ROJO)
                perso.borrarGA(AJ)
                return
            }
        }
        _celda = celda
        _artesano = perso
        if (_esCraft) {
            perso.setOcupado(true)
            perso.setHaciendoTrabajo(this)
            GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(_artesano, 3, casillasMax.toString() + ";" + trabajoID)
            GestorSalida.ENVIAR_GDF_FORZADO_PERSONAJE(_artesano, _celda.getID().toString() + ";" + 2 + ";" + 1)
            _artesano.setTipoExchange(Constantes.INTERCAMBIO_TIPO_TALLER)
            _artesano.setExchanger(this)
            perso.celdadeparo = celda.getID()
            return
        } else {
            perso.setIsOnAction(true)
            if (OI != null) {
                OI.setInteractivo(false)
                OI.setEstado(Constantes.OI_ESTADO_ESPERA)
            }
            GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA(_artesano.getMapa(), AJ.getIDUnica(), 501, _artesano.getID().toString() + "", _celda
                    .getID().toString() + "," + tiempo)
            GestorSalida.ENVIAR_GDF_ESTADO_OBJETO_INTERACTIVO_TODOS(perso, celda, perso.getMapa())
        }
    }

    /*synchronized boolean iniciarTrabajo(final Personaje perso, final int idUnica, final Celda celda) {
		if (perso.isOnAction()) {
			GestorSalida.ENVIAR_GA_ACCION_DE_JUEGO(perso, 0, "", "");
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Debe acabar primero la acción actual!", Constantes.COLOR_ROJO);	
			return false;
		}
		_artesano = perso;
		_celda = celda;
		if (_esCraft) {
			//System.out.println(8);
			GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(_artesano, 3, _casillasMax + ";" + _skillID);
			GestorSalida.ENVIAR_GDF_FORZADO_PERSONAJE(_artesano, _celda.getID() + ";" + 2 + ";" + 1);
			_artesano.setTipoExchange(Constantes.INTERCAMBIO_TIPO_TALLER);
			_artesano.setExchanger(this);
			return false;
		}
		//System.out.println(9);
		if(_celda.getObjetoInteractivo() == null || !_celda.getObjetoInteractivo().puedeIniciarRecolecta()) {
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso, "No puedes recolectar este objeto en estos momentos");
			return false;
	    }else {
	    	
	    	//System.out.println(10);
	    	perso.setIsOnAction(true);
			if (_celda.getObjetoInteractivo() != null && _celda.getObjetoInteractivo().puedeIniciarRecolecta()) {
				_celda.getObjetoInteractivo().iniciarRecolecta(_tiempoRecoleccion);
				GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA(_artesano.getMapa(), idUnica, 501, _artesano.getID() + "", _celda
				.getID() + "," + _tiempoRecoleccion);
				iniciarThread();
			}
			return true;
		}
	}*/
    fun puedeFinalizarRecolecta(): Boolean {
        //System.out.println("f5");
        return if (_celda.getObjetoInteractivo() == null) {
            false
        } else _celda.getObjetoInteractivo().puedeFinalizarRecolecta()
    }

    @get:Synchronized
    val expFinalizarRecoleccion: Int
        get() {
            if (_celda.getObjetoInteractivo() == null) {
                return 0
            }
            val coefEstrellas: Float = _celda.getObjetoInteractivo().getBonusEstrellas() / 100f
            _celda.getObjetoInteractivo().activandoRecarga(Constantes.OI_ESTADO_VACIANDO, Constantes.OI_ESTADO_VACIO)
            return (preExp(_xpGanadaRecoleccion) + _xpGanadaRecoleccion * coefEstrellas).toInt()
        }

    private fun preExp(exp: Int): Int {
        var exp = exp
        exp *= MainServidor.RATE_XP_OFICIO
        var finalExp = exp
        if (_artesano != null) {
            if (MainServidor.RATE_XP_OFICIO_ABONADOS > 1) {
                if (_artesano.esAbonado()) {
                    finalExp *= MainServidor.RATE_XP_OFICIO_ABONADOS
                }
            }
        }
        return finalExp
    }

    val celda: Celda?
        get() = _celda

    fun esCraft(): Boolean {
        return _esCraft
    }

    fun esTaller(): Boolean {
        return _cliente != null
    }

    fun esFM(): Boolean {
        return _esForjaMagia
    }

    fun mostrarProbabilidades(perso: Personaje) {
        val precio: Int = MainServidor.KAMAS_MOSTRAR_PROBABILIDAD_FORJA
        if (precio <= 0) {
            GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "<b>NO SE PUEDE USAR ESTA ACCION</b>", Constantes.COLOR_ROJO)
            return
        }
        if (perso.getKamas() < precio) {
            GestorSalida.ENVIAR_Im_INFORMACION(perso, "1128;$precio")
            return
        }
        perso.addKamas(-precio, true, true)
        var objAMaguear: Objeto? = null
        var objRunaOPocima: Objeto? = null
        var statMagueo = -1
        var valorRuna = 0
        var pesoPlusRuna = 0
        for (idIngrediente in _ingredientes.keySet()) {
            val ing: Objeto = _artesano.getObjeto(idIngrediente)
            if (ing == null) {
                GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "<b>HAY UN INGREDIENTE NULO</b>", Constantes.COLOR_ROJO)
                return
            }
            val idModelo: Int = ing.getObjModeloID()
            val statRuna: Int = Constantes.getStatPorRunaPocima(ing)
            if (statRuna > 0) {
                statMagueo = statRuna
                valorRuna = Constantes.getValorPorRunaPocima(ing)
                pesoPlusRuna = Constantes.getPotenciaPlusRuna(ing)
                objRunaOPocima = ing
            } else if (idModelo == 7508) {
                // runa de firma
            } else {
                val tipo: Int = ing.getObjModelo().getTipo()
                if (tipo >= 1 && tipo <= 11 || tipo >= 16 && tipo <= 22 || tipo == 81 || tipo == 102 || tipo == 114 || ing
                                .getObjModelo().getCostePA() > 0) {
                    objAMaguear = ing
                }
            }
        }
        if (_statOficio == null) {
            GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "<b>EL STATOFICIO ES NULO</b>", Constantes.COLOR_ROJO)
            return
        }
        if (objAMaguear == null) {
            GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "<b>EL OBJETO A MAGUEAR ES NULO</b>", Constantes.COLOR_ROJO)
            return
        }
        if (objRunaOPocima == null) {
            GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "<b>LA RUNA O POCIMA ES NULO</b>", Constantes.COLOR_ROJO)
            return
        }
        var pesoRuna: Float = Constantes.getPesoStat(statMagueo)
        when (statMagueo) {
            96, 97, 98, 99 -> pesoRuna = 1f
        }
        if (pesoRuna <= 0) {
            GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "<b>RUNA FORJAMAGIA INCORRECTA</b>", Constantes.COLOR_ROJO)
            return
        }
        var ExitoCritico = 0
        var ExitoNormal = 0
        var FallaNormal = 0
        var FalloCritico = 0
        var resultados = IntArray(4)
        resultados = getProbabilidadesMagueo(objAMaguear, objRunaOPocima, statMagueo, valorRuna, pesoPlusRuna)
        ExitoCritico = resultados[0]
        ExitoNormal = resultados[1]
        FallaNormal = resultados[2]
        FalloCritico = resultados[3]
        if (perso.getCuenta().getIdioma().equalsIgnoreCase("fr")) {
            GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "La probabilité de FM ton item <b>" + objAMaguear.getObjModelo()
                    .getNombre().toString() + "</b>:", Constantes.COLOR_NEGRO)
            GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "<b>[Succès critique] = $ExitoCritico%</b>",
                    Constantes.COLOR_AZUL)
            GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "<b>[Succès] = $ExitoNormal%</b>",
                    Constantes.COLOR_VERDE_OSCURO)
            GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "<b>[Echec] = $FallaNormal%</b>", Constantes.COLOR_NARANJA)
            GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "<b>[Echec Critique] = $FalloCritico%</b>",
                    Constantes.COLOR_ROJO)
        } else {
            GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "La probabilidad de magueo del objeto <b>" + objAMaguear.getObjModelo()
                    .getNombre().toString() + "</b> con la <b>" + objRunaOPocima.getObjModelo().getNombre().toString() + "</b>", Constantes.COLOR_NEGRO)
            GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "<b>[Exito Crítico] = $ExitoCritico%</b>",
                    Constantes.COLOR_AZUL)
            GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "<b>[Exito Normal] = $ExitoNormal%</b>",
                    Constantes.COLOR_VERDE_OSCURO)
            GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "<b>[Fallo Normal] = $FallaNormal%</b>",
                    Constantes.COLOR_NARANJA)
            GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "<b>[Fallo Crítico] = $FalloCritico%</b>",
                    Constantes.COLOR_ROJO)
        }
    }

    private fun getProbabilidadesMagueo(objMaguear: Objeto?, objRuna: Objeto?, statMagueo: Int, cantAumRuna: Int,
                                        pesoPlus: Int): IntArray {
        val probabilidades = IntArray(4)
        val objModelo: ObjetoModelo = objMaguear.getObjModelo()
        val razonMax: Float = MainServidor.NIVEL_MAX_PERSONAJE / MainServidor.NIVEL_MAX_OFICIO
        var nivelOficio = (razonMax * _statOficio.getNivel() / objModelo.getNivel()) as Int
        if (nivelOficio > 25) {
            nivelOficio = 25
        } else if (nivelOficio < 0) {
            nivelOficio = 0
        }
        when (statMagueo) {
            96, 97, 98, 99 -> {
                var suerte: Int = objModelo.getProbabilidadGC() * objModelo.getCostePA() / (objModelo.getBonusGC() + objMaguear
                        .getDañoPromedioNeutral())
                suerte += cantAumRuna + nivelOficio
                if (suerte > 100) {
                    suerte = 100
                } else if (suerte < 5) {
                    suerte = 5
                }
                probabilidades[0] = suerte
                probabilidades[1] = 0
                probabilidades[2] = 100 - suerte
                probabilidades[3] = 0
            }
            else -> {
                var pozoResidual = 0
                if (MainServidor.PARAM_FM_CON_POZO_RESIDUAL) {
                    pozoResidual += objMaguear.getStats().getStatParaMostrar(Constantes.STAT_POZO_RESIDUAL)
                }
                var pesoGlActual = 0f
                var pesoGlMin = 0f
                var pesoGlMax = 0f
                var cantStMin = 0
                var cantStMax = 0
                var cantStActual = 0
                var pesoStActual = 0f
                var pesoStMax = 0f
                var pesoStMin = 0f
                pesoGlActual -= pozoResidual.toFloat()
                if (MainServidor.MODO_DEBUG) {
                    System.out.println("-------------- FORMULA FM --------------")
                    System.out.println("statMagueo: $statMagueo")
                    System.out.println("pozoResidual: $pozoResidual")
                }
                var pesoExo = 0f
                var pesoExcesoOver = 0f
                for (entry in objMaguear.getStats().getEntrySet()) {
                    val statID: Int = entry.getKey()
                    var cant: Int = entry.getValue()
                    val statPositivo: Int = Constantes.getStatPositivoDeNegativo(statID)
                    var coef = 1f
                    if (statPositivo != statID) {
                        cant *= -1
                    }
                    if (statPositivo == statMagueo) {
                        cantStActual = cant
                        pesoStActual = Constantes.getPesoStat(statPositivo) * cant
                    }
                    if (objModelo.tieneStatInicial(statPositivo)) {
                        val max: Int = objModelo.getDuoInicial(statPositivo)._segundo
                        if (max < cant) {
                            // over
                            if (statPositivo != statMagueo) {
                                pesoExcesoOver += Constantes.getPesoStat(statPositivo) * (cant - max)
                            }
                            coef = 1.2f
                        }
                    } else {
                        // exo
                        if (statPositivo != statMagueo) {
                            pesoExo += Constantes.getPesoStat(statPositivo) * cant
                        }
                        coef = 1.4f
                    }
                    pesoGlActual += Constantes.getPesoStat(statPositivo) * cant * coef
                }
                for (entry in objModelo.getStatsIniciales().entrySet()) {
                    val statID: Int = entry.getKey()
                    val statMin: Int = entry.getValue()._primero
                    val statMax: Int = entry.getValue()._segundo
                    if (statID == statMagueo) {
                        cantStMin = statMin
                        cantStMax = statMax
                        pesoStMin = Constantes.getPesoStat(statID) * statMin
                        pesoStMax = Constantes.getPesoStat(statID) * statMax
                    }
                    pesoGlMin += Constantes.getPesoStat(statID) * statMin
                    pesoGlMax += Constantes.getPesoStat(statID) * statMax
                }
                // if (Bustemu.RATE_FM != 1) {///
                // cantStMax *= Bustemu.RATE_FM;
                // pesoStMax *= Bustemu.RATE_FM;
                // pesoGlMax *= Bustemu.RATE_FM;
                // }
                var tipoMagueo = MAGUEO_EXO
                if (pesoStMax != 0f) {
                    tipoMagueo = MAGUEO_NORMAL // 0
                }
                if (tipoMagueo == MAGUEO_NORMAL && cantStActual + cantAumRuna > cantStMax) { // es stat over
                    tipoMagueo = MAGUEO_OVER // 1
                }
                val pesoRuna = Math.ceil(Constantes.getPesoStat(statMagueo) * cantAumRuna) as Int
                if (MainServidor.MODO_DEBUG) {
                    System.out.println("tipoMagueo: $tipoMagueo")
                    System.out.println("cantAumRuna: $cantAumRuna , pesoRuna: $pesoRuna")
                    System.out.println("cantStMax: $cantStMax , cantStActual: $cantStActual")
                    System.out.println("pesoGlMax: " + pesoGlMax + " , pesoGlMin: " + pesoGlMin + " , pesoGlActual: "
                            + pesoGlActual)
                    System.out.println("pesoStMax: " + pesoStMax + " , pesoStMin: " + pesoStMin + " , pesoStActual: "
                            + pesoStActual)
                }
                var puede = true
                if (pesoGlMax == 0f) {
                    puede = false
                }
                pesoGlMax += pesoPlus.toFloat() // el aumento de la runa plus
                if (pesoStMax <= pesoStActual && Constantes.excedioLimitePeso(objRuna, cantStActual + cantAumRuna)) {
                    if (MainServidor.MODO_DEBUG) {
                    }
                    puede = false
                }
                when (tipoMagueo) {
                    MAGUEO_EXO -> {
                        if (pesoGlActual < 0 && pesoGlMax < 0) {
                            if (MainServidor.MODO_DEBUG) {
                            }
                            puede = false
                        }
                        if (pesoStActual * 3 + pesoRuna + pesoGlActual >= pesoGlMax * 2) {
                            if (MainServidor.MODO_DEBUG) {
                            }
                            puede = false
                        }
                        if (Constantes.excedioLimiteMagueoDeRuna(objRuna.getObjModeloID(), cantStActual + cantAumRuna)) {
                            if (MainServidor.MODO_DEBUG) {
                            }
                            puede = false
                        }
                        if (Constantes.excedioLimiteExomagia(statMagueo, cantStActual + cantAumRuna)) {
                            puede = false
                        }
                    }
                    MAGUEO_OVER -> {
                        if (pesoStActual + pesoRuna > pesoStMax * 2) {
                            if (MainServidor.MODO_DEBUG) {
                                System.out.println("fallo en 5")
                            }
                            puede = false
                        }
                        if (Constantes.excedioLimiteOvermagia(statMagueo, cantStActual + cantAumRuna)) {
                            puede = false
                        }
                        if (cantStMin < 0 && (cantStActual >= 0 || cantStActual >= cantStMax)) {
                            if (MainServidor.MODO_DEBUG) {
                            }
                            puede = false
                        }
                        if (cantStMax < 0 && cantStMax < cantStActual + cantAumRuna) {
                            if (MainServidor.MODO_DEBUG) {
                            }
                            puede = false
                        }
                    }
                    MAGUEO_NORMAL -> {
                        if (cantStMin < 0 && (cantStActual >= 0 || cantStActual >= cantStMax)) {
                            if (MainServidor.MODO_DEBUG) {
                            }
                            puede = false
                        }
                        if (cantStMax < 0 && cantStMax < cantStActual + cantAumRuna) {
                            if (MainServidor.MODO_DEBUG) {
                            }
                            puede = false
                        }
                    }
                }
                if (!puede) {
                    if (MainServidor.MODO_DEBUG) {
                    }
                    val FC = 40 + pesoRuna / 2
                    probabilidades[0] = 0
                    probabilidades[1] = 0
                    probabilidades[2] = 100 - FC
                    probabilidades[3] = FC
                } else {
                    // pGlActual = Math.max(0, pGlActual);
                    // pGlMax = Math.max(0, pGlMax);
                    // pGlMin = Math.max(0, pGlMin);
                    var porcGlobal = 0f
                    var porcStat = 0f
                    if (pesoGlMin < 0 || pesoGlMax < 0) {
                        if (pesoGlActual > 0) {
                            pesoGlActual += Math.abs(pesoGlMin)
                        }
                        porcGlobal = Math.abs(pesoGlActual * 100 / pesoGlMin)
                    } else {
                        porcGlobal = if (pesoGlMax == 0f) {
                            // no tiene stats
                            100f
                        } else if (pesoGlMax == pesoGlMin) {
                            // son stats fijos
                            pesoGlActual * 100f / pesoGlMax
                        } else {
                            (pesoGlActual - pesoGlMin) * 100f / (pesoGlMax - pesoGlMin)
                        }
                    }
                    if (pesoStMin < 0 || pesoStMax < 0) {
                        if (pesoStActual > 0) {
                            pesoStActual += Math.abs(pesoStMin)
                        }
                        porcStat = Math.abs(pesoStActual * 100 / pesoStMin)
                    } else {
                        if (pesoStMax == 0f) { // exo
                            porcStat = 100f
                        } else if (pesoStMax == pesoStMin) {
                            porcStat = pesoStActual * 100f / pesoStMax
                        } else {
                            if (tipoMagueo == MAGUEO_NORMAL) {
                                pesoStMin -= pesoExo / 2 + pesoExcesoOver / 3
                                pesoStMin = Math.max(0, pesoStMin)
                            }
                            porcStat = (pesoStActual - pesoStMin) * 100f / (pesoStMax - pesoStMin)
                        }
                    }
                    porcGlobal = Math.max(0, porcGlobal)
                    porcStat = Math.max(0, porcStat)
                    // el porcStat esta basado desde el Min Valor al Max Valor
                    if (MainServidor.MODO_DEBUG) {
                        System.out.println("Antes-> porcGlobal: $porcGlobal , porcStat: $porcStat")
                    }
                    var pSG = ((pesoStMax + pesoPlus) * 100 / pesoGlMax).toInt()
                    pSG = Math.max(0, pSG)
                    var pG = (100 - porcGlobal).toInt()
                    var pS = (100 - porcStat).toInt()
                    var porcMaxExito = 0
                    var EC = 0
                    var EN = 0
                    val FN = 0
                    var FC = 0
                    when (tipoMagueo) {
                        MAGUEO_EXO -> porcMaxExito = 11 - pesoRuna / 10
                        MAGUEO_OVER -> porcMaxExito = 50 - pesoRuna / 2
                        MAGUEO_NORMAL -> porcMaxExito = 100
                    }
                    when (tipoMagueo) {
                        MAGUEO_EXO -> {
                            if (pesoRuna + pesoExo * 3 + pesoExcesoOver * 2 >= pesoGlMax) {
                                pG = 0
                            }
                            pS = 0
                            EN = pG / 2 // puede ser maximo 50
                        }
                        MAGUEO_OVER -> {
                            if (pesoRuna + pesoExo * 3 + pesoExcesoOver * 2 >= pesoGlMax) {
                                pG = 0
                            } else if (pesoExo > 0) {
                                if (pesoExo > pesoRuna) {
                                    pG -= (pesoExo / pesoRuna).toInt()
                                }
                            }
                            EN = pSG + pS // pS aqui es negativo
                        }
                        MAGUEO_NORMAL -> {
                            if (pesoExo > 0) {
                                if (pesoExo > pesoRuna) {
                                    pG -= (pesoExo / pesoRuna).toInt()
                                }
                            }
                            if (pesoExcesoOver > 0) {
                                if (pesoExcesoOver > pesoRuna) {
                                    pG -= (pesoExcesoOver / pesoRuna).toInt()
                                }
                            }
                            if (porcStat > MainServidor.MAX_PORCENTAJE_DE_STAT_PARA_FM) {
                                pS = Math.sqrt(pS)
                            }
                            EN = pS
                        }
                    }
                    if (MainServidor.MODO_DEBUG) {
                        System.out.println("Despues-> porcGlobal: $porcGlobal , porcStat: $porcStat")
                        System.out.println("Despues-> pG: $pG, pS: $pS, pSG: $pSG")
                        System.out.println("Anterior-> EN: $EN")
                    }
                    EN = Math.max(0, EN)
                    EN = Math.min(porcMaxExito, EN)
                    val factorRate: Int = (100 - EN) * MainServidor.RATE_FM / 100
                    EN += factorRate // aqui se adiciona el rate de la FM
                    if (MainServidor.MODO_DEBUG) {
                        System.out.println("Despues-> EN: $EN")
                    }
                    when (tipoMagueo) {
                        MAGUEO_EXO -> {
                            EC = Math.ceil(EN / 2f)
                            EN = 0
                        }
                        MAGUEO_OVER -> {
                        }
                        MAGUEO_NORMAL -> {
                            var critico = 0
                            critico = if (pG <= 0) {
                                ((pS + pSG) / 2)
                            } else {
                                ((pS + pSG + pG) / 2)
                            }
                            critico = Math.max(1, critico)
                            critico = Math.min(99, critico)
                            if (critico < EN) {
                                EC = critico
                                EN -= critico
                            } else {
                                EC = EN
                                EN = 0
                            }
                        }
                    }
                    // if (pesoStMax == pesoGlMax && pesoStActual == 0) {
                    // // cuando tiene un solo stat
                    // EC = 99;
                    // EN = 1;
                    // } else
                    FC = 100 - (EN + EC) // no pasa nada
                    // FC = pesoRuna * FN / 100;
                    // if (pesoGlActual == 0) {
                    // FC = 0;
                    // }
                    // FN = FN - FC;
                    probabilidades[0] = EC
                    probabilidades[1] = EN
                    probabilidades[2] = FN
                    probabilidades[3] = FC
                }
            }
        }
        return probabilidades
    }

    private fun addIngrediente(idModelo: Int, cantidad: Long) {
        if (_ingredientes!![idModelo] == null) {
            _ingredientes.put(idModelo, cantidad)
        } else {
            val nueva = _ingredientes!![idModelo]!! + cantidad
            _ingredientes.remove(idModelo)
            _ingredientes.put(idModelo, nueva)
        }
    }

    fun setArtesanoCliente(artesano: Personaje?, _perso: Personaje?) {
        _artesano = artesano
        _cliente = _perso
        if (_objArtesano == null) {
            _objArtesano = ArrayList()
        }
        if (_objCliente == null) {
            _objCliente = ArrayList()
        }
        if (_objetosPago == null) {
            _objetosPago = ArrayList()
        }
        if (_objetosSiSeConsegui == null) {
            _objetosSiSeConsegui = ArrayList()
        }
    }

    // machacar recursos
    fun iniciarTaller(objArtesano: ArrayList<Duo<Integer?, Long?>?>?,
                      objCliente: ArrayList<Duo<Integer?, Long?>?>?): Boolean {
        if (!_esCraft) {
            return false
        }
        _ingredientes.clear()
        for (duo in objArtesano) {
            addIngrediente(duo._primero, duo._segundo)
        }
        for (duo in objCliente) {
            addIngrediente(duo._primero, duo._segundo)
        }
        return if (Constantes.esSkillMago(trabajoID)) {
            trabajoPagoFM()
        } else {
            trabajoPagoCraft()
        }
    }

    private fun iniciarThread() {
        if (!_finThread) {
            return
        }
        val _thread = Thread(this)
        _thread.setDaemon(true)
        _thread.setPriority(10)
        _thread.start()
    }

    fun run() {
        try {
            _finThread = false
            if (_esCraft) {
                val esVIP: Boolean = _artesano.esAbonado()
                var speedCraft = MainServidor.PARAM_VIP_CRAFT_SPEED && esVIP
                _ultimosIngredientes.clear()
                _ultimosIngredientes.putAll(_ingredientes)
                try {
                    if (!Constantes.esSkillMago(trabajoID) && MainServidor.PARAM_CRAFT_SPEED) {
                        trabajoCraftear(false, _cuantasRepeticiones)
                    } else {
                        for (a in _cuantasRepeticiones downTo 1) {
                            if (_interrumpir != MENSAJE_SIN_RESULTADO && _interrumpir != MENSAJE_OBJETO_FABRICADO) {
                                break
                            }
                            if (a == 1) {
                                speedCraft = false
                            }
                            if (_cuantasRepeticiones > 1 && a != _cuantasRepeticiones) {
                                GestorSalida.ENVIAR_EA_TURNO_RECETA(_artesano, a)
                            }
                            iniciarCraft(speedCraft)
                            if (a == 1 && _interrumpir == MENSAJE_SIN_RESULTADO) {
                                _interrumpir = MENSAJE_OBJETO_FABRICADO
                            }
                            if (_interrumpir == MENSAJE_SIN_RESULTADO) {
                                Thread.sleep(if (speedCraft) 25 else 1000)
                            }
                        }
                    }
                } catch (e: Exception) {
                }
                when (_interrumpir) {
                    MENSAJE_RECETA_NO_FUNCIONA, MENSAJE_FALTA_RECURSOS -> {
                        if (!_esForjaMagia) {
                            GestorSalida.ENVIAR_Ec_RESULTADO_RECETA(_artesano, "EI")
                        }
                        GestorSalida.ENVIAR_IO_ICONO_OBJ_INTERACTIVO(_artesano.getMapa(), _artesano.getID(), "-")
                    }
                }
                if (_cuantasRepeticiones > 1 || _interrumpir > 1) {
                    GestorSalida.ENVIAR_Ea_MENSAJE_RECETAS(_artesano, _interrumpir)
                }
            } else {
                // recolecta
                try {
                    Thread.sleep(tiempo)
                } catch (e: Exception) {
                }
                _statOficio.finalizarTrabajo(_artesano)
            }
        } catch (e: Exception) {
            MainServidor.redactarLogServidorln("EXCEPTION DE RUN TRABAJO EN OFICIO esCraft: $_esCraft")
            e.printStackTrace()
        } finally {
            if (_ingredientes != null) {
                if (!_esForjaMagia || esTaller()) {
                    _ingredientes.clear()
                }
            }
            _cuantasRepeticiones = 0
            _interrumpir = MENSAJE_SIN_RESULTADO
            _finThread = true
        }
    }

    private fun iniciarCraft(esSpeedCraft: Boolean): Objeto? {
        return if (Constantes.esSkillMago(trabajoID)) {
            trabajoMaguear(esSpeedCraft)
        } else {
            trabajoCraftear(esSpeedCraft, 1)
            null
        }
    }

    private fun trabajoCraftear(esSpeedCraft: Boolean, cant: Int) {
        try {
            val ingredientesModelo: Map<Integer, Long> = TreeMap<Integer, Long>()
            val runasModelo: Map<Integer, Long> = TreeMap<Integer, Long>()
            for (ingrediente in _ingredientes.entrySet()) {
                val objetoID: Int = ingrediente.getKey()
                val cantObjeto: Long = ingrediente.getValue()
                val objeto: Objeto = _artesano.getObjeto(objetoID)
                if (objeto == null || objeto.getCantidad() < cantObjeto * cant) {
                    if (objeto == null) {
                        continue
                    } else {
                        GestorSalida.ENVIAR_Im_INFORMACION(_artesano, "1CRAFT_NOT_ENOUGHT;" + objeto.getObjModeloID().toString() + " ("
                                + objeto.getCantidad().toString() + ")")
                    }
                    _interrumpir = MENSAJE_FALTA_RECURSOS
                    return
                }
                if (_varios) {
                    GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_artesano, 'O', "+", objetoID.toString() + "|" + cantObjeto * cant)
                }
                if (trabajoID == Constantes.SKILL_ROMPER_OBJETO) {
                    objeto.runasRomperObjeto(runasModelo, cantObjeto)
                } else {
                    ingredientesModelo.put(objeto.getObjModeloID(), cantObjeto)
                }
                val nuevaCant: Long = objeto.getCantidad() - cantObjeto * cant
                if (nuevaCant == 0L) {
                    _artesano.borrarOEliminarConOR(objetoID, true)
                } else {
                    objeto.setCantidad(nuevaCant)
                    GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_artesano, objeto)
                }
            }
            _varios = true
            var firmado = false
            if (ingredientesModelo.containsKey(7508)) {
                ingredientesModelo.remove(7508)
                firmado = true
            }
            var resultadoReceta = -1
            resultadoReceta = if (trabajoID == Constantes.SKILL_ROMPER_OBJETO) {
                if (runasModelo.isEmpty()) -1 else 8378
            } else {
                Mundo.getIDRecetaPorIngredientes(_statOficio.getOficio().listaRecetaPorTrabajo(trabajoID),
                        ingredientesModelo)
            }
            if (resultadoReceta == -1 || Mundo.getObjetoModelo(resultadoReceta) == null || trabajoID != Constantes.SKILL_ROMPER_OBJETO && !_statOficio.getOficio().puedeReceta(trabajoID,
                            resultadoReceta)) {
                _interrumpir = MENSAJE_RECETA_NO_FUNCIONA
                return
            }
            var suerte = 100
            suerte = when (trabajoID) {
                Constantes.SKILL_PELAR_PATATAS, Constantes.SKILL_UTILIZAR_BANCO, Constantes.SKILL_MACHACAR_RECURSOS -> 100
                Constantes.SKILL_ROMPER_OBJETO -> 99
                else -> Constantes.getSuerteNivelYSlots(_statOficio.getNivel(), ingredientesModelo.size())
            }
            val exito = MainServidor.PARAM_CRAFT_SIEMPRE_EXITOSA || suerte == 100 || suerte >= Formulas
                    .getRandomInt(1, 100)
            if (exito) {
                var objCreado: Objeto = Mundo.getObjetoModelo(resultadoReceta).crearObjeto(cant, Constantes.OBJETO_POS_NO_EQUIPADO,
                        if (MainServidor.PARAM_CRAFT_PERFECTO_STATS) CAPACIDAD_STATS.MAXIMO else CAPACIDAD_STATS.RANDOM)
                if (trabajoID == Constantes.SKILL_ROMPER_OBJETO) {
                    val st = StringBuilder()
                    for (entry in runasModelo.entrySet()) {
                        if (entry.getValue() > 0) {
                            if (st.length() > 0) {
                                st.append(",")
                            }
                            st.append("1f4#" + Integer.toHexString(entry.getKey()).toString() + "#" + Long.toHexString(entry.getValue()))
                        }
                    }
                    objCreado.convertirStringAStats(st.toString())
                } else if (firmado) {
                    objCreado.addStatTexto(Constantes.STAT_FACBRICADO_POR, "0#0#0#" + _artesano.getNombre())
                }
                val igual: Objeto = _artesano.getObjIdentInventario(objCreado, null)
                if (igual == null) {
                    _artesano.addObjetoConOAKO(objCreado, true)
                } else {
                    igual.setCantidad(igual.getCantidad() + 1)
                    GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_artesano, igual)
                    objCreado = igual
                }
                if (!esSpeedCraft) {
                    GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_artesano, 'O', "+", objCreado.stringObjetoConPalo(objCreado
                            .getCantidad()))
                    GestorSalida.ENVIAR_Ec_RESULTADO_RECETA(_artesano, "K;$resultadoReceta")
                }
            } else {
                if (!esSpeedCraft) {
                    GestorSalida.ENVIAR_Ec_RESULTADO_RECETA(_artesano, "EF")
                    GestorSalida.ENVIAR_Im_INFORMACION(_artesano, "0118")
                }
            }
            if (!esSpeedCraft) {
                GestorSalida.ENVIAR_IO_ICONO_OBJ_INTERACTIVO(_artesano.getMapa(), _artesano.getID(), (if (exito) "+" else "-")
                        + resultadoReceta)
                GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_artesano)
            }
            when (trabajoID) {
                Constantes.SKILL_PELAR_PATATAS, Constantes.SKILL_UTILIZAR_BANCO, Constantes.SKILL_ROMPER_OBJETO, Constantes.SKILL_MACHACAR_RECURSOS -> {
                }
                else -> {
                    val exp: Int = Constantes.calculXpGanadaEnOficio(_statOficio.getNivel(), ingredientesModelo.size()) * cant
                    _statOficio.addExperiencia(_artesano, preExp(exp), Constantes.OFICIO_EXP_TIPO_CRAFT)
                }
            }
            _artesano.setOcupado(false)
            _artesano.setHaciendoTrabajo(null)
        } catch (e: Exception) {
            _interrumpir = MENSAJE_INTERRUMPIDA
        }
    }

    private fun trabajoMaguear(esSpeedCraft: Boolean): Objeto? {
        try {
            var objAMaguear: Objeto? = null
            var objRunaFirma: Objeto? = null
            var objRunaOPocima: Objeto? = null
            var statMagueo = -1
            var valorRuna = 0
            var pesoPlusRuna = 0
            var firmado = false
            if (_statOficio == null) {
                _interrumpir = MENSAJE_RECETA_NO_FUNCIONA
                return null
            }
            for (idIngrediente in _ingredientes.keySet()) {
                val ing: Objeto = _artesano.getObjeto(idIngrediente)
                        ?: // GestorSalida.ENVIAR_Im_INFORMACION(_artesano, "1OBJECT_DONT_EXIST;" + idIngrediente);
                        // _interrumpir = MENSAJE_FALTA_RECURSOS;
                        // return null;
                        continue
                val statRuna: Int = Constantes.getStatPorRunaPocima(ing)
                val idModelo: Int = ing.getObjModeloID()
                if (idModelo == 7508) {
                    firmado = true
                    objRunaFirma = ing
                } else if (statRuna > 0) {
                    statMagueo = statRuna
                    valorRuna = Constantes.getValorPorRunaPocima(ing)
                    pesoPlusRuna = Constantes.getPotenciaPlusRuna(ing)
                    objRunaOPocima = ing
                } else {
                    val tipo: Int = ing.getObjModelo().getTipo()
                    when (tipo) {
                        Constantes.OBJETO_TIPO_AMULETO, Constantes.OBJETO_TIPO_ARCO, Constantes.OBJETO_TIPO_VARITA, Constantes.OBJETO_TIPO_BASTON, Constantes.OBJETO_TIPO_DAGAS, Constantes.OBJETO_TIPO_ESPADA, Constantes.OBJETO_TIPO_MARTILLO, Constantes.OBJETO_TIPO_PALA, Constantes.OBJETO_TIPO_ANILLO, Constantes.OBJETO_TIPO_CINTURON, Constantes.OBJETO_TIPO_BOTAS, Constantes.OBJETO_TIPO_SOMBRERO, Constantes.OBJETO_TIPO_CAPA, Constantes.OBJETO_TIPO_HACHA, Constantes.OBJETO_TIPO_HERRAMIENTA, Constantes.OBJETO_TIPO_PICO, Constantes.OBJETO_TIPO_GUADAÑA, Constantes.OBJETO_TIPO_MOCHILA, Constantes.OBJETO_TIPO_BALLESTA, Constantes.OBJETO_TIPO_ARMA_MAGICA -> objAMaguear = ing
                    }
                }
            }
            if (objAMaguear == null || objRunaOPocima == null) {
                _interrumpir = MENSAJE_FALTA_RECURSOS
                return null
            }
            var pesoRuna: Float = Constantes.getPesoStat(statMagueo)
            when (statMagueo) {
                96, 97, 98, 99 -> pesoRuna = 1f
            }
            if (pesoRuna <= 0) {
                GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_artesano, "<b>RUNA FORJAMAGIA INCORRECTA</b>", Constantes.COLOR_ROJO)
                return null
            }
            if (objRunaOPocima != null) {
                val nuevaCant: Long = objRunaOPocima.getCantidad() - 1
                if (nuevaCant <= 0) {
                    _artesano.borrarOEliminarConOR(objRunaOPocima.getID(), true)
                } else {
                    objRunaOPocima.setCantidad(nuevaCant)
                    GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_artesano, objRunaOPocima)
                }
                val o: Objeto? = objRunaOPocima
                val n = _ingredientes!![o.getID()]!! - 1
                if (n <= 0) {
                    _ultimosIngredientes.remove(o.getID())
                } else {
                    _ultimosIngredientes.put(o.getID(), n)
                }
            }
            if (objRunaFirma != null) {
                val nuevaCant: Long = objRunaFirma.getCantidad() - 1
                if (nuevaCant <= 0) {
                    _artesano.borrarOEliminarConOR(objRunaFirma.getID(), true)
                } else {
                    objRunaFirma.setCantidad(nuevaCant)
                    GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_artesano, objRunaFirma)
                }
                val o: Objeto? = objRunaFirma
                val n = _ingredientes!![o.getID()]!! - 1
                if (n <= 0) {
                    _ultimosIngredientes.remove(o.getID())
                } else {
                    _ultimosIngredientes.put(o.getID(), n)
                }
            }
            if (objAMaguear != null) {
                val nuevaCantidad: Long = objAMaguear.getCantidad() - 1
                if (nuevaCantidad >= 1) {
                    val nuevoObj: Objeto = objAMaguear.clonarObjeto(nuevaCantidad, Constantes.OBJETO_POS_NO_EQUIPADO)
                    _artesano.addObjetoConOAKO(nuevoObj, true)
                    objAMaguear.setCantidad(1)
                    GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_artesano, objAMaguear)
                    GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_artesano, 'O', "+", nuevoObj.stringObjetoConPalo(nuevoObj.getCantidad()))
                    _ultimosIngredientes.put(objAMaguear.getID(), objAMaguear.getCantidad())
                }
            }
            _ingredientes.clear()
            _ingredientes.putAll(_ultimosIngredientes)
            var ExitoCritico = 0
            var ExitoNormal = 0
            var FallaNormal = 0
            var FalloCritico = 0
            val objModeloID: Int = objAMaguear.getObjModeloID()
            val jet: Int = Formulas.getRandomInt(1, 100)
            var resultados = IntArray(4)
            var dano = valorRuna
            if (objAMaguear.esStatExo(statMagueo) && !objAMaguear.tieneAlgunStatExo()) {
                resultados[0] = 15
                resultados[1] = 0
                resultados[2] = 0
                resultados[3] = 100
                dano = 10000
            } else {
                resultados = getProbabilidadesMagueo(objAMaguear, objRunaOPocima, statMagueo, valorRuna, pesoPlusRuna)
                if (objAMaguear.esStatExo(statMagueo) and MainServidor.MODO_IMPACT) dano = 10000
            }
            ExitoCritico = resultados[0]
            ExitoNormal = resultados[1]
            FallaNormal = resultados[2]
            FalloCritico = resultados[3]
            if (MainServidor.MODO_DEBUG) {
                System.out.println("ExitoCritico: $ExitoCritico")
                System.out.println("ExitoNormal: $ExitoNormal")
                System.out.println("FallaNormal: $FallaNormal")
                System.out.println("FalloCritico: $FalloCritico")
                System.out.println("Jet: $jet")
            }
            var r = 0
            var t = 0
            for (i in resultados) {
                r++
                t += i
                if (jet <= t) {
                    break
                }
            }
            if (MainServidor.MODO_DEBUG) {
                var res = "NADA"
                when (r) {
                    RESULTADO_EXITO_CRITICO -> res = "RESULTADO_EXITO_CRITICO"
                    RESULTADO_EXITO_NORMAL -> res = "RESULTADO_EXITO_NORMAL"
                    RESULTADO_FALLO_NORMAL -> res = "RESULTADO_FALLO_NORMAL"
                    RESULTADO_FALLO_CRITICO -> res = "RESULTADO_FALLO_CRITICO"
                }
                System.out.println("Resultado: $res")
            }
            var exito = false
            when (r) {
                RESULTADO_EXITO_NORMAL -> {
                    // va antes porq agrega el mensaje de la magia no ha funcionado bien
                    objAMaguear.forjaMagiaPerder(statMagueo, valorRuna, false)
                    if (!esSpeedCraft) {
                        GestorSalida.ENVIAR_Im_INFORMACION(_artesano, "0194")
                    }
                    if (firmado) {
                        objAMaguear.addStatTexto(Constantes.STAT_MODIFICADO_POR, "0#0#0#" + _artesano.getNombre())
                    }
                    objAMaguear.forjaMagiaGanar(statMagueo, valorRuna)
                    exito = true
                }
                RESULTADO_EXITO_CRITICO -> {
                    if (firmado) {
                        objAMaguear.addStatTexto(Constantes.STAT_MODIFICADO_POR, "0#0#0#" + _artesano.getNombre())
                    }
                    objAMaguear.forjaMagiaGanar(statMagueo, valorRuna)
                    exito = true
                }
                RESULTADO_FALLO_NORMAL -> if (!esSpeedCraft) {
                    GestorSalida.ENVIAR_Im_INFORMACION(_artesano, "0183")
                }
                RESULTADO_FALLO_CRITICO -> {
                    objAMaguear.forjaMagiaPerder(statMagueo, if (MainServidor.MODO_IMPACT) dano else valorRuna, true)
                    if (!esSpeedCraft) {
                        GestorSalida.ENVIAR_Im_INFORMACION(_artesano, "0117")
                    }
                }
            }
            if (!esSpeedCraft) {
                GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(_artesano, objAMaguear)
                if (exito) {
                    GestorSalida.ENVIAR_Ec_RESULTADO_RECETA(_artesano, "K;" + objAMaguear.getObjModeloID())
                } else {
                    GestorSalida.ENVIAR_Ec_RESULTADO_RECETA(_artesano, "EF")
                }
                GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_artesano, 'O', "+", objAMaguear.stringObjetoConPalo(objAMaguear
                        .getCantidad()))
                for (e in _ultimosIngredientes.entrySet()) {
                    GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_artesano, 'O', "+", e.getKey().toString() + "|" + e.getValue())
                }
                GestorSalida.ENVIAR_IO_ICONO_OBJ_INTERACTIVO(_artesano.getMapa(), _artesano.getID(), (if (exito) "+" else "-")
                        + objModeloID)
            }
            val exp: Int = Constantes.getExpForjamaguear(Constantes.getPesoStat(statMagueo) * valorRuna, objAMaguear.getObjModelo()
                    .getNivel())
            _statOficio.addExperiencia(_artesano, preExp(exp), Constantes.OFICIO_EXP_TIPO_CRAFT)
            return objAMaguear
        } catch (e: Exception) {
            _interrumpir = MENSAJE_INTERRUMPIDA
        }
        return null
    }

    private fun trabajoPagoCraft(): Boolean {
        return try {
            var nuevoObj: Objeto? = null
            var r = RESULTADO_FALLO_NORMAL.toInt()
            val ingredientesPorModelo: Map<Integer, Long?> = TreeMap<Integer, Long>()
            for (ingrediente in _ingredientes.entrySet()) {
                val objetoID: Int = ingrediente.getKey()
                val cantObjeto: Long = ingrediente.getValue()
                val objeto: Objeto = Mundo.getObjeto(objetoID)
                var dueño: Personaje? = null
                if (_artesano.tieneObjetoID(objetoID)) {
                    dueño = _artesano
                } else if (_cliente.tieneObjetoID(objetoID)) {
                    dueño = _cliente
                }
                if (dueño == null || objeto == null || objeto.getCantidad() < cantObjeto) {
                    _artesano.setForjaEc("EI")
                    _cliente.setForjaEc("EI")
                    if (objeto == null) {
                        GestorSalida.ENVIAR_Im_INFORMACION(_artesano, "1OBJECT_DONT_EXIST;$objetoID")
                    } else {
                        GestorSalida.ENVIAR_Im_INFORMACION(_artesano, "1CRAFT_NOT_ENOUGHT;" + objeto.getObjModeloID().toString() + " ("
                                + objeto.getCantidad().toString() + ")")
                    }
                    return false
                }
                val nuevaCant: Long = objeto.getCantidad() - cantObjeto
                if (nuevaCant <= 0) {
                    // agregar si lo tiene el artesano o el cliente
                    dueño.borrarOEliminarConOR(objetoID, true)
                } else {
                    objeto.setCantidad(nuevaCant)
                    GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(dueño, objeto)
                }
                val idModelo: Int = objeto.getObjModeloID()
                if (ingredientesPorModelo[idModelo] == null) {
                    ingredientesPorModelo.put(idModelo, cantObjeto)
                } else {
                    val nueva = ingredientesPorModelo[idModelo]!! + cantObjeto
                    ingredientesPorModelo.remove(idModelo)
                    ingredientesPorModelo.put(idModelo, nueva)
                }
            }
            var firmado = false
            if (ingredientesPorModelo.containsKey(7508)) {
                ingredientesPorModelo.remove(7508)
                firmado = true
            }
            val recetaID: Int = Mundo.getIDRecetaPorIngredientes(_statOficio.getOficio().listaRecetaPorTrabajo(trabajoID),
                    ingredientesPorModelo)
            if (recetaID == -1 || !_statOficio.getOficio().puedeReceta(trabajoID, recetaID)) {
                r = RESULTADO_FALLO_CRITICO.toInt()
            }
            var exito = false
            if (r != RESULTADO_FALLO_CRITICO.toInt()) {
                val suerte: Int = Constantes.getSuerteNivelYSlots(_statOficio.getNivel(), ingredientesPorModelo.size())
                exito = MainServidor.PARAM_CRAFT_SIEMPRE_EXITOSA || suerte >= Formulas.getRandomInt(1, 100)
                if (exito) {
                    r = RESULTADO_EXITO_NORMAL.toInt()
                    nuevoObj = Mundo.getObjetoModelo(recetaID).crearObjeto(1, Constantes.OBJETO_POS_NO_EQUIPADO,
                            if (MainServidor.PARAM_CRAFT_PERFECTO_STATS) CAPACIDAD_STATS.MAXIMO else CAPACIDAD_STATS.RANDOM)
                    if (firmado) {
                        nuevoObj.addStatTexto(Constantes.STAT_FACBRICADO_POR, "0#0#0#" + _artesano.getNombre())
                    }
                    val igual: Objeto = _cliente.getObjIdentInventario(nuevoObj, null)
                    if (igual == null) {
                        _cliente.addObjetoConOAKO(nuevoObj, true)
                    } else {
                        igual.setCantidad(igual.getCantidad() + 1)
                        GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_cliente, igual)
                        nuevoObj = igual
                    }
                }
            }
            GestorSalida.ENVIAR_IO_ICONO_OBJ_INTERACTIVO(_artesano.getMapa(), _artesano.getID(), (if (nuevoObj == null) "-" else "+") + recetaID)
            GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_artesano)
            GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_cliente)
            when (r) {
                RESULTADO_EXITO_NORMAL, RESULTADO_EXITO_CRITICO -> {
                    val statsNuevoObj: String = nuevoObj.convertirStatsAString(false)
                    val todaInfo: String = nuevoObj.stringObjetoConPalo(nuevoObj.getCantidad())
                    GestorSalida.ENVIAR_ErK_RESULTADO_TRABAJO(_artesano, "O", "+", todaInfo)
                    GestorSalida.ENVIAR_ErK_RESULTADO_TRABAJO(_cliente, "O", "+", todaInfo)
                    _artesano.setForjaEc("K;" + recetaID + ";T" + _cliente.getNombre() + ";" + statsNuevoObj)
                    _cliente.setForjaEc("K;" + recetaID + ";B" + _artesano.getNombre() + ";" + statsNuevoObj)
                }
                RESULTADO_FALLO_NORMAL, RESULTADO_FALLO_CRITICO -> {
                    GestorSalida.ENVIAR_Im_INFORMACION(_artesano, "0118")
                    _artesano.setForjaEc("EF")
                    _cliente.setForjaEc("EF")
                }
            }
            val exp: Int = Constantes.calculXpGanadaEnOficio(_statOficio.getNivel(), ingredientesPorModelo.size())
            _statOficio.addExperiencia(_artesano, preExp(exp), Constantes.OFICIO_EXP_TIPO_CRAFT)
            exito
        } catch (e: Exception) {
            false
        }
    }

    private fun trabajoPagoFM(): Boolean {
        return try {
            var objAMaguear: Objeto? = null
            var objRunaFirma: Objeto? = null
            var objRunaOPocima: Objeto? = null
            var statMagueo = -1
            var valorRuna = 0
            var pesoPlusRuna = 0
            var firmado = false
            for (idIngrediente in _ingredientes.keySet()) {
                val ing: Objeto = Mundo.getObjeto(idIngrediente)
                if (ing == null) {
                    _artesano.setForjaEc("EI")
                    _cliente.setForjaEc("EI")
                    return false
                }
                val statRuna: Int = Constantes.getStatPorRunaPocima(ing)
                val idModelo: Int = ing.getObjModeloID()
                if (idModelo == 7508) {
                    firmado = true
                    objRunaFirma = ing
                } else if (statRuna > 0) {
                    statMagueo = statRuna
                    valorRuna = Constantes.getValorPorRunaPocima(ing)
                    pesoPlusRuna = Constantes.getPotenciaPlusRuna(ing)
                    objRunaOPocima = ing
                } else {
                    val tipo: Int = ing.getObjModelo().getTipo()
                    when (tipo) {
                        Constantes.OBJETO_TIPO_AMULETO, Constantes.OBJETO_TIPO_ARCO, Constantes.OBJETO_TIPO_VARITA, Constantes.OBJETO_TIPO_BASTON, Constantes.OBJETO_TIPO_DAGAS, Constantes.OBJETO_TIPO_ESPADA, Constantes.OBJETO_TIPO_MARTILLO, Constantes.OBJETO_TIPO_PALA, Constantes.OBJETO_TIPO_ANILLO, Constantes.OBJETO_TIPO_CINTURON, Constantes.OBJETO_TIPO_BOTAS, Constantes.OBJETO_TIPO_SOMBRERO, Constantes.OBJETO_TIPO_CAPA, Constantes.OBJETO_TIPO_HACHA, Constantes.OBJETO_TIPO_HERRAMIENTA, Constantes.OBJETO_TIPO_PICO, Constantes.OBJETO_TIPO_GUADAÑA, Constantes.OBJETO_TIPO_MOCHILA, Constantes.OBJETO_TIPO_BALLESTA, Constantes.OBJETO_TIPO_ARMA_MAGICA -> {
                            objAMaguear = ing
                            val nuevaCantidad: Long = objAMaguear.getCantidad() - 1
                            if (nuevaCantidad >= 1) {
                                val modificado: Personaje = if (_artesano.tieneObjetoID(idIngrediente)) _artesano else _cliente
                                val nuevoObj: Objeto = objAMaguear.clonarObjeto(nuevaCantidad, Constantes.OBJETO_POS_NO_EQUIPADO)
                                modificado.addObjetoConOAKO(nuevoObj, true)
                                objAMaguear.setCantidad(1)
                                GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(modificado, objAMaguear)
                            }
                        }
                    }
                }
            }
            if (_statOficio == null || objAMaguear == null || objRunaOPocima == null) {
                _artesano.setForjaEc("EI")
                _cliente.setForjaEc("EI")
                return false
            }
            val pesoRuna: Float = Constantes.getPesoStat(statMagueo)
            if (pesoRuna <= 0) {
                _artesano.setForjaEc("EI")
                _cliente.setForjaEc("EI")
                return false
            }
            if (objRunaFirma != null) {
                val modificado: Personaje = if (_artesano.tieneObjetoID(objRunaFirma.getID())) _artesano else _cliente
                val nuevaCant: Long = objRunaFirma.getCantidad() - 1
                if (nuevaCant <= 0) {
                    modificado.borrarOEliminarConOR(objRunaFirma.getID(), true)
                } else {
                    objRunaFirma.setCantidad(nuevaCant)
                    GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(modificado, objRunaFirma)
                }
            }
            if (objRunaOPocima != null) {
                val modificado: Personaje = if (_artesano.tieneObjetoID(objRunaOPocima.getID())) _artesano else _cliente
                val nuevaCant: Long = objRunaOPocima.getCantidad() - 1
                if (nuevaCant <= 0) {
                    modificado.borrarOEliminarConOR(objRunaOPocima.getID(), true)
                } else {
                    objRunaOPocima.setCantidad(nuevaCant)
                    GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(modificado, objRunaOPocima)
                }
            }
            val objModeloID: Int = objAMaguear.getObjModeloID()
            val jet: Int = Formulas.getRandomInt(1, 100)
            val resultados = getProbabilidadesMagueo(objAMaguear, objRunaOPocima, statMagueo, valorRuna, pesoPlusRuna)
            var r = 0
            var t = 0
            for (i in resultados) {
                r++
                t += i
                if (jet <= t) {
                    break
                }
            }
            when (r) {
                RESULTADO_EXITO_NORMAL -> {
                    objAMaguear.forjaMagiaPerder(statMagueo, valorRuna, false)
                    GestorSalida.ENVIAR_Im_INFORMACION(_cliente, "0194")
                    GestorSalida.ENVIAR_Im_INFORMACION(_artesano, "0194")
                    if (firmado) {
                        objAMaguear.addStatTexto(Constantes.STAT_MODIFICADO_POR, "0#0#0#" + _artesano.getNombre())
                    }
                    objAMaguear.forjaMagiaGanar(statMagueo, valorRuna)
                }
                RESULTADO_EXITO_CRITICO -> {
                    if (firmado) {
                        objAMaguear.addStatTexto(Constantes.STAT_MODIFICADO_POR, "0#0#0#" + _artesano.getNombre())
                    }
                    objAMaguear.forjaMagiaGanar(statMagueo, valorRuna)
                }
                RESULTADO_FALLO_NORMAL -> {
                    GestorSalida.ENVIAR_Im_INFORMACION(_cliente, "0183")
                    GestorSalida.ENVIAR_Im_INFORMACION(_artesano, "0183")
                }
                RESULTADO_FALLO_CRITICO -> {
                    objAMaguear.forjaMagiaPerder(statMagueo, valorRuna, true)
                    GestorSalida.ENVIAR_Im_INFORMACION(_cliente, "0117")
                    GestorSalida.ENVIAR_Im_INFORMACION(_artesano, "0117")
                }
            }
            val modificado: Personaje = if (_artesano.tieneObjetoID(objAMaguear.getID())) _artesano else _cliente
            GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_cliente)
            GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_artesano)
            GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(modificado, objAMaguear)
            val todaInfo: String = objAMaguear.stringObjetoConPalo(objAMaguear.getCantidad())
            GestorSalida.ENVIAR_ErK_RESULTADO_TRABAJO(_artesano, "O", "+", todaInfo)
            GestorSalida.ENVIAR_ErK_RESULTADO_TRABAJO(_cliente, "O", "+", todaInfo)
            when (r) {
                RESULTADO_EXITO_NORMAL, RESULTADO_EXITO_CRITICO -> {
                    val statsNuevoObj: String = objAMaguear.convertirStatsAString(false)
                    _artesano.setForjaEc("K;" + objModeloID + ";T" + _cliente.getNombre() + ";" + statsNuevoObj)
                    _cliente.setForjaEc("K;" + objModeloID + ";B" + _artesano.getNombre() + ";" + statsNuevoObj)
                }
                RESULTADO_FALLO_NORMAL, RESULTADO_FALLO_CRITICO -> {
                    _artesano.setForjaEc("EF")
                    _cliente.setForjaEc("EF")
                }
            }
            val exp: Int = Constantes.getExpForjamaguear(Constantes.getPesoStat(statMagueo) * valorRuna, objAMaguear.getObjModelo()
                    .getNivel())
            _statOficio.addExperiencia(_artesano, preExp(exp), Constantes.OFICIO_EXP_TIPO_CRAFT)
            r == RESULTADO_EXITO_CRITICO.toInt() || r == RESULTADO_EXITO_NORMAL.toInt()
        } catch (e: Exception) {
            false
        }
    }

    fun limpiarReceta() {
        if (_ingredientes != null) {
            _ingredientes.clear()
        }
        if (_ultimosIngredientes != null) {
            _ultimosIngredientes.clear()
        }
        kamasSiSeConsigue = 0
        kamasPaga = kamasSiSeConsigue
        _ok2 = false
        _ok1 = _ok2
        _cliente = null
        _artesano = null
        if (_objArtesano != null) {
            _objArtesano.clear()
        }
        if (_objCliente != null) {
            _objCliente.clear()
        }
        if (_objetosPago != null) {
            _objetosPago.clear()
        }
        if (_objetosSiSeConsegui != null) {
            _objetosSiSeConsegui.clear()
        }
    }

    fun craftearXVeces(cantidad: Int) {
        if (_esCraft) {
            if (_cuantasRepeticiones > 0) {
                return
            }
            try {
                _cuantasRepeticiones = Math.abs(cantidad)
                _interrumpir = MENSAJE_SIN_RESULTADO
                _varios = false
                iniciarThread()
            } catch (e: Exception) {
            }
        }
    }

    fun interrumpirReceta() {
        if (_esCraft) {
            _interrumpir = MENSAJE_INTERRUMPIDA
        }
    }

    fun ponerIngredUltRecet() {
        if (_ultimosIngredientes == null || _ultimosIngredientes!!.isEmpty() || _ingredientes == null || !_ingredientes!!
                        .isEmpty()) {
            return
        }
        _ingredientes.putAll(_ultimosIngredientes)
        for (e in _ingredientes.entrySet()) {
            val objeto: Objeto = _artesano.getObjeto(e.getKey()) ?: continue
            if (objeto.getCantidad() < e.getValue()) {
                continue
            }
            GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_artesano, 'O', "+", objeto.getID().toString() + "|" + e.getValue())
        }
    }

    @Override
    fun cerrar(perso: Personaje?, exito: String?) {
        if (_artesano != null) {
            _artesano.cerrarVentanaExchange(exito)
            _artesano.setInvitandoA(null, "")
            _artesano.setInvitador(null, "")
        }
        if (_cliente != null) {
            _cliente.cerrarVentanaExchange(exito)
            _cliente.setInvitandoA(null, "")
            _cliente.setInvitador(null, "")
        }
        interrumpirReceta()
        limpiarReceta()
    }

    @Synchronized
    fun botonOK(perso: Personaje) {
        if (_artesano.getID() === perso.getID()) {
            _ok1 = !_ok1
            GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_artesano, _ok1, perso.getID())
            GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_cliente, _ok1, perso.getID())
        } else if (_cliente.getID() === perso.getID()) {
            _ok2 = !_ok2
            GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_artesano, _ok2, perso.getID())
            GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_cliente, _ok2, perso.getID())
        } else {
            return
        }
        if (_ok1 && _ok2) {
            aplicar()
        }
    }

    fun desCheck() {
        _ok1 = false
        _ok2 = false
        GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_artesano, _ok1, _artesano.getID())
        GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_cliente, _ok1, _artesano.getID())
        GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_artesano, _ok2, _cliente.getID())
        GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_cliente, _ok2, _cliente.getID())
    }

    fun setKamas(tipoPago: Int, kamas: Long, kamasT: Long) {
        var kamas = kamas
        desCheck()
        if (kamas < 0) {
            return
        }
        if (tipoPago == 1) {
            if (kamasSiSeConsigue + kamas > kamasT) {
                kamas = kamasT - kamasSiSeConsigue
            }
            kamasPaga = kamas
        } else {
            if (kamasPaga + kamas > kamasT) {
                kamas = kamasT - kamasPaga
            }
            kamasSiSeConsigue = kamas
        }
        GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_artesano, tipoPago, "G", "+", kamas.toString() + "")
        GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_cliente, tipoPago, "G", "+", kamas.toString() + "")
    }

    @Synchronized
    fun aplicar() {
        try {
            val resultado = iniciarTaller(_objArtesano, _objCliente)
            val oficio: StatOficio = _artesano.getStatOficioPorTrabajo(trabajoID)
            if (_cliente.getKamas() < kamasSiSeConsigue + kamasPaga) {
                kamasPaga = _cliente.getKamas()
                kamasSiSeConsigue = 0
            }
            if (oficio != null) {
                if (resultado) {
                    _cliente.addKamas(-kamasSiSeConsigue, true, true)
                    _artesano.addKamas(kamasSiSeConsigue, true, true)
                    for (duo in _objetosSiSeConsegui) {
                        try {
                            val cant: Long = duo._segundo
                            if (cant == 0L) {
                                continue
                            }
                            val obj: Objeto = _cliente.getObjeto(duo._primero)
                            if (obj.getCantidad() - cant < 1) {
                                _cliente.borrarOEliminarConOR(duo._primero, false)
                                _artesano.addObjIdentAInventario(obj, true)
                            } else {
                                val nuevoOjb: Objeto = obj.clonarObjeto(cant, Constantes.OBJETO_POS_NO_EQUIPADO)
                                _artesano.addObjIdentAInventario(nuevoOjb, false)
                                obj.setCantidad(obj.getCantidad() - cant)
                                GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_cliente, obj)
                            }
                        } catch (e: Exception) {
                        }
                    }
                }
                if (!oficio.esGratisSiFalla() || resultado) {
                    _cliente.addKamas(-kamasPaga, true, true)
                    _artesano.addKamas(kamasPaga, true, true)
                    for (duo in _objetosPago) {
                        try {
                            val cant: Long = duo._segundo
                            if (cant == 0L) {
                                continue
                            }
                            val obj: Objeto = _cliente.getObjeto(duo._primero)
                            if (obj.getCantidad() - cant < 1) {
                                _cliente.borrarOEliminarConOR(duo._primero, false)
                                _artesano.addObjIdentAInventario(obj, true)
                            } else {
                                obj.setCantidad(obj.getCantidad() - cant)
                                GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_cliente, obj)
                                val nuevoOjb: Objeto = obj.clonarObjeto(cant, Constantes.OBJETO_POS_NO_EQUIPADO)
                                _artesano.addObjIdentAInventario(nuevoOjb, false)
                            }
                        } catch (e: Exception) {
                        }
                    }
                }
            }
            _objetosSiSeConsegui.clear()
            _objetosPago.clear()
            _objArtesano.clear()
            _objCliente.clear()
            kamasPaga = 0
            kamasSiSeConsigue = 0
            GestorSalida.ENVIAR_Ec_RESULTADO_RECETA(_artesano, _artesano.getForjaEc())
            GestorSalida.ENVIAR_Ec_RESULTADO_RECETA(_cliente, _cliente.getForjaEc())
            _artesano.setForjaEc("")
            _cliente.setForjaEc("")
        } catch (e: Exception) {
            cerrar(null, "")
        }
    }

    fun cantSlotsctual(): Int {
        return _objArtesano.size() + _objCliente.size()
    }

    @Override
    @Synchronized
    fun addObjetoExchanger(objeto: Objeto, cantidad: Long, perso: Personaje, precio: Int) {
        if (!perso.tieneObjetoID(objeto.getID()) || objeto.getPosicion() !== Constantes.OBJETO_POS_NO_EQUIPADO) {
            GestorSalida.ENVIAR_Im_INFORMACION(perso, "1OBJECT_DONT_EXIST")
            return
        }
        if (esTaller()) {
            addObjetoExchangerTaller(objeto, cantidad, perso, precio)
        } else {
            addObjetoExchangerCraft(objeto, cantidad, perso, precio)
        }
    }

    @Override
    @Synchronized
    fun remObjetoExchanger(objeto: Objeto, cantidad: Long, perso: Personaje, precio: Int) {
        if (esTaller()) {
            addObjetoExchangerTaller(objeto, -cantidad, perso, precio)
        } else {
            addObjetoExchangerCraft(objeto, -cantidad, perso, precio)
        }
    }

    @Synchronized
    fun addObjetoExchangerCraft(objeto: Objeto, cantidad: Long, perso: Personaje?, precio: Int) {
        var cantidad = cantidad
        if (cantidad > 0) {
            //
            if (objeto.getObjModeloID() === 7508) {
                // runa de firma
            } else if (!objeto.getObjModelo().esForjaMagueable() && _esForjaMagia || !Constantes
                            .getTipoObjPermitidoEnTrabajo(trabajoID, objeto.getObjModelo().getTipo())) {
                return
            }
            if (Constantes.getStatPorRunaPocima(objeto) <= 0 && Constantes.esSkillMago(trabajoID)) {
                val coef: Float = MainServidor.NIVEL_MAX_PERSONAJE / MainServidor.NIVEL_MAX_OFICIO
                if (objeto.getObjModelo().getNivel() > _statOficio.getNivel() * coef) {
                    GestorSalida.ENVIAR_M1_MENSAJE_SERVER_SVR_MUESTRA_INSTANTANEO(perso, 22, objeto.getObjModelo().getNivel()
                            .toString() + ";" + (objeto.getObjModelo().getNivel() / coef) as Int, "")
                    return
                }
            }
        }
        val cantInter = if (_ingredientes!![objeto.getID()] == null) 0 else _ingredientes!![objeto.getID()]!!
        if (cantidad + cantInter > objeto.getCantidad()) {
            cantidad = objeto.getCantidad() - cantInter
        } else if (cantidad + cantInter < 0) {
            cantidad = -cantInter
        }
        if (cantidad == 0L) {
            return
        }
        _ingredientes.remove(objeto.getID())
        val nuevaCant = cantidad + cantInter
        if (nuevaCant > 0) {
            _ingredientes.put(objeto.getID(), nuevaCant)
            GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(perso, 'O', "+", objeto.getID().toString() + "|" + nuevaCant)
        } else {
            GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(perso, 'O', "-", objeto.getID().toString() + "")
        }
    }

    @Synchronized
    fun addObjetoExchangerTaller(objeto: Objeto, cantidad: Long, perso: Personaje, precio: Int) {
        var cantidad = cantidad
        val objetoID: Int = objeto.getID()
        val artesano: Personaje = if (_artesano.getID() === perso.getID()) _artesano else _cliente
        val cliente: Personaje = if (_artesano.getID() === perso.getID()) _cliente else _artesano
        val objMovedor: ArrayList<Duo<Integer?, Long?>?> = if (_artesano.getID() === perso.getID()) _objArtesano else _objCliente
        val objDelOtro: ArrayList<Duo<Integer?, Long?>?> = if (_artesano.getID() === perso.getID()) _objCliente else _objArtesano
        val duoMovedor: Duo<Integer, Long> = Mundo.getDuoPorIDPrimero1(objMovedor, objetoID)
        val duoOtro: Duo<Integer, Long> = Mundo.getDuoPorIDPrimero1(objDelOtro, objetoID)
        val cantInter: Long = if (duoMovedor == null) 0 else duoMovedor._segundo
        if (cantidad + cantInter > objeto.getCantidad()) {
            cantidad = objeto.getCantidad() - cantInter
        } else if (cantidad + cantInter < 0) {
            cantidad = -cantInter
        }
        if (cantidad == 0L) {
            return
        }
        if (cantidad > 0) {
            if (objeto.getObjModeloID() === 7508) {
                // runa de firma
            } else if (!objeto.getObjModelo().esForjaMagueable() && _esForjaMagia || !Constantes
                            .getTipoObjPermitidoEnTrabajo(trabajoID, objeto.getObjModelo().getTipo())) {
                return
            }
            if (_esForjaMagia) {
                if (Constantes.getStatPorRunaPocima(objeto) <= 0 && Constantes.esSkillMago(trabajoID)) {
                    val coef: Float = MainServidor.NIVEL_MAX_PERSONAJE / MainServidor.NIVEL_MAX_OFICIO
                    if (objeto.getObjModelo().getNivel() > _statOficio.getNivel() * coef) {
                        GestorSalida.ENVIAR_M1_MENSAJE_SERVER_SVR_MUESTRA_INSTANTANEO(artesano, 22, objeto.getObjModelo().getNivel()
                                .toString() + ";" + (objeto.getObjModelo().getNivel() / coef) as Int, "")
                        GestorSalida.ENVIAR_M1_MENSAJE_SERVER_SVR_MUESTRA_INSTANTANEO(cliente, 22, objeto.getObjModelo().getNivel()
                                .toString() + ";" + (objeto.getObjModelo().getNivel() / coef) as Int, "")
                        return
                    }
                }
            }
            if (duoMovedor == null && duoOtro == null && cantSlotsctual() >= casillasMax) {
                // si el item es nuevo y ya llego al limite de slots
                return
            }
        }
        desCheck()
        val str = "$objetoID|$cantidad"
        val add = "|" + objeto.getObjModeloID().toString() + "|" + objeto.convertirStatsAString(false)
        if (duoMovedor != null) {
            duoMovedor._segundo += cantidad
            if (duoMovedor._segundo > 0) {
                GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(artesano, 'O', "+", "" + objetoID + "|" + duoMovedor._segundo)
                GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(cliente, 'O', "+", "" + objetoID + "|" + duoMovedor._segundo
                        + add)
            } else {
                objMovedor.remove(duoMovedor)
                GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(artesano, 'O', "-", objeto.getID().toString() + "")
                GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(cliente, 'O', "-", objeto.getID().toString() + "")
            }
        } else {
            GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(artesano, 'O', "+", str)
            GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(cliente, 'O', "+", str + add)
            objMovedor.add(Duo<Integer, Long>(objetoID, cantidad))
        }
    }

    @Synchronized
    fun addObjetoPaga(obj: Objeto, cant: Long, pagoID: Int) {
        var cant = cant
        desCheck()
        if (cant == 1L) {
            cant = 1
        }
        val idObj: Int = obj.getID()
        val str = "$idObj|$cant"
        val add = "|" + obj.getObjModeloID().toString() + "|" + obj.convertirStatsAString(false)
        if (pagoID == 1) {
            val duo: Duo<Integer, Long> = Mundo.getDuoPorIDPrimero1(_objetosPago, idObj)
            if (duo != null) {
                duo._segundo += cant
                GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_artesano, pagoID, "O", "+", idObj.toString() + "|" + duo._segundo
                        + add)
                GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_cliente, pagoID, "O", "+", idObj.toString() + "|" + duo._segundo)
            } else {
                GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_artesano, pagoID, "O", "+", str + add)
                GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_cliente, pagoID, "O", "+", str)
                _objetosPago.add(Duo<Integer, Long>(idObj, cant))
            }
        } else {
            val duo: Duo<Integer, Long> = Mundo.getDuoPorIDPrimero1(_objetosSiSeConsegui, idObj)
            if (duo != null) {
                duo._segundo += cant
                GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_artesano, pagoID, "O", "+", idObj.toString() + "|" + duo._segundo
                        + add)
                GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_cliente, pagoID, "O", "+", idObj.toString() + "|" + duo._segundo)
            } else {
                GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_artesano, pagoID, "O", "+", str + add)
                GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_cliente, pagoID, "O", "+", str)
                _objetosSiSeConsegui.add(Duo<Integer, Long>(idObj, cant))
            }
        }
    }

    @Synchronized
    fun quitarObjetoPaga(obj: Objeto, cant: Long, idPago: Int) {
        desCheck()
        val idObj: Int = obj.getID()
        if (idPago == 1) {
            val duo: Duo<Integer, Long> = Mundo.getDuoPorIDPrimero1(_objetosPago, idObj) ?: return
            GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_artesano, idPago, "O", "-", idObj.toString() + "")
            GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_cliente, idPago, "O", "-", idObj.toString() + "")
            val nuevaCantidad: Long = duo._segundo - cant
            if (nuevaCantidad <= 0) {
                _objetosPago.remove(duo)
            } else {
                duo._segundo = nuevaCantidad
                GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_artesano, idPago, "O", "+", idObj.toString() + "|" + nuevaCantidad + "|"
                        + obj.getObjModeloID() + "|" + obj.convertirStatsAString(false))
                GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_cliente, idPago, "O", "+", "$idObj|$nuevaCantidad")
            }
        } else {
            val duo: Duo<Integer, Long> = Mundo.getDuoPorIDPrimero1(_objetosSiSeConsegui, idObj) ?: return
            GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_artesano, idPago, "O", "-", idObj.toString() + "")
            GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_cliente, idPago, "O", "-", idObj.toString() + "")
            val nuevaCantidad: Long = duo._segundo - cant
            if (nuevaCantidad <= 0) {
                _objetosSiSeConsegui.remove(duo)
            } else {
                duo._segundo = nuevaCantidad
                GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_artesano, idPago, "O", "+", idObj.toString() + "|" + nuevaCantidad + "|"
                        + obj.getObjModeloID() + "|" + obj.convertirStatsAString(false))
                GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_cliente, idPago, "O", "+", "$idObj|$nuevaCantidad")
            }
        }
    }

    @Synchronized
    fun getCantObjetoPago(idObj: Int, tipoPago: Int): Long {
        val objetos: ArrayList<Duo<Integer, Long>>
        objetos = if (tipoPago == 1) {
            _objetosPago
        } else {
            _objetosSiSeConsegui
        }
        for (duo in objetos) {
            if (duo._primero === idObj) {
                return duo._segundo
            }
        }
        return 0
    }

    @Override
    fun addKamas(kamas: Long, perso: Personaje?) {
    }

    @get:Override
    val kamas: Long
        get() = 0

    @Override
    fun getListaExchanger(perso: Personaje?): String? {
        // TODO Auto-generated method stub
        return null
    }

    companion object {
        const val MENSAJE_SIN_RESULTADO: Byte = 0
        const val MENSAJE_OBJETO_FABRICADO: Byte = 1
        const val MENSAJE_INTERRUMPIDA: Byte = 2
        const val MENSAJE_FALTA_RECURSOS: Byte = 3
        const val MENSAJE_RECETA_NO_FUNCIONA: Byte = 4
        const val MAGUEO_EXO: Byte = 2
        const val MAGUEO_OVER: Byte = 1
        const val MAGUEO_NORMAL: Byte = 0
        const val RESULTADO_EXITO_CRITICO: Byte = 1
        const val RESULTADO_EXITO_NORMAL: Byte = 2
        const val RESULTADO_FALLO_NORMAL: Byte = 3
        const val RESULTADO_FALLO_CRITICO: Byte = 4
    }

    init {
        casillasMax = max
        casillasMin = min
        _statOficio = oficio
        if (esCraft.also { _esCraft = it }) {
            suerte = nSuerteTiempo
            _ingredientes = TreeMap()
            _ultimosIngredientes = TreeMap()
        } else {
            tiempo = nSuerteTiempo
            _xpGanadaRecoleccion = xpGanada
        }
        _esForjaMagia = Constantes.esOficioMago(_statOficio.getOficio().getID())
    }
}