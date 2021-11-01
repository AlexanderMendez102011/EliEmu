package variables.objeto

import java.util.ArrayList

class Objeto {
    private var _objModelo: ObjetoModelo? = null
    var posicion: Byte = Constantes.OBJETO_POS_NO_EQUIPADO
    var iD = 0
    var objevivoID = 0
        private set
    var objModeloID = 0
        private set
    var durabilidad = -1
        private set
    var durabilidadMax = -1
        private set
    var dueñoTemp = 0
    var precio = 0
    var cantidad: Long
    var _statsGeneral: Stats = Stats()
    private var _efectosNormales: ArrayList<EfectoHechizo>? = null
    private var _encarnacion: Encarnacion? = null

    // public void destruir() {
    // try {
    // this.finalize();
    // } catch (Throwable e) {}
    // }
    constructor() {
        cantidad = 1
        posicion = Constantes.OBJETO_POS_NO_EQUIPADO
        _statsGeneral = Stats()
    }

    constructor(id: Int, idObjModelo: Int, cant: Long, pos: Byte, strStats: String,
                idObjevi: Int, precio: Int) {
        iD = id
        _objModelo = Mundo.getObjetoModelo(idObjModelo)
        if (_objModelo == null) {
            MainServidor.redactarLogServidorln("La id del objeto " + id + " esta bug porque no tiene objModelo "
                    + idObjModelo)
            return
        }
        cantidad = cant
        posicion = pos
        _statsGeneral = Stats()
        objevivoID = idObjevi
        objModeloID = idObjModelo
        this.precio = precio
        if (MainServidor.IDS_OBJETOS_STATS_MAXIMOS.contains(idObjModelo)) {
            convertirStringAStats(_objModelo.generarStatsModelo(CAPACIDAD_STATS.MAXIMO))
        } else if (MainServidor.IDS_OBJETOS_STATS_MINIMOS.contains(idObjModelo)) {
            convertirStringAStats(_objModelo.generarStatsModelo(CAPACIDAD_STATS.MINIMO))
        } else if (MainServidor.IDS_OBJETOS_STATS_RANDOM.contains(idObjModelo)) {
            convertirStringAStats(_objModelo.generarStatsModelo(CAPACIDAD_STATS.RANDOM))
        } /*else if(Mundo.getObjetoModelo(idObjModelo).esMimo()){
			convertirStringAStats(strStats);
			String statsMimo = "";
			if(tieneStatTexto(Constantes.STAT_APARIENCIA_OBJETO)){
				statsMimo = getParamStatTexto(Constantes.STAT_APARIENCIA_OBJETO, 3);
			}
			if(tieneStatTexto(Constantes.STAT_OBJETO_OCULTO_1)){
				int idobj1 = Integer.parseInt(getStats().getStatTexto(Constantes.STAT_OBJETO_OCULTO_1));
				int idobj2 = Integer.parseInt(getStats().getStatTexto(Constantes.STAT_OBJETO_OCULTO_2));
				Objeto obj1 = Mundo.getObjeto(idobj1);
				Objeto obj2 = Mundo.getObjeto(idobj2);
				if(obj1==null || obj2==null){
					System.out.println("Objeto id:"+_id+" 1:"+idobj1+"	2:"+idobj2);
					return;
				}
				String statsString = convertirStatsAString(true);
				String[] temp = statsString.split(",");
				StringBuilder statsNew = new StringBuilder();
				for (String s : temp) {
					if (statsNew.length() > 0) statsNew.append(",");
					String[] temp2 = s.split("#");
					if (Constantes.esStatTexto(Integer.parseInt(temp2[0], 16))) {
						if(Integer.parseInt(temp2[0],16)==Constantes.STAT_NIVEL) {
							statsNew.append(temp2[0]+"#0#0#"+obj1.getParamStatTexto(Constantes.STAT_NIVEL,3));
						}else {
							statsNew.append(s);	
						}	
					} else {
						int stats = Integer.parseInt(temp2[0], 16);
						int valor = 0;
						if (obj1.getStats().tieneStatID(stats)) {
							valor = obj1.getStatValor(stats);
						} else if (obj2.getStats().tieneStatID(stats)) {
							valor = obj2.getStatValor(stats);
						}
						statsNew.append(temp2[0]).append("#").append(Integer.toHexString(valor)).append("#0#0#0d0+").append(valor);
					}
				}
				convertirStringAStats(statsNew.toString());
				if(!statsMimo.isEmpty())addStatTexto(Constantes.STAT_APARIENCIA_OBJETO, "0#0#" + statsMimo);
				GestorSQL.SALVAR_OBJETO(this);
				System.out.println("Objeto Salvado Revuelto "+id+" Modelo: "+idObjModelo);
			}else if(tieneStatTexto(Constantes.STAT_NIVEL)) {
				String statsTemp = getParamStatTexto(Constantes.STAT_NIVEL,3);
				int nivelObjeto = Integer.parseInt(statsTemp, 16);
				int nivel = 1;
				convertirStringAStats(_objModelo.generarStatsModelo(CAPACIDAD_STATS.MAXIMO));
				for (int j = 0; j < nivelObjeto-1; j++) {
					nivel++;
					String statsString = convertirStatsAString(true);
					String[] temp = statsString.split(",");
					StringBuilder statsNew = new StringBuilder();
					for(int i = 0 ; i<temp.length;i++) {
						if(statsNew.length()>0)statsNew.append(",");
						String [] temp2 = temp[i].split("#");
						int stats = Integer.parseInt(temp2[0],16);
						if(Constantes.esStatTexto(stats)){
							if(Integer.parseInt(temp2[0],16)==Constantes.STAT_NIVEL) {
								statsNew.append(temp2[0]+"#0#0#"+Integer.toHexString(nivel));
							}else {
								statsNew.append(temp[i]);
							}
						}else {
							int valor = Integer.parseInt(temp2[1], 16);
							int valor2 = Integer.parseInt(temp2[2], 16);
							if(!MainServidor.STATS_NO_FORTICABLES.contains(stats)) {
								valor+=(int)Math.ceil(valor*5*0.01);
								valor2+=(int)Math.ceil(valor2*5*0.01);
								statsNew.append(temp2[0]+"#"+Integer.toHexString(valor)+"#"+Integer.toHexString(valor2)+"#0#0d0+"+valor);
							}else{
								statsNew.append(temp[i]);// con esto lo pone tal cual como lo tenia
							}
						}
					}
					convertirStringAStats(statsNew.toString());
				}
				if(!statsMimo.isEmpty())addStatTexto(Constantes.STAT_APARIENCIA_OBJETO, "0#0#" + statsMimo);
				GestorSQL.SALVAR_OBJETO(this);
				System.out.println("Objeto Salvado Fortificado"+id+" Modelo: "+idObjModelo);
			}else{
				convertirStringAStats(_objModelo.generarStatsModelo(CAPACIDAD_STATS.MAXIMO));
				if(!statsMimo.isEmpty())addStatTexto(Constantes.STAT_APARIENCIA_OBJETO, "0#0#" + statsMimo);
				GestorSQL.SALVAR_OBJETO(this);
				System.out.println("Objeto Salvado "+id+" Modelo: "+idObjModelo);
			}
		}*/ else {
            convertirStringAStats(strStats)
        }
        crearEncarnacion()
    }

