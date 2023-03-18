package com.org.seratic.lucky;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.org.seratic.lucky.accessData.SQLiteDatabaseAdapter;
import com.org.seratic.lucky.accessData.control.E_tbl_mov_fotosController;
import com.org.seratic.lucky.accessData.entities.E_tbl_mov_fotos;
import com.org.seratic.lucky.manager.CustomTextWatcher;
import com.org.seratic.lucky.manager.DatosManager;

public class ReporteFotoIncidencia extends Activity {
	/** Called when the activity is first created. */
	private static int REQUEST_TOMARFOTO = 1;
	private Intent intent;
	private ImageView iv;
	private int idCabecera = 0;
	private SQLiteDatabase db;
	private Button btn;
	private EditText comentario;
	private Bitmap mImageBitmap;
	private boolean tomoFoto;
	private TextView tituloReporte;
	private int idFoto, index;
	private String codigoElemento;
	private String tipoSubreporte;
	private SharedPreferences preferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i("", "onCreate(Reporte FotoInicidencia)");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ly_comentario_photo);

		Bundle extras = getIntent().getExtras();
		preferences = getSharedPreferences("ReporteFotoIncidencia", MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
		if (extras != null) {
			// idReporte = DatosManager.getInstancia().getIdReporte();
			idCabecera = extras.getInt("idCabecera");
			codigoElemento = extras.getString("codigoElemento");
			index = extras.getInt("index");
			tipoSubreporte = extras.getString("subreporte");
			Log.i("Reporte Foto Incidencia", "Extras: - idCabecera: " + idCabecera + " - codigoElemento: " + codigoElemento + " - tipoSubreporte: " + tipoSubreporte);
			tituloReporte = (TextView) findViewById(R.id.txtReporte);
			tituloReporte.setText(tipoSubreporte);

			SQLiteDatabaseAdapter aSQLiteDatabaseAdapter = SQLiteDatabaseAdapter.getInstance(this);
			db = aSQLiteDatabaseAdapter.getWritableDatabase();
			if (DatosManager.getInstancia().getUsuario() == null) {
				DatosManager instanciaDM = (DatosManager) getLastNonConfigurationInstance();
				if (instanciaDM == null) {
					Log.i("ReporteFotoIncidencia", "Instancia recuperada Null");
					DatosManager.getInstancia().cargarDatos(db);
					SharedPreferences prefContenedor = getSharedPreferences("ContenedorReporte", MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
					int idReporte = prefContenedor.getInt("idReporte", 0);
					DatosManager.getInstancia().setIdReporte(idReporte);
				} else {
					DatosManager.setInstancia(instanciaDM);
				}
			} else {
				tomoFoto = false;
			}

			DatosManager.getInstancia().setFotoTomada(false);
			btn = (Button) findViewById(R.id.guardarFoto);
			btn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					confirmar();
				}
			});
			comentario = (EditText) findViewById(R.id.editTextcomentVisi);
			comentario.setFilters(new InputFilter[] { new CustomTextWatcher(comentario) });

			iv = (ImageView) findViewById(R.id.imagenFoto);
		}else{			
			idCabecera = preferences.getInt("idCabecera", 0);
			codigoElemento = preferences.getString("codigoElemento", null);
			tipoSubreporte = preferences.getString("subreporte", null);
			Log.i("Reporte Foto Incidencia", "Preferences: - idCabecera: " + idCabecera + " - codigoElemento: " + codigoElemento + " - tipoSubreporte: " + tipoSubreporte);
			setRetorno();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		// super.onActivityResult(requestCode, resultCode, data);
		Log.i("ResultadoFoto", "-" + resultCode + "-" + requestCode);
		comentario.setText(preferences.getString("comentario", ""));
		Editor editFoto = preferences.edit();
		editFoto.clear();
		editFoto.commit();
		if (resultCode == RESULT_OK) {
			// mostrarMensaje(getString(R.string.reporte_fot_cargandoFoto));
			Bundle extras = data.getExtras();
			mImageBitmap = (Bitmap) extras.get("data");
			fijarFoto("", mImageBitmap);
			eliminarGaleria(data.getData());
		}
	}

	private void fijarFoto(String arch, Bitmap mImageBitmap) {
		try {
			if (mImageBitmap != null) {
				tomoFoto = true;
				iv.setImageBitmap(mImageBitmap);
			}
		} catch (Exception e) {
			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
		}
	}

	private void eliminarGaleria(Uri uri_foto){
		if(uri_foto!=null){
			int numDeleted = getContentResolver().delete(uri_foto, null, null);
			Log.i("Reporte fotografico", "Eliminando uri: " + uri_foto.toString() + " con registros eliminados = " + numDeleted);
		}else{
			Log.i("Reporte fotografico", "No se recupero la uri de la foto");
		}
	}
	
	
	public void guardar(int idCabeceraGuardada) {

		btn.setEnabled(false);

		SQLiteDatabaseAdapter aSQLiteDatabaseAdapter = SQLiteDatabaseAdapter.getInstance(this);
		db = aSQLiteDatabaseAdapter.getWritableDatabase();
		Log.i("*", "Guardando Reporte fotoSOD con Idcabecera " + idCabecera);
		byte[] byteArray = null;

		if (mImageBitmap != null) {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			Log.i("tam foto", "ancho" + (mImageBitmap.getWidth()) + "alto "+mImageBitmap.getHeight());
			mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
			byteArray = stream.toByteArray();
			Log.i("Reporte Foto Incidencia", "*** tama�o foto" + ((byteArray.length) / 1024) + "K byte");
		}

		E_tbl_mov_fotos mov_fotos = new E_tbl_mov_fotos(DatosManager.getInstancia().crearNombreFoto(), E_tbl_mov_fotos.FOTO_TEMPORAL, idCabecera, byteArray);
		idFoto = new E_tbl_mov_fotosController(db).createR(mov_fotos);

		Log.i("ReportesFotoInciadencia", "foto Guardado" + idFoto);
		intent = null;
		Editor ed = preferences.edit();
		ed.putInt("resultCode", RESULT_OK);
		ed.putInt("idCabecera", idCabecera);
		ed.putString("codigoElemento", codigoElemento);
		ed.putString("tipoSubreporte", tipoSubreporte);
		ed.putInt("index", index);
		ed.putString("comentario", comentario.getText().toString());
		// ed.putBoolean("reinicio", true);
		ed.putInt("idFoto", idFoto);
		ed.commit();
		setRetorno();
		iv = null;
	}

	public void mostrarMensaje(String msg) {
		// dialog=ProgressDialog.show(ReporteFotografico.this, "", msg, true);
	}

	public String validarDatos() {
		String msg = "";
		String coment = comentario.getText().toString();
		if (!tomoFoto) {
			msg += "Debe tomar una foto para agregarla";
		} else {
			if (!tipoSubreporte.equalsIgnoreCase("Incidencia") && !tipoSubreporte.equalsIgnoreCase("Presencia Materiales") && !tipoSubreporte.equalsIgnoreCase("Acciones de Mercado") && !tipoSubreporte.equalsIgnoreCase("Elementos Visib")) {			
				if (coment != null && coment.trim().isEmpty() && DatosManager.getInstancia().validarCaracteresEspeciales(coment).isEmpty()) {
					msg += "Debe escribir un comentario para agregar la foto";
				}
				
			}
		}
		return msg;
	}

	public void confirmar() {
		String msg = "";
		msg = validarDatos();
		if (msg.trim().isEmpty()) {
			AlertDialog alertDialog = new AlertDialog.Builder(ReporteFotoIncidencia.this).create();

			alertDialog.setMessage(getString(R.string.agregar_foto));

			alertDialog.setButton("Si", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					guardar(idCabecera);
				}
			});
			alertDialog.setButton2("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {

				}
			});
			alertDialog.show();
		} else {
			Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onBackPressed() {
		idFoto = 0;
		comentario = null;
		Editor edit = preferences.edit();
		edit.putInt("idFoto", idFoto);
		edit.putString("comentario", null);
		edit.putInt("resultCode", RESULT_CANCELED);
		edit.commit();
		super.onBackPressed();
		setRetorno();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Alternativa
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_foto, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (DatosManager.getInstancia().getUsuario() == null) {
			SQLiteDatabaseAdapter aSQLiteDatabaseAdapter = SQLiteDatabaseAdapter.getInstance(this);
			db = aSQLiteDatabaseAdapter.getWritableDatabase();

			DatosManager instanciaDM = (DatosManager) getLastNonConfigurationInstance();
			if (instanciaDM == null) {
				Log.i("ReporteFotoIncidencia", "Instancia recuperada Null");
				DatosManager.getInstancia().cargarDatos(db);
			} else {
				DatosManager.setInstancia(instanciaDM);
			}
		}
		Log.i("ReporteFotoIncidencia", "comentario antes de tomar la foto: " + comentario.getText().toString());
		Editor editFoto = preferences.edit();
		editFoto.putString("comentario", comentario.getText().toString());
		editFoto.commit();
		intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, REQUEST_TOMARFOTO);
		return true;
	}


	public void setRetorno() {
		Log.i("ReporteFotoIncidencia", "finish() de reporte foto incidencia");
		Intent data = new Intent();		
		data.putExtra("reinicio", true);
		setResult(REQUEST_TOMARFOTO, data);
		finish();
	}

}