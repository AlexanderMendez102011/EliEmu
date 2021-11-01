package variables.mercadillo

import java.util.ArrayList
import java.util.Collections
import variables.objeto.Objeto

class LineaMercadillo(private val _lineaID: Int, objMerca: ObjetoMercadillo) {
    private val _modeloID: Int
    private val _categoriasDeUnaLinea: ArrayList<ArrayList<ObjetoMercadillo>> = ArrayList<ArrayList<ObjetoMercadillo>>(
            3)
    private val _strStats: String
    fun tieneIgual(objMerca: ObjetoMercadillo): Boolean {
        if (!lineaVacia() && !tieneMismoStats(objMerca.getObjeto())) {
            return false
        }
        objMerca.setLineaID(_lineaID)
        val categoria = (objMerca.getTipoCantidad(false) - 1) as Int
        _categoriasDeUnaLinea.get(categoria).add(objMerca)
        ordenar(categoria)
        return true
    }

    private fun tieneMismoStats(objeto: Objeto): Boolean {
        return _strStats.equalsIgnoreCase(objeto.convertirStatsAString(false))
    }

    fun tuTienes(categoria: Int, precio: Long): ObjetoMercadillo? {
        val index = categoria - 1
        for (i in 0 until _categoriasDeUnaLinea.get(index).size()) {
            if (_categoriasDeUnaLinea.get(index).get(i).getPrecio() === precio) {
                return _categoriasDeUnaLinea.get(index).get(i)
            }
        }
        return null
    }

    val los3PreciosPorLinea: LongArray
        get() {
            val str = LongArray(3)
            for (i in 0 until _categoriasDeUnaLinea.size()) {
                try {
                    str[i] = _categoriasDeUnaLinea.get(i).get(0).getPrecio()
                } catch (e: IndexOutOfBoundsException) {
                    str[i] = 0
                }
            }
            return str
        }

    //	public ArrayList<ObjetoMercadillo> todosObjMercaDeUnaLinea() {
    //		final int totalEntradas = _categoriasDeUnaLinea.get(0).size() + _categoriasDeUnaLinea.get(1).size()
    //		+ _categoriasDeUnaLinea.get(2).size();
    //		final ArrayList<ObjetoMercadillo> todosObjMerca = new ArrayList<ObjetoMercadillo>(totalEntradas);
    //		for (int cat = 0; cat < _categoriasDeUnaLinea.size(); cat++) {
    //			todosObjMerca.addAll(_categoriasDeUnaLinea.get(cat));
    //		}
    //		return todosObjMerca;
    //	}
    fun borrarObjMercaDeLinea(objMerca: ObjetoMercadillo): Boolean {
        val categoria = (objMerca.getTipoCantidad(false) - 1) as Int // 1, 10 ,100
        val borrable: Boolean = _categoriasDeUnaLinea.get(categoria).remove(objMerca)
        ordenar(categoria)
        return borrable
    }

    fun strListaDeLineasDeModelo(): String {
        val precio = los3PreciosPorLinea
        return (_lineaID.toString() + ";" + _strStats + ";" + (if (precio[0] == 0) "" else precio[0]) + ";"
                + (if (precio[1] == 0) "" else precio[1]) + ";" + if (precio[2] == 0) "" else precio[2])
    }

    fun str3PrecioPorLinea(): String {
        val precio = los3PreciosPorLinea
        return (_lineaID.toString() + "|" + _modeloID + "|" + _strStats + "|" + (if (precio[0] == 0) "" else precio[0]) + "|"
                + (if (precio[1] == 0) "" else precio[1]) + "|" + if (precio[2] == 0) "" else precio[2])
    }

    fun ordenar(categoria: Int) {
        Collections.sort(_categoriasDeUnaLinea.get(categoria))
    }

    fun lineaVacia(): Boolean {
        var i = 0
        while (i < 3) {
            // 3 categorias
            try {
                if (_categoriasDeUnaLinea.get(i).get(0) != null) {
                    return false
                }
            } catch (e: Exception) {
            }
            i++
        }
        return true
    }

    init {
        val objeto: Objeto = objMerca.getObjeto()
        _strStats = objeto.convertirStatsAString(false)
        _modeloID = objeto.getObjModeloID()
        for (i in 0..2) {
            _categoriasDeUnaLinea.add(ArrayList<ObjetoMercadillo>())
        }
        val categoria = (objMerca.getTipoCantidad(false) - 1) as Int
        _categoriasDeUnaLinea.get(categoria).add(objMerca)
        ordenar(categoria)
        objMerca.setLineaID(_lineaID)
    }
}