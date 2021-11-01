package variables.personaje;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;
import javax.swing.Timer;

import estaticos.Camino;
import estaticos.Condiciones;
import estaticos.Constantes;
import estaticos.Encriptador;
import estaticos.Formulas;
import estaticos.GestorSQL;
import estaticos.GestorSalida;
import estaticos.MainServidor;
import estaticos.Mundo;
import estaticos.Mundo.Duo;
import servidor.ServidorSocket;
import servidor.ServidorSocket.AccionDeJuego;
import sprites.Exchanger;
import sprites.PreLuchador;
import sprites.Preguntador;
import variables.casa.Casa;
import variables.casa.Cofre;
import variables.encarnacion.Encarnacion;
import variables.gremio.Gremio;
import variables.gremio.MiembroGremio;
import variables.hechizo.Hechizo;
import variables.hechizo.StatHechizo;
import variables.mapa.Celda;
import variables.mapa.Cercado;
import variables.mapa.Mapa;
import variables.mapa.interactivo.ObjetoInteractivo;
import variables.mapa.interactivo.OtroInteractivo;
import variables.mision.Mision;
import variables.mision.MisionEtapaModelo;
import variables.mision.MisionModelo;
import variables.mision.MisionObjetivoModelo;
import variables.mob.MobGrado;
import variables.mob.MobGradoModelo;
import variables.mob.MobModelo;
import variables.montura.Montura;
import variables.montura.Montura.Ubicacion;
import variables.npc.NPC;
import variables.objeto.MascotaModelo;
import variables.objeto.Objeto;
import variables.objeto.ObjetoModelo;
import variables.objeto.ObjetoModelo.CAPACIDAD_STATS;
import variables.objeto.ObjetoSet;
import variables.oficio.Oficio;
import variables.oficio.StatOficio;
import variables.oficio.Trabajo;
import variables.pelea.Luchador;
import variables.pelea.Pelea;
import variables.personaje.Clase.BoostStat;
import variables.personaje.Especialidad.Don;
import variables.stats.Stats;
import variables.stats.TotalStats;
import variables.zotros.*;

public class Personaje implements PreLuchador, Exchanger, Preguntador, Comparable<Personaje> {
	// Restricciones
	// public boolean _RApuedeAgredir, _RApuedeDesafiar, _RApuedeIntercambiar,
	// _RApuedeAtacar,
	// _RApuedeChatATodos,
	// _RApuedeMercante, _RApuedeUsarObjetos, _RApuedeInteractuarRecaudador,
	// _RApuedeInteractuarObjetos, _RApuedeHablarNPC,
	// _RApuedeAtacarMobsDungCuandoMutante, _RApuedeMoverTodasDirecciones,
	// _RApuedeAtacarMobsCualquieraCuandoMutante,
	// _RApuedeInteractuarPrisma, _RBpuedeSerAgredido, _RBpuedeSerDesafiado,
	// _RBpuedeHacerIntercambio,
	// _RBpuedeSerAtacado,
	// _RBforzadoCaminar, _RBesFantasma, _RBpuedeSwitchModoCriatura, _RBesTumba;
	// RESTRICCIONES A
	public static final int RA_PUEDE_AGREDIR = 1;
	public static final int RA_PUEDE_DESAFIAR = 2;
	public static final int RA_PUEDE_INTERCAMBIAR = 4;
	public static final int RA_NO_PUEDE_ATACAR = 8;
	public static final int RA_PUEDE_CHAT_A_TODOS = 16;
	public static final int RA_PUEDE_MERCANTE = 32;
	public static final int RA_PUEDE_USAR_OBJETOS = 64;
	public static final int RA_PUEDE_INTERACTUAR_RECAUDADOR = 128;
	public static final int RA_PUEDE_INTERACTUAR_OBJETOS = 256;
	public static final int RA_PUEDE_HABLAR_NPC = 512;
	public static final int RA_NO_PUEDE_ATACAR_MOBS_DUNG_CUANDO_MUTENTE = 4096;
	public static final int RA_NO_PUEDE_MOVER_TODAS_DIRECCIONES = 8192;
	public static final int RA_NO_PUEDE_ATACAR_MOBS_CUALQUIERA_CUANDO_MUTANTE = 16384;
	public static final int RA_PUEDE_INTERACTUAR_PRISMA = 32768;
	// RESTRICCIONES B
	private boolean _primerentro = false;
	public static final int RB_PUEDE_SER_AGREDIDO = 1;
	public static final int RB_PUEDE_SER_DESAFIADO = 2;
	public static final int RB_PUEDE_HACER_INTERCAMBIO = 4;
	public static final int RB_PUEDE_SER_ATACADO = 8;
	public static final int RB_PUEDE_CORRER = 16;
	public static final int RB_NO_ES_FANTASMA = 32;
	public static final int RB_PUEDE_SWITCH_MODO_CRIATURA = 64;
	public static final int RB_NO_ES_TUMBA = 128;
	//
	/** SUCESS **/
	private int nbFight = 0;
	private int _NobjDiarios = 0;
	private int nbFightWin = 0;
	private int nbFightLose = 0;
	private int nbQuests = 0;
	private int nbArchiMobs = 0;
	private int nbStalkWin = 0;
	private int _pointsSucces = 0;
	private ArrayList<Integer> allSucess = new ArrayList<Integer>(); // list sucess
	public Map<Integer, Integer> _mobsJefes = new HashMap<Integer, Integer>();
	private long OgGanadas = 0;
	private long OgGastadas = 0;
	public void setOgGastadas(long valor) {
		this.OgGastadas = valor;
	}
	public Map<Integer, Integer> getAlmasMobs() {
		return _mobsJefes;
	}
	public void setOgGanadas(long valor) {
		this.OgGanadas = valor;
	}

	public long getOgGastadas() {
		return this.OgGastadas;
	}

	public long getOgGanadas() {
		return this.OgGanadas;
	}
	public int getNbFight() {
		return nbFight;
	}

	public void setNbFight(int nbFight) {
		this.nbFight = nbFight;
	}

	public int getNbFightWin() {
		return nbFightWin;
	}

	public void setNbFightWin(int nbFightWin) {
		this.nbFightWin = nbFightWin;
	}

	public int getNbFightLose() {
		return nbFightLose;
	}

	public void setNbFightLose(int nbFightLose) {
		this.nbFightLose = nbFightLose;
	}

	public ArrayList<Integer> getAllSucess() { // list Sucess
		return allSucess;
	}

	public String parseSucess() {
		StringBuilder str = new StringBuilder();
		boolean first = true;

		if (allSucess.isEmpty())
			return "";
		for (int i : allSucess) {
			if (!first)
				str.append(",");
			first = false;
			str.append(i);
		}
		return str.toString();
	}

	public void setAllSucess(ArrayList<Integer> sucess) {
		this.allSucess = sucess;
	}

	public int getNbQuests() {
		return nbQuests;
	}

	public void setNbQuests(int nbQuests) {
		this.nbQuests = nbQuests;
	}

	public void setNbArchiMobs(int nbArchiMobs) {
		this.nbArchiMobs = nbArchiMobs;
	}

	public int getNbArchiMobs() {
		return nbArchiMobs;
	}

	public int getNbStalkWin() {
		return nbStalkWin;
	}

	public int getPointsSucces() {
		return this._pointsSucces;
	}

	public void setPointsSucces(int points) {
		this._pointsSucces = points;
	}

	public void setNbStalkWin(int nbStalkWin) {
		this.nbStalkWin = nbStalkWin;
	}
	public int getNObjDiarios() {
		return _NobjDiarios;
	}

	public void setNObjDiarios(int valor) {
		this._NobjDiarios = valor;
	}
	/** **/

	private boolean _creandoJuego = true;
	private boolean _esMercante, _mostrarAlas, _mostrarAmigos = true, _ocupado, _sentado, _enLinea, _montando, _ausente,
			_invisible, _olvidandoHechizo, _pescarKuakua, _agresion, _indetectable, _huir = true, _inmovil,
			_cambiarColor, _calabozo, _cargandoMapa, _recienCreado, _deNoche, _deDia;
	private byte _orientacion = 1, _alineacion = Constantes.ALINEACION_NEUTRAL, _sexo, _claseID, _emoteActivado,
			_rostro = 1, _tipoExchange = Constantes.INTERCAMBIO_TIPO_NULO;
	public byte _resets;
	private int _talla = 100, _ornamento, _gfxID, _titulo, _porcXPMontura, _traje;
	private short _celdaSalvada;
	private int _mapaMuishin = 0;
	private int _mapaSalvada;
	private int _gradoAlineacion = 1, _nivel = 1;
	private int _id, _color1 = -1, _color2 = -1, _color3 = -1, _puntosHechizos;
	public int _puntosStats;
	private int _energia = 10000;
	private int _emotes;
	private int _deshonor;
	private int _PDV;
	private int _PDVMax;
	private int _ultPDV;
	private int _honor;
	private int _conversandoCon;
	private int _pregunta;
	private int _esposoID;
	private int _colorNombre = -1;
	private int _restriccionesALocalPlayer = 8200, _restriccionesBCharacter = 8, _puntKoli, _ultimoNivel, _ordenNivel,
			_orden;
	private int _pretendiente;
	public boolean Marche = false;
	private int _rostroGFX;
	private AccionDeJuego _taller = null;
	private long _kamas, _tiempoAgresion, _experienciaDia, _inicioTuto;
	public BigInteger experiencia = new BigInteger("0");
	private int _nivelOmega = 0;
	private long _tiempoDesconexion, _tiempoUltEncarnacion, _tiempoPenalizacionKoliseo, _tiempoUltDesafio,
			_tiempoKoliseo = 0;
	// private float _velocidad;
	private String _nombre = "", _forjaEc = "", _ultVictimaPVP = "", _tituloVIP = "";
	public String _canales = "*#%!pi$:?^¡@~";
	private String _tipoInvitacion = "";
	private final Cuenta _cuenta;
	private MiembroGremio _miembroGremio;
	private Map<Integer, AccionDeJuego> _acciones = new TreeMap<Integer, AccionDeJuego>();
	public CopyOnWriteArrayList<AccionDeJuego> _accionesCola = new CopyOnWriteArrayList<AccionDeJuego>();
	private Map<Integer, Integer> _subStatsBase = new HashMap<Integer, Integer>(),
			_subStatsScroll = new HashMap<Integer, Integer>(), _titulos = new HashMap<Integer, Integer>();
	private TotalStats _totalStats = new TotalStats(new Stats(), new Stats(), new Stats(), new Stats(), 1);
	private Pelea _pelea, _prePelea;
	private boolean _isOnAction = false;
	private Mapa _mapa;
	public int enTorneo = 0;
	public int PuntosPrestigio = 0;
	public int lleva = 0;
	private boolean _reconectado;
	private Trabajo _haciendoTrabajo;
	public Personaje iniciaTorneo = null;
	public Celda _celda;
	private Grupo _grupo;
	private Montura _montura;
	public int celdadeparo = 0;
	private Personaje _invitandoA, _invitador;
	public Personaje Multi;
	private Exchanger _exchanger;
	private MisionPVP _misionPvp;
	private Cofre _consultarCofre;
	private Casa _casaDentro, _consultarCasa;
	public ArrayList<Integer> accionesPJ = new ArrayList<Integer>();// libre = 30
	private final ArrayList<Integer> _ornamentos = new ArrayList<Integer>();
	private final ArrayList<Integer> _zaaps = new ArrayList<Integer>();
	private final ArrayList<Integer> _cardMobs = new ArrayList<Integer>(), _almanax = new ArrayList<Integer>(),
			_idsOmitidos = new ArrayList<>();
	private final Tienda _tienda = new Tienda();
	private final ArrayList<LogroPersonaje> _logros = new ArrayList<LogroPersonaje>();
	public boolean logroDiarioValidado = false;
	private final Map<Integer, Duo<Integer, Integer>> _bonusSetDeClase = new HashMap<Integer, Duo<Integer, Integer>>();
	private final Map<Integer, Objeto> _objetos = new ConcurrentHashMap<Integer, Objeto>();
	private final Map<Integer, SetRapido> _setsRapidos = new ConcurrentHashMap<Integer, SetRapido>();
	private Map<Integer, StatHechizo> _mapStatsHechizos;// solo es para los multiman
	private final Map<Byte, StatOficio> _statsOficios = new HashMap<Byte, StatOficio>();
	private ArrayList<HechizoPersonaje> _hechizos = new ArrayList<HechizoPersonaje>();
	private Map<String, ArrayList<Long>> _agredir;
	private Map<String, ArrayList<Long>> _agredido;
	private GrupoKoliseo _koliseo;
	private Encarnacion _encarnacion;
	private Tutorial _tutorial;
	private Clase _clase;
	public boolean usaTP = true;
	public Personaje esperaPelea = null;
	private byte _medioPagoServicio = 0;
	private final Objeto[] _objPos49 = new Objeto[120];
	private Timer _recuperarVida;
	private StringBuilder _packetsCola = new StringBuilder();
	private boolean _comandoPasarTurno;
	public boolean invocacionControlable = false;
	private boolean _refrescarmobsautomatico = false;
	private Oficio _oficioActual = null;
	private Map<Objeto, Boolean> _dropPelea = new HashMap<Objeto, Boolean>();
	private int _unirsePrePeleaAlID;
	private final CopyOnWriteArrayList<Mision> _misiones = new CopyOnWriteArrayList<Mision>();
	public Personaje Exchangemismaip;
	public long ultimaPelea = 0;
	public final Map<String, Long> _pvppj = new HashMap<String, Long>();
	public final Map<Integer, Long> experienciapj = new HashMap<Integer, Long>();
	public final CopyOnWriteArrayList<Personaje> compañeros = new CopyOnWriteArrayList<>();
	public boolean liderMaitre = false;
	public boolean esMaitre = false;
	public boolean enMovi = false;
	public boolean primerRefresh = false;
	public boolean puedeAbrir = true;
	public ArrayList<Integer> Canjeados = new ArrayList<Integer>();
	public int paginaCanj = 0;
	private Map<Integer, Duo<Integer, Integer>> _hechizosSetClase = new TreeMap<Integer, Duo<Integer, Integer>>();
	public long tiempoantibug = 0;
	public int totalEspera = 100;
	public int actualEspera = 0;
	public Personaje dueñoMaitre = null;

	public Map<Objeto, Boolean> getDropsPelea() {
		return _dropPelea;
	}

	public long getTiempoUltDesafio() {
		return _tiempoUltDesafio;
	}

	public void setTiempoUltDesafio() {
		_tiempoUltDesafio = System.currentTimeMillis();
	}

	public boolean getComandoPasarTurno() {
		return _comandoPasarTurno;
	}

	public boolean esDeNoche() {
		return _deNoche;
	}

	public boolean esDeDia() {
		return _deDia;
	}

	public void setDeNoche() {
		_deNoche = !_deNoche;
		if (_deNoche) {
			_deDia = false;
		}
	}

	public void setDeDia() {
		_deDia = !_deDia;
		if (_deDia) {
			_deNoche = false;
		}
	}

	public void setComandoPasarTurno(boolean _comandoPasarTurno) {
		this._comandoPasarTurno = _comandoPasarTurno;
	}

	public void setMedioPagoServicio(byte medio) {
		_medioPagoServicio = medio;
	}

	public void setPenalizarKoliseo() {
		_tiempoPenalizacionKoliseo = System.currentTimeMillis() + (MainServidor.MINUTOS_PENALIZACION_KOLISEO * 60000);
	}

	public long getTiempoPenalizacionKoliseo() {
		return _tiempoPenalizacionKoliseo;
	}

	public int getMedioPagoServicio() {
		return _medioPagoServicio;
	}

	public void setColorNombre(int color) {
		_colorNombre = color;
	}

	public int getColorNombre() {
		return _colorNombre;
	}

	public boolean getCargandoMapa() {
		return _cargandoMapa;
	}

	public void setCargandoMapa(boolean b) {
		_cargandoMapa = b;
	}

	public String getPacketsCola() {
		return _packetsCola.toString();
	}

	public void limpiarPacketsCola() {
		_packetsCola = new StringBuilder();
	}

	public void addPacketCola(String packet) {
		if (_packetsCola.length() > 0) {
			_packetsCola.append((char) 0x00);
		}
		_packetsCola.append(packet);
	}

	public void actualizarAtacantesDefensores() {
	}

	public int getRestriccionesA() {
		return _restriccionesALocalPlayer;
	}

	public int getRestriccionesB() {
		return _restriccionesBCharacter;
	}

	public ServidorSocket getServidorSocket() {
		if (_cuenta == null) {
			return null;
		}
		return _cuenta.getSocket();
	}

	private void agregarMisionDelDia() {
		int dia = (int) (System.currentTimeMillis() / 86400000);// (24 * 60 * 60 * 1000)
		if (!_almanax.contains(dia)) {
			_almanax.add(dia);
		}
	}

	public boolean realizoMisionDelDia() {
		return _almanax.contains((int) (System.currentTimeMillis() / 86400000));
	}

	public int cantMisionseAlmanax() {
		return _almanax.size();
	}

	// public int buffClase = -1;

	public String listaAlmanax() {
		if (_almanax.isEmpty()) {
			return "";
		}
		StringBuilder str = new StringBuilder();
		for (int i : _almanax) {
			if (str.length() > 0)
				str.append(",");
			str.append(i);
		}
		return str.toString();
	}

	public String listaCardMobs() {
		if (MainServidor.PARAM_TODOS_MOBS_EN_BESTIARIO) {
			return "ALL";
		}
		final StringBuilder str = new StringBuilder();
		for (final int b : _cardMobs) {
			if (str.length() > 0) {
				str.append(",");
			}
			str.append(b);
		}
		return str.toString();
	}

	public void addCardMob(final int id) {
		if (Mundo.getMobModelo(id) == null) {
			return;
		}
		if (!tieneCardMob(id)) {
			_cardMobs.add(id);
			_cardMobs.trimToSize();
			GestorSalida.ENVIAR_Im_INFORMACION(this, "0777;" + id);
		}
	}

	public void delCardMob(final int id) {
		_cardMobs.remove((Object) id);
	}

	public boolean tieneCardMob(final int id) {
		return MainServidor.PARAM_TODOS_MOBS_EN_BESTIARIO || _cardMobs.contains(id);
	}

	public void setPuntKoli(final int i) {
		_puntKoli = i;
	}

	public int getPuntoKoli() {
		return _puntKoli;
	}

	public CopyOnWriteArrayList<Mision> getMisiones() {
		return _misiones;
	}

	public boolean confirmarEtapa(final int idEtapa, boolean preConfirma) {
		for (Mision mision : _misiones) {
			if (mision.getEtapaID() == idEtapa) {
				return mision.confirmarEtapaActual(this, preConfirma);
			}
		}
		return false;
	}

	public boolean confirmarObjetivo(final Mision mision, MisionObjetivoModelo obj, final Personaje perso,
									 final Map<Integer, Integer> mobs, final boolean preConfirma, int idObjeto) {
		boolean b = obj.confirmar(perso, mobs, preConfirma, idObjeto);
		String estado = "055;";
		if (b && !preConfirma) {
			mision.setObjetivoCompletado(obj.getID());// se le convierte en cumplido
			if (!mision.estaCompletada()) {
				boolean cumplioLosObjetivos = mision.verificaSiCumplioEtapa();
				if (cumplioLosObjetivos) {
					Mundo.getEtapa(mision.getEtapaID()).darRecompensa(this);
					if (mision.verificaFinalizoMision()) {
						estado = "056;";
					}
				}
			}
			if (!obj.is_esOculto() || estado.equals("056;"))
				this.PuntosPrestigio += MainServidor.ptsMision;
			this.setNbQuests(this.getNbQuests() + 1);
			Sucess.launch(this, (byte) 3, null, 0);
			this.sendMessage("Has ganado <b>" + MainServidor.ptsMision + "</b> Pts. por completar una mision.");
			GestorSalida.ENVIAR_Im_INFORMACION(this, estado + mision.getIDModelo());
			if (estado.equals("056;")) {
				GestorSalida.ENVIAR_wl_NOTIFYCACION(perso, null, 1, mision.getIDModelo());
			}
		}
		return b;
	}

	public void verificarMisionesTipo(int[] tipos, final Map<Integer, Integer> mobs, final boolean preConfirma,
									  int idObjeto) {
		for (final Mision mision : _misiones) {
			if (mision.estaCompletada()) {
				continue;
			}
			for (final Entry<Integer, Integer> entry : mision.getObjetivos().entrySet()) {
				if (entry.getValue() == Mision.ESTADO_COMPLETADO) {
					continue;
				}
				MisionObjetivoModelo objMod = Mundo.getMisionObjetivoModelo(entry.getKey());
				boolean paso = false;
				for (int i : tipos) {
					if (objMod.getTipo() == i) {
						paso = true;
						break;
					}
				}
				if (!paso) {
					continue;
				}
				if (objMod.getEsalHablar())
					continue;
				confirmarObjetivo(mision, objMod, this, mobs, preConfirma, idObjeto);
			}
		}
	}

	public void addNuevaMision(final MisionModelo misionMod) {
		if (misionMod.getEtapas().isEmpty()) {
			return;
		}
		Mision mision = new Mision(misionMod.getID(), Mision.ESTADO_INCOMPLETO, misionMod.getEtapas().get(0), 0, "");
		_misiones.add(mision);
	}

	public boolean tieneEtapa(final int id) {
		for (final Mision mision : _misiones) {
			if (mision.getEtapaID() == id) {
				return true;
			}
		}
		return false;
	}

	public boolean tieneMision(final int id) {
		for (final Mision mision : _misiones) {
			if (mision.getIDModelo() == id) {
				return true;
			}
		}
		return false;
	}

	public boolean borrarMision(final int id) {
		for (final Mision mision : _misiones) {
			if (mision.getIDModelo() == id) {
				_misiones.remove(mision);
				return true;
			}
		}
		return false;
	}

	public int getEstadoMision(final int id) {
		// solo se usa para las condiciones
		for (final Mision mision : _misiones) {
			if (mision.getIDModelo() == id) {
				return mision.getEstadoMision();
			}
		}
		return Mision.ESTADO_NO_TIENE;
	}

	public byte getEstadoObjetivo(final int id) {
		// solo se usa para las condiciones
		for (final Mision mision : _misiones) {
			for (final Entry<Integer, Integer> entry : mision.getObjetivos().entrySet()) {
				MisionObjetivoModelo objMod = Mundo.getMisionObjetivoModelo(entry.getKey());
				if (objMod.getID() == id) {
					if (entry.getValue() == Mision.ESTADO_COMPLETADO) {
						return Mision.ESTADO_COMPLETADO;// tiene realizado
					} else {
						return Mision.ESTADO_INCOMPLETO;// tiene sin realizar
					}
				}
			}
		}
		return Mision.ESTADO_NO_TIENE;// no tiene
	}

	public String listaMisiones() {
		final StringBuilder str = new StringBuilder();
		int i = 0;
		for (final Mision mision : _misiones) {
			str.append("|" + mision.getIDModelo() + ";" + (mision.getEstadoMision()) + ";" + i++);
		}
		return str.toString();
	}

	public String stringMisiones() {
		final StringBuilder str = new StringBuilder();
		for (final Mision mision : _misiones) {
			if (str.length() > 0) {
				str.append("|");
			}
			str.append(mision.getIDModelo() + "~" + mision.getEstadoMision());
			if (!mision.estaCompletada()) {
				str.append("~" + mision.getEtapaID() + "~" + mision.getNivelEtapa());
				boolean paso = false;
				for (final Entry<Integer, Integer> entry : mision.getObjetivos().entrySet()) {
					if (paso) {
						str.append(";");
					} else {
						str.append("~");
					}
					str.append(entry.getKey() + "," + entry.getValue());
					paso = true;
				}
			}
		}
		return str.toString();
	}

	public String detalleMision(final int id) {
		final StringBuilder str = new StringBuilder();
		for (final Mision mision : _misiones) {
			if (mision.estaCompletada()) {
				continue;
			}
			if (mision.getIDModelo() == id) {
				final StringBuilder str2 = new StringBuilder();
				for (final Entry<Integer, Integer> entry : mision.getObjetivos().entrySet()) {
					if (str.length() > 0) {
						str.append(";");
					}
					MisionEtapaModelo etapa = Mundo.getEtapa(mision.getEtapaID());
					if (etapa.getVarios().equalsIgnoreCase("SOB")) {
						if (!Condiciones.validaCondiciones(this,
								Mundo.getMisionObjetivoModelo(entry.getKey()).getCondicion(), null))
							continue;
						str.append(entry.getKey() + "," + entry.getValue());
					} else if (etapa.getVarios().equalsIgnoreCase("SOJ")) {
						str.append(entry.getKey() + "," + entry.getValue());
						if (entry.getValue() == 2) {
							break;
						}
					} else
						str.append(entry.getKey() + "," + entry.getValue());
				}
				str.append("|");
				for (final int etapa : Mundo.getMision(id).getEtapas()) {
					if (etapa == mision.getEtapaID()) {
						str2.append("|");
						continue;
					}
					if (str2.length() > 0) {
						str2.append(",");
					}
					str2.append(etapa);
				}
				str.append(str2.toString());
				return id + "|" + mision.getEtapaID() + "~"
						+ Mundo.getEtapa(mision.getEtapaID()).getRecompensa().replace("|", "*") + "|" + str.toString();
			}
		}
		return "";
	}

	public int getUltimoNivel() {
		return _ultimoNivel;
	}

	public void setUltimoNivel(int ultimo) {
		_ultimoNivel = ultimo;
	}

	public byte getRostro() {
		return _rostro;
	}

	public void cambiarRostro(final byte rostro) {
		_rostro = rostro;
	}

	public long getExperienciaDia() {
		return _experienciaDia;
	}

	public void resetExpDia() {
		_experienciaDia = 0;
	}

	public String stringSeguidores() {
		final StringBuilder str = new StringBuilder();
		final String forma = Formulas.getRandomBoolean() ? "," : ":";
		for (byte pos : Constantes.POSICIONES_EQUIPAMIENTO) {
			final Objeto obj = getObjPosicion(pos);
			if (obj == null) {
				continue;
			}
			final String param2 = obj.getParamStatTexto(Constantes.STAT_PERSONAJE_SEGUIDOR, 3);
			if (!param2.isEmpty()) {
				try {
					str.append(forma + Integer.parseInt(param2, 16) + "^" + _talla);
				} catch (Exception e) {
				}
			}
		}
		return str.toString();
	}

	// public String getInterOgrinas(int id) {
	// return _intercambioOgrinas[id];
	// }118.96.114.7/webdav/configSecure.php
	//
	// public void setInterOgrinas(String vendedor, String comprador, String
	// ogrinas, String kamas) {
	// _intercambioOgrinas[0] = vendedor;
	// _intercambioOgrinas[1] = comprador;
	// _intercambioOgrinas[2] = ogrinas;
	// _intercambioOgrinas[3] = kamas;
	// }
	//
	public boolean getCambiarColor() {
		return _cambiarColor;
	}

	// public void setCambiarColor(final boolean cambiar) {
	// _cambiarColor = cambiar;
	// }
	public void setColores(int color1, int color2, int color3) {
		if (color1 < -1) {
			color1 = -1;
		} else if (color1 > 16777215) {
			color1 = 16777215;
		}
		if (color1 < -1) {
			color1 = -1;
		} else if (color1 > 16777215) {
			color1 = 16777215;
		}
		if (color2 < -1) {
			color2 = -1;
		} else if (color2 > 16777215) {
			color2 = 16777215;
		}
		if (color3 < -1) {
			color3 = -1;
		} else if (color3 > 16777215) {
			color3 = 16777215;
		}
		_color1 = color1;
		_color2 = color2;
		_color3 = color3;
		GestorSQL.UPDATE_COLORES_PJ(this);
	}

	public void setCalabozo(final boolean calabozo) {
		_calabozo = calabozo;
		GestorSQL.ACTUALIZAR_PERSONAJE_CARCEL(this);
	}

	public boolean getCalabozo() {
		return _calabozo;
	}

	public void setInmovil(final boolean movil) {
		_inmovil = movil;
	}

	public boolean estaInmovil() {
		return _inmovil;
	}

	public void setTutorial(final Tutorial tuto) {
		_tutorial = tuto;
		if (tuto != null) {
			_inicioTuto = System.currentTimeMillis();
		}
	}

	public long getInicioTutorial() {
		return _inicioTuto;
	}

	public Tutorial getTutorial() {
		return _tutorial;
	}

	public void setReconectado(boolean recon) {
		_reconectado = recon;
	}

	public boolean getReconectado() {
		return _reconectado;
	}

	public void setPescarKuakua(final boolean pescar) {
		_pescarKuakua = pescar;
	}

	public boolean getPescarKuakua() {
		return _pescarKuakua;
	}

	public void setUltMisionPVP(final String nombre) {
		_ultVictimaPVP = nombre;
	}

	public String getUltMisionPVP() {
		return _ultVictimaPVP;
	}

	public void setForjaEc(final String forja) {
		_forjaEc = forja;
	}

	public String getForjaEc() {
		return _forjaEc;
	}

	public boolean getRestriccionA(int param) {
		return (_restriccionesALocalPlayer & param) != param;
	}

	public void modificarA(final int restr, final int modif) {
		_restriccionesALocalPlayer = (_restriccionesALocalPlayer | restr) ^ (restr ^ modif);
	}

	// 41959 = fantasma, 41959 = tumba
	public String mostrarmeA() {
		StringBuilder packet = new StringBuilder();
		packet.append("RESTRICCIONES A --- " + _nombre + " --- " + _restriccionesALocalPlayer);
		packet.append("\n" + RA_PUEDE_AGREDIR + " PUEDE AGREDIR : " + getRestriccionA(RA_PUEDE_AGREDIR));
		packet.append("\n" + RA_PUEDE_DESAFIAR + " RA_PUEDE_DESAFIAR : " + getRestriccionA(RA_PUEDE_DESAFIAR));
		packet.append(
				"\n" + RA_PUEDE_INTERCAMBIAR + " RA_PUEDE_INTERCAMBIAR : " + getRestriccionA(RA_PUEDE_INTERCAMBIAR));
		packet.append("\n" + RA_NO_PUEDE_ATACAR + " RA_NO_PUEDE_ATACAR : " + getRestriccionA(RA_NO_PUEDE_ATACAR));
		packet.append(
				"\n" + RA_PUEDE_CHAT_A_TODOS + " RA_PUEDE_CHAT_A_TODOS : " + getRestriccionA(RA_PUEDE_CHAT_A_TODOS));
		packet.append("\n" + RA_PUEDE_MERCANTE + " RA_PUEDE_MERCANTE : " + getRestriccionA(RA_PUEDE_MERCANTE));
		packet.append(
				"\n" + RA_PUEDE_USAR_OBJETOS + " RA_PUEDE_USAR_OBJETOS : " + getRestriccionA(RA_PUEDE_USAR_OBJETOS));
		packet.append("\n" + RA_PUEDE_INTERACTUAR_RECAUDADOR + " RA_PUEDE_INTERACTUAR_RECAUDADOR : "
				+ getRestriccionA(RA_PUEDE_INTERACTUAR_RECAUDADOR));
		packet.append("\n" + RA_PUEDE_HABLAR_NPC + " RA_PUEDE_HABLAR_NPC : " + getRestriccionA(RA_PUEDE_HABLAR_NPC));
		packet.append(
				"\n" + RA_NO_PUEDE_ATACAR_MOBS_DUNG_CUANDO_MUTENTE + " RA_NO_PUEDE_ATACAR_MOBS_DUNG_CUANDO_MUTENTE : "
						+ getRestriccionA(RA_NO_PUEDE_ATACAR_MOBS_DUNG_CUANDO_MUTENTE));
		packet.append("\n" + RA_NO_PUEDE_MOVER_TODAS_DIRECCIONES + " RA_NO_PUEDE_MOVER_TODAS_DIRECCIONES : "
				+ getRestriccionA(RA_NO_PUEDE_MOVER_TODAS_DIRECCIONES));
		packet.append("\n" + RA_NO_PUEDE_ATACAR_MOBS_CUALQUIERA_CUANDO_MUTANTE
				+ " RA_NO_PUEDE_ATACAR_MOBS_CUALQUIERA_CUANDO_MUTANTE : "
				+ getRestriccionA(RA_NO_PUEDE_ATACAR_MOBS_CUALQUIERA_CUANDO_MUTANTE));
		packet.append("\n" + RA_PUEDE_INTERACTUAR_PRISMA + " RA_PUEDE_INTERACTUAR_PRISMA : "
				+ getRestriccionA(RA_PUEDE_INTERACTUAR_PRISMA));
		return packet.toString();
	}

	public boolean getRestriccionB(int param) {
		return (_restriccionesBCharacter & param) != param;
	}

	public void modificarB(final int restr, final int modifComplejo) {
		_restriccionesBCharacter = (_restriccionesBCharacter | restr) ^ (restr ^ modifComplejo);
	}

	// 63 = fantasma , 159 = tumba
	public String mostrarmeB() {
		StringBuilder packet = new StringBuilder();
		packet.append("RESTRICCIONES B --- " + _nombre + " --- " + _restriccionesBCharacter);
		packet.append(
				"\n" + RB_PUEDE_SER_AGREDIDO + " PUEDDE SER AGREDIDO : " + getRestriccionB(RB_PUEDE_SER_AGREDIDO));
		packet.append(
				"\n" + RB_PUEDE_SER_DESAFIADO + " PUEDE SER DESAFIADO : " + getRestriccionB(RB_PUEDE_SER_DESAFIADO));
		packet.append("\n" + RB_PUEDE_HACER_INTERCAMBIO + " PUEDE HACER INTERCAMBIO : "
				+ getRestriccionB(RB_PUEDE_HACER_INTERCAMBIO));
		packet.append("\n" + RB_PUEDE_SER_ATACADO + " PUEDE SER ATACADO : " + getRestriccionB(RB_PUEDE_SER_ATACADO));
		packet.append("\n" + RB_PUEDE_CORRER + " PUEDE CORRER : " + getRestriccionB(RB_PUEDE_CORRER));
		packet.append("\n" + RB_NO_ES_FANTASMA + " NO ES FANTASMA : " + getRestriccionB(RB_NO_ES_FANTASMA));
		packet.append("\n" + RB_PUEDE_SWITCH_MODO_CRIATURA + " PUEDE SWITCH MODO CRIATURA : "
				+ getRestriccionB(RB_PUEDE_SWITCH_MODO_CRIATURA));
		packet.append("\n" + RB_NO_ES_TUMBA + " NO ES TUMBA : " + getRestriccionB(RB_NO_ES_TUMBA));
		return packet.toString();
	}

	public void setGrupoKoliseo(final GrupoKoliseo koli) {
		_koliseo = koli;
		if (koli != null) {
			GestorSalida.ENVIAR_kCK_CREAR_KOLISEO(this);
			GestorSalida.ENVIAR_kM_TODOS_MIEMBROS_KOLISEO(this, koli);
		}
	}

	public GrupoKoliseo getGrupoKoliseo() {
		return _koliseo;
	}

	private void refrescarParteSetClase(boolean enviarSiOSi) {
		ArrayList<Integer> tiene = null;
		if (_totalStats.getStatsObjetos().getStatHechizos() != null) {
			tiene = new ArrayList<>();
			for (final String stat : _totalStats.getStatsObjetos().getStatHechizos()) {
				try {
					final String[] val = stat.split("#");
					final int efecto = Integer.parseInt(val[0], 16);
					final int hechizoID = Integer.parseInt(val[1], 16);
					int modif = 1;
					switch (efecto) {
						case Constantes.STAT_HECHIZO_CLASE_DESACTIVA_LINEA_DE_VUELO:
						case Constantes.STAT_HECHIZO_CLASE_DESACTIVA_LINEA_RECTA:
							break;
						default:
							modif = Integer.parseInt(val[3], 16);
							break;
					}
					final String modificacion = efecto + ";" + hechizoID + ";" + modif;
					tiene.add(hechizoID);
					if (!_bonusSetDeClase.containsKey(hechizoID)) {
						_bonusSetDeClase.put(hechizoID, new Duo<Integer, Integer>(efecto, modif));
						if (_enLinea) {
							GestorSalida.ENVIAR_SB_HECHIZO_BOOST_SET_CLASE(this, modificacion);
						}
					}
				} catch (Exception e) {
				}
			}
		}
		if (!_bonusSetDeClase.isEmpty()) {
			ArrayList<Integer> noTiene = new ArrayList<>();
			for (int hechizoID : _bonusSetDeClase.keySet()) {
				if (tiene == null || !tiene.contains(hechizoID)) {
					noTiene.add(hechizoID);
				}
			}
			for (int hechizoID : noTiene) {
				int efecto = _bonusSetDeClase.get(hechizoID)._primero;
				String modificacion = efecto + ";" + hechizoID;
				_bonusSetDeClase.remove(hechizoID);
				if (_enLinea) {
					GestorSalida.ENVIAR_SB_HECHIZO_BOOST_SET_CLASE(this, modificacion);
				}
			}
		}
	}

