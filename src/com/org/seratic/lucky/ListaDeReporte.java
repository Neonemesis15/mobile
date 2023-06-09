package com.org.seratic.lucky;

import java.util.List;

import org.seratic.location.MarcacionLocationHandler;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.org.seratic.lucky.accessData.SQLiteDatabaseAdapter;
import com.org.seratic.lucky.accessData.control.E_tblMstReporteController;
import com.org.seratic.lucky.accessData.control.E_TblMovReporteCabController;
import com.org.seratic.lucky.accessData.control.E_tbl_mov_fotosController;
import com.org.seratic.lucky.accessData.control.E_tbl_mov_videosController;
import com.org.seratic.lucky.accessData.control.MovRegistroVisitaController;
import com.org.seratic.lucky.accessData.entities.E_MST_TBL_REPORTE;
import com.org.seratic.lucky.accessData.entities.E_TBL_MOV_REGISTROVISITA;
import com.org.seratic.lucky.accessData.entities.E_TblMovReporteCab;
import com.org.seratic.lucky.accessData.entities.E_Usuario;
import com.org.seratic.lucky.accessData.entities.E_tbl_mov_fotos;
import com.org.seratic.lucky.accessData.entities.E_tbl_mov_videos;
import com.org.seratic.lucky.accessData.entities.Entity;
import com.org.seratic.lucky.comunicacion.IComunicacionListener;
import com.org.seratic.lucky.manager.DatosManager;
import com.org.seratic.lucky.thread.ThreadFinLabor;
import com.org.seratic.lucky.vo.PuntoventaVo;

public class ListaDeReporte extends ListActivity implements IComunicacionListener {
	private SQLiteDatabase db;
	private ProgressDialog pd;
	List<Entity> e_mst_reporte;
	private E_tblMstReporteController e_tbl_reporteController;
	String[] aliasReporte;
	int[] idReporte;
	private MarcacionLocationHandler locationHandler;
	String puntoVenta;
	E_TBL_MOV_REGISTROVISITA entidad;
	private MovRegistroVisitaController rvController;
	private E_TblMovReporteCabController movRepCabController;
	private E_tbl_mov_fotosController movFotosController;
	private E_tbl_mov_videosController movVideosController;
	ProgressDialog indicadorProgreso;

	ThreadFinLabor tF;
	Thread t;
	private boolean	isLock	= true;
	private boolean	opFinalizarVisita	= true;

