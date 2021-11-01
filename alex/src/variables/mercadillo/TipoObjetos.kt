package variables.mercadillo

import java.util.HashMap
import java.util.Map
import variables.personaje.Personaje
import estaticos.GestorSalida

class TipoObjetos(val tipoObjetoID: Int) {
    private val _modelosDeUnTipo: Map<Integer, ModeloMercadillo> = HashMap<Integer, ModeloMercadillo>()
    fun addModeloVerificacion(objMerca: ObjetoMercadillo) {
        val modeloID: Int = objMerca.getObjeto().getObjModeloID()
        val modelo: ModeloMercadillo? = _modelosDeUnTipo[modeloID]
        if (modelo == null) {
            _modelosDeUnTipo.put(modeloID, ModeloMercadillo(modeloID, objMerca))
        } else {
            modelo.addObjMercaConLinea(objMerca)
        }
    }

    fun borrarObjMercaDeModelo(objMerca: ObjetoMercadillo, perso: Personaje?, puesto: Mercadillo?): Boolean {
        val idModelo: Int = objMerca.getObjeto().getObjModeloID()
        val borrable: Boolean = _modelosDeUnTipo[idModelo].borrarObjMercaDeUnaLinea(objMerca, perso, puesto)
        if (_modelosDeUnTipo[idModelo].estaVacio()) {
            _modelosDeUnTipo.remove(idModelo)
            GestorSalida.ENVIAR_EHM_MOVER_OBJMERCA_POR_MODELO(perso, "-", idModelo.toString() + "")
        }
        return borrable
    }

    fun getModelo(modeloID: Int): ModeloMercadillo? {
        return _modelosDeUnTipo[modeloID]
    }

    // public ArrayList<ObjetoMercadillo> todoListaObjMercaDeUnTipo() {
    // final ArrayList<ObjetoMercadillo> listaObjMerca = new ArrayList<ObjetoMercadillo>();
    // for (final ModeloMercadillo modelo : _modelosDeUnTipo.values()) {
    // listaObjMerca.addAll(modelo.todosObjMercaDeUnModelo());
    // }
    // return listaObjMerca;
    // }
    fun stringModelo(): String {
        val str = StringBuilder()
        for (idModelo in _modelosDeUnTipo.keySet()) {
            if (str.length() > 0) {
                str.append(";")
            }
            str.append(idModelo)
        }
        return str.toString()
    }
}