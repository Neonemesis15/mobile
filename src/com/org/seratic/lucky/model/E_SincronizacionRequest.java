package com.org.seratic.lucky.model;

public class E_SincronizacionRequest {


	private String a;
	private int b;
	private String c;
	
	public E_SincronizacionRequest(){
		
	}
	
	/**
	 * 	
	 * @param a C�digo de Equipo
	 * @param b C�digo de Compa��a
	 * @param c C�digo de Usuario
	 */
	public E_SincronizacionRequest(String a, int b, String c) {
		super();
		this.a = a;
		this.b = b;
		this.c = c;
	}

	public String getA() {
		return a;
	}

	public void setA(String a) {
		this.a = a;
	}

	public int getB() {
		return b;
	}

	public void setB(int b) {
		this.b = b;
	}

	public String getC() {
		return c;
	}

	public void setC(String c) {
		this.c = c;
	}

}
