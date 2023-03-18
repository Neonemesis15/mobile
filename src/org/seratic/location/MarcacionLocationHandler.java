package org.seratic.location;

import java.util.ArrayList;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.org.seratic.lucky.PendientesActivity;
import com.org.seratic.lucky.PuntosVentaActivity;
import com.org.seratic.lucky.accessData.control.E_tbl_mov_fotosController;
import com.org.seratic.lucky.accessData.control.MovMarcacionController;
import com.org.seratic.lucky.accessData.control.MovRegistroNoVisitaBodegaController;
import com.org.seratic.lucky.accessData.control.MovRegistroVisitaController;
import com.org.seratic.lucky.accessData.control.TblMovRegPDVController;
import com.org.seratic.lucky.accessData.control.TblPuntoGPSController;
import com.org.seratic.lucky.accessData.entities.E_MovMarcacion;
import com.org.seratic.lucky.accessData.entities.E_TBL_MOV_REGISTROVISITA;
import com.org.seratic.lucky.accessData.entities.E_TblMovRegPDV;
import com.org.seratic.lucky.accessData.entities.E_Tbl_Mov_RegistroBodega;
import com.org.seratic.lucky.accessData.entities.E_Tbl_Mov_RegistroBodega_Detalle;
import com.org.seratic.lucky.accessData.entities.E_Usuario;
import com.org.seratic.lucky.accessData.entities.E_tbl_mov_fotos;
import com.org.seratic.lucky.accessData.entities.TblPuntoGPS;
import com.org.seratic.lucky.comunicacion.Conexion;
import com.org.seratic.lucky.comunicacion.IComunicacionListener;
import com.org.seratic.lucky.gui.vo.PeticionGPS;
import com.org.seratic.lucky.manager.DatosManager;
import com.org.seratic.lucky.manager.GPSManager;
import com.org.seratic.lucky.model.E_Codigo_ITT_New;
import com.org.seratic.lucky.model.E_MarcacionRequest;
import com.org.seratic.lucky.model.E_RegistrarMotivoColgateBodega_Request;
import com.org.seratic.lucky.model.E_VisitaRequest;
import com.org.seratic.lucky.model.ReportesColgateBodega_RegistrarPDV_Mov_Request;
import com.org.seratic.lucky.vo.PuntoventaVo;



public class MarcacionLocationHandler extends Handler implements IComunicacionListener, IGPSManager {
	public final static int ACCION_REGISTRAR_INICIO = 0;
	public final static int ACCION_REGISTRAR_FINAL = 1;
	public final static int SOLO_OBTENER_COORDENADAS = 2;
	public final static int ACCION_REGISTRAR_INICIO_VISITA = 3;
	public final static int ACCION_REGISTRAR_FINAL_VISITA = 4;
	public final static int ACCION_REGISTRAR_FINAL_VISITA_CON_REPORTES = 5;

	private ProgressDialog pd;

	private SQLiteDatabase db;
	private LocationRequester requester;
	private TblPuntoGPS puntoGPS;
	private Activity activity;
	private E_MovMarcacion movMarcacion;
	private E_TBL_MOV_REGISTROVISITA movRegVisita;
	private int accion;
	private int actividad;

	// private E_UsuarioController e_UsuarioController;
	private TblPuntoGPSController puntoGPSController;
	// private GenericController genericController;

	Conexion conexion;
	private boolean esperandoRespuesta = false;
	private Handler hdler;
	// PuntoGPSManager gpsManager;
	private MovRegistroVisitaController movRVController;
	private MovMarcacionController movController;
	private String	mensajeServer="";

	public MarcacionLocationHandler(SQLiteDatabase db, Activity activity) {
		super();
		this.db = db;
		this.activity = activity;
		// gpsManager = new PuntoGPSManager(this);
		movController = new MovMarcacionController(db);
		movRVController = new MovRegistroVisitaController(db);
		mensajeServer="";
	}

	@Override
	public void handleMessage(Message msg) {
		Log.i("MarcacionLocationHandler", "handleMessage. accion = " + msg.arg1);
		if (pd != null) {
			pd.dismiss();
		}

	}

