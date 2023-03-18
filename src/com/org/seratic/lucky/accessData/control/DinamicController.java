package com.org.seratic.lucky.accessData.control;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.org.seratic.lucky.accessData.SQLiteDatabaseAdapter;
import com.org.seratic.lucky.accessData.entities.E_Tbl_Mov_RegistroBodega_Detalle;
import com.org.seratic.lucky.accessData.entities.Entity;
import com.org.seratic.lucky.manager.DatosManager;

//import com.org.seratic.lucky.vo.zvo;

public class DinamicController extends EntityController {

	// "{tabla:'TBL_MST_DISTRIBUIDORA',valorOk:'true',propiedadValor:[{campo:'',propiedad:'',tipoDato:'',valueJson:''}]}";

	private final String te_tabla = "tabla";
	private final String te_valorOk = "valorOk";
	private final String te_propiedadValor = "propiedadValor";
	private final String te_campo = "campo";
	private final String te_propiedad = "propiedad";
	private final String te_tipoDato = "tipoDato";
	private final String te_valueJson = "valueJson";

	private SQLiteDatabase db;
	private Cursor dbCursor;
	HashMap<String, String> tablaEntidadMap = new HashMap<String, String>();
	private Object tp_string = "string";
	private Object tp_int = "int";
	private Object tp_boolen = "boolean";
	private Object tp_date = "date";
	private Object tp_Byte = "Byte";
	private Object tp_double = "double";
	private Object tp_float = "float";
	private Object tp_long = "long";
	private Context context;
	final String TAG = DinamicController.class.getSimpleName();

	public DinamicController(Context context) {
		super();
		addTablaEntidades();
		this.context = context;
		// sqliteAdapter = SQLiteDatabaseAdapter.getInstance(this.context);
	}

