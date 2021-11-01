package estaticos

import java.io.BufferedReader

/**
 *
 */
object MainServidor {
    private val ARCHIVO_CONFIG: String? = "config_Servidor.txt"
    val calendar: Calendar? = Calendar.getInstance()
    var NIVEL_MAX_OMEGA = 701
    private var LOG_SERVIDOR: PrintStream? = null
    var ptsPvP = 2
    var ptsRecaudador = 2
    var ptsDailyQuest = 5
    var ptsJefe = 2
    var ptsRaid = 3
    var ptsRaid1 = 5
    var ptsRaid2 = 8
    var ptsRaid3 = 12
    var ptsOfrenda = 15
    var ptsNPC = 20
    var ptsMision = 2
    var TorneoOn = true //permite inscripciones, on,off
    var empezoTorneo = false
    var faseTorneo = 0
    var _globalTime: Timer? = null
    var torneoR = 10
    var ES_LOCALHOST = false
    var PARAMS_OBJETO_FABRUSHIO = false
    var PARAMS_MOSTRAR_TITULO_VIP = true
    var PARAMS_EQUIPAR_SET_PELEA = false
    var PARAMS_SCROLL_NO_RESET = false
    var ELIMINAR_MUERTO = false
    var DOBLE_EXPERIENCIA = true

    //public static boolean PARAMS_BORRAR_STATS_COLOR = false;
    var RESET_NO_ALAS = false
    var tallerAbierto = true
    var LIMITE_STATS_PVP_PERSONALIZADOS = false
    var LIMITE_STATS_PERCO_PERSONALIZADOS = false
    var LIMITE_STATS_KOLI_PERSONALIZADOS = false
    var AUTO_AGREDIR_MISION = false
    var MODO_EXILED = false
    var MODO_ALL_OGRINAS = false
    var PARAM_MERCA_OGRINAS = false
    var LIMITE_ARTESANOS_TALLER = 25
    var MODO_NO_MOSTRAR_PANEL_ESCLAVOS = false
    var MODO_CANAL_VIP_INCARMAN = false
    var MODO_IMPACT = false
    var COMPRAR_OBJETOS_VIP_NO_PERFECTOS = false
    var MODO_SOLO_PVP = false
    var SON_DE_LUCIANO = false
    var PARAMS_XP_DESAFIO = false
    var MAX_PACKETS_PARA_RASTREAR = 10
    var RESET_APRENDER_OMEGA = 11
    var MAX_CARACTERES_SONIDO = 50
    var MAX_PACKETS_DESCONOCIDOS = 5
    var CLON_PLACAHE_HUIDA = 0
    var BONUS_UNICA_IP = 0f
    var URL_LINK_COMPRA: String? = ""
    var URL_LINK_VOTO: String? = ""
    var URL_LINK_BUG: String? = ""
    var URL_IMAGEN_VOTO: String? = ""
    var URL_BACKUP_PHP: String? = ""
    var URL_DETECTAR_DDOS: String? = ""
    var URL_LINK_MP3: String? = "http://localhost/mp3/"
    var DIRECTORIO_LOCAL_MP3: String? = "C://wamp/www/mp3/"
    var INVASION_MAPAS_NO_PERMITIDOS: String? = ""
    var COMANDOS_PERMITIDOS: ArrayList<String?>? = ArrayList()
    var EVENTO_MAPA_NO_PERMITIDOS: ArrayList<Integer?>? = ArrayList()
    var MAPAS_PVP: CopyOnWriteArrayList<Integer?>? = CopyOnWriteArrayList()
    var MOBS_JEFE: CopyOnWriteArrayList<Integer?>? = CopyOnWriteArrayList()
    var COMANDOS_VIP: ArrayList<String?>? = ArrayList()
    var ID_MIMOBIONTE = -1
    var ID_ORBE = 0
    var INDEX_IP = 0
    var TIME_SLEEP_PACKETS_CARGAR_MAPA = 50
    var CANTIDAD_GRUPO_MOBS_MOVER_POR_MAPA = 5
    var VECES_PARA_BAN_IP_SIN_ESPERA = 3
    var NIVEL_INTELIGENCIA_ARTIFICIAL = 8
    var OBJETOS_POS_AYUDANTE: Byte = Constantes.OBJETO_POS_COMPAÑERO

    // reset ranking
    var RESET_RANKING_PVP = -1
    var RESET_RANKING_KOLISEO = -1
    var REGALO_RANKING_KOLISEO = 0

    // npc ranking pvp
    var NPC_RANKING_PVP = -1

    // npc ranking koliseo
    var NPC_RANKING_KOLISEO = -1

    // horas
    var HORA_NOCHE = 2
    var EXPERIENCIA_CANTIDAD = 2
    var MINUTOS_NOCHE = 0
    var HORA_DIA = 14
    var MINUTOS_DIA = 0

    // public static short PODER_PRISMA = 100;
    // public static short PDV_PRISMA = 1000;
    // public static short DIVISOR_PP = 2000;
    var ACTIVAR_CONSOLA = true
    var MOSTRAR_RECIBIDOS = false
    var MOSTRAR_ENVIOS = false
    var MOSTRAR_SINCRONIZACION = false

    // public static boolean REGISTER_SENDING = false;
    // public static boolean REGISTER_RECIVED = true;
    // MODOS
    var MODO_DEBUG = false
    var MODO_IMPRIMIR_LOG = true
    var MODO_MAPAS_LIMITE = false
    var MODO_PVP = false
    var REFRESCA_PANEL_KOLISEO = false
    var MODO_PVP_AGRESIVO = false
    var MODO_PVP_STRONG = false
    var MODO_PUEDE_AGREDIR_OCUPADO = false
    var PARAM_MOSTRAR_MENSAJE_NIVEL_MAXIMO = true
    var MODO_HEROICO = false
    var MODO_ANKALIKE = false
    var MODO_BATTLE_ROYALE = false
    var MODO_BETA = false
    var MODO_DESCONECTAR_PACKET_MALO = false
    var NPC_BOUTIQUE: NPC? = null
    var NPC_EVENTO: NPC? = null
    var ID_NPC_BOUTIQUE = 0
    var MOB_AUMENTO_RESET = 0
    var EVENTO_NPC_2 = 0
    var LIMITE_VIDA = 0
    var LIMITE_VIDA_KOLISEO = 0
    var LIMITE_VIDA_PERCO = 0
    var LIMITE_CURAS = 0
    var LIMITE_REENVIO = 0
    var LIMITE_LOCURA = 0
    var LIMITE_VIDA_INVO_OSA = 0
    var LIMITE_VIDA_INVO_KOLI_OSA = 0
    var LIMITE_VIDA_INVO = 0
    var LOGRO_REGALO_FIN = 0
    var LIMITE_VIDA_INVO_KOLI = 0
    var LIMITE_VIDA_BUFF = 0
    var LIMITE_VIDA_INVO_BUFF = 0
    var ID_BOLSA_OGRINAS = 0
    var ID_VENTA_MERCANTE = 0
    var ID_BOLSA_CREDITOS = 0
    var IMPUESTO_BOLSA_OGRINAS = 1
    var IMPUESTO_BOLSA_CREDITOS = 1
    var DURABILIDAD_REDUCIR_OBJETO_CRIA = 10
    var MODO_PERFECCION_OBJETOS = 0
    var DIAS_PARA_BORRAR: Short = 60
    var PALABRA_CLAVE_CONSOLA: String? = ""
    var PERMITIR_MULTIMAN: String? = "0,4"
    var SISTEMA_ITEMS_PERFECTO_MULTIPLICA_POR = 2f
    var SISTEMA_ITEMS_EXO_PA_PRECIO: Short = 100
    var SISTEMA_ITEMS_EXO_PM_PRECIO: Short = 100
    var SISTEMA_ITEMS_TIPO_DE_PAGO: String? = "OGRINAS"
    var SISTEMA_ITEMS_EXO_TIPOS_NO_PERMITIDOS: ArrayList<Short?>? = ArrayList()
    var MAPAS_MODO_HEROICO: ArrayList<Integer?>? = ArrayList()
    var STATS_NO_FORTICABLES: ArrayList<Integer?>? = ArrayList()
    var OBJETOS_NO_LUPEAR: ArrayList<Integer?>? = ArrayList()
    var RUNAS_NO_PERMITIDAS: ArrayList<Integer?>? = ArrayList()
    var PARAMS_ITEM_PILA: ArrayList<Integer?>? = ArrayList()
    var REGALO_RANKING_PVP: ArrayList<Integer?>? = ArrayList<Integer?>()
    var HECHIZOS_NO_KOLI_PVP: ArrayList<Integer?>? = ArrayList()
    var MOBS_DOBLE_ORBES: ArrayList<Integer?>? = ArrayList()
    var MOBS_NO_ORBES: ArrayList<Integer?>? = ArrayList()
    var MOBS_TRAMPOSOS: ArrayList<Integer?>? = ArrayList()
    var IDS_NPCS_VENDE_OBJETOS_STATS_MAXIMOS: ArrayList<Integer?>? = ArrayList()
    var IDS_OBJETOS_STATS_MAXIMOS: ArrayList<Integer?>? = ArrayList()
    var IDS_OBJETOS_STATS_RANDOM: ArrayList<Integer?>? = ArrayList()
    var IDS_OBJETOS_STATS_MINIMOS: ArrayList<Integer?>? = ArrayList()
    var STATS_HEREDADOS_INVOS: ArrayList<Integer?>? = ArrayList()
    var SALVAR_LOGS_TIPO_COMBATE: ArrayList<Byte?>? = ArrayList()
    var PERMITIR_MULTIMAN_TIPO_COMBATE: ArrayList<Byte?>? = ArrayList()
    var OGRINAS_CREAR_CLASE: Map<Byte?, Integer?>? = TreeMap()
    var JEFE_HECHIZO: Map<Integer?, Integer?>? = TreeMap()
    var JEFE_AYUDANTE: Map<Integer?, String?>? = TreeMap()
    var ADMIN_CLASE: Map<Byte?, Integer?>? = TreeMap()
    var PRECIOS_SERVICIOS: Map<String?, String?>? = TreeMap()
    var COLOR_CELDAS_PELEA_AGRESOR: String? = ""
    var MAX_GOLPES_CAC: Map<Integer?, Integer?>? = TreeMap<Integer?, Integer?>()
    var ITEM_SOLO_POS: Map<Integer?, Integer?>? = TreeMap<Integer?, Integer?>()
    var STATS_POR_NIVEL: Map<Byte?, Integer?>? = TreeMap<Byte?, Integer?>()
    var GOLPES_MAPAS: Map<Integer?, Integer?>? = TreeMap<Integer?, Integer?>()
    var OBJETOS_OGRINAS_NPC: Map<Integer?, Integer?>? = TreeMap<Integer?, Integer?>()
    var MAPAS_PERSONAJE: Map<Integer?, Integer?>? = TreeMap<Integer?, Integer?>()
    var LIMITE_BUFF_PVP: Map<Integer?, Integer?>? = TreeMap<Integer?, Integer?>()
    var LIMITE_BUFF_KOLISEO: Map<Integer?, Integer?>? = TreeMap<Integer?, Integer?>()
    var LIMITE_BONUS_2: Map<Integer?, Integer?>? = TreeMap<Integer?, Integer?>()
    var LIMITE_BONUS_VALOR: Map<Integer?, Integer?>? = TreeMap<Integer?, Integer?>()
    var LIMITE_VIDA_CLASE: Map<Byte?, Integer?>? = TreeMap<Byte?, Integer?>()
    var LIMITE_VIDA_CLASE_KOLISEO: Map<Byte?, Integer?>? = TreeMap<Byte?, Integer?>()
    var MAX_GOLPES_CAC_PVM: Map<Integer?, Integer?>? = TreeMap<Integer?, Integer?>()
    var HECHIZOS_CLASE_UNICOS: Map<Integer?, Integer?>? = TreeMap<Integer?, Integer?>()
    var HECHIZOS_CLASE_UNICOS2: Map<Integer?, Integer?>? = TreeMap<Integer?, Integer?>()

    // CREAR TU ITEM
    var GFX_CREA_TU_ITEM_CAPAS: String? = "1,2,3,4,5,7,8,9,10,11,12,15,16,17,18,19,21,22,23,33,34,35,36,37,38,39,40,41,42,43,44,46,47,48,49,50,51,52,53,54,55,56,58,59,60,61,62,63,64,65,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,89,90,91,92,93,94,95,96,97,98,99,100,101,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,136,137,138,139,140,141,142,143,144,145,146,147,148,150,151,152,153,154,155,156,157,158,159,160,161,162,163,164,165,166,167,168,169,171,172,173,174,175,176,177,178,179,180,181,182,183,184,185,186,187,189,190,191,192,193,194,195,196,197,198,199,200,201,202,203,204,205,206,207,208,209,210,211,212,213,214,215,216,217,218,219,220,221,222,223,224,225,226,227,230,231,232,233,234,236,237,238,239,240,241,242,243,244,245,246,247,248,249,250,251,252,253,254,255,256,257,258,259"
    var GFX_CREA_TU_ITEM_AMULETOS: String? = "1,2,3,4,5,6,7,8,9,10,11,12,13,15,16,17,18,19,20,22,23,24,25,26,27,28,29,30,31,32,33,34,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,136,137,138,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159,160,161,162,163,164,165,166,167,168,169,170,171,172,173,174,175,176,177,178,179,180,181,182,183,184,185,186,187,188,189,190,191,192,193,194,195,196,197,198,199,200,201,202,203,204,205,206,207,208,209,210,211,212,213,214,215,216,217,218,219,220,221,222,223,224,225"
    var GFX_CREA_TU_ITEM_ANILLOS: String? = "1,2,3,4,5,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159,160,162,163,164,165,166,167,168,169,170,171,172,173,174,175,176,177,178,179,180,181,182,183,184,185,186,187,188,189,190,191,192,193,194,195,196,197,198,199,200,201,202,203,204,205,206,207,208,209,210,211,212,213,214,215,216,217,218,219,220,221,222,223,224,225,226,227,228,229,230,231,232,233,234,235,236,237,238,239,240,241,242,243,244,245,246,247,248,249,250,251,252,253,254,255,256"
    var GFX_CREA_TU_ITEM_CINTURONES: String? = "3,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159,160,161,162,163,164,165,166,167,168,169,170,171,172,173,174,175,176,177,178,179,180,181,182,183,184,185,186,187,188,189,190,191,192,193,194,195,196,197,198,199,200,201,202,203,204,205,206,207,208,209,210,211,212,213,214,215,216,217,218,219,220,221,222,223,224,225,226,227,228,229,230,231,232,233,234,235,236,237"
    var GFX_CREA_TU_ITEM_BOTAS: String? = "1,2,3,4,5,6,7,8,9,10,11,12,14,15,16,17,18,19,20,21,22,23,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,155,156,157,158,159,160,161,162,163,164,165,166,167,168,169,170,171,172,173,174,175,176,177,178,179,180,181,182,183,184,185,186,187,188,189,190,191,192,193,194,195,196,197,198,199,200,201,202,203,204,205,206,207,208,209,210,211,212,213,214,215,216,217,218,219,220,221,222,223,224,225,226,227,228,229,230"
    var GFX_CREA_TU_ITEM_SOMBREROS: String? = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,20,21,22,23,24,25,26,27,28,29,30,31,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,61,64,65,66,67,68,69,70,71,72,73,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,102,103,108,109,110,111,112,114,115,116,117,118,119,120,121,122,123,124,125,127,128,129,130,131,132,133,134,135,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159,160,166,167,168,169,170,171,172,173,174,175,176,177,178,179,180,181,182,183,184,185,186,187,188,189,190,191,192,193,194,195,196,197,199,200,201,202,203,204,205,206,207,208,209,210,212,213,214,215,216,217,218,219,220,221,222,223,224,225,226,227,228,229,230,231,232,233,234,235,236,237,238,239,240,241,242,243,244,245,246,247,248,249,250,251,252,253,254,255,256,257,259,260,261,262,263,264,265,266,268,269,270,271,272,273,274,275,276,277,278,279,280,281,282,283,284,285,286,287,288,289,290,291,292,293,294,295,296,297,300,301,302,304,305,306,307,308,309,310,313,314,315,316,317,318,319,320,321,322,323,324,325,326,327,328,329,330,331,332,333,334,335,336,337,339,340,341,342,343"
    var GFX_CREA_TU_ITEM_ESCUDOS: String? = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,71,72,73,74"
    var GFX_CREA_TU_ITEM_DOFUS: String? = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18"

    // BONUS RESETS
    var SUFIJO_RESET: String? = "R"
    var BONUS_RESET_PUNTOS_HECHIZOS: Short = 3
    var BONUS_RESET_PUNTOS_STATS: Short = 200

    // KAMAS
    var KAMAS_RULETA_JALATO = 1000
    var KAMAS_BANCO = 0
    var KAMAS_MOSTRAR_PROBABILIDAD_FORJA = 0

    // OGRINAS
    var OGRINAS_POR_VOTO: Short = -1
    var VALOR_KAMAS_POR_OGRINA = 0
    var DIAS_INTERCAMBIO_COMPRAR_SISTEMA_ITEMS = 0

    // ALMANAX
    var ALMANAX_OBJETO_CANTIDAD = 1

    // LOTERIA
    var PRECIO_LOTERIA = 5
    var PREMIO_LOTERIA = 50
    var GANADORES_POR_BOLETOS = 20

    // SISTEMA OGRINAS
    //
    var STR_MAPAS_LIMITE: String? = "7411,8534,951"
    var STR_SUBAREAS_LIMITE: String? = "1,2"
    var MAPAS_KOLISEO: String? = "951,7449"
    var MENSAJE_BIENVENIDA: String? = ""
    var PANEL_BIENVENIDA: String? = ""
    var PANEL_DESPUES_CREAR_PERSONAJE: String? = ""
    var TUTORIAL_FR: String? = ""
    var TUTORIAL_ES: String? = ""
    var MENSAJE_SERVICIOS: String? = "Mensaje de lista de servicios del servidor"
    var MENSAJE_COMANDOS: String? = "Mensaje de lista de comandos del servidor"
    var MENSAJE_VIP: String? = "Beneficios de ser vip"
    var MENSAJE_ERROR_OGRINAS_CREAR_CLASE: String? = "PRICE OGRINES"
    var MENSAJE_ERROR_ADMIN_CREAR_CLASE: String? = "No puedes crear esta clase no eres admin"
    var CANALES_COLOR_CHAT: String? = ""

    // public static String COLOR_CHAT_ALL = "#777777";
    // MAPA CELDA
    var CERCADO_MAPA_CELDA: String? = ""
    var START_MAPA_CELDA: String? = "7411,340"
    var SHOP_MAPA_CELDA: String? = ""
    var TIENDAFUS_MAPA_CELDA: String? = ""
    var PVP_MAPA_CELDA: String? = ""

    // MUTE
    var MUTE_CANAL_INCARNAM = false
    var MUTE_CANAL_COMERCIO = false
    var MUTE_CANAL_ALINEACION = false
    var MUTE_CANAL_RECLUTAMIENTO = false

    // PARAMETROS
    var PARAM_NERFEAR_STATS_EXOTICOS = false
    var PARAM_APRENDER_HECHIZO = false
    var VENDER_MERCANTE_OBJETO = -1
    var PARAM_VIP_CRAFT_SPEED = false
    var PARAM_CRAFT_SPEED = false
    var PARAM_CORREGIR_NOMBRE_JUGADOR = true
    var PARAM_AGREDIR_MISMO_RESET = false
    var PARAM_VARIAS_ACCIONES = false
    var PARAM_AGREDIR_RESET_ALTERNOS = false
    var PARAM_KOLI_MISMO_RESET = false
    var PARAM_ANTIFLOOD = false
    var PARAM_LIMITAR_RECAUDADOR_GREMIO_POR_ZONA = false
    var PARAM_RESETEAR_LUPEAR_OBJETOS_MAGUEADOS = false
    var PARAM_DESHABILITAR_SQL = false
    var PARAM_FM_CON_POZO_RESIDUAL = false
    var PARAM_MOSTRAR_STATS_INVOCACION = false
    var PARAM_ENCRIPTAR_PACKETS = false
    var PARAM_BORRAR_PIEDRA_MISION = false
    var PARAM_PERMITIR_ORNAMENTOS = false
    var PARAM_RESTRINGIR_COLOR_DIA = false
    var PARAM_REINICIAR_ESTRELLAS_SI_LLEGA_MAX = false
    var PARAM_CLASIFICAR_POR_STUFF_EN_KOLISEO = false
    var PARAM_CLASIFICAR_POR_RANKING_EN_KOLISEO = false
    var PARAM_MOSTRAR_APODO_LISTA_AMIGOS = true
    var PARAM_MOSTRAR_EXP_MOBS = true
    var PARAM_PERMITIR_MISMAS_CLASES_EN_KOLISEO = true
    var PARAM_PERMITIR_DESACTIVAR_ALAS = true
    var PARAM_AGREDIR_ALAS_DESACTIVADAS = true
    var PARAM_PERMITIR_MULTICUENTA_PELEA_KOLISEO = false
    var PARAM_PERMITIR_MULTICUENTA_PELEA_RECAUDADOR = true
    var PARAM_PERMITIR_MULTICUENTA_PELEA_PVP = false
    var PARAM_SISTEMA_IP_ESPERA = false
    var PARAM_BORRAR_CUENTAS_VIEJAS = false
    var PARAM_AUTO_COMMIT = false
    var PARAM_AGREDIR_NEUTRAL = true
    var PARAM_MOVER_MOBS_FIJOS = true
    var PARAM_MOBS_RANDOM_REAPARECER_OTRA_CELDA = true
    var PARAM_CRIAR_MONTURA = true
    var PARAM_TIMER_ACCESO = true
    var PARAM_START_EMOTES_COMPLETOS = false
    var PARAM_SOLO_PRIMERA_VEZ = false
    var PARAM_PVP = true
    var PARAM_PERMITIR_MOBS = true
    var PARAM_ACTIVAR_AURA = true
    var PARAM_AURA_VIP = true
    var PARAM_AURA_VIP_2 = false
    var PARAM_PERDER_ENERGIA = true
    var PARAM_COMANDOS_JUGADOR = true
    var PARAM_ALMANAX = false
    var PARAM_ESTRELLAS_RECURSOS = false
    var PARAM_HEROICO_PIERDE_ITEMS_VIP = false
    var PARAM_LOTERIA = false
    var PARAM_LOTERIA_OGRINAS = true
    var PARAM_PERDER_PDV_ARMAS_ETEREAS = true
    var PARAM_HEROICO_GAME_OVER = true
    var PARAM_DEVOLVER_OGRINAS = false
    var PARAM_KOLISEO = false
    var PARAM_LADDER_NIVEL = false
    var PARAM_LADDER_KOLISEO = false
    var PARAM_LADDER_PVP = false
    var PARAM_LADDER_GREMIO = false
    var PARAM_LADDER_EXP_DIA = false
    var PARAM_LADDER_STAFF = false
    var PARAM_ANTI_SPEEDHACK = false
    var PARAM_MOSTRAR_CHAT_VIP_TODOS = false
    var PARAM_DESACTIVAR_MERCANTE = false
    var MODO_ESTADO_PESADO = false

    // public static boolean PARAM_CREAR_ITEM;
    var PARAM_VER_JUGADORES_KOLISEO = false