	public void enviarMarcion(E_MovMarcacion movMarcacion) {
		this.movMarcacion = movMarcacion;
		esperandoRespuesta = true;
		conexion = Conexion.getInstance(this.activity);
		conexion.setComListener(this, activity.getBaseContext());
		E_Usuario e_UsuarioMarcacion = DatosManager.getInstancia().getUsuario();
		TblPuntoGPS puntoGpsInicial = getPuntoGps(movMarcacion.getIdPunto_inicio());
		TblPuntoGPS puntoGpsFinal = null;
		if (movMarcacion.getIdPunto_fin() != 0) {
			puntoGpsFinal = getPuntoGps(movMarcacion.getIdPunto_fin());
			movMarcacion.setEstado(MovMarcacionController.ESTADO_MARCACION_FIN_ENVIANDO);
			movController.edit(movMarcacion);
		}
		E_MarcacionRequest e_MarcacionRequest = new E_MarcacionRequest(movMarcacion, e_UsuarioMarcacion, puntoGpsInicial, puntoGpsFinal);
		conexion.registrarMarcacion(e_MarcacionRequest);
	}

	private void enviarVisita(E_TBL_MOV_REGISTROVISITA movRegVisita) {
		this.movRegVisita = movRegVisita;
		esperandoRespuesta = true;
		conexion = Conexion.getInstance(this.activity);
		conexion.setComListener(this, activity.getBaseContext());
		E_Usuario e_UsuarioMarcacion = DatosManager.getInstancia().getUsuario();
		TblPuntoGPS puntoGpsInicial = getPuntoGps(movRegVisita.getIdPuntoGPSInicio());
		TblPuntoGPS puntoGpsFinal = null;
		if (movRegVisita.getIdPuntoGPSFin() != 0) {
			puntoGpsFinal = getPuntoGps(movRegVisita.getIdPuntoGPSFin());
		}
		int id_foto = movRegVisita.getIdFoto();
		E_tbl_mov_fotos e_foto = new E_tbl_mov_fotosController(db).getById(id_foto);
		E_VisitaRequest e_VisitaRequest = new E_VisitaRequest(movRegVisita, e_UsuarioMarcacion, puntoGpsInicial, puntoGpsFinal, e_foto);
		conexion.registrarVistaPuntoVenta(e_VisitaRequest);
		// DatosManager.getInstancia().enviarReportePuntoVenta(e_VisitaRequest,
		// db);

	}

	private TblPuntoGPS getPuntoGps(int idPunto) {
		if (puntoGPSController == null) {
			puntoGPSController = new TblPuntoGPSController(db);
		}
		TblPuntoGPS puntoGps = puntoGPSController.getPuntoById(idPunto);
		return puntoGps;
	}

	@Override
	public void dispatchMessage(Message msg) {
		// TODO Auto-generated method stub
		super.dispatchMessage(new Message());
	}

	public ProgressDialog getPd() {
		return pd;
	}

	public void setPd(ProgressDialog pd) {
		this.pd = pd;
	}

	public LocationRequester getRequester() {
		return requester;
	}

	public void setRequester(LocationRequester requester) {
		this.requester = requester;
	}

	public TblPuntoGPS getPuntoGPS() {
		return this.puntoGPS;
	}

	public void setMovMarcacion(E_MovMarcacion movMarcacion) {
		this.movMarcacion = movMarcacion;
	}

	public E_TBL_MOV_REGISTROVISITA getMovRegVisita() {
		return movRegVisita;
	}

	public void setMovRegVisita(E_TBL_MOV_REGISTROVISITA movRegVisita) {
		Log.i("setMovRegVisita", "" + movRegVisita);
		this.movRegVisita = movRegVisita;
	}

	public void setAccion(int accion) {
		this.accion = accion;
	}

	public void setAccion(int accion, Handler hdler) {
		this.hdler = hdler;
		this.accion = accion;
	}

	public int getActividad() {
		return actividad;
	}

	public void setActividad(int actividad) {
		this.actividad = actividad;
	}

	public int getAccion() {
		return accion;
	}

