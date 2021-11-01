package variables.pelea

import java.util.ArrayList
import variables.hechizo.EfectoHechizo
import variables.hechizo.Hechizo
import variables.hechizo.StatHechizo
import variables.hechizo.EfectoHechizo.TipoDaño
import variables.mapa.Celda
import estaticos.Constantes
import estaticos.Encriptador
import estaticos.GestorSalida

class Trampa(pelea: Pelea, lanzador: Luchador, celda: Celda, tamaño: Byte,
             trampaHechizo: StatHechizo, hechizoID: Int, mostrar: ArrayList<Luchador?>,
             celdas: ArrayList<Celda>, color: Int) : Comparable<Trampa?> {
    private val _lanzador: Luchador
    private val _celda: Celda
    val tamaño: Byte
    private var _paramEquipoDueño: Byte = -1
    private val _hechizoID: Int
    val color: Int
    val _trampaSH: StatHechizo
    private val _pelea: Pelea
    private val _visibles: ArrayList<Integer> = ArrayList<Integer>()
    private val _celdas: ArrayList<Celda>
    val celda: Celda
        get() = _celda
    val paramEquipoDueño: Int
        get() = _paramEquipoDueño.toInt()
    val lanzador: Luchador
        get() = _lanzador

    // public void setObjetivos(ArrayList<Luchador> obj) {
    // _objetivos = obj;
    // }
    //
    // public ArrayList<Luchador> getObjetivos() {
    // return _objetivos;
    // }
    //
    fun esInvisiblePara(idMirador: Int): Boolean {
        if (idMirador != 0) {
            if (_visibles.contains(idMirador)) {
                return false
            }
        }
        return true
    }

    fun activarTrampa(victima: Luchador?) {
        if (_pelea.getFase() !== Constantes.PELEA_FASE_COMBATE) {
            return
        }
        _pelea.borrarTrampa(this)
        for (c in _celdas) {
            c.borrarTrampa(this)
        }
        desaparecer()
        if (victima == null) {
            return
        }
        GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, 7, 307, victima.getID().toString() + "", _hechizoID.toString() + "," + _celda.getID()
                + ",0,1,1," + _lanzador.getID())
        // try {
        // Thread.sleep(100);
        // } catch (Exception e) {}
        if (!victima.estaMuerto()) {
            Hechizo.aplicaHechizoAPelea(_pelea, _lanzador, _celda, _trampaSH.getEfectosNormales(), TipoDaño.TRAMPA, false)
            if (_pelea.getLuchadorTurno().getIA() != null) {
                _pelea.getLuchadorTurno().getIA().nullear()
            }
        }
    }

    fun aparecer(luchadores: ArrayList<Luchador?>) {
        for (luchador in luchadores) {
            if (!_visibles.contains(luchador.getID())) {
                _visibles.add(luchador.getID())
            }
        }
        GestorSalida.ENVIAR_GDZ_COLOREAR_ZONA_A_LUCHADORES(luchadores, "+", _celda.getID(), tamaño, color, ' ')
        val permisos = BooleanArray(16)
        val valores = IntArray(16)
        permisos[2] = true
        permisos[0] = true
        valores[2] = 25
        valores[0] = 1
        GestorSalida.ENVIAR_GDC_ACTUALIZAR_CELDA_A_LUCHADORES(luchadores, _celda.getID(), Encriptador.stringParaGDC(
                permisos, valores), false)
    }

    fun desaparecer() {
        val luchadores: ArrayList<Luchador> = ArrayList()
        for (i in _visibles) {
            luchadores.add(_pelea.getLuchadorPorID(i))
        }
        GestorSalida.ENVIAR_GDZ_COLOREAR_ZONA_A_LUCHADORES(luchadores, "-", _celda.getID(), tamaño, color, ' ')
        val permisos = BooleanArray(16)
        val valores = IntArray(16)
        permisos[2] = true
        permisos[0] = true
        GestorSalida.ENVIAR_GDC_ACTUALIZAR_CELDA_A_LUCHADORES(luchadores, _celda.getID(), Encriptador.stringParaGDC(
                permisos, valores), false)
    }

    private val prioridad: Int
        private get() {
            var p = 0
            for (eh in _trampaSH.getEfectosNormales()) {
                when (eh.getEfectoID()) {
                    5 -> p = 10
                }
            }
            return p
        }

    operator fun compareTo(o: Trampa): Int {
        return Integer.valueOf(prioridad).compareTo(o.prioridad)
    }

    // private ArrayList<Luchador> _objetivos;
    init {
        _pelea = pelea
        _lanzador = lanzador
        _celda = celda
        this.tamaño = tamaño
        _trampaSH = trampaHechizo
        _hechizoID = hechizoID
        this.color = color
        _paramEquipoDueño = lanzador.getParamEquipoAliado()
        _pelea.addTrampa(this)
        _celdas = celdas
        for (c in celdas) {
            c.addTrampa(this)
        }
        aparecer(mostrar)
    }
}