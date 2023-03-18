package com.org.seratic.lucky;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.org.seratic.lucky.accessData.SQLiteDatabaseAdapter;
import com.org.seratic.lucky.accessData.control.E_MstMotivoObsController;
import com.org.seratic.lucky.accessData.control.E_MstMotivoReporteController;
import com.org.seratic.lucky.accessData.control.E_TblMovReporteCabController;
import com.org.seratic.lucky.accessData.control.E_tblMovRepMaterialDeApoyoController;
import com.org.seratic.lucky.accessData.control.E_tblMstTipoPrecioController;
import com.org.seratic.lucky.accessData.control.PuntoVentaController;
import com.org.seratic.lucky.accessData.control.ReportesController;
import com.org.seratic.lucky.accessData.control.TblMovRepNewCodigoITTController;
import com.org.seratic.lucky.accessData.control.TblMovRepPresencia;
import com.org.seratic.lucky.accessData.control.TblMstClusterController;
import com.org.seratic.lucky.accessData.control.TblMstDistribuidoraController;
import com.org.seratic.lucky.accessData.control.TblMstFamiliaController;
import com.org.seratic.lucky.accessData.control.TblMstMaterialApoyoController;
import com.org.seratic.lucky.accessData.control.TblMstMovFiltrosAppController;
import com.org.seratic.lucky.accessData.control.TblMstObservacionController;
import com.org.seratic.lucky.accessData.control.TblMstProductoController;
import com.org.seratic.lucky.accessData.entities.E_MotivoObservacion;
import com.org.seratic.lucky.accessData.entities.E_ReporteAuditoria;
import com.org.seratic.lucky.accessData.entities.E_ReporteEstrella;
import com.org.seratic.lucky.accessData.entities.E_ReporteExhibicion;
import com.org.seratic.lucky.accessData.entities.E_ReporteExhibicionDet;
import com.org.seratic.lucky.accessData.entities.E_ReporteImpulso;
import com.org.seratic.lucky.accessData.entities.E_ReporteIncidencia;
import com.org.seratic.lucky.accessData.entities.E_ReportePotencial;
import com.org.seratic.lucky.accessData.entities.E_ReportePrecio;
import com.org.seratic.lucky.accessData.entities.E_ReporteQuiebre;
import com.org.seratic.lucky.accessData.entities.E_ReporteStock;
import com.org.seratic.lucky.accessData.entities.E_ReporteEncuesta;
import com.org.seratic.lucky.accessData.entities.E_TBL_MOV_REP_COD_NEW_ITT;
import com.org.seratic.lucky.accessData.entities.E_TBL_MOV_REP_PRESENCIA;
import com.org.seratic.lucky.accessData.entities.E_TblFiltrosApp;
import com.org.seratic.lucky.accessData.entities.E_TblMovRepMaterialDeApoyo;
import com.org.seratic.lucky.accessData.entities.E_TblMovReporteCab;
import com.org.seratic.lucky.accessData.entities.E_TblMstOpcPedido;
import com.org.seratic.lucky.accessData.entities.E_TblMst_Tipo_Material;
import com.org.seratic.lucky.accessData.entities.E_TipoPrecioPDV;
import com.org.seratic.lucky.accessData.entities.Entity;
import com.org.seratic.lucky.manager.CustomDigitWatcherAAVVSanFernando;
import com.org.seratic.lucky.manager.CustomDigitWatcher_0_2500;
import com.org.seratic.lucky.manager.CustomTextWatcher;
import com.org.seratic.lucky.manager.DatosManager;
import com.org.seratic.lucky.manager.DecimalFilter;
import com.org.seratic.lucky.manager.TiposReportes;
import com.org.seratic.lucky.vo.DistribuidoraVo;

public class ReportesGrillaActivity implements IReportes {

	private SQLiteDatabase db;
	private List<HashMap<String, String>> datosAnterioresList;
	private String keyReporte;
	private List<Object> elementos;
	private HashMap<String, ArrayList<Object>> datosReporte;
	private ArrayList<Object> datosFila;
	private Context context;
	private View view;
	private int idCabecera;
	private Boolean reporteCambio = false;
	private boolean infoRelevada = true;
	private LayoutInflater inflator;
	private int tipoReporte;
	private TblMovRepPresencia reporteController;
	private TableRow filaCambiar;
	private int index_filaCambiar;
	private int colorFila = 0;
	private int colorFilaSeleccion = 0;
	private ReportesController reportesController;
	private List<Entity> opciones;
	List<Entity> motivosOferta;
	E_tblMovRepMaterialDeApoyoController repMaterialApoyoController;
	List<E_TblMstOpcPedido> opcPedidos;
	private TblMstDistribuidoraController distribuidorasController;
	AlertDialog dialogListPedidos;
	List<Object> elementosPedidos;

	public ReportesGrillaActivity(Context context, int idCabecera,
			int tipoReporte) {

		this.context = context;
		this.idCabecera = idCabecera;

		inflator = LayoutInflater.from(context);
		colorFila = context.getResources().getColor(R.color.azulclaro);
		colorFilaSeleccion = context.getResources().getColor(
				R.color.fucsiaSeleccion);

		this.tipoReporte = tipoReporte;

		switch (tipoReporte) {

		case TiposReportes.TIPO_PRESENCIA_VISIBILIDAD_COLGATE_COD_CANTIDAD:
		case TiposReportes.TIPO_PRESENCIA_VISIBILIDAD_COLGATE_COD_CANTIDAD_VENTANA:
			view = inflator.inflate(R.layout.ly_reporte_presencia_codigonombre_head, null);
			break;
		// TIPO_VISIVILIDAD_COLGATE_VENTANA
		case TiposReportes.TIPO_PRESENCIA_VISIBILIDAD_COLGATE_COD_CUENTA_CUMPLE:
			view = inflator.inflate(R.layout.ly_reporte_presencia_codigocuentacumple_head, null);
			break;

		case TiposReportes.TIPO_PRESENCIA_VISIBILIDAD_COMPETENCIA_COD_PRECIO:
			view = inflator.inflate(R.layout.ly_reporte_presencia_materialprecio_head, null);
			break;
		case TiposReportes.TIPO_PRESENCIA_VISIB_COMPETENCIA_COD_CANTIDAD:
			view = inflator.inflate(R.layout.ly_reporte_presencia_visib_competencia_cod_cant_head, null);
			break;

		case TiposReportes.TIPO_PRESENCIA_VISIB_COMPETENCIA_COD_CUENTA:
			view = inflator.inflate(R.layout.ly_reporte_presencia_visib_competencia_cod_cuenta_head, null);
			break;

		case TiposReportes.TIPO_PRESENCIA_COLGATE_SUPERVISOR_SKUPRECIO:
		case TiposReportes.TIPO_PRESENCIA_COMPETENCIA_SUPERVISOR_SKUPRECIO:

			view = inflator.inflate(R.layout.ly_reporte_presencia_skuprecio_head, null);
			break;

		case TiposReportes.TIPO_PRESENCIA_OBSERVACIONES_COLGATE:
			view = inflator.inflate(R.layout.ly_reporte_presencia_codigoobservacion_head, null);
			break;

		case TiposReportes.TIPO_PRESENCIA_COLGATE_SKU_FRENTE_PROFUNDIDAD:
			view = inflator.inflate(R.layout.ly_reporte_presencia_skufrenteprof_head, null);
			break;

		case TiposReportes.TIPO_PRESENCIA_COLGATE_GESTOR_SKUPRESENCIA:
		case TiposReportes.TIPO_PRESENCIA_COMPETENCIA_GESTOR_SKUPRESENCIA:
			view = inflator.inflate(R.layout.ly_reporte_presencia_sku_presencia_head, null);
			break;

		case TiposReportes.TIPO_PRESENCIA_COLGATE_GESTOR_SKU_PRESENCIA_STOCK:
		case TiposReportes.TIPO_PRESENCIA_COMPETENCIA_GESTOR_SKU_PRESENCIA_STOCK:
			view = inflator.inflate(R.layout.ly_reporte_presencia_sku_presencia_stock_head, null);
			break;


		case TiposReportes.TIPO_PRESENCIA_STOCK_COLGATE_SKU_STOCK:
		case TiposReportes.TIPO_PRESENCIA_STOCK_COMPETENCIA_SKU_STOCK:
			view = inflator.inflate(R.layout.ly_reporte_stock_skustock_head, null);
			break;
		case TiposReportes.TIPO_PRESENCIA_CLUSTER_PREGUNTA_MARQUE_CANTIDAD:
			view = inflator.inflate(R.layout.ly_reporte_presencia_clusterpreguntamarquecant_head, null);
			break;
		case TiposReportes.TIPO_PRECIOS_ALICORP_SKU_PPDV_POFERTA_MOBS:
			view = inflator.inflate(R.layout.ly_reporte_precio_alicorp_autoservicios_head, null);
			break;
		case TiposReportes.TIPO_PRECIOS_ALICORP_SKU_PMAYORISTA_PREVENTA_POFERTA_MOBS:
			view = inflator.inflate(R.layout.ly_reporte_precio_alicorp_mayoristas_head, null);
			break;
		/*
		 * case TiposReportes.TIPO_PRECIOS_AAVV_SANFERNANDO: view =
		 * inflator.inflate(R.layout.ly_reporte_precio_sanfernando_aavv_head,
		 * null); break;
		 */
		case TiposReportes.TIPO_PRECIOS_SF_MODERNO:
			view = inflator.inflate(R.layout.ly_reporte_precio_sanfernando_moderno_head, null);
			break;

		case TiposReportes.TIPO_QUIEBRES_ALICORP_SKU_MOBS:
			view = inflator.inflate(R.layout.ly_reporte_quiebre_alicorp_autoservicios_head, null);
			break;
		case TiposReportes.TIPO_STOCK_ALICORP_COD_STOCK_MOBS:
			view = inflator.inflate(R.layout.ly_reporte_stock_alicorp_mayoristas_head, null);
			break;
		/*
		 * case TiposReportes.TIPO_VENTAS_AAVV_SANFERNANDO: view =
		 * inflator.inflate(R.layout.ly_reporte_ventas_sanfernando_aavv_head,
		 * null); break;
		 */
		case TiposReportes.TIPO_INGRESOS_SF_MODERNO:
			view = inflator.inflate(R.layout.ly_reporte_ingresos_sanfernando_moderno_head, null);
			break;
		case TiposReportes.TIPO_IMPULSO_SF_MODERNO:
			view = inflator.inflate(R.layout.ly_reporte_impulso_sanfernando_moderno_head, null);
			break;
		/*
		 * case TiposReportes.TIPO_PRESENCIA_TRADICIONAL_SANFERNANDO: view =
		 * inflator.inflate(
		 * R.layout.ly_reporte_presencia_sanfernando_tradicional_head, null);
		 * break; case TiposReportes.TIPO_EXHIBICION_TRADICIONAL_SANFERNANDO:
		 * view = inflator .inflate(
		 * R.layout.ly_reporte_exhibicion_sanfernando_tradicional_head, null);
		 * break; case
		 * TiposReportes.TIPO_ENTREGA_MATERIALES_TRADICIONAL_SANFERNANDO: view =
		 * inflator.inflate( R.layout.ly_reporte_entrega_materiales_head, null);
		 * break; case
		 * TiposReportes.TIPO_ASESORAMIENTO_PROD_TRADICIONAL_SANFERNANDO: view =
		 * inflator .inflate(
		 * R.layout.ly_reporte_asesoramiento_prod_sanfernando_tradicional_head,
		 * null); break; case
		 * TiposReportes.TIPO_INCIDENCIAS_STATUS_TRADICIONAL_SANFERNANDO: view =
		 * inflator .inflate(
		 * R.layout.ly_reporte_incidencia_status_sanfernando_tradicional_head,
		 * null); break; case
		 * TiposReportes.TIPO_INCIDENCIAS_INCID_TRADICIONAL_SANFERNANDO: view =
		 * inflator .inflate(
		 * R.layout.ly_reporte_incidencia_sanfernando_tradicional_head, null);
		 * break;
		 */
		case TiposReportes.TIPO_CODIGOS_ITT_COLGATE:
			view = inflator.inflate(R.layout.ly_reporte_itt_distribuidoracodigoitt_head, null);
			break;
		case TiposReportes.TIPO_POTENCIAL_REVESTIMIENTO_SF_AAVV:
		case TiposReportes.TIPO_POTENCIAL_POTENCIAL_SF_AAVV:
			view = inflator.inflate(R.layout.ly_reporte_entrega_materiales_head, null);
			break;
		case TiposReportes.TIPO_PRECIOPVP_SF_AAVV:
			view = inflator.inflate(R.layout.ly_reporte_precio_pvp_sanfernando_aavv_head, null);
			break;
		case TiposReportes.TIPO_PRECIOPDV_OBS_SF_AAVV:
			view = inflator.inflate(R.layout.ly_reporte_precio_pdv_obs_sanfernando_aavv_head, null);
			break;
		case TiposReportes.TIPO_PRECIOPDV_PDV_SF_AAVV:
			view = inflator.inflate(R.layout.ly_reporte_precio_pdv_pdv_sanfernando_aavv_head, null);
			break;
		case TiposReportes.TIPO_AUDITORIA_PLASTAZUL_SF_AAVV:
		case TiposReportes.TIPO_AUDITORIA_REJBLANCA_SF_AAVV:
			view = inflator.inflate(R.layout.ly_reporte_auditoria_sanfernando_aavv_head, null);
			break;
		case TiposReportes.TIPO_ESTRELLA_SF_AAVV:
			view = inflator.inflate(R.layout.ly_reporte_estrella_sanfernando_aavv_head, null);
			break;
		case TiposReportes.TIPO_PRECIOS_SF_TRADICIONAL_CHIKARA:
			view = inflator.inflate(R.layout.ly_reporte_precio_sanfernando_tradicional_chikara_head, null);
			break;
		case TiposReportes.TIPO_PRESENCIA_PRODUCTOS_SF_TRADICIONAL_CHIKARA:
			view = inflator.inflate(R.layout.ly_reporte_presencia_sanfernando_tradicional_chikara_head, null);
			break;
		case TiposReportes.TIPO_ENCUESTAS_SF_TRADICIONAL_CHIKARA:
			view = inflator.inflate(R.layout.ly_reporte_encuesta_sanfernando_chikara_head, null);
			break;
		case TiposReportes.TIPO_BLOQUEAZUL_BLOQUE_SF_TRADICIONAL_CHIKARA:
			view = inflator.inflate(R.layout.ly_reporte_bloque_azul_sanfernando_chikara_head, null);
			break;
		case TiposReportes.TIPO_BLOQUEAZUL_FRENTE_SF_TRADICIONAL_CHIKARA:
			view = inflator.inflate(R.layout.ly_reporte_bloque_azul_sanfernando_chikara_head, null);
			break;
		default:
			view = inflator.inflate(R.layout.ly_reporte_presencia_codigonombre_head, null);
			break;
		}
		init();
	}

	public void init() {
		SQLiteDatabaseAdapter aSQLiteDatabaseAdapter = SQLiteDatabaseAdapter.getInstance(context);
		db = aSQLiteDatabaseAdapter.getWritableDatabase();

		reporteController = new TblMovRepPresencia(db);
		reportesController = new ReportesController(db);
		repMaterialApoyoController = new E_tblMovRepMaterialDeApoyoController(db);
		distribuidorasController = new TblMstDistribuidoraController(db);

		switch (tipoReporte) {
		case TiposReportes.TIPO_PRESENCIA_VISIBILIDAD_COLGATE_COD_CANTIDAD:
			// case TiposReportes.TIPO_PRESENCIA_BOD_VISIBILIDAD:
			show_reporte_presencia_tblmateriales_codigonombre(true, 2, false);
			break;

		case TiposReportes.TIPO_PRESENCIA_VISIBILIDAD_COLGATE_COD_CANTIDAD_VENTANA:
			show_reporte_presencia_tblmateriales_codigonombre(true, 2, true);
			break;

		case TiposReportes.TIPO_PRESENCIA_VISIBILIDAD_COLGATE_COD_CUENTA_CUMPLE:
			show_reporte_presencia_tblmateriales_codigocuentacumple_head(true, 2);
			break;

		case TiposReportes.TIPO_PRESENCIA_COLGATE_SUPERVISOR_SKUPRECIO:
			show_reporte_presencia_tblproductos_skuprecio(true);
			break;

		case TiposReportes.TIPO_PRESENCIA_COMPETENCIA_SUPERVISOR_SKUPRECIO:
			show_reporte_presencia_tblproductos_skuprecio(false);
			break;

		case TiposReportes.TIPO_PRESENCIA_VISIBILIDAD_COMPETENCIA_COD_PRECIO:
			show_reporte_presencia_tblmateriales_skuprecio(false, 2);
			break;
		case TiposReportes.TIPO_PRESENCIA_VISIB_COMPETENCIA_COD_CANTIDAD:
			show_reporte_presencia_visib_competencia_cod_cant(false, 2);
			break;
		case TiposReportes.TIPO_PRESENCIA_VISIB_COMPETENCIA_COD_CUENTA:
			show_reporte_presencia_visib_competencia_cod_cuenta(false, 2);
			break;

		case TiposReportes.TIPO_PRESENCIA_OBSERVACIONES_COLGATE:
			show_reporte_presencia_tblobservacion_codigoobservacion();
			break;

		case TiposReportes.TIPO_PRESENCIA_COLGATE_SKU_FRENTE_PROFUNDIDAD:
			show_reporte_presencia_tblproductos_skufrenteprof(true);
			break;

		case TiposReportes.TIPO_PRESENCIA_COLGATE_GESTOR_SKUPRESENCIA:
			show_reporte_presencia_tblproductos_skupresencia(true);
			break;
		case TiposReportes.TIPO_PRESENCIA_COMPETENCIA_GESTOR_SKUPRESENCIA:
			show_reporte_presencia_tblproductos_skupresencia(false);
			break;

		case TiposReportes.TIPO_PRESENCIA_COLGATE_GESTOR_SKU_PRESENCIA_STOCK:
			show_reporte_presencia_tblproductos_skupresenciastock(true);
			break;
		case TiposReportes.TIPO_PRESENCIA_COMPETENCIA_GESTOR_SKU_PRESENCIA_STOCK:
			show_reporte_presencia_tblproductos_skupresenciastock(false);
			break;

		case TiposReportes.TIPO_PRESENCIA_STOCK_COLGATE_SKU_STOCK:
			show_reporte_presencia_tblproductos_skustock(true);
			break;
		case TiposReportes.TIPO_PRESENCIA_STOCK_COMPETENCIA_SKU_STOCK:
			show_reporte_presencia_tblproductos_skustock(false);
			break;
		case TiposReportes.TIPO_PRESENCIA_CLUSTER_PREGUNTA_MARQUE_CANTIDAD:
			show_reporte_presencia_tblcluster_preguntamarquecantidad();
			break;

		case TiposReportes.TIPO_PRECIOS_ALICORP_SKU_PPDV_POFERTA_MOBS:
			show_reporte_precio_alicorp_autoservicio();
			break;
		case TiposReportes.TIPO_PRECIOS_ALICORP_SKU_PMAYORISTA_PREVENTA_POFERTA_MOBS:
			show_reporte_precio_alicorp_mayoristas();
			break;
		/*
		 * case TiposReportes.TIPO_PRECIOS_AAVV_SANFERNANDO:
		 * show_reporte_precio_sanfernando_aavv(); break;
		 */
		case TiposReportes.TIPO_PRECIOS_SF_MODERNO:
			show_reporte_precio_sanfernando_moderno();
			break;
		/*
		 * case TiposReportes.TIPO_PRECIOS_TRADICIONAL_SANFERNANDO:
		 * show_reporte_precio_sanfernando_tradicional(); break;
		 */
		case TiposReportes.TIPO_QUIEBRES_ALICORP_SKU_MOBS:
			show_reporte_quiebre_alicorp_autoservicio();
			break;
		case TiposReportes.TIPO_STOCK_ALICORP_COD_STOCK_MOBS:
			show_reporte_stock_alicorp_mayoristas();
			break;

		/*
		 * case TiposReportes.TIPO_VENTAS_AAVV_SANFERNANDO:
		 * show_reporte_ventas_sanfernando_aavv(); break;
		 */
		case TiposReportes.TIPO_INGRESOS_SF_MODERNO:
			show_reporte_ingresos_sanfernando_moderno(false);
			break;
		case TiposReportes.TIPO_IMPULSO_SF_MODERNO:
			show_reporte_impulso_sanfernando_moderno(false);
			break;
		/*
		 * case TiposReportes.TIPO_PRESENCIA_TRADICIONAL_SANFERNANDO:
		 * show_reporte_presencia_tradicional_Sanfernando(); break; case
		 * TiposReportes.TIPO_EXHIBICION_TRADICIONAL_SANFERNANDO:
		 * show_reporte_exhibicion_sanfernando_tradicional(); break; case
		 * TiposReportes.TIPO_ENTREGA_MATERIALES_TRADICIONAL_SANFERNANDO:
		 * show_reporte_entrega_materiales_sanfernando_tradicional(); break;
		 * case TiposReportes.TIPO_ASESORAMIENTO_PROD_TRADICIONAL_SANFERNANDO:
		 * show_reporte_asesoramiento_prod_sanfernando_tradicional(); break;
		 * case TiposReportes.TIPO_INCIDENCIAS_STATUS_TRADICIONAL_SANFERNANDO:
		 * show_reporte_incidencias_status_sanfernando_tradicionalCheck();
		 * show_reporte_incidencias_status_sanfernando_tradicionalPedidos();
		 * break; case
		 * TiposReportes.TIPO_INCIDENCIAS_INCID_TRADICIONAL_SANFERNANDO:
		 * show_reporte_incidencias_incid_sanfernando_tradicional(); break;
		 */
		case TiposReportes.TIPO_CODIGOS_ITT_COLGATE:
			show_reporte_itt_colgate_bodega(false);
			break;
		case TiposReportes.TIPO_POTENCIAL_REVESTIMIENTO_SF_AAVV:
			show_reporte_potencial_revest_sanfernando_aavv();
			break;
		case TiposReportes.TIPO_POTENCIAL_POTENCIAL_SF_AAVV:
			show_reporte_potencial_potencial_sanfernando_aavv();
			break;
		case TiposReportes.TIPO_PRECIOPVP_SF_AAVV:
			show_reporte_precio_pvp_sanfernando_aavv();
			break;
		case TiposReportes.TIPO_PRECIOPDV_OBS_SF_AAVV:
			show_reporte_precio_pvd_obs_sanfernando_aavv();
			break;
		case TiposReportes.TIPO_PRECIOPDV_PDV_SF_AAVV:
			show_reporte_precio_pvd_pdv_sanfernando_aavv();
			break;
		case TiposReportes.TIPO_AUDITORIA_PLASTAZUL_SF_AAVV:
			show_reporte_auditoria_sanfernando_AAVV(E_TblMst_Tipo_Material.TIPO_AZUL_PLASTICO);
			break;
		case TiposReportes.TIPO_AUDITORIA_REJBLANCA_SF_AAVV:
			show_reporte_auditoria_sanfernando_AAVV(E_TblMst_Tipo_Material.TIPO_REJILLA_PLASTICA);
			break;
		case TiposReportes.TIPO_ESTRELLA_SF_AAVV:
			show_reporte_estrella_sanfernando_aavv();
			break;
		case TiposReportes.TIPO_PRECIOS_SF_TRADICIONAL_CHIKARA:
			show_reporte_precio_sanfernando_tradicional_chikara();
			break;
		case TiposReportes.TIPO_PRESENCIA_PRODUCTOS_SF_TRADICIONAL_CHIKARA:
			show_reporte_presencia_Sanfernando_Tradicional_Chikara();
			break;
		case TiposReportes.TIPO_ENCUESTAS_SF_TRADICIONAL_CHIKARA:
			show_reporte_encuestas_Sanfernando_Tradicional_Chikara();
			break;

		}
		view.invalidate();
		view.refreshDrawableState();
	}

	private void onClickFila(View v, String textSubtitulo, int index,
			String key, TableRow fila) {
		TextView tvSubtitulo = (TextView) view.findViewById(R.id.tv_subtitulo);
		tvSubtitulo.setText(Html.fromHtml(textSubtitulo));
		if (elementos != null && !key.equals("")) {
			((View) ((datosReporte.get(key)).get(0))).requestFocus();
		}
		if (fila != null) {
			Log.i("", "setColorGrilla by fila" + index);
			fila.setBackgroundColor(colorFilaSeleccion);
			fila.invalidate();
		} else {
			Log.i("", "setColorGrilla " + index);
			v.setBackgroundColor(colorFilaSeleccion);
			v.invalidate();
		}
		if (filaCambiar != null && (index_filaCambiar != index)) {

			if (index_filaCambiar % 2 == 0) {

				filaCambiar.setBackgroundColor(colorFila);
			} else {
				filaCambiar.setBackgroundColor(Color.WHITE);
			}

		}
		if (fila != null) {
			filaCambiar = fila;
		} else {
			filaCambiar = (TableRow) v;
		}
		index_filaCambiar = index;
	}

	// @Override
	public String guardar(int idCabeceraGuardada) {
		String msg = "";
		msg = validarDatos();

		if (msg.trim().isEmpty()) {
			guardarReporte();
		}
		return msg;
	}

	public String getKeyReporte() {
		return keyReporte;
	}

	// @Override
	public void setKey(String key) {
		this.keyReporte = key;

	}

	/*
	 * Reporte de Presencia Elementos de Visibilidad para canal mayorista
	 * minorista bodegas farmacias IT.
	 */
	public void show_reporte_presencia_tblmateriales_codigonombre(
			boolean isPropio, int tipo, boolean ventana) {
		Log.i("ReportesGrillaActivity",
				"... show_reporte_presencia_tblmateriales_codigonombre(isPropio="
						+ isPropio + ", tipo=" + tipo + ")");
		TableLayout table = (TableLayout) view
				.findViewById(R.id.tl_reporte_presencia_codigonombre);
		table.removeAllViews();
		elementos = (new TblMstMaterialApoyoController(db)).getElementsForGrid(
				isPropio, tipo, idCabecera, 2, ventana);
		int numMateriales = 0;
		boolean ini = true;
		if (elementos != null && (numMateriales = elementos.size()) > 0) {
			datosReporte = new HashMap<String, ArrayList<Object>>();
			for (int i = 0; i < numMateriales; i++) {
				datosFila = new ArrayList<Object>();
				final E_TBL_MOV_REP_PRESENCIA mA = (E_TBL_MOV_REP_PRESENCIA) elementos
						.get(i);
				TableRow row = (TableRow) inflator.inflate(
						R.layout.ly_reporte_presencia_codigonombre_body, null);
				((TextView) row.findViewById(R.id.tv_codigo)).setText(mA
						.getCod_material_apoyo());
				EditText et_nombre = (EditText) row
						.findViewById(R.id.et_nombre);
				et_nombre.setText(mA.getValor_material_apoyo());
				datosFila.add(et_nombre);
				Log.i("@@@@", "Set Text" + mA.getValor_material_apoyo()
						+ "Codigo" + mA.getCod_material_apoyo());
				et_nombre.invalidate();
				et_nombre.bringToFront();
				et_nombre.refreshDrawableState();

				String key = mA.getCod_material_apoyo();

				createRow(table, row, et_nombre, null, null, null, null, null, i, ini, mA.getDescripcion(), null, key);
				datosFila.add(et_nombre);
				Log.i("ReportesGrillaActivity", "... datosReporte.put. key = " + key + " - valor_material = " + mA.getValor_material_apoyo());
				datosReporte.put(key, datosFila);
			}
		} else {
			Toast.makeText(this.context, "No hay materiales de apoyo registrados para este reporte", Toast.LENGTH_SHORT).show();
		}
		table.invalidate();
	}

	/*
	 * Reporte de Presencia Visibilidad Competencia para canal farmacias IT, DT
	 * y Bodegas.
	 */
	public void show_reporte_presencia_visib_competencia_cod_cant(
			boolean isPropio, int tipo) {
		Log.i("ReportesGrillaActivity","... show_reporte_presencia_visib_competencia_cod_cant(isPropio=" + isPropio + ", tipo=" + tipo + ")");
		TableLayout table = (TableLayout) view.findViewById(R.id.tl_reporte_presencia_visib_competencia_cod_cant);
		table.removeAllViews();
		elementos = (new TblMstMaterialApoyoController(db)).getElementsForGrid(isPropio, tipo, idCabecera, 3, false);
		int numMateriales = 0;
		boolean ini = true;
		if (elementos != null && (numMateriales = elementos.size()) > 0) {
			datosReporte = new HashMap<String, ArrayList<Object>>();
			for (int i = 0; i < numMateriales; i++) {
				datosFila = new ArrayList<Object>();
				final E_TBL_MOV_REP_PRESENCIA mA = (E_TBL_MOV_REP_PRESENCIA) elementos.get(i);
				TableRow row = (TableRow) inflator.inflate(R.layout.ly_reporte_presencia_visib_competencia_cod_cant_body, null);
				((TextView) row.findViewById(R.id.tv_codigo)).setText(mA.getCod_material_apoyo());
				EditText et_cantidad = (EditText) row.findViewById(R.id.et_cantidad);
				et_cantidad.setText(mA.getCantidad());
				datosFila.add(et_cantidad);
				String key = mA.getCod_material_apoyo();
				createRow(table, row, et_cantidad, null, null, null, null, null, i, ini, mA.getDescripcion(), null, key);
				Log.i("ReportesGrillaActivity", "... datosReporte.put. key = " + key + " - valor_material = " + mA.getValor_material_apoyo());
				datosReporte.put(key, datosFila);
			}
		} else {
			Toast.makeText(this.context,
					"No hay materiales de apoyo registrados para este reporte", Toast.LENGTH_SHORT).show();
		}
		table.invalidate();
	}

	public void show_reporte_presencia_visib_competencia_cod_cuenta(
			boolean isPropio, int tipo) {
		elementos = (new TblMstMaterialApoyoController(db)).getElementsForGrid(
				isPropio, tipo, idCabecera, 2, false);

		TableLayout table = (TableLayout) view
				.findViewById(R.id.tl_reporte_presencia_visib_competencia_cod_cuenta);
		int numMateriales = 0;

		boolean ini = true;
		if (elementos != null && (numMateriales = elementos.size()) > 0) {
			datosReporte = new HashMap<String, ArrayList<Object>>();
			for (int i = 0; i < numMateriales; i++) {
				final E_TBL_MOV_REP_PRESENCIA mA = (E_TBL_MOV_REP_PRESENCIA) elementos
						.get(i);
				datosFila = new ArrayList<Object>();
				TableRow row = (TableRow) inflator
						.inflate(
								R.layout.ly_reporte_presencia_visib_competencia_cod_cuenta_body,
								null);
				((TextView) row.findViewById(R.id.tv_codigo)).setText(mA
						.getCod_material_apoyo());
				CheckBox ck_cuenta = (CheckBox) row
						.findViewById(R.id.ck_cuenta);

				String key = mA.getCod_material_apoyo();

				createRow(table, row, null, null, null, null, ck_cuenta, null,
						i, ini, mA.getDescripcion(), null, key);

				datosFila.add(ck_cuenta);
				if (mA.getValor_material_apoyo() != null
						&& mA.getValor_material_apoyo().equals("1")) {
					ck_cuenta.setChecked(true);
				}
				ck_cuenta.invalidate();
				ck_cuenta.setSelected(true);
				datosReporte.put(key, datosFila);
			}
			table.invalidate();
		} else {
			Toast.makeText(this.context,
					"No hay materiales de apoyo registrados para este reporte",
					Toast.LENGTH_SHORT).show();

		}
	}

