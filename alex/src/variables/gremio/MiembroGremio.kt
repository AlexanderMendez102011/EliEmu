package variables.gremio

import java.util.Map
import java.util.TreeMap
import org.joda.time.Hours
import org.joda.time.LocalDateTime
import variables.personaje.Personaje
import estaticos.Constantes
import estaticos.Mundo

class MiembroGremio(val iD: Int, gremio: Gremio, rango: Int, xpDonada: Long, porcXp: Byte,
                    derechos: Int) {
    var porcXpDonada: Int
        private set
    private var _rango: Int
    var derechos = 0
        private set
    var xpDonada: Long
        private set
    private val _gremio: Gremio
    private val _perso: Personaje
    private val _tieneDerecho: Map<Integer, Boolean> = TreeMap<Integer, Boolean>()
    var rango: Int
        get() = if (derechos == 1) 1 else _rango
        set(rango) {
            _rango = rango
        }
    val gremio: Gremio
        get() = _gremio

    fun analizarDerechos(): String {
        return Integer.toString(derechos, 36)
    }

    val gfx: Int
        get() = _perso.getGfxID(false)
    val nivel: Int
        get() = _perso.getNivel()
    val nombre: String
        get() = _perso.getNombre()
    val ultimaConexion: String
        get() = _perso.getCuenta().getUltimaConexion()
    val personaje: Personaje
        get() = _perso
    val horasDeUltimaConeccion: Int
        get() = try {
            val strFecha: Array<String> = ultimaConexion.split("~")
            val ultConeccion = LocalDateTime(Integer.parseInt(strFecha[0]), Integer.parseInt(strFecha[1]),
                    Integer.parseInt(strFecha[2]), Integer.parseInt(strFecha[3]), Integer.parseInt(strFecha[4]), Integer.parseInt(
                    strFecha[5]))
            val ahora = LocalDateTime()
            Hours.hoursBetween(ultConeccion, ahora).getHours()
        } catch (e: Exception) {
            0
        }

    fun puede(derecho: Int): Boolean {
        return if (_tieneDerecho[Constantes.G_TODOS_LOS_DERECHOS]!! || _rango == 1) {
            true
        } else _tieneDerecho[derecho]!!
    }

    fun darXpAGremio(xp: Long) {
        xpDonada += xp
        _gremio.addExperiencia(xp, false)
    }

    fun setTodosDerechos(rango: Int, porcXpdonar: Int, derechos: Int) {
        var porcXpdonar = porcXpdonar
        if (rango != -1) {
            _rango = rango
        }
        if (porcXpdonar != -1) {
            if (porcXpdonar < 0) {
                porcXpdonar = 0
            }
            if (porcXpdonar > 90) {
                porcXpdonar = 90
            }
            porcXpDonada = porcXpdonar
        }
        if (derechos != -1) {
            convertirDerechosAInt(derechos)
        }
    }

    private fun convertirDerechosAInt(derechos: Int) {
        // derechosIniciales();
        var newDerechos = 0
        for (i in 0..14) {
            val elevado = Math.pow(2, i) as Int
            var permiso = derechos and elevado == elevado
            if (_rango == 1) {
                permiso = true
                newDerechos = 1
            } else {
                if (derechos == 1) {
                    permiso = elevado != 1
                }
                if (permiso) {
                    newDerechos += elevado
                }
            }
            _tieneDerecho.put(elevado, permiso)
        }
        this.derechos = newDerechos
    } // private void derechosIniciales() {

    // _tieneDerecho.put(Informacion.G_TODOS_LOS_DERECHOS, false);
    // _tieneDerecho.put(Informacion.G_MODIF_BOOST, false);
    // _tieneDerecho.put(Informacion.G_MODIF_DERECHOS, false);
    // _tieneDerecho.put(Informacion.G_INVITAR, false);
    // _tieneDerecho.put(Informacion.G_BANEAR, false);
    // _tieneDerecho.put(Informacion.G_TODAS_XP_DONADAS, false);
    // _tieneDerecho.put(Informacion.G_MODIF_RANGOS, false);
    // _tieneDerecho.put(Informacion.G_PONER_RECAUDADOR, false);
    // _tieneDerecho.put(Informacion.G_SU_XP_DONADA, false);
    // _tieneDerecho.put(Informacion.G_RECOLECTAR_RECAUDADOR, false);
    // _tieneDerecho.put(Informacion.G_USAR_CERCADOS, false);
    // _tieneDerecho.put(Informacion.G_MEJORAR_CERCADOS, false);
    // _tieneDerecho.put(Informacion.G_OTRAS_MONTURAS, false);
    // }
    init {
        _perso = Mundo.getPersonaje(iD)
        _gremio = gremio
        _rango = rango
        this.xpDonada = xpDonada
        porcXpDonada = porcXp.toInt()
        convertirDerechosAInt(derechos)
        _perso.setMiembroGremio(this)
    }
}