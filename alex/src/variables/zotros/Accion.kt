package variables.zotros

import java.awt.event.ActionEvent

class Accion(//private static final int ACCION_BORRAR_SUBCLASE = 912;
        private val _id: Int, val _args: String?, condicion: String?) {
    private var _condicion: String? = ""
    fun getID(): Int {
        return _id
    }

    fun getArgs(): String? {
        return _args
    }

    fun getCondicion(): String? {
        return _condicion
    }

    fun setCondicion(condicion: String?) {
        _condicion = condicion
    }

    fun realizarAccion(perso: Personaje?, objetivo: Personaje?, idObjUsar: Int, celda: Short): Boolean {
        return realizar_Accion_Estatico(_id, _args, perso, objetivo, idObjUsar, celda)
    }

    companion object {
        private const val ACCION_CREAR_GREMIO = -2
        private const val ACCION_ABRIR_BANCO = -1
        private const val ACCION_TELEPORT_MAPA = 0
        private const val ACCION_DIALOGO = 1
        private const val ACCION_AGREGAR_OBJETO_AZAR = 2
        private const val LUPA_PERFECION = 3
        private const val ACCION_DAR_QUITAR_KAMAS = 4
        private const val ACCION_DAR_QUITAR_OBJETOS = 5
        private const val ACCION_APRENDER_OFICIO = 6
        private const val ACCION_RETORNAR_PUNTO_SALVADA = 7
        private const val ACCION_BOOST_STATS = 8
        private const val ACCION_APRENDER_HECHIZO = 9
        private const val ACCION_CURAR = 10
        private const val ACCION_CAMBIAR_ALINEACION = 11
        private const val ACCION_CREAR_GRUPO_MOB_CON_PIEDRA = 12
        private const val ACCION_RESETEAR_STATS = 13
        private const val ACCION_OLVIDAR_HECHIZO_PANEL = 14
        private const val ACCION_USAR_LLAVE = 15
        private const val ACCION_DAR_QUITAR_HONOR = 16
        private const val ACCION_EXP_OFICIO = 17
        private const val ACCION_TELEPORT_CASA = 18
        private const val ACCION_PANEL_CASA_GREMIO = 19
        private const val ACCION_DAR_QUITAR_PUNTOS_HECHIZO = 20
        private const val ACCION_DAR_QUITAR_ENERGIA = 21
        private const val ACCION_DAR_EXPERIENCIA = 22
        private const val ACCION_OLVIDAR_OFICIO = 23
        private const val ACCION_CAMBIAR_GFX = 24
        private const val ACCION_DEFORMAR = 25
        private const val ACCION_PANEL_CERCADO_GREMIO = 26
        private const val ACCION_INICIAR_PELEA_VS_MOBS = 27
        private const val ACCION_SUBIR_BAJAR_MONTURA = 28
        private const val ACCION_INCIAR_PELEA_VS_MOBS_NO_ESPADA = 29
        private const val ACCION_REFRESCAR_MOBS = 30
        private const val ACCION_CAMBIAR_CLASE = 31
        private const val ACCION_AUMENTAR_RESETS = 32
        private const val ACCION_OBJETO_BOOST = 33
        private const val ACCION_CAMBIAR_SEXO = 34
        private const val ACCION_PAGAR_PESCAR_KUAKUA = 35
        private const val ACCION_RULETA_JALATO = 36
        private const val ACCION_DAR_ORNAMENTO = 37
        private const val ACCION_TELEPORT_CELDA_MISMO_MAPA = 38
        private const val ACCION_GANAR_RULETA_JALATO = 39
        private const val ACCION_KAMAS_RULETA_JALATO = 40
        private const val ACCION_INICIAR_PELEA_DOPEUL = 41
        private const val ACCION_DAR_SET_OBJETOS = 42
        private const val ACCION_CONFIRMAR_CUMPLIO_OBJETIVO_MISION = 43
        private const val ACCION_DAR_MISION = 44
        private const val ACCION_AGREGAR_MOB_ALBUM = 45
        private const val ACCION_DAR_TITULO = 46
        private const val ACCION_RECOMPENSA_DOPEUL = 47
        private const val ACCION_VERIFICA_MISION_ALMANAX = 48
        private const val ACCION_DAR_MISION_PVP_CON_PERGAMINOS = 49
        private const val ACCION_DAR_MISION_PVP = 50
        private const val ACCION_GEOPOSICION_MISION_PVP = 51
        private const val ACCION_TELEPORT_MISION_PVP = 52
        private const val ACCION_CONFIRMA_CUMPLIO_MISION = 53
        private const val ACCION_BOOST_FULL_STATS = 54
        private const val ACCION_PAGAR_PARA_REALIZAR_ACCION = 55
        private const val ACCION_SOLICITAR_OBJETOS_PARA_DAR_OTROS = 56
        private const val ACCION_REVIVIR = 57
        private const val ACCION_ABRIR_DOCUMENTO = 58
        private const val ACCION_DAR_SET_OBJETOS_POR_FICHAS = 59
        private const val ACCION_REALIZAR_ACCION_PJS_EN_MAPA_POR_ALINEACION_Y_DISTANCIA = 60
        private const val ACCION_LIBERAR_TUMBA = 61
        private const val ACCION_REVIVIR2 = 62
        private const val ACCION_AGREGAR_PJ_LIBRO_ARTESANOS = 63
        private const val ACCION_ACTIVAR_CELDAS_INTERACTIVAS = 64
        private const val ACCION_DAR_QUITAR_EMOTE = 65
        private const val ACCION_SOLICITAR_OBJETOS_PARA_REALIZAR_ACCION = 66
        private const val ACCION_CAMBIAR_ROSTRO = 67
        private const val ACCION_MENSAJE_INFORMACION = 68
        private const val ACCION_MENSAJE_PANEL = 69
        private const val ACCION_DAR_OBJETOS_DE_LOS_STATS = 70
        private const val ACCION_DAR_ABONO_DIAS = 71
        private const val ACCION_DAR_ABONO_HORAS = 72
        private const val ACCION_DAR_ABONO_MINUTOS = 73
        private const val ACCION_DAR_NIVEL_DE_ORDEN = 74
        private const val ACCION_DAR_ORDEN = 75
        private const val ACCION_BORRAR_OBJETO_MODELO = 76
        private const val ACCION_VERIFICA_STAT_OBJETO_Y_LO_BORRA = 77
        private const val ACCION_BORRAR_OBJETO_AL_AZAR_PARA_DAR_OTROS = 78
        private const val ACCION_GDF_PERSONA = 79
        private const val ACCION_GDF_MAPA = 80
        private const val ACCION_RULETA_PREMIOS = 81
        private const val ACCION_TIEMPO_PROTECCION_RECAUDADOR = 82
        private const val ACCION_TIEMPO_PROTECCION_PRISMA = 83
        private const val ACCION_OLVIDAR_HECHIZO_RECAUDADOR = 84
        private const val ACCION_ENVIAR_PACKET = 99
        private const val ACCION_DAR_HABILIDAD_MONTURA = 100
        private const val ACCION_CASAR_DOS_PJS = 101
        private const val ACCION_DISCURSO_SACEDORTE = 102
        private const val ACCION_DIVORCIARSE = 103
        private const val ACCION_DAR_OGRINAS = 104
        private const val ACCION_DAR_CREDITOS = 105
        private const val ACCION_AGREGAR_OBJETO_A_CERCADO = 200
        private const val ACCION_AGREGAR_PRISMA_A_MAPA = 201
        private const val ACCION_LANZAR_ANIMACION = 227
        private const val ACCION_LANZAR_ANIMACION2 = 228
        private const val ACCION_AGREGAR_STATS_MEJORADO = 106
        private const val ACCION_REFRESCAR_PJ = 107
        private const val ACCION_VERIFICA_STAT_OBJETO_Y_LO_BORRA_EJECUTA_ACCION = 85
        private const val ACCION_EVOLUCIONA_OBJETOS = 86
        private const val ACCION_INTERCAMBIA_OBJETOS_FORTIFICADOS = 87
        private const val ACCION_REINCIAR_RESET = 88
        private const val ACCION_REVOLVER_ITEM = 89
        private const val ACCION_ENVIAR_PACKET_SIN = 90
        private const val ACCION_BORRAR_FUSION_MONTURA = 91
        private const val ACCION_REVOLVER_DOFUS = 92
        private const val ACCION_QUITAR_EVOLUCION = 93
        private const val ACCION_ENVIAR_PACKET_OBJETO = 94
        private const val ACCION_ENVIAR_PACKET_ORNAMENTO = 95
        private const val ACCION_ENVIAR_PACKET_REVOLVER = 96
        private const val ACCION_TELEPORT_MAPA_SALVADO = 97
        fun realizar_Accion_Estatico(_id: Int, _args: String?, perso: Personaje?,
                                     objetivo: Personaje?, idObjUsar: Int, celdaID: Short): Boolean {
            var objetivo: Personaje? = objetivo
            var celdaID = celdaID
            return try {
                if (objetivo == null) {
                    objetivo = perso
                }
                if (perso.estaExchange()) {
                    GestorSalida.ENVIAR_BN_NADA(perso)
                    return false
                }
                if (celdaID.toInt() == -1) {
                    celdaID = perso.getCelda().getID()
                }
                // if (!Condicion.validaCondiciones(perso, condicion)) {
                // return false;
                // }
                val objUsar: Objeto = Mundo.getObjeto(idObjUsar)
                when (_id) {
                    ACCION_CREAR_GREMIO -> try {
                        if (!perso.estaDisponible(false, false)) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo)
                            return false
                        }
                        if (perso.getGremio() != null || perso.getMiembroGremio() != null) {
                            GestorSalida.ENVIAR_gC_CREAR_PANEL_GREMIO(perso, "Ea")
                            return false
                        }
                        // perso.addObjIdentAInventario(Mundo.getObjetoModelo(1575).crearObjDesdeModelo(1,
                        // Constantes.OBJETO_POS_NO_EQUIPADO, 0), false);
                        perso.setOcupado(true)
                        GestorSalida.ENVIAR_gn_CREAR_GREMIO(perso)
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_ABRIR_BANCO -> try {
                        if (perso.getDeshonor() >= 1) {
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "183")
                            return false
                        }
                        if (!perso.estaDisponible(false, false)) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo)
                            return false
                        }
                        val costo: Int = perso.getCostoAbrirBanco()
                        if (perso.getKamas() - costo < 0) {
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "1128;$costo")
                            GestorSalida.ENVIAR_M1_MENSAJE_SERVER_SVR_MUESTRA_INSTANTANEO(perso, 10, costo.toString() + "", "")
                        } else {
                            perso.addKamas(-costo, false, true)
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "020;$costo")
                            perso.getBanco().abrirCofre(perso)
                        }
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_TELEPORT_MAPA_SALVADO -> try {
                        if (perso.getHaciendoTrabajo() != null || !perso.usaTP) return false
                        var nuevoMapaID: Short = Short.parseShort(_args.split(",", 2).get(0))
                        var nuevaCeldaID: Short = Short.parseShort(_args.split(",", 2).get(1))
                        val InfoMap: Int = perso.getMushinPiso()
                        if (InfoMap != 0) {
                            nuevoMapaID = InfoMap.toShort()
                            nuevaCeldaID = Mundo.getMapa(perso.getMushinPiso()).getRandomCeldaIDLibre()
                        }
                        perso.teleportPelea(nuevoMapaID, nuevaCeldaID)
                    } catch (e: Exception) {
                        return false
                    }
                    ACCION_TELEPORT_MAPA -> try {
                        if (perso.getHaciendoTrabajo() != null || !perso.usaTP) return false
                        val anteriormapa: Int = perso.getMapa().getID()
                        val nuevoMapaID: Short = Short.parseShort(_args.split(",", 2).get(0))
                        val nuevaCeldaID: Short = Short.parseShort(_args.split(",", 2).get(1))
                        perso.teleportPelea(nuevoMapaID, nuevaCeldaID)
                        if (perso.liderMaitre) {
                            val timer = Timer(500, object : ActionListener() {
                                @Override
                                fun actionPerformed(e: ActionEvent?) {
                                    (e.getSource() as Timer).stop()
                                    for (pjx in perso.getGrupoParty().getPersos()) {
                                        if (pjx === perso) continue
                                        if (pjx.getMapa().getID() !== anteriormapa) {
                                            continue
                                        }
                                        pjx.teleport(nuevoMapaID, nuevaCeldaID)
                                    }
                                }
                            })
                            timer.start()
                        }
                    } catch (e: Exception) {
                        return false
                    }
                    ACCION_DIALOGO -> try {
                        if (_args!!.equals("DV")) {
                            perso.dialogoFin()
                        } else {
                            var preguntaID = 0
                            try {
                                preguntaID = Integer.parseInt(_args)
                            } catch (e: Exception) {
                            }
                            if (preguntaID <= 0) {
                                perso.dialogoFin()
                            }
                            var pregunta: PreguntaNPC? = Mundo.getPreguntaNPC(preguntaID)
                            if (pregunta == null) {
                                pregunta = PreguntaNPC(preguntaID, "", "", "")
                                Mundo.addPreguntaNPC(pregunta)
                            }
                            GestorSalida.ENVIAR_DQ_DIALOGO_PREGUNTA(perso, pregunta.stringArgParaDialogo(perso, perso))
                        }
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_AGREGAR_OBJETO_AZAR -> {
                        // no borra el objeto q se usa
                        try {
                            val quitar: String = _args.split(Pattern.quote("|")).get(0)
                            val azar: Array<String?> = _args.split(Pattern.quote("|")).get(1).split(";")
                            val idQuitar: Int = Integer.parseInt(quitar.split(",").get(0))
                            val cantQuitar: Int = Math.abs(Integer.parseInt(quitar.split(",").get(1)))
                            if (perso.tenerYEliminarObjPorModYCant(idQuitar, cantQuitar)) {
                                val objetoAzar = azar[Formulas.getRandomInt(0, azar.size - 1)]
                                val idDar: Int = Integer.parseInt(objetoAzar.split(",").get(0))
                                val cantDar: Int = Math.abs(Integer.parseInt(objetoAzar.split(",").get(1)))
                                perso.addObjIdentAInventario(Mundo.getObjetoModelo(idDar).crearObjeto(cantDar,
                                        Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM), false)
                                GestorSalida.ENVIAR_Im_INFORMACION(perso, "022;$cantQuitar~$idQuitar")
                                GestorSalida.ENVIAR_Im_INFORMACION(perso, "021;$cantDar~$idDar")
                                GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso)
                            } else {
                                GestorSalida.ENVIAR_Im_INFORMACION(perso, "14|43")
                            }
                        } catch (e: Exception) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        }
                        return false // no borra
                    }
                    LUPA_PERFECION -> {
                        val posAMover: Int = Integer.parseInt(_args)
                        val objetoPos: Objeto = perso.getObjPosicion(posAMover.toByte())
                        if (objetoPos != null) {
                            if (objetoPos.tieneStatTexto(Constantes.STAT_OBJETO_OCULTO_1)) {
                                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso, "No se puede lupear un objeto fusionado.")
                                return false
                            }
                            if (MainServidor.OBJETOS_NO_LUPEAR.contains(objetoPos.getObjModeloID())) {
                                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso, "No se puede lupear este objeto.")
                                return false
                            }
                            objetoPos.convertirStringAStats(objetoPos.getObjModelo().generarStatsModelo(CAPACIDAD_STATS.MAXIMO))
                            GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(perso, objetoPos)
                            GestorSQL.SALVAR_OBJETO(objetoPos)
                            GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso)
                        } else {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso, "No se encontr� un objeto en dicha posici�n.")
                            return false
                        }
                        return true
                    }
                    ACCION_DAR_QUITAR_KAMAS -> try {
                        var kamas: Long = 0
                        val s: Array<String?> = _args.split(",")
                        kamas = if (s.size == 1) {
                            Integer.parseInt(s[0])
                        } else {
                            Formulas.getRandomInt(Integer.parseInt(s[0]), Integer.parseInt(s[1]))
                        }
                        perso.addKamas(kamas, true, true)
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_DAR_QUITAR_OBJETOS -> try {
                        var b = false
                        for (s in _args.split(";")) {
                            val ss: Array<String?> = s.split(",")
                            val id: Int = Integer.parseInt(ss[0])
                            var cant = 1 // corregir los otros
                            if (ss.size > 1) {
                                cant = Integer.parseInt(ss[1])
                            }
                            if (celdaID < 0) {
                                cant *= -1 * celdaID
                            }
                            val tempObjMod: ObjetoModelo = Mundo.getObjetoModelo(id)
                            if (tempObjMod == null) {
                                GestorSalida.ENVIAR_BN_NADA(objetivo, "BUG ACCION $_id idObjMod $id")
                            } else {
                                if (cant > 0) {
                                    b = true
                                    perso.addObjIdentAInventario(tempObjMod.crearObjeto(cant, Constantes.OBJETO_POS_NO_EQUIPADO,
                                            CAPACIDAD_STATS.RANDOM), false)
                                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "021;$cant~$id")
                                } else if (cant < 0) {
                                    val borrados: Int = perso.restarObjPorModYCant(id, Math.abs(cant))
                                    if (borrados > 0) {
                                        b = true
                                        GestorSalida.ENVIAR_Im_INFORMACION(perso, "022;$borrados~$id")
                                    }
                                }
                            }
                        }
                        if (b) {
                            GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso)
                        } else {
                            return false
                        }
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_APRENDER_OFICIO -> try {
                        var b = false
                        for (s in _args.split(";")) {
                            val idOficio: Int = Integer.parseInt(s.split(",").get(0))
                            var siOSi = false
                            try {
                                siOSi = s.split(",").get(1).equals("1")
                            } catch (e: Exception) {
                            }
                            if (Mundo.getOficio(idOficio) == null) {
                                GestorSalida.ENVIAR_BN_NADA(objetivo)
                            } else {
                                if (siOSi) {
                                    b = true
                                    perso.aprenderOficio(Mundo.getOficio(idOficio), if (MainServidor.MODO_PVP) 1000000 else 0)
                                } else {
                                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "16")
                                }
                            }
                        }
                        if (!b) {
                            return false
                        }
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_RETORNAR_PUNTO_SALVADA -> try {
                        if (perso.getPelea() != null) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo)
                            return false
                        }
                        //perso.teleportPtoSalvada();
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_BOOST_STATS -> try {
                        var `as` = false
                        for (s in _args.split(";")) {
                            val statID: Int = Integer.parseInt(s.split(",").get(0))
                            val cantidad: Int = Integer.parseInt(s.split(",").get(1))
                            var mensajeID = 0
                            if (MainServidor.LIMITE_SCROLL_COMANDO > 0 && cantidad == MainServidor.LIMITE_SCROLL_COMANDO) {
                                val statsBase: Stats = perso.getTotalStats().getStatsBase()
                                if (statsBase.getStatParaMostrar(statID) > 0) {
                                    GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
                                            "No puede seguir bosteandote",
                                            "000000")
                                    return false
                                }
                            }
                            when (statID) {
                                Constantes.STAT_MAS_SABIDURIA -> {
                                    perso.addScrollStat(Constantes.STAT_MAS_SABIDURIA, cantidad)
                                    mensajeID = 9
                                }
                                Constantes.STAT_MAS_FUERZA -> {
                                    perso.addScrollStat(Constantes.STAT_MAS_FUERZA, cantidad)
                                    mensajeID = 10
                                }
                                Constantes.STAT_MAS_SUERTE -> {
                                    perso.addScrollStat(Constantes.STAT_MAS_SUERTE, cantidad)
                                    mensajeID = 11
                                }
                                Constantes.STAT_MAS_AGILIDAD -> {
                                    perso.addScrollStat(Constantes.STAT_MAS_AGILIDAD, cantidad)
                                    mensajeID = 12
                                }
                                Constantes.STAT_MAS_VITALIDAD -> {
                                    perso.addScrollStat(Constantes.STAT_MAS_VITALIDAD, cantidad)
                                    mensajeID = 13
                                }
                                Constantes.STAT_MAS_INTELIGENCIA -> {
                                    perso.addScrollStat(Constantes.STAT_MAS_INTELIGENCIA, cantidad)
                                    mensajeID = 14
                                }
                            }
                            if (mensajeID > 0) {
                                `as` = true
                                GestorSalida.ENVIAR_Im_INFORMACION(perso, "0$mensajeID;$cantidad")
                            }
                        }
                        if (`as`) {
                            GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso)
                        }
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_APRENDER_HECHIZO -> try {
                        for (s in _args.split(";")) {
                            val hechizoID: Int = Integer.parseInt(s)
                            if (Mundo.getHechizo(hechizoID) == null) {
                                GestorSalida.ENVIAR_BN_NADA(objetivo)
                                return false
                            }
                            if (!objetivo.tieneHechizoID(hechizoID)) {
                                objetivo.fijarNivelHechizoOAprender(hechizoID, 1, true)
                            } else {
                                GestorSalida.ENVIAR_Im_INFORMACION(objetivo, "17;$hechizoID")
                                return false
                            }
                        }
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_CURAR -> try {
                        if (objetivo.getPorcPDV() >= 100) {
                            GestorSalida.ENVIAR_BN_NADA(perso)
                            return false
                        }
                        try {
                            if (objUsar != null) {
                                val tipo: Short = objUsar.getObjModelo().getTipo()
                                when (tipo) {
                                    Constantes.OBJETO_TIPO_BEBIDA, Constantes.OBJETO_TIPO_POCION -> GestorSalida.ENVIAR_eUK_EMOTE_MAPA(objetivo.getMapa(), objetivo.getID(), 18, 0)
                                    Constantes.OBJETO_TIPO_PAN, Constantes.OBJETO_TIPO_CARNE_COMESTIBLE, Constantes.OBJETO_TIPO_PESCADO_COMESTIBLE -> GestorSalida.ENVIAR_eUK_EMOTE_MAPA(objetivo.getMapa(), objetivo.getID(), 17, 0)
                                }
                            }
                        } catch (e: Exception) {
                        }
                        var valor = 0
                        val s: Array<String?> = _args.split(",")
                        valor = if (s.size == 1) {
                            Integer.parseInt(s[0])
                        } else {
                            Formulas.getRandomInt(Integer.parseInt(s[0]), Integer.parseInt(s[1]))
                        }
                        objetivo.addPDV(valor)
                        GestorSalida.ENVIAR_Im_INFORMACION(perso, "01;$valor")
                        GestorSalida.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(objetivo)
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_CAMBIAR_ALINEACION -> {
                        return try {
                            val alineacion: Byte = Byte.parseByte(_args.split(",").get(0))
                            if (alineacion == perso.getAlineacion()) {
                                false
                            } else perso.cambiarAlineacion(alineacion, false)
                        } catch (e: Exception) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                            false
                        }
                        try {
                            // si o si es con el objeto a usar
                            // final boolean delObj = _args.split(",")[0].equals("true");
                            if (objUsar == null) {
                                GestorSalida.ENVIAR_Im_INFORMACION(perso, "14|43")
                                return false
                            }
                            val enArena: Boolean = _args.split(",").get(1).equalsIgnoreCase("true")
                            if (enArena && !perso.getMapa().esArena()) {
                                GestorSalida.ENVIAR_Im_INFORMACION(perso, "113")
                                return false
                            }
                            if (perso.getMapa().esMazmorra()) {
                                GestorSalida.ENVIAR_Im_INFORMACION(perso, "113")
                                return false
                            }
                            val condicion = "Mi=" + perso.getID()
                            val jefe: Int = objUsar.strGrupoMobJefe()
                            val hechizoJefe: Int = MainServidor.JEFE_HECHIZO.get(jefe)
                            val Idolos: String = MainServidor.JEFE_AYUDANTE.get(jefe)
                            val grupoMob: GrupoMob = perso.getMapa().addGrupoMobPorTipo(perso.getCelda().getID(), objUsar.strGrupoMob(),
                                    TipoGrupo.SOLO_UNA_PELEA, condicion, null, true, jefe, Idolos, hechizoJefe)
                            grupoMob.setCondUnirsePelea("CL=1,1&CL=2,1&CL=3,1&CL=4,1&CL=5,1&CL=6,1&CL=7,1&CL=8,1&CL=9,1&CL=10,1&CL=11,1&CL=12,1&CL=13,1&CL=14,1&CL=15,1&CL=16,1&CL=17,1&CL=18,1")
                            grupoMob.startTiempoCondicion()
                        } catch (e: Exception) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                            return false
                        }
                    }
                    ACCION_CREAR_GRUPO_MOB_CON_PIEDRA -> try {
                        if (objUsar == null) {
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "14|43")
                            return false
                        }
                        val enArena: Boolean = _args.split(",").get(1).equalsIgnoreCase("true")
                        if (enArena && !perso.getMapa().esArena()) {
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "113")
                            return false
                        }
                        if (perso.getMapa().esMazmorra()) {
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "113")
                            return false
                        }
                        val condicion = "Mi=" + perso.getID()
                        val jefe: Int = objUsar.strGrupoMobJefe()
                        val hechizoJefe: Int = MainServidor.JEFE_HECHIZO.get(jefe)
                        val Idolos: String = MainServidor.JEFE_AYUDANTE.get(jefe)
                        val grupoMob: GrupoMob = perso.getMapa().addGrupoMobPorTipo(perso.getCelda().getID(), objUsar.strGrupoMob(),
                                TipoGrupo.SOLO_UNA_PELEA, condicion, null, true, jefe, Idolos, hechizoJefe)
                        grupoMob.setCondUnirsePelea("CL=1,1&CL=2,1&CL=3,1&CL=4,1&CL=5,1&CL=6,1&CL=7,1&CL=8,1&CL=9,1&CL=10,1&CL=11,1&CL=12,1&CL=13,1&CL=14,1&CL=15,1&CL=16,1&CL=17,1&CL=18,1")
                        grupoMob.startTiempoCondicion()
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_INICIAR_PELEA_DOPEUL -> try {
                        val mobDopeul: Int = Integer.parseInt(_args)
                        val nivel: Int = Constantes.getNivelDopeul(perso.getNivel())
                        val grupoMob = GrupoMob(perso.getMapa(), perso.getCelda().getID(), mobDopeul.toString() + "," + nivel + ","
                                + nivel, TipoGrupo.SOLO_UNA_PELEA, "", true, 0.toByte(), 0, "", 0)
                        perso.getMapa().iniciarPelea(perso, null, perso.getCelda().getID(), -1.toShort(),
                                Constantes.PELEA_TIPO_PVM_NO_ESPADA, grupoMob, false)
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_INICIAR_PELEA_VS_MOBS, ACCION_INCIAR_PELEA_VS_MOBS_NO_ESPADA ->                    // iniciar pelea vs 1 mob no espada mobID,mobnivel|mobID,mobNivel|.....
                        try {
                            if (objUsar != null) {
                                if (perso.getMapa().esMazmorra()) {
                                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "113")
                                    return false
                                }
                            }
                            val mobGrupo = StringBuilder()
                            for (mobYNivel in _args.split(Pattern.quote(";"))) {
                                val mobONivel: Array<String?> = mobYNivel.split(",")
                                val mobID: Int = Integer.parseInt(mobONivel[0])
                                mobGrupo.append(mobID)
                                if (mobONivel.size > 1) {
                                    mobGrupo.append("," + Integer.parseInt(mobONivel[1]))
                                }
                                if (mobONivel.size > 2) {
                                    mobGrupo.append("," + Integer.parseInt(mobONivel[2]))
                                }
                                mobGrupo.append(";")
                            }
                            val grupoMob = GrupoMob(perso.getMapa(), (perso.getCelda().getID() + 1) as Short, mobGrupo
                                    .toString(), TipoGrupo.SOLO_UNA_PELEA, "", true, 0.toByte(), 0, "", 0)
                            if (grupoMob.getCantMobs() <= 0) {
                                return false
                            }
                            val tipoPelea: Byte = if (ACCION_INICIAR_PELEA_VS_MOBS == _id) Constantes.PELEA_TIPO_PVM else Constantes.PELEA_TIPO_PVM_NO_ESPADA
                            perso.getMapa().iniciarPelea(perso, null, perso.getCelda().getID(), -1.toShort(), tipoPelea, grupoMob, false)
                        } catch (e: Exception) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                            return false
                        }
                    ACCION_RESETEAR_STATS -> try {
                        var todo = false
                        try {
                            todo = _args.equalsIgnoreCase("true")
                        } catch (e: Exception) {
                        }
                        perso.resetearStats(todo)
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_OLVIDAR_HECHIZO_PANEL -> try {
                        try {
                            return perso.olvidarHechizo(Integer.parseInt(_args), false, true)
                        } catch (e: Exception) {
                            perso.setOlvidandoHechizo(true)
                            GestorSalida.ENVIAR_SF_OLVIDAR_HECHIZO('+', perso)
                        }
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_USAR_LLAVE -> try {
                        // tpmap,tpcelda,objnecesario,mapanecesario
                        val nuevoMapaID: Short = Short.parseShort(_args.split(",").get(0))
                        val nuevaCeldaID: Short = Short.parseShort(_args.split(",").get(1))
                        var objNecesario = 0
                        try {
                            objNecesario = Integer.parseInt(_args.split(",").get(2))
                        } catch (e: Exception) {
                        }
                        var mapaNecesario = 0
                        try {
                            mapaNecesario = Integer.parseInt(_args.split(",").get(3))
                        } catch (e: Exception) {
                        }
                        if (objNecesario == 0) {
                            perso.teleport(nuevoMapaID, nuevaCeldaID)
                        } else if (objNecesario > 0) {
                            if (mapaNecesario == 0) {
                                if (perso.tenerYEliminarObjPorModYCant(objNecesario, 1)) {
                                    GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso)
                                    perso.teleport(nuevoMapaID, nuevaCeldaID)
                                } else {
                                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "14|45")
                                }
                            } else if (mapaNecesario > 0) {
                                if (perso.getMapa().getID() === mapaNecesario) {
                                    if (perso.tenerYEliminarObjPorModYCant(objNecesario, 1)) {
                                        GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso)
                                        perso.teleport(nuevoMapaID, nuevaCeldaID)
                                    } else {
                                        GestorSalida.ENVIAR_Im_INFORMACION(perso, "14|45")
                                    }
                                } else if (perso.getMapa().getID() !== mapaNecesario) {
                                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "113")
                                }
                            }
                        }
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_DAR_QUITAR_HONOR -> try {
                        if (perso.getAlineacion() === Constantes.ALINEACION_NEUTRAL) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo)
                            return false
                        }
                        perso.addHonor(Integer.parseInt(_args))
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_EXP_OFICIO -> try {
                        val oficioID: Int = Integer.parseInt(_args.split(",").get(0))
                        val xp: Int = Integer.parseInt(_args.split(",").get(1))
                        if (perso.getStatOficioPorID(oficioID) == null) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo)
                            return false
                        }
                        GestorSalida.ENVIAR_Im_INFORMACION(perso, "017;$xp~$oficioID")
                        perso.getStatOficioPorID(oficioID).addExperiencia(perso, xp, 0)
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_TELEPORT_CASA -> try {
                        val casa: Casa = Mundo.getCasaDePj(perso.getID())
                        if (objUsar == null || casa == null) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo)
                            return false
                        }
                        if (casa.getActParametros()) {
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "126")
                            return false
                        }
                        perso.teleport(casa.getMapaIDDentro(), casa.getCeldaIDDentro())
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_PANEL_CASA_GREMIO -> GestorSalida.ENVIAR_gUT_PANEL_CASA_GREMIO(perso)
                    ACCION_DAR_QUITAR_PUNTOS_HECHIZO -> try {
                        perso.addPuntosHechizos(Integer.parseInt(_args))
                        GestorSalida.ENVIAR_Im_INFORMACION(perso, "016;" + Integer.parseInt(_args))
                        GestorSalida.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(perso)
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_DAR_QUITAR_ENERGIA -> try {
                        var valor = 0
                        val s: Array<String?> = _args.split(",")
                        valor = if (s.size == 1) {
                            Integer.parseInt(s[0])
                        } else {
                            Formulas.getRandomInt(Integer.parseInt(s[0]), Integer.parseInt(s[1]))
                        }
                        perso.addEnergiaConIm(valor, true)
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_DAR_EXPERIENCIA -> try {
                        var valor = 0
                        val s: Array<String?> = _args.split(",")
                        valor = if (s.size == 1) {
                            Integer.parseInt(s[0])
                        } else {
                            Formulas.getRandomInt(Integer.parseInt(s[0]), Integer.parseInt(s[1]))
                        }
                        perso.addExperiencia(valor, true)
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_OLVIDAR_OFICIO -> {
                        return try {
                            val oficio: Int = Integer.parseInt(_args)
                            if (oficio < 1) {
                                GestorSalida.ENVIAR_BN_NADA(objetivo)
                                return false
                            }
                            perso.olvidarOficio(oficio)
                        } catch (e: Exception) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                            false
                        }
                        try {
                            val gfxID: Short = Short.parseShort(_args)
                            if (gfxID <= 0) {
                                GestorSalida.ENVIAR_BN_NADA(objetivo)
                                return false
                            }
                            perso.setGfxID(gfxID, true)
                            perso.refrescarEnMapa()
                        } catch (e: Exception) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                            return false
                        }
                    }
                    ACCION_CAMBIAR_GFX -> try {
                        val gfxID: Short = Short.parseShort(_args)
                        if (gfxID <= 0) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo)
                            return false
                        }
                        perso.setGfxID(gfxID, true)
                        perso.refrescarEnMapa()
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_DEFORMAR -> try {
                        perso.setGfxID((perso.getClaseID(true) * 10 + perso.getSexo()) as Short, true)
                        perso.refrescarEnMapa()
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_PANEL_CERCADO_GREMIO -> GestorSalida.ENVIAR_gUF_PANEL_CERCADOS_GREMIO(perso)
                    ACCION_SUBIR_BAJAR_MONTURA -> {
                        perso.subirBajarMontura(false)
                        return false
                    }
                    ACCION_REFRESCAR_MOBS -> perso.getMapa().refrescarGrupoMobs()
                    ACCION_CAMBIAR_CLASE -> {
                        return try {
                            if (perso.getEncarnacionN() != null) {
                                GestorSalida.ENVIAR_BN_NADA(perso)
                                return false
                            }
                            perso.cambiarClase(Byte.parseByte(_args))
                        } catch (e: Exception) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                            false
                        }
                        return try {
                            var reiniciar = false
                            try {
                                reiniciar = _args.equalsIgnoreCase("true")
                            } catch (e: Exception) {
                            }
                            if (perso.getEncarnacionN() != null) {
                                GestorSalida.ENVIAR_BN_NADA(perso)
                                return false
                            }
                            if (!perso.estaDisponible(false, false)) {
                                GestorSalida.ENVIAR_BN_NADA(perso)
                                return false
                            }
                            perso.aumentarReset(reiniciar)
                        } catch (e: Exception) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                            false
                        }
                        return try {
                            if (perso.getEncarnacionN() != null) {
                                GestorSalida.ENVIAR_BN_NADA(perso)
                                return false
                            }
                            if (!perso.estaDisponible(false, false)) {
                                GestorSalida.ENVIAR_BN_NADA(perso)
                                return false
                            }
                            perso.reiniciarReset()
                        } catch (e: Exception) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                            false
                        }
                        try {
                            val args: Array<String?> = _args.split(",")
                            val nuevo: Objeto = Mundo.getObjetoModelo(Integer.parseInt(args[0])).crearObjeto(1,
                                    Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM)
                            if (nuevo.getPosicion() < 20 || nuevo.getPosicion() > 25) {
                                return false
                            }
                            if (args.size > 1) {
                                val stats: String = nuevo.convertirStatsAString(true)
                                nuevo.convertirStringAStats((if (stats.isEmpty()) "" else "$stats,") + args[1])
                            }
                            if (perso.getObjPosicion(nuevo.getPosicion()) != null) {
                                perso.borrarOEliminarConOR(perso.getObjPosicion(nuevo.getPosicion()).getID(), true)
                            }
                            perso.addObjetoConOAKO(nuevo, true)
                            GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso)
                            GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso)
                            if (!nuevo.getParamStatTexto(Constantes.STAT_PERSONAJE_SEGUIDOR, 3).isEmpty()) {
                                perso.refrescarEnMapa()
                            }
                        } catch (e: Exception) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                            return false
                        }
                    }
                    ACCION_AUMENTAR_RESETS -> {
                        return try {
                            var reiniciar = false
                            try {
                                reiniciar = _args.equalsIgnoreCase("true")
                            } catch (e: Exception) {
                            }
                            if (perso.getEncarnacionN() != null) {
                                GestorSalida.ENVIAR_BN_NADA(perso)
                                return false
                            }
                            if (!perso.estaDisponible(false, false)) {
                                GestorSalida.ENVIAR_BN_NADA(perso)
                                return false
                            }
                            perso.aumentarReset(reiniciar)
                        } catch (e: Exception) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                            false
                        }
                        return try {
                            if (perso.getEncarnacionN() != null) {
                                GestorSalida.ENVIAR_BN_NADA(perso)
                                return false
                            }
                            if (!perso.estaDisponible(false, false)) {
                                GestorSalida.ENVIAR_BN_NADA(perso)
                                return false
                            }
                            perso.reiniciarReset()
                        } catch (e: Exception) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                            false
                        }
                        try {
                            val args: Array<String?> = _args.split(",")
                            val nuevo: Objeto = Mundo.getObjetoModelo(Integer.parseInt(args[0])).crearObjeto(1,
                                    Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM)
                            if (nuevo.getPosicion() < 20 || nuevo.getPosicion() > 25) {
                                return false
                            }
                            if (args.size > 1) {
                                val stats: String = nuevo.convertirStatsAString(true)
                                nuevo.convertirStringAStats((if (stats.isEmpty()) "" else "$stats,") + args[1])
                            }
                            if (perso.getObjPosicion(nuevo.getPosicion()) != null) {
                                perso.borrarOEliminarConOR(perso.getObjPosicion(nuevo.getPosicion()).getID(), true)
                            }
                            perso.addObjetoConOAKO(nuevo, true)
                            GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso)
                            GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso)
                            if (!nuevo.getParamStatTexto(Constantes.STAT_PERSONAJE_SEGUIDOR, 3).isEmpty()) {
                                perso.refrescarEnMapa()
                            }
                        } catch (e: Exception) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                            return false
                        }
                    }
                    ACCION_REINCIAR_RESET -> {
                        return try {
                            if (perso.getEncarnacionN() != null) {
                                GestorSalida.ENVIAR_BN_NADA(perso)
                                return false
                            }
                            if (!perso.estaDisponible(false, false)) {
                                GestorSalida.ENVIAR_BN_NADA(perso)
                                return false
                            }
                            perso.reiniciarReset()
                        } catch (e: Exception) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                            false
                        }
                        try {
                            val args: Array<String?> = _args.split(",")
                            val nuevo: Objeto = Mundo.getObjetoModelo(Integer.parseInt(args[0])).crearObjeto(1,
                                    Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM)
                            if (nuevo.getPosicion() < 20 || nuevo.getPosicion() > 25) {
                                return false
                            }
                            if (args.size > 1) {
                                val stats: String = nuevo.convertirStatsAString(true)
                                nuevo.convertirStringAStats((if (stats.isEmpty()) "" else "$stats,") + args[1])
                            }
                            if (perso.getObjPosicion(nuevo.getPosicion()) != null) {
                                perso.borrarOEliminarConOR(perso.getObjPosicion(nuevo.getPosicion()).getID(), true)
                            }
                            perso.addObjetoConOAKO(nuevo, true)
                            GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso)
                            GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso)
                            if (!nuevo.getParamStatTexto(Constantes.STAT_PERSONAJE_SEGUIDOR, 3).isEmpty()) {
                                perso.refrescarEnMapa()
                            }
                        } catch (e: Exception) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                            return false
                        }
                    }
                    ACCION_OBJETO_BOOST -> try {
                        val args: Array<String?> = _args.split(",")
                        val nuevo: Objeto = Mundo.getObjetoModelo(Integer.parseInt(args[0])).crearObjeto(1,
                                Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM)
                        if (nuevo.getPosicion() < 20 || nuevo.getPosicion() > 25) {
                            return false
                        }
                        if (args.size > 1) {
                            val stats: String = nuevo.convertirStatsAString(true)
                            nuevo.convertirStringAStats((if (stats.isEmpty()) "" else "$stats,") + args[1])
                        }
                        if (perso.getObjPosicion(nuevo.getPosicion()) != null) {
                            perso.borrarOEliminarConOR(perso.getObjPosicion(nuevo.getPosicion()).getID(), true)
                        }
                        perso.addObjetoConOAKO(nuevo, true)
                        GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso)
                        GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso)
                        if (!nuevo.getParamStatTexto(Constantes.STAT_PERSONAJE_SEGUIDOR, 3).isEmpty()) {
                            perso.refrescarEnMapa()
                        }
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_CAMBIAR_SEXO -> try {
                        perso.cambiarSexo()
                        perso.deformar()
                        perso.refrescarEnMapa()
                        GestorSQL.CAMBIAR_SEXO_CLASE(perso)
                    } catch (e: Exception) {
                    }
                    ACCION_PAGAR_PESCAR_KUAKUA -> try {
                        val kamasApostar: Long = Integer.parseInt(_args)
                        val tempKamas: Long = perso.getKamas()
                        if (tempKamas < kamasApostar) {
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "1128;$kamasApostar")
                            return false
                        }
                        perso.addKamas(kamasApostar, true, true)
                        perso.setPescarKuakua(true)
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_RULETA_JALATO -> try {
                        val precio: Long = Integer.parseInt(_args.split(",").get(0))
                        var tutorial: Int = Integer.parseInt(_args.split(",").get(1))
                        if (tutorial == 30) {
                            val aleatorio: Int = Formulas.getRandomInt(1, 200)
                            if (aleatorio == 100) {
                                tutorial = 31
                            }
                        }
                        val tuto: Tutorial = Mundo.getTutorial(tutorial)
                        if (tuto == null || precio < 0) {
                            return false
                        }
                        if (perso.getKamas() < precio) {
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "182")
                            return false
                        }
                        perso.addKamas(-precio, true, true)
                        if (tuto.getInicio() != null) {
                            tuto.getInicio().realizarAccion(perso, null, -1, -1.toShort())
                        }
                        Thread.sleep(1500)
                        GestorSalida.ENVIAR_TC_CARGAR_TUTORIAL(perso, tutorial)
                        perso.setTutorial(tuto)
                        perso.setOcupado(true)
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_DAR_ORNAMENTO -> try {
                        perso.addOrnamento(Integer.parseInt(_args))
                        perso.setOrnamento(Integer.parseInt(_args))
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_TELEPORT_CELDA_MISMO_MAPA -> try {
                        val nuevaCelda: Short = Short.parseShort(_args)
                        val mapa: Mapa = perso.getMapa()
                        GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mapa, perso.getID())
                        perso.setCelda(mapa.getCelda(nuevaCelda))
                        GestorSalida.ENVIAR_GM_AGREGAR_PJ_A_TODOS(perso.getMapa(), perso)
                        if (mapa.getID() === 6863) {
                            GestorSalida.enviar(perso, "cMK|-1|For Inkas|P�satelo bien!!!|", true)
                        }
                    } catch (e: Exception) {
                    }
                    ACCION_GANAR_RULETA_JALATO -> try {
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(("El anutrofado ganador de la RULETA DEL JALATO es: "
                                + perso.getNombre()) + ", demosle un fuerte aplauso!!!", "Vous avez gagnez en jouant a la roulette du bouftou :  " + perso.getNombre().toString() + ", f�licitations !")
                        perso.addKamas(MainServidor.KAMAS_RULETA_JALATO, true, true)
                        MainServidor.KAMAS_RULETA_JALATO = 10000
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_KAMAS_RULETA_JALATO -> MainServidor.KAMAS_RULETA_JALATO += 1000
                    ACCION_DAR_SET_OBJETOS -> try {
                        for (s in _args.split(";")) {
                            val OS: ObjetoSet = Mundo.getObjetoSet(Integer.parseInt(s))
                            if (OS == null) {
                                GestorSalida.ENVIAR_BN_NADA(objetivo)
                                return false
                            }
                            for (objMod in OS.getObjetosModelos()) {
                                objetivo.addObjIdentAInventario(objMod.crearObjeto(1, Constantes.OBJETO_POS_NO_EQUIPADO,
                                        CAPACIDAD_STATS.RANDOM), false)
                            }
                        }
                        GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(objetivo)
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_CONFIRMAR_CUMPLIO_OBJETIVO_MISION ->                    // confirma si se cumplio el objetivo de la mision
                        try {
                            for (s in _args.split(";")) {
                                for (mision in perso.getMisiones()) {
                                    if (mision.estaCompletada()) {
                                        continue
                                    }
                                    for (entry in mision.getObjetivos().entrySet()) {
                                        if (entry.getValue() === Mision.ESTADO_COMPLETADO) {
                                            continue
                                        }
                                        val objMod: MisionObjetivoModelo = Mundo.getMisionObjetivoModelo(entry.getKey())
                                        if (objMod.getID() !== Integer.parseInt(s)) {
                                            continue
                                        }
                                        perso.confirmarObjetivo(mision, objMod, perso, null, false, 0)
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                            return false
                        }
                    ACCION_DAR_MISION -> try {
                        if (perso.tieneMision(Integer.parseInt(_args))) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo, "TIENE MISION")
                            return false
                        }
                        val misionMod: MisionModelo = Mundo.getMision(Integer.parseInt(_args))
                        if (misionMod.getEtapas().isEmpty()) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo, "ETAPAS VACIAS")
                            return false
                        }
                        if (Mundo.getEtapa(misionMod.getEtapas().get(0)).getObjetivosPorNivel(0).isEmpty()) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo, "OBJETIVOS VACIOS")
                            return false
                        }
                        perso.addNuevaMision(misionMod)
                        GestorSalida.ENVIAR_Im_INFORMACION(perso, "054;" + Integer.parseInt(_args))
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_AGREGAR_MOB_ALBUM -> try {
                        for (s in _args.split(";")) {
                            perso.addCardMob(Integer.parseInt(s))
                        }
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(perso, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_DAR_TITULO -> try {
                        val titulo: Byte = Byte.parseByte(_args)
                        if (titulo < 1) {
                            GestorSalida.ENVIAR_BN_NADA(perso)
                            return false
                        }
                        perso.addTitulo(titulo, -1)
                        perso.refrescarEnMapa()
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_RECOMPENSA_DOPEUL -> {
                        try {
                            var exp = 0
                            if (perso.getNivel() < 20) {
                                exp = 3000
                            } else if (perso.getNivel() <= 40) {
                                exp = 8000
                            } else if (perso.getNivel() <= 60) {
                                exp = 19000
                            } else if (perso.getNivel() <= 80) {
                                exp = 34000
                            } else if (perso.getNivel() <= 100) {
                                exp = 57000
                            } else if (perso.getNivel() <= 120) {
                                exp = 90000
                            } else if (perso.getNivel() <= 140) {
                                exp = 130000
                            } else if (perso.getNivel() <= 160) {
                                exp = 180000
                            } else if (perso.getNivel() <= 180) {
                                exp = 245000
                            } else if (perso.getNivel() <= 200) {
                                exp = 320000
                            }
                            perso.addExperiencia(exp, true)
                        } catch (e: Exception) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                            return false
                        }
                        return false
                    }
                    ACCION_VERIFICA_MISION_ALMANAX -> return perso.cumplirMisionAlmanax()
                    ACCION_DAR_MISION_PVP_CON_PERGAMINOS -> try {
                        var cantidad = 1
                        try {
                            cantidad = Integer.parseInt(_args)
                        } catch (e: Exception) {
                        }
                        if (objetivo === perso) {
                            return false
                        }
                        val victima: Personaje? = objetivo
                        val nombreVict: String = victima.getNombre()
                        val pergamino: Objeto = Mundo.getObjetoModelo(10085).crearObjeto(cantidad, Constantes.OBJETO_POS_NO_EQUIPADO,
                                CAPACIDAD_STATS.RANDOM)
                        pergamino.addStatTexto(Constantes.STAT_MISION, "0#0#0#$nombreVict")
                        pergamino.addStatTexto(Constantes.STAT_RANGO, "0#0#" + Integer.toHexString(victima.getGradoAlineacion()))
                        pergamino.addStatTexto(Constantes.STAT_NIVEL, "0#0#" + Integer.toHexString(victima.getNivel()))
                        pergamino.addStatTexto(Constantes.STAT_ALINEACION, "0#0#" + Integer.toHexString(victima.getAlineacion()))
                        perso.addObjetoConOAKO(pergamino, true)
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(perso, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_DAR_MISION_PVP -> try {
                        if (perso.getNivel() < MainServidor.NIVEL_MINIMO_PARA_PVP) {
                            return false
                        }
                        if (perso.getResets() < MainServidor.RESET_MINIMO_PARA_PVP) {
                            return false
                        }
                        if (perso.getAlineacion() === Constantes.ALINEACION_NEUTRAL) {
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "134")
                            return false
                        }
                        val mision: MisionPVP = perso.getMisionPVP()
                        if (mision != null) {
                            if (System.currentTimeMillis() - mision.getTiempoInicio() < MainServidor.MINUTOS_MISION_PVP * 60 * 1000) {
                                if (perso.getCuenta().getIdioma().equalsIgnoreCase("fr")) {
                                    GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
                                            "<b>[Thomas Sacre]</b> Tu viens de terminer un contrat, tu dois attendre 10 minutes avant de te relancer dans ta qu�te de meurtre.",
                                            "000000")
                                } else {
                                    GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
                                            "<b>[Thomas Sacre]</b> Usted acaba de terminar un contrato, por ahora debes descansar 10 minutos.",
                                            "000000")
                                }
                                return false
                            }
                        }
                        if (!perso.alasActivadas()) {
                            perso.botonActDesacAlas('+')
                        }
                        val tiempo: Long = System.currentTimeMillis()
                        var victima: Personaje? = null
                        val victimas: ArrayList<Personaje?> = ArrayList<Personaje?>()
                        for (temp in Mundo.getPersonajesEnLinea()) {
                            if (temp.getNivel() < MainServidor.NIVEL_MINIMO_PARA_PVP) {
                                continue
                            }
                            if (temp.getResets() < MainServidor.RESET_MINIMO_PARA_PVP) {
                                continue
                            }
                            if (temp === perso || temp.getAlineacion() === perso.getAlineacion() || temp
                                            .getAlineacion() === Constantes.ALINEACION_NEUTRAL || !temp.alasActivadas()) {
                                continue
                            }
                            if (!MainServidor.ES_LOCALHOST) {
                                if (temp.getNombre().equalsIgnoreCase(perso.getUltMisionPVP()) || temp.getAdmin() > 0) {
                                    continue
                                }
                                if (temp.getCuenta().getActualIP().equals(perso.getCuenta().getActualIP())) {
                                    continue
                                }
                                if (MainServidor.PARAM_AGREDIR_MISMO_RESET && temp.getResets() !== perso.getResets()) {
                                    continue
                                }
                                if (MainServidor.PARAM_AGREDIR_RESET_ALTERNOS) {
                                    if (Math.abs(temp.getResets() - perso.getResets()) > 1) {
                                        continue
                                    }
                                }
                                if (MainServidor.MODO_PVP_AGRESIVO || MainServidor.MODO_PVP_STRONG) {
                                    if (perso.getCuenta().getApellidoReal().equalsIgnoreCase(temp.getCuenta().getApellidoReal())) {
                                        continue
                                    }
                                    if (perso.getCuenta().getNombreReal().equalsIgnoreCase(temp.getCuenta().getNombreReal())) {
                                        continue
                                    }
                                    if (perso.getCuenta().getCorreoReal().equalsIgnoreCase(temp.getCuenta().getCorreoReal())) {
                                        continue
                                    }
                                    if (perso.getUltMisionPVP().equalsIgnoreCase(temp.getNombre())) {
                                        continue
                                    }
                                    if (temp.getUltMisionPVP().equalsIgnoreCase(perso.getNombre())) {
                                        continue
                                    }
                                }
                            }
                            if (perso.getNivel() + MainServidor.RANGO_NIVEL_PVP >= temp.getNivel() && perso.getNivel()
                                    - MainServidor.RANGO_NIVEL_PVP <= temp.getNivel()) {
                                val ip: String = temp.getCuenta().getActualIP()
                                if (!perso._pvppj.containsKey(ip)) {
                                    perso._pvppj.put(ip, 0L)
                                }
                                val tiempoperso: Long = perso._pvppj.get(ip)
                                if (tiempo - tiempoperso >= MainServidor.MINUTOS_MISION_PVP) {
                                    victimas.add(temp)
                                }
                            }
                        }
                        if (victimas.isEmpty()) {
                            if (perso.getCuenta().getIdioma().equalsIgnoreCase("fr")) {
                                GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
                                        "<b>[Thomas Sacre]</b> Je ne trouve pas de victime � ta hauteur, reviens plus tard.", "000000")
                            } else {
                                GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
                                        "<b>[Thomas Sacre]</b> No se encontr� ning�n personaje a tu altura, porfavor regresa m�s tarde.",
                                        "000000")
                            }
                            return false
                        }
                        victima = victimas.get(Formulas.getRandomInt(0, victimas.size() - 1))
                        val nombreVict: String = victima.getNombre()
                        if (perso.getCuenta().getIdioma().equalsIgnoreCase("fr")) {
                            GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "<b>[Thomas Sacre]</b> Ta victime est : $nombreVict.",
                                    "000000")
                        } else {
                            GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "<b>[Thomas Sacre]</b> Usted esta ahora a la caza de "
                                    + nombreVict + ".", "000000")
                        }
                        val recompensaExp: Long = Formulas.getXPMision(victima.getNivel())
                        val misionPVP = MisionPVP(System.currentTimeMillis(), victima, MainServidor.MISION_PVP_KAMAS,
                                recompensaExp, Constantes.getCraneoPorClase(victima.getClaseID(true)))
                        val pergamino: Objeto = Mundo.getObjetoModelo(10085).crearObjeto(20, Constantes.OBJETO_POS_NO_EQUIPADO,
                                CAPACIDAD_STATS.RANDOM)
                        pergamino.addStatTexto(Constantes.STAT_INTERCAMBIABLE_DESDE, ObjetoModelo.stringFechaIntercambiable(365))
                        pergamino.addStatTexto(Constantes.STAT_MISION, "0#0#0#$nombreVict")
                        pergamino.addStatTexto(Constantes.STAT_RANGO, "0#0#" + Integer.toHexString(victima.getGradoAlineacion()))
                        pergamino.addStatTexto(Constantes.STAT_NIVEL, "0#0#" + Integer.toHexString(victima.getNivel()))
                        pergamino.addStatTexto(Constantes.STAT_ALINEACION, "0#0#" + Integer.toHexString(victima.getAlineacion()))
                        perso.addObjetoConOAKO(pergamino, true)
                        perso._pvppj.put(victima.getCuenta().getActualIP(), tiempo)
                        perso.setUltMisionPVP(nombreVict)
                        if (MainServidor.MODO_PVP_AGRESIVO || MainServidor.MODO_PVP_STRONG) {
                            victima.setUltMisionPVP(perso.getNombre())
                        }
                        perso.setMisionPVP(misionPVP)
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_GEOPOSICION_MISION_PVP -> try {
                        if (perso.getPelea() != null || perso.esFantasma() || objUsar == null) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo)
                            return false
                        }
                        val victima: Personaje = Mundo.getPersonajePorNombre(objUsar.getParamStatTexto(Constantes.STAT_MISION, 4))
                        if (victima == null || !victima.enLinea()) {
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "1211")
                            return false
                        }
                        if (victima.esFantasma()) {
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "1OBJETIVE_GHOST")
                            return false
                        }
                        if (perso.getMisionPVP() == null || !perso.getMisionPVP().getNombreVictima().equalsIgnoreCase(victima
                                        .getNombre())) {
                            var recompensaExp: Long = 0
                            if (objUsar.tieneStatTexto(Constantes.STAT_INTERCAMBIABLE_DESDE)) {
                                recompensaExp = Formulas.getXPMision(victima.getNivelOmega())
                            }
                            perso.setMisionPVP(MisionPVP(0, victima, objUsar.getStatValor(
                                    Constantes.STAT_GANAR_KAMAS), recompensaExp, victima.getClaseID(true)))
                        }
                        GestorSalida.ENVIAR_IC_PERSONAJE_BANDERA_COMPAS(perso, victima.getMapa().getX().toString() + "|" + victima.getMapa().getY())
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_TELEPORT_MISION_PVP -> try {
                        if (perso.getPelea() != null || perso.esFantasma() || objUsar == null) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo)
                            return false
                        }
                        val victima: Personaje = Mundo.getPersonajePorNombre(objUsar.getParamStatTexto(989, 4))
                        if (victima == null || !victima.enLinea()) {
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "1211")
                            return false
                        }
                        if (victima.esFantasma()) {
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "1OBJETIVE_GHOST")
                            return false
                        }
                        if (!victima.alasActivadas()) {
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "1195")
                            return false
                        }
                        if (victima.getPelea() != null) {
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "1OBJETIVE_IN_FIGHT")
                            return false
                        }
                        if (!victima.getHuir()) {
                            if (System.currentTimeMillis() - victima.getTiempoAgre() > 10000) {
                                victima.setHuir(true)
                            } else {
                                GestorSalida.ENVIAR_Im_INFORMACION(perso, "1OBJETIVE_IN_FIGHT")
                                return false
                            }
                        }
                        if (victima.estaExchange()) {
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "1NO_PUEDES_TELEPORT_POR_EXCHANGE")
                            return false
                        }
                        val mapas: CopyOnWriteArrayList<Integer?> = MainServidor.MAPAS_PVP
                        val mapa: Int = mapas.get(Formulas.getRandomInt(0, mapas.size() - 1))
                        perso.teleport(mapa, 399.toShort())
                        victima.teleport(mapa, 194.toShort())
                        perso.setHuir(false)
                        victima.setHuir(false)
                        perso.setTiempoAgre(System.currentTimeMillis())
                        victima.setTiempoAgre(System.currentTimeMillis())
                        if (MainServidor.AUTO_AGREDIR_MISION) {
                            Thread.sleep(3000L)
                            synchronized(perso.getMapa().getPrePelea()) {
                                if (!perso.estaDisponible(false, true) || perso.estaInmovil()) {
                                    GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 'o')
                                    return false
                                }
                                if (perso.esFantasma() || perso.esTumba()) {
                                    GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 'd')
                                    return false
                                }
                                if (!perso.getMapa().puedeAgregarOtraPelea()) {
                                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "1MAP_LIMI_OF_FIGHTS")
                                    return false
                                }
                                val agredido: Personaje = victima
                                if (!Constantes.puedeAgredir(perso, agredido)) {
                                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "1DONT_ATTACK_PLAYER")
                                    return false
                                }
                                if (MainServidor.PARAM_AGREDIR_MISMO_RESET && agredido.getResets() !== perso.getResets()) {
                                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "1DONT_ATTACK_PLAYER")
                                    return false
                                }
                                if (!MainServidor.PARAM_AGRESION_ADMIN && (agredido.getAdmin() > 0 || perso.getAdmin() > 0)) {
                                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "1DONT_ATTACK_PLAYER")
                                    return false
                                }
                                if (!MainServidor.SON_DE_LUCIANO) if (Math.abs(perso.getNivel() - agredido.getNivel()) > MainServidor.RANGO_NIVEL_PVP) {
                                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "1DONT_ATTACK_PLAYER")
                                    return false
                                }
                                // System.out.println("3");
                                // Thread.sleep(20000);
                                // System.out.println("4");
                                var deshonor = false
                                if (MainServidor.PARAM_AGREDIR_JUGADORES_ASESINOS && agredido.getDeshonor() > 0 && agredido.getAlineacion() !== perso.getAlineacion()) {
                                    // salta para irse a atacar
                                } else if (perso.getMapa().mapaNoAgresion()
                                        || perso.getMapa().getSubArea().getArea().getSuperArea().getID() === 3 || MainServidor.SUBAREAS_NO_PVP.contains(perso.getMapa().getSubArea().getID())) {
                                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "113")
                                    return false
                                } else if (!MainServidor.PARAM_AGREDIR_NEUTRAL
                                        && agredido.getAlineacion() === Constantes.ALINEACION_NEUTRAL) {
                                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "1DONT_ATTACK_PLAYER")
                                    return false
                                } else if (!MainServidor.PARAM_AGREDIR_ALAS_DESACTIVADAS && !agredido.alasActivadas()) {
                                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "1DONT_ATTACK_PLAYER")
                                    return false
                                } else if (agredido.getAlineacion() === Constantes.ALINEACION_NEUTRAL || !agredido.alasActivadas()) {
                                    if (perso.getAlineacion() !== Constantes.ALINEACION_NEUTRAL) {
                                        perso.addDeshonor(1)
                                        GestorSalida.ENVIAR_Im_INFORMACION(perso, "084;1")
                                        deshonor = true
                                    }
                                }
                                Thread.sleep(1000L)
                                perso.getMapa().iniciarPeleaPVP(perso, agredido, deshonor)
                            }
                        }
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_CONFIRMA_CUMPLIO_MISION -> try {
                        for (s in _args.split(";")) {
                            for (mision in perso.getMisiones()) {
                                if (mision.estaCompletada()) {
                                    continue
                                }
                                if (mision.getIDModelo() !== Integer.parseInt(s)) {
                                    continue
                                }
                                for (entry in mision.getObjetivos().entrySet()) {
                                    if (entry.getValue() === Mision.ESTADO_COMPLETADO) {
                                        continue
                                    }
                                    val objMod: MisionObjetivoModelo = Mundo.getMisionObjetivoModelo(entry.getKey())
                                    perso.confirmarObjetivo(mision, objMod, perso, null, false, 0)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_BOOST_FULL_STATS -> try {
                        val args: Int = Integer.parseInt(_args)
                        if (args < 10 || args > 15) {
                            return false
                        }
                        perso.boostStat2(args, perso.getCapital())
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_PAGAR_PARA_REALIZAR_ACCION -> try {
                        val sep: Array<String?> = _args.split(Pattern.quote("|"))
                        val precio: Int = Integer.parseInt(sep[0])
                        if (perso.getKamas() >= precio) {
                            perso.addKamas(-precio, true, true)
                            var args = ""
                            try {
                                args = sep[1].split(";", 2).get(1)
                            } catch (e1: Exception) {
                            }
                            realizar_Accion_Estatico(Integer.parseInt(sep[1].split(";").get(0)), args, perso, null, -1, (-1.toShort()).toShort())
                        } else {
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "182")
                        }
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_SOLICITAR_OBJETOS_PARA_DAR_OTROS ->                    // quita unos objetos para dar otros , pedir, solicitar
                        try {
                            val t: Array<String?> = _args.split(Pattern.quote("|"))
                            var args = t[0]
                            if (t.size < 2) {
                                if (objUsar == null) {
                                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "14")
                                    return false
                                }
                            } else {
                                args = t[1]
                                for (s in t[0].split(";")) {
                                    val id: Int = Integer.parseInt(s.split(",").get(0))
                                    val cant: Int = Integer.parseInt(s.split(",").get(1))
                                    if (!perso.tieneObjPorModYCant(id, cant)) {
                                        GestorSalida.ENVIAR_Im_INFORMACION(perso, "14")
                                        return false
                                    }
                                }
                                for (s in t[0].split(";")) {
                                    val id: Int = Integer.parseInt(s.split(",").get(0))
                                    val cant: Int = Integer.parseInt(s.split(",").get(1))
                                    if (perso.tenerYEliminarObjPorModYCant(id, cant)) {
                                        GestorSalida.ENVIAR_Im_INFORMACION(perso, "022;$cant~$id")
                                    }
                                }
                            }
                            for (s in args.split(";")) {
                                val id: Int = Integer.parseInt(s.split(",").get(0))
                                val cant: Int = Integer.parseInt(s.split(",").get(1))
                                var max = false
                                try {
                                    if (s.split(",").length > 2) max = s.split(",").get(2).equals("1")
                                } catch (e: Exception) {
                                }
                                val tempObjMod: ObjetoModelo = Mundo.getObjetoModelo(id)
                                if (tempObjMod == null) {
                                    GestorSalida.ENVIAR_BN_NADA(objetivo, "BUG ACCION $_id idObjMod $id")
                                    continue
                                }
                                if (cant > 0) {
                                    perso.addObjIdentAInventario(tempObjMod.crearObjeto(cant, Constantes.OBJETO_POS_NO_EQUIPADO,
                                            CAPACIDAD_STATS.RANDOM), max)
                                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "021;$cant~$id")
                                }
                            }
                            GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso)
                        } catch (e: Exception) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                            return false
                        }
                    ACCION_REVIVIR -> {
                        if (objetivo.getPelea() != null) {
                            return false
                        }
                        objetivo.revivir(true)
                    }
                    ACCION_ABRIR_DOCUMENTO -> GestorSalida.ENVIAR_dC_ABRIR_DOCUMENTO(perso, _args)
                    ACCION_DAR_SET_OBJETOS_POR_FICHAS -> try {
                        val idSet: Int = Integer.parseInt(_args.split(",").get(0))
                        val fichas: Int = Integer.parseInt(_args.split(",").get(1))
                        val OS: ObjetoSet = Mundo.getObjetoSet(idSet)
                        if (OS == null) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo)
                            return false
                        }
                        if (perso.tenerYEliminarObjPorModYCant(1749, fichas)) {
                            for (objM in OS.getObjetosModelos()) {
                                objetivo.addObjIdentAInventario(objM.crearObjeto(1, Constantes.OBJETO_POS_NO_EQUIPADO,
                                        CAPACIDAD_STATS.RANDOM), false)
                                GestorSalida.ENVIAR_Im_INFORMACION(perso, "021;1~" + objM.getID())
                            }
                        } else {
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "14|43")
                        }
                        GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(objetivo)
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_REALIZAR_ACCION_PJS_EN_MAPA_POR_ALINEACION_Y_DISTANCIA ->                                                                                                                                                        // personajes en el mapa
                        try {
                            val t: Array<String?> = _args.split(Pattern.quote("|"))
                            var tipoAlin = 0
                            try {
                                tipoAlin = Integer.parseInt(t[0])
                            } catch (e: Exception) {
                            }
                            var dist = 1000
                            try {
                                dist = Integer.parseInt(t[1])
                            } catch (e: Exception) {
                            }
                            val idAccion: Int = Integer.parseInt(t[2].split(";").get(0))
                            var args2 = ""
                            try {
                                args2 = t[2].split(";", 2).get(1)
                            } catch (e1: Exception) {
                            }
                            val mapa: Mapa = perso.getMapa()
                            val celdaPerso: Short = perso.getCelda().getID()
                            val aplicar: ArrayList<Personaje?> = ArrayList()
                            for (o in mapa.getArrayPersonajes()) {
                                if (tipoAlin == 0) {
                                    // pasan normal
                                } else {
                                    if (o.getAlineacion() === Constantes.ALINEACION_NEUTRAL) {
                                        continue
                                    }
                                    if (tipoAlin == 1 && o.getAlineacion() !== perso.getAlineacion()) {
                                        continue
                                    } else if (tipoAlin == 2 && o.getAlineacion() === perso.getAlineacion()) {
                                        continue
                                    }
                                }
                                if (o.getMapa().getID() !== mapa.getID()) {
                                    continue
                                }
                                if (Camino.distanciaDosCeldas(mapa, o.getCelda().getID(), celdaPerso) > dist) {
                                    continue
                                }
                                aplicar.add(o)
                            }
                            for (o in aplicar) {
                                realizar_Accion_Estatico(idAccion, args2, o, null, -1, (-1.toShort()).toShort())
                            }
                        } catch (e: Exception) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                            return false
                        }
                    ACCION_LIBERAR_TUMBA -> {
                        if (objetivo.getPelea() != null) {
                            return false
                        }
                        if (objetivo.esTumba()) {
                            objetivo.convertirseFantasma()
                        }
                    }
                    ACCION_REVIVIR2 -> {
                        if (objetivo.getPelea() != null) {
                            return false
                        }
                        objetivo.revivir(true)
                    }
                    ACCION_AGREGAR_PJ_LIBRO_ARTESANOS -> {
                        try {
                            val idOficio: Int = Integer.parseInt(_args)
                            for (SO in perso.getStatsOficios().values()) {
                                if (SO.getOficio().getID() === idOficio) {
                                    SO.setLibroArtesano(true)
                                    GestorSalida.ENVIAR_Ej_AGREGAR_LIBRO_ARTESANO(perso, "+$idOficio")
                                    return false
                                }
                            }
                        } catch (e: Exception) {
                        }
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_ACTIVAR_CELDAS_INTERACTIVAS -> try {
                        for (s in _args.split(";")) {
                            var m: Int = perso.getMapa().getID()
                            var c: Short = -1
                            val split: Array<String?> = s.split(",")
                            try {
                                val cm = split[0]
                                c = Short.parseShort(cm.split("m").get(0))
                                m = Short.parseShort(cm.split("m").get(1))
                            } catch (e: Exception) {
                            }
                            var bAnimacionMovimiento = false // conGDF
                            var milisegundos: Long = 30000
                            try {
                                bAnimacionMovimiento = split[1]!!.equals("1")
                            } catch (e: Exception) {
                            }
                            try {
                                milisegundos = Long.parseLong(split[2])
                            } catch (e: Exception) {
                            }
                            val mapa: Mapa = Mundo.getMapa(m)
                            if (mapa == null) {
                                GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id MAPA $m ES NULO")
                                return false
                            }
                            val celda: Celda = mapa.getCelda(c)
                            if (celda == null) {
                                GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION " + _id + " MAPA " + m + " CELDA " + c
                                        + " ES NULO")
                                return false
                            }
                            celda.activarCelda(bAnimacionMovimiento, milisegundos)
                        }
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_DAR_QUITAR_EMOTE -> try {
                        for (s in _args.split(",")) {
                            var emote: Byte = Byte.parseByte(s)
                            if (emote < 0) {
                                emote = Math.abs(emote)
                                if (perso.borrarEmote(emote)) {
                                    GestorSalida.ENVIAR_eR_BORRAR_EMOTE(perso, emote, true)
                                }
                            } else if (emote > 0) {
                                if (perso.addEmote(emote)) {
                                    GestorSalida.ENVIAR_eA_AGREGAR_EMOTE(perso, emote, true)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_ENVIAR_PACKET_ORNAMENTO -> try {
                        val t: Array<String?> = _args.split(Pattern.quote("|"))
                        val nuevaAccion = t[0]
                        val Packete = t[1]
                        val ArgumentosP = t[2]
                        val Argumentos = t[3]
                        GestorSalida.enviar(perso, Packete + ArgumentosP, true)
                        var args: String? = ""
                        val accionID: Int = Integer.parseInt(nuevaAccion)
                        try {
                            args = Argumentos
                        } catch (e1: Exception) {
                        }
                        realizar_Accion_Estatico(accionID, args, perso, null, -1, (-1.toShort()).toShort())
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_ENVIAR_PACKET_OBJETO -> try {
                        val t: Array<String?> = _args.split(Pattern.quote("|"))
                        val nuevaAccion = t[0]
                        val Packete = t[1]
                        val Argumentos = t[2]
                        GestorSalida.enviar(perso, Packete + Argumentos, true)
                        var args: String? = ""
                        val accionID: Int = Integer.parseInt(nuevaAccion)
                        try {
                            args = Argumentos
                        } catch (e1: Exception) {
                        }
                        realizar_Accion_Estatico(accionID, args, perso, null, -1, (-1.toShort()).toShort())
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_SOLICITAR_OBJETOS_PARA_REALIZAR_ACCION ->                    // quitar o restar el objeto para realizar una accion
                        try {
                            val t: Array<String?> = _args.split(Pattern.quote("|"))
                            var nuevaAccion = t[0]
                            if (t.size < 2) {
                                if (objUsar == null) {
                                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "14")
                                    return false
                                }
                            } else {
                                nuevaAccion = t[1]
                                val solicita = t[0]
                                for (s in solicita.split(";")) {
                                    val id: Int = Integer.parseInt(s.split(",").get(0))
                                    val cant: Int = Math.abs(Integer.parseInt(s.split(",").get(1)))
                                    if (!perso.tieneObjPorModYCant(id, cant)) {
                                        GestorSalida.ENVIAR_Im_INFORMACION(perso, "14")
                                        return false
                                    }
                                }
                                for (s in solicita.split(";")) {
                                    val id: Int = Integer.parseInt(s.split(",").get(0))
                                    val cant: Int = Math.abs(Integer.parseInt(s.split(",").get(1)))
                                    if (perso.tenerYEliminarObjPorModYCant(id, cant)) {
                                        GestorSalida.ENVIAR_Im_INFORMACION(perso, "022;$cant~$id")
                                    }
                                }
                            }
                            var args2 = ""
                            val accionID: Int = Integer.parseInt(nuevaAccion.split(";").get(0))
                            try {
                                args2 = nuevaAccion.split(";", 2).get(1)
                            } catch (e1: Exception) {
                            }
                            realizar_Accion_Estatico(accionID, args2, perso, null, -1, (-1.toShort()).toShort())
                        } catch (e: Exception) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                            return false
                        }
                    ACCION_CAMBIAR_ROSTRO -> try {
                        perso.cambiarRostro(Byte.parseByte(_args))
                        GestorSalida.ENVIAR_Oa_CAMBIAR_ROPA_MAPA(perso.getMapa(), perso)
                    } catch (e: Exception) {
                    }
                    ACCION_MENSAJE_INFORMACION -> GestorSalida.ENVIAR_Im_INFORMACION(perso, _args)
                    ACCION_MENSAJE_PANEL -> GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(perso, _args)
                    ACCION_DAR_OBJETOS_DE_LOS_STATS -> {
                        if (objUsar == null) {
                            return false
                        }
                        for (s in objUsar.strDarObjetos().split(";")) {
                            try {
                                if (s.isEmpty()) {
                                    continue
                                }
                                val id: Int = Integer.parseInt(s.split(",").get(0))
                                val cant: Int = Integer.parseInt(s.split(",").get(1))
                                val tempObjMod: ObjetoModelo = Mundo.getObjetoModelo(id)
                                if (tempObjMod == null) {
                                    GestorSalida.ENVIAR_BN_NADA(objetivo, "BUG ACCION $_id idObjMod $id")
                                    continue
                                }
                                if (cant > 0) {
                                    perso.addObjIdentAInventario(tempObjMod.crearObjeto(cant, Constantes.OBJETO_POS_NO_EQUIPADO,
                                            CAPACIDAD_STATS.RANDOM), false)
                                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "021;$cant~$id")
                                }
                            } catch (e: Exception) {
                                GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                            }
                        }
                    }
                    ACCION_DAR_ABONO_DIAS -> try {
                        val idInt: Int = Integer.parseInt(_args)
                        var abono: Long = Math.max(GestorSQL.GET_ABONO(objetivo.getCuenta().getNombre()), System.currentTimeMillis())
                        abono += (idInt * 24 * 3600 * 1000).toLong()
                        abono = Math.max(abono, System.currentTimeMillis() - 1000)
                        GestorSQL.SET_ABONO(abono, objetivo.getCuentaID())
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_DAR_ABONO_HORAS -> try {
                        val idInt: Int = Integer.parseInt(_args)
                        var abono: Long = Math.max(GestorSQL.GET_ABONO(objetivo.getCuenta().getNombre()), System.currentTimeMillis())
                        abono += (idInt * 3600 * 1000).toLong()
                        abono = Math.max(abono, System.currentTimeMillis() - 1000)
                        GestorSQL.SET_ABONO(abono, objetivo.getCuentaID())
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_DAR_ABONO_MINUTOS -> try {
                        val idInt: Int = Integer.parseInt(_args)
                        var abono: Long = Math.max(GestorSQL.GET_ABONO(objetivo.getCuenta().getNombre()), System.currentTimeMillis())
                        abono += (idInt * 60 * 1000).toLong()
                        abono = Math.max(abono, System.currentTimeMillis() - 1000)
                        GestorSQL.SET_ABONO(abono, objetivo.getCuentaID())
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_DAR_NIVEL_DE_ORDEN -> try {
                        objetivo.addOrdenNivel(Integer.parseInt(_args))
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_DAR_ORDEN -> try {
                        objetivo.setOrden(Integer.parseInt(_args))
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_BORRAR_OBJETO_MODELO -> try {
                        var b = false
                        for (s in _args.split(";")) {
                            val args: Array<String?> = s.split(",")
                            val id: Int = Integer.parseInt(args[0])
                            var minutos = 0
                            try {
                                minutos = Integer.parseInt(args[1])
                            } catch (e: Exception) {
                            }
                            val borrados: Int = perso.eliminarPorObjModeloRecibidoDesdeMinutos(id, minutos)
                            if (borrados > 0) {
                                b = true
                                GestorSalida.ENVIAR_Im_INFORMACION(perso, "022;$borrados~$id")
                            }
                        }
                        if (b) {
                            GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso)
                        }
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_VERIFICA_STAT_OBJETO_Y_LO_BORRA -> try {
                        var b = true
                        for (s in _args.split(";")) {
                            var bb = false
                            val args: Array<String?> = s.split(",")
                            val idObjModelo: Int = Integer.parseInt(args[0])
                            val llaveID: Int = Integer.parseInt(args[1])
                            for (obj in perso.getObjetosTodos()) {
                                if (obj.getObjModeloID() !== idObjModelo) {
                                    continue
                                }
                                val stats: Array<String?> = obj.convertirStatsAString(true).split(",")
                                for (st in stats) {
                                    val statID: Int = Integer.parseInt(st.split("#").get(0), 16)
                                    val tempObjetoID: Int = Integer.parseInt(st.split("#").get(3), 16)
                                    if (statID != Constantes.STAT_LLAVE_MAZMORRA) {
                                        continue
                                    }
                                    if (tempObjetoID == llaveID) {
                                        bb = true
                                    }
                                }
                                if (bb) {
                                    break
                                }
                            }
                            b = b and bb
                        }
                        if (b) {
                            for (s in _args.split(";")) {
                                val args: Array<String?> = s.split(",")
                                val id: Int = Integer.parseInt(args[0])
                                val objetoID: Int = Integer.parseInt(args[1])
                                for (obj in perso.getObjetosTodos()) {
                                    if (obj.getObjModeloID() !== id) {
                                        continue
                                    }
                                    val stats: Array<String?> = obj.convertirStatsAString(true).split(",")
                                    val nuevo = StringBuilder()
                                    b = false
                                    for (st in stats) {
                                        if (nuevo.length() > 0) {
                                            nuevo.append(",")
                                        }
                                        val statID: Int = Integer.parseInt(st.split("#").get(0), 16)
                                        val tempObjetoID: Int = Integer.parseInt(st.split("#").get(3), 16)
                                        if (statID == Constantes.STAT_LLAVE_MAZMORRA && tempObjetoID == objetoID) {
                                            b = true
                                        } else {
                                            nuevo.append(st)
                                        }
                                    }
                                    if (b) {
                                        obj.convertirStringAStats(nuevo.toString())
                                        GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(perso, obj)
                                        break
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_INTERCAMBIA_OBJETOS_FORTIFICADOS -> {
                        val argumentos: Array<String?> = _args.split(Pattern.quote(";"))
                        val objetoFortificado: Int = Integer.parseInt(argumentos[0])
                        val nivel: Int = Integer.parseInt(argumentos[1])
                        val objetoEntregar: Int = Integer.parseInt(argumentos[2])
                        val cantObjetoEntregar: Int = Integer.parseInt(argumentos[3])
                        val objetosPedir = argumentos[4]
                        val _necesita: ArrayList<Duo<Integer?, Integer?>?> = ArrayList<Duo<Integer?, Integer?>?>()
                        for (pedir in objetosPedir.split(",")) {
                            val id: Int = Integer.parseInt(pedir.split(":").get(0))
                            val cant: Int = Integer.parseInt(pedir.split(":").get(1))
                            _necesita.add(Duo<Integer?, Integer?>(id, cant))
                        }
                        if (perso.tieneObjPorModYNivel2(objetoFortificado, nivel)) {
                            val obj: Objeto = perso.getObjModeloNoEquipadoFortificado2(objetoFortificado, nivel)
                            if (obj != null) {
                                for (duo in _necesita) {
                                    if (!perso.tieneObjPorModYCant(duo._primero, duo._segundo)) {
                                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso, "No tiene los objetos requeridos para intercambiar")
                                        return false
                                    }
                                }
                                for (duo in _necesita) {
                                    if (perso.tenerYEliminarObjPorModYCant(duo._primero, duo._segundo)) {
                                        GestorSalida.ENVIAR_Im_INFORMACION(perso, "022;" + duo._segundo.toString() + "~" + duo._primero)
                                    }
                                }
                                perso.restarCantObjOEliminar(obj.getID(), 1, true)
                                GestorSalida.ENVIAR_Im_INFORMACION(perso, "022;" + 1 + "~" + obj.getObjModeloID())
                                val tempObjMod: ObjetoModelo = Mundo.getObjetoModelo(objetoEntregar)
                                if (tempObjMod == null) {
                                    GestorSalida.ENVIAR_BN_NADA(objetivo, "BUG ACCION $_id idObjMod ")
                                } else {
                                    if (cantObjetoEntregar > 0) {
                                        perso.addObjIdentAInventario(tempObjMod.crearObjeto(cantObjetoEntregar, Constantes.OBJETO_POS_NO_EQUIPADO,
                                                CAPACIDAD_STATS.RANDOM), false)
                                        GestorSalida.ENVIAR_Im_INFORMACION(perso, "021;$cantObjetoEntregar~$objetoEntregar")
                                    }
                                }
                            } else {
                                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso, "El objeto Fortificado no tiene el nivel requerido")
                                return false
                            }
                        } else {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso, "No tienes el objeto fortificado o lo tienes puesto")
                            return false
                        }
                    }
                    ACCION_EVOLUCIONA_OBJETOS -> {
                        argumentos = _args.split(Pattern.quote(","))
                        val pos: Byte = Byte.parseByte(argumentos.get(0))
                        nivel = Integer.parseInt(argumentos.get(1))
                        val porcStats: Int = Integer.parseInt(argumentos.get(2))
                        var porcProba: Int = Integer.parseInt(argumentos.get(3))
                        val objetoTemp: Objeto = perso.getObjPosicion(pos)
                        if (objetoTemp == null) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso, "No tienes puesto el objeto a fortificar")
                            return false
                        }
                        if (!objetoTemp.tieneStatTexto(Constantes.STAT_NIVEL)) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso, "El objeto no se puede fortificar")
                            return false
                        }
                        val statsTemp: String = objetoTemp.getParamStatTexto(Constantes.STAT_NIVEL, 3)
                        val nivelObjeto: Int = Integer.parseInt(statsTemp, 16)
                        if (nivel <= nivelObjeto) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso, "El objeto a fortificar no concuerda con el nivel del pergamino")
                            return false
                        }
                        if (porcProba < 100) {
                            when (nivelObjeto) {
                                1 -> porcProba = 90
                                2 -> porcProba = 80
                                3 -> porcProba = 70
                                4 -> porcProba = 60
                                5 -> porcProba = 50
                                6 -> porcProba = 40
                                7 -> porcProba = 30
                                8 -> porcProba = 20
                                9 -> porcProba = 10
                                10 -> porcProba = 8
                                11 -> porcProba = 7
                                12 -> porcProba = 6
                                13 -> porcProba = 5
                                14 -> porcProba = 4
                            }
                        }
                        val numRand: Int = Formulas.getRandomInt(0, 100)
                        if (numRand <= porcProba) {
                            nivel = nivelObjeto + 1
                            if (objetoTemp.tieneStatTexto(Constantes.STAT_OBJETO_OCULTO_1) && objetoTemp.tieneStatTexto(Constantes.STAT_OBJETO_OCULTO_1)) {
                                val idobj1: Int = Integer.parseInt(objetoTemp.getStats().getStatTexto(Constantes.STAT_OBJETO_OCULTO_1))
                                val idobj2: Int = Integer.parseInt(objetoTemp.getStats().getStatTexto(Constantes.STAT_OBJETO_OCULTO_2))
                                val obj1: Objeto = Mundo.getObjeto(idobj1)
                                val obj2: Objeto = Mundo.getObjeto(idobj2)

                                //subir obj1
                                var statsString: String = obj1.convertirStatsAString(true)
                                var temp: Array<String?>? = statsString.split(",")
                                var statsNew: StringBuilder? = StringBuilder()
                                run {
                                    var i = 0
                                    while (i < temp!!.size) {
                                        if (statsNew.length() > 0) statsNew.append(",")
                                        val temp2: Array<String?> = temp!![i].split("#")
                                        val stats: Int = Integer.parseInt(temp2[0], 16)
                                        if (Constantes.esStatTexto(Integer.parseInt(temp2[0], 16))) {
                                            if (Integer.parseInt(temp2[0], 16) === Constantes.STAT_NIVEL) {
                                                statsNew.append(temp2[0].toString() + "#0#0#" + Integer.toHexString(nivel))
                                            } else {
                                                statsNew.append(temp!![i])
                                            }
                                        } else {
                                            var valor: Int = Integer.parseInt(temp2[1], 16)
                                            var valor2: Int = Integer.parseInt(temp2[2], 16)
                                            if (!MainServidor.STATS_NO_FORTICABLES.contains(stats)) {
                                                valor += Math.ceil(valor * porcStats * 0.01) as Int
                                                valor2 += Math.ceil(valor2 * porcStats * 0.01) as Int
                                                statsNew.append(temp2[0].toString() + "#" + Integer.toHexString(valor) + "#" + Integer.toHexString(valor2) + "#0#0d0+" + valor)
                                            } else {
                                                statsNew.append(temp!![i]) // con esto lo pone tal cual como lo tenia
                                            }
                                        }
                                        i++
                                    }
                                }
                                obj1.convertirStringAStats(statsNew.toString())
                                GestorSQL.SALVAR_OBJETO(obj1)

                                //subir obj2
                                statsString = obj2.convertirStatsAString(true)
                                temp = statsString.split(",")
                                statsNew = StringBuilder()
                                var i = 0
                                while (i < temp!!.size) {
                                    if (statsNew.length() > 0) statsNew.append(",")
                                    val temp2: Array<String?> = temp[i].split("#")
                                    val stats: Int = Integer.parseInt(temp2[0], 16)
                                    if (Constantes.esStatTexto(Integer.parseInt(temp2[0], 16))) {
                                        if (Integer.parseInt(temp2[0], 16) === Constantes.STAT_NIVEL) {
                                            statsNew.append(temp2[0].toString() + "#0#0#" + Integer.toHexString(nivel))
                                        } else {
                                            statsNew.append(temp[i])
                                        }
                                    } else {
                                        var valor: Int = Integer.parseInt(temp2[1], 16)
                                        var valor2: Int = Integer.parseInt(temp2[2], 16)
                                        if (!MainServidor.STATS_NO_FORTICABLES.contains(stats)) {
                                            valor += Math.ceil(valor * porcStats * 0.01) as Int
                                            valor2 += Math.ceil(valor2 * porcStats * 0.01) as Int
                                            statsNew.append(temp2[0].toString() + "#" + Integer.toHexString(valor) + "#" + Integer.toHexString(valor2) + "#0#0d0+" + valor)
                                        } else {
                                            statsNew.append(temp[i]) // con esto lo pone tal cual como lo tenia
                                        }
                                    }
                                    i++
                                }
                                obj2.convertirStringAStats(statsNew.toString())
                                GestorSQL.SALVAR_OBJETO(obj2)
                            }
                            val statsString: String = objetoTemp.convertirStatsAString(true)
                            val temp: Array<String?> = statsString.split(",")
                            val statsNew = StringBuilder()
                            var objTemp2: Objeto? = null
                            if (Mundo.CRAFEOS.get(objetoTemp.getObjModeloID()) != null) {
                                if (nivel == 2) {
                                    if (objetoTemp.tieneStatTexto(Constantes.STAT_OBJETO_OCULTO_3)) {
                                        val idobj1: Int = Integer.parseInt(objetoTemp.getStats().getStatTexto(Constantes.STAT_OBJETO_OCULTO_3))
                                        objTemp2 = Mundo.getObjeto(idobj1)
                                        objTemp2.convertirStringAStats(objetoTemp.getStats().convertirStatsAString())
                                        objTemp2.addStatTexto(Constantes.STAT_NIVEL, objetoTemp.getStats().getStatTexto(Constantes.STAT_NIVEL))
                                        GestorSQL.SALVAR_OBJETO(objTemp2)
                                    } else {
                                        objTemp2 = Mundo.getObjetoModelo(objetoTemp.getObjModeloID()).crearObjeto(1, Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.MAXIMO)
                                        objTemp2.convertirStringAStats(objetoTemp.getStats().convertirStatsAString())
                                        objTemp2.addStatTexto(Constantes.STAT_NIVEL, objetoTemp.getStats().getStatTexto(Constantes.STAT_NIVEL))
                                        Mundo.addObjeto(objTemp2, true)
                                    }
                                }
                            }
                            var i = 0
                            while (i < temp.size) {
                                if (statsNew.length() > 0) statsNew.append(",")
                                val temp2: Array<String?> = temp[i].split("#")
                                val stats: Int = Integer.parseInt(temp2[0], 16)
                                if (Constantes.esStatTexto(stats)) {
                                    if (Integer.parseInt(temp2[0], 16) === Constantes.STAT_NIVEL) {
                                        statsNew.append(temp2[0].toString() + "#0#0#" + Integer.toHexString(nivel))
                                    } else {
                                        statsNew.append(temp[i])
                                    }
                                } else {
                                    var valor: Int = Integer.parseInt(temp2[1], 16)
                                    var valor2: Int = Integer.parseInt(temp2[2], 16) // con esto fortificara los 2 stats no solo el primero quitalo de los stats no forticable queir ver si funciona esto
                                    if (!MainServidor.STATS_NO_FORTICABLES.contains(stats)) {
                                        valor += Math.ceil(valor * porcStats * 0.01) as Int
                                        valor2 += Math.ceil(valor2 * porcStats * 0.01) as Int
                                        statsNew.append(temp2[0].toString() + "#" + Integer.toHexString(valor) + "#" + Integer.toHexString(valor2) + "#0#0d0+" + valor)
                                    } else {
                                        statsNew.append(temp[i]) // con esto lo pone tal cual como lo tenia
                                    }
                                }
                                i++
                            }
                            objetoTemp.convertirStringAStats(statsNew.toString())
                            if (objTemp2 != null) objetoTemp.addStatTexto(Constantes.STAT_OBJETO_OCULTO_3, objTemp2.getID().toString() + "")
                            GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(perso, objetoTemp)
                            GestorSQL.SALVAR_OBJETO(objetoTemp)
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso, "Tu equipo se a fortificado con exito a nivel: $nivel")
                            GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso)
                            perso.refrescarStuff(true, true, false)
                        } else {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso, "Tu pergamino fallo, int�ntalo nuevamente, seguro tendr�s mas suerte")
                        }
                        return true
                    }
                    ACCION_VERIFICA_STAT_OBJETO_Y_LO_BORRA_EJECUTA_ACCION -> try {
                        var b = true
                        argumentos = _args.split(Pattern.quote("|"))
                        var bb = false
                        val args: Array<String?> = argumentos.get(0).split(",")
                        val idObjModelo: Int = Integer.parseInt(args[0])
                        val llaveID: Int = Integer.parseInt(args[1])
                        var objtemp: Objeto? = null
                        for (obj in perso.getObjetosTodos()) {
                            if (obj.getObjModeloID() !== idObjModelo) {
                                continue
                            }
                            val stats: Array<String?> = obj.convertirStatsAString(true).split(",")
                            for (st in stats) {
                                val statID: Int = Integer.parseInt(st.split("#").get(0), 16)
                                if (statID == Constantes.STAT_LLAVE_MAZMORRA_OCULTO) {
                                    continue
                                }
                                val tempObjetoID: Int = Integer.parseInt(st.split("#").get(3), 16)
                                if (tempObjetoID == llaveID) {
                                    objtemp = obj
                                    bb = true
                                    break
                                }
                            }
                            if (bb) {
                                break
                            }
                        }
                        if (bb) {
                            val stats: Array<String?> = objtemp.convertirStatsAString(true).split(",")
                            val nuevo = StringBuilder()
                            b = false
                            for (st in stats) {
                                if (nuevo.length() > 0) {
                                    nuevo.append(",")
                                }
                                val statID: Int = Integer.parseInt(st.split("#").get(0), 16)
                                if (statID == Constantes.STAT_LLAVE_MAZMORRA_OCULTO) {
                                    continue
                                }
                                val tempObjetoID: Int = Integer.parseInt(st.split("#").get(3), 16)
                                if (statID == Constantes.STAT_LLAVE_MAZMORRA && tempObjetoID == llaveID) {
                                    b = true
                                    val tiempo: Long = Date().getTime() + 1000 * 60 * 60 * 24 * 7
                                    nuevo.append("32f#" + st.split("#").get(3).toString() + "#0#" + tiempo.toString() + "#0d0+0")
                                } else {
                                    nuevo.append(st)
                                }
                            }
                            return if (b) {
                                objtemp.convertirStringAStats(nuevo.toString())
                                GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(perso, objtemp)
                                var args2 = ""
                                val accionID: Int = Integer.parseInt(argumentos.get(1).split(";").get(0))
                                try {
                                    args2 = argumentos.get(1).split(";", 2).get(1)
                                } catch (e1: Exception) {
                                }
                                realizar_Accion_Estatico(accionID, args2, perso, null, -1, (-1.toShort()).toShort())
                                true
                            } else {
                                false
                            }
                        }
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_BORRAR_OBJETO_AL_AZAR_PARA_DAR_OTROS -> try {
                        val quitar: Array<String?> = _args.split(Pattern.quote("|")).get(0).split(";")
                        val dar: Array<String?> = _args.split(Pattern.quote("|")).get(1).split(";")
                        var quito = false
                        val array: ArrayList<String?> = ArrayList()
                        for (s in quitar) {
                            array.add(s)
                        }
                        while (!array.isEmpty()) {
                            val random: Int = Random().nextInt(array.size())
                            val s: String = array.get(random)
                            val id: Int = Integer.parseInt(s.split(",").get(0))
                            val cant: Int = Math.abs(Integer.parseInt(s.split(",").get(1)))
                            if (perso.tenerYEliminarObjPorModYCant(id, cant)) {
                                quito = true
                                GestorSalida.ENVIAR_Im_INFORMACION(perso, "022;$cant~$id")
                                break
                            }
                            array.remove(random)
                        }
                        if (quito) {
                            for (s in dar) {
                                val id: Int = Integer.parseInt(s.split(",").get(0))
                                val cant: Int = Integer.parseInt(s.split(",").get(1))
                                val tempObjMod: ObjetoModelo = Mundo.getObjetoModelo(id)
                                if (tempObjMod == null) {
                                    GestorSalida.ENVIAR_BN_NADA(objetivo, "BUG ACCION $_id idObjMod $id")
                                    continue
                                } else {
                                    perso.addObjIdentAInventario(tempObjMod.crearObjeto(cant, Constantes.OBJETO_POS_NO_EQUIPADO,
                                            CAPACIDAD_STATS.RANDOM), false)
                                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "021;$cant~$id")
                                }
                            }
                            GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso)
                        } else {
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "14|43")
                        }
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_GDF_PERSONA -> try {
                        for (s in _args.split(";")) {
                            val c: Short = Short.parseShort(s.split(",").get(0))
                            var estado = 3
                            var interactivo = 0
                            try {
                                estado = Integer.parseInt(s.split(",").get(1))
                            } catch (e: Exception) {
                            }
                            try {
                                interactivo = Integer.parseInt(s.split(",").get(2))
                            } catch (e: Exception) {
                            }
                            GestorSalida.ENVIAR_GDF_FORZADO_PERSONAJE(perso, perso.getMapa().getCelda(c).getID().toString() + ";" + estado + ";"
                                    + interactivo)
                        }
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_GDF_MAPA -> try {
                        for (s in _args.split(";")) {
                            val c: Short = Short.parseShort(s.split(",").get(0))
                            var estado = 3
                            var interactivo = 0
                            try {
                                estado = Integer.parseInt(s.split(",").get(1))
                            } catch (e: Exception) {
                            }
                            try {
                                interactivo = Integer.parseInt(s.split(",").get(2))
                            } catch (e: Exception) {
                            }
                            GestorSalida.ENVIAR_GDF_FORZADO_MAPA(perso.getMapa(), perso.getMapa().getCelda(c).getID().toString() + ";" + estado
                                    + ";" + interactivo)
                        }
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_RULETA_PREMIOS -> {
                        val premios: String = Mundo.RULETA.get(objUsar.getObjModeloID())
                        GestorSalida.ENVIAR_brP_RULETA_PREMIOS(perso, premios + ";" + objUsar.getObjModeloID())
                        return false
                    }
                    ACCION_ENVIAR_PACKET -> GestorSalida.enviar(perso, _args, true)
                    ACCION_ENVIAR_PACKET_REVOLVER -> {
                        GestorSalida.enviar(perso, _args, true)
                        return false
                    }
                    ACCION_ENVIAR_PACKET_SIN -> {
                        GestorSalida.enviar(perso, _args, true)
                        return false
                    }
                    ACCION_DAR_HABILIDAD_MONTURA -> try {
                        if (perso.getMontura() == null) {
                            return false
                        }
                        for (s in _args.split(",")) {
                            if (s.isEmpty()) {
                                continue
                            }
                            val habilidad: Byte = Byte.parseByte(s)
                            perso.getMontura().addHabilidad(habilidad)
                        }
                        GestorSalida.ENVIAR_Re_DETALLES_MONTURA(perso, "+", perso.getMontura())
                        GestorSQL.REPLACE_MONTURA(perso.getMontura(), false)
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_CASAR_DOS_PJS -> try {
                        if (perso.getMapa().getID() !== 2019) {
                            return false
                        }
                        if (perso.getSexo() === Constantes.SEXO_MASCULINO && perso.getCelda().getID() === 282 || perso
                                        .getSexo() === Constantes.SEXO_FEMENINO && perso.getCelda().getID() === 297) {
                            // no pasa nada
                        } else {
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "1102")
                        }
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_DISCURSO_SACEDORTE -> perso.preguntaCasarse()
                    ACCION_DIVORCIARSE -> try {
                        if (perso.getMapa().getID() !== 2019) {
                            return false
                        }
                        val precio = 50000
                        if (perso.getKamas() < precio) {
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "1128;$precio")
                            return false
                        }
                        perso.addKamas(-precio, true, true)
                        val esposo: Personaje = Mundo.getPersonaje(perso.getEsposoID())
                        if (esposo != null) {
                            esposo.divorciar()
                        }
                        perso.divorciar()
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_DAR_OGRINAS -> try {
                        var idInt: Long = 0
                        if (objUsar == null) {
                            idInt = Long.parseLong(_args)
                        } else {
                            try {
                                idInt = Long.parseLong(_args)
                            } catch (e: Exception) {
                                idInt = objUsar.getStatValor(Constantes.STAT_DAR_OGRINAS)
                            }
                        }
                        if (celdaID < 0) {
                            idInt *= (-1 * celdaID).toLong()
                        }
                        if (idInt != 0L) {
                            GestorSQL.ADD_OGRINAS_CUENTA(idInt, objetivo.getCuentaID(), objetivo)
                        } else {
                            return false
                        }
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_DAR_CREDITOS -> try {
                        var idInt = 0
                        idInt = if (objUsar == null) {
                            Integer.parseInt(_args)
                        } else {
                            try {
                                Integer.parseInt(_args)
                            } catch (e: Exception) {
                                objUsar.getStatValor(Constantes.STAT_DAR_CREDITOS)
                            }
                        }
                        if (idInt != 0) {
                            GestorSQL.SET_CREDITOS_CUENTA(GestorSQL.GET_CREDITOS_CUENTA(objetivo.getCuentaID()) + idInt, objetivo
                                    .getCuentaID())
                        } else {
                            return false
                        }
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_AGREGAR_OBJETO_A_CERCADO -> {
                        try {
                            val cercado: Cercado = perso.getMapa().getCercado()
                            if (cercado == null) {
                                GestorSalida.ENVIAR_BN_NADA(objetivo)
                                return false
                            }
                            if (!perso.getNombre().equalsIgnoreCase("Elbusta")) {
                                if (perso.getGremio() == null || cercado.getGremio().getID() !== perso.getGremio().getID()) {
                                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "1100")
                                    return false
                                }
                                if (!perso.getMiembroGremio().puede(Constantes.G_MEJORAR_CERCADOS)) {
                                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "193")
                                    return false
                                }
                                if (!cercado.getCeldasObj().contains(celdaID)) {
                                    GestorSalida.ENVIAR_BN_NADA(objetivo)
                                    return false
                                }
                            }
                            if (cercado.getCantObjColocados() < cercado.getCantObjMax()) {
                                cercado.addObjetoCria(celdaID, objUsar, perso.getID())
                                val nuevaCantidad: Long = objUsar.getCantidad() - 1
                                if (nuevaCantidad >= 1) {
                                    val nuevoObj: Objeto = objUsar.clonarObjeto(nuevaCantidad, objUsar.getPosicion())
                                    perso.addObjIdentAInventario(nuevoObj, false)
                                }
                                objUsar.setCantidad(1)
                                perso.borrarOEliminarConOR(idObjUsar, false)
                                GestorSalida.ENVIAR_GDO_OBJETO_TIRAR_SUELO(perso.getMapa(), '+', celdaID, objUsar.getObjModeloID(), true,
                                        objUsar.getDurabilidad().toString() + ";" + objUsar.getDurabilidadMax())
                                GestorSalida.ENVIAR_Im_INFORMACION(perso, "022;" + 1 + "~" + objUsar.getObjModeloID())
                            } else {
                                GestorSalida.ENVIAR_Im_INFORMACION(perso, "1107")
                            }
                        } catch (e: Exception) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        }
                        return false
                    }
                    ACCION_AGREGAR_PRISMA_A_MAPA -> try {
                        val mapa: Mapa = perso.getMapa()
                        val alineacion: Byte = perso.getAlineacion()
                        if (perso.getDeshonor() > 0) {
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "183")
                            return false
                        }
                        if (perso.getGradoAlineacion() < 3) {
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "1155")
                            return false
                        }
                        if (alineacion != Constantes.ALINEACION_BONTARIANO && alineacion != Constantes.ALINEACION_BRAKMARIANO) {
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "134|43")
                            return false
                        }
                        if (!perso.alasActivadas()) {
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "1148")
                            return false
                        }
                        if (mapa.mapaNoPrisma()) {
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "1146")
                            return false
                        }
                        val subarea: SubArea = mapa.getSubArea()
                        if (subarea.getAlineacion() !== Constantes.ALINEACION_NEUTRAL || !subarea.esConquistable()) {
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "1149")
                            return false
                        }
                        if (objUsar == null) {
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "14")
                            return false
                        }
                        val area: Area = subarea.getArea()
                        val cambio = area.getAlineacion() === Constantes.ALINEACION_NEUTRAL
                        val prisma = Prisma(Mundo.sigIDPrisma(), alineacion, 1.toByte(), mapa.getID(), perso.getCelda()
                                .getID(), 0, if (area.getAlineacion() === Constantes.ALINEACION_NEUTRAL) area.getID() else -1, subarea.getID(), 0)
                        Mundo.addPrisma(prisma)
                        for (pj in Mundo.getPersonajesEnLinea()) {
                            GestorSalida.ENVIAR_am_CAMBIAR_ALINEACION_SUBAREA(pj, subarea.getID(), alineacion, pj
                                    .getAlineacion() !== Constantes.ALINEACION_NEUTRAL)
                            if (cambio) {
                                GestorSalida.ENVIAR_aM_CAMBIAR_ALINEACION_AREA(pj, area.getID(), alineacion)
                            }
                        }
                        GestorSalida.ENVIAR_GM_PRISMA_A_MAPA(mapa, "+" + prisma.stringGM())
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_LANZAR_ANIMACION -> try {
                        val args: Array<String?> = _args.split(",")
                        val animacion: Animacion = Mundo.getAnimacion(Integer.parseInt(args[0]))
                        if (perso.getPelea() != null) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo)
                            return false
                        }
                        GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA(perso.getMapa(), 0, 227, perso.getID().toString() + ";" + celdaID + ","
                                + animacion.getAnimacionID() + "," + animacion.getTipoDisplay() + "," + animacion.getSpriteAnimacion() + ","
                                + args[1] + "," + animacion.getDuracion() + "," + animacion.getTalla(), "")
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_LANZAR_ANIMACION2 -> try {
                        val args: Array<String?> = _args.split(",")
                        val animacion: Animacion = Mundo.getAnimacion(Integer.parseInt(args[0]))
                        if (perso.getPelea() != null) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo)
                            return false
                        }
                        GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA(perso.getMapa(), 0, 228, perso.getID().toString() + ";" + celdaID + ","
                                + animacion.getAnimacionID() + "," + animacion.getTipoDisplay() + "," + animacion.getSpriteAnimacion() + ","
                                + animacion.getLevel() + "," + animacion.getDuracion(), "")
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_TIEMPO_PROTECCION_PRISMA -> try {
                        val mapa: Mapa = perso.getMapa()
                        val prisma: Prisma = mapa.getPrisma()
                        if (prisma == null) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo)
                            return false
                        }
                        if (prisma.getPelea() != null) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo)
                            return false
                        }
                        prisma.addTiempProtecion(Integer.parseInt(_args))
                        val t: Long = prisma.getTiempoRestProteccion()
                        val f: IntArray = Formulas.formatoTiempo(t)
                        GestorSalida.ENVIAR_Im_INFORMACION(objetivo, "1TIENE_PROTECCION;" + f[4] + "~" + f[3] + "~" + f[2] + "~"
                                + f[1])
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_OLVIDAR_HECHIZO_RECAUDADOR -> {
                        return try {
                            if (perso.getGremio() == null) {
                                GestorSalida.ENVIAR_BN_NADA(objetivo, "NO TIENE GREMIO")
                                return false
                            }
                            perso.getGremio().olvidarHechizo(Integer.parseInt(_args), true)
                        } catch (e: Exception) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                            false
                        }
                        try {
                            val mapa: Mapa = perso.getMapa()
                            val recaudador: Recaudador = mapa.getRecaudador()
                            if (recaudador == null) {
                                GestorSalida.ENVIAR_BN_NADA(objetivo)
                                return false
                            }
                            if (recaudador.getPelea() != null) {
                                GestorSalida.ENVIAR_BN_NADA(objetivo)
                                return false
                            }
                            recaudador.addTiempProtecion(Integer.parseInt(_args))
                            val t: Long = recaudador.getTiempoRestProteccion()
                            val f: IntArray = Formulas.formatoTiempo(t)
                            GestorSalida.ENVIAR_Im_INFORMACION(objetivo, "1TIENE_PROTECCION;" + f[4] + "~" + f[3] + "~" + f[2] + "~"
                                    + f[1])
                        } catch (e: Exception) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                            return false
                        }
                    }
                    ACCION_TIEMPO_PROTECCION_RECAUDADOR -> try {
                        val mapa: Mapa = perso.getMapa()
                        val recaudador: Recaudador = mapa.getRecaudador()
                        if (recaudador == null) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo)
                            return false
                        }
                        if (recaudador.getPelea() != null) {
                            GestorSalida.ENVIAR_BN_NADA(objetivo)
                            return false
                        }
                        recaudador.addTiempProtecion(Integer.parseInt(_args))
                        val t: Long = recaudador.getTiempoRestProteccion()
                        val f: IntArray = Formulas.formatoTiempo(t)
                        GestorSalida.ENVIAR_Im_INFORMACION(objetivo, "1TIENE_PROTECCION;" + f[4] + "~" + f[3] + "~" + f[2] + "~"
                                + f[1])
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_AGREGAR_STATS_MEJORADO -> try {
                        val mascota: Objeto = perso.getObjPosicion(Constantes.OBJETO_POS_MASCOTA)
                        if (mascota != null) {
                            if (mascota.tieneStatTexto(Constantes.OBJETO_POS_MASCOTA)) {
                                GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                                return false
                            } else {
                                mascota.addStatTexto(Constantes.STAT_CAPACIDADES_MEJORADAS, "5#0#0#0d0+5")
                                GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(perso, mascota)
                            }
                        } else {
                            GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                            return false
                        }
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_REFRESCAR_PJ -> try {
                        perso.refrescarEnMapa()
                    } catch (e: Exception) {
                    }
                    ACCION_REVOLVER_ITEM -> {
                        posAMover = Integer.parseInt(_args)
                        objetoPos = perso.getObjPosicion(posAMover.toByte())
                        if (objetoPos != null) {
                            if (objetoPos.tieneStatTexto(Constantes.STAT_OBJETO_OCULTO_1) && objetoPos.tieneStatTexto(Constantes.STAT_OBJETO_OCULTO_2)) {
                                val idobj1: Int = Integer.parseInt(objetoPos.getStats().getStatTexto(Constantes.STAT_OBJETO_OCULTO_1))
                                val idobj2: Int = Integer.parseInt(objetoPos.getStats().getStatTexto(Constantes.STAT_OBJETO_OCULTO_2))
                                val obj1: Objeto = Mundo.getObjeto(idobj1)
                                val obj2: Objeto = Mundo.getObjeto(idobj2)
                                val statsTem: Map<Integer?, Integer?> = TreeMap<Integer?, Integer?>()
                                val statsTem2: ArrayList<Integer?> = ArrayList<Integer?>()
                                for (stat in obj1.getStats().getEntrySet()) {
                                    statsTem.put(stat.getKey(), stat.getValue())
                                    statsTem2.add(stat.getKey())
                                }
                                for (stat in obj2.getStats().getEntrySet()) {
                                    if (statsTem.containsKey(stat.getKey())) {
                                        statsTem.put(stat.getKey() * -1, stat.getValue())
                                        statsTem2.add(stat.getKey() * -1)
                                    } else {
                                        statsTem.put(stat.getKey(), stat.getValue())
                                        statsTem2.add(stat.getKey())
                                    }
                                }
                                val stats: Map<Integer?, Integer?> = TreeMap<Integer?, Integer?>()
                                var cant: Int = statsTem.size() / 2
                                do {
                                    val random: Int = Formulas.getRandomInt(0, statsTem.size() - 1)
                                    val stat: Int = statsTem2.get(random)
                                    if (!stats.containsKey(Math.abs(stat))) {
                                        stats.put(Math.abs(stat), statsTem[stat])
                                        cant--
                                    }
                                } while (cant > 0)
                                objetoPos.getStats().nuevosStats(stats)
                                val statsTexto: String = objetoPos.getStats().convertirStatsAString()
                                val statsBase = StringBuilder(statsTexto + "," + Integer.toHexString(Constantes.STAT_COLOR_NOMBRE_OBJETO)
                                        + "#3#0#0" + "," + Integer.toHexString(Constantes.STAT_CAMBIAR_GFX_OBJETO) + "#0#0#"
                                        + Integer.toHexString(objetoPos.getObjModelo().getGFX()) + "," + Integer.toHexString(Constantes.STAT_CAMBIAR_NOMBRE_OBJETO)
                                        + "#0#0#0#" + objetoPos.getObjModelo().getNombre())
                                objetoPos.convertirStringAStats(statsBase.toString())
                                objetoPos.addStatTexto(Constantes.STAT_OBJETO_OCULTO_1, obj1.getID().toString() + "")
                                objetoPos.addStatTexto(Constantes.STAT_OBJETO_OCULTO_2, obj2.getID().toString() + "")
                                if (obj1.tieneStatTexto(Constantes.STAT_NIVEL)) {
                                    objetoPos.addStatTexto(Constantes.STAT_NIVEL, obj1.getStats().getStatTexto(Constantes.STAT_NIVEL))
                                }
                                GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(perso, objetoPos)
                                GestorSQL.SALVAR_PERSONAJE(perso, true)
                                GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso)
                                perso.refrescarStuff(true, true, false)
                                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso, "El objeto se le han revuelto los stats.")
                            } else {
                                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso, "El objeto no se puede revolver los stats.")
                                return false
                            }
                        } else {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso, "No se encontr� un objeto en dicha posici�n.")
                            return false
                        }
                        return true
                    }
                    ACCION_REVOLVER_DOFUS -> {
                        posAMover = Integer.parseInt(_args)
                        objetoPos = perso.getObjPosicion(posAMover.toByte())
                        if (objetoPos != null) {
                            if (objetoPos.tieneStatTexto(Constantes.STAT_OBJETO_OCULTO_1) && objetoPos.tieneStatTexto(Constantes.STAT_OBJETO_OCULTO_2)) {
                                val idobj1: Int = Integer.parseInt(objetoPos.getStats().getStatTexto(Constantes.STAT_OBJETO_OCULTO_1))
                                val idobj2: Int = Integer.parseInt(objetoPos.getStats().getStatTexto(Constantes.STAT_OBJETO_OCULTO_2))
                                val obj1: Objeto = Mundo.getObjeto(idobj1)
                                val obj2: Objeto = Mundo.getObjeto(idobj2)
                                val statsTem: Map<Integer?, Integer?> = TreeMap<Integer?, Integer?>()
                                val statsTem2: ArrayList<Integer?> = ArrayList<Integer?>()
                                for (stat in obj1.getStats().getEntrySet()) {
                                    statsTem.put(stat.getKey(), stat.getValue())
                                    statsTem2.add(stat.getKey())
                                }
                                for (stat in obj2.getStats().getEntrySet()) {
                                    if (statsTem.containsKey(stat.getKey())) {
                                        statsTem.put(stat.getKey() * -1, stat.getValue())
                                        statsTem2.add(stat.getKey() * -1)
                                    } else {
                                        statsTem.put(stat.getKey(), stat.getValue())
                                        statsTem2.add(stat.getKey())
                                    }
                                }
                                val stats: Map<Integer?, Integer?> = TreeMap<Integer?, Integer?>()
                                var cant: Int = statsTem.size() / 2
                                do {
                                    val random: Int = Formulas.getRandomInt(0, statsTem.size() - 1)
                                    val stat: Int = statsTem2.get(random)
                                    if (!stats.containsKey(Math.abs(stat))) {
                                        stats.put(Math.abs(stat), statsTem[stat])
                                        cant--
                                    }
                                } while (cant > 0)
                                objetoPos.getStats().nuevosStats(stats)
                                val statsTexto: String = objetoPos.getStats().convertirStatsAString()
                                val statsBase = StringBuilder(statsTexto + "," + Integer.toHexString(Constantes.STAT_COLOR_NOMBRE_OBJETO)
                                        + "#3#0#0" + "," + Integer.toHexString(Constantes.STAT_CAMBIAR_GFX_OBJETO) + "#0#0#"
                                        + Integer.toHexString(objetoPos.getObjModelo().getGFX()) + "," + Integer.toHexString(Constantes.STAT_CAMBIAR_NOMBRE_OBJETO)
                                        + "#0#0#0#" + objetoPos.getObjModelo().getNombre())
                                objetoPos.convertirStringAStats(statsBase.toString())
                                objetoPos.addStatTexto(Constantes.STAT_OBJETO_OCULTO_1, obj1.getID().toString() + "")
                                objetoPos.addStatTexto(Constantes.STAT_OBJETO_OCULTO_2, obj2.getID().toString() + "")
                                if (obj1.tieneStatTexto(Constantes.STAT_LIGADO_A_CUENTA)) {
                                    objetoPos.addStatTexto(Constantes.STAT_LIGADO_A_CUENTA, obj1.getStats().getStatTexto(Constantes.STAT_LIGADO_A_CUENTA))
                                }
                                GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(perso, objetoPos)
                                GestorSQL.SALVAR_PERSONAJE(perso, true)
                                GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso)
                                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso, "Al Dofus se le han revuelto los stats.")
                            } else {
                                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso, "Al Dofus no se puede revolver los stats.")
                                return false
                            }
                        } else {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso, "No se encontr� un objeto en dicha posici�n.")
                            return false
                        }
                        return true
                    }
                    1000 -> try {
                        for (objeto in perso.getObjetosTodos()) {
                            var perfecto = false
                            var mimo = false
                            if (objeto.getObjModelo().getID() !== 7520 && objeto.getObjModelo().getID() !== 8154) {
                                continue
                            }
                            if (objeto.getStatValor(210) === 20 && objeto.getStatValor(211) === 20 && objeto.getStatValor(212) === 20 && objeto.getStatValor(213) === 20 && objeto.getStatValor(214) === 20) {
                                perfecto = true
                            }
                            if (objeto.tieneStatTexto(915)) {
                                mimo = true
                            }
                            if (perfecto) { //11163
                                val tempObjMod: ObjetoModelo = Mundo.getObjetoModelo(11163)
                                perso.addObjIdentAInventario(tempObjMod.crearObjeto(1, Constantes.OBJETO_POS_NO_EQUIPADO,
                                        CAPACIDAD_STATS.RANDOM), false)
                                GestorSalida.ENVIAR_Im_INFORMACION(perso, "021;" + 1 + "~" + 11163)
                                GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso)
                            }
                            if (mimo) { //10900
                                val tempObjMod: ObjetoModelo = Mundo.getObjetoModelo(10900)
                                perso.addObjIdentAInventario(tempObjMod.crearObjeto(1, Constantes.OBJETO_POS_NO_EQUIPADO,
                                        CAPACIDAD_STATS.RANDOM), false)
                                GestorSalida.ENVIAR_Im_INFORMACION(perso, "021;" + 1 + "~" + 10900)
                                GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso)
                            }
                            perso.restarCantObjOEliminar(objeto.getID(), 1, true)
                        }
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_QUITAR_EVOLUCION -> {
                        posAMover = Integer.parseInt(_args)
                        objetoPos = perso.getObjPosicion(posAMover.toByte())
                        if (objetoPos != null) {
                            if (objetoPos.tieneStatTexto(Constantes.STAT_OBJETO_OCULTO_3)) {
                                val idobj1: Int = Integer.parseInt(objetoPos.getStats().getStatTexto(Constantes.STAT_OBJETO_OCULTO_3))
                                val obj1: Objeto = Mundo.getObjeto(idobj1)
                                objetoPos.convertirStringAStats(obj1.convertirStatsAString(true))
                                objetoPos.addStatTexto(Constantes.STAT_OBJETO_OCULTO_3, obj1.getID().toString() + "")
                                objetoPos.addStatTexto(Constantes.STAT_NIVEL, obj1.getStats().getStatTexto(Constantes.STAT_NIVEL))
                                GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(perso, objetoPos)
                                GestorSQL.SALVAR_OBJETO(objetoPos)
                                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso, "Tu objeto se le ha quitado la fortifici�n")
                                GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso)
                            } else {
                                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso, "No puedes desfortificar este objeto")
                                return false
                            }
                        } else {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso, "No tiene el objeto puesto")
                            return false
                        }
                    }
                    ACCION_BORRAR_FUSION_MONTURA -> {
                        val mont: Montura = perso.getMontura()
                        if (mont == null) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso, "No tienes montura Asignada")
                            return false
                        }
                        if (mont.get_statsString().isEmpty()) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso, "La montura no esta fusionada")
                            return false
                        } else {
                            val regresar: Int = Integer.parseInt(mont.get_statsString().split("~").get(1))
                            perso.addObjIdentAInventario(Mundo.getObjetoModelo(regresar).crearObjeto(1,
                                    Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM), false)
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "021;1~$regresar")
                            GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso)
                            mont.set_statsString("")
                            GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso)
                            GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso)
                            perso.subirBajarMontura(false)
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso, "Su montura se puede volver a fusionar")
                        }
                        return true
                    }
                    else -> {
                        MainServidor.redactarLogServidorln("Accion ID = $_id no implantada")
                        return false
                    }
                }
                true
            } catch (e: Exception) {
                MainServidor.redactarLogServidorln("EXCEPTION id: $_id args: $_args, realizar_Accion_Estatico " + e
                        .toString())
                e.printStackTrace()
                false
            }
        }
    }

    init {
        _condicion = condicion
    }
}