	public void respuestaEnvio(int cod, String msg) {
		Log.i("MarcacionLocatinHandler", "respuestaEnvio(int cod," + cod + "String msg " + msg + ")");
		if (esperandoRespuesta) {
			esperandoRespuesta = false;
			String msgResultado = "";
			Message ms = new Message();
			switch (cod) {
			case -2:
				Log.i("Recibiendo respuesta", "case -2");
				ms.arg1 = -2;
				msgResultado = "Error: La conexi�n a Internet es baja, por favor enviar su informaci�n por pendientes.";
				if (accion == ACCION_REGISTRAR_FINAL) {
					Log.i("Marcacion Location Handler", "Fallo envio de Fin de marcacion");
					movMarcacion.setEstado(MovMarcacionController.ESTADO_MARCACION_FIN_GUARDADO);
					MovMarcacionController movController = new MovMarcacionController(db);
					movController.edit(movMarcacion);
				}
				break;
			case -1:
				Log.i("Recibiendo respuesta", "case -1");
				ms.arg1 = -1;
				msgResultado = "Ocurri� un error con el servicio";
				if (accion == ACCION_REGISTRAR_FINAL) {
					Log.i("Marcacion Location Handler", "Fallo envio de Fin de marcacion");
					movMarcacion.setEstado(MovMarcacionController.ESTADO_MARCACION_FIN_GUARDADO);
					MovMarcacionController movController = new MovMarcacionController(db);
					movController.edit(movMarcacion);
				}
				break;
			case 0:
			case 1:
				Log.i("Marcacion Location Handler", "Recibiendo respuesta: case 0 o 1");
				if (accion == ACCION_REGISTRAR_INICIO || accion == ACCION_REGISTRAR_FINAL) {
					Log.i("Marcacion Location Handler", "Inicio o Fin de marcacion");
					movMarcacion.setEstado(movMarcacion.getEstado() + 1);
					MovMarcacionController movController = new MovMarcacionController(db);
					movController.edit(movMarcacion);
					msgResultado = msg == null || msg.trim().isEmpty() ? "Registro enviado con �xito" : msg;
					if (activity.getClass() == PendientesActivity.class) {
						ms.arg1 = 4;
					}

				} else if (accion == ACCION_REGISTRAR_INICIO_VISITA || accion == ACCION_REGISTRAR_FINAL_VISITA) {
					Log.i("Marcacion Location Handler", "Inicio o fin de visita"+" "+mensajeServer);
					movRegVisita.setEstado(movRegVisita.getEstado() + 1);
					MovRegistroVisitaController movRVController = new MovRegistroVisitaController(db);
					movRVController.edit(movRegVisita);
					msgResultado=(mensajeServer==null || mensajeServer.trim().isEmpty())? msg:mensajeServer;

				} else if (accion == ACCION_REGISTRAR_FINAL_MOTIVO_NOVISITA_BODEGA) {
					Log.i("Marcacion Location Handler", "motivo no visita bodega");
					
					if (movRegVisita != null){
						ms.arg1 = 1;
						enviarVisita(movRegVisita);
						accion=ACCION_REGISTRAR_FINAL_VISITA;
						mensajeServer=msg;
						Log.i("Marcacion Location Handler", "mensjes fijado"+mensajeServer);
					}
//					if (movRegVisita != null) {
//						movRegVisita.setEstado(movRegVisita.getEstado() + 1);
//						MovRegistroVisitaController movRVController = new MovRegistroVisitaController(db);
//						movRVController.edit(movRegVisita);
//						if (activity.getClass() == PendientesActivity.class) {
//							Message hmsg = new Message();
//							hmsg.arg1 = 1;
//							hdler.sendMessage(hmsg);
//						} else if (activity.getClass() == ListaDeReporte.class) {
//							Log.i("MarcacionLocationHandler", "Enviando");
//							hdler.sendEmptyMessage(0);
//						}
//					}
				}
				break;
			default:
				Log.i("Recibiendo respuesta", "case default");
				msgResultado = "Ocurri� un error en el envio";
				ms.arg1 = 3;
				if (accion == ACCION_REGISTRAR_FINAL) {
					Log.i("Marcacion Location Handler", "Fallo envio de Fin de marcacion");
					movMarcacion.setEstado(MovMarcacionController.ESTADO_MARCACION_FIN_GUARDADO);
					MovMarcacionController movController = new MovMarcacionController(db);
					movController.edit(movMarcacion);
				}
				break;
			}
			ms.obj = msgResultado;
			if (hdler != null) {
				hdler.sendMessage(ms);
			} else {
				Log.i("Marcacion Location Handler", "handler es null - enviando a PuntosVentaActivity");
				Log.i("Marcacion Location Handler", "Handler: " + activity.getClass());
				Intent nombre = new Intent(this.activity, PuntosVentaActivity.class);
				nombre.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				this.activity.startActivity(nombre);
			}
		} else {
			// TODO BUSCAR C�MO DETECTAR CUANDO LA ACTIVIDAD PRINCIPAL ES OTRA Y
			// COMO REENVIAR LA RESPUESTA DE ENV�O A DICHA ACTIVIDAD
		}
	}

