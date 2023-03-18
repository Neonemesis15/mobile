package com.org.seratic.lucky.model;

import com.org.seratic.lucky.accessData.entities.E_ReportePotencial;

public class E_Reporte_Potencial_Mov_Detalle {

	/*
	 * a String Cod_Sub Reporte b String Cod_MatApoyo c String Check d String
	 * Cantidad
	 */

	private String a;
	private String b;
	private String c;
	private String d;

	
	public E_Reporte_Potencial_Mov_Detalle(E_ReportePotencial reporte, String codSubreporte) {
		this.a = codSubreporte == null || codSubreporte.isEmpty() ? null : codSubreporte;
		this.b = reporte.getCodMaterial() == null || reporte.getCodMaterial().isEmpty() ? null : reporte.getCodMaterial();
		this.c = reporte.getValorCheck() == null || reporte.getValorCheck().isEmpty() ? null : reporte.getValorCheck();
		this.d = reporte.getCantidad() == null || reporte.getCantidad().isEmpty() ? null : reporte.getCantidad();
	}


	
	public String getA() {
		return a;
	}

	public void setA(String a) {
		this.a = a;
	}

	public String getB() {
		return b;
	}

	public void setB(String b) {
		this.b = b;
	}

	public String getD() {
		return d;
	}

	public void setD(String d) {
		this.d = d;
	}

	public String getC() {
		return c;
	}

	public void setC(String c) {
		this.c = c;
	}

}
