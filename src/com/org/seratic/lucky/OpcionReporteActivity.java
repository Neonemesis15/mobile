package com.org.seratic.lucky;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.org.seratic.lucky.accessData.SQLiteDatabaseAdapter;
import com.org.seratic.lucky.accessData.control.E_tblMstReporteController;
import com.org.seratic.lucky.accessData.entities.E_MST_TBL_REPORTE;
import com.org.seratic.lucky.accessData.entities.Entity;
import com.org.seratic.lucky.manager.DatosManager;

public class OpcionReporteActivity extends Activity {

	private SQLiteDatabase db;
	private List<Entity> reportes;
	private TabHost mTabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ly_opcion_reporte);

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

		Intent intent = this.getIntent();
		Bundle extra = intent.getExtras();

		if (extra != null) {
			int idReporte = extra.getInt("idOpcionReporte");
			actualizarReporte(idReporte);
		}

		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
	}

	public void actualizarReporte(int idReporte) {
		E_tblMstReporteController reporteController = new E_tblMstReporteController(db);
		reportes = reporteController.getById(idReporte);
		actualizarVista();
	}

	public void actualizarVista() {
		if (reportes.size() > 0) {
			E_MST_TBL_REPORTE r = (E_MST_TBL_REPORTE) reportes.get(0);
			TextView tituloPuntoTextView = (TextView) findViewById(R.id.tituloPuntoVentaTextView);
			// tituloPuntoTextView.setText(reporte.getAlias());
			tituloPuntoTextView.setText("");
			TextView tituloReporteTextView = (TextView) findViewById(R.id.tituloReporteTextView);
			tituloReporteTextView.setText(r.getAlias());

			// Determinamos si hay subreportes
			if (reportes.size() > 1) {
				// TabHost tabs = (TabHost) findViewById(android.R.id.tabhost);
				// tabs.setup();
				for (int i = 0; i < reportes.size(); i++) {
					r = (E_MST_TBL_REPORTE) reportes.get(i);
					if (r.getIdSubreporte() != 0) {
						/*
						 * LinearLayout contentTab = new LinearLayout(this);
						 * contentTab.setLayoutParams(new
						 * LayoutParams(LayoutParams.FILL_PARENT,
						 * LayoutParams.FILL_PARENT)); contentTab.setId(i);
						 * TabHost.TabSpec spec = tabs.newTabSpec("mitab1");
						 * spec.setIndicator(r.getAliasSubreporte(), null);
						 * tabs.addTab(spec);
						 */
						setupTab(new TextView(this), r.getAliasSubreporte());
					}
				}
				// tabs.setCurrentTab(0);
			}
		}

	}

	private void setupTab(final View view, final String tag) {
		View tabview = createTabView(this, tag);
		TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview).setContent(new TabContentFactory() {
			public View createTabContent(String tag) {
				return view;
			}
		});
		mTabHost.addTab(setContent);
	}

	private static View createTabView(final Context context, final String text) {
		View view = LayoutInflater.from(context).inflate(R.layout.bg_tab, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		return view;
	}
}
