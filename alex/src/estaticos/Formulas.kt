package estaticos

import java.security.SecureRandom
import java.util.ArrayList
import java.util.Random
import java.util.concurrent.ThreadLocalRandom
import variables.gremio.Recaudador
import variables.mapa.Mapa
import variables.pelea.Botin
import variables.pelea.Luchador
import variables.personaje.Personaje
import variables.stats.TotalStats

object Formulas {
    var RANDOM: SecureRandom = SecureRandom()
    private var rangos = 0
    fun lanzarError() {
        try {
            Integer.parseInt("3RR0R")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun rangoKoli(nivel: Int) {
        var rango = 1
        while (rango * MainServidor.RANGO_NIVEL_KOLISEO < nivel) {
            rango++
        }
        rangos = rango
    }

    fun getRandomValor(i1: Int, i2: Int): Int {
        val rand = Random()
        return rand.nextInt(i2 - i1 + 1) + i1
    }

    fun getRandomValor(rango: String): Int {
        try {
            var num = 0
            val veces: Int = Integer.parseInt(rango.split("d").get(0))
            val margen: Int = Integer.parseInt(rango.split("d").get(1).split("\\+").get(0))
            val adicional: Int = Integer
                    .parseInt(rango.split("d").get(1).split("\\+").get(1))
            for (a in 0 until veces) {
                num += getRandomValor(1, margen)
            }
            return num + adicional
        } catch (localException: Exception) {
        }
        return -1
    }

    fun rangoKoli2(perso: Personaje): Int {
        var rango = 1
        while (rango * MainServidor.RANGO_NIVEL_KOLISEO < perso.getNivel()) {
            rango++
        }
        return rango
    }

    fun rangoKoli(perso: Personaje): Int {
        var rango = 1
        while (rango * MainServidor.RANGO_NIVEL_KOLISEO < perso.getNivel()) {
            rango++
        }
        if (MainServidor.PARAM_KOLI_MISMO_RESET) {
            rango = rango + perso.getResets() * rangos
        }
        return rango
    }

    fun valorValido(cantidad: Int, precio: Long): Boolean {
        // if (cantidad > 1000) {
        // return false;
        // }
        if (precio == 0L || cantidad == 0) {
            return true
        }
        val signo = precio >= 0
        for (i in 0 until cantidad) {
            val signo2: Boolean = precio * (i + 1) >= 0
            if (signo2 != signo) {
                return false
            }
        }
        return true
    }

    fun valorValido1(cantidad: Long, precio: Long): Boolean {
        // if (cantidad > 1000) {
        // return false;
        // }
        if (precio == 0L || cantidad == 0L) {
            return true
        }
        val signo = precio >= 0
        for (i in 0 until cantidad) {
            val signo2: Boolean = precio * (i + 1) >= 0
            if (signo2 != signo) {
                return false
            }
        }
        return true
    }

    val randomBoolean: Boolean
        get() = RANDOM.nextBoolean()

    fun getRandomInt(i1: Int, i2: Int): Int {
        return try {
            if (i1 < 0) {
                return i2
            }
            if (i2 < 0) {
                return i1
            }
            if (i1 > i2) {
                RANDOM.nextInt(i1 - i2 + 1) + i2
            } else {
                RANDOM.nextInt(i2 - i1 + 1) + i1
            }
        } catch (e: Exception) {
            0
        }
    }

    fun getRandomLong(i1: Long, i2: Long): Long {
        return try {
            ThreadLocalRandom.current().nextLong(i1, i2 + 1)
        } catch (e: Exception) {
            i1
        }
    }

    fun formatoTiempo(milis: Long): IntArray {
        var milis = milis
        val f = intArrayOf(1, 1000, 60000, 3600000, 86400000)
        val formato = IntArray(f.size)
        for (i in f.indices.reversed()) {
            formato[i] = (milis / f[i]).toInt()
            milis %= f[i]
        }
        return formato
    }

    fun calcularCosteZaap(mapa1: Mapa, mapa2: Mapa): Int {
        if (mapa1.getID() === mapa2.getID()) {
            return 0
        }
        return if (MainServidor.MODO_ALL_OGRINAS) 0 else 10 * Camino.distanciaEntreMapas(mapa1, mapa2)
    }

    // @SuppressWarnings("unused")
    // private static long getXPGanadaPVM(final ArrayList<Luchador> luchadores, final Luchador
    // luchRec, int nivelGrupoPJ,
    // int nivelGrupoMob, long totalExp, float coefBonus) {
    // if (luchRec.getPersonaje() == null) {
    // return 0;
    // }
    // final TotalStats totalStats = luchRec.getTotalStats();
    // // 910 multiplicar la xp
    // int numJugadores = 0;
    // final float coefSab = (totalStats.getStatParaMostrar(Constantes.STAT_MAS_SABIDURIA)
    // + totalStats.getStatParaMostrar(Constantes.STAT_MAS_PORC_EXP) + 100) / 100f;
    // float coefEntreNiv = nivelGrupoMob / (float) nivelGrupoPJ;
    // if (coefEntreNiv <= 1.1f && coefEntreNiv >= 0.9) {
    // coefEntreNiv = 1;
    // } else if (coefEntreNiv > 1) {
    // coefEntreNiv = 1 / coefEntreNiv;
    // } else if (coefEntreNiv < 0.01) {
    // coefEntreNiv = 0.01f;
    // }
    // for (final Luchador luch : luchadores) {
    // if (luch.esInvocacion() || luch.estaRetirado()) {
    // continue;
    // }
    // numJugadores++;
    // }
    // float coefMul = 1;
    // switch (numJugadores) {
    // case 0 :
    // coefMul = 0.5f;
    // break;
    // case 1 :
    // coefMul = 1;
    // break;
    // case 2 :
    // coefMul = 1.1f;
    // break;
    // case 3 :
    // coefMul = 1.5f;
    // break;
    // case 4 :
    // coefMul = 2.3f;
    // break;
    // case 5 :
    // coefMul = 3.1f;
    // break;
    // case 6 :
    // coefMul = 3.6f;
    // break;
    // case 7 :
    // coefMul = 4.2f;
    // break;
    // case 8 :
    // coefMul = 4.7f;
    // break;
    // default :
    // coefMul = 4.7f;
    // break;
    // }
    // long expFinal = (long) ((1 + coefSab + coefBonus) * (coefMul + coefEntreNiv) * (totalExp /
    // numJugadores))
    // * MainServidor.RATE_XP_PVM;
    // if (expFinal < 0) {
    // expFinal = 0;
    // }
    // return expFinal;
    // }
    fun getXPGanadaRecau(recaudador: Recaudador, totalXP: Long): Long {
        val coef: Float = (recaudador.getGremio().getStatRecolecta(Constantes.STAT_MAS_SABIDURIA) + 100) / 100f
        return (coef * totalXP).toLong()
    }

    fun getXPOficial(luchadores: ArrayList<Luchador?>?, mobs: ArrayList<Luchador?>,
                     luchador: Luchador?, coefEstrellas: Float, coefReto: Float, gano: Boolean): Long {
        var sumaNivelesLuch: Long = 0
        var maxNivelLuch: Long = 0
        var sumaNivelesMobs: Long = 0
        var maxNivelMob: Long = 0
        var sumaExpMobs: Long = 0
        var cantLuch: Long = 0
        var DifIp = 0
        var coefMobLuch = 0.2f
        var esRecaudador = true
        if (luchadores != null) {
            if (luchadores.size() > 1) {
                for (luchDifIP in luchadores) {
                    if (luchDifIP == null || luchador == null) {
                        continue
                    }
                    if (luchDifIP.getID() === luchador.getID()) {
                        continue
                    }
                    if (luchDifIP.esInvocacion() || luchDifIP.estaRetirado()) {
                        continue
                    }
                    if (luchDifIP.getPersonaje() == null || luchador.getPersonaje() == null) {
                        continue
                    }
                    if (luchDifIP.getPersonaje().esMultiman()) {
                        continue
                    }
                    if (!luchDifIP.getPersonaje().getCuenta().getActualIP().equals(luchador.getPersonaje().getCuenta().getActualIP())) {
                        DifIp++
                    }
                }
            }
            for (luch in luchadores) {
                if (luch.getID() === luchador.getID()) {
                    esRecaudador = false
                }
                if (luch.esInvocacion() || luch.estaRetirado()) {
                    continue
                }
                cantLuch++
                sumaNivelesLuch += luch.getNivelViejo()
                if (maxNivelLuch < luch.getNivelViejo()) {
                    maxNivelLuch = luch.getNivelViejo()
                }
            }
        } else {
            sumaNivelesLuch = luchador.getNivelViejo()
            maxNivelLuch = luchador.getNivelViejo()
        }
        for (luch in mobs) {
            if (luch.esInvocacion() || luch.estaRetirado()) {
                continue
            }
            if (luch.getMob() == null) {
                continue
            }
            if (gano || luch.getMuertoPor() != null && luch.getMuertoPor().getEquipoBin() === luchador.getEquipoBin()) {
                sumaExpMobs += luch.getMob().getBaseXp()
                sumaNivelesMobs += luch.getNivelViejo()
                if (maxNivelMob < luch.getNivelViejo()) {
                    maxNivelMob = luch.getNivelViejo()
                }
            }
        }
        if (sumaExpMobs <= 0) {
            return 0
        }
        val coefSab: Float = (luchador.getTotalStats().getTotalStatParaMostrar(Constantes.STAT_MAS_SABIDURIA, luchador.getPelea(), null) + luchador
                .getTotalStats().getTotalStatParaMostrar(Constantes.STAT_MAS_PORC_EXP, luchador.getPelea(), null) + 100) / 100f
        // ahora se calcula la media
        var ratioLuch = 0
        if (!esRecaudador && gano) {
            for (luch in luchadores) {
                if (luch.esInvocacion() || luch.estaRetirado()) {
                    continue
                }
                if (luch.getNivelViejo() >= maxNivelLuch / 3) {
                    ratioLuch++
                }
            }
        }
        var coefNivel = 0.8f
        if (cantLuch > 1) {
            coefMobLuch = Math.min(luchador.getNivelViejo(), Math.round(2.5f * maxNivelMob)) / sumaNivelesLuch.toFloat()
            if (coefMobLuch > 1) {
                coefMobLuch = 1f
            } else if (coefMobLuch < 0.2f) {
                coefMobLuch = 0.2f
            }
            if (sumaNivelesLuch - 5 > sumaNivelesMobs) {
                coefNivel = (sumaNivelesMobs / sumaNivelesLuch).toFloat()
            } else if (sumaNivelesLuch + 10 < sumaNivelesMobs) {
                coefNivel = ((sumaNivelesLuch + 10) / sumaNivelesMobs).toFloat()
            }
        }
        if (coefNivel > 1.2f) {
            coefNivel = 1.2f
        } else if (coefNivel < 0.8f) {
            coefNivel = 0.8f
        }
        var coefMult = 0f
        var coefDifIP = 0f
        coefDifIP = when (DifIp) {
            0 -> 1f
            1 -> 1f
            2 -> 1.1f
            3 -> 1.2f
            4 -> 1.3f
            5 -> 1.4f
            6 -> 1.5f
            7 -> 1.7f
            8 -> 1.8f
            else -> 1.8f
        }
        coefMult = when (ratioLuch) {
            0 -> 1f
            1 -> 1f
            2 -> 1.1f
            3 -> 1.5f
            4 -> 2.3f
            5 -> 3.1f
            6 -> 3.6f
            7 -> 4.2f
            8 -> 4.7f
            else -> 4.7f
        }
        var baseXp = sumaExpMobs
        var ExperienciaMonoP: Long = 0
        if (!esRecaudador) {
            ExperienciaMonoP = (baseXp * coefMult * coefMobLuch * coefNivel).toLong()
            baseXp = (baseXp * coefMult * coefMobLuch * coefNivel * coefDifIP).toLong()
        }
        var xp = (baseXp * coefSab).toLong()
        if (!esRecaudador) {
            xp = (xp * (coefReto + coefEstrellas + MainServidor.RATE_XP_PVM)) as Long
        } else {
            xp = xp * MainServidor.RATE_XP_RECAUDADOR
        }
        if (luchador.getPersonaje() != null) {
            val ExperienciaMono = baseXp - ExperienciaMonoP
            luchador.getPersonaje().setExpMono(ExperienciaMono)
            if (MainServidor.RATE_XP_PVM_ABONADOS > 1) {
                if (luchador.getPersonaje().esAbonado()) {
                    xp *= MainServidor.RATE_XP_PVM_ABONADOS
                }
            }
        }
        if (MainServidor.MODO_DEBUG) {
            System.out.println("suma exp $sumaExpMobs")
            System.out.println("coefNivel $coefNivel")
            System.out.println("coefMob $coefMobLuch")
            System.out.println("coefMult $coefMult")
            System.out.println("coefSab $coefSab")
            System.out.println("coefReto $coefReto")
            System.out.println("coefEstrellas $coefEstrellas")
            System.out.println("MainServidor.RATE_XP_PVM " + MainServidor.RATE_XP_PVM)
            System.out.println("sumaExpMobs $sumaExpMobs")
            System.out.println("baseXp $baseXp")
            System.out.println("xp es $xp")
        }
        if (xp < 1) {
            xp = 0
        }
        return xp
    }

    fun getRandomDecimal(decimales: Int): Float {
        val entero: Int = RANDOM.nextInt(100)
        var decimal = 0f
        if (decimales > 0) {
            val b = Math.pow(10, decimales) as Int
            decimal = (RANDOM.nextInt(b) + 1) / b.toFloat()
        }
        return entero + decimal
    }

    fun getPorcDropLuchador(porcDrop: Float, luch: Luchador, ppRequerida: Int): Float {
        var Final = porcDrop
        Final += (luch.getProspeccionLuchador() - 100) / 1000f
        if (ppRequerida < luch.getProspeccionLuchador()) {
            Final += (luch.getProspeccionLuchador() - ppRequerida - 100) / 100f
        }
        if (Final < 0.01) {
            Final = 0.01f
        }
        return Final
    }

    fun getPorcParaDropAlEquipo(prospecEquipo: Int, coefEstrellas: Float, coefReto: Float, drop: Botin,
                                cantDropeadores: Int): Float {
        // int pp = prospec * (coefReto + coefEstrellas +rate);
        var porcDrop: Float = drop.getPorcentajeBotin() * 1000
        var cantCeros = 0
        if (porcDrop >= 1) {
            cantCeros = Math.log10(porcDrop) as Int + 1
        }
        var rate = 0
        var porcEquipo: Float = ((prospecEquipo - drop.getProspeccionBotin()) * MainServidor.FACTOR_PLUS_PP_PARA_DROP
                * cantDropeadores)
        var factor: Int = MainServidor.FACTOR_ZERO_DROP
        if (drop.esDropFijo()) {
            rate = MainServidor.RATE_DROP_ARMAS_ETEREAS
            factor += 3
        } else {
            rate = MainServidor.RATE_DROP_NORMAL
        }
        if (cantCeros < factor) {
            // si factor zero es mas alto, mayor sera la dificultad para dropear
            porcEquipo = (porcEquipo / Math.pow(10, factor - cantCeros)) as Int.toFloat()
        }
        porcDrop += porcEquipo
        var coef = rate.toFloat()
        if (!MainServidor.PARAM_PERMITIR_BONUS_PELEA_AFECTEN_PROSPECCION) {
            coef += coefReto + coefEstrellas
        }
        /*if(drop.getProspeccionBotin() < prospecEquipo) {
			porcDrop+= (prospecEquipo - drop.getProspeccionBotin()) / 1000f;
		}*/
        var entero = (porcDrop / 1000).toInt()
        var decimal = (porcDrop % 1000).toInt()
        entero += Math.sqrt(entero) * coef
        decimal += Math.sqrt(decimal) * coef
        return entero + decimal / 1000f // decimal
    }

    fun getIniciativa(totalStats: TotalStats, coefPDV: Float): Int {
        var iniciativa = 0
        iniciativa += totalStats.getTotalStatParaMostrar(Constantes.STAT_MAS_INICIATIVA, null, null) // iniciativa
        iniciativa += totalStats.getTotalStatParaMostrar(Constantes.STAT_MAS_AGILIDAD, null, null)
        iniciativa += totalStats.getTotalStatParaMostrar(Constantes.STAT_MAS_INTELIGENCIA, null, null)
        iniciativa += totalStats.getTotalStatParaMostrar(Constantes.STAT_MAS_SUERTE, null, null)
        iniciativa += totalStats.getTotalStatParaMostrar(Constantes.STAT_MAS_FUERZA, null, null)
        // iniciativa += getPDVMax() / fact;
        iniciativa *= (coefPDV / 100).toInt()
        if (iniciativa < 0) {
            iniciativa = 0
        }
        return iniciativa
    }

    fun getXPDonada(nivelPerso: Int, nivelOtro: Int, xpGanada: Long): Long {
        val dif = nivelPerso - nivelOtro
        var coef = 0.1f
        if (dif < 10) {
            coef = 0.1f
        } else if (dif < 20) {
            coef = 0.08f
        } else if (dif < 30) {
            coef = 0.06f
        } else if (dif < 40) {
            coef = 0.04f
        } else if (dif < 50) {
            coef = 0.03f
        } else if (dif < 60) {
            coef = 0.02f
        } else if (dif < 70) {
            coef = 0.015f
        } else if (dif > 70) {
            coef = 0.01f
        }
        return (xpGanada * coef).toLong()
    }

    fun getXPMision(nivelGanador: Int): Long {
        var nivelGanador = nivelGanador
        if (nivelGanador >= MainServidor.NIVEL_MAX_OMEGA) {
            nivelGanador = MainServidor.NIVEL_MAX_OMEGA - 1
        }
        val experiencia: Long = Mundo.getExpCazaCabezas(nivelGanador)
        return experiencia * MainServidor.RATE_XP_PVM
        // float coef = 0.125f;
        // if (nivelGanador > nivelPerdedor) {
        // coef = 1 / ((float) Math.sqrt(nivelGanador - nivelPerdedor) * 8);
        // } else if (nivelGanador < nivelPerdedor) {
        // coef = (2 / (float) Math.sqrt(nivelGanador - nivelPerdedor));
        // }
        // return (long) (exp * Bustemu.RATE_XP_PVP * coef * 8);
    }

    fun getExperienciaGanada(ganadores: ArrayList<Luchador?>, perdedores: ArrayList<Luchador?>,
                             recibidor: Personaje): Int {
        var totalNivLuchGanador = 0
        var totalNivLuchPerdedor = 0
        var cantGanadores: Byte = 0
        var cantPerdedores: Byte = 0
        val ips: ArrayList<String> = ArrayList(16)
        val oGanadores: ArrayList<Luchador> = ArrayList<Luchador>()
        val oPerdedores: ArrayList<Luchador> = ArrayList<Luchador>()
        while (oGanadores.size() < ganadores.size()) {
            var mayor = -1
            var lTemp: Luchador? = null
            for (luch in ganadores) {
                if (oGanadores.contains(luch)) {
                    continue
                }
                if (luch.getNivelViejo() > mayor) {
                    mayor = luch.getNivelViejo()
                    lTemp = luch
                }
            }
            if (lTemp != null) {
                oGanadores.add(lTemp)
            }
        }
        while (oPerdedores.size() < perdedores.size()) {
            var mayor = -1
            var lTemp: Luchador? = null
            for (luch in perdedores) {
                if (oPerdedores.contains(luch)) {
                    continue
                }
                if (luch.getNivelViejo() > mayor) {
                    mayor = luch.getNivelViejo()
                    lTemp = luch
                }
            }
            if (lTemp != null) {
                oPerdedores.add(lTemp)
            }
        }
        for (luch in oGanadores) {
            if (luch.esInvocacion()) {
                continue
            }
            if (!MainServidor.ES_LOCALHOST) {
                if (luch.getPersonaje() != null) {
                    ips.add(luch.getPersonaje().getCuenta().getActualIP())
                }
            }
            totalNivLuchGanador += luch.getNivelViejo()
            cantGanadores++
        }
        for (luch in oPerdedores) {
            if (luch.esInvocacion()) {
                continue
            }
            if (!MainServidor.ES_LOCALHOST) {
                if (luch.getPersonaje() != null) {
                    if (ips.contains(luch.getPersonaje().getCuenta().getActualIP())) {
                        return 0
                    }
                }
            }
            totalNivLuchPerdedor += luch.getNivelViejo()
            cantPerdedores++
        }
        if (cantGanadores.toInt() != 0 || cantPerdedores.toInt() != 0) {
            return 0
        }
        var paso = false
        var experiencia = 0
        val diferenciaNivel: Int = Math.abs(totalNivLuchPerdedor - totalNivLuchGanador)
        if (totalNivLuchGanador <= totalNivLuchPerdedor || diferenciaNivel <= MainServidor.RANGO_NIVEL_PVP) {
            paso = true
        }
        if (!paso) {
            return 0
        }
        for (luch in oPerdedores) {
            if (luch.esInvocacion()) {
                continue
            }
            if (recibidor.experienciapj.containsKey(luch.getPersonaje().getID())) {
                val tiempo: Long = recibidor.experienciapj.get(luch.getPersonaje().getID())
                if (System.currentTimeMillis() - tiempo < MainServidor.MINUTOS_MISION_PVP * 60 * 1000) return 0
            }
            recibidor.experienciapj.put(luch.getPersonaje().getID(), System.currentTimeMillis())
        }
        if (recibidor.getNivel() < 16) {
            experiencia = 200000
        } else if (recibidor.getNivel() < 31) {
            experiencia = 500000
        } else if (recibidor.getNivel() < 61) {
            experiencia = 750000
        } else if (recibidor.getNivel() < 81) {
            experiencia = 1000000
        } else if (recibidor.getNivel() < 101) {
            experiencia = 1250000
        } else if (recibidor.getNivel() < 121) {
            experiencia = 1500000
        } else if (recibidor.getNivel() < 141) {
            experiencia = 1750000
        } else if (recibidor.getNivel() < 161) {
            experiencia = 2000000
        } else if (recibidor.getNivel() < 181) {
            experiencia = 2500000
        } else if (recibidor.getNivel() < 201) {
            experiencia = 3000000
        }
        return MainServidor.RATE_XP_PVM * experiencia
    }

    fun getHonorGanado(ganadores: ArrayList<Luchador?>, perdedores: ArrayList<Luchador?>,
                       recibidor: Luchador, peleaMobs: Boolean): Int {
        if (peleaMobs) {
            return 0
        }
        var totalNivLuchGanador = 0
        var totalNivLuchPerdedor = 0
        var gradoGanador = 0
        var gradoPerdedor = 0
        var cantGanadores: Byte = 0
        var cantPerdedores: Byte = 0
        val ips: ArrayList<String> = ArrayList(16)
        val oGanadores: ArrayList<Luchador> = ArrayList<Luchador>()
        val oPerdedores: ArrayList<Luchador> = ArrayList<Luchador>()
        while (oGanadores.size() < ganadores.size()) {
            var mayor = -1
            var lTemp: Luchador? = null
            for (luch in ganadores) {
                if (oGanadores.contains(luch)) {
                    continue
                }
                if (luch.getNivelViejo() > mayor) {
                    mayor = luch.getNivelViejo()
                    lTemp = luch
                }
            }
            if (lTemp != null) {
                oGanadores.add(lTemp)
            }
        }
        while (oPerdedores.size() < perdedores.size()) {
            var mayor = -1
            var lTemp: Luchador? = null
            for (luch in perdedores) {
                if (oPerdedores.contains(luch)) {
                    continue
                }
                if (luch.getNivelViejo() > mayor) {
                    mayor = luch.getNivelViejo()
                    lTemp = luch
                }
            }
            if (lTemp != null) {
                oPerdedores.add(lTemp)
            }
        }
        for (luch in oGanadores) {
            if (luch.esInvocacion()) {
                continue
            }
            if (luch.getAlineacion() === Constantes.ALINEACION_NEUTRAL) {
                return 0
            }
            if (!MainServidor.ES_LOCALHOST) {
                if (luch.getPersonaje() != null) {
                    ips.add(luch.getPersonaje().getCuenta().getActualIP())
                }
            }
            totalNivLuchGanador += luch.getNivelViejo()
            gradoGanador += luch.getPersonaje().getGradoAlineacion()
            cantGanadores++
        }
        for (luch in oPerdedores) {
            if (luch.esInvocacion()) {
                continue
            }
            if (luch.getAlineacion() === Constantes.ALINEACION_NEUTRAL) {
                return 0
            }
            if (!MainServidor.ES_LOCALHOST) {
                if (luch.getPersonaje() != null) {
                    if (ips.contains(luch.getPersonaje().getCuenta().getActualIP())) {
                        return 0
                    }
                }
            }
            totalNivLuchPerdedor += luch.getNivelViejo()
            gradoPerdedor += luch.getPersonaje().getGradoAlineacion()
            cantPerdedores++
        }
        // System.out.println("totalNivLuchGanador " + totalNivLuchGanador);
        //System.out.println("cantPerdedores " + cantPerdedores);
        // System.out.println("totalNivLuchPerdedor " + totalNivLuchPerdedor);
        // System.out.println("cantGanadores " + cantGanadores);
        if (cantGanadores.toInt() == 0 || cantPerdedores.toInt() == 0) {
            return 0
        }
        var paso = false
        var honor = 0
        val diferenciaNivel: Int = Math.abs(totalNivLuchPerdedor - totalNivLuchGanador)
        val diferenciaRango = gradoPerdedor - gradoGanador
        if (totalNivLuchGanador <= totalNivLuchPerdedor || diferenciaNivel <= MainServidor.RANGO_NIVEL_PVP) {
            paso = true
        }
        // System.out.println("porcPerd " + porcPerd);
        // System.out.println("paso " + paso);
        if (!paso) {
            return 0
        }
        var nivelAlin: Int = recibidor.getNivelAlineacion()
        if (nivelAlin < 1) {
            nivelAlin = 1
        } else if (nivelAlin > 10) {
            nivelAlin = 10
        }
        if (MainServidor.PARAM_GANAR_HONOR_RANDOM) {
            if (!ganadores.contains(recibidor)) {
                honor = -(Mundo.getExpAlineacion(nivelAlin) * 13 / 100)
                honor = honor - honor * MainServidor.DISMINUIR_PERDIDA_HONOR / 100
            } else {
                honor = getRandomInt(100, 150)
            }
        } else {
            if (!ganadores.contains(recibidor)) {
                honor = when (nivelAlin) {
                    7 -> -(recibidor.getPersonaje().getHonor() * getRandomDecimal(5.0, 8.0) / 100)
                    8 -> -(recibidor.getPersonaje().getHonor() * getRandomDecimal(6.0, 9.0) / 100)
                    9 -> -(recibidor.getPersonaje().getHonor() * getRandomDecimal(5.0, 9.0) / 100)
                    10 -> -(recibidor.getPersonaje().getHonor() * getRandomDecimal(7.0, 9.0) / 100)
                    else -> -(recibidor.getPersonaje().getHonor() * getRandomDecimal(5.0, 7.0) / 100)
                }
                honor = honor - honor * MainServidor.DISMINUIR_PERDIDA_HONOR / 100
            } else {
                honor = if (totalNivLuchGanador == totalNivLuchPerdedor) {
                    if (diferenciaRango == 0 || gradoGanador - gradoPerdedor >= 0) {
                        getRandomInt(90, 115)
                    } else {
                        getRandomInt(110, 135)
                    }
                } else if (totalNivLuchGanador < totalNivLuchPerdedor) {
                    getRandomInt(115, 150)
                } else {
                    getRandomInt(110, 135)
                }
            }
        }
        //	 System.out.println("Honor " + honor);
        return MainServidor.RATE_HONOR * honor
    }

    fun getRandomDecimal(min: Double, max: Double): Double {
        return min + (max - min) * RANDOM.nextDouble()
    }

    fun getKamasGanadas(minKamas: Long, maxKamas: Long, perso: Personaje?): Long {
        var posiblesKamas = minKamas
        posiblesKamas = getRandomLong(minKamas, maxKamas)
        val coef = Math.sqrt(MainServidor.RATE_KAMAS) as Float
        /*if (perso != null) {
			if (MainServidor.RATE_KAMAS_ABONADOS > 1) {
				if (perso.esAbonado()) {
					coef += Math.sqrt(MainServidor.RATE_KAMAS_ABONADOS) - 1;
				}
			}
		}*/posiblesKamas = (posiblesKamas * coef).toLong()
        return posiblesKamas
    }
}