	/*
	 * Reporte de Presencia Colgate Competencia de Visibilida para canal
	 * mayorista minorista bodegas .
	 */

	public void show_reporte_presencia_tblproductos_skuprecio(boolean isPropio) {
		elementos = (new TblMstProductoController(db)).getElementsForGrid(
				isPropio, idCabecera, false);
		TableLayout table = (TableLayout) view
				.findViewById(R.id.tl_reporte_presencia_codigonombre);
		int numElementos = 0;
		boolean ini = true;
		if (elementos != null && (numElementos = elementos.size()) > 0) {
			datosReporte = new HashMap<String, ArrayList<Object>>();
			for (int i = 0; i < numElementos; i++) {
				datosFila = new ArrayList<Object>();
				final E_TBL_MOV_REP_PRESENCIA mA = (E_TBL_MOV_REP_PRESENCIA) elementos
						.get(i);
				TableRow row = (TableRow) inflator.inflate(
						R.layout.ly_reporte_presencia_skuprecio_body, null);
				((TextView) row.findViewById(R.id.tv_sku)).setText(mA
						.getSku_producto());
				final EditText et_precio = (EditText) row
						.findViewById(R.id.et_precio);
				et_precio.setText(mA.getPrecio());
				et_precio.invalidate();
				et_precio.setSelected(true);
				datosFila.add(et_precio);

				String key = mA.getSku_producto();

				createRow(table, row, et_precio, null, null, null, null, null,
						i, ini, mA.getDescripcion(), null, key);

				Log.i("ReportesGrillaActivity", "... datosReporte.put. key = "
						+ key);
				datosReporte.put(key, datosFila);
			}
			table.invalidate();
		} else {
			Toast.makeText(this.context,
					"No hay productos registrados para este reporte",
					Toast.LENGTH_SHORT).show();
		}
	}

	public void show_reporte_presencia_tblmateriales_skuprecio(
			boolean isPropio, int tipo) {
		elementos = (new TblMstMaterialApoyoController(db)).getElementsForGrid(
				isPropio, tipo, idCabecera, 1, false);// precio
		TableLayout table = (TableLayout) view
				.findViewById(R.id.tl_reporte_presencia_codigonombre);
		int numElementos = 0;
		boolean ini = true;
		if (elementos != null && (numElementos = elementos.size()) > 0) {
			datosReporte = new HashMap<String, ArrayList<Object>>();
			for (int i = 0; i < numElementos; i++) {
				final E_TBL_MOV_REP_PRESENCIA mA = (E_TBL_MOV_REP_PRESENCIA) elementos
						.get(i);
				datosFila = new ArrayList<Object>();
				TableRow row = (TableRow) inflator.inflate(
						R.layout.ly_reporte_presencia_skuprecio_body, null);
				((TextView) row.findViewById(R.id.tv_sku)).setText(mA
						.getCod_material_apoyo());
				EditText et_precio = (EditText) row
						.findViewById(R.id.et_precio);
				et_precio.setText(mA.getPrecio());
				et_precio.invalidate();
				et_precio.setSelected(true);
				datosFila.add(et_precio);
				String key = mA.getCod_material_apoyo();

				createRow(table, row, et_precio, null, null, null, null, null,
						i, ini, mA.getDescripcion(), null, key);
				datosReporte.put(key, datosFila);
			}
			table.invalidate();
		} else {
			Toast.makeText(this.context,
					"No hay materiales de apoyo registrados para este reporte",
					Toast.LENGTH_SHORT).show();
		}
	}

	public void show_reporte_presencia_tblobservacion_codigoobservacion() {
		elementos = (new TblMstObservacionController(db))
				.getElementsForGrid(idCabecera);

		TableLayout table = (TableLayout) view
				.findViewById(R.id.tl_reporte_presencia_codigoobservacion);
		int numObservaciones = 0;
		// subtitulos = new HashMap<String, String>();
		// TextView tvSubtitulo = (TextView) findViewById(R.id.tv_subtitulo);
		boolean ini = true;
		if (elementos != null && (numObservaciones = elementos.size()) > 0) {
			datosReporte = new HashMap<String, ArrayList<Object>>();
			for (int i = 0; i < numObservaciones; i++) {
				final E_TBL_MOV_REP_PRESENCIA mA = (E_TBL_MOV_REP_PRESENCIA) elementos
						.get(i);
				datosFila = new ArrayList<Object>();
				TableRow row = (TableRow) inflator.inflate(
						R.layout.ly_reporte_presencia_codigoobservacion_body,
						null);
				((TextView) row.findViewById(R.id.tv_codigo)).setText(mA
						.getCod_observacion());
				CheckBox et_observacion = (CheckBox) row
						.findViewById(R.id.et_observacion);

				String key = mA.getCod_observacion();

				createRow(table, row, null, null, null, null, et_observacion,
						null, i, ini, mA.getDescripcion(), null, key);

				datosFila.add(et_observacion);
				if (mA.getObservacion() != null
						&& mA.getObservacion().equals("1")) {
					et_observacion.setChecked(true);
				}
				et_observacion.invalidate();
				et_observacion.setSelected(true);
				datosReporte.put(key, datosFila);
			}
			table.invalidate();
		} else {
			Toast.makeText(this.context,
					"No hay observaciones registradas para este reporte",
					Toast.LENGTH_SHORT).show();
		}
	}

	public void show_reporte_presencia_tblproductos_skustock(boolean isPropio) {
		elementos = (new TblMstProductoController(db)).getElementsForGrid(isPropio, idCabecera, false);
		TableLayout table = (TableLayout) view.findViewById(R.id.tl_reporte_presencia_codigonombre);
		int numElementos = 0;
		// subtitulos = new HashMap<String, String>();
		// TextView tvSubtitulo = (TextView) findViewById(R.id.tv_subtitulo);
		boolean ini = true;
		if (elementos != null && (numElementos = elementos.size()) > 0) {
			datosReporte = new HashMap<String, ArrayList<Object>>();
			for (int i = 0; i < numElementos; i++) {

				E_TBL_MOV_REP_PRESENCIA mA = (E_TBL_MOV_REP_PRESENCIA) elementos.get(i);
				datosFila = new ArrayList<Object>();
				TableRow row = (TableRow) inflator.inflate(R.layout.ly_reporte_stock_skustock_body, null);
				((TextView) row.findViewById(R.id.tv_sku)).setText(mA.getSku_producto());
				final EditText et_stock = (EditText) row.findViewById(R.id.et_stock);
				et_stock.setText(mA.getStock());
				et_stock.invalidate();
				et_stock.setSelected(true);
				datosFila.add(et_stock);

				String key = mA.getSku_producto();

				createRow(table, row, et_stock, null, null, null, null, null, i, ini, mA.getDescripcion(), null, key);

				datosReporte.put(key, datosFila);
			}
			table.invalidate();
		} else {
			Toast.makeText(this.context,
					"No hay productos registrados para este reporte",
					Toast.LENGTH_SHORT).show();
		}
	}

	public void show_reporte_presencia_tblmateriales_codigocuentacumple_head(
			boolean isPropio, int tipo) {
		elementos = (new TblMstMaterialApoyoController(db)).getElementsForGrid(
				isPropio, tipo, idCabecera, 2, false);// valor
		TableLayout table = (TableLayout) view
				.findViewById(R.id.tl_reporte_presencia_codigonombre);
		int numElementos = 0;
		boolean ini = true;
		if (elementos != null && (numElementos = elementos.size()) > 0) {
			datosReporte = new HashMap<String, ArrayList<Object>>();
			for (int i = 0; i < numElementos; i++) {
				E_TBL_MOV_REP_PRESENCIA mA = (E_TBL_MOV_REP_PRESENCIA) elementos
						.get(i);
				if (mA.getValor_material_apoyo() != null
						&& mA.getValor_material_apoyo().equalsIgnoreCase("1")) {
					if (mA.getCumple_layout() == null) {
						mA.setCumple_layout("0");
						((E_TBL_MOV_REP_PRESENCIA) elementos.get(i))
								.setCumple_layout("0");
					}
				} else if (mA.getCumple_layout() != null
						&& mA.getCumple_layout().equalsIgnoreCase("1")) {
					if (mA.getValor_material_apoyo() == null) {
						mA.setValor_material_apoyo("0");
						((E_TBL_MOV_REP_PRESENCIA) elementos.get(i))
								.setCod_material_apoyo("0");
					}
				} else if ((mA.getValor_material_apoyo() == null || mA
						.getValor_material_apoyo().equalsIgnoreCase("0"))
						&& (mA.getCumple_layout() == null || mA
								.getCumple_layout().equalsIgnoreCase("0"))) {
					mA.setValor_material_apoyo(null);
					mA.setCumple_layout(null);
					((E_TBL_MOV_REP_PRESENCIA) elementos.get(i))
							.setValor_material_apoyo(null);
					((E_TBL_MOV_REP_PRESENCIA) elementos.get(i))
							.setCumple_layout(null);
				}
				datosFila = new ArrayList<Object>();
				TableRow row = (TableRow) inflator.inflate(
						R.layout.ly_reporte_presencia_codigocuentacumple_body,
						null);
				((TextView) row.findViewById(R.id.tv_codigo)).setText(mA
						.getCod_material_apoyo());
				CheckBox ck_cuenta = (CheckBox) row
						.findViewById(R.id.ck_cuenta);
				ck_cuenta.setText("");
				datosFila.add(ck_cuenta);
				CheckBox ck_cumple = (CheckBox) row
						.findViewById(R.id.ck_cumple);
				ck_cumple.setText("");
				datosFila.add(ck_cumple);
				String key = mA.getCod_material_apoyo();
				createRow(table, row, null, null, null, null, ck_cuenta,
						ck_cumple, i, ini, mA.getDescripcion(), null, key);
				datosReporte.put(key, datosFila);
				if (mA.getCumple_layout() != null
						&& mA.getCumple_layout().equals("1")) {
					ck_cumple.setChecked(true);
				}
				if (mA.getValor_material_apoyo() != null
						&& mA.getValor_material_apoyo().equals("1")) {
					ck_cuenta.setChecked(true);
				}
				ck_cuenta.invalidate();
				ck_cuenta.setSelected(true);
				ck_cumple.invalidate();
				ck_cumple.setSelected(true);
			}
			table.invalidate();
		} else {
			Toast.makeText(this.context,
					"No hay materiales de apoyo registrados para este reporte",
					Toast.LENGTH_SHORT).show();
		}
	}

	public void show_reporte_presencia_tblproductos_skufrenteprof(
			boolean isPropio) {
		elementos = (new TblMstProductoController(db)).getElementsForGrid(
				isPropio, idCabecera, false);
		TableLayout table = (TableLayout) view
				.findViewById(R.id.tl_reporte_presencia_codigonombre);
		int numElementos = 0;
		// subtitulos = new HashMap<String, String>();
		// TextView tvSubtitulo = (TextView) findViewById(R.id.tv_subtitulo);
		boolean ini = true;
		if (elementos != null && (numElementos = elementos.size()) > 0) {
			datosReporte = new HashMap<String, ArrayList<Object>>();
			for (int i = 0; i < numElementos; i++) {
				E_TBL_MOV_REP_PRESENCIA mA = (E_TBL_MOV_REP_PRESENCIA) elementos
						.get(i);
				datosFila = new ArrayList<Object>();
				TableRow row = (TableRow) inflator.inflate(
						R.layout.ly_reporte_presencia_skufrenteprof_body, null);
				((TextView) row.findViewById(R.id.tv_sku)).setText(mA
						.getSku_producto());
				final EditText et_frente = (EditText) row
						.findViewById(R.id.et_frente);
				et_frente.setText(mA.getNum_frentes());
				et_frente.invalidate();
				et_frente.setSelected(true);
				datosFila.add(et_frente);
				final EditText et_prof = (EditText) row
						.findViewById(R.id.et_prof);
				et_prof.setText(mA.getProfundidad());
				et_prof.invalidate();
				et_prof.setSelected(true);
				datosFila.add(et_prof);
				String key = mA.getSku_producto();
				createRow(table, row, et_frente, et_prof, null, null, null,
						null, i, ini, mA.getDescripcion(), null, key);
				datosReporte.put(key, datosFila);
			}
			table.invalidate();
		} else {
			Toast.makeText(this.context,
					"No hay productos registrados para este reporte",
					Toast.LENGTH_SHORT).show();
		}
	}

	public void show_reporte_presencia_tblproductos_skupresencia(boolean isPropio) {
		elementos = (new TblMstProductoController(db)).getElementsForGrid(isPropio, idCabecera, true);
		TableLayout table = (TableLayout) view.findViewById(R.id.tl_reporte_presencia_sku_presencia);
		int numElementos = 0;
		// subtitulos = new HashMap<String, String>();
		// TextView tvSubtitulo = (TextView) findViewById(R.id.tv_subtitulo);
		boolean ini = true;
		if (elementos != null && (numElementos = elementos.size()) > 0) {
			datosReporte = new HashMap<String, ArrayList<Object>>();
			for (int i = 0; i < numElementos; i++) {
				E_TBL_MOV_REP_PRESENCIA mA = (E_TBL_MOV_REP_PRESENCIA) elementos.get(i);
				datosFila = new ArrayList<Object>();
				TableRow row = (TableRow) inflator.inflate(R.layout.ly_reporte_presencia_sku_presencia_body, null);
				((TextView) row.findViewById(R.id.tv_sku)).setText(mA.getSku_producto());
				CheckBox ck_presen = (CheckBox) row.findViewById(R.id.ck_presencia);
				datosFila.add(ck_presen);
				String key = mA.getSku_producto();
				createRow(table, row, null, null, null, null, ck_presen, null, i, ini, mA.getDescripcion(), null, key);
				datosReporte.put(key, datosFila);
				Log.i("ReportesGrillaActivity", "cargando reporte sku_presencia con presencia en: " + mA.getCod_presencia());
				if (mA.getCod_presencia() != null && mA.getCod_presencia().equals("1")) {
					ck_presen.setChecked(true);
				}
				ck_presen.invalidate();
				ck_presen.setSelected(true);
			}
			table.invalidate();
		} else {
			Toast.makeText(this.context, "No hay productos registrados para este reporte", Toast.LENGTH_SHORT).show();
		}
	}

	public void show_reporte_presencia_tblproductos_skupresenciastock(boolean isPropio) {
		elementos = (new TblMstProductoController(db)).getElementsForGrid(isPropio, idCabecera, true);
		TableLayout table = (TableLayout) view.findViewById(R.id.tl_reporte_presencia_sku_presencia);
		int numElementos = 0;
		// subtitulos = new HashMap<String, String>();
		// TextView tvSubtitulo = (TextView) findViewById(R.id.tv_subtitulo);
		boolean ini = true;
		if (elementos != null && (numElementos = elementos.size()) > 0) {
			datosReporte = new HashMap<String, ArrayList<Object>>();
			for (int i = 0; i < numElementos; i++) {
				E_TBL_MOV_REP_PRESENCIA mA = (E_TBL_MOV_REP_PRESENCIA) elementos.get(i);
				datosFila = new ArrayList<Object>();
				TableRow row = (TableRow) inflator.inflate(R.layout.ly_reporte_presencia_sku_presencia_stock_body, null);
				((TextView) row.findViewById(R.id.tv_sku)).setText(mA.getSku_producto());
				CheckBox ck_presen = (CheckBox) row.findViewById(R.id.ck_presencia);
				datosFila.add(ck_presen);
				EditText et_stock = (EditText) row.findViewById(R.id.et_stock);
				et_stock.setText(mA.getStock());
				et_stock.invalidate();
				et_stock.setSelected(true);
				datosFila.add(et_stock);
				String key = mA.getSku_producto();
				createRow(table, row, et_stock, null, null, null, ck_presen, null, i, ini, mA.getDescripcion(), null, key);
				datosReporte.put(key, datosFila);
				Log.i("ReportesGrillaActivity", "cargando reporte sku_presencia_stock con presencia en: " + mA.getCod_presencia() + " y stock en: " + mA.getStock());
				if (mA.getCod_presencia() != null && mA.getCod_presencia().equals("1")) {
					ck_presen.setChecked(true);
				}
				ck_presen.invalidate();
				ck_presen.setSelected(true);
			}
			table.invalidate();
		} else {
			Toast.makeText(this.context, "No hay productos registrados para este reporte", Toast.LENGTH_SHORT).show();
		}
	}

