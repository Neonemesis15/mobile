package com.org.seratic.lucky.vo;

public class PerfilVo {

	/*
	 * a string C�digo del Tipo de Perfil. 
	 * b string C�digo de Perfil. 
	 * c string C�digo de Equipo. 
	 * d string Descripci�n del Perfil.
	 */

	private String cod_tipoPerfil;
	private String cod_perfil;
	private String cod_equipo;
	private String desc_perfil;

	
	
	public void setCod_tipoPerfil(String cod_tipoPerfil) {
		this.cod_tipoPerfil = cod_tipoPerfil;
	}

	public String getCod_tipoPerfil() {
		return cod_tipoPerfil;
	}

	public void setCod_perfil(String cod_perfil) {
		this.cod_perfil = cod_perfil;
	}

	public String getCod_perfil() {
		return cod_perfil;
	}

	public void setCod_equipo(String cod_equipo) {
		this.cod_equipo = cod_equipo;
	}

	public String getCod_equipo() {
		return cod_equipo;
	}

	public void setDesc_perfil(String desc_perfil) {
		this.desc_perfil = desc_perfil;
	}

	public String getDesc_perfil() {
		return desc_perfil;
	}

}
