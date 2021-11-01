package variables.zotros

class MasterReset {
    var id = 0
    var nivel = 0
    var sufijo: String? = null
    var capital = 0
    var hechizo = 0
    var isReinicia = false

    constructor() {}
    constructor(id: Int, nivel: Int, sufijo: String?, capital: Int, hechizo: Int, reinicia: Boolean) : super() {
        this.id = id
        this.nivel = nivel
        this.sufijo = sufijo
        this.capital = capital
        this.hechizo = hechizo
        isReinicia = reinicia
    }
}