    // public static boolean PARAM_SISTEMA_OBJETOS_POR_OGRINAS;
    var PARAM_PRECIO_RECURSOS_EN_OGRINAS = false
    var PARAM_BESTIARIO = false
    var PARAM_TODOS_MOBS_EN_BESTIARIO = false
    var PARAM_AUTO_RECUPERAR_TODA_VIDA = false
    var PARAM_CRAFT_SIEMPRE_EXITOSA = false
    var PARAM_CRAFT_PERFECTO_STATS = false
    var PARAM_MONTURA_SIEMPRE_MONTABLES = false
    var PARAM_JUGAR_RAPIDO = false
    var PARAM_ANTI_DDOS = false
    var PARAM_MOSTRAR_NRO_TURNOS = false
    var PARAM_RESET_STATS_OBJETO = false
    var PARAM_OBJETOS_PEFECTOS_COMPRADOS_NPC = false
    var PARAM_DAR_ALINEACION_AUTOMATICA = false
    var PARAM_CINEMATIC_CREAR_PERSONAJE = true
    var PARAM_REGISTRO_LOGS_JUGADORES = false
    var PARAM_REGISTRO_LOGS_SQL = false
    var PARAM_NOMBRE_COMPRADOR = false
    var PARAM_OBJETOS_OGRINAS_LIGADO = false
    var PARAM_VARIOS_RECAUDADORES = false
    var PARAM_ELIMINAR_PERSONAJES_BUG = false
    var PARAM_AGREDIR_JUGADORES_ASESINOS = true
    var PARAM_MOSTRAR_IP_CONECTANDOSE = false
    var PARAM_MENSAJE_ASESINOS_HEROICO = true
    var PARAM_CONTRA_DANO = true
    var PARAM_MENSAJE_ASESINOS_PVP = false
    var PARAM_MENSAJE_ASESINOS_KOLISEO = false
    var PARAM_GUARDAR_LOGS_INTERCAMBIOS = true
    var PARAM_FORMULA_TIPO_OFICIAL = false
    var PARAM_STOP_SEGUNDERO = false
    var PARAM_BOTON_BOUTIQUE = false
    var PARAM_SISTEMA_ITEMS_SOLO_PERFECTO = false
    var PARAM_SISTEMA_ITEMS_EXO_PA_PM = false
    var PARAM_GANAR_HONOR_RANDOM = false
    var PARAM_RESET_STATS_PLAYERS = false
    var PARAM_AGRESION_ADMIN = true
    var PARAM_AUTO_SALTAR_TURNO = false
    var PARAM_TITULO_MAESTRO_OFICIO = true
    var PARAM_GANAR_KAMAS_PVP = true
    var PARAM_GANAR_EXP_PVP = true
    var PARAM_ALIMENTAR_MASCOTAS = true
    var PARAM_MASCOTAS_PERDER_VIDA = true
    var PARAM_LIMITE_MIEMBROS_GREMIO = true
    var PARAM_MOSTRAR_PROBABILIDAD_TACLEO = false
    var PARAM_SISTEMA_ORBES = false
    var PARAM_MATRIMONIO_GAY = false
    var PARAM_PERMITIR_OFICIOS = true
    var PARAM_SALVAR_LOGS_AGRESION_SQL = false
    var PARAM_MOB_TENER_NIVEL_INVOCADOR_PARA_EMPUJAR = false
    var PARAM_NO_USAR_OGRINAS = false
    var PARAM_NO_USAR_CREDITOS = false
    var PARAM_PERMITIR_DESHONOR = true
    var PARAM_PERMITIR_AGRESION_MILICIANOS = true
    var PARAM_PERMITIR_MILICIANOS_EN_PELEA = true
    var PARAM_CAPTURAR_MONTURA_COMO_PERGAMINO = false
    var PARAM_EXPULSAR_PREFASE_PVP = true
    var PARAM_JUGADORES_HEROICO_MORIR = true
    var PARAM_INFO_DAÑO_BATALLA = false
    var PARAM_BOOST_SACRO_DESBUFEABLE = false
    var PARAM_REINICIAR_CANALES = false
    var PARAM_CAMBIAR_NOMBRE_CADA_RESET = false
    var PARAM_PERMITIR_BONUS_PELEA_AFECTEN_PROSPECCION = false
    var PARAM_PERMITIR_BONUS_ESTRELLAS = true
    var PARAM_PERMITIR_BONUS_DROP_RETOS = true
    var PARAM_PERMITIR_BONUS_EXP_RETOS = true
    var PARAM_PERMITIR_ADMIN_EN_LADDER = true
    var PARAM_MERCADILLO_OGRINAS = false
    var PARAM_KOLI_NO_GRUPO = true
    var PARAM_MOVER_MULTIPLE_OBJETOS_SOLO_ABONADOS = false
    var PARAM_EXP_PVP_MISION_POR_TABLA = true

    //
    var MENSAJE_TIMER_REBOOT: String? = ""

    //
    var PRECIO_MEDIO_ITEM: ArrayList<Integer?>? = ArrayList<Integer?>()
    var ITEM_CONSUMIBLE_AUTOMATICO: ArrayList<Integer?>? = ArrayList<Integer?>()
    var HECHIZOS_NO_BORRAR: ArrayList<Integer?>? = ArrayList<Integer?>()
    var SUBAREAS_NO_PVP: ArrayList<Integer?>? = ArrayList<Integer?>()
    var TIPO_RECURSOS: ArrayList<Short?>? = ArrayList<Short?>()
    var OBJ_NO_PERMITIDOS: ArrayList<Integer?>? = ArrayList<Integer?>()
    var CLASES_NO_PERMITIDAS_RESET: ArrayList<Byte?>? = ArrayList<Byte?>()
    var CLASES_NO_PERMITIDAS_KOLISEO: ArrayList<Byte?>? = ArrayList<Byte?>()
    var TIPO_ALIMENTO_MONTURA: ArrayList<Short?>? = ArrayList<Short?>()
    var PUBLICIDAD: ArrayList<String?>? = ArrayList<String?>()
    var ARMAS_ENCARNACIONES: String? = "9544,9545,9546,9547,9548,10125,10126,10127,10133"
    var SABIDURIA_PARA_REENVIO = 200
    var DISMINUIR_PERDIDA_HONOR = 0
    var VALOR_BASE_RECAUDADOR = 1000
    var NIVEL_PJ_RESET = 60
    var PARAMS_HECHIZO_CAC_MULTIMAN = 0

    // TIEMPOS MILISEGUNDOS
    var MILISEGUNDOS_ANTI_FLOOD = 5 * 1000 // 5 segundos
    var MILISEGUNDOS_CERRAR_SERVIDOR = 3 * 1000 // segundos

    // TIEMPO SEGUNDOS
    var SEGUNDOS_ENTRE_DESAFIOS_PJ = 5
    var SEGUNDOS_ARENA = 10 * 60 // segundos
    var SEGUNDOS_INICIO_PELEA = 45 // segundos
    var SEGUNDOS_INICIO_EVENTO = 25 // segundos
    var SEGUNDOS_TURNO_PELEA = 30 // segundos
    var SEGUNDOS_CANAL_COMERCIO = 45 // segundos
    var SEGUNDOS_CANAL_RECLUTAMIENTO = 20 // segundos
    var SEGUNDOS_CANAL_ALINEACION = 20 // segundos
    var SEGUNDOS_CANAL_VIP = 10 // segundos
    var SEGUNDOS_CANAL_INCARNAM = 5 // segundos
    var SEGUNDOS_CANAL_ALL = 5 // segundos
    var SEGUNDOS_INACTIVIDAD = 30 * 60 // segundos
    var SEGUNDOS_TRANSACCION_BD = 30 // segundos
    var SEGUNDOS_MOVER_MONTURAS = 10 * 60 // segundos (10 miuntos)
    var SEGUNDOS_MOVER_RECAUDADOR = 30 * 60 // segundos (10 miuntos)
    var SEGUNDOS_MOVER_GRUPO_MOBS = 1 * 60 // segundos (1 minuto)
    var SEGUNDOS_ESTRELLAS_GRUPO_MOBS = 20 * 60 // segundos (20 minutos)
    var SEGUNDOS_ESTRELLAS_RECURSOS = 15 * 60 // segundos (15 minutos)
    var SEGUNDOS_PUBLICIDAD = 55 * 60 // segundos (55 minutos)
    var SEGUNDOS_SALVAR = 60 * 60 // segundos (60 minutos = 1 hora)
    var SEGUNDOS_INICIAR_KOLISEO = 10 * 60 // segundos (10 minutos)
    var SEGUNDOS_LIMPIAR_MEMORIA = 600 // segundos
    var SEGUNDOS_RESET_RATES = 0 // segundos
    var SEGUNDOS_LIVE_ACTION = 0 // segundos
    var SEGUNDOS_REBOOT_SERVER = 24 * 60 * 60 // minutos (24 horas = 1 dia)
    var SEGUNDOS_DETECTAR_DDOS = 5 * 60 // 5 minutos
    var SEGUNDOS_REAPARECER_MOBS = 0
    var SEGUNDOS_AGREDIR_RECIEN_LLEGADO_MAPA = 2

    // TIEMPO MINUTOS
    var MINUTOS_MISION_PVP = 10 // minutos (10 minutos)
    var MINUTOS_ALIMENTACION_MASCOTA = 10 // minutos (10 minutos)
    var MINUTOS_GESTACION_MONTURA = 60 // minutos (1 hora)
    var MINUTOS_SPAMEAR_BOTON_VOTO = 30
    var MINUTOS_VALIDAR_VOTO = 180
    var MINUTOS_PENALIZACION_KOLISEO = 10

    // TIEMPO HORAS
    var HORAS_VOLVER_A_PONER_RECAUDADOR_MAPA = 6
    var HORAS_PERDER_CRIAS_MONTURA = 24
    var HORAS_MINIMO_RECLAMAR_PREMIO: Long = 0
    var HORAS_MAXIMO_RECLAMAR_PREMIO: Long = 0

    // INFO SERVER
    var SERVIDOR_PRIORIDAD = 10

    // public static int PUERTO_MULTISERVIDOR = 444;
    var PUERTO_SERVIDOR = 5555
    var PUERTO_SINCRONIZADOR = 19999
    var IP_MULTISERVIDOR: CopyOnWriteArrayList<String?>? = CopyOnWriteArrayList() // 25.91.217.194

    //public static CopyOnWriteArrayList<String> IP_MULTISERVIDOR2 = new CopyOnWriteArrayList<>();// 25.91.217.194
    //public static CopyOnWriteArrayList<String> IP_PERMTIDAS = new CopyOnWriteArrayList<>();// 25.91.217.194
    //public static CopyOnWriteArrayList<String> PARAMETROS_CONFIG_CUSTOM = new CopyOnWriteArrayList<>();// 25.91.217.194
    //public static CopyOnWriteArrayList<String> PARAMETROS_CONFIG = new CopyOnWriteArrayList<>();// 25.91.217.194
    var IP_PUBLICA_SERVIDOR: String? = ""
    var BD_HOST: String? = null
    var BD_PUERTO: String? = "3306"
    var BD_HOST_DINAMICA: String? = null
    var BD_USUARIO: String? = null
    var BD_PASS: String? = null
    var BD_ESTATICA: String? = null
    var BD_DINAMICA: String? = null
    var BD_CUENTAS: String? = null
    var NOMBRE_SERVER: String? = "Server"
    var LOGIN_CABECERA: String? = "Regalo " + NOMBRE_SERVER
    var LOGIN_CUERPO: String? = "SIN RELLENO"
    var SERVIDOR_ID = 1
    var ACCESO_ADMIN_MINIMO = 0

    // RATES
    var RATE_XP_PVP = 1
    var RATE_XP_PVM = 1
    var RATE_XP_MONTURA = 1
    var RATE_XP_RECAUDADOR = 1
    var RATE_DROP_NORMAL = 1
    var RATE_KAMAS = 1
    var RATE_XP_OFICIO = 1
    var RATE_XP_PVM_ABONADOS = 1
    var RATE_DROP_ABONADOS = 1
    var RATE_KAMAS_ABONADOS = 1
    var RATE_XP_OFICIO_ABONADOS = 1
    var RATE_HONOR = 1
    var RATE_CRIANZA_MONTURA = 1
    var RATE_CAPTURA_MONTURA = 1
    var RATE_DROP_ARMAS_ETEREAS = 1
    var RATE_PODS = 1
    var RATE_FM = 1
    var RATE_CONQUISTA_EXPERIENCIA = 1
    var RATE_CONQUISTA_RECOLECTA = 1
    var RATE_CONQUISTA_DROP = 1

    // INICIO PERSONAJE
    var INICIO_NIVEL = 1
    var INICIO_KAMAS = 0
    var INICIO_EMOTES = 1 // 7667711
    var INICIO_PUNTOS_STATS = 0
    var INICIO_OBJETOS: String? = ""
    var INICIO_SET_ID: String? = ""
    var INICIO_ZAAPS: String? = "164,528,844,935,951,1158,1242,1841,2191,3022,3250,4263,4739,5295,6137,6855,6954,7411,8037,8088,8125,8163,8437,8785,9454,10297,10304,10317,10349,10643,11170,11210"
    var INICIO_BONUS_ESTRELLAS_RECURSOS = -20
    var INICIO_BONUS_ESTRELLAS_MOBS = -20
    var INICIO_NIVEL_MONTURA = 1
    var PUNTOS_STATS_POR_NIVEL = 5
    var PUNTOS_STATS_POR_NIVEL_OMEGA = 10
    var PUNTOS_HECHIZO_POR_NIVEL = 1

    // OTROS SERVER
    var PRECIO_SISTEMA_RECURSO = 0f
    var FACTOR_DEVOLVER_OGRINAS = 0.75f
    var FACTOR_OBTENER_RUNAS = 1f
    var FACTOR_PLUS_PP_PARA_DROP = 1f
    var FACTOR_ZERO_DROP = 3
    var MIN_CANTIDAD_MOBS_EN_GRUPO = 1
    var MAX_ID_OBJETO_MODELO = 99999
    var MAX_CUENTAS_POR_IP = 50
    var MAX_MISIONES_ALMANAX = 180
    var MAX_RECAUDADORES_POR_ZONA = 1
    var MAX_BONUS_ESTRELLAS_RECURSOS = 50 * 20
    var MAX_BONUS_ESTRELLAS_MOBS = 50 * 20
    var MAX_PESO_POR_STAT = 101
    var MAX_RESETS = 3
    var MAX_CAC_POR_TURNO = 0
    var MAX_PJS_POR_CUENTA = 5
    var MAX_PORCENTAJE_DE_STAT_PARA_FM = 80
    var PORCENTAJE_DAÑO_NO_CURABLE = 10
    var PROBABILIDAD_ARCHI_MOBS = 10
    var PROBABILIDAD_PROTECTOR_RECURSOS = 1
    var PROBABILIDAD_RECURSO_ESPECIAL = 1
    var PROBABLIDAD_PERDER_STATS_FM = 100
    var PROBABILIDAD_ESCAPAR_MONTURA_DESPUES_FECUNDAR = 30
    var HONOR_FIJO_PARA_TODOS = -1
    var NIVEL_MINIMO_PARA_PVP = 25
    var RESET_MINIMO_PARA_PVP = 10
    var RANGO_NIVEL_PVP = 20
    var RANGO_NIVEL_KOLISEO = 20
    var CANTIDAD_MIEMBROS_EQUIPO_KOLISEO = 3
    var CANTIDAD_MIEMBROS_EQUIPO_KOLISEO_VS1 = 0
    var CANTIDAD_MIEMBROS_EQUIPO_KOLISEO_VS2 = 0

    // KOLISEO
    var MIN_NIVEL_KOLISEO = 1
    var KOLISEO_PREMIO_KAMAS = 0
    var KOLISEO_DIVISOR_XP = 3
    var KOLISEO_PREMIO_OBJETOS: String? = ""
    var KOLISEO_PREMIO_OBJETOS_ALEATORIOS: String? = ""
    var MISION_PVP_KAMAS = 2000
    var MISION_PVP_OBJETOS: String? = "10275,2"
    var MISION_PVP_TIEMPO = 1000 * 60 * 60

    // NPC EVENTO
    var EVENTO_PREMIO_OBJETOS: String? = ""
    var EVENTO_AREAS: String? = ""
    var INVASION_SUBAREAS: String? = ""
    var EVENTO_NPC = -1
    var PORTAL_NPC = -1
    var EVENTO_MOBS: String? = ""
    var INVASION_MOBS: String? = ""
    var INVASION_CONDICIONES: String? = null
    var EVENTO_TIEMPO_SEGUNDO = 0
    var PORTAL_TIEMPO_SEGUNDO = 0
    var EVENTO_MAPA_MOB_CANTIDAD = 3
    var EVENTO_MAPAS_MOB = 5
    var INVASION_TIEMPO_SEGUNDO = 0
    var INVASION_AGRESION_MOBS: Byte = 0

    // LIMITES
    var LIMITE_MAPAS = 15000
    var LIMITE_LADDER = 20
    var LIMITE_REPORTES = 50
    var LIMITE_SCROLL = 101
    var LIMITE_SCROLL_COMANDO = -1
    var LIMITE_MIEMBROS_GREMIO = 0
    var LIMITE_OBJETOS_COFRE = 80
    var LIMITE_DETECTAR_FALLA_KAMAS: Long = 10000000
    var EXPERIENCIA_X_RESET: Map<Byte?, Integer?>? = TreeMap<Byte?, Integer?>()
    var CLASES_PELEA_RECAUDADOR: Map<Byte?, Integer?>? = TreeMap<Byte?, Integer?>()
    var LIMITE_STATS_SIN_BUFF: Map<Integer?, Integer?>? = TreeMap<Integer?, Integer?>()
    var LIMITE_STATS_PVP: Map<Integer?, Integer?>? = TreeMap<Integer?, Integer?>()
    var LIMITE_STATS_PERCO: Map<Integer?, Integer?>? = TreeMap<Integer?, Integer?>()
    var LIMITE_STATS_PVP_INVO: Map<Integer?, Integer?>? = TreeMap<Integer?, Integer?>()
    var LIMITE_STATS_INVO: Map<Integer?, Integer?>? = TreeMap<Integer?, Integer?>()
    var LIMITE_STATS_KOLISEO: Map<Integer?, Integer?>? = TreeMap<Integer?, Integer?>()
    var LIMITE_STATS_CON_BUFF: Map<Integer?, Integer?>? = TreeMap<Integer?, Integer?>()
    var LIMITE_STATS_CON_BUFF_PVP: Map<Integer?, Integer?>? = TreeMap<Integer?, Integer?>()
    var LIMITE_STATS_CON_BUFF_PVP_INVOCACION: Map<Integer?, Integer?>? = TreeMap<Integer?, Integer?>()
    var RECAUDADOR_STATS: Map<Integer?, Integer?>? = TreeMap<Integer?, Integer?>()
    var RECAUDADOR_STATS_GREMIO: Map<Integer?, Integer?>? = TreeMap<Integer?, Integer?>()
    var RECAUDADOR_VIDA_X_NIVEL = 100
    var LIMITE_STATS_EXO_FORJAMAGIA: Map<Integer?, Integer?>? = TreeMap<Integer?, Integer?>()
    var LIMITE_STATS_OVER_FORJAMAGIA: Map<Integer?, Integer?>? = TreeMap<Integer?, Integer?>()
    var APRENDER_HECHIZO_RESET: Map<Integer?, Integer?>? = TreeMap<Integer?, Integer?>()
    var MIMOVIOENTE_REGRESAR = false
    var MIMOVIOENTE_RESTRICION_NIVEL = true
    var PARAMS_LOGIN_PREMIO = false
    var PARAMS_MASTER_RESET = false
    var PARAMS_FUSION = false
    var PARAMS_HECHIZOS_NIVEL_7 = false

    // public static short LIMITE_PA = 15;
    // public static short LIMITE_PM = 7;
    // public static short LIMITE_ALCANCE = 30;
    // public static short LIMITE_PORC_RESISTENCIA_OBJETOS = 75;
    // public static short LIMITE_PORC_RESISTENCIA_BUFFS = 75;
    // NIVELES MAXIMOS
    var NIVEL_MAX_OFICIO = 0
    var NIVEL_MAX_PERSONAJE = 0
    var NIVEL_MAX_MONTURA = 0
    var NIVEL_MAX_GREMIO = 0
    var NIVEL_MAX_ENCARNACION = 0
    var NIVEL_MAX_ALINEACION = 0
    var NIVEL_MAX_ESCOGER_NIVEL = 0

    //
    var PALABRAS_PROHIBIDAS: ArrayList<String?>? = ArrayList<String?>()
    var OBJETOS_FORTIFICADOS: ArrayList<String?>? = ArrayList<String?>()
    var HECHIZOS_NO_DESECHIZABLE: ArrayList<Integer?>? = ArrayList<Integer?>()

    // PRIVATES
    private var DEFECTO_XP_PVM = 0
    private var DEFECTO_XP_PVP = 0
    private var DEFECTO_XP_OFICIO = 0
    private var DEFECTO_XP_HONOR = 0
    private var DEFECTO_DROP = 0
    private var DEFECTO_KAMAS = 0
    private var DEFECTO_CRIANZA_MONTURA = 0

    // INFO_CHAT_COLOR = "009900";
    // MSG_CHAT_COLOR = "111111";
    // EMOTE_CHAT_COLOR = "222222";
    // THINK_CHAT_COLOR = "232323";
    // MSGCHUCHOTE_CHAT_COLOR = "0066FF";
    // GROUP_CHAT_COLOR = "006699";
    // ERROR_CHAT_COLOR = "C10000";
    // GUILD_CHAT_COLOR = "663399";
    // PVP_CHAT_COLOR = "DD7700";
    // RECRUITMENT_CHAT_COLOR = "737373";
    // TRADE_CHAT_COLOR = "663300";
    // MEETIC_CHAT_COLOR = "0000CC";
    // ADMIN_CHAT_COLOR = "FF00FF";
    // VIP_CHAT_COLOR = "FF00FF";
    fun main(args: Array<String?>?) {
        Runtime.getRuntime().addShutdownHook(object : Thread() {
            fun run() {
                cerrarServer()
            }
        })
        STATS_HEREDADOS_INVOS.add(Constantes.STAT_MAS_FUERZA)
        STATS_HEREDADOS_INVOS.add(Constantes.STAT_MAS_INTELIGENCIA)
        STATS_HEREDADOS_INVOS.add(Constantes.STAT_MAS_SUERTE)
        STATS_HEREDADOS_INVOS.add(Constantes.STAT_MAS_AGILIDAD)
        STATS_HEREDADOS_INVOS.add(Constantes.STAT_MAS_SABIDURIA)
        System.out.println("AEMU " + Constantes.VERSION_EMULADOR + Constantes.SUBVERSION_EMULADOR)
        System.out.println("Modificado Por")
        System.out.println("░█████╗░██╗░░░░░███████╗██╗░░██╗░█████╗░███╗░░██╗██████╗░███████╗██████╗░")
        System.out.println("██╔══██╗██║░░░░░██╔════╝╚██╗██╔╝██╔══██╗████╗░██║██╔══██╗██╔════╝██╔══██╗")
        System.out.println("███████║██║░░░░░█████╗░░░╚███╔╝░███████║██╔██╗██║██║░░██║█████╗░░██████╔╝")
        System.out.println("██╔══██║██║░░░░░██╔══╝░░░██╔██╗░██╔══██║██║╚████║██║░░██║██╔══╝░░██╔══██╗")
        System.out.println("██║░░██║███████╗███████╗██╔╝╚██╗██║░░██║██║░╚███║██████╔╝███████╗██║░░██║")
        System.out.println("╚═╝░░╚═╝╚══════╝╚══════╝╚═╝░░╚═╝╚═╝░░╚═╝╚═╝░░╚══╝╚═════╝░╚══════╝╚═╝░░╚═╝")
        System.out.println("Para El Servidor Eterno")
        System.out.println("Todos Los Derechos Son Propuedad De:")
        System.out.println("Eterno Epic Games")
        System.out.println("Seridor Permitido")

        // cargando la config
        System.out.println("Cargando la configuración")
        //leyendoIpsPermitidas();
        cargarConfiguracion(null)
        while (!IP_MULTISERVIDOR.get(0).equalsIgnoreCase("127.0.0.1")) {
            try {
                val fecha: String = (Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString() + "-"
                        + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "-"
                        + Calendar.getInstance().get(Calendar.YEAR))
                LOG_SERVIDOR = PrintStream(FileOutputStream(
                        "Logs_Servidor_" + NOMBRE_SERVER + "/Log_Servidor_" + fecha + ".txt", true))
                LOG_SERVIDOR.println("---------- INICIO DEL SERVER ----------")
                LOG_SERVIDOR.flush()
                System.setErr(LOG_SERVIDOR)
                break
            } catch (e: IOException) {
                File("Logs_Servidor_" + NOMBRE_SERVER).mkdir()
            } catch (e: Exception) {
                e.printStackTrace()
                break
            }
        }
        // conectado a la base de datos sql
        System.out.print("Conexión a la base de datos:  ")
        if (GestorSQL.iniciarConexion()) {
            System.out.println("CONEXION OK!!")
        } else {
            redactarLogServidorln("CONEXION SQL INVALIDA!!")
            System.exit(1)
            return
        }
        when (RESET_RANKING_PVP) {
            1 -> if (calendar.get(Calendar.DAY_OF_WEEK) === Calendar.MONDAY) {
                GestorSQL.ASIGNAR_REGALO_RANKING_PVP()
            }
            0 -> if (calendar.get(Calendar.DAY_OF_WEEK) === Calendar.SUNDAY) {
                modificarParam("RESET_RANKING_PVP", "1")
            }
        }
        when (RESET_RANKING_KOLISEO) {
            1 -> if (calendar.get(Calendar.DAY_OF_WEEK) === Calendar.MONDAY) {
                GestorSQL.ASIGNAR_REGALO_RANKING_KOLISEO()
            }
            0 -> if (calendar.get(Calendar.DAY_OF_WEEK) === Calendar.SUNDAY) {
                modificarParam("RESET_RANKING_KOLISEO", "1")
            }
        }
        //GestorSQL.ASIGNAR_REGALO_RANKING_COMPENSACION();
        System.out.println("Creando el Servidor ...")
        Mundo.crearServidor()
        IniciarSincronizacion()
        ServidorServer()
        if (ACTIVAR_CONSOLA) {
            Consola()
        }
        if (NPC_RANKING_KOLISEO > 0) Mundo.actualizarLiderKoliseo()
        if (NPC_RANKING_PVP > 0) Mundo.actualizarLiderPVP()
        System.out.println("Esperando que los jugadores se conecten: ")
    }

