package dominio;

import java.util.Date;

import org.bson.types.ObjectId;

public class Stat {
	
	private String fechaAcceso;
	private String navegador;
	private String ipOrigen;
	
	
	public Stat() {
	}
	
	public Stat(String fechaAcceso, String navegador, String ipOrigen) {
		this.setFechaAcceso(fechaAcceso);
		this.setNavegador(navegador);
		this.setIpOrigen(ipOrigen);
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

	public void setIpOrigen(String ipOrigen) {
		this.ipOrigen = ipOrigen;
	}
}
