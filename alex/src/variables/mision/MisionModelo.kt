package variables.mision

import java.util.ArrayList
import estaticos.Mundo

class MisionModelo(val iD: Int, etapas: String, nombre: String, pregDarMision: String,
                   pregMisCumplida: String, pregMisIncompleta: String, puedeRepetirse: Boolean) {
    var puedeRepetirse: Boolean
    val nombre: String
    private val _etapas: ArrayList<Integer> = ArrayList<Integer>()
    private val _preguntas: Array<MisionPregunta?> = arrayOfNulls<MisionPregunta>(3)
    fun setPreguntas(pregunta: String, estado: Int) {
        try {
            val s: Array<String> = pregunta.split(";")
            var npc = 0
            var pregID = 0
            var condicion = ""
            try {
                npc = Integer.parseInt(s[0])
            } catch (e: Exception) {
            }
            try {
                pregID = Integer.parseInt(s[1])
            } catch (e: Exception) {
            }
            try {
                condicion = s[2]
            } catch (e: Exception) {
            }
            _preguntas[estado] = MisionPregunta(pregID, npc, condicion)
            if (npc > 0) {
                Mundo.getNPCModelo(npc).addMision(this)
            }
        } catch (e: Exception) {
        }
    }

    fun getMisionPregunta(estado: Int): MisionPregunta? {
        return _preguntas[estado]
    }

    fun strMisionPregunta(estado: Int): String {
        val preg: MisionPregunta = _preguntas[estado] ?: return "null"
        var str: String = preg.getNPCID().toString() + ";" + preg.getPreguntaID()
        if (!preg.getCondicion().isEmpty()) {
            str += ";" + preg.getCondicion()
        }
        return str
    }

    var etapas: ArrayList<Integer>
        get() = _etapas
        set(etapas) {
            _etapas.clear()
            for (str in etapas.split(",")) {
                try {
                    _etapas.add(Integer.parseInt(str))
                } catch (e: Exception) {
                }
            }
        }

    fun strEtapas(): String {
        var s = ""
        for (i in _etapas) {
            if (!s.isEmpty()) {
                s += ","
            }
            s += i
        }
        return s
    }

    fun siguienteEtapa(id: Int): Int {
        return try {
            _etapas.get(_etapas.indexOf(id) + 1)
        } catch (e: Exception) {
            -1
        }
    }

    init {
        etapas = etapas
        this.nombre = nombre
        setPreguntas(pregDarMision, Mision.ESTADO_NO_TIENE)
        setPreguntas(pregMisCumplida, Mision.ESTADO_COMPLETADO)
        setPreguntas(pregMisIncompleta, Mision.ESTADO_INCOMPLETO)
        this.puedeRepetirse = puedeRepetirse
    }
}