package sprites

import java.util.Map
import variables.hechizo.StatHechizo
import variables.pelea.Pelea
import variables.stats.TotalStats

interface PreLuchador {
    val alineacion: Byte
    val iD: Int
    fun getGfxID(rolePlayBuff: Boolean): Int
    val pDVMax: Int
    val pDV: Int
    val totalStatsPelea: TotalStats?
    val nivel: Int
    val gradoAlineacion: Int

    // public int getIniciativa();
    fun setPelea(pelea: Pelea?)
    fun actualizarAtacantesDefensores()
    fun stringGMLuchador(): String?
    val hechizos: Map<Any?, Any?>?
    val deshonor: Int
    val honor: Int
    fun addHonor(honor: Int)
    fun addDeshonor(honor: Int): Boolean
    fun addKamasGanada(kamas: Double)
    fun addXPGanada(exp: Long)
    fun murio()
    fun sobrevivio()
}