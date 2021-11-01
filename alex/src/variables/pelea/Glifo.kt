package variables.pelea

import java.util.ArrayList
import variables.hechizo.Hechizo
import variables.hechizo.StatHechizo
import variables.hechizo.EfectoHechizo.TipoDaño
import variables.mapa.Celda
import estaticos.Constantes
import estaticos.GestorSalida

class Glifo(pelea: Pelea, lanzador: Luchador, celda: Celda, tamaño: Byte,
            glifoHechizo: StatHechizo, _duracion2: Int, hechizoID: Int, inicioTurno: Boolean,
            celdas: ArrayList<Celda>, color: Int, forma: Char) {
    private val _inicioTurno: Boolean
    private val _lanzador: Luchador
    private val _celda: Celda
    val tamaño: Byte
    var duracion: Int
        private set
    val _hechizoID: Int
    val color: Int
    private val _glifoSH: StatHechizo
    private val _pelea: Pelea
    private val _celdas: ArrayList<Celda>
    val forma: Char
    fun esInicioTurno(): Boolean {
        return _inicioTurno
    }

    val celda: Celda
        get() = _celda
    val lanzador: Luchador
        get() = _lanzador

    fun disminuirDuracion(): Int {
        duracion--
        return duracion
    }

    fun activarGlifo(glifeado: Luchador) {
        if (_pelea.getFase() !== Constantes.PELEA_FASE_COMBATE) {
            return
        }
        GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, 7, 307, glifeado.getID().toString() + "", _hechizoID.toString() + "," + _celda.getID()
                + ",0,1,1," + _lanzador.getID())
        try {
            Thread.sleep(100)
        } catch (e: Exception) {
        }
        Hechizo.aplicaHechizoAPelea(_pelea, _lanzador, glifeado.getCeldaPelea(), _glifoSH.getEfectosNormales(),
                TipoDaño.GLIFO, false)
        // _pelea.acaboPelea((byte) 3);
    }

    fun desaparecer() {
        _pelea.borrarGlifo(this)
        for (c in _celdas) {
            c.borrarGlifo(this)
        }
        GestorSalida.ENVIAR_GDZ_COLOREAR_ZONA_EN_PELEA(_pelea, 7, "-", _celda.getID(), tamaño, color, ' ')
        // GestorSalida.ENVIAR_GDC_ACTUALIZAR_CELDA_EN_PELEA(_pelea, 7, _celda.getID(),
        // "Haaaaaaaaa3005", false);
    }

    init {
        _pelea = pelea
        _lanzador = lanzador
        _celda = celda
        _hechizoID = hechizoID
        this.tamaño = tamaño
        _glifoSH = glifoHechizo
        duracion = _duracion2
        this.color = color
        _inicioTurno = inicioTurno
        _pelea.addGlifo(this)
        _celdas = celdas
        this.forma = forma
        for (c in celdas) {
            c.addGlifo(this)
        }
        GestorSalida.ENVIAR_GDZ_COLOREAR_ZONA_EN_PELEA(pelea, 7, "+", celda.getID(), this.tamaño, this.color, this.forma)
        // GestorSalida.ENVIAR_GDC_ACTUALIZAR_CELDA_EN_PELEA(pelea, 7, celda.getID(), "Haaaaaaaaa3005",
        // false);
    }
}