	private void addTablaEntidades() {
		tablaEntidadMap.put("EstadoVo", "{tabla:'TBL_ESTADOS',valorOk:'true',propiedadValor:[{campo:'id_estado',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'descripcion',propiedad:'idEstado',tipoDato:'string',valueJson:'b'}]}");
		tablaEntidadMap.put("CategoriaVo", "{tabla:'TBL_MST_CATEGORIA',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'b'},{campo:'idReporte',propiedad:'idReporte',tipoDato:'int',valueJson:'a'},{campo:'nombre',propiedad:'nombre',tipoDato:'string',valueJson:'c'}]}");
		tablaEntidadMap.put("CompetidoraVo", "{tabla:'TBL_MST_COMPETIDORA',valorOk:'true',propiedadValor:[{campo:'cod_competidora',propiedad:'codCompetidora',tipoDato:'string',valueJson:'a'},{campo:'nom_competidora',propiedad:'nomCompetidora',tipoDato:'string',valueJson:'b'}]}");
		tablaEntidadMap.put("DatosPresenciaVo", "{tabla:'TBL_MST_DATOS_PRESENCIA',valorOk:'true',propiedadValor:[{campo:'cod_punto_venta',propiedad:'codPuntoVenta',tipoDato:'string',valueJson:'a'},{campo:'cod_categoria',propiedad:'codCategoria',tipoDato:'string',valueJson:'b'},{campo:'cod_marca',propiedad:'codMarca',tipoDato:'string',valueJson:'c'},{campo:'cod_subreporte',propiedad:'codSubreporte',tipoDato:'string',valueJson:'d'},{campo:'cod_elemento',propiedad:'codElemento',tipoDato:'string',valueJson:'e'},{campo:'nom_elemento',propiedad:'nomElemento',tipoDato:'string',valueJson:'f'}]}");
		tablaEntidadMap.put("DistribuidoraPuntoVentaVO", "{tabla:'TBL_MST_DISTRIB_PUNTO_VENTA',valorOk:'true',propiedadValor:[{campo:'cod_distribuidora',propiedad:'cod_distribuidora',tipoDato:'string',valueJson:'a'},{campo:'cod_punto_venta',propiedad:'cod_punto_venta',tipoDato:'string',valueJson:'b'}]}");
		tablaEntidadMap.put("DistribuidoraVo", "{tabla:'TBL_MST_DISTRIBUIDORA',valorOk:'true',propiedadValor:[{campo:'cod_reporte',propiedad:'codReporte',tipoDato:'string',valueJson:'a'},{campo:'cod_distribuidora',propiedad:'codDistribuidora',tipoDato:'string',valueJson:'b'},{campo:'nom_distribuidora',propiedad:'nomDistribuidora',tipoDato:'string',valueJson:'c'}]}");
		tablaEntidadMap.put("FamiliaVo", "{tabla:'TBL_MST_FAMILIA',valorOk:'true',propiedadValor:[{campo:'cod_reporte',propiedad:'codReporte',tipoDato:'string',valueJson:'a'},{campo:'cod_familia',propiedad:'codFamilia',tipoDato:'string',valueJson:'b'},{campo:'nom_familia',propiedad:'nomFamilia',tipoDato:'string',valueJson:'c'},{campo:'cod_categoria',propiedad:'codCategoria',tipoDato:'string',valueJson:'e'},{campo:'cod_subcategoria',propiedad:'codSubcategoria',tipoDato:'string',valueJson:'f'},{campo:'cod_marca',propiedad:'codMarca',tipoDato:'string',valueJson:'d'},{campo:'cod_submarca',propiedad:'codSubmarca',tipoDato:'string',valueJson:'g'},{campo:'cod_presentacion',propiedad:'codPresentacion',tipoDato:'string',valueJson:'h'}]}");
		tablaEntidadMap.put("MarcaVo", "{tabla:'TBL_MST_MARCA',valorOk:'true',propiedadValor:[{campo:'cod_reporte',propiedad:'codReporte',tipoDato:'string',valueJson:'a'},{campo:'cod_marca',propiedad:'tipoMarca',tipoDato:'string',valueJson:'b'},{campo:'nom_marca',propiedad:'nomMarca',tipoDato:'string',valueJson:'c'},{campo:'cod_categoria',propiedad:'codCategoria',tipoDato:'string',valueJson:'d'},{campo:'cod_subcategoria',propiedad:'codSubcategoria',tipoDato:'string',valueJson:'f'},{campo:'propio',propiedad:'propio',tipoDato:'string',valueJson:'e'}]}");
		tablaEntidadMap.put("MaterialApoyoVo", "{tabla:'TBL_MST_MATERIAL_APOYO',valorOk:'true',propiedadValor:[{campo:'cod_reporte',propiedad:'codReporte',tipoDato:'string',valueJson:'a'},{campo:'tipo_material',propiedad:'tipoMaterial',tipoDato:'string',valueJson:'b'},{campo:'cod_material',propiedad:'codMaterial',tipoDato:'string',valueJson:'c'},{campo:'descripcion',propiedad:'descripcion',tipoDato:'string',valueJson:'d'},{campo:'propio',propiedad:'propio',tipoDato:'string',valueJson:'e'},{campo:'cod_subreporte',propiedad:'codSubreporte',tipoDato:'string',valueJson:'f'},{campo:'cod_categoria',propiedad:'codCategoria',tipoDato:'string',valueJson:'g'},{campo:'cod_subcategoria',propiedad:'codSubcategoria',tipoDato:'string',valueJson:'h'},{campo:'cod_marca',propiedad:'codMarca',tipoDato:'string',valueJson:'i'},{campo:'cod_submarca',propiedad:'codSubmarca',tipoDato:'string',valueJson:'j'},{campo:'cod_familia',propiedad:'codFamilia',tipoDato:'string',valueJson:'k'},{campo:'cod_subfamilia',propiedad:'codSubfamilia',tipoDato:'string',valueJson:'l'},{campo:'cod_presentacion',propiedad:'codPresentacion',tipoDato:'string',valueJson:'m'},{campo:'req_check',propiedad:'reqCheck',tipoDato:'string',valueJson:'n'},{campo:'req_cantidad',propiedad:'reqCantidad',tipoDato:'string',valueJson:'o'} ]}");
		tablaEntidadMap.put("ObservacionVo", "{tabla:'TBL_MST_OBSERVACION',valorOk:'true',propiedadValor:[{campo:'cod_observacion',propiedad:'codObservacion',tipoDato:'string',valueJson:'a'},{campo:'descripcion',propiedad:'descripcion',tipoDato:'string',valueJson:'b'},{campo:'cod_reporte',propiedad:'codReporte',tipoDato:'string',valueJson:'c'},{campo:'cod_subreporte',propiedad:'codSubreporte',tipoDato:'string',valueJson:'d'}]}");
		tablaEntidadMap.put("SubfamiliaVo", "{tabla:'TBL_MST_SUBFAMILIA',valorOk:'true',propiedadValor:[{campo:'cod_reporte',propiedad:'codReporte',tipoDato:'string',valueJson:'a'},{campo:'cod_familia',propiedad:'codFamilia',tipoDato:'string',valueJson:'f'},{campo:'cod_subfamilia',propiedad:'codSubfamilia',tipoDato:'string',valueJson:'b'},{campo:'nom_subfamilia',propiedad:'nomSubfamilia',tipoDato:'string',valueJson:'c'},{campo:'cod_categoria',propiedad:'codCategoria',tipoDato:'string',valueJson:'d'},{campo:'cod_subcategoria',propiedad:'codSubcategoria',tipoDato:'string',valueJson:'g'},{campo:'cod_marca',propiedad:'codMarca',tipoDato:'string',valueJson:'e'},{campo:'cod_submarca',propiedad:'codSubmarca',tipoDato:'string',valueJson:'h'},{campo:'cod_presentacion',propiedad:'codPresentacion',tipoDato:'string',valueJson:'i'}]}");
		tablaEntidadMap.put("SubmarcaVo", "{tabla:'TBL_MST_SUBMARCA',valorOk:'true',propiedadValor:[{campo:'cod_reporte',propiedad:'codReporte',tipoDato:'string',valueJson:'a'},{campo:'cod_categoria',propiedad:'codCategoria',tipoDato:'string',valueJson:'b'},{campo:'cod_subcategoria',propiedad:'codSubcategoria',tipoDato:'string',valueJson:'c'},{campo:'cod_marca',propiedad:'codMarca',tipoDato:'string',valueJson:'d'},{campo:'cod_submarca',propiedad:'codSubmarca',tipoDato:'string',valueJson:'e'},{campo:'nom_submarca',propiedad:'nomSubmarca',tipoDato:'string',valueJson:'f'}]}");
		/*------------------------*/
		tablaEntidadMap.put("ActividadVo", "{tabla:'TBL_MST_ACTIVIDAD',valorOk:'true',propiedadValor:[{campo:'cod_reporte',propiedad:'codReporte',tipoDato:'string',valueJson:'a'},{campo:'cod_actividad',propiedad:'codActividad',tipoDato:'string',valueJson:'b'},{campo:'nom_actividad',propiedad:'nomActividad',tipoDato:'string',valueJson:'c'}]}");
		tablaEntidadMap.put("ClusterVo", "{tabla:'TBL_MST_CLUSTER',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'string',valueJson:'a'},{campo:'pregunta',propiedad:'pregunta',tipoDato:'string',valueJson:'b'},{campo:'req_cantidad',propiedad:'reqCantidad',tipoDato:'int',valueJson:'c'}]}");
		tablaEntidadMap.put("CondExhibidorVo", "{tabla:'TBL_MST_COND_EXHIBIDOR',valorOk:'true',propiedadValor:[{campo:'cod_reporte',propiedad:'codReporte',tipoDato:'string',valueJson:'a'},{campo:'cod_cond_exhibidor',propiedad:'codCondExhibidor',tipoDato:'string',valueJson:'b'},{campo:'nom_cond_exhibidor',propiedad:'nomCondExhibidor',tipoDato:'string',valueJson:'c'}]}");
		tablaEntidadMap.put("FaseVo", "{tabla:'TBL_MST_FASE',valorOk:'true',propiedadValor:[{campo:'cod_fase',propiedad:'codFase',tipoDato:'string',valueJson:'a'},{campo:'nom_fase',propiedad:'nomFase',tipoDato:'string',valueJson:'b'},{campo:'orden_elemento',propiedad:'ordenElemento',tipoDato:'string',valueJson:'c'}]}");
		tablaEntidadMap.put("GrupoObjetivoVo", "{tabla:'TBL_MST_GRUPO_OBJETIVO',valorOk:'true',propiedadValor:[{campo:'cod_reporte',propiedad:'codReporte',tipoDato:'string',valueJson:'a'},{campo:'cod_grupo_obj',propiedad:'codGrupoObj',tipoDato:'string',valueJson:'b'},{campo:'nom_grupo_obj',propiedad:'nomGrupoObj',tipoDato:'string',valueJson:'c'}]}");
		tablaEntidadMap.put("MotivoReporteVo", "{tabla:'TBL_MST_MOTIVO_REPORTE',valorOk:'true',propiedadValor:[{campo:'cod_reporte',propiedad:'codReporte',tipoDato:'string',valueJson:'a'},{campo:'cod_motivo',propiedad:'codMotivo',tipoDato:'string',valueJson:'b'},{campo:'nom_motivo',propiedad:'nomMotivo',tipoDato:'string',valueJson:'c'}]}");
		tablaEntidadMap.put("MotivoVo", "{tabla:'TBL_MST_MOTIVO',valorOk:'true',propiedadValor:[{campo:'cod_motivo',propiedad:'codMotivo',tipoDato:'string',valueJson:'a'},{campo:'descrip_estado',propiedad:'descripEstado',tipoDato:'string',valueJson:'b'},{campo:'descrip_motivo',propiedad:'descripMotivo',tipoDato:'string',valueJson:'c'}]}");
		tablaEntidadMap.put("NovisitaVo", "{tabla:'TBL_NOVISITA',valorOk:'true',propiedadValor:[{campo:'_id',propiedad:'id',tipoDato:'int',valueJson:'?'},{campo:'id_novisita',propiedad:'idNovisita',tipoDato:'string',valueJson:'a'},{campo:'descripcion',propiedad:'descripcion',tipoDato:'string',valueJson:'b'},{campo:'tipo',propiedad:'tipo',tipoDato:'string',valueJson:'c'},{campo:'grupo',propiedad:'grupo',tipoDato:'string',valueJson:'d'}]}");
		tablaEntidadMap.put("ObjMarcaVo", "{tabla:'TBL_MST_OBJ_MARCA',valorOk:'true',propiedadValor:[{campo:'cod_punto_venta',propiedad:'codPuntoVenta',tipoDato:'string',valueJson:'a'},{campo:'cod_categoria',propiedad:'codCategoria',tipoDato:'string',valueJson:'f'},{campo:'cod_marca',propiedad:'codMarca',tipoDato:'string',valueJson:'b'},{campo:'nom_marca',propiedad:'nomMarca',tipoDato:'string',valueJson:'c'},{campo:'objetivo',propiedad:'objetivo',tipoDato:'string',valueJson:'d'},{campo:'cantidad',propiedad:'cantidad',tipoDato:'string',valueJson:'e'}]}");
		tablaEntidadMap.put("OpcPedidoVo", "{tabla:'TBL_MST_OPC_PEDIDO',valorOk:'true',propiedadValor:[{campo:'cod_reporte',propiedad:'codReporte',tipoDato:'string',valueJson:'a'},{campo:'cod_opc_pedido',propiedad:'codOpcPedido',tipoDato:'string',valueJson:'b'},{campo:'nom_opc_pedido',propiedad:'nomOpcPedido',tipoDato:'string',valueJson:'c'}]}");
		tablaEntidadMap.put("PoblacionVo", "{tabla:'TBL_MST_POBLACION',valorOk:'true',propiedadValor:[{campo:'cod_poblacion',propiedad:'codPoblacion',tipoDato:'string',valueJson:'a'},{campo:'nom_poblacion',propiedad:'nomPoblacion',tipoDato:'string',valueJson:'b'}]}");
		tablaEntidadMap.put("PosicionVo", "{tabla:'TBL_MST_POSICION',valorOk:'true',propiedadValor:[{campo:'cod_reporte',propiedad:'codReporte',tipoDato:'string',valueJson:'a'},{campo:'cod_subreporte',propiedad:'codSubreporte',tipoDato:'string',valueJson:'b'},{campo:'cod_posicion',propiedad:'codPosicion',tipoDato:'string',valueJson:'c'},{campo:'descrip_posicion',propiedad:'descripPosicion',tipoDato:'string',valueJson:'d'}]}");
		tablaEntidadMap.put("PresentacionVo", "{tabla:'TBL_MST_PRESENTACION',valorOk:'true',propiedadValor:[{campo:'cod_reporte',propiedad:'codReporte',tipoDato:'string',valueJson:'a'},{campo:'cod_categoria',propiedad:'codCategoria',tipoDato:'string',valueJson:'b'},{campo:'cod_subcategoria',propiedad:'codSubcategoria',tipoDato:'string',valueJson:'c'},{campo:'cod_marca',propiedad:'codMarca',tipoDato:'string',valueJson:'d'},{campo:'cod_submarca',propiedad:'codSubmarca',tipoDato:'string',valueJson:'e'},{campo:'cod_presentacion',propiedad:'codPresentacion',tipoDato:'string',valueJson:'f'},{campo:'nom_presentacion',propiedad:'nomPresentacion',tipoDato:'string',valueJson:'g'}]}");
		tablaEntidadMap.put("ProductoVo", "{tabla:'TBL_MST_PRODUCTO',valorOk:'true',propiedadValor:[{campo:'idReporte',propiedad:'idReporte',tipoDato:'int',valueJson:'a'},{campo:'id',propiedad:'id',tipoDato:'string',valueJson:'b'},{campo:'SKU',propiedad:'sku',tipoDato:'string',valueJson:'c'},{campo:'nombre',propiedad:'nombre',tipoDato:'string',valueJson:'d'},{campo:'propio',propiedad:'propio',tipoDato:'int',valueJson:'e'},{campo:'idCategoria',propiedad:'idCategoria',tipoDato:'int',valueJson:'f'},{campo:'idSubCategoria',propiedad:'idSubCategoria',tipoDato:'string',valueJson:'g'},{campo:'idMarca',propiedad:'idMarca',tipoDato:'string',valueJson:'h'},{campo:'idSubMarca',propiedad:'idSubMarca',tipoDato:'string',valueJson:'i'},{campo:'idFamilia',propiedad:'idFamilia',tipoDato:'string',valueJson:'j'},{campo:'idSubFamilia',propiedad:'idSubFamilia',tipoDato:'string',valueJson:'k'},{campo:'cod_presentacion',propiedad:'codPresentacion',tipoDato:'string',valueJson:'l'}]}");
		tablaEntidadMap.put("PromocionVo", "{tabla:'TBL_MST_PROMOCION',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'descripcion',propiedad:'descripcion',tipoDato:'string',valueJson:'b'},{campo:'idReporte',propiedad:'idReporte',tipoDato:'int',valueJson:'c'},{campo:'cod_empresa',propiedad:'codEmpresa',tipoDato:'string',valueJson:'d'}]}");
		tablaEntidadMap.put("PuntoventaVo", "{tabla:'TBL_PUNTOVENTA',valorOk:'true',propiedadValor:[{campo:'id_PtoVenta',propiedad:'idPtoVenta',tipoDato:'string',valueJson:'a'},{campo:'razon_social',propiedad:'razonSocial',tipoDato:'string',valueJson:'b'},{campo:'direccion',propiedad:'direccion',tipoDato:'string',valueJson:'c'},{campo:'cod_cadena',propiedad:'codCadena',tipoDato:'string',valueJson:'d'},{campo:'nom_cadena',propiedad:'nomCadena',tipoDato:'string',valueJson:'e'},{campo:'cod_canal',propiedad:'codCanal',tipoDato:'string',valueJson:'f'},{campo:'nom_canal',propiedad:'nomCanal',tipoDato:'string',valueJson:'g'},{campo:'tipo_mercado',propiedad:'tipoMercado',tipoDato:'string',valueJson:'h'},{campo:'latitud',propiedad:'latitud',tipoDato:'string',valueJson:'i'},{campo:'longitud',propiedad:'longitud',tipoDato:'string',valueJson:'j'},{campo:'cod_fase',propiedad:'codFase',tipoDato:'string',valueJson:'k'}]}");
		tablaEntidadMap.put("ServicioVo", "{tabla:'TBL_MST_SERVICIO',valorOk:'true',propiedadValor:[{campo:'cod_reporte',propiedad:'codReporte',tipoDato:'string',valueJson:'a'},{campo:'cod_servicio',propiedad:'codServicio',tipoDato:'string',valueJson:'b'},{campo:'nom_servicio',propiedad:'nomServicio',tipoDato:'string',valueJson:'c'},{campo:'cod_categoria',propiedad:'codCategoria',tipoDato:'string',valueJson:'d'},{campo:'cod_marca',propiedad:'codMarca',tipoDato:'string',valueJson:'e'}]}");
		tablaEntidadMap.put("UbicacionVo", "{tabla:'TBL_MST_UBICACION',valorOk:'true',propiedadValor:[{campo:'cod_reporte',propiedad:'codReporte',tipoDato:'string',valueJson:'a'},{campo:'cod_subreporte',propiedad:'codSubreporte',tipoDato:'string',valueJson:'b'},{campo:'cod_ubicacion',propiedad:'codUbicacion',tipoDato:'string',valueJson:'c'},{campo:'drescrip_ubicacion',propiedad:'drescripUbicacion',tipoDato:'string',valueJson:'d'}]}");
		tablaEntidadMap.put("OpcReporteVo", "{tabla:'TBL_MST_OPC_REPORTE',valorOk:'true',propiedadValor:[{campo:'idReporte',propiedad:'idReporte',tipoDato:'int',valueJson:'a'},{campo:'idSubreporte',propiedad:'idSubreporte',tipoDato:'int',valueJson:'b'},{campo:'verCategoria',propiedad:'verCategoria',tipoDato:'int',valueJson:'c'},{campo:'verSubcategoria',propiedad:'verSubcategoria',tipoDato:'int',valueJson:'d'},{campo:'verMarca',propiedad:'verMarca',tipoDato:'int',valueJson:'e'},{campo:'verSubmarca',propiedad:'verSubmarca',tipoDato:'int',valueJson:'f'},{campo:'verPresentacion',propiedad:'verPresentacion',tipoDato:'int',valueJson:'g'},{campo:'verFamilia',propiedad:'verFamilia',tipoDato:'int',valueJson:'h'},{campo:'verSubfamilia',propiedad:'verSubfamilia',tipoDato:'int',valueJson:'i'},{campo:'verProducto',propiedad:'verProducto',tipoDato:'int',valueJson:'j'}]}");
		tablaEntidadMap.put("ReporteVo", "{tabla:'TBL_MST_REPORTE',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'alias',propiedad:'alias',tipoDato:'string',valueJson:'b'},{campo:'idSubreporte',propiedad:'idSubReporte',tipoDato:'int',valueJson:'c'},{campo:'aliasSubreporte',propiedad:'aliasSubreporte',tipoDato:'string',valueJson:'d'},{campo:'orden',propiedad:'orden',tipoDato:'int',valueJson:'e'}]}");
		tablaEntidadMap.put("SubcategoriaVo", "{tabla:'TBL_MST_SUBCATEGORIA',valorOk:'true',propiedadValor:[{campo:'cod_reporte',propiedad:'codReporte',tipoDato:'string',valueJson:'a'},{campo:'cod_categoria',propiedad:'codCategoria',tipoDato:'string',valueJson:'b'},{campo:'cod_subcategoria',propiedad:'codSubcategoria',tipoDato:'string',valueJson:'c'},{campo:'nom_subcategoria',propiedad:'nomSubcategoria',tipoDato:'string',valueJson:'d'}]}");
		tablaEntidadMap.put("subEstadoVo", "{tabla:'TBL_SUBESTADOS',valorOk:'true',propiedadValor:[{campo:'_id',propiedad:'id',tipoDato:'int',valueJson:'?'},{campo:'id_subestados',propiedad:'idSubEstado',tipoDato:'string',valueJson:'a'},{campo:'id_estados',propiedad:'idEstado',tipoDato:'string',valueJson:'b'},{campo:'descripcion',propiedad:'descripcion',tipoDato:'string',valueJson:'c'}]}");
		tablaEntidadMap.put("MotivoObsVo","{tabla:'TBL_MST_MOTIVO_OBS',valorOk:'true',propiedadValor:[{campo:'cod_motivo',propiedad:'cod_motivo',tipoDato:'string',valueJson:'a'},{campo:'desc_motivo',propiedad:'desc_motivo',tipoDato:'string',valueJson:'b'},{campo:'cod_reporte',propiedad:'cod_reporte',tipoDato:'string',valueJson:'c'}]}");
		tablaEntidadMap.put("TipoExhibicionVo","{tabla:'TBL_MST_TIPO_EXHIBICION',valorOk:'true',propiedadValor:[{campo:'cod_tipo_exhib',propiedad:'cod_tipo_exhib',tipoDato:'string',valueJson:'b'},{campo:'cod_reporte',propiedad:'cod_reporte',tipoDato:'int',valueJson:'a'},{campo:'descripcion',propiedad:'descripcion',tipoDato:'string',valueJson:'c'}]}");
		tablaEntidadMap.put("E_TipoPrecioPDV", "{tabla:'TBL_MST_TIPO_PRECIO',valorOk:'true',propiedadValor:[{campo:'cod_tipo_precio',propiedad:'codTipoPrecio',tipoDato:'string',valueJson:'a'},{campo:'descripcion',propiedad:'descripcion',tipoDato:'string',valueJson:'b'}]}");
		/*------------*/
		tablaEntidadMap.put("MarcajePrecioVo", "{tabla:'TBL_MST_MARCAJE_PRECIO',valorOk:'true',propiedadValor:[{campo:'cod_reporte',propiedad:'cod_reporte',tipoDato:'string',valueJson:'a'},{campo:'cod_marcaje',propiedad:'cod_marcaje',tipoDato:'string',valueJson:'b'},{campo:'nom_marcaje',propiedad:'nom_marcaje',tipoDato:'string',valueJson:'c'}]}");
		tablaEntidadMap.put("CapacitacionVo", "{tabla:'TBL_MST_CAPACITACION',valorOk:'true',propiedadValor:[{campo:'cod_reporte',propiedad:'cod_reporte',tipoDato:'string',valueJson:'a'},{campo:'cod_capacitacion',propiedad:'cod_capacitacion',tipoDato:'string',valueJson:'b'},{campo:'desc_capacitacion',propiedad:'desc_capacitacion',tipoDato:'string',valueJson:'c'}]}");
		tablaEntidadMap.put("StatusVo", "{tabla:'TBL_MST_STATUS',valorOk:'true',propiedadValor:[{campo:'cod_reporte',propiedad:'cod_reporte',tipoDato:'string',valueJson:'a'},{campo:'cod_status',propiedad:'cod_status',tipoDato:'string',valueJson:'b'},{campo:'desc_status',propiedad:'desc_status',tipoDato:'string',valueJson:'c'}]}");
		tablaEntidadMap.put("IncidenciaVo", "{tabla:'TBL_MST_INCIDENCIA',valorOk:'true',propiedadValor:[{campo:'cod_reporte',propiedad:'cod_reporte',tipoDato:'string',valueJson:'a'},{campo:'cod_incidencia',propiedad:'cod_incidencia',tipoDato:'string',valueJson:'b'},{campo:'desc_incidencia',propiedad:'desc_incidencia',tipoDato:'string',valueJson:'c'},{campo:'has_cantidad',propiedad:'has_cantidad',tipoDato:'string',valueJson:'d'},{campo:'cod_subreporte',propiedad:'cod_subreporte',tipoDato:'string',valueJson:'e'},{campo:'cod_tipo_incidencia',propiedad:'cod_tipo_incidencia',tipoDato:'string',valueJson:'f'},{campo:'cod_categoria',propiedad:'cod_categoria',tipoDato:'string',valueJson:'g'},{campo:'cod_subcategoria',propiedad:'cod_subcategoria',tipoDato:'string',valueJson:'h'},{campo:'cod_marca',propiedad:'cod_marca',tipoDato:'string',valueJson:'i'},{campo:'cod_submarca',propiedad:'cod_submarca',tipoDato:'string',valueJson:'j'},{campo:'cod_familia',propiedad:'cod_familia',tipoDato:'string',valueJson:'k'},{campo:'cod_subfamilia',propiedad:'cod_subfamilia',tipoDato:'string',valueJson:'l'},{campo:'cod_presentacion',propiedad:'cod_presentacion',tipoDato:'string',valueJson:'m'},{campo:'cod_producto',propiedad:'cod_producto',tipoDato:'string',valueJson:'n'}]}");
		tablaEntidadMap.put("CreditoVo", "{tabla:'TBL_MST_CREDITO',valorOk:'true',propiedadValor:[{campo:'cod_reporte',propiedad:'cod_reporte',tipoDato:'string',valueJson:'a'},{campo:'cod_credito',propiedad:'cod_credito',tipoDato:'string',valueJson:'b'},{campo:'nom_credito',propiedad:'nom_credito',tipoDato:'string',valueJson:'c'}]}");
		tablaEntidadMap.put("ParametrizacionVo", "{tabla:'TBL_MOV_PARAM_REPORTE',valorOk:'true',propiedadValor:[{campo:'cod_reporte',propiedad:'cod_reporte',tipoDato:'int',valueJson:'a'},{campo:'cod_subreporte',propiedad:'cod_subreporte',tipoDato:'int',valueJson:'b'},{campo:'cod_canal',propiedad:'cod_canal',tipoDato:'string',valueJson:'c'},{campo:'cod_cliente',propiedad:'cod_cliente',tipoDato:'string',valueJson:'d'},{campo:'tipo_reporte',propiedad:'tipo_reporte',tipoDato:'string',valueJson:'e'},{campo:'cod_equipo',propiedad:'cod_equipo',tipoDato:'string',valueJson:'f'}]}");
		tablaEntidadMap.put("PerfilVo", "{tabla:'TBL_MST_PERFIL',valorOk:'true',propiedadValor:[{campo:'cod_tipo_perfil',propiedad:'cod_tipoPerfil',tipoDato:'string',valueJson:'a'},{campo:'cod_perfil',propiedad:'cod_perfil',tipoDato:'string',valueJson:'b'},{campo:'cod_equipo',propiedad:'cod_equipo',tipoDato:'string',valueJson:'c'},{campo:'desc_perfil',propiedad:'desc_perfil',tipoDato:'string',valueJson:'d'}]}");
		tablaEntidadMap.put("TipoPerfilVo", "{tabla:'TBL_MST_TIPO_PERFIL',valorOk:'true',propiedadValor:[{campo:'cod_tipo_perfil',propiedad:'cod_tipoPerfil',tipoDato:'string',valueJson:'a'},{campo:'desc_tipo_perfil',propiedad:'desc_tipoPerfil',tipoDato:'string',valueJson:'b'},{campo:'estado',propiedad:'estado',tipoDato:'string',valueJson:'c'}]}");
		tablaEntidadMap.put("TipoIncidenciaVo", "{tabla:'TBL_MST_TIPO_INCIDENCIA',valorOk:'true',propiedadValor:[{campo:'cod_tipo_incidencia',propiedad:'codTipoIncidencia',tipoDato:'string',valueJson:'a'},{campo:'desc_tipo_incidencia',propiedad:'descTipoIncidencia',tipoDato:'string',valueJson:'b'}]}");
		tablaEntidadMap.put("TipoMaterialVo", "{tabla:'TBL_MST_TIPO_MATERIAL',valorOk:'true',propiedadValor:[{campo:'cod_tipo_material',propiedad:'codTipoMaterial',tipoDato:'string',valueJson:'a'},{campo:'descripcion',propiedad:'descTipoMaterial',tipoDato:'string',valueJson:'b'}]}");
		tablaEntidadMap.put("PDVEstrellaVo", "{tabla:'TBL_MST_PDV_ESTRELLA',valorOk:'true',propiedadValor:[{campo:'cod_reporte',propiedad:'codReporte',tipoDato:'string',valueJson:'a'},{campo:'cod_pdv',propiedad:'codPDV',tipoDato:'string',valueJson:'b'},{campo:'cod_estrella',propiedad:'codEstrella',tipoDato:'string',valueJson:'c'},{campo:'desc_estrella',propiedad:'descEstrella',tipoDato:'string',valueJson:'d'},{campo:'valor_estrella',propiedad:'valorEstrella',tipoDato:'string',valueJson:'e'}]}");
		
		/* Datos precarga */
		tablaEntidadMap.put("DepartamentoVo", "{tabla:'TBL_MST_DEPARTAMENTO',valorOk:'true',propiedadValor:[{campo:'cod_departamento',propiedad:'codDepartamento',tipoDato:'string',valueJson:'a'},{campo:'cod_pais',propiedad:'codPais',tipoDato:'string',valueJson:'b'},{campo:'departamento',propiedad:'departamento',tipoDato:'string',valueJson:'c'}]}");
		tablaEntidadMap.put("DistritoVo", "{tabla:'TBL_MST_DISTRITO',valorOk:'true',propiedadValor:[{campo:'cod_distrito',propiedad:'codDistrito',tipoDato:'string',valueJson:'a'},{campo:'cod_pais',propiedad:'codPais',tipoDato:'string',valueJson:'b'},{campo:'cod_departamento',propiedad:'codDeprtamento',tipoDato:'string',valueJson:'c'},{campo:'cod_provincia',propiedad:'codProvincia',tipoDato:'string',valueJson:'d'},{campo:'distrito',propiedad:'distrito',tipoDato:'string',valueJson:'e'}]}");
		tablaEntidadMap.put("PaisVo", "{tabla:'TBL_MST_PAIS',valorOk:'true',propiedadValor:[{campo:'cod_pais',propiedad:'codPais',tipoDato:'string',valueJson:'a'},{campo:'pais',propiedad:'pais',tipoDato:'string',valueJson:'b'}]}");
		tablaEntidadMap.put("ProvinciaVo", "{tabla:'TBL_MST_PROVINCIA',valorOk:'true',propiedadValor:[{campo:'cod_provincia',propiedad:'codProvincia',tipoDato:'string',valueJson:'a'},{campo:'cod_pais',propiedad:'codPais',tipoDato:'string',valueJson:'b'},{campo:'cod_departamento',propiedad:'codDepartamento',tipoDato:'string',valueJson:'c'},{campo:'provincia',propiedad:'provincia',tipoDato:'string',valueJson:'d'}]}");
		
		/*Data trabajada*/
		tablaEntidadMap.put("E_TblFiltrosApp", "{tabla:'TBL_MOV_FILTROS_APP',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'cod_reporte',propiedad:'cod_reporte',tipoDato:'string',valueJson:'b'},{campo:'cod_subreporte',propiedad:'cod_subreporte',tipoDato:'string',valueJson:'c'},{campo:'cod_categoria',propiedad:'cod_categoria',tipoDato:'string',valueJson:'d'},{campo:'cod_subcategoria',propiedad:'cod_subcategoria',tipoDato:'string',valueJson:'e'},{campo:'cod_marca',propiedad:'cod_marca',tipoDato:'string',valueJson:'f'},{campo:'cod_submarca',propiedad:'cod_submarca',tipoDato:'string',valueJson:'g'},{campo:'cod_presentacion',propiedad:'cod_presentacion',tipoDato:'string',valueJson:'g'},{campo:'cod_familia',propiedad:'cod_familia',tipoDato:'string',valueJson:'j'},{campo:'cod_subfamilia',propiedad:'cod_subfamilia',tipoDato:'string',valueJson:'i'},{campo:'cod_producto',propiedad:'cod_producto',tipoDato:'string',valueJson:'j'}]}");
		tablaEntidadMap.put("E_tbl_mov_fotos", "{tabla:'TBL_MOV_FOTOS',valorOk:'true',propiedadValor:[{campo:'id_foto',propiedad:'id_foto',tipoDato:'int',valueJson:'a'},{campo:'nom_foto',propiedad:'nom_foto',tipoDato:'string',valueJson:'b'},{campo:'estado_envio',propiedad:'envio',tipoDato:'int',valueJson:'c'},{campo:'file',propiedad:'fotobyteArray',tipoDato:'byte[]',valueJson:'d'}]}");
		tablaEntidadMap.put("E_TBL_MOV_REGISTROVISITA", "{tabla:'TBL_MOV_REGISTRO_VISITA',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'string',valueJson:'a'},{campo:'idPuntoVenta',propiedad:'idPuntoVenta',tipoDato:'string',valueJson:'b'},{campo:'idmotivoNoVisita',propiedad:'idmotivoNoVisita',tipoDato:'string',valueJson:'c'},{campo:'idPuntoGPSInicio',propiedad:'idPuntoGPSInicio',tipoDato:'int',valueJson:'c'},{campo:'idPuntoGPSFin',propiedad:'idPuntoGPSFin',tipoDato:'int',valueJson:'d'},{campo:'estado',propiedad:'estado',tipoDato:'int',valueJson:'e'},{campo:'idFoto',propiedad:'idFoto',tipoDato:'int',valueJson:'f'},{campo:'comentario',propiedad:'comentario',tipoDato:'string',valueJson:'g'}]}");
		tablaEntidadMap.put("E_Tbl_Mov_RegistroBodega", "{tabla:'TBL_MOV_REG_MOTIVOS_BODEGA',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'id_usuario',propiedad:'idUsuario',tipoDato:'string',valueJson:'b'},{campo:'id_punto_venta',propiedad:'idPuntoVenta',tipoDato:'string',valueJson:'c'},{campo:'id_punto_gps',propiedad:'idPuntoGPS',tipoDato:'int',valueJson:'d'},{campo:'cod_fase',propiedad:'idFase',tipoDato:'string',valueJson:'e'},{campo:'id_visita',propiedad:'idVisita',tipoDato:'int',valueJson:'f'}]}");
		tablaEntidadMap.put("E_Tbl_Mov_RegistroBodega_Detalle", "{tabla:'TBL_MOV_REG_MOTIVOS_BODEGA_DETALLE',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'id_cab',propiedad:'id_Cabecera',tipoDato:'int',valueJson:'b'},{campo:'cod_motivo',propiedad:'idmotivoNoVisita',tipoDato:'string',valueJson:'c'}]}");
		tablaEntidadMap.put("E_TblMovRegPDV", "{tabla:'TBL_MOV_REG_PDV',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'id_usuario',propiedad:'id_usuario',tipoDato:'string',valueJson:'b'},{campo:'id_punto_venta',propiedad:'id_punto_venta',tipoDato:'string',valueJson:'c'},{campo:'nom_cliente',propiedad:'nom_cliente',tipoDato:'string',valueJson:'d'},{campo:'apellido_cliente',propiedad:'apellido_cliente',tipoDato:'string',valueJson:'e'},{campo:'razon_social',propiedad:'razon_social',tipoDato:'string',valueJson:'f'},{campo:'tipo_doc',propiedad:'tipo_doc',tipoDato:'string',valueJson:'g'},{campo:'num_doc',propiedad:'num_doc',tipoDato:'string',valueJson:'h'},{campo:'cod_pais',propiedad:'cod_pais',tipoDato:'string',valueJson:'i'},{campo:'cod_departamento',propiedad:'cod_departamento',tipoDato:'string',valueJson:'j'},{campo:'cod_provincia',propiedad:'cod_provincia',tipoDato:'string',valueJson:'k'},{campo:'cod_distrito',propiedad:'cod_distrito',tipoDato:'string',valueJson:'l'},{campo:'direccion',propiedad:'direccion',tipoDato:'string',valueJson:'m'},{campo:'cod_poblacion',propiedad:'cod_poblacion',tipoDato:'string',valueJson:'n'},{campo:'referencia',propiedad:'referencia',tipoDato:'string',valueJson:'o'},{campo:'telefono',propiedad:'telefono',tipoDato:'string',valueJson:'p'},{campo:'id_punto_gps',propiedad:'id_punto_gps',tipoDato:'int',valueJson:'q'},{campo:'estado_envio',propiedad:'estado_envio',tipoDato:'string',valueJson:'r'}]}");
		tablaEntidadMap.put("E_TblMovReporteCab", "{tabla:'TBL_MOV_REPORTE_CAB',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'id_usuario',propiedad:'id_usuario',tipoDato:'string',valueJson:'b'},{campo:'id_punto_venta',propiedad:'id_punto_de_venta',tipoDato:'string',valueJson:'c'},{campo:'cod_reporte',propiedad:'cod_reporte',tipoDato:'string',valueJson:'d'},{campo:'cod_subreporte',propiedad:'cod_subreporte',tipoDato:'string',valueJson:'e'},{campo:'id_filtros_app',propiedad:'id_filtros_app',tipoDato:'int',valueJson:'f'},{campo:'id_punto_gps',propiedad:'id_punto_gps',tipoDato:'int',valueJson:'g'},{campo:'comentario',propiedad:'comentario',tipoDato:'string',valueJson:'h'},{campo:'cod_competidora',propiedad:'cod_competidora',tipoDato:'string',valueJson:'i'},{campo:'estado_envio',propiedad:'estado_envio',tipoDato:'int',valueJson:'j'},{campo:'id_visita',propiedad:'idVisita',tipoDato:'int',valueJson:'k'}]}");
		tablaEntidadMap.put("E_ReporteCapacitacion", "{tabla:'TBL_MOV_REP_CAPACITACION',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'cod_reporte_cab',propiedad:'cod_reporte_cab',tipoDato:'string',valueJson:'b'},{campo:'cod_capacitacion',propiedad:'cod_capacitacion',tipoDato:'string',valueJson:'c'},{campo:'valor_capacitacion',propiedad:'valor_capacitacion',tipoDato:'string',valueJson:'d'},{campo:'cod_motivo',propiedad:'cod_motivo',tipoDato:'string',valueJson:'e'},{campo:'valor_motivo',propiedad:'valor_motivo',tipoDato:'string',valueJson:'f'},{campo:'cod_categoria',propiedad:'cod_categoria',tipoDato:'string',valueJson:'g'}]}");
		tablaEntidadMap.put("E_ReporteCompetencia", "{tabla:'TBL_MOV_REP_COMPETENCIA',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'id_reporte_cab',propiedad:'id_reporte_cab',tipoDato:'int',valueJson:'b'},{campo:'cod_promo',propiedad:'cod_promo',tipoDato:'string',valueJson:'c'},{campo:'cod_actividad',propiedad:'cod_actividad',tipoDato:'string',valueJson:'d'},{campo:'cod_grupo_obj',propiedad:'cod_grupo_obj',tipoDato:'string',valueJson:'e'},{campo:'precio_costo',propiedad:'cod_precio_costo',tipoDato:'string',valueJson:'f'},{campo:'fecha_ini',propiedad:'fecha_ini',tipoDato:'long',valueJson:'g'},{campo:'fecha_fin',propiedad:'fecha_fin',tipoDato:'long',valueJson:'h'},{campo:'desc_grupo_obj',propiedad:'desc_grupo_obj',tipoDato:'string',valueJson:'i'},{campo:'cantidad_personal',propiedad:'cantidad_personal',tipoDato:'string',valueJson:'j'},{campo:'premio',propiedad:'premio',tipoDato:'string',valueJson:'k'},{campo:'mecanica',propiedad:'mecanica',tipoDato:'string',valueJson:'l'},{campo:'desc_actividad',propiedad:'desc_actividad',tipoDato:'string',valueJson:'m'},{campo:'desc_material',propiedad:'desc_material',tipoDato:'string',valueJson:'n'},{campo:'id_foto',propiedad:'id_foto',tipoDato:'int',valueJson:'o'},{campo:'precio_pdv',propiedad:'precio_pdv',tipoDato:'string',valueJson:'p'},{campo:'cod_marca',propiedad:'cod_marca',tipoDato:'string',valueJson:'q'},{campo:'fecha_com',propiedad:'fecha_com',tipoDato:'long',valueJson:'r'},{campo:'cod_competidora',propiedad:'cod_competidora',tipoDato:'string',valueJson:'s'},{campo:'precio_regular',propiedad:'precio_regular',tipoDato:'string',valueJson:'t'},{campo:'precio_oferta',propiedad:'precio_oferta',tipoDato:'string',valueJson:'u'},{campo:'precio_mayorista',propiedad:'precio_mayorista',tipoDato:'string',valueJson:'v'},{campo:'cod_tipo_oferta',propiedad:'cod_tipo_oferta',tipoDato:'string',valueJson:'w'}]}");
		tablaEntidadMap.put("E_ReporteCompetenciaDet", "{tabla:'TBL_MOV_REP_COMPETENCIA_DET',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'id_rep_competencia',propiedad:'id_rep_competencia',tipoDato:'int',valueJson:'b'},{campo:'cod_material',propiedad:'cod_material',tipoDato:'string',valueJson:'c'},{campo:'selected',propiedad:'selected',tipoDato:'string',valueJson:'d'}]}");
		tablaEntidadMap.put("E_ReporteCredito", "{tabla:'TBL_MOV_REP_CREDITO',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'cod_reporte_cab',propiedad:'cod_reporte_cab',tipoDato:'int',valueJson:'b'},{campo:'cod_credito',propiedad:'cod_credito',tipoDato:'string',valueJson:'c'},{campo:'valor_credito',propiedad:'valor_credito',tipoDato:'string',valueJson:'d'}]}");
		tablaEntidadMap.put("E_ReporteExhibicion", "{tabla:'TBL_MOV_REP_EXHIBICION',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'id_reporte_cab',propiedad:'id_reporte_cab',tipoDato:'string',valueJson:'b'},{campo:'cod_cond_exhib',propiedad:'cod_cond_exhib',tipoDato:'string',valueJson:'c'},{campo:'fecha_ini',propiedad:'fecha_ini',tipoDato:'long',valueJson:'d'},{campo:'fecha_fin',propiedad:'fecha_fin',tipoDato:'long',valueJson:'e'},{campo:'id_foto',propiedad:'id_foto',tipoDato:'int',valueJson:'f'},{campo:'cod_motivo',propiedad:'cod_motivo',tipoDato:'string',valueJson:'g'},{campo:'valor_motivo',propiedad:'valor_motivo',tipoDato:'string',valueJson:'h'}]}");
		tablaEntidadMap.put("E_ReporteExhibicionDet", "{tabla:'TBL_MOV_REP_EXHIBICION_DET',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'id_rep_exhib',propiedad:'id_rep_exhib',tipoDato:'int',valueJson:'b'},{campo:'sku_prod',propiedad:'sku_prod',tipoDato:'string',valueJson:'c'},{campo:'cantidad',propiedad:'cantidad',tipoDato:'string',valueJson:'d'},{campo:'cod_exhib',propiedad:'cod_exhib',tipoDato:'string',valueJson:'e'},{campo:'valor_exhib',propiedad:'valor_exhib',tipoDato:'string',valueJson:'f'}]}");
		tablaEntidadMap.put("E_ReporteImpulso", "{tabla:'TBL_MOV_REP_IMPULSO',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'id_reporte_cab',propiedad:'id_reporte_cab',tipoDato:'int',valueJson:'b'},{campo:'sku_prod',propiedad:'sku_prod',tipoDato:'string',valueJson:'c'},{campo:'ingreso',propiedad:'ingreso',tipoDato:'string',valueJson:'d'},{campo:'stock_final',propiedad:'stock_final',tipoDato:'string',valueJson:'e'}]}");
		tablaEntidadMap.put("E_ReporteIncidencia", "{tabla:'TBL_MOV_REP_INCIDENCIA',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'id_reporte_cab',propiedad:'id_reporte_cab',tipoDato:'int',valueJson:'b'},{campo:'sku_prod',propiedad:'sku_prod',tipoDato:'string',valueJson:'c'},{campo:'cod_servicio',propiedad:'cod_servicio',tipoDato:'string',valueJson:'d'},{campo:'has_pedido',propiedad:'has_pedido',tipoDato:'bit',valueJson:'e'},{campo:'id_foto',propiedad:'id_foto',tipoDato:'int',valueJson:'f'},{campo:'comentario',propiedad:'comentario',tipoDato:'string',valueJson:'g'},{campo:'cod_status',propiedad:'cod_status',tipoDato:'string',valueJson:'h'},{campo:'valor_status',propiedad:'valor_status',tipoDato:'string',valueJson:'i'},{campo:'cod_incidencia',propiedad:'cod_incidencia',tipoDato:'string',valueJson:'j'},{campo:'valor_incidencia',propiedad:'valor_incidencia',tipoDato:'string',valueJson:'k'},{campo:'cantidad',propiedad:'cantidad',tipoDato:'string',valueJson:'l'}]}");
		tablaEntidadMap.put("E_ReporteLayout", "{tabla:'TBL_MOV_REP_LAYOUT',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'id_reporte_cab',propiedad:'id_reporte_cab',tipoDato:'int',valueJson:'b'},{campo:'cantidad',propiedad:'cantidad',tipoDato:'string',valueJson:'c'},{campo:'frente',propiedad:'frente',tipoDato:'string',valueJson:'d'},{campo:'objetivo',propiedad:'objetivo',tipoDato:'string',valueJson:'e'}]}");
		tablaEntidadMap.put("E_ReporteMarcajePrecio", "{tabla:'TBL_MOV_REP_MARCAJE_PRECIO',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'cod_reporte_cab',propiedad:'cod_reporte_cab',tipoDato:'int',valueJson:'b'},{campo:'cod_marcaje',propiedad:'cod_marcaje',tipoDato:'string',valueJson:'c'},{campo:'cantidad',propiedad:'cantidad',tipoDato:'string',valueJson:'d'},{campo:'id_foto',propiedad:'id_foto',tipoDato:'int',valueJson:'e'},{campo:'comentario',propiedad:'comentario',tipoDato:'string',valueJson:'f'},{campo:'cod_motivo',propiedad:'cod_motivo',tipoDato:'string',valueJson:'g'}]}");
		tablaEntidadMap.put("E_TblMovRepMaterialDeApoyo", "{tabla:'TBL_MOV_REP_MATERIAL_APOYO',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'id_reporte_cab',propiedad:'id_reporte_cab',tipoDato:'int',valueJson:'b'},{campo:'cod_marial_apoyo',propiedad:'cod_marial_apoyo',tipoDato:'string',valueJson:'c'},{campo:'cod_presencia',propiedad:'cod_presencia',tipoDato:'string',valueJson:'d'},{campo:'cod_marca',propiedad:'cod_marca',tipoDato:'string',valueJson:'e'},{campo:'comentario',propiedad:'comentario',tipoDato:'string',valueJson:'f'},{campo:'cod_reporte',propiedad:'fecha_ini',tipoDato:'long',valueJson:'g'},{campo:'fecha_fin',propiedad:'fecha_fin',tipoDato:'long',valueJson:'h'},{campo:'id_foto',propiedad:'id_foto',tipoDato:'int',valueJson:'i'},{campo:'cantidad',propiedad:'cantidad',tipoDato:'string',valueJson:'j'}]}");
		tablaEntidadMap.put("E_TBL_MOV_REP_COD_NEW_ITT", "{tabla:'TBL_MOV_REP_NEW_COD_ITT',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'id_reporte_cab',propiedad:'id_reporte_cab',tipoDato:'int',valueJson:'b'},{campo:'cod_distribuidora',propiedad:'cod_distribuidora',tipoDato:'string',valueJson:'c'},{campo:'cod_itt',propiedad:'cod_itt',tipoDato:'string',valueJson:'d'}]}");
		tablaEntidadMap.put("E_ReportePrecio", "{tabla:'TBL_MOV_REP_PRECIO',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'id_reporte_cab',propiedad:'id_reporte_cab',tipoDato:'int',valueJson:'b'},{campo:'sku_prod',propiedad:'sku_prod',tipoDato:'string',valueJson:'c'},{campo:'precio_lista',propiedad:'precio_lista',tipoDato:'string',valueJson:'d'},{campo:'precio_reventa',propiedad:'precio_reventa',tipoDato:'string',valueJson:'e'},{campo:'precio_oferta',propiedad:'precio_oferta',tipoDato:'string',valueJson:'f'},{campo:'precio_pdv',propiedad:'precio_pdv',tipoDato:'string',valueJson:'g'},{campo:'precio_costo',propiedad:'precio_costo',tipoDato:'string',valueJson:'h'},{campo:'precio_regular',propiedad:'precio_regular',tipoDato:'string',valueJson:'i'},{campo:'precio_min',propiedad:'precio_min',tipoDato:'string',valueJson:'j'},{campo:'precio_min',propiedad:'precio_max',tipoDato:'string',valueJson:'k'},{campo:'precio_mayorista',propiedad:'precio_mayorista',tipoDato:'string',valueJson:'l'},{campo:'cod_motivo_obs',propiedad:'cod_motivo_obs',tipoDato:'string',valueJson:'m'}]}");
		tablaEntidadMap.put("E_TBL_MOV_REP_PRESENCIA", "{tabla:'TBL_MOV_REP_PRESENCIA',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'id_reporte_cab',propiedad:'id_reporte_cab',tipoDato:'int',valueJson:'b'},{campo:'cod_material_apoyo',propiedad:'cod_material_apoyo',tipoDato:'string',valueJson:'c'},{campo:'valor_material_apoyo',propiedad:'valor_material_apoyo',tipoDato:'string',valueJson:'d'},{campo:'sku_producto',propiedad:'sku_producto',tipoDato:'string',valueJson:'e'},{campo:'precio',propiedad:'precio',tipoDato:'string',valueJson:'f'},{campo:'cod_observacion',propiedad:'cod_observacion',tipoDato:'string',valueJson:'g'},{campo:'observacion',propiedad:'observacion',tipoDato:'string',valueJson:'h'},{campo:'stock',propiedad:'stock',tipoDato:'string',valueJson:'i'},{campo:'cantidad',propiedad:'cantidad',tipoDato:'string',valueJson:'j'},{campo:'num_frentes',propiedad:'num_frentes',tipoDato:'string',valueJson:'k'},{campo:'pedido_sugerido',propiedad:'pedido_sugerido',tipoDato:'string',valueJson:'l'},{campo:'profundidad',propiedad:'profundidad',tipoDato:'string',valueJson:'m'},{campo:'cod_presencia',propiedad:'cod_presencia',tipoDato:'string',valueJson:'n'},{campo:'cod_ubicacion',propiedad:'cod_ubicacion',tipoDato:'string',valueJson:'o'},{campo:'cod_posicion',propiedad:'cod_posicion',tipoDato:'string',valueJson:'p'},{campo:'cod_cluster',propiedad:'cod_cluster',tipoDato:'string',valueJson:'q'},{campo:'cluster',propiedad:'cluster',tipoDato:'string',valueJson:'r'},{campo:'cumple_layout',propiedad:'cumple_layout',tipoDato:'string',valueJson:'s'}]}");
		tablaEntidadMap.put("TBL_MOV_REP_PROMOCION", "{tabla:'TBL_MOV_REP_PROMOCION',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'id_reporte_cab',propiedad:'id_reporte_cab',tipoDato:'int',valueJson:'b'},{campo:'cod_competidora',propiedad:'cod_competidora',tipoDato:'string',valueJson:'c'},{campo:'cod_promocion',propiedad:'cod_promocion',tipoDato:'string',valueJson:'d'},{campo:'desc_promocion',propiedad:'desc_promocion',tipoDato:'string',valueJson:'e'},{campo:'sku_producto',propiedad:'sku_producto',tipoDato:'string',valueJson:'f'},{campo:'mecanica',propiedad:'mecanica',tipoDato:'string',valueJson:'g'},{campo:'fecha_ini_promo',propiedad:'fecha_ini_promo',tipoDato:'long',valueJson:'h'},{campo:'fecha_fin_promo',propiedad:'fecha_fin_promo',tipoDato:'long',valueJson:'i'},{campo:'precio_promo',propiedad:'precio_promo',tipoDato:'string',valueJson:'j'},{campo:'precio_reg',propiedad:'precio_reg',tipoDato:'string',valueJson:'k'},{campo:'id_foto',propiedad:'id_foto',tipoDato:'int',valueJson:'l'}]}");
		tablaEntidadMap.put("E_TblMovRepPromocion", "{tabla:'TBL_MOV_REP_PROMOCION',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'id_reporte_cab',propiedad:'id_reporte_cab',tipoDato:'int',valueJson:'b'},{campo:'cod_competidora',propiedad:'cod_competidora',tipoDato:'string',valueJson:'c'},{campo:'cod_promocion',propiedad:'cod_promocion',tipoDato:'string',valueJson:'d'},{campo:'desc_promocion',propiedad:'desc_promocion',tipoDato:'string',valueJson:'e'},{campo:'sku_producto',propiedad:'sku_producto',tipoDato:'string',valueJson:'f'},{campo:'mecanica',propiedad:'mecanica',tipoDato:'string',valueJson:'g'},{campo:'fecha_ini_promo',propiedad:'fecha_ini_promo',tipoDato:'long',valueJson:'h'},{campo:'fecha_fin_promo',propiedad:'fecha_fin_promo',tipoDato:'long',valueJson:'i'},{campo:'precio_promo',propiedad:'precio_promo',tipoDato:'string',valueJson:'j'},{campo:'precio_reg',propiedad:'precio_reg',tipoDato:'string',valueJson:'k'},{campo:'id_foto',propiedad:'id_foto',tipoDato:'int',valueJson:'l'}]}");
		tablaEntidadMap.put("E_ReporteQuiebre", "{tabla:'TBL_MOV_REP_QUIEBRE',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'id_reporte_cab',propiedad:'id_reporte_cab',tipoDato:'int',valueJson:'b'},{campo:'sku_prod',propiedad:'sku_prod',tipoDato:'string',valueJson:'c'},{campo:'cod_quiebre',propiedad:'cod_quiebre',tipoDato:'string',valueJson:'d'}]}");
		tablaEntidadMap.put("E_ReporteSod", "{tabla:'TBL_MOV_REP_SOD',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'id_reporte_cab',propiedad:'id_reporte_cab',tipoDato:'int',valueJson:'b'},{campo:'id_foto',propiedad:'id_foto',tipoDato:'int',valueJson:'c'}]}");
		tablaEntidadMap.put("E_ReporteSodDet", "{tabla:'TBL_MOV_REP_SOD_DET',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'id_rep_sod',propiedad:'id_rep_sod',tipoDato:'int',valueJson:'b'},{campo:'sku_prod',propiedad:'sku_prod',tipoDato:'string',valueJson:'c'},{campo:'exhib_prim',propiedad:'exhib_prim',tipoDato:'string',valueJson:'d'},{campo:'exhib_sec',propiedad:'exhib_sec',tipoDato:'string',valueJson:'e'},{campo:'cod_motivo_obs',propiedad:'cod_motivo_obs',tipoDato:'string',valueJson:'f'}]}");
		tablaEntidadMap.put("E_ReporteStock", "{tabla:'TBL_MOV_REP_STOCK',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'id_reporte_cab',propiedad:'id_reporte_cab',tipoDato:'int',valueJson:'b'},{campo:'cod_familia',propiedad:'cod_familia',tipoDato:'string',valueJson:'c'},{campo:'sku_prod',propiedad:'sku_prod',tipoDato:'string',valueJson:'d'},{campo:'stock',propiedad:'stock',tipoDato:'string',valueJson:'e'},{campo:'cod_motivo_obs',propiedad:'cod_motivo_obs',tipoDato:'string',valueJson:'f'},{campo:'camara',propiedad:'camara',tipoDato:'string',valueJson:'g'},{campo:'exhibicion',propiedad:'exhibicion',tipoDato:'string',valueJson:'h'},{campo:'pedido',propiedad:'pedido',tipoDato:'string',valueJson:'i'},{campo:'venta',propiedad:'venta',tipoDato:'string',valueJson:'j'}]}");
		tablaEntidadMap.put("TBL_MOV_REP_VISCOMP", "{tabla:'TBL_MOV_REP_VISCOMP',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'id_reporte_cab',propiedad:'id_reporte_cab',tipoDato:'int',valueJson:'b'},{campo:'comentario',propiedad:'comentario',tipoDato:'string',valueJson:'c'},{campo:'cod_material_apoyo',propiedad:'cod_material_apoyo',tipoDato:'string',valueJson:'d'},{campo:'id_foto',propiedad:'id_foto',tipoDato:'int',valueJson:'e'}]}");
		tablaEntidadMap.put("TblPuntoGPS", "{tabla:'TBL_PUNTO_GPS',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'x',propiedad:'x',tipoDato:'string',valueJson:'b'},{campo:'y',propiedad:'y',tipoDato:'string',valueJson:'c'},{campo:'fecha',propiedad:'fecha',tipoDato:'datetime',valueJson:'d'},{campo:'origen',propiedad:'origen',tipoDato:'string',valueJson:'e'}]}");
///////////////
		tablaEntidadMap.put("E_ReporteAccionesMercado", "{tabla:'TBL_MOV_REP_ACCIONES_MERCADO',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'id_reporte_cab',propiedad:'id_reporte_cab',tipoDato:'int',valueJson:'b'},{campo:'cod_tipo',propiedad:'cod_tipo',tipoDato:'string',valueJson:'c'},{campo:'precio',propiedad:'precio',tipoDato:'string',valueJson:'d'},{campo:'fecha',propiedad:'fecha',tipoDato:'long',valueJson:'e'},{campo:'mecanica',propiedad:'mecanica',tipoDato:'string',valueJson:'f'},{campo:'desc_tipo',propiedad:'desc_tipo',tipoDato:'string',valueJson:'g'},{campo:'id_foto',propiedad:'id_foto',tipoDato:'int',valueJson:'h'}]}");
		tablaEntidadMap.put("E_ReporteAccionesMercadoDet", "{tabla:'TBL_MOV_REP_ACCIONES_MERCADO_DET',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'id_rep_acciones_mercado',propiedad:'id_rep_acciones_mercado',tipoDato:'int',valueJson:'b'},{campo:'cod_material',propiedad:'cod_material',tipoDato:'string',valueJson:'c'},{campo:'selected_material',propiedad:'selected_material',tipoDato:'boolean',valueJson:'d'},{campo:'cod_marca',propiedad:'cod_marca',tipoDato:'string',valueJson:'e'},{campo:'selected_marca',propiedad:'selected_marca',tipoDato:'boolean',valueJson:'f'}]}");
		tablaEntidadMap.put("E_ReporteAuditoria", "{tabla:'TBL_MOV_REP_AUDITORIA',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'id_reporte_cab',propiedad:'id_reporte_cab',tipoDato:'int',valueJson:'b'},{campo:'cod_material_apoyo',propiedad:'cod_mat_apoyo',tipoDato:'string',valueJson:'c'},{campo:'mat_apoyo_check',propiedad:'mat_apoyo_Check',tipoDato:'string',valueJson:'d'},{campo:'cantidad',propiedad:'cantidad',tipoDato:'string',valueJson:'e'}]}");
		tablaEntidadMap.put("E_ReporteEstrella", "{tabla:'TBL_MST_PDV_ESTRELLA',valorOk:'true',propiedadValor:[{campo:'cod_reporte',propiedad:'cod_reporte',tipoDato:'string',valueJson:'a'},{campo:'cod_pdv',propiedad:'cod_pdv',tipoDato:'string',valueJson:'b'},{campo:'cod_estrella',propiedad:'cod_estrella',tipoDato:'string',valueJson:'c'},{campo:'desc_estrella',propiedad:'desc_estrella',tipoDato:'string',valueJson:'d'},{campo:'valor_estrella',propiedad:'valor_estrella',tipoDato:'string',valueJson:'e'}]}");
		tablaEntidadMap.put("E_ReportePotencial", "{tabla:'TBL_MOV_REP_POTENCIAL',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'id_reporte_cab',propiedad:'codReporteCab',tipoDato:'int',valueJson:'b'},{campo:'cod_material',propiedad:'codMaterial',tipoDato:'string',valueJson:'c'},{campo:'valor_check',propiedad:'valorCheck',tipoDato:'string',valueJson:'d'},{campo:'cantidad',propiedad:'cantidad',tipoDato:'string',valueJson:'e'}]}");
		tablaEntidadMap.put("E_ReporteRevestimiento", "{tabla:'TBL_MOV_REP_REVESTIMIENTO',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'id_reporte_cab',propiedad:'id_reporte_cab',tipoDato:'int',valueJson:'b'},{campo:'cod_mat_apoyo',propiedad:'cod_mat_apoyo',tipoDato:'string',valueJson:'c'},{campo:'id_Foto',propiedad:'id_foto',tipoDato:'int',valueJson:'d'},{campo:'comentario',propiedad:'comentario',tipoDato:'string',valueJson:'e'}]}");
		tablaEntidadMap.put("E_tbl_mov_videos", "{tabla:'TBL_MOV_VIDEOS',valorOk:'true',propiedadValor:[{campo:'id_video',propiedad:'id_video',tipoDato:'int',valueJson:'a'},{campo:'nom_video',propiedad:'nom_video',tipoDato:'string',valueJson:'b'},{campo:'estado_envio',propiedad:'envio',tipoDato:'int',valueJson:'c'},{campo:'s_uri_video',propiedad:'s_uri_video',tipoDato:'string',valueJson:'d'}]}");
		tablaEntidadMap.put("E_TblMovRegistroFotografico", "{tabla:'TBL_MOV_REP_FOTOGRAFICO',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'id_reporte_cab',propiedad:'id_reporte_cab',tipoDato:'int',valueJson:'b'},{campo:'idFoto',propiedad:'idFoto',tipoDato:'int',valueJson:'c'}]}");
		tablaEntidadMap.put("E_TblMovRegistroFotografico", "{tabla:'TBL_MOV_REP_VIDEO',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'id_reporte_cab',propiedad:'id_reporte_cab',tipoDato:'int',valueJson:'b'},{campo:'id_video',propiedad:'idFoto',tipoDato:'int',valueJson:'c'}]}");
		tablaEntidadMap.put("E_ReporteEncuesta", "{tabla:'TBL_MOV_REP_ENCUESTA',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'id_reporte_cab',propiedad:'codReporteCab',tipoDato:'int',valueJson:'b'},{campo:'cod_material_apoyo',propiedad:'codMaterial',tipoDato:'string',valueJson:'c'},{campo:'item_check',propiedad:'itemChecked',tipoDato:'string',valueJson:'d'}]}");
		tablaEntidadMap.put("E_ReporteBloqueAzul", "{tabla:'TBL_MOV_REP_BLOQUE_AZUL',valorOk:'true',propiedadValor:[{campo:'id',propiedad:'id',tipoDato:'int',valueJson:'a'},{campo:'id_reporte_cab',propiedad:'id_reporte_cab',tipoDato:'int',valueJson:'b'},{campo:'cod_material_apoyo',propiedad:'cod_mat_apoyo',tipoDato:'int',valueJson:'c'},{campo:'valor_relevado',propiedad:'mat_apoyo_Check',tipoDato:'string',valueJson:'d'},{campo:'comentario',propiedad:'comentario',tipoDato:'string',valueJson:'e'},{campo:'id_foto',propiedad:'id_foto',tipoDato:'string',valueJson:'f'}]}");
		
	}

