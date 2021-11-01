package variables.zotros

import java.util.Date
import estaticos.MainServidor
import estaticos.Mundo

class LoginPremio {
    var etapa: Int
    var dia: Int
    var premio = 0
    var fecha: Long = 0

    constructor(etapa: Int, dia: Int, premio: Int) {
        this.etapa = etapa
        this.dia = dia
        this.premio = premio
    }

    constructor(cuenta: Array<String?>) {
        etapa = Integer.parseInt(cuenta[0])
        dia = Integer.parseInt(cuenta[1])
        fecha = Long.parseLong(cuenta[2])
    }

    fun guardarEstado(): String {
        dia++
        if (!Mundo.LOGINPREMIOS.containsKey(toString())) {
            etapa++
            dia = 1
        }
        val fechaTemp = Date()
        fecha = fechaTemp.getTime() + MainServidor.HORAS_MINIMO_RECLAMAR_PREMIO
        return "$etapa,$dia,$fecha"
    }

    fun premiosFaltantes(): String {
        var diaTemp = dia
        val premios = StringBuilder()
        while (Mundo.LOGINPREMIOS.containsKey("$etapa,$diaTemp")) {
            premios.append(",")
            premios.append(Mundo.LOGINPREMIOS.get("$etapa,$diaTemp").premio)
            diaTemp++
        }
        return premios.toString()
    }

    fun reiniciarEstado(): String {
        dia = 1
        return "$etapa,$dia,$fecha"
    }

    @Override
    override fun toString(): String {
        return "$etapa,$dia"
    }
}