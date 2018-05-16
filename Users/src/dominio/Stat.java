package dominio;

import java.util.Date;

import org.bson.types.ObjectId;

public class Stat {
	
	private String fechaAcceso;
	private String navegador;
	private String ipOrigen;
	
	
	public Stat() {
		
	}


	public String getFechaAcceso() {
		return fechaAcceso;
	}


	public void setFechaAcceso(String fechaAcceso) {
		this.fechaAcceso = fechaAcceso;
	}


	public String getNavegador() {
		return navegador;
	}


	public void setNavegador(String navegador) {
		this.navegador = navegador;
	}


	public String getIpOrigen() {
		return ipOrigen;
	}


	public void setIpOrigen(String paginaOrigen) {
		this.ipOrigen = paginaOrigen;
	}

}
