package variables.hechizo

import java.util.ArrayList
import java.util.Map.Entry
import variables.mapa.Celda
import variables.mapa.Mapa
import variables.mob.MobGrado
import variables.pelea.Glifo
import variables.pelea.Luchador
import variables.pelea.Pelea
import variables.pelea.Reto
import variables.pelea.Trampa
import variables.pelea.Reto.EstReto
import variables.personaje.Personaje
import variables.stats.TotalStats
import estaticos.Camino
import estaticos.Constantes
import estaticos.MainServidor
import estaticos.Encriptador
import estaticos.Formulas
import estaticos.GestorSalida
import estaticos.Mundo
import estaticos.Mundo.Duo

class EfectoHechizo {
    enum class TipoDaño {
        NORMAL, POST_TURNOS, GLIFO, TRAMPA, CAC, NULL
    }

    //
    protected var _suerte: Byte = 0
    var efectoID = 0
    protected var _nivelHechizoID = 1
    var duracion = 0
        protected set
    var afectados = 0
    var afectadosCond = 0
    var primerValor = -1
        protected set
    var segundoValor = -1
        protected set
    var tercerValor = -1
        protected set
    var hechizoID: Int
        protected set
    protected var _args = ""
    protected var _condicionHechizo = ""

    // condicion es para especificar si el buff hara efecto segun la condicion DAÑO
    // AGUA, DAÑO TIERRA,
    // CURA , MENOS_PA, NADA ... etc
    var zonaEfecto: String? = null
        protected set

    constructor(hechizoID: Int) {
        this.hechizoID = hechizoID
    }

    constructor(efectoID: Int, args: String, hechizo: Int, grado: Int, zonaEfecto: String?) {
        this.efectoID = efectoID
        hechizoID = hechizo
        _nivelHechizoID = grado
        this.zonaEfecto = zonaEfecto
        args = args
    }

    fun setCondicion(condicion: String) {
        if (duracion <= 0) {
            _condicionHechizo = ""
            return
        }
        if (!condicion.contains("BN")) {
            _condicionHechizo = condicion.toUpperCase().trim()
        }
    }

    fun esMismoHechizo(id: Int): Boolean {
        return hechizoID == id
    }

    val suerte: Int
        get() = _suerte.toInt()
    var args: String
        get() = _args
        set(args) {
            _args = args
            val split: Array<String> = _args.split(",")
            try {
                primerValor = Integer.parseInt(split[0])
            } catch (e: Exception) {
            }
            try {
                segundoValor = Integer.parseInt(split[1])
            } catch (e: Exception) {
            }
            try {
                tercerValor = Integer.parseInt(split[2])
            } catch (e: Exception) {
            }
            try {
                duracion = Integer.parseInt(split[3])
            } catch (e: Exception) {
            }
            try {
                _suerte = Byte.parseByte(split[4])
            } catch (e: Exception) {
            }
            if (duracion <= -1) {
                duracion = -3
            }
        }

    fun getRandomValor(objetivo: Luchador): Int {
        if (segundoValor <= 0) {
            return primerValor
        }
        return if (objetivo.tieneBuff(781)) { // mala sombra
            Math.min(primerValor, segundoValor)
        } else Formulas.getRandomInt(primerValor, segundoValor)
    }

    val valorParaPromediar: Int
        get() = when (efectoID) {
            5, 6, 8, 132, 141, 405, 765 -> 1
            else -> {
                val split: Array<String> = _args.split(",")
                var max = 1
                try {
                    if (!split[0].equals("null")) {
                        max = Math.max(max, Integer.parseInt(split[0]))
                    }
                } catch (e: Exception) {
                }
                try {
                    if (!split[1].equals("null")) {
                        max = Math.max(max, Integer.parseInt(split[1]))
                    }
                } catch (e: Exception) {
                }
                max
            }
        }

    protected fun getMaxMinHechizo(objetivo: Luchador, valor: Int): Int {
        // System.out.println("old valor " + valor);
        // if (objetivo.tieneBuff(781)) {// mala sombra
        // valor = _primerValor;
        // } else
        var valor = valor
        if (objetivo.tieneBuff(782)) { // brokle
            valor = Math.max(primerValor, segundoValor)
        }
        // System.out.println("new valor " + valor);
        return valor
    }

    private fun curaSiLoGolpeas(objetivo: Luchador, lanzador: Luchador, afectados: StringBuilder, daño: Int) {
        if (this.getClass() === Buff::class.java) {
            return
        }
        if (objetivo.tieneBuff(786)) {
            restarPDVLuchador(null, lanzador, objetivo, afectados, -daño, Constantes.getElementoPorEfectoID(efectoID))
        }
    }

    private fun duracionFinal(luch: Luchador): Int {
        return if (luch.puedeJugar()) duracion + 1 else duracion
    }

    private fun quitarInvisibilidad(lanzador: Luchador, tipo: TipoDaño) {
        if (lanzador.esInvisible(0) && (tipo == TipoDaño.CAC || tipo == TipoDaño.NORMAL)) {
            lanzador.hacerseVisible()
        }
    }

    private fun aplicarHechizoDeBuff(pelea: Pelea, objetivo: Luchador, celdaObjetivo: Celda) {
        val hechizo: Hechizo = Mundo.getHechizo(primerValor) ?: return
        val sh: StatHechizo = hechizo.getStatsPorNivel(segundoValor) ?: return
        Hechizo.aplicaHechizoAPelea(pelea, objetivo, celdaObjetivo, sh.getEfectosNormales(), TipoDaño.NORMAL, false)
    }

    fun aplicarAPelea(pelea: Pelea, lanzador: Luchador, objetivos: ArrayList<Luchador?>?,
                      celdaObjetivo: Celda, tipo: TipoDaño, esGC: Boolean) {
        try {
            if (objetivos != null) {
                pelea.setUltAfec(objetivos.size() as Byte)
                // pelea.addTiempoHechizo(objetivos.size() * 200);
            }
            if ((pelea.getTipoPelea() === Constantes.PELEA_TIPO_PVM
                            || pelea.getTipoPelea() === Constantes.PELEA_TIPO_PVM_NO_ESPADA) && pelea.getRetos() != null && lanzador.esNoIA()) {
                for (entry in pelea.getRetos().entrySet()) {
                    val reto: Reto = entry.getValue()
                    val retoID: Byte = entry.getKey()
                    var exitoReto: EstReto = reto.getEstado()
                    if (exitoReto !== Reto.EstReto.EN_ESPERA) {
                        continue
                    }
                    val elementoDaño: Int = Constantes.getElementoPorEfectoID(efectoID)
                    when (retoID) {
                        Constantes.RETO_BARBARO -> if (tipo != TipoDaño.CAC) {
                            exitoReto = Reto.EstReto.FALLADO
                        }
                        Constantes.RETO_INCURABLE -> if (efectoID == 108) { // cura
                            exitoReto = Reto.EstReto.FALLADO
                        }
                        Constantes.RETO_ELEMENTAL -> if (elementoDaño != Constantes.ELEMENTO_NULO) {
                            for (luch in pelea.getInicioLuchEquipo2()) {
                                if (objetivos.contains(luch)) {
                                    if (pelea.getUltimoElementoReto() === Constantes.ELEMENTO_NULO) {
                                        pelea.setUltimoElementoReto(elementoDaño)
                                        // fija para siempre el elemento
                                    } else if (pelea.getUltimoElementoReto() !== elementoDaño) {
                                        exitoReto = Reto.EstReto.FALLADO
                                    }
                                }
                                break
                            }
                        }
                        Constantes.RETO_CIRCULEN -> if (efectoID == Constantes.STAT_ROBA_PM || efectoID == Constantes.STAT_MENOS_PM || efectoID == Constantes.STAT_MENOS_PM_FIJO) {
                            for (luch in pelea.getInicioLuchEquipo2()) {
                                if (objetivos.contains(luch)) {
                                    exitoReto = Reto.EstReto.FALLADO
                                    break
                                }
                            }
                        }
                        Constantes.RETO_EL_TIEMPO_PASA -> if (efectoID == Constantes.STAT_ROBA_PA || efectoID == Constantes.STAT_MENOS_PA || efectoID == Constantes.STAT_MENOS_PA_FIJO) {
                            for (luch in pelea.getInicioLuchEquipo2()) {
                                if (objetivos.contains(luch)) {
                                    exitoReto = Reto.EstReto.FALLADO
                                    break
                                }
                            }
                        }
                        Constantes.RETO_PERDIDO_DE_VISTA -> if (efectoID == Constantes.STAT_MENOS_ALCANCE || efectoID == Constantes.STAT_ROBA_ALCANCE) {
                            for (luch in pelea.getInicioLuchEquipo2()) {
                                if (objetivos.contains(luch)) {
                                    exitoReto = Reto.EstReto.FALLADO
                                    break
                                }
                            }
                        }
                        Constantes.RETO_FOCALIZACION -> if (elementoDaño != Constantes.ELEMENTO_NULO) {
                            // es efecto de daño
                            if (reto.getLuchMob() == null) {
                                for (luch in objetivos) {
                                    if (luch.getEquipoBin() === 1) {
                                        reto.setMob(luch)
                                        break
                                    }
                                }
                            }
                            for (luch in objetivos) {
                                if (luch.getEquipoBin() === 1) {
                                    if (reto.getLuchMob() != null && luch.getID() !== reto.getLuchMob().getID()) {
                                        exitoReto = Reto.EstReto.FALLADO
                                        break
                                    }
                                }
                            }
                        }
                        Constantes.RETO_ELITISTA, Constantes.RETO_IMPREVISIBLE ->                             // son lo mismo, execpto q el mob cambia cada turno en elitista
                            if (elementoDaño != Constantes.ELEMENTO_NULO) {
                                if (reto.getLuchMob() != null) {
                                    for (luch in objetivos) {
                                        if (!pelea.getInicioLuchEquipo2().contains(luch)) {
                                            continue
                                        }
                                        if (luch.getID() !== reto.getLuchMob().getID()) {
                                            exitoReto = Reto.EstReto.FALLADO
                                            break
                                        }
                                    }
                                }
                            }
                        Constantes.RETO_ABNEGACION -> if (efectoID == Constantes.STAT_CURAR || efectoID == Constantes.STAT_CURAR_2) { // cura
                            for (luch in objetivos) {
                                if (luch.getID() === lanzador.getID()) {
                                    exitoReto = Reto.EstReto.FALLADO
                                    break
                                }
                            }
                        }
                        Constantes.RETO_DUELO, Constantes.RETO_CADA_UNO_CON_SU_MONSTRUO -> if (elementoDaño != Constantes.ELEMENTO_NULO) {
                            for (luch in objetivos) {
                                if (!pelea.getInicioLuchEquipo2().contains(luch)) {
                                    continue
                                }
                                if (luch.getLuchQueAtacoUltimo() === 0) {
                                    luch.setLuchQueAtacoUltimo(lanzador.getID())
                                } else {
                                    if (luch.getLuchQueAtacoUltimo() !== lanzador.getID()) {
                                        exitoReto = Reto.EstReto.FALLADO
                                        break
                                    }
                                }
                            }
                        }
                    }
                    reto.setEstado(exitoReto)
                }
            }
        } catch (e: Exception) {
            MainServidor.redactarLogServidorln("Exception en aplicarAPelea " + e.toString())
            e.printStackTrace()
            return
        }
        aplicarEfecto(pelea, lanzador, objetivos, celdaObjetivo, tipo, esGC)
    }

    fun aplicarEfecto(pelea: Pelea, lanzador: Luchador, objetivos: ArrayList<Luchador?>?,
                      celdaObjetivo: Celda, tipo: TipoDaño, esGC: Boolean) {
        when (efectoID) {
            4 -> efecto_Telenstransporta(pelea, lanzador, celdaObjetivo)
            5 -> efecto_Empujar(objetivos, pelea, lanzador, celdaObjetivo)
            6 -> efecto_Atraer(objetivos, pelea, lanzador, celdaObjetivo)
            8 -> efecto_Intercambiar_Posiciones(objetivos, pelea, lanzador, celdaObjetivo)
            9, 510, 511 -> efecto_Buff_Valor_Fijo(objetivos, pelea, lanzador, celdaObjetivo)
            50 -> efecto_Levantar_Jugador(pelea, lanzador, celdaObjetivo)
            51 -> efecto_Lanzar_Jugador(pelea, lanzador, celdaObjetivo)
            84, 77 -> efecto_Robo_PA_PM(objetivos, pelea, lanzador, celdaObjetivo)
            79 -> efecto_Buff_Valor_Fijo(objetivos, pelea, lanzador, celdaObjetivo)
            81, 108 -> efecto_Cura(objetivos, pelea, lanzador, tipo)
            82 -> efecto_Robo_PDV_Fijo(objetivos, pelea, lanzador, celdaObjetivo, tipo, esGC)
            90 -> efecto_Dona_Porc_Vida(objetivos, pelea, lanzador, celdaObjetivo)
            275, 276, 277, 278, 279, 85, 86, 87, 88, 89 -> efecto_Daños_Porc_Elemental(objetivos, pelea, lanzador, tipo, esGC)
            91, 92, 93, 94, 95 -> efecto_Roba_PDV_Elemental(objetivos, pelea, lanzador, tipo, esGC)
            96, 97, 98, 99, 100 -> efecto_Daños_Elemental(objetivos, pelea, lanzador, tipo, esGC)
            512 -> efecto_Daños_Elemental_lejos(objetivos, pelea, lanzador, tipo, esGC)
            101, 127, 168, 169 -> efecto_Menos_PA_PM(objetivos, pelea, lanzador, celdaObjetivo, tipo)
            106 -> efecto_Reenvio_Hechizo(objetivos, pelea, lanzador, celdaObjetivo)
            109 -> efecto_Daños_Para_Lanzador(pelea, lanzador, celdaObjetivo, tipo, esGC)
            78, 105, 107, 110, 111, 112, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 128, 138, 142, 144, 145, 152, 153, 154, 155, 156, 157, 160, 161, 162, 163, 164, 171, 176, 177, 178, 179, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 265, 182, 183, 184, 186, 410, 411, 413, 414, 415, 416, 417, 418, 419, 425, 430, 429, 431, 432, 433, 434, 435, 436, 437, 438, 439, 440, 441, 442, 443, 444, 606, 607, 608, 609, 610, 611, 776 -> efecto_Bonus_Malus(objetivos, pelea, lanzador, celdaObjetivo, tipo)
            130 -> efecto_Robar_Kamas(objetivos, pelea, lanzador, celdaObjetivo)
            131, 140 -> efecto_Buff_Valor_Fijo(objetivos, pelea, lanzador, celdaObjetivo)
            132 -> efecto_Deshechizar(objetivos, pelea, lanzador, celdaObjetivo)
            141 -> efecto_Matar_Objetivo(objetivos, pelea, lanzador, celdaObjetivo)
            143 -> efecto_Curar_Sin_Stats(objetivos, pelea, lanzador, celdaObjetivo)
            149 -> efecto_Cambiar_Apariencia(objetivos, pelea, lanzador, celdaObjetivo)
            150 -> efecto_Invisibilidad(objetivos, pelea, lanzador, celdaObjetivo)
            165 -> efecto_Dominio_Arma(objetivos, pelea, lanzador, celdaObjetivo)
            180 -> efecto_Invoca_Doble(pelea, lanzador, celdaObjetivo)
            181, 405 -> efecto_Invoca_Mob(pelea, lanzador, celdaObjetivo)
            200 -> efecto_Invoca_Mob_controlable(pelea, lanzador, celdaObjetivo)
            203 -> efecto_Invoca_Mob_celda_aleatoria(pelea, lanzador, celdaObjetivo)
            201 -> efecto_ResucitarMuertos(pelea, lanzador, celdaObjetivo)
            185 -> efecto_Invoca_Estatico(pelea, lanzador, celdaObjetivo)
            202 -> efecto_Releva_Invisibles(objetivos, pelea, lanzador, celdaObjetivo)
            266, 267, 268, 269, 270, 271 -> efecto_Robo_Bonus(objetivos, pelea, lanzador, celdaObjetivo)
            293 -> efecto_Aumenta_Daños_Hechizo(pelea, lanzador, celdaObjetivo)
            300 -> {
            }
            302 -> aplicarHechizoDeBuff(pelea, lanzador, celdaObjetivo)
            301, 303, 304, 305 -> efecto_Efectos_De_Hechizos(objetivos, pelea, lanzador)
            311 -> efecto_Cura_Porc_Vida_Objetivo(objetivos, pelea, lanzador, celdaObjetivo)
            320 -> efecto_Robo_Alcance(objetivos, pelea, lanzador, celdaObjetivo)
            400 -> efecto_Poner_Trampa(pelea, lanzador, celdaObjetivo)
            401 -> efecto_Glifo_Libre(pelea, lanzador, celdaObjetivo)
            402 -> efecto_Glifo_Fin_Turno(pelea, lanzador, celdaObjetivo)
            420 -> efecto_Quita_Efectos_Hechizo(objetivos, pelea, lanzador, celdaObjetivo)
            421 -> efecto_Retroceder(objetivos, pelea, lanzador, celdaObjetivo)
            422 -> efecto_Porc_PDV_Escudo(objetivos, pelea, lanzador, celdaObjetivo)
            423 -> efecto_Avanzar(objetivos, pelea, lanzador, celdaObjetivo)
            424 -> efecto_Menos_Porc_PDV_Temporal(objetivos, lanzador, celdaObjetivo)
            666 -> {
            }
            670, 671, 672 -> efecto_Daños_Porc_Vida_Neutral(objetivos, pelea, lanzador, celdaObjetivo, tipo, esGC)
            750, 751, 765 -> efecto_Buff_Valor_Fijo(objetivos, pelea, lanzador, celdaObjetivo)
            766 ->                 // FIXME TODO
                efecto_Buff_Valor_Fijo(objetivos, pelea, lanzador, celdaObjetivo)
            780 -> efecto_Resucitar(pelea, lanzador, celdaObjetivo)
            782, 781 -> efecto_Buff_Valor_Fijo(objetivos, pelea, lanzador, celdaObjetivo)
            783 -> efecto_Retrocede_Hasta_Cierta_Casilla(pelea, lanzador, celdaObjetivo)
            784 -> efecto_Teleport_Inicio(objetivos, pelea, lanzador, celdaObjetivo)
            786, 787, 788 -> efecto_Buff_Valor_Fijo(objetivos, pelea, lanzador, celdaObjetivo)
            950, 951 -> efecto_Estados(objetivos, pelea, lanzador, celdaObjetivo)
            else -> MainServidor.redactarLogServidorln("Efecto no implantado ID: " + efectoID + " args: " + _args)
        }
    }

    // private void efectosDeHechizoAfeitado(Luchador lanzador, Pelea pelea,
    // Luchador objetivo) {
    // final Buff buff = objetivo.getBuffPorHechizoYEfecto(1038,
    // Constantes.STAT_MAS_PA);
    // objetivo.addBuffConGIE(Constantes.STAT_MAS_PA, buff.getPrimerValor(),
    // buff.getTurnosRestantes(), _hechizoID,
    // convertirArgs(buff.getPrimerValor(), Constantes.STAT_MAS_PA, buff.getArgs()),
    // lanzador, true,
    // TipoDaño.POST_TURNOS);
    // GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, Constantes.STAT_MAS_PA,
    // lanzador.getID() + "",
    // objetivo.getID() + ","
    // + buff.getPrimerValor() + "," + buff.getTurnosRestantes());
    // }
    private fun efecto_Quita_Efectos_Hechizo(objetivos: ArrayList<Luchador?>?, pelea: Pelea, lanzador: Luchador,
                                             celdaObjetivo: Celda) {
        for (objetivo in objetivos) {
            val buffs: ArrayList<Buff> = ArrayList()
            var tiene = false
            when (tercerValor) {
                2201 -> {
                    if (objetivo.tieneEstado(63)) {
                        objetivo.setEstado(63, 0)
                    }
                    if (objetivo.tieneEstado(62)) {
                        objetivo.setEstado(62, 0)
                    }
                }
                2207 -> {
                    if (objetivo.tieneEstado(64)) {
                        objetivo.setEstado(64, 0)
                    }
                    if (objetivo.tieneEstado(62)) {
                        objetivo.setEstado(62, 0)
                    }
                }
                2209 -> {
                    if (objetivo.tieneEstado(64)) {
                        objetivo.setEstado(64, 0)
                    }
                    if (objetivo.tieneEstado(63)) {
                        objetivo.setEstado(63, 0)
                    }
                }
            }
            for (buff in objetivo.getBuffsPelea()) {
                if (buff.getHechizoID() !== tercerValor) {
                    buffs.add(buff)
                } else {
                    tiene = true
                }
            }
            if (!tiene) {
                continue
            }
            GestorSalida.ENVIAR_GIe_QUITAR_BUFF(pelea, 7, objetivo.getID())
            objetivo.resetearBuffs(buffs)
        }
    }

