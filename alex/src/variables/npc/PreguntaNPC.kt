package variables.npc

import java.util.ArrayList
import java.util.Map
import java.util.TreeMap
import java.util.Map.Entry
import variables.personaje.Personaje
import estaticos.Condiciones
import estaticos.MainServidor
import estaticos.Mundo
import sprites.Preguntador

class PreguntaNPC(val iD: Int, respuestas: String, var params: String, alternos: String) {
    var strAlternos: String? = null
        private set
    private val _respuestas: ArrayList<Integer> = ArrayList()
    private val _pregCondicionales: Map<String, Integer> = TreeMap()
    fun setPreguntasCondicionales(alternos: String) {
        strAlternos = alternos
        _pregCondicionales.clear()
        val alt: Array<String> = alternos.replaceAll("\\],\\[", "�").replaceAll("[\\[\\]]", "").split("�")
        for (s in alt) {
            try {
                val split: Array<String> = s.split(";")
                _pregCondicionales.put(split[0], Integer.parseInt(split[1]))
            } catch (e: Exception) {
            }
        }
    }

    val strRespuestas: String
        get() {
            val str = StringBuilder()
            for (i in _respuestas) {
                if (str.length() > 0) {
                    str.append(";")
                }
                str.append(i)
            }
            return str.toString()
        }
    var respuestas: ArrayList<Integer>
        get() = _respuestas
        set(respuestas) {
            _respuestas.clear()
            for (s in respuestas.replace(";", ",").split(",")) {
                try {
                    _respuestas.add(Integer.parseInt(s))
                } catch (e: Exception) {
                }
            }
        }

    fun stringArgParaDialogo(perso: Personaje, preguntador: Preguntador): String {
        val str = StringBuilder(iD.toString() + "")
        try {
            for (entry in _pregCondicionales.entrySet()) {
                if (entry.getValue() === iD || entry.getValue() <= 0) {
                    continue
                }
                if (Condiciones.validaCondiciones(perso, entry.getKey(), null)) {
                    if (Mundo.getPreguntaNPC(entry.getValue()) == null) {
                        Mundo.addPreguntaNPC(PreguntaNPC(entry.getValue(), "", "", ""))
                    }
                    return Mundo.getPreguntaNPC(entry.getValue()).stringArgParaDialogo(perso, preguntador)
                }
            }
            str.append(preguntador.getArgsDialogo(params))
            var b = true
            for (i in _respuestas) {
                if (i <= 0) {
                    continue
                }
                var respuesta: RespuestaNPC? = Mundo.getRespuestaNPC(i)
                if (respuesta == null) {
                    respuesta = RespuestaNPC(i)
                    Mundo.addRespuestaNPC(respuesta)
                }
                val cond: String = respuesta.getCondicion()
                if (MainServidor.SON_DE_LUCIANO) {
                    if (!respuesta.mostrar(perso)) {
                        continue
                    }
                } else {
                    if (!Condiciones.validaCondiciones(perso, cond, null)) {
                        continue
                    }
                }
                if (b) {
                    str.append("|")
                } else {
                    str.append(";")
                }
                b = false
                str.append(i)
            }
            perso.setPreguntaID(iD)
        } catch (e: Exception) {
            MainServidor.redactarLogServidorln("Hay un error en el NPC Pregunta " + iD)
        }
        return str.toString()
    }

    init {
        respuestas = respuestas
        setPreguntasCondicionales(alternos)
    }
}