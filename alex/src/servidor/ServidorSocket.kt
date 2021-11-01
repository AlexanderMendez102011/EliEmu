package servidor

import java.awt.event.ActionEvent

class ServidorSocket(socket: Socket?) : Runnable {
    //
    // dinamicos
    //
    private var _in: BufferedInputStream? = null
    private var _out: PrintWriter? = null
    private var _socket: Socket? = null
    private var _thread: Thread? = null
    private var _cuenta: Cuenta? = null
    private var _perso: Personaje? = null
    private var _timerAcceso: Timer? = null
    var actualIP: String? = null
    private var _ultimoPacket: String? = null

    // private boolean _votarDespuesPelea = false;
    private var _accionesDeJuego: Map<Integer?, AccionDeJuego?>? = null
    private var _tiempoUltComercio: Long = 0
    private var _tiempoUltReclutamiento: Long = 0
    private var _tiempoUltAlineacion: Long = 0
    private var _tiempoUltIncarnam: Long = 0
    private var _tiempoUltVIP: Long = 0
    private var _ultSalvada: Long = 0
    var tiempoUltPacket: Long = 0
        private set
    private val _tiempoLLegoMapa: Long = 0
    private var _tiempoUltAll: Long = 0
    val ping: Long = 0
    private var _excesoPackets: Byte = 0

    /*
	 * private void juego_Cargando_Informacion_Mapa() { try { _tiempoLLegoMapa =
	 * System.currentTimeMillis(); limpiarAcciones(true);
	 * Thread.sleep(MainServidor.TIME_SLEEP_PACKETS_CARGAR_MAPA); if
	 * (_iniciandoPerso) { creandoJuego();
	 * Thread.sleep(MainServidor.TIME_SLEEP_PACKETS_CARGAR_MAPA); } boolean
	 * cargandoMapa = false; _iniciandoPerso = false; if (_perso.getPrePelea() !=
	 * null && _perso.getPrePelea().getFase() == Constantes.PELEA_FASE_POSICION) {
	 * _perso.getPrePelea().unirsePelea(_perso, _perso.getUnirsePrePeleaAlID()); }
	 * else if (_perso.getPelea() != null && _perso.getPelea().getFase() !=
	 * Constantes.PELEA_FASE_FINALIZADO) {
	 * _perso.getPelea().reconectarLuchador(_perso); } else { Mapa mapa =
	 * _perso.getMapa(); StringBuilder packet = new StringBuilder("");
	 * packet.append("GDK").append((char) 0x00); String pj1 =
	 * mapa.getGMsPersonajes(_perso); if (!pj1.equals("")) packet.append(pj1 +
	 * (char) 0x00); String pj2 = mapa.getGMsGrupoMobs(_perso); if (!pj2.equals(""))
	 * packet.append(pj2 + (char) 0x00); String pj3 = mapa.getGMsNPCs(_perso); if
	 * (!pj3.equals("")) packet.append(pj3 + (char) 0x00); String pj4 =
	 * mapa.getGMsMercantes(); if (!pj4.equals("")) packet.append(pj4 + (char)
	 * 0x00);// WTFFF BRO??? esta loco el que hizo esto--- la madre de dios, u // //
	 * // programador bueno como vea esto lo tira a la basura String pj5 =
	 * mapa.getGMsMonturas(_perso); if (!pj5.equals("")) packet.append(pj5 + (char)
	 * 0x00); String pj6 = mapa.getGMPrisma(); if (!pj6.equals(""))
	 * packet.append(pj6 + (char) 0x00); String pj7 = mapa.getGMRecaudador(); if
	 * (!pj7.equals("")) packet.append(pj7 + (char) 0x00); String pj8 =
	 * mapa.getObjetosCria(); if (!pj8.equals("")) packet.append(pj8 + (char) 0x00);
	 * String pj9 = mapa.getObjetosInteracGDF(); if (!pj9.equals(""))
	 * packet.append(pj9 + (char) 0x00); Cercado cercado = mapa.getCercado(); if
	 * (cercado != null) { packet.append(cercado.informacionCercado() + (char)
	 * 0x00); } String pj10 = "fC" + mapa.getNumeroPeleas(); packet.append(pj10 +
	 * (char) 0x00); GestorSalida.enviar(_perso, packet.toString(), true);
	 * cargandoMapa = true; } _perso.setCargandoMapa(false); if (cargandoMapa) {
	 * Mundo.cargarPropiedadesCasa(_perso);
	 * _perso.getMapa().agregarEspadaPelea(_perso);
	 * _perso.getMapa().objetosTirados(_perso); // nada de abajo es grafico
	 * _perso.packetModoInvitarTaller(_perso.getOficioActual(), false); //
	 * GestorSalida.ENVIAR_fC_CANTIDAD_DE_PELEAS(_perso, _perso.getMapa()); if
	 * (_votarDespuesPelea) { if (_perso.getPelea() == null) {
	 * GestorSalida.ENVIAR_bP_VOTO_RPG_PARADIZE(_perso,
	 * _cuenta.tiempoRestanteParaVotar(), false); _votarDespuesPelea = false; } } if
	 * (MainServidor.MODO_PVP && MainServidor.NIVEL_MAX_ESCOGER_NIVEL >= 1 &&
	 * _perso.getRecienCreado()) { GestorSalida.ENVIAR_bA_ESCOGER_NIVEL(_perso); } }
	 * registrarUltPing(); } catch (Exception e) { e.printStackTrace();
	 * System.out.println("ERROR MAPA : " + e.getMessage());
	 * _perso.setCargandoMapa(false); MainServidor.
	 * redactarLogServidorln("EXCEPTION juego_Cargando_Informacion_Mapa " +
	 * e.toString()); // en que linea sale el error } }
	 */
    /*
           * private boolean _iniciandoPerso = true;
           *
           * private void creandoJuego() { if (!_perso.getCreandoJuego()) { try {
           * Thread.sleep(500); } catch (Exception e) { } creandoJuego(); return; } if
           * (MainServidor.PRECIO_SISTEMA_RECURSO > 0) {
           * GestorSalida.ENVIAR_ÑR_BOTON_RECURSOS(this); } if
           * (MainServidor.PARAM_BOTON_BOUTIQUE) {
           * GestorSalida.ENVIAR_Ñs_BOTON_BOUTIQUE(this); } // + (_cuenta.esAbonado() ?
           * "¡" : "") if (_perso.getPelea() == null) { _perso.mostrarTutorial(); if
           * (!MainServidor.MENSAJE_BIENVENIDA.isEmpty()) {
           * GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
           * MainServidor.MENSAJE_BIENVENIDA); } if
           * (!MainServidor.PANEL_BIENVENIDA.isEmpty()) {
           * GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(_perso,
           * MainServidor.PANEL_BIENVENIDA); } } try { for (String m :
           * _cuenta.getMensajes()) { enviarPW(m); } _cuenta.getMensajes().clear(); }
           * catch (Exception e) { } if (MainServidor.OGRINAS_POR_VOTO >= 0 ||
           * _votarDespuesPelea) { if (_perso.getPelea() == null) {
           * GestorSalida.ENVIAR_bP_VOTO_RPG_PARADIZE(_perso,
           * _cuenta.tiempoRestanteParaVotar(), false); _votarDespuesPelea = false; } }
           * _perso.setCreandoJuego(false); }
           */  var realizandoAccion = false
        private set

    //private long[] _timePackets;
    //private String[] _ultPackets;
    private val _aKeys: Array<String?>?
    private var _currentKey = -1
    private fun posibleAtaque() {
        if (POSIBLES_ATAQUES.get(actualIP) != null) {
            var veces: Int = POSIBLES_ATAQUES.get(actualIP)
            if (veces > MainServidor.VECES_PARA_BAN_IP_SIN_ESPERA) {
                GestorSQL.INSERT_BAN_IP(actualIP)
            } else {
                POSIBLES_ATAQUES.put(actualIP, veces++)
            }
        } else {
            POSIBLES_ATAQUES.put(actualIP, 0)
        }
    }

    private fun crearTimerAcceso() {
        _timerAcceso = Timer(10 * 1000, object : ActionListener() {
            fun actionPerformed(arg0: ActionEvent?) {
                MainServidor.redactarLogServidorln("TIMER ACCEDER SERVER AGOTADO (posible ataque): " + actualIP)
                posibleAtaque()
                cerrarSocket(true, "crearTimerAcceso()")
            }
        })
    }

    private fun crearPacketKey(): String? {
        if (MainServidor.PARAM_ENCRIPTAR_PACKETS) {
            _currentKey = Formulas.getRandomInt(1, 15)
            // String key = Encriptador.crearKey(16);
            // _currentKey = Formulas.getRandomInt(1, 63);
            val key: String = Encriptador.crearKey(16)
            _aKeys!![_currentKey] = Encriptador.prepareKey(key)
            return Integer.toHexString(_currentKey).toUpperCase() + key
        }
        return "0"
    }

    private var paqueteR = 0
    fun run() {
        try {
            GestorSalida.ENVIAR_HG_SALUDO_JUEGO_GENERAL(this)
            if (MainServidor.PARAM_TIMER_ACCESO) {
                crearTimerAcceso()
                _timerAcceso.start()
            }
            val contents = ByteArray(1024)
            var bytesRead = 0
            var packet: String?
            while (_in.read(contents).also { bytesRead = it } != -1) {
                packet = String(contents, 0, bytesRead - 1).replace("\r", "\n").replace("\u0000", "\n")
                for (pkt in packet.split("\n")) {
                    if (pkt!!.equals("") || pkt!!.equals(" ")) continue
                    packet = analizarWPE(pkt, true)
                    if (MainServidor.PARAM_ENCRIPTAR_PACKETS) packet = Encriptador.unprepareData(packet, _currentKey, _aKeys)
                    if (MainServidor.MOSTRAR_RECIBIDOS) System.out.println("<<RECIBIR PERSONAJE:  $packet")
                    rastrear(packet)
                    registrar("===>> $packet")
                    val packetf = packet
                    Thread { analizar_Packets(packetf, true) }.start()
                }
            }
        } catch (e: Exception) {
            // registrar("<===> Exception " + e.toString());
            // e.printStackTrace();
        } finally {
            cerrarSocket(true, "ServidorSocket.run()")
        }
    }

    private fun analizarWPE(packet: String?, original: Boolean): String? {
        if (_perso != null) {
            if (!packet!!.equals("Af") && !packet.equals("GC1") && original && establecido) {
                /*if (_cuenta.claveseguridad.isEmpty()) {
					paqueteR = 0;
					// cerrarSocket(true, "WPE");
					return packet;
				}*/
                // char lastc = packet.charAt(packet.length()-1); //ves esos char que varian con
                // el paqueteR cada vez suman 1 y bajan a 0, aleatoriamente cambias entre clave
                // cada vez que recibes paquete y se le hace imposible saber la siguiente letra
                // y si no recibes la letra correcta le xpulsa, y esa letra es imposible
                // cambiarla ya que esta incristada en el cliente y con ningun programa externo
                // se puede tocar
                if (paqueteR >= _cuenta.claveseguridad.length()) paqueteR = 0
                // char cuent = _cuenta.claveseguridad.charAt(paqueteR);
                paqueteR += 1
                // if (lastc != cuent) {//myioelcwwoazulokgyfigjorgxiyykbp
                // System.out.println("WPE Detectado de la cuenta: " + _cuenta.getNombre() + "
                // El packet es: " + packet);
                // paqueteR = 0;
                // cerrarSocket(true, "WPE");
                // return packet;
                // } else {
                solounavez = true
                // }
                return packet.substring(0, packet.length() - 1)
            }
        }
        return packet
    }

    fun enviarPWSinEncriptar(packet: String?) {
        enviarPW(packet, true, false)
    }

    fun enviarPW(packet: String?) {
        enviarPW(packet, false, true)
        if (MainServidor.MOSTRAR_ENVIOS) {
            if (_perso != null && !packet.isEmpty() && !packet!!.equals("" + 0x00.toChar())) {
                System.out.println("EnviaPW>>  $packet")
            }
        }
    }

    fun enviarPW(p: String?, redactar: Boolean, encriptado: Boolean) {
        if (Mundo.SERVIDOR_ESTADO === 0) return
        for (packet in p.split("\u0000")) {
            if (_out != null && !packet.isEmpty()) {
                if (MainServidor.PARAM_ENCRIPTAR_PACKETS && encriptado) packet = Encriptador.prepareData(packet, _currentKey, _aKeys)
                packet = Encriptador.aUTF(packet)
                _out.print(packet.toString() + "\u0000")
                _out.flush()
                if (redactar && _perso != null) _perso.registrar("<<=== $packet")
            }
        }
    }

    fun cerrarSocket(cuenta: Boolean, n: String?) {
        try {
            if (MainServidor.MODO_DEBUG) {
                System.out.println("CERRAR SOCKET $n")
            }
            ServidorServer.borrarCliente(this)
            ServidorServer.delEsperandoCuenta(_cuenta)
            try {
                if (_timerAcceso != null && _timerAcceso.isRunning()) {
                    _timerAcceso.stop()
                }
            } catch (e: Exception) {
                MainServidor.redactarLogServidorln("EXCEPTION al cerrar socket [" + actualIP + "]: timer " + e.toString())
            }
            if (_in != null) {
                _in.close()
            }
            if (_out != null) {
                _out.close()
            }
            if (_socket != null) {
                if (cuenta) {
                    registrar("<===> DESCONECTANDO CON ULTIMO PACKET $_ultimoPacket")
                    if (_cuenta != null) {
                        _cuenta.desconexion()
                    }
                }
                if (_socket != null) {
                    _socket.close()
                }
            }
            _socket = null
            _cuenta = null
            _perso = null
            _timerAcceso = null
            _accionesDeJuego = null
            if (_thread != null) {
                if (_thread.isAlive()) {
                    if (!_thread.isInterrupted()) {
                        _thread.interrupt()
                        _thread = null
                    }
                }
            }
        } catch (e: Exception) {
            MainServidor.redactarLogServidorln("EXCEPTION al cerrar servidor socket " + e.toString())
            e.printStackTrace()
        }
    }

    private fun rastrear(packet: String?) {
        try {
            if (RASTREAR_IPS.contains(actualIP) || _cuenta != null && RASTREAR_CUENTAS.contains(_cuenta.getID())) {
                if (_perso != null) {
                    MainServidor.redactarLogServidorln("[" + _perso.getNombre().toString() + "] " + packet.toString())
                } else if (_cuenta != null) {
                    MainServidor.redactarLogServidorln("<<" + _cuenta.getNombre().toString() + ">> " + packet.toString())
                } else {
                    MainServidor.redactarLogServidorln("{" + actualIP + "} " + packet.toString())
                }
            }
        } catch (e: Exception) {
        }
    }

    fun registrar(packet: String?) {
        try {
            if (_cuenta != null) {
                if (REGISTROS!![_cuenta.getNombre()] == null) {
                    REGISTROS.put(_cuenta.getNombre(), StringBuilder())
                }
                REGISTROS!![_cuenta.getNombre()].append(System.currentTimeMillis().toString() + " - "
                        + Date(System.currentTimeMillis()) + " : \t" + packet + "\n")
            }
        } catch (e: Exception) {
        }
    }

    val out: PrintWriter?
        get() = _out
    val sock: Socket?
        get() = _socket
    var personaje: Personaje?
        get() = _perso
        set(perso) {
            _perso = perso
        }
    val cuenta: Cuenta?
        get() = _cuenta
    var establecido = false
    fun analizar_Packets(packet: String?, original: Boolean) {
        if (Mundo.SERVIDOR_ESTADO === Constantes.SERVIDOR_OFFLINE) {
            return
        }
        if (packet!!.equals("<policy-file-request/>")) {
            GestorSalida.ENVIAR_XML_POLICY_FILE(this)
            return
        }
        /*
		 * if (packet.charAt(0) != 'A' && _perso == null) { establecido = true;
		 * paqueteR+= 1; GestorSalida.ENVIAR_BN_NADA(this); return; }
		 */tiempoUltPacket = System.currentTimeMillis()
        _ultimoPacket = packet
        /*if (antiFlood(packet)) {
			return;
		}*/
        val estb: Char
        try {
            estb = packet.charAt(0)
        } catch (e: Exception) {
            return
        }
        when (estb) {
            'x' -> {
                _cuenta.claveseguridad = packet.substring(1)
                establecido = true
            }
            'A' -> analizar_Cuenta(packet)
            'B' -> analizar_Basicos(packet)
            'C' -> analizar_Conquista(packet)
            'c' -> analizar_Canal(packet)
            'D' -> analizar_Dialogos(packet)
            'd' -> analizar_Documentos(packet)
            'E' -> analizar_Intercambios(packet)
            'e' -> analizar_Ambiente(packet)
            'F' -> analizar_Amigos(packet)
            'f' -> analizar_Peleas(packet)
            'G' -> analizar_Juego(packet)
            'g' -> analizar_Gremio(packet)
            'h' -> analizar_Casas(packet)
            'i' -> analizar_Enemigos(packet)
            'I' -> {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return
            }
            'J' -> analizar_Oficios(packet)
            'k' -> analizar_Koliseo(packet)
            'K' -> analizar_Claves(packet)
            'Ñ' -> try {
                GestorSalida.ENVIAR_ÑV_VOTO_RPG(this,
                        Mundo.CAPTCHAS.get(Formulas.getRandomInt(0, Mundo.CAPTCHAS.size() - 1)))
            } catch (e: Exception) {
            }
            'O' -> analizar_Objetos(packet)
            'P' -> analizar_Grupo(packet)
            'p' -> if (packet.equals("ping")) {
                GestorSalida.ENVIAR_pong(_perso)
            }
            'Q' -> analizar_Misiones(packet)
            'q' -> analizar_Qping(packet)
            'R' -> analizar_Montura(packet)
            'r' -> {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return
            }
            'S' -> analizar_Hechizos(packet)
            't' -> if (_perso != null) {
                val pel: Pelea = _perso.getPelea() ?: return
                val lxx: Luchador = pel.getLuchadorTurno()
                if (lxx != null && (pel.esperaMove || pel.esperahechizo)) {
                    if (lxx.getMob() != null || lxx.getRecaudador() != null || lxx.getPrisma() != null || lxx.esDoble()
                            || lxx.getPersonaje() != null) {
                        pel.intentos += 1
                        var luchadoresactivos = 0
                        val lucx: Luchador = _perso.getPelea().getLuchadorPorID(_perso.getID())
                        if (lucx != null) {
                            for (luc in pel.luchadoresDeEquipo(lucx.getParamEquipoAliado())) {
                                if (luc == null) continue
                                if (luc.getRecaudador() != null || luc.esDoble() || luc.getPrisma() != null) continue
                                if (luc.estaMuerto() || luc.getMob() != null || luc.estaRetirado() || luc.esInvocacion()
                                        || luc.esEstatico() || luc.getPersonaje().getCompañeros().size() > 0) continue
                                if (luc._desconectado) continue
                                luchadoresactivos += 1
                            }
                        }
                        if (pel.intentos >= luchadoresactivos) {
                            pel.intentos = 0
                            pel.esperaMove = false
                            pel.esperahechizo = false
                        }
                    }
                }
                GestorSalida.ENVIAR_BN_NADA(_perso)
            }
            'm' -> {
                var itemid = 0
                itemid = try {
                    Integer.parseInt(packet.substring(1))
                } catch (e: Exception) {
                    return
                }
                for (its in Mundo.Prestigios.entrySet()) {
                    val vali: Int = its.getKey()
                    val valores: Int = Integer.parseInt(its.getValue().split(",").get(0))
                    if (valores == itemid) {
                        if (_perso.Canjeados.contains(vali)) break
                        val puntos: Int = Integer.parseInt(its.getValue().split(",").get(1))
                        if (_perso.PuntosPrestigio < puntos) break
                        val tipo: String = its.getValue().split(",").get(2)
                        if (tipo.contains("item")) {
                            _perso.addObjIdentAInventario(Mundo.getObjetoModelo(valores).crearObjeto(1,
                                    Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM), false)
                            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "021;1~$valores")
                        } else if (tipo.contains("hechi")) {
                            _perso.fijarNivelHechizoOAprender(valores, 1, true)
                        }
                        _perso.Canjeados.add(vali)
                        break
                    }
                }
            }
            'T' -> analizar_Tutoriales(packet)
            'W' -> analizar_Areas(packet)
            'Y' -> {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                /*
				 * if (_perso == null) return; if (_perso.buffClase == -1) { int valor = 0; try
				 * { valor = Integer.parseInt(packet.substring(1)); } catch(Exception e) {
				 * return; } if (valor < 0 || valor > 3) return; String clase = ""; switch
				 * (valor) { case 1: clase = "Guerrero"; break; case 2: clase = "Sacerdote";
				 * break; case 3: clase = "Mago"; _perso.fijarNivelHechizoOAprender(3700, 1,
				 * true); break; } GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
				 * GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso,
				 * "Has elegido la clase <b>"+clase+"</b> y has ganado un poder abrumador!!!",
				 * Colores.VERDE); _perso.buffClase = valor;
				 * GestorSalida.enviar(_perso,"At"+valor,true); _perso.refrescarEnMapa(); }
				 */return
            }
            'z' -> analizar_Zonas(packet)
            'Z', 'X' -> analizar_Bustofus(packet)
            'w' -> analizar_Palmad(packet)
            '|' -> {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return  // nada pero es el q ocsiona las aparecidas
            }
            else -> return
        }
    }

    private fun analizar_Qping(packet: String?) {
        if (packet!!.equals("qping")) {
            // if (_perso.getPelea() != null) {
            // _perso.getPelea().tiempoParaPasarTurno();
            // }
            GestorSalida.ENVIAR_BN_NADA(_perso, "QPING")
        }
    }

    /*private boolean antiFlood(final String packet) {
		try {
			if (!MainServidor.PARAM_ANTIFLOOD) {
				return false;
			}
			if (packet.equalsIgnoreCase("GT") || packet.equals("BD") || packet.equals("qping") || packet.equals("ping")
					|| packet.equals("EMR1")) {
				return false;
			} else if (packet.length() >= 2 && (packet.substring(0, 2).equalsIgnoreCase("OU")
					|| packet.substring(0, 2).equals("EB") || packet.substring(0, 2).equals("BA")
					|| packet.substring(0, 2).equals("Zz") || packet.substring(0, 2).equals("GP"))) {
				return false;
			} else if (packet.length() >= 3 && (packet.substring(0, 3).equalsIgnoreCase("EMO"))) {
				return false;
			} else if (packet.length() >= 5 && (packet.substring(0, 5).equals("GA300"))) {
				return false;
			} else {
				_ultPackets[_sigPacket] = packet;
				_timePackets[_sigPacket] = System.currentTimeMillis();
				_sigPacket += 1;
				if (_sigPacket >= 7) {
					_sigPacket = 0;
				}
				if (_ultPackets[0].equals(_ultPackets[1]) && System.currentTimeMillis()
						- _timePackets[_sigPacket] < MainServidor.MILISEGUNDOS_ANTI_FLOOD) {
					if (_ultPackets[1].equals(_ultPackets[2])) {
						if (_ultPackets[2].equals(_ultPackets[3])) {
							if (_ultPackets[3].equals(_ultPackets[4])) {
								if (_ultPackets[4].equals(_ultPackets[5])) {
									if (_ultPackets[5].equals(_ultPackets[6])) {
										if (_ultPackets[6].equals(_ultPackets[0])) {
											registrar("<===> " + "EXPULSADOR POR ANTI-FLOOD PACKET " + packet);
											GestorSalida.ENVIAR_M0_MENSAJE_BASICOS_SVR_MUESTRA_DISCONNECT(this, "45",
													"DISCONNECT FOR FLOOD", "");
											cerrarSocket(true, "antiFlood");
											return true;
										}
									}
								} else {
									GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1ADVERTENCIA_FLOOD");
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			MainServidor.redactarLogServidorln("EXCEPTION Packet " + packet + ", antiFlood " + e.toString());
			e.printStackTrace();
		}
		return false;
	}*/
    private fun analizar_Cuenta(packet: String?) {
        val estb: Char
        try {
            estb = packet.charAt(1)
        } catch (e: Exception) {
            return
        }
        try {
            when (estb) {
                'A' -> {
                    if (_perso != null) {
                        return
                    }
                    cuenta_Crear_Personaje(packet)
                }
                'B' -> {
                    if (_perso == null) {
                        return
                    }
                    cuenta_Boostear_Stat(packet)
                }
                'D' -> {
                    if (_perso != null) {
                        return
                    }
                    cuenta_Eliminar_Personaje(packet)
                }
                'f' -> if (_excesoPackets > 10) {
                    cerrarSocket(true, "analizarCuenta(1)")
                } else {
                    _excesoPackets++
                }
                'g' -> cuenta_Idioma(packet)
                'G' -> {
                    if (_perso != null) {
                        return
                    }
                    cuenta_Entregar_Regalo(packet.substring(2))
                }
                'i' -> if (_perso != null) {
                    return
                }
                'k' -> {
                }
                'L' -> {
                    if (_perso != null) {
                        return
                    }
                    // LAS Ñs y sus variables globales
                    // GestorSalida.ENVIAR_Ñm_MENSAJE_NOMBRE_SERVER(this);;
                    GestorSalida.ENVIAR_ÑG_CLASES_PERMITIDAS(this)
                    GestorSalida.ENVIAR_ÑO_ID_OBJETO_MODELO_MAX(this)
                    GestorSalida.ENVIAR_Ña_AUTO_PASAR_TURNO(this)
                    GestorSalida.ENVIAR_Ñe_EXO_PANEL_ITEMS(this)
                    GestorSalida.ENVIAR_Ñr_SUFJIO_RESET(this)
                    GestorSalida.ENVIAR_ÑD_DAÑO_PERMANENTE(this)
                    // el % de daño incurable
                    GestorSalida.ENVIAR_ÑI_CREA_TU_ITEM_OBJETOS(this)
                    GestorSalida.ENVIAR_Ñp_RANGO_NIVEL_PVP(this)
                    GestorSalida.ENVIAR_ÑZ_COLOR_CHAT(this)
                    GestorSalida.ENVIAR_ÑV_ACTUALIZAR_URL_LINK_MP3(this)
                    GestorSalida.ENVIAR_bo_RESTRINGIR_COLOR_DIA(this)
                    if (!MainServidor.URL_IMAGEN_VOTO.isEmpty()) {
                        GestorSalida.ENVIAR_ÑU_URL_IMAGEN_VOTO(this)
                    }
                    if (!MainServidor.URL_LINK_VOTO.isEmpty()) {
                        GestorSalida.ENVIAR_Ñu_URL_LINK_VOTO(this)
                    }
                    if (!MainServidor.URL_LINK_BUG.isEmpty()) {
                        GestorSalida.ENVIAR_Ñx_URL_LINK_BUG(this)
                    }
                    if (!MainServidor.URL_LINK_COMPRA.isEmpty()) {
                        GestorSalida.ENVIAR_Ñz_URL_LINK_COMPRA(this)
                    }
                    GestorSalida.ENVIAR_ALK_LISTA_DE_PERSONAJES(this, _cuenta)
                }
                'P' -> {
                    if (_perso != null) {
                        return
                    }
                    GestorSalida.ENVIAR_APK_NOMBRE_PJ_ALEATORIO(this, Encriptador.palabraAleatorio(5))
                }
                'p' -> {
                    val ptc: Char = packet.charAt(2)
                    when (ptc) {
                        'c' -> {
                            _perso.resetearStats()
                            val mires = 1
                            val captotal: Int = _perso.getNivel() * 5
                            _perso._puntosStats = captotal + mires
                            GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso)
                        }
                        's' -> {
                            val id: Int = Integer.parseInt(packet.substring(3))
                            if (_perso.nerfearHechizo(id)) {
                                GestorSalida.ENVIAR_SUK_SUBIR_NIVEL_HECHIZO(_perso, id, _perso.getStatsHechizo(id).getGrado())
                                GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso)
                            } else {
                                GestorSalida.ENVIAR_SUE_SUBIR_NIVEL_HECHIZO_ERROR(_perso)
                                return
                            }
                        }
                    }
                }
                'R' -> {
                    if (_perso != null || !MainServidor.MODO_HEROICO && MainServidor.MAPAS_MODO_HEROICO.isEmpty()) {
                        return
                    }
                    cuenta_Reiniciar_Personaje(packet)
                }
                'S' -> {
                    if (_perso != null) {
                        return
                    }
                    cuenta_Seleccion_Personaje(packet)
                }
                'T' -> {
                    if (_perso != null) {
                        return
                    }
                    cuenta_Acceder_Server(packet)
                }
                'V' -> {
                    if (_perso != null) {
                        return
                    }
                    GestorSalida.ENVIAR_AV_VERSION_REGIONAL(this)
                }
                else -> return
            }
        } catch (e: Exception) {
            MainServidor.redactarLogServidorln(
                    "EXCEPTION Packet " + packet + ", analizar cuenta " + e.toString() + ", packet " + packet)
            e.printStackTrace()
        }
    }

    /*
	 * private String getStringDesconocido() { _excesoPackets++; if (_excesoPackets
	 * >= MainServidor.MAX_PACKETS_PARA_RASTREAR) { if (!RASTREAR_IPS.contains(_IP))
	 * { RASTREAR_IPS.add(_IP); } if (_cuenta != null) { if
	 * (!RASTREAR_CUENTAS.contains(_cuenta.getID())) {
	 * RASTREAR_CUENTAS.add(_cuenta.getID()); } } } return
	 * "PACKET DESCONOCIDO Cuenta: " + (_cuenta == null ? " null " :
	 * (_cuenta.getNombre() + "(" + _cuenta.getID() + ") Perso: " + (_perso == null
	 * ? " null " : (_perso.getNombre() + "(" + _perso.getID() + ")")))); }
	 */
    private fun cuenta_Acceder_Server(packet: String?) {
        try {
            for (i in 0..2) {
                _cuenta = ServidorServer.getEsperandoCuenta(Integer.parseInt(packet.substring(2)))
                if (_cuenta != null) {
                    try {
                        if (_timerAcceso != null && _timerAcceso.isRunning()) {
                            _timerAcceso.stop()
                        }
                        _timerAcceso = null
                    } catch (e: Exception) {
                    }
                    ServidorServer.delEsperandoCuenta(_cuenta)
                    if (_cuenta.getAdmin() < MainServidor.ACCESO_ADMIN_MINIMO) {
                        GestorSalida.ENVIAR_M0_MENSAJE_BASICOS_SVR_MUESTRA_DISCONNECT(this, "19", "", "")
                        cerrarSocket(true, "cuenta_Acceder_Server(1)")
                        return
                    }
                    if (GestorSQL.ES_IP_BANEADA(actualIP)) {
                        GestorSalida.ENVIAR_AlEb_CUENTA_BANEADA_DEFINITIVO(this)
                        cerrarSocket(true, "cuenta_Acceder_Server(2)")
                        return
                    }
                    val tiempoBaneo: Long = GestorSQL.GET_BANEADO(_cuenta.getNombre())
                    if (tiempoBaneo != 0L) {
                        if (tiempoBaneo <= -1) {
                            GestorSalida.ENVIAR_AlEb_CUENTA_BANEADA_DEFINITIVO(this)
                            cerrarSocket(true, "cuenta_Acceder_Server(3)")
                            return
                        } else if (tiempoBaneo > System.currentTimeMillis()) {
                            GestorSalida.ENVIAR_AlEk_CUENTA_BANEADA_TIEMPO(this, tiempoBaneo)
                            cerrarSocket(true, "cuenta_Acceder_Server(4)")
                            return
                        } else {
                            GestorSQL.SET_BANEADO(_cuenta.getNombre(), 0)
                        }
                    }
                    _cuenta.setEntradaPersonaje(this)
                    _cuenta.setActualIP(actualIP)
                    GestorSalida.ENVIAR_ATK_TICKET_A_CUENTA(this, crearPacketKey())
                    if (MainServidor.MODO_HEROICO) {
                        GestorSalida.ENVIAR_ÑS_SERVER_HEROICO(this)
                    }
                    for (perso in _cuenta.getPersonajes()) {
                        if (perso.getPelea() == null) {
                            continue
                        }
                        _perso = perso
                        _perso.conectarse()
                        return
                    }
                    return
                }
                Thread.sleep(1000)
            }
        } catch (e: Exception) {
            MainServidor
                    .redactarLogServidorln("EXCEPTION Packet: $packet SE INTENTA ACCEDER CON UNA CUENTA RARA")
            e.printStackTrace()
        }
        GestorSalida.ENVIAR_ATE_TICKET_FALLIDA(this)
        cerrarSocket(true, "cuenta_Acceder_Server(5) _cuenta: $_cuenta packet: $packet")
    }

    private fun cuenta_Idioma(packet: String?) {
        GestorSalida.ENVIAR_ÑA_LISTA_GFX(this)
        GestorSalida.ENVIAR_ÑB_LISTA_NIVEL(this)
        _cuenta.setIdioma(packet.substring(2))
        if (_perso == null) {
            cuenta_Regalo()
        }
    }

    private fun cuenta_Regalo(): Boolean {
        val regalo: String = _cuenta.getRegalo()
        if (regalo.isEmpty()) {
            return false
        }
        // if (MainServidor.HORAS_MINIMO_RECLAMAR_PREMIO > 0) {
        // LoginPremio premios = new LoginPremio(_cuenta.getLoginPremio().split(","));
        // regalo = regalo + premios.premiosFaltantes();
        // }
        val lista = StringBuilder()
        for (str in regalo.split(",")) {
            try {
                val efectos: String = Mundo.getObjetoModelo(Integer.parseInt(str)).stringStatsModelo()
                if (lista.length() > 0) {
                    lista.append(";")
                }
                lista.append("0~" + Integer.toString(Integer.parseInt(str), 16).toString() + "~1~~" + efectos)
            } catch (e: Exception) {
            }
        }
        if (lista.length() === 0) {
            return false
        }
        GestorSalida.ENVIAR_Ag_LISTA_REGALOS(this, 1, lista.toString())
        return true
    }

    private fun cuenta_Entregar_Regalo(packet: String?) {
        try {
            val regalo: String = _cuenta.getRegalo()
            if (regalo.isEmpty()) {
                GestorSalida.ENVIAR_BN_NADA(this)
                return
            }
            val info: Array<String?> = packet.split(Pattern.quote("|"))
            val idPerso: Int = Integer.parseInt(info[1])
            val idObjMod: Int = Integer.parseInt(info[0])
            val nuevo = StringBuilder()
            var listo = false
            for (str in regalo.split(",")) {
                if (str.isEmpty()) {
                    continue
                }
                var idTemp = 0
                idTemp = try {
                    Integer.parseInt(str)
                } catch (e: Exception) {
                    continue
                }
                if (Mundo.getObjetoModelo(idTemp) == null) {
                    continue
                }
                if (listo || idTemp != idObjMod) {
                    if (nuevo.length() > 0) {
                        nuevo.append(",")
                    }
                    nuevo.append(str)
                } else {
                    listo = true
                }
            }
            _cuenta.setRegalo(nuevo.toString())
            if (listo) {
                Mundo.getPersonaje(idPerso).addObjIdentAInventario(Mundo.getObjetoModelo(idObjMod).crearObjeto(1,
                        Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.MAXIMO), false)
            } else {
                GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(this, "NO CUMPLES LOS REQUISITOS")
            }
            cuenta_Regalo()
            GestorSalida.ENVIAR_AG_SIGUIENTE_REGALO(this)
        } catch (e: Exception) {
            GestorSalida.ENVIAR_BN_NADA(this)
        }
    }

    private fun cuenta_Boostear_Stat(packet: String?) {
        var stat = 0
        var capital = 1
        stat = try {
            Integer.parseInt(packet.substring(2).split(";").get(0))
        } catch (e: Exception) {
            GestorSalida.ENVIAR_BN_NADA(this, "BOOSTEAR INVALIDO")
            return
        }
        try {
            capital = Integer.parseInt(packet.split(";").get(1))
        } catch (e: Exception) {
        }
        _perso.boostStat2(stat, capital)
    }

    private fun cuenta_Reiniciar_Personaje(packet: String?) {
        try {
            val p: Personaje = _cuenta.getPersonaje(Integer.parseInt(packet.substring(2)))
            if (p.esFantasma()) {
                p.reiniciarCero()
            }
        } catch (e: Exception) {
        }
    }

    private fun cuenta_Seleccion_Personaje(packet: String?) {
        val idPerso: Int = Integer.parseInt(packet.substring(2))
        if (_cuenta.getPersonaje(idPerso) != null) {
            _cuenta.setEntradaPersonaje(_cuenta.getSocket())
            set_perso(_cuenta.getPersonaje(idPerso))
            _perso = _cuenta.getSocket().getPersonaje()
            _perso.primerRefresh = true
            if (_perso != null) {
                _perso.conectarse()
                return
            }
        }
        GestorSalida.ENVIAR_ASE_SELECCION_PERSONAJE_FALLIDA(this)
    }

    private fun set_perso(perso: Personaje?) {
        _perso = perso
    }

    private fun cuenta_Eliminar_Personaje(packet: String?) {
        try {
            val split: Array<String?> = packet.substring(2).split(Pattern.quote("|"))
            val perso: Personaje = _cuenta.getPersonaje(Integer.parseInt(split[0]))
            var respuesta = ""
            try {
                respuesta = URLDecoder.decode(split[1], "UTF-8")
            } catch (e: Exception) {
            }
            if (perso != null) {
                if (perso.getNivel() < 25
                        || perso.getNivel() >= 25 && respuesta.equalsIgnoreCase(_cuenta.getRespuesta())) {
                    _cuenta.eliminarPersonaje(perso.getID())
                    GestorSalida.ENVIAR_ALK_LISTA_DE_PERSONAJES(this, _cuenta)
                } else {
                    GestorSalida.ENVIAR_ADE_ERROR_BORRAR_PJ(this)
                }
            } else {
                GestorSalida.ENVIAR_ADE_ERROR_BORRAR_PJ(this)
            }
        } catch (e: Exception) {
            GestorSalida.ENVIAR_ADE_ERROR_BORRAR_PJ(this)
        }
    }

    private fun cuenta_Crear_Personaje(packet: String?) {
        try {
            val infos: Array<String?> = packet.substring(2).split(Pattern.quote("|"))
            if (Mundo.getPersonajePorNombre(infos[0]) != null) {
                GestorSalida.ENVIAR_AAE_ERROR_CREAR_PJ(this, "a")
                return
            }
            if (_cuenta.getPersonajes().size() >= MainServidor.MAX_PJS_POR_CUENTA) {
                GestorSalida.ENVIAR_AAE_ERROR_CREAR_PJ(this, "f")
                return
            }
            val nombre: String = Personaje.nombreValido(infos[0], false)
            if (nombre == null) {
                GestorSalida.ENVIAR_AAE_ERROR_CREAR_PJ(_perso, "a")
                return
            }
            if (nombre.isEmpty()) {
                GestorSalida.ENVIAR_AAE_ERROR_CREAR_PJ(_perso, "n")
                return
            }
            val claseID: Byte = Byte.parseByte(infos[1])
            val sexo: Byte = Byte.parseByte(infos[2])
            val color1: Int = Integer.parseInt(infos[3])
            val color2: Int = Integer.parseInt(infos[4])
            val color3: Int = Integer.parseInt(infos[5])
            var gfx = 0
            if (infos.size > 6) {
                gfx = Integer.parseInt(infos[6])
            }
            if (Mundo.getClase(claseID) == null) {
                GestorSalida.ENVIAR_AAE_ERROR_CREAR_PJ(this, "ZCLASE NO EXISTE")
                return
            }
            if (MainServidor.OGRINAS_CREAR_CLASE.containsKey(claseID)) {
                val ogrinas: Long = MainServidor.OGRINAS_CREAR_CLASE.get(claseID)
                if (!GestorSQL.RESTAR_OGRINAS1(_cuenta, ogrinas, null)) {
                    GestorSalida.ENVIAR_AAE_ERROR_CREAR_PJ(this, "Z")
                    GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(this,
                            MainServidor.MENSAJE_ERROR_OGRINAS_CREAR_CLASE.toString() + " " + ogrinas)
                    return
                }
            }
            if (MainServidor.ADMIN_CLASE.containsKey(claseID)) {
                val rango: Int = MainServidor.ADMIN_CLASE.get(claseID)
                if (_cuenta.getAdmin() < rango) {
                    GestorSalida.ENVIAR_AAE_ERROR_CREAR_PJ(this, "Z")
                    GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(this,
                            MainServidor.MENSAJE_ERROR_ADMIN_CREAR_CLASE)
                    return
                }
            }
            val perso: Personaje = _cuenta.crearPersonaje(nombre, claseID, sexo, color1, color2, color3, gfx)
            if (perso != null) {
                // cambia la alineacion aletario
                if (MainServidor.PARAM_DAR_ALINEACION_AUTOMATICA) {
                    perso.cambiarAlineacion(if (Random().nextBoolean()) Constantes.ALINEACION_BONTARIANO else Constantes.ALINEACION_BRAKMARIANO, true)
                }
                GestorSalida.ENVIAR_AAK_CREACION_PJ_OK(this)
                GestorSalida.ENVIAR_ALK_LISTA_DE_PERSONAJES(this, _cuenta)
                if (MainServidor.PARAM_CINEMATIC_CREAR_PERSONAJE) {
                    try {
                        Thread.sleep(300L)
                        GestorSalida.ENVIAR_TB_CINEMA_INICIO_JUEGO(this)
                    } catch (exception: Exception) {
                    }
                }
                if (!MainServidor.PANEL_DESPUES_CREAR_PERSONAJE.isEmpty()) {
                    GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(this,
                            MainServidor.PANEL_DESPUES_CREAR_PERSONAJE)
                }
            } else {
                GestorSalida.ENVIAR_AAE_ERROR_CREAR_PJ(this, "Z")
            }
        } catch (e: Exception) {
            GestorSalida.ENVIAR_AAE_ERROR_CREAR_PJ(this, "Z")
        }
    }

    private fun analizar_Palmad(packet: String?) {
        try {
            when (packet.charAt(1)) {
                'a' -> if (_perso != null) {
                    val rostro: String = packet.split(";").get(1)
                    val GFX: Int = Integer.parseInt(rostro)
                    if (GFX == 0) {
                        _perso.set_rostroGFX(_perso.getClaseID(true) * 10 + _perso.getSexo())
                        _perso.refrescarEnMapa()
                        Thread.sleep(150)
                        //GestorSalida.ENVIAR_Ño_PANEL_COLOR(_perso);
                    } else {
                        _perso.set_rostroGFX(GFX)
                        _perso.refrescarEnMapa()
                        Thread.sleep(150)
                        //GestorSalida.ENVIAR_Ño_PANEL_COLOR(_perso);
                    }
                }
                'A' -> {
                    val ObjetosPosible = StringBuilder()
                    val objeto: Objeto = _perso.getObjeto(Integer.parseInt(packet.substring(2)))
                    var argumentos = ""
                    var primero = true
                    for (accion in objeto.getObjModelo().getAccion().values()) {
                        if (accion.getID() !== 96) continue
                        var k = 0
                        while (k < accion.getArgs().substring(2).split(Pattern.quote("|")).get(1)
                                        .split(";").length) {
                            argumentos = accion.getArgs().substring(2).split(Pattern.quote("|")).get(1).split(";").get(k)
                                    .split(",").get(0)
                            val objetoModelo: ObjetoModelo = Mundo.getObjetoModelo(Integer.parseInt(argumentos))
                            if (primero) {
                                primero = false
                                ObjetosPosible.append(objetoModelo.getID().toString() + ";" + objetoModelo.getStatsModelo())
                            } else {
                                ObjetosPosible.append("|" + objetoModelo.getID().toString() + ";" + objetoModelo.getStatsModelo())
                            }
                            k++
                        }
                    }
                    GestorSalida.ENVIAR_wP_PANEL_ITEMS_OBJETOS_POR_TIPO(_perso, ObjetosPosible.toString())
                }
                'b' -> if (_perso != null) {
                    palmad_rostro()
                }
                'B' -> if (MainServidor.TorneoOn) {
                    var lafase: Int = _perso.enTorneo
                    if (MainServidor.faseTorneo !== 0 || MainServidor.empezoTorneo) {
                        lafase = 2
                    }
                    if (!MainServidor.empezoTorneo) GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 123),
                            Colores.ROJO)
                    GestorSalida.enviar(_perso,
                            "bB" + lafase + ";101447^101447^" + Mundo.getObjetoModelo(101447).getStatsModelo()
                                    + ";101418^101418^" + Mundo.getObjetoModelo(101418).getStatsModelo() + ":"
                                    + listaTorneo(),
                            true)
                } else {
                    GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 124),
                            Colores.ROJO)
                    GestorSalida.enviar(_perso, "bB2;101447^101447^" + Mundo.getObjetoModelo(101447).getStatsModelo()
                            .toString() + ";101418^101418^" + Mundo.getObjetoModelo(101418).getStatsModelo().toString() + ":", true)
                }
                'c' -> if (_perso != null) {
                    GestorSalida.ENVIAR_Ño_PANEL_COLOR(_perso)
                }
                'C' -> {
                    var lista = ""
                    var first = true
                    for (Recompensa in Mundo.SUCCESS.values()) {
                        if (Recompensa != null) {
                            if (first) {
                                first = false
                                lista += (Recompensa.getId().toString() + "~" + Recompensa.getName() + "~" + Recompensa.getPoints()
                                        + "~" + Recompensa.getCategoria() + "~" + Recompensa.getArt())
                            } else {
                                lista += (";" + Recompensa.getId().toString() + "~" + Recompensa.getName().toString() + "~"
                                        + Recompensa.getPoints().toString() + "~" + Recompensa.getCategoria().toString() + "~"
                                        + Recompensa.getArt())
                            }
                        }
                    }
                    GestorSalida.enviar(_perso,
                            "bZ" + _perso.parseSucess().toString() + "|" + _perso.getPointsSucces().toString() + "|" + lista, false)
                }
                'd' -> {
                    val PacketR: String = packet.substring(2)
                    val quitar: Int = Integer.parseInt(PacketR.split(Pattern.quote("|")).get(0).split(",").get(0))
                    val infos: String = PacketR.split(Pattern.quote("|")).get(1)
                    if (_perso.getObjModeloNoEquipado(quitar, 1) != null) {
                        if (_perso.tenerYEliminarObjPorModYCant(quitar, 1)) {
                            if (quitar == 101380) {
                                if (_perso.tieneMision(559) && _perso.getEstadoMision(559) === 2) {
                                    val tempObjMod: ObjetoModelo = Mundo.getObjetoModelo(101411)
                                    _perso.addObjIdentAInventario(tempObjMod.crearObjeto(1,
                                            Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM), false)
                                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "021;1~$tempObjMod")
                                }
                            }
                            val item1 = randomItem(infos)
                            GestorSalida.enviar(_perso, "kN$item1", true)
                            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "022;1~$quitar")
                            GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso)
                        }
                    }
                    if (_perso.getObjModeloNoEquipado(quitar, 1) != null) {
                        Thread.sleep(1900L)
                        GestorSalida.enviar(_perso, "kf", true)
                    }
                }
                'D' -> {
                    var extrastS = ""
                    val idObject: Int = Integer.parseInt(packet.substring(2))
                    val InfoObjet: ObjetoModelo = Mundo.getObjetoModelo(idObject)
                    extrastS = InfoObjet.getStatsModelo().replace(",", "^")
                    GestorSalida.enviar(_perso, "wF" + InfoObjet.getID().toString() + "|" + extrastS, true)
                }
                'e' -> GestorSalida.enviar(_perso, "bw" + Mundo.listaAuras(_perso).toString() + "|" + _perso.getNombre(),
                        true)
                'E' -> try {
                    if (_perso.getPelea() != null) {
                        return
                    }
                    val a: String = packet.substring(2)
                    val AuraID: Int = Integer.parseInt(a)
                    if (AuraID == 0) {
                        _perso.setCurAura(0)
                        _perso.refrescarEnMapa()
                    } else {
                        val Aura: Aura = Mundo.getAuras(AuraID)
                        if (_perso.getAllAura(Aura.getId())) {
                            _perso.setCurAura(Aura.getId())
                            _perso.refrescarEnMapa()
                        }
                        _perso.refrescarEnMapa()
                    }
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BN_NADA(_perso)
                }
                'f' -> {
                    val mont: Montura = _perso.getMontura()
                    if (mont == null) {
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "No tienes montura Asignada")
                        return
                    }
                    if (mont.get_statsString().isEmpty()) {
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "La montura no esta fusionada")
                        return
                    } else {
                        val regresar: Int = Integer.parseInt(mont.get_statsString().split("~").get(1))
                        _perso.addObjIdentAInventario(Mundo.getObjetoModelo(regresar).crearObjeto(1,
                                Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM), false)
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "021;1~$regresar")
                        GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso)
                        mont.set_statsString("")
                        GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso)
                        GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso)
                        _perso.subirBajarMontura(false)
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Su montura se puede volver a fusionar")
                    }
                }
                'F' -> crearMaitre(_perso, true)
                'g' -> if (_perso.getGremio() != null) {
                    GestorSalida.enviar(_perso, "wg" + _perso.getGremio().getAnuncio(), true)
                }
                'G' -> {
                    var anuncio: String? = ""
                    if (_perso.getGremio() != null) {
                        anuncio = packet.substring(2)
                        _perso.getGremio().SetAnuncio(anuncio)
                        if (!_perso.getGremio().getAnuncio().isEmpty()) {
                            for (z in Mundo.getGremio(_perso.getGremio().getID()).getMiembros()) {
                                if (z == null) continue
                                if (z.enLinea()) {
                                    GestorSalida.GAME_SEND_MESSAGE(z, "<b>[Anuncio de gremio] : </b>$anuncio")
                                }
                            }
                        }
                    }
                }
                'i' -> {
                    val objetos: Array<String?> = packet.substring(2).split(Pattern.quote("|"))
                    val objeto1: Int = Integer.parseInt(objetos[0])
                    val objeto2: Int = Integer.parseInt(objetos[1])
                    val objetoBase: Objeto = _perso.getObjeto(objeto1)
                    val objetoBonus: Objeto = _perso.getObjeto(objeto2)
                    if (objetoBase == null || objetoBonus == null) {
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                "No se detectaron items en la mesa. Vuelvelos a poner.")
                        return
                    }
                    val statsTemp: String = objetoBase.getParamStatTexto(Constantes.STAT_NIVEL, 3)
                    val nivelObjeto: Int = Integer.parseInt(statsTemp, 16)
                    if (nivelObjeto != 15) {
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                "Este objeto debe ser nivel 15 para aumentar su rareza.")
                    }
                    val craf: EpicItems = Mundo.EPICITEMS.get(objetoBase.getObjModeloID())
                    if (craf != null) {
                        if (objetoBonus.getStats().tieneStatTexto(Constantes.STAT_MITICO)) {
                            val nuevo: Objeto = Mundo.getObjetoModelo(craf.getDar()).crearObjeto(1,
                                    Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.MAXIMO)
                            val statsString: String = objetoBase.convertirStatsAString(true)
                            val temp: Array<String?> = statsString.split(",")
                            val statsNew = StringBuilder()
                            var i = 0
                            while (i < temp.size) {
                                if (statsNew.length() > 0) statsNew.append(",")
                                val temp2: Array<String?> = temp[i].split("#")
                                val stats: Int = Integer.parseInt(temp2[0], 16)
                                if (Constantes.esStatTexto(Integer.parseInt(temp2[0], 16))) {
                                    if (Integer.parseInt(temp2[0], 16) === Constantes.STAT_NIVEL) {
                                        statsNew.append(temp2[0].toString() + "#0#0#" + Integer.toHexString(1))
                                    } else {
                                        statsNew.append(temp[i])
                                    }
                                } else {
                                    var valor: Int = Integer.parseInt(temp2[1], 16)
                                    var valor2: Int = Integer.parseInt(temp2[2], 16)
                                    if (!MainServidor.STATS_NO_FORTICABLES.contains(stats)) {
                                        valor += Math.ceil(valor * 8 * 0.01) as Int
                                        valor2 += Math.ceil(valor2 * 8 * 0.01) as Int
                                        statsNew.append(temp2[0].toString() + "#" + Integer.toHexString(valor) + "#"
                                                + Integer.toHexString(valor2) + "#0#0d0+" + valor)
                                    } else {
                                        statsNew.append(temp[i]) // con esto lo pone tal cual como lo tenia
                                    }
                                }
                                i++
                            }
                            nuevo.convertirStringAStats(statsNew.toString())
                            nuevo.addStatTexto(Constantes.STAT_MITICO,
                                    objetoBonus.getStats().getStatTexto(Constantes.STAT_MITICO))
                            _perso.restarCantObjOEliminar(objetoBase.getID(), 1, true)
                            _perso.restarCantObjOEliminar(objetoBonus.getID(), 1, true)
                            _perso.addObjIdentAInventario(nuevo, false)
                            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "021;" + 1 + "~" + craf.getDar())
                            GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso)
                            GestorSQL.SALVAR_OBJETO(nuevo)
                            return
                        } else if (objetoBonus.getStats().tieneStatTexto(Constantes.STAT_EPICO)) {
                            val nuevo: Objeto = Mundo.getObjetoModelo(craf.getDar()).crearObjeto(1,
                                    Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.MAXIMO)
                            val statsString: String = objetoBase.convertirStatsAString(true)
                            val temp: Array<String?> = statsString.split(",")
                            val statsNew = StringBuilder()
                            var i = 0
                            while (i < temp.size) {
                                if (statsNew.length() > 0) statsNew.append(",")
                                val temp2: Array<String?> = temp[i].split("#")
                                val stats: Int = Integer.parseInt(temp2[0], 16)
                                if (Constantes.esStatTexto(Integer.parseInt(temp2[0], 16))) {
                                    if (Integer.parseInt(temp2[0], 16) === Constantes.STAT_NIVEL) {
                                        statsNew.append(temp2[0].toString() + "#0#0#" + Integer.toHexString(1))
                                    } else {
                                        statsNew.append(temp[i])
                                    }
                                } else {
                                    var valor: Int = Integer.parseInt(temp2[1], 16)
                                    var valor2: Int = Integer.parseInt(temp2[2], 16)
                                    if (!MainServidor.STATS_NO_FORTICABLES.contains(stats)) {
                                        valor += Math.ceil(valor * 8 * 0.01) as Int
                                        valor2 += Math.ceil(valor2 * 8 * 0.01) as Int
                                        statsNew.append(temp2[0].toString() + "#" + Integer.toHexString(valor) + "#"
                                                + Integer.toHexString(valor2) + "#0#0d0+" + valor)
                                    } else {
                                        statsNew.append(temp[i]) // con esto lo pone tal cual como lo tenia
                                    }
                                }
                                i++
                            }
                            nuevo.convertirStringAStats(statsNew.toString())
                            nuevo.addStatTexto(Constantes.STAT_EPICO,
                                    objetoBonus.getStats().getStatTexto(Constantes.STAT_EPICO))
                            _perso.restarCantObjOEliminar(objetoBase.getID(), 1, true)
                            _perso.restarCantObjOEliminar(objetoBonus.getID(), 1, true)
                            _perso.addObjIdentAInventario(nuevo, false)
                            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "021;" + 1 + "~" + craf.getDar())
                            GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso)
                            GestorSQL.SALVAR_OBJETO(nuevo)
                            return
                        } else if (objetoBonus.getStats().tieneStatTexto(Constantes.STAT_LEGENDARIO)) {
                            val nuevo: Objeto = Mundo.getObjetoModelo(craf.getDar()).crearObjeto(1,
                                    Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.MAXIMO)
                            val statsString: String = objetoBase.convertirStatsAString(true)
                            val temp: Array<String?> = statsString.split(",")
                            val statsNew = StringBuilder()
                            var i = 0
                            while (i < temp.size) {
                                if (statsNew.length() > 0) statsNew.append(",")
                                val temp2: Array<String?> = temp[i].split("#")
                                val stats: Int = Integer.parseInt(temp2[0], 16)
                                if (Constantes.esStatTexto(Integer.parseInt(temp2[0], 16))) {
                                    if (Integer.parseInt(temp2[0], 16) === Constantes.STAT_NIVEL) {
                                        statsNew.append(temp2[0].toString() + "#0#0#" + Integer.toHexString(1))
                                    } else {
                                        statsNew.append(temp[i])
                                    }
                                } else {
                                    var valor: Int = Integer.parseInt(temp2[1], 16)
                                    var valor2: Int = Integer.parseInt(temp2[2], 16)
                                    if (!MainServidor.STATS_NO_FORTICABLES.contains(stats)) {
                                        valor += Math.ceil(valor * 12 * 0.01) as Int
                                        valor2 += Math.ceil(valor2 * 12 * 0.01) as Int
                                        statsNew.append(temp2[0].toString() + "#" + Integer.toHexString(valor) + "#"
                                                + Integer.toHexString(valor2) + "#0#0d0+" + valor)
                                    } else {
                                        statsNew.append(temp[i]) // con esto lo pone tal cual como lo tenia
                                    }
                                }
                                i++
                            }
                            nuevo.convertirStringAStats(statsNew.toString())
                            nuevo.addStatTexto(Constantes.STAT_LEGENDARIO,
                                    objetoBonus.getStats().getStatTexto(Constantes.STAT_LEGENDARIO))
                            _perso.restarCantObjOEliminar(objetoBase.getID(), 1, true)
                            _perso.restarCantObjOEliminar(objetoBonus.getID(), 1, true)
                            _perso.addObjIdentAInventario(nuevo, false)
                            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "021;" + 1 + "~" + craf.getDar())
                            GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso)
                            GestorSQL.SALVAR_OBJETO(nuevo)
                            return
                        } else {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "No se detecto piedra encantamiento.")
                            return
                        }
                    } else {
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Este no es un objeto que se pueda mejorar.")
                        return
                    }
                }
                'I' -> if (MainServidor.TorneoOn) {
                    if (MainServidor.empezoTorneo) {
                        GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 115),
                                Colores.ROJO)
                        return
                    }
                    if (!PjsTorneo!!.containsValue(_perso)) {
                        GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 116),
                                Colores.ROJO)
                        return
                    }
                    var laid = -1
                    for (px in PjsTorneo.entrySet()) {
                        if (px.getValue() === _perso) {
                            laid = px.getKey()
                            break
                        }
                    }
                    if (laid != -1) PjsTorneo.remove(laid)
                    var orden = 1
                    val PjsTorneoNEW: Map<Integer?, Personaje?> = TreeMap<Integer?, Personaje?>()
                    for (px in PjsTorneo!!.values()) {
                        PjsTorneoNEW.put(orden, px)
                        orden += 1
                    }
                    PjsTorneo.clear()
                    PjsTorneo.putAll(PjsTorneoNEW)
                    _perso.enTorneo = 0
                    var lafase: Int = _perso.enTorneo
                    if (MainServidor.faseTorneo !== 0) {
                        lafase = 2
                    }
                    GestorSalida.enviar(_perso,
                            "bb" + lafase + ";36025^36025^" + Mundo.getObjetoModelo(36025).getStatsModelo()
                                    + ";50071^50071^" + Mundo.getObjetoModelo(50071).getStatsModelo() + ":"
                                    + listaTorneo(),
                            true)
                } else {
                    GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, "No hay ningún Torneo programado todavía",
                            Colores.ROJO)
                    GestorSalida.enviar(_perso, "bb2;36025^36025^" + Mundo.getObjetoModelo(36025).getStatsModelo()
                            .toString() + ";50071^50071^" + Mundo.getObjetoModelo(50071).getStatsModelo().toString() + ":", true)
                }
                'h' -> crearMaitre(_perso, false)
                'H' -> {
                    if (_perso.getGrupoParty() == null) {
                        GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, "Debes tener un grupo para usar este comando",
                                "B9121B")
                        return
                    }
                    if (!_perso.estaDisponible(false, false)) {
                        if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                            GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, "El jugador esta ocupado", "B9121B")
                        }
                        return
                    }
                    if (_perso.getGrupoParty().esLiderGrupo(_perso)) {
                        if (_perso.getMapa().esMazmorra() && _perso.getMapa().getSubArea().getID() !== 658) {
                            GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, "No puedes teletansportar a Dungeons", "B9121B")
                            return
                        }
                        for (pjx in _perso.getGrupoParty().getPersos()) {
                            if (pjx === _perso) continue
                            if (!compruebaTps(pjx)) continue
                            if (_perso.getMapa().getSubArea().getID() === 658) {
                                if (pjx.getMapa().getSubArea().getID() !== 658) {
                                    GestorSalida.ENVIAR_cs_CHAT_MENSAJE(pjx, "No puedes ir hasta dimensiones", "B9121B")
                                    GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, pjx.getNombre().toString() + " no esta en la dimension", "B9121B")
                                    continue
                                }
                            }
                            pjx.teleport(_perso.getMapa().getID(), _perso.getCelda().getID())
                        }
                        GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, "Has traido a todo tu grupo con exito.", "B9121B")
                        return
                    } else {
                        GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, "no eres el lider del grupo.", "B9121B")
                        return
                    }
                }
                'O' -> if (MainServidor.TorneoOn) {
                    if (MainServidor.empezoTorneo) {
                        GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 117),
                                Colores.ROJO)
                        return
                    }
                    /*if (_perso._resets >= MainServidor.torneoR) {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 149),
								Colores.ROJO);
						return;
					}*/if (PjsTorneo!!.containsValue(_perso)) {
                        GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 118),
                                Colores.ROJO)
                        return
                    }
                    if (PjsTorneo!!.size() === 8) {
                        GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 119),
                                Colores.ROJO)
                        return
                    }
                    /*boolean multi = false;
					for (Personaje pjx : PjsTorneo.values()) {
						if (pjx.getCuenta().getActualIP().compareTo(_perso.getCuenta().getActualIP()) == 0) {
							multi = true;
							break;
						}
					}
					if (multi) {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 120),
								Colores.ROJO);
						return;
					}*/
                    val posl = posicionLibre()
                    if (posl == -1) {
                        GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 121),
                                Colores.ROJO)
                        return
                    }
                    PjsTorneo.put(posl, _perso)
                    _perso.enTorneo = 1
                    var lafase: Int = _perso.enTorneo
                    if (MainServidor.faseTorneo !== 0) {
                        lafase = 2
                    }
                    if (PjsTorneo!!.size() === 8) { //8
                        val PjsTorneoNEW: Map<Integer?, Personaje?> = TreeMap<Integer?, Personaje?>()
                        val valuesList: List<Personaje?> = ArrayList<Personaje?>(PjsTorneo!!.values())
                        Collections.shuffle(valuesList)
                        var elx = 1
                        for (prx in valuesList) {
                            PjsTorneoNEW.put(elx, prx)
                            elx += 1
                        }
                        PjsTorneo.clear()
                        PjsTorneo.putAll(PjsTorneoNEW)
                        MainServidor.startTorneo()
                        GestorSalida.ENVIAR_cs_CHAT_MENSAJE_A_TODOS(Idiomas.getTexto(_perso.getCuenta().idioma, 122),
                                Colores.NARANJA)
                    }
                    GestorSalida.enviar(_perso,
                            "bb" + lafase + ";101447^101447^" + Mundo.getObjetoModelo(101447).getStatsModelo()
                                    + ";101418^101418^" + Mundo.getObjetoModelo(101418).getStatsModelo() + ":"
                                    + listaTorneo(),
                            true)
                } else {
                    GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 124),
                            Colores.ROJO)
                    GestorSalida.enviar(_perso, "bb2;101447^101447^" + Mundo.getObjetoModelo(101447).getStatsModelo()
                            .toString() + ";101418^101418^" + Mundo.getObjetoModelo(101418).getStatsModelo().toString() + ":", true)
                }
                'r' -> if (_perso != null) {
                    if (_perso.getPelea() != null) {
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                "Estas en combate, no puedes abrir este panel")
                        return
                    }
                    val logrosTemp = StringBuilder()
                    if (_perso.getLogros().size() > 0) {
                        var pos = 1
                        for (logro in _perso.getLogros()) {
                            if (logrosTemp.length() > 0) {
                                logrosTemp.append(";")
                            }
                            var cantidad: String
                            cantidad = when (logro.getLogro().getTipo()) {
                                4 -> "1"
                                5, 6 -> logro.getLogro().getArgs()
                                else -> logro.getLogro().getArgs().split(",").get(1)
                            }
                            logrosTemp.append(pos).append(",").append(logro.getLogro().getTitulo()).append(",")
                                    .append(logro.getLogro().getTipo()).append(",").append(cantidad).append(",")
                                    .append(logro.getLleva()).append(",").append(logro.getEstado())
                            pos++
                        }
                        GestorSalida.ENVIAR_wr_PANEL_OBJETIVOS(_perso, logrosTemp.toString())
                    }
                }
                'R' -> if (_perso != null) {
                    val info: String = packet.substring(2)
                    val Posicion: Int = Integer.parseInt(info)
                    val logro: LogroPersonaje = _perso.getLogros().get(Posicion)
                    logro.confirmarMision(null, "", _perso)
                }
                'T' -> if (_perso != null) {
                    GestorSalida.ENVIAR_bRI_INICIAR_CUENTA_REGRESIVA(_perso)
                    GestorSalida.enviar(_perso, "cC+" + _perso._canales, true)
                }
                'x' -> if (_perso != null) {
                    val infosS: Array<String?> = packet.substring(2).split(Pattern.quote(";"))
                    val Posicion: Int = Integer.parseInt(infosS[0]) - 1
                    val logro: LogroPersonaje = _perso.getLogros().get(Posicion)
                    val titulo: String = logro.getLogro().getTitulo()
                    val Descripcion: String = logro.getLogro().getDescripcion()
                    val reecompensa: Int = logro.getLogro().getRecompensa()
                    var cantidad: String = logro.getLogro().getCantidad()
                    var ItemID = "0"
                    var item = ""
                    if (reecompensa == 1) {
                        try {
                            val expTotal: BigInteger = Mundo.getExpPersonajeReset(_perso.getNivel(), _perso.getNivelOmega())
                            val b = BigDecimal("0.02")
                            val expNecesaria = BigDecimal(expTotal)
                            val XpAdd: BigDecimal = expNecesaria.multiply(b)
                            cantidad = Math.round(XpAdd.longValue()).toString() + ""
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    if (reecompensa == 3) {
                        cantidad = logro.getLogro().getCantidad().split(",").get(1)
                        ItemID = logro.getLogro().getCantidad().split(",").get(0)
                        item = Mundo.getObjetoModelo(Integer.parseInt(ItemID)).getNombre()
                    }
                    GestorSalida.ENVIAR_wy_PANEL_OBJETIVOS(_perso, titulo, Descripcion, reecompensa, cantidad, item,
                            ItemID, Posicion, logro.getLogro().getTipo(), MainServidor.LOGRO_REGALO_FIN)
                }
                'X' -> if (_perso != null) {
                    if (!_perso.logroDiarioValidado) {
                        if (_perso.getLogros().size() > 0) {
                            var validar = false
                            for (logro in _perso.getLogros()) {
                                if (logro.getEstado() === 0) {
                                    validar = false
                                    break
                                } else {
                                    validar = true
                                }
                            }
                            if (validar) {
                                val obj: Int = MainServidor.LOGRO_REGALO_FIN
                                val cant = 1
                                _perso.addObjIdentAInventario(Mundo.getObjetoModelo(obj).crearObjeto(cant,
                                        Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM), false)
                                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "021;$cant~$obj")
                                _perso.logroDiarioValidado = true
                                _perso.setNObjDiarios(_perso.getNObjDiarios() + 1)
                                Sucess.launch(_perso, 13.toByte(), null, 0)
                                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                        "Felicidades! Acabas de completar todos los objetivos diarios!")
                            } else {
                                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                        "Aun no cumples con tus objetivos diarios")
                            }
                        }
                    } else {
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Ya reclamaste tu premio")
                    }
                }
                'v' -> {
                    // GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, "El pase de batalla no esta
                    // disponible en el momento, espera un futuro evento aventureo.", Colores.ROJO);
                    var extra = 0
                    try {
                        if (packet.contains("NaN")) return
                        extra = Integer.parseInt(packet.substring(2))
                    } catch (e: Exception) {
                        extra += _perso.paginaCanj
                    }
                    if (extra < 0) extra = 0
                    val str = StringBuilder("")
                    var prim = false
                    val puntos: Int = _perso.PuntosPrestigio
                    var porcent = 0
                    var siguiente = false
                    var ptsant = 0
                    var b = 0
                    var t = 0
                    var vl = 0 // todo:
                    var maxi = 0
                    var ix = 1
                    while (ix < 9) {
                        val newvlr = ix + extra
                        if (newvlr + 1 > Mundo.Prestigios.size()) {
                            maxi = 1
                        }
                        if (newvlr > Mundo.Prestigios.size()) {
                            ix++
                            continue
                        }
                        val valores: String = Mundo.Prestigios.get(newvlr)
                        if (prim) str.append(",")
                        var canjeados = 0
                        if (_perso.Canjeados.contains(newvlr)) {
                            canjeados = 1
                        }
                        val necesario: Int = Integer.parseInt(valores.split(",").get(1))
                        if (necesario == 0) {
                            maxi = 1
                            ix++
                            continue
                        }
                        var desbloq = 0
                        if (puntos >= necesario) {
                            if (newvlr - 5 > _perso.paginaCanj) _perso.paginaCanj = newvlr - 5
                            desbloq = 1
                            when (ix) {
                                1 -> porcent = 17
                                2 -> porcent = 72
                                3 -> porcent = 124
                                4 -> porcent = 177
                                5 -> porcent = 229
                                6 -> porcent = 283
                                7 -> porcent = 335
                                8 -> porcent = 388
                            }
                            ptsant = necesario
                            if (puntos > necesario && ix >= 8) {
                                porcent = 400
                            }
                            siguiente = true
                        } else {
                            var dividen = 0
                            when (ix) {
                                1 -> dividen = 17
                                2 -> dividen = 35
                                3 -> dividen = 52
                                4 -> dividen = 53
                                5 -> dividen = 52
                                6 -> dividen = 54
                                7 -> dividen = 52
                                8 -> dividen = 53
                            }
                            when (ix) {
                                1 -> {
                                    var newpts = puntos
                                    if (newpts > 17) newpts = 17
                                    t = newpts * 17 / necesario
                                    ptsant += necesario
                                    porcent = t
                                    siguiente = false
                                }
                                2, 3, 4, 5, 6, 7, 8 -> {
                                    if (!siguiente) break
                                    vl = dividen
                                    newpts = puntos - ptsant
                                    t = newpts * vl * 100 / ((necesario - ptsant) * vl)
                                    b = t * vl / 100
                                    ptsant = necesario
                                    if (b > vl) b = vl
                                    porcent += b
                                    siguiente = false
                                }
                            }
                        }
                        val vip: Int = Integer.parseInt(valores.split(",").get(3))
                        val tipo: String = valores.split(",").get(2)
                        var extrast = ""
                        val idespecc: Int = Integer.parseInt(valores.split(",").get(0))
                        if (tipo.equals("item")) {
                            extrast = Mundo.getObjetoModelo(idespecc).getStatsModelo().replace(",", "^")
                        }
                        str.append(ix.toString() + ";" + tipo + ";" + desbloq + ";" + necesario + ";" + canjeados + ";" + idespecc
                                + ";" + vip + ";" + extrast) // id orden, tipo, desbloqueado,necesario,canjeado,id objeto
                        prim = true
                        ix++
                    }
                    str.append(",info;" + porcent + ";" + _perso.getCuenta().getVip() + ";" + extra + ";" + maxi + ";"
                            + _perso.PuntosPrestigio)
                    GestorSalida.enviar(_perso, "Oo" + str.toString(), true)
                }
            }
        } catch (e: Exception) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
            e.printStackTrace()
        }
    }

    private fun analizar_Bustofus(packet: String?) {
        try {
            val estb: Char
            try {
                estb = packet.charAt(1)
            } catch (e: Exception) {
                return
            }
            when (estb) {
                'A' -> bustofus_Panel_Almanax()
                'a' -> {
                    val id: Int = Integer.parseInt(packet.substring(2))
                    val mascota: Objeto = _perso.getObjeto(id)
                    if (_perso.getCasaDentro() != null) {
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                "Debes salir de casa para aplicar esta fusion.")
                        return
                    }
                    if (mascota == null) {
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "No existe el objeto")
                        return
                    }
                    if (_perso.getMontura() == null) {
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                "No tienes puesta la montura a la cual le asignaras los stats")
                        return
                    }
                    if (_perso.getMontura().getNivel() < 100) {
                        val experiencia: Long = Mundo.getExpMontura(MainServidor.NIVEL_MAX_MONTURA)
                        -_perso.getMontura().getExp() + 10
                        _perso.getMontura().addExperiencia(experiencia)
                    }
                    _perso.getMontura().setMaxEnergia()
                    if (!_perso.getMontura().get_statsString().isEmpty()) {
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Ya esta montura fue transformada")
                        return
                    }
                    if (_perso.convertirMascota(id)) {
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                "Tu montura a adquirido los stats de tu mascota con éxito, su mascota ha sido borrada")
                    }
                }
                'b' -> {
                }
                'B' -> bustofus_Mision_Almanax()
                'w' -> _perso.resetearStats(true)
                'C' -> GestorSalida.ENVIAR_ÑF_BESTIARIO_MOBS(this, _perso.listaCardMobs())
                'c' -> {
                    // enviar precio medio
                    val precios = StringBuilder()
                    precios.append("kr")
                    for (objetos in MainServidor.PRECIO_MEDIO_ITEM) {
                        if (precios.toString().equalsIgnoreCase("kr")) {
                            precios.append(Mundo.getObjetoModelo(objetos).getPrecioPromedio() * 100)
                        } else {
                            precios.append(";")
                            precios.append(Mundo.getObjetoModelo(objetos).getPrecioPromedio() * 100)
                        }
                    }
                    GestorSalida.ENVIAR_PRECIO_MEDIO(this, precios)
                }
                'D' -> if (packet!!.length() > 2) {
                    val s: String = packet.substring(2)
                    when (s) {
                        "0" -> GestorSalida.ENVIAR_wf_PANEL_IDOLOS(_perso, _perso.getBonusIdolo())
                        "1" -> if (_perso != null) {
                            if (_perso.getPelea() != null) {
                                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                        "Estas en combate, no puedes abrir este panel")
                                return
                            }
                            GestorSalida.ENVIAR_WK_PANEL_IDOLOS(_perso, _perso.getBonusIdolo())
                        }
                        "3" -> comando_jugador(".finaccion")
                        "4" -> {
                            if (_perso.getPelea() != null) {
                                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Estas en pelea en estos momentos")
                                return
                            }
                            val casa: Casa = Mundo.getCasaDePj(_perso.getID())
                            if (casa == null) {
                                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "No tienes ninguna casa")
                                return
                            }
                            if (casa.getActParametros()) {
                                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "126")
                                return
                            }
                            _perso.teleport(casa.getMapaIDDentro(), casa.getCeldaIDDentro())
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Teleport exitoso")
                        }
                        "5" -> comando_jugador(".tienda")
                        "6" -> comando_jugador(".enciclopedia")
                        "7" -> comando_jugador(".zonas")
                        "8" -> comando_jugador(".tp")
                        "10" -> if (_perso.getNivelOmega() > 0) {
                            var RActual = ""
                            var lista = ""
                            val temp: Omega = Mundo.getOmega(_perso.getNivelOmega())
                            if (temp != null) {
                                temp.getObjeto()
                                RActual = temp.getObjeto().toString() + ";" + temp.getNivel()
                            }
                            for (Recompensa in Mundo.OMEGAS.values()) {
                                if (Recompensa != null) {
                                    if (Recompensa.getObjeto() != null) {
                                        lista += "~" + Recompensa.getObjeto().toString() + ";" + Recompensa.getNivel()
                                    }
                                }
                            }
                            val packetx = StringBuilder()
                            packetx.append("wX").append(_perso.getNivelOmega()).append("|").append(RActual).append("|")
                                    .append(lista.substring(1))
                            GestorSalida.enviar(_perso, packetx.toString(), true)
                        } else {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                    "Debes ser nivel omega para ver este panel")
                            return
                        }
                        "11" -> if (!_perso.isOnAction()) {
                            val celda: Short = 339
                            _perso.teleport(6825, celda)
                        }
                        else -> System.out.println("Packet desconocido: $s")
                    }
                }
                'E' -> {
                    val idMob: Int = Integer.parseInt(packet.substring(2))
                    if (!_perso.tieneCardMob(idMob)) {
                        GestorSalida.ENVIAR_BN_NADA(_perso)
                        return
                    }
                    if (Mundo.getMobModelo(idMob) != null) GestorSalida.ENVIAR_ÑE_DETALLE_MOB(this, Mundo.getMobModelo(idMob).detalleMob())
                }
                'e' -> bustofus_Buscar_Mobs_Drop(packet)
                'F' -> bustofus_Crea_Tu_Item(packet)
                'G' -> {
                }
                'h' -> Mundo.getObjetosPorTipo(_perso, Short.parseShort(packet.substring(2)))
                'I' -> bustofus_Comprar_Sistema_Recurso(packet)
                'J' -> bustofus_Sistema_Recurso()
                'L' -> Mundo.comprarLoteria(packet, _perso)
                'l' -> bustofus_Ruleta_Suerte(packet)
                'm' -> bustofus_Mostrar_Loteria()
                'n' -> {
                    if (_perso.getPelea() != null) {
                        return
                    }
                    bustofus_Panel_Ornamentos()
                }
                'N' -> bustofus_Elegir_Ornamento(packet)
                'o' -> if (_perso != null) {
                    palmad_Ogrinas()
                }
                'P' -> bustofus_Cambiar_Nivel_Alineacion(packet)
                'q' -> bustofus_Borrar_Reporte(packet)
                'r' -> bustofus_Detalle_Reporte(packet)
                'R' -> bustofus_Reportar(packet)
                's' -> if (_perso != null) {
                    bustofus_Servicios(packet)
                }
                'S' -> bustofus_Sets_Rapidos(packet)
                't' -> {
                    if (_perso.getPelea() != null) {
                        return
                    }
                    GestorSalida.ENVIAR_bt_PANEL_TITULOS(_perso)
                }
                'T' -> bustofus_Elegir_Titulo(packet)
                'v' -> {
                    val infos2: Array<String?> = packet.substring(2).split(Pattern.quote("|"))
                    try {
                        val idobj1: Int = Integer.parseInt(infos2[0])
                        val idobj2: Int = Integer.parseInt(infos2[1])
                        val idObjUsado: Int = Integer.parseInt(infos2[2])
                        val obj1: Objeto = _perso.getObjeto(idobj1)
                        val obj2: Objeto = _perso.getObjeto(idobj2)
                        val objTemp1: Objeto = Mundo.getObjetoModelo(obj1.getObjModeloID()).crearObjeto(1,
                                Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.MAXIMO)
                        val objTemp2: Objeto = Mundo.getObjetoModelo(obj2.getObjModeloID()).crearObjeto(1,
                                Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.MAXIMO)
                        objTemp1.convertirStringAStats(obj1.getStats().convertirStatsAString())
                        objTemp2.convertirStringAStats(obj2.getStats().convertirStatsAString())
                        if (obj1.tieneStatTexto(Constantes.STAT_NIVEL)) {
                            objTemp1.addStatTexto(Constantes.STAT_NIVEL,
                                    obj1.getStats().getStatTexto(Constantes.STAT_NIVEL))
                        }
                        if (obj1 == null || obj2 == null) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Uno de los objetos es null")
                            return
                        }
                        if (obj1.getObjModelo().getTipo() === Constantes.OBJETO_TIPO_MASCOTA
                                || obj2.getObjModelo().getTipo() === Constantes.OBJETO_TIPO_MASCOTA) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                    "No puedes fusionar mascota con esta piedra")
                            return
                        }
                        if (obj1.getObjModelo().getTipo() !== obj2.getObjModelo().getTipo()) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Los objetos no son del mismo tipo")
                            return
                        }
                        if (obj1.tieneStatTexto(Constantes.STAT_NIVEL)) {
                            if (!obj1.tieneStatTexto(Constantes.STAT_NIVEL)) {
                                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                        "Uno de los objetos no es fortificado")
                                return
                            } else {
                                val statsTemp: String = obj1.getParamStatTexto(Constantes.STAT_NIVEL, 3)
                                val nivelObjeto: Int = Integer.parseInt(statsTemp, 16)
                                val statsTemp2: String = obj2.getParamStatTexto(Constantes.STAT_NIVEL, 3)
                                val nivelObjeto2: Int = Integer.parseInt(statsTemp2, 16)
                                if (nivelObjeto != nivelObjeto2) {
                                    GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                            "Los objetos no tiene el mismo nivel de fortificacion")
                                    return
                                }
                            }
                        } else if (obj2.tieneStatTexto(Constantes.STAT_NIVEL)) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Uno de los objetos no es fortificado")
                            return
                        }
                        var item: Fusion? = null
                        for (itemTemp in Mundo.FUSIONES) {
                            if (itemTemp.getEntregar1() === obj1.getObjModeloID()) {
                                if (itemTemp.getEntregar2() === obj2.getObjModeloID()) {
                                    item = itemTemp
                                    break
                                }
                            } else if (itemTemp.getEntregar2() === obj1.getObjModeloID()) {
                                if (itemTemp.getEntregar1() === obj2.getObjModeloID()) {
                                    item = itemTemp
                                    break
                                }
                            }
                        }
                        if (item == null) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Estos Objetos no se puede fusionar")
                            return
                        }
                        Mundo.addObjeto(objTemp1, true)
                        Mundo.addObjeto(objTemp2, true)
                        val nuevo: Objeto = Mundo.getObjetoModelo(item.getDar()).crearObjeto(1,
                                Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.MAXIMO)
                        val statsTemp: Map<Integer?, Integer?> = TreeMap<Integer?, Integer?>()
                        val statsTemp2: ArrayList<Integer?> = ArrayList<Integer?>()
                        for (stat in obj1.getStats().getEntrySet()) {
                            statsTemp.put(stat.getKey(), stat.getValue())
                            statsTemp2.add(stat.getKey())
                        }
                        for (stat in obj2.getStats().getEntrySet()) {
                            if (statsTemp.containsKey(stat.getKey())) {
                                statsTemp.put(stat.getKey() * -1, stat.getValue())
                                statsTemp2.add(stat.getKey() * -1)
                            } else {
                                statsTemp.put(stat.getKey(), stat.getValue())
                                statsTemp2.add(stat.getKey())
                            }
                        }
                        val stats: Map<Integer?, Integer?> = TreeMap<Integer?, Integer?>()
                        var cant: Int = statsTemp.size() / 2
                        do {
                            val random: Int = Formulas.getRandomInt(0, statsTemp.size() - 1)
                            val stat: Int = statsTemp2.get(random)
                            if (!stats.containsKey(Math.abs(stat))) {
                                stats.put(Math.abs(stat), statsTemp[stat])
                                cant--
                            }
                        } while (cant > 0)
                        nuevo.getStats().nuevosStats(stats)
                        nuevo.addStatTexto(Constantes.STAT_OBJETO_OCULTO_1, objTemp1.getID().toString() + "")
                        nuevo.addStatTexto(Constantes.STAT_OBJETO_OCULTO_2, objTemp2.getID().toString() + "")
                        if (obj1.tieneStatTexto(Constantes.STAT_NIVEL)) {
                            nuevo.addStatTexto(Constantes.STAT_NIVEL, obj1.getStats().getStatTexto(Constantes.STAT_NIVEL))
                        }
                        _perso.addObjIdentAInventario(nuevo, false)
                        _perso.restarCantObjOEliminar(idobj2, 1, true)
                        _perso.restarCantObjOEliminar(idobj1, 1, true)
                        if (idObjUsado != 101235) {
                            _perso.restarObjPorModYCant(100915, 1)
                        }
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "022;1~" + obj1.getObjModeloID())
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "022;1~" + obj2.getObjModeloID())
                        if (idObjUsado != 101235) {
                            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "022;1~" + 100915)
                        }
                        GestorSQL.SALVAR_PERSONAJE(_perso, true)
                        GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(_perso,
                                "Objeto Fusionado, su nuevo objeto se llama: " + nuevo.getObjModelo().getNombre())
                    } catch (e: Exception) {
                        System.out.println("Error Fusion Objeto: " + e.getMessage().toString() + " Perso: " + _perso.getNombre())
                        GestorSalida.ENVIAR_BN_NADA(_perso)
                    }
                }
                'u' -> {
                    val infos3: Array<String?> = packet.substring(2).split(Pattern.quote("|"))
                    try {
                        val idobj1: Int = Integer.parseInt(infos3[0])
                        val idobj2: Int = Integer.parseInt(infos3[1])
                        val idObjUsado: Int = Integer.parseInt(infos3[2])
                        val obj1: Objeto = _perso.getObjeto(idobj1)
                        val obj2: Objeto = _perso.getObjeto(idobj2)
                        val objTemp1: Objeto = Mundo.getObjetoModelo(obj1.getObjModeloID()).crearObjeto(1,
                                Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.MAXIMO)
                        val objTemp2: Objeto = Mundo.getObjetoModelo(obj2.getObjModeloID()).crearObjeto(1,
                                Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.MAXIMO)
                        objTemp1.convertirStringAStats(obj1.getStats().convertirStatsAString())
                        objTemp2.convertirStringAStats(obj2.getStats().convertirStatsAString())
                        if (obj1.tieneStatTexto(Constantes.STAT_LIGADO_A_CUENTA)) {
                            objTemp1.addStatTexto(Constantes.STAT_LIGADO_A_CUENTA,
                                    obj1.getStats().getStatTexto(Constantes.STAT_LIGADO_A_CUENTA))
                        }
                        if (obj1 == null || obj2 == null) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Uno de los objetos es null")
                            return
                        }
                        if (obj1.getObjModelo().getTipo() !== obj2.getObjModelo().getTipo()) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Los objetos no son del mismo tipo")
                            return
                        }
                        if (obj1.tieneStatTexto(Constantes.STAT_LIGADO_A_CUENTA)) {
                            if (!obj1.tieneStatTexto(Constantes.STAT_LIGADO_A_CUENTA)) {
                                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                        "Uno de los objetos no es fortificado")
                                return
                            } else {
                                val statsTemp: String = obj1.getParamStatTexto(Constantes.STAT_LIGADO_A_CUENTA, 3)
                                val nivelObjeto: Int = Integer.parseInt(statsTemp, 16)
                                val statsTemp2: String = obj2.getParamStatTexto(Constantes.STAT_LIGADO_A_CUENTA, 3)
                                val nivelObjeto2: Int = Integer.parseInt(statsTemp2, 16)
                                if (nivelObjeto != nivelObjeto2) {
                                    GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                            "Los objetos no tiene el mismo nivel de fortificacion")
                                    return
                                }
                            }
                        } else if (obj2.tieneStatTexto(Constantes.STAT_LIGADO_A_CUENTA)) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Uno de los objetos no es fortificado")
                            return
                        }
                        var item: Fusion? = null
                        for (itemTemp in Mundo.FUSIONES) {
                            if (itemTemp.getEntregar1() === obj1.getObjModeloID()) {
                                if (itemTemp.getEntregar2() === obj2.getObjModeloID()) {
                                    item = itemTemp
                                    break
                                }
                            } else if (itemTemp.getEntregar2() === obj1.getObjModeloID()) {
                                if (itemTemp.getEntregar1() === obj2.getObjModeloID()) {
                                    item = itemTemp
                                    break
                                }
                            }
                        }
                        if (item == null) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Estos Objetos no se puede fusionar")
                            return
                        }
                        Mundo.addObjeto(objTemp1, true)
                        Mundo.addObjeto(objTemp2, true)
                        val nuevo: Objeto = Mundo.getObjetoModelo(item.getDar()).crearObjeto(1,
                                Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.MAXIMO)
                        val statsTemp: Map<Integer?, Integer?> = TreeMap<Integer?, Integer?>()
                        val statsTemp2: ArrayList<Integer?> = ArrayList<Integer?>()
                        for (stat in obj1.getStats().getEntrySet()) {
                            statsTemp.put(stat.getKey(), stat.getValue())
                            statsTemp2.add(stat.getKey())
                        }
                        for (stat in obj2.getStats().getEntrySet()) {
                            if (statsTemp.containsKey(stat.getKey())) {
                                statsTemp.put(stat.getKey() * -1, stat.getValue())
                                statsTemp2.add(stat.getKey() * -1)
                            } else {
                                statsTemp.put(stat.getKey(), stat.getValue())
                                statsTemp2.add(stat.getKey())
                            }
                        }
                        val stats: Map<Integer?, Integer?> = TreeMap<Integer?, Integer?>()
                        var cant: Int = statsTemp.size() / 2
                        do {
                            val random: Int = Formulas.getRandomInt(0, statsTemp.size() - 1)
                            val stat: Int = statsTemp2.get(random)
                            if (!stats.containsKey(Math.abs(stat))) {
                                stats.put(Math.abs(stat), statsTemp[stat])
                                cant--
                            }
                        } while (cant > 0)
                        nuevo.getStats().nuevosStats(stats)
                        nuevo.addStatTexto(Constantes.STAT_OBJETO_OCULTO_1, objTemp1.getID().toString() + "")
                        nuevo.addStatTexto(Constantes.STAT_OBJETO_OCULTO_2, objTemp2.getID().toString() + "")
                        if (obj1.tieneStatTexto(Constantes.STAT_LIGADO_A_CUENTA)) {
                            nuevo.addStatTexto(Constantes.STAT_LIGADO_A_CUENTA,
                                    obj1.getStats().getStatTexto(Constantes.STAT_LIGADO_A_CUENTA))
                        }
                        _perso.addObjIdentAInventario(nuevo, false)
                        _perso.restarCantObjOEliminar(idobj2, 1, true)
                        _perso.restarCantObjOEliminar(idobj1, 1, true)
                        if (idObjUsado != 101237) {
                            _perso.restarObjPorModYCant(101125, 1)
                        }
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "022;1~" + obj1.getObjModeloID())
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "022;1~" + obj2.getObjModeloID())
                        if (idObjUsado != 101237) {
                            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "022;1~" + 101125)
                        }
                        GestorSQL.SALVAR_PERSONAJE(_perso, true)
                        GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(_perso,
                                "Objeto Fusionado, su nuevo objeto se llama: " + nuevo.getObjModelo().getNombre())
                    } catch (e: Exception) {
                        System.out.println("Error Fusion Objeto: " + e.getMessage().toString() + " Perso: " + _perso.getNombre())
                        GestorSalida.ENVIAR_BN_NADA(_perso)
                    }
                }
                'U' -> {
                    val infos4: Array<String?> = packet.substring(2).split(Pattern.quote("|"))
                    try {
                        val idobj1: Int = Integer.parseInt(infos4[0])
                        val idobj2: Int = Integer.parseInt(infos4[1])
                        var idObjUsado = 0
                        if (infos4.size > 2) {
                            idObjUsado = Integer.parseInt(infos4[2])
                        }
                        val obj1: Objeto = _perso.getObjeto(idobj1)
                        val obj2: Objeto = _perso.getObjeto(idobj2)
                        val objTemp1: Objeto = Mundo.getObjetoModelo(obj1.getObjModeloID()).crearObjeto(1,
                                Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.MAXIMO)
                        val objTemp2: Objeto = Mundo.getObjetoModelo(obj2.getObjModeloID()).crearObjeto(1,
                                Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.MAXIMO)
                        objTemp1.convertirStringAStats(obj1.getStats().convertirStatsAString())
                        objTemp2.convertirStringAStats(obj2.getStats().convertirStatsAString())
                        if (obj1 == null || obj2 == null) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Uno de los objetos es null")
                            return
                        }
                        if (obj1.getObjModelo().getTipo() !== obj2.getObjModelo().getTipo()) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Los objetos no son del mismo tipo")
                            return
                        }
                        var item: Fusion? = null
                        for (itemTemp in Mundo.FUSIONES) {
                            if (itemTemp.getEntregar1() === obj1.getObjModeloID()) {
                                if (itemTemp.getEntregar2() === obj2.getObjModeloID()) {
                                    item = itemTemp
                                    break
                                }
                            } else if (itemTemp.getEntregar2() === obj1.getObjModeloID()) {
                                if (itemTemp.getEntregar1() === obj2.getObjModeloID()) {
                                    item = itemTemp
                                    break
                                }
                            }
                        }
                        if (item == null) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Estos Objetos no se puede fusionar")
                            return
                        }
                        Mundo.addObjeto(objTemp1, true)
                        Mundo.addObjeto(objTemp2, true)
                        val nuevo: Objeto = Mundo.getObjetoModelo(item.getDar()).crearObjeto(1,
                                Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.MAXIMO)
                        val statsTemp: Map<Integer?, Integer?> = TreeMap<Integer?, Integer?>()
                        val statsTemp2: ArrayList<Integer?> = ArrayList<Integer?>()
                        for (stat in obj1.getStats().getEntrySet()) {
                            statsTemp.put(stat.getKey(), stat.getValue())
                            statsTemp2.add(stat.getKey())
                        }
                        for (stat in obj2.getStats().getEntrySet()) {
                            if (statsTemp.containsKey(stat.getKey())) {
                                statsTemp.put(stat.getKey() * -1, stat.getValue())
                                statsTemp2.add(stat.getKey() * -1)
                            } else {
                                statsTemp.put(stat.getKey(), stat.getValue())
                                statsTemp2.add(stat.getKey())
                            }
                        }
                        val stats: Map<Integer?, Integer?> = TreeMap<Integer?, Integer?>()
                        var cant: Int = statsTemp.size() / 2
                        do {
                            val random: Int = Formulas.getRandomInt(0, statsTemp.size() - 1)
                            val stat: Int = statsTemp2.get(random)
                            if (!stats.containsKey(Math.abs(stat))) {
                                stats.put(Math.abs(stat), statsTemp[stat])
                                cant--
                            }
                        } while (cant > 0)
                        nuevo.getStats().nuevosStats(stats)
                        nuevo.addStatTexto(Constantes.STAT_OBJETO_OCULTO_1, objTemp1.getID().toString() + "")
                        nuevo.addStatTexto(Constantes.STAT_OBJETO_OCULTO_2, objTemp2.getID().toString() + "")
                        _perso.addObjIdentAInventario(nuevo, false)
                        _perso.restarCantObjOEliminar(idobj2, 1, true)
                        _perso.restarCantObjOEliminar(idobj1, 1, true)
                        if (idObjUsado != 101236) {
                            _perso.restarObjPorModYCant(101124, 1)
                        }
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "022;1~" + obj1.getObjModeloID())
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "022;1~" + obj2.getObjModeloID())
                        if (idObjUsado != 101236) {
                            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "022;1~" + 101124)
                        }
                        GestorSQL.SALVAR_PERSONAJE(_perso, true)
                        GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(_perso,
                                "Objeto Fusionado, su nuevo objeto se llama: " + nuevo.getObjModelo().getNombre())
                    } catch (e: Exception) {
                        System.out.println("Error Fusion Objeto: " + e.getMessage().toString() + " Perso: " + _perso.getNombre())
                        GestorSalida.ENVIAR_BN_NADA(_perso)
                    }
                }
                'z' -> {
                    val infos: Array<String?> = packet.substring(2).split(Pattern.quote("|"))
                    val buscar = if (infos.size > 1) infos[1] else ""
                    var iniciarEn = if (infos.size > 2) Integer.parseInt(infos[2]) else 0
                    if (iniciarEn < 0) {
                        iniciarEn = Math.abs(iniciarEn)
                        iniciarEn -= MainServidor.LIMITE_LADDER
                    }
                    Mundo.enviarRanking(_perso, infos[0], buscar.toUpperCase(), iniciarEn)
                }
                'Z' -> GestorSalida.ENVIAR_bL_RANKING_PERMITIDOS(_perso)
                'Y' -> {
                    val objetos: Array<String?> = packet.substring(2).split(Pattern.quote("|"))
                    val objeto1: Int = Integer.parseInt(objetos[0])
                    val objeto2: Int = Integer.parseInt(objetos[1])
                    var cantVeces = 1
                    try {
                        if (objetos.size > 2) {
                            cantVeces = Integer.parseInt(objetos[2])
                        }
                    } catch (e: Exception) {
                        cantVeces = 1
                    }
                    if (cantVeces == 0) {
                        cantVeces = 1
                    }
                    var empezo = false
                    var objetoBase: Objeto? = _perso.getObjeto(objeto1)
                    val objetoBonus: Objeto = _perso.getObjeto(objeto2)
                    if (objetoBase == null || objetoBonus == null) {
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                "No se detectaron items en la mesa. Vuelvelos a poner.")
                        return
                    }
                    if (objetoBase.tieneStatTexto(Constantes.STAT_NIVEL)) {
                        val statsTemp: String = objetoBase.getParamStatTexto(Constantes.STAT_NIVEL, 3)
                        val nivelObjeto: Int = Integer.parseInt(statsTemp, 16)
                        if (nivelObjeto > 1) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                    "Este objeto no se puede mejorar porque tiene fortificacion")
                            return
                        }
                    }
                    var inicio = 0
                    while (inicio < cantVeces) {
                        inicio++
                        if (empezo == true) {
                            Thread.sleep(1000) // es para evitar lag en packets
                        }
                        empezo = true
                        val craf: Crafeo = Mundo.CRAFEOS.get(objetoBase.getObjModeloID())
                        if (craf != null) {
                            val condicion: String = objetoBonus.getObjModelo().getCondiciones()
                            val cond: Int = Integer.parseInt(condicion)
                            if (objetoBonus.getStats().tieneStatID(Constantes.STAT_PORCENTAJE_EXITO)) {
                                val porcentaje: Int = objetoBonus.getStatValor(Constantes.STAT_PORCENTAJE_EXITO)
                                objetoBonus.getStats().fijarStatID(Constantes.STAT_PORCENTAJE_EXITO, 0)
                                val stats: Map<Integer?, Integer?> = HashMap()
                                for (stat in objetoBonus.getStats().getEntrySet()) {
                                    if (!craf.getStats().containsKey(stat.getKey())) {
                                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                                "Este stats no es permitido para este objeto.")
                                        return
                                    }
                                    stats.put(stat.getKey(), stat.getValue())
                                }
                                objetoBonus.getStats().fijarStatID(Constantes.STAT_PORCENTAJE_EXITO, porcentaje)
                                for (stat in stats.entrySet()) {
                                    var valor: Int = objetoBase.getStatValor(stat.getKey())
                                    if (valor == 0) {
                                        if (objetoBase.getStats().getEntrySet2().size() >= craf.getCantidad()) {
                                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                                    "Haz alcanzado el tope maximo de stats para este objeto.")
                                            return
                                        }
                                    }
                                    if (valor >= cond) {
                                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                                "Imposible mejorar por condiciones del objeto.")
                                        return
                                    }
                                    if (valor >= craf.getStatMaximo(stat.getKey())) {
                                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                                "Haz alcanzado el tope maximo para este stats.")
                                        return
                                    } else {
                                        valor += stat.getValue()
                                        valor = Math.min(valor, craf.getStatMaximo(stat.getKey()))
                                        stats.put(stat.getKey(), valor)
                                    }
                                }
                                var nuevo = false
                                if (objetoBase.getCantidad() > 1) {
                                    _perso.restarCantObjOEliminar(objetoBase.getID(), 1, true)
                                    objetoBase = objetoBase.clonarObjeto(1, -1.toByte())
                                    nuevo = true
                                }
                                val random: Int = Formulas.getRandomInt(0, 100)
                                if (random <= porcentaje) {
                                    if (objetoBonus.tieneStatTexto(Constantes.STAT_NIVEL)) {
                                        if (!objetoBase.tieneStatTexto(Constantes.STAT_NIVEL)) {
                                            if (craf.getStats().containsKey(Constantes.STAT_NIVEL)) {
                                                objetoBase.addStatTexto(Constantes.STAT_NIVEL,
                                                        objetoBonus.getStats().getStatTexto(Constantes.STAT_NIVEL))
                                            } else {
                                                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                                        "Este stats no es permitido para este objeto.")
                                                return
                                            }
                                        } else {
                                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                                    "Haz alcanzado el tope maximo para este stats.")
                                            return
                                        }
                                    }
                                    val statsTemp2 = StringBuilder()
                                    for (stat in stats.entrySet()) {
                                        statsTemp2.append(" ( ").append(stat.getValue()).append(" de ")
                                                .append(craf.getStatMaximo(stat.getKey())).append(" ")
                                                .append(Mundo.STATS_NOMBRE.get(stat.getKey())).append(" maximos) ")
                                        objetoBase.fijarStatValor(stat.getKey(), stat.getValue())
                                    }
                                    if (nuevo) {
                                        _perso.addObjIdentAInventario(objetoBase, true)
                                        val temp: Objeto = _perso.getObjIdentInventario(objetoBase, null)
                                        if (temp != null) {
                                            objetoBase = temp
                                        }
                                    }
                                    _perso.restarCantObjOEliminar(objetoBonus.getID(), 1, true)
                                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "022;1~" + objetoBonus.getObjModeloID())
                                    GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(_perso, objetoBase)
                                    GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso)
                                    GestorSQL.SALVAR_OBJETO(objetoBase)
                                    GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(_perso,
                                            "Tu piedra entro con exito!")
                                    GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "El objeto ha adquirido" + statsTemp2.toString().toString() + " con exito.")
                                    if (_perso.getObjModeloNoEquipado(_perso.getObjeto(objeto2).getObjModelo().getID(),
                                                    1) != null) {
                                        GestorSalida.ENVIAR_by_PANEL_ABRIR_FUS(_perso, objetoBase, objetoBonus)
                                    } else {
                                        GestorSalida.ENVIAR_by_PANEL_ABRIR_FUS(_perso, objetoBase, null)
                                    }
                                } else {
                                    if (nuevo) {
                                        _perso.addObjIdentAInventario(objetoBase, true)
                                        val temp: Objeto = _perso.getObjIdentInventario(objetoBase, null)
                                        if (temp != null) {
                                            objetoBase = temp
                                        }
                                    }
                                    _perso.restarCantObjOEliminar(objetoBonus.getID(), 1, true)
                                    _perso.restarObjPorModYCant(101141, 1)
                                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "022;1~" + objetoBonus.getObjModeloID())
                                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "022;1~" + 101141)
                                    GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(_perso,
                                            "No has tenido suerte por favor intentalo nuevamente, has perdido una mesa")
                                    GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                            "Tu piedra ha fallado y has perdido una mesa.")
                                    GestorSalida.ENVIAR_by_PANEL_ABRIR_FUS(_perso, objetoBase, objetoBonus)
                                }
                            } else {
                                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Este objeto no se puede mejorar.")
                                return
                            }
                        } else {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                    "Este no es un objeto que se pueda mejorar.")
                            return
                        }
                        if (_perso.getObjModeloNoEquipado(_perso.getObjeto(objeto2).getObjModelo().getID(), 1) == null) {
                            GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(_perso,
                                    "Te has quedado sin piedras para continuar.")
                        }
                        if (_perso.getObjModeloNoEquipado(101141, 1) == null) {
                            GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(_perso,
                                    "Tu piedra Fallo y te has quedado sin mesas, el proceso se detuvo!")
                            GestorSalida.ENVIAR_by_PANEL_CERRAR_FUS(_perso)
                            return
                        }
                    }
                }
            }
        } catch (e: Exception) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
            e.printStackTrace()
            return
        }
    }

    private fun bustofus_Panel_Ornamentos() {
        if (!MainServidor.PARAM_PERMITIR_ORNAMENTOS) {
            GestorSalida.ENVIAR_BN_NADA(_perso, "ORNAMENTOS NO DISPONIBLES")
            return
        }
        GestorSalida.ENVIAR_bñ_PANEL_ORNAMENTOS(_perso)
    }

    private fun bustofus_Elegir_Ornamento(packet: String?) {
        try {
            if (_perso.getPelea() != null) {
                return
            }
            if (!MainServidor.PARAM_PERMITIR_ORNAMENTOS) {
                GestorSalida.ENVIAR_BN_NADA(_perso, "ORNAMENTOS NO DISPONIBLES")
                return
            }
            val ornamentoID: Int = Integer.parseInt(packet.substring(2))
            if (ornamentoID == 0) {
                _perso.setOrnamento(ornamentoID)
            } else {
                val ornamento: Ornamento = Mundo.getOrnamento(ornamentoID)
                if (ornamento.adquirirOrnamento(_perso)) {
                    _perso.addOrnamento(ornamentoID)
                    _perso.setOrnamento(ornamentoID)
                }
            }
        } catch (e: Exception) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
        }
    }

    private fun bustofus_Elegir_Titulo(packet: String?) {
        try {
            if (_perso.getPelea() != null) {
                return
            }
            val a: Array<String?> = packet.substring(2).split(";")
            val tituloID: Int = Integer.parseInt(a[0])
            val color: Int = Integer.parseInt(a[1])
            if (tituloID == 0) {
                _perso.addTitulo(0, -1)
                _perso.refrescarEnMapa()
                GestorSalida.ENVIAR_bV_CERRAR_PANEL(_perso)
            } else {
                val titulo: Titulo = Mundo.getTitulo(tituloID)
                if (titulo.esVip()) {
                    if (!_perso.getCuenta().esAbonado()) {
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Titulo disponible solo para VIP")
                        return
                    }
                }
                if (titulo.adquirirTitulo(_perso)) {
                    _perso.addTitulo(tituloID, color)
                    _perso.refrescarEnMapa()
                    GestorSalida.ENVIAR_bV_CERRAR_PANEL(_perso)
                }
                _perso.refrescarEnMapa()
                GestorSalida.ENVIAR_bV_CERRAR_PANEL(_perso)
            }
        } catch (e: Exception) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
        }
    }

    private fun bustofus_Buscar_Mobs_Drop(packet: String?) {
        val str = StringBuilder()
        val mobs: ArrayList<Integer?> = ArrayList()
        for (s in packet.substring(2).split(",")) {
            if (s.isEmpty()) {
                continue
            }
            val objMod: ObjetoModelo = Mundo.getObjetoModelo(Integer.parseInt(s)) ?: continue
            for (idMob in objMod.getMobsQueDropean()) {
                if (!_perso.tieneCardMob(idMob)) {
                    continue
                }
                if (!mobs.contains(idMob)) {
                    mobs.add(idMob)
                }
            }
        }
        for (idMob in mobs) {
            if (str.length() > 0) {
                str.append(",")
            }
            str.append(idMob)
        }
        GestorSalida.ENVIAR_Ñf_BESTIARIO_DROPS(this, str.toString())
    }

    fun bustofus_Mision_Almanax() {
        _perso.cumplirMisionAlmanax()
    }

    private fun randomItem(Packet: String?): Int {
        val azar: Array<String?> = Packet.split(";")
        val objetoAzar = azar[Formulas.getRandomInt(0, azar.size - 1)]
        val idDar: Int = Integer.parseInt(objetoAzar.split(",").get(0))
        val cantDar: Int = Math.abs(Integer.parseInt(objetoAzar.split(",").get(1)))
        _perso.addObjIdentAInventario(Mundo.getObjetoModelo(idDar).crearObjeto(cantDar,
                Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM), false)
        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "021;$cantDar~$idDar")
        GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso)
        return idDar
    }

    private fun bustofus_Ruleta_Suerte(packet: String?) {
        try {
            when (packet.charAt(2)) {
                'P' -> {
                }
                'G' -> {
                    val ficha: Int = Integer.parseInt(packet.substring(3))
                    val index: Int = Formulas.getRandomInt(0, 7)
                    val premios: String = Mundo.RULETA.get(ficha)
                    if (premios == null || premios.isEmpty()) {
                        GestorSalida.ENVIAR_BN_NADA(_perso, "RULETA NO TIENE PREMIOS")
                        return
                    }
                    val premio: Int = Integer.parseInt(premios.split(",").get(index))
                    val objMod: ObjetoModelo = Mundo.getObjetoModelo(premio)
                    if (objMod == null) {
                        GestorSalida.ENVIAR_BN_NADA(_perso, "RULETA PREMIO NO EXISTE")
                        return
                    }
                    if (!_perso.restarCantObjOEliminar(ficha, 1, true)) {
                        GestorSalida.ENVIAR_BN_NADA(_perso, "RULETA NO TIENE FICHA")
                        return
                    }
                    GestorSalida.ENVIAR_brG_RULETA_GANADOR(this, index)
                    Thread.sleep(3000)
                    _perso.addObjIdentAInventario(
                            objMod.crearObjeto(1, Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM), false)
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "021;1~$premio")
                }
            }
        } catch (e: Exception) {
            MainServidor
                    .redactarLogServidorln("EXCEPTION Packet " + packet + ", bustofus_Ruleta_Suerte " + e.toString())
            e.printStackTrace()
        }
    }

    /*
	 * private void Palmad_Servicios(String packet) { try { Servicio servicio =
	 * null; switch (packet.charAt(2)) { case 'a':// cambio de color servicio =
	 * Mundo.getServicio(Constantes.SERVICIO_CAMBIO_COLOR); break; case 'b'://
	 * cambio de color servicio =
	 * Mundo.getServicio(Constantes.SERVICIO_CAMBIO_COLOR); break; case 'c'://
	 * cambio de color servicio =
	 * Mundo.getServicio(Constantes.SERVICIO_CAMBIO_COLOR); break; case 'd'://
	 * cambio de color servicio =
	 * Mundo.getServicio(Constantes.SERVICIO_CAMBIO_COLOR); break; default: String[]
	 * arg = packet.split(";"); try { if (arg.length > 1) {
	 * _perso.setMedioPagoServicio(Byte.parseByte(arg[1])); } } catch (Exception e)
	 * { } servicio = Mundo.getServicio(Integer.parseInt(arg[0].substring(2)));
	 * packet = ""; break; } servicio.usarServicio(_perso, packet); } catch
	 * (Exception e) { GestorSalida.ENVIAR_BN_NADA(_perso,
	 * "EXCEPTION BUSTOFUS SERVICIOS");
	 * MainServidor.redactarLogServidorln("EXCEPTION Packet " + packet +
	 * ", bustofus_Servicios " + e.toString()); e.printStackTrace(); } };
	 */
    private fun bustofus_Servicios(packet: String?) {
        var packet = packet
        try {
            var servicio: Servicio? = null
            when (packet.charAt(2)) {
                'C' -> servicio = Mundo.getServicio(Constantes.SERVICIO_CAMBIO_COLOR)
                'G' -> servicio = Mundo.getServicio(Constantes.SERVICIO_CAMBIO_EMBLEMA)
                'M' -> servicio = Mundo.getServicio(Constantes.SERVICIO_MIMOBIONTE)
                'm' -> servicio = Mundo.getServicio(Constantes.SERVICIO_TRANSFORMAR_MONTURA)
                'N' -> servicio = Mundo.getServicio(Constantes.SERVICIO_CAMBIO_NOMBRE)
                'T' -> servicio = Mundo.getServicio(Constantes.SERVICIO_TITULO_PERSONALIZADO)
                'H' -> servicio = Mundo.getServicio(Constantes.SERVICIO_ALINEACION_MERCENARIO)
                'R' -> servicio = Mundo.getServicio(Constantes.SERVICIO_REVIVIR)
                'X' -> servicio = Mundo.getServicio(Constantes.SERVICIO_CAMBIO_SEXO)
                '@' -> servicio = Mundo.getServicio(Constantes.SERVICIO_MONTURA_CAMALEON)
                '*' -> servicio = Mundo.getServicio(Constantes.SERVICIO_RESET_NIVEL)
                'f' -> servicio = Mundo.getServicio(Constantes.SERVICIO_CAMBIO_FECA)
                'O' -> servicio = Mundo.getServicio(Constantes.SERVICIO_CAMBIO_OSAMODA)
                'A' -> servicio = Mundo.getServicio(Constantes.SERVICIO_CAMBIO_ANUTROF)
                'S' -> servicio = Mundo.getServicio(Constantes.SERVICIO_CAMBIO_SRAM)
                'x' -> servicio = Mundo.getServicio(Constantes.SERVICIO_CAMBIO_XELOR)
                'Z' -> servicio = Mundo.getServicio(Constantes.SERVICIO_CAMBIO_ZURCARAK)
                'a' -> servicio = Mundo.getServicio(Constantes.SERVICIO_CAMBIO_ANIRIPSA)
                'y' -> servicio = Mundo.getServicio(Constantes.SERVICIO_CAMBIO_YOPUKA)
                'o' -> servicio = Mundo.getServicio(Constantes.SERVICIO_CAMBIO_OCRA)
                's' -> servicio = Mundo.getServicio(Constantes.SERVICIO_CAMBIO_SADIDA)
                ')' -> servicio = Mundo.getServicio(Constantes.SERVICIO_CAMBIO_SACROGITO)
                'p' -> servicio = Mundo.getServicio(Constantes.SERVICIO_CAMBIO_PANDAWA)
                't' -> servicio = Mundo.getServicio(Constantes.SERVICIO_CAMBIO_TYMADOR)
                'z' -> servicio = Mundo.getServicio(Constantes.SERVICIO_CAMBIO_ZOBAL)
                'D' -> servicio = Mundo.getServicio(Constantes.SERVICIO_CAMBIO_STEAMER)
                'd' -> servicio = Mundo.getServicio(Constantes.SERVICIO_CAMBIO_SELATROP)
                'h' -> servicio = Mundo.getServicio(Constantes.SERVICIO_CAMBIO_HIPERMAGO)
                'u' -> servicio = Mundo.getServicio(Constantes.SERVICIO_CAMBIO_UGINAK)
                else -> {
                    val arg: Array<String?> = packet.split(";")
                    try {
                        if (arg.size > 1) {
                            _perso.setMedioPagoServicio(Byte.parseByte(arg[1]))
                        }
                    } catch (e: Exception) {
                    }
                    servicio = Mundo.getServicio(Integer.parseInt(arg[0].substring(2)))
                    packet = ""
                }
            }
            servicio.usarServicio(_perso, packet)
        } catch (e: Exception) {
            GestorSalida.ENVIAR_BN_NADA(_perso, "EXCEPTION BUSTOFUS SERVICIOS")
            MainServidor.redactarLogServidorln("EXCEPTION Packet " + packet + ", bustofus_Servicios " + e.toString())
            e.printStackTrace()
        }
    }

    private fun bustofus_Sets_Rapidos(packet: String?) {
        try {
            when (packet.charAt(2)) {
                'B' -> {
                    _perso.borrarSetRapido(Integer.parseInt(packet.substring(3)))
                    GestorSalida.ENVIAR_BN_NADA(_perso, "SET RAPIDO BORRADO")
                }
                'C' -> {
                    val split: Array<String?> = packet.substring(3).split(Pattern.quote("|"))
                    val id: Int = Integer.parseInt(split[0])
                    var nombre = split[1]
                    if (nombre!!.length() > 20) {
                        nombre = nombre.substring(0, 20)
                    }
                    val plantilla: String = (Encriptador.NUMEROS + Encriptador.ABC_MIN + Encriptador.ABC_MAY
                            + Encriptador.ESPACIO)
                    for (letra in nombre.toCharArray()) {
                        if (!plantilla.contains(letra.toString() + "")) {
                            nombre = nombre.replace(letra.toString() + "", "")
                        }
                    }
                    val icono: Int = Integer.parseInt(split[2])
                    val data = objeto_String_Set_Equipado()
                    _perso.addSetRapido(id, nombre, icono, data)
                    GestorSalida.ENVIAR_BN_NADA(_perso, "SET RAPIDO CREADO")
                }
                'U' -> {
                    val set: SetRapido = _perso.getSetRapido(Integer.parseInt(packet.substring(3))) ?: return
                    var cambio = objeto_Desequipar_Set()
                    if (cambio >= 1) {
                        _perso.actualizarObjEquipStats()
                    }
                    Thread.sleep(200)
                    cambio = Math.max(cambio, objeto_Equipar_Set(set))
                    if (cambio >= 1) {
                        _perso.refrescarStuff(true, cambio >= 1, cambio >= 2)
                    }
                }
            }
        } catch (e: Exception) {
            MainServidor
                    .redactarLogServidorln("EXCEPTION Packet " + packet + ", bustofus_Sets_Rapidos " + e.toString())
            e.printStackTrace()
        }
    }

    private fun bustofus_Cambiar_Nivel_Alineacion(packet: String?) {
        if (!_perso.estaDisponible(true, true)) {
            GestorSalida.ENVIAR_BN_NADA(_perso, "NO DISPONIBLE")
            return
        }
        if (MainServidor.NIVEL_MAX_ESCOGER_NIVEL <= 1) {
            GestorSalida.ENVIAR_BN_NADA(_perso, "MAX ESCOGER NIVEL ES 1")
            return
        }
        try {
            val split: Array<String?> = packet.substring(2).split(Pattern.quote("|"))
            val nivel: Int = Integer.parseInt(split[0])
            val alineacion: Byte = Byte.parseByte(split[1])
            if (MainServidor.MODO_PVP) {
                _perso.cambiarNivelYAlineacion(nivel, alineacion)
            } else {
                Mundo.getServicio(Constantes.SERVICIO_ESCOGER_NIVEL).usarServicio(_perso, packet)
            }
        } catch (e: Exception) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
        }
    }

    private fun bustofus_Panel_Almanax() {
        if (!MainServidor.PARAM_ALMANAX) {
            GestorSalida.ENVIAR_BN_NADA(_perso, "ALMANAX NO DISPONIBLE")
            return
        }
        val almanax: Almanax = Mundo.getAlmanaxDelDia()
        if (almanax == null) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1ALMANAX_NO_HAY_MISION")
            return
        }
        try {
            val cal: Calendar = Calendar.getInstance()
            GestorSalida.ENVIAR_ÑX_PANEL_ALMANAX(this,
                    cal.get(Calendar.YEAR).toString() + "|" + cal.get(Calendar.MONTH) + "|" + cal.get(Calendar.DAY_OF_MONTH) + "|"
                            + almanax.getOfrenda()._primero + "," + almanax.getOfrenda()._segundo + "|"
                            + almanax.getTipo() + "," + almanax.getBonus() + "|" + _perso.cantMisionseAlmanax() + ","
                            + MainServidor.MAX_MISIONES_ALMANAX + "|" + _perso.getNombre() + "|"
                            + if (_perso.realizoMisionDelDia()) 1 else 0)
        } catch (e: Exception) {
        }
    }

    private fun bustofus_Borrar_Reporte(packet: String?) {
        if (GestorSQL.DELETE_REPORTE(Byte.parseByte(packet.charAt(2).toString() + ""), Integer.parseInt(packet.substring(3)))) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1REPORTE_BORRADO_OK")
        } else {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1REPORTE_BORRADO_ERROR")
        }
    }

    private fun bustofus_Detalle_Reporte(packet: String?) {
        when (packet.charAt(2)) {
            '0', '1', '2', '3' -> {
                val arg: Array<String?> = packet.substring(3).split(";")
                val tipo: Byte = Byte.parseByte(packet.charAt(2).toString() + "")
                val idReporte: Int = Integer.parseInt(arg[0])
                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, GestorSQL.GET_DESCRIPTION_REPORTE(tipo, idReporte))
                _cuenta.addIDReporte(tipo, idReporte)
            }
            else -> {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return
            }
        }
    }

    private fun bustofus_Reportar(packet: String?) {
        if (System.currentTimeMillis() - _cuenta.getUltimoReporte() < 300000) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1REPORTE_ESPERAR_ENVIAR_OTRO")
            return
        }
        val packet2: String = Constantes.filtro(packet)
        val tema: String = packet2.substring(4).split(Pattern.quote("|")).get(1)
        val detalle: String = packet2.substring(4).split(Pattern.quote("|")).get(2)
        when (packet.charAt(3)) {
            '1' -> GestorSQL.INSERT_REPORTE_BUG(_perso.getNombre(), tema, detalle)
            '2' -> GestorSQL.INSERT_SUGERENCIAS(_perso.getNombre(), tema, detalle)
            '3' -> GestorSQL.INSERT_DENUNCIAS(_perso.getNombre(), tema, detalle)
            '4' -> GestorSQL.INSERT_PROBLEMA_OGRINAS(_perso.getNombre(), tema, detalle)
            else -> {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1REPORTE_ENVIADO_ERROR")
                return
            }
        }
        _cuenta.setUltimoReporte(System.currentTimeMillis())
        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1REPORTE_ENVIADO_OK")
    }

    private fun bustofus_Mostrar_Loteria() {
        if (!MainServidor.PARAM_LOTERIA_OGRINAS) {
            var precioL: Float = MainServidor.PRECIO_LOTERIA / 1000000
            var millonesPrecio = true
            if (precioL <= 0) {
                precioL = MainServidor.PRECIO_LOTERIA / 1000
                millonesPrecio = false
            }
            var premioL: Float = MainServidor.PREMIO_LOTERIA / 1000000
            var millonesPremio = true
            if (premioL <= 0) {
                premioL = MainServidor.PREMIO_LOTERIA / 1000
                millonesPremio = false
            }
            GestorSalida.ENVIAR_bT_PANEL_LOTERIA(_perso,
                    (if (precioL % 1 > 0) precioL else precioL.toInt().toString() + "").toString() + (if (millonesPrecio) "M" else "K") + ";"
                            + (if (premioL % 1 > 0) premioL else premioL.toInt().toString() + "") + if (millonesPremio) "M" else "K")
        } else {
            GestorSalida.ENVIAR_bT_PANEL_LOTERIA(_perso,
                    MainServidor.PRECIO_LOTERIA.toString() + ";" + MainServidor.PREMIO_LOTERIA)
        }
    }

    /*
	 * private void bustofus_Votar() { if (MainServidor.OGRINAS_POR_VOTO < 0) {
	 * GestorSalida.ENVIAR_BN_NADA(_perso, "VOTAR NO OGRINAS"); return; } if
	 * (_perso.getPelea() != null) { _votarDespuesPelea = true;
	 * GestorSalida.ENVIAR_BN_NADA(_perso, "VOTAR PELEA"); return; } int
	 * tiempoRestante = _cuenta.tiempoRestanteParaVotar(); int tiempoAparecer =
	 * MainServidor.MINUTOS_SPAMEAR_BOTON_VOTO; if (tiempoRestante > 0) {
	 * tiempoAparecer = tiempoRestante; } if (MainServidor.OGRINAS_POR_VOTO > 0) {
	 * if (_cuenta.puedeVotar()) { _cuenta.darOgrinasPorVoto(); } } for
	 * (ServidorSocket ep : ServidorServer.getClientes()) { if (ep == this) {
	 * continue; } if (ep._perso == null) { continue; } if
	 * (ep.getActualIP().equals(_IP)) { if (tiempoRestante <= 0 &&
	 * ep._perso.getPelea() != null) { continue; }
	 * GestorSalida.ENVIAR_bP_VOTO_RPG_PARADIZE(ep._perso, tiempoAparecer, false); }
	 * } GestorSalida.ENVIAR_bP_VOTO_RPG_PARADIZE(_perso, tiempoAparecer,
	 * tiempoRestante <= 0); }
	 */
    private fun bustofus_Sistema_Recurso() {
        if (MainServidor.PRECIO_SISTEMA_RECURSO <= 0) {
            GestorSalida.ENVIAR_BN_NADA(_perso, "SISTEMA RECURSO DESHABILITADO")
            return
        }
        val str = StringBuilder()
        for (tipo in MainServidor.TIPO_RECURSOS) {
            if (str.length() > 0) {
                str.append(";")
            }
            str.append(tipo)
        }
        val str2 = StringBuilder()
        for (objNo in MainServidor.OBJ_NO_PERMITIDOS) {
            if (str2.length() > 0) {
                str2.append(";")
            }
            str2.append(objNo)
        }
        GestorSalida.ENVIAR_bI_SISTEMA_RECURSO(_perso, MainServidor.PRECIO_SISTEMA_RECURSO.toString() + "|" + str.toString() + "|"
                + str2.toString() + "|" + if (MainServidor.PARAM_PRECIO_RECURSOS_EN_OGRINAS) 1 else 0)
    }

    private fun bustofus_Comprar_Sistema_Recurso(packet: String?) {
        if (MainServidor.PRECIO_SISTEMA_RECURSO <= 0) {
            GestorSalida.ENVIAR_BN_NADA(_perso, "COMPRAR SISTEMA RECURSO DESHABILITADO")
            return
        }
        val r: Array<String?> = packet.substring(2).split(";")
        var idObjMod = -1
        var cantidad = -1
        try {
            idObjMod = Integer.parseInt(r[0])
            cantidad = Integer.parseInt(r[1])
        } catch (e: Exception) {
            GestorSalida.ENVIAR_BN_NADA(_perso, "COMPRAR SISTEMA RECURSO $packet")
            return
        }
        val objMod: ObjetoModelo = Mundo.getObjetoModelo(idObjMod)
        if (objMod == null) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1OBJECT_DONT_EXIST")
            return
        }
        if (!MainServidor.TIPO_RECURSOS.contains(objMod.getTipo()) || MainServidor.OBJ_NO_PERMITIDOS.contains(idObjMod)
                || objMod.getOgrinas() > 0) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1ERROR_BUY_RECURSE")
            return
        }
        val precioInt = (MainServidor.PRECIO_SISTEMA_RECURSO * objMod.getNivel()
                * Math.pow(objMod.getNivel(), 0.5)) as Float
        if (cantidad < 1) {
            cantidad = 1
        }
        if (!Formulas.valorValido(cantidad, precioInt.toInt())) {
            GestorSalida.ENVIAR_BN_NADA(_perso, "INTENTO BUG MULTIPLICADOR")
            return
        }
        var precio: Long = 0
        try {
            precio = Math.ceil(precioInt * cantidad) as Int.toLong()
            if (!MainServidor.PARAM_PRECIO_RECURSOS_EN_OGRINAS) {
                precio = Math.max(objMod.getKamas() * cantidad, precio)
            }
        } catch (e: Exception) {
            GestorSalida.ENVIAR_BN_NADA(_perso, "COMPRAR SISTEM RECURSO PRECIO INVALIDO")
            return
        }
        if (precio <= 0) {
            GestorSalida.ENVIAR_BN_NADA(_perso, "COMPRAR SISTEMA RECURSO PRECIO <= 0")
            return
        }
        if (MainServidor.PARAM_PRECIO_RECURSOS_EN_OGRINAS) {
            if (!GestorSQL.RESTAR_OGRINAS1(_cuenta, precio, _perso)) {
                return
            }
        } else {
            if (_perso.getKamas() >= precio) {
                _perso.addKamas(-precio, true, true)
            } else {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1128;$precio")
                return
            }
        }
        _perso.addObjIdentAInventario(
                objMod.crearObjeto(cantidad, Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM), false)
        GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso)
        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "021;$cantidad~$idObjMod")
    }

    private fun bustofus_Crea_Tu_Item(packet: String?) {
        var error = 0
        try {
            error = 1
            if (!Mundo.getServicio(Constantes.SERVICIO_CREA_TU_ITEM).estaActivo()) {
                GestorSalida.ENVIAR_BN_NADA(_perso, "CREA TU ITEM DESHABILITADO")
                return
            }
            error = 2
            val split: Array<String?> = packet.substring(2).split(Pattern.quote("|"))
            val nombre = split[0]
            val idModelo: Int = Integer.parseInt(split[1])
            val gfx: Int = Integer.parseInt(split[2])
            val aStats: Array<String?> = split[3].split(";")
            val firma = split[4]!!.equals("1")
            val crea: CreaTuItem = Mundo.getCreaTuItem(idModelo)
            val objMod: ObjetoModelo = Mundo.getObjetoModelo(idModelo)
            error = 3
            if (objMod == null) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1CREA_TU_ITEM_ERROR_OBJ_MODELO")
                return
            }
            error = 4
            if (crea == null) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1CREA_TU_ITEM_ERROR_CREAR_MODELO")
                return
            }
            val tipo: Int = objMod.getTipo()
            error = 5
            if (Constantes.GFXS_CREA_TU_ITEM.get(tipo) == null) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1CREA_TU_ITEM_ERROR_GFX_TIPO")
                return
            }
            error = 6
            if (!Constantes.GFXS_CREA_TU_ITEM.get(tipo).contains(gfx)) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1CREA_TU_ITEM_ERROR_GFX_CONTAIN")
                return
            }
            var ogrinas: Long = crea.getPrecioBase()
            if (firma) {
                ogrinas += 10
            }
            error = 7
            val plantilla: String = (Encriptador.NUMEROS + Encriptador.ABC_MIN + Encriptador.ABC_MAY
                    + Encriptador.ESPACIO)
            var paso = true
            for (letra in nombre.toCharArray()) {
                if (!plantilla.contains(letra.toString() + "")) {
                    paso = false
                    break
                }
            }
            if (nombre!!.length() < 4 || nombre.length() > 30) {
                paso = false
            }
            error = 8
            if (!paso) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1CREA_TU_ITEM_ERROR_NOMBRE")
                return
            }
            error = 9
            val stats = StringBuilder(Integer.toHexString(Constantes.STAT_COLOR_NOMBRE_OBJETO)
                    .toString() + "#3#0#0" + "," + Integer.toHexString(Constantes.STAT_CAMBIAR_GFX_OBJETO) + "#0#0#"
                    + Integer.toHexString(gfx) + "," + Integer.toHexString(Constantes.STAT_CAMBIAR_NOMBRE_OBJETO)
                    + "#0#0#0#" + nombre)
            val ids: ArrayList<Integer?> = ArrayList()
            for (e in aStats) {
                try {
                    val statID: Int = Integer.parseInt(e.split(",").get(0))
                    if (statID <= 0 || ids.contains(statID)) {
                        continue
                    }
                    val cantidad: Int = Math.max(1, Math.min(Integer.parseInt(e.split(",").get(1)), crea.getMaximoStat(statID)))
                    if (CreaTuItem.PRECIOS.get(statID) == null) {
                        continue
                    }
                    ogrinas += Math.ceil(CreaTuItem.PRECIOS.get(statID) * cantidad)
                    if (stats.length() > 0) {
                        stats.append(",")
                    }
                    stats.append(
                            Integer.toHexString(statID).toString() + "#" + Integer.toHexString(cantidad) + "#0#0#0d0+" + cantidad)
                    ids.add(statID)
                    if (ids.size() >= 9) {
                        break
                    }
                } catch (e1: Exception) {
                }
            }
            error = 10
            val maximo: Int = crea.getMaxOgrinas()
            if (ogrinas > maximo) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1CREA_TU_ITEM_ERROR_MAXIMO_OGRINAS")
                return
            }
            error = 11
            if (firma) {
                if (stats.length() > 0) {
                    stats.append(",")
                }
                stats.append(Integer.toHexString(Constantes.STAT_FACBRICADO_POR).toString() + "#0#0#0#" + _perso.getNombre())
            }
            error = 12
            if (GestorSQL.RESTAR_OGRINAS1(_cuenta, ogrinas, _perso)) {
                error = 13
                val nuevo: Objeto = Mundo.getObjetoModelo(idModelo).crearObjeto(1, Constantes.OBJETO_POS_NO_EQUIPADO,
                        CAPACIDAD_STATS.MAXIMO)
                error = 14
                if (MainServidor.PARAM_OBJETOS_OGRINAS_LIGADO) {
                    nuevo.addStatTexto(Constantes.STAT_LIGADO_A_CUENTA, "0#0#0#" + _perso.getNombre())
                }
                error = 15
                nuevo.convertirStringAStats(stats.toString())
                error = 16
                _perso.addObjIdentAInventario(nuevo, false)
            }
        } catch (e: Exception) {
            GestorSalida.ENVIAR_BN_NADA(_perso, "CREART TU ITEM EXCEPTION - $error")
        }
    }

    /*
	 * private void bustofus_Ogrinas() { try { _perso.setMedioPagoServicio((byte)
	 * 0); GestorSalida.ENVIAR_bOc_ABRIR_PANEL_SERVICIOS(_perso,
	 * GestorSQL.GET_CREDITOS_CUENTA(_cuenta.getID()),
	 * GestorSQL.GET_OGRINAS_CUENTA(_cuenta.getID())); } catch (final Exception e) {
	 * } }
	 */
    private fun palmad_Ogrinas() {
        try {
            _perso.setMedioPagoServicio(0.toByte())
            GestorSalida.ENVIAR_bOC_ABRIR_PANEL_SERVICIOS(_perso, GestorSQL.GET_CREDITOS_CUENTA(_cuenta.getID()),
                    GestorSQL.GET_OGRINAS_CUENTA(_cuenta.getID()))
        } catch (e: Exception) {
        }
    }

    private fun palmad_rostro() {
        try {
            GestorSalida.ENVIAR_bOC_ABRIR_PANEL_ROSTRO(_perso)
        } catch (e: Exception) {
        }
    }

    private fun analizar_Documentos(packet: String?) {
        when (packet.charAt(1)) {
            'V' -> GestorSalida.ENVIAR_dV_CERRAR_DOCUMENTO(_perso)
            else -> return
        }
    }

    private fun analizar_Tutoriales(packet: String?) {
        val param: Array<String?> = packet.split(Pattern.quote("|"))
        val tuto: Tutorial = _perso.getTutorial()
        if (tuto == null) {
            GestorSalida.ENVIAR_BN_NADA(_perso, "TUTORIAL NULO")
            return
        }
        _perso.setTutorial(null)
        when (packet.charAt(1)) {
            'V' -> {
                if (packet.charAt(2) !== '0' && packet.charAt(2) !== '4') {
                    try {
                        if (System.currentTimeMillis() - _perso.getInicioTutorial() > 13000) {
                            val recompensa: Int = Integer.parseInt(packet.charAt(2).toString() + "") - 1
                            tuto.getRecompensa().get(recompensa).realizarAccion(_perso, null, -1, -1.toShort())
                        }
                    } catch (e: Exception) {
                        MainServidor.redactarLogServidorln("Se quizo usar un tutorial con $packet")
                    }
                }
                if (tuto.getFin() != null) {
                    tuto.getFin().realizarAccion(_perso, null, -1, -1.toShort())
                }
                try {
                    if (param.size > 2) {
                        val orientacion: Byte = Byte.parseByte(param[2])
                        val celdaID: Short = Short.parseShort(param[1])
                        _perso.setOrientacion(orientacion)
                        _perso.setCelda(_perso.getMapa().getCelda(celdaID))
                    }
                } catch (e: Exception) {
                }
                _perso.setOcupado(false)
                GestorSalida.ENVIAR_BN_NADA(_perso)
            }
            else -> return
        }
    }

    private fun analizar_Misiones(packet: String?) {
        when (packet.charAt(1)) {
            'L' -> GestorSalida.ENVIAR_QL_LISTA_MISIONES(_perso, _perso.listaMisiones())
            'S' -> {
                var misionID = -1
                try {
                    misionID = Integer.parseInt(packet.substring(2))
                } catch (e: Exception) {
                }
                if (misionID <= 0) {
                    GestorSalida.ENVIAR_BN_NADA(_perso)
                }
                GestorSalida.ENVIAR_QS_PASOS_RECOMPENSA_MISION(_perso, _perso.detalleMision(misionID))
            }
            else -> return
        }
    }

    private fun analizar_Conquista(packet: String?) {
        when (packet.charAt(1)) {
            'b' -> conquista_Balance()
            'B' -> conquista_Bonus()
            'W' -> conquista_Geoposicion(packet)
            'I' -> conquista_Defensa(packet)
            'F' -> conquista_Unirse_Defensa_Prisma(packet)
            else -> return
        }
    }

    private fun conquista_Balance() {
        val balanceMundo: Float = Mundo.getBalanceMundo(_perso)
        val balanceArea: Float = Mundo.getBalanceArea(_perso)
        GestorSalida.ENVIAR_Cb_BALANCE_CONQUISTA(_perso, "$balanceMundo;$balanceArea")
    }

    private fun conquista_Bonus() {
        val balanceMundo: Float = Mundo.getBalanceMundo(_perso)
        val bonusExp: Float = Mundo.getBonusAlinExp(_perso)
        val bonusRecolecta: Float = Mundo.getBonusAlinRecolecta(_perso)
        val bonusDrop: Float = Mundo.getBonusAlinDrop(_perso)
        GestorSalida.ENVIAR_CB_BONUS_CONQUISTA(_perso, balanceMundo.toString() + "," + balanceMundo + "," + balanceMundo + ";"
                + bonusExp + "," + bonusRecolecta + "," + bonusDrop)
    }

    private fun conquista_Defensa(packet: String?) {
        try {
            if (_perso.getAlineacion() !== Constantes.ALINEACION_BONTARIANO
                    && _perso.getAlineacion() !== Constantes.ALINEACION_BRAKMARIANO) {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return
            }
            when (packet.charAt(2)) {
                'J' -> {
                    val prisma: Prisma = _perso.getMapa().getSubArea().getPrisma()
                    if (prisma != null && prisma.getPelea() != null) {
                        prisma.actualizarAtacantesDefensores()
                    }
                    GestorSalida.ENVIAR_CIJ_INFO_UNIRSE_PRISMA(_perso,
                            if (prisma == null) "-3" else prisma.analizarPrismas(_perso.getAlineacion()))
                }
                'V' ->                    // GestorSalida.ENVIAR_CIV_CERRAR_INFO_CONQUISTA(_perso);
                    GestorSalida.ENVIAR_BN_NADA(_perso)
                else -> GestorSalida.ENVIAR_BN_NADA(_perso)
            }
        } catch (e: Exception) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
        }
    }

    private fun conquista_Geoposicion(packet: String?) {
        when (packet.charAt(2)) {
            'J' -> GestorSalida.ENVIAR_CW_INFO_MUNDO_CONQUISTA(_perso, Mundo.prismasGeoposicion(_perso.getAlineacion()))
            'V' ->                // GestorSalida.ENVIAR_CIV_CERRAR_INFO_CONQUISTA(_perso);
                GestorSalida.ENVIAR_BN_NADA(_perso)
            else -> return
        }
    }

    private fun ShowCellFight(packet: String?, perso: Personaje?) {
        if (packet.equalsIgnoreCase("CFP")) {
            GestorSalida.GAME_SEND_FIGHT_PLACES_PACKET_BOUTON(perso.getServidorSocket(), perso.getMapa()._celdasPelea)
        }
    }

    private fun conquista_Unirse_Defensa_Prisma(packet: String?) {
        when (packet.charAt(2)) {
            'J' -> {
                val prisma: Prisma = _perso.getMapa().getSubArea().getPrisma()
                if (prisma == null || prisma.getPelea() == null || _perso.getPelea() != null || prisma.getAlineacion() !== _perso.getAlineacion()) {
                    return
                }
                prisma.getPelea().unirsePelea(_perso, prisma.getID())
            }
            'P' -> ShowCellFight(packet, _perso)
            else -> return
        }
    }

    private fun analizar_Casas(packet: String?) {
        val casa: Casa = _perso.getAlgunaCasa()
        if (casa == null) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
            return
        }
        when (packet.charAt(1)) {
            'B' -> casa.comprarCasa(_perso)
            'G' -> casa.analizarCasaGremio(_perso, packet.substring(2))
            'Q' -> casa.expulsar(_perso, packet.substring(2))
            'S' -> casa.modificarPrecioVenta(_perso, packet.substring(2))
            'V' -> casa.cerrarVentanaCompra(_perso)
            else -> return
        }
    }

    private fun analizar_Koliseo(packet: String?) {
        if (!MainServidor.PARAM_KOLISEO) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1KOLISEO_DESACTIVADO")
            return
        }
        try {
            if (_perso.getPelea().getTipoPelea() === Constantes.PELEA_TIPO_KOLISEO) {
                return
            }
        } catch (e: Exception) {
        }
        when (packet.charAt(1)) {
            'A' -> koliseo_Aceptar_Invitacion(packet)
            'I' -> koliseo_Invitar(packet)
            'P' -> {
                if (_perso.getPelea() != null) {
                    GestorSalida.ENVIAR_BN_NADA(_perso)
                    return
                }
                var victorias = InfoKolo()
                if (victorias.isEmpty()) {
                    victorias = "0,0"
                }
                GestorSalida.ENVIAR_kP_PANEL_KOLISEO(_perso, victorias)
            }
            'R' -> koliseo_Rechazar_Invitacion()
            'V' -> koliseo_Expulsar(packet)
            'y' -> koliseo_Inscribirse1()
            'Y' -> koliseo_Inscribirse()
            'Z' -> koliseo_Desinscribirse()
            else -> return
        }
    }

    fun InfoKolo(): String? {
        val str = StringBuilder()
        for (rank in Mundo._RANKINGS_KOLISEO.values()) {
            if (_perso != null) if (_perso.getNombre().equals(rank.getNombre())) {
                str.append(rank.getVictorias().toString() + "," + rank.getDerrotas())
            } else {
                continue
            }
        }
        return str.toString()
    }

    private fun koliseo_Inscribirse() {
        if (_perso.getNivel() < MainServidor.MIN_NIVEL_KOLISEO) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "13")
            return
        }
        if (_perso.getTiempoPenalizacionKoliseo() > System.currentTimeMillis()) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1PENALIZACION_KOLISEO;"
                    + (_perso.getTiempoPenalizacionKoliseo() - System.currentTimeMillis()) / 60000)
            return
        }
        if (Mundo.SEGUNDOS_INICIO_KOLISEO <= 5) { // inscribirse
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1KOLISEO_INSCRIBIR_TARDE")
            return
        }
        if (_perso.esMaitre) {
            GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, "No puedes unirte a un Koliseo estando en un grupo de Maitre",
                    "C60914")
            return
        }
        if (Mundo.estaEnKoliseo(_perso)) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1KOLISEO_INSCRIBIR_REPETIDA")
            return
        }
        Mundo.addKoliseo(_perso)
        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1KOLISEO_INSCRIBIR_OK")
        var victorias = InfoKolo()
        if (victorias.isEmpty()) {
            victorias = "0,0"
        }
        if (MainServidor.REFRESCA_PANEL_KOLISEO) GestorSalida.ENVIAR_kP_PANEL_KOLISEO(_perso, victorias)
    }

    private fun koliseo_Inscribirse1() {
        if (_perso.getNivel() < MainServidor.MIN_NIVEL_KOLISEO) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "13")
            return
        }
        if (_perso.getTiempoPenalizacionKoliseo() > System.currentTimeMillis()) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1PENALIZACION_KOLISEO;"
                    + (_perso.getTiempoPenalizacionKoliseo() - System.currentTimeMillis()) / 60000)
            return
        }
        if (Mundo.SEGUNDOS_INICIO_KOLISEO <= 5) { // inscribirse
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1KOLISEO_INSCRIBIR_TARDE")
            return
        }
        if (Mundo.estaEnKoliseo(_perso)) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1KOLISEO_INSCRIBIR_REPETIDA")
            return
        }
        Mundo.addKoliseo1(_perso)
        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1KOLISEO_INSCRIBIR_OK")
        var victorias = InfoKolo()
        if (victorias.isEmpty()) {
            victorias = "0,0"
        }
        if (MainServidor.REFRESCA_PANEL_KOLISEO) GestorSalida.ENVIAR_kP_PANEL_KOLISEO(_perso, victorias)
    }

    private fun koliseo_Inscribirse2() {
        if (_perso.getNivel() < MainServidor.MIN_NIVEL_KOLISEO) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "13")
            return
        }
        if (_perso.getTiempoPenalizacionKoliseo() > System.currentTimeMillis()) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1PENALIZACION_KOLISEO;"
                    + (_perso.getTiempoPenalizacionKoliseo() - System.currentTimeMillis()) / 60000)
            return
        }
        if (Mundo.SEGUNDOS_INICIO_KOLISEO <= 5) { // inscribirse
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1KOLISEO_INSCRIBIR_TARDE")
            return
        }
        if (Mundo.estaEnKoliseo(_perso)) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1KOLISEO_INSCRIBIR_REPETIDA")
            return
        }
        Mundo.addKoliseo2(_perso)
        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1KOLISEO_INSCRIBIR_OK")
        var victorias = InfoKolo()
        if (victorias.isEmpty()) {
            victorias = "0,0"
        }
        if (MainServidor.REFRESCA_PANEL_KOLISEO) GestorSalida.ENVIAR_kP_PANEL_KOLISEO(_perso, victorias)
    }

    fun koliseo_Desinscribirse() {
        if (Mundo.SEGUNDOS_INICIO_KOLISEO <= 5) { // inscribirse
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1KOLISEO_DESINSCRIBIR_TARDE")
            return
        }
        if (!Mundo.estaEnKoliseo(_perso)) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1KOLISEO_DESINSCRIBIR_NO_EXISTE")
            return
        }
        if (_perso.getGrupoKoliseo() != null) {
            _perso.getGrupoKoliseo().dejarGrupo(_perso)
        }
        Mundo.delKoliseo(_perso)
        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1KOLISEO_DESINSCRIBIR_OK")
    }

    private fun koliseo_Invitar(packet: String?) {
        if (!Mundo.estaEnKoliseo0(_perso)) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1KOLISEO_INVITAR_TU_NO_INSCRITO")
            return
        }
        if (MainServidor.PARAM_KOLI_NO_GRUPO) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "Los Grupos de koliseo estan desactivados.")
            return
        }
        if (Mundo.SEGUNDOS_INICIO_KOLISEO <= 20) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1KOLISEO_INVITAR_TARDE")
            return
        }
        var nombre: String = packet.substring(2)
        val invitandoA: Personaje = Mundo.getPersonajePorNombre(nombre)
        if (invitandoA == null || invitandoA === _perso || !invitandoA.enLinea()) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1211")
            return
        }
        nombre = invitandoA.getNombre()
        if (!invitandoA.estaVisiblePara(_perso)) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1209")
            return
        }
        if (!invitandoA.estaVisiblePara(_perso)) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1209")
            return
        }
        if (Mundo.estaEnKoliseo(invitandoA)) {
            if (!Mundo.estaEnKoliseo0(invitandoA)) {
                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                        "No lo puedes invitar porque esta inscrito en otro koliseo diferente")
                return
            }
        }
        if (_perso.getRangoKoli() !== invitandoA.getRangoKoli()) {
            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                    "No lo puedes invitar porque no esta en el mismo rango de koliseo")
            return
        }
        if (invitandoA.getGrupoKoliseo() != null) {
            GestorSalida.ENVIAR_kIE_ERROR_INVITACION_KOLISEO(_perso, "a$nombre")
            return
        }
        if (!_perso.puedeInvitar() || !invitandoA.puedeInvitar()) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1PLAYERS_IS_BUSSY")
            return
        }
        if (_perso.getGrupoKoliseo() != null
                && _perso.getGrupoKoliseo().getCantPjs() >= MainServidor.CANTIDAD_MIEMBROS_EQUIPO_KOLISEO) {
            GestorSalida.ENVIAR_kIE_ERROR_INVITACION_KOLISEO(_perso, "f")
            return
        }
        if (!MainServidor.PARAM_PERMITIR_MISMAS_CLASES_EN_KOLISEO
                && invitandoA.getClaseID(false) === _perso.getClaseID(false)) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1KOLISEO_MISMAS_CLASES")
            return
        }
        invitandoA.setInvitador(_perso, "koliseo")
        _perso.setInvitandoA(invitandoA, "koliseo")
        GestorSalida.ENVIAR_kIK_INVITAR_KOLISEO(_perso, _perso.getNombre(), nombre)
        GestorSalida.ENVIAR_kIK_INVITAR_KOLISEO(invitandoA, _perso.getNombre(), nombre)
    }

    private fun koliseo_Aceptar_Invitacion(packet: String?) {
        if (!_perso.getTipoInvitacion().equals("koliseo")) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
            return
        }
        if (Mundo.SEGUNDOS_INICIO_KOLISEO <= 20) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1KOLISEO_GRUPO_TARDE")
            return
        }
        val invitador: Personaje = _perso.getInvitador()
        if (invitador == null || !invitador.enLinea()) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1211")
            return
        }
        var grupo: GrupoKoliseo? = invitador.getGrupoKoliseo()
        try {
            if (grupo == null) {
                grupo = GrupoKoliseo(invitador)
                invitador.setGrupoKoliseo(grupo)
            } else if (grupo.getCantPjs() >= MainServidor.CANTIDAD_MIEMBROS_EQUIPO_KOLISEO) {
                GestorSalida.ENVIAR_kIE_ERROR_INVITACION_KOLISEO(_perso, "f")
                return
            }
            if (!Mundo.estaEnKoliseo(_perso)) {
                koliseo_Inscribirse()
            }
            grupo.addPersonaje(_perso)
            _perso.setGrupoKoliseo(grupo)
            GestorSalida.ENVIAR_kA_ACEPTAR_INVITACION_KOLISEO(invitador)
            GestorSalida.ENVIAR_kA_ACEPTAR_INVITACION_KOLISEO(_perso)
        } catch (e: Exception) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
        }
        invitador.setInvitandoA(null, "")
        _perso.setInvitador(null, "")
    }

    private fun koliseo_Rechazar_Invitacion() {
        _perso.rechazarKoliseo()
    }

    private fun koliseo_Expulsar(packet: String?) { // usar este packet para atacar
        try {
            _perso.getGrupoKoliseo().dejarGrupo(_perso)
            GestorSalida.ENVIAR_kV_DEJAR_KOLISEO(_perso)
        } catch (e: Exception) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
        }
    }

    /*
	 * private void koliseo_Invitar(final String packet) { if
	 * (!Mundo.estaEnKoliseo(_perso.getID())) {
	 * GestorSalida.ENVIAR_Im_INFORMACION(_perso,
	 * "1KOLISEO_INVITAR_TU_NO_INSCRITO"); return; } if
	 * (Mundo.SEGUNDOS_INICIO_KOLISEO <= 20) {
	 * GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1KOLISEO_INVITAR_TARDE"); return;
	 * } final String nombre = packet.substring(2); final Personaje invitandoA =
	 * Mundo.getPersonajePorNombre(nombre); if (invitandoA == null || invitandoA ==
	 * _perso || !invitandoA.enLinea()) { GestorSalida.ENVIAR_Im_INFORMACION(_perso,
	 * "1211"); return; } if (!invitandoA.estaVisiblePara(_perso)) {
	 * GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1209"); return; } if
	 * (!Mundo.estaEnKoliseo(invitandoA.getID())) {
	 * GestorSalida.ENVIAR_Im_INFORMACION(_perso,
	 * "1KOLISEO_INVITAR_EL_NO_INSCRITO"); return; } if
	 * (invitandoA.getGrupoKoliseo() != null) {
	 * GestorSalida.ENVIAR_kIE_ERROR_INVITACION_KOLISEO(_perso, "a" + nombre);
	 * return; } if (!_perso.puede() || !invitandoA.puedeInvitar()) {
	 * GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1PLAYERS_IS_BUSSY"); return; } if
	 * (_perso.getGrupoKoliseo() != null && _perso.getGrupoKoliseo() .getCantPjs()
	 * >= MainServidor.CANTIDAD_MIEMBROS_EQUIPO_KOLISEO) {
	 * GestorSalida.ENVIAR_kIE_ERROR_INVITACION_KOLISEO(_perso, "f"); return; } if
	 * (!MainServidor.PARAM_PERMITIR_MISMAS_CLASES_EN_KOLISEO &&
	 * invitandoA.getClaseID(false) == _perso.getClaseID( false)) {
	 * GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1KOLISEO_MISMAS_CLASES"); return;
	 * } invitandoA.setInvitador(_perso, "koliseo");
	 * _perso.setInvitandoA(invitandoA, "koliseo");
	 * GestorSalida.ENVIAR_kIK_INVITAR_KOLISEO(_perso, _perso.getNombre(), nombre);
	 * GestorSalida.ENVIAR_kIK_INVITAR_KOLISEO(invitandoA, _perso.getNombre(),
	 * nombre); }
	 *
	 * private void koliseo_Aceptar_Invitacion(final String packet) { if
	 * (!_perso.getTipoInvitacion().equals("koliseo")) {
	 * GestorSalida.ENVIAR_BN_NADA(_perso); return; } final Personaje invitador =
	 * _perso.getInvitador(); if (invitador == null || !invitador.enLinea()) {
	 * GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1211"); return; } GrupoKoliseo
	 * grupo = invitador.getGrupoKoliseo(); try { if (grupo == null) { grupo = new
	 * GrupoKoliseo(invitador); invitador.setGrupoKoliseo(grupo); } else if
	 * (grupo.getCantPjs() >= MainServidor.CANTIDAD_MIEMBROS_EQUIPO_KOLISEO) {
	 * GestorSalida.ENVIAR_kIE_ERROR_INVITACION_KOLISEO(_perso, "f"); return; }
	 * grupo.addPersonaje(_perso,grupo);
	 *
	 * GestorSalida.ENVIAR_kA_ACEPTAR_INVITACION_KOLISEO(_perso); } catch (final
	 * Exception e) { GestorSalida.ENVIAR_BN_NADA(_perso); }
	 * invitador.setInvitandoA(null, ""); _perso.setInvitador(null, ""); }
	 *
	 * private void koliseo_Rechazar_Invitacion() { _perso.rechazarKoliseo(); }
	 *
	 * private void koliseo_Expulsar(final String packet) {// usar este packet para
	 * atacar try { _perso.getGrupoKoliseo().dejarGrupo(_perso);
	 * GestorSalida.ENVIAR_kV_DEJAR_KOLISEO(_perso); } catch (final Exception e) {
	 * GestorSalida.ENVIAR_BN_NADA(_perso); } }
	 */
    private fun analizar_Claves(packet: String?) {
        try {
            when (packet.charAt(1)) {
                'V' -> if (_perso.getConsultarCofre() != null) {
                    _perso.getConsultarCofre().cerrarVentanaClave(_perso)
                } else if (_perso.getConsultarCasa() != null) {
                    _perso.getConsultarCasa().cerrarVentanaClave(_perso)
                }
                'K' -> panel_Claves(packet)
                else -> return
            }
        } catch (e: Exception) {
        }
    }

    private fun panel_Claves(packet: String?) {
        try {
            when (packet.charAt(2)) {
                '0' -> if (_perso.getConsultarCofre() != null) {
                    _perso.getConsultarCofre().intentarAcceder(_perso, packet.substring(4))
                } else if (_perso.getAlgunaCasa() != null) {
                    _perso.getAlgunaCasa().intentarAcceder(_perso, packet.substring(4))
                }
                '1' -> if (_perso.getConsultarCofre() != null) {
                    _perso.getConsultarCofre().modificarClave(_perso, packet.substring(4))
                } else if (_perso.getAlgunaCasa() != null) {
                    _perso.getAlgunaCasa().modificarClave(_perso, packet.substring(4))
                }
                else -> return
            }
        } catch (e: Exception) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
        }
    }

    private fun analizar_Enemigos(packet: String?) {
        when (packet.charAt(1)) {
            'A' -> enemigo_Agregar(packet)
            'D' -> enemigo_Borrar(packet)
            'L' -> GestorSalida.ENVIAR_iL_LISTA_ENEMIGOS(_perso)
            else -> return
        }
    }

    private fun enemigo_Agregar(packet: String?) {
        var id = -1
        var nombre: String? = ""
        when (packet.charAt(2)) {
            '%' -> {
                nombre = packet.substring(3)
                val perso: Personaje = Mundo.getPersonajePorNombre(nombre)
                if (perso == null) {
                    GestorSalida.ENVIAR_FD_BORRAR_AMIGO(_perso, "Ef")
                    return
                }
                id = perso.getCuentaID()
            }
            '*' -> {
                nombre = packet.substring(3)
                val cuenta: Cuenta = Mundo.getCuentaPorApodo(nombre)
                if (cuenta == null) {
                    GestorSalida.ENVIAR_FD_BORRAR_AMIGO(_perso, "Ef")
                    return
                }
                id = cuenta.getID()
            }
            else -> {
                nombre = packet.substring(2)
                val perso2: Personaje = Mundo.getPersonajePorNombre(nombre)
                if (perso2 == null || !perso2.enLinea()) {
                    GestorSalida.ENVIAR_FD_BORRAR_AMIGO(_perso, "Ef")
                    return
                }
                id = perso2.getCuentaID()
            }
        }
        _cuenta.addEnemigo(nombre, id)
    }

    private fun enemigo_Borrar(packet: String?) {
        var id = -1
        var nombre: String? = ""
        when (packet.charAt(2)) {
            '%' -> {
                nombre = packet.substring(3)
                val pj: Personaje = Mundo.getPersonajePorNombre(nombre)
                if (pj == null) {
                    GestorSalida.ENVIAR_FD_BORRAR_AMIGO(_perso, "Ef")
                    return
                }
                id = pj.getCuentaID()
            }
            '*' -> {
                nombre = packet.substring(3)
                val cuenta: Cuenta = Mundo.getCuentaPorApodo(nombre)
                if (cuenta == null) {
                    GestorSalida.ENVIAR_FD_BORRAR_AMIGO(_perso, "Ef")
                    return
                }
                id = cuenta.getID()
            }
            else -> {
                nombre = packet.substring(2)
                val perso: Personaje = Mundo.getPersonajePorNombre(nombre)
                if (perso == null || !perso.enLinea()) {
                    GestorSalida.ENVIAR_FD_BORRAR_AMIGO(_perso, "Ef")
                    return
                }
                id = perso.getCuentaID()
            }
        }
        _cuenta.borrarEnemigo(id)
    }

    private fun analizar_Oficios(packet: String?) {
        when (packet.charAt(1)) {
            'O' -> {
                val infos: Array<String?> = packet.substring(2).split(Pattern.quote("|"))
                val posOficio: Byte = Byte.parseByte(infos[0])
                val opciones: Int = Integer.parseInt(infos[1])
                val slots: Byte = Byte.parseByte(infos[2])
                val statOficio: StatOficio = _perso.getStatsOficios().get(posOficio) ?: return
                statOficio.setOpciones(opciones)
                statOficio.setSlotsPublico(slots)
                GestorSalida.ENVIAR_JO_OFICIO_OPCIONES(_perso, statOficio)
            }
            else -> return
        }
    }

    private fun analizar_Zonas(packet: String?) {
        if (_perso.getPelea() != null) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
            return
        }
        when (packet.charAt(1)) {
            'U' -> {
                try {
                    _perso.usarZonas(Short.parseShort(packet.substring(2)))
                } catch (e: Exception) {
                }
                GestorSalida.ENVIAR_zV_CERRAR_ZONAS(_perso)
            }
            'V' -> GestorSalida.ENVIAR_zV_CERRAR_ZONAS(_perso)
            else -> return
        }
    }

    private fun analizar_Areas(packet: String?) {
        if (_perso.getPelea() != null) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
            return
        }
        when (packet.charAt(1)) {
            'U' -> zaap_Usar(packet)
            'u' -> zaapi_Usar(packet)
            'v' -> GestorSalida.ENVIAR_Wv_CERRAR_ZAPPI(_perso)
            'V' -> GestorSalida.ENVIAR_WV_CERRAR_ZAAP(_perso)
            'w' -> GestorSalida.ENVIAR_Ww_CERRAR_PRISMA(_perso)
            'p' -> prisma_Usar(packet)
            else -> return
        }
    }

    private fun zaap_Usar(packet: String?) {
        try {
            _perso.usarZaap(Short.parseShort(packet.substring(2)))
        } catch (e: Exception) {
        }
    }

    private fun zaapi_Usar(packet: String?) {
        try {
            _perso.usarZaapi(Short.parseShort(packet.substring(2)))
        } catch (e: Exception) {
        }
    }

    private fun prisma_Usar(packet: String?) {
        try {
            _perso.usarPrisma(Short.parseShort(packet.substring(2)))
        } catch (e: Exception) {
        }
    }

    private fun analizar_Gremio(packet: String?) {
        val gremio: Gremio = _perso.getGremio()
        if (packet.charAt(1) !== 'C' && packet.charAt(1) !== 'V' && packet.charAt(1) !== 'J') {
            if (gremio == null) {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return
            }
        }
        val estb: Char
        try {
            estb = packet.charAt(1)
        } catch (e: Exception) {
            return
        }
        when (estb) {
            'B' -> gremio_Stats(packet)
            'b' -> gremio_Hechizos(packet)
            'C' -> gremio_Crear(packet)
            'f' -> {
                if (!_perso.estaDisponible(true, true)) {
                    GestorSalida.ENVIAR_BN_NADA(_perso)
                    return
                }
                gremio_Cercado(packet.substring(2))
            }
            'F' -> gremio_Retirar_Recaudador(packet.substring(2))
            'h' -> {
                if (!_perso.estaDisponible(true, true)) {
                    GestorSalida.ENVIAR_BN_NADA(_perso)
                    return
                }
                gremio_Casa(packet.substring(2))
            }
            'H' -> gremio_Poner_Recaudador()
            'I' -> gremio_Informacion(packet)
            'J' -> gremio_Invitar(packet)
            'K' -> gremio_Expulsar(packet.substring(2))
            'P' -> gremio_Promover_Rango(packet.substring(2))
            'T' -> gremio_Pelea_Recaudador(packet.substring(2))
            'V' -> gremio_Cancelar_Creacion()
            else -> return
        }
    }

    private fun gremio_Stats(packet: String?) {
        val gremio: Gremio = _perso.getGremio()
        if (!_perso.getMiembroGremio().puede(Constantes.G_MODIF_BOOST)) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1101")
            return
        }
        when (packet.charAt(2)) {
            'p' -> {
                if (gremio.getCapital() < 1 || gremio.getStatRecolecta(176) >= 500) {
                    return
                }
                gremio.addCapital(-1)
                gremio.addStat(176, 1)
            }
            'x' -> {
                if (gremio.getCapital() < 1 || gremio.getStatRecolecta(124) >= 400) {
                    return
                }
                gremio.addCapital(-1)
                gremio.addStat(124, 1)
            }
            'o' -> {
                if (gremio.getCapital() < 1 || gremio.getStatRecolecta(158) >= 5000) {
                    return
                }
                gremio.addCapital(-1)
                gremio.addStat(158, 20)
            }
            'k' -> {
                if (gremio.getCapital() < 10 || gremio.getNroMaxRecau() >= 50) {
                    return
                }
                gremio.addCapital(-10)
                gremio.setNroMaxRecau((gremio.getNroMaxRecau() + 1) as Byte)
            }
            else -> return
        }
        GestorSalida.ENVIAR_gIB_GREMIO_INFO_BOOST(_perso, gremio.analizarRecauAGremio())
    }

    private fun gremio_Hechizos(packet: String?) {
        val gremio: Gremio = _perso.getGremio()
        if (gremio.getCapital() < 5) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
            return
        }
        if (!_perso.getMiembroGremio().puede(Constantes.G_MODIF_BOOST)) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1101")
            return
        }
        val hechizoID: Int = Integer.parseInt(packet.substring(2))
        if (gremio.boostHechizo(hechizoID)) {
            gremio.addCapital(-5)
            GestorSalida.ENVIAR_gIB_GREMIO_INFO_BOOST(_perso, _perso.getGremio().analizarRecauAGremio())
        } // probar los hechizos de recaudador porq no ataca
    }

    private fun gremio_Pelea_Recaudador(packet: String?) {
        try {
            val recaudadorID: Int = Integer.parseInt(packet.substring(1))
            when (packet.charAt(0)) {
                'J' -> {
                    if (!_perso.estaDisponible(true, true)) {
                        GestorSalida.ENVIAR_BN_NADA(_perso)
                        return
                    }
                    Mundo.getRecaudador(recaudadorID).getPelea().unirsePelea(_perso, recaudadorID)
                }
                'V' -> {
                    val p: Pelea = Mundo.getRecaudador(recaudadorID).getPelea()
                    if (p.getFase() === Constantes.PELEA_FASE_POSICION) {
                        p.retirarsePelea(_perso.getID(), 0, false)
                    }
                }
                else -> return
            }
        } catch (e: Exception) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
        }
    }

    private fun gremio_Retirar_Recaudador(packet: String?) {
        val recaudadorID: Int = Integer.parseInt(packet)
        val recaudador: Recaudador = Mundo.getRecaudador(recaudadorID)
        if (recaudador == null || recaudador.getPelea() != null || _perso.getGremio() == null) {
            GestorSalida.ENVIAR_BN_NADA(_perso, "NO SE PUEDE RETIRAR")
            return
        }
        if (recaudador.getGremio().getID() !== _perso.getGremio().getID()) {
            GestorSalida.ENVIAR_BN_NADA(_perso, "NO ES DEL GREMIO")
            return
        }
        if (!_perso.getMiembroGremio().puede(Constantes.G_RECOLECTAR_RECAUDADOR)) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1101")
            return
        }
        val str: String = _perso.getGremio().analizarRecaudadores()
        val str2: String = recaudador.stringPanelInfo(_perso)
        for (p in _perso.getGremio().getMiembros()) {
            if (p.enLinea()) {
                GestorSalida.ENVIAR_gITM_GREMIO_INFO_RECAUDADOR(p, str)
                GestorSalida.ENVIAR_gT_PANEL_RECAUDADORES_GREMIO(p, 'R', str2)
            }
        }
        recaudador.borrarRecaudador()
    }

    private fun gremio_Poner_Recaudador() {
        if (!_perso.estaDisponible(true, true)) {
            GestorSalida.ENVIAR_BN_NADA(_perso, "PONER_RECAUDADOR NO DISPONIBLE")
            return
        }
        val gremio: Gremio = _perso.getGremio()
        if (gremio.getCantidadMiembros() < 10) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1NOT_ENOUGHT_MEMBERS_IN_GUILD")
            return
        }
        if (!_perso.getMiembroGremio().puede(Constantes.G_PONER_RECAUDADOR)) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1101")
            return
        }
        val mapa: Mapa = _perso.getMapa()
        if (mapa.mapaNoRecaudador() || mapa.esArena() || !mapa.getTrabajos().isEmpty()
                || Mundo.getCasaDentroPorMapa(mapa.getID()) != null || mapa.getSubArea().getArea().getSuperArea().getID() === 3) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "113")
            return
        }
        if (gremio.getCantRecaudadores() >= gremio.getNroMaxRecau()) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1CANT_HIRE_MAX_TAXCOLLECTORS") // WTFFF !!!!!!
            return
        }
        if (mapa.getRecaudador() != null) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1ALREADY_TAXCOLLECTOR_ON_MAP")
            return
        }
        if (!gremio.puedePonerRecaudadorMapa(mapa.getID())) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1CANT_PUT_TAXCOLLECTOR_FOR_TIME")
            return
        }
        if (MainServidor.PARAM_LIMITAR_RECAUDADOR_GREMIO_POR_ZONA
                && !Mundo.puedePonerRecauEnZona(mapa.getSubArea().getID(), gremio.getID())) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1168;" + MainServidor.MAX_RECAUDADORES_POR_ZONA)
            return
        }
        if (!MainServidor.MODO_ALL_OGRINAS) {
            val precio: Int = MainServidor.VALOR_BASE_RECAUDADOR + 10 * gremio.getNivel()
            if (precio <= 0 || _perso.getKamas() < precio) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "182")
                return
            }
            _perso.addKamas(-precio, true, true)
        }
        val random1: String = Integer.toString(Formulas.getRandomInt(1, 129), 36)
        val random2: String = Integer.toString(Formulas.getRandomInt(1, 227), 36)
        val recau = Recaudador(Mundo.sigIDRecaudador(), mapa.getID(), _perso.getCelda().getID(), 3.toByte(), gremio.getID(), random1, random2, "", 0, 0, 0, System.currentTimeMillis(), _perso.getID())
        Mundo.addRecaudador(recau)
        gremio.addUltRecolectaMapa(mapa.getID())
        GestorSalida.ENVIAR_GM_RECAUDADOR_A_MAPA(mapa, "+" + recau.stringGM())
        val str: String = gremio.analizarRecaudadores()
        val str2: String = recau.stringPanelInfo(_perso)
        for (p in gremio.getMiembros()) {
            if (p.enLinea()) {
                GestorSalida.ENVIAR_gITM_GREMIO_INFO_RECAUDADOR(p, str)
                GestorSalida.ENVIAR_gT_PANEL_RECAUDADORES_GREMIO(p, 'S', str2)
            }
        }
    }

    private fun gremio_Cercado(packet: String?) {
        if (!_perso.estaDisponible(true, true)) {
            return
        }
        var hola: String? = ""
        hola = try {
            packet
        } catch (e: Exception) {
            return
        }
        val mapaID: Short = Short.parseShort(hola)
        val cercado: Cercado = Mundo.getMapa(mapaID).getCercado()
        if (cercado.getGremio().getID() !== _perso.getGremio().getID()) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1135")
            return
        }
        val celdaID: Short = Mundo.getCeldaCercadoPorMapaID(mapaID)
        if (_perso.tenerYEliminarObjPorModYCant(9035, 1)) {
            _perso.teleport(mapaID, celdaID)
        } else {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1159")
            return
        }
    }

    private fun gremio_Casa(packet: String?) {
        val casaID: Int = Integer.parseInt(packet)
        val casa: Casa = Mundo.getCasas().get(casaID) ?: return
        if (_perso.getGremio().getID() !== casa.getGremioID()) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1135")
            return
        }
        if (!casa.tieneDerecho(Constantes.C_TELEPORT_GREMIO)) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1136")
            return
        }
        if (!_perso.estaDisponible(true, true)) {
            return
        }
        if (_perso.tenerYEliminarObjPorModYCant(8883, 1)) { // pocima de la casa del gremio
            _perso.teleport(casa.getMapaIDDentro(), casa.getCeldaIDDentro())
        } else {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1137")
            return
        }
    }

    private fun gremio_Cancelar_Creacion() {
        _perso.setOcupado(false)
        GestorSalida.ENVIAR_gV_CERRAR_PANEL_GREMIO(_perso)
    }

    private fun gremio_Promover_Rango(packet: String?) {
        try {
            val infos: Array<String?> = packet.split(Pattern.quote("|"))
            val id: Int = Integer.parseInt(infos[0])
            var rango: Int = Integer.parseInt(infos[1])
            var xpDonada: Int = Integer.parseInt(infos[2])
            var derecho: Int = Integer.parseInt(infos[3])
            val perso: Personaje = Mundo.getPersonaje(id)
            val cambiador: MiembroGremio = _perso.getMiembroGremio()
            if (perso == null || perso.getGremio() == null) {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return
            }
            val aCambiar: MiembroGremio = perso.getMiembroGremio()
            if (aCambiar == null || _perso.getGremio().getID() !== perso.getGremio().getID()) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1210")
                return
            }
            if (cambiador.getRango() === 1) { // lider de gremio
                if (cambiador.getID() === aCambiar.getID()) {
                    rango = -1
                    derecho = -1
                } else if (rango == 1) {
                    derecho = 1
                    xpDonada = -1
                    cambiador.setTodosDerechos(2, xpDonada, Constantes.G_TODOS_LOS_DERECHOS)
                }
            } else {
                if (aCambiar.getRango() === 1) {
                    GestorSalida.ENVIAR_BN_NADA(_perso, "CAMBIAR RANGO A LIDER")
                    return
                } else {
                    if (xpDonada >= 0 && xpDonada != aCambiar.getPorcXpDonada()) {
                        if (cambiador.getID() === aCambiar.getID()) {
                            if (!cambiador.puede(Constantes.G_SU_XP_DONADA)) {
                                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1101")
                                return
                            }
                        } else if (!cambiador.puede(Constantes.G_TODAS_XP_DONADAS)) {
                            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1101")
                            return
                        }
                    }
                    if (rango >= 2) {
                        if (rango != aCambiar.getRango() && !cambiador.puede(Constantes.G_MODIF_RANGOS)) {
                            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1101")
                            return
                        }
                    }
                    if (derecho >= 2) {
                        if (derecho != aCambiar.getDerechos() && !cambiador.puede(Constantes.G_MODIF_DERECHOS)) {
                            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1101")
                            return
                        }
                    }
                }
            }
            aCambiar.setTodosDerechos(rango, xpDonada, derecho)
            GestorSalida.ENVIAR_gS_STATS_GREMIO(_perso, _perso.getMiembroGremio())
            if (perso != null && perso.getID() !== _perso.getID()) {
                GestorSalida.ENVIAR_gS_STATS_GREMIO(perso, perso.getMiembroGremio())
            }
        } catch (e: Exception) {
        }
    }

    private fun gremio_Expulsar(nombre: String?) {
        var nombre = nombre
        val persoExpulsar: Personaje = Mundo.getPersonajePorNombre(nombre)
        if (persoExpulsar == null) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
            return
        }
        nombre = persoExpulsar.getNombre()
        var gremio: Gremio = persoExpulsar.getGremio()
        if (gremio == null) {
            gremio = Mundo.getGremio(_perso.getGremio().getID())
        }
        val aExpulsar: MiembroGremio = gremio.getMiembro(persoExpulsar.getID())
        if (aExpulsar == null || aExpulsar.getGremio().getID() !== _perso.getGremio().getID()) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
            return
        }
        if (gremio.getID() !== _perso.getGremio().getID()) {
            GestorSalida.ENVIAR_gK_GREMIO_BAN(_perso, "Ea")
            return
        }
        val expulsador: MiembroGremio = _perso.getMiembroGremio()
        if (!expulsador.puede(Constantes.G_BANEAR) && expulsador.getID() !== aExpulsar.getID()) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1101")
            return
        }
        if (expulsador.getID() !== aExpulsar.getID()) {
            if (aExpulsar.getRango() === 1) {
                return
            }
            gremio.expulsarMiembro(aExpulsar.getID())
            GestorSalida.ENVIAR_gK_GREMIO_BAN(_perso, "K" + _perso.getNombre().toString() + "|" + nombre)
            if (persoExpulsar != null) {
                GestorSalida.ENVIAR_gK_GREMIO_BAN(persoExpulsar, "K" + _perso.getNombre())
            }
        } else {
            if (expulsador.getRango() === 1 && gremio.getMiembros().size() > 1) {
                for (pj in gremio.getMiembros()) {
                    gremio.expulsarMiembro(pj.getID())
                }
            } else {
                gremio.expulsarMiembro(_perso.getID())
            }
            if (gremio.getMiembros().isEmpty()) {
                Mundo.eliminarGremio(gremio)
            }
            GestorSalida.ENVIAR_gK_GREMIO_BAN(_perso, "K$nombre|$nombre")
        }
    }

    private fun gremio_Invitar(packet: String?) {
        when (packet.charAt(2)) {
            'R' -> gremio_Invitar_Unirse(packet)
            'E' -> gremio_Invitar_Rechazar()
            'K' -> gremio_Invitar_Aceptar()
            else -> return
        }
    }

    private fun gremio_Invitar_Unirse(packet: String?) {
        val invitandoA: Personaje = Mundo.getPersonajePorNombre(packet.substring(3))
        if (invitandoA == null || invitandoA === _perso) {
            GestorSalida.ENVIAR_gJ_GREMIO_UNIR(_perso, "Eu")
            return
        }
        if (!invitandoA.enLinea()) {
            GestorSalida.ENVIAR_gJ_GREMIO_UNIR(_perso, "Eu")
            return
        }
        if (!invitandoA.estaVisiblePara(_perso)) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1209")
            return
        }
        if (invitandoA.getGremio() != null) {
            GestorSalida.ENVIAR_gJ_GREMIO_UNIR(_perso, "Ea")
            return
        }
        if (!_perso.puedeInvitar() || !invitandoA.puedeInvitar()
                || invitandoA.getCuenta().getSocket().getRealizandoAccion()) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1ERROR_209")
            return
        }
        if (!_perso.getMiembroGremio().puede(Constantes.G_INVITAR)) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1101")
            return
        }
        if (MainServidor.PARAM_LIMITE_MIEMBROS_GREMIO) {
            val maxMiembros: Int = _perso.getGremio().getMaxMiembros()
            if (_perso.getGremio().getCantidadMiembros() >= maxMiembros) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "155;$maxMiembros")
                return
            }
        }
        _perso.setInvitandoA(invitandoA, "gremio")
        invitandoA.setInvitador(_perso, "gremio")
        GestorSalida.ENVIAR_gJ_GREMIO_UNIR(_perso, "R" + invitandoA.getNombre())
        GestorSalida.ENVIAR_gJ_GREMIO_UNIR(invitandoA,
                "r" + _perso.getID().toString() + "|" + _perso.getNombre().toString() + "|" + _perso.getGremio().getNombre())
    }

    private fun gremio_Invitar_Aceptar() {
        if (!_perso.getTipoInvitacion().equals("gremio")) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
            return
        }
        val invitador: Personaje = _perso.getInvitador()
        if (invitador == null) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
            return
        }
        val gremio: Gremio = invitador.getGremio()
        if (gremio == null) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
            return
        }
        if (MainServidor.PARAM_LIMITE_MIEMBROS_GREMIO) {
            var maxMiembros: Int = 40 + gremio.getNivel()
            if (MainServidor.LIMITE_MIEMBROS_GREMIO > 0) {
                maxMiembros = MainServidor.LIMITE_MIEMBROS_GREMIO
            }
            if (gremio.getCantidadMiembros() >= maxMiembros) {
                GestorSalida.ENVIAR_Im_INFORMACION(invitador, "155;$maxMiembros")
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "155;$maxMiembros")
                return
            }
        }
        val miembro: MiembroGremio = gremio.addMiembro(_perso.getID(), 0, 0, 0.toByte(), 0)
        _perso.setMiembroGremio(miembro)
        invitador.setInvitandoA(null, "")
        _perso.setInvitador(null, "")
        GestorSalida.ENVIAR_gJ_GREMIO_UNIR(invitador, "Ka" + _perso.getNombre())
        GestorSalida.ENVIAR_gS_STATS_GREMIO(_perso, miembro)
        GestorSalida.ENVIAR_gJ_GREMIO_UNIR(_perso, "Kj")
        _perso.cambiarRopaVisual()
    }

    private fun gremio_Invitar_Rechazar() {
        _perso.rechazarGremio()
    }

    private fun gremio_Informacion(packet: String?) {
        val gremio: Gremio = _perso.getGremio()
        if (gremio == null) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
            return
        }
        when (packet.charAt(2)) {
            'B' -> GestorSalida.ENVIAR_gIB_GREMIO_INFO_BOOST(_perso, gremio.analizarRecauAGremio())
            'F' -> GestorSalida.ENVIAR_gIF_GREMIO_INFO_CERCADOS(_perso, gremio.analizarInfoCercados())
            'G' -> GestorSalida.ENVIAR_gIG_GREMIO_INFO_GENERAL(_perso, gremio)
            'H' -> GestorSalida.ENVIAR_gIH_GREMIO_INFO_CASAS(_perso, Casa.stringCasaGremio(gremio.getID()))
            'M' -> GestorSalida.ENVIAR_gIM_GREMIO_INFO_MIEMBROS(_perso, gremio, '+')
            'T' -> {
                var c = 'a'
                try {
                    c = packet.charAt(3)
                } catch (e: Exception) {
                }
                when (c) {
                    'V' -> {
                    }
                    else -> {
                        GestorSalida.ENVIAR_gITM_GREMIO_INFO_RECAUDADOR(_perso, gremio.analizarRecaudadores())
                        gremio.actualizarAtacantesDefensores()
                    }
                }
            }
            else -> return
        }
    }

    private fun gremio_Crear(packet: String?) {
        if (_perso.getMiembroGremio() != null) {
            GestorSalida.ENVIAR_gC_CREAR_PANEL_GREMIO(_perso, "Ea")
            return
        }
        try {
            val infos: Array<String?> = packet.substring(2).split(Pattern.quote("|"))
            val escudoID: String = Integer.toString(Integer.parseInt(infos[0]), 36)
            val colorEscudo: String = Integer.toString(Integer.parseInt(infos[1]), 36)
            val emblemaID: String = Integer.toString(Integer.parseInt(infos[2]), 36)
            val colorEmblema: String = Integer.toString(Integer.parseInt(infos[3]), 36)
            val nombre: String = infos[4].substring(0, 1).toUpperCase() + infos[4].substring(1).toLowerCase()
            if (Mundo.nombreGremioUsado(nombre)) {
                GestorSalida.ENVIAR_gC_CREAR_PANEL_GREMIO(_perso, "Ean")
                return
            }
            if (nombre.length() < 2 || nombre.length() > 30) {
                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "1NAME_GUILD_MANY_LONG")
                return
            }
            var esValido = true
            val abcMin = "abcdefghijklmnopqrstuvwxyz- '"
            var cantSimbol: Byte = 0
            var cantLetras: Byte = 0
            var letra_A = ' '
            var letra_B = ' '
            for (letra in nombre.toLowerCase().toCharArray()) {
                if (!abcMin.contains(letra.toString() + "")) {
                    esValido = false
                    break
                }
                if (letra == letra_A && letra == letra_B) {
                    esValido = false
                    break
                }
                if (letra != '-') {
                    letra_A = letra_B
                    letra_B = letra
                    cantLetras++
                } else {
                    if (cantLetras.toInt() == 0 || cantSimbol > 0) {
                        esValido = false
                        break
                    }
                    cantSimbol++
                }
            }
            if (!esValido) {
                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "1NAME_GUILD_USE_CHARACTERS_INVALIDS")
                return
            }
            val emblema = "$escudoID,$colorEscudo,$emblemaID,$colorEmblema"
            if (Mundo.emblemaGremioUsado(emblema)) {
                GestorSalida.ENVIAR_gC_CREAR_PANEL_GREMIO(_perso, "Eae")
                return
            }
            if (!_perso.tieneObjPorModYCant(1575, 1)) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "14")
                return
            }
            _perso.restarObjPorModYCant(1575, 1) // quitar la gremiologema
            val gremio = Gremio(_perso, nombre, emblema)
            Mundo.addGremio(gremio)
            GestorSQL.INSERT_GREMIO(gremio)
            val miembro: MiembroGremio = gremio.addMiembro(_perso.getID(), 0, 0, 0.toByte(), 0)
            miembro.setTodosDerechos(1, 0.toByte(), 1)
            _perso.setMiembroGremio(miembro)
            GestorSalida.ENVIAR_gS_STATS_GREMIO(_perso, miembro)
            GestorSalida.ENVIAR_gC_CREAR_PANEL_GREMIO(_perso, "K")
            GestorSalida.ENVIAR_gV_CERRAR_PANEL_GREMIO(_perso)
            _perso.setOcupado(false)
            _perso.cambiarRopaVisual()
        } catch (e: Exception) {
        }
    }

    private fun analizar_Canal(packet: String?) {
        when (packet.charAt(1)) {
            'C' -> canal_Cambiar(packet)
            else -> return
        }
    }

    private fun canal_Cambiar(packet: String?) {
        var chan = ""
        chan = try {
            packet.charAt(3).toString() + ""
        } catch (e: Exception) {
            return
        }
        when (packet.charAt(2)) {
            '+' -> _perso.addCanal(chan)
            '-' -> _perso.removerCanal(chan)
        }
        GestorSQL.SALVAR_PERSONAJE(_perso, false)
    }

    private fun analizar_Montura(packet: String?) {
        when (packet.charAt(1)) {
            'b' -> montura_Comprar_Cercado(packet)
            'c' -> montura_Castrar()
            'd' -> montura_Descripcion(packet)
            'f' -> montura_Liberar()
            'n' -> montura_Nombre(packet.substring(2))
            'o' -> montura_Borrar_Objeto_Crianza(packet)
            'p' -> montura_Descripcion(packet)
            'r' -> montura_Montar()
            's' -> montura_Vender_Cercado(packet)
            'v' -> GestorSalida.ENVIAR_Rv_MONTURA_CERRAR(_perso)
            'x' -> montura_CambiarXP_Donada(packet)
            'Z' ->                // try {
                // if (_perso.getMontura().getColor() == 75) {
                // GestorSalida.ENVIAR_Rz_STATS_VIP(_perso, _perso.getMontura().getStatsVIP());
                // } else {
                // GestorSalida.ENVIAR_BN_NADA(_perso);
                // }
                // } catch (final Exception e) {
                GestorSalida.ENVIAR_BN_NADA(_perso)
            else -> {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return
            }
        }
    }

    private fun montura_Vender_Cercado(packet: String?) {
        GestorSalida.ENVIAR_Rv_MONTURA_CERRAR(_perso)
        val cercado: Cercado = _perso.getMapa().getCercado()
        if (cercado.esPublico()) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "194")
            return
        }
        if (cercado.getDueñoID() !== _perso.getID()) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "195")
            return
        }
        var precio = 0
        try {
            precio = Integer.parseInt(packet.substring(2))
        } catch (e: Exception) {
        }
        if (precio < 0) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
            return
        }
        cercado.setPrecioPJ(precio)
        for (p in _perso.getMapa().getArrayPersonajes()) {
            GestorSalida.ENVIAR_Rp_INFORMACION_CERCADO(p, cercado)
        }
    }

    private fun montura_Comprar_Cercado(packet: String?) {
        GestorSalida.ENVIAR_Rv_MONTURA_CERRAR(_perso)
        val cercado: Cercado = _perso.getMapa().getCercado()
        val vendedor: Personaje = Mundo.getPersonaje(cercado.getDueñoID())
        if (cercado.esPublico()) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "196")
            return
        }
        if (cercado.getPrecio() <= 0) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "197")
            return
        }
        if (_perso.getKamas() < cercado.getPrecio()) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1128;" + cercado.getPrecio())
            return
        }
        if (_perso.getGremio() == null) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1135")
            return
        }
        // if (_perso.getMiembroGremio().getRango() != 1) {
        // GestorSalida.ENVIAR_Im_INFORMACION(_perso, "198");
        // return;
        // }
        if (Mundo.getCantCercadosGremio(
                        _perso.getGremio().getID()) >= Math.ceil(_perso.getGremio().getNivel() / 10f) as Byte) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1103")
            return
        }
        _perso.addKamas(-cercado.getPrecio(), true, true)
        if (vendedor != null) {
            vendedor.addKamasBanco(cercado.getPrecio())
            val tempPerso: Personaje = vendedor.getCuenta().getTempPersonaje()
            if (tempPerso != null) {
                GestorSalida.ENVIAR_M1_MENSAJE_SERVER_SVR_MUESTRA_INSTANTANEO(tempPerso, 17,
                        cercado.getPrecio().toString() + ";" + _perso.getGremio().getNombre(), "")
            } else {
                vendedor.getCuenta()
                        .addMensaje("M117|" + cercado.getPrecio().toString() + ";" + _perso.getGremio().getNombre().toString() + "|", true)
            }
        }
        cercado.setPrecioPJ(0)
        cercado.setDueñoID(_perso.getID())
        cercado.setGremio(_perso.getGremio())
        for (pj in _perso.getMapa().getArrayPersonajes()) {
            GestorSalida.ENVIAR_Rp_INFORMACION_CERCADO(pj, cercado)
        }
    }

    private fun montura_CambiarXP_Donada(packet: String?) {
        try {
            val xp: Byte = Byte.parseByte(packet.substring(2))
            _perso.setPorcXPMontura(xp)
            GestorSalida.ENVIAR_Rx_EXP_DONADA_MONTURA(_perso)
        } catch (e: Exception) {
        }
    }

    private fun montura_Borrar_Objeto_Crianza(packet: String?) {
        try {
            val cercado: Cercado = _perso.getMapa().getCercado()
            if (cercado == null) {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return
            }
            if (_perso.getNombre() !== "Elbusta") {
                if (_perso.getGremio() == null) {
                    GestorSalida.ENVIAR_BN_NADA(_perso)
                    return
                }
                if (!_perso.getMiembroGremio().puede(Constantes.G_MEJORAR_CERCADOS)) {
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "193")
                    return
                }
            }
            val celda: Short = Short.parseShort(packet.substring(2))
            if (cercado.retirarObjCria(celda, _perso)) {
                GestorSalida.ENVIAR_GDO_OBJETO_TIRAR_SUELO(_perso.getMapa(), '-', celda, 0, false, "")
                return
            }
        } catch (e: Exception) {
        }
    }

    private fun montura_Nombre(nombre: String?) {
        if (_perso.getMontura() == null) {
            GestorSalida.ENVIAR_Re_DETALLES_MONTURA(_perso, "Er", null)
        } else {
            _perso.getMontura().setNombre(nombre)
            GestorSalida.ENVIAR_Rn_CAMBIO_NOMBRE_MONTURA(_perso, nombre)
        }
    }

    private fun montura_Montar() {
        _perso.subirBajarMontura(false)
    }

    private fun montura_Castrar() {
        if (_perso.getMontura() == null) {
            GestorSalida.ENVIAR_Re_DETALLES_MONTURA(_perso, "Er", null)
        } else {
            _perso.getMontura().castrarPavo()
            GestorSalida.ENVIAR_Re_DETALLES_MONTURA(_perso, "+", _perso.getMontura())
        }
    }

    private fun montura_Liberar() {
        if (_perso.getMontura() == null) {
            GestorSalida.ENVIAR_Re_DETALLES_MONTURA(_perso, "Er", null)
        } else {
            Mundo.eliminarMontura(_perso.getMontura())
            _perso.setMontura(null)
        }
    }

    private fun montura_Descripcion(packet: String?) {
        try {
            var id: Int = Integer.parseInt(packet.substring(2).split(Pattern.quote("|")).get(0))
            if (id > 0) {
                id = -id
            }
            GestorSalida.ENVIAR_Rd_DESCRIPCION_MONTURA(_perso, Mundo.getMontura(id))
        } catch (e: Exception) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
        }
    }

    private fun analizar_Amigos(packet: String?) {
        when (packet.charAt(1)) {
            'A' -> amigo_Agregar(packet)
            'D' -> amigo_Borrar(packet)
            'L' -> GestorSalida.ENVIAR_FL_LISTA_DE_AMIGOS(_perso)
            'O' -> when (packet.charAt(2)) {
                '-' -> {
                    _perso.mostrarAmigosEnLinea(false)
                    GestorSalida.ENVIAR_BN_NADA(_perso)
                }
                '+' -> {
                    _perso.mostrarAmigosEnLinea(true)
                    GestorSalida.ENVIAR_BN_NADA(_perso)
                }
            }
            'J' -> amigo_Esposo(packet)
            'S' -> {
                System.out.println(packet.substring(2))
                val amigo: Personaje
                amigo = try {
                    Mundo.getPersonajePorNombre(packet.substring(2))
                } catch (e: Exception) {
                    return
                }
                System.out.println(amigo.getNombre())
                if (amigo.getPelea() != null) {
                    val pelea: Pelea = amigo.getMapa().getPelea(amigo.getPelea().getID())
                    pelea.unirseEspectador(_perso, _perso.getAdmin() > 0)
                }
            }
            else -> {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return
            }
        }
    }

    private fun amigo_Esposo(packet: String?) {
        val esposo: Personaje
        if (_perso.getEsposoID() !== 0) {
            esposo = Mundo.getPersonaje(_perso.getEsposoID())
            if (esposo == null) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "138")
                return
            }
            if (!esposo.enLinea()) {
                if (esposo.getSexo() === Constantes.SEXO_FEMENINO) {
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "136")
                } else {
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "137")
                }
                GestorSalida.ENVIAR_FL_LISTA_DE_AMIGOS(_perso)
                return
            }
        } else {
            esposo = Mundo.getPersonajePorNombre(packet.substring(3))
        }
        when (packet.charAt(2)) {
            'F' -> _perso.teleportIncarman(esposo)
            'S' -> _perso.teleportEsposo(esposo)
            'C' -> _perso.seguirEsposo(esposo, packet)
            else -> {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return
            }
        }
    }

    private fun amigo_Borrar(packet: String?) {
        var id = -1
        id = when (packet.charAt(2)) {
            '%' -> {
                val perso: Personaje = Mundo.getPersonajePorNombre(packet.substring(3))
                if (perso == null) {
                    GestorSalida.ENVIAR_FD_BORRAR_AMIGO(_perso, "Ef")
                    return
                }
                perso.getCuentaID()
            }
            '*' -> {
                val cuenta: Cuenta = Mundo.getCuentaPorApodo(packet.substring(3))
                if (cuenta == null) {
                    GestorSalida.ENVIAR_FD_BORRAR_AMIGO(_perso, "Ef")
                    return
                }
                cuenta.getID()
            }
            else -> {
                val perso2: Personaje = Mundo.getPersonajePorNombre(packet.substring(2))
                if (perso2 == null || !perso2.enLinea()) {
                    GestorSalida.ENVIAR_FD_BORRAR_AMIGO(_perso, "Ef")
                    return
                }
                perso2.getCuentaID()
            }
        }
        if (id == -1 || !_cuenta.esAmigo(id)) {
            GestorSalida.ENVIAR_FD_BORRAR_AMIGO(_perso, "Ef")
        } else {
            _cuenta.borrarAmigo(id)
        }
    }

    private fun amigo_Agregar(packet: String?) {
        var id = -1
        id = when (packet.charAt(2)) {
            '%' -> {
                val perso: Personaje = Mundo.getPersonajePorNombre(packet.substring(3))
                if (perso == null || !perso.enLinea()) {
                    GestorSalida.ENVIAR_FA_AGREGAR_AMIGO(_perso, "Ef")
                    return
                }
                perso.getCuentaID()
            }
            '*' -> {
                val cuenta: Cuenta = Mundo.getCuentaPorApodo(packet.substring(3))
                if (cuenta == null || !cuenta.enLinea()) {
                    GestorSalida.ENVIAR_FA_AGREGAR_AMIGO(_perso, "Ef")
                    return
                }
                cuenta.getID()
            }
            else -> {
                val perso2: Personaje = Mundo.getPersonajePorNombre(packet.substring(2))
                if (perso2 == null || !perso2.enLinea()) {
                    GestorSalida.ENVIAR_FA_AGREGAR_AMIGO(_perso, "Ef")
                    return
                }
                perso2.getCuentaID()
            }
        }
        if (id == -1) {
            GestorSalida.ENVIAR_FA_AGREGAR_AMIGO(_perso, "Ef")
        } else {
            _cuenta.addAmigo(id)
        }
    }

    private fun analizar_Grupo(packet: String?) {
        val grupo: Grupo = _perso.getGrupoParty()
        when (packet.charAt(1)) {
            'A' -> grupo_Aceptar()
            'F' -> {
                if (grupo == null) {
                    GestorSalida.ENVIAR_BN_NADA(_perso)
                    return
                }
                grupo_Seguir(packet)
            }
            'G' -> {
                if (grupo == null) {
                    GestorSalida.ENVIAR_BN_NADA(_perso)
                    return
                }
                grupo_Seguirme_Todos(packet)
            }
            'I' -> grupo_Invitar(packet)
            'R' -> grupo_Rechazar()
            'V' -> {
                if (grupo == null) {
                    GestorSalida.ENVIAR_BN_NADA(_perso)
                    return
                }
                grupo_Expulsar(packet)
            }
            'W' -> {
                if (grupo == null) {
                    GestorSalida.ENVIAR_BN_NADA(_perso)
                    return
                }
                grupo_Localizar()
            }
            else -> {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return
            }
        }
    }

    private fun grupo_Seguirme_Todos(packet: String?) {
        if (_perso.getGrupoParty() == null) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
            return
        }
        var id = -1
        try {
            id = Integer.parseInt(packet.substring(3))
        } catch (e: Exception) {
        }
        val persoSeguir: Personaje = Mundo.getPersonaje(id)
        if (persoSeguir == null || !persoSeguir.enLinea()) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1211")
            return
        }
        when (packet.charAt(2)) {
            '+' -> {
                _perso.getGrupoParty().setRastrear(persoSeguir)
                for (integrante in _perso.getGrupoParty().getMiembros()) {
                    GestorSalida.ENVIAR_IC_PERSONAJE_BANDERA_COMPAS(integrante,
                            persoSeguir.getMapa().getX().toString() + "|" + persoSeguir.getMapa().getY())
                    GestorSalida.ENVIAR_PF_SEGUIR_PERSONAJE(integrante, "+" + persoSeguir.getID())
                }
            }
            '-' -> {
                _perso.getGrupoParty().setRastrear(null)
                for (integrante in _perso.getGrupoParty().getMiembros()) {
                    GestorSalida.ENVIAR_IC_BORRAR_BANDERA_COMPAS(integrante)
                    GestorSalida.ENVIAR_PF_SEGUIR_PERSONAJE(integrante, "-")
                }
            }
        }
    }

    private fun grupo_Seguir(packet: String?) {
        if (_perso.getGrupoParty() == null) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
            return
        }
        var id = -1
        try {
            id = Integer.parseInt(packet.substring(3))
        } catch (e: Exception) {
        }
        val persoSeguir: Personaje = Mundo.getPersonaje(id)
        if (persoSeguir == null || !persoSeguir.enLinea()) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1211")
            return
        }
        when (packet.charAt(2)) {
            '+' -> {
                GestorSalida.ENVIAR_IC_PERSONAJE_BANDERA_COMPAS(_perso,
                        persoSeguir.getMapa().getX().toString() + "|" + persoSeguir.getMapa().getY())
                GestorSalida.ENVIAR_PF_SEGUIR_PERSONAJE(_perso, "+" + persoSeguir.getID())
                _perso.getGrupoParty().setRastrear(persoSeguir)
            }
            '-' -> {
                GestorSalida.ENVIAR_IC_BORRAR_BANDERA_COMPAS(_perso)
                GestorSalida.ENVIAR_PF_SEGUIR_PERSONAJE(_perso, "-")
                _perso.getGrupoParty().setRastrear(null)
            }
        }
    }

    private fun grupo_Localizar() {
        val grupo: Grupo = _perso.getGrupoParty()
        val str = StringBuilder()
        for (miembro in grupo.getMiembros()) {
            if (str.length() > 0) {
                str.append("|")
            }
            str.append(miembro.getMapa().getX().toString() + ";" + miembro.getMapa().getY() + ";" + miembro.getMapa().getID()
                    + ";2;" + miembro.getID() + ";" + miembro.getNombre())
        }
        GestorSalida.ENVIAR_IH_COORDENADAS_UBICACION(_perso, str.toString())
    }

    private fun grupo_Expulsar(packet: String?) { // usar este packet para atacar
        val grupo: Grupo = _perso.getGrupoParty() ?: return
        if (!grupo.esLiderGrupo(_perso) || packet!!.length() === 2) {
            grupo.dejarGrupo(_perso, false)
        } else {
            var id = -1
            try {
                id = Integer.parseInt(packet.substring(2))
            } catch (e: Exception) {
            }
            val expulsado: Personaje = Mundo.getPersonaje(id)
            if (expulsado == null) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1211")
                return
            }
            grupo.dejarGrupo(expulsado, true)
        }
    }

    private fun grupo_Invitar(packet: String?) {
        if (_perso == null) return
        val nombre: String = packet.substring(2)
        val invitado: Personaje = Mundo.getPersonajePorNombre(nombre) ?: return
        if (!invitado.enLinea() || invitado.esIndetectable()) {
            GestorSalida.ENVIAR_PIE_ERROR_INVITACION_GRUPO(_perso, "n$nombre")
            return
        }
        if (invitado.getGrupoParty() != null) {
            GestorSalida.ENVIAR_PIE_ERROR_INVITACION_GRUPO(_perso, "a$nombre")
            return
        }
        if (MainServidor.MAPAS_PERSONAJE.containsKey(_perso.getMapa().getID())) {
            if (_perso.getGrupoParty() != null && _perso.getGrupoParty().getMiembros()
                            .size() >= MainServidor.MAPAS_PERSONAJE.get(_perso.getMapa().getID())) {
                GestorSalida.ENVIAR_PIE_ERROR_INVITACION_GRUPO(_perso, "f")
                return
            }
        } else if (_perso.getGrupoParty() != null && _perso.getGrupoParty().getMiembros().size() >= 8) {
            GestorSalida.ENVIAR_PIE_ERROR_INVITACION_GRUPO(_perso, "f")
            return
        }
        invitado.setInvitador(_perso, "grupo")
        _perso.setInvitandoA(invitado, "grupo")
        if (_perso.getCuenta().getuuid().equalsIgnoreCase(invitado.getCuenta().getuuid())) {
            val lider: Personaje = invitado.getInvitador()
            if (lider == null) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1211")
                return
            }
            var grupo: Grupo? = lider.getGrupoParty()
            if (grupo == null) {
                grupo = Grupo()
                grupo.addIntegrante(lider)
                lider.mostrarGrupo()
            }
            GestorSalida.ENVIAR_PM_AGREGAR_PJ_GRUPO_A_GRUPO(grupo, invitado.stringInfoGrupo())
            grupo.addIntegrante(invitado)
            invitado.mostrarGrupo()
            lider.setInvitandoA(null, "")
            invitado.setInvitandoA(null, "")
        } else {
            GestorSalida.ENVIAR_PIK_INVITAR_GRUPO(_perso, _perso.getNombre(), nombre)
            GestorSalida.ENVIAR_PIK_INVITAR_GRUPO(invitado, _perso.getNombre(), nombre)
        }
    }

    private fun grupo_Rechazar() {
        _perso.rechazarGrupo()
    }

    private fun grupo_Aceptar() {
        if (!_perso.getTipoInvitacion().equals("grupo")) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
            return
        }
        val lider: Personaje = _perso.getInvitador()
        if (lider == null) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1211")
            return
        }
        var grupo: Grupo? = lider.getGrupoParty()
        if (grupo == null) {
            grupo = Grupo()
            grupo.addIntegrante(lider)
            lider.mostrarGrupo()
        }
        GestorSalida.ENVIAR_PM_AGREGAR_PJ_GRUPO_A_GRUPO(grupo, _perso.stringInfoGrupo())
        grupo.addIntegrante(_perso)
        _perso.mostrarGrupo()
        _perso.setInvitador(null, "")
        lider.setInvitandoA(null, "")
        GestorSalida.ENVIAR_PA_ACEPTAR_INVITACION_GRUPO(lider)
        for (pjx in _perso.getGrupoParty().getPersos()) {
            if (pjx == null) continue
            pjx.liderMaitre = false
            pjx.esMaitre = false
        }
    }

    private fun analizar_Objetos(packet: String?) {
        if (_perso.estaExchange()) {
            return
        }
        when (packet.charAt(1)) {
            'd' -> objeto_Eliminar(packet)
            'D' -> objeto_Tirar(packet)
            'f' -> objeto_Alimentar_Objevivo(packet)
            'M' -> objeto_Mover(packet)
            'm' -> objeto_Desasociar_Mimobionte(packet)
            's' -> objeto_Apariencia_Objevivo(packet)
            'T' -> objeto_Usar_Veces(packet)
            'U', 'u' -> objeto_Usar(packet)
            'x' -> objeto_Desequipar_Objevivo(packet)
            else -> {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return
            }
        }
    }

    @Synchronized
    private fun objeto_Eliminar(packet: String?) {
        try {
            val infos: Array<String?> = packet.substring(2).split(Pattern.quote("|"))
            val id: Int = Integer.parseInt(infos[0])
            var cant = 0
            try {
                if (infos.size > 1) {
                    cant = Integer.parseInt(infos[1])
                }
            } catch (e: Exception) {
            }
            val objeto: Objeto = _perso.getObjeto(id)
            if (objeto == null || cant <= 0 || !_perso.estaDisponible(false, true)) {
                GestorSalida.ENVIAR_ODE_ERROR_ELIMINAR_OBJETO(_perso)
                return
            }
            if (objeto.getObjModelo().getTipo() === Constantes.OBJETO_TIPO_OBJETO_DE_BUSQUEDA) {
                GestorSalida.ENVIAR_ODE_ERROR_ELIMINAR_OBJETO(_perso)
                return
            }
            if (MainServidor.PARAM_APRENDER_HECHIZO && objeto.tieneStatTexto(Constantes.STAT_APRENDE_HECHIZO)) {
                val hechizo: Int = Integer.parseInt(objeto.getStats().getParamStatTexto(Constantes.STAT_APRENDE_HECHIZO, 3),
                        16)
                _perso.olvidarHechizo(hechizo)
            }
            if (objeto.getCantidad() - cant < 1) {
                _perso.borrarOEliminarConOR(id, true)
                if (Constantes.esPosicionEquipamiento(objeto.getPosicion())) {
                    GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso)
                }
                if (Constantes.esPosicionVisual(objeto.getPosicion())) {
                    _perso.cambiarRopaVisual()
                }
            } else {
                objeto.setCantidad(objeto.getCantidad() - cant)
                GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, objeto)
            }
            GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso)
        } catch (e: Exception) {
            GestorSalida.ENVIAR_ODE_ERROR_ELIMINAR_OBJETO(_perso)
        }
    }

    @Synchronized
    private fun objeto_Tirar(packet: String?) {
        var id = -1
        var cantidad: Long = -1
        try {
            id = Integer.parseInt(packet.substring(2).split(Pattern.quote("|")).get(0))
            cantidad = Integer.parseInt(packet.split(Pattern.quote("|")).get(1))
        } catch (e: Exception) {
        }
        val objeto: Objeto = _perso.getObjeto(id)
        if (objeto == null || cantidad < 1) {
            GestorSalida.ENVIAR_BN_NADA(_perso, "OBJETO TIRAR NULO O -1")
            return
        }
        if (!_perso.estaDisponible(false, true)) {
            GestorSalida.ENVIAR_BN_NADA(_perso, "OBJETO TIRAR NO DISPONIBLE")
            return
        }
        if (objeto.getObjModelo().getTipo() === Constantes.OBJETO_TIPO_OBJETO_DE_BUSQUEDA) {
            GestorSalida.ENVIAR_BN_NADA(_perso, "OBJETO TIRAR TIPO BUSQUEDA")
            return
        }
        if (objeto.tieneStatTexto(Constantes.STAT_LIGADO_A_CUENTA)) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1129")
            return
        }
        if (!objeto.pasoIntercambiableDesde()) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1129")
            return
        }
        if (objeto.getObjModeloID() === 10085) { // pergaminos de busqueda
            _perso.borrarOEliminarConOR(id, true)
            return
        }
        val celdaDrop: Short = Camino.getCeldaIDCercanaLibre(_perso.getCelda(), _perso.getMapa())
        if (celdaDrop.toInt() == 0) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1145")
            return
        }
        val celdaTirar: Celda = _perso.getMapa().getCelda(celdaDrop)
        val nuevaCantidad: Long = objeto.getCantidad() - cantidad
        if (MainServidor.PARAM_APRENDER_HECHIZO && objeto.tieneStatTexto(Constantes.STAT_APRENDE_HECHIZO)) {
            val hechizo: Int = Integer.parseInt(objeto.getStats().getParamStatTexto(Constantes.STAT_APRENDE_HECHIZO, 3), 16)
            _perso.olvidarHechizo(hechizo)
        }
        if (nuevaCantidad >= 1) {
            val nuevoObj: Objeto = objeto.clonarObjeto(nuevaCantidad, Constantes.OBJETO_POS_NO_EQUIPADO)
            _perso.addObjetoConOAKO(nuevoObj, true)
            objeto.setCantidad(cantidad)
            GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, objeto)
        }
        _perso.borrarOEliminarConOR(id, false)
        celdaTirar.setObjetoTirado(objeto)
        GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso)
        GestorSalida.ENVIAR_GDO_OBJETO_TIRAR_SUELO(_perso.getMapa(), '+', celdaTirar.getID(), objeto.getObjModeloID(),
                false, "")
        // GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
    }

    @Synchronized
    private fun objeto_Usar_Veces(packet: String?) {
        try {
            if (!_perso.getRestriccionA(Personaje.RA_PUEDE_USAR_OBJETOS)) {
                GestorSalida.ENVIAR_BN_NADA(_perso, "OBJETO USAR NO PUEDES USAR OBJETOS")
                return
            }
            if (_perso.estaFullOcupado()) {
                GestorSalida.ENVIAR_BN_NADA(_perso, "OBJETO FULL OCUPADO")
                return
            }
            var idObjeto = -1
            val idObjetivo = -1
            var idCant: Long = 1
            val idCelda: Short = -1
            var pjObjetivo: Personaje? = null
            val infos: Array<String?> = packet.substring(2).split(Pattern.quote("|"))
            try {
                idObjeto = Integer.parseInt(infos[0])
            } catch (e: Exception) {
            }
            try {
                idCant = Integer.parseInt(infos[1])
            } catch (e: Exception) {
            }
            if (idCant <= 0) {
                GestorSalida.ENVIAR_BN_NADA(this)
                return
            }
            val objeto: Objeto = _perso.getObjeto(idObjeto)
            if (objeto == null) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1OBJECT_DONT_EXIST")
                return
            }
            pjObjetivo = Mundo.getPersonaje(idObjetivo)
            if (pjObjetivo == null) {
                pjObjetivo = _perso
            }
            if (pjObjetivo.getMapa() !== _perso.getMapa()) {
                return
            }
            val objModelo: ObjetoModelo = objeto.getObjModelo()
            if (objModelo.getID() === MainServidor.ID_MIMOBIONTE) {
                GestorSalida.ENVIAR_ÑM_PANEL_MIMOBIONTE(_perso)
                return
            }
            val comestible = objModelo.getTipo() === Constantes.OBJETO_TIPO_BEBIDA || objModelo.getTipo() === Constantes.OBJETO_TIPO_POCION || objModelo.getTipo() === Constantes.OBJETO_TIPO_PAN || objModelo.getTipo() === Constantes.OBJETO_TIPO_CARNE_COMESTIBLE || objModelo.getTipo() === Constantes.OBJETO_TIPO_PESCADO_COMESTIBLE
            if (!comestible && objModelo.getNivel() > _perso.getNivel()) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "13")
                return
            }
            if (!(objModelo.getID() <= 680 && objModelo.getID() >= 678)
                    && !Condiciones.validaCondiciones(_perso, objModelo.getCondiciones(), null)) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "119|43")
                return
            }
            if (_perso.getPelea() != null
                    && (_perso.getPelea().getFase() !== Constantes.PELEA_FASE_POSICION || !comestible)) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "191")
            } else if (!Condiciones.validaCondiciones(_perso, objModelo.getCondiciones(), null)) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "119|43")
            } else {
                if (objeto.getCantidad() < idCant) {
                    idCant = objeto.getCantidad()
                }
                objModelo.aplicarAccion2(_perso, pjObjetivo, idObjeto, idCelda, (idCant * -1).toShort())
                val tt = intArrayOf(MisionObjetivoModelo.UTILIZAR_OBJETO)
                _perso.verificarMisionesTipo(tt, null, false, objModelo.getID())
            }
        } catch (e: Exception) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
        }
    }

    @Synchronized
    private fun objeto_Usar(packet: String?) {
        try {
            if (!_perso.getRestriccionA(Personaje.RA_PUEDE_USAR_OBJETOS)) {
                GestorSalida.ENVIAR_BN_NADA(_perso, "OBJETO USAR NO PUEDES USAR OBJETOS")
                return
            }
            if (_perso.estaFullOcupado()) {
                GestorSalida.ENVIAR_BN_NADA(_perso, "OBJETO FULL OCUPADO")
                return
            }
            var idObjeto = -1
            var idObjetivo = -1
            var idCelda: Short = -1
            var pjObjetivo: Personaje? = null
            val infos: Array<String?> = packet.substring(2).split(Pattern.quote("|"))
            try {
                idObjeto = Integer.parseInt(infos[0])
            } catch (e: Exception) {
            }
            try {
                idObjetivo = Integer.parseInt(infos[1])
            } catch (e: Exception) {
            }
            try {
                idCelda = Short.parseShort(infos[2])
            } catch (e: Exception) {
            }
            val objeto: Objeto = _perso.getObjeto(idObjeto)
            if (objeto == null) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1OBJECT_DONT_EXIST")
                return
            }
            pjObjetivo = Mundo.getPersonaje(idObjetivo)
            if (pjObjetivo == null) {
                pjObjetivo = _perso
            }
            if (pjObjetivo.getMapa() !== _perso.getMapa()) {
                return
            }
            val objModelo: ObjetoModelo = objeto.getObjModelo()
            if (objModelo.getID() === MainServidor.ID_MIMOBIONTE) {
                GestorSalida.ENVIAR_ÑM_PANEL_MIMOBIONTE(_perso)
                return
            }
            val comestible = objModelo.getTipo() === Constantes.OBJETO_TIPO_BEBIDA || objModelo.getTipo() === Constantes.OBJETO_TIPO_POCION || objModelo.getTipo() === Constantes.OBJETO_TIPO_PAN || objModelo.getTipo() === Constantes.OBJETO_TIPO_CARNE_COMESTIBLE || objModelo.getTipo() === Constantes.OBJETO_TIPO_PESCADO_COMESTIBLE
            if (!comestible && objModelo.getNivel() > _perso.getNivel()) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "13")
                return
            }
            if (!(objModelo.getID() <= 680 && objModelo.getID() >= 678)
                    && !Condiciones.validaCondiciones(_perso, objModelo.getCondiciones(), null)) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "119|43")
                return
            }
            if (_perso.getPelea() != null
                    && (_perso.getPelea().getFase() !== Constantes.PELEA_FASE_POSICION || !comestible)) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "191")
            } else if (!Condiciones.validaCondiciones(_perso, objModelo.getCondiciones(), null)) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "119|43")
            } else {
                if (MainServidor.ITEM_CONSUMIBLE_AUTOMATICO.contains(objModelo.getID())) {
                    val idCant: Long = objeto.getCantidad()
                    for (i in 0 until idCant) {
                        objModelo.aplicarAccion(_perso, pjObjetivo, idObjeto, idCelda)
                    }
                    val tt = intArrayOf(MisionObjetivoModelo.UTILIZAR_OBJETO)
                    _perso.verificarMisionesTipo(tt, null, false, objModelo.getID())
                } else {
                    objModelo.aplicarAccion(_perso, pjObjetivo, idObjeto, idCelda)
                    val tt = intArrayOf(MisionObjetivoModelo.UTILIZAR_OBJETO)
                    _perso.verificarMisionesTipo(tt, null, false, objModelo.getID())
                }
            }
        } catch (e: Exception) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
        }
    }

    private fun objeto_Mover_2(objetoID: Int, posAMover: Byte, cantObjMover: Long): Int {
        var posAMover = posAMover
        var cantObjMover = cantObjMover
        var r = -1
        val objMover: Objeto = _perso.getObjeto(objetoID)
        if (objMover == null || cantObjMover < 1) {
            GestorSalida.ENVIAR_BN_NADA(_perso, "OM2")
            return r
        }
        val posAnt: Byte = objMover.getPosicion()
        if (posAnt == posAMover) { // misma posicion a mover
            GestorSalida.ENVIAR_BN_NADA(_perso, "OM3")
            return r
        }
        val objMoverMod: ObjetoModelo = objMover.getObjModelo()
        if (posAMover != Constantes.OBJETO_POS_NO_EQUIPADO
                && objMoverMod.getTipo() === Constantes.OBJETO_TIPO_OBJETO_DE_BUSQUEDA) {
            GestorSalida.ENVIAR_BN_NADA(_perso, "OM4")
            return r
        }
        if (MainServidor.ITEM_SOLO_POS.containsKey(objMover.getObjModeloID())) {
            posAMover = if (objMover.getPosicion() !== -1) {
                -1
            } else {
                MainServidor.ITEM_SOLO_POS.get(objMover.getObjModeloID()).byteValue()
                /*
				 * if(objMover.getObjModelo().getTipo() == Constantes.OBJETO_TIPO_GEMAS) {
				 * Sucess.launch(_perso, (byte) 10, null,0); }
				 */
            }
            /*
			 * if (MainServidor.ITEM_SOLO_POS.get(objMover.getObjModeloID()) != posAMover) {
			 * GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
			 * "Estas intentando usar una objeto en un item equivocado"); return r; }
			 */
        }
        if (Constantes.esPosicionEquipamiento(posAMover)) {
            if (objMoverMod.getNivel() > _perso.getNivel()) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "13")
                return r
            }
            if (!Condiciones.validaCondiciones(_perso, objMoverMod.getCondiciones(), null)) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "119|43")
                return r
            }
            if (!_perso.puedeEquiparRepetido(objMoverMod, 1)) {
                GestorSalida.ENVIAR_BN_NADA(_perso, "OM5")
                return r
            }
            if (objMover.getEncarnacion() != null && !_perso.sePuedePonerEncarnacion()) {
                GestorSalida.ENVIAR_BN_NADA(_perso, "60 SEG PARA ENCARNAR")
                return r
            }
            cantObjMover = 1
        } else if (cantObjMover > objMover.getCantidad()) {
            cantObjMover = objMover.getCantidad()
        }
        if (posAMover == Constantes.OBJETO_POS_MASCOTA) { // posicion de mascota
            if (objMoverMod.getTipo() !== Constantes.OBJETO_TIPO_MASCOTA) { // alimentar a mascota
                return objeto_Alimentar_Mascota(objMover, _perso.getObjPosicion(Constantes.OBJETO_POS_MASCOTA))
            }
            if (_perso.estaMontando()) {
                _perso.subirBajarMontura(false)
            }
        }
        if (posAMover == Constantes.OBJETO_POS_MONTURA && _perso.getMontura() != null) {
            return objeto_Alimentar_Montura(objMoverMod, cantObjMover, objetoID)
        }
        if (!Constantes.esUbicacionValidaObjeto(objMoverMod.getTipo(), posAMover)) {
            GestorSalida.ENVIAR_BN_NADA(_perso, "OM6")
            return r
        }
        if (objMoverMod.esMimo()) {
            return objeto_Equipar_MIMO(objMover, posAMover)
        }
        if (objMoverMod.getTipo() === Constantes.OBJETO_TIPO_OBJEVIVO) { // si es objevivo
            return objeto_Equipar_Objevivo(objMover, posAMover)
        }
        /*
		 * if (objMoverMod.esMimo()) { return objeto_Equipar_MIMO(objMover, posAMover);
		 * }
		 */r = 0
        // se crea un nuevo objeto con la cantidad restante (para no repetirlo despues)
        val nuevaCantidad: Long = objMover.getCantidad() - cantObjMover
        if (nuevaCantidad >= 1) {
            val nuevoObj: Objeto = objMover.clonarObjeto(nuevaCantidad, posAnt)
            _perso.addObjetoConOAKO(nuevoObj, true)
            objMover.setCantidad(cantObjMover)
            GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, objMover)
            _perso.actualizarSetsRapidos(objMover.getID(), nuevoObj.getID(), objMover.getPosicion(), posAMover)
        }
        if (objMoverMod.getTipo() !== Constantes.OBJETO_TIPO_ESPECIALES
                && objMoverMod.getTipo() !== Constantes.OBJETO_TIPO_POCION_FORJAMAGIA) {
            if (posAMover == Constantes.OBJETO_POS_ESCUDO) { // pos a mover es escudo
                objeto_Quitar_Arma_Dos_Manos(objMover)
            } else if (posAMover == Constantes.OBJETO_POS_ARMA) { // pos a mover es arma
                if (objMoverMod.esDosManos()) {
                    objeto_Quitar_Escudo_Para_Arma(objMover)
                }
            }
        }
        val exObj: Objeto = _perso.getObjPosicion(posAMover)
        if (exObj != null) { // el objeto q habia en la posicion a mover
            if (objMoverMod.getTipo() === Constantes.OBJETO_TIPO_ESPECIALES || objMoverMod.getTipo() === Constantes.OBJETO_TIPO_POCION_FORJAMAGIA || objMoverMod.getTipo() === Constantes.OBJETO_TIPO_FABRUSHIO) {
                // convertir perfecto, si es lupa o pocima de FM
                objeto_Maguear_O_Lupear(exObj, posAMover, objMover)
                return r
            } else if (Constantes.esPosicionEquipamiento(posAMover)) {
                // no es del tipo especial o pocima fm (cuando se mueve a una pos equipamiento)
                val identInvExObj: Objeto = _perso.getObjIdentInventario(exObj, objMover)
                if (identInvExObj != null) {
                    identInvExObj.setCantidad(identInvExObj.getCantidad() + exObj.getCantidad())
                    GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, identInvExObj)
                    _perso.borrarOEliminarConOR(exObj.getID(), true)
                    _perso.actualizarSetsRapidos(exObj.getID(), identInvExObj.getID(), exObj.getPosicion(),
                            identInvExObj.getPosicion())
                } else {
                    // mueve el exobjeto al inventario
                    exObj.setPosicion(Constantes.OBJETO_POS_NO_EQUIPADO, _perso, false)
                }
                objMover.setPosicion(posAMover, _perso, false)
                if (exObj.getObjModelo().getSetID() > 0) {
                    GestorSalida.ENVIAR_OS_BONUS_SET(_perso, exObj.getObjModelo().getSetID(), -1)
                }
            } else {
                // posiciones donde se pone caramelo, panes y otros
                if (objMover.getObjModeloID() === exObj.getObjModeloID() && objMover.sonStatsIguales(exObj)) {
                    exObj.setCantidad(cantObjMover + exObj.getCantidad())
                    GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, exObj)
                    _perso.borrarOEliminarConOR(objMover.getID(), true)
                    _perso.actualizarSetsRapidos(objMover.getID(), exObj.getID(), objMover.getPosicion(),
                            exObj.getPosicion())
                } else {
                    GestorSalida.ENVIAR_BN_NADA(_perso, "OM PENDEJADA")
                }
                return r
            }
            if (MainServidor.PARAM_APRENDER_HECHIZO && exObj.tieneStatTexto(Constantes.STAT_APRENDE_HECHIZO)) {
                val hechizo: Int = Integer.parseInt(exObj.getStats().getParamStatTexto(Constantes.STAT_APRENDE_HECHIZO, 3),
                        16)
                _perso.olvidarHechizo(hechizo)
            }
        } else { // si no habia un objeto donde queremos mover
            if (objMoverMod.getTipo() === Constantes.OBJETO_TIPO_ESPECIALES
                    || objMoverMod.getTipo() === Constantes.OBJETO_TIPO_POCION_FORJAMAGIA) {
                // no equipables
                GestorSalida.ENVIAR_BN_NADA(_perso, "OM8")
                return r
            }
            var identicoInv: Objeto?
            if (posAMover == Constantes.OBJETO_POS_NO_EQUIPADO
                    && _perso.getObjIdentInventario(objMover, null).also { identicoInv = it } != null) {
                // mover a NO EQUIPADO y hay otro objeto identico
                _perso.borrarOEliminarConOR(objMover.getID(), true)
                identicoInv.setCantidad(identicoInv.getCantidad() + cantObjMover)
                GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, identicoInv)
                _perso.actualizarSetsRapidos(objMover.getID(), identicoInv.getID(), objMover.getPosicion(),
                        identicoInv.getPosicion())
            } else {
                objMover.setPosicion(posAMover, _perso, false)
            }
        }
        if (objMover.getStats().tieneStatTexto(Constantes.STAT_TITULO)
                || objMover.getStats().tieneStatID(Constantes.STAT_CAMBIA_APARIENCIA_2)
                || objMover.getStats().tieneStatID(Constantes.STAT_CAMBIA_APARIENCIA)
                || objMover.getStats().tieneStatID(Constantes.STAT_MAS_VELOCIDAD)
                || objMover.getStats().tieneStatID(Constantes.STAT_AURA)) {
            _perso.refrescarEnMapa()
        }
        // para los oficios
        // rectifica la cantidad de objetos por set
        if (objMoverMod.getSetID() > 0) {
            GestorSalida.ENVIAR_OS_BONUS_SET(_perso, objMoverMod.getSetID(), -1)
        }
        if (Constantes.esPosicionEquipamiento(posAMover)
                || posAMover == Constantes.OBJETO_POS_NO_EQUIPADO && Constantes.esPosicionEquipamiento(posAnt)) {
            r = 1
        }
        if (objMoverMod.getTipo() === Constantes.OBJETO_TIPO_TRAJES) {
            _perso.activa = true
            GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(_perso.getMapa(), _perso)
            r = 2
        }
        // solo cambios visuales
        if (Constantes.esPosicionVisual(posAMover)
                || posAMover == Constantes.OBJETO_POS_NO_EQUIPADO && Constantes.esPosicionVisual(posAnt)) {
            r = 2
        }
        if (MainServidor.PARAM_APRENDER_HECHIZO && objMover.tieneStatTexto(Constantes.STAT_APRENDE_HECHIZO)) {
            val hechizo: Int = Integer.parseInt(objMover.getStats().getParamStatTexto(Constantes.STAT_APRENDE_HECHIZO, 3),
                    16)
            if (posAMover != Constantes.OBJETO_POS_NO_EQUIPADO) {
                _perso.fijarNivelHechizoOAprender(hechizo, 6, true)
            } else {
                _perso.olvidarHechizo(hechizo)
            }
        }
        return r
    }

    @Synchronized
    fun objeto_Mover(packet: String?) {
        // al mover se actualizan los stats objetos
        try {
            if (_perso.getPelea() != null) {
                if (_perso.getPelea().getFase() !== Constantes.PELEA_FASE_POSICION || equiparSet) {
                    GestorSalida.ENVIAR_BN_NADA(_perso, "OM1")
                    return
                }
            }
            var cambio = 0
            val subPacket: String = packet.substring(2).split("\n").get(0)
            for (s in subPacket.split(Pattern.quote("*"))) {
                val infos: Array<String?> = s.split(Pattern.quote("|"))
                val objetoID: Int = Integer.parseInt(infos[0])
                val posAMover: Byte = Byte.parseByte(infos[1])
                var cantObjMover = 1
                try {
                    if (infos.size > 2) {
                        cantObjMover = Integer.parseInt(infos[2])
                    }
                } catch (e: Exception) {
                }
                cambio = Math.max(objeto_Mover_2(objetoID, posAMover, cantObjMover.toLong()), cambio)
                Thread.sleep(100)
            }
            if (cambio >= 1) {
                _perso.refrescarStuff(true, cambio >= 1, cambio >= 2)
            }
        } catch (e: Exception) {
            GestorSalida.ENVIAR_BN_NADA(_perso, "OM9")
            return
        }
    }

    val equiparSet: Boolean
        get() = if (MainServidor.PARAMS_EQUIPAR_SET_PELEA) {
            when (_perso.getPelea().getTipoPelea()) {
                Constantes.PELEA_TIPO_KOLISEO, Constantes.PELEA_TIPO_PVP -> true
                else -> false
            }
        } else {
            false
        }

    private fun objeto_Equipar_Set(set: SetRapido?): Int {
        var r = -1
        try {
            if (_perso.getPelea() != null) {
                if (_perso.getPelea().getFase() !== Constantes.PELEA_FASE_POSICION || equiparSet) {
                    GestorSalida.ENVIAR_BN_NADA(_perso, "EQUIPAR TODO EN PELEA")
                    return r
                }
            }
            val orden = byteArrayOf(Constantes.OBJETO_POS_DOFUS1, Constantes.OBJETO_POS_DOFUS2, Constantes.OBJETO_POS_DOFUS3,
                    Constantes.OBJETO_POS_DOFUS4, Constantes.OBJETO_POS_DOFUS5, Constantes.OBJETO_POS_DOFUS6,
                    Constantes.OBJETO_POS_COMPAÑERO, Constantes.OBJETO_POS_MASCOTA, Constantes.OBJETO_POS_ANILLO1,
                    Constantes.OBJETO_POS_ANILLO_DERECHO, Constantes.OBJETO_POS_BOTAS, Constantes.OBJETO_POS_CINTURON,
                    Constantes.OBJETO_POS_AMULETO, Constantes.OBJETO_POS_SOMBRERO, Constantes.OBJETO_POS_CAPA,
                    Constantes.OBJETO_POS_ESCUDO, Constantes.OBJETO_POS_ARMA)
            for (i in orden) {
                val idObjeto: Int = set.getObjetos().get(i.toInt())
                if (idObjeto <= 0) {
                    continue
                }
                r = Math.max(r, objeto_Mover_2(idObjeto, i, 1))
                Thread.sleep(100)
            }
        } catch (e: Exception) {
            GestorSalida.ENVIAR_BN_NADA(_perso, "EQUIPAR TODO EXCEPTION")
            e.printStackTrace()
        }
        return r
    }

    private fun objeto_Desequipar_Set(): Int {
        var r = -1
        try {
            if (_perso.getPelea() != null) {
                if (_perso.getPelea().getFase() !== Constantes.PELEA_FASE_POSICION
                        || _perso.getPelea().getTipoPelea() === Constantes.PELEA_TIPO_KOLISEO) {
                    GestorSalida.ENVIAR_BN_NADA(_perso, "DESEQUIPAR TODO EN PELEA")
                    return r
                }
            }
            val orden = byteArrayOf(Constantes.OBJETO_POS_DOFUS1, Constantes.OBJETO_POS_DOFUS2, Constantes.OBJETO_POS_DOFUS3,
                    Constantes.OBJETO_POS_DOFUS4, Constantes.OBJETO_POS_DOFUS5, Constantes.OBJETO_POS_DOFUS6,
                    Constantes.OBJETO_POS_COMPAÑERO, Constantes.OBJETO_POS_MASCOTA, Constantes.OBJETO_POS_ANILLO1,
                    Constantes.OBJETO_POS_ANILLO_DERECHO, Constantes.OBJETO_POS_BOTAS, Constantes.OBJETO_POS_CINTURON,
                    Constantes.OBJETO_POS_AMULETO, Constantes.OBJETO_POS_SOMBRERO, Constantes.OBJETO_POS_CAPA,
                    Constantes.OBJETO_POS_ESCUDO, Constantes.OBJETO_POS_ARMA)
            for (i in orden) {
                val objeto: Objeto = _perso.getObjPosicion(i) ?: continue
                r = Math.max(r, objeto_Mover_2(objeto.getID(), Constantes.OBJETO_POS_NO_EQUIPADO, 1))
                Thread.sleep(100)
            }
        } catch (e: Exception) {
            GestorSalida.ENVIAR_BN_NADA(_perso, "DESEQUIPAR TODO EXCEPTION")
            e.printStackTrace()
        }
        return r
    }

    private fun objeto_String_Set_Equipado(): String? {
        val str = StringBuilder()
        val cond = StringBuilder()
        val orden = byteArrayOf(Constantes.OBJETO_POS_IDOLO_1, Constantes.OBJETO_POS_IDOLO_2, Constantes.OBJETO_POS_IDOLO_3,
                Constantes.OBJETO_POS_IDOLO_4, Constantes.OBJETO_POS_IDOLO_5, Constantes.OBJETO_POS_IDOLO_6,
                Constantes.OBJETO_POS_DOFUS1, Constantes.OBJETO_POS_DOFUS2, Constantes.OBJETO_POS_DOFUS3,
                Constantes.OBJETO_POS_DOFUS4, Constantes.OBJETO_POS_DOFUS5, Constantes.OBJETO_POS_DOFUS6,
                Constantes.OBJETO_POS_COMPAÑERO, Constantes.OBJETO_POS_MASCOTA, Constantes.OBJETO_POS_ANILLO1,
                Constantes.OBJETO_POS_ANILLO_DERECHO, Constantes.OBJETO_POS_BOTAS, Constantes.OBJETO_POS_CINTURON,
                Constantes.OBJETO_POS_AMULETO, Constantes.OBJETO_POS_SOMBRERO, Constantes.OBJETO_POS_CAPA,
                Constantes.OBJETO_POS_ESCUDO, Constantes.OBJETO_POS_ARMA)
        for (i in orden) {
            val obj: Objeto = _perso.getObjPosicion(i) ?: continue
            if (obj.getObjModelo().getCondiciones().isEmpty()) {
                if (str.length() > 0) {
                    str.append(";")
                }
                str.append(obj.getID().toString() + "," + obj.getPosicion())
            } else {
                if (cond.length() > 0) {
                    cond.append(";")
                }
                cond.append(obj.getID().toString() + "," + obj.getPosicion())
            }
        }
        if (cond.length() > 0) {
            if (str.length() > 0) {
                str.append(";")
            }
            str.append(cond.toString())
        }
        return str.toString()
    }

    private fun objeto_Quitar_Escudo_Para_Arma(objMover: Objeto?) {
        val escudo: Objeto = _perso.getObjPosicion(Constantes.OBJETO_POS_ESCUDO) // escudo
        if (escudo != null) {
            val identInvExObj: Objeto = _perso.getObjIdentInventario(escudo, objMover)
            if (identInvExObj != null) { // el objeto
                identInvExObj.setCantidad(identInvExObj.getCantidad() + escudo.getCantidad())
                GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, identInvExObj)
                _perso.borrarOEliminarConOR(escudo.getID(), true)
                _perso.actualizarSetsRapidos(escudo.getID(), identInvExObj.getID(), escudo.getPosicion(),
                        identInvExObj.getPosicion())
            } else {
                escudo.setPosicion(Constantes.OBJETO_POS_NO_EQUIPADO, _perso, false)
            }
            if (escudo.getObjModelo().getSetID() > 0) {
                GestorSalida.ENVIAR_OS_BONUS_SET(_perso, escudo.getObjModelo().getSetID(), -1)
            }
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "079")
        }
    }

    private fun objeto_Quitar_Arma_Dos_Manos(objMover: Objeto?) {
        val arma: Objeto = _perso.getObjPosicion(Constantes.OBJETO_POS_ARMA) // arma
        if (arma != null && arma.getObjModelo().esDosManos()) { // arma 2 manos
            val identicoArma: Objeto = _perso.getObjIdentInventario(arma, objMover)
            if (identicoArma != null) { // el objeto
                identicoArma.setCantidad(identicoArma.getCantidad() + arma.getCantidad())
                GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, identicoArma)
                _perso.borrarOEliminarConOR(arma.getID(), true)
                _perso.actualizarSetsRapidos(arma.getID(), identicoArma.getID(), arma.getPosicion(),
                        identicoArma.getPosicion())
            } else {
                arma.setPosicion(Constantes.OBJETO_POS_NO_EQUIPADO, _perso, false)
            }
            if (arma.getObjModelo().getSetID() > 0) {
                GestorSalida.ENVIAR_OS_BONUS_SET(_perso, arma.getObjModelo().getSetID(), -1)
            }
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "078")
        }
    }

    private fun objeto_Alimentar_Montura(objMoverMod: ObjetoModelo?, cantObjMover: Long, idObjMover: Int): Int {
        if (Constantes.esAlimentoMontura(objMoverMod.getTipo())) {
            _perso.restarCantObjOEliminar(idObjMover, cantObjMover, true)
            _perso.getMontura().aumentarEnergia(objMoverMod.getNivel(), cantObjMover)
            GestorSalida.ENVIAR_Re_DETALLES_MONTURA(_perso, "+", _perso.getMontura())
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "0105")
            return 0
        } else {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "190")
        }
        return -1
    }

    private fun objeto_Equipar_MIMO(mascara: Objeto?, posAMover: Byte): Int {
        val r = -1
        try {
            if (!_perso.estaDisponible(true, true)) {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return r
            }
            val objeto: Objeto = _perso.getObjPosicion(posAMover)
            if (objeto == null) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1161")
                return r
            }
            if (objeto.tieneStatTexto(Constantes.STAT_APARIENCIA_OBJETO)) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1161")
                return r
            }
            if (objeto.getObjevivoID() !== 0) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1MIMOBIONTE_ERROR_TYPES")
                return r
            }
            val tipoObj: Int = objeto.getObjModelo().getTipo()
            val tipoMimo: Int = mascara.getObjModelo().getTipo()
            var paso = false
            when (tipoMimo) {
                Constantes.OBJETO_TIPO_CAPA -> paso = tipoObj == tipoMimo || tipoObj == Constantes.OBJETO_TIPO_MOCHILA
                Constantes.OBJETO_TIPO_SOMBRERO, Constantes.OBJETO_TIPO_AMULETO, Constantes.OBJETO_TIPO_ESCUDO, Constantes.OBJETO_TIPO_BOTAS, Constantes.OBJETO_TIPO_MASCOTA, Constantes.OBJETO_TIPO_CINTURON, Constantes.OBJETO_TIPO_ANILLO -> paso = tipoObj == tipoMimo
            }
            if (!paso) {
                return r
            }
            if (!_perso.restarCantObjOEliminar(mascara.getID(), 1, true)) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "14|43")
                return r
            }
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "022;" + 1 + "~" + mascara.getObjModeloID())
            val nuevaCantidad = (objeto.getCantidad() - 1) as Int
            if (nuevaCantidad >= 1) {
                val nuevo: Objeto = objeto.clonarObjeto(nuevaCantidad, Constantes.OBJETO_POS_NO_EQUIPADO)
                _perso.addObjetoConOAKO(nuevo, true)
                objeto.setCantidad(1)
                GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, objeto)
            }
            objeto.addStatTexto(Constantes.STAT_APARIENCIA_OBJETO,
                    "0#0#" + Integer.toHexString(mascara.getObjModeloID()))
            GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(_perso, objeto)
            GestorSQL.SALVAR_OBJETO(mascara)
            if (Constantes.esPosicionVisual(objeto.getPosicion())) {
                _perso.cambiarRopaVisual()
            }
        } catch (e: Exception) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
        }
        return r
    }

    private fun objeto_Equipar_Objevivo(objevivo: Objeto?, posAMover: Byte): Int {
        val r = -1
        try {
            if (!_perso.estaDisponible(true, true)) {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return r
            }
            val objeto: Objeto = _perso.getObjPosicion(posAMover)
            if (objeto == null) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1161")
                return r
            }
            if (objeto.tieneStatTexto(Constantes.STAT_APARIENCIA_OBJETO)) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1161")
                return r
            }
            /*
			 * if(objeto.getObjModelo().esMimo()){ GestorSalida.ENVIAR_BN_NADA(_perso);
			 * return r; }
			 */
            val tipoObj: Int = objeto.getObjModelo().getTipo()
            val tipoVivo: Int = Integer.parseInt(objevivo.getParamStatTexto(Constantes.STAT_REAL_TIPO, 3), 16)
            var paso = false
            when (tipoVivo) {
                Constantes.OBJETO_TIPO_CAPA -> paso = tipoObj == tipoVivo || tipoObj == Constantes.OBJETO_TIPO_MOCHILA
                Constantes.OBJETO_TIPO_SOMBRERO, Constantes.OBJETO_TIPO_AMULETO, Constantes.OBJETO_TIPO_ESCUDO, Constantes.OBJETO_TIPO_BOTAS, Constantes.OBJETO_TIPO_MASCOTA, Constantes.OBJETO_TIPO_CINTURON, Constantes.OBJETO_TIPO_ANILLO -> paso = tipoObj == tipoVivo
            }
            if (!paso) {
                return r
            }
            if (objeto.tieneStatTexto(Constantes.STAT_INTERCAMBIABLE_DESDE)) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1162")
                return r
            }
            if (objeto.getObjevivoID() > 0 || objeto.tieneStatTexto(Constantes.STAT_TURNOS)) {
                GestorSalida.ENVIAR_BN_NADA(_perso, "EQUIPAR OBJEVIVO TIENE OBJEVIVO")
                return r
            }
            val nuevaCantidad: Long = objevivo.getCantidad() - 1
            if (nuevaCantidad >= 1) {
                val nuevoObj: Objeto = objevivo.clonarObjeto(nuevaCantidad, Constantes.OBJETO_POS_NO_EQUIPADO)
                _perso.addObjetoConOAKO(nuevoObj, true)
                objevivo.setCantidad(1)
            }
            objevivo.addStatTexto(Constantes.STAT_REAL_GFX, "0#0#" + Integer.toHexString(objevivo.getObjModeloID()))
            objeto.setIDObjevivo(objevivo.getID())
            _perso.borrarOEliminarConOR(objevivo.getID(), false)
            GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(_perso, objeto)
            // lo salva porq despues ya no lo tendra en la lista de items
            GestorSQL.SALVAR_OBJETO(objevivo)
            if (Constantes.esPosicionVisual(objeto.getPosicion())) {
                _perso.cambiarRopaVisual()
            }
        } catch (e: Exception) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
        }
        return r
    }

    private fun objeto_Maguear_O_Lupear(exObj: Objeto?, posAMover: Byte, objMover: Objeto?) {
        if (exObj.getObjevivoID() !== 0 || exObj.getCantidad() > 1) {
            GestorSalida.ENVIAR_BN_NADA(_perso, "OM7")
            return
        }
        val objMoverMod: ObjetoModelo = objMover.getObjModelo()
        if (objMoverMod.getTipo() === Constantes.OBJETO_TIPO_ESPECIALES) {
            if (!exObj.convertirPerfecto(objMoverMod.getNivel())) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1OBJETO_NO_NECESITA_MEJORAS")
                return
            }
        } else if (!MainServidor.MODO_ANKALIKE && objMoverMod.getTipo() === Constantes.OBJETO_TIPO_POCION_FORJAMAGIA && posAMover == Constantes.OBJETO_POS_ARMA) {
            // cambiar daño elemental
            val statFM: Int = Constantes.getStatPorRunaPocima(objMover)
            val potenciaFM: Int = Constantes.getValorPorRunaPocima(objMover)
            exObj.forjaMagiaGanar(statFM, potenciaFM)
            GestorSalida.ENVIAR_IO_ICONO_OBJ_INTERACTIVO(_perso.getMapa(), _perso.getID(), "+" + objMoverMod.getID())
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1OBJETO_CAMBIO_DANO_ELEMENTAL")
        } else if (MainServidor.PARAMS_OBJETO_FABRUSHIO && objMoverMod.getTipo() === Constantes.OBJETO_TIPO_FABRUSHIO) {
            // codigo del sistema nuevo
            if ((exObj.getStats().tieneStatID(Constantes.STAT_MAS_PA)
                            && objMover.getStats().tieneStatID(Constantes.STAT_MAS_PA))
                    || (exObj.getStats().tieneStatID(Constantes.STAT_MAS_PM)
                            && objMover.getStats().tieneStatID(Constantes.STAT_MAS_PM))) {
                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                        "No puedes ponerle mas PA o PM al objeto porque ya posee uno de estos stats")
                return
            }
            if ((exObj.tieneStatExo(Constantes.STAT_MAS_PA) || exObj.tieneStatExo(Constantes.STAT_MAS_PM))
                    && (objMover.getStats().tieneStatID(Constantes.STAT_MAS_PM)
                            || objMover.getStats().tieneStatID(Constantes.STAT_MAS_PA))) {
                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                        "NO puede meterle otro PA o PM al item porque ya posee uno stats exo")
                return
            }
            if (objMover.getObjModelo().getOgrinas() > 0) {
                if (exObj.tieneStatTexto(Constantes.STAT_OBJETO_FABRUSHIO_VIP)) {
                    val stats: Array<String?> = exObj.getStats().getStatTexto(Constantes.STAT_OBJETO_FABRUSHIO_VIP).split("#")
                    if (stats.size > 1) {
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                "Ya le has fusiando todos los fragmentos VIP")
                        GestorSalida.ENVIAR_BN_NADA(this)
                        return
                    } else {
                        for (st in stats) {
                            if (st.equalsIgnoreCase(objMoverMod.getID().toString() + "")) {
                                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                        "No le puede fusionar 2 veces el mismo objeto")
                                return
                            }
                        }
                        exObj.getStats().acumularStats(objMover.getStats())
                        exObj.getStats().addStatTexto(Constantes.STAT_OBJETO_FABRUSHIO_VIP,
                                exObj.getStats().getStatTexto(Constantes.STAT_OBJETO_FABRUSHIO_VIP).toString() + "#"
                                        + objMover.getObjModelo().getID(),
                                false)
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Objeto Fusionado con exito")
                    }
                } else {
                    exObj.getStats().acumularStats(objMover.getStats())
                    exObj.getStats().addStatTexto(Constantes.STAT_OBJETO_FABRUSHIO_VIP, objMover.getObjModelo().getID().toString() + "", false)
                    GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Objeto Fusionado con exito")
                }
            } else {
                if (exObj.tieneStatTexto(Constantes.STAT_OBJETO_FABRUSHIO)) {
                    val stats: Array<String?> = exObj.getStats().getStatTexto(Constantes.STAT_OBJETO_FABRUSHIO).split("#")
                    if (stats.size > 2) {
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                "Ya le has fusiando todos los fragmentos")
                        GestorSalida.ENVIAR_BN_NADA(this)
                        return
                    } else {
                        for (st in stats) {
                            if (st.equalsIgnoreCase(objMoverMod.getID().toString() + "")) {
                                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                        "No le puede fusionar 2 veces el mismo objeto")
                                return
                            }
                        }
                        exObj.getStats().acumularStats(objMover.getStats())
                        exObj.getStats().addStatTexto(Constantes.STAT_OBJETO_FABRUSHIO,
                                exObj.getStats().getStatTexto(Constantes.STAT_OBJETO_FABRUSHIO).toString() + "#"
                                        + objMover.getObjModelo().getID(),
                                false)
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Objeto Fusionado con exito")
                    }
                } else {
                    exObj.getStats().acumularStats(objMover.getStats())
                    exObj.getStats().addStatTexto(Constantes.STAT_OBJETO_FABRUSHIO, objMover.getObjModelo().getID().toString() + "", false)
                    GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Objeto Fusionado con exito")
                }
            }
        }
        if (objMover.addDurabilidad(-1)) {
            _perso.borrarOEliminarConOR(objMover.getID(), true)
        } else {
            GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(_perso, objMover)
        }
        GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(_perso, exObj)
        GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso)
        GestorSQL.SALVAR_OBJETO(exObj)
    }

    private fun objeto_Apariencia_Objevivo(packet: String?) {
        try {
            val split: Array<String?> = packet.substring(2).split(Pattern.quote("|"))
            val objeto: Objeto = Mundo.getObjeto(Integer.parseInt(split[0]))
            var objevivo: Objeto? = null
            objevivo = if (objeto.getObjModelo().getTipo() === Constantes.OBJETO_TIPO_OBJEVIVO) {
                objeto
            } else {
                Mundo.getObjeto(objeto.getObjevivoID())
            }
            val exp: Int = Integer.parseInt(objevivo.getParamStatTexto(Constantes.STAT_EXP_OBJEVIVO, 3), 16)
            var skin: Int = Integer.parseInt(split[2])
            val nivel: Int = Constantes.getNivelObjevivo(exp)
            if (skin > nivel) {
                skin = nivel
            }
            if (skin < 1) {
                skin = 1
            }
            objevivo.addStatTexto(Constantes.STAT_SKIN_OBJEVIVO, "0#0#" + Integer.toHexString(skin))
            GestorSQL.SALVAR_OBJETO(objevivo)
            GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(_perso, objeto)
            if (Constantes.esPosicionVisual(objeto.getPosicion())) {
                _perso.cambiarRopaVisual()
            }
        } catch (e: Exception) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
        }
    }

    private fun objeto_Alimentar_Objevivo(packet: String?) {
        try {
            if (!_perso.estaDisponible(true, true)) {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return
            }
            val objetoObj: Objeto = _perso
                    .getObjeto(Integer.parseInt(packet.substring(2).split(Pattern.quote("|")).get(0)))
            val idObjAlimento: Int = Integer.parseInt(packet.split(Pattern.quote("|")).get(2))
            if (objetoObj.getPosicion() === Constantes.OBJETO_POS_NO_EQUIPADO || !_perso.tieneObjetoID(idObjAlimento)) {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return
            }
            val objevivo: Objeto = Mundo.getObjeto(objetoObj.getObjevivoID())
            objevivo.addStatTexto(Constantes.STAT_SE_HA_COMIDO_EL,
                    ObjetoModelo.getStatSegunFecha(Calendar.getInstance())) // comida
            var expActual: Int = Integer.parseInt(objevivo.getParamStatTexto(Constantes.STAT_EXP_OBJEVIVO, 3), 16)
            var expAdicional = Math.ceil(_perso.getObjeto(idObjAlimento).getObjModelo().getNivel() / 5) as Int
            val tipoVivo: Int = Integer.parseInt(objevivo.getParamStatTexto(Constantes.STAT_REAL_TIPO, 3), 16)
            if (tipoVivo == 82 && expActual >= 126) {
                expAdicional = 0
                expActual = 144
            }
            objevivo.addStatTexto(Constantes.STAT_EXP_OBJEVIVO, "0#0#" + Integer.toHexString(expActual + expAdicional))
            _perso.restarCantObjOEliminar(idObjAlimento, 1, true)
            GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(_perso, objetoObj)
            GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso)
            GestorSQL.SALVAR_OBJETO(objevivo)
        } catch (e: Exception) {
            GestorSalida.ENVIAR_BN_NADA(_perso, "ALIMENTAR OBJEVIVO")
        }
    }

    private fun objeto_Desequipar_Objevivo(packet: String?) {
        try {
            if (!_perso.estaDisponible(true, true)) {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return
            }
            val objeto: Objeto = _perso.getObjeto(Integer.parseInt(packet.substring(2).split(Pattern.quote("|")).get(0)))
            val objevivo: Objeto = Mundo.getObjeto(objeto.getObjevivoID())
            if (objevivo != null) {
                objevivo.addStatTexto(Constantes.STAT_REAL_GFX, "0#0#0")
                _perso.addObjetoConOAKO(objevivo, true)
                GestorSQL.SALVAR_OBJETO(objevivo)
            }
            objeto.setIDObjevivo(0)
            GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(_perso, objeto)
            if (Constantes.esPosicionVisual(objeto.getPosicion())) {
                _perso.cambiarRopaVisual()
            }
        } catch (e: Exception) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
        }
    }

    private fun objeto_Desasociar_Mimobionte(packet: String?) {
        try {
            if (!_perso.estaDisponible(true, true)) {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return
            }
            val objeto: Objeto = _perso.getObjeto(Integer.parseInt(packet.substring(2)))
            if (objeto == null) {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return
            }
            if (MainServidor.MIMOVIOENTE_REGRESAR) {
                val regresar: Int = Integer.parseInt(objeto.getParamStatTexto(Constantes.STAT_APARIENCIA_OBJETO, 3), 16)
                _perso.addObjIdentAInventario(Mundo.getObjetoModelo(regresar).crearObjeto(1,
                        Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM), false)
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "021;1~$regresar")
                GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso)
            }
            objeto.addStatTexto(Constantes.STAT_APARIENCIA_OBJETO, "")
            GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(_perso, objeto)
            if (Constantes.esPosicionVisual(objeto.getPosicion())) {
                _perso.cambiarRopaVisual()
            }
        } catch (e: Exception) {
        }
    }

    private fun objeto_Alimentar_Mascota(comida: Objeto?, mascObj: Objeto?): Int {
        val r = -1
        try {
            val mascota: MascotaModelo = Mundo.getMascotaModelo(mascObj.getObjModeloID())
            if (comida.getObjModeloID() === 2239) { // polvo de aniripsa
                val pdv: Int = mascObj.getPDV()
                if (pdv >= 10) {
                    GestorSalida.ENVIAR_BN_NADA(_perso)
                    return r
                }
                mascObj.addStatTexto(Constantes.STAT_PUNTOS_VIDA, "0#0#" + Integer.toHexString(pdv + 1))
            } else if (MainServidor.PARAM_ALIMENTAR_MASCOTAS && !mascota.esDevoradorAlmas()
                    && mascota.getComida(comida.getObjModeloID()) != null) {
                if (mascObj.horaComer(false, Constantes.CORPULENCIA_NORMAL)) {
                    mascObj.comerComida(comida.getObjModeloID())
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "032")
                    GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso)
                } else {
                    val corpulencia: Int = mascObj.getCorpulencia()
                    mascObj.setCorpulencia(Constantes.CORPULENCIA_OBESO)
                    if (corpulencia == Constantes.CORPULENCIA_OBESO) {
                        _perso.restarVidaMascota(mascObj)
                    }
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "026")
                }
            } else {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "153")
                return r
            }
            _perso.restarCantObjOEliminar(comida.getID(), 1, true)
            GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(_perso, mascObj)
        } catch (e: Exception) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
        }
        return r
    }

    private fun analizar_Dialogos(packet: String?) {
        when (packet.charAt(1)) {
            'C' -> dialogo_Iniciar(packet)
            'B' -> {
            }
            'R' -> dialogo_Respuesta(packet)
            'V' -> _perso.dialogoFin()
            else -> {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return
            }
        }
    }

    private fun dialogo_Iniciar(packet: String?) {
        try {
            val id: Int = Integer.parseInt(packet.substring(2).split("\n").get(0))
            if (!_perso.puedeAbrir) {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return
            }
            _perso.setConversandoCon(id)
            if (!MainServidor.SON_DE_LUCIANO) {
                if (id > -100) {
                    val tt = intArrayOf(MisionObjetivoModelo.HABLAR_CON_NPC, MisionObjetivoModelo.VOLVER_VER_NPC,
                            MisionObjetivoModelo.ENSEÑAR_OBJETO_NPC, MisionObjetivoModelo.ENTREGAR_OBJETO_NPC)
                    _perso.verificarMisionesTipo(tt, null, false, 0)
                }
                var preguntador: Preguntador? = _perso
                var preguntaID = 0
                val npc: NPC = _perso.getMapa().getNPC(id)
                if (npc != null) {
                    _perso.confirmarLogrosNPC(npc.getModeloID().toString() + "")
                    preguntaID = npc.getPreguntaID(_perso)
                } else {
                    val recau: Recaudador = _perso.getMapa().getRecaudador()
                    if (recau != null) {
                        if (recau.getID() === id) {
                            preguntaID = 1
                            preguntador = recau
                        }
                    }
                }
                if (preguntaID == 0) {
                    _perso.dialogoFin()
                    return
                }
                var pregunta: PreguntaNPC? = Mundo.getPreguntaNPC(preguntaID)
                if (pregunta == null) {
                    pregunta = PreguntaNPC(preguntaID, "", "", "")
                    Mundo.addPreguntaNPC(pregunta)
                }
                val str: String = pregunta.stringArgParaDialogo(_perso, preguntador)
                GestorSalida.ENVIAR_DCK_CREAR_DIALOGO(_perso, id)
                GestorSalida.ENVIAR_DQ_DIALOGO_PREGUNTA(_perso, str)
            } else {
                var preguntador: Preguntador? = _perso
                var preguntaID = 0
                val npc: NPC = _perso.getMapa().getNPC(id)
                if (npc != null) {
                    preguntaID = npc.getPreguntaID(_perso)
                } else {
                    val recau: Recaudador = _perso.getMapa().getRecaudador()
                    if (recau != null) {
                        if (recau.getID() === id) {
                            preguntaID = 1
                            preguntador = recau
                        }
                    }
                }
                if (preguntaID == 0) {
                    _perso.dialogoFin()
                    return
                }
                var pregunta: PreguntaNPC? = Mundo.getPreguntaNPC(preguntaID)
                if (pregunta == null) {
                    pregunta = PreguntaNPC(preguntaID, "", "", "")
                    Mundo.addPreguntaNPC(pregunta)
                }
                val str: String = pregunta.stringArgParaDialogo(_perso, preguntador)
                GestorSalida.ENVIAR_DCK_CREAR_DIALOGO(_perso, id)
                GestorSalida.ENVIAR_DQ_DIALOGO_PREGUNTA(_perso, str)
                if (id > -100) {
                    val tt = intArrayOf(MisionObjetivoModelo.HABLAR_CON_NPC, MisionObjetivoModelo.VOLVER_VER_NPC,
                            MisionObjetivoModelo.ENSEÑAR_OBJETO_NPC, MisionObjetivoModelo.ENTREGAR_OBJETO_NPC)
                    _perso.verificarMisionesTipo(tt, null, false, 0)
                }
            }
        } catch (e: Exception) {
            _perso.dialogoFin()
        }
    }

    private fun dialogo_Respuesta(packet: String?) {
        val infos: Array<String?> = packet.substring(2).split(Pattern.quote("|"))
        try {
            val preguntaID: Int = Integer.parseInt(infos[0])
            if (_perso.getConversandoCon() >= 0 || _perso.getPreguntaID() === 0 || preguntaID != _perso.getPreguntaID()) {
                _perso.dialogoFin()
                return
            }
            val respuestaID: Int = Integer.parseInt(infos[1])
            val pregunta: PreguntaNPC = Mundo.getPreguntaNPC(preguntaID)
            val respuesta: RespuestaNPC = Mundo.getRespuestaNPC(respuestaID)
            if (pregunta.getRespuestas().contains(respuestaID)) {
                respuesta.aplicar(_perso)
                if (_perso.getPreguntaID() === 0) {
                    _perso.dialogoFin()
                }
            } else {
                _perso.dialogoFin()
            }
        } catch (e: Exception) {
            _perso.dialogoFin()
        }
    }

    private fun analizar_Intercambios(packet: String?) {
        when (packet.charAt(1)) {
            'A' -> intercambio_Aceptar()
            'B' -> intercambio_Comprar(packet)
            'f' -> intercambio_Cercado(packet)
            'F' -> if (_perso.getTrabajo() != null) {
                _perso.getTrabajo().mostrarProbabilidades(_perso)
            }
            'H' -> intercambio_Mercadillo(packet)
            'J' -> intercambio_Oficios(packet)
            'K' -> intercambio_Boton_OK()
            'L' -> intercambio_Repetir_Ult_Craft()
            'M' -> intercambio_Mover_Objeto(packet)
            'q' -> intercambio_Preg_Mercante()
            'P' -> intercambio_Pago_Por_Trabajo(packet)
            'Q' -> intercambio_Ok_Mercante()
            'r' -> intercambio_Establo(packet)
            'R' -> intercambio_Iniciar(packet)
            'S' -> intercambio_Vender(packet)
            'V' -> intercambio_Cerrar()
            'W' -> intercambio_Oficio_Publico(packet)
            else -> {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return
            }
        }
    }

    @Synchronized
    private fun intercambio_Iniciar(packet: String?) {
        try {
            val split: Array<String?> = packet.substring(2).split(Pattern.quote("|"))
            var tipo: Byte = -1
            try {
                tipo = Byte.parseByte(split[0])
            } catch (e: Exception) {
            }
            if (!_perso.puedeAbrir) {
                GestorSalida.ENVIAR_BN_NADA(this)
                return
            }
            if ((_perso.getTipoExchange() === Constantes.INTERCAMBIO_TIPO_MERCADILLO_COMPRAR
                            && tipo == Constantes.INTERCAMBIO_TIPO_MERCADILLO_VENDER)
                    || (_perso.getTipoExchange() === Constantes.INTERCAMBIO_TIPO_MERCADILLO_VENDER
                            && tipo == Constantes.INTERCAMBIO_TIPO_MERCADILLO_COMPRAR)) {
                // nada
            } else if (!_perso.estaDisponible(true, true)) {
                GestorSalida.ENVIAR_BN_NADA(_perso, "INTERCAMBIO NO ESTA DISPONIBLE")
                return
            }
            if (_perso.getConsultarCofre() != null) {
                GestorSalida.ENVIAR_BN_NADA(_perso, "INTERCAMBIO CONSULTAR COFRE")
                return
            }
            if (_perso.getConsultarCasa() != null) {
                GestorSalida.ENVIAR_BN_NADA(_perso, "INTERCAMBIO CONSULTAR CASA")
                return
            }
            if (realizandoAccion) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1YOU_ALREADY_EXCHANGE")
                return
            }
            when (tipo) {
                Constantes.INTERCAMBIO_TIPO_MERCADILLO_COMPRAR, Constantes.INTERCAMBIO_TIPO_MERCADILLO_VENDER -> {
                    if (_perso.getDeshonor() >= 5) {
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "183")
                        GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_perso, "")
                        return
                    }
                    if (_perso.getExchanger() != null) {
                        GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_perso, "")
                    }
                    val mercadillo: Mercadillo = Mundo.getPuestoPorMapa(_perso.getMapa().getID())
                    if (mercadillo == null) {
                        GestorSalida.ENVIAR_BN_NADA(_perso)
                        return
                    }
                    _perso.setExchanger(mercadillo)
                    GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(_perso, tipo,
                            "1,10,100;" + mercadillo.getTipoObjPermitidos().toString() + ";" + mercadillo.getPorcentajeImpuesto().toString() + ";"
                                    + mercadillo.getNivelMax().toString() + ";" + mercadillo.getMaxObjCuenta().toString() + ";-1;"
                                    + mercadillo.getTiempoVenta())
                    if (tipo == Constantes.INTERCAMBIO_TIPO_MERCADILLO_VENDER) { // mercadillo vender
                        GestorSalida.ENVIAR_EL_LISTA_EXCHANGER(_perso, mercadillo)
                    }
                }
                Constantes.INTERCAMBIO_TIPO_TALLER_ARTESANO -> {
                    val invitadoID: Int = Integer.parseInt(split[1])
                    val trabajoID: Int = Integer.parseInt(split[2])
                    var trabajo: Trabajo? = null
                    var paso = false
                    for (statOficio in _perso.getStatsOficios().values()) {
                        if (statOficio.getPosicion() === 7) {
                            continue
                        }
                        for (t in statOficio.trabajosARealizar()) {
                            if (t.getTrabajoID() !== trabajoID) {
                                continue
                            }
                            trabajo = t
                            paso = true
                            break
                        }
                        if (paso) {
                            break
                        }
                    }
                    if (trabajo == null) {
                        GestorSalida.ENVIAR_BN_NADA(_perso)
                        return
                    }
                    val invitandoA: Personaje = Mundo.getPersonaje(invitadoID)
                    if (invitandoA == null || invitandoA === _perso) {
                        GestorSalida.ENVIAR_BN_NADA(_perso)
                        return
                    }
                    if (invitandoA.getCuenta().getSocket().getRealizandoAccion()) {
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1ALREADY_EXCHANGE")
                        return
                    }
                    if (!invitandoA.estaVisiblePara(_perso)) {
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1209")
                        return
                    }
                    _perso.setInvitandoA(invitandoA, "taller")
                    invitandoA.setInvitador(_perso, "taller")
                    _perso.setExchanger(trabajo)
                    GestorSalida.ENVIAR_ERK_CONSULTA_INTERCAMBIO(_perso, _perso.getID(), invitandoA.getID(),
                            Constantes.INTERCAMBIO_TIPO_TALLER_ARTESANO)
                    GestorSalida.ENVIAR_ERK_CONSULTA_INTERCAMBIO(invitandoA, _perso.getID(), invitandoA.getID(),
                            Constantes.INTERCAMBIO_TIPO_TALLER_CLIENTE)
                }
                Constantes.INTERCAMBIO_TIPO_MONTURA -> {
                    if (_perso.getMontura() == null) {
                        GestorSalida.ENVIAR_BN_NADA(_perso)
                        return
                    }
                    _perso.setExchanger(_perso.getMontura())
                    GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(_perso, tipo, _perso.getMontura().getID().toString() + "")
                    GestorSalida.ENVIAR_EL_LISTA_EXCHANGER(_perso, _perso.getMontura())
                    GestorSalida.ENVIAR_Ew_PODS_MONTURA(_perso)
                }
                Constantes.INTERCAMBIO_TIPO_TIENDA_NPC -> {
                    val npcID: Int = Integer.parseInt(split[1])
                    val npc: NPC = _perso.getMapa().getNPC(npcID)
                    if (npc == null) {
                        GestorSalida.ENVIAR_BN_NADA(_perso)
                        return
                    }
                    _perso.setExchanger(npc)
                    GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(_perso, tipo, npcID.toString() + "")
                    GestorSalida.ENVIAR_EL_LISTA_EXCHANGER(_perso, npc)
                }
                Constantes.INTERCAMBIO_TIPO_PERSONAJE -> {
                    val objetidoID: Int = Integer.parseInt(split[1])
                    val invitandoA2: Personaje = Mundo.getPersonaje(objetidoID)
                    if (invitandoA2 == null || invitandoA2 === _perso || invitandoA2.getMapa() !== _perso.getMapa() || !invitandoA2.enLinea()) {
                        GestorSalida.ENVIAR_ERE_ERROR_CONSULTA(_perso, 'E')
                        return
                    }
                    if (!invitandoA2.estaVisiblePara(_perso)) {
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1209")
                        return
                    }
                    if (!invitandoA2.estaDisponible(false, true)) {
                        GestorSalida.ENVIAR_ERE_ERROR_CONSULTA(_perso, 'O')
                        return
                    }
                    if (invitandoA2.getCuenta().getSocket().getRealizandoAccion()) {
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1ALREADY_EXCHANGE")
                        return
                    }
                    _perso.setInvitandoA(invitandoA2, "intercambio")
                    invitandoA2.setInvitador(_perso, "intercambio")
                    GestorSalida.ENVIAR_ERK_CONSULTA_INTERCAMBIO(_perso, _perso.getID(), invitandoA2.getID(),
                            Constantes.INTERCAMBIO_TIPO_PERSONAJE)
                    GestorSalida.ENVIAR_ERK_CONSULTA_INTERCAMBIO(invitandoA2, _perso.getID(), invitandoA2.getID(),
                            Constantes.INTERCAMBIO_TIPO_PERSONAJE)
                    GestorSalida.ENVIAR_wl_NOTIFYCACION(invitandoA2, _perso.getNombre(), 3, 0)
                    if (_perso.getCuenta().getuuid().equalsIgnoreCase(invitandoA2.getCuenta().getuuid())) {
                        val invitador: Personaje? = _perso
                        val intercambio = Intercambio(invitador, invitandoA2)
                        invitador.setTipoExchange(Constantes.INTERCAMBIO_TIPO_PERSONAJE)
                        invitandoA2.setTipoExchange(Constantes.INTERCAMBIO_TIPO_PERSONAJE)
                        invitador.setExchanger(intercambio)
                        invitandoA2.setExchanger(intercambio)
                        GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(invitador, Constantes.INTERCAMBIO_TIPO_PERSONAJE, "")
                        GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(invitandoA2, Constantes.INTERCAMBIO_TIPO_PERSONAJE,
                                "")
                        invitador.setInvitandoA(null, "")
                        invitandoA2.setInvitador(null, "")
                        invitador.Exchangemismaip = invitandoA2
                        invitandoA2.Exchangemismaip = invitador
                    }
                }
                Constantes.INTERCAMBIO_TIPO_TRUEQUE, Constantes.INTERCAMBIO_TIPO_RESUCITAR_MASCOTA, Constantes.INTERCAMBIO_TIPO_TRUEQUE_MONTURA -> {
                    val npcID2: Int = Integer.parseInt(split[1])
                    val npc2: NPC = _perso.getMapa().getNPC(npcID2)
                    if (npc2 == null) {
                        GestorSalida.ENVIAR_BN_NADA(_perso)
                        return
                    }
                    val trueque = Trueque(_perso, tipo == Constantes.INTERCAMBIO_TIPO_RESUCITAR_MASCOTA,
                            npc2.getModeloID())
                    _perso.setExchanger(trueque)
                    GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(_perso, Constantes.INTERCAMBIO_TIPO_TRUEQUE, "" + npcID2)
                }
                Constantes.INTERCAMBIO_TIPO_MERCANTE -> {
                    val mercanteID: Int = Integer.parseInt(split[1])
                    val mercante: Personaje = Mundo.getPersonaje(mercanteID)
                    if (mercante == null) {
                        GestorSalida.ENVIAR_BN_NADA(_perso)
                        return
                    }
                    _perso.setExchanger(mercante)
                    GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(_perso, tipo, mercanteID.toString() + "")
                    GestorSalida.ENVIAR_EL_LISTA_EXCHANGER(_perso, mercante)
                }
                Constantes.INTERCAMBIO_TIPO_MI_TIENDA -> if (!MainServidor.PARAM_DESACTIVAR_MERCANTE) {
                    _perso.setExchanger(_perso.getTienda())
                    GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(_perso, tipo, _perso.getID().toString() + "")
                    GestorSalida.ENVIAR_EL_LISTA_EXCHANGER(_perso, _perso)
                } else {
                    GestorSalida.ENVIAR_BN_NADA(this)
                    _perso.setExchanger(null)
                    return
                }
                Constantes.INTERCAMBIO_TIPO_RECAUDADOR -> {
                    val recaudaID: Int = Integer.parseInt(split[1])
                    val recaudador: Recaudador = Mundo.getRecaudador(recaudaID)
                    if (recaudador == null || recaudador.getPelea() != null || recaudador.getEnRecolecta()
                            || _perso.getGremio() == null) {
                        GestorSalida.ENVIAR_BN_NADA(_perso)
                        return
                    }
                    if (recaudador.getGremio().getID() !== _perso.getGremio().getID()) {
                        GestorSalida.ENVIAR_BN_NADA(_perso, "NO ES DEL GREMIO")
                        return
                    }
                    if (!_perso.getMiembroGremio().puede(Constantes.G_RECOLECTAR_RECAUDADOR)) {
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1101")
                        return
                    }
                    recaudador.setEnRecolecta(true)
                    GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(_perso, tipo, recaudador.getID().toString() + "")
                    GestorSalida.ENVIAR_EL_LISTA_EXCHANGER(_perso, recaudador)
                    _perso.setExchanger(recaudador)
                }
                else -> {
                    GestorSalida.ENVIAR_BN_NADA(_perso)
                    return
                }
            }
            _perso.setTipoExchange(tipo)
        } catch (e: Exception) {
            MainServidor.redactarLogServidorln("EXCEPTION Packet " + packet + ", intercambio_Iniciar " + e.toString())
            e.printStackTrace()
        }
    }

    @Synchronized
    private fun intercambio_Aceptar() {
        when (_perso.getTipoInvitacion()) {
            "taller" -> {
                val artesano: Personaje = _perso.getInvitador()
                if (artesano == null) {
                    GestorSalida.ENVIAR_BN_NADA(_perso, "INTERCAMBIO ACEPTAR ARTESANO NULO")
                    return
                }
                val trabajo: Trabajo = artesano.getIntercambiandoCon(Trabajo::class.java) as Trabajo
                if (trabajo == null) {
                    GestorSalida.ENVIAR_BN_NADA(_perso, "INTERCAMBIO ACEPTAR TRABAJO NULO")
                    return
                }
                // final InvitarTaller taller = new InvitarTaller(artesano, _perso, trabajo);
                artesano.setTipoExchange(Constantes.INTERCAMBIO_TIPO_TALLER_ARTESANO)
                _perso.setTipoExchange(Constantes.INTERCAMBIO_TIPO_TALLER_CLIENTE)
                _perso.setExchanger(trabajo)
                GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(artesano, Constantes.INTERCAMBIO_TIPO_TALLER_ARTESANO,
                        trabajo.getCasillasMax().toString() + ";" + trabajo.getTrabajoID())
                GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(_perso, Constantes.INTERCAMBIO_TIPO_TALLER_CLIENTE,
                        trabajo.getCasillasMax().toString() + ";" + trabajo.getTrabajoID())
                _perso.setInvitador(null, "")
                artesano.setInvitandoA(null, "")
                trabajo.setArtesanoCliente(artesano, _perso)
            }
            "intercambio" -> {
                val invitador: Personaje = _perso.getInvitador()
                if (invitador == null) {
                    GestorSalida.ENVIAR_BN_NADA(_perso, "INTERCAMBIO ACEPTAR INTERCAMBIO NULO")
                    return
                }
                val intercambio = Intercambio(invitador, _perso)
                invitador.setTipoExchange(Constantes.INTERCAMBIO_TIPO_PERSONAJE)
                _perso.setTipoExchange(Constantes.INTERCAMBIO_TIPO_PERSONAJE)
                invitador.setExchanger(intercambio)
                _perso.setExchanger(intercambio)
                GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(invitador, Constantes.INTERCAMBIO_TIPO_PERSONAJE, "")
                GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(_perso, Constantes.INTERCAMBIO_TIPO_PERSONAJE, "")
                _perso.setInvitador(null, "")
                invitador.setInvitandoA(null, "")
            }
        }
    }

    @Synchronized
    private fun intercambio_Cerrar() {
        _perso.cerrarExchange("")
    }

    @Synchronized
    private fun intercambio_Boton_OK() {
        if (_perso.getExchanger() != null) {
            _perso.getExchanger().botonOK(_perso)
            try {
                val invitado: Personaje = _perso.Exchangemismaip
                if (invitado != null) {
                    invitado.getExchanger().botonOK(invitado)
                    invitado.Exchangemismaip = null
                }
                _perso.Exchangemismaip = null
            } catch (e: Exception) {
            }
        }
    }

    private fun intercambio_Oficios(packet: String?) {
        when (packet.charAt(2)) {
            'F' -> {
                val idOficio: Int = Integer.parseInt(packet.substring(3))
                for (artesano in Mundo.getPersonajesEnLinea()) {
                    val mapa: Mapa = artesano.getMapa()
                    for (oficio in artesano.getStatsOficios().values()) {
                        if (oficio.getLibroArtesano() && oficio.getOficio().getID() === idOficio) {
                            GestorSalida.ENVIAR_EJ_DESCRIPCION_LIBRO_ARTESANO(_perso,
                                    "+" + oficio.getOficio().getID().toString() + ";" + artesano.getID().toString() + ";" + artesano.getNombre()
                                            .toString() + ";" + oficio.getNivel().toString() + ";" + mapa.getID().toString() + ";"
                                            + (if (mapa.getTrabajos().isEmpty()) 0 else 1).toString() + ";" + artesano.getClaseID(true).toString() + ";"
                                            + artesano.getSexo().toString() + ";" + artesano.getColor1().toString() + "," + artesano.getColor2()
                                            .toString() + "," + artesano.getColor3().toString() + ";" + artesano.getStringAccesorios().toString() + ";"
                                            + oficio.getOpcionBin().toString() + "," + oficio.getSlotsPublico())
                        }
                    }
                }
                GestorSalida.ENVIAR_BN_NADA(_perso)
            }
            else -> {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return
            }
        }
    }

    private fun intercambio_Oficio_Publico(packet: String?) {
        when (packet.charAt(2)) {
            '+' -> {
                GestorSalida.ENVIAR_EW_OFICIO_MODO_PUBLICO(_perso, "+")
                for (oficio in _perso.getStatsOficios().values()) {
                    oficio.setLibroArtesano(true)
                }
                _perso.packetModoInvitarTaller(null, false)
            }
            '-' -> {
                // for (StatsOficio SO : _perso.getStatsOficios().values()) {
                // if (SO.getPosicion() != 7)
                // GestorSalida.ENVIAR_Ej_AGREGAR_LIBRO_ARTESANO(_perso, "-" +
                // SO.getOficio().getID());
                // }
                for (oficio in _perso.getStatsOficios().values()) {
                    oficio.setLibroArtesano(false)
                }
                GestorSalida.ENVIAR_EW_OFICIO_MODO_PUBLICO(_perso, "-")
                GestorSalida.ENVIAR_EW_OFICIO_MODO_INVITACION(_perso, "-", _perso.getID(), "")
            }
            else -> {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return
            }
        }
    }

    private fun intercambio_Mover_Objeto(packet: String?) {
        try {
            when (packet.charAt(2)) {
                'G' -> try {
                    var kamas: Long = 0
                    try {
                        kamas = Long.parseLong(packet.substring(3))
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(_perso, "KAMAS EXCEPTION")
                        return
                    }
                    if (kamas < 0) { // retirar
                        kamas = Math.abs(kamas)
                        when (_perso.getTipoExchange()) {
                            Constantes.INTERCAMBIO_TIPO_RECAUDADOR, Constantes.INTERCAMBIO_TIPO_COFRE -> {
                                if (_perso.getExchanger() == null) {
                                    return
                                }
                                if (_perso.getExchanger().getKamas() < kamas) {
                                    kamas = _perso.getExchanger().getKamas()
                                }
                                _perso.getExchanger().addKamas(-kamas, _perso)
                                _perso.addKamas(kamas, false, true)
                                GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(_perso,
                                        "G" + _perso.getExchanger().getKamas())
                            }
                        }
                    } else { // si kamas > 0
                        if (_perso.getKamas() < kamas) {
                            kamas = _perso.getKamas()
                        }
                        when (_perso.getTipoExchange()) {
                            Constantes.INTERCAMBIO_TIPO_RECAUDADOR, Constantes.INTERCAMBIO_TIPO_COFRE -> {
                                if (_perso.getExchanger() == null) {
                                    return
                                }
                                _perso.getExchanger().addKamas(kamas, _perso)
                                _perso.addKamas(-kamas, false, true)
                                GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(_perso,
                                        "G" + _perso.getExchanger().getKamas())
                            }
                            Constantes.INTERCAMBIO_TIPO_PERSONAJE -> {
                                if (_perso.getExchanger() == null) {
                                    return
                                }
                                _perso.getExchanger().addKamas(kamas, _perso)
                            }
                        }
                    }
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BN_NADA(_perso, "INTERCAMBIO MOVER OBJETO KAMAS")
                    MainServidor.redactarLogServidorln(
                            "EXCEPTION Packet " + packet + ", intercambio_Mover_Objeto(kamas) " + e.toString())
                    e.printStackTrace()
                }
                'O' -> {
                    try {
                        val sp: String = packet.substring(3).replace("-", ";-").replace("+", ";+")
                        var varios = false
                        val split: Array<String?> = sp.split(Pattern.quote(";"))
                        if (MainServidor.PARAM_MOVER_MULTIPLE_OBJETOS_SOLO_ABONADOS && _perso.getCuenta().getVip() === 0) {
                            if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                                GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, "Réservé au V.I.P", "B9121B")
                            } else {
                                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1ONLY_FOR_VIP")
                            }
                            return
                        }
                        var i = 0
                        for (sPacket in split) {
                            if (sPacket.isEmpty()) {
                                continue
                            }
                            if (varios) {
                                Thread.sleep(500) // es para evitar lag en packets
                            }
                            if (split.size > 2) varios = true
                            if (varios) {
                                GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso,
                                        "Faltan " + (split.size - ++i) + " segundos para terminar la operacion",
                                        Constantes.COLOR_ROJO)
                            }
                            val infos: Array<String?> = sPacket.substring(1).split(Pattern.quote("|"))
                            var id = -1
                            var cantidad: Long = -1
                            var precio = 0
                            try {
                                id = Integer.parseInt(infos[0])
                                cantidad = Integer.parseInt(infos[1])
                            } catch (e: Exception) {
                            }
                            val objeto: Objeto = Mundo.getObjeto(id)
                            if (cantidad < 0 || objeto == null) {
                                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1OBJECT_DONT_EXIST~$id")
                                continue
                            }
                            if (objeto.getObjModelo().getTipo() === Constantes.OBJETO_TIPO_OBJETO_MISION) {
                                GestorSalida.ENVIAR_BN_NADA(_perso, "INTERCAMBIO MOVER TIPO MISION")
                                continue
                            }
                            if (objeto.getObjModelo().getTipo() === Constantes.OBJETO_TIPO_OBJETO_DE_BUSQUEDA) {
                                GestorSalida.ENVIAR_BN_NADA(_perso, "INTERCAMBIO MOVER TIPO BUSQUEDA")
                                continue
                            }
                            if (_perso.getTipoExchange() !== Constantes.INTERCAMBIO_TIPO_COFRE) {
                                if (objeto.tieneStatTexto(Constantes.STAT_LIGADO_A_CUENTA)) {
                                    GestorSalida.ENVIAR_BN_NADA(_perso, "INTERCAMBIO MOVER LIGADO")
                                    continue
                                }
                                if (!objeto.pasoIntercambiableDesde()) {
                                    GestorSalida.ENVIAR_BN_NADA(_perso, "INTERCAMBIO MOVER NO INTERCAMBIABLE")
                                    return
                                }
                            }
                            val c: Char = sPacket.charAt(0)
                            when (c) {
                                '+' -> {
                                    if (cantidad > objeto.getCantidad()) {
                                        cantidad = objeto.getCantidad()
                                    }
                                    when (_perso.getTipoExchange()) {
                                        Constantes.INTERCAMBIO_TIPO_TALLER, Constantes.INTERCAMBIO_TIPO_RECAUDADOR, Constantes.INTERCAMBIO_TIPO_PERSONAJE, Constantes.INTERCAMBIO_TIPO_TALLER_ARTESANO, Constantes.INTERCAMBIO_TIPO_TALLER_CLIENTE, Constantes.INTERCAMBIO_TIPO_TRUEQUE, Constantes.INTERCAMBIO_TIPO_MI_TIENDA, Constantes.INTERCAMBIO_TIPO_MERCADILLO_VENDER, Constantes.INTERCAMBIO_TIPO_RESUCITAR_MASCOTA, Constantes.INTERCAMBIO_TIPO_MONTURA, Constantes.INTERCAMBIO_TIPO_COFRE, Constantes.INTERCAMBIO_TIPO_TRUEQUE_MONTURA -> {
                                            if (_perso.getExchanger() == null) {
                                                continue
                                            }
                                            try {
                                                precio = Integer.parseInt(infos[2])
                                            } catch (e: Exception) {
                                            }
                                            if (precio < 0) {
                                                GestorSalida.ENVIAR_BN_NADA(_perso)
                                                continue
                                            }
                                            _perso.getExchanger().addObjetoExchanger(objeto, cantidad, _perso, precio)
                                        }
                                        else -> if (MainServidor.MODO_IMPRIMIR_LOG) System.out.println("Tipo de intercambio incorrepto: " + _perso.getTipoExchange())
                                    }
                                }
                                '-' -> when (_perso.getTipoExchange()) {
                                    Constantes.INTERCAMBIO_TIPO_PERSONAJE, Constantes.INTERCAMBIO_TIPO_TALLER_ARTESANO, Constantes.INTERCAMBIO_TIPO_TALLER_CLIENTE, Constantes.INTERCAMBIO_TIPO_TRUEQUE, Constantes.INTERCAMBIO_TIPO_TRUEQUE_MONTURA -> {
                                        if (_perso.getObjeto(id) == null) {
                                            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1OBJECT_DONT_EXIST")
                                            continue
                                        }
                                        if (_perso.getExchanger() == null) {
                                            continue
                                        }
                                        try {
                                            precio = Integer.parseInt(infos[2])
                                        } catch (e: Exception) {
                                        }
                                        if (precio < 0) {
                                            GestorSalida.ENVIAR_BN_NADA(_perso)
                                            continue
                                        }
                                        _perso.getExchanger().remObjetoExchanger(objeto, cantidad, _perso, precio)
                                    }
                                    Constantes.INTERCAMBIO_TIPO_TALLER, Constantes.INTERCAMBIO_TIPO_RECAUDADOR, Constantes.INTERCAMBIO_TIPO_MI_TIENDA, Constantes.INTERCAMBIO_TIPO_MERCADILLO_VENDER, Constantes.INTERCAMBIO_TIPO_RESUCITAR_MASCOTA, Constantes.INTERCAMBIO_TIPO_MONTURA, Constantes.INTERCAMBIO_TIPO_COFRE -> {
                                        if (_perso.getExchanger() == null) {
                                            continue
                                        }
                                        try {
                                            precio = Integer.parseInt(infos[2])
                                        } catch (e: Exception) {
                                        }
                                        if (precio < 0) {
                                            GestorSalida.ENVIAR_BN_NADA(_perso)
                                            continue
                                        }
                                        _perso.getExchanger().remObjetoExchanger(objeto, cantidad, _perso, precio)
                                    }
                                    else -> if (MainServidor.MODO_IMPRIMIR_LOG) System.out.println("Tipo de intercambio incorrepto: " + _perso.getTipoExchange())
                                }
                            }
                        }
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BN_NADA(_perso, "INTERCAMBIO MOVER OBJETO")
                        MainServidor.redactarLogServidorln(
                                "EXCEPTION Packet " + packet + ", intercambio_Mover_Objeto " + e.toString())
                        e.printStackTrace()
                    }
                    GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso)
                    if (_perso.getTipoExchange() === Constantes.INTERCAMBIO_TIPO_MONTURA && _perso.getMontura() != null) {
                        GestorSalida.ENVIAR_Ew_PODS_MONTURA(_perso)
                    }
                }
                'R' -> when (_perso.getTipoExchange()) {
                    Constantes.INTERCAMBIO_TIPO_TALLER -> {
                        val trabajo: Trabajo = _perso.getIntercambiandoCon(Trabajo::class.java) as Trabajo
                        if (trabajo == null) {
                            GestorSalida.ENVIAR_BN_NADA(_perso, "INTERCAMBIO MOVER OBJETO TRABAJO NULL 'R'")
                            return
                        }
                        if (trabajo.esCraft()) {
                            trabajo.craftearXVeces(Integer.parseInt(packet.substring(3)))
                        }
                    }
                }
                'r' -> when (_perso.getTipoExchange()) {
                    Constantes.INTERCAMBIO_TIPO_TALLER -> {
                        val trabajo: Trabajo = _perso.getIntercambiandoCon(Trabajo::class.java) as Trabajo
                        if (trabajo == null) {
                            GestorSalida.ENVIAR_BN_NADA(_perso, "INTERCAMBIO MOVER OBJETO TRABAJO NULL 'r'")
                            return
                        }
                        if (trabajo.esCraft()) {
                            trabajo.interrumpirReceta()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            GestorSalida.ENVIAR_BN_NADA(_perso, "INTERCAMBIO MOVER OBJETO FINAL")
            MainServidor.redactarLogServidorln(
                    "EXCEPTION Packet " + packet + ", intercambio_Mover_Objeto(final) " + e.toString())
            e.printStackTrace()
        }
    }

    private fun intercambio_Pago_Por_Trabajo(packet: String?) {
        val taller: Trabajo = _perso.getIntercambiandoCon(Trabajo::class.java) as Trabajo
        if (taller == null || !taller.esTaller()) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
            return
        }
        val tipoPago: Int = Integer.parseInt(packet.substring(2, 3))
        val caracter: Char = packet.charAt(3)
        val signo: Char = packet.charAt(4)
        when (caracter) {
            'G' -> {
                var kamas: Long = 0
                try {
                    kamas = Long.parseLong(packet.substring(4))
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BN_NADA(_perso)
                    return
                }
                if (kamas < 0) {
                    kamas = 0
                }
                taller.setKamas(tipoPago, kamas, _perso.getKamas())
            }
            'O' -> {
                val infos: Array<String?> = packet.substring(5).split(Pattern.quote("|"))
                var id = -1
                var cantidad: Long = 0
                try {
                    id = Integer.parseInt(infos[0])
                    cantidad = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                }
                val objeto: Objeto = _perso.getObjeto(id)
                if (cantidad <= 0 || objeto == null) {
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1OBJECT_DONT_EXIST")
                    return
                }
                if (objeto.tieneStatTexto(Constantes.STAT_LIGADO_A_CUENTA)) {
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1129")
                    return
                }
                if (!objeto.pasoIntercambiableDesde()) {
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1129")
                    return
                }
                val cantInter: Long = taller.getCantObjetoPago(id, tipoPago)
                when (signo) {
                    '+' -> {
                        val nuevaCant: Long = objeto.getCantidad() - cantInter
                        if (cantidad > nuevaCant) {
                            cantidad = nuevaCant.toInt().toLong()
                        }
                        taller.addObjetoPaga(objeto, cantidad, tipoPago)
                    }
                    '-' -> {
                        if (cantidad > cantInter) {
                            cantidad = cantInter
                        }
                        taller.quitarObjetoPaga(objeto, cantidad, tipoPago)
                    }
                }
            }
            else -> {
                val infos: Array<String?> = packet.substring(5).split(Pattern.quote("|"))
                var id = -1
                var cantidad: Long = 0
                try {
                    id = Integer.parseInt(infos[0])
                    cantidad = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                }
                val objeto: Objeto = _perso.getObjeto(id)
                if (cantidad <= 0 || objeto == null) {
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1OBJECT_DONT_EXIST")
                    return
                }
                if (objeto.tieneStatTexto(Constantes.STAT_LIGADO_A_CUENTA)) {
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1129")
                    return
                }
                if (!objeto.pasoIntercambiableDesde()) {
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1129")
                    return
                }
                val cantInter: Long = taller.getCantObjetoPago(id, tipoPago)
                when (signo) {
                    '+' -> {
                        val nuevaCant: Long = objeto.getCantidad() - cantInter
                        if (cantidad > nuevaCant) {
                            cantidad = nuevaCant.toInt().toLong()
                        }
                        taller.addObjetoPaga(objeto, cantidad, tipoPago)
                    }
                    '-' -> {
                        if (cantidad > cantInter) {
                            cantidad = cantInter
                        }
                        taller.quitarObjetoPaga(objeto, cantidad, tipoPago)
                    }
                }
            }
        }
    }

    private fun intercambio_Preg_Mercante() {
        if (_perso.getDeshonor() >= 4) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "183")
            return
        }
        if (!_perso.estaDisponible(false, false)) {
            return
        }
        val tasa = 1 // _perso.getNivel() / 2;
        var impuesto: Long = _perso.precioTotalTienda() * tasa / 1000
        val mapaID: Int = _perso.getMapa().getID()
        if (Constantes.esMapaMercante(mapaID)) {
            impuesto = 0
        }
        GestorSalida.ENVIAR_Eq_PREGUNTAR_MERCANTE(_perso, _perso.getObjetosTienda().size(), tasa, impuesto)
    }

    private fun intercambio_Ok_Mercante() {
        if (!_perso.estaDisponible(false, false)) {
            return
        }
        if (_perso.getObjetosTienda().isEmpty()) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "123")
            return
        }
        if (_perso.getMapa().cantMercantes() >= _perso.getMapa().getMaxMercantes()) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "125;" + _perso.getMapa().getMaxMercantes())
            return
        }
        if (!_perso.getCelda().librerParaMercante() || !_perso.getCelda().esCaminable(false)) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "124")
            return
        }
        var impuesto: Long = _perso.precioTotalTienda() / 1000
        val mapaID: Int = _perso.getMapa().getID()
        if (Constantes.esMapaMercante(mapaID)) {
            impuesto = 0
        }
        if (MainServidor.MODO_ALL_OGRINAS) {
            if (MainServidor.PARAM_MERCA_OGRINAS) {
                if (impuesto < 0 || _perso.getKamas() < impuesto) {
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "176")
                } else {
                    _perso.addKamas(-impuesto, false, true)
                    _perso.setMercante(true)
                    _perso.getMapa().addMercante(_perso)
                    cerrarSocket(true, "intercambio_Ok_Mercante()")
                }
            } else {
                _perso.setMercante(true)
                _perso.getMapa().addMercante(_perso)
                cerrarSocket(true, "intercambio_Ok_Mercante()")
            }
        } else {
            if (impuesto < 0 || _perso.getKamas() < impuesto) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "176")
            } else {
                _perso.addKamas(-impuesto, false, true)
                _perso.setMercante(true)
                _perso.getMapa().addMercante(_perso)
                cerrarSocket(true, "intercambio_Ok_Mercante()")
            }
        }
    }

    private fun intercambio_Mercadillo(packet: String?) {
        try {
            val mercadillo: Mercadillo = _perso.getIntercambiandoCon(Mercadillo::class.java) as Mercadillo
            if (mercadillo == null) {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return
            }
            when (packet.charAt(2)) {
                'B' -> {
                    val info: Array<String?> = packet.substring(3).split(Pattern.quote("|"))
                    if (mercadillo.comprarObjeto(Integer.parseInt(info[0]), Integer.parseInt(info[1]),
                                    Long.parseLong(info[2]), _perso)) {
                        GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso)
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "068")
                    } else {
                        if (!MainServidor.PARAM_MERCADILLO_OGRINAS) {
                            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "172")
                        }
                    }
                }
                'l' -> {
                    val str: String = mercadillo.strListaLineasPorModelo(Integer.parseInt(packet.substring(3)))
                    if (str.isEmpty()) {
                        GestorSalida.ENVIAR_EHM_MOVER_OBJMERCA_POR_MODELO(_perso, "-", Integer.parseInt(packet.substring(3)).toString() + "")
                    } else {
                        GestorSalida.ENVIAR_EHl_LISTA_LINEAS_OBJMERCA_POR_MODELO(_perso, str)
                    }
                }
                'P' -> GestorSalida.ENVIAR_EHP_PRECIO_PROMEDIO_OBJ(_perso, Integer.parseInt(packet.substring(3)),
                        Mundo.getObjetoModelo(Integer.parseInt(packet.substring(3))).getPrecioPromedio())
                'S' -> {
                    val splt: Array<String?> = packet.substring(3).split(Pattern.quote("|"))
                    if (mercadillo.esTipoDeEsteMercadillo(Integer.parseInt(splt[0]))) {
                        if (mercadillo.hayModeloEnEsteMercadillo(Integer.parseInt(splt[0]), Integer.parseInt(splt[1]))) {
                            GestorSalida.ENVIAR_EHS_BUSCAR_OBJETO_MERCADILLO(_perso, "K")
                            GestorSalida.ENVIAR_EHl_LISTA_LINEAS_OBJMERCA_POR_MODELO(_perso,
                                    mercadillo.strListaLineasPorModelo(Integer.parseInt(splt[1])))
                        } else {
                            GestorSalida.ENVIAR_EHS_BUSCAR_OBJETO_MERCADILLO(_perso, "E")
                        }
                    } else {
                        GestorSalida.ENVIAR_EHS_BUSCAR_OBJETO_MERCADILLO(_perso, "E")
                    }
                }
                'T' -> GestorSalida.ENVIAR_EHL_LISTA_OBJMERCA_POR_TIPO(_perso, Integer.parseInt(packet.substring(3)),
                        mercadillo.stringModelo(Integer.parseInt(packet.substring(3))))
                else -> {
                    GestorSalida.ENVIAR_BN_NADA(_perso)
                    return
                }
            }
        } catch (e: Exception) {
        }
    }

    @Synchronized
    private fun intercambio_Cercado(packet: String?) {
        try {
            _perso.setIsOnAction(false)
            val cercado: Cercado = _perso.getIntercambiandoCon(Cercado::class.java) as Cercado ?: return
            if (_perso.getDeshonor() >= 5) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "183")
                GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_perso, "")
                return
            }
            val c: Char = packet.charAt(2)
            val packet2: String = packet.substring(3)
            var id = -1
            id = try {
                Integer.parseInt(packet2)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return
            }
            var montura: Montura? = null
            when (c) {
                'g' -> {
                    if (!cercado.borrarMonturaCercado(id)) {
                        GestorSalida.ENVIAR_BN_NADA(_perso)
                        return
                    }
                    montura = Mundo.getMontura(id)
                    if (montura == null) {
                        GestorSalida.ENVIAR_BN_NADA(_perso)
                        return
                    }
                    GestorSalida.ENVIAR_Ef_MONTURA_A_CRIAR(_perso, '-', montura.getID().toString() + "")
                    GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(_perso.getMapa(), id)
                    if (!escapaDespuesParir(montura)) {
                        montura.setMapaCelda(null, null)
                        _cuenta.addMonturaEstablo(montura)
                        GestorSalida.ENVIAR_Ee_MONTURA_A_ESTABLO(_perso, '+', montura.detallesMontura())
                    }
                }
                'p' -> {
                    if (!cercado.puedeAgregar()) {
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1107")
                        return
                    }
                    if (!_cuenta.borrarMonturaEstablo(id)) {
                        GestorSalida.ENVIAR_BN_NADA(_perso)
                        return
                    }
                    montura = Mundo.getMontura(id)
                    if (_perso.getMontura() != null && _perso.getMontura().getID() === id) {
                        if (_perso.estaMontando()) {
                            _perso.subirBajarMontura(false)
                        }
                        _perso.setMontura(null)
                    }
                    montura.setMapaCelda(_perso.getMapa(), _perso.getMapa().getCelda(cercado.getCeldaMontura()))
                    cercado.addCriando(montura)
                    GestorSalida.ENVIAR_Ef_MONTURA_A_CRIAR(_perso, '+', montura.detallesMontura())
                    GestorSalida.ENVIAR_Ee_MONTURA_A_ESTABLO(_perso, '-', montura.getID().toString() + "")
                    GestorSalida.ENVIAR_GM_DRAGOPAVO_A_MAPA(_perso.getMapa(), "+", montura)
                }
                else -> {
                    GestorSalida.ENVIAR_BN_NADA(_perso)
                    return
                }
            }
        } catch (e: Exception) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
        }
    }

    @Synchronized
    private fun intercambio_Establo(packet: String?) {
        try {
            val cercado: Cercado = _perso.getIntercambiandoCon(Cercado::class.java) as Cercado ?: return
            if (_perso.getDeshonor() >= 5) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "183")
                GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_perso, "")
                return
            }
            val c: Char = packet.charAt(2)
            val packet2: String = packet.substring(3)
            var id = -1
            id = try {
                Integer.parseInt(packet2)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return
            }
            var montura: Montura? = null
            when (c) {
                'C' -> {
                    val obj: Objeto = _perso.getObjeto(id)
                    if (obj == null) {
                        GestorSalida.ENVIAR_BN_NADA(_perso)
                        return
                    }
                    if (!Condiciones.validaCondiciones(_perso, obj.getObjModelo().getCondiciones(), null)) {
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "119|43")
                        return
                    }
                    montura = Mundo.getMontura(-Math.abs(obj.getStatValor(Constantes.STAT_CONSULTAR_MONTURA)))
                    if (montura == null) {
                        val color: Int = Constantes.getColorMonturaPorCertificado(obj.getObjModeloID())
                        if (color < 1) {
                            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1MOUNT_COLOR_NOT_EXIST")
                            return
                        }
                        montura = Montura(color, _perso.getID(), true, false)
                    }
                    if (obj.getCantidad() <= 1) {
                        _perso.borrarOEliminarConOR(id, true)
                    } else {
                        obj.setCantidad(obj.getCantidad() - 1)
                        GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, obj)
                    }
                    if (montura.getPergamino() !== -1 && montura.getPergamino() !== obj.getID()) {
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1MOUNT_IS_NOT_CERTIFICATED")
                        return
                    }
                    if (!escapaDespuesParir(montura)) {
                        _cuenta.addMonturaEstablo(montura)
                        montura.setDueñoID(_perso.getID())
                        montura.setPergamino(0)
                        GestorSalida.ENVIAR_Ee_MONTURA_A_ESTABLO(_perso, '+', montura.detallesMontura())
                    }
                }
                'c' -> {
                    montura = Mundo.getMontura(id)
                    if (montura == null || !_cuenta.borrarMonturaEstablo(id)) {
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1104")
                        return
                    }
                    if (montura.getPergamino() > 0) {
                        GestorSalida.ENVIAR_Ee_MONTURA_A_ESTABLO(_perso, '-', montura.getID().toString() + "")
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1104")
                        return
                    }
                    val obj1: Objeto = montura.getObjModCertificado().crearObjeto(1, Constantes.OBJETO_POS_NO_EQUIPADO,
                            CAPACIDAD_STATS.RANDOM)
                    obj1.fijarStatValor(Constantes.STAT_CONSULTAR_MONTURA, Math.abs(montura.getID()))
                    obj1.addStatTexto(Constantes.STAT_PERTENECE_A, "0#0#0#" + _perso.getNombre())
                    obj1.addStatTexto(Constantes.STAT_NOMBRE, "0#0#0#" + montura.getNombre())
                    _perso.addObjetoConOAKO(obj1, true)
                    montura.setMapaCelda(null, null)
                    montura.setPergamino(obj1.getID())
                    GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso)
                    GestorSalida.ENVIAR_Ee_MONTURA_A_ESTABLO(_perso, '-', montura.getID().toString() + "")
                    GestorSQL.REPLACE_MONTURA(montura, false)
                }
                'g' -> {
                    if (_perso.getMontura() != null) {
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1YOU_HAVE_MOUNT")
                        return
                    }
                    montura = Mundo.getMontura(id)
                    if (montura == null || _cuenta.getEstablo().remove(id) == null) {
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1104")
                        return
                    }
                    if (montura.getPergamino() > 0) {
                        GestorSalida.ENVIAR_Ee_MONTURA_A_ESTABLO(_perso, '-', montura.getID().toString() + "")
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1104")
                        return
                    }
                    montura.setMapaCelda(null, null)
                    _perso.setMontura(montura)
                    GestorSalida.ENVIAR_Ee_MONTURA_A_ESTABLO(_perso, '-', montura.getID().toString() + "")
                    GestorSalida.ENVIAR_Rx_EXP_DONADA_MONTURA(_perso)
                }
                'p' -> {
                    montura = _perso.getMontura()
                    if (montura == null || montura.getID() !== id) {
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1YOU_DONT_HAVE_MOUNT")
                        return
                    }
                    if (!montura.getObjetos().isEmpty()) {
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1106")
                        return
                    }
                    if (_perso.estaMontando()) {
                        _perso.subirBajarMontura(false)
                    }
                    _perso.setMontura(null)
                    if (!escapaDespuesParir(montura)) {
                        montura.setMapaCelda(null, null)
                        montura.setUbicacion(Ubicacion.ESTABLO)
                        _cuenta.addMonturaEstablo(montura)
                        GestorSalida.ENVIAR_Ee_MONTURA_A_ESTABLO(_perso, '+', montura.detallesMontura())
                    }
                    GestorSalida.ENVIAR_Rx_EXP_DONADA_MONTURA(_perso)
                }
                else -> {
                    GestorSalida.ENVIAR_BN_NADA(_perso)
                    return
                }
            }
        } catch (e: Exception) {
            MainServidor.redactarLogServidorln("EXCEPTION Packet " + packet + ", intercambio_Establo " + e.toString())
            e.printStackTrace()
        }
    }

    @Synchronized
    private fun escapaDespuesParir(madre: Montura?): Boolean {
        val padre: Montura = Mundo.getMontura(madre.getParejaID())
        if (madre.getFecundadaHaceMinutos() >= MainServidor.HORAS_PERDER_CRIAS_MONTURA * 60) {
            // las crias mueren por tiempo
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1112")
            madre.aumentarReproduccion()
        } else if (madre.getFecundadaHaceMinutos() >= madre.minutosParir()) {
            // nacen las crias
            var crias: Int = Formulas.getRandomInt(1, 2)
            if (madre.getCapacidades().contains(Constantes.HABILIDAD_REPRODUCTORA)) {
                crias *= 2
            }
            if (madre.getReprod() + crias > 20) {
                crias = 20 - madre.getReprod()
            }
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, (if (crias == 1) 1110 else 1111).toString() + ";" + crias)
            for (i in 1..crias) {
                val bebeMontura = Montura(madre, padre)
                GestorSalida.ENVIAR_Ee_MONTURA_A_ESTABLO(_perso, '~', bebeMontura.detallesMontura())
                madre.aumentarReproduccion()
                bebeMontura.setMapaCelda(null, null)
                _cuenta.addMonturaEstablo(bebeMontura)
            }
        } else {
            return false
        }
        if (padre != null) {
            GestorSQL.REPLACE_MONTURA(padre, false)
        }
        madre.restarAmor(7500)
        madre.restarResistencia(7500)
        madre.setFecundada(false)
        return madre.pudoEscapar()
    }

    @Synchronized
    private fun intercambio_Repetir_Ult_Craft() {
        val trabajo: Trabajo = _perso.getIntercambiandoCon(Trabajo::class.java) as Trabajo ?: return
        trabajo.ponerIngredUltRecet()
    }

    @Synchronized
    private fun intercambio_Vender(packet: String?) {
        try {
            when (_perso.getTipoExchange()) {
                Constantes.INTERCAMBIO_TIPO_TIENDA_NPC, Constantes.INTERCAMBIO_TIPO_BOUTIQUE -> {
                    // case 20 ://boutique
                    _perso.venderObjetos(packet.substring(2))
                    return
                }
            }
        } catch (e: Exception) {
        }
        GestorSalida.ENVIAR_ESE_ERROR_VENTA(_perso)
    }

    @Synchronized
    private fun intercambio_Comprar(packet: String?) {
        try {
            val infos: Array<String?> = packet.substring(2).split(Pattern.quote("|"))
            when (_perso.getTipoExchange()) {
                Constantes.INTERCAMBIO_TIPO_TIENDA_NPC, Constantes.INTERCAMBIO_TIPO_BOUTIQUE -> try {
                    var objModeloID = 0
                    var cantidad: Long = 0
                    try {
                        objModeloID = Integer.parseInt(infos[0])
                        cantidad = Long.parseLong(infos[1])
                    } catch (e: Exception) {
                    }
                    if (cantidad <= 0 || objModeloID <= 0) {
                        GestorSalida.ENVIAR_BN_NADA(_perso)
                        return
                    }
                    val objModelo: ObjetoModelo = Mundo.getObjetoModelo(objModeloID)
                    if (objModelo == null) {
                        GestorSalida.ENVIAR_EBE_ERROR_DE_COMPRA(_perso)
                        return
                    }
                    var capStats: CAPACIDAD_STATS = CAPACIDAD_STATS.RANDOM
                    var npc: NPC? = null
                    if (_perso.getTipoExchange() === Constantes.INTERCAMBIO_TIPO_TIENDA_NPC) {
                        npc = _perso.getIntercambiandoCon(NPC::class.java) as NPC
                        if (npc == null) {
                            GestorSalida.ENVIAR_EBE_ERROR_DE_COMPRA(_perso)
                            return
                        }
                        val npcMod: NPCModelo = npc.getModelo()
                        if (npcMod == null || !npcMod.tieneObjeto(objModeloID)) {
                            GestorSalida.ENVIAR_EBE_ERROR_DE_COMPRA(_perso)
                            return
                        }
                        if (MainServidor.IDS_NPCS_VENDE_OBJETOS_STATS_MAXIMOS.contains(npcMod.getID())) {
                            capStats = CAPACIDAD_STATS.MAXIMO
                        }
                        if (MainServidor.EVENTO_NPC_2 === npc.getModeloID()) {
                            Mundo.eventoComprando = true
                            if (cantidad > 1) {
                                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                        "Con este NPC solo puedes comprar item con cantidad 1")
                                cantidad = 1
                            }
                        }
                    } else {
                        try {
                            if (!Mundo.getNPCModelo(MainServidor.ID_NPC_BOUTIQUE).tieneObjeto(objModeloID)) {
                                GestorSalida.ENVIAR_EBE_ERROR_DE_COMPRA(_perso)
                                return
                            }
                        } catch (e: Exception) {
                            GestorSalida.ENVIAR_EBE_ERROR_DE_COMPRA(_perso)
                            return
                        }
                    }
                    var ogrinasCompra = false
                    if (npc != null) {
                        if (MainServidor.OBJETOS_OGRINAS_NPC.containsKey(objModeloID)) {
                            if (MainServidor.OBJETOS_OGRINAS_NPC.get(objModeloID) === npc.getModeloID()) {
                                if (objModelo.getOgrinas() > 0) ogrinasCompra = true
                            }
                        }
                    }
                    if (objModelo.getItemPago() != null && !ogrinasCompra) {
                        val idItemPago: Int = objModelo.getItemPago()._primero
                        val cantItemPago: Int = objModelo.getItemPago()._segundo
                        if (!Formulas.valorValido1(cantidad, cantItemPago)) {
                            GestorSalida.ENVIAR_BN_NADA(_perso, "INTENTO BUG MULTIPLICADOR")
                            return
                        }
                        if (!_perso.tieneObjPorModYCant(idItemPago, cantItemPago * cantidad)) {
                            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "14")
                            GestorSalida.ENVIAR_EBE_ERROR_DE_COMPRA(_perso)
                            return
                        }
                        _perso.restarObjPorModYCant(idItemPago, cantItemPago * cantidad)
                    } else if (objModelo.getOgrinas() > 0) {
                        if (!Formulas.valorValido1(cantidad, objModelo.getOgrinas())) {
                            GestorSalida.ENVIAR_BN_NADA(_perso, "INTENTO BUG MULTIPLICADOR")
                            return
                        }
                        val ogrinas: Long = objModelo.getOgrinas() * cantidad
                        if (ogrinas < 0) {
                            GestorSalida.ENVIAR_EBE_ERROR_DE_COMPRA(_perso)
                            return
                        }
                        if (objModelo.getKamas() > 0) {
                            objModelo.setKamas(0)
                        }
                        if (!GestorSQL.RESTAR_OGRINAS1(_cuenta, ogrinas, _perso)) {
                            GestorSalida.ENVIAR_EBE_ERROR_DE_COMPRA(_perso)
                            return
                        }
                    } else {
                        if (!Formulas.valorValido1(cantidad, objModelo.getKamas())) {
                            GestorSalida.ENVIAR_BN_NADA(_perso, "INTENTO BUG MULTIPLICADOR")
                            return
                        }
                        val kamas: Long = objModelo.getKamas() * cantidad
                        if (kamas < 0) {
                            GestorSalida.ENVIAR_EBE_ERROR_DE_COMPRA(_perso)
                            return
                        }
                        if (_perso.getKamas() < kamas) {
                            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "182")
                            GestorSalida.ENVIAR_EBE_ERROR_DE_COMPRA(_perso)
                            return
                        }
                        _perso.addKamas(-kamas, true, true)
                    }
                    if (MainServidor.PARAM_OBJETOS_PEFECTOS_COMPRADOS_NPC
                            || objModelo.getOgrinas() > 0 and !MainServidor.COMPRAR_OBJETOS_VIP_NO_PERFECTOS) {
                        capStats = CAPACIDAD_STATS.MAXIMO
                    }
                    val objeto: Objeto = objModelo.crearObjeto(cantidad, Constantes.OBJETO_POS_NO_EQUIPADO, capStats)
                    if (objModelo.getOgrinas() > 0) {
                        if (MainServidor.PARAM_NOMBRE_COMPRADOR) {
                            objeto.addStatTexto(Constantes.STAT_PERTENECE_Y_NO_VENDER, "0#0#0#" + _perso.getNombre())
                        }
                        if (MainServidor.PARAM_OBJETOS_OGRINAS_LIGADO) {
                            objeto.addStatTexto(Constantes.STAT_LIGADO_A_CUENTA, "0#0#0#" + _perso.getNombre())
                        }
                    }
                    _perso.addObjIdentAInventario(objeto, false)
                    GestorSalida.ENVIAR_EBK_COMPRADO(_perso)
                    GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso)
                    if (Mundo.eventoComprando) {
                        Mundo.eventoComprando = false
                        _perso.cerrarExchange("")
                    }
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_EBE_ERROR_DE_COMPRA(_perso)
                }
                Constantes.INTERCAMBIO_TIPO_MERCANTE -> {
                    val mercante: Personaje = _perso.getIntercambiandoCon(Personaje::class.java) as Personaje
                    if (!mercante.esMercante()) {
                        GestorSalida.ENVIAR_EBE_ERROR_DE_COMPRA(_perso)
                        return
                    }
                    try {
                        val cantidad: Int = Integer.parseInt(infos[1])
                        val objetoID: Int = Integer.parseInt(infos[0])
                        val objeto: Objeto = Mundo.getObjeto(objetoID)
                        if (mercante.comprarTienda(_perso, cantidad, objeto)) {
                            GestorSalida.ENVIAR_EBK_COMPRADO(_perso)
                            GestorSalida.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(_perso)
                            GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso)
                            if (mercante.getTienda().estaVacia()) {
                                _perso.getMapa().removerMercante(mercante.getID())
                                mercante.setMercante(false)
                                _perso.cerrarVentanaExchange("b")
                                GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(_perso.getMapa(), mercante.getID())
                            } else {
                                GestorSalida.ENVIAR_EL_LISTA_EXCHANGER(_perso, mercante)
                            }
                        } else {
                            GestorSalida.ENVIAR_EBE_ERROR_DE_COMPRA(_perso)
                        }
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_EBE_ERROR_DE_COMPRA(_perso)
                        GestorSalida.ENVIAR_EL_LISTA_EXCHANGER(_perso, mercante)
                    }
                }
            }
        } catch (e: Exception) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
        }
    }

    private fun analizar_Ambiente(packet: String?) {
        when (packet.charAt(1)) {
            'D' -> ambiente_Cambio_Direccion(packet)
            'U' -> ambiente_Emote(packet)
            else -> {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return
            }
        }
    }

    private fun ambiente_Emote(packet: String?) {
        var emote: Byte = -1
        try {
            emote = Byte.parseByte(packet.substring(2))
        } catch (e: Exception) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
            return
        }
        if (_perso.getPelea() != null) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
            return
        }
        if (emote < 0 || !_perso.tieneEmote(emote)) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1CANT_USE_EMOTE")
            return
        }
        when (emote) {
            Constantes.EMOTE_ACOSTARSE, Constantes.EMOTE_SENTARSE -> {
                if (_perso.estaSentado()) {
                    emote = 0
                }
                _perso.setSentado(!_perso.estaSentado())
            }
        }
        _perso.setEmoteActivado(emote)
        var tiempo = 0
        if (emote == Constantes.EMOTE_FLAUTA) { // flauta
            tiempo = 9000
        } else if (emote == Constantes.EMOTE_CAMPEON) { // campeon
            tiempo = 5000
        }
        GestorSalida.ENVIAR_eUK_EMOTE_MAPA(_perso.getMapa(), _perso.getID(), emote, tiempo)
        val cercado: Cercado = _perso.getMapa().getCercado()
        if (cercado != null) {
            when (emote) {
                Constantes.EMOTE_SEÑAL_CON_MANO, Constantes.EMOTE_ENFADARSE, Constantes.EMOTE_APLAUDIR, Constantes.EMOTE_PEDO, Constantes.EMOTE_MOSTRAR_ARMA, Constantes.EMOTE_BESO -> {
                    var monturas: ArrayList<Montura?>? = ArrayList<Montura?>()
                    for (montura in cercado.getCriando().values()) {
                        if (montura.getDueñoID() === _perso.getID()) {
                            monturas.add(montura)
                        }
                    }
                    if (!monturas.isEmpty()) {
                        var casillas = 0
                        when (emote) {
                            Constantes.EMOTE_SEÑAL_CON_MANO, Constantes.EMOTE_ENFADARSE -> casillas = 1
                            Constantes.EMOTE_APLAUDIR, Constantes.EMOTE_PEDO -> casillas = Formulas.getRandomInt(2, 3)
                            Constantes.EMOTE_MOSTRAR_ARMA, Constantes.EMOTE_BESO -> casillas = Formulas.getRandomInt(4, 7)
                        }
                        val alejar: Boolean
                        alejar = if (emote == Constantes.EMOTE_SEÑAL_CON_MANO || emote == Constantes.EMOTE_APLAUDIR || emote == Constantes.EMOTE_BESO) {
                            false
                        } else {
                            true
                        }
                        monturas.get(Formulas.getRandomInt(0, monturas.size() - 1)).moverMontura(_perso, -1, casillas,
                                alejar)
                    }
                    monturas = null
                }
            }
        }
    }

    private fun ambiente_Cambio_Direccion(packet: String?) {
        try {
            if (_perso.getPelea() != null) {
                return
            }
            val dir: Byte = Byte.parseByte(packet.substring(2))
            _perso.setOrientacion(dir)
            GestorSalida.ENVIAR_eD_CAMBIAR_ORIENTACION(_perso.getMapa(), _perso.getID(), dir)
        } catch (e: Exception) {
        }
    }

    private fun analizar_Hechizos(packet: String?) {
        when (packet.charAt(1)) {
            'B' -> hechizos_Boost(packet)
            'F' -> hechizos_Olvidar(packet)
            'M' -> hechizos_Acceso_Rapido(packet)
            else -> {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return
            }
        }
    }

    private fun hechizos_Acceso_Rapido(packet: String?) {
        try {
            val split: Array<String?> = packet.substring(2).split(Pattern.quote("|"))
            val hechizoID: Int = Integer.parseInt(split[0])
            val posicion: Int = Integer.parseInt(split[1])
            if (_perso.getPelea() != null && !_perso.tieneHechizoID(hechizoID) && _perso.getCompañeros().size() > 0) {
                for (perso in _perso.getCompañeros()) {
                    if (perso.tieneHechizoID(hechizoID)) {
                        perso.setPosHechizo(hechizoID, Encriptador.getValorHashPorNumero(posicion))
                        return
                    }
                }
            }
            _perso.setPosHechizo(hechizoID, Encriptador.getValorHashPorNumero(posicion))
        } catch (e: Exception) {
        }
    }

    private fun hechizos_Boost(packet: String?) {
        try {
            // if (_perso.getPelea() != null) {
            // GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1CANT_BOOST_IN_GAME");
            // return;
            // }
            if (!_perso.boostearHechizo(Integer.parseInt(packet.substring(2)))) {
                GestorSalida.ENVIAR_SUE_NIVEL_HECHIZO_ERROR(_perso)
            }
        } catch (e: Exception) {
            GestorSalida.ENVIAR_SUE_NIVEL_HECHIZO_ERROR(_perso)
        }
    }

    private fun hechizos_Olvidar(packet: String?) {
        try {
            if (!_perso.estaOlvidandoHechizo()) {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return
            }
            if (_perso.olvidarHechizo(Integer.parseInt(packet.substring(2)), false, true)) {
                _perso.setOlvidandoHechizo(false)
            }
        } catch (e: Exception) {
        }
    }

    private fun analizar_Peleas(packet: String?) {
        val pelea: Pelea = _perso.getPelea()
        when (packet.charAt(1)) {
            'D' -> {
                if (pelea != null) {
                    GestorSalida.ENVIAR_BN_NADA(_perso)
                    return
                }
                pelea_Detalles(packet)
            }
            'H' -> {
                if (pelea == null) {
                    GestorSalida.ENVIAR_BN_NADA(_perso)
                    return
                }
                pelea.botonAyuda(_perso.getID())
            }
            'L' -> {
                if (pelea != null) {
                    GestorSalida.ENVIAR_BN_NADA(_perso)
                    return
                }
                GestorSalida.ENVIAR_fL_LISTA_PELEAS(_perso, _perso.getMapa())
            }
            'N' -> {
                if (pelea == null) {
                    GestorSalida.ENVIAR_BN_NADA(_perso)
                    return
                }
                if (_perso.getPelea().getMobGrupo() != null) {
                    if (_perso.getPelea().getMobGrupo().is_evento()) {
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "No puedes bloquear combates de invasion.")
                        return
                    }
                }
                pelea.botonBloquearMasJug(_perso.getID())
            }
            'P' -> {
                if (pelea == null || _perso.getGrupoParty() == null) {
                    GestorSalida.ENVIAR_BN_NADA(_perso)
                    return
                }
                if (_perso.getPelea().getMobGrupo() != null) {
                    if (_perso.getPelea().getMobGrupo().is_evento()) {
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                "La invasion es libre, tu funcion de solo grupo no sirve aqui!.")
                        return
                    }
                }
                pelea.botonSoloGrupo(_perso.getID())
            }
            'S' -> {
                if (pelea == null) {
                    GestorSalida.ENVIAR_BN_NADA(_perso)
                    return
                }
                pelea.botonBloquearEspect(_perso.getID())
            }
            else -> {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return
            }
        }
    }

    private fun pelea_Detalles(packet: String?) {
        var id: Short = -1
        try {
            id = Short.parseShort(packet.substring(2).replace("0", ""))
        } catch (e: Exception) {
        }
        if (id.toInt() == -1) {
            GestorSalida.ENVIAR_BN_NADA(_perso)
            return
        }
        GestorSalida.ENVIAR_fD_DETALLES_PELEA(_perso, _perso.getMapa().getPeleas().get(id))
    }

    private var solounavez = false
    private fun analizar_Basicos(packet: String?) {
        try {
            when (packet.charAt(1)) {
                'a' -> basicos_Comandos_Rapidos(packet)
                'A' -> {
                    if (!solounavez) {
                        paqueteR += 1
                        establecido = true
                        solounavez = true
                    }
                    basicos_Comandos_Consola(packet)
                }
                'D' -> {
                    if (!solounavez) {
                        paqueteR += 1
                        establecido = true
                        solounavez = true
                    }
                    basicos_Enviar_Fecha()
                }
                'K' -> GestorSalida.ENVIAR_BN_NADA(_perso)
                'M' -> basicos_Chat(packet)
                'R' -> {
                }
                'S' -> _perso.mostrarEmoteIcon(packet.substring(2))
                'Q' -> {
                    val celdaMercante: Short = Short.parseShort(packet.substring(2))
                    _perso.getMapa().expulsarMercanterPorCelda(celdaMercante)
                }
                'W' -> basicos_Mensaje_Informacion(packet)
                'Y' -> basicos_Estado(packet)
                else -> {
                    GestorSalida.ENVIAR_BN_NADA(_perso)
                    return
                }
            }
        } catch (e: Exception) {
            MainServidor.redactarLogServidorln("EXCEPTION Packet " + packet + ", analizar_Basicos " + e.toString())
            e.printStackTrace()
        }
    }

    private fun basicos_Comandos_Rapidos(packet: String?) {
        when (packet.charAt(2)) {
            'M' -> {
                if (_perso.getAdmin() === 0) {
                    GestorSalida.ENVIAR_BN_NADA(_perso, "NO TIENE RANGO")
                    return
                }
                if (!_perso.estaDisponible(false, true)) {
                    GestorSalida.ENVIAR_BN_NADA(_perso, "NO ESTA DISPONIBLE")
                    return
                }
                try {
                    val infos: Array<String?> = packet.substring(3).split(",")
                    val coordX: Int = Integer.parseInt(infos[0])
                    val coordY: Int = Integer.parseInt(infos[1])
                    val mapa: Mapa = Mundo.mapaPorCoordXYContinente(coordX, coordY,
                            _perso.getMapa().getSubArea().getArea().getSuperArea().getID())
                    _perso.teleport(mapa.getID(), mapa.getRandomCeldaIDLibre())
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1MAPA_NO_EXISTE")
                }
            }
            'K' -> GestorSalida.ENVIAR_BN_NADA(_perso, MainServidor.PALABRA_CLAVE_CONSOLA)
            else -> {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return
            }
        }
    }

    private fun basicos_Comandos_Consola(packet: String?) {
        var mensaje: String? = packet.substring(2)
        if (!MainServidor.PALABRA_CLAVE_CONSOLA.isEmpty()) {
            if (!mensaje.contains(MainServidor.PALABRA_CLAVE_CONSOLA)) {
                return
            }
            mensaje = mensaje.replaceFirst(MainServidor.PALABRA_CLAVE_CONSOLA, "")
        }
        Comandos.consolaComando(mensaje, _cuenta, _perso)
    }

    private fun basicos_Estado(packet: String?) {
        when (packet.charAt(2)) {
            'A' -> if (_perso.estaAusente()) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "038")
                _perso.setAusente(false)
            } else {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "037")
                _perso.setAusente(true)
            }
            'I' -> if (_perso.esInvisible()) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "051")
                _perso.setInvisible(false)
            } else {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "050")
                _perso.setInvisible(true)
            }
            'O' -> when (packet.charAt(3)) {
                '+' -> _perso.addOmitido(packet.substring(4))
                '-' -> _perso.borrarOmitido(packet.substring(4))
                else -> _perso.addOmitido(packet.substring(4))
            }
            else -> {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return
            }
        }
    }

    private fun basicos_Enviar_Fecha() {
        // deshabilitados por ser innecesario solo se mandara 1 vez al entrar al juego
        // GestorSalida.ENVIAR_BD_FECHA_SERVER(_perso);
        // GestorSalida.ENVIAR_BT_TIEMPO_SERVER(_perso);
    }

    private fun basicos_Mensaje_Informacion(packet: String?) {
        try {
            val perso: Personaje = Mundo.getPersonajePorNombre(packet.substring(2))
            if (perso == null) {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return
            }
            if (!perso.enLinea()) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1211")
                return
            }
            GestorSalida.ENVIAR_BWK_QUIEN_ES(_perso, perso.getCuenta().getApodo().toString() + "|"
                    + (if (perso.getPelea() != null) 2 else 1) + "|" + perso.getNombre() + "|" + perso.getMapa().getID())
        } catch (e: Exception) {
        }
    }

    private fun basicos_Chat(packet: String?) {
        try {
            var msjChat: String? = ""
            if (_perso.estaMuteado()) {
                val tiempoTrans: Long = System.currentTimeMillis() - _cuenta.getHoraMuteado()
                if (tiempoTrans > _cuenta.getTiempoMuteado()) {
                    _cuenta.mutear(false, 0)
                    _perso.modificarA(Personaje.RA_PUEDE_USAR_OBJETOS,
                            Personaje.RA_PUEDE_USAR_OBJETOS xor Personaje.RA_PUEDE_USAR_OBJETOS)
                    GestorSalida.ENVIAR_AR_RESTRICCIONES_PERSONAJE(_perso)
                    _perso.setCalabozo(false)
                } else {
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso,
                            "1124;" + (_cuenta.getTiempoMuteado() - tiempoTrans) / 1000)
                    return
                }
            }
            if (_perso.getCalabozo()) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1124;" + System.currentTimeMillis() / 1000)
                return
            }
            var packet2: String? = packet.replace("<", "").replace(">", "")
            if (packet2!!.length() <= 3) {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return
            }
            if (packet2.length() > 1500) {
                packet2 = packet2.substring(0, 1499)
            }
            try {
                msjChat = packet2.split("\\|", 2).get(1)
                if (msjChat.charAt(msjChat.length() - 1) === '|') msjChat = msjChat.substring(0, msjChat.length() - 1)
            } catch (e: Exception) {
                msjChat = ""
            }
            if (msjChat.isEmpty()) {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return
            }
            if (!MainServidor.PALABRAS_PROHIBIDAS.isEmpty()) {
                val filtro: Array<String?> = msjChat.replace(".", " ").split(" ")
                var veces = 0
                for (s in filtro) {
                    if (MainServidor.PALABRAS_PROHIBIDAS.contains(s.toLowerCase())) {
                        veces++
                    }
                }
                if (veces == 0) {
                    val filtro2: String = msjChat.replace(" ", "")
                    for (s in MainServidor.PALABRAS_PROHIBIDAS) {
                        if (s!!.length() < 5) {
                            continue
                        }
                        if (filtro2.toLowerCase().contains(s)) {
                            veces++
                        }
                    }
                }
                if (veces > 0) {
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1DONT_USE_BLOCK_WORDS;$veces")
                    _cuenta.mutear(true, veces * 60)
                    return
                }
            }
            val sufijo: String = packet2.charAt(2).toString() + ""
            when (sufijo) {
                "$" -> {
                    if (!_perso.tieneCanal(sufijo) || _perso.getGrupoParty() == null) {
                        GestorSalida.ENVIAR_BN_NADA(_perso)
                        return
                    }
                    GestorSalida.ENVIAR_cMK_MENSAJE_CHAT_GRUPO(_perso, msjChat)
                }
                "¿" -> {
                    if (_perso.getGrupoKoliseo() == null) {
                        GestorSalida.ENVIAR_BN_NADA(_perso)
                        return
                    }
                    GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_KOLISEO(_perso, msjChat)
                }
                "~" -> GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_TODOS(sufijo, _perso, msjChat)
                "¬" -> GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_TODOS(sufijo, _perso, msjChat)
                "%" -> {
                    if (!_perso.tieneCanal(sufijo) || _perso.getGremio() == null) {
                        GestorSalida.ENVIAR_BN_NADA(_perso)
                        return
                    }
                    GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_GREMIO(_perso, msjChat)
                }
                "#" -> {
                    if (!_perso.tieneCanal(sufijo) || _perso.getPelea() == null) {
                        GestorSalida.ENVIAR_BN_NADA(_perso)
                        return
                    }
                    val equipo: Int = _perso.getPelea().getParamMiEquipo(_perso.getID())
                    if (equipo == 4) {
                        GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_PELEA(_perso.getPelea(), 4, sufijo, _perso.getID(),
                                _perso.getNombre(), msjChat)
                    } else {
                        GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_PELEA(_perso.getPelea(), equipo, sufijo, _perso.getID(),
                                _perso.getNombre(), msjChat)
                    }
                }
                "*" -> {
                    if (!_perso.tieneCanal(sufijo)) {
                        GestorSalida.ENVIAR_BN_NADA(_perso)
                        return
                    }
                    if (comando_jugador(msjChat)) {
                        return
                    }
                    // mensaje mapa
                    if (_perso.getPelea() == null) {
                        if (!_perso.getMapa().getMuteado() || _perso.getAdmin() > 0) {
                            GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_MAPA(_perso, sufijo, msjChat)
                        } else {
                            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1MAPA_MUTEADO")
                        }
                    } else {
                        val equipo2: Int = _perso.getPelea().getParamMiEquipo(_perso.getID())
                        if (equipo2 == 1 || equipo2 == 2) {
                            GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_PELEA(_perso.getPelea(), 7, "", _perso.getID(),
                                    _perso.getNombre(), msjChat)
                        } else {
                            GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_PELEA(_perso.getPelea(), 7, "p", _perso.getID(),
                                    _perso.getNombre(), msjChat)
                        }
                    }
                }
                "¡" -> {
                    if (!_cuenta.esAbonado()) {
                        GestorSalida.ENVIAR_BN_NADA(_perso, "NO ABONADO")
                        return
                    }
                    var h: Long
                    if (((System.currentTimeMillis() - _tiempoUltVIP) / 1000).also { h = it } < MainServidor.SEGUNDOS_CANAL_VIP) {
                        h = MainServidor.SEGUNDOS_CANAL_VIP - h
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "0115;" + (Math.ceil(h) as Int + 1))
                        return
                    }
                    _tiempoUltVIP = System.currentTimeMillis()
                    GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_TODOS(sufijo, _perso, msjChat)
                }
                "!" -> {
                    if (!_perso.tieneCanal(sufijo) || _perso.getAlineacion() === Constantes.ALINEACION_NEUTRAL) {
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "0106")
                        return
                    }
                    if (_perso.getDeshonor() >= 1) {
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "183")
                        return
                    }
                    if (_perso.getGradoAlineacion() < 3) {
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "0106")
                        return
                    }
                    if (MainServidor.MUTE_CANAL_ALINEACION) {
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "0115;777777")
                        return
                    }
                    var k: Long
                    if (((System.currentTimeMillis() - _tiempoUltAlineacion)
                                    / 1000).also { k = it } < MainServidor.SEGUNDOS_CANAL_ALINEACION) {
                        k = MainServidor.SEGUNDOS_CANAL_ALINEACION - k
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "0115;" + (Math.ceil(k) as Int + 1))
                        return
                    }
                    _tiempoUltAlineacion = System.currentTimeMillis()
                    GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_TODOS(sufijo, _perso, msjChat)
                }
                "^" -> {
                    if (_perso.getMapa().getSubArea().getArea().getSuperArea().getID() !== 3) {
                        return
                    }
                    if (!_perso.tieneCanal(sufijo)) {
                        GestorSalida.ENVIAR_BN_NADA(_perso)
                        return
                    }
                    if (MainServidor.MUTE_CANAL_INCARNAM) {
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "0115;777777")
                        return
                    }
                    var i: Long
                    if (((System.currentTimeMillis() - _tiempoUltIncarnam)
                                    / 1000).also { i = it } < MainServidor.SEGUNDOS_CANAL_INCARNAM) {
                        i = MainServidor.SEGUNDOS_CANAL_INCARNAM - i
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "0115;" + (Math.ceil(i) as Int + 1))
                        return
                    }
                    _tiempoUltIncarnam = System.currentTimeMillis()
                    GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_TODOS(sufijo, _perso, msjChat)
                }
                ":" -> {
                    if (!_perso.tieneCanal(sufijo)) {
                        GestorSalida.ENVIAR_BN_NADA(_perso)
                        return
                    }
                    if (MainServidor.MUTE_CANAL_COMERCIO) {
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "0115;777777")
                        return
                    }
                    if (MainServidor.SON_DE_LUCIANO) {
                        if (_perso.getNivel() < 6) {
                            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "0157;6")
                            return
                        }
                    }
                    var l: Long
                    if (((System.currentTimeMillis() - _tiempoUltComercio)
                                    / 1000).also { l = it } < MainServidor.SEGUNDOS_CANAL_COMERCIO) {
                        l = MainServidor.SEGUNDOS_CANAL_COMERCIO - l
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "0115;" + (Math.ceil(l) as Int + 1))
                        return
                    }
                    _tiempoUltComercio = System.currentTimeMillis()
                    GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_TODOS(sufijo, _perso, msjChat)
                }
                "?" -> {
                    if (!_perso.tieneCanal(sufijo)) {
                        GestorSalida.ENVIAR_BN_NADA(_perso)
                        return
                    }
                    if (MainServidor.MUTE_CANAL_RECLUTAMIENTO) {
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "0115;777777")
                        return
                    }
                    if (MainServidor.SON_DE_LUCIANO) {
                        if (_perso.getNivel() < 6) {
                            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "0157;6")
                            return
                        }
                    }
                    var j: Long
                    if (((System.currentTimeMillis() - _tiempoUltReclutamiento)
                                    / 1000).also { j = it } < MainServidor.SEGUNDOS_CANAL_RECLUTAMIENTO) {
                        j = MainServidor.SEGUNDOS_CANAL_RECLUTAMIENTO - j
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "0115;" + (Math.ceil(j) as Int + 1))
                        return
                    }
                    _tiempoUltReclutamiento = System.currentTimeMillis()
                    GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_TODOS(sufijo, _perso, msjChat)
                }
                "@" -> {
                    if (_perso.getAdmin() === 0) {
                        GestorSalida.ENVIAR_BN_NADA(_perso)
                        return
                    }
                    GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_TODOS(sufijo, _perso, msjChat)
                }
                else -> {
                    val nombre: String = packet2.substring(2).split(Pattern.quote("|")).get(0)
                    if (nombre.length() <= 1) {
                        break
                    }
                    val perso: Personaje = Mundo.getPersonajePorNombre(nombre)
                    if (perso == null || !perso.enLinea() || perso.esIndetectable()) {
                        GestorSalida.ENVIAR_cMEf_CHAT_ERROR(_perso, nombre)
                        return
                    }
                    if (!perso.estaVisiblePara(_perso)) {
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "114;" + perso.getNombre())
                        return
                    }
                    GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_PERSONAJE(perso, "F", _perso.getID(), _perso.getNombre(), msjChat)
                    GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_PERSONAJE(_perso, "T", perso.getID(), perso.getNombre(), msjChat)
                    if (_perso.estaAusente()) {
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "072")
                    }
                }
            }
        } catch (e: Exception) {
            MainServidor.redactarLogServidorln("EXCEPTION Packet " + packet + ", basicos_Chat " + e.toString())
            e.printStackTrace()
        }
    }

    private fun comando_jugador(msjChat: String?): Boolean {
        if (!MainServidor.PARAM_COMANDOS_JUGADOR) {
            return false
        }
        if (msjChat.charAt(0) === '.') {
            var split: Array<String?>? = msjChat.split(" ")
            val cmd = split!![0]
            val comando: String = cmd.substring(1).toLowerCase()
            if (!MainServidor.COMANDOS_PERMITIDOS.contains(comando)) {
                return false
            }
            try {
                var mapa_celda: String?
                val mapa: Mapa
                val celdaID: Short
                when (comando) {
                    "convert", "convertir" -> {
                        if (MainServidor.VALOR_KAMAS_POR_OGRINA <= 0) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "No se puede convertir ahora")
                        } else {
                            if (split.size < 2) {
                                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Pon la cantidad a convertir")
                            } else {
                                try {
                                    val cantidad: Int = Integer.parseInt(split[1])
                                    if (!Formulas.valorValido(cantidad, MainServidor.VALOR_KAMAS_POR_OGRINA)) {
                                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Valor invalido")
                                        return true
                                    }
                                    if (!GestorSQL.RESTAR_OGRINAS1(_cuenta, cantidad, _perso)) {
                                        return true
                                    }
                                    _perso.addKamas(cantidad * MainServidor.VALOR_KAMAS_POR_OGRINA, true, true)
                                } catch (e: Exception) {
                                    GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Valor invalido")
                                }
                            }
                        }
                        return true
                    }
                    "resethechizos" -> {
                        _perso.resetearTodosHechizos()
                        _perso.fijarHechizosInicio()
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Hechizos reiniciados")
                    }
                    "hdv" -> try {
                        if (!_perso.puedeAbrir) {
                            GestorSalida.ENVIAR_BN_NADA(this)
                            return true
                        }
                        val tipo: Byte = 11
                        if (!_perso.estaDisponible(true, true)) {
                            GestorSalida.ENVIAR_BN_NADA(_perso, "INTERCAMBIO NO ESTA DISPONIBLE")
                            return true
                        }
                        if (_perso.getConsultarCofre() != null) {
                            GestorSalida.ENVIAR_BN_NADA(_perso, "INTERCAMBIO CONSULTAR COFRE")
                            return true
                        }
                        if (_perso.getConsultarCasa() != null) {
                            GestorSalida.ENVIAR_BN_NADA(_perso, "INTERCAMBIO CONSULTAR CASA")
                            return true
                        }
                        if (realizandoAccion) {
                            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1YOU_ALREADY_EXCHANGE")
                            return true
                        }
                        when (tipo) {
                            Constantes.INTERCAMBIO_TIPO_MERCADILLO_COMPRAR, Constantes.INTERCAMBIO_TIPO_MERCADILLO_VENDER -> {
                                if (_perso.getDeshonor() >= 5) {
                                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "183")
                                    GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_perso, "")
                                    return true
                                }
                                if (_perso.getExchanger() != null) {
                                    GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_perso, "")
                                }
                                val mercadillo: Mercadillo = Mundo.getPuestoMercadillo(58)
                                if (mercadillo == null) {
                                    GestorSalida.ENVIAR_BN_NADA(_perso)
                                    return true
                                }
                                _perso.setExchanger(mercadillo)
                                GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(_perso, tipo,
                                        "1,10,100;" + mercadillo.getTipoObjPermitidos().toString() + ";" + mercadillo.getPorcentajeImpuesto().toString() + ";"
                                                + mercadillo.getNivelMax().toString() + ";" + mercadillo.getMaxObjCuenta().toString() + ";-1;"
                                                + mercadillo.getTiempoVenta())
                                if (tipo == Constantes.INTERCAMBIO_TIPO_MERCADILLO_VENDER) { // mercadillo vender
                                    GestorSalida.ENVIAR_EL_LISTA_EXCHANGER(_perso, mercadillo)
                                }
                                _perso.setTipoExchange(tipo)
                            }
                        }
                    } catch (e: Exception) {
                        _perso.sendMessage("Erreur dans les paramètres.")
                        return true
                    }
                    "liderip", "chefip" -> {
                        return try {
                            split = msjChat.split(" ", 2)
                            var b = false
                            if (split.size > 1) {
                                val onOff = split[1]
                                when (onOff.toLowerCase()) {
                                    "on", "true", "1" -> b = true
                                }
                            } else {
                                _perso.sendMessage("Vous devez activer ou désactiver .chefip pour activer ou désactiver (.chefip on ou .chefip off)")
                                _perso.sendMessage("AVERTISSEMENT: LA COMMANDE EST EN BETA ET ELLE PEUT GÉNÉRER DES BUGS, VEUILLEZ LES SIGNALER. \n Nous vous remercions d'avance d'avoir signalé les bugs et d'avoir généré une meilleure commande et un meilleur service pour vous tous.")
                                return true
                            }
                            if (b) {
                                _perso.sendMessage("Vous êtes maintenant Chef IP")
                                _perso.Multis.clear()
                                for (p in Mundo.ONLINES) {
                                    if (p.getCuenta().getActualIP().equals(_perso.getCuenta().getActualIP())) {
                                        p.EsliderIP = false
                                        p.LiderIP = _perso
                                        _perso.Multis.add(p)
                                    }
                                }
                                _perso.EsliderIP = true
                                true
                            } else {
                                _perso.sendMessage("Chef IP désactivé")
                                _perso.Multis.clear()
                                for (p in Mundo.ONLINES) {
                                    if (p.getCuenta().getActualIP().equals(_perso.getCuenta().getActualIP())) {
                                        p.EsliderIP = false
                                        p.LiderIP = null
                                        p.Multi = null
                                    }
                                }
                                true
                            }
                        } catch (e: Exception) {
                            _perso.sendMessage("Erreur dans la commande.")
                            true
                        }
                        if (split.size < 2) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, MainServidor.MENSAJE_SERVICIOS)
                        } else {
                            try {
                                val servicio: String = split[1].toLowerCase()
                                when (servicio) {
                                    "guilde", "guild", "gremio" -> {
                                        if (!_perso.estaDisponible(true, true)) {
                                            return false
                                        }
                                        if (_perso.getGremio() != null || _perso.getMiembroGremio() != null) {
                                            return false
                                        }
                                        if (puede_Usar_Servicio(servicio)) {
                                            Accion.realizar_Accion_Estatico(-2, "", _perso, null, -1, -1.toShort())
                                        }
                                    }
                                    "scroll", "fullstats", "parcho" -> {
                                        if (!_perso.estaDisponible(false, false)) {
                                            return false
                                        }
                                        if (MainServidor.PARAMS_SCROLL_NO_RESET) {
                                            if (_perso.getResets() > 0) {
                                                return true
                                            }
                                        }
                                        if (puede_Usar_Servicio(servicio)) {
                                            val stats = intArrayOf(124, 118, 123, 119, 125, 126)
                                            for (s in stats) {
                                                if (_perso.getStatScroll(s) > 0) {
                                                    GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                                            "Veuillez remettre à zéro vos caractéristiques via la Fée Risette avant de vous parchotter.")
                                                    return true
                                                }
                                            }
                                            for (s in stats) {
                                                Accion.realizar_Accion_Estatico(8, "$s,101", _perso, null, -1, -1.toShort())
                                            }
                                        }
                                        return true
                                    }
                                    "restater", "restarter", "reset" -> {
                                        if (_perso.getNivel() < 30) {
                                            return false
                                        }
                                        if (puede_Usar_Servicio(servicio)) {
                                            _perso.resetearStats(false)
                                        }
                                    }
                                    "sortspecial" -> if (puede_Usar_Servicio(servicio)) {
                                        _perso.fijarNivelHechizoOAprender(350, 1, false)
                                    }
                                    "sortclasse" -> if (puede_Usar_Servicio(servicio)) {
                                        when (_perso.getClaseID(true)) {
                                            Constantes.CLASE_FECA -> _perso.fijarNivelHechizoOAprender(422, 1, false)
                                            Constantes.CLASE_OSAMODAS -> _perso.fijarNivelHechizoOAprender(420, 1, false)
                                            Constantes.CLASE_ANUTROF -> _perso.fijarNivelHechizoOAprender(425, 1, false)
                                            Constantes.CLASE_SRAM -> _perso.fijarNivelHechizoOAprender(416, 1, false)
                                            Constantes.CLASE_XELOR -> _perso.fijarNivelHechizoOAprender(424, 1, false)
                                            Constantes.CLASE_ZURCARAK -> _perso.fijarNivelHechizoOAprender(412, 1, false)
                                            Constantes.CLASE_ANIRIPSA -> _perso.fijarNivelHechizoOAprender(427, 1, false)
                                            Constantes.CLASE_YOPUKA -> _perso.fijarNivelHechizoOAprender(410, 1, false)
                                            Constantes.CLASE_OCRA -> _perso.fijarNivelHechizoOAprender(418, 1, false)
                                            Constantes.CLASE_SADIDA -> _perso.fijarNivelHechizoOAprender(426, 1, false)
                                            Constantes.CLASE_SACROGITO -> _perso.fijarNivelHechizoOAprender(421, 1, false)
                                            Constantes.CLASE_PANDAWA -> _perso.fijarNivelHechizoOAprender(423, 1, false)
                                        }
                                    }
                                    else -> GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Servicio no existe.")
                                }
                            } catch (e: Exception) {
                                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Servicio con excepcion [1].")
                            }
                        }
                        return true
                    }
                    "servicio", "service", "services", "servicios" -> {
                        if (split.size < 2) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, MainServidor.MENSAJE_SERVICIOS)
                        } else {
                            try {
                                val servicio: String = split[1].toLowerCase()
                                when (servicio) {
                                    "guilde", "guild", "gremio" -> {
                                        if (!_perso.estaDisponible(true, true)) {
                                            return false
                                        }
                                        if (_perso.getGremio() != null || _perso.getMiembroGremio() != null) {
                                            return false
                                        }
                                        if (puede_Usar_Servicio(servicio)) {
                                            Accion.realizar_Accion_Estatico(-2, "", _perso, null, -1, -1.toShort())
                                        }
                                    }
                                    "scroll", "fullstats", "parcho" -> {
                                        if (!_perso.estaDisponible(false, false)) {
                                            return false
                                        }
                                        if (MainServidor.PARAMS_SCROLL_NO_RESET) {
                                            if (_perso.getResets() > 0) {
                                                return true
                                            }
                                        }
                                        if (puede_Usar_Servicio(servicio)) {
                                            val stats = intArrayOf(124, 118, 123, 119, 125, 126)
                                            for (s in stats) {
                                                if (_perso.getStatScroll(s) > 0) {
                                                    GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                                            "Veuillez remettre à zéro vos caractéristiques via la Fée Risette avant de vous parchotter.")
                                                    return true
                                                }
                                            }
                                            for (s in stats) {
                                                Accion.realizar_Accion_Estatico(8, "$s,101", _perso, null, -1, -1.toShort())
                                            }
                                        }
                                        return true
                                    }
                                    "restater", "restarter", "reset" -> {
                                        if (_perso.getNivel() < 30) {
                                            return false
                                        }
                                        if (puede_Usar_Servicio(servicio)) {
                                            _perso.resetearStats(false)
                                        }
                                    }
                                    "sortspecial" -> if (puede_Usar_Servicio(servicio)) {
                                        _perso.fijarNivelHechizoOAprender(350, 1, false)
                                    }
                                    "sortclasse" -> if (puede_Usar_Servicio(servicio)) {
                                        when (_perso.getClaseID(true)) {
                                            Constantes.CLASE_FECA -> _perso.fijarNivelHechizoOAprender(422, 1, false)
                                            Constantes.CLASE_OSAMODAS -> _perso.fijarNivelHechizoOAprender(420, 1, false)
                                            Constantes.CLASE_ANUTROF -> _perso.fijarNivelHechizoOAprender(425, 1, false)
                                            Constantes.CLASE_SRAM -> _perso.fijarNivelHechizoOAprender(416, 1, false)
                                            Constantes.CLASE_XELOR -> _perso.fijarNivelHechizoOAprender(424, 1, false)
                                            Constantes.CLASE_ZURCARAK -> _perso.fijarNivelHechizoOAprender(412, 1, false)
                                            Constantes.CLASE_ANIRIPSA -> _perso.fijarNivelHechizoOAprender(427, 1, false)
                                            Constantes.CLASE_YOPUKA -> _perso.fijarNivelHechizoOAprender(410, 1, false)
                                            Constantes.CLASE_OCRA -> _perso.fijarNivelHechizoOAprender(418, 1, false)
                                            Constantes.CLASE_SADIDA -> _perso.fijarNivelHechizoOAprender(426, 1, false)
                                            Constantes.CLASE_SACROGITO -> _perso.fijarNivelHechizoOAprender(421, 1, false)
                                            Constantes.CLASE_PANDAWA -> _perso.fijarNivelHechizoOAprender(423, 1, false)
                                        }
                                    }
                                    else -> GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Servicio no existe.")
                                }
                            } catch (e: Exception) {
                                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Servicio con excepcion [1].")
                            }
                        }
                        return true
                    }
                    "help", "comandos", "tutorial", "ayuda", "commands", "command", "commandes" -> {
                        if (!MainServidor.MENSAJE_COMANDOS.isEmpty()) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, MainServidor.MENSAJE_COMANDOS)
                        } else {
                            GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso,
                                    """
                                        Les commandes disponnible sont :
                                        <b>.infos</b> - Permet d'obtenir des informations sur le serveur.
                                        <b>.start</b> - Permet de se téléporter au zaap d'Astrub.
                                        <b>.staff</b> - Permet de voir les membres du staff connectés.
                                        <b>.boutique</b> - Permet de se téléporter à la map Boutique.
                                        <b>.points</b> - Savoir ses points boutique.
                                        <b>.all</b> - Permet d'envoyer un message à tous les joueurs.
                                        <b>.celldeblo</b> - Vous tp a une cellule Libre si vous êtes bloqué.
                                        <b>.banque</b> - Ouvrir la banque n’importe où.
                                        <b>.maitre</b> -permet crée l'éscouade , inviter tout tes mules dans ton groupes et rediriger tout les Messages privés de tes mûles vers le Maître.
                                        <b>.pass</b> -  permet au joueurs de passer automatiquement ses tours.
                                        <b>.transfert</b> -  transfert rapide en banque ( Items , Divers et ressources).
                                        <b>.tp</b> - Permet de TP tes Personajes sur ta map actuel ( hors Donjon).
                                        <b>.join</b> - permet que les Personajes s’autotp et rejoignent automatiquement quand un combat et lancer.
                                        """.trimIndent(),
                                    "B9121B")
                        }
                        return true
                    }
                    "desbug" -> {
                        if (_perso.getPelea() != null) {
                            return false
                        }
                        _perso.resetearVariables()
                        GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, "Tus variables han sido reseteadas", "B9121B")
                        return true
                    }
                    "banco" -> {
                        try {
                            if (!_perso.estaDisponible(false, false)) {
                                if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                                    GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, "Vous êtes occupé", "B9121B")
                                }
                                return true
                            }
                            val costo: Int = _perso.getCostoAbrirBanco()
                            if (_perso.getKamas() - costo < 0) {
                                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1128;$costo")
                                GestorSalida.ENVIAR_M1_MENSAJE_SERVER_SVR_MUESTRA_INSTANTANEO(_perso, 10, costo.toString() + "", "")
                            } else {
                                _perso.addKamas(-costo, false, true)
                                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "020;$costo")
                                _perso.getBanco().abrirCofre(_perso)
                            }
                        } catch (e: Exception) {
                            return true
                        }
                        return true
                    }
                    "boutique" -> {
                        if (!_perso.estaDisponible(false, true)) {
                            if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                                GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, "Vous êtes occupé", "B9121B")
                            }
                            break
                        }
                        _perso.teleport(21455.toShort(), 242.toShort())
                        return true
                    }
                    "noall" -> {
                        _perso.removerCanal("~")
                        return true
                    }
                    "todos", "all" -> {
                        split = msjChat.split(" ", 2)
                        if (split.size < 2) {
                            return true
                        }
                        // if (_perso.getNivel() < 30) {
                        // GestorSalida.ENVIAR_Im_INFORMACION(_perso, "13");
                        // return false;
                        // }
                        var h: Long
                        if (((System.currentTimeMillis() - _tiempoUltAll) / 1000).also { h = it } < MainServidor.SEGUNDOS_CANAL_ALL) {
                            h = MainServidor.SEGUNDOS_CANAL_ALL - h
                            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "0115;" + (Math.ceil(h) as Int + 1))
                            return true
                        }
                        _tiempoUltAll = System.currentTimeMillis()
                        GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_TODOS("~", _perso, split[1])
                        return true
                    }
                    "vip", "abonado" -> {
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, MainServidor.MENSAJE_VIP)
                        return true
                    }
                    "staff" -> {
                        val staff = StringBuilder()
                        var staffO = 0
                        for (perso in Mundo.getPersonajesEnLinea()) {
                            try {
                                if (perso.esIndetectable()) {
                                    continue
                                }
                                if (perso.getAdmin() < 1) {
                                    continue
                                }
                                if (staff.length() > 0) {
                                    staff.append(" - ")
                                }
                                staff.append(perso.getNombre())
                                staffO++
                            } catch (e: Exception) {
                            }
                        }
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "<b>" + staffO + " online: "
                                + staff.toString()
                                + " Si no encuentras ningun STAFF, no dudes en preguntar a cualquier usuario! Eso es lo que es un GS.</b>")
                        return true
                    }
                    "npc" -> {
                        if (Mundo.evento) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                    """
                                        ====================
                                        <b>${MainServidor.NOMBRE_SERVER.toString()}</b>
                                        Evento: La subzona del npc es: ${Mundo.eventoMensaje.toString()}
                                        Portal: La subzona del npc es: ${Mundo.portalMensaje.toString()}
                                        ====================
                                        """.trimIndent())
                        } else {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                    """
                                        ====================
                                        <b>${MainServidor.NOMBRE_SERVER.toString()}</b>
                                        Portal: La subzona del npc es: ${Mundo.portalMensaje.toString()}
                                        ====================
                                        """.trimIndent())
                        }
                        return true
                    }
                    "info_server", "info", "infos", "online" -> {
                        try {
                            var enLinea: Long = ServidorServer.getSegundosON() * 1000
                            val dia = (enLinea / 86400000L).toInt()
                            enLinea %= 86400000L
                            val hora = (enLinea / 3600000L).toInt()
                            enLinea %= 3600000L
                            val minuto = (enLinea / 60000L).toInt()
                            enLinea %= 60000L
                            val segundo = (enLinea / 1000L).toInt()
                            if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                        """
                                            ====================
                                            <b>${MainServidor.NOMBRE_SERVER.toString()}</b>
                                            Uptime: 
                                            """.trimIndent() + dia
                                                .toString() + "j " + hora.toString() + "h " + minuto.toString() + "m " + segundo.toString() + "s\n" + "Joueurs en ligne: " + ServidorServer.nroJugadoresLinea().toString() + "\n" + "Record de connexions: " + ServidorServer.getRecordJugadores().toString() + "\n" + "====================")
                            } else {
                                if (!MainServidor.MODO_IMPACT) {
                                    GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                            """
                                                ====================
                                                <b>${MainServidor.NOMBRE_SERVER.toString()}</b>
                                                EnLínea: ${dia}d ${hora}h ${minuto}m ${segundo}s
                                                Jugadores en línea: ${ServidorServer.nroJugadoresLinea().toString()}
                                                IP unicas: ${ServidorServer.getUniqueIP().toString()}
                                                Record de conexión: ${ServidorServer.getRecordJugadores().toString()}
                                                
                                                """.trimIndent() + (if (Mundo.evento) """
     Evento: La subzona del npc es: ${Mundo.eventoMensaje.toString()}
     
     """.trimIndent() else "")
                                                    .toString() + "====================")
                                } else {
                                    val segundosFaltan: Int = MainServidor.SEGUNDOS_REBOOT_SERVER
                                    var ProxResetM = segundosFaltan / 60
                                    val ProxResetH = ProxResetM / 60
                                    ProxResetM = ProxResetM % 60
                                    val messES = """
                                        <font color='#009900'>-</font>
                                        <font color='#009900'><b>Servidor</b> <b>${MainServidor.NOMBRE_SERVER}</b></font>
                                        <font color='#009900'>Tiempo <b>en línea</b> : </font><font color='#09bac1 '>$dia<b>d</b> $hora<b>h</b> $minuto<b>m</b> $segundo<b>s</b>
                                        <font color='#009900'>Jugadores <b>en línea</b> : </font><font color='#09bac1 '>${ServidorServer.nroJugadoresLinea()}</font>
                                        <font color='#009900'>IP unicas:: </font><font color='#09bac1 '>${ServidorServer.getUniqueIP()}</font>
                                        <font color='#009900'>Record de conexiones : </font><font color='#09bac1 '>${ServidorServer.getRecordJugadores()}</font>
                                        <font color='#009900'>Reset automático en : </font><font color='#09bac1 '>$ProxResetH<b>h </b>$ProxResetM<b>m</b></font>
                                        
                                        """.trimIndent()
                                    GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, messES)
                                }
                            }
                        } catch (e: Exception) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, e.getMessage())
                        }
                        return true
                    }
                    "titulo" -> {
                        if (split.size < 2) {
                            val mensaje = StringBuilder()
                            mensaje.append("<b>Seleccione un titulo comando .titulo #</b>")
                            mensaje.append("\n")
                            for (b in _perso.getTitulos().keySet()) {
                                val nombre: String = Mundo.getTitulo(b).get_nombre()
                                mensaje.append("<b>.titulo $b</b> - $nombre")
                                mensaje.append("\n")
                            }
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, mensaje.toString())
                        } else {
                            try {
                                val titulo: Int = Integer.parseInt(split[1])
                                if (_perso.getTitulos().containsKey(titulo)) {
                                    _perso.setTitulo(titulo)
                                    GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                            "Seleccionaste el titulo: " + Mundo.getTitulo(titulo).get_nombre())
                                } else {
                                    GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                            "El titulo escogido no lo tienes agregado")
                                }
                            } catch (e: Exception) {
                                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Titulo escojido no existe")
                            }
                        }
                        return true
                    }
                    "bloquearxp" -> {
                        _perso.bloquearXP = !_perso.bloquearXP
                        if (_perso.bloquearXP) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                    "Su xp ha sido bloqueada para todo lo que te de experiencia")
                        } else {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                    "Su xp ha sido desbloqueada ya puede seguir ganado experiencia")
                        }
                        return true
                    }
                    "maguear", "elemental", "fmcac", "fm" -> {
                        val exObj: Objeto = _perso.getObjPosicion(Constantes.OBJETO_POS_ARMA)
                        if (exObj == null) {
                            if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Vous ne portez aucune arme.")
                            } else {
                                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "NO TIENES UN ARMA A MAGUEAR")
                            }
                            return false
                        }
                        if (split.size < 2) {
                            if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                        "Vous devez specifier un argument (air - terre - eau - feu).")
                            } else {
                                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                        "Debes especificar un elemento (aire - tierra - agua - fuego).")
                            }
                            return false
                        }
                        var statFM = 0
                        when (split[1].toLowerCase()) {
                            "eau", "agua", "suerte", "water" -> statFM = 96
                            "terre", "tierra", "fuerza", "earth" -> statFM = 97
                            "air", "aire", "agilidad", "agi" -> statFM = 98
                            "feu", "fuego", "inteligencia", "fire" -> statFM = 99
                        }
                        if (statFM == 0) {
                            if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                        "Vous devez specifier un argument (air - terre - eau - feu).")
                            } else {
                                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                        "Debes especificar un elemento (aire - tierra - agua - fuego).")
                            }
                            return false
                        }
                        val potenciaFM = 100
                        exObj.forjaMagiaGanar(statFM, potenciaFM)
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1OBJETO_CAMBIO_DANO_ELEMENTAL")
                        GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(_perso, exObj)
                        GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso)
                        GestorSQL.SALVAR_OBJETO(exObj)
                        return true
                    }
                    "exo" -> {
                        if (split.size < 2) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                    "Por favor especificar el argumento (pa/po/pm/invo) espacio (sombrero/capa/botas/anilloderecho/anilloizquierdo/cinturon/amuleto/arma).")
                            return false
                        }
                        var statID = 0
                        when (split[1].toLowerCase()) {
                            "pa" -> statID = Constantes.STAT_MAS_PA
                            "pm" -> statID = Constantes.STAT_MAS_PM
                            "po" -> statID = Constantes.STAT_MAS_ALCANCE
                            "invo" -> statID = Constantes.STAT_MAS_CRIATURAS_INVO
                        }
                        if (statID == 0) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                    "Debes especificar un elemento (pa/po/pm/invo).")
                            return false
                        }
                        var pos: Byte = -1
                        when (split[2].toLowerCase()) {
                            "sombrero" -> pos = Constantes.OBJETO_POS_SOMBRERO
                            "capa" -> pos = Constantes.OBJETO_POS_CAPA
                            "botas" -> pos = Constantes.OBJETO_POS_BOTAS
                            "anilloderecho" -> pos = Constantes.OBJETO_POS_ANILLO_DERECHO
                            "anilloizquierdo" -> pos = Constantes.OBJETO_POS_ANILLO1
                            "cinturon" -> pos = Constantes.OBJETO_POS_CINTURON
                            "amuleto" -> pos = Constantes.OBJETO_POS_AMULETO
                            "arma" -> pos = Constantes.OBJETO_POS_ARMA
                        }
                        if (pos.toInt() == -1) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                    "Debes especificar un elemento (sombrero/capa/botas/anilloderecho/anilloizquierdo/cinturon/amuleto/arma).")
                            return false
                        }
                        val objeto: Objeto = _perso.getObjPosicion(pos)
                        if (objeto == null) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "NO TIENES PUESTO EL ITEM A MAGUEAR")
                            return false
                        }
                        val cantStat: Int = objeto.getStatValor(statID)
                        if (cantStat != 0) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "YA TIENE UN STAT")
                            return false
                        }
                        val statsExo = intArrayOf(Constantes.STAT_MAS_PA, Constantes.STAT_MAS_PM, Constantes.STAT_MAS_ALCANCE,
                                Constantes.STAT_MAS_CRIATURAS_INVO)
                        for (s in statsExo) {
                            if (objeto.tieneStatExo(s)) {
                                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "YA ESTA CON EXOMAGIA")
                                return false
                            }
                        }
                        val precio = 250
                        if (MainServidor.MODO_IMPACT && !GestorSQL.RESTAR_OGRINAS1(_cuenta, precio, _perso)) {
                            return false
                        }
                        if (pos != Constantes.OBJETO_POS_ARMA) objeto.convertirStringAStats(objeto.getObjModelo().generarStatsModelo(CAPACIDAD_STATS.MAXIMO))
                        objeto.fijarStatValor(statID, 1)
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1OBJETO_MAGUEADO")
                        GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(_perso, objeto)
                        GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso)
                        GestorSQL.SALVAR_OBJETO(objeto)
                        return true
                    }
                    "grupo", "group" -> {
                        if (_perso.getGrupoParty() == null) {
                            val idWeb: Int = GestorSQL.GET_ID_WEB(_cuenta.getNombre())
                            if (idWeb <= 0) {
                                return false
                            }
                            val grupo: Grupo?
                            val p: ArrayList<Personaje?> = ArrayList()
                            for (pe in Mundo.getPersonajesEnLinea()) {
                                if (pe.getGrupoParty() != null) {
                                    continue
                                }
                                if (GestorSQL.GET_ID_WEB(pe.getCuenta().getNombre()) === idWeb) {
                                    p.add(pe)
                                }
                            }
                            if (p.size() >= 2) {
                                grupo = Grupo()
                                for (pe in p) {
                                    GestorSalida.ENVIAR_PM_AGREGAR_PJ_GRUPO_A_GRUPO(grupo, pe.stringInfoGrupo())
                                    grupo.addIntegrante(pe)
                                    pe.mostrarGrupo()
                                }
                            }
                        }
                        return true
                    }
                    "jour" -> {
                        _perso.setDeDia()
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, if (_perso.esDeDia()) "1DAY_ON" else "1DAY_OFF")
                        GestorSalida.ENVIAR_BT_TIEMPO_SERVER(_perso)
                        return true
                    }
                    "nuit" -> {
                        _perso.setDeNoche()
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, if (_perso.esDeNoche()) "1NIGHT_ON" else "1NIGHT_OFF")
                        GestorSalida.ENVIAR_BT_TIEMPO_SERVER(_perso)
                        return true
                    }
                    "passTurn", "pasarTurno", "pass" -> {
                        _perso.setComandoPasarTurno(!_perso.getComandoPasarTurno())
                        if (_perso.getComandoPasarTurno()) {
                            if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                                GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, "Pass On", "B9121B")
                            } else {
                                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1PASS_ON")
                            }
                        } else {
                            if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                                GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, "Pass Off", "B9121B")
                            } else {
                                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1PASS_OFF")
                            }
                        }
                        return true
                    }
                    "prisma", "prisme" -> {
                        if (!_perso.estaDisponible(true, true)) {
                            break
                        }
                        Accion.realizar_Accion_Estatico(201, "2,1", _perso, null, -1, -1.toShort())
                        return true
                    }
                    "caceria", "chasse" -> {
                        if (Mundo.NOMBRE_CACERIA.isEmpty() || Mundo.KAMAS_OBJ_CACERIA.isEmpty()) {
                            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1EVENTO_CACERIA_DESACTIVADO")
                            return true
                        }
                        val victima: Personaje = Mundo.getPersonajePorNombre(Mundo.NOMBRE_CACERIA)
                        if (victima == null || !victima.enLinea()) {
                            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1211")
                            return true
                        }
                        if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                            GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_PERSONAJE(_perso, "", 0, Mundo.NOMBRE_CACERIA,
                                    "RECOMPENSE CHASSE - " + Mundo.mensajeCaceria())
                        } else {
                            GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_PERSONAJE(_perso, "", 0, Mundo.NOMBRE_CACERIA,
                                    "RECOMPENSA CACERIA - " + Mundo.mensajeCaceria())
                        }
                        GestorSalida.ENVIAR_IC_PERSONAJE_BANDERA_COMPAS(_perso,
                                victima.getMapa().getX().toString() + "|" + victima.getMapa().getY())
                        return true
                    }
                    "lvl", "nivel", "level", "alignement", "alineacion", "alin", "align" -> {
                        if (!_perso.estaDisponible(true, true)) {
                            break
                        }
                        if (MainServidor.NIVEL_MAX_ESCOGER_NIVEL <= 1) {
                            GestorSalida.ENVIAR_BN_NADA(_perso, "MAX ESCOGER NIVEL ES 1")
                            break
                        }
                        GestorSalida.ENVIAR_bA_ESCOGER_NIVEL(_perso)
                        return true
                    }
                    "taller", "atelier" -> {
                        if (!_perso.estaDisponible(true, true)) {
                            break
                        }
                        if (_perso.getAlineacion() === Constantes.ALINEACION_BONTARIANO) {
                            _perso.teleport(8731.toShort(), 381.toShort())
                        } else if (_perso.getAlineacion() === Constantes.ALINEACION_BRAKMARIANO) {
                            _perso.teleport(8732.toShort(), 367.toShort())
                        }
                        return true
                    }
                    "salvar", "guardar", "save" -> {
                        if (System.currentTimeMillis() - _ultSalvada > 300000) {
                            _ultSalvada = System.currentTimeMillis()
                            GestorSQL.SALVAR_PERSONAJE(_perso, true)
                            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1PERSONAJE_GUARDADO_OK")
                        }
                        return true
                    }
                    "feria" -> {
                        if (!_perso.estaDisponible(true, true)) {
                            break
                        }
                        if (_perso.liderMaitre) {
                            for (pjx in _perso.getGrupoParty().getPersos()) {
                                if (pjx === _perso) continue
                                if (!compruebaTps(pjx)) continue
                                pjx.teleport(6863.toShort(), 324.toShort())
                            }
                        }
                        _perso.teleport(6863.toShort(), 324.toShort())
                        return true
                    }
                    "turn", "turno" -> {
                        try {
                            _perso.getPelea().checkeaPasarTurno()
                        } catch (e: Exception) {
                        }
                        GestorSalida.ENVIAR_BN_NADA(_perso)
                        return true
                    }
                    "endaction", "finaccion", "finalizaraccion" -> {
                        try {
                            _perso.getPelea().finAccion(_perso)
                        } catch (e: Exception) {
                            GestorSalida.ENVIAR_BN_NADA(_perso)
                        }
                        return true
                    }
                    "reports", "reportes" -> {
                        if (_perso.getAdmin() > 0) {
                            GestorSalida.ENVIAR_bD_LISTA_REPORTES(_perso, GestorSQL.GET_LISTA_REPORTES(_cuenta))
                        }
                        return true
                    }
                    "recurso", "ressource" -> {
                        if (!_perso.estaDisponible(true, false)) {
                            break
                        }
                        bustofus_Sistema_Recurso()
                        return true
                    }
                    "tickets", "misboletos", "boletos" -> {
                        if (!_perso.estaDisponible(true, false)) {
                            break
                        }
                        val boletos: String = Mundo.misBoletos(_perso.getID())
                        if (boletos.isEmpty()) {
                            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1DONT_HAVE_TICKETS_LOTERIE")
                        } else {
                            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1YOUR_NUMBERS_TICKETS_LOTERIE;$boletos")
                        }
                        return true
                    }
                    "rates" -> {
                        _perso.mostrarRates()
                        return true
                    }
                    "teodex", "ozeydex", "collection", "cardsmobs", "coleccion", "album", "zafidex", "bestiarie" -> {
                        GestorSalida.ENVIAR_ÑF_BESTIARIO_MOBS(this, _perso.listaCardMobs())
                        return true
                    }
                    "scroll", "parcho", "fullstats" -> {
                        if (!_perso.estaDisponible(false, false)) {
                            break
                        }
                        if (MainServidor.PARAMS_SCROLL_NO_RESET) {
                            if (_perso.getResets() > 0) {
                                return false
                            }
                        }
                        if (MainServidor.LIMITE_SCROLL_COMANDO > 0) {
                            Accion.realizar_Accion_Estatico(8, "124," + MainServidor.LIMITE_SCROLL_COMANDO, _perso, null,
                                    -1, -1.toShort())
                            Accion.realizar_Accion_Estatico(8, "118," + MainServidor.LIMITE_SCROLL_COMANDO, _perso, null,
                                    -1, -1.toShort())
                            Accion.realizar_Accion_Estatico(8, "123," + MainServidor.LIMITE_SCROLL_COMANDO, _perso, null,
                                    -1, -1.toShort())
                            Accion.realizar_Accion_Estatico(8, "119," + MainServidor.LIMITE_SCROLL_COMANDO, _perso, null,
                                    -1, -1.toShort())
                            Accion.realizar_Accion_Estatico(8, "125," + MainServidor.LIMITE_SCROLL_COMANDO, _perso, null,
                                    -1, -1.toShort())
                            Accion.realizar_Accion_Estatico(8, "126," + MainServidor.LIMITE_SCROLL_COMANDO, _perso, null,
                                    -1, -1.toShort())
                        } else {
                            Accion.realizar_Accion_Estatico(8, "124," + MainServidor.LIMITE_SCROLL, _perso, null, -1, -1.toShort())
                            Accion.realizar_Accion_Estatico(8, "118," + MainServidor.LIMITE_SCROLL, _perso, null, -1, -1.toShort())
                            Accion.realizar_Accion_Estatico(8, "123," + MainServidor.LIMITE_SCROLL, _perso, null, -1, -1.toShort())
                            Accion.realizar_Accion_Estatico(8, "119," + MainServidor.LIMITE_SCROLL, _perso, null, -1, -1.toShort())
                            Accion.realizar_Accion_Estatico(8, "125," + MainServidor.LIMITE_SCROLL, _perso, null, -1, -1.toShort())
                            Accion.realizar_Accion_Estatico(8, "126," + MainServidor.LIMITE_SCROLL, _perso, null, -1, -1.toShort())
                        }
                        return true
                    }
                    "guild", "creargremio", "guilde", "gremio", "crear_gremio" -> {
                        if (!_perso.estaDisponible(true, true)) {
                            break
                        }
                        if (_perso.getGremio() != null || _perso.getMiembroGremio() != null) {
                            break
                        }
                        Accion.realizar_Accion_Estatico(-2, "", _perso, null, -1, -1.toShort())
                        return true
                    }
                    "jcj", "pvp" -> {
                        if (!compruebaTps(_perso)) return false
                        if (!_perso.estaDisponible(false, true)) {
                            break
                        }
                        mapa_celda = MainServidor.PVP_MAPA_CELDA
                        if (mapa_celda.isEmpty()) {
                            mapa_celda = "951"
                        }
                        split = mapa_celda.split(";")
                        mapa_celda = split[Formulas.getRandomInt(0, split.size - 1)]
                        mapa = Mundo.getMapa(Short.parseShort(mapa_celda.split(",").get(0)))
                        if (mapa != null) {
                            if (mapa_celda.split(",").length === 1) {
                                celdaID = mapa.getRandomCeldaIDLibre()
                            } else {
                                celdaID = Short.parseShort(mapa_celda.split(",").get(1))
                            }
                            if (_perso.liderMaitre) {
                                for (pjx in _perso.getGrupoParty().getPersos()) {
                                    if (pjx === _perso) continue
                                    if (!compruebaTps(pjx)) continue
                                    pjx.teleport(mapa.getID(), celdaID)
                                }
                            }
                            _perso.teleport(mapa.getID(), celdaID)
                        }
                        return true
                    }
                    "master", "leader", "lider", "maitre", "maestro" -> {
                        if (_perso.getGrupoParty() != null) {
                            crearMaitre(_perso, true)
                        }
                        return true
                    }
                    "inicio" -> {
                        if (!_perso.estaDisponible(false, true)) {
                            break
                        }
                        mapa_celda = MainServidor.START_MAPA_CELDA
                        if (mapa_celda.isEmpty()) {
                            mapa_celda = "7411"
                        }
                        split = mapa_celda.split(";")
                        mapa_celda = split[Formulas.getRandomInt(0, split.size - 1)]
                        mapa = Mundo.getMapa(Short.parseShort(mapa_celda.split(",").get(0)))
                        if (mapa != null) {
                            if (mapa_celda.split(",").length === 1) {
                                celdaID = mapa.getRandomCeldaIDLibre()
                            } else {
                                celdaID = Short.parseShort(mapa_celda.split(",").get(1))
                            }
                            if (_perso.liderMaitre) {
                                for (pjx in _perso.getGrupoParty().getPersos()) {
                                    if (pjx === _perso) continue
                                    if (!compruebaTps(pjx)) continue
                                    pjx.teleport(mapa.getID(), celdaID)
                                }
                            }
                            _perso.teleport(mapa.getID(), celdaID)
                        }
                        return true
                    }
                    "deblo" -> {
                        if (!_perso.estaDisponible(false, true)) {
                            if (_perso.getPelea() != null) {
                                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "191")
                            } else {
                                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1YOU_ARE_BUSSY")
                            }
                            break
                        }
                        if (Mundo.getCasaDentroPorMapa(_perso.getMapa().getID()) != null) {
                            val mapaN: Short = Mundo.getCasaDentroPorMapa(_perso.getMapa().getID()).getMapaIDFuera()
                            val CelR: Short = _perso.getMapa().getRandomCeldaIDLibre()
                            if (_perso.liderMaitre) {
                                for (pjx in _perso.getGrupoParty().getPersos()) {
                                    if (pjx === _perso) continue
                                    if (!compruebaTps(pjx)) continue
                                    pjx.teleport(mapaN, CelR)
                                }
                            }
                            _perso.teleport(mapaN, CelR)
                        } else {
                            val CelR: Short = _perso.getMapa().getRandomCeldaIDLibre()
                            if (_perso.liderMaitre) {
                                for (pjx in _perso.getGrupoParty().getPersos()) {
                                    if (pjx === _perso) continue
                                    if (!compruebaTps(pjx)) continue
                                    pjx.teleport(_perso.getMapa().getID(), CelR)
                                    _perso.teleport(_perso.getMapa().getID(), CelR)
                                }
                            }
                        }
                        return true
                    }
                    "astrub" -> {
                        if (!_perso.estaDisponible(false, true)) {
                            if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                                GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, "Vous êtes occupé", "B9121B")
                            }
                            break
                        }
                        _perso.teleport(7411.toShort(), 340.toShort())
                        return true
                    }
                    "return", "start" -> {
                        if (!compruebaTps(_perso)) return false
                        if (!_perso.estaDisponible(false, true)) {
                            if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                                GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, "Vous êtes occupé", "B9121B")
                            }
                            break
                        }
                        mapa_celda = MainServidor.START_MAPA_CELDA
                        if (mapa_celda.isEmpty()) {
                            mapa_celda = "7411"
                        }
                        split = mapa_celda.split(";")
                        mapa_celda = split[Formulas.getRandomInt(0, split.size - 1)]
                        mapa = Mundo.getMapa(Short.parseShort(mapa_celda.split(",").get(0)))
                        if (mapa != null) {
                            if (mapa_celda.split(",").length === 1) {
                                celdaID = mapa.getRandomCeldaIDLibre()
                            } else {
                                celdaID = Short.parseShort(mapa_celda.split(",").get(1))
                            }
                            if (_perso.liderMaitre) {
                                for (pjx in _perso.getGrupoParty().getPersos()) {
                                    if (pjx === _perso) continue
                                    if (!compruebaTps(pjx)) continue
                                    pjx.teleport(mapa.getID(), celdaID)
                                }
                            }
                            _perso.teleport(mapa.getID(), celdaID)
                        }
                        return true
                    }
                    "shopmap", "shop" -> {
                        if (!compruebaTps(_perso)) return false
                        if (!_perso.estaDisponible(false, true)) {
                            if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                                GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, "Vous êtes occupé", "B9121B")
                            }
                            break
                        }
                        mapa_celda = MainServidor.SHOP_MAPA_CELDA
                        if (mapa_celda.isEmpty()) {
                            mapa_celda = "7411"
                        }
                        split = mapa_celda.split(";")
                        mapa_celda = split[Formulas.getRandomInt(0, split.size - 1)]
                        mapa = Mundo.getMapa(Short.parseShort(mapa_celda.split(",").get(0)))
                        if (mapa != null) {
                            if (mapa_celda.split(",").length === 1) {
                                celdaID = mapa.getRandomCeldaIDLibre()
                            } else {
                                celdaID = Short.parseShort(mapa_celda.split(",").get(1))
                            }
                            if (_perso.liderMaitre) {
                                for (pjx in _perso.getGrupoParty().getPersos()) {
                                    if (pjx === _perso) continue
                                    if (!compruebaTps(pjx)) continue
                                    pjx.teleport(mapa.getID(), celdaID)
                                }
                            }
                            _perso.teleport(mapa.getID(), celdaID)
                        }
                        return true
                    }
                    "tiendafus" -> {
                        if (!_perso.estaDisponible(false, true)) {
                            if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                                GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, "Vous êtes occupé", "B9121B")
                            }
                            break
                        }
                        mapa_celda = MainServidor.TIENDAFUS_MAPA_CELDA
                        if (mapa_celda.isEmpty()) {
                            GestorSalida.ENVIAR_BN_NADA(this)
                            return true
                        }
                        split = mapa_celda.split(";")
                        mapa_celda = split[Formulas.getRandomInt(0, split.size - 1)]
                        mapa = Mundo.getMapa(Short.parseShort(mapa_celda.split(",").get(0)))
                        if (mapa != null) {
                            if (mapa_celda.split(",").length === 1) {
                                celdaID = mapa.getRandomCeldaIDLibre()
                            } else {
                                celdaID = Short.parseShort(mapa_celda.split(",").get(1))
                            }
                            _perso.teleport(mapa.getID(), celdaID)
                        }
                        return true
                    }
                    "enclos", "enclo", "cercado", "cercados" -> {
                        if (!_perso.estaDisponible(false, true)) {
                            if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                                GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, "Vous êtes occupé", "B9121B")
                            }
                            break
                        }
                        mapa_celda = MainServidor.CERCADO_MAPA_CELDA
                        if (mapa_celda.isEmpty()) {
                            mapa_celda = "8747"
                        }
                        split = mapa_celda.split(";")
                        mapa_celda = split[Formulas.getRandomInt(0, split.size - 1)]
                        mapa = Mundo.getMapa(Short.parseShort(mapa_celda.split(",").get(0)))
                        if (mapa != null) {
                            if (mapa_celda.split(",").length === 1) {
                                celdaID = mapa.getRandomCeldaIDLibre()
                            } else {
                                celdaID = Short.parseShort(mapa_celda.split(",").get(1))
                            }
                            _perso.teleport(mapa.getID(), celdaID)
                        }
                        return true
                    }
                    "spellmax" -> {
                        _perso.boostearFullTodosHechizos()
                        _perso.boostearFullTodosHechizosOmega()
                        return true
                    }
                    "bolsa_ogrinas" -> {
                        if (split.size < 2) {
                            return false
                        }
                        var precioO: Int = Integer.parseInt(split[1])
                        if (precioO <= MainServidor.IMPUESTO_BOLSA_OGRINAS || precioO > 100000) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Ingresa un valor entre " + MainServidor.IMPUESTO_BOLSA_OGRINAS.toString() + " a 100000")
                            return false
                        }
                        val bolsaO: Objeto = Mundo.getObjetoModelo(MainServidor.ID_BOLSA_OGRINAS).crearObjeto(1,
                                Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM)
                        if (!GestorSQL.RESTAR_OGRINAS1(_cuenta, precioO, _perso)) {
                            return false
                        }
                        precioO -= MainServidor.IMPUESTO_BOLSA_OGRINAS
                        bolsaO.fijarStatValor(Constantes.STAT_DAR_OGRINAS, precioO)
                        _perso.addObjetoConOAKO(bolsaO, true)
                        return true
                    }
                    "bolsa_creditos" -> {
                        if (split.size < 2) {
                            return false
                        }
                        var precioC: Int = Integer.parseInt(split[1])
                        if (precioC <= MainServidor.IMPUESTO_BOLSA_CREDITOS || precioC > 100000) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Ingresa un valor entre " + MainServidor.IMPUESTO_BOLSA_CREDITOS.toString() + " a 100000")
                            return false
                        }
                        val bolsaC: Objeto = Mundo.getObjetoModelo(MainServidor.ID_BOLSA_CREDITOS).crearObjeto(1,
                                Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM)
                        if (!GestorSQL.RESTAR_CREDITOS(_cuenta, precioC, _perso)) {
                            return false
                        }
                        precioC -= MainServidor.IMPUESTO_BOLSA_CREDITOS
                        bolsaC.fijarStatValor(Constantes.STAT_DAR_CREDITOS, precioC)
                        _perso.addObjetoConOAKO(bolsaC, true)
                        return true
                    }
                    "revivir", "resuciter" -> {
                        _perso.revivir(true)
                        return true
                    }
                    "life", "vida", "vie" -> {
                        if (!_perso.estaDisponible(true, true)) {
                            break
                        }
                        _perso.fullPDV()
                        GestorSalida.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(_perso)
                        return true
                    }
                    "angel", "bonta", "bontariano", "bontarien" -> {
                        Accion.realizar_Accion_Estatico(11, "1", _perso, null, -1, -1.toShort())
                        return true
                    }
                    "demon", "brakmar", "brakmarien", "brakmariano" -> {
                        Accion.realizar_Accion_Estatico(11, "2", _perso, null, -1, -1.toShort())
                        return true
                    }
                    "neutre", "neutral" -> {
                        Accion.realizar_Accion_Estatico(11, "0", _perso, null, -1, -1.toShort())
                        return true
                    }
                    "iglesia", "casarse", "mariage" -> {
                        if (!_perso.estaDisponible(true, true)) {
                            break
                        }
                        _perso.teleport(2019.toShort(), 340.toShort())
                        return true
                    }
                    "puntos", "points", "ogrinas" -> {
                        if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Tu avez " + GestorSQL.GET_OGRINAS_CUENTA(_cuenta.getID()).toString() + " " + comando + ".")
                        } else {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Tienes " + GestorSQL.GET_OGRINAS_CUENTA(_cuenta.getID()).toString() + " " + comando + ".")
                        }
                        return true
                    }
                    "invasion" -> {
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, Mundo.INVASION_AREA)
                        return true
                    }
                    "npcshop", "tienda", "npc_boutique", "npcboutique" -> {
                        if (!_perso.estaDisponible(true, true)) {
                            GestorSalida.ENVIAR_BN_NADA(_perso)
                            return true
                        }
                        if (MainServidor.NPC_BOUTIQUE == null) {
                            return true
                        }
                        _perso.setTipoExchange(Constantes.INTERCAMBIO_TIPO_TIENDA_NPC)
                        _perso.setExchanger(MainServidor.NPC_BOUTIQUE)
                        GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(_perso, 0, MainServidor.NPC_BOUTIQUE.getModelo().getGfxID().toString() + "")
                        GestorSalida.ENVIAR_EL_LISTA_EXCHANGER(_perso, MainServidor.NPC_BOUTIQUE)
                        return true
                    }
                    "enciclopedia" -> {
                        GestorSalida.ENVIAR_bSP_PANEL_ITEMS(_perso)
                        return true
                    }
                    "koliseum", "kolizeum", "koliseo", "koli" -> {
                        if (_perso.getPelea() != null) {
                            break
                        }
                        if (_perso.getGrupoKoliseo() != null || Mundo.estaEnKoliseo(_perso)) {
                            koliseo_Desinscribirse()
                        } else {
                            koliseo_Inscribirse()
                        }
                        return true
                    }
                    "koli1" -> {
                        if (MainServidor.CANTIDAD_MIEMBROS_EQUIPO_KOLISEO_VS1 === 0) {
                            return true
                        }
                        if (_perso.getPelea() != null) {
                            break
                        }
                        if (_perso.getGrupoKoliseo() != null || Mundo.estaEnKoliseo(_perso)) {
                            koliseo_Desinscribirse()
                        } else {
                            koliseo_Inscribirse1()
                        }
                        return true
                    }
                    "koli2" -> {
                        if (MainServidor.CANTIDAD_MIEMBROS_EQUIPO_KOLISEO_VS2 === 0) {
                            return true
                        }
                        if (_perso.getPelea() != null) {
                            break
                        }
                        if (_perso.getGrupoKoliseo() != null || Mundo.estaEnKoliseo(_perso)) {
                            koliseo_Desinscribirse()
                        } else {
                            koliseo_Inscribirse2()
                        }
                        return true
                    }
                    "zone", "zones", "zonas" -> {
                        if (!_perso.estaDisponible(false, true)) {
                            break
                        }
                        GestorSalida.ENVIAR_zC_LISTA_ZONAS(_perso)
                        return true
                    }
                    "energia", "energy", "energie" -> {
                        if (_perso.getPelea() != null) {
                            break
                        }
                        _perso.addEnergiaConIm(10000, true)
                        return true
                    }
                    "refreshmobs", "refrescarmobs", "refresh", "refrescar" -> {
                        _perso.getMapa().refrescarGrupoMobs()
                        return true
                    }
                    "montable" -> {
                        if (_perso.getMontura() == null) {
                            break
                        }
                        _perso.getMontura().setSalvaje(false)
                        _perso.getMontura().setMaxEnergia()
                        _perso.getMontura().setMaxMadurez()
                        _perso.getMontura().setFatiga(0)
                        val restante: Long = Mundo.getExpMontura(5) - _perso.getMontura().getExp()
                        if (restante > 0) {
                            _perso.getMontura().addExperiencia(restante)
                        }
                        return true
                    }
                    "z" -> {
                        if (_perso.getAdmin() > 0) {
                            val msj1 = "<b>" + _perso.getNombre().toString() + "</b> : " + msjChat.split(" ", 2).get(1)
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(msj1)
                            return true
                        }
                        return false
                    }
                    "zx" -> {
                        if (_perso.getAdmin() > 0) {
                            val msj1: String = msjChat.split(" ", 2).get(1)
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(msj1)
                            return true
                        }
                        return false
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return false
    }

    private fun puede_Usar_Servicio(servicio: String?): Boolean {
        if (!MainServidor.PRECIOS_SERVICIOS.containsKey(servicio)) {
            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Servicio no disponible.")
            return false
        }
        try {
            var sPrecio: String? = MainServidor.PRECIOS_SERVICIOS.get(servicio)
            if (sPrecio.contains("k")) {
                sPrecio = sPrecio.replace("k", "")
                val precio: Int = Integer.parseInt(sPrecio)
                if (_perso.getKamas() >= precio) {
                    _perso.addKamas(-precio, true, true)
                } else {
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1128;$precio")
                    return false
                }
            } else if (sPrecio.contains("o")) {
                sPrecio = sPrecio.replace("o", "")
                val precio: Int = Integer.parseInt(sPrecio)
                if (!GestorSQL.RESTAR_OGRINAS1(_cuenta, precio, _perso)) {
                    return false
                }
            } else {
                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Servicio sin precio.")
                return false
            }
        } catch (e: Exception) {
            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Servicio con excepcion [2].")
            return false
        }
        return true
    }

    private fun analizar_Juego(packet: String?) {
        try {
            val pelea: Pelea = _perso.getPelea()
            val estb: Char
            try {
                estb = packet.charAt(1)
            } catch (e: Exception) {
                return
            }
            when (estb) {
                'A' -> juego_Iniciar_Accion(packet)
                'b' -> if (_perso.getPelea() == null) {
                    GestorSalida.ENVIAR_BN_NADA(_perso)
                } else {
                    GestorSalida.ENVIAR_Gñ_IDS_PARA_MODO_CRIATURA(_perso.getPelea(), _perso)
                }
                'C' -> {
                    establecido = true
                    _perso.crearJuegoPJ()
                }
                'D' -> GestorSalida.ENVIAR_GDM_MAPDATA_COMPLETO(_perso)
                'd' -> juego_Retos(packet)
                'f' -> {
                    if (pelea == null) {
                        GestorSalida.ENVIAR_BN_NADA(_perso)
                        return
                    }
                    juego_Mostrar_Celda(packet)
                }
                'F' -> {
                    if (pelea != null) {
                        return
                    }
                    _perso.convertirseFantasma()
                }
                'I' -> juego_Extra_Informacion()
                'K' -> juego_Finalizar_Accion(packet, _perso, _out, _cuenta)
                'P' -> {
                    _perso.botonActDesacAlas(packet.charAt(2))
                    _perso.refrescarStuff(true, true, true)
                }
                'p' -> {
                    if (pelea == null) {
                        GestorSalida.ENVIAR_BN_NADA(_perso)
                        return
                    }
                    juego_Cambio_Posicion(packet)
                }
                'M' -> {
                    if (pelea == null) {
                        GestorSalida.ENVIAR_BN_NADA(_perso)
                        return
                    }
                    juego_Cambio_PosMultiman(packet)
                }
                'm' -> {
                    if (pelea == null) {
                        GestorSalida.ENVIAR_BN_NADA(_perso)
                        return
                    }
                    juego_Cambio_PosPersonaje(packet)
                }
                'Q' -> {
                    if (pelea == null) {
                        GestorSalida.ENVIAR_BN_NADA(_perso)
                        return
                    }
                    juego_Retirar_Pelea(packet)
                }
                'R' -> {
                    if (pelea == null || pelea.getFase() !== Constantes.PELEA_FASE_POSICION) {
                        GestorSalida.ENVIAR_BN_NADA(_perso)
                        return
                    }
                    juego_Listo(packet)
                }
                's', 't' -> {
                    if (pelea == null || pelea.getFase() !== Constantes.PELEA_FASE_COMBATE) {
                        GestorSalida.ENVIAR_BN_NADA(_perso)
                        return
                    }
                    juego_Pasar_Turno()
                }
                'T' -> {
                }
                else -> {
                    GestorSalida.ENVIAR_BN_NADA(_perso)
                    return
                }
            }
        } catch (e: Exception) {
            MainServidor.redactarLogServidorln("EXCEPTION Packet " + packet + ", analizar_Juego " + e.toString())
            e.printStackTrace()
        }
    }

    private fun juego_Retos(packet: String?) {
        try {
            when (packet.charAt(2)) {
                'i' -> {
                    val retoID: Byte = Byte.parseByte(packet.substring(3))
                    _perso.getPelea().mostrarObjetivoReto(retoID, _perso)
                }
            }
        } catch (e: Exception) {
            MainServidor.redactarLogServidorln("EXCEPTION Packet " + packet + ", juego_Retos " + e.toString())
            e.printStackTrace()
        }
    }

    private fun juego_Pasar_Turno() {
        _perso.getPelea().pasarTurnoBoton(_perso)
    }

    private fun juego_Retirar_Pelea(packet: String?) {
        var objetivoID = 0
        try {
            if (packet!!.length() > 2) {
                objetivoID = Integer.parseInt(packet.substring(2))
            }
        } catch (e: Exception) {
        }
        try {
            if (_perso.getPelea() == null) {
                GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(_perso)
                _perso.retornoMapa()
                return
            }
            if (objetivoID != 0) {
                if (_perso.getPelea() != null) {
                    if (_perso.getPelea().getMobGrupo() != null) {
                        if (_perso.getPelea().getMobGrupo().is_evento()) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                    "No puedes expulsar gente en invasion Covid")
                            return
                        }
                    }
                    if (!_perso.getCompañeros().isEmpty()) {
                        if (_perso.getCompañeros().size() !== 1) {
                            for (comp in _perso.getCompañeros()) {
                                val multID: Int = comp.getID()
                                if (objetivoID != multID) {
                                    continue
                                } else {
                                    GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
                                            "No puedes expulsar a un Ayudante")
                                    return
                                }
                            }
                        }
                    }
                }
            }
            if (!_perso.esMultiman()) {
                _perso.getPelea().retirarsePelea(_perso.getID(), objetivoID, false)
            }
        } catch (e: Exception) {
        }
    }

    private fun juego_Mostrar_Celda(packet: String?) {
        try {
            GestorSalida.ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(_perso.getPelea(), 7, _perso.getID(),
                    Short.parseShort(packet.substring(2)))
        } catch (e: Exception) {
        }
    }

    private fun juego_Listo(packet: String?) {
        try {
            if (_perso.getPelea() == null) {
                GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(_perso)
                _perso.retornoMapa()
                return
            }
            if (_perso.getPelea().peleaEmpezada) {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return
            }
            if (_perso.liderMaitre) {
                _perso.getPelea().acabaMaitre = _perso
                for (pjx in _perso.getGrupoParty().getPersos()) {
                    if (pjx === _perso) {
                        _perso.liderMaitre = false
                        continue
                    }
                    pjx.getCuenta().getSocket().analizar_Packets("GR1", false)
                    pjx.esMaitre = false
                }
            } else {
                if (!_perso.esMaitre) {
                    if (System.currentTimeMillis() - _perso.tiempoantibug < 500) {
                        GestorSalida.ENVIAR_BN_NADA(_perso)
                        return
                    }
                    _perso.tiempoantibug = System.currentTimeMillis()
                }
            }
            _perso.totalEspera = 100
            _perso.actualEspera = 0
            _perso.dueñoMaitre = null
            val listo: Boolean = packet.substring(2).equals("1")
            _perso.getPelea().getLuchadorPorID(_perso.getID()).setListo(listo)
            GestorSalida.ENVIAR_GR_TODOS_LUCHADORES_LISTOS(_perso.getPelea(), 3, _perso.getID(), listo)
            _perso.getPelea().verificaTodosListos()
        } catch (e: Exception) {
        }
    }

    private fun juego_Cambio_Posicion(packet: String?) {
        try {
            if (_perso.getPelea() == null) {
                return
            }
            val celdaID: Short = Short.parseShort(packet.substring(2))
            _perso.getPelea().cambiarPosicion(_perso.getID(), celdaID)
        } catch (e: Exception) {
        }
    }

    private fun juego_Cambio_PosPersonaje(packet: String?) {
        try {
            if (_perso.getPelea() == null) {
                return
            }
            val idLuch: Int = Short.parseShort(packet.substring(2))
            _perso.getPelea().cambiarPosPerso(_perso, idLuch)
        } catch (e: Exception) {
        }
    }

    private fun juego_Cambio_PosMultiman(packet: String?) {
        try {
            if (_perso.getPelea() == null) {
                return
            }
            val multimanID: Int = Integer.parseInt(packet.substring(2))
            _perso.getPelea().cambiarPosMultiman(_perso, multimanID)
        } catch (e: Exception) {
        }
    }

    private fun juego_Extra_Informacion() {
        try {
            var reconecta = false
            if (_perso.getPelea() != null) {
                if (_perso.getPelea().getFase() < 4) {
                    GestorSalida.ENVIAR_GDK_CARGAR_MAPA(_perso)
                    try {
                        Thread.sleep(500L)
                    } catch (localException: Exception) {
                    }
                    if (_perso.getReconectado()) {
                        reconecta = true
                        _perso.getPelea().reconectarLuchador(_perso)
                        _perso.setReconectado(false)
                    }
                    return
                } else if (_perso.getReconectado()) {
                    _perso.setReconectado(false)
                }
            }
            val mapa: Mapa = _perso.getMapa()
            val packet = StringBuilder("")
            packet.append("GDK").append(0x00.toChar())
            if (_perso.getPelea() != null) {
                packet.append(mapa.getGMsPersonajes(_perso)).append(0x00.toChar())
                GestorSalida.enviar(_perso, packet.toString(), true)
                return
            }
            if (!reconecta) {
                val cercado: Cercado = mapa.getCercado()
                if (cercado != null) {
                    packet.append("Rp").append(cercado.getDueñoID()).append(";").append(cercado.getPrecio()).append(";")
                            .append(cercado.getCapacidadMax()).append(";").append(cercado.getCantObjMax()).append(";")
                    val gremio: Gremio = cercado.getGremio()
                    if (gremio != null) packet.append(gremio.getNombre()).append(";").append(gremio.getEmblema()) else packet.append(";")
                    packet.append(0x00.toChar())
                }
            }
            if (_perso.esFantasma() /* || Emu.Maldicion */) {
                packet.append("GDZ|+239;18;1").append(0x00.toChar())
            }
            packet.append(mapa.getGMsPersonajes(_perso)).append(0x00.toChar())
            val mobs: String = mapa.getGMsGrupoMobs(_perso)
            if (mobs !== "" && mobs.length() > 4) packet.append(mobs).append(0x00.toChar())
            val npcs: String = mapa.getGMsNPCs(_perso)
            if (npcs !== "" && npcs.length() >= 4) packet.append(npcs).append(0x00.toChar())
            val recau: String = mapa.getGMRecaudador()
            if (recau !== "" && recau.length() > 4) packet.append(recau).append(0x00.toChar())
            val obj: String = mapa.getObjetosInteracGDF()
            if (obj !== "" && obj.length() > 4) packet.append(obj).append(0x00.toChar())
            val merca: String = mapa.getGMsMercantes()
            if (merca !== "" && merca.length() > 4) packet.append(merca).append(0x00.toChar())
            val prism: String = mapa.getGMPrisma()
            if (prism !== "") packet.append(prism).append(0x00.toChar())
            val mont: String = mapa.getGMsMonturas(_perso)
            if (mont !== "" && mont.length() > 4) packet.append(mont).append(0x00.toChar())
            val gdo: String = mapa.getObjetosCria()
            if (gdo !== "" && gdo.length() > 4) packet.append(gdo).append(0x00.toChar())
            // packet.append("ILS" + _perso.lasttime).append((char) 0x00);
            packet.append("fC" + mapa.getNumeroPeleas()).append(0x00.toChar())
            GestorSalida.enviar(_perso, packet.toString(), true)
            if (!reconecta) Mundo.cargarPropiedadesCasa(_perso)
            _perso.getMapa().agregarEspadaPelea(_perso)
            _perso.getMapa().objetosTirados(_perso)
            // nada de abajo es grafico
            _perso.packetModoInvitarTaller(_perso.getOficioActual(), false)
            _perso.setCargandoMapa(false)
            if (_perso.iniciaTorneo != null) {
                val timer = Timer(500, object : ActionListener() {
                    @Override
                    fun actionPerformed(e: ActionEvent?) {
                        (e.getSource() as Timer).stop()
                        if (MainServidor.empezoTorneo) {
                            if (_perso.iniciaTorneo.enLinea()) {
                                iniciaPeleaVS(_perso, _perso.iniciaTorneo)
                            } else {
                                ganadorTorneo(_perso)
                                perderTorneo(_perso.iniciaTorneo)
                                GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso,
                                        Idiomas.getTexto(_perso.getCuenta().idioma, 82), Colores.VERDE)
                            }
                        }
                        _perso.iniciaTorneo = null
                        return
                    }
                })
                timer.start()
            }
            if (_perso.esperaPelea != null) {
                val timer = Timer(500, object : ActionListener() {
                    @Override
                    fun actionPerformed(e: ActionEvent?) {
                        (e.getSource() as Timer).stop()
                        if (MainServidor.empezoTorneo) {
                            if (!_perso.esperaPelea.enLinea()) {
                                ganadorTorneo(_perso)
                                perderTorneo(_perso.esperaPelea)
                                GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 82), Colores.VERDE)
                            }
                        }
                        _perso.esperaPelea = null
                        return
                    }
                })
                timer.start()
            }
            if (_perso.dueñoMaitre != null) {
                _perso.dueñoMaitre.actualEspera += 1
                if (_perso.dueñoMaitre.actualEspera === _perso.dueñoMaitre.totalEspera) {
                    if (_perso.dueñoMaitre.enLinea()
                            && _perso.dueñoMaitre.getMapa().getID() === _perso.getMapa().getID()) crearMaitre(_perso.dueñoMaitre, true)
                    _perso.dueñoMaitre = null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            System.out.println("ERROR MAPA : " + e.getMessage())
            _perso.setCargandoMapa(false)
            MainServidor.redactarLogServidorln("EXCEPTION juego_Cargando_Informacion_Mapa " + e.toString())
        }
    }

    fun salir(nullea: Boolean) {
        try {
            if (_cuenta != null && _cuenta.getSocket() != null) {
                _cuenta.desconexion()
            }
            if (_socket != null) {
                if (!_socket.isClosed()) {
                    _socket.close()
                    _socket = null
                }
            }
            if (_in != null) {
                _in.close()
                _in = null
            }
            if (_out != null) {
                _out.close()
                _out = null
            }
            if (_thread != null) {
                if (_thread.isAlive()) {
                    if (!_thread.isInterrupted()) {
                        _thread.interrupt()
                        _thread = null
                    }
                }
            }
        } catch (e: Exception) {
        }
    }

    private fun juego_Iniciar_Accion(packet: String?) {
        try {
            val accionID: Int
            accionID = try {
                Integer.parseInt(packet.substring(2, 5))
            } catch (e: NumberFormatException) {
                return
            }
            var sigAccionJuegoID = 1
            val _acciones: Map<Integer?, AccionDeJuego?> = _perso.get_acciones()
            if (accionID == 1 && _perso.getPelea() == null) {
                if (_perso.get_acciones().get(100) != null) _perso.get_acciones().remove(100)
                sigAccionJuegoID = 100
            } else {
                if (_acciones.size() > 0) {
                    sigAccionJuegoID = _acciones.keySet().toArray().get(_acciones.size() - 1) as Integer + 1
                }
            }
            val AJ = AccionDeJuego(sigAccionJuegoID, accionID, packet)
            when (accionID) {
                1 -> juego_Desplazamiento(AJ, _perso, _out, _cuenta, packet)
                500 -> {
                    if (_perso.liderMaitre || _perso.esMaitre) {
                        GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 83),
                                Colores.ROJO)
                        GestorSalida.ENVIAR_BLOQUEO_PANTALLA(false, _perso)
                        return
                    }
                    juego_Accion(AJ, _perso, _cuenta)
                    _perso.setTaller(AJ)
                }
                34 -> if (MainServidor.SON_DE_LUCIANO) {
                    val _args: String = packet.substring(5, packet!!.length())
                    if (_perso.tieneMision(Integer.parseInt(_args))) {
                        GestorSalida.ENVIAR_BN_NADA(_perso, "TIENE MISION")
                        return
                    }
                    val misionMod: MisionModelo = Mundo.getMision(Integer.parseInt(_args))
                    if (misionMod.getEtapas().isEmpty()) {
                        GestorSalida.ENVIAR_BN_NADA(_perso, "ETAPAS VACIAS")
                        return
                    }
                    if (Mundo.getEtapa(misionMod.getEtapas().get(0)).getObjetivosPorNivel(0).isEmpty()) {
                        GestorSalida.ENVIAR_BN_NADA(_perso, "OBJETIVOS VACIOS")
                        return
                    }
                    _perso.addNuevaMision(misionMod)
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "054;" + Integer.parseInt(_args))
                } else {
                    juego_Caceria()
                }
                300 -> juego_Lanzar_Hechizo(packet)
                303 -> juego_Ataque_CAC(packet)
                512 -> _perso.abrirMenuPrisma()
                507 -> juego_Casa_Accion(packet)
                618, 619 -> {
                    val proponeID: Int = Integer.parseInt(packet.substring(5))
                    _perso.confirmarMatrimonio(proponeID, accionID == 618)
                }
                900 -> juego_Desafiar(packet)
                901 -> if (!juego_Aceptar_Desafio(packet)) {
                    _perso.rechazarDesafio()
                }
                902 -> _perso.rechazarDesafio()
                903 -> juego_Unirse_Pelea(packet)
                906 -> juego_Agresion(packet)
                909 -> juego_Ataque_Recaudador(packet)
                910 -> juego_Ataque_Caceria(packet)
                912 -> juego_Ataque_Prisma(packet)
                else -> {
                    GestorSalida.ENVIAR_BN_NADA(_perso)
                    return
                }
            }
        } catch (e: Exception) {
            GestorSalida.ENVIAR_BN_NADA(_perso, "JUEGO ACCIONES $packet")
        }
    }

    private fun juego_Finalizar_Accion(packet: String?, _perso: Personaje?, _out: PrintWriter?, _cuenta: Cuenta?) {
        try {
            var idUnica = -1
            val infos: Array<String?> = packet.substring(3).split(Pattern.quote("|"))
            idUnica = try {
                Integer.parseInt(infos[0])
            } catch (e: Exception) {
                return
            }
            if (idUnica == -1) {
                return
            }
            val AJ: AccionDeJuego = _perso.get_acciones().get(idUnica) ?: return
            val esOk = packet.charAt(2) === 'K'
            when (AJ._accionID) {
                1 -> if (esOk) { // TODO:
                    if (_perso.getPelea() == null) {
                        val path = AJ.packet
                        val mapa: Mapa = _perso.getMapa()
                        val celdaInicio: Celda = mapa
                                .getCelda(Encriptador.celdaCodigoAID(path.substring(path!!.length() - 2)))
                        if (celdaInicio == null) {
                            GestorSalida.ENVIAR_BN_NADA(_perso)
                            return
                        }
                        val celdaObjetivo: Celda = mapa
                                .getCelda(Encriptador.celdaCodigoAID(AJ.packet.substring(AJ.packet!!.length() - 2)))
                        _perso.setCelda(celdaInicio)
                        _perso.setOrientacion(Encriptador.getNumeroPorValorHash(path.charAt(path!!.length() - 3)))
                        var objeto: ObjetoInteractivo? = null
                        if (celdaObjetivo != null) objeto = celdaObjetivo.getObjetoInteractivo()
                        if (_perso.estaOcupado()) _perso.setOcupado(false)
                        if (objeto != null) {
                            Constantes.getActionIO(_perso, celdaObjetivo, objeto.getGfxID(), _out)
                            Constantes.getSignIO(_perso, celdaObjetivo.getID(), objeto.getGfxID())
                        }
                        _perso.Marche = false
                        if (_perso.liderMaitre) {
                            for (pjx in _perso.getGrupoParty().getPersos()) {
                                if (pjx === _perso) {
                                    GestorSalida.ENVIAR_BLOQUEO_PANTALLA(false, _perso)
                                    continue
                                }
                                pjx.enMovi = false
                            }
                        }
                        mapa.jugadorLLegaACelda(_perso, _perso.getCelda().getID())
                    } else {
                        _perso.getPelea().finalizarMovimiento(_perso)
                        _perso.borrarGA(AJ)
                        if (_perso.getCompañeros().size() > 0) {
                            for (persox in _perso.getCompañeros()) {
                                persox.borrarGA(AJ)
                            }
                        }
                        return
                    }
                } else {
                    var nuevaCeldaID: Short = -1
                    try {
                        nuevaCeldaID = Short.parseShort(infos[1])
                    } catch (e: Exception) {
                        return
                    }
                    if (nuevaCeldaID.toInt() == -1) return
                    val celda: Celda = _perso.getMapa().getCelda(nuevaCeldaID)
                    val path = AJ.packet
                    _perso.setCelda(celda)
                    _perso.setOrientacion(Encriptador.getNumeroPorValorHash(path.charAt(path!!.length() - 3)))
                    // GestorSalida.ENVIAR_BN_NADA(_out);
                }
                500 -> _perso.finalizarAccionEnCelda(AJ)
                else -> System.out.println("No se ha establecido el final de la accion ID: " + AJ._accionID)
            }
            _perso.borrarGA(AJ)
        } catch (e: Exception) {
            e.printStackTrace()
            val error = "EXCEPTION juego_Finalizar_Accion packet " + packet + " e:" + e.toString()
            GestorSalida.ENVIAR_BN_NADA(_perso, error)
            MainServidor.redactarLogServidorln(error)
            return
        }
    }

    private fun juego_Caceria() {
        if (Mundo.NOMBRE_CACERIA.isEmpty() || Mundo.KAMAS_OBJ_CACERIA.isEmpty()) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1EVENTO_CACERIA_DESACTIVADO")
            return
        }
        val victima: Personaje = Mundo.getPersonajePorNombre(Mundo.NOMBRE_CACERIA)
        if (victima == null || !victima.enLinea()) {
            GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1211")
            return
        }
        GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_PERSONAJE(_perso, "", 0, Mundo.NOMBRE_CACERIA,
                "RECOMPENSA CACERIA - " + Mundo.mensajeCaceria())
        GestorSalida.ENVIAR_IC_PERSONAJE_BANDERA_COMPAS(_perso,
                victima.getMapa().getX().toString() + "|" + victima.getMapa().getY())
    }

    private fun juego_Casa_Accion(packet: String?) {
        try {
            val casa: Casa = _perso.getAlgunaCasa()
            if (casa == null) {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return
            }
            val accionID: Int = Integer.parseInt(packet.substring(5))
            when (accionID) {
                81 -> casa.ponerClave(_perso, true)
                100 -> casa.quitarCerrojo(_perso)
                97, 98, 108 -> casa.abrirVentanaCompraVentaCasa(_perso)
            }
        } catch (e: Exception) {
        }
    }

    private fun juego_Ataque_Recaudador(packet: String?) {
        try {
            synchronized(_perso.getMapa().getPrePelea()) {
                if (!_perso.estaDisponible(false, true) || _perso.estaInmovil()) {
                    GestorSalida.ENVIAR_GA903_ERROR_PELEA(_perso, 'o')
                    return
                }
                if (MainServidor.CLASES_PELEA_RECAUDADOR.containsKey(_perso.getClaseID(true))) {
                    if (MainServidor.CLASES_PELEA_RECAUDADOR.get(_perso.getClaseID(true)) === 0) {
                        GestorSalida.ENVIAR_GA903_ERROR_PELEA(_perso, 'f')
                        return
                    }
                }
                if (_perso.esFantasma() || _perso.esTumba()) {
                    GestorSalida.ENVIAR_GA903_ERROR_PELEA(_perso, 'd')
                    return
                }
                if (_perso.liderMaitre || _perso.esMaitre) {
                    GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 84),
                            Colores.AZUL)
                    GestorSalida.ENVIAR_BLOQUEO_PANTALLA(false, _perso)
                    return
                }
                val id: Int = Integer.parseInt(packet.substring(5))
                val recaudador: Recaudador = Mundo.getRecaudador(id)
                if (recaudador.getPelea() != null || recaudador == null || recaudador.getPelea() != null) {
                    return
                }
                if (recaudador.getEnRecolecta()) {
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1180")
                    return
                }
                val t: Long = recaudador.getTiempoRestProteccion()
                if (t > 0) {
                    val f: IntArray = Formulas.formatoTiempo(t)
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso,
                            "1TIENE_PROTECCION;" + f[4] + "~" + f[3] + "~" + f[2] + "~" + f[1])
                    return
                }
                GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA(_perso.getMapa(), -1, 909, _perso.getID().toString() + "", id.toString() + "")
                _perso.getMapa().iniciarPelea(_perso, recaudador, _perso.getCelda().getID(),
                        recaudador.getCelda().getID(), Constantes.PELEA_TIPO_RECAUDADOR, null, false)
            }
        } catch (e: Exception) {
        }
    }

    private fun juego_Ataque_Prisma(packet: String?) {
        try {
            synchronized(_perso.getMapa().getPrePelea()) {
                if (!_perso.estaDisponible(false, true) || _perso.estaInmovil()) {
                    GestorSalida.ENVIAR_GA903_ERROR_PELEA(_perso, 'o')
                    return
                }
                if (_perso.esFantasma() || _perso.esTumba()) {
                    GestorSalida.ENVIAR_GA903_ERROR_PELEA(_perso, 'd')
                    return
                }
                if (_perso.getAlineacion() === Constantes.ALINEACION_NEUTRAL
                        || _perso.getAlineacion() === Constantes.ALINEACION_MERCENARIO) {
                    GestorSalida.ENVIAR_GA903_ERROR_PELEA(_perso, 'a')
                    return
                }
                val id: Int = Integer.parseInt(packet.substring(5))
                val prisma: Prisma = Mundo.getPrisma(id)
                if (prisma.getPelea() != null || prisma.getEstadoPelea() === 0 || prisma.getEstadoPelea() === -2) {
                    return
                }
                val t: Long = prisma.getTiempoRestProteccion()
                if (t > 0) {
                    val f: IntArray = Formulas.formatoTiempo(t)
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso,
                            "1TIENE_PROTECCION;" + f[4] + "~" + f[3] + "~" + f[2] + "~" + f[1])
                    return
                }
                GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA(_perso.getMapa(), -1, 909, _perso.getID().toString() + "", id.toString() + "")
                _perso.getMapa().iniciarPelea(_perso, prisma, _perso.getCelda().getID(), prisma.getCelda().getID(),
                        Constantes.PELEA_TIPO_PRISMA, null, false)
            }
        } catch (e: Exception) {
        }
    }

    fun juego_Agresion(packet: String?) {
        try {
            synchronized(_perso.getMapa().getPrePelea()) {
                if (MainServidor.AUTO_AGREDIR_MISION) {
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "No puedes agredir, solo por mision")
                    return
                }
                if (!_perso.estaDisponible(false, true) || _perso.estaInmovil()) {
                    GestorSalida.ENVIAR_GA903_ERROR_PELEA(_perso, 'o')
                    return
                }
                if (_perso.esFantasma() || _perso.esTumba()) {
                    GestorSalida.ENVIAR_GA903_ERROR_PELEA(_perso, 'd')
                    return
                }
                if (_perso.liderMaitre || _perso.esMaitre) {
                    GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 87),
                            Colores.ROJO)
                    GestorSalida.ENVIAR_BLOQUEO_PANTALLA(false, _perso)
                    return
                }
                if (!_perso.getMapa().puedeAgregarOtraPelea()) {
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1MAP_LIMI_OF_FIGHTS")
                    return
                }
                val agredido: Personaje = Mundo.getPersonaje(Integer.parseInt(packet.substring(5)))
                if (agredido == null) {
                    GestorSalida.ENVIAR_BN_NADA(_perso, "NO EXISTE AGREDIDO")
                    return
                }
                if (agredido.liderMaitre || agredido.esMaitre) {
                    GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 93),
                            Colores.ROJO)
                    return
                }
                if (System.currentTimeMillis() - _tiempoLLegoMapa < MainServidor.SEGUNDOS_AGREDIR_RECIEN_LLEGADO_MAPA
                        * 1000
                        || System.currentTimeMillis() - agredido.getCuenta()
                                .getSocket()._tiempoLLegoMapa < MainServidor.SEGUNDOS_AGREDIR_RECIEN_LLEGADO_MAPA
                        * 1000) {
                    GestorSalida.ENVIAR_BN_NADA(_perso,
                            "NO PUEDES AGREDIR POR " + MainServidor.SEGUNDOS_AGREDIR_RECIEN_LLEGADO_MAPA)
                    return
                }
                if (MainServidor.MODO_PUEDE_AGREDIR_OCUPADO) {
                    if (!Constantes.puedeAgredir2(_perso, agredido)) {
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1DONT_ATTACK_PLAYER")
                        return
                    }
                } else if (!Constantes.puedeAgredir(_perso, agredido)) {
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1DONT_ATTACK_PLAYER")
                    return
                }
                if (MainServidor.PARAM_AGREDIR_MISMO_RESET && agredido.getResets() !== _perso.getResets()) {
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1DONT_ATTACK_PLAYER")
                    return
                }
                if (MainServidor.PARAM_AGREDIR_RESET_ALTERNOS
                        && Math.abs(agredido.getResets() - _perso.getResets()) > 1) {
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1DONT_ATTACK_PLAYER")
                    return
                }
                if (!MainServidor.PARAM_AGRESION_ADMIN && (agredido.getAdmin() > 0 || _perso.getAdmin() > 0)) {
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1DONT_ATTACK_PLAYER")
                    return
                }
                if (!MainServidor.SON_DE_LUCIANO) if (Math.abs(_perso.getNivel() - agredido.getNivel()) > MainServidor.RANGO_NIVEL_PVP) {
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1DONT_ATTACK_PLAYER")
                    return
                }
                if (!_perso.getCuenta().getActualIP().equals("127.0.0.1")
                        && _perso.getCuenta().getActualIP().equals(agredido.getCuenta().getActualIP())) {
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1DONT_ATTACK_PLAYER_SAME_IP")
                    return
                }
                var deshonor = false
                if (MainServidor.PARAM_AGREDIR_JUGADORES_ASESINOS && agredido.getDeshonor() > 0 && agredido.getAlineacion() !== _perso.getAlineacion()) {
                    // salta para irse a atacar
                } else if (_perso.getMapa().mapaNoAgresion()
                        || _perso.getMapa().getSubArea().getArea().getSuperArea().getID() === 3 || MainServidor.SUBAREAS_NO_PVP.contains(_perso.getMapa().getSubArea().getID())) {
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "113")
                    return
                } else if (!MainServidor.PARAM_AGREDIR_NEUTRAL
                        && agredido.getAlineacion() === Constantes.ALINEACION_NEUTRAL) {
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1DONT_ATTACK_PLAYER")
                    return
                } else if (!MainServidor.PARAM_AGREDIR_ALAS_DESACTIVADAS && !agredido.alasActivadas()) {
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1DONT_ATTACK_PLAYER")
                    return
                } else if (agredido.getAlineacion() === Constantes.ALINEACION_NEUTRAL || !agredido.alasActivadas()) {
                    if (_perso.getAlineacion() !== Constantes.ALINEACION_NEUTRAL) {
                        _perso.addDeshonor(1)
                        GestorSalida.ENVIAR_Im_INFORMACION(_perso, "084;1")
                        deshonor = true
                    }
                }
                _perso.getMapa().iniciarPeleaPVP(_perso, agredido, deshonor)
            }
        } catch (e: Exception) {
        }
    }

    private fun juego_Ataque_Caceria(packet: String?) {
        try {
            synchronized(_perso.getMapa().getPrePelea()) {
                if (!_perso.estaDisponible(false, true) || _perso.estaInmovil()) {
                    GestorSalida.ENVIAR_GA903_ERROR_PELEA(_perso, 'o')
                    return
                }
                if (_perso.esFantasma() || _perso.esTumba()) {
                    GestorSalida.ENVIAR_GA903_ERROR_PELEA(_perso, 'd')
                    return
                }
                val id: Int = Integer.parseInt(packet.substring(5))
                val agredido: Personaje = Mundo.getPersonaje(id)
                if (!Mundo.NOMBRE_CACERIA.equalsIgnoreCase(agredido.getNombre())) {
                    return
                }
                if (agredido == null || !agredido.enLinea() || !agredido.estaDisponible(true, true)
                        || agredido.getMapa() !== _perso.getMapa()) {
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1DONT_ATTACK_PLAYER")
                    return
                }
                GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA(_perso.getMapa(), -1, 906, _perso.getID().toString() + "", id.toString() + "")
                _perso.getMapa().iniciarPelea(_perso, agredido, _perso.getCelda().getID(), agredido.getCelda().getID(),
                        Constantes.PELEA_TIPO_CACERIA, null, false)
            }
            // _perso.getPelea().cargarMultiman(_perso);
        } catch (e: Exception) {
        }
    }

    private fun juego_Desafiar(packet: String?) {
        try {
            if (!_perso.estaDisponible(false, true)) {
                GestorSalida.ENVIAR_GA903_ERROR_PELEA(_perso, 'o')
                return
            }
            if (_perso.esFantasma() || _perso.esTumba()) {
                GestorSalida.ENVIAR_GA903_ERROR_PELEA(_perso, 'd')
                return
            }
            if (_perso.getMapa().mapaNoDesafio()) { // || _perso.getMapa().mapaMazmorra()
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "113")
                return
            }
            if (!_perso.getMapa().puedeAgregarOtraPelea()) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1MAP_LIMI_OF_FIGHTS")
                return
            }
            if (_perso.liderMaitre || _perso.esMaitre) {
                GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 39),
                        Colores.ROJO)
                GestorSalida.ENVIAR_BLOQUEO_PANTALLA(false, _perso)
                return
            }
            if (System.currentTimeMillis()
                    - _perso.getTiempoUltDesafio() <= MainServidor.SEGUNDOS_ENTRE_DESAFIOS_PJ * 1000) {
                GestorSalida.ENVIAR_GA903_ERROR_PELEA(_perso, 'o')
                return
            }
            val desafiadoID: Int = Integer.parseInt(packet.substring(5))
            val invitandoA: Personaje = Mundo.getPersonaje(desafiadoID)
            if (invitandoA == null || invitandoA === _perso || !invitandoA.enLinea()
                    || !invitandoA.estaDisponible(true, true) || invitandoA.getMapa() !== _perso.getMapa()) {
                GestorSalida.ENVIAR_GA903_ERROR_PELEA(_perso, 'z')
                return
            }
            if (!_perso.puedeInvitar() || !invitandoA.puedeInvitar()
                    || invitandoA.getCuenta().getSocket().getRealizandoAccion()) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1CANT_YOU_OPPONENT_OCCUPED")
                return
            }
            if (realizandoAccion) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1CANT_YOU_R_OCCUPED")
                return
            }
            if (invitandoA.liderMaitre || invitandoA.esMaitre) {
                GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 91),
                        Colores.ROJO)
                return
            }
            if (!invitandoA.estaVisiblePara(_perso)) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1209")
                return
            }
            _perso.setTiempoUltDesafio()
            _perso.setInvitandoA(invitandoA, "desafio")
            invitandoA.setInvitador(_perso, "desafio")
            GestorSalida.ENVIAR_GA900_DESAFIAR(_perso.getMapa(), _perso.getID(), invitandoA.getID())
            GestorSalida.ENVIAR_wl_NOTIFYCACION(invitandoA, _perso.getNombre(), 4, 0)
        } catch (e: Exception) {
        }
    }

    private fun juego_Aceptar_Desafio(packet: String?): Boolean {
        synchronized(_perso.getMapa().getPrePelea()) {
            if (!_perso.getTipoInvitacion().equals("desafio")) {
                GestorSalida.ENVIAR_BN_NADA(_perso)
                return false
            }
            if (_perso.estaInmovil()) {
                GestorSalida.ENVIAR_GA903_ERROR_PELEA(_perso, 'o')
                return false
            }
            if (_perso.esFantasma() || _perso.esTumba()) {
                GestorSalida.ENVIAR_GA903_ERROR_PELEA(_perso, 'd')
                return false
            }
            val retador: Personaje = _perso.getInvitador()
            if (retador == null || !retador.enLinea()) {
                GestorSalida.ENVIAR_GA903_ERROR_PELEA(retador, 'o')
                return false
            }
            if (!_perso.getMapa().puedeAgregarOtraPelea()) {
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1MAP_LIMI_OF_FIGHTS")
                return false
            }
            GestorSalida.ENVIAR_GA901_ACEPTAR_DESAFIO(_perso, retador.getID(), _perso.getID())
            GestorSalida.ENVIAR_GA901_ACEPTAR_DESAFIO(retador, retador.getID(), _perso.getID())
            retador.setInvitador(null, "")
            _perso.setInvitandoA(null, "")
            _perso.getMapa().iniciarPelea(retador, _perso, retador.getCelda().getID(), _perso.getCelda().getID(),
                    Constantes.PELEA_TIPO_DESAFIO, null, false)
        }
        return true
    }

    private fun juego_Ataque_CAC(packet: String?) {
        try {
            val pelea: Pelea = _perso.getPelea()
            if (pelea == null || pelea.getFase() !== Constantes.PELEA_FASE_COMBATE) {
                GestorSalida.ENVIAR_BN_NADA(_perso, "JUEGO ATAQUE CAC")
                return
            }
            var perso: Personaje? = _perso
            var luch: Luchador? = pelea.getLuchadorPorID(perso.getID())
            if (!luch.puedeJugar()) {
                luch = null
                perso = null
                for (compañero in _perso.getCompañeros()) {
                    val luchTemp: Luchador = pelea.getLuchadorPorID(compañero.getID())
                    if (luchTemp.puedeJugar()) {
                        luch = luchTemp
                        perso = compañero
                        break
                    }
                }
                if (luch == null) {
                    return
                }
            }
            val celdaID: Short = Short.parseShort(packet.substring(5))
            pelea.intentarCAC(perso, celdaID)
        } catch (e: Exception) {
            GestorSalida.ENVIAR_BN_NADA(_perso, "JUEGO ATAQUE CAC EXCEPTION")
            MainServidor.redactarLogServidorln("EXCEPTION Packet " + packet + ", juego_Ataque_CAC " + e.toString())
            e.printStackTrace()
        }
    }

    private fun juego_Lanzar_Hechizo(packet: String?) {
        try {
            val pelea: Pelea = _perso.getPelea()
            if (pelea == null || pelea.getFase() !== Constantes.PELEA_FASE_COMBATE) {
                GestorSalida.ENVIAR_BN_NADA(_perso, "JUEGO LANZAR HECHIZO")
                return
            }
            var perso: Personaje? = _perso
            var luch: Luchador? = pelea.getLuchadorPorID(perso.getID())
            if (!luch.puedeJugar()) {
                luch = null
                perso = null
                if (_perso.getCompañeros().size() > 0) {
                    for (compañero in _perso.getCompañeros()) {
                        val luchTemp: Luchador = pelea.getLuchadorPorID(compañero.getID())
                        if (luchTemp.puedeJugar()) {
                            luch = luchTemp
                            perso = compañero
                            break
                        }
                    }
                }
                if (luch == null) {
                    return
                }
            }
            try {
                if (packet.contains("undefined")) return
                val split: Array<String?> = packet.split(";")
                if (split[1] === "") return
                val hechizoID: Int = Integer.parseInt(split[0].substring(5))
                if (!perso.tieneHechizoID(hechizoID)) {
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1169")
                    return
                }
                val celdaID: Short = Short.parseShort(split[1])
                val SH: StatHechizo = perso.getStatsHechizo(hechizoID)
                if (SH != null) {
                    // pelea.intentarLanzarHechizo(luch, SH, celdaID);
                    pelea.intentarLanzarHechizo1(luch, SH, pelea.getMapaCopia().getCelda(celdaID), false)
                }
            } catch (e: Exception) {
                MainServidor.redactarLogServidorln(
                        "JUEGO LANZAR HECHIZO EXCEPTION " + packet + " | Personaje: " + _perso.getNombre())
                e.printStackTrace()
                return
            }
        } catch (e: Exception) {
            MainServidor.redactarLogServidorln(
                    "JUEGO LANZAR HECHIZO EXCEPTION" + packet + " | Personaje: " + _perso.getNombre())
            e.printStackTrace()
            GestorSalida.ENVIAR_BN_NADA(_perso, "JUEGO LANZAR HECHIZO EXCEPTION$packet")
            return
        }
    }

    private fun juego_Unirse_Pelea(packet: String?) {
        try {
            if (!_perso.estaDisponible(false, true)) {
                GestorSalida.ENVIAR_GA903_ERROR_PELEA(_perso, 'o')
                return
            }
            if (_perso.esFantasma() || _perso.esTumba()) {
                GestorSalida.ENVIAR_GA903_ERROR_PELEA(_perso, 'd')
                return
            }
            val infos: Array<String?> = packet.substring(5).split(";")
            val pelea: Pelea = _perso.getMapa().getPelea(Short.parseShort(infos[0]))
            if (infos.size == 1) {
                pelea.unirseEspectador(_perso, _perso.getAdmin() > 0)
            } else {
                pelea.unirsePelea(_perso, Integer.parseInt(infos[1]))
            }
        } catch (e: Exception) {
            GestorSalida.ENVIAR_BN_NADA(_perso, "JUEGO UNIRSE PELEA EXCEPTION")
        }
    }

    /*
	 * private AccionDeJuego getAccionJuego(int unicaID) { return
	 * _accionesDeJuego.get(unicaID); }
	 */
    @Synchronized
    fun limpiarAcciones(forzar: Boolean) {
        _accionesDeJuego.clear()
        if (forzar) {
            realizandoAccion = false
        }
    }

    @Synchronized
    fun borrarAccionJuego(unicaID: Int, forzar: Boolean) {
        _accionesDeJuego.remove(unicaID)
        if (forzar) {
            realizandoAccion = false
        }
    }

    /*
	 * private synchronized void addAccionJuego(AccionDeJuego AJ) { try { int
	 * idUnica = 1; if (!_accionesDeJuego.isEmpty()) { idUnica = ((Integer)
	 * _accionesDeJuego.keySet().toArray()[_accionesDeJuego.size() - 1] + 1); }
	 * AJ.setIDUnica(idUnica); _accionesDeJuego.put(idUnica, AJ); } catch (Exception
	 * e) { String error = "EXCEPTION addAccionJuego e: " + e.toString();
	 * GestorSalida.ENVIAR_BN_NADA(_perso, error);
	 * MainServidor.redactarLogServidorln(error); } }
	 */
    class AccionDeJuego(var iDUnica: Int, var _accionID: Int, packet: String?) {
        var celdas = 0
        val tiempoInicio: Long
        val pathPacket: String?
        var pathReal: String? = null
        var packet: String?

        init {
            pathPacket = packet.substring(5)
            this.packet = packet
            tiempoInicio = System.currentTimeMillis()
        }
    }

    companion object {
        private val POSIBLES_ATAQUES: HashMap<String?, Integer?>? = HashMap()
        var REGISTROS: Map<String?, StringBuilder?>? = ConcurrentHashMap()
        var JUGADORES_REGISTRAR: ArrayList<String?>? = ArrayList<String?>()
        var RASTREAR_CUENTAS: ArrayList<Integer?>? = ArrayList<Integer?>()
        var RASTREAR_IPS: ArrayList<String?>? = ArrayList<String?>()
        private fun posicionLibre(): Int {
            val posiciones: ArrayList<Integer?> = ArrayList<Integer?>()
            posiciones.add(1)
            posiciones.add(2)
            posiciones.add(3)
            posiciones.add(4)
            posiciones.add(5)
            posiciones.add(6)
            posiciones.add(7)
            posiciones.add(8)
            for (ids in PjsTorneo.entrySet()) {
                if (posiciones.contains(ids.getKey())) {
                    posiciones.remove(ids.getKey())
                }
            }
            return if (posiciones.size() === 0) -1 else posiciones.get(0)
        }

        var PjsTorneo: Map<Integer?, Personaje?>? = TreeMap<Integer?, Personaje?>() // fase de 8
        var PjsTorneo2: Map<Integer?, Personaje?>? = TreeMap<Integer?, Personaje?>() // fase de 4
        var PjsTorneo3: Map<Integer?, Personaje?>? = TreeMap<Integer?, Personaje?>() // fase de 2
        var ganador: Personaje? = null
        var listaMuertos: ArrayList<Personaje?>? = ArrayList<Personaje?>()
        var listaMuertos2: ArrayList<Personaje?>? = ArrayList<Personaje?>()
        var listaMuertos3: ArrayList<Personaje?>? = ArrayList<Personaje?>()
        private fun listaTorneo(): String? {
            val str = StringBuilder()
            var pasad = false
            for (pjx in PjsTorneo.entrySet()) {
                if (pasad) str.append(";")
                var muerto = 0
                if (listaMuertos.contains(pjx.getValue())) muerto = 1
                val pj: Personaje = pjx.getValue()
                str.append(pjx.getKey().toString() + "," + muerto + "," + pj.getColor1() + "*" + pj.getColor2() + "*" + pj.getColor3()
                        + "," + pj.getNombre() + "," + pj.getGfxID(true))
                pasad = true
            }
            for (pjx in PjsTorneo2.entrySet()) {
                if (pasad) str.append(";")
                var muerto = 0
                if (listaMuertos2.contains(pjx.getValue())) muerto = 1
                val pj: Personaje = pjx.getValue()
                str.append(pjx.getKey().toString() + "," + muerto + "," + pj.getColor1() + "*" + pj.getColor2() + "*" + pj.getColor3()
                        + "," + pj.getNombre() + "," + pj.getGfxID(true))
                pasad = true
            }
            for (pjx in PjsTorneo3.entrySet()) {
                if (pasad) str.append(";")
                var muerto = 0
                if (listaMuertos3.contains(pjx.getValue())) muerto = 1
                val pj: Personaje = pjx.getValue()
                str.append(pjx.getKey().toString() + "," + muerto + "," + pj.getColor1() + "*" + pj.getColor2() + "*" + pj.getColor3()
                        + "," + pj.getNombre() + "," + pj.getGfxID(true))
                pasad = true
            }
            if (ganador != null) {
                str.append(";")
                str.append(15.toString() + ",0," + ganador.getColor1() + "*" + ganador.getColor2() + "*" + ganador.getColor3() + ","
                        + ganador.getNombre() + "," + ganador.getGfxID(true))
            }
            return str.toString()
        }

        private val randomMap: Int
            private get() {
                val rand: Int = Formulas.getRandomValor(1, 3)
                return when (rand) {
                    1 -> 10133
                    2 -> 10131
                    3 -> 10134
                    else -> 10134
                }
            }

        fun crearMaitre(_perso: Personaje?, crear: Boolean) {
            if (crear) {
                if (_perso.liderMaitre) {
                    GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 132),
                            Colores.VERDE)
                    return
                }
                if (_perso.getGrupoParty() == null) {
                    GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, "Necesitas un grupo para activar el Maitre", Colores.VERDE)
                    return
                }
                if (_perso.getPelea() != null) {
                    GestorSalida.ENVIAR_BN_NADA(_perso)
                    return
                }
                if (_perso.esMaitre) {
                    GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 133),
                            Colores.VERDE)
                    return
                }
                if (_perso.Marche) return
                val todopasa = true
                var todopasa2 = true
                for (grp in _perso.getGrupoParty().getPersos()) {
                    if (grp === _perso) continue
                    if (grp.getMapa().getID() !== _perso.getMapa().getID()) {
                        GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 134),
                                Colores.VERDE)
                        todopasa2 = false
                        break
                    }
                    grp.esMaitre = true
                }
                if (!todopasa2) return
                if (todopasa) {
                    _perso.liderMaitre = true
                    _perso.esMaitre = false
                    for (grp in _perso.getGrupoParty().getPersos()) {
                        if (grp === _perso) continue
                        grp.teleport(_perso.getMapa().getID(), _perso.getCelda().getID())
                    }
                    GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 135),
                            Colores.VERDE)
                    _perso.sendMessage("Lider Ip activo")
                    _perso.Multis.clear()
                    for (p in Mundo.ONLINES) {
                        if (p.getCuenta().getActualIP().equals(_perso.getCuenta().getActualIP())) {
                            p.EsliderIP = false
                            p.LiderIP = _perso
                            _perso.Multis.add(p)
                        }
                    }
                    _perso.EsliderIP = true
                } else {
                    for (grp in _perso.getGrupoParty().getPersos()) {
                        if (grp === _perso) continue
                        grp.esMaitre = false
                    }
                    GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 136),
                            Colores.ROJO)
                }
            } else {
                if (!_perso.liderMaitre) {
                    GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, "No eres lider del maitre", Colores.VERDE)
                    return
                }
                _perso.liderMaitre = false
                _perso.esMaitre = false
                for (grp in _perso.getGrupoParty().getPersos()) {
                    if (grp === _perso) continue
                    grp.esMaitre = false
                    GestorSalida.ENVIAR_cs_CHAT_MENSAJE(grp, "Ya no eres parte del maitre", Colores.NARANJA)
                }
                _perso.sendMessage("Lider IP desactivo")
                _perso.Multis.clear()
                for (p in Mundo.ONLINES) {
                    if (p.getCuenta().getActualIP().equals(_perso.getCuenta().getActualIP())) {
                        p.EsliderIP = false
                        p.LiderIP = null
                        p.Multi = null
                    }
                }
                GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, "Ya no eres parte del maitre", Colores.NARANJA)
            }
        }

        fun ganadorTorneo(_perso: Personaje?) {
            if (MainServidor.faseTorneo === 1) {
                var posantes = -1
                for (px in PjsTorneo.entrySet()) {
                    if (px.getValue() === _perso) {
                        posantes = px.getKey()
                        break
                    }
                }
                if (posantes == -1) return
                var newpos = -1
                when (posantes) {
                    1, 2 -> newpos = 9
                    3, 4 -> newpos = 10
                    5, 6 -> newpos = 11
                    7, 8 -> newpos = 12
                }
                if (newpos == -1) return
                PjsTorneo2.put(newpos, _perso)
                _perso.addTitulo(124, -1)
                if (PjsTorneo2!!.size() === 4) {
                    GestorSalida.ENVIAR_cs_CHAT_MENSAJE_A_TODOS(Idiomas.getTexto(_perso.getCuenta().idioma, 109),
                            Colores.AZUL)
                    MainServidor.startTorneo()
                }
            } else if (MainServidor.faseTorneo === 2) {
                var posantes = -1
                for (px in PjsTorneo2.entrySet()) {
                    if (px.getValue() === _perso) {
                        posantes = px.getKey()
                        break
                    }
                }
                if (posantes == -1) return
                var newpos = -1
                when (posantes) {
                    9, 10 -> newpos = 13
                    11, 12 -> newpos = 14
                }
                if (newpos == -1) return
                PjsTorneo3.put(newpos, _perso)
                _perso.addObjIdentAInventario(Mundo.getObjetoModelo(36025).crearObjeto(1, Constantes.OBJETO_POS_NO_EQUIPADO,
                        CAPACIDAD_STATS.RANDOM), false)
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "021;" + 1 + "~" + 36025)
                _perso.addTitulo(124, -1)
                if (PjsTorneo3!!.size() === 2) {
                    GestorSalida.ENVIAR_cs_CHAT_MENSAJE_A_TODOS(Idiomas.getTexto(_perso.getCuenta().idioma, 110),
                            Colores.AZUL)
                    MainServidor.startTorneo()
                }
            } else if (MainServidor.faseTorneo === 3) {
                _perso.addObjIdentAInventario(Mundo.getObjetoModelo(50071).crearObjeto(1, Constantes.OBJETO_POS_NO_EQUIPADO,
                        CAPACIDAD_STATS.RANDOM), false)
                GestorSalida.ENVIAR_Im_INFORMACION(_perso, "021;" + 1 + "~" + 50071)
                _perso.addTitulo(125, -1)
                ganador = _perso
                _perso.enTorneo = 0
                GestorSalida.ENVIAR_cs_CHAT_MENSAJE_A_TODOS("Felicidades a " + _perso.getNombre().toString() + " por ganar el Torneo!!!", Colores.NARANJA)
            }
            if (_perso.enLinea() && MainServidor.faseTorneo !== 3) GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 111),
                    Colores.VERDE)
        }

        fun perderTorneo(_perso: Personaje?) {
            if (MainServidor.empezoTorneo) {
                if (MainServidor.faseTorneo === 1) {
                    if (!listaMuertos.contains(_perso)) listaMuertos.add(_perso)
                } else if (MainServidor.faseTorneo === 2) {
                    if (!listaMuertos2.contains(_perso)) listaMuertos2.add(_perso)
                } else if (MainServidor.faseTorneo === 3) {
                    if (!listaMuertos3.contains(_perso)) listaMuertos3.add(_perso)
                }
                if (_perso.enLinea()) GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 112),
                        Colores.ROJO)
            } else {
                if (_perso.enLinea()) GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 113),
                        Colores.ROJO)
                var laid = -1
                for (px in PjsTorneo.entrySet()) {
                    if (px.getValue() === _perso) {
                        laid = px.getKey()
                        break
                    }
                }
                if (laid != -1) PjsTorneo.remove(laid)
                var orden = 1
                val PjsTorneoNEW: Map<Integer?, Personaje?> = TreeMap<Integer?, Personaje?>()
                for (px in PjsTorneo!!.values()) {
                    PjsTorneoNEW.put(orden, px)
                    orden += 1
                }
                PjsTorneo.clear()
                PjsTorneo.putAll(PjsTorneoNEW)
            }
            _perso.enTorneo = 0
        }

        fun iniciarTorneo() {
            if (MainServidor.faseTorneo === 0) {
                if (PjsTorneo!!.size() === 8) {
                    System.out.println("ACA@")
                    MainServidor.empezoTorneo = true
                    MainServidor.faseTorneo = 1
                    GestorSalida.ENVIAR_cs_CHAT_MENSAJE_A_TODOS(
                            "La fase " + MainServidor.faseTorneo
                                    .toString() + "/3 del Torneo ha empezado, puedes ír a ver a los participantes escribiendo .arena",
                            Colores.AZUL)
                    for (i in 1..4) { // 1,2,3,4
                        var val1 = 0
                        var val2 = 1
                        when (i) {
                            1 -> {
                                val1 = 1
                                val2 = 2
                            }
                            2 -> {
                                val1 = 3
                                val2 = 4
                            }
                            3 -> {
                                val1 = 5
                                val2 = 6
                            }
                            4 -> {
                                val1 = 7
                                val2 = 8
                            }
                        }
                        val pj1: Personaje? = PjsTorneo!![val1]
                        val pj2: Personaje? = PjsTorneo!![val2]
                        var listam = false
                        if (!pj1.enLinea() && !pj2.enLinea()) {
                            val random: Int = Formulas.getRandomValor(1, 2)
                            if (random == 1) {
                                perderTorneo(pj1)
                                ganadorTorneo(pj2)
                            } else {
                                perderTorneo(pj2)
                                ganadorTorneo(pj1)
                            }
                            listam = true
                        } else if (!pj1.enLinea() && pj2.enLinea()) {
                            perderTorneo(pj1)
                            ganadorTorneo(pj2)
                            GestorSalida.ENVIAR_cs_CHAT_MENSAJE(pj2, Idiomas.getTexto(pj2.getCuenta().idioma, 82),
                                    Colores.VERDE)
                            listam = true
                        } else if (pj1.enLinea() && !pj2.enLinea()) {
                            perderTorneo(pj2)
                            ganadorTorneo(pj1)
                            GestorSalida.ENVIAR_cs_CHAT_MENSAJE(pj1, Idiomas.getTexto(pj1.getCuenta().idioma, 82),
                                    Colores.VERDE)
                            listam = true
                        }
                        if (listam) continue
                        val randommap = randomMap
                        if (pj1.getMapa().getID() !== randommap) pj1.teleport(randommap, 280.toShort())
                        if (pj2.getMapa().getID() !== randommap) pj2.teleport(randommap, 280.toShort())
                        pj1.iniciaTorneo = pj2
                        pj2.esperaPelea = pj1
                    }
                }
            } else if (MainServidor.faseTorneo === 1) {
                if (PjsTorneo2!!.size() === 4) {
                    MainServidor.faseTorneo = 2
                    GestorSalida.ENVIAR_cs_CHAT_MENSAJE_A_TODOS("La fase " + MainServidor.faseTorneo.toString() + "/3 del Torneo ha empezado", Colores.AZUL)
                    for (i in 1..2) {
                        var val1 = 0
                        var val2 = 1
                        when (i) {
                            1 -> {
                                val1 = 9
                                val2 = 10
                            }
                            2 -> {
                                val1 = 11
                                val2 = 12
                            }
                        }
                        val pj1: Personaje? = PjsTorneo2!![val1]
                        val pj2: Personaje? = PjsTorneo2!![val2]
                        var listam = false
                        if (!pj1.enLinea() && !pj2.enLinea()) {
                            val random: Int = Formulas.getRandomValor(1, 2)
                            if (random == 1) {
                                perderTorneo(pj1)
                                ganadorTorneo(pj2)
                            } else {
                                perderTorneo(pj2)
                                ganadorTorneo(pj1)
                            }
                            listam = true
                        } else if (!pj1.enLinea() && pj2.enLinea()) {
                            perderTorneo(pj1)
                            ganadorTorneo(pj2)
                            GestorSalida.ENVIAR_cs_CHAT_MENSAJE(pj2, Idiomas.getTexto(pj2.getCuenta().idioma, 82),
                                    Colores.VERDE)
                            listam = true
                        } else if (pj1.enLinea() && !pj2.enLinea()) {
                            perderTorneo(pj2)
                            ganadorTorneo(pj1)
                            GestorSalida.ENVIAR_cs_CHAT_MENSAJE(pj1, Idiomas.getTexto(pj1.getCuenta().idioma, 82),
                                    Colores.VERDE)
                            listam = true
                        }
                        if (listam) continue
                        val randommap = randomMap
                        if (pj1.getMapa().getID() !== randommap) pj1.teleport(randommap, 280.toShort())
                        if (pj2.getMapa().getID() !== randommap) pj2.teleport(randommap, 280.toShort())
                        pj1.iniciaTorneo = pj2
                        pj2.esperaPelea = pj1
                    }
                }
            } else if (MainServidor.faseTorneo === 2) {
                if (PjsTorneo3!!.size() === 2) {
                    MainServidor.faseTorneo = 3
                    GestorSalida.ENVIAR_cs_CHAT_MENSAJE_A_TODOS("La fase " + MainServidor.faseTorneo.toString() + "/3 del Torneo ha empezado", Colores.AZUL)
                    val pj1: Personaje? = PjsTorneo3!![13]
                    val pj2: Personaje? = PjsTorneo3!![14]
                    var listam = false
                    if (!pj1.enLinea() && !pj2.enLinea()) {
                        val random: Int = Formulas.getRandomValor(1, 2)
                        if (random == 1) {
                            perderTorneo(pj1)
                            ganadorTorneo(pj2)
                        } else {
                            perderTorneo(pj2)
                            ganadorTorneo(pj1)
                        }
                        listam = true
                    } else if (!pj1.enLinea() && pj2.enLinea()) {
                        perderTorneo(pj1)
                        ganadorTorneo(pj2)
                        GestorSalida.ENVIAR_cs_CHAT_MENSAJE(pj2, Idiomas.getTexto(pj2.getCuenta().idioma, 82),
                                Colores.VERDE)
                        listam = true
                    } else if (pj1.enLinea() && !pj2.enLinea()) {
                        perderTorneo(pj2)
                        ganadorTorneo(pj1)
                        GestorSalida.ENVIAR_cs_CHAT_MENSAJE(pj1, Idiomas.getTexto(pj1.getCuenta().idioma, 82),
                                Colores.VERDE)
                        listam = true
                    }
                    if (listam) return
                    val randommap = randomMap
                    if (pj1.getMapa().getID() !== randommap) pj1.teleport(randommap, 280.toShort())
                    if (pj2.getMapa().getID() !== randommap) pj2.teleport(randommap, 280.toShort())
                    pj1.iniciaTorneo = pj2
                    pj2.esperaPelea = pj1
                }
            }
        }

        private fun iniciaPeleaVS(pj1: Personaje?, pj2: Personaje?) {
            pj1.usaTP = false
            pj2.usaTP = false
            GestorSalida.ENVIAR_cs_CHAT_MENSAJE(pj1, Idiomas.getTexto(pj1.getCuenta().idioma, 114), Colores.VERDE)
            GestorSalida.ENVIAR_cs_CHAT_MENSAJE(pj2, Idiomas.getTexto(pj2.getCuenta().idioma, 114), Colores.VERDE)
            val timer = Timer(3000, object : ActionListener() {
                @Override
                fun actionPerformed(e: ActionEvent?) {
                    (e.getSource() as Timer).stop()
                    var finaliza = false
                    if (!pj1.enLinea() && !pj2.enLinea()) {
                        val random: Int = Formulas.getRandomValor(1, 2)
                        if (random == 1) {
                            perderTorneo(pj1)
                            ganadorTorneo(pj2)
                        } else {
                            perderTorneo(pj2)
                            ganadorTorneo(pj1)
                        }
                        finaliza = true
                    } else if (!pj1.enLinea() && pj2.enLinea()) {
                        perderTorneo(pj1)
                        ganadorTorneo(pj2)
                        GestorSalida.ENVIAR_cs_CHAT_MENSAJE(pj2, Idiomas.getTexto(pj2.getCuenta().idioma, 82),
                                Colores.VERDE)
                        finaliza = true
                    } else if (pj1.enLinea() && !pj2.enLinea()) {
                        perderTorneo(pj2)
                        ganadorTorneo(pj1)
                        GestorSalida.ENVIAR_cs_CHAT_MENSAJE(pj1, Idiomas.getTexto(pj1.getCuenta().idioma, 82),
                                Colores.VERDE)
                        finaliza = true
                    }
                    if (pj1.getPelea() != null) {
                        perderTorneo(pj1)
                        ganadorTorneo(pj2)
                        GestorSalida.ENVIAR_cs_CHAT_MENSAJE(pj2, Idiomas.getTexto(pj2.getCuenta().idioma, 82),
                                Colores.VERDE)
                        finaliza = true
                    }
                    if (pj2.getPelea() != null) {
                        perderTorneo(pj2)
                        ganadorTorneo(pj1)
                        GestorSalida.ENVIAR_cs_CHAT_MENSAJE(pj1, Idiomas.getTexto(pj1.getCuenta().idioma, 82),
                                Colores.VERDE)
                        finaliza = true
                    }
                    if (finaliza) return
                    //GestorSalida.ENVIAR_GA900_DESAFIAR(pj1.getMapa(), pj1.getID(), pj2.getID()); // MAPA,YO,OBJETIVO DUELOID
                    //GestorSalida.ENVIAR_GA901_ACEPTAR_DESAFIO(pj2, pj1.getID(), pj2.getID()); // PERSO MAPA,DUELOID,PERSOID
                    //GestorSalida.ENVIAR_GA902_RECHAZAR_DESAFIO(pj2, pj2.getID(), pj2.getID());
                    pj1.getMapa().iniciarPelea(pj1, pj2, pj1.getCelda().getID(), pj2.getCelda().getID(),
                            Constantes.PELEA_TIPO_DESAFIO, null, true)
                    //GestorSalida.ENVIAR_fC_CANTIDAD_DE_PELEAS(this);
                    pj1.usaTP = true
                    pj2.usaTP = true
                }
            })
            timer.start()
        }

        fun compruebaTps(_perso: Personaje?): Boolean {
            if (_perso == null) return false
            if (!_perso.enLinea()) return false
            if (_perso.getPelea() != null) {
                GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 65), Colores.AZUL)
                return false
            }
            if (_perso.isOnAction()) return false
            if (_perso.getTipoExchange() !== -1) {
                GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 66), Colores.AZUL)
                return false
            }
            if (_perso.getHaciendoTrabajo() != null) {
                GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 67), Colores.AZUL)
                return false
            }
            if (_perso.estaOcupado()) {
                GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 68), Colores.AZUL)
                return false
            }
            return true
        }

        fun juego_Accion(AJ: AccionDeJuego?, _perso: Personaje?, _cuenta: Cuenta?) {
            if (_perso.Marche) {
                _perso._accionesCola.add(AJ)
                return
            }
            val packet: String = AJ!!.packet.substring(5)
            var celdaID: Short = -1
            var accionID = -1
            try {
                celdaID = Short.parseShort(packet.split(";").get(0))
                accionID = Integer.parseInt(packet.split(";").get(1))
            } catch (e: Exception) {
            }
            if (celdaID.toInt() == -1 || _perso.isOnAction() || accionID == -1 || _perso == null || _perso.getMapa() == null || _perso.getMapa().getCelda(celdaID) == null) return
            AJ.packet = "$celdaID;$accionID"
            _perso.addGA(AJ)
            // System.out.println(1);
            _perso.iniciarAccionEnCelda(AJ)
        }

        private fun juego_Desplazamiento(AJ: AccionDeJuego?, _perso: Personaje?, _out: PrintWriter?, _cuenta: Cuenta?,
                                         packet: String?) {
            if (_perso.isOnAction()) return
            val pelea: Pelea = _perso.getPelea()
            if (pelea == null) {
                var persoid = -1
                // int celdadestino = -1;
                var desplaza = ""
                var path = ""
                var celdaD: Short = -1
                try {
                    path = packet.substring(5).split(";").get(0)
                    persoid = Integer.parseInt(packet.split(";").get(1))
                    /*
				 * if (ruta.length() > 2) celdadestino =
				 * Encriptador.celdaCodigoAID(path.substring(path.length()-2)); else
				 * celdadestino = Encriptador.celdaCodigoAID(path);
				 */desplaza = packet.split(";").get(2)
                    celdaD = Short.parseShort(packet.split(";").get(3))
                } catch (e: Exception) {
                    if (_cuenta.getSocket() != null) _cuenta.getSocket().salir(false)
                    return
                }
                if (_perso.esTumba()) {
                    GestorSalida.ENVIAR_BN_NADA(_perso)
                    return
                }
                if (_perso.esMaitre && !_perso.enMovi) {
                    GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 100),
                            Colores.ROJO)
                }
                if (_perso.esMaitre && !_perso.enMovi) {
                    _perso.enMovi = true
                    GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 101),
                            Colores.ROJO)
                }
                if (_perso.isOnAction()) {
                    GestorSalida.ENVIAR_GA_ACCION_DE_JUEGO(_perso, 0, "", "")
                    _perso.borrarGA(AJ)
                    return
                }
                if (celdaD.toInt() != 0) {
                    if (_perso.getMapa().getCelda(celdaD)._acciones.get(0) != null) {
                        val idacc: Int = _perso.getMapa().getCelda(celdaD)._acciones.get(0).getID()
                        val args: String = _perso.getMapa().getCelda(celdaD)._acciones.get(0)._args
                        if (idacc == 0) {
                            _perso.setCargandoMapa(true)
                            val mapa: Mapa = Mundo.getMapa(Integer.parseInt(args.split(",").get(0)))
                            var extra = ""
                            if (mapa.getKey() != null) extra = mapa.getKey()
                            val packetx = StringBuilder()
                            packetx.append("gr").append("|").append(mapa.getID()).append("|").append(mapa.getFecha())
                                    .append("|").append(extra).append("|").append(mapa.getAncho()).append("|")
                                    .append(mapa.getAlto()).append("|").append(mapa.getBgID()).append("|")
                                    .append(mapa.getMusicID()).append("|").append(mapa.getAmbienteID()).append("|")
                                    .append(mapa.getOutDoor()).append("|").append(mapa.getCapabilities()).append("|")
                                    .append(mapa.getMapData()).append("|").append("1")
                            GestorSalida.enviar(_perso, packetx.toString(), true)
                        }
                    }
                }
                _perso.Marche = true
                _perso.addGA(AJ)
                AJ!!.packet = path
                if (_perso.liderMaitre) {
                    for (grp in _perso.getGrupoParty().getPersos()) {
                        if (grp === _perso) continue
                        grp.teleport(_perso.getMapa().getID(), _perso.getCelda().getID())
                    }
                }
                for (z in _perso.getMapa().getArrayPersonajes()) {
                    if (z.getID() === _perso.getID()) {
                        continue
                    }
                    val packetx = ("kw" + (if (AJ.iDUnica <= -1) "" else AJ.iDUnica).toString() + ";" + "1" + ";" + persoid.toString() + ";"
                            + desplaza)
                    GestorSalida.enviar(z, packetx, true)
                }
                if (_perso.estaSentado()) _perso.setSentado(false)
                _perso.setOcupado(true)
                if (_perso.liderMaitre) {
                    if (_perso.getGrupoParty().getPersos().size() > 1) {
                        for (pjx in _perso.getGrupoParty().getPersos()) {
                            if (!pjx.enLinea()) continue
                            if (pjx === _perso) {
                                GestorSalida.ENVIAR_BLOQUEO_PANTALLA(true, _perso)
                                continue
                            }
                            if (pjx.getCelda().getID() === _perso.getCelda().getID()) {
                                pjx.enMovi = true
                                if (pjx.getCuenta().getSocket() != null) {
                                    for (z in pjx.getMapa().getArrayPersonajes()) {
                                        pjx.addGA(AJ)
                                        val packetx = ("kw" + (if (AJ.iDUnica <= -1) "" else AJ.iDUnica).toString() + ";" + "1" + ";"
                                                + pjx.getID().toString() + ";" + desplaza)
                                        GestorSalida.enviar(z, packetx, true)
                                    }
                                } else pjx.esMaitre = false
                            }
                        }
                    }
                }
            } else {
                val path: String = AJ!!.packet.substring(5)
                val luchador: Luchador = pelea.getLuchadorPorID(_perso.getID()) ?: return
                AJ.packet = path
                _perso.inicioAccionMoverse(AJ)
            }
        }
    }

    init {
        try {
            _socket = socket
            actualIP = _socket.getInetAddress().getHostAddress()
            _in = BufferedInputStream(_socket.getInputStream())
            _out = PrintWriter(_socket.getOutputStream())
            if (MainServidor.PARAM_MOSTRAR_IP_CONECTANDOSE || MainServidor.MODO_DEBUG) {
                System.out.println("SE ESTA CONECTANDO LA IP " + actualIP)
            }
            if (Mundo.BLOQUEANDO) {
                GestorSalida.ENVIAR_M0_MENSAJE_BASICOS_SVR_MUESTRA_DISCONNECT(this, "16", "", "")
                cerrarSocket(false, "ServidorSocket BLOQUEADO")
                return
            }
            if (GestorSQL.ES_IP_BANEADA(actualIP)) {
                GestorSalida.ENVIAR_M0_MENSAJE_BASICOS_SVR_MUESTRA_DISCONNECT(this, "29", "", "")
                cerrarSocket(false, "ServidorSocket IP BANEADA")
                return
            }
            if (MainServidor.PARAM_SISTEMA_IP_ESPERA && !ServidorServer.borrarIPEspera(actualIP)) {
                // defecto en la seguridad de tu conexion
                GestorSalida.ENVIAR_M0_MENSAJE_BASICOS_SVR_MUESTRA_DISCONNECT(this, "30", "", "")
                MainServidor.redactarLogServidorln("IP SIN ESPERA (posible ataque): " + actualIP)
                posibleAtaque()
                cerrarSocket(false, "ServidorSocket IP SIN ESPERA")
                return
            }
            POSIBLES_ATAQUES.put(actualIP, 0)
            _accionesDeJuego = TreeMap<Integer?, AccionDeJuego?>()
            //_ultPackets = new String[7];
            //_timePackets = new long[7];
            _aKeys = arrayOfNulls<String?>(16)
            ServidorServer.addCliente(this)
            GestorSalida.ENVIAR_XML_POLICY_FILE(this)
            _thread = Thread(this)
            _thread.setDaemon(true)
            _thread.setPriority(Thread.MAX_PRIORITY)
            _thread.start()
            if (_thread == null) {
                cerrarSocket(true, "ServidorSocket sin thread")
            }
        } catch (e: IOException) {
            cerrarSocket(false, "ServidorSocket(3)")
        } catch (e: Exception) {
            cerrarSocket(false, "ServidorSocket(4)")
            e.printStackTrace()
        }
    }
}