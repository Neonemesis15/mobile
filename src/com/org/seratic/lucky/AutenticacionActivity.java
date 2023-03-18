package com.org.seratic.lucky;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.org.seratic.lucky.accessData.SQLiteDatabaseAdapter;
import com.org.seratic.lucky.accessData.control.E_TblMovReporteCabController;
import com.org.seratic.lucky.accessData.control.E_UsuarioController;
import com.org.seratic.lucky.accessData.control.MovMarcacionController;
import com.org.seratic.lucky.accessData.control.MovRegistroVisitaController;
import com.org.seratic.lucky.comunicacion.HttpConnector;
import com.org.seratic.lucky.comunicacion.JsonParser;
import com.org.seratic.lucky.manager.CustomTextWatcher;
import com.org.seratic.lucky.manager.DatosManager;
import com.org.seratic.lucky.model.E_Usuario;
import com.org.seratic.lucky.model.E_UsuarioRequest;
import com.org.seratic.lucky.thread.ControlTimeOut;
import com.org.seratic.lucky.thread.ThreadCom;

public class AutenticacionActivity extends Activity implements ControlTimeOut {

	JsonParser j = new JsonParser(this);
	E_UsuarioRequest eUR = new E_UsuarioRequest();
	E_Usuario eU = new E_Usuario();
	EditText lblUsr;
	EditText lblPass;
	CharSequence textToast;
	// private TextView version;
	E_UsuarioController eUController;

	int durationToast = Toast.LENGTH_SHORT;
	// PRUEBA BASE DATOS
	// private static final String DATABASE_NAME = "LuckyDataBase.db";
	ProgressDialog pDialog;
	// BASE DATOS
	private SQLiteDatabase db;

	public static final int ERROR = 0;
	public static final int LOGUEO_CORRECTO = 1;
	static final int INCIO_LABOR_MARCACION_OK = 2;
	static final int INICIO_LABOR_MARCACION_ERROR = 3;
	static final int FIN_LABOR_MARCACION_OK = 4;
	static final int FIN_LABOR_MARCACION_ERROR = 5;
	MovMarcacionController marcacion;
	MovRegistroVisitaController rVController;
	private E_TblMovReporteCabController movReporteController;
	private SharedPreferences preferences;

