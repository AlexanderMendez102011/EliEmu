package variables.personaje

import java.util.ArrayList
import java.util.concurrent.CopyOnWriteArrayList
import estaticos.GestorSalida

class Grupo {
    private var _rastrear: Personaje? = null
    private val _integrantes: CopyOnWriteArrayList<Personaje> = CopyOnWriteArrayList<Personaje>()

    /*private synchronized void teleport(int id, short cell) {
		if (getLiderGrupo() == null) {
			return;
		}
		for (Personaje p : _alumnos) {
			if(p.getPelea() != null){
				GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(getLiderGrupo(), p.getNombre() + " Se encuentra en pelea.");
				return;
		   }
			try {
				if (p.getID() == getLiderGrupo().getID()) {
					continue;
				}
				p.teleport(id, cell);
			} catch (Exception e) {
			}
		}
	}

	public synchronized void teleportATodos(int id, short cell) {
		if (getLiderGrupo() == null) {
			return;
		}
		if (_integrantes.isEmpty()) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(getLiderGrupo(), "Has traido a todo tu grupo con exito.", "B9121B");	
			return;
		}
		for (Personaje p : _integrantes) {
			try {
				if(p.getPelea() != null){
					GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(getLiderGrupo(), p.getNombre() + " Se encuentra en pelea.");
					return;
				}
				if (p.getID() == getLiderGrupo().getID()) {
					continue;
				}
				p.teleport(id, cell);
			} catch (Exception e) {
			}
		}
	}
	public synchronized void teleportATodosZapps(int id) {
		if (_alumnos.isEmpty()) {
			return;
		}
		for (Personaje p : _alumnos) {
		if(p.getPelea() != null){
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(getLiderGrupo(), p.getNombre() + " Se encuentra en pelea.");
			return;
		}
		}
		final short celdaID = (short)Mundo.getCeldaZaapPorMapaID(id);
		teleport(id, celdaID);
	}

	public synchronized void teleportATodosZappis(short id) {
		if (_alumnos.isEmpty()) {
			return;
		}
		for (Personaje p : _alumnos) {
		if(p.getPelea() != null){
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(getLiderGrupo(), p.getNombre() + " Se encuentra en pelea.");
			return;
		}
		}
		final Mapa mapa = Mundo.getMapa(id);
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
		teleport(id, celdaID);
	}

	public void EsconderPanel() {
		if (_alumnos.isEmpty() || !_autoUnir) {
			return;
		}
		for (Personaje p : _alumnos) {
			if(p.getPelea() != null){
				GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(getLiderGrupo(), p.getNombre() + " Se encuentra en pelea.");
				return;
			}
			//GestorSalida.ENVIAR_wC_ESCONDER_ESCLAVO(p);
			}
		}
	
	public void usarZonas(final short mapaID) {
		for (Personaje p : _alumnos) {
			if(p.getPelea() != null){
				GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(getLiderGrupo(), p.getNombre() + " Se encuentra en pelea.");
				return;
			}
		}
		try {
			if (Mundo.getMapa(mapaID) == null || Mundo.ZONAS.get(mapaID) == null) {
				return;
			}
			teleport(mapaID, Mundo.ZONAS.get(mapaID));
		} catch (final Exception e) {
		}
	}

	public synchronized void unirAPelea(Pelea pelea) {
		if (_alumnos.isEmpty() || !_autoUnir) {
			return;
		}
		for (Personaje p : _alumnos) {
			_packet = "GA903" + pelea.getID() + ";" + getLiderGrupo().getID();
			if (p.getPelea() == null && p.getMapa() == getLiderGrupo().getMapa()) {
				p.getCuenta().getSocket().analizar_Packets(_packet, false);
			}
		}
	}

	public synchronized void unirAPeleaEsclavos(Pelea pelea, Personaje perso) {
		if (_alumnos.isEmpty() || !_autoUnir) {
			return;
		}
		if(perso == getLiderGrupo()) {
			for (Personaje p : _alumnos) {
				if (perso.getMapa() != getLiderGrupo().getMapa()) {
					return;
				}
				if (p.getPelea() != null) {
					return;
				}
				_packet = "GA903" + pelea.getID() + ";" + getLiderGrupo().getID();
				p.getCuenta().getSocket().analizar_Packets(_packet, true);
			}
		}else {
		for (Personaje p : _integrantes) {
			if (perso.getMapa() != getLiderGrupo().getMapa()) {
				return;
			}
			if (p.getPelea() != null) {
				return;
			}
			_packet = "GA903" + pelea.getID() + ";" + perso.getID();
			p.getCuenta().getSocket().analizar_Packets(_packet, true);
		}
		}
	}

	public synchronized void JuegoListo(String packet) {
		if (_alumnos.isEmpty())
			return;
		String _packet = packet;
		for (Personaje p : _alumnos) {
			if (p.getMapa() != getLiderGrupo().getMapa())
				return;
			if (p.getPelea() != null) {
				p.getCuenta().getSocket().analizar_Packets(_packet, true);
			}
		}
	}

	public synchronized void packetSeguirLider(String packet) { //Moverse
		if (_alumnos.isEmpty())
			return;
		String _packet = packet;
		for (Personaje p : _alumnos) {
			if(p == null)
			{
				return;
			}
			if(p.getPelea() != null){
				continue;
			}
			if (p.getMapa() != getLiderGrupo().getMapa())
				continue;
			while (p.getCelda() != getLiderGrupo().getCelda()) {
				p.setCelda(getLiderGrupo().getCelda());
				GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA_SIN_HUMO(p.getMapa(), p);
			}
			p.getCuenta().getSocket().analizar_Packets(_packet, true);
		}
	}*/  var autoUnir = true
    fun esLiderGrupo(perso: Personaje): Boolean {
        return if (_integrantes.isEmpty()) {
            false
        } else _integrantes.get(0).getID() === perso.getID()
    }