	public boolean tieneModfiSetClase(int hechizoID) {
		return _bonusSetDeClase.containsKey(hechizoID);
	}

	public int getModifSetClase(final int hechizoID, final int efecto) {
		if (_bonusSetDeClase.containsKey(hechizoID) && _bonusSetDeClase.get(hechizoID)._primero == efecto) {
			return _bonusSetDeClase.get(hechizoID)._segundo;
		}
		return 0;
	}

	public int getEmotes() {
		return _emotes;
	}

	public boolean tieneEmote(final int emote) {
		final int valor = (int) Math.pow(2, emote - 1);
		return (_emotes & valor) != 0;
	}

	public boolean addEmote(final byte emote) {
		final int valor = (int) Math.pow(2, emote - 1);
		if ((_emotes & valor) != 0) {
			return false;
		}
		_emotes += valor;
		if (_emotes < 0) {
			_emotes = 0;
		} else if (_emotes > 7667711) {
			_emotes = 7667711;
		}
		return true;
	}

	public boolean borrarEmote(final byte emote) {
		final int valor = (int) Math.pow(2, emote - 1);
		if ((_emotes & valor) == 0) {
			return false;
		}
		_emotes -= valor;
		if (_emotes < 0) {
			_emotes = 0;
		} else if (_emotes > 7667711) {
			_emotes = 7667711;
		}
		return true;
	}

	public boolean esMercante() {
		return _esMercante;
	}

	public void setMercante(final boolean mercante) {
		_esMercante = mercante;
	}

	public int getMushinPiso() {
		return _mapaMuishin;
	}

	public void setMushinPiso(int ptoSalvada) {
		this._mapaMuishin = ptoSalvada;
	}

	public void setPuntoSalvada(String ptoSalvada) {
		try {
			final String[] infos = ptoSalvada.split(",");
			_mapaSalvada = Short.parseShort(infos[0]);
			_celdaSalvada = Short.parseShort(infos[1]);
			Mundo.getMapa(_mapaSalvada).getCelda(_celdaSalvada).getID();
		} catch (final Exception e) {
			_mapaSalvada = 7411;
			_celdaSalvada = 340;
		}
	}
	/*
	 * public Personaje getCompañero() { return _compañero; }
	 *
	 * boolean compañeronull = false;
	 *
	 * public void setCompañero(Personaje compañero) { if (compañero != null)
	 * compañeronull = true; _compañero = compañero; }
	 */

	public CopyOnWriteArrayList<Personaje> getCompañeros() {

		return compañeros;
	}

	public void addCompañero(Personaje compañero) {
		compañeros.add(compañero);
	}

	public boolean esMultiman() {
		return _claseID == Constantes.CLASE_MULTIMAN;
	}

