package estaticos

import java.util.ArrayList

class Mundo {
    val auraSize: Int
        get() = AURAS.size()
    val auras: Map<Any, Any>
        get() = AURAS

    private class CompMasaMenos : Comparator<Logro?> {
        fun compare(p1: Logro, p2: Logro): Int {
            return Integer.valueOf(p2.getPos()).compareTo(p1.getPos())
        }
    }

    private class CompNivelMasMenos : Comparator<Personaje?> {
        @Override
        fun compare(p1: Personaje, p2: Personaje): Int {
            return String(p2.getExperiencia()).compareTo(String(p1.getExperiencia()))
        }
    }

    private class CompDiaMasMenos : Comparator<Personaje?> {
        fun compare(p1: Personaje, p2: Personaje): Int {
            return Long.valueOf(p2.getExperienciaDia()).compareTo(p1.getExperienciaDia())
        }
    }

    private class CompGremioMasMenos : Comparator<Gremio?> {
        fun compare(p1: Gremio, p2: Gremio): Int {
            return Long.valueOf(p2.getExperiencia()).compareTo(p1.getExperiencia())
        }
    }

    private class CompPVPMasMenos : Comparator<RankingPVP?> {
        fun compare(p1: RankingPVP, p2: RankingPVP): Int {
            val v: Int = Long.valueOf(p2.getVictorias()).compareTo(p1.getVictorias() as Long)
            return if (v == 0) {
                Long.valueOf(p1.getDerrotas()).compareTo(p2.getDerrotas() as Long)
            } else v
        }
    }

    private class CompKoliseoMasMenos : Comparator<RankingKoliseo?> {
        fun compare(p1: RankingKoliseo, p2: RankingKoliseo): Int {
            val v: Int = Long.valueOf(p2.getVictorias()).compareTo(p1.getVictorias() as Long)
            return if (v == 0) {
                Long.valueOf(p1.getDerrotas()).compareTo(p2.getDerrotas() as Long)
            } else v
        }
    }

    fun setSucess(sucess: Map<Integer, Sucess>) {
        SUCCESS = sucess
    }

    fun getSucess(guid: Int): Sucess? {
        return SUCCESS[guid]
    }

    class Duo<L, R>(var _primero: L, var _segundo: R)
    class Experiencia(var _personaje: String, var _oficio: Int, var _montura: Int, var _gremio: Long, var _alineacion: Int,
                      encarnacion: Int, omega: String?) {
        var _encarnacion: Long
        var _omega: BigInteger = BigInteger("0")

        init {
            _encarnacion = encarnacion.toLong()
            _omega = BigInteger(omega)
        }
    }