    val persos: CopyOnWriteArrayList<Personaje>
        get() = _integrantes

    //
    fun addIntegrante(perso: Personaje) {
        if (_integrantes.contains(perso)) {
            return
        }
        _integrantes.add(perso)
        perso.setGrupo(this)
    }

    val iDsPersos: ArrayList<Integer>
        get() {
            val lista: ArrayList<Integer> = ArrayList<Integer>()
            for (perso in _integrantes) {
                lista.add(perso.getID())
                for (compa in perso.getCompa) {
                }
                eros()
                run {
                    if (compa.esMultiman()) {
                        lista.add(compa.getID())
                    }
                }
            }
            return lista
        }
    val nivelGrupo: Int
        get() {
            var nivel = 0
            for (p in _integrantes) {
                nivel += p.getNivel()
            }
            return nivel
        }
    val miembros: CopyOnWriteArrayList<Personaje>
        get() = _integrantes
    val liderGrupo: Personaje?
        get() = if (_integrantes.isEmpty()) {
            null
        } else _integrantes.get(0)
    var rastrear: Personaje?
        get() = _rastrear
        set(seguir) {
            _rastrear = seguir
        }

    fun dejarGrupo(expulsado: Personaje, expLider: Boolean) {
        if (!_integrantes.contains(expulsado)) {
            return
        }
        if (expulsado.liderMaitre) {
            for (pjx in expulsado.getGrupoParty().getPersos()) {
                if (pjx === expulsado) {
                    expulsado.liderMaitre = false
                    continue
                }
                pjx.esMaitre = false
            }
        }
        if (expulsado.getGrupoParty().getPersos().size() <= 2) {
            if (expulsado.esMaitre) {
                for (pjx in expulsado.getGrupoParty().getPersos()) {
                    if (pjx === expulsado) {
                        expulsado.esMaitre = false
                        continue
                    }
                    pjx.liderMaitre = false
                }
            }
        }
        if (expulsado.esMaitre) expulsado.esMaitre = false
        if (_rastrear === expulsado) {
            _rastrear = null
            for (perso in _integrantes) {
                GestorSalida.ENVIAR_IC_BORRAR_BANDERA_COMPAS(perso)
                GestorSalida.ENVIAR_PF_SEGUIR_PERSONAJE(perso, "-")
            }
        }
        if (expulsado.enLinea()) {
            GestorSalida.ENVIAR_PV_DEJAR_GRUPO(expulsado, if (expLider) liderGrupo.getID().toString() + "" else "")
            GestorSalida.ENVIAR_IH_COORDENADAS_UBICACION(expulsado, "")
        }
        expulsado.setGrupo(null)
        _integrantes.remove(expulsado)
        if (_integrantes.size() === 1) {
            dejarGrupo(_integrantes.get(0), false)
        } else if (_integrantes.size() >= 2) {
            GestorSalida.ENVIAR_PM_EXPULSAR_PJ_GRUPO(this, expulsado.getID())
        }
    }
}