package variables.mercadillo

import java.util.HashMap
import java.util.Map
import variables.personaje.Personaje
import estaticos.GestorSalida
import estaticos.Mundo

class ModeloMercadillo(private val _modeloID: Int, objMerca: ObjetoMercadillo?) {
    private val _lineasDeUnModelo: Map<Integer, LineaMercadillo> = HashMap<Integer, LineaMercadillo>()
    fun addObjMercaConLinea(objMerca: ObjetoMercadillo?) {
        for (linea in _lineasDeUnModelo.values()) {
            if (linea.tieneIgual(objMerca)) {
                return
            }
        }
        val lineaID: Int = Mundo.sigIDLineaMercadillo()
        _lineasDeUnModelo.put(lineaID, LineaMercadillo(lineaID, objMerca))
    }

    fun getLinea(lineaID: Int): LineaMercadillo? {
        return _lineasDeUnModelo[lineaID]
    }

    fun borrarObjMercaDeUnaLinea(objMerca: ObjetoMercadillo, perso: Personaje?,
                                 puesto: Mercadillo): Boolean {
        val lineaID: Int = objMerca.getLineaID()
        val borrable: Boolean = _lineasDeUnModelo[lineaID].borrarObjMercaDeLinea(objMerca)
        if (_lineasDeUnModelo[lineaID].lineaVacia()) {
            _lineasDeUnModelo.remove(lineaID)
            puesto.borrarPath(lineaID)
            GestorSalida.ENVIAR_EHm_DETALLE_LINEA_CON_PRECIOS(perso, "-", lineaID.toString() + "")
        } else {
            GestorSalida
                    .ENVIAR_EHm_DETALLE_LINEA_CON_PRECIOS(perso, "+", _lineasDeUnModelo[lineaID].str3PrecioPorLinea())
        }
        return borrable
    }

    // public ArrayList<ObjetoMercadillo> todosObjMercaDeUnModelo() {
    // final ArrayList<ObjetoMercadillo> listaObj = new ArrayList<ObjetoMercadillo>();
    // for (final LineaMercadillo linea : _lineasDeUnModelo.values()) {
    // listaObj.addAll(linea.todosObjMercaDeUnaLinea());
    // }
    // return listaObj;
    // }
    fun strLineasPorObjMod(): String {
        val str = StringBuilder()
        for (linea in _lineasDeUnModelo.values()) {
            if (str.length() > 0) {
                str.append("|")
            }
            str.append(linea.strListaDeLineasDeModelo())
        }
        return _modeloID.toString() + "|" + str.toString()
    }

    fun estaVacio(): Boolean {
        return _lineasDeUnModelo.isEmpty()
    }

    init {
        addObjMercaConLinea(objMerca)
    }
}