package com.org.seratic.lucky.vo;

public class PaisVo {

	// @
	// {
	// CREATE TABLE [TBL_MST_PAIS] (
	// [cod_pais] VARCHAR PRIMARY KEY NOT NULL ,
	// [pais] VARCHAR NOT NULL
	// );
	// }
	// {
	// a string C�digo de Pa�s
	// b string Nombre del Pa�s
	// }
	// {
	private String codPais;
	private String pais;
	// }
	// @
	public PaisVo() {
		super();
	}

	public PaisVo(String codPais, String pais) {
		super();
		this.codPais = codPais;
		this.pais = pais;
	}

	public String getCodPais() {
		return codPais;
	}

	public void setCodPais(String codPais) {
		this.codPais = codPais;
	}

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

}
