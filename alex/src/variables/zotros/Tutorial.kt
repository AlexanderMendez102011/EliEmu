package variables.zotros

import java.util.ArrayList
import estaticos.MainServidor

class Tutorial(val iD: Int, recompensa: String, inicio: String, fin: String) {
    private val _recompensa: ArrayList<Accion> = ArrayList<Accion>(4)
    private var _inicio: Accion? = null
    private var _final: Accion? = null
    val recompensa: ArrayList<Accion>
        get() = _recompensa
    val inicio: Accion?
        get() = _inicio
    val fin: Accion?
        get() = _final

    init {
        try {
            for (str in recompensa.split("\\$")) {
                if (str.isEmpty()) {
                    _recompensa.add(null)
                } else {
                    val a: Array<String> = str.split("@")
                    if (a.size >= 2) {
                        _recompensa.add(Accion(Integer.parseInt(a[0]), a[1], ""))
                    } else {
                        _recompensa.add(Accion(Integer.parseInt(a[0]), "", ""))
                    }
                }
            }
            if (inicio.isEmpty()) {
                _inicio = null
            } else {
                val b: Array<String> = inicio.split("@")
                if (b.size >= 2) {
                    _inicio = Accion(Integer.parseInt(b[0]), b[1], "")
                } else {
                    _inicio = Accion(Integer.parseInt(b[0]), "", "")
                }
            }
            if (fin.isEmpty()) {
                _final = null
            } else {
                val c: Array<String> = fin.split("@")
                if (c.size >= 2) {
                    _final = Accion(Integer.parseInt(c[0]), c[1], "")
                } else {
                    _final = Accion(Integer.parseInt(c[0]), "", "")
                }
            }
        } catch (e: Exception) {
            if (MainServidor.MODO_IMPRIMIR_LOG) System.out.println("Ocurrio un error al cargar el tutorial $iD")
            System.exit(1)
        }
    }
}