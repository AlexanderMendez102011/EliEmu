package variables.stats

import estaticos.Constantes
import estaticos.MainServidor
import variables.pelea.Pelea
import variables.personaje.Personaje

class TotalStats(statsBase: Stats?, statsObjEquipados: Stats?, statsBuff: Stats?, statsBendMald: Stats?, tipo: Int) {
    private val _statsBase: Stats?
    private val _statsObjetos: Stats?
    private val _statsBuff: Stats?
    private val _statsBendMald: Stats?
    private val _tipo: Int
    val statsBase: Stats?
        get() = _statsBase
    val statsObjetos: Stats?
        get() = _statsObjetos
    val statsBuff: Stats?
        get() = _statsBuff
    val statsBendMald: Stats?
        get() = _statsBendMald

    fun clear() {
        if (_statsBase != null) {
            _statsBase.clear()
        }
        if (_statsBendMald != null) {
            _statsBendMald.clear()
        }
        if (_statsObjetos != null) {
            _statsObjetos.clear()
        }
        if (_statsBuff != null) {
            _statsBuff.clear()
        }
    }

    //	private boolean tieneStatID(int statID) {
    //		boolean b = false;
    //		if (_statsBase != null) {
    //			b |= _statsBase.tieneStatID(statID);
    //		}
    //		if (_statsBendMald != null) {
    //			b |= _statsBendMald.tieneStatID(statID);
    //		}
    //		if (_statsObjetos != null) {
    //			b |= _statsObjetos.tieneStatID(statID);
    //		}
    //		if (_statsBuff != null) {
    //			b |= _statsBuff.tieneStatID(statID);
    //		} 
    //		return b;
    //	}
    // aqui se aplican los limites
    fun getTotalStatConComplemento(statID: Int, perso: Pelea?, persoX: Personaje?): Int {
        val valores2: IntArray
        var divisor = 1
        when (statID) {
            Constantes.STAT_MAS_RES_FIJA_PVP_TIERRA -> valores2 = getArrayStats(Constantes.STAT_MAS_RES_FIJA_TIERRA, perso)
            Constantes.STAT_MAS_RES_FIJA_PVP_AGUA -> valores2 = getArrayStats(Constantes.STAT_MAS_RES_FIJA_AGUA, perso)
            Constantes.STAT_MAS_RES_FIJA_PVP_AIRE -> valores2 = getArrayStats(Constantes.STAT_MAS_RES_FIJA_AIRE, perso)
            Constantes.STAT_MAS_RES_FIJA_PVP_FUEGO -> valores2 = getArrayStats(Constantes.STAT_MAS_RES_FIJA_FUEGO, perso)
            Constantes.STAT_MAS_RES_FIJA_PVP_NEUTRAL -> valores2 = getArrayStats(Constantes.STAT_MAS_RES_FIJA_NEUTRAL, perso)
            Constantes.STAT_MAS_RES_PORC_PVP_TIERRA -> valores2 = getArrayStats(Constantes.STAT_MAS_RES_PORC_TIERRA, perso)
            Constantes.STAT_MAS_RES_PORC_PVP_AGUA -> valores2 = getArrayStats(Constantes.STAT_MAS_RES_PORC_AGUA, perso)
            Constantes.STAT_MAS_RES_PORC_PVP_AIRE -> valores2 = getArrayStats(Constantes.STAT_MAS_RES_PORC_AIRE, perso)
            Constantes.STAT_MAS_RES_PORC_PVP_FUEGO -> valores2 = getArrayStats(Constantes.STAT_MAS_RES_PORC_FUEGO, perso)
            Constantes.STAT_MAS_RES_PORC_PVP_NEUTRAL -> valores2 = getArrayStats(Constantes.STAT_MAS_RES_PORC_NEUTRAL, perso)
            Constantes.STAT_MAS_ESQUIVA_PERD_PA -> {
                valores2 = getArrayStats(Constantes.STAT_MAS_SABIDURIA, perso)
                divisor = 4
            }
            Constantes.STAT_MAS_ESQUIVA_PERD_PM -> {
                valores2 = getArrayStats(Constantes.STAT_MAS_SABIDURIA, perso)
                divisor = 4
            }
            Constantes.STAT_MAS_PROSPECCION -> {
                valores2 = getArrayStats(Constantes.STAT_MAS_SUERTE, perso)
                divisor = 10
            }
            Constantes.STAT_MAS_HUIDA -> {
                valores2 = getArrayStats(Constantes.STAT_MAS_AGILIDAD, perso)
                divisor = 10
            }
            Constantes.STAT_MAS_PLACAJE -> {
                valores2 = getArrayStats(Constantes.STAT_MAS_AGILIDAD, perso)
                divisor = 10
            }
            else -> return getTotalStatParaMostrar(statID, perso, persoX)
        }
        var valor = 0
        if (_statsBase != null) {
            valor += _statsBase.getStatParaMostrar(statID)
            valor += valores2[0] / divisor
        }
        if (_statsBendMald != null) {
            valor += _statsBendMald.getStatParaMostrar(statID)
            valor += valores2[1] / divisor
        }
        if (_statsObjetos != null) {
            valor += _statsObjetos.getStatParaMostrar(statID)
            valor += valores2[2] / divisor
        }
        var limitSin = 0
        if (_tipo == 1) {
            if (MainServidor.LIMITE_STATS_PVP_PERSONALIZADOS && perso != null && perso.getTipoPelea() === Constantes.PELEA_TIPO_PVP) {
                if (MainServidor.LIMITE_STATS_PVP.containsKey(statID)) {
                    limitSin = MainServidor.LIMITE_STATS_PVP.get(statID)
                    valor = Math.min(valor, limitSin)
                } else if (MainServidor.LIMITE_STATS_SIN_BUFF.get(statID) != null) {
                    limitSin = MainServidor.LIMITE_STATS_SIN_BUFF.get(statID)
                    valor = Math.min(valor, limitSin)
                }
            } else if (MainServidor.LIMITE_STATS_PERCO_PERSONALIZADOS && perso != null && perso.getTipoPelea() === Constantes.PELEA_TIPO_RECAUDADOR) {
                if (MainServidor.LIMITE_STATS_PERCO.containsKey(statID)) {
                    limitSin = MainServidor.LIMITE_STATS_PERCO.get(statID)
                    valor = Math.min(valor, limitSin)
                } else if (MainServidor.LIMITE_STATS_SIN_BUFF.get(statID) != null) {
                    limitSin = MainServidor.LIMITE_STATS_SIN_BUFF.get(statID)
                    valor = Math.min(valor, limitSin)
                }
            } else if (MainServidor.LIMITE_STATS_KOLI_PERSONALIZADOS && perso != null && perso.getTipoPelea() === Constantes.PELEA_TIPO_KOLISEO) {
                if (MainServidor.LIMITE_STATS_KOLISEO.containsKey(statID)) {
                    limitSin = MainServidor.LIMITE_STATS_KOLISEO.get(statID)
                    valor = Math.min(valor, limitSin)
                } else if (MainServidor.LIMITE_STATS_SIN_BUFF.get(statID) != null) {
                    limitSin = MainServidor.LIMITE_STATS_SIN_BUFF.get(statID)
                    valor = Math.min(valor, limitSin)
                }
            } else {
                if (MainServidor.LIMITE_STATS_SIN_BUFF.get(statID) != null) {
                    limitSin = MainServidor.LIMITE_STATS_SIN_BUFF.get(statID)
                    valor = Math.min(valor, limitSin)
                }
            }
        }
        if (_statsBuff != null) {
            val v: Int = _statsBuff.getStatParaMostrar(statID)
            valor += v
            valor += valores2[3] / divisor
        }
        if (_tipo == 1) {
            if (MainServidor.LIMITE_STATS_PVP_PERSONALIZADOS && perso != null && perso.getTipoPelea() === Constantes.PELEA_TIPO_PVP) {
                if (MainServidor.LIMITE_STATS_CON_BUFF_PVP.containsKey(statID)) {
                    limitSin = MainServidor.LIMITE_STATS_CON_BUFF_PVP.get(statID)
                    valor = Math.min(valor, limitSin)
                } else if (MainServidor.LIMITE_STATS_CON_BUFF.get(statID) != null) {
                    limitSin = MainServidor.LIMITE_STATS_CON_BUFF.get(statID)
                    valor = Math.min(valor, limitSin)
                }
            } else if (MainServidor.LIMITE_STATS_CON_BUFF.get(statID) != null) {
                val limitCon: Int = Math.max(limitSin, MainServidor.LIMITE_STATS_CON_BUFF.get(statID))
                valor = Math.min(valor, limitCon)
            }
        } else if (_tipo == 2) {
            if (MainServidor.LIMITE_STATS_PVP_PERSONALIZADOS && perso != null && perso.getTipoPelea() === Constantes.PELEA_TIPO_PVP) {
                if (MainServidor.LIMITE_STATS_CON_BUFF_PVP_INVOCACION.containsKey(statID)) {
                    limitSin = MainServidor.LIMITE_STATS_CON_BUFF_PVP_INVOCACION.get(statID)
                    valor = Math.min(valor, limitSin)
                }
            }
        }
        return valor
    }