	private void reiniciarSubStats(Map<Integer, Integer> map) {
		map.clear();
		map.put(Constantes.STAT_MAS_VITALIDAD, 0);
		map.put(Constantes.STAT_MAS_FUERZA, 0);
		map.put(Constantes.STAT_MAS_SABIDURIA, 0);
		map.put(Constantes.STAT_MAS_INTELIGENCIA, 0);
		map.put(Constantes.STAT_MAS_SUERTE, 0);
		map.put(Constantes.STAT_MAS_AGILIDAD, 0);
	}
	//Todo en una cuenta
	public ArrayList<Personaje> Multis = new ArrayList<>();
	public Personaje LiderIP;
	public boolean EsliderIP = false;
	//
	public Personaje(final int id, final String nombre, final byte sexo, final byte claseID, final int color1,
					 final int color2, final int color3, final long kamas, final int puntosHechizo, final int capital,
					 final int energia, final short nivel, final String exp, final int talla, final int gfxID,
					 final byte alineacion, final int cuenta, final Map<Integer, Integer> statsBase,
					 final Map<Integer, Integer> statsScroll, final boolean mostrarAmigos, final boolean mostarAlineacion,
					 final String canal, final short mapa, final short celda, final String inventario, final int porcPDV,
					 final String hechizos, final String ptoSalvada, final String oficios, final byte porcXPMontura,
					 final int montura, final int honor, final int deshonor, final byte gradoAlineacion, final String zaaps,
					 final int esposoID, final String tienda, final boolean mercante, final int restriccionesA,
					 final int restriccionesB, final int encarnacion, final int emotes, final String titulos,
					 final String tituloVIP, final String ornamentos, final String misiones, final String coleccion,
					 final byte resets, final String almanax, final int ultimoNivel, final String setsRapidos,
					 final int colorNombre, final String orden, final boolean carcel, final int rostro, final int nivelOmega,
					 String pasebatalla, final int nbFightWin, final int nbFightLose, final int nbFight, final String allSucess,
					 final int points, int nbQuests, String mobsJefes, long OgGanadas, long OgGastadas, int NobjDiarios,
					 String kolis, String MisAura, int AuraActual, int traje, int mushinTower, final int pvpQuest) {
		_cuenta = Mundo.getCuenta(cuenta);
		try {
			boolean modificar = false;
			try {
				_mapa = Mundo.getMapa(mapa);
				setCelda(_mapa.getCelda(celda));
			} catch (Exception e) {
				_mapa = Mundo.getMapa((short) 7411);
				setCelda(_mapa.getCelda((short) 200));
			}
			if (_mapa == null || _celda == null) {
				MainServidor.redactarLogServidorln("Mapa o celda invalido del personaje " + nombre
						+ ", por lo tanto se cierra el server, mapa: " + mapa + " celda: " + celda);
				System.exit(1);
				return;
			}
			_id = id;
			_nombre = nombre;
			_colorNombre = colorNombre;
			_sexo = sexo;
			_claseID = claseID;
			_clase = Mundo.getClase(_claseID);
			_color1 = color1;
			_color2 = color2;
			_color3 = color3;
			_nivelOmega = nivelOmega;
			_subStatsBase.putAll(statsBase);
			_puntosHechizos = puntosHechizo;
			_puntosStats = capital;
			_energia = energia;
			_talla = talla;
			_gfxID = gfxID;
			nbStalkWin = pvpQuest;
			this._rostroGFX = rostro;
			if (traje > 0) {
				_gfxID = traje;
			} else if (this._rostroGFX > 0) {
				_gfxID = rostro;
			}
			_restriccionesALocalPlayer = restriccionesA;
			_restriccionesBCharacter = restriccionesB;
			_ultimoNivel = ultimoNivel;
			_canales = canal;
			if (MainServidor.PARAM_REINICIAR_CANALES) {
				_canales = "*#%!pi$:?^¡@~";
				modificar = true;
			}
			_esposoID = esposoID;
			_resets = resets;
			experiencia = new BigInteger(exp);
			_nivel = nivel;
			this.nbFightWin = nbFightWin;
			this.nbFightLose = nbFightLose;
			this.nbFight = nbFight;
			this.nbQuests = nbQuests;
			_alineacion = alineacion;
			if (_alineacion != Constantes.ALINEACION_NEUTRAL) {
				_honor = honor;
				_deshonor = deshonor;
				_gradoAlineacion = gradoAlineacion;
				if (MainServidor.HONOR_FIJO_PARA_TODOS > -1) {
					_honor = MainServidor.HONOR_FIJO_PARA_TODOS;
					refrescarGradoAlineacion();
				}
			}
			if (orden.isEmpty()) {
				_ordenNivel = 0;
				switch (_alineacion) {
					case Constantes.ALINEACION_BONTARIANO:
						_orden = 1;
						break;
					case Constantes.ALINEACION_BRAKMARIANO:
						_orden = 5;
						break;
					case Constantes.ALINEACION_MERCENARIO:
						_orden = 9;
						break;
					default:
						_orden = 0;
						break;
				}
			} else {
				String[] ord = orden.split(",");
				try {
					_orden = Integer.parseInt(ord[0]);
					_ordenNivel = Integer.parseInt(ord[1]);
				} catch (Exception e) {
				}
			}
			actualizarStatsEspecialidad(Mundo.getEspecialidad(_orden, _ordenNivel));
			if (MainServidor.PARAM_START_EMOTES_COMPLETOS) {
				_emotes = 7667711;
			} else {
				_emotes = emotes;
			}
			if (montura < -1) {
				setMontura(Mundo.getMontura(montura));
			}
			if (!mobsJefes.isEmpty()) {
				String jefes = mobsJefes.replace("{", "").replace("}", "").replace(" ", "");
				if (!jefes.isEmpty())
					for (String a : jefes.split(",")) {
						try {
							this._mobsJefes.put(Integer.parseInt(a.split("=")[0]), Integer.parseInt(a.split("=")[1]));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
			}
			setPorcXPMontura(porcXPMontura);
			_subStatsScroll.putAll(statsScroll);
			_totalStats.getStatsBase().nuevosStatsBase(_subStatsBase, this);
			_totalStats.getStatsBase().acumularStats(_subStatsScroll);
			_calabozo = carcel;
			this.OgGanadas = OgGanadas;
			this.OgGastadas = OgGastadas;
			this._NobjDiarios = NobjDiarios;
			addKamas(kamas, false, false);
			this.setMushinPiso(mushinTower);
			setPuntoSalvada(ptoSalvada);
			setMisiones(misiones);
			this.setCurAura(AuraActual);
			if (!MisAura.isEmpty()) {
				for (String a : MisAura.split(",")) {
					try {
						allAura.add(Integer.parseInt(a));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			final String[] tArray = titulos.split(Pattern.quote(","));
			for (String t : tArray) {
				if (t.isEmpty()) {
					continue;
				}
				try {
					String[] tt = t.split(Pattern.quote("*"));
					int titulo = Integer.parseInt(tt[0]);
					int color = -1;
					if (tt.length > 1) {
						color = Integer.parseInt(tt[1]);
					}
					_titulos.put(titulo, color);
					if (t.contains("+")) {
						_titulo = titulo;
					}
				} catch (Exception e) {
				}
			}
			_tituloVIP = tituloVIP;
			for (final String str : ornamentos.split(",")) {
				if (str.isEmpty()) {
					continue;
				}
				try {
					int ornamento = Integer.parseInt(str);
					_ornamentos.add(ornamento);
					if (str.contains("+")) {
						_ornamento = ornamento;
					}
				} catch (final Exception e) {
				}
			}
			_ornamentos.trimToSize();
			for (final String str : coleccion.split(",")) {
				if (str.isEmpty()) {
					continue;
				}
				try {
					_cardMobs.add(Integer.parseInt(str));
				} catch (final Exception e) {
				}
			}
			_cardMobs.trimToSize();
			if (MainServidor.PARAM_PERMITIR_DESACTIVAR_ALAS) {
				_mostrarAlas = mostarAlineacion;
			} else {
				_mostrarAlas = _alineacion != Constantes.ALINEACION_NEUTRAL;
			}
			_mostrarAmigos = mostrarAmigos;
			for (final String str : zaaps.split(",")) {
				try {
					_zaaps.add(Integer.parseInt(str));
				} catch (final Exception e) {
				}
			}
			try {
				_kolirango = Integer.parseInt(kolis.split(";")[0]);
			} catch (Exception e1) {
				_kolirango = 1;
			}
			try {
				_kolipuntos = Integer.parseInt(kolis.split(";")[1]);
			} catch (Exception e1) {
				_kolipuntos = 0;
			}
			_zaaps.trimToSize();
			for (final String str : almanax.split(",")) {
				try {
					_almanax.add(Integer.parseInt(str));
				} catch (final Exception e) {
				}
			}
			_almanax.trimToSize();
			this._pointsSucces = points;
			if (!allSucess.isEmpty()) {
				for (String a : allSucess.split(",")) {
					try {
						this.getAllSucess().add(Integer.parseInt(a));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			for (final String idObjeto : inventario.split(Pattern.quote("|"))) {
				try {
					if (idObjeto.isEmpty()) {
						continue;
					}
					Objeto obj = Mundo.getObjeto(Integer.parseInt(idObjeto));
					//objeto_Desasociar_Mimobionte(obj);
					if (obj.getDueñoTemp() == 0) {
						obj.setDueñoTemp(_id);
						addObjetoConOAKO(obj, false);
					} else {
						modificar = true;
						MainServidor.redactarLogServidorln("El objetoID " + idObjeto + " tiene dueño "
								+ (obj.getDueñoTemp()) + " no se puede agregar a " + _nombre + "(" + _id + ")");
					}
				} catch (Exception e) {
					modificar = true;
					MainServidor.redactarLogServidorln(
							"El objetoID " + idObjeto + " pertenece a " + _nombre + "(" + _id + ")" + ", no existe");
				}
			}
			for (final String idObjeto : tienda.split(Pattern.quote("|"))) {
				try {
					if (idObjeto.isEmpty()) {
						continue;
					}
					Objeto obj = Mundo.getObjeto(Integer.parseInt(idObjeto));
					//objeto_Desasociar_Mimobionte(obj);
					if (obj.getDueñoTemp() == 0) {
						obj.setDueñoTemp(_id);
						if (obj.getPrecio() <= 0) {
							addObjetoConOAKO(obj, false);
						} else {
							_tienda.addObjeto(obj);
						}
					} else {
						modificar = true;
						MainServidor.redactarLogServidorln("La tiendaID " + idObjeto + " tiene dueño "
								+ (obj.getDueñoTemp()) + " no se puede agregar a " + _nombre + "(" + _id + ")");
					}
				} catch (Exception e) {
					modificar = true;
					MainServidor.redactarLogServidorln(
							"El objetoID " + idObjeto + " pertenece a " + _nombre + "(" + _id + ")" + ", no existe");
				}
			}
			for (Logro logro : Mundo.LOGROS.values()) {
				LogroPersonaje log = new LogroPersonaje(logro, 0, 0);
				_logros.add(log);
			}
			if (!pasebatalla.equals("")) {
				String[] splitx = pasebatalla.split(";");
				if (!splitx[0].isEmpty()) {
					for (String str : splitx[0].split(",")) {
						Canjeados.add(Integer.parseInt(str));
					}
				}
				PuntosPrestigio = Integer.parseInt(splitx[1]);
				paginaCanj = Integer.parseInt(splitx[2]);
			}

			// buffClase = clase;
			// boolean mensaje = false;
			// StringBuilder str = new StringBuilder();
			// ArrayList<Objeto> objetos = new ArrayList<>();
			// objetos.addAll(_objetos.values());
			// objetos.addAll(_tienda);
			// for (Objeto o : objetos) {
			// if (o._reseteado) {
			// mensaje = true;
			// if (str.length() > 0)
			// str.append(",");
			// str.append("6962");
			// }
			// }
			// if (mensaje) {
			// _cuenta.addRegalo(str.toString());
			// _cuenta
			// .addMensaje(
			// "1223;Tus objetos magueados se han puesto con los stats base debido a una
			// modificación en
			// la forjamagia muy importante, así evitarémos el over magueo en el servidor
			// ANKALIKE. Te
			// hemos dejado un regalo por cada objeto que te modificamos.",
			// true);
			// }
			if (MainServidor.PARAM_RESET_STATS_PLAYERS) {
				modificar = true;
				resetearStats(true);
			}
			try {
				Titulo titulo = Mundo.getTitulo(_titulo);
				if (titulo.esVip()) {
					if (!getCuenta().esAbonado()) {
						setTitulo(0);
					}
				}
			} catch (Exception e) {
			}
			if (_cuenta != null) {
				_esMercante = mercante;
				if (_esMercante) {
					if (!_tienda.estaVacia()) {
						_mapa.addMercante(this);
					} else {
						_esMercante = false;
					}
				}
				// se pone en la creacion para considerar como si recien se hubiera desconectado
				_tiempoDesconexion = System.currentTimeMillis();
				analizarPosHechizos(hechizos);
				if (MainServidor.PARAM_PERMITIR_OFICIOS) {
					_statsOficios.put((byte) 7, new StatOficio((byte) 7, Mundo.getOficio(1), 0));
					if (!oficios.isEmpty()) {
						for (final String data : oficios.split(";")) {
							try {
								final String[] infos = data.split(",");
								aprenderOficio(Mundo.getOficio(Integer.parseInt(infos[0])), Integer.parseInt(infos[1]));
							} catch (final Exception e) {
							}
						}
					}
				}
				try {
					for (String s : setsRapidos.split(Pattern.quote("*"))) {
						if (s.isEmpty()) {
							continue;
						}
						String[] split = s.split(Pattern.quote("|"));
						int idSet = Integer.parseInt(split[0]);
						String nombreSet = split[1];
						int iconoSet = Integer.parseInt(split[2]);
						String dataSet = split[3];
						addSetRapido(idSet, nombreSet, iconoSet, dataSet);
					}
				} catch (Exception e) {
				}
				if (_energia > 10000) {
					_energia = 10000;
				} else if (_energia < 0 && !esTumba()) {
					convertirseTumba();
				} else if (_energia == 0 && !esFantasma()) {
					convertirseFantasma();
				}
				if (!MainServidor.PARAM_PERDER_ENERGIA) {
					_energia = 10000;
				}
				actualizarPDV(porcPDV);
				if (modificar) {
					GestorSQL.SALVAR_PERSONAJE(this, false);
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			if (_cuenta == null) {
				MainServidor.redactarLogServidorln(
						"SE DEBE ELIMINAR PERSONAJE " + nombre + " (" + id + ") - CUENTA " + cuenta);
				if (MainServidor.PARAM_ELIMINAR_PERSONAJES_BUG) {
					Mundo.eliminarPersonaje(this, true);
				}
			} else {
				_cuenta.addPersonaje(this);
			}
		}
	}

	/*private void objeto_Desasociar_Mimobionte(Objeto objeto) {
		try {

			if(objeto.tieneStatTexto(Constantes.STAT_APARIENCIA_OBJETO)) {
				int regresar = Integer.parseInt(objeto.getParamStatTexto(Constantes.STAT_APARIENCIA_OBJETO, 3), 16);
				addObjIdentAInventario(Mundo.getObjetoModelo(regresar).crearObjeto(1,Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM), false);
				objeto.addStatTexto(Constantes.STAT_APARIENCIA_OBJETO, "");
				System.out.println("Personaje: "+_nombre+" disasicion objeto: "+objeto.getObjModelo().getNombre());
			}
		} catch (Exception e) {
		}
	}*/

	public synchronized static Personaje crearPersonaje(final String nombre, byte sexo, byte claseID, int color1,
														int color2, int color3, int gfx, final Cuenta cuenta) {
		try {
			color1 = Math.min(16777215, Math.max(-1, color1));
			color2 = Math.min(16777215, Math.max(-1, color2));
			color3 = Math.min(16777215, Math.max(-1, color3));
			sexo = (sexo != Constantes.SEXO_MASCULINO ? Constantes.SEXO_FEMENINO : Constantes.SEXO_MASCULINO);
			if (Mundo.getClase(claseID) == null) {
				claseID = 1;
			}
			Clase clase = Mundo.getClase(claseID);
			final StringBuilder zaaps = new StringBuilder();
			for (final String zaap : MainServidor.INICIO_ZAAPS.split(",")) {
				try {
					if (zaap.isEmpty()) {
						continue;
					}
					if (Mundo.getCeldaZaapPorMapaID(Short.parseShort(zaap)) == -1) {
						continue;
					}
					if (zaaps.length() > 0) {
						zaaps.append(",");
					}
					zaaps.append(zaap);
				} catch (Exception e) {
				}
			}
			long kamas = 0;
			final StringBuilder objetos = new StringBuilder();
			final int nivel = MainServidor.INICIO_NIVEL;
			if (!MainServidor.PARAM_SOLO_PRIMERA_VEZ || cuenta.getPrimeraVez() == 1) {
				// cuenta.addKamasBanco(MainServidor.KAMAS_BANCO);
				for (final String str : MainServidor.INICIO_OBJETOS.split(";")) {
					try {
						if (str.isEmpty()) {
							continue;
						}
						String[] arg = str.split(",");
						final Objeto obj = Mundo.getObjetoModelo(Integer.parseInt(arg[0])).crearObjeto(
								Integer.parseInt(arg[1]), Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.MAXIMO);
						Mundo.addObjeto(obj, false);
						try {
							if (arg.length > 2) {
								byte pos = Byte.parseByte(arg[2]);
								obj.setPosicion(pos);
							}
						} catch (Exception e) {
						}
						if (objetos.length() > 0) {
							objetos.append("|");
						}
						objetos.append(obj.getID());
					} catch (final Exception e) {
					}
				}
				for (final String str : MainServidor.INICIO_SET_ID.split(",")) {
					if (str.isEmpty()) {
						continue;
					}
					final ObjetoSet objSet = Mundo.getObjetoSet(Integer.parseInt(str));
					if (objSet != null) {
						for (final ObjetoModelo OM : objSet.getObjetosModelos()) {
							final Objeto x = OM.crearObjeto(1, Constantes.OBJETO_POS_NO_EQUIPADO,
									CAPACIDAD_STATS.MAXIMO);
							Mundo.addObjeto(x, false);
							if (objetos.length() > 0) {
								objetos.append("|");
							}
							objetos.append(x.getID());
						}
					}
				}
				kamas += MainServidor.INICIO_KAMAS;
				cuenta.setPrimeraVez();
			}
			short mapaID = clase.getMapaInicio();
			short celdaID = clase.getCeldaInicio();
			Mapa mapa = Mundo.getMapa(mapaID);
			if (mapa == null) {
				mapaID = 7411;
				celdaID = 340;
			}
			String puntoSalvada = mapaID + "," + celdaID;
			int id = Mundo.sigIDPersonaje();
			String oficios = "27,1000000;15,1000000;16,1000000;17,1000000;28,1000000;20,1000000;1,1000000;65,1000000;2,1000000;11,1000000;13,1000000;14,1000000;18,1000000;19,1000000;24,1000000;25,1000000;26,1000000;31,1000000;36,1000000;41,1000000;56,1000000;58,1000000;60,1000000";
			int puntosHechizo = (nivel - 1) * MainServidor.PUNTOS_HECHIZO_POR_NIVEL;
			int puntosStats = ((nivel - 1) * MainServidor.PUNTOS_STATS_POR_NIVEL) + MainServidor.INICIO_PUNTOS_STATS;
			int gfxID = clase.getGfxs(sexo);
			if (gfx > 0) {
				gfxID = gfx;// Si el rostro esta en 0 no hay peo? esto e smas que todo es para mi server ya
				// que el tuyo siempre estara con gfx con lo nuevo ok
			}
			int talla = clase.getTallas(sexo);
			String xp = Mundo.getExpPersonaje(nivel, 0) + "";
			int emotes = MainServidor.INICIO_EMOTES;
			final Personaje nuevoPersonaje = new Personaje(id, nombre, sexo, claseID, color1, color2, color3, kamas,
					puntosHechizo, puntosStats, nivel, xp, talla, gfxID, cuenta.getID(), mapaID, celdaID,
					objetos.toString(), puntoSalvada, zaaps.toString(), emotes, gfx, oficios);
			Mundo.addPersonaje(nuevoPersonaje);
			GestorSQL.SALVAR_PERSONAJE(nuevoPersonaje, true);
			nuevoPersonaje.primerRefresh = true;
			return nuevoPersonaje;
		} catch (final Exception e) {
			MainServidor.redactarLogServidorln("EXCEPTION crearPersonaje " + e.toString());
			e.printStackTrace();
			return null;
		}
	}

	public Personaje(final int id, final MobGrado mobModelo) {
		Stats stats = new Stats();
		_totalStats = new TotalStats(new Stats(), new Stats(), new Stats(), new Stats(), 2);
		MobGrado mobGrado = mobModelo;
		for (Entry<Integer, Integer> entry : mobGrado.getStats().getEntrySet()) {
			int valor = entry.getValue();
			stats.addStatID(entry.getKey(), valor);
		}
		int PDV = mobGrado.getPDVMax();
		_id = id;
		_nombre = mobModelo.getMobModelo().getNombre();
		_claseID = Constantes.CLASE_MULTIMAN;
		_nivel = mobGrado.getNivel();
		_gfxID = mobModelo.getGfxID(true);
		_talla = mobModelo.getMobModelo().getTalla();
		_totalStats.getStatsBase().nuevosStats(stats);
		_PDVMax = _PDV = PDV;
		_cuenta = null;
		int i = 1;
		for (Entry<Integer, StatHechizo> entry : mobGrado.getHechizos().entrySet()) {
			StatHechizo st = entry.getValue();
			if (st == null) {
				continue;
			}
			addHechizoPersonaje(Encriptador.getValorHashPorNumero(i), st.getHechizo(), st.getGrado());
			i++;
		}
		_mapStatsHechizos = new HashMap<>();
		_mapStatsHechizos.putAll(mobGrado.getHechizos());
	}

	public static Personaje crearInvoControlable(final int id, final MobGrado grado) {
		Personaje multiman = new Personaje(id, grado);
		multiman.invocacionControlable = true;
		return multiman;
	}

	public Personaje(final int id, final String nombre, final byte sexo, final byte claseID, final int color1,
					 final int color2, final int color3, final long kamas, final int puntosHechizo, final int capital,
					 final int nivel, final String exp, final int talla, final int gfxID, final int cuenta, final short mapa,
					 final short celda, final String inventario, final String ptoSalvada, final String zaaps, final int emotes,
					 final int gfx, final String oficios) {
		// personaje recien creado
		_cuenta = Mundo.getCuenta(cuenta);
		_id = id;
		_nombre = nombre;
		_sexo = sexo;
		_claseID = claseID;
		_clase = Mundo.getClase(_claseID);
		_color1 = color1;
		_color2 = color2;
		_color3 = color3;
		_puntosHechizos = puntosHechizo;
		_puntosStats = capital;
		_talla = talla;
		_gfxID = gfxID;
		experiencia = new BigInteger(exp);
		_nivel = nivel;
		_resets = 1;
		reiniciarSubStats(_subStatsBase);
		reiniciarSubStats(_subStatsScroll);
		_totalStats.getStatsBase().nuevosStatsBase(_subStatsBase, this);
		_totalStats.getStatsBase().acumularStats(_subStatsScroll);
		addKamas(kamas, false, false);
		setPuntoSalvada(ptoSalvada);
		if (MainServidor.PARAM_START_EMOTES_COMPLETOS) {
			_emotes = 7667711;
		} else {
			_emotes = emotes;
		}
		if (!MainServidor.PARAM_PERMITIR_DESACTIVAR_ALAS) {
			_mostrarAlas = _alineacion != Constantes.ALINEACION_NEUTRAL;
		}
		for (Logro logro : Mundo.LOGROS.values()) {
			LogroPersonaje log = new LogroPersonaje(logro, 0, 0);
			_logros.add(log);
		}
		for (final String str : zaaps.split(",")) {
			try {
				_zaaps.add(Integer.parseInt(str));
			} catch (final Exception e) {
			}
		}
		_zaaps.trimToSize();
		for (final String idObjeto : inventario.split(Pattern.quote("|"))) {
			try {
				if (idObjeto.isEmpty()) {
					continue;
				}
				Objeto obj = Mundo.getObjeto(Integer.parseInt(idObjeto));
				if (obj.getDueñoTemp() == 0) {
					obj.setDueñoTemp(_id);
					// se agrega el objeto al array _objPos
					addObjetoConOAKO(obj, false);
				} else {
					MainServidor.redactarLogServidorln("El objetoID " + idObjeto + " tiene dueño "
							+ (obj.getDueñoTemp()) + " no se puede agregar a " + _nombre + "(" + _id + ")");
				}
			} catch (Exception e) {
				MainServidor.redactarLogServidorln(
						"El objetoID " + idObjeto + " pertenece a " + _nombre + "(" + _id + ")" + ", no existe");
			}
		}
		if (MainServidor.PARAM_RESET_STATS_PLAYERS) {
			resetearStats(true);
		}
		fullPDV();
		if (MainServidor.PARAM_PERMITIR_OFICIOS) {
			_statsOficios.put((byte) 7, new StatOficio((byte) 7, Mundo.getOficio(1), 0));
			if (!oficios.isEmpty()) {
				for (final String data : oficios.split(";")) {
					try {
						final String[] infos = data.split(",");
						aprenderOficio(Mundo.getOficio(Integer.parseInt(infos[0])), Integer.parseInt(infos[1]));
					} catch (final Exception e) {
					}
				}
			}
		}
		fijarHechizosInicio();
		_recienCreado = true;
		_mapa = Mundo.getMapa(mapa);
		setCelda(_mapa.getCelda(celda));
		_rostroGFX = gfx;
	}

	// Doble
	public static Personaje crearClon(final Personaje perso, final int id) {
		boolean mostrarAlas = false;
		int gradoAlineacion = 0;
		if (perso.alasActivadas()) {
			mostrarAlas = true;
			gradoAlineacion = perso.getGradoAlineacion();
		}
		final Personaje clon = new Personaje(id, perso.getNombre(), perso._sexo, perso._claseID, perso._color1,
				perso._color2, perso._color3, perso._nivel, perso._talla, perso._rostroGFX, perso._totalStats,
				perso.getPorcPDV(), perso.getPDVMax(), mostrarAlas, gradoAlineacion, perso._alineacion,
				(perso._montando && perso._montura != null) ? perso._montura : null, perso._objPos49);
		clon.getTotalStats().getStatsBase().addStatID(Constantes.STAT_MAS_PLACAJE, MainServidor.CLON_PLACAHE_HUIDA);
		clon.getTotalStats().getStatsBase().addStatID(Constantes.STAT_MAS_HUIDA, MainServidor.CLON_PLACAHE_HUIDA);
		return clon;
	}

	// CLON
	public Personaje(final int id, final String nombre, final byte sexo, final byte clase, final int color1,
					 final int color2, final int color3, final int nivel, final int talla, final int gfxID,
					 final TotalStats totalStats, final float porcPDV, final int pdvMax, final boolean mostarAlineacion,
					 final int gradoAlineacion, final byte alineacion, Montura montura, Objeto[] objPos) {
		// crear clon
		_id = id;
		_nombre = nombre;
		_sexo = sexo;
		_claseID = clase;
		_clase = Mundo.getClase(_claseID);
		_color1 = color1;
		_color2 = color2;
		_color3 = color3;
		_nivel = nivel;
		_gradoAlineacion = gradoAlineacion;
		_alineacion = alineacion;
		_talla = talla;
		_gfxID = gfxID;
		_totalStats.getStatsBase().nuevosStats(totalStats.getStatsBase());
		_totalStats.getStatsObjetos().nuevosStats(totalStats.getStatsObjetos());
		if (MainServidor.PARAM_PERMITIR_DESACTIVAR_ALAS) {
			_mostrarAlas = mostarAlineacion;
		} else {
			_mostrarAlas = _alineacion != Constantes.ALINEACION_NEUTRAL;
		}
		actualizarPDV(porcPDV);
		if (montura != null) {
			_montando = true;
			_montura = montura;
		}
		if (objPos != null) {
			for (Objeto obj : objPos) {
				if (obj == null) {
					continue;
				}
				_objPos49[obj.getPosicion()] = obj;
			}
		}
		_cuenta = null;
	}

	MobModelo MobAyudante;

	public static Personaje crearMultiman(final int id, final int nivel, int iniciativa, final MobModelo mobModelo) {
		Personaje multiman = new Personaje(id, nivel, iniciativa, mobModelo);
		multiman.MobAyudante = mobModelo;
		return multiman;
	}

	public Personaje(final int id, final int nivel, int iniciativa, final MobModelo mobModelo) {
		Stats stats = new Stats();
		stats.fijarStatID(Constantes.STAT_MAS_PA, 6);
		stats.fijarStatID(Constantes.STAT_MAS_PM, 3);
		MobGradoModelo mobGrado = mobModelo.getGradoPorGrado((byte) 1);
		for (Entry<Integer, Integer> entry : mobGrado.getStats().getEntrySet()) {
			int valor = entry.getValue();
			switch (entry.getKey()) {
				case Constantes.STAT_MAS_PA:
					valor -= 6;
					break;
				case Constantes.STAT_MAS_PM:
					valor -= 3;
					break;
				case Constantes.STAT_MAS_INICIATIVA:
					continue;
			}
			stats.addStatID(entry.getKey(), valor * nivel / MainServidor.NIVEL_MAX_PERSONAJE);
		}
		stats.addStatID(Constantes.STAT_MAS_INICIATIVA, iniciativa / 2);
		int PDV = mobGrado.getPDVMAX() * nivel / MainServidor.NIVEL_MAX_PERSONAJE;
		_id = id;
		_nombre = mobModelo.getNombre();
		_claseID = Constantes.CLASE_MULTIMAN;
		_nivel = nivel;
		_gfxID = mobModelo.getGfxID();
		_talla = mobModelo.getTalla();
		_totalStats.getStatsBase().nuevosStats(stats);
		_PDVMax = _PDV = PDV;
		_cuenta = null;
		int i = 1;
		for (Entry<Integer, StatHechizo> entry : mobGrado.getHechizos().entrySet()) {
			StatHechizo st = entry.getValue();
			if (st == null) {
				continue;
			}
			addHechizoPersonaje(Encriptador.getValorHashPorNumero(i), st.getHechizo(), st.getGrado());
			i++;
		}
		_mapStatsHechizos = new HashMap<Integer, StatHechizo>();
		_mapStatsHechizos.putAll(mobGrado.getHechizos());
	}

	public void conectarse() {
		if (LiderIP != null) {
			if (LiderIP.Multi == this) {
				LiderIP.Multi = null;
			}
			if (LiderIP.getServidorSocket() != null) {
				LiderIP.getServidorSocket().setPersonaje(LiderIP);
				if (LiderIP.Multi == null) {
					LiderIP.sendMessage("El personaje " + _nombre + " Ha dejado de ser parte del multi, usa .maestro otra vez");
				} else {
					LiderIP.Multi.sendMessage("El personaje " + _nombre + " Ha dejado de ser parte del multi, usa .maestro otra vez");
				}
			}
			LiderIP.Multis.remove(this);
		}
		if (!Multis.isEmpty()) {
			for (Personaje p :
					Multis) {
				p.LiderIP = null;
				p.Multi = null;
				p.EsliderIP = false;
			}
		}
		Multis.clear();
		LiderIP = null;
		Multi = null;
		EsliderIP = false;
		if (_cuenta.getSocket() == null) {
			MainServidor.redactarLogServidorln("El personaje " + _nombre + " tiene como entrada personaje NULL");
			return;
		}
		_cuenta.setTempPerso(this);
		setEnLinea(true);
		if (_esMercante) {
			_mapa.removerMercante(_id);
			_esMercante = false;
			GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(_mapa, _id);
		}
		if (_montura != null)
			GestorSalida.ENVIAR_Re_DETALLES_MONTURA(this, "+", _montura);
		GestorSalida.ENVIAR_Rx_EXP_DONADA_MONTURA(this);
		addPuntosPorDesconexion();
		if (_titulo == 55 && Mundo._liderRankingPvp != this.getNombre()
				|| _titulo == 56 && Mundo._liderRankingPvp1 != this.getNombre()
				|| _titulo == 57 && Mundo._liderRankingPvp2 != this.getNombre()) {
			setTitulo(0);
		}
		if (_ornamento == 141 && Mundo._liderRankingPvp != this.getNombre()
				|| _ornamento == 140 && Mundo._liderRankingPvp1 != this.getNombre()
				|| _ornamento == 139 && Mundo._liderRankingPvp2 != this.getNombre()) {
			setOrnamento(0);
		}
		GestorSalida.ENVIAR_ASK_PERSONAJE_SELECCIONADO(this);
		StringBuilder packet = new StringBuilder("");
		packet.append("wK|" + getNivelOmega() + "" + (char) 0x00);
		packet.append("ZS" + _alineacion + "" + (char) 0x00);
		MiembroGremio gm = _miembroGremio;
		if (gm != null) {
			packet.append("gS" + gm.getGremio().getNombre() + "|" + gm.getGremio().getEmblema().replace(',', '|') + "|"
					+ gm.analizarDerechos() + "" + (char) 0x00);
		}
		packet.append("SL" + stringListaHechizos() + "" + (char) 0x00);
		packet.append("eL" + _emotes + "" + (char) 0x00);
		packet.append("Ow" + getPodsUsados() + "|" + getPodsMaximos() + "" + (char) 0x00);
		packet.append("FO" + (_mostrarAmigos ? "+" : "-") + "" + (char) 0x00);
		packet.append("Im" + "189" + "" + (char) 0x00);
		if (!_cuenta.getUltimaConexion().isEmpty() && !_cuenta.getUltimaIP().isEmpty()) {
			String u = _cuenta.getUltimaConexion();
			packet.append("Im" + "0152;" + u.substring(0, u.lastIndexOf("~")) + "~" + _cuenta.getUltimaIP() + ""
					+ (char) 0x00);
		}
		packet.append("Im" + "0153;" + _cuenta.getActualIP() + "" + (char) 0x00);
		packet.append("al|" + Mundo.getAlineacionTodasSubareas() + "" + (char) 0x00);
		packet.append("AR" + getRestriccionesA() + "" + (char) 0x00);
		packet.append("gm" + this.getMapa().celdasTPs + "" + (char) 0x00);
		GestorSalida.enviar(this, packet.toString(), true);
		enviarMsjAAmigos();
		_cuenta.setUltimaIP(_cuenta.getActualIP());
		_cuenta.setUltimaConexion();
		if (_pelea == null) {
			crearTimerRegenPDV();
			setDelayTimerRegenPDV(MainServidor.MODO_BATTLE_ROYALE ? 0 : 1000);
		} else {
			_reconectado = true;
			if (_pelea._espectadores.containsKey(getID()))
				salirEspectador();
			else
				return;
		}
		_casaDentro = Mundo.getCasaDentroPorMapa(_mapa.getID());
		if (_casaDentro != null) {
			GestorSalida.ENVIAR_hL_INFO_CASA(this, _casaDentro.informacionCasa(_id));
		}
		if (!MainServidor.MODO_HEROICO && _energia < 1500) {
			GestorSalida.ENVIAR_M1_MENSAJE_SERVER_SVR_MUESTRA_INSTANTANEO(this, 11, _energia + "", "");
		}
		if (_miembroGremio != null) {
			GestorSalida.ENVIAR_gIG_GREMIO_INFO_GENERAL(this, _miembroGremio.getGremio());
		}
		if(getGremio() != null) {
			if(!getGremio().getAnuncio().isEmpty())
			{
				sendMessage("<b> [Anuncio de gremio] : </b>"+ getGremio().getAnuncio());
			}
		}
		GestorSalida.ENVIAR_El_LISTA_OBJETOS_COFRE_PRECARGADO(this, getBanco());
		// GestorSQL.UPDATE_CUENTA_LOGUEADO(_cuenta.getID(), (byte) 1);
		if (MainServidor.PARAM_ALIMENTAR_MASCOTAS) {
			comprobarMascotas(false);
		}
		activa = true;
		refrescarStuff(true, false, false);// actualizas los stats y refresca stuff
		enviarBonusSet();
		GestorSalida.ENVIAR_Os_SETS_RAPIDOS(this);
		if (_statsOficios.size() > 1) {
			String lista = "";
			String lista2 = "";
			for (final StatOficio statOficio : _statsOficios.values()) {
				if (statOficio.getPosicion() != 7) {
					lista += (statOficio.stringSKillsOficio());

					lista2 += ("|" + statOficio.getOficio().getID() + ";" + statOficio.getNivel() + ";"
							+ statOficio.getExpString(";") + ";");
				}
			}
			StringBuilder packets = new StringBuilder();
			packets.append("JS" + lista + "" + (char) 0x00);
			packets.append("JX" + lista2 + "" + (char) 0x00);
			for (StatOficio sm : _statsOficios.values()) {
				packets.append(
						"JO" + sm.getPosicion() + "|" + sm.getOpcionBin() + "|" + sm.getSlotsPublico() + (char) 0x00);
			}
			GestorSalida.enviar(this, packets.toString(), true);
			// verificarHerramientOficio();
		}
		primerRefresh = true;
		/*
		 * if (buffClase == -1) GestorSalida.enviar(this, "kd", true);
		 */
		_creandoJuego = true;
		if (esFantasma()) {
			revivir(true);
		}
	}

	private void enviarBonusSet() {
		Map<Integer, Integer> map = new TreeMap<>();
		for (byte pos : Constantes.POSICIONES_EQUIPAMIENTO) {
			final Objeto obj = getObjPosicion(pos);
			if (obj == null) {
				continue;
			}
			int setID = obj.getObjModelo().getSetID();
			if (setID < 1) {
				continue;
			}
			int v = 1;
			if (map.containsKey(setID)) {
				v = map.get(setID) + 1;
			}
			map.put(setID, v);
		}
		for (Entry<Integer, Integer> entry : map.entrySet()) {
			GestorSalida.ENVIAR_OS_BONUS_SET(this, entry.getKey(), entry.getValue());
		}
	}

	public void comprobarMascotas(boolean salvaldo) {
		for (final Objeto objeto : _objetos.values()) {
			if (objeto.getObjModelo().getTipo() != Constantes.OBJETO_TIPO_MASCOTA) {
				continue;
			}
			MascotaModelo mascota = Mundo.getMascotaModelo(objeto.getObjModeloID());
			int pdv = objeto.getPDV();
			if (pdv < 1) {
				continue;
			}
			boolean comido = false;
			if (objeto.esDevoradorAlmas()) {
				comido = true;
			} else if (objeto.getDiferenciaTiempo(Constantes.STAT_SE_HA_COMIDO_EL, 60 * 1000) <= mascota
					.getTiempoAlimentacionMaximo()) {
				comido = true;
			}
			if (comido) {
				if (!salvaldo)
					GestorSalida.ENVIAR_Im_INFORMACION(this, "025");
			} else {
				if (objeto.getCorpulencia() == Constantes.CORPULENCIA_DELGADO) {
					objeto.horaComer(true, Constantes.CORPULENCIA_DELGADO);
					restarVidaMascota(objeto);
				} else {
					objeto.setCorpulencia(Constantes.CORPULENCIA_DELGADO);
				}
				if (!salvaldo)
					GestorSalida.ENVIAR_Im_INFORMACION(this, "150");
			}
		}
	}

	public void restarVidaMascota(Objeto mascota) {
		if (!MainServidor.PARAM_MASCOTAS_PERDER_VIDA) {
			return;
		}
		if (mascota == null) {
			mascota = getObjPosicion(Constantes.OBJETO_POS_MASCOTA);
		}
		if (mascota == null) {
			return;
		}
		final int pdv = mascota.getPDV();
		if (pdv > 1) {
			mascota.setPDV(pdv - 1);
			GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(this, mascota);
		} else if (pdv == 1) {
			// murio mascota
			mascota.setPDV(0);
			final int fantasma = Mundo.getMascotaModelo(mascota.getObjModeloID()).getFantasma();
			if (Mundo.getObjetoModelo(fantasma) != null) {
				GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(this, mascota.getID());
				mascota.setPosicion(Constantes.OBJETO_POS_NO_EQUIPADO, this, true);
				mascota.setIDOjbModelo(fantasma);
				GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(this, mascota);
			} else {
				borrarOEliminarConOR(mascota.getID(), true);
			}
			GestorSalida.ENVIAR_Im_INFORMACION(this, "154");
		}
	}

	private void addPuntosPorDesconexion() {
		if (_pelea == null && _tiempoDesconexion > -1) {
			int segundos = (int) ((System.currentTimeMillis() - _tiempoDesconexion) / (1000));
			if (!esFantasma() && !esTumba()) {
				int horas = segundos / 3600;
				int energiaAdd = horas * (_casaDentro != null ? 100 : 50);
				energiaAdd = Math.min(energiaAdd, 10000 - _energia);
				if (energiaAdd > 0) {
					addEnergiaConIm(energiaAdd, false);
					GestorSalida.ENVIAR_Im_INFORMACION(this, "092;" + energiaAdd);
				}
			}
		}
		_tiempoDesconexion = -1;
	}

	public boolean getRecienCreado() {
		if (MainServidor.MODO_PVP) {
			if (_alineacion == Constantes.ALINEACION_NEUTRAL) {
				return true;
			}
		}
		return _recienCreado;
	}

	public Clase getClase() {
		return _clase;
	}

	public Map<Integer, Integer> getSubStatsScroll() {
		return _subStatsScroll;
	}

	public Map<Integer, Integer> getSubStatsBase() {
		return _subStatsBase;
	}

	public void addAgredirA(String nombre) {
		if (_agredir == null) {
			_agredir = new HashMap<>();
		}
		if (_agredir.get(nombre) == null) {
			_agredir.put(nombre, new ArrayList<Long>());
		}
		_agredir.get(nombre).add(System.currentTimeMillis());
	}

	public ArrayList<Long> getAgredirA(String nombre) {
		if (_agredir == null) {
			return null;
		}
		return _agredir.get(nombre);
	}

	public void addAgredidoPor(String nombre) {
		if (_agredido == null) {
			_agredido = new HashMap<>();
		}
		if (_agredido.get(nombre) == null) {
			_agredido.put(nombre, new ArrayList<Long>());
		}
		_agredido.get(nombre).add(System.currentTimeMillis());
	}

	public ArrayList<Long> getAgredidoPor(String nombre) {
		if (_agredido == null) {
			return null;
		}
		return _agredido.get(nombre);
	}

	public Oficio getOficioActual() {
		return _oficioActual;
	}

	public void setTaller(AccionDeJuego Taller) {
		_taller = Taller;
	}

	public AccionDeJuego getTaller() {
		return _taller;
	}

	public void packetModoInvitarTaller(Oficio oficio, boolean enviarOT) {
		_oficioActual = oficio;
		if (_mapa.getTrabajos().isEmpty()) {
			return;
		}
		final StringBuilder mostrar = new StringBuilder();
		if (oficio != null) {
			final String[] trabajos = Constantes.trabajosOficioTaller(oficio.getID()).split(";");
			for (final String skill : trabajos) {
				if (skill.isEmpty()) {
					continue;
				}
				if (!_mapa.getTrabajos().contains(Integer.parseInt(skill))) {
					continue;
				}
				if (mostrar.length() > 0) {
					mostrar.append(";");
				}
				mostrar.append(skill);
			}
		}
		GestorSalida.ENVIAR_EW_OFICIO_MODO_INVITACION(this, oficio != null ? "+" : "-", _id, mostrar.toString());
	}

	public void crearJuegoPJ() {
		if (_cuenta.getSocket() == null)
			return;
		StringBuilder str = new StringBuilder();
		str.append("GCK|1|" + (char) 0x00);// TODO: CUIDADO CON EL CODIGO
		str.append(stringStats() + (char) 0x00);
		str.append("wY" + MainServidor.EXPERIENCIA_CANTIDAD + (char) 0x00);
		str.append("GDM|" + _mapa.getID() + "|" + _mapa.getFecha());
		this.setMapaGDM(_mapa);
		GestorSalida.enviar(this, str.toString(), true);
		if (_pelea != null) {
			if (_pelea.getFase() != Constantes.PELEA_FASE_FINALIZADO) {
				return;
			} else {
				salirPelea(false, false, this.getPelea().acabaMaitre, 0);
			}
		}
		if (primerRefresh) {
			primerRefresh = false;
			_mapa.addPersonaje(this, true);
		}
	}

	/*
	 * public void crearJuegoPJ() { try { Thread.sleep(500); } catch (Exception e) {
	 * } if (_cuenta.getSocket() == null) { return; } Mapa mapa = _mapa; if (_pelea
	 * != null) { mapa = _pelea.getMapaReal(); } // setCargandoMapa(true, null);
	 * GestorSalida.ENVIAR_GCK_CREAR_PANTALLA_PJ(this);
	 * GestorSalida.ENVIAR_As_STATS_DEL_PJ(this); if
	 * (MainServidor.DOBLE_EXPERIENCIA) {
	 * GestorSalida.ENVIAR_DOBLE_EXPERIENCIA(this,
	 * MainServidor.EXPERIENCIA_CANTIDAD); } /* try { Thread.sleep(500); } catch
	 * (Exception e) {}
	 *
	 * GestorSalida.ENVIAR_GDM_CAMBIO_DE_MAPA(this, mapa); if (_pelea != null) { if
	 * (_pelea.getFase() != Constantes.PELEA_FASE_FINALIZADO) { return; } else {
	 * salirPelea(false, false); } } // solo se agrega si la pelea es null o se sale
	 * de la pelea por eso es _mapa GestorSalida.ENVIAR_wC_IDOLOS_MAPA(this);
	 * GestorSalida.ENVIAR_GM_PJ_A_MAPA(_mapa, this); _celda.addPersonaje(this,
	 * true); }
	 */

	private Mapa _mapaGDM;

	public void setMapaGDM(Mapa mapa) {
		_mapaGDM = mapa;
	}

	public Mapa getMapaGDM() {
		return _mapaGDM;
	}

	// private boolean _espiarPJ = false;
	//
	// public void setEspiarPj(boolean b) {
	// _espiarPJ = b;
	// }
	public boolean getCreandoJuego() {
		return _creandoJuego;
	}

	public void setCreandoJuego(boolean b) {
		_creandoJuego = b;
	}

	private void setMisiones(String misiones) {
		for (final String str : misiones.split(Pattern.quote("|"))) {
			try {
				if (str.isEmpty()) {
					continue;
				}
				final String[] s = str.split("~");
				int idMision = Integer.parseInt(s[0]);
				int estado = Integer.parseInt(s[1]);
				int etapaMision = 0;
				int nivelEtapa = 0;
				String objetivosCumplidos = "";
				try {
					etapaMision = Integer.parseInt(s[2]);
				} catch (Exception e) {
				}
				try {
					nivelEtapa = Integer.parseInt(s[3]);
				} catch (Exception e) {
				}
				try {
					objetivosCumplidos = s[4];
				} catch (Exception e) {
				}
				_misiones.add(new Mision(idMision, estado, etapaMision, nivelEtapa, objetivosCumplidos));
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

	public ArrayList<Integer> allAura = new ArrayList<Integer>();
	private int curAura;

	public boolean getAllAura(final int ornamento) {
		return allAura.contains(ornamento);
	}

	public void setAllAura(ArrayList<Integer> allAura) {
		this.allAura = allAura;
	}

	public int getCurAura() {
		return curAura;
	}

	public void setCurAura(int curAura) {
		this.curAura = curAura;
	}

	public Cofre getBanco() {
		return _cuenta.getBanco();
	}

	public String parseAuras() {
		StringBuilder str = new StringBuilder();
		boolean first = true;

		if (allAura.isEmpty())
			return "";
		for (int i : allAura) {
			if (!first)
				str.append(",");
			first = false;
			str.append(i);
		}
		return str.toString();
	}

	public Trabajo getTrabajo() {
		Trabajo trabajo = (Trabajo) getIntercambiandoCon(Trabajo.class);
		return trabajo;
	}

	private void interrumpirReceta() {
		Trabajo trabajo = (Trabajo) getIntercambiandoCon(Trabajo.class);
		if (trabajo != null) {
			if (trabajo.esCraft()) {
				trabajo.interrumpirReceta();
				trabajo.limpiarReceta();
			}
		}
	}

	public void previosDesconectar() {
		interrumpirReceta();
		if (_pelea != null) {
			if (Mundo.SERVIDOR_ESTADO == Constantes.SERVIDOR_OFFLINE) {
				if (_pelea.getTipoPelea() != Constantes.PELEA_TIPO_DESAFIO) {
					_cuenta.addMensaje("Im1192;" + _nombre, false);
				}
			}
		}
	}

	public void rechazarGrupo() {
		if (!_tipoInvitacion.equals("grupo")) {
			return;
		}
		Personaje invitandoA, invitador;
		if (_invitador != null) {
			invitador = _invitador;
			invitandoA = this;
		} else if (_invitandoA != null) {
			invitador = this;
			invitandoA = _invitandoA;
		} else {
			GestorSalida.ENVIAR_BN_NADA(this);
			return;
		}
		invitador.setInvitandoA(null, "");
		invitandoA.setInvitador(null, "");
		GestorSalida.ENVIAR_PR_RECHAZAR_INVITACION_GRUPO(invitador);
		GestorSalida.ENVIAR_PR_RECHAZAR_INVITACION_GRUPO(invitandoA);
	}

	public void rechazarGremio() {
		if (!_tipoInvitacion.equals("gremio")) {
			return;
		}
		Personaje invitandoA, invitador;
		if (_invitador != null) {
			invitador = _invitador;
			invitandoA = this;
		} else if (_invitandoA != null) {
			invitador = this;
			invitandoA = _invitandoA;
		} else {
			GestorSalida.ENVIAR_BN_NADA(this);
			return;
		}
		invitador.setInvitandoA(null, "");
		invitandoA.setInvitador(null, "");
		GestorSalida.ENVIAR_gJ_GREMIO_UNIR(invitador, "Ec");
		GestorSalida.ENVIAR_gJ_GREMIO_UNIR(invitandoA, "Ec");
	}

	public void rechazarKoliseo() {
		if (!_tipoInvitacion.equals("koliseo")) {
			return;
		}
		Personaje invitandoA, invitador;
		if (_invitador != null) {
			invitador = _invitador;
			invitandoA = this;
		} else if (_invitandoA != null) {
			invitador = this;
			invitandoA = _invitandoA;
		} else {
			GestorSalida.ENVIAR_BN_NADA(this);
			return;
		}
		invitador.setInvitandoA(null, "");
		invitandoA.setInvitador(null, "");
		GestorSalida.ENVIAR_kR_RECHAZAR_INVITACION_KOLISEO(invitador);
		GestorSalida.ENVIAR_kR_RECHAZAR_INVITACION_KOLISEO(invitandoA);
	}

	public void rechazarDesafio() {
		if (!_tipoInvitacion.equals("desafio")) {
			return;
		}
		Personaje invitandoA, invitador;
		if (_invitador != null) {
			invitador = _invitador;
			invitandoA = this;
		} else if (_invitandoA != null) {
			invitador = this;
			invitandoA = _invitandoA;
		} else {
			GestorSalida.ENVIAR_BN_NADA(this);
			return;
		}
		invitador.setInvitandoA(null, "");
		invitandoA.setInvitador(null, "");
		GestorSalida.ENVIAR_GA902_RECHAZAR_DESAFIO(invitador, invitador.getID(), _id);
		GestorSalida.ENVIAR_GA902_RECHAZAR_DESAFIO(invitandoA, invitador.getID(), _id);
	}

	public boolean puedeInvitar() {
		return _tipoInvitacion.isEmpty();
	}

	public void desconectar(boolean salvar) {
		try {
			if (Mundo.SERVIDOR_ESTADO == Constantes.SERVIDOR_OFFLINE) {
				return;
			}
			if (!_enLinea) {
				return;
			}
			if (EsliderIP) {
				EsliderIP = false;
				for (Personaje p :
						Multis) {
					p.LiderIP = null;
					p.Multi = null;
					p.EsliderIP = false;
				}
				Multis.clear();
			}
			if (this.enTorneo == 1)
				ServidorSocket.perderTorneo(this);
			boolean borrame = false;
			rechazarGrupo();
			rechazarKoliseo();
			rechazarGremio();
			rechazarDesafio();
			cerrarExchange("");
			setEnLinea(false);
			if (_pelea != null) {
				if (_pelea.esEspectador(_id)) {
					salirEspectador(true);
					borrame = true;
				} else {
					if (_pelea.getFase() != 3 || _pelea.getTipoPelea() == 0) {
						_pelea.retirarsePelea(_id, 0, true);
						borrame = true;
					} else if (_pelea.getFase() == 3 && _pelea.getTipoPelea() <= 4) {
						// perso.getCelda().removerPersonaje(perso.getID());
						_pelea.desconectarLuchador(this);
						return;
					} else {
						_pelea.retirarsePelea(_id, 0, true);
					}
				}
			} else if (_mapa != null) {
				GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(_mapa, _id);
				if (esMercante()) {
					GestorSalida.ENVIAR_GM_MERCANTE_A_MAPA(_mapa, "+" + stringGMmercante());
				}
			}
			if (_pelea == null) {
				if (_grupo != null) {
					_grupo.dejarGrupo(this, false);
				}
				if (Mundo.estaEnKoliseo(this)) {
					Mundo.delKoliseo(this);
				}
			}
			setCelda(null);
			setDelayTimerRegenPDV(0);
			if (borrame)
				GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(getMapa(), getID());
			getMapa().borrarJugador(this);
			resetearVariables();
			_tiempoDesconexion = System.currentTimeMillis();
		} catch (Exception e) {
			// si ocurre algo
		} finally {
			if (salvar) {
				GestorSQL.SALVAR_PERSONAJE(this, true);
			}
		}
	}

	public void cambiarSexo() {
		set_rostroGFX(0);
		if (_sexo == Constantes.SEXO_FEMENINO) {
			_sexo = Constantes.SEXO_MASCULINO;
		} else {
			_sexo = Constantes.SEXO_FEMENINO;
		}
	}

	public boolean enLinea() {
		return _enLinea;
	}

	public void registrar(String packet) {
		if (_cuenta != null) {
			if (ServidorSocket.REGISTROS.get(_cuenta.getNombre()) == null) {
				ServidorSocket.REGISTROS.put(_cuenta.getNombre(), new StringBuilder());
			}
			ServidorSocket.REGISTROS.get(_cuenta.getNombre())
					.append(System.currentTimeMillis() + ": \t" + packet + "\n");
		}
	}

	public void setEnLinea(final boolean linea) {
		if (Mundo.SERVIDOR_ESTADO == Constantes.SERVIDOR_OFFLINE) {
			return;
		}
		_enLinea = linea;
		if (_enLinea) {
			Mundo.addOnline(this);
		} else {
			Mundo.removeOnline(this);
		}
	}

	public void setGrupo(final Grupo grupo) {
		_grupo = grupo;
	}

	public Grupo getGrupoParty() {
		if (esMultiman()) {
			return compañeros.get(0).getGrupoParty();
		}
		return _grupo;
	}

	public String getPtoSalvada() {
		return _mapaSalvada + "," + _celdaSalvada;
	}

	public int getConversandoCon() {
		return _conversandoCon;
	}

	public void setConversandoCon(final int conversando) {
		_conversandoCon = conversando;
	}

	public int getPreguntaID() {
		return _pregunta;
	}

	public void setPreguntaID(final int pregunta) {
		_pregunta = pregunta;
	}

	public void dialogoFin() {
		GestorSalida.ENVIAR_DV_FINALIZAR_DIALOGO(this);
		_conversandoCon = 0;
		_pregunta = 0;
	}

	public long getKamas() {
		if (MainServidor.MODO_ALL_OGRINAS) {
			return GestorSQL.GET_OGRINAS_CUENTA(getCuentaID());
		} else {
			return _kamas;
		}
	}
	/*
	 * public void setKamasCero() { _kamas = 0; }
	 */

	public void addKamas(final long kamas, final boolean msj, boolean conAk) {
		if (kamas == 0) {
			return;
		}
		if (MainServidor.MODO_ALL_OGRINAS) {
			if (kamas > 0) {
				GestorSQL.ADD_OGRINAS_CUENTA((long) kamas, _cuenta.getID(), this);
			} else {
				GestorSQL.RESTAR_OGRINAS(_cuenta, (long) -kamas, this);
			}
			if (_enLinea) {
				if (conAk) {
					GestorSalida.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(this);
				}
				if (msj) {
					if (kamas < 0) {
						GestorSalida.ENVIAR_Im_INFORMACION(this, "046;" + (-kamas));
					} else if (kamas > 0) {
						GestorSalida.ENVIAR_Im_INFORMACION(this, "045;" + kamas);

					}
				}
			}
		}
	}

	/*
	 * KoloRango
	 */
	public int _kolirango = 1;// no se usa
	public int _kolipuntos = 0;

	public int getKolorango() {
		return this._kolirango;
	}

	public int getKoloPuntos() {
		return this._kolipuntos;
	}

	public void addPuntos(int cantidad) {
		if (cantidad >= 0) {
			_kolipuntos += cantidad;
			if (_kolipuntos >= 100) {
				if (_kolirango < 6) {
					_kolirango += 1;
					_kolipuntos = 0;
				} else if (_kolirango == 6) {
					_kolipuntos = 100;
				}
			}
		}
	}

	public void restarPuntos(int cantidad) {
		_kolipuntos -= cantidad;
		if (_kolipuntos < 0) {
			if (_kolirango > 1) {
				_kolirango -= 1;
				_kolipuntos = 0;
			} else if (_kolirango == 1) {
				_kolipuntos = 0;
			}
		}
	}

	/*
	 */
	public synchronized boolean comprarTienda(final Personaje comprador, long cantidad, final Objeto objeto) {
		try {
			if (objeto == null || !_tienda.contains(objeto)) {
				return false;
			}
			if (cantidad < 1) {
				cantidad = 1;
			} else if (cantidad > objeto.getCantidad()) {
				cantidad = objeto.getCantidad();
			}
			if (!Formulas.valorValido1(cantidad, objeto.getPrecio())) {
				GestorSalida.ENVIAR_BN_NADA(this, "INTENTO BUG MULTIPLICADOR");
				return false;
			}
			long precio = objeto.getPrecio() * cantidad;
			if (precio <= 0) {
				return false;
			}

			if (MainServidor.VENDER_MERCANTE_OBJETO > 0) {
				if (!Formulas.valorValido1(cantidad, (int) precio)) {
					GestorSalida.ENVIAR_BN_NADA(comprador, "INTENTO BUG MULTIPLICADOR");
					return false;
				}
				if (!comprador.tieneObjPorModYCant(MainServidor.VENDER_MERCANTE_OBJETO,
						objeto.getPrecio() * cantidad)) {
					GestorSalida.ENVIAR_Im_INFORMACION(comprador, "14");
					GestorSalida.ENVIAR_EBE_ERROR_DE_COMPRA(comprador);
					return false;
				}
				comprador.restarObjPorModYCant(MainServidor.VENDER_MERCANTE_OBJETO, objeto.getPrecio() * cantidad);
				final ObjetoModelo objModelo = Mundo.getObjetoModelo(MainServidor.VENDER_MERCANTE_OBJETO);
				Objeto objeto2 = objModelo.crearObjeto(objeto.getPrecio() * cantidad, Constantes.OBJETO_POS_NO_EQUIPADO,
						CAPACIDAD_STATS.RANDOM);
				addObjIdentAInventario(objeto2, true);
			} else {
				if (comprador.getKamas() < precio) {
					GestorSalida.ENVIAR_Im_INFORMACION(comprador, "1128;" + precio);
					return false;
				}
				comprador.addKamas(-precio, true, false);
				addKamas(precio, false, false);
			}
			long nuevaCantidad = objeto.getCantidad() - cantidad;
			if (nuevaCantidad >= 1) {
				final Objeto nuevoObj = objeto.clonarObjeto(nuevaCantidad, Constantes.OBJETO_POS_NO_EQUIPADO);
				nuevoObj.setPrecio(objeto.getPrecio());
				_tienda.addObjeto(nuevoObj);
				objeto.setCantidad(cantidad);
				// GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(comprador, objeto);
			}
			borrarObjTienda(objeto);
			objeto.setPrecio(0);
			comprador.addObjIdentAInventario(objeto, true);
			return true;
		} catch (final Exception e) {
			return false;
		}
	}

	public Cuenta getCuenta() {
		return _cuenta;
	}

	public int getPuntosHechizos() {
		return _puntosHechizos;
	}

	public Gremio getGremio() {
		if (_miembroGremio == null) {
			return null;
		}
		return _miembroGremio.getGremio();
	}

	public void setMiembroGremio(final MiembroGremio gremio) {
		_miembroGremio = gremio;
	}

	public Pelea getPelea() {
		return _pelea;
	}

	public boolean mostrarAmigos() {
		return _mostrarAmigos;
	}

	public boolean alasActivadas() {
		if (_alineacion == Constantes.ALINEACION_NEUTRAL) {
			return false;
		}
		return _mostrarAlas;
	}

	private void setRestriccionesA(int[][] r) {
		int restr = 0;
		int modif = 0;
		for (int[] a : r) {
			restr += a[0];
			if (a[1] == 1) {
				modif += a[0];
			}
		}
		modificarA(restr, restr - modif);
	}

	private void setRestriccionesB(int[][] r) {
		int restr = 0;
		int modif = 0;
		for (int[] a : r) {
			restr += a[0];
			if (a[1] == 1) {
				modif += a[0];
			}
		}
		modificarB(restr, restr - modif);
	}

	public int getEnergia() {
		return _energia;
	}

	public void addEnergiaConIm(final int energia, boolean mensaje) {
		if (Mundo.SERVIDOR_ESTADO == Constantes.SERVIDOR_OFFLINE) {
			return;
		}
		if (esMultiman()) {
			return;
		}
		if (MainServidor.MODO_HEROICO || MainServidor.MAPAS_MODO_HEROICO.contains(_mapa.getID())
				|| !MainServidor.PARAM_PERDER_ENERGIA) {
			return;
		}
		final int exEnergia = _energia;
		_energia = Math.min(10000, _energia + energia);
		if (energia > 0) {
			if (esFantasma() && exEnergia <= 0 && _energia > 0) {
				deformar();
				_ocupado = false;
				int[][] rA = { { RA_PUEDE_INTERCAMBIAR, 1 }, { RA_PUEDE_HABLAR_NPC, 1 }, { RA_PUEDE_MERCANTE, 1 },
						{ RA_PUEDE_INTERACTUAR_RECAUDADOR, 1 }, { RA_PUEDE_INTERACTUAR_PRISMA, 1 },
						{ RA_PUEDE_USAR_OBJETOS, 1 }, { RA_NO_PUEDE_ATACAR, 0 }, { RA_PUEDE_DESAFIAR, 1 },
						{ RA_PUEDE_INTERACTUAR_OBJETOS, 1 }, { RA_PUEDE_AGREDIR, 1 } };
				setRestriccionesA(rA);
				int[][] rB = { { RB_PUEDE_SER_AGREDIDO, 1 }, { RB_PUEDE_SER_DESAFIADO, 1 },
						{ RB_PUEDE_HACER_INTERCAMBIO, 1 }, { RB_NO_ES_FANTASMA, 1 }, { RB_PUEDE_CORRER, 1 },
						{ RB_PUEDE_SER_ATACADO, 0 }, { RB_NO_ES_TUMBA, 1 } };
				setRestriccionesB(rB);
				refrescarEnMapa();
				GestorSalida.ENVIAR_AR_RESTRICCIONES_PERSONAJE(this);
			}
		} else {
			if (_energia <= 0) {
				convertirseTumba();
				_energia = 0;
			} else if (_energia < 1500) {
				GestorSalida.ENVIAR_M1_MENSAJE_SERVER_SVR_MUESTRA_INSTANTANEO(this, 11, _energia + "", "");
			}
		}
		if (_enLinea && mensaje) {
			if (energia > 0) {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "07;" + energia);
			} else if (energia < 0) {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "034;" + Math.abs(energia));
			}
		}
		GestorSalida.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(this);
	}

	public void convertirseTumba() {
		if (esMultiman()) {
			return;
		}
		if (esTumba()) {
			return;
		}
		if (estaMontando()) {
			subirBajarMontura(false);
		}
		_energia = -1;
		int[][] rA = { { RA_PUEDE_INTERCAMBIAR, 0 }, { RA_PUEDE_HABLAR_NPC, 0 }, { RA_PUEDE_MERCANTE, 0 },
				{ RA_PUEDE_INTERACTUAR_RECAUDADOR, 0 }, { RA_PUEDE_INTERACTUAR_PRISMA, 0 },
				{ RA_PUEDE_USAR_OBJETOS, 0 }, { RA_NO_PUEDE_ATACAR, 1 }, { RA_PUEDE_DESAFIAR, 0 },
				{ RA_PUEDE_INTERACTUAR_OBJETOS, 0 }, { RA_PUEDE_AGREDIR, 0 } };
		setRestriccionesA(rA);
		int[][] rB = { { RB_PUEDE_SER_AGREDIDO, 0 }, { RB_PUEDE_SER_DESAFIADO, 0 }, { RB_PUEDE_HACER_INTERCAMBIO, 0 },
				{ RB_NO_ES_FANTASMA, 1 }, { RB_PUEDE_CORRER, 0 }, { RB_PUEDE_SER_ATACADO, 0 }, { RB_NO_ES_TUMBA, 0 } };
		setRestriccionesB(rB);
		_gfxID = _clase.getGfxs(3);
		refrescarEnMapa();
		GestorSalida.ENVIAR_AR_RESTRICCIONES_PERSONAJE(this);
		GestorSalida.ENVIAR_M1_MENSAJE_SERVER_SVR_MUESTRA_INSTANTANEO(this, 12, "", "");
	}

	public void convertirseFantasma() {
		if (esMultiman()) {
			return;
		}
		if (esFantasma()) {
			return;
		}
		_energia = 0;
		_gfxID = 8004;
		int[][] rA = { { RA_PUEDE_INTERCAMBIAR, 0 }, { RA_PUEDE_HABLAR_NPC, 0 }, { RA_PUEDE_MERCANTE, 0 },
				{ RA_PUEDE_INTERACTUAR_RECAUDADOR, 0 }, { RA_PUEDE_INTERACTUAR_PRISMA, 0 },
				{ RA_PUEDE_USAR_OBJETOS, 0 }, { RA_NO_PUEDE_ATACAR, 1 }, { RA_PUEDE_DESAFIAR, 0 },
				{ RA_PUEDE_INTERACTUAR_OBJETOS, 0 }, { RA_PUEDE_AGREDIR, 0 } };
		setRestriccionesA(rA);
		int[][] rB = { { RB_PUEDE_SER_AGREDIDO, 0 }, { RB_PUEDE_SER_DESAFIADO, 0 }, { RB_PUEDE_HACER_INTERCAMBIO, 0 },
				{ RB_NO_ES_FANTASMA, 0 }, { RB_PUEDE_CORRER, 0 }, { RB_PUEDE_SER_ATACADO, 0 }, { RB_NO_ES_TUMBA, 1 } };
		setRestriccionesB(rB);
		if (MainServidor.MODO_HEROICO || MainServidor.MAPAS_MODO_HEROICO.contains(_mapa.getID())) {
			if (!MainServidor.PARAM_HEROICO_GAME_OVER) {
				revivir(true);
				return;
			}
			if (_enLinea) {
				if (MainServidor.PARAM_MENSAJE_ASESINOS_HEROICO) {
					GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS("1DIE;" + _nombre);
				}
				GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(_mapa, _id);
			}
			if (_grupo != null) {
				_grupo.dejarGrupo(this, false);
			}
			if (Mundo.estaEnKoliseo(this)) {
				Mundo.delKoliseo(this);
			}

			setCelda(null);
			resetearVariables();
			GestorSalida.ENVIAR_GO_GAME_OVER(this);
			Mundo.eliminarPersonaje(this, false);
			setEnLinea(false);
			GestorSQL.SALVAR_PERSONAJE(this, true);
		} else {// si es fantasma
			refrescarEnMapa();
			GestorSalida.ENVIAR_AR_RESTRICCIONES_PERSONAJE(this);
			String cementerio = _mapa.getSubArea().getCementerio();
			// teleport((short) 10342, (short) 223);
			if (cementerio.isEmpty()) {
				cementerio = "1188,297";
			}
			short mapaID = 1188;
			short celdaID = 297;
			try {
				mapaID = Short.parseShort(cementerio.split(",")[0]);
				celdaID = Short.parseShort(cementerio.split(",")[1]);
			} catch (Exception e) {
			}
			if (MainServidor.MODO_IMPACT) {
				teleport((short) 8534, (short) 200);
			} else
				teleport(mapaID, celdaID);
			GestorSalida.ENVIAR_IH_COORDENADAS_UBICACION(this,
					this.getMapa().getSubArea().getArea().getSuperArea().getID() == 3
							? Constantes.ESTATUAS_FENIX_INCARMAN
							: Constantes.ESTATUAS_FENIX);
			GestorSalida.ENVIAR_M1_MENSAJE_SERVER_SVR_MUESTRA_INSTANTANEO(this, 15, "", "");
		}
	}

	public void revivir(final boolean aparecer) {
		if (esMultiman()) {
			return;
		}
		if (!esFantasma() && !esTumba()) {
			return;
		}
		_energia = MainServidor.MODO_HEROICO ? 10000 : 1000;
		deformar();
		_ocupado = false;
		int[][] rA = { { RA_PUEDE_INTERCAMBIAR, 1 }, { RA_PUEDE_HABLAR_NPC, 1 }, { RA_PUEDE_MERCANTE, 1 },
				{ RA_PUEDE_INTERACTUAR_RECAUDADOR, 1 }, { RA_PUEDE_INTERACTUAR_PRISMA, 1 },
				{ RA_PUEDE_USAR_OBJETOS, 1 }, { RA_NO_PUEDE_ATACAR, 0 }, { RA_PUEDE_DESAFIAR, 1 },
				{ RA_PUEDE_INTERACTUAR_OBJETOS, 1 }, { RA_PUEDE_AGREDIR, 1 } };
		setRestriccionesA(rA);
		int[][] rB = { { RB_PUEDE_SER_AGREDIDO, 1 }, { RB_PUEDE_SER_DESAFIADO, 1 }, { RB_PUEDE_HACER_INTERCAMBIO, 1 },
				{ RB_NO_ES_FANTASMA, 1 }, { RB_PUEDE_CORRER, 1 }, { RB_PUEDE_SER_ATACADO, 0 }, { RB_NO_ES_TUMBA, 1 } };
		setRestriccionesB(rB);
		if (aparecer && _pelea == null) {
			refrescarEnMapa();
		}
		if (_enLinea) {
			GestorSalida.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(this);
			GestorSalida.ENVIAR_AR_RESTRICCIONES_PERSONAJE(this);
			GestorSalida.ENVIAR_IH_COORDENADAS_UBICACION(this, "");
			GestorSalida.ENVIAR_Im_INFORMACION(this, "033");
		}
	}

	public int getNivelGremio() {
		if (_miembroGremio == null) {
			return 0;
		}
		return _miembroGremio.getGremio().getNivel();
	}

	public int getNivel() {
		return _nivel;
	}

	public void setNivel(final int nivel) {
		_nivel = nivel;
	}

	public String getExperiencia() {
		return experiencia.toString();
	}

	public Celda getCelda() {
		return _celda;
	}

	public synchronized void setCelda(final Celda celda) {
		boolean difMapa = celda == null || _celda == null ? true : (celda.getMapa() != _celda.getMapa());
		if (esMultiman()) {
			difMapa = false;
		}
		if (_celda != null) {
			_celda.removerPersonaje(this, difMapa || !_enLinea);
		}
		if (celda != null) {
			_celda = celda;
			_celda.addPersonaje(this, difMapa && _enLinea);
		}
	}

	public int getTalla() {
		return _talla;
	}

	public void setTalla(final short talla) {
		_talla = talla;
	}

	public void setPelea(final Pelea pelea) {
		if (_pelea != null && pelea == null) {
			if (!compañeros.isEmpty()) {
				for (Personaje perso : compañeros) {
					perso.getCompañeros().clear();
				}
				compañeros.clear();
			}
			if (Multi != null) {
				GestorSalida.ENVIAR_AI_CAMBIAR_ID(this, _id);
				GestorSalida.ENVIAR_SL_LISTA_HECHIZOS(this);
				if (enLinea()) {
					getCuenta().getSocket().setPersonaje(this);
				}
				if (!Multis.isEmpty()) {
					for (Personaje p :
							Multis) {
						if (!p.enLinea()) {
							p.desconectar(true);
						}
					}
				}
				Multi = null;
			}
			if (enLinea()) {
				getCuenta().getSocket().setPersonaje(this);
				Multi = null;
				GestorSalida.ENVIAR_AI_CAMBIAR_ID(this, _id);
				GestorSalida.ENVIAR_SL_LISTA_HECHIZOS(this);
			}else {
				desconectar(true);
			}
			setDelayTimerRegenPDV(1000);
		} else if (pelea != null) {
			setDelayTimerRegenPDV(0);
		}
		_pelea = pelea;
	}

	private void disminuirTurnos() {
		if (_montando && _montura != null) {
			_montura.energiaPerdida(5);
		}
		for (byte pos : Constantes.POSICIONES_EQUIPAMIENTO) {
			try {
				final Objeto obj = getObjPosicion(pos);
				if (obj == null) {
					continue;
				}
				final String param = obj.getParamStatTexto(Constantes.STAT_TURNOS, 3);
				if (param.isEmpty()) {
					continue;
				}
				final int turnos = Integer.parseInt(param, 16);
				if (turnos == 1) {
					borrarOEliminarConOR(obj.getID(), true);
				} else if (turnos > 1) {
					obj.addStatTexto(Constantes.STAT_TURNOS, "0#0#" + Integer.toString(turnos - 1, 16));
					GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(this, obj);
				}
			} catch (final Exception e) {
			}
		}
		if (MainServidor.PARAM_PERDER_PDV_ARMAS_ETEREAS) {
			Objeto arma = getObjPosicion(Constantes.OBJETO_POS_ARMA);
			if (arma != null && arma.getObjModelo().esEtereo()) {
				if (arma.addDurabilidad(-1)) {
					borrarOEliminarConOR(arma.getID(), true);
					GestorSalida.ENVIAR_Im_INFORMACION(this, "160");
				} else {
					GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(this, arma);
				}
			}
		}
	}

	public int getGfxID(boolean rolePlayBuff) {
		if (_encarnacion != null) {
			return _encarnacion.getGfxID();
		}
		int gfx = _totalStats.getStatsObjetos().getStatParaMostrar(Constantes.STAT_CAMBIA_APARIENCIA_2);
		if (rolePlayBuff && gfx != 0) {
			return gfx;
		}
		gfx = _totalStats.getStatsObjetos().getStatParaMostrar(Constantes.STAT_CAMBIA_APARIENCIA);
		if (gfx != 0) {
			return gfx;
		}
		return _gfxID;
	}

	public int getGfxIDReal() {
		return _gfxID;
	}

	public void setGfxID(final int gfxid, boolean rostro) {
		if (rostro) {
			_rostroGFX = 0;
		}
		_gfxID = gfxid;
	}

	public void deformar() {
		if (_encarnacion != null) {
			_gfxID = _encarnacion.getGfxID();
		} else {
			if (_rostroGFX > 0) {
				_gfxID = _rostroGFX;
			} else {
				_gfxID = (_claseID * 10 + _sexo);
			}

		}
	}

	public int getID() {
		return _id;
	}

	public Mapa getMapa() {
		return _mapa;
	}

	public String getNombre() {
		return _nombre;
	}

	public String getNombreGM() {
		if (Mundo.getMasterResetExist(_resets)) {
			return "[" + Mundo.getMasterReset(_resets).getSufijo() + "]" + _nombre;
		}
		return _nombre;
	}


	public boolean estaDisponible(boolean muerto, boolean otros) {
		if (estaOcupado() || getPelea() != null) {
			return false;
		}
		if (otros) {
			if (estaFullOcupado()) {
				return false;
			}
		}
		if (muerto) {
			if (esFantasma() || esTumba()) {
				return false;
			}
		}
		return true;
	}

	public boolean estaFullOcupado() {
		if (_conversandoCon != 0 || _exchanger != null || !puedeInvitar() || estaExchange()) {
			return true;
		}
		// return getCuenta().getSocket().getRealizandoAccion();
		return false;
	}

	public boolean estaOcupado() {
		return _ocupado;
	}

	public void setOcupado(final boolean ocupado) {
		_ocupado = ocupado;
	}

	public boolean estaSentado() {
		return _sentado;
	}

	public byte getSexo() {
		return _sexo;
	}

	public byte getClaseID(final boolean original) {
		if (!original && _encarnacion != null) {
			return 20;
		}
		return _claseID;
	}

	public void setClaseID(final byte clase) {
		_claseID = clase;
	}

	public int getColor1() {
		return _color1;
	}

	void salirEspectador(boolean sacalo) {
		if (getPelea() != null) {
			if (getPelea()._espectadores.containsKey(this.getID())) {
				getPelea()._espectadores.remove(this.getID());
			}
		}
		salirEspectador();
	}

	public boolean instante = false;

	public void retornoMapa() {
		_pelea = null;
		_ocupado = false;
		instante = true;
		_mapa.addPersonaje(this, true);
	}

	public void salirEspectador() {
		setPelea(null);
		setOcupado(false);
		retornoMapa();
		GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(this);
	}

	public int getColor2() {
		return _color2;
	}

	public int getColor3() {
		return _color3;
	}

	public int getCapital() {
		return _puntosStats;
	}

	public void resetearStats(final boolean todo) {
		// ya contiene resfrescarStuff
		if (todo) {
			reiniciarSubStats(_subStatsScroll);
		}
		reiniciarSubStats(_subStatsBase);
		_totalStats.getStatsBase().nuevosStatsBase(_subStatsScroll, this);
		_puntosStats = ((_nivel - 1) * MainServidor.PUNTOS_STATS_POR_NIVEL)
				+ (MainServidor.BONUS_RESET_PUNTOS_STATS * _resets) + MainServidor.INICIO_PUNTOS_STATS
				+ ((Mundo.getMasterResetExist(_resets) && Mundo.getMasterReset(_resets).isReinicia())
				? Mundo.getMasterReset(_resets).getCapital()
				: 0);
		_puntosStats = (_puntosStats + (_nivelOmega * MainServidor.PUNTOS_STATS_POR_NIVEL_OMEGA));
		refrescarStuff(true, true, false);
	}

	public boolean cambiarClase(byte clase) {// cambiar raza
		if (Mundo.CLASES.get((int) clase) == null) {
			GestorSalida.ENVIAR_BN_NADA(this, "LA CLASE NO EXISTE");
			return false;
		}
		if (clase == getClaseID(true)) {
			GestorSalida.ENVIAR_BN_NADA(this, "CAMBIAR CLASE - MISMA CLASE");
			return false;
		}
		if (_koliseo != null) {
			this.getServidorSocket().koliseo_Desinscribirse();
		}
		_claseID = clase;
		_clase = Mundo.getClase(_claseID);
		if (_nivel < MainServidor.INICIO_NIVEL) {
			_nivel = MainServidor.INICIO_NIVEL;
			experiencia = Mundo.getExpPersonaje(_nivel, _nivelOmega);
			GestorSalida.ENVIAR_AN_MENSAJE_NUEVO_NIVEL(this, _nivel);
			GestorSalida.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(this);
		}
		set_rostroGFX(0);
		GestorSalida.ENVIAR_AC_CAMBIAR_CLASE(this, getClaseID(true));
		deformar();
		for (final HechizoPersonaje hp : _hechizos) {
			for (int i = 1; i < hp.getNivel(); i++) {
				_puntosHechizos += i;
			}
		}
		fijarHechizosInicio();
		_puntosHechizos = (_nivel - 1) + (MainServidor.BONUS_RESET_PUNTOS_HECHIZOS * _resets)
				+ ((Mundo.getMasterResetExist(_resets) && Mundo.getMasterReset(_resets).isReinicia())
				? Mundo.getMasterReset(_resets).getHechizo()
				: 0);
		if (!Mundo.HECHIZOS_OMEGA.isEmpty()) {
			boostearFullTodosHechizos();
			if (_resets >= MainServidor.RESET_APRENDER_OMEGA) {
				boostearFullTodosHechizosOmega();
			}
		}
		resetearStats(false);
		refrescarEnMapa();
		GestorSQL.CAMBIAR_SEXO_CLASE(this);
		return true;
	}

	public void setResets(byte reset) {
		_resets = reset;
	}

	public byte getResets() {

		return _resets;
	}

	public boolean aumentarReset(boolean reiniciar) {
		if (_resets >= MainServidor.MAX_RESETS) {
			return false;
		}
		_encarnacion = null;
		_resets++;
		if (!reiniciar) {
			int difNivel = _nivel - MainServidor.INICIO_NIVEL;
			_nivel = MainServidor.INICIO_NIVEL;
			experiencia = Mundo.getExpPersonajeReset(_nivel, _nivelOmega);
			if (Mundo.HECHIZOS_OMEGA.isEmpty()) {
				resetearTodosHechizos();
			}
			if (!Mundo.HECHIZOS_OMEGA.isEmpty() && _resets >= MainServidor.RESET_APRENDER_OMEGA)
				boostearFullTodosHechizosOmega();
			_puntosHechizos -= difNivel;
		} else {
			if (Mundo.getMasterResetExist(_resets)) {
				addScrollStat(Mundo.getMasterReset(_resets).getCapital());
			}
		}
		monturaACertificado();
		_porcXPMontura = 0;
		_restriccionesALocalPlayer = 8200;
		_restriccionesBCharacter = 8;
		if (Mundo.getMasterResetExist(_resets) && Mundo.getMasterReset(_resets).isReinicia()) {
			_puntosHechizos += Mundo.getMasterReset(_resets).getHechizo();
		}
		_puntosHechizos += MainServidor.BONUS_RESET_PUNTOS_HECHIZOS;
		resetearStats(false);
		fullPDV();// FIXME
		refrescarEnMapa();
		GestorSalida.ENVIAR_AN_MENSAJE_NUEVO_NIVEL(this, _nivel);
		GestorSalida.ENVIAR_AN_MENSAJE_NUEVO_RESET(this, _resets);
		return true;
	}

	public boolean reiniciarReset() {
		if (MainServidor.CLASES_NO_PERMITIDAS_RESET.contains(_claseID)) {
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(this, "Esta clase no esta permitida para reiniciarla");
			return false;
		}
		_resets--;
		_encarnacion = null;
		int difNivel = _nivel - MainServidor.NIVEL_PJ_RESET;
		_nivel = MainServidor.NIVEL_PJ_RESET;
		_alineacion = Constantes.ALINEACION_NEUTRAL;
		_mostrarAlas = false;
		_honor = 0;
		_deshonor = 0;
		_gradoAlineacion = 1;
		experiencia = Mundo.getExpPersonaje(_nivel, _nivelOmega);
		monturaACertificado();
		_porcXPMontura = 0;
		_restriccionesALocalPlayer = 8200;
		_restriccionesBCharacter = 8;
		resetearTodosHechizos();
		_puntosHechizos += MainServidor.BONUS_RESET_PUNTOS_HECHIZOS;
		_puntosHechizos -= difNivel;
		resetearStats(false);
		fullPDV();// FIXME
		refrescarEnMapa();
		GestorSalida.ENVIAR_AN_MENSAJE_NUEVO_NIVEL(this, _nivel);
		GestorSalida.ENVIAR_SL_LISTA_HECHIZOS(this);
		GestorSalida.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(this);
		return true;
	}

	private void monturaACertificado() {
		try {
			if (_montura == null)
				return;
			if (estaMontando()) {
				subirBajarMontura(false);
			}
			final Objeto obj1 = _montura.getObjModCertificado().crearObjeto(1, Constantes.OBJETO_POS_NO_EQUIPADO,
					CAPACIDAD_STATS.RANDOM);
			obj1.fijarStatValor(Constantes.STAT_CONSULTAR_MONTURA, Math.abs(_montura.getID()));
			obj1.addStatTexto(Constantes.STAT_PERTENECE_A, "0#0#0#" + getNombre());
			obj1.addStatTexto(Constantes.STAT_NOMBRE, "0#0#0#" + _montura.getNombre());
			addObjetoConOAKO(obj1, true);
			_montura.setPergamino(obj1.getID());
			GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(this);
			GestorSalida.ENVIAR_Re_DETALLES_MONTURA(this, "-", null);
			GestorSQL.REPLACE_MONTURA(_montura, false);
			_montura = null;
		} catch (Exception e) {
		}
	}

	public void reiniciarCero() {
		if (esMultiman()) {
			return;
		}
		_encarnacion = null;
		revivir(false);
		_ultimoNivel = _nivel;
		_nivel = MainServidor.INICIO_NIVEL;
		_kamas = 0;
		_puntosHechizos = (_nivel - 1) + (MainServidor.BONUS_RESET_PUNTOS_HECHIZOS * _resets);
		if (Mundo.getMasterResetExist(_resets) && Mundo.getMasterReset(_resets).isReinicia()) {
			_puntosHechizos += Mundo.getMasterReset(_resets).getHechizo();
		}
		_alineacion = Constantes.ALINEACION_NEUTRAL;
		_mostrarAlas = false;
		_honor = 0;
		_deshonor = 0;
		_gradoAlineacion = 1;
		_energia = 10000;
		experiencia = Mundo.getExpPersonaje(_nivel, _nivelOmega);
		_montura = null;
		_porcXPMontura = 0;
		_talla = _clase.getTallas(_sexo);
		_gfxID = _clase.getGfxs(_sexo);
		final short mapaID = _clase.getMapaInicio();
		final short celdaID = _clase.getCeldaInicio();
		_mapa = Mundo.getMapa(mapaID);
		if (_mapa == null) {
			_mapa = Mundo.getMapa((short) 7411);
		}
		setCelda(_mapa.getCelda(celdaID));
		// setPuntoSalvada(mapaID + "," + celdaID);
		_tienda.clear();
		_esMercante = false;
		fullPDV();
		_statsOficios.clear();
		if (MainServidor.PARAM_PERMITIR_OFICIOS) {
			_statsOficios.put((byte) 7, new StatOficio((byte) 7, Mundo.getOficio(1), 0));
		}
		_restriccionesALocalPlayer = 8200;
		_restriccionesBCharacter = 8;
		fijarHechizosInicio();
		resetearStats(true);
		_enLinea = true;
		GestorSalida.ENVIAR_ALK_LISTA_DE_PERSONAJES(this, _cuenta);
		_enLinea = false;
		GestorSQL.SALVAR_PERSONAJE(this, false);
	}

	public boolean tieneHechizoID(final int hechizoID) {
		if (_encarnacion != null) {
			return _encarnacion.tieneHechizoID(hechizoID);
		}
		return getHechizoPersonajePorID(hechizoID) != null;
	}

	public boolean tieneHechizoID2(final int hechizoID) {
		if (_encarnacion != null) {
			return _encarnacion.tieneHechizoID(hechizoID);
		}
		return getHechizoPersonajePorID2(hechizoID) != null;
	}

	public boolean boostearFullTodosHechizos() {
		if (_encarnacion != null) {
			return false;
		}
		for (HechizoPersonaje h : _hechizos) {
			if (h == null) {
				continue;
			}
			int hechizoID2 = h.getHechizo().getID();
			if (MainServidor.HECHIZOS_CLASE_UNICOS2.containsKey(hechizoID2)) {
				if (_claseID != MainServidor.HECHIZOS_CLASE_UNICOS2.get(hechizoID2)) {
					continue;
				}
			}
			final int antNivel = h.getStatHechizo().getGrado();
			if (antNivel >= 6) {
				continue;
			} /*
			 * if(Mundo.HECHIZOS_OMEGA.containsKey(h.getStatHechizo().getHechizoID())) {
			 * continue; }
			 */
			while (fijarNivelHechizoOAprender(h.getHechizo().getID(), h.getStatHechizo().getGrado() + 1, false)
					&& h.getStatHechizo().getGrado() < 6) {
			}
		}
		GestorSalida.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(this);
		return true;
	}

	public void resetearStats() {
		_subStatsBase.put(125, -_subStatsBase.get(125) + _subStatsScroll.get(125));
		_subStatsBase.put(124, -_subStatsBase.get(124) + _subStatsScroll.get(124));
		_subStatsBase.put(118, -_subStatsBase.get(118) + _subStatsScroll.get(118));
		_subStatsBase.put(123, -_subStatsBase.get(123) + _subStatsScroll.get(123));
		_subStatsBase.put(119, -_subStatsBase.get(119) + _subStatsScroll.get(119));
		_subStatsBase.put(126, -_subStatsBase.get(126) + _subStatsScroll.get(126));
	}

	public boolean boostearFullTodosHechizosOmega() {
		if (_encarnacion != null) {
			return false;
		}
		for (HechizoPersonaje h : _hechizos) {
			if (h == null) {
				continue;
			}
			int hechizoID2 = h.getHechizo().getID();
			if (MainServidor.HECHIZOS_CLASE_UNICOS2.containsKey(hechizoID2)) {
				if (_claseID != MainServidor.HECHIZOS_CLASE_UNICOS2.get(hechizoID2)) {
					continue;
				}
			}
			if (Mundo.HECHIZOS_OMEGA.containsKey(h.getStatHechizo().getHechizoID())) {
				final int antNivel = h.getStatHechizo().getGrado();
				if (antNivel == 6) {
					fijarNivelHechizoOAprender(h, 7, false);
				}
			}
		}
		GestorSalida.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(this);
		return true;
	}

	public boolean boostearHechizo(final int hechizoID) {// subir hechizo
		if (_encarnacion != null) {
			return false;
		}
		HechizoPersonaje h = getHechizoPersonajePorID(hechizoID);
		if (h == null) {
			return false;
		}
		final int antNivel = h.getStatHechizo().getGrado();
		if (antNivel >= 6) {
			return false;
		}
		if (_puntosHechizos < antNivel) {
			return false;
		}
		if (!fijarNivelHechizoOAprender(hechizoID, antNivel + 1, false)) {
			return false;
		}
		_puntosHechizos -= antNivel;
		GestorSalida.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(this);
		return true;
	}

	public void resetearTodosHechizos() {
		if (_encarnacion != null) {
			return;
		}
		final ArrayList<HechizoPersonaje> hechizos = new ArrayList<HechizoPersonaje>();
		hechizos.addAll(_hechizos);
		for (final HechizoPersonaje hp : hechizos) {
			for (int i = 1; i < hp.getNivel(); i++) {
				_puntosHechizos += i;
			}
			hp.setNivel(1);
			fijarNivelHechizoOAprender(hp.getHechizo().getID(),
					hp.getStatHechizo().getNivelRequerido() > _nivel ? 0 : 1, false);
		}
		GestorSalida.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(this);
		GestorSalida.ENVIAR_SL_LISTA_HECHIZOS(this);
	}

	public boolean olvidarHechizo(final int hechizoID, boolean porCompleto, boolean mensaje) {
		if (_encarnacion != null) {
			return false;
		}
		HechizoPersonaje h = getHechizoPersonajePorID(hechizoID);
		if (h == null) {
			return false;
		}
		for (int i = 1; i < h.getNivel(); i++) {
			_puntosHechizos += i;
		}
		fijarNivelHechizoOAprender(hechizoID, porCompleto ? 0 : 1, false);
		if (mensaje) {
			GestorSalida.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(this);
		}
		return true;
	}

	public boolean olvidarHechizo(final int hechizoID) {
		if (_encarnacion != null) {
			return false;
		}
		HechizoPersonaje h = getHechizoPersonajePorID(hechizoID);
		if (h == null) {
			return false;
		}
		fijarNivelHechizoOAprender(hechizoID, 0, false);
		GestorSalida.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(this);

		return true;
	}

	public boolean nerfearHechizo(int hechizoID) {
		if (_encarnacion != null || _hechizos.get(hechizoID) == null)
			return false;
		int antNivel = _hechizos.get(hechizoID).getNivel();
		if (antNivel <= 1)
			return false;
		if (fijarNivelHechizoOAprender(hechizoID, (antNivel - 1), false)) {
			int total = 0;
			for (int i = (antNivel - 1); i < antNivel; i++)
				total += i;
			_puntosHechizos += total;
			return true;
		}
		return false;
	}

	private HechizoPersonaje getHechizoPersonajePorID2(int hechizoID) {
		HechizoPersonaje h = null;
		for (HechizoPersonaje hp : _hechizos) {
			if (hp.getHechizo().getID() == hechizoID) {
				h = hp;
				break;
			}
		}
		return h;
	}

	private HechizoPersonaje getHechizoPersonajePorID(int hechizoID) {
		HechizoPersonaje h = null;
		for (HechizoPersonaje hp : _hechizos) {
			int hechizoID2 = hp.getHechizo().getID();
			if (MainServidor.HECHIZOS_CLASE_UNICOS2.containsKey(hechizoID2)) {
				if (_claseID != MainServidor.HECHIZOS_CLASE_UNICOS2.get(hechizoID2)) {
					continue;
				}
			}
			if (hp.getHechizo().getID() == hechizoID) {
				h = hp;
				break;
			}
		}
		return h;
	}

	public boolean inicioAccionMoverse(final AccionDeJuego AJ) {
		try {
			if (AJ == null) {
				GestorSalida.ENVIAR_BN_NADA(this, "inicioAccionMoverse AJ null");
				return false;
			}
			Luchador luch = _pelea.getLuchadorPorID(getID());
			if (!luch.puedeJugar()) {
				luch = null;
				for (Personaje perso : compañeros) {
					Luchador luchTemp = _pelea.getLuchadorPorID(perso.getID());
					if (luchTemp.puedeJugar()) {
						luch = luchTemp;
						break;
					}
				}
				if (luch == null) {
					return false;
				}
			}
			String moverse = _pelea.intentarMoverse(luch, AJ.getPathPacket(), AJ.getIDUnica(), AJ);
			return (moverse.equals("ok") || moverse.equals("stop"));

		} catch (Exception e) {
			String error = "EXCEPTION inicioAccionMoverse AJ.getPacket(): " + AJ.getPathPacket() + " e: "
					+ e.toString();
			GestorSalida.ENVIAR_BN_NADA(this, error);
			MainServidor.redactarLogServidorln(error);
		}
		return false;
	}

	private HechizoPersonaje getHechizoPersonajePorPos(char pos) {
		HechizoPersonaje h = null;
		for (HechizoPersonaje hp : _hechizos) {
			int hechizoID2 = hp.getHechizo().getID();
			if (MainServidor.HECHIZOS_CLASE_UNICOS2.containsKey(hechizoID2)) {
				if (_claseID != MainServidor.HECHIZOS_CLASE_UNICOS2.get(hechizoID2)) {
					continue;
				}
			}
			if (hp.getPosicion() == pos) {
				h = hp;
				break;
			}
		}
		return h;
	}

	public Map<Integer, Duo<Integer, Integer>> getHechizosSetClase() {
		return _hechizosSetClase;
	}

	public void delHechizosSetClase(int hechizo) {
		if (_hechizosSetClase.containsKey(hechizo)) {
			_hechizosSetClase.remove(hechizo);
		}
	}

	public void addHechizosSetClase(int hechizo, int efecto, int modificacion) {
		if (!_hechizosSetClase.containsKey(hechizo)) {
			_hechizosSetClase.put(hechizo, new Duo<Integer, Integer>(efecto, modificacion));
		}
	}

	public void fijarHechizosInicio() {
		if (esMultiman()) {
			return;
		}
		ArrayList<StatHechizo> tempHechizos = new ArrayList<>();
		for (HechizoPersonaje h : _hechizos) {
			if (h.getStatHechizo() == null) {
				continue;
			}
			switch (h.getStatHechizo().getTipo()) {
				case 0:// normales
				case 4: // de clase
					continue;
			}
			tempHechizos.add(h.getStatHechizo());
		}
		_hechizos.clear();
		// _mapStatsHechizos.clear();
		for (int nivel = 1; nivel <= _nivel; nivel++) {
			_clase.aprenderHechizo(this, nivel);
		}
		int i = 1;
		for (HechizoPersonaje hp : _hechizos) {
			hp.setPosicion(Encriptador.getValorHashPorNumero(i));
			i++;
		}
		for (StatHechizo sh : tempHechizos) {
			fijarNivelHechizoOAprender(sh.getHechizoID(), sh.getGrado(), false);
		}
		if (_enLinea) {
			GestorSalida.ENVIAR_SL_LISTA_HECHIZOS(this);
		}
	}

	public boolean fijarNivelHechizoOAprender(final int hechizoID, final int nivel, final boolean mensaje) {
		if (_encarnacion != null) {
			return false;
		}
		if (nivel > 0) {
			final Hechizo hechizo = Mundo.getHechizo(hechizoID);
			if (hechizo == null) {
				return false;
			}
			final StatHechizo statHechizo = hechizo.getStatsPorNivel(nivel);
			if (statHechizo == null || statHechizo.getNivelRequerido() > _nivel) {
				return false;
			}
			HechizoPersonaje h = getHechizoPersonajePorID(hechizoID);
			if (h == null) {
				addHechizoPersonaje('_', hechizo, nivel);
			} else {
				if (nivel == 6 && MainServidor.APRENDER_HECHIZO_RESET.containsKey(hechizoID)) {
					if (_resets >= MainServidor.APRENDER_HECHIZO_RESET.get(hechizoID)) {
						h.setNivel(nivel);
					} else {
						return false;
					}
				} else if (nivel == 7) {
					if (_resets > 10) {
						h.setNivel(nivel);
					} else {
						return false;
					}
				} else {
					h.setNivel(nivel);
				}

			}
		} else {
			HechizoPersonaje h = getHechizoPersonajePorID(hechizoID);
			if (h == null) {
				return false;
			}
			_hechizos.remove(h);
			// _mapStatsHechizos.remove(hechizoID);
		}
		if (_enLinea) {
			GestorSalida.ENVIAR_SUK_NIVEL_HECHIZO(this, hechizoID, nivel);
			if (mensaje) {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "03;" + hechizoID);
			}
		}
		return true;
	}

	public boolean fijarNivelHechizoOAprender(HechizoPersonaje h, final int nivel, final boolean mensaje) {
		if (_encarnacion != null) {
			return false;
		}
		if (nivel > 0) {
			final Hechizo hechizo = Mundo.getHechizo(h.getHechizo().getID());
			if (hechizo == null) {
				return false;
			}
			final StatHechizo statHechizo = hechizo.getStatsPorNivel(nivel);
			if (statHechizo == null || statHechizo.getNivelRequerido() > _nivel) {
				return false;
			}
			if (nivel == 6 && MainServidor.APRENDER_HECHIZO_RESET.containsKey(hechizo.getID())) {
				if (_resets >= MainServidor.APRENDER_HECHIZO_RESET.get(hechizo.getID())) {
					h.setNivel(nivel);
				} else {
					return false;
				}
			} else if (nivel == 7) {
				if (_resets >= MainServidor.RESET_APRENDER_OMEGA) {
					h.setNivel(nivel);
				} else {
					return false;
				}
			} else {
				h.setNivel(nivel);
			}
		} else {
			if (h == null) {
				return false;
			}
			_hechizos.remove(h);
			// _mapStatsHechizos.remove(hechizoID);
		}
		if (_enLinea) {
			GestorSalida.ENVIAR_SUK_NIVEL_HECHIZO(this, h.getHechizo().getID(), nivel);
			if (mensaje) {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "03;" + h.getHechizo().getID());
			}
		}
		return true;
	}

	private void analizarPosHechizos(final String str) {
		for (final String s : str.split(";")) {
			try {
				String[] split = s.split(",");
				int id = Integer.parseInt(split[0]);
				int nivel = Integer.parseInt(split[1]);
				char pos = split[2].charAt(0);
				HechizoPersonaje h2 = getHechizoPersonajePorPos(pos);
				if (h2 != null) {
					h2.setPosicion('_');
				}
				addHechizoPersonaje(pos, Mundo.getHechizo(id), nivel);
			} catch (final Exception e) {
			}
		}
	}

	public void setPosHechizo(final int hechizoID, final char pos) {
		if (_encarnacion != null) {
			_encarnacion.setPosHechizo(hechizoID, pos, this);
			return;
		}
		if (pos == 'a') {
			GestorSalida.ENVIAR_BN_NADA(this, "SET POS HECHIZO - POS INVALIDA");
			return;
		}
		System.out.println(pos);
		HechizoPersonaje h = getHechizoPersonajePorID(hechizoID);
		if (h == null) {
			GestorSalida.ENVIAR_BN_NADA(this, "SET POS HECHIZO - NO TIENE HECHIZO");
			return;
		}
		HechizoPersonaje h2 = getHechizoPersonajePorPos(pos);
		if (h2 != null) {
			h2.setPosicion('_');
		}
		h.setPosicion(pos);
		GestorSalida.ENVIAR_BN_NADA(this);
	}

	public StatHechizo getStatsHechizo(final int hechizoID) {
		if (_encarnacion != null) {
			return _encarnacion.getStatsHechizo(hechizoID);
		}
		HechizoPersonaje h = getHechizoPersonajePorID(hechizoID);
		if (h == null) {
			return null;
		}
		return h.getStatHechizo();
	}

	private void addHechizoPersonaje(char pos, Hechizo hechizo, int nivel) {
		_hechizos.add(new HechizoPersonaje(pos, hechizo, nivel));
		// _mapStatsHechizos.put(hechizo.getID(), hechizo.getStatsPorNivel(nivel));
	}

	public String stringHechizosParaSQL() {
		final StringBuilder str = new StringBuilder();
		for (HechizoPersonaje hp : _hechizos) {
			if (hp.getHechizo() == null) {
				continue;
			}
			if (str.length() > 0) {
				str.append(";");
			}
			str.append(hp.getHechizo().getID() + "," + hp.getNivel() + "," + hp.getPosicion());
		}
		return str.toString();
	}

	public String stringListaHechizos() {
		if (_encarnacion != null) {
			return _encarnacion.stringListaHechizos();
		}
		final StringBuilder str = new StringBuilder();
		for (HechizoPersonaje hp : _hechizos) {
			int hechizoID2 = hp.getHechizo().getID();
			if (MainServidor.HECHIZOS_CLASE_UNICOS2.containsKey(hechizoID2)) {
				if (_claseID != MainServidor.HECHIZOS_CLASE_UNICOS2.get(hechizoID2)) {
					continue;
				}
			}
			if (hp.getHechizo() == null) {
				continue;
			}
			if (str.length() > 0) {
				str.append(";");
			}
			str.append(hp.getHechizo().getID()).append("~").append(hp.getNivel()).append("~").append(hp.getPosicion());
		}
		return str.toString();
	}

	public boolean sePuedePonerEncarnacion() {
		return System.currentTimeMillis() - _tiempoUltEncarnacion > 60000;
	}

	public String stringParaListaPJsServer() {
		int hola = _nivel;
		int esOmega = 0;
		if (getNivelOmega() > 0) {
			hola = getNivelOmega();
			esOmega = getNivelOmega();
		} else {
			hola = _nivel;
			esOmega = 0;
		}
		final StringBuilder str = new StringBuilder("|");
		str.append(_id + ";");
		str.append(getNombre() + ";");
		str.append(hola + ";");
		str.append(getGfxID(false) + ";");
		str.append((_color1 > -1 ? Integer.toHexString(_color1) : -1) + ";");
		str.append((_color2 > -1 ? Integer.toHexString(_color2) : -1) + ";");
		str.append((_color3 > -1 ? Integer.toHexString(_color3) : -1) + ";");
		str.append(getStringAccesorios() + ";");
		str.append((_esMercante ? 1 : 0) + ";");
		str.append(MainServidor.SERVIDOR_ID + ";");
		if (MainServidor.MODO_HEROICO || MainServidor.MAPAS_MODO_HEROICO.contains(_mapa.getID())) {
			str.append((esFantasma() ? 1 : 0) + ";");
		} else {
			str.append("0;");
		}
		str.append(";");
		str.append(MainServidor.NIVEL_MAX_PERSONAJE + ";");
		str.append(esOmega);
		return str.toString();
	}

	public String stringParaListaPJsServerNuevo() {
		String clase = "";
		int hola = _nivel;
		int esOmega = 0;
		if (getNivelOmega() > 0) {
			hola = getNivelOmega();
			esOmega = getNivelOmega();
		} else {
			hola = _nivel;
			esOmega = 0;
		}
		switch (getClaseID(false)) {
			case 1:
				clase = "Feca";
				break;
			case 2:
				clase = "Osamodas";
				break;
			case 3:
				clase = "Anutrof";
				break;
			case 4:
				clase = "Sram";
				break;
			case 5:
				clase = "Xelor";
				break;
			case 6:
				clase = "Zurcarak";
				break;
			case 7:
				clase = "Aniripsa";
				break;
			case 8:
				clase = "Yopuka";
				break;
			case 9:
				clase = "Ocra";
				break;
			case 10:
				clase = "Sadida";
				break;
			case 11:
				clase = "Sacrogrito";
				break;
			case 12:
				clase = "Panda";
				break;
			case 13:
				clase = "Tymador";
				break;
			case 14:
				clase = "Zobal";
				break;
			case 15:
				clase = "Steamer";
				break;
			case 16:
				clase = "Selatrop";
				break;
			case 17:
				clase = "Hipermago";
				break;
			case 18:
				clase = "Uginak";
				break;
		}
		final StringBuilder str = new StringBuilder("|");
		str.append(getNombre() + ";");
		str.append(hola + ";");
		str.append(clase + ";");
		str.append(esOmega);
		return str.toString();
	}

	public void mostrarAmigosEnLinea(final boolean mostrar) {
		_mostrarAmigos = mostrar;
	}

	public void mostrarRates() {
		if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
			GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(this, "<b>Bienvenu sur " + MainServidor.NOMBRE_SERVER

					+ ": \nPrix ressource : " + MainServidor.PRECIO_SISTEMA_RECURSO
					+ (MainServidor.MODO_ALL_OGRINAS ? "" : ("\nKamas par : " + MainServidor.RATE_KAMAS))
					+ "   \nDrop par : " + MainServidor.RATE_DROP_NORMAL + "\nXP PVM par : " + MainServidor.RATE_XP_PVM
					+ "   \nXP PVP par : " + MainServidor.RATE_XP_PVP + "\nHonor par : " + MainServidor.RATE_HONOR
					+ "\nXP metier par : " + MainServidor.RATE_XP_OFICIO + " \nRate Elevage par : "
					+ MainServidor.RATE_CRIANZA_MONTURA + "\nTemps pour mettre bas par : "
					+ MainServidor.MINUTOS_GESTACION_MONTURA + " minutos" + " \nLes familiers se nourissant toutes les "
					+ MainServidor.MINUTOS_ALIMENTACION_MASCOTA + " minutos</b>");
		} else {
			if (!MainServidor.MODO_IMPACT) {
				GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(this, "<b>BIENVENIDO A " + MainServidor.NOMBRE_SERVER
						+ ": \nPRECIO RECURSO : " + MainServidor.PRECIO_SISTEMA_RECURSO + "\nKAMAS por : "
						+ MainServidor.RATE_KAMAS + "   \nDROP por : " + MainServidor.RATE_DROP_NORMAL
						+ "\nXP PVM por : " + MainServidor.RATE_XP_PVM + "   \nXP PVP por : " + MainServidor.RATE_XP_PVP
						+ "\nHONOR por : " + MainServidor.RATE_HONOR + "\nXP OFICIO por : "
						+ MainServidor.RATE_XP_OFICIO + " \nCRIANZA DE PAVOS por : " + MainServidor.RATE_CRIANZA_MONTURA
						+ "\nTIEMPO PARIR MONTURA por : " + MainServidor.MINUTOS_GESTACION_MONTURA + " minutos"
						+ " \nLAS MASCOTAS SE ALIMENTARAN CADA " + MainServidor.MINUTOS_ALIMENTACION_MASCOTA
						+ " minutos</b>");
			} else {
				GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(this,
						"<b>Los rates del servidor son</b>\n" + "\nExperiencia : x" + MainServidor.RATE_XP_PVM
								+ "\nKamas : x" + MainServidor.RATE_KAMAS + "\nDrop : x" + MainServidor.RATE_DROP_NORMAL
								+ "\nHonor : x" + MainServidor.RATE_HONOR + "\nOficio : x"
								+ MainServidor.RATE_XP_OFICIO);
			}
		}
	}

	public void mostrarTutorial() {
		if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
			if (!MainServidor.TUTORIAL_FR.isEmpty())
				GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(this, MainServidor.TUTORIAL_FR);
		} else {
			if (!MainServidor.TUTORIAL_ES.isEmpty())
				GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(this, MainServidor.TUTORIAL_ES);
		}
	}

	public String strRopaDelPJ() {// ropa del personaje, stuff del personaje
		return "Oa" + _id + "|" + getStringAccesorios();
	}

	public String stringGMmercante() {
		final StringBuilder str = new StringBuilder();
		str.append(_celda.getID() + ";");
		str.append("1;");
		str.append("0;");
		str.append(_id + ";");
		str.append(getNombreGM() + "^" + _colorNombre + ";");
		str.append("-5" + ",");
		int titulo = getTitulo(false);
		if (titulo > 0) {
			str.append(titulo);
			if (_titulos.containsKey(titulo) && _titulos.get(titulo) > -1) {
				str.append("**" + _titulos.get(titulo));
			}
		}
		str.append(";");
		str.append(getGfxID(false) + "^" + _talla + ";");
		str.append((_color1 == -1 ? "-1" : Integer.toHexString(_color1)) + ";");
		str.append((_color2 == -1 ? "-1" : Integer.toHexString(_color2)) + ";");
		str.append((_color3 == -1 ? "-1" : Integer.toHexString(_color3)) + ";");
		str.append(getStringAccesorios() + ";");
		// str.append(_miembroGremio.getGremio().getNombre() + ";" +
		// _miembroGremio.getGremio().getEmblema() + ";");
		if (_miembroGremio != null && _miembroGremio.getGremio().getCantidadMiembros() >= 10) {
			str.append(_miembroGremio.getGremio().getNombre() + ";" + _miembroGremio.getGremio().getEmblema() + ";");
		} else {
			str.append(";;");
		}
		str.append("0");
		return str.toString();
	}

	public String getCanjeados() {
		StringBuilder strx = new StringBuilder();
		boolean prim = false;
		for (Integer str : Canjeados) {
			if (prim)
				strx.append(",");
			strx.append(str);
			prim = true;
		}
		return strx.toString();
	}

	public int getTraje() {
		return _traje;
	}

	public String stringGM() {
		if (activa) { // 28
			if (this.getObjPosicion(Constantes.OBJETO_POS_TRAJE) != null) {
				_gfxID = 39;
				_traje = 39;
			} else {
				if (this.get_rostroGFX() > 0) {
					_gfxID = this.get_rostroGFX();
				} else {
					_gfxID = (_claseID * 10 + _sexo);
				}
				activa = false;
				_traje = 0;
			}
			GestorSQL.SALVAR_PERSONAJE(this, true);
		}
		final StringBuilder str = new StringBuilder();
		if (_pelea != null) {
			return "";
		}
		str.append(_celda.getID() + ";");
		str.append(_orientacion + ";");
		str.append(_ornamento + "^" + (MainServidor.PARAM_AURA_VIP ? ((esAbonado() ? 1 : 0)) : "") + ";");
		str.append(_id + ";");
		str.append(getNombreGM() + "^" + _colorNombre + ";");
		str.append(_claseID + ",");
		int titulo = getTitulo(false);
		if (titulo > 0) {
			str.append(titulo);
			if (_titulos.containsKey(titulo) && _titulos.get(titulo) > -1) {
				str.append("**" + _titulos.get(titulo));
			}
		}
		if (!_tituloVIP.isEmpty()) {
			str.append("~" + _tituloVIP);
		}
		str.append(";");
		str.append(getGfxID(true) + "^" + _talla + stringSeguidores() + ";");
		str.append(_sexo + ";");
		str.append(_alineacion + ",");
		str.append(_ordenNivel + ",");
		str.append((alasActivadas() ? _gradoAlineacion : "0") + ",");
		str.append((_id + _nivel) + ",");
		str.append((_deshonor > 0 ? 1 : 0) + ";");
		str.append((_color1 < 0 ? "-1" : Integer.toHexString(_color1)) + ";");
		str.append((_color2 < 0 ? "-1" : Integer.toHexString(_color2)) + ";");
		str.append((_color3 < 0 ? "-1" : Integer.toHexString(_color3)) + ";");
		str.append(getStringAccesorios() + ";");
		if (this.getMapa().getID() != 12200) {
			str.append(this.getCurAura()).append(";");
		} else {
			str.append("0;");
		}
		str.append(";");
		str.append(";");
		if (_miembroGremio != null && _miembroGremio.getGremio().getCantidadMiembros() >= 10) {
			str.append(_miembroGremio.getGremio().getNombre() + ";" + _miembroGremio.getGremio().getEmblema() + ";");
		} else {
			str.append(";;");
		}
		str.append(Integer.toString(_restriccionesBCharacter, 36) + ";");
		str.append((_montando && _montura != null ? _montura.getStringColor(stringColor()) : "") + ";");// 19
		str.append(Math.max(0, _totalStats.getStatsObjetos().getStatParaMostrar(Constantes.STAT_MAS_VELOCIDAD) / 1000f)
				+ ";");
		str.append(Mundo.getMasterResetExist(_resets) ? 0 : _resets + ";");
		str.append(";");
		str.append(_nivelOmega + ";");
		// str.append(buffClase);
		return str.toString();
	}

	public String getStringAccesoriosNPC() {
		final StringBuilder str = new StringBuilder();
		str.append(strObjEnPosParaOa(Constantes.OBJETO_POS_ARMA, Constantes.OBJETO_POS_ARMA) + ",");// arma
		str.append(strObjEnPosParaOa(Constantes.OBJETO_POS_SOMBRERO, (byte) 55) + ",");// sombrero
		str.append(strObjEnPosParaOa(Constantes.OBJETO_POS_CAPA, (byte) 54) + ",");// capa
		str.append(strObjEnPosParaOa(Constantes.OBJETO_POS_MASCOTA, (byte) 52) + ",");// mascota
		str.append(Integer.toHexString(12008) + ",");// escudo
		str.append(1 + ",");
		try {
			if (getGremio() != null) {
				final String[] args = getGremio().getEmblema().split(",");
				final String colorEscudo = Integer.toHexString(Integer.parseInt(args[1], 36));
				final int emblemaID = Integer.parseInt(args[2], 36);
				final int colorEmblema = Integer.parseInt(args[3], 36) + 1;
				str.append(colorEscudo + "~" + emblemaID + "~" + colorEmblema);
			}
		} catch (final Exception e) {
		}
		return str.toString();
	}

	public String getStringAccesorios() {
		final StringBuilder str = new StringBuilder();
		str.append(strObjEnPosParaOa(Constantes.OBJETO_POS_ARMA, Constantes.OBJETO_POS_ARMA) + ",");// arma
		str.append(strObjEnPosParaOa(Constantes.OBJETO_POS_SOMBRERO, (byte) 55) + ",");// sombrero
		str.append(strObjEnPosParaOa(Constantes.OBJETO_POS_CAPA, (byte) 54) + ",");// capa
		str.append(strObjEnPosParaOa(Constantes.OBJETO_POS_MASCOTA, (byte) 52) + ",");// mascota
		str.append(strObjEnPosParaOa(Constantes.OBJETO_POS_ESCUDO, (byte) 53) + ",");// escudo
		str.append(_rostro + ",");// change face
		try {
			if (getMiembroGremio() != null) {
				final String[] args = getMiembroGremio().getGremio().getEmblema().split(",");
				final String colorEscudo = Integer.toHexString(Integer.parseInt(args[1], 36));
				final int emblemaID = Integer.parseInt(args[2], 36);
				final int colorEmblema = Integer.parseInt(args[3], 36) + 1;
				str.append(colorEscudo + "~" + emblemaID + "~" + colorEmblema);
			}
		} catch (final Exception e) {
		}
		return str.toString();
	}

	public String stringStats() {
		final StringBuilder str = new StringBuilder("As");
		str.append(stringStatsComplemento());
		str.append(getIniciativa() + "|");
		int base = 0, equipo = 0, bendMald = 0, buff = 0, total = 0;
		total = _totalStats.getTotalStatConComplemento(Constantes.STAT_MAS_PROSPECCION, this.getPelea(), null);
		// prospeccion
		str.append(total + "|");
		final int[] stats = { 111, 128, 118, 125, 124, 123, 119, 126, 117, 182, 112, 142, 165, 138, 178, 225, 226, 220,
				115, 122, 160, 161, 244, 214, 264, 254, 240, 210, 260, 250, 241, 211, 261, 251, 242, 212, 262, 252, 243,
				213, 263, 253, 410, 413, 419, 416, 415, 417, 418 };
		for (final int s : stats) {
			if (LiderIP != null && getPelea() != null && s == 182) {
				base = 99;
				equipo = 99;
				bendMald = 99;
				buff = 99;
				total = 99;
			} else {
				base = _totalStats.getStatsBase().getStatParaMostrar(s);
				equipo = _totalStats.getStatsObjetos().getStatParaMostrar(s);
				bendMald = _totalStats.getStatsBendMald().getStatParaMostrar(s);
				buff = _totalStats.getStatsBuff().getStatParaMostrar(s);
				total = _totalStats.getTotalStatParaMostrar(s, this.getPelea(), this);
				str.append(base + "," + equipo + "," + bendMald + "," + buff + "," + (total) + "|");
			}
		}
		return str.toString();
	}

	public String stringStats2() {
		final StringBuilder str = new StringBuilder("Ak");
		str.append(stringStatsComplemento());
		return str.toString();
	}

	private String stringStatsComplemento() {
		final StringBuilder str = new StringBuilder();
		str.append(stringExperiencia(",") + "|");
		str.append(getKamas() + "|");
		if (_encarnacion != null) {
			str.append("0|0|");
		} else {
			str.append(_puntosStats + "|" + _puntosHechizos + "|");
		}
		str.append(_alineacion + "~");
		str.append(_alineacion + ",");// fake alineacion, si son diferentes se activa haveFakeAlignment
		str.append(_ordenNivel + ",");// orden alineacion
		str.append(_gradoAlineacion + ",");// nValue
		str.append(_honor + ",");// nHonour
		str.append(_deshonor + ",");// nDisgrace
		str.append((alasActivadas() ? "1" : "0") + "|");// bEnabled
		int PDV = getPDV();
		int PDVMax = getPDVMax();
		if (_pelea != null && _pelea.getLuchadorPorID(_id) != null) {
			final Luchador luchador = _pelea.getLuchadorPorID(_id);
			if (luchador != null) {
				PDV = luchador.getPDVConBuff();
				PDVMax = luchador.getPDVMaxConBuff();
			}
		}
		str.append(PDV + "," + PDVMax + "|");
		str.append(_energia + ",10000|");
		return str.toString();
	}

	public String stringExperiencia(final String c) {
		String data = Mundo.getExpPersonaje(_nivel, _nivelOmega) + c + experiencia + c
				+ Mundo.getExpPersonaje(_nivel + 1, _nivelOmega + 1);
		if (_nivel == MainServidor.NIVEL_MAX_PERSONAJE) {
			data = Mundo.getExpPersonaje(MainServidor.NIVEL_MAX_PERSONAJE, _nivelOmega) + c + experiencia + c
					+ Mundo.getExpPersonaje(MainServidor.NIVEL_MAX_PERSONAJE + 1, _nivelOmega + 1);
		}
		return data;
	}

	public int emoteActivado() {
		return _emoteActivado;
	}

	public void setEmoteActivado(final byte emoteActivado) {
		_emoteActivado = emoteActivado;
	}

	public Collection<Objeto> getObjetosTodos() {
		return _objetos.values();
	}

	public void actualizarObjEquipStats() {
		_totalStats.getStatsObjetos().clear();
		boolean esEncarnacion = false;
		final ArrayList<Integer> listaSetsEquipados = new ArrayList<Integer>();
		for (byte pos : Constantes.POSICIONES_EQUIPAMIENTO) {
			final Objeto objeto = getObjPosicion(pos);
			if (objeto == null) {
				continue;
			}
			if (objeto.getEncarnacion() != null) {
				esEncarnacion = true;
			}
			_totalStats.getStatsObjetos().acumularStats(objeto.getStats());
			final int setID = objeto.getObjModelo().getSetID();
			if (setID > 0 && !listaSetsEquipados.contains(setID)) {
				listaSetsEquipados.add(setID);
				final ObjetoSet OS = Mundo.getObjetoSet(setID);
				if (OS != null) {
					_totalStats.getStatsObjetos()
							.acumularStats(OS.getBonusStatPorNroObj(getNroObjEquipadosDeSet(setID)));
				}
			}
		} // actualizando
		if (esEncarnacion) {
			_montando = false;
		} else if (_montando && _montura != null) {
			_totalStats.getStatsObjetos().acumularStats(_montura.getStats());
		}
	}

	public Encarnacion getEncarnacionN() {
		return _encarnacion;
	}

	public int getIniciativa() {
		return Formulas.getIniciativa(getTotalStats(), getPorcPDV());
	}

	public TotalStats getTotalStats() {
		return _totalStats;
	}

	public TotalStats getTotalStatsPelea() {
		if (_encarnacion != null && _encarnacion.getTotalStats() != null) {
			return _encarnacion.getTotalStats();
		}
		return _totalStats;
	}

	public byte getOrientacion() {
		return _orientacion;
	}

	public void setOrientacion(final byte orientacion) {
		_orientacion = orientacion;
	}

	public int getPodsUsados() {
		int pods = 0;
		for (final Objeto objeto : _objetos.values()) {
			if (objeto == null) {
				continue;
			}
			if (objeto.getObjModelo() == null) {
				MainServidor.redactarLogServidorln(
						"El objeto " + objeto.getID() + ", objModelo " + objeto.getObjModeloID() + " nulo OBJETOS");
				continue;
			}
			pods += Math.abs(objeto.getObjModelo().getPeso() * objeto.getCantidad());
		}
		for (final Objeto objeto : _tienda.getObjetos()) {
			if (objeto == null) {
				continue;
			}
			if (objeto.getObjModelo() == null) {
				MainServidor.redactarLogServidorln(
						"El objeto " + objeto.getID() + ", objModelo " + objeto.getObjModeloID() + " nulo TIENDA");
				continue;
			}
			pods += Math.abs(objeto.getObjModelo().getPeso() * objeto.getCantidad());
		}
		return pods;
	}

	public int getPodsMaximos() {
		int pods = _totalStats.getTotalStatParaMostrar(Constantes.STAT_MAS_PODS, this.getPelea(), null);
		pods += _totalStats.getStatsBase().getStatParaMostrar(Constantes.STAT_MAS_FUERZA) * 5;
		pods += _totalStats.getStatsObjetos().getStatParaMostrar(Constantes.STAT_MAS_FUERZA) * 5;
		pods += _totalStats.getStatsBendMald().getStatParaMostrar(Constantes.STAT_MAS_FUERZA) * 5;
		for (final StatOficio SO : _statsOficios.values()) {
			if (SO == null) {
				continue;
			}
			if (SO.getPosicion() == 7) {
				continue;
			}
			pods += SO.getNivel() * 5;
			if (SO.getNivel() >= 100) {
				pods += 1000;
			}
		}
		pods *= MainServidor.RATE_PODS;
		if (pods < 1000) {
			pods = 1000;
		}
		return pods;
	}

	public int getPDV() {
		return _PDV;
	}
	public int getPDVMax() {
		return _PDVMax;
	}

	public void addPDV(int pdv) {
		setPDV(_PDV + pdv, false);
	}

	public void actualizarPDV(float porcPDV) {
		int oldPDVMAX = _PDVMax;
		if (porcPDV < 1) {
			porcPDV = getPorcPDV();
		} else if (porcPDV > 100) {
			porcPDV = 100;
		}
		actualizarPDVMax();
		int PDV = Math.round(porcPDV * _PDVMax / 100);
		setPDV(PDV, _PDVMax != oldPDVMAX);
	}

	private void actualizarPDVMax() {
		if (_encarnacion != null && _encarnacion.getTotalStats() != null) {
			_PDVMax = _encarnacion.getTotalStats().getTotalStatParaMostrar(Constantes.STAT_MAS_VITALIDAD,
					this.getPelea(), null);
		} else {
			_PDVMax = _clase.getPDV() + ((_nivel - 1) * 5);
			_PDVMax += getTotalStatsPelea().getTotalStatParaMostrar(Constantes.STAT_MAS_VITALIDAD, this.getPelea(),
					null);
		}
	}

	public void setPDV(int pdv, boolean cambioPDVMAX) {
		int oldPDV = _PDV;
		if (pdv > _PDVMax || MainServidor.PARAM_AUTO_RECUPERAR_TODA_VIDA) {
			_PDV = _PDVMax;
		} else if (pdv < 1) {
			_PDV = 1;
		} else {
			_PDV = pdv;
		}
		if (oldPDV == _PDV && !cambioPDVMAX) {
			return;
		}
		actualizarInfoGrupo();
		if (_pelea != null && _pelea.getFase() != Constantes.PELEA_FASE_COMBATE) {
			final Luchador luchador = _pelea.getLuchadorPorID(_id);
			if (luchador != null) {
				limitarVida();
				luchador.setPDVMAX(getPDVMax(), false);
				luchador.setPDV(getPDV());
				GestorSalida.ENVIAR_GTM_INFO_STATS_TODO_LUCHADORES_A_PERSO(this, _pelea);
			}
		}
	}

	public void limitarVida() {
		if (MainServidor.LIMITE_STATS_PVP_PERSONALIZADOS && MainServidor.LIMITE_VIDA > 0
				&& _pelea.getTipoPelea() == Constantes.PELEA_TIPO_PVP) {
			int vida = MainServidor.LIMITE_VIDA;
			if (MainServidor.LIMITE_VIDA_CLASE.containsKey(getClaseID(true))) {
				vida = MainServidor.LIMITE_VIDA_CLASE.get(getClaseID(true));
			}
			_PDVMax = Math.min(_PDVMax, vida);
			_PDV = Math.min(_PDV, vida);

		} else if (MainServidor.LIMITE_STATS_KOLI_PERSONALIZADOS && MainServidor.LIMITE_VIDA_KOLISEO > 0
				&& _pelea.getTipoPelea() == Constantes.PELEA_TIPO_KOLISEO) {
			int vida = MainServidor.LIMITE_VIDA_KOLISEO;
			if (MainServidor.LIMITE_VIDA_CLASE_KOLISEO.containsKey(getClaseID(true))) {
				vida = MainServidor.LIMITE_VIDA_CLASE_KOLISEO.get(getClaseID(true));
			}
			_PDVMax = Math.min(_PDVMax, vida);
			_PDV = Math.min(_PDV, vida);
		} else if (MainServidor.LIMITE_STATS_PERCO_PERSONALIZADOS && MainServidor.LIMITE_VIDA_PERCO > 0
				&& _pelea.getTipoPelea() == Constantes.PELEA_TIPO_RECAUDADOR) {
			int vida = MainServidor.LIMITE_VIDA_PERCO;
			_PDVMax = Math.min(_PDVMax, vida);
			_PDV = Math.min(_PDV, vida);
		}

	}

	public void fullPDV() {
		actualizarPDV(100);
	}

	public float getPorcPDV() {
		if (_PDVMax <= 0) {
			return 0;
		}
		float porc = _PDV * 100f / _PDVMax;
		porc = Math.max(0, porc);
		porc = Math.min(100, porc);
		return porc;
	}

	public void enviarmensajeRojo(String text) {
		if (enLinea())
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(this, text, "FF0000");
	}

	private void setDelayTimerRegenPDV(int tiempo) {
		if (_recuperarVida != null) {
			if (tiempo == 0) {
				_recuperarVida.stop();
			} else {
				_recuperarVida.setDelay(tiempo);
				_recuperarVida.restart();
			}
			if (_enLinea) {
				GestorSalida.ENVIAR_ILS_TIEMPO_REGENERAR_VIDA(this, tiempo);
			}
		}
	}

	private void crearTimerRegenPDV() {
		// regenerar vida PDV
		if (_recuperarVida == null) {
			_recuperarVida = new Timer(1000, new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					if (MainServidor.MODO_BATTLE_ROYALE) {
						_PDV--;
					} else {
						if (_pelea != null || _PDV >= _PDVMax) {
							// return;
						} else {
							_PDV++;
						}
					}
				}
			});
		}
		// _recuperarVida.restart();
	}

	public void setSentado(final boolean sentado) {
		if (MainServidor.MODO_BATTLE_ROYALE) {
			return;
		}
		_sentado = sentado;
		if (_sentado) {
			_ultPDV = _PDV;
		}
		final int tiempo = _sentado ? 500 : 1000;
		if (_enLinea) {
			if (!_sentado) {
				GestorSalida.ENVIAR_ILF_CANTIDAD_DE_VIDA(this, _PDV - _ultPDV);
				setPDV(_PDV, true);
			}
		}
		setDelayTimerRegenPDV(tiempo);
		if (!sentado && (_emoteActivado == Constantes.EMOTE_SENTARSE || _emoteActivado == Constantes.EMOTE_ACOSTARSE)) {
			_emoteActivado = 0;// no hay emote
		}
	}

	public byte getAlineacion() {
		return _alineacion;
	}

	private void actualizarInfoGrupo() {
		if (_grupo != null) {
			GestorSalida.ENVIAR_PM_ACTUALIZAR_INFO_PJ_GRUPO(_grupo, stringInfoGrupo());
		}
	}

	public void mostrarEmoteIcon(final String str) {
		try {
			if (_pelea == null) {
				GestorSalida.ENVIAR_cS_EMOTICON_MAPA(_mapa, _id, Integer.parseInt(str));
			} else {
				GestorSalida.ENVIAR_cS_EMOTE_EN_PELEA(_pelea, 7, _id, Integer.parseInt(str));
			}
		} catch (final Exception e) {
		}
	}

	public void salirPeleaTorre() {
		if (esMultiman()) {
			return;
		}
		if (_pelea == null) {
			return;
		}
		if (_pelea.getTipoPelea() != Constantes.PELEA_TIPO_DESAFIO && !_pelea.esEspectador(_id)) {
			disminuirTurnos();
		}
		setReconectado(false);
		setPelea(null);
		retornoMapa();
		this.setNbFight(this.getNbFightLose() + this.getNbFightWin());
		Sucess.launch(this, (byte) 7, null, 0);
		// GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(this);
		_ocupado = false;
		if (_energia < 1) {
			return;
		}
		Marche = false;
		this.setMushinPiso(0);
		int mapa = 29931;
		short celda = 200;
		teleport(mapa, celda);
		// GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(this);
	}

	public void salirPelea(boolean ptoSalvada, boolean borrarMapa, Personaje salirMaitre, int paraespera) {
		if (esMultiman()) {
			return;
		}
		if (_pelea == null) {
			return;
		}
		if (_pelea.getTipoPelea() != Constantes.PELEA_TIPO_DESAFIO && !_pelea.esEspectador(_id)) {
			disminuirTurnos();
		}
		setReconectado(false);
		setPelea(null);
		retornoMapa();
		this.setNbFight(this.getNbFightLose() + this.getNbFightWin());
		Sucess.launch(this, (byte) 7, null, 0);
		// GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(this);
		_ocupado = false;
		if (_energia < 1) {
			return;
		}
		if (salirMaitre != null && salirMaitre == this) {
			totalEspera = paraespera;
		} else {
			dueñoMaitre = salirMaitre;
		}
		Marche = false;
		if (ptoSalvada) {
			try {
				if (borrarMapa) {
					GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(_mapa, _id);
				}
				_mapa = Mundo.getMapa(_mapaSalvada);
				if (_nivel > 15 && _mapa.getSubArea().getArea().getSuperArea().getID() == 3) {
					_mapa = Mundo.getMapa((short) 7411);
					setCelda(_mapa.getCelda((short) 340));
				} else {
					setCelda(_mapa.getCelda(_celdaSalvada));
				}
			} catch (final Exception e) {
			}
		}
		// GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(this);
	}

	public void teleportPtoSalvada() {
		if (esMultiman()) {
			return;
		}
		int mapa = _mapaSalvada;
		short celda = _celdaSalvada;
		if (_nivel > 15 && _mapa.getSubArea().getArea().getSuperArea().getID() == 3) {
			mapa = (short) 7411;
			celda = (short) 340;
		}
		teleport(mapa, celda);
	}

	public void addScrollStat(int cantidad) {
		int[] s = { Constantes.STAT_MAS_FUERZA, Constantes.STAT_MAS_INTELIGENCIA, Constantes.STAT_MAS_SUERTE,
				Constantes.STAT_MAS_AGILIDAD, Constantes.STAT_MAS_SABIDURIA, Constantes.STAT_MAS_VITALIDAD };
		for (int i : s) {
			int anterior = _subStatsScroll.get(i);
			int valor = anterior + cantidad;
			_subStatsScroll.put(i, valor);
			_totalStats.getStatsBase().addStatID(i, cantidad);
		}
	}

	public void addScrollStat(int statID, int cantidad) {
		int anterior = _subStatsScroll.get(statID);
		int valor = anterior + cantidad;
		if (valor > MainServidor.LIMITE_SCROLL) {
			valor = MainServidor.LIMITE_SCROLL;
			cantidad = MainServidor.LIMITE_SCROLL - anterior;
		}
		_subStatsScroll.put(statID, valor);
		_totalStats.getStatsBase().addStatID(statID, cantidad);
	}

	public int getStatScroll(int statID) {
		if (_subStatsScroll.get(statID) == null) {
			return 0;
		}
		return _subStatsScroll.get(statID);
	}

	private void addStatBase(int statID, int cantidad) {
		int valor = 0;
		if (_subStatsBase.get(statID) != null) {
			valor = _subStatsBase.get(statID) + cantidad;
		}
		_subStatsBase.put(statID, valor);
		_totalStats.getStatsBase().addStatID(statID, cantidad);
	}

	public synchronized void boostStat2(final int tipo, int puntosUsar) {
		if (esMultiman()) {
			return;
		}
		if (_puntosStats <= 0) {
			return;
		}
		int statID = 0, usados = 0;
		switch (tipo) {
			case 10:
				statID = (Constantes.STAT_MAS_FUERZA);
				break;
			case 11:
				statID = (Constantes.STAT_MAS_VITALIDAD);
				break;
			case 12:
				statID = (Constantes.STAT_MAS_SABIDURIA);
				break;
			case 13:
				statID = (Constantes.STAT_MAS_SUERTE);
				break;
			case 14:
				statID = (Constantes.STAT_MAS_AGILIDAD);
				break;
			case 15:
				statID = (Constantes.STAT_MAS_INTELIGENCIA);
				break;
		}
		if (puntosUsar > _puntosStats) {
			puntosUsar = _puntosStats;
		}
		int valorStat = 0;
		BoostStat boost;
		boolean mod = false;
		while (true) {
			valorStat = _totalStats.getStatsBase().getStatParaMostrar(statID);
			boost = _clase.getBoostStat(statID, valorStat);
			usados += boost.getCoste();
			if (usados <= puntosUsar) {
				_puntosStats -= boost.getCoste();
				mod = true;
				addStatBase(statID, boost.getPuntos());
			} else {
				break;
			}
		}
		if (statID == Constantes.STAT_MAS_VITALIDAD) {// vitalidad
			actualizarPDV(0);
		}
		if (mod) {
			refrescarStuff(false, true, false);
		}
	}

	public void setMapa(final Mapa mapa) {
		_mapa = mapa;
	}

	public String stringObjetosABD() {
		final StringBuilder str = new StringBuilder();
		for (final Objeto obj : _objetos.values()) {
			if (str.length() > 0) {
				str.append("|");
			}
			str.append(obj.getID());
		}
		return str.toString();
	}

	public boolean estaMuteado() {
		return _cuenta.estaMuteado();
	}

	public Objeto getObjIdentInventario(final Objeto objeto, Objeto prohibido) {
		if (objeto.puedeTenerStatsIguales()) {
			for (final Objeto obj : _objetos.values()) {
				if (obj.getPosicion() != Constantes.OBJETO_POS_NO_EQUIPADO) {
					continue;
				}
				if (objeto.getID() == obj.getID()) {
					continue;
				}
				// boolean prohibido = false;
				// for (Objeto o : prohibidos) {
				// if (o.getID() == obj.getID()) {
				// prohibido = true;
				// break;
				// }
				// }
				// if (prohibido) {
				// continue;
				// }
				if (prohibido != null && prohibido.getID() == obj.getID()) {
					continue;
				}
				if (obj.getObjModeloID() == objeto.getObjModeloID() && obj.sonStatsIguales(objeto)) {
					return obj;
				}
			}
		}
		return null;
	}

	public boolean addObjIdentAInventario(final Objeto objeto, final boolean eliminar) {
		if (_objetos.containsKey(objeto.getID())) {
			return false;
		}
		// tipo piedra de alma y mascota
		Objeto igual = getObjIdentInventario(objeto, null);
		if (igual != null) {
			igual.setCantidad(igual.getCantidad() + objeto.getCantidad());
			GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(this, igual);
			if (eliminar && objeto.getID() > 0) {
				Mundo.eliminarObjeto(objeto.getID());
			}
			return true;
		}
		addObjetoConOAKO(objeto, true);
		return false;
	}

	public void addObjDropPelea(final Objeto objeto, final boolean eliminar) {
		if (_objetos.containsKey(objeto.getID()))
			return;
		// tipo piedra de alma y mascota
		Objeto igual = getObjIdentInventario(objeto, null);
		if (igual != null) {
			igual.setCantidad(igual.getCantidad() + objeto.getCantidad());
			_dropPelea.put(igual, false);
			if (eliminar && objeto.getID() > 0) {
				Mundo.eliminarObjeto(objeto.getID());
			}
			return;
		}
		addObjetoConOAKO(objeto, false);
		_dropPelea.put(objeto, true);
	}

	public void addObjApilado(final Objeto objeto, final boolean eliminar) {
		if (_objetos.containsKey(objeto.getID()))
			return;
		if (objeto.getPosicion() == Constantes.OBJETO_POS_NO_EQUIPADO) {
			// tipo piedra de alma y mascota
			Objeto igual = getObjIdentInventario(objeto, null);
			if (igual != null) {
				igual.setCantidad(igual.getCantidad() + objeto.getCantidad());
				if (eliminar && objeto.getID() > 0) {
					Mundo.eliminarObjeto(objeto.getID());

				}
				GestorSQL.SALVAR_OBJETO(igual);
				return;
			}
		}
		addObjetoConOAKO(objeto, false);
	}

	public void addObjetoConOAKO(final Objeto objeto, final boolean enviarOAKO) {
		if (objeto.getID() == 0) {
			Mundo.addObjeto(objeto, false);
		}
		_objetos.put(objeto.getID(), objeto);
		byte pos = objeto.getPosicion();
		if (Constantes.esPosicionObjeto(pos)) {
			if (Constantes.esPosicionEquipamiento(pos)) {
				boolean desequipar = false;
				if (objeto.getObjModelo().getNivel() > _nivel) {
					desequipar = true;
				} else if (!puedeEquiparRepetido(objeto.getObjModelo(), 1)) {
					desequipar = true;
				}
				if (desequipar) {
					pos = Constantes.OBJETO_POS_NO_EQUIPADO;
					objeto.setPosicion(pos);
				}
			}
			cambiarPosObjeto(objeto, Constantes.OBJETO_POS_NO_EQUIPADO, pos, true);
		}
		if (_enLinea && enviarOAKO) {
			GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(this, objeto);
		}
	}

	public void borrarOEliminarConOR(final int id, final boolean eliminar) {
		if (borrarObjeto(id) && _enLinea) {
			GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(this, id);
		}
		if (eliminar) {
			Mundo.eliminarObjeto(id);
		}
	}

	public boolean borrarObjeto(final int id) {
		if (_objetos.get(id) != null) {
			byte pos = _objetos.get(id).getPosicion();
			if (pos == Constantes.OBJETO_POS_NO_EQUIPADO || pos >= _objPos49.length) {
				return _objetos.remove(id) != null;
			}
			if (_objPos49[pos].getID() == id) {
				cambiarPosObjeto(null, pos, Constantes.OBJETO_POS_NO_EQUIPADO, true);
			}
		}
		return _objetos.remove(id) != null;
	}

	public void cambiarPosObjeto(final Objeto obj, final byte oldPos, final byte newPos, boolean refrescarStuff) {
		if (oldPos == newPos) {
			return;
		}
		if (oldPos != Constantes.OBJETO_POS_NO_EQUIPADO && oldPos < _objPos49.length) {
			if (_objPos49[oldPos] != null) {
				if (obj == null || obj.getID() == _objPos49[oldPos].getID()) {
					_objPos49[oldPos].setPosicion(Constantes.OBJETO_POS_NO_EQUIPADO);
					_objPos49[oldPos] = null;
				}
			}
		}
		if (newPos != Constantes.OBJETO_POS_NO_EQUIPADO && newPos < _objPos49.length) {
			_objPos49[newPos] = obj;
		}
		if (obj != null) {
			obj.setPosicion(newPos);
		}
		if (_enLinea) {
			boolean visual = false;
			if (Constantes.esPosicionVisual(newPos) || Constantes.esPosicionVisual(oldPos)) {
				visual = true;
			}
			if (Constantes.esPosicionEquipamiento(newPos) || Constantes.esPosicionEquipamiento(oldPos)) {
				actualizarObjEquipStats();
				if (refrescarStuff) {
					refrescarStuff(false, true, visual);
				}
			}
		}
	}

	public boolean restarCantObjOEliminar(final int idObjeto, long cantidad, final boolean eliminar) {
		try {
			final Objeto obj = _objetos.get(idObjeto);
			if (obj == null) {
				return false;
			}
			if (cantidad > obj.getCantidad()) {
				cantidad = obj.getCantidad();
			}
			if (obj.getCantidad() - cantidad > 0) {
				obj.setCantidad(obj.getCantidad() - cantidad);
				if (_enLinea) {
					GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(this, obj);
				}
			} else {
				borrarOEliminarConOR(obj.getID(), eliminar);
			}
			return true;
		} catch (final Exception e) {
		}
		return false;
	}

	public boolean tenerYEliminarObjPorModYCant(final int idModelo, int cantidad) {
		final ArrayList<Objeto> listaObjBorrar = new ArrayList<Objeto>();
		for (final Objeto obj : _objetos.values()) {
			if (obj.getObjModeloID() != idModelo) {
				continue;
			}
			if (obj.getCantidad() >= cantidad) {
				final long nuevaCant = obj.getCantidad() - cantidad;
				if (nuevaCant > 0) {
					obj.setCantidad(nuevaCant);
					GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(this, obj);
				} else {
					listaObjBorrar.add(obj);
				}
				for (final Objeto objBorrar : listaObjBorrar) {
					borrarOEliminarConOR(objBorrar.getID(), true);
				}
				return true;
			} else {
				cantidad -= obj.getCantidad();
				listaObjBorrar.add(obj);
			}
		}
		return false;
	}

	public int restarObjPorModYCant(final int idModelo, long cantidad) {
		final ArrayList<Objeto> listaObjBorrar = new ArrayList<Objeto>();
		int eliminados = 0;
		for (final Objeto obj : _objetos.values()) {
			if (obj.getObjModeloID() != idModelo) {
				continue;
			}
			if (cantidad <= 0) {
				break;
			}
			if (obj.getCantidad() - cantidad > 0) {
				eliminados += cantidad;
				obj.setCantidad(obj.getCantidad() - cantidad);
				GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(this, obj);
				break;
			} else {
				cantidad -= obj.getCantidad();
				eliminados += obj.getCantidad();
				listaObjBorrar.add(obj);
			}
		}
		for (final Objeto objBorrar : listaObjBorrar) {
			borrarOEliminarConOR(objBorrar.getID(), true);
		}
		return eliminados;
	}

	public int eliminarPorObjModeloRecibidoDesdeMinutos(final int objModeloID, int recibidoMinutos) {
		final ArrayList<Objeto> lista = new ArrayList<Objeto>();
		lista.addAll(_objetos.values());
		int eliminados = 0;
		for (final Objeto obj : lista) {
			if (obj.getObjModeloID() != objModeloID) {
				continue;
			}
			if (recibidoMinutos > 0) {
				if (obj.tieneStatTexto(Constantes.STAT_RECIBIDO_EL)) {
					if (obj.getDiferenciaTiempo(Constantes.STAT_RECIBIDO_EL, 60 * 1000) < recibidoMinutos) {
						continue;
					}
				}
			}
			eliminados += obj.getCantidad();
			borrarOEliminarConOR(obj.getID(), true);
		}
		return eliminados;
	}

	public void println(String txt) {
		System.out.println(txt);
	}

	public Objeto getObjPosicion(final byte pos) {
		try {
			if (pos > Constantes.OBJETO_POS_NO_EQUIPADO) {
				return _objPos49[pos];
			}
		} catch (final Exception e) {
		}
		return null;
	}

	public Objeto getObjeto(final int id) {
		return _objetos.get(id);
	}

	public boolean tieneObjetoID(final int id) {
		return _objetos.containsKey(id);
	}

	public boolean tieneObjPorModYCant(final int idModelo, long cantidad) {
		for (final Objeto obj : _objetos.values()) {
			if (obj.getObjModeloID() != idModelo) {
				continue;
			}
			if (obj.getCantidad() >= cantidad) {
				return true;
			} else {
				cantidad -= obj.getCantidad();
			}
		}
		return false;
	}

	public boolean tieneObjPorModYNivel2(final int idModelo, int nivel) {
		for (final Objeto obj : _objetos.values()) {
			if (obj.getObjModeloID() != idModelo) {
				continue;
			}
			if (Constantes.esPosicionEquipamiento(obj.getPosicion())) {
				continue;
			}
			if (!obj.tieneStatTexto(Constantes.STAT_NIVEL)) {
				continue;
			}
			String statsTemp = obj.getParamStatTexto(Constantes.STAT_NIVEL, 3);
			int nivelObjeto = Integer.parseInt(statsTemp, 16);
			if (nivelObjeto >= nivel)
				return true;
		}
		return false;
	}

	public boolean tieneObjPorModYNivel(final int idModelo, int nivel) {
		for (final Objeto obj : _objetos.values()) {
			if (obj.getObjModeloID() != idModelo) {
				continue;
			}
			if (Constantes.esPosicionEquipamiento(obj.getPosicion())) {
				continue;
			}
			if (!obj.tieneStatTexto(Constantes.STAT_NIVEL)) {
				continue;
			}
			String statsTemp = obj.getParamStatTexto(Constantes.STAT_NIVEL, 3);
			int nivelObjeto = Integer.parseInt(statsTemp, 16);
			if (nivel == nivelObjeto)
				return true;
		}
		return false;
	}

	public String strListaObjetos() {
		final StringBuilder str = new StringBuilder();
		TreeMap<Integer, Objeto> objetos = new TreeMap<>();
		objetos.putAll(_objetos);
		for (final Objeto obj : objetos.values()) {
			if (obj == null) {
				continue;
			}
			str.append(obj.stringObjetoConGuiño());
		}
		return str.toString();
	}

	public String getObjetosPersonajePorID(final String separador) {
		final StringBuilder str = new StringBuilder();
		for (final int id : _objetos.keySet()) {
			if (str.length() != 0) {
				str.append(separador);
			}
			str.append(id);
		}
		return str.toString();
	}

	public void venderObjetos(String packet) {
		for (String str : packet.split(";")) {
			try {
				final String[] infos = str.split(Pattern.quote("|"));
				int id = Integer.parseInt(infos[0]);
				long cant = Long.parseLong(infos[1]);
				final Objeto objeto = _objetos.get(id);
				if (objeto.getPosicion() != Constantes.OBJETO_POS_NO_EQUIPADO
						|| objeto.getObjModelo().getTipo() == Constantes.OBJETO_TIPO_OBJETO_DE_BUSQUEDA) {
					GestorSalida.ENVIAR_ESE_ERROR_VENTA(this);
					continue;
				}
				if (objeto.tieneStatTexto(Constantes.STAT_PERTENECE_Y_NO_VENDER)) {
					if (!objeto.getParamStatTexto(Constantes.STAT_PERTENECE_Y_NO_VENDER, 4)
							.equalsIgnoreCase(getNombre())) {
						return;
					}
				}
				if (objeto.getCantidad() < cant) {
					cant = objeto.getCantidad();
				}
				final long kamas = objeto.getObjModelo().getKamas() * cant;
				long ogrinas = 0;
				try {
					ogrinas = objeto.getObjModelo().getOgrinas() * cant;
				} catch (final Exception e) {
					continue;
				}
				if (ogrinas < 0 || kamas < 0) {
					GestorSalida.ENVIAR_ESE_ERROR_VENTA(this);
					continue;
				}
				if (kamas == 0 && ogrinas > 0) {
					if (!MainServidor.PARAM_DEVOLVER_OGRINAS) {
						continue;
					}
					GestorSQL.ADD_OGRINAS_CUENTA((long) (ogrinas * MainServidor.FACTOR_DEVOLVER_OGRINAS),
							_cuenta.getID(), this);
				} else {
					addKamas(kamas / 10, false, false);
				}
				if (objeto.getCantidad() - cant < 1) {
					borrarOEliminarConOR(id, true);
				} else {
					objeto.setCantidad(objeto.getCantidad() - cant);
					GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(this, objeto);
				}
				Thread.sleep(150);
			} catch (final Exception e) {
				GestorSalida.ENVIAR_ESE_ERROR_VENTA(this);
			}
		}
		GestorSalida.ENVIAR_ESK_VENDIDO(this);
		GestorSalida.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(this);
		GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(this);
	}

	public void setHaciendoTrabajo(Trabajo JA) {
		_haciendoTrabajo = JA;
	}

	public Trabajo getHaciendoTrabajo() {
		return _haciendoTrabajo;
	}

	public boolean bloquearXP = false;
	private long ExpBonusMono = 0;

	public long getExpMono() {
		return this.ExpBonusMono;
	}

	public void setExpMono(long cant) {
		this.ExpBonusMono = cant;
	}

	public void addExperiencia(long Expadd, boolean mensaje) {
		if (esMultiman()) {
			return;
		}
		if (bloquearXP) {
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(this,
					"Has sido castigado 1 dia entero por Palmad, tu XP estara bloqueada y no ganaras nada de xp por eso. Si queire ganar, debes tener un mejor comportamiento, y aprender que quejandote no solucionaras nada :)");
			return;
		}
		if (MainServidor.EXPERIENCIA_X_RESET.containsKey(_resets)) {
			Expadd = (long) (Expadd * MainServidor.EXPERIENCIA_X_RESET.get(_resets) / 100);
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(this, "La xp ganada es: " + Expadd);
		}
		int exNivel = _nivel;
		int nuevoNivel = _nivel;
		int exNivelOmega = _nivelOmega;
		int nuevoNivelOmega = _nivelOmega;
		if (_encarnacion != null) {
			exNivel = _encarnacion.getNivel();
			_encarnacion.addExperiencia(Expadd, this);
			nuevoNivel = _encarnacion.getNivel();
		} else {
			_experienciaDia += Expadd;
			// if (_experiencia <
			// Mundo.getExpPersonaje(MainServidor.NIVEL_MAX_PERSONAJE,MainServidor.NIVEL_MAX_OMEGA))
			// {
			experiencia = experiencia.add(new BigInteger(Expadd + ""));
			// }
			while (experiencia.compareTo(Mundo.getExpPersonaje(_nivel + 1, 0)) != -1
					&& _nivel < MainServidor.NIVEL_MAX_PERSONAJE) {
				subirNivel(false);
			}
			if (_resets > 14) {
				while (experiencia
						.compareTo(Mundo.getExpPersonaje(MainServidor.NIVEL_MAX_PERSONAJE + 1, _nivelOmega + 1)) != -1
						&& _nivelOmega < MainServidor.NIVEL_MAX_OMEGA) {
					subirNivelOmega(false);
				}
			}
			nuevoNivel = _nivel;
			nuevoNivelOmega = _nivelOmega;
		}
		if (exNivel < nuevoNivel || exNivelOmega < nuevoNivelOmega) {
			fullPDV();
		}
		if (_enLinea) {
			if (mensaje) {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "08;" + Expadd);
			}
			if (exNivel < nuevoNivel) {
				GestorSalida.ENVIAR_AN_MENSAJE_NUEVO_NIVEL(this, nuevoNivel);
				GestorSalida.ENVIAR_SL_LISTA_HECHIZOS(this);
			}
			if (exNivelOmega < nuevoNivelOmega) {
				GestorSalida.ENVIAR_AN_MENSAJE_NUEVO_OMEGA(this, nuevoNivelOmega);
				GestorSalida.ENVIAR_SL_LISTA_HECHIZOS(this);
				try {
					Omega temp = Mundo.getOmega(_nivelOmega);
					Thread.sleep(300L);
					if (temp != null) {
						temp.aplicarPacket(this);
					}
				} catch (Exception e) {
				}
			}

			GestorSalida.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(this);
		}
	}

	public void subirHastaNivel(int nivel) {
		if (nivel > _nivel) {
			while (_nivel < nivel) {
				subirNivel(true);
			}
			fullPDV();
			if (_enLinea) {
				GestorSalida.ENVIAR_SL_LISTA_HECHIZOS(this);
				GestorSalida.ENVIAR_AN_MENSAJE_NUEVO_NIVEL(this, _nivel);
				GestorSalida.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(this);
			}
		}
	}

	public void subirHastaNivelOmega(int nivel) {
		if (nivel > _nivelOmega) {
			while (_nivelOmega < nivel) {
				subirNivelOmega(true);
			}
			fullPDV();
			if (_enLinea) {
				GestorSalida.ENVIAR_AN_MENSAJE_NUEVO_OMEGA(this, _nivelOmega);
				GestorSalida.ENVIAR_SL_LISTA_HECHIZOS(this);
				GestorSalida.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(this);
				try {
					Omega temp = Mundo.getOmega(_nivelOmega);
					if (temp != null) {
						Thread.sleep(300L);
						temp.aplicarPacket(this);
					}

				} catch (Exception e) {
				}
			}
		}
	}

	private void subirNivelOmega(final boolean expDeNivel) {
		if (esMultiman()) {
			return;
		}
		if (_nivelOmega == MainServidor.NIVEL_MAX_OMEGA || _encarnacion != null) {
			return;
		}
		_nivelOmega += 1;
		_puntosStats += MainServidor.PUNTOS_STATS_POR_NIVEL_OMEGA;
		/*
		 * Omega temp = Mundo.getOmega(_nivelOmega); if (temp != null) {
		 * temp.aplicarAccion(this); }
		 */
		if (expDeNivel) {
			experiencia = Mundo.getExpPersonaje(_nivel, MainServidor.NIVEL_MAX_PERSONAJE);
		}
		Sucess.launch(this, (byte) 1, null, 0);
	}

	private void subirNivel(final boolean expDeNivel) {
		if (esMultiman()) {
			return;
		}
		if (_nivel == MainServidor.NIVEL_MAX_PERSONAJE || _encarnacion != null) {
			return;
		}
		_nivel += 1;
		if (!MainServidor.MODO_PVP && _nivel == MainServidor.NIVEL_MAX_PERSONAJE
				&& MainServidor.PARAM_MOSTRAR_MENSAJE_NIVEL_MAXIMO) {
			GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS("1CONGRATULATIONS_LVL_MAX;" + getNombre());
		}
		_puntosStats += MainServidor.PUNTOS_STATS_POR_NIVEL;
		_puntosHechizos += MainServidor.PUNTOS_HECHIZO_POR_NIVEL;
		if (_nivel == 100) {
			_totalStats.getStatsBase().addStatID(Constantes.STAT_MAS_PA, 1);
		}
		if (_koliseo != null) {
			this.getServidorSocket().koliseo_Desinscribirse();
		}
		_clase.aprenderHechizo(this, _nivel);
		if (expDeNivel) {
			experiencia = Mundo.getExpPersonaje(_nivel, 0);
		}
	}

	public void cambiarNivelYAlineacion(int nivel, byte alineacion) {
		if (MainServidor.NIVEL_MAX_ESCOGER_NIVEL <= 1) {
			return;
		}
		if (nivel > MainServidor.NIVEL_MAX_ESCOGER_NIVEL) {
			nivel = MainServidor.NIVEL_MAX_ESCOGER_NIVEL;
		}
		subirHastaNivel(nivel);
		if (MainServidor.MODO_PVP) {
			if ((alineacion == Constantes.ALINEACION_BONTARIANO || alineacion == Constantes.ALINEACION_BRAKMARIANO)
					&& alineacion != _alineacion) {
				cambiarAlineacion(alineacion, false);
			}
			if (_alineacion == Constantes.ALINEACION_NEUTRAL) {
				GestorSalida.ENVIAR_bA_ESCOGER_NIVEL(this);
			}
		}
		_recienCreado = false;
	}

	public Map<Byte, StatOficio> getStatsOficios() {
		return _statsOficios;
	}

	public StatOficio getStatOficioPorID(final int oficioID) {
		for (final StatOficio SO : _statsOficios.values()) {
			if (SO.getOficio().getID() == oficioID) {
				return SO;
			}
		}
		return null;
	}

	public StatOficio getStatOficioPorTrabajo(final int skillID) {
		for (final StatOficio SO : _statsOficios.values()) {
			if (SO.esValidoTrabajo(skillID)) {
				return SO;
			}
		}
		return null;
	}

	public int getNivelStatOficio(int oficioID) {
		try {
			StatOficio so = getStatOficioPorID(oficioID);
			if (so != null)
				return so.getNivel();
		} catch (Exception e) {
		}
		return 0;
	}

	public String stringOficios() {
		final StringBuilder str = new StringBuilder();
		for (byte i = 0; i < 23; i++) {
			try {
				_statsOficios.get(i).getNivel();// es para activar el exception
				if (str.length() > 0) {
					str.append(";");
				}
				str.append(_statsOficios.get(i).getOficio().getID() + "," + _statsOficios.get(i).getExp());
			} catch (Exception e) {
			}
		}
		return str.toString();
	}

	public boolean olvidarOficio(final int oficio) {
		try {
			byte id = -1;
			for (Entry<Byte, StatOficio> s : _statsOficios.entrySet()) {
				if (s.getValue().getOficio().getID() == oficio) {
					id = s.getKey();
					break;
				}
			}
			if (id != -1) {
				_statsOficios.remove(id);
				GestorSalida.ENVIAR_JR_OLVIDAR_OFICIO(this, oficio);
			}
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	public int aprenderOficio(final Oficio oficio, int exp) {
		if (!MainServidor.PARAM_PERMITIR_OFICIOS) {
			return -1;
		}
		/// boolean esMago = Constantes.esOficioMago(oficio.getID());
		byte pos = -1;
		for (final StatOficio SO : _statsOficios.values()) {
			if (SO.getPosicion() == 7) {
				continue;
			}
		}
		for (final StatOficio SO : _statsOficios.values()) {
			if (SO.getPosicion() == 7) {
				continue;
			}
			if (SO.getOficio().getID() == oficio.getID()) {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "111");
				return -1;
			}
		}
		for (byte p = 0; p < 23; p++) {
			if (_statsOficios.get(p) == null) {
				pos = p;
				break;
			}
		}
		if (pos == -1) {
			if (_enLinea) {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "19");
			}
			return -1;
		}
		final StatOficio statOficio = new StatOficio(pos, oficio, exp);
		_statsOficios.put(pos, statOficio);
		if (_enLinea) {
			GestorSalida.ENVIAR_Im_INFORMACION(this, "02;" + oficio.getID());
			GestorSalida.ENVIAR_JS_SKILL_DE_OFICIO(this, statOficio);
			GestorSalida.ENVIAR_JX_EXPERINENCIA_OFICIO(this, statOficio);
			GestorSalida.ENVIAR_JO_OFICIO_OPCIONES(this, statOficio);
			// verificarHerramientOficio();
		}
		return pos;
	}

	public boolean tieneDon(int id, int nivel) {
		Especialidad esp = Mundo.getEspecialidad(_orden, _ordenNivel);
		if (esp == null) {
			return false;
		}
		for (Don don : esp.getDones()) {
			if (don.getID() == id && don.getNivel() >= nivel) {
				return true;
			}
		}
		return false;
	}

	public boolean tieneObjModeloEquipado(final int id) {
		for (byte pos : Constantes.POSICIONES_EQUIPAMIENTO) {
			final Objeto obj = getObjPosicion(pos);
			if (obj == null) {
				continue;
			}
			if (obj.getObjModeloID() == id) {
				return true;
			}
		}
		return false;
	}

	private int cantEquipadoModelo(final int id) {
		int i = 0;
		for (byte pos : Constantes.POSICIONES_EQUIPAMIENTO) {
			final Objeto obj = getObjPosicion(pos);
			if (obj == null) {
				continue;
			}
			if (obj.getObjModeloID() == id) {
				i++;
			}
		}
		return i;
	}

	public void setInvitandoA(final Personaje invitando, String tipo) {
		_invitandoA = invitando;
		_tipoInvitacion = tipo;
	}

	public Personaje getInvitandoA() {
		return _invitandoA;
	}

	public void setInvitador(final Personaje invitando, String tipo) {
		_invitador = invitando;
		_tipoInvitacion = tipo;
	}

	public Personaje getInvitador() {
		return _invitador;
	}

	public String getTipoInvitacion() {
		return _tipoInvitacion;
	}

	public String stringInfoGrupo() {
		final StringBuilder str = new StringBuilder();
		str.append(_id + ";");
		str.append(getNombre() + ";");
		str.append(getGfxID(false) + ";");
		str.append(_color1 + ";");
		str.append(_color2 + ";");
		str.append(_color3 + ";");
		str.append(getStringAccesorios() + ";");
		str.append(_PDV + "," + _PDVMax + ";");
		str.append(_nivel + ";");
		str.append(getIniciativa() + ";");
		str.append(
				_totalStats.getTotalStatConComplemento(Constantes.STAT_MAS_PROSPECCION, this.getPelea(), null) + ";");
		str.append("1");
		return str.toString();
	}

	public int getNroObjEquipadosDeSet(final int setID) {
		int nro = 0;
		for (byte pos : Constantes.POSICIONES_EQUIPAMIENTO) {
			final Objeto obj = getObjPosicion(pos);
			if (obj == null) {
				continue;
			}
			if (obj.getObjModelo().getSetID() == setID) {
				nro++;
			}
		}
		return nro;
	}

	public boolean puedeIniciarTrabajo(final int skillID, final ObjetoInteractivo objInterac,
									   final AccionDeJuego unicaID, final Celda celda) {
		// System.out.println(5);
		try {
			StatOficio statOficio = getStatOficioPorTrabajo(skillID);
			if (statOficio == null) {
				return false;
			}
			// System.out.println(6);
			return statOficio.iniciarTrabajo(skillID, this, objInterac, unicaID, celda);
		} catch (Exception e) {
		}
		// esta lejos para realizar el trabajo
		return false;
	}

	public boolean finalizarTrabajo(final int skillID) {
		StatOficio skill = getStatOficioPorTrabajo(skillID);
		if (skill == null) {
			return false;
		}
		return skill.finalizarTrabajo(this);
	}

	public void iniciarAccionEnCelda(AccionDeJuego GA) {
		short celdaID = -1;
		int accion = -1;
		try {
			celdaID = Short.parseShort(GA._packet.split(";")[0]);
			accion = Integer.parseInt(GA._packet.split(";")[1]);
		} catch (Exception e) {
		}
		if (celdaID == -1 || accion == -1)
			return;
		/*
		 * if (!_mapa.getCelda(celdaID).puedeHacerAccion(accion, _pescarKuakua)) return;
		 */// TODO:
		// System.out.println(2);
		_mapa.getCelda(celdaID).iniciarAccion(this, GA);
	}

	public void setCasa(Casa casa) {
		_casaDentro = casa;
	}

	public Casa getCasa() {
		return _casaDentro;
	}
	/*
	 * public boolean puedeIniciarAccionEnCelda(final AccionDeJuego AJ) { try {
	 * System.out.println(1); if (AJ == null) { return false; } short celdaID =
	 * Short.parseShort(AJ.getPathPacket().split(";")[0]); int skillID =
	 * Integer.parseInt(AJ.getPathPacket().split(";")[1]); Celda celda =
	 * _mapa.getCelda(celdaID); switch (_orientacion) { case 0: case 2: case 4: case
	 * 6: case 8: cambiarOrientacionADiagonal((byte) 7); break; }
	 * System.out.println(2); boolean puede = false; if
	 * (celda.puedeHacerAccion(skillID, _pescarKuakua)) { celda.iniciarAccion(this,
	 * AJ); } return puede; } catch (final Exception e) { String error =
	 * "EXCEPTION iniciarAccionEnCelda AJ.getPacket(): " + AJ.getPathPacket() +
	 * " e: " + e.toString(); GestorSalida.ENVIAR_BN_NADA(this, error);
	 * MainServidor.redactarLogServidorln(error); }
	 * GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(this,
	 * "Aun no cumples los requisitos para realizar esta accion");
	 *
	 * // si no puede realizar la accion porque no esta cerca al IO return false; }
	 */

	public boolean finalizarAccionEnCelda(final AccionDeJuego AJ) {
		try {
			if (AJ != null) {
				short celdaID = Short.parseShort(AJ.getPathPacket().split(";")[0]);
				return _mapa.getCelda(celdaID).finalizarAccion(this, AJ);
			}
		} catch (final Exception e) {
			String error = "EXCEPTION finalizarAccionEnCelda e:" + e.toString();
			GestorSalida.ENVIAR_BN_NADA(this, error);
			MainServidor.redactarLogServidorln(error);
		}
		return false;
	}

	/*
	 * public boolean finAccionMoverse(final AccionDeJuego AJ, String packet) { byte
	 * bug = 0; try { final boolean ok = packet.charAt(2) == 'K'; bug = 1; if
	 * (MainServidor.PARAM_ANTI_SPEEDHACK && ok) { boolean correr = AJ.getCeldas() >
	 * 5; long debeSer = 0; if (_montando) { if (correr) { debeSer = 130; } else {
	 * debeSer = 280; } } else { if (correr) { debeSer = 170; } else { debeSer =
	 * 390; } } long ping = 0; try { ping = _cuenta.getSocket().getPing(); } catch
	 * (Exception e) { } float fVelocidad = (Math.max(0,
	 * _totalStats.getTotalStatParaMostrar(Constantes.STAT_MAS_VELOCIDAD,
	 * this.getPelea(), null) / 1000f)); debeSer = (long) ((debeSer *
	 * AJ.getCeldas()) / (1 + fVelocidad)); long fue = (System.currentTimeMillis() -
	 * AJ.getTiempoInicio()) + ping; if (debeSer > fue) {
	 * MainServidor.redactarLogServidorln("PLAYER " + _nombre + "(" + _id +
	 * ") USE SPEEDHACK => MAYBE:" + debeSer + " - WAS:" + fue + " - PING:" + ping +
	 * " (" + AJ.getCeldas() + ")"); //
	 * GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(this, "1DONT_USE_SPEEDHACK");
	 * // GestorSalida.ENVIAR_M0_MENSAJE_BASICOS_SVR(this, "45", "DISCONNECT FOR USE
	 * // SPEED HACK", // ""); // try { // Thread.sleep(3000); //
	 * _cuenta.getEntradaPersonaje().cerrarSocket(true, "finAccionMoverse()"); // }
	 * catch (Exception e) {} // return false; try { Thread.sleep(debeSer - fue +
	 * 1); } catch (Exception e) { } } } bug = 2; if (_pelea == null) { return
	 * finMovimiento(AJ, packet); } else { return _pelea.finalizarMovimiento(this);
	 * } } catch (Exception e) { String error =
	 * "EXCEPTION finAccionMoverse AJ.getPacket(): " + AJ.getPacket() + " , bug: " +
	 * bug + " e:" + e.toString(); GestorSalida.ENVIAR_BN_NADA(this, error);
	 * MainServidor.redactarLogServidorln(error); } return false; }
	 */
	/*
	 * private boolean finMovimiento(ServidorSocket.AccionDeJuego AJ, String packet)
	 * { byte bug = 0; try { short celdaAMover = -1, celdaPacket = -1; String
	 * pathReal = AJ.getPathReal(); String pathPacket = AJ.getPathPacket();
	 * this._orientacion =
	 * Encriptador.getNumeroPorValorHash(pathReal.charAt(pathReal.length() - 3));
	 * bug = 1; boolean ok = (packet.charAt(2) == 'K'); if (ok) { bug = 2;
	 * celdaAMover = Encriptador.hashACeldaID(pathReal.substring(pathReal.length() -
	 * 2)); celdaPacket =
	 * Encriptador.hashACeldaID(pathPacket.substring(pathPacket.length() - 2)); }
	 * else { bug = 3; String[] infos =
	 * packet.substring(3).split(Pattern.quote("|")); celdaPacket = celdaAMover =
	 * Short.parseShort(infos[1]); } bug = 4; return
	 * this._mapa.jugadorLLegaACelda(this, celdaAMover, celdaPacket, ok); } catch
	 * (Exception e) { String error = "EXCEPTION finMovimiento AJ.getPacket(): " +
	 * AJ.getPacket() + " , bug: " + bug + " e:" + e.toString();
	 * GestorSalida.ENVIAR_BN_NADA(this, error);
	 * MainServidor.redactarLogServidorln(error); return false; } }
	 */

	public boolean realizarOtroInteractivo(Celda celdaObjetivo, ObjetoInteractivo objInteractivo) {
		boolean b = false;
		if (objInteractivo == null || !objInteractivo.puedeIniciarRecolecta()) {
			return b;
		}
		for (final OtroInteractivo oi : Mundo.OTROS_INTERACTIVOS) {
			if (oi.getGfxID() <= -1 && oi.getMapaID() <= -1 && oi.getCeldaID() <= -1) {
				continue;
			}
			if (oi.getGfxID() > -1 && oi.getGfxID() != objInteractivo.getGfxID()) {
				continue;
			}
			if (oi.getMapaID() > -1 && oi.getMapaID() != _mapa.getID()) {
				continue;
			}
			if (oi.getCeldaID() > -1 && oi.getCeldaID() != celdaObjetivo.getID()) {
				continue;
			}
			if (!Condiciones.validaCondiciones(this, oi.getCondicion(), null)) {
				continue;
			}
			objInteractivo.forzarActivarRecarga(oi.getTiempoRecarga());
			oi.getAccion().realizarAccion(this, null, -1, (short) -1);
			return true;
		}
		return b;
	}

	public boolean puedeIrKoliseo() {
		if (_tutorial != null || _calabozo || _pelea != null || !_enLinea) {
			return false;
		}
		return true;
	}

	public int getPretendiente() {
		return _pretendiente;
	}

	public boolean puedeCasarse() {
		if (_mapa.getID() != 2019) {
			return false;
		}
		if (_celda.getID() != 297 && _celda.getID() != 282) {
			return false;
		}
		if (!MainServidor.PARAM_MATRIMONIO_GAY) {
			if (_celda.getID() == 282 && _sexo == Constantes.SEXO_FEMENINO) {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "1102");
				return false;
			}
			if (_celda.getID() == 297 && _sexo == Constantes.SEXO_MASCULINO) {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "1102");
				return false;
			}
		}
		if (_esposoID > 0) {
			return false;
		}
		return true;
	}

	public void preguntaCasarse() {
		if (_mapa.getID() != 2019) {
			return;
		}
		short celda = 0;
		byte sexo = 0;
		if (_celda.getID() == 282) {
			celda = 297;
			sexo = Constantes.SEXO_FEMENINO;
		} else if (_celda.getID() == 297) {
			celda = 282;
			sexo = Constantes.SEXO_MASCULINO;
		} else {
			return;
		}
		Personaje novio = _mapa.getCelda(celda).getPrimerPersonaje();
		if (novio == null || novio.getEsposoID() > 0) {
			return;
		}
		if (!MainServidor.PARAM_MATRIMONIO_GAY && novio.getSexo() != sexo) {
			return;
		}
		GestorSalida.ENVIAR_GA_ACCIONES_MATRIMONIO(_mapa, 617, _id, novio.getID(), -51);
	}

	public void confirmarMatrimonio(int proponeID, boolean acepto) {
		Personaje propone = Mundo.getPersonaje(proponeID);
		if (propone == null)
			return;
		if (!acepto) {
			_pretendiente = propone._pretendiente = 0;
			GestorSalida.ENVIAR_GA_ACCIONES_MATRIMONIO(_mapa, 619, _id, proponeID, -51);
		} else {
			if (propone.getPretendiente() == _id) {
				_pretendiente = propone._pretendiente = 0;
				_esposoID = propone.getID();
				propone._esposoID = _id;
				GestorSalida.ENVIAR_GA_ACCIONES_MATRIMONIO(_mapa, 618, _id, proponeID, -51);
			} else {
				_pretendiente = proponeID;
				GestorSalida.ENVIAR_GA_ACCIONES_MATRIMONIO(_mapa, 617, _id, proponeID, -51);
			}
		}
	}

	public void divorciar() {
		try {
			if (_enLinea) {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "047;" + Mundo.getPersonaje(_esposoID).getNombre());
			}
		} catch (final Exception e) {
		}
		_esposoID = 0;
	}

	public boolean is_primerentro() {
		return _primerentro;
	}

	public void set_primerentro(boolean primerentro) {
		_primerentro = primerentro;
	}

	public int getEsposoID() {
		return _esposoID;
	}

	public void setEsposoID(final int id) {
		_esposoID = id;
	}

	public byte getTipoExchange() {
		return _tipoExchange;
	}

	public void setTipoExchange(final byte tipo) {
		_tipoExchange = tipo;
	}

	public boolean estaExchange() {
		return _calabozo ? true : _tipoExchange != Constantes.INTERCAMBIO_TIPO_NULO;
	}

	public void cerrarVentanaExchange(String exito) {
		setExchanger(null);
		setTipoExchange(Constantes.INTERCAMBIO_TIPO_NULO);
		GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(this, exito);
	}

	public synchronized void cerrarExchange(String exito) {
		switch (getTipoInvitacion()) {
			case "intercambio":
				Personaje invitandoA, invitador;
				if (_invitador != null) {
					invitador = _invitador;
					invitandoA = this;
				} else if (_invitandoA != null) {
					invitador = this;
					invitandoA = _invitandoA;
				} else {
					GestorSalida.ENVIAR_BN_NADA(this);
					return;
				}
				invitador.setInvitandoA(null, "");
				invitandoA.setInvitador(null, "");
				invitador.cerrarVentanaExchange("");
				invitandoA.cerrarVentanaExchange("");
				break;
			default:
				if (!estaExchange()) {
					GestorSalida.ENVIAR_BN_NADA(this);
					return;
				}
				switch (_tipoExchange) {
					case Constantes.INTERCAMBIO_TIPO_LIBRO_ARTESANOS:// libro de artesanos
						cerrarVentanaExchange("");
						break;
					case Constantes.INTERCAMBIO_TIPO_TIENDA_NPC:// tienda npc
					case Constantes.INTERCAMBIO_TIPO_MERCANTE:// mercante
					case Constantes.INTERCAMBIO_TIPO_MI_TIENDA:// misma tienda
					case Constantes.INTERCAMBIO_TIPO_MONTURA:// dragopavo
					case Constantes.INTERCAMBIO_TIPO_BOUTIQUE: // boutique
					case Constantes.INTERCAMBIO_TIPO_PERSONAJE:// intercambio
					case Constantes.INTERCAMBIO_TIPO_TALLER: // accion oficio
					case Constantes.INTERCAMBIO_TIPO_COFRE:// cofre o banco
					case Constantes.INTERCAMBIO_TIPO_RECAUDADOR: // recaudador
					case Constantes.INTERCAMBIO_TIPO_MERCADILLO_COMPRAR:
					case Constantes.INTERCAMBIO_TIPO_MERCADILLO_VENDER: // mercadillo
					case Constantes.INTERCAMBIO_TIPO_TALLER_CLIENTE:
					case Constantes.INTERCAMBIO_TIPO_TALLER_ARTESANO:// invitar taller
					case Constantes.INTERCAMBIO_TIPO_CERCADO:// cercado
					case Constantes.INTERCAMBIO_TIPO_TRUEQUE:
					case Constantes.INTERCAMBIO_TIPO_TRUEQUE_MONTURA:
					case Constantes.INTERCAMBIO_TIPO_RESUCITAR_MASCOTA:// mascota
					case 9:// no se q es
						_exchanger.cerrar(this, exito);
						break;
				}
		}
	}

	public boolean esAbonado() {
		return (_cuenta != null && _cuenta.esAbonado());
	}

	private int mapaanterior = 7411;
	private short celdaanterior = 450;
	boolean cerrojocasa = false;

	public void teleportPelea(int nuevoMapaID, short nuevaCeldaID) {
		if (_tutorial != null)
			return;
		Mapa nuevoMapa = Mundo.getMapa(nuevoMapaID);
		if (nuevoMapa == null)
			return;
		if (nuevoMapa.getCelda(nuevaCeldaID) == null) {
			nuevaCeldaID = nuevoMapa.getRandomCeldaIDLibre();
			if (nuevaCeldaID <= 0)
				return;
		}
		if (getConversandoCon() != 0) {
			GestorSalida.ENVIAR_DV_FINALIZAR_DIALOGO(this);
			setConversandoCon(0);
		}
		if (_pregunta > 0) {
			dialogoFin();
		}
		if (nuevoMapa.getID() == 8757 || nuevoMapa.getID() == 8756 || nuevoMapa.getID() == 2187) {
			if (MainServidor.tallerAbierto) {
				if (nuevoMapa.getArrayPersonajes().size() > MainServidor.LIMITE_ARTESANOS_TALLER) {
					GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(this,
							"No puedes ingresar al taller porque esta lleno, porfavor regresa más tarde o intenta en otro taller.");
					nuevoMapaID = mapaanterior;
					nuevaCeldaID = celdaanterior;
					nuevoMapa = Mundo.getMapa(nuevoMapaID);
				}
			} else {
				GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(this,
						"No puedes ingresar al taller porque está cerrado, los talleres sólo abren los fines de semana de manera aleatoria.");
				nuevoMapaID = mapaanterior;
				nuevaCeldaID = celdaanterior;
				nuevoMapa = Mundo.getMapa(nuevoMapaID);
			}
		}
		_casaDentro = Mundo.getCasaDentroPorMapa(nuevoMapaID);
		// GestorSalida.ENVIAR_GA2_CARGANDO_MAPA(out, _ID);
		instante = false;
		GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(_mapa, _id);
		_mapa.removerPersonaje(this);
		_mapa = nuevoMapa;
		setMapaGDM(getMapa());
		_celda = _mapa.getCelda(nuevaCeldaID);
		_mapa.addPersonaje(this, true);
		mapaanterior = nuevoMapaID;
		celdaanterior = nuevaCeldaID;
		setCargandoMapa(true);
		String packet = "GDM|" + getMapa().getID() + "|" + getMapa().getFecha() + "|" + getMapa().getKey();
		if (estaExchange()) {
			GestorSalida.ENVIAR_Im_INFORMACION(this, "1NO_PUEDES_TELEPORT_POR_EXCHANGE");
			return;
		}
		setOcupado(false);
		// GestorSalida.ENVIAR_GDM_MAPDATA_COMPLETO(this);
		if (_grupo != null && _grupo.getRastrear() != null && _grupo.getRastrear().getID() == _id) {
			for (final Personaje elQueSigue : _grupo.getMiembros()) {
				try {
					if (elQueSigue.getID() == _grupo.getRastrear().getID()) {
						continue;
					}
					if (elQueSigue._enLinea) {
						GestorSalida.ENVIAR_IC_PERSONAJE_BANDERA_COMPAS(elQueSigue, _mapa.getX() + "|" + _mapa.getY());
					}
				} catch (Exception e) {
				}
			}
		}
		GestorSalida.enviar(this, packet + (char) 0x00 + "gm" + this.getMapa().celdasTPs, true);
		int[] tt = { MisionObjetivoModelo.DESCUBRIR_MAPA, MisionObjetivoModelo.DESCUBRIR_ZONA };
		verificarMisionesTipo(tt, null, false, 0);
		switch (nuevoMapaID) {
			case 7411:
				Interactivos.mensajeNPC(-57, "Elvio Miss", "Hola aventurero, intercambia tus gemas conmigo.", this,
						nuevoMapaID);
				break;
			case 29003:
				Interactivos.mensajeNPC(-71, "Elvio Miss", "Hola aventurero, intercambia tus gemas conmigo.", this,
						nuevoMapaID);
				break;
			case 6825:
				if (nuevaCeldaID == 339) {
					if (!accionesPJ.contains(3)) {
						Interactivos.iniciaTutorial(this);
						accionesPJ.add(3);
					}
				}
				break;
		}
	}

	public void teleport(int nuevoMapaID, short nuevaCeldaID) {
		if (_tutorial != null)
			return;
		if (getPelea() != null)
			return;
		if (this.tieneObjModeloEquipado(101610) || this.tieneObjModeloEquipado(101721)) {
			Objeto Ayudante = this.getObjPosicion(MainServidor.OBJETOS_POS_AYUDANTE);
			this.borrarOEliminarConOR(Ayudante.getID(), true);
		}
		Mapa nuevoMapa = Mundo.getMapa(nuevoMapaID);
		if (nuevoMapa == null)
			return;
		if (nuevoMapa.getCelda(nuevaCeldaID) == null) {
			nuevaCeldaID = nuevoMapa.getRandomCeldaIDLibre();
			if (nuevaCeldaID <= 0)
				return;
		}
		if (getConversandoCon() != 0) {
			GestorSalida.ENVIAR_DV_FINALIZAR_DIALOGO(this);
			setConversandoCon(0);
		}
		if (_pregunta > 0) {
			dialogoFin();
		}
		if (nuevoMapa.getID() == 8757 || nuevoMapa.getID() == 8756 || nuevoMapa.getID() == 2187) {
			if (MainServidor.tallerAbierto) {
				if (nuevoMapa.getArrayPersonajes().size() > MainServidor.LIMITE_ARTESANOS_TALLER) {
					GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(this,
							"No puedes ingresar al taller porque esta lleno, porfavor regresa más tarde o intenta en otro taller.");
					nuevoMapaID = mapaanterior;
					nuevaCeldaID = celdaanterior;
					nuevoMapa = Mundo.getMapa(nuevoMapaID);
				}
			} else {
				GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(this,
						"No puedes ingresar al taller porque está cerrado, los talleres sólo abren los fines de semana de manera aleatoria.");
				nuevoMapaID = mapaanterior;
				nuevaCeldaID = celdaanterior;
				nuevoMapa = Mundo.getMapa(nuevoMapaID);
			}
		}
		_casaDentro = Mundo.getCasaDentroPorMapa(nuevoMapaID);
		instante = false;
		GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(_mapa, _id);
		_mapa.removerPersonaje(this);
		_mapa = nuevoMapa;
		setMapaGDM(getMapa());
		_celda = _mapa.getCelda(nuevaCeldaID);
		_mapa.addPersonaje(this, true);
		mapaanterior = nuevoMapaID;
		celdaanterior = nuevaCeldaID;
		setCargandoMapa(true);
		String packet = "GDM|" + getMapa().getID() + "|" + getMapa().getFecha() + "|" + getMapa().getKey();
		if (estaExchange()) {
			GestorSalida.ENVIAR_Im_INFORMACION(this, "1NO_PUEDES_TELEPORT_POR_EXCHANGE");
			return;
		}
		setOcupado(false);
		// GestorSalida.ENVIAR_GDM_MAPDATA_COMPLETO(this);
		if (_grupo != null && _grupo.getRastrear() != null && _grupo.getRastrear().getID() == _id) {
			for (final Personaje elQueSigue : _grupo.getMiembros()) {
				try {
					if (elQueSigue.getID() == _grupo.getRastrear().getID()) {
						continue;
					}
					if (elQueSigue._enLinea) {
						GestorSalida.ENVIAR_IC_PERSONAJE_BANDERA_COMPAS(elQueSigue, _mapa.getX() + "|" + _mapa.getY());
					}
				} catch (Exception e) {
				}
			}
		}
		GestorSalida.enviar(this, packet + (char) 0x00 + "gm" + this.getMapa().celdasTPs, true);
		int[] tt = { MisionObjetivoModelo.DESCUBRIR_MAPA, MisionObjetivoModelo.DESCUBRIR_ZONA };
		verificarMisionesTipo(tt, null, false, 0);
		switch (nuevoMapaID) {
			case 7411:
				Interactivos.mensajeNPC(-57, "Elvio Miss", "Hola aventurero, intercambia tus gemas conmigo.", this,
						nuevoMapaID);
				break;
			case 29003:
				Interactivos.mensajeNPC(-71, "Elvio Miss", "Hola aventurero, intercambia tus gemas conmigo.", this,
						nuevoMapaID);
				break;
			case 6825:
				if (nuevaCeldaID == 339) {
					if (!accionesPJ.contains(3)) {
						Interactivos.iniciaTutorial(this);
						accionesPJ.add(3);
					}
				}
				break;
		}
	}

	/*
	 * public void teleport(final int nuevoMapaID, short nuevaCeldaID) { if
	 * (esMultiman()) { return; } try { if (_tutorial != null || _inmovil ||
	 * _calabozo) { if (_calabozo) { GestorSalida.ENVIAR_Im_INFORMACION(this,
	 * "1YOU_ARE_IN_JAIL"); } if (_tutorial != null) {
	 * GestorSalida.ENVIAR_Im_INFORMACION(this, "1YOU_ARE_DOING_TUTORIAL"); } if
	 * (_inmovil) { GestorSalida.ENVIAR_Im_INFORMACION(this, "1DONT_MOVE_TEMP"); }
	 * return; } if (estaExchange()) { GestorSalida.ENVIAR_Im_INFORMACION(this,
	 * "1NO_PUEDES_TELEPORT_POR_EXCHANGE"); return; } if (!_huir) { if
	 * (System.currentTimeMillis() - _tiempoAgresion > 8000) { _huir = true; } else
	 * { GestorSalida.ENVIAR_Im_INFORMACION(this, "1NO_PUEDES_HUIR;" +
	 * (System.currentTimeMillis() - _tiempoAgresion) / 1000); return; } } Mapa
	 * nuevoMapa = Mundo.getMapa(nuevoMapaID); if (nuevoMapa == null) { nuevoMapa =
	 * Mundo.getMapa((short) 7411); } if (nuevoMapa.getCelda(nuevaCeldaID) == null)
	 * { nuevaCeldaID = nuevoMapa.getRandomCeldaIDLibre(); } if
	 * (nuevoMapa.mapaAbonado() && !esAbonado()) {
	 * GestorSalida.ENVIAR_Im_INFORMACION(this, "131"); return; } _casaDentro =
	 * Mundo.getCasaDentroPorMapa(nuevoMapaID);;
	 * GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(_mapa, _id); _mapa = nuevoMapa;
	 * setCelda(_mapa.getCelda(nuevaCeldaID)); if (_pregunta > 0) { dialogoFin(); }
	 * String packet = "GDM|" + getMapa().getID() + "|" + getMapa().getFecha() +
	 * "|"+getMapa().getKey(); GestorSalida.enviar(this, packet+(char)
	 * 0x00+"gm"+this.getMapa().celdasTPs, true); rastrearGrupo(); int[] tt = {
	 * MisionObjetivoModelo.DESCUBRIR_MAPA, MisionObjetivoModelo.DESCUBRIR_ZONA };
	 * verificarMisionesTipo(tt, null, false, 0); } catch (final Exception e) {
	 * e.printStackTrace(); } }
	 */

	public boolean teleportSinTodos(final int nuevoMapaID, final short nuevaCeldaID) {
		Mapa nuevoMapa = Mundo.getMapa(nuevoMapaID);
		if (nuevoMapa == null || esMultiman()) {
			return false;
		}
		// if (_mapa.getID() == nuevoMapaID) {
		// return false;
		// }
		GestorSalida.ENVIAR_GDM_CAMBIO_DE_MAPA(this, nuevoMapa);
		GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(_mapa, _id);
		return true;
	}

	public String getAcciones() {
		StringBuilder strx = new StringBuilder();
		boolean prim = false;
		for (Integer str : accionesPJ) {
			if (prim)
				strx.append(";");
			strx.append(str);
			prim = true;
		}
		return strx.toString();
	}

	public void setHuir(final boolean huir) {
		_huir = huir;
	}

	public boolean getHuir() {
		return _huir;
	}

	public long getTiempoAgre() {
		return _tiempoAgresion;
	}

	public void setTiempoAgre(final long tiempo) {
		_tiempoAgresion = tiempo;
	}

	public void setAgresion(final boolean agre) {
		_agresion = agre;
	}

	public boolean getAgresion() {
		return _agresion;
	}

	public int getCostoAbrirBanco() {
		if (MainServidor.MODO_ALL_OGRINAS) {
			return 0;
		}
		return _cuenta.getObjetosBanco().size();
	}

	public String getStringVar(final String str) {
		if (str.equalsIgnoreCase("nombre")) {
			return getNombre();
		}
		if (str.equalsIgnoreCase("costoBanco")) {
			return getCostoAbrirBanco() + "";
		}
		return "";
	}

	public void addKamasBanco(final long i) {
		_cuenta.addKamasBanco(i);
	}

	public void addPuntosStats(final int pts) {
		_puntosStats += pts;
	}

	public void addPuntosHechizos(final int puntos) {
		_puntosHechizos += puntos;
	}

	public void abrirCercado() {
		if (_deshonor >= 5) {
			GestorSalida.ENVIAR_Im_INFORMACION(this, "183");
			return;
		}
		Cercado cercado = _mapa.getCercado();
		if (cercado == null) {
			return;
		}
		_exchanger = cercado;
		_tipoExchange = Constantes.INTERCAMBIO_TIPO_CERCADO;
		GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(this, _tipoExchange, analizarListaMonturas(cercado));
	}

	private String analizarListaMonturas(Cercado cercado) {
		final StringBuilder str = new StringBuilder();
		boolean primero = false;
		for (final Montura montura : _cuenta.getEstablo().values()) {
			if (primero) {
				str.append(";");
			}
			str.append(montura.detallesMontura());
			primero = true;
		}
		str.append("~");
		primero = false;
		for (final Montura montura : cercado.getCriando().values()) {
			if (montura.getDueñoID() == _id) {
				if (primero) {
					str.append(";");
				}
				str.append(montura.detallesMontura());
			} else {
				if (cercado.esPublico() || _miembroGremio == null
						|| !_miembroGremio.puede(Constantes.G_OTRAS_MONTURAS)) {
					continue;
				}
				if (primero) {
					str.append(";");
				}
				str.append(montura.detallesMontura());
			}
			primero = true;
		}
		return str.toString();
	}

	public Objeto tienellave() {
		for (final Objeto obj : _objetos.values()) {
			if (obj.getObjModeloID() != 10207) {
				continue;
			}
			return obj;
		}
		return null;
	}

	public void refrescarStuff(boolean actualizar, boolean enviarAs, boolean visual) {
		if (actualizar) {
			actualizarObjEquipStats();
		}
		Encarnacion encarnacionTemp = _encarnacion;
		float velocidadTemp = Math.max(0,
				_totalStats.getStatsObjetos().getStatParaMostrar(Constantes.STAT_MAS_VELOCIDAD) / 1000f);
		int aparienciaTemp = _totalStats.getStatsObjetos().getStatParaMostrar(Constantes.STAT_CAMBIA_APARIENCIA_2);
		int tituloTemp = getTitulo(false);
		Encarnacion encarnacion = null;
		do {
			actualizar = false;
			for (byte i : Constantes.POSICIONES_EQUIPAMIENTO) {
				final Objeto objeto = getObjPosicion(i);
				if (objeto == null) {
					continue;
				}
				if (objeto.getEncarnacion() != null) {
					encarnacion = objeto.getEncarnacion();
				}
				ObjetoModelo objMoverMod = objeto.getObjModelo();
				boolean desequipar = false;
				if (objMoverMod.getNivel() > _nivel) {
					desequipar = true;
				} else if (!Condiciones.validaCondiciones(this, objMoverMod.getCondiciones(), null)) {
					desequipar = true;
				} else if (!puedeEquiparRepetido(objMoverMod, 2)) {
					desequipar = true;
				}
				if (desequipar) {
					// si el item no debe ser equipado, se le pone posicio no equipado
					actualizar = true;
					enviarAs |= true;
					if (Constantes.esPosicionVisual(i)) {
						visual |= true;
					}
					if (MainServidor.PARAM_APRENDER_HECHIZO && objeto.tieneStatTexto(Constantes.STAT_APRENDE_HECHIZO)) {
						int hechizo = Integer
								.parseInt(objeto.getStats().getParamStatTexto(Constantes.STAT_APRENDE_HECHIZO, 3), 16);
						olvidarHechizo(hechizo);
					}
					objeto.setPosicion(Constantes.OBJETO_POS_NO_EQUIPADO, this, false);
				}
			}
		} while (actualizar);
		float velocidad = Math.max(0,
				_totalStats.getStatsObjetos().getStatParaMostrar(Constantes.STAT_MAS_VELOCIDAD) / 1000f);
		int apariencia = _totalStats.getStatsObjetos().getStatParaMostrar(Constantes.STAT_CAMBIA_APARIENCIA_2);
		int titulo = getTitulo(false);
		int[][] rB = { { RB_PUEDE_SWITCH_MODO_CRIATURA, apariencia != 0 ? 0 : 1 } };
		setRestriccionesB(rB);
		boolean cambioEncarnacion = false;
		if (encarnacion != encarnacionTemp) {
			_encarnacion = encarnacion;
			if (_encarnacion != null) {
				_tiempoUltEncarnacion = System.currentTimeMillis();
			}
			if (_enLinea) {
				GestorSalida.ENVIAR_AC_CAMBIAR_CLASE(this, getClaseID(false));
				GestorSalida.ENVIAR_SL_LISTA_HECHIZOS(this);
				cambioEncarnacion = true;
			}
		}
		if (_encarnacion == null) {
			if (_enLinea) {
				refrescarParteSetClase(true);
			}
		}
		actualizarPDV(0);
		if (enviarAs && _enLinea) {
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
			GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(this);
		}
		if (cambioEncarnacion || velocidadTemp != velocidad || aparienciaTemp != apariencia || tituloTemp != titulo) {
			if (_enLinea) {
				refrescarEnMapa();
			}
		}
		if (visual) {
			cambiarRopaVisual();
		}
	}

	public boolean puedeEquiparRepetido(ObjetoModelo objMoverMod, int cant) {
		if ((objMoverMod.getSetID() > 0 || objMoverMod.getTipo() == Constantes.OBJETO_TIPO_DOFUS
				|| objMoverMod.getTipo() == Constantes.OBJETO_TIPO_OMEGA
				|| objMoverMod.getTipo() == Constantes.OBJETO_TIPO_IDOLOS
				|| objMoverMod.getTipo() == Constantes.OBJETO_TIPO_TROFEO)
				&& Mundo.getCreaTuItem(objMoverMod.getID()) == null
				&& cantEquipadoModelo(objMoverMod.getID()) >= cant) {
			return false;
		}
		return true;
	}

	public void refrescarEnMapa() {
		if (_pelea == null) {
			GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(_mapa, this);
		} else if (_pelea.getFase() == Constantes.PELEA_FASE_POSICION) {
			final Luchador luchador = _pelea.getLuchadorPorID(_id);
			if (luchador != null) {
				GestorSalida.ENVIAR_GM_REFRESCAR_LUCHADOR_EN_PELEA(_pelea, luchador);
			}
		}
	}

	public void refrescarEnMapaSinHumo() {
		if (_pelea == null) {
			GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA_SIN_HUMO(_mapa, this);
		} else if (_pelea.getFase() == Constantes.PELEA_FASE_POSICION) {
			final Luchador luchador = _pelea.getLuchadorPorID(_id);
			if (luchador != null) {
				GestorSalida.ENVIAR_GM_REFRESCAR_LUCHADOR_EN_PELEA(_pelea, luchador);
			}
		}
	}

	public void cambiarRopaVisual() {
		if (_pelea != null) {
			GestorSalida.ENVIAR_Oa_CAMBIAR_ROPA_PELEA(_pelea, this);
		} else {
			GestorSalida.ENVIAR_Oa_CAMBIAR_ROPA_MAPA(_mapa, this);
		}
		if (_grupo != null) {
			GestorSalida.ENVIAR_PM_ACTUALIZAR_INFO_PJ_GRUPO(_grupo, stringInfoGrupo());
		}
	}

	public String analizarListaAmigos(final int id) {
		final StringBuilder str = new StringBuilder(";");
		if(this.getPelea() != null) {
			str.append("2;");
		}else {
			str.append("?;");
		}
		str.append(getNombre() + ";");
		if (_cuenta.esAmigo(id)) {
			str.append(_nivel + ";");
			str.append(_alineacion + ";");
		} else {
			str.append("?;");
			str.append("-1;");
		}
		str.append(_claseID + ";");
		str.append(_sexo + ";");
		str.append(getGfxID(false));
		return str.toString();
	}

	public String analizarListaEnemigos(final int id) {
		final StringBuilder str = new StringBuilder(";");
		str.append("?;");
		str.append(getNombre() + ";");
		if (_cuenta.esEnemigo(id)) {
			str.append(_nivel + ";");
			str.append(_alineacion + ";");
		} else {
			str.append("?;");
			str.append("-1;");
		}
		str.append(_claseID + ";");
		str.append(_sexo + ";");
		str.append(getGfxID(false));
		return str.toString();
	}

	public boolean estaMontando() {
		return _montando;
	}

	public synchronized void subirBajarMontura(boolean obligatorio) {
		if (_montura == null) {
			if (_enLinea) {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "1MOUNT_NO_EQUIP");
			}
			return;
		}
		if (!obligatorio) {
			if (_encarnacion != null) {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "134|44");
				return;
			}
			if (_pelea != null && (_pelea.getFase() != Constantes.PELEA_FASE_POSICION || _pelea.esEspectador(_id)
					|| _cuenta.getSocket().getEquiparSet())) {
				return;
			}
			if (!_montando) {// va a montar
				if (_nivel < 60 || esFantasma() || esTumba()) {
					GestorSalida.ENVIAR_Im_INFORMACION(this, "1MOUNT_ERROR_RIDE");
					return;
				}
				if (MainServidor.PARAM_CRIAR_MONTURA) {
					if (_montura.getEnergia() < 10) {
						GestorSalida.enviar(this, "wc", true);
						return;
					}
					if (!_montura.esMontable()) {
						GestorSalida.ENVIAR_Im_INFORMACION(this, "1176");
						return;
					}
				}
				if (_casaDentro != null) {
					GestorSalida.ENVIAR_Im_INFORMACION(this, "1117");
					return;
				}
				if (_montura.getDueñoID() != _id) {
					GestorSalida.ENVIAR_BN_NADA(this, "SUBIR BAJAR MONTURA NO DUEÑO " + _montura.getDueñoID());
					return;
				}
			}
			_montura.energiaPerdida(15);
		}
		_montando = !_montando;
		final Objeto mascota = getObjPosicion(Constantes.OBJETO_POS_MASCOTA);
		if (_montando && mascota != null) {
			mascota.setPosicion(Constantes.OBJETO_POS_NO_EQUIPADO, this, false);
		}
		refrescarStuff(true, !obligatorio, false);
		if (!obligatorio) {
			if (_enLinea) {
				refrescarEnMapa();
				GestorSalida.ENVIAR_Re_DETALLES_MONTURA(this, "+", _montura);
				GestorSalida.ENVIAR_Rr_ESTADO_MONTADO(this, _montando ? "+" : "-");
			}
		}
	}

	public Montura getMontura() {
		return _montura;
	}

	public void setMontura(final Montura montura) {
		_montura = montura;
		if (_montura != null) {
			_montura.setUbicacion(Ubicacion.EQUIPADA);
		}
		if (_enLinea) {
			GestorSalida.ENVIAR_Re_DETALLES_MONTURA(this, _montura != null ? "+" : "-", _montura);
		}
	}

	public int getPorcXPMontura() {
		return _porcXPMontura;
	}

	public void setPorcXPMontura(final int porcXP) {
		_porcXPMontura = porcXP;
		_porcXPMontura = Math.max(_porcXPMontura, 0);
		_porcXPMontura = Math.min(_porcXPMontura, 90);
	}

	public void resetearVariables() {
		_prePelea = null;// es para perco y prismas
		_tipoExchange = Constantes.INTERCAMBIO_TIPO_NULO;
		if (_invitandoA != null) {
			_invitandoA.setInvitador(null, "");
			_invitandoA = null;
		}
		if (_invitador != null) {
			_invitador.setInvitandoA(null, "");
			_invitador = null;
		}
		Marche = false;
		_tipoInvitacion = "";
		_conversandoCon = 0;
		_pretendiente = 0;
		_pregunta = 0;
		enTorneo = 0;
		_emoteActivado = 0;
		_exchanger = null;
		_tutorial = null;
		puedeAbrir = true;
		_consultarCasa = null;
		_consultarCofre = null;
		_isOnAction = false;
		esperaPelea = null;
		primerRefresh = false;
		_ocupado = false;
		totalEspera = 100;
		actualEspera = 0;
		dueñoMaitre = null;
		_sentado = false;
		setHaciendoTrabajo(null);
		usaTP = true;
		iniciaTorneo = null;
		_ausente = false;// para q no recibas MP de todos
		_invisible = false;// para q solo recibias MP de tus amigos
		_cargandoMapa = false;
		_olvidandoHechizo = false;
		_deDia = false;
		_deNoche = false;
		_isOnAction = false;
		_idsOmitidos.clear();
		if (esMaitre)
			esMaitre = false;
		if (liderMaitre)
			liderMaitre = false;
		if (_acciones != null) {
			_acciones.clear();
		}
		_bonusSetDeClase.clear();
		limpiarPacketsCola();
	}

	public void setPrePelea(Pelea pelea, int idUnirse) {
		_prePelea = pelea;
		_unirsePrePeleaAlID = idUnirse;
	}

	public Pelea getPrePelea() {
		return _prePelea;
	}

	public int getUnirsePrePeleaAlID() {
		return _unirsePrePeleaAlID;
	}

	public void addOmitido(String name) {
		Personaje p = Mundo.getPersonajePorNombre(name);
		if (p == null) {
			return;
		}
		if (!_idsOmitidos.contains(p.getID())) {
			_idsOmitidos.add(p.getID());
		}
	}

	public void borrarOmitido(String name) {
		Personaje p = Mundo.getPersonajePorNombre(name);
		if (p == null) {
			return;
		}
		if (_idsOmitidos.contains(p.getID())) {
			_idsOmitidos.remove((Object) p.getID());
		}
	}

	public String getCanales() {
		return _canales;
	}

	public boolean tieneCanal(String c) {
		switch (c) {
			case "p":// espectador
			case "F":// envia
			case "T":// recibe
			case "@":// admin
				// case "¡" :// vip
			case "¬":// unknown
			case "~":// all
				return true;
		}
		return _canales.contains(c);
	}

	public void addCanal(String canal) {
		if (_canales.indexOf(canal) >= 0)
			return;
		_canales += canal;
		GestorSalida.ENVIAR_cC_SUSCRIBIR_CANAL(this, '+', canal);
	}

	public void removerCanal(String canal) {
		_canales = _canales.replace(canal, "");
		GestorSalida.ENVIAR_cC_SUSCRIBIR_CANAL(this, '-', canal);
	}

	public boolean cambiarAlineacion(final byte alineacion, boolean siOsi) {
		if (!siOsi) {
			if (getDeshonor() >= 2) {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "183");
				return false;
			}
		}
		_honor = 0;
		_deshonor = 0;
		_alineacion = alineacion;
		_mostrarAlas = alineacion != Constantes.ALINEACION_NEUTRAL;
		if (_alineacion != Constantes.ALINEACION_NEUTRAL) {
			if (MainServidor.HONOR_FIJO_PARA_TODOS > -1) {
				_honor = MainServidor.HONOR_FIJO_PARA_TODOS;
			}
		}
		_ordenNivel = 0;
		switch (_alineacion) {
			case Constantes.ALINEACION_BONTARIANO:
				_orden = 1;
				break;
			case Constantes.ALINEACION_BRAKMARIANO:
				_orden = 5;
				break;
			case Constantes.ALINEACION_MERCENARIO:
				_orden = 9;
				break;
			default:
				_orden = 0;
				break;
		}
		refrescarGradoAlineacion();
		Especialidad esp = Mundo.getEspecialidad(_orden, _ordenNivel);
		if (esp != null) {
			GestorSalida.ENVIAR_ZC_CAMBIAR_ESPECIALIDAD_ALINEACION(this, esp.getID());
		}
		actualizarStatsEspecialidad(esp);
		refrescarStuff(true, true, false);
		refrescarEnMapa();
		return true;
	}

	public void addOrdenNivel(int nivel) {
		Especialidad esp = Mundo.getEspecialidad(_orden, _ordenNivel);
		_ordenNivel += nivel;
		if (_ordenNivel > 100) {
			_ordenNivel = 100;
		}
		if (esp != null && esp.getID() != (esp = Mundo.getEspecialidad(_orden, _ordenNivel)).getID()) {
			GestorSalida.ENVIAR_ZC_CAMBIAR_ESPECIALIDAD_ALINEACION(this, esp.getID());
		}
		actualizarStatsEspecialidad(esp);
		GestorSalida.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(this);
	}

	public void setOrden(int orden) {
		_orden = orden;
		Especialidad esp = Mundo.getEspecialidad(_orden, _ordenNivel);
		if (esp != null) {
			GestorSalida.ENVIAR_ZC_CAMBIAR_ESPECIALIDAD_ALINEACION(this, esp.getID());
		}
		actualizarStatsEspecialidad(esp);
		GestorSalida.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(this);
	}

	public void actualizarStatsEspecialidad(Especialidad esp) {
		_totalStats.getStatsBendMald().clear();
		if (esp == null) {
			return;
		}
		for (Don don : esp.getDones()) {
			_totalStats.getStatsBendMald().acumularStats(don.getStat());
		}
	}

	public int getOrden() {
		return _orden;
	}

	public int getOrdenNivel() {
		return _ordenNivel;
	}

	public int getEspecialidad() {
		Especialidad esp = Mundo.getEspecialidad(_orden, _ordenNivel);
		if (esp != null) {
			return esp.getID();
		}
		return 0;
	}

	public int getDeshonor() {
		return _deshonor;
	}

	public boolean addDeshonor(int deshonor) {
		if (_alineacion == Constantes.ALINEACION_NEUTRAL || !MainServidor.PARAM_PERMITIR_DESHONOR) {
			return false;
		}
		_deshonor += deshonor;
		if (_deshonor < 0) {
			_deshonor = 0;
		}
		return true;
	}

	public int getHonor() {
		return _honor;
	}

	public void addHonor(final int honor) {
		if (esMultiman()) {
			return;
		}
		if (honor == 0 || _alineacion == Constantes.ALINEACION_NEUTRAL) {
			return;
		}
		if (honor > 0) {
			GestorSalida.ENVIAR_Im_INFORMACION(this, "074;" + honor);
		} else if (honor < 0) {
			GestorSalida.ENVIAR_Im_INFORMACION(this, "076;" + -honor);
		}
		_honor += honor;
		refrescarGradoAlineacion();
	}

	public void refrescarGradoAlineacion() {
		final int nivelAntes = _gradoAlineacion;
		if (_honor < 0) {
			_honor = 0;
		} else if (_honor >= Mundo.getExpAlineacion(MainServidor.NIVEL_MAX_ALINEACION)) {
			_gradoAlineacion = MainServidor.NIVEL_MAX_ALINEACION;
			_honor = Mundo.getExpAlineacion(MainServidor.NIVEL_MAX_ALINEACION);
		}
		for (byte n = 1; n <= MainServidor.NIVEL_MAX_ALINEACION; n++) {
			if (_honor < Mundo.getExpAlineacion(n)) {
				_gradoAlineacion = (byte) (n - 1);
				break;
			}
		}
		if (nivelAntes == _gradoAlineacion) {
			return;
		}
		if (nivelAntes < _gradoAlineacion) {
			GestorSalida.ENVIAR_Im_INFORMACION(this, "082;" + _gradoAlineacion);
		} else if (nivelAntes > _gradoAlineacion) {
			GestorSalida.ENVIAR_Im_INFORMACION(this, "083;" + _gradoAlineacion);
		}
		refrescarStuff(true, true, false);
		Sucess.launch(this, (byte) 4, null, 0);
	}

	public int getGradoAlineacion() {
		if (_alineacion == Constantes.ALINEACION_NEUTRAL) {
			return 1;
		}
		return _gradoAlineacion;
	}

	public void botonActDesacAlas(final char c) {
		if (_alineacion == Constantes.ALINEACION_NEUTRAL) {
			_mostrarAlas = false;
			return;
		}
		if (!MainServidor.PARAM_PERMITIR_DESACTIVAR_ALAS) {
			_mostrarAlas = _alineacion != Constantes.ALINEACION_NEUTRAL;
			return;
		}
		final int honorPerd = _honor / 20;
		switch (c) {
			case '*':
				GestorSalida.ENVIAR_GIP_ACT_DES_ALAS_PERDER_HONOR(this, honorPerd);
				return;
			case '+':
				_mostrarAlas = true;
				GestorSalida.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(this);
				break;
			case '-':
				_mostrarAlas = false;
				addHonor(-honorPerd);
				GestorSalida.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(this);
				break;
		}
	}

	public MiembroGremio getMiembroGremio() {
		return _miembroGremio;
	}

	public int getCuentaID() {
		if (_cuenta == null) {
			return -1;
		}
		return _cuenta.getID();
	}

	public boolean cumplirMisionAlmanax() {
		if (!MainServidor.PARAM_ALMANAX) {
			this.sendMessage("No hay Almanax hoy");
			GestorSalida.ENVIAR_BN_NADA(this, "ALMANAX NO DISPONIBLE");
			return false;
		}
		// buscar ontoral Zo
		if (realizoMisionDelDia()) {
			this.sendMessage("Ya hiciste el Almanax de hoy");
			GestorSalida.ENVIAR_BN_NADA(this, "YA REALIZO ALMANAX DEL DIA");
			return false;
		}
		Almanax almanax = Mundo.getAlmanaxDelDia();
		if (almanax == null) {
			GestorSalida.ENVIAR_BN_NADA(this, "NO EXISTE ALMANAX DEL DIA");
			return false;
		}
		int id = almanax.getOfrenda()._primero;
		int cant = almanax.getOfrenda()._segundo;
		if (tenerYEliminarObjPorModYCant(id, cant)) {
			GestorSalida.ENVIAR_Im_INFORMACION(this, "022;" + cant + "~" + id);
			GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(this);
			if (almanax.getTipo() == Constantes.ALMANAX_BONUS_ITEM) {
				ObjetoModelo tempObjMod = Mundo.getObjetoModelo(almanax.getBonus());
				if (tempObjMod == null) {
					GestorSalida.ENVIAR_BN_NADA(this);
				} else {
					addObjIdentAInventario(tempObjMod.crearObjeto(MainServidor.ALMANAX_OBJETO_CANTIDAD,
							Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM), false);
					GestorSalida.ENVIAR_Im_INFORMACION(this,
							"021;" + MainServidor.ALMANAX_OBJETO_CANTIDAD + "~" + almanax.getBonus());
				}
			}
			this.PuntosPrestigio += MainServidor.ptsOfrenda;
			this.sendMessage("Has ganado <b>" + MainServidor.ptsOfrenda + "</b> Pts. por dar una ofrenda diaria");
			agregarMisionDelDia();
			GestorSalida.ENVIAR_ÑX_PANEL_ALMANAX(this.getServidorSocket(), "");
		} else {
			GestorSalida.ENVIAR_Im_INFORMACION(this, "14");
		}
		return true;
	}

	public String listaPrismas() {
		final StringBuilder str = new StringBuilder(_mapa.getID());
		final int subAreaID = _mapa.getSubArea().getArea().getSuperArea().getID();
		for (final Prisma prisma : Mundo.getPrismas()) {
			try {
				if (prisma.getAlineacion() != _alineacion) {
					continue;
				}
				if (prisma.getMapa().getSubArea().getArea().getSuperArea().getID() != subAreaID) {
					continue;
				}
				if (prisma.getEstadoPelea() == 0 || prisma.getEstadoPelea() == -2) {
					str.append("|" + prisma.getMapa().getID() + ";*");
				} else {
					int costo = Formulas.calcularCosteZaap(_mapa, prisma.getMapa());
					if (MainServidor.MODO_ALL_OGRINAS)
						costo = 0;
					str.append("|" + prisma.getMapa().getID() + ";" + costo);
				}
			} catch (Exception e) {
			}
		}
		return str.toString();
	}

	public String listaZaap() {
		final StringBuilder str = new StringBuilder();
		if (_zaaps.contains(_mapaSalvada)) {
			str.append(_mapaSalvada);
		}
		final int superAreaID = _mapa.getSubArea().getArea().getSuperArea().getID();
		for (final int i : _zaaps) {
			try {
				if (Mundo.getMapa(i).getSubArea().getArea().getSuperArea().getID() != superAreaID) {
					continue;
				}
				int costo = Formulas.calcularCosteZaap(_mapa, Mundo.getMapa(i));
				str.append("|" + i + ";" + costo);
			} catch (final Exception e) {
			}
		}
		return str.toString();
	}

	public boolean tieneZaap(final int mapaID) {
		return _zaaps.contains(mapaID);
	}

	public void abrirMenuZaap() {
		if (_deshonor >= 3) {
			GestorSalida.ENVIAR_Im_INFORMACION(this, "183");
			return;
		}
		if (!tieneZaap(_mapa.getID())) {
			_zaaps.add(_mapa.getID());
			_zaaps.trimToSize();
			GestorSalida.ENVIAR_Im_INFORMACION(this, "024");
		}
		GestorSalida.ENVIAR_WC_MENU_ZAAP(this);
	}

	public void abrirMenuZaapi() {
		if (_deshonor >= 3) {
			GestorSalida.ENVIAR_Im_INFORMACION(this, "183");
			return;
		}
		final StringBuilder listaZaapi = new StringBuilder();
		if (_mapa.getSubArea().getArea().getID() != 7 || _alineacion == Constantes.ALINEACION_BRAKMARIANO) {
			// nada
		} else {
			final String[] Zaapis = "6159,4174,8758,4299,4180,8759,4183,2221,4300,4217,4098,8757,4223,8760,2214,4179,4229,4232,8478,4238,4263,4216,4172,4247,4272,4271,4250,4178,4106,4181,4259,4090,4262,4287,4300,4240,4218,4074,4308"
					.replace(_mapa.getID() + ",", "").replace(_mapa.getID() + "", "").split(",");
			int precio = 20;
			if (_alineacion == Constantes.ALINEACION_BONTARIANO) {
				precio = 10;
			}
			for (final String s : Zaapis) {
				listaZaapi.append(s + ";" + precio + "|");
			}
		}
		if (_mapa.getSubArea().getArea().getID() != 11 || _alineacion == Constantes.ALINEACION_BONTARIANO) {
			// nada
		} else {
			final String[] Zaapis = "8756,8755,8493,5304,5311,5277,5317,4612,4618,5112,4639,4637,5116,5332,4579,4588,4549,4562,5334,5295,4646,4629,4601,4551,4607,4930,4622,4620,4615,4595,4627,4623,4604,8754,8753,4630"
					.replace(_mapa.getID() + ",", "").replace(_mapa.getID() + "", "").split(",");
			int precio = 20;
			if (_alineacion == Constantes.ALINEACION_BRAKMARIANO) {
				precio = 10;
			}
			if (MainServidor.MODO_ALL_OGRINAS)
				precio = 0;
			for (final String s : Zaapis) {
				listaZaapi.append(s + ";" + precio + "|");
			}

		}
		GestorSalida.ENVIAR_Wc_LISTA_ZAPPIS(this, _mapa.getID() + "|" + listaZaapi.toString());
	}

	public void abrirMenuPrisma() {
		if (_deshonor >= 3) {
			GestorSalida.ENVIAR_Im_INFORMACION(this, "183");
			return;
		}
		GestorSalida.ENVIAR_Wp_MENU_PRISMA(this);
	}

	public String stringZaapsParaBD() {
		final StringBuilder str = new StringBuilder();
		for (final int i : _zaaps) {
			if (str.length() > 0) {
				str.append(",");
			}
			str.append(i);
		}
		return str.toString();
	}

	public void usarZaap(final int mapaID) {
		try {
			final Mapa mapa = Mundo.getMapa(mapaID);
			if (mapa == null || mapaID == _mapa.getID() || !tieneZaap(_mapa.getID()) || !tieneZaap(mapaID)) {
				GestorSalida.ENVIAR_BN_NADA(this);
				return;
			}
			final short celdaID = (short) Mundo.getCeldaZaapPorMapaID(mapaID);
			if (mapa.getCelda(celdaID) == null || mapa.getSubArea().getArea().getSuperArea().getID() != _mapa
					.getSubArea().getArea().getSuperArea().getID()) {
				GestorSalida.ENVIAR_WUE_ZAPPI_ERROR(this);
				return;
			}
			if (_alineacion == Constantes.ALINEACION_BRAKMARIANO) {
				if (mapaID == 4263 || _mapa.getSubArea().getAlineacion() == Constantes.ALINEACION_BONTARIANO) {
					GestorSalida.ENVIAR_Im_INFORMACION(this, "1TEAM_DIFFERENT_ALIGNMENT");
					GestorSalida.ENVIAR_WUE_ZAPPI_ERROR(this);
					return;
				}
			}
			if (_alineacion == Constantes.ALINEACION_BONTARIANO) {
				if (mapaID == 5295 || _mapa.getSubArea().getAlineacion() == Constantes.ALINEACION_BRAKMARIANO) {
					GestorSalida.ENVIAR_Im_INFORMACION(this, "1TEAM_DIFFERENT_ALIGNMENT");
					GestorSalida.ENVIAR_WUE_ZAPPI_ERROR(this);
					return;
				}
			}
			final int costo = Formulas.calcularCosteZaap(_mapa, mapa);
			if (getKamas() < costo) {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "182");
				return;
			}
			addKamas(-costo, false, true);
			if (this.liderMaitre) {
				for (Personaje pjx : this.getGrupoParty().getPersos()) {
					if (pjx == this)
						continue;
					pjx.teleport(mapaID, celdaID);
				}
			}
			teleport(mapaID, celdaID);
			GestorSalida.ENVIAR_WV_CERRAR_ZAAP(this);
		} catch (final Exception e) {
		}
	}

	public void usarZaapi(final short mapaID) {
		try {
			if (_deshonor >= 2) {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "183");
				return;
			}
			final Mapa mapa = Mundo.getMapa(mapaID);
			if (mapa == null || mapaID == _mapa.getID() || !Mundo.esZaapi(_mapa.getID(), _alineacion)
					|| !Mundo.esZaapi(mapaID, _alineacion)) {
				GestorSalida.ENVIAR_BN_NADA(this);
				return;
			}
			short celdaID = 0;
			for (final Celda celda : mapa.getCeldas().values()) {
				try {
					if (celda.getObjetoInteractivo().getObjIntModelo().getID() != 106) {
						continue;
					}
					celdaID = (short) (celda.getID() + mapa.getAncho());
					break;
				} catch (final Exception e) {
				}
			}
			if (celdaID == 0) {
				return;
			}
			int costo = 20;
			if (_alineacion == Constantes.ALINEACION_BONTARIANO || _alineacion == Constantes.ALINEACION_BRAKMARIANO) {
				costo = 10;
			}
			if (!MainServidor.MODO_ALL_OGRINAS) {
				if (getKamas() < costo) {
					GestorSalida.ENVIAR_Im_INFORMACION(this, "182");
					return;
				}
				addKamas(-costo, false, true);
			}
			if (this.liderMaitre) {
				for (Personaje pjx : this.getGrupoParty().getPersos()) {
					if (pjx == this)
						continue;
					pjx.teleport(mapaID, celdaID);
				}
			}
			teleport(mapaID, celdaID);
			GestorSalida.ENVIAR_Wv_CERRAR_ZAPPI(this);
		} catch (final Exception e) {
		}
	}

	public void usarPrisma(final short mapaID) {
		try {
			if (_deshonor >= 1) {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "183");
				return;
			}
			final Mapa mapa = Mundo.getMapa(mapaID);
			if (mapa == null || mapaID == _mapa.getID() || _mapa.getPrisma() == null || mapa.getPrisma() == null
					|| _mapa.getPrisma().getAlineacion() != _alineacion
					|| mapa.getPrisma().getAlineacion() != _alineacion) {
				GestorSalida.ENVIAR_BN_NADA(this);
				return;
			}
			if (!alasActivadas()) {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "1144");
				return;
			}
			short celdaID = mapa.getPrisma().getCelda().getID();
			if (!MainServidor.MODO_ALL_OGRINAS) {
				int costo = Formulas.calcularCosteZaap(_mapa, Mundo.getMapa(mapaID));
				if (getKamas() < costo) {
					GestorSalida.ENVIAR_Im_INFORMACION(this, "182");
					return;
				}
				addKamas(-costo, false, true);
			}
			teleport(mapaID, celdaID);
			GestorSalida.ENVIAR_Ww_CERRAR_PRISMA(this);
		} catch (final Exception e) {
		}
	}

	public void usarZonas(final short mapaID) {
		try {
			if (Mundo.getMapa(mapaID) == null || Mundo.ZONAS.get(mapaID) == null) {
				return;
			}
			if (this.liderMaitre) {
				for (Personaje pjx : this.getGrupoParty().getPersos()) {
					if (pjx == this)
						continue;
					pjx.teleport(mapaID, Mundo.ZONAS.get(mapaID));
				}
			}
			teleport(mapaID, Mundo.ZONAS.get(mapaID));
		} catch (final Exception e) {
		}
	}

	public Objeto getObjModeloNoEquipado(final int idModelo, final int cantidad) {
		for (final Objeto obj : _objetos.values()) {
			if (Constantes.esPosicionEquipamiento(obj.getPosicion()) || obj.getObjModeloID() != idModelo) {
				continue;
			}
			if (obj.getCantidad() >= cantidad) {
				return obj;
			}
		}
		return null;
	}

	public Objeto getObjModeloNoEquipadoFortificado(final int idModelo, final int nivel) {
		for (final Objeto obj : _objetos.values()) {
			if (Constantes.esPosicionEquipamiento(obj.getPosicion()) || obj.getObjModeloID() != idModelo) {
				continue;
			}
			if (!obj.tieneStatTexto(Constantes.STAT_NIVEL)) {
				continue;
			}
			String statsTemp = obj.getParamStatTexto(Constantes.STAT_NIVEL, 3);
			int nivelObjeto = Integer.parseInt(statsTemp, 16);
			if (nivel == nivelObjeto)
				return obj;
		}
		return null;
	}

	public Objeto getObjModeloNoEquipadoFortificado2(final int idModelo, final int nivel) {
		for (final Objeto obj : _objetos.values()) {
			if (Constantes.esPosicionEquipamiento(obj.getPosicion()) || obj.getObjModeloID() != idModelo) {
				continue;
			}
			if (!obj.tieneStatTexto(Constantes.STAT_NIVEL)) {
				continue;
			}
			String statsTemp = obj.getParamStatTexto(Constantes.STAT_NIVEL, 3);
			int nivelObjeto = Integer.parseInt(statsTemp, 16);
			if (nivelObjeto >= nivel)
				return obj;
		}
		return null;
	}

	public void setOlvidandoHechizo(final boolean olvidandoHechizo) {
		_olvidandoHechizo = olvidandoHechizo;
	}

	public boolean estaOlvidandoHechizo() {
		return _olvidandoHechizo;
	}

	public boolean estaVisiblePara(final Personaje perso) {
		if (_ausente) {
			return false;
		}
		if (_idsOmitidos.contains(perso.getID())) {
			return false;
		}
		if (_cuenta.esEnemigo(perso.getCuentaID())) {
			return false;
		}
		if (_invisible) {
			return _cuenta.esAmigo(perso.getCuentaID());
		}
		return true;
	}

	public void enviarMsjAAmigos() {
		String str = getCuenta().getApodo() + " (<b><a href='asfunction:onHref,ShowPlayerPopupMenu," + getNombre()
				+ "'>" + getNombre() + "</a></b>)";
		for (Personaje online : Mundo.getPersonajesEnLinea()) {
			try {
				if (online._cuenta.esAmigo(_id)) {
					GestorSalida.ENVIAR_Im0143_AMIGO_CONECTADO(online, str);
				}
			} catch (Exception e) {
			}
		}
	}

	public boolean estaAusente() {
		return _ausente;
	}

	public void setAusente(final boolean ausente) {
		_ausente = ausente;
	}

	public boolean esIndetectable() {
		return _indetectable;
	}

	public void setIndetectable(final boolean indetectable) {
		_indetectable = indetectable;
	}

	public boolean esInvisible() {
		return _invisible;
	}

	public void setInvisible(final boolean invisible) {
		_invisible = invisible;
	}

	public boolean esFantasma() {
		return !getRestriccionB(RB_NO_ES_FANTASMA);
	}

	public boolean esTumba() {
		return !getRestriccionB(RB_NO_ES_TUMBA);
	}

	public int getTitulo(boolean real) {
		if (!real) {
			try {
				String titulo = _totalStats.getStatsObjetos().getParamStatTexto(Constantes.STAT_TITULO, 3);
				if (!titulo.isEmpty()) {
					return Integer.parseInt(titulo, 16);
				}
			} catch (Exception e) {
			}
		}
		if (MainServidor.PARAMS_MOSTRAR_TITULO_VIP) {
			return _cuenta.esAbonado() ? 15 : _titulo;
		} else {
			return _titulo;
		}
	}

	public String listaTitulosParaBD() {
		final StringBuilder str = new StringBuilder();
		for (final int b : _titulos.keySet()) {
			if (str.length() > 0) {
				str.append(",");
			}
			if (b == _titulo) {
				str.append("+");
			}
			str.append(b);
			if (_titulos.get(b) > -1) {
				str.append("*" + _titulos.get(b));
			}
		}
		return str.toString();
	}

	public void setTituloVIP(final String titulo) {
		_tituloVIP = titulo;
	}

	public String getTituloVIP() {
		return _tituloVIP;
	}

	public void addTitulo(final int titulo, int color) {
		if (titulo > 0) {
			_titulos.put(titulo, color);
		}
		_titulo = titulo;
		if (_enLinea) {
			refrescarEnMapa();
		}
	}

	public void setTitulo(final int titulo) {
		_titulo = titulo;
		if (_enLinea) {
			refrescarEnMapa();
		}
	}

	public ArrayList<Integer> getOrnamentos() {
		return _ornamentos;
	}

	public void addOrnamento(final int ornamento) {
		if (ornamento <= 0 || _ornamentos.contains(ornamento)) {
			return;
		}
		_ornamentos.add(ornamento);
		GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(this,
				"Felicidades has adquirido un nuevo Ornamento! Dirigete a el panel de ornamentos.");
		_ornamentos.trimToSize();
	}

	public void RemoOrnamento(final int ornamento) {
		if (ornamento <= 0 || !_ornamentos.contains(ornamento)) {
			return;
		}
		_ornamentos.remove(ornamento);
	}

	public void setOrnamento(final int ornamento) {
		if (ornamento <= 0 || _ornamentos.contains(ornamento)) {
			_ornamento = ornamento;
			refrescarEnMapa();
		} else {
			GestorSalida.ENVIAR_BN_NADA(this);
		}
	}

	public String listaOrnamentosParaBD() {
		final StringBuilder str = new StringBuilder();
		for (final int b : _ornamentos) {
			if (str.length() > 0) {
				str.append(",");
			}
			if (b == _ornamento) {
				str.append("+");
			}
			str.append(b);
		}
		return str.toString();
	}

	public boolean tieneOrnamento(final int ornamento) {
		return _ornamentos.contains(ornamento);
	}

	public Map<Integer, Integer> getTitulos() {
		return _titulos;
	}

	public boolean tieneTitulo(final int ornamento) {
		return _titulos.containsKey(ornamento);
	}

	public int getOrnamento() {
		return _ornamento;
	}

	public void setNombre(final String nombre) {
		_nombre = nombre;
		GestorSQL.UPDATE_NOMBRE_PJ(this);
		if (getMiembroGremio() != null) {
			GestorSQL.REPLACE_MIEMBRO_GREMIO(getMiembroGremio());
		}
	}

	public MisionPVP getMisionPVP() {
		return _misionPvp;
	}

	public void setMisionPVP(final MisionPVP mision) {
		_misionPvp = mision;
	}

	public String getEsposoListaAmigos() {
		final Personaje esposo = Mundo.getPersonaje(_esposoID);
		final StringBuilder str = new StringBuilder();
		if (esposo != null) {
			str.append(esposo.getNombre() + "|" + esposo._claseID + esposo._sexo + "|" + esposo._color1 + "|"
					+ esposo._color2 + "|" + esposo._color3 + "|");
			if (!esposo._enLinea) {
				str.append("|");
			} else {
				str.append(esposo.stringUbicEsposo() + "|");
			}
		} else {
			str.append("|");
		}
		return str.toString();
	}

	public String stringUbicEsposo() {
		return _mapa.getID() + "|" + _nivel + "|" + (_pelea != null ? 1 : 0);
	}

	public void seguirEsposo(Personaje esposo, String packet) {
		if (packet.charAt(3) == '+') {
			if (esposo.getMapa().getSubArea().getArea().getSuperArea() != _mapa.getSubArea().getArea().getSuperArea()) {
				if (esposo.getSexo() == Constantes.SEXO_FEMENINO) {
					GestorSalida.ENVIAR_Im_INFORMACION(this, "178");
				} else {
					GestorSalida.ENVIAR_Im_INFORMACION(this, "179");
				}
			}
			GestorSalida.ENVIAR_IC_PERSONAJE_BANDERA_COMPAS(this,
					esposo.getMapa().getX() + "|" + esposo.getMapa().getY());
		} else {
			GestorSalida.ENVIAR_IC_BORRAR_BANDERA_COMPAS(this);
		}
	}

	public void teleportIncarman(final Personaje perso) {
		if (perso.getMapa().getSubArea().getArea().getSuperArea().getID() != 3 || perso.getMapa().esMazmorra()) {
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(this, "Esta acción no está autorizada en este mapa.");
			return;
		}
		final short celdaPosicion = Camino.getCeldaIDCercanaLibre(perso.getCelda(), perso.getMapa());
		if (celdaPosicion == 0) {
			if (perso.getSexo() == Constantes.SEXO_FEMENINO) {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "141");
			} else {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "142");
			}
			return;
		}
		teleport(perso.getMapa().getID(), celdaPosicion);
	}

	public void teleportEsposo(final Personaje esposo) {
		if (!estaDisponible(false, true)) {
			if (esposo.getSexo() == Constantes.SEXO_FEMENINO) {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "139");
			} else {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "140");
			}
			return;
		}
		if (esFantasma() || esTumba()) {
			if (esposo.getSexo() == Constantes.SEXO_FEMENINO) {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "178");
			} else {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "179");
			}
			return;
		}
		final int dist = Camino.distanciaEntreMapas(_mapa, esposo.getMapa());
		if (dist > 10 || esposo.getMapa().esMazmorra()) {
			if (esposo.getSexo() == Constantes.SEXO_FEMENINO) {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "181");
			} else {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "180");
			}
			return;
		}
		final short celdaPosicion = Camino.getCeldaIDCercanaLibre(esposo.getCelda(), esposo.getMapa());
		if (celdaPosicion == 0) {
			if (esposo.getSexo() == Constantes.SEXO_FEMENINO) {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "141");
			} else {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "142");
			}
			return;
		}
		teleport(esposo.getMapa().getID(), celdaPosicion);
	}

	/*
	 * private void cambiarOrientacionADiagonal(final byte orientacion) { switch
	 * (_orientacion) { case 0: case 2: case 4: case 6: setOrientacion(orientacion);
	 * GestorSalida.ENVIAR_eD_CAMBIAR_ORIENTACION(_mapa, getID(), orientacion);
	 * break; } }
	 */

	public void addObjetoAlBanco(Objeto obj) {
		_cuenta.addObjetoAlBanco(obj);
	}

	public void setConsultarCasa(final Casa casa) {
		_consultarCasa = casa;
	}

	public Cofre getConsultarCofre() {
		return _consultarCofre;
	}

	public void setConsultarCofre(Cofre cofre) {
		_consultarCofre = cofre;
	}

	public Casa getConsultarCasa() {
		return _consultarCasa;
	}

	public Casa getAlgunaCasa() {
		if (_consultarCasa != null) {
			return _consultarCasa;
		}
		if (_casaDentro != null) {
			return _casaDentro;
		}
		return null;
	}

	public Casa getCasaDentro() {
		return _casaDentro;
	}

	public String stringColor() {
		return (_color1 <= -1 ? "" : Integer.toHexString(_color1)) + ","
				+ (_color2 <= -1 ? "" : Integer.toHexString(_color2)) + ","
				+ (_color3 <= -1 ? "" : Integer.toHexString(_color3));
	}

	private String strObjEnPosParaOa(final byte posicion, final byte posicion2) {
		Objeto objtem = getObjPosicion(posicion2);
		if (objtem == null) {
			objtem = getObjPosicion(posicion);
			if (objtem == null) {
				return "null";
			}
		}
		final Objeto obj = objtem;
		try {
			if (obj.getParamStatTexto(Constantes.STAT_APARIENCIA_OBJETO, 2) != "") {
				return obj.getParamStatTexto(Constantes.STAT_APARIENCIA_OBJETO, 3);
			}
			if (obj.getObjevivoID() > 0) {
				final Objeto objVivo = Mundo.getObjeto(obj.getObjevivoID());
				if (objVivo != null) {
					return Integer.toHexString(objVivo.getObjModeloID()) + "~" + obj.getObjModelo().getTipo() + "~"
							+ Byte.parseByte(objVivo.getParamStatTexto(Constantes.STAT_SKIN_OBJEVIVO, 3), 16);
				} else {
					obj.setIDObjevivo(0);
				}
			}
			if (Mundo.getCreaTuItem(obj.getObjModeloID()) != null) {
				return Integer.toHexString(obj.getObjModeloID()) + "~" + obj.getObjModelo().getTipo() + "~"
						+ (Integer.parseInt(obj.getParamStatTexto(Constantes.STAT_CAMBIAR_GFX_OBJETO, 3), 16) + 1);
			}
		} catch (Exception e) {
		}
		return Integer.toHexString(obj.getObjModeloID());
	}

	public void setExchanger(final Exchanger intercambiando) {
		_exchanger = intercambiando;
	}

	@SuppressWarnings("rawtypes")
	public Object getIntercambiandoCon(Class clase) {
		if (_exchanger == null) {
			return null;
		}
		if (_exchanger.getClass() == clase) {
			return (clase.cast(_exchanger));
		}
		return null;
	}

	public Exchanger getExchanger() {
		return _exchanger;
	}

	public String getStringTienda() {
		final StringBuilder str = new StringBuilder();
		for (final Objeto obj : _tienda.getObjetos()) {
			str.append(obj.getID() + "|");
		}
		return str.toString();
	}

	public void borrarObjTienda(final Objeto obj) {
		_tienda.borrarObjeto(obj);
	}

	public ArrayList<Objeto> getObjetosTienda() {
		return _tienda.getObjetos();
	}

	public Tienda getTienda() {
		return _tienda;
	}

	public long precioTotalTienda() {
		long precio = 0;
		for (final Objeto obj : _tienda.getObjetos()) {
			precio += obj.getPrecio();
		}
		return precio;
	}

	public String getListaExchanger(Personaje perso) {
		final StringBuilder str = new StringBuilder();
		for (final Objeto obj : _tienda.getObjetos()) {
			if (str.length() > 0) {
				str.append("|");
			}
			str.append(obj.getID() + ";" + obj.getCantidad() + ";" + obj.getObjModeloID() + ";"
					+ obj.convertirStatsAString(false) + ";" + obj.getPrecio());
		}
		return str.toString();
	}

	public String stringGMLuchador() {
		StringBuilder str = new StringBuilder();
		str.append(getClaseID(false) + ";");
		str.append(getGfxID(false) + "^" + getTalla() + ";");
		str.append(getSexo() + ";");
		str.append(getNivel() + ";");
		str.append(getAlineacion() + ",");
		str.append(getOrdenNivel() + ",");
		str.append((alasActivadas() ? getGradoAlineacion() : "0") + ",");
		str.append((getID() + getNivel()) + "," + (getDeshonor() > 0 ? 1 : 0) + ";");
		str.append((getColor1() > -1 ? Integer.toHexString(getColor1()) : -1) + ";");
		str.append((getColor2() > -1 ? Integer.toHexString(getColor2()) : -1) + ";");
		str.append((getColor3() > -1 ? Integer.toHexString(getColor3()) : -1) + ";");
		if (MobAyudante != null) {
			if (esMultiman() && !invocacionControlable) {
				str.append(MobAyudante.getStringAccesorios() + ";");
			}
		} else {
			str.append(getStringAccesorios() + ";");
			invocacionControlable = false;
		}
		return str.toString();
	}

	public Map<Integer, StatHechizo> getHechizos() {
		return _mapStatsHechizos;
	}

	public void addXPGanada(long exp) {
	}

	public void mostrarGrupo() {
		if (_grupo == null) {
			return;
		}
		Personaje lider = _grupo.getLiderGrupo();
		GestorSalida.ENVIAR_PCK_CREAR_GRUPO(this, lider.getNombre());
		GestorSalida.ENVIAR_PL_LIDER_GRUPO(this, lider.getID());
		GestorSalida.ENVIAR_PM_TODOS_MIEMBROS_GRUPO_A_PERSO(this, _grupo);
	}

	@Override
	public void murio() {
	}

	@Override
	public void sobrevivio() {
	}

	boolean cambiarNombre = true;

	public void cambiarNombre(String nombre) {
		if (MainServidor.PARAM_CAMBIAR_NOMBRE_CADA_RESET && !cambiarNombre) {
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(this, "No puedes cambiar de nombre hasta el proximo reset");
			return;
		}
		setNombre(nombre);
		GestorSalida.ENVIAR_bn_CAMBIAR_NOMBRE_CONFIRMADO(this, nombre);
		refrescarEnMapa();
		GestorSalida.ENVIAR_Im_INFORMACION(this, "1NAME_CHANGED;" + nombre);
		cambiarNombre = false;
	}

	public static String nombreValido(String nombre, boolean comando) {
		if (Mundo.getPersonajePorNombre(nombre) != null) {
			return null;
		}
		if (!MainServidor.PALABRAS_PROHIBIDAS.isEmpty()) {
			String[] filtro = nombre.replace(".", " ").split(" ");
			int veces = 0;
			for (String s : filtro) {
				if (MainServidor.PALABRAS_PROHIBIDAS.contains(s.toLowerCase())) {
					veces++;
				}
			}
			if (veces == 0) {
				String filtro2 = nombre.replace(" ", "");
				for (String s : MainServidor.PALABRAS_PROHIBIDAS) {
					if (s.length() < 5) {
						continue;
					}
					if (filtro2.toLowerCase().contains(s)) {
						veces++;
					}
				}
			}
			if (veces > 0) {
				return "";
			}
		}
		if (nombre.length() < 1 || nombre.length() > 20) {
			return "";
		}
		if (!comando) {
			StringBuilder nombreFinal = new StringBuilder("");
			final String nLower = nombre.toLowerCase();
			final String abcMin = "abcdefghijklmnopqrstuvwxyz-";
			int cantSimbol = 0;
			char letra_A = ' ', letra_B = ' ';
			boolean primera = true;
			for (final char letra : nLower.toCharArray()) {
				if (primera && letra == '-' || !abcMin.contains(letra + "") || letra == letra_A && letra == letra_B) {
					return "";
				}
				if (primera) {
					nombreFinal.append((letra + "").toUpperCase());
				} else {
					nombreFinal.append((letra + ""));
				}
				primera = false;
				if (abcMin.contains(letra + "") && letra != '-') {
					letra_A = letra_B;
					letra_B = letra;
				} else if (letra == '-') {
					primera = true;
					if (cantSimbol >= 1) {
						return "";
					}
					cantSimbol++;
				}
			}
			if (MainServidor.PARAM_CORREGIR_NOMBRE_JUGADOR) {
				nombre = nombreFinal.toString();
			}
		}
		return nombre;
	}

	public void addSetRapido(int id, String nombre, int icono, String data) {
		SetRapido set = new SetRapido(id, nombre, icono, data);
		_setsRapidos.put(set.getID(), set);
	}

	public void borrarSetRapido(int id) {
		_setsRapidos.remove(id);
	}

	public SetRapido getSetRapido(int id) {
		return _setsRapidos.get(id);
	}

	public String getSetsRapidos() {
		StringBuilder str = new StringBuilder();
		for (SetRapido s : _setsRapidos.values()) {
			if (str.length() > 0) {
				str.append("*");
			}
			str.append(s.getString());
		}
		return str.toString();
	}

	public void actualizarSetsRapidos(int oldID, int newID, byte oldPos, byte newPos) {
		boolean b = false;
		for (SetRapido set : _setsRapidos.values()) {
			b |= set.actualizarObjetos(oldID, newID, oldPos, newPos);
		}
		if (b) {
			GestorSalida.ENVIAR_Os_SETS_RAPIDOS(this);
		}
	}

	public Stats getStatsObjEquipados() {
		return _totalStats.getStatsObjetos();
	}

	@Override
	public void addKamas(long k, Personaje perso) {
		addKamas(k, false, true);
	}

	@Override
	public synchronized void addObjetoExchanger(Objeto objeto, long cantidad, Personaje perso, int precio) {
	}

	@Override
	public synchronized void remObjetoExchanger(Objeto objeto, long cantidad, Personaje perso, int precio) {
	}

	@Override
	public void cerrar(Personaje perso, String exito) {
		perso.cerrarVentanaExchange(exito);
	}

	public void botonOK(Personaje perso) {
	}

	@Override
	public String getArgsDialogo(String args) {
		if (args.isEmpty()) {
			return args;
		}
		if (args.substring(0, 4).contains("LOC,")) {
			String mapaString = args.substring(4);
			try {
				short mapaNumero = Short.parseShort(mapaString);
				Mapa mapa = Mundo.getMapa(mapaNumero);
				GestorSalida.ENVIAR_IC_PERSONAJE_BANDERA_COMPAS(this, mapa.getX() + "|" + mapa.getY());
			} catch (Exception e) {
				System.out.print("Error Dialogos argumentos: " + e.getMessage() + " Perso: " + getNombre());
			}
		}
		if (args.substring(0, 4).contains("COB,")) {
			for (String argumento : args.substring(4).split("&")) {
				String[] mapaString = argumento.split(";", 2);
				try {
					Accion.realizar_Accion_Estatico(Integer.parseInt(mapaString[0]), mapaString[1], this, null, -1,
							(short) -1);
				} catch (Exception e) {
					System.out.print("Error Dialogos argumentos: " + e.getMessage() + " Perso: " + getNombre());
				}
			}
		}
		if (Mundo.evento) {
			if (args.equalsIgnoreCase("EVENTO")) {
				String[] premios = MainServidor.EVENTO_PREMIO_OBJETOS.split(",");
				int indice = Formulas.getRandomInt(0, premios.length - 1);
				int id = Integer.parseInt(premios[indice]);
				ObjetoModelo tempObjMod = Mundo.getObjetoModelo(id);
				if (tempObjMod == null) {
					GestorSalida.ENVIAR_BN_NADA(this);
				} else {
					addObjIdentAInventario(
							tempObjMod.crearObjeto(1, Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM),
							false);
					GestorSalida.ENVIAR_Im_INFORMACION(this, "021;" + 1 + "~" + id);
				}
				this.PuntosPrestigio += MainServidor.ptsNPC;
				this.sendMessage("Has ganado <b>" + MainServidor.ptsNPC + "</b> Pts. por encontrar el NPC");
				GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(
						"El jugador " + getNombre() + " ha encontrado el npc de evento y ha recibido su premio");
				for (final NPC npc : _mapa.getNPCs().values()) {
					if (!npc.is_evento())
						continue;
					GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(_mapa, npc.getID());
					_mapa.borrarNPC(npc.getID());
				}
				Mundo.evento = false;
				Mundo.eventoMensaje = "";
				Mundo.mapaNPC = -1;
			} else if (args.equalsIgnoreCase("EVENTO2")) {
				GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(
						"El jugador " + getNombre() + " ha encontrado el npc de evento y va a comprar su premio");
				for (final NPC npc : _mapa.getNPCs().values()) {
					if (!npc.is_evento())
						continue;
					GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(_mapa, npc.getID());
					_mapa.borrarNPC(npc.getID());
				}
				setTipoExchange(Constantes.INTERCAMBIO_TIPO_TIENDA_NPC);
				setExchanger(MainServidor.NPC_EVENTO);
				GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(this, Constantes.INTERCAMBIO_TIPO_TIENDA_NPC,
						MainServidor.NPC_EVENTO.getModelo().getGfxID() + "");
				GestorSalida.ENVIAR_EL_LISTA_EXCHANGER(this, MainServidor.NPC_EVENTO);
				this.PuntosPrestigio += MainServidor.ptsNPC;
				this.sendMessage("Has ganado <b>" + MainServidor.ptsNPC + "</b> Pts. por encontrar el NPC");
				Mundo.evento = false;
				Mundo.eventoMensaje = "";
				Mundo.mapaNPC = -1;
			}
		}
		return ";" + args.replace("[nombre]", getStringVar("nombre"))
				.replace("[costoBanco]", getStringVar("costoBanco")).replace("[liderPVP]", Mundo._liderRankingPvp)
				.replace("[liderKoliseo]", Mundo._liderRankingKoliseo)
				.replace("[npcKamas]", MainServidor.KAMAS_RULETA_JALATO + "").replace("EVENTO", getStringVar("nombre"));
	}

	@Override
	public void addKamasGanada(double kamas) {
		// TODO Auto-generated method stub

	}

	public int getAdmin() {
		return GestorSQL.GET_RANGO_PERSO(getID());
	}

	public void setRango(final int rango) {
		GestorSQL.SET_RANGO_PERSO(getID(), rango);
	}

	public long getTiempoKoliseo() {
		return _tiempoKoliseo;
	}

	public void setTiempoKoliseo() {
		this._tiempoKoliseo = System.currentTimeMillis() + (MainServidor.MINUTOS_MISION_PVP * 60000);
	}

	@Override
	public int compareTo(Personaje e) {
		return e.getCuenta().getActualIP().compareTo(getCuenta().getActualIP());
	}

	public boolean convertirMascota(int id) {
		Objeto obj = getObjeto(id);
		if (obj == null) {
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(this, "La mascota no existe");
			return false;
		}
		if (_montura == null) {
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(this,
					"No tienes puesta la montura a la cual le asignaras los stats");
			return false;
		}
		StringBuilder statsTexto = new StringBuilder();
		boolean primero = true;
		for (Entry<Integer, Integer> stats : obj.getStats().getEntrySet2().entrySet()) {
			if (primero) {
				statsTexto.append(stats.getKey() + "," + stats.getValue());
				primero = false;
			} else {
				statsTexto.append(";" + stats.getKey() + "," + stats.getValue());
			}
		}
		restarCantObjOEliminar(obj.getID(), 1, true);
		if (statsTexto.length() == 0) {
			_montura.set_statsString(Constantes.STAT_APARIENCIA_OBJETOS + "~" + obj.getObjModeloID());
		} else {
			_montura.set_statsString(
					statsTexto.toString() + ";" + Constantes.STAT_APARIENCIA_OBJETOS + "~" + obj.getObjModeloID());
		}
		GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(this);
		GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
		subirBajarMontura(false);
		return true;
	}

	public boolean convertirMascota() {
		Objeto obj = getObjPosicion(Constantes.OBJETO_POS_MASCOTA);
		if (obj == null) {
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(this, "No tiene la mascota puesta que vas a convertir");
			return false;
		}
		if (_montura == null) {
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(this,
					"No tienes puesta la montura a la cual le asignaras los stats");
			return false;
		}
		StringBuilder statsTexto = new StringBuilder();
		boolean primero = true;
		for (Entry<Integer, Integer> stats : obj.getStats().getEntrySet2().entrySet()) {
			if (primero) {
				statsTexto.append(stats.getKey() + "," + stats.getValue());
				primero = false;
			} else {
				statsTexto.append(";" + stats.getKey() + "," + stats.getValue());
			}
		}
		borrarOEliminarConOR(obj.getID(), true);
		_montura.set_statsString(statsTexto.toString());
		GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(this);
		GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
		subirBajarMontura(false);
		return true;
	}

	public void set_acciones(Map<Integer, AccionDeJuego> acciones) {
		_acciones = acciones;
	}

	public Map<Integer, AccionDeJuego> get_acciones() {
		return _acciones;
	}

	public boolean get_Refrescarmobspelea() {
		return this._refrescarmobsautomatico;
	}

	public void set_Refrescarmobspelea(boolean estado) {
		this._refrescarmobsautomatico = estado;
	}

	public int getRangoKoliSinReset() {
		return Formulas.rangoKoli2(this);
	}

	public int getRangoKoli() {
		return Formulas.rangoKoli(this);
	}

	public int get_rostroGFX() {
		return _rostroGFX;
	}

	public void set_rostroGFX(int _rostroGFX) {
		this._gfxID = _rostroGFX;
		this._rostroGFX = _rostroGFX;
	}

	public int getNivelOmega() {
		return _nivelOmega;
	}

	/*
	 * public int getSubclase() { return buffClase; } public void setSubclase(int
	 * id) { this.buffClase = id; }
	 */
	public void setNivelOmega(int nivelOmega) {
		this._nivelOmega = nivelOmega;
	}

	public Map<Integer, ArrayList<Idolo>> getIdolos() {
		Map<Integer, ArrayList<Idolo>> idolos = new TreeMap<>();
		for (byte i = 112; i <= 117; i++) {
			Objeto temp = getObjPosicion(i);
			if (temp != null) {
				Idolo idolo = Mundo.getIdolo(temp.getObjModeloID());
				if (idolo != null) {
					if (idolos.containsKey(idolo.getEjecucion())) {
						idolos.get(idolo.getEjecucion()).add(idolo);
					} else {
						idolos.put(idolo.getEjecucion(), new ArrayList<>());
						idolos.get(idolo.getEjecucion()).add(idolo);
					}
				}
			}
		}
		return idolos;
	}

	public int getBonusIdolo() {
		int bonusIdolo = 0;
		for (byte i = 112; i <= 117; i++) {
			Objeto temp = getObjPosicion(i);
			if (temp != null) {
				Idolo idolo = Mundo.getIdolo(temp.getObjModeloID());
				if (idolo != null) {
					bonusIdolo += idolo.getBonus();
				}
			}
		}
		return bonusIdolo;
	}

	public ArrayList<LogroPersonaje> getLogros() {
		return _logros;
	}

	public void confirmarLogrosPelea(final Map<Integer, Integer> mobs) {
		for (LogroPersonaje logro : _logros) {
			if (logro.getLogro().getTipo() != 1)
				continue;
			logro.confirmarMision(mobs, "", this);
		}
	}

	public class MutableInteger {
		private int value;

		public MutableInteger(int value) {
			this.value = value;
		}

		public void set(int value) {
			this.value = value;
		}

		public int intValue() {
			return value;
		}
	}

	public void verificarPeleasLogros(final Map<Integer, Integer> mobs, boolean puede) {
		if (puede) {
			for (final Entry<Integer, Integer> entry : mobs.entrySet()) {
				if (this._mobsJefes.containsKey(entry.getKey())) {
					this._mobsJefes.put(entry.getKey(), this._mobsJefes.get(entry.getKey()).intValue() + 1);
				} else {
					this._mobsJefes.put(entry.getKey(), 1);
				}
			}
		}
	}

	public void verificarPeleasPase(final Map<Integer, Integer> mobs, Mapa mapa) {
		for (final Entry<Integer, Integer> entry : mobs.entrySet()) {
			if (mapa.esMazmorra()) {
				if (entry.getKey() == 423) {
					this.PuntosPrestigio += MainServidor.ptsRaid;
					this.sendMessage("Has ganado <b>" + MainServidor.ptsRaid + "</b> Pts. por mobs Raid");
				} else if (entry.getKey() == 40523) {
					this.PuntosPrestigio += MainServidor.ptsRaid1;
					this.sendMessage("Has ganado <b>" + MainServidor.ptsRaid1 + "</b> Pts. por mobs Raid");
				} else if (entry.getKey() == 40534) {
					this.PuntosPrestigio += MainServidor.ptsRaid2;
					this.sendMessage("Has ganado <b>" + MainServidor.ptsRaid2 + "</b> Pts. por mobs Raid");
				} else if (entry.getKey() == 40544) {
					this.PuntosPrestigio += MainServidor.ptsRaid3;
					this.sendMessage("Has ganado <b>" + MainServidor.ptsRaid3 + "</b> Pts. por mobs Raid");
				}
				if (MainServidor.MOBS_JEFE.contains(entry.getKey()) && mapa.getID() != 11095) {
					this.sendMessage("Has ganado <b>" + MainServidor.ptsJefe + "</b> Pts. por mobs Jefe");
					this.PuntosPrestigio += MainServidor.ptsJefe;
				}
			}
		}
	}

	public boolean ipDropActivated = false;

	public void sendMessage(String msg) {
		GestorSalida.GAME_SEND_MESSAGE(this, msg);
	}

	public void confirmarLogrosRecolecta(final String recolecta) {
		for (LogroPersonaje logro : _logros) {
			if (logro.getLogro().getTipo() != 2)
				continue;
			logro.confirmarMision(null, recolecta, this);
		}
	}

	public void confirmarLogrosNPC(final String npc) {
		for (LogroPersonaje logro : _logros) {
			if (logro.getLogro().getTipo() != 4)
				continue;
			logro.confirmarMision(null, npc, this);
		}
	}

	public void borrarGA(AccionDeJuego AJ) {
		if (AJ == null)
			return;
		if (AJ._accionID == 1 && getPelea() == null) {
			String packet = "";
			for (AccionDeJuego curAction : _accionesCola) {
				short cellID = 0;
				if (curAction != null) {
					try {
						packet = curAction._packet.substring(5).split(";")[0];
						cellID = (short) Camino.celdaMasCercanaACeldaObjetivo(getMapa(),
								(short) Integer.parseInt(packet.split(";")[0]), getCelda().getID(), null, false);
					} catch (Exception e) {
						e.printStackTrace();
					}
					int dist = Camino.distanciaDosCeldas(getMapa(), cellID, getCelda().getID());
					if (dist <= 30) {
						getServidorSocket();
						ServidorSocket.juego_Accion(curAction, this, _cuenta);
					}
				}
			}
		}
		if (_acciones.containsKey(AJ._idUnica))
			_acciones.remove(AJ._idUnica);
	}

	public boolean activa = true;

	public boolean isOnAction() {
		return _isOnAction;
	}

	public void addGA(AccionDeJuego AJ) {
		if (isOnAction())
			return;
		if (AJ._accionID == 1 && getPelea() == null) {
			Marche = true;
			_accionesCola.clear();
		}
		_acciones.put(AJ._idUnica, AJ);
	}

	public void setIsOnAction(boolean is) {
		_isOnAction = is;
	}

	public void confirmarPeleaPVP(String gano) {
		for (LogroPersonaje logro : _logros) {
			if (logro.getLogro().getTipo() != 5 && logro.getLogro().getTipo() != 6)
				continue;
			logro.confirmarMision(null, gano, this);
		}
	}

}
