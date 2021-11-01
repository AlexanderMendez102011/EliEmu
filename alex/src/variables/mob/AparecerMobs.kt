package variables.mob

import variables.mapa.Mapa
import estaticos.MainServidor

class AparecerMobs(mapa: Mapa, grupoMob: GrupoMob, aparecer: Aparecer) : Thread() {
    enum class Aparecer {
        INICIO_PELEA, FINAL_PELEA
    }

    private val _mapa: Mapa
    private val _grupoMob: GrupoMob
    private val _tipoAparecer: Aparecer
    fun run() {
        var tiempo: Int = MainServidor.SEGUNDOS_REAPARECER_MOBS
        if (_grupoMob.getSegundosRespawn() > 0) {
            tiempo = _grupoMob.getSegundosRespawn()
        }
        if (tiempo > 0) {
            try {
                Thread.sleep(tiempo * 1000)
            } catch (e: Exception) {
            }
        }
        when (_tipoAparecer) {
            Aparecer.INICIO_PELEA -> _mapa.addSiguienteGrupoMob(_grupoMob, false)
            Aparecer.FINAL_PELEA -> _mapa.addUltimoGrupoMob(_grupoMob, false)
        }
    }

    init {
        _mapa = mapa
        _grupoMob = grupoMob
        _tipoAparecer = aparecer
        setDaemon(true)
        setPriority(6)
        start()
    }
}