    private fun getArrayStats(statID: Int, perso: Pelea?): IntArray {
        val valores = IntArray(5)
        var valor = 0
        if (_statsBase != null) {
            valores[0] = _statsBase.getStatParaMostrar(statID)
            valor += valores[0]
        }
        if (_statsBendMald != null) {
            valores[1] += _statsBendMald.getStatParaMostrar(statID)
            valor += valores[1]
        }
        if (_statsObjetos != null) {
            valores[2] += _statsObjetos.getStatParaMostrar(statID)
            valor += valores[2]
        }
        var limitSin = 0
        if (_tipo == 1) {
            if (MainServidor.LIMITE_STATS_PVP_PERSONALIZADOS && perso != null && perso.getTipoPelea() === Constantes.PELEA_TIPO_PVP) {
                if (MainServidor.LIMITE_STATS_PVP.containsKey(statID)) {
                    limitSin = MainServidor.LIMITE_STATS_PVP.get(statID)
                    valor = Math.min(valor, limitSin)
                } else if (MainServidor.LIMITE_STATS_SIN_BUFF.get(statID) != null) {
                    limitSin = MainServidor.LIMITE_STATS_SIN_BUFF.get(statID)
                    valor = Math.min(valor, limitSin)
                }
            } else if (MainServidor.LIMITE_STATS_PERCO_PERSONALIZADOS && perso != null && perso.getTipoPelea() === Constantes.PELEA_TIPO_RECAUDADOR) {
                if (MainServidor.LIMITE_STATS_PERCO.containsKey(statID)) {
                    limitSin = MainServidor.LIMITE_STATS_PERCO.get(statID)
                    valor = Math.min(valor, limitSin)
                } else if (MainServidor.LIMITE_STATS_SIN_BUFF.get(statID) != null) {
                    limitSin = MainServidor.LIMITE_STATS_SIN_BUFF.get(statID)
                    valor = Math.min(valor, limitSin)
                }
            } else if (MainServidor.LIMITE_STATS_KOLI_PERSONALIZADOS && perso != null && perso.getTipoPelea() === Constantes.PELEA_TIPO_KOLISEO) {
                if (MainServidor.LIMITE_STATS_KOLISEO.containsKey(statID)) {
                    limitSin = MainServidor.LIMITE_STATS_KOLISEO.get(statID)
                    valor = Math.min(valor, limitSin)
                } else if (MainServidor.LIMITE_STATS_SIN_BUFF.get(statID) != null) {
                    limitSin = MainServidor.LIMITE_STATS_SIN_BUFF.get(statID)
                    valor = Math.min(valor, limitSin)
                }
            } else {
                if (MainServidor.LIMITE_STATS_SIN_BUFF.get(statID) != null) {
                    limitSin = MainServidor.LIMITE_STATS_SIN_BUFF.get(statID)
                    valor = Math.min(valor, limitSin)
                }
            }
        }
        if (_statsBuff != null) {
            valores[3] += _statsBuff.getStatParaMostrar(statID)
            valor += valores[3]
        }
        if (_tipo == 1) {
            if (MainServidor.LIMITE_STATS_PVP_PERSONALIZADOS && perso != null && perso.getTipoPelea() === Constantes.PELEA_TIPO_PVP) {
                if (MainServidor.LIMITE_STATS_CON_BUFF_PVP.containsKey(statID)) {
                    limitSin = MainServidor.LIMITE_STATS_CON_BUFF_PVP.get(statID)
                    valor = Math.min(valor, limitSin)
                } else if (MainServidor.LIMITE_STATS_CON_BUFF.get(statID) != null) {
                    limitSin = MainServidor.LIMITE_STATS_CON_BUFF.get(statID)
                    valor = Math.min(valor, limitSin)
                }
            } else if (MainServidor.LIMITE_STATS_CON_BUFF.get(statID) != null) {
                val limitCon: Int = Math.max(limitSin, MainServidor.LIMITE_STATS_CON_BUFF.get(statID))
                valor = Math.min(valor, limitCon)
            }
        } else if (_tipo == 2) {
            if (MainServidor.LIMITE_STATS_PVP_PERSONALIZADOS && perso != null && perso.getTipoPelea() === Constantes.PELEA_TIPO_PVP) {
                if (MainServidor.LIMITE_STATS_CON_BUFF_PVP_INVOCACION.containsKey(statID)) {
                    limitSin = MainServidor.LIMITE_STATS_CON_BUFF_PVP_INVOCACION.get(statID)
                    valor = Math.min(valor, limitSin)
                }
            }
        }
        valores[4] = valor
        return valores
    }