	@Override
	public boolean create(Entity e) {
		return false;
	}

	public boolean saveJson(Class<?> objetToSave, JSONObject jsonToSave) {
		String key = objetToSave.getSimpleName();
		try {
			ContentValues cv = new ContentValues();
			int cont = 0;
			// ******
			logSaveJson(key);
			TablaEntidad tablaEntidad = getTablaEntidad(key);
			PropiedadValor pValue = null;
			// *******
			Iterator<String> jkeys = jsonToSave.keys();
			String keyJson;

			while (jkeys.hasNext()) {
				keyJson = jkeys.next();
				if (tablaEntidad.valorOk) {
					pValue = getPropiedadValor(tablaEntidad.propiedadValor, keyJson);
					if (pValue!=null && !pValue.valueJson.equals("?")) {
						addCv(cv, pValue.campo, jsonToSave.get(keyJson), pValue.tipoDato);
					}
				}
			}
			SQLiteDatabaseAdapter aSQLiteDatabaseAdapter = SQLiteDatabaseAdapter.getInstance(context);
			db = aSQLiteDatabaseAdapter.getWritableDatabase();
			Log.i("DinamicController", "Insertando datos Maestros en:" + tablaEntidad.tabla);
			db.insert(tablaEntidad.tabla, DatosManager.DATABASE_NAME, cv);
			contObjToSave += 1;
			return true;
		} catch (Exception ex) {
			Log.i(TAG, "@@@@@Error al gurdar un objeto " + key + " " + ex.toString());
			return false;
		}
	}

