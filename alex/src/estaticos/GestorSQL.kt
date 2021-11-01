package estaticos

import java.math.BigInteger

object GestorSQL {
    private val _bdDinamica: Connection? = null
    private val _bdEstatica: Connection? = null
    private val _bdCuentas: Connection? = null
    private val dinamcios: HikariDataSource? = null
    private val estaticos: HikariDataSource? = null
    private val cuentas: HikariDataSource? = null
    var LOG_SQL: StringBuilder = StringBuilder()

    // public static ResultSet consultaSQL(final PreparedStatement declaracion) throws Exception {
    // if (!Bustemu._INICIADO) {
    // return null;
    // }
    // final ResultSet resultado = declaracion.executeQuery();
    // declaracion.setQueryTimeout(300);
    // return resultado;
    // }
    // private static PreparedStatement preparedQuery(final String consultaSQL, final Connection
    // conexion) throws Exception {
    // if (!Bustemu._INICIADO) {
    // return null;
    // }
    // return (PreparedStatement) conexion.prepareStatement(consultaSQL);
    // }
    //
    private fun cerrarResultado(resultado: ResultSet) {
        try {
            resultado.getStatement().close()
            resultado.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun exceptionExit(e: Exception, metodo: String) {
        MainServidor.redactarLogServidorln("EXCEP EXIT SQL " + metodo + ": " + e.toString())
        e.printStackTrace()
        System.exit(1)
    }

    private fun exceptionNormal(e: Exception, metodo: String) {
        MainServidor.redactarLogServidorln("EXCEP NORMAL SQL " + metodo + ": " + e.toString())
        e.printStackTrace()
    }

    private fun exceptionModify(e: Exception, consultaSQL: String, metodo: String) {
        MainServidor.redactarLogServidorln("EXCEP MODIFY SQL " + metodo + ": " + e.toString())
        MainServidor.redactarLogServidorln("LINEA MODIFY SQL $metodo: $consultaSQL")
        e.printStackTrace()
    }

    @Throws(Exception::class)
    fun consultaSQL(consultaSQL: String?, coneccion: Connection): ResultSet {
        val declaracion: PreparedStatement = coneccion.prepareStatement(consultaSQL) as PreparedStatement
        val resultado: ResultSet = declaracion.executeQuery()
        declaracion.setQueryTimeout(300)
        return resultado
    }

    @Throws(Exception::class)
    fun transaccionSQL(consultaSQL: String?, conexion: Connection): PreparedStatement {
        return conexion.prepareStatement(consultaSQL) as PreparedStatement
    }

    private fun ejecutarTransaccion(declaracion: PreparedStatement): Int {
        var ejecutar = 0
        try {
            ejecutar = declaracion.executeUpdate()
        } catch (e: SQLException) {
            MainServidor.redactarLogServidorln("EXECUTE UPDATE " + declaracion.toString())
            e.printStackTrace()
        }
        val str: String = declaracion.toString()
        estaticos.GestorSQL.LOG_SQL.append(System.currentTimeMillis().toString() + " " + str.substring(str.indexOf(":")) + "\n")
        return ejecutar
    }

    fun ejecutarBatch(declaracion: PreparedStatement) {
        try {
            declaracion.executeBatch()
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: SQLException) {
            MainServidor.redactarLogServidorln("EXECUTE UPDATE " + declaracion.toString())
            e.printStackTrace()
        }
    }

    fun cerrarDeclaracion(declaracion: PreparedStatement) {
        try {
            declaracion.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun iniciarConexion(): Boolean {
        try {
            Logger.getLogger("com.zaxxer.hikari.pool.PoolBase").setLevel(Level.OFF)
            Logger.getLogger("com.zaxxer.hikari.pool.HikariPool").setLevel(Level.OFF)
            Logger.getLogger("com.zaxxer.hikari.HikariDataSource").setLevel(Level.OFF)
            Logger.getLogger("com.zaxxer.hikari.HikariConfig").setLevel(Level.OFF)
            Logger.getLogger("com.zaxxer.hikari.util.DriverDataSource").setLevel(Level.OFF)
            val configCuentas = HikariConfig()
            configCuentas.setDataSourceClassName("org.mariadb.jdbc.MySQLDataSource")
            configCuentas.addDataSourceProperty("serverName", MainServidor.BD_HOST)
            configCuentas.addDataSourceProperty("port", MainServidor.BD_PUERTO)
            configCuentas.addDataSourceProperty("databaseName", MainServidor.BD_CUENTAS)
            configCuentas.addDataSourceProperty("user", MainServidor.BD_USUARIO)
            configCuentas.addDataSourceProperty("password", MainServidor.BD_PASS)
            configCuentas.addDataSourceProperty("properties", "useUnicode=true;characterEncoding=utf8")
            configCuentas.setAutoCommit(true)
            configCuentas.setMaximumPoolSize(1)
            configCuentas.setMinimumIdle(1)
            configCuentas.setPoolName("Cuentas")
            val configDinamica = HikariConfig()
            configDinamica.setDataSourceClassName("org.mariadb.jdbc.MySQLDataSource")
            configDinamica.addDataSourceProperty("serverName", MainServidor.BD_HOST)
            configDinamica.addDataSourceProperty("port", MainServidor.BD_PUERTO)
            configDinamica.addDataSourceProperty("databaseName", MainServidor.BD_DINAMICA)
            configDinamica.addDataSourceProperty("user", MainServidor.BD_USUARIO)
            configDinamica.addDataSourceProperty("password", MainServidor.BD_PASS)
            configDinamica.addDataSourceProperty("properties", "useUnicode=true;characterEncoding=utf8")
            configDinamica.setAutoCommit(true)
            configDinamica.setMaximumPoolSize(1)
            configDinamica.setMinimumIdle(1)
            configDinamica.setPoolName("Dinamicos")
            val configEstatica = HikariConfig()
            configEstatica.setDataSourceClassName("org.mariadb.jdbc.MySQLDataSource")
            configEstatica.addDataSourceProperty("serverName", MainServidor.BD_HOST)
            configEstatica.addDataSourceProperty("port", MainServidor.BD_PUERTO)
            configEstatica.addDataSourceProperty("databaseName", MainServidor.BD_ESTATICA)
            configEstatica.addDataSourceProperty("user", MainServidor.BD_USUARIO)
            configEstatica.addDataSourceProperty("password", MainServidor.BD_PASS)
            configEstatica.addDataSourceProperty("properties", "useUnicode=true;characterEncoding=utf8")
            configEstatica.setAutoCommit(true)
            configEstatica.setMaximumPoolSize(1)
            configEstatica.setMinimumIdle(1)
            configEstatica.setPoolName("Estaticos")
            estaticos.GestorSQL.cuentas = HikariDataSource(configCuentas)
            estaticos.GestorSQL.dinamcios = HikariDataSource(configDinamica)
            estaticos.GestorSQL.estaticos = HikariDataSource(configEstatica)
            estaticos.GestorSQL._bdCuentas = estaticos.GestorSQL.cuentas.getConnection()
            estaticos.GestorSQL._bdDinamica = estaticos.GestorSQL.dinamcios.getConnection()
            estaticos.GestorSQL._bdEstatica = estaticos.GestorSQL.estaticos.getConnection()
            //solo eso wtff entonces una conexion para todo xD
            if (!estaticos.GestorSQL._bdEstatica.isValid(1000) || !estaticos.GestorSQL._bdDinamica.isValid(1000) || !estaticos.GestorSQL._bdCuentas.isValid(1000)) {
                MainServidor.redactarLogServidorln("SQLError : Conexion a la BDD invalida")
                return false
            }
            return true
        } catch (e: Exception) {
            MainServidor.redactarLogServidorln("ERROR SQL INICIAR CONEXION: " + e.toString())
            e.printStackTrace()
        }
        return false
    }

    fun cerrarConexion() {
        try {
            estaticos.GestorSQL._bdCuentas.close()
            estaticos.GestorSQL._bdDinamica.close()
            estaticos.GestorSQL._bdEstatica.close()
            MainServidor.redactarLogServidorln("########## CONEXIONES SQL CERRADAS ##########")
        } catch (e: Exception) {
            MainServidor.redactarLogServidorln("SQL ERROR CERRAR CONEXION: " + e.toString())
            e.printStackTrace()
        }
    }

    fun ES_IP_BANEADA(ip: String): Boolean {
        var b = false
        val consultaSQL = "SELECT `ip` FROM `banip` WHERE `ip` = '$ip';"
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL(consultaSQL, estaticos.GestorSQL._bdCuentas)
            if (resultado.first()) {
                b = true
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
        return b
    }

    fun ES_UUID_BANEADA(ip: String): Boolean {
        var b = false
        val consultaSQL = "SELECT `uuid` FROM `banuuid` WHERE `uuid` = '$ip';"
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL(consultaSQL, estaticos.GestorSQL._bdCuentas)
            if (resultado.first()) {
                b = true
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
        return b
    }

    fun mapa_evento(subareas: String): Short {
        var b: Short = -1 //SELECT mapas.id FROM mapas INNER JOIN subareas ON mapas.subArea = subareas.id WHERE subareas.area IN (7) order by RAND() limit 1
        val consultaSQL = "SELECT mapas.id FROM mapas INNER JOIN subareas ON mapas.subArea = subareas.id WHERE subareas.area IN ($subareas) order by RAND() limit 1;"
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            if (resultado.first()) {
                b = resultado.getShort("id")
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
        return b
    }

    fun mapa_evento_subarea(subareas: String): String {
        var b = ""
        val consultaSQL = "SELECT GROUP_CONCAT(mapas.id) id FROM mapas INNER JOIN subareas ON mapas.subArea = subareas.id WHERE subareas.id IN ($subareas);"
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            if (resultado.first()) {
                b = resultado.getString("id")
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
        return b
    }

    fun LISTA_BAN_IP(): String {
        val str = StringBuilder()
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT `ip` FROM `banip`;", estaticos.GestorSQL._bdCuentas)
            while (resultado.next()) {
                if (str.length() > 0) {
                    str.append(", ")
                }
                str.append(resultado.getString("ip"))
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
        return str.toString()
    }

    fun INSERT_BAN_UUID(ip: String): Boolean {
        // return true;
        MainServidor.redactarLogServidorln("UUID BANEADA : $ip")
        //GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS("1IP_BAN;" + ip);
        val consultaSQL = "INSERT INTO `banuuid` (`uuid`) VALUES (?);"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdCuentas)
            declaracion.setString(1, ip)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
        return false
    }

    fun INSERT_BAN_IP(ip: String): Boolean {
        // return true;
        MainServidor.redactarLogServidorln("IP BANEADA : $ip")
        GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS("1IP_BAN;$ip")
        val consultaSQL = "INSERT INTO `banip` (`ip`) VALUES (?);"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdCuentas)
            declaracion.setString(1, ip)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
        return false
    }

    fun DELETE_BAN_IP(ip: String?): Boolean {
        val consultaSQL = "DELETE FROM `banip` WHERE `ip` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdCuentas)
            declaracion.setString(1, ip)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
        return false
    }

    /*
	public static void CARGAR_PARAMS_CUSTOM() {
		//PARAMETROS_CONFIG_CUSTOM
		String consultaSQL = "select nombre parametro from parametros_custom;";
		try {
			final ResultSet resultado = consultaSQL(consultaSQL, _bdAlterna);
			while (resultado.next()) {
				MainServidor.PARAMETROS_CONFIG_CUSTOM.add(resultado.getString("parametro"));
			}
			cerrarResultado(resultado);
		} catch (final Exception e) {
			 exceptionNormal(e, "");
		}
	}

	public static void CARGAR_PARAMS_CUSTOM(String ip) {
		//PARAMETROS_CONFIG_CUSTOM
		String consultaSQL = "select nombre parametro from parametros_custom;";
		if(!ip.equalsIgnoreCase("127.0.0.1")) {
			consultaSQL = "select c.nombre parametro from parametros p inner join parametros_custom c on p.parametro=c.id where p.ip= '"+ip+"';";
		}
		try {
			final ResultSet resultado = consultaSQL(consultaSQL, _bdAlterna);
			while (resultado.next()) {
				MainServidor.PARAMETROS_CONFIG.add(resultado.getString("parametro"));
			}
			cerrarResultado(resultado);
		} catch (final Exception e) {
			 exceptionNormal(e, "");
		}
	}*/
    fun GET_VIP(cuenta: String): Byte {
        var b: Byte = 0
        val consultaSQL = "SELECT * FROM `cuentas` WHERE `cuenta` = '$cuenta' ;"
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL(consultaSQL, estaticos.GestorSQL._bdCuentas)
            while (resultado.next()) {
                b = try {
                    resultado.getByte("vip")
                } catch (e: Exception) {
                    1
                }
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            // exceptionNormal(e, "");
        }
        return b
    }

    fun GET_RANGO(cuenta: String): Byte {
        var b: Byte = 0
        val consultaSQL = "SELECT `rango` FROM `cuentas` WHERE `cuenta` = '$cuenta' ;"
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL(consultaSQL, estaticos.GestorSQL._bdCuentas)
            while (resultado.next()) {
                try {
                    b = resultado.getByte("rango")
                } catch (e: Exception) {
                }
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
        return b
    }

    fun GET_RANGO_PERSO(cuenta: Int): Byte {
        var b: Byte = 0
        val consultaSQL = "SELECT `rango` FROM `personajes` WHERE `id` = '$cuenta' ;"
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            while (resultado.next()) {
                try {
                    b = resultado.getByte("rango")
                } catch (e: Exception) {
                }
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
        return b
    }

    // public static String GET_ULTIMA_IP(final String cuenta) {
    // String str = "";
    // String consultaSQL = "SELECT `ultimaIP` FROM `cuentas` WHERE `cuenta` = '" + cuenta + "' ;";
    // try {
    // final ResultSet resultado = consultaSQL(consultaSQL, _bdCuentas);
    // while (resultado.next()) {
    // str = resultado.getString("ultimaIP");
    // }
    // cerrarResultado(resultado);
    // } catch (final Exception e) {
    // Bustemu.redactarLogServidorln("ERROR SQL GET ULTIMA IP: " + e.toString());
    // Bustemu.redactarLogServidorln("LINEA SQL: " + consultaSQL);
    // e.printStackTrace();
    // }
    // return str;
    // }
    fun GET_ID_WEB(cuenta: String): Int {
        var str = -1
        val consultaSQL = "SELECT `idWeb` FROM `cuentas` WHERE `cuenta` = '$cuenta' ;"
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL(consultaSQL, estaticos.GestorSQL._bdCuentas)
            if (resultado.first()) {
                str = resultado.getInt("idWeb")
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
        return str
    }

    fun GET_APODO(cuenta: String): String {
        var str = ""
        val consultaSQL = "SELECT `apodo` FROM `cuentas` WHERE `cuenta` = '$cuenta' ;"
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL(consultaSQL, estaticos.GestorSQL._bdCuentas)
            if (resultado.first()) {
                str = resultado.getString("apodo")
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
        return str
    }

    fun GET_UUID(cuenta: String): String {
        var str = ""
        val consultaSQL = "SELECT `uuid` FROM `cuentas` WHERE `cuenta` = '$cuenta' ;"
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL(consultaSQL, estaticos.GestorSQL._bdCuentas)
            if (resultado.first()) {
                str = resultado.getString("uuid")
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
        return str
    }

    fun GET_NOMBRE(cuenta: String): String {
        var str = ""
        val consultaSQL = "SELECT `nombre` FROM `cuentas` WHERE `cuenta` = '$cuenta' ;"
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL(consultaSQL, estaticos.GestorSQL._bdCuentas)
            if (resultado.first()) {
                str = resultado.getString("nombre")
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
        return str
    }

    fun GET_APELLIDO(cuenta: String): String {
        var str = ""
        val consultaSQL = "SELECT `email` FROM `cuentas` WHERE `cuenta` = '$cuenta' ;"
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL(consultaSQL, estaticos.GestorSQL._bdCuentas)
            if (resultado.first()) {
                str = resultado.getString("email")
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
        return str
    }

    fun GET_CORREO(cuenta: String): String {
        var str = ""
        val consultaSQL = "SELECT `apodo` FROM `cuentas` WHERE `cuenta` = '$cuenta' ;"
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL(consultaSQL, estaticos.GestorSQL._bdCuentas)
            if (resultado.first()) {
                str = resultado.getString("apodo")
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
        return str
    }

    fun GET_ABONO(cuenta: String): Long {
        var l: Long = 0
        val consultaSQL = "SELECT `abono` FROM `cuentas` WHERE `cuenta` = '$cuenta' ;"
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL(consultaSQL, estaticos.GestorSQL._bdCuentas)
            if (resultado.first()) {
                l = resultado.getLong("abono")
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
        return l
    }

    fun SET_ABONO(abono: Long, cuentaID: Int) {
        val consultaSQL = "UPDATE `cuentas` SET `abono`='$abono' WHERE `id`= '$cuentaID'"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdCuentas)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun GET_OGRINAS_CUENTA(cuentaID: Int): Long {
        if (MainServidor.PARAM_NO_USAR_OGRINAS) {
            return 9999999
        }
        var i: Long = 0
        val consultaSQL = "SELECT `ogrinas` FROM `cuentas` WHERE `id` = '$cuentaID' ;"
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL(consultaSQL, estaticos.GestorSQL._bdCuentas)
            if (resultado.first()) {
                i = resultado.getLong("ogrinas")
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
        return i
    }

    fun GET_CREDITOS_CUENTA(cuentaID: Int): Int {
        if (MainServidor.PARAM_NO_USAR_CREDITOS) {
            return 9999999
        }
        var i = 0
        val consultaSQL = "SELECT `creditos` FROM `cuentas` WHERE `id` = '$cuentaID' ;"
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL(consultaSQL, estaticos.GestorSQL._bdCuentas)
            if (resultado.first()) {
                i = resultado.getInt("creditos")
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
        return i
    }

    private fun SET_OGRINAS_CUENTA(monto: Long, cuentaID: Int, perso: Personaje?): Boolean {
        var monto = monto
        if (MainServidor.PARAM_NO_USAR_OGRINAS) {
            return false
        }
        if (monto < 0) {
            monto = 0
        }
        val consultaSQL = "UPDATE `cuentas` SET `ogrinas` = '$monto' WHERE `id` = '$cuentaID'"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdCuentas)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
            return false
        } finally {
            if (MainServidor.MODO_ALL_OGRINAS && perso != null) {
                GestorSalida.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(perso)
            }
        }
        return true
    }

    fun SET_CREDITOS_CUENTA(monto: Int, cuentaID: Int): Boolean {
        var monto = monto
        if (MainServidor.PARAM_NO_USAR_CREDITOS) {
            return false
        }
        if (monto < 0) {
            monto = 0
        }
        val consultaSQL = "UPDATE `cuentas` SET `creditos` = '$monto' WHERE `id` = '$cuentaID'"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdCuentas)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
            return false
        }
        return true
    }

    fun ADD_OGRINAS_CUENTA(ogrinas: Long, cuentaID: Int, perso: Personaje?) {
        if (MainServidor.PARAM_NO_USAR_OGRINAS) {
            return
        }
        val exOgrinas: Long = estaticos.GestorSQL.GET_OGRINAS_CUENTA(cuentaID)
        estaticos.GestorSQL.SET_OGRINAS_CUENTA(ogrinas + exOgrinas, cuentaID, perso)
        if (perso != null) {
            perso.setOgGanadas(perso.getOgGanadas() + ogrinas)
            Sucess.launch(perso, 8.toByte(), null, 0)
        }
    }

    fun ADD_CREDITOS_CUENTA(creditos: Int, cuentaID: Int) {
        if (MainServidor.PARAM_NO_USAR_CREDITOS) {
            return
        }
        val exCreditos: Int = estaticos.GestorSQL.GET_CREDITOS_CUENTA(cuentaID)
        estaticos.GestorSQL.SET_CREDITOS_CUENTA(creditos + exCreditos, cuentaID)
    }

    fun RESTAR_OGRINAS(cuenta: Cuenta, restar: Long, perso: Personaje?) {
        var restar = restar
        try {
            val ogrinas: Long = estaticos.GestorSQL.GET_OGRINAS_CUENTA(cuenta.getID())
            if (ogrinas < restar) {
                restar = ogrinas
            }
            if (!estaticos.GestorSQL.SET_OGRINAS_CUENTA(ogrinas - restar, cuenta.getID(), null)) {
                return
            }
        } catch (e: Exception) {
        }
        return
    }

    fun RESTAR_OGRINAS1(cuenta: Cuenta, restar: Long, perso: Personaje?): Boolean {
        if (MainServidor.PARAM_NO_USAR_OGRINAS) {
            return true
        }
        var resultado = false
        try {
            val ogrinas: Long = estaticos.GestorSQL.GET_OGRINAS_CUENTA(cuenta.getID())
            if (restar <= 0 || ogrinas < restar) {
                GestorSalida.ENVIAR_Im_INFORMACION(perso, "1ERROR_BUY_WITH_OGRINES;" + (restar - ogrinas))
                return false
            }
            if (!estaticos.GestorSQL.SET_OGRINAS_CUENTA(ogrinas - restar, cuenta.getID(), perso)) {
                return false
            }
            resultado = true
            GestorSalida.ENVIAR_Im_INFORMACION(perso, "1EXITO_BUY_WITH_OGRINES;" + (ogrinas - restar) + "~"
                    + MainServidor.NOMBRE_SERVER)
            if (perso != null) {
                perso.setOgGastadas(perso.getOgGastadas() + restar)
                Sucess.launch(perso, 9.toByte(), null, 0)
            }
        } catch (e: Exception) {
        }
        return resultado
    }

    fun RESTAR_CREDITOS(cuenta: Cuenta, restar: Int, perso: Personaje?): Boolean {
        if (MainServidor.PARAM_NO_USAR_CREDITOS) {
            return true
        }
        var resultado = false
        try {
            val creditos: Int = estaticos.GestorSQL.GET_CREDITOS_CUENTA(cuenta.getID())
            if (restar <= 0 || creditos < restar) {
                GestorSalida.ENVIAR_Im_INFORMACION(perso, "1ERROR_BUY_WITH_CREDITS;" + (restar - creditos))
                return false
            }
            if (!estaticos.GestorSQL.SET_CREDITOS_CUENTA(creditos - restar, cuenta.getID())) {
                return false
            }
            resultado = true
            GestorSalida.ENVIAR_Im_INFORMACION(perso, "1EXITO_BUY_WITH_CREDITS;" + (creditos - restar) + "~"
                    + MainServidor.NOMBRE_SERVER)
        } catch (e: Exception) {
        }
        return resultado
    }

    fun GET_CONTRASEÑA_CUENTA(cuenta: String): String {
        var str = ""
        val consultaSQL = "SELECT `contraseña` FROM `cuentas` WHERE `cuenta` = '$cuenta' ;"
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL(consultaSQL, estaticos.GestorSQL._bdCuentas)
            if (resultado.first()) {
                try {
                    str = resultado.getString("contraseña")
                } catch (e: Exception) {
                }
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
        return str
    }

    fun CAMBIAR_CONTRASEÑA_CUENTA(contraseña: String?, cuentaID: Int) {
        val consultaSQL = "UPDATE `cuentas` SET `contraseña`= ? WHERE `id`= ?"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdCuentas)
            declaracion.setString(1, contraseña)
            declaracion.setInt(2, cuentaID)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun GET_PREGUNTA_SECRETA(cuenta: String): String {
        var str = ""
        val consultaSQL = "SELECT `pregunta` FROM `cuentas` WHERE `cuenta` = '$cuenta' ;"
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL(consultaSQL, estaticos.GestorSQL._bdCuentas)
            if (resultado.first()) {
                try {
                    str = resultado.getString("pregunta")
                } catch (e: Exception) {
                }
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
        return str
    }

    fun GET_BANEADO(cuenta: String): Long {
        var i: Long = 0
        try {
            val consultaSQL = "SELECT `baneado` FROM `cuentas` WHERE `cuenta` = '$cuenta' ;"
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL(consultaSQL, estaticos.GestorSQL._bdCuentas)
            if (resultado.first()) {
                i = resultado.getLong("baneado")
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
        return i
    }

    fun GET_RESPUESTA_SECRETA(cuenta: String): String {
        var str = ""
        val consultaSQL = "SELECT `respuesta` FROM `cuentas` WHERE `cuenta` = '$cuenta' ;"
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL(consultaSQL, estaticos.GestorSQL._bdCuentas)
            if (resultado.first()) {
                try {
                    str = resultado.getString("respuesta")
                } catch (e: Exception) {
                }
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
        return str
    }

    fun SET_RANGO(cuenta: String?, rango: Int) {
        val consultaSQL = "UPDATE `cuentas` SET `rango` = ? WHERE `cuenta` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdCuentas)
            declaracion.setInt(1, rango)
            declaracion.setString(2, cuenta)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun SET_RANGO_PERSO(cuenta: Int, rango: Int) {
        val consultaSQL = "UPDATE `personajes` SET `rango` = ? WHERE `id` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setInt(1, rango)
            declaracion.setInt(2, cuenta)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    // public static void SET_ULTIMA_IP(final String cuenta, final String ip) {
    // String consultaSQL = "UPDATE `cuentas` SET `ultimaIP` = ? WHERE `cuenta` = ?;";
    // try {
    // final PreparedStatement declaracion = transaccionSQL(consultaSQL, _bdCuentas);
    // declaracion.setString(1, ip);
    // declaracion.setString(2, cuenta);
    // ejecutarTransaccion(declaracion);
    // cerrarDeclaracion(declaracion);
    // } catch (final Exception e) {
    // Bustemu.redactarLogServidorln("ERROR SQL SET ULTIMA IP: " + e.toString());
    // Bustemu.redactarLogServidorln("LINEA SQL: " + consultaSQL);
    // e.printStackTrace();
    // }
    // }
    fun SET_BANEADO(cuenta: String, baneado: Long) {
        val consultaSQL = "UPDATE `cuentas` SET `baneado` = '$baneado' WHERE `cuenta` = '$cuenta';"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdCuentas)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun GET_CUENTAS_CONECTADAS_IP(ip: String): Int {
        var i = 0
        val consultaSQL = "SELECT * FROM `cuentas` WHERE `ultimaIP` = '$ip' AND `logeado` = 1 ;"
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL(consultaSQL, estaticos.GestorSQL._bdCuentas)
            while (resultado.next()) {
                i++
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
        return i
    }

    fun UPDATE_CUENTA_LOGUEADO(cuentaID: Int, log: Byte) {
        val consultaSQL = "UPDATE `cuentas` SET `logeado`= $log WHERE `id`=$cuentaID;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdCuentas)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    //
    // public static void UPDATE_TODAS_CUENTAS_CERO() {
    // final String cuentas = Mundo.strCuentasOnline();
    // if (cuentas.isEmpty()) {
    // return;
    // }
    // String consultaSQL = "UPDATE `cuentas` SET `logeado`= 0 WHERE `id` IN (" + cuentas + ");";
    // try {
    // final PreparedStatement declaracion = transaccionSQL(consultaSQL, _bdCuentas);
    // ejecutarTransaccion(declaracion);
    // cerrarDeclaracion(declaracion);
    // } catch (final Exception e) {
    // exceptionModify(e, consultaSQL, "");
    // }
    // }
    fun CARGAR_CUENTA_POR_ID(id: Int) {
        val consultaSQL = "SELECT * FROM `cuentas` WHERE `id` = $id;"
        try {
            if (Mundo.getCuenta(id) != null) {
                return
            }
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL(consultaSQL, estaticos.GestorSQL._bdCuentas)
            if (resultado.first()) {
                val cuenta = Cuenta(resultado.getInt("id"), resultado.getString("cuenta"))
                Mundo.addCuenta(cuenta)
                estaticos.GestorSQL.REPLACE_CUENTA_SERVIDOR(cuenta, 1.toByte())
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
    }

    fun CARGAR_DB_CUENTAS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `cuentas`;", estaticos.GestorSQL._bdCuentas)
            while (resultado.next()) {
                val cuenta = Cuenta(resultado.getInt("id"), resultado.getString("cuenta"))
                Mundo.addCuenta(cuenta)
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun GET_REGALO(cuenta: String): String {
        var str = ""
        val consultaSQL = "SELECT `regalo` FROM `cuentas_servidor` WHERE `cuenta` = '$cuenta' ;"
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            if (resultado.first()) {
                try {
                    str = resultado.getString("regalo")
                } catch (e: Exception) {
                }
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
        return str
    }

    fun GET_LOGIN_PREMIO(cuenta: String): String {
        var str = "1,1,0"
        val consultaSQL = "SELECT `loginPremio` FROM `cuentas_servidor` WHERE `cuenta` = '$cuenta' ;"
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            if (resultado.first()) {
                try {
                    str = resultado.getString("loginPremio")
                } catch (e: Exception) {
                }
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
        return str
    }

    fun CARGAR_CAPTCHAS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `captchas`;", estaticos.GestorSQL._bdCuentas)
            while (resultado.next()) {
                Mundo.CAPTCHAS.add(resultado.getString("captcha").toString() + "|" + resultado.getString("respuesta"))
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
        }
    }

    fun GET_ULTIMO_SEGUNDOS_VOTO(ip: String, cuentaID: Int): Long {
        var time: Long = 0
        var consultaSQL = ("SELECT `ultimoVoto` FROM `cuentas` WHERE `ultimaIP` = '" + ip
                + "' ORDER BY `ultimoVoto` DESC ;")
        try {
            var resultado: ResultSet = estaticos.GestorSQL.consultaSQL(consultaSQL, estaticos.GestorSQL._bdCuentas)
            while (resultado.next()) {
                try {
                    if (resultado.getString("ultimoVoto").isEmpty()) {
                        continue
                    }
                    time = resultado.getLong("ultimoVoto")
                    break
                } catch (e: Exception) {
                }
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
            consultaSQL = "SELECT `ultimoVoto` FROM `cuentas` WHERE `id` = '$cuentaID' ;"
            resultado = estaticos.GestorSQL.consultaSQL(consultaSQL, estaticos.GestorSQL._bdCuentas)
            while (resultado.next()) {
                if (resultado.getString("ultimoVoto").isEmpty()) {
                    continue
                }
                time = Math.max(time, resultado.getLong("ultimoVoto"))
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
        return time
    }

    fun GET_VOTOS(cuenta: String): Int {
        var i = 0
        val consultaSQL = "SELECT `votos` FROM `cuentas` WHERE `cuenta` = '$cuenta' ;"
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL(consultaSQL, estaticos.GestorSQL._bdCuentas)
            if (resultado.first()) {
                try {
                    i = resultado.getInt("votos")
                } catch (e: Exception) {
                }
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
        return i
    }

    fun SET_ULTIMO_SEGUNDOS_VOTO(cuentaID: Int, ip: String?, time: Long) {
        val consultaSQL = "UPDATE `cuentas` SET `ultimoVoto` = ? WHERE `id` = ? OR `ultimaIP` = ? ;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdCuentas)
            declaracion.setLong(1, time)
            declaracion.setInt(2, cuentaID)
            declaracion.setString(3, ip)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun SET_VOTOS(cuentaID: Int, votos: Int) {
        val consultaSQL = "UPDATE `cuentas` SET `votos` = ? WHERE `id` = ? ;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdCuentas)
            declaracion.setInt(1, votos)
            declaracion.setInt(2, cuentaID)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun CARGAR_CUENTAS_SERVER_PERSONAJE() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `cuentas_servidor`;", estaticos.GestorSQL._bdDinamica)
            while (resultado.next()) {
                try {
                    Mundo.getCuenta(resultado.getInt("id")).cargarInfoServerPersonaje(resultado.getString("objetos"), resultado
                            .getLong("kamas"), resultado.getString("amigos"), resultado.getString("enemigos"), resultado.getString(
                            "establo"), resultado.getString("reportes"), resultado.getString("ultimaConexion"), resultado.getString(
                            "mensajes"), resultado.getString("ultimaIP"))
                } catch (e: Exception) {
                }
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
    }

    fun GET_PRIMERA_VEZ(cuenta: String): Byte {
        var b: Byte = 1
        val consultaSQL = "SELECT `primeraVez` FROM `cuentas_servidor` WHERE `cuenta` = '$cuenta' ;"
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            if (resultado.first()) {
                try {
                    b = resultado.getByte("primeraVez")
                } catch (e: Exception) {
                }
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
        return b
    }

    fun SET_PRIMERA_VEZ_CERO(cuenta: String?) {
        val consultaSQL = "UPDATE `cuentas_servidor` SET `primeraVez` = 0 WHERE `cuenta` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setString(1, cuenta)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun SET_REGALO(cuenta: String?, regalo: String?) {
        val consultaSQL = "UPDATE `cuentas_servidor` SET `regalo` = ? WHERE `cuenta` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setString(1, regalo)
            declaracion.setString(2, cuenta)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun SET_LOGIN_PREMIO(cuenta: String?, premio: String?) {
        val consultaSQL = "UPDATE `cuentas_servidor` SET `loginPremio` = ? WHERE `cuenta` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setString(1, premio)
            declaracion.setString(2, cuenta)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun UPDATE_MENSAJES_CUENTA(cuenta: String?, mensajes: String?) {
        val consultaSQL = "UPDATE `cuentas_servidor` SET `mensajes` = ? WHERE `cuenta` = ? ;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setString(1, mensajes)
            declaracion.setString(2, cuenta)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun REPLACE_CUENTA_SERVIDOR(cuenta: Cuenta, primeraVez: Byte) {
        val consultaSQL = "REPLACE INTO `cuentas_servidor` VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?);"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setInt(1, cuenta.getID())
            declaracion.setString(2, cuenta.getNombre())
            declaracion.setLong(3, cuenta.getKamasBanco())
            declaracion.setString(4, cuenta.stringBancoObjetosBD())
            declaracion.setString(5, cuenta.stringIDsEstablo())
            declaracion.setString(6, cuenta.analizarListaAmigosABD())
            declaracion.setString(7, cuenta.stringListaEnemigosABD())
            declaracion.setString(8, cuenta.listaReportes())
            declaracion.setByte(9, primeraVez)
            declaracion.setString(10, cuenta.getRegalo())
            declaracion.setString(11, cuenta.getUltimaConexion())
            declaracion.setString(12, cuenta.getActualIP())
            declaracion.setString(13, cuenta.stringMensajes())
            //declaracion.setString(14, cuenta.getLoginPremio());
            //cuenta.setUltimaIP(cuenta.getActualIP());
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            estaticos.GestorSQL.SALVAR_OBJETOS(cuenta.getObjetosBanco())
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    //
    // public static ArrayList<Personaje> GET_RANKING_NIVEL() {
    // final ArrayList<Personaje> persos = new ArrayList<>();
    // String consultaSQL = "SELECT `id` FROM `personajes` ORDER BY `xp` DESC LIMIT " +
    // Bustemu.LIMITE_LADDER + ";";
    // try {
    // final ResultSet resultado = consultaSQL(consultaSQL, _bdDinamica);
    // while (resultado.next()) {
    // try {
    // persos.add(Mundo.getPersonaje(Integer.parseInt(resultado.getString("id"))));
    // } catch (final Exception e) {}
    // }
    // cerrarResultado(resultado);
    // } catch (final Exception e) {
    // Bustemu.redactarLogServidorln("ERROR SQL: " + e.toString());
    // e.printStackTrace();
    // }
    // return persos;
    // }
    //
    // public static ArrayList<Gremio> GET_RANKING_GREMIOS() {
    // final ArrayList<Gremio> gremios = new ArrayList<>();
    // String consultaSQL = "SELECT `id` FROM `gremios` ORDER BY `xp` DESC LIMIT " +
    // Bustemu.LIMITE_LADDER + ";";
    // try {
    // final ResultSet resultado = consultaSQL(consultaSQL, _bdDinamica);
    // while (resultado.next()) {
    // try {
    // gremios.add(Mundo.getGremio(Integer.parseInt(resultado.getString("id"))));
    // } catch (final Exception e) {}
    // }
    // cerrarResultado(resultado);
    // } catch (final Exception e) {
    // Bustemu.redactarLogServidorln("ERROR SQL: " + e.toString());
    // e.printStackTrace();
    // }
    // return gremios;
    // }
    //
    // public static ArrayList<Personaje> GET_RANKING_PVP() {
    // final ArrayList<Personaje> persos = new ArrayList<>();
    // String consultaSQL =
    // "SELECT `id` FROM `ranking_pvp` ORDER BY `victorias` DESC, `derrotas`ASC LIMIT "
    // + Bustemu.LIMITE_LADDER + ";";
    // try {
    // final ResultSet resultado = consultaSQL(consultaSQL, _bdDinamica);
    // while (resultado.next()) {
    // try {
    // persos.add(Mundo.getPersonaje(resultado.getInt("id")));
    // } catch (final Exception e) {}
    // }
    // cerrarResultado(resultado);
    // } catch (final Exception e) {
    // Bustemu.redactarLogServidorln("ERROR SQL: " + e.toString());
    // e.printStackTrace();
    // }
    // return persos;
    // }
    //
    // public static ArrayList<Personaje> GET_RANKING_KOLISEO() {
    // final ArrayList<Personaje> persos = new ArrayList<>();
    // String consultaSQL =
    // "SELECT `id` FROM `ranking_koliseo` ORDER BY `victorias` DESC, `derrotas`ASC LIMIT "
    // + Bustemu.LIMITE_LADDER + ";";
    // try {
    // final ResultSet resultado = consultaSQL(consultaSQL, _bdDinamica);
    // while (resultado.next()) {
    // try {
    // persos.add(Mundo.getPersonaje(resultado.getInt("id")));
    // } catch (final Exception e) {}
    // }
    // cerrarResultado(resultado);
    // } catch (final Exception e) {
    // Bustemu.redactarLogServidorln("ERROR SQL: " + e.toString());
    // e.printStackTrace();
    // }
    // return persos;
    // }
    //
    fun GET_SIG_ID_OBJETO(): Int {
        var id = 1
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT MAX(id) AS max FROM `objetos`;", estaticos.GestorSQL._bdDinamica)
            if (resultado.first()) {
                id = resultado.getInt("max")
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
            return id
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
        return id
    }

    fun CARGAR_RECETAS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `recetas`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                val arrayDuos: ArrayList<Duo<Integer, Integer>> = ArrayList<Duo<Integer, Integer>>()
                var continua = false
                val idReceta: Int = resultado.getInt("id")
                val receta: String = resultado.getString("receta")
                for (str in receta.split(";")) {
                    continua = try {
                        val s: Array<String> = str.split(Pattern.quote(","))
                        val idModeloObj: Int = Integer.parseInt(s[0])
                        val cantidad: Int = Integer.parseInt(s[1])
                        arrayDuos.add(Duo<Integer, Integer>(idModeloObj, cantidad))
                        true
                    } catch (e: Exception) {
                        false
                    }
                }
                if (continua) {
                    Mundo.addReceta(idReceta, arrayDuos)
                }
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_DROPS(): Int {
        var numero = 0
        try {
            if (!MainServidor.PARAM_SISTEMA_ORBES) {
                val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `drops`;", estaticos.GestorSQL._bdEstatica)
                while (resultado.next()) {
                    if (Mundo.getObjetoModelo(resultado.getInt("objeto")) == null) {
                        continue
                    }
                    val drop = DropMob(resultado.getInt("objeto"), resultado.getInt("prospeccion"), resultado.getFloat(
                            "porcentaje"), resultado.getInt("max"), resultado.getString("condicion"), resultado.getBoolean("raro"))
                    try {
                        Mundo.getMobModelo(resultado.getInt("mob")).addDrop(drop)
                        numero++
                    } catch (e: Exception) {
                        System.out.println("El drop " + drop.getIDObjModelo().toString() + " tiene bug por favor solucionarlo por mob: " + resultado.getInt("mob"))
                    }
                }
                estaticos.GestorSQL.cerrarResultado(resultado)
            }
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
        return numero
    }

    fun CARGAR_DROPS_FIJOS(): Int {
        var numero = 0
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `drops_fijos`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                if (Mundo.getObjetoModelo(resultado.getInt("objeto")) == null) {
                    continue
                }
                Mundo.addDropFijo(DropMob(resultado.getInt("objeto"), resultado.getFloat("porcentaje"), resultado.getInt(
                        "nivelMin"), resultado.getInt("nivelMax")))
                numero++
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
        return numero
    }

    fun SELECT_ZONAS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM zonas;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                Mundo.ZONAS.put(resultado.getShort("mapa"), resultado.getShort("celda"))
                Mundo.LISTA_ZONAS += "|" + resultado.getString("nombre").toString() + ";" + resultado.getShort("mapa")
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    /*public static void SELECT_LOGIN_REGALO() {
		try {
			final ResultSet resultado = consultaSQL("SELECT * from login_premio;", _bdEstatica);
			System.out.print("Cargando Regalos al iniciar: ");
			while (resultado.next()) {
				LoginPremio premio = new LoginPremio(resultado.getInt("etapa"), resultado.getInt("dia"), resultado.getInt("premio"));
				Mundo.LOGINPREMIOS.put(premio.toString(), premio);
			}
			System.out.println(Mundo.LOGINPREMIOS.size() + " premios etapas cargados");
			cerrarResultado(resultado);
		} catch (final Exception e) {
			//System.out.println(e.getMessage());
		}
	}*/
    fun CARGAR_FUSION() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * from fusion_modelo;", estaticos.GestorSQL._bdEstatica)
            System.out.print("Cargando Fusiones: ")
            while (resultado.next()) {
                val master = Fusion(resultado.getInt("id"), resultado.getInt("dar"), resultado.getInt("entregar_1"), resultado.getInt("entregar_2"))
                Mundo.FUSIONES.add(master)
            }
            System.out.println(Mundo.MASTER_RESET.size().toString() + " master reset cargados")
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            //System.out.println(e.getMessage());
        }
    }

    fun CARGAR_EPICITEMS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * from epic_items;", estaticos.GestorSQL._bdEstatica)
            System.out.print("Cargando EpicItems: ")
            while (resultado.next()) {
                val craft = EpicItems(resultado.getInt("base"), resultado.getInt("dar"))
                Mundo.EPICITEMS.put(craft.getId(), craft)
            }
            System.out.println(Mundo.EPICITEMS.size().toString() + " EpicItems cargados")
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            System.out.println(e.getMessage())
        }
    }

    fun CARGAR_CRAFEO() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * from crafeo;", estaticos.GestorSQL._bdEstatica)
            System.out.print("Cargando Crafeos: ")
            while (resultado.next()) {
                val craft = Crafeo(resultado.getInt("base"), resultado.getString("stats"), resultado.getInt("cant"))
                Mundo.CRAFEOS.put(craft.getBase(), craft)
            }
            System.out.println(Mundo.CRAFEOS.size().toString() + " crafeos cargados")
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            System.out.println(e.getMessage())
        }
    }

    fun CARGAR_MASTER_RESET() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * from master_reset;", estaticos.GestorSQL._bdEstatica)
            System.out.print("Cargando Master reset: ")
            while (resultado.next()) {
                val master = MasterReset(resultado.getInt("id"), resultado.getInt("nivel"), resultado.getString("sufijo"), resultado.getInt("capital"), resultado.getInt("hechizos"), resultado.getBoolean("reinicia"))
                Mundo.addMasterReset(master)
            }
            System.out.println(Mundo.MASTER_RESET.size().toString() + " master reset cargados")
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            //System.out.println(e.getMessage());
        }
    }

    fun CARGAR_OBJETOS_SETS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM objetos_set;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                val set = ObjetoSet(resultado.getInt("id"), resultado.getString("nombre"), resultado.getString(
                        "objetos"))
                for (i in 2..8) {
                    set.setStats(resultado.getString(i.toString() + "_objetos"), i)
                }
                Mundo.addObjetoSet(set)
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_CERCADOS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `cercados_modelo`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                val mapa: Mapa = Mundo.getMapa(resultado.getShort("mapa")) ?: continue
                val cercado = Cercado(mapa, resultado.getByte("capacidad"), resultado.getByte("objetos"),
                        resultado.getShort("celdaPJ"), resultado.getShort("celdaPuerta"), resultado.getShort("celdaMontura"), resultado
                        .getString("celdasObjetos"), resultado.getInt("precioOriginal"))
                Mundo.addCercado(cercado)
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_OFICIOS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `oficios`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                Mundo.addOficio(Oficio(resultado.getInt("id"), resultado.getString(
                        "recetas")))
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_SERVICIOS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `servicios`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                Mundo.addServicio(Servicio(resultado.getInt("id"), resultado.getInt("creditos"), resultado.getInt(
                        "ogrinas"), resultado.getBoolean("activado"), resultado.getInt("creditosVIP"), resultado.getInt("ogrinasVIP")))
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_ENCARNACIONES_MODELOS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `encarnaciones_modelo`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                Mundo.addEncarnacionModelo(EncarnacionModelo(resultado.getInt("gfx"), resultado.getString("statsFijos"),
                        resultado.getString("statsPorNivel"), resultado.getString("hechizos")))
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_CLASES() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `clases`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                val clase = Clase(resultado.getInt("id"), resultado.getString("gfxs"), resultado.getString("tallas"),
                        resultado.getShort("mapaInicio"), resultado.getShort("celdaInicio"), resultado.getInt("PDV"), resultado
                        .getString("boostVitalidad"), resultado.getString("boostSabiduria"), resultado.getString("boostFuerza"),
                        resultado.getString("boostInteligencia"), resultado.getString("boostAgilidad"), resultado.getString(
                        "boostSuerte"), resultado.getString("statsInicio"), resultado.getString("hechizos"), resultado.getInt("craneo"))
                Mundo.CLASES.put(clase.getID(), clase)
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_CREA_OBJETOS_MODELOS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `crear_objetos_modelos`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                val creaTuItem = CreaTuItem(resultado.getInt("id"), resultado.getString("statsMaximos"), resultado
                        .getInt("limiteOgrinas"), resultado.getInt("precioBase"))
                Mundo.CREA_TU_ITEM.put(creaTuItem.getID(), creaTuItem)
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_CREA_OBJETOS_PRECIOS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `crear_objetos_stats`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                if (!Mundo.CREAT_TU_ITEM_PRECIOS.isEmpty()) {
                    Mundo.CREAT_TU_ITEM_PRECIOS += ";"
                }
                val stat: Int = resultado.getInt("id")
                val ogrinas: Float = resultado.getFloat("ogrinas")
                val nombre: String = resultado.getString("nombre") //prueba y esto porque se borro? ni idea no he tocado el gestor
                Mundo.CREAT_TU_ITEM_PRECIOS += "$stat,$ogrinas"
                CreaTuItem.PRECIOS.put(stat, ogrinas)
                Mundo.STATS_NOMBRE.put(stat, nombre)
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_AREA() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `areas`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                val area = Area(resultado.getShort("id"), resultado.getShort("superarea"), resultado.getString(
                        "nombre"))
                Mundo.addArea(area)
                area.getSuperArea().addArea(area)
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_SUBAREA() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `subareas`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                val subarea = SubArea(resultado.getShort("id"), resultado.getShort("area"), resultado.getString(
                        "nombre"), resultado.getInt("conquistable") === 1, resultado.getInt("minNivelGrupoMob"), resultado.getInt(
                        "maxNivelGrupoMob"), resultado.getString("cementerio"))
                Mundo.addSubArea(subarea)
                if (subarea.getArea() != null) {
                    subarea.getArea().addSubArea(subarea)
                }
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_NPCS(): Int {
        var numero = 0
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `npcs_ubicacion`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                try {
                    val npcModelo: NPCModelo = Mundo.getNPCModelo(resultado.getInt("npc"))
                    if (npcModelo == null) {
                        estaticos.GestorSQL.DELETE_NPC_UBICACION(resultado.getInt("npc"))
                        continue
                    }
                    Mundo.getMapa(resultado.getShort("mapa")).addNPC(npcModelo, resultado.getShort("celda"), resultado.getByte(
                            "orientacion"), resultado.getString("condicion"))
                    numero++
                } catch (e: Exception) {
                }
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
        return numero
    }

    fun CARGAR_CASAS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `casas_modelo`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                if (Mundo.getMapa(resultado.getShort("mapaFuera")) == null) {
                    continue
                }
                Mundo.addCasa(Casa(resultado.getInt("id"), resultado.getShort("mapaFuera"), resultado.getShort(
                        "celdaFuera"), resultado.getShort("mapaDentro"), resultado.getShort("celdaDentro"), resultado.getLong("precio"),
                        resultado.getString("mapasContenidos")))
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun RECARGAR_CASAS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `casas`;", estaticos.GestorSQL._bdDinamica)
            while (resultado.next()) {
                try {
                    Mundo.getCasa(resultado.getInt("id")).actualizarCasa(resultado.getInt("dueño"), resultado.getInt("precio"),
                            resultado.getByte("bloqueado"), resultado.getString("clave"), resultado.getInt("derechosGremio"))
                } catch (e: Exception) {
                }
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
    }

    fun CARGAR_COFRES() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `cofres_modelo`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                Mundo.addCofre(Cofre(resultado.getInt("id"), resultado.getInt("casa"), resultado.getShort("mapa"), resultado
                        .getShort("celda"), MainServidor.LIMITE_OBJETOS_COFRE))
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_EXPERIENCIA() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `experiencia`;", estaticos.GestorSQL._bdEstatica)
            var maxAlineacion = 0
            var maxPersonaje = 0
            var maxGremio = 0
            var maxOficio = 0
            var maxMontura = 0
            var maxEncarnacion = 0
            var maxNivelOmega = 0
            while (resultado.next()) {
                val nivel: Int = resultado.getInt("nivel")
                val exp = Experiencia(resultado.getString("personaje"), resultado.getInt("oficio"), resultado.getInt(
                        "dragopavo"), resultado.getLong("gremio"), resultado.getInt("pvp"), resultado.getInt("encarnacion"), resultado.getString("omega"))
                if (exp._alineacion > 0) {
                    maxAlineacion = Math.max(maxAlineacion, nivel)
                }
                if (BigInteger(exp._personaje).compareTo(BigInteger.ZERO) === 1) {
                    maxPersonaje = Math.max(maxPersonaje, nivel)
                }
                if (exp._gremio > 0) {
                    maxGremio = Math.max(maxGremio, nivel)
                }
                if (exp._oficio > 0) {
                    maxOficio = Math.max(maxOficio, nivel)
                }
                if (exp._montura > 0) {
                    maxMontura = Math.max(maxMontura, nivel)
                }
                if (exp._encarnacion > 0) {
                    maxEncarnacion = Math.max(maxEncarnacion, nivel)
                }
                if (exp._omega.compareTo(BigInteger.ZERO) === 1) {
                    maxNivelOmega = Math.max(maxNivelOmega, nivel)
                }
                Mundo.addExpNivel(nivel, exp)
            }
            if (MainServidor.NIVEL_MAX_PERSONAJE <= 0) {
                MainServidor.NIVEL_MAX_PERSONAJE = maxPersonaje
            }
            if (MainServidor.NIVEL_MAX_ALINEACION <= 0) {
                MainServidor.NIVEL_MAX_ALINEACION = maxAlineacion
            }
            if (MainServidor.NIVEL_MAX_GREMIO <= 0) {
                MainServidor.NIVEL_MAX_GREMIO = maxGremio
            }
            if (MainServidor.NIVEL_MAX_OFICIO <= 0) {
                MainServidor.NIVEL_MAX_OFICIO = maxOficio
            }
            if (MainServidor.NIVEL_MAX_MONTURA <= 0) {
                MainServidor.NIVEL_MAX_MONTURA = maxMontura
            }
            if (MainServidor.NIVEL_MAX_ENCARNACION <= 0) {
                MainServidor.NIVEL_MAX_ENCARNACION = maxEncarnacion
            }
            MainServidor.NIVEL_MAX_OMEGA = maxNivelOmega
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_TRIGGERS(): Int {
        var RS: ResultSet? = null
        var numero = 0
        try {
            RS = estaticos.GestorSQL.consultaSQL("SELECT * FROM `celdas_accion`;", estaticos.GestorSQL._bdEstatica)
            while (RS.next()) {
                val mapa: Mapa = Mundo.getMapaSQL(RS.getShort("mapa"))
                if (mapa == null || mapa.getCelda(RS.getShort("celda")) == null) continue
                val accion: Int = RS.getInt("accion")
                val celda: Int = RS.getInt("celda")
                if (accion == 0) if (mapa.celdasTPs.toString().equals("")) mapa.celdasTPs.append(celda) else mapa.celdasTPs.append(",$celda")
                mapa.getCelda(RS.getShort("celda")).addAccion(RS.getInt("accion"), RS.getString("args"),
                        RS.getString("condicion"))
                numero++
            }
            estaticos.GestorSQL.cerrarResultado(RS)
            return numero
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
        return numero
    }

    fun CARGAR_MOBS_EVENTO() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `mobs_evento`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                Mundo.addMobEvento(resultado.getByte("evento"), resultado.getInt("mobOriginal"), resultado.getInt("mobEvento"))
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun ACTUALIZAR_PERSONAJE_CARCEL(perso: Personaje) {
        val consultaSQL = "UPDATE `personajes` SET `carcel` = ? WHERE `id` = ? ;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setInt(1, if (perso.getCalabozo()) 1 else 0)
            declaracion.setInt(2, perso.getID())
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun CARGAR_PERSONAJES_ID(id: Int) {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `personajes` WHERE `cuenta`=$id;", estaticos.GestorSQL._bdDinamica)
            while (resultado.next()) {
                val statsBase: TreeMap<Integer, Integer> = TreeMap<Integer, Integer>()
                statsBase.put(Constantes.STAT_MAS_VITALIDAD, resultado.getInt("vitalidad"))
                statsBase.put(Constantes.STAT_MAS_FUERZA, resultado.getInt("fuerza"))
                statsBase.put(Constantes.STAT_MAS_SABIDURIA, resultado.getInt("sabiduria"))
                statsBase.put(Constantes.STAT_MAS_INTELIGENCIA, resultado.getInt("inteligencia"))
                statsBase.put(Constantes.STAT_MAS_SUERTE, resultado.getInt("suerte"))
                statsBase.put(Constantes.STAT_MAS_AGILIDAD, resultado.getInt("agilidad"))
                val statsScroll: TreeMap<Integer, Integer> = TreeMap<Integer, Integer>()
                statsScroll.put(Constantes.STAT_MAS_VITALIDAD, resultado.getInt("sVitalidad"))
                statsScroll.put(Constantes.STAT_MAS_FUERZA, resultado.getInt("sFuerza"))
                statsScroll.put(Constantes.STAT_MAS_SABIDURIA, resultado.getInt("sSabiduria"))
                statsScroll.put(Constantes.STAT_MAS_INTELIGENCIA, resultado.getInt("sInteligencia"))
                statsScroll.put(Constantes.STAT_MAS_SUERTE, resultado.getInt("sSuerte"))
                statsScroll.put(Constantes.STAT_MAS_AGILIDAD, resultado.getInt("sAgilidad"))
                val perso = Personaje(resultado.getInt("id"), resultado.getString("nombre"), resultado.getByte(
                        "sexo"), resultado.getByte("clase"), resultado.getInt("color1"), resultado.getInt("color2"), resultado.getInt(
                        "color3"), resultado.getLong("kamas"), resultado.getInt("puntosHechizo"), resultado.getInt("capital"), resultado
                        .getInt("energia"), resultado.getShort("nivel"), resultado.getString("xp"), resultado.getInt("talla"), resultado
                        .getInt("gfx"), resultado.getByte("alineacion"), resultado.getInt("cuenta"), statsBase, statsScroll, resultado
                        .getInt("mostrarAmigos") === 1, resultado.getByte("mostrarAlineacion") === 1, resultado.getString("canal"),
                        resultado.getShort("mapa"), resultado.getShort("celda"), resultado.getString("objetos"), resultado.getByte(
                        "porcVida"), resultado.getString("hechizos"), resultado.getString("posSalvada"), resultado.getString("oficios"),
                        resultado.getByte("xpMontura"), resultado.getInt("montura"), resultado.getInt("honor"), resultado.getInt(
                        "deshonor"), resultado.getByte("nivelAlin"), resultado.getString("zaaps"), resultado.getInt("esposo"), resultado
                        .getString("tienda"), resultado.getInt("mercante") === 1, resultado.getInt("restriccionesA"), resultado.getInt(
                        "restriccionesB"), resultado.getInt("encarnacion"), resultado.getInt("emotes"), resultado.getString("titulos"),
                        resultado.getString("tituloVIP"), resultado.getString("ornamentos"), resultado.getString("misiones"), resultado
                        .getString("coleccion"), resultado.getByte("resets"), resultado.getString("almanax"), resultado.getInt(
                        "ultimoNivel"), resultado.getString("setsRapidos"), resultado.getInt("colorNombre"), resultado.getString(
                        "orden"), resultado.getInt("carcel") === 1, resultado.getInt("rostro"), resultado.getInt("nivelomega"), resultado.getString("pasebatalla"), resultado.getInt("LoseFight"), resultado.getInt("WinFight"), resultado.getInt("Fights"), resultado.getString("AllSucces"), resultado.getInt("puntos"), resultado.getInt("QuestN"), resultado.getString("mobsjefes"), resultado.getLong("ogGanadas"), resultado.getLong("ogGastadas"), resultado.getInt("NobjDiarios"), resultado.getString("Koli"), resultado.getString("MisAuras"), resultado.getInt("AuraActual"), resultado.getInt("Traje"), resultado.getInt("MushinPiso"), resultado.getInt("pvp"))
                if (perso.getCuenta() != null) {
                    Mundo.addPersonaje(perso)
                }
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_PERSONAJES() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `personajes`;", estaticos.GestorSQL._bdDinamica)
            while (resultado.next()) {
                val statsBase: TreeMap<Integer, Integer> = TreeMap<Integer, Integer>()
                statsBase.put(Constantes.STAT_MAS_VITALIDAD, resultado.getInt("vitalidad"))
                statsBase.put(Constantes.STAT_MAS_FUERZA, resultado.getInt("fuerza"))
                statsBase.put(Constantes.STAT_MAS_SABIDURIA, resultado.getInt("sabiduria"))
                statsBase.put(Constantes.STAT_MAS_INTELIGENCIA, resultado.getInt("inteligencia"))
                statsBase.put(Constantes.STAT_MAS_SUERTE, resultado.getInt("suerte"))
                statsBase.put(Constantes.STAT_MAS_AGILIDAD, resultado.getInt("agilidad"))
                val statsScroll: TreeMap<Integer, Integer> = TreeMap<Integer, Integer>()
                statsScroll.put(Constantes.STAT_MAS_VITALIDAD, resultado.getInt("sVitalidad"))
                statsScroll.put(Constantes.STAT_MAS_FUERZA, resultado.getInt("sFuerza"))
                statsScroll.put(Constantes.STAT_MAS_SABIDURIA, resultado.getInt("sSabiduria"))
                statsScroll.put(Constantes.STAT_MAS_INTELIGENCIA, resultado.getInt("sInteligencia"))
                statsScroll.put(Constantes.STAT_MAS_SUERTE, resultado.getInt("sSuerte"))
                statsScroll.put(Constantes.STAT_MAS_AGILIDAD, resultado.getInt("sAgilidad"))
                val perso = Personaje(resultado.getInt("id"), resultado.getString("nombre"), resultado.getByte(
                        "sexo"), resultado.getByte("clase"), resultado.getInt("color1"), resultado.getInt("color2"), resultado.getInt(
                        "color3"), resultado.getLong("kamas"), resultado.getInt("puntosHechizo"), resultado.getInt("capital"), resultado
                        .getInt("energia"), resultado.getShort("nivel"), resultado.getString("xp"), resultado.getInt("talla"), resultado
                        .getInt("gfx"), resultado.getByte("alineacion"), resultado.getInt("cuenta"), statsBase, statsScroll, resultado
                        .getInt("mostrarAmigos") === 1, resultado.getByte("mostrarAlineacion") === 1, resultado.getString("canal"),
                        resultado.getShort("mapa"), resultado.getShort("celda"), resultado.getString("objetos"), resultado.getByte(
                        "porcVida"), resultado.getString("hechizos"), resultado.getString("posSalvada"), resultado.getString("oficios"),
                        resultado.getByte("xpMontura"), resultado.getInt("montura"), resultado.getInt("honor"), resultado.getInt(
                        "deshonor"), resultado.getByte("nivelAlin"), resultado.getString("zaaps"), resultado.getInt("esposo"), resultado
                        .getString("tienda"), resultado.getInt("mercante") === 1, resultado.getInt("restriccionesA"), resultado.getInt(
                        "restriccionesB"), resultado.getInt("encarnacion"), resultado.getInt("emotes"), resultado.getString("titulos"),
                        resultado.getString("tituloVIP"), resultado.getString("ornamentos"), resultado.getString("misiones"), resultado
                        .getString("coleccion"), resultado.getByte("resets"), resultado.getString("almanax"), resultado.getInt(
                        "ultimoNivel"), resultado.getString("setsRapidos"), resultado.getInt("colorNombre"), resultado.getString(
                        "orden"), resultado.getInt("carcel") === 1, resultado.getInt("rostro"), resultado.getInt("nivelomega"), resultado.getString("pasebatalla"), resultado.getInt("LooseFight"), resultado.getInt("WinFight"), resultado.getInt("Fights"), resultado.getString("AllSucces"), resultado.getInt("puntos"), resultado.getInt("QuestN"), resultado.getString("mobsjefes"), resultado.getLong("ogGanadas"), resultado.getLong("ogGastadas"), resultado.getInt("NobjDiarios"), resultado.getString("Koli"), resultado.getString("MisAuras"), resultado.getInt("AuraActual"), resultado.getInt("Traje"), resultado.getInt("MushinPiso"), resultado.getInt("pvp"))
                if (perso.getCuenta() != null) {
                    Mundo.addPersonaje(perso)
                }
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_PASEBATALLA() {
        try {
            val RS: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * from pasebatalla ORDER BY id ASC;", estaticos.GestorSQL._bdEstatica)
            while (RS.next()) {
                val idpos: Int = RS.getInt("id")
                val idesp: Int = RS.getInt("idespec")
                val puntos: Int = RS.getInt("puntos")
                val tipo: String = RS.getString("tipo")
                val vip: Int = RS.getInt("vip")
                Mundo.Prestigios.put(idpos, "$idesp,$puntos,$tipo,$vip")
            }
            estaticos.GestorSQL.cerrarResultado(RS)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_PRISMAS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `prismas`;", estaticos.GestorSQL._bdDinamica)
            while (resultado.next()) {
                if (Mundo.getMapa(resultado.getShort("mapa")) == null) {
                    continue
                }
                val prisma = Prisma(resultado.getInt("id"), resultado.getByte("alineacion"), resultado.getByte("nivel"),
                        resultado.getShort("mapa"), resultado.getShort("celda"), resultado.getInt("honor"), resultado.getShort("area"),
                        resultado.getShort("subArea"), resultado.getLong("tiempoProteccion"))
                Mundo.addPrisma(prisma)
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun SELECT_OBJETOS_MERCADILLO(): Int {
        var num = 0
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `mercadillo_objetos`;", estaticos.GestorSQL._bdDinamica)
            while (resultado.next()) {
                val puesto: Mercadillo = Mundo.getPuestoMercadillo(resultado.getInt("mercadillo"))
                val objeto: Objeto = Mundo.getObjeto(resultado.getInt("objeto"))
                if (puesto == null || objeto == null || objeto.getDueñoTemp() !== 0) {
                    MainServidor.redactarLogServidorln("Se borro el objeto mercadillo id:" + resultado.getInt("objeto")
                            .toString() + ", dueño: " + resultado.getInt("dueño"))
                    estaticos.GestorSQL.DELETE_OBJ_MERCADILLO(resultado.getInt("objeto"))
                    continue
                }
                puesto.addObjMercaAlPuesto(ObjetoMercadillo(resultado.getInt("precio"), resultado.getByte("cantidad"),
                        resultado.getInt("dueño"), objeto, puesto.getID()))
                num++
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
        return num
    }

    fun CARGAR_RECAUDADORES(): Int {
        var numero = 0
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `recaudadores`;", estaticos.GestorSQL._bdDinamica)
            while (resultado.next()) {
                val mapa: Mapa = Mundo.getMapa(resultado.getShort("mapa")) ?: continue
                val recaudador = Recaudador(resultado.getInt("id"), resultado.getShort("mapa"), resultado
                        .getShort("celda"), resultado.getByte("orientacion"), resultado.getInt("gremio"), resultado.getString(
                        "nombre1"), resultado.getString("nombre2"), resultado.getString("objetos"), resultado.getLong("kamas"),
                        resultado.getLong("xp"), resultado.getLong("tiempoProteccion"), resultado.getLong("tiempoCreacion"), resultado
                        .getInt("dueño"))
                Mundo.addRecaudador(recaudador)
                numero++
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
        return numero
    }

    fun CARGAR_GREMIOS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM gremios;", estaticos.GestorSQL._bdDinamica)
            while (resultado.next()) {
                Mundo.addGremio(Gremio(resultado.getInt("id"), resultado.getString("nombre"), resultado.getString(
                        "emblema"), resultado.getShort("nivel"), resultado.getLong("xp"), resultado.getShort("capital"), resultado
                        .getByte("recaudadores"), resultado.getString("hechizos"), resultado.getString("stats"), resultado.getNString("anuncio")))
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    @Throws(SQLException::class)
    private fun RESULSET_MAP(resultado: ResultSet): Mapa {
        val mapaID: Int = resultado.getInt("id")
        if (MainServidor.MODO_DEBUG) {
            System.out.println("Cargando mapa ID $mapaID")
        }
        return Mapa(mapaID, resultado.getString("fecha"), resultado.getByte("ancho"), resultado.getByte("alto"),
                resultado.getString("posPelea"), resultado.getString("mapData"), resultado.getString("key"), resultado.getString(
                "mobs"), resultado.getShort("X"), resultado.getShort("Y"), resultado.getShort("subArea"), resultado.getByte(
                "maxGrupoMobs"), resultado.getByte("minMobsPorGrupo"), resultado.getByte("maxMobsPorGrupo"), resultado.getByte("maxMercantes"), resultado.getShort(
                "capabilities"), resultado.getByte("maxPeleas"), resultado.getInt("bgID"), resultado.getShort("musicID"),
                resultado.getShort("ambienteID"), resultado.getByte("outDoor"), resultado.getInt("minNivelGrupoMob"), resultado
                .getInt("maxNivelGrupoMob"), resultado.getString("personaliza"), resultado.getString("limpieza"))
    }

    fun CLONAR_MAPA(mapaClonar: Mapa, nuevaID: Int, nuevaFecha: String?, nuevaX: Int, nuevaY: Int,
                    nuevaSubArea: Int): Boolean {
        var consultaSQL = "REPLACE INTO `mapas` VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
        try {
            var i = 1
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setInt(i++, nuevaID)
            declaracion.setString(i++, nuevaFecha)
            declaracion.setInt(i++, mapaClonar.getAncho())
            declaracion.setInt(i++, mapaClonar.getAlto())
            declaracion.setInt(i++, mapaClonar.getBgID())
            declaracion.setInt(i++, mapaClonar.getAmbienteID())
            declaracion.setInt(i++, mapaClonar.getMusicID())
            declaracion.setInt(i++, mapaClonar.getOutDoor())
            declaracion.setInt(i++, mapaClonar.getCapabilities())
            declaracion.setString(i++, mapaClonar.strCeldasPelea())
            declaracion.setString(i++, mapaClonar.getKey())
            declaracion.setString(i++, mapaClonar.getMapData())
            declaracion.setString(i++, "")
            declaracion.setInt(i++, nuevaX)
            declaracion.setInt(i++, nuevaY)
            declaracion.setInt(i++, nuevaSubArea)
            declaracion.setInt(i++, mapaClonar.getMaxGrupoDeMobs())
            declaracion.setInt(i++, mapaClonar.getMaxMobsPorGrupo())
            declaracion.setInt(i++, mapaClonar.getMinNivelGrupoMob())
            declaracion.setInt(i++, mapaClonar.getMaxNivelGrupoMob())
            declaracion.setInt(i++, mapaClonar.getMaxMercantes())
            declaracion.setInt(i++, mapaClonar.getMaxNumeroPeleas())
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            consultaSQL = "SELECT * FROM `mapas` WHERE `id` = $nuevaID;"
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                if (Mundo.mapaExiste(resultado.getInt("id"))) {
                    continue
                }
                val mapa: Mapa = estaticos.GestorSQL.RESULSET_MAP(resultado)
                Mundo.addMapa(mapa)
                return true
            }
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
        return false
    }

    fun CARGAR_MAPAS() {
        var consultaSQL = "SELECT * FROM `mapas` ;"
        try {
            if (MainServidor.MODO_MAPAS_LIMITE) {
                consultaSQL = ("SELECT * FROM `mapas` WHERE `subArea` IN (" + MainServidor.STR_SUBAREAS_LIMITE.toString() + ") OR `id` IN ("
                        + MainServidor.STR_MAPAS_LIMITE.toString() + ");")
            }
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            var mapa: Mapa
            // 256 MB = 1500 MAPAS
            // 1 GB = 6000 MAPAS
            while (resultado.next()) {
                if (Mundo.mapaExiste(resultado.getInt("id"))) {
                    continue
                }
                mapa = estaticos.GestorSQL.RESULSET_MAP(resultado)
                Mundo.addMapa(mapa)
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_MAPAS_IDS(ids: String) {
        if (ids.isEmpty()) {
            return
        }
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `mapas` WHERE `id` IN ($ids) ;", estaticos.GestorSQL._bdEstatica)
            var mapa: Mapa
            while (resultado.next()) {
                if (Mundo.mapaExiste(resultado.getInt("id"))) {
                    continue
                }
                mapa = estaticos.GestorSQL.RESULSET_MAP(resultado)
                Mundo.addMapa(mapa)
                estaticos.GestorSQL.CARGAR_TRIGGERS_POR_MAPA(mapa.getID())
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_MAPAS_SUBAREAS(subAreas: String) {
        if (subAreas.isEmpty()) {
            return
        }
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `mapas` WHERE `subArea` IN ($subAreas) ;",
                    estaticos.GestorSQL._bdEstatica)
            var mapa: Mapa
            while (resultado.next()) {
                if (Mundo.mapaExiste(resultado.getInt("id"))) {
                    continue
                }
                mapa = estaticos.GestorSQL.RESULSET_MAP(resultado)
                Mundo.addMapa(mapa)
                estaticos.GestorSQL.CARGAR_TRIGGERS_POR_MAPA(mapa.getID())
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_TRIGGERS_POR_MAPA(id: Int) {
        val consultaSQL = "SELECT * FROM `celdas_accion` WHERE `mapa` = '$id';"
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                val mapa: Mapa = Mundo.getMapa(resultado.getInt("mapa"))
                if (mapa == null || mapa.getCelda(resultado.getShort("celda")) == null) {
                    continue
                }
                mapa.getCelda(resultado.getShort("celda")).addAccion(resultado.getInt("accion"), resultado.getString("args"),
                        resultado.getString("condicion"))
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
    }

    fun CARGAR_MOBS_FIJOS(): Int {
        var numero = 0
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `mobs_fix`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                val mapas: ArrayList<Mapa> = ArrayList()
                for (m in resultado.getString("mapa").split(",")) {
                    if (m.isEmpty()) {
                        continue
                    }
                    try {
                        val mapa: Mapa = Mundo.getMapa(Short.parseShort(m))
                        if (m != null) {
                            mapas.add(mapa)
                        }
                    } catch (e: Exception) {
                    }
                }
                if (mapas.isEmpty()) {
                    continue
                }
                val mapa: Mapa = mapas.get(0)
                if (mapa == null) {
                    MainServidor.redactarLogServidorln("EL MAPA " + resultado.getShort("mapa").toString() + " NO EXISTE")
                    continue
                }
                if (mapa.getCelda(resultado.getShort("celda")) == null) {
                    MainServidor.redactarLogServidorln("LA CELDA " + resultado.getShort("celda").toString() + " DEL MAPA " + resultado
                            .getShort("mapa").toString() + " NO EXISTE")
                    continue
                }
                var tipoGrupo: TipoGrupo = Constantes.getTipoGrupoMob(resultado.getInt("tipo"))
                if (tipoGrupo === TipoGrupo.NORMAL) {
                    tipoGrupo = TipoGrupo.FIJO
                }
                val grupoMob: GrupoMob = mapa.addGrupoMobPorTipo(resultado.getShort("celda"), resultado.getString("mobs"), tipoGrupo,
                        resultado.getString("condicion"), mapas, if (resultado.getInt("movible") === 1) true else false, resultado.getInt("jefe"), resultado.getString("idolos"), resultado.getInt("hechizoJefe"))
                if (!MainServidor.MOBS_TRAMPOSOS.contains(grupoMob.getJefe())) {
                    MainServidor.MOBS_TRAMPOSOS.add(grupoMob.getJefe())
                }
                MainServidor.JEFE_HECHIZO.put(grupoMob.getJefe(), grupoMob.gethechizoJefe())
                MainServidor.JEFE_AYUDANTE.put(grupoMob.getJefe(), grupoMob.getIodoloJefe())
                val s1: ArrayList<Integer> = Mundo.getMapaEstrellas(mapa.getID())
                val s2: ArrayList<String> = Mundo.getMapaHeroico(mapa.getID())
                val estrellas = if (s1 == null) -1 else if (s1.isEmpty()) -1 else s1.get(0)
                val heroico = if (s2 == null) "" else if (s2.isEmpty()) "" else s2.get(0)
                if (estrellas > -1) {
                    grupoMob.setBonusEstrellas(estrellas)
                }
                if (!heroico.isEmpty()) {
                    grupoMob.addObjetosKamasInicioServer(heroico)
                }
                if (s1 != null && !s1.isEmpty()) {
                    s1.remove(0)
                }
                if (s2 != null && !s2.isEmpty()) {
                    s2.remove(0)
                }
                grupoMob.setSegundosRespawn(resultado.getInt("segundosRespawn"))
                numero++
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
        return numero
    }

    fun SELECT_ANIMACIONES() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `animaciones`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                Mundo.addAnimacion(Animacion(resultado.getInt("id"), resultado.getInt("hechizoAnimacion"), resultado.getInt(
                        "tipoDisplay"), resultado.getInt("spriteAnimacion"), resultado.getInt("level"), resultado.getInt("duracion"),
                        resultado.getInt("talla")))
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_COMANDOS_MODELO() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `comandos_modelo`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                Mundo.addComando(resultado.getString("comando"), resultado.getInt("rango"))
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_LIBROS_ARTESANOS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `libro_artesanos`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                val libro = LibroArtesano(resultado.getInt("mapa"), resultado.getString("oficios"))
                Mundo.addLibroArtesano(libro.getMapa(), libro)
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_IDOLOS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `idolos`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                val idolo = Idolo(resultado.getInt("idolo"), resultado.getInt("bonus"), resultado.getInt("hechizo"), resultado.getInt("nivel"), resultado.getInt("ejecucion"))
                Mundo.addIdolos(idolo.getIdolo(), idolo)
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_SUCCESS() {
        try {
            val RS: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `sucess`;", estaticos.GestorSQL._bdEstatica)
            while (RS.next()) {
                Mundo.addSucess(Sucess(RS.getInt("guid"), RS.getString("name"), RS.getByte("type"), RS.getString("args"), RS.getInt("recompense"), RS.getString("recompenseArgs"), RS.getInt("puntos"), RS.getInt("art"), RS.getInt("categoria")))
            }
            estaticos.GestorSQL.cerrarResultado(RS)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_LOGROS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `logros`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                val logro = Logro()
                logro.setId(resultado.getInt("id"))
                logro.setTitulo(resultado.getString("titulo"))
                logro.setDescripcion(resultado.getString("descripcion"))
                logro.setRecompensa(resultado.getInt("recompensa"))
                logro.setCantidad(resultado.getString("cantidad"))
                logro.setTipo(resultado.getInt("tipo"))
                logro.setArgs(resultado.getString("args"))
                Mundo.addLogro(logro.getId(), logro)
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_ACCIONES_OMEGA() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `omega`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                val omega = Omega(resultado.getInt("nivel"), resultado.getInt("accion"), resultado.getString("args"), resultado.getString("packet"))
                Mundo.addOmega(omega.getNivel(), omega)
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_OTROS_INTERACTIVOS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `otros_interactivos`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                Mundo.addOtroInteractivo(OtroInteractivo(resultado.getInt("gfx"), resultado.getShort("mapaID"), resultado
                        .getShort("celdaID"), resultado.getInt("accion"), resultado.getString("args"), resultado.getString("condicion"),
                        resultado.getInt("tiempoRecarga")))
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_COMIDAS_MASCOTAS(): Int {
        var numero = 0
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `mascotas_modelo`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                Mundo.addMascotaModelo(MascotaModelo(resultado.getInt("mascota"), resultado.getInt("maximoComidas"),
                        resultado.getString("statsPorEfecto"), resultado.getString("comidas"), resultado.getInt("devorador"), resultado
                        .getInt("fantasma"), resultado.getString("statsPorEfectoMejorado"), resultado.getInt("maximoComidasMejorado"), resultado.getInt("minutosAlimento"), resultado.getInt("maxminutosAlimento")))
                numero++
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
        return numero
    }

    fun CARGAR_HECHIZOS_OMEGA() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `hechizos_omega`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                val id: Int = resultado.getInt("id")
                val txt: String = resultado.getString("nivelOmega")
                if (!txt.isEmpty()) {
                    try {
                        val sh: StatHechizo = Hechizo.analizarHechizoStats(id, 7, txt)
                        Mundo.HECHIZOS_OMEGA.put(id, sh)
                    } catch (e: Exception) {
                        MainServidor.redactarLogServidorln("BUG HECHIZO: $id NIVEL 7")
                        estaticos.GestorSQL.exceptionExit(e, "")
                    }
                }
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
            System.out.println("Cargando Hechizo Omega: " + Mundo.HECHIZOS_OMEGA.size())
        } catch (e: Exception) {
        }
    }

    fun CARGAR_HECHIZOS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `hechizos`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                val id: Int = resultado.getInt("id")
                val hechizo = Hechizo(id, resultado.getString("nombre"), resultado.getInt("sprite"), resultado
                        .getString("spriteInfos"), resultado.getInt("valorIA"))
                Mundo.addHechizo(hechizo)
                for (i in 1..6) {
                    try {
                        var sh: StatHechizo? = null
                        val txt: String = resultado.getString("nivel$i")
                        if (!txt.isEmpty()) {
                            try {
                                sh = Hechizo.analizarHechizoStats(id, i, txt)
                            } catch (e: Exception) {
                                MainServidor.redactarLogServidorln("BUG HECHIZO: $id NIVEL $i")
                                estaticos.GestorSQL.exceptionExit(e, "")
                            }
                        }
                        if (sh != null) {
                            hechizo.addStatsHechizos(i, sh)
                        }
                    } catch (e: Exception) {
                    }
                }
                if (MainServidor.PARAMS_HECHIZOS_NIVEL_7) {
                    try {
                        var sh: StatHechizo? = null
                        val txt: String = resultado.getString("nivel7")
                        if (!txt.isEmpty()) {
                            try {
                                sh = Hechizo.analizarHechizoStats(id, 7, txt)
                            } catch (e: Exception) {
                                MainServidor.redactarLogServidorln("BUG HECHIZO: $id NIVEL 7")
                                estaticos.GestorSQL.exceptionExit(e, "")
                            }
                        }
                        if (sh != null) {
                            Mundo.HECHIZOS_OMEGA.put(id, sh)
                            hechizo.addStatsHechizos(7, sh)
                        }
                    } catch (e: Exception) {
                    }
                }
                /*if(Mundo.HECHIZOS_OMEGA.containsKey(id)){
					hechizo.addStatsHechizos(7,Mundo.HECHIZOS_OMEGA.get(id));
				}*/hechizo.setAfectados(resultado.getString("afectados"))
                hechizo.setCondiciones(resultado.getString("condiciones"))
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_ESPECIALIDADES() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `especialidades`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                Mundo.addEspecialidad(Especialidad(resultado.getInt("id"), resultado.getInt("orden"), resultado.getInt(
                        "nivel"), resultado.getString("dones")))
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_DONES_MODELOS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `dones`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                Mundo.addDonModelo(resultado.getInt("id"), resultado.getInt("stat"))
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_OBJETOS_MODELOS() {
        try {
            var maxID = 0
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `objetos_modelo`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                if (resultado.getInt("id") > MainServidor.MAX_ID_OBJETO_MODELO) {
                    continue
                }
                val obj = ObjetoModelo(resultado.getInt("id"), resultado.getString("statsModelo"), resultado
                        .getString("nombre"), resultado.getShort("tipo"), resultado.getShort("nivel"), resultado.getShort("pods"),
                        resultado.getInt("kamas"), resultado.getString("condicion"), resultado.getString("infosArma"), resultado.getInt(
                        "vendidos"), resultado.getInt("precioMedio"), resultado.getInt("ogrinas"), resultado.getBoolean("magueable"),
                        resultado.getInt("gfx"), resultado.getBoolean("nivelCore"), resultado.getBoolean("etereo"), resultado.getInt(
                        "diasIntercambio"), resultado.getInt("panelOgrinas"), resultado.getInt("panelKamas"), resultado.getString("itemPago"), resultado.getBoolean("mimo"))
                Mundo.addObjModelo(obj)
                maxID = Math.max(maxID, obj.getID())
            }
            MainServidor.MAX_ID_OBJETO_MODELO = Math.min(maxID, MainServidor.MAX_ID_OBJETO_MODELO)
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_MONTURAS_MODELOS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `monturas_modelo`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                Mundo.addMonturaModelo(MonturaModelo(resultado.getInt("id"), resultado.getString("stats"), resultado
                        .getString("color"), resultado.getInt("certificado"), resultado.getByte("generacion")))
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_MOBS_MODELOS_ID(mob: Int) {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `mobs_modelo` WHERE `id`=$mob;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                val capturable = resultado.getInt("capturable") === 1
                val esKickeable: Boolean = resultado.getString("kickeable").equals("true")
                val alineacion: Byte = resultado.getByte("alineacion")
                val tipoIA: Byte = resultado.getByte("tipoIA")
                val tipo: Byte = resultado.getByte("tipo")
                val talla: Short = resultado.getShort("talla")
                val distAgresion: Byte = resultado.getByte("agresion")
                val id: Int = resultado.getInt("id")
                val gfxID: Short = resultado.getShort("gfxID")
                val mK: String = resultado.getString("minKamas")
                val MK: String = resultado.getString("maxKamas")
                val nombre: String = resultado.getString("nombre")
                val colores: String = resultado.getString("colores")
                val grados: String = resultado.getString("grados").replaceAll(" ", "").replaceAll(",g", "g").replaceAll(
                        ":\\{l:", "@").replaceAll(",r:\\[", ",").replaceAll("\\]", "|").replaceAll("\\]\\}", "|")
                // g1: {l: 1, r: [25, 0, -12, 6, -50, 15, 15], lp: 30, ap: 5, mp: 2}, g2: {l: 2
                val hechizos: String = resultado.getString("hechizos")
                val stats: String = resultado.getString("stats")
                val pdvs: String = resultado.getString("pdvs")
                val puntos: String = resultado.getString("puntos")
                val iniciativa: String = resultado.getString("iniciativa")
                val xp: String = resultado.getString("exps")
                val sombrero: Int = resultado.getInt("sombrero")
                val capa: Int = resultado.getInt("capa")
                val mascota: Int = resultado.getInt("escudo")
                val escudo: Int = resultado.getInt("mascota")
                val nuevo: MobModelo = Mundo.getMobModelo(mob)
                if (nuevo != null) {
                    Mundo.removeMobModelo(id)
                    Mundo.addMobModelo(MobModelo(id, nombre, gfxID, alineacion, colores, grados, hechizos, stats, pdvs, puntos,
                            iniciativa, mK, MK, xp, tipoIA, capturable, talla, distAgresion, tipo, esKickeable, sombrero, capa, mascota, escudo))
                } else {
                    Mundo.addMobModelo(MobModelo(id, nombre, gfxID, alineacion, colores, grados, hechizos, stats, pdvs, puntos,
                            iniciativa, mK, MK, xp, tipoIA, capturable, talla, distAgresion, tipo, esKickeable, sombrero, capa, mascota, escudo))
                }
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_MOBS_MODELOS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `mobs_modelo`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                val capturable = resultado.getInt("capturable") === 1
                val esKickeable: Boolean = resultado.getString("kickeable").equals("true")
                val alineacion: Byte = resultado.getByte("alineacion")
                val tipoIA: Byte = resultado.getByte("tipoIA")
                val tipo: Byte = resultado.getByte("tipo")
                val talla: Short = resultado.getShort("talla")
                val distAgresion: Byte = resultado.getByte("agresion")
                val id: Int = resultado.getInt("id")
                val gfxID: Short = resultado.getShort("gfxID")
                val mK: String = resultado.getString("minKamas")
                val MK: String = resultado.getString("maxKamas")
                val nombre: String = resultado.getString("nombre")
                val colores: String = resultado.getString("colores")
                val grados: String = resultado.getString("grados").replaceAll(" ", "").replaceAll(",g", "g").replaceAll(
                        ":\\{l:", "@").replaceAll(",r:\\[", ",").replaceAll("\\]", "|").replaceAll("\\]\\}", "|")
                // g1: {l: 1, r: [25, 0, -12, 6, -50, 15, 15], lp: 30, ap: 5, mp: 2}, g2: {l: 2
                val hechizos: String = resultado.getString("hechizos")
                val stats: String = resultado.getString("stats")
                val pdvs: String = resultado.getString("pdvs")
                val puntos: String = resultado.getString("puntos")
                val iniciativa: String = resultado.getString("iniciativa")
                val xp: String = resultado.getString("exps")
                val sombrero: Int = resultado.getInt("sombrero")
                val capa: Int = resultado.getInt("capa")
                val mascota: Int = resultado.getInt("escudo")
                val escudo: Int = resultado.getInt("mascota")
                Mundo.addMobModelo(MobModelo(id, nombre, gfxID, alineacion, colores, grados, hechizos, stats, pdvs, puntos,
                        iniciativa, mK, MK, xp, tipoIA, capturable, talla, distAgresion, tipo, esKickeable, sombrero, capa, mascota, escudo))
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_MOBS_RAROS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `mobs_raros`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                val idMobRaro: Int = resultado.getInt("idMobRaro")
                val idMobNormal: Int = resultado.getInt("idMobNormal")
                val subAreas: String = resultado.getString("subAreas")
                val probabilidad: Int = resultado.getInt("probabilidad")
                val mobM: MobModelo = Mundo.getMobModelo(idMobRaro) ?: continue
                val mobN: MobModelo = Mundo.getMobModelo(idMobNormal)
                if (mobN != null) {
                    mobN.setArchiMob(mobM)
                }
                mobM.setDataExtra(probabilidad, subAreas)
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_MIEMBROS_GREMIO(): Int {
        var numero = 0
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM miembros_gremio;", estaticos.GestorSQL._bdDinamica)
            while (resultado.next()) {
                if (Mundo.getPersonaje(resultado.getInt("id")) == null) {
                    estaticos.GestorSQL.DELETE_MIEMBRO_GREMIO(resultado.getInt("id"))
                    continue
                }
                val gremio: Gremio = Mundo.getGremio(resultado.getInt("gremio")) ?: continue
                gremio.addMiembro(resultado.getInt("id"), resultado.getInt("rango"), resultado.getLong("xpDonada"), resultado
                        .getByte("porcXp"), resultado.getInt("derechos"))
                numero++
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
        return numero
    }

    fun CARGAR_MONTURAS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `monturas`;", estaticos.GestorSQL._bdDinamica)
            while (resultado.next()) {
                Mundo.addMontura(Montura(resultado.getInt("id"), resultado.getInt("color"), resultado.getByte("sexo"),
                        resultado.getInt("amor"), resultado.getInt("resistencia"), resultado.getInt("nivel"), resultado.getLong("xp"),
                        resultado.getString("nombre"), resultado.getInt("fatiga"), resultado.getInt("energia"), resultado.getByte(
                        "reproducciones"), resultado.getInt("madurez"), resultado.getInt("serenidad"), resultado.getString("objetos"),
                        resultado.getString("ancestros"), resultado.getString("habilidad"), resultado.getByte("talla"), resultado
                        .getShort("celda"), resultado.getShort("mapa"), resultado.getInt("dueño"), resultado.getByte("orientacion"),
                        resultado.getLong("fecundable"), resultado.getInt("pareja"), resultado.getByte("salvaje"), resultado.getString("stats")), false)
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_NPC_MODELOS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `npcs_modelo`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                val id: Int = resultado.getInt("id")
                val gfxID: Int = resultado.getInt("gfxID")
                val escalaX: Short = resultado.getShort("scaleX")
                val escalaY: Short = resultado.getShort("scaleY")
                val sexo: Byte = resultado.getByte("sexo")
                val color1: Int = resultado.getInt("color1")
                val color2: Int = resultado.getInt("color2")
                val color3: Int = resultado.getInt("color3")
                val foto: Int = resultado.getInt("foto")
                val preguntaID: Int = resultado.getInt("pregunta")
                val ventas: String = resultado.getString("ventas")
                val nombre: String = resultado.getString("nombre")
                val npcModelo = NPCModelo(id, gfxID, escalaX, escalaY, sexo, color1, color2, color3, foto, preguntaID,
                        ventas, nombre, resultado.getInt("arma"), resultado.getInt("sombrero"), resultado.getInt("capa"), resultado
                        .getInt("mascota"), resultado.getInt("escudo"))
                Mundo.addNPCModelo(npcModelo)
                if (MainServidor.ID_NPC_BOUTIQUE === npcModelo.getID()) {
                    MainServidor.NPC_BOUTIQUE = NPC(npcModelo, 0, 0.toShort(), 0.toByte(), "")
                } else if (MainServidor.EVENTO_NPC_2 === npcModelo.getID()) {
                    MainServidor.NPC_EVENTO = NPC(npcModelo, 0, 0.toShort(), 0.toByte(), "")
                }
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_MISION_OBJETIVOS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `mision_objetivos`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                Mundo.addMisionObjetivoModelo(resultado.getInt("id"), resultado.getByte("tipo"), resultado.getString("args"), resultado.getByte("esalHablar") === 1, resultado.getString("condicion"), resultado.getByte("esOculto") === 1)
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_ORNAMENTOS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `ornamentos`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                val o = Ornamento(resultado.getInt("id"), resultado.getString("nombre"), resultado.getInt("creditos"),
                        resultado.getInt("ogrinas"), resultado.getInt("kamas"), resultado.getString("vender").equalsIgnoreCase("true"),
                        resultado.getString("valido").equalsIgnoreCase("true"))
                Mundo.addOrnamento(o)
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_TITULOS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `titulos`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                val o = Titulo(resultado.getInt("id"), resultado.getString("nombre"), resultado.getInt("creditos"),
                        resultado.getInt("ogrinas"), resultado.getInt("kamas"), resultado.getString("vender").equalsIgnoreCase("true"),
                        resultado.getString("valido").equalsIgnoreCase("true"))
                try {
                    o.set_vip(resultado.getString("vip").equalsIgnoreCase("true"))
                } catch (e: Exception) {
                    // TODO: handle exception
                }
                Mundo.addTitulo(o)
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_AURAS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `auras`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                val o = Aura(resultado.getInt("id"), resultado.getString("nombre"))
                Mundo.addAuras(o)
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_ZAAPS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `zaaps`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                Mundo.addZaap(resultado.getShort("mapa"), resultado.getShort("celda"))
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_PREGUNTAS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `npc_preguntas`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                Mundo.addPreguntaNPC(PreguntaNPC(resultado.getInt("id"), resultado.getString("respuestas"), resultado
                        .getString("params"), resultado.getString("alternos")))
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_RESPUESTAS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `npc_respuestas`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                val id: Int = resultado.getInt("id")
                val tipo: Int = resultado.getInt("accion")
                val args: String = resultado.getString("args")
                val condicion: String = resultado.getString("condicion")
                var respuesta: RespuestaNPC? = Mundo.getRespuestaNPC(id)
                if (respuesta == null) {
                    respuesta = RespuestaNPC(id)
                    Mundo.addRespuestaNPC(respuesta)
                }
                respuesta.addAccion(Accion(tipo, args, condicion))
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_ACCION_FINAL_DE_PELEA(): Int {
        var numero = 0
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `accion_pelea`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                val mapa: Mapa = Mundo.getMapa(resultado.getShort("mapa")) ?: continue
                val accion = Accion(resultado.getInt("accion"), resultado.getString("args"), resultado.getString(
                        "condicion"))
                mapa.addAccionFinPelea(resultado.getInt("tipoPelea"), accion)
                numero++
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
            return numero
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
        return numero
    }

    fun CARGAR_ACCIONES_USO_OBJETOS(): Int {
        var numero = 0
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `objetos_accion`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                val objMod: ObjetoModelo = Mundo.getObjetoModelo(resultado.getInt("objetoModelo")) ?: continue
                objMod.addAccion(Accion(resultado.getInt("accion"), resultado.getString("args"), ""))
                numero++
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
            return numero
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
        return numero
    }

    fun CARGAR_TUTORIALES() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `tutoriales`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                val id: Int = resultado.getInt("id")
                val inicio: String = resultado.getString("inicio")
                val recompensa: String = (resultado.getString("recompensa1").toString() + "$" + resultado.getString("recompensa2") + "$"
                        + resultado.getString("recompensa3") + "$" + resultado.getString("recompensa4"))
                val fin: String = resultado.getString("final")
                Mundo.addTutorial(Tutorial(id, recompensa, inicio, fin))
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
            return
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_INTERACTIVOS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `objetos_interactivos`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                Mundo.addObjInteractivoModelo(ObjetoInteractivoModelo(resultado.getInt("id"), resultado.getInt("recarga"),
                        resultado.getInt("duracion"), resultado.getByte("accionPJ"), resultado.getByte("caminable"), resultado.getByte(
                        "tipo"), resultado.getString("gfx"), resultado.getString("skill")))
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun RECARGAR_CERCADOS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `cercados`;", estaticos.GestorSQL._bdDinamica)
            while (resultado.next()) {
                try {
                    Mundo.getCercadoPorMapa(resultado.getShort("mapa")).actualizarCercado(resultado.getInt("propietario"),
                            resultado.getInt("gremio"), resultado.getInt("precio"), resultado.getString("objetosColocados"), resultado
                            .getString("criando"))
                } catch (e: Exception) {
                }
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun RECARGAR_COFRES() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `cofres`;", estaticos.GestorSQL._bdDinamica)
            while (resultado.next()) {
                try {
                    Mundo.getCofre(resultado.getInt("id")).actualizarCofre(resultado.getString("objetos"), resultado.getLong(
                            "kamas"), resultado.getString("clave"), resultado.getInt("dueño"))
                } catch (e: Exception) {
                }
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_OBJETOS_TRUEQUE() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `objetos_trueque` ORDER BY `prioridad` DESC;",
                    estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                try {
                    Mundo.addObjetoTrueque(resultado.getInt("idObjeto"), resultado.getString("necesita"), resultado.getInt(
                            "prioridad"), resultado.getString("npc_ids"))
                } catch (e: Exception) {
                }
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
            return
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_ALMANAX() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `almanax`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                try {
                    if (resultado.getString("ofrenda").isEmpty()) {
                        continue
                    }
                    Mundo.addAlmanax(Almanax(resultado.getInt("id"), resultado.getInt("tipo"), resultado.getInt("bonus"),
                            resultado.getString("ofrenda")))
                } catch (e: Exception) {
                }
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
            return
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_MISIONES() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `misiones`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                val mision = MisionModelo(resultado.getInt("id"), resultado.getString("etapas"), resultado
                        .getString("nombre"), resultado.getString("pregDarMision"), resultado.getString("pregMisCompletada"), resultado
                        .getString("pregMisIncompleta"), resultado.getString("puedeRepetirse").equalsIgnoreCase("true"))
                Mundo.addMision(mision)
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun SELECT_PUESTOS_MERCADILLOS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `mercadillos`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                Mundo.addPuestoMercadillo(Mercadillo(resultado.getInt("id"), resultado.getString("mapa"), resultado.getInt(
                        "porcVenta"), resultado.getShort("tiempoVenta"), resultado.getShort("cantidad"), resultado.getShort("nivelMax"),
                        resultado.getString("categorias")))
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_ETAPAS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `mision_etapas`;", estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                Mundo.addEtapa(resultado.getInt("id"), resultado.getString("recompensas"), resultado.getString("objetivos"),
                        resultado.getString("nombre"), resultado.getString("variosobj"))
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_MAPAS_ESTRELLAS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `mapas_estrellas`;", estaticos.GestorSQL._bdDinamica)
            while (resultado.next()) {
                try {
                    Mundo.addMapaEstrellas(resultado.getShort("mapa"), resultado.getString("estrellas"))
                } catch (e: Exception) {
                }
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
            return
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_OBJETOS() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `objetos`;", estaticos.GestorSQL._bdDinamica)
            while (resultado.next()) {
                Mundo.objetoIniciarServer(resultado.getInt("id"), resultado.getInt("modelo"), resultado.getLong("cantidad"),
                        resultado.getByte("posicion"), resultado.getString("stats"), resultado.getInt("objevivo"), resultado.getInt(
                        "precio"))
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun SELECT_RANKING_KOLISEO() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `ranking_koliseo`;", estaticos.GestorSQL._bdDinamica)
            while (resultado.next()) {
                Mundo.addRankingKoliseo(RankingKoliseo(resultado.getInt("id"), resultado
                        .getInt("victorias"), resultado.getInt("derrotas")))
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun SELECT_RANKING_PVP() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `ranking_pvp`;", estaticos.GestorSQL._bdDinamica)
            while (resultado.next()) {
                Mundo.addRankingPVP(RankingPVP(resultado.getInt("id"), resultado.getInt(
                        "victorias"), resultado.getInt("derrotas"), resultado.getInt("nivelAlineacion")))
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun CARGAR_MAPAS_HEROICO() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `mapas_heroico`;", estaticos.GestorSQL._bdDinamica)
            while (resultado.next()) {
                try {
                    Mundo.addMapaHeroico(resultado.getShort("mapa"), resultado.getString("mobs"), resultado.getString("objetos"),
                            resultado.getString("kamas"))
                } catch (e: Exception) {
                }
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
            return
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionExit(e, "")
        }
    }

    fun INSERT_CEMENTERIO(nombre: String?, nivel: Int, sexo: Byte, clase: Byte, asesino: String?, subArea: Int) {
        val consultaSQL = "INSERT INTO `cementerio` (`nombre`,`nivel`,`sexo`,`clase`,`asesino`,`subArea`,`fecha`) VALUES (?,?,?,?,?,?,?);"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setString(1, nombre)
            declaracion.setInt(2, nivel)
            declaracion.setByte(3, sexo)
            declaracion.setByte(4, clase)
            declaracion.setString(5, asesino)
            declaracion.setInt(6, subArea)
            declaracion.setLong(7, System.currentTimeMillis())
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun INSERT_OBJETO_TRUEQUE(objeto: Int, solicita: String?, prioridad: Int, npcs: String?, nombre: String?) {
        val consultaSQL = "INSERT INTO `objetos_trueque` (`idObjeto`,`necesita`,`prioridad`,`npc_ids`,`nombre_objeto`) VALUES (?,?,?,?,?);"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setInt(1, objeto)
            declaracion.setString(2, solicita)
            declaracion.setInt(3, prioridad)
            declaracion.setString(4, npcs)
            declaracion.setString(5, nombre)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun UPDATE_STATS_OBJETO_SET(id: Int, bonus: String?) {
        val consultaSQL = "UPDATE `objetos_set` SET `bonus` = ? WHERE `id` = ? ;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setString(1, bonus)
            declaracion.setInt(2, id)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun UPDATE_PRECIO_OBJETO_MODELO(id: Int, ogrinas: Long, vip: Boolean) {
        var consultaSQL = "UPDATE `objetos_modelo` SET `kamas` = ? WHERE `id` = ? ;"
        if (vip) {
            consultaSQL = "UPDATE `objetos_modelo` SET `ogrinas` = ? WHERE `id` = ? ;"
        }
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setLong(1, ogrinas)
            declaracion.setInt(2, id)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun UPDATE_STATS_OBJETO_MODELO(id: Int, stats: String?) {
        val consultaSQL = "UPDATE `objetos_modelo` SET `statsModelo` = ? WHERE `id` = ? ;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setString(1, stats)
            declaracion.setInt(2, id)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun INSERT_COFRE_MODELO(casaID: Int, mapaID: Int, celdaID: Short) {
        val consultaSQL = "INSERT INTO `cofres_modelo` (`casa`,`mapa`,`celda`) VALUES (?,?,?);"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setInt(1, casaID)
            declaracion.setInt(2, mapaID)
            declaracion.setInt(3, celdaID)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun GET_COFRE_POR_MAPA_CELDA(mapa: Int, celda: Short): Int {
        var id = -1
        val consultaSQL = "SELECT * FROM `cofres_modelo` WHERE `mapa` = '$mapa' AND `celda` = '$celda';"
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            if (resultado.first()) {
                id = resultado.getInt("id")
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
        return id
    }

    fun REPLACE_COFRE(cofre: Cofre?, salvarObjetos: Boolean) {
        if (cofre == null) {
            return
        }
        val consultaSQL = "REPLACE INTO `cofres` VALUES (?,?,?,?,?)"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setInt(1, cofre.getID())
            declaracion.setString(2, cofre.analizarObjetoCofreABD())
            declaracion.setLong(3, cofre.getKamas())
            declaracion.setString(4, cofre.getClave())
            declaracion.setInt(5, cofre.getDueñoID())
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            if (salvarObjetos) {
                estaticos.GestorSQL.SALVAR_OBJETOS(cofre.getObjetos())
            }
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun CARGAR_PERSONAJES_POR_CUENTA(cuenta: Cuenta) {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `personajes` WHERE `cuenta` = " +
                    cuenta.getID().toString() + ";",
                    estaticos.GestorSQL._bdDinamica)
            while (resultado.next()) {
                try {
                    cuenta.addPersonaje(Mundo.getPersonaje(resultado.getInt("id")))
                } catch (e: Exception) {
                    MainServidor.redactarLogServidorln("El personaje " + resultado.getString("nombre")
                            .toString() + " no se pudo agregar a la cuenta (REFRESCAR CUENTA)")
                }
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            MainServidor.redactarLogServidorln("ERROR SQL: " + e.toString())
            e.printStackTrace()
        }
    }

    fun DELETE_PERSONAJE(perso: Personaje): Boolean {
        val consultaSQL = "DELETE FROM `personajes` WHERE `id` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setInt(1, perso.getID())
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
        return false
    }

    fun INSERT_CAPTCHA(captcha: String?, respuesta: String?) {
        val consultaSQL = "INSERT INTO `captchas` (`captcha`,`respuesta`) VALUES (?,?);"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setString(1, captcha)
            declaracion.setString(2, respuesta)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
        }
    }

    fun INSERT_MAPA(id: Short, fecha: String?, ancho: Byte, alto: Byte,
                    mapData: String?, X: Short, Y: Short, subArea: Short) {
        val consultaSQL = "INSERT INTO `mapas` (`id`,`fecha`,`ancho`,`alto`,`mapData`,`X`,`Y`, `subArea`,`key`, `mobs`) VALUES (?,?,?,?,?,?,?,?,'','');"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setShort(1, id)
            declaracion.setString(2, fecha)
            declaracion.setByte(3, ancho)
            declaracion.setByte(4, alto)
            declaracion.setString(5, mapData)
            declaracion.setShort(6, X)
            declaracion.setShort(7, Y)
            declaracion.setShort(8, subArea)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun UPDATE_GFX_OBJMODELO(id: Int, gfx: Int) {
        val consultaSQL = "UPDATE `objetos_modelo` SET `gfx` = ? WHERE `id` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setInt(1, gfx)
            declaracion.setInt(2, id)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun UPDATE_NIVEL_OBJMODELO(id: Int, nivel: Short) {
        val consultaSQL = "UPDATE `objetos_modelo` SET `nivel` = ?, `nivelCore` = 'true' WHERE `id` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setShort(1, nivel)
            declaracion.setInt(2, id)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun ACTUALIZAR_NPC_VENTAS(npc: NPCModelo) {
        val consultaSQL = "UPDATE `npcs_modelo` SET `ventas` = ? WHERE `id` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setString(1, npc.actualizarStringBD())
            declaracion.setInt(2, npc.getID())
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun REPLACE_GRUPOMOB_FIJO(mapaID: Int, celdaID: Int, grupoData: String?, tipo: Int,
                              condicion: String?, segundos: Int): Boolean {
        val consultaSQL = "REPLACE INTO `mobs_fix` (`mapa`,`celda`,`mobs`,`tipo`,`condicion`,`segundosRespawn`,`descripcion`) VALUES (?,?,?,?,?,?,'')"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setInt(1, mapaID)
            declaracion.setInt(2, celdaID)
            declaracion.setString(3, grupoData)
            declaracion.setInt(4, tipo)
            declaracion.setString(5, condicion)
            declaracion.setInt(6, segundos)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
        return false
    }

    fun UPDATE_MISION(id: Int, etapas: String?, pregDarMision: String?,
                      pregMisCompletada: String?, pregMisIncompleta: String?) {
        val consultaSQL = "UPDATE `misiones` SET `etapas`= ?, `pregDarMision`= ?, `pregMisCompletada`= ?, `pregMisIncompleta`= ? WHERE `id` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setString(1, etapas)
            declaracion.setString(2, pregDarMision)
            declaracion.setString(3, pregMisCompletada)
            declaracion.setString(4, pregMisIncompleta)
            declaracion.setInt(5, id)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun UPDATE_OBJETIVO_MISION(id: Int, args: String?) {
        val consultaSQL = "UPDATE `mision_objetivos` SET `args`= ? WHERE `id` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setString(1, args)
            declaracion.setInt(2, id)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun UPDATE_RECOMPENSA_ETAPA(id: Int, recompensas: String?) {
        val consultaSQL = "UPDATE `mision_etapas` SET `recompensas`= ? WHERE `id` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setString(1, recompensas)
            declaracion.setInt(2, id)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun UPDATE_ETAPA(id: Int, objetivos: String?) {
        val consultaSQL = "UPDATE `mision_etapas` SET `objetivos`= ? WHERE `id` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setString(1, objetivos)
            declaracion.setInt(2, id)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun UPDATE_NPC_MODELO(npcMod: NPCModelo, arma: Int, sombrero: Int, capa: Int,
                          mascota: Int, escudo: Int) {
        val consultaSQL = "UPDATE `npcs_modelo` SET `sexo`= ?, `scaleX`= ?, `scaleY`= ?, `gfxID`= ?, `color1`= ?, `color2`= ?, `color3`= ?, `arma`= ?, `sombrero`= ?, `capa`= ?, `mascota`= ?, `escudo`= ?  WHERE `id` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setByte(1, npcMod.getSexo())
            declaracion.setInt(2, npcMod.getTallaX())
            declaracion.setInt(3, npcMod.getTallaY())
            declaracion.setInt(4, npcMod.getGfxID())
            declaracion.setInt(5, npcMod.getColor1())
            declaracion.setInt(6, npcMod.getColor2())
            declaracion.setInt(7, npcMod.getColor3())
            declaracion.setInt(8, arma)
            declaracion.setInt(9, sombrero)
            declaracion.setInt(10, capa)
            declaracion.setInt(11, mascota)
            declaracion.setInt(12, escudo)
            declaracion.setInt(13, npcMod.getID())
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun UPDATE_ALMANAX(id: Int, ofrenda: String?, tipo: Int, bonus: Int) {
        val consultaSQL = "UPDATE `almanax` SET `ofrenda`=?, `tipo`=?, `bonus`= ? WHERE `id` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setString(1, ofrenda)
            declaracion.setInt(2, tipo)
            declaracion.setInt(3, bonus)
            declaracion.setInt(4, id)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun CAMBIAR_SEXO_CLASE(perso: Personaje) {
        val consultaSQL = "UPDATE `personajes` SET `sexo`=?, `clase`= ?, `hechizos`= ? WHERE `id`= ?"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setInt(1, perso.getSexo())
            declaracion.setInt(2, perso.getClaseID(true))
            declaracion.setString(3, perso.stringHechizosParaSQL())
            declaracion.setInt(4, perso.getID())
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun UPDATE_NOMBRE_PJ(perso: Personaje) {
        val consultaSQL = "UPDATE `personajes` SET `nombre` = ? WHERE `id` = ? ;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setString(1, perso.getNombre())
            declaracion.setInt(2, perso.getID())
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun UPDATE_COLORES_PJ(perso: Personaje) {
        val consultaSQL = "UPDATE `personajes` SET `color1` = ?, `color2`= ?, `color3` = ? WHERE `id` = ? ;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setInt(1, perso.getColor1())
            declaracion.setInt(2, perso.getColor2())
            declaracion.setInt(3, perso.getColor3())
            declaracion.setInt(4, perso.getID())
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun SALVAR_PERSONAJE(perso: Personaje, salvarObjetos: Boolean) {
        val consultaSQL = "REPLACE INTO `personajes` VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);"
        try {
            var parametro = 1
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setInt(parametro++, perso.getID())
            declaracion.setString(parametro++, perso.getNombre())
            declaracion.setByte(parametro++, perso.getSexo())
            declaracion.setByte(parametro++, perso.getClaseID(true))
            declaracion.setInt(parametro++, perso.getColor1())
            declaracion.setInt(parametro++, perso.getColor2())
            declaracion.setInt(parametro++, perso.getColor3())
            declaracion.setLong(parametro++, if (MainServidor.MODO_ALL_OGRINAS) 0 else perso.getKamas())
            declaracion.setInt(parametro++, perso.getPuntosHechizos())
            declaracion.setInt(parametro++, perso.getCapital())
            declaracion.setInt(parametro++, perso.getEnergia())
            declaracion.setInt(parametro++, perso.getNivel())
            declaracion.setString(parametro++, perso.getExperiencia())
            declaracion.setInt(parametro++, perso.getTalla())
            declaracion.setInt(parametro++, perso.getGfxIDReal())
            declaracion.setInt(parametro++, perso.getAlineacion())
            declaracion.setInt(parametro++, perso.getHonor())
            declaracion.setInt(parametro++, perso.getDeshonor())
            declaracion.setInt(parametro++, perso.getGradoAlineacion())
            declaracion.setInt(parametro++, perso.getCuentaID())
            declaracion.setInt(parametro++, perso.getSubStatsBase().get(Constantes.STAT_MAS_VITALIDAD))
            declaracion.setInt(parametro++, perso.getSubStatsBase().get(Constantes.STAT_MAS_FUERZA))
            declaracion.setInt(parametro++, perso.getSubStatsBase().get(Constantes.STAT_MAS_SABIDURIA))
            declaracion.setInt(parametro++, perso.getSubStatsBase().get(Constantes.STAT_MAS_INTELIGENCIA))
            declaracion.setInt(parametro++, perso.getSubStatsBase().get(Constantes.STAT_MAS_SUERTE))
            declaracion.setInt(parametro++, perso.getSubStatsBase().get(Constantes.STAT_MAS_AGILIDAD))
            declaracion.setInt(parametro++, if (perso.mostrarAmigos()) 1 else 0)
            declaracion.setInt(parametro++, if (perso.alasActivadas()) 1 else 0)
            declaracion.setString(parametro++, perso.getCanales())
            declaracion.setInt(parametro++, perso.getMapa().getID())
            declaracion.setInt(parametro++, perso.getCelda().getID())
            declaracion.setInt(parametro++, perso.getPorcPDV() as Int)
            declaracion.setString(parametro++, perso.stringHechizosParaSQL())
            declaracion.setString(parametro++, perso.stringObjetosABD())
            declaracion.setString(parametro++, perso.getPtoSalvada())
            declaracion.setString(parametro++, perso.stringZaapsParaBD())
            declaracion.setString(parametro++, perso.stringOficios())
            declaracion.setInt(parametro++, perso.getPorcXPMontura())
            declaracion.setInt(parametro++, if (perso.getMontura() != null) perso.getMontura().getID() else -1)
            declaracion.setInt(parametro++, perso.getEsposoID())
            declaracion.setString(parametro++, perso.getStringTienda())
            declaracion.setInt(parametro++, if (perso.esMercante()) 1 else 0)
            declaracion.setInt(parametro++, perso.getSubStatsScroll().get(Constantes.STAT_MAS_FUERZA))
            declaracion.setInt(parametro++, perso.getSubStatsScroll().get(Constantes.STAT_MAS_INTELIGENCIA))
            declaracion.setInt(parametro++, perso.getSubStatsScroll().get(Constantes.STAT_MAS_AGILIDAD))
            declaracion.setInt(parametro++, perso.getSubStatsScroll().get(Constantes.STAT_MAS_SUERTE))
            declaracion.setInt(parametro++, perso.getSubStatsScroll().get(Constantes.STAT_MAS_VITALIDAD))
            declaracion.setInt(parametro++, perso.getSubStatsScroll().get(Constantes.STAT_MAS_SABIDURIA))
            declaracion.setLong(parametro++, perso.getRestriccionesA())
            declaracion.setLong(parametro++, perso.getRestriccionesB())
            declaracion.setInt(parametro++, 0)
            declaracion.setInt(parametro++, perso.getEmotes())
            declaracion.setString(parametro++, perso.listaTitulosParaBD())
            declaracion.setString(parametro++, perso.getTituloVIP())
            declaracion.setString(parametro++, perso.listaOrnamentosParaBD())
            declaracion.setString(parametro++, perso.stringMisiones())
            declaracion.setString(parametro++, perso.listaCardMobs())
            declaracion.setByte(parametro++, perso.getResets())
            declaracion.setString(parametro++, perso.listaAlmanax())
            declaracion.setInt(parametro++, perso.getUltimoNivel())
            declaracion.setString(parametro++, perso.getSetsRapidos())
            declaracion.setInt(parametro++, perso.getColorNombre())
            declaracion.setString(parametro++, perso.getOrden().toString() + "," + perso.getOrdenNivel())
            declaracion.setInt(parametro++, if (perso.getCalabozo()) 1 else 0)
            declaracion.setInt(parametro++, perso.getAdmin())
            declaracion.setInt(parametro++, perso.get_rostroGFX())
            declaracion.setLong(parametro++, perso.getNivelOmega())
            declaracion.setString(parametro++, perso.getCanjeados().toString() + ";" + perso.PuntosPrestigio + ";" + perso.paginaCanj)
            declaracion.setInt(parametro++, perso.getNbFightLose())
            declaracion.setInt(parametro++, perso.getNbFightWin())
            declaracion.setInt(parametro++, perso.getNbFight())
            declaracion.setString(parametro++, perso.parseSucess())
            declaracion.setInt(parametro++, perso.getPointsSucces())
            declaracion.setInt(parametro++, perso.getNbQuests())
            declaracion.setString(parametro++, perso.getAlmasMobs().toString())
            declaracion.setLong(parametro++, perso.getOgGanadas())
            declaracion.setLong(parametro++, perso.getOgGastadas())
            declaracion.setLong(parametro++, perso.getNObjDiarios())
            declaracion.setString(parametro++, perso._kolirango.toString() + ";" + perso._kolipuntos)
            declaracion.setString(parametro++, perso.parseAuras())
            declaracion.setInt(parametro++, perso.getCurAura())
            declaracion.setInt(parametro++, perso.getTraje())
            declaracion.setInt(parametro++, perso.getMushinPiso())
            declaracion.setInt(parametro++, perso.getNbStalkWin())
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            val str: String = declaracion.toString()
            perso.registrar("<=SQL=> " + str.substring(str.indexOf(":")))
            if (Mundo.SERVIDOR_ESTADO === Constantes.SERVIDOR_OFFLINE) {
                MainServidor.redactarLogServidorSinPrint("SAVE SQL [" + perso.getNombre().toString() + "] ==>" + str.substring(str.indexOf(
                        ":")))
            }
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            if (perso.getMiembroGremio() != null) {
                estaticos.GestorSQL.REPLACE_MIEMBRO_GREMIO(perso.getMiembroGremio())
            }
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "PERSONAJE NO SALVADO")
        }
        if (salvarObjetos) {
            estaticos.GestorSQL.SALVAR_OBJETOS(perso.getObjetosTienda())
            estaticos.GestorSQL.SALVAR_OBJETOS(perso.getObjetosTodos())
            // SALVAR_OBJETOS(perso.getObjetosBanco());
            if (perso.getMontura() != null) {
                estaticos.GestorSQL.REPLACE_MONTURA(perso.getMontura(), true)
            }
        }
    }

    fun SALVAR_OBJETOS(objetos: Collection<Objeto?>?) {
        if (objetos == null || objetos.isEmpty()) {
            return
        }
        var tempObjetos: List<Objeto?>? = ArrayList()
        tempObjetos.addAll(objetos)
        val consultaSQL = "REPLACE INTO `objetos` VALUES(?,?,?,?,?,?,?);"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            for (obj in tempObjetos!!) {
                if (obj == null) {
                    continue
                }
                declaracion.setInt(1, obj.getID())
                declaracion.setInt(2, obj.getObjModeloID())
                declaracion.setLong(3, obj.getCantidad())
                declaracion.setInt(4, obj.getPosicion())
                declaracion.setString(5, obj.convertirStatsAString(true))
                declaracion.setInt(6, obj.getObjevivoID())
                declaracion.setLong(7, obj.getPrecio())
                estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            }
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            tempObjetos = null
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun SALVAR_OBJETO(objeto: Objeto) {
        val consultaSQL = "REPLACE INTO `objetos` VALUES (?,?,?,?,?,?,?);"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setInt(1, objeto.getID())
            declaracion.setInt(2, objeto.getObjModeloID())
            declaracion.setLong(3, objeto.getCantidad())
            declaracion.setInt(4, objeto.getPosicion())
            declaracion.setString(5, objeto.convertirStatsAString(true))
            declaracion.setInt(6, objeto.getObjevivoID())
            declaracion.setLong(7, objeto.getPrecio())
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun VACIAR_MAPAS_ESTRELLAS() {
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL("TRUNCATE `mapas_estrellas`;", estaticos.GestorSQL._bdDinamica)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
    }

    fun VACIAR_MAPAS_HEROICO() {
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL("TRUNCATE `mapas_heroico`;", estaticos.GestorSQL._bdDinamica)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
    }

    fun GET_STATEMENT_SQL_DINAMICA(consultaSQL: String?): PreparedStatement? {
        try {
            return estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
        } catch (e: Exception) {
        }
        return null
    }

    fun REPLACE_MAPAS_ESTRELLAS_BATCH(declaracion: PreparedStatement, mapaID: Int, estrellas: String?) {
        try {
            declaracion.setInt(1, mapaID)
            declaracion.setString(2, estrellas)
            declaracion.addBatch()
        } catch (e: Exception) {
        }
        return
    }

    fun REPLACE_MAPAS_HEROICO(mapaID: Int, mobs: String?, objetos: String?, kamas: String?) {
        val consultaSQL = "REPLACE INTO `mapas_heroico` VALUES (?,?,?,?);"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setInt(1, mapaID)
            declaracion.setString(2, mobs)
            declaracion.setString(3, objetos)
            declaracion.setString(4, kamas)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun DELETE_MAPA_HEROICO(mapaID: Int) {
        val consultaSQL = "DELETE FROM `mapas_heroico` WHERE `mapa` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setInt(1, mapaID)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun DELETE_MONTURA(drago: Montura) {
        val consultaSQL = "DELETE FROM `monturas` WHERE `id` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setInt(1, drago.getID())
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun DELETE_DRAGOPAVO_LISTA(lista: String) {
        val consultaSQL = "DELETE FROM `monturas` WHERE `id` IN ($lista);"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun DELETE_OBJETOS_LISTA(lista: String) {
        val consultaSQL = "DELETE FROM `objetos` WHERE `id` IN ($lista);"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun DELETE_OBJETO(id: Int) {
        val consultaSQL = "DELETE FROM `objetos` WHERE `id` = ?"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setInt(1, id)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun ACTUALIZAR_TITULO_POR_NOMBRE(nombre: String?) {
        val consultaSQL = "UPDATE `personajes` SET `titulo` = 0 WHERE `nombre` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setString(1, nombre)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun REPLACE_MONTURA(montura: Montura, salvarObjetos: Boolean) {
        val consultaSQL = "REPLACE INTO `monturas` VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setInt(1, montura.getID())
            declaracion.setInt(2, montura.getColor())
            declaracion.setInt(3, montura.getSexo())
            declaracion.setString(4, montura.getNombre())
            declaracion.setLong(5, montura.getExp())
            declaracion.setInt(6, montura.getNivel())
            declaracion.setInt(7, montura.getTalla())
            declaracion.setInt(8, montura.getResistencia())
            declaracion.setInt(9, montura.getAmor())
            declaracion.setInt(10, montura.getMadurez())
            declaracion.setInt(11, montura.getSerenidad())
            declaracion.setInt(12, montura.getReprod())
            declaracion.setInt(13, montura.getFatiga())
            declaracion.setInt(14, montura.getEnergia())
            declaracion.setString(15, montura.stringObjetosBD())
            declaracion.setString(16, montura.getAncestros())
            declaracion.setString(17, montura.strCapacidades())
            declaracion.setInt(18, montura.getOrientacion())
            declaracion.setInt(19, if (montura.getCelda() == null) -1 else montura.getCelda().getID())
            declaracion.setInt(20, if (montura.getMapa() == null) -1 else montura.getMapa().getID())
            declaracion.setInt(21, montura.getDueñoID())
            declaracion.setLong(22, montura.getTiempoFecundacion())
            declaracion.setInt(23, montura.getParejaID())
            declaracion.setString(24, if (montura.esSalvaje()) "1" else "0")
            declaracion.setString(25, montura.get_statsString())
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            if (salvarObjetos) {
                estaticos.GestorSQL.SALVAR_OBJETOS(montura.getObjetos())
            }
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun DELETE_CERCADO(id: Int): Boolean {
        val consultaSQL = "DELETE FROM `cercados` WHERE `mapa` = ? ;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setInt(1, id)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
        return false
    }

    fun REPLACE_CERCADO(cercado: Cercado) {
        var consultaSQL = "REPLACE INTO `cercados` VALUES (?,?,?,?,?,?);"
        try {
            var declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setInt(1, cercado.getMapa().getID())
            declaracion.setInt(2, cercado.getDueñoID())
            declaracion.setInt(3, if (cercado.getGremio() == null) -1 else cercado.getGremio().getID())
            declaracion.setInt(4, cercado.getPrecioPJ())
            declaracion.setString(5, cercado.strPavosCriando())
            declaracion.setString(6, cercado.strObjCriaParaBD())
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            consultaSQL = "REPLACE INTO `objetos` VALUES (?,?,?,?,?,?,?);"
            try {
                declaracion = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
                for (obj in cercado.getObjetosParaBD()) {
                    if (obj == null) {
                        continue
                    }
                    declaracion.setInt(1, obj.getID())
                    declaracion.setInt(2, obj.getObjModeloID())
                    declaracion.setLong(3, obj.getCantidad())
                    declaracion.setInt(4, obj.getPosicion())
                    declaracion.setString(5, obj.convertirStatsAString(true))
                    declaracion.setInt(6, obj.getObjevivoID())
                    declaracion.setLong(7, obj.getPrecio())
                    estaticos.GestorSQL.ejecutarTransaccion(declaracion)
                }
            } catch (e1: Exception) {
            }
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun REPLACE_RANKING_KOLISEO(rank: RankingKoliseo) {
        val consultaSQL = "REPLACE INTO `ranking_koliseo` VALUES (?,?,?);"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setInt(1, rank.getID())
            declaracion.setInt(2, rank.getVictorias())
            declaracion.setInt(3, rank.getDerrotas())
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun DELETE_RANKING_KOLISEO(id: Int): Boolean {
        val consultaSQL = "DELETE FROM `ranking_koliseo` WHERE `id` = ? ;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setInt(1, id)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
        return false
    }

    fun REPLACE_RANKING_PVP(rank: RankingPVP) {
        val consultaSQL = "REPLACE INTO `ranking_pvp` VALUES (?,?,?,?);"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setInt(1, rank.getID())
            declaracion.setInt(2, rank.getVictorias())
            declaracion.setInt(3, rank.getDerrotas())
            declaracion.setInt(4, rank.getGradoAlineacion())
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun DELETE_RANKING_PVP(id: Int): Boolean {
        val consultaSQL = "DELETE FROM `ranking_pvp` WHERE `id` = ? ;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setInt(1, id)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
        return false
    }

    fun REPLACE_ACCION_OBJETO(idModelo: Int, accion: Int, args: String?,
                              nombre: String?) {
        val consultaSQL = "REPLACE INTO `objetos_accion` VALUES(?,?,?,?);"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setInt(1, idModelo)
            declaracion.setInt(2, accion)
            declaracion.setString(3, args)
            declaracion.setString(4, nombre)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun DELETE_ACCION_OBJETO(id: Int) {
        val consultaSQL = "DELETE FROM `objetos_accion` WHERE `objetoModelo` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setInt(1, id)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun INSERT_DROP(mob: Int, objeto: Int, prosp: Int, porcentaje: Float, max: Int,
                    nMob: String?, nObjeto: String?, condicion: String?) {
        val consultaSQL = "INSERT INTO `drops` (`mob`,`objeto`,`prospeccion`, `porcentaje`,`max`, `nombre_mob`, `nombre_objeto`,`condicion`) VALUES (?,?,?,?,?,?,?,?);"
        try {
            estaticos.GestorSQL.DELETE_DROP(objeto, mob)
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setInt(1, mob)
            declaracion.setInt(2, objeto)
            declaracion.setInt(3, prosp)
            declaracion.setFloat(4, porcentaje)
            declaracion.setInt(5, max)
            declaracion.setString(6, nMob)
            declaracion.setString(7, nObjeto)
            declaracion.setString(8, condicion)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun UPDATE_DROPS(idMob: Int, idObjeto: Int, nombreMob: String?,
                     nombreObjeto: String?) {
        val consultaSQL = "UPDATE `drops` SET `nombre_mob`=?, `nombre_objeto` =? WHERE `mob`=? AND `objeto`= ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setString(1, nombreMob)
            declaracion.setString(2, nombreObjeto)
            declaracion.setInt(3, idMob)
            declaracion.setInt(4, idObjeto)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun DELETE_DROP(objeto: Int, mob: Int) {
        val consultaSQL = "DELETE FROM `drops` WHERE `objeto` ='$objeto' AND `mob`= '$mob' ;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun REPLACE_CELDAS_ACCION(mapa1: Int, celda1: Int, accion: Int, args: String?,
                              condicion: String?): Boolean {
        val consultaSQL = "REPLACE INTO `celdas_accion` VALUES (?,?,?,?,?);"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setInt(1, mapa1)
            declaracion.setInt(2, celda1)
            declaracion.setInt(3, accion)
            declaracion.setString(4, args)
            declaracion.setString(5, condicion)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
        return false
    }

    fun REPLACE_OBJETO_MODELO(id: Int, tipo: Short, nombre: String?, gfx: Short,
                              nivelCore: Boolean, nivel: Short, stats: String?, peso: Short, set: Short, kamas: Long,
                              ogrinas: Long, magueable: Boolean, infoArma: String?, condicion: String?): Boolean {
        val consultaSQL = "REPLACE INTO `objetos_modelo` VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,'0','0');"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setInt(1, id)
            declaracion.setShort(2, tipo)
            declaracion.setString(3, nombre)
            declaracion.setShort(4, gfx)
            declaracion.setString(5, if (nivelCore) "true" else "false")
            declaracion.setShort(6, nivel)
            declaracion.setString(7, stats)
            declaracion.setShort(8, peso)
            declaracion.setShort(9, set)
            declaracion.setLong(10, kamas)
            declaracion.setLong(11, ogrinas)
            declaracion.setString(12, if (magueable) "true" else "false")
            declaracion.setString(13, infoArma)
            declaracion.setString(14, condicion)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
        return false
    }

    fun DELETE_TRIGGER(mapaID: Int, celdaID: Int): Boolean {
        val consultaSQL = "DELETE FROM `celdas_accion` WHERE `mapa` = ? AND `celda` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setInt(1, mapaID)
            declaracion.setInt(2, celdaID)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
        return false
    }

    fun UPDATE_MAPA_POS_PELEA(mapaID: Int, pos: String?): Boolean {
        val consultaSQL = "UPDATE `mapas` SET `posPelea` = ? WHERE `id` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setString(1, pos)
            declaracion.setInt(2, mapaID)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
        return false
    }

    fun UPDATE_MAPA_MAX_PELEAS(mapaID: Int, max: Byte): Boolean {
        val consultaSQL = "UPDATE `mapas` SET `maxPeleas` = ? WHERE `id` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setByte(1, max)
            declaracion.setInt(2, mapaID)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
        return false
    }

    fun UPDATE_MAPA_MAX_MERCANTES(mapaID: Int, max: Byte): Boolean {
        val consultaSQL = "UPDATE `mapas` SET `maxMercantes` = ? WHERE `id` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setByte(1, max)
            declaracion.setInt(2, mapaID)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
        return false
    }

    fun UPDATE_MAPA_MAX_GRUPO_MOBS(mapaID: Int, max: Byte): Boolean {
        val consultaSQL = "UPDATE `mapas` SET `maxGrupoMobs` = ? WHERE `id` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setByte(1, max)
            declaracion.setInt(2, mapaID)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
        return false
    }

    fun UPDATE_MAPA_MAX_MOB_GRUPO(mapaID: Int, max: Byte): Boolean {
        val consultaSQL = "UPDATE `mapas` SET `maxMobsPorGrupo` = ? WHERE `id` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setByte(1, max)
            declaracion.setInt(2, mapaID)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
        return false
    }

    fun UPDATE_MAPA_MIN_MOB_GRUPO(mapaID: Int, min: Byte): Boolean {
        val consultaSQL = "UPDATE `mapas` SET `minMobsPorGrupo` = ? WHERE `id` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setByte(1, min)
            declaracion.setInt(2, mapaID)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
        return false
    }

    fun UPDATE_MAPA_PARAMETROS(id: Int, param: Int): Boolean {
        val consultaSQL = "UPDATE `mapas` SET `capabilities` = ? WHERE `id` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setInt(1, param)
            declaracion.setInt(2, id)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
        return false
    }

    fun DELETE_NPC_DEL_MAPA(mapa: Int, id: Int): Boolean {
        val consultaSQL = "DELETE FROM `npcs_ubicacion` WHERE `mapa` = ? AND `npc` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setInt(1, mapa)
            declaracion.setInt(2, id)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
        return false
    }

    fun DELETE_NPC_UBICACION(id: Int): Boolean {
        val consultaSQL = "DELETE FROM `npcs_ubicacion` WHERE `npc` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setInt(1, id)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
        return false
    }

    fun DELETE_RECAUDADOR(id: Int) {
        val consultaSQL = "DELETE FROM `recaudadores` WHERE `id` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setInt(1, id)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            return
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun REPLACE_NPC_AL_MAPA(mapa: Int, celda: Short, id: Int, direccion: Byte,
                            nombre: String?, condicion: String?): Boolean {
        val consultaSQL = "REPLACE INTO `npcs_ubicacion` VALUES (?,?,?,?,?,?);"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setInt(1, mapa)
            declaracion.setShort(2, celda)
            declaracion.setInt(3, id)
            declaracion.setByte(4, direccion)
            declaracion.setString(5, nombre)
            declaracion.setString(6, condicion)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
        return false
    }

    fun REPLACE_RECAUDADOR(recaudador: Recaudador, salvarObjetos: Boolean) {
        val consultaSQL = "REPLACE INTO `recaudadores` VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?);"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setInt(1, recaudador.getID())
            declaracion.setInt(2, recaudador.getMapa().getID())
            declaracion.setInt(3, recaudador.getCelda().getID())
            declaracion.setInt(4, recaudador.getOrientacion())
            declaracion.setInt(5, recaudador.getGremio().getID())
            declaracion.setString(6, recaudador.getN1())
            declaracion.setString(7, recaudador.getN2())
            declaracion.setString(8, recaudador.stringListaObjetosBD())
            declaracion.setLong(9, recaudador.getKamas())
            declaracion.setLong(10, recaudador.getExp())
            declaracion.setLong(11, recaudador.getTiempoProteccion())
            declaracion.setInt(12, recaudador.getDueño())
            declaracion.setLong(13, recaudador.getTiempoCreacion())
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            try {
                if (salvarObjetos) {
                    estaticos.GestorSQL.SALVAR_OBJETOS(recaudador.getObjetos())
                }
            } catch (e: Exception) {
            }
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun INSERT_LOG_PELEA(cuentas_g: String?, personajes_g: String?, ips_g: String?, puntos_g: String?,
                         cuentas_p: String?, personajes_p: String?, ips_p: String?, puntos_p: String?, duracion: Long, agresor: Int, agredido: Int,
                         mapa: Int): Boolean {
        val consultaSQL = "INSERT INTO `logs_aggro` (`gagnant_account`,`gagnant_perso`,`gagnant_ip`,`gagnant_ph`,`perdant_account`,`perdant_perso`,`perdant_ip`,`perdant_ph`,`duree`,`aggroBy`,`aggroTo`,`idMap`,`timestamp`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?);"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setString(1, cuentas_g)
            declaracion.setString(2, personajes_g)
            declaracion.setString(3, ips_g)
            declaracion.setString(4, puntos_g)
            declaracion.setString(5, cuentas_p)
            declaracion.setString(6, personajes_p)
            declaracion.setString(7, ips_p)
            declaracion.setString(8, puntos_p)
            declaracion.setLong(9, duracion)
            declaracion.setInt(10, agresor)
            declaracion.setInt(11, agredido)
            declaracion.setInt(12, mapa)
            declaracion.setLong(13, System.currentTimeMillis())
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
        return false
    }

    fun INSERT_ACCION_FIN_PELEA(mapaID: Int, tipoPelea: Int, accionID: Int,
                                args: String?, condicion: String?, descripcion: String?): Boolean {
        estaticos.GestorSQL.DELETE_FIN_ACCION_PELEA(mapaID, tipoPelea, accionID)
        val consultaSQL = "INSERT INTO `accion_pelea` VALUES (?,?,?,?,?,?);"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setInt(1, mapaID)
            declaracion.setInt(2, tipoPelea)
            declaracion.setInt(3, accionID)
            declaracion.setString(4, args)
            declaracion.setString(5, condicion)
            declaracion.setString(6, descripcion)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
        return false
    }

    fun DELETE_FIN_ACCION_PELEA(mapaID: Int, tipoPelea: Int, accionID: Int): Boolean {
        val consultaSQL = "DELETE FROM `accion_pelea` WHERE mapa = ? AND tipoPelea = ? AND accion = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setInt(1, mapaID)
            declaracion.setInt(2, tipoPelea)
            declaracion.setInt(3, accionID)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
        return false
    }

    fun INSERT_GREMIO(gremio: Gremio) {
        val consultaSQL = "INSERT INTO `gremios` VALUES (?,?,?,1,0,0,0,?,?,?);"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setInt(1, gremio.getID())
            declaracion.setString(2, gremio.getNombre())
            declaracion.setString(3, gremio.getEmblema())
            declaracion.setString(4, "462;0|461;0|460;0|459;0|458;0|457;0|456;0|455;0|454;0|453;0|452;0|451;0|")
            declaracion.setString(5, "176;100|158;1000|124;100|")
            declaracion.setString(6, gremio.getAnuncio())
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun REPLACE_GREMIO(gremio: Gremio) {
        val consultaSQL = "REPLACE INTO `gremios` VALUES(?,?,?,?,?,?,?,?,?,?);"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setInt(1, gremio.getID())
            declaracion.setString(2, gremio.getNombre())
            declaracion.setString(3, gremio.getEmblema())
            declaracion.setInt(4, gremio.getNivel())
            declaracion.setLong(5, gremio.getExperiencia())
            declaracion.setInt(6, gremio.getCapital())
            declaracion.setInt(7, gremio.getNroMaxRecau())
            declaracion.setString(8, gremio.compilarHechizo())
            declaracion.setString(9, gremio.compilarStats())
            declaracion.setString(10, gremio.getAnuncio())
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun DELETE_GREMIO(id: Int) {
        val consultaSQL = "DELETE FROM `gremios` WHERE `id` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setInt(1, id)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun REPLACE_MIEMBRO_GREMIO(miembro: MiembroGremio) {
        val consultaSQL = "REPLACE INTO `miembros_gremio` VALUES(?,?,?,?,?,?);"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setInt(1, miembro.getID())
            declaracion.setInt(2, miembro.getGremio().getID())
            declaracion.setInt(3, miembro.getRango())
            declaracion.setLong(4, miembro.getXpDonada())
            declaracion.setInt(5, miembro.getPorcXpDonada())
            declaracion.setInt(6, miembro.getDerechos())
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun DELETE_MIEMBRO_GREMIO(id: Int) {
        val consultaSQL = "DELETE FROM `miembros_gremio` WHERE `id` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setInt(1, id)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun DELETE_OTRO_INTERACTIVO(gfxID: Int, mapaID: Short, celdaID: Short, accion: Int) {
        val consultaSQL = "DELETE FROM `otros_interactivos` WHERE `gfx` = ? AND `mapaID` = ? AND `celdaID` = ? AND `accion` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setInt(1, gfxID)
            declaracion.setInt(2, mapaID)
            declaracion.setInt(3, celdaID)
            declaracion.setInt(4, accion)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun INSERT_OTRO_INTERACTIVO(gfxID: Int, mapaID: Short, celdaID: Short,
                                accionID: Int, args: String?, condiciones: String?, tiempoRecarga: Int, descripcion: String?) {
        val consultaSQL = "REPLACE INTO `otros_interactivos` VALUES (?,?,?,?,?,?,?,?);"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setInt(1, gfxID)
            declaracion.setInt(2, mapaID)
            declaracion.setInt(3, celdaID)
            declaracion.setInt(4, accionID)
            declaracion.setString(5, args)
            declaracion.setString(6, condiciones)
            declaracion.setInt(7, tiempoRecarga)
            declaracion.setString(8, descripcion)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            return
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
        return
    }

    fun REPLACE_ACCIONES_RESPUESTA(respuestaID: Int, accion: Int, args: String?,
                                   condicion: String?): Boolean {
        var consultaSQL = "REPLACE INTO `npc_respuestas` VALUES (?,?,?,?);"
        try {
            var declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setInt(1, respuestaID)
            declaracion.setInt(2, accion)
            declaracion.setString(3, args)
            declaracion.setString(4, condicion)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            consultaSQL = "UPDATE `npc_respuestas` SET `condicion` = ? WHERE `id` = ?;"
            declaracion = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setString(1, condicion)
            declaracion.setInt(2, respuestaID)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
        return false
    }

    fun DELETE_ACCIONES_RESPUESTA(respuestaID: Int): Boolean {
        val consultaSQL = "DELETE FROM `npc_respuestas` WHERE `id` = ? ;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setInt(1, respuestaID)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
        return false
    }

    fun UPDATE_NPC_PREGUNTA(id: Int, pregunta: Int): Boolean {
        val consultaSQL = "UPDATE `npcs_modelo` SET `pregunta` = ? WHERE `id` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setInt(1, pregunta)
            declaracion.setInt(2, id)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
        return false
    }

    fun REPLACE_PREGUNTA_NPC(pregunta: PreguntaNPC): Boolean {
        val consultaSQL = "REPLACE INTO `npc_preguntas` VALUES (?,?,?,?);"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setInt(1, pregunta.getID())
            declaracion.setString(2, pregunta.getStrRespuestas())
            declaracion.setString(3, pregunta.getParams())
            declaracion.setString(4, pregunta.getStrAlternos())
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
        return false
    }

    fun REPLACE_CASA(casa: Casa) {
        val consultaSQL = "REPLACE INTO `casas` VALUES (?,?,?,?,?,?);"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setInt(1, casa.getID())
            declaracion.setInt(2, if (casa.getDueño() != null) casa.getDueño().getID() else 0)
            declaracion.setLong(3, casa.getKamasVenta())
            declaracion.setByte(4, (if (casa.getActParametros()) 1 else 0).toByte())
            declaracion.setString(5, casa.getClave())
            declaracion.setInt(6, casa.getDerechosGremio())
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun UPDATE_CELDA_MAPA_DENTRO_CASA(casa: Casa) {
        val consultaSQL = "UPDATE `casas_modelo` SET `mapaDentro` = ?, `celdaDentro` = ? WHERE id = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setShort(1, casa.getMapaIDDentro())
            declaracion.setShort(2, casa.getCeldaIDDentro())
            declaracion.setInt(3, casa.getID())
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun REPLACE_OBJETOS_MERCADILLOS(lista: ArrayList<ObjetoMercadillo?>) {
        try {
            for (objMerca in lista) {
                estaticos.GestorSQL.REPLACE_OBJETO_MERCADILLO(objMerca)
            }
        } catch (e: Exception) {
        }
    }

    fun REPLACE_OBJETO_MERCADILLO(objMerca: ObjetoMercadillo): Boolean {
        val consultaSQL = "REPLACE INTO `mercadillo_objetos` (`objeto`,`mercadillo`,`cantidad`,`dueño`,`precio`) VALUES (?,?,?,?,?);"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            if (objMerca.getCuentaID() === 0) {
                return false
            }
            declaracion.setInt(1, objMerca.getObjeto().getID())
            declaracion.setInt(2, objMerca.getMercadilloID())
            declaracion.setLong(3, objMerca.getTipoCantidad(false))
            declaracion.setInt(4, objMerca.getCuentaID())
            declaracion.setLong(5, objMerca.getPrecio())
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            estaticos.GestorSQL.SALVAR_OBJETO(objMerca.getObjeto())
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
            return false
        }
        return true
    }

    fun DELETE_OBJ_MERCADILLO(idObjeto: Int) {
        val consultaSQL = "DELETE FROM `mercadillo_objetos` WHERE `objeto` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setInt(1, idObjeto)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun UPDATE_PRECIO_MEDIO_OBJETO_MODELO(objMod: ObjetoModelo) {
        val consultaSQL = "UPDATE `objetos_modelo` SET vendidos = ?, precioMedio = ? WHERE id = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setInt(1, objMod.getVendidos())
            declaracion.setLong(2, objMod.getPrecioPromedio())
            declaracion.setInt(3, objMod.getID())
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun UPDATE_MOB_IA_TALLA(mob: MobModelo) {
        val consultaSQL = "UPDATE `mobs_modelo` SET `tipoIA` = ?, `talla` = ? WHERE id = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setInt(1, mob.getTipoIA())
            declaracion.setInt(2, mob.getTalla())
            declaracion.setInt(3, mob.getID())
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun UPDATE_STATS_MOB(id: Int, stats: String?) {
        val consultaSQL = "UPDATE `mobs_modelo` SET `stats` = ? WHERE id = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setString(1, stats)
            declaracion.setInt(2, id)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun UPDATE_STATS_PUNTOS_PDV_XP_MOB(id: Int, stats: String?, pdv: String?, exp: String?, minKamas: String?,
                                       maxKamas: String?) {
        val consultaSQL = "UPDATE `mobs_modelo` SET `stats` = ?, `pdvs` = ?,`exps` = ? ,`minKamas` = ?,`maxKamas` = ? WHERE id = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setString(1, stats)
            declaracion.setString(2, pdv)
            declaracion.setString(3, exp)
            declaracion.setString(4, minKamas)
            declaracion.setString(5, maxKamas)
            declaracion.setInt(6, id)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun UPDATE_MOB_COLORES(mob: MobModelo) {
        val consultaSQL = "UPDATE `mobs_modelo` SET `colores` = ? WHERE `id` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setString(1, mob.getColores())
            declaracion.setInt(2, mob.getID())
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun UPDATE_MOB_AGRESION(mob: MobModelo) {
        val consultaSQL = "UPDATE `mobs_modelo` SET `agresion` = ? WHERE id = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setInt(1, mob.getDistAgresion())
            declaracion.setInt(2, mob.getID())
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun UPDATE_HECHIZO_AFECTADOS(id: Int, afectados: String?) {
        val consultaSQL = "UPDATE `hechizos` SET `afectados` = ? WHERE id = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setString(1, afectados)
            declaracion.setInt(2, id)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun UPDATE_HECHIZOS_VALOR_IA(id: Int, valorIA: Int) {
        val consultaSQL = "UPDATE `hechizos` SET `valorIA` = ? WHERE id = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setInt(1, valorIA)
            declaracion.setInt(2, id)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun ACTUALIZAR_CONDICIONES_HECHIZO(id: Int, condiciones: String?) {
        val consultaSQL = "UPDATE `hechizos` SET `condiciones` = ? WHERE id = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setString(1, condiciones)
            declaracion.setInt(2, id)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun UPDATE_STAT_HECHIZO(id: Int, stat: String?, grado: Int) {
        val consultaSQL = "UPDATE `hechizos` SET `nivel$grado` = ? WHERE id = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setString(1, stat)
            declaracion.setInt(2, id)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun ACTUALIZAR_SPRITE_INFO_HECHIZO(id: Int, str: String?) {
        val consultaSQL = "UPDATE `hechizos` SET `spriteInfos` = ? WHERE id = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setString(1, str)
            declaracion.setInt(2, id)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun ACTUALIZAR_SPRITE_ID_HECHIZO(id: Int, sprite: Int) {
        val consultaSQL = "UPDATE `hechizos` SET `sprite` = ? WHERE id = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setInt(1, sprite)
            declaracion.setInt(2, id)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun DELETE_MASCOTA(id: Int) {
        val consultaSQL = "DELETE FROM `mascotas` WHERE `objeto` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setInt(1, id)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun GET_NUEVA_FECHA_KEY(mapa: Short): String {
        var str = ""
        val consultaSQL = "SELECT * FROM `maps` WHERE `id` = '$mapa';"
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            while (resultado.next()) {
                try {
                    str = resultado.getString("fecha").toString() + "|" + resultado.getString("key") + "|" + resultado.getString("mapData")
                } catch (e: Exception) {
                }
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
        return str
    }

    fun UPDATE_FECHA_KEY_MAPDATA(mapa: Int, fecha: String?, key: String?,
                                 mapData: String?) {
        val consultaSQL = "UPDATE `mapas` SET `fecha` = ?, `key`= ?, `mapData`= ? WHERE `id` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setString(1, fecha)
            declaracion.setString(2, key)
            declaracion.setString(3, mapData)
            declaracion.setInt(4, mapa)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun UPDATE_KEY_MAPA(mapa: Short, key: String?) {
        val consultaSQL = "UPDATE `mapas` SET `key` = ? WHERE `id` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setString(1, key)
            declaracion.setShort(2, mapa)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun UPDATE_FECHA_MAPA(mapa: Int, fecha: String?) {
        val consultaSQL = "UPDATE `mapas` SET `fecha` = ? WHERE `id` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setString(1, fecha)
            declaracion.setInt(2, mapa)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun UPDATE_SET_MOBS_MAPA(mapa: Int, mob: String?) {
        val consultaSQL = "UPDATE `mapas` SET `mobs` = ? WHERE `id` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setString(1, mob)
            declaracion.setInt(2, mapa)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun DELETE_MOBS_FIX_MAPA(mapa: Int) {
        val consultaSQL = "DELETE FROM `mobs_fix` WHERE `mapa` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setInt(1, mapa)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun DELETE_ACCION_PELEA(mapa: Int) {
        val consultaSQL = "DELETE FROM `accion_pelea` WHERE `mapa` = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdEstatica)
            declaracion.setInt(1, mapa)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun CARGAR_LIVE_ACTION() {
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `live_action`;", estaticos.GestorSQL._bdDinamica)
            while (resultado.next()) {
                val objMod: ObjetoModelo = Mundo.getObjetoModelo(resultado.getInt("idModelo")) ?: continue
                val objNew: Objeto = objMod.crearObjeto(resultado.getInt("cantidad"), Constantes.OBJETO_POS_NO_EQUIPADO,
                        CAPACIDAD_STATS.RANDOM)
                objNew.convertirStringAStats(resultado.getString("stats"))
                val perso: Personaje = Mundo.getPersonaje(resultado.getInt("idPersonaje"))
                if (perso != null) {
                    perso.addObjetoConOAKO(objNew, true)
                    GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso, "Vous avez reçu " + resultado.getInt("cantidad").toString() + " "
                            + resultado.getString("nombreObjeto"))
                }
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
    }

    fun VACIAR_LIVE_ACTION() {
        val consultaSQL = "TRUNCATE `live_action`;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun DELETE_PRISMA(id: Int) {
        val consultaSQL = "DELETE FROM `prismas` WHERE id = ?;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setInt(1, id)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun REPLACE_PRISMA(prisma: Prisma) {
        val consultaSQL = "REPLACE INTO `prismas` VALUES(?,?,?,?,?,?,?,?,?);"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setInt(1, prisma.getID())
            declaracion.setInt(2, prisma.getAlineacion())
            declaracion.setInt(3, prisma.getNivel())
            declaracion.setInt(4, prisma.getMapa().getID())
            declaracion.setInt(5, prisma.getCelda().getID())
            declaracion.setInt(6, prisma.getHonor())
            declaracion.setInt(7, prisma.getSubArea().getID())
            declaracion.setInt(8, if (prisma.getArea() == null) -1 else prisma.getArea().getID())
            declaracion.setLong(9, prisma.getTiempoProteccion())
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    @SuppressWarnings("deprecation")
    fun INSERT_COMANDO_GM(rango: String?, comando: String?) {
        val consultaSQL = "INSERT INTO `comandos` (`nombre gm`,`comando`,`date`) VALUES (?,?,?);"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setString(1, rango)
            declaracion.setString(2, comando)
            declaracion.setString(3, Date().toLocaleString())
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    @SuppressWarnings("deprecation")
    fun INSERT_INTERCAMBIO(inte: String?) {
        val consultaSQL = "INSERT INTO `intercambios` (`intercambio`,`fecha`) VALUES (?,?);"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setString(1, inte)
            declaracion.setString(2, Date().toLocaleString())
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    @SuppressWarnings("deprecation")
    fun INSERT_REPORTE_BUG(nombre: String?, tema: String?, detalle: String?) {
        val consultaSQL = "INSERT INTO `reporte_bugs` (`perso`,`asunto`,`detalle`,`fecha`) VALUES (?,?,?,?);"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setString(1, nombre)
            declaracion.setString(2, tema)
            declaracion.setString(3, detalle)
            declaracion.setString(4, Date().toLocaleString())
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    @SuppressWarnings("deprecation")
    fun INSERT_PROBLEMA_OGRINAS(nombre: String?, tema: String?, detalle: String?) {
        val consultaSQL = "INSERT INTO `problema_ogrinas` (`perso`,`asunto`,`detalle`,`fecha`) VALUES (?,?,?,?);"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setString(1, nombre)
            declaracion.setString(2, tema)
            declaracion.setString(3, detalle)
            declaracion.setString(4, Date().toLocaleString())
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    @SuppressWarnings("deprecation")
    fun INSERT_DENUNCIAS(nombre: String?, tema: String?, detalle: String?) {
        val consultaSQL = "INSERT INTO `denuncias` (`perso`,`asunto`,`detalle`,`fecha`) VALUES (?,?,?,?);"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setString(1, nombre)
            declaracion.setString(2, tema)
            declaracion.setString(3, detalle)
            declaracion.setString(4, Date().toLocaleString())
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    @SuppressWarnings("deprecation")
    fun INSERT_SUGERENCIAS(nombre: String?, tema: String?, detalle: String?) {
        val consultaSQL = "INSERT INTO `sugerencias` (`perso`,`asunto`,`detalle`,`fecha`) VALUES (?,?,?,?);"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            declaracion.setString(1, nombre)
            declaracion.setString(2, tema)
            declaracion.setString(3, detalle)
            declaracion.setString(4, Date().toLocaleString())
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
    }

    fun DELETE_REPORTE(tipo: Byte, id: Int): Boolean {
        val tipos = arrayOf("reporte_bugs", "sugerencias", "denuncias", "problema_ogrinas")
        val consultaSQL = "DELETE FROM `" + tipos[tipo.toInt()] + "` WHERE `id` = '" + id + "';"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
        return false
    }

    fun GET_DESCRIPTION_REPORTE(tipo: Byte, id: Int): String {
        var str = ""
        try {
            val tipos = arrayOf("reporte_bugs", "sugerencias", "denuncias", "problema_ogrinas")
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL("SELECT * FROM `" + tipos[tipo.toInt()] + "` WHERE `id` = '" + id + "';",
                    estaticos.GestorSQL._bdDinamica)
            while (resultado.next()) {
                str = ("<b>" + resultado.getString("perso").toString() + "</b> - <i><u>" + resultado.getString("asunto").toString() + "</i></u>: "
                        + resultado.getString("detalle"))
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
        return str
    }

    fun GET_LISTA_REPORTES(cuenta: Cuenta): String {
        val str = StringBuilder()
        try {
            var resultado: ResultSet
            resultado = estaticos.GestorSQL.consultaSQL("SELECT * FROM `reporte_bugs` LIMIT " + MainServidor.LIMITE_REPORTES.toString() + ";", estaticos.GestorSQL._bdDinamica)
            var str2 = StringBuilder()
            while (resultado.next()) {
                if (str2.length() > 0) {
                    str2.append("#")
                }
                str2.append(resultado.getInt("id").toString() + ";" + resultado.getString("perso") + ";" + resultado.getString("asunto")
                        + ";" + resultado.getString("fecha") + ";" + if (cuenta.tieneReporte(Constantes.REPORTE_BUGS, resultado.getInt(
                                "id"))) 1 else 0)
            }
            str.append(str2.toString().toString() + "|")
            estaticos.GestorSQL.cerrarResultado(resultado)
            resultado = estaticos.GestorSQL.consultaSQL("SELECT * FROM `sugerencias` LIMIT " + MainServidor.LIMITE_REPORTES.toString() + ";", estaticos.GestorSQL._bdDinamica)
            str2 = StringBuilder()
            while (resultado.next()) {
                if (str2.length() > 0) {
                    str2.append("#")
                }
                str2.append(resultado.getInt("id").toString() + ";" + resultado.getString("perso") + ";" + resultado.getString("asunto")
                        + ";" + resultado.getString("fecha") + ";" + if (cuenta.tieneReporte(Constantes.REPORTE_SUGERENCIAS, resultado
                                .getInt("id"))) 1 else 0)
            }
            str.append(str2.toString().toString() + "|")
            estaticos.GestorSQL.cerrarResultado(resultado)
            resultado = estaticos.GestorSQL.consultaSQL("SELECT * FROM `denuncias` LIMIT " + MainServidor.LIMITE_REPORTES.toString() + ";", estaticos.GestorSQL._bdDinamica)
            str2 = StringBuilder()
            while (resultado.next()) {
                if (str2.length() > 0) {
                    str2.append("#")
                }
                str2.append(resultado.getInt("id").toString() + ";" + resultado.getString("perso") + ";" + resultado.getString("asunto")
                        + ";" + resultado.getString("fecha") + ";" + if (cuenta.tieneReporte(Constantes.REPORTE_DENUNCIAS, resultado
                                .getInt("id"))) 1 else 0)
            }
            str.append(str2.toString().toString() + "|")
            estaticos.GestorSQL.cerrarResultado(resultado)
            resultado = estaticos.GestorSQL.consultaSQL("SELECT * FROM `problema_ogrinas` LIMIT " + MainServidor.LIMITE_REPORTES.toString() + ";",
                    estaticos.GestorSQL._bdDinamica)
            str2 = StringBuilder()
            while (resultado.next()) {
                if (str2.length() > 0) {
                    str2.append("#")
                }
                str2.append(resultado.getInt("id").toString() + ";" + resultado.getString("perso") + ";" + resultado.getString("asunto")
                        + ";" + resultado.getString("fecha") + ";" + if (cuenta.tieneReporte(Constantes.REPORTE_OGRINAS, resultado.getInt(
                                "id"))) 1 else 0)
            }
            str.append(str2.toString())
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
        return str.toString()
    }

    fun ASIGNAR_REGALO_RANKING_KOLISEO() {
        val consultaSQL = "select c.cuenta,c.regalo from ranking_koliseo r inner join personajes p on p.id=r.id INNER JOIN cuentas_servidor c on p.cuenta = c.id WHERE victorias>0 ORDER BY r.victorias DESC, r.derrotas asc limit 3;;"
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            while (resultado.next()) {
                try {
                    val cuenta: String = resultado.getString("cuenta")
                    var regalo: String = resultado.getString("regalo")
                    if (regalo.isEmpty()) {
                        regalo = MainServidor.REGALO_RANKING_KOLISEO.toString() + ""
                    } else {
                        regalo += "," + MainServidor.REGALO_RANKING_KOLISEO
                    }
                    estaticos.GestorSQL.SET_REGALO(cuenta, regalo)
                } catch (ignored: Exception) {
                }
            }
            estaticos.GestorSQL.DELETE_RANKING_KOLISEO()
            MainServidor.modificarParam("RESET_RANKING_KOLISEO", "0")
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
    }

    fun ASIGNAR_REGALO_RANKING_COMPENSACION() {
        val consultaSQL = "select c.cuenta,c.regalo from personajes p INNER JOIN cuentas_servidor c on p.cuenta = c.id WHERE p.nivelomega>339;"
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            while (resultado.next()) {
                try {
                    val cuenta: String = resultado.getString("cuenta")
                    var regalo: String = resultado.getString("regalo")
                    if (regalo.isEmpty()) {
                        regalo = "101301"
                    } else {
                        regalo += ",101301"
                    }
                    estaticos.GestorSQL.SET_REGALO(cuenta, regalo)
                } catch (ignored: Exception) {
                }
            }
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
    }

    fun DELETE_RANKING_KOLISEO(): Boolean {
        val consultaSQL = "DELETE FROM `ranking_koliseo`;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
        return false
    }

    fun ASIGNAR_REGALO_RANKING_PVP() {
        val consultaSQL = "select c.cuenta,c.regalo from ranking_pvp r inner join personajes p on p.id=r.id INNER JOIN cuentas_servidor c on p.cuenta = c.id WHERE victorias>0  ORDER BY r.victorias DESC, r.derrotas asc limit 3;"
        try {
            val resultado: ResultSet = estaticos.GestorSQL.consultaSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            var i = 0
            while (resultado.next()) {
                try {
                    val cuenta: String = resultado.getString("cuenta")
                    var regalo: String = resultado.getString("regalo")
                    if (regalo.isEmpty()) {
                        regalo = MainServidor.REGALO_RANKING_PVP.get(i).toString() + ""
                    } else {
                        regalo += "," + MainServidor.REGALO_RANKING_PVP.get(i)
                    }
                    i++
                    estaticos.GestorSQL.SET_REGALO(cuenta, regalo)
                } catch (ignored: Exception) {
                }
            }
            estaticos.GestorSQL.DELETE_RANKING_PVP()
            MainServidor.modificarParam("RESET_RANKING_PVP", "0")
            estaticos.GestorSQL.cerrarResultado(resultado)
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionNormal(e, "")
        }
    }

    fun REINICIAR_MERCANTES(): Boolean {
        val consultaSQL = "UPDATE `personajes` SET `mercante` = 0;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
        return false
    }

    fun DELETE_RANKING_PVP(): Boolean {
        val consultaSQL = "DELETE FROM `ranking_pvp`;"
        try {
            val declaracion: PreparedStatement = estaticos.GestorSQL.transaccionSQL(consultaSQL, estaticos.GestorSQL._bdDinamica)
            estaticos.GestorSQL.ejecutarTransaccion(declaracion)
            estaticos.GestorSQL.cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            estaticos.GestorSQL.exceptionModify(e, consultaSQL, "")
        }
        return false
    }
}