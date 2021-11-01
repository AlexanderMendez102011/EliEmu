package variables.zotros

import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.Timer
import estaticos.GestorSalida
import variables.personaje.Personaje

object Interactivos {
    fun tutorialIncarnam(perso: Personaje) {
        val timer = Timer(1000, object : ActionListener() {
            var paso = 0
            @Override
            fun actionPerformed(e: ActionEvent) {
                val mapaid: Int = perso.getMapa().getID()
                if (mapaid != 10258 || !perso.enLinea() || perso.getPelea() != null) {
                    paso = 0
                    (e.getSource() as Timer).stop()
                    return
                }
                when (paso) {
                    0 -> GestorSalida.enviar(perso, "GM|+403;5;0;-99;99;-4;70^100;0;ffffff;-1;-1;0,205f,205e,0,0;;0", true)
                    1 -> GestorSalida.enviar(perso, "cMK|-99|Zino|Te voy a contar una historia sobre esta zona|", true)
                    8 -> GestorSalida.enviar(perso, "cMK|-99|Zino|Muchas criaturas malignas viven aqu� y se han hecho con el control de todo!!!|", true)
                    15 -> GestorSalida.enviar(perso, "cMK|-99|Zino|Los ciudadanos est�n muy asustados debido a la alta cantidad de monstruos que aparecen|", true)
                    22 -> GestorSalida.enviar(perso, "cMK|-99|Zino|La mazmorra de Incarnam ha sido ocupada completamente por ellos|", true)
                    29 -> GestorSalida.enviar(perso, "cMK|-99|Zino|Tendr�s que completar la mazmorra y averiguar por qu� salen tantas criaturas|", true)
                    36 -> GestorSalida.enviar(perso, "cMK|-99|Zino|Y si puedes, intenta echarles una mano tambi�n a las personas que te encuentres, seguro que te compensar�n muy bien!!!|", true)
                    37 -> {
                        paso = 0
                        (e.getSource() as Timer).stop()
                        return
                    }
                }
                paso += 1
            }
        })
        timer.start()
    }

    fun iniciaTutorial(perso: Personaje) {
        val timer = Timer(1000, object : ActionListener() {
            var paso = 1
            @Override
            fun actionPerformed(e: ActionEvent) {
                val mapaid: Int = perso.getMapa().getID()
                if (mapaid != 6825 || !perso.enLinea() || perso.getPelea() != null) {
                    paso = 0
                    perso.puedeAbrir = true
                    (e.getSource() as Timer).stop()
                    return
                }
                when (paso) {
                    1 -> {
                        perso.puedeAbrir = false
                        GestorSalida.enviar(perso, "GM|+210;3;0;-51;379;-4;81^100x100;0;edda11;-1;0;0,18727,18728,1872e,1872f;;0", false)
                    }
                    2 -> GestorSalida.enviar(perso, "cMK|-51| Lily[MR]|" + Idiomas.getTexto("es", 188), false)
                    8 -> GestorSalida.enviar(perso, "cMK|-51|Lily[MR]|" + Idiomas.getTexto("es", 189), false)
                    15 -> {
                        GestorSalida.enviar(perso, "cMK|-51|[Maestro Reset]|" + Idiomas.getTexto("es", 190), false)
                        perso.getCuenta().getSocket().analizar_Packets("wr", true)
                    }
                    22 -> GestorSalida.enviar(perso, "cMK|-51|Lily[MR]|" + Idiomas.getTexto("es", 191), false)
                    29 -> GestorSalida.enviar(perso, "cMK|-51|Lily[MR]|" + Idiomas.getTexto("es", 192) + 0x00.toChar().toString() + "wB", false)
                    36 -> GestorSalida.enviar(perso, "cMK|-51|Lily[MR]|" + Idiomas.getTexto("es", 193), false)
                    43 -> GestorSalida.enviar(perso, "cMK|-51|Lily[MR]|" + Idiomas.getTexto("es", 194) + 0x00.toChar().toString() + "wx", false)
                    50 -> GestorSalida.enviar(perso, "cMK|-51|Lily[MR]|" + Idiomas.getTexto("es", 195), false)
                    57 -> GestorSalida.enviar(perso, "cMK|-51|Lily[MR]|" + Idiomas.getTexto("es", 196), false)
                    64 -> GestorSalida.enviar(perso, "cMK|-51|Lily[MR]|" + Idiomas.getTexto("es", 197), false)
                    65 -> {
                        perso.puedeAbrir = true
                        paso = 0
                        (e.getSource() as Timer).stop()
                        return
                    }
                }
                paso += 1
            }
        })
        timer.start()
    }

    fun fuegosArficiales(npcid: Int, perso: Personaje?, celda: Int) {
        val packet = StringBuilder("")
        packet.append("GA0;228;$npcid;$celda,2900,11,8,2").append(0x00.toChar())
        packet.append("GA0;228;$npcid;$celda,2901,11,8,2").append(0x00.toChar())
        packet.append("GA0;228;$npcid;$celda,2902,11,8,2").append(0x00.toChar())
        packet.append("GA0;228;$npcid;$celda,2901,11,8,2").append(0x00.toChar())
        packet.append("GA0;228;$npcid;$celda,2902,11,8,2").append(0x00.toChar())
        GestorSalida.enviar(perso, packet.toString(), true)
    }

    fun mensajeNPC(npc: Int, nombrenpc: String, mensaje: String, perso: Personaje, nuevomapa: Int) {
        val timer = Timer(1000, object : ActionListener() {
            var paso = 0
            @Override
            fun actionPerformed(e: ActionEvent) {
                val mapaid: Int = perso.getMapa().getID()
                if (mapaid != nuevomapa || !perso.enLinea() || perso.getPelea() != null) {
                    paso = 0
                    (e.getSource() as Timer).stop()
                    return
                }
                paso += 1
                when (paso) {
                    2 -> GestorSalida.enviar(perso, "cMK|$npc|$nombrenpc|$mensaje|", true)
                    3 -> {
                        paso = 0
                        (e.getSource() as Timer).stop()
                    }
                }
            }
        })
        timer.start()
    }
}