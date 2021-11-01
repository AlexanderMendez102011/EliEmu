package estaticos

import java.util.ArrayList
import java.util.Map
import java.util.TreeMap
import java.util.concurrent.atomic.AtomicReference
import java.util.regex.Pattern
import variables.hechizo.StatHechizo
import variables.mapa.Celda
import variables.mapa.Mapa
import variables.mob.GrupoMob
import variables.pelea.Luchador
import variables.pelea.Pelea
import variables.pelea.Trampa
import variables.personaje.Personaje
import estaticos.Mundo.Duo

object Camino {
    private val DIRECCIONES = charArrayOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h')
    private val COORD_ALREDEDOR = arrayOf(byteArrayOf(1, 0), byteArrayOf(0, 1), byteArrayOf(-1, 0), byteArrayOf(0, -1))
    fun getPathPelea(mapa: Mapa, celdaInicio: Short,
                     celdaDestino: Short, PM: Int, tacleado: Luchador?, ignoraLuchadores: Boolean): Duo<Integer, ArrayList<Celda>>? {
        var celdaDestino = celdaDestino
        var PM = PM
        var intentos = 0
        while (intentos < 5) {
            try {
                if (celdaInicio == celdaDestino || mapa.getCelda(celdaInicio) == null || mapa.getCelda(celdaDestino) == null) {
                    return null
                }
                if (PM < 0) {
                    PM = 500
                }
                val ancho: Byte = mapa.getAncho()
                // final int nroLados = 4;
                val diagonales = byteArrayOf(ancho, (ancho - 1).toByte(), (-ancho).toByte(), (-(ancho - 1).toByte()).toByte())
                val unos = byteArrayOf(1, 1, 1, 1)
                val celdas: Map<Short, Celda?> = TreeMap<Short, Celda>()
                celdas.putAll(mapa.getCeldas())
                val celdasCamino1: Map<Short, CeldaCamino?> = TreeMap<Short, CeldaCamino>()
                val celdasCamino2: Map<Short, CeldaCamino?> = TreeMap<Short, CeldaCamino>()
                var ok = true
                val newCeldaCamino = CeldaCamino()
                newCeldaCamino.id = celdaInicio
                newCeldaCamino.cantPM = 0
                newCeldaCamino.valorX = 0
                newCeldaCamino.distEstimada = distanciaEstimada(mapa, celdaInicio, celdaDestino).toInt()
                newCeldaCamino.distEstimadaX = newCeldaCamino.distEstimada
                newCeldaCamino.level = celdas[celdaInicio].getLevel()
                newCeldaCamino.movimiento = celdas[celdaInicio].getMovimiento()
                newCeldaCamino.anterior = null
                celdasCamino1.put(newCeldaCamino.id, newCeldaCamino)
                // pone la primera celda de inicio
                var celdaAnterior: Short = -1
                while (ok) {
                    var sigCelda: Short = -1
                    var distEntreCeldas = 500000
                    for (c in celdasCamino1.values()) {
                        if (c.distEstimadaX < distEntreCeldas) {
                            distEntreCeldas = c.distEstimadaX
                            sigCelda = c.id
                        }
                    }
                    var celdaCamino = celdasCamino1[sigCelda]
                    celdasCamino1.remove(sigCelda)
                    if (celdaCamino!!.anterior != null) {
                        celdaAnterior = celdaCamino.anterior!!.id
                    }
                    if (celdaCamino.id == celdaDestino) {
                        // se llego al objetivo
                        val tempCeldas: ArrayList<Celda> = ArrayList<Celda>()
                        while (celdaCamino!!.id != celdaInicio) {
                            if (celdaCamino.movimiento.toInt() == 0) {
                                tempCeldas.clear()
                            } else {
                                tempCeldas.add(0, celdas[celdaCamino.id])
                            }
                            celdaCamino = celdaCamino.anterior
                        }
                        return Duo<Integer, ArrayList<Celda>>(intentos, tempCeldas)
                    }
                    var enemigoAlr = false
                    if (tacleado != null) {
                        if (hayAlrededorAmigoOEnemigo(mapa, tacleado, false, true)) {
                            enemigoAlr = true
                        }
                    }
                    val direcciones = listaDirEntreDosCeldas2(mapa, celdaCamino.id, celdaDestino, celdaAnterior)
                    var puedeLlegarDestino = false
                    if (!enemigoAlr) {
                        for (i in 0..3) {
                            val direccion = direcciones[i]
                            val tempCeldaID = (celdaCamino.id + diagonales[direccion.toInt()]).toShort()
                            if (celdas[tempCeldaID] == null) {
                                continue
                            }
                            if (Math.abs(celdas[tempCeldaID].getCoordX() - celdas[celdaCamino.id].getCoordX()) <= 53) {
                                val tempCelda: Celda? = celdas[tempCeldaID]
                                val tempLevelCelda: Byte = tempCelda.getLevel()
                                val sinLuchador = if (tempCeldaID == celdaDestino || ignoraLuchadores) true else if (tempCelda.getPrimerLuchador() == null) true else false
                                puedeLlegarDestino = if (tempCeldaID == celdaDestino && tempCelda.getMovimiento() === 1) true else false
                                val caminable = celdaCamino.level.toInt() == -1 || Math.abs(tempLevelCelda - celdaCamino.level) < 2
                                if (caminable && tempCelda.getActivo() && sinLuchador) {
                                    val aaaa = if (tempCelda.getMovimiento() === 0 || tempCelda.getMovimiento() === 1) 1000 else 0
                                    val valorX: Short = (celdaCamino.valorX + unos[direccion.toInt()] + aaaa + if (tempCelda
                                                    .getMovimiento() === 1 && puedeLlegarDestino) -1000 else (if (direccion.toShort() != celdaCamino.direccion) 0.5 else 0) + (5 - tempCelda.getMovimiento()) / 3).toShort()
                                    val cantMov = (celdaCamino.cantPM + unos[direccion.toInt()]).toShort()
                                    var tempValorX: Short = -1
                                    if (celdasCamino1[tempCeldaID] != null) {
                                        tempValorX = celdasCamino1[tempCeldaID]!!.valorX
                                    } else if (celdasCamino2[tempCeldaID] != null) {
                                        tempValorX = celdasCamino2[tempCeldaID]!!.valorX
                                    }
                                    if ((tempValorX.toInt() == -1 || tempValorX > valorX) && cantMov <= PM) {
                                        if (celdasCamino2[tempCeldaID] != null) {
                                            celdasCamino2.remove(tempCeldaID)
                                        }
                                        val tempCeldaCamino = CeldaCamino()
                                        tempCeldaCamino.id = tempCeldaID
                                        tempCeldaCamino.cantPM = cantMov
                                        tempCeldaCamino.valorX = valorX
                                        tempCeldaCamino.distEstimada = distanciaEstimada(mapa, tempCeldaID, celdaDestino).toInt()
                                        tempCeldaCamino.distEstimadaX = tempCeldaCamino.valorX + tempCeldaCamino.distEstimada + i * 3
                                        tempCeldaCamino.direccion = direccion.toShort()
                                        tempCeldaCamino.level = tempLevelCelda.toShort()
                                        tempCeldaCamino.movimiento = tempCelda.getMovimiento()
                                        tempCeldaCamino.anterior = celdaCamino
                                        celdasCamino1.put(tempCeldaID, tempCeldaCamino)
                                    }
                                }
                            }
                        }
                    }
                    celdasCamino2.put(celdaCamino.id, CeldaCamino())
                    celdasCamino2[celdaCamino.id]!!.valorX = celdaCamino.valorX
                    ok = false
                    for (c in celdasCamino1.values()) {
                        if (c == null) {
                            continue
                        }
                        ok = true
                        break
                    }
                }
                return null
            } catch (e: Exception) {
                celdaDestino = celdaMasCercanaACeldaObjetivo(mapa, celdaDestino, celdaInicio, null, false)
                intentos++
            }
        }
        return null
    }