    val encarnacion: Encarnacion?
        get() = _encarnacion

    fun addDurabilidad(valor: Int): Boolean {
        durabilidad += valor
        if (durabilidad > durabilidadMax) {
            durabilidad = durabilidadMax
        }
        return if (durabilidad < 1) {
            true
        } else false
    }

    fun puedeTenerStatsIguales(): Boolean {
        // parece q es para q no se junten los items en 1 solo
        return if (_objModelo.getTipo() === Constantes.OBJETO_TIPO_PIEDRA_DE_ALMA_LLENA || _objModelo
                        .getTipo() === Constantes.OBJETO_TIPO_MASCOTA || _objModelo.getTipo() === Constantes.OBJETO_TIPO_FANTASMA_MASCOTA || Mundo.getCreaTuItem(_objModelo.getID()) != null || _encarnacion != null) {
            false
        } else true
    }

    fun sonStatsIguales(otro: Objeto): Boolean {
        if (MainServidor.PARAMS_ITEM_PILA.contains(otro.objModeloID)) {
            return true
        }
        if (durabilidad != otro.durabilidad || durabilidadMax != otro.durabilidadMax) {
            return false
        }
        if (objevivoID > 0 || otro.objevivoID > 0) {
            return false
        }
        if (!_statsGeneral.sonStatsIguales(otro.stats)) {
            return false
        }
        if (_efectosNormales == null && otro._efectosNormales == null) {
            // nada
        } else if (_efectosNormales == null && otro._efectosNormales != null || (_efectosNormales != null
                        && otro._efectosNormales == null) || _efectosNormales.isEmpty() && !otro._efectosNormales.isEmpty()
                || otro._efectosNormales.isEmpty() && !_efectosNormales.isEmpty()) {
            return false
        } else if (!_efectosNormales.isEmpty() && !otro._efectosNormales.isEmpty()) {
            val ePropios: ArrayList<String> = ArrayList()
            val eOtros: ArrayList<String> = ArrayList()
            for (eh in _efectosNormales) {
                ePropios.add(eh.getEfectoID().toString() + "," + eh.getArgs())
            }
            for (eh in otro._efectosNormales) {
                eOtros.add(eh.getEfectoID().toString() + "," + eh.getArgs())
            }
            for (eh in _efectosNormales) {
                val entry: String = eh.getEfectoID().toString() + "," + eh.getArgs()
                if (!eOtros.contains(entry)) {
                    return false
                } else {
                    eOtros.remove(entry)
                }
            }
            for (eh in otro._efectosNormales) {
                val entry: String = eh.getEfectoID().toString() + "," + eh.getArgs()
                if (!ePropios.contains(entry)) {
                    return false
                } else {
                    ePropios.remove(entry)
                }
            }
        }
        return true
    }

    fun setIDObjevivo(id: Int) {
        objevivoID = id
    }

    fun setIDOjbModelo(idObjModelo: Int) {
        if (Mundo.getObjetoModelo(idObjModelo) == null) {
            return
        }
        objModeloID = idObjModelo
        _objModelo = Mundo.getObjetoModelo(idObjModelo)
    }

    val objModelo: ObjetoModelo?
        get() = _objModelo
    val stats: Stats
        get() = _statsGeneral

    fun setPosicion(newPos: Byte, perso: Personaje?, refrescarStuff: Boolean) {
        if (posicion == newPos) {
            return
        }
        val oldPos = posicion
        posicion = newPos
        if (perso != null) {
            perso.cambiarPosObjeto(this, oldPos, newPos, refrescarStuff)
            if (!refrescarStuff && perso.enLinea()) {
                GestorSalida.ENVIAR_OM_MOVER_OBJETO(perso, this)
                GestorSalida.ENVIAR_wI_Idolo_Puntaje(perso, perso.getBonusIdolo())
            }
        }
    }