    fun modificarParam(p: String?, v: String?) {
        try {
            val config = BufferedReader(FileReader(ARCHIVO_CONFIG))
            var linea: String? = ""
            val str = StringBuilder()
            var tiene = false
            while (config.readLine().also { linea = it } != null) {
                if (linea.split("=").length === 1) {
                    str.append(linea)
                } else {
                    val param: String = linea.split("=").get(0).trim()
                    if (param.equalsIgnoreCase(p)) {
                        str.append("$param = $v")
                        tiene = true
                    } else {
                        str.append(linea)
                    }
                }
                str.append("\n")
            }
            if (!tiene) {
                str.append(p.toString() + " = " + v)
            }
            config.close()
            val mod = BufferedWriter(FileWriter(ARCHIVO_CONFIG))
            mod.write(str.toString())
            mod.flush()
            mod.close()
        } catch (e: Exception) {
        }
    }

    val configuracion: String?
        get() {
            val str = StringBuilder()
            try {
                val config = BufferedReader(FileReader(ARCHIVO_CONFIG))
                var linea: String? = ""
                while (config.readLine().also { linea = it } != null) {
                    str.append("""
    ${linea.toString()}
    
    """.trimIndent())
                }
                config.close()
            } catch (e: Exception) {
            }
            return str.toString()
        }