    companion object {
        // Fijos
        var HECHIZOS_OMEGA: Map<Integer, StatHechizo> = TreeMap<Integer, StatHechizo>()
        var MAPAS: Map<Integer, Mapa> = TreeMap<Integer, Mapa>()
        var AREAS: Map<Integer, Area> = TreeMap<Integer, Area>()
        var IDOLOS: Map<Integer, Idolo> = TreeMap<Integer, Idolo>()
        var LOGROS: Map<Integer, Logro> = TreeMap<Integer, Logro>()
        var IDOLOS_HECHIZOS: ArrayList<Integer> = ArrayList()
        var STATS_NOMBRE: Map<Integer, String> = TreeMap()
        var SUPER_AREAS: Map<Integer, SuperArea> = TreeMap<Integer, SuperArea>()
        val calendar: Calendar = Calendar.getInstance()
        var SUB_AREAS: Map<Integer, SubArea> = TreeMap<Integer, SubArea>()
        var CERCADOS: Map<Integer, Cercado> = HashMap<Integer, Cercado>()
        var EXPERIENCIA: Map<Integer, Experiencia> = TreeMap<Integer, Experiencia>()
        var HECHIZOS: Map<Integer, Hechizo> = HashMap<Integer, Hechizo>()
        var OBJETOS_MODELOS: Map<Integer, ObjetoModelo> = HashMap<Integer, ObjetoModelo>()
        var SISTEMA_ITEMS: Map<Short, String?> = HashMap<Short, String>()
        var MOBS_MODELOS: Map<Integer, MobModelo> = HashMap<Integer, MobModelo>()
        var MONTURAS_MODELOS: Map<Integer, MonturaModelo> = HashMap<Integer, MonturaModelo>()
        var NPC_MODELOS: Map<Integer, NPCModelo> = HashMap<Integer, NPCModelo>()
        var NPC_PREGUNTAS: Map<Integer, PreguntaNPC> = HashMap<Integer, PreguntaNPC>()
        var NPC_RESPUESTAS: Map<Integer, RespuestaNPC> = HashMap<Integer, RespuestaNPC>()
        var OFICIOS: Map<Integer, Oficio> = HashMap<Integer, Oficio>()
        var RECETAS: Map<Integer, ArrayList<Duo<Integer, Integer>>> = HashMap<Integer, ArrayList<Duo<Integer, Integer>>>()
        var OBJETOS_SETS: Map<Integer, ObjetoSet> = HashMap<Integer, ObjetoSet>()
        var CASAS: Map<Integer, Casa> = HashMap<Integer, Casa>()
        var MERCADILLOS: Map<Integer, Mercadillo> = HashMap<Integer, Mercadillo>()
        var ANIMACIONES: Map<Integer, Animacion> = HashMap<Integer, Animacion>()
        var COFRES: Map<Integer, Cofre> = HashMap<Integer, Cofre>()
        var TUTORIALES: Map<Integer, Tutorial> = HashMap<Integer, Tutorial>()
        var LIBROS_ARTESANOS: Map<Integer, LibroArtesano?> = HashMap<Integer, LibroArtesano>()
        var OMEGAS: Map<Integer, Omega> = TreeMap<Integer, Omega>()
        var OBJETIVOS_MODELOS: Map<Integer, MisionObjetivoModelo> = HashMap<Integer, MisionObjetivoModelo>()
        var ENCARNACIONES_MODELOS: Map<Integer, EncarnacionModelo> = HashMap<Integer, EncarnacionModelo>()
        var ETAPAS: Map<Integer, MisionEtapaModelo> = HashMap<Integer, MisionEtapaModelo>()
        var MISIONES_MODELOS: Map<Integer, MisionModelo> = HashMap<Integer, MisionModelo>()
        var MASCOTAS_MODELOS: Map<Integer, MascotaModelo> = TreeMap<Integer, MascotaModelo>()
        var ESPECIALIDADES: Map<Integer, Especialidad> = TreeMap<Integer, Especialidad>()
        var DONES_MODELOS: Map<Integer, Integer> = TreeMap<Integer, Integer>()
        var OBJETOS_INTERACTIVOS_MODELOS: ArrayList<ObjetoInteractivoModelo> = ArrayList<ObjetoInteractivoModelo>()
        var SERVICIOS: Map<Integer, Servicio> = HashMap<Integer, Servicio>()
        var COMANDOS: Map<String, Integer?> = HashMap<String, Integer>()
        var MAPAS_ESTRELLAS: Map<Integer, ArrayList<Integer>> = HashMap<Integer, ArrayList<Integer>>()
        var MAPAS_HEROICOS: Map<Integer, ArrayList<String>> = HashMap<Integer, ArrayList<String>>()
        var MOBS_EVENTOS: Map<Byte, ArrayList<Duo<Integer, Integer>>> = HashMap<Byte, ArrayList<Duo<Integer, Integer>>>()
        var ALMANAX: Map<Integer, Almanax> = HashMap<Integer, Almanax>()
        var OTROS_INTERACTIVOS: CopyOnWriteArrayList<OtroInteractivo> = CopyOnWriteArrayList<OtroInteractivo>()
        var OBJETOS_INTERACTIVOS: ArrayList<ObjetoInteractivo> = ArrayList<ObjetoInteractivo>()
        var DROPS_FIJOS: ArrayList<DropMob> = ArrayList<DropMob>()
        var OBJETOS_TRUEQUE: ArrayList<ObjetoTrueque> = ArrayList()
        var ZAAPS: Map<Integer, Short?> = HashMap<Integer, Short>()
        var ZAAPIS_BONTA: ArrayList<Integer> = ArrayList<Integer>()
        var ZAAPIS_BRAKMAR: ArrayList<Integer> = ArrayList<Integer>()
        var CLASES: Map<Integer, Clase> = TreeMap<Integer, Clase>()
        var CREA_TU_ITEM: Map<Integer, CreaTuItem> = TreeMap<Integer, CreaTuItem>()
        var FUSIONES: ArrayList<Fusion> = ArrayList<Fusion>()
        var CRAFEOS: Map<Integer, Crafeo> = TreeMap()
        var EPICITEMS: Map<Integer, EpicItems> = TreeMap()
        var RULETA: Map<Integer, String> = TreeMap<Integer, String>()
        var ORNAMENTOS: Map<Integer, Ornamento> = TreeMap<Integer, Ornamento>()
        var AURAS: Map<Integer, Aura> = TreeMap<Integer, Aura>()
        var TITULOS: Map<Integer, Titulo> = TreeMap<Integer, Titulo>()
        var MASTER_RESET: Map<Integer, MasterReset> = TreeMap<Integer, MasterReset>()
        var LOGINPREMIOS: Map<String, LoginPremio> = TreeMap<String, LoginPremio>()
        var misionesPvP: Map<Integer, Integer> = HashMap<Integer, Integer>()
        var Prestigios: Map<Integer, String> = HashMap<Integer, String>()
        var _liderRankingPvp: String? = "Palmad"
        var _liderRankingPvp1: String? = "Palmad"
        var _liderRankingPvp2: String? = "Palmad"
        var _liderRankingKoliseo = "Palmad"
        var SUCCESS: Map<Integer, Sucess> = TreeMap<Integer, Sucess>()

        //
        // concurrentes
        //
        private val _CUENTAS: ConcurrentHashMap<Integer, Cuenta> = ConcurrentHashMap<Integer, Cuenta>()
        private val _CUENTAS_POR_NOMBRE: ConcurrentHashMap<String, Integer> = ConcurrentHashMap<String, Integer>()
        private val _PERSONAJES: ConcurrentHashMap<Integer, Personaje> = ConcurrentHashMap<Integer, Personaje>()
        private val _OBJETOS: ConcurrentHashMap<Integer, Objeto> = ConcurrentHashMap<Integer, Objeto>()
        private val _GREMIOS: ConcurrentHashMap<Integer, Gremio> = ConcurrentHashMap<Integer, Gremio>()
        private val _MONTURAS: ConcurrentHashMap<Integer, Montura> = ConcurrentHashMap<Integer, Montura>()
        private val _PRISMAS: ConcurrentHashMap<Integer, Prisma> = ConcurrentHashMap<Integer, Prisma>()
        private val _RECAUDADORES: ConcurrentHashMap<Integer, Recaudador> = ConcurrentHashMap<Integer, Recaudador>()
        var _RANKINGS_KOLISEO: ConcurrentHashMap<Integer, RankingKoliseo> = ConcurrentHashMap<Integer, RankingKoliseo>()
        private val _RANKINGS_PVP: ConcurrentHashMap<Integer, RankingPVP> = ConcurrentHashMap<Integer, RankingPVP>()

        // private static ConcurrentHashMap<Integer, Encarnacion> _ENCARNACIONES = new
        // ConcurrentHashMap<Integer, Encarnacion>();
        // private static ConcurrentHashMap<Integer, Personaje> _INSCRITOS_KOLISEO = new
        // ConcurrentHashMap<Integer, Personaje>();
        var INSCRITOS_KOLISEO_RANGO: ConcurrentHashMap<Integer, CopyOnWriteArrayList<Personaje>> = ConcurrentHashMap()
        var INSCRITOS_KOLISEO_RANGO_VS1: ConcurrentHashMap<Integer, CopyOnWriteArrayList<Personaje>> = ConcurrentHashMap()
        var INSCRITOS_KOLISEO_RANGO_VS2: ConcurrentHashMap<Integer, CopyOnWriteArrayList<Personaje>> = ConcurrentHashMap()

        //
        // publicas
        //
        var ZONAS: Map<Short, Short> = HashMap<Short, Short>()

        // public static ArrayList<Short> MAPAS_OBJETIVOS = new ArrayList<>();
        var CAPTCHAS: ArrayList<String> = ArrayList<String>()
        var CUENTAS_A_BORRAR: ArrayList<Cuenta> = ArrayList<Cuenta>()
        var ONLINES: CopyOnWriteArrayList<Personaje> = CopyOnWriteArrayList()

        //
        // variables primitivas
        //
        var BLOQUEANDO = false
        var VENDER_BOLETOS = false
        var SERVIDOR_ESTADO: Byte = Constantes.SERVIDOR_OFFLINE
        var LOTERIA_BOLETOS = IntArray(10000)
        var SIG_ID_LINEA_MERCADILLO = 0
        var SIG_ID_OBJETO = 0
        var SIG_ID_PERSONAJE = 0
        var SIG_ID_MONTURA = -101
        var SIG_ID_RECAUDADOR = -100
        var SIG_ID_PRISMA = -102
        var CANT_SALVANDO = 0
        var TOTAL_SALVADO = 0
        var SEGUNDOS_INICIO_KOLISEO = 0
        var DIA_DEL_AÑO = 0
        var MOB_EVENTO: Byte = 0
        var SEG_CUENTA_REGRESIVA: Long = 0
        var MINUTOS_VIDA_REAL: Long = 0
        var LIDER_RANKING = "Ninguno"
        var MSJ_CUENTA_REGRESIVA = ""
        var LISTA_GFX = ""
        var LISTA_NIVEL = ""
        var LISTA_ZONAS = ""
        var KAMAS_OBJ_CACERIA = ""
        var NOMBRE_CACERIA = ""
        var LISTA_MASCOTAS = ""
        var CLASES_PERMITIDAS = ""
        var CREA_TU_ITEM_OBJETOS = ""
        var CREA_TU_ITEM_DATA = ""
        var CREAT_TU_ITEM_PRECIOS = ""
        private val _LADDER_KOLISEO: CopyOnWriteArrayList<RankingKoliseo> = CopyOnWriteArrayList()
        private val _LADDER_PVP: CopyOnWriteArrayList<RankingPVP> = CopyOnWriteArrayList()
        private val _LADDER_NIVEL: CopyOnWriteArrayList<Personaje> = CopyOnWriteArrayList()
        private val _LADDER_EXP_DIA: CopyOnWriteArrayList<Personaje> = CopyOnWriteArrayList()
        private val _LADDER_GREMIO: CopyOnWriteArrayList<Gremio> = CopyOnWriteArrayList()
        fun crearServidor() {
            try {
                System.out.println("TotalMemory: " + (Runtime.getRuntime().totalMemory() / 1048576f).toString() + " MB\t" + "MaxMemory: "
                        + (Runtime.getRuntime().maxMemory() / 1048576f).toString() + " MB")
            } catch (e: Exception) {
            }
            for (s in Constantes.ZAAPI_BONTA.split(",")) {
                if (s.isEmpty()) {
                    continue
                }
                try {
                    ZAAPIS_BONTA.add(Integer.parseInt(s))
                } catch (e: Exception) {
                }
            }
            for (s in Constantes.ZAAPI_BRAKMAR.split(",")) {
                if (s.isEmpty()) {
                    continue
                }
                try {
                    ZAAPIS_BRAKMAR.add(Integer.parseInt(s))
                } catch (e: Exception) {
                }
            }
            DIA_DEL_AÑO = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
            MINUTOS_VIDA_REAL = Constantes.getTiempoActualEscala(1000 * 60)
            SEGUNDOS_INICIO_KOLISEO = MainServidor.SEGUNDOS_INICIAR_KOLISEO
            System.out.println("===========> Database Static <===========")
            GestorSQL.CARGAR_CREA_OBJETOS_PRECIOS()
            GestorSQL.CARGAR_CREA_OBJETOS_MODELOS()
            for (c in CREA_TU_ITEM.values()) {
                if (!CREA_TU_ITEM_OBJETOS.isEmpty()) {
                    CREA_TU_ITEM_OBJETOS += ","
                    CREA_TU_ITEM_DATA += "|"
                }
                CREA_TU_ITEM_OBJETOS += c.getID()
                CREA_TU_ITEM_DATA += (c.getID().toString() + ";" + c.getMaximosStats() + ";" + c.getMaxOgrinas() + ";"
                        + c.getPrecioBase())
            }
            System.out.print("Cargando las clases: ")
            GestorSQL.CARGAR_CLASES()
            System.out.println(CLASES.size().toString() + " clases cargadas")
            for (c in CLASES.values()) {
                if (!CLASES_PERMITIDAS.isEmpty()) {
                    CLASES_PERMITIDAS += ","
                }
                CLASES_PERMITIDAS += c.getID()
            }
            System.out.print("Cargando los servicios: ")
            GestorSQL.CARGAR_SERVICIOS()
            System.out.println(SERVICIOS.size().toString() + " servicios cargados")
            System.out.print("Cargando los ornamentos: ")
            GestorSQL.CARGAR_ORNAMENTOS()
            System.out.println(ORNAMENTOS.size().toString() + " ornamentos cargados")
            System.out.print("Cargando los titulos: ")
            GestorSQL.CARGAR_TITULOS()
            System.out.println(TITULOS.size().toString() + " titulos cargados")
            System.out.print("Cargando los comandos modelo: ")
            GestorSQL.CARGAR_COMANDOS_MODELO()
            System.out.println(COMANDOS.size().toString() + " comandos modelo cargados")
            System.out.print("Cargando los dones: ")
            GestorSQL.CARGAR_DONES_MODELOS()
            System.out.println(DONES_MODELOS.size().toString() + " dones cargados")
            System.out.print("Cargando las especialidades: ")
            GestorSQL.CARGAR_ESPECIALIDADES()
            System.out.println(ESPECIALIDADES.size().toString() + " especialidades cargadas")
            System.out.print("Cargando los misiones almanax: ")
            GestorSQL.CARGAR_ALMANAX()
            System.out.println(ALMANAX.size().toString() + " almanax cargados")
            System.out.print("Cargando los niveles de experiencia: ")
            GestorSQL.CARGAR_EXPERIENCIA()
            System.out.println(EXPERIENCIA.size().toString() + " niveles cargados")
            GestorSQL.CARGAR_HECHIZOS_OMEGA()
            System.out.print("Cargando los hechizos: ")
            GestorSQL.CARGAR_HECHIZOS()
            System.out.println(HECHIZOS.size().toString() + " hechizos cargados")
            if (MainServidor.PARAMS_HECHIZOS_NIVEL_7) {
                System.out.print("Cargando los hechizos Omega: ")
                System.out.println(HECHIZOS_OMEGA.size().toString() + " hechizos omega cargados")
            }
            System.out.print("Cargando las encarnaciones modelos: ")
            GestorSQL.CARGAR_ENCARNACIONES_MODELOS()
            System.out.println(ENCARNACIONES_MODELOS.size().toString() + " encarnaciones modelo cargados")
            System.out.print("Cargando los mounstros: ")
            GestorSQL.CARGAR_MOBS_MODELOS()
            GestorSQL.CARGAR_MOBS_RAROS()
            GestorSQL.CARGAR_MOBS_EVENTO()
            System.out.println(MOBS_MODELOS.size().toString() + " mounstros cargados")
            System.out.print("Cargando los objetos modelos: ")
            GestorSQL.CARGAR_OBJETOS_MODELOS()
            System.out.println(OBJETOS_MODELOS.size().toString() + " objetos modelos cargados")
            System.out.print("Cargando los sets de objetos: ")
            GestorSQL.CARGAR_OBJETOS_SETS()
            System.out.println(OBJETOS_SETS.size().toString() + " set de objetos cargados")
            System.out.print("Cargando las monturas modelos: ")
            GestorSQL.CARGAR_MONTURAS_MODELOS()
            System.out.println(MONTURAS_MODELOS.size().toString() + " monturas modelos cargados")
            System.out.print("Cargando los objetos trueque: ")
            GestorSQL.CARGAR_OBJETOS_TRUEQUE()
            System.out.println(OBJETOS_TRUEQUE.size().toString() + " objetos trueque cargados")
            System.out.print("Cargando los drops: ")
            System.out.println(GestorSQL.CARGAR_DROPS().toString() + " drops cargados")
            System.out.print("Cargando los drops fijos: ")
            System.out.println(GestorSQL.CARGAR_DROPS_FIJOS().toString() + " drops fijos cargados")
            System.out.print("Cargando los NPC: ")
            GestorSQL.CARGAR_NPC_MODELOS()
            System.out.println(NPC_MODELOS.size().toString() + " NPC cargados")
            System.out.print("Cargando las preguntas de NPC: ")
            GestorSQL.CARGAR_PREGUNTAS()
            System.out.println(NPC_PREGUNTAS.size().toString() + " preguntas de NPC cargadas")
            System.out.print("Cargando las respuestas de NPC: ")
            GestorSQL.CARGAR_RESPUESTAS()
            System.out.println(NPC_RESPUESTAS.size().toString() + " respuestas de NPC cargadas")
            System.out.print("Cargando las areas: ")
            GestorSQL.CARGAR_AREA()
            System.out.println(AREAS.size().toString() + " areas cargadas")
            System.out.print("Cargando las sub-areas: ")
            GestorSQL.CARGAR_SUBAREA()
            System.out.println(SUB_AREAS.size().toString() + " sub-areas cargadas")
            System.out.print("Cargando los objetos interactivos: ")
            GestorSQL.CARGAR_INTERACTIVOS()
            System.out.println(OBJETOS_INTERACTIVOS_MODELOS.size().toString() + " objetos interactivos cargados")
            System.out.print("Cargando las recetas: ")
            GestorSQL.CARGAR_RECETAS()
            System.out.println(RECETAS.size().toString() + " recetas cargadas")
            System.out.print("Cargando los oficios: ")
            GestorSQL.CARGAR_OFICIOS()
            System.out.println(OFICIOS.size().toString() + " oficios cargados")
            System.out.print("Cargando libros de Artesano: ")
            GestorSQL.CARGAR_LIBROS_ARTESANOS()
            System.out.println(LIBROS_ARTESANOS.size().toString() + " cargados")
            System.out.print("Cargando Acciones Omegas: ")
            GestorSQL.CARGAR_ACCIONES_OMEGA()
            System.out.println(OMEGAS.size().toString() + " cargados")
            System.out.print("Cargando los objetivos: ")
            GestorSQL.CARGAR_MISION_OBJETIVOS()
            System.out.println(OBJETIVOS_MODELOS.size().toString() + " objetivos cargados")
            System.out.print("Cargando las etapas: ")
            GestorSQL.CARGAR_ETAPAS()
            System.out.println(ETAPAS.size().toString() + " etapas cargadas")
            System.out.print("Cargando los misiones: ")
            GestorSQL.CARGAR_MISIONES()
            System.out.println(MISIONES_MODELOS.size().toString() + " misiones cargados")
            GestorSQL.CARGAR_MAPAS_ESTRELLAS()
            if (MainServidor.MODO_HEROICO || !MainServidor.MAPAS_MODO_HEROICO.isEmpty()) {
                System.out.print("Cargando los mapas heroicos: ")
                GestorSQL.CARGAR_MAPAS_HEROICO()
                System.out.println(MAPAS_HEROICOS.size().toString() + " mapas heroicos cargados")
            }
            System.out.print("Cargando los mapas: ")
            val xxxx: Long = System.currentTimeMillis()
            GestorSQL.CARGAR_MAPAS()
            System.out.println(
                    MAPAS.size().toString() + " mapas cargados ----> (en " + (System.currentTimeMillis() - xxxx) + ") milisegundos")
            System.out.print("Cargando los grupo mobs fijos: ")
            System.out.println(GestorSQL.CARGAR_MOBS_FIJOS().toString() + " grupo mobs fijos cargados")
            System.out.print("Cargando los zaaps: ")
            GestorSQL.CARGAR_ZAAPS()
            System.out.println(ZAAPS.size().toString() + " zaaps cargados")
            System.out.print("Cargando los triggers: ")
            System.out.println(GestorSQL.CARGAR_TRIGGERS().toString() + " trigger cargados")
            System.out.print("Cargando las acciones de pelea: ")
            System.out.println(GestorSQL.CARGAR_ACCION_FINAL_DE_PELEA().toString() + " acciones de pelea cargadas")
            System.out.print("Cargando los NPCs: ")
            System.out.println(GestorSQL.CARGAR_NPCS().toString() + " NPCs cargados")
            System.out.print("Cargando las acciones de objetos: ")
            System.out.println(GestorSQL.CARGAR_ACCIONES_USO_OBJETOS().toString() + " acciones de objetos cargados")
            System.out.print("Cargando las animaciones: ")
            GestorSQL.SELECT_ANIMACIONES()
            System.out.println(ANIMACIONES.size().toString() + " animaciones cargadas")
            System.out.print("Cargando los otros interactivos: ")
            GestorSQL.CARGAR_OTROS_INTERACTIVOS()
            System.out.println(OTROS_INTERACTIVOS.size().toString() + " otros interactivos cargados")
            System.out.print("Cargando las comidas de mascotas: ")
            System.out.println(GestorSQL.CARGAR_COMIDAS_MASCOTAS().toString() + " comidas de mascotas cargadas")
            System.out.print("Cargando los tutoriales: ")
            GestorSQL.CARGAR_TUTORIALES()
            System.out.println(TUTORIALES.size().toString() + " tutoriales cargados")
            System.out.print("Cargando las zonas: ")
            GestorSQL.SELECT_ZONAS()
            System.out.println(ZONAS.size().toString() + " zonas cargados")
            // if(MainServidor.PARAMS_LOGIN_PREMIO)GestorSQL.SELECT_LOGIN_REGALO();
            // if(MainServidor.PARAMS_MASTER_RESET)GestorSQL.CARGAR_MASTER_RESET();
            if (MainServidor.PARAMS_FUSION) GestorSQL.CARGAR_FUSION()
            GestorSQL.CARGAR_CRAFEO()
            GestorSQL.CARGAR_EPICITEMS()
            System.out.print("Cargando Idolos: ")
            GestorSQL.CARGAR_IDOLOS()
            System.out.println(IDOLOS.size().toString() + " idolos cargados")
            System.out.print("Cargando Logros: ")
            GestorSQL.CARGAR_LOGROS()
            setLogrosServidor()
            System.out.println(LOGROS.size().toString() + " Logros cargados")
            System.out.print("Cargando Pase Batalla: ")
            GestorSQL.CARGAR_PASEBATALLA()
            System.out.println(Prestigios.size().toString() + " Items PB cargados")
            System.out.print("Cargando Success: ")
            GestorSQL.CARGAR_SUCCESS()
            System.out.println(SUCCESS.size().toString() + " Succes cargados")
            System.out.print("Cargando las Auras: ")
            GestorSQL.CARGAR_AURAS()
            System.out.println(AURAS.size().toString() + " Auras cargadas")
            System.out.println("===========> Database Dynamic <===========")
            System.out.print("Cargando los objetos: ")
            GestorSQL.CARGAR_OBJETOS()
            System.out.println(_OBJETOS.size().toString() + " objetos cargados")
            System.out.print("Cargando los dragopavos: ")
            GestorSQL.CARGAR_MONTURAS()
            System.out.println(_MONTURAS.size().toString() + " dragopavos cargados")
            System.out.print("Cargando los puesto mercadillos: ")
            GestorSQL.SELECT_PUESTOS_MERCADILLOS()
            System.out.println(MERCADILLOS.size().toString() + " puestos mercadillos cargados")
            System.out.print("Cargando las cuentas: ")
            GestorSQL.CARGAR_DB_CUENTAS()
            GestorSQL.CARGAR_CUENTAS_SERVER_PERSONAJE()
            System.out.println(_CUENTAS.size().toString() + " cuentas cargadas")
            System.out.print("REINICIAR MERCANTES: " + calendar.get(Calendar.DAY_OF_WEEK).toString() + " + " + Calendar.THURSDAY.toString() + " ")
            if (calendar.get(Calendar.DAY_OF_WEEK) === Calendar.THURSDAY) {
                System.out.print("Reiniciadndo los mercantes...")
                GestorSQL.REINICIAR_MERCANTES()
            }
            System.out.print("Cargando los personajes: ")
            GestorSQL.CARGAR_PERSONAJES()
            System.out.println(_PERSONAJES.size().toString() + " personajes cargados")
            System.out.print("Cargando los objetos mercadillos: ")
            System.out.println(GestorSQL.SELECT_OBJETOS_MERCADILLO().toString() + " objetos mercadillos cargados")
            System.out.print("Cargando los rankings PVP: ")
            GestorSQL.SELECT_RANKING_PVP()
            System.out.println(_RANKINGS_PVP.size().toString() + " rankings PVP cargados cargados")
            System.out.print("Cargando los prismas: ")
            GestorSQL.CARGAR_PRISMAS()
            System.out.println(_PRISMAS.size().toString() + " prismas cargados")
            System.out.print("Cargando los rankings Koliseo: ")
            GestorSQL.SELECT_RANKING_KOLISEO()
            System.out.println(_RANKINGS_KOLISEO.size().toString() + " rankings Koliseo cargados cargados")
            System.out.print("Cargando los gremios: ")
            GestorSQL.CARGAR_GREMIOS()
            System.out.println(_GREMIOS.size().toString() + " gremios cargados")
            System.out.print("Cargando los miembros de gremio: ")
            System.out.println(GestorSQL.CARGAR_MIEMBROS_GREMIO().toString() + " miembros de gremio cargados")
            System.out.print("Cargando los recaudadores: ")
            GestorSQL.CARGAR_RECAUDADORES()
            System.out.println(_RECAUDADORES.size().toString() + " recaudadores cargados")
            System.out.print("Cargando los cercados: ")
            GestorSQL.CARGAR_CERCADOS()
            GestorSQL.RECARGAR_CERCADOS()
            System.out.println(CERCADOS.size().toString() + " cercados cargados")
            System.out.print("Cargando las casas: ")
            GestorSQL.CARGAR_CASAS()
            GestorSQL.RECARGAR_CASAS()
            System.out.println(CASAS.size().toString() + " casas cargadas")
            System.out.print("Cargando los cofres: ")
            GestorSQL.CARGAR_COFRES()
            GestorSQL.RECARGAR_COFRES()
            System.out.println(COFRES.size().toString() + " cofres cargados")
            SIG_ID_OBJETO = GestorSQL.GET_SIG_ID_OBJETO()
            try {
                if (!CUENTAS_A_BORRAR.isEmpty()) {
                    var eliminados = 0
                    Thread.sleep(100)
                    for (cuenta in CUENTAS_A_BORRAR) {
                        for (perso in cuenta.getPersonajes()) {
                            if (perso == null) {
                                continue
                            }
                            cuenta.eliminarPersonaje(perso.getID())
                            eliminados++
                        }
                    }
                    if (eliminados > 0) {
                        MainServidor.redactarLogServidorln(
                                "\nSe eliminaron $eliminados personajes con sus objetos, dragopavos, casas\n")
                    }
                    Thread.sleep(100)
                }
            } catch (e: Exception) {
            }
            actualizarRankings()
            prepararListaGFX()
            prepararListaNivel()
            prepararPanelItems()
            listaMascotas()
            Idiomas.cargarIdiomaES()
            // lanzamiento del server1
            setServidorEstado(Constantes.SERVIDOR_ONLINE)
            if (MainServidor.EVENTO_NPC > 0) {
                agregarNpcEvento()
            }
            if (MainServidor.PORTAL_NPC > 0) {
                agregarPortal()
            }
            Formulas.rangoKoli(MainServidor.NIVEL_MAX_PERSONAJE)
        }

        fun esZaapi(mapaID: Int, alineacion: Byte): Boolean {
            if (alineacion == Constantes.ALINEACION_BONTARIANO) {
                return ZAAPIS_BONTA.contains(mapaID)
            } else if (alineacion == Constantes.ALINEACION_BRAKMARIANO) {
                return ZAAPIS_BRAKMAR.contains(mapaID)
            }
            return ZAAPIS_BONTA.contains(mapaID) || ZAAPIS_BRAKMAR.contains(mapaID)
        }

        fun setServidorEstado(estado: Byte) {
            SERVIDOR_ESTADO = estado
            SincronizadorSocket.sendPacket("S" + SERVIDOR_ESTADO, true)
        }

        private fun listaMascotas() {
            val s = StringBuilder()
            for (o in OBJETOS_MODELOS.values()) {
                if (o.getTipo() === Constantes.OBJETO_TIPO_MASCOTA) {
                    if (s.length() > 0) {
                        s.append(",")
                    }
                    s.append(o.getID())
                }
            }
            LISTA_MASCOTAS = s.toString()
        }

        fun getServicio(id: Int): Servicio? {
            return SERVICIOS[id]
        }

        fun addServicio(servicio: Servicio) {
            SERVICIOS.put(servicio.getID(), servicio)
        }

        fun addEncarnacionModelo(encarnacion: EncarnacionModelo) {
            ENCARNACIONES_MODELOS.put(encarnacion.getGfxID(), encarnacion)
        }

        fun mensajeCaceria(): String {
            val s = StringBuilder()
            val s2 = StringBuilder()
            val param: Array<String> = KAMAS_OBJ_CACERIA.split(Pattern.quote("|"))
            if (param.size > 1) {
                var i: Byte = 0
                for (a in param[1].split(";")) {
                    try {
                        val b: Array<String> = a.split(",")
                        val stats: String = getObjetoModelo(Integer.parseInt(b[0])).stringStatsModelo()
                        if (s.length() > 0) {
                            s.append(", ")
                        }
                        s.append("°" + i + "x" + b[1])
                        if (s2.length() > 0) {
                            s2.append("!")
                        }
                        s2.append(b[0] + "!" + stats)
                        i++
                    } catch (e: Exception) {
                    }
                }
            }
            s.append(", " + param[0] + " Kamas|" + s2.toString())
            return s.toString()
        }

        fun addMision(mision: MisionModelo) {
            MISIONES_MODELOS.put(mision.getID(), mision)
        }

        fun getMision(id: Int): MisionModelo? {
            return MISIONES_MODELOS[id]
        }

        fun addAlmanax(almanax: Almanax) {
            ALMANAX.put(almanax.getID(), almanax)
        }

        fun getAlmanax(id: Int): Almanax? {
            return ALMANAX[id]
        }

        val almanaxDelDia: Almanax?
            get() = ALMANAX[DIA_DEL_AÑO]

        fun getClase(clase: Int): Clase? {
            return CLASES[clase]
        }

        fun addOrnamento(ornamento: Ornamento) {
            ORNAMENTOS.put(ornamento.getID(), ornamento)
        }

        fun getOrnamento(id: Int): Ornamento? {
            return ORNAMENTOS[id]
        }

        fun addMasterReset(ornamento: MasterReset) {
            MASTER_RESET.put(ornamento.getNivel(), ornamento)
        }

        fun getMasterReset(id: Int): MasterReset? {
            return MASTER_RESET[id]
        }

        fun getMasterResetExist(id: Int): Boolean {
            return MASTER_RESET.containsKey(id)
        }

        fun addTitulo(ornamento: Titulo) {
            TITULOS.put(ornamento.getID(), ornamento)
        }

        fun getTitulo(id: Int): Titulo? {
            return TITULOS[id]
        }

        fun getAuras(ID: Int): Aura? {
            return AURAS[ID]
        }

        fun addAuras(t: Aura) {
            AURAS.put(t.getId(), t)
        }

        fun listaAuras(perso: Personaje): String {
            val str = StringBuilder()
            for (o in AURAS.values()) {
                if (perso.getAllAura(o.getId())) {
                    if (str.length() > 0) {
                        str.append(";")
                    }
                    str.append(o.getId().toString() + "," + o.getName())
                } else {
                    if (str.length() > 0) {
                        str.append(";")
                    }
                    str.append(o.getId().toString() + "," + o.getName() + "," + 1)
                }
            }
            return str.toString()
        }

        fun listarOrnamentos(perso: Personaje): String {
            val str = StringBuilder()
            for (o in ORNAMENTOS.values()) {
                if (perso.tieneOrnamento(o.getID())) {
                    if (str.length() > 0) {
                        str.append(";")
                    }
                    if (o.getID() === 141) {
                        if (_liderRankingPvp !== perso.getNombre()) {
                            str.append(o.getID().toString() + "," + o.get_kamas())
                        }
                    } else if (o.getID() === 140) {
                        if (_liderRankingPvp1 !== perso.getNombre()) {
                            str.append(o.getID().toString() + "," + o.get_kamas())
                        }
                    } else if (o.getID() === 139) {
                        if (_liderRankingPvp2 !== perso.getNombre()) {
                            str.append(o.getID().toString() + "," + o.get_kamas())
                        }
                    } else {
                        str.append(o.getID().toString() + "," + o.get_kamas() + "," + "T")
                    }
                } else if (!o.esParaVender()) {
                    if (str.length() > 0) {
                        str.append(";")
                    }
                    str.append(o.getID().toString() + "," + o.get_kamas())
                } else {
                    if (str.length() > 0) {
                        str.append(";")
                    }
                    str.append(o.getID().toString() + "," + o.get_kamas() + "," + o.getPrecioStr())
                }
            }
            return str.toString()
        }

        fun listarTitulos(perso: Personaje): String {
            val str = StringBuilder()
            for (o in TITULOS.values()) {
                if (perso.tieneTitulo(o.getID())) {
                    if (str.length() > 0) {
                        str.append(";")
                    }
                    if (o.getID() === 55) {
                        if (_liderRankingPvp !== perso.getNombre()) {
                            str.append(o.getID())
                        }
                    } else if (o.getID() === 56) {
                        if (_liderRankingPvp1 !== perso.getNombre()) {
                            str.append(o.getID())
                        }
                    } else if (o.getID() === 57) {
                        if (_liderRankingPvp2 !== perso.getNombre()) {
                            str.append(o.getID())
                        }
                    } else {
                        str.append(o.getID().toString() + "," + "T")
                    }
                } else if (!o.esParaVender()) {
                    if (str.length() > 0) {
                        str.append(";")
                    }
                    str.append(o.getID())
                } else {
                    if (str.length() > 0) {
                        str.append(";")
                    }
                    str.append(o.getID().toString() + "," + o.getPrecioStr())
                }
            }
            return str.toString()
        }

        fun addComando(comando: String, rango: Int) {
            COMANDOS.put(comando.toUpperCase(), rango)
        }

        fun getRangoComando(comando: String): Int {
            return if (COMANDOS[comando] == null) {
                -1
            } else COMANDOS[comando]
        }

        fun addOmega(mapa: Int, libro: Omega?) {
            OMEGAS.put(mapa, libro)
        }

        fun getOmega(mapa: Int): Omega? {
            return OMEGAS[mapa]
        }

        fun addLibroArtesano(mapa: Int, libro: LibroArtesano?) {
            LIBROS_ARTESANOS.put(mapa, libro)
        }

        fun addIdolos(id: Int, libro: Idolo) {
            IDOLOS.put(id, libro)
            if (!IDOLOS_HECHIZOS.contains(libro.getHechizo())) {
                IDOLOS_HECHIZOS.add(libro.getHechizo())
            }
        }

        fun getIdolo(id: Int): Idolo? {
            return IDOLOS[id]
        }

        fun addLogro(id: Int, libro: Logro?) {
            LOGROS.put(id, libro)
        }

        fun getLogro(id: Int): Logro? {
            return LOGROS[id]
        }

        fun setLogrosServidor() {
            val logros: ArrayList<Logro> = ArrayList<Logro>()
            for (logro in LOGROS.values()) {
                logros.add(logro)
            }
            LOGROS.clear()
            Collections.sort(logros, CompMasaMenos())
            val size = if (logros.size() > 12) 12 else logros.size()
            for (i in 0 until size) {
                val logro: Logro = logros.get(i)
                LOGROS.put(logro.getId(), logro)
            }
        }

        fun getLibroArtesano(mapa: Int): String {
            return if (LIBROS_ARTESANOS[mapa] == null) {
                Constantes.SKILLS_LIBRO_ARTESANOS
            } else LIBROS_ARTESANOS[mapa].getOficios()
        }

        fun addEtapa(id: Int, recompensas: String?, steps: String?, nombre: String?,
                     varios: String?) {
            ETAPAS.put(id, MisionEtapaModelo(id, recompensas, steps, nombre, varios))
        }

        fun getEtapa(id: Int): MisionEtapaModelo? {
            return ETAPAS[id]
        }

        fun addDropFijo(drop: DropMob?) {
            DROPS_FIJOS.add(drop)
        }

        fun listaDropsFijos(): ArrayList<DropMob> {
            return DROPS_FIJOS
        }

        fun addMisionObjetivoModelo(id: Int, tipo: Byte, args: String?, esalHablar: Boolean,
                                    condicion: String?, esOculto: Boolean) {
            OBJETIVOS_MODELOS.put(id, MisionObjetivoModelo(id, tipo, args, esalHablar, condicion, esOculto))
        }

        fun getMisionObjetivoModelo(id: Int): MisionObjetivoModelo? {
            return OBJETIVOS_MODELOS[id]
        }

        fun prepararListaGFX() {
            val str = StringBuilder()
            for (obj in OBJETOS_MODELOS.values()) {
                if (obj.getGFX() <= 0) {
                    continue
                }
                if (str.length() > 0) {
                    str.append(";")
                }
                str.append(obj.getID().toString() + "," + obj.getGFX())
            }
            LISTA_GFX = str.toString()
        }

        fun prepararListaNivel() {
            val str = StringBuilder()
            for (obj in OBJETOS_MODELOS.values()) {
                if (!obj.getNivelModifi()) {
                    continue
                }
                if (str.length() > 0) {
                    str.append(";")
                }
                str.append(obj.getID().toString() + "," + obj.getNivel())
            }
            LISTA_NIVEL = str.toString()
        }

        fun addMobEvento(evento: Byte, mobOriginal: Int, mobEvento: Int) {
            if (MOBS_EVENTOS[evento] == null) {
                MOBS_EVENTOS.put(evento, ArrayList<Duo<Integer, Integer>>())
            }
            MOBS_EVENTOS[evento].add(Duo(mobOriginal, mobEvento))
        }

        val mobsEventoDelDia: ArrayList<Duo<Integer, Integer>>?
            get() = MOBS_EVENTOS[MOB_EVENTO]

        fun refrescarTodosMobs() {
            for (mapa in MAPAS.values()) {
                mapa.refrescarGrupoMobs()
            }
        }

        fun resetearStatsObjetos(idsModelo: ArrayList<Integer?>) {
            val objetos: ArrayList<Objeto> = ArrayList()
            for (obj in _OBJETOS.values()) {
                if (idsModelo.contains(obj.getObjModeloID())) {
                    objetos.add(obj)
                    obj.convertirStringAStats(obj.getObjModelo().generarStatsModelo(CAPACIDAD_STATS.MAXIMO))
                }
            }
            GestorSQL.SALVAR_OBJETOS(objetos)
        }

        fun moverMobs() {
            val mapas: ArrayList<Integer> = ArrayList()
            for (perso in personajesEnLinea) {
                val id: Int = perso.getMapa().getID()
                if (!mapas.contains(id)) {
                    Thread { perso.getMapa().moverGrupoMobs(MainServidor.CANTIDAD_GRUPO_MOBS_MOVER_POR_MAPA) }.start()
                    mapas.add(id)
                }
            }
        }

        private fun rankingNivel() {
            if (!MainServidor.PARAM_LADDER_NIVEL) return
            val persos: ArrayList<Personaje> = ArrayList()
            persos.addAll(_PERSONAJES.values())
            Collections.sort(persos, CompNivelMasMenos())
            _LADDER_NIVEL.clear()
            _LADDER_NIVEL.addAll(persos)
        }

        private fun rankingDia() {
            if (!MainServidor.PARAM_LADDER_EXP_DIA) return
            val persos: ArrayList<Personaje> = ArrayList()
            persos.addAll(_PERSONAJES.values())
            Collections.sort(persos, CompDiaMasMenos())
            _LADDER_EXP_DIA.clear()
            _LADDER_EXP_DIA.addAll(persos)
        }

        private fun rankingGremio() {
            if (!MainServidor.PARAM_LADDER_GREMIO) return
            val persos: ArrayList<Gremio> = ArrayList()
            persos.addAll(_GREMIOS.values())
            Collections.sort(persos, CompGremioMasMenos())
            _LADDER_GREMIO.clear()
            _LADDER_GREMIO.addAll(persos)
        }

        private fun rankingPVP() {
            if (!MainServidor.PARAM_LADDER_PVP) {
                return
            }
            val persos: ArrayList<RankingPVP> = ArrayList()
            persos.addAll(_RANKINGS_PVP.values())
            Collections.sort(persos, CompPVPMasMenos())
            _LADDER_PVP.clear()
            _LADDER_PVP.addAll(persos)
        }

        private fun rankingKoliseo() {
            if (!MainServidor.PARAM_LADDER_KOLISEO) {
                return
            }
            val persos: ArrayList<RankingKoliseo> = ArrayList()
            persos.addAll(_RANKINGS_KOLISEO.values())
            Collections.sort(persos, CompKoliseoMasMenos())
            _LADDER_KOLISEO.clear()
            _LADDER_KOLISEO.addAll(persos)
        }

        private fun addPaginas(temp: StringBuilder, inicio: Int, add: Int) {
            temp.append("|" + (if (inicio == -1) 0 else 1) + "|" + if (add == MainServidor.LIMITE_LADDER + 1) 1 else 0)
        }

        private fun addStringParaLadder(temp: StringBuilder, perso: Personaje, pos: Int) {
            if (temp.length() > 0) {
                temp.append("#")
            }
            temp.append(getStringParaLadder(perso, pos))
        }

        private fun addStringParaLadder2(temp: StringBuilder, perso: Personaje, pos: Int, pvp: Boolean) {
            if (temp.length() > 0) {
                temp.append("#")
            }
            temp.append(getStringParaLadder2(perso, pos, pvp))
        }

        private fun getStringParaLadder2(perso: Personaje, pos: Int, pvp: Boolean): String {
            val victorias: Int = if (pvp) _RANKINGS_PVP.get(perso.getID()).getVictorias() else _RANKINGS_KOLISEO.get(perso.getID()).getVictorias()
            val derrotas: Int = if (pvp) _RANKINGS_PVP.get(perso.getID()).getDerrotas() else _RANKINGS_KOLISEO.get(perso.getID()).getDerrotas()
            return (pos.toString() + ";" + perso.getGfxID(false) + ";" + perso.getNombre() + ";" + perso.getTitulo(false) + ";"
                    + perso.getNivel() + ";" + victorias + "	" + derrotas + ";"
                    + (if (perso.enLinea()) if (perso.getPelea() != null) 2 else 1 else 0) + ";" + perso.getAlineacion())
        }

        private fun getStringParaLadder(perso: Personaje, pos: Int): String {
            val hola: Int
            hola = if (perso.getNivelOmega() > 0) {
                perso.getNivelOmega()
            } else {
                perso.getNivel()
            }
            return (pos.toString() + ";" + perso.getGfxID(false) + ";" + perso.getNombre() + ";" + perso.getTitulo(false) + ";" + hola
                    + ";" + perso.getExperiencia() + ";" + (if (perso.enLinea()) if (perso.getPelea() != null) 2 else 1 else 0) + ";"
                    + perso.getAlineacion() + ";" + perso.getNivelOmega())
        }

        private fun strStaffOnline(out: Personaje, buscar: String, iniciarEn: Int) {
            if (!MainServidor.PARAM_LADDER_STAFF) {
                return
            }
            var pos = 0
            var add = 0
            var inicio = 0
            val temp = StringBuilder()
            for (perso in ONLINES) {
                try {
                    if (add > MainServidor.LIMITE_LADDER) {
                        break
                    }
                    if (perso.esIndetectable()) {
                        continue
                    }
                    if (perso.getAdmin() <= 0) {
                        continue
                    }
                    pos++
                    if (!buscar.isEmpty()) {
                        if (!perso.getNombre().toUpperCase().contains(buscar)) {
                            continue
                        }
                    }
                    if (inicio == 0) {
                        inicio = pos
                    }
                    if (pos < iniciarEn) {
                        continue
                    }
                    if (pos == inicio) {
                        inicio = -1
                    }
                    if (add < MainServidor.LIMITE_LADDER) {
                        addStringParaLadder(temp, perso, pos)
                    }
                    add++
                } catch (e: Exception) {
                }
            }
            addPaginas(temp, inicio, add)
            GestorSalida.ENVIAR_bl_RANKING_DATA(out, "STAFF", temp.toString())
        }

        private fun strRankingNivel(out: Personaje, buscar: String, iniciarEn: Int) {
            if (!MainServidor.PARAM_LADDER_NIVEL) {
                return
            }
            var pos = 0
            var add = 0
            var inicio = 0
            val temp = StringBuilder()
            for (perso in _LADDER_NIVEL) {
                try {
                    if (add > MainServidor.LIMITE_LADDER) {
                        break
                    }
                    if (perso.esIndetectable()) {
                        continue
                    }
                    if (!MainServidor.PARAM_PERMITIR_ADMIN_EN_LADDER) {
                        if (perso.getAdmin() > 0) {
                            continue
                        }
                    }
                    pos++
                    if (!buscar.isEmpty()) {
                        if (!perso.getNombre().toUpperCase().contains(buscar)) {
                            continue
                        }
                    }
                    if (inicio == 0) {
                        inicio = pos
                    }
                    if (pos < iniciarEn) {
                        continue
                    }
                    if (pos == inicio) {
                        inicio = -1
                    }
                    if (add < MainServidor.LIMITE_LADDER) {
                        addStringParaLadder(temp, perso, pos)
                    }
                    add++
                } catch (e: Exception) {
                }
            }
            addPaginas(temp, inicio, add)
            GestorSalida.ENVIAR_bl_RANKING_DATA(out, "NIVEL", temp.toString())
        }

        private fun strRankingDia(out: Personaje, buscar: String, iniciarEn: Int) {
            if (!MainServidor.PARAM_LADDER_EXP_DIA) {
                return
            }
            var pos = 0
            var add = 0
            var inicio = 0
            val temp = StringBuilder()
            try {
                for (perso in _LADDER_EXP_DIA) {
                    try {
                        if (add > MainServidor.LIMITE_LADDER) {
                            break
                        }
                        if (perso.esIndetectable()) {
                            continue
                        }
                        if (!MainServidor.PARAM_PERMITIR_ADMIN_EN_LADDER) {
                            if (perso.getAdmin() > 0) {
                                continue
                            }
                        }
                        pos++
                        if (!buscar.isEmpty()) {
                            if (!perso.getNombre().toUpperCase().contains(buscar)) {
                                continue
                            }
                        }
                        if (inicio == 0) {
                            inicio = pos
                        }
                        if (pos < iniciarEn) {
                            continue
                        }
                        if (pos == inicio) {
                            inicio = -1
                        }
                        if (add < MainServidor.LIMITE_LADDER) {
                            addStringParaLadder(temp, perso, pos)
                        }
                        add++
                    } catch (e: Exception) {
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            addPaginas(temp, inicio, add)
            GestorSalida.ENVIAR_bl_RANKING_DATA(out, "DIA", temp.toString())
        }

        private fun strRankingPVP(out: Personaje, buscar: String, iniciarEn: Int) {
            if (!MainServidor.PARAM_LADDER_PVP) {
                return
            }
            var pos = 0
            var add = 0
            var inicio = 0
            val temp = StringBuilder()
            try {
                for (rank in _LADDER_PVP) {
                    try {
                        if (add > MainServidor.LIMITE_LADDER) {
                            break
                        }
                        val perso: Personaje = getPersonaje(rank.getID()) ?: continue
                        if (perso.esIndetectable()) {
                            continue
                        }
                        if (!MainServidor.PARAM_PERMITIR_ADMIN_EN_LADDER) {
                            if (perso.getAdmin() > 0) {
                                continue
                            }
                        }
                        pos++
                        if (!buscar.isEmpty()) {
                            if (!perso.getNombre().toUpperCase().contains(buscar)) {
                                continue
                            }
                        }
                        if (inicio == 0) {
                            inicio = pos
                        }
                        if (pos < iniciarEn) {
                            continue
                        }
                        if (pos == inicio) {
                            inicio = -1
                        }
                        if (add < MainServidor.LIMITE_LADDER) {
                            addStringParaLadder2(temp, perso, pos, true)
                        }
                        add++
                    } catch (e: Exception) {
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            addPaginas(temp, inicio, add)
            GestorSalida.ENVIAR_bl_RANKING_DATA(out, "PVP", temp.toString())
        }

        private fun strRankingKoliseo(out: Personaje, buscar: String, iniciarEn: Int) {
            if (!MainServidor.PARAM_LADDER_KOLISEO) {
                return
            }
            var pos = 0
            var add = 0
            var inicio = 0
            val temp = StringBuilder()
            try {
                for (rank in _LADDER_KOLISEO) {
                    try {
                        if (add > MainServidor.LIMITE_LADDER) {
                            break
                        }
                        val perso: Personaje = getPersonaje(rank.getID()) ?: continue
                        if (perso.esIndetectable()) {
                            continue
                        }
                        if (!MainServidor.PARAM_PERMITIR_ADMIN_EN_LADDER) {
                            if (perso.getAdmin() > 0) {
                                continue
                            }
                        }
                        pos++
                        if (!buscar.isEmpty()) {
                            if (!perso.getNombre().toUpperCase().contains(buscar)) {
                                continue
                            }
                        }
                        if (inicio == 0) {
                            inicio = pos
                        }
                        if (pos < iniciarEn) {
                            continue
                        }
                        if (pos == inicio) {
                            inicio = -1
                        }
                        if (add < MainServidor.LIMITE_LADDER) {
                            addStringParaLadder2(temp, perso, pos, false)
                        }
                        add++
                    } catch (e: Exception) {
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            addPaginas(temp, inicio, add)
            GestorSalida.ENVIAR_bl_RANKING_DATA(out, "KOLISEO", temp.toString())
        }

        private fun strRankingGremio(out: Personaje, buscar: String, iniciarEn: Int) {
            if (!MainServidor.PARAM_LADDER_GREMIO) {
                return
            }
            var pos = 0
            var add = 0
            var inicio = 0
            val temp = StringBuilder()
            try {
                for (gremio in _LADDER_GREMIO) {
                    try {
                        if (add > MainServidor.LIMITE_LADDER) {
                            break
                        }
                        pos++
                        if (!buscar.isEmpty()) {
                            if (!gremio.getNombre().toUpperCase().contains(buscar)) {
                                continue
                            }
                        }
                        if (inicio == 0) {
                            inicio = pos
                        }
                        if (pos < iniciarEn) {
                            continue
                        }
                        if (pos == inicio) {
                            inicio = -1
                        }
                        if (add < MainServidor.LIMITE_LADDER) {
                            if (temp.length() > 0) {
                                temp.append("#")
                            }
                            temp.append(pos.toString() + ";" + gremio.getEmblema() + ";" + gremio.getNombre() + ";"
                                    + gremio.getCantidadMiembros() + ";" + gremio.getNivel() + ";" + gremio.getExperiencia()
                                    + ";;;")
                        }
                        add++
                    } catch (e: Exception) {
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            addPaginas(temp, inicio, add)
            GestorSalida.ENVIAR_bl_RANKING_DATA(out, "GREMIO", temp.toString())
        }

        fun nombreLiderRankingKoliseo(): String {
            var nombre = ""
            if (_RANKINGS_KOLISEO.size() <= 0) {
                return nombre
            }
            var vict = 0
            var derr = 0
            for (rank in _RANKINGS_KOLISEO.values()) {
                if (rank.getVictorias() > vict) {
                    nombre = rank.getNombre()
                    vict = rank.getVictorias()
                    derr = rank.getDerrotas()
                } else {
                    if (rank.getVictorias() !== vict || rank.getDerrotas() > derr) {
                        continue
                    }
                    nombre = rank.getNombre()
                    vict = rank.getVictorias()
                    derr = rank.getDerrotas()
                }
            }
            return nombre
        }

        fun actualizarLiderKoliseo() {
            val perso: Personaje? = getPersonajePorNombre(nombreLiderRankingKoliseo())
            if (perso != null) {
                val npc: NPCModelo? = getNPCModelo(MainServidor.NPC_RANKING_KOLISEO)
                if (npc != null) {
                    npc.modificarNPC(perso.getSexo(), 150.toShort(), 150.toShort(), perso.getGfxID(false), perso.getColor1(),
                            perso.getColor2(), perso.getColor3(), perso.getGremio())
                }
                npc.setAccesorios(perso.getStringAccesoriosNPC())
            }
            _liderRankingKoliseo = nombreLiderRankingKoliseo()
        }

        fun nombreLiderRankingPVP(cual: String): String? {
            val nameslist: Map<Integer, String> = TreeMap<Integer, String>()
            if (_RANKINGS_PVP.size() <= 0) return "Ninguno" else {
                for (rank in _RANKINGS_PVP.values()) {
                    nameslist.put(rank.getVictorias(), rank.getNombre())
                }
                val size: Int = nameslist.size()
                if (size <= 0) return "Ninguno"
                val keys: Array<Object> = nameslist.keySet().toArray()
                if (cual === "1") {
                    if (size < 1) return "Ninguno"
                    val prim = keys[keys.size - 1] as Int
                    return nameslist[prim]
                } else if (cual === "2") {
                    if (size < 2) return "Ninguno"
                    val segund = keys[keys.size - 2] as Int
                    return nameslist[segund]
                } else if (cual === "3") {
                    if (size < 3) return "Ninguno"
                    val tercer = keys[keys.size - 3] as Int
                    return nameslist[tercer]
                }
            }
            return ""
        }

        /*
     * public static String nombreLiderRankingPVP() { String nombre = ""; if
     * (_RANKINGS_PVP.size() <= 0) { return nombre; } int vict = 0, derr = 0; for
     * (final RankingPVP rank : _RANKINGS_PVP.values()) { if (rank.getVictorias() >
     * vict) { nombre = rank.getNombre(); vict = rank.getVictorias(); derr =
     * rank.getDerrotas(); } else { if (rank.getVictorias() != vict ||
     * rank.getDerrotas() > derr) { continue; } nombre = rank.getNombre(); vict =
     * rank.getVictorias(); derr = rank.getDerrotas(); } } return nombre; }
     */
        fun actualizarLiderPVP() {
            val perso: Personaje? = getPersonajePorNombre(nombreLiderRankingPVP("1"))
            if (perso != null) {
                val npc: NPCModelo? = getNPCModelo(MainServidor.NPC_RANKING_PVP)
                if (npc != null) {
                    npc.modificarNPC(perso.getSexo(), 150.toShort(), 150.toShort(), perso.getGfxID(false), perso.getColor1(),
                            perso.getColor2(), perso.getColor3(), perso.getGremio())
                    npc.setAccesorios(perso.getStringAccesoriosNPC())
                }
                if (!perso.tieneOrnamento(141)) {
                    perso.addOrnamento(141)
                    perso.setOrnamento(141)
                    perso.setTitulo(55)
                }
            }
            val perso1: Personaje? = getPersonajePorNombre(nombreLiderRankingPVP("2"))
            if (perso1 != null) {
                if (!perso1.tieneOrnamento(140)) {
                    perso1.addOrnamento(140)
                    perso1.setOrnamento(140)
                    perso1.setTitulo(56)
                }
            }
            val perso2: Personaje? = getPersonajePorNombre(nombreLiderRankingPVP("3"))
            if (perso2 != null) {
                if (!perso2.tieneOrnamento(139)) {
                    perso2.addOrnamento(139)
                    perso2.setOrnamento(139)
                    perso2.setTitulo(57)
                }
            }
            _liderRankingPvp = nombreLiderRankingPVP("1")
            _liderRankingPvp1 = nombreLiderRankingPVP("2")
            _liderRankingPvp2 = nombreLiderRankingPVP("3")
        }

        fun rankingsPermitidos(): String {
            val temp = StringBuilder()
            if (MainServidor.PARAM_LADDER_NIVEL) {
                if (temp.length() > 0) {
                    temp.append("|")
                }
                temp.append("Nivel")
            }
            if (MainServidor.PARAM_LADDER_PVP) {
                if (temp.length() > 0) {
                    temp.append("|")
                }
                temp.append("PVP")
            }
            if (MainServidor.PARAM_LADDER_GREMIO) {
                if (temp.length() > 0) {
                    temp.append("|")
                }
                temp.append("Gremio")
            }
            if (MainServidor.PARAM_LADDER_KOLISEO) {
                if (temp.length() > 0) {
                    temp.append("|")
                }
                temp.append("Koliseo")
            }
            if (MainServidor.PARAM_LADDER_EXP_DIA) {
                if (temp.length() > 0) {
                    temp.append("|")
                }
                temp.append("DiaXP")
            }
            if (MainServidor.PARAM_LADDER_STAFF) {
                if (temp.length() > 0) {
                    temp.append("|")
                }
                temp.append("Staff")
            }
            return temp.toString()
        }

        fun enviarRanking(perso: Personaje, param: String?, buscar: String, iniciarEn: Int) {
            when (param) {
                "NIVEL" -> strRankingNivel(perso, buscar, iniciarEn)
                "PVP" -> strRankingPVP(perso, buscar, iniciarEn)
                "DIA" -> strRankingDia(perso, buscar, iniciarEn)
                "STAFF" -> strStaffOnline(perso, buscar, iniciarEn)
                "KOLISEO" -> strRankingKoliseo(perso, buscar, iniciarEn)
                "GREMIO" -> strRankingGremio(perso, buscar, iniciarEn)
                else -> {
                    GestorSalida.ENVIAR_BN_NADA(perso)
                    return
                }
            }
        }

        fun actualizarRankings() {
            rankingNivel()
            rankingPVP()
            rankingGremio()
            rankingKoliseo()
            rankingDia()
        }

        fun misBoletos(persoID: Int): String {
            if (!VENDER_BOLETOS) {
                return ""
            }
            val str = StringBuilder()
            for (a in 1..LOTERIA_BOLETOS.size) {
                if (LOTERIA_BOLETOS[a - 1] != persoID) {
                    continue
                }
                if (str.length() > 0) {
                    str.append(", ")
                }
                str.append(a)
            }
            return str.toString()
        }

        fun devolverBoletos() {
            for (a in LOTERIA_BOLETOS.indices) {
                try {
                    if (LOTERIA_BOLETOS[a] == 0) {
                        continue
                    }
                    val perso: Personaje = getPersonaje(LOTERIA_BOLETOS[a])
                    if (MainServidor.PARAM_LOTERIA_OGRINAS) {
                        val idCuenta: Int = perso.getCuentaID()
                        GestorSQL.ADD_OGRINAS_CUENTA(MainServidor.PRECIO_LOTERIA, idCuenta, perso)
                    } else {
                        perso.addKamas(MainServidor.PRECIO_LOTERIA, false, true)
                    }
                } catch (e: Exception) {
                }
            }
        }

        @Synchronized
        fun comprarLoteria(packet: String, perso: Personaje) {
            if (!VENDER_BOLETOS) {
                GestorSalida.ENVIAR_Im_INFORMACION(perso, "1DONT_TIME_BUY_LOTERIE")
                return
            }
            var boleto = 1
            try {
                boleto = Integer.parseInt(packet.substring(3))
            } catch (e: Exception) {
            }
            if (boleto < 1) {
                boleto = 1
            } else if (boleto > LOTERIA_BOLETOS.size) {
                boleto = LOTERIA_BOLETOS.size
            }
            if (boleto > 9999) {
                GestorSalida.ENVIAR_Im_INFORMACION(perso, "1NUMBER_LOTERIE_INCORRECT")
                return
            }
            if (LOTERIA_BOLETOS[boleto - 1] != 0) {
                GestorSalida.ENVIAR_Im_INFORMACION(perso, "1NUMBER_LOTERIE_OCCUPED")
                return
            }
            if (MainServidor.PARAM_LOTERIA_OGRINAS) {
                if (GestorSQL.RESTAR_OGRINAS1(perso.getCuenta(), MainServidor.PRECIO_LOTERIA, perso)) {
                    LOTERIA_BOLETOS[boleto - 1] = perso.getID()
                }
            } else {
                if (perso.getKamas() >= MainServidor.PRECIO_LOTERIA) {
                    perso.addKamas(-MainServidor.PRECIO_LOTERIA, true, true)
                    LOTERIA_BOLETOS[boleto - 1] = perso.getID()
                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "1TU_BOLETO;$boleto")
                } else {
                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "182")
                }
            }
        }

        fun iniciarLoteria() {
            if (!MainServidor.PARAM_LOTERIA) {
                return
            }
            if (MSJ_CUENTA_REGRESIVA.equalsIgnoreCase("RESET RATES") || MSJ_CUENTA_REGRESIVA.equalsIgnoreCase("LOTERIA")) {
                return
            }
            MSJ_CUENTA_REGRESIVA = "LOTERIA"
            SEG_CUENTA_REGRESIVA = 1800
            VENDER_BOLETOS = true
            // GestorSalida.ENVIAR_bRI_INICIAR_CUENTA_REGRESIVA_TODOS();
        }

        fun sortearBoletos() {
            if (!VENDER_BOLETOS) {
                return
            }
            VENDER_BOLETOS = false
            val lista: ArrayList<Integer> = ArrayList<Integer>()
            for (x in 1..LOTERIA_BOLETOS.size) {
                if (LOTERIA_BOLETOS[x - 1] != 0) {
                    lista.add(x)
                }
            }
            if (lista.size() < 10) {
                SEG_CUENTA_REGRESIVA = 600
                MSJ_CUENTA_REGRESIVA = "LOTERIA"
                VENDER_BOLETOS = true
                GestorSalida.ENVIAR_ÑL_BOTON_LOTERIA_TODOS(true)
                // GestorSalida.ENVIAR_bRI_INICIAR_CUENTA_REGRESIVA_TODOS();
                GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS("1PLUS_TIME_SORTEO")
                return
            }
            GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS("1SORTEO_LOTERIE")
            try {
                Thread.sleep(10000)
            } catch (e: Exception) {
            }
            var premios = 1
            premios += lista.size() / MainServidor.GANADORES_POR_BOLETOS
            val ganadores: Map<Integer, Integer> = TreeMap<Integer, Integer>()
            for (a in 0 until premios) {
                val boleto: Int = lista.get(Formulas.getRandomInt(0, lista.size() - 1))
                ganadores.put(boleto, LOTERIA_BOLETOS[boleto - 1])
                lista.remove(lista.indexOf(boleto))
            }
            var b = 1
            for (entry in ganadores.entrySet()) {
                val perso: Personaje = getPersonaje(entry.getValue()) ?: continue
                val idCuenta: Int = perso.getCuentaID()
                GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS(
                        "1NUMBER_WIN_LOTERIE;" + b + "~(" + entry.getKey() + ") - " + perso.getNombre())
                if (MainServidor.PARAM_LOTERIA_OGRINAS) {
                    GestorSQL.ADD_OGRINAS_CUENTA(MainServidor.PREMIO_LOTERIA, idCuenta, perso)
                } else {
                    perso.addKamas(MainServidor.PREMIO_LOTERIA, true, true)
                }
                try {
                    Thread.sleep(2000)
                } catch (e: InterruptedException) {
                }
                b++
            }
            GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS("1FINISH_LOTERIE")
            LOTERIA_BOLETOS = IntArray(10000)
        }

        fun resetExpDia() {
            for (perso in _PERSONAJES.values()) {
                perso.resetExpDia()
            }
        }

        fun moverMonturas() {
            for (cercado in CERCADOS.values()) {
                cercado.startMoverMontura()
            }
        }

        fun moverRecaudadores() {
            for (recauador in _RECAUDADORES.values()) {
                recauador.puedeMoverRecaudador()
            }
        }

        fun disminuirFatigaMonturas() {
            for (montura in _MONTURAS.values()) {
                if (montura.getUbicacion() !== Ubicacion.ESTABLO) {
                    continue
                }
                montura.disminuirFatiga()
            }
        }

        fun checkearObjInteractivos() {
            for (oi in OBJETOS_INTERACTIVOS) {
                oi.recargando(false)
                oi.subirEstrella()
            }
        }

        /*
     * public static void expulsarInactivos() { for (final ServidorSocket ss :
     * ServidorServer.getClientes()) { try { if (ss.getTiempoUltPacket() +
     * (MainServidor.SEGUNDOS_INACTIVIDAD * 1000) < System.currentTimeMillis()) {
     * ss.registrar("<===> EXPULSAR POR INACTIVIDAD!!!");
     * GestorSalida.ENVIAR_M0_MENSAJE_BASICOS_SVR_MUESTRA_DISCONNECT(ss, "1", "",
     * ""); ss.cerrarSocket(true, "expulsarInactivos()"); } } catch (final Exception
     * e) {} } }
     */
        fun lanzarPublicidad(str: String?) {
            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(str)
        }

        fun finalizarPeleas() {
            for (mapa in MAPAS.values()) {
                try {
                    for (pelea in mapa.getPeleas().values()) {
                        if (pelea == null) {
                            return
                        }
                        when (pelea.getTipoPelea()) {
                            Constantes.PELEA_TIPO_DESAFIO, Constantes.PELEA_TIPO_PVM, Constantes.PELEA_TIPO_PVM_NO_ESPADA -> pelea.acaboPelea(2.toByte())
                            else -> pelea.cancelarPelea()
                        }
                    }
                } catch (e: Exception) {
                    MainServidor.redactarLogServidorln("EXCEPTION finalizarPeleas " + e.toString())
                    e.printStackTrace()
                }
            }
        }

        var SALVANDO = false
        fun salvarServidor(inclusoOffline: Boolean) {
            SALVANDO = true
            MainServidor.redactarLogServidorln("Se invoco el metodo salvar Servidor (MUNDO DOFUS) ")
            if (SERVIDOR_ESTADO != Constantes.SERVIDOR_OFFLINE) {
                setServidorEstado(Constantes.SERVIDOR_SALVANDO)
            }
            MainServidor.redactarLogServidor("Iniciando salvado de registros JUGADORES Y SQL ... ")
            MainServidor.imprimirLogPlayers()
            MainServidor.redactarLogServidorln("100%")
            TOTAL_SALVADO = 0
            try {
                MainServidor.redactarLogServidor("Salvando Kamas de la Ruleta de Jalato")
                MainServidor.modificarParam("KAMAS_RULETA_JALATO", MainServidor.KAMAS_RULETA_JALATO.toString() + "")
                // PERSONAJES
                CANT_SALVANDO = 0
                MainServidor.redactarLogServidorln("Salvando los personajes: ")
                for (perso in _PERSONAJES.values()) {
                    try {
                        if (perso == null || perso.getCuenta() == null) {
                            continue
                        }
                        val llave: Objeto = perso.tienellave()
                        if (llave != null) {
                            llave.cambiarStatllave(perso)
                        }
                        if (MainServidor.PARAM_ALIMENTAR_MASCOTAS) perso.comprobarMascotas(true)
                        if (perso.enLinea() || inclusoOffline) {
                            MainServidor.redactarLogServidor(" -> Salvando a " + perso.getNombre().toString() + " ... ") // Ecatome
                            if (SERVIDOR_ESTADO == Constantes.SERVIDOR_OFFLINE) {
                                perso.previosDesconectar()
                            }
                            GestorSQL.SALVAR_PERSONAJE(perso, true)
                            if (perso.enLinea()) {
                                MainServidor.redactarLogServidorln(" [ONLINE] " + " 100%")
                            } else {
                                MainServidor.redactarLogServidorln(" [OFFLINE] " + " 100%")
                            }
                            CANT_SALVANDO++
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                MainServidor.redactarLogServidorln("Finalizó con " + CANT_SALVANDO)
                TOTAL_SALVADO += CANT_SALVANDO
                CANT_SALVANDO = 0
                MainServidor.redactarLogServidorln("Salvando los mercantes: ")
                for (perso in _PERSONAJES.values()) {
                    try {
                        if (perso == null || perso.getCuenta() == null || inclusoOffline) {
                            continue
                        }
                        if (perso.esMercante()) {
                            MainServidor.redactarLogServidor(" -> Salvando a " + perso.getNombre().toString() + " ... ") // Ecatome
                            GestorSQL.SALVAR_PERSONAJE(perso, true)
                            MainServidor.redactarLogServidorln(" [MERCANTE] " + " 100%")
                            CANT_SALVANDO++
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                MainServidor.redactarLogServidorln("Finalizó con " + CANT_SALVANDO)
                TOTAL_SALVADO += CANT_SALVANDO
                CANT_SALVANDO = 0
                MainServidor.redactarLogServidor("Salvando los prismas: ")
                for (prisma in _PRISMAS.values()) {
                    try {
                        GestorSQL.REPLACE_PRISMA(prisma)
                        CANT_SALVANDO++
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                MainServidor.redactarLogServidorln("Finalizó con " + CANT_SALVANDO)
                TOTAL_SALVADO += CANT_SALVANDO
                CANT_SALVANDO = 0
                MainServidor.redactarLogServidor("Salvando los gremios: ")
                for (gremio in _GREMIOS.values()) {
                    try {
                        GestorSQL.REPLACE_GREMIO(gremio)
                        CANT_SALVANDO++
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                MainServidor.redactarLogServidorln("Finalizó con " + CANT_SALVANDO)
                TOTAL_SALVADO += CANT_SALVANDO
                CANT_SALVANDO = 0
                MainServidor.redactarLogServidor("Salvando los recaudadores: ")
                for (recau in _RECAUDADORES.values()) {
                    try {
                        if (recau.getGremio() == null) {
                            continue
                        }
                        GestorSQL.REPLACE_RECAUDADOR(recau, true)
                        CANT_SALVANDO++
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                MainServidor.redactarLogServidorln("Finalizó con " + CANT_SALVANDO)
                TOTAL_SALVADO += CANT_SALVANDO
                CANT_SALVANDO = 0
                MainServidor.redactarLogServidor("Salvando los cercados: ")
                for (cercado in CERCADOS.values()) {
                    try {
                        GestorSQL.REPLACE_CERCADO(cercado)
                        CANT_SALVANDO++
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                MainServidor.redactarLogServidorln("Finalizó con " + CANT_SALVANDO)
                TOTAL_SALVADO += CANT_SALVANDO
                CANT_SALVANDO = 0
                MainServidor.redactarLogServidor("Salvando las monturas: ")
                for (montura in _MONTURAS.values()) {
                    try {
                        if (montura.estaCriando()) {
                            GestorSQL.REPLACE_MONTURA(montura, false)
                            CANT_SALVANDO++
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                MainServidor.redactarLogServidorln("Finalizó con " + CANT_SALVANDO)
                TOTAL_SALVADO += CANT_SALVANDO
                CANT_SALVANDO = 0
                MainServidor.redactarLogServidor("Salvando las casas: ")
                for (casa in CASAS.values()) {
                    try {
                        GestorSQL.REPLACE_CASA(casa)
                        CANT_SALVANDO++
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                MainServidor.redactarLogServidorln("Finalizó con " + CANT_SALVANDO)
                TOTAL_SALVADO += CANT_SALVANDO
                CANT_SALVANDO = 0
                MainServidor.redactarLogServidor("Salvando los cofres: ")
                for (cofre in COFRES.values()) {
                    try {
                        GestorSQL.REPLACE_COFRE(cofre, true)
                        CANT_SALVANDO++
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                MainServidor.redactarLogServidorln("Finalizó con " + CANT_SALVANDO)
                TOTAL_SALVADO += CANT_SALVANDO
                CANT_SALVANDO = 0
                MainServidor.redactarLogServidor("Salvando los rankings PVP: ")
                for (rank in _RANKINGS_PVP.values()) {
                    try {
                        GestorSQL.REPLACE_RANKING_PVP(rank)
                        CANT_SALVANDO++
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                MainServidor.redactarLogServidorln("Finalizó con " + CANT_SALVANDO)
                TOTAL_SALVADO += CANT_SALVANDO
                CANT_SALVANDO = 0
                MainServidor.redactarLogServidor("Salvando los rankings Koliseo: ")
                for (rank in _RANKINGS_KOLISEO.values()) {
                    try {
                        GestorSQL.REPLACE_RANKING_KOLISEO(rank)
                        CANT_SALVANDO++
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                MainServidor.redactarLogServidorln("Finalizó con " + CANT_SALVANDO)
                TOTAL_SALVADO += CANT_SALVANDO
                CANT_SALVANDO = 0
                MainServidor.redactarLogServidor("Salvando las cuentas: ")
                for (cuenta in _CUENTAS.values()) {
                    try {
                        if (cuenta.enLinea() || inclusoOffline) {
                            GestorSQL.REPLACE_CUENTA_SERVIDOR(cuenta, GestorSQL.GET_PRIMERA_VEZ(cuenta.getNombre()))
                            CANT_SALVANDO++
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                MainServidor.redactarLogServidorln("Finalizó con " + CANT_SALVANDO)
                TOTAL_SALVADO += CANT_SALVANDO
                MainServidor.redactarLogServidorln("------------ Se salvó exitosamente el servidor 100% ------------")
            } catch (e: ConcurrentModificationException) {
                MainServidor.redactarLogServidorln("------------ Ocurrio un error de concurrent " + e.toString())
                e.printStackTrace()
            } catch (e: Exception) {
                MainServidor.redactarLogServidorln("------------ Error al salvar : " + e.toString())
                e.printStackTrace()
            } finally {
                if (SERVIDOR_ESTADO != Constantes.SERVIDOR_OFFLINE) {
                    setServidorEstado(Constantes.SERVIDOR_ONLINE)
                }
                if (!MainServidor.URL_BACKUP_PHP.isEmpty()) {
                    try {
                        if (!MainServidor.PARAM_AUTO_COMMIT) {
                            Thread.sleep(20000)
                        }
                        MainServidor.redactarLogServidorln("REALIZANDO BACKUP SQL DEL SERVIDOR")
                        Encriptador.consultaWeb(MainServidor.URL_BACKUP_PHP)
                        MainServidor.redactarLogServidorln("BACKUP SQL REALIZADO CON EXITO")
                    } catch (e: Exception) {
                        MainServidor.redactarLogServidorln("ERROR AL REALIZAR BACKUP SQL")
                        e.printStackTrace()
                    }
                }
                MainServidor.imprimirLogCombates()
                SALVANDO = false
            }
        }

        fun salvarMapasEstrellas() {
            if (MainServidor.MODO_DEBUG || MainServidor.MODO_PVP) {
                return
            }
            CANT_SALVANDO = 0
            MainServidor.redactarLogServidor("Salvando las estrellas de los mobs: ")
            GestorSQL.VACIAR_MAPAS_ESTRELLAS()
            val declaracion: PreparedStatement = GestorSQL
                    .GET_STATEMENT_SQL_DINAMICA("REPLACE INTO `mapas_estrellas` VALUES (?,?);")
            for (mapa in MAPAS.values()) {
                if (mapa.getGrupoMobsTotales().isEmpty()) {
                    continue
                }
                val s = StringBuilder()
                for (gm in mapa.getGrupoMobsTotales().values()) {
                    if (gm.realBonusEstrellas() <= 0) {
                        continue
                    }
                    if (s.length() > 0) {
                        s.append(",")
                    }
                    s.append(gm.realBonusEstrellas())
                }
                if (s.length() === 0) {
                    continue
                }
                CANT_SALVANDO++
                GestorSQL.REPLACE_MAPAS_ESTRELLAS_BATCH(declaracion, mapa.getID(), s.toString())
            }
            if (CANT_SALVANDO > 0) {
                GestorSQL.ejecutarBatch(declaracion)
            }
            MainServidor.redactarLogServidorln("Finalizo con " + CANT_SALVANDO)
            TOTAL_SALVADO += CANT_SALVANDO
        }

        fun getCofresPorCasa(casa: Casa): ArrayList<Cofre> {
            val cofres: ArrayList<Cofre> = ArrayList<Cofre>()
            for (cofre in COFRES.values()) {
                if (cofre.getCasaID() === casa.getID()) {
                    cofres.add(cofre)
                }
            }
            return cofres
        }

        fun getCofrePorUbicacion(mapaID: Int, celdaID: Short): Cofre? {
            for (cofre in COFRES.values()) {
                if (cofre.getMapaID() === mapaID && cofre.getCeldaID() === celdaID) {
                    return cofre
                }
            }
            return null
        }

        fun borrarLasCuentas(minutos: Long): Boolean {
            if (!MainServidor.PARAM_BORRAR_CUENTAS_VIEJAS) {
                return false
            }
            return if (MINUTOS_VIDA_REAL - minutos > MainServidor.DIAS_PARA_BORRAR * 24 * 60) {
                // se convierte a minutos para comparar 2 meses
                true
            } else false
        }

        fun usoMemoria() {
            System.out.println("======== FreeMemory: " + (Runtime.getRuntime().freeMemory() / 1048576f).toString() + " MB ========")
        }

        fun getCantCercadosGremio(id: Int): Byte {
            var i: Byte = 0
            for (cercado in CERCADOS.values()) {
                if (cercado.getGremio() != null && cercado.getGremio().getID() === id) {
                    i++
                }
            }
            return i
        }

        fun addMapaEstrellas(id: Int, estrellas: String) {
            try {
                val array: ArrayList<Integer> = ArrayList()
                for (s in estrellas.split(",")) {
                    array.add(Integer.parseInt(s))
                }
                MAPAS_ESTRELLAS.put(id, array)
            } catch (e: Exception) {
            }
        }

        fun addMapaHeroico(id: Int, mobs: String, objetos: String, kamas: String) {
            try {
                val array: ArrayList<String> = ArrayList()
                val m: Array<String> = mobs.split(Pattern.quote("|"))
                val o: Array<String> = objetos.split(Pattern.quote("|"))
                val k: Array<String> = kamas.split(Pattern.quote("|"))
                for (i in m.indices) {
                    array.add(m[i] + "|" + o[i] + "|" + k[i])
                }
                MAPAS_HEROICOS.put(id, array)
            } catch (e: Exception) {
            }
        }

        fun getMapaHeroico(id: Int): ArrayList<String>? {
            return MAPAS_HEROICOS[id]
        }

        fun getMapaEstrellas(id: Int): ArrayList<Integer>? {
            return MAPAS_ESTRELLAS[id]
        }

        fun getArea(area: Int): Area? {
            return AREAS[area]
        }

        fun getSubArea(subArea: Int): SubArea? {
            return SUB_AREAS[subArea]
        }

        fun getSuperArea(superArea: Int): SuperArea? {
            return SUPER_AREAS[superArea]
        }

        fun addArea(area: Area) {
            AREAS.put(area.getID(), area)
        }

        fun addSubArea(subArea: SubArea) {
            SUB_AREAS.put(subArea.getID(), subArea)
        }

        fun addSuperArea(superArea: SuperArea) {
            SUPER_AREAS.put(superArea.getID(), superArea)
        }

        fun addExpNivel(nivel: Int, exp: Experiencia?) {
            // if (nivel > Bustemu.NIVEL_MAX_PERSONAJE) {
            // return;
            // }
            EXPERIENCIA.put(nivel, exp)
        }

        val creaTuItem: Map<Any, Any>
            get() = CREA_TU_ITEM

        fun getCreaTuItem(id: Int): CreaTuItem? {
            return CREA_TU_ITEM[id]
        }

        val cuentas: ConcurrentHashMap<Integer, Cuenta>
            get() = _CUENTAS

        /*
     * public static void actualizarEtapa(String etapa) { for(Cuenta cuenta :
     * _CUENTAS.values()) { cuenta.setLoginPremio(etapa+",1,0"); } }
     */
        fun getCuenta(id: Int): Cuenta {
            return _CUENTAS.get(id)
        }

        fun addRespuestaNPC(respuesta: RespuestaNPC) {
            NPC_RESPUESTAS.put(respuesta.getID(), respuesta)
        }

        fun getRespuestaNPC(id: Int): RespuestaNPC? {
            return NPC_RESPUESTAS[id]
        }

        fun addPreguntaNPC(pregunta: PreguntaNPC) {
            NPC_PREGUNTAS.put(pregunta.getID(), pregunta)
        }

        fun getPreguntaNPC(id: Int): PreguntaNPC? {
            return NPC_PREGUNTAS[id]
        }

        fun getNPCModelo(id: Int): NPCModelo? {
            return NPC_MODELOS[id]
        }

        fun addNPCModelo(npcModelo: NPCModelo) {
            NPC_MODELOS.put(npcModelo.getID(), npcModelo)
        }

        fun getMapaSQL(id: Int): Mapa? {
            return MAPAS[id]
        }

        fun getMapa(id: Int): Mapa? {
            GestorSQL.CARGAR_MAPAS_IDS(id.toString() + "")
            return MAPAS[id]
        }

        fun addMapa(mapa: Mapa) {
            if (!MAPAS.containsKey(mapa.getID())) {
                MAPAS.put(mapa.getID(), mapa)
            }
        }

        fun mapaExiste(mapa: Int): Boolean {
            return MAPAS.containsKey(mapa)
        }

        fun mapaPorCoordXYContinente(mapaX: Int, mapaY: Int, idContinente: Int): Mapa? {
            for (mapa in MAPAS.values()) {
                if (mapa.getX() === mapaX && mapa.getY() === mapaY && mapa.getSubArea().getArea().getSuperArea().getID() === idContinente) {
                    return mapa
                }
            }
            return null
        }

        fun mapaPorCoordenadas(mapaX: Int, mapaY: Int, idContinente: Int): String {
            val str = StringBuilder()
            for (mapa in MAPAS.values()) {
                if (mapa.getX() === mapaX && mapa.getY() === mapaY && mapa.getSubArea().getArea().getSuperArea().getID() === idContinente) {
                    str.append(mapa.getID().toString() + ", ")
                }
            }
            return str.toString()
        }

        fun subirEstrellasMobs(cant: Int) {
            for (mapa in MAPAS.values()) {
                mapa.subirEstrellasMobs(cant)
            }
        }

        var evento = false
        var eventoComprando = false
        var eventoMensaje = ""
        var portalMensaje = ""
        var mapaNPC = -1
        var portalNPC = -1
        var mapaMOB = -1
        fun agregarMobEvento() {
            var mapa: Mapa? = null
            var celdaEvento: Short = -1
            do {
                val mapaEvento: Int = GestorSQL.mapa_evento(MainServidor.EVENTO_AREAS)
                mapa = MAPAS[mapaEvento]
            } while (mapa == null || MainServidor.EVENTO_MAPA_NO_PERMITIDOS.contains(mapa.getID()))
            celdaEvento = mapa.getRandomCeldaIDLibre()
            val mobEvento: Array<String> = MainServidor.EVENTO_MOBS.split(",")
            val Mob: Int = Formulas.getRandomInt(0, mobEvento.size - 1)
            val tipoGrupo: TipoGrupo = Constantes.getTipoGrupoMob(2)
            val grupoMob: GrupoMob = mapa.addGrupoMobPorTipo(celdaEvento, mobEvento[Mob], tipoGrupo, null, null, true, 0, "", 0)
            grupoMob.set_evento(true)
            mapaMOB = mapa.getID()
            eventoMensaje = mapa.getSubArea().getNombre()
            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(
                    "Se ha puesto un Mob de EVENTO en la subarea: " + eventoMensaje)
        }

        var INVASION_MAPA = "0"
        var INVASION_AREA = "No hay invasiones disponibles en este momento"
        var AREA_INVASION = -1
        fun agregarMobIvancion() {
            var mapa: Mapa? = null
            val areas: Array<String> = MainServidor.INVASION_SUBAREAS.split(",")
            if (AREA_INVASION > -1) {
                val mapaInvasionVieja: Array<String> = GestorSQL.mapa_evento_subarea(areas[AREA_INVASION]).split(",")
                for (mapaTemp in mapaInvasionVieja) {
                    try {
                        val mapaT: Short = Short.parseShort(mapaTemp)
                        val mapaTe: Mapa? = getMapa(mapaT.toInt())
                        mapaTe.borrarMobsEvento()
                    } catch (e: Exception) {
                        // TODO: handle exception
                    }
                }
            }
            AREA_INVASION = Formulas.getRandomInt(0, areas.size - 1)
            val mapaEvento: Array<String> = GestorSQL.mapa_evento_subarea(areas[AREA_INVASION]).split(",")
            INVASION_MAPA = mapaEvento[0]
            for (i in mapaEvento.indices) {
                mapa = MAPAS[Integer.parseInt(mapaEvento[i])]
                for (j in 0 until MainServidor.EVENTO_MAPA_MOB_CANTIDAD) {
                    val celdaEvento: Short = mapa.getRandomCeldaIDLibreEvento()
                    val tipoGrupo: TipoGrupo = Constantes.getTipoGrupoMob(2)
                    val grupoMob: GrupoMob = mapa.addGrupoMobPorTipoAgresion(celdaEvento, MainServidor.INVASION_MOBS, tipoGrupo,
                            MainServidor.INVASION_CONDICIONES, null, true, MainServidor.INVASION_AGRESION_MOBS, 0, "", 0)
                    grupoMob.set_evento(true)
                }
            }
            INVASION_AREA = "Hay una invasion en la zona: " + mapa.getSubArea().getNombre()
            GestorSalida.ENVIAR_WA_INVASION(mapa.getSubArea().getNombre())
            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(
                    "Ha empezado la invasion en la zona: " + mapa.getSubArea().getNombre())
        }

        private var evento_npc = false
        fun agregarPortal() {
            if (portalNPC != -1) {
                val mapa: Mapa? = getMapa(portalNPC)
                for (npc in mapa.getNPCs().values()) {
                    if (!npc.isPortal()) continue
                    mapa.borrarNPC(npc.getID())
                    GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(mapa, npc.getID())
                }
            }
            var mapa: Mapa? = null
            var celdaEvento: Short = -1
            do {
                val mapaEvento: Int = Formulas.getRandomInt(0, MAPAS.size() - 1) as Short.toInt()
                mapa = MAPAS[mapaEvento]
            } while (mapa == null || MainServidor.EVENTO_MAPA_NO_PERMITIDOS.contains(mapa.getID())
                    || CASAS.containsKey(mapa.getID()) || mapa.getSubArea().getArea().getID() === 12 || mapa.getSubArea().getArea().getID() === 8 || mapa.getSubArea().getID() === 91 || mapa.getSubArea().getID() === 99 || mapa.getSubArea().getID() === 100)
            celdaEvento = mapa.getRandomCeldaIDLibre()
            mapa.addNPC(getNPCModelo(MainServidor.PORTAL_NPC), celdaEvento, 1.toByte(), "")
            portalNPC = mapa.getID()
            portalMensaje = mapa.getSubArea().getNombre()
        }

        fun agregarNpcEvento() {
            if (evento) {
                val mapa: Mapa? = getMapa(mapaNPC)
                for (npc in mapa.getNPCs().values()) {
                    if (!npc.is_evento()) continue
                    mapa.borrarNPC(npc.getID())
                    GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(mapa, npc.getID())
                }
            }
            var mapa: Mapa? = null
            var celdaEvento: Short = -1
            do {
                val mapaEvento = Formulas.getRandomInt(0, MAPAS.size() - 1) as Int
                mapa = MAPAS[mapaEvento]
            } while (mapa == null || MainServidor.EVENTO_MAPA_NO_PERMITIDOS.contains(mapa.getID())
                    || CASAS.containsKey(mapa.getID()) || mapa.getSubArea().getArea().getID() === 12 || mapa.getSubArea().getArea().getID() === 8 || mapa.getSubArea().getID() === 91 || mapa.getSubArea().getID() === 99 || mapa.getSubArea().getID() === 100)
            celdaEvento = mapa.getRandomCeldaIDLibre()
            if (MainServidor.EVENTO_NPC_2 > 0.toShort()) {
                if (evento_npc) {
                    mapa.addNPC(getNPCModelo(MainServidor.EVENTO_NPC_2), celdaEvento, 1.toByte(), "")
                } else {
                    mapa.addNPC(getNPCModelo(MainServidor.EVENTO_NPC), celdaEvento, 1.toByte(), "")
                }
                evento_npc = !evento_npc
            } else {
                mapa.addNPC(getNPCModelo(MainServidor.EVENTO_NPC), celdaEvento, 1.toByte(), "")
            }
            evento = true
            mapaNPC = mapa.getID()
            eventoMensaje = mapa.getSubArea().getNombre()
            if (!evento_npc) {
                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(
                        "Se ha puesto un npc de EVENTO en la subarea: " + eventoMensaje)
            } else {
                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(
                        "Se ha puesto un npc de EVENTO MERCANTE en la subarea: " + eventoMensaje)
            }
        }

        fun subirEstrellasOI(cant: Int) {
            for (mapa in MAPAS.values()) {
                mapa.subirEstrellasOI(cant)
            }
        }

        fun getCuentaPorApodo(apodo: String?): Cuenta? {
            for (cuenta in _CUENTAS.values()) {
                if (cuenta.getApodo().equals(apodo)) {
                    return cuenta
                }
            }
            return null
        }

        fun strCuentasOnline(): String {
            val str = StringBuilder()
            for (perso in ONLINES) {
                if (str.length() > 0) {
                    str.append(",")
                }
                str.append(perso.getCuentaID())
            }
            return str.toString()
        }

        fun getCuentaPorNombre(nombre: String): Cuenta? {
            return if (_CUENTAS_POR_NOMBRE.get(nombre.toLowerCase()) != null) _CUENTAS.get(_CUENTAS_POR_NOMBRE.get(nombre.toLowerCase())) else null
        }

        fun addCuenta(cuenta: Cuenta) {
            _CUENTAS.put(cuenta.getID(), cuenta)
            _CUENTAS_POR_NOMBRE.put(cuenta.getNombre().toLowerCase(), cuenta.getID())
        }

        fun addPersonaje(perso: Personaje) {
            if (perso.getID() > SIG_ID_PERSONAJE) {
                SIG_ID_PERSONAJE = perso.getID()
            }
            _PERSONAJES.put(perso.getID(), perso)
        }

        fun getPersonaje(id: Int): Personaje {
            return _PERSONAJES.get(id)
        }

        val cantidadPersonajes: Int
            get() = _PERSONAJES.size()

        fun getPersonajePorNombre(nombre: String?): Personaje? {
            val Ps: ArrayList<Personaje> = ArrayList<Personaje>()
            Ps.addAll(_PERSONAJES.values())
            for (perso in Ps) {
                if (perso.getNombreGM().equalsIgnoreCase(nombre)) {
                    return perso
                } else if (perso.getNombre().equalsIgnoreCase(nombre)) {
                    return perso
                }
            }
            return null
        } // listo

        fun getCasaPorUbicacion(mapaID: Int, celdaID: Int): Casa? {
            for (casa in CASAS.values()) {
                if (casa.getMapaIDFuera() === mapaID && casa.getCeldaIDFuera() === celdaID) {
                    return casa
                }
            }
            return null
        }

        fun cargarPropiedadesCasa(perso: Personaje) {
            val str = StringBuilder()
            for (casa in CASAS.values()) {
                try {
                    if (casa.getMapaIDFuera() === perso.getMapa().getID()) {
                        str.append("hP" + casa.propiedadesPuertaCasa(perso) + 0x00.toChar())
                        str.append("hL" + casa.informacionCasa(perso.getID()) + 0x00.toChar())
                    }
                } catch (e: Exception) {
                }
            }
            GestorSalida.enviar(perso, str.toString(), true)
            GestorSalida.imprimir("CASA: PERSOJES", str.toString())
        }

        fun cantCasasGremio(gremioID: Int): Byte {
            var i: Byte = 0
            for (casa in CASAS.values()) {
                if (casa.getGremioID() === gremioID) {
                    i++
                }
            }
            return i
        }

        fun getCasaDePj(persoID: Int): Casa? {
            for (casa in CASAS.values()) {
                if (casa.esSuCasa(persoID)) {
                    return casa
                }
            }
            return null
        }

        fun borrarCasaGremio(gremioID: Int) {
            for (casa in CASAS.values()) {
                if (casa.getGremioID() === gremioID) {
                    casa.nullearGremio()
                    casa.actualizarDerechos(0)
                }
            }
        }

        fun getCasaDentroPorMapa(mapaID: Int): Casa? {
            for (casa in CASAS.values()) {
                if (casa.getMapasContenidos().contains(mapaID)) {
                    return casa
                }
            }
            return null
        }

        val alineacionTodasSubareas: String
            get() {
                val str = StringBuilder()
                for (subarea in SUB_AREAS.values()) {
                    try {
                        if (str.length() > 0) {
                            str.append("|")
                        }
                        str.append(subarea.getID().toString() + ";" + subarea.getAlineacion())
                    } catch (e: Exception) {
                        if (MainServidor.MODO_IMPRIMIR_LOG) System.out.println(e.getMessage())
                    }
                }
                return str.toString()
            }

        fun getExpCazaCabezas(nivel: Int): Long {
            var nivel = nivel
            if (nivel >= MainServidor.NIVEL_MAX_OMEGA) {
                nivel = MainServidor.NIVEL_MAX_OMEGA - 1
            } else if (nivel < 1) {
                nivel = 1
            }
            if (MainServidor.PARAM_EXP_PVP_MISION_POR_TABLA) {
                var exp: Long = 0
                if (nivel < 60) {
                    exp = Long.parseLong("440000000000")
                } else if (nivel < 175) {
                    exp = Long.parseLong("999000000000")
                } else if (nivel <= 300) {
                    exp = Long.parseLong("999000000000")
                } else if (nivel <= 340) {
                    exp = Long.parseLong("999000000000")
                } else if (nivel <= 400) {
                    exp = Long.parseLong("999000000000")
                } else if (nivel <= 450) {
                    exp = Long.parseLong("1299000000000")
                } else if (nivel <= 500) {
                    exp = Long.parseLong("1299000000000")
                } else if (nivel <= MainServidor.NIVEL_MAX_PERSONAJE) {
                    exp = 6000000
                }
                return exp
            }
            return Long.parseLong(EXPERIENCIA[nivel]!!._personaje) / 20
        }

        fun getExpPersonaje(nivel: Int, nivelomega: Int): BigInteger {
            var nivel = nivel
            var nivelomega = nivelomega
            if (nivel > MainServidor.NIVEL_MAX_PERSONAJE) {
                if (nivelomega > MainServidor.NIVEL_MAX_OMEGA) {
                    return BigInteger(
                            "9999999999999999999999999999999999999999999999999999999999999999999999999999999999")
                } else if (nivelomega < 1) {
                    nivelomega = 1
                }
                return EXPERIENCIA[nivelomega]!!._omega
            } else if (nivel < 1) {
                nivel = 1
            }
            return BigInteger(EXPERIENCIA[nivel]!!._personaje + "")
        }

        fun getExpPersonajeReset(nivel: Int, nivelomega: Int): BigInteger {
            var nivel = nivel
            var nivelomega = nivelomega
            if (nivelomega > 0) {
                if (nivelomega > MainServidor.NIVEL_MAX_OMEGA) {
                    return BigInteger(
                            "9999999999999999999999999999999999999999999999999999999999999999999999999999999999")
                } else if (nivelomega < 1) {
                    nivelomega = 1
                }
                return EXPERIENCIA[nivelomega]!!._omega
            } else if (nivel < 1) {
                nivel = 1
            }
            return BigInteger(EXPERIENCIA[nivel]!!._personaje)
        }

        fun getExpGremio(nivel: Int): Long {
            var nivel = nivel
            if (nivel > MainServidor.NIVEL_MAX_GREMIO) {
                nivel = MainServidor.NIVEL_MAX_GREMIO
            } else if (nivel < 1) {
                nivel = 1
            }
            return EXPERIENCIA[nivel]!!._gremio
        }

        fun getExpMontura(nivel: Int): Long {
            var nivel = nivel
            if (nivel > MainServidor.NIVEL_MAX_MONTURA) {
                nivel = MainServidor.NIVEL_MAX_MONTURA
            } else if (nivel < 1) {
                nivel = 1
            }
            return EXPERIENCIA[nivel]!!._montura.toLong()
        }

        fun getExpEncarnacion(nivel: Int): Long {
            var nivel = nivel
            if (nivel > MainServidor.NIVEL_MAX_ENCARNACION) {
                nivel = MainServidor.NIVEL_MAX_ENCARNACION
            } else if (nivel < 1) {
                nivel = 1
            }
            return EXPERIENCIA[nivel]!!._encarnacion
        }

        fun getExpOficio(nivel: Int): Int {
            var nivel = nivel
            if (nivel > MainServidor.NIVEL_MAX_OFICIO) {
                nivel = MainServidor.NIVEL_MAX_OFICIO
            } else if (nivel < 1) {
                nivel = 1
            }
            return EXPERIENCIA[nivel]!!._oficio
        }

        fun getExpAlineacion(nivel: Int): Int {
            var nivel = nivel
            if (nivel > MainServidor.NIVEL_MAX_ALINEACION) {
                nivel = MainServidor.NIVEL_MAX_ALINEACION
            } else if (nivel < 1) {
                nivel = 1
            }
            return EXPERIENCIA[nivel]!!._alineacion
        }

        fun getExpParaNivelAlineacion(nivel: Int): Int {
            var nivel = nivel
            if (nivel > MainServidor.NIVEL_MAX_ALINEACION) {
                nivel = MainServidor.NIVEL_MAX_ALINEACION
            } else if (nivel < 2) {
                nivel = 2
            }
            return EXPERIENCIA[nivel]!!._alineacion - EXPERIENCIA[nivel - 1]!!._alineacion
        }

        // public static Experiencia getExpNivel(final int nivel) {
        // return Experiencia.get(nivel);
        // }
        fun getObjInteractivoModelo(id: Int): ObjetoInteractivoModelo {
            return OBJETOS_INTERACTIVOS_MODELOS.get(id)
        }

        fun getObjIntModeloPorGfx(gfx: Int): ObjetoInteractivoModelo? {
            for (oi in OBJETOS_INTERACTIVOS_MODELOS) {
                if (oi.getGfxs().contains(gfx)) {
                    return oi
                }
            }
            return null
        }

        fun addObjInteractivo(oi: ObjetoInteractivo?) {
            OBJETOS_INTERACTIVOS.add(oi)
        }

        fun addObjInteractivoModelo(OIM: ObjetoInteractivoModelo?) {
            OBJETOS_INTERACTIVOS_MODELOS.add(OIM)
        }

        fun getOficio(id: Int): Oficio? {
            return OFICIOS[id]
        }

        fun addOficio(oficio: Oficio) {
            OFICIOS.put(oficio.getID(), oficio)
        }

        fun addReceta(id: Int, arrayDuos: ArrayList<Duo<Integer?, Integer?>?>?) {
            RECETAS.put(id, arrayDuos)
        }

        fun getReceta(id: Int): ArrayList<Duo<Integer, Integer>>? {
            return RECETAS[id]
        }

        fun esIngredienteDeReceta(id: Int): Boolean {
            for (a in RECETAS.values()) {
                for (d in a) {
                    if (d._primero == id) return true
                }
            }
            return false
        }

        fun getIDRecetaPorIngredientes(listaIDRecetas: ArrayList<Integer?>?,
                                       ingredientes: Map<Integer?, Long?>): Int {
            if (listaIDRecetas == null) {
                return -1
            }
            for (id in listaIDRecetas) {
                val receta: ArrayList<Duo<Integer, Integer>>? = RECETAS[id]
                if (receta == null || receta.size() !== ingredientes.size()) {
                    continue
                }
                var ok = true
                for (ing in receta) {
                    if (ingredientes[ing._primero] == null) {
                        ok = false
                        break
                    }
                    val primera = ingredientes[ing._primero]!!
                    val segunda: Int = ing._segundo
                    if (primera != segunda.toLong()) {
                        ok = false
                        break
                    }
                }
                if (ok) {
                    return id
                }
            }
            return -1
        }

        fun addObjetoSet(objetoSet: ObjetoSet) {
            OBJETOS_SETS.put(objetoSet.getID(), objetoSet)
        }

        fun getObjetoSet(id: Int): ObjetoSet? {
            return OBJETOS_SETS[id]
        }

        val numeroObjetoSet: Int
            get() = OBJETOS_SETS.size()

        fun sigIDPersonaje(): Int {
            return ++SIG_ID_PERSONAJE
        }

        // public static int sigIDCofre() {
        // return ++sigIDCofre;
        // }
        @Synchronized
        fun sigIDObjeto(): Int {
            return ++SIG_ID_OBJETO
        }

        @Synchronized
        fun sigIDLineaMercadillo(): Int {
            return ++SIG_ID_LINEA_MERCADILLO
        }

        fun sigIDRecaudador(): Int {
            SIG_ID_RECAUDADOR -= 3
            return SIG_ID_RECAUDADOR
        }

        @Synchronized
        fun sigIDMontura(): Int {
            SIG_ID_MONTURA -= 3
            return SIG_ID_MONTURA
        }

        @Synchronized
        fun sigIDPrisma(): Int {
            SIG_ID_PRISMA -= 3
            return SIG_ID_PRISMA
        }

        @Synchronized
        fun sigIDGremio(): Int {
            if (_GREMIOS.isEmpty()) {
                return 1
            }
            var n = 0
            for (entry in _GREMIOS.entrySet()) {
                val x: Int = entry.getKey()
                if (n < x) {
                    n = x
                }
            }
            return n + 1
        }

        fun addGremio(gremio: Gremio) {
            _GREMIOS.put(gremio.getID(), gremio)
        }

        @Synchronized
        fun nombreGremioUsado(nombre: String?): Boolean {
            try {
                for (gremio in _GREMIOS.values()) {
                    if (gremio.getNombre().equalsIgnoreCase(nombre)) {
                        return true
                    }
                }
            } catch (e: Exception) {
                return true
            }
            return false
        }

        @Synchronized
        fun emblemaGremioUsado(emblema: String?): Boolean {
            for (gremio in _GREMIOS.values()) {
                if (gremio.getEmblema().equals(emblema)) {
                    return true
                }
            }
            return false
        }

        val sucess: Map<Any, Any>
            get() = SUCCESS

        fun addSucess(sucess: Sucess) {
            SUCCESS.put(sucess.getId(), sucess)
        }

        fun getGremio(i: Int): Gremio {
            return _GREMIOS.get(i)
        }

        fun addZaap(mapa: Int, celda: Short) {
            ZAAPS.put(mapa, celda)
        }

        fun getCeldaZaapPorMapaID(mapaID: Int): Int {
            try {
                if (ZAAPS[mapaID] != null) {
                    return ZAAPS[mapaID]
                }
            } catch (e: Exception) {
            }
            return -1
        }

        fun getCeldaCercadoPorMapaID(mapaID: Short): Short {
            val cercado: Cercado = getMapa(mapaID.toInt()).getCercado()
            return if (cercado != null && cercado.getCeldaID() > 0) {
                cercado.getCeldaID()
            } else -1
        }

        fun eliminarMontura(montura: Montura) {
            montura.setUbicacion(Ubicacion.NULL)
            _MONTURAS.remove(montura.getID())
            GestorSQL.DELETE_MONTURA(montura)
            val objetos: ArrayList<Objeto> = ArrayList<Objeto>()
            objetos.addAll(montura.getObjetos())
            eliminarObjetosPorArray(objetos)
        }

        @Synchronized
        fun eliminarPersonaje(perso: Personaje, totalmente: Boolean) {
            // perso.getObjetos().clear();
            if (perso.esMercante()) {
                perso.getMapa().removerMercante(perso.getID())
                GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(perso.getMapa(), perso.getID())
            }
            if (totalmente) {
                if (perso.getMontura() != null) {
                    eliminarMontura(perso.getMontura())
                }
                val casa: Casa? = getCasaDePj(perso.getID())
                if (casa != null) casa.resetear()
                for (cercado in CERCADOS.values()) {
                    if (cercado.getDueñoID() === perso.getID()) {
                        val criando: Array<String> = cercado.strPavosCriando().split(";")
                        for (pavo in criando) {
                            try {
                                eliminarMontura(getMontura(Integer.parseInt(pavo)))
                            } catch (e: Exception) {
                            }
                        }
                        if (cercado.strPavosCriando().length() > 0) {
                            GestorSQL.DELETE_DRAGOPAVO_LISTA(cercado.strPavosCriando().replaceAll(";", ","))
                        }
                        cercado.resetear()
                        GestorSQL.DELETE_CERCADO(cercado.getMapa().getID())
                    }
                }
                if (perso.getMiembroGremio() != null) {
                    val gremio: Gremio = perso.getGremio()
                    if (gremio.getCantidadMiembros() <= 1 || perso.getMiembroGremio().getRango() === 1) {
                        eliminarGremio(gremio)
                    }
                    gremio.expulsarMiembro(perso.getID())
                }
                delRankingPVP(perso.getID())
                delRankingKoliseo(perso.getID())
                val esposo: Personaje = getPersonaje(perso.getEsposoID())
                if (esposo != null) {
                    esposo.divorciar()
                    perso.divorciar()
                }
                val objetos: ArrayList<Objeto> = ArrayList<Objeto>()
                objetos.addAll(perso.getObjetosTodos())
                eliminarObjetosPorArray(objetos)
                objetos.clear()
                objetos.addAll(perso.getObjetosTienda())
                eliminarObjetosPorArray(objetos)
                GestorSQL.DELETE_PERSONAJE(perso)
                _PERSONAJES.remove(perso.getID())
            }
        }

        fun eliminarGremio(gremio: Gremio) {
            gremio.eliminarTodosRecaudadores()
            gremio.expulsarTodosMiembros()
            borrarCasaGremio(gremio.getID())
            GestorSQL.DELETE_GREMIO(gremio.getID())
            _GREMIOS.remove(gremio.getID())
            // gremio.destruir();
        }

        fun cuentasIP(ip: String?): Int {
            var veces = 0
            for (c in _CUENTAS.values()) {
                if (!c.enLinea()) {
                    continue
                }
                if (c.getActualIP().equals(ip)) {
                    veces++
                }
            }
            return veces
        }

        fun addOnline(perso: Personaje?) {
            if (!ONLINES.contains(perso)) ONLINES.add(perso)
        }

        fun removeOnline(perso: Personaje?) {
            ONLINES.remove(perso)
        }

        fun addHechizo(hechizo: Hechizo) {
            HECHIZOS.put(hechizo.getID(), hechizo)
        }

        fun addObjModelo(objMod: ObjetoModelo) {
            OBJETOS_MODELOS.put(objMod.getID(), objMod)
        }

        private fun prepararPanelItems() {
            for (tipo in 1..199) {
                val add = StringBuilder()
                for (objMod in OBJETOS_MODELOS.values()) {
                    if (objMod.getTipo() !== tipo) {
                        continue
                    }
                    if (MainServidor.SISTEMA_ITEMS_TIPO_DE_PAGO.equalsIgnoreCase("KAMAS")) {
                        if (objMod.getPrecioPanelKamas() <= 0) {
                            continue
                        }
                    } else {
                        if (objMod.getPrecioPanelOgrinas() <= 0) {
                            continue
                        }
                    }
                    if (add.length() > 0) {
                        add.append("|")
                    }
                    add.append(objMod.getID().toString() + ";")
                    add.append(objMod.stringStatsModelo().toString() + ";")
                    if (MainServidor.SISTEMA_ITEMS_TIPO_DE_PAGO.equalsIgnoreCase("KAMAS")) {
                        add.append(objMod.getPrecioPanelKamas())
                    } else {
                        add.append(objMod.getPrecioPanelOgrinas())
                    }
                    add.append(";")
                    if (MainServidor.PARAM_SISTEMA_ITEMS_SOLO_PERFECTO) {
                        add.append("1") // solo perfecto
                    } else {
                        add.append("0")
                        if (MainServidor.PARAM_SISTEMA_ITEMS_EXO_PA_PM) {
                            if (!MainServidor.SISTEMA_ITEMS_EXO_TIPOS_NO_PERMITIDOS.contains(objMod.getTipo())) {
                                add.append(";" + (if (objMod.tieneStatInicial(Constantes.STAT_MAS_PA)) "0" else "1") + ";"
                                        + if (objMod.tieneStatInicial(Constantes.STAT_MAS_PM)) "0" else "1")
                            }
                        }
                    }
                }
                if (add.length() > 0) {
                    SISTEMA_ITEMS.put(tipo, add.toString())
                }
            }
        }

        val tiposPanelItems: String
            get() {
                val str = StringBuilder()
                for (s in SISTEMA_ITEMS.keySet()) {
                    if (str.length() > 0) {
                        str.append(";")
                    }
                    str.append(s)
                }
                return str.toString()
            }

        fun getObjetosPorTipo(out: Personaje?, tipo: Short) {
            if (SISTEMA_ITEMS[tipo] == null) {
                for (s in SISTEMA_ITEMS.keySet()) {
                    GestorSalida.ENVIAR_bSO_PANEL_ITEMS_OBJETOS_POR_TIPO(out, s.toString() + "@" + SISTEMA_ITEMS[s])
                }
                GestorSalida.ENVIAR_bSO_PANEL_ITEMS_OBJETOS_POR_TIPO(out, "-1@")
            } else {
                GestorSalida.ENVIAR_bSO_PANEL_ITEMS_OBJETOS_POR_TIPO(out, tipo.toString() + "@" + SISTEMA_ITEMS[tipo])
            }
        }

        fun getHechizo(id: Int): Hechizo? {
            return HECHIZOS[id]
        }

        fun getObjetoModelo(id: Int): ObjetoModelo? {
            return OBJETOS_MODELOS[id]
        }

        fun addMobModelo(mob: MobModelo) {
            MOBS_MODELOS.put(mob.getID(), mob)
        }

        fun getMobModelo(id: Int): MobModelo? {
            return MOBS_MODELOS[id]
        }

        fun removeMobModelo(id: Int) {
            MOBS_MODELOS.remove(id)
        }

        fun getMonturaModelo(id: Int): MonturaModelo? {
            return MONTURAS_MODELOS[id]
        }

        fun addMonturaModelo(montura: MonturaModelo) {
            MONTURAS_MODELOS.put(montura.getColorID(), montura)
        }

        val personajesEnLinea: CopyOnWriteArrayList<Personaje>
            get() = ONLINES

        fun objetoIniciarServer(id: Int, idObjModelo: Int, cant: Long, pos: Byte,
                                strStats: String?, idObvi: Int, precio: Int) {
            if (getObjetoModelo(idObjModelo) == null) {
                MainServidor.redactarLogServidorln(
                        "La id del objeto $id esta bug porque no tiene objModelo $idObjModelo")
                if (!MainServidor.PARAM_DESHABILITAR_SQL) {
                    GestorSQL.DELETE_OBJETO(id)
                }
                return
            }
            val obj = Objeto(id, idObjModelo, cant, pos, strStats, idObvi, precio)
            if (MainServidor.PARAM_RESETEAR_LUPEAR_OBJETOS_MAGUEADOS) {
                when (obj.getObjModelo().getTipo()) {
                    Constantes.OBJETO_TIPO_AMULETO, Constantes.OBJETO_TIPO_ANILLO, Constantes.OBJETO_TIPO_CINTURON, Constantes.OBJETO_TIPO_BOTAS, Constantes.OBJETO_TIPO_SOMBRERO, Constantes.OBJETO_TIPO_CAPA, Constantes.OBJETO_TIPO_BASTON, Constantes.OBJETO_TIPO_HACHA, Constantes.OBJETO_TIPO_PALA, Constantes.OBJETO_TIPO_ESPADA, Constantes.OBJETO_TIPO_ARCO, Constantes.OBJETO_TIPO_MARTILLO, Constantes.OBJETO_TIPO_GUADAÑA, Constantes.OBJETO_TIPO_DAGAS -> {
                        obj.convertirStringAStats(obj.getObjModelo().generarStatsModelo(CAPACIDAD_STATS.MAXIMO))
                        // obj._reseteado = true;
                        GestorSQL.SALVAR_OBJETO(obj)
                    }
                }
            }
            _OBJETOS.put(id, obj)
        }

        @Synchronized
        fun addObjeto(obj: Objeto, salvarSQL: Boolean) {
            try {
                if (obj.getID() === 0) {
                    obj.setID(sigIDObjeto())
                }
                _OBJETOS.put(obj.getID(), obj)
                if (SERVIDOR_ESTADO != Constantes.SERVIDOR_OFFLINE) {
                    if (salvarSQL || obj.getObjModelo().getTipo() === Constantes.OBJETO_TIPO_OBJEVIVO) {
                        GestorSQL.SALVAR_OBJETO(obj)
                    }
                } else if (MainServidor.PARAM_RESET_STATS_OBJETO) {
                    obj.convertirStringAStats(obj.getObjModelo().generarStatsModelo(CAPACIDAD_STATS.MAXIMO))
                    GestorSQL.SALVAR_OBJETO(obj)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun getObjeto(id: Int): Objeto {
            return _OBJETOS.get(id)
        }

        fun eliminarObjeto(id: Int) {
            if (id == 0) {
                return
            }
            GestorSQL.DELETE_OBJETO(id)
            _OBJETOS.remove(id)
        }

        private fun eliminarObjetosPorArray(objetos: ArrayList<Objeto>) {
            if (objetos.isEmpty()) {
                return
            }
            val str = StringBuilder()
            for (obj in objetos) {
                str.append((if (str.length() > 0) "," else "") + obj.getID())
                _OBJETOS.remove(obj.getID())
                obj = null
            }
            GestorSQL.DELETE_OBJETOS_LISTA(str.toString())
        }

        fun getMontura(id: Int): Montura {
            return _MONTURAS.get(id)
        }

        @Synchronized
        fun addMontura(montura: Montura, agregar: Boolean) {
            if (montura.getID() < SIG_ID_MONTURA) {
                SIG_ID_MONTURA = montura.getID()
            }
            _MONTURAS.put(montura.getID(), montura)
            if (agregar) {
                GestorSQL.REPLACE_MONTURA(montura, false)
            }
        }

        @Synchronized
        fun addPuestoMercadillo(mercadillo: Mercadillo) {
            MERCADILLOS.put(mercadillo.getID(), mercadillo)
        }

        fun getPuestoMercadillo(id: Int): Mercadillo? {
            return MERCADILLOS[id]
        }

        fun getPuestoPorMapa(mapa: Int): Mercadillo? {
            for (merca in MERCADILLOS.values()) {
                if (merca.getMapas().contains(mapa)) {
                    return merca
                }
            }
            return null
        }

        fun cantPuestosMercadillos(): Int {
            return MERCADILLOS.size()
        }

        fun getAnimacion(animacionId: Int): Animacion? {
            return ANIMACIONES[animacionId]
        }

        fun addAnimacion(animacion: Animacion) {
            ANIMACIONES.put(animacion.getID(), animacion)
        }

        fun addObjetoTrueque(objetoID: Int, necesita: String?, prioridad: Int, npcs: String?) {
            val objT = ObjetoTrueque(objetoID, necesita, prioridad, npcs)
            if (!objT.getNecesita().isEmpty()) {
                OBJETOS_TRUEQUE.add(objT)
            }
        }

        val objInteractivos: ArrayList<ObjetoInteractivoModelo>
            get() = OBJETOS_INTERACTIVOS_MODELOS

        fun borrarOtroInteractivo(gfxID: Int, mapaID: Short, celdaID: Short, accion: Int, conAccion: Boolean) {
            for (oi in OTROS_INTERACTIVOS) {
                if (gfxID == oi.getGfxID() && mapaID == oi.getMapaID() && celdaID == oi.getCeldaID() && (!conAccion || accion == oi.getAccionID())) {
                    OTROS_INTERACTIVOS.remove(oi)
                    GestorSQL.DELETE_OTRO_INTERACTIVO(gfxID, mapaID, celdaID, oi.getAccionID())
                }
            }
        }

        fun addOtroInteractivo(otro: OtroInteractivo?) {
            OTROS_INTERACTIVOS.add(otro)
        }

        fun addMascotaModelo(mascota: MascotaModelo) {
            MASCOTAS_MODELOS.put(mascota.getID(), mascota)
        }

        fun getMascotaModelo(id: Int): MascotaModelo? {
            return MASCOTAS_MODELOS[id]
        }

        fun addEspecialidad(especialidad: Especialidad) {
            ESPECIALIDADES.put(especialidad.getID(), especialidad)
        }

        fun getEspecialidad(orden: Int, nivel: Int): Especialidad? {
            var esp: Especialidad? = null
            for (e in ESPECIALIDADES.values()) {
                if (e.getOrden() !== orden) {
                    continue
                }
                if (esp == null || e.getNivel() <= nivel && e.getNivel() > esp.getNivel()) {
                    esp = e
                }
            }
            return esp
        }

        fun addDonModelo(id: Int, stat: Int) {
            DONES_MODELOS.put(id, stat)
        }

        fun getDonStat(id: Int): Int {
            return DONES_MODELOS[id]
        }

        fun addCasa(casa: Casa) {
            CASAS.put(casa.getID(), casa)
        }

        val casas: Map<Any, Any>
            get() = CASAS

        fun getCasa(id: Int): Casa? {
            return CASAS[id]
        }

        fun addCercado(cercado: Cercado) {
            CERCADOS.put(cercado.getMapa().getID(), cercado)
        }

        fun getCercadoPorMapa(mapa: Int): Cercado? {
            return CERCADOS[mapa]
        }

        fun getPrisma(id: Int): Prisma {
            return _PRISMAS.get(id)
        }

        fun addPrisma(prisma: Prisma) {
            if (prisma.getMapa().getPrisma() != null) {
                _PRISMAS.remove(prisma.getID())
                GestorSQL.DELETE_PRISMA(prisma.getID())
                return
            }
            prisma.getMapa().setPrisma(prisma)
            if (prisma.getArea() != null) {
                prisma.getArea().setPrisma(prisma)
            }
            if (prisma.getSubArea() != null) {
                prisma.getSubArea().setPrisma(prisma)
            }
            if (prisma.getID() < SIG_ID_PRISMA) {
                SIG_ID_PRISMA = prisma.getID()
            }
            _PRISMAS.put(prisma.getID(), prisma)
        }

        fun eliminarPrisma(prisma: Prisma) {
            prisma.getMapa().setPrisma(null)
            if (prisma.getArea() != null) {
                prisma.getArea().setPrisma(null)
            }
            if (prisma.getSubArea() != null) {
                prisma.getSubArea().setPrisma(null)
            }
            _PRISMAS.remove(prisma.getID())
            GestorSQL.DELETE_PRISMA(prisma.getID())
        }

        val prismas: Collection<Any>
            get() = _PRISMAS.values()

        fun getRecaudador(id: Int): Recaudador {
            return _RECAUDADORES.get(id)
        }

        fun addRecaudador(recaudador: Recaudador) {
            if (recaudador.getMapa().getRecaudador() != null) {
                recaudador.getGremio().delRecaudador(recaudador)
                GestorSQL.DELETE_RECAUDADOR(recaudador.getID())
                return
            }
            recaudador.getMapa().setRecaudador(recaudador)
            if (recaudador.getID() < SIG_ID_RECAUDADOR) {
                SIG_ID_RECAUDADOR = recaudador.getID()
            }
            _RECAUDADORES.put(recaudador.getID(), recaudador)
        }

        fun eliminarRecaudador(recaudador: Recaudador) {
            recaudador.getMapa().setRecaudador(null)
            recaudador.getGremio().delRecaudador(recaudador)
            _RECAUDADORES.remove(recaudador.getID())
            GestorSQL.DELETE_RECAUDADOR(recaudador.getID())
        }

        fun puedePonerRecauEnZona(subAreaID: Int, gremioID: Int): Boolean {
            var i = 0
            for (recau in _RECAUDADORES.values()) {
                if (recau.getGremio().getID() === gremioID && recau.getMapa().getSubArea().getID() === subAreaID) {
                    i++
                }
            }
            return i < MainServidor.MAX_RECAUDADORES_POR_ZONA
        }

        fun addCofre(cofre: Cofre) {
            COFRES.put(cofre.getID(), cofre)
        }

        fun getCofre(id: Int): Cofre? {
            return COFRES[id]
        }

        fun addRankingKoliseo(rank: RankingKoliseo) {
            _RANKINGS_KOLISEO.put(rank.getID(), rank)
        }

        fun delRankingKoliseo(id: Int) {
            GestorSQL.DELETE_RANKING_PVP(id)
            _RANKINGS_KOLISEO.remove(id)
        }

        fun getRankingKoliseo(id: Int): RankingKoliseo {
            return _RANKINGS_KOLISEO.get(id)
        }

        fun addRankingPVP(rank: RankingPVP) {
            _RANKINGS_PVP.put(rank.getID(), rank)
        }

        fun delRankingPVP(id: Int) {
            GestorSQL.DELETE_RANKING_PVP(id)
            _RANKINGS_PVP.remove(id)
        }

        fun getRankingPVP(id: Int): RankingPVP {
            return _RANKINGS_PVP.get(id)
        }

        fun getBalanceMundo(perso: Personaje): Float {
            var cant = 0
            for (subarea in SUB_AREAS.values()) {
                if (subarea.getAlineacion() === perso.getAlineacion()) {
                    cant++
                }
            }
            return if (cant == 0 || SUB_AREAS.isEmpty()) {
                0
            } else Math.rint(1000f * cant / SUB_AREAS.size()) as Float / 10f
        }

        fun getBalanceArea(perso: Personaje): Float {
            var cant = 0
            var area: Area? = null
            try {
                area = perso.getMapa().getSubArea().getArea()
            } catch (e: Exception) {
            }
            if (area == null) {
                return 0
            }
            for (subarea in SUB_AREAS.values()) {
                if (subarea.getArea() === area && subarea.getAlineacion() === perso.getAlineacion()) {
                    cant++
                }
            }
            return if (cant == 0 || area.getSubAreas().isEmpty()) {
                0
            } else Math.rint(1000f * cant / area.getSubAreas().size()) as Float / 10f
        }

        fun getBonusAlinExp(perso: Personaje): Float {
            val bonus = (Math.rint(Math.sqrt(MainServidor.RATE_CONQUISTA_EXPERIENCIA) * 100) / 100f) as Float
            return perso.getGradoAlineacion() / 2.5f + bonus
        }

        fun getBonusAlinRecolecta(perso: Personaje): Float {
            val bonus = (Math.rint(Math.sqrt(MainServidor.RATE_CONQUISTA_RECOLECTA) * 100) / 100f) as Float
            return perso.getGradoAlineacion() / 2.5f + bonus
        }

        fun getBonusAlinDrop(perso: Personaje): Float {
            val bonus = (Math.rint(Math.sqrt(MainServidor.RATE_CONQUISTA_DROP) * 100) / 100f) as Float
            return perso.getGradoAlineacion() / 2.5f + bonus
        }

        fun prismasGeoposicion(alineacion: Int): String {
            val str = StringBuilder()
            if (alineacion == Constantes.ALINEACION_BONTARIANO) {
                str.append(SubArea.BONTAS)
            } else if (alineacion == Constantes.ALINEACION_BRAKMARIANO) {
                str.append(SubArea.BRAKMARS)
            }
            str.append("|" + SUB_AREAS.size().toString() + "|" + (SUB_AREAS.size() - (SubArea.BONTAS + SubArea.BRAKMARS)).toString() + "|")
            var primero = false
            for (subArea in SUB_AREAS.values()) {
                if (!subArea.esConquistable()) {
                    continue
                }
                if (primero) {
                    str.append(";")
                }
                str.append(subArea.getID().toString() + ",")
                str.append(subArea.getAlineacion().toString() + ",")
                str.append((if (subArea.getPrisma() == null) 0 else if (subArea.getPrisma().getPelea() == null) 0 else 1).toString() + ",") // pelea
                str.append((if (subArea.getPrisma() == null) 0 else subArea.getPrisma().getMapa().getID()).toString() + ",")
                str.append("1") // atacable
                primero = true
            }
            str.append("|")
            if (alineacion == Constantes.ALINEACION_BONTARIANO) {
                str.append(Area.BONTAS)
            } else if (alineacion == Constantes.ALINEACION_BRAKMARIANO) {
                str.append(Area.BRAKMARS)
            }
            str.append("|" + AREAS.size().toString() + "|")
            primero = false
            for (area in AREAS.values()) {
                if (primero) {
                    str.append(";")
                }
                str.append(area.getID().toString() + ",")
                str.append(area.getAlineacion().toString() + ",") // alineacion
                str.append("1,") // door
                str.append(if (area.getPrisma() == null) 0 else 1) // tiene prisma
                primero = true
            }
            return str.toString()
        }

        fun getEncarnacionModelo(id: Int): EncarnacionModelo? {
            return ENCARNACIONES_MODELOS[id]
        }

        fun addTutorial(tutorial: Tutorial) {
            TUTORIALES.put(tutorial.getID(), tutorial)
        }

        fun getTutorial(id: Int): Tutorial? {
            return TUTORIALES[id]
        }

        fun getDuoPorIDPrimero(objetos: ArrayList<Duo<Integer?, Integer?>?>,
                               id: Int): Duo<Integer, Integer>? {
            for (duo in objetos) {
                if (duo._primero == id) {
                    return duo
                }
            }
            return null
        }

        fun getDuoPorIDPrimero1(objetos: ArrayList<Duo<Integer?, Long?>?>, id: Int): Duo<Integer, Long>? {
            for (duo in objetos) {
                if (duo._primero == id) {
                    return duo
                }
            }
            return null
        }

        fun stringServicios(perso: Personaje): String {
            val abonado: Boolean = perso.getCuenta().esAbonado()
            val str = StringBuilder()
            for (s in SERVICIOS.values()) {
                if (s.string(abonado).isEmpty()) {
                    continue
                }
                if (str.length() > 0) {
                    str.append("|")
                }
                str.append(s.string(abonado))
            }
            return str.toString()
        }

        fun getMascotaPorFantasma(fantasma: Int): Int {
            for (masc in MASCOTAS_MODELOS.values()) {
                if (masc.getFantasma() === fantasma) {
                    return masc.getID()
                }
            }
            return 0
        }

        fun delKoliseo(perso: Personaje) {
            if (INSCRITOS_KOLISEO_RANGO.containsKey(perso.getRangoKoli())) {
                if (INSCRITOS_KOLISEO_RANGO.get(perso.getRangoKoli()).contains(perso)) {
                    INSCRITOS_KOLISEO_RANGO.get(perso.getRangoKoli()).remove(perso)
                }
            }
            if (INSCRITOS_KOLISEO_RANGO_VS1.containsKey(perso.getRangoKoli())) {
                if (INSCRITOS_KOLISEO_RANGO_VS1.get(perso.getRangoKoli()).contains(perso)) {
                    INSCRITOS_KOLISEO_RANGO_VS1.get(perso.getRangoKoli()).remove(perso)
                }
            }
            if (INSCRITOS_KOLISEO_RANGO_VS2.containsKey(perso.getRangoKoli())) {
                if (INSCRITOS_KOLISEO_RANGO_VS2.get(perso.getRangoKoli()).contains(perso)) {
                    INSCRITOS_KOLISEO_RANGO_VS2.get(perso.getRangoKoli()).remove(perso)
                }
            }
        }

        fun addKoliseo(perso: Personaje) {
            if (!INSCRITOS_KOLISEO_RANGO.containsKey(perso.getRangoKoli())) {
                INSCRITOS_KOLISEO_RANGO.put(perso.getRangoKoli(), CopyOnWriteArrayList<Personaje>())
            }
            if (!MainServidor.PARAM_PERMITIR_MULTICUENTA_PELEA_KOLISEO) {
                for (p in INSCRITOS_KOLISEO_RANGO.get(perso.getRangoKoli())) {
                    if (p.getCuenta().getUltimaIP().equalsIgnoreCase(perso.getCuenta().getUltimaIP())) {
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso,
                                "<b>Koliseo:</b> Ya tiene un personaje registrado con tu IP")
                        return
                    }
                }
            }
            if (MainServidor.CLASES_NO_PERMITIDAS_KOLISEO.contains(perso.getClaseID(true))) {
                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso,
                        "<b>Koliseo:</b> Esta clase no esta permitida para koliseo")
                return
            }
            INSCRITOS_KOLISEO_RANGO.get(perso.getRangoKoli()).add(perso)
            if (_RANKINGS_KOLISEO.get(perso.getID()) == null) {
                val rank = RankingKoliseo(perso.getID(), 0, 0)
                _RANKINGS_KOLISEO.put(perso.getID(), rank)
                GestorSQL.REPLACE_RANKING_KOLISEO(rank)
            }
            val mienbros: Int = MainServidor.CANTIDAD_MIEMBROS_EQUIPO_KOLISEO * 2
            if (INSCRITOS_KOLISEO_RANGO.get(perso.getRangoKoli()).size() < mienbros) {
                if (MainServidor.PARAM_KOLI_MISMO_RESET) {
                    GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_TODOS("?", "Koliseo",
                            "Faltan " + (mienbros - INSCRITOS_KOLISEO_RANGO.get(perso.getRangoKoli()).size())
                                    .toString() + " personas para iniciar un koli en rango: "
                                    + (perso.getRangoKoliSinReset() * MainServidor.RANGO_NIVEL_KOLISEO
                                    - MainServidor.RANGO_NIVEL_KOLISEO)
                                    .toString() + " - " + (perso.getRangoKoli() * MainServidor.RANGO_NIVEL_KOLISEO).toString() + " y reset: "
                                    + perso.getResets())
                } else {
                    GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_TODOS("?", "Koliseo",
                            "Faltan " + (mienbros - INSCRITOS_KOLISEO_RANGO.get(perso.getRangoKoli()).size())
                                    .toString() + " personas para iniciar un koli en rango: "
                                    + (perso.getRangoKoli() * MainServidor.RANGO_NIVEL_KOLISEO
                                    - MainServidor.RANGO_NIVEL_KOLISEO)
                                    .toString() + " - " + perso.getRangoKoli() * MainServidor.RANGO_NIVEL_KOLISEO)
                }
            } else {
                // iniciarKoliseo(perso.getRangoKoli());
            }
        }

        fun addKoliseo1(perso: Personaje) {
            if (MainServidor.CANTIDAD_MIEMBROS_EQUIPO_KOLISEO_VS1 === 0) {
                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso,
                        "<b>Koliseo:</b> Hable con su administrador del server para activar este koliseo")
                return
            }
            if (!INSCRITOS_KOLISEO_RANGO_VS1.containsKey(perso.getRangoKoli())) {
                INSCRITOS_KOLISEO_RANGO_VS1.put(perso.getRangoKoli(), CopyOnWriteArrayList<Personaje>())
            }
            if (!MainServidor.PARAM_PERMITIR_MULTICUENTA_PELEA_KOLISEO) {
                for (p in INSCRITOS_KOLISEO_RANGO_VS1.get(perso.getRangoKoli())) {
                    if (p.getCuenta().getUltimaIP().equalsIgnoreCase(perso.getCuenta().getUltimaIP())) {
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso,
                                "<b>Koliseo:</b> Ya tiene un personaje registrado con tu IP")
                        return
                    }
                }
            }
            if (MainServidor.CLASES_NO_PERMITIDAS_KOLISEO.contains(perso.getClaseID(true))) {
                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso,
                        "<b>Koliseo:</b> Esta clase no esta permitida para koliseo")
                return
            }
            INSCRITOS_KOLISEO_RANGO_VS1.get(perso.getRangoKoli()).add(perso)
            if (_RANKINGS_KOLISEO.get(perso.getID()) == null) {
                val rank = RankingKoliseo(perso.getID(), 0, 0)
                _RANKINGS_KOLISEO.put(perso.getID(), rank)
                GestorSQL.REPLACE_RANKING_KOLISEO(rank)
            }
            val mienbros: Int = MainServidor.CANTIDAD_MIEMBROS_EQUIPO_KOLISEO_VS1 * 2
            if (INSCRITOS_KOLISEO_RANGO_VS1.get(perso.getRangoKoli()).size() < mienbros) {
                if (MainServidor.PARAM_KOLI_MISMO_RESET) {
                    GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_TODOS("?",
                            "Koliseo(" + MainServidor.CANTIDAD_MIEMBROS_EQUIPO_KOLISEO_VS1.toString() + " VS "
                                    + MainServidor.CANTIDAD_MIEMBROS_EQUIPO_KOLISEO_VS1.toString() + ")",
                            "Faltan " + (mienbros - INSCRITOS_KOLISEO_RANGO_VS1.get(perso.getRangoKoli()).size())
                                    .toString() + " personas para iniciar un koli en rango: "
                                    + (perso.getRangoKoliSinReset() * MainServidor.RANGO_NIVEL_KOLISEO
                                    - MainServidor.RANGO_NIVEL_KOLISEO)
                                    .toString() + " - " + (perso.getRangoKoli() * MainServidor.RANGO_NIVEL_KOLISEO).toString() + " y reset: "
                                    + perso.getResets())
                } else {
                    GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_TODOS("?",
                            "Koliseo(" + MainServidor.CANTIDAD_MIEMBROS_EQUIPO_KOLISEO_VS1.toString() + " VS "
                                    + MainServidor.CANTIDAD_MIEMBROS_EQUIPO_KOLISEO_VS1.toString() + ")",
                            "Faltan " + (mienbros - INSCRITOS_KOLISEO_RANGO_VS1.get(perso.getRangoKoli()).size())
                                    .toString() + " personas para iniciar un koli en rango: "
                                    + (perso.getRangoKoli() * MainServidor.RANGO_NIVEL_KOLISEO
                                    - MainServidor.RANGO_NIVEL_KOLISEO)
                                    .toString() + " - " + perso.getRangoKoli() * MainServidor.RANGO_NIVEL_KOLISEO)
                }
            } else {
                // iniciarKoliseo1(perso.getRangoKoli());
            }
        }

        fun addKoliseo2(perso: Personaje) {
            if (MainServidor.CANTIDAD_MIEMBROS_EQUIPO_KOLISEO_VS2 === 0) {
                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso,
                        "<b>Koliseo:</b> Hable con su administrador del server para activar este koliseo")
                return
            }
            if (!INSCRITOS_KOLISEO_RANGO_VS2.containsKey(perso.getRangoKoli())) {
                INSCRITOS_KOLISEO_RANGO_VS2.put(perso.getRangoKoli(), CopyOnWriteArrayList<Personaje>())
            }
            if (!MainServidor.PARAM_PERMITIR_MULTICUENTA_PELEA_KOLISEO) {
                for (p in INSCRITOS_KOLISEO_RANGO_VS2.get(perso.getRangoKoli())) {
                    if (p.getCuenta().getUltimaIP().equalsIgnoreCase(perso.getCuenta().getUltimaIP())) {
                        GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso,
                                "<b>Koliseo:</b> Ya tiene un personaje registrado con tu IP")
                        return
                    }
                }
            }
            if (MainServidor.CLASES_NO_PERMITIDAS_KOLISEO.contains(perso.getClaseID(true))) {
                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso,
                        "<b>Koliseo:</b> Esta clase no esta permitida para koliseo")
                return
            }
            INSCRITOS_KOLISEO_RANGO_VS2.get(perso.getRangoKoli()).add(perso)
            if (_RANKINGS_KOLISEO.get(perso.getID()) == null) {
                val rank = RankingKoliseo(perso.getID(), 0, 0)
                _RANKINGS_KOLISEO.put(perso.getID(), rank)
                GestorSQL.REPLACE_RANKING_KOLISEO(rank)
            }
            val mienbros: Int = MainServidor.CANTIDAD_MIEMBROS_EQUIPO_KOLISEO_VS1 * 2
            if (INSCRITOS_KOLISEO_RANGO_VS2.get(perso.getRangoKoli()).size() < mienbros) {
                if (MainServidor.PARAM_KOLI_MISMO_RESET) {
                    GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_TODOS("?",
                            "Koliseo(" + MainServidor.CANTIDAD_MIEMBROS_EQUIPO_KOLISEO_VS2.toString() + " VS "
                                    + MainServidor.CANTIDAD_MIEMBROS_EQUIPO_KOLISEO_VS2.toString() + ")",
                            "Faltan " + (mienbros - INSCRITOS_KOLISEO_RANGO_VS2.get(perso.getRangoKoli()).size())
                                    .toString() + " personas para iniciar un koli en rango: "
                                    + (perso.getRangoKoliSinReset() * MainServidor.RANGO_NIVEL_KOLISEO
                                    - MainServidor.RANGO_NIVEL_KOLISEO)
                                    .toString() + " - " + (perso.getRangoKoli() * MainServidor.RANGO_NIVEL_KOLISEO).toString() + " y reset: "
                                    + perso.getResets())
                } else {
                    GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_TODOS("?",
                            "Koliseo(" + MainServidor.CANTIDAD_MIEMBROS_EQUIPO_KOLISEO_VS2.toString() + " VS "
                                    + MainServidor.CANTIDAD_MIEMBROS_EQUIPO_KOLISEO_VS2.toString() + ")",
                            "Faltan " + (mienbros - INSCRITOS_KOLISEO_RANGO_VS2.get(perso.getRangoKoli()).size())
                                    .toString() + " personas para iniciar un koli en rango: "
                                    + (perso.getRangoKoli() * MainServidor.RANGO_NIVEL_KOLISEO
                                    - MainServidor.RANGO_NIVEL_KOLISEO)
                                    .toString() + " - " + perso.getRangoKoli() * MainServidor.RANGO_NIVEL_KOLISEO)
                }
            } else {
                // iniciarKoliseo2(perso.getRangoKoli());
            }
        }

        fun estaEnKoliseo(perso: Personaje): Boolean {
            var esta = false
            if (INSCRITOS_KOLISEO_RANGO.containsKey(perso.getRangoKoli())) {
                esta = INSCRITOS_KOLISEO_RANGO.get(perso.getRangoKoli()).contains(perso)
            }
            if (!esta && INSCRITOS_KOLISEO_RANGO_VS1.containsKey(perso.getRangoKoli())) {
                esta = INSCRITOS_KOLISEO_RANGO_VS1.get(perso.getRangoKoli()).contains(perso)
            }
            if (!esta && INSCRITOS_KOLISEO_RANGO_VS2.containsKey(perso.getRangoKoli())) {
                esta = INSCRITOS_KOLISEO_RANGO_VS2.get(perso.getRangoKoli()).contains(perso)
            }
            return esta
        }

        fun estaEnKoliseo0(perso: Personaje): Boolean {
            var esta = false
            if (INSCRITOS_KOLISEO_RANGO.containsKey(perso.getRangoKoli())) {
                esta = INSCRITOS_KOLISEO_RANGO.get(perso.getRangoKoli()).contains(perso)
            }
            return esta
        }

        fun cantKoliseo(perso: Personaje): Int {
            return if (!INSCRITOS_KOLISEO_RANGO.containsKey(perso.getRangoKoli())) {
                0
            } else INSCRITOS_KOLISEO_RANGO.get(perso.getRangoKoli()).size()
        }

        fun cantKoliseo2(perso: Personaje): Int {
            return if (!INSCRITOS_KOLISEO_RANGO_VS1.containsKey(perso.getRangoKoli())) {
                0
            } else INSCRITOS_KOLISEO_RANGO_VS1.get(perso.getRangoKoli()).size()
        }

        fun cantKoliseo3(perso: Personaje): Int {
            return if (!INSCRITOS_KOLISEO_RANGO_VS2.containsKey(perso.getRangoKoli())) {
                0
            } else INSCRITOS_KOLISEO_RANGO_VS2.get(perso.getRangoKoli()).size()
        }

        val inscritosKoliseo: ConcurrentHashMap<Integer, CopyOnWriteArrayList<Personaje>>
            get() = INSCRITOS_KOLISEO_RANGO

        fun iniciarKoliseo() {
            try {
                val mapas: ArrayList<Mapa> = ArrayList<Mapa>()
                for (s in MainServidor.MAPAS_KOLISEO.split(",")) {
                    try {
                        mapas.add(getMapa(Short.parseShort(s)))
                    } catch (e: Exception) {
                    }
                }
                if (mapas.isEmpty()) {
                    SEGUNDOS_INICIO_KOLISEO = MainServidor.SEGUNDOS_INICIAR_KOLISEO
                    GestorSalida.ENVIAR_Im_INFORMACION_KOLISEO("1KOLISEO_FALTA_MAPAS")
                    return
                }
                for (_INSCRITOS_KOLISEO in INSCRITOS_KOLISEO_RANGO.values()) {
                    val listos: ArrayList<Personaje> = ArrayList<Personaje>()
                    var rango = -1
                    for (p in _INSCRITOS_KOLISEO) {
                        rango = p.getRangoKoli()
                        if (p.puedeIrKoliseo()) {
                            listos.add(p)
                        } else {
                            if (p.getGrupoKoliseo() != null) {
                                p.getGrupoKoliseo().dejarGrupo(p)
                            }
                        }
                    }
                    if (listos.size() < MainServidor.CANTIDAD_MIEMBROS_EQUIPO_KOLISEO * 2) {
                        SEGUNDOS_INICIO_KOLISEO = MainServidor.SEGUNDOS_INICIAR_KOLISEO
                        GestorSalida.ENVIAR_Im_INFORMACION_KOLISEO("1KOLISEO_FALTA_INSCRITOS", rango)
                        continue
                    }
                    _INSCRITOS_KOLISEO.clear()
                    val ordenados: ArrayList<Personaje> = ArrayList<Personaje>()
                    ordenados.addAll(listos)
                    val grupos: ArrayList<GrupoKoliseo> = ArrayList<GrupoKoliseo>()
                    var i = 0
                    for (p in ordenados) {
                        if (!p.puedeIrKoliseo()) continue
                        if (p.getGrupoKoliseo() != null) {
                            if (!grupos.contains(p.getGrupoKoliseo())) {
                                grupos.add(p.getGrupoKoliseo())
                            }
                        } else {
                            var salir = false
                            while (!salir) {
                                try {
                                    if (grupos.get(i).addPersonaje(p)) {
                                        p.setGrupoKoliseo(grupos.get(i))
                                        salir = true
                                    } else {
                                        i++
                                    }
                                } catch (e: Exception) {
                                    val g = GrupoKoliseo(p)
                                    p.setGrupoKoliseo(g)
                                    grupos.add(g)
                                    salir = true
                                }
                            }
                        }
                    }
                    val combate: ArrayList<GrupoKoliseo> = ArrayList<GrupoKoliseo>()
                    if (combate.size() === grupos.size()) {
                        continue
                    }
                    combate.addAll(grupos)
                    for (grupoA in combate) {
                        if (grupos.contains(grupoA)) {
                            for (grupoB in combate) {
                                if (grupoA === grupoB || grupoA.getCantPjs() !== grupoB.getCantPjs() || grupoA.contieneIPOtroGrupo(grupoB)) {
                                    continue
                                }
                                var team1: String? = null
                                val team2: String? = null
                                for (perso in grupoA.getMiembros()) {
                                    perso.fullPDV()
                                    if (team1 == null) {
                                        team1 = perso.getNombre()
                                    } else {
                                        team1 += " <b>,</b> " + perso.getNombre()
                                    }
                                }
                                val mapa: Mapa = mapas.get(Formulas.getRandomInt(0, mapas.size() - 1))
                                for (perso1 in grupoA.getMiembros()) {
                                    if (perso1 != null) perso1.teleport(mapa.getID(), 399.toShort())
                                }
                                for (perso2 in grupoB.getMiembros()) {
                                    if (perso2 != null) perso2.teleport(mapa.getID(), 399.toShort())
                                }
                                if (mapa.iniciarPeleaKoliseo(grupoA, grupoB)) {
                                    grupos.remove(grupoA)
                                    grupos.remove(grupoB)
                                    GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_TODOS("?", "Koliseo",
                                            team1.toString() + " <b>VS</b> " + team2)
                                }
                            }
                        }
                    }
                    for (k in grupos) {
                        for (p in k.getMiembros()) {
                            GestorSalida.ENVIAR_Im_INFORMACION(p, "1KOLISEO_NO_ELEGIDO")
                            INSCRITOS_KOLISEO_RANGO.get(p.getRangoKoli()).add(p)
                        }
                        k.limpiarGrupo()
                    }
                }
            } catch (e: Exception) {
                MainServidor.redactarLogServidorln("null en koliseo" + e.getMessage())
            }
            SEGUNDOS_INICIO_KOLISEO = MainServidor.SEGUNDOS_INICIAR_KOLISEO
        }

        fun iniciarKoliseo1() {
            try {
                val mapas: ArrayList<Mapa> = ArrayList<Mapa>()
                for (s in MainServidor.MAPAS_KOLISEO.split(",")) {
                    try {
                        mapas.add(getMapa(Short.parseShort(s)))
                    } catch (e: Exception) {
                    }
                }
                if (mapas.isEmpty()) {
                    SEGUNDOS_INICIO_KOLISEO = MainServidor.SEGUNDOS_INICIAR_KOLISEO
                    GestorSalida.ENVIAR_Im_INFORMACION_KOLISEO("1KOLISEO_FALTA_MAPAS")
                    return
                }
                for (_INSCRITOS_KOLISEO in INSCRITOS_KOLISEO_RANGO_VS1.values()) {
                    val listos: ArrayList<Personaje> = ArrayList<Personaje>()
                    var rango = -1
                    for (p in _INSCRITOS_KOLISEO) {
                        rango = p.getRangoKoli()
                        if (p.puedeIrKoliseo()) {
                            listos.add(p)
                        } else {
                            if (p.getGrupoKoliseo() != null) {
                                p.getGrupoKoliseo().dejarGrupo(p)
                            }
                        }
                    }
                    if (listos.size() < MainServidor.CANTIDAD_MIEMBROS_EQUIPO_KOLISEO_VS1 * 2) {
                        SEGUNDOS_INICIO_KOLISEO = MainServidor.SEGUNDOS_INICIAR_KOLISEO
                        GestorSalida.ENVIAR_Im_INFORMACION_KOLISEO("1KOLISEO_FALTA_INSCRITOS", rango)
                        continue
                    }
                    _INSCRITOS_KOLISEO.clear()
                    val ordenados: ArrayList<Personaje> = ArrayList<Personaje>()
                    ordenados.addAll(listos)
                    val grupos: ArrayList<GrupoKoliseo> = ArrayList<GrupoKoliseo>()
                    var i = 0
                    for (p in ordenados) {
                        if (!p.puedeIrKoliseo()) continue
                        if (p.getGrupoKoliseo() != null) {
                            if (!grupos.contains(p.getGrupoKoliseo())) {
                                grupos.add(p.getGrupoKoliseo())
                            }
                        } else {
                            var salir = false
                            while (!salir) {
                                try {
                                    if (grupos.get(i).addPersonaje1(p)) {
                                        p.setGrupoKoliseo(grupos.get(i))
                                        salir = true
                                    } else {
                                        i++
                                    }
                                } catch (e: Exception) {
                                    val g = GrupoKoliseo(p)
                                    p.setGrupoKoliseo(g)
                                    grupos.add(g)
                                    salir = true
                                }
                            }
                        }
                    }
                    val combate: ArrayList<GrupoKoliseo> = ArrayList<GrupoKoliseo>()
                    if (combate.size() === grupos.size()) {
                        continue
                    }
                    combate.addAll(grupos)
                    for (grupoA in combate) {
                        if (grupos.contains(grupoA)) {
                            for (grupoB in combate) {
                                if (grupoA === grupoB || grupoA.getCantPjs() !== grupoB.getCantPjs() || grupoA.contieneIPOtroGrupo(grupoB)) {
                                    continue
                                }
                                for (perso in grupoA.getMiembros()) {
                                    perso.fullPDV()
                                }
                                for (perso in grupoB.getMiembros()) {
                                    perso.fullPDV()
                                }
                                val mapa: Mapa = mapas.get(Formulas.getRandomInt(0, mapas.size() - 1))
                                for (perso1 in grupoA.getMiembros()) {
                                    if (perso1 != null) perso1.teleport(mapa.getID(), 399.toShort())
                                }
                                for (perso2 in grupoB.getMiembros()) {
                                    if (perso2 != null) perso2.teleport(mapa.getID(), 399.toShort())
                                }
                                if (mapa.iniciarPeleaKoliseo(grupoA, grupoB)) {
                                    grupos.remove(grupoA)
                                    grupos.remove(grupoB)
                                }
                            }
                        }
                    }
                    for (k in grupos) {
                        for (p in k.getMiembros()) {
                            GestorSalida.ENVIAR_Im_INFORMACION(p, "1KOLISEO_NO_ELEGIDO")
                            INSCRITOS_KOLISEO_RANGO_VS1.get(p.getRangoKoli()).add(p)
                        }
                        k.limpiarGrupo()
                    }
                }
            } catch (e: Exception) {
                MainServidor.redactarLogServidorln("null en koliseo2 " + e.getMessage())
            }
            SEGUNDOS_INICIO_KOLISEO = MainServidor.SEGUNDOS_INICIAR_KOLISEO
        }

        fun iniciarKoliseo2() {
            try {
                val mapas: ArrayList<Mapa> = ArrayList<Mapa>()
                for (s in MainServidor.MAPAS_KOLISEO.split(",")) {
                    try {
                        mapas.add(getMapa(Short.parseShort(s)))
                    } catch (e: Exception) {
                    }
                }
                if (mapas.isEmpty()) {
                    SEGUNDOS_INICIO_KOLISEO = MainServidor.SEGUNDOS_INICIAR_KOLISEO
                    GestorSalida.ENVIAR_Im_INFORMACION_KOLISEO("1KOLISEO_FALTA_MAPAS")
                    return
                }
                for (_INSCRITOS_KOLISEO in INSCRITOS_KOLISEO_RANGO_VS2.values()) {
                    val listos: ArrayList<Personaje> = ArrayList<Personaje>()
                    var rango = -1
                    for (p in _INSCRITOS_KOLISEO) {
                        rango = p.getRangoKoli()
                        if (p.puedeIrKoliseo()) {
                            listos.add(p)
                        } else {
                            if (p.getGrupoKoliseo() != null) {
                                p.getGrupoKoliseo().dejarGrupo(p)
                            }
                        }
                    }
                    if (listos.size() < MainServidor.CANTIDAD_MIEMBROS_EQUIPO_KOLISEO_VS2 * 2) {
                        SEGUNDOS_INICIO_KOLISEO = MainServidor.SEGUNDOS_INICIAR_KOLISEO
                        GestorSalida.ENVIAR_Im_INFORMACION_KOLISEO("1KOLISEO_FALTA_INSCRITOS", rango)
                        continue
                    }
                    _INSCRITOS_KOLISEO.clear()
                    val ordenados: ArrayList<Personaje> = ArrayList<Personaje>()
                    ordenados.addAll(listos)
                    val grupos: ArrayList<GrupoKoliseo> = ArrayList<GrupoKoliseo>()
                    var i = 0
                    for (p in ordenados) {
                        if (!p.puedeIrKoliseo()) continue
                        if (p.getGrupoKoliseo() != null) {
                            if (!grupos.contains(p.getGrupoKoliseo())) {
                                grupos.add(p.getGrupoKoliseo())
                            }
                        } else {
                            var salir = false
                            while (!salir) {
                                try {
                                    if (grupos.get(i).addPersonaje2(p)) {
                                        p.setGrupoKoliseo(grupos.get(i))
                                        salir = true
                                    } else {
                                        i++
                                    }
                                } catch (e: Exception) {
                                    val g = GrupoKoliseo(p)
                                    p.setGrupoKoliseo(g)
                                    grupos.add(g)
                                    salir = true
                                }
                            }
                        }
                    }
                    val combate: ArrayList<GrupoKoliseo> = ArrayList<GrupoKoliseo>()
                    if (combate.size() === grupos.size()) {
                        continue
                    }
                    combate.addAll(grupos)
                    for (grupoA in combate) {
                        if (grupos.contains(grupoA)) {
                            for (grupoB in combate) {
                                if (grupoA === grupoB || grupoA.getCantPjs() !== grupoB.getCantPjs() || grupoA.contieneIPOtroGrupo(grupoB)) {
                                    continue
                                }
                                for (perso in grupoA.getMiembros()) {
                                    perso.fullPDV()
                                }
                                for (perso in grupoB.getMiembros()) {
                                    perso.fullPDV()
                                }
                                val mapa: Mapa = mapas.get(Formulas.getRandomInt(0, mapas.size() - 1))
                                for (perso1 in grupoA.getMiembros()) {
                                    if (perso1 != null) perso1.teleport(mapa.getID(), 399.toShort())
                                }
                                for (perso2 in grupoB.getMiembros()) {
                                    if (perso2 != null) perso2.teleport(mapa.getID(), 399.toShort())
                                }
                                if (mapa.iniciarPeleaKoliseo(grupoA, grupoB)) {
                                    grupos.remove(grupoA)
                                    grupos.remove(grupoB)
                                }
                            }
                        }
                    }
                    for (k in grupos) {
                        for (p in k.getMiembros()) {
                            GestorSalida.ENVIAR_Im_INFORMACION(p, "1KOLISEO_NO_ELEGIDO")
                            INSCRITOS_KOLISEO_RANGO_VS1.get(p.getRangoKoli()).add(p)
                        }
                        k.limpiarGrupo()
                    }
                }
            } catch (e: Exception) {
                MainServidor.redactarLogServidorln("null en koliseo3 " + e.getMessage())
            }
            SEGUNDOS_INICIO_KOLISEO = MainServidor.SEGUNDOS_INICIAR_KOLISEO
        }

        fun iniciarKoliseo(rango: Int) {
            try {
                val mapas: ArrayList<Mapa> = ArrayList<Mapa>()
                for (s in MainServidor.MAPAS_KOLISEO.split(",")) {
                    try {
                        mapas.add(getMapa(Short.parseShort(s)))
                    } catch (e: Exception) {
                    }
                }
                if (mapas.isEmpty()) {
                    SEGUNDOS_INICIO_KOLISEO = MainServidor.SEGUNDOS_INICIAR_KOLISEO
                    GestorSalida.ENVIAR_Im_INFORMACION_KOLISEO("1KOLISEO_FALTA_MAPAS")
                    return
                }
                val _INSCRITOS_KOLISEO: CopyOnWriteArrayList<Personaje> = INSCRITOS_KOLISEO_RANGO.get(rango)
                val listos: ArrayList<Personaje> = ArrayList<Personaje>()
                for (p in _INSCRITOS_KOLISEO) {
                    if (p.puedeIrKoliseo()) {
                        listos.add(p)
                    } else {
                        if (p.getGrupoKoliseo() != null) {
                            p.getGrupoKoliseo().dejarGrupo(p)
                        }
                    }
                }
                if (listos.size() < MainServidor.CANTIDAD_MIEMBROS_EQUIPO_KOLISEO * 2) {
                    GestorSalida.ENVIAR_Im_INFORMACION_KOLISEO("1KOLISEO_FALTA_INSCRITOS", rango)
                    return
                }
                _INSCRITOS_KOLISEO.clear()
                val ordenados: ArrayList<Personaje> = ArrayList<Personaje>()
                ordenados.addAll(listos)
                val grupos: ArrayList<GrupoKoliseo> = ArrayList<GrupoKoliseo>()
                var i = 0
                for (p in ordenados) {
                    if (!p.puedeIrKoliseo()) continue
                    if (p.getGrupoKoliseo() != null) {
                        if (!grupos.contains(p.getGrupoKoliseo())) {
                            grupos.add(p.getGrupoKoliseo())
                        }
                    } else {
                        var salir = false
                        while (!salir) {
                            try {
                                if (grupos.get(i).addPersonaje(p)) {
                                    p.setGrupoKoliseo(grupos.get(i))
                                    salir = true
                                } else {
                                    i++
                                }
                            } catch (e: Exception) {
                                val g = GrupoKoliseo(p)
                                p.setGrupoKoliseo(g)
                                grupos.add(g)
                                salir = true
                            }
                        }
                    }
                }
                val combate: ArrayList<GrupoKoliseo> = ArrayList<GrupoKoliseo>()
                if (combate.size() === grupos.size()) {
                    return
                }
                combate.addAll(grupos)
                for (grupoA in combate) {
                    if (grupos.contains(grupoA)) {
                        for (grupoB in combate) {
                            if (grupoA === grupoB || grupoA.getCantPjs() !== grupoB.getCantPjs() || grupoA.contieneIPOtroGrupo(grupoB)) {
                                continue
                            }
                            var team1: String? = null
                            var team2: String? = null
                            for (perso in grupoA.getMiembros()) {
                                perso.fullPDV()
                                if (team1 == null) {
                                    team1 = perso.getNombre()
                                } else {
                                    team1 += " <b>,</b> " + perso.getNombre()
                                }
                            }
                            for (perso in grupoB.getMiembros()) {
                                perso.fullPDV()
                                if (team2 == null) {
                                    team2 = perso.getNombre()
                                } else {
                                    team2 += " <b>,</b> " + perso.getNombre()
                                }
                            }
                            val mapa: Mapa = mapas.get(Formulas.getRandomInt(0, mapas.size() - 1))
                            for (perso1 in grupoA.getMiembros()) {
                                if (perso1 != null) perso1.teleport(mapa.getID(), 399.toShort())
                            }
                            for (perso2 in grupoB.getMiembros()) {
                                if (perso2 != null) perso2.teleport(mapa.getID(), 399.toShort())
                            }
                            if (mapa.iniciarPeleaKoliseo(grupoA, grupoB)) {
                                grupos.remove(grupoA)
                                grupos.remove(grupoB)
                                GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_TODOS("?", "Koliseo", team1.toString() + " <b>VS</b> " + team2)
                            }
                        }
                    }
                }
                for (k in grupos) {
                    for (p in k.getMiembros()) {
                        GestorSalida.ENVIAR_Im_INFORMACION(p, "1KOLISEO_NO_ELEGIDO")
                        INSCRITOS_KOLISEO_RANGO.get(p.getRangoKoli()).add(p)
                    }
                    k.limpiarGrupo()
                }
            } catch (e: Exception) {
                MainServidor.redactarLogServidorln("null en koliseo 4 " + e.getMessage())
            }
        }

        fun iniciarKoliseo1(rango: Int) {
            try {
                val mapas: ArrayList<Mapa> = ArrayList<Mapa>()
                for (s in MainServidor.MAPAS_KOLISEO.split(",")) {
                    try {
                        mapas.add(getMapa(Short.parseShort(s)))
                    } catch (e: Exception) {
                    }
                }
                if (mapas.isEmpty()) {
                    SEGUNDOS_INICIO_KOLISEO = MainServidor.SEGUNDOS_INICIAR_KOLISEO
                    GestorSalida.ENVIAR_Im_INFORMACION_KOLISEO("1KOLISEO_FALTA_MAPAS")
                    return
                }
                val _INSCRITOS_KOLISEO: CopyOnWriteArrayList<Personaje> = INSCRITOS_KOLISEO_RANGO_VS1.get(rango)
                val listos: ArrayList<Personaje> = ArrayList<Personaje>()
                for (p in _INSCRITOS_KOLISEO) {
                    if (p.puedeIrKoliseo()) {
                        listos.add(p)
                    } else {
                        if (p.getGrupoKoliseo() != null) {
                            p.getGrupoKoliseo().dejarGrupo(p)
                        }
                    }
                }
                if (listos.size() < MainServidor.CANTIDAD_MIEMBROS_EQUIPO_KOLISEO_VS1 * 2) {
                    GestorSalida.ENVIAR_Im_INFORMACION_KOLISEO("1KOLISEO_FALTA_INSCRITOS", rango)
                    return
                }
                _INSCRITOS_KOLISEO.clear()
                val ordenados: ArrayList<Personaje> = ArrayList<Personaje>()
                ordenados.addAll(listos)
                val grupos: ArrayList<GrupoKoliseo> = ArrayList<GrupoKoliseo>()
                var i = 0
                for (p in ordenados) {
                    if (!p.puedeIrKoliseo()) continue
                    if (p.getGrupoKoliseo() != null) {
                        if (!grupos.contains(p.getGrupoKoliseo())) {
                            grupos.add(p.getGrupoKoliseo())
                        }
                    } else {
                        var salir = false
                        while (!salir) {
                            try {
                                if (grupos.get(i).addPersonaje1(p)) {
                                    p.setGrupoKoliseo(grupos.get(i))
                                    salir = true
                                } else {
                                    i++
                                }
                            } catch (e: Exception) {
                                val g = GrupoKoliseo(p)
                                p.setGrupoKoliseo(g)
                                grupos.add(g)
                                salir = true
                            }
                        }
                    }
                }
                val combate: ArrayList<GrupoKoliseo> = ArrayList<GrupoKoliseo>()
                if (combate.size() === grupos.size()) {
                    return
                }
                combate.addAll(grupos)
                for (grupoA in combate) {
                    if (grupos.contains(grupoA)) {
                        for (grupoB in combate) {
                            if (grupoA === grupoB || grupoA.getCantPjs() !== grupoB.getCantPjs() || grupoA.contieneIPOtroGrupo(grupoB)) {
                                continue
                            }
                            var team1: String? = null
                            var team2: String? = null
                            for (perso in grupoA.getMiembros()) {
                                perso.fullPDV()
                                if (team1 == null) {
                                    team1 = perso.getNombre()
                                } else {
                                    team1 += " <b>,</b> " + perso.getNombre()
                                }
                            }
                            for (perso in grupoB.getMiembros()) {
                                perso.fullPDV()
                                if (team2 == null) {
                                    team2 = perso.getNombre()
                                } else {
                                    team2 += " <b>,</b> " + perso.getNombre()
                                }
                            }
                            val mapa: Mapa = mapas.get(Formulas.getRandomInt(0, mapas.size() - 1))
                            for (perso1 in grupoA.getMiembros()) {
                                if (perso1 != null) perso1.teleport(mapa.getID(), 399.toShort())
                            }
                            for (perso2 in grupoB.getMiembros()) {
                                if (perso2 != null) perso2.teleport(mapa.getID(), 399.toShort())
                            }
                            if (mapa.iniciarPeleaKoliseo(grupoA, grupoB)) {
                                grupos.remove(grupoA)
                                grupos.remove(grupoB)
                                GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_TODOS("?", "Koliseo", team1.toString() + " <b>VS</b> " + team2)
                            }
                        }
                    }
                }
                for (k in grupos) {
                    for (p in k.getMiembros()) {
                        GestorSalida.ENVIAR_Im_INFORMACION(p, "1KOLISEO_NO_ELEGIDO")
                        INSCRITOS_KOLISEO_RANGO.get(p.getRangoKoli()).add(p)
                    }
                    k.limpiarGrupo()
                }
            } catch (e: Exception) {
                MainServidor.redactarLogServidorln("null en koliseo5" + e.getMessage())
            }
        }

        fun iniciarKoliseo2(rango: Int) {
            try {
                val mapas: ArrayList<Mapa> = ArrayList<Mapa>()
                for (s in MainServidor.MAPAS_KOLISEO.split(",")) {
                    try {
                        mapas.add(getMapa(Short.parseShort(s)))
                    } catch (e: Exception) {
                    }
                }
                if (mapas.isEmpty()) {
                    SEGUNDOS_INICIO_KOLISEO = MainServidor.SEGUNDOS_INICIAR_KOLISEO
                    GestorSalida.ENVIAR_Im_INFORMACION_KOLISEO("1KOLISEO_FALTA_MAPAS")
                    return
                }
                val _INSCRITOS_KOLISEO: CopyOnWriteArrayList<Personaje> = INSCRITOS_KOLISEO_RANGO_VS2.get(rango)
                val listos: ArrayList<Personaje> = ArrayList<Personaje>()
                for (p in _INSCRITOS_KOLISEO) {
                    if (p.puedeIrKoliseo()) {
                        listos.add(p)
                    } else {
                        if (p.getGrupoKoliseo() != null) {
                            p.getGrupoKoliseo().dejarGrupo(p)
                        }
                    }
                }
                if (listos.size() < MainServidor.CANTIDAD_MIEMBROS_EQUIPO_KOLISEO_VS2 * 2) {
                    GestorSalida.ENVIAR_Im_INFORMACION_KOLISEO("1KOLISEO_FALTA_INSCRITOS", rango)
                    return
                }
                _INSCRITOS_KOLISEO.clear()
                val ordenados: ArrayList<Personaje> = ArrayList<Personaje>()
                ordenados.addAll(listos)
                val grupos: ArrayList<GrupoKoliseo> = ArrayList<GrupoKoliseo>()
                var i = 0
                for (p in ordenados) {
                    if (!p.puedeIrKoliseo()) continue
                    if (p.getGrupoKoliseo() != null) {
                        if (!grupos.contains(p.getGrupoKoliseo())) {
                            grupos.add(p.getGrupoKoliseo())
                        }
                    } else {
                        var salir = false
                        while (!salir) {
                            try {
                                if (grupos.get(i).addPersonaje2(p)) {
                                    p.setGrupoKoliseo(grupos.get(i))
                                    salir = true
                                } else {
                                    i++
                                }
                            } catch (e: Exception) {
                                val g = GrupoKoliseo(p)
                                p.setGrupoKoliseo(g)
                                grupos.add(g)
                                salir = true
                            }
                        }
                    }
                }
                val combate: ArrayList<GrupoKoliseo> = ArrayList<GrupoKoliseo>()
                if (combate.size() === grupos.size()) {
                    return
                }
                combate.addAll(grupos)
                for (grupoA in combate) {
                    if (grupos.contains(grupoA)) {
                        for (grupoB in combate) {
                            if (grupoA === grupoB || grupoA.getCantPjs() !== grupoB.getCantPjs() || grupoA.contieneIPOtroGrupo(grupoB)) {
                                continue
                            }
                            var team1: String? = null
                            var team2: String? = null
                            for (perso in grupoA.getMiembros()) {
                                perso.fullPDV()
                                if (team1 == null) {
                                    team1 = perso.getNombre()
                                } else {
                                    team1 += " <b>,</b> " + perso.getNombre()
                                }
                            }
                            for (perso in grupoB.getMiembros()) {
                                perso.fullPDV()
                                if (team2 == null) {
                                    team2 = perso.getNombre()
                                } else {
                                    team2 += " <b>,</b> " + perso.getNombre()
                                }
                            }
                            val mapa: Mapa = mapas.get(Formulas.getRandomInt(0, mapas.size() - 1))
                            for (perso1 in grupoA.getMiembros()) {
                                if (perso1 != null) perso1.teleport(mapa.getID(), 399.toShort())
                            }
                            for (perso2 in grupoB.getMiembros()) {
                                if (perso2 != null) perso2.teleport(mapa.getID(), 399.toShort())
                            }
                            if (mapa.iniciarPeleaKoliseo(grupoA, grupoB)) {
                                grupos.remove(grupoA)
                                grupos.remove(grupoB)
                                GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_TODOS("?", "Koliseo", team1.toString() + " <b>VS</b> " + team2)
                            }
                        }
                    }
                }
                for (k in grupos) {
                    for (p in k.getMiembros()) {
                        GestorSalida.ENVIAR_Im_INFORMACION(p, "1KOLISEO_NO_ELEGIDO")
                        INSCRITOS_KOLISEO_RANGO.get(p.getRangoKoli()).add(p)
                    }
                    k.limpiarGrupo()
                }
            } catch (e: Exception) {
                MainServidor.redactarLogServidorln("null en koliseo6 " + e.getMessage())
            }
        }

        fun listaObjetosTruequePor(aDar: Map<Integer?, Long?>, npcID: Int): Map<Integer, Long> {
            val recibir: Map<Integer, Long> = TreeMap<Integer, Long>()
            val aDar2: Map<Integer, Long> = TreeMap<Integer, Long>()
            aDar2.putAll(aDar)
            val objetos: ArrayList<ObjetoTrueque> = ArrayList()
            for (objT in OBJETOS_TRUEQUE) {
                if (objT.permiteNPC(npcID) && !objT.getNecesita().isEmpty()) {
                    objetos.add(objT)
                }
            }
            Collections.sort(objetos)
            for (objT in objetos) {
                var cantFinal: Long = -1
                var completo = true
                if (aDar.size() !== objT.getNecesita().entrySet().size()) {
                    completo = false
                } else {
                    for (a in objT.getNecesita().entrySet()) {
                        var cant: Long = 0
                        var residuo: Long = 0
                        val idNecesita: Int = a.getKey()
                        val cantNecesita: Long = a.getValue()
                        try {
                            val tiene = aDar2[idNecesita]!!
                            cant = tiene / cantNecesita
                            residuo = tiene % cantNecesita
                        } catch (e: Exception) {
                            completo = false
                            break
                        }
                        if (cant <= 0 || residuo != 0L) {
                            completo = false
                            break
                        }
                        if (cantFinal == -1L) {
                            cantFinal = cant
                        } else if (cantFinal != cant) {
                            completo = false
                            break
                        }
                    }
                }
                if (completo) {
                    for (a in objT.getNecesita().entrySet()) {
                        val idNecesita: Int = a.getKey()
                        val cantNecesita: Int = a.getValue()
                        val tiene = aDar2[idNecesita]!! - cantFinal * cantNecesita
                        if (tiene <= 0) {
                            aDar2.remove(idNecesita)
                        } else {
                            aDar2.put(idNecesita, tiene)
                        }
                    }
                    var actual: Long = 0
                    if (recibir.containsKey(objT.getID())) {
                        actual = recibir[objT.getID()]!!
                    }
                    recibir.put(objT.getID(), actual + cantFinal)
                }
            }
            return recibir
        }
    }
}