	public SharedPreferences preferences;

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Log.i("Lista de reportes", "handler. accion: " + msg.arg1);
			ActivityManager am = (ActivityManager) ListaDeReporte.this.getSystemService(ACTIVITY_SERVICE);
			List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
			Log.i("ListaDeReportes", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName());
			switch (msg.arg1) {
			case 3:
				
				// get the info from the currently running task
				
				if (taskInfo.get(0).topActivity.getClassName().equalsIgnoreCase(ListaDeReporte.class.getName())) {
					AlertDialog alertDialog = new AlertDialog.Builder(ListaDeReporte.this).create();
					alertDialog.setMessage("Mensaje :" + (String) msg.obj);
					alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

							if (DatosManager.CANAL_BODEGAS.equalsIgnoreCase(DatosManager.getInstancia().getUsuario().getCod_canal())) {
								String idFase = preferences.getString("codFase", "");
								if (idFase.equalsIgnoreCase("R")) {
									finish();
									Intent nombre = new Intent(ListaDeReporte.this, MenuBodegasActivity.class);
									nombre.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(nombre);
								} else {
									finish();
								}
							} else {
								Intent nombre = new Intent(ListaDeReporte.this, PuntosVentaActivity.class);
								nombre.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(nombre);
							}
						}
					});
					alertDialog.show();
				} else {
					
				
					Toast.makeText(getBaseContext(), "Mensaje :" + (String) msg.obj, Toast.LENGTH_LONG).show();
					
					if (DatosManager.CANAL_BODEGAS.equalsIgnoreCase(DatosManager.getInstancia().getUsuario().getCod_canal())) {
						String idFase = preferences.getString("codFase", "");
						if (idFase.equalsIgnoreCase("R")) {
							finish();
							Intent nombre = new Intent(ListaDeReporte.this, MenuBodegasActivity.class);
							nombre.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(nombre);
						} else {
							finish();
						}
					} else {
						Intent nombre = new Intent(ListaDeReporte.this, PuntosVentaActivity.class);
						nombre.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(nombre);
					}
					
				}
				// forzar el envio de las fotos
				new Thread() {
					public void run() {
						List<E_tbl_mov_fotos> fotos = movFotosController.isPendienteEnvio(E_tbl_mov_fotos.FOTO_GUARDADA_PARA_ENVIO);
						if (fotos != null && db != null) {
							Log.i("ListaDeReportes", "Fotos " + fotos.size());
							DatosManager.getInstancia().enviarFoto(fotos, db, ListaDeReporte.this);
						} else {
							Log.i("ListaDeREportes", "No hay Fotos por enviar ");
						}
						List<E_tbl_mov_videos> archivos = movVideosController.isPendienteEnvio(E_tbl_mov_videos.VIDEO_GUARDADO_PARA_ENVIO);
						if (archivos != null && db != null) {
							Log.i("ListaDeReportes", "Archivos " + archivos.size());
							DatosManager.getInstancia().enviarArchivo(archivos, db, ListaDeReporte.this);
						} else {
							Log.i("ListaDeREportes", "No hay archivos por enviar ");
						}
					}
				}.start();

				// Toast.makeText(ListaDeReporte.this, (String) msg.obj,
				// Toast.LENGTH_LONG).show();
				break;

			case 4:
				if (taskInfo.get(0).topActivity.getClassName().equalsIgnoreCase(ListaDeReporte.class.getName())) {
					AlertDialog alertDialog = new AlertDialog.Builder(ListaDeReporte.this).create();
					alertDialog.setMessage("Mensaje :" + (String) msg.obj);
					alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Intent nombre = new Intent(ListaDeReporte.this, PuntosVentaActivity.class);
							nombre.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(nombre);
						}
					});
					alertDialog.show();
				} else {
					Intent nombre = new Intent(ListaDeReporte.this, PuntosVentaActivity.class);
					nombre.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					Toast.makeText(ListaDeReporte.this, R.string.reportes_no_enviados, Toast.LENGTH_LONG).show();
					startActivity(nombre);
				}
				
				break;

			case 5:
				mostrarMensaje("Cargando reporte..");
				break;

			case 6:
				enviarReportes();
				break;

			default:
				Intent retorno = new Intent(ListaDeReporte.this, PuntosVentaActivity.class);
				retorno.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(retorno);
				break;
			}

		};
	};
	private SharedPreferences preferencesApp;
	private Intent	envio;
	private TextView	mensaje;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		Log.i("ListadoReportes", "onCreate");
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ly_reportes);
		opFinalizarVisita=true;
		// Intent in = getIntent();
		// Bundle b = in.getExtras();
		SQLiteDatabaseAdapter aSQLiteDatabaseAdapter = SQLiteDatabaseAdapter.getInstance(this);
		db = aSQLiteDatabaseAdapter.getWritableDatabase();
		if (DatosManager.getInstancia().getUsuario() == null) {
			DatosManager instanciaDM = (DatosManager) getLastNonConfigurationInstance();
			if (instanciaDM == null) {
				Log.i("Empresa", "Instancia recuperada Null");
				DatosManager.getInstancia().cargarDatos(db);
			} else {
				DatosManager.setInstancia(instanciaDM);
			}
		}
		DatosManager.getInstancia().inicializarControladores();
		preferences = getSharedPreferences("NoVisitaBodega", MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);

		PuntoventaVo pvo = DatosManager.getInstancia().getPuntoVentaSeleccionado();
		if (pvo != null) {
			puntoVenta = pvo.getRazonSocial();
		} else {
			Intent nombre = new Intent(ListaDeReporte.this, PuntosVentaActivity.class);
			nombre.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(nombre);
		}
		final TextView ptoVenta = (TextView) findViewById(R.id.textView1);
		ptoVenta.setText(puntoVenta);

		locationHandler = new MarcacionLocationHandler(db, this);
		e_tbl_reporteController = new E_tblMstReporteController(db);

		rvController = new MovRegistroVisitaController(db);
		movRepCabController = new E_TblMovReporteCabController(db);
		movFotosController = new E_tbl_mov_fotosController(db);
		movVideosController = new E_tbl_mov_videosController(db);

		e_mst_reporte = e_tbl_reporteController.getReportes();
		aliasReporte = new String[e_mst_reporte.size()];
		idReporte = new int[e_mst_reporte.size()];
		ListView lstOpciones = getListView();
		for (int i = 0; i < e_mst_reporte.size(); i++) {
			E_MST_TBL_REPORTE reporte = (E_MST_TBL_REPORTE) e_mst_reporte.get(i);
			aliasReporte[i] = reporte.getAlias();
			idReporte[i] = reporte.getId();
		}
		ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, aliasReporte);
		setListAdapter(adaptador);
		preferencesApp = getSharedPreferences("Navegacion", MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);

		lstOpciones.setOnItemClickListener(new OnItemClickListener() {
			// @Override
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				// Message msg = new Message();
				// msg.arg1 = 5;
				// handler.sendMessage(msg);
				int idR = idReporte[position];
				DatosManager.getInstancia().setIdReporte(idR);

				Editor edit = preferencesApp.edit();
				edit.putInt("idReporte", idR);
				edit.commit();
				switch (idR) {

				default:
					E_tblMstReporteController reporteController = new E_tblMstReporteController(db);
					List<Entity> reportes = reporteController.getById(idR);
					for (Entity reporte : reportes) {
						E_MST_TBL_REPORTE rp = (E_MST_TBL_REPORTE) reporte;
						String keyReportes = idR + "-" + rp.getIdSubreporte() + "-" + DatosManager.getInstancia().getUsuario().getCod_canal();
						SharedPreferences preferences = getSharedPreferences("Filtros" + keyReportes, MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
						Editor ed = preferences.edit();
						ed.clear();
						ed.commit();
						SharedPreferences prefNav = getSharedPreferences("Nav" + keyReportes, MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
						ed = prefNav.edit();
						ed.clear();
						ed.commit();
					}
					cargarReportes(idR);
					break;
				}
			}
		});

		int idRAPP = preferencesApp.getInt("idReporte", 0);
		if (idRAPP != 0) {
			cargarReportes(idRAPP);
		}
	}

	public void cargarReportes(int idR) {
		DatosManager.getInstancia().setIdReporte(idR);
		Intent reporteGeneral = new Intent(ListaDeReporte.this, ContenedorReportes.class);
		reporteGeneral.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		reporteGeneral.putExtra("idSubreporte", 0);
		startActivity(reporteGeneral);
	}

	public void verificarReinicio() {
		preferences = getSharedPreferences("ContenedorReporte", MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
		boolean reinicio = preferences.getBoolean("Reinicio", false);
		Log.i("Listado Reportes", "reinicio: " + reinicio);
		if (reinicio) {

			new Thread() {
				public void run() {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Intent reporteGeneral = new Intent(ListaDeReporte.this, ContenedorReportes.class);
					reporteGeneral.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					reporteGeneral.putExtra("idSubreporte", 0);
					startActivity(reporteGeneral);

				}

			}.start();
		}

	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		DatosManager datosManager = DatosManager.getInstancia();
		E_Usuario e_Usuario = datosManager.getUsuario();
		List<E_TblMovReporteCab> e_MovReportb = movRepCabController.getByIdUsuarioIdPuntoVentaIdVisita(e_Usuario.getIdUsuario(), DatosManager.getInstancia().getPuntoVentaSeleccionado().getIdPtoVenta(), E_TblMovReporteCab.ESTADO_GUARDADA, datosManager.getVisita().getId());

		if (e_MovReportb != null) {
			if (e_MovReportb.size() > 0) {
				MenuInflater inflater = getMenuInflater();
				inflater.inflate(R.menu.menu_nombre_puntodeventa, menu);
			}
		} else {
			if (!DatosManager.CANAL_BODEGAS.equalsIgnoreCase(DatosManager.getInstancia().getUsuario().getCod_canal())) {
				MenuInflater inflater = getMenuInflater();
				inflater.inflate(R.menu.menu_nombre_puntodeventanovisita, menu);
			}
		}

		return true;
	}

	

	public void MostrarAlert() {
		mostrarMotivoNoVisista();
	}

	public void mostrarMotivoNoVisista() {
		Intent intentr0 = new Intent(ListaDeReporte.this, MotivoNoVisita.class);
		intentr0.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intentr0);
	}

	public void mostrarAlerta2() {
		if(opFinalizarVisita){
		opFinalizarVisita=false;
		final AlertDialog alertDialog = new AlertDialog.Builder(ListaDeReporte.this).create();
		alertDialog.setTitle("Alerta");
		alertDialog.setMessage("�Desea registrar su fin de visita?");
		alertDialog.setButton("Si", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// mostrarMensaje("Enviando reportes.");
				try {
					
//				 envio = new Intent(ListaDeReporte.this, Prueba.class);
//				   envio.putExtra("mensaje", "Finalizando visita");
//					envio.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//					startActivity(envio);
				
					isLock=true;
					Log.i("ListaReporte MA2","onCreate "+isLock);
					setContentView(R.layout.ly_envio_reportes);
					mensaje=(TextView) findViewById(R.id.mensajeEnvio);
					mensaje.setText("Finalizando visita");	
					mensaje.invalidate();
					
						//indicadorProgreso = ProgressDialog.show(ListaDeReporte.this, "", "Finalizando visita", true);
				} catch (Exception ex) {
					Log.i("Lista de Reportes: ", "error mostrando mensaje: " + ex);
				}
				locationHandler.setMovRegVisita(DatosManager.getInstancia().getVisita());
				locationHandler.setAccion(MarcacionLocationHandler.ACCION_REGISTRAR_FINAL_VISITA_CON_REPORTES, handler);
				locationHandler.crearFinVisita(DatosManager.getInstancia().getVisita());

			}
		});
		alertDialog.setButton2("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// alertDialog.hide();
			}
		});
		alertDialog.show();
		}
	}

	public void showProgressDialog() {
		DialogInterface.OnCancelListener dialogCancel = new DialogInterface.OnCancelListener() {

			public void onCancel(DialogInterface dialog) {
				Toast.makeText(getBaseContext(), "Se�al GPS no encontrada", Toast.LENGTH_LONG).show();
			}
		};
		pd = ProgressDialog.show(this, "Guardando...", "Guardando Motivo de fin de visita", true, true, dialogCancel);
	}

	public void actualizarLocalizacion() {
		Toast.makeText(getBaseContext(), "Fin de visita guardado exitosamente", Toast.LENGTH_LONG).show();

	}

	@Override
	public void onBackPressed() {

		try {
			if (indicadorProgreso != null) {
				indicadorProgreso.dismiss();
			}
		} catch (Exception ex) {
			Log.i("Lista de reportes: ", "Error en el dismiss del onBackPressed: " + ex);
		}
		if (DatosManager.getInstancia().getPuntoVentaSeleccionado() == null) {
			Log.e("OJO", "El punto de venta seleccionado est� en null");
		} else {
			String idPV = DatosManager.getInstancia().getPuntoVentaSeleccionado().getIdPtoVenta();
			if (rvController.isRegistroVisitaSinFinalizar(idPV)) {
				DatosManager datosManager = DatosManager.getInstancia();
				E_Usuario e_Usuario = datosManager.getUsuario();
				List<E_TblMovReporteCab> e_MovReportb = movRepCabController.getByIdUsuarioIdPuntoVentaIdVisita(e_Usuario.getIdUsuario(), DatosManager.getInstancia().getPuntoVentaSeleccionado().getIdPtoVenta(), E_TblMovReporteCab.ESTADO_GUARDADA, datosManager.getVisita().getId());

				if (e_MovReportb != null) {
					if (e_MovReportb.size() > 0) {
						Toast.makeText(this, "Debe finalizar la visita", Toast.LENGTH_SHORT).show();
					}
				} else {
					// Validacion para el Canal Bodegas
					// Joseph Gonzales - Lucky SAC
					if (DatosManager.CANAL_BODEGAS.equalsIgnoreCase(DatosManager.getInstancia().getUsuario().getCod_canal())) {
						Toast.makeText(this, "Debe registrar los datos y finalizar la visita", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(this, "No se ha ingresado informaci�n, debe registrar un motivo de no visita.", Toast.LENGTH_SHORT).show();
					}
				}
			} else {
				super.onBackPressed();
			}
		}
	}

	public void mostrarMensaje(String msg) {
	}

	public void enviarReportes() {
		Log.i("ListaDeReporte", "...enviarReporte");
		try {
//			if (indicadorProgreso != null) {
//				indicadorProgreso.dismiss();
//			}
//			indicadorProgreso = ProgressDialog.show(ListaDeReporte.this, "", "Enviando reportes", true);
		
		//	requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.ly_enviando_reportes);
			
			
		} catch (Exception ex) {
			Log.i("Lista de Reportes: ", "error mostrando mensaje: " + ex);
		}
		DatosManager.getInstancia().setVisita_envio(DatosManager.getInstancia().getVisita());
		String msg = DatosManager.getInstancia().fijarDatosEnv�o(db, ListaDeReporte.this, E_TblMovReporteCab.ESTADO_GUARDADA);
		if (msg != null) {
			Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
			finish();
		} else {
			msg = DatosManager.getInstancia().enviarReportes(this);
			if (msg != null) {
				Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	public void respuestaEnvio(int cod, String msg) {
		Log.i("ListaDeReporte", "respuestaEnvio(int cod," + cod + "String msg " + msg + ") LR");
		Message ms = new Message();
		
		try {
			if (indicadorProgreso != null) {
				indicadorProgreso.dismiss();
			}
		} catch (Exception ex) {
			Log.i("Lista de reportes: ", "Error en el dismiss de respuesta envio: " + ex);
		}
		
		switch (cod) {
		case -2:
			ms.arg1 = 4;
			ms.obj = "Ocurri� un error: Revise su conexi�n a internet";
			DatosManager.getInstancia().dejarPendienteEnvio();
			// handler.sendMessage(ms);
			break;
		case -1:
			ms = new Message();
			ms.arg1 = 4;
			ms.obj = "Ocurri� un error en el servicio al enviar el reporte: " + msg;
			DatosManager.getInstancia().dejarPendienteEnvio();
			// System.out.println("Ocurri� un error en el servicio al enviar el reporte: "
			// + msg);
				// handler.sendMessage(ms);
			break;
		case 0:
		case 1:
			DatosManager datosManager = DatosManager.getInstancia();
			E_TBL_MOV_REGISTROVISITA visita = datosManager.getVisita();
			if (visita != null) {
				visita.setEstado(MovRegistroVisitaController.ESTADO_VISITA_FIN_ENVIADO);
				MovRegistroVisitaController movController = new MovRegistroVisitaController(db);
				movController.edit(visita);
				E_TblMovReporteCabController e_TblMovReporteCabController = new E_TblMovReporteCabController(db);
				e_TblMovReporteCabController.updateEstadoCabeceraByUsuarioVisita(E_TblMovReporteCab.ESTADO_ENVIADA, datosManager.getUsuario().getIdUsuario(), visita.getId());
			}
			
			ms = new Message();
			ms.arg1 = 3;
			String msgResultado = msg == null || msg.trim().isEmpty() ? "Reportes enviados con �xito" : msg;
			ms.obj = msgResultado;
			// movRepCabController.eliminarReportesEnviados();
			/*
			 * if (handler != null) { handler.sendMessage(ms); } else {
			 * Log.i("Lista de Reportes",
			 * "handler es null - enviando a PuntosVentaActivity"); Intent
			 * nombre = new Intent(ListaDeReporte.this,
			 * PuntosVentaActivity.class);
			 * nombre.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			 * startActivity(nombre); }
			 */
			break;

		default:
			ms = new Message();
			ms.arg1 = 4;
			ms.obj = "Ocurri� un error al enviar el reporte";
			DatosManager.getInstancia().dejarPendienteEnvio();
				// handler.sendMessage(ms);
			break;
		}

		if (handler != null) {
			Log.i("ListaDeReporte", "Handler en clase: " + handler.getClass().toString());
			handler.sendMessage(ms);
		} else {
			Log.i("Lista de Reportes", "handler es null - enviando a PuntosVentaActivity.  ms.arg1 = " + ms.arg1);
			/*
			 * Intent nombre = new Intent(ListaDeReporte.this,
			 * PuntosVentaActivity.class);
			 * nombre.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			 * startActivity(nombre);
			 */

			switch (ms.arg1) {
			case 3:
				AlertDialog alertDialog = new AlertDialog.Builder(ListaDeReporte.this).create();
				alertDialog.setMessage("Mensaje :" + (String) ms.obj);
				alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						if (DatosManager.CANAL_BODEGAS.equalsIgnoreCase(DatosManager.getInstancia().getUsuario().getCod_canal())) {
							String idFase = preferences.getString("codFase", "");
							if (idFase.equalsIgnoreCase("R")) {
								finish();
								Intent nombre = new Intent(ListaDeReporte.this, MenuBodegasActivity.class);
								nombre.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(nombre);
							} else {
								finish();
							}
						} else {
							Intent nombre = new Intent(ListaDeReporte.this, PuntosVentaActivity.class);
							nombre.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(nombre);
						}
					}
				});
				alertDialog.show();

				// forzar el envio de las fotos
				new Thread() {
					public void run() {
						List<E_tbl_mov_fotos> fotos = movFotosController.isPendienteEnvio(E_tbl_mov_fotos.FOTO_GUARDADA_PARA_ENVIO);
						if (fotos != null && db != null) {
							Log.i("ListaDeReportes", "Fotos " + fotos.size());
							DatosManager.getInstancia().enviarFoto(fotos, db, ListaDeReporte.this);
						} else {
							Log.i("ListaDeREportes", "No hay Fotos por enviar ");
						}
						List<E_tbl_mov_videos> archivos = movVideosController.isPendienteEnvio(E_tbl_mov_videos.VIDEO_GUARDADO_PARA_ENVIO);
						if (archivos != null && db != null) {
							Log.i("ListaDeReportes", "Videos " + archivos.size());
							DatosManager.getInstancia().enviarArchivo(archivos, db, ListaDeReporte.this);
						} else {
							Log.i("ListaDeREportes", "No hay Archivos por enviar ");
						}
					}
				}.start();
				break;

			case 4:
				Intent nombre = new Intent(ListaDeReporte.this, PuntosVentaActivity.class);
				nombre.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				Toast.makeText(ListaDeReporte.this, R.string.reportes_no_enviados, Toast.LENGTH_LONG).show();
				startActivity(nombre);
				break;

			case 5:
				mostrarMensaje("Cargando reporte..");
				break;

			case 6:
				enviarReportes();
				break;

			default:
				Intent retorno = new Intent(ListaDeReporte.this, PuntosVentaActivity.class);
				retorno.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(retorno);
				break;
			}
		}

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		DatosManager datosManager = DatosManager.getInstancia();
		E_Usuario e_Usuario = datosManager.getUsuario();
		List<E_TblMovReporteCab> e_MovReportb = movRepCabController.getByIdUsuarioIdPuntoVentaIdVisita(e_Usuario.getIdUsuario(), DatosManager.getInstancia().getPuntoVentaSeleccionado().getIdPtoVenta(), E_TblMovReporteCab.ESTADO_GUARDADA, datosManager.getVisita().getId());
		if(opFinalizarVisita){
		if (e_MovReportb != null) {
			if (e_MovReportb.size() > 0) {
				MenuInflater inflater = getMenuInflater();
				inflater.inflate(R.menu.menu_nombre_puntodeventa, menu);
			}
		} else {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.menu_nombre_puntodeventanovisita, menu);
		}

		return true;
		}else{
			return false;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// *****CASO VISITA***********
		case R.id.NoVisita:
			MostrarAlert();
			return true;
			// ******CASO REPORTE**************
		case R.id.FinVisita:
			
			mostrarAlerta2();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onStart() {
		Log.i("ListaDeReportes", "LR onStart()");
		try {
			if (indicadorProgreso != null)
				indicadorProgreso.dismiss();
		} catch (Exception ex) {
			Log.i("Lista de reportes: ", "Error en el dismiss del onStart: " + ex);
		}

		super.onStart();
		verificarReinicio();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {

		if ((event.getKeyCode() == KeyEvent.KEYCODE_HOME) && isLock) {

			return true;
		} else
			return super.dispatchKeyEvent(event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		if ((keyCode == KeyEvent.KEYCODE_BACK) && isLock) {
			// mTextView.setText("KEYCODE_BACK");
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onAttachedToWindow() {
		Log.i("ListaReporte","onCreate "+isLock);
		if (isLock) {
			this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
			super.onAttachedToWindow();
		} else {
			this.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION);
			super.onAttachedToWindow();
		}
	}
}