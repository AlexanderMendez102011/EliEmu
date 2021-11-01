package estaticos

import java.io.BufferedReader

object Encriptador {
    private val HASH = charArrayOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',  // 15
            'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',  // 38
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',  // 61
            '-', '_') // q = 16, N = 40, - = 63 _ = 64
    private val HEX_CHARS = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
    const val ABC_MIN = "abcdefghijklmnopqrstuvwxyz"
    const val ABC_MAY = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    const val VOCALES = "aeiouAEIOU"
    const val CONSONANTES = "bcdfghjklmnpqrstvwxyzBCDFGHJKLMNPQRSTVWXYZ"
    const val NUMEROS = "0123456789"
    const val ESPACIO = " "
    const val GUIONES = "_-"
    fun crearKey(limite: Int): String {
        val nombre = StringBuilder()
        while (nombre.length() < limite) {
            nombre.append(HASH[Formulas.getRandomInt(0, HASH.size - 1)])
        }
        val key = StringBuilder()
        for (c in nombre.toString().toCharArray()) {
            key.append(Integer.toHexString(c))
        }
        return key.toString()
    }

    fun palabraAleatorio(limite: Int): String {
        val nombre = StringBuilder()
        var i = Math.floor(Math.random() * ABC_MAY.length()) as Int
        var temp: Char = ABC_MAY.charAt(i)
        nombre.append(temp)
        var xxx: Char
        while (nombre.length() < limite) {
            i = Math.floor(Math.random() * ABC_MIN.length())
            xxx = ABC_MIN.charAt(i)
            if (temp == xxx || VOCALES.contains(temp.toString() + "") && VOCALES.contains(xxx.toString() + "") || (CONSONANTES.contains(temp.toString() + "")
                            && CONSONANTES.contains(xxx.toString() + ""))) {
                continue
            }
            temp = xxx
            nombre.append(xxx)
        }
        return nombre.toString()
    }

    fun stringParaGDC(permisos: BooleanArray, valores: IntArray): String {
        // 16 var layerObjectExternalAutoSize = (_loc6 & 65536) != 0;
        // 15 var layerObjectExternalInteractive = (_loc6 & 32768) != 0;
        // 14 var layerObjectExternal = (_loc6 & 16384) != 0;
        // 13 var active = (_loc6 & 8192) != 0;
        // 12 var lineOfSight = (_loc6 & 4096) != 0;
        // 11 var movement = (_loc6 & 2048) != 0;
        // 10 var groundLevel = (_loc6 & 1024) != 0;
        // 9 var groundSlope = (_loc6 & 512) != 0;
        // 8 var layerGroundNum = (_loc6 & 256) != 0;
        // 7 var layerGroundFlip = (_loc6 & 128) != 0;
        // 6 var layerGroundRot = (_loc6 & 64) != 0;
        // 5 var layerObject1Num = (_loc6 & 32) != 0;
        // 4 var layerObject1Flip = (_loc6 & 16) != 0;
        // 3 var layerObject1Rot = (_loc6 & 8) != 0;
        // 2 var layerObject2Num = (_loc6 & 4) != 0;
        // 1 var layerObject2Flip = (_loc6 & 2) != 0;
        // 0 var layerObject2Interactive = (_loc6 & 1) != 0; << 0
        var i = 0
        var finalPermiso = 0
        for (b in permisos) {
            if (b) {
                finalPermiso += 1 shl i
            }
            i++
        }
        val fP: String = Integer.toHexString(finalPermiso)
        val preData = IntArray(10)
        preData[0] = (if (valores[13] == 1) 1 else 0) shl 5
        preData[0] = preData[0] or if (valores[12] == 1) 1 else 0
        preData[0] = preData[0] or (valores[8] and 1536) shr 6
        preData[0] = preData[0] or (valores[5] and 8192) shr 11
        preData[0] = preData[0] or (valores[2] and 8192) shr 12
        preData[1] = valores[3] and 3 shl 4
        preData[1] = preData[1] or valores[10] and 15
        preData[2] = valores[11] and 7 shl 3
        preData[2] = preData[2] or valores[8] shr 6 and 7
        preData[3] = valores[8] and 63
        preData[4] = valores[9] and 15 shl 2
        preData[4] = preData[4] or (if (valores[7] == 1) 1 else 0) shl 1
        preData[4] = preData[4] or valores[5] shr 12 and 1
        preData[5] = valores[5] shr 6 and 63
        preData[6] = valores[5] and 63
        preData[7] = valores[3] and 3 shl 4
        preData[7] = preData[7] or (if (valores[4] == 1) 1 else 0) shl 3
        preData[7] = preData[7] or (if (valores[1] == 1) 1 else 0) shl 2
        preData[7] = preData[7] or (if (valores[0] == 1) 1 else 0) shl 1
        preData[7] = preData[7] or valores[2] shr 12 and 1
        preData[8] = valores[2] shr 6 and 63
        preData[9] = valores[2] and 63
        var fD = ""
        for (d in preData) {
            fD += getValorHashPorNumero(d)
        }
        return fD + fP
    }

    fun encriptarIP(IP: String): String {
        val split: Array<String> = IP.split(Pattern.quote("."))
        val encriptado = StringBuilder()
        var cantidad = 0
        var i = 0
        while (i < 50) {
            var o = 0
            while (o < 50) {
                if (i and 15 shl 4 or o and 15 == Integer.parseInt(split[cantidad])) {
                    val A: Character = (i + 48).toChar()
                    val B: Character = (o + 48).toChar()
                    encriptado.append(A.toString() + B.toString())
                    i = 0
                    o = 0
                    cantidad++
                    if (cantidad == 4) {
                        return encriptado.toString()
                    }
                }
                o++
            }
            i++
        }
        return "DD"
    }

    fun encriptarPuerto(puerto: Int): String {
        var P = puerto
        val numero = StringBuilder()
        for (a in 2 downTo 0) {
            numero.append(HASH[(P / Math.pow(64, a)) as Int])
            P = P % Math.pow(64, a) as Int
        }
        return numero.toString()
    }

    fun celdaIDAHash(celdaID: Short): String {
        return HASH[celdaID / 64].toString() + "" + HASH[celdaID % 64]
    }

    fun hashACeldaID(celdaCodigo: String): Short {
        val char1: Char = celdaCodigo.charAt(0)
        val char2: Char = celdaCodigo.charAt(1)
        var code1: Short = 0
        var code2: Short = 0
        var a: Short = 0
        while (a < HASH.size) {
            if (HASH[a.toInt()] == char1) {
                code1 = (a * 64).toShort()
            }
            if (HASH[a.toInt()] == char2) {
                code2 = a
            }
            a++
        }
        return (code1 + code2).toShort()
    }

    fun getNumeroPorValorHash(c: Char): Byte {
        for (a in HASH.indices) {
            if (HASH[a.toInt()] == c) {
                return a
            }
        }
        return -1
    }

    fun getValorHashPorNumero(c: Int): Char {
        var c = c
        return try {
            if (c >= HASH.size || c < 0) {
                c = 0
            }
            HASH[c]
        } catch (e: Exception) {
            'a'
        }
    }

    fun celdaIDACodigo(celdaID: Int): String {
        val char1 = celdaID / 64
        val char2 = celdaID % 64
        return HASH[char1].toString() + "" + HASH[char2]
    }

    fun celdaCodigoAID(celdaCodigo: String): Short {
        val char1: Char = celdaCodigo.charAt(0)
        val char2: Char = celdaCodigo.charAt(1)
        var code1: Short = 0
        var code2: Short = 0
        var a: Short = 0
        while (a < HASH.size) {
            if (HASH[a.toInt()] == char1) {
                code1 = (a * 64).toShort()
            }
            if (HASH[a.toInt()] == char2) {
                code2 = a
            }
            a = (a + 1).toShort()
        }
        return (code1 + code2).toShort()
    }

    fun analizarCeldasDeInicio(posPelea: String, listaCeldas: ArrayList<Short?>) {
        try {
            var a = 0
            while (a < posPelea.length()) {
                listaCeldas.add(((getNumeroPorValorHash(posPelea.charAt(a)) shl 6) + getNumeroPorValorHash(posPelea
                        .charAt(a + 1))).toShort())
                a += 2
            }
        } catch (e: Exception) {
        }
    }

    private fun reeplazarEl(str: String?, index: Int, replace: Char): String? {
        if (str == null) {
            return str
        } else if (index < 0 || index >= str.length()) {
            return str
        }
        val chars: CharArray = str.toCharArray()
        chars[index] = replace
        return String.valueOf(chars)
    }

    fun decompilarMapaDataOBJ(dData: String, _personaliza: Map<Integer?, String>, _limpieza: Map<Integer?, String>, id: Int, mapa: Mapa?): String {
        val str = StringBuilder()
        var f = 0
        while (f < dData.length()) {
            var CellData: String? = ""
            try {
                CellData = dData.substring(f, f + 10)
            } catch (exception: Exception) {
                System.out.println("Revisa el mapa: $id")
                f += 10
                continue
            }
            val celda = (f / 10).toShort().toInt()
            if (_limpieza.containsKey(celda)) {
                CellData = _limpieza[celda].split(",").get(0)
            }
            if (_personaliza.containsKey(celda)) {
                val objid: String = _personaliza[celda].split(",").get(0)
                if (objid.length() === 3) str.append(CellData.substring(0, CellData!!.length() - 3).toString() + "" + objid) else {
                    when (CellData.charAt(2)) {
                        'G' -> CellData = reeplazarEl(CellData, 2, 'a')
                        'H' -> CellData = reeplazarEl(CellData, 2, 'b')
                        'I' -> CellData = reeplazarEl(CellData, 2, 'c')
                        'J' -> CellData = reeplazarEl(CellData, 2, 'd')
                        'K' -> CellData = reeplazarEl(CellData, 2, 'e')
                        'L' -> CellData = reeplazarEl(CellData, 2, 'f')
                        'M' -> CellData = reeplazarEl(CellData, 2, 'g')
                        'N' -> CellData = reeplazarEl(CellData, 2, 'h')
                    }
                    str.append(CellData.substring(0, CellData!!.length() - 3).toString() + "" + objid.substring(7, objid.length()))
                }
            } else {
                str.append(CellData)
            }
            f += 10
        }
        return str.toString()
    }

    fun decompilarMapaData(mapa: Mapa) {
        try {
            var activo: Boolean
            var lineaDeVista: Boolean
            var tieneObjInteractivo: Boolean
            var caminable: Byte
            var level: Byte
            var slope: Byte
            var objInteractivo: Short
            var f: Short = 0
            while (f < mapa.getMapData().length()) {
                val celdaData = StringBuilder(mapa.getMapData().substring(f, f + 10))
                val celdaInfo: ArrayList<Byte> = ArrayList<Byte>()
                for (i in 0 until celdaData.length()) {
                    celdaInfo.add(getNumeroPorValorHash(celdaData.charAt(i)))
                }
                activo = celdaInfo.get(0) and 32 shr 5 !== 0
                lineaDeVista = celdaInfo.get(0) and 1 !== 0
                tieneObjInteractivo = celdaInfo.get(7) and 2 shr 1 !== 0
                caminable = (celdaInfo.get(2) and 56 shr 3) // 0 = no, 1 = medio, 4 = si
                level = (celdaInfo.get(1) and 15)
                slope = (celdaInfo.get(4) and 60 shr 2)
                objInteractivo = ((celdaInfo.get(0) and 2 shl 12) + (celdaInfo.get(7) and 1 shl 12) + (celdaInfo.get(
                        8) shl 6) + celdaInfo.get(9))
                val celdaID = (f / 10).toShort()
                val celda = Celda(mapa, celdaID, activo, caminable, level, slope, lineaDeVista, if (tieneObjInteractivo) objInteractivo else -1)
                mapa.getCeldas().put(celdaID, celda)
                celda.celdaNornmal()
                if (tieneObjInteractivo && objInteractivo.toInt() != -1) {
                    Constantes.getTrabajosPorOI(objInteractivo, mapa.getTrabajos())
                }
                (f += 10).toShort()
            }
        } catch (e: Exception) {
            MainServidor.redactarLogServidorln("El mapa ID " + mapa.getID().toString() + " esta errado, con mapData lenght " + mapa
                    .getMapData().length())
            e.printStackTrace()
        }
    }

    fun decifrarMapData(key: String, preData: String): String {
        var key = key
        var data = preData
        try {
            key = prepareKey(key)
            data = decypherData(preData, key, checksum(key).toString() + "")
        } catch (e: Exception) {
        }
        return data
    }

    fun unprepareData(s: String, currentKey: Int, aKeys: Array<String?>): String {
        return try {
            if (currentKey < 1) {
                return s
            }
            val _loc3 = aKeys[Integer.parseInt(s.substring(0, 1), 16)] ?: return s
            val _loc4: String = s.substring(1, 2).toUpperCase()
            val _loc5 = decypherData(s.substring(2), _loc3, _loc4)
            if (checksum(_loc5) != _loc4.charAt(0)) {
                s
            } else _loc5
        } catch (e: Exception) {
            s
        }
    }

    fun desencriptarContraseña(contraseña: String, key: String): String {
        var l1: Int
        var l2: Int
        var l3: Int
        var l4: Int
        var l5: Int
        var l7 = ""
        val abecedario = ABC_MIN + ABC_MAY + GUIONES
        l1 = 0
        while (l1 <= contraseña.length() - 1) {
            l3 = key.charAt(l1 / 2)
            l2 = abecedario.indexOf(contraseña.charAt(l1))
            l4 = 64 + l2 - l3
            val l11 = l1 + 1
            l2 = abecedario.indexOf(contraseña.charAt(l11))
            l5 = 64 + l2 - l3
            if (l5 < 0) {
                l5 = 64 + l5
            }
            l7 = l7 + (16 * l4 + l5).toChar()
            l1 += 2
        }
        return l7
    }

    fun prepareData(s: String, currentKey: Int, aKeys: Array<String?>): String {
        if (currentKey < 1) {
            return s
        }
        if (aKeys[currentKey] == null) {
            return s
        }
        val _loc3 = HEX_CHARS[currentKey]
        val _loc4 = checksum(s)
        return _loc3.toString() + "" + _loc4 + "" + cypherData(s, aKeys[currentKey], Integer.parseInt(_loc4.toString() + "", 16) * 2)
    }

    private fun cypherData(d: String, k: String?, c: Int): String {
        var d = d
        val _loc5 = StringBuilder()
        val _loc6: Int = k!!.length()
        d = preEscape(d)
        for (_loc7 in 0 until d.length()) {
            _loc5.append(d2h(d.charAt(_loc7) as Int xor k.charAt((_loc7 + c) % _loc6) as Int))
        }
        return _loc5.toString()
    }

    @Throws(Exception::class)
    private fun decypherData(d: String, k: String, checksum: String): String {
        val c: Int = Integer.parseInt(checksum, 16) * 2
        var _loc5 = ""
        val _loc6: Int = k.length()
        var _loc7 = 0
        var _loc9 = 0
        while (_loc9 < d.length()) {
            _loc5 += (Integer.parseInt(d.substring(_loc9, _loc9 + 2), 16) xor k.codePointAt((_loc7 + c) % _loc6)) as Char
            _loc7++
            _loc9 = _loc9 + 2
        }
        _loc5 = unescape(_loc5)
        return _loc5
    }

    private fun d2h(d: Int): String {
        var d = d
        if (d > 255) {
            d = 255
        }
        return HEX_CHARS[Math.floor(d / 16)].toString() + "" + HEX_CHARS[d % 16]
    }

    private fun unescape(s: String): String {
        var s = s
        try {
            s = URLDecoder.decode(s, "UTF-8")
        } catch (e: Exception) {
        }
        return s
    }

    // oscila del 32 al 127, todos los contenidos de k 95
    private fun escape(s: String): String {
        var s = s
        try {
            s = URLEncoder.encode(s, "UTF-8")
        } catch (e: Exception) {
        }
        return s
    }

    private fun preEscape(s: String): String {
        val _loc3 = StringBuilder()
        for (_loc4 in 0 until s.length()) {
            val _loc5: Char = s.charAt(_loc4)
            val _loc6: Int = _loc5.code
            if (_loc6 < 32 || _loc6 > 127 || _loc5 == '%' || _loc5 == '+') {
                _loc3.append(escape(_loc5.toString() + ""))
                continue
            }
            _loc3.append(_loc5)
        }
        return _loc3.toString()
    }

    fun prepareKey(d: String): String {
        var _loc3 = String()
        var _loc4 = 0
        while (_loc4 < d.length()) {
            _loc3 = _loc3 + Integer.parseInt(d.substring(_loc4, _loc4 + 2), 16) as Char
            _loc4 = _loc4 + 2
        }
        _loc3 = unescape(_loc3)
        return _loc3
    }

    private fun checksum(s: String): Char {
        var _loc3 = 0
        var _loc4 = 0
        while (_loc4 < s.length()) {
            _loc3 = _loc3 + s.codePointAt(_loc4) % 16
            _loc4++
        }
        return HEX_CHARS[_loc3 % 16]
    }

    @Throws(Exception::class)
    fun consultaWeb(url: String?) {
        val obj = URL(url)
        val con: URLConnection = obj.openConnection()
        con.setRequestProperty("Content-type", "charset=Unicode")
        val `in` = BufferedReader(InputStreamReader(con.getInputStream()))
        while (`in`.readLine() != null) {
            Thread.sleep(5)
        }
        `in`.close()
    }

    fun aUTF(entrada: String): String {
        var out = ""
        try {
            out = String(entrada.getBytes("UTF-8"))
        } catch (e: Exception) {
            if (MainServidor.MODO_IMPRIMIR_LOG) System.out.println("Conversion en UTF-8 fallida! : " + e.toString())
        }
        return out
    }

    fun aUnicode(entrada: String): String {
        var out = ""
        try {
            out = String(entrada.getBytes(), "UTF-8")
        } catch (e: Exception) {
            if (MainServidor.MODO_IMPRIMIR_LOG) System.out.println("Conversion en UNICODE fallida! : " + e.toString())
        }
        return out
    }
}