    val efectosNormales: ArrayList<EfectoHechizo>?
        get() = _efectosNormales
    val efectosCriticos: ArrayList<EfectoHechizo>?
        get() {
            if (_efectosNormales != null) {
                val efectos: ArrayList<EfectoHechizo> = ArrayList<EfectoHechizo>()
                for (EH in _efectosNormales) {
                    try {
                        if (EH.getEfectoID() === Constantes.STAT_MENOS_PA) {
                            efectos.add(EH)
                        } else {
                            val infos: Array<String> = EH.getArgs().split(",")
                            var dados = ""
                            var primerValor: Int = Integer.parseInt(infos[0])
                            var segundoValor: Int = Integer.parseInt(infos[1])
                            if (segundoValor <= 0) {
                                segundoValor = -1
                                primerValor += _objModelo.getBonusGC()
                                dados = "0d0+$primerValor"
                            } else {
                                primerValor += _objModelo.getBonusGC()
                                segundoValor += _objModelo.getBonusGC()
                                dados = "1d" + (segundoValor - primerValor) + "+" + (primerValor - 1)
                            }
                            val eh = EfectoHechizo(EH.getEfectoID(), primerValor.toString() + "," + segundoValor + ",-1,0,0,"
                                    + dados, 0, -1, Constantes.getZonaEfectoArma(_objModelo.getTipo()))
                            eh.setAfectados(2)
                            efectos.add(eh)
                        }
                    } catch (e: Exception) {
                    }
                }
                return efectos
            }
            return null
        }

    fun getStatValor(statID: Int): Int {
        return _statsGeneral.getStatParaMostrar(statID)
    }

    fun fijarStatValor(statID: Int, valor: Int) {
        _statsGeneral.fijarStatID(statID, valor)
    }

    fun addStatTexto(statID: Int, texto: String?) {
        _statsGeneral.addStatTexto(statID, texto, false)
    }

    fun tieneStatTexto(statID: Int): Boolean {
        return _statsGeneral.tieneStatTexto(statID)
    }

    fun tieneAlgunStatExo(): Boolean {
        for (entry in _statsGeneral.getEntrySet()) {
            if (esStatExo(entry.getKey())) {
                return true
            }
        }
        return false
    }

    fun tieneStatExo(statID: Int): Boolean {
        return if (!esStatExo(statID)) {
            false
        } else getStatValor(statID) != 0
    }

    fun esStatExo(statID: Int): Boolean {
        return if (!Constantes.esStatDePelea(statID)) {
            false
        } else !_objModelo.tieneStatInicial(statID)
        // siempre sera positivo
    }

    fun esStatOver(statID: Int, valor: Int): Boolean {
        if (!Constantes.esStatDePelea(statID)) {
            return false
        }
        val duo: Duo<Integer, Integer> = _objModelo.getDuoInicial(statID) ?: return false
        return valor > duo._segundo
    }

    fun cambiarStatllave(perso: Personaje?) {
        val stats: Array<String> = convertirStatsAString(true).split(",")
        val nuevo = StringBuilder()
        var b = false
        for (st in stats) {
            if (nuevo.length() > 0) {
                nuevo.append(",")
            }
            val statID: Int = Integer.parseInt(st.split("#").get(0), 16)
            if (statID == Constantes.STAT_LLAVE_MAZMORRA_OCULTO) {
                b = true
                val llave: String = st.split("#").get(1)
                val fecha: Long = Long.parseLong(st.split("#").get(3))
                val fechahoy: Long = Date().getTime()
                //32e#0#0#1fcf#0d0+0
                if (fecha <= fechahoy) {
                    nuevo.append("32e#0#0#$llave#0d0+0")
                } else {
                    nuevo.append(st)
                }
            } else {
                nuevo.append(st)
            }
        }
        if (b) {
            convertirStringAStats(nuevo.toString())
            GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(perso, this)
        }
    }