	String lastObjToSave = "";
	int contObjToSave = 0;

	private void logSaveJson(String key) {
		if (!lastObjToSave.equals(key) && !lastObjToSave.equals("")) {
			Log.i(TAG, "@@@@@" + key + " guardados: " + contObjToSave);
			contObjToSave = 0;
			lastObjToSave = "";
		}
		lastObjToSave = key;
	}

	private TablaEntidad getTablaEntidad(String key) {
		TablaEntidad tablaEntidad = null;
		try {
			JSONObject jsonObject = new JSONObject(tablaEntidadMap.get(key));
			tablaEntidad = new TablaEntidad(jsonObject.getString(te_tabla), jsonObject.getBoolean(te_valorOk), jsonObject.getJSONArray(te_propiedadValor));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return tablaEntidad;
	}

	private PropiedadValor getPropiedadValor(JSONArray jsonArray, String valueJson) {
		PropiedadValor propiedadValor = null;
		try {
			JSONObject jsonObject;
			for (int i = 0; i < jsonArray.length(); i++) {
				jsonObject = jsonArray.getJSONObject(i);
				if (jsonObject.getString(te_valueJson).equals(valueJson)) {
					propiedadValor = new PropiedadValor(jsonObject.getString(te_campo), jsonObject.getString(te_propiedad), jsonObject.getString(te_tipoDato), jsonObject.getString(te_valueJson));
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return propiedadValor;
	}

	private PropiedadValor getPropiedadValor(JSONArray jsonArray, int index) {
		PropiedadValor propiedadValor = null;
		try {
			JSONObject jsonObject;
			jsonObject = jsonArray.getJSONObject(index);
			propiedadValor = new PropiedadValor(jsonObject.getString(te_campo), jsonObject.getString(te_propiedad), jsonObject.getString(te_tipoDato), jsonObject.getString(te_valueJson));

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return propiedadValor;
	}

	private void addCv(ContentValues cv, String campo, Object object, String tipoDato) {
		if (tipoDato.equals("string")) {
			cv.put(campo, (String) object.toString());
		} else if (tipoDato.equals("int")) {
			cv.put(campo, Integer.parseInt(object.toString()));
		} else if (tipoDato.equals("date")) {
			cv.put(campo, (Long) object);
		} else if (tipoDato.equals("boolen")) {
			cv.put(campo, (Boolean) object);
		} else if (tipoDato.equals(tp_Byte)) {
			cv.put(campo, (Byte) object);
		} else if (tipoDato.equals(tp_double)) {
			cv.put(campo, (Double) object);
		} else if (tipoDato.equals(tp_float)) {
			cv.put(campo, (Float) object);
		} else if (tipoDato.equals(tp_long)) {
			cv.put(campo, (Long) object);
		}
		// else if (tipoDato.equals(tp_byte_arr)) {
		// cv.put(campo,(Byte)object);
		// }
	}

	/**
	 * Obtiene los registros de la consulta sql especificada
	 * 
	 * @param objectToGet
	 * @param sql
	 * @return
	 */
	public ArrayList<Object> getDatos(Object objectToGet, String sql) {
		String key = objectToGet.getClass().getSimpleName();
		TablaEntidad tablaEntidad = getTablaEntidad(key);
		return executeSelect(objectToGet, tablaEntidad, sql);
	}

	/**
	 * Obtiene todos los regitros de la tabla
	 * 
	 * @param objectToGet
	 * @return
	 */
	public ArrayList<Object> getDatos(Object objectToGet) {
		String key = objectToGet.getClass().getSimpleName();
		TablaEntidad tablaEntidad = getTablaEntidad(key);
		String sql = "SELECT * FROM " + tablaEntidad.tabla;
		return executeSelect(objectToGet, tablaEntidad, sql);
	}

	private ArrayList<Object> executeSelect(Object objectToGet, TablaEntidad tblEntidad, String sql) {
		ArrayList<Object> lista = new ArrayList<Object>();
		PropiedadValor pValue;
		SQLiteDatabaseAdapter aSQLiteDatabaseAdapter = SQLiteDatabaseAdapter.getInstance(context);
		db = aSQLiteDatabaseAdapter.getReadableDatabase();
		dbCursor = db.rawQuery(sql, null);

		int cont = 0;
		if (dbCursor.moveToFirst()) {
			int indexCol;
			do {
				lista.add(getObjectInstance(objectToGet));
				Object objectToSave = lista.get(cont);
				for (int i = 0; i < tblEntidad.propiedadValor.length(); i++) {
					pValue = getPropiedadValor(tblEntidad.propiedadValor, i);
					indexCol = dbCursor.getColumnIndex(pValue.campo);
					if (pValue != null && indexCol != -1) {
						lista.set(cont, getDato(pValue, objectToSave, dbCursor));
					}
				}
				cont += 1;
			} while (dbCursor.moveToNext());
		}
		return lista;
	}

	private Object getObjectInstance(Object objectBase) {
		Class c = objectBase.getClass();
		Constructor constructor;
		Object newObject = null;
		try {
			constructor = c.getConstructor();
			newObject = constructor.newInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newObject;
	}

	private Object getDato(PropiedadValor pValor, Object objectToSave, Cursor dbc) {
		try {
			objectToSave = setField(objectToSave, pValor.propiedad, pValor.tipoDato, dbc, pValor.campo);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return objectToSave;
	}

	private Object setField(Object object, String fieldName, String tipoDato, Cursor dbc, String campo) throws SecurityException, NoSuchFieldException {
		Class clazz = object.getClass();
		Object value = null;
		int index = dbc.getColumnIndex(campo);
		try {
			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			if (tipoDato.equals(tp_string)) {
				value = dbc.getString(index);
				field.set(object, new String(value.toString()));
			} else if (tipoDato.equals(tp_int)) {
				value = dbc.getInt(index);
				field.set(object, new Integer((Integer) value));
			} else if (tipoDato.equals(tp_boolen)) {
				// value = dbc.get(index);
				field.set(object, new Boolean((String) value));
			} else if (tipoDato.equals(tp_date)) {
				value = dbc.getLong(index);
				field.set(object, new Date((Long) value));
			} else if (tipoDato.equals(tp_Byte)) {
				// value = dbc.getB(index);
				field.set(object, (Byte) value);
			} else if (tipoDato.equals(tp_double)) {
				value = dbc.getDouble(index);
				field.set(object, (Double) value);
			} else if (tipoDato.equals(tp_float)) {
				value = dbc.getFloat(index);
				field.set(object, (Float) value);
			} else if (tipoDato.equals(tp_long)) {
				value = dbc.getLong(index);
				field.set(object, (Long) value);
			}

		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return object;
	}

	public int truncate(Class objTruncate) {
		String key = objTruncate.getSimpleName();
		TablaEntidad table = getTablaEntidad(key);
		if (table == null)
			return 0;

		SQLiteDatabaseAdapter aSQLiteDatabaseAdapter = SQLiteDatabaseAdapter.getInstance(context);
		db = aSQLiteDatabaseAdapter.getWritableDatabase();
		int rows = db.delete(table.tabla, null, null);
		Log.i(DinamicController.class.getSimpleName(), "@@@@@Tabla: " + table.tabla + " truncate rows afectados: " + rows);

		return rows;
	}

	@Override
	public boolean edit(Entity e) {
		return false;
	}

	@Override
	public boolean remove(Entity e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Entity> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	class TablaEntidad {

		public String tabla;
		public boolean valorOk;
		public JSONArray propiedadValor;

		public TablaEntidad(String tabla, Boolean valorOk, JSONArray jsonArray) {
			super();
			this.tabla = tabla;
			this.valorOk = valorOk;
			this.propiedadValor = jsonArray;
		}

	};

	class PropiedadValor {
		public String campo;
		public String propiedad;
		public String tipoDato;
		public String valueJson;

		public PropiedadValor(String campo, String propiedad, String tipoDato, String valueJson) {
			super();
			this.campo = campo;
			this.propiedad = propiedad;
			this.tipoDato = tipoDato;
			this.valueJson = valueJson;
		}
	}
}