    fun getTotalStatParaMostrar(statID: Int, perso: Pelea?, persox: Personaje?): Int {
        var valor = 0
        if (_statsBase != null) {
            valor += _statsBase.getStatParaMostrar(statID)
        }
        if (_statsBendMald != null) {
            valor += _statsBendMald.getStatParaMostrar(statID)
        }
        if (_statsObjetos != null) {
            valor += _statsObjetos.getStatParaMostrar(statID)
        }
        var limitSin = 0
        if (_tipo == 1) {
            if (MainServidor.LIMITE_STATS_PVP_PERSONALIZADOS && perso != null && perso.getTipoPelea() === Constantes.PELEA_TIPO_PVP) {
                if (MainServidor.LIMITE_STATS_PVP.containsKey(statID)) {
                    limitSin = MainServidor.LIMITE_STATS_PVP.get(statID)
                    valor = Math.min(valor, limitSin)
                } else if (MainServidor.LIMITE_STATS_SIN_BUFF.get(statID) != null) {
                    limitSin = MainServidor.LIMITE_STATS_SIN_BUFF.get(statID)
                    valor = Math.min(valor, limitSin)
                }
            } else if (MainServidor.LIMITE_STATS_PERCO_PERSONALIZADOS && perso != null && perso.getTipoPelea() === Constantes.PELEA_TIPO_RECAUDADOR) {
                if (MainServidor.LIMITE_STATS_PERCO.containsKey(statID)) {
                    limitSin = MainServidor.LIMITE_STATS_PERCO.get(statID)
                    valor = Math.min(valor, limitSin)
                } else if (MainServidor.LIMITE_STATS_SIN_BUFF.get(statID) != null) {
                    limitSin = MainServidor.LIMITE_STATS_SIN_BUFF.get(statID)
                    valor = Math.min(valor, limitSin)
                }
            } else if (MainServidor.LIMITE_STATS_KOLI_PERSONALIZADOS && perso != null && perso.getTipoPelea() === Constantes.PELEA_TIPO_KOLISEO) {
                if (MainServidor.LIMITE_STATS_KOLISEO.containsKey(statID)) {
                    limitSin = MainServidor.LIMITE_STATS_KOLISEO.get(statID)
                    valor = Math.min(valor, limitSin)
                } else if (MainServidor.LIMITE_STATS_SIN_BUFF.get(statID) != null) {
                    limitSin = MainServidor.LIMITE_STATS_SIN_BUFF.get(statID)
                    valor = Math.min(valor, limitSin)
                }
            } else {
                if (MainServidor.LIMITE_STATS_SIN_BUFF.get(statID) != null) {
                    limitSin = MainServidor.LIMITE_STATS_SIN_BUFF.get(statID)
                    valor = Math.min(valor, limitSin)
                }
            }
        }
        if (_statsBuff != null) {
            valor += _statsBuff.getStatParaMostrar(statID)
        }
        if (_tipo == 1) {
            if (MainServidor.LIMITE_STATS_PVP_PERSONALIZADOS && perso != null && perso.getTipoPelea() === Constantes.PELEA_TIPO_PVP) {
                if (MainServidor.LIMITE_STATS_CON_BUFF_PVP.containsKey(statID)) {
                    limitSin = MainServidor.LIMITE_STATS_CON_BUFF_PVP.get(statID)
                    valor = Math.min(valor, limitSin)
                } else if (MainServidor.LIMITE_STATS_CON_BUFF.get(statID) != null) {
                    limitSin = MainServidor.LIMITE_STATS_CON_BUFF.get(statID)
                    valor = Math.min(valor, limitSin)
                }
            } else if (MainServidor.LIMITE_STATS_CON_BUFF.get(statID) != null) {
                val limitCon: Int = Math.max(limitSin, MainServidor.LIMITE_STATS_CON_BUFF.get(statID))
                valor = Math.min(valor, limitCon)
            }
        } else if (_tipo == 2) {
            if (MainServidor.LIMITE_STATS_PVP_PERSONALIZADOS && perso != null && perso.getTipoPelea() === Constantes.PELEA_TIPO_PVP) {
                if (MainServidor.LIMITE_STATS_CON_BUFF_PVP_INVOCACION.containsKey(statID)) {
                    limitSin = MainServidor.LIMITE_STATS_CON_BUFF_PVP_INVOCACION.get(statID)
                    valor = Math.min(valor, limitSin)
                }
            }
        }
        /*if (persox != null) {
				if (persox.getSubclase() == 1) {
					if (statID == Constantes.STAT_MAS_RES_PORC_NEUTRAL || statID == Constantes.STAT_MAS_RES_PORC_TIERRA
							|| statID == Constantes.STAT_MAS_RES_PORC_FUEGO
							|| statID == Constantes.STAT_MAS_RES_PORC_AGUA
							|| statID == ConstafsetPDVntes.STAT_MAS_RES_PORC_AIRE) {
						valor = valor + 5;
						return valor;
					}
				}
				if (persox.getSubclase() == 3 || persox.getSubclase() == 2) {
					if (statID == Constantes.STAT_MAS_RES_PORC_NEUTRAL || statID == Constantes.STAT_MAS_RES_PORC_TIERRA
							|| statID == Constantes.STAT_MAS_RES_PORC_FUEGO
							|| statID == Constantes.STAT_MAS_RES_PORC_AGUA
							|| statID == Constantes.STAT_MAS_RES_PORC_AIRE) {
						valor = valor - 1;
						return valor;
					}
				}
			}*/return valor
    }

    fun clearBuffStats() {
        if (_statsBuff != null) {
            _statsBuff.clear()
        }
    }

    init {
        _statsBase = statsBase
        _statsObjetos = statsObjEquipados
        _statsBuff = statsBuff
        _statsBendMald = statsBendMald
        _tipo = tipo
    }
}