    fun convertirStringAStats(strStats: String) {
        var intercambiable = ""
        var nombre = ""
        if (tieneStatTexto(Constantes.STAT_INTERCAMBIABLE_DESDE)) {
            intercambiable = _statsGeneral.getStatTexto(Constantes.STAT_INTERCAMBIABLE_DESDE)
        }
        if (tieneStatTexto(Constantes.STAT_PERTENECE_Y_NO_VENDER)) {
            nombre = _statsGeneral.getStatTexto(Constantes.STAT_PERTENECE_Y_NO_VENDER)
        }
        _statsGeneral.clear()
        if (!intercambiable.equalsIgnoreCase("")) {
            addStatTexto(Constantes.STAT_INTERCAMBIABLE_DESDE, intercambiable)
        }
        if (!nombre.equalsIgnoreCase("")) {
            addStatTexto(Constantes.STAT_PERTENECE_Y_NO_VENDER, nombre)
        }
        _efectosNormales = null
        durabilidadMax = -1
        durabilidad = durabilidadMax
        for (str in strStats.split(",")) {
            if (str.isEmpty()) {
                continue
            }
            try {
                val stats: Array<String> = str.split("#")
                val statID: Int = ObjetoModelo.statSimiliar(Integer.parseInt(stats[0], 16))
                // si no es objevivo y tiene stats entre 970 y 974
                // if (_objModelo.getTipo() != Constantes.OBJETO_TIPO_OBJEVIVO && statID >= 970 && statID
                // <= 974) {
                // continue;
                // }
                // if (_idObjevivo > 0 && (statID == Constantes.STAT_RECIBIDO_EL || statID ==
                // Constantes.STAT_SE_HA_COMIDO_EL)) {
                // continue;
                // }
                /*if(MainServidor.PARAMS_BORRAR_STATS_COLOR && statID == Constantes.STAT_COLOR_NOMBRE_OBJETO) {
					continue;
				}*/if (Constantes.STAT_RECIBIDO_EL === statID) {
                    if (stats.size > 1 && stats[1].equals("0")) {
                        val actual: Calendar = Calendar.getInstance()
                        str = stats[0] + "#" + (Integer.toHexString(actual.get(Calendar.YEAR)).toString() + "#" + Integer.toHexString(actual
                                .get(Calendar.MONTH) * 100 + actual.get(Calendar.DAY_OF_MONTH)) + "#" + Integer.toHexString(actual.get(
                                Calendar.HOUR_OF_DAY) * 100 + actual.get(Calendar.MINUTE)))
                    }
                }
                if (Constantes.STAT_RESISTENCIA === statID) {
                    durabilidad = Integer.parseInt(stats[2], 16)
                    durabilidadMax = Integer.parseInt(stats[3], 16)
                } else if (Constantes.esStatHechizo(statID)) {
                    _statsGeneral.addStatHechizo(str)
                } else if (Constantes.esStatRepetible(statID)) {
                    _statsGeneral.addStatRepetido(str)
                } else if (Constantes.esStatTexto(statID)) {
                    _statsGeneral.addStatTexto(statID, str, true)
                } else if (Constantes.esEfectoHechizo(statID)) {
                    var dados = ""
                    val primerValor: Int = Integer.parseInt(stats[1], 16)
                    var segundoValor: Int = Integer.parseInt(stats[2], 16)
                    if (segundoValor <= 0) {
                        segundoValor = 0
                        dados = "0d0+$primerValor"
                    } else {
                        dados = "1d" + (segundoValor - primerValor) + "+" + (primerValor - 1)
                    }
                    val eh = EfectoHechizo(statID, "$primerValor,$segundoValor,-1,0,0,$dados", 0, -1,
                            Constantes.getZonaEfectoArma(_objModelo.getTipo()))
                    eh.setAfectados(2)
                    if (_efectosNormales == null) {
                        _efectosNormales = ArrayList()
                    }
                    _efectosNormales.add(eh)
                } else {
                    // int statPositivo = Constantes.getStatPositivoDeNegativo(statID);// +agi
                    var valor: Int = Integer.parseInt(stats[1], 16) // 100
                    // if (_objModelo.tieneStatInicial(statPositivo)) {//+agi100
                    // int cantStatInicial = _objModelo.getStatsIniciales().get(statPositivo)._primero;
                    // if (cantStatInicial < 0) {// si es stat inicial negativo
                    // // -agi -20
                    // int tempValor = valor;
                    // if (statPositivo != statID) {
                    // tempValor = -tempValor;
                    // }
                    // if (tempValor > 0) {
                    // continue;
                    // }
                    // }
                    // }
                    if (MainServidor.PARAM_NERFEAR_STATS_EXOTICOS) {
                        if (esStatExo(statID)) {
                            valor = 1
                        }
                        if (esStatOver(statID, valor)) {
                            val duo: Duo<Integer, Integer> = _objModelo.getDuoInicial(statID)
                            valor = if (duo != null) {
                                duo._segundo
                            } else {
                                0
                            }
                        }
                    }
                    _statsGeneral.addStatID(statID, valor)
                }
            } catch (e: Exception) {
                MainServidor.redactarLogServidorln("BUG OBJETO ID: " + iD + ", OBJMOD: " + objModeloID + ", STAT BUG: " + str
                        + ", STATS: " + strStats + ", STATS MODELO: " + _objModelo.getStatsModelo())
                e.printStackTrace()
            }
        }
        if (_efectosNormales != null) {
            _efectosNormales.trimToSize()
        }
        if (_encarnacion != null) {
            _encarnacion.refrescarStatsItem()
        }
    }

    private fun crearEncarnacion() {
        if (tieneStatTexto(Constantes.STAT_ENCARNACION_NIVEL)) {
            if (_encarnacion == null) {
                val encarID: Int = Integer.parseInt(getParamStatTexto(Constantes.STAT_ENCARNACION_NIVEL, 1), 16)
                val encarExp: Int = Integer.parseInt(getParamStatTexto(Constantes.STAT_ENCARNACION_NIVEL, 2), 16)
                val encarModelo: EncarnacionModelo = Mundo.getEncarnacionModelo(encarID)
                if (encarModelo != null) {
                    _encarnacion = Encarnacion(this, encarExp, encarModelo)
                }
            }
        }
    }

