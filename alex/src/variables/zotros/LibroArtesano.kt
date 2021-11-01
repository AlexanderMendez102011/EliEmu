package variables.zotros

class LibroArtesano {
    var mapa = 0
    var oficios: String? = null

    constructor() {}
    constructor(mapa: Int, oficios: String?) {
        this.mapa = mapa
        this.oficios = oficios
    }
}