	public void posicionActualizada(PeticionGPS peticion, TblPuntoGPS puntoGPS) {
		Log.i("MarcacionLocationHandler", " Peticion posicionActualizada. accion = " + accion + " Accion Peti" + peticion.getAccion() + " peticion " + peticion.getIdPunto());
		switch (peticion.getAccion()) {
		case ACCION_REGISTRAR_INICIO_VISITA:
		case ACCION_REGISTRAR_FINAL_VISITA:// _CON_NO_VISITA
			Log.i("MarcacionLocationHandler", "Fin Visita sin reportes");
			enviarVisita(movRegVisita);
			break;

		case ACCION_REGISTRAR_FINAL_VISITA_CON_REPORTES:
			Log.i("MarcacionLocationHandler", "Enviando 1");
			// if (activity.getClass() == ListaDeReporte.class) {
			if (hdler != null) {
				Log.i("MarcacionLocationHandler", "Enviando 2");
				Message mss = hdler.obtainMessage();
				mss.arg1 = 6;
				hdler.sendMessage(mss);
			} else {
				Intent intent = new Intent(activity, PuntosVentaActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				activity.startActivity(intent);
			}
			// }
			break;
		case ACCION_REGISTRAR_FINAL_MOTIVO_NOVISITA_BODEGA:
			guradarNoVisitaBodega();
			
			break;
	
		case ACCION_REGISTRAR_INICIO:
		case ACCION_REGISTRAR_FINAL:
			enviarMarcion(movMarcacion);
			break;
		case ACCION_REGISTRAR_NUEVO_PTO_VENTA:
			Log.i("MarcacionLocationHandler", "Enviando");
			if (hdler != null) {
				Message mss = hdler.obtainMessage();
				mss.arg1 = 1;
				hdler.sendMessage(mss);
			}
			break;
		default:
			break;
		}

	}

	public void crearRegistroVisita(E_TBL_MOV_REGISTROVISITA movRegVisita) {
		this.movRegVisita = movRegVisita;
		puntoGPS = GPSManager.getManager().getPuntoGPS(db, activity, true);
		int id = puntoGPS.getId();
		this.movRegVisita.setIdPuntoGPSInicio(id);
		this.movRegVisita.setEstado(1);
		MovRegistroVisitaController movRVController = new MovRegistroVisitaController(db);
		PuntoventaVo pv = DatosManager.getInstancia().getPuntoVentaSeleccionado();
		this.movRegVisita.setIdPuntoVenta(pv.getIdPtoVenta());
		this.movRegVisita.setId(movRVController.createR(movRegVisita));
		PeticionGPS peticion = new PeticionGPS(id, this);
		peticion.setAccion(ACCION_REGISTRAR_INICIO_VISITA);
		peticion.setIdVisita(movRegVisita.getId());
		GPSManager.getManager().actualizarPosicion(peticion, db, 10000, true);
		DatosManager.getInstancia().setVisita(this.movRegVisita);
		mostraResultadoGuardar("Inicio de visita registrado con exito!");
	}

	public void crearFinVisita(E_TBL_MOV_REGISTROVISITA movRegVisita) {
		this.movRegVisita = movRegVisita;
		puntoGPS = GPSManager.getManager().getPuntoGPS(db, activity, true);
		int id = puntoGPS.getId();
		this.movRegVisita.setIdPuntoGPSFin(id);
		this.movRegVisita.setEstado(MovRegistroVisitaController.ESTADO_VISITA_FIN_GUARDADO);
		Log.i("Resultado de edicion de visita", "" + movRVController.edit(movRegVisita));
		PeticionGPS peticion = new PeticionGPS(id, this);
		peticion.setAccion(accion);
		peticion.setIdVisita(movRegVisita.getId());
		GPSManager.getManager().actualizarPosicion(peticion, db, 10000, false);
		// espera a obtener
		// posici�n,
		// recibe
		// respuesta por
		// posici�n
		// actualizada
		mostraResultadoGuardar("Fin de visita registrado con exito!");
	}

	public void crearInicio(E_MovMarcacion movMarcacion) {
		puntoGPS = GPSManager.getManager().getPuntoGPS(db, activity, true);
		int id = puntoGPS.getId();
		this.movMarcacion = movMarcacion;
		this.movMarcacion.setIdPunto_inicio(id);
		this.movMarcacion.setEstado(1);
		movController.create(movMarcacion);
		PeticionGPS peticion = new PeticionGPS(id, this);
		peticion.setAccion(accion);
		peticion.setIdVisita(movMarcacion.getId());
		GPSManager.getManager().actualizarPosicion(peticion, db, 60000, true);
		mostraResultadoGuardar("Inicio guardado con exito!");
		DatosManager.getInstancia().setTerminarLabor(false);
	}

	public void crearFin(E_MovMarcacion movMarcacion) {
		puntoGPS = GPSManager.getManager().getPuntoGPS(db, activity, true);
		int id = puntoGPS.getId();
		this.movMarcacion = movMarcacion;
		this.movMarcacion.setIdUsuario(Integer.parseInt(DatosManager.getInstancia().getUsuario().getIdUsuario()));
		this.movMarcacion.setIdPunto_fin(id);
		this.movMarcacion.setEstado(MovMarcacionController.ESTADO_MARCACION_FIN_GUARDADO);
		movController.edit(movMarcacion);
		PeticionGPS peticion = new PeticionGPS(id, this);
		peticion.setAccion(accion);
		peticion.setIdVisita(movMarcacion.getId());
		GPSManager.getManager().actualizarPosicion(peticion, db, 10000, true);
		mostraResultadoGuardar("Fin guardado con exito!");
		DatosManager.getInstancia().setTerminarLabor(true);
	}

	public void mostraResultadoGuardar(String resultado) {
		Toast.makeText(activity, resultado, Toast.LENGTH_SHORT).show();
	}

	// ***********************************************************************************************************************
	// ***********************************************************************************************************************
	// ***********************************************************************************************************************
	// Joseph Gonzales
	// Lucky SAC

	public final static int ACCION_REGISTRAR_FINAL_MOTIVO_NOVISITA_BODEGA = 6;
	public final static int ACCION_REGISTRAR_NUEVO_PTO_VENTA = 7;

	private E_Tbl_Mov_RegistroBodega movRegNoVisitaBodega;

	public E_Tbl_Mov_RegistroBodega getMovRegNoVisitaBodega() {
		return movRegNoVisitaBodega;
	}

	public void setMovRegNoVisitaBodega(E_Tbl_Mov_RegistroBodega movRegNoVisitaBodega) {
		this.movRegNoVisitaBodega = movRegNoVisitaBodega;
	}

	public void guradarNoVisitaBodega() {
		esperandoRespuesta = true;
		conexion = Conexion.getInstance(this.activity);
		conexion.setComListener(this, this.activity);

		E_Usuario e_UsuarioMarcacion = DatosManager.getInstancia().getUsuario();

		E_Tbl_Mov_RegistroBodega noVisitaBodega = getMovRegNoVisitaBodega();
		TblPuntoGPS puntoGps = getPuntoGps(noVisitaBodega.getIdPuntoGPS());

		E_RegistrarMotivoColgateBodega_Request e_NoVisitaBodegaRequest = new E_RegistrarMotivoColgateBodega_Request(noVisitaBodega, e_UsuarioMarcacion, puntoGps);
		conexion.registrarNoVistaBodega(e_NoVisitaBodegaRequest);

	}

	public void crearNoVisitaBodega(E_Tbl_Mov_RegistroBodega movRegNoVisitaBodega) {
		// TODO Auto-generated method stub
		puntoGPS = GPSManager.getManager().getPuntoGPS(db, activity, true);
		int id = puntoGPS.getId();
		this.movRegNoVisitaBodega = movRegNoVisitaBodega;

		this.movRegNoVisitaBodega.setIdPuntoGPS(id);
		PuntoventaVo epvb = DatosManager.getInstancia().getPuntoVentaSeleccionado();
		this.movRegNoVisitaBodega.setIdPuntoVenta(epvb.getIdPtoVenta());
		this.movRegNoVisitaBodega.setIdUsuario(Integer.parseInt(DatosManager.getInstancia().getUsuario().getIdUsuario()));
		this.movRegNoVisitaBodega.setIdVisita(movRegVisita.getId());

		MovRegistroNoVisitaBodegaController movRVControllerBodega = new MovRegistroNoVisitaBodegaController(db);
		int posCab = movRVControllerBodega.createR(movRegNoVisitaBodega);

		for (E_Tbl_Mov_RegistroBodega_Detalle detalle : movRegNoVisitaBodega.getDetalle()) {
			detalle.setId_Cabecera(posCab);
			movRVControllerBodega.createDetalle(detalle);
		}
		PeticionGPS peticion = new PeticionGPS(id, this);
		peticion.setAccion(accion);
		// peticion.setIdVisita(movMarcacion.getId());
		// gpsManager.actualizarPosicion(id, db, activity, 5000);
		GPSManager.getManager().actualizarPosicion(peticion, db, 5000, false);

		if (DatosManager.getInstancia().getVisita() != null) {
			movRegVisita.setIdPuntoGPSFin(id);
			movRegVisita.setEstado(3);
			movRegVisita.setIdmotivoNoVisita(-1);
			movRVController.edit(movRegVisita);
			// guardarVisita(movRegVisita);
		}

		//mostraResultadoGuardar("Registrado realizado con exito!");
	}

	public void crearFinRegistro() {
		// TODO Auto-generated method stub
		// puntoGPS = gpsManager.getPuntoGPS(db, activity, 5000);
		// int id = puntoGPS.getId();
		// gpsManager.actualizarPosicion(id, db, 5000, 1);
		puntoGPS = GPSManager.getManager().getPuntoGPS(db, activity, true);
		int id = puntoGPS.getId();

		PeticionGPS peticion = new PeticionGPS(id, this);
		peticion.setAccion(accion);
		GPSManager.getManager().actualizarPosicion(peticion, db, 5000, false);
	}

	public ReportesColgateBodega_RegistrarPDV_Mov_Request setRegistrarNuevoPtoVenta(E_TblMovRegPDV regNuevoPtoVenta, ArrayList<E_Codigo_ITT_New> list_Codigo_ITT_Distribuidora) {
		// TODO Auto-generated method stub

		// puntoGPS = gpsManager.getPuntoGPS(db);
		puntoGPS = GPSManager.getManager().getPuntoGPS(db, activity, true);
		int id = puntoGPS.getId();

		E_Usuario e_UsuarioMarcacion = DatosManager.getInstancia().getUsuario();
		TblPuntoGPS puntoGps = getPuntoGps(id);
		regNuevoPtoVenta.setId_punto_gps(id);
		TblMovRegPDVController reg_pdv_controller = new TblMovRegPDVController(db);
		reg_pdv_controller.updateIdPDV(regNuevoPtoVenta, regNuevoPtoVenta.getId());
		// FALTA GUARDAR EL REPORTE DE CODIGOS ITT ANTES DE ENVIARLO

		ReportesColgateBodega_RegistrarPDV_Mov_Request request = new ReportesColgateBodega_RegistrarPDV_Mov_Request(regNuevoPtoVenta, list_Codigo_ITT_Distribuidora, puntoGps, e_UsuarioMarcacion);

		return request;
	}

}