    fun convertirStatsAString(sinAdicionales: Boolean): String {
        val stats = StringBuilder()
        if (ObjetoModelo.getTipoConStatsModelo(_objModelo.getTipo())) {
            stats.append(_objModelo.getStatsModelo())
        } else {
            if (_encarnacion != null) {
                addStatTexto(Constantes.STAT_ENCARNACION_NIVEL, Integer.toHexString(_encarnacion.getGfxID()).toString() + "#" + Integer
                        .toHexString(_encarnacion.getExp()) + "#" + Integer.toHexString(_encarnacion.getNivel()))
            }
            if (_efectosNormales != null) {
                for (EH in _efectosNormales) {
                    if (stats.length() > 0) {
                        stats.append(",")
                    }
                    val infos: Array<String> = EH.getArgs().split(",")
                    try {
                        stats.append(Integer.toHexString(EH.getEfectoID()).toString() + "#" + Integer.toHexString(Integer.parseInt(infos[0]))
                                + "#" + Integer.toHexString(Integer.parseInt(infos[1])) + "#0#" + infos[5])
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            if (durabilidadMax > 0 && durabilidad > 0) {
                if (stats.length() > 0) {
                    stats.append(",")
                }
                stats.append(Integer.toHexString(Constantes.STAT_RESISTENCIA).toString() + "#0#" + Integer.toHexString(durabilidad) + "#"
                        + Integer.toHexString(durabilidadMax) + "#0d0+" + durabilidad)
            }
            val oStats: String = _statsGeneral.getStringStats(this)
            if (!oStats.isEmpty()) {
                if (stats.length() > 0) {
                    stats.append(",")
                }
                stats.append(oStats)
            }
            if (!sinAdicionales) {
                if (objevivoID > 0 && objevivoID != iD) {
                    val objevivo: Objeto = Mundo.getObjeto(objevivoID)
                    if (objevivo != null) {
                        if (stats.length() > 0) {
                            stats.append(",")
                        }
                        stats.append(objevivo.convertirStatsAString(false))
                    }
                } else {
                    objevivoID = 0
                }
            }
        }
        // fuera de for
        /*if (!MainServidor.PARAMS_BORRAR_STATS_COLOR && _objModelo.getOgrinas() > 0 && Mundo.getCreaTuItem(_objModelo.getID()) == null) {
			if (stats.length() > 0) {
				stats.append(",");
			}
			stats.append(Integer.toHexString(Constantes.STAT_COLOR_NOMBRE_OBJETO) + "#1");
		}*/return stats.toString()
    }

    fun strGrupoMob(): String {
        if (_statsGeneral.getStatRepetidos() == null) {
            return ""
        }
        val stats = StringBuilder()
        for (str in _statsGeneral.getStatRepetidos()) {
            try {
                val s: Array<String> = str.split("#")
                if (Integer.parseInt(s[0], 16) !== Constantes.STAT_INVOCA_MOB && Integer.parseInt(s[0],
                                16) !== Constantes.STAT_INVOCA_MOB_2) {
                    continue
                }
                if (stats.length() > 0) {
                    stats.append(";")
                }
                stats.append(Integer.parseInt(s[3], 16).toString() + "," + Integer.parseInt(s[1], 16) + "," + Integer.parseInt(s[1], 16))
            } catch (e: Exception) {
            }
        }
        return stats.toString()
    }

    fun strGrupoMobJefe(): Int {
        if (_statsGeneral.getStatRepetidos() == null) {
            return 0
        }
        for (str in _statsGeneral.getStatRepetidos()) {
            try {
                val s: Array<String> = str.split("#")
                if (Integer.parseInt(s[0], 16) !== Constantes.STAT_INVOCA_MOB && Integer.parseInt(s[0],
                                16) !== Constantes.STAT_INVOCA_MOB_2) {
                    continue
                }
                val jefe: Int = Integer.parseInt(s[3], 16)
                if (MainServidor.MOBS_TRAMPOSOS.contains(jefe)) {
                    return jefe
                }
            } catch (e: Exception) {
            }
        }
        return 0
    }

    fun strDarObjetos(): String {
        if (_statsGeneral.getStatRepetidos() == null) {
            return ""
        }
        val stats = StringBuilder()
        for (str in _statsGeneral.getStatRepetidos()) {
            try {
                val s: Array<String> = str.split("#")
                if (Integer.parseInt(s[0], 16) !== Constantes.STAT_DAR_OBJETO) continue
                if (stats.length() > 0) {
                    stats.append(";")
                }
                stats.append(Integer.parseInt(s[1], 16).toString() + "," + Integer.parseInt(s[2], 16))
            } catch (e: Exception) {
            }
        }
        return stats.toString()
    }

    fun pasoIntercambiableDesde(): Boolean {
        if (tieneStatTexto(Constantes.STAT_INTERCAMBIABLE_DESDE)) {
            if (getDiferenciaTiempo(Constantes.STAT_INTERCAMBIABLE_DESDE, 60 * 1000) >= 0) {
                addStatTexto(Constantes.STAT_INTERCAMBIABLE_DESDE, "")
            } else {
                return false
            }
        }
        return true
    }

    fun getParamStatTexto(stat: Int, parametro: Int): String {
        return _statsGeneral.getParamStatTexto(stat, parametro)
    }

    fun comerComida(idModComida: Int) {
        var nroComidas = 0
        val mascModelo: MascotaModelo = Mundo.getMascotaModelo(objModeloID) ?: return
        try {
            if (tieneStatTexto(Constantes.STAT_NUMERO_COMIDAS)) {
                nroComidas = Integer.parseInt(getParamStatTexto(Constantes.STAT_NUMERO_COMIDAS, 1), 16)
            }
        } catch (e: Exception) {
        }
        nroComidas++
        addStatTexto(Constantes.STAT_ULTIMA_COMIDA, "0#0#" + Integer.toHexString(idModComida))
        if (nroComidas == 3) {
            nroComidas = 0
            val comida: Comida = mascModelo.getComida(idModComida) ?: return
            val efecto: Int = comida.getIDStat()
            var maximo = 0
            var maxPorStat = 0
            for (entry in _statsGeneral.getEntrySet()) {
                val statID: Int = entry.getKey()
                var factor: Byte = 1
                when (statID) {
                    Constantes.STAT_MAS_RES_PORC_AGUA, Constantes.STAT_MAS_RES_PORC_TIERRA, Constantes.STAT_MAS_RES_PORC_AIRE, Constantes.STAT_MAS_RES_PORC_NEUTRAL, Constantes.STAT_MAS_RES_PORC_FUEGO -> factor = 6
                }
                maximo += entry.getValue() * factor
                if (statID == efecto) {
                    maxPorStat = entry.getValue() * factor
                }
            }
            if (maximo >= mascModelo.getMaxStats() || maxPorStat >= mascModelo.getStatsPorEfecto(efecto)) {
                if (maximo < mascModelo.getMaxStatsMejorado() && maxPorStat < mascModelo.getStatsPorEfectoMejorado(efecto) && tieneStatTexto(Constantes.STAT_CAPACIDADES_MEJORADAS)) {
                    if (efecto == Constantes.STAT_MAS_INICIATIVA || efecto == Constantes.STAT_MAS_PODS) {
                        _statsGeneral.addStatID(efecto, 10)
                    } else {
                        _statsGeneral.addStatID(efecto, 1)
                    }
                }
            } else {
                if (efecto == Constantes.STAT_MAS_INICIATIVA || efecto == Constantes.STAT_MAS_PODS) {
                    _statsGeneral.addStatID(efecto, 10)
                } else {
                    _statsGeneral.addStatID(efecto, 1)
                }
            }
        }
        addStatTexto(Constantes.STAT_NUMERO_COMIDAS, nroComidas.toString() + "")
    }

    fun comerAlma(idMobModelo: Int, cantAlmasDevor: Int) {
        val mascModelo: MascotaModelo = Mundo.getMascotaModelo(objModeloID) ?: return
        val comida: Comida = mascModelo.getComida(idMobModelo) ?: return
        var valorTemp = 0
        var index = -1
        val efecto: Int = comida.getIDStat()
        var maximo = 0
        var maxPorStat = 0
        if (_statsGeneral.getStatRepetidos() != null) {
            for (stati in _statsGeneral.getStatRepetidos()) {
                try {
                    val x: Array<String> = stati.split("#")
                    if (Integer.parseInt(x[0], 16) !== Constantes.STAT_NOMBRE_MOB) {
                        continue
                    }
                    val i: Int = Integer.parseInt(x[1], 16)
                    val c: Int = Integer.parseInt(x[3], 16)
                    if (i == idMobModelo) {
                        valorTemp = c
                        index = _statsGeneral.getStatRepetidos().indexOf(stati)
                    }
                } catch (e: Exception) {
                }
            }
            if (index > -1) {
                _statsGeneral.getStatRepetidos().remove(index)
            }
        }
        _statsGeneral.addStatRepetido(Integer.toHexString(Constantes.STAT_NOMBRE_MOB).toString() + "#" + Integer.toHexString(
                idMobModelo) + "#0#" + Integer.toHexString(valorTemp + cantAlmasDevor) + "#0")
        for (entry in _statsGeneral.getEntrySet()) {
            val statID: Int = entry.getKey()
            var por: Byte = 1
            when (statID) {
                Constantes.STAT_MAS_RES_PORC_TIERRA, Constantes.STAT_MAS_RES_PORC_AGUA, Constantes.STAT_MAS_RES_PORC_AIRE, Constantes.STAT_MAS_RES_PORC_FUEGO, Constantes.STAT_MAS_RES_PORC_NEUTRAL -> por = 6
            }
            maximo += entry.getValue() * por
            if (statID == efecto) {
                maxPorStat = entry.getValue() * por
            }
        }
        if (maximo >= mascModelo.getMaxStats() || maxPorStat >= mascModelo.getStatsPorEfecto(efecto)) {
            if (maximo < mascModelo.getMaxStatsMejorado() && maxPorStat < mascModelo.getStatsPorEfectoMejorado(efecto) && tieneStatTexto(Constantes.STAT_CAPACIDADES_MEJORADAS)) {
                if ((valorTemp + cantAlmasDevor) / comida.getCantidad() > valorTemp / comida.getCantidad()) {
                    if (efecto == Constantes.STAT_MAS_INICIATIVA || efecto == Constantes.STAT_MAS_PODS) {
                        _statsGeneral.addStatID(efecto, 10)
                    } else {
                        _statsGeneral.addStatID(efecto, 1)
                    }
                }
            }
        } else {
            if ((valorTemp + cantAlmasDevor) / comida.getCantidad() > valorTemp / comida.getCantidad()) {
                if (efecto == Constantes.STAT_MAS_INICIATIVA || efecto == Constantes.STAT_MAS_PODS) {
                    _statsGeneral.addStatID(efecto, 10)
                } else {
                    _statsGeneral.addStatID(efecto, 1)
                }
            }
        }
    }

    fun getDiferenciaTiempo(stat: Int, escala: Int): Long {
        val tiempoActual: Long = Constantes.getTiempoActualEscala(escala)
        val tiempoDif: Long = Constantes.getTiempoDeUnStat(_statsGeneral.getStatTexto(stat), escala)
        return tiempoActual - tiempoDif
    }

    fun horaComer(forzado: Boolean, corpulencia: Int): Boolean {
        val mascota: MascotaModelo = Mundo.getMascotaModelo(objModeloID)
        if (forzado || getDiferenciaTiempo(Constantes.STAT_SE_HA_COMIDO_EL, 60
                        * 1000) >= mascota.getTiempoAlimentacion()) {
            addStatTexto(Constantes.STAT_SE_HA_COMIDO_EL, ObjetoModelo.getStatSegunFecha(Calendar.getInstance()))
            corpulencia = corpulencia
            return true
        }
        return false
    }

    var pDV: Int
        get() = if (!tieneStatTexto(Constantes.STAT_PUNTOS_VIDA)) {
            -1
        } else Integer.parseInt(getParamStatTexto(Constantes.STAT_PUNTOS_VIDA, 3), 16)
        set(pdv) {
            addStatTexto(Constantes.STAT_PUNTOS_VIDA, "0#0#" + Integer.toHexString(pdv))
        }
    var corpulencia: Int
        get() {
            if (!tieneStatTexto(Constantes.STAT_CORPULENCIA)) {
                return -1
            }
            if (getParamStatTexto(Constantes.STAT_CORPULENCIA, 3).equals("7")) {
                return Constantes.CORPULENCIA_DELGADO
            }
            return if (getParamStatTexto(Constantes.STAT_CORPULENCIA, 2).equals("7")) {
                Constantes.CORPULENCIA_OBESO
            } else Constantes.CORPULENCIA_NORMAL
        }
        set(numero) {
            when (numero) {
                Constantes.CORPULENCIA_OBESO -> addStatTexto(Constantes.STAT_CORPULENCIA, "0#7#0")
                Constantes.CORPULENCIA_DELGADO -> addStatTexto(Constantes.STAT_CORPULENCIA, "0#0#7")
                Constantes.CORPULENCIA_NORMAL -> addStatTexto(Constantes.STAT_CORPULENCIA, "0#0#0")
            }
        }

    fun esDevoradorAlmas(): Boolean {
        return try {
            Mundo.getMascotaModelo(objModeloID).esDevoradorAlmas()
        } catch (e: Exception) {
            false
        }
    }

    val dañoPromedioNeutral: Int
        get() {
            if (_efectosNormales != null) {
                for (EH in _efectosNormales) {
                    try {
                        if (EH.getEfectoID() !== Constantes.STAT_DAÑOS_NEUTRAL) {
                            continue
                        }
                        val infos: Array<String> = EH.getArgs().split(",")
                        return (Integer.parseInt(infos[1], 16) + Integer.parseInt(infos[0], 16)) / 2
                    } catch (e: Exception) {
                    }
                }
            }
            return 1
        }

    fun forjaMagiaGanar(statID: Int, potencia: Int) {
        when (statID) {
            96, 97, 98, 99 -> if (_efectosNormales != null) {
                for (EH in _efectosNormales) {
                    if (EH.getEfectoID() !== Constantes.STAT_DAÑOS_NEUTRAL) {
                        continue
                    }
                    val infos: Array<String> = EH.getArgs().split(",")
                    try {
                        val min: Int = Integer.parseInt(infos[0])
                        val max: Int = Integer.parseInt(infos[1])
                        var nuevoMin = (Math.floor((min - 1) * (potencia / 100f)) + 1) as Int // 50 y 78
                        val nuevoMax = (Math.floor((min - 1) * (potencia / 100f)) + Math.floor((max - min + 1)
                                * (potencia / 100f))) as Int
                        if (MainServidor.MODO_DEBUG) {
                            System.out.println("min $min")
                            System.out.println("max $max")
                            System.out.println("nuevoMin $nuevoMin")
                            System.out.println("nuevoMax $nuevoMax")
                        }
                        if (nuevoMin == 0) {
                            nuevoMin = 1
                        }
                        val nuevosArgs = (nuevoMin.toString() + "," + nuevoMax + ",-1,0,0," + "1d" + (nuevoMax - nuevoMin + 1) + "+"
                                + (nuevoMin - 1))
                        if (MainServidor.MODO_DEBUG) {
                            System.out.println("Nuevo Args FM elemental $nuevosArgs")
                        }
                        EH.setArgs(nuevosArgs)
                        EH.setEfectoID(statID)
                        return
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            else -> _statsGeneral.addStatID(statID, potencia)
        }
    }

    fun runasRomperObjeto(runas: Map<Integer?, Long?>, cantObjeto: Long) {
        for (i in 1..cantObjeto) {
            for (s in convertirStatsAString(true).split(",")) {
                try {
                    if (s.isEmpty()) {
                        continue
                    }
                    val stats: Array<String> = s.split("#")
                    val statID: Int = Integer.parseInt(stats[0], 16)
                    val statPositivo: Int = Constantes.getStatPositivoDeNegativo(statID)
                    var valor: Int = Integer.parseInt(stats[1], 16)
                    if (statID != statPositivo) {
                        valor = -valor
                    }
                    if (valor < 1) {
                        continue
                    }
                    if (Constantes.getRunaPorStat(statID, 1) === 0) {
                        continue
                    }
                    val pesoIndividual: Float = Constantes.getPesoStat(statID)
                    var pesoStat = pesoIndividual * valor
                    while (pesoStat > 0) {
                        var tipoRuna: Int = Constantes.getTipoRuna(statID, pesoStat)
                        if (tipoRuna == 0) {
                            break
                        }
                        val v: IntArray = Constantes.getPotenciaRunaPorStat(statID)
                        val jet: Float = Formulas.getRandomDecimal(3)
                        val prob = Constantes.getPorcCrearRuna(statID, pesoStat, tipoRuna, _objModelo.getNivel()) as Int
                        tipoRuna--
                        var red = 0f
                        if (prob >= jet) {
                            val exTipoRuna = tipoRuna
                            red += v[tipoRuna] * pesoIndividual * 3
                            // tipoRuna = Formulas.getRandomInt(0, tipoRuna);//aqui cambia la runa por mas o menos
                            val runa: Int = Constantes.getRunaPorStat(statID, tipoRuna + 1)
                            // if (!MainServidor.RUNAS_NO_PERMITIDAS.contains(runa)) {
                            var cant: Long = Math.pow(2, exTipoRuna - tipoRuna) as Int.toLong()
                            if (runas[runa] != null) {
                                cant += runas[runa]!!
                            }
                            runas.put(runa, cant)
                            // }
                        }
                        red += v[tipoRuna] * pesoIndividual
                        red = Math.ceil(red / MainServidor.FACTOR_OBTENER_RUNAS)
                        pesoStat -= red
                    }
                } catch (e: Exception) {
                }
            }
        }
    }

    fun forjaMagiaPerder(statMaguear: Int, potencia: Int, afectarStatMagueo: Boolean) {
        var pozoResidual = getStatValor(Constantes.STAT_POZO_RESIDUAL)
        var pesoRunaRestar = Math.ceil(Constantes.getPesoStat(statMaguear) * potencia) as Int
        if (MainServidor.MODO_DEBUG) {
            System.out.println("------------- PERDIENDO STATS FM -------------------")
            System.out.println("pozoResidual: $pozoResidual")
            System.out.println("pesoRuna: $pesoRunaRestar")
        }
        val pesoOrigRuna = pesoRunaRestar
        if (pesoRunaRestar > 0) {
            // si sobro peso a restar, se tiene q disminuir
            val statsCheckeados: ArrayList<Integer> = ArrayList()
            if (!afectarStatMagueo) {
                statsCheckeados.add(statMaguear)
            }
            while (pesoRunaRestar > 0) {
                var statPerder = getStatElegidoAPerder(pesoOrigRuna, statMaguear, statsCheckeados)
                if (MainServidor.MODO_DEBUG) {
                    System.out.println("SE ESCOGIO A PERDER STATID $statPerder")
                }
                if (statPerder == 0) {
                    break
                }
                var overExo: Int = Trabajo.MAGUEO_NORMAL
                if (statPerder > 2000) {
                    statPerder -= 2000
                    overExo = Trabajo.MAGUEO_EXO
                } else if (statPerder > 1000) {
                    statPerder -= 1000
                    overExo = Trabajo.MAGUEO_OVER
                }
                if (overExo == Trabajo.MAGUEO_NORMAL) {
                    if (pozoResidual > 0) {
                        pesoRunaRestar -= pozoResidual // 100 .... 35 - 52 = -17
                        pozoResidual = 0
                        continue
                    }
                }
                statsCheckeados.add(statPerder)
                val pesoRunaPerder: Float = Constantes.getPesoStat(statPerder)
                if (pesoRunaPerder == 0f) {
                    continue
                }
                val cantStatPerder = getStatValor(statPerder)
                // lo mas cercano a positivo, minimo es 1
                val cantDebePerder = Math.ceil(pesoRunaRestar / pesoRunaPerder) as Int
                val maxPerder: Int = Math.min(cantStatPerder, cantDebePerder)
                if (MainServidor.MODO_DEBUG) {
                    System.out.println("statPerder " + statPerder + " cantStatPerder " + cantStatPerder + " pesoRunaRestar "
                            + pesoRunaRestar + " cantDebePerder " + cantDebePerder + " maxPerder " + maxPerder)
                }
                if (maxPerder <= 0) {
                    continue
                }
                var random = maxPerder
                random = when (overExo) {
                    Trabajo.MAGUEO_OVER, Trabajo.MAGUEO_EXO -> Formulas.getRandomInt(1, cantStatPerder)
                    else -> if (pesoRunaRestar == 1) {
                        1
                    } else {
                        Formulas.getRandomInt(1, maxPerder)
                    }
                }
                _statsGeneral.addStatID(statPerder, -random)
                val pesoPerder = (random * pesoRunaPerder).toInt()
                pesoRunaRestar -= pesoPerder
            }
        }
        if (pesoRunaRestar > 0) {
            pesoRunaRestar = 0
        }
        _statsGeneral.fijarStatID(Constantes.STAT_POZO_RESIDUAL, Math.abs(pesoRunaRestar))
    }

    private fun getStatElegidoAPerder(pesoOrigRuna: Int, statRuna: Int,
                                      statsCheckeados: ArrayList<Integer>): Int {
        val listaStats: ArrayList<Integer> = ArrayList<Integer>()
        for (entry in _statsGeneral.getEntrySet()) {
            val statID: Int = entry.getKey()
            val valor: Int = entry.getValue()
            if (MainServidor.MODO_DEBUG) {
                System.out.println("Se intenta perder el stat $statID valor $valor")
            }
            if (Constantes.getStatPositivoDeNegativo(statID) !== statID) {
                // si es negativo no se le borrara
                if (MainServidor.MODO_DEBUG) {
                    System.out.println("-- Cancel 1")
                }
                continue
            }
            if (!Constantes.esStatDePelea(statID)) {
                if (MainServidor.MODO_DEBUG) {
                    System.out.println("-- Cancel 2")
                }
                continue
            }
            if (statsCheckeados.contains(statID)) {
                if (MainServidor.MODO_DEBUG) {
                    System.out.println("-- Cancel 3")
                }
                continue
            }
            if (esStatOver(statID, valor)) {
                // si el stat es OVER
                if (statID != statRuna) {
                    return statID + 1000
                }
            } else if (esStatExo(statID)) {
                // si el stat es EXO retorna primero
                if (statID != statRuna) {
                    return statID + 2000
                }
            }
            listaStats.add(statID)
        }
        while (!listaStats.isEmpty()) {
            // if (listaStats.size() == statsCheckeados.size()) {
            // statsCheckeados.clear();
            // }
            val statID: Int = listaStats.get(Formulas.getRandomInt(0, listaStats.size() - 1))
            listaStats.remove(statID as Object)
            val pesoRunaPerder: Float = Constantes.getPesoStat(statID)
            // si es 3 = sabiduria, pero tiene 10 de sab y el otro es 10 agilidad
            if (MainServidor.MODO_DEBUG) {
                System.out.println("-> Escoger statID $statID pesoRuna $pesoRunaPerder pesoOrig $pesoOrigRuna")
            }
            if (pesoRunaPerder > pesoOrigRuna) {
                val suerte: Int = Formulas.getRandomInt(1, 101)
                if (suerte <= pesoOrigRuna * 100 / pesoRunaPerder) {
                    return statID
                }
            } else {
                return statID
            }
        }
        return 0
    }

    fun stringObjetoConGuiño(): String {
        val str = StringBuilder()
        try {
            str.append(Integer.toHexString(iD).toString() + "~" + Integer.toHexString(objModeloID) + "~" + Long.toHexString(
                    cantidad) + "~" + (if (posicion == Constantes.OBJETO_POS_NO_EQUIPADO) "" else Integer.toHexString(posicion)) + "~"
                    + convertirStatsAString(false) + "~" + _objModelo.getKamas() / 10)
            str.append(";")
        } catch (e: Exception) {
            MainServidor.redactarLogServidorln("OBJETO BUG stringObjetoConGuiño " + iD + " Exception: " + e.toString())
        }
        return str.toString()
    }

    fun stringObjetoConPalo(cantidad: Long): String {
        val str = StringBuilder()
        try {
            str.append(iD.toString() + "|" + cantidad + "|" + objModeloID + "|" + convertirStatsAString(false))
        } catch (e: Exception) {
            MainServidor.redactarLogServidorln("OBJETO BUG stringObjetoConPalo " + iD + " Exception: " + e.toString())
        }
        return str.toString()
    }

    fun convertirPerfecto(cantMod: Int): Boolean {
        return _objModelo.convertirStatsPerfecto(cantMod, _statsGeneral)
    }

    val statsModelo: String
        get() = _objModelo.getStatsModelo()

    @Synchronized
    fun clonarObjeto(cantidad: Long, pos: Byte): Objeto {
        var cantidad = cantidad
        if (cantidad < 1) {
            cantidad = 1
        }
        return Objeto(0, objModeloID, cantidad, pos, convertirStatsAString(true), 0, 0)
    }
}