    fun nroCeldasAMover(mapa: Mapa, pelea: Pelea?, pathRef: AtomicReference<String?>,
                        celdaInicio: Short, celdaFinal: Short, perso: Personaje): Short {
        var nuevaCelda = celdaInicio
        var movimientos: Short = 0
        val path: String = pathRef.get()
        val nuevoPath = StringBuilder()
        var i = 0
        while (i < path.length()) {
            if (path.length() < i + 3) {
                return movimientos
            }
            val miniPath: String = path.substring(i, i + 3)
            val cDir: Char = miniPath.charAt(0)
            val celdaTemp: Short = Encriptador.hashACeldaID(miniPath.substring(1))
            // if (pelea != null && i > 0) {
            // if (getEnemigoAlrededor(nuevaCelda, mapa, null, pelea.getLuchadorTurno().getEquipoBin())
            // != null) {
            // pathRef.set(nuevoPath.toString());
            // return (short) (movimientos + 10000);
            // }
            // for (final Trampa trampa : pelea.getTrampas()) {
            // final int dist = distanciaDosCeldas(mapa, trampa.getCelda().getID(), nuevaCelda);
            // if (dist <= trampa.getTamaño()) {
            // pathRef.set(nuevoPath.toString());
            // return (short) (movimientos + 10000);
            // }
            // }
            // if (pelea.getMapaCopia().getCelda(nuevaCelda).getPrimerLuchador() != null) {
            // pathRef.set(nuevoPath.toString());
            // return (short) (movimientos + 20000);
            // }
            // }
            val aPathInfos: Array<String> = pathSimpleValido(nuevaCelda, celdaTemp, getIndexPorDireccion(cDir).toInt(), mapa, pelea,
                    celdaFinal, perso).split(Pattern.quote(";"))
            val resultado = aPathInfos[0]
            val nroCeldas: Int = Integer.parseInt(aPathInfos[1])
            if (aPathInfos.size > 2) {
                nuevaCelda = Short.parseShort(aPathInfos[2])
            }
            when (resultado) {
                "invisible" -> {
                    (movimientos += nroCeldas.toShort()).toShort()
                    nuevoPath.append(cDir + Encriptador.celdaIDAHash(nuevaCelda))
                    pathRef.set(nuevoPath.toString())
                    return (movimientos + 20000).toShort()
                }
                "stop", "trampa" -> {
                    (movimientos += nroCeldas.toShort()).toShort()
                    nuevoPath.append(cDir + Encriptador.celdaIDAHash(nuevaCelda))
                    pathRef.set(nuevoPath.toString())
                    return (movimientos + 10000).toShort()
                }
                "no" -> {
                    pathRef.set(nuevoPath.toString())
                    return -1000
                }
                "ok" -> {
                    nuevaCelda = celdaTemp
                    (movimientos += nroCeldas.toShort()).toShort()
                }
            }
            nuevoPath.append(cDir + Encriptador.celdaIDAHash(nuevaCelda))
            i += 3
        }
        pathRef.set(nuevoPath.toString())
        return movimientos
    }

    private fun pathSimpleValido(celdaID: Short, celdaSemiFinal: Short, dir: Int,
                                 mapa: Mapa, pelea: Pelea?, celdaFinalDest: Short, perso: Personaje): String {
        var ultimaCelda = celdaID
        var _nroMovimientos = 1
        while (_nroMovimientos <= 64) {
            val celdaTempID = getSigIDCeldaMismaDir(ultimaCelda, dir, mapa, pelea != null)
            val celdaTemp: Celda = mapa.getCelda(celdaTempID)
            if (celdaTemp == null || !celdaTemp.esCaminable(true)) {
                _nroMovimientos--
                return "stop;$_nroMovimientos;$ultimaCelda"
            }
            if (pelea != null) {
                val ocupado: Luchador = mapa.getCelda(celdaTempID).getPrimerLuchador()
                val luchTurno: Luchador = pelea.getLuchadorTurno()
                if (ocupado != null) {
                    _nroMovimientos--
                    return if (ocupado.esInvisible(luchTurno.getID())) {
                        "invisible;$_nroMovimientos;$ultimaCelda"
                    } else {
                        "stop;$_nroMovimientos;$ultimaCelda"
                    }
                }
                if (celdaTempID != celdaFinalDest) {
                    // si algun luchador esta alrededor por donde va a pasar
                    val alrededor: Luchador? = getEnemigoAlrededor(celdaTempID, mapa, null, if (luchTurno.esIAChafer()) 3 else luchTurno.getEquipoBin())
                    if (alrededor != null && alrededor.getID() !== luchTurno.getID()) {
                        return if (alrededor.esInvisible(luchTurno.getID())) {
                            "invisible;$_nroMovimientos;$celdaTempID"
                        } else {
                            "stop;$_nroMovimientos;$celdaTempID"
                        }
                    }
                    // si se topa con una trampa
                    if (pelea.getTrampas() != null) {
                        for (trampa in pelea.getTrampas()) {
                            val dist = distanciaDosCeldas(mapa, trampa.getCelda().getID(), celdaTempID).toInt()
                            if (dist <= trampa.getTamaño()) {
                                return "trampa;$_nroMovimientos;$celdaTempID"
                            }
                        }
                    }
                }
            } else {
                try {
                    for (p in mapa.getArrayPersonajes()) {
                        if (!Constantes.puedeAgredir(perso, p)) {
                            continue
                        }
                        if (p.getAlineacion() === Constantes.ALINEACION_BONTARIANO && perso
                                        .getAlineacion() === Constantes.ALINEACION_NEUTRAL || p
                                        .getAlineacion() === Constantes.ALINEACION_BRAKMARIANO && perso
                                        .getAlineacion() === Constantes.ALINEACION_NEUTRAL || (p.getAlineacion() === Constantes.ALINEACION_MERCENARIO
                                        && perso.getAlineacion() === Constantes.ALINEACION_NEUTRAL)) {
                            continue
                        }
                        val agroP: Int = p.getTotalStats().getTotalStatParaMostrar(Constantes.STAT_AGREDIR_AUTOMATICAMENTE, p.getPelea(), null)
                        val agroPerso: Int = perso.getTotalStats().getTotalStatParaMostrar(Constantes.STAT_AGREDIR_AUTOMATICAMENTE, p.getPelea(), null)
                        if (agroP <= 0 && agroPerso <= 0) {
                            continue
                        }
                        val distAgro = if (agroPerso >= agroP) agroPerso else agroP
                        if (distanciaDosCeldas(mapa, p.getCelda().getID(), celdaTempID) <= distAgro) {
                            return "stop;$_nroMovimientos;$celdaTempID"
                        }
                    }
                } catch (e: Exception) {
                }
                try {
                    for (gm in mapa.getGrupoMobsTotales().values()) {
                        if (!perso.estaDisponible(true, true)) {
                            continue
                        }
                        if (gm.getDistAgresion() <= 0) {
                            continue
                        }
                        if (perso.getAlineacion() === gm.getAlineacion()) {
                            continue
                        }
                        if (gm.getAlineacion() === Constantes.ALINEACION_BONTARIANO && perso
                                        .getAlineacion() === Constantes.ALINEACION_NEUTRAL || gm
                                        .getAlineacion() === Constantes.ALINEACION_BRAKMARIANO && perso
                                        .getAlineacion() === Constantes.ALINEACION_NEUTRAL || gm
                                        .getAlineacion() === Constantes.ALINEACION_MERCENARIO && perso
                                        .getAlineacion() === Constantes.ALINEACION_NEUTRAL) {
                            continue
                        }
                        if (distanciaDosCeldas(mapa, gm.getCeldaID(), celdaTempID) <= gm.getDistAgresion()) {
                            return "stop;$_nroMovimientos;$celdaTempID"
                        }
                    }
                } catch (e: Exception) {
                }
                if (celdaTempID == celdaFinalDest) {
                    if (celdaTemp.getObjetoInteractivo() != null) {
                        // para hacer q los trigos, cereales e interactivos caminables
                        // los demas como nidos bwaks y otros se muevan hasta ahi
                        return "stop;$_nroMovimientos;$ultimaCelda"
                    }
                }
                if (!celdaTemp.accionesIsEmpty()) {
                    return "stop;$_nroMovimientos;$celdaTempID"
                }
            }
            if (celdaTempID == celdaSemiFinal) {
                return "ok;$_nroMovimientos"
            }
            ultimaCelda = celdaTempID
            _nroMovimientos++
        }
        return "no" + ";" + 0
    }

    fun cellArroundCaseIDisOccuped(fight: Pelea, cell: Int): Boolean {
        val dirs = charArrayOf('b', 'd', 'f', 'h')
        val Cases: ArrayList<Short> = ArrayList<Short>()
        var array: CharArray
        val length: Int = dirs.also { array = it }.size
        var i = 0
        while (i < length) {
            val dir = array[i]
            val caseID = getSigIDCeldaMismaDir1(cell, dir, fight.getMapaCopia(), true)
            Cases.add(caseID.toShort())
            ++i
        }
        var ha = 0
        for (o in 0 until Cases.size()) {
            val cellx: Celda = fight.getMapaCopia().getCelda(Cases.get(o))
            if (fight.getMapaCopia().getCelda(Cases.get(o)) != null) if (cellx.getPrimerLuchador() != null) {
                ++ha
            }
        }
        return ha != 4
    }