    fun cargarConfiguracion(perso: Personaje?) {
        try {
            val config = BufferedReader(FileReader(ARCHIVO_CONFIG))
            var linea: String? = ""
            val parametros: ArrayList<String?> = ArrayList()
            val repetidos: Map<String?, String?> = TreeMap()
            while (config.readLine().also { linea = it } != null) {
                try {
                    val parametro: String = linea.split("=", 2).get(0).trim()
                    var valor: String = linea.split("=", 2).get(1).trim()
                    if (parametros.contains(parametro)) {
                        System.out.println("EN EL ARCHIVO " + ARCHIVO_CONFIG + " SE REPITE EL PARAMETRO " + parametro
                                + ", Borre uno porque si no tomara en cuenta el que encuentre primero.")
                        return
                    } else {
                        //if (PARAMETROS_CONFIG_CUSTOM.contains(parametro)) {
                        //	if (PARAMETROS_CONFIG.contains(parametro)) {
                        //		parametros.add(parametro);
                        //	} else {
                        //		System.out.println("NO ESTA AUTORIZADO A USAR ESTE PARAMETRO: " + parametro);
                        //	}
                        //} else {
                        parametros.add(parametro)
                        //}
                    }
                    var variable = ""
                    valor = valor.replace("\\n", "\n").replace("\\t", "\t").replace("\\r", "\r").replace("\\b", "\b")
                    when (parametro.toUpperCase()) {
                        "IP_PUBLIC_SERVER", "IP_SERVIDOR_PUBLICA", "IP_SERVIDOR_FIJA", "IP_FIJA_SERVIDOR", "IP_FIX_SERVER", "IP_PUBLICA_SERVIDOR" -> {
                            variable = "IP_PUBLICA_SERVIDOR"
                            //if (IP_PERMTIDAS.contains(valor) || IP_PERMTIDAS.contains("*")) {
                            IP_PUBLICA_SERVIDOR = valor
                        }
                        "RESET_RANKING_PVP" -> {
                            RESET_RANKING_PVP = Integer.parseInt(valor)
                            variable = "RESET_RANKING_PVP"
                        }
                        "RESET_RANKING_KOLISEO" -> {
                            RESET_RANKING_KOLISEO = Integer.parseInt(valor)
                            variable = "RESET_RANKING_KOLISEO"
                        }
                        "IP_MULTISERVIDOR", "IP_MULTISERVER" -> {
                            variable = "IP_MULTISERVIDOR"
                            for (s in valor.split(";")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                //if (IP_PERMTIDAS.contains(s) || IP_PERMTIDAS.contains("*")) {
                                try {
                                    val `in`: InetAddress = InetAddress.getByName(s)
                                    IP_MULTISERVIDOR.add(`in`.getHostAddress())
                                    //IP_MULTISERVIDOR2.add(s);
                                } catch (e: Exception) {
                                    IP_MULTISERVIDOR.add(s)
                                    //IP_MULTISERVIDOR2.add(s);
                                }
                                //}
                            }
                        }
                        "ES_LOCALHOST" -> {
                            ES_LOCALHOST = valor.equalsIgnoreCase("true")
                            variable = "ES_LOCALHOST"
                        }
                        "MOSTRAR_ENVIADOS", "ENVIADOS" -> {
                            MOSTRAR_ENVIOS = valor.equalsIgnoreCase("true")
                            variable = "MOSTRAR_ENVIOS"
                        }
                        "MOSTRAR_SINCRONIZADOR", "SINCRONIZADOS", "MOSTRAR_SINCRONIZACION" -> {
                            MOSTRAR_SINCRONIZACION = valor.equalsIgnoreCase("true")
                            variable = "MOSTRAR_SINCRONIZACION"
                        }
                        "ACTIVAR_CONSOLA" -> {
                            ACTIVAR_CONSOLA = valor.equalsIgnoreCase("true")
                            variable = "ACTIVAR_CONSOLA"
                        }
                        "MOSTRAR_RECIBIDOS", "RECIBIDOS" -> {
                            MOSTRAR_RECIBIDOS = valor.equalsIgnoreCase("true")
                            variable = "MOSTRAR_RECIBIDOS"
                        }
                        "MODO_IMPRIMIR_LOG" -> {
                            MODO_IMPRIMIR_LOG = valor.equalsIgnoreCase("true")
                            variable = "MODO_IMPRIMIR_LOG"
                        }
                        "MODO_DEBUG" -> {
                            MODO_DEBUG = valor.equalsIgnoreCase("true")
                            variable = "MODO_DEBUG"
                        }
                        "INICIO_NIVEL_MONTURA" -> {
                            INICIO_NIVEL_MONTURA = Integer.parseInt(valor)
                            variable = "INICIO_NIVEL_MONTURA"
                        }
                        "INICIO_KAMAS" -> {
                            INICIO_KAMAS = Integer.parseInt(valor)
                            variable = "INICIO_KAMAS"
                            if (INICIO_KAMAS < 0) {
                                INICIO_KAMAS = 0
                            }
                        }
                        "INICIO_NIVEL" -> {
                            INICIO_NIVEL = Short.parseShort(valor)
                            variable = "INICIO_NIVEL"
                            if (INICIO_NIVEL < 1) {
                                INICIO_NIVEL = 1
                            }
                        }
                        "INICIO_SET_ID" -> {
                            INICIO_SET_ID = valor
                            variable = "INICIO_SET_ID"
                        }
                        "INICIO_ZAAPS" -> {
                            INICIO_ZAAPS = valor
                            variable = "INICIO_ZAAPS"
                        }
                        "INICIO_OBJETOS" -> {
                            INICIO_OBJETOS = valor
                            variable = "INICIO_OBJETOS"
                        }
                        "INICIO_EMOTES" -> {
                            INICIO_EMOTES = Integer.parseInt(valor)
                            variable = "INICIO_EMOTES"
                        }
                        "EVENTO_PREMIO_OBJETOS" -> {
                            EVENTO_PREMIO_OBJETOS = valor
                            variable = "EVENTO_PREMIO_OBJETOS"
                        }
                        "EVENTO_AREAS" -> {
                            EVENTO_AREAS = valor
                            variable = "EVENTO_AREAS"
                        }
                        "INVASION_SUBAREAS" -> {
                            INVASION_SUBAREAS = valor
                            variable = "INVASION_SUBAREAS"
                        }
                        "EVENTO_MOBS" -> {
                            EVENTO_MOBS = valor
                            variable = "EVENTO_MOBS"
                        }
                        "INVASION_MOBS" -> {
                            INVASION_MOBS = valor
                            variable = "INVASION_MOBS"
                        }
                        "INVASION_CONDICIONES" -> {
                            INVASION_CONDICIONES = valor
                            variable = "INVASION_CONDICIONES"
                        }
                        "PORTAL_NPC" -> {
                            PORTAL_NPC = Integer.parseInt(valor)
                            variable = "PORTAL_NPC"
                        }
                        "EVENTO_NPC" -> {
                            EVENTO_NPC = Integer.parseInt(valor)
                            variable = "EVENTO_NPC"
                        }
                        "PARAMS_HECHIZOS_NIVEL_7" -> {
                            PARAMS_HECHIZOS_NIVEL_7 = valor.equalsIgnoreCase("true")
                            variable = "PARAMS_HECHIZOS_NIVEL_7"
                        }
                        "EVENTO_TIEMPO_SEGUNDO" -> {
                            EVENTO_TIEMPO_SEGUNDO = Integer.parseInt(valor)
                            variable = "EVENTO_TIEMPO_SEGUNDO"
                        }
                        "PORTAL_TIEMPO_SEGUNDO" -> {
                            PORTAL_TIEMPO_SEGUNDO = Integer.parseInt(valor)
                            variable = "PORTAL_TIEMPO_SEGUNDO"
                        }
                        "INVASION_TIEMPO_SEGUNDO" -> {
                            INVASION_TIEMPO_SEGUNDO = Integer.parseInt(valor)
                            variable = "INVASION_TIEMPO_SEGUNDO"
                        }
                        "INVASION_AGRESION_MOBS" -> {
                            INVASION_AGRESION_MOBS = Byte.parseByte(valor)
                            variable = "INVASION_AGRESION_MOBS"
                        }
                        "EVENTO_MAPA_MOB_CANTIDAD" -> {
                            EVENTO_MAPA_MOB_CANTIDAD = Integer.parseInt(valor)
                            variable = "EVENTO_MAPA_MOB_CANTIDAD"
                        }
                        "EVENTO_MAPAS_MOB" -> {
                            EVENTO_MAPAS_MOB = Integer.parseInt(valor)
                            variable = "EVENTO_MAPAS_MOB"
                        }
                        "INICIO_PUNTOS_STATS" -> {
                            INICIO_PUNTOS_STATS = Integer.parseInt(valor)
                            variable = "INICIO_PUNTOS_STATS"
                        }
                        "PUNTOS_STAT_POR_NIVEL", "PUNTOS_STATS_POR_NIVEL" -> {
                            PUNTOS_STATS_POR_NIVEL = Integer.parseInt(valor)
                            variable = "PUNTOS_STATS_POR_NIVEL"
                        }
                        "PUNTOS_HECHIZO_POR_NIVEL" -> {
                            PUNTOS_HECHIZO_POR_NIVEL = Integer.parseInt(valor)
                            variable = "PUNTOS_HECHIZO_POR_NIVEL"
                        }
                        "KOLISEO_PREMIO_KAMAS", "KOLISEO_KAMAS" -> {
                            KOLISEO_PREMIO_KAMAS = Integer.parseInt(valor)
                            variable = "KOLISEO_PREMIO_KAMAS"
                        }
                        "MISION_PVP_KAMAS" -> {
                            MISION_PVP_KAMAS = Integer.parseInt(valor)
                            variable = "MISION_PVP_KAMAS"
                        }
                        "MISION_PVP_TIEMPO" -> {
                            MISION_PVP_TIEMPO = Integer.parseInt(valor) * 60000
                            variable = "MISION_PVP_TIEMPO"
                        }
                        "KOLISEO_DIVISOR_XP" -> {
                            KOLISEO_DIVISOR_XP = Integer.parseInt(valor)
                            variable = "KOLISEO_DIVISOR_XP"
                        }
                        "KOLISEO_PREMIO_OBJETOS", "KOLISEO_OBJETOS" -> {
                            KOLISEO_PREMIO_OBJETOS = valor
                            variable = "KOLISEO_PREMIO_OBJETOS"
                        }
                        "KOLISEO_PREMIO_OBJETOS_ALEATORIOS" -> {
                            KOLISEO_PREMIO_OBJETOS_ALEATORIOS = valor
                            variable = "KOLISEO_PREMIO_OBJETOS_ALEATORIOS"
                        }
                        "MISION_PVP_OBJETOS" -> {
                            MISION_PVP_OBJETOS = valor
                            variable = "MISION_PVP_OBJETOS"
                        }
                        "PARAM_PERMITIR_BONUS_PELEA_AFECTEN_PROSPECCION" -> {
                            PARAM_PERMITIR_BONUS_PELEA_AFECTEN_PROSPECCION = valor.equalsIgnoreCase("true")
                            variable = "PARAM_PERMITIR_BONUS_PELEA_AFECTEN_PROSPECCION"
                        }
                        "PERMITIR_BONUS_ESTRELLAS" -> {
                            PARAM_PERMITIR_BONUS_ESTRELLAS = valor.equalsIgnoreCase("true")
                            variable = "PERMITIR_BONUS_ESTRELLAS"
                        }
                        "PERMITIR_BONUS_DROP_RETOS" -> {
                            PARAM_PERMITIR_BONUS_DROP_RETOS = valor.equalsIgnoreCase("true")
                            variable = "PERMITIR_BONUS_DROP_RETOS"
                        }
                        "PERMITIR_BONUS_EXP_RETOS" -> {
                            PARAM_PERMITIR_BONUS_EXP_RETOS = valor.equalsIgnoreCase("true")
                            variable = "PERMITIR_BONUS_EXP_RETOS"
                        }
                        "PARAM_PERMITIR_ADMIN_EN_LADDER" -> {
                            PARAM_PERMITIR_ADMIN_EN_LADDER = valor.equalsIgnoreCase("true")
                            variable = "PARAM_PERMITIR_ADMIN_EN_LADDER"
                        }
                        "PARAM_MERCADILLO_OGRINAS" -> {
                            PARAM_MERCADILLO_OGRINAS = valor.equalsIgnoreCase("true")
                            variable = "PARAM_MERCADILLO_OGRINAS"
                        }
                        "PARAM_KOLI_NO_GRUPO" -> {
                            PARAM_KOLI_NO_GRUPO = valor.equalsIgnoreCase("true")
                            variable = "PARAM_KOLI_NO_GRUPO"
                        }
                        "MIMOVIOENTE_REGRESAR" -> {
                            MIMOVIOENTE_REGRESAR = valor.equalsIgnoreCase("true")
                            variable = "MIMOVIOENTE_REGRESAR"
                        }
                        "PARAMS_LOGIN_PREMIO" -> {
                            PARAMS_LOGIN_PREMIO = valor.equalsIgnoreCase("true")
                            variable = "PARAMS_LOGIN_PREMIO"
                        }
                        "PARAMS_MASTER_RESET" -> {
                            PARAMS_MASTER_RESET = valor.equalsIgnoreCase("true")
                            variable = "PARAMS_MASTER_RESET"
                        }
                        "PARAMS_FUSION" -> {
                            PARAMS_FUSION = valor.equalsIgnoreCase("true")
                            variable = "PARAMS_FUSION"
                        }
                        "MIMOVIOENTE_RESTRICION_NIVEL" -> {
                            MIMOVIOENTE_RESTRICION_NIVEL = valor.equalsIgnoreCase("true")
                            variable = "MIMOVIOENTE_RESTRICION_NIVEL"
                        }
                        "PARAM_MOVER_MULTIPLE_OBJETOS_SOLO_ABONADOS" -> {
                            PARAM_MOVER_MULTIPLE_OBJETOS_SOLO_ABONADOS = valor.equalsIgnoreCase("true")
                            variable = "PARAM_MOVER_MULTIPLE_OBJETOS_SOLO_ABONADOS"
                        }
                        "PARAM_EXP_PVP_MISION_POR_TABLA" -> {
                            PARAM_EXP_PVP_MISION_POR_TABLA = valor.equalsIgnoreCase("true")
                            variable = "PARAM_EXP_PVP_MISION_POR_TABLA"
                        }
                        "FACTOR_ZERO_DROP" -> {
                            FACTOR_ZERO_DROP = Integer.parseInt(valor)
                            variable = "FACTOR_ZERO_DROP"
                        }
                        "RATE_FM" -> {
                            // case "DIFICULTAD_FM" :
                            RATE_FM = Integer.parseInt(valor)
                            variable = "RATE_FM"
                        }
                        "RATE_KAMAS" -> {
                            RATE_KAMAS = Integer.parseInt(valor)
                            variable = "RATE_KAMAS"
                            DEFECTO_KAMAS = RATE_KAMAS
                        }
                        "RATE_HONOR" -> {
                            RATE_HONOR = Integer.parseInt(valor)
                            variable = "RATE_HONOR"
                            DEFECTO_XP_HONOR = RATE_HONOR
                        }
                        "RATE_XP_OFICIO" -> {
                            RATE_XP_OFICIO = Integer.parseInt(valor)
                            variable = "RATE_XP_OFICIO"
                            DEFECTO_XP_OFICIO = RATE_XP_OFICIO
                        }
                        "RATE_PVM", "RATE_XP_PVM" -> {
                            RATE_XP_PVM = Integer.parseInt(valor)
                            variable = "RATE_XP_PVM"
                            DEFECTO_XP_PVM = RATE_XP_PVM
                        }
                        "RATE_XP_MONTURA" -> {
                            RATE_XP_MONTURA = Integer.parseInt(valor)
                            variable = "RATE_XP_MONTURA"
                        }
                        "RATE_XP_PERCO", "RATE_XP_RECAUDADOR" -> {
                            RATE_XP_RECAUDADOR = Integer.parseInt(valor)
                            variable = "RATE_XP_RECAUDADOR"
                        }
                        "RATE_PVP", "RATE_XP_PVP" -> {
                            RATE_XP_PVP = Integer.parseInt(valor)
                            variable = "RATE_XP_PVP"
                            DEFECTO_XP_PVP = RATE_XP_PVP
                        }
                        "RATE_DROP_PORC", "RATE_DROP" -> {
                            RATE_DROP_NORMAL = Integer.parseInt(valor)
                            variable = "RATE_DROP_PORC"
                            DEFECTO_DROP = RATE_DROP_NORMAL
                        }
                        "RATE_XP_PVM_ABONADOS" -> {
                            RATE_XP_PVM_ABONADOS = Integer.parseInt(valor)
                            variable = "RATE_XP_PVM_ABONADOS"
                        }
                        "RATE_DROP_ABONADOS" -> {
                            RATE_DROP_ABONADOS = Integer.parseInt(valor)
                            variable = "RATE_DROP_ABONADOS"
                        }
                        "RATE_KAMAS_ABONADOS" -> {
                            RATE_KAMAS_ABONADOS = Integer.parseInt(valor)
                            variable = "RATE_KAMAS_ABONADOS"
                        }
                        "RATE_XP_OFICIO_ABONADOS" -> {
                            RATE_XP_OFICIO_ABONADOS = Integer.parseInt(valor)
                            variable = "RATE_XP_OFICIO_ABONADOS"
                        }
                        "RATE_PODS" -> {
                            RATE_PODS = Integer.parseInt(valor)
                            variable = "RATE_PODS"
                        }
                        "RATE_CONQUISTA_EXPERIENCIA" -> {
                            RATE_CONQUISTA_EXPERIENCIA = Integer.parseInt(valor)
                            variable = "RATE_CONQUISTA_EXPERIENCIA"
                        }
                        "RATE_CONQUISTA_RECOLECTA" -> {
                            RATE_CONQUISTA_RECOLECTA = Integer.parseInt(valor)
                            variable = "RATE_CONQUISTA_RECOLECTA"
                        }
                        "RATE_CONQUISTA_DROP" -> {
                            RATE_CONQUISTA_DROP = Integer.parseInt(valor)
                            variable = "RATE_CONQUISTA_DROP"
                        }
                        "RATE_DROP_ARMAS_ETEREAS" -> {
                            RATE_DROP_ARMAS_ETEREAS = Integer.parseInt(valor)
                            variable = "RATE_DROP_ARMAS_ETEREAS"
                        }
                        "RATE_CRIANZA_MONTURAS", "RATE_CRIANZA_MONTURA", "RATE_CRIANZA_PAVOS" -> {
                            RATE_CRIANZA_MONTURA = Integer.parseInt(valor)
                            variable = "RATE_CRIANZA_MONTURA"
                            DEFECTO_CRIANZA_MONTURA = RATE_CRIANZA_MONTURA
                        }
                        "RATE_CAPTURA_MONTURAS", "RATE_CAPTURA_MONTURA", "RATE_CAPTURA_PAVOS" -> {
                            RATE_CAPTURA_MONTURA = Integer.parseInt(valor)
                            variable = "RATE_CAPTURA_MONTURA"
                        }
                        "MENSAJE_BIENVENIDA_1", "MENSAJE_BIENVENIDA" -> {
                            MENSAJE_BIENVENIDA = valor
                            variable = "MENSAJE_BIENVENIDA"
                        }
                        "PANEL_BIENVENIDA_1", "PANEL_BIENVENIDA" -> {
                            PANEL_BIENVENIDA = valor
                            variable = "PANEL_BIENVENIDA"
                        }
                        "PANEL_CREAR_PJ" -> {
                            PANEL_DESPUES_CREAR_PERSONAJE = valor
                            variable = "PANEL_DESPUES_CREAR_PJ"
                        }
                        "MESSAGE_COMMANDS", "MENSAJE_COMANDOS" -> {
                            MENSAJE_COMANDOS = valor
                            variable = "MENSAJE_COMANDOS"
                        }
                        "MESSAGE_SERVICIES", "MENSAJE_SERVICIOS" -> {
                            MENSAJE_SERVICIOS = valor
                            variable = "MENSAJE_SERVICIOS"
                        }
                        "MENSAJE_ERROR_OGRINAS_CREAR_CLASE" -> {
                            MENSAJE_ERROR_OGRINAS_CREAR_CLASE = valor
                            variable = "MENSAJE_ERROR_OGRINAS_CREAR_CLASE"
                        }
                        "MENSAJE_ERROR_ADMIN_CREAR_CLASE" -> {
                            MENSAJE_ERROR_ADMIN_CREAR_CLASE = valor
                            variable = "MENSAJE_ERROR_ADMIN_CREAR_CLASE"
                        }
                        "MESSAGE_VIP", "MENSAJE_VIP" -> {
                            MENSAJE_VIP = valor
                            variable = "MENSAJE_VIP"
                        }
                        "TUTORIAL_FR" -> {
                            TUTORIAL_FR = valor
                            variable = "TUTORIAL_FR"
                        }
                        "TUTORIAL_ES" -> {
                            TUTORIAL_ES = valor
                            variable = "TUTORIAL_ES"
                        }
                        "PUBLICIDAD_1", "PUBLICIDAD_2", "PUBLICIDAD_3", "PUBLICIDAD_4", "PUBLICIDAD_5" -> PUBLICIDAD.add(valor)
                        "MAPAS_KOLISEO" -> {
                            MAPAS_KOLISEO = valor
                            variable = "MAPAS_KOLISEO"
                        }
                        "PUERTO_SERVER", "PUERTO_SERVIDOR" -> {
                            PUERTO_SERVIDOR = Integer.parseInt(valor)
                            variable = "PUERTO_SERVIDOR"
                        }
                        "PUERTO_SINCRONIZACION", "PUERTO_SINCRONIZADOR" -> {
                            PUERTO_SINCRONIZADOR = Integer.parseInt(valor)
                            variable = "PUERTO_SINCRONIZADOR"
                        }
                        "SERVIDOR_PRIORIDAD" -> {
                            SERVIDOR_PRIORIDAD = Integer.parseInt(valor)
                            variable = "SERVIDOR_PRIORIDAD"
                        }
                        "PARAM_NERFEAR_STATS_EXOTICOS" -> PARAM_NERFEAR_STATS_EXOTICOS = valor.equalsIgnoreCase("true")
                        "PARAM_APRENDER_HECHIZO" -> PARAM_APRENDER_HECHIZO = valor.equalsIgnoreCase("true")
                        "VENDER_MERCANTE_OBJETO" -> VENDER_MERCANTE_OBJETO = Integer.parseInt(valor)
                        "DB_HOST", "BD_HOST" -> {
                            BD_HOST = valor
                            variable = "BD_HOST"
                        }
                        "BD_HOST_DINAMICA" -> {
                            BD_HOST_DINAMICA = valor
                            variable = "BD_HOST_DINAMICA"
                        }
                        "DB_USER", "BD_USER", "BD_USUARIO" -> {
                            BD_USUARIO = valor
                            variable = "BD_USUARIO"
                        }
                        "DB_PASSWORD", "DB_PASS", "BD_PASSWORD", "BD_CONTRASEÑA", "BD_PASS" -> {
                            BD_PASS = valor
                            variable = "BD_PASS"
                        }
                        "DB_STATIC", "BD_STATIC", "BD_STATIQUE", "BD_FIJA", "BD_LUIS" -> {
                            BD_ESTATICA = valor
                            variable = "BD_ESTATICA"
                        }
                        "DB_DYNAMIC", "BD_TANIA", "BD_DYNAMIC", "BD_DINAMICA", "BD_OTHERS" -> {
                            BD_DINAMICA = valor
                            variable = "BD_DINAMICA"
                        }
                        "DB_ACCOUNTS", "BD_ACCOUNTS", "BD_COMPTES", "BD_CUENTAS", "BD_LOGIN", "BD_REALM" -> {
                            BD_CUENTAS = valor
                            variable = "BD_CUENTAS"
                        }
                        "NAME_SERVER", "NOMBRE_SERVIDOR", "NOMBRE_SERVER" -> {
                            NOMBRE_SERVER = valor
                            variable = "NOMBRE_SERVER"
                        }
                        "LOGIN_CUERPO" -> {
                            LOGIN_CUERPO = valor
                            variable = "LOGIN_CUERPO"
                        }
                        "LOGIN_CABECERA" -> {
                            LOGIN_CABECERA = valor
                            variable = "LOGIN_CABECERA"
                        }
                        "URL_IMAGEN_VOTO" -> {
                            URL_IMAGEN_VOTO = valor
                            variable = "URL_IMAGEN_VOTO"
                        }
                        "URL_LINK_VOTE", "URL_LINK_VOTO" -> {
                            URL_LINK_VOTO = valor
                            variable = "URL_LINK_VOTO"
                        }
                        "URL_LINK_COMPRA" -> {
                            URL_LINK_COMPRA = valor
                            variable = "URL_LINK_COMPRA"
                        }
                        "URL_BACKUP_PHP" -> {
                            URL_BACKUP_PHP = valor
                            variable = "URL_BACKUP_PHP"
                        }
                        "URL_DETECTAR_DDOS" -> {
                            URL_DETECTAR_DDOS = valor
                            variable = "URL_DETECTAR_DDOS"
                        }
                        "URL_REPORT_BUG", "URL_LINK_BUG" -> {
                            URL_LINK_BUG = valor
                            variable = "URL_LINK_BUG"
                        }
                        "URL_LINK_MP3" -> {
                            URL_LINK_MP3 = valor
                            variable = "URL_LINK_MP3"
                        }
                        "DIRECTORIO_MP3", "DIRECTORIO_LOCAL_MP3" -> {
                            DIRECTORIO_LOCAL_MP3 = valor
                            variable = "DIRECTORIO_LOCAL_MP3"
                        }
                        "COMMANDS_AUTHORIZATE", "COMMANDS_PLAYER", "COMANDOS_AUTORIZADOS", "COMANDOS_JUGADOR", "COMANDOS_PERMITIDOS" -> {
                            variable = "COMANDOS_PERMITIDOS"
                            for (s in valor.split(",")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                COMANDOS_PERMITIDOS.add(s)
                            }
                        }
                        "EVENTO_MAPA_NO_PERMITIDOS" -> {
                            variable = "EVENTO_MAPA_NO_PERMITIDOS"
                            INVASION_MAPAS_NO_PERMITIDOS = valor
                            for (s in valor.split(",")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    EVENTO_MAPA_NO_PERMITIDOS.add(Integer.parseInt(s))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "MAPAS_PVP" -> {
                            variable = "MAPAS_PVP"
                            for (s in valor.split(",")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    MAPAS_PVP.add(Integer.parseInt(s))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "MOBS_JEFE" -> {
                            variable = "MOBS_JEFE"
                            for (s in valor.split(",")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    MOBS_JEFE.add(Integer.parseInt(s))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "COMMANDS_VIP", "COMMANDS_BOUTIQUE", "COMANDOS_ABONADO", "COMANDOS_VIP" -> {
                            variable = "COMANDOS_VIP"
                            for (s in valor.split(",")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                COMANDOS_VIP.add(s)
                            }
                        }
                        "SALVAR_LOGS_TIPO_PELEA", "SALVAR_LOGS_TIPO_COMBATE" -> {
                            variable = "SALVAR_LOGS_TIPO_COMBATE"
                            for (s in valor.split(",")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    SALVAR_LOGS_TIPO_COMBATE.add(Byte.parseByte(s))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "CANALES_COLOR_CHAT", "COLORES_CANALES_CHAT", "COLOR_CHAT" -> {
                            CANALES_COLOR_CHAT = valor
                            variable = "CANALES_COLOR_CHAT"
                        }
                        "PERMITIR_MULTIMAN_TIPO_PELEA", "PERMITIR_MULTIMAN_TIPO_COMBATE" -> {
                            PERMITIR_MULTIMAN = valor
                            variable = "PERMITIR_MULTIMAN"
                        }
                        "PALABRA_CLAVE_CONSOLA" -> {
                            PALABRA_CLAVE_CONSOLA = valor
                            variable = "PALABRA_CLAVE_CONSOLA"
                        }
                        "SISTEMA_ITEMS_TIPO_DE_PAGO", "PANEL_ITEMS_TIPO_DE_PAGO" -> {
                            SISTEMA_ITEMS_TIPO_DE_PAGO = valor.toUpperCase()
                            variable = "SISTEMA_ITEMS_TIPO_DE_PAGO"
                        }
                        "INICIO_MAPA_CELDA", "START_MAPA_CELDA", "RETURN_MAPA_CELDA" -> {
                            START_MAPA_CELDA = valor
                            variable = "START_MAPA_CELDA"
                        }
                        "ENCLO_MAPA_CELDA", "ENCLOS_MAPA_CELDA", "CERCADO_MAPA_CELDA", "CERCADOS_MAPA_CELDA" -> {
                            CERCADO_MAPA_CELDA = valor
                            variable = "CERCADO_MAPA_CELDA"
                        }
                        "TIENDA_MAPA_CELDA", "SHOP_MAPA_CELDA" -> {
                            SHOP_MAPA_CELDA = valor
                            variable = "SHOP_MAPA_CELDA"
                        }
                        "TIENDAFUS_MAPA_CELDA" -> {
                            TIENDAFUS_MAPA_CELDA = valor
                            variable = "TIENDAFUS_MAPA_CELDA"
                        }
                        "PVP_MAPA_CELDA" -> {
                            PVP_MAPA_CELDA = valor
                            variable = "PVP_MAPA_CELDA"
                        }
                        "NIVEL_IA", "LEVEL_PROCCESS_IA", "NIVEL_PROCESAMIENTO_IA", "NIVEL_EJECUCION_IA", "NIVEL_INTELIGENCIA_ARTIFICIAL" -> {
                            NIVEL_INTELIGENCIA_ARTIFICIAL = Integer.parseInt(valor)
                            variable = "NIVEL_INTELIGENCIA_ARTIFICIAL"
                        }
                        "OBJETOS_POS_AYUDANTE" -> {
                            OBJETOS_POS_AYUDANTE = Byte.parseByte(valor)
                            variable = "OBJETOS_POS_AYUDANTE"
                        }
                        "VECES_PARA_BAN_IP_SIN_ESPERA" -> {
                            VECES_PARA_BAN_IP_SIN_ESPERA = Integer.parseInt(valor)
                            variable = "VECES_PARA_BAN_IP_SIN_ESPERA"
                        }
                        "SEGUNDOS_CANAL_COMERCIO" -> {
                            SEGUNDOS_CANAL_COMERCIO = Integer.parseInt(valor)
                            variable = "SEGUNDOS_CANAL_COMERCIO"
                        }
                        "SEGUNDOS_CANAL_RECLUTAMIENTO" -> {
                            SEGUNDOS_CANAL_RECLUTAMIENTO = Integer.parseInt(valor)
                            variable = "SEGUNDOS_CANAL_RECLUTAMIENTO"
                        }
                        "SEGUNDOS_CANAL_ALINEACION" -> {
                            SEGUNDOS_CANAL_ALINEACION = Integer.parseInt(valor)
                            variable = "SEGUNDOS_CANAL_ALINEACION"
                        }
                        "SEGUNDOS_CANAL_INCARNAM" -> {
                            SEGUNDOS_CANAL_INCARNAM = Integer.parseInt(valor)
                            variable = "SEGUNDOS_CANAL_INCARNAM"
                        }
                        "SEGUNDOS_CANAL_ALL" -> {
                            SEGUNDOS_CANAL_ALL = Integer.parseInt(valor)
                            variable = "SEGUNDOS_CANAL_ALL"
                        }
                        "SEGUNDOS_CANAL_VIP" -> {
                            SEGUNDOS_CANAL_VIP = Integer.parseInt(valor)
                            variable = "SEGUNDOS_CANAL_VIP"
                        }
                        "SEGUNDOS_TURNO_PELEA" -> {
                            SEGUNDOS_TURNO_PELEA = Integer.parseInt(valor)
                            variable = "SEGUNDOS_TURNO_PELEA"
                        }
                        "SEGUNDOS_ARENA" -> {
                            SEGUNDOS_ARENA = Integer.parseInt(valor)
                            variable = "SEGUNDOS_ARENA"
                        }
                        "SEGUNDOS_ENTRE_DESAFIOS_PJ" -> {
                            SEGUNDOS_ENTRE_DESAFIOS_PJ = Integer.parseInt(valor)
                            variable = "SEGUNDOS_ENTRE_DESAFIOS_PJ"
                        }
                        "SEGUNDOS_INACTIVIDAD" -> {
                            SEGUNDOS_INACTIVIDAD = Integer.parseInt(valor)
                            variable = "SEGUNDOS_INACTIVIDAD"
                        }
                        "SEGUNDOS_TRANSACCION_BD" -> {
                            SEGUNDOS_TRANSACCION_BD = Integer.parseInt(valor)
                            variable = "SEGUNDOS_TRANSACCION_BD"
                        }
                        "SEGUNDOS_SALVAR" -> {
                            SEGUNDOS_SALVAR = Integer.parseInt(valor)
                            variable = "SEGUNDOS_SALVAR"
                        }
                        "SEGUNDOS_REBOOT_SERVER", "SEGUNDOS_RESET_SERVER", "SEGUNDOS_REBOOT", "SEGUNDOS_RESET" -> {
                            SEGUNDOS_REBOOT_SERVER = Integer.parseInt(valor)
                            variable = "SEGUNDOS_REBOOT_SERVER"
                        }
                        "SEGUNDOS_DETECTAR_DDOS" -> {
                            SEGUNDOS_DETECTAR_DDOS = Integer.parseInt(valor)
                            variable = "SEGUNDOS_DETECTAR_DDOS"
                        }
                        "SEGUNDOS_VOLVER_APARECER_MOBS", "SEGUNDOS_REAPARECER_GRUPO_MOBS", "SEGUNDOS_REAPARECER_MOBS" -> {
                            SEGUNDOS_REAPARECER_MOBS = Integer.parseInt(valor)
                            variable = "SEGUNDOS_REAPARECER_MOBS"
                        }
                        "SEGUNDOS_AGREDIR_RECIEN_LLEGADO_MAPA" -> {
                            SEGUNDOS_AGREDIR_RECIEN_LLEGADO_MAPA = Integer.parseInt(valor)
                            variable = "SEGUNDOS_AGREDIR_RECIEN_LLEGADO_MAPA"
                        }
                        "SEGUNDOS_LIMPIAR_MEMORIA" -> {
                            SEGUNDOS_LIMPIAR_MEMORIA = Integer.parseInt(valor)
                            variable = "SEGUNDOS_LIMPIAR_MEMORIA"
                        }
                        "SEGUNDOS_RESET_RATES" -> {
                            SEGUNDOS_RESET_RATES = Integer.parseInt(valor)
                            variable = "SEGUNDOS_RESET_RATES"
                        }
                        "MINUTOS_VALIDAR_VOTO", "MINUTOS_SIGUIENTE_VOTO", "MINUTOS_SIG_VOTO" -> {
                            MINUTOS_VALIDAR_VOTO = Integer.parseInt(valor)
                            variable = "MINUTOS_VALIDAR_VOTO"
                        }
                        "MINUTOS_PENALIZACION_KOLISEO" -> {
                            MINUTOS_PENALIZACION_KOLISEO = Integer.parseInt(valor)
                            variable = "MINUTOS_PENALIZACION_KOLISEO"
                        }
                        "HORAS_MINIMO_RECLAMAR_PREMIO" -> {
                            HORAS_MINIMO_RECLAMAR_PREMIO = Integer.parseInt(valor) * 1000 * 60 * 60
                            variable = "HORAS_MINIMO_RECLAMAR_PREMIO"
                        }
                        "HORAS_MAXIMO_RECLAMAR_PREMIO" -> {
                            HORAS_MAXIMO_RECLAMAR_PREMIO = Integer.parseInt(valor) * 1000 * 60 * 60
                            variable = "HORAS_MAXIMO_RECLAMAR_PREMIO"
                        }
                        "MINUTOS_SPAMEAR_BOTON_VOTO" -> {
                            MINUTOS_SPAMEAR_BOTON_VOTO = Integer.parseInt(valor)
                            variable = "MINUTOS_SPAMEAR_BOTON_VOTO"
                        }
                        "MILISEGUNDOS_CERRAR_SERVIDOR" -> {
                            MILISEGUNDOS_CERRAR_SERVIDOR = Integer.parseInt(valor)
                            variable = "MILISEGUNDOS_CERRAR_SERVIDOR"
                        }
                        "SEGUNDOS_LIVE_ACTION" -> {
                            SEGUNDOS_LIVE_ACTION = Integer.parseInt(valor)
                            variable = "SEGUNDOS_LIVE_ACTION"
                        }
                        "SEGUNDOS_PUBLICIDAD" -> {
                            SEGUNDOS_PUBLICIDAD = Integer.parseInt(valor)
                            variable = "SEGUNDOS_PUBLICIDAD"
                        }
                        "SEGUNDOS_ESTRELLAS_GRUPO_MOBS", "SEGUNDOS_ESTRELLAS_MOBS" -> {
                            SEGUNDOS_ESTRELLAS_GRUPO_MOBS = Integer.parseInt(valor)
                            variable = "SEGUNDOS_ESTRELLAS_GRUPO_MOBS"
                        }
                        "SEGUNDOS_ESTRELLAS_RECURSOS" -> {
                            SEGUNDOS_ESTRELLAS_RECURSOS = Integer.parseInt(valor)
                            variable = "SEGUNDOS_ESTRELLAS_RECURSOS"
                        }
                        "SEGUNDOS_INICIAR_KOLISEO" -> {
                            SEGUNDOS_INICIAR_KOLISEO = Integer.parseInt(valor)
                            variable = "SEGUNDOS_INICIAR_KOLISEO"
                        }
                        "MINUTOS_ALIMENTACION_MASCOTA", "MINUTOS_ALIMENTACION" -> {
                            MINUTOS_ALIMENTACION_MASCOTA = Integer.parseInt(valor)
                            variable = "MINUTOS_ALIMENTACION_MASCOTA"
                        }
                        "MINUTOS_MISION_PVP" -> {
                            MINUTOS_MISION_PVP = Integer.parseInt(valor)
                            variable = "MINUTOS_MISION_PVP"
                        }
                        "MINUTOS_GESTACION_MONTURA", "MINUTOS_PARIR", "MINUTOS_PARIR_MONTURA" -> {
                            MINUTOS_GESTACION_MONTURA = Integer.parseInt(valor)
                            variable = "MINUTOS_GESTACION_MONTURA"
                        }
                        "SEGUNDOS_MOVER_RECAUDADOR" -> {
                            SEGUNDOS_MOVER_RECAUDADOR = Integer.parseInt(valor)
                            variable = "SEGUNDOS_MOVER_RECAUDADOR"
                        }
                        "SEGUNDOS_MOVER_MONTURAS", "SEGUNDOS_MOVERSE_MONTURAS", "SEGUNDOS_MOVERSE_PAVOS" -> {
                            SEGUNDOS_MOVER_MONTURAS = Integer.parseInt(valor)
                            variable = "SEGUNDOS_MOVER_MONTURAS"
                        }
                        "SEGUNDOS_MOVER_GRUPO_MOBS", "SEGUNDOS_MOVER_MOBS", "SEGUNDOS_MOVERSE_MOBS" -> {
                            SEGUNDOS_MOVER_GRUPO_MOBS = Integer.parseInt(valor)
                            variable = "SEGUNDOS_MOVER_GRUPO_MOBS"
                        }
                        "HORAS_VOLVER_A_PONER_RECAUDADOR_MAPA" -> {
                            HORAS_VOLVER_A_PONER_RECAUDADOR_MAPA = Integer.parseInt(valor)
                            variable = "HORAS_VOLVER_A_PONER_RECAUDADOR_MAPA"
                        }
                        "HORAS_PERDER_CRIAS_MONTURA" -> {
                            HORAS_PERDER_CRIAS_MONTURA = Integer.parseInt(valor)
                            variable = "HORAS_PERDER_CRIAS_MONTURA"
                        }
                        "MIN_NIVEL_KOLISEO" -> {
                            MIN_NIVEL_KOLISEO = Short.parseShort(valor)
                            variable = "MIN_NIVEL_KOLISEO"
                        }
                        "LIMITE_SCROLL", "MAX_SCROLL" -> {
                            LIMITE_SCROLL = Short.parseShort(valor)
                            variable = "LIMITE_SCROLL"
                        }
                        "MAX_SCROLL_COMANDO" -> {
                            LIMITE_SCROLL_COMANDO = Short.parseShort(valor)
                            variable = "LIMITE_SCROLL_COMANDO"
                        }
                        "MAX_MIEMBROS_GREMIO", "LIMITE_MIEMBROS_GREMIO" -> {
                            LIMITE_MIEMBROS_GREMIO = Integer.parseInt(valor)
                            variable = "LIMITE_MIEMBROS_GREMIO"
                        }
                        "LIMITE_OBJETOS_COFRE", "MAX_OBJETOS_COFRE", "LIMITE_MAX_OBJETOS_COFRE" -> {
                            LIMITE_OBJETOS_COFRE = Integer.parseInt(valor)
                            variable = "LIMITE_OBJETOS_COFRE"
                        }
                        "ID_BOLSA_CREDITOS" -> {
                            ID_BOLSA_CREDITOS = Integer.parseInt(valor)
                            variable = "ID_BOLSA_CREDITOS"
                        }
                        "ID_BOLSA_OGRINAS" -> {
                            ID_BOLSA_OGRINAS = Integer.parseInt(valor)
                            variable = "ID_BOLSA_OGRINAS"
                        }
                        "ID_VENTA_MERCANTE" -> {
                            ID_VENTA_MERCANTE = Integer.parseInt(valor)
                            variable = "ID_VENTA_MERCANTE"
                        }
                        "IMPUESTO_BOLSA_OGRINAS" -> {
                            IMPUESTO_BOLSA_OGRINAS = Integer.parseInt(valor)
                            variable = "IMPUESTO_BOLSA_OGRINAS"
                        }
                        "MODO_PERFECCION_OBJETOS" -> {
                            MODO_PERFECCION_OBJETOS = Integer.parseInt(valor)
                            variable = "MODO_PERFECCION_OBJETOS"
                        }
                        "DURABILIDAD_REDUCIR_OBJETO_CRIA" -> {
                            DURABILIDAD_REDUCIR_OBJETO_CRIA = Integer.parseInt(valor)
                            variable = "DURABILIDAD_REDUCIR_OBJETO_CRIA"
                        }
                        "IMPUESTO_BOLSA_CREDITOS" -> {
                            IMPUESTO_BOLSA_CREDITOS = Integer.parseInt(valor)
                            variable = "IMPUESTO_BOLSA_CREDITOS"
                        }
                        "LIMITE_LADDER" -> {
                            LIMITE_LADDER = Short.parseShort(valor)
                            variable = "LIMITE_LADDER"
                        }
                        "LIMITE_MOSTRAR_REPORTES", "MAX_MOSTRAR_REPORTES", "MAX_REPORTES", "LIMIT_REPORTES" -> {
                            LIMITE_REPORTES = Short.parseShort(valor)
                            variable = "LIMITE_REPORTES"
                        }
                        "MAX_MISIONES_ALMANAX" -> {
                            MAX_MISIONES_ALMANAX = Short.parseShort(valor)
                            variable = "MAX_MISIONES_ALMANAX"
                        }
                        "COLOR_CASES_PLAYER", "COLOR_CASES_FIGHT_AGRESSOR", "COLOR_CELDAS_PELEA_AGRESOR" -> {
                            COLOR_CELDAS_PELEA_AGRESOR = valor.toLowerCase()
                            variable = "COLOR_CELDAS_PELEA_AGRESOR"
                        }
                        "MAX_RECAUDADORES_POR_ZONA" -> {
                            MAX_RECAUDADORES_POR_ZONA = Integer.parseInt(valor)
                            variable = "MAX_RECAUDADORES_POR_ZONA"
                        }
                        "MAX_BONUS_STARS_MOB", "MAX_BONUS_STARS_MOBS", "MAX_BONUS_ESTRELLAS_MOBS" -> {
                            MAX_BONUS_ESTRELLAS_MOBS = Short.parseShort(valor)
                            variable = "MAX_BONUS_ESTRELLAS_MOBS"
                        }
                        "MAX_BONUS_STARS_RESSOURCES", "MAX_BONUS_ESTRELLAS_RECURSO", "MAX_BONUS_ESTRELLAS_RECURSOS" -> {
                            MAX_BONUS_ESTRELLAS_RECURSOS = Short.parseShort(valor)
                            variable = "MAX_BONUS_ESTRELLAS_RECURSOS"
                        }
                        "MAX_STARS_MOBS", "MAX_ESTRELLAS_MOB", "MAX_ESTRELLAS_MOBS" -> {
                            MAX_BONUS_ESTRELLAS_MOBS = (Short.parseShort(valor) * 20) as Short.toInt()
                            variable = "MAX_BONUS_ESTRELLAS_MOBS"
                        }
                        "MAX_STARS_RESSOURCES", "MAX_STARS_RECURSOS", "MAX_ESTRELLAS_RECURSO", "MAX_ESTRELLAS_RECURSOS" -> {
                            MAX_BONUS_ESTRELLAS_RECURSOS = (Short.parseShort(valor) * 20) as Short.toInt()
                            variable = "MAX_BONUS_ESTRELLAS_RECURSOS"
                        }
                        "INICIO_BONUS_ESTRELLAS_MOBS" -> {
                            INICIO_BONUS_ESTRELLAS_MOBS = Short.parseShort(valor)
                            variable = "INICIO_BONUS_ESTRELLAS_MOBS"
                        }
                        "INICIO_ESTRELLAS_MOBS" -> {
                            INICIO_BONUS_ESTRELLAS_MOBS = (Short.parseShort(valor) * 20) as Short.toInt()
                            variable = "INICIO_BONUS_ESTRELLAS_MOBS"
                        }
                        "INICIO_BONUS_ESTRELLAS_RECURSOS" -> {
                            INICIO_BONUS_ESTRELLAS_RECURSOS = Short.parseShort(valor)
                            variable = "INICIO_BONUS_ESTRELLAS_RECURSOS"
                        }
                        "INICIO_ESTRELLAS_RECURSOS" -> {
                            INICIO_BONUS_ESTRELLAS_RECURSOS = (Short.parseShort(valor) * 20) as Short.toInt()
                            variable = "INICIO_BONUS_ESTRELLAS_RECURSOS"
                        }
                        "NIVEL_MAX_PERSONAJE" -> {
                            NIVEL_MAX_PERSONAJE = Short.parseShort(valor)
                            variable = "NIVEL_MAX_PERSONAJE"
                        }
                        "NIVEL_MAX_ESCOGER_NIVEL" -> {
                            NIVEL_MAX_ESCOGER_NIVEL = Short.parseShort(valor)
                            variable = "NIVEL_MAX_ESCOGER_NIVEL"
                        }
                        "NIVEL_MAX_MONTURA", "NIVEL_MAX_DRAGOPAVO" -> {
                            NIVEL_MAX_MONTURA = Short.parseShort(valor)
                            variable = "NIVEL_MAX_MONTURA"
                        }
                        "NIVEL_MAX_GREMIO" -> {
                            NIVEL_MAX_GREMIO = Short.parseShort(valor)
                            variable = "NIVEL_MAX_GREMIO"
                        }
                        "NIVEL_MAX_ENCARNACION" -> {
                            NIVEL_MAX_ENCARNACION = Short.parseShort(valor)
                            variable = "NIVEL_MAX_ENCARNACION"
                        }
                        "NIVEL_MAX_ALINEACION" -> {
                            NIVEL_MAX_ALINEACION = Byte.parseByte(valor)
                            variable = "NIVEL_MAX_ALINEACION"
                        }
                        "MAX_PESO_POR_STAT" -> {
                            MAX_PESO_POR_STAT = Integer.parseInt(valor)
                            variable = "MAX_PESO_POR_STAT"
                        }
                        "MAX_RESET", "MAX_RESETS" -> {
                            MAX_RESETS = Integer.parseInt(valor)
                            variable = "MAX_RESETS"
                        }
                        "RECAUDADOR_VIDA_X_NIVEL" -> {
                            RECAUDADOR_VIDA_X_NIVEL = Integer.parseInt(valor)
                            variable = "RECAUDADOR_VIDA_X_NIVEL"
                        }
                        "MAX_CAC_POR_TURNO" -> {
                            MAX_CAC_POR_TURNO = Integer.parseInt(valor)
                            variable = "MAX_CAC_POR_TURNO"
                        }
                        "PRECIO_LOTERIA" -> {
                            PRECIO_LOTERIA = Short.parseShort(valor)
                            variable = "PRECIO_LOTERIA"
                        }
                        "ALMANAX_OBJETO_CANTIDAD" -> {
                            ALMANAX_OBJETO_CANTIDAD = Short.parseShort(valor)
                            variable = "ALMANAX_OBJETO_CANTIDAD"
                        }
                        "PREMIO_LOTERIA" -> {
                            PREMIO_LOTERIA = Short.parseShort(valor)
                            variable = "PREMIO_LOTERIA"
                        }
                        "DIAS_PARA_BORRAR" -> {
                            DIAS_PARA_BORRAR = Short.parseShort(valor)
                            variable = "DIAS_PARA_BORRAR"
                        }
                        "PARAM_CRAFT_SPEED" -> {
                            PARAM_CRAFT_SPEED = valor.equalsIgnoreCase("true")
                            variable = "PARAM_CRAFT_SPEED"
                        }
                        "PARAM_VIP_CRAFT_SPEED" -> {
                            PARAM_VIP_CRAFT_SPEED = valor.equalsIgnoreCase("true")
                            variable = "PARAM_VIP_CRAFT_SPEED"
                        }
                        "PARAM_CORREGIR_NOMBRE_JUGADOR" -> {
                            PARAM_CORREGIR_NOMBRE_JUGADOR = valor.equalsIgnoreCase("true")
                            variable = "PARAM_CORREGIR_NOMBRE_JUGADOR"
                        }
                        "PARAM_KOLI_MISMO_RESET" -> {
                            PARAM_KOLI_MISMO_RESET = valor.equalsIgnoreCase("true")
                            variable = "PARAM_KOLI_MISMO_RESET"
                        }
                        "PARAM_VARIAS_ACCIONES" -> {
                            PARAM_VARIAS_ACCIONES = valor.equalsIgnoreCase("true")
                            variable = "PARAM_VARIAS_ACCIONES"
                        }
                        "PARAM_AGREDIR_MISMO_RESET" -> {
                            PARAM_AGREDIR_MISMO_RESET = valor.equalsIgnoreCase("true")
                            variable = "PARAM_AGREDIR_MISMO_RESET"
                        }
                        "PARAM_AGREDIR_RESET_ALTERNOS" -> {
                            PARAM_AGREDIR_RESET_ALTERNOS = valor.equalsIgnoreCase("true")
                            variable = "PARAM_AGREDIR_RESET_ALTERNOS"
                        }
                        "PARAM_REINICIAR_ESTRELLAS_SI_LLEGA_MAX", "RESETEAR_ESTRELLAS" -> {
                            PARAM_REINICIAR_ESTRELLAS_SI_LLEGA_MAX = valor.equalsIgnoreCase("true")
                            variable = "PARAM_REINICIAR_ESTRELLAS_SI_LLEGA_MAX"
                        }
                        "PARAM_ANTI_FLOOD", "PARAM_ANTIFLOOD" -> {
                            PARAM_ANTIFLOOD = valor.equalsIgnoreCase("true")
                            variable = "PARAM_ANTIFLOOD"
                        }
                        "PARAM_LIMITAR_RECAUDADOR_GREMIO_POR_ZONA" -> {
                            PARAM_LIMITAR_RECAUDADOR_GREMIO_POR_ZONA = valor.equalsIgnoreCase("true")
                            variable = "PARAM_LIMITAR_RECAUDADOR_GREMIO_POR_ZONA"
                        }
                        "RESETEAR_LUPEAR_OBJETOS_MAGUEADOS" -> {
                            PARAM_RESETEAR_LUPEAR_OBJETOS_MAGUEADOS = valor.equalsIgnoreCase("true")
                            variable = "RESETEAR_LUPEAR_OBJETOS_MAGUEADOS"
                        }
                        "NPC_BOUTIQUE" -> {
                            ID_NPC_BOUTIQUE = Integer.parseInt(valor)
                            variable = "ID_NPC_BOUTIQUE"
                        }
                        "MOB_AUMENTO_RESET" -> {
                            MOB_AUMENTO_RESET = Integer.parseInt(valor)
                            variable = "MOB_AUMENTO_RESET"
                        }
                        "EVENTO_NPC_2" -> {
                            EVENTO_NPC_2 = Integer.parseInt(valor)
                            variable = "EVENTO_NPC_2"
                        }
                        "EXPERIENCIA_CANTIDAD" -> {
                            EXPERIENCIA_CANTIDAD = Integer.parseInt(valor)
                            variable = "EXPERIENCIA_CANTIDAD"
                        }
                        "LIMITE_CURAS" -> {
                            LIMITE_CURAS = Integer.parseInt(valor)
                            variable = "LIMITE_CURAS"
                        }
                        "LIMITE_LOCURA" -> {
                            LIMITE_LOCURA = Integer.parseInt(valor)
                            variable = "LIMITE_LOCURA"
                        }
                        "LIMITE_REENVIO" -> {
                            LIMITE_REENVIO = Integer.parseInt(valor)
                            variable = "LIMITE_REENVIO"
                        }
                        "LIMITE_VIDA_BUFF" -> {
                            LIMITE_VIDA_BUFF = Integer.parseInt(valor)
                            variable = "LIMITE_VIDA_BUFF"
                        }
                        "LOGRO_REGALO_FIN" -> {
                            LOGRO_REGALO_FIN = Integer.parseInt(valor)
                            variable = "LOGRO_REGALO_FIN"
                        }
                        "LIMITE_VIDA_INVO_BUFF" -> {
                            LIMITE_VIDA_INVO_BUFF = Integer.parseInt(valor)
                            variable = "LIMITE_VIDA_INVO_BUFF"
                        }
                        "LIMITE_BUFF_KOLISEO" -> {
                            variable = "LIMITE_BUFF_KOLISEO"
                            for (s in valor.split(";")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat: Array<String?> = s.split(",")
                                    LIMITE_BUFF_KOLISEO.put(Integer.parseInt(stat[0]), Integer.parseInt(stat[1]))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "LIMITE_BUFF_PVP" -> {
                            variable = "LIMITE_BUFF_PVP"
                            for (s in valor.split(";")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat: Array<String?> = s.split(",")
                                    LIMITE_BUFF_PVP.put(Integer.parseInt(stat[0]), Integer.parseInt(stat[1]))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "LIMITE_BONUS_2" -> {
                            variable = "LIMITE_BONUS_2"
                            for (s in valor.split(";")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat: Array<String?> = s.split(",")
                                    LIMITE_BONUS_2.put(Integer.parseInt(stat[0]), Integer.parseInt(stat[1]))
                                    LIMITE_BONUS_VALOR.put(Integer.parseInt(stat[0]), Integer.parseInt(stat[2]))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "LIMITE_VIDA_CLASE_KOLISEO" -> {
                            variable = "LIMITE_VIDA_CLASE_KOLISEO"
                            for (s in valor.split(";")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat: Array<String?> = s.split(",")
                                    LIMITE_VIDA_CLASE_KOLISEO.put(Byte.parseByte(stat[0]), Integer.parseInt(stat[1]))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "LIMITE_VIDA_CLASE" -> {
                            variable = "LIMITE_VIDA_CLASE"
                            for (s in valor.split(";")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat: Array<String?> = s.split(",")
                                    LIMITE_VIDA_CLASE.put(Byte.parseByte(stat[0]), Integer.parseInt(stat[1]))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "LIMITE_VIDA" -> {
                            LIMITE_VIDA = Integer.parseInt(valor)
                            variable = "LIMITE_VIDA"
                        }
                        "LIMITE_VIDA_KOLISEO" -> {
                            LIMITE_VIDA_KOLISEO = Integer.parseInt(valor)
                            variable = "LIMITE_VIDA_KOLISEO"
                        }
                        "LIMITE_VIDA_PERCO" -> {
                            LIMITE_VIDA_PERCO = Integer.parseInt(valor)
                            variable = "LIMITE_VIDA_PERCO"
                        }
                        "LIMITE_VIDA_INVO_KOLI_OSA" -> {
                            LIMITE_VIDA_INVO_KOLI_OSA = Integer.parseInt(valor)
                            variable = "LIMITE_VIDA_INVO_KOLI_OSA"
                        }
                        "LIMITE_VIDA_INVO_OSA" -> {
                            LIMITE_VIDA_INVO_OSA = Integer.parseInt(valor)
                            variable = "LIMITE_VIDA_INVO_OSA"
                        }
                        "LIMITE_VIDA_INVO_KOLI" -> {
                            LIMITE_VIDA_INVO_KOLI = Integer.parseInt(valor)
                            variable = "LIMITE_VIDA_INVO_KOLI"
                        }
                        "LIMITE_VIDA_INVO" -> {
                            LIMITE_VIDA_INVO = Integer.parseInt(valor)
                            variable = "LIMITE_VIDA_INVO"
                        }
                        "PRECIO_SISTEMA_RECURSO", "PRECIO_RECURSO" -> {
                            PRECIO_SISTEMA_RECURSO = Float.parseFloat(valor)
                            variable = "PRECIO_SISTEMA_RECURSO"
                        }
                        "FACTOR_OBTENER_RUNAS" -> {
                            FACTOR_OBTENER_RUNAS = Float.parseFloat(valor)
                            variable = "FACTOR_OBTENER_RUNAS"
                        }
                        "FACTOR_PLUS_PP_PARA_DROP" -> {
                            FACTOR_PLUS_PP_PARA_DROP = Float.parseFloat(valor)
                            variable = "FACTOR_PLUS_PP_PARA_DROP"
                        }
                        "FACTOR_DEVOLVER_OGRINAS" -> {
                            FACTOR_DEVOLVER_OGRINAS = Float.parseFloat(valor)
                            variable = "FACTOR_DEVOLVER_OGRINAS"
                        }
                        "OGRINAS_CREAR_CLASE" -> {
                            variable = "OGRINAS_CREAR_CLASE"
                            for (s in valor.split(";")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat: Array<String?> = s.split(",")
                                    OGRINAS_CREAR_CLASE.put(Byte.parseByte(stat[0]), Integer.parseInt(stat[1]))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "ADMIN_CLASE" -> {
                            variable = "ADMIN_CLASE"
                            for (s in valor.split(";")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat: Array<String?> = s.split(",")
                                    ADMIN_CLASE.put(Byte.parseByte(stat[0]), Integer.parseInt(stat[1]))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "GFX_CREA_TU_ITEM_CAPAS" -> {
                            GFX_CREA_TU_ITEM_CAPAS = valor
                            variable = "GFX_CREA_TU_ITEM_CAPAS"
                        }
                        "GFX_CREA_TU_ITEM_AMULETOS" -> {
                            GFX_CREA_TU_ITEM_AMULETOS = valor
                            variable = "GFX_CREA_TU_ITEM_AMULETOS"
                        }
                        "GFX_CREA_TU_ITEM_ANILLOS" -> {
                            GFX_CREA_TU_ITEM_ANILLOS = valor
                            variable = "GFX_CREA_TU_ITEM_ANILLOS"
                        }
                        "GFX_CREA_TU_ITEM_CINTURONES" -> {
                            GFX_CREA_TU_ITEM_CINTURONES = valor
                            variable = "GFX_CREA_TU_ITEM_CINTURONES"
                        }
                        "GFX_CREA_TU_ITEM_BOTAS" -> {
                            GFX_CREA_TU_ITEM_BOTAS = valor
                            variable = "GFX_CREA_TU_ITEM_BOTAS"
                        }
                        "GFX_CREA_TU_ITEM_SOMBREROS" -> {
                            GFX_CREA_TU_ITEM_SOMBREROS = valor
                            variable = "GFX_CREA_TU_ITEM_SOMBREROS"
                        }
                        "GFX_CREA_TU_ITEM_ESCUDOS" -> {
                            GFX_CREA_TU_ITEM_SOMBREROS = valor
                            variable = "GFX_CREA_TU_ITEM_SOMBREROS"
                        }
                        "GFX_CREA_TU_ITEM_DOFUS" -> {
                            GFX_CREA_TU_ITEM_DOFUS = valor
                            variable = "GFX_CREA_TU_ITEM_DOFUS"
                        }
                        "PRECIOS_SERVICIOS", "PRECIO_SERVICIOS" -> {
                            variable = "PRECIOS_SERVICIOS"
                            for (s in valor.split(";")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat: Array<String?> = s.split(",")
                                    PRECIOS_SERVICIOS.put(stat[0].toLowerCase(), stat[1].toLowerCase())
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "MAX_GOLPES_CAC" -> {
                            variable = "MAX_GOLPES_CAC"
                            for (s in valor.split(";")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat: Array<String?> = s.split(",")
                                    MAX_GOLPES_CAC.put(Integer.parseInt(stat[0]), Integer.parseInt(stat[1]))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "ITEM_SOLO_POS" -> {
                            variable = "ITEM_SOLO_POS"
                            for (s in valor.split(";")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat: Array<String?> = s.split(",")
                                    ITEM_SOLO_POS.put(Integer.parseInt(stat[0]), Integer.parseInt(stat[1]))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "STATS_POR_NIVEL" -> {
                            variable = "STATS_POR_NIVEL"
                            for (s in valor.split(";")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat: Array<String?> = s.split(",")
                                    STATS_POR_NIVEL.put(Byte.parseByte(stat[0]), Integer.parseInt(stat[1]))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "GOLPES_MAPAS" -> {
                            variable = "GOLPES_MAPAS"
                            for (s in valor.split(";")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat: Array<String?> = s.split(",")
                                    GOLPES_MAPAS.put(Integer.parseInt(stat[0]), Integer.parseInt(stat[1]))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "OBJETOS_OGRINAS_NPC" -> {
                            variable = "OBJETOS_OGRINAS_NPC"
                            for (s in valor.split(";")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat: Array<String?> = s.split(",")
                                    OBJETOS_OGRINAS_NPC.put(Integer.parseInt(stat[0]), Integer.parseInt(stat[1]))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "REGALO_RANKING_PVP" -> {
                            variable = "REGALO_RANKING_PVP"
                            for (s in valor.split(",")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    REGALO_RANKING_PVP.add(Integer.parseInt(s))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "MAPAS_PERSONAJE" -> {
                            variable = "MAPAS_PERSONAJE"
                            for (s in valor.split(";")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat: Array<String?> = s.split(",")
                                    MAPAS_PERSONAJE.put(Integer.parseInt(stat[0]), Integer.parseInt(stat[1]))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "HECHIZOS_CLASE_UNICOS" -> {
                            variable = "HECHIZOS_CLASE_UNICOS"
                            for (s in valor.split(";")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat: Array<String?> = s.split(",")
                                    HECHIZOS_CLASE_UNICOS.put(Integer.parseInt(stat[0]), Integer.parseInt(stat[1]))
                                    HECHIZOS_CLASE_UNICOS2.put(Integer.parseInt(stat[1]), Integer.parseInt(stat[0]))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "MAX_GOLPES_CAC_PVM" -> {
                            variable = "MAX_GOLPES_CAC_PVM"
                            for (s in valor.split(";")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat: Array<String?> = s.split(",")
                                    MAX_GOLPES_CAC_PVM.put(Integer.parseInt(stat[0]), Integer.parseInt(stat[1]))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "LIMITE_STATS_CON_BUFF_PVP_INVOCACION" -> {
                            variable = "LIMITE_STATS_CON_BUFF_PVP_INVOCACION"
                            for (s in valor.split(";")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat: Array<String?> = s.split(",")
                                    LIMITE_STATS_CON_BUFF_PVP_INVOCACION.put(Integer.parseInt(stat[0]),
                                            Integer.parseInt(stat[1]))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "LIMITE_STATS_CON_BUFF_PVP" -> {
                            variable = "LIMITE_STATS_CON_BUFF_PVP"
                            for (s in valor.split(";")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat: Array<String?> = s.split(",")
                                    LIMITE_STATS_CON_BUFF_PVP.put(Integer.parseInt(stat[0]), Integer.parseInt(stat[1]))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "LIMITE_STATS_CON_BUFF" -> {
                            variable = "LIMITE_STATS_CON_BUFF"
                            for (s in valor.split(";")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat: Array<String?> = s.split(",")
                                    LIMITE_STATS_CON_BUFF.put(Integer.parseInt(stat[0]), Integer.parseInt(stat[1]))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "RECAUDADOR_STATS_GREMIO" -> {
                            variable = "RECAUDADOR_STATS_GREMIO"
                            for (s in valor.split(";")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat: Array<String?> = s.split(",")
                                    RECAUDADOR_STATS_GREMIO.put(Integer.parseInt(stat[0]), Integer.parseInt(stat[1]))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "RECAUDADOR_STATS" -> {
                            variable = "RECAUDADOR_STATS"
                            for (s in valor.split(";")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat: Array<String?> = s.split(",")
                                    RECAUDADOR_STATS.put(Integer.parseInt(stat[0]), Integer.parseInt(stat[1]))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "LIMITE_STATS_EXO_FM", "LIMITE_STATS_EXOMAGIA", "LIMITE_STATS_EXO_FORJAMAGIA" -> {
                            variable = "LIMITE_STATS_EXO_FORJAMAGIA"
                            for (s in valor.split(";")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat: Array<String?> = s.split(",")
                                    LIMITE_STATS_EXO_FORJAMAGIA.put(Integer.parseInt(stat[0]), Integer.parseInt(stat[1]))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "LIMITE_STATS_OVER_FM", "LIMITE_STATS_OVERMAGIA", "LIMITE_STATS_OVER_FORJAMAGIA" -> {
                            variable = "LIMITE_STATS_OVER_FORJAMAGIA"
                            for (s in valor.split(";")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat: Array<String?> = s.split(",")
                                    LIMITE_STATS_OVER_FORJAMAGIA.put(Integer.parseInt(stat[0]), Integer.parseInt(stat[1]))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "APRENDER_HECHIZO_RESET" -> {
                            variable = "APRENDER_HECHIZO_RESET"
                            for (s in valor.split(";")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat: Array<String?> = s.split(",")
                                    APRENDER_HECHIZO_RESET.put(Integer.parseInt(stat[0]), Integer.parseInt(stat[1]))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "LIMITE_STATS_SIN_BUFF" -> {
                            variable = "LIMITE_STATS_SIN_BUFF"
                            for (s in valor.split(";")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat: Array<String?> = s.split(",")
                                    LIMITE_STATS_SIN_BUFF.put(Integer.parseInt(stat[0]), Integer.parseInt(stat[1]))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "LIMITE_STATS_PERCO" -> {
                            variable = "LIMITE_STATS_PERCO"
                            for (s in valor.split(";")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat: Array<String?> = s.split(",")
                                    LIMITE_STATS_PERCO.put(Integer.parseInt(stat[0]), Integer.parseInt(stat[1]))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "LIMITE_STATS_PVP" -> {
                            variable = "LIMITE_STATS_PVP"
                            for (s in valor.split(";")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat: Array<String?> = s.split(",")
                                    LIMITE_STATS_PVP.put(Integer.parseInt(stat[0]), Integer.parseInt(stat[1]))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "LIMITE_STATS_INVO" -> {
                            variable = "LIMITE_STATS_INVO"
                            for (s in valor.split(";")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat: Array<String?> = s.split(",")
                                    LIMITE_STATS_INVO.put(Integer.parseInt(stat[0]), Integer.parseInt(stat[1]))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "LIMITE_STATS_PVP_INVO" -> {
                            variable = "LIMITE_STATS_PVP_INVO"
                            for (s in valor.split(";")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat: Array<String?> = s.split(",")
                                    LIMITE_STATS_PVP_INVO.put(Integer.parseInt(stat[0]), Integer.parseInt(stat[1]))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "LIMITE_STATS_KOLISEO" -> {
                            variable = "LIMITE_STATS_KOLISEO"
                            for (s in valor.split(";")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat: Array<String?> = s.split(",")
                                    LIMITE_STATS_KOLISEO.put(Integer.parseInt(stat[0]), Integer.parseInt(stat[1]))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "EXPERIENCIA_X_RESET" -> {
                            variable = "EXPERIENCIA_X_RESET"
                            for (s in valor.split(";")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat: Array<String?> = s.split(",")
                                    EXPERIENCIA_X_RESET.put(Byte.parseByte(stat[0]), Integer.parseInt(stat[1]))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "CLASES_NO_PERMITIDAS_KOLISEO" -> {
                            variable = "CLASES_NO_PERMITIDAS_KOLISEO"
                            for (s in valor.split(";")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    CLASES_NO_PERMITIDAS_KOLISEO.add(Byte.parseByte(s))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "CLASES_PELEA_RECAUDADOR" -> {
                            variable = "CLASES_PELEA_RECAUDADOR"
                            for (s in valor.split(";")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat: Array<String?> = s.split(",")
                                    CLASES_PELEA_RECAUDADOR.put(Byte.parseByte(stat[0]), Integer.parseInt(stat[1]))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "SERVER_ID" -> {
                            SERVIDOR_ID = Short.parseShort(valor)
                            variable = "SERVIDOR_ID"
                        }
                        "ADMIN_ACCESS", "ACCESO_ADMIN", "ACCESS_ADMIN", "ACCESO_ADMIN_MIN" -> {
                            ACCESO_ADMIN_MINIMO = Integer.parseInt(valor)
                            variable = "ACCESO_ADMIN_MINIMO"
                        }
                        "PORCENTAJE_DAÑO_NO_CURABLE", "DAÑO_PERMANENTE" -> {
                            PORCENTAJE_DAÑO_NO_CURABLE = Integer.parseInt(valor)
                            variable = "PORCENTAJE_DAÑO_NO_CURABLE"
                            if (PORCENTAJE_DAÑO_NO_CURABLE < 0) {
                                PORCENTAJE_DAÑO_NO_CURABLE = 0
                            }
                            if (PORCENTAJE_DAÑO_NO_CURABLE > 100) {
                                PORCENTAJE_DAÑO_NO_CURABLE = 100
                            }
                        }
                        "LIMITE_MAPAS" -> {
                            LIMITE_MAPAS = Short.parseShort(valor)
                            variable = "LIMITE_MAPAS"
                        }
                        "LIMITE_DETECTAR_FALLA_KAMAS" -> {
                            LIMITE_DETECTAR_FALLA_KAMAS = Long.parseLong(valor)
                            variable = "LIMITE_DETECTAR_FALLA_KAMAS"
                        }
                        "MAX_PJS_POR_CUENTA" -> {
                            MAX_PJS_POR_CUENTA = Integer.parseInt(valor)
                            variable = "MAX_PJS_POR_CUENTA"
                        }
                        "BONUS_RESET_PUNTOS_STATS" -> {
                            BONUS_RESET_PUNTOS_STATS = Short.parseShort(valor)
                            variable = "BONUS_RESET_PUNTOS_STATS"
                        }
                        "BONUS_RESET_PUNTOS_HECHIZOS" -> {
                            BONUS_RESET_PUNTOS_HECHIZOS = Short.parseShort(valor)
                            variable = "BONUS_RESET_PUNTOS_HECHIZOS"
                        }
                        "SISTEMA_ITEMS_EXO_PM_PRECIO", "PANEL_ITEMS_EXO_PM_PRECIO", "PANEL_ITEMS_EXO_PM" -> {
                            SISTEMA_ITEMS_EXO_PM_PRECIO = Short.parseShort(valor)
                            variable = "SISTEMA_ITEMS_EXO_PM_PRECIO"
                        }
                        "SISTEMA_ITEMS_EXO_PA_PRECIO", "PANEL_ITEMS_EXO_PA_PRECIO", "PANEL_ITEMS_EXO_PA" -> {
                            SISTEMA_ITEMS_EXO_PA_PRECIO = Short.parseShort(valor)
                            variable = "SISTEMA_ITEMS_EXO_PA_PRECIO"
                        }
                        "SISTEMA_ITEMS_PERFECTO_MULTIPLICA_POR", "PANEL_ITEMS_PERFECTO_MULTIPLICA_POR" -> {
                            SISTEMA_ITEMS_PERFECTO_MULTIPLICA_POR = Float.parseFloat(valor)
                            variable = "SISTEMA_ITEMS_PERFECTO_MULTIPLICA_POR"
                        }
                        "SISTEMA_ITEMS_EXO_TIPOS_NO_PERMITIDOS", "PANEL_ITEMS_EXO_TIPOS_NO_PERMITIDOS" -> {
                            variable = "SISTEMA_ITEMS_EXO_TIPOS_NO_PERMITIDOS"
                            for (s in valor.split(",")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    SISTEMA_ITEMS_EXO_TIPOS_NO_PERMITIDOS.add(Short.parseShort(s))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "MAPAS_MODO_HEROICO" -> {
                            variable = "MAPAS_MODO_HEROICO"
                            for (s in valor.split(",")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    MAPAS_MODO_HEROICO.add(Integer.parseInt(s))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "PARAMS_ITEM_PILA" -> {
                            variable = "PARAMS_ITEM_PILA"
                            for (s in valor.split(",")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    PARAMS_ITEM_PILA.add(Integer.parseInt(s))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "STATS_NO_FORTICABLES" -> {
                            variable = "STATS_NO_FORTICABLES"
                            for (s in valor.split(",")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    STATS_NO_FORTICABLES.add(Integer.parseInt(s))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "OBJETOS_NO_LUPEAR" -> {
                            variable = "OBJETOS_NO_LUPEAR"
                            for (s in valor.split(",")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    OBJETOS_NO_LUPEAR.add(Integer.parseInt(s))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "RUNAS_NO_PERMITIDAS" -> {
                            variable = "RUNAS_NO_PERMITIDAS"
                            for (s in valor.split(",")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    RUNAS_NO_PERMITIDAS.add(Integer.parseInt(s))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "HECHIZOS_NO_KOLI_PVP" -> {
                            variable = "HECHIZOS_NO_KOLI_PVP"
                            for (s in valor.split(",")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    HECHIZOS_NO_KOLI_PVP.add(Integer.parseInt(s))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "MOBS_DOBLE_ORBES" -> {
                            variable = "MOBS_DOBLE_ORBES"
                            for (s in valor.split(",")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    MOBS_DOBLE_ORBES.add(Integer.parseInt(s))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "MOBS_NO_ORBES" -> {
                            variable = "MOBS_NO_ORBES"
                            for (s in valor.split(",")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    MOBS_NO_ORBES.add(Integer.parseInt(s))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "MOBS_TRAMPOSOS" -> {
                            variable = "MOBS_TRAMPOSOS"
                            for (s in valor.split(",")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    MOBS_TRAMPOSOS.add(Integer.parseInt(s))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "NPCS_VENDE_OBJETOS_STATS_MAXIMOS", "IDS_NPCS_VENDE_OBJETOS_STATS_MAXIMOS" -> {
                            variable = "IDS_NPCS_VENDE_OBJETOS_STATS_MAXIMOS"
                            for (s in valor.split(",")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    IDS_NPCS_VENDE_OBJETOS_STATS_MAXIMOS.add(Integer.parseInt(s))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "IDS_OBJETOS_STATS_MAXIMOS", "OBJETOS_STATS_MAXIMOS" -> {
                            variable = "IDS_OBJETOS_STATS_MAXIMOS"
                            for (s in valor.split(",")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    IDS_OBJETOS_STATS_MAXIMOS.add(Integer.parseInt(s))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "IDS_OBJETOS_STATS_MINIMOS", "OBJETOS_STATS_MINIMOS" -> {
                            variable = "IDS_OBJETOS_STATS_MINIMOS"
                            for (s in valor.split(",")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    IDS_OBJETOS_STATS_MINIMOS.add(Integer.parseInt(s))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "IDS_OBJETOS_STATS_RANDOM", "OBJETOS_STATS_RANDOM" -> {
                            variable = "IDS_OBJETOS_STATS_RANDOM"
                            for (s in valor.split(",")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    IDS_OBJETOS_STATS_RANDOM.add(Integer.parseInt(s))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "OGRINAS_POR_VOTO" -> {
                            OGRINAS_POR_VOTO = Short.parseShort(valor)
                            variable = "OGRINAS_POR_VOTO"
                        }
                        "VALOR_KAMAS_POR_OGRINA" -> {
                            VALOR_KAMAS_POR_OGRINA = Integer.parseInt(valor)
                            variable = "VALOR_KAMAS_POR_OGRINA"
                        }
                        "DIAS_INTERCAMBIO_COMPRAR_SISTEMA_ITEMS", "DIAS_INTERCAMBIO_COMPRAR_PANEL_ITEMS" -> {
                            DIAS_INTERCAMBIO_COMPRAR_SISTEMA_ITEMS = Integer.parseInt(valor)
                            variable = "DIAS_INTERCAMBIO_COMPRAR_SISTEMA_ITEMS"
                        }
                        "KAMAS_RULETA_JALATO" -> {
                            KAMAS_RULETA_JALATO = Integer.parseInt(valor)
                            variable = "KAMAS_RULETA_JALATO"
                        }
                        "KAMAS_MOSTRAR_PROBABILIDAD_FORJA_FM", "KAMAS_MOSTRAR_PROBABILIDAD_FORJAMAGIA", "KAMAS_MOSTRAR_PROBABILIDAD_FORJA" -> {
                            KAMAS_MOSTRAR_PROBABILIDAD_FORJA = Integer.parseInt(valor)
                            variable = "KAMAS_MOSTRAR_PROBABILIDAD_FORJA"
                        }
                        "KAMAS_BANCO" -> {
                            KAMAS_BANCO = Integer.parseInt(valor)
                            variable = "KAMAS_BANCO"
                        }
                        "SABIDURIA_PARA_REENVIO" -> {
                            SABIDURIA_PARA_REENVIO = Integer.parseInt(valor)
                            variable = "SABIDURIA_PARA_REENVIO"
                        }
                        "VALOR_BASE_RECAUDADOR" -> {
                            VALOR_BASE_RECAUDADOR = Integer.parseInt(valor)
                            variable = "VALOR_BASE_RECAUDADOR"
                        }
                        "DISMINUIR_PERDIDA_HONOR" -> {
                            DISMINUIR_PERDIDA_HONOR = Integer.parseInt(valor)
                            variable = "DISMINUIR_PERDIDA_HONOR"
                        }
                        "NIVEL_PJ_RESET" -> {
                            NIVEL_PJ_RESET = Integer.parseInt(valor)
                            variable = "NIVEL_PJ_RESET"
                        }
                        "PARAMS_HECHIZO_CAC_MULTIMAN" -> {
                            PARAMS_HECHIZO_CAC_MULTIMAN = Integer.parseInt(valor)
                            variable = "PARAMS_HECHIZO_CAC_MULTIMAN"
                        }
                        "ID_MIMOBIONTE" -> {
                            ID_MIMOBIONTE = Integer.parseInt(valor)
                            variable = "ID_MIMOBIONTE"
                        }
                        "ID_ORBE" -> {
                            ID_ORBE = Integer.parseInt(valor)
                            variable = "ID_ORBE"
                        }
                        "CANTIDAD_GRUPO_MOBS_MOVER_POR_MAPA" -> {
                            CANTIDAD_GRUPO_MOBS_MOVER_POR_MAPA = Integer.parseInt(valor)
                            variable = "CANTIDAD_GRUPO_MOBS_MOVER_POR_MAPA"
                        }
                        "MAX_CARACTERES_SONIDO" -> {
                            MAX_CARACTERES_SONIDO = Integer.parseInt(valor)
                            variable = "MAX_CARACTERES_SONIDO"
                        }
                        "RESET_APRENDER_OMEGA" -> {
                            RESET_APRENDER_OMEGA = Integer.parseInt(valor)
                            variable = "RESET_APRENDER_OMEGA"
                        }
                        "MAX_PACKETS_PARA_RASTREAR" -> {
                            MAX_PACKETS_PARA_RASTREAR = Integer.parseInt(valor)
                            variable = "MAX_PACKETS_PARA_RASTREAR"
                        }
                        "BONUS_UNICA_IP" -> {
                            BONUS_UNICA_IP = Integer.parseInt(valor) / 100
                            variable = "BONUS_UNICA_IP"
                        }
                        "CLON_PLACAHE_HUIDA" -> {
                            CLON_PLACAHE_HUIDA = Integer.parseInt(valor)
                            variable = "CLON_PLACAHE_HUIDA"
                        }
                        "MAX_PACKETS_DESCONOCIDOS" -> {
                            MAX_PACKETS_DESCONOCIDOS = Integer.parseInt(valor)
                            variable = "MAX_PACKETS_DESCONOCIDOS"
                        }
                        "MININMO_CANTIDAD_MOBS_EN_GRUPO", "MIN_CANTIDAD_MOBS_EN_GRUPO" -> {
                            MIN_CANTIDAD_MOBS_EN_GRUPO = Integer.parseInt(valor)
                            variable = "MIN_CANTIDAD_MOBS_EN_GRUPO"
                        }
                        "MAX_CUENTAS_POR_IP" -> {
                            MAX_CUENTAS_POR_IP = Integer.parseInt(valor)
                            variable = "MAX_CUENTAS_POR_IP"
                        }
                        "TIME_SLEEP_PACKETS_CARGAR_MAPA" -> {
                            TIME_SLEEP_PACKETS_CARGAR_MAPA = Integer.parseInt(valor)
                            variable = "TIME_SLEEP_PACKETS_CARGAR_MAPA"
                        }
                        "MAX_ID_OBJETO_MODELO", "ID_OBJETO_MODELO_MAX" -> {
                            MAX_ID_OBJETO_MODELO = Integer.parseInt(valor)
                            variable = "MAX_ID_OBJETO_MODELO"
                        }
                        "CANTIDAD_MIEMBROS_EQUIPO_KOLISEO", "CANTIDAD_VS_KOLISEO" -> {
                            CANTIDAD_MIEMBROS_EQUIPO_KOLISEO = Integer.parseInt(valor)
                            if (CANTIDAD_MIEMBROS_EQUIPO_KOLISEO > 3) {
                                CANTIDAD_MIEMBROS_EQUIPO_KOLISEO = 3
                            } else if (CANTIDAD_MIEMBROS_EQUIPO_KOLISEO < 1) {
                                CANTIDAD_MIEMBROS_EQUIPO_KOLISEO = 1
                            }
                            variable = "CANTIDAD_MIEMBROS_EQUIPO_KOLISEO"
                        }
                        "CANTIDAD_MIEMBROS_EQUIPO_KOLISEO_VS1" -> {
                            CANTIDAD_MIEMBROS_EQUIPO_KOLISEO_VS1 = Integer.parseInt(valor)
                            variable = "CANTIDAD_MIEMBROS_EQUIPO_KOLISEO_VS1"
                        }
                        "CANTIDAD_MIEMBROS_EQUIPO_KOLISEO_VS2" -> {
                            CANTIDAD_MIEMBROS_EQUIPO_KOLISEO_VS2 = Integer.parseInt(valor)
                            variable = "CANTIDAD_MIEMBROS_EQUIPO_KOLISEO_VS2"
                        }
                        "RANGO_NIVEL_PVP" -> {
                            RANGO_NIVEL_PVP = Integer.parseInt(valor)
                            variable = "RANGO_NIVEL_PVP"
                        }
                        "RANGO_NIVEL_KOLISEO" -> {
                            RANGO_NIVEL_KOLISEO = Integer.parseInt(valor)
                            variable = "RANGO_NIVEL_KOLISEO"
                        }
                        "PROBABILIDAD_ESCAPAR_MONTURA_DESPUES_FECUNDAR" -> {
                            PROBABILIDAD_ESCAPAR_MONTURA_DESPUES_FECUNDAR = Integer.parseInt(valor)
                            variable = "PROBABILIDAD_ESCAPAR_MONTURA_DESPUES_FECUNDAR"
                        }
                        "HONOR_FIJO_PARA_TODOS" -> {
                            HONOR_FIJO_PARA_TODOS = Integer.parseInt(valor)
                            variable = "HONOR_FIJO_PARA_TODOS"
                        }
                        "RESET_MINIMO_PARA_PVP" -> {
                            RESET_MINIMO_PARA_PVP = Integer.parseInt(valor)
                            variable = "RESET_MINIMO_PARA_PVP"
                        }
                        "NIVEL_MINIMO_PARA_PVP" -> {
                            NIVEL_MINIMO_PARA_PVP = Integer.parseInt(valor)
                            variable = "NIVEL_MINIMO_PARA_PVP"
                        }
                        "ADIC_CAC_PVP" -> {
                            EfectoHechizo.MULTIPLICADOR_DAÑO_CAC_PVP = Float.parseFloat(valor)
                            variable = "MULTIPLICADOR_DAÑO_CAC_PVP"
                        }
                        "ADIC_CAC_KOLI" -> {
                            EfectoHechizo.MULTIPLICADOR_DAÑO_CAC_KOLI = Float.parseFloat(valor)
                            variable = "MULTIPLICADOR_DAÑO_CAC_KOLI"
                        }
                        "ADIC_CAC" -> {
                            EfectoHechizo.MULTIPLICADOR_DAÑO_CAC = Float.parseFloat(valor)
                            variable = "MULTIPLICADOR_DAÑO_CAC"
                        }
                        "ADIC_MOB" -> {
                            EfectoHechizo.MULTIPLICADOR_DAÑO_MOB = Float.parseFloat(valor)
                            variable = "MULTIPLICADOR_DAÑO_MOB"
                        }
                        "ADIC_PJ" -> {
                            EfectoHechizo.MULTIPLICADOR_DAÑO_PJ = Float.parseFloat(valor)
                            variable = "MULTIPLICADOR_DAÑO_PJ"
                        }
                        "PROBABILIDAD_ARCHI_MOBS" -> {
                            PROBABILIDAD_ARCHI_MOBS = Integer.parseInt(valor)
                            variable = "PROBABILIDAD_ARCHI_MOBS"
                        }
                        "MAX_PORCENTAJE_DE_STAT_PARA_FM", "PORCENTAJE_MAX_STAT_PARA_FM" -> {
                            MAX_PORCENTAJE_DE_STAT_PARA_FM = Integer.parseInt(valor)
                            variable = "MAX_PORCENTAJE_DE_STAT_PARA_FM"
                        }
                        "PROBABILIDAD_PROTECTOR_RECURSOS" -> {
                            PROBABILIDAD_PROTECTOR_RECURSOS = Integer.parseInt(valor)
                            variable = "PROBABILIDAD_PROTECTOR_RECURSOS"
                        }
                        "PROBABILIDAD_RECURSO_ESPECIAL", "PROBABILIDAD_OBJ_ESPECIAL" -> {
                            PROBABILIDAD_RECURSO_ESPECIAL = Integer.parseInt(valor)
                            variable = "PROBABILIDAD_RECURSO_ESPECIAL"
                        }
                        "PROBABILIDAD_LOST_STATS_FM", "PROBABLIDAD_PERDER_STATS_FM", "PROBABILIDAD_FALLO_FM" -> {
                            PROBABLIDAD_PERDER_STATS_FM = Integer.parseInt(valor)
                            variable = "PROBABLIDAD_PERDER_STATS_FM"
                        }
                        "MODO_MAPAS_LIMITE", "MODO_MAPAS_TEST" -> {
                            MODO_MAPAS_LIMITE = valor.equalsIgnoreCase("true")
                            variable = "MODO_MAPAS_LIMITE"
                        }
                        "STR_MAPAS_LIMITE", "STR_MAPAS_TEST" -> {
                            STR_MAPAS_LIMITE = valor
                            variable = "STR_MAPAS_LIMITE"
                        }
                        "STR_SUBAREAS_LIMITE", "STR_SUBAREAS_TEST" -> {
                            STR_SUBAREAS_LIMITE = valor
                            variable = "STR_SUBAREAS_LIMITE"
                        }
                        "SUFIJO_RESET" -> {
                            SUFIJO_RESET = valor
                            variable = "SUFIJO_RESET"
                        }
                        "MUTE_CANAL_INCARNAM" -> {
                            MUTE_CANAL_INCARNAM = valor.equalsIgnoreCase("true")
                            variable = "MUTE_CANAL_INCARNAM"
                        }
                        "MUTE_CANAL_RECLUTAMIENTO" -> {
                            MUTE_CANAL_RECLUTAMIENTO = valor.equalsIgnoreCase("true")
                            variable = "MUTE_CANAL_RECLUTAMIENTO"
                        }
                        "MUTE_CANAL_ALINEACION" -> {
                            MUTE_CANAL_ALINEACION = valor.equalsIgnoreCase("true")
                            variable = "MUTE_CANAL_ALINEACION"
                        }
                        "MUTE_CANAL_COMERCIO" -> {
                            MUTE_CANAL_COMERCIO = valor.equalsIgnoreCase("true")
                            variable = "MUTE_CANAL_COMERCIO"
                        }
                        "PARAM_RESTRINGIR_COLOR_DIA", "PARAM_SIEMPRE_DIA" -> {
                            PARAM_RESTRINGIR_COLOR_DIA = valor.equalsIgnoreCase("true")
                            variable = "PARAM_RESTRINGIR_COLOR_DIA"
                        }
                        "PARAM_PERMITIR_ORNAMENTOS" -> {
                            PARAM_PERMITIR_ORNAMENTOS = valor.equalsIgnoreCase("true")
                            variable = "PARAM_PERMITIR_ORNAMENTOS"
                        }
                        "PARAM_MOSTRAR_STATS_INVOCACION" -> {
                            PARAM_MOSTRAR_STATS_INVOCACION = valor.equalsIgnoreCase("true")
                            variable = "PARAM_MOSTRAR_STATS_INVOCACION"
                        }
                        "PARAM_BORRAR_PIEDRA_MISION" -> {
                            PARAM_BORRAR_PIEDRA_MISION = valor.equalsIgnoreCase("true")
                            variable = "PARAM_BORRAR_PIEDRA_MISION"
                        }
                        "PARAM_ENCRIPTAR_PACKETS" -> {
                            PARAM_ENCRIPTAR_PACKETS = valor.equalsIgnoreCase("true")
                            variable = "PARAM_ENCRIPTAR_PACKETS"
                        }
                        "PARAM_FM_CON_POZO_RESIDUAL" -> {
                            PARAM_FM_CON_POZO_RESIDUAL = valor.equalsIgnoreCase("true")
                            variable = "PARAM_FM_CON_POZO_RESIDUAL"
                        }
                        "PARAM_SISTEMA_IP_ESPERA", "SISTEMA_IP_ESPERA" -> {
                            PARAM_SISTEMA_IP_ESPERA = valor.equalsIgnoreCase("true")
                            variable = "PARAM_SISTEMA_IP_ESPERA"
                        }
                        "PARAM_PERMITIR_MULTICUENTA_PELEA_KOLISEO", "PARAM_MISMA_IP_VS_KOLISEO" -> {
                            PARAM_PERMITIR_MULTICUENTA_PELEA_KOLISEO = valor.equalsIgnoreCase("true")
                            variable = "PARAM_PERMITIR_MULTICUENTA_PELEA_KOLISEO"
                        }
                        "PARAM_PERMITIR_MULTICUENTA_PELEA_RECAUDADOR", "PARAM_MISMA_IP_VS_RECAUDADOR" -> {
                            PARAM_PERMITIR_MULTICUENTA_PELEA_RECAUDADOR = valor.equalsIgnoreCase("true")
                            variable = "PARAM_PERMITIR_MULTICUENTA_PELEA_RECAUDADOR"
                        }
                        "PARAM_PERMITIR_MULTICUENTA_PELEA_PVP", "PARAM_AGRESION_MULTICUENTA" -> {
                            PARAM_PERMITIR_MULTICUENTA_PELEA_PVP = valor.equalsIgnoreCase("true")
                            variable = "PARAM_PERMITIR_MULTICUENTA_PELEA_PVP"
                        }
                        "PARAM_MOSTRAR_EXP_MOBS" -> {
                            PARAM_MOSTRAR_EXP_MOBS = valor.equalsIgnoreCase("true")
                            variable = "PARAM_MOSTRAR_EXP_MOBS"
                        }
                        "PARAM_MOSTRAR_APODO_LISTA_AMIGOS" -> {
                            PARAM_MOSTRAR_APODO_LISTA_AMIGOS = valor.equalsIgnoreCase("true")
                            variable = "PARAM_MOSTRAR_APODO_LISTA_AMIGOS"
                        }
                        "PARAM_CLASIFICAR_POR_STUFF_EN_KOLISEO" -> {
                            PARAM_CLASIFICAR_POR_STUFF_EN_KOLISEO = valor.equalsIgnoreCase("true")
                            variable = "PARAM_CLASIFICAR_POR_STUFF_EN_KOLISEO"
                        }
                        "PARAM_CLASIFICAR_POR_RANKING_EN_KOLISEO" -> {
                            PARAM_CLASIFICAR_POR_RANKING_EN_KOLISEO = valor.equalsIgnoreCase("true")
                            variable = "PARAM_CLASIFICAR_POR_RANKING_EN_KOLISEO"
                        }
                        "PARAM_PERMITIR_MISMAS_CLASES_EN_KOLISEO" -> {
                            PARAM_PERMITIR_MISMAS_CLASES_EN_KOLISEO = valor.equalsIgnoreCase("true")
                            variable = "PARAM_PERMITIR_MISMAS_CLASES_EN_KOLISEO"
                        }
                        "PARAM_PERMITIR_DESACTIVAR_ALINEACION", "PARAM_PERMITIR_DESACTIVAR_ALAS" -> {
                            PARAM_PERMITIR_DESACTIVAR_ALAS = valor.equalsIgnoreCase("true")
                            variable = "PARAM_PERMITIR_DESACTIVAR_ALAS"
                        }
                        "PARAM_AGREDIR_ALAS_DESACTIVADAS", "PARAM_AGREDIR_PJ_ALAS_DESACTIVADAS" -> {
                            PARAM_AGREDIR_ALAS_DESACTIVADAS = valor.equalsIgnoreCase("true")
                            variable = "PARAM_AGREDIR_ALAS_DESACTIVADAS"
                        }
                        "MENSAJE_REBOOT", "MENSAJE_RESET", "MENSAJE_TIMER_REBOOT", "MENSAJE_TIMER_RESET" -> {
                            MENSAJE_TIMER_REBOOT = valor
                            variable = "MENSAJE_TIMER_REBOOT"
                        }
                        "PARAM_SISTEMA_ORBES" -> {
                            PARAM_SISTEMA_ORBES = valor.equalsIgnoreCase("true")
                            variable = "PARAM_SISTEMA_ORBES"
                        }
                        "PARAM_MATRIMONIO_GAY" -> {
                            PARAM_MATRIMONIO_GAY = valor.equalsIgnoreCase("true")
                            variable = "PARAM_MATRIMONIO_GAY"
                        }
                        "PARAM_PERMITIR_OFICIOS" -> {
                            PARAM_PERMITIR_OFICIOS = valor.equalsIgnoreCase("true")
                            variable = "PARAM_PERMITIR_OFICIOS"
                        }
                        "PARAM_SALVAR_LOGS_AGRESION_SQL" -> {
                            PARAM_SALVAR_LOGS_AGRESION_SQL = valor.equalsIgnoreCase("true")
                            variable = "PARAM_SALVAR_LOGS_AGRESION_SQL"
                        }
                        "PARAM_MOB_TENER_NIVEL_INVOCADOR_PARA_EMPUJAR" -> {
                            PARAM_MOB_TENER_NIVEL_INVOCADOR_PARA_EMPUJAR = valor.equalsIgnoreCase("true")
                            variable = "PARAM_MOB_TENER_NIVEL_INVOCADOR_PARA_EMPUJAR"
                        }
                        "PARAM_NO_USAR_CREDITOS" -> {
                            PARAM_NO_USAR_CREDITOS = valor.equalsIgnoreCase("true")
                            variable = "PARAM_NO_USAR_CREDITOS"
                        }
                        "PARAM_PERMITIR_DESHONOR" -> {
                            PARAM_PERMITIR_DESHONOR = valor.equalsIgnoreCase("true")
                            variable = "PARAM_PERMITIR_DESHONOR"
                        }
                        "PARAM_PERMITIR_MILICIANOS_EN_PELEA" -> {
                            PARAM_PERMITIR_MILICIANOS_EN_PELEA = valor.equalsIgnoreCase("true")
                            variable = "PARAM_PERMITIR_MILICIANOS_EN_PELEA"
                        }
                        "PARAM_PERMITIR_AGRESION_MILICIANOS" -> {
                            PARAM_PERMITIR_AGRESION_MILICIANOS = valor.equalsIgnoreCase("true")
                            variable = "PARAM_PERMITIR_AGRESION_MILICIANOS"
                        }
                        "PARAM_CAPTURAR_MONTURA_COMO_PERGAMINO" -> {
                            PARAM_CAPTURAR_MONTURA_COMO_PERGAMINO = valor.equalsIgnoreCase("true")
                            variable = "PARAM_CAPTURAR_MONTURA_COMO_PERGAMINO"
                        }
                        "PARAM_EXPULSAR_PREFASE_PVP" -> {
                            PARAM_EXPULSAR_PREFASE_PVP = valor.equalsIgnoreCase("true")
                            variable = "PARAM_EXPULSAR_PREFASE_PVP"
                        }
                        "PARAM_TODOS_MOBS_EN_BESTIARIO" -> {
                            PARAM_TODOS_MOBS_EN_BESTIARIO = valor.equalsIgnoreCase("true")
                            variable = "PARAM_TODOS_MOBS_EN_BESTIARIO"
                        }
                        "PARAM_INFO_DAÑO_BATALLA" -> {
                            PARAM_INFO_DAÑO_BATALLA = valor.equalsIgnoreCase("true")
                            variable = "PARAM_INFO_DAÑO_BATALLA"
                        }
                        "PARAM_BOOST_SACRO_DESBUFEABLE" -> {
                            PARAM_BOOST_SACRO_DESBUFEABLE = valor.equalsIgnoreCase("true")
                            variable = "PARAM_BOOST_SACRO_DESBUFEABLE"
                        }
                        "PARAM_REINICIAR_CANALES" -> {
                            PARAM_REINICIAR_CANALES = valor.equalsIgnoreCase("true")
                            variable = "PARAM_REINICIAR_CANALES"
                        }
                        "PARAM_CAMBIAR_NOMBRE_CADA_RESET" -> {
                            PARAM_CAMBIAR_NOMBRE_CADA_RESET = valor.equalsIgnoreCase("true")
                            variable = "PARAM_CAMBIAR_NOMBRE_CADA_RESET"
                        }
                        "PARAM_NO_USAR_OGRINAS" -> {
                            PARAM_NO_USAR_OGRINAS = valor.equalsIgnoreCase("true")
                            variable = "PARAM_NO_USAR_OGRINAS"
                        }
                        "PARAM_REGISTRO_LOGS_JUGADORES", "PARAM_REGISTRO_JUGADORES" -> {
                            PARAM_REGISTRO_LOGS_JUGADORES = valor.equalsIgnoreCase("true")
                            variable = "PARAM_REGISTRO_LOGS_JUGADORES"
                        }
                        "PARAM_REGISTRO_LOGS_SQL" -> {
                            PARAM_REGISTRO_LOGS_SQL = valor.equalsIgnoreCase("true")
                            variable = "PARAM_REGISTRO_LOGS_SQL"
                        }
                        "PARAM_BOTON_BOUTIQUE" -> {
                            PARAM_BOTON_BOUTIQUE = valor.equalsIgnoreCase("true")
                            variable = "PARAM_BOTON_BOUTIQUE"
                        }
                        "PARAM_AUTO_SALTAR_TURNO" -> {
                            PARAM_AUTO_SALTAR_TURNO = valor.equalsIgnoreCase("true")
                            variable = "PARAM_AUTO_SALTAR_TURNO"
                        }
                        "PARAM_TITULO_MAESTRO_OFICIO" -> {
                            PARAM_TITULO_MAESTRO_OFICIO = valor.equalsIgnoreCase("true")
                            variable = "PARAM_TITULO_MAESTRO_OFICIO"
                        }
                        "PARAM_GANAR_KAMAS_PVP" -> {
                            PARAM_GANAR_KAMAS_PVP = valor.equalsIgnoreCase("true")
                            variable = "PARAM_GANAR_KAMAS_PVP"
                        }
                        "PARAM_GANAR_EXP_PVP" -> {
                            PARAM_GANAR_EXP_PVP = valor.equalsIgnoreCase("true")
                            variable = "PARAM_GANAR_EXP_PVP"
                        }
                        "PARAM_MASCOTAS_PERDER_VIDA" -> {
                            PARAM_MASCOTAS_PERDER_VIDA = valor.equalsIgnoreCase("true")
                            variable = "PARAM_MASCOTAS_PERDER_VIDA"
                        }
                        "PARAM_LIMITE_MIEMBROS_GREMIO" -> {
                            PARAM_LIMITE_MIEMBROS_GREMIO = valor.equalsIgnoreCase("true")
                            variable = "PARAM_LIMITE_MIEMBROS_GREMIO"
                        }
                        "PARAM_MOSTRAR_PROBABILIDAD_TACLEO" -> {
                            PARAM_MOSTRAR_PROBABILIDAD_TACLEO = valor.equalsIgnoreCase("true")
                            variable = "PARAM_MOSTRAR_PROBABILIDAD_TACLEO"
                        }
                        "PARAM_AGRESION_ADMIN" -> {
                            PARAM_AGRESION_ADMIN = valor.equalsIgnoreCase("true")
                            variable = "PARAM_AGRESION_ADMIN"
                        }
                        "PARAM_RESET_STATS_PLAYERS" -> {
                            PARAM_RESET_STATS_PLAYERS = valor.equalsIgnoreCase("true")
                            variable = "PARAM_RESET_STATS_PLAYERS"
                        }
                        "PARAM_GANAR_HONOR_RANDOM" -> {
                            PARAM_GANAR_HONOR_RANDOM = valor.equalsIgnoreCase("true")
                            variable = "PARAM_GANAR_HONOR_RANDOM"
                        }
                        "PARAM_SISTEMA_ITEMS_SOLO_PERFECTO", "PARAM_SISTEMA_ITEMS_PERFECTO" -> {
                            PARAM_SISTEMA_ITEMS_SOLO_PERFECTO = valor.equalsIgnoreCase("true")
                            variable = "PARAM_SISTEMA_ITEMS_SOLO_PERFECTO"
                        }
                        "PARAM_SISTEMA_ITEMS_EXO_PA_PM" -> {
                            PARAM_SISTEMA_ITEMS_EXO_PA_PM = valor.equalsIgnoreCase("true")
                            variable = "PARAM_SISTEMA_ITEMS_EXO_PA_PM"
                        }
                        "PARAM_FORMULA_TIPO_OFICIAL" -> {
                            PARAM_FORMULA_TIPO_OFICIAL = valor.equalsIgnoreCase("true")
                            variable = "PARAM_FORMULA_TIPO_OFICIAL"
                        }
                        "PARAM_BORRAR_CUENTAS_VIEJAS", "BORRAR_CUENTAS_VIEJAS" -> {
                            PARAM_BORRAR_CUENTAS_VIEJAS = valor.equalsIgnoreCase("true")
                            variable = "PARAM_BORRAR_CUENTAS_VIEJAS"
                        }
                        "PARAM_MOSTRAR_IP_CONECTANDOSE" -> {
                            PARAM_MOSTRAR_IP_CONECTANDOSE = valor.equalsIgnoreCase("true")
                            variable = "PARAM_MOSTRAR_IP_CONECTANDOSE"
                        }
                        "PARAM_TIMER_ACCESO" -> {
                            PARAM_TIMER_ACCESO = valor.equalsIgnoreCase("true")
                            variable = "PARAM_TIMER_ACCESO"
                        }
                        "PARAM_START_EMOTES_COMPLETOS", "PARAM_START_EMOTES" -> {
                            PARAM_START_EMOTES_COMPLETOS = valor.equalsIgnoreCase("true")
                            variable = "PARAM_START_EMOTES_COMPLETOS"
                        }
                        "PARAM_CRIAR_MONTURA", "PARAM_CRIAR_DRAGOPAVO" -> {
                            PARAM_CRIAR_MONTURA = valor.equalsIgnoreCase("true")
                            variable = "PARAM_CRIAR_MONTURA"
                        }
                        "PARAM_MOVER_MOBS_FIJOS" -> {
                            PARAM_MOVER_MOBS_FIJOS = valor.equalsIgnoreCase("true")
                            variable = "PARAM_MOVER_MOBS_FIJOS"
                        }
                        "PARAM_MOBS_RANDOM_REAPARECER_OTRA_CELDA" -> {
                            PARAM_MOBS_RANDOM_REAPARECER_OTRA_CELDA = valor.equalsIgnoreCase("true")
                            variable = "PARAM_MOBS_RANDOM_REAPARECER_OTRA_CELDA"
                        }
                        "PARAM_ALIMENTAR_MASCOTAS" -> {
                            PARAM_ALIMENTAR_MASCOTAS = valor.equalsIgnoreCase("true")
                            variable = "PARAM_ALIMENTAR_MASCOTAS"
                        }
                        "PARAM_LOTERIA" -> {
                            PARAM_LOTERIA = valor.equalsIgnoreCase("true")
                            variable = "PARAM_LOTERIA"
                        }
                        "PARAM_LOTERIA_OGRINAS", "LOTERIA_OGRINAS" -> {
                            PARAM_LOTERIA_OGRINAS = valor.equalsIgnoreCase("true")
                            variable = "PARAM_LOTERIA_OGRINAS"
                        }
                        "PARAM_PERDER_PDV_ARMAS_ETEREAS", "PARAM_LOST_PDV_WEAPONS_ETHEREES" -> {
                            PARAM_PERDER_PDV_ARMAS_ETEREAS = valor.equalsIgnoreCase("true")
                            variable = "PARAM_PERDER_PDV_ARMAS_ETEREAS"
                        }
                        "PARAM_MENSAJE_ASESINOS_HEROICO" -> {
                            PARAM_MENSAJE_ASESINOS_HEROICO = valor.equalsIgnoreCase("true")
                            variable = "PARAM_MENSAJE_ASESINOS_HEROICO"
                        }
                        "PARAM_CONTRA_DANO" -> {
                            PARAM_CONTRA_DANO = valor.equalsIgnoreCase("true")
                            variable = "PARAM_CONTRA_DANO"
                        }
                        "PARAM_MENSAJE_ASESINOS_PVP" -> {
                            PARAM_MENSAJE_ASESINOS_PVP = valor.equalsIgnoreCase("true")
                            variable = "PARAM_MENSAJE_ASESINOS_PVP"
                        }
                        "PARAM_MENSAJE_ASESINOS_KOLISEO" -> {
                            PARAM_MENSAJE_ASESINOS_KOLISEO = valor.equalsIgnoreCase("true")
                            variable = "PARAM_MENSAJE_ASESINOS_KOLISEO"
                        }
                        "PARAM_GUARDAR_LOGS_INTERCAMBIOS" -> {
                            PARAM_GUARDAR_LOGS_INTERCAMBIOS = valor.equalsIgnoreCase("true")
                            variable = "PARAM_GUARDAR_LOGS_INTERCAMBIOS"
                        }
                        "PARAM_AGRESION_NEUTRALES", "PARAM_AGREDIR_NEUTRALES", "PARAM_AGRESION_NEUTRAL", "PARAM_AGREDIR_NEUTRAL" -> {
                            PARAM_AGREDIR_NEUTRAL = valor.equalsIgnoreCase("true")
                            variable = "PARAM_AGREDIR_NEUTRAL"
                        }
                        "BD_AUTO_COMMIT", "PARAM_AUTO_COMMIT" -> {
                            PARAM_AUTO_COMMIT = valor.equalsIgnoreCase("true")
                            variable = "PARAM_AUTO_COMMIT"
                        }
                        "PARAM_HEROICO_PIERDE_ITEMS_VIP" -> {
                            PARAM_HEROICO_PIERDE_ITEMS_VIP = valor.equalsIgnoreCase("true")
                            variable = "PARAM_HEROICO_PIERDE_ITEMS_VIP"
                        }
                        "PARAM_ESTRELLAS_RECURSOS" -> {
                            PARAM_ESTRELLAS_RECURSOS = valor.equalsIgnoreCase("true")
                            variable = "PARAM_ESTRELLAS_RECURSOS"
                        }
                        "PARAM_ALMANAX" -> {
                            PARAM_ALMANAX = valor.equalsIgnoreCase("true")
                            variable = "PARAM_ALMANAX"
                        }
                        "PARAM_DEVOLVER_OGRINAS" -> {
                            PARAM_DEVOLVER_OGRINAS = valor.equalsIgnoreCase("true")
                            variable = "PARAM_DEVOLVER_OGRINAS"
                        }
                        "PARAM_HEROICO_GAME_OVER" -> {
                            PARAM_HEROICO_GAME_OVER = valor.equalsIgnoreCase("true")
                            variable = "PARAM_HEROICO_GAME_OVER"
                        }
                        "PARAM_KOLISEO" -> {
                            PARAM_KOLISEO = valor.equalsIgnoreCase("true")
                            variable = "PARAM_KOLISEO"
                        }
                        "PARAM_ALBUM_MOBS", "PARAM_BESTIARIO", "PARAM_ALBUM" -> {
                            PARAM_BESTIARIO = valor.equalsIgnoreCase("true")
                            variable = "PARAM_BESTIARIO"
                        }
                        "PARAM_AUTO_PDV", "PARAM_AUTO_RECUPERAR_VIDA", "PARAM_AUTO_CURAR", "PARAM_AUTO_SANAR" -> {
                            PARAM_AUTO_RECUPERAR_TODA_VIDA = valor.equalsIgnoreCase("true")
                            variable = "PARAM_AUTO_RECUPERAR_TODA_VIDA"
                        }
                        "PARAM_VER_JUGADORES_KOLISEO" -> {
                            PARAM_VER_JUGADORES_KOLISEO = valor.equalsIgnoreCase("true")
                            variable = "PARAM_VER_JUGADORES_KOLISEO"
                        }
                        "PARAM_OBJETOS_OGRINAS_LIGADO" -> {
                            PARAM_OBJETOS_OGRINAS_LIGADO = valor.equalsIgnoreCase("true")
                            variable = "PARAM_OBJETOS_OGRINAS_LIGADO"
                        }
                        "PARAM_NOMBRE_COMPRADOR" -> {
                            PARAM_NOMBRE_COMPRADOR = valor.equalsIgnoreCase("true")
                            variable = "PARAM_NOMBRE_COMPRADOR"
                        }
                        "PARAM_VARIOS_RECAUDADORES" -> {
                            PARAM_VARIOS_RECAUDADORES = valor.equalsIgnoreCase("true")
                            variable = "PARAM_VARIOS_RECAUDADORES"
                        }
                        "PARAM_AGREDIR_JUGADORES_ASESINOS" -> {
                            PARAM_AGREDIR_JUGADORES_ASESINOS = valor.equalsIgnoreCase("true")
                            variable = "PARAM_AGREDIR_JUGADORES_ASESINOS"
                        }
                        "PARAM_STOP_SEGUNDERO", "STOP_SEGUNDERO" -> {
                            PARAM_STOP_SEGUNDERO = valor.equalsIgnoreCase("true")
                            variable = "PARAM_STOP_SEGUNDERO"
                        }
                        "PARAM_DELETE_PLAYERS_BUG", "PARAM_ELIMINAR_PERSONAJES_BUG" -> {
                            PARAM_ELIMINAR_PERSONAJES_BUG = valor.equalsIgnoreCase("true")
                            variable = "PARAM_ELIMINAR_PERSONAJES_BUG"
                        }
                        "PARAM_PERDER_ENERGIA" -> {
                            PARAM_PERDER_ENERGIA = valor.equalsIgnoreCase("true")
                            variable = "PARAM_PERDER_ENERGIA"
                        }
                        "PARAM_LADDER_NIVEL", "PARAM_RANKING_NIVEL" -> {
                            PARAM_LADDER_NIVEL = valor.equalsIgnoreCase("true")
                            variable = "PARAM_RANKING_NIVEL"
                        }
                        "PARAM_LADDER_STAFF", "PARAM_RANKING_STAFF" -> {
                            PARAM_LADDER_STAFF = valor.equalsIgnoreCase("true")
                            variable = "PARAM_RANKING_STAFF"
                        }
                        "PARAM_LADDER_KOLISEO", "PARAM_RANKING_KOLISEO" -> {
                            PARAM_LADDER_KOLISEO = valor.equalsIgnoreCase("true")
                            variable = "PARAM_RANKING_KOLISEO"
                        }
                        "PARAM_LADDER_PVP", "PARAM_RANKING_PVP" -> {
                            PARAM_LADDER_PVP = valor.equalsIgnoreCase("true")
                            variable = "PARAM_RANKING_PVP"
                        }
                        "PARAM_LADDER_GREMIO", "PARAM_RANKING_GREMIO" -> {
                            PARAM_LADDER_GREMIO = valor.equalsIgnoreCase("true")
                            variable = "PARAM_RANKING_GREMIO"
                        }
                        "PARAM_LADDER_EXP_DIA", "PARAM_RANKING_EXP_DIA" -> {
                            PARAM_LADDER_EXP_DIA = valor.equalsIgnoreCase("true")
                            variable = "PARAM_RANKING_EXP_DIA"
                        }
                        "PARAM_ANTI_SPEEDHACK" -> {
                            PARAM_ANTI_SPEEDHACK = valor.equalsIgnoreCase("true")
                            variable = "PARAM_ANTI_SPEEDHACK"
                        }
                        "PARAM_DESACTIVAR_MERCANTE" -> {
                            PARAM_DESACTIVAR_MERCANTE = valor.equalsIgnoreCase("true")
                            variable = "PARAM_DESACTIVAR_MERCANTE"
                        }
                        "PARAM_MOSTRAR_CHAT_VIP_TODOS" -> {
                            PARAM_MOSTRAR_CHAT_VIP_TODOS = valor.equalsIgnoreCase("true")
                            variable = "PARAM_MOSTRAR_CHAT_VIP_TODOS"
                        }
                        "PARAM_SOLO_PRIMERA_VEZ" -> {
                            PARAM_SOLO_PRIMERA_VEZ = valor.equalsIgnoreCase("true")
                            variable = "PARAM_SOLO_PRIMERA_VEZ"
                        }
                        "PARAM_PRECIO_RECURSOS_EN_OGRINAS", "PARAM_RECURSOS_EN_OGRINAS" -> {
                            PARAM_PRECIO_RECURSOS_EN_OGRINAS = valor.equalsIgnoreCase("true")
                            variable = "PARAM_PRECIO_RECURSOS_EN_OGRINAS"
                        }
                        "PARAM_PVP" -> {
                            PARAM_PVP = valor.equalsIgnoreCase("true")
                            variable = "PARAM_PVP"
                        }
                        "PARAM_AURA" -> {
                            PARAM_ACTIVAR_AURA = valor.equalsIgnoreCase("true")
                            variable = "PARAM_ACTIVAR_AURA"
                        }
                        "PARAM_AURA_VIP" -> {
                            PARAM_AURA_VIP = valor.equalsIgnoreCase("true")
                            variable = "PARAM_AURA_VIP"
                        }
                        "PARAM_AURA_VIP_2" -> {
                            PARAM_AURA_VIP_2 = valor.equalsIgnoreCase("true")
                            variable = "PARAM_AURA_VIP_2"
                        }
                        "PARAM_CRAFT_SIEMPRE_EXITOSA", "PARAM_RECETA_SIEMPRE_EXITOSA" -> {
                            PARAM_CRAFT_SIEMPRE_EXITOSA = valor.equalsIgnoreCase("true")
                            variable = "PARAM_CRAFT_SIEMPRE_EXITOSA"
                        }
                        "PARAM_CRAFT_PERFECTO_STATS", "PARAM_CRAFT_PERFECTO" -> {
                            PARAM_CRAFT_PERFECTO_STATS = valor.equalsIgnoreCase("true")
                            variable = "PARAM_CRAFT_PERFECTO_STATS"
                        }
                        "PARAM_MONTURA_SIEMPRE_MONTABLES" -> {
                            PARAM_MONTURA_SIEMPRE_MONTABLES = valor.equalsIgnoreCase("true")
                            variable = "PARAM_MONTURA_SIEMPRE_MONTABLES"
                        }
                        "JUGAR_RAPIDO", "PARAM_JUGAR_RAPIDO" -> {
                            PARAM_JUGAR_RAPIDO = valor.equalsIgnoreCase("true")
                            variable = "PARAM_JUGAR_RAPIDO"
                        }
                        "PARAM_PERMITIR_MOBS", "PARAM_MOBS" -> {
                            PARAM_PERMITIR_MOBS = valor.equalsIgnoreCase("true")
                            variable = "PARAM_PERMITIR_MOBS"
                        }
                        "PARAM_COMANDOS_JUGADOR" -> {
                            PARAM_COMANDOS_JUGADOR = valor.equalsIgnoreCase("true")
                            variable = "PARAM_COMANDOS_JUGADOR"
                        }
                        "PARAM_ANTI_DDOS" -> {
                            PARAM_ANTI_DDOS = valor.equalsIgnoreCase("true")
                            variable = "PARAM_ANTI_DDOS"
                        }
                        "PARAM_MOSTRAR_NRO_TURNOS" -> {
                            PARAM_MOSTRAR_NRO_TURNOS = valor.equalsIgnoreCase("true")
                            variable = "PARAM_MOSTRAR_NRO_TURNOS"
                        }
                        "PARAM_RESET_STATS_OBJETO" -> {
                            PARAM_RESET_STATS_OBJETO = valor.equalsIgnoreCase("true")
                            variable = "PARAM_RESET_STATS_OBJETO"
                        }
                        "PARAM_OBJETOS_PEFECTOS_COMPRADOS_NPC" -> {
                            PARAM_OBJETOS_PEFECTOS_COMPRADOS_NPC = valor.equalsIgnoreCase("true")
                            variable = "PARAM_OBJETOS_PEFECTOS_COMPRADOS_NPC"
                        }
                        "MODO_ESTADO_PESADO" -> {
                            MODO_ESTADO_PESADO = valor.equalsIgnoreCase("true")
                            variable = "MODO_ESTADO_PESADO"
                        }
                        "PARAM_CINEMATIC_CREAR_PERSONAJE" -> {
                            PARAM_CINEMATIC_CREAR_PERSONAJE = valor.equalsIgnoreCase("true")
                            variable = "PARAM_CINEMATIC_CREAR_PERSONAJE"
                        }
                        "PARAM_DAR_ALINEACION_AUTOMATICA" -> {
                            PARAM_DAR_ALINEACION_AUTOMATICA = valor.equalsIgnoreCase("true")
                            variable = "PARAM_DAR_ALINEACION_AUTOMATICA"
                        }
                        "MODO_PVP_STRONG" -> {
                            MODO_PVP_STRONG = valor.equalsIgnoreCase("true")
                            variable = "MODO_PVP_STRONG"
                        }
                        "REFRESCA_PANEL_KOLISEO" -> {
                            REFRESCA_PANEL_KOLISEO = valor.equalsIgnoreCase("true")
                            variable = "REFRESCA_PANEL_KOLISEO"
                        }
                        "MODO_PVP_AGRESIVO" -> {
                            MODO_PVP_AGRESIVO = valor.equalsIgnoreCase("true")
                            variable = "MODO_PVP_AGRESIVO"
                        }
                        "MODO_PUEDE_AGREDIR_OCUPADO" -> {
                            MODO_PUEDE_AGREDIR_OCUPADO = valor.equalsIgnoreCase("true")
                            variable = "MODO_PUEDE_AGREDIR_OCUPADO"
                        }
                        "MODO_PVP" -> {
                            MODO_PVP = valor.equalsIgnoreCase("true")
                            variable = "MODO_PVP"
                        }
                        "PARAM_MOSTRAR_MENSAJE_NIVEL_MAXIMO" -> {
                            PARAM_MOSTRAR_MENSAJE_NIVEL_MAXIMO = valor.equalsIgnoreCase("true")
                            variable = "PARAM_MOSTRAR_MENSAJE_NIVEL_MAXIMO"
                        }
                        "NPC_RANKING_PVP" -> {
                            NPC_RANKING_PVP = Integer.parseInt(valor)
                            variable = "NPC_RANKING_PVP"
                        }
                        "NPC_RANKING_KOLISEO" -> {
                            NPC_RANKING_KOLISEO = Integer.parseInt(valor)
                            variable = "NPC_RANKING_KOLISEO"
                        }
                        "MODO_EXILED" -> {
                            MODO_EXILED = valor.equalsIgnoreCase("true")
                            variable = "MODO_EXILED"
                        }
                        "AUTO_AGREDIR_MISION" -> {
                            AUTO_AGREDIR_MISION = valor.equalsIgnoreCase("true")
                            variable = "AUTO_AGREDIR_MISION"
                        }
                        "LIMITE_STATS_PERCO_PERSONALIZADOS" -> {
                            LIMITE_STATS_PERCO_PERSONALIZADOS = valor.equalsIgnoreCase("true")
                            variable = "LIMITE_STATS_PERCO_PERSONALIZADOS"
                        }
                        "LIMITE_STATS_PVP_PERSONALIZADOS" -> {
                            LIMITE_STATS_PVP_PERSONALIZADOS = valor.equalsIgnoreCase("true")
                            variable = "LIMITE_STATS_PVP_PERSONALIZADOS"
                        }
                        "LIMITE_STATS_KOLI_PERSONALIZADOS" -> {
                            LIMITE_STATS_KOLI_PERSONALIZADOS = valor.equalsIgnoreCase("true")
                            variable = "LIMITE_STATS_KOLI_PERSONALIZADOS"
                        }
                        "PARAMS_SCROLL_NO_RESET" -> {
                            PARAMS_SCROLL_NO_RESET = valor.equalsIgnoreCase("true")
                            variable = "PARAMS_SCROLL_NO_RESET"
                        }
                        "PARAMS_EQUIPAR_SET_PELEA" -> {
                            PARAMS_EQUIPAR_SET_PELEA = valor.equalsIgnoreCase("true")
                            variable = "PARAMS_EQUIPAR_SET_PELEA"
                        }
                        "PARAMS_OBJETO_FABRUSHIO" -> {
                            PARAMS_OBJETO_FABRUSHIO = valor.equalsIgnoreCase("true")
                            variable = "PARAMS_OBJETO_FABRUSHIO"
                        }
                        "PARAMS_MOSTRAR_TITULO_VIP" -> {
                            PARAMS_MOSTRAR_TITULO_VIP = valor.equalsIgnoreCase("true")
                            variable = "PARAMS_MOSTRAR_TITULO_VIP"
                        }
                        "ELIMINAR_MUERTO" -> {
                            ELIMINAR_MUERTO = valor.equalsIgnoreCase("true")
                            variable = "ELIMINAR_MUERTO"
                        }
                        "DOBLE_EXPERIENCIA" -> {
                            DOBLE_EXPERIENCIA = valor.equalsIgnoreCase("true")
                            variable = "DOBLE_EXPERIENCIA"
                        }
                        "RESET_NO_ALAS" -> {
                            RESET_NO_ALAS = valor.equalsIgnoreCase("true")
                            variable = "RESET_NO_ALAS"
                        }
                        "PARAM_MERCA_OGRINAS" -> {
                            PARAM_MERCA_OGRINAS = valor.equalsIgnoreCase("true")
                            variable = "PARAM_MERCA_OGRINAS"
                        }
                        "MODO_ALL_OGRINAS" -> {
                            MODO_ALL_OGRINAS = valor.equalsIgnoreCase("true")
                            variable = "MODO_ALL_OGRINAS"
                        }
                        "MODO_CANAL_VIP_INCARMAN" -> {
                            MODO_CANAL_VIP_INCARMAN = valor.equalsIgnoreCase("true")
                            variable = "MODO_CANAL_VIP_INCARMAN"
                        }
                        "MODO_IMPACT" -> {
                            MODO_IMPACT = valor.equalsIgnoreCase("true")
                            variable = "MODO_IMPACT"
                        }
                        "COMPRAR_OBJETOS_VIP_NO_PERFECTOS" -> {
                            COMPRAR_OBJETOS_VIP_NO_PERFECTOS = valor.equalsIgnoreCase("true")
                            variable = "COMPRAR_OBJETOS_VIP_NO_PERFECTOS"
                        }
                        "MODO_SOLO_PVP" -> {
                            MODO_SOLO_PVP = valor.equalsIgnoreCase("true")
                            variable = "MODO_SOLO_PVP"
                        }
                        "SON_DE_LUCIANO" -> {
                            SON_DE_LUCIANO = valor.equalsIgnoreCase("true")
                            variable = "SON_DE_LUCIANO"
                        }
                        "PARAMS_XP_DESAFIO" -> {
                            PARAMS_XP_DESAFIO = valor.equalsIgnoreCase("true")
                            variable = "PARAMS_XP_DESAFIO"
                        }
                        "MODO_HEROICO" -> {
                            MODO_HEROICO = valor.equalsIgnoreCase("true")
                            variable = "MODO_HEROICO"
                        }
                        "MODO_ANKALIKE" -> {
                            MODO_ANKALIKE = valor.equalsIgnoreCase("true")
                            variable = "MODO_ANKALIKE"
                        }
                        "MODO_BATTLE_ROYALE" -> {
                            MODO_BATTLE_ROYALE = valor.equalsIgnoreCase("true")
                            variable = "MODO_BATTLE_ROYALE"
                        }
                        "MODO_BETA" -> {
                            MODO_BETA = valor.equalsIgnoreCase("true")
                            variable = "MODO_BETA"
                        }
                        "MODO_DESCONECTAR_PACKET_MALO" -> {
                            MODO_DESCONECTAR_PACKET_MALO = valor.equalsIgnoreCase("true")
                            variable = "MODO_DESCONECTAR_PACKET_MALO"
                        }
                        "DESHABILITAR_SQL" -> {
                            PARAM_DESHABILITAR_SQL = valor.equalsIgnoreCase("true")
                            variable = "DESHABILITAR_SQL"
                        }
                        "PROBABILIDAD_MONTURAS_MACHOS_HEMBRAS", "PROBABILIDAD_MONTURAS_MACHOS_Y_HEMBRAS" -> {
                            val sss: Array<String?> = valor.split(",")
                            val machos: Int = Integer.parseInt(sss[0])
                            val hembras: Int = Integer.parseInt(sss[1])
                            Montura.SEXO_POSIBLES = ByteArray(machos + hembras)
                            var i = 0
                            while (i < machos + hembras) {
                                Montura.SEXO_POSIBLES.get(i) = (if (i < machos) 0 else 1).toByte()
                                i++
                            }
                            variable = "PROBABILIDAD_MONTURAS_MACHOS_HEMBRAS"
                        }
                        "RULETA_1", "RULETA_2", "RULETA_3", "RULETA_4", "RULETA_5", "RULETA_6", "RULETA_7", "RULETA_8" -> {
                            val ficha: Int = Integer.parseInt(valor.split(";").get(0))
                            val premios: String = valor.split(";").get(1)
                            Mundo.RULETA.put(ficha, premios)
                        }
                        "TIPO_RECURSOS" -> {
                            variable = "TIPO_RECURSOS"
                            for (s in valor.split(",")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    TIPO_RECURSOS.add(Short.parseShort(s))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "OBJ_NO_PERMITIDOS" -> {
                            variable = "OBJ_NO_PERMITIDOS"
                            for (s in valor.split(",")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    OBJ_NO_PERMITIDOS.add(Integer.parseInt(s))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "CLASES_NO_PERMITIDAS_RESET" -> {
                            variable = "CLASES_NO_PERMITIDAS_RESET"
                            for (s in valor.split(",")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    CLASES_NO_PERMITIDAS_RESET.add(Byte.parseByte(s))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "SUBAREAS_NO_PVP" -> {
                            variable = "SUBAREAS_NO_PVP"
                            for (s in valor.split(",")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    SUBAREAS_NO_PVP.add(Integer.parseInt(s))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "HECHIZOS_NO_BORRAR" -> {
                            variable = "HECHIZOS_NO_BORRAR"
                            for (s in valor.split(",")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    HECHIZOS_NO_BORRAR.add(Integer.parseInt(s))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "ITEM_CONSUMIBLE_AUTOMATICO" -> {
                            variable = "ITEM_CONSUMIBLE_AUTOMATICO"
                            for (s in valor.split(",")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    ITEM_CONSUMIBLE_AUTOMATICO.add(Integer.parseInt(s))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "PRECIO_MEDIO_ITEM" -> {
                            variable = "PRECIO_MEDIO_ITEM"
                            for (s in valor.split(",")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    PRECIO_MEDIO_ITEM.add(Integer.parseInt(s))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "TIPO_ALIMENTO_MONTURA" -> {
                            variable = "TIPO_ALIMENTO_MONTURA"
                            for (s in valor.split(",")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    TIPO_ALIMENTO_MONTURA.add(Short.parseShort(s))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "HORARIO_DIA" -> {
                            val dia: Array<String?> = valor.split(":")
                            try {
                                val h: Int = Integer.parseInt(dia[0])
                                if (h >= 0 && h <= 23) {
                                    HORA_DIA = h
                                }
                            } catch (e: Exception) {
                            }
                            try {
                                val h: Int = Integer.parseInt(dia[1])
                                if (h >= 0 && h <= 59) {
                                    MINUTOS_DIA = h
                                }
                            } catch (e: Exception) {
                            }
                        }
                        "HORARIO_NOCHE" -> {
                            val noche: Array<String?> = valor.split(":")
                            try {
                                val h: Int = Integer.parseInt(noche[0])
                                if (h >= 0 && h <= 23) {
                                    HORA_NOCHE = h
                                }
                            } catch (e: Exception) {
                            }
                            try {
                                val h: Int = Integer.parseInt(noche[1])
                                if (h >= 0 && h <= 59) {
                                    MINUTOS_NOCHE = h
                                }
                            } catch (e: Exception) {
                            }
                        }
                        "PALABRAS_PROHIBIDAS", "BLOCK_WORD" -> {
                            variable = "PALABRAS_PROHIBIDAS"
                            for (s in valor.split(",")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    PALABRAS_PROHIBIDAS.add(s.toLowerCase())
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "OBJETOS_FORTIFICADOS" -> {
                            variable = "OBJETOS_FORTIFICADOS"
                            for (s in valor.split(",")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    OBJETOS_FORTIFICADOS.add(s.toLowerCase())
                                } catch (e: Exception) {
                                }
                            }
                        }
                        "HECHIZOS_NO_DESECHIZABLE" -> {
                            variable = "HECHIZOS_NO_DESECHIZABLE"
                            for (s in valor.split(",")) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    HECHIZOS_NO_DESECHIZABLE.add(Integer.parseInt(s))
                                } catch (e: Exception) {
                                }
                            }
                        }
                        else -> if (!parametro.isEmpty() && parametro.charAt(0) !== '#') {
                            System.out.println("NO EXISTE EL COMANDO O PARAMETRO : $parametro")
                        }
                    }
                    if (!variable.isEmpty()) {
                        if (repetidos[variable] != null) {
                            if (perso != null) {
                                GestorSalida.ENVIAR_BAT2_CONSOLA(perso, ("Config Exception COMMAND REPEAT "
                                        + parametro.toUpperCase()) + " WITH " + repetidos[variable])
                            }
                            System.out.println("EL PARAMETRO " + parametro.toUpperCase().toString() + " ES SIMILAR AL PARAMETRO "
                                    + repetidos[variable].toString() + " POR FAVOR ELIMINA UNO")
                            if (Mundo.SERVIDOR_ESTADO === Constantes.SERVIDOR_OFFLINE) {
                                System.exit(1)
                            }
                            return
                        }
                        repetidos.put(variable, parametro.toUpperCase())
                    }
                } catch (e: Exception) {
                }
            }
            config.close()
            if (BD_ESTATICA == null || BD_CUENTAS == null || BD_DINAMICA == null || BD_HOST == null || BD_PASS == null || BD_USUARIO == null) {
                throw Exception()
            }
            if (BD_HOST_DINAMICA == null) {
                System.out.println("BD dinamica == BD_HOST")
                BD_HOST_DINAMICA = BD_HOST
            }
            if (MAPAS_PVP.isEmpty()) {
                MAPAS_PVP.add(4422)
                MAPAS_PVP.add(7810)
                MAPAS_PVP.add(952)
                MAPAS_PVP.add(1132)
                MAPAS_PVP.add(833)
            }
        } catch (e: Exception) {
            if (perso != null) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(perso, "Config Exception DONT FILE")
            }
            System.out.println(e.toString())
            System.out.println("Ficha de la configuración no existe o ilegible")
            System.out.println("Cerrando el server")
            if (Mundo.SERVIDOR_ESTADO === Constantes.SERVIDOR_OFFLINE) {
                System.exit(1)
            }
            return
        }
        for (s in PERMITIR_MULTIMAN.split(",")) {
            try {
                PERMITIR_MULTIMAN_TIPO_COMBATE.add(Byte.parseByte(s))
            } catch (e: Exception) {
            }
        }
        for (str in GFX_CREA_TU_ITEM_CAPAS.split(",")) {
            try {
                Constantes.GFX_CREA_TU_ITEM_CAPAS.add(Integer.parseInt(str))
            } catch (e: Exception) {
            }
        }
        for (str in GFX_CREA_TU_ITEM_CINTURONES.split(",")) {
            try {
                Constantes.GFX_CREA_TU_ITEM_CINTURONES.add(Integer.parseInt(str))
            } catch (e: Exception) {
            }
        }
        for (str in GFX_CREA_TU_ITEM_SOMBREROS.split(",")) {
            try {
                Constantes.GFX_CREA_TU_ITEM_SOMBREROS.add(Integer.parseInt(str))
            } catch (e: Exception) {
            }
        }
        for (str in GFX_CREA_TU_ITEM_DOFUS.split(",")) {
            try {
                Constantes.GFX_CREA_TU_ITEM_DOFUS.add(Integer.parseInt(str))
            } catch (e: Exception) {
            }
        }
        for (str in GFX_CREA_TU_ITEM_ANILLOS.split(",")) {
            try {
                Constantes.GFX_CREA_TU_ITEM_ANILLOS.add(Integer.parseInt(str))
            } catch (e: Exception) {
            }
        }
        for (str in GFX_CREA_TU_ITEM_AMULETOS.split(",")) {
            try {
                Constantes.GFX_CREA_TU_ITEM_AMULETOS.add(Integer.parseInt(str))
            } catch (e: Exception) {
            }
        }
        for (str in GFX_CREA_TU_ITEM_ESCUDOS.split(",")) {
            try {
                Constantes.GFX_CREA_TU_ITEM_ESCUDOS.add(Integer.parseInt(str))
            } catch (e: Exception) {
            }
        }
        for (str in GFX_CREA_TU_ITEM_BOTAS.split(",")) {
            try {
                Constantes.GFX_CREA_TU_ITEM_BOTAS.add(Integer.parseInt(str))
            } catch (e: Exception) {
            }
        }
        Constantes.GFXS_CREA_TU_ITEM.put(Constantes.OBJETO_TIPO_CAPA, Constantes.GFX_CREA_TU_ITEM_CAPAS)
        Constantes.GFXS_CREA_TU_ITEM.put(Constantes.OBJETO_TIPO_SOMBRERO, Constantes.GFX_CREA_TU_ITEM_SOMBREROS)
        Constantes.GFXS_CREA_TU_ITEM.put(Constantes.OBJETO_TIPO_CINTURON, Constantes.GFX_CREA_TU_ITEM_CINTURONES)
        Constantes.GFXS_CREA_TU_ITEM.put(Constantes.OBJETO_TIPO_BOTAS, Constantes.GFX_CREA_TU_ITEM_BOTAS)
        Constantes.GFXS_CREA_TU_ITEM.put(Constantes.OBJETO_TIPO_AMULETO, Constantes.GFX_CREA_TU_ITEM_AMULETOS)
        Constantes.GFXS_CREA_TU_ITEM.put(Constantes.OBJETO_TIPO_ANILLO, Constantes.GFX_CREA_TU_ITEM_ANILLOS)
        Constantes.GFXS_CREA_TU_ITEM.put(Constantes.OBJETO_TIPO_ESCUDO, Constantes.GFX_CREA_TU_ITEM_ESCUDOS)
        Constantes.GFXS_CREA_TU_ITEM.put(Constantes.OBJETO_TIPO_DOFUS, Constantes.GFX_CREA_TU_ITEM_DOFUS)
        COMANDOS_PERMITIDOS.add("z")
        COMANDOS_PERMITIDOS.add("zx")
        COMANDOS_PERMITIDOS.add("reports")
        COMANDOS_PERMITIDOS.add("reportes")
        COMANDOS_PERMITIDOS.add("endaction")
        COMANDOS_PERMITIDOS.add("finaccion")
        if (IP_MULTISERVIDOR.isEmpty()) {
            IP_MULTISERVIDOR.add("127.0.0.1")
        }
        if (NIVEL_MAX_ESCOGER_NIVEL > NIVEL_MAX_PERSONAJE) {
            NIVEL_MAX_ESCOGER_NIVEL = NIVEL_MAX_PERSONAJE
        }
        if (MODO_ANKALIKE) {
            PARAM_CRIAR_MONTURA = true
            PARAM_PVP = true
            PARAM_PERMITIR_MOBS = true
            PROBABILIDAD_RECURSO_ESPECIAL = 75
        }
        if (perso != null) {
            GestorSalida.ENVIAR_BAT2_CONSOLA(perso, "CONFIG LOADED PERFECTLY!!!")
        }
    }

    fun startTorneo() {
        val lot: TimerTask = object : TimerTask() {
            @Override
            fun run() {
                this.cancel()
                if (TorneoOn) {
                    if (faseTorneo == 0 && ServidorSocket.PjsTorneo.size() === 8) {
                        System.out.println("aca")
                        ServidorSocket.iniciarTorneo()
                    } else if (faseTorneo == 1 && ServidorSocket.PjsTorneo2.size() === 4) {
                        ServidorSocket.iniciarTorneo()
                    } else if (faseTorneo == 2 && ServidorSocket.PjsTorneo3.size() === 2) {
                        ServidorSocket.iniciarTorneo()
                    }
                }
            }
        }
        if (_globalTime == null) _globalTime = Timer()
        _globalTime.schedule(lot, 5000, 5000)
    }

    /*
	public static void cargarConfiguracionIP(Personaje perso) {
		try {
			final BufferedReader config = new BufferedReader(new FileReader(ARCHIVO_CONFIG));
			String linea = "";
			ArrayList<String> parametros = new ArrayList<>();
			Map<String, String> repetidos = new TreeMap<>();
			while ((linea = config.readLine()) != null) {
				try {
					final String parametro = linea.split("=", 2)[0].trim();
					String valor = linea.split("=", 2)[1].trim();
					if (parametros.contains(parametro)) {
						System.out.println("EN EL ARCHIVO " + ARCHIVO_CONFIG + " SE REPITE EL PARAMETRO " + parametro
								+ ", Borre uno porque si no tomara en cuenta el que encuentre primero.");
						return;
					} else {
						parametros.add(parametro);
					}
					String variable = "";
					valor = valor.replace("\\n", "\n").replace("\\t", "\t").replace("\\r", "\r").replace("\\b", "\b");
					switch (parametro.toUpperCase()) {
					case "IP_PUBLIC_SERVER":
					case "IP_SERVIDOR_PUBLICA":
					case "IP_SERVIDOR_FIJA":
					case "IP_FIJA_SERVIDOR":
					case "IP_FIX_SERVER":
					case "IP_PUBLICA_SERVIDOR":
						variable = "IP_PUBLICA_SERVIDOR";
						//if (IP_PERMTIDAS.contains(valor) || IP_PERMTIDAS.contains("*")) {
							IP_PUBLICA_SERVIDOR = valor;
						//}
						break;
					case "IP_MULTISERVIDOR":
					case "IP_MULTISERVER":
						variable = "IP_MULTISERVIDOR";
						for (String s : valor.split(";")) {
							if (s.isEmpty()) {
								continue;
							}
							//if (IP_PERMTIDAS.contains(s) || IP_PERMTIDAS.contains("*")) {
								try {
									InetAddress in = InetAddress.getByName(s);
									IP_MULTISERVIDOR.add(in.getHostAddress());
									//IP_MULTISERVIDOR2.add(s);
								} catch (Exception e) {
									IP_MULTISERVIDOR.add(s);
									//IP_MULTISERVIDOR2.add(s);
								}
							//}
						}
						break;
					}
					if (!variable.isEmpty()) {
						if (repetidos.get(variable) != null) {
							if (perso != null) {
								GestorSalida.ENVIAR_BAT2_CONSOLA(perso, "Config Exception COMMAND REPEAT "
										+ parametro.toUpperCase() + " WITH " + repetidos.get(variable));
							}
							System.out.println("EL PARAMETRO " + parametro.toUpperCase() + " ES SIMILAR AL PARAMETRO "
									+ repetidos.get(variable) + " POR FAVOR ELIMINA UNO");
							if (Mundo.SERVIDOR_ESTADO == Constantes.SERVIDOR_OFFLINE) {
								System.exit(1);
							}
							return;
						}
						repetidos.put(variable, parametro.toUpperCase());
					}
				} catch (Exception e) {
				}
			}
			config.close();
		} catch (final Exception e) {
			if (perso != null) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(perso, "Config Exception DONT FILE");
			}
			System.out.println(e.toString());
			System.out.println("Ficha de la configuración no existe o ilegible");
			System.out.println("Cerrando el server");
			if (Mundo.SERVIDOR_ESTADO == Constantes.SERVIDOR_OFFLINE) {
				System.exit(1);
			}
			return;
		}
		if (IP_MULTISERVIDOR.isEmpty()) {
			IP_MULTISERVIDOR.add("127.0.0.1");
			//IP_MULTISERVIDOR2.add("127.0.0.1");
		}
	}
*/
    fun redactarLogServidorSinPrint(str: String?) {
        try {
            if (LOG_SERVIDOR == null) {
                return
            }
            val hora: Date = Calendar.getInstance().getTime()
            LOG_SERVIDOR.println("[$hora]  $str")
            LOG_SERVIDOR.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun redactarLogServidorln(str: String?) {
        try {
            if (MODO_IMPRIMIR_LOG) System.out.println(str)
            if (LOG_SERVIDOR == null) {
                return
            }
            val hora: Date = Calendar.getInstance().getTime()
            LOG_SERVIDOR.println("[$hora]  $str")
            LOG_SERVIDOR.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun redactarLogServidor(str: String?) {
        try {
            if (MODO_IMPRIMIR_LOG) System.out.print(str)
            if (LOG_SERVIDOR == null) {
                return
            }
            val hora: Date = Calendar.getInstance().getTime()
            LOG_SERVIDOR.print("[$hora]  $str")
            LOG_SERVIDOR.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun imprimirLogPlayers() {
        try {
            if (ServidorSocket.REGISTROS.isEmpty()) {
                return
            }
            val dia: String = (Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString() + "-"
                    + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "-"
                    + Calendar.getInstance().get(Calendar.YEAR))
            val dir = File("Logs_Players_" + NOMBRE_SERVER)
            if (!dir.exists()) {
                dir.mkdir()
            }
            val dir2 = File("Logs_Players_" + NOMBRE_SERVER + "/" + dia)
            if (!dir2.exists()) {
                dir2.mkdir()
            }
            for (entry in ServidorSocket.REGISTROS.entrySet()) {
                if (!PARAM_REGISTRO_LOGS_JUGADORES && !ServidorSocket.JUGADORES_REGISTRAR.contains(entry.getKey())) {
                    continue
                }
                val log = PrintStream(FileOutputStream(
                        "Logs_Players_" + NOMBRE_SERVER + "/" + dia + "/Log_" + entry.getKey() + "_" + dia + ".txt",
                        true))
                log.println(entry.getValue().toString())
                log.flush()
                log.close()
            }
            ServidorSocket.REGISTROS.clear()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressWarnings("resource")
    fun imprimirLogCombates() {
        try {
            if (Pelea.LOG_COMBATES.length() === 0) {
                return
            }
            val fecha: String = (Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString() + "-"
                    + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "-"
                    + Calendar.getInstance().get(Calendar.YEAR))
            try {
                val f = FileOutputStream(
                        "Logs_Combates_" + NOMBRE_SERVER + "/Log_Combates_" + fecha + ".txt", true)
                val log = PrintStream(f)
                log.flush()
            } catch (e: IOException) {
                File("Logs_Combates_" + NOMBRE_SERVER).mkdir()
                val log = PrintStream(FileOutputStream(
                        "Log_Combates_" + NOMBRE_SERVER + "/Log_Combates_" + fecha + ".txt", true))
                log.println("----- FECHA -----\t- TIPO -\t-- MAPA --\t-------- PANEL RESULTADOS --------")
                log.flush()
                log.close()
            }
            val log = PrintStream(
                    FileOutputStream("Log_Combates_" + NOMBRE_SERVER + "/Log_Combates_" + fecha + ".txt", true))
            log.println(Pelea.LOG_COMBATES.toString())
            log.flush()
            log.close()
            Pelea.LOG_COMBATES = StringBuilder()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressWarnings("resource")
    fun imprimirLogSQL() {
        try {
            if (GestorSQL.LOG_SQL.length() === 0) {
                return
            }
            val fecha: String = (Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString() + "-"
                    + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "-"
                    + Calendar.getInstance().get(Calendar.YEAR))
            try {
                val f = FileOutputStream("Logs_SQL_" + NOMBRE_SERVER + "/Log_SQL_" + fecha + ".txt",
                        true)
                val log = PrintStream(f)
                log.flush()
            } catch (e: IOException) {
                File("Logs_SQL_" + NOMBRE_SERVER).mkdir()
            }
            val log = PrintStream(
                    FileOutputStream("Logs_SQL_" + NOMBRE_SERVER + "/Log_SQL_" + fecha + ".txt", true))
            log.println(GestorSQL.LOG_SQL.toString())
            log.flush()
            log.close()
            GestorSQL.LOG_SQL = StringBuilder()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun cerrarServer() {
        // GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION_TODOS("CERRANDO SERVIDOR /
        // CLOSING SERVER
        // / FERMER SERVEUR");
        GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS("115;1 seconde")
        redactarLogServidorln(" ######## INICIANDO CIERRE DEL SERVIDOR  ########")
        try {
            val estabaCorriendo = Mundo.SERVIDOR_ESTADO !== Constantes.SERVIDOR_OFFLINE
            Mundo.setServidorEstado(Constantes.SERVIDOR_OFFLINE)
            if (estabaCorriendo) {
                // GestorSQL.UPDATE_TODAS_CUENTAS_CERO();
                Mundo.devolverBoletos()
                while (Mundo.SALVANDO) {
                    try {
                        Thread.sleep(5000)
                    } catch (e: Exception) {
                    }
                }
                Mundo.salvarServidor(false)
                Mundo.salvarMapasEstrellas()
                try {
                    Thread.sleep(1000)
                } catch (e: Exception) {
                }
                redactarLogServidor(" ########  CERRANDO SERVERSOCKET...  ")
                ServidorServer.cerrarSocketServidor()
                redactarLogServidorln("... IS OK  ########")
            }
            try {
                Thread.sleep(1000)
            } catch (e: Exception) {
            }
        } catch (e: Exception) {
            redactarLogServidorln("EXCEPTION MIENTRAS SE CERRABA EL SERVIDOR : " + e.toString())
            e.printStackTrace()
        }
        redactarLogServidorln(" ########  IMPRIMIENDO LOGS PLAYERS  ########")
        imprimirLogPlayers()
        if (PARAM_REGISTRO_LOGS_SQL) {
            redactarLogServidorln(" ########  IMPRIMIENDO LOGS SQL  ########")
            imprimirLogPlayers()
        }
        redactarLogServidorln(" ########  SERVIDOR CERRO CON EXITO  ########")
    }

    fun resetRates() {
        RATE_XP_PVM = DEFECTO_XP_PVM
        RATE_XP_PVP = DEFECTO_XP_PVP
        RATE_XP_OFICIO = DEFECTO_XP_OFICIO
        RATE_HONOR = DEFECTO_XP_HONOR
        RATE_DROP_NORMAL = DEFECTO_DROP
        RATE_KAMAS = DEFECTO_KAMAS
        RATE_CRIANZA_MONTURA = DEFECTO_CRIANZA_MONTURA
    }

    private class IniciarSincronizacion : Thread() {
        fun run() {
            SincronizadorSocket()
            ACTIVO = false
        }

        companion object {
            private var ACTIVO = false
        }

        init {
            if (ACTIVO) {
                return
            }
            ACTIVO = true
            this.setDaemon(true)
            this.setPriority(5)
            this.start()
        }
    }
}