	public Handler hand = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOGUEO_CORRECTO:
				procesarLogin((com.org.seratic.lucky.accessData.entities.E_Usuario) msg.obj);
				break;
			case ERROR:
				procesarError();
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ly_autenticacion);

		// version = (TextView) findViewById(R.id.version);
		SQLiteDatabaseAdapter aSQLiteDatabaseAdapter = SQLiteDatabaseAdapter.getInstance(this);
		db = aSQLiteDatabaseAdapter.getWritableDatabase();

		eUController = new E_UsuarioController(db, this);
		marcacion = new MovMarcacionController(db);
		rVController = new MovRegistroVisitaController(db);
		movReporteController = new E_TblMovReporteCabController(db);
		preferences = getSharedPreferences("Autenticacion", MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);

		final Button botonIngresar = (Button) findViewById(R.id.botoningresar);

		lblUsr = (EditText) findViewById(R.id.edit_usuario);
		lblPass = (EditText) findViewById(R.id.edit_password);

		lblUsr.setFilters(new InputFilter[] { new CustomTextWatcher(lblUsr) });
		botonIngresar.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (lblUsr.getText().toString().equals("") || lblPass.getText().toString().equals("")) {
					Toast.makeText(AutenticacionActivity.this, "Ingrese todos los datos", Toast.LENGTH_SHORT).show();
				}
				else {
					if (HttpConnector.isNetworkAvailable(AutenticacionActivity.this)) {
						eUR.setU(DatosManager.getInstancia().validarCaracteresEspeciales(lblUsr.getText().toString()));
						eUR.setP(DatosManager.getInstancia().validarCaracteresEspeciales(lblPass.getText().toString()));
						j.createJSON(eUR);

						SharedPreferences preferencesURL = getSharedPreferences("Config", Context.MODE_WORLD_READABLE);
						ThreadCom tC = new ThreadCom(AutenticacionActivity.this, j, "http://" + preferencesURL.getString("URL", DatosManager.DEFAULT_URL) + "/SeguridadService.svc/Login_Mov");
						Thread t = new Thread(tC);
						pDialog = ProgressDialog.show(AutenticacionActivity.this, null, "Autenticando usuario...", false, false);
						
						t.start();
					} else {
						Toast.makeText(AutenticacionActivity.this, "Internet no disponible, se va a realizar logueo local", Toast.LENGTH_LONG).show();
						com.org.seratic.lucky.accessData.entities.E_Usuario user = null;
						if ((user = eUController.getUsuarioByLoginPass(lblUsr.getText().toString(), lblPass.getText().toString())) != null) {
							DatosManager.getInstancia().setUsuarioLogeado(user);
							SharedPreferences.Editor ed = preferences.edit();
							ed.putString("login", lblUsr.getText().toString());
							ed.putString("password", lblPass.getText().toString());
							ed.commit();
							Intent intent = new Intent(AutenticacionActivity.this, MainMenu.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							intent.putExtra("nombre", user.getNombre());
							startActivity(intent);
							finish();
						} else {
							Toast toastUsrInvalido = Toast.makeText(AutenticacionActivity.this, "Datos locales no v�lidos ", durationToast);
							toastUsrInvalido.show();
						}
					}
				}
			}
		});
	}

	@Override
	public void onRestart() {
		super.onRestart();

	}

	public void procesarLogin(com.org.seratic.lucky.accessData.entities.E_Usuario usr) {
		pDialog.dismiss();
		boolean autenticar = false;
		if (usr.isUsrValido()) {
			if ((eUController.getUsuarioByLoginPass(lblUsr.getText().toString().toUpperCase(), lblPass.getText().toString().toUpperCase())) != null) {
				autenticar = true;
			} else {
				com.org.seratic.lucky.accessData.entities.E_Usuario userExistente = eUController.getUltimoUsuario();
				if (userExistente != null) {
					if (verificarPendientes(Integer.parseInt(userExistente.getIdUsuario()))) {
						Toast.makeText(AutenticacionActivity.this, "No puede ingresar hasta que el usuario " + userExistente.getNombre() + " envie los pendientes", Toast.LENGTH_LONG).show();
					} else {
						db.delete("TBL_ESTADOS", null, null);
						autenticar = true;
					}
				} else {
					Log.i("Autenticacion", "Usuario no existe en bd");
					autenticar = true;
				}
			}
			if (autenticar) {
				DatosManager.getInstancia().setUsuarioLogeado(usr);
				eUController.crearUsuario(usr, lblUsr.getText().toString().toUpperCase(), lblPass.getText().toString().toUpperCase());
				SharedPreferences.Editor ed = preferences.edit();
				ed.putString("login", lblUsr.getText().toString().toUpperCase());
				ed.putString("password", lblPass.getText().toString().toUpperCase());
				ed.commit();
				Intent intent = new Intent(AutenticacionActivity.this, MainMenu.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("nombre", usr.getNombre().toUpperCase());
				startActivity(intent);
				finish();
			}
		}
		else {
			textToast = usr.getMsgUsuario();
			Toast toastUsrInvalido = Toast.makeText(getApplicationContext(), textToast, durationToast);
			toastUsrInvalido.show();
		}
	}

	public boolean verificarPendientes(int idUsuario) {
		boolean hayPendMarc;
		boolean hayPendVisita;
		boolean hayPendReportes;

		hayPendMarc = marcacion.hayPendientesMovMarcacion(idUsuario);
		hayPendVisita = rVController.hayVisitasPendientes(idUsuario);
		hayPendReportes = movReporteController.isReportesPendientes(idUsuario);
		startManagingCursor(marcacion.dbCursor);
		startManagingCursor(rVController.dbCursor);
		startManagingCursor(movReporteController.dbCursor);
		if (hayPendMarc || hayPendVisita || hayPendReportes) {
			return true;
		}
		return false;
	}

	public void procesarError() {
		if (pDialog.isShowing() && pDialog != null) {
			pDialog.dismiss();
			Toast.makeText(AutenticacionActivity.this, "Internet no disponible, se va a realizar logueo local", Toast.LENGTH_LONG).show();
			com.org.seratic.lucky.accessData.entities.E_Usuario user = null;
			if ((user = eUController.getUsuarioByLoginPass(lblUsr.getText().toString().toUpperCase(), lblPass.getText().toString().toUpperCase())) != null) {
				DatosManager.getInstancia().setUsuarioLogeado(user);
				SharedPreferences.Editor ed = preferences.edit();
				ed.putString("login", lblUsr.getText().toString().toUpperCase());
				ed.putString("password", lblPass.getText().toString().toUpperCase());
				ed.commit();
				Intent intent = new Intent(AutenticacionActivity.this, MainMenu.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("nombre", user.getNombre().toUpperCase());
				startActivity(intent);
				finish();
			} else {
				textToast = E_Usuario.getInstancia().getD();
				String msg = "Datos locales no v�lidos";
				if(textToast!=null && !textToast.toString().trim().isEmpty()){
					msg = textToast.toString();
				}
				Toast.makeText(AutenticacionActivity.this, msg, durationToast).show();
			}
		}
	}

	public Handler getHandler() {
		// TODO Auto-generated method stub
		return hand;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Alternativa
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_sincronizarpredatos, menu);
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try {

			Intent intent = new Intent(AutenticacionActivity.this, ConfiguracionActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);

		} catch (Exception ex) {
			Log.e("AutenticacionActiity", ex.getMessage());
		}
		return true;
	}

}
