package variables.hechizo

import java.util.ArrayList

class Hechizo(val iD: Int, val nombre: String, var spriteID: Int, // tipo lanz, anim pj, 1 o 0 (frente al sprite)
              var spriteInfos: String,
              valorIA: Int) {

    // public ArrayList<Integer> getArrayAfectados() {
    // return _afectados;
    // }
    var valorIA = 0
    private val _statsHechizos: Map<Integer, StatHechizo> = HashMap<Integer, StatHechizo>()
    fun setAfectados(afectados: String) {
        var afectados = afectados
        if (afectados.contains(":") || afectados.contains(";")) {
            afectados = afectados.replace(":", "|").replace(";", ",")
            GestorSQL.UPDATE_HECHIZO_AFECTADOS(iD, afectados)
        }
        var normales = ""
        try {
            normales = afectados.split(Pattern.quote("|")).get(0)
        } catch (e: Exception) {
        }
        var criticos = ""
        try {
            criticos = afectados.split(Pattern.quote("|")).get(1)
        } catch (e: Exception) {
        }
        val aNormales: Array<String> = normales.split(",")
        val aCriticos: Array<String> = criticos.split(",")
        for (sh in _statsHechizos.values()) {
            if (sh == null) {
                continue
            }
            sh.setAfectados(aNormales, aCriticos)
        }
    }

    fun setCondiciones(condicion: String) {
        var condicion = condicion
        if (condicion.contains(":") || condicion.contains(";")) {
            condicion = condicion.replace(":", "|").replace(";", ",")
        }
        var normales = ""
        try {
            normales = condicion.split(Pattern.quote("|")).get(0)
        } catch (e: Exception) {
        }
        var criticos = ""
        try {
            criticos = condicion.split(Pattern.quote("|")).get(1)
        } catch (e: Exception) {
        }
        val aNormales: Array<String> = normales.split(",")
        val aCriticos: Array<String> = criticos.split(",")
        for (sh in _statsHechizos.values()) {
            if (sh == null) {
                continue
            }
            sh.setCondiciones(aNormales, aCriticos)
        }
    }

    fun getStatsPorNivel(nivel: Int): StatHechizo? {
        return _statsHechizos[nivel]
    }

    fun addStatsHechizos(nivel: Int, stats: StatHechizo?) {
        _statsHechizos.put(nivel, stats)
    }

    companion object {
        fun strDañosStats(sh: StatHechizo, valoresStat: IntArray): String {
            val str = StringBuilder(sh.getHechizoID().toString() + "")
            var paso = false
            for (eh in sh.getEfectosNormales()) {
                val nombre: String = Constantes.getNombreEfecto(eh.getEfectoID())
                var valorStat = -1
                when (eh.getEfectoID()) {
                    Constantes.STAT_CURAR, Constantes.STAT_CURAR_2 -> valorStat = valoresStat[2] // inteligencia
                    else -> {
                        val elemento: Byte = Constantes.getElementoPorEfectoID(eh.getEfectoID())
                        if (elemento != Constantes.ELEMENTO_NULO) {
                            valorStat = valoresStat[elemento.toInt()]
                        }
                    }
                }
                if (paso) {
                    str.append("\n")
                } else {
                    str.append(";")
                }
                str.append("-> $nombre")
                str.append(" " + stringDataEfecto(eh, valorStat))
                paso = true
            }
            return str.toString()
        }

        private fun stringDataEfecto(EH: EfectoHechizo, valorStat: Int): String {
            var s = ""
            s = if (valorStat != -1) {
                if (EH._segundoValor !== -1) {
                    "of " + (EH._primerValor * (100 + valorStat) / 100).toString() + " a " + (EH._segundoValor * (100 + valorStat)
                            / 100)
                } else if (EH._primerValor !== -1) {
                    "fix " + EH._primerValor * (100 + valorStat) / 100
                } else {
                    "[" + EH._primerValor.toString() + ", " + EH._segundoValor.toString() + "]"
                }
            } else {
                "[" + EH._primerValor.toString() + ", " + EH._segundoValor.toString() + "]"
            }
            s += " (Turns: " + (if (EH._duracion <= -1) "Inf." else EH._duracion) + ")"
            return "<i>$s</i>"
        }

        fun aplicaHechizoAPelea(pelea: Pelea, lanzador: Luchador?, celdaObj: Celda,
                                efectosH: ArrayList<EfectoHechizo?>?, tipo: TipoDaño?, esGC: Boolean) {
            val cantObjetivos = aplicaHechizoAPeleaSinGTM(pelea, lanzador, celdaObj, efectosH, tipo, esGC)
            if (cantObjetivos > 0) {
                GestorSalida.ENVIAR_GTM_INFO_STATS_TODO_LUCHADORES_A_TODOS(pelea, 7, true)
            }
        }

        fun aplicaHechizoAPeleaSinGTM(pelea: Pelea, lanzador: Luchador?, celdaObj: Celda,
                                      efectosH: ArrayList<EfectoHechizo?>?, tipo: TipoDaño?, esGC: Boolean): Int {
            if (efectosH == null) {
                return 0
            }
            var suerte = 0
            var suerteMax = 0
            var cantObjetivos = 0
            var azar = 0
            for (EH in efectosH) {
                suerteMax += EH.getSuerte()
            }
            if (suerteMax > 0) {
                azar = Formulas.getRandomInt(1, suerteMax)
            }
            for (EH in efectosH) {
                if (pelea.getFase() === Constantes.PELEA_FASE_FINALIZADO) {
                    return 0
                }
                if (suerteMax > 0) {
                    if (EH.getSuerte() > 0 && EH.getSuerte() < 100) {
                        if (azar < suerte || azar >= EH.getSuerte() + suerte) {
                            suerte += EH.getSuerte()
                            continue
                        }
                        suerte += EH.getSuerte()
                    }
                }
                val objetivos: ArrayList<Luchador> = getObjetivosEfecto(pelea.getMapaCopia(), lanzador, EH, celdaObj.getID())
                if (cantObjetivos < objetivos.size()) {
                    cantObjetivos = objetivos.size()
                }
                EH.aplicarAPelea(pelea, lanzador, objetivos, celdaObj, tipo, esGC)
            }
            return cantObjetivos
        }

        // public static int aplicaHechizoAPelea(final Pelea pelea, final Luchador lanzador, final Celda
        // celdaObj,
        // final ArrayList<EfectoHechizo> efectosH, final TipoDaño tipo, final boolean esGC,
        // ArrayList<ArrayList<Luchador>> aObjetivos) {
        // if (efectosH == null) {
        // return 0;
        // }
        // int suerte = 0, suerteMax = 0, cantObjetivos = 0, azar = 0;
        // for (final EfectoHechizo EH : efectosH) {
        // suerteMax += EH.getSuerte();
        // }
        // if (suerteMax > 0) {
        // azar = Formulas.getRandomValor(1, suerteMax);
        // }
        // int index = 0;
        // for (final EfectoHechizo EH : efectosH) {
        // index++;
        // if (pelea.getFase() == Constantes.PELEA_FASE_FINALIZADO) {
        // return 0;
        // }
        // if (suerteMax > 0) {
        // if (EH.getSuerte() > 0 && EH.getSuerte() < 100) {
        // if (azar < suerte || azar >= EH.getSuerte() + suerte) {
        // suerte += EH.getSuerte();
        // continue;
        // }
        // suerte += EH.getSuerte();
        // }
        // }
        // ArrayList<Luchador> objetivos = aObjetivos.get(index);
        // if (cantObjetivos < objetivos.size()) {
        // cantObjetivos = objetivos.size();
        // }
        // EH.aplicarAPelea(pelea, lanzador, objetivos, celdaObj, tipo, esGC);
        // try {
        // Thread.sleep(EfectoHechizo.TIEMPO_ENTRE_EFECTOS);
        // } catch (Exception e) {}
        // }
        // return cantObjetivos;
        // }
        fun getObjetivosEfecto(mapa: Mapa?, lanzador: Luchador, EH: EfectoHechizo,
                               celdaObjetivo: Short): ArrayList<Luchador> {
            var objetivos: ArrayList<Luchador> = ArrayList()
            val elemento: Int = EH.getAfectadosCond()
            if (elemento > 0) {
                val ultDaño: Int = lanzador.getUltimoElementoDaño()
                if (ultDaño < Constantes.ELEMENTO_NULO) {
                    return objetivos
                }
                if (1 shl ultDaño and elemento == 0) {
                    return objetivos
                }
            }
            val celdasObj: ArrayList<Celda> = Camino.celdasAfectadasEnElArea(mapa, celdaObjetivo, lanzador.getCeldaPelea()
                    .getID(), EH.getZonaEfecto())
            objetivos = getAfectadosZona(lanzador, celdasObj, EH.getAfectados(), celdaObjetivo)
            return objetivos
        }

        private fun getAfectadosZona(lanzador: Luchador, celdasObj: ArrayList<Celda>, afectados: Int,
                                     celdaObjetivo: Short): ArrayList<Luchador> {
            val objetivos: ArrayList<Luchador> = ArrayList<Luchador>()
            for (C in celdasObj) {
                if (C == null) {
                    continue
                }
                val luchTemp: Luchador = C.getPrimerLuchador() ?: continue
                if (afectados >= 0) {
                    // no afecta a los aliados
                    if (afectados and 1 != 0 && luchTemp.getEquipoBin() === lanzador.getEquipoBin()) {
                        continue
                    }
                    // no afecta al lanzador
                    if (afectados and 2 != 0 && luchTemp.getID() === lanzador.getID()) {
                        continue
                    }
                    // no afecta a los enemigos
                    if (afectados and 4 != 0 && luchTemp.getEquipoBin() !== lanzador.getEquipoBin()) {
                        continue
                    }
                    // no afecta a los combatientes (solamente invocaciones)
                    if (afectados and 8 != 0 && (!luchTemp.esInvocacion() || luchTemp.esInvocacion() && luchTemp.invocacionNoLatigo)) {
                        continue
                    }
                    // No afecta a las invocaciones
                    if (afectados and 16 != 0 && luchTemp.esInvocacion()) {
                        continue
                    }
                    // 32 y 64 son de agregar si o si, respectivamente lanzador e invocador
                    if (afectados == 32 && luchTemp.getID() !== lanzador.getID()) {
                        continue
                    }
                    if (afectados == 64 && lanzador.getInvocador() != null && lanzador.getInvocador().getID() !== luchTemp
                                    .getID()) {
                        continue
                    }
                    // no afecta a la casilla donde se lanza
                    if (afectados and 128 != 0 && celdaObjetivo == luchTemp.getCeldaPelea().getID()) {
                        continue
                    }
                    // afecta solo infocaciones
                    if (afectados and 256 != 0 && luchTemp.esInvocacion()) {
                        continue
                    }
                    // para el bonus del sacro que solo le da a aliados pero no a el
                    if (afectados and 512 != 0 && (luchTemp.getEquipoBin() !== lanzador.getEquipoBin() || luchTemp.getID() === lanzador.getID())) {
                        continue
                    }
                    // de aqui pasa el siguiente filtro
                }
                if (!objetivos.contains(luchTemp)) {
                    objetivos.add(luchTemp)
                }
            }
            // agrega si o si al lanzador
            if (afectados >= 0) {
                if (afectados and 32 != 0) {
                    if (!objetivos.contains(lanzador)) {
                        objetivos.add(lanzador)
                    }
                }
                // agrega si o si al invocador
                if (afectados and 64 != 0) {
                    val invocador: Luchador = lanzador.getInvocador()
                    if (invocador != null && !objetivos.contains(invocador)) {
                        objetivos.add(invocador)
                    }
                }
            }
            return objetivos
        }

        //
        @Throws(Exception::class)
        fun analizarHechizoStats(hechizoID: Int, grado: Int, str: String?): StatHechizo {
            val stat: ArrayList<String> = Constantes.convertirStringArray(str)
            val efectosNormales: String = stat.get(0).replace("\"", "")
            val efectosCriticos: String = stat.get(1).replace("\"", "")
            val costePA: Byte = Byte.parseByte(stat.get(2))
            val alcMin: Byte = Byte.parseByte(stat.get(3))
            val alcMax: Byte = Byte.parseByte(stat.get(4))
            val probGC: Short = Short.parseShort(stat.get(5))
            val probFC: Short = Short.parseShort(stat.get(6))
            val lanzarLinea: Boolean = stat.get(7).equalsIgnoreCase("true")
            val lineaVista: Boolean = stat.get(8).equalsIgnoreCase("true")
            val celdaVacia: Boolean = stat.get(9).equalsIgnoreCase("true")
            val alcanceModificable: Boolean = stat.get(10).equalsIgnoreCase("true")
            val tipoHechizo: Byte = Byte.parseByte(stat.get(11))
            val maxPorTurno: Byte = Byte.parseByte(stat.get(12))
            val maxPorObjetivo: Byte = Byte.parseByte(stat.get(13))
            val sigLanzamiento: Byte = Byte.parseByte(stat.get(14))
            val areaAfectados: String = stat.get(15).replace("\"", "")
            val estadosNecesarios: String = stat.get(16)
            val estadosProhibidos: String = stat.get(17)
            val nivelReq: Int = Integer.parseInt(stat.get(18))
            val finTurnoSiFC: Boolean = stat.get(19).equalsIgnoreCase("true")
            val necesitaObjetivo = if (stat.size() >= 21) stat.get(20).equalsIgnoreCase("true") else false
            val stats = StatHechizo(hechizoID, grado, costePA, alcMin, alcMax, probGC, probFC, lanzarLinea,
                    lineaVista, celdaVacia, alcanceModificable, maxPorTurno, maxPorObjetivo, sigLanzamiento, nivelReq, finTurnoSiFC,
                    estadosProhibidos, estadosNecesarios, tipoHechizo, necesitaObjetivo)
            stats.analizarEfectos(efectosNormales, efectosCriticos, areaAfectados, hechizoID)
            return stats
        }
    }

    init {
        this.valorIA = valorIA
    }
}