package com.example.demo.app;

import org.springframework.data.annotation.Id;

public class ColeccionJson {
	@Id
	public String id;
	public String contenido;
	
	
	
	public ColeccionJson() {
	}



	public ColeccionJson(String id, String contenido) {
		super();
		this.id = id;
		this.contenido = contenido;
	}



	@Override
	public String toString() {
		return "ColeccionJson [id=" + id + ", contenido=" + contenido + "]";
	}



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getContenido() {
		return contenido;
	}



	public void setContenido(String contenido) {
		this.contenido = contenido;
	}
	
	
	
	
}
