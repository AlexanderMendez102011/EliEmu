package estaticos

import java.util.ArrayList

object Comandos {
    fun consolaComando(mensaje: String?, _cuenta: Cuenta?, _perso: Personaje?) {
        try {
            val infos: Array<String?> = mensaje.split(" ")
            val comando: String = infos[0].toUpperCase()
            val rangoJugador: Int = _perso.getAdmin()
            val rangoComando: Int = Mundo.getRangoComando(comando)
            if (rangoComando == -1) {
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Commande non reconnue: $comando")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Comando no reconocido: $comando")
                }
                return
            }
            if (rangoComando <= rangoJugador) {
                GM_lvl_5(comando, infos, mensaje, _cuenta, _perso)
                GestorSQL.INSERT_COMANDO_GM(_perso.getNombre(), mensaje)
            } else {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1DONT_RANGE_GM")
            }
            /*if(rangoJugador>5)rangoJugador=5;
			switch (rangoJugador) {
				case 1 :
					GM_lvl_1(comando, infos, mensaje, _cuenta, _perso);
					break;
				case 2 :
					GM_lvl_2(comando, infos, mensaje, _cuenta, _perso);
					break;
				case 3 :
					GM_lvl_3(comando, infos, mensaje, _cuenta, _perso);
					break;
				case 4 :
					GM_lvl_4(comando, infos, mensaje, _cuenta, _perso);
					break;
				case 5 :
					GM_lvl_5(comando, infos, mensaje, _cuenta, _perso);
					break;
				default :
					GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1DONT_RANGE_GM");
					return;
			}*/
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun GM_lvl_1(comando: String?, infos: Array<String?>?, mensaje: String?, _cuenta: Cuenta?,
                 _perso: Personaje?) {
        var infos = infos
        var objetivo: Personaje? = null
        var numInt = -1
        var mapaID = -1
        var celdaID: Short = -1
        var numInts = 1
        var numShort: Short = 1
        val strB = StringBuilder()
        var mapa: Mapa = _perso.getMapa()
        when (comando.toUpperCase()) {
            "CELDA_A_HASH" -> try {
                celdaID = Short.parseShort(infos!![1])
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "CeldaID: $celdaID  HASH: " + Encriptador.celdaIDAHash(
                        celdaID))
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incompletos")
            }
            "HASH_A_CELDA" -> try {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "HASH: " + infos!![1] + "  CeldaID: " + Encriptador.hashACeldaID(
                        infos[1]))
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incompletos")
            }
            "INFO_NPC" -> try {
                val npcMod: NPCModelo = Mundo.getNPCModelo(Integer.parseInt(infos!![1]))
                if (npcMod == null) {
                    GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(_perso, "NPC NO EXISTE")
                } else {
                    GestorSalida.enviar(_perso, "bp" + npcMod.getSexo().toString() + "," + npcMod.getTallaX().toString() + "," + npcMod.getTallaY()
                            .toString() + "," + npcMod.getGfxID().toString() + "," + npcMod.getColor1().toString() + "," + npcMod.getColor2().toString() + "," + npcMod.getColor3()
                            .toString() + "," + npcMod.getAccesoriosInt(), true)
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incompletos")
            }
            "RATES" -> _perso.mostrarRates()
            "CONGELAR", "FREEZE" -> {
                objetivo = if (infos!!.size > 1) {
                    Mundo.getPersonajePorNombre(infos[1])
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Arguments incorrects")
                    return
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                objetivo.setInmovil(true)
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.getNombre().toString() + " a été freeze.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ha sido inmovilizado el personaje " + objetivo.getNombre())
                }
            }
            "DESCONGELAR", "UN_FREEZE", "UNFREEZE" -> {
                objetivo = if (infos!!.size > 1) {
                    Mundo.getPersonajePorNombre(infos[1])
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Arguments incorrects")
                    return
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                objetivo.setInmovil(false)
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.getNombre().toString() + " peut désormais bouger.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ha sido movilizado el personaje " + objetivo.getNombre())
                }
            }
            "CONGELAR_MAPA", "FREEZE_MAP" -> {
                if (infos!!.size > 1) {
                    mapa = Mundo.getMapa(Short.parseShort(infos[1]))
                }
                for (objetivos in mapa.getArrayPersonajes()) {
                    if (objetivos.getAdmin() > 0) {
                        continue
                    }
                    objetivos.setInmovil(true)
                }
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Tous les joueurs présents sur la MAP " + mapa.getID()
                            .toString() + " ont été freeze.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Han sido inmovilizados todos los personajes del mapa " + mapa
                            .getID())
                }
            }
            "DESCONGELAR_MAPA", "UN_FREEZE_MAP", "UNFREEZE_MAP" -> {
                if (infos!!.size > 1) mapa = Mundo.getMapa(Short.parseShort(infos[1]))
                for (objetivos in mapa.getArrayPersonajes()) {
                    objetivos.setInmovil(false)
                }
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Les joueurs de cette map ont été défreeze.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Han sido movilizados todos los personajes del mapa " + mapa
                            .getID())
                }
            }
            "MUTEAR_MAPA", "MUTE_MAPA", "MUTE_MAP" -> {
                if (infos!!.size > 1) mapa = Mundo.getMapa(Short.parseShort(infos[1]))
                mapa.setMuteado(true)
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Map mutée.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ha sido muteado el mapa " + mapa.getID())
                }
            }
            "DES_MUTEAR_MAPA", "DESMUTEAR_MAPA", "DES_MUTE_MAP", "UN_MUTE_MAP", "DESMUTE_MAP" -> {
                try {
                    mapa = Mundo.getMapa(Short.parseShort(infos!![1]))
                } catch (e: Exception) {
                }
                mapa.setMuteado(false)
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Map unmute.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ha sido desmuteado el mapa " + mapa.getID())
                }
            }
            "MUTE_SEGUNDOS", "MUTE_SECONDS", "MUTEAR", "SILENCIAR", "MUTE" -> {
                objetivo = if (infos!!.size > 1) {
                    Mundo.getPersonajePorNombre(infos[1])
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Arguments incorrects")
                    return
                }
                var motivo: String? = ""
                try {
                    if (infos.size > 2) {
                        numInt = Integer.parseInt(infos[2])
                    }
                } catch (e: Exception) {
                }
                try {
                    if (infos.size > 3) {
                        infos = mensaje.split(" ", 4)
                        motivo = infos[3]
                    }
                } catch (e: Exception) {
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                if (numInt < 0) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La durée de mute est invalide.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La duracion es invalida.")
                    }
                    return
                }
                objetivo.getCuenta().mutear(true, numInt)
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.getNombre().toString() + " a été mute pour " + numInt
                            .toString() + " secondes.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ha sido mute " + objetivo.getNombre().toString() + " por " + numInt
                            .toString() + " segundos.")
                }
                GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS("1JUGADOR_MUTEAR;" + objetivo.getNombre().toString() + "~" + (numInt / 60).toString() + "~"
                        + motivo)
                if (objetivo.getPelea() != null || objetivo.getTutorial() != null || objetivo.estaExchange() || objetivo
                                .estaInmovil()) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso,
                            "Le joueur est en combat ou en craft, impossible de le TP en prison.")
                    return
                }
                objetivo.modificarA(Personaje.RA_PUEDE_USAR_OBJETOS, 0 xor Personaje.RA_PUEDE_USAR_OBJETOS)
                GestorSalida.ENVIAR_AR_RESTRICCIONES_PERSONAJE(objetivo)
                val celdas = shortArrayOf(127, 119, 359, 351)
                objetivo.teleport(666.toShort(), celdas[Formulas.getRandomInt(0, 3)])
                objetivo.setCalabozo(true)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El jugador " + objetivo.getNombre().toString() + " a sido enviado a la prisión")
            }
            "UN_MUTE", "DESMUTE", "UNMUTE" -> {
                objetivo = if (infos!!.size > 1) {
                    Mundo.getPersonajePorNombre(infos[1])
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Arguments incorrects")
                    return
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                objetivo.getCuenta().mutear(false, 0)
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur peut désormais parler.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El jugador " + objetivo.getNombre().toString() + " ha sido desmuteado")
                }
            }
            "CARCEL", "JAIL" -> {
                objetivo = if (infos!!.size > 1) {
                    Mundo.getPersonajePorNombre(infos[1])
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Arguments incorrects")
                    return
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                if (objetivo.getPelea() != null || objetivo.getTutorial() != null || objetivo.estaExchange() || objetivo
                                .estaInmovil()) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso,
                            "Le joueur est en combat ou en craft, impossible de le TP en prison.")
                    return
                }
                objetivo.modificarA(Personaje.RA_PUEDE_USAR_OBJETOS, 0 xor Personaje.RA_PUEDE_USAR_OBJETOS)
                GestorSalida.ENVIAR_AR_RESTRICCIONES_PERSONAJE(objetivo)
                val celdas2 = shortArrayOf(127, 119, 359, 351)
                objetivo.teleport(666.toShort(), celdas2[Formulas.getRandomInt(0, 3)])
                objetivo.setCalabozo(true)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.getNombre().toString() + " a été envoyé en prison")
            }
            "UNJAIL", "LIBERAR", "UN_JAIL" -> {
                objetivo = if (infos!!.size > 1) {
                    Mundo.getPersonajePorNombre(infos[1])
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Arguments incorrects")
                    return
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                objetivo.modificarA(Personaje.RA_PUEDE_USAR_OBJETOS, Personaje.RA_PUEDE_USAR_OBJETOS
                        xor Personaje.RA_PUEDE_USAR_OBJETOS)
                GestorSalida.ENVIAR_AR_RESTRICCIONES_PERSONAJE(objetivo)
                objetivo.setCalabozo(false)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.getNombre().toString() + " a été libéré.")
            }
            "TAMAÑO", "TALLA", "SIZE" -> {
                try {
                    if (infos!!.size > 1) numShort = Short.parseShort(infos[1])
                } catch (e: Exception) {
                }
                if (numShort < 0) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Taille invalide")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Talla invalida")
                    }
                    return
                }
                objetivo = _perso
                if (infos!!.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                    if (objetivo == null) {
                        if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                        }
                        return
                    }
                }
                objetivo.setTalla(numShort)
                objetivo.refrescarEnMapa()
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La taille du joueur " + objetivo.getNombre().toString() + " a été modifiée")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La talla del personaje " + objetivo.getNombre()
                            .toString() + " ha sido modificada")
                }
            }
            "INVISIBLE", "INDETECTABLE" -> {
                _perso.setIndetectable(true)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Has entrado al estado INDETECTABLE")
            }
            "VISIBLE", "DETECTABLE" -> {
                _perso.setIndetectable(false)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Has salido al estado INDETECTABLE")
            }
            "GFXID", "FORMA", "MORPH" -> {
                try {
                    if (infos!!.size > 1) numInts = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                }
                if (numInts < 0) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Gfx ID invalide")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Gfx ID invalida")
                    }
                    return
                }
                objetivo = _perso
                if (infos!!.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                    if (objetivo == null) {
                        if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                        }
                        return
                    }
                }
                objetivo.setGfxID(numInts, true)
                objetivo.refrescarEnMapa()
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.getNombre().toString() + " a changé d'apparence.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje " + objetivo.getNombre()
                            .toString() + " a cambiado de apariencia")
                }
            }
            "INFO", "INFOS" -> try {
                var enLinea: Long = ServidorServer.getSegundosON() * 1000
                val dia = (enLinea / 86400000L).toInt()
                enLinea %= 86400000L
                val hora = (enLinea / 3600000L).toInt()
                enLinea %= 3600000L
                val minuto = (enLinea / 60000L).toInt()
                enLinea %= 60000L
                val segundo = (enLinea / 1000L).toInt()
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "====================\n" + MainServidor.NOMBRE_SERVER
                            .toString() + " (ELBUSTEMU " + Constantes.VERSION_EMULADOR + Constantes.SUBVERSION_EMULADOR.toString() + ")\n\nUptime: " + dia.toString() + "j " + hora.toString() + "h " + minuto.toString() + "m "
                            + segundo.toString() + "s\n" + "Joueurs en ligne: " + ServidorServer.nroJugadoresLinea().toString() + "\n" + "Record de connexions: " + ServidorServer.getRecordJugadores().toString() + "\n" + "====================")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "====================\n" + MainServidor.NOMBRE_SERVER
                            .toString() + " (ELBUSTEMU " + Constantes.VERSION_EMULADOR + Constantes.SUBVERSION_EMULADOR.toString() + ")\n\nEnLínea: " + dia.toString() + "d " + hora.toString() + "h " + minuto.toString() + "m "
                            + segundo.toString() + "s\n" + "Jugadores en línea: " + ServidorServer.nroJugadoresLinea().toString() + "\n" + "IP unicas: " + ServidorServer.getUniqueIP().toString() + "\n" + "Record de conexión: " + ServidorServer.getRecordJugadores().toString() + "\n" + "====================")
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio un error")
                e.printStackTrace()
                return
            }
            "REFRESCAR_MOBS", "REFRESH_MOBS" -> {
                _perso.getMapa().refrescarGrupoMobs()
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Mobs respawns.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Mobs Refrescados")
                }
            }
            "INFO_MAP", "INFO_MAPA", "MAPA_INFO", "MAP_INFOS", "MAPA_INFOS", "INFOS_MAPA", "INFOS_MAP", "MAP_INFO" -> {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "====================")
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "MAP ID: " + mapa.getID().toString() + " [" + mapa.getX().toString() + ", " + mapa.getY()
                        .toString() + "]")
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Liste des PNJS sur la map:")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Lista de NPC del mapa:")
                }
                mapa = _perso.getMapa()
                for (npc in mapa.getNPCs().values()) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "ID: " + npc.getID().toString() + " - Template: " + npc.getModelo().getID()
                                .toString() + " - Nom: " + npc.getModelo().getNombre().toString() + " - Case: " + npc.getCeldaID().toString() + " - Question: " + npc
                                .getModelo().getPreguntaID(null))
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "ID: " + npc.getID().toString() + " - Modelo: " + npc.getModelo().getID()
                                .toString() + " - Nombre: " + npc.getModelo().getNombre().toString() + " - Celda: " + npc.getCeldaID().toString() + " - Pregunta: " + npc
                                .getModelo().getPreguntaID(null))
                    }
                }
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Liste des groupes de monstres sur la map:")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Lista de los grupos de mounstros:")
                }
                for (gm in mapa.getGrupoMobsTotales().values()) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "ID: " + gm.getID().toString() + " - Case ID: " + gm.getCeldaID()
                                .toString() + " - Monstres: " + gm.getStrGrupoMob().toString() + " - Quantité: " + gm.getCantMobs().toString() + " - Type: " + gm.getTipo()
                                .toString() + " - Kamas: " + gm.getKamasHeroico().toString() + " - ItemsID: " + gm.getIDsObjeto())
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "ID: " + gm.getID().toString() + " - CeldaID: " + gm.getCeldaID()
                                .toString() + " - StringMob: " + gm.getStrGrupoMob().toString() + " - Cantidad: " + gm.getCantMobs().toString() + " - Tipo: " + gm.getTipo()
                                .toString() + " - Kamas: " + gm.getKamasHeroico().toString() + " - ObjetosID: " + gm.getIDsObjeto())
                    }
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "====================")
            }
            "CANT_SALVANDO", "SAVE_TIMES" -> if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le serveur a été sauvegardé " + Mundo.CANT_SALVANDO.toString() + " fois.")
            } else {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El salvado del servidor esta en el " + Mundo.CANT_SALVANDO)
            }
            "EN_LINEA", "ONLINE", "JUGADORES", "PLAYERS", "JOUERS", "QUIENES", "WHOIS" -> {
                var maximo = 50
                try {
                    maximo = Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                }
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "====================\nListe de joueur en ligne:")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "====================\nLista de los jugadores en línea:")
                }
                var players = 0
                for (ep in ServidorServer.getClientes()) {
                    objetivo = try {
                        ep.getPersonaje()
                    } catch (e: Exception) {
                        continue
                    }
                    players++
                    if (players >= maximo) {
                        continue
                    }
                    if (strB.length() > 0) {
                        strB.append("\n")
                    }
                    if (ep.getCuenta() == null) {
                        strB.append("Socket sin loguear cuenta - IP: " + ep.getActualIP())
                        continue
                    }
                    if (objetivo == null) {
                        strB.append("Cuenta sin loguear personaje - Cuenta: " + ep.getCuenta().getNombre().toString() + " IP: " + ep
                                .getActualIP())
                        continue
                    }
                    if (!objetivo.enLinea()) {
                        strB.append("Personaje Offline: " + objetivo.getNombre().toString() + "Cuenta: " + ep.getCuenta().getNombre().toString() + " IP: "
                                + ep.getActualIP())
                        continue
                    }
                    strB.append(objetivo.getNombre().toString() + "\t")
                    strB.append("(" + objetivo.getID().toString() + ") " + "\t")
                    strB.append("[" + objetivo.getCuenta().getNombre().toString() + "]" + "\t")
                    when (objetivo.getClaseID(true)) {
                        1 -> strB.append("Feca" + "\t")
                        2 -> strB.append("Osamoda" + "\t")
                        3 -> strB.append("Anutrof" + "\t")
                        4 -> strB.append("Sram" + "\t")
                        5 -> strB.append("Xelor" + "\t")
                        6 -> strB.append("Zurcarak" + "\t")
                        7 -> strB.append("Aniripsa" + "\t")
                        8 -> strB.append("Yopuka" + "\t")
                        9 -> strB.append("Ocra" + "\t")
                        10 -> strB.append("Sadida" + "\t")
                        11 -> strB.append("Sacrogito" + "\t")
                        12 -> strB.append("Pandawa" + "\t")
                        13 -> strB.append("Tymador" + "\t")
                        14 -> strB.append("Zobal" + "\t")
                        15 -> strB.append("Steamer" + "\t")
                        else -> strB.append("Desconocido" + "\t")
                    }
                    strB.append(" " + (if (objetivo.getSexo() === 0) "M" else "F") + "\t")
                    strB.append(objetivo.getNivel().toString() + "\t")
                    strB.append(objetivo.getMapa().getID().toString() + "\t")
                    strB.append("(" + objetivo.getMapa().getX().toString() + "," + objetivo.getMapa().getY().toString() + ")" + "\t")
                    strB.append(if (objetivo.getPelea() == null) "" else "En combat ")
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, strB.toString())
                if (players > 0) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Et $players joueurs en plus")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Realmente $players personajes")
                    }
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "====================")
            }
            "CREAR_GREMIO", "CREATE_GUILD" -> {
                objetivo = _perso
                if (infos!!.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                if (!objetivo.enLinea()) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'est pas connecté.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no esta conectado")
                    }
                    return
                }
                if (objetivo.getGremio() != null || objetivo.getMiembroGremio() != null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.getNombre()
                                .toString() + " appartient déjà à une guilde.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje " + objetivo.getNombre().toString() + " ya tiene gremio")
                    }
                    return
                }
                Accion.realizar_Accion_Estatico(-2, "", objetivo, objetivo, -1, -1.toShort())
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Panel de guilde ouvert pour : " + objetivo.getNombre())
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se abrió la ventana de gremio al personaje " + objetivo
                            .getNombre())
                }
            }
            "INICIARTORNEO" -> {
                ServidorSocket.iniciarTorneo()
                return
            }
            "TORNEOON" -> {
                var torneorx = 0
                try {
                    torneorx = Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                }
                if (torneorx < 0 || torneorx > 3) return
                MainServidor.torneoR = torneorx
                if (torneorx > 0) GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS("Se han abierto las inscripciones al Torneo sólo para los R$torneorx.") else GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS("Se han abierto las inscripciones al Torneo sólo para las personas sin R.")
                for (px in Mundo.getPersonajesEnLinea()) {
                    if (px == null) continue
                    if (px.enTorneo !== 0) px.enTorneo = 0
                }
                MainServidor.TorneoOn = true
                MainServidor.empezoTorneo = false
                MainServidor.faseTorneo = 0
                ServidorSocket.PjsTorneo.clear()
                ServidorSocket.PjsTorneo2.clear()
                ServidorSocket.PjsTorneo3.clear()
                ServidorSocket.listaMuertos.clear()
                ServidorSocket.listaMuertos2.clear()
                ServidorSocket.listaMuertos3.clear()
                ServidorSocket.ganador = null
                return
            }
            "TORNEOOFF" -> {
                for (px in Mundo.getPersonajesEnLinea()) {
                    if (px == null) continue
                    if (px.enTorneo !== 0) px.enTorneo = 0
                }
                MainServidor.TorneoOn = false
                MainServidor.empezoTorneo = false
                MainServidor.faseTorneo = 0
                ServidorSocket.PjsTorneo.clear()
                ServidorSocket.PjsTorneo2.clear()
                ServidorSocket.PjsTorneo3.clear()
                ServidorSocket.listaMuertos.clear()
                ServidorSocket.listaMuertos2.clear()
                ServidorSocket.listaMuertos3.clear()
                ServidorSocket.ganador = null
                return
            }
            "DEFORMAR", "DEMORPH" -> {
                objetivo = _perso
                if (infos!!.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                    if (objetivo == null) {
                        if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                        }
                        return
                    }
                }
                if (objetivo.getPelea() != null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur est en combat.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje esta en un combate")
                    }
                    return
                }
                objetivo.deformar()
                objetivo.refrescarEnMapa()
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.getNombre()
                            .toString() + " a retrouvé son apparence initiale.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El jugador " + objetivo.getNombre().toString() + " ha sido deformado")
                }
            }
            "IR_DONDE", "JOIN", "GO_TO_PLAYER" -> {
                objetivo = if (infos!!.size > 1) {
                    Mundo.getPersonajePorNombre(infos[1])
                } else {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Veuillez indiquer le nom du joueur à rejoindre.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Hace falta colocar un nombre de jugador")
                    }
                    return
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                if (!objetivo.enLinea()) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'est pas connecté.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no esta conectado")
                    }
                    return
                }
                mapaID = objetivo.getMapa().getID()
                celdaID = objetivo.getCelda().getID()
                var teleportado: Personaje? = _perso
                if (infos.size > 2) {
                    teleportado = Mundo.getPersonajePorNombre(infos[2])
                    if (teleportado == null || !teleportado.enLinea()) {
                        if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur à téléporter n'existe pas ou n'est pas connecté")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje a teleportar no existe o no esta conectado")
                        }
                        return
                    }
                }
                if (teleportado.getPelea() != null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + teleportado.getNombre().toString() + " est en combat.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje " + teleportado.getNombre().toString() + " esta en combate")
                    }
                    return
                }
                if (teleportado.estaExchange()) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + teleportado.getNombre()
                                .toString() + " est entrain de exchange.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje " + teleportado.getNombre()
                                .toString() + " esta haciendo un exchange")
                    }
                    return
                }
                if (teleportado.getTutorial() != null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + teleportado.getNombre().toString() + " est en tutoriel.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje " + teleportado.getNombre()
                                .toString() + " esta en un tutorial")
                    }
                    return
                }
                if (!teleportado.getHuir()) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + teleportado.getNombre()
                                .toString() + " ne peut fuir d'un combat PVP")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje " + teleportado.getNombre()
                                .toString() + " no puede huir de una pelea PVP")
                    }
                    return
                }
                teleportado.teleport(mapaID, celdaID)
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur a été téléporté")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El jugador " + teleportado.getNombre()
                            .toString() + " fue teletransportado donde jugador " + objetivo.getNombre().toString() + " (Map: " + objetivo.getMapa().getID()
                            .toString() + ")")
                }
            }
            "TRAER", "JOIN_ME" -> {
                objetivo = if (infos!!.size > 1) {
                    Mundo.getPersonajePorNombre(infos[1])
                } else {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Veuillez indiquer le nom du joueur à rejoindre.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Hace falta colocar un nombre de jugador")
                    }
                    return
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                if (!objetivo.enLinea()) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'est pas connecté.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no esta conectado")
                    }
                    return
                }
                if (objetivo.getPelea() != null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.getNombre().toString() + " est en combat.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje " + objetivo.getNombre().toString() + " esta en combate")
                    }
                    return
                }
                if (objetivo.estaExchange()) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.getNombre().toString() + " est en exchange.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje " + objetivo.getNombre().toString() + " esta en exchange")
                    }
                    return
                }
                if (objetivo.getTutorial() != null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.getNombre().toString() + " est en tutoriel.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje " + objetivo.getNombre().toString() + " esta en un tutorial")
                    }
                    return
                }
                if (!objetivo.getHuir()) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.getNombre()
                                .toString() + " ne peut fuir d'un combat PVP")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje " + objetivo.getNombre()
                                .toString() + " no puede huir de una pelea PVP")
                    }
                    return
                }
                var traedor: Personaje? = _perso
                if (infos.size > 2) {
                    traedor = Mundo.getPersonajePorNombre(infos[2])
                    if (traedor == null || !traedor.enLinea()) {
                        if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'est pas connecté.")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personajeno no esta conectado")
                        }
                        return
                    }
                }
                mapaID = traedor.getMapa().getID()
                celdaID = traedor.getCelda().getID()
                objetivo.teleport(mapaID, celdaID)
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.getNombre()
                            .toString() + " ha sido teletransportado hacia el personaje " + traedor.getNombre())
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El jugador " + objetivo.getNombre().toString() + " (Map: " + objetivo
                            .getNombre().toString() + ")" + " fue teletransportado donde jugador " + traedor.getNombre())
                }
            }
            "AN", "ALL", "ANNOUNCE" -> try {
                infos = mensaje.split(" ", 2)
                if (infos!!.size < 2) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Veuillez indiquer le message à envoyer!")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Falta argumentos")
                    }
                    return
                }
                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS("<b>[" + _perso.getNombre().toString() + "] : </b> " + infos[1])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }
            "TELEPORT" -> {
                try {
                    if (infos!!.size > 1) {
                        mapaID = Short.parseShort(infos[1])
                    }
                    if (infos.size > 2) {
                        celdaID = Short.parseShort(infos[2])
                    }
                } catch (e: Exception) {
                }
                mapa = Mundo.getMapa(mapaID)
                if (mapa == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "MAPID INVALIDE!")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El mapa a teleportar no existe")
                    }
                    return
                }
                if (celdaID <= -1) {
                    celdaID = mapa.getRandomCeldaIDLibre()
                } else if (mapa.getCelda(celdaID) == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "CELLID INVALIDE!")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "CeldaID inválida")
                    }
                    return
                }
                objetivo = _perso
                objetivo.teleport(mapaID, celdaID)
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.getNombre()
                            .toString() + " ha sido teletransportado a mapaID: " + mapaID.toString() + ", celdaID: " + celdaID)
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El jugador " + objetivo.getNombre()
                            .toString() + " ha sido teletransportado a mapaID: " + mapaID.toString() + ", celdaID: " + celdaID)
                }
            }
            "TELEPORT_SIN_TODOS" -> {
                try {
                    if (infos!!.size > 1) {
                        mapaID = Short.parseShort(infos[1])
                    }
                    if (infos.size > 2) {
                        celdaID = Short.parseShort(infos[2])
                    }
                } catch (e: Exception) {
                }
                mapa = Mundo.getMapa(mapaID)
                if (mapa == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "MAPID INVALIDE!")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El mapa a teleportar no existe")
                    }
                    return
                }
                if (celdaID <= -1) {
                    celdaID = mapa.getRandomCeldaIDLibre()
                } else if (mapa.getCelda(celdaID) == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "CELLID INVALIDE!")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "CeldaID inválida")
                    }
                    return
                }
                objetivo = _perso
                if (infos!!.size > 3) {
                    objetivo = Mundo.getPersonajePorNombre(infos[3])
                    if (objetivo == null) {
                        if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                        }
                        return
                    }
                    if (!objetivo.enLinea()) {
                        if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'est pas connecté.")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no esta conectado")
                        }
                        return
                    }
                }
                objetivo.teleport(mapaID, celdaID)
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.getNombre()
                            .toString() + " ha sido teletransportado a mapaID: " + mapaID.toString() + ", celdaID: " + celdaID)
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El jugador " + objetivo.getNombre()
                            .toString() + " ha sido teletransportado a mapaID: " + mapaID.toString() + ", celdaID: " + celdaID)
                }
            }
            "IR_MAPA", "GO_MAP" -> {
                var mapaX = 0
                var mapaY = 0
                celdaID = 0
                var contID = 0
                try {
                    mapaX = Integer.parseInt(infos!![1])
                    mapaY = Integer.parseInt(infos[2])
                    celdaID = Short.parseShort(infos[3])
                    contID = Integer.parseInt(infos[4])
                } catch (e10: Exception) {
                }
                mapa = Mundo.mapaPorCoordXYContinente(mapaX, mapaY, contID)
                if (mapa == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Position ou continent invalide!")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Posicion o continente inválido")
                    }
                    return
                }
                if (mapa.getCelda(celdaID) == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "CellID invalide!")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "CeldaID inválido")
                    }
                    return
                }
                objetivo = _perso
                if (infos!!.size > 5) {
                    objetivo = Mundo.getPersonajePorNombre(infos[5])
                    if (objetivo == null) {
                        if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                        }
                        return
                    }
                    if (!objetivo.enLinea()) {
                        if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'est pas connecté.")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no esta conectado")
                        }
                        return
                    }
                    if (objetivo.getPelea() != null) {
                        if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur est en combat!")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje esta en combate")
                        }
                        return
                    }
                }
                objetivo.teleport(mapa.getID(), celdaID)
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.getNombre().toString() + " a été téléporté!")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El jugador " + objetivo.getNombre().toString() + " ha sido teletransportado")
                }
            }
            else -> {
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Commande non reconnue: $comando")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Comando no reconocido: $comando")
                }
                return
            }
        }
        // if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
        // GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Commande GM 1!");
        // } else {
        // GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Comando de nivel 1");
        // }
    }

    fun GM_lvl_2(comando: String?, infos: Array<String?>?, mensaje: String?, _cuenta: Cuenta?,
                 _perso: Personaje?) {
        var infos = infos
        var numInt = -1
        var celdaID: Short
        var x: Short
        var y: Short
        var objetivo: Personaje? = null
        var strB: StringBuilder? = StringBuilder()
        val mapa: Mapa = _perso.getMapa()
        var motivo: String? = ""
        when (comando.toUpperCase()) {
            "INFO_PJ", "INFO_PLAYER", "INFO_PERSONAJE", "STATS_PERSO", "STATS_PJ", "STATS_PERSONAJE", "STATS_PLAYER" -> {
                objetivo = _perso
                if (infos!!.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Abre tu panel de caracteristicas para ver la informacion del jugador "
                        + objetivo.getNombre())
                GestorSalida.enviar(_perso, objetivo.stringStats(), false)
            }
            "INVENTORY_PLAYER", "INVENTARIO_PLAYER", "INVENTARIO_JUGADOR", "INVENTARIO_PERSO", "INVENTARIO_PJ", "INVENTORY_PJ", "INVENTORY_PERSO" -> {
                objetivo = _perso
                if (infos!!.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Abre tu panel de inventario para ver los items del jugador "
                        + objetivo.getNombre())
                // _perso.setEspiarPj(true);
                GestorSalida.ENVIAR_ASK_PERSONAJE_A_ESPIAR(objetivo, _perso)
            }
            "EQUIPO_GANADOR", "WINNER", "GANADOR_EQUIPO", "GANAR_PELEA", "GANADOR_PELEA", "WINNER_FIGHT", "TEAM_WINNER" -> try {
                numInt = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento incorrecto")
                    return
                }
                if (numInt != 2 && numInt != 1) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ganador invalido")
                    return
                }
                objetivo = _perso
                if (infos.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                val pelea: Pelea = objetivo.getPelea()
                if (pelea == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, objetivo.getNombre().toString() + " no estas en pelea")
                    return
                }
                pelea.acaboPelea((if (numInt == 1) 2 else 1).toByte())
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El equipo " + infos[1] + " ha salido victorioso, en la pelea ID "
                        + pelea.getID() + " del mapa " + pelea.getMapaCopia().getID())
            } catch (e: Exception) {
            }
            "CANCEL_FIGHT", "CANCELAR_PELEA", "ANULAR_PELEA", "ANULATE_FIGHT", "FIGHT_CANCEL" -> try {
                objetivo = _perso
                if (infos!!.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                val pelea: Pelea = objetivo.getPelea()
                if (pelea == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, objetivo.getNombre().toString() + " no estas en pelea")
                    return
                }
                pelea.cancelarPelea()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La pelea ID " + pelea.getID().toString() + " del mapa " + pelea.getMapaCopia()
                        .getID().toString() + " ha sido cancelada")
            } catch (e: Exception) {
            }
            "SHOW_BAN_IPS", "MOSTRAR_BAN_IPS", "SHOW_LIST_BAN_IPS", "MOSTRAR_BAN_IP" -> GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, """
     Las IPs Baneadas son las siguientes:
     ${GestorSQL.LISTA_BAN_IP()}
     """.trimIndent())
            "BANEAR", "BAN" -> {
                objetivo = if (infos!!.size > 1) {
                    Mundo.getPersonajePorNombre(infos[1])
                } else {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Arguments incorrects.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    }
                    return
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                numInt = try {
                    Integer.parseInt(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Debes ingresar un tiempo (minutos)")
                    return
                }
                try {
                    if (infos.size > 3) {
                        infos = mensaje.split(" ", 4)
                        motivo = infos[3]
                    }
                } catch (e: Exception) {
                }
                if (numInt == 0) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Durée du ban incorrecte!")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Tiempo de baneo incorrecto")
                    }
                    return
                }
                objetivo.getCuenta().setBaneado(true, numInt)
                if (objetivo.getServidorSocket() != null) {
                    objetivo.getServidorSocket().cerrarSocket(true, " command BANEAR")
                }
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.getNombre().toString() + " a été banni par " + numInt
                            .toString() + " minutes.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ha sido baneado " + objetivo.getNombre().toString() + " por " + numInt
                            .toString() + " minutos.")
                }
                GestorSalida.ENVIAR_Im_INFORMACION_A_ADMIN("1JUGADOR_BANEAR;" + objetivo.getNombre().toString() + "~" + motivo)
            }
            "DES_BAN", "UN_BAN", "DESBANEAR", "UNBAN" -> {
                objetivo = if (infos!!.size > 1) {
                    Mundo.getPersonajePorNombre(infos[1])
                } else {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Arguments incorrects.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    }
                    return
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                objetivo.getCuenta().setBaneado(false, 0)
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.getNombre().toString() + " a été débanni.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ha sido desbaneado " + objetivo.getNombre())
                }
            }
            "BANEAR_IP_PJ", "BAN_IP_PLAYER", "BAN_IP_PERSO" -> {
                objetivo = if (infos!!.size > 1) {
                    Mundo.getPersonajePorNombre(infos[1])
                } else {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Arguments incorrects.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    }
                    return
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                val ipBaneada: String = objetivo.getCuenta().getActualIP()
                if (!GestorSQL.ES_IP_BANEADA(ipBaneada)) {
                    if (GestorSQL.INSERT_BAN_IP(ipBaneada)) {
                        if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "L'IP $ipBaneada est bannie.")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La IP $ipBaneada esta baneada.")
                        }
                    }
                    if (objetivo.enLinea()) {
                        objetivo.getServidorSocket().cerrarSocket(true, "command Banear IP")
                        if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur a été kick.")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El jugador fue retirado.")
                        }
                    }
                } else {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "L'IP n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La IP no existe")
                    }
                }
            }
            "BANEAR_IP", "BAN_IP", "BANEAR_IP_NUMERO", "BAN_IP_NUMBER" -> {
                if (infos!!.size <= 1) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argument incorrect.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    }
                    return
                }
                if (!GestorSQL.ES_IP_BANEADA(infos[1])) {
                    if (GestorSQL.INSERT_BAN_IP(infos[1])) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La IP " + infos[1] + " esta baneada.")
                    }
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La IP no existe")
                }
            }
            "DESBANEAR_IP_NUMERO", "UNBAN_IP_NUMERO" -> {
                if (infos!!.size <= 1) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argument incorrect.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    }
                    return
                }
                GestorSQL.DELETE_BAN_IP(infos[1])
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "L'ip " + infos[1] + " a été débannie.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se borro la ip " + infos[1] + " de la lista de ip baneadas")
                }
            }
            "EXPULSAR", "KICK" -> {
                objetivo = if (infos!!.size > 1) {
                    Mundo.getPersonajePorNombre(infos[1])
                } else {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Arguments incorrects.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    }
                    return
                }
                try {
                    if (infos.size > 2) {
                        infos = mensaje.split(" ", 3)
                        motivo = infos[2]
                    }
                } catch (e: Exception) {
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                if (!objetivo.enLinea()) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'est pas connecté.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no esta conectado")
                    }
                    return
                } else {
                    try {
                        objetivo.getServidorSocket().cerrarSocket(true, "command EXPULSAR")
                    } catch (e: Exception) {
                        if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Impossible de kicker " + objetivo.getNombre())
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "No se ha podido expulsar a " + objetivo.getNombre())
                        }
                        return
                    }
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.getNombre().toString() + " a été kick.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ha sido expulsado " + objetivo.getNombre())
                    }
                    GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS("1JUGADOR_EXPULSAR;" + objetivo.getNombre().toString() + "~" + motivo)
                }
            }
            "BOLETOS_COMPRADOS", "TICKETS_ACHETES", "GET_BOUGHT_TICKETS" -> {
                numInt = 0
                var z = 1
                while (z <= Mundo.LOTERIA_BOLETOS.length) {
                    if (Mundo.LOTERIA_BOLETOS.get(z - 1) !== 0) {
                        numInt++
                    }
                    z++
                }
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Actuellement, le nombre de tickets achetés est de $numInt.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Actualmente hay $numInt boletos comprados.")
                }
            }
            "LISTA_BOLETOS_COMPRADOS" -> {
                var z = 1
                while (z <= Mundo.LOTERIA_BOLETOS.length) {
                    if (Mundo.LOTERIA_BOLETOS.get(z - 1) !== 0) {
                        if (strB.length() > 0) {
                            strB.append(",")
                        }
                        strB.append(Mundo.LOTERIA_BOLETOS.get(z - 1))
                    }
                    z++
                }
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Liste de tickets achetes " + strB.toString().toString() + ".")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Lista de boletos comprados: " + strB.toString())
                }
            }
            "SET_CELDAS_PELEA" -> {
                mapa.setStrCeldasPelea(infos!![1])
                if (!GestorSQL.UPDATE_MAPA_POS_PELEA(mapa.getID(), mapa.getConvertCeldasPelea())) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Une erreur est survenue lors de la sauvegarde en BDD!")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrió un error al guardar la actualización en la BD.")
                    }
                    return
                } else {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le string cells fight change to " + infos[1])
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El str de celdas pelea cambio a " + infos[1])
                    }
                }
            }
            "SET_COLOUR_AGGRESSOR", "SET_COLOR_ATK", "SET_COLOR_AGRESOR", "SET_COLOR_ATACANTE" -> {
                mapa.setColorCeldasAtacante(infos!![1])
                if (!GestorSQL.UPDATE_MAPA_POS_PELEA(mapa.getID(), mapa.getConvertCeldasPelea())) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Une erreur est survenue lors de la sauvegarde en BDD!")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrió un error al guardar la actualización en la BD.")
                    }
                    return
                } else {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le colour de cells aggressor c'est " + infos[1])
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El color de celdas del agresor es " + infos[1])
                    }
                }
            }
            "BORRAR_POSICIONES", "ELIMINAR_POSICIONES", "DEL_POSICIONES_PELEA", "BORRAR_TODAS_POS_PELEA", "DEL_ALL_POS" -> {
                mapa.decodificarPosPelea("")
                if (!GestorSQL.UPDATE_MAPA_POS_PELEA(mapa.getID(), mapa.getConvertCeldasPelea())) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Une erreur est survenue lors de la sauvegarde en BDD!")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrió un error al guardar la actualización en la BD.")
                    }
                } else {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Les positions de combat ont été supprimées.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Las posiciones de pelea han sido borradas.")
                    }
                }
            }
            "DEL_FIGHT_POS", "DEL_POS_FIGHT", "BORRAR_POS_PELEA", "DEL_FIGHT_POS_BY_CELL" -> {
                celdaID = -1
                try {
                    celdaID = Short.parseShort(infos!![1])
                } catch (e: Exception) {
                }
                if (mapa.getCelda(celdaID) == null) {
                    celdaID = _perso.getCelda().getID()
                }
                // if (mapa.getCercado() != null) {
                // mapa.getCercado().getCeldasObj().remove((Object) celdaID);
                // }
                GestorSalida.enviar(_perso, "GDZ|-$celdaID;0;4|-$celdaID;0;11|-$celdaID;0;5", false)
                mapa.borrarCeldasPelea(celdaID)
                if (!GestorSQL.UPDATE_MAPA_POS_PELEA(mapa.getID(), mapa.getConvertCeldasPelea())) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Une erreur est survenue lors de la sauvegarde en BDD!")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrió un error al guardar la actualización en la BD.")
                    }
                }
            }
            "ADD_CELL_FIGHT", "AGREGAR_CELDA_PELEA", "ADD_CELDA_PELEA", "ADD_POS_FIGHT", "AGREGAR_POS_PELEA", "ADD_FIGHT_POS" -> {
                var equipo = -1
                celdaID = -1
                try {
                    equipo = Integer.parseInt(infos!![1])
                    celdaID = Short.parseShort(infos[2])
                } catch (e: Exception) {
                }
                if (equipo != 2 && equipo != 1) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Equipe incorrecte, use colour 2(blue) o 1(rouge)")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Equipo incorrecto, usa 2(azul) o 1(rojo)")
                    }
                    return
                }
                if (mapa.getCelda(celdaID) == null || !mapa.getCelda(celdaID).esCaminable(true)) {
                    celdaID = _perso.getCelda().getID()
                }
                GestorSalida.enviar(_perso, "GDZ|-$celdaID;0;4|-$celdaID;0;11", false)
                GestorSalida.enviar(_perso, "GDZ|+" + celdaID + ";0;" + if (equipo == 1) 4 else 11, false)
                mapa.addCeldaPelea(equipo, celdaID)
                if (!GestorSQL.UPDATE_MAPA_POS_PELEA(mapa.getID(), mapa.getConvertCeldasPelea())) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Une erreur est survenue lors de la sauvegarde en BDD.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrió un error al guardar la actualización en la BD.")
                    }
                    return
                }
            }
            "OCULTAR_POSICIONES", "HIDE_POSITIONS", "ESCONDER_POSICIONES" -> mapa.panelPosiciones(_perso, false)
            "LISTA_POS_PELEA", "MOSTRAR_POSICIONES", "MOSTRAR_POSICIONES_PELEA", "MOSTRAR_POS_PELEA", "SHOW_POSITIONS", "SHOW_FIGHT_POS" -> mapa.panelPosiciones(_perso, true)
            "MAPAS", "MAPS", "MAPAS_COORDENADAS", "GET_MAPS_BY_COORDS" -> {
                x = -1
                y = -1
                try {
                    x = Short.parseShort(infos!![1])
                    y = Short.parseShort(infos!![2])
                } catch (e: Exception) {
                    x = mapa.getX()
                    y = mapa.getY()
                }
                strB = StringBuilder(Mundo.mapaPorCoordenadas(x, y, mapa.getSubArea().getArea().getSuperArea().getID()))
                if (strB.toString().isEmpty()) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "No hay ID mapa para esas coordenadas")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Los ID mapas para las coordenas X: " + x + " Y: " + y + " son "
                            + strB.toString())
                }
            }
            "MAP_UP", "MAPA_ARRIBA" -> {
                x = mapa.getX()
                y = (mapa.getY() - 1)
                strB = StringBuilder(Mundo.mapaPorCoordenadas(x, y, mapa.getSubArea().getArea().getSuperArea().getID()))
                if (strB.toString().isEmpty()) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "No hay ID mapa para esas coordenadas")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Los ID mapas para las coordenas X: " + x + " Y: " + y + " son "
                            + strB.toString())
                }
            }
            "MAP_DOWN", "MAPA_ABAJO" -> {
                x = mapa.getX()
                y = (mapa.getY() + 1)
                strB = StringBuilder(Mundo.mapaPorCoordenadas(x, y, mapa.getSubArea().getArea().getSuperArea().getID()))
                if (strB.toString().isEmpty()) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "No hay ID mapa para esas coordenadas")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Los ID mapas para las coordenas X: " + x + " Y: " + y + " son "
                            + strB.toString())
                }
            }
            "MAP_LEFT", "MAPA_IZQUIERDA" -> {
                x = (mapa.getX() - 1)
                y = mapa.getY()
                strB = StringBuilder(Mundo.mapaPorCoordenadas(x, y, mapa.getSubArea().getArea().getSuperArea().getID()))
                if (strB.toString().isEmpty()) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "No hay ID mapa para esas coordenadas")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Los ID mapas para las coordenas X: " + x + " Y: " + y + " son "
                            + strB.toString())
                }
            }
            "MAP_RIGHT", "MAPA_DERECHA" -> {
                x = (mapa.getX() + 1)
                y = mapa.getY()
                strB = StringBuilder(Mundo.mapaPorCoordenadas(x, y, mapa.getSubArea().getArea().getSuperArea().getID()))
                if (strB.toString().isEmpty()) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "No hay ID mapa para esas coordenadas")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Los ID mapas para las coordenas X: " + x + " Y: " + y + " son "
                            + strB.toString())
                }
            }
            "CAMBIAR_ALINEACION", "ALINEACION", "ALIGN", "SET_ALIGN" -> {
                var alineacion: Byte = -1
                try {
                    alineacion = Byte.parseByte(infos!![1])
                } catch (e5: Exception) {
                }
                if (alineacion < -1 || alineacion > 3) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Alignement incorrect.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    }
                    return
                }
                objetivo = _perso
                if (infos!!.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                    if (objetivo == null) {
                        if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                        }
                        return
                    }
                }
                objetivo.cambiarAlineacion(alineacion, true)
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "L'alignement du joueur " + objetivo.getNombre()
                            .toString() + " a été modifié.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La alineacion del personaje " + objetivo.getNombre()
                            .toString() + " ha sido modificada")
                }
            }
            "APRENDER_OFICIO", "LEARN_JOB" -> {
                numInt = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Veuillez indiquer l'id du métier.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos Incorrectos")
                    }
                    return
                }
                if (Mundo.getOficio(numInt) == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "L'ID du métier est incorrect.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "ID Oficio no existe")
                    }
                    return
                }
                objetivo = _perso
                if (infos.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                    if (objetivo == null) {
                        if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                        }
                        return
                    }
                }
                if (objetivo.aprenderOficio(Mundo.getOficio(numInt), 0) !== -1) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur a appris ce métier $numInt")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje " + objetivo.getNombre().toString() + " ha aprendido el oficio "
                                + numInt)
                    }
                } else {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur ne peut pas apprendre ce métier.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje " + objetivo.getNombre()
                                .toString() + " no puede aprender ese oficio")
                    }
                }
            }
            "PDV", "PDVPER" -> {
                var porcPDV = 0
                try {
                    porcPDV = Integer.parseInt(infos!![1])
                    if (porcPDV < 0) {
                        porcPDV = 0
                    }
                    if (porcPDV > 100) {
                        porcPDV = 100
                    }
                    objetivo = _perso
                    if (infos.size > 2) {
                        val nombre = infos[2]
                        objetivo = Mundo.getPersonajePorNombre(nombre)
                        if (objetivo == null || !objetivo.enLinea()) {
                            objetivo = _perso
                        }
                    }
                    objetivo.actualizarPDV(porcPDV)
                    if (objetivo.enLinea()) {
                        GestorSalida.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(objetivo)
                    }
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le pourcentage de vie du joueur " + objetivo.getNombre()
                                .toString() + " a été modifié en " + porcPDV)
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ha sido modificado el porcentaje de vida " + objetivo.getNombre()
                                .toString() + " a " + porcPDV)
                    }
                    GestorSalida.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(objetivo)
                } catch (e: Exception) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Arguments incorrects")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    }
                }
            }
            else -> {
                GM_lvl_1(comando, infos, mensaje, _cuenta, _perso)
                return
            }
        }
        // if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
        // GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Commande de GM 2!");
        // } else {
        // GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Comando de nivel 2");
        // }
    }

    fun GM_lvl_3(comando: String?, infos: Array<String?>?, mensaje: String?, _cuenta: Cuenta?,
                 _perso: Personaje?) {
        var infos = infos
        var sql = false
        // byte idByte = 0, numByte = 0;
        // short numShort = 0;
        // int numInt = 0, tipo = 0, accionID = -1, id2 = -1, restriccion = -1;
        // int idMob = -1, idObjMod = -1, prospecc = 100, max = 1;
        // float porcentaje = 0, numFloat = 0;
        // final StringBuilder strB = new StringBuilder();
        // String args = "", condicion = "", str = "";
        var mapa: Mapa = _perso.getMapa()
        // short celdaID = -1, mapaID = mapa.getID();
        // MobModelo mobModelo;
        var objetivo: Personaje? = null
        when (comando.toUpperCase()) {
            "KICK_MERCHANT", "KICK_MERCHAND", "VOTAR_MERCANTE", "EXPULSAR_MERCANTE" -> try {
                if (infos!!.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                if (!objetivo.esMercante()) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje " + objetivo.getNombre()
                            .toString() + " no esta en modo mercante")
                    return
                }
                objetivo.getMapa().removerMercante(objetivo.getID())
                objetivo.setMercante(false)
                GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(objetivo.getMapa(), objetivo.getID())
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje " + objetivo.getNombre()
                        .toString() + " ha sido expulsado del modo mercante")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "EXCEPTION COMANDO")
            }
            "IPS_AFKS", "IPS_CLIENTES_AFKS", "IPS_BUGS", "IPS_ATACANTES", "IPS_ATTACK" -> try {
                var segundos = 0
                segundos = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Valor incorrecto (segundos)")
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Las ips de las connexiones BUGS son: " + ServidorServer
                        .listaClientesBug(segundos))
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "EXCEPTION COMANDO")
            }
            "EXPULSAR_AFKS", "EXPULSAR_CLIENTES_BUG", "KICK_CLIENTS_BUG", "VOTAR_CLIENTES_BUG", "EXPULSAR_INACTIVOS", "CLEAN_SERVER", "LIMPIAR_SERVIDOR", "LIMPIAR_SOCKETS" -> try {
                var segundos = 0
                segundos = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Valor incorrecto (segundos)")
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se expulsó " + ServidorServer.borrarClientesBug(segundos)
                        .toString() + " clientes bugeados")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "EXCEPTION COMANDO")
            }
            "ACTUALIZAR_NPC", "ACCESSORIES_NPC", "STUFF_NPC" -> {
                var numInt = -1
                try {
                    numInt = Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                }
                if (Mundo.getNPCModelo(numInt) == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "NPC modelo invalido")
                    return
                }
                objetivo = _perso
                if (infos!!.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                }
                if (objetivo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    return
                }
                val npcMod: NPCModelo = Mundo.getNPCModelo(numInt)
                try {
                    npcMod.modificarNPC(objetivo.getSexo(), objetivo.getTalla() as Short, objetivo.getTalla() as Short, objetivo.getGfxID(false), objetivo.getColor1(), objetivo.getColor2(), objetivo.getColor3(), objetivo.getGremio())
                    npcMod.setAccesorios(objetivo.getStringAccesorios())
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se actualizo el NPC modelo " + numInt +
                            ", Nombre: "
                            + npcMod.getNombre() + ", Gfx: " + npcMod.getGfxID() + ", Accesorios: " +
                            npcMod.getAccesoriosInt())
                } catch (e: Exception) {
                }
            }
            "PANEL_ADMIN" -> try {
                GestorSalida.enviar(_perso, "ÑP" + mapa.getCapabilitiesCompilado().toString() + "|" + mapa.getMaxGrupoDeMobs()
                        .toString() + "|" + mapa.getMaxMobsPorGrupo().toString() + "|" + mapa.getMaxMercantes(), false)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "A" -> {
                infos = mensaje.split(" ", 2)
                try {
                    GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Falta argumentos")
                }
            }
            "GET_LISTA_PACKETS_COLA" -> try {
                val packetsCola: String = _perso.getPacketsCola().replaceAll(0x00.toChar().toString() + "", "\n")
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Lista packets en cola:\n$packetsCola")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "HACER_ACCION", "DO_ACTION", "REALIZAR_ACCION" -> try {
                objetivo = _perso
                if (infos!!.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                if (!objetivo.enLinea()) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'est pas connecté.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no esta conectado")
                    }
                    return
                }
                var tipoAccion = 0
                var args: String? = ""
                tipoAccion = try {
                    Integer.parseInt(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "ID Accion incorrecta")
                    return
                }
                if (infos.size > 3) {
                    args = infos[3]
                }
                Accion.realizar_Accion_Estatico(tipoAccion, args, objetivo, null, -1, -1.toShort())
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje " + objetivo.getNombre().toString() + " realizó la acción "
                        + tipoAccion.toString() + " con los argumentos " + args)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
            }
            "FIJAR_STATS_MOB", "FIJAR_STATS", "SET_STATS_MOB", "FIJAR_DAÑOS", "FIJAR_DAÑO", "MODIFICAR_STATS_MOB" -> try {
                var id = 0
                var grado: Byte = 0
                var stats: String? = ""
                try {
                    id = Integer.parseInt(infos!![1])
                    grado = Byte.parseByte(infos[2])
                    stats = infos[3]
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                val mobModelo: MobModelo = Mundo.getMobModelo(id)
                if (mobModelo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "MobModelo " + infos[1] + " no existe")
                    return
                }
                val mGrado: MobGradoModelo = mobModelo.getGradoPorGrado(grado)
                if (mGrado == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "MobGradorModelo " + infos[1] + "-" + infos[2] + " no existe")
                    return
                }
                if (mobModelo.modificarStats(grado, stats)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El mob " + mobModelo.getNombre().toString() + " (" + mobModelo.getID()
                            .toString() + ") g: " + grado.toString() + " lvl: " + mobModelo.getGradoPorGrado(grado).getNivel().toString() + " ha sido modificado stats a "
                            + stats)
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "STATS_DEFECTO_MOB" -> try {
                var id = 0
                id = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                val mobModelo: MobModelo = Mundo.getMobModelo(id)
                if (mobModelo == null) {
                    GestorSalida.ENVIAR_BN_NADA(_perso)
                    return
                }
                GestorSalida.ENVIAR_ÑJ_STATS_DEFECTO_MOB(_perso, mobModelo.strStatsTodosMobs())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "TEST_DAÑO" -> try {
                var id = 0
                var grado: Byte = 1
                var stats: String? = ""
                try {
                    id = Integer.parseInt(infos!![1])
                    grado = Byte.parseByte(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                try {
                    stats = infos[3]
                } catch (e: Exception) {
                }
                val mobModelo: MobModelo = Mundo.getMobModelo(id)
                if (mobModelo == null) {
                    GestorSalida.ENVIAR_BN_NADA(_perso)
                    return
                }
                GestorSalida.ENVIAR_ÑK_TEST_DAÑO_MOB(_perso, mobModelo.calculoDaño(grado, stats))
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "MAX_PELEAS_MAPA", "MAP_MAX_FIGHTS", "MAX_FIGHTS_MAP" -> try {
                var maxPeleas: Byte = 0
                var mapaID: Short = 0
                try {
                    maxPeleas = Byte.parseByte(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                try {
                    mapaID = Short.parseShort(infos!![2])
                    if (Mundo.getMapa(mapaID) != null) {
                        mapa = Mundo.getMapa(mapaID)
                    }
                } catch (e: Exception) {
                }
                mapa.setMaxPeleas(maxPeleas)
                GestorSQL.UPDATE_MAPA_MAX_PELEAS(mapa.getID(), maxPeleas)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El mapa " + mapa.getID().toString() + " cambio el valor de maximo de pleas a "
                        + maxPeleas)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "MODIFICAR_NPC" -> try {
                var id = 0
                var sexo: Byte = 1
                var escalaX: Short = 100
                var escalaY: Short = 100
                var gfxID: Short = 9999
                var color1 = -1
                var color2 = -1
                var color3 = -1
                var arma = 0
                var sombrero = 0
                var capa = 0
                var mascota = 0
                var escudo = 0
                try {
                    id = Short.parseShort(infos!![1])
                } catch (e: Exception) {
                }
                try {
                    sexo = Byte.parseByte(infos!![2])
                } catch (e: Exception) {
                }
                try {
                    escalaX = Short.parseShort(infos!![3])
                } catch (e: Exception) {
                }
                try {
                    escalaY = Short.parseShort(infos!![4])
                } catch (e: Exception) {
                }
                try {
                    gfxID = Short.parseShort(infos!![5])
                } catch (e: Exception) {
                }
                try {
                    color1 = Integer.parseInt(infos!![6])
                } catch (e: Exception) {
                }
                try {
                    color2 = Integer.parseInt(infos!![7])
                } catch (e: Exception) {
                }
                try {
                    color3 = Integer.parseInt(infos!![8])
                } catch (e: Exception) {
                }
                try {
                    arma = Integer.parseInt(infos!![9])
                } catch (e: Exception) {
                }
                try {
                    sombrero = Integer.parseInt(infos!![10])
                } catch (e: Exception) {
                }
                try {
                    capa = Integer.parseInt(infos!![11])
                } catch (e: Exception) {
                }
                try {
                    mascota = Integer.parseInt(infos!![12])
                } catch (e: Exception) {
                }
                try {
                    escudo = Integer.parseInt(infos!![13])
                } catch (e: Exception) {
                }
                npcMod = Mundo.getNPCModelo(id)
                if (npcMod == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "NPC $id no existe")
                }
                npcMod.modificarNPC(sexo, escalaX, escalaY, gfxID, color1, color2, color3, null)
                npcMod.setAccesorios(arma, sombrero, capa, mascota, escudo)
                GestorSQL.UPDATE_NPC_MODELO(npcMod, arma, sombrero, capa, mascota, escudo)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modifico el NPC " + id + " con las sig. caracteristicas, GFX: "
                        + gfxID + " SEX: " + sexo + " ESCALA X: " + escalaX + " ESCALA Y: " + escalaY + " COLOR1: " + color1
                        + " COLOR2: " + color2 + " COLOR3: " + color3 + " ACCES: " + npcMod.getAccesoriosInt())
                GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(_perso, "NPC MODIFICADO!! :D")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "NPC_RANKING" -> {
                if (MainServidor.NPC_RANKING_KOLISEO > 0) {
                    Mundo.actualizarLiderKoliseo()
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Koliseo Actualizado")
                }
                if (MainServidor.NPC_RANKING_PVP > 0) {
                    Mundo.actualizarLiderPVP()
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "PVP Actualizado")
                }
            }
            "CELDA_COORD", "CELL_COORD", "EJES_CELDA", "POS_CELDA", "COORD_CELDA", "CELDA_POS" -> try {
                var celdaID: Short = 0
                try {
                    celdaID = Short.parseShort(infos!![1])
                } catch (e: Exception) {
                    celdaID = _perso.getCelda().getID()
                }
                try {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Las coordenadas de la celda $celdaID es, X: " + mapa
                            .getCelda(celdaID).getCoordX() + ", Y: " + mapa.getCelda(celdaID).getCoordY())
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Celda No existe")
                    return
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "TEST_CELDAS" -> try {
                var celdaID: Short = 0
                try {
                    celdaID = Short.parseShort(infos!![1])
                } catch (e: Exception) {
                    return
                }
                try {
                    var s = ""
                    for (c in Camino.celdasPorDistancia(_perso.getCelda(), _perso.getMapa(), celdaID)) {
                        s += "$c,"
                        GestorSalida.ENVIAR_GDO_OBJETO_TIRAR_SUELO(_perso, '+', c, 311, false, "")
                    }
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Las celdas a mostrar son $s")
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Celda No existe")
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "CONSULTAR_OGRINAS", "GET_OGRINAS", "CONSULTA_OGRINAS", "CONSULTA_PUNTOS", "GET_POINTS" -> {
                try {
                    objetivo = _perso
                    if (infos!!.size > 1) {
                        objetivo = Mundo.getPersonajePorNombre(infos[1])
                    }
                    if (objetivo == null) {
                        if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                        }
                        return
                    }
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
                }
                try {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.getNombre().toString() + " possède " + GestorSQL
                                .GET_OGRINAS_CUENTA(objetivo.getCuentaID()).toString() + " ogrines/points")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje " + objetivo.getNombre().toString() + " posee " + GestorSQL
                                .GET_OGRINAS_CUENTA(objetivo.getCuentaID()))
                    }
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
                }
            }
            "SEGUNDOS_TURNO_PELEA", "TIEMPO_TURNO_PELEA", "RATE_TIEMPO_PELEA" -> try {
                var segundos = 0
                segundos = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.SEGUNDOS_TURNO_PELEA = segundos
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, ("Se modificó el SEGUNDOS_TURNO_PELEA a "
                        + MainServidor.SEGUNDOS_TURNO_PELEA) + " segundos")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "MINUTOS_ALIMENTACION_MASCOTA", "TIEMPO_ALIMENTACION", "RATE_TIEMPO_ALIMENTACION" -> try {
                var minutos = 0
                minutos = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.MINUTOS_ALIMENTACION_MASCOTA = minutos
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, ("Se modificó el MINUTOS_ALIMENTACION_MASCOTA a "
                        + MainServidor.MINUTOS_ALIMENTACION_MASCOTA) + " minutos")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "SEGUNDOS_MOVERSE_MONTURAS", "TIEMPO_MOVERSE_PAVOS", "RATE_TIEMPO_MOV_PAVO" -> try {
                var segundos = 0
                segundos = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.SEGUNDOS_MOVER_MONTURAS = segundos
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso,
                        ("El Tiempo para que los dragopavos se muevan automáticamente ha sido modificado a "
                                + MainServidor.SEGUNDOS_MOVER_MONTURAS) + " segundos")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "MINUTOS_PARIR_MONTURA", "TIEMPO_PARIR", "RATE_TIEMPO_PARIR" -> try {
                var minutos = 0
                minutos = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.MINUTOS_GESTACION_MONTURA = minutos
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, ("Se modificó el MINUTOS_PARIR_MONTURA a "
                        + MainServidor.MINUTOS_GESTACION_MONTURA) + " minutos")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "RATE_FM", "DIFICULTAD_FM" -> try {
                var rate: Byte = 0
                try {
                    rate = Byte.parseByte(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.RATE_FM = rate.toInt()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modificó el RATE_FM a " + MainServidor.RATE_FM)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "RATE_PODS" -> try {
                var rate: Byte = 0
                try {
                    rate = Byte.parseByte(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.RATE_PODS = rate.toInt()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modificó el RATE_PODS a " + MainServidor.RATE_PODS)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "RATE_CAPTURA_PAVOS" -> try {
                var rate: Byte = 0
                try {
                    rate = Byte.parseByte(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.RATE_CAPTURA_MONTURA = rate.toInt()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modificó el RATE_CAPTURA_MONTURA a "
                        + MainServidor.RATE_CAPTURA_MONTURA)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "RATE_KAMAS" -> try {
                var rate: Byte = 0
                try {
                    rate = Byte.parseByte(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.RATE_KAMAS = rate.toInt()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modificó el RATE_KAMAS a " + MainServidor.RATE_KAMAS)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "RATE_DROP" -> try {
                var rate: Byte = 0
                try {
                    rate = Byte.parseByte(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.RATE_DROP_NORMAL = rate.toInt()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modificó el RATE_DROP a " + MainServidor.RATE_DROP_NORMAL)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "RATE_PVM", "RATE_XP_PVM", "RATE_EXP_PVM" -> try {
                var rate = 0
                rate = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.RATE_XP_PVM = rate
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modificó el RATE_XP_PVM a " + MainServidor.RATE_XP_PVM)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "RATE_PVP", "RATE_XP_PVP", "RATE_EXP_PVP" -> try {
                var rate: Byte = 0
                try {
                    rate = Byte.parseByte(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.RATE_XP_PVP = rate.toInt()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modificó el RATE_XP_PVP a " + MainServidor.RATE_XP_PVP)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "RATE_OFICIO", "RATE_XP_OFICIO", "RATE_EXP_OFICIO", "RATE_METIER" -> try {
                var rate: Byte = 0
                try {
                    rate = Byte.parseByte(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.RATE_XP_OFICIO = rate.toInt()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modificó el RATE_XP_OFICIO a " + MainServidor.RATE_XP_OFICIO)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "RATE_CRIANZA_PAVOS", "RATE_CRIANZA_MONTURA", "RATE_CRIANZA", "RATE_ELEVAGE" -> try {
                var rate: Byte = 0
                try {
                    rate = Byte.parseByte(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.RATE_CRIANZA_MONTURA = rate.toInt()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modificó el RATE_CRIANZA_MONTURA a "
                        + MainServidor.RATE_CRIANZA_MONTURA)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "RATE_HONOUR", "RATE_HONOR" -> try {
                var rate: Byte = 0
                try {
                    rate = Byte.parseByte(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.RATE_HONOR = rate.toInt()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modificó el RATE_HONOR a " + MainServidor.RATE_HONOR)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "RATE_DROP_ARMAS_ETEREAS" -> try {
                var rate: Byte = 0
                try {
                    rate = Byte.parseByte(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.RATE_DROP_ARMAS_ETEREAS = rate.toInt()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modificó el RATE_DROP_ARMAS_ETEREAS a "
                        + MainServidor.RATE_DROP_ARMAS_ETEREAS)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "MAX_REPORTES", "LIMITE_MOSTRAR_REPORTES", "MAX_MOSTRAR_REPORTES", "LIMITE_REPORTES" -> try {
                var limite: Short = 0
                try {
                    limite = Short.parseShort(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.LIMITE_REPORTES = limite.toInt()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se configuro a $limite el limite de reportes")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "LIMIT_LADDER", "LIMITE_LADDER" -> try {
                var limite: Short = 0
                try {
                    limite = Short.parseShort(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.LIMITE_LADDER = limite.toInt()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se configuro a $limite el limite del ladder")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "TIEMPO_MOSTRAR_BOTON_VOTO", "MINUTOS_SPAMEAR_BOTON_VOTO" -> try {
                var minutos = 0
                minutos = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.MINUTOS_SPAMEAR_BOTON_VOTO = minutos
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se configuro a " + MainServidor.MINUTOS_SPAMEAR_BOTON_VOTO
                        .toString() + " minutos para mostrar boton voto")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "TIEMPO_ESTRELLAS_MOBS", "TIEMPO_MOB_ESTRELLAS", "SEGUNDOS_ESTRELLAS_MOBS" -> try {
                var segundos = 0
                segundos = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.SEGUNDOS_ESTRELLAS_GRUPO_MOBS = segundos
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se configuro a " + MainServidor.SEGUNDOS_ESTRELLAS_GRUPO_MOBS
                        .toString() + " segundos la recarga de estrellas de mobs")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "PROBABILIDAD_ARCHI_MOBS" -> try {
                var probabilidad = 0
                probabilidad = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.PROBABILIDAD_ARCHI_MOBS = probabilidad
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se configuro a " + MainServidor.PROBABILIDAD_ARCHI_MOBS
                        .toString() + " probabilidad de archi mobs")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "TIEMPO_ESTRELLAS_RECURSOS", "TIEMPO_RECURSOS_ESTRELLAS", "SEGUNDOS_ESTRELLAS_RECURSOS" -> try {
                var segundos = 0
                segundos = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.SEGUNDOS_ESTRELLAS_RECURSOS = segundos
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se configuro a " + MainServidor.SEGUNDOS_ESTRELLAS_RECURSOS
                        .toString() + " segundos la recarga de estrellas de mobs")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "RESSOURCES_MAP_STARS", "STARS_RESSOURCES_MAP", "ESTRELLAS_RECURSOS_MAPA", "SUBIR_ESTRELLAS_RECURSOS_MAPA" -> try {
                var estrellas = 0
                estrellas = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    1
                }
                _perso.getMapa().subirEstrellasOI(estrellas)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se subio $estrellas estrellas recursos a este mapa")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "STARS_RESSOURCES", "RESSOURCES_STARS", "ESTRELLAS_RECURSOS", "SUBIR_ESTRELLAS_RECURSOS" -> try {
                var estrellas = 0
                estrellas = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    1
                }
                Mundo.subirEstrellasOI(estrellas)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se subio $estrellas estrellas a todos los recursos")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "MAPA_STARS_MOBS", "MAPA_MOBS_STARS", "MAP_MOBS_STARS", "MAP_STARS_MOBS", "MOB_MAP_STARS", "STARS_MOBS_MAP", "UP_STARS_MOBS_MAP", "ESTRELLAS_MAPA_MOBS", "MOBS_ESTRELLAS_MAPA", "MOB_ESTRELLAS_MAPA", "ESTRELLAS_MOBS_MAPA", "MAPA_ESTRELLAS_MOBS", "MAPA_ESTRELLAS_MOB", "MAPA_MOBS_ESTRELLAS", "MAPA_MOB_ESTRELLAS", "ESTRELLAS_MOB_MAPA", "SUBIR_ESTRELLAS_MOBS_MAPA" -> try {
                var estrellas = 0
                estrellas = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    1
                }
                mapa.subirEstrellasMobs(estrellas)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se subio " + estrellas + " estrellas mob al mapa " + mapa.getID())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "MOBS_ESTRELLAS_TODOS", "MOB_ESTRELLAS_TODOS", "MOBS_STARS_TODOS", "TODOS_MOBS_ESTRELLAS", "TODOS_MOBS_STARS", "TODOS_STARS_MOBS", "TODOS_ESTRELLAS_MOBS", "ALL_MOBS_STARS", "ALL_MOBS_ESTRELLAS", "ALL_STARS_MOBS", "UP_ALL_STARS_MOBS", "ESTRELLAS_MOBS_TODOS", "SUBIR_ESTRELLAS_MOB_TODOS" -> try {
                var estrellas = 0
                estrellas = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    1
                }
                Mundo.subirEstrellasMobs(estrellas)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se subio $estrellas estrellas a todos los mobs")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "SHOW_A", "MOSTRAR_A", "SHOW_RESTRICTIONS_A", "MOSTRAR_RESTRICCIONES_A" -> try {
                objetivo = _perso
                if (infos!!.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, objetivo.mostrarmeA())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "SHOW_B", "MOSTRAR_B", "SHOW_RESTRICTIONS_B", "MOSTRAR_RESTRICCIONES_B" -> try {
                objetivo = _perso
                if (infos!!.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, objetivo.mostrarmeB())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "SET_RESTRICCIONES_A", "RESTRICCIONES_A", "MODIFICAR_A", "RESTRICCION_A" -> try {
                var restriccion = 0
                var modificador = 0
                try {
                    restriccion = Integer.parseInt(infos!![1])
                    modificador = Integer.parseInt(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                objetivo = _perso
                if (infos.size > 3) {
                    objetivo = Mundo.getPersonajePorNombre(infos[3])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                objetivo.modificarA(restriccion, restriccion xor modificador)
                GestorSalida.ENVIAR_AR_RESTRICCIONES_PERSONAJE(objetivo)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se coloco la restriccionA " + objetivo.getRestriccionesA()
                        .toString() + " al pj " + objetivo.getNombre())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "SET_RESTRICCIONES_B", "RESTRICCIONES_B", "MODIFICAR_B", "RESTRICCION_B" -> try {
                var restriccion = 0
                var modificador = 0
                try {
                    restriccion = Integer.parseInt(infos!![1])
                    modificador = Integer.parseInt(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                objetivo = _perso
                if (infos.size > 3) {
                    objetivo = Mundo.getPersonajePorNombre(infos[3])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                objetivo.modificarB(restriccion, restriccion xor modificador)
                objetivo.refrescarEnMapa()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se coloco la restriccionB " + objetivo.getRestriccionesB()
                        .toString() + " al pj " + objetivo.getNombre())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "PANEL_ALL", "PANEL_ONLINE", "PANEL_TODOS" -> try {
                infos = mensaje.split(" ", 2)
                GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION_TODOS(infos!![1])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento incorrecto")
            }
            "PANEL_ALONE", "PANEL", "PANEL_SOLO" -> try {
                infos = mensaje.split(" ", 2)
                GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(_perso, infos!![1])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento incorrecto")
            }
            "PREMIO_CACERIA" -> try {
                Mundo.KAMAS_OBJ_CACERIA = infos!![1] // kamas | id,cant;id,cant
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se fijo el premio de la caceria a: " + infos[1])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
            }
            "NOMBRE_CACERIA" -> try {
                Mundo.NOMBRE_CACERIA = infos!![1]
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se fijo el nombre de la caceria a: " + infos[1])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
            }
            "LIMPIAR_CACERIA" -> try {
                Mundo.KAMAS_OBJ_CACERIA = ""
                Mundo.NOMBRE_CACERIA = ""
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se limpio el nombre caceria y premio caceria")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
            }
            "MAPAS_KOLISEO" -> try {
                MainServidor.MAPAS_KOLISEO = infos!![1]
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se fijo la lista de mapas koliseo a: " + infos[1])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
            }
            "LISTA_RASTREOS" -> try {
                val strB = StringBuilder()
                for (i in ServidorSocket.RASTREAR_CUENTAS) {
                    if (strB.length() > 0) {
                        strB.append(", ")
                    }
                    strB.append(i)
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Las ids de las cuentas que estan siendo rastreadas son " + strB
                        .toString())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }
            "BORRAR_RASTREOS" -> try {
                ServidorSocket.RASTREAR_CUENTAS.clear()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se limpio la lista de rastreados")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }
            "SPY_PERSO", "SPY_PJ", "ESPIAR_PJ", "ESPIAR_JUGADOR", "SPY_PLAYER", "RASTREAR_PJ" -> try {
                objetivo = try {
                    Mundo.getPersonajePorNombre(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Personaje no existe")
                    return
                }
                if (!ServidorSocket.RASTREAR_CUENTAS.contains(objetivo.getCuentaID())) {
                    ServidorSocket.RASTREAR_CUENTAS.add(objetivo.getCuentaID())
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se agregó a la lista de rastreos: " + infos[1])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }
            "SPY_ACCOUNT", "SPY_COMPTE", "RASTREAR_CUENTA" -> try {
                val cuenta: Cuenta
                cuenta = try {
                    Mundo.getCuentaPorNombre(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Cuenta no existe")
                    return
                }
                if (cuenta == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Cuenta no existe")
                    return
                }
                if (!ServidorSocket.RASTREAR_CUENTAS.contains(cuenta.getID())) {
                    ServidorSocket.RASTREAR_CUENTAS.add(cuenta.getID())
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se agregó a la lista de rastreos: " + infos[1])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }
            "MODIFY_OBJETIVE_MISION", "MODIFICA_OBJETIVO_MISION", "MODIFICAR_MISION_OBJETIVO", "MODIFICA_MISION_OBJETIVO", "MODIFICAR_OBJETIVO_MISION" -> try {
                var id = 0
                var args: String? = ""
                id = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                try {
                    args = infos[2]
                } catch (e: Exception) {
                }
                val objMision: MisionObjetivoModelo = Mundo.getMisionObjetivoModelo(id)
                if (objMision == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El objetivo mision no existe")
                    return
                }
                objMision.setArgs(args)
                GestorSQL.UPDATE_OBJETIVO_MISION(id, args)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El objetivo mision (" + id + ") tipo: " + objMision.getTipo()
                        + " ha modificado sus args a " + args)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }
            "MODIFY_RECOMPENSE_STEP", "MODIFICA_RECOMPENSA_ETAPA", "MODIFICA_PREMIO_ETAPA", "MODIFICAR_RECOMPENSA_ETAPA" -> try {
                var id = 0
                var args: String? = ""
                id = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                try {
                    args = infos[2]
                } catch (e: Exception) {
                }
                val etapa: MisionEtapaModelo = Mundo.getEtapa(id)
                if (etapa == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La etapa de misión no existe")
                    return
                }
                etapa.setRecompensa(args)
                GestorSQL.UPDATE_RECOMPENSA_ETAPA(id, args)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La etapa de misión (" + id + ") " + etapa.getNombre()
                        + " ha modificado sus recompensas a " + args)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }
            "MODIFICA_ETAPA", "MODIFY_STEP", "MODIFICAR_ETAPA" -> try {
                var id = 0
                var args: String? = ""
                id = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                try {
                    args = infos[2]
                } catch (e: Exception) {
                }
                val etapa: MisionEtapaModelo = Mundo.getEtapa(id)
                if (etapa == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La etapa de misión no existe")
                    return
                }
                etapa.setObjetivos(args)
                GestorSQL.UPDATE_ETAPA(id, args)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La etapa de misión (" + id + ") " + etapa.getNombre()
                        + " ha modificado sus objetivos a " + args)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }
            "INFO_STEP", "INFO_STEPS", "INFO_ETAPAS", "INFO_ETAPA" -> try {
                var id = 0
                id = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                val etapa: MisionEtapaModelo = Mundo.getEtapa(id)
                if (etapa == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La etapa de misión no existe")
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La etapa de misión (" + id + ") " + etapa.getNombre()
                        + " tiene como objetivos: " + etapa.strObjetivos())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }
            "MODIFY_QUEST", "MODIFY_MISSION", "MODIFICAR_QUEST", "MODIFICAR_MISION", "MODIFICA_MISION", "MODIFICA_QUEST" -> try {
                var id = 0
                var pregDarMision: String? = ""
                var pregMisCumplida: String? = ""
                var pregMisIncompleta: String? = ""
                var etapas: String? = ""
                id = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                try {
                    etapas = infos[2]
                } catch (e: Exception) {
                }
                try {
                    pregDarMision = infos[3]
                } catch (e: Exception) {
                }
                try {
                    pregMisCumplida = infos[4]
                } catch (e: Exception) {
                }
                try {
                    pregMisIncompleta = infos[5]
                } catch (e: Exception) {
                }
                val mision: MisionModelo = Mundo.getMision(id)
                if (mision == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La misión no existe")
                    return
                }
                mision.setEtapas(etapas)
                mision.setPreguntas(pregDarMision, Mision.ESTADO_NO_TIENE)
                mision.setPreguntas(pregMisCumplida, Mision.ESTADO_COMPLETADO)
                mision.setPreguntas(pregMisIncompleta, Mision.ESTADO_INCOMPLETO)
                GestorSQL.UPDATE_MISION(id, etapas, pregDarMision, pregMisCumplida, pregMisIncompleta)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La misión (" + id + ") " + mision.getNombre()
                        + " ha modificado sus etapas: " + etapas + ", pregDarMision: " + pregDarMision + ", pregMisCumplida: "
                        + pregMisCumplida + ", pregMisIncompleta: " + pregMisIncompleta)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }
            "INFO_QUEST", "INFO_MISION" -> try {
                var id = 0
                id = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                val mision: MisionModelo = Mundo.getMision(id)
                if (mision == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La misión no existe")
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La misión (" + id + ") " + mision.getNombre()
                        + " tiene como info etapas: " + mision.strEtapas() + ", pregDarMision: " + mision.strMisionPregunta(
                        Mision.ESTADO_NO_TIENE) + ", pregMisCumplida: " + mision.strMisionPregunta(Mision.ESTADO_COMPLETADO)
                        + ", pregMisIncompleta: " + mision.strMisionPregunta(Mision.ESTADO_INCOMPLETO))
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }
            "NIVEL_OBJETO_MODELO" -> try {
                val id: Int = Integer.parseInt(infos!![1])
                val nivel: Short = Short.parseShort(infos[2])
                val objModelo: ObjetoModelo = Mundo.getObjetoModelo(id)
                if (objModelo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El objeto no existe")
                    return
                }
                objModelo.setNivel(nivel)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modificó el objeto (" + id + ") " + objModelo.getNombre()
                        + " con nivel " + objModelo.getNivel())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
            }
            "GFX_OBJETO_MODELO" -> try {
                val id: Int = Integer.parseInt(infos!![1])
                val objModelo: ObjetoModelo = Mundo.getObjetoModelo(id)
                if (objModelo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El objeto no existe")
                    return
                }
                objModelo.setGFX(Integer.parseInt(infos[2]))
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modificó el objeto (" + id + ") " + objModelo.getNombre()
                        + " con gfx " + infos[2])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
            }
            "PREPARAR_LISTA_NIVEL" -> try {
                Mundo.prepararListaNivel()
                GestorSalida.ENVIAR_ÑB_LISTA_NIVEL_TODOS()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se actualizó la lista de niveles modificados")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }
            "PREPARAR_LISTA_GFX" -> try {
                Mundo.prepararListaGFX()
                GestorSalida.ENVIAR_ÑA_LISTA_GFX_TODOS()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se actualizó la lista de GFXs modificados")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }
            "AGREGAR_MOBS_MAPA", "ADD_MOBS_MAPA", "ADD_MOBS", "AGREGAR_MOBS", "INSERTAR_MOBS" -> try {
                var mobs: String? = ""
                mobs = if (infos!!.size > 1) {
                    infos[1]
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Arguments incorrects")
                    return
                }
                try {
                    GestorSQL.UPDATE_SET_MOBS_MAPA(mapa.getID(), mobs)
                    mapa.insertarMobs(mobs)
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Has cometido algun error")
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se inserto la lista de mobs " + mobs + " al mapa " + mapa.getID())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "SET_DATE_MAP", "SET_FECHA_MAPA" -> try {
                var mapaID: Short = 0
                var date: String? = ""
                try {
                    mapaID = Short.parseShort(infos!![1])
                    date = infos!![2]
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                mapa = Mundo.getMapa(mapaID)
                if (mapa == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Mapa no existe")
                    return
                }
                mapa.setFecha(date)
                GestorSQL.UPDATE_FECHA_MAPA(mapa.getID(), date)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se cambio la fecha del mapa " + mapa.getID().toString() + " a " + date)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "REFRESH_MAP", "REFRESCAR_MAPA", "RELOAD_MAP", "RECARGAR_MAPA", "RELOAD_DATE_KEY_MAPDATA_MAP", "MAP_OLD", "MAPA_VIEJO", "CAMBIAR_MAPA_VIEJO", "CHANGE_MAP_OLD" -> try {
                var mapaID: Short = 0
                try {
                    mapaID = Short.parseShort(infos!![1])
                } catch (e: Exception) {
                }
                val key: Array<String?> = GestorSQL.GET_NUEVA_FECHA_KEY(mapaID).split(Pattern.quote("|"))
                // if (key.length < 20) {
                // GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "tiene una key muy corta");
                // }
                mapa = Mundo.getMapa(mapaID)
                if (mapa == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Mapa no existe")
                    return
                }
                mapa.setKeyMapData(key[0], key[1], key[2])
                GestorSQL.UPDATE_FECHA_KEY_MAPDATA(mapa.getID(), key[0], key[1], key[2])
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se cambió el mapa " + mapa.getID().toString() + " por un mapa con key")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "LIST_MOBS", "LISTA_MOBS" -> try {
                var id = 0
                val strB = StringBuilder()
                id = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento incorrecto")
                    return
                }
                for (mMod in Mundo.MOBS_MODELOS.values()) {
                    if (mMod.getTipoMob() === id) {
                        strB.append("ID: " + mMod.getID().toString() + " - Nombre: " + mMod.getNombre().toString() + " - Niveles: " + mMod
                                .listaNiveles().toString() + " - Colores: " + mMod.getColores().toString() + "\n")
                    }
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Los Mobs Tipo Criatura - " + Constantes.getNombreTipoMob(id)
                        .toString() + " son:\n" + strB.toString())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "LIST_TYPE_MOBS", "LISTA_TIPO_MOBS", "LIST_CREATURES_TYPE", "LIST_TYPE_CREATURES", "LIST_TYPE_CRIATURES", "LISTA_TIPO_CRIATURAS" -> try {
                val strB = StringBuilder()
                var i = -1
                while (i < 100) {
                    if (!Constantes.getNombreTipoMob(i).isEmpty()) {
                        strB.append("""Tipo ID: $i - Criaturas: ${Constantes.getNombreTipoMob(i)}
""")
                    }
                    i++
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, """
     Lista Tipo Criatura son:
     ${strB.toString()}
     """.trimIndent())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "BORRAR_OTRO_INTERACTIVO", "DELETE_OTRO_INTERACTIVO", "ELIMINAR_OTRO_INTERACTIVO", "DEL_OTRO_INTERACTIVO", "BORRAR_OTRO_OI", "ELIMINAR_OTRO_OI", "DEL_OTRO_OI" -> try {
                var id = 0
                var mapaID: Short = 0
                var celdaID: Short = 0
                try {
                    id = Integer.parseInt(infos!![1])
                    mapaID = Short.parseShort(infos[2])
                    celdaID = Short.parseShort(infos[3])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos inválidos")
                    return
                }
                Mundo.borrarOtroInteractivo(id, mapaID, celdaID, 0, false)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se borraron todos los otros interactivos con gfxID: " + id
                        + " mapaID: " + mapaID + " celdaID: " + celdaID)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "ADD_OTRO_INTERACTIVO", "ADD_OTRO_OI", "AGREGAR_OTRO_INTERACTIVO" -> try {
                var id = 0
                var accionID = 0
                var tiempoRecarga = 0
                var mapaID: Short = 0
                var celdaID: Short = 0
                var args: String? = ""
                var condicion: String? = ""
                var descripcion: String? = ""
                val strB = StringBuilder()
                try {
                    id = Integer.parseInt(infos!![1])
                    mapaID = Short.parseShort(infos[2])
                    celdaID = Short.parseShort(infos[3])
                    accionID = Integer.parseInt(infos[4])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos inválidos")
                    return
                }
                try {
                    args = infos[5]
                } catch (e: Exception) {
                }
                try {
                    condicion = infos[6]
                } catch (e: Exception) {
                }
                try {
                    tiempoRecarga = Integer.parseInt(infos[7])
                } catch (e: Exception) {
                }
                try {
                    descripcion = infos[8]
                } catch (e: Exception) {
                }
                strB.append("Se creo acción para Otro Interactivo GfxID: " + id + ", mapaID: " + mapaID + ", celdaID: "
                        + celdaID + ", accionID: " + accionID + ", args: " + args + ", condicion: " + condicion + ", tiempoRecarga: "
                        + tiempoRecarga)
                Mundo.borrarOtroInteractivo(id, mapaID, celdaID, accionID, true)
                val otro = OtroInteractivo(id, mapaID, celdaID, accionID, args, condicion, tiempoRecarga)
                Mundo.addOtroInteractivo(otro)
                GestorSQL.INSERT_OTRO_INTERACTIVO(id, mapaID, celdaID, accionID, args, condicion, tiempoRecarga, descripcion)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, strB.toString())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "MOSTRAR_OTROS_INTERACTIVOS", "MOSTRAR_OTROS_OIS", "LISTAR_OTROS_INTERACTIVOS", "LIST_OTROS_INTERACTIVOS", "SHOW_OTHER_INTERACTIVES", "LISTA_OTROS_OIS", "LISTAR_OTROS_OIS" -> try {
                var mapaID: Int = mapa.getID()
                val strB = StringBuilder()
                try {
                    mapaID = Short.parseShort(infos!![1])
                } catch (e: Exception) {
                }
                for (oi in Mundo.OTROS_INTERACTIVOS) {
                    if (oi.getMapaID() !== mapaID) {
                        continue
                    }
                    strB.append("\n")
                    strB.append("Mapa: " + oi.getMapaID().toString() + " Celda: " + oi.getCeldaID().toString() + " GfxID: " + oi.getGfxID()
                            .toString() + " Accion: " + oi.getAccionID().toString() + " Args: " + oi.getArgs().toString() + " Condicion: " + oi.getCondicion())
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Los otros interactivos son:" + strB.toString())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "ACCESO_ADMIN_MINIMO", "BLOQUEAR", "BLOCK_GM" -> try {
                var accesoGM: Byte = 0
                var botarRango: Byte = 0
                try {
                    accesoGM = Byte.parseByte(infos!![1])
                    botarRango = Byte.parseByte(infos!![2])
                } catch (e: Exception) {
                }
                MainServidor.ACCESO_ADMIN_MINIMO = accesoGM.toInt()
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso,
                            "Le serveur est désormais accessible au joueur dont le GM est supérieur à : $accesoGM")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Server bloqueado a Nivel GM : $accesoGM")
                }
                if (botarRango > 0) {
                    for (pj in Mundo.getPersonajesEnLinea()) {
                        if (pj.getAdmin() < botarRango) {
                            try {
                                GestorSalida.ENVIAR_M0_MENSAJE_BASICOS_SVR_MUESTRA_DISCONNECT(pj.getServidorSocket(), "19", "", "")
                                pj.getServidorSocket().cerrarSocket(true, "command BLOCK GM")
                            } catch (e: Exception) {
                            }
                        }
                    }
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso,
                                "Le joueurs dont le GM est inférieur à celui spécifié ont été expulsés.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Los jugadores nivel GM inferior a " + botarRango
                                + " han sido expulsados.")
                    }
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "ADD_ACTION_REPONSE", "ADD_ACTION_ANSWER", "ADD_ACCION_RESPUESTA", "ADD_ACCIONES_RESPUESTA", "ADD_ACCIONES_RESPUESTAS", "FIJAR_ACCION_RESPUESTA", "AGREGAR_ACCION_RESPUESTA" -> try {
                infos = mensaje.split(" ", 5)
                var id = 0
                var accionID = 0
                var args: String? = ""
                var condicion: String? = ""
                val strB = StringBuilder()
                try {
                    id = Integer.parseInt(infos!![1])
                    accionID = Integer.parseInt(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos inválidos")
                    return
                }
                try {
                    args = infos[3]
                } catch (e: Exception) {
                }
                try {
                    condicion = infos[4]
                } catch (e: Exception) {
                }
                var respuesta: RespuestaNPC? = Mundo.getRespuestaNPC(id)
                if (respuesta == null) {
                    respuesta = RespuestaNPC(id)
                    Mundo.addRespuestaNPC(respuesta)
                }
                val accion = Accion(accionID, args, condicion)
                respuesta.addAccion(accion)
                strB.append("La acción respuesta " + respuesta.getID().toString() + ", accionID: " + accion.getID().toString() + ", args: " + accion
                        .getArgs().toString() + ", condición: " + accion.getCondicion().toString() + " agregada")
                if (GestorSQL.REPLACE_ACCIONES_RESPUESTA(respuesta.getID(), accion.getID(), accion.getArgs(), accion
                                .getCondicion())) {
                    strB.append(" a la BDD")
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, strB.toString())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "DELETE_ACTIONS_ANSWER", "DELETE_ACTIONS_REPONSE", "DEL_ACTIONS_ANSWER", "DEL_ACTIONS_REPONSE", "REMOVE_ACTONS_REPONSE", "BORRAR_ACCIONES_RESPUESTA", "BORRAR_ACCION_RESPUESTA" -> try {
                var id = 0
                id = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos inválidos")
                    return
                }
                val respuesta: RespuestaNPC = Mundo.getRespuestaNPC(id)
                if (respuesta == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Respuesta inválida")
                    return
                }
                respuesta.borrarAcciones()
                GestorSQL.DELETE_ACCIONES_RESPUESTA(id)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se borraron todas las acciones de la respuesta $id")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "FIX_QUESTION_NPC", "FIX_QUESTION", "FIJAR_PREGUNTA", "FIJAR_NPC_PREGUNTA", "FIJAR_PREGUNTA_NPC" -> try {
                var npcID = 0
                var preguntaID = 0
                val strB = StringBuilder()
                try {
                    npcID = Integer.parseInt(infos!![1])
                    preguntaID = Integer.parseInt(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos inválidos")
                    return
                }
                val npcModelo: NPCModelo = Mundo.getNPCModelo(npcID)
                if (npcModelo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "NPC no existe")
                    return
                }
                strB.append("Fija al NPC Modelo " + npcID + " - Nombre: " + npcModelo.getNombre() + ", Pregunta: "
                        + preguntaID)
                npcModelo.setPreguntaID(preguntaID)
                if (GestorSQL.UPDATE_NPC_PREGUNTA(npcID, preguntaID)) {
                    strB.append(" a la BDD")
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, strB.toString())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "ADD_ANSWERS", "FIX_ANSWERS", "FIX_REPONSES", "WRITE_ANSWERS", "FIJAR_RESPUESTAS", "FIJAR_RESPUESTAS_PREGUNTA", "SET_ANSWERS", "SET_RESPUESTAS" -> try {
                val strB = StringBuilder()
                var id = 0
                var args: String? = ""
                var respuestas: String? = ""
                id = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos inválidos")
                    return
                }
                try {
                    respuestas = infos[2]
                } catch (e: Exception) {
                }
                try {
                    args = infos[3]
                } catch (e: Exception) {
                }
                var pregunta: PreguntaNPC? = Mundo.getPreguntaNPC(id)
                if (pregunta == null) {
                    pregunta = PreguntaNPC(id, respuestas, args, "")
                    Mundo.addPreguntaNPC(pregunta)
                } else {
                    pregunta.setRespuestas(respuestas)
                    pregunta.setParams(args)
                }
                strB.append("Parámetros de la pregunta " + id + " => respuestas: " + pregunta.getStrRespuestas() + ", args: "
                        + pregunta.getParams() + ", alternos: " + pregunta.getStrAlternos())
                if (GestorSQL.REPLACE_PREGUNTA_NPC(pregunta)) {
                    strB.append(" a la BDD")
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, strB.toString())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "SET_ALTERNOS_PREGUNTA", "SET_ALTERNOS_QUESTION", "SET_PREGUNTA_ALTERNAS", "SET_PREGUNTA_ALTERNOS", "FIJAR_PREGUNTA_ALTERNOS", "FIJAR_ALTERNOS", "FIJAR_ALTENOS_PREGUNTA", "FIJAR_PREGUNTA_ALTERNAS", "SET_ALTERNOS", "SET_QUESTIONS_CONDITIONS" -> try {
                var id = 0
                var alternos: String? = ""
                val strB = StringBuilder()
                id = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos inválidos")
                    return
                }
                try {
                    alternos = infos[2]
                } catch (e: Exception) {
                }
                var pregunta: PreguntaNPC? = Mundo.getPreguntaNPC(id)
                if (pregunta == null) {
                    pregunta = PreguntaNPC(id, "", "", alternos)
                    Mundo.addPreguntaNPC(pregunta)
                } else {
                    pregunta.setPreguntasCondicionales(alternos)
                }
                strB.append("Parámetros de la pregunta " + id + " => respuestas: " + pregunta.getStrRespuestas() + ", args: "
                        + pregunta.getParams() + ", alternos: " + pregunta.getStrAlternos())
                if (GestorSQL.REPLACE_PREGUNTA_NPC(pregunta)) {
                    strB.append(" a la BDD")
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, strB.toString())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "BUSCAR_PREGUNTA", "BUSCAR_PREGUNTAS", "SEARCH_QUESTIONS", "SEARCH_QUESTION" -> try {
                infos = mensaje.split(" ", 2)
                val buscar = infos!![1]
                GestorSalida.enviar(_perso, "DBQ" + buscar.toUpperCase(), true)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "BUSCAR_RESPUESTA", "BUSCAR_RESPUESTAS", "SEARCH_ANSWERS", "SEARCH_ANSWER" -> try {
                infos = mensaje.split(" ", 2)
                val buscar = infos!![1]
                GestorSalida.enviar(_perso, "DBA" + buscar.toUpperCase(), true)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "SHOW_QUESTIONS", "SHOW_QUESTION", "LISTAR_PREGUNTAS", "LISTA_PREGUNTAS", "MOSTRAR_PREGUNTAS" -> try {
                for (ss in infos!![1].split(",")) {
                    if (ss.isEmpty()) {
                        continue
                    }
                    var respuestaID = 0
                    respuestaID = try {
                        Integer.parseInt(ss)
                    } catch (e: Exception) {
                        continue
                    }
                    val pregunta: PreguntaNPC = Mundo.getPreguntaNPC(respuestaID) ?: continue
                    val sB = StringBuilder()
                    sB.append("""
	--> Answers: ${pregunta.getStrRespuestas()}""")
                    sB.append("""
	--> Alternates: ${pregunta.getStrAlternos()}""")
                    GestorSalida.enviar(_perso, "DLQ" + respuestaID + "|" + sB.toString(), true)
                }
                GestorSalida.enviar(_perso, "DX", true)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
            }
            "SHOW_ANSWER", "SHOW_ANSWERS", "SHOW_REPONSES", "LISTAR_RESPUESTAS", "LISTA_RESPUESTAS", "MOSTRAR_RESPUESTAS" -> try {
                for (ss in infos!![1].split(",")) {
                    if (ss.isEmpty()) {
                        continue
                    }
                    var respuestaID = 0
                    respuestaID = try {
                        Integer.parseInt(ss)
                    } catch (e: Exception) {
                        continue
                    }
                    val respuesta: RespuestaNPC = Mundo.getRespuestaNPC(respuestaID) ?: continue
                    val sB = StringBuilder()
                    sB.append("""
	--> Condition: ${respuesta.getCondicion()}""")
                    for (a in respuesta.getAcciones()) {
                        sB.append("""
	--> Action ID: ${a.getID().toString()}, Args: ${a.getArgs()}""")
                    }
                    GestorSalida.enviar(_perso, "DLA" + respuestaID + "|" + sB.toString(), true)
                }
                GestorSalida.enviar(_perso, "DX", true)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
            }
            "LISTAR_RESPUESTAS_POR_TIPO_Y_ARGS", "LISTAR_RESPUESTAS_POR_TIPO_ARGS", "LISTAR_RESPUESTAS_POR_TIPO_O_ARGS", "LISTAR_RESPUESTAS_TIPO_ARGS", "LISTAR_ACCIONES_RESPUESTAS_POR_TIPO_Y_ARGS", "LISTAR_ACCIONES_RESPUESTAS_POR_TIPO_ARGS", "LISTAR_ACCIONES_RESPUESTAS_POR_TIPO_O_ARGS", "LISTAR_ACCIONES_RESPUESTAS_TIPO_ARGS", "LISTA_RESPUESTAS_POR_TIPO_Y_ARGS", "LISTA_RESPUESTAS_POR_TIPO_ARGS", "LISTA_RESPUESTAS_POR_TIPO_O_ARGS", "LISTA_RESPUESTAS_TIPO_ARGS", "LISTA_ACCIONES_RESPUESTAS_POR_TIPO_Y_ARGS", "LISTA_ACCIONES_RESPUESTAS_POR_TIPO_ARGS", "LISTA_ACCIONES_RESPUESTAS_POR_TIPO_O_ARGS", "LISTA_ACCIONES_RESPUESTAS_TIPO_ARGS" -> try {
                var id = -100
                var args: String? = ""
                val strB = StringBuilder()
                try {
                    id = Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                }
                try {
                    args = infos!![2]
                } catch (e: Exception) {
                }
                for (respuesta2 in Mundo.NPC_RESPUESTAS.values()) {
                    var b = false
                    for (a in respuesta2.getAcciones()) {
                        if (id != -100 && a.getID() !== id) {
                            continue
                        }
                        if (!args.isEmpty() && !a.getArgs().toUpperCase().contains(args.toUpperCase())) {
                            continue
                        }
                        b = true
                        break
                    }
                    if (!b) {
                        continue
                    }
                    if (strB.length() > 0) {
                        strB.append("\n----------------------------------\n")
                    }
                    strB.append("Acciones de la respuesta ID " + respuesta2.getID().toString() + ", condición: " + respuesta2
                            .getCondicion())
                    for (a in respuesta2.getAcciones()) {
                        strB.append("""
	Accion ID: ${a.getID().toString()}, Args: ${a.getArgs()}""")
                    }
                }
                if (strB.length() === 0) {
                    strB.append("No se encontraron respuestas con esos datos")
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, strB.toString())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "LISTA_PREGUNTAS_CON_RESPUESTAS", "LISTA_PREGUNTAS_CON_RESPUESTA", "LISTAR_PREGUNTAS_CON_RESPUESTAS", "LISTAR_PREGUNTAS_CON_RESPUESTA" -> try {
                var id = 0
                val strB = StringBuilder()
                id = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento incorrecto")
                    return
                }
                for (pregunta2 in Mundo.NPC_PREGUNTAS.values()) {
                    if (pregunta2.getRespuestas().contains(id)) {
                        if (strB.length() > 0) {
                            strB.append("\n")
                        }
                        strB.append("Respuestas de la pregunta " + pregunta2.getID().toString() + ", respuestas: " + pregunta2
                                .getStrRespuestas())
                    }
                }
                if (strB.length() === 0) {
                    strB.append("No se encontraron preguntas con esos datos")
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, strB.toString())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "UPDATE_HOUSE", "MODIFY_HOUSE", "MODIFICAR_CASA", "UPDATE_CASA", "MAPA_DENTRO_CASA", "ACTUALIZAR_CASA" -> try {
                var mapaID: Short = 0
                var celdaID: Short = 0
                try {
                    mapaID = Short.parseShort(infos!![1])
                    celdaID = Short.parseShort(infos!![2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento incorrecto")
                    return
                }
                var casa: Casa? = null
                val ancho: Short = mapa.getAncho()
                val dir = shortArrayOf((-ancho).toShort(), (-(ancho - 1).toShort()).toShort(), (ancho - 1).toShort(), ancho, 0)
                var i = 0
                while (i < 5) {
                    casa = Mundo.getCasaPorUbicacion(mapa.getID(), _perso.getCelda().getID() + dir[i])
                    if (casa != null) {
                        break
                    }
                    i++
                }
                if (casa == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "No existe la casa")
                    return
                }
                casa.setCeldaIDDentro(celdaID)
                casa.setMapaIDDentro(mapaID)
                GestorSQL.UPDATE_CELDA_MAPA_DENTRO_CASA(casa)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se actualizo la casa " + casa.getID().toString() + " , con mapaID dentro: "
                        + mapaID.toString() + " y como celdaID dentro: " + celdaID)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "SHOW_STATS_OBJETO", "GET_STATS_OBJETO", "INFO_ITEM", "INFO_OBJETO" -> try {
                var id = -1
                try {
                    id = Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                }
                val obj: Objeto = Mundo.getObjeto(id)
                if (obj == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Objeto nulo")
                    return
                }
                val objModelo: ObjetoModelo = obj.getObjModelo()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Info del objeto $id: \nNombre Objeto - " + objModelo
                        .getNombre() + "\nNivel - " + objModelo.getNivel() + "\nTipo - " + objModelo.getTipo() + "\nPosicion - " + obj
                        .getPosicion() + "\nString Stats - " + obj.convertirStatsAString(true) + "\n---------------------------")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "GUARDAR", "SALVAR", "SAVE" -> {
                if (Mundo.SERVIDOR_ESTADO === Constantes.SERVIDOR_SALVANDO) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Une sauvegarde est déjà en cours.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso,
                                "No se puede ejecutar el comando, porque el server ya se esta salvando")
                    }
                    return
                }
                MainServidor.redactarLogServidorln("Se uso el comando SALVAR (COMANDOS) por " + _perso.getNombre())
                SalvarServidor(false)
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Lancement de la sauvegarde...")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Salvando servidor...")
                }
            }
            "GUARDAR_TODOS", "SALVAR_TODOS", "SAVE_ALL" -> {
                if (Mundo.SERVIDOR_ESTADO === Constantes.SERVIDOR_SALVANDO) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Une sauvegarde est déjà en cours.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso,
                                "No se puede ejecutar el comando, porque el server ya se esta salvando")
                    }
                    return
                }
                MainServidor.redactarLogServidorln("Se uso el comando SALVAR (COMANDOS) por " + _perso.getNombre())
                SalvarServidor(true)
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Lancement de la sauvegarde ONLINE Y OFFLINE ...")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Salvando servidor ONLINE Y OFFLINE ...")
                }
            }
            "SECONDS_ON", "SECONDS_ONLINE", "SEGUNDOS_ON" -> GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El servidor tiene " + ServidorServer.getSegundosON()
                    .toString() + " segundos ONLINE")
            "PASS_TURN", "PASAR_TURNO", "FIN_TURNO", "END_TURN", "CHECK_TURNO", "DEBUG_TURN" -> {
                if (_perso.getPelea() == null) {
                    return
                }
                _perso.getPelea().pasarTurno(null)
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Debug du tour..")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se verifico el pase de turno ")
                }
            }
            "SET_TEMP" -> try {
                val strB = StringBuilder()
                if (_perso.getPelea() == null) {
                    return
                }
                try {
                    strB.append(infos!![1])
                } catch (e: Exception) {
                }
                _perso.getPelea().setTempAccion(strB.toString())
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se asigno la tempAccion: " + strB.toString())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "COUNT_OIS", "CONTAR_OBJETOS_INTERACTIVOS", "CONTAR_OIS" -> try {
                var cantidad = 0
                for (celda in mapa.getCeldas().values()) {
                    if (celda.getObjetoInteractivo() != null) {
                        cantidad++
                    }
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Este mapa tiene $cantidad interactivos")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "SHOW_OIS", "MOSTRAR_OBJETOS_INTERACTIVOS", "MOSTRAR_OIS" -> try {
                val strB = StringBuilder()
                for (celda in mapa.getCeldas().values()) {
                    if (celda.getObjetoInteractivo() != null) {
                        strB.append("\n")
                        strB.append("Mapa: " + mapa.getID().toString() + " Celda: " + celda.getID().toString() + " Movimiento: " + celda.getMovimiento()
                                .toString() + " Gfx: " + celda.getObjetoInteractivo().getGfxID())
                        try {
                            strB.append(" ID ObjInt: " + celda.getObjetoInteractivo().getObjIntModelo().getID())
                        } catch (e: Exception) {
                        }
                    }
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Este mapa tiene:" + strB.toString())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "RESETEAR_OBJETOS_INTERACTIVOS", "RESET_OBJETOS_INTERACTIVOS", "RESETEAR_OIS", "REINICIAR_OIS", "REFRESCAR_OIS" -> try {
                var cantidad = 0
                val strB = StringBuilder()
                for (celda in mapa.getCeldas().values()) {
                    if (celda.getObjetoInteractivo() != null) {
                        if (strB.length() > 0) {
                            strB.append("|")
                        }
                        celda.getObjetoInteractivo().recargando(true)
                        strB.append(celda.getID().toString() + ";" + celda.getObjetoInteractivo().getInfoPacket())
                        cantidad++
                    }
                }
                GestorSalida.ENVIAR_GDF_FORZADO_MAPA(mapa, strB.toString())
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se ha refrescado $cantidad interactivos")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "MAX_MERCANTES", "MAX_MERCHANTS" -> try {
                var limite: Byte = 0
                try {
                    limite = Byte.parseByte(infos!![1])
                } catch (e: Exception) {
                }
                if (limite < 0) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                mapa.setMaxMercantes(limite)
                if (!GestorSQL.UPDATE_MAPA_MAX_MERCANTES(mapa.getID(), limite)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrió un error al guardar la actualización en la BD.")
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "En el mapa " + mapa.getID()
                        .toString() + " el max de mercantes ha sido modificado a " + limite)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "MOBS_FOR_GROUP", "MAX_MOBS_POR_GRUPO", "MOBS_POR_GRUPO" -> try {
                var limite: Byte = 0
                try {
                    limite = Byte.parseByte(infos!![1])
                } catch (e: Exception) {
                }
                if (limite <= 0) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                mapa.setMaxMobsPorGrupo(limite)
                if (!GestorSQL.UPDATE_MAPA_MAX_MOB_GRUPO(mapa.getID(), limite)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrió un error al guardar la actualización en la BD.")
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "En el mapa " + mapa.getID()
                        .toString() + " el maximo de mobs por grupo ha sido modificado a " + limite)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "MIN_MOBS_POR_GRUPO" -> try {
                var limite: Byte = 0
                try {
                    limite = Byte.parseByte(infos!![1])
                } catch (e: Exception) {
                }
                if (limite <= 0) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                mapa.setMinMobsPorGrupo(limite)
                if (!GestorSQL.UPDATE_MAPA_MIN_MOB_GRUPO(mapa.getID(), limite)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrió un error al guardar la actualización en la BD.")
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "En el mapa " + mapa.getID()
                        .toString() + " el minimo de mobs por grupo ha sido modificado a " + limite)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "GRUPO_MOBS_POR_MAPA", "MAX_GROUP_MOBS", "MAX_GRUPO_MOBS" -> try {
                var limite: Byte = 0
                try {
                    limite = Byte.parseByte(infos!![1])
                } catch (e: Exception) {
                }
                if (limite < 0) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                mapa.setMaxGrupoDeMobs(limite)
                if (!GestorSQL.UPDATE_MAPA_MAX_GRUPO_MOBS(mapa.getID(), limite)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrió un error al guardar la actualización en la BD.")
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "En el mapa " + mapa.getID()
                        .toString() + " el maximo de grupo mobs ha sido modificado a " + limite)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "REBOOT", "RESET", "SALIR", "RESETEAR", "EXIT" -> try {
                if (Mundo.SERVIDOR_ESTADO === Constantes.SERVIDOR_SALVANDO) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le serveur est en cours de sauvegarde, impossible de reboot.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso,
                                "No se puede cerrar, porque el server se esta guardando, intentar en 5 minutos")
                    }
                    return
                }
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Reboot maintenant.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se esta cerrando el server")
                }
                Reiniciar(0)
            } catch (e: Exception) {
                e.printStackTrace()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }
            "INFO_DROP_MOB_OBJETO", "DROP_POR_OBJETO_MOB" -> try {
                var mobID = 0
                var objModID = 0
                try {
                    mobID = Integer.parseInt(infos!![1])
                    objModID = Integer.parseInt(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Error con los argumentos")
                    return
                }
                val mobModelo: MobModelo = Mundo.getMobModelo(mobID)
                val objModelo: ObjetoModelo = Mundo.getObjetoModelo(objModID)
                if (mobModelo == null || objModelo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Objeto o mob nulos")
                    return
                }
                for (drop in mobModelo.getDrops()) {
                    if (drop.getIDObjModelo() === objModID) {
                        GestorSalida.enviar(_perso, "Ñd" + drop.getProspeccion().toString() + ";" + (drop.getPorcentaje() * 1000).toString() + ";"
                                + drop.getMaximo(), false)
                        break
                    }
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "ADD_DROP", "AGREGAR_DROP" -> try {
                var mobID = 0
                var objModID = 0
                var prospecc = 0
                var max = 0
                var porcentaje = 0f
                var condicion: String? = ""
                try {
                    mobID = Integer.parseInt(infos!![1])
                    objModID = Integer.parseInt(infos[2])
                    prospecc = Integer.parseInt(infos[3])
                    porcentaje = Float.parseFloat(infos[4])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Error con los argumentos")
                    return
                }
                try {
                    max = Integer.parseInt(infos[5])
                } catch (e: Exception) {
                }
                try {
                    condicion = infos[6]
                } catch (e: Exception) {
                }
                val mobModelo: MobModelo = Mundo.getMobModelo(mobID)
                val objModelo: ObjetoModelo = Mundo.getObjetoModelo(objModID)
                if (mobModelo == null || objModelo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Objeto o mob nulos")
                    return
                }
                mobModelo.addDrop(DropMob(objModID, prospecc, porcentaje, max, condicion, false))
                GestorSQL.INSERT_DROP(mobID, objModID, prospecc, porcentaje, max, mobModelo.getNombre(), objModelo
                        .getNombre(), condicion)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se agregó al mob " + mobModelo.getNombre().toString() + " (" + mobModelo
                        .getID().toString() + ") el objeto " + objModelo.getNombre().toString() + " (" + objModelo.getID().toString() + ") con PP " + prospecc.toString() + ", "
                        + porcentaje.toString() + "%, máximo " + max.toString() + " y condición " + condicion)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "LIST_DROPS", "LISTA_DROPS", "LISTA_DROP" -> try {
                val strB = StringBuilder()
                var mobID = 0
                mobID = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Error con los argumentos")
                    return
                }
                val mobModelo: MobModelo = Mundo.getMobModelo(mobID)
                if (mobModelo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Hay valores nulos")
                    return
                }
                for (drop in mobModelo.getDrops()) {
                    val objModelo: ObjetoModelo = Mundo.getObjetoModelo(drop.getIDObjModelo()) ?: continue
                    strB.append(" - " + drop.getIDObjModelo().toString() + " - " + objModelo.getNombre().toString() + "\tProsp: " + drop
                            .getProspeccion().toString() + "\tPorcentaje: " + drop.getPorcentaje().toString() + "%\tMax: " + drop.getMaximo().toString() + "\n")
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, """La listas de drop del mob ${mobModelo.getNombre().toString()} es: 
${strB.toString()}""")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "DEL_DROP", "ELIMINATE_DROP", "ERASER_DROP", "BORRAR_DROP", "DELETE_DROP" -> try {
                var mobID = 0
                var objModID = 0
                try {
                    mobID = Integer.parseInt(infos!![1])
                    objModID = Integer.parseInt(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Error con los argumentos")
                    return
                }
                val objModelo: ObjetoModelo = Mundo.getObjetoModelo(objModID)
                val mobModelo: MobModelo = Mundo.getMobModelo(mobID)
                if (objModelo == null || mobModelo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Hay valores nulos")
                    return
                }
                mobModelo.borrarDrop(objModID)
                GestorSQL.DELETE_DROP(objModID, mobID)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se borro el objeto " + objModelo.getNombre().toString() + " del drop del mob "
                        + mobModelo.getNombre())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "ADD_END_FIGHT", "ADD_END_FIGHT_ACTION", "ADD_ACTION_END_FIGHT", "ADD_ACCION_FIN_PELEA", "AGREGAR_ACCION_FIN_PELEA" -> try {
                infos = mensaje.split(" ", 6)
                var tipo = 0
                var accionID = 0
                var args: String? = ""
                var condicion: String? = ""
                var descripcion: String? = ""
                val strB = StringBuilder()
                try {
                    tipo = Integer.parseInt(infos!![1])
                    accionID = Integer.parseInt(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento inválido")
                    return
                }
                if (infos.size > 3) {
                    args = infos[3]
                }
                if (infos.size > 4) {
                    condicion = infos[4]
                }
                try {
                    if (infos.size > 5) {
                        descripcion = infos[5]
                    }
                } catch (e: Exception) {
                }
                try {
                    if (infos.size > 6) {
                        mapa = Mundo.getMapa(Short.parseShort(infos[6]))
                    }
                } catch (e: Exception) {
                }
                mapa.addAccionFinPelea(tipo, Accion(accionID, args, ""))
                strB.append("Se agregó la accion fin pelea, mapaID: " + mapa.getID().toString() + ", tipoPelea: " + tipo.toString() + ", accionID: "
                        + accionID.toString() + ", args: " + args.toString() + " condicion: " + condicion)
                if (GestorSQL.INSERT_ACCION_FIN_PELEA(mapa.getID(), tipo, accionID, args, condicion, descripcion)) {
                    strB.append(" a la BDD")
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, strB.toString())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }
            "DELETED_ACTION_END_FIGHT", "ELIMNAR_ACCION_FIN_PELEA", "DEL_ACTION_END_FIGHT", "BORRAR_ACCION_FIN_PELEA", "BORRAR_ACCIONES_FIN_PELEA" -> {
                mapa.borrarAccionesPelea()
                GestorSQL.DELETE_ACCION_PELEA(mapa.getID())
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se borraron las acciones de pelea")
            }
            "ESPECTATOR_FIGHT", "ESPECTAR_PELEA", "ESPECTATE_FIGHT", "ESPECTAR_A", "JOIN_FIGHT", "UNIRSE_PELEA" -> try {
                if (infos!!.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                if (objetivo.getPelea() == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no esta en pelea")
                    return
                }
                if (objetivo.getPelea().getFase() < Constantes.PELEA_FASE_COMBATE) {
                    objetivo.getPelea().unirsePelea(_perso, objetivo.getID())
                } else {
                    objetivo.getPelea().unirseEspectador(_perso, true)
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Te uniste a la pelea de " + infos[1])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }
            "SHOW_FIGHTS", "MOSTRAR_PELEAS" -> try {
                val packet = StringBuilder()
                var primero = true
                for (pelea in mapa.getPeleas().values()) {
                    if (!primero) {
                        packet.append("|")
                    }
                    try {
                        val info: String = pelea.strParaListaPelea()
                        if (!info.isEmpty()) {
                            packet.append(info)
                            primero = false
                        }
                    } catch (e: Exception) {
                    }
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, """
     Lista peleas de:
     ${packet.toString()}
     """.trimIndent())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }
            "ADD_GRUPOMOB_FIJO", "AGREGAR_GRUPOMOB_FIJO", "ADD_MOB_FIJO", "AGREGAR_MOB_FIJO", "SPAWN_FIX", "SPAWN_SQL", "AGREGAR_MOB_GRUPO_SQL", "AGREGAR_GRUPO_MOB_SQL", "ADD_GRUPO_MOB_SQL" -> {
                sql = true
                try {
                    var condUnirse: String? = ""
                    var condInicio: String? = ""
                    var grupoData: String? = ""
                    var segundosRespawn = 0
                    var tipoGrupoMob = 0
                    try {
                        grupoData = infos!![1]
                        tipoGrupoMob = Integer.parseInt(infos[2])
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Arguments incorrects")
                        return
                    }
                    if (grupoData.isEmpty()) {
                        return
                    }
                    try {
                        condInicio = infos[3].replaceAll("menor", "<")
                        condInicio = condInicio.replaceAll("mayor", ">")
                        condInicio = condInicio.replaceAll("igual", "=")
                        condInicio = condInicio.replaceAll("diferente", "!")
                    } catch (e: Exception) {
                    }
                    try {
                        condUnirse = infos[4].replaceAll("menor", "<")
                        condUnirse = condUnirse.replaceAll("mayor", ">")
                        condUnirse = condUnirse.replaceAll("igual", "=")
                        condUnirse = condUnirse.replaceAll("diferente", "!")
                    } catch (e: Exception) {
                    }
                    try {
                        segundosRespawn = Integer.parseInt(infos[5])
                    } catch (e: Exception) {
                    }
                    var tipoGrupo: TipoGrupo = Constantes.getTipoGrupoMob(tipoGrupoMob)
                    if (tipoGrupo === TipoGrupo.NORMAL) {
                        tipoGrupo = TipoGrupo.FIJO
                    }
                    val grupoMob: GrupoMob = mapa.addGrupoMobPorTipo(_perso.getCelda().getID(), grupoData, tipoGrupo, condInicio,
                            null, true, 0, "", 0)
                    grupoMob.setCondUnirsePelea(condUnirse)
                    grupoMob.setSegundosRespawn(segundosRespawn)
                    if (sql) {
                        GestorSQL.REPLACE_GRUPOMOB_FIJO(mapa.getID(), _perso.getCelda().getID(), grupoData, tipoGrupoMob,
                                condInicio, segundosRespawn)
                    }
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le groupe monstre a été spawn.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se agrego el grupomob: " + grupoData + " de tipo: " + tipoGrupo
                                + ", condInicio: " + condInicio + ", condUnirse: " + condUnirse + ", tiempoReaparecer: " + segundosRespawn
                                + if (sql) " y guardado en la BD" else "")
                    }
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
                }
            }
            "AGREGAR_MOB_GRUPO", "AGREGAR_GRUPO_MOB", "ADD_GRUPO_MOB", "ADD_GROUP_MOB", "SPAWN_MOBS", "SPAWN_GROUP_MOB", "SPAWN_MOB", "SPAWN" -> try {
                var condUnirse: String? = ""
                var condInicio: String? = ""
                var grupoData: String? = ""
                var segundosRespawn = 0
                var tipoGrupoMob = 0
                try {
                    grupoData = infos!![1]
                    tipoGrupoMob = Integer.parseInt(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Arguments incorrects")
                    return
                }
                if (grupoData.isEmpty()) {
                    return
                }
                try {
                    condInicio = infos[3].replaceAll("menor", "<")
                    condInicio = condInicio.replaceAll("mayor", ">")
                    condInicio = condInicio.replaceAll("igual", "=")
                    condInicio = condInicio.replaceAll("diferente", "!")
                } catch (e: Exception) {
                }
                try {
                    condUnirse = infos[4].replaceAll("menor", "<")
                    condUnirse = condUnirse.replaceAll("mayor", ">")
                    condUnirse = condUnirse.replaceAll("igual", "=")
                    condUnirse = condUnirse.replaceAll("diferente", "!")
                } catch (e: Exception) {
                }
                try {
                    segundosRespawn = Integer.parseInt(infos[5])
                } catch (e: Exception) {
                }
                var tipoGrupo: TipoGrupo = Constantes.getTipoGrupoMob(tipoGrupoMob)
                if (tipoGrupo === TipoGrupo.NORMAL) {
                    tipoGrupo = TipoGrupo.FIJO
                }
                val grupoMob: GrupoMob = mapa.addGrupoMobPorTipo(_perso.getCelda().getID(), grupoData, tipoGrupo, condInicio,
                        null, true, 0, "", 0)
                grupoMob.setCondUnirsePelea(condUnirse)
                grupoMob.setSegundosRespawn(segundosRespawn)
                if (sql) {
                    GestorSQL.REPLACE_GRUPOMOB_FIJO(mapa.getID(), _perso.getCelda().getID(), grupoData, tipoGrupoMob,
                            condInicio, segundosRespawn)
                }
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le groupe monstre a été spawn.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se agrego el grupomob: " + grupoData + " de tipo: " + tipoGrupo
                            + ", condInicio: " + condInicio + ", condUnirse: " + condUnirse + ", tiempoReaparecer: " + segundosRespawn
                            + if (sql) " y guardado en la BD" else "")
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "REMOVE_MOBS", "DELETE_MOBS", "DEL_MOBS", "ELIMINAR_MOBS", "BORRAR_MOBS" -> {
                mapa.borrarTodosMobsNoFijos()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se borraron todos los mobs normales de este mapa")
            }
            "INVASION_MOBS" -> {
                Mundo.agregarMobIvancion()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se ha agregado una invasion")
            }
            "INVASION_MAPA" -> GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El mapa de invasion es: " + Mundo.INVASION_MAPA)
            "ELIMINAR_MOBS_FIJOS", "BORRAR_MOBS_FIX", "BORRAR_MOBS_FIJOS", "REMOVE_MOBS_FIX", "DEL_MOBS_FIX", "DELETE_MOBS_FIX" -> {
                mapa.borrarTodosMobsFijos()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se borraron todos los mobs fix de este mapa")
            }
            "AGREGAR_NPC", "ADD_NPC" -> try {
                var id = 0
                try {
                    id = Integer.parseInt(infos!![1])
                } catch (e5: Exception) {
                }
                if (Mundo.getNPCModelo(id) == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "L'ID du PNJ est invalide.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "NPC ID inválido")
                    }
                    return
                }
                val npc: NPC = mapa.addNPC(Mundo.getNPCModelo(id), _perso.getCelda().getID(), _perso.getOrientacion(), "")
                GestorSalida.ENVIAR_GM_NPC_A_MAPA(mapa, '+', npc.strinGM(null))
                if (GestorSQL.REPLACE_NPC_AL_MAPA(mapa.getID(), _perso.getCelda().getID(), id, _perso.getOrientacion(), npc
                                .getModelo().getNombre(), "")) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le PNJ a été ajouté.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El NPC " + npc.getModelo().getNombre().toString() + " ha sido agregado")
                    }
                } else {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Une erreur est survenue.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Error al agregar el NPC")
                    }
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "MOVER_NPC", "MOVE_NPC" -> try {
                var id = 0
                id = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argument incorrect.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    }
                    return
                }
                val npc: NPC = mapa.getNPC(id)
                if (npc == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "L'ID du PNJ est invalide.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "NPC ID inválido")
                    }
                    return
                }
                npc.setOrientacion(_perso.getOrientacion())
                if (GestorSQL.REPLACE_NPC_AL_MAPA(mapa.getID(), _perso.getCelda().getID(), npc.getModelo().getID(), _perso
                                .getOrientacion(), npc.getModelo().getNombre(), npc.getCondiciones())) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El NPC " + npc.getModelo().getNombre().toString() + " ha sido desplazado")
                    npc.setCeldaID(_perso.getCelda().getID())
                    GestorSalida.ENVIAR_GM_NPC_A_MAPA(mapa, '~', npc.strinGM(null))
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Error al mover el NPC")
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "BORRAR_NPC", "DEL_NPC" -> try {
                var id = 0
                try {
                    id = Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                }
                val npc: NPC = mapa.getNPC(id)
                if (npc == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "L'ID du PNJ est invalide.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "NPC ID inválido")
                    }
                    return
                }
                GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(mapa, id)
                mapa.borrarNPC(id)
                if (GestorSQL.DELETE_NPC_DEL_MAPA(mapa.getID(), npc.getModeloID())) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El NPC fue eliminado correctamente")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "No se pudo eliminar el NPC de la BD")
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }
            "BORRAR_TRIGER", "BORRAR_CELDA_TELEPORT", "DEL_TRIGGER", "BORRAR_TRIGGER", "BORRAR_CELDA_ACCION", "DEL_CELDA_ACCION" -> try {
                var celdaID: Short? = -1
                try {
                    celdaID = Short.parseShort(infos!![1])
                } catch (e: Exception) {
                }
                var celda: Celda = mapa.getCelda(celdaID)
                if (celda == null) {
                    celda = _perso.getCelda()
                }
                GestorSalida.enviar(_perso, "GDZ|-$celdaID;0;11", false)
                celda.eliminarAcciones()
                val exito: Boolean = GestorSQL.DELETE_TRIGGER(mapa.getID(), celdaID)
                if (exito) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El trigger de la celda $celdaID ha sido borrado")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El trigger no se puede borrar")
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }
            "ADD_TRIGGER", "AGREGAR_TRIGER", "ADD_CELDA_ACCION", "AGREGAR_CELDA_ACCION", "AGREGAR_CELDA_TELEPORT", "AGREGAR_TRIGGER" -> try {
                var accionID = 0
                var args: String? = ""
                var condicion: String? = ""
                try {
                    accionID = Integer.parseInt(infos!![1])
                    args = infos[2]
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                if (accionID <= -3) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "AccionID incorrecta")
                    return
                }
                var celda: Celda = _perso.getCelda()
                try {
                    if (infos.size > 3) {
                        mapa = Mundo.getMapa(Short.parseShort(infos[3].split(",").get(0)))
                        celda = mapa.getCelda(Short.parseShort(infos[3].split(",").get(1)))
                    }
                } catch (e: Exception) {
                    mapa = _perso.getMapa()
                    celda = _perso.getCelda()
                }
                try {
                    if (infos.size > 4) {
                        condicion = infos[4]
                    }
                } catch (e: Exception) {
                }
                if (GestorSQL.REPLACE_CELDAS_ACCION(mapa.getID(), celda.getID(), accionID, args, condicion)) {
                    if (mapa.getID() === _perso.getMapa().getID()) {
                        GestorSalida.enviar(_perso, "GDZ|+" + celda.getID().toString() + ";0;11", false) // color
                        // azul
                    }
                    celda.addAccion(accionID, args, condicion)
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El mapa: " + mapa.getID().toString() + ", celda: " + celda.getID()
                            .toString() + ", le ha sido agregado la acción: " + accionID.toString() + ", args: " + args.toString() + ", y condición (4to arg): "
                            + condicion)
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El trigger no se puede agregar")
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }
            "LIST_TRIGGERS", "LISTA_CELDAS_ACCION", "LISTA_TRIGGERS" -> try {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Triggers del mapa " + mapa.getID())
                for (celda in mapa.getCeldas().values()) {
                    if (celda.accionesIsEmpty()) {
                        continue
                    }
                    for (a in celda.getAcciones().values()) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "\tCeldaID: " + celda.getID().toString() + " AccionID: " + a.getID()
                                .toString() + " Args: " + a.getArgs().toString() + " Condicion: " + a.getCondicion())
                    }
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }
            "MOSTRAR_TRIGGERS", "SHOW_TRIGGERS", "SHOW_CELLS_ACTION" -> mapa.panelTriggers(_perso, true)
            "OCULTAR_TRIGGERS", "HIDE_TRIGGERS", "ESCONDER_TRIGGERS" -> mapa.panelTriggers(_perso, false)
            "ADD_ACCION_OBJETO", "ADD_ACTION_ITEM", "AGREGAR_ACCION_OBJETO", "ADD_ITEM_ACTION", "ADD_OBJETO_ACCION", "AGREGAR_OBJETO_ACCION" -> try {
                var id = 0
                var accionID = 0
                var args: String? = ""
                try {
                    id = Integer.parseInt(infos!![1])
                    accionID = Integer.parseInt(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Error con los argumentos")
                    return
                }
                try {
                    args = infos[3]
                } catch (e: Exception) {
                }
                val objModelo: ObjetoModelo = Mundo.getObjetoModelo(id)
                if (objModelo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Algun valor inválido")
                    return
                }
                objModelo.addAccion(Accion(accionID, args, ""))
                GestorSQL.REPLACE_ACCION_OBJETO(id, accionID, args, objModelo.getNombre())
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El objeto " + objModelo.getNombre()
                        .toString() + " se le ha agregado la accionID " + accionID.toString() + " con args " + args)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }
            "DEL_ACTION_ITEM", "BORRAR_ACCION_OBJETO", "BORRAR_ACCIONES_OBJETO", "BORRAR_OBJETO_ACCIONES", "BORRAR_OBJETO_ACCION", "DELETE_ITEM_ACTIONS", "DELETE_ACTION_ITEM" -> try {
                var id = 0
                id = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Error con los argumentos")
                    return
                }
                val objModelo: ObjetoModelo = Mundo.getObjetoModelo(id)
                if (objModelo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Objeto modelo inválido")
                    return
                }
                objModelo.borrarAcciones()
                GestorSQL.DELETE_ACCION_OBJETO(id)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El objeto " + objModelo.getNombre().toString() + " borro todas sus acciones")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }
            "MESSAGE_WELCOME", "MENSAJE_BIENVENIDA" -> try {
                var str = ""
                try {
                    str = mensaje.split(" ", 2).get(1)
                } catch (e: Exception) {
                }
                MainServidor.MENSAJE_BIENVENIDA = str
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El mensaje de bienvenida es :\n$str")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "PANEL_BIENVENIDA" -> try {
                var str = ""
                try {
                    str = mensaje.split(" ", 2).get(1)
                } catch (e: Exception) {
                }
                MainServidor.PANEL_BIENVENIDA = str
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El panel bievenida dice :\n$str")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "PANEL_DESPUES_CREAR_PJ", "PANEL_CREAR_PJ" -> try {
                var str = ""
                try {
                    str = mensaje.split(" ", 2).get(1)
                } catch (e: Exception) {
                }
                MainServidor.PANEL_DESPUES_CREAR_PERSONAJE = str
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El panel crear pj dice :\n$str")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "MENSAJE_COMANDOS" -> try {
                var str = ""
                try {
                    str = mensaje.split(" ", 2).get(1)
                } catch (e: Exception) {
                }
                MainServidor.MENSAJE_COMANDOS = str
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El nuevo mensaje de comandos es :\n$str")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "TUTORIAL_ES" -> try {
                var str = ""
                try {
                    str = mensaje.split(" ", 2).get(1)
                } catch (e: Exception) {
                }
                MainServidor.TUTORIAL_ES = str
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El mensaje de tutorial_es es :\n$str")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "TUTORIAL_FR" -> try {
                var str = ""
                try {
                    str = mensaje.split(" ", 2).get(1)
                } catch (e: Exception) {
                }
                MainServidor.TUTORIAL_FR = str
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El mensaje de tutorial_fr es :\n$str")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "ADD_CELDA_CERCADO", "ADD_CELL_MOUNTPARK", "CELDA_OBJETO" -> try {
                var celdaID: Short = 0
                if (mapa.getCercado() == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Este mapa no tiene cercado")
                    return
                }
                try {
                    celdaID = Short.parseShort(infos!![1])
                } catch (e: Exception) {
                    celdaID = _perso.getCelda().getID()
                }
                mapa.getCercado().addCeldaObj(celdaID)
                GestorSalida.enviar(_perso, "GDZ|+$celdaID;0;5", false)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }
            "CELDAS_CERCADO" -> try {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Tiene las celdas: " + mapa.getCercado().getStringCeldasObj())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Este mapa no tiene cercado")
            }
            "MUTE_CANAL_ALINEACION" -> try {
                var a = false
                try {
                    a = infos!![1].equalsIgnoreCase("true")
                } catch (e: Exception) {
                }
                MainServidor.MUTE_CANAL_ALINEACION = a
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Canal alineacion: " + !MainServidor.MUTE_CANAL_COMERCIO)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "MUTE_CANAL_INCARNAM", "MUTE_CANAL_ALL" -> try {
                var a = false
                try {
                    a = infos!![1].equalsIgnoreCase("true")
                } catch (e: Exception) {
                }
                MainServidor.MUTE_CANAL_INCARNAM = a
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Canal incarnam: " + !MainServidor.MUTE_CANAL_COMERCIO)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "MUTE_CANAL_COMERCIO", "MUTE_CANAL_COMMERCE" -> try {
                var a = false
                try {
                    a = infos!![1].equalsIgnoreCase("true")
                } catch (e: Exception) {
                }
                MainServidor.MUTE_CANAL_COMERCIO = a
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Canal commerce: " + !MainServidor.MUTE_CANAL_COMERCIO)
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Canal comercio: " + !MainServidor.MUTE_CANAL_COMERCIO)
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "MUTE_CANAL_RECRUTEMENT", "MUTE_CANAL_RECLUTAMIENTO" -> try {
                var b = false
                try {
                    b = infos!![1].equalsIgnoreCase("true")
                } catch (e: Exception) {
                }
                MainServidor.MUTE_CANAL_RECLUTAMIENTO = b
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Canal recrutement: " + !MainServidor.MUTE_CANAL_RECLUTAMIENTO)
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Canal reclutamiento: " + !MainServidor.MUTE_CANAL_RECLUTAMIENTO)
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            else -> {
                GM_lvl_2(comando, infos, mensaje, _cuenta, _perso)
                return
            }
        }
        // if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
        // GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Commande GM 3!.");
        // } else {
        // GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Comando de nivel 3");
        // }
    }

    fun GM_lvl_4(comando: String?, infos: Array<String?>?, mensaje: String?, _cuenta: Cuenta?,
                 _perso: Personaje?) {
        var infos = infos
        var npcID = 0
        var id = -1
        var cantInt = 0
        var tipo = 0
        var cantByte: Byte = 0
        var cantShort: Short = 0
        var cantLong: Long = 0
        var cantFloat = 0f
        var objModelo: ObjetoModelo
        val mobModelo: MobModelo
        val npcModelo: NPCModelo
        var str: String? = ""
        var intercambiable = ""
        val strB = StringBuilder()
        var objetivo: Personaje? = null
        val hechizo: Hechizo // *0287014
        when (comando.toUpperCase()) {
            "SET_SPELLS_MOB", "SET_HECHIZOS_MOB" -> {
                try {
                    id = Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                }
                mobModelo = Mundo.getMobModelo(id)
                if (mobModelo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Mob ID $id no existe")
                    return
                }
                try {
                    cantByte = Byte.parseByte(infos!![2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ingresa un valor correcto para GRADO del mob")
                    return
                }
                val mobGradoModelo: MobGradoModelo = mobModelo.getGradoPorGrado(cantByte)
                if (mobGradoModelo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Mob ID $id con Grado $cantByte no existe")
                    return
                }
                try {
                    str = infos!![3]
                } catch (e: Exception) {
                }
                mobGradoModelo.setHechizos(str)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El mob " + mobModelo.getNombre().toString() + " (" + id.toString() + ") con Grado "
                        + cantByte.toString() + " a modificado sus hechizos a " + str)
            }
            "ADD_ALMANAX", "AGREGAR_MISION_DIARIA", "MISION_ALMANAX", "UPDATE_ALMANAX" -> {
                try {
                    id = Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                }
                if (id == -1) {
                    id = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
                }
                if (id < 1 || id > 366) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Dia Incorrecto")
                    return
                }
                try {
                    str = infos!![2]
                    tipo = Integer.parseInt(infos[3])
                    cantInt = Integer.parseInt(infos[4])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento incorrecto")
                    return
                }
                GestorSQL.UPDATE_ALMANAX(id, str, tipo, cantInt)
                Mundo.addAlmanax(Almanax(id, tipo, cantInt, str))
                var bonus = "EXP PJ"
                when (tipo) {
                    1 -> bonus = "EXP PJ"
                    2 -> bonus = "KAMAS"
                    3 -> bonus = "DROP"
                    4 -> bonus = "EXP CRAFT"
                    5 -> bonus = "EXP RECOLECCION"
                    6 -> bonus = "DROP RECOLECCION"
                    7 -> bonus = "BONUS HONOR"
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se actualizó el día almanax " + id + ", con ofrenda " + str
                        + ", tipoBonus " + bonus + " y bonus %" + cantInt)
            }
            "ADD_MOB_CARD", "ADD_CARD_MOB" -> {
                try {
                    id = Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                }
                objetivo = _perso
                if (infos!!.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                if (!objetivo.enLinea()) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'est pas connecté.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no esta conectado")
                    }
                    return
                }
                if (Mundo.getMobModelo(id) == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le monstre n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El mob no existe")
                    }
                    return
                }
                objetivo.addCardMob(id)
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.getNombre()
                            .toString() + " agrego asu lista de cardMobs la tarjeta N°" + id)
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje " + objetivo.getNombre()
                            .toString() + " agrego asu lista de cardMobs la tarjeta N°" + id.toString() + " (" + Mundo.getMobModelo(id).getNombre().toString() + ")")
                }
            }
            "PRECIO_SISTEMA_RECURSO", "PRECIO_RECURSO" -> {
                try {
                    cantFloat = Float.parseFloat(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento incorrecto")
                    return
                }
                MainServidor.PRECIO_SISTEMA_RECURSO = cantFloat
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El precio de recurso se cambio a $cantFloat")
            }
            "CARGAR_MAPAS_IDS", "LOAD_MAPS_IDS", "CARGAR_MAPAS", "MAPPEAR_IDS" -> try {
                GestorSQL.CARGAR_MAPAS_IDS(infos!![1])
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se integro los mapas " + infos[1])
            } catch (e1: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                return
            }
            "CARGAR_SUBAREAS", "LOAD_SUBAREAS", "MAPPEAR_SUBAREAS" -> try {
                GestorSQL.CARGAR_MAPAS_SUBAREAS(infos!![1])
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se integro las subareas " + infos[1])
            } catch (e1: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                return
            }
            "ADD_ENERGIA", "AGREGAR_ENERGIA", "ENERGIA", "ENERGY", "SET_ENERGY" -> {
                if (infos!!.size <= 1) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argument incorrect.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    }
                    return
                }
                cantInt = Integer.parseInt(infos[1])
                objetivo = _perso
                if (infos.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                    if (objetivo == null) {
                        if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                        }
                        return
                    }
                }
                objetivo.addEnergiaConIm(cantInt, true)
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "L'énergie de " + objetivo.getNombre().toString() + " a été modifiée en "
                            + objetivo.getEnergia())
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ha sido modificado la energía de " + objetivo.getNombre().toString() + " a "
                            + objetivo.getEnergia())
                }
            }
            "ADD_TITULO", "SET_TITLE", "ADD_TITLE", "TITULO", "TITRE", "TITLE" -> {
                var titulo = 0
                var color = -1
                try {
                    titulo = Byte.parseByte(infos!![1])
                    color = Integer.parseInt(infos!![2])
                } catch (e: Exception) {
                }
                objetivo = _perso
                if (infos!!.size > 3) {
                    objetivo = Mundo.getPersonajePorNombre(infos[3])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                objetivo.addTitulo(titulo, color)
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.getNombre().toString() + " possède désormais le titre "
                            + titulo)
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje " + objetivo.getNombre().toString() + " adquirió el título "
                            + titulo)
                }
                if (objetivo.getPelea() == null) {
                    objetivo.refrescarEnMapa()
                }
            }
            "ORNAMENTO", "ORNEMENT" -> {
                try {
                    cantByte = Byte.parseByte(infos!![1])
                } catch (e: Exception) {
                }
                objetivo = _perso
                if (infos!!.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                objetivo.addOrnamento(cantByte)
                objetivo.setOrnamento(cantByte)
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.getNombre()
                            .toString() + " possède désormais l'ornement " + cantByte)
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje " + objetivo.getNombre().toString() + " adquirió el ornamento "
                            + cantByte)
                }
                if (objetivo.getPelea() == null) {
                    objetivo.refrescarEnMapa()
                }
            }
            "TITULO_VIP", "TITRE_VIP" -> try {
                infos = mensaje.split(" ", 2)
                objetivo = _perso
                if (infos!!.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                str = infos[2]
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                objetivo.setTituloVIP(str)
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.getNombre()
                            .toString() + " possède désormais le titre VIP " + str)
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje " + objetivo.getNombre()
                            .toString() + " a adquirido el titulo VIP de " + str)
                }
                if (objetivo.getPelea() == null) {
                    objetivo.refrescarEnMapa()
                }
            } catch (e: Exception) {
            }
            "SET_STATS_OBJETO_SET", "SET_STATS_SET_OBJETO", "SET_STATS_SET_ITEM", "SET_STATS_ITEM_SET", "SET_BONUS_OBJETO_SET", "SET_BONUS_SET_OBJETO", "SET_BONUS_SET_ITEM", "SET_BONUS_ITEM_SET" -> {
                try {
                    id = Integer.parseInt(infos!![1])
                    str = infos[2]
                    cantInt = Integer.parseInt(infos[3])
                } catch (e: Exception) {
                }
                val set: ObjetoSet = Mundo.getObjetoSet(id)
                if (set == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El objeto set $id no existe")
                    return
                }
                set.setStats(str, cantInt)
                GestorSQL.UPDATE_STATS_OBJETO_SET(id, str)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El objeto set " + id + " (" + set.getNombre()
                        + ") cambio su bonus de " + cantInt + " objetos a: " + str)
            }
            "SET_OBJETO", "SET_ITEM", "OBJETO_SET", "ITEM_SET" -> try {
                try {
                    id = Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                }
                val OS: ObjetoSet = Mundo.getObjetoSet(id)
                if (OS == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El objeto set $id no existe")
                    return
                }
                objetivo = _perso
                if (infos!!.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                }
                if (MainServidor.PARAM_NOMBRE_COMPRADOR) {
                    if (_perso.getAdmin() < 5) {
                        objetivo = _perso
                    }
                    intercambiable = ObjetoModelo.stringFechaIntercambiable(3650)
                }
                var useMax: CAPACIDAD_STATS = CAPACIDAD_STATS.RANDOM
                if (infos.size > 3) {
                    useMax = if (infos[3].equalsIgnoreCase("MAX")) CAPACIDAD_STATS.MAXIMO else if (infos[3].equalsIgnoreCase("MIN")) CAPACIDAD_STATS.MINIMO else CAPACIDAD_STATS.RANDOM
                }
                for (OM in OS.getObjetosModelos()) {
                    val obj: Objeto = OM.crearObjeto(1, Constantes.OBJETO_POS_NO_EQUIPADO, useMax)
                    if (MainServidor.PARAM_NOMBRE_COMPRADOR) {
                        obj.addStatTexto(Constantes.STAT_INTERCAMBIABLE_DESDE, intercambiable)
                        obj.addStatTexto(Constantes.STAT_PERTENECE_Y_NO_VENDER, "0#0#0#" + _perso.getNombre())
                    }
                    objetivo.addObjIdentAInventario(obj, false)
                }
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    strB.append("Création de la panoplie " + OS.getNombre().toString() + " pour " + objetivo.getNombre())
                } else {
                    strB.append("Creación del objeto set " + OS.getNombre().toString() + " a " + objetivo.getNombre())
                }
                when (useMax) {
                    MAXIMO -> if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        strB.append(" avec des jets parfaits.")
                    } else {
                        strB.append(" con stats máximos")
                    }
                    MINIMO -> if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        strB.append(" avec des jets minimuns.")
                    } else {
                        strB.append(" con stats mínimos")
                    }
                    else -> {
                    }
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, strB.toString())
                GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(objetivo)
            } catch (e: Exception) {
            }
            "DEL_NPC_ITEM", "DEL_ITEM_NPC", "BORRAR_NPC_ITEM", "BORRAR_ITEM_NPC", "BORRAR_OBJETO_NPC", "BORRAR_NPC_OBJETO" -> try {
                try {
                    npcID = Integer.parseInt(infos!![1])
                    npcModelo = Mundo.getNPCModelo(npcID)
                    npcModelo.getID()
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "NPC inválido")
                    return
                }
                val objetos: ArrayList<ObjetoModelo?> = ArrayList<ObjetoModelo?>()
                for (a in infos[2].split(",")) {
                    try {
                        objModelo = Mundo.getObjetoModelo(Integer.parseInt(a))
                        if (objModelo != null) {
                            objetos.add(objModelo)
                            strB.append("""
    
    ${objModelo.getNombre()}
    """.trimIndent())
                        }
                    } catch (e: Exception) {
                    }
                }
                npcModelo.borrarObjetoAVender(objetos)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Al NPC " + npcModelo.getNombre()
                        .toString() + " se le borró los siguientes objetos:" + strB.toString())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos inválidos")
                return
            }
            "ADD_ITEM_NPC", "ADD_OBJETO_NPC", "ADD_NPC_ITEM", "ADD_NPC_OBJETO", "AGREGAR_NPC_OBJETO", "AGREGAR_OBJETO_NPC", "AGREGAR_NPC_ITEM", "AGREGAR_ITEM_NPC" -> try {
                try {
                    npcID = Integer.parseInt(infos!![1])
                    npcModelo = Mundo.getNPCModelo(npcID)
                    npcModelo.getID()
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "NPC inválido")
                    return
                }
                val objetos: ArrayList<ObjetoModelo?> = ArrayList<ObjetoModelo?>()
                for (a in infos[2].split(",")) {
                    try {
                        objModelo = Mundo.getObjetoModelo(Integer.parseInt(a))
                        if (objModelo != null) {
                            objetos.add(objModelo)
                            strB.append("""
    
    ${objModelo.getNombre()}
    """.trimIndent())
                        }
                    } catch (e: Exception) {
                    }
                }
                npcModelo.addObjetoAVender(objetos)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Al NPC " + npcModelo.getNombre()
                        .toString() + " se le agregó los siguientes objetos:" + strB.toString())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos inválidos")
                return
            }
            "BORRAR_OBJETOS_NPC", "BORRAR_NPC_TODOS_OBJETOS" -> try {
                npcID = Integer.parseInt(infos!![1])
                npcModelo = Mundo.getNPCModelo(npcID)
                npcModelo.borrarTodosObjVender()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se borraron todos los objetos del NPC " + npcModelo.getNombre())
            } catch (ex: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos inválido")
                return
            }
            "HONOUR", "HONOR" -> {
                var honor = 0
                try {
                    honor = Integer.parseInt(infos!![1])
                } catch (e6: Exception) {
                }
                objetivo = _perso
                if (infos!!.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                    if (objetivo == null) {
                        if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                        }
                        return
                    }
                }
                if (objetivo.getAlineacion() === Constantes.ALINEACION_NEUTRAL || objetivo
                                .getAlineacion() === Constantes.ALINEACION_NULL) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur est neutre.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje es neutral")
                    }
                    return
                }
                objetivo.addHonor(honor)
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "$honor points d'honneur ont été ajoutés à " + objetivo
                            .getNombre())
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ha sido agregado " + honor + " honor a " + objetivo.getNombre())
                }
            }
            "MAPA_PARAMETROS", "PARAMETERS_MAPA", "MAPA_DESCRIPCION", "DESCRIPCION_MAPA" -> {
                try {
                    cantShort = Byte.parseByte(infos!![1])
                } catch (e: Exception) {
                }
                _perso.getMapa().setParametros(cantShort)
                GestorSQL.UPDATE_MAPA_PARAMETROS(_perso.getMapa().getID(), cantShort)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Los parametros del mapa cambió a $cantShort")
            }
            "REGALO", "GIFT" -> {
                try {
                    str = infos!![1]
                } catch (e: Exception) {
                }
                objetivo = _perso
                if (infos!!.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                    if (objetivo == null) {
                        if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas.")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                        }
                        return
                    }
                }
                objetivo.getCuenta().addRegalo(str)
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le cadeau été envoyé.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se entregó el regalo " + str + " a " + objetivo.getNombre())
                }
            }
            "REGALO_PARA_ONLINE", "REGALO_ONLINE", "GIFT_ONLINE" -> {
                try {
                    str = infos!![1]
                } catch (e: Exception) {
                }
                for (pj in Mundo.getPersonajesEnLinea()) {
                    try {
                        pj.getCuenta().addRegalo(str)
                    } catch (e: Exception) {
                    }
                }
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le cadeau a été envoyé à tous les joueurs en ligne.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se entregó el regalo $str a todos los jugadores en línea")
                }
            }
            "REGALO_PARA_TODOS", "GIFT_FOR_ALL" -> {
                try {
                    str = infos!![1]
                } catch (e: Exception) {
                }
                for (cuenta in Mundo.getCuentas().values()) {
                    cuenta.addRegalo(str)
                }
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le cadeau a été envoyé à tous les joueurs database.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se entregó el regalo " + str
                            + " a todos los jugadores de la database")
                }
            }
            "CARGAR_MOB_ID" -> {
                id = 0
                try {
                    id = Integer.parseInt(infos!![1])
                } catch (e13: Exception) {
                }
                if (id > 0) {
                    GestorSQL.CARGAR_MOBS_MODELOS_ID(id)
                    if (Mundo.getMobModelo(id) != null) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Mob actualizado exitosamente")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Mob cargado exitosamente")
                    }
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ingresa una ID valida")
                }
            }
            "OBJETO_PARA_PLAYERS_ONLINE", "OBJETO_PARA_JUGADORES_ONLINE", "ITEM_PLAYERS_ONLINE", "ITEM_FOR_ONLINE", "OBJETO_PARA_ONLINE", "GIVE_ITEM_TO_ONLINE" -> {
                try {
                    id = Integer.parseInt(infos!![1])
                    cantInt = Integer.parseInt(infos[2])
                } catch (e13: Exception) {
                }
                objModelo = Mundo.getObjetoModelo(id)
                if (objModelo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "L'objet n'existe pas.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Objeto modelo nulo")
                    }
                    return
                }
                if (cantInt < 1) {
                    cantInt = 1
                }
                if (MainServidor.PARAM_NOMBRE_COMPRADOR) {
                    intercambiable = ObjetoModelo.stringFechaIntercambiable(3650)
                }
                for (pj in Mundo.getPersonajesEnLinea()) {
                    val obj: Objeto = objModelo.crearObjeto(cantInt, Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM)
                    if (MainServidor.PARAM_NOMBRE_COMPRADOR) {
                        obj.addStatTexto(Constantes.STAT_INTERCAMBIABLE_DESDE, intercambiable)
                        obj.addStatTexto(Constantes.STAT_PERTENECE_Y_NO_VENDER, "0#0#0#" + _perso.getNombre())
                    }
                    pj.addObjIdentAInventario(obj, false)
                }
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "L'objet " + objModelo.getNombre().toString() + " avec quant " + cantInt
                            .toString() + " a été envoyé à tous les joueurs en ligne")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se entregó el objeto " + objModelo.getNombre().toString() + " con cantidad "
                            + cantInt.toString() + " a todos los jugadores en línea")
                }
            }
            "ADD_EXP_OFICIO", "ADD_XP_OFICIO", "EXP_OFICIO", "ADD_JOB_XP" -> {
                if (infos!!.size <= 2) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argument incorrect.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    }
                    return
                }
                var oficio = -1
                var exp = -1
                try {
                    oficio = Integer.parseInt(infos[1])
                    exp = Integer.parseInt(infos[2])
                } catch (e: Exception) {
                }
                if (oficio < 0 || exp < 0) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argument incorrect.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    }
                    return
                }
                objetivo = _perso
                if (infos.size > 3) {
                    objetivo = Mundo.getPersonajePorNombre(infos[3])
                    if (objetivo == null) {
                        if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                        }
                        return
                    }
                }
                val statsOficio: StatOficio = objetivo.getStatOficioPorID(oficio)
                if (statsOficio == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'exerce pas ce métier.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no conoce el oficio")
                    }
                    return
                }
                statsOficio.addExperiencia(objetivo, exp, 0)
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le métier du joueur a gagné de l'expérience.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El oficio ha subido de experiencia")
                }
            }
            "PUNTOS_HECHIZO", "SPELL_POINTS" -> {
                var pts = -1
                try {
                    if (infos!!.size > 1) {
                        pts = Integer.parseInt(infos[1])
                    }
                } catch (e: Exception) {
                }
                if (pts < 0) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argument incorrect.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    }
                    return
                }
                objetivo = _perso
                if (infos!!.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                objetivo.addPuntosHechizos(pts)
                GestorSalida.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(objetivo)
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.getNombre().toString() + " a reçu " + pts
                            .toString() + " points de sort")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje " + objetivo.getNombre().toString() + " se le ha aumentado " + pts
                            .toString() + " puntos de hechizo")
                }
            }
            "FORGET_SPELL", "OLVIDAR_HECHIZO" -> {
                try {
                    if (infos!!.size > 1) id = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                }
                if (id < 0) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argument incorrect.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    }
                    return
                }
                objetivo = _perso
                if (infos!!.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                hechizo = Mundo.getHechizo(id)
                if (hechizo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le sort n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El hechizo no existe")
                    }
                    return
                }
                objetivo.olvidarHechizo(id, true, true)
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.getNombre().toString() + " a oublier le sort " + hechizo
                            .getNombre())
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje " + objetivo.getNombre().toString() + " ha olvidado el hechizo "
                            + hechizo.getNombre())
                }
            }
            "APRENDER_HECHIZO", "LEARN_SPELL" -> {
                try {
                    if (infos!!.size > 1) id = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                }
                if (id < 0) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argument incorrect.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    }
                    return
                }
                objetivo = _perso
                if (infos!!.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                hechizo = Mundo.getHechizo(id)
                if (hechizo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le sort n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El hechizo no existe")
                    }
                    return
                }
                objetivo.fijarNivelHechizoOAprender(id, 1, true)
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.getNombre().toString() + " a appris le sort " + id
                            .toString() + " (" + hechizo.getNombre().toString() + ")")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje " + objetivo.getNombre().toString() + " aprendio el hechizo " + id
                            .toString() + " (" + hechizo.getNombre().toString() + ")")
                }
            }
            "ADD_XP_MONTURA", "ADD_EXP_MONTURA", "AGREGAR_EXP_MONTURA" -> try {
                var montura: Montura = _perso.getMontura()
                try {
                    if (infos!!.size > 1) {
                        id = Integer.parseInt(infos[1])
                        if (id > 0) {
                            id = -id
                        }
                        if (id != 0) montura = Mundo.getMontura(id)
                    }
                } catch (e: Exception) {
                }
                if (montura == null) {
                    return
                }
                montura.addExperiencia(Integer.parseInt(infos!![2]))
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La montura ID " + montura.getID().toString() + " a recibido " + Integer
                        .parseInt(infos[2]).toString() + " puntos de exp")
            } catch (e: Exception) {
            }
            "FECUNDADA_HACE" -> try {
                var montura: Montura = _perso.getMontura()
                try {
                    if (infos!!.size > 1) {
                        id = Integer.parseInt(infos[1])
                        if (id > 0) {
                            id = -id
                        }
                        if (id != 0) montura = Mundo.getMontura(id)
                    }
                } catch (e: Exception) {
                }
                if (montura == null) {
                    return
                }
                montura.setFecundada(100)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La montura ID " + montura.getID().toString() + " esta lista para parir")
            } catch (e: Exception) {
            }
            "MONTABLE", "MONTAR" -> try {
                var montura: Montura = _perso.getMontura()
                try {
                    if (infos!!.size > 1) {
                        id = Integer.parseInt(infos[1])
                        if (id > 0) {
                            id = -id
                        }
                        if (id != 0) montura = Mundo.getMontura(id)
                    }
                } catch (e: Exception) {
                }
                if (montura == null) {
                    return
                }
                montura.setSalvaje(false)
                montura.setMaxEnergia()
                montura.setMaxMadurez()
                montura.setFatiga(0)
                val restante: Long = Mundo.getExpMontura(5) - montura.getExp()
                if (restante > 0) {
                    montura.addExperiencia(restante)
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La montura ID " + montura.getID().toString() + " ahora es montable")
            } catch (e: Exception) {
            }
            "FECUNDABLE", "FECUNDAR" -> try {
                var montura: Montura = _perso.getMontura()
                try {
                    if (infos!!.size > 1) {
                        id = Integer.parseInt(infos[1])
                        if (id > 0) {
                            id = -id
                        }
                        if (id != 0) montura = Mundo.getMontura(id)
                    }
                } catch (e: Exception) {
                }
                if (montura == null) {
                    return
                }
                // montura.setSalvaje(false);
                montura.setAmor(7500)
                montura.setResistencia(7500)
                montura.setMaxEnergia()
                montura.setMaxMadurez()
                val restante: Long = Mundo.getExpMontura(5) - montura.getExp()
                if (restante > 0) {
                    montura.addExperiencia(restante)
                }
                if (montura.esCastrado()) {
                    montura.quitarCastrado()
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La montura ID " + montura.getID().toString() + " ahora esta fecundo")
            } catch (e: Exception) {
            }
            "STATS_POINTS", "POINTS_CAPITAL", "PUNTOS_STATS", "PUNTOS_CAPITAL", "CAPITAL" -> {
                var puntos = -1
                try {
                    if (infos!!.size > 1) puntos = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                }
                if (puntos < 0) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argument incorrect.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    }
                    return
                }
                objetivo = _perso
                if (infos!!.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                objetivo.addPuntosStats(puntos)
                GestorSalida.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(objetivo)
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.getNombre().toString() + " a reçu " + puntos
                            .toString() + " points de capital")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje " + objetivo.getNombre().toString() + " se le ha aumentado "
                            + puntos.toString() + " puntos de capital")
                }
            }
            "KAMAS" -> {
                try {
                    if (infos!!.size > 1) {
                        cantLong = Long.parseLong(infos[1])
                    }
                } catch (e: Exception) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argument incorrect.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    }
                    return
                }
                objetivo = _perso
                if (infos.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                objetivo.addKamas(cantLong, true, true)
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Tu as " + (if (cantLong < 0) "retiré" else "ajouté") + " " + Math.abs(
                            cantLong) + " kamas a " + objetivo.getNombre())
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ha sido " + (if (cantLong < 0) "retirado" else "agregado") + " " + Math
                            .abs(cantLong) + " kamas a " + objetivo.getNombre())
                }
            }
            "REINICIAR_EN", "RESET_IN", "RESET_EN", "REBOOT_IN", "REBOOT_EN", "REBOOTEN", "RESETEN" -> {
                id = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento incorrecto (minutos)")
                    return
                }
                if (id < 0) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento incorrecto valor positivo")
                    return
                }
                var msj: String? = if (MainServidor.MENSAJE_TIMER_REBOOT.isEmpty()) "REBOOT" else MainServidor.MENSAJE_TIMER_REBOOT
                try {
                    infos = mensaje.split(" ", 3)
                    msj = infos[2]
                } catch (e: Exception) {
                }
                val segundos = id * 60
                Mundo.SEG_CUENTA_REGRESIVA = segundos
                if (id == 0) {
                    MainServidor.SEGUNDOS_REBOOT_SERVER = 0
                    Mundo.MSJ_CUENTA_REGRESIVA = ""
                    GestorSalida.ENVIAR_bRS_PARAR_CUENTA_REGRESIVA_TODOS()
                } else {
                    MainServidor.SEGUNDOS_REBOOT_SERVER = segundos
                    Mundo.MSJ_CUENTA_REGRESIVA = msj
                    GestorSalida.ENVIAR_bRI_INICIAR_CUENTA_REGRESIVA_TODOS()
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se lanzo el temporizador rebooot por $id minutos")
            }
            "RESET_RATES" -> {
                if (!Mundo.MSJ_CUENTA_REGRESIVA.isEmpty()) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Espera que se termine el otro evento.")
                    return
                }
                id = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento incorrecto (minutos)")
                    return
                }
                if (id < 0) {
                    id = 0
                }
                val segundosRates = id * 60
                MainServidor.SEGUNDOS_RESET_RATES = segundosRates + ServidorServer.getSegundosON()
                Mundo.SEG_CUENTA_REGRESIVA = segundosRates
                Mundo.MSJ_CUENTA_REGRESIVA = "RESET RATES"
                GestorSalida.ENVIAR_bRI_INICIAR_CUENTA_REGRESIVA_TODOS()
                GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS("1RESET_RATES;$id")
            }
            "CUENTA_REGRESIVA" -> {
                if (Mundo.MSJ_CUENTA_REGRESIVA.equalsIgnoreCase("RESET RATES")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Espera que se termine el evento de super rates.")
                    return
                }
                infos = mensaje.split(" ", 3)
                try {
                    id = Integer.parseInt(infos!![1])
                    str = infos[2]
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                Mundo.SEG_CUENTA_REGRESIVA = id
                Mundo.MSJ_CUENTA_REGRESIVA = str
                if (str.equalsIgnoreCase("LOTERIA")) {
                    Mundo.VENDER_BOLETOS = true
                } else if (str.equalsIgnoreCase("CACERIA")) {
                    val victima: Personaje = Mundo.getPersonajePorNombre(Mundo.NOMBRE_CACERIA)
                    if (victima != null && !victima.enLinea()) {
                        val geo: String = victima.getMapa().getX().toString() + "|" + victima.getMapa().getY()
                        val rec: String = Mundo.mensajeCaceria()
                        try {
                            for (perso in Mundo.getPersonajesEnLinea()) {
                                GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_PERSONAJE(perso, "", 0, Mundo.NOMBRE_CACERIA, "INICIA CACERIA - "
                                        + rec + " - USA COMANDO .caceria")
                                GestorSalida.ENVIAR_IC_PERSONAJE_BANDERA_COMPAS(perso, geo)
                            }
                        } catch (e: Exception) {
                        }
                    }
                }
                GestorSalida.ENVIAR_ÑL_BOTON_LOTERIA_TODOS(Mundo.VENDER_BOLETOS)
                GestorSalida.ENVIAR_bRI_INICIAR_CUENTA_REGRESIVA_TODOS()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se inicio la cuenta regresiva con mensaje " + str + " y tiempo " + id
                        + " segundos")
            }
            "BOLETO_DE" -> try {
                id = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                objetivo = Mundo.getPersonaje(Mundo.LOTERIA_BOLETOS.get(id))
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El dueño del boleto Nº $id es el jugador " + objetivo
                        .getNombre())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio un error")
                return
            }
            "ADIC_PJ", "MULTIPLICADOR_DAÑO_PJ" -> {
                try {
                    cantFloat = Float.parseFloat(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                EfectoHechizo.MULTIPLICADOR_DAÑO_PJ = cantFloat
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El multiplicador daño personaje ha sido cambiado a $cantFloat")
            }
            "ADIC_MOB", "MULTIPLICADOR_DAÑO_MOB" -> {
                try {
                    cantFloat = Float.parseFloat(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                EfectoHechizo.MULTIPLICADOR_DAÑO_MOB = cantFloat
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El multiplicador de daño mob ha sido cambiado a $cantFloat")
            }
            "ADIC_CAC", "MULTIPLICADOR_DAÑO_CAC" -> {
                try {
                    cantFloat = Float.parseFloat(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                EfectoHechizo.MULTIPLICADOR_DAÑO_CAC = cantFloat
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El multiplicador de daño CaC ha sido cambiado a $cantFloat")
            }
            "SET_IA", "SET_IA_MOB", "SET_MOB_IA", "CAMBIAR_IA_MOB", "SET_TIPO_IA" -> {
                id = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                mobModelo = Mundo.getMobModelo(id)
                if (mobModelo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Mob no existe")
                    return
                }
                var tipoIA: Byte = 0
                try {
                    tipoIA = Byte.parseByte(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                mobModelo.setTipoIA(tipoIA)
                GestorSQL.UPDATE_MOB_IA_TALLA(mobModelo)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El mob " + mobModelo.getNombre().toString() + " a cambiado su IA a: " + tipoIA)
            }
            "GET_IA", "GET_IA_MOB", "GET_MOB_IA", "INFO_IA_MOB", "GET_TIPO_IA" -> {
                id = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                mobModelo = Mundo.getMobModelo(id)
                if (mobModelo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Mob no existe")
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El mob " + mobModelo.getNombre().toString() + " tiene la IA: " + mobModelo
                        .getTipoIA())
            }
            "MOB_COLORES" -> {
                id = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                mobModelo = Mundo.getMobModelo(id)
                if (mobModelo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Mob no existe")
                    return
                }
                str = try {
                    infos[2]
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                mobModelo.setColores(str)
                GestorSQL.UPDATE_MOB_COLORES(mobModelo)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El mob " + mobModelo.getNombre().toString() + " a cambiado su color a: " + str)
            }
            "DISTANCIA_AGRESION_MOB", "DISTANCIA_AGRESION", "MOB_DISTANCIA_AGRESION", "MOB_AGRESION" -> {
                id = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                mobModelo = Mundo.getMobModelo(id)
                if (mobModelo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Mob no existe")
                    return
                }
                var agresion: Byte = 0
                try {
                    agresion = Byte.parseByte(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                mobModelo.setDistAgresion(agresion)
                GestorSQL.UPDATE_MOB_AGRESION(mobModelo)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El mob " + mobModelo.getNombre().toString() + " a cambiado su agresion a: "
                        + agresion)
            }
            "MOB_SIZE", "MOB_TALLA", "SIZE_MOB", "TALLA_MOB" -> {
                id = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                mobModelo = Mundo.getMobModelo(id)
                if (mobModelo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Mob no existe")
                    return
                }
                try {
                    cantShort = Short.parseShort(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                mobModelo.setTalla(cantShort)
                GestorSQL.UPDATE_MOB_IA_TALLA(mobModelo)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El mob " + mobModelo.getNombre().toString() + " a cambiado su talla a: "
                        + cantShort)
            }
            "MODIFICAR_STATS_HECHIZO", "MODIFICAR_HECHIZO", "STAT_HECHIZO", "STATS_HECHIZO", "MODIFICAR_STAT_HECHIZO" -> {
                var stat: String? = ""
                try {
                    infos = mensaje.split(" ", 4)
                    id = Integer.parseInt(infos!![1])
                    cantInt = Integer.parseInt(infos[2])
                    stat = infos[3]
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                hechizo = Mundo.getHechizo(id)
                if (hechizo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Hechizo no existe")
                    return
                }
                var sh: StatHechizo? = null
                try {
                    sh = Hechizo.analizarHechizoStats(id, cantInt, stat)
                    hechizo.addStatsHechizos(cantInt, sh)
                    GestorSQL.UPDATE_STAT_HECHIZO(id, stat, cantInt)
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modifico el hechizo " + hechizo.getNombre().toString() + " (" + hechizo
                            .getID().toString() + ") nivel " + cantInt.toString() + " a " + stat)
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Stat hechizo incorrecto o no valido")
                    return
                }
            }
            "SET_CONDICIONES_HECHIZO", "SET_CONDICION_HECHIZO", "SET_HECHIZO_CONDICION", "SET_HECHIZO_CONDICIONES", "CONDICIONES_HECHIZO", "SET_CONDICIONES_HECHIZOS", "HECHIZO_CONDICIONES", "CONDICIONES_HECHIZOS", "CONDITION_SPELLS", "CONDITION_SPELL", "CONDITIONS_SPELLS", "CONDITIONS_SPELL", "SPELLS_CONDITIONS", "SPELL_CONDITIONS", "SET_SPELLS_CONDITIONS", "SET_SPELL_CONDITIONS" -> {
                id = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                hechizo = Mundo.getHechizo(id)
                if (hechizo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Hechizo no existe")
                    return
                }
                var condiciones: String? = ""
                try {
                    condiciones = infos[2]
                } catch (e: Exception) {
                }
                hechizo.setCondiciones(condiciones)
                GestorSQL.ACTUALIZAR_CONDICIONES_HECHIZO(id, condiciones)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El hechizo " + hechizo.getNombre().toString() + " a cambiado sus condiciones : "
                        + condiciones)
            }
            "SET_AFECTADOS", "HECHIZO_AFECTADOS", "SPELL_TARGETS", "TARGETS_SPELL", "TARGETS", "AFECTADOS" -> {
                id = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                hechizo = Mundo.getHechizo(id)
                if (hechizo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Hechizo no existe")
                    return
                }
                var afectados: String? = ""
                try {
                    afectados = infos[2]
                } catch (e: Exception) {
                }
                hechizo.setAfectados(afectados)
                GestorSQL.UPDATE_HECHIZO_AFECTADOS(id, afectados)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El hechizo " + hechizo.getNombre().toString() + " a cambiado sus afectados : "
                        + afectados)
            }
            "SET_HECHIZO_VALOR_IA", "SPELL_VALUE_IA", "HECHIZO_VALOR_IA", "VALOR_IA_HECHIZO", "SET_IA_HECHIZO", "SET_VALOR_IA_HECHIZO" -> {
                id = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                hechizo = Mundo.getHechizo(id)
                if (hechizo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Hechizo no existe")
                    return
                }
                var valorIA = 0
                valorIA = try {
                    Integer.parseInt(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                hechizo.setValorIA(valorIA)
                GestorSQL.UPDATE_HECHIZOS_VALOR_IA(id, valorIA)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El hechizo " + hechizo.getNombre().toString() + " a cambiado su valorIA : "
                        + valorIA)
            }
            "SPRITE_ID_HECHIZO", "HECHIZO_SPRITE_ID", "SPELL_SPRITE_ID", "SPRITE_ID_SPELL", "SPRITE_ID" -> {
                id = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                hechizo = Mundo.getHechizo(id)
                if (hechizo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Hechizo no existe")
                    return
                }
                var spriteID = 0
                spriteID = try {
                    Integer.parseInt(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                hechizo.setSpriteID(spriteID)
                GestorSQL.ACTUALIZAR_SPRITE_ID_HECHIZO(id, spriteID)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El hechizo " + hechizo.getNombre().toString() + " a cambiado su sprite ID : "
                        + spriteID)
            }
            "SPELL_SPRITE_INFOS", "HECHIZO_SPRITE_INFOS", "SPRITE_INFOS_HECHIZO", "SPRITE_INFOS_SPELL", "SPRITE_INFOS" -> {
                id = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                hechizo = Mundo.getHechizo(id)
                if (hechizo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Hechizo no existe")
                    return
                }
                var spriteInfos: String? = ""
                spriteInfos = try {
                    infos[2]
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                hechizo.setSpriteInfos(spriteInfos)
                GestorSQL.ACTUALIZAR_SPRITE_INFO_HECHIZO(id, spriteInfos)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El hechizo " + hechizo.getNombre().toString() + " a cambiado su spriteInfos : "
                        + spriteInfos)
            }
            "GET_SPRITE_INFOS", "DAR_SPRITE_INFOS" -> {
                id = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                hechizo = Mundo.getHechizo(id)
                if (hechizo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Hechizo no existe")
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El hechizo " + hechizo.getNombre().toString() + " tiene como spriteInfos : "
                        + hechizo.getSpriteInfos())
            }
            "GET_SPRITE_ID", "DAR_SPRITE_ID" -> {
                id = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                hechizo = Mundo.getHechizo(id)
                if (hechizo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Hechizo no existe")
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El hechizo " + hechizo.getNombre().toString() + " tiene como sprite ID : "
                        + hechizo.getSpriteID())
            }
            "ADD_XP", "ADD_EXP", "DAR_EXP", "DAR_XP", "GANAR_XP", "GANAR_EXP" -> try {
                cantLong = Long.parseLong(infos!![1])
                if (cantLong < 1) {
                    cantLong = 1
                }
                objetivo = _perso
                if (infos!!.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                objetivo.addExperiencia(cantLong, true)
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.getNombre().toString() + " a gagne " + cantLong
                            .toString() + " points experience")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "EL jugador " + objetivo.getNombre().toString() + " a ganado " + cantLong
                            .toString() + " puntos de experiencia")
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Arguments incorrects")
            }
            "OMEGA" -> try {
                cantInt = Integer.parseInt(infos!![1])
                if (cantInt < 1) {
                    cantInt = 1
                }
                if (cantInt > MainServidor.NIVEL_MAX_OMEGA) {
                    cantInt = MainServidor.NIVEL_MAX_OMEGA
                }
                objetivo = _perso
                if (infos.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                if (objetivo.getEncarnacionN() != null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso,
                                "Le joueur est en mode incarnation, impossible de lui augmenter son niveau.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso,
                                "No se le puede subir el nivel, porque el personaje es una encarnación.")
                    }
                    return
                }
                objetivo.subirHastaNivelOmega(cantInt)
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le niveau du joueur a été modifié.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ha sido modificado el nivel de " + objetivo.getNombre().toString() + " a "
                            + cantInt)
                }
                objetivo.experiencia = Mundo.getExpPersonaje(objetivo.getNivel(), objetivo.getNivelOmega())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Arguments incorrects")
            }
            "BLOQUEAR_EXP" -> try {
                objetivo = if (infos!!.size > 1) {
                    Mundo.getPersonajePorNombre(infos[1])
                } else {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Veuillez indiquer le nom du joueur à rejoindre.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Hace falta colocar un nombre de jugador")
                    }
                    return
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                if (!objetivo.enLinea()) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'est pas connecté.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no esta conectado")
                    }
                    return
                }
                objetivo.bloquearXP = !objetivo.bloquearXP
                if (objetivo.bloquearXP) {
                    GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(objetivo,
                            "Su xp ha sido bloqueada para todo lo que te de experiencia")
                } else {
                    GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(objetivo,
                            "Su xp ha sido desbloqueada ya puede seguir ganado experiencia")
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Arguments incorrects")
            }
            "UP_LEVEL", "NIVEL", "LEVEL" -> try {
                cantInt = Integer.parseInt(infos!![1])
                if (cantInt < 1) {
                    cantInt = 1
                }
                if (cantInt > MainServidor.NIVEL_MAX_PERSONAJE) {
                    cantInt = MainServidor.NIVEL_MAX_PERSONAJE
                }
                objetivo = _perso
                if (infos.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                if (objetivo.getEncarnacionN() != null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso,
                                "Le joueur est en mode incarnation, impossible de lui augmenter son niveau.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso,
                                "No se le puede subir el nivel, porque el personaje es una encarnación.")
                    }
                    return
                }
                objetivo.subirHastaNivel(cantInt)
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le niveau du joueur a été modifié.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ha sido modificado el nivel de " + objetivo.getNombre().toString() + " a "
                            + cantInt)
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Arguments incorrects")
            }
            "IP_PLAYER", "IP_PERSONAJE", "DAR_IP_PLAYER", "DAR_IP_PERSONAJE", "DAR_IP", "GET_IP", "GET_IP_PLAYER" -> {
                if (infos!!.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                if (!objetivo.enLinea()) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'est pas connecté.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no esta conectado")
                    }
                    return
                }
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "L'IP du joueur " + objetivo.getNombre().toString() + " est : " + objetivo
                            .getCuenta().getActualIP())
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El IP del personaje " + objetivo.getNombre().toString() + " es: " + objetivo
                            .getCuenta().getActualIP())
                }
            }
            "MOVER_RECAU", "MOVER_PERCO", "MOVE_PERCO", "MOVER_RECAUDADOR" -> {
                if (_perso.getPelea() != null) {
                    return
                }
                val recaudador: Recaudador = _perso.getMapa().getRecaudador()
                if (recaudador == null || recaudador.getPelea() != null) {
                    return
                }
                recaudador.moverRecaudador()
                recaudador.setEnRecolecta(false)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se movio el recaudador del mapa")
            }
            "MOVER_MOBS", "MOVE_MOB", "MOVE_MOBS", "MOVER_MOB" -> {
                if (_perso.getPelea() != null) {
                    return
                }
                id = 1
                try {
                    id = Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                }
                _perso.getMapa().moverGrupoMobs(id)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se movieron $id  grupos de mobs del mapa")
            }
            "OBJETO", "ITEM", "!GETITEM" ->                // final boolean esPorConsole = comando.equalsIgnoreCase("!GETITEM");
                try {
                    id = Integer.parseInt(infos!![1])
                    val OM: ObjetoModelo = Mundo.getObjetoModelo(id)
                    if (OM == null) {
                        if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "L'objet $id n'existe pas")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El objeto modelo $id no existe ")
                        }
                        return
                    }
                    if (OM.getOgrinas() > 0 && _perso.getAdmin() < 5) {
                        if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Tu ne possèdes pas le GM nécessaire pour spawn cet objet.")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "No posees el nivel de GM requerido")
                        }
                        return
                    }
                    try {
                        if (infos.size > 2) {
                            cantInt = Integer.parseInt(infos[2])
                        }
                    } catch (e: Exception) {
                    }
                    if (cantInt < 1) {
                        cantInt = 1
                    }
                    objetivo = _perso
                    if (infos.size > 3) {
                        objetivo = Mundo.getPersonajePorNombre(infos[3])
                    }
                    if (objetivo == null) {
                        if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                        }
                        return
                    }
                    if (MainServidor.PARAM_NOMBRE_COMPRADOR) {
                        if (_perso.getAdmin() < 5) {
                            objetivo = _perso
                        }
                        intercambiable = ObjetoModelo.stringFechaIntercambiable(3650)
                    }
                    var useMax: CAPACIDAD_STATS = CAPACIDAD_STATS.RANDOM
                    if (infos.size > 4) {
                        useMax = if (infos[4].equalsIgnoreCase("MAX")) CAPACIDAD_STATS.MAXIMO else if (infos[4].equalsIgnoreCase("MIN")) CAPACIDAD_STATS.MINIMO else CAPACIDAD_STATS.RANDOM
                    }
                    val obj: Objeto = OM.crearObjeto(cantInt, Constantes.OBJETO_POS_NO_EQUIPADO, useMax)
                    if (MainServidor.PARAM_NOMBRE_COMPRADOR && _perso.getAdmin() < 6) {
                        obj.addStatTexto(Constantes.STAT_INTERCAMBIABLE_DESDE, intercambiable)
                        obj.addStatTexto(Constantes.STAT_PERTENECE_Y_NO_VENDER, "0#0#0#" + _perso.getNombre())
                    }
                    objetivo.addObjIdentAInventario(obj, false)
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        strB.append("Creatio de l'objet " + OM.getNombre().toString() + " (" + id.toString() + ") en " + cantInt.toString() + " exemplaires pour "
                                + objetivo.getNombre())
                    } else {
                        strB.append("Se creó " + cantInt + " objeto(s) " + OM.getNombre() + " (" + id + ") para el personaje "
                                + objetivo.getNombre())
                    }
                    when (useMax) {
                        MAXIMO -> if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                            strB.append(" avec des jets parfaits.")
                        } else {
                            strB.append(" con stats máximos")
                        }
                        MINIMO -> if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                            strB.append(" avec des jets minimuns.")
                        } else {
                            strB.append(" con stats mínimos")
                        }
                        else -> {
                        }
                    }
                    if (objetivo.enLinea()) {
                        GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(objetivo)
                    } else {
                        strB.append(", mais le joueur n'est pas en ligne")
                    }
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, strB.toString())
                } catch (e: Exception) {
                }
            else -> {
                GM_lvl_3(comando, infos, mensaje, _cuenta, _perso)
                return
            }
        }
        // if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
        // GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Commande GM 4!");
        // } else {
        // GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Comando de nivel 4");
        // }spamear elbusta 3 HOwww.dofus.com
    }

    fun GM_lvl_5(comando: String?, infos: Array<String?>?, mensaje: String?, _cuenta: Cuenta?,
                 _perso: Personaje?) {
        var infos = infos
        var boleano = false
        var idByte: Byte = 0
        var ogrinas: Long = 0
        var idCant: Long = 0
        var idInt = 0
        var accionID = 0
        var idShort: Short = 0
        var celda1: Short = 0
        var celda2: Short = 0
        val objMod: ObjetoModelo
        var objetivo: Personaje? = null
        var cuenta: Cuenta?
        var str: String? = ""
        var args: String? = ""
        when (comando.toUpperCase()) {
            "RELOAD_CONFIG", "CARGAR_CONFIGURACION", "LOAD_CONFIG", "REFRESH_CONFIG" -> MainServidor.cargarConfiguracion(_perso)
            "BANEAR_UIID" -> {
                objetivo = if (infos!!.size > 1) {
                    Mundo.getPersonajePorNombre(infos[1])
                } else {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Arguments incorrects.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    }
                    return
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                val ipBaneada2: String = objetivo.getCuenta().getActualIP()
                val uiid: String = objetivo.getCuenta().getuuid()
                if (!GestorSQL.ES_IP_BANEADA(ipBaneada2)) {
                    if (GestorSQL.INSERT_BAN_IP(ipBaneada2)) {
                        if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "L'IP $ipBaneada2 est bannie.")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La IP $ipBaneada2 esta baneada.")
                        }
                    }
                }
                if (!GestorSQL.ES_UUID_BANEADA(uiid)) {
                    if (GestorSQL.INSERT_BAN_UUID(uiid)) {
                        if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "L'UUID $uiid est bannie.")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La UUID $uiid esta baneada.")
                        }
                    }
                }
                if (objetivo.enLinea()) {
                    objetivo.getServidorSocket().cerrarSocket(true, "command Banear IP")
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur a été kick.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El jugador fue retirado.")
                    }
                }
                for (account in Mundo.getCuentas().values()) {
                    if (account.enLinea()) {
                        if (account.getUltimaIP().equalsIgnoreCase(ipBaneada2) || account.getuuid().equalsIgnoreCase(uiid)) {
                            account.getSocket().cerrarSocket(true, "command Banear IP")
                        }
                    }
                }
            }
            "EVENTO_UBICACION" -> if (Mundo.evento) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El npc del evento esta en el mapa: " + Mundo.mapaNPC)
            } else {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "No hay eventos activos")
            }
            "PORTAL_GENERAR" -> Mundo.agregarPortal()
            "PORTAL_UBICACION" -> GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El npc del evento esta en el mapa: " + Mundo.portalNPC)
            "EVENTO_UBICACION_MOB" -> GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El npc del evento esta en el mapa: " + Mundo.mapaMOB)
            "EVENTO_GENERAR" -> {
                if (Mundo.evento) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se borro el npc de evento antiguo")
                }
                Mundo.agregarNpcEvento()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se agrego un nuevo NPC de eventos")
            }
            "EVENTO_GENERAR_MOB" -> {
                Mundo.agregarMobEvento()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se agrego un nuevo NPC de eventos")
            }
            "CLONAR_MAPA" -> try {
                val idClonar: Int = Integer.parseInt(infos!![1])
                val nuevaID: Int = Integer.parseInt(infos[2])
                val fecha = infos[3]
                val x: Int = Integer.parseInt(infos[4])
                val y: Int = Integer.parseInt(infos[5])
                val subArea: Int = Integer.parseInt(infos[6])
                val mapa: Mapa = Mundo.getMapa(idClonar.toShort())
                if (mapa == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Mapa a clonar no existe")
                    return
                }
                if (Mundo.getMapa(nuevaID.toShort()) != null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Mapa a crear ya existe")
                    return
                }
                if (GestorSQL.CLONAR_MAPA(mapa, nuevaID, fecha, x, y, subArea)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Mapa clonado con ID $nuevaID y fecha $fecha")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "No se pudo clonar el mapa")
                    return
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
                return
            }
            "CREAR_AUDIO", "CREAR_MP3", "CREATE_SOUND", "CREAR_SONIDO" -> try {
                infos = mensaje.split(" ", 2)
                str = infos!![1]
                val mp3: String = TextoAVoz.crearMP3(str, "")
                if (mp3 == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "No se pudo crear el sonido")
                } else if (mp3.isEmpty()) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Excediste en los caracteres")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Sonando el mp3 $mp3")
                    GestorSalida.ENVIAR_Bv_SONAR_MP3(_perso, mp3)
                }
            } catch (e: Exception) {
            }
            "CREAR_AUDIO_IDIOMA", "CREAR_MP3_IDIOMA", "CREAR_MP3_LANG", "CREAR_SOUND_LANG", "CREATE_MP3_LANG", "CREATE_SOUND_LANG", "CREAR_SONIDO_IDIOMA" -> try {
                infos = mensaje.split(" ", 3)
                str = infos!![2]
                val mp3: String = TextoAVoz.crearMP3(str, infos[1])
                if (mp3 == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "No se pudo crear el sonido")
                } else if (mp3.isEmpty()) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Excediste en los caracteres")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Sonando el mp3 $mp3")
                    GestorSalida.ENVIAR_Bv_SONAR_MP3(_perso, mp3)
                }
            } catch (e: Exception) {
            }
            "TELEPORT_TODOS" -> try {
                var mapaID: Short = 0
                var celdaID: Short = 0
                try {
                    if (infos!!.size > 1) {
                        mapaID = Short.parseShort(infos[1])
                    }
                    if (infos.size > 2) {
                        celdaID = Short.parseShort(infos[2])
                    }
                } catch (e: Exception) {
                }
                val mapa: Mapa = Mundo.getMapa(mapaID)
                if (mapa == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "MAPID INVALIDE!")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El mapa a teleportar no existe")
                    }
                    return
                }
                if (celdaID <= -1) {
                    celdaID = mapa.getRandomCeldaIDLibre()
                } else if (mapa.getCelda(celdaID) == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "CELLID INVALIDE!")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "CeldaID inválida")
                    }
                    return
                }
                for (p in Mundo.getPersonajesEnLinea()) {
                    p.teleport(mapaID, celdaID)
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
                return
            }
            "LIMITE_STATS_CON_BUFF" -> try {
                for (s in infos!![1].split(";")) {
                    if (s.isEmpty()) {
                        continue
                    }
                    try {
                        val stat: Array<String?> = s.split(",")
                        MainServidor.LIMITE_STATS_CON_BUFF.put(Integer.parseInt(stat[0]), Integer.parseInt(stat[1]))
                    } catch (e: Exception) {
                    }
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
                return
            }
            "LIMITE_STATS_SIN_BUFF" -> try {
                for (s in infos!![1].split(";")) {
                    if (s.isEmpty()) {
                        continue
                    }
                    try {
                        val stat: Array<String?> = s.split(",")
                        MainServidor.LIMITE_STATS_SIN_BUFF.put(Integer.parseInt(stat[0]), Integer.parseInt(stat[1]))
                    } catch (e: Exception) {
                    }
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
                return
            }
            "GET_CONFIGURACION", "GET_CONFIG" -> GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, MainServidor.getConfiguracion())
            "ADD_OBJETO_TRUEQUE", "AGREGAR_OBJETO_TRUEQUE", "ADD_TRUEQUE", "AGREGAR_TRUEQUE" -> try {
                var prioridad = 0
                val npcs = ""
                try {
                    idInt = Integer.parseInt(infos!![1])
                    str = infos[2]
                    prioridad = Byte.parseByte(infos[3])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
                    return
                }
                objMod = Mundo.getObjetoModelo(idInt)
                if (objMod == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Objeto modelo no existe")
                    return
                }
                if (str.isEmpty()) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Objetos necesarios invalidos")
                    return
                }
                Mundo.addObjetoTrueque(objMod.getID(), str, prioridad, npcs)
                GestorSQL.INSERT_OBJETO_TRUEQUE(objMod.getID(), str, prioridad, npcs, objMod.getNombre())
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se agregó el objeto trueque " + objMod.getNombre().toString() + " (" + objMod
                        .getID().toString() + "), objetos necesarios: " + str.toString() + ", prioridad: " + prioridad.toString() + ", npcs: " + npcs)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }
            "BAN_PERM_FDP", "BAN_PERMANENTE", "BAN_CLIENTE", "BAN_CLIENT", "BAN_DOFUS" -> try {
                if (infos!!.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                if (!objetivo.enLinea()) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'est pas connecté.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no esta conectado")
                    }
                    return
                }
                var tiempo = 10000
                try {
                    if (infos.size > 2) {
                        tiempo = Integer.parseInt(infos[2])
                    }
                } catch (e: Exception) {
                }
                GestorSalida.enviar(objetivo, "$$tiempo", true)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje " + objetivo.getNombre()
                        .toString() + " ha sido crasheado su cliente por " + tiempo.toString() + " minutos")
                objetivo.getCuenta().getSocket().cerrarSocket(true, "command BAN CLIENTE")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
            }
            "CRASH", "CRASH_FDP" -> try {
                infos = mensaje.split(" ", 3)
                if (infos!!.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                if (!objetivo.enLinea()) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'est pas connecté.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no esta conectado")
                    }
                    return
                }
                val veces = 10000
                GestorSalida.enviar(objetivo, "@" + veces + ";HOhttp://" + infos[2], true)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje " + objetivo.getNombre()
                        .toString() + " ha sido crasheado con la url " + infos[2])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
            }
            "SPAMMEAR", "SPAM", "SPAMEAR" -> try {
                infos = mensaje.split(" ", 4)
                if (infos!!.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                if (!objetivo.enLinea()) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'est pas connecté.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no esta conectado")
                    }
                    return
                }
                var veces = 10000
                try {
                    if (infos.size > 2) {
                        veces = Integer.parseInt(infos[2])
                    }
                } catch (e: Exception) {
                }
                GestorSalida.enviar(objetivo, "@" + veces + ";" + infos[3], true)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje " + objetivo.getNombre()
                        .toString() + " ha sido spameado su cliente por " + veces.toString() + " veces, con el packet " + infos[3])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
            }
            "CAMBIAR_NOMBRE", "CHANGE_NAME" -> try {
                if (infos!!.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                if (infos.size > 2) {
                    val viejoNombre: String = objetivo.getNombre()
                    val nombre: String = Personaje.nombreValido(infos[2], true)
                    if (nombre != null && !nombre.isEmpty()) {
                        objetivo.cambiarNombre(nombre)
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se cambio el nombre del jugador " + viejoNombre + " cambio a "
                                + objetivo.getNombre())
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Nuevo nombre incorrecto o ya esta en uso")
                    }
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ingrese el nuevo nombre")
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
            }
            "GET_PATH" -> {
                try {
                    celda1 = Short.parseShort(infos!![1])
                    celda2 = Short.parseShort(infos!![2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                idInt = -1
                try {
                    idInt = Integer.parseInt(infos!![3])
                } catch (e: Exception) {
                }
                val path: Duo<Integer?, ArrayList<Celda?>?> = Camino.getPathPelea(if (_perso.getPelea() != null) _perso.getPelea().getMapaCopia() else _perso.getMapa(), celda1, celda2, idInt, null, true)
                if (path != null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Path es " + path._primero.toString() + " y " + path._segundo.size())
                    var s = ""
                    for (c in path._segundo) {
                        s += c.getID().toString() + " "
                    }
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "celdas $s")
                } else GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Path es nulo")
            }
            "FINISH_ALL_FIGHTS", "FINALIZAR_PELEAS", "FINISH_COMBATS", "FINISH_FIGHTS" -> {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "FINALIZANDO TODAS LAS PELEAS ... ")
                Mundo.finalizarPeleas()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "100%")
            }
            "REGISTER", "REGISTRO", "REGISTE", "REGISTRAR" -> {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "INICIANDO EL REGISTRO DE JUGADORES Y SQL ... ")
                MainServidor.imprimirLogPlayers()
                MainServidor.imprimirLogSQL()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "100%")
            }
            "REGISTER_SQL", "REGISTRO_SQL", "REGISTE_SQL", "REGISTRAR_SQL" -> {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "INICIANDO EL REGISTRO DE SQL ... ")
                MainServidor.imprimirLogSQL()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "100%")
            }
            "REGISTER_PLAYERS", "REGISTRO_PLAYERS", "REGISTE_PLAYERS", "REGISTRAR_PLAYERS" -> {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "INICIANDO EL REGISTRO DE JUGADORES ... ")
                MainServidor.imprimirLogPlayers()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "100%")
            }
            "REGISTRAR_PJ", "REGISTER_PLAYER", "REGISTRAR_PLAYER", "REGISTRAR_JUGADOR" -> {
                objetivo = _perso
                if (infos!!.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                if (!ServidorSocket.JUGADORES_REGISTRAR.contains(objetivo.getCuenta().getNombre())) {
                    ServidorSocket.JUGADORES_REGISTRAR.add(objetivo.getCuenta().getNombre())
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La cuenta del personaje " + objetivo.getNombre()
                            .toString() + " fue registrada para archivar los logs")
                }
            }
            "COMANDOS_PERMITIDOS", "COMANDO_PERMITIDO", "ADD_COMANDO_PERMITIDO", "AGREGAR_COMANDO_PERMITIDO" -> try {
                MainServidor.COMANDOS_PERMITIDOS.add(infos!![1])
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se agrego a comandos permitidos: " + infos[1])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Debes poner algun comando para agregar")
            }
            "COMANDOS_VIP", "COMANDO_VIP", "ADD_COMANDO_VIP", "AGREGAR_COMANDO_VIP" -> try {
                MainServidor.COMANDOS_VIP.add(infos!![1])
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se agrego a comandos vips: " + infos[1])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Debes poner algun comando para agregar")
            }
            "PALABRAS_PROHIBIDAS" -> try {
                MainServidor.PALABRAS_PROHIBIDAS.add(infos!![1])
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se agrego a palabras prohibidas: " + infos[1])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Debes poner alguna palabra para agregar")
            }
            "INFO_STUFF_PJ", "INFO_STUFF_PERSO", "INFO_ROPA_PJ", "INFO_ROPA_PERSO" -> {
                objetivo = _perso
                if (infos!!.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                for (i in Constantes.POSICIONES_EQUIPAMIENTO) {
                    if (!str.isEmpty()) {
                        str += ", "
                    }
                    if (objetivo.getObjPosicion(i) == null) {
                        str += "null"
                    } else {
                        str += objetivo.getObjPosicion(i).getID()
                    }
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Stuff de " + objetivo.getNombre().toString() + " es " + str)
            }
            "TIEMPO_POR_LANZAR_HECHIZO" -> {
                idInt = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                EfectoHechizo.TIEMPO_POR_LANZAR_HECHIZO = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El TIEMPO_POR_LANZAR_HECHIZO cambio a $idInt")
            }
            "TIEMPO_GAME_ACTION" -> {
                idInt = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                EfectoHechizo.TIEMPO_GAME_ACTION = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El TIEMPO_GAME_ACTION cambio a $idInt")
            }
            "TIEMPO_ENTRE_EFECTOS" -> {
                idInt = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                EfectoHechizo.TIEMPO_ENTRE_EFECTOS = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El TIEMPO_ENTRE_EFECTOS cambio a $idInt")
            }
            "TIME_SLEEP_PACKETS_CARGAR_MAPA" -> {
                idInt = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.TIME_SLEEP_PACKETS_CARGAR_MAPA = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El TIME_SLEEP_PACKETS_CARGAR_MAPA cambio a $idInt")
            }
            "PROBABILIDAD_PROTECTOR_RECURSOS" -> {
                try {
                    idByte = Byte.parseByte(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.PROBABILIDAD_PROTECTOR_RECURSOS = idByte.toInt()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PROBABILIDAD_PROTECTOR_RECURSOS cambio a $idByte")
            }
            "SEGUNDOS_REAPARECER_MOBS" -> {
                idInt = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.SEGUNDOS_REAPARECER_MOBS = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El SEGUNDOS_REAPARECER_MOBS cambio a $idInt")
            }
            "FACTOR_ZERO_DROP" -> {
                idInt = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.FACTOR_ZERO_DROP = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El FACTOR_ZERO_DROP cambio a $idInt")
            }
            "ID_MIMOBIONTE" -> {
                idInt = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.ID_MIMOBIONTE = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La ID del mimobionte cambio a $idInt")
            }
            "MODIFICAR_PARAM" -> try {
                val resto: String = mensaje.split(" ", 2).get(1)
                consolaComando(resto, _cuenta, _perso)
                infos = resto.split(" ", 2)
                MainServidor.modificarParam(infos!![0], infos[1])
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se cambio el parametro: " + infos[0] + " a " + infos[1])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
            }
            "DESHABILITAR_SQL" -> {
                try {
                    boleano = infos!![1].equalsIgnoreCase("true")
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.PARAM_DESHABILITAR_SQL = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Deshabilitar sql ahora esta $boleano")
            }
            "OGRINAS_POR_VOTO" -> {
                try {
                    idByte = Byte.parseByte(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.OGRINAS_POR_VOTO = idByte.toShort()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Las ogrinas por voto cambio a $idByte")
            }
            "MINUTOS_VALIDAR_VOTO", "MINUTOS_SIGUIENTE_VOTO", "MINUTOS_SIG_VOTO" -> {
                try {
                    idShort = Short.parseShort(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.MINUTOS_VALIDAR_VOTO = idShort.toInt()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Los minutos para el siguiente voto cambio a $idShort")
            }
            "MAX_MISIONES_ALMANAX" -> {
                try {
                    idShort = Short.parseShort(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.MAX_MISIONES_ALMANAX = idShort.toInt()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El max de misiones almanax cambio a $idShort")
            }
            "MAX_CARACTERES_SONIDO" -> {
                idInt = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.MAX_CARACTERES_SONIDO = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El MAX_CARACTERES_SONIDO cambio a $idInt")
            }
            "MAX_PACKETS_DESCONOCIDOS" -> {
                idInt = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.MAX_PACKETS_DESCONOCIDOS = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El MAX_PACKETS_PARA_RASTREAR cambio a $idInt")
            }
            "MAX_PACKETS_PARA_RASTREAR" -> {
                idInt = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.MAX_PACKETS_PARA_RASTREAR = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El MAX_PACKETS_PARA_RASTREAR cambio a $idInt")
            }
            "PROBABILIDAD_RECURSO_ESPECIAL", "PROBABILIDAD_OBJ_ESPECIAL" -> {
                idInt = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.PROBABILIDAD_RECURSO_ESPECIAL = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La probabilidad de recurso recolecta especial cambio a "
                        + MainServidor.PROBABILIDAD_RECURSO_ESPECIAL)
            }
            "PROBABLIDAD_PERDER_STATS_FM", "PROBABLIDAD_LOST_STATS_FM", "PROBABILIDAD_FALLO_FM" -> {
                idInt = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.PROBABLIDAD_PERDER_STATS_FM = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La probabilidad de fallo critico FM cambio a "
                        + MainServidor.PROBABLIDAD_PERDER_STATS_FM)
            }
            "PERMITIR_BONUS_ESTRELLAS" -> {
                try {
                    boleano = infos!![1].equalsIgnoreCase("true")
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.PARAM_PERMITIR_BONUS_ESTRELLAS = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El parametro permitir bonus estrellas cambio a "
                        + MainServidor.PARAM_PERMITIR_BONUS_ESTRELLAS)
            }
            "PERMITIR_BONUS_RETOS" -> {
                try {
                    boleano = infos!![1].equalsIgnoreCase("true")
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.PARAM_PERMITIR_BONUS_DROP_RETOS = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El parametro permitir bonus retos cambio a $boleano")
            }
            "MAX_STARS_MOBS", "MAX_ESTRELLAS_MOBS" -> {
                try {
                    idShort = Short.parseShort(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.MAX_BONUS_ESTRELLAS_MOBS = (idShort * 20).toShort().toInt()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El maximo de estrellas mobs cambio a $idShort")
            }
            "MAX_BONUS_ESTRELLAS_RECURSOS", "MAX_STARS_RESSOURCES", "MAX_STARS_RECURSOS", "MAX_ESTRELLAS_RECURSOS" -> {
                try {
                    idShort = Short.parseShort(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.MAX_BONUS_ESTRELLAS_RECURSOS = (idShort * 20).toShort().toInt()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El maximo de estrellas mobs cambio a $idShort")
            }
            "PROBABILIDAD_ESCAPAR_MONTURA_DESPUES_PARIR" -> {
                try {
                    idByte = Byte.parseByte(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.PROBABILIDAD_ESCAPAR_MONTURA_DESPUES_FECUNDAR = idByte.toInt()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La probabilidad de escapar la montura despues de parir cambio a "
                        + MainServidor.PROBABILIDAD_ESCAPAR_MONTURA_DESPUES_FECUNDAR)
            }
            "CHANGE_FACE", "CAMBIAR_ROSTRO" -> {
                try {
                    idByte = Byte.parseByte(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                objetivo = _perso
                if (infos!!.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                objetivo.cambiarRostro(idByte)
                GestorSalida.ENVIAR_Oa_CAMBIAR_ROPA_MAPA(objetivo.getMapa(), objetivo)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se cambio el rostro al personaje " + objetivo.getNombre().toString() + " a "
                        + idByte)
            }
            "PERMITIR_BONUS_DROP_RETOS" -> {
                try {
                    boleano = infos!![1].equalsIgnoreCase("true")
                } catch (e: Exception) {
                }
                MainServidor.PARAM_PERMITIR_BONUS_DROP_RETOS = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PERMITIR_BONUS_DROP_RETOS cambio a $boleano")
            }
            "PERMITIR_BONUS_EXP_RETOS" -> {
                try {
                    boleano = infos!![1].equalsIgnoreCase("true")
                } catch (e: Exception) {
                }
                MainServidor.PARAM_PERMITIR_BONUS_EXP_RETOS = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PERMITIR_BONUS_EXP_RETOS cambio a $boleano")
            }
            "PARAM_RANKING_STAFF" -> {
                try {
                    boleano = infos!![1].equalsIgnoreCase("true")
                } catch (e: Exception) {
                }
                MainServidor.PARAM_LADDER_STAFF = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PARAM_RANKING_STAFF cambio a $boleano")
            }
            "PARAM_INFO_DAÑO_BATALLA" -> {
                try {
                    boleano = infos!![1].equalsIgnoreCase("true")
                } catch (e: Exception) {
                }
                MainServidor.PARAM_INFO_DAÑO_BATALLA = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PARAM_INFO_DAÑO_BATALLA cambio a $boleano")
            }
            "PARAM_MOSTRAR_EXP_MOBS" -> {
                try {
                    boleano = infos!![1].equalsIgnoreCase("true")
                } catch (e: Exception) {
                }
                MainServidor.PARAM_MOSTRAR_EXP_MOBS = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PARAM_MOSTRAR_EXP_MOBS cambio a $boleano")
            }
            "PARAM_AUTO_SANAR", "PARAM_AUTO_CURAR", "PARAM_AUTO_RECUPERAR_VIDA" -> {
                try {
                    boleano = infos!![1].equalsIgnoreCase("true")
                } catch (e: Exception) {
                }
                MainServidor.PARAM_AUTO_RECUPERAR_TODA_VIDA = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PARAM_AUTO_RECUPERAR_TODA_VIDA cambio a $boleano")
            }
            "PARAM_MOSTRAR_PROBABILIDAD_TACLEO" -> {
                try {
                    boleano = infos!![1].equalsIgnoreCase("true")
                } catch (e: Exception) {
                }
                MainServidor.PARAM_MOSTRAR_PROBABILIDAD_TACLEO = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PARAM_MOSTRAR_PROBABILIDAD_TACLEO cambio a $boleano")
            }
            "PARAM_AUTO_SALTAR_TURNO" -> {
                try {
                    boleano = infos!![1].equalsIgnoreCase("true")
                } catch (e: Exception) {
                }
                MainServidor.PARAM_AUTO_SALTAR_TURNO = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PARAM_AUTO_SALTAR_TURNO cambio a $boleano")
            }
            "PARAM_TODOS_MOBS_EN_BESTIARIO" -> {
                try {
                    boleano = infos!![1].equalsIgnoreCase("true")
                } catch (e: Exception) {
                }
                MainServidor.PARAM_TODOS_MOBS_EN_BESTIARIO = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PARAM_TODOS_MOBS_EN_BESTIARIO cambio a $boleano")
            }
            "PARAM_CAPTURAR_MONTURA_COMO_PERGAMINO" -> {
                try {
                    boleano = infos!![1].equalsIgnoreCase("true")
                } catch (e: Exception) {
                }
                MainServidor.PARAM_CAPTURAR_MONTURA_COMO_PERGAMINO = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PARAM_CAPTURAR_MONTURA_COMO_PERGAMINO cambio a $boleano")
            }
            "PARAM_AGRESION_ADMIN" -> {
                try {
                    boleano = infos!![1].equalsIgnoreCase("true")
                } catch (e: Exception) {
                }
                MainServidor.PARAM_AGRESION_ADMIN = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PARAM_AGRESION_ADMIN cambio a $boleano")
            }
            "JUGAR_RAPIDO", "PARAM_JUGAR_RAPIDO" -> {
                try {
                    boleano = infos!![1].equalsIgnoreCase("true")
                } catch (e: Exception) {
                }
                MainServidor.PARAM_JUGAR_RAPIDO = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PARAM_JUGAR_RAPIDO cambio a $boleano")
            }
            "PARAM_PERDER_ENERGIA" -> {
                try {
                    boleano = infos!![1].equalsIgnoreCase("true")
                } catch (e: Exception) {
                }
                MainServidor.PARAM_PERDER_ENERGIA = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PARAM_PERDER_ENERGIA cambio a $boleano")
            }
            "PARAM_ALBUM", "ALBUM_MOBS", "ACTIVAR_ALBUM" -> {
                try {
                    boleano = infos!![1].equalsIgnoreCase("true")
                } catch (e: Exception) {
                }
                MainServidor.PARAM_BESTIARIO = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PARAM_ALBUM_MOBS cambio a $boleano")
            }
            "PARAM_AGRESION_MULTICUENTA" -> {
                try {
                    boleano = infos!![1].equalsIgnoreCase("true")
                } catch (e: Exception) {
                }
                MainServidor.PARAM_PERMITIR_MULTICUENTA_PELEA_PVP = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PARAM_AGRESION_MULTICUENTA cambio a $boleano")
            }
            "PARAM_LOTERIA" -> {
                try {
                    boleano = infos!![1].equalsIgnoreCase("true")
                } catch (e: Exception) {
                }
                MainServidor.PARAM_LOTERIA = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PARAM_LOTERIA cambio a $boleano")
            }
            "COMANDOS_JUGADOR", "COMMANDES_JOUERS", "COMMANDS_PLAYERS", "PARAM_COMANDOS_JUGADOR" -> {
                try {
                    boleano = infos!![1].equalsIgnoreCase("true")
                } catch (e: Exception) {
                }
                MainServidor.PARAM_COMANDOS_JUGADOR = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PARAM_COMANDOS_JUGADOR cambio a $boleano")
            }
            "PARAM_AURA", "ACTIVAR_AURA", "AURA" -> {
                try {
                    boleano = infos!![1].equalsIgnoreCase("true")
                } catch (e: Exception) {
                }
                MainServidor.PARAM_ACTIVAR_AURA = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PARAM_ACTIVAR_AURA cambio a $boleano")
            }
            "PARAM_ANTI_SPEEDHACK" -> {
                try {
                    boleano = infos!![1].equalsIgnoreCase("true")
                } catch (e: Exception) {
                }
                MainServidor.PARAM_ANTI_SPEEDHACK = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PARAM_ANTI_SPEEDHACK cambio a $boleano")
            }
            "PARAM_ANTI_DDOS", "CONTRA_DDOS", "ANTI_DDOS" -> {
                try {
                    boleano = infos!![1].equalsIgnoreCase("true")
                } catch (e: Exception) {
                }
                MainServidor.PARAM_ANTI_DDOS = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PARAM_ANTI_DDOS cambio a $boleano")
            }
            "RECOLECTOR_BASURA", "GC", "GARBAGE_COLLECTOR" -> try {
                System.gc()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se inicio el garbage collector")
            } catch (e: Exception) {
                MainServidor.redactarLogServidorln("COMANDO GARBAGE COLLECTOR " + e.toString())
                e.printStackTrace()
            }
            "MEMORY", "MEMORY_USE", "MEMORIA", "MEMORIA_USADA", "ESTADO_JVM" -> try {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "----- ESTADO JVM -----\nFreeMemory: " + (Runtime.getRuntime()
                        .freeMemory() / 1048576f).toString() + " MB\nTotalMemory: " + (Runtime.getRuntime().totalMemory() / 1048576f
                        ).toString() + " MB\nMaxMemory: " + (Runtime.getRuntime().maxMemory() / 1048576f).toString() + " MB\nProcesos: " + Runtime.getRuntime()
                        .availableProcessors())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }
            "SABIDURIA_PARA_REENVIO" -> {
                idInt = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.SABIDURIA_PARA_REENVIO = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La sabiduria para aumentar el daño por reenvio cambio a $idInt")
            }
            "MILISEGUNDOS_CERRAR_SERVIDOR", "TIEMPO_CERRAR_SERVIDOR", "TIME_CLOSE_SERVER" -> {
                idInt = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.MILISEGUNDOS_CERRAR_SERVIDOR = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modifico el tiempo para cerra el servidor a " + idInt
                        + " milisegundos")
            }
            "SEGUNDOS_PUBLICIDAD", "TIEMPO_PUBLICIDAD" -> {
                idInt = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.SEGUNDOS_PUBLICIDAD = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modifico el tiempo de publicidad a $idInt segundos")
            }
            "MILISEGUNDOS_ANTI_FLOOD", "TIEMPO_ANTI_FLOOD" -> {
                idInt = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.MILISEGUNDOS_ANTI_FLOOD = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modifico el tiempo de Anti-Flood a $idInt milisegundos")
            }
            "MIN_NIVEL_KOLISEO" -> {
                idInt = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.MIN_NIVEL_KOLISEO = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modifico el nivel minimo para koliseo a $idInt")
            }
            "SEGUNDOS_INICIAR_KOLISEO", "TIEMPO_KOLISEO" -> {
                idInt = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                Mundo.SEGUNDOS_INICIO_KOLISEO = idInt
                MainServidor.SEGUNDOS_INICIAR_KOLISEO = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modifico el tiempo de Koliseo a $idInt")
            }
            "PARAM_DEVOLVER_OGRINAS", "DEVOLVER_OGRINAS" -> {
                try {
                    boleano = infos!![1].equalsIgnoreCase("true")
                } catch (e: Exception) {
                }
                MainServidor.PARAM_DEVOLVER_OGRINAS = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se cambió devolver ogrinas a " + MainServidor.PARAM_DEVOLVER_OGRINAS)
            }
            "PARAM_LADDER", "LADDER" -> {
                try {
                    boleano = infos!![1].equalsIgnoreCase("true")
                } catch (e: Exception) {
                }
                MainServidor.PARAM_LADDER_NIVEL = boleano
                if (boleano) Mundo.actualizarRankings()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se cambió ladder a " + MainServidor.PARAM_LADDER_NIVEL)
            }
            "MOBS_EVENTO" -> {
                try {
                    idByte = Byte.parseByte(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                Mundo.MOB_EVENTO = idByte
                RefrescarTodosMobs().start()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se cambió el mobEvento a " + idByte
                        + " y se esta refrescando todos los mapas")
            }
            "SET_STATS_OBJ_MODELO", "SET_STATS_OBJETO_MODELO", "SET_STATS_ITEM_TEMPLATE", "SET_STATS_MODELO" -> {
                idInt = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                try {
                    str = infos[2]
                } catch (e: Exception) {
                }
                objMod = Mundo.getObjetoModelo(idInt)
                if (objMod == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Objeto set nulo")
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modifico los statsModelo del objeto " + objMod.getNombre()
                        .toString() + ": \nAntiguo Stats - " + objMod.getStatsModelo().toString() + "\nNuevos Stats - " + str)
                objMod.setStatsModelo(str)
                GestorSQL.UPDATE_STATS_OBJETO_MODELO(idInt, str)
                try {
                    for (npcMod in Mundo.NPC_MODELOS.values()) {
                        if (npcMod.getObjAVender().contains(objMod)) {
                            npcMod.actualizarObjetosAVender()
                        }
                    }
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
                }
            }
            "PRECIO_LOTERIA" -> {
                try {
                    idInt = Integer.parseInt(infos!![1])
                    boleano = infos[2].equalsIgnoreCase("true")
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.PRECIO_LOTERIA = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modifico el precio de loteria  a " + idInt + if (boleano) " ogrinas" else " kamas")
            }
            "PREMIO_LOTERIA" -> {
                idInt = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.PREMIO_LOTERIA = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modifico el premio de loteria  a " + idInt
                        + if (MainServidor.PARAM_LOTERIA_OGRINAS) " ogrinas" else " kamas")
            }
            "LOTERIA_OGRINAS" -> {
                try {
                    boleano = infos!![1].equalsIgnoreCase("true")
                } catch (e: Exception) {
                }
                MainServidor.PARAM_LOTERIA_OGRINAS = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Loteria ogrinas es " + MainServidor.PARAM_DEVOLVER_OGRINAS)
            }
            "GANADORES_POR_BOLETOS" -> {
                idInt = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                MainServidor.GANADORES_POR_BOLETOS = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modifico la cantidad de premios por cada " + idInt
                        + " boletos comprados")
            }
            "OGRINAS_OBJETO_MODELO", "SET_ITEM_POINTS" -> try {
                try {
                    idInt = Integer.parseInt(infos!![1])
                    ogrinas = Integer.parseInt(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                objMod = Mundo.getObjetoModelo(idInt)
                if (objMod == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Objeto set nulo")
                    return
                }
                objMod.setOgrinas(ogrinas)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modifico el precio del objeto modelo " + objMod.getNombre()
                        .toString() + " a " + ogrinas.toString() + " ogrinas")
                try {
                    for (npcMod in Mundo.NPC_MODELOS.values()) {
                        if (npcMod.getObjAVender().contains(objMod)) {
                            npcMod.actualizarObjetosAVender()
                        }
                    }
                } catch (e: Exception) {
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }
            "KAMAS_OBJETO_MODELO" -> try {
                try {
                    idInt = Integer.parseInt(infos!![1])
                    ogrinas = Integer.parseInt(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                objMod = Mundo.getObjetoModelo(idInt)
                if (objMod == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Objeto set nulo")
                    return
                }
                objMod.setKamas(ogrinas)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modifico el precio del objeto modelo " + objMod.getNombre()
                        .toString() + " a " + ogrinas.toString() + " kamas")
                try {
                    for (npcMod in Mundo.NPC_MODELOS.values()) {
                        if (npcMod.getObjAVender().contains(objMod)) {
                            npcMod.actualizarObjetosAVender()
                        }
                    }
                } catch (e: Exception) {
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }
            "SET_STATS_ITEM", "SETSTATSOBJETO", "SET_STATS_OBJETO" -> try {
                infos = mensaje.split(" ", 3)
                idInt = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                val obj: Objeto = Mundo.getObjeto(idInt)
                if (obj == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Objeto nulo")
                    return
                }
                try {
                    str = infos[2]
                } catch (e: Exception) {
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Cambio stats del objeto $idInt: \nAntiguo Stats - " + obj
                        .convertirStatsAString(true) + "\nNuevos Stats - " + str)
                obj.convertirStringAStats(str)
                if (_perso.getObjeto(idInt) != null) {
                    if (_perso.enLinea()) {
                        GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(_perso, obj)
                        if (Constantes.esPosicionEquipamiento(obj.getPosicion())) {
                            _perso.refrescarStuff(true, true, false)
                        }
                    }
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
            }
            "BORRAR_OBJETO", "BORRAR_ITEM", "DEL_ITEM", "DELETE_ITEM" -> {
                idInt = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                val obj: Objeto = Mundo.getObjeto(idInt)
                if (obj == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Objeto nulo")
                    return
                }
                if (_perso.tieneObjetoID(obj.getID())) {
                    _perso.borrarOEliminarConOR(idInt, true)
                } else {
                    Mundo.eliminarObjeto(obj.getID())
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se elimino el objeto " + obj.getID().toString() + " (" + obj.getObjModelo()
                        .getNombre().toString() + ")")
            }
            "CAMBIAR_CONTRASEÑA", "CAMBIAR_CLAVE", "CHANGE_PASSWORD" -> {
                var consultado: Cuenta? = null
                if (infos!!.size > 1) {
                    consultado = Mundo.getCuentaPorNombre(infos[1])
                }
                if (consultado == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La cuenta no existe")
                    return
                }
                str = if (infos.size > 2) {
                    infos[2]
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La contraseña no puede estar vacia")
                    return
                }
                GestorSQL.CAMBIAR_CONTRASEÑA_CUENTA(str, consultado.getID())
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La cuenta " + consultado.getNombre().toString() + " ha cambiado su contraseña a "
                        + str)
            }
            "ADMIN" -> {
                idInt = -1
                idInt = try {
                    Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Arguments incorrecto")
                    return
                }
                if (idInt <= -1) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Merci d'indiquer un GM valide!")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El arguemento tiene que ser un número positivo")
                    }
                    return
                }
                objetivo = _perso
                if (infos.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                objetivo.getCuenta().setRango(if (idInt > 0) 1 else 0)
                objetivo.setRango(idInt)
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le GM du joueur a été modifié!")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje " + objetivo.getNombre().toString() + " ahora tiene GM nivel "
                            + idInt)
                }
            }
            "PARAM_PRECIO_RECURSOS_EN_OGRINAS", "RECURSOS_EN_OGRINAS" -> {
                try {
                    boleano = infos!![1].equalsIgnoreCase("true")
                } catch (e: Exception) {
                }
                MainServidor.PARAM_PRECIO_RECURSOS_EN_OGRINAS = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Sistema recurso de ogrinas se cambio a $boleano")
            }
            "PARAM_RECETA_SIEMPRE_EXITOSA", "PARAM_CRAFT_SEGURO", "CRAFT_SEGURO" -> {
                try {
                    boleano = infos!![1].equalsIgnoreCase("true")
                } catch (e: Exception) {
                }
                MainServidor.PARAM_CRAFT_SIEMPRE_EXITOSA = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Craft Seguro se cambio a "
                        + MainServidor.PARAM_CRAFT_SIEMPRE_EXITOSA)
            }
            "DATOS_SQL" -> GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Datos de la database: \nusuario-" + MainServidor.BD_USUARIO
                    .toString() + "\npass-" + MainServidor.BD_PASS.toString() + "\nhost-" + MainServidor.BD_HOST.toString() + "\ndb_dinamica-"
                    + MainServidor.BD_DINAMICA.toString() + "\nbd_estatica-" + MainServidor.BD_ESTATICA.toString() + "\nbd_cuentas-"
                    + MainServidor.BD_CUENTAS)
            "COMIDA_MASCOTA" -> {
                var mascota = -1
                try {
                    mascota = Integer.parseInt(infos!![1])
                } catch (e1: Exception) {
                }
                if (mascota == -1) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                val masc: MascotaModelo = Mundo.getMascotaModelo(mascota)
                if (masc == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Mascota nula")
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Las estadisticas de la mascota son " + masc.getStrComidas())
            }
            "BLOQUEAR_ATAQUE", "BLOCK_ATTACK" -> {
                try {
                    boleano = infos!![1].equalsIgnoreCase("true")
                } catch (e2: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento incorrecto")
                    return
                }
                Mundo.BLOQUEANDO = boleano
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso,
                            "L'accès au serveur est bloqué le temps que les attaques se calment.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso,
                            "Se activo medidas de bloqueo acceso al server, hasta que pare el ataque")
                }
                if (Mundo.BLOQUEANDO) {
                    GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS("El Servidor esta siendo atacado, se ha activado la el ANTI-ATTACK de Elbusta, por el momento no se podran conectar al servidor, "
                            + "pero si continuar jugando, porfavor eviten salir, que en unos minutos reestablecemos la conexion al servidor, GRACIAS!!",
                            "L'accès au serveur est bloqué car nous sommes attaqués, merci de ne pas vous déconnecter!")
                } else {
                    GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(
                            "El ataque ha parado, ahora el servidor desbloqueara el acceso a las cuentas, YA PUEDEN LOGUEARSE, SIN TEMOR!! GRACIAS ELBUSTA!!",
                            "L'accès au serveur est rétabli!")
                }
            }
            "PARAM_REGISTRO_JUGADORES" -> {
                try {
                    boleano = infos!![1].equalsIgnoreCase("true")
                } catch (e: Exception) {
                }
                MainServidor.PARAM_REGISTRO_LOGS_JUGADORES = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "PARAM_REGISTRO_JUGADORES se cambio a $boleano")
            }
            "MOSTRAR_RECIBIDOS", "RECIBIDOS" -> {
                try {
                    boleano = infos!![1].equalsIgnoreCase("true")
                } catch (e: Exception) {
                }
                MainServidor.MOSTRAR_RECIBIDOS = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Mostrar recibidos se cambio a $boleano")
            }
            "MOSTRAR_ENVIOS", "ENVIADOS" -> {
                try {
                    boleano = infos!![1].equalsIgnoreCase("true")
                } catch (e: Exception) {
                }
                MainServidor.MOSTRAR_ENVIOS = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Mostrar enviados se cambio a $boleano")
            }
            "DOBLE_EXP" -> {
                try {
                    boleano = infos!![1].equalsIgnoreCase("true")
                } catch (e: Exception) {
                }
                var rate = 0
                rate = try {
                    Integer.parseInt(infos!![2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "No se encontro cantidad de rate")
                    return
                }
                MainServidor.DOBLE_EXPERIENCIA = boleano
                MainServidor.EXPERIENCIA_CANTIDAD = rate
                GestorSalida.ENVIAR_DOBLE_EXPERIENCIA(_perso, MainServidor.EXPERIENCIA_CANTIDAD)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El bonus de Experiencia esta$boleano")
            }
            "DEBUG", "MODO_DEBUG" -> {
                try {
                    boleano = infos!![1].equalsIgnoreCase("true")
                } catch (e: Exception) {
                }
                MainServidor.MODO_DEBUG = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Mostrar mensajes debug se cambio a $boleano")
            }
            "MODO_HEROICO" -> {
                try {
                    boleano = infos!![1].equalsIgnoreCase("true")
                } catch (e: Exception) {
                }
                MainServidor.MODO_HEROICO = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El MODO_HEROICO cambio a $boleano")
            }
            "ACCOUNT_PASSWORD", "CUENTA_CONTRASEÑA", "GET_PASS" -> {
                if (infos!!.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                cuenta = objetivo.getCuenta()
                if (cuenta == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La cuenta es nula")
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La cuenta es " + cuenta.getNombre().toString() + " y la contraseña es " + cuenta
                        .getContraseña())
            }
            "BORRAR_PRISMA" -> try {
                val mapa: Mapa = _perso.getMapa()
                val prisma: Prisma = mapa.getSubArea().getPrisma()
                if (prisma == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Esta subArea no posee prisma")
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se borró el prisma de la subArea " + prisma.getSubArea().getID())
                prisma.murio()
            } catch (e: Exception) {
            }
            "SEND", "ENVIAR" -> try {
                infos = mensaje.split(" ", 2)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El emulador ha recibido el packet " + infos!![1])
                _cuenta.getSocket().analizar_Packets(infos[1], false)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento incorrecto")
            }
            "SEND_FLOOD", "ENVIAR_FLOOD" -> try {
                infos = mensaje.split(" ", 4)
                val veces: Int = Integer.parseInt(infos!![1])
                var time: Int = Integer.parseInt(infos[2])
                if (time <= 0) {
                    time = 1
                }
                var i = 0
                while (i < veces) {
                    _cuenta.getSocket().analizar_Packets(infos[3], false)
                    Thread.sleep(time)
                    i++
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El emulador ha recibido el packet " + infos[1])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento incorrecto")
            }
            "SEND_PLAYER", "ENVIAR_PJ" -> try {
                infos = mensaje.split(" ", 3)
                if (infos!!.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                cuenta = objetivo.getCuenta()
                if (cuenta == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La cuenta es nula")
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El emulador ha recibido del jugador " + objetivo.getNombre()
                        .toString() + " el packet " + infos[2])
                cuenta.getSocket().analizar_Packets(infos[2], false)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento incorrecto")
            }
            "RECIVED", "RECIBIR" -> try {
                infos = mensaje.split(" ", 2)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La core ha recibido el packet " + infos!![1])
                GestorSalida.enviar(_perso, infos[1], false)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento incorrecto")
            }
            "RECIBIR_TODOS" -> try {
                infos = mensaje.split(" ", 3)
                val tiempo: Int = Integer.parseInt(infos!![1])
                GestorSalida.enviarTodos(tiempo, infos[2])
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La core de todos han recibido el packet " + infos[2])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento incorrecto")
            }
            "RECIBIR_MAPA" -> try {
                infos = mensaje.split(" ", 2)
                for (perso in _perso.getMapa().getArrayPersonajes()) {
                    GestorSalida.enviar(perso, infos!![1], false)
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La core de los del mapa han recibido el packet " + infos!![1])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento incorrecto")
            }
            "RECIBIRNOS" -> try {
                infos = mensaje.split(" ", 3)
                objetivo = Mundo.getPersonajePorNombre(infos!![1])
                GestorSalida.enviar(objetivo, infos[2], false)
                GestorSalida.enviar(_perso, infos[2], false)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La core de " + infos[1] + " y tu, han recibido el packet "
                        + infos[2])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento incorrecto")
            }
            "ADD_ACCION_PELEA", "ADD_ACTION_FIGHT", "AGREGAR_ACCION_PELEA" -> try {
                val pelea: Pelea = _perso.getPelea()
                if (pelea == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "No te encuentras en una pelea")
                    return
                }
                infos = mensaje.split(" ", 3)
                try {
                    accionID = Integer.parseInt(infos!![1])
                    args = infos[2]
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento inválido")
                    return
                }
                pelea.addAccion(Accion(accionID, args, ""))
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La pelea agrego la accion: ID $accionID, Args $args")
            } catch (e: Exception) {
            }
            "STR_ACCIONES_PELEA" -> {
                if (_perso.getPelea() == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "No te encuentras en una pelea")
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La acciones de esta pelea son: " + _perso.getPelea()
                        .getStrAcciones())
            }
            "REPETIR_MISION" -> try {
                idInt = Integer.parseInt(infos!![1])
                val mision: MisionModelo = Mundo.getMision(idInt)
                mision.setPuedeRepetirse(!mision.getPuedeRepetirse())
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La mision repetir: " + if (mision.getPuedeRepetirse()) "Si" else "No")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento inválido")
                return
            }
            "GET_PERSONAJES" -> GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La cantidad de personajes en MundoDofus es de " + Mundo
                    .getCantidadPersonajes())
            "REGALAR_CREDITOS", "AGREGAR_CREDITOS", "DAR_CREDITOS", "ADD_CREDITS" -> {
                try {
                    idInt = Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                }
                objetivo = _perso
                if (infos!!.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                GestorSQL.SET_CREDITOS_CUENTA(GestorSQL.GET_CREDITOS_CUENTA(objetivo.getCuentaID()) + idInt, objetivo
                        .getCuentaID())
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, idInt.toString() + " creditos ont été ajoutés à " + objetivo.getNombre())
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se le ha agregado $idInt creditos a " + objetivo
                            .getNombre())
                }
            }
            "REGALAR_OGRINAS", "AGREGAR_OGRINAS", "DAR_OGRINAS", "ADD_POINTS" -> {
                try {
                    idCant = Long.parseLong(infos!![1])
                } catch (e: Exception) {
                }
                objetivo = _perso
                if (infos!!.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                GestorSQL.ADD_OGRINAS_CUENTA(idCant, objetivo.getCuentaID(), objetivo)
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, idInt.toString() + " ogrines ont été ajoutés à " + objetivo.getNombre())
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se le ha agregado " + idCant + " ogrinas a " + objetivo.getNombre())
                }
            }
            "DAR_OGRINAS_CUENTA", "ADD_POINTS_ACCOUNT" -> {
                try {
                    idCant = Long.parseLong(infos!![1])
                } catch (e: Exception) {
                }
                cuenta = _cuenta
                if (infos!!.size > 2) {
                    cuenta = Mundo.getCuentaPorNombre(infos[2])
                }
                if (cuenta == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le compte pas exist.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La cuenta no existe")
                    }
                    return
                }
                GestorSQL.ADD_OGRINAS_CUENTA(idCant, cuenta.getID(), objetivo)
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, idCant.toString() + " ogrines ont été ajoutés à " + cuenta.getNombre())
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se le ha agregado " + idCant + " ogrinas a " + cuenta.getNombre())
                }
            }
            "ABONO_MINUTES", "ABONO_MINUTOS", "DAR_ABONO_MINUTOS", "ADD_ABONO_MINUTOS", "ADD_ABONO_MINUTES" -> {
                try {
                    idInt = Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                }
                objetivo = _perso
                if (infos!!.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                var abonoM: Long = Math.max(GestorSQL.GET_ABONO(objetivo.getCuenta().getNombre()), System.currentTimeMillis())
                abonoM += idInt * 60 * 1000L
                abonoM = Math.max(abonoM, System.currentTimeMillis() - 1000)
                GestorSQL.SET_ABONO(abonoM, objetivo.getCuentaID())
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, idInt.toString() + " minutes abonne ont été ajoutés à " + objetivo.getNombre())
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se le ha agregado $idInt minutos de abono a " + objetivo
                            .getNombre())
                }
            }
            "ABONO_HOURS", "ABONO_HORAS", "DAR_ABONO_HORAS", "ADD_ABONO_HORAS", "ADD_ABONO_HOURS" -> {
                try {
                    idInt = Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                }
                objetivo = _perso
                if (infos!!.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                var abonoH: Long = Math.max(GestorSQL.GET_ABONO(objetivo.getCuenta().getNombre()), System.currentTimeMillis())
                abonoH += idInt * 3600 * 1000L
                abonoH = Math.max(abonoH, System.currentTimeMillis() - 1000)
                GestorSQL.SET_ABONO(abonoH, objetivo.getCuentaID())
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, idInt.toString() + " heures abonne ont été ajoutés à " + objetivo.getNombre())
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se le ha agregado $idInt horas de abono a " + objetivo
                            .getNombre())
                }
            }
            "ABONO_DAYS", "ABONO_DIAS", "DAR_ABONO_DIAS", "ADD_ABONO_DIAS", "ADD_ABONO_DAYS" -> {
                try {
                    idInt = Integer.parseInt(infos!![1])
                } catch (e: Exception) {
                }
                objetivo = _perso
                if (infos!!.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                }
                if (objetivo == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                var abonoD: Long = Math.max(GestorSQL.GET_ABONO(objetivo.getCuenta().getNombre()), System.currentTimeMillis())
                abonoD += idInt * 24 * 3600 * 1000L
                abonoD = Math.max(abonoD, System.currentTimeMillis() - 1000)
                GestorSQL.SET_ABONO(abonoD, objetivo.getCuentaID())
                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, idInt.toString() + " jouers abonne ont été ajoutés à " + objetivo.getNombre())
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se le ha agregado $idInt dias de abono a " + objetivo
                            .getNombre())
                }
            }
            "RESETEAR_STATS_OBJETOS_MODELO" -> try {
                val idsObjetos: ArrayList<Integer?> = ArrayList()
                for (s in infos!![1].split(";")) {
                    if (s.isEmpty()) {
                        continue
                    }
                    idsObjetos.add(Integer.parseInt(s))
                }
                Mundo.resetearStatsObjetos(idsObjetos)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se reseteo los objetos modelo IDs: " + infos[1])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
                return
            }
            "LISTA_DIR_CELDAS" -> try {
                celda1 = Short.parseShort(infos!![1])
                celda2 = Short.parseShort(infos!![2])
                val b: ByteArray = Camino.listaDirEntreDosCeldas2(_perso.getMapa(), celda1, celda2, -1.toShort())
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "listaDirEntreDosCeldas2 " + b[0] + "," + b[1] + "," + b[2] + ","
                        + b[3])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
                return
            }
            "HORARIO_DIA" -> try {
                val dia: Array<String?> = infos!![1].split(":")
                try {
                    val h: Int = Integer.parseInt(dia[0])
                    if (h >= 0 && h <= 23) {
                        MainServidor.HORA_DIA = h
                    }
                } catch (e: Exception) {
                }
                try {
                    val h: Int = Integer.parseInt(dia[1])
                    if (h >= 0 && h <= 59) {
                        MainServidor.MINUTOS_DIA = h
                    }
                } catch (e: Exception) {
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se cambio el HORARIO_DIA a " + MainServidor.HORA_DIA.toString() + ":" + (if (MainServidor.MINUTOS_DIA < 10) "0" else "") + MainServidor.MINUTOS_DIA)
                for (p in Mundo.getPersonajesEnLinea()) {
                    if (p.esDeDia()) {
                        GestorSalida.ENVIAR_BT_TIEMPO_SERVER(p)
                    }
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
                return
            }
            "HORARIO_NOCHE" -> try {
                val dia: Array<String?> = infos!![1].split(":")
                try {
                    val h: Int = Integer.parseInt(dia[0])
                    if (h >= 0 && h <= 23) {
                        MainServidor.HORA_NOCHE = h
                    }
                } catch (e: Exception) {
                }
                try {
                    val h: Int = Integer.parseInt(dia[1])
                    if (h >= 0 && h <= 59) {
                        MainServidor.MINUTOS_NOCHE = h
                    }
                } catch (e: Exception) {
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se cambio el HORARIO_NOCHE a " + MainServidor.HORA_NOCHE.toString() + ":" + (if (MainServidor.MINUTOS_NOCHE < 10) "0" else "") + MainServidor.MINUTOS_NOCHE)
                for (p in Mundo.getPersonajesEnLinea()) {
                    if (p.esDeNoche()) {
                        GestorSalida.ENVIAR_BT_TIEMPO_SERVER(p)
                    }
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
                return
            }
            "SIMULACION_GE", "SIMULATION_GE" -> try {
                var mapaID: Short = 0
                var celdaID: Short = 0
                try {
                    if (infos!!.size > 1) {
                        mapaID = Short.parseShort(infos[1])
                    }
                    if (infos.size > 2) {
                        celdaID = Short.parseShort(infos[2])
                    }
                } catch (e: Exception) {
                }
                val mapa: Mapa = Mundo.getMapa(mapaID)
                if (mapa == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "MAPID INVALIDE!")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El mapa a teleportar no existe")
                    }
                    return
                }
                val ge = infos!![3]
                if (celdaID <= -1) {
                    celdaID = mapa.getRandomCeldaIDLibre()
                } else if (mapa.getCelda(celdaID) == null) {
                    if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "CELLID INVALIDE!")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "CeldaID inválida")
                    }
                    return
                }
                for (p in Mundo.getPersonajesEnLinea()) {
                    p.setMapa(mapa)
                    p.setCelda(mapa.getCelda(celdaID))
                    GestorSalida.enviar(p, ge, true)
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
                return
            }
            else -> {
                GM_lvl_4(comando, infos, mensaje, _cuenta, _perso)
                return
            }
        }
    }

    fun GM_lvl_0(comando: String?, infos: Array<String?>?, mensaje: String?, _cuenta: Cuenta?,
                 _perso: Personaje?) {
    }
}