package variables.zotros

import estaticos.GestorSalida
import variables.personaje.Personaje

class Omega(var nivel: Int, var accion: Int, var arg: String, var packet: String) {
    fun aplicarAccion(perso: Personaje?) {
        Accion.realizar_Accion_Estatico(accion, arg, perso, null, -1, -1.toShort())
    }

    fun aplicarPacket(perso: Personaje?) {
        GestorSalida.ENVIAR_ws_PACKETE_OMEGA(perso, packet)
    }

    val objeto: String?
        get() = if (accion == 5) {
            arg.split(",").get(0)
        } else null
}