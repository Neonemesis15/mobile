package com.org.seratic.lucky.vo;

public class PresentacionVo {

	// @
	// {
	// CREATE TABLE [TBL_MST_PRESENTACION] (
	// [cod_reporte] vARCHAR(20) NULL,
	// [cod_categoria] vARCHAR(20) NULL,
	// [cod_subcategoria] vARCHAR(20) NULL,
	// [cod_marca] vARCHAR(20) NULL,
	// [cod_submarca] vARCHAR(20) NULL,
	// [cod_presentacion] vARCHAR(20) NULL,
	// [nom_presentacion] vARCHAR(100) NULL
	// );
	// }
	// {
	// a string C�digo de Reporte
	// b string C�digo de Categor�a
	// c string C�digo de Sub Categor�a
	// d string C�digo de Marca
	// e string C�digo de Sub Marca
	// f string C�digo de Presentaci�n
	// g string Nombre de Presentaci�n
	// }
	// {
	private String codReporte;
	private String codCategoria;
	private String codSubcategoria;
	private String codMarca;
	private String codSubmarca;
	private String codPresentacion;
	private String nomPresentacion;

	// }
	// @

	public PresentacionVo() {
	}

	public PresentacionVo(String codReporte, String codCategoria,
			String codSubcategoria, String codMarca, String codSubmarca,
			String codPresentacion, String nomPresentacion) {
		this.codReporte = codReporte;
		this.codCategoria = codCategoria;
		this.codSubcategoria = codSubcategoria;
		this.codMarca = codMarca;
		this.codSubmarca = codSubmarca;
		this.codPresentacion = codPresentacion;
		this.nomPresentacion = nomPresentacion;
	}

	public String getCodReporte() {
		return this.codReporte;
	}

	public void setCodReporte(String codReporte) {
		this.codReporte = codReporte;
	}

	public String getCodCategoria() {
		return this.codCategoria;
	}

	public void setCodCategoria(String codCategoria) {
		this.codCategoria = codCategoria;
	}

	public String getCodSubcategoria() {
		return this.codSubcategoria;
	}

	public void setCodSubcategoria(String codSubcategoria) {
		this.codSubcategoria = codSubcategoria;
	}

	public String getCodMarca() {
		return this.codMarca;
	}

	public void setCodMarca(String codMarca) {
		this.codMarca = codMarca;
	}

	public String getCodSubmarca() {
		return this.codSubmarca;
	}

	public void setCodSubmarca(String codSubmarca) {
		this.codSubmarca = codSubmarca;
	}

	public String getCodPresentacion() {
		return this.codPresentacion;
	}

	public void setCodPresentacion(String codPresentacion) {
		this.codPresentacion = codPresentacion;
	}

	public String getNomPresentacion() {
		return this.nomPresentacion;
	}

	public void setNomPresentacion(String nomPresentacion) {
		this.nomPresentacion = nomPresentacion;
	}

}
