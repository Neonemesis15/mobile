package com.org.seratic.lucky.accessData.entities;

public class E_MstMaterialDeApoyo extends Entity {
	String cod_reporte;
	String tipo_material;
	String cod_material;
	String descripcion;

	public String getCod_reporte() {
		return cod_reporte;
	}

	public void setCod_reporte(String cod_reporte) {
		this.cod_reporte = cod_reporte;
	}

	public String getTipo_material() {
		return tipo_material;
	}

	public void setTipo_material(String tipo_material) {
		this.tipo_material = tipo_material;
	}

	public String getCod_material() {
		return cod_material;
	}

	public void setCod_material(String cod_material) {
		this.cod_material = cod_material;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public E_MstMaterialDeApoyo(String cod_reporte, String tipo_material,
			String cod_material, String descripcion) {
		super();
		this.cod_reporte = cod_reporte;
		this.tipo_material = tipo_material;
		this.cod_material = cod_material;
		this.descripcion = descripcion;
	}

	public E_MstMaterialDeApoyo() {
		super();
	}

}
