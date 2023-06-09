package com.org.seratic.lucky.thread;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Message;
import android.util.Log;

import com.org.seratic.lucky.ListaDeReporte;
import com.org.seratic.lucky.MainMenu;
import com.org.seratic.lucky.accessData.control.E_tbl_mov_fotosController;
import com.org.seratic.lucky.accessData.control.E_tbl_mov_videosController;
import com.org.seratic.lucky.accessData.control.MovRegistroVisitaController;
import com.org.seratic.lucky.accessData.entities.E_TBL_MOV_REGISTROVISITA;
import com.org.seratic.lucky.accessData.entities.E_tbl_mov_fotos;
import com.org.seratic.lucky.accessData.entities.E_tbl_mov_videos;
import com.org.seratic.lucky.comunicacion.ReportesService;

public class ThreadFinLabor implements Runnable {

	private static final String TAG = ThreadFinLabor.class.getSimpleName();
	MainMenu mainMenuActivity;
	ListaDeReporte listaReporteActivity;
	boolean terminarLabor = true;
	SQLiteDatabase db;
	Context context;
	E_tbl_mov_fotosController movFotosController;
	E_tbl_mov_videosController movVideosController;

	boolean corriendo = false;
	int TYPE = 0;
	final int TYPE_DESDE_REPORTE = 3;

	public ThreadFinLabor(MainMenu mM, SQLiteDatabase db, Context context) {
		TYPE = 0;
		this.mainMenuActivity = mM;
		this.db = db;
		this.context = context;
	}

	public ThreadFinLabor(ListaDeReporte mM, SQLiteDatabase db, Context context) {
		TYPE = TYPE_DESDE_REPORTE;
		this.listaReporteActivity = mM;
		this.db = db;
		this.context = context;
	}

	
	public void run() {
		Log.i(TAG, "Iniciando Hilo de env�o de Fotos");
		movFotosController = new E_tbl_mov_fotosController(db);
		movVideosController = new E_tbl_mov_videosController(db);
		try {
			while (terminarLabor) {

				corriendo = true;
				Log.i(TAG, "Ejecutando Hilo de env�o de Fotos");
				Date fechaActual = new Date();
				// System.out.println("fechaActual.getHours()): "+fechaActual.getHours());
				// if (fechaActual.getHours() >= 18) {
				// terminarLabor = false;
				// System.out.println("Mas de las 6 pm: ");
				// DatosManager.getInstancia().setTerminarLabor(true);
				//
				// Message msj = mainMenuActivity.handler.obtainMessage();
				// msj.what = MainMenu.TERMINAR_LABOR;
				// mainMenuActivity.handler.sendMessage(msj);
				// }
				List<E_tbl_mov_fotos> fotos = movFotosController.isPendienteEnvio(E_tbl_mov_fotos.FOTO_GUARDADA_PARA_ENVIO);
				if (fotos != null) {
					Log.i(TAG, "Enviando Fotos Pendientes");
					// if(TYPE == TYPE_DESDE_REPORTE)
					// {
					// Message msj =
					// listaReporteActivity.handlerFoto.obtainMessage();
					// msj.what = MainMenu.ENVIAR_FOTOS;
					// msj.obj=fotos;
					// listaReporteActivity.handlerFoto.sendMessage(msj);
					// }
					// else
					// {
					Message msj = mainMenuActivity.handler.obtainMessage();
					msj.what = MainMenu.ENVIAR_FOTOS;
					msj.obj = fotos;
					mainMenuActivity.handler.sendMessage(msj);
					// }
				} else {
					Log.i(TAG, "** No hay fotos pendientes");
					Message msj = mainMenuActivity.handler.obtainMessage();
					msj.what = MainMenu.ENVIAR_PENDIENTES_ENVIANDO;
					mainMenuActivity.handler.sendMessage(msj);
				}
				
				List<E_tbl_mov_videos> archivos = movVideosController.isPendienteEnvio(E_tbl_mov_videos.VIDEO_GUARDADO_PARA_ENVIO);
				if (archivos != null) {
					Log.i(TAG, "Enviando archivos Pendientes");
					Message msj = mainMenuActivity.handler.obtainMessage();
					msj.what = MainMenu.ENVIAR_ARCHIVOS;
					msj.obj = archivos;
					mainMenuActivity.handler.sendMessage(msj);
					// }
				} else {
					Log.i(TAG, "** No hay archivos pendientes");
					Message msj = mainMenuActivity.handler.obtainMessage();
					msj.what = MainMenu.ENVIAR_PENDIENTES_ENVIANDO;
					mainMenuActivity.handler.sendMessage(msj);
				}
				Thread.sleep(120000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isCorriendo() {
		return corriendo;
	}
}
