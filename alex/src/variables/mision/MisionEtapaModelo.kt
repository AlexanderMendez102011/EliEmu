package variables.mision

import java.util.ArrayList
import java.util.TreeMap
import java.util.regex.Pattern
import variables.objeto.ObjetoModelo.CAPACIDAD_STATS
import variables.personaje.Personaje
import variables.zotros.Accion
import estaticos.Constantes
import estaticos.GestorSalida
import estaticos.Mundo

class MisionEtapaModelo(val iD: Int, recompensas: String, objetivos: String, val nombre: String, varios: String) {
    private val _recompensas = arrayOfNulls<String>(7)
    var recompensa: String? = null
        private set
    private val _objetivos: ArrayList<TreeMap<Integer, MisionObjetivoModelo>> = ArrayList()
    private var _strObjetivos = ""
    val varios = ""
    fun setObjetivos(objetivos: String) {
        _objetivos.clear()
        _strObjetivos = objetivos
        for (s in objetivos.split(Pattern.quote("|"))) {
            val map: TreeMap<Integer, MisionObjetivoModelo> = TreeMap()
            for (str in s.split(",")) {
                try {
                    val idObjetivo: Int = Integer.parseInt(str)
                    val objetivo: MisionObjetivoModelo = Mundo.getMisionObjetivoModelo(idObjetivo)
                    if (objetivo != null) {
                        map.put(idObjetivo, objetivo)
                    }
                } catch (e: Exception) {
                }
            }
            _objetivos.add(map)
        }
    }

    fun strObjetivos(): String {
        return _strObjetivos
    }

    fun setRecompensa(recompensas: String) {
        recompensa = recompensas
        var i: Byte = 0
        for (str in recompensas.split(Pattern.quote("|"))) {
            try {
                if (!str.equalsIgnoreCase("null")) {
                    _recompensas[i.toInt()] = str
                }
            } catch (e: Exception) {
            }
            i++
        }
    }

    fun getObjetivosPorNivel(nivel: Int): TreeMap<Integer, MisionObjetivoModelo>? {
        return if (_objetivos.size() <= nivel) {
            null
        } else _objetivos.get(nivel)
    }

    // XP, KAMAS, ITEMS, EMOTES, OFICIOS, HECHIZO = 1000|5000|
    // 311,15;9336,5;....etc|8,9,....|51,52,....|145,966,....
    fun darRecompensa(perso: Personaje) {
        for (i in 0..6) {
            try {
                if (_recompensas[i] != null) {
                    when (i) {
                        0 -> perso.addExperiencia(Integer.parseInt(_recompensas[0]), true)
                        1 -> perso.addKamas(Integer.parseInt(_recompensas[1]), true, true)
                        2 -> {
                            for (str in _recompensas[2].split(";")) {
                                if (str.isEmpty()) {
                                    continue
                                }
                                try {
                                    val id: Int = Integer.parseInt(str.split(",").get(0))
                                    val cant: Int = Integer.parseInt(str.split(",").get(1))
                                    perso.addObjIdentAInventario(Mundo.getObjetoModelo(id).crearObjeto(cant,
                                            Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM), false)
                                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "021;$cant~$id")
                                } catch (e: Exception) {
                                }
                            }
                            GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso)
                        }
                        3 -> for (str in _recompensas[3].split(",")) {
                            try {
                                if (str.isEmpty()) {
                                    continue
                                }
                                Accion(65, str, "").realizarAccion(perso, perso, -1, -1.toShort())
                            } catch (e: Exception) {
                            }
                        }
                        4 -> for (str in _recompensas[4].split(",")) {
                            try {
                                if (str.isEmpty()) {
                                    continue
                                }
                                Accion(6, str, "").realizarAccion(perso, perso, -1, -1.toShort())
                            } catch (e: Exception) {
                            }
                        }
                        5 -> for (str in _recompensas[5].split(",")) {
                            try {
                                if (str.isEmpty()) {
                                    continue
                                }
                                Accion(9, str, "").realizarAccion(perso, perso, -1, -1.toShort())
                            } catch (e: Exception) {
                            }
                        }
                        6 -> for (str in _recompensas[6].split(Pattern.quote("*"))) {
                            try {
                                if (str.isEmpty()) {
                                    continue
                                }
                                val accion: Int = Integer.parseInt(str.split("@").get(0))
                                var arg = ""
                                try {
                                    arg = str.split("@").get(1)
                                } catch (e: Exception) {
                                }
                                Accion(accion, arg, "").realizarAccion(perso, perso, -1, -1.toShort())
                            } catch (e: Exception) {
                            }
                        }
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    init {
        setRecompensa(recompensas)
        setObjetivos(objetivos)
        this.varios = varios
    }
}