    fun getCeldas(mapa: Mapa?, celdaInicio: Short, alcanceMax: Int, sH: StatHechizo?, pelea: Pelea, lanzador: Luchador): ArrayList<Short> {
        val tempPath: ArrayList<Short> = ArrayList<Short>()
        val dirs = charArrayOf('b', 'd', 'f', 'h')
        var lastcell: Short = 0
        for (d in dirs) {
            lastcell = celdaInicio
            for (a in 0 until alcanceMax) {
                val sigCelda = getSigIDCeldaMismaDir(lastcell, d.code, mapa, true)
                if (sigCelda.toInt() != 0) lastcell = sigCelda
                if (mapa == null) break
                val C: Celda = mapa.getCelda(sigCelda) ?: continue
                if (C.esCaminable(false) && C.getPrimerLuchador() == null) {
                    if (!tempPath.contains(sigCelda)) {
                        if (sH != null) {
                            if (pelea.puedeLanzarHechizo(lanzador, sH, C, lanzador.getCeldaPelea().getID()) != null) {
                                tempPath.add(sigCelda)
                            }
                        } else {
                            tempPath.add(sigCelda)
                        }
                        if (a > 0) {
                            var milastc: Short = 0
                            for (b in 0 until a) {
                                if (milastc.toInt() == 0) milastc = sigCelda
                                var dx = 0.toChar()
                                when (d) {
                                    'b' -> dx = 'e'
                                    'd' -> dx = 'g'
                                    'f' -> dx = 'a'
                                    'h' -> dx = 'c'
                                }
                                if (dx.code != 0) {
                                    val lacell = getSigIDCeldaMismaDir(milastc, dx.code, mapa, false)
                                    milastc = lacell
                                    val C1: Celda = mapa.getCelda(lacell) ?: continue
                                    if (C1.esCaminable(false) && C1.getPrimerLuchador() == null) {
                                        if (!tempPath.contains(lacell)) {
                                            if (sH != null) {
                                                if (pelea.puedeLanzarHechizo(lanzador, sH, C1, lanzador.getCeldaPelea().getID()) != null) {
                                                    tempPath.add(lacell)
                                                }
                                            } else {
                                                tempPath.add(lacell)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return tempPath
    }

    fun celdaCercanas(celdas: ArrayList<Short?>, celdadestino: Short, mapa: Mapa): Short {
        var dist = 1000
        var celdafinal: Short = -1
        for (celda in celdas) {
            val dis = distanciaDosCeldas(mapa, celda, celdadestino).toInt()
            val C: Celda = mapa.getCelda(celda) ?: continue
            if (dis < dist && C.esCaminable(false) && C.getLuchadores().size() <= 0) {
                celdafinal = celda
                dist = dis
            }
        }
        return celdafinal
    }

    fun siCeldasEstanEnMismaLinea1(map: Mapa?, c1: Int, c2: Int, dir: Char): Boolean {
        var c1 = c1
        if (c1 == c2) return true
        if (dir != 'z') {
            for (a in 0..69) {
                if (getSigIDCeldaMismaDir1(c1, dir, map, true) == c2) return true
                if (getSigIDCeldaMismaDir1(c1, dir, map, true) == -1) break
                c1 = getSigIDCeldaMismaDir1(c1, dir, map, true)
            }
        } else {
            val dirs = charArrayOf('b', 'd', 'f', 'h')
            for (d in dirs) {
                var c = c1
                for (a in 0..69) {
                    if (getSigIDCeldaMismaDir1(c, d, map, true) == c2) return true
                    c = getSigIDCeldaMismaDir1(c, d, map, true)
                }
            }
        }
        return false
    }

    fun haveFighterOnThisCell(cell: Int, fight: Pelea): Boolean {
        for (f in fight.luchadoresDeEquipo(3)) {
            if (f.getCeldaPelea().getID() === cell && !f.estaMuerto() && !f.estaRetirado() || f.getCeldaPelea().getID() === cell && f.esEstatico()) {
                return true
            }
        }
        return false
    }

    fun getSigIDCeldaMismaDir1(celdaID: Int, direccion: Char, mapa: Mapa?, combate: Boolean): Int {
        if (mapa == null) return -1
        when (direccion) {
            'a' -> return if (combate) -1 else celdaID + 1
            'b' -> return celdaID + mapa.getAncho() // diagonal derecha abajo
            'c' -> return if (combate) -1 else celdaID + (mapa.getAncho() * 2 - 1)
            'd' -> return celdaID + (mapa.getAncho() - 1) // diagonal izquierda abajo
            'e' -> return if (combate) -1 else celdaID - 1
            'f' -> return celdaID - mapa.getAncho() // diagonal izquierda arriba
            'g' -> return if (combate) -1 else celdaID - (mapa.getAncho() * 2 - 1)
            'h' -> return celdaID - mapa.getAncho() + 1 // diagonal derecha arriba
        }
        return -1
    }

    fun getPathComoString(mapa: Mapa?, celdas: ArrayList<Celda?>, celdaInicio: Short, esPelea: Boolean): String {
        val pathStr = StringBuilder()
        var tempCeldaID = celdaInicio
        for (celda in celdas) {
            val dir = direccionEntreDosCeldas(mapa, tempCeldaID, celda.getID(), esPelea)
            if (dir == -1) {
                return ""
            }
            pathStr.append(getDireccionPorIndex(dir))
            pathStr.append(Encriptador.celdaIDAHash(celda.getID()))
            tempCeldaID = celda.getID()
        }
        return pathStr.toString()
    }

    fun getEnemigoAlrededor(celdaID: Short, mapa: Mapa, noRepetir: ArrayList<Integer?>?,
                            equipoBinLanz: Int): Luchador? {
        val celda: Celda = mapa.getCelda(celdaID)
        for (c in COORD_ALREDEDOR) {
            val cell: Celda = mapa.getCeldaPorPos((celda.getCoordX() + c[0]) as Byte, (celda.getCoordY() + c[1]) as Byte)
                    ?: continue
            val luchador: Luchador = cell.getPrimerLuchador() ?: continue
            if (noRepetir != null) {
                if (noRepetir.contains(luchador.getID())) {
                    continue
                }
            }
            if (luchador.getEquipoBin() !== equipoBinLanz) {
                return luchador
            }
        }
        return null
    }

    fun getEnemigosAlrededor1(lanzador: Luchador, mapa: Mapa?, pelea: Pelea?): ArrayList<Luchador>? {
        val dirs = charArrayOf('b', 'd', 'f', 'h')
        val enemy: ArrayList<Luchador> = ArrayList<Luchador>()
        for (dir in dirs) {
            if (mapa == null) continue
            val sigCelda: Celda = mapa.getCelda(getSigIDCeldaMismaDir(lanzador.getCeldaPelea().getID(), dir.code, mapa, false))
                    ?: continue
            val luchador: Luchador = sigCelda.getPrimerLuchador()
            if (luchador != null) {
                if (luchador.getEquipoBin() !== lanzador.getEquipoBin()) {
                    enemy.add(luchador)
                }
            }
        }
        return if (enemy.size() === 0 || enemy.size() === 4) null else enemy
    }

    fun hayAlrededorAmigoOEnemigo(mapa: Mapa, lanzador: Luchador, amigo: Boolean,
                                  invisible: Boolean): Boolean {
        val celda: Celda = lanzador.getCeldaPelea()
        for (c in COORD_ALREDEDOR) {
            val cell: Celda = mapa.getCeldaPorPos((celda.getCoordX() + c[0]) as Byte, (celda.getCoordY() + c[1]) as Byte)
                    ?: continue
            val luchador: Luchador = cell.getPrimerLuchador()
            if (luchador != null && !luchador.estaMuerto()) {
                if (amigo) {
                    if (luchador.getEquipoBin() === lanzador.getEquipoBin()) {
                        return true
                    }
                } else { // enemigo
                    if (luchador.getEquipoBin() !== lanzador.getEquipoBin()) {
                        if (invisible) {
                            if (luchador.esInvisible(lanzador.getID())) {
                                continue
                            }
                        }
                        return true
                    }
                }
            }
        }
        return false
    }

    fun luchadoresAlrededor(mapa: Mapa, pelea: Pelea?, celda: Celda): ArrayList<Luchador> {
        val luchadores: ArrayList<Luchador> = ArrayList()
        for (c in COORD_ALREDEDOR) {
            val cell: Celda = mapa.getCeldaPorPos((celda.getCoordX() + c[0]) as Byte, (celda.getCoordY() + c[1]) as Byte)
                    ?: continue
            val luchador: Luchador = cell.getPrimerLuchador()
            if (luchador != null) {
                luchadores.add(luchador)
            }
        }
        return luchadores
    }

    fun esSiguienteA(celda1: Celda, celda2: Celda): Boolean {
        val x = Math.abs(celda1.getCoordX() - celda2.getCoordX()) as Byte
        val y = Math.abs(celda1.getCoordY() - celda2.getCoordY()) as Byte
        return x.toInt() == 1 && y.toInt() == 0 || x.toInt() == 0 && y.toInt() == 1
    }

    fun esSiguienteA1(celda1: Short, celda2: Short, mapa: Mapa): Boolean {
        val ancho: Int = mapa.getAncho()
        return if (celda1 + (ancho - 1) == celda2.toInt() || celda1 + ancho == celda2.toInt() || celda1 - (ancho - 1) == celda2.toInt() || celda1 - ancho == celda2.toInt()) true else false
    }

    fun distanciaEntreMapas(mapa1: Mapa, mapa2: Mapa): Int {
        return if (mapa1.getSubArea().getArea().getSuperArea() !== mapa2.getSubArea().getArea().getSuperArea()) 10000 else Math.abs(mapa2.getX() - mapa1.getX()) + Math.abs(mapa2.getY() - mapa1.getY())
    }

    fun getSigIDCeldaMismaDir(celdaID: Short, direccion: Int, mapa: Mapa?,
                              combate: Boolean): Short {
        when (direccion) {
            0 -> return (if (combate) -1 else celdaID + 1).toShort() // derecha
            1 -> return (celdaID + mapa.getAncho()) as Short // diagonal derecha abajo
            2 -> return (if (combate) -1 else celdaID + (mapa.getAncho() * 2 - 1)).toShort() // abajo
            3 -> return (celdaID + (mapa.getAncho() - 1)) as Short // diagonal izquierda abajo
            4 -> return (if (combate) -1 else celdaID - 1).toShort() // izquierda
            5 -> return (celdaID - mapa.getAncho()) as Short // diagonal izquierda arriba
            6 -> return (if (combate) -1 else celdaID - (mapa.getAncho() * 2 - 1)).toShort() // arriba
            7 -> return (celdaID - mapa.getAncho() + 1) // diagonal derecha arriba
        }
        return -1
    }

    fun distanciaDosCeldas(mapa: Mapa, celdaInicio: Short, celdaDestino: Short): Short {
        if (celdaInicio == celdaDestino) {
            return 0
        }
        val cInicio: Celda = mapa.getCelda(celdaInicio)
        val cDestino: Celda = mapa.getCelda(celdaDestino)
        if (cInicio == null || cDestino == null) {
            return 0
        }
        val difX: Int = Math.abs(cInicio.getCoordX() - cDestino.getCoordX())
        val difY: Int = Math.abs(cInicio.getCoordY() - cDestino.getCoordY())
        return (difX + difY).toShort()
    }

    fun celdaLejana(celdas: ArrayList<Short?>, celdadestino: Short, mapa: Mapa): Short {
        var dist = 0
        var celdafinal: Short = -1
        for (celda in celdas) {
            val dis = distanciaDosCeldas(mapa, celda, celdadestino).toInt()
            val C: Celda = mapa.getCelda(celda) ?: continue
            if (dis > dist && C.esCaminable(false) && C.getLuchadores().size() <= 0) {
                celdafinal = celda
                dist = dis
            }
        }
        return celdafinal
    }

    private fun distanciaEstimada(mapa: Mapa, celdaInicio: Short, celdaDestino: Short): Short {
        if (celdaInicio == celdaDestino) {
            return 0
        }
        val cInicio: Celda = mapa.getCelda(celdaInicio)
        val cDestino: Celda = mapa.getCelda(celdaDestino)
        if (cInicio == null || cDestino == null) {
            return 0
        }
        val difX: Int = Math.abs(cInicio.getCoordX() - cDestino.getCoordX())
        val difY: Int = Math.abs(cInicio.getCoordY() - cDestino.getCoordY())
        // return (short) Math.sqrt(Math.pow(difX, 2) + Math.pow(difY, 2));
        // era antes pero lo modifique
        return (difX + difY).toShort()
    }

    fun getCeldaDespuesDeEmpujon(pelea: Pelea, celdaInicio: Celda,
                                 celdaObjetivo: Celda, movimientos: Int): Duo<Integer, Short> {
        var movimientos = movimientos
        if (celdaInicio.getID() === celdaObjetivo.getID()) {
            return Duo<Integer, Short>(-1, -1.toShort())
        }
        val mapa: Mapa = pelea.getMapaCopia()
        var dir = direccionEntreDosCeldas(mapa, celdaInicio.getID(), celdaObjetivo.getID(), true)
        var celdaID: Short = celdaObjetivo.getID()
        if (movimientos < 0) {
            dir = getDireccionOpuesta(dir)
            movimientos = -movimientos
        }
        for (i in 0 until movimientos) {
            val sigCeldaID = getSigIDCeldaMismaDir(celdaID, dir, mapa, true)
            val sigCelda: Celda = mapa.getCelda(sigCeldaID)
            if (sigCelda == null || !sigCelda.esCaminable(true) || sigCelda.getPrimerLuchador() != null) {
                return Duo<Integer, Short>(movimientos - i, celdaID)
            }
            if (pelea.getTrampas() != null) {
                for (trampa in pelea.getTrampas()) {
                    val dist = distanciaDosCeldas(mapa, trampa.getCelda().getID(), sigCeldaID).toInt()
                    if (dist <= trampa.getTamaño()) {
                        return Duo<Integer, Short>(0, sigCeldaID)
                    }
                }
            }
            celdaID = sigCeldaID
        }
        return if (celdaID == celdaObjetivo.getID()) {
            Duo<Integer, Short>(-1, -1.toShort())
        } else Duo<Integer, Short>(0, celdaID)
    }

    fun getDireccionOpuesta(dir: Int): Int {
        return correctaDireccion(dir - 4)
    }

    fun siCeldasEstanEnMismaLinea(mapa: Mapa?, c1: Celda, c2: Celda): Boolean {
        return if (c1.getID() === c2.getID()) {
            true
        } else c1.getCoordX() === c2.getCoordX() || c1.getCoordY() === c2.getCoordY()
    }

    fun getIndexPorDireccion(c: Char): Byte {
        var b: Byte = 0
        for (a in DIRECCIONES) {
            if (a == c) {
                return b
            }
            b++
        }
        return 0
    }

    fun getDireccionAleatorio(combate: Boolean): Char {
        return DIRECCIONES[Formulas.getRandomInt(0, if (combate) 7 else 3)]
    }

    fun getDireccionPorIndex(index: Int): Char {
        return DIRECCIONES[index]
    }

    fun getIndexDireccion(c: Char): Byte {
        var b: Byte = 0
        val dirs = charArrayOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h')
        for (a in dirs) {
            if (a == c) return b
            b++
        }
        return 0
    }

    fun direccionEntreDosCeldas(mapa: Mapa?, celdaInicio: Short, celdaDestino: Short,
                                esPelea: Boolean): Int {
        if (celdaInicio == celdaDestino || mapa == null) {
            return -1
        }
        if (!esPelea) {
            val ancho: Byte = mapa.getAncho()
            val alrededores = byteArrayOf(1, ancho, (ancho * 2 - 1).toByte(), (ancho - 1).toByte(), -1, (-ancho).toByte(), (-ancho * 2 + 1).toByte(), (-(ancho - 1).toByte()).toByte())
            val _loc7 = celdaDestino - celdaInicio
            for (_loc8 in 7 downTo 0) {
                if (alrededores[_loc8] == _loc7) {
                    return _loc8
                }
            }
        }
        val cInicio: Celda = mapa.getCelda(celdaInicio)
        val cDestino: Celda = mapa.getCelda(celdaDestino)
        val difX: Int = cDestino.getCoordX() - cInicio.getCoordX()
        val difY: Int = cDestino.getCoordY() - cInicio.getCoordY()
        return if (difX == 0) {
            if (difY > 0) {
                3
            } else {
                7
            }
        } else if (difX > 0) {
            1
        } else {
            5
        }
    }

    fun listaDirEntreDosCeldas(mapa: Mapa?, celdaInicio: Short, celdaDestino: Short): CharArray {
        if (celdaInicio == celdaDestino || mapa == null) {
            return charArrayOf()
        }
        val abc = CharArray(4)
        val b = listaDirEntreDosCeldas2(mapa, celdaInicio, celdaDestino, (-1.toShort()).toShort())
        for (i in 0..3) {
            when (b[i]) {
                0 -> abc[i] = 'b'
                1 -> abc[i] = 'd'
                2 -> abc[i] = 'f'
                3 -> abc[i] = 'h'
            }
        }
        return abc
    }

    fun getDirEntreDosCeldas(mapa: Mapa?, id1: Short, id2: Short): Char {
        if (id1 == id2) return 0
        if (mapa == null) return 0
        val difX = getCeldaCoordenadaX(mapa, id1.toInt()) - getCeldaCoordenadaX(mapa, id2.toInt())
        val difY = getCeldaCoordenadaY(mapa, id1.toInt()) - getCeldaCoordenadaY(mapa, id2.toInt())
        val difXabs: Int = Math.abs(difX)
        val difYabs: Int = Math.abs(difY)
        return if (difXabs > difYabs) {
            if (difX > 0) 'f' else 'b'
        } else {
            if (difY > 0) 'h' else 'd'
        }
    }

    fun getDirEntreDosCeldas(celdaID1: Int, celdaID2: Int, mapa: Mapa?, combate: Boolean): Char {
        val direcciones: ArrayList<Character> = ArrayList<Character>()
        direcciones.add('b')
        direcciones.add('d')
        direcciones.add('f')
        direcciones.add('h')
        if (!combate) {
            direcciones.add('a')
            direcciones.add('b')
            direcciones.add('c')
            direcciones.add('d')
        }
        for (c in direcciones) {
            var celda = celdaID1
            for (i in 0..64) {
                if (getSigIDCeldaMismaDir1(celda, c, mapa, combate) == celdaID2) return c
                celda = getSigIDCeldaMismaDir1(celda, c, mapa, combate)
            }
        }
        return 0
    }

    private fun getPrimerLuchadorMismaDireccion(mapa: Mapa?, id: Short, dir: Char): Luchador? {
        var dir = dir
        if (mapa == null) return null
        if (dir == ('a'.code - 1).toChar()) dir = 'h'
        if (dir == ('h'.code + 1).toChar()) dir = 'a'
        val cell: Celda = mapa.getCelda(getSigIDCeldaMismaDir1(id.toInt(), dir, mapa, false).toShort())
        return cell.getPrimerLuchador()
    }

    private fun getFighter2CellBefore(celdaID: Int, dir: Char, mapa: Mapa): Luchador? {
        val nueva2CeldaID = getSigIDCeldaMismaDir1(getSigIDCeldaMismaDir1(celdaID, dir, mapa, false), dir, mapa, false).toShort()
        val cell: Celda = mapa.getCelda(nueva2CeldaID)
        val lucpr: Luchador = cell.getPrimerLuchador()
        return if (lucpr != null) lucpr else null
    }

    fun getObjetivosZonaArma(pelea: Pelea, tipo: Int, celda: Celda, celdaIDLanzador: Short): ArrayList<Luchador> {
        val objetivos: ArrayList<Luchador> = ArrayList<Luchador>()
        val c = getDirEntreDosCeldas(celdaIDLanzador.toInt(), celda.getID(), pelea.getMapaCopia(), true)
        if (c.code == 0) {
            val lucprim: Luchador = celda.getPrimerLuchador()
            if (lucprim != null) objetivos.add(lucprim)
            return objetivos
        }
        if (pelea.getMapaCopia() == null) return objetivos
        when (tipo) {
            Constantes.OBJETO_TIPO_MARTILLO -> {
                val f: Luchador? = getFighter2CellBefore(celdaIDLanzador.toInt(), c, pelea.getMapaCopia())
                if (f != null) objetivos.add(f)
                val g: Luchador? = getPrimerLuchadorMismaDireccion(pelea.getMapaCopia(), celdaIDLanzador, (c.code - 1).toChar())
                if (g != null) objetivos.add(g) // Agregar casilla a izquierda
                val h: Luchador? = getPrimerLuchadorMismaDireccion(pelea.getMapaCopia(), celdaIDLanzador, (c.code + 1).toChar())
                if (h != null) objetivos.add(h) // Agregar casilla a derecha
                val i: Luchador = celda.getPrimerLuchador()
                if (i != null) objetivos.add(i)
            }
            Constantes.OBJETO_TIPO_BASTON -> {
                val j: Luchador? = getPrimerLuchadorMismaDireccion(pelea.getMapaCopia(), celdaIDLanzador, (c.code - 1).toChar())
                if (j != null) objetivos.add(j) // Agregar casilla a izquierda
                val k: Luchador? = getPrimerLuchadorMismaDireccion(pelea.getMapaCopia(), celdaIDLanzador, (c.code + 1).toChar())
                if (k != null) objetivos.add(k) // Agregar casilla a derecha
                val l: Luchador = celda.getPrimerLuchador()
                if (l != null) objetivos.add(l) // Agregar casilla objetivo
            }
            Constantes.OBJETO_TIPO_PICO, Constantes.OBJETO_TIPO_ESPADA, Constantes.OBJETO_TIPO_GUADAÑA, Constantes.OBJETO_TIPO_DAGAS, Constantes.OBJETO_TIPO_VARITA, Constantes.OBJETO_TIPO_PALA, Constantes.OBJETO_TIPO_ARCO, Constantes.OBJETO_TIPO_HACHA -> {
                val m: Luchador = celda.getPrimerLuchador()
                if (m != null) objetivos.add(m)
            }
        }
        return objetivos
    }

    private fun getCeldaCoordenadaX(mapa: Mapa?, celdaID: Int): Int {
        if (mapa == null) return 0
        val w: Int = mapa.getAncho()
        return (celdaID - (w - 1) * getCeldaCoordenadaY(mapa, celdaID)) / w
    }

    private fun getCeldaCoordenadaY(mapa: Mapa, celdaID: Int): Int {
        val w: Int = mapa.getAncho()
        val loc5 = (celdaID / (w * 2 - 1))
        val loc6 = celdaID - loc5 * (w * 2 - 1)
        val loc7 = loc6 % w
        return loc5 - loc7
    }

    fun getCeldaMasCercanaAlrededor2(mapa: Mapa, celdaInicio: Short, celdaFinal: Short, alcanceMin: Int, alcanceMax: Int): Short {
        var dist = 1000
        var celdaID = celdaInicio
        val d = getDirEntreDosCeldas(mapa, celdaInicio, celdaFinal)
        var celdaInicio2 = celdaInicio
        var sigCelda: Short = 0
        var i = 0
        while (i < alcanceMax) {
            sigCelda = getSigIDCeldaMismaDir1(celdaInicio2.toInt(), d, mapa, true).toShort()
            celdaInicio2 = sigCelda
            i++
            if (i > alcanceMin) {
                val C: Celda = mapa.getCelda(sigCelda) ?: continue
                if (C.esCaminable(false) && C.getPrimerLuchador() == null) break
            }
        }
        val C: Celda = mapa.getCelda(sigCelda) ?: return -1
        val dis = distanciaDosCeldas(mapa, celdaFinal, sigCelda).toInt()
        if (dis < dist && C.esCaminable(true) && C.getPrimerLuchador() == null) {
            dist = dis
            celdaID = sigCelda
        }
        return if (celdaID == celdaInicio) -1 else celdaID
    }

    fun getCeldaMasCercanaAlrededor(mapa: Mapa, celdaInicio: Short, celdaFinal: Short, celdasProhibidas: ArrayList<Celda?>?): Short {
        var celdasProhibidas: ArrayList<Celda?>? = celdasProhibidas
        var dist = 1000
        var celdaID = celdaInicio
        if (celdasProhibidas == null) celdasProhibidas = ArrayList<Celda>()
        val dirs = charArrayOf('b', 'd', 'f', 'h')
        for (d in dirs) {
            val sigCelda = getSigIDCeldaMismaDir1(celdaInicio.toInt(), d, mapa, true).toShort()
            val C: Celda = mapa.getCelda(sigCelda) ?: break
            val dis = distanciaDosCeldas(mapa, celdaFinal, sigCelda).toInt()
            if (dis < dist && C.esCaminable(true) && C.getPrimerLuchador() == null && !celdasProhibidas.contains(C) && C.getLuchadores().size() === 0) {
                dist = dis
                celdaID = sigCelda
            }
        }
        return if (celdaID == celdaInicio) -1 else celdaID
    }

    fun listaDirEntreDosCeldas2(mapa: Mapa?, celdaInicio: Short, celdaDestino: Short,
                                celdaAnterior: Short): ByteArray {
        if (celdaInicio == celdaDestino || mapa == null) {
            return byteArrayOf()
        }
        val cInicio: Celda = mapa.getCelda(celdaInicio)
        val cDestino: Celda = mapa.getCelda(celdaDestino)
        val difX: Int = cDestino.getCoordX() - cInicio.getCoordX()
        val difY: Int = cDestino.getCoordY() - cInicio.getCoordY()
        return if (Math.abs(difY) === Math.abs(difX) && celdaAnterior > 0) {
            listaDirEntreDosCeldas2(mapa, celdaAnterior, celdaDestino, (-1.toShort()).toShort())
        } else if (Math.abs(difY) > Math.abs(difX)) {
            val c = arrayOf(intArrayOf(difX, 0, 2), intArrayOf(difY, 1, 3))
            formulaDireccion(c)
        } else {
            val c = arrayOf(intArrayOf(difY, 1, 3), intArrayOf(difX, 0, 2))
            formulaDireccion(c)
        }
    }

    private fun formulaDireccion(c: Array<IntArray>): ByteArray {
        val abc = ByteArray(4)
        for (i in 0..1) {
            val dif = c[i][0]
            var p = i
            if (dif < 0) {
                p = Math.abs(3 - i)
            }
            abc[p] = c[i][1].toByte()
            abc[Math.abs(3 - p)] = c[i][2].toByte()
        }
        return abc
    }

    private fun correctaDireccion(dir: Int): Int {
        var dir = dir
        while (dir < 0) {
            dir += 8
        }
        while (dir >= 8) {
            dir -= 8
        }
        return dir
    }

    fun getCoordPorDireccion(dir: Int): ByteArray {
        val f = arrayOf(byteArrayOf(1, -1), byteArrayOf(1, 0), byteArrayOf(1, 1), byteArrayOf(0, 1), byteArrayOf(-1, 1), byteArrayOf(-1, 0), byteArrayOf(-1, -1), byteArrayOf(0, -1))
        return f[dir]
    }

    fun celdasAfectadasEnElArea(mapa: Mapa, celdaIDObjetivo: Short,
                                celdaIDLanzador: Short, areaEfecto: String): ArrayList<Celda> {
        val celdas: ArrayList<Celda> = ArrayList<Celda>()
        if (mapa.getCelda(celdaIDObjetivo) == null) {
            return celdas
        }
        val tamaño: Int = Encriptador.getNumeroPorValorHash(areaEfecto.charAt(1))
        when (areaEfecto.charAt(0)) {
            'A' -> {
                var a = tamaño
                while (a >= 0) {
                    for (celda2 in celdasPorCruz(mapa.getCelda(celdaIDLanzador), mapa, a)) {
                        val celda: Celda = mapa.getCelda(celda2)
                        if (!celdas.contains(celda)) {
                            celdas.add(celda)
                        }
                    }
                    a--
                }
            }
            'D' -> {
                var i = if (tamaño % 2 == 0) 1 else 0
                while (i < tamaño) {
                    for (celda2 in celdasPorDistancia(mapa.getCelda(celdaIDObjetivo), mapa, i + 1)) {
                        val celda: Celda = mapa.getCelda(celda2)
                        if (!celdas.contains(celda)) {
                            celdas.add(celda)
                        }
                    }
                    i += 2
                }
            }
            'C' -> {
                if (tamaño >= 64) {
                    celdas.addAll(mapa.getCeldas().values())
                    break
                }
                var a = tamaño
                while (a >= 0) {
                    for (celda2 in celdasPorDistancia(mapa.getCelda(celdaIDObjetivo), mapa, a)) {
                        val celda: Celda = mapa.getCelda(celda2)
                        if (!celdas.contains(celda)) {
                            celdas.add(celda)
                        }
                    }
                    a--
                }
            }
            'O' -> for (celda2 in celdasPorDistancia(mapa.getCelda(celdaIDObjetivo), mapa, tamaño)) {
                val celda: Celda = mapa.getCelda(celda2)
                if (!celdas.contains(celda)) {
                    celdas.add(celda)
                }
            }
            'X' -> {
                var a = tamaño
                while (a >= 0) {
                    for (celda2 in celdasPorCruz(mapa.getCelda(celdaIDObjetivo), mapa, a)) {
                        val celda: Celda = mapa.getCelda(celda2)
                        if (!celdas.contains(celda)) {
                            celdas.add(celda)
                        }
                    }
                    a--
                }
            }
            'T' -> {
                val dir2 = direccionEntreDosCeldas(mapa, celdaIDLanzador, celdaIDObjetivo, true)
                for (celda2 in celdasPorLinea(mapa.getCelda(celdaIDObjetivo), mapa, tamaño, correctaDireccion(dir2
                        - 2))) {
                    val celda: Celda = mapa.getCelda(celda2)
                    if (!celdas.contains(celda)) {
                        celdas.add(celda)
                    }
                }
                for (celda2 in celdasPorLinea(mapa.getCelda(celdaIDObjetivo), mapa, tamaño, correctaDireccion(dir2
                        + 2))) {
                    val celda: Celda = mapa.getCelda(celda2)
                    if (!celdas.contains(celda)) {
                        celdas.add(celda)
                    }
                }
                if (!celdas.contains(mapa.getCelda(celdaIDObjetivo))) {
                    celdas.add(mapa.getCelda(celdaIDObjetivo))
                }
            }
            'L' -> {
                val dir = direccionEntreDosCeldas(mapa, celdaIDLanzador, celdaIDObjetivo, true)
                for (celda2 in celdasPorLinea(mapa.getCelda(celdaIDObjetivo), mapa, tamaño, correctaDireccion(
                        dir))) {
                    val celda: Celda = mapa.getCelda(celda2)
                    if (!celdas.contains(celda)) {
                        celdas.add(celda)
                    }
                }
            }
            'P' -> if (!celdas.contains(mapa.getCelda(celdaIDObjetivo))) {
                celdas.add(mapa.getCelda(celdaIDObjetivo))
            }
            else -> MainServidor.redactarLogServidorln("[FIXME]Tipo de alcance no reconocido: " + areaEfecto.charAt(0))
        }
        return celdas
    }

    fun celdasPorDistancia(celda: Celda, mapa: Mapa, distancia: Int): ArrayList<Short> {
        val celdas: ArrayList<Short> = ArrayList<Short>()
        val x: Byte = celda.getCoordX()
        val y: Byte = celda.getCoordY()
        val f = arrayOf(byteArrayOf(1, 1), byteArrayOf(1, -1), byteArrayOf(-1, 1), byteArrayOf(-1, -1))
        for (x2 in 0..distancia) {
            val y2 = distancia - x2
            for (b in f) {
                val cell: Celda = mapa.getCeldaPorPos((x + b[0] * x2).toByte(), (y + b[1] * y2).toByte())
                if (cell != null) {
                    if (!celdas.contains(cell.getID())) celdas.add(cell.getID())
                }
            }
        }
        return celdas
    }

    private fun celdasPorCruz(celda: Celda, mapa: Mapa, distancia: Int): ArrayList<Short> {
        val celdas: ArrayList<Short> = ArrayList<Short>()
        val x: Byte = celda.getCoordX()
        val y: Byte = celda.getCoordY()
        for (b in COORD_ALREDEDOR) {
            val cell: Celda = mapa.getCeldaPorPos((x + b[0] * distancia).toByte(), (y + b[1] * distancia).toByte())
            if (cell != null) {
                if (!celdas.contains(cell.getID())) celdas.add(cell.getID())
            }
        }
        return celdas
    }

    private fun celdasPorLinea(celda: Celda, mapa: Mapa, distancia: Int, dir: Int): ArrayList<Short> {
        val celdas: ArrayList<Short> = ArrayList<Short>()
        if (dir == -1) {
            return celdas
        }
        val x: Byte = celda.getCoordX()
        val y: Byte = celda.getCoordY()
        val b = getCoordPorDireccion(dir)
        for (x2 in distancia downTo 0) {
            val cell: Celda = mapa.getCeldaPorPos((x + b[0] * x2).toByte(), (y + b[1] * x2).toByte())
            if (cell != null) {
                if (!celdas.contains(cell.getID())) celdas.add(cell.getID())
            }
        }
        return celdas
    }

    fun celdasPosibleLanzamiento(SH: StatHechizo, lanzador: Luchador,
                                 mapa: Mapa, tempCeldaIDLanzador: Short, celdaObjetivo: Short): ArrayList<Celda> {
        val celdasF: ArrayList<Celda> = ArrayList<Celda>()
        val perso: Personaje = lanzador.getPersonaje()
        var maxAlc: Int = SH.getMaxAlc()
        val minAlc: Int = SH.getMinAlc()
        var alcModificable: Boolean = SH.esAlcanceModificable()
        var lineaVista: Boolean = SH.esLineaVista()
        var lanzarLinea: Boolean = SH.esLanzarLinea()
        val necesitaCeldaLibre: Boolean = SH.esNecesarioCeldaLibre()
        val necesitaObjetivo: Boolean = SH.esNecesarioObjetivo()
        val hechizoID: Int = SH.getHechizoID()
        if (perso != null && perso.tieneModfiSetClase(hechizoID)) {
            maxAlc += perso.getModifSetClase(hechizoID, 281)
            alcModificable = alcModificable or (perso.getModifSetClase(hechizoID, 282) === 1)
            lanzarLinea = lanzarLinea and !(perso.getModifSetClase(hechizoID, 288) === 1)
            lineaVista = lineaVista and !(perso.getModifSetClase(hechizoID, 289) === 1)
        }
        if (alcModificable) {
            maxAlc += lanzador.getTotalStats().getTotalStatParaMostrar(Constantes.STAT_MAS_ALCANCE, lanzador.getPelea(), null)
        }
        if (maxAlc < minAlc) {
            maxAlc = minAlc
        }
        var celdaI: Celda = mapa.getCelda(tempCeldaIDLanzador)
        if (celdaI == null) {
            celdaI = lanzador.getCeldaPelea()
        }
        val suponiendo = lanzador.getCeldaPelea().getID() !== celdaI.getID()
        for (celdaC in mapa.getCeldas().values()) {
            if (celdaC == null) {
                continue
            }
            val dist = distanciaDosCeldas(mapa, celdaI.getID(), celdaC.getID()).toInt()
            if (dist < minAlc || dist > maxAlc) {
                if (celdaObjetivo == celdaC.getID()) {
                    if (perso != null) {
                        GestorSalida.ENVIAR_Im_INFORMACION(perso, "1171;$minAlc~$maxAlc~$dist")
                    }
                    if (MainServidor.MODO_DEBUG) {
                        System.out.println("El hechizo " + SH.getHechizo().getNombre().toString() + " esta fuera del rango")
                    }
                }
                continue
            }
            if (lanzarLinea) {
                if (celdaI.getCoordX() !== celdaC.getCoordX() && celdaI.getCoordY() !== celdaC.getCoordY()) {
                    if (celdaObjetivo == celdaC.getID()) {
                        if (perso != null) {
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "1173")
                        }
                        if (MainServidor.MODO_DEBUG) {
                            System.out.println("El hechizo " + SH.getHechizo().getNombre().toString() + " necesita lanzarse en linea recta")
                        }
                    }
                    continue
                }
            }
            if (necesitaCeldaLibre) {
                if (celdaC.getMovimiento() > 1 && celdaC.getPrimerLuchador() != null) {
                    if (celdaObjetivo == celdaC.getID()) {
                        if (perso != null) {
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "1172")
                        }
                        if (MainServidor.MODO_DEBUG) {
                            System.out.println("El hechizo " + SH.getHechizo().getNombre().toString() + " necesita celda libre")
                        }
                    }
                    continue
                }
                if (celdaC.getMovimiento() <= 1) {
                    continue
                }
            }
            if (necesitaObjetivo) {
                if (celdaC.getPrimerLuchador() == null) {
                    if (celdaObjetivo == celdaC.getID()) {
                        if (perso != null) {
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "1172")
                        }
                        if (MainServidor.MODO_DEBUG) {
                            System.out.println("El hechizo " + SH.getHechizo().getNombre().toString() + " necesita un objetivo")
                        }
                    }
                    continue
                }
            }
            if (lineaVista) {
                if (!lineaDeVista1(mapa, celdaI, celdaC, lanzador, suponiendo, celdaObjetivo)) {
                    if (celdaObjetivo == celdaC.getID()) {
                        if (perso != null) {
                            GestorSalida.ENVIAR_Im_INFORMACION(perso, "1174")
                        }
                        if (MainServidor.MODO_DEBUG) {
                            System.out.println("El hechizo " + SH.getHechizo().getNombre().toString() + " tiene linea de vista")
                        }
                    }
                    continue
                }
            }
            celdasF.add(celdaC)
        }
        // for (Celda c : celdasF) {
        // GestorSalida.enviar(perso, "GDZ|+" + c.getID() + ";0;3");
        // }
        return celdasF
    }

    private fun celdaPorCoordenadas(mapa: Mapa, x: Int, y: Int): Celda {
        return mapa.getCelda((x * mapa.getAncho() + y * (mapa.getAncho() - 1)) as Short)
    }

    private fun lineaDeVista1(mapa: Mapa, celdaI: Celda, celdaC: Celda, lanzador: Luchador, suponiendo: Boolean,
                              celdaObjetivo: Short): Boolean {
        val _loc9: Float = if (celdaI.tieneSprite(lanzador.getID(), suponiendo)) 1.5f else 0
        val _loc10: Float = if (celdaC.tieneSprite(lanzador.getID(), suponiendo)) 1.5f else 0
        val zI: Float = celdaI.getAlto() + _loc9
        val zC: Float = celdaC.getAlto() + _loc10
        val _loc11 = zC - zI
        val _loc12: Float = Math.max(Math.abs(celdaI.getCoordY() - celdaC.getCoordY()), Math.abs(celdaI.getCoordX() - celdaC
                .getCoordX()))
        val _loc13 = (celdaI.getCoordY() - celdaC.getCoordY()) as Float / (celdaI.getCoordX() - celdaC
                .getCoordX()) as Float
        val isNaN = Float.isInfinite(_loc13) || Float.isNaN(_loc13)
        val _loc14: Float = celdaI.getCoordY() - _loc13 * celdaI.getCoordX()
        val _loc15: Float = if (celdaC.getCoordX() - celdaI.getCoordX() < 0) -1 else 1.toFloat()
        val _loc16: Float = if (celdaC.getCoordY() - celdaI.getCoordY() < 0) -1 else 1.toFloat()
        var _loc17: Int = celdaI.getCoordY()
        // int _loc18 = celdaI.getX();
        val _loc19: Float = celdaC.getCoordX() * _loc15
        // int _loc20 = celdaC.getY() * _loc16;
        var _loc26 = 0f
        var _loc27: Float = celdaI.getCoordX() + 0.5f * _loc15
        // if (celdaC.getID() == celdaObjetivo) {
        // System.out.println("_loc5.x " + celdaI.getX() + " _loc5.y " + celdaI.getY() + " alto5 " +
        // celdaI.getAlto()
        // + " _loc5.z " + zI + " _loc6.x " + celdaC.getX() + " _loc6.y " + celdaC.getY() + " alto6 " +
        // celdaC.getAlto()
        // + " _loc6.z " + zC + " _loc9 " + _loc9 + " _loc10 " + _loc10 + " _loc11 " + _loc11 +
        // " _loc12 " + _loc12
        // + " _loc13 " + _loc13 + " _loc14 " + _loc14 + " _loc15 " + _loc15 + " _loc16 " + _loc16 +
        // " _loc17 " + _loc17
        // + " _loc19 " + _loc19 + " _loc27 " + _loc27);
        // }
        if (!isNaN) {
            while (_loc27 * _loc15 <= _loc19) {
                val _loc25 = _loc13 * _loc27 + _loc14
                var _loc21 = 0
                var _loc22 = 0
                if (_loc16 > 0) {
                    _loc21 = Math.round(_loc25)
                    _loc22 = Math.ceil(_loc25 - 0.5f)
                } else {
                    _loc21 = Math.ceil(_loc25 - 0.5f)
                    _loc22 = Math.round(_loc25)
                }
                _loc26 = _loc17.toFloat()
                while (_loc26 * _loc16 <= _loc22 * _loc16) {
                    if (!lineaDeVista2(mapa, (_loc27 - _loc15 / 2).toInt(), _loc26.toInt(), false, celdaI, celdaC, zI, zC, _loc11,
                                    _loc12, lanzador.getID(), suponiendo, celdaObjetivo)) {
                        return false
                    }
                    _loc26 = _loc26 + _loc16
                }
                _loc17 = _loc21
                _loc27 = _loc27 + _loc15
            }
        }
        _loc26 = _loc17.toFloat() // celdaI.getY();
        while (_loc26 * _loc16 <= celdaC.getCoordY() * _loc16) {
            if (!lineaDeVista2(mapa, (_loc27 - 0.5f * _loc15).toInt(), _loc26.toInt(), false, celdaI, celdaC, zI, zC, _loc11,
                            _loc12, lanzador.getID(), suponiendo, celdaObjetivo)) {
                // if (celdaC.getID() == celdaObjetivo) {
                // System.out.println("FUE EN LINEA 2 ");
                // }
                return false
            }
            _loc26 = _loc26 + _loc16
        }
        return if (!lineaDeVista2(mapa, (_loc27 - 0.5f * _loc15).toInt(), (_loc26 - _loc16).toInt(), true, celdaI, celdaC, zI, zC,
                        _loc11, _loc12, lanzador.getID(), suponiendo, celdaObjetivo)) {
            // if (celdaC.getID() == celdaObjetivo) {
            // System.out.println("FUE EN LINEA 3 ");
            // }
            false
        } else true
    }

    private fun lineaDeVista2(mapa: Mapa, x: Int, y: Int, bool: Boolean, celdaI: Celda, celdaC: Celda, zI: Float,
                              zC: Float, zDiff: Float, d: Float, idLanzador: Int, suponiendo: Boolean, celdaObjetivo: Short): Boolean {
        val _loc11: Celda = celdaPorCoordenadas(mapa, x, y)
        val _loc12: Float = Math.max(Math.abs(celdaI.getCoordY() - y), Math.abs(celdaI.getCoordX() - x))
        val _loc13 = _loc12 / d * zDiff + zI
        val _loc14: Float = _loc11.getAlto()
        val _loc15 = if (!_loc11.tieneSprite(idLanzador, suponiendo) || _loc12 == 0f || (bool || celdaC.getCoordX() === x
                        && celdaC.getCoordY() === y)) false else true
        // if (celdaObjetivo == _loc11.getID()) {
        // System.out.println(" _loc11.lineaDeVista " + _loc11.lineaDeVista() + " _loc12 " + _loc12 +
        // " _loc13 " + _loc13
        // + " _loc14 " + _loc14 + " _loc15 " + _loc15 + " _loc14 <= _loc13 " + (_loc14 <= _loc13)
        // + " (_loc14 <= _loc13 && !_loc15) " + (_loc14 <= _loc13 && !_loc15));
        // }
        // NaN en java con condicional siempre es FALSE, en AS2 es true con >= <= y FALSE con ==
        return if (_loc11.lineaDeVista() && (Float.isNaN(_loc13) || _loc14 <= _loc13) && !_loc15) {
            true
        } else {
            if (bool) {
                true
            } else false
        }
    }

    fun celdaMasCercanaACeldaObjetivo(mapa: Mapa, celdaInicio: Short, celdaDestino: Short,
                                      celdasProhibidas: ArrayList<Celda?>?, ocupada: Boolean): Short {
        var celdasProhibidas: ArrayList<Celda?>? = celdasProhibidas
        if (mapa.getCelda(celdaInicio) == null || mapa.getCelda(celdaDestino) == null) {
            return -1
        }
        var distancia = 1000
        var celdaID = celdaInicio
        if (celdasProhibidas == null) {
            celdasProhibidas = ArrayList<Celda>()
        }
        val dirs = listaDirEntreDosCeldas(mapa, celdaInicio, celdaDestino)
        for (d in dirs) {
            val sigCelda = getSigIDCeldaMismaDir(celdaInicio, d.code, mapa, true)
            val celda: Celda = mapa.getCelda(sigCelda) ?: continue
            val tempDistancia = distanciaDosCeldas(mapa, celdaDestino, sigCelda).toInt()
            if (tempDistancia < distancia && celda.esCaminable(true) && (!ocupada || celda.getPrimerLuchador() == null)
                    && !celdasProhibidas.contains(celda)) {
                distancia = tempDistancia
                celdaID = sigCelda
            }
        }
        return if (celdaID == celdaInicio) -1 else celdaID
    }

    fun celdasDeMovimiento(pelea: Pelea, celdaInicio: Celda, filtro: Boolean,
                           ocupadas: Boolean, tacleado: Luchador?): ArrayList<Short> {
        val celdas: ArrayList<Short> = ArrayList<Short>()
        if (pelea.getPMLuchadorTurno() <= 0) {
            return celdas
        }
        val mapa: Mapa = pelea.getMapaCopia()
        for (a in 0..pelea.getPMLuchadorTurno()) {
            for (tempCeldaID in celdasPorDistancia(celdaInicio, mapa, a)) {
                val tempCelda: Celda = mapa.getCelda(tempCeldaID)
                if (!tempCelda.esCaminable(true)) {
                    continue
                }
                if (ocupadas && tempCelda.getPrimerLuchador() != null) {
                    continue
                }
                if (!celdas.contains(tempCeldaID)) {
                    if (filtro) {
                        val pathTemp: Duo<Integer, ArrayList<Celda>> = getPathPelea(mapa, celdaInicio.getID(), tempCeldaID, pelea
                                .getPMLuchadorTurno(), tacleado, false) ?: continue
                        if (pathTemp._segundo.isEmpty()) {
                            continue
                        }
                        if (pathTemp._segundo.get(pathTemp._segundo.size() - 1).getID() !== tempCeldaID) {
                            continue
                        }
                    }
                    celdas.add(tempCeldaID)
                }
            }
        }
        return celdas
    }

    fun celdaMoverSprite(mapa: Mapa, celda: Short): Short {
        val celdasPosibles: ArrayList<Short> = ArrayList<Short>()
        val ancho: Short = mapa.getAncho()
        val dir = shortArrayOf((-ancho).toShort(), (-(ancho - 1).toShort()).toShort(), (ancho - 1).toShort(), ancho)
        for (element in dir) {
            try {
                if (celda + element > 14 || celda + element < 464) {
                    if (mapa.getCelda((celda + element).toShort()).esCaminable(false)) {
                        celdasPosibles.add((celda + element).toShort())
                    }
                }
            } catch (e: Exception) {
            }
        }
        return if (celdasPosibles.size() <= 0) {
            -1
        } else celdasPosibles.get(Formulas.getRandomInt(0, celdasPosibles.size() - 1))
    }

    fun getCeldaIDCercanaLibre(celda: Celda, mapa: Mapa): Short {
        for (c in COORD_ALREDEDOR) {
            val cell: Celda = mapa.getCeldaPorPos((celda.getCoordX() + c[0]) as Byte, (celda.getCoordY() + c[1]) as Byte)
            if (cell != null && cell.getObjetoTirado() == null && cell.getPrimerPersonaje() == null && cell.esCaminable(
                            false)) {
                return cell.getID()
            }
        }
        return 0
    }

    fun ultimaCeldaID(mapa: Mapa): Short {
        return (mapa.getAncho() * mapa.getAlto() * 2 - (mapa.getAlto() + mapa.getAncho()))
    }

    fun esCeldaLadoIzq(ancho: Byte, alto: Byte, celda: Short): Boolean {
        var ladoIzq = ancho.toShort()
        for (i in 0 until alto) {
            if (celda == ladoIzq || celda.toInt() == ladoIzq - ancho) {
                return true
            }
            (ladoIzq += (ancho * 2 - 1).toShort()).toShort()
        }
        return false
    }

    fun esCeldaLadoDer(ancho: Byte, alto: Byte, celda: Short): Boolean {
        var ladoDer = (2 * (ancho - 1)).toShort()
        for (i in 0 until alto) {
            if (celda == ladoDer || celda.toInt() == ladoDer - ancho + 1) {
                return true
            }
            (ladoDer += (ancho * 2 - 1).toShort()).toShort()
        }
        return false
    }

    fun celdaSalienteLateral(ancho: Byte, alto: Byte, celda1: Short, celda2: Short): Boolean {
        if (esCeldaLadoIzq(ancho, alto, celda1) && (celda2.toInt() == celda1 + ancho - 1 || celda2.toInt() == celda1 - ancho)) {
            return true
        }
        return if (esCeldaLadoDer(ancho, alto, celda1) && (celda2.toInt() == celda1 - ancho + 1 || celda2.toInt() == celda1 + ancho)) {
            true
        } else false
    }

    private class CeldaCamino {
        val id: Short = 0
        val valorX: Short = 0
        val cantPM: Short = 0
        val direccion: Short = 0
        val movimiento: Short = 0
        val level: Short = 0
        val distEstimadaX = 0
        val distEstimada = 0
        val anterior: CeldaCamino? = null
    }
}