    private fun efecto_Telenstransporta(pelea: Pelea, lanzador: Luchador, celdaObjetivo: Celda) { // teletransporta
        if (lanzador.estaMuerto() || lanzador.esEstatico() || lanzador.tieneEstado(Constantes.ESTADO_PESADO)
                || lanzador.tieneEstado(Constantes.ESTADO_ARRAIGADO)
                || lanzador.tieneEstado(Constantes.ESTADO_TRANSPORTADO)
                || lanzador.tieneEstado(Constantes.ESTADO_PORTADOR)) {
            return
        }
        if (!celdaObjetivo.esCaminable(true)) {
            GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(pelea, 7, "1CELDA_NO_CAMINABLE")
            return
        }
        if (celdaObjetivo.getPrimerLuchador() != null) {
            GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 151, lanzador.getID().toString() + "", hechizoID.toString() + "")
            return
        }
        lanzador.getCeldaPelea().moverLuchadoresACelda(celdaObjetivo)
        GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 4, lanzador.getID().toString() + "",
                lanzador.getID().toString() + "," + celdaObjetivo.getID())
        try {
            Thread.sleep(500)
        } catch (e: Exception) {
        }
        verificaTrampas(lanzador)
    }

    private fun efecto_Empujar(objetivos: ArrayList<Luchador?>?, pelea: Pelea, lanzador: Luchador,
                               celdaObjetivo: Celda) {
        if (duracion == 0) {
            for (objetivo in objetivos) {
                var celdaInicio: Celda = celdaObjetivo
                if (objetivo.getCeldaPelea().getID() === celdaObjetivo.getID()) {
                    celdaInicio = lanzador.getCeldaPelea()
                }
                var nroCasillas = primerValor
                if (objetivo.tieneEstado(Constantes.ESTADO_ESCARIFICADO)) {
                    if (hechizoID == 3298) {
                        nroCasillas += 1
                    }
                    if (hechizoID == 3299) {
                        nroCasillas += 2
                    }
                }
                efectoEmpujones(pelea, lanzador, objetivo, celdaInicio, objetivo.getCeldaPelea(), nroCasillas, true)
            }
        }
    }

    // auto retroceder - zobal
    private fun efecto_Retroceder(objetivos: ArrayList<Luchador?>?, pelea: Pelea, lanzador: Luchador,
                                  celdaObjetivo: Celda) {
        val nroCasillas = primerValor
        // for (final Luchador objetivo : objetivos) {
        efectoEmpujones(pelea, lanzador, lanzador, celdaObjetivo, lanzador.getCeldaPelea(), nroCasillas, true)
        // }
    }

    // auto avanzar - zobal
    private fun efecto_Avanzar(objetivos: ArrayList<Luchador?>?, pelea: Pelea, lanzador: Luchador,
                               celdaObjetivo: Celda) {
        val nroCasillas = -primerValor
        // for (final Luchador objetivo : objetivos) {
        efectoEmpujones(pelea, lanzador, lanzador, celdaObjetivo, lanzador.getCeldaPelea(), nroCasillas, false)
        // }
    }

    private fun efecto_Atraer(objetivos: ArrayList<Luchador?>?, pelea: Pelea, lanzador: Luchador,
                              celdaObjetivo: Celda) {
        for (objetivo in objetivos) {
            var celdaInicio: Celda = celdaObjetivo
            if (objetivo.getCeldaPelea().getID() === celdaObjetivo.getID()) {
                celdaInicio = lanzador.getCeldaPelea()
            }
            val nroCasillas = -primerValor
            efectoEmpujones(pelea, lanzador, objetivo, celdaInicio, objetivo.getCeldaPelea(), nroCasillas, false)
        }
    }

    private fun efecto_Porc_PDV_Escudo(objetivos: ArrayList<Luchador?>?, pelea: Pelea, lanzador: Luchador,
                                       celdaObjetivo: Celda) {
        val escudo: Int = lanzador.getPDVConBuff() * primerValor / 100
        val efectoID = getStatPorEfecto(efectoID)
        for (objetivo in objetivos) {
            if (objetivo.estaMuerto()) {
                continue
            }
            objetivo.addEscudo(escudo)
            objetivo.addBuffConGIE(efectoID, escudo, duracion, hechizoID, convertirArgs(escudo, efectoID, _args),
                    lanzador, false, TipoDaño.POST_TURNOS, _condicionHechizo)
        }
    }

    private fun efecto_Menos_Porc_PDV_Temporal(objetivos: ArrayList<Luchador?>?, lanzador: Luchador,
                                               celdaObjetivo: Celda) {
        val efectoID = getStatPorEfecto(efectoID)
        for (objetivo in objetivos) {
            if (objetivo.estaMuerto()) {
                continue
            }
            val valor: Int = objetivo.getPDVConBuff() * primerValor / 100
            objetivo.addBuffConGIE(efectoID, valor, duracion, hechizoID, convertirArgs(valor, efectoID, _args),
                    lanzador, true, TipoDaño.POST_TURNOS, _condicionHechizo)
        }
    }

    /*
     * private void efecto_Intercambiar_Posiciones(ArrayList<Luchador> objetivos,
     * Pelea pelea, Luchador lanzador, Celda celdaObjetivo) { if
     * (lanzador.estaMuerto() || lanzador.esEstatico()) return; if
     * (lanzador.tieneEstado(Constantes.ESTADO_PESADO) ||
     * lanzador.tieneEstado(Constantes.ESTADO_ARRAIGADO) || lanzador
     * .tieneEstado(Constantes.ESTADO_TRANSPORTADO) ||
     * lanzador.tieneEstado(Constantes.ESTADO_PORTADOR)) return; if (objetivos ==
     * null || objetivos.isEmpty()) return; Luchador objetivo = objetivos.get(0); if
     * (objetivo == null || objetivo.estaMuerto()) return; switch (this._hechizoID)
     * { default: if (objetivo.esEstatico()) break; case 438: case 445: case 449: if
     * (objetivo.tieneEstado(Constantes.ESTADO_ARRAIGADO) ||
     * objetivo.tieneEstado(Constantes.ESTADO_TRANSPORTADO) || objetivo
     * .tieneEstado(Constantes.ESTADO_PORTADOR)) return; break; } Celda
     * exCeldaObjetivo = objetivo.getCeldaPelea(); Celda exCeldaLanzador =
     * lanzador.getCeldaPelea(); exCeldaObjetivo.limpiarLuchadores();
     * exCeldaLanzador.limpiarLuchadores(); objetivo.setCeldaPelea(exCeldaLanzador);
     * lanzador.setCeldaPelea(exCeldaObjetivo);
     * GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 4, "" + lanzador.getID(), "" +
     * objetivo.getID() + "," + objetivo.getID());
     * GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 4, "" + lanzador.getID(), "" +
     * lanzador.getID() + "," + lanzador.getID()); try { Thread.sleep(500L); } catch
     * (Exception exception) {} verificaTrampas(objetivo);
     * verificaTrampas(lanzador); }
     */
    private fun efecto_Intercambiar_Posiciones(objetivos: ArrayList<Luchador?>?, pelea: Pelea,
                                               lanzador: Luchador, celdaObjetivo: Celda) {
        if (lanzador.estaMuerto() || lanzador.esEstatico()) {
            return
        }
        if (lanzador.tieneEstado(Constantes.ESTADO_PESADO) || lanzador.tieneEstado(Constantes.ESTADO_ARRAIGADO)
                || lanzador.tieneEstado(Constantes.ESTADO_TRANSPORTADO)
                || lanzador.tieneEstado(Constantes.ESTADO_PORTADOR)) {
            return
        }
        if (objetivos == null || objetivos.isEmpty()) {
            return
        }
        val objetivo: Luchador = objetivos.get(0)
        if (objetivo == null || objetivo.estaMuerto()) {
            return
        }
        when (hechizoID) {
            438, 449, 445 -> if (objetivo.tieneEstado(Constantes.ESTADO_ARRAIGADO)
                    || objetivo.tieneEstado(Constantes.ESTADO_TRANSPORTADO)
                    || objetivo.tieneEstado(Constantes.ESTADO_PORTADOR)) {
                return
            }
            else -> {
                if (objetivo.esEstatico()) {
                    break
                }
                if (objetivo.tieneEstado(Constantes.ESTADO_ARRAIGADO)
                        || objetivo.tieneEstado(Constantes.ESTADO_TRANSPORTADO)
                        || objetivo.tieneEstado(Constantes.ESTADO_PORTADOR)) {
                    return
                }
            }
        }
        val exCeldaObjetivo: Celda = objetivo.getCeldaPelea()
        val exCeldaLanzador: Celda = lanzador.getCeldaPelea()
        exCeldaObjetivo.limpiarLuchadores()
        exCeldaLanzador.limpiarLuchadores()
        objetivo.setCeldaPelea(exCeldaLanzador)
        lanzador.setCeldaPelea(exCeldaObjetivo)
        GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 4, lanzador.getID().toString() + "",
                objetivo.getID().toString() + "," + exCeldaLanzador.getID())
        GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 4, lanzador.getID().toString() + "",
                lanzador.getID().toString() + "," + exCeldaObjetivo.getID())
        try {
            Thread.sleep(500)
        } catch (e: Exception) {
        }
        verificaTrampas(objetivo)
        verificaTrampas(lanzador)
    }

    // esquiva golpes retrocediendo casillas
    private fun efecto_Buff_Valor_Fijo(objetivos: ArrayList<Luchador?>?, pelea: Pelea, lanzador: Luchador,
                                       celdaObjetivo: Celda) {
        val efectoID = getStatPorEfecto(efectoID)
        val temp: ArrayList<Luchador> = ArrayList<Luchador>()
        for (objetivo in objetivos) {
            if (objetivo.estaMuerto()) {
                continue
            }
            temp.add(objetivo)
        }
        if (!temp.isEmpty()) {
            pelea.addBuffLuchadores(temp, efectoID, primerValor, duracion, hechizoID,
                    convertirArgs(primerValor, efectoID, _args), lanzador, TipoDaño.POST_TURNOS, _condicionHechizo)
        }
    }

    // permite levantar a un jugador
    private fun efecto_Levantar_Jugador(pelea: Pelea, lanzador: Luchador, celdaObjetivo: Celda) {
        val objetivo: Luchador = celdaObjetivo.getPrimerLuchador()
        if (lanzador.estaMuerto() || lanzador.esEstatico() || lanzador.tieneEstado(Constantes.ESTADO_TRANSPORTADO)
                || lanzador.tieneEstado(Constantes.ESTADO_PORTADOR)) {
            return
        }
        if (objetivo == null || objetivo.estaMuerto() || objetivo.esEstatico()
                || objetivo.tieneEstado(Constantes.ESTADO_ARRAIGADO)
                || objetivo.tieneEstado(Constantes.ESTADO_TRANSPORTADO)
                || objetivo.tieneEstado(Constantes.ESTADO_PORTADOR)) {
            return
        }
        objetivo.getCeldaPelea().removerLuchador(objetivo)
        objetivo.setCeldaPelea(lanzador.getCeldaPelea())
        objetivo.setEstado(Constantes.ESTADO_TRANSPORTADO, -1) // infinito
        lanzador.setEstado(Constantes.ESTADO_PORTADOR, -1)
        objetivo.setTransportadoPor(lanzador)
        lanzador.setTransportando(objetivo)
        GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 50, lanzador.getID().toString() + "", "" + objetivo.getID())
        try {
            Thread.sleep(500)
        } catch (e: Exception) {
        }
    }

    // lanza a un jugador
    private fun efecto_Lanzar_Jugador(pelea: Pelea, lanzador: Luchador, celdaObjetivo: Celda) {
        if (!celdaObjetivo.esCaminable(true) || celdaObjetivo.getPrimerLuchador() != null) {
            return
        }
        val objetivo: Luchador = lanzador.getTransportando()
        objetivo.getCeldaPelea().removerLuchador(objetivo)
        objetivo.setCeldaPelea(celdaObjetivo)
        GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 51, lanzador.getID().toString() + "", celdaObjetivo.getID().toString() + "")
        try {
            Thread.sleep(1000)
        } catch (e: Exception) {
        }
        pelea.quitarTransportados(lanzador)
        GestorSalida.ENVIAR_GTM_INFO_STATS_TODO_LUCHADORES_A_TODOS(pelea, 7, true)
    }

    private fun efecto_Robo_PA_PM(objetivos: ArrayList<Luchador?>?, pelea: Pelea, lanzador: Luchador,
                                  celdaObjetivo: Celda) {
        val valor = getRandomValor(lanzador)
        val afectados = StringBuilder()
        var ganados = 0
        var efectoID = getStatPorEfecto(efectoID)
        for (objetivo in objetivos) {
            if (objetivo.estaMuerto()) {
                continue
            }
            var paso = false
            for (b in objetivo.getBuffsPelea()) {
                if (b.getCondicionBuff().isEmpty()) {
                    continue
                }
                if (efectoID == Constantes.STAT_MENOS_PA && b.getCondicionBuff().contains("-PA")) {
                    paso = true
                    b.aplicarBuffCondicional(objetivo)
                    continue
                }
                if (efectoID == Constantes.STAT_MENOS_PM && b.getCondicionBuff().contains("-PM")) {
                    paso = true
                    b.aplicarBuffCondicional(objetivo)
                    continue
                }
            }
            if (paso) {
                continue
            }
            var perdidos = getPuntosPerdidos(efectoID, valor, lanzador, objetivo)
            perdidos = getMaxMinHechizo(objetivo, perdidos)
            if (perdidos < valor) {
                GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, if (efectoID == Constantes.STAT_MENOS_PM) 309 else 308, lanzador.getID().toString() + "", objetivo.getID().toString() + "," + (valor - perdidos))
            }
            if (perdidos >= 1) {
                objetivo.addBuffConGIE(efectoID, perdidos, duracion, hechizoID,
                        convertirArgs(perdidos, efectoID, _args), lanzador, true, TipoDaño.POST_TURNOS,
                        _condicionHechizo)
                if (afectados.length() > 0) {
                    afectados.append("¬")
                }
                afectados.append(objetivo.getID().toString() + "," + -perdidos + "," + duracionFinal(objetivo))
                ganados += perdidos
            }
        }
        if (afectados.length() > 0 && _condicionHechizo.isEmpty()) {
            GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, efectoID, lanzador.getID().toString() + "", afectados.toString())
        }
        if (ganados > 0) {
            efectoID = if (this.efectoID == Constantes.STAT_ROBA_PM) Constantes.STAT_MAS_PM else Constantes.STAT_MAS_PA
            lanzador.addBuffConGIE(efectoID, ganados, duracion, hechizoID, convertirArgs(ganados, efectoID, _args),
                    lanzador, true, TipoDaño.POST_TURNOS, _condicionHechizo)
            GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, efectoID, lanzador.getID().toString() + "",
                    lanzador.getID().toString() + "," + ganados + "," + duracionFinal(lanzador))
        }
    }

    // FIXME ANALIZAR PORQ DURACION + 1
    private fun efecto_Robo_Alcance(objetivos: ArrayList<Luchador?>?, pelea: Pelea, lanzador: Luchador,
                                    celdaObjetivo: Celda) {
        val valor = getRandomValor(lanzador)
        val afectados = StringBuilder()
        var ganados = 0
        var efectoID = getStatPorEfecto(efectoID)
        val temp: ArrayList<Luchador> = ArrayList<Luchador>()
        for (objetivo in objetivos) {
            if (objetivo.estaMuerto()) {
                continue
            }
            if (afectados.length() > 0) afectados.append("¬")
            afectados.append(objetivo.getID().toString() + "," + valor + "," + duracionFinal(objetivo))
            temp.add(objetivo)
            ganados += valor
        }
        if (afectados.length() > 0 && _condicionHechizo.isEmpty()) {
            GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, efectoID, lanzador.getID().toString() + "", afectados.toString())
        }
        if (!temp.isEmpty()) {
            pelea.addBuffLuchadores(temp, efectoID, valor, duracion, hechizoID, convertirArgs(valor, efectoID, _args),
                    lanzador, TipoDaño.POST_TURNOS, _condicionHechizo)
        }
        if (ganados > 0) {
            efectoID = Constantes.STAT_MAS_ALCANCE
            lanzador.addBuffConGIE(efectoID, ganados, duracion, hechizoID, convertirArgs(ganados, efectoID, _args),
                    lanzador, true, TipoDaño.POST_TURNOS, _condicionHechizo)
            GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, efectoID, lanzador.getID().toString() + "",
                    lanzador.getID().toString() + "," + ganados + "," + duracionFinal(lanzador))
        }
    }

    // FIXME OBSERVAR
    private fun efecto_Robo_Bonus(objetivos: ArrayList<Luchador?>?, pelea: Pelea, lanzador: Luchador,
                                  celdaObjetivo: Celda) {
        var valor = getRandomValor(lanzador)
        val afectados = StringBuilder()
        var robo = 0
        val efectoID = getStatPorEfecto(efectoID)
        val valor2 = valor
        val temp: ArrayList<Luchador> = ArrayList<Luchador>()
        for (objetivo in objetivos) {
            if (objetivo.estaMuerto()) {
                continue
            }
            valor = getMaxMinHechizo(objetivo, valor)
            if (validar(objetivo.getPelea()) && MainServidor.LIMITE_BONUS_2.containsKey(efectoID)
                    && valor <= MainServidor.LIMITE_BONUS_VALOR.get(efectoID)) {
                valor = Math.min(valor, MainServidor.LIMITE_BONUS_2.get(efectoID))
            }
            if (valor != valor2) {
                objetivo.addBuffConGIE(efectoID, valor, duracion, hechizoID, convertirArgs(valor, efectoID, _args),
                        lanzador, true, TipoDaño.POST_TURNOS, _condicionHechizo)
            } else {
                temp.add(objetivo)
            }
            if (afectados.length() > 0) afectados.append("¬")
            afectados.append(objetivo.getID().toString() + "," + valor + "," + duracionFinal(objetivo))
            robo += valor
            valor = valor2
        }
        if (afectados.length() > 0 && _condicionHechizo.isEmpty()) {
            GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, efectoID, lanzador.getID().toString() + "", afectados.toString())
        }
        if (!temp.isEmpty()) {
            pelea.addBuffLuchadores(temp, efectoID, valor, duracion, hechizoID, convertirArgs(valor, efectoID, _args),
                    lanzador, TipoDaño.POST_TURNOS, _condicionHechizo)
        }
        if (robo > 0) {
            val stat = getStatContrario(efectoID)
            lanzador.addBuffConGIE(stat, robo, duracion, hechizoID, convertirArgs(robo, stat, _args), lanzador, true,
                    TipoDaño.POST_TURNOS, _condicionHechizo)
            GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, stat, lanzador.getID().toString() + "",
                    lanzador.getID().toString() + "," + robo + "," + duracionFinal(lanzador))
        }
    }

    private fun efecto_Robo_PDV_Fijo(objetivos: ArrayList<Luchador?>?, pelea: Pelea, lanzador: Luchador,
                                     celdaObjetivo: Celda, tipo: TipoDaño, esGC: Boolean) {
        val efectoID = getStatPorEfecto(efectoID)
        if (duracion == 0) {
            quitarInvisibilidad(lanzador, tipo)
            val afectados = StringBuilder()
            for (objetivo in objetivos) {
                if (objetivo.estaMuerto()) {
                    continue
                }
                if (objetivo.tieneEstado(76)) {
                    if (Camino.distanciaDosCeldas(pelea.getMapaCopia(), objetivo.getCeldaPelea().getID(),
                                    lanzador.getCeldaPelea().getID()) <= 1) {
                        objetivo = reenvioHechizo(pelea, _nivelHechizoID, hechizoID, objetivo, lanzador, tipo, true)
                    }
                } else {
                    objetivo = reenvioHechizo(pelea, _nivelHechizoID, hechizoID, objetivo, lanzador, tipo, false)
                }
                objetivo = sacrificio(pelea, objetivo)
                var daño = primerValor
                daño = calcularDañoFinal(pelea, lanzador, objetivo, 10 + Constantes.getElementoPorEfectoID(efectoID),
                        daño.toFloat(), hechizoID, tipo, esGC)
                if (validar(objetivo.getPelea()) && MainServidor.LIMITE_LOCURA > 0) {
                    daño = Math.min(daño, MainServidor.LIMITE_LOCURA)
                }
                daño = aplicarBuffContraGolpe(efectoID, daño, objetivo, lanzador, pelea, hechizoID, tipo)
                daño = restarPDVLuchador(pelea, objetivo, lanzador, afectados, daño,
                        Constantes.getElementoPorEfectoID(this.efectoID))
                if (daño > 0 && !tipo.equals(TipoDaño.POST_TURNOS)) restarPDVLuchador(pelea, lanzador, lanzador, afectados, -daño / 2,
                        Constantes.getElementoPorEfectoID(this.efectoID))
                curaSiLoGolpeas(objetivo, lanzador, afectados, daño)
            }
            if (afectados.length() > 0) {
                GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, lanzador.getID().toString() + "", afectados.toString())
            }
        } else {
            val temp: ArrayList<Luchador> = ArrayList<Luchador>()
            for (objetivo in objetivos) {
                if (objetivo.estaMuerto()) {
                    continue
                }
                temp.add(objetivo)
            }
            if (!temp.isEmpty()) {
                pelea.addBuffLuchadores(temp, efectoID, primerValor, duracion, hechizoID,
                        convertirArgs(primerValor, efectoID, _args), lanzador, tipo, _condicionHechizo)
            }
        }
    }

    private fun efecto_Robar_Kamas(objetivos: ArrayList<Luchador?>?, pelea: Pelea, lanzador: Luchador,
                                   celdaObjetivo: Celda) { // robar
        // kamas
        if (pelea.getTipoPelea() === 0) {
            return
        }
        var valor = getRandomValor(lanzador)
        val afectados = StringBuilder()
        for (objetivo in objetivos) {
            val perso: Personaje = objetivo.getPersonaje()
            if (objetivo.estaMuerto() || perso == null) {
                continue
            }
            if (valor > perso.getKamas()) {
                valor = perso.getKamas()
            }
            if (valor == 0) {
                continue
            }
            perso.addKamas(-valor, false, false)
            val perso2: Personaje = lanzador.getPersonaje()
            if (perso2 != null) {
                perso2.addKamas(valor, false, false)
            }
            if (afectados.length() > 0) {
                afectados.append("¬")
            }
            afectados.append(objetivo.getID().toString() + "," + valor)
        }
        if (afectados.length() > 0 && _condicionHechizo.isEmpty()) {
            GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 130, lanzador.getID().toString() + "", afectados.toString())
        }
    }

    private fun efecto_Efectos_De_Hechizos(objetivos: ArrayList<Luchador?>?, pelea: Pelea, lanzador: Luchador) {
        val efectoID = getStatPorEfecto(efectoID)
        val temp: ArrayList<Luchador> = ArrayList<Luchador>()
        for (objetivo in objetivos) {
            if (objetivo.estaMuerto()) {
                continue
            }
            temp.add(objetivo)
        }
        if (!temp.isEmpty()) {
            pelea.addBuffLuchadores(temp, efectoID, primerValor, duracion, hechizoID,
                    convertirArgs(primerValor, efectoID, _args), lanzador, TipoDaño.POST_TURNOS, _condicionHechizo)
        }
    }

    private fun efecto_Menos_PA_PM(objetivos: ArrayList<Luchador?>?, pelea: Pelea, lanzador: Luchador,
                                   celdaObjetivo: Celda, tipo: TipoDaño) {
        val valor = getRandomValor(lanzador)
        val efectoID = getStatPorEfecto(efectoID)
        val temp: ArrayList<Luchador> = ArrayList<Luchador>()
        val afectados = StringBuilder()
        for (objetivo in objetivos) {
            if (objetivo.estaMuerto()) {
                continue
            }
            if (efectoID == Constantes.STAT_MENOS_PM && objetivo.tieneEstado(75)) {
                val temp2: ArrayList<Luchador?> = ArrayList<Luchador>()
                temp2.add(objetivo)
                efecto_Intercambiar_Posiciones(temp2, pelea, lanzador, celdaObjetivo)
                continue
            }
            var paso = false
            for (b in objetivo.getBuffsPelea()) {
                if (b.getCondicionBuff().isEmpty()) {
                    continue
                }
                if (efectoID == Constantes.STAT_MENOS_PA && b.getCondicionBuff().contains("-PA")) {
                    paso = true
                    b.aplicarBuffCondicional(objetivo)
                    continue
                }
                if (efectoID == Constantes.STAT_MENOS_PM && b.getCondicionBuff().contains("-PM")) {
                    paso = true
                    b.aplicarBuffCondicional(objetivo)
                    continue
                }
            }
            if (paso) {
                continue
            }
            var perdidos = valor
            when (this.efectoID) {
                Constantes.STAT_MENOS_PA -> {
                    objetivo = reenvioHechizo(pelea, _nivelHechizoID, hechizoID, objetivo, lanzador, tipo, false)
                    if (objetivo.estaMuerto()) {
                        continue
                    }
                    perdidos = getPuntosPerdidos(this.efectoID, valor, lanzador, objetivo)
                    perdidos = getMaxMinHechizo(objetivo, perdidos)
                    if (perdidos < valor) { // esquivados
                        GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, if (efectoID == Constantes.STAT_MENOS_PM) 309 else 308, lanzador.getID().toString() + "", objetivo.getID().toString() + "," + (valor - perdidos))
                    }
                    if (perdidos >= 1) {
                        objetivo.addBuffConGIE(efectoID, perdidos, duracion, hechizoID,
                                convertirArgs(perdidos, this.efectoID, _args), lanzador, true, tipo, _condicionHechizo)
                    }
                }
                Constantes.STAT_MENOS_PM -> {
                    perdidos = getPuntosPerdidos(this.efectoID, valor, lanzador, objetivo)
                    perdidos = getMaxMinHechizo(objetivo, perdidos)
                    if (perdidos < valor) {
                        GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, if (efectoID == Constantes.STAT_MENOS_PM) 309 else 308, lanzador.getID().toString() + "", objetivo.getID().toString() + "," + (valor - perdidos))
                    }
                    if (perdidos >= 1) {
                        objetivo.addBuffConGIE(efectoID, perdidos, duracion, hechizoID,
                                convertirArgs(perdidos, this.efectoID, _args), lanzador, true, tipo, _condicionHechizo)
                    }
                }
                Constantes.STAT_MENOS_PA_FIJO, Constantes.STAT_MENOS_PM_FIJO -> temp.add(objetivo)
            }
            if (perdidos <= 0) {
                continue
            }
            if (afectados.length() > 0) {
                afectados.append("¬")
            }
            afectados.append(objetivo.getID().toString() + "," + -perdidos + "," + duracionFinal(objetivo))
        }
        if (afectados.length() > 0 && _condicionHechizo.isEmpty()) {
            GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, efectoID, lanzador.getID().toString() + "", afectados.toString())
        }
        if (!temp.isEmpty()) {
            pelea.addBuffLuchadores(temp, efectoID, valor, duracion, hechizoID, convertirArgs(valor, efectoID, _args),
                    lanzador, tipo, _condicionHechizo)
        }
    }

    protected fun efecto_Cura(objetivos: ArrayList<Luchador?>?, pelea: Pelea, lanzador: Luchador,
                              tipo: TipoDaño?) { // curacion
        // if (lanzador.tieneEstado(Constantes.ESTADO_ALTRUISTA)) {
        // return;
        // }
        val efectoID = getStatPorEfecto(efectoID)
        var modi = 0
        val perso: Personaje = lanzador.getPersonaje()
        if (perso != null) {
            if (perso.tieneModfiSetClase(hechizoID)) {
                modi = perso.getModifSetClase(hechizoID, 284)
            }
        }
        val cura2 = getRandomValor(lanzador)
        if (duracion == 0) {
            val afectados = StringBuilder()
            for (objetivo in objetivos) {
                if (objetivo.estaMuerto()) {
                    continue
                }
                // if (objetivo.tieneEstado(Constantes.ESTADO_ALTRUISTA)) {
                // continue;
                // }
                var paso = false
                for (b in objetivo.getBuffsPelea()) {
                    if (b.getCondicionBuff().isEmpty()) {
                        continue
                    }
                    if (b.getCondicionBuff().contains("SOIN")) {
                        paso = true
                        b.aplicarBuffCondicional(objetivo)
                        continue
                    }
                }
                if (paso) {
                    continue
                }
                var cura = getMaxMinHechizo(objetivo, cura2)
                cura = calcularCuraFinal(lanzador, cura) + modi
                restarPDVLuchador(pelea, objetivo, lanzador, afectados, -cura,
                        Constantes.getElementoPorEfectoID(this.efectoID))
            }
            if (afectados.length() > 0) {
                GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, lanzador.getID().toString() + "", afectados.toString())
            }
        } else {
            val temp: ArrayList<Luchador> = ArrayList<Luchador>()
            for (objetivo in objetivos) {
                if (objetivo.estaMuerto()) {
                    continue
                }
                temp.add(objetivo)
            }
            if (!temp.isEmpty()) {
                pelea.addBuffLuchadores(temp, efectoID, primerValor, duracion, hechizoID,
                        convertirArgs(primerValor, efectoID, _args), lanzador, tipo, _condicionHechizo)
            }
        }
    }

    private fun efecto_Cura_Porc_Vida_Objetivo(objetivos: ArrayList<Luchador?>?, pelea: Pelea,
                                               lanzador: Luchador, celdaObjetivo: Celda) {
        val efectoID = 108
        if (duracion == 0) {
            val porc = getRandomValor(lanzador)
            val afectados = StringBuilder()
            for (objetivo in objetivos) {
                if (objetivo.estaMuerto()) {
                    continue
                }
                val pdvMaxBuff: Int = objetivo.getPDVMaxConBuff()
                val cura = porc * pdvMaxBuff / 100
                restarPDVLuchador(pelea, objetivo, lanzador, afectados, -cura, 0)
            }
            if (afectados.length() > 0) {
                GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, lanzador.getID().toString() + "", afectados.toString())
            }
        } else {
            val temp: ArrayList<Luchador> = ArrayList<Luchador>()
            for (objetivo in objetivos) {
                if (objetivo.estaMuerto()) {
                    continue
                }
                temp.add(objetivo)
            }
            if (!temp.isEmpty()) {
                pelea.addBuffLuchadores(temp, efectoID, primerValor, duracion, hechizoID,
                        convertirArgs(primerValor, efectoID, _args), lanzador, TipoDaño.POST_TURNOS,
                        _condicionHechizo)
            }
        }
    }

    private fun efecto_Curar_Sin_Stats(objetivos: ArrayList<Luchador?>?, pelea: Pelea, lanzador: Luchador,
                                       celdaObjetivo: Celda) {
        if (duracion == 0) {
            val cura = getRandomValor(lanzador)
            val afectados = StringBuilder()
            for (objetivo in objetivos) {
                if (objetivo.estaMuerto()) {
                    continue
                }
                val curaTemp = getMaxMinHechizo(objetivo, cura)
                restarPDVLuchador(pelea, objetivo, lanzador, afectados, -curaTemp, 0)
            }
            if (afectados.length() > 0) {
                GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, lanzador.getID().toString() + "", afectados.toString())
            }
        } else {
            val efectoID = getStatPorEfecto(efectoID)
            val temp: ArrayList<Luchador> = ArrayList<Luchador>()
            for (objetivo in objetivos) {
                if (objetivo.estaMuerto()) {
                    continue
                }
                temp.add(objetivo)
            }
            if (!temp.isEmpty()) pelea.addBuffLuchadores(temp, efectoID, primerValor, duracion, hechizoID,
                    convertirArgs(primerValor, efectoID, _args), lanzador, TipoDaño.POST_TURNOS,
                    _condicionHechizo)
        }
    }

    private fun efecto_Dona_Porc_Vida(objetivos: ArrayList<Luchador?>?, pelea: Pelea, lanzador: Luchador,
                                      celdaObjetivo: Celda) {
        val efectoID = getStatPorEfecto(efectoID)
        if (duracion == 0) {
            val afectados = StringBuilder()
            val porc = getRandomValor(lanzador)
            var daño: Int = porc * lanzador.getPDVConBuff() / 100
            if (daño > lanzador.getPDVConBuff()) {
                daño = lanzador.getPDVConBuff() - 1
            }
            daño = restarPDVLuchador(pelea, lanzador, lanzador, afectados, daño, 0)
            for (objetivo in objetivos) {
                if (objetivo.estaMuerto()) {
                    continue
                }
                val cura = daño
                restarPDVLuchador(pelea, objetivo, lanzador, afectados, -cura, 0)
            }
            if (afectados.length() > 0) {
                GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, lanzador.getID().toString() + "", afectados.toString())
            }
        } else {
            val temp: ArrayList<Luchador> = ArrayList<Luchador>()
            for (objetivo in objetivos) {
                if (objetivo.estaMuerto()) {
                    continue
                }
                temp.add(objetivo)
            }
            if (!temp.isEmpty()) pelea.addBuffLuchadores(temp, efectoID, primerValor, duracion, hechizoID,
                    convertirArgs(primerValor, efectoID, _args), lanzador, TipoDaño.POST_TURNOS,
                    _condicionHechizo)
        }
    }

    // daños % de vida elemental --> tenia el ex CaC como true
    protected fun efecto_Daños_Porc_Elemental(objetivos: ArrayList<Luchador?>?, pelea: Pelea,
                                              lanzador: Luchador, tipo: TipoDaño, esGC: Boolean) {
        val efectoID = getStatPorEfecto(efectoID)
        if (duracion == 0) {
            quitarInvisibilidad(lanzador, tipo)
            val afectados = StringBuilder()
            for (objetivo in objetivos) {
                if (objetivo.estaMuerto()) {
                    continue
                }
                if (objetivo.tieneEstado(76)) {
                    if (Camino.distanciaDosCeldas(pelea.getMapaCopia(), objetivo.getCeldaPelea().getID(),
                                    lanzador.getCeldaPelea().getID()) <= 1) {
                        reenvioLejano = true
                        objetivo = reenvioHechizo(pelea, _nivelHechizoID, hechizoID, objetivo, lanzador, tipo, false)
                    }
                } else {
                    reenvioLejano = false
                    objetivo = reenvioHechizo(pelea, _nivelHechizoID, hechizoID, objetivo, lanzador, tipo, false)
                }
                objetivo = sacrificio(pelea, objetivo)
                var porc = getRandomValor(lanzador)
                porc = getMaxMinHechizo(objetivo, porc)
                var daño: Int = porc * lanzador.getPDVConBuff() / 100
                daño = getDañoAumentadoPorHechizo(lanzador, hechizoID, daño)
                daño = calcularDañoFinal(pelea, lanzador, objetivo, 10 + Constantes.getElementoPorEfectoID(this.efectoID),
                        daño.toFloat(), hechizoID, tipo, esGC)
                daño = dañoPorEspalda(pelea, lanzador, objetivo, daño)
                daño = aplicarBuffContraGolpe(efectoID, daño, objetivo, lanzador, pelea, hechizoID, tipo)
                daño = restarPDVLuchador(pelea, objetivo, lanzador, afectados, daño,
                        Constantes.getElementoPorEfectoID(this.efectoID))
                curaSiLoGolpeas(objetivo, lanzador, afectados, daño)
            }
            if (afectados.length() > 0) {
                GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, lanzador.getID().toString() + "", afectados.toString())
            }
        } else {
            val temp: ArrayList<Luchador> = ArrayList<Luchador>()
            for (objetivo in objetivos) {
                if (objetivo.estaMuerto()) {
                    continue
                }
                temp.add(objetivo)
            }
            if (!temp.isEmpty()) pelea.addBuffLuchadores(temp, efectoID, primerValor, duracion, hechizoID,
                    convertirArgs(primerValor, efectoID, _args), lanzador, tipo, _condicionHechizo)
        }
    }

    // roba PDV elementales
    protected fun efecto_Roba_PDV_Elemental(objetivos: ArrayList<Luchador?>?, pelea: Pelea,
                                            lanzador: Luchador, tipo: TipoDaño, esGC: Boolean) {
        var modi = 0
        val perso: Personaje = lanzador.getPersonaje()
        if (perso != null) {
            if (perso.tieneModfiSetClase(hechizoID)) {
                modi = perso.getModifSetClase(hechizoID, 283)
            }
        }
        if (duracion == 0) {
            quitarInvisibilidad(lanzador, tipo)
            val afectados = StringBuilder()
            for (objetivo in objetivos) {
                if (objetivo.estaMuerto()) {
                    continue
                }
                objetivo = reenvioHechizo(pelea, _nivelHechizoID, hechizoID, objetivo, lanzador, tipo, false)
                objetivo = sacrificio(pelea, objetivo)
                var daño = getRandomValor(lanzador)
                daño = getMaxMinHechizo(objetivo, daño)
                daño = getDañoAumentadoPorHechizo(lanzador, hechizoID, daño)
                daño = calcularDañoFinal(pelea, lanzador, objetivo, Constantes.getElementoPorEfectoID(efectoID), daño.toFloat(),
                        hechizoID, tipo, esGC)
                daño += modi
                dañoPorEspalda(pelea, lanzador, objetivo, daño)
                daño = aplicarBuffContraGolpe(efectoID, daño, objetivo, lanzador, pelea, hechizoID, tipo)
                daño = restarPDVLuchador(pelea, objetivo, lanzador, afectados, daño,
                        Constantes.getElementoPorEfectoID(efectoID))
                if (daño > 0 && !tipo.equals(TipoDaño.POST_TURNOS)) restarPDVLuchador(pelea, lanzador, lanzador, afectados, -daño / 2,
                        Constantes.getElementoPorEfectoID(efectoID))
                curaSiLoGolpeas(objetivo, lanzador, afectados, daño)
            }
            if (afectados.length() > 0) {
                GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, lanzador.getID().toString() + "", afectados.toString())
            }
        } else {
            val temp: ArrayList<Luchador> = ArrayList<Luchador>()
            for (objetivo in objetivos) {
                if (objetivo.estaMuerto()) {
                    continue
                }
                temp.add(objetivo)
            }
            if (!temp.isEmpty()) pelea.addBuffLuchadores(temp, efectoID, primerValor, duracion, hechizoID,
                    convertirArgs(primerValor, efectoID, _args), lanzador, tipo, _condicionHechizo)
        }
    }

    var valor = 0

    // daños elementales por parte de los hechizos
    protected fun efecto_Daños_Elemental(objetivos: ArrayList<Luchador?>?, pelea: Pelea,
                                         lanzador: Luchador, tipo: TipoDaño, esGC: Boolean) {
        val efectoID = getStatPorEfecto(efectoID)
        var modi = 0
        val perso: Personaje = lanzador.getPersonaje()
        if (perso != null) {
            if (perso.tieneModfiSetClase(hechizoID)) {
                modi = perso.getModifSetClase(hechizoID, 283)
            }
        }
        if (duracion == 0) {
            quitarInvisibilidad(lanzador, tipo)
            val afectados = StringBuilder()
            for (objetivo in objetivos) {
                if (objetivo.estaMuerto()) {
                    continue
                }
                if (objetivo.tieneEstado(76)) {
                    if (Camino.distanciaDosCeldas(pelea.getMapaCopia(), objetivo.getCeldaPelea().getID(),
                                    lanzador.getCeldaPelea().getID()) <= 1) {
                        reenvioLejano = true
                        objetivo = reenvioHechizo(pelea, _nivelHechizoID, hechizoID, objetivo, lanzador, tipo, true)
                    }
                } else {
                    reenvioLejano = false
                    objetivo = reenvioHechizo(pelea, _nivelHechizoID, hechizoID, objetivo, lanzador, tipo, false)
                }
                objetivo = sacrificio(pelea, objetivo)
                var daño = getRandomValor(lanzador)
                daño = getMaxMinHechizo(objetivo, daño)
                daño = getDañoAumentadoPorHechizo(lanzador, hechizoID, daño)
                daño = calcularDañoFinal(pelea, lanzador, objetivo, Constantes.getElementoPorEfectoID(this.efectoID), daño.toFloat(),
                        hechizoID, tipo, esGC)
                daño += modi
                dañoPorEspalda(pelea, lanzador, objetivo, daño)
                daño = aplicarBuffContraGolpe(efectoID, daño, objetivo, lanzador, pelea, hechizoID, tipo)
                daño = restarPDVLuchador(pelea, objetivo, lanzador, afectados, daño,
                        Constantes.getElementoPorEfectoID(this.efectoID))
                curaSiLoGolpeas(objetivo, lanzador, afectados, daño)
            }
            if (afectados.length() > 0) {
                GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, lanzador.getID().toString() + "", afectados.toString())
            }
        } else {
            val temp: ArrayList<Luchador> = ArrayList<Luchador>()
            for (objetivo in objetivos) {
                if (objetivo.estaMuerto()) {
                    continue
                }
                temp.add(objetivo)
            }
            if (!temp.isEmpty()) pelea.addBuffLuchadores(temp, efectoID, primerValor, duracion, hechizoID,
                    convertirArgs(primerValor, efectoID, _args), lanzador, tipo, _condicionHechizo)
        }
    }

    protected fun efecto_Daños_Elemental_lejos(objetivos: ArrayList<Luchador?>?, pelea: Pelea,
                                               lanzador: Luchador, tipo: TipoDaño, esGC: Boolean) {
        val efectoID = getStatPorEfecto(efectoID)
        var modi = 0
        val perso: Personaje = lanzador.getPersonaje()
        if (perso != null) {
            if (perso.tieneModfiSetClase(hechizoID)) {
                modi = perso.getModifSetClase(hechizoID, 283)
            }
        }
        if (duracion == 0) {
            quitarInvisibilidad(lanzador, tipo)
            val afectados = StringBuilder()
            for (objetivo in objetivos) {
                if (objetivo.estaMuerto()) {
                    continue
                }
                objetivo = reenvioHechizo(pelea, _nivelHechizoID, hechizoID, objetivo, lanzador, tipo, false)
                objetivo = sacrificio(pelea, objetivo)
                var lugar: Int = Camino.distanciaDosCeldas(pelea.getMapaCopia(), objetivo.getCeldaPelea().getID(),
                        lanzador.getCeldaPelea().getID())
                lugar = lugar - 1
                var inicio = 0
                var coef = 1.0f
                while (inicio < lugar) {
                    inicio++
                    coef = coef + 0.1f
                }
                var daño = getRandomValor(lanzador)
                daño = getMaxMinHechizo(objetivo, daño)
                daño = getDañoAumentadoPorHechizo(lanzador, hechizoID, daño)
                daño = calcularDañoFinal(pelea, lanzador, objetivo, Constantes.getElementoPorEfectoID(this.efectoID), daño.toFloat(),
                        hechizoID, tipo, esGC)
                daño += modi
                daño = (daño * coef).toInt()
                dañoPorEspalda(pelea, lanzador, objetivo, daño)
                daño = aplicarBuffContraGolpe(efectoID, daño, objetivo, lanzador, pelea, hechizoID, tipo)
                daño = restarPDVLuchador(pelea, objetivo, lanzador, afectados, daño,
                        Constantes.getElementoPorEfectoID(this.efectoID))
                curaSiLoGolpeas(objetivo, lanzador, afectados, daño)
            }
            if (afectados.length() > 0) {
                GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, lanzador.getID().toString() + "", afectados.toString())
            }
        } else {
            val temp: ArrayList<Luchador> = ArrayList<Luchador>()
            for (objetivo in objetivos) {
                if (objetivo.estaMuerto()) {
                    continue
                }
                temp.add(objetivo)
            }
            if (!temp.isEmpty()) pelea.addBuffLuchadores(temp, efectoID, primerValor, duracion, hechizoID,
                    convertirArgs(primerValor, efectoID, _args), lanzador, tipo, _condicionHechizo)
        }
    }

    // daños para el lanzador (fixe) (FIJOS)--> no sacrificio, no reenvio
    private fun efecto_Daños_Para_Lanzador(pelea: Pelea, lanzador: Luchador, celdaObjetivo: Celda,
                                           tipo: TipoDaño, esGC: Boolean) {
        val efectoID = getStatPorEfecto(efectoID)
        if (duracion == 0) {
            quitarInvisibilidad(lanzador, tipo)
            var daño = getRandomValor(lanzador)
            daño = calcularDañoFinal(pelea, lanzador, lanzador, Constantes.getElementoPorEfectoID(this.efectoID), daño.toFloat(),
                    hechizoID, tipo, esGC)
            daño = aplicarBuffContraGolpe(efectoID, daño, lanzador, lanzador, pelea, hechizoID, tipo)
            daño = restarPDVLuchador(pelea, lanzador, lanzador, null, daño,
                    Constantes.getElementoPorEfectoID(this.efectoID))
        } else {
            lanzador.addBuffConGIE(efectoID, primerValor, duracion, hechizoID,
                    convertirArgs(primerValor, efectoID, _args), lanzador, true, tipo, _condicionHechizo)
        }
    }

    private fun efecto_Daños_Porc_Vida_Neutral(objetivos: ArrayList<Luchador?>?, pelea: Pelea,
                                               lanzador: Luchador, celdaObjetivo: Celda, tipo: TipoDaño, esGC: Boolean) {
        val `val` = getRandomValor(lanzador) / 100f
        val pdvMax: Int = lanzador.getPDVMaxConBuff()
        val pdvMedio = pdvMax / 2
        var porc = 1f
        if (efectoID == 672) { // enemigos
            porc = 1 - Math.abs(lanzador.getPDVConBuff() - pdvMedio) / pdvMedio.toFloat()
        }
        val daño = (`val` * pdvMax * porc).toInt()
        quitarInvisibilidad(lanzador, tipo)
        val afectados = StringBuilder()
        for (objetivo in objetivos) {
            if (objetivo.estaMuerto()) {
                continue
            }
            if (objetivo.tieneEstado(76)) {
                if (Camino.distanciaDosCeldas(pelea.getMapaCopia(), objetivo.getCeldaPelea().getID(),
                                lanzador.getCeldaPelea().getID()) <= 1) {
                    reenvioLejano = true
                    objetivo = reenvioHechizo(pelea, _nivelHechizoID, hechizoID, objetivo, lanzador, tipo, true)
                }
            } else {
                reenvioLejano = false
                objetivo = reenvioHechizo(pelea, _nivelHechizoID, hechizoID, objetivo, lanzador, tipo, false)
            }
            objetivo = sacrificio(pelea, objetivo)
            var daño2 = calcularDañoFinal(pelea, lanzador, objetivo, (10 + Constantes.ELEMENTO_NEUTRAL) as Byte.toInt(), daño.toFloat().toInt(),
                    hechizoID, tipo, esGC)
            daño2 = aplicarBuffContraGolpe(efectoID, daño2, objetivo, lanzador, pelea, hechizoID, tipo)
            daño2 = restarPDVLuchador(pelea, objetivo, lanzador, afectados, daño2,
                    Constantes.getElementoPorEfectoID(efectoID))
        }
        if (afectados.length() > 0) {
            GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, lanzador.getID().toString() + "",
                    afectados.toString().toString() + "," + Constantes.getElementoPorEfectoID(efectoID))
        }
    }

    private fun efecto_Matar_Objetivo(objetivos: ArrayList<Luchador?>?, pelea: Pelea, lanzador: Luchador,
                                      celdaObjetivo: Celda) {
        quitarInvisibilidad(lanzador, TipoDaño.NORMAL)
        for (objetivo in objetivos) {
            if (objetivo.estaMuerto()) {
                continue
            }
            if (duracion == 0) {
                objetivo = sacrificio(pelea, objetivo)
            }
            pelea.addMuertosReturnFinalizo(objetivo, lanzador)
            try {
                Thread.sleep(500)
            } catch (e: Exception) {
            }
        }
    }

    private fun efecto_Dominio_Arma(objetivos: ArrayList<Luchador?>?, pelea: Pelea, lanzador: Luchador,
                                    celdaObjetivo: Celda) {
        val efectoID = getStatPorEfecto(efectoID)
        val temp: ArrayList<Luchador> = ArrayList<Luchador>()
        for (objetivo in objetivos) {
            if (objetivo.estaMuerto()) {
                continue
            }
            temp.add(objetivo)
        }
        if (!temp.isEmpty()) {
            pelea.addBuffLuchadores(temp, efectoID, primerValor, duracion, hechizoID,
                    convertirArgs(primerValor, efectoID, _args), lanzador, TipoDaño.POST_TURNOS, _condicionHechizo)
        }
    }

    private fun efecto_Bonus_Malus(objetivos: ArrayList<Luchador?>?, pelea: Pelea, lanzador: Luchador,
                                   celdaObjetivo: Celda, tipo: TipoDaño) {
        // solo tiene los mas pa y mas pm, no esta los menos
        when (hechizoID) {
            2210 -> if (celdaObjetivo.getPrimerLuchador() == null) {
                return
            }
        }
        var valor = getRandomValor(lanzador)
        val efectoID = getStatPorEfecto(efectoID)
        val valor2 = valor
        val temp: ArrayList<Luchador> = ArrayList<Luchador>()
        val afectados = StringBuilder()
        for (objetivo in objetivos) {
            if (objetivo.estaMuerto()) {
                continue
            }
            valor = getMaxMinHechizo(objetivo, valor2)
            if (!objetivo.esInvocacion()) {
                if (validar(objetivo.getPelea()) && MainServidor.LIMITE_BONUS_2.containsKey(efectoID)
                        && valor <= MainServidor.LIMITE_BONUS_VALOR.get(efectoID)) {
                    valor = Math.min(valor, MainServidor.LIMITE_BONUS_2.get(efectoID))
                }
            }
            if (valor != valor2) {
                objetivo.addBuffConGIE(efectoID, valor, duracion, hechizoID, convertirArgs(valor, efectoID, _args),
                        lanzador, true, tipo, _condicionHechizo)
            } else {
                temp.add(objetivo)
            }
            when (this.efectoID) {
                78, 111, 128, 120, 110, 112, 114, 115, 116, 117, 118, 119, 121, 122, 123, 124, 125, 126, 138, 142, 144, 145, 152, 153, 154, 155, 156, 157, 160, 161, 162, 163, 171, 176, 177, 178, 179, 182, 183, 184, 186, 425, 606, 607, 608, 609, 610, 611, 776 -> {
                    if (afectados.length() > 0) {
                        afectados.append("¬")
                    }
                    afectados.append(objetivo.getID().toString() + "," + valor + "," + duracionFinal(objetivo))
                }
            }
        }
        if (afectados.length() > 0 && _condicionHechizo.isEmpty()) {
            GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, this.efectoID, lanzador.getID().toString() + "", afectados.toString())
        }
        if (!temp.isEmpty()) {
            pelea.addBuffLuchadores(temp, efectoID, valor, duracion, hechizoID, convertirArgs(valor, efectoID, _args),
                    lanzador, tipo, _condicionHechizo)
        }
    }

    private fun efecto_Reenvio_Hechizo(objetivos: ArrayList<Luchador?>?, pelea: Pelea, lanzador: Luchador,
                                       celdaObjetivo: Celda) {
        val nivelMax = segundoValor
        val efectoID = getStatPorEfecto(efectoID)
        if (nivelMax == -1) {
            return
        }
        val temp: ArrayList<Luchador> = ArrayList<Luchador>()
        for (objetivo in objetivos) {
            if (objetivo.estaMuerto()) {
                continue
            }
            temp.add(objetivo)
        }
        if (!temp.isEmpty()) pelea.addBuffLuchadores(temp, efectoID, nivelMax, duracion, hechizoID,
                convertirArgs(nivelMax, efectoID, _args), lanzador, TipoDaño.POST_TURNOS, _condicionHechizo)
        try {
            Thread.sleep(200)
        } catch (e: Exception) {
        }
    }

    private fun efecto_Deshechizar(objetivos: ArrayList<Luchador?>?, pelea: Pelea, lanzador: Luchador,
                                   celdaObjetivo: Celda) { // deshechiza
        val afectados = StringBuilder()
        for (objetivo in objetivos) {
            if (objetivo.estaMuerto()) {
                continue
            }
            if (afectados.length() > 0) {
                afectados.append("¬")
            }
            afectados.append(objetivo.getID().toString() + "")
        }
        if (afectados.length() > 0 && _condicionHechizo.isEmpty()) {
            GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, efectoID, lanzador.getID().toString() + "", afectados.toString())
            for (objetivo in objetivos) {
                if (objetivo.estaMuerto()) {
                    continue
                }
                objetivo.deshechizar(lanzador, true)
            }
        }
    }

    // cambia la apariencia
    private fun efecto_Cambiar_Apariencia(objetivos: ArrayList<Luchador?>?, pelea: Pelea, lanzador: Luchador,
                                          celdaObjetivo: Celda) {
        var gfxID = tercerValor
        val efectoID = getStatPorEfecto(efectoID)
        val temp: ArrayList<Luchador> = ArrayList<Luchador>()
        quitarInvisibilidad(lanzador, TipoDaño.NORMAL)
        for (objetivo in objetivos) {
            if (objetivo.estaMuerto()) {
                continue
            }
            temp.add(objetivo)
            if (gfxID == 8010 && objetivo.getPersonaje() != null && objetivo.getPersonaje().getSexo() === Constantes.SEXO_FEMENINO) {
                gfxID = 8011
            }
            if (gfxID == -1 || duracion == 0) {
                GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, this.efectoID, lanzador.getID().toString() + "",
                        objetivo.getID().toString() + "," + objetivo.getGfxID() + "," + objetivo.getGfxID())
            } else {
                GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, this.efectoID, lanzador.getID().toString() + "",
                        objetivo.getID().toString() + "," + objetivo.getGfxID() + "," + gfxID + "," + duracionFinal(objetivo))
            }
        }
        if (gfxID > -1 && duracion != 0) {
            if (!temp.isEmpty()) pelea.addBuffLuchadores(temp, efectoID, gfxID, duracion, hechizoID,
                    convertirArgs(gfxID, efectoID, _args), lanzador, TipoDaño.POST_TURNOS, _condicionHechizo)
        }
    }

    // vuelve invisible a un pj
    private fun efecto_Invisibilidad(objetivos: ArrayList<Luchador?>?, pelea: Pelea, lanzador: Luchador,
                                     celdaObjetivo: Celda) {
        val efectoID = getStatPorEfecto(efectoID)
        val temp: ArrayList<Luchador> = ArrayList<Luchador>()
        val afectados = StringBuilder()
        for (objetivo in objetivos) {
            if (objetivo.estaMuerto()) {
                continue
            }
            temp.add(objetivo)
            objetivo.vaciarVisibles()
            if (afectados.length() > 0) afectados.append("¬")
            afectados.append(objetivo.getID().toString() + "," + duracionFinal(objetivo))
        }
        if (afectados.length() > 0 && _condicionHechizo.isEmpty()) {
            GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, this.efectoID, lanzador.getID().toString() + "", afectados.toString())
        }
        if (!temp.isEmpty()) pelea.addBuffLuchadores(temp, efectoID, primerValor, duracion, hechizoID,
                convertirArgs(primerValor, efectoID, _args), lanzador, TipoDaño.POST_TURNOS, _condicionHechizo)
    }

    // invocar doble
    private fun efecto_Invoca_Doble(pelea: Pelea, lanzador: Luchador, celdaObjetivo: Celda) {
        if (lanzador.getNroInvocaciones() >= lanzador.getTotalStats().getTotalStatParaMostrar(182, lanzador.getPelea(),
                        null)) {
            GestorSalida.ENVIAR_Im_INFORMACION(lanzador.getPersonaje(),
                    "0CANT_SUMMON_MORE_CREATURE;" + lanzador.getNroInvocaciones())
            return
        }
        if (!celdaObjetivo.esCaminable(true)) {
            GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(pelea, 7, "1CELDA_NO_CAMINABLE")
            GestorSalida.ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(pelea, 7, lanzador.getID(), celdaObjetivo.getID())
            return
        }
        if (celdaObjetivo.getPrimerLuchador() != null) {
            GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 151, lanzador.getID().toString() + "", hechizoID.toString() + "")
            GestorSalida.ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(pelea, 7, lanzador.getID(), celdaObjetivo.getID())
            return
        }
        val doble: Luchador = lanzador.clonarLuchador(pelea.getSigIDLuchador())
        doble.setEquipoBin(lanzador.getEquipoBin())
        doble.setInvocador(lanzador)
        doble.setPDVMAX(lanzador.getPDVMaxSinBuff(), false)
        doble.setPDV(lanzador.getPDVMaxSinBuff())
        doble.setCeldaPelea(celdaObjetivo)
        pelea.getOrdenLuchadores().add(pelea.getOrdenLuchadores().indexOf(lanzador) + 1, doble)
        pelea.addLuchadorEnEquipo(doble, lanzador.getEquipoBin())
        GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 181, lanzador.getID().toString() + "", "+" + doble.stringGM(0))
        GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 999, lanzador.getID().toString() + "", pelea.stringOrdenJugadores())
        lanzador.addNroInvocaciones(1)
        try {
            Thread.sleep(1000)
        } catch (e: Exception) {
        }
        verificaTrampas(doble)
        // pelea.actualizarNumTurnos(null);
    }

    private fun efecto_Invoca_Mob_controlable(pelea: Pelea, lanzador: Luchador, celdaObjetivo: Celda) {
        if (lanzador.getNroInvocaciones() >= lanzador.getTotalStats()
                        .getTotalStatParaMostrar(Constantes.STAT_MAS_CRIATURAS_INVO, lanzador.getPelea(), null)) {
            GestorSalida.ENVIAR_Im_INFORMACION(lanzador.getPersonaje(),
                    "0CANT_SUMMON_MORE_CREATURE;" + lanzador.getNroInvocaciones())
            return
        }
        if (efectoID == 405) { // mata para invocar
            if (celdaObjetivo.getPrimerLuchador() != null) {
                pelea.addMuertosReturnFinalizo(celdaObjetivo.getPrimerLuchador(), lanzador)
                try {
                    Thread.sleep(1000)
                } catch (ignored: Exception) {
                }
            }
        }
        if (!celdaObjetivo.esCaminable(true)) {
            GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(pelea, 7, "1CELDA_NO_CAMINABLE")
            GestorSalida.ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(pelea, 7, lanzador.getID(), celdaObjetivo.getID())
            return
        }
        if (celdaObjetivo.getPrimerLuchador() != null) {
            GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 151, lanzador.getID().toString() + "", hechizoID.toString() + "")
            GestorSalida.ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(pelea, 7, lanzador.getID(), celdaObjetivo.getID())
            return
        }
        var mobID = 0
        var mobNivel: Byte = 0
        try {
            mobID = Integer.parseInt(_args.split(",").get(0))
            mobNivel = Byte.parseByte(_args.split(",").get(1))
        } catch (ignored: Exception) {
        }
        var mob: MobGrado? = null
        val idInvocacion: Int = pelea.getSigIDLuchador()
        mob = try {
            Mundo.getMobModelo(mobID).getGradoPorGrado(mobNivel).invocarMob(idInvocacion, false, lanzador)
        } catch (e: Exception) {
            MainServidor.redactarLogServidorln("El Mob ID esta reparandose: $mobID")
            return
        }
        val mobControlable: Personaje = Personaje.crearInvoControlable(lanzador.getPelea().getSigIDLuchador(), mob)
        val invocacion = Luchador(pelea, mobControlable, false)
        try {
            lanzador.getPersonaje().addCompañero(invocacion.getPersonaje())
            invocacion.getPersonaje().addCompañero(lanzador.getPersonaje())
            invocacion.setInvocacion(true)
            invocacion.setIDReal(false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        invocacion.setEquipoBin(lanzador.getEquipoBin())
        invocacion.setInvocador(lanzador)
        invocacion.setCeldaPelea(celdaObjetivo)
        pelea.getOrdenLuchadores().add(pelea.getOrdenLuchadores().indexOf(lanzador) + 1, invocacion)
        pelea.addLuchadorEnEquipo(invocacion, lanzador.getEquipoBin())
        GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 181, lanzador.getID().toString() + "", "+" + invocacion.stringGM(0))
        GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 999, lanzador.getID().toString() + "", pelea.stringOrdenJugadores())
        lanzador.addNroInvocaciones(1)
        if (MainServidor.PARAM_MOSTRAR_STATS_INVOCACION) {
            val str = StringBuilder()
            str.append("<b>LOS STATS DE INVOCACION SON: [</b>")
            str.append("<font color='#d3a900'><b>FO:</b>" + invocacion.getTotalStats()
                    .getTotalStatParaMostrar(Constantes.STAT_MAS_FUERZA, invocacion.getPelea(), null).toString() + "</font>, ")
            str.append("<font color='#FF0000'><b>INT:</b> " + invocacion.getTotalStats().getTotalStatParaMostrar(
                    Constantes.STAT_MAS_INTELIGENCIA, invocacion.getPelea(), null).toString() + "</font>, ")
            str.append("<font color='#00e63c'><b>AGI:</b> " + invocacion.getTotalStats()
                    .getTotalStatParaMostrar(Constantes.STAT_MAS_AGILIDAD, invocacion.getPelea(), null).toString() + "</font>, ")
            str.append("<font color='#00deff'><b>CHA:</b> " + invocacion.getTotalStats().getTotalStatParaMostrar(
                    Constantes.STAT_MAS_SUERTE, invocacion.getPelea(), null).toString() + "<b></font>]</b>")
            GestorSalida.ENVIAR_cs_CHAT_MENSAJE_A_PELEA(pelea, str.toString(), Constantes.COLOR_VERDE_CLARO)
        }
        try {
            Thread.sleep(1000)
        } catch (ignored: Exception) {
        }
        verificaTrampas(invocacion)
        // pelea.actualizarNumTurnos(null);
    }

    // invocar una criatura
    private fun efecto_Invoca_Mob(pelea: Pelea, lanzador: Luchador, celdaObjetivo: Celda) {
        if (lanzador.getNroInvocaciones() >= lanzador.getTotalStats()
                        .getTotalStatParaMostrar(Constantes.STAT_MAS_CRIATURAS_INVO, lanzador.getPelea(), null)) {
            GestorSalida.ENVIAR_Im_INFORMACION(lanzador.getPersonaje(),
                    "0CANT_SUMMON_MORE_CREATURE;" + lanzador.getNroInvocaciones())
            return
        }
        if (efectoID == 405) { // mata para invocar
            if (celdaObjetivo.getPrimerLuchador() != null) {
                pelea.addMuertosReturnFinalizo(celdaObjetivo.getPrimerLuchador(), lanzador)
                try {
                    Thread.sleep(1000)
                } catch (e: Exception) {
                }
            }
        }
        if (!celdaObjetivo.esCaminable(true)) {
            GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(pelea, 7, "1CELDA_NO_CAMINABLE")
            GestorSalida.ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(pelea, 7, lanzador.getID(), celdaObjetivo.getID())
            return
        }
        if (celdaObjetivo.getPrimerLuchador() != null) {
            GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 151, lanzador.getID().toString() + "", hechizoID.toString() + "")
            GestorSalida.ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(pelea, 7, lanzador.getID(), celdaObjetivo.getID())
            return
        }
        var mobID = 0
        var mobNivel: Byte = 0
        try {
            mobID = Integer.parseInt(_args.split(",").get(0))
            mobNivel = Byte.parseByte(_args.split(",").get(1))
        } catch (e: Exception) {
        }
        var mob: MobGrado? = null
        val idInvocacion: Int = pelea.getSigIDLuchador()
        mob = try {
            Mundo.getMobModelo(mobID).getGradoPorGrado(mobNivel).invocarMob(idInvocacion, false, lanzador)
        } catch (e: Exception) {
            MainServidor.redactarLogServidorln("El Mob ID esta reparandose: $mobID")
            return
        }
        val invocacion = Luchador(pelea, mob, false)
        // invocacion.getMob().getIDModelo()
        invocacion.setEquipoBin(lanzador.getEquipoBin())
        invocacion.setInvocador(lanzador)
        invocacion.setCeldaPelea(celdaObjetivo)
        pelea.getOrdenLuchadores().add(pelea.getOrdenLuchadores().indexOf(lanzador) + 1, invocacion)
        pelea.addLuchadorEnEquipo(invocacion, lanzador.getEquipoBin())
        GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 181, lanzador.getID().toString() + "", "+" + invocacion.stringGM(0))
        GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 999, lanzador.getID().toString() + "", pelea.stringOrdenJugadores())
        lanzador.addNroInvocaciones(1)
        if (MainServidor.PARAM_MOSTRAR_STATS_INVOCACION) {
            val str = StringBuilder()
            str.append("<b>LOS STATS DE INVOCACION SON: [</b>")
            str.append("<font color='#d3a900'><b>FO:</b>" + invocacion.getTotalStats()
                    .getTotalStatParaMostrar(Constantes.STAT_MAS_FUERZA, invocacion.getPelea(), null).toString() + "</font>, ")
            str.append("<font color='#FF0000'><b>INT:</b> " + invocacion.getTotalStats().getTotalStatParaMostrar(
                    Constantes.STAT_MAS_INTELIGENCIA, invocacion.getPelea(), null).toString() + "</font>, ")
            str.append("<font color='#00e63c'><b>AGI:</b> " + invocacion.getTotalStats()
                    .getTotalStatParaMostrar(Constantes.STAT_MAS_AGILIDAD, invocacion.getPelea(), null).toString() + "</font>, ")
            str.append("<font color='#00deff'><b>CHA:</b> " + invocacion.getTotalStats().getTotalStatParaMostrar(
                    Constantes.STAT_MAS_SUERTE, invocacion.getPelea(), null).toString() + "<b></font>]</b>")
            GestorSalida.ENVIAR_cs_CHAT_MENSAJE_A_PELEA(pelea, str.toString(), Constantes.COLOR_VERDE_CLARO)
        }
        try {
            Thread.sleep(1000)
        } catch (e: Exception) {
        }
        when (mobID) {
            556, 282 -> {
                invocacion.setEstatico(true)
                invocacion.setSirveParaBuff(false)
            }
            2750 -> invocacion.setEstatico(true)
        }
        verificaTrampas(invocacion)
        // pelea.actualizarNumTurnos(null);
    }

    private fun efecto_Invoca_Mob_celda_aleatoria(pelea: Pelea, lanzador: Luchador, celdaObjetivo: Celda) {
        var celdaObjetivo: Celda? = celdaObjetivo
        if (lanzador.getNroInvocaciones() >= lanzador.getTotalStats()
                        .getTotalStatParaMostrar(Constantes.STAT_MAS_CRIATURAS_INVO, lanzador.getPelea(), null)) {
            GestorSalida.ENVIAR_Im_INFORMACION(lanzador.getPersonaje(),
                    "0CANT_SUMMON_MORE_CREATURE;" + lanzador.getNroInvocaciones())
            return
        }
        val mapa: Mapa = celdaObjetivo.getMapa()
        celdaObjetivo = mapa.getRandomCeldaIDLibrePelea()
        if (celdaObjetivo == null) {
            GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(pelea, 7, "1CELDA_NO_CAMINABLE")
            return
        }
        if (efectoID == 405) { // mata para invocar
            if (celdaObjetivo.getPrimerLuchador() != null) {
                pelea.addMuertosReturnFinalizo(celdaObjetivo.getPrimerLuchador(), lanzador)
                try {
                    Thread.sleep(1000)
                } catch (e: Exception) {
                }
            }
        }
        if (!celdaObjetivo.esCaminable(true)) {
            GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(pelea, 7, "1CELDA_NO_CAMINABLE")
            GestorSalida.ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(pelea, 7, lanzador.getID(), celdaObjetivo.getID())
            return
        }
        if (celdaObjetivo.getPrimerLuchador() != null) {
            GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 151, lanzador.getID().toString() + "", hechizoID.toString() + "")
            GestorSalida.ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(pelea, 7, lanzador.getID(), celdaObjetivo.getID())
            return
        }
        var mobID = 0
        var mobNivel: Byte = 0
        try {
            mobID = Integer.parseInt(_args.split(",").get(0))
            mobNivel = Byte.parseByte(_args.split(",").get(1))
        } catch (e: Exception) {
        }
        var mob: MobGrado? = null
        val idInvocacion: Int = pelea.getSigIDLuchador()
        mob = try {
            Mundo.getMobModelo(mobID).getGradoPorGrado(mobNivel).invocarMob(idInvocacion, false, lanzador)
        } catch (e: Exception) {
            MainServidor.redactarLogServidorln("El Mob ID esta reparandose: $mobID")
            return
        }
        val invocacion = Luchador(pelea, mob, false)
        // invocacion.getMob().getIDModelo()
        invocacion.setEquipoBin(lanzador.getEquipoBin())
        invocacion.invocacionNoLatigo = true
        invocacion.setInvocador(lanzador)
        invocacion.setCeldaPelea(celdaObjetivo)
        pelea.getOrdenLuchadores().add(pelea.getOrdenLuchadores().indexOf(lanzador) + 1, invocacion)
        pelea.addLuchadorEnEquipo(invocacion, lanzador.getEquipoBin())
        GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 181, lanzador.getID().toString() + "", "+" + invocacion.stringGM(0))
        GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 999, lanzador.getID().toString() + "", pelea.stringOrdenJugadores())
        lanzador.addNroInvocaciones(1)
        try {
            Thread.sleep(1000)
        } catch (e: Exception) {
        }
        when (mobID) {
            556, 282 -> {
                invocacion.setEstatico(true)
                invocacion.setSirveParaBuff(false)
            }
            2750 -> invocacion.setEstatico(true)
        }
        verificaTrampas(invocacion)
        // pelea.actualizarNumTurnos(null);
    }

    // invoca una criatura estatica
    private fun efecto_Invoca_Estatico(pelea: Pelea, lanzador: Luchador, celdaObjetivo: Celda) {
        // if (lanzador.getNroInvocaciones() >=
        // lanzador.getTotalStats().getStatParaMostrar(
        // CentroInfo.STAT_MAS_CRIATURAS_INVO)) {
        // GestorSalida.ENVIAR_Im_INFORMACION(lanzador.getPersonaje(),
        // "0CANT_SUMMON_MORE_CREATURE;"
        // + lanzador.getNroInvocaciones());
        // return;
        // }
        if (!celdaObjetivo.esCaminable(true)) {
            GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(pelea, 7, "1CELDA_NO_CAMINABLE")
            GestorSalida.ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(pelea, 7, lanzador.getID(), celdaObjetivo.getID())
            return
        }
        if (celdaObjetivo.getPrimerLuchador() != null) {
            GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 151, lanzador.getID().toString() + "", hechizoID.toString() + "")
            GestorSalida.ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(pelea, 7, lanzador.getID(), celdaObjetivo.getID())
            return
        }
        var mobID = 0
        var mobnivel: Byte = 0
        try {
            mobID = Integer.parseInt(_args.split(",").get(0))
            mobnivel = Byte.parseByte(_args.split(",").get(1))
        } catch (e: Exception) {
        }
        var mob: MobGrado? = null
        val idInvocacion: Int = pelea.getSigIDLuchador()
        mob = try {
            Mundo.getMobModelo(mobID).getGradoPorGrado(mobnivel).invocarMob(idInvocacion, false, lanzador)
        } catch (e: Exception) {
            MainServidor.redactarLogServidorln("El Mob ID esta mal configurado: $mobID")
            return
        }
        val invocacion = Luchador(pelea, mob, false)
        val equipoLanz: Byte = lanzador.getEquipoBin()
        invocacion.setEquipoBin(equipoLanz)
        invocacion.setInvocador(lanzador)
        invocacion.setEstatico(true)
        invocacion.setSirveParaBuff(false)
        invocacion.setEstado(Constantes.ESTADO_PESADO, 10000)
        invocacion.setCeldaPelea(celdaObjetivo)
        pelea.addLuchadorEnEquipo(invocacion, equipoLanz)
        GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 185, lanzador.getID().toString() + "", "+" + invocacion.stringGM(0))
        // lanzador.aumentarInvocaciones(); supuestamente no cuenta para estas
        try {
            Thread.sleep(1000)
        } catch (e: Exception) {
        }
        // invocacion.setEstado(Constantes.ESTADO_ARRAIGADO, -1);
        // invocacion.setEstado(Constantes.ESTADO_PESADO, -1);
        verificaTrampas(invocacion)
    }

    // invoca a un aliado muerto en combate, revivir
    private fun efecto_Resucitar(pelea: Pelea, lanzador: Luchador, celdaObjetivo: Celda) {
        var objetivo: Luchador? = null
        for (i in pelea.getListaMuertos().size() - 1 downTo 0) {
            val muerto: Luchador = pelea.getListaMuertos().get(i)
            if (muerto.estaRetirado()) {
                continue
            }
            if (muerto.getEquipoBin() === lanzador.getEquipoBin()) {
                if (muerto.esInvocacion()) {
                    if (muerto.getInvocador().estaMuerto()) {
                        continue
                    }
                }
                objetivo = muerto
                break
            }
        }
        if (objetivo == null) {
            return
        }
        objetivo.setEstaMuerto(false)
        objetivo.setCeldaPelea(celdaObjetivo)
        objetivo.getBuffsPelea().clear()
        pelea.getListaMuertos().remove(objetivo)
        var vida = 0
        var vidaMax = 0
        try {
            vida = primerValor * objetivo.getPersonaje().getPDVMax() / 100
            vidaMax = objetivo.getPersonaje().getPDVMax()
        } catch (e: Exception) {
        }
        try {
            vida = primerValor * objetivo.getMob().getPDVMax() / 100
            vidaMax = objetivo.getMob().getPDVMax()
        } catch (e: Exception) {
        }
        val iniciador: Boolean = pelea.esLuchInicioPelea(objetivo)
        if (!iniciador) {
            pelea.getOrdenLuchadores().add(pelea.getOrdenLuchadores().indexOf(lanzador) + 1, objetivo)
        }
        if (objetivo.getPersonaje() != null) {
            GestorSalida.ENVIAR_ILF_CANTIDAD_DE_VIDA(objetivo.getPersonaje(), vida)
        }
        if (pelea.getJefe() != null) {
            if (pelea.getJefe() === lanzador) {
                try {
                    objetivo.setRevivido(true)
                    objetivo.setIDReal(false)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        objetivo.setPDVMAX(vidaMax, false)
        objetivo.setPDV(vida)
        objetivo.limitarVida()
        objetivo.setEquipoBin(lanzador.getEquipoBin())
        objetivo.setInvocador(lanzador)
        pelea.addLuchadorEnEquipo(objetivo, lanzador.getEquipoBin())
        GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, if (iniciador) 147 else 780, lanzador.getID().toString() + "",
                "+" + objetivo.stringGM(0))
        GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 999, lanzador.getID().toString() + "", pelea.stringOrdenJugadores())
        if (objetivo.getPersonaje() != null) {
            GestorSalida.ENVIAR_As_STATS_DEL_PJ(objetivo.getPersonaje())
        }
        if (!iniciador) {
            lanzador.addNroInvocaciones(1)
        }
        try {
            Thread.sleep(1000)
        } catch (e: Exception) {
        }
        verificaTrampas(objetivo)
        // pelea.actualizarNumTurnos(null);
    }

    private fun efecto_ResucitarMuertos(pelea: Pelea, lanzador: Luchador, celdaObjetivo: Celda) {
        var objetivo: Luchador? = null
        for (i in pelea.getListaMuertos().size() - 1 downTo 0) {
            val muerto: Luchador = pelea.getListaMuertos().get(i)
            if (muerto.estaRetirado()) {
                continue
            }
            if (muerto.getEquipoBin() === lanzador.getEquipoBin()) {
                if (muerto.esInvocacion()) {
                    if (muerto.getInvocador().estaMuerto()) {
                        continue
                    }
                }
                objetivo = muerto
                break
            }
        }
        if (objetivo == null) {
            return
        }
        objetivo.setEstaMuerto(false)
        objetivo.setCeldaPelea(celdaObjetivo)
        objetivo.getBuffsPelea().clear()
        pelea.getListaMuertos().remove(objetivo)
        var vida = 0
        var vidaMax = 0
        try {
            vida = primerValor * objetivo.getPersonaje().getPDVMax() / 100
            vidaMax = objetivo.getPersonaje().getPDVMax()
        } catch (e: Exception) {
        }
        try {
            vida = primerValor * objetivo.getMob().getPDVMax() / 100
            vidaMax = objetivo.getMob().getPDVMax()
        } catch (e: Exception) {
        }
        val iniciador: Boolean = pelea.esLuchInicioPelea(objetivo)
        if (!iniciador) {
            pelea.getOrdenLuchadores().add(pelea.getOrdenLuchadores().indexOf(lanzador) + 1, objetivo)
        }
        if (objetivo.getPersonaje() != null) {
            GestorSalida.ENVIAR_ILF_CANTIDAD_DE_VIDA(objetivo.getPersonaje(), vida)
        }
        if (objetivo.esInvocacion() && objetivo.getPersonaje() != null) try {
            lanzador.getPersonaje().addCompañero(objetivo.getPersonaje())
            objetivo.getPersonaje().addCompañero(lanzador.getPersonaje())
            objetivo.setIDReal(false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        objetivo.setPDVMAX(vidaMax, false)
        objetivo.setPDV(vida)
        objetivo.limitarVida()
        objetivo.setEquipoBin(lanzador.getEquipoBin())
        objetivo.setInvocador(lanzador)
        pelea.addLuchadorEnEquipo(objetivo, lanzador.getEquipoBin())
        GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, if (iniciador) 147 else 780, lanzador.getID().toString() + "",
                "+" + objetivo.stringGM(0))
        GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 999, lanzador.getID().toString() + "", pelea.stringOrdenJugadores())
        if (objetivo.getPersonaje() != null) {
            GestorSalida.ENVIAR_As_STATS_DEL_PJ(objetivo.getPersonaje())
        }
        if (!iniciador) {
            lanzador.addNroInvocaciones(1)
        }
        try {
            Thread.sleep(1000)
        } catch (e: Exception) {
        }
        verificaTrampas(objetivo)
        // pelea.actualizarNumTurnos(null);
    }

    private fun efecto_Releva_Invisibles(objetivos: ArrayList<Luchador?>?, pelea: Pelea, lanzador: Luchador,
                                         celdaObjetivo: Celda) {
        val aliados: ArrayList<Luchador> = pelea.luchadoresDeEquipo(lanzador.getParamEquipoAliado())
        val celdasObj: ArrayList<Celda> = Camino.celdasAfectadasEnElArea(pelea.getMapaCopia(), celdaObjetivo.getID(),
                lanzador.getCeldaPelea().getID(), zonaEfecto)
        for (mostrar in pelea.luchadoresDeEquipo(3)) {
            if (mostrar.estaMuerto() || !mostrar.esInvisible(0)) {
                continue
            }
            if (!celdasObj.contains(mostrar.getCeldaPelea())) {
                continue
            }
            for (aliado in aliados) {
                if (mostrar.esInvisible(aliado.getID())) {
                    mostrar.aparecer(aliado)
                }
            }
        }
        if (pelea.getTrampas() != null) {
            for (trampa in pelea.getTrampas()) {
                if (!celdasObj.contains(trampa.getCelda())) {
                    continue
                }
                trampa.aparecer(objetivos)
            }
        }
        //
    }

    // aumenta los daños del hechizo X
    private fun efecto_Aumenta_Daños_Hechizo(pelea: Pelea, lanzador: Luchador, celdaObjetivo: Celda) {
        val efectoID = getStatPorEfecto(efectoID)
        lanzador.addBuffConGIE(efectoID, primerValor, duracion, hechizoID,
                convertirArgs(primerValor, efectoID, _args), lanzador, true, TipoDaño.POST_TURNOS, _condicionHechizo)
    }

    // pone una trampa de nivel X
    private fun efecto_Poner_Trampa(pelea: Pelea, lanzador: Luchador, celdaObjetivo: Celda) {
        if (!celdaObjetivo.esCaminable(true)) {
            GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(pelea, 7, "1CELDA_NO_CAMINABLE")
            GestorSalida.ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(pelea, 7, lanzador.getID(), celdaObjetivo.getID())
            return
        }
        if (celdaObjetivo.getPrimerLuchador() != null) {
            GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 151, lanzador.getID().toString() + "", hechizoID.toString() + "")
            GestorSalida.ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(pelea, 7, lanzador.getID(), celdaObjetivo.getID())
            return
        }
        if (celdaObjetivo.esTrampa()) {
            GestorSalida.ENVIAR_Im_INFORMACION(lanzador.getPersonaje(), "1229")
            GestorSalida.ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(pelea, 7, lanzador.getID(), celdaObjetivo.getID())
            return
        }
        val SH: StatHechizo = Mundo.getHechizo(primerValor).getStatsPorNivel(segundoValor) ?: return
        val tamaño: Byte = Encriptador.getNumeroPorValorHash(zonaEfecto.charAt(1))
        val celdas: ArrayList<Celda> = Camino.celdasAfectadasEnElArea(pelea.getMapaCopia(), celdaObjetivo.getID(),
                lanzador.getCeldaPelea().getID(), zonaEfecto)
        val trampa = Trampa(pelea, lanzador, celdaObjetivo, tamaño, SH, hechizoID,
                pelea.luchadoresDeEquipo(lanzador.getParamEquipoAliado()), celdas, tercerValor)
        if (pelea.getJefe() != null) {
            if (pelea.getJefe() === lanzador) {
                trampa.aparecer(pelea.luchadoresDeEquipo(3))
            }
        }
    }

    // pone un glifo nivel X
    private fun efecto_Glifo_Libre(pelea: Pelea, lanzador: Luchador, celdaObjetivo: Celda) {
        if (!celdaObjetivo.esCaminable(true)) {
            GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(pelea, 7, "1CELDA_NO_CAMINABLE")
            GestorSalida.ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(pelea, 7, lanzador.getID(), celdaObjetivo.getID())
            return
        }
        val SH: StatHechizo = Mundo.getHechizo(primerValor).getStatsPorNivel(segundoValor) ?: return
        val tamaño: Byte = Encriptador.getNumeroPorValorHash(zonaEfecto.charAt(1))
        val tipo: Char = zonaEfecto.charAt(0)
        val celdas: ArrayList<Celda> = Camino.celdasAfectadasEnElArea(pelea.getMapaCopia(), celdaObjetivo.getID(),
                lanzador.getCeldaPelea().getID(), zonaEfecto)
        Glifo(pelea, lanzador, celdaObjetivo, tamaño, SH, duracion, hechizoID, true, celdas, tercerValor, tipo)
    }

    // pone un glifo nivel X asi halla un jugador, efecto al final de turno
    private fun efecto_Glifo_Fin_Turno(pelea: Pelea, lanzador: Luchador, celdaObjetivo: Celda) {
        if (!celdaObjetivo.esCaminable(true)) {
            GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(pelea, 7, "1CELDA_NO_CAMINABLE")
            GestorSalida.ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(pelea, 7, lanzador.getID(), celdaObjetivo.getID())
            return
        }
        val SH: StatHechizo = Mundo.getHechizo(primerValor).getStatsPorNivel(segundoValor) ?: return
        val tamaño: Byte = Encriptador.getNumeroPorValorHash(zonaEfecto.charAt(1))
        val celdas: ArrayList<Celda> = Camino.celdasAfectadasEnElArea(pelea.getMapaCopia(), celdaObjetivo.getID(),
                lanzador.getCeldaPelea().getID(), zonaEfecto)
        Glifo(pelea, lanzador, celdaObjetivo, tamaño, SH, duracion, hechizoID, false, celdas, tercerValor,
                zonaEfecto.charAt(0))
    }

    // hechizo miedo
    private fun efecto_Retrocede_Hasta_Cierta_Casilla(pelea: Pelea, lanzador: Luchador, celdaObjetivo: Celda) {
        val celdaLanzamiento: Celda = lanzador.getCeldaPelea()
        val mapaCopia: Mapa = pelea.getMapaCopia()
        val dir: Int = Camino.direccionEntreDosCeldas(mapaCopia, celdaLanzamiento.getID(), celdaObjetivo.getID(),
                true)
        val sigCeldaID: Short = Camino.getSigIDCeldaMismaDir(celdaLanzamiento.getID(), dir, mapaCopia, true)
        val sigCelda: Celda = mapaCopia.getCelda(sigCeldaID)
        if (sigCelda == null || sigCelda.getPrimerLuchador() == null) {
            return
        }
        val objetivo: Luchador = sigCelda.getPrimerLuchador()
        if (objetivo.estaMuerto() || objetivo.tieneEstado(Constantes.ESTADO_ARRAIGADO)) {
            return
        }
        val distancia: Int = Camino.distanciaDosCeldas(mapaCopia, sigCeldaID, celdaObjetivo.getID())
        efectoEmpujones(pelea, lanzador, objetivo, celdaLanzamiento, sigCelda, distancia, false)
    }

    private fun efecto_Teleport_Inicio(objetivos: ArrayList<Luchador?>?, pelea: Pelea, lanzador: Luchador,
                                       celdaObjetivo: Celda) { // teletransporta
        if (lanzador.tieneEstado(Constantes.ESTADO_PESADO)) {
            return
        }
        for (objetivo in objetivos) {
            if (objetivo.estaMuerto() || objetivo.esInvocacion()) {
                continue
            }
            var celda1: Celda? = null
            for (entry in pelea.getPosInicial().entrySet()) {
                if (entry.getKey() === objetivo.getID()) {
                    celda1 = entry.getValue()
                    break
                }
            }
            if (celda1.esCaminable(true) && celda1.getPrimerLuchador() == null) {
                objetivo.getCeldaPelea().moverLuchadoresACelda(celda1)
                GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 4, lanzador.getID().toString() + "",
                        objetivo.getID().toString() + "," + celda1.getID())
                verificaTrampas(objetivo)
            }
        }
    }

    private fun efecto_Estados(objetivos: ArrayList<Luchador?>?, pelea: Pelea, lanzador: Luchador,
                               celdaObjetivo: Celda) { // estatdo X
        val estadoID = tercerValor
        if (estadoID == -1) {
            return
        }
        val temp: ArrayList<Luchador> = ArrayList<Luchador>()
        // StringBuilder afectados = new StringBuilder();
        for (objetivo in objetivos) {
            if (objetivo.estaMuerto()) {
                continue
            }
            temp.add(objetivo)
            if (efectoID == Constantes.STAT_QUITAR_ESTADO && !objetivo.tieneEstado(estadoID)) {
                continue
            }
            // if (afectados.length() > 0) {
            // afectados.append("¬");
            // }
            // afectados.append(objetivo.getID() + "," + idEstado + "," + (_efectoID ==
            // Constantes.STAT_DAR_ESTADO ? 1 : 0));
        }
        if (!temp.isEmpty()) {
            pelea.addBuffLuchadores(temp, efectoID, estadoID, duracion, hechizoID,
                    convertirArgs(estadoID, efectoID, _args), lanzador, TipoDaño.POST_TURNOS, _condicionHechizo)
        }
    }

    companion object {
        // staticos
        var TIEMPO_ENTRE_EFECTOS = 50
        var TIEMPO_GAME_ACTION = 5
        var TIEMPO_POR_LANZAR_HECHIZO = 100
        var TIEMPO_POR_LUCHADOR_MUERTO = 500
        var MULTIPLICADOR_DAÑO_PJ = 1f
        var MULTIPLICADOR_DAÑO_MOB = 1f
        var MULTIPLICADOR_DAÑO_CAC = 1f
        var MULTIPLICADOR_DAÑO_CAC_PVP = 0f
        var MULTIPLICADOR_DAÑO_CAC_KOLI = 0f
        fun getStatPorEfecto(efecto: Int): Int {
            when (efecto) {
                Constantes.STAT_ROBA_PM, 169 -> return Constantes.STAT_MENOS_PM
                Constantes.STAT_ROBA_PA, 168 -> return Constantes.STAT_MENOS_PA
                266 -> return Constantes.STAT_MENOS_SUERTE
                267 -> return Constantes.STAT_MENOS_VITALIDAD
                268 -> return Constantes.STAT_MENOS_AGILIDAD
                269 -> return Constantes.STAT_MENOS_INTELIGENCIA
                270 -> return Constantes.STAT_MENOS_SABIDURIA
                271 -> return Constantes.STAT_MENOS_FUERZA
                Constantes.STAT_ROBA_ALCANCE -> return Constantes.STAT_MENOS_ALCANCE
                606 -> return Constantes.STAT_MAS_SABIDURIA
                607 -> return Constantes.STAT_MAS_FUERZA
                608 -> return Constantes.STAT_MAS_SUERTE
                609 -> return Constantes.STAT_MAS_AGILIDAD
                610 -> return Constantes.STAT_MAS_VITALIDAD
                611 -> return Constantes.STAT_MAS_INTELIGENCIA
            }
            return efecto
        }

        private fun getStatContrario(efecto: Int): Int {
            when (efecto) {
                Constantes.STAT_MAS_PM -> return Constantes.STAT_MENOS_PM
                Constantes.STAT_MAS_PA -> return Constantes.STAT_MENOS_PA
                Constantes.STAT_MAS_SUERTE -> return Constantes.STAT_MENOS_SUERTE
                Constantes.STAT_MAS_VITALIDAD -> return Constantes.STAT_MENOS_VITALIDAD
                Constantes.STAT_MAS_AGILIDAD -> return Constantes.STAT_MENOS_AGILIDAD
                Constantes.STAT_MAS_INTELIGENCIA -> return Constantes.STAT_MENOS_INTELIGENCIA
                Constantes.STAT_MAS_SABIDURIA -> return Constantes.STAT_MENOS_SABIDURIA
                Constantes.STAT_MAS_FUERZA -> return Constantes.STAT_MENOS_FUERZA
                Constantes.STAT_MAS_ALCANCE -> return Constantes.STAT_MENOS_ALCANCE
                Constantes.STAT_MENOS_PM -> return Constantes.STAT_MAS_PM
                Constantes.STAT_MENOS_PA -> return Constantes.STAT_MAS_PA
                Constantes.STAT_MENOS_SUERTE -> return Constantes.STAT_MAS_SUERTE
                Constantes.STAT_MENOS_VITALIDAD -> return Constantes.STAT_MAS_VITALIDAD
                Constantes.STAT_MENOS_AGILIDAD -> return Constantes.STAT_MAS_AGILIDAD
                Constantes.STAT_MENOS_INTELIGENCIA -> return Constantes.STAT_MAS_INTELIGENCIA
                Constantes.STAT_MENOS_SABIDURIA -> return Constantes.STAT_MAS_SABIDURIA
                Constantes.STAT_MENOS_FUERZA -> return Constantes.STAT_MAS_FUERZA
                Constantes.STAT_MENOS_ALCANCE -> return Constantes.STAT_MAS_ALCANCE
            }
            return efecto
        }

        fun convertirArgs(valor: Int, efectoID: Int, args: String): String {
            var valor = valor
            if (efectoID == 788) { // castigo
                return args
            }
            val splits: Array<String> = args.split(",")
            var valMax = "-1"
            when (efectoID) {
                81, 85, 86, 87, 88, 89, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 512, 107, 108, 220, 265 -> {
                    valor = Integer.parseInt(splits[0])
                    if (!splits[1].equals(valor.toString() + "")) {
                        valMax = splits[1]
                    }
                }
                9, 510, 511, 79, 106, 131, 165, 181, 293, 301, 302, 303, 304, 305, 787, 788 -> valMax = splits[1]
            }
            return valor.toString() + "," + valMax + "," + splits[2] + "," + splits[3] + "," + splits[4]
        }

        // aumentados por efecto 293
        fun getDañoAumentadoPorHechizo(lanzador: Luchador, hechizo: Int, daño: Int): Int {
            var daño = daño
            if (hechizo != 0) {
                for (buff in lanzador.getBuffsPorEfectoID(293)) {
                    if (buff.getPrimerValor() === hechizo) {
                        val add: Int = buff.getTercerValor()
                        if (add <= 0) {
                            continue
                        }
                        daño += add
                    }
                }
            }
            return daño
        }

        // param official
        fun getPorcHuida2(agiMov: Int, agiTac: Int): Int {
            // int porcTac = (int) ((100 * (Math.pow(agiTac + 25, 2))) / ((Math.pow(agiTac +
            // 25, 2)) +
            // (Math.pow(agiMov + 25, 2))));
            var porcTac = 300 * (agiMov + 25) / (agiMov + agiTac + 50) - 100
            porcTac = Math.min(100, porcTac)
            porcTac = Math.max(0, porcTac)
            return porcTac
        }

        fun getPorcHuida(movedor: Luchador, tacleador: Luchador): Int {
            val statsTac: TotalStats = tacleador.getTotalStats()
            val placajeTac: Int = statsTac.getTotalStatConComplemento(Constantes.STAT_MAS_PLACAJE, tacleador.getPelea(),
                    null)
            if (2 * (placajeTac + 2) <= 0) {
                return 100
            }
            val statsMov: TotalStats = movedor.getTotalStats()
            var huidaMov: Int = statsMov.getTotalStatConComplemento(Constantes.STAT_MAS_HUIDA, movedor.getPelea(), null)
            if (huidaMov < -2) {
                huidaMov = -2
            }
            var porc = (huidaMov + 2) * 100 / (2 * (placajeTac + 2))
            if (porc < 0) {
                porc = 0
            } else if (porc > 100) {
                porc = 100
            }
            return porc
        }

        private fun reenvioDaño(objetivo: Luchador): Int {
            val totalStats: TotalStats = objetivo.getTotalStats()
            val sabiduria: Int = totalStats.getTotalStatParaMostrar(Constantes.STAT_MAS_SABIDURIA, objetivo.getPelea(), null)
            val rEquipo: Int = totalStats.getTotalStatParaMostrar(Constantes.STAT_REENVIA_DAÑOS, objetivo.getPelea(), null)
            var rHechizos = 0
            for (buff in objetivo.getBuffsPorEfectoID(Constantes.STAT_DAÑOS_DEVUELTOS)) {
                rHechizos += buff.getRandomValor(buff.getLanzador())
            }
            for (buff in objetivo.getBuffsPorEfectoID(Constantes.STAT_REENVIA_DAÑOS)) {
                rHechizos += buff.getRandomValor(buff.getLanzador())
            }
            var bonus = (rHechizos * (1 + sabiduria / MainServidor.SABIDURIA_PARA_REENVIO as Float) + rEquipo).toInt()
            if (validar(objetivo.getPelea()) && MainServidor.LIMITE_REENVIO > 0) {
                bonus = Math.min(bonus, MainServidor.LIMITE_REENVIO)
            }
            return bonus
        }

        fun aplicarBuffContraGolpe(efectoID: Int, daño: Int, objetivo: Luchador, lanzador: Luchador,
                                   pelea: Pelea, hechizoID: Int, tipo: TipoDaño): Int {
            var daño = daño
            if ((tipo == TipoDaño.CAC || tipo == TipoDaño.NORMAL || tipo == TipoDaño.GLIFO || tipo == TipoDaño.TRAMPA)
                    && lanzador.getID() !== objetivo.getID()) {
                var reenvio = reenvioDaño(objetivo)
                if (reenvio > daño) {
                    reenvio = daño
                }
                if (MainServidor.PARAM_CONTRA_DANO) daño -= reenvio
                // el reenvio no disminuye los daños
                // dañoFinal -= reenvio;
                if (reenvio > 0) {
                    pelea.setUltimoTipoDaño(TipoDaño.NORMAL)
                    GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 107, "-1", objetivo.getID().toString() + "," + reenvio)
                    restarPDVLuchador(pelea, lanzador, objetivo, null, reenvio, 0)
                }
            }
            if (objetivo.estaMuerto()) {
                return 0
            }
            // for (final int id : Constantes.BUFF_ACCION_RESPUESTA) {
            for (buff in objetivo.getBuffsPelea()) {
                if (objetivo.estaMuerto()) {
                    return 0
                }
                if (!buff.getCondicionBuff().isEmpty()) {
                    continue
                }
                when (buff.getEfectoID()) {
                    9 -> {
                        if (tipo == TipoDaño.TRAMPA || tipo == TipoDaño.GLIFO) {
                            continue
                        }
                        if (Camino.distanciaDosCeldas(pelea.getMapaCopia(), objetivo.getCeldaPelea().getID(),
                                        lanzador.getCeldaPelea().getID()) > 1) {
                            continue
                        }
                        val elusion: Int = buff.getPrimerValor()
                        val azar: Int = Formulas.getRandomInt(1, 100)
                        if (azar > elusion) {
                            continue
                        }
                        var nroCasillas = 0
                        try {
                            nroCasillas = Integer.parseInt(buff.getArgs().split(",").get(1))
                        } catch (e: Exception) {
                        }
                        efectoEmpujones(pelea, lanzador, objetivo, lanzador.getCeldaPelea(), objetivo.getCeldaPelea(),
                                nroCasillas, true)
                        daño = 0
                    }
                    510 -> {
                        if (Camino.distanciaDosCeldas(pelea.getMapaCopia(), objetivo.getCeldaPelea().getID(),
                                        lanzador.getCeldaPelea().getID()) > 1) {
                            continue
                        }
                        val statTemp: Int = buff.getPrimerValor()
                        val cantidadTemp: Int = buff.getSegundoValor()
                        val turno: Int = buff.getTercerValor()
                        val splits2: Array<String> = buff.getArgs().split(",", 2)
                        objetivo.addBuffConGIE(statTemp, cantidadTemp, turno, buff.getHechizoID(),
                                convertirArgs(cantidadTemp, statTemp, cantidadTemp.toString() + "," + splits2[1]), null, true,
                                TipoDaño.POST_TURNOS, "")
                        GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, statTemp, lanzador.getID().toString() + "",
                                objetivo.getID().toString() + "," + cantidadTemp + "," + 5)
                    }
                    511 -> {
                        if (Camino.distanciaDosCeldas(pelea.getMapaCopia(), objetivo.getCeldaPelea().getID(),
                                        lanzador.getCeldaPelea().getID()) <= 1) {
                            continue
                        }
                        statTemp = buff.getPrimerValor()
                        cantidadTemp = buff.getSegundoValor()
                        turno = buff.getTercerValor()
                        splits2 = buff.getArgs().split(",", 2)
                        objetivo.addBuffConGIE(statTemp, cantidadTemp, turno, buff.getHechizoID(),
                                convertirArgs(cantidadTemp, statTemp, cantidadTemp.toString() + "," + splits2.get(1)), null, true,
                                TipoDaño.POST_TURNOS, "")
                        GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, statTemp, lanzador.getID().toString() + "",
                                objetivo.getID().toString() + "," + cantidadTemp + "," + 5)
                    }
                    79 -> {
                        if (tipo == TipoDaño.TRAMPA || tipo == TipoDaño.GLIFO) {
                            continue
                        }
                        try {
                            val infos: Array<String> = buff.getArgs().split(",")
                            val coefDaño: Int = Integer.parseInt(infos[0])
                            val coefCura: Int = Integer.parseInt(infos[1])
                            val suerte: Int = Integer.parseInt(infos[2])
                            if (Formulas.getRandomInt(1, 100) <= suerte) {
                                // Cura
                                daño *= coefCura
                                daño = Math.min(daño, objetivo.getPDVMaxConBuff() - objetivo.getPDVConBuff())
                                daño = -daño
                            } else {
                                daño *= coefDaño
                            }
                        } catch (e: Exception) {
                        }
                    }
                    304 -> for (o in pelea.luchadoresDeEquipo(3)) {
                        if (o.estaMuerto()) {
                            continue
                        }
                        if (o.tieneEstado(Constantes.ESTADO_TRANSPORTADO)) {
                            continue
                        }
                        val b: Buff = o.getBuff(766)
                        if (b != null && b.getLanzador().getID() === objetivo.getID()) {
                            b.aplicarHechizoDeBuff(pelea, o, o.getCeldaPelea())
                        }
                    }
                    305 -> if (tipo == TipoDaño.CAC) {
                        buff.aplicarHechizoDeBuff(pelea, objetivo, objetivo.getCeldaPelea())
                    }
                    776 -> {
                        if (tipo == TipoDaño.TRAMPA || tipo == TipoDaño.GLIFO) {
                            continue
                        }
                        if (objetivo.tieneBuff(776)) { // si posee daños incurables
                            var pdvMax: Int = objetivo.getPDVMaxSinBuff()
                            val pdaño: Float = objetivo.getValorPorBuffsID(776) / 100f
                            pdvMax -= (daño * pdaño).toInt()
                            if (pdvMax < 0) {
                                pdvMax = 0
                            }
                            objetivo.setPDVMAX(pdvMax, false)
                        }
                    }
                    788 -> {
                        when (efectoID) {
                            85, 86, 87, 88, 89 -> return daño
                        }
                        if (tipo == TipoDaño.POST_TURNOS || hechizoID == 1495) {
                            continue
                        }
                        val porc = if (lanzador.getPersonaje() == null) 1 else 2
                        var bonusGanado = daño / porc
                        var stat: Int = buff.getPrimerValor()
                        if (stat == Constantes.STAT_CURAR) {
                            stat = Constantes.STAT_MAS_VITALIDAD
                        }
                        var max = 0
                        try {
                            max = Integer.parseInt(buff.getArgs().split(",").get(1))
                        } catch (e: Exception) {
                        }
                        max -= objetivo.getBonusCastigo(stat)
                        if (max <= 0 || bonusGanado <= 0) {
                            continue
                        }
                        if (bonusGanado > max) {
                            bonusGanado = max
                        }
                        objetivo.setBonusCastigo(objetivo.getBonusCastigo(stat) + bonusGanado, stat)
                        val splits: Array<String> = buff.getArgs().split(",", 2)
                        val duo: Duo<Boolean, Buff> = objetivo.addBuffConGIE(stat, bonusGanado, 5, buff.getHechizoID(),
                                convertirArgs(bonusGanado, stat, bonusGanado.toString() + "," + splits[1]), null, true,
                                TipoDaño.POST_TURNOS, "")
                        if (duo._segundo != null) {
                            duo._segundo.setDesbufeable(MainServidor.PARAM_BOOST_SACRO_DESBUFEABLE)
                        }
                        GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, stat, lanzador.getID().toString() + "",
                                objetivo.getID().toString() + "," + bonusGanado + "," + 5)
                    }
                    else -> {
                    }
                }
                // }
            }
            return daño
        }

        private fun efectoEmpujones(pelea: Pelea, lanzador: Luchador, objetivo: Luchador, celdaInicio: Celda,
                                    celdaDestino: Celda, nCeldasAMover: Int, golpe: Boolean): Boolean {
            var nCeldasAMover = nCeldasAMover
            if (nCeldasAMover == 0 || objetivo.estaMuerto() || objetivo.esEstatico()
                    || objetivo.tieneEstado(Constantes.ESTADO_ARRAIGADO)) {
                return false
            }
            if (MainServidor.MODO_ESTADO_PESADO && objetivo.tieneEstado(Constantes.ESTADO_PESADO)) {
                return false
            }
            val mapaCopia: Mapa = pelea.getMapaCopia()
            val duo: Duo<Integer, Short> = Camino.getCeldaDespuesDeEmpujon(pelea, celdaInicio, celdaDestino, nCeldasAMover)
            val celdasFaltantes: Int = duo._primero
            if (celdasFaltantes == -1) {
                return false
            }
            nCeldasAMover = Math.abs(nCeldasAMover)
            var dañoEmpuje = 0
            val nuevaCeldaID: Short = duo._segundo
            if (celdasFaltantes > 0) { // si falto celdas para seguir empujando
                nCeldasAMover -= celdasFaltantes
                if (golpe) {
                    var empujador: Luchador = lanzador
                    if (MainServidor.PARAM_MOB_TENER_NIVEL_INVOCADOR_PARA_EMPUJAR) {
                        while (empujador.esInvocacion()) {
                            empujador = empujador.getInvocador()
                        }
                    }
                    val nivelEmpujador: Int = empujador.getNivel()
                    val statsDañoEmpuje: Int = lanzador.getTotalStats()
                            .getTotalStatParaMostrar(Constantes.STAT_MAS_DAÑOS_EMPUJE, lanzador.getPelea(), null)
                    // modifique esta formula por la q me dio un frances, y suena mejor
                    val max: Int = Math.max(1, 8 * nivelEmpujador / 50)
                    val rand: Int = Formulas.getRandomInt(1, max)
                    dañoEmpuje = (8 + rand) * celdasFaltantes
                    if (dañoEmpuje < 8) {
                        dañoEmpuje = 8
                    }
                    dañoEmpuje += statsDañoEmpuje
                    if (dañoEmpuje < 0) {
                        dañoEmpuje = 0
                    }
                }
            }
            if (nuevaCeldaID > 0 && objetivo.getCeldaPelea().getID() !== nuevaCeldaID) {
                val nuevaCelda: Celda = mapaCopia.getCelda(nuevaCeldaID)
                if (nuevaCelda != null) {
                    val transportado: Luchador = objetivo.getTransportando()
                    if (transportado != null) {
                        objetivo.getCeldaPelea().removerLuchador(objetivo)
                        objetivo.setCeldaPelea(nuevaCelda)
                        pelea.quitarTransportados(objetivo)
                    } else {
                        objetivo.getCeldaPelea().moverLuchadoresACelda(nuevaCelda)
                    }
                    GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 5, lanzador.getID().toString() + "",
                            objetivo.getID().toString() + "," + nuevaCeldaID)
                    try {
                        Thread.sleep((300 + 200 * Math.sqrt(nCeldasAMover)) as Int)
                    } catch (e: Exception) {
                    }
                }
            }
            if (dañoEmpuje > 0) {
                val dir: Int = Camino.direccionEntreDosCeldas(mapaCopia, celdaInicio.getID(), celdaDestino.getID(), true)
                var afectado: Luchador? = null
                var celdaQueGolpea: Celda? = null
                while (dañoEmpuje > 0) {
                    if (celdaQueGolpea == null) {
                        afectado = objetivo
                        celdaQueGolpea = objetivo.getCeldaPelea()
                    } else {
                        val sigCeldaID: Short = Camino.getSigIDCeldaMismaDir(celdaQueGolpea.getID(), dir, mapaCopia, true)
                        val sigCelda: Celda = mapaCopia.getCelda(sigCeldaID) ?: break
                        celdaQueGolpea = sigCelda
                        afectado = sigCelda.getPrimerLuchador()
                        if (afectado == null) {
                            break
                        }
                    }
                    val redDañoEmpuje: Int = afectado.getTotalStats()
                            .getTotalStatParaMostrar(Constantes.STAT_MAS_REDUCCION_EMPUJE, afectado.getPelea(), null)
                    val dañoEmpuje2 = dañoEmpuje - redDañoEmpuje
                    dañoEmpuje /= 2
                    if (redDañoEmpuje > 0) {
                        GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, Constantes.STAT_MAS_DAÑOS_REDUCIDOS_NO_FECA, lanzador.getID().toString() + "", afectado.getID().toString() + "," + redDañoEmpuje)
                    }
                    if (dañoEmpuje2 > 0) {
                        restarPDVLuchador(pelea, afectado, lanzador, null, dañoEmpuje2, 0)
                        if (afectado.estaMuerto()) {
                            break
                        } else {
                            val buff: Buff = afectado.getBuff(303)
                            if (buff != null) {
                                buff.aplicarHechizoDeBuff(pelea, afectado, afectado.getCeldaPelea())
                            }
                        }
                    }
                }
            }
            verificaTrampas(objetivo)
            return true
        }

        fun verificaTrampas(objetivo: Luchador?) {
            if (objetivo.getCeldaPelea().getTrampas() == null) {
                return
            }
            for (trampa in objetivo.getCeldaPelea().getTrampas()) {
                trampa.activarTrampa(objetivo)
            }
        }

        var reenvioLejano = false
        private fun reenvioHechizo(pelea: Pelea, nivelHechizo: Int, hechizoID: Int, objetivo: Luchador,
                                   lanzador: Luchador, tipo: TipoDaño, reenviar: Boolean): Luchador {
            return when (tipo) {
                TipoDaño.CAC, TipoDaño.POST_TURNOS -> objetivo
                else -> {
                    var retorno: Luchador = objetivo
                    if (hechizoID != 0) {
                        if (objetivo.getValorPorBuffsID(106) >= nivelHechizo) {
                            val azar: Int = Formulas.getRandomInt(1, 100)
                            val reenvia: Boolean = azar <= objetivo.getBuff(106).getTercerValor()
                            GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 106, objetivo.getID().toString() + "",
                                    objetivo.getID().toString() + "," + if (reenvia) 1 else 0)
                            if (reenvia) {
                                retorno = lanzador
                            }
                        } else if (reenviar) {
                            val reenvia = true
                            GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 106, objetivo.getID().toString() + "",
                                    objetivo.getID().toString() + "," + if (reenvia) 1 else 0)
                            if (reenvia) {
                                retorno = lanzador
                            }
                        }
                    }
                    retorno
                }
            }
        }

        fun sacrificio(pelea: Pelea?, objetivo: Luchador): Luchador {
            var retorno: Luchador = objetivo
            if (retorno.tieneBuff(765)) {
                val sacrificado: Luchador = retorno.getBuff(765).getLanzador()
                if (MainServidor.MODO_IMPACT and !retorno.estaMuerto() and !sacrificado.estaMuerto()
                        and (retorno.tieneEstado(Constantes.ESTADO_ARRAIGADO)
                                || retorno.tieneEstado(Constantes.ESTADO_TRANSPORTADO)
                                || retorno.tieneEstado(Constantes.ESTADO_PORTADOR)
                                || sacrificado.tieneEstado(Constantes.ESTADO_ARRAIGADO)
                                || sacrificado.tieneEstado(Constantes.ESTADO_TRANSPORTADO)
                                || sacrificado.tieneEstado(Constantes.ESTADO_PORTADOR))) {
                    return sacrificado
                }
                if (retorno.estaMuerto() || retorno.esEstatico() || retorno.tieneEstado(Constantes.ESTADO_ARRAIGADO)
                        || retorno.tieneEstado(Constantes.ESTADO_TRANSPORTADO)
                        || retorno.tieneEstado(Constantes.ESTADO_PORTADOR)) {
                    return retorno
                }
                if (sacrificado.estaMuerto() || sacrificado.esEstatico()
                        || sacrificado.tieneEstado(Constantes.ESTADO_ARRAIGADO)
                        || sacrificado.tieneEstado(Constantes.ESTADO_TRANSPORTADO)
                        || sacrificado.tieneEstado(Constantes.ESTADO_PORTADOR)) {
                    return retorno
                }
                if (retorno.tieneEstado(Constantes.ESTADO_PESADO) || sacrificado.tieneEstado(Constantes.ESTADO_PESADO)) {
                    return sacrificado
                }
                val cSacrificado: Celda = sacrificado.getCeldaPelea()
                val cObjetivo: Celda = objetivo.getCeldaPelea()
                cSacrificado.limpiarLuchadores()
                cObjetivo.limpiarLuchadores()
                sacrificado.setCeldaPelea(cObjetivo)
                objetivo.setCeldaPelea(cSacrificado)
                GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 4, objetivo.getID().toString() + "",
                        objetivo.getID().toString() + "," + cSacrificado.getID())
                GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 4, sacrificado.getID().toString() + "",
                        sacrificado.getID().toString() + "," + cObjetivo.getID())
                retorno = sacrificado
                try {
                    Thread.sleep(250)
                } catch (e: Exception) {
                }
            }
            if (retorno.tieneBuff(766)) { // intercepcion de daños (no cambia de posicion)
                if (retorno.esEstatico() || retorno.estaMuerto()) {
                    return retorno
                }
                val sacrificado: Luchador = retorno.getBuff(766).getLanzador()
                if (sacrificado.estaMuerto()) {
                    return retorno
                }
                retorno = sacrificado
            }
            return retorno
        }

        // public static int maxResistencia(Luchador objetivo, int resPorcT) {
        // if (objetivo.getMob() != null) {
        // return resPorcT;
        // }
        // // if (objetivo.getPersonaje() != null && resPorcT >
        // MainServidor.LIMITE_PORC_RESISTENCIA_BUFFS)
        // // {
        // // return MainServidor.LIMITE_PORC_RESISTENCIA_BUFFS;
        // // }
        // if (resPorcT > MainServidor.LIMITE_PORC_RESISTENCIA_BUFFS) {// recaurdador,
        // prisma
        // return MainServidor.LIMITE_PORC_RESISTENCIA_BUFFS;
        // }
        // return resPorcT;
        // }
        fun calcularCuraFinal(curador: Luchador, base: Int): Int {
            val stats: TotalStats = curador.getTotalStats()
            val inteligencia: Int = Math.max(0,
                    stats.getTotalStatParaMostrar(Constantes.STAT_MAS_INTELIGENCIA, curador.getPelea(), null))
            val curas: Int = Math.max(0, stats.getTotalStatParaMostrar(Constantes.STAT_MAS_CURAS, curador.getPelea(), null))
            var cura: Int = Math.max(0, base * (100 + inteligencia) / 100 + curas)
            if (validar(curador.getPelea()) && base < 400 && MainServidor.LIMITE_CURAS > 0) {
                cura = Math.min(cura, MainServidor.LIMITE_CURAS)
            }
            return cura
        }

        fun validar(pelea: Pelea?): Boolean {
            if (pelea != null) {
                when (pelea.getTipoPelea()) {
                    Constantes.PELEA_TIPO_PVP -> if (MainServidor.LIMITE_STATS_PVP_PERSONALIZADOS) {
                        return true
                    }
                }
            }
            return false
        }

        fun getDañosReducidos(afectado: Luchador, elementoID: Int): Int {
            var defensa = 0
            val stats = intArrayOf(Constantes.STAT_MAS_DAÑOS_REDUCIDOS_ARMADURAS_FECA,
                    Constantes.STAT_MAS_DAÑOS_REDUCIDOS_NO_FECA)
            for (efectoID in stats) {
                for (buff in afectado.getBuffsPorEfectoID(efectoID)) { // daños reducidos
                    var statComplementario: Int = Constantes.STAT_MAS_INTELIGENCIA
                    when (buff.getHechizoID()) {
                        1, 452 -> {
                            if (elementoID != Constantes.ELEMENTO_FUEGO) {
                                continue
                            }
                            statComplementario = Constantes.STAT_MAS_INTELIGENCIA
                        }
                        6, 453 -> {
                            if (elementoID != Constantes.ELEMENTO_NEUTRAL && elementoID != Constantes.ELEMENTO_TIERRA) {
                                continue
                            }
                            statComplementario = Constantes.STAT_MAS_FUERZA
                        }
                        14, 454 -> {
                            if (elementoID != Constantes.ELEMENTO_AIRE) {
                                continue
                            }
                            statComplementario = Constantes.STAT_MAS_AGILIDAD
                        }
                        18, 451 -> {
                            if (elementoID != Constantes.ELEMENTO_AGUA) {
                                continue
                            }
                            statComplementario = Constantes.STAT_MAS_SUERTE
                        }
                    }
                    var lTemp: Luchador = buff.getLanzador()
                    if (efectoID == Constantes.STAT_MAS_DAÑOS_REDUCIDOS_NO_FECA) {
                        lTemp = afectado
                    }
                    var inteligencia: Int = afectado.getTotalStats().getTotalStatParaMostrar(Constantes.STAT_MAS_INTELIGENCIA,
                            afectado.getPelea(), null)
                    inteligencia = Math.max(0, inteligencia)
                    var complemento: Int = lTemp.getTotalStats().getTotalStatParaMostrar(statComplementario, lTemp.getPelea(),
                            null)
                    complemento = Math.max(0, complemento)
                    val value: Int = buff.getRandomValor(buff.getLanzador())
                    val sinComp = 1 + complemento / 100f
                    val conComp = 1 + inteligencia / 200f + complemento / 200f
                    defensa += Math.max(conComp, sinComp) * value
                }
            }
            return defensa
        }

        fun getPuntosPerdidos(efecto: Int, puntosARestar: Int, lanzador: Luchador,
                              objetivo: Luchador): Int {
            val esquivaLanzador: Int
            var esquivaObjetivo: Int
            var puntosIniciales: Int
            var puntosActuales = 0
            esquivaLanzador = lanzador.getTotalStats().getTotalStatConComplemento(Constantes.STAT_MAS_SABIDURIA,
                    lanzador.getPelea(), null) / 4
            if (efecto == Constantes.STAT_MENOS_PM) { // movimiento
                esquivaObjetivo = objetivo.getTotalStats().getTotalStatConComplemento(Constantes.STAT_MAS_ESQUIVA_PERD_PM,
                        objetivo.getPelea(), null)
                puntosActuales = objetivo.getTotalStats().getTotalStatParaMostrar(Constantes.STAT_MAS_PM,
                        objetivo.getPelea(), null)
                puntosIniciales = objetivo.getBaseStats().getStatParaMostrar(Constantes.STAT_MAS_PM)
                if (objetivo.getObjetosStats() != null) {
                    puntosIniciales += objetivo.getObjetosStats().getStatParaMostrar(Constantes.STAT_MAS_PM)
                }
            } else {
                esquivaObjetivo = objetivo.getTotalStats().getTotalStatConComplemento(Constantes.STAT_MAS_ESQUIVA_PERD_PA,
                        objetivo.getPelea(), null)
                puntosActuales = objetivo.getTotalStats().getTotalStatParaMostrar(Constantes.STAT_MAS_PA,
                        objetivo.getPelea(), null)
                puntosIniciales = objetivo.getBaseStats().getStatParaMostrar(Constantes.STAT_MAS_PA)
                if (objetivo.getObjetosStats() != null) {
                    puntosIniciales += objetivo.getObjetosStats().getStatParaMostrar(Constantes.STAT_MAS_PA)
                }
            }
            var plus = 0
            if (esquivaObjetivo < 0) {
                plus = Math.abs(esquivaObjetivo)
            }
            esquivaObjetivo = Math.max(1, esquivaObjetivo)
            puntosIniciales = Math.max(1, puntosIniciales)
            // System.out.println("--------------");
            // System.out.println("esquivaLanzador " + esquivaLanzador);
            // System.out.println("esquivaObjetivo " + esquivaObjetivo);
            // System.out.println("puntosActuales " + puntosActuales);
            // System.out.println("puntosiniciales " + puntosIniciales);
            var restar = 0
            for (i in 0 until puntosARestar) {
                var acierto = (puntosActuales / puntosIniciales.toFloat()
                        * (esquivaLanzador / esquivaObjetivo.toFloat()) * 50).toInt()
                // System.out.println("prob " + acierto);
                acierto += plus
                if (acierto < 10) {
                    acierto = 10
                } else if (acierto > 90) {
                    acierto = 90
                }
                if (acierto >= Formulas.getRandomInt(1, 100)) {
                    puntosActuales--
                    restar++
                }
            }
            if (restar > puntosActuales) {
                restar = puntosActuales
            }
            return restar
        }

        fun dañoPorEspalda(pelea: Pelea?, lanzador: Luchador?, objetivo: Luchador?, daño: Int): Int {
            // if (MainServidor.BONUS_ATAQUE_ESPALDA > 0 && lanzador.getDireccion() ==
            // objetivo.getDireccion()) {
            // if (Camino.esSiguienteA(lanzador.getCeldaPelea(), objetivo.getCeldaPelea()))
            // {
            // daño += (daño * MainServidor.BONUS_ATAQUE_ESPALDA / 100);
            // // GestorSalida.ENVIAR_cS_EMOTE_EN_PELEA(pelea, 7, objetivo.getID(), 61);
            // // GestorSalida.ENVIAR_cS_EMOTE_EN_PELEA(pelea, 7, lanzador.getID(), 68);
            // }
            // }
            return daño
        }

        fun calcularDañoFinal(pelea: Pelea?, lanzador: Luchador, objetivo: Luchador,
                              elemento: Int, dañoInicial: Float, hechizoID: Int, tipoDaño: TipoDaño, esGC: Boolean): Int {
            var elemento = elemento
            val esCaC = tipoDaño == TipoDaño.CAC
            val tStatsLanzador: TotalStats = lanzador.getTotalStats()
            val tStatsObjetivo: TotalStats = objetivo.getTotalStats()
            var multiDañoPJ = MULTIPLICADOR_DAÑO_PJ
            var statC = 0
            var resMasO = 0
            var resPorcO = 0
            var redMag = 0
            var redFis = 0
            var redArmadO = 0
            var masDaños = 0
            var porcDaños = 0
            var multiplicaDaños = 0
            val lanzaPerso: Personaje = lanzador.getPersonaje()
            var info: StringBuilder? = null
            if (MainServidor.PARAM_INFO_DAÑO_BATALLA) {
                info = StringBuilder()
                info.append("SpellID: " + hechizoID + " TargetID: " + objetivo.getID() + " CaC: " + esCaC + " GC: " + esGC
                        + " DmgStart: " + dañoInicial)
            }
            when (elemento) {
                Constantes.ELEMENTO_NEUTRAL + 10, Constantes.ELEMENTO_NEUTRAL -> {
                    statC = tStatsLanzador.getTotalStatConComplemento(Constantes.STAT_MAS_FUERZA, lanzador.getPelea(), null)
                    masDaños = tStatsLanzador.getTotalStatConComplemento(Constantes.STAT_MAS_DAÑO_FISICO, lanzador.getPelea(),
                            null)
                    masDaños = tStatsLanzador.getTotalStatConComplemento(Constantes.STAT_MAS_DAÑOS_DE_NEUTRAL,
                            lanzador.getPelea(), null)
                    resMasO = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_FIJA_NEUTRAL,
                            objetivo.getPelea(), null)
                    resPorcO = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_PORC_NEUTRAL,
                            objetivo.getPelea(), objetivo.getPersonaje())
                    redFis = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_REDUCCION_FISICA, objetivo.getPelea(),
                            null)
                    when (pelea.getTipoPelea()) {
                        Constantes.PELEA_TIPO_DESAFIO, Constantes.PELEA_TIPO_KOLISEO, Constantes.PELEA_TIPO_PVP, Constantes.PELEA_TIPO_RECAUDADOR -> {
                            resPorcO = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_PORC_PVP_NEUTRAL,
                                    objetivo.getPelea(), null)
                            resMasO = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_FIJA_PVP_NEUTRAL,
                                    objetivo.getPelea(), null)
                        }
                    }
                }
                Constantes.ELEMENTO_TIERRA + 10, Constantes.ELEMENTO_TIERRA -> {
                    statC = tStatsLanzador.getTotalStatConComplemento(Constantes.STAT_MAS_FUERZA, lanzador.getPelea(), null)
                    masDaños = tStatsLanzador.getTotalStatConComplemento(Constantes.STAT_MAS_DAÑO_FISICO, lanzador.getPelea(),
                            null)
                    masDaños = tStatsLanzador.getTotalStatConComplemento(Constantes.STAT_MAS_DAÑOS_DE_TIERRA,
                            lanzador.getPelea(), null)
                    resMasO = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_FIJA_TIERRA,
                            objetivo.getPelea(), null)
                    resPorcO = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_PORC_TIERRA,
                            objetivo.getPelea(), objetivo.getPersonaje())
                    redFis = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_REDUCCION_FISICA, objetivo.getPelea(),
                            null)
                    when (pelea.getTipoPelea()) {
                        Constantes.PELEA_TIPO_DESAFIO, Constantes.PELEA_TIPO_KOLISEO, Constantes.PELEA_TIPO_PVP, Constantes.PELEA_TIPO_RECAUDADOR -> {
                            resPorcO = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_PORC_PVP_TIERRA,
                                    objetivo.getPelea(), null)
                            resMasO = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_FIJA_PVP_TIERRA,
                                    objetivo.getPelea(), null)
                        }
                    }
                }
                Constantes.ELEMENTO_FUEGO + 10, Constantes.ELEMENTO_FUEGO -> {
                    statC = tStatsLanzador.getTotalStatConComplemento(Constantes.STAT_MAS_INTELIGENCIA, lanzador.getPelea(),
                            null)
                    masDaños = tStatsLanzador.getTotalStatConComplemento(Constantes.STAT_MAS_DAÑOS_DE_FUEGO,
                            lanzador.getPelea(), null)
                    resMasO = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_FIJA_FUEGO, objetivo.getPelea(),
                            null)
                    resPorcO = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_PORC_FUEGO,
                            objetivo.getPelea(), objetivo.getPersonaje())
                    redMag = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_REDUCCION_MAGICA, objetivo.getPelea(),
                            null)
                    when (pelea.getTipoPelea()) {
                        Constantes.PELEA_TIPO_DESAFIO, Constantes.PELEA_TIPO_KOLISEO, Constantes.PELEA_TIPO_PVP, Constantes.PELEA_TIPO_RECAUDADOR -> {
                            resPorcO = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_PORC_PVP_FUEGO,
                                    objetivo.getPelea(), null)
                            resMasO = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_FIJA_PVP_FUEGO,
                                    objetivo.getPelea(), null)
                        }
                    }
                }
                Constantes.ELEMENTO_AGUA + 10, Constantes.ELEMENTO_AGUA -> {
                    statC = tStatsLanzador.getTotalStatConComplemento(Constantes.STAT_MAS_SUERTE, lanzador.getPelea(), null)
                    masDaños = tStatsLanzador.getTotalStatConComplemento(Constantes.STAT_MAS_DAÑOS_DE_AGUA, lanzador.getPelea(),
                            null)
                    resMasO = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_FIJA_AGUA, objetivo.getPelea(),
                            null)
                    resPorcO = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_PORC_AGUA, objetivo.getPelea(),
                            objetivo.getPersonaje())
                    redMag = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_REDUCCION_MAGICA, objetivo.getPelea(),
                            null)
                    when (pelea.getTipoPelea()) {
                        Constantes.PELEA_TIPO_DESAFIO, Constantes.PELEA_TIPO_KOLISEO, Constantes.PELEA_TIPO_PVP, Constantes.PELEA_TIPO_RECAUDADOR -> {
                            resPorcO = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_PORC_PVP_AGUA,
                                    objetivo.getPelea(), null)
                            resMasO = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_FIJA_PVP_AGUA,
                                    objetivo.getPelea(), null)
                        }
                    }
                }
                Constantes.ELEMENTO_AIRE + 10, Constantes.ELEMENTO_AIRE -> {
                    statC = tStatsLanzador.getTotalStatConComplemento(Constantes.STAT_MAS_AGILIDAD, lanzador.getPelea(), null)
                    masDaños = tStatsLanzador.getTotalStatConComplemento(Constantes.STAT_MAS_DAÑOS_DE_AIRE, lanzador.getPelea(),
                            null)
                    resMasO = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_FIJA_AIRE, objetivo.getPelea(),
                            null)
                    resPorcO = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_PORC_AIRE, objetivo.getPelea(),
                            objetivo.getPersonaje())
                    redMag = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_REDUCCION_MAGICA, objetivo.getPelea(),
                            null)
                    when (pelea.getTipoPelea()) {
                        Constantes.PELEA_TIPO_DESAFIO, Constantes.PELEA_TIPO_KOLISEO, Constantes.PELEA_TIPO_PVP, Constantes.PELEA_TIPO_RECAUDADOR -> {
                            resPorcO = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_PORC_PVP_AIRE,
                                    objetivo.getPelea(), null)
                            resMasO = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_FIJA_PVP_AIRE,
                                    objetivo.getPelea(), null)
                        }
                    }
                }
            }
            if (elemento >= 10) {
                elemento -= 10
                statC = 0
                masDaños = 0
                porcDaños = 0
            } else {
                masDaños += tStatsLanzador.getTotalStatParaMostrar(Constantes.STAT_MAS_DAÑOS, lanzador.getPelea(), null)
                porcDaños += tStatsLanzador.getTotalStatParaMostrar(Constantes.STAT_MAS_PORC_DAÑOS, lanzador.getPelea(),
                        null)
            }
            multiplicaDaños = tStatsLanzador.getTotalStatParaMostrar(Constantes.STAT_MULTIPLICA_DAÑOS, lanzador.getPelea(),
                    null)
            if (MainServidor.PARAM_INFO_DAÑO_BATALLA) {
                info.append("\n")
                info.append("PtsStats: " + statC + " +Dmg: " + masDaños + " %Dmg: " + porcDaños + " +ResTarget: " + resMasO
                        + " %ResTarget: " + resPorcO + " RedMagTarget: " + redMag + " RedPhyTarget: " + redFis + " xDmg: "
                        + multiplicaDaños)
            }
            // resPorcO = maxResistencia(objetivo, resPorcO);
            var armaClase = 90
            var dominioArma = 0
            if (lanzaPerso != null && esCaC) {
                multiDañoPJ = MULTIPLICADOR_DAÑO_CAC
                if (MainServidor.LIMITE_STATS_KOLI_PERSONALIZADOS && pelea != null && pelea.getTipoPelea() === Constantes.PELEA_TIPO_KOLISEO && MULTIPLICADOR_DAÑO_CAC_KOLI > 0) {
                    multiDañoPJ = MULTIPLICADOR_DAÑO_CAC_KOLI
                } else if (MainServidor.LIMITE_STATS_PVP_PERSONALIZADOS && pelea != null && pelea.getTipoPelea() === Constantes.PELEA_TIPO_PVP && MULTIPLICADOR_DAÑO_CAC_PVP > 0) {
                    multiDañoPJ = MULTIPLICADOR_DAÑO_CAC_PVP
                }
                var armaTipo = 0
                try {
                    armaTipo = lanzaPerso.getObjPosicion(Constantes.OBJETO_POS_ARMA).getObjModelo().getTipo()
                    val clase: Int = lanzaPerso.getClaseID(true)
                    dominioArma = lanzador.getValorPorPrimerYEfectoID(165, armaTipo)
                    when (armaTipo) {
                        Constantes.OBJETO_TIPO_ARCO -> armaClase = if (clase == Constantes.CLASE_SRAM) {
                            95
                        } else {
                            if (clase != Constantes.CLASE_OCRA) {
                                break
                            }
                            100
                        }
                        Constantes.OBJETO_TIPO_VARITA -> armaClase = if (clase == Constantes.CLASE_FECA || clase == Constantes.CLASE_XELOR) {
                            95
                        } else {
                            if (clase != Constantes.CLASE_ANIRIPSA) {
                                break
                            }
                            100
                        }
                        Constantes.OBJETO_TIPO_BASTON -> armaClase = if (clase == Constantes.CLASE_ANIRIPSA || clase == Constantes.CLASE_OSAMODAS || clase == Constantes.CLASE_PANDAWA) {
                            95
                        } else {
                            if (clase != Constantes.CLASE_FECA && clase != Constantes.CLASE_SADIDA) {
                                break
                            }
                            100
                        }
                        Constantes.OBJETO_TIPO_DAGAS -> armaClase = if (clase == Constantes.CLASE_OCRA || clase == Constantes.CLASE_ZURCARAK) {
                            95
                        } else {
                            if (clase != Constantes.CLASE_SRAM) {
                                break
                            }
                            100
                        }
                        Constantes.OBJETO_TIPO_ESPADA -> {
                            if (clase != Constantes.CLASE_YOPUKA && clase != Constantes.CLASE_ZURCARAK) {
                                break
                            }
                            armaClase = 100
                        }
                        Constantes.OBJETO_TIPO_MARTILLO -> armaClase = if (clase == Constantes.CLASE_ANUTROF || clase == Constantes.CLASE_YOPUKA || clase == Constantes.CLASE_SADIDA) {
                            95
                        } else {
                            if (clase != Constantes.CLASE_OSAMODAS && clase != Constantes.CLASE_XELOR) {
                                break
                            }
                            100
                        }
                        Constantes.OBJETO_TIPO_PALA -> {
                            if (clase != Constantes.CLASE_ANUTROF) {
                                break
                            }
                            armaClase = 100
                        }
                        Constantes.OBJETO_TIPO_HACHA -> {
                            if (clase != Constantes.CLASE_PANDAWA) {
                                break
                            }
                            armaClase = 100
                        }
                    }
                    if (MainServidor.PARAM_INFO_DAÑO_BATALLA) {
                        info.append(" %DmgWeapon: $dominioArma ClasseWeapon: $armaClase")
                    }
                } catch (e: Exception) {
                }
            }
            if (statC < 0) {
                statC = 0
            }
            if (tipoDaño == TipoDaño.TRAMPA) {
                val porcTrampa: Int = tStatsLanzador.getTotalStatParaMostrar(Constantes.STAT_MAS_PORC_DAÑOS_TRAMPA,
                        lanzador.getPelea(), null)
                val masTrampa: Int = tStatsLanzador.getTotalStatParaMostrar(Constantes.STAT_MAS_DAÑOS_TRAMPA,
                        lanzador.getPelea(), null)
                porcDaños += porcTrampa
                masDaños += masTrampa
                if (MainServidor.PARAM_INFO_DAÑO_BATALLA) {
                    info.append(" %DmgTrap: $porcTrampa +DmgTrap: $masTrampa")
                }
            }
            if (multiplicaDaños < 1) {
                multiplicaDaños = 1
            }
            if (MainServidor.PARAM_INFO_DAÑO_BATALLA) {
                info.append("\n")
                info.append("Formule: ")
                info.append("DmgFinal = DmgStart X xDmg {$dañoInicial * $multiplicaDaños}")
            }
            var dañoFinal = dañoInicial * multiplicaDaños
            /*
         * if (lanzador.getPersonaje() != null) { int clase =
         * lanzador.getPersonaje().getSubclase(); if (clase == 1) { dañoFinal -=
         * (dañoFinal * 10 / 100); } if (clase == 3) { dañoFinal += (dañoFinal * 5 /
         * 100); } if (clase == 2) { dañoFinal -= (dañoFinal * 5 / 100); } }
         */if (esCaC) {
                if (MainServidor.PARAM_INFO_DAÑO_BATALLA) {
                    info.append("\n")
                    info.append("DmgFinal = DmgFinal X ((100 + %DmgWeapon) / 100) X (ClasseWeapon / 100)   {" + dañoFinal
                            + " * " + " ((100 + " + dominioArma + ") / 100f) * (" + armaClase + "/ 100f)}")
                }
                dañoFinal = dañoFinal * ((100 + dominioArma) / 100f) * (armaClase / 100f)
            }
            if (MainServidor.PARAM_INFO_DAÑO_BATALLA) {
                info.append("\n")
                info.append("DmgFinal = (DmgFinal X (100 + PtsStats + %Dmg) / 100) + +Dmg   {(" + dañoFinal + " * "
                        + " (100 + " + statC + " + " + porcDaños + ") / 100f) + " + masDaños + " }")
            }
            dañoFinal = dañoFinal * (100 + statC + porcDaños) / 100f
            dañoFinal += masDaños.toFloat()
            if (esGC) {
                val dañoCritico: Int = tStatsLanzador.getTotalStatParaMostrar(Constantes.STAT_MAS_DAÑOS_CRITICOS,
                        lanzador.getPelea(), null)
                val redDañoCritico: Int = tStatsObjetivo.getTotalStatParaMostrar(Constantes.STAT_MAS_REDUCCION_CRITICOS,
                        objetivo.getPelea(), null)
                if (MainServidor.PARAM_INFO_DAÑO_BATALLA) {
                    info.append("\n")
                    info.append("DmgFinal = DmgFinal + +DmgCritique - +RedCritTarget  {" + dañoFinal + " + " + dañoCritico
                            + " + " + redDañoCritico + "}")
                }
                dañoFinal += dañoCritico.toFloat()
                dañoFinal -= redDañoCritico.toFloat()
            }
            dañoFinal *= if (lanzador.getMob() != null) {
                if (lanzador.esInvocacion()) {
                    MULTIPLICADOR_DAÑO_MOB - 0.3f
                } else {
                    MULTIPLICADOR_DAÑO_MOB
                }
            } else {
                multiDañoPJ
            }
            // if (hechizoID == 2006 && elemento != -1) {//hechizo la sacrificada
            // daño = lanzador.getPDVMaxSinBuff();
            // }
            if (dañoFinal < 0) {
                dañoFinal = 0f
            }
            if (tipoDaño != TipoDaño.POST_TURNOS) {
                redArmadO = getDañosReducidos(objetivo, elemento)
                if (redArmadO > 0) {
                    GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, Constantes.STAT_MAS_DAÑOS_REDUCIDOS_NO_FECA, lanzador.getID().toString() + "", objetivo.getID().toString() + "," + redArmadO)
                }
            } else {
                resMasO = 0
                redArmadO = 0
            }
            val dañoReducido = (dañoFinal * resPorcO / 100f).toInt()
            if (MainServidor.PARAM_INFO_DAÑO_BATALLA) {
                info.append("\n")
                info.append("DmgFinal = DmgFinal - (DmgFinal X  %ResTarget / 100)  {" + dañoFinal + " - (" + dañoFinal
                        + " * " + resPorcO + " / 100f)}")
            }
            dañoFinal -= dañoReducido.toFloat()
            val resistencias = resMasO + redMag + redFis + redArmadO
            if (MainServidor.PARAM_INFO_DAÑO_BATALLA) {
                info.append("\n")
                info.append("DmgFinal = DmgFinal - (ResistElem + RedMagic + RedPhysic + ArmSpell)  {" + dañoFinal + " - ("
                        + resMasO + " + " + redMag + " + " + redFis + " + " + redArmadO + ")}")
            }
            dañoFinal -= resistencias.toFloat()
            if (dañoFinal < 0) {
                dañoFinal = 0f
            }
            for (b in objetivo.getBuffsPelea()) {
                if (b.getCondicionBuff().isEmpty()) {
                    continue
                }
                var condicion = ""
                when (elemento) {
                    Constantes.ELEMENTO_NEUTRAL -> condicion = "N"
                    Constantes.ELEMENTO_TIERRA -> condicion = "E"
                    Constantes.ELEMENTO_FUEGO -> condicion = "F"
                    Constantes.ELEMENTO_AGUA -> condicion = "W"
                    Constantes.ELEMENTO_AIRE -> condicion = "A"
                }
                if (b.getCondicionBuff().contains("DMG_ALL") || b.getCondicionBuff().contains("DMG_$condicion")
                        || b.getCondicionBuff().contains("D_") && b.getCondicionBuff().contains(condicion)) {
                    b.aplicarBuffCondicional(objetivo)
                }
            }
            if (MainServidor.PARAM_INFO_DAÑO_BATALLA && lanzaPerso != null) {
                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(lanzaPerso, info.toString())
            }
            if ((pelea.getTipoPelea() === Constantes.PELEA_TIPO_PVM
                            || pelea.getTipoPelea() === Constantes.PELEA_TIPO_PVM_NO_ESPADA) && pelea.getRetos() != null && lanzador.esNoIA()) {
                for (entry in pelea.getRetos().entrySet()) {
                    val reto: Reto = entry.getValue()
                    val retoID: Byte = entry.getKey()
                    val exitoReto: EstReto = reto.getEstado()
                    if (exitoReto !== Reto.EstReto.EN_ESPERA) {
                        continue
                    }
                    when (retoID) {
                        Constantes.RETO_BLITZKRIEG -> if (objetivo.getEquipoBin() === 1) {
                            if (reto.getLuchMob() == null) {
                                reto.setMob(objetivo)
                            }
                        }
                    }
                }
            }
            pelea.setUltimoTipoDaño(tipoDaño)
            objetivo.setUltimoElementoDaño(elemento)
            return dañoFinal.toInt()
        }

        fun buffFinTurno(luchTurno: Luchador) {
            var cadaCuantosPA: Int
            var nroPAusados: Int
            // efecto daños por PA usados
            for (buff in luchTurno.getBuffsPorEfectoID(131)) {
                if (luchTurno.estaMuerto()) {
                    continue
                }
                val dañoPorPA: Int = buff.getSegundoValor()
                if (dañoPorPA <= 0) {
                    continue
                }
                cadaCuantosPA = buff.getPrimerValor()
                nroPAusados = Math.floor(luchTurno.getPAUsados() / cadaCuantosPA)
                val statsTotal: TotalStats = buff.getLanzador().getTotalStats()
                val inteligencia: Int = statsTotal.getTotalStatParaMostrar(Constantes.STAT_MAS_INTELIGENCIA,
                        luchTurno.getPelea(), null)
                val pDaños: Int = statsTotal.getTotalStatParaMostrar(Constantes.STAT_MAS_PORC_DAÑOS, luchTurno.getPelea(),
                        null)
                val masDaños: Int = statsTotal.getTotalStatParaMostrar(Constantes.STAT_MAS_DAÑOS, luchTurno.getPelea(),
                        null)
                val reduccion: Int = statsTotal.getTotalStatParaMostrar(Constantes.STAT_MAS_DAÑOS_REDUCIDOS_NO_FECA,
                        luchTurno.getPelea(), null)
                val factor = (100 + inteligencia + pDaños) / 100f
                var daño = (dañoPorPA * factor * nroPAusados).toInt() + masDaños
                if (reduccion > 0) {
                    GestorSalida.ENVIAR_GA_ACCION_PELEA(luchTurno.getPelea(), 7,
                            Constantes.STAT_MAS_DAÑOS_REDUCIDOS_NO_FECA, luchTurno.getID().toString() + "",
                            luchTurno.getID().toString() + "," + reduccion)
                    daño -= reduccion
                }
                val resPorcO: Int = luchTurno.getTotalStats().getTotalStatConComplemento(Constantes.STAT_MAS_RES_PORC_FUEGO,
                        luchTurno.getPelea(), null)
                if (resPorcO >= 100) {
                    daño = 0
                }
                if (daño <= 0) {
                    continue
                }
                luchTurno.getPelea().setUltimoTipoDaño(TipoDaño.POST_TURNOS)
                restarPDVLuchador(luchTurno.getPelea(), luchTurno, buff.getLanzador(), null, daño, 0)
                if (luchTurno.estaMuerto()) {
                    break
                }
            }
        }

        private fun restarPDVLuchador(pelea: Pelea?, objetivo: Luchador?, lanzador: Luchador, afectados: StringBuilder?,
                                      valor: Int, elemento: Int): Int {
            var valor = valor
            if (valor < 0) {
                var cura = -valor
                if (cura + objetivo.getPDVSinBuff() > objetivo.getPDVMaxSinBuff()) {
                    cura = objetivo.getPDVMaxSinBuff() - objetivo.getPDVSinBuff()
                }
                valor = -cura
            }
            val Ush: Luchador? = objetivo
            if (Ush != null) {
                if (Ush.tieneEstado(78)) {
                    if (!pelea.UshGaleshSegundo) {
                        if (Camino.distanciaDosCeldas(pelea.getMapaCopia(), objetivo.getCeldaPelea().getID(),
                                        lanzador.getCeldaPelea().getID()) > 1) {
                            pelea.UshGaleshSegundo = true
                            GestorSalida.GAME_SEND_MESSAGE_PELEA(pelea, 7, "UshGalesh2 ahora es vulnerable ahora")
                        } else {
                            GestorSalida.GAME_SEND_MESSAGE_PELEA(pelea, 7, "UshGalesh2 es inmmune")
                            pelea.UshGaleshSegundo = false
                        }
                        return 0
                    } else {
                        if (Camino.distanciaDosCeldas(pelea.getMapaCopia(), objetivo.getCeldaPelea().getID(),
                                        lanzador.getCeldaPelea().getID()) > 1) {
                            GestorSalida.GAME_SEND_MESSAGE_PELEA(pelea, 7, "UshGalesh2 es inmmune")
                            pelea.UshGaleshSegundo = false
                            return 0
                        } else {
                            pelea.UshGaleshSegundo = true
                        }
                    }
                }
                if (Ush.tieneEstado(77)) {
                    if (!pelea.UshGalesh) {
                        if (Camino.distanciaDosCeldas(pelea.getMapaCopia(), objetivo.getCeldaPelea().getID(),
                                        lanzador.getCeldaPelea().getID()) <= 1) {
                            pelea.UshGalesh = true
                            GestorSalida.GAME_SEND_MESSAGE_PELEA(pelea, 7, "UshGalesh ahora es vulnerable ahora")
                        } else {
                            GestorSalida.GAME_SEND_MESSAGE_PELEA(pelea, 7, "UshGalesh es inmmune")
                            pelea.UshGalesh = false
                        }
                        return 0
                    } else {
                        if (Camino.distanciaDosCeldas(pelea.getMapaCopia(), objetivo.getCeldaPelea().getID(),
                                        lanzador.getCeldaPelea().getID()) <= 1) {
                            GestorSalida.GAME_SEND_MESSAGE_PELEA(pelea, 7, "UshGalesh es inmmune")
                            pelea.UshGalesh = false
                            return 0
                        } else {
                            pelea.UshGalesh = true
                        }
                    }
                }
            }
            val vitalidad: Int = objetivo.getBuffsStats().getStatParaMostrar(Constantes.STAT_MAS_VITALIDAD)
            objetivo.restarPDV(valor)
            val pdv: Int = objetivo.getPDVSinBuff() + vitalidad
            if (pdv <= 0) {
                valor += pdv // si pdv es menor a 0 le resta al daño
                GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, lanzador.getID().toString() + "",
                        objetivo.getID().toString() + "," + -valor)
                if (pelea != null) {
                    pelea.addMuertosReturnFinalizo(objetivo, lanzador)
                }
            } else if (afectados == null) {
                GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, lanzador.getID().toString() + "",
                        objetivo.getID().toString() + "," + -valor)
            } else {
                if (afectados.length() > 0) {
                    afectados.append("¬")
                }
                afectados.append(objetivo.getID().toString() + "," + -valor + "," + elemento)
            }
            return valor
        }
    }
}