	public void show_reporte_presencia_tblcluster_preguntamarquecantidad() {
		elementos = (new TblMstClusterController(db)).getElementsForGrid(idCabecera);
		TableLayout table = (TableLayout) view.findViewById(R.id.tl_reporte_presencia_cluster);
		int numElementos = 0;
		// subtitulos = new HashMap<String, String>();
		// TextView tvSubtitulo = (TextView) findViewById(R.id.tv_subtitulo);
		boolean ini = true;
		if (elementos != null && (numElementos = elementos.size()) > 0) {
			datosReporte = new HashMap<String, ArrayList<Object>>();
			for (int i = 0; i < numElementos; i++) {
				final E_TBL_MOV_REP_PRESENCIA mA = (E_TBL_MOV_REP_PRESENCIA) elementos.get(i);
				datosFila = new ArrayList<Object>();
				TableRow row = (TableRow) inflator.inflate(R.layout.ly_reporte_presencia_clusterpreguntamarquecant_body, null);
				((TextView) row.findViewById(R.id.tv_pregunta)).setText(mA.getCod_cluster());
				CheckBox ck_marque = (CheckBox) row.findViewById(R.id.ck_marque);
				datosFila.add(ck_marque);
				ck_marque.setChecked(mA.getCluster() == "1");
				final EditText et_cantidad = (EditText) row.findViewById(R.id.et_cantidad);
				et_cantidad.setText(mA.getCantidad());
				et_cantidad.invalidate();
				et_cantidad.setSelected(true);
				datosFila.add(et_cantidad);

				boolean check = ("1".equalsIgnoreCase(mA.getCluster()) || (mA.getCantidad() != null && !mA.getCantidad().trim().isEmpty()));
				ck_marque.setChecked(check);
				et_cantidad.setEnabled(check);
				String key = mA.getCod_cluster();

				createRow(table, row, et_cantidad, null, null, null, ck_marque, null, i, ini, mA.getDescripcion(), null, "");
				datosReporte.put(key, datosFila);

				ck_marque.setOnCheckedChangeListener(new OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
								if ("1".equalsIgnoreCase(mA.getReq_cantidad())) {
									et_cantidad.setEnabled(isChecked);
									et_cantidad.requestFocus();
									if (!isChecked) {
										et_cantidad.setText("");
									}
								}
							}
						});
				ck_marque.invalidate();
				ck_marque.setSelected(true);
			}
			table.invalidate();
		} else {
			Toast.makeText(this.context, "No hay clusters registrados para este reporte", Toast.LENGTH_SHORT).show();
		}
	}

	// Reporte Precio Cliente Alicorp Canal Autoservicio
	public void show_reporte_precio_alicorp_autoservicio() {
		E_MstMotivoObsController mObsController = new E_MstMotivoObsController(db);

		opciones = mObsController.getMotivoObsByIdReporte(TiposReportes.COD_REP_PRECIO);

		elementos = (new TblMstProductoController(db)).getElementsForGridPrecio(idCabecera);
		TableLayout table = (TableLayout) view.findViewById(R.id.tl_reporte_precio_alicorp_autoservicios);
		int numElementos = 0;
		// subtitulos = new HashMap<String, String>();
		// TextView tvSubtitulo = (TextView) findViewById(R.id.tv_subtitulo);
		boolean ini = true;
		if (elementos != null && (numElementos = elementos.size()) > 0) {
			datosReporte = new HashMap<String, ArrayList<Object>>();
			for (int i = 0; i < numElementos; i++) {

				final E_ReportePrecio mA = (E_ReportePrecio) elementos.get(i);
				datosFila = new ArrayList<Object>();
				TableRow row = (TableRow) inflator.inflate(R.layout.ly_reporte_precio_alicorp_autoservicios_body, null);

				final TextView tv = ((TextView) row.findViewById(R.id.tv_sku));
				tv.setText(mA.getSku_prod());
				final EditText et_ppdv = (EditText) row.findViewById(R.id.et_ppdv);
				et_ppdv.setText(mA.getPrecio_pdv());
				datosFila.add(et_ppdv);
				final EditText et_poferta = (EditText) row.findViewById(R.id.et_poferta);
				et_poferta.setText(mA.getPrecio_oferta());
				datosFila.add(et_poferta);
				final Spinner mObsSpinner = (Spinner) row.findViewById(R.id.spinnerMObs);

				if (opciones != null) {
					String[] motivos = new String[opciones.size()];
					for (int j = 0; j < opciones.size(); j++) {
						E_MotivoObservacion motivo = (E_MotivoObservacion) opciones.get(j);
						motivos[j] = motivo.getDesc_motivo();
					}
					ArrayAdapter<String> adaptadorObs = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, motivos);
					adaptadorObs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

					mObsSpinner.setAdapter(adaptadorObs);
					if (mA.getCod_motivo_obs() != null) {
						for (int k = 0; k < opciones.size(); k++) {
							E_MotivoObservacion motivo = (E_MotivoObservacion) opciones.get(k);
							if (mA.getCod_motivo_obs().equals(motivo.getCod_motivo())) {
								mObsSpinner.setSelection(k);
								break;
							}
						}
					} else {
						mObsSpinner.setSelection(-1);
						if (mA.getPrecio_reventa() != null && mA.getPrecio_oferta() != null) {
							mObsSpinner.setEnabled(false);
						}
					}
					mObsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
								public void onItemSelected(AdapterView<?> parent, android.view.View v, int position, long id) {
									E_MotivoObservacion motivo = (E_MotivoObservacion) opciones.get(position);
									if (motivo.getCod_motivo().equalsIgnoreCase("F")) {
										et_ppdv.setText("");
										et_poferta.setText("");
										et_ppdv.setEnabled(false);
										et_poferta.setEnabled(false);
									} else {
										et_ppdv.setEnabled(true);
										et_poferta.setEnabled(true);
									}
								}

								public void onNothingSelected(AdapterView<?> parent) {
								}
							});
				}

				et_ppdv.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						// TODO Auto-generated method stub
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
						// TODO Auto-generated method stub
					}

					@Override
					public void afterTextChanged(Editable s) {
						// TODO Auto-generated method stub
					}
				});

				et_poferta.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						// TODO Auto-generated method stub
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
						// TODO Auto-generated method stub
					}

					@Override
					public void afterTextChanged(Editable s) {
						// TODO Auto-generated method stub
					}
				});

				datosFila.add(mObsSpinner);

				String key = mA.getSku_prod();
				createRow(table, row, et_ppdv, et_poferta, null, null, null, null, i, ini, mA.getDesc_prod(), mObsSpinner, key);
				datosReporte.put(key, datosFila);
			}
		} else {
			Toast.makeText(this.context, "No hay productos registrados para este reporte", Toast.LENGTH_SHORT).show();
		}
	}

	// Reporte Quiebre Cliente Alicorp Canal Autoservicio
	public void show_reporte_quiebre_alicorp_autoservicio() {
		E_MstMotivoObsController mObsController = new E_MstMotivoObsController(db);
		opciones = mObsController.getMotivoObsByIdReporte(TiposReportes.COD_REP_QUIEBRE);
		elementos = (new TblMstProductoController(db)).getElementsForGridQuiebre(idCabecera);
		TableLayout table = (TableLayout) view.findViewById(R.id.tl_reporte_quiebre_alicorp_autoservicios);
		int numElementos = 0;
		// subtitulos = new HashMap<String, String>();
		// TextView tvSubtitulo = (TextView) findViewById(R.id.tv_subtitulo);
		boolean ini = true;
		if (elementos != null && (numElementos = elementos.size()) > 0) {
			datosReporte = new HashMap<String, ArrayList<Object>>();
			for (int i = 0; i < numElementos; i++) {

				E_ReporteQuiebre mA = (E_ReporteQuiebre) elementos.get(i);
				datosFila = new ArrayList<Object>();
				TableRow row = (TableRow) inflator.inflate(R.layout.ly_reporte_quiebre_alicorp_autoservicios_body, null);

				((TextView) row.findViewById(R.id.tv_sku)).setText(mA.getSku_prod());

				final Spinner mObsSpinner = (Spinner) row.findViewById(R.id.spinnerMObs);

				if (opciones != null) {
					String[] motivos = new String[opciones.size()];
					for (int j = 0; j < opciones.size(); j++) {
						E_MotivoObservacion motivo = (E_MotivoObservacion) opciones.get(j);
						motivos[j] = motivo.getDesc_motivo();
					}
					ArrayAdapter<String> adaptadorObs = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, motivos);
					adaptadorObs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

					mObsSpinner.setAdapter(adaptadorObs);
					if (mA.getCod_motivo_quiebre() != null) {
						for (int k = 0; k < opciones.size(); k++) {
							E_MotivoObservacion motivo = (E_MotivoObservacion) opciones.get(k);
							if (mA.getCod_motivo_quiebre().equals(motivo.getCod_motivo())) {
								mObsSpinner.setSelection(k);
								break;
							}
						}
					} else {
						mObsSpinner.setSelection(-1);
					}
				}
				datosFila.add(mObsSpinner);

				String key = mA.getSku_prod();
				createRow(table, row, null, null, null, null, null, null, i, ini, mA.getDesc_prod(), mObsSpinner, key);
				datosReporte.put(key, datosFila);
			}
		} else {
			Toast.makeText(this.context, "No hay productos registrados para este reporte", Toast.LENGTH_SHORT).show();
		}
	}

	// Reporte Precio Cliente Alicorp Canal Mayoristas
	public void show_reporte_precio_alicorp_mayoristas() {
		E_MstMotivoObsController mObsController = new E_MstMotivoObsController(db);
		opciones = mObsController.getMotivoObsByIdReporte(TiposReportes.COD_REP_PRECIO);
		elementos = (new TblMstProductoController(db)).getElementsForGridPrecio(idCabecera);
		TableLayout table = (TableLayout) view.findViewById(R.id.tl_reporte_precio_alicorp_mayoristas);
		int numElementos = 0;
		// subtitulos = new HashMap<String, String>();
		// TextView tvSubtitulo = (TextView) findViewById(R.id.tv_subtitulo);
		boolean ini = true;
		if (elementos != null && (numElementos = elementos.size()) > 0) {
			datosReporte = new HashMap<String, ArrayList<Object>>();
			for (int i = 0; i < numElementos; i++) {

				E_ReportePrecio mA = (E_ReportePrecio) elementos.get(i);
				datosFila = new ArrayList<Object>();
				TableRow row = (TableRow) inflator.inflate(R.layout.ly_reporte_precio_alicorp_mayoristas_body, null);

				final TextView tv = ((TextView) row.findViewById(R.id.tv_sku));
				tv.setText(mA.getSku_prod());

				final EditText et_pmayor = (EditText) row.findViewById(R.id.et_plista);
				et_pmayor.setText(mA.getPrecio_mayorista());
				datosFila.add(et_pmayor);

				final EditText et_preventa = (EditText) row.findViewById(R.id.et_preventa);
				et_preventa.setText(mA.getPrecio_reventa());
				datosFila.add(et_preventa);

				final EditText et_poferta = (EditText) row.findViewById(R.id.et_poferta);
				et_poferta.setText(mA.getPrecio_oferta());
				datosFila.add(et_poferta);

				final Spinner mObsSpinner = (Spinner) row.findViewById(R.id.spinnerMObs);

				if (opciones != null) {
					String[] motivos = new String[opciones.size()];
					for (int j = 0; j < opciones.size(); j++) {
						E_MotivoObservacion motivo = (E_MotivoObservacion) opciones.get(j);
						motivos[j] = motivo.getDesc_motivo();
					}
					ArrayAdapter<String> adaptadorObs = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, motivos);
					adaptadorObs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

					mObsSpinner.setAdapter(adaptadorObs);
					if (mA.getCod_motivo_obs() != null) {
						for (int k = 0; k < opciones.size(); k++) {
							E_MotivoObservacion motivo = (E_MotivoObservacion) opciones.get(k);
							if (mA.getCod_motivo_obs().equals(motivo.getCod_motivo())) {
								mObsSpinner.setSelection(k);
								break;
							}
						}
					} else {
						mObsSpinner.setSelection(-1);
					}

					mObsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
								public void onItemSelected(AdapterView<?> parent, android.view.View v, int position, long id) {
									if (position > 0 && ((E_MotivoObservacion) opciones.get(position)).getCod_motivo().equalsIgnoreCase("F")) {
										et_pmayor.setText("");
										et_preventa.setText("");
										et_poferta.setText("");
										et_pmayor.setEnabled(false);
										et_preventa.setEnabled(false);
										et_poferta.setEnabled(false); // tv.requestFocus();
									} else {
										et_pmayor.setEnabled(true);
										et_preventa.setEnabled(true);
										et_poferta.setEnabled(true);
										et_pmayor.requestFocus();
									}
								}

								public void onNothingSelected(AdapterView<?> parent) {
								}
							});
				}

				datosFila.add(mObsSpinner);
				String key = mA.getSku_prod();
				createRow(table, row, et_pmayor, et_preventa, et_poferta, null, null, null, i, ini, mA.getDesc_prod(), mObsSpinner, "");
				datosReporte.put(key, datosFila);
			}
		} else {
			Toast.makeText(this.context, "No hay productos registrados para este reporte", Toast.LENGTH_SHORT).show();
		}
	}

	// Reporte Stock Cliente Alicorp Canal Mayoristas
	public void show_reporte_stock_alicorp_mayoristas() {
		E_MstMotivoObsController mObsController = new E_MstMotivoObsController(db);
		opciones = mObsController.getMotivoObsByIdReporte(TiposReportes.COD_REP_STOCK);
		elementos = (new TblMstFamiliaController(db)).getElementsForGridStock(idCabecera);
		TableLayout table = (TableLayout) view.findViewById(R.id.tl_reporte_stock_alicorp_mayoristas);
		int numElementos = 0;
		// subtitulos = new HashMap<String, String>();
		// TextView tvSubtitulo = (TextView) findViewById(R.id.tv_subtitulo);
		boolean ini = true;
		if (elementos != null && (numElementos = elementos.size()) > 0) {
			datosReporte = new HashMap<String, ArrayList<Object>>();
			for (int i = 0; i < numElementos; i++) {

				E_ReporteStock mA = (E_ReporteStock) elementos.get(i);
				datosFila = new ArrayList<Object>();
				TableRow row = (TableRow) inflator.inflate(R.layout.ly_reporte_stock_alicorp_mayoristas_body,null);

				((TextView) row.findViewById(R.id.tv_codigo)).setText(mA.getCod_familia());

				final EditText et_stock = (EditText) row.findViewById(R.id.et_stock);
				Log.i("RGA", "mA.getStock() " + mA.getStock());
				et_stock.setText(mA.getStock());
				datosFila.add(et_stock);

				final Spinner mObsSpinner = (Spinner) row.findViewById(R.id.spinnerMObs);

				if (opciones != null) {
					String[] motivos = new String[opciones.size()];
					for (int j = 0; j < opciones.size(); j++) {
						E_MotivoObservacion motivo = (E_MotivoObservacion) opciones.get(j);
						motivos[j] = motivo.getDesc_motivo();
					}
					ArrayAdapter<String> adaptadorObs = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, motivos);
					adaptadorObs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

					mObsSpinner.setAdapter(adaptadorObs);
					if (mA.getCod_motivo_obs() != null) {
						for (int k = 0; k < opciones.size(); k++) {
							E_MotivoObservacion motivo = (E_MotivoObservacion) opciones.get(k);
							if (mA.getCod_motivo_obs().equals(motivo.getCod_motivo())) {
								mObsSpinner.setSelection(k);
								break;
							}
						}
					} else {
						mObsSpinner.setSelection(-1);
						if (mA.getStock() != null) {
							mObsSpinner.setEnabled(false);
						}
					}
					mObsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
								public void onItemSelected(AdapterView<?> parent, android.view.View v, int position, long id) {

									E_MotivoObservacion motivo = (E_MotivoObservacion) opciones.get(position);
									if (motivo.getCod_motivo().equalsIgnoreCase("Q")) {
										et_stock.setText("0");
										et_stock.setEnabled(false);
									} else if (motivo.getCod_motivo().equalsIgnoreCase("F")) {
										et_stock.setText("");
										et_stock.setEnabled(false);
									} else {
										if (et_stock.getText().toString().trim().equals("0")) {
											et_stock.setText("");
										}
										et_stock.setEnabled(true);
									}
								}

								public void onNothingSelected(AdapterView<?> parent) {
								}
							});
				}

				et_stock.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						// TODO Auto-generated method stub
						if (et_stock.getText().toString().trim().equals("0")) {
							for (int k = 0; k < opciones.size(); k++) {
								E_MotivoObservacion motivo = (E_MotivoObservacion) opciones.get(k);
								if (motivo.getCod_motivo().equalsIgnoreCase("Q")) {
									mObsSpinner.setSelection(k);
									break;
								}
							}

							mObsSpinner.setEnabled(true);
						}
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
						// TODO Auto-generated method stub
					}

					@Override
					public void afterTextChanged(Editable s) {
						// TODO Auto-generated method stub
					}
				});

				datosFila.add(mObsSpinner);

				String key = mA.getCod_familia();
				createRow(table, row, et_stock, null, null, null, null, null, i, ini, mA.getDesc_prod(), mObsSpinner, key);

				datosReporte.put(key, datosFila);
			}
		} else {
			Toast.makeText(this.context, "No hay datos registrados para este reporte", Toast.LENGTH_SHORT).show();
		}
	}

	// Reporte Precio PVP Cliente SanFernando Canal AAVV
	public void show_reporte_precio_pvp_sanfernando_aavv() {

		elementos = (new TblMstProductoController(db)).getElementsForGridPrecio(idCabecera);
		TableLayout table = (TableLayout) view.findViewById(R.id.tl_reporte_precio_pvp_sanfernando_aavv);
		int numElementos = 0;
		// subtitulos = new HashMap<String, String>();
		// TextView tvSubtitulo = (TextView) findViewById(R.id.tv_subtitulo);
		boolean ini = true;
		if (elementos != null && (numElementos = elementos.size()) > 0) {
			datosReporte = new HashMap<String, ArrayList<Object>>();
			for (int i = 0; i < numElementos; i++) {

				E_ReportePrecio mA = (E_ReportePrecio) elementos.get(i);
				datosFila = new ArrayList<Object>();
				TableRow row = (TableRow) inflator.inflate(R.layout.ly_reporte_precio_pvp_sanfernando_aavv_body, null);

				((TextView) row.findViewById(R.id.tv_sku)).setText(mA.getSku_prod());

				final EditText et_pvp = (EditText) row.findViewById(R.id.et_ppvp);
				et_pvp.setFilters(new InputFilter[] {new DecimalFilter(15, 2)});
				et_pvp.setFilters(new InputFilter[] {new CustomDigitWatcherAAVVSanFernando(et_pvp)});
				et_pvp.setText(mA.getPrecio_lista());
				datosFila.add(et_pvp);

				String key = mA.getSku_prod();
				createRow(table, row, et_pvp, null, null, null, null, null, i, ini, mA.getDesc_prod(), null, key);
				datosReporte.put(key, datosFila);
			}
		} else {
			Toast.makeText(this.context, "No hay productos registrados para este reporte", Toast.LENGTH_SHORT).show();
		}
	}

	// Reporte Precio PDV_PDV Cliente SanFernando Canal AAVV
	public void show_reporte_precio_pvd_pdv_sanfernando_aavv() {

		elementos = (new TblMstProductoController(db)).getElementsForGridPrecio(idCabecera);
		opciones = (new E_tblMstTipoPrecioController(db)).getAll();
		TableLayout table = (TableLayout) view.findViewById(R.id.tl_reporte_precio_pdv_pdv_sanfernando_aavv);
		int numElementos = 0;
		// subtitulos = new HashMap<String, String>();
		// TextView tvSubtitulo = (TextView) findViewById(R.id.tv_subtitulo);
		boolean ini = true;
		if (elementos != null && (numElementos = elementos.size()) > 0) {
			datosReporte = new HashMap<String, ArrayList<Object>>();
			for (int i = 0; i < numElementos; i++) {

				E_ReportePrecio mA = (E_ReportePrecio) elementos.get(i);
				datosFila = new ArrayList<Object>();
				TableRow row = (TableRow) inflator.inflate(R.layout.ly_reporte_precio_pdv_pdv_sanfernando_aavv_body, null);

				((TextView) row.findViewById(R.id.tv_sku)).setText(mA.getSku_prod());

				final EditText et_pvp = (EditText) row.findViewById(R.id.et_ppdv);
				et_pvp.setFilters(new InputFilter[] { new DecimalFilter(15, 2) });
				et_pvp.setText(mA.getPrecio_pdv());
				datosFila.add(et_pvp);
				
				Spinner sp_tipo = (Spinner)row.findViewById(R.id.sp_tipo);

				if (opciones != null) {
					String[] s_opciones = new String[opciones.size()];
					for (int j = 0; j < opciones.size(); j++) {
						E_TipoPrecioPDV tipo = (E_TipoPrecioPDV) opciones.get(j);
						s_opciones[j] = tipo.getDescripcion();
					}
					ArrayAdapter<String> adaptador = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, s_opciones);
					adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

					sp_tipo.setAdapter(adaptador);
					if (mA.getSku_prod() != null) {
						for (int k = 0; k < opciones.size(); k++) {
							E_TipoPrecioPDV opcion = (E_TipoPrecioPDV) opciones.get(k);
							if (mA.getCod_tipo_precio()!=null && mA.getCod_tipo_precio().equals(opcion.getCodTipoPrecio())) {
								sp_tipo.setSelection(k);
								break;
							}
						}
					} else {
						sp_tipo.setSelection(-1);
					}
					sp_tipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
								public void onItemSelected(AdapterView<?> parent, android.view.View v, int position, long id) {

								}

								public void onNothingSelected(AdapterView<?> parent) {
								}
							});
				}
				
				E_TblMovReporteCab cab = new E_TblMovReporteCabController(db).getByIdCabecera(idCabecera);
				E_TblFiltrosApp filtros =  new TblMstMovFiltrosAppController(db).getById(cab.getId_filtros_app());
				if(filtros!=null){
					if(filtros.getCod_categoria().equalsIgnoreCase(TiposReportes.CATEG_POLLO)){
						sp_tipo.setEnabled(true);
					}else{
						sp_tipo.setEnabled(false);
					}
				}
				datosFila.add(sp_tipo);
				String key = mA.getSku_prod();
				createRow(table, row, et_pvp, null, null, null, null, null, i, ini, mA.getDesc_prod(), sp_tipo, key);
				datosReporte.put(key, datosFila);
			}
		} else {
			Toast.makeText(this.context, "No hay productos registrados para este reporte", Toast.LENGTH_SHORT).show();
		}
	}

	// Reporte Precio PDV_OBS Cliente SanFernando Canal AAVV
	public void show_reporte_precio_pvd_obs_sanfernando_aavv() {
		elementos = (new TblMstObservacionController(db)).getElementsForGridPrecio(idCabecera);

		TableLayout table = (TableLayout) view.findViewById(R.id.tl_reporte_precio_pdv_obs_sanfernando_aavv);
		int numObservaciones = 0;
		// subtitulos = new HashMap<String, String>();
		// TextView tvSubtitulo = (TextView) findViewById(R.id.tv_subtitulo);
		boolean ini = true;
		if (elementos != null && (numObservaciones = elementos.size()) > 0) {
			datosReporte = new HashMap<String, ArrayList<Object>>();
			for (int i = 0; i < numObservaciones; i++) {
				final E_ReportePrecio mA = (E_ReportePrecio) elementos.get(i);
				datosFila = new ArrayList<Object>();
				TableRow row = (TableRow) inflator.inflate(R.layout.ly_reporte_precio_pdv_obs_sanfernando_aavv_body, null);
				((TextView) row.findViewById(R.id.tv_codigo)).setText(mA.getCod_motivo_obs());
				CheckBox ck_observacion = (CheckBox) row.findViewById(R.id.ck_observacion);

				String key = mA.getCod_motivo_obs();

				createRow(table, row, null, null, null, null, ck_observacion, null, i, ini, mA.getDesc_prod(), null, key);

				datosFila.add(ck_observacion);
				if (mA.getObservacion() != null && mA.getObservacion().equals("1")) {
					ck_observacion.setChecked(true);
				}
				ck_observacion.invalidate();
				ck_observacion.setSelected(true);
				datosReporte.put(key, datosFila);
			}
			table.invalidate();
		} else {
			Toast.makeText(this.context, "No hay observaciones registradas para este reporte", Toast.LENGTH_SHORT).show();
		}
	}

	// Subreporte Auditoria SanFernando Canal AAVV
	public void show_reporte_auditoria_sanfernando_AAVV(int tipoMaterial) {
		elementos = (new TblMstMaterialApoyoController(db)).getElementsForAuditoriaGrid(tipoMaterial, idCabecera);
		TableLayout table = (TableLayout) view.findViewById(R.id.tl_reporte_auditoria_sanfernando_aavv);
		int numElementos = 0;
		boolean ini = true;
		if (elementos != null && (numElementos = elementos.size()) > 0) {
			datosReporte = new HashMap<String, ArrayList<Object>>();
			for (int i = 0; i < numElementos; i++) {

				final E_ReporteAuditoria mA = (E_ReporteAuditoria) elementos.get(i);
				datosFila = new ArrayList<Object>();
				TableRow row = (TableRow) inflator.inflate(R.layout.ly_reporte_auditoria_sanfernando_aavv_body, null);

				((TextView) row.findViewById(R.id.tv_pregunta)).setText(mA.getCod_mat_apoyo());

				CheckBox ck_marque = (CheckBox) row.findViewById(R.id.ck_marque);
				ck_marque.invalidate();
				ck_marque.setSelected(true);
				datosFila.add(ck_marque);

				final EditText et_cantidad = (EditText) row.findViewById(R.id.et_cantidad);
				datosFila.add(et_cantidad);

				String key = mA.getCod_mat_apoyo();

				createRow(table, row, et_cantidad, null, null, null, ck_marque, null, i, ini, mA.getDescripcion(), null, "");
				datosReporte.put(key, datosFila);

				if (mA.getHasCantidad().equalsIgnoreCase("1")) {
					et_cantidad.setEnabled(true);
					ck_marque.setEnabled(false);
					et_cantidad.invalidate();
					et_cantidad.setSelected(true);
					if (mA.getCantidad() != null && !mA.getCantidad().isEmpty()) {
						et_cantidad.setText(mA.getCantidad());
					}
				} else {
					if (mA.getHasCheck().equalsIgnoreCase("1")) {
						ck_marque.setEnabled(true);
						et_cantidad.setEnabled(false);
						et_cantidad.setText("");
						if (mA.getMat_apoyo_Check() != null
								&& mA.getMat_apoyo_Check().equals("1")) {
							ck_marque.setChecked(true);
						}
					}

				}
			}
		} else {
			Toast.makeText(this.context, "No hay materiales registrados para este reporte", Toast.LENGTH_SHORT).show();
		}
	}

	// Reporte Estrella Cliente SanFernando Canal AAVV
	public void show_reporte_estrella_sanfernando_aavv() {
		elementos = (new PuntoVentaController(db)).getElementsForGridEstrella(TiposReportes.COD_REP_ESTRELLA, DatosManager.getInstancia().getVisita().getIdPuntoVenta());
		TableLayout table = (TableLayout) view.findViewById(R.id.tl_reporte_estrella_sanfernando_aavv);
		int numElementos = 0;
		// subtitulos = new HashMap<String, String>();
		// TextView tvSubtitulo = (TextView) findViewById(R.id.tv_subtitulo);
		boolean ini = true;
		if (elementos != null && (numElementos = elementos.size()) > 0) {
			datosReporte = new HashMap<String, ArrayList<Object>>();
			for (int i = 0; i < numElementos; i++) {

				final E_ReporteEstrella mA = (E_ReporteEstrella) elementos.get(i);
				datosFila = new ArrayList<Object>();
				final TableRow row = (TableRow) inflator.inflate(R.layout.ly_reporte_estrella_sanfernando_aavv_body,null);
				((TextView) row.findViewById(R.id.tv_cod)).setText(mA.getCod_estrella());

				final int index = i;
				final TextView tv_valor = (TextView) row.findViewById(R.id.tv_valor);
				tv_valor.setText(mA.getValor_estrella());
				datosFila.add(tv_valor);
				String key = mA.getCod_estrella();
				datosReporte.put(key, datosFila);
				createRowTextViews(table, row, tv_valor, index, ini,mA.getDesc_estrella(), key);
			}
		} else {
			Toast.makeText(this.context, "No hay estrellas registradas para este reporte", Toast.LENGTH_SHORT).show();
		}
	}

	/*
	 * // Reporte Precio Cliente SanFernando Canal AAVV public void
	 * show_reporte_precio_sanfernando_aavv() {
	 * 
	 * elementos = (new TblMstProductoController(db))
	 * .getElementsForGridPrecio(idCabecera); TableLayout table = (TableLayout)
	 * view .findViewById(R.id.tl_reporte_precio_sanfernando_aavv); int
	 * numElementos = 0; // subtitulos = new HashMap<String, String>(); //
	 * TextView tvSubtitulo = (TextView) findViewById(R.id.tv_subtitulo);
	 * boolean ini = true; if (elementos != null && (numElementos =
	 * elementos.size()) > 0) { datosReporte = new HashMap<String,
	 * ArrayList<Object>>(); for (int i = 0; i < numElementos; i++) {
	 * 
	 * E_ReportePrecio mA = (E_ReportePrecio) elementos.get(i); datosFila = new
	 * ArrayList<Object>(); TableRow row = (TableRow) inflator.inflate(
	 * R.layout.ly_reporte_precio_sanfernando_aavv_body, null);
	 * 
	 * ((TextView) row.findViewById(R.id.tv_sku)).setText(mA .getSku_prod());
	 * 
	 * final EditText et_pmin = (EditText) row .findViewById(R.id.et_pmin);
	 * et_pmin.setText(mA.getPrecio_min()); datosFila.add(et_pmin);
	 * 
	 * final EditText et_pmax = (EditText) row .findViewById(R.id.et_pmax);
	 * et_pmax.setText(mA.getPrecio_max()); datosFila.add(et_pmax);
	 * 
	 * String key = mA.getSku_prod(); createRow(table, row, et_pmin, et_pmax,
	 * null, null, null, null, i, ini, mA.getDesc_prod(), null, key);
	 * datosReporte.put(key, datosFila); } } else { Toast.makeText(this.context,
	 * "No hay productos registrados para este reporte",
	 * Toast.LENGTH_SHORT).show(); } }
	 * 
	 * // Reporte Ventas Cliente SanFernando Canal AAVV public void
	 * show_reporte_ventas_sanfernando_aavv() { elementos = (new
	 * TblMstProductoController(db)) .getElementsForGridVentas(idCabecera);
	 * TableLayout table = (TableLayout) view
	 * .findViewById(R.id.tl_reporte_ventas_sanfernando_aavv); int numElementos
	 * = 0; // subtitulos = new HashMap<String, String>(); // TextView
	 * tvSubtitulo = (TextView) findViewById(R.id.tv_subtitulo); boolean ini =
	 * true; if (elementos != null && (numElementos = elementos.size()) > 0) {
	 * datosReporte = new HashMap<String, ArrayList<Object>>(); for (int i = 0;
	 * i < numElementos; i++) {
	 * 
	 * E_ReporteStock mA = (E_ReporteStock) elementos.get(i); datosFila = new
	 * ArrayList<Object>(); TableRow row = (TableRow) inflator.inflate(
	 * R.layout.ly_reporte_ventas_sanfernando_aavv_body, null);
	 * 
	 * ((TextView) row.findViewById(R.id.tv_sku)).setText(mA .getSku_prod());
	 * 
	 * final EditText et_pedido = (EditText) row .findViewById(R.id.et_pedido);
	 * et_pedido.setText(mA.getPedido()); datosFila.add(et_pedido);
	 * 
	 * final EditText et_ingeso = (EditText) row .findViewById(R.id.et_ingreso);
	 * et_ingeso.setText(mA.getStock()); datosFila.add(et_ingeso);
	 * 
	 * final EditText et_venta = (EditText) row .findViewById(R.id.et_venta);
	 * et_venta.setText(mA.getVenta()); datosFila.add(et_venta);
	 * 
	 * String key = mA.getSku_prod(); createRow(table, row, et_pedido,
	 * et_ingeso, et_venta, null, null, null, i, ini, mA.getDesc_prod(), null,
	 * key);
	 * 
	 * datosReporte.put(key, datosFila); } } else { Toast.makeText(this.context,
	 * "No hay productos registrados para este reporte",
	 * Toast.LENGTH_SHORT).show(); } }
	 */
	// Reporte Precio Cliente SanFernando Canal Moderno
	public void show_reporte_precio_sanfernando_moderno() {
		elementos = (new TblMstProductoController(db)).getElementsForGridPrecio(idCabecera);
		TableLayout table = (TableLayout) view.findViewById(R.id.tl_reporte_precio_sanfernando_moderno);
		int numElementos = 0;
		boolean ini = true;
		if (elementos != null && (numElementos = elementos.size()) > 0) {
			datosReporte = new HashMap<String, ArrayList<Object>>();
			for (int i = 0; i < numElementos; i++) {

				E_ReportePrecio mA = (E_ReportePrecio) elementos.get(i);
				datosFila = new ArrayList<Object>();
				TableRow row = (TableRow) inflator.inflate(R.layout.ly_reporte_precio_sanfernando_moderno_body, null);

				((TextView) row.findViewById(R.id.tv_sku)).setText(mA.getSku_prod());

				final EditText et_poferta = (EditText) row.findViewById(R.id.et_plista);
				et_poferta.setText(mA.getPrecio_oferta());
				datosFila.add(et_poferta);

				final EditText et_pregular = (EditText) row.findViewById(R.id.et_preventa);
				et_pregular.setText(mA.getPrecio_regular());
				datosFila.add(et_pregular);

				String key = mA.getSku_prod();
				createRow(table, row, et_poferta, et_pregular, null, null, null, null, i, ini, mA.getDesc_prod(), null, key);

				datosReporte.put(key, datosFila);
			}
		} else {
			Toast.makeText(this.context, "No hay productos registrados para este reporte", Toast.LENGTH_SHORT).show();
		}
	}

	// Reporte Ingresos Cliente SanFernando Canal Moderno
	public void show_reporte_ingresos_sanfernando_moderno(boolean isPropio) {
		E_MstMotivoObsController mObsController = new E_MstMotivoObsController(db);
		opciones = mObsController.getMotivoObsByIdReporte(TiposReportes.COD_REP_STOCK_INGRESO);
		elementos = (new TblMstProductoController(db)).getElementsForGridIngresoStock(idCabecera);
		TableLayout table = (TableLayout) view.findViewById(R.id.tl_reporte_ingreso_sanfernando_moderno);
		int numElementos = 0;
		// subtitulos = new HashMap<String, String>();
		// TextView tvSubtitulo = (TextView) findViewById(R.id.tv_subtitulo);
		boolean ini = true;
		if (elementos != null && (numElementos = elementos.size()) > 0) {
			datosReporte = new HashMap<String, ArrayList<Object>>();
			for (int i = 0; i < numElementos; i++) {

				E_ReporteStock mA = (E_ReporteStock) elementos.get(i);
				datosFila = new ArrayList<Object>();
				TableRow row = (TableRow) inflator.inflate(R.layout.ly_reporte_ingreso_sanfernando_moderno_body, null);

				((TextView) row.findViewById(R.id.tv_sku)).setText(mA.getSku_prod());

				final EditText et_exhibicion = (EditText) row.findViewById(R.id.et_exhibicion);
				et_exhibicion.setText(mA.getExhibicion());
				datosFila.add(et_exhibicion);

				final EditText et_camara = (EditText) row.findViewById(R.id.et_camara);
				et_camara.setText(mA.getCamara());
				datosFila.add(et_camara);

				final Spinner mObsSpinner = (Spinner) row.findViewById(R.id.spinnerMotivo);

				if (opciones != null) {
					String[] motivos = new String[opciones.size()];
					for (int j = 0; j < opciones.size(); j++) {
						E_MotivoObservacion motivo = (E_MotivoObservacion) opciones.get(j);
						motivos[j] = motivo.getDesc_motivo();
					}
					ArrayAdapter<String> adaptadorObs = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, motivos);
					adaptadorObs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

					mObsSpinner.setAdapter(adaptadorObs);
					if (mA.getCod_motivo_obs() != null) {
						for (int k = 0; k < opciones.size(); k++) {
							E_MotivoObservacion motivo = (E_MotivoObservacion) opciones.get(k);
							if (mA.getCod_motivo_obs().equals(motivo.getCod_motivo())) {
								mObsSpinner.setSelection(k);
								break;
							}
						}
					} else {
						mObsSpinner.setSelection(-1);
						if (mA.getStock() != null) {
							mObsSpinner.setEnabled(false);
						}
					}
					mObsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
								public void onItemSelected(AdapterView<?> parent, android.view.View v, int position, long id) {
									if (position > 0) {
										et_exhibicion.setEnabled(false);
										et_camara.setEnabled(false);
									} else {
										et_exhibicion.setEnabled(true);
										et_camara.setEnabled(true);
									}
								}

								public void onNothingSelected(AdapterView<?> parent) {
								}
							});
				}

				et_exhibicion.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						// TODO Auto-generated method stub
						if (et_exhibicion.getText().toString().trim().equals("") && et_camara.getText().toString().trim().equals("")) {
							mObsSpinner.setEnabled(true);
						} else {
							mObsSpinner.setSelection(0);
							mObsSpinner.setEnabled(false);
						}
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
						// TODO Auto-generated method stub
					}

					@Override
					public void afterTextChanged(Editable s) {
						// TODO Auto-generated method stub
					}
				});

				et_camara.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						// TODO Auto-generated method stub
						if (et_exhibicion.getText().toString().trim().equals("") && et_camara.getText().toString().trim().equals("")) {
							mObsSpinner.setEnabled(true);
						} else {
							mObsSpinner.setSelection(0);
							mObsSpinner.setEnabled(false);
						}
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
						// TODO Auto-generated method stub
					}

					@Override
					public void afterTextChanged(Editable s) {
						// TODO Auto-generated method stub
					}
				});
				final EditText et_ingresos = (EditText) row.findViewById(R.id.et_ingresos);
				et_ingresos.setText(mA.getStock());

				datosFila.add(mObsSpinner);
				datosFila.add(et_ingresos);

				String key = mA.getSku_prod();
				createRow(table, row, et_exhibicion, et_camara, et_ingresos, null, null, null, i, ini, mA.getDesc_prod(), mObsSpinner, key);

				datosReporte.put(key, datosFila);
			}
		} else {
			Toast.makeText(this.context, "No hay productos registrados para este reporte", Toast.LENGTH_SHORT).show();
		}
	}

	// Reporte Impulso Cliente SanFernando Canal Moderno
	public void show_reporte_impulso_sanfernando_moderno(boolean isPropio) {
		elementos = (new TblMstProductoController(db)).getElementsForGridImpulso(idCabecera);
		TableLayout table = (TableLayout) view.findViewById(R.id.tl_reporte_impulso_sanfernando_moderno);
		int numElementos = 0;
		// subtitulos = new HashMap<String, String>();
		// TextView tvSubtitulo = (TextView) findViewById(R.id.tv_subtitulo);
		boolean ini = true;
		if (elementos != null && (numElementos = elementos.size()) > 0) {
			datosReporte = new HashMap<String, ArrayList<Object>>();
			for (int i = 0; i < numElementos; i++) {

				E_ReporteImpulso mA = (E_ReporteImpulso) elementos.get(i);
				datosFila = new ArrayList<Object>();
				TableRow row = (TableRow) inflator.inflate(R.layout.ly_reporte_impulso_sanfernando_moderno_body,null);

				((TextView) row.findViewById(R.id.tv_sku)).setText(mA.getSku_prod());

				final EditText et_ingresos = (EditText) row.findViewById(R.id.et_ingresos);
				et_ingresos.setText(mA.getIngreso());
				datosFila.add(et_ingresos);

				final EditText et_stockfinal = (EditText) row.findViewById(R.id.et_stockfinal);
				et_stockfinal.setText(mA.getStock_final());
				datosFila.add(et_stockfinal);

				String key = mA.getSku_prod();
				createRow(table, row, et_ingresos, et_stockfinal, null, null,null, null, i, ini, mA.getDesc_prod(), null, key);

				datosReporte.put(key, datosFila);
			}
		} else {
			Toast.makeText(this.context,"No hay productos registrados para este reporte",Toast.LENGTH_SHORT).show();
		}
	}



	// Reporte Entrega Materiales Cliente SanFernando Canal Tradicional
	public void show_reporte_entrega_materiales_sanfernando_tradicional() {
		elementos = (new TblMstMaterialApoyoController(db)).getElementsForEntregaMatGrid(idCabecera);
		TableLayout table = (TableLayout) view.findViewById(R.id.tl_reporte_entrega_materiales);
		int numElementos = 0;
		boolean ini = true;
		if (elementos != null && (numElementos = elementos.size()) > 0) {
			datosReporte = new HashMap<String, ArrayList<Object>>();
			for (int i = 0; i < numElementos; i++) {

				E_TblMovRepMaterialDeApoyo mA = (E_TblMovRepMaterialDeApoyo) elementos.get(i);

				datosFila = new ArrayList<Object>();
				TableRow row = (TableRow) inflator.inflate(R.layout.ly_reporte_entrega_materiales_body, null);

				((TextView) row.findViewById(R.id.tv_sku)).setText(mA.getCod_marial_apoyo());

				final EditText et_cantidad = (EditText) row.findViewById(R.id.et_cantidad);
				et_cantidad.setText(mA.getCantidad());
				datosFila.add(et_cantidad);

				String key = mA.getCod_marial_apoyo();
				createRow(table, row, et_cantidad, null, null, null, null, null, i, ini, mA.getDescripcion(), null, key);

				datosReporte.put(key, datosFila);
			}
		} else {
			Toast.makeText(this.context, "No hay materiales pop registrados para este reporte", Toast.LENGTH_SHORT).show();
		}
	}


	// Reporte Potencial - Revestimiento Cliente SanFernando Canal AAVV
	public void show_reporte_potencial_revest_sanfernando_aavv() {
		elementos = (new TblMstMaterialApoyoController(db)).getElementsForPotencialRevestimientoGrid(idCabecera);
		TableLayout table = (TableLayout) view.findViewById(R.id.tl_reporte_entrega_materiales);
		int numElementos = 0;
		boolean ini = true;
		if (elementos != null && (numElementos = elementos.size()) > 0) {
			datosReporte = new HashMap<String, ArrayList<Object>>();
			for (int i = 0; i < numElementos; i++) {

				E_ReportePotencial mA = (E_ReportePotencial) elementos.get(i);

				datosFila = new ArrayList<Object>();
				TableRow row = (TableRow) inflator.inflate(R.layout.ly_reporte_potencial_sanfdo_body, null);

				((TextView) row.findViewById(R.id.tv_codigo)).setText(mA.getCodMaterial());

				final CheckBox ck_cantidad = (CheckBox) row.findViewById(R.id.ck_cantidad);
				String valorCheck = mA.getValorCheck() == null || mA.getValorCheck().trim().isEmpty() ? "0" : mA.getValorCheck();
				ck_cantidad.setChecked(valorCheck.equalsIgnoreCase("1"));
				datosFila.add(ck_cantidad);

				String key = mA.getCodMaterial();
				createRow(table, row, null, null, null, null, ck_cantidad, null, i, ini, mA.getDescripcion(), null, key);

				datosReporte.put(key, datosFila);
			}
		} else {
			Toast.makeText(this.context,"No hay materiales de apoyo registrados para este reporte",Toast.LENGTH_SHORT).show();
		}
	}

	// Reporte Potencial - Potencial Cliente SanFernando Canal AAVV
	public void show_reporte_potencial_potencial_sanfernando_aavv() {
		elementos = (new TblMstMaterialApoyoController(db)).getElementsForPotencialPotencialGrid(idCabecera);
		TableLayout table = (TableLayout) view.findViewById(R.id.tl_reporte_entrega_materiales);
		int numElementos = 0;
		boolean ini = true;
		if (elementos != null && (numElementos = elementos.size()) > 0) {
			datosReporte = new HashMap<String, ArrayList<Object>>();
			for (int i = 0; i < numElementos; i++) {

				E_ReportePotencial mA = (E_ReportePotencial) elementos.get(i);

				datosFila = new ArrayList<Object>();
				TableRow row = (TableRow) inflator.inflate(R.layout.ly_reporte_potencial_potencial_body, null);

				((TextView) row.findViewById(R.id.tv_sku)).setText(mA.getCodMaterial());

				final EditText et_cantidad = (EditText) row.findViewById(R.id.et_cantidad);
				String cantidad = mA.getCantidad() == null|| mA.getCantidad().trim().isEmpty() ? "" : mA.getCantidad();
				et_cantidad.setText(cantidad);
				if(i==0){
					et_cantidad.setFilters(new InputFilter[]{new DecimalFilter(15, 2)});
				}else{
					et_cantidad.setFilters(new InputFilter[]{new CustomDigitWatcher_0_2500(et_cantidad)});
				}
				datosFila.add(et_cantidad);

				String key = mA.getCodMaterial();
				createRow(table, row, et_cantidad, null, null, null, null,null, i, ini, mA.getDescripcion(), null, key);

				datosReporte.put(key, datosFila);
			}
		} else {
			Toast.makeText(this.context,"No hay materiales de apoyo registrados para este reporte",Toast.LENGTH_SHORT).show();
		}
	}

	// Reporte Precio Cliente SanFernando Canal Chikara
	public void show_reporte_precio_sanfernando_tradicional_chikara() {

		elementos = (new TblMstProductoController(db)).getElementsForGridPrecio(idCabecera);
		TableLayout table = (TableLayout) view.findViewById(R.id.tl_reporte_precio_sanfernando_tradicional_chikara);
		int numElementos = 0;
		// subtitulos = new HashMap<String, String>();
		// TextView tvSubtitulo = (TextView) findViewById(R.id.tv_subtitulo);
		boolean ini = true;
		if (elementos != null && (numElementos = elementos.size()) > 0) {
			datosReporte = new HashMap<String, ArrayList<Object>>();
			for (int i = 0; i < numElementos; i++) {

				E_ReportePrecio mA = (E_ReportePrecio) elementos.get(i);
				datosFila = new ArrayList<Object>();
				TableRow row = (TableRow) inflator.inflate(R.layout.ly_reporte_precio_sanfernando_tradicional_chikara_body, null);

				((TextView) row.findViewById(R.id.tv_sku)).setText(mA.getSku_prod());

				final EditText et_ppvp = (EditText) row.findViewById(R.id.et_ppvp);
				et_ppvp.setFilters(new InputFilter[] { new DecimalFilter(15, 2) });
				et_ppvp.setText(mA.getPrecio_pdv());
				datosFila.add(et_ppvp);

				final EditText et_ppvd = (EditText) row.findViewById(R.id.et_ppvd);
				et_ppvd.setFilters(new InputFilter[] { new DecimalFilter(15, 2) });
				et_ppvd.setText(mA.getPrecio_pvd());
				datosFila.add(et_ppvd);

				String key = mA.getSku_prod();
				createRow(table, row, et_ppvp, et_ppvd, null, null, null, null, i, ini, mA.getDesc_prod(), null, key);
				datosReporte.put(key, datosFila);
			}
		} else {
			Toast.makeText(this.context, "No hay productos registrados para este reporte", Toast.LENGTH_SHORT).show();
		}
	}

	// Reporte Presencia Producto Cliente SanFernando Canal Tradicional/Chikara
	public void show_reporte_presencia_Sanfernando_Tradicional_Chikara() {
		elementos = (new TblMstProductoController(db)).getElementsForGridPresenciaSFTradicional(idCabecera);

		TableLayout table = (TableLayout) view.findViewById(R.id.tl_reporte_presencia_sanfernando_chikara);
		int numObservaciones = 0;

		boolean ini = true;
		if (elementos != null && (numObservaciones = elementos.size()) > 0) {
			datosReporte = new HashMap<String, ArrayList<Object>>();
			for (int i = 0; i < numObservaciones; i++) {
				final E_TBL_MOV_REP_PRESENCIA mA = (E_TBL_MOV_REP_PRESENCIA) elementos.get(i);
				datosFila = new ArrayList<Object>();
				TableRow row = (TableRow) inflator.inflate(R.layout.ly_reporte_presencia_sanfernando_tradicional_chikara_body, null);
				((TextView) row.findViewById(R.id.tv_sku)).setText(mA.getSku_producto());
				CheckBox ck_presencia = (CheckBox) row.findViewById(R.id.ck_presencia);

				String key = mA.getSku_producto();

				createRow(table, row, null, null, null, null, ck_presencia, null, i, ini, mA.getDescripcion(), null, key);

				datosFila.add(ck_presencia);
				if (mA.getCod_presencia() != null && mA.getCod_presencia().equals("1")) {
					ck_presencia.setChecked(true);
				}
				ck_presencia.invalidate();
				ck_presencia.setSelected(true);
				datosReporte.put(key, datosFila);
			}
			table.invalidate();
		} else {
			Toast.makeText(this.context, "No hay productos registrados para este reporte", Toast.LENGTH_SHORT).show();
		}
	}

	// *Reporte encuestas san fernando
	public void show_reporte_encuestas_Sanfernando_Tradicional_Chikara() {
		elementos = (new TblMstMaterialApoyoController(db)).getElementsForEncuestasGrid(idCabecera);
		TableLayout table = (TableLayout) view.findViewById(R.id.tl_reporte_encuesta_sanfernando_chikara);
		int numElementos = 0;
		boolean ini = true;
		if (elementos != null && (numElementos = elementos.size()) > 0) {
			datosReporte = new HashMap<String, ArrayList<Object>>();
			for (int i = 0; i < numElementos; i++) {
				E_ReporteEncuesta mA = (E_ReporteEncuesta) elementos.get(i);
				datosFila = new ArrayList<Object>();
				TableRow row = (TableRow) inflator.inflate(R.layout.ly_reporte_encuesta_sanfernando_chikara_body, null);
				((TextView) row.findViewById(R.id.tv_pregunta)).setText(mA.getCodMaterial());
				CheckBox ck_si = (CheckBox) row.findViewById(R.id.ck_si);
				CheckBox ck_no = (CheckBox) row.findViewById(R.id.ck_no);

				String key = mA.getCodMaterial();

				createRow(table, row, null, null, null, null, ck_si, ck_no, i, ini, mA.getDescripcion(), null, key);

				datosFila.add(ck_si);
				if (mA.getItemChecked() != null && mA.getItemChecked().equals("si")) {
					ck_si.setChecked(true);
				}
				ck_si.invalidate();
				ck_si.setSelected(true);

				datosFila.add(ck_no);
				if (mA.getItemChecked() != null && mA.getItemChecked().equals("no")) {
					ck_no.setChecked(true);
				}
				ck_no.invalidate();
				ck_no.setSelected(true);
				datosReporte.put(key, datosFila);
			}
			table.invalidate();
		} else {
			Toast.makeText(this.context, "No hay productos registrados para este reporte", Toast.LENGTH_SHORT).show();
		}
	}

	public static final int COLUMN_EDITABLE_0 = 0;
	public static final int COLUMN_EDITABLE_1 = 1;
	public static final int COLUMN_EDITABLE_2 = 2;
	public static final int COLUMN_EDITABLE_3 = 3;

	public Boolean setDatosEvisibilidadGuardar(E_TBL_MOV_REP_PRESENCIA elemento) {
		Log.i("ReportesGrillaActivity", "... setDatosEvisibilidadGuardar. codMAterialApoyo = " + elemento.getCod_material_apoyo());

		ArrayList<Object> arr = datosReporte.get(elemento.getCod_material_apoyo());
		Boolean hayCambio = false;

		if (arr == null) {
			Log.e("ReportesGrillaActivity", "datosReporte.get(" + elemento.getCod_material_apoyo() + ") es null");
		} else {
			EditText editText = (EditText) arr.get(COLUMN_EDITABLE_0);
			String texto = editText.getText().toString();

			if (elemento.getValor_material_apoyo() == null) {
				if (!texto.trim().equals("")) {
					hayCambio = true;
				}
			} else if (!elemento.getValor_material_apoyo().equals(texto)) {
				hayCambio = true;
			}

			if (!texto.trim().equals("") && !infoRelevada) {
				infoRelevada = true;
			}

			HashMap<String, String> datosAnteriores = new HashMap<String, String>();
			datosAnteriores.put("valor_material_apoyo", elemento.getValor_material_apoyo());
			datosAnterioresList.add(datosAnteriores);

			if (hayCambio != null && hayCambio) {
				elemento.setValor_material_apoyo(texto);
				elemento.setHayCambio(true);
			}
		}
		Log.i("ReportesGrillaActivity", "setDatosEvisibilidadGuardar. hayCambio = " + hayCambio);
		return hayCambio;
	}

	public boolean setDatosEvisibilidadFITTGuardar(E_TBL_MOV_REP_PRESENCIA elemento) {
		Log.i("ReportesGrillaActivity", "... setDatosEvisibilidadGuardar. codMAterialApoyo = " + elemento.getCod_material_apoyo());

		boolean hayCambio = setDatosEvisibilidadGuardar(elemento);
		// TODO 1. Incluir el c�digo para guardar la ubicacion y la posicion

		Log.i("ReportesGrillaActivity", "setDatosEvisibilidadGuardar. hayCambio = " + hayCambio);
		return hayCambio;
	}

	public Boolean setDatosSKUPrecioProductoGuardar(E_TBL_MOV_REP_PRESENCIA elemento) {
		Log.i("ReportesGrillaActivity", "... setDatosSKUPrecioGuardar. codMAterialApoyo = " + elemento.getCod_material_apoyo());

		ArrayList<Object> arr = datosReporte.get(elemento.getSku_producto());
		Boolean hayCambio = false;

		if (arr == null) {
			Log.e("ReportesGrillaActivity", "datosReporte.get(" + elemento.getSku_producto() + ") es null");
		} else {
			EditText editText = (EditText) arr.get(COLUMN_EDITABLE_0);
			String texto = editText.getText().toString();

			if (elemento.getPrecio() == null) {
				if (!texto.trim().equals("")) {
					hayCambio = true;
				}
			} else if (!elemento.getPrecio().equals(texto)) {
				hayCambio = true;
			}

			if (!texto.trim().equals("") && !infoRelevada) {
				infoRelevada = true;
			}

			HashMap<String, String> datosAnteriores = new HashMap<String, String>();
			datosAnteriores.put("precio", elemento.getPrecio());
			datosAnterioresList.add(datosAnteriores);

			if (hayCambio != null && hayCambio) {

				elemento.setPrecio(texto);
				elemento.setHayCambio(true);
			}
		}
		Log.i("ReportesGrillaActivity", "setDatosSKUPrecioGuardar. hayCambio = " + hayCambio);
		return hayCambio;
	}

	public Boolean setDatosSKUPrecioMaterialGuardar(E_TBL_MOV_REP_PRESENCIA elemento) {
		Log.i("ReportesGrillaActivity", "... setDatosSKUPrecioGuardar. codMAterialApoyo = " + elemento.getCod_material_apoyo());
		ArrayList<Object> arr = datosReporte.get(elemento.getCod_material_apoyo());
		Boolean hayCambio = false;
		if (arr == null) {
			Log.e("ReportesGrillaActivity", "datosReporte.get(" + elemento.getSku_producto() + ") es null");
		} else {
			EditText editText = (EditText) arr.get(COLUMN_EDITABLE_0);
			String texto = editText.getText().toString();

			if (elemento.getPrecio() == null) {
				if (!texto.trim().equals("")) {

					hayCambio = true;
				}
			} else if (!elemento.getPrecio().equals(texto)) {
				hayCambio = true;
			}

			if (!texto.trim().equals("") && !infoRelevada) {
				infoRelevada = true;
			}

			HashMap<String, String> datosAnteriores = new HashMap<String, String>();
			datosAnteriores.put("precio", elemento.getPrecio());
			datosAnterioresList.add(datosAnteriores);

			if (hayCambio != null && hayCambio) {
				elemento.setPrecio(texto);
				elemento.setHayCambio(true);
			}
		}
		Log.i("ReportesGrillaActivity",
				"setDatosSKUPrecioGuardar. hayCambio = " + hayCambio);
		return hayCambio;
	}

	public Boolean setDatosSKUStockGuardar(E_TBL_MOV_REP_PRESENCIA elemento) {
		Log.i("ReportesGrillaActivity", "... ssetDatosSKUStockGuardar. codMAterialApoyo = " + elemento.getCod_material_apoyo());
		ArrayList<Object> arr = datosReporte.get(elemento.getSku_producto());
		Boolean hayCambio = false;
		if (arr == null) {
			Log.e("ReportesGrillaActivity", "datosReporte.get(" + elemento.getSku_producto() + ") es null");
		} else {
			EditText editText = (EditText) arr.get(COLUMN_EDITABLE_0);
			String texto = editText.getText().toString();

			if (elemento.getStock() == null) {
				if (!texto.trim().equals("")) {
					hayCambio = true;
				}
			} else if (!elemento.getStock().equals(texto)) {
				hayCambio = true;
			}

			if (!texto.trim().equals("") && !infoRelevada) {
				infoRelevada = true;
			}

			HashMap<String, String> datosAnteriores = new HashMap<String, String>();
			datosAnteriores.put("stock", elemento.getStock());
			datosAnterioresList.add(datosAnteriores);

			if (hayCambio != null && hayCambio) {
				elemento.setStock(texto);
				elemento.setHayCambio(true);
			}
		}
		Log.i("ReportesGrillaActivity", "setDatosSKUStockGuardar. hayCambio = " + hayCambio);
		return hayCambio;
	}

	public Boolean setDatosObservacionGuardar(E_TBL_MOV_REP_PRESENCIA elemento) {
		Log.i("ReportesGrillaActivity", "... setDatosObservacionGuardar. codMAterialApoyo = " + elemento.getCod_material_apoyo());
		ArrayList<Object> arr = datosReporte.get(elemento.getCod_observacion());
		Boolean hayCambio = false;
		if (arr == null) {
			Log.e("ReportesGrillaActivity", "datosReporte.get(" + elemento.getCod_observacion() + ") es null");
		} else {
			CheckBox editText = (CheckBox) arr.get(COLUMN_EDITABLE_0);
			boolean check = editText.isChecked();
			if (elemento.getObservacion() == null) {
				if (check) {
					hayCambio = true;
				}
			} else {

				if (elemento.getObservacion().equals("1")) {
					if (!check) {
						hayCambio = true;
					}
				} else {
					if (check) {
						hayCambio = true;
					}
				}
			}

			if (check && !infoRelevada) {
				infoRelevada = true;
			}

			HashMap<String, String> datosAnteriores = new HashMap<String, String>();
			datosAnteriores.put("observacion", elemento.getObservacion());
			datosAnterioresList.add(datosAnteriores);

			// hayCambio=check;
			if (hayCambio != null && hayCambio) {
				elemento.setObservacion(check ? "1" : "0");
				elemento.setHayCambio(true);
			}
		}
		Log.i("ReportesGrillaActivity", "setDatosObservacionGuardar. hayCambio = " + hayCambio);
		return hayCambio;
	}

	public boolean setDatosMaterialCuentaCumpleGuardar(
			E_TBL_MOV_REP_PRESENCIA elemento) {
		Log.i("ReportesGrillaActivity", "... setDatosMaterialCuentaCumpleGuardar. codMAterialApoyo = " + elemento.getCod_material_apoyo());
		ArrayList<Object> arr = datosReporte.get(elemento .getCod_material_apoyo());
		boolean hayCambio = false;
		if (arr == null) {
			Log.e("ReportesGrillaActivity", "datosReporte.get(" + elemento.getCod_material_apoyo() + ") es null");
		} else {
			CheckBox ck_cuenta = (CheckBox) arr.get(COLUMN_EDITABLE_0);
			boolean isCuenta = ck_cuenta.isChecked();
			CheckBox ck_cumple = (CheckBox) arr.get(COLUMN_EDITABLE_1);
			boolean isCumple = ck_cumple.isChecked();

			if (elemento.getValor_material_apoyo() == null) {
				if (isCuenta) {
					hayCambio = true;
				}
			} else {
				if (elemento.getValor_material_apoyo().equals("1")) {
					if (!isCuenta) {
						hayCambio = true;
					}
				} else {
					if (isCuenta) {
						hayCambio = true;
					}
				}
			}

			if (elemento.getCumple_layout() == null) {
				if (isCumple) {
					hayCambio = true;
				}
			} else {
				if (elemento.getCumple_layout().equals("1")) {
					if (!isCumple) {
						hayCambio = true;
					}
				} else {
					if (isCumple) {
						hayCambio = true;
					}
				}
			}

			if ((isCuenta || isCumple) && !infoRelevada) {
				infoRelevada = true;
			}

			HashMap<String, String> datosAnteriores = new HashMap<String, String>();
			if ((elemento.getValor_material_apoyo() != null && elemento .getValor_material_apoyo().equalsIgnoreCase("1"))
					|| (elemento.getCumple_layout() != null && elemento.getCumple_layout().equalsIgnoreCase("1"))) {
				if ((elemento.getValor_material_apoyo() != null && elemento.getValor_material_apoyo().equalsIgnoreCase("1"))
						|| (elemento.getValor_material_apoyo() != null && elemento.getValor_material_apoyo().equalsIgnoreCase("0"))) {
					datosAnteriores.put("valor_material_apoyo", elemento.getValor_material_apoyo());
				} else {
					datosAnteriores.put("valor_material_apoyo", "0");
				}
				if ((elemento.getCumple_layout() != null && elemento.getCumple_layout().equalsIgnoreCase("1"))
						|| (elemento.getCumple_layout() != null && elemento.getCumple_layout().equalsIgnoreCase("0"))) {
					datosAnteriores.put("cumple_layout", elemento.getCumple_layout());
				} else {
					datosAnteriores.put("cumple_layout", "0");
				}
				datosAnterioresList.add(datosAnteriores);
			}
			if (hayCambio) {
				if (isCuenta || isCumple) {
					elemento.setValor_material_apoyo(isCuenta ? "1" : "0");
					elemento.setCumple_layout(isCumple ? "1" : "0");
					elemento.setHayCambio(true);
				} else {
					elemento.setValor_material_apoyo(null);
					elemento.setCumple_layout(null);
					elemento.setHayCambio(true);
				}
			}
		}
		Log.i("ReportesGrillaActivity", "setDatosMaterialCuentaCumpleGuardar. hayCambio = " + hayCambio);
		return hayCambio;
	}

	public boolean setDatosSkuFrenteProfGuardar(E_TBL_MOV_REP_PRESENCIA elemento) {
		Log.i("ReportesGrillaActivity", "... setDatosSkuFrenteProfPrecioPedGuardar. codSKU = " + elemento.getSku_producto());
		ArrayList<Object> arr = datosReporte.get(elemento.getSku_producto());
		boolean hayCambio = false;
		if (arr == null) {
			Log.e("ReportesGrillaActivity", "datosReporte.get(" + elemento.getSku_producto() + ") es null");
		} else {
			EditText et_frente = (EditText) arr.get(COLUMN_EDITABLE_0);
			EditText et_prof = (EditText) arr.get(COLUMN_EDITABLE_1);
			String tx_frente = et_frente.getText().toString();
			String tx_prof = et_prof.getText().toString();

			if (elemento.getNum_frentes() == null) {
				if (!tx_frente.trim().equals("")) {
					hayCambio = true;
				}
			} else if (!elemento.getNum_frentes().equals(tx_frente)) {
				hayCambio = true;
			}

			if (elemento.getProfundidad() == null) {
				if (!tx_prof.trim().equals("")) {
					hayCambio = true;
				}
			} else if (!elemento.getProfundidad().equals(tx_prof)) {
				hayCambio = true;
			}
			if (!tx_frente.trim().equals("") && !infoRelevada) {
				infoRelevada = true;
			}
			if (!tx_prof.trim().equals("") && !infoRelevada) {
				infoRelevada = true;
			}
			HashMap<String, String> datosAnteriores = new HashMap<String, String>();
			datosAnteriores.put("num_frentes", elemento.getNum_frentes());
			datosAnteriores.put("profundidad", elemento.getProfundidad());
			datosAnterioresList.add(datosAnteriores);

			if (hayCambio) {
				elemento.setNum_frentes(tx_frente);
				elemento.setProfundidad(tx_prof);
				elemento.setHayCambio(true);
			}
		}
		Log.i("ReportesGrillaActivity", "setDatosFrenteProfGuardar. hayCambio = " + hayCambio);
		return hayCambio;
	}

	public boolean setDatosSkuPresenPrecioGuardar(E_TBL_MOV_REP_PRESENCIA elemento) {
		Log.i("ReportesGrillaActivity", "... setDatosSkuPresenPrecioGuardar. codSKU = " + elemento.getSku_producto());
		ArrayList<Object> arr = datosReporte.get(elemento.getSku_producto());
		boolean hayCambio = false;
		if (arr == null) {
			Log.e("ReportesGrillaActivity", "datosReporte.get(" + elemento.getSku_producto() + ") es null");
		} else {
			CheckBox ck_presencia = (CheckBox) arr.get(COLUMN_EDITABLE_0);
			boolean isPresencia = ck_presencia.isChecked();
			EditText et_precio = (EditText) arr.get(COLUMN_EDITABLE_1);
			String tx_precio = et_precio.getText().toString();

			if (elemento.getPrecio() == null) {
				if (!tx_precio.trim().equals("")) {
					hayCambio = true;
				}
			} else if (!elemento.getPrecio().equals(tx_precio)) {
				hayCambio = true;
			}

			if (elemento.getCod_presencia() == null) {
				if (isPresencia)
					hayCambio = true;
			} else {
				if (elemento.getCod_presencia().equals("1")) {
					if (!isPresencia)
						hayCambio = true;
				} else {
					if (isPresencia)
						hayCambio = true;
				}
			}

			if (!tx_precio.trim().equals("") && !infoRelevada) {
				infoRelevada = true;
			}
			if (isPresencia && !infoRelevada) {
				infoRelevada = true;
			}

			HashMap<String, String> datosAnteriores = new HashMap<String, String>();
			datosAnteriores.put("cod_presencia", elemento.getCod_presencia());
			datosAnteriores.put("precio", elemento.getPrecio());
			datosAnterioresList.add(datosAnteriores);

			if (hayCambio) {

				elemento.setCod_presencia(isPresencia ? "1" : "0");
				elemento.setPrecio(tx_precio);
				elemento.setHayCambio(true);
			}
		}
		Log.i("ReportesGrillaActivity", "setDatosSkuPresenPrecioGuardar. hayCambio = " + hayCambio);
		return hayCambio;
	}

	public boolean setDatosSkuPresenGuardar(E_TBL_MOV_REP_PRESENCIA elemento) {
		Log.i("ReportesGrillaActivity", "... setDatosSkuPresenPrecioPedGuardar. codSKU = " + elemento.getSku_producto());
		ArrayList<Object> arr = datosReporte.get(elemento.getSku_producto());
		boolean hayCambio = false;
		if (arr == null) {
			Log.e("ReportesGrillaActivity", "datosReporte.get(" + elemento.getSku_producto() + ") es null");
		} else {
			CheckBox ck_presencia = (CheckBox) arr.get(COLUMN_EDITABLE_0);
			boolean isPresencia = ck_presencia.isChecked();
			Log.i("ReportesGrillaActivity", "fijando datos a guardar en reporte sku_presencia con presencia visual en: " + isPresencia);
			Log.i("ReportesGrillaActivity", "fijando datos a guardar en reporte sku_presencia con presencia en elemento en: " + elemento.getCod_presencia());

			if (elemento.getCod_presencia() == null) {
				if (isPresencia)
					hayCambio = true;
			} else {
				if (elemento.getCod_presencia().equals("1")) {
					if (!isPresencia)
						hayCambio = true;
				} else {
					if (isPresencia)
						hayCambio = true;
				}
			}

			if (isPresencia && !infoRelevada) {
				infoRelevada = true;
			}

			HashMap<String, String> datosAnteriores = new HashMap<String, String>();
			datosAnteriores.put("cod_presencia", elemento.getCod_presencia());
			datosAnterioresList.add(datosAnteriores);

			if (hayCambio) {
				elemento.setCod_presencia(isPresencia ? "1" : "0");
				elemento.setHayCambio(true);
			}
		}
		Log.i("ReportesGrillaActivity", "setDatosSkuPresenPrecioPedGuardar. hayCambio = " + hayCambio);
		return hayCambio;
	}

	public boolean setDatosSkuPresenStockGuardar(E_TBL_MOV_REP_PRESENCIA elemento) {
		Log.i("ReportesGrillaActivity", "... setDatosSkuPresenStockGuardar. codSKU = " + elemento.getSku_producto());
		ArrayList<Object> arr = datosReporte.get(elemento.getSku_producto());
		boolean hayCambio = false;
		if (arr == null) {
			Log.e("ReportesGrillaActivity", "datosReporte.get(" + elemento.getSku_producto() + ") es null");
		} else {
			CheckBox ck_presencia = (CheckBox) arr.get(COLUMN_EDITABLE_0);
			boolean isPresencia = ck_presencia.isChecked();
			EditText et_stock = (EditText) arr.get(COLUMN_EDITABLE_1);
			String tx_stock = et_stock.getText().toString();

			if (elemento.getStock() == null) {
				if (!tx_stock.trim().equals("")) {
					hayCambio = true;
				}
			} else if (!elemento.getStock().equals(tx_stock)) {
				hayCambio = true;
			}

			if (elemento.getCod_presencia() == null) {
				if (isPresencia)
					hayCambio = true;
			} else {
				if (elemento.getCod_presencia().equals("1")) {
					if (!isPresencia)
						hayCambio = true;
				} else {
					if (isPresencia)
						hayCambio = true;
				}
			}

			if (!tx_stock.trim().equals("") && !infoRelevada) {
				infoRelevada = true;
			}
			if (isPresencia && !infoRelevada) {
				infoRelevada = true;
			}

			HashMap<String, String> datosAnteriores = new HashMap<String, String>();
			datosAnteriores.put("cod_presencia", elemento.getCod_presencia());
			datosAnteriores.put("stock", elemento.getStock());
			datosAnterioresList.add(datosAnteriores);

			if (hayCambio) {

				elemento.setCod_presencia(isPresencia ? "1" : "0");
				elemento.setStock(tx_stock);
				elemento.setHayCambio(true);
			}
		}
		Log.i("ReportesGrillaActivity", "setDatosSkuPresenStockGuardar. hayCambio = " + hayCambio);
		return hayCambio;
	}

	public Boolean setDatosClusterPreguntaMarqueCantidadGuardar(E_TBL_MOV_REP_PRESENCIA elemento) {
		Log.i("ReportesGrillaActivity", "... setDatosClusterPreguntaMarqueCantidadGuardar. codCluster = " + elemento.getCod_cluster());
		ArrayList<Object> arr = datosReporte.get(elemento.getCod_cluster());
		boolean hayCambio = false;
		if (arr == null) {
			Log.e("ReportesGrillaActivity", "datosReporte.get(" + elemento.getCod_cluster() + ") es null");
		} else {
			CheckBox ck_marque = (CheckBox) arr.get(COLUMN_EDITABLE_0);
			boolean isMarque = ck_marque.isChecked();
			EditText et_cantidad = (EditText) arr.get(COLUMN_EDITABLE_1);
			String texto = et_cantidad.getText().toString();

			if (!texto.trim().equals("") && !infoRelevada) {
				infoRelevada = true;
			}

			HashMap<String, String> datosAnteriores = new HashMap<String, String>();
			//
			datosAnteriores.put("cantidad", elemento.getCantidad());
			datosAnterioresList.add(datosAnteriores);

			if (isMarque) {
				hayCambio = isMarque;
				infoRelevada = true;
				elemento.setCantidad(texto);
				elemento.setHayCambio(true);
			} else {
				elemento.setHayCambio(false);
			}
		}
		Log.i("ReportesGrillaActivity", "setDatosClusterPreguntaMarqueCantidadGuardar. hayCambio = " + hayCambio);
		return hayCambio;
	}

	public Boolean setDatosPresenVisibCompetCodCuentaGuardar(E_TBL_MOV_REP_PRESENCIA elemento) {
		Log.i("ReportesGrillaActivity", "... setDatosPresenVisibCompetCodCuentaGuardar. codMAterialApoyo = " + elemento.getCod_material_apoyo());
		ArrayList<Object> arr = datosReporte.get(elemento.getCod_material_apoyo());
		Boolean hayCambio = false;
		if (arr == null) {
			Log.e("ReportesGrillaActivity", "datosReporte.get(" + elemento.getCod_material_apoyo() + ") es null");
		} else {
			CheckBox ck_cuenta = (CheckBox) arr.get(COLUMN_EDITABLE_0);
			boolean check = ck_cuenta.isChecked();
			if (elemento.getValor_material_apoyo() == null) {
				if (check) {
					hayCambio = true;
				}
			} else {

				if (elemento.getValor_material_apoyo().equals("1")) {
					if (!check) {
						hayCambio = true;
					}
				} else {
					if (check) {
						hayCambio = true;
					}
				}
			}

			if (check && !infoRelevada) {
				infoRelevada = true;
			}

			HashMap<String, String> datosAnteriores = new HashMap<String, String>();
			datosAnteriores.put("valor_material_apoyo",
					elemento.getValor_material_apoyo());
			datosAnterioresList.add(datosAnteriores);

			if (hayCambio != null && hayCambio) {
				elemento.setValor_material_apoyo(check ? "1" : "0");
				elemento.setHayCambio(true);
			}
		}
		Log.i("ReportesGrillaActivity", "setDatosPresenVisibCompetCodCuentaGuardar. hayCambio = " + hayCambio);
		return hayCambio;
	}

	public Boolean setDatosPresenVisibCompetCodCantidadGuardar(E_TBL_MOV_REP_PRESENCIA elemento) {
		Log.i("ReportesGrillaActivity", "... setDatosPresenVisibCompetCodCantidadGuardar. codMAterialApoyo = " + elemento.getCod_material_apoyo());
		ArrayList<Object> arr = datosReporte.get(elemento.getCod_material_apoyo());
		Boolean hayCambio = false;
		if (arr == null) {
			Log.i("ReportesGrillaActivity", "datosReporte.get(" + elemento.getCod_material_apoyo() + ") es null");
		} else {
			EditText et_cantidad = (EditText) arr.get(COLUMN_EDITABLE_0);
			String tx_cantidad = et_cantidad.getText().toString();

			if (elemento.getCantidad() == null) {
				if (!tx_cantidad.trim().equals("")) {
					hayCambio = true;
				}
			} else if (!elemento.getCantidad().equals(tx_cantidad)) {
				hayCambio = true;
			}

			if (!tx_cantidad.trim().equals("") && !infoRelevada) {
				infoRelevada = true;
			}

			HashMap<String, String> datosAnteriores = new HashMap<String, String>();
			datosAnteriores.put("cantidad", elemento.getCantidad());
			datosAnterioresList.add(datosAnteriores);

			if (hayCambio != null && hayCambio) {
				elemento.setCantidad(tx_cantidad);
				elemento.setHayCambio(true);
			}
		}
		Log.i("ReportesGrillaActivity", "setDatosPresenVisibCompetCodCantidadGuardar. hayCambio = " + hayCambio);
		return hayCambio;
	}

	public boolean setDatosPrecioAlicorpAutoservicioGuardar(E_ReportePrecio elemento) {
		Log.i("ReportesGrillaActivity", "... setDatosPrecioAlicorpAutoservicioGuardar. codSKU = " + elemento.getSku_prod());
		ArrayList<Object> arr = datosReporte.get(elemento.getSku_prod());
		boolean hayCambio = false;
		E_MotivoObservacion motivo = new E_MotivoObservacion();
		if (arr == null) {
			Log.e("ReportesGrillaActivity", "datosReporte.get(" + elemento.getSku_prod() + ") es null");
		} else {
			EditText et_ppdv = (EditText) arr.get(COLUMN_EDITABLE_0);
			String tx_ppdv = et_ppdv.getText().toString();
			EditText et_poferta = (EditText) arr.get(COLUMN_EDITABLE_1);
			String tx_poferta = et_poferta.getText().toString();
			Spinner spMObs = (Spinner) arr.get(COLUMN_EDITABLE_2);
			String tx_mobs = null;
			int posicionSpinner = spMObs.getSelectedItemPosition();
			if (posicionSpinner > 0) {
				motivo = (E_MotivoObservacion) opciones.get(posicionSpinner);
				tx_mobs = motivo.getCod_motivo();
			} else {
				tx_mobs = null;
			}

			if (elemento.getPrecio_pdv() == null) {
				if (!tx_ppdv.trim().equals("")) {
					hayCambio = true;
				}
			} else if (!elemento.getPrecio_pdv().equals(tx_ppdv)) {
				hayCambio = true;
			}
			if (elemento.getPrecio_oferta() == null) {
				if (!tx_poferta.trim().equals("")) {
					hayCambio = true;
				}
			} else if (!elemento.getPrecio_oferta().equals(tx_poferta)) {
				hayCambio = true;
			}

			if (elemento.getCod_motivo_obs() == null) {
				if (tx_mobs != null)
					hayCambio = true;
			} else {
				if (!elemento.getCod_motivo_obs()
						.equals(motivo.getCod_motivo())) {
					hayCambio = true;
				}
			}

			if (!tx_ppdv.trim().equals("") && !infoRelevada) {
				infoRelevada = true;
			}
			if (!tx_poferta.trim().equals("") && !infoRelevada) {
				infoRelevada = true;
			}

			if (tx_mobs != null && !infoRelevada) {
				infoRelevada = true;
			}

			HashMap<String, String> datosAnteriores = new HashMap<String, String>();
			datosAnteriores.put("precio_pdv", elemento.getPrecio_pdv());
			datosAnteriores.put("precio_oferta", elemento.getPrecio_oferta());
			datosAnteriores.put("mobs", elemento.getCod_motivo_obs());
			datosAnterioresList.add(datosAnteriores);

			if (hayCambio) {

				elemento.setCod_motivo_obs(tx_mobs);
				elemento.setPrecio_pdv(tx_ppdv);
				elemento.setPrecio_oferta(tx_poferta);
			}
		}
		Log.i("ReportesGrillaActivity","setDatosPrecioAlicorpAutoservicioGuardar. hayCambio = "+ hayCambio);
		return hayCambio;
	}

	public boolean setDatosQuiebreAlicorpAutoservicioGuardar(E_ReporteQuiebre elemento) {
		Log.i("ReportesGrillaActivity", "... setDatosQuiebreAlicorpAutoservicioGuardar. codSKU = " + elemento.getSku_prod());
		ArrayList<Object> arr = datosReporte.get(elemento.getSku_prod());
		boolean hayCambio = false;
		E_MotivoObservacion motivo = new E_MotivoObservacion();
		if (arr == null) {
			Log.e("ReportesGrillaActivity", "datosReporte.get(" + elemento.getSku_prod() + ") es null");
		} else {
			Spinner spMObs = (Spinner) arr.get(COLUMN_EDITABLE_0);
			String tx_mobs = null;

			int posicionSpinner = spMObs.getSelectedItemPosition();
			if (posicionSpinner > 0) {
				motivo = (E_MotivoObservacion) opciones.get(posicionSpinner);
				tx_mobs = motivo.getCod_motivo();
			} else {
				tx_mobs = null;
			}

			if (elemento.getCod_motivo_quiebre() == null) {
				if (tx_mobs != null)
					hayCambio = true;
			} else {
				if (!elemento.getCod_motivo_quiebre().equals(motivo.getCod_motivo())) {
					hayCambio = true;
				}
			}

			if (tx_mobs != null && !infoRelevada) {
				infoRelevada = true;
			}

			HashMap<String, String> datosAnteriores = new HashMap<String, String>();
			datosAnteriores.put("cod_quiebre", elemento.getCod_motivo_quiebre());
			datosAnterioresList.add(datosAnteriores);

			if (hayCambio) {
				elemento.setCod_motivo_quiebre(tx_mobs);
				elemento.setHayCambio(true);
			}
		}
		Log.i("ReportesGrillaActivity", "setDatosQuiebreAlicorpAutoservicioGuardar. hayCambio = " + hayCambio);
		return hayCambio;
	}

	public boolean setDatosPrecioAlicorpMayoristasGuardar(
			E_ReportePrecio elemento) {
		Log.i("ReportesGrillaActivity", "... setDatosPrecioAlicorpMayoristasGuardar. codSKU = " + elemento.getSku_prod());
		ArrayList<Object> arr = datosReporte.get(elemento.getSku_prod());
		boolean hayCambio = false;
		E_MotivoObservacion motivo = new E_MotivoObservacion();
		if (arr == null) {
			Log.e("ReportesGrillaActivity", "datosReporte.get(" + elemento.getSku_prod() + ") es null");
		} else {
			EditText et_pmayor = (EditText) arr.get(COLUMN_EDITABLE_0);
			String tx_pmayor = et_pmayor.getText().toString();
			EditText et_preventa = (EditText) arr.get(COLUMN_EDITABLE_1);
			String tx_preventa = et_preventa.getText().toString();
			EditText et_poferta = (EditText) arr.get(COLUMN_EDITABLE_2);
			String tx_poferta = et_poferta.getText().toString();
			Spinner spMObs = (Spinner) arr.get(COLUMN_EDITABLE_3);
			String tx_mobs = null;

			int posicionSpinner = spMObs.getSelectedItemPosition();
			if (posicionSpinner > -1) {// >0
				motivo = (E_MotivoObservacion) opciones.get(posicionSpinner);
				tx_mobs = motivo.getCod_motivo();
			} else {
				tx_mobs = null;
			}

			if (elemento.getPrecio_mayorista() == null) {
				if (!tx_pmayor.trim().equals("")) {
					hayCambio = true;
				}
			} else if (!elemento.getPrecio_mayorista().equals(tx_pmayor)) {
				hayCambio = true;
			}

			if (elemento.getPrecio_reventa() == null) {
				if (!tx_preventa.trim().equals("")) {
					hayCambio = true;
				}
			} else if (!elemento.getPrecio_reventa().equals(tx_preventa)) {
				hayCambio = true;
			}
			if (elemento.getPrecio_oferta() == null) {
				if (!tx_poferta.trim().equals("")) {
					hayCambio = true;
				}
			} else if (!elemento.getPrecio_oferta().equals(tx_poferta)) {
				hayCambio = true;
			}

			if (elemento.getCod_motivo_obs() == null) {
				if (tx_mobs != null && !tx_mobs.equalsIgnoreCase(((E_MotivoObservacion) opciones.get(0)).getCod_motivo())) {
					hayCambio = true;
				}
			} else {
				if (!elemento.getCod_motivo_obs()
						.equals(motivo.getCod_motivo())) {
					hayCambio = true;
				}
			}

			if (!tx_pmayor.trim().equals("") && !infoRelevada) {
				infoRelevada = true;
			}
			if (!tx_preventa.trim().equals("") && !infoRelevada) {
				infoRelevada = true;
			}
			if (!tx_poferta.trim().equals("") && !infoRelevada) {
				infoRelevada = true;
			}

			if (tx_mobs != null && !infoRelevada) {
				infoRelevada = true;
			}

			HashMap<String, String> datosAnteriores = new HashMap<String, String>();
			datosAnteriores.put("precio_mayorista", elemento.getPrecio_mayorista());
			datosAnteriores.put("precio_reventa", elemento.getPrecio_reventa());
			datosAnteriores.put("precio_oferta", elemento.getPrecio_oferta());
			datosAnteriores.put("mobs", elemento.getCod_motivo_obs());
			datosAnterioresList.add(datosAnteriores);

			if (hayCambio) {
				elemento.setCod_motivo_obs(tx_mobs);
				elemento.setPrecio_mayorista(tx_pmayor);
				elemento.setPrecio_reventa(tx_preventa);
				elemento.setPrecio_oferta(tx_poferta);
				elemento.setHayCambio(true);
			}
		}
		Log.i("ReportesGrillaActivity", "setDatosPrecioAlicorpMayoristasGuardar. hayCambio = " + hayCambio);
		return hayCambio;
	}

	public boolean setDatosStockAlicorpMayoristasGuardar(E_ReporteStock elemento) {
		Log.i("ReportesGrillaActivity", "... setDatosStockAlicorpMayoristasGuardar. cod_familia = " + elemento.getCod_familia());
		ArrayList<Object> arr = datosReporte.get(elemento.getCod_familia());
		boolean hayCambio = false;
		E_MotivoObservacion motivo = new E_MotivoObservacion();
		if (arr == null) {
			Log.e("ReportesGrillaActivity", "datosReporte.get(" + elemento.getCod_familia() + ") es null");
		} else {
			EditText et_stock = (EditText) arr.get(COLUMN_EDITABLE_0);
			String tx_stock = et_stock.getText().toString();
			Spinner spMObs = (Spinner) arr.get(COLUMN_EDITABLE_1);
			String tx_mobs = null;

			int posicionSpinner = spMObs.getSelectedItemPosition();
			if (posicionSpinner > 0) {
				motivo = (E_MotivoObservacion) opciones.get(posicionSpinner);
				tx_mobs = motivo.getCod_motivo();
			} else {
				tx_mobs = null;
			}

			if (elemento.getStock() == null) {
				if (!tx_stock.trim().equals("")) {
					hayCambio = true;
				}
			} else if (!elemento.getStock().equals(tx_stock)) {
				hayCambio = true;
			}

			if (elemento.getCod_motivo_obs() == null) {
				if (tx_mobs != null)
					hayCambio = true;
			} else {
				if (!elemento.getCod_motivo_obs().equals(motivo.getCod_motivo())) {
					hayCambio = true;
				}
			}

			if (!tx_stock.trim().equals("") && !infoRelevada) {
				infoRelevada = true;
			}
			if (tx_mobs != null && !infoRelevada) {
				infoRelevada = true;
			}

			HashMap<String, String> datosAnteriores = new HashMap<String, String>();
			datosAnteriores.put("stock", elemento.getStock());
			datosAnteriores.put("mobs", elemento.getCod_motivo_obs());
			datosAnterioresList.add(datosAnteriores);

			if (hayCambio) {
				elemento.setCod_motivo_obs(tx_mobs);
				elemento.setStock(tx_stock);
				elemento.setHayCambio(true);
			}
		}
		Log.i("ReportesGrillaActivity", "setDatosStockAlicorpMayoristasGuardar. hayCambio = " + hayCambio);
		return hayCambio;
	}

	public boolean setDatosPrecioPVPSanFernandoAAVVGuardar(
			E_ReportePrecio elemento) {
		Log.i("ReportesGrillaActivity", "... setDatosPrecioPVPSanFerandoAAVVGuardar. codSKU = " + elemento.getSku_prod());
		ArrayList<Object> arr = datosReporte.get(elemento.getSku_prod());
		boolean hayCambio = false;
		if (arr == null) {
			Log.e("ReportesGrillaActivity", "datosReporte.get(" + elemento.getSku_prod() + ") es null");
		} else {
			EditText et_pvp = (EditText) arr.get(COLUMN_EDITABLE_0);
			String txt_pvp = et_pvp.getText().toString();

			if (elemento.getPrecio_lista() == null) {
				if (!txt_pvp.trim().equals("")) {
					hayCambio = true;
				}
			} else if (!elemento.getPrecio_lista().equals(txt_pvp)) {
				hayCambio = true;
			}
			if (!txt_pvp.trim().equals("") && !infoRelevada) {
				infoRelevada = true;
			}

			HashMap<String, String> datosAnteriores = new HashMap<String, String>();
			datosAnteriores.put("precio_pvp", elemento.getPrecio_lista());
			datosAnterioresList.add(datosAnteriores);

			if (hayCambio) {
				elemento.setPrecio_lista(txt_pvp);
				elemento.setHayCambio(true);
			}
		}
		Log.i("ReportesGrillaActivity", "setDatosPrecioSanFernandoAAVVGuardar. hayCambio = " + hayCambio);
		return hayCambio;
	}

	public boolean setDatosPrecioPDV_PDVSanFernandoAAVVGuardar(E_ReportePrecio elemento) {
		Log.i("ReportesGrillaActivity", "... setDatosPrecioPDV_PDVSanFernandoAAVVGuardar. codSKU = " + elemento.getSku_prod());
		ArrayList<Object> arr = datosReporte.get(elemento.getSku_prod());
		boolean hayCambio = false;
		if (arr == null) {
			Log.e("ReportesGrillaActivity", "datosReporte.get(" + elemento.getSku_prod() + ") es null");
		} else {
			EditText et_pdv = (EditText) arr.get(COLUMN_EDITABLE_0);
			String txt_pdv = et_pdv.getText().toString();
			Spinner sp_tipo = (Spinner) arr.get(COLUMN_EDITABLE_1);
			String cod_tipo = null;
			E_TipoPrecioPDV tipo_precio= null;
			int posicionSpinner = sp_tipo.getSelectedItemPosition();
			if (posicionSpinner > -1) {// >0
				tipo_precio = (E_TipoPrecioPDV) opciones.get(posicionSpinner);
				cod_tipo = tipo_precio.getCodTipoPrecio();
			}

			if (elemento.getPrecio_pdv() == null) {
				if (!txt_pdv.trim().equals("")) {
					hayCambio = true;
				}
			} else if (!elemento.getPrecio_pdv().equals(txt_pdv)) {
				hayCambio = true;
			}
			if (elemento.getCod_tipo_precio() == null) {
				if (cod_tipo != null && !cod_tipo.equalsIgnoreCase(((E_TipoPrecioPDV) opciones.get(0)).getCodTipoPrecio())) {
					hayCambio = true;
				}
			} else {
				if (!elemento.getCod_tipo_precio().equals(tipo_precio.getCodTipoPrecio())) {
					hayCambio = true;
				}
			}
			if (!txt_pdv.trim().equals("") && !infoRelevada) {
				infoRelevada = true;
			}
			if (cod_tipo != null && !infoRelevada) {
				infoRelevada = true;
			}

			HashMap<String, String> datosAnteriores = new HashMap<String, String>();
			datosAnteriores.put("precio_pdv", elemento.getPrecio_lista());
			datosAnteriores.put("cod_tipo_precio", elemento.getCod_tipo_precio());
			datosAnterioresList.add(datosAnteriores);

			if (hayCambio) {
				elemento.setPrecio_pdv(txt_pdv);
				elemento.setCod_tipo_precio(cod_tipo);
				elemento.setHayCambio(true);
			}
		}
		Log.i("ReportesGrillaActivity", "setDatosPrecioPDV_PDVSanFernandoAAVVGuardar. hayCambio = " + hayCambio);
		return hayCambio;
	}

	public Boolean setDatosPrecioPDV_OBSSanFernandoAAVVGuardar(
			E_ReportePrecio elemento) {
		Log.i("ReportesGrillaActivity", "... setDatosPrecioPDV_OBSSanFernandoAAVVGuardar. Cod_motivo_obs = " + elemento.getCod_motivo_obs());
		ArrayList<Object> arr = datosReporte.get(elemento.getCod_motivo_obs());
		Boolean hayCambio = false;
		if (arr == null) {
			Log.e("ReportesGrillaActivity", "datosReporte.get(" + elemento.getCod_motivo_obs() + ") es null");
		} else {
			CheckBox ck_obs = (CheckBox) arr.get(COLUMN_EDITABLE_0);
			boolean check = ck_obs.isChecked();
			if (elemento.getObservacion() == null) {
				if (check) {
					hayCambio = true;
				}
			} else {

				if (elemento.getObservacion().equals("1")) {
					if (!check) {
						hayCambio = true;
					}
				} else {
					if (check) {
						hayCambio = true;
					}
				}
			}

			if (check && !infoRelevada) {
				infoRelevada = true;
			}

			HashMap<String, String> datosAnteriores = new HashMap<String, String>();
			datosAnteriores.put("observacion", elemento.getObservacion());
			datosAnterioresList.add(datosAnteriores);

			// hayCambio=check;
			if (hayCambio != null && hayCambio) {
				elemento.setObservacion(check ? "1" : "0");
				elemento.setHayCambio(true);
			}
		}
		Log.i("ReportesGrillaActivity", "setDatosPrecioPDV_OBSSanFernandoAAVVGuardar. hayCambio = " + hayCambio);
		return hayCambio;
	}

	public boolean setDatosVentasSanFernandoAAVVGuardar(E_ReporteStock elemento) {
		Log.i("ReportesGrillaActivity", "... setDatosVentasSanFerandoAAVVGuardar. codSKU = " + elemento.getSku_prod());
		ArrayList<Object> arr = datosReporte.get(elemento.getSku_prod());
		boolean hayCambio = false;
		if (arr == null) {
			Log.e("ReportesGrillaActivity", "datosReporte.get(" + elemento.getSku_prod() + ") es null");
		} else {
			EditText et_pedido = (EditText) arr.get(COLUMN_EDITABLE_0);
			String tx_pedido = et_pedido.getText().toString();
			EditText et_ingreso = (EditText) arr.get(COLUMN_EDITABLE_1);
			String tx_ingreso = et_ingreso.getText().toString();
			EditText et_venta = (EditText) arr.get(COLUMN_EDITABLE_2);
			String tx_venta = et_venta.getText().toString();

			if (elemento.getPedido() == null) {
				if (!tx_pedido.trim().equals("")) {
					hayCambio = true;
				}
			} else if (!elemento.getPedido().equals(tx_pedido)) {
				hayCambio = true;
			}
			if (elemento.getStock() == null) {
				if (!tx_ingreso.trim().equals("")) {
					hayCambio = true;
				}
			} else if (!elemento.getStock().equals(tx_ingreso)) {
				hayCambio = true;
			}
			if (elemento.getVenta() == null) {
				if (!tx_venta.trim().equals("")) {
					hayCambio = true;
				}
			} else if (!elemento.getVenta().equals(tx_venta)) {
				hayCambio = true;
			}

			if (!tx_pedido.trim().equals("") && !infoRelevada) {
				infoRelevada = true;
			}
			if (!tx_ingreso.trim().equals("") && !infoRelevada) {
				infoRelevada = true;
			}
			if (!tx_venta.trim().equals("") && !infoRelevada) {
				infoRelevada = true;
			}

			HashMap<String, String> datosAnteriores = new HashMap<String, String>();
			datosAnteriores.put("pedido", elemento.getPedido());
			datosAnteriores.put("ingreso", elemento.getStock());
			datosAnteriores.put("venta", elemento.getVenta());
			datosAnterioresList.add(datosAnteriores);

			if (hayCambio) {

				elemento.setPedido(tx_pedido);
				elemento.setStock(tx_ingreso);
				elemento.setVenta(tx_venta);
				elemento.setHayCambio(true);
			}
		}
		Log.i("ReportesGrillaActivity", "setDatosVentasSanFernandoAAVVGuardar. hayCambio = " + hayCambio);
		return hayCambio;
	}

	public boolean setDatosPrecioSanFernandoModernoGuardar(E_ReportePrecio elemento) {
		Log.i("ReportesGrillaActivity", "... setDatosPrecioSanFerandoModernoGuardar. codSKU = " + elemento.getSku_prod());
		ArrayList<Object> arr = datosReporte.get(elemento.getSku_prod());
		boolean hayCambio = false;
		if (arr == null) {
			Log.e("ReportesGrillaActivity", "datosReporte.get(" + elemento.getSku_prod() + ") es null");
		} else {
			EditText et_poferta = (EditText) arr.get(COLUMN_EDITABLE_0);
			String tx_poferta = et_poferta.getText().toString();
			EditText et_pregular = (EditText) arr.get(COLUMN_EDITABLE_1);
			String tx_pregular = et_pregular.getText().toString();

			if (elemento.getPrecio_oferta() == null) {
				if (!tx_poferta.trim().equals("")) {
					hayCambio = true;
				}
			} else if (!elemento.getPrecio_oferta().equals(tx_poferta)) {
				hayCambio = true;
			}
			if (elemento.getPrecio_regular() == null) {
				if (!tx_pregular.trim().equals("")) {
					hayCambio = true;
				}
			} else if (!elemento.getPrecio_regular().equals(tx_pregular)) {
				hayCambio = true;
			}

			if (!tx_poferta.trim().equals("") && !infoRelevada) {
				infoRelevada = true;
			}
			if (!tx_pregular.trim().equals("") && !infoRelevada) {
				infoRelevada = true;
			}

			HashMap<String, String> datosAnteriores = new HashMap<String, String>();
			datosAnteriores.put("precio_oferta", elemento.getPrecio_oferta());
			datosAnteriores.put("precio_regular", elemento.getPrecio_regular());
			datosAnterioresList.add(datosAnteriores);

			if (hayCambio) {

				elemento.setPrecio_oferta(tx_poferta);
				elemento.setPrecio_regular(tx_pregular);
				elemento.setHayCambio(true);
			}
		}
		Log.i("ReportesGrillaActivity", "setDatosPrecioSanFernandoModernoGuardar. hayCambio = " + hayCambio);
		return hayCambio;
	}

	public boolean setDatosIngresosSanFernandoModernoGuardar(E_ReporteStock elemento) {
		Log.i("ReportesGrillaActivity", "... setDatosIngresosSanFernandoModernoGuardar. codSKU = " + elemento.getSku_prod());
		ArrayList<Object> arr = datosReporte.get(elemento.getSku_prod());
		boolean hayCambio = false;
		E_MotivoObservacion motivo = new E_MotivoObservacion();
		if (arr == null) {
			Log.e("ReportesGrillaActivity", "datosReporte.get(" + elemento.getSku_prod() + ") es null");
		} else {

			EditText et_exhibicion = (EditText) arr.get(COLUMN_EDITABLE_0);
			String tx_exhibicion = et_exhibicion.getText().toString();
			EditText et_camara = (EditText) arr.get(COLUMN_EDITABLE_1);
			String tx_camara = et_camara.getText().toString();

			Spinner spMObs = (Spinner) arr.get(COLUMN_EDITABLE_2);
			String tx_mobs = null;

			int posicionSpinner = spMObs.getSelectedItemPosition();
			if (posicionSpinner > 0) {
				motivo = (E_MotivoObservacion) opciones.get(posicionSpinner);
				tx_mobs = motivo.getCod_motivo();
			} else {
				tx_mobs = null;
			}

			if (elemento.getExhibicion() == null) {
				if (!tx_exhibicion.trim().equals("")) {
					hayCambio = true;
				}
			} else if (!elemento.getExhibicion().equals(tx_exhibicion)) {
				hayCambio = true;
			}

			if (elemento.getCamara() == null) {
				if (!tx_camara.trim().equals("")) {
					hayCambio = true;
				}
			} else if (!elemento.getCamara().equals(tx_camara)) {
				hayCambio = true;
			}

			if (elemento.getCod_motivo_obs() == null) {
				if (tx_mobs != null)
					hayCambio = true;
			} else {
				if (!elemento.getCod_motivo_obs()
						.equals(motivo.getCod_motivo())) {
					hayCambio = true;
				}
			}

			EditText et_ingresos = (EditText) arr.get(COLUMN_EDITABLE_3);
			String tx_ingresos = et_ingresos.getText().toString();

			if (!tx_exhibicion.trim().equals("") && !infoRelevada) {
				infoRelevada = true;
			}
			if (!tx_camara.trim().equals("") && !infoRelevada) {
				infoRelevada = true;
			}
			if (!tx_ingresos.trim().equals("") && !infoRelevada) {
				infoRelevada = true;
			}
			if (tx_mobs != null && !infoRelevada) {
				infoRelevada = true;
			}

			HashMap<String, String> datosAnteriores = new HashMap<String, String>();
			datosAnteriores.put("camara", elemento.getCamara());
			datosAnteriores.put("exhibicion", elemento.getExhibicion());
			datosAnteriores.put("mobs", elemento.getCod_motivo_obs());
			datosAnteriores.put("stock", elemento.getStock());
			datosAnterioresList.add(datosAnteriores);

			if (hayCambio) {
				elemento.setCod_motivo_obs(tx_mobs);
				elemento.setExhibicion(tx_exhibicion);
				elemento.setCamara(tx_camara);
				elemento.setStock(tx_ingresos);
				elemento.setHayCambio(true);
			}
		}
		Log.i("ReportesGrillaActivity", "setDatosIngresosSanFernandoModernoGuardar. hayCambio = " + hayCambio);
		return hayCambio;
	}

	public boolean setDatosImpulsoSanFernandoModernoGuardar(E_ReporteImpulso elemento) {
		Log.i("ReportesGrillaActivity", "... setDatosImpulsoSanFernandoModernoGuardar. codSKU = " + elemento.getSku_prod());
		ArrayList<Object> arr = datosReporte.get(elemento.getSku_prod());
		boolean hayCambio = false;
		if (arr == null) {
			Log.e("ReportesGrillaActivity", "datosReporte.get(" + elemento.getSku_prod() + ") es null");
		} else {

			EditText et_ingresos = (EditText) arr.get(COLUMN_EDITABLE_0);
			String tx_ingresos = et_ingresos.getText().toString();
			EditText et_stockfinal = (EditText) arr.get(COLUMN_EDITABLE_1);
			String tx_stockfinal = et_stockfinal.getText().toString();

			if (elemento.getIngreso() == null) {
				if (!tx_ingresos.trim().equals("")) {
					hayCambio = true;
				}
			} else if (!elemento.getIngreso().equals(tx_ingresos)) {
				hayCambio = true;
			}

			if (elemento.getStock_final() == null) {
				if (!tx_stockfinal.trim().equals("")) {
					hayCambio = true;
				}
			} else if (!elemento.getStock_final().equals(tx_stockfinal)) {
				hayCambio = true;
			}

			if (!tx_ingresos.trim().equals("") && !infoRelevada) {
				infoRelevada = true;
			}
			if (!tx_stockfinal.trim().equals("") && !infoRelevada) {
				infoRelevada = true;
			}

			HashMap<String, String> datosAnteriores = new HashMap<String, String>();
			datosAnteriores.put("ingreso", elemento.getIngreso());
			datosAnteriores.put("stock_final", elemento.getStock_final());
			datosAnterioresList.add(datosAnteriores);

			if (hayCambio) {
				elemento.setIngreso(tx_ingresos);
				elemento.setStock_final(tx_stockfinal);
				elemento.setHayCambio(true);
			}
		}
		Log.i("ReportesGrillaActivity", "setDatosImpulsoSanFernandoModernoGuardar. hayCambio = " + hayCambio);
		return hayCambio;
	}

	/*
	 * public boolean setDatosPrecioSanFernandoTradicionalGuardar(
	 * E_ReportePrecio elemento) { Log.i("ReportesGrillaActivity",
	 * "... setDatosPrecioSanFernandoTradicionalGuardar. codSKU = " +
	 * elemento.getSku_prod()); ArrayList<Object> arr =
	 * datosReporte.get(elemento.getSku_prod()); boolean hayCambio = false; if
	 * (arr == null) { Log.e("ReportesGrillaActivity", "datosReporte.get(" +
	 * elemento.getSku_prod() + ") es null"); } else { EditText et_costo =
	 * (EditText) arr.get(COLUMN_EDITABLE_0); String tx_costo =
	 * et_costo.getText().toString(); EditText et_pconsumidor = (EditText)
	 * arr.get(COLUMN_EDITABLE_1); String tx_pconsumidor =
	 * et_pconsumidor.getText().toString();
	 * 
	 * if (elemento.getPrecio_costo() == null) { if
	 * (!tx_costo.trim().equals("")) { hayCambio = true; } } else if
	 * (!elemento.getPrecio_costo().equals(tx_costo)) { hayCambio = true; } if
	 * (elemento.getPrecio_pdv() == null) { if
	 * (!tx_pconsumidor.trim().equals("")) { hayCambio = true; } } else if
	 * (!elemento.getPrecio_pdv().equals(tx_pconsumidor)) { hayCambio = true; }
	 * 
	 * if (!tx_costo.trim().equals("") && !infoRelevada) { infoRelevada = true;
	 * } if (!tx_pconsumidor.trim().equals("") && !infoRelevada) { infoRelevada
	 * = true; }
	 * 
	 * HashMap<String, String> datosAnteriores = new HashMap<String, String>();
	 * datosAnteriores.put("precio_costo", elemento.getPrecio_costo());
	 * datosAnteriores.put("precio_pdv", elemento.getPrecio_pdv());
	 * datosAnterioresList.add(datosAnteriores);
	 * 
	 * if (hayCambio) {
	 * 
	 * elemento.setPrecio_costo(tx_costo);
	 * elemento.setPrecio_pdv(tx_pconsumidor); elemento.setHayCambio(true); } }
	 * Log.i("ReportesGrillaActivity",
	 * "setDatosPrecioSanFernandoTradicionalGuardar. hayCambio = " + hayCambio);
	 * return hayCambio; }
	 */

	public Boolean setDatosPresenciaSanFernandoTradicionalGuardar(E_TBL_MOV_REP_PRESENCIA elemento) {
		Log.i("ReportesGrillaActivity", "... setDatosPresenciaSanFernandoTradicionalGuardar. codMAterialApoyo = " + elemento.getCod_material_apoyo());
		ArrayList<Object> arr = datosReporte.get(elemento.getSku_producto());
		Boolean hayCambio = null;
		if (arr == null) {
			Log.e("ReportesGrillaActivity", "datosReporte.get(" + elemento.getCod_observacion() + ") es null");
		} else {
			CheckBox editText = (CheckBox) arr.get(COLUMN_EDITABLE_0);
			boolean check = editText.isChecked();
			if (elemento.getCod_presencia() == null) {
				if (check) {
					hayCambio = true;
				}
			} else {

				if (elemento.getCod_presencia().equals("1")) {
					if (!check) {
						hayCambio = true;
					}
				} else {
					if (check) {
						hayCambio = true;
					}
				}
			}

			if (check && !infoRelevada) {
				infoRelevada = true;
			}

			HashMap<String, String> datosAnteriores = new HashMap<String, String>();
			datosAnteriores.put("cod_presencia", elemento.getCod_presencia());
			datosAnterioresList.add(datosAnteriores);

			if (hayCambio != null && hayCambio) {
				elemento.setCod_presencia(check ? "1" : "0");
				elemento.setHayCambio(true);
			}
		}
		Log.i("ReportesGrillaActivity", "setDatosPresenciaGuardar. hayCambio = " + hayCambio);
		return hayCambio;
	}

	public Boolean setDatosExhibicionSanFernandoTradicionalGuardar(E_ReporteExhibicionDet elemento) {
		Log.i("ReportesGrillaActivity", "... setDatosExhibicionSanFernandoTradicionalGuardar. Cod_exhib() = " + elemento.getCod_exhib());
		ArrayList<Object> arr = datosReporte.get(elemento.getCod_exhib());
		Boolean hayCambio = false;
		if (arr == null) {
			Log.e("ReportesGrillaActivity", "datosReporte.get(" + elemento.getCod_exhib() + ") es null");
		} else {
			CheckBox editText = (CheckBox) arr.get(COLUMN_EDITABLE_0);
			boolean check = editText.isChecked();
			if (elemento.getValor_exhib() == null) {
				if (check) {
					hayCambio = true;
				}
			} else {

				if (elemento.getValor_exhib().equals("1")) {
					if (!check) {
						hayCambio = true;
					}
				} else {
					if (check) {
						hayCambio = true;
					}
				}
			}

			if (check && !infoRelevada) {
				infoRelevada = true;
			}

			HashMap<String, String> datosAnteriores = new HashMap<String, String>();
			datosAnteriores.put("valor_exhib", elemento.getValor_exhib());
			datosAnterioresList.add(datosAnteriores);

			if (hayCambio != null && hayCambio) {

				elemento.setValor_exhib(check ? "1" : "0");
				elemento.setHayCambio(true);
			}
		}
		Log.i("ReportesGrillaActivity", "setDatosExhibicionSanFernandoTradicionalGuardar. hayCambio = " + hayCambio);
		return hayCambio;
	}

	public boolean setDatosEntregaMaterialesSanFernandoTradicionalGuardar(E_TblMovRepMaterialDeApoyo elemento) {
		Log.i("ReportesGrillaActivity", "... setDatosEntregaMaterialesSanFernandoTradicionalGuardar. codSKU = " + elemento.getCod_marial_apoyo());
		ArrayList<Object> arr = datosReporte .get(elemento.getCod_marial_apoyo());
		boolean hayCambio = false;
		if (arr == null) {
			Log.e("ReportesGrillaActivity", "datosReporte.get(" + elemento.getCod_marial_apoyo() + ") es null");
		} else {
			EditText et_cantidad = (EditText) arr.get(COLUMN_EDITABLE_0);
			String tx_cantidad = et_cantidad.getText().toString();

			if (elemento.getCantidad() == null) {
				if (!tx_cantidad.trim().equals("")) {
					hayCambio = true;
				}
			} else if (!elemento.getCantidad().equals(tx_cantidad)) {
				hayCambio = true;
			}

			if (!tx_cantidad.trim().equals("") && !infoRelevada) {
				infoRelevada = true;
			}

			HashMap<String, String> datosAnteriores = new HashMap<String, String>();
			datosAnteriores.put("cantidad", elemento.getCantidad());
			datosAnterioresList.add(datosAnteriores);

			if (hayCambio) {
				elemento.setCantidad(tx_cantidad);
				elemento.setHayCambio(true);
			}
		}
		Log.i("ReportesGrillaActivity",
				"setDatosEntregaMaterialesSanFernandoTradicionalGuardar. hayCambio = "
						+ hayCambio);
		return hayCambio;
	}

	public Boolean setDatosInicidenciasIncidSanFernandoTradicionalGuardar(E_ReporteIncidencia elemento) {
		Log.i("ReportesGrillaActivity", "... setDatosInicidenciasIncidSanFernandoTradicionalGuardar. codigo = " + elemento.getCod_incidencia());
		ArrayList<Object> arr = datosReporte.get(elemento.getCod_incidencia());
		Boolean hayCambio = false;
		if (arr == null) {
			Log.e("ReportesGrillaActivity", "datosReporte.get(" + elemento.getCod_incidencia() + ") es null");
		} else {
			CheckBox ck_marque = (CheckBox) arr.get(COLUMN_EDITABLE_0);
			boolean isMarque = ck_marque.isChecked();
			EditText et_cantidad = (EditText) arr.get(COLUMN_EDITABLE_1);
			String tx_cantidad = et_cantidad.getText().toString();

			if (elemento.getValor_incidencia() == null) {
				if (isMarque)
					hayCambio = true;
			} else {
				if (elemento.getValor_incidencia().equals("1")) {
					if (!isMarque)
						hayCambio = true;
				} else {
					if (isMarque)
						hayCambio = true;
				}
			}

			if (isMarque && !infoRelevada) {
				infoRelevada = true;
			}

			if (elemento.getHasCantidad() != null
					&& elemento.getHasCantidad().equalsIgnoreCase("1")) {

				if (elemento.getCantidad() == null) {
					if (!tx_cantidad.trim().equals("")) {
						hayCambio = true;
					}

				} else if (!elemento.getCantidad().equals(tx_cantidad)) {
					hayCambio = true;
				}

				if (!tx_cantidad.trim().equals("") && !infoRelevada) {
					infoRelevada = true;
				}
			}

			HashMap<String, String> datosAnteriores = new HashMap<String, String>();
			datosAnteriores.put("valor_incidencia", elemento.getValor_incidencia());
			datosAnteriores.put("cantidad", elemento.getCantidad());

			datosAnterioresList.add(datosAnteriores);

			if (hayCambio != null && hayCambio) {
				elemento.setValor_incidencia(isMarque ? "1" : "0");
				elemento.setCantidad(tx_cantidad);
				elemento.setHayCambio(true);
			}
		}
		Log.i("ReportesGrillaActivity", "setDatosInicidenciasIncidSanFernandoTradicionalGuardar. hayCambio = " + hayCambio);
		return hayCambio;
	}

	private Boolean setDatosPotencialRevestimientoSanFernandoAAVVGuardar(E_ReportePotencial elemento) {
		Log.i("ReportesGrillaActivity", "... setDatosPotencialRevestimientoSanFernandoAAVVGuardar. codigo = " + elemento.getCodMaterial());
		ArrayList<Object> arr = datosReporte.get(elemento.getCodMaterial());
		Boolean hayCambio = false;
		if (arr == null) {
			Log.e("ReportesGrillaActivity", "datosReporte.get(" + elemento.getCodMaterial() + ") es null");
		} else {
			CheckBox ck_cantidad = (CheckBox) arr.get(COLUMN_EDITABLE_0);
			boolean isChecked = ck_cantidad.isChecked();

			if (elemento.getValorCheck() == null) {
				if (isChecked)
					hayCambio = true;
			} else {
				if (elemento.getValorCheck().equals("1")) {
					if (!isChecked)
						hayCambio = true;
				} else {
					if (isChecked)
						hayCambio = true;
				}
			}

			if (isChecked && !infoRelevada) {
				infoRelevada = true;
			}

			HashMap<String, String> datosAnteriores = new HashMap<String, String>();
			datosAnteriores.put("valor_checked", elemento.getValorCheck());
			datosAnterioresList.add(datosAnteriores);
			if (hayCambio != null && hayCambio) {
				elemento.setValorCheck(isChecked ? "1" : "0");
				elemento.setCantidad(null);
				elemento.setHayCambio(true);
			}
		}
		Log.i("ReportesGrillaActivity", "setDatosPotencialRevestimientoSanFernandoAAVVGuardar. hayCambio = " + hayCambio);
		return hayCambio;

	}

	private Boolean setDatosPotencialPotencialSanFernandoAAVVGuardar(E_ReportePotencial elemento) {
		Log.i("ReportesGrillaActivity", "... setDatosPotencialPotencialSanFernandoAAVVGuardar. codigo = " + elemento.getCodMaterial());
		ArrayList<Object> arr = datosReporte.get(elemento.getCodMaterial());
		Boolean hayCambio = false;
		if (arr == null) {
			Log.e("ReportesGrillaActivity", "datosReporte.get(" + elemento.getCodMaterial() + ") es null");
		} else {
			EditText et_cantidad = (EditText) arr.get(COLUMN_EDITABLE_0);
			String tx_cantidad = et_cantidad.getText().toString();

			if (elemento.getCantidad() == null) {
				if (!tx_cantidad.trim().equals("")) {
					hayCambio = true;
				}

			} else if (!elemento.getCantidad().equals(tx_cantidad)) {
				hayCambio = true;
			}

			if (!tx_cantidad.trim().equals("") && !infoRelevada) {
				infoRelevada = true;
			}
			HashMap<String, String> datosAnteriores = new HashMap<String, String>();
			datosAnteriores.put("cantidad", elemento.getCantidad());

			datosAnterioresList.add(datosAnteriores);

			if (hayCambio != null && hayCambio) {
				elemento.setValorCheck(null);
				elemento.setCantidad(tx_cantidad);
				elemento.setHayCambio(true);
			}
		}
		Log.i("ReportesGrillaActivity", "setDatosPotencialPotencialSanFernandoAAVVGuardar. hayCambio = " + hayCambio);
		return hayCambio;

	}

	public Boolean setDatosAuditoriaSanFernandoAAVVGuardar(E_ReporteAuditoria elemento) {
		Log.i("ReportesGrillaActivity", "... setDatosAuditoriaSanFernandoAAVVGuardar. codigo = " + elemento.getCod_mat_apoyo());
		ArrayList<Object> arr = datosReporte.get(elemento.getCod_mat_apoyo());
		Boolean hayCambio = false;
		if (arr == null) {
			Log.e("ReportesGrillaActivity", "datosReporte.get(" + elemento.getCod_mat_apoyo() + ") es null");
		} else {
			CheckBox ck_marque = (CheckBox) arr.get(COLUMN_EDITABLE_0);
			boolean isMarque = ck_marque.isChecked();
			EditText et_cantidad = (EditText) arr.get(COLUMN_EDITABLE_1);
			String tx_cantidad = et_cantidad.getText().toString();

			if (elemento.getHasCheck() != null && elemento.getHasCheck().equalsIgnoreCase("1")) {
				if (elemento.getMat_apoyo_Check() == null) {
					if (isMarque)
						hayCambio = true;
				} else {
					if (elemento.getMat_apoyo_Check().equals("1")) {
						if (!isMarque)
							hayCambio = true;
					} else {
						if (isMarque)
							hayCambio = true;
					}
				}

				if (isMarque && !infoRelevada) {
					infoRelevada = true;
				}
			} else {
				if (elemento.getHasCantidad() != null && elemento.getHasCantidad().equalsIgnoreCase("1")) {

					if (elemento.getCantidad() == null) {
						if (!tx_cantidad.trim().equals("")) {
							isMarque = true;
							hayCambio = true;
						}

					} else if (!elemento.getCantidad().equals(tx_cantidad)) {
						isMarque = true;
						hayCambio = true;
					}

					if (!tx_cantidad.trim().equals("") && !infoRelevada) {
						infoRelevada = true;
					}
				}
			}

			HashMap<String, String> datosAnteriores = new HashMap<String, String>();
			datosAnteriores.put("mat_apoyo_check", elemento.getMat_apoyo_Check());
			datosAnteriores.put("cantidad", elemento.getCantidad());

			datosAnterioresList.add(datosAnteriores);

			if (hayCambio != null && hayCambio) {
				elemento.setMat_apoyo_Check(isMarque ? "1" : "0");
				elemento.setCantidad(tx_cantidad);
				elemento.setHayCambio(true);
			}
		}
		Log.i("ReportesGrillaActivity", "setDatosAuditoriaSanFernandoAAVVGuardar. hayCambio = " + hayCambio);
		return hayCambio;
	}

	public boolean setDatosPrecioSanFernandoTradicionalChikaraGuardar(E_ReportePrecio elemento) {
		Log.i("ReportesGrillaActivity", "... setDatosPrecioSanFernandoChikaraGuardar. codSKU = " + elemento.getSku_prod());
		ArrayList<Object> arr = datosReporte.get(elemento.getSku_prod());
		boolean hayCambio = false;
		if (arr == null) {
			Log.e("ReportesGrillaActivity", "datosReporte.get(" + elemento.getSku_prod() + ") es null");
		} else {
			EditText et_pvp = (EditText) arr.get(COLUMN_EDITABLE_0);
			String txt_pvp = et_pvp.getText().toString();

			EditText et_pvd = (EditText) arr.get(COLUMN_EDITABLE_1);
			String txt_pvd = et_pvd.getText().toString();

			if (elemento.getPrecio_pdv() == null) {
				if (!txt_pvp.trim().equals("")) {
					hayCambio = true;
				}
			} else if (!elemento.getPrecio_pdv().equals(txt_pvp)) {
				hayCambio = true;
			}
			if (elemento.getPrecio_pvd() == null) {
				if (!txt_pvd.trim().equals("")) {
					hayCambio = true;
				}
			} else if (!elemento.getPrecio_pvd().equals(txt_pvd)) {
				hayCambio = true;
			}
			if ((!txt_pvp.trim().equals("") || !txt_pvd.trim().equals("")) && !infoRelevada) {
				infoRelevada = true;
			}

			HashMap<String, String> datosAnteriores = new HashMap<String, String>();
			datosAnteriores.put("precio_pvp", elemento.getPrecio_pdv());
			datosAnteriores.put("precio_pvd", elemento.getPrecio_pvd());
			datosAnterioresList.add(datosAnteriores);

			if (hayCambio) {
				elemento.setPrecio_pdv(txt_pvp);
				elemento.setPrecio_pvd(txt_pvd);
				elemento.setHayCambio(true);
			}
		}
		Log.i("ReportesGrillaActivity", "setDatosPrecioSanFernandoChikaraGuardar. hayCambio = " + hayCambio);
		return hayCambio;
	}

	public Boolean setDatosPresenciaSanFernandoChikaraGuardar(E_TBL_MOV_REP_PRESENCIA elemento) {
		Log.i("ReportesGrillaActivity", "... setDatosPresenciaSanFernandoChikaraGuardar. codMAterialApoyo = " + elemento.getCod_material_apoyo());
		ArrayList<Object> arr = datosReporte.get(elemento.getSku_producto());
		Boolean hayCambio = null;
		if (arr == null) {
			Log.e("ReportesGrillaActivity", "datosReporte.get(" + elemento.getCod_presencia() + ") es null");
		} else {
			CheckBox editText = (CheckBox) arr.get(COLUMN_EDITABLE_0);
			boolean check = editText.isChecked();
			if (elemento.getCod_presencia() == null) {
				if (check) {
					hayCambio = true;
				}
			} else {

				if (elemento.getCod_presencia().equals("1")) {
					if (!check) {
						hayCambio = true;
					}
				} else {
					if (check) {
						hayCambio = true;
					}
				}
			}

			if (check && !infoRelevada) {
				infoRelevada = true;
			}

			HashMap<String, String> datosAnteriores = new HashMap<String, String>();
			datosAnteriores.put("cod_presencia", elemento.getCod_presencia());
			datosAnterioresList.add(datosAnteriores);

			if (hayCambio != null && hayCambio) {

				elemento.setCod_presencia(check ? "1" : "0");
				elemento.setHayCambio(true);
			}
		}
		Log.i("ReportesGrillaActivity", "setDatosPresenciaSanFernandoChikaraGuardar. hayCambio = " + hayCambio);
		return hayCambio;
	}

	public Boolean setDatosEncuestaSanFernandoChikaraGuardar(E_ReporteEncuesta elemento) {
		Log.i("ReportesGrillaActivity", "... setDatosEncuestaSanFernandoChikaraGuardar. codMAterialApoyo = " + elemento.getCodMaterial());
		ArrayList<Object> arr = datosReporte.get(elemento.getCodMaterial());
		Boolean hayCambio = null;
		if (arr == null) {
			Log.e("ReportesGrillaActivity", "datosReporte.get(" + elemento.getDescripcion() + ") es null");
		} else {
			CheckBox editText_si = (CheckBox) arr.get(COLUMN_EDITABLE_0);
			CheckBox editText_no = (CheckBox) arr.get(COLUMN_EDITABLE_1);
			boolean check_si = editText_si.isChecked();
			boolean check_no = editText_no.isChecked();
			if (elemento.getItemChecked() == null) {
				if (check_si || check_no) {
					hayCambio = true;
				}
			} else if (elemento.getItemChecked().equals("si")) {
					if (!check_si) {
						hayCambio = true;
					}
			} else if (elemento.getItemChecked().equals("no")) {
					if (!check_no) {
						hayCambio = true;
					}
			}

			if ((check_si || check_no) && !infoRelevada) {
				infoRelevada = true;
			}

			// *********************
			HashMap<String, String> datosAnteriores = new HashMap<String, String>();
			datosAnteriores.put("cod_material", elemento.getCodMaterial());
			datosAnteriores.put("item_check", elemento.getItemChecked());
			datosAnterioresList.add(datosAnteriores);

			if (hayCambio != null && hayCambio) {
				elemento.setItemChecked(check_si ? "si" : check_no ? "no" : "");
				elemento.setHayCambio(true);
			}
		}
		Log.i("ReportesGrillaActivity", "setDatosEncuestaSanFernandoChikaraGuardar. hayCambio = " + hayCambio);
		return hayCambio;
	}

	private Boolean fijarDatosCambiados() {
		Boolean res = null;
		infoRelevada = false;
		if (elementos != null) {
			datosAnterioresList = new ArrayList<HashMap<String, String>>();
			for (Object elementV : elementos) {
				Boolean c = null;
				switch (this.tipoReporte) {
				case TiposReportes.TIPO_PRESENCIA_VISIBILIDAD_COLGATE_COD_CANTIDAD:
				case TiposReportes.TIPO_PRESENCIA_VISIBILIDAD_COLGATE_COD_CANTIDAD_VENTANA:
					c = setDatosEvisibilidadGuardar((E_TBL_MOV_REP_PRESENCIA) elementV);
					break;

				case TiposReportes.TIPO_PRESENCIA_COLGATE_SUPERVISOR_SKUPRECIO:
				case TiposReportes.TIPO_PRESENCIA_COMPETENCIA_SUPERVISOR_SKUPRECIO:
					c = setDatosSKUPrecioProductoGuardar((E_TBL_MOV_REP_PRESENCIA) elementV);
					break;

				case TiposReportes.TIPO_PRESENCIA_COLGATE_GESTOR_SKUPRESENCIA:
				case TiposReportes.TIPO_PRESENCIA_COMPETENCIA_GESTOR_SKUPRESENCIA:
					// case
					// TiposReportes.TIPO_PRESENCIA_COLGATE_SKU_PRES_PRECIO_PEDIDO:
					c = setDatosSkuPresenGuardar((E_TBL_MOV_REP_PRESENCIA) elementV);
					break;

				case TiposReportes.TIPO_PRESENCIA_COLGATE_GESTOR_SKU_PRESENCIA_STOCK:
				case TiposReportes.TIPO_PRESENCIA_COMPETENCIA_GESTOR_SKU_PRESENCIA_STOCK:
					c = setDatosSkuPresenStockGuardar((E_TBL_MOV_REP_PRESENCIA) elementV);
					break;

				case TiposReportes.TIPO_PRESENCIA_VISIBILIDAD_COMPETENCIA_COD_PRECIO:
					c = setDatosSKUPrecioMaterialGuardar((E_TBL_MOV_REP_PRESENCIA) elementV);
					break;
				case TiposReportes.TIPO_PRESENCIA_VISIB_COMPETENCIA_COD_CANTIDAD:
					c = setDatosPresenVisibCompetCodCantidadGuardar((E_TBL_MOV_REP_PRESENCIA) elementV);
					break;
				case TiposReportes.TIPO_PRESENCIA_VISIB_COMPETENCIA_COD_CUENTA:
					c = setDatosPresenVisibCompetCodCuentaGuardar((E_TBL_MOV_REP_PRESENCIA) elementV);
					break;

				case TiposReportes.TIPO_PRESENCIA_OBSERVACIONES_COLGATE:
					c = setDatosObservacionGuardar((E_TBL_MOV_REP_PRESENCIA) elementV);
					break;

				case TiposReportes.TIPO_PRESENCIA_STOCK_COLGATE_SKU_STOCK:
				case TiposReportes.TIPO_PRESENCIA_STOCK_COMPETENCIA_SKU_STOCK:

					c = setDatosSKUStockGuardar((E_TBL_MOV_REP_PRESENCIA) elementV);
					break;

				case TiposReportes.TIPO_PRESENCIA_VISIBILIDAD_COLGATE_COD_CUENTA_CUMPLE:
					c = setDatosMaterialCuentaCumpleGuardar((E_TBL_MOV_REP_PRESENCIA) elementV);
					break;
				case TiposReportes.TIPO_PRESENCIA_COLGATE_SKU_FRENTE_PROFUNDIDAD:
					c = setDatosSkuFrenteProfGuardar((E_TBL_MOV_REP_PRESENCIA) elementV);
					break;
				// case
				// TiposReportes.TIPO_PRESENCIA_COMPETENCIA_SKU_PRES_PRECIO:
				// c = setDatosSkuPresenPrecioGuardar((E_TBL_MOV_REP_PRESENCIA)
				// elementV);
				// break;
				case TiposReportes.TIPO_PRESENCIA_CLUSTER_PREGUNTA_MARQUE_CANTIDAD:
					c = setDatosClusterPreguntaMarqueCantidadGuardar((E_TBL_MOV_REP_PRESENCIA) elementV);
					break;
				case TiposReportes.TIPO_PRECIOS_ALICORP_SKU_PPDV_POFERTA_MOBS:
					c = setDatosPrecioAlicorpAutoservicioGuardar((E_ReportePrecio) elementV);
					break;
				case TiposReportes.TIPO_PRECIOS_ALICORP_SKU_PMAYORISTA_PREVENTA_POFERTA_MOBS:
					c = setDatosPrecioAlicorpMayoristasGuardar((E_ReportePrecio) elementV);
					break;
				/*
				 * case TiposReportes.TIPO_PRECIOS_AAVV_SANFERNANDO: c =
				 * setDatosPrecioSanFernandoAAVVGuardar((E_ReportePrecio)
				 * elementV); break;
				 */
				case TiposReportes.TIPO_PRECIOS_SF_MODERNO:
					c = setDatosPrecioSanFernandoModernoGuardar((E_ReportePrecio) elementV);
					break;
				/*
				 * case TiposReportes.TIPO_PRECIOS_TRADICIONAL_SANFERNANDO: c =
				 * setDatosPrecioSanFernandoTradicionalGuardar((E_ReportePrecio)
				 * elementV); break;
				 */
				case TiposReportes.TIPO_QUIEBRES_ALICORP_SKU_MOBS:
					c = setDatosQuiebreAlicorpAutoservicioGuardar((E_ReporteQuiebre) elementV);
					break;
				case TiposReportes.TIPO_STOCK_ALICORP_COD_STOCK_MOBS:
					c = setDatosStockAlicorpMayoristasGuardar((E_ReporteStock) elementV);
					break;

				/*
				 * case TiposReportes.TIPO_VENTAS_AAVV_SANFERNANDO: c =
				 * setDatosVentasSanFernandoAAVVGuardar((E_ReporteStock)
				 * elementV); break;
				 */
				case TiposReportes.TIPO_INGRESOS_SF_MODERNO:
					c = setDatosIngresosSanFernandoModernoGuardar((E_ReporteStock) elementV);
					break;

				case TiposReportes.TIPO_IMPULSO_SF_MODERNO:
					c = setDatosImpulsoSanFernandoModernoGuardar((E_ReporteImpulso) elementV);
					break;
				/*
				 * case TiposReportes.TIPO_PRESENCIA_TRADICIONAL_SANFERNANDO: c
				 * = setDatosPresenciaSanFernandoTradicionalGuardar((
				 * E_TBL_MOV_REP_PRESENCIA) elementV); break;
				 * 
				 * case TiposReportes.TIPO_EXHIBICION_TRADICIONAL_SANFERNANDO: c
				 * = setDatosExhibicionSanFernandoTradicionalGuardar((
				 * E_ReporteExhibicionDet) elementV); break; case
				 * TiposReportes.TIPO_ENTREGA_MATERIALES_TRADICIONAL_SANFERNANDO
				 * : c =
				 * setDatosEntregaMaterialesSanFernandoTradicionalGuardar((
				 * E_TblMovRepMaterialDeApoyo) elementV); break; case
				 * TiposReportes
				 * .TIPO_ASESORAMIENTO_PROD_TRADICIONAL_SANFERNANDO: c =
				 * setDatosAsesoramientoProdSanFernandoTradicionalGuardar
				 * ((E_ReporteCapacitacion) elementV); break; case
				 * TiposReportes.
				 * TIPO_INCIDENCIAS_STATUS_TRADICIONAL_SANFERNANDO:
				 * 
				 * if (!((E_ReporteIncidencia) elementV).getCod_status()
				 * .equalsIgnoreCase("3")) { c =
				 * setDatosInicidenciasStatusSanFernandoTradicionalGuardar
				 * ((E_ReporteIncidencia) elementV); } else { // int
				 * numPedidosSelected = 0;
				 * 
				 * // for (int j = 0; j < opcPedidos.size(); j++) { //
				 * E_TblMstOpcPedido pedido = (E_TblMstOpcPedido) //
				 * opcPedidos.get(j); // if(pedido.isSelected()){ //
				 * numPedidosSelected=numPedidosSelected+1; // } //
				 * if(elementosPedidos.size() < numPedidosSelected){ //
				 * elementos.add(new E_ReporteIncidencia(idCabecera, // "3",
				 * null)); // } // } c =
				 * setDatosInicidenciasStatusSanFernandoTradicionalGuardarPedido
				 * ((E_ReporteIncidencia) elementV);
				 * 
				 * } break; case
				 * TiposReportes.TIPO_INCIDENCIAS_INCID_TRADICIONAL_SANFERNANDO:
				 * c = setDatosInicidenciasIncidSanFernandoTradicionalGuardar((
				 * E_ReporteIncidencia) elementV); break;
				 */
				case TiposReportes.TIPO_CODIGOS_ITT_COLGATE:
					c = setDatosITTColgateBodegaGuardar((E_TBL_MOV_REP_COD_NEW_ITT) elementV);
					break;
				case TiposReportes.TIPO_POTENCIAL_REVESTIMIENTO_SF_AAVV:
					c = setDatosPotencialRevestimientoSanFernandoAAVVGuardar((E_ReportePotencial) elementV);
					break;
				case TiposReportes.TIPO_POTENCIAL_POTENCIAL_SF_AAVV:
					c = setDatosPotencialPotencialSanFernandoAAVVGuardar((E_ReportePotencial) elementV);
					break;
				case TiposReportes.TIPO_PRECIOPVP_SF_AAVV:
					c = setDatosPrecioPVPSanFernandoAAVVGuardar((E_ReportePrecio) elementV);
					break;
				case TiposReportes.TIPO_PRECIOPDV_PDV_SF_AAVV:
					c = setDatosPrecioPDV_PDVSanFernandoAAVVGuardar((E_ReportePrecio) elementV);
					break;
				case TiposReportes.TIPO_PRECIOPDV_OBS_SF_AAVV:
					c = setDatosPrecioPDV_OBSSanFernandoAAVVGuardar((E_ReportePrecio) elementV);
					break;
				case TiposReportes.TIPO_AUDITORIA_PLASTAZUL_SF_AAVV:
				case TiposReportes.TIPO_AUDITORIA_REJBLANCA_SF_AAVV:
					c = setDatosAuditoriaSanFernandoAAVVGuardar((E_ReporteAuditoria) elementV);
					break;
				case TiposReportes.TIPO_PRECIOS_SF_TRADICIONAL_CHIKARA:
					c = setDatosPrecioSanFernandoTradicionalChikaraGuardar((E_ReportePrecio) elementV);
					break;
				case TiposReportes.TIPO_PRESENCIA_PRODUCTOS_SF_TRADICIONAL_CHIKARA:
					c = setDatosPresenciaSanFernandoChikaraGuardar((E_TBL_MOV_REP_PRESENCIA) elementV);
					break;
				case TiposReportes.TIPO_ENCUESTAS_SF_TRADICIONAL_CHIKARA:
					c = setDatosEncuestaSanFernandoChikaraGuardar((E_ReporteEncuesta) elementV);
					break;
				default:
					break;
				}

				if (res == null) {
					res = c;
				} else {
					if (c != null) {
						res = res || c;
					}
				}

			}
			if (!infoRelevada) {
				res = null;
			}

		}
		return res;
	}

	private boolean revertirDatosCambiados() {
		boolean res = false;
		if (elementos != null) {
			for (int i = 0; i < elementos.size(); i++) {
				Object elementV = elementos.get(i);
				if (i < datosAnterioresList.size()) {
					HashMap<String, String> datosAnteriores = datosAnterioresList.get(i);

					switch (this.tipoReporte) {
					case TiposReportes.TIPO_PRESENCIA_VISIBILIDAD_COLGATE_COD_CANTIDAD:
					case TiposReportes.TIPO_PRESENCIA_VISIBILIDAD_COLGATE_COD_CANTIDAD_VENTANA:
						((E_TBL_MOV_REP_PRESENCIA) elementV).setValor_material_apoyo(datosAnteriores.get("valor_material_apoyo"));
						break;

					case TiposReportes.TIPO_PRESENCIA_COLGATE_SUPERVISOR_SKUPRECIO:
					case TiposReportes.TIPO_PRESENCIA_COMPETENCIA_SUPERVISOR_SKUPRECIO:
						((E_TBL_MOV_REP_PRESENCIA) elementV).setPrecio(datosAnteriores.get("precio"));
						break;

					case TiposReportes.TIPO_PRESENCIA_COMPETENCIA_GESTOR_SKUPRESENCIA:
					case TiposReportes.TIPO_PRESENCIA_COLGATE_GESTOR_SKUPRESENCIA:
						((E_TBL_MOV_REP_PRESENCIA) elementV).setCod_presencia(datosAnteriores.get("cod_presencia"));
						break;

					case TiposReportes.TIPO_PRESENCIA_COLGATE_GESTOR_SKU_PRESENCIA_STOCK:
					case TiposReportes.TIPO_PRESENCIA_COMPETENCIA_GESTOR_SKU_PRESENCIA_STOCK:
						((E_TBL_MOV_REP_PRESENCIA) elementV).setCod_presencia(datosAnteriores.get("cod_presencia"));
						((E_TBL_MOV_REP_PRESENCIA) elementV).setStock(datosAnteriores.get("stock"));
						break;

					case TiposReportes.TIPO_PRESENCIA_VISIBILIDAD_COMPETENCIA_COD_PRECIO:
						((E_TBL_MOV_REP_PRESENCIA) elementV).setPrecio(datosAnteriores.get("precio"));
						break;

					case TiposReportes.TIPO_PRESENCIA_VISIB_COMPETENCIA_COD_CANTIDAD:
						((E_TBL_MOV_REP_PRESENCIA) elementV).setCantidad(datosAnteriores.get("cantidad"));
						break;

					case TiposReportes.TIPO_PRESENCIA_VISIB_COMPETENCIA_COD_CUENTA:
						((E_TBL_MOV_REP_PRESENCIA) elementV).setValor_material_apoyo(datosAnteriores.get("valor_material_apoyo"));
						break;

					case TiposReportes.TIPO_PRESENCIA_OBSERVACIONES_COLGATE:
						((E_TBL_MOV_REP_PRESENCIA) elementV).setObservacion(datosAnteriores.get("observacion"));
						break;

					case TiposReportes.TIPO_PRESENCIA_STOCK_COLGATE_SKU_STOCK:
					case TiposReportes.TIPO_PRESENCIA_STOCK_COMPETENCIA_SKU_STOCK:
						((E_TBL_MOV_REP_PRESENCIA) elementV).setStock(datosAnteriores.get("stock"));
						break;

					case TiposReportes.TIPO_PRESENCIA_VISIBILIDAD_COLGATE_COD_CUENTA_CUMPLE:
						((E_TBL_MOV_REP_PRESENCIA) elementV).setValor_material_apoyo(datosAnteriores.get("valor_material_apoyo"));
						((E_TBL_MOV_REP_PRESENCIA) elementV).setCumple_layout(datosAnteriores.get("cumple_layout"));
						break;

					case TiposReportes.TIPO_PRESENCIA_COLGATE_SKU_FRENTE_PROFUNDIDAD:
						((E_TBL_MOV_REP_PRESENCIA) elementV).setNum_frentes(datosAnteriores.get("num_frentes"));
						((E_TBL_MOV_REP_PRESENCIA) elementV).setProfundidad(datosAnteriores.get("profundidad"));
						((E_TBL_MOV_REP_PRESENCIA) elementV).setPrecio(datosAnteriores.get("precio"));
						((E_TBL_MOV_REP_PRESENCIA) elementV).setPedido_sugerido(datosAnteriores.get("pedido_sugerido"));
						break;

					case TiposReportes.TIPO_PRESENCIA_CLUSTER_PREGUNTA_MARQUE_CANTIDAD:
						((E_TBL_MOV_REP_PRESENCIA) elementV).setCantidad(datosAnteriores.get("cantidad"));
						break;

					case TiposReportes.TIPO_PRECIOS_ALICORP_SKU_PPDV_POFERTA_MOBS:
						((E_ReportePrecio) elementV).setPrecio_pdv(datosAnteriores.get("precio_pdv"));
						((E_ReportePrecio) elementV).setPrecio_oferta(datosAnteriores.get("precio_oferta"));
						((E_ReportePrecio) elementV).setCod_motivo_obs(datosAnteriores.get("mobs"));
						break;

					case TiposReportes.TIPO_PRECIOS_ALICORP_SKU_PMAYORISTA_PREVENTA_POFERTA_MOBS:
						((E_ReportePrecio) elementV).setPrecio_mayorista(datosAnteriores.get("precio_mayorista"));
						((E_ReportePrecio) elementV).setPrecio_reventa(datosAnteriores.get("precio_reventa"));
						((E_ReportePrecio) elementV).setPrecio_oferta(datosAnteriores.get("precio_oferta"));
						((E_ReportePrecio) elementV).setCod_motivo_obs(datosAnteriores.get("mobs"));
						break;

					case TiposReportes.TIPO_PRECIOS_SF_MODERNO:
						((E_ReportePrecio) elementV).setPrecio_oferta(datosAnteriores.get("precio_oferta"));
						((E_ReportePrecio) elementV).setPrecio_regular(datosAnteriores.get("precio_regular"));
						break;

					case TiposReportes.TIPO_QUIEBRES_ALICORP_SKU_MOBS:
						((E_ReporteQuiebre) elementV).setCod_motivo_quiebre(datosAnteriores.get("cod_quiebre"));
						break;
					case TiposReportes.TIPO_STOCK_ALICORP_COD_STOCK_MOBS:
						((E_ReporteStock) elementV).setStock(datosAnteriores.get("stock"));
						((E_ReporteStock) elementV).setCod_motivo_obs(datosAnteriores.get("mobs"));
						break;
					case TiposReportes.TIPO_INGRESOS_SF_MODERNO:
						((E_ReporteStock) elementV).setExhibicion(datosAnteriores.get("exhibicion"));
						((E_ReporteStock) elementV).setCamara(datosAnteriores.get("camara"));
						((E_ReporteStock) elementV).setStock(datosAnteriores.get("stock"));
						((E_ReporteStock) elementV).setCod_motivo_obs(datosAnteriores.get("mobs"));
						break;

					case TiposReportes.TIPO_IMPULSO_SF_MODERNO:
						((E_ReporteImpulso) elementV).setIngreso(datosAnteriores.get("ingreso"));
						((E_ReporteImpulso) elementV).setStock_final(datosAnteriores.get("stock_final"));
						break;

					case TiposReportes.TIPO_PRESENCIA_PRODUCTOS_SF_TRADICIONAL_CHIKARA:
						((E_TBL_MOV_REP_PRESENCIA) elementV).setCod_presencia(datosAnteriores.get("cod_presencia"));
						break;

					case TiposReportes.TIPO_CODIGOS_ITT_COLGATE:
						((E_TBL_MOV_REP_COD_NEW_ITT) elementV).setId_distribuidora(datosAnteriores.get("codigodistribuidora"));
						((E_TBL_MOV_REP_COD_NEW_ITT) elementV).setCodigo_ITT(datosAnteriores.get("codigoitt"));
						break;
					case TiposReportes.TIPO_POTENCIAL_REVESTIMIENTO_SF_AAVV:
						((E_ReportePotencial) elementV).setValorCheck(datosAnteriores.get("valor_check"));
						((E_ReportePotencial) elementV).setHayCambio(false);
						break;
					case TiposReportes.TIPO_POTENCIAL_POTENCIAL_SF_AAVV:
						((E_ReportePotencial) elementV).setCantidad(datosAnteriores.get("cantidad"));
						((E_ReportePotencial) elementV).setHayCambio(false);
						break;
					case TiposReportes.TIPO_PRECIOPVP_SF_AAVV:
						((E_ReportePrecio) elementV).setPrecio_lista(datosAnteriores.get("precio_pvp"));
						((E_ReportePrecio) elementV).setHayCambio(false);
						break;
					case TiposReportes.TIPO_PRECIOPDV_PDV_SF_AAVV:
						((E_ReportePrecio) elementV).setPrecio_pdv(datosAnteriores.get("precio_pdv"));
						((E_ReportePrecio) elementV).setCod_tipo_precio(datosAnteriores.get("cod_tipo_precio"));
						((E_ReportePrecio) elementV).setHayCambio(false);
						break;
					case TiposReportes.TIPO_PRECIOPDV_OBS_SF_AAVV:
						((E_ReportePrecio) elementV).setPrecio_pdv(datosAnteriores.get("observacion"));
						((E_ReportePrecio) elementV).setHayCambio(false);
						break;
					case TiposReportes.TIPO_AUDITORIA_PLASTAZUL_SF_AAVV:
					case TiposReportes.TIPO_AUDITORIA_REJBLANCA_SF_AAVV:
						((E_ReporteAuditoria) elementV).setMat_apoyo_Check(datosAnteriores.get("mat_apoyo_check"));
						((E_ReporteAuditoria) elementV).setCantidad(datosAnteriores.get("cantidad"));
						((E_ReporteAuditoria) elementV).setHayCambio(false);
						break;
					case TiposReportes.TIPO_PRECIOS_SF_TRADICIONAL_CHIKARA:
						((E_ReportePrecio) elementV).setPrecio_pdv(datosAnteriores.get("precio_pvp"));
						((E_ReportePrecio) elementV).setPrecio_pvd(datosAnteriores.get("precio_pvd"));
						break;

					default:
						break;
					}
				}

				if (tipoReporte == TiposReportes.TIPO_PRECIOS_ALICORP_SKU_PPDV_POFERTA_MOBS
						|| tipoReporte == TiposReportes.TIPO_PRECIOS_ALICORP_SKU_PMAYORISTA_PREVENTA_POFERTA_MOBS
						|| tipoReporte == TiposReportes.TIPO_PRECIOS_SF_MODERNO
						|| tipoReporte == TiposReportes.TIPO_PRECIOPVP_SF_AAVV
						|| tipoReporte == TiposReportes.TIPO_PRECIOPDV_PDV_SF_AAVV
						|| tipoReporte == TiposReportes.TIPO_PRECIOPDV_OBS_SF_AAVV
						|| tipoReporte == TiposReportes.TIPO_PRECIOS_SF_TRADICIONAL_CHIKARA) {
					((E_ReportePrecio) elementV).setHayCambio(false);
				} else if (tipoReporte == TiposReportes.TIPO_QUIEBRES_ALICORP_SKU_MOBS) {
					((E_ReporteQuiebre) elementV).setHayCambio(false);
				} else if (tipoReporte == TiposReportes.TIPO_STOCK_ALICORP_COD_STOCK_MOBS
						|| tipoReporte == TiposReportes.TIPO_INGRESOS_SF_MODERNO) {
					((E_ReporteStock) elementV).setHayCambio(false);
				}/*
				 * else if (tipoReporte ==
				 * TiposReportes.TIPO_VENTAS_AAVV_SANFERNANDO) {
				 * ((E_ReporteStock) elementV).setHayCambio(false); }
				 */
				/*
				 * else if (tipoReporte ==
				 * TiposReportes.TIPO_IMPULSO_MODERNO_SANFERNANDO) {
				 * ((E_ReporteImpulso) elementV).setHayCambio(false); } else if
				 * (tipoReporte ==
				 * TiposReportes.TIPO_EXHIBICION_TRADICIONAL_SANFERNANDO) {
				 * ((E_ReporteExhibicionDet) elementV).setHayCambio(false); }
				 * else if (tipoReporte ==
				 * TiposReportes.TIPO_ENTREGA_MATERIALES_TRADICIONAL_SANFERNANDO
				 * ) { ((E_TblMovRepMaterialDeApoyo)
				 * elementV).setHayCambio(false); } else if (tipoReporte ==
				 * TiposReportes
				 * .TIPO_ASESORAMIENTO_PROD_TRADICIONAL_SANFERNANDO) {
				 * ((E_ReporteCapacitacion) elementV).setHayCambio(false); }
				 * else if (tipoReporte ==
				 * TiposReportes.TIPO_INCIDENCIAS_STATUS_TRADICIONAL_SANFERNANDO
				 * || tipoReporte ==
				 * TiposReportes.TIPO_INCIDENCIAS_INCID_TRADICIONAL_SANFERNANDO)
				 * { ((E_ReporteIncidencia) elementV).setHayCambio(false); }
				 */else if (tipoReporte == TiposReportes.TIPO_CODIGOS_ITT_COLGATE) {
					((E_TBL_MOV_REP_COD_NEW_ITT) elementV).setHayCambio(false);
				} else if (tipoReporte == TiposReportes.TIPO_AUDITORIA_PLASTAZUL_SF_AAVV
						|| tipoReporte == TiposReportes.TIPO_AUDITORIA_REJBLANCA_SF_AAVV) {
					((E_ReporteAuditoria) elementV).setHayCambio(false);
				} else if (tipoReporte == TiposReportes.TIPO_POTENCIAL_POTENCIAL_SF_AAVV
						|| tipoReporte == TiposReportes.TIPO_POTENCIAL_REVESTIMIENTO_SF_AAVV) {
					((E_ReportePotencial) elementV).setHayCambio(false);
				} else if (tipoReporte == TiposReportes.TIPO_ENCUESTAS_SF_TRADICIONAL_CHIKARA) {
					((E_ReporteEncuesta) elementV).setHayCambio(false);
				} else {
					((E_TBL_MOV_REP_PRESENCIA) elementV).setHayCambio(false);
				}
			}
		}
		return res;
	}

	public void guardarReporte() {
		if (tipoReporte == TiposReportes.TIPO_CODIGOS_ITT_COLGATE) {
			reportesController.insert_update_ReporteCodigoITT(elementos, idCabecera);
		} else {
			for (Object elementV : elementos) {

				// if (tipoReporte ==
				// TiposReportes.TIPO_PRECIOS_ALICORP_SKU_PPDV_POFERTA_MOBS ||
				// tipoReporte ==
				// TiposReportes.TIPO_PRECIOS_ALICORP_SKU_PMAYORISTA_PREVENTA_POFERTA_MOBS
				// || tipoReporte == TiposReportes.TIPO_PRECIOS_AAVV_SANFERNANDO
				// || tipoReporte ==
				// TiposReportes.TIPO_PRECIOS_MODERNO_SANFERNANDO || tipoReporte
				// == TiposReportes.TIPO_PRECIOS_TRADICIONAL_SANFERNANDO) {
				if (tipoReporte == TiposReportes.TIPO_PRECIOS_ALICORP_SKU_PPDV_POFERTA_MOBS
						|| tipoReporte == TiposReportes.TIPO_PRECIOS_ALICORP_SKU_PMAYORISTA_PREVENTA_POFERTA_MOBS
						|| tipoReporte == TiposReportes.TIPO_PRECIOS_SF_MODERNO
						|| tipoReporte == TiposReportes.TIPO_PRECIOPVP_SF_AAVV
						|| tipoReporte == TiposReportes.TIPO_PRECIOPDV_PDV_SF_AAVV
						|| tipoReporte == TiposReportes.TIPO_PRECIOPDV_OBS_SF_AAVV
						|| tipoReporte == TiposReportes.TIPO_PRECIOS_SF_TRADICIONAL_CHIKARA) {
					E_ReportePrecio repPrecio = (E_ReportePrecio) elementV;
					reportesController.insert_update_ReportePrecio(repPrecio, tipoReporte);
				} else if (tipoReporte == TiposReportes.TIPO_QUIEBRES_ALICORP_SKU_MOBS) {
					E_ReporteQuiebre repQuiebre = (E_ReporteQuiebre) elementV;
					reportesController.insert_update_ReporteQuiebre(repQuiebre);
					// } else if (tipoReporte ==
					// TiposReportes.TIPO_STOCK_ALICORP_COD_STOCK_MOBS ||
					// tipoReporte ==
					// TiposReportes.TIPO_INGRESOS_MODERNO_SANFERNANDO ||
					// tipoReporte ==
					// TiposReportes.TIPO_VENTAS_AAVV_SANFERNANDO) {
				} else if (tipoReporte == TiposReportes.TIPO_STOCK_ALICORP_COD_STOCK_MOBS
						|| tipoReporte == TiposReportes.TIPO_INGRESOS_SF_MODERNO) {
					E_ReporteStock repStock = (E_ReporteStock) elementV;
					reportesController.insert_update_ReporteStock(repStock,
							tipoReporte);
				} else if (tipoReporte == TiposReportes.TIPO_IMPULSO_SF_MODERNO) {
					E_ReporteImpulso repImpulso = (E_ReporteImpulso) elementV;
					reportesController.insert_update_ReporteImpulso(repImpulso);
				} /*
				 * else if (tipoReporte ==
				 * TiposReportes.TIPO_ENTREGA_MATERIALES_TRADICIONAL_SANFERNANDO
				 * ) { E_TblMovRepMaterialDeApoyo repEM =
				 * (E_TblMovRepMaterialDeApoyo) elementV;
				 * repMaterialApoyoController
				 * .insert_update_ReporteEntregaMaterlaies(repEM); } else if
				 * (tipoReporte ==
				 * TiposReportes.TIPO_ASESORAMIENTO_PROD_TRADICIONAL_SANFERNANDO
				 * ) { E_ReporteCapacitacion repCap = (E_ReporteCapacitacion)
				 * elementV; reportesController
				 * .insert_update_ReporteCapacitacion(repCap); } else if
				 * (tipoReporte ==
				 * TiposReportes.TIPO_INCIDENCIAS_STATUS_TRADICIONAL_SANFERNANDO
				 * || tipoReporte ==
				 * TiposReportes.TIPO_INCIDENCIAS_INCID_TRADICIONAL_SANFERNANDO)
				 * { E_ReporteIncidencia repIncidencia = (E_ReporteIncidencia)
				 * elementV; reportesController.insert_update_ReporteIncidencia(
				 * repIncidencia, tipoReporte); } else if (tipoReporte ==
				 * TiposReportes.TIPO_EXHIBICION_TRADICIONAL_SANFERNANDO) {
				 * detRepExhibicion.add((E_ReporteExhibicionDet) elementV); }
				 */else if (tipoReporte == TiposReportes.TIPO_POTENCIAL_REVESTIMIENTO_SF_AAVV
						|| tipoReporte == TiposReportes.TIPO_POTENCIAL_POTENCIAL_SF_AAVV) {
					E_ReportePotencial repPotencial = (E_ReportePotencial) elementV;
					reportesController.insert_update_ReportePotencial(repPotencial, tipoReporte);
				} else if (tipoReporte == TiposReportes.TIPO_AUDITORIA_PLASTAZUL_SF_AAVV
						|| tipoReporte == TiposReportes.TIPO_AUDITORIA_REJBLANCA_SF_AAVV) {
					E_ReporteAuditoria repAuditoria = (E_ReporteAuditoria) elementV;
					reportesController.insert_update_ReporteAuditoria(repAuditoria);
				} else if (tipoReporte == TiposReportes.TIPO_ENCUESTAS_SF_TRADICIONAL_CHIKARA) {
					E_ReporteEncuesta repEncuesta = (E_ReporteEncuesta) elementV;
					reportesController.insert_update_ReporteEncuestas(repEncuesta);
				} else {
					E_TBL_MOV_REP_PRESENCIA rpPres = (E_TBL_MOV_REP_PRESENCIA) elementV;
					reporteController.createReporte(rpPres, this.tipoReporte);
				}
			}
			/*
			 * if (tipoReporte ==
			 * TiposReportes.TIPO_EXHIBICION_TRADICIONAL_SANFERNANDO) {
			 * E_ReporteExhibicion exhib = reportesController
			 * .getReporteExhibByIdCab(idCabecera); if (exhib == null) { exhib =
			 * new E_ReporteExhibicion(); exhib.setId_reporte_cab(idCabecera); }
			 * exhib.setDetalles(detRepExhibicion);
			 * reportesController.insert_update_ReporteExhibicion(exhib); }
			 */
		}
	}

	public String validarDatos() {
		String msg = "";
		switch (tipoReporte) {
		case TiposReportes.TIPO_CODIGOS_ITT_COLGATE:
			msg = validarReporteCodigosITTColgate();
			break;
		case TiposReportes.TIPO_PRECIOS_ALICORP_SKU_PPDV_POFERTA_MOBS:
			msg = validarReportePrecioAlicorpAS();
			break;
		case TiposReportes.TIPO_PRECIOS_ALICORP_SKU_PMAYORISTA_PREVENTA_POFERTA_MOBS:
			msg = validarReportePrecioAlicorpMayorista();
			break;
		/*
		 * case TiposReportes.TIPO_PRECIOS_AAVV_SANFERNANDO: msg =
		 * validarReportePrecioSanFdoAAVV(); break;
		 */
		case TiposReportes.TIPO_PRECIOS_SF_MODERNO:
			msg = validarReportePrecioSanFdoModerno();
			break;
		/*
		 * case TiposReportes.TIPO_PRECIOS_TRADICIONAL_SANFERNANDO: msg =
		 * validarReportePrecioSanFdoTradicional(); break;
		 */
		case TiposReportes.TIPO_STOCK_ALICORP_COD_STOCK_MOBS:
			msg = validarReporteStockAlicorpMayorista();
			break;
		case TiposReportes.TIPO_INGRESOS_SF_MODERNO:
			msg = validarReporteIngresosSanFdoModerno();
			break;
		/*
		 * case TiposReportes.TIPO_VENTAS_AAVV_SANFERNANDO: msg =
		 * validarReporteVentaSanFdoModerno(); break;
		 */
		case TiposReportes.TIPO_IMPULSO_SF_MODERNO:
			msg = validarReporteImpulsoSanFdoModerno();
			break;
		case TiposReportes.TIPO_QUIEBRES_ALICORP_SKU_MOBS:
			break;
		/*
		 * case TiposReportes.TIPO_ENTREGA_MATERIALES_TRADICIONAL_SANFERNANDO:
		 * break; case
		 * TiposReportes.TIPO_ASESORAMIENTO_PROD_TRADICIONAL_SANFERNANDO: break;
		 * case TiposReportes.TIPO_EXHIBICION_TRADICIONAL_SANFERNANDO: break;
		 * case TiposReportes.TIPO_INCIDENCIAS_STATUS_TRADICIONAL_SANFERNANDO:
		 * break; case
		 * TiposReportes.TIPO_INCIDENCIAS_INCID_TRADICIONAL_SANFERNANDO: msg =
		 * validarReporteIncidenciaIncidSanFdoTradicional(); break;
		 */
		case TiposReportes.TIPO_POTENCIAL_REVESTIMIENTO_SF_AAVV:
			msg = validarReportePotencialRevestimientoSanFdoAAVV();
			break;
		case TiposReportes.TIPO_POTENCIAL_POTENCIAL_SF_AAVV:
			msg = validarReportePotencialPotencialSanFdoAAVV();
			break;
		case TiposReportes.TIPO_PRECIOPVP_SF_AAVV:
			msg = validarReportePrecioPVPSanFdoAAVV();
			break;
		case TiposReportes.TIPO_PRECIOPDV_PDV_SF_AAVV:
			msg = validarReportePrecioPDV_PDVSanFdoAAVV();
			break;
		case TiposReportes.TIPO_PRECIOPDV_OBS_SF_AAVV:
		case TiposReportes.TIPO_AUDITORIA_PLASTAZUL_SF_AAVV:
		case TiposReportes.TIPO_AUDITORIA_REJBLANCA_SF_AAVV:
			break;
		case TiposReportes.TIPO_PRECIOS_SF_TRADICIONAL_CHIKARA:
			msg = validarReportePrecioSanFdoTradicionalChikara();
			break;
		case TiposReportes.TIPO_ENCUESTAS_SF_TRADICIONAL_CHIKARA:
			msg = validarReporteEncuestaSanFdoChikara();
			break;
		default:
			msg = validarReportePresenciaColgate();
			break;
		}

		return msg;
	}

	private String validarReportePrecioAlicorpAS() {
		boolean isValido = true;
		String msg = "";
		int i = 0;
		int cont_campos_null = 0;
		int cont_pdv = 0;
		int cont_oferta = 0;
		int cont_sin_motivo = 0;
		int cont_sin_motivo_ok = 0;
		int cont_sin_pdv = 0;
		int cont_sin_oferta = 0;
		for (Object elemento : elementos) {
			E_ReportePrecio repPrecio = (E_ReportePrecio) elemento;
			if ((repPrecio.getPrecio_pdv() == null || repPrecio.getPrecio_pdv()
					.trim().isEmpty())
					&& (repPrecio.getPrecio_oferta() == null || repPrecio
							.getPrecio_oferta().trim().isEmpty())
					&& (repPrecio.getCod_motivo_obs() == null || repPrecio
							.getCod_motivo_obs().equalsIgnoreCase("0"))) {
				isValido &= false;
				cont_campos_null++;
			}
			if ((repPrecio.getPrecio_pdv() != null && !repPrecio
					.getPrecio_pdv().trim().isEmpty())
					&& (repPrecio.getPrecio_pdv().startsWith(".") || Float
							.parseFloat(repPrecio.getPrecio_pdv()) == 0)) {
				isValido &= false;
				cont_pdv++;
			}
			if ((repPrecio.getPrecio_oferta() != null && !repPrecio
					.getPrecio_oferta().trim().isEmpty())
					&& (repPrecio.getPrecio_oferta().startsWith(".") || Float
							.parseFloat(repPrecio.getPrecio_oferta()) == 0)) {
				isValido &= false;
				cont_oferta++;
			}
			if (repPrecio.getPrecio_oferta() != null
					&& !repPrecio.getPrecio_oferta().trim().isEmpty()
					&& repPrecio.getPrecio_pdv() != null
					&& repPrecio.getPrecio_pdv().isEmpty()) {
				isValido &= false;
				cont_sin_pdv++;
			}
			if (repPrecio.getPrecio_oferta() != null
					&& !repPrecio.getPrecio_oferta().trim().isEmpty()
					&& (repPrecio.getCod_motivo_obs() == null || repPrecio
							.getCod_motivo_obs().equalsIgnoreCase("0"))) {
				isValido &= false;
				cont_sin_motivo++;
			}
			if (repPrecio.getPrecio_oferta() != null
					&& !repPrecio.getPrecio_oferta().trim().isEmpty()
					&& repPrecio.getCod_motivo_obs() != null
					&& (repPrecio.getCod_motivo_obs().equalsIgnoreCase("D") || repPrecio
							.getCod_motivo_obs().equalsIgnoreCase("F"))) {
				isValido &= false;
				cont_sin_motivo_ok++;
			}
			if (repPrecio.getPrecio_oferta() != null
					&& repPrecio.getPrecio_oferta().trim().isEmpty()
					&& repPrecio.getCod_motivo_obs() != null
					&& !(repPrecio.getCod_motivo_obs().equalsIgnoreCase("D") || repPrecio
							.getCod_motivo_obs().equalsIgnoreCase("F"))) {
				isValido &= false;
				cont_sin_oferta++;
			}
			i++;
		}
		if (!isValido) {
			if (cont_campos_null == i) {
				msg = "No se ha relevado informaci�n";
			} else {
				if (cont_pdv > 0) {
					msg += "El precio PVP no puede ser 0 ni empezar por .";
				}
				if (cont_oferta > 0) {
					msg += "\nEl precio de Oferta no puede ser 0 ni empezar por .";
				}
				if (cont_sin_pdv > 0) {
					msg += "\nEl precio de PVP no puede estar vac�o";
				}
				if (cont_sin_motivo > 0) {
					msg += "\nDebe seleccionar un tipo de Oferta";
				}
				if (cont_sin_motivo_ok > 0) {
					msg += "\nDebe seleccionar un tipo de Oferta V�lido";
				}
				if (cont_sin_oferta > 0) {
					msg += "\nEl precio de Oferta no puede estar vac�o";
				}
			}
		}
		return msg;
	}

	private String validarReportePrecioAlicorpMayorista() {
		boolean isValido = true;
		String msg = "";
		int i = 0;
		int cont_campos_null = 0;
		int cont_mayor = 0;
		int cont_reventa = 0;
		int cont_oferta = 0;
		for (Object elemento : elementos) {
			E_ReportePrecio repPrecio = (E_ReportePrecio) elemento;
			if ((repPrecio.getPrecio_mayorista() == null || repPrecio
					.getPrecio_mayorista().trim().isEmpty())
					&& (repPrecio.getPrecio_reventa() == null || repPrecio
							.getPrecio_reventa().trim().isEmpty())
					&& (repPrecio.getPrecio_oferta() == null || repPrecio
							.getPrecio_oferta().trim().isEmpty())
					&& (repPrecio.getCod_motivo_obs() == null || repPrecio
							.getCod_motivo_obs().equalsIgnoreCase(
									((E_MotivoObservacion) opciones.get(0))
											.getCod_motivo()))) {
				isValido &= false;
				cont_campos_null++;
			}
			if ((repPrecio.getPrecio_mayorista() != null
					&& !repPrecio.getPrecio_mayorista().trim().isEmpty() && repPrecio
					.getPrecio_mayorista().startsWith("."))
					|| (repPrecio.getPrecio_mayorista() != null
							&& !repPrecio.getPrecio_mayorista().trim()
									.isEmpty() && Float.parseFloat(repPrecio
							.getPrecio_mayorista()) == 0)) {
				isValido &= false;
				cont_mayor++;
			}
			if ((repPrecio.getPrecio_reventa() != null
					&& !repPrecio.getPrecio_reventa().trim().isEmpty() && repPrecio
					.getPrecio_reventa().startsWith("."))
					|| (repPrecio.getPrecio_reventa() != null
							&& !repPrecio.getPrecio_reventa().trim().isEmpty() && Float
							.parseFloat(repPrecio.getPrecio_reventa()) == 0)) {
				isValido &= false;
				cont_reventa++;
			}
			if ((repPrecio.getPrecio_oferta() != null
					&& !repPrecio.getPrecio_oferta().trim().isEmpty() && repPrecio
					.getPrecio_oferta().startsWith("."))
					|| (repPrecio.getPrecio_oferta() != null
							&& !repPrecio.getPrecio_oferta().trim().isEmpty() && Float
							.parseFloat(repPrecio.getPrecio_oferta()) == 0)) {
				isValido &= false;
				cont_oferta++;
			}
			i++;
		}
		if (!isValido) {
			if (cont_campos_null == i) {
				msg = "No se ha relevado informaci�n";
			} else {
				if (cont_mayor > 0) {
					msg += "El Precio Mayorista no puede ser 0 ni empezar por .  ";
				}
				if (cont_reventa > 0) {
					msg += "\nEl Precio Reventa no puede ser 0 ni empezar por .  ";
				}
				if (cont_oferta > 0) {
					msg += "\nEl Precio Oferta no puede ser 0 ni empezar por .  ";
				}
			}
		}
		return msg;
	}

	private String validarReportePrecioPVPSanFdoAAVV() {
		boolean isValido = true;
		String msg = "";
		for (Object elemento : elementos) {
			E_ReportePrecio repPrecio = (E_ReportePrecio) elemento;
			if ((repPrecio.getPrecio_lista() != null
					&& !repPrecio.getPrecio_lista().trim().isEmpty() && repPrecio
					.getPrecio_lista().startsWith("."))
					|| (repPrecio.getPrecio_lista() != null
							&& !repPrecio.getPrecio_lista().trim().isEmpty() && Float
							.parseFloat(repPrecio.getPrecio_lista()) == 0)) {
				isValido &= false;
				msg += "El Precio PVP no puede ser 0 ni empezar por .";
			}
			if (!isValido) {
				break;
			}
		}
		return msg;
	}

	private String validarReportePrecioPDV_PDVSanFdoAAVV() {
		boolean isValido = true;
		String msg = "";
		for (Object elemento : elementos) {
			E_ReportePrecio repPrecio = (E_ReportePrecio) elemento;
			if ((repPrecio.getPrecio_pdv() != null
					&& !repPrecio.getPrecio_pdv().trim().isEmpty() && repPrecio
					.getPrecio_pdv().startsWith("."))
					|| (repPrecio.getPrecio_pdv() != null
							&& !repPrecio.getPrecio_pdv().trim().isEmpty() && Float
							.parseFloat(repPrecio.getPrecio_pdv()) == 0)) {
				isValido &= false;
				msg += "El Precio PDV no puede ser 0 ni empezar por .";
			}
			if (!isValido) {
				break;
			}
		}
		return msg;
	}

	private String validarReportePrecioSanFdoModerno() {
		boolean isValido = true;
		String msg = "";
		for (Object elemento : elementos) {
			E_ReportePrecio repPrecio = (E_ReportePrecio) elemento;
			if ((repPrecio.getPrecio_oferta() != null && !repPrecio.getPrecio_oferta().trim().isEmpty() && repPrecio.getPrecio_oferta().startsWith("."))
					|| (repPrecio.getPrecio_oferta() != null && !repPrecio.getPrecio_oferta().trim().isEmpty() && Float.parseFloat(repPrecio.getPrecio_oferta()) == 0)) {
				isValido &= false;
				msg += "El Precio de oferta no puede ser 0 ni empezar por .";
			}
			if ((repPrecio.getPrecio_regular() != null && !repPrecio.getPrecio_regular().trim().isEmpty() && repPrecio.getPrecio_regular().startsWith("."))
					|| (repPrecio.getPrecio_regular() != null && !repPrecio.getPrecio_regular().trim().isEmpty() && Float.parseFloat(repPrecio.getPrecio_regular()) == 0)) {
				isValido &= false;
				msg += "\nEl Precio regular no puede ser 0 ni empezar por .";
			}
			if (!isValido) {
				break;
			}
		}
		return msg;
	}

	private String validarReporteStockAlicorpMayorista() {
		boolean isValido = true;
		String msg = "";
		for (Object elemento : elementos) {
			E_ReporteStock repStock = (E_ReporteStock) elemento;
			if ((repStock.getStock() != null
					&& !repStock.getStock().trim().isEmpty() && repStock
					.getStock().startsWith("."))) {
				isValido &= false;
				msg += "\nEl Stock no puede empezar por .";
			}
			if (repStock.getStock() != null
					&& repStock.getStock().trim().isEmpty()
					&& repStock.getCod_motivo_obs() == null) {
				isValido &= false;
				msg += "\nEl Stock no puede estar vac�o";
			}
			if (!isValido) {
				break;
			}
		}
		return msg;
	}

	private String validarReporteIngresosSanFdoModerno() {
		boolean isValido = true;
		String msg = "";
		for (Object elemento : elementos) {
			E_ReporteStock repStock = (E_ReporteStock) elemento;
			if ((repStock.getExhibicion() != null
					&& !repStock.getExhibicion().trim().isEmpty() && repStock
					.getExhibicion().startsWith("."))
					|| (repStock.getExhibicion() != null
							&& !repStock.getExhibicion().trim().isEmpty() && Float
							.parseFloat(repStock.getExhibicion()) == 0)) {
				isValido &= false;
				msg += "\nExhibici�n no puede ser 0 ni empezar por .";
			}
			if ((repStock.getCamara() != null
					&& !repStock.getCamara().trim().isEmpty() && repStock
					.getCamara().startsWith("."))
					|| (repStock.getCamara() != null
							&& !repStock.getCamara().trim().isEmpty() && Float
							.parseFloat(repStock.getCamara()) == 0)) {
				isValido &= false;
				msg += "\nC�mara no puede ser 0 ni empezar por .";
			}
			if (repStock.getExhibicion() != null
					&& repStock.getExhibicion().trim().isEmpty()
					&& repStock.getCod_motivo_obs() == null) {
				isValido &= false;
				msg += "\nExhibici�n no puede estar vac�a";
			}
			if (repStock.getCamara() != null
					&& repStock.getCamara().trim().isEmpty()
					&& repStock.getCod_motivo_obs() == null) {
				isValido &= false;
				msg += "\nC�mara no puede estar vac�a";
			}
			if (!isValido) {
				break;
			}
		}
		return msg;
	}

	private String validarReporteImpulsoSanFdoModerno() {
		boolean isValido = true;
		String msg = "";
		for (Object elemento : elementos) {
			E_ReporteImpulso repImpulso = (E_ReporteImpulso) elemento;
			if ((repImpulso.getIngreso() != null && !repImpulso.getIngreso().trim().isEmpty() && repImpulso.getIngreso().startsWith("."))
					|| (repImpulso.getIngreso() != null && !repImpulso.getIngreso().trim().isEmpty() && Float.parseFloat(repImpulso.getIngreso()) == 0)) {
				isValido &= false;
				msg += "\nIngreso no puede ser 0 ni empezar por .";
			}
			if ((repImpulso.getStock_final() != null && !repImpulso.getStock_final().trim().isEmpty() && repImpulso.getStock_final().startsWith("."))
					|| (repImpulso.getStock_final() != null && !repImpulso.getStock_final().trim().isEmpty() && Float.parseFloat(repImpulso.getStock_final()) == 0)) {
				isValido &= false;
				msg += "\nStock Final no puede ser 0 ni empezar por .";
			}

			if (repImpulso.getIngreso() != null && repImpulso.getIngreso().trim().isEmpty()) {
				isValido &= false;
				msg += "\nIngreso no puede estar vac�o. ";
			}
			if (repImpulso.getStock_final() != null && repImpulso.getStock_final().trim().isEmpty()) {
				isValido &= false;
				msg += "\nStock Final no puede estar vac�o. ";
			}
			if (!isValido) {
				break;
			}
		}
		return msg;
	}

	private String validarReporteCodigosITTColgate() {
		boolean isValido = true;
		String msg = "";
		for (Object elemento : elementos) {
			E_TBL_MOV_REP_COD_NEW_ITT repCodigoITT = (E_TBL_MOV_REP_COD_NEW_ITT) elemento;
			if (repCodigoITT.getId_distribuidora() != null && repCodigoITT.getCodigo_ITT() != null && repCodigoITT.getCodigo_ITT().trim().isEmpty()) {
				isValido &= false;
				msg += "El c�digo ITT no puede estar vac�o. ";
			}
			/*
			 * if (isRegistroITTRepetido(repCodigoITT, i)) { isValido &= false;
			 * msg += "La distribuidora " + repCodigoITT.getId_distribuidora() +
			 * ": " + ((DistribuidoraVo)
			 * distribuidoras.get(repCodigoITT.getPosDist(
			 * ))).getNomDistribuidora() + " ya tiene asignado el c�digo itt: "
			 * + repCodigoITT.getCodigo_ITT(); }
			 */

			if (!isValido) {
				break;
			}
		}
		return msg;
	}

	private String validarReportePresColgateDT_Gestor() {
		boolean isValido = true;
		String msg = "";
		for (Object elemento : elementos) {
			E_TBL_MOV_REP_PRESENCIA repPres = (E_TBL_MOV_REP_PRESENCIA) elemento;
			if ((repPres.getNum_frentes() != null && !repPres.getNum_frentes().trim().isEmpty() && repPres.getNum_frentes().startsWith("."))
					|| (repPres.getNum_frentes() != null && !repPres.getNum_frentes().trim().isEmpty() && Float.parseFloat(repPres.getNum_frentes()) == 0)) {
				isValido &= false;
				msg = "El Frente no puede ser 0 ni empezar por .";
			}
			if ((repPres.getProfundidad() != null
					&& !repPres.getProfundidad().trim().isEmpty() && repPres.getProfundidad().startsWith(".")) || (repPres.getProfundidad() != null
							&& !repPres.getProfundidad().trim().isEmpty() && Float.parseFloat(repPres.getProfundidad()) == 0)) {
				isValido &= false;
				msg += "\nLa profundidad no puede ser 0 ni empezar por .";
			}

			if (!isValido) {
				break;
			}
		}
		return msg;
	}

	private String validarReportePresStockColgate() {
		boolean isValido = true;
		String msg = "";
		for (Object elemento : elementos) {
			E_TBL_MOV_REP_PRESENCIA repPres = (E_TBL_MOV_REP_PRESENCIA) elemento;
			if (repPres.getStock() != null && !repPres.getStock().trim().isEmpty() && Float.parseFloat(repPres.getStock()) == 0) {
				isValido &= false;
				msg = "El stock no puede ser 0";
			}else if(repPres.getStock() != null && !repPres.getStock().trim().isEmpty() && Float.parseFloat(repPres.getStock()) < 0){
				isValido &= false;
				msg = "El stock debe ser mayor que 0";
			}
			if (!isValido) {
				break;
			}
		}
		return msg;
	}

	private String validarReportePresenciaColgate() {
		boolean isValido = true;
		String msg = "";
		for (Object elemento : elementos) {
			E_TBL_MOV_REP_PRESENCIA repPres = (E_TBL_MOV_REP_PRESENCIA) elemento;
			if ((repPres.getPrecio() != null && !repPres.getPrecio().trim().isEmpty() && repPres.getPrecio().startsWith("."))
					|| (repPres.getPrecio() != null && !repPres.getPrecio().trim().isEmpty() && Float.parseFloat(repPres.getPrecio()) == 0)) {
				isValido &= false;
				msg += "El precio no puede ser 0 ni empezar por .";
			}
			switch (TiposReportes.getInstancia(context).getIDSubReportefromMap(keyReporte)) {
			case TiposReportes.TIPO_PRESENCIA_STOCK_COLGATE_SKU_STOCK:
			case TiposReportes.TIPO_PRESENCIA_STOCK_COMPETENCIA_SKU_STOCK:
				msg = validarReportePresStockColgate();
				break;
			case TiposReportes.TIPO_PRESENCIA_COLGATE_GESTOR_SKUPRESENCIA:
			case TiposReportes.TIPO_PRESENCIA_COMPETENCIA_GESTOR_SKUPRESENCIA:
				break;
			case TiposReportes.TIPO_PRESENCIA_COLGATE_SKU_FRENTE_PROFUNDIDAD:
				msg = validarReportePresColgateDT_Gestor();
				break;

			case TiposReportes.TIPO_PRESENCIA_COLGATE_GESTOR_SKU_PRESENCIA_STOCK:
			case TiposReportes.TIPO_PRESENCIA_COMPETENCIA_GESTOR_SKU_PRESENCIA_STOCK:
				msg = validarReportePresStockColgate();
				break;
			}

			if (!isValido) {
				break;
			}
		}
		return msg;
	}

	private String validarReportePotencialRevestimientoSanFdoAAVV() {
		String msg = "";
		int contVacios = 0;
		for (Object elemento : elementos) {
			E_ReportePotencial repPotencial = (E_ReportePotencial) elemento;
			if (repPotencial.getValorCheck() == null || repPotencial.getValorCheck().trim().isEmpty() || repPotencial.getValorCheck().equalsIgnoreCase("0")) {
				contVacios++;
			}
		}
		if (contVacios == elementos.size()) {
			msg = "No se ha relevado informaci�n";
		}
		return msg;
	}

	private String validarReporteEncuestaSanFdoChikara() {
		String msg = "";
		int contVacios = 0;
		int dobleRespuesta = 0;
		for (Object elemento : elementos) {
			E_ReporteEncuesta repEncuesta = (E_ReporteEncuesta) elemento;
			if (repEncuesta.getItemChecked() == null || repEncuesta.getItemChecked().trim().isEmpty() || repEncuesta.getItemChecked().equalsIgnoreCase("")) {
				contVacios++;
			} else {
				if (repEncuesta.getItemChecked().equalsIgnoreCase("sino")) {
					dobleRespuesta++;
				}
			}
		}
		if (contVacios == elementos.size()) {
			msg = "No se ha relevado informaci�n";
		} else {
			if (dobleRespuesta > 0)
				msg = "No se pueden chekear 2 respuestas a la misma pregunta";
		}
		return msg;
	}

	private String validarReportePotencialPotencialSanFdoAAVV() {
		boolean isValido = true;
		boolean onlyTotal = false;
		int contVacios = 0;
		String msg = "";
		double suma = 0;
		String totalCompra = "";
		for (int i=0; i<elementos.size(); i++) {
			E_ReportePotencial repPotencial = (E_ReportePotencial) elementos.get(i);
			if(i==0){
				if(repPotencial.getCantidad() == null || repPotencial.getCantidad().trim().isEmpty()){
					isValido &= false;
					msg += "El total de la compra no puede estar vac�o";
				}else{
					onlyTotal = true;
					totalCompra = String.valueOf(Double.parseDouble(repPotencial.getCantidad()));
				}
			}
			else{
				if (repPotencial.getCantidad() != null && !repPotencial.getCantidad().trim().isEmpty()) {
					onlyTotal = false;
					if (repPotencial.getCantidad().equalsIgnoreCase("0")) {
						isValido &= false;
						msg += "La cantidad no puede ser 0.";
					}else{
						suma += Double.parseDouble(repPotencial.getCantidad());
						Log.i("Reporte Potencial - Potencial", "suma: " + suma + " de cantidad: " + repPotencial.getCantidad());
					}
				} else {
					contVacios++;
				}
			}
			if (!isValido) {
				break;
			}
		}
		if (contVacios == elementos.size()) {
			msg = "No se ha relevado informaci�n";
		}else if(onlyTotal){
			msg = "Debe registrar al menos una cantidad";
		}
		else{
			String s_suma = String.valueOf(suma);
			int posPunto = s_suma.indexOf(".");
			posPunto +=3;
			if(posPunto<s_suma.length()){
				s_suma = s_suma.substring(0, s_suma.indexOf(".")+3);
			}
			if(!totalCompra.trim().isEmpty() && !s_suma.equalsIgnoreCase(totalCompra)){
				msg = "El total de la compra (" + totalCompra + ") no coincide con la suma de cantidades (" + s_suma +")";
				Log.i("Reporte Potencial - Potencial", "suma: " + msg);
			}
		}
		return msg;
	}

	private String validarReportePrecioSanFdoTradicionalChikara() {
		boolean isValido = true;
		String msg = "";
		for (Object elemento : elementos) {
			E_ReportePrecio repPrecio = (E_ReportePrecio) elemento;
			if ((repPrecio.getPrecio_pdv() != null && !repPrecio.getPrecio_pdv().trim().isEmpty() && repPrecio.getPrecio_pdv().startsWith("."))
					|| (repPrecio.getPrecio_pdv() != null && !repPrecio.getPrecio_pdv().trim().isEmpty() && Float.parseFloat(repPrecio.getPrecio_pdv()) == 0)) {
				isValido &= false;
				msg += "El precio PVP no puede ser 0 ni empezar por .";
			}
			if ((repPrecio.getPrecio_pvd() != null && !repPrecio.getPrecio_pvd().trim().isEmpty() && repPrecio.getPrecio_pvd().startsWith("."))
					|| (repPrecio.getPrecio_pvd() != null && !repPrecio.getPrecio_pvd().trim().isEmpty() && Float.parseFloat(repPrecio.getPrecio_pvd()) == 0)) {
				isValido &= false;
				msg += "\nEl precio PVD no puede ser 0 ni empezar por .";
			}
			if (!isValido) {
				break;
			}
		}
		return msg;
	}

	public void createRow(TableLayout table, final TableRow row, EditText et1, EditText et2, EditText et3, EditText et4, final CheckBox ck1, final CheckBox ck2, final int index, boolean ini, final String textSubtitulo, Spinner sp, final String key) {
		// int colorFila = context.getResources().getColor(R.color.azulclaro);

		if (ini) {
			ini = false;
		}
		if (index % 2 == 0) {
			row.setBackgroundColor(colorFila);
		} else {
			row.setBackgroundColor(Color.WHITE);
		}

		if (et1 != null) {
			et1.setOnFocusChangeListener(new OnFocusChangeListener() {

				public void onFocusChange(View v, boolean hasFocus) {
					onClickFila(v, textSubtitulo, index, "", row);
				}
			});
		}
		if (et2 != null) {
			Log.i("*", "et2 != null");
			et2.setOnFocusChangeListener(new OnFocusChangeListener() {

				public void onFocusChange(View v, boolean hasFocus) {
					onClickFila(v, textSubtitulo, index, "", row);

				}
			});
		}
		if (et3 != null) {
			Log.i("*", "et3 != null");
			et3.setOnFocusChangeListener(new OnFocusChangeListener() {

				public void onFocusChange(View v, boolean hasFocus) {
					onClickFila(v, textSubtitulo, index, "", row);
				}
			});
		}
		if (et4 != null) {
			Log.i("*", "et4 != null");
			et4.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					onClickFila(v, textSubtitulo, index, "", row);

				}
			});
		}

		if (ck1 != null) {
			Log.i("*", "ck1 != null");
			ck1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				public void onCheckedChanged(CompoundButton v, boolean isChecked) {
					Log.i("OnCheckedChanged", "checkbox 1 chequeado en = " + isChecked);
					ck1.setChecked(isChecked);
					onClickFila(v, textSubtitulo, index, "", row);
					switch(tipoReporte){
					case TiposReportes.TIPO_POTENCIAL_REVESTIMIENTO_SF_AAVV:
						if(isChecked){
							validarUnChecked(index);
						}
					break;
					case TiposReportes.TIPO_ENCUESTAS_SF_TRADICIONAL_CHIKARA:
						if (isChecked) {
							if (ck2.isChecked()) {
								ck2.setChecked(false);
							}
						}
						break;
					case TiposReportes.TIPO_PRECIOPDV_OBS_SF_AAVV:
						if(isChecked){
							validarUnChecked(index);
						}
					break;
					}
				}
			});
		}

		if (ck2 != null) {
			ck2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				public void onCheckedChanged(CompoundButton v, boolean isChecked) {
					Log.i("OnCheckedChanged", "checkbox 2 chequeado en = " + isChecked);
					ck2.setChecked(isChecked);
					onClickFila(v, textSubtitulo, index, "", row);
					switch(tipoReporte){
					case TiposReportes.TIPO_ENCUESTAS_SF_TRADICIONAL_CHIKARA:
						if (isChecked) {
							if (ck1.isChecked()) {
								ck1.setChecked(false);
							}
						}
						break;
					}
				}
			});
		}

		if (sp != null) {
			Log.i("*", "sp != null");
			sp.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					onClickFila(v, textSubtitulo, index, "", row);

				}
			});
		}

		row.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickFila(v, textSubtitulo, index, key, null);
			}
		});

		// Add the TableRow to the TableLayout
		table.addView(row, new TableLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

	}
	
	private void validarUnChecked(int index){
		int numElementos = 0;
		if (elementos != null && (numElementos = elementos.size()) > 0) {
			for (int i = 0; i < numElementos; i++) {
				ArrayList<Object> arr = null;
				switch(tipoReporte){
				case TiposReportes.TIPO_POTENCIAL_REVESTIMIENTO_SF_AAVV:
					arr = datosReporte.get(((E_ReportePotencial)elementos.get(i)).getCodMaterial());
					break;
				case TiposReportes.TIPO_PRECIOPDV_OBS_SF_AAVV:
					arr = datosReporte.get(((E_ReportePrecio)elementos.get(i)).getCod_motivo_obs());
					break;
				}
				if(arr!=null){
					CheckBox ck_ = (CheckBox) arr.get(COLUMN_EDITABLE_0);
					if(i!=index){
						ck_.setChecked(false);
						ck_.invalidate();
					}
				}
			}
		} else {
			Toast.makeText(this.context,"No hay items registrados para este reporte",Toast.LENGTH_SHORT).show();
		}
	}

	public void createRowTextViews(TableLayout table, final TableRow row, TextView et1, final int index, boolean ini, final String textSubtitulo, final String key) {
		// int colorFila = context.getResources().getColor(R.color.azulclaro);

		if (ini) {
			ini = false;
		}
		if (index % 2 == 0) {
			row.setBackgroundColor(colorFila);
		} else {
			row.setBackgroundColor(Color.WHITE);
		}

		if (et1 != null) {
			et1.setOnFocusChangeListener(new OnFocusChangeListener() {

				public void onFocusChange(View v, boolean hasFocus) {
					onClickFila(v, textSubtitulo, index, "", row);
				}
			});
		}

		row.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickFila(v, textSubtitulo, index, key, null);
			}
		});

		// Add the TableRow to the TableLayout
		table.addView(row, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

	}

	@Override
	public View getView() {
		// TODO Auto-generated method stub
		return view;
	}

	public Boolean isReporteCambio() {
		reporteCambio = fijarDatosCambiados();
		Log.i("ReportesGrillaActivity", "... isReporteCambio() = " + reporteCambio);
		return reporteCambio;
	}

	@Override
	public void setHandler(Handler handler) {
		// TODO Auto-generated method stub

	}

	public List<Object> getElementos() {
		return elementos;
	}

	public E_TBL_MOV_REP_PRESENCIA hayElementoVentana() {
		E_TBL_MOV_REP_PRESENCIA res = null;

		res = (new TblMstMaterialApoyoController(db)).getVentana(true, 2, idCabecera);

		return res;
	}

	@Override
	public void setReporteCambio(boolean reporteCambio) {
		// TODO Auto-generated method stub
		this.reporteCambio = reporteCambio;
		if (!reporteCambio) {
			revertirDatosCambiados();
		}
	}

	private boolean isRegistroITTRepetido(E_TBL_MOV_REP_COD_NEW_ITT repCodigoITT, int pos) {
		boolean isRepetido = false;
		if (repCodigoITT != null) {
			for (int i = 0; i < elementos.size(); i++) {
				E_TBL_MOV_REP_COD_NEW_ITT rep = (E_TBL_MOV_REP_COD_NEW_ITT) elementos
						.get(i);
				if (i != pos) {
					if (repCodigoITT.getId_distribuidora().equalsIgnoreCase(
							rep.getId_distribuidora())) {
						if (rep.getCodigo_ITT() != null) {
							if (repCodigoITT.getCodigo_ITT() != null
									&& repCodigoITT.getCodigo_ITT()
											.equalsIgnoreCase(
													rep.getCodigo_ITT())) {
								isRepetido = true;
								break;
							}
						}
					}
				}
			}
		}
		return isRepetido;

	}

	@Override
	public void setIdFiltro(int idFiltro) {
	}

	// ******************************************************************************************************

	private ArrayList<DistribuidoraVo> distribuidoras = new ArrayList<DistribuidoraVo>();

	public void show_reporte_itt_colgate_bodega(boolean agregar) {
		distribuidoras = distribuidorasController.consultarDistribuidoras();
		if (!agregar) {
			elementos = (new TblMovRepNewCodigoITTController(db)).getElementsForGrid(idCabecera);
			if (elementos == null)
				elementos = new ArrayList<Object>();
		}

		final TableLayout table = (TableLayout) view.findViewById(R.id.tl_reporte_itt_distribuidoracodigo);
		table.removeAllViews();
		int numElementos = 0;
		boolean ini = true;
		if (elementos != null && (numElementos = elementos.size()) > 0) {
			datosReporte = new HashMap<String, ArrayList<Object>>();
			for (int i = 0; i < numElementos; i++) {
				datosFila = new ArrayList<Object>();
				final E_TBL_MOV_REP_COD_NEW_ITT mA = (E_TBL_MOV_REP_COD_NEW_ITT) elementos.get(i);
				mA.setCodigoRep(i);
				String codDistribuidora = mA.getId_distribuidora();
				if (codDistribuidora != null) {
					if (distribuidoras != null) {
						mA.setPosDist(obtenerPosicionDistribuidora(codDistribuidora));
					}
				}

				TableRow row = (TableRow) inflator.inflate(R.layout.ly_reporte_itt_distribuidoracodigoitt_body, null);
				// ((TextView)
				// row.findViewById(R.id.tv_sku)).setText(mA.getSku_producto());
				final EditText et_itt = (EditText) row.findViewById(R.id.et_codigoitt);
				// if(!agregar || mA.getCodigo_ITT()!=null){
				et_itt.setText(mA.getCodigo_ITT());
				// }
				et_itt.invalidate();
				et_itt.setSelected(true);
				et_itt.setFilters(new InputFilter[] { new CustomTextWatcher(et_itt) });
				datosFila.add(et_itt);

				et_itt.addTextChangedListener(new TextWatcher() {

					public void afterTextChanged(Editable s) {
						String filtered_str = s.toString();
						mA.setCodigo_ITT(filtered_str);
						System.out.println(filtered_str + "");
					}

					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					}

					public void onTextChanged(CharSequence s, int start, int before, int count) {
					}
				});

				final Spinner spinnerDist = (Spinner) row.findViewById(R.id.spinnerDist);
				// datosFila.add(spinnerDist);

				if (distribuidoras != null) {
					String[] distrbs = new String[distribuidoras.size()];
					for (int j = 0; j < distribuidoras.size(); j++) {
						DistribuidoraVo distribuidora = (DistribuidoraVo) distribuidoras.get(j);
						distrbs[j] = distribuidora.getNomDistribuidora();
					}
					ArrayAdapter<String> adaptadorDistb = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item,distrbs);
					adaptadorDistb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

					spinnerDist.setAdapter(adaptadorDistb);

					spinnerDist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
								public void onItemSelected(AdapterView<?> parent, android.view.View v, int position, long id) {
									mA.setPosDist(position);
									mA.setId_distribuidora(distribuidoras.get(position).getCodDistribuidora() + "");
									et_itt.requestFocus();
								}

								public void onNothingSelected(AdapterView<?> parent) {
									mA.setPosDist(0);
									mA.setId_distribuidora("0");
									et_itt.requestFocus();
								}
							});

					if (mA.getPosDist() > 0) {
						spinnerDist.setSelection(mA.getPosDist());
					}
				}

				final ImageButton imgBorrarDist = (ImageButton) row.findViewById(R.id.imgBorrarDist);

				final int jf = i;
				imgBorrarDist.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						table.removeViewAt(jf);
						elementos.remove(jf);
						show_reporte_itt_colgate_bodega(true);
					}
				});
				datosFila.add(imgBorrarDist);
				String key = i + "";

				// createRow(table, row, et_itt, null, null, null, null, null,
				// i, ini,
				// distribuidoras.get(mA.getPosDist()).getNomDistribuidora(),
				// spinnerDist, key);
				createRow(table, row, et_itt, null, null, null, null, null, i, ini, "", spinnerDist, key);

				Log.i("ReportesGrillaActivity", "... datosReporte.put. key = " + key);
				datosReporte.put(key, datosFila);
			}
			table.invalidate();
		}
	}

	private int obtenerPosicionDistribuidora(String codDistribuidora) {
		// TODO Auto-generated method stub
		int pos = 0;
		int valor = 0;
		for (DistribuidoraVo dist : distribuidoras) {
			if (codDistribuidora.equalsIgnoreCase(dist.getCodDistribuidora())) {
				valor = pos;
				break;
			}
			pos++;
		}
		return valor;
	}

	public void agregarDistribuidora() {
		// TODO Auto-generated method stub
		E_TBL_MOV_REP_COD_NEW_ITT itt = new E_TBL_MOV_REP_COD_NEW_ITT();
		itt.setId_reporte_cab(idCabecera);
		elementos.add(itt);

		if (distribuidoras == null) {
			Toast.makeText(this.context, "No hay distribuidoras descargadas para este reporte", Toast.LENGTH_SHORT).show();
		} else {
			show_reporte_itt_colgate_bodega(true);
		}
	}

	public Boolean setDatosITTColgateBodegaGuardar(E_TBL_MOV_REP_COD_NEW_ITT elemento) {
		Log.i("ReportesGrillaActivity", "... setDatosCodigoITTGuardar. Posicion = " + elemento.getCodigoRep());

		ArrayList<Object> arr = datosReporte.get(elemento.getCodigoRep() + "");
		Boolean hayCambio = true;

		if (arr == null) {
			Log.e("ReportesGrillaActivity", "datosReporte.get(" + elemento.getCodigoRep() + ") es null");
		} else {
			EditText editText = (EditText) arr.get(COLUMN_EDITABLE_0);
			String texto = editText.getText().toString();

			if (elemento.getCodigo_ITT() == null) {
				if (!texto.trim().equals("")) {
					hayCambio = true;
				}
			} else if (!elemento.getCodigo_ITT().equals(texto)) {
				hayCambio = true;
			}

			if (!texto.trim().equals("") && !infoRelevada) {
				infoRelevada = true;
			}

			HashMap<String, String> datosAnteriores = new HashMap<String, String>();
			datosAnteriores.put("codigodistribuidora", elemento.getId_distribuidora());
			datosAnteriores.put("codigoitt", elemento.getCodigo_ITT());
			datosAnterioresList.add(datosAnteriores);

			if (hayCambio != null && hayCambio) {

				elemento.setCodigo_ITT(texto);
				elemento.setHayCambio(true);
			}
		}
		Log.i("ReportesGrillaActivity", "setDatosITTGuardar. hayCambio = " + hayCambio);
		return hayCambio;
	}

	public void show_reporte_bloque_azul_sanfernando_tradicianal_chikara(int tipo_material) {
		elementos = (new TblMstMaterialApoyoController(db)).getElementsForBloqueAzulGrid(tipo_material, idCabecera);
		TableLayout table = (TableLayout) view.findViewById(R.id.tl_reporte_entrega_materiales);
		int numElementos = 0;
		boolean ini = true;
		if (elementos != null && (numElementos = elementos.size()) > 0) {
			datosReporte = new HashMap<String, ArrayList<Object>>();
			for (int i = 0; i < numElementos; i++) {

				E_ReportePotencial mA = (E_ReportePotencial) elementos.get(i);

				datosFila = new ArrayList<Object>();
				TableRow row = (TableRow) inflator.inflate(R.layout.ly_reporte_potencial_sanfdo_body, null);

				((TextView) row.findViewById(R.id.tv_codigo)).setText(mA.getCodMaterial());

				final CheckBox ck_cantidad = (CheckBox) row.findViewById(R.id.ck_cantidad);
				String valorCheck = mA.getValorCheck() == null || mA.getValorCheck().trim().isEmpty() ? "0" : mA.getValorCheck();
				ck_cantidad.setChecked(valorCheck.equalsIgnoreCase("1"));
				datosFila.add(ck_cantidad);
				String key = mA.getCodMaterial();
				createRow(table, row, null, null, null, null, ck_cantidad, null, i, ini, mA.getDescripcion(), null, key);

				datosReporte.put(key, datosFila);
			}
		} else {
			Toast.makeText(this.context, "No hay materiales de apoyo registrados para este reporte", Toast.LENGTH_SHORT).show();
		}
	}
}
