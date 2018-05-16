package dominio;

import java.util.ArrayList;
import java.util.List;

public class User {
	private String nombre;
	private String apellidos;
	private String email;
	private String password;
	private List<String> longUrls = new ArrayList<String>();


	public User() {
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		
			return "\nNombre: " + nombre + "\nApellidos: " + apellidos + "\nemail: " + email + "\npassword: "
					+ password;
	}
	
	
	public void setLongUrls(List<String> longUrls) {		
		this.longUrls = longUrls;
	}

	public List<String> getLongUrls() {
		return longUrls;
	}

}
