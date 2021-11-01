package variables.mercadillo

import variables.objeto.Objeto

class ObjetoMercadillo(val precio: Long, private val _tipoCantidad: Long, val cuentaID: Int, objeto: Objeto, mercadilloID: Int) : Comparable<ObjetoMercadillo?> {
    var lineaID = 0

    /*private void objeto_Desasociar_Mimobionte(Objeto objeto) {
           try {
               if(objeto.tieneStatTexto(Constantes.STAT_APARIENCIA_OBJETO)) {
                   int regresar = Integer.parseInt(objeto.getParamStatTexto(Constantes.STAT_APARIENCIA_OBJETO, 3), 16);
                   //Objeto apariencia = Mundo.getObjetoModelo(regresar).crearObjeto(1,Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM);
                   //Mundo.addObjeto(apariencia, true);
                   objeto.addStatTexto(Constantes.STAT_APARIENCIA_OBJETO, "");
                   Mundo.getCuenta(_cuentaID).addRegalo(regresar+"");
                   //_objetos.put(apariencia.getID(),apariencia);
                   GestorSQL.SALVAR_OBJETO(objeto);
                   System.out.println("Mercadillo: "+_objeto.getID()+" disasicion objeto: "+objeto.getObjModelo().getNombre());
               }
           } catch (Exception e) {
           }
       }*/  val mercadilloID: Int
    private val _objeto: Objeto
    fun getTipoCantidad(cantidadReal: Boolean): Long {
        return if (cantidadReal) {
            ((Math.pow(10.0, _tipoCantidad) / 10.0) as Int).toLong()
        } else _tipoCantidad
    }

    val objeto: Objeto
        get() = _objeto
    val objetoID: Int
        get() = _objeto.getID()

    fun analizarParaEL(): String {
        return _objeto.getID().toString() + ";" + getTipoCantidad(true) + ";" + _objeto.getObjModeloID() + ";" + _objeto
                .convertirStatsAString(false) + ";" + precio + ";350"
    }

    fun analizarParaEmK(): String {
        return _objeto.getID().toString() + "|" + getTipoCantidad(true) + "|" + _objeto.getObjModeloID() + "|" + _objeto
                .convertirStatsAString(false) + "|" + precio + "|350"
    }

    fun analizarObjeto(separador: Char): String {
        return lineaID + separador.code + getTipoCantidad(true) + separador + _objeto.getObjModeloID() + separador + _objeto
                .convertirStatsAString(false) + separador + precio + separador.toString() + "350"
    }

    operator fun compareTo(objMercadillo: ObjetoMercadillo): Int {
        val otroPrecio = objMercadillo.precio
        if (otroPrecio > precio) {
            return -1
        }
        if (otroPrecio == precio) {
            return 0
        }
        return if (otroPrecio < precio) {
            1
        } else 0
    }

    init {
        _objeto = objeto
        this.mercadilloID = mercadilloID
        //objeto_Desasociar_Mimobionte(objeto);
    }
}