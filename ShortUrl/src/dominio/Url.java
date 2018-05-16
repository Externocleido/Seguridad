package dominio;

import java.util.ArrayList;
import java.util.List;

public class Url {

	private String longURL;
	private String shortURL;
	private String user;
	private String dateOfCreation;
	private List<Stat> stats = new ArrayList<Stat>();
	
	
	public Url() {}

	public Url(String longURL, String shortString, String user) {

		this.longURL = longURL;
		this.shortURL = shortString;
		this.user = user;
	}

	public String getLongURL() {
		return longURL;
	}

	public void setLongURL(String longURL) {
		this.longURL = longURL;
	}

	public String getShortURL() {
		return shortURL;
	}

	public void setShortURL(String shortURL) {
		this.shortURL = shortURL;
	}

	/*
	 * public User getUser() { return user; } public void setUser(User user) {
	 * this.user = user; } public Stat getEstadistica() { return estadistica; }
	 * public void setEstadistica(Stat estadistica) { this.estadistica =
	 * estadistica; }
	 */
	@Override
	public String toString() {
		return "{longURL: " + longURL + ", shortURL: " + shortURL + "}";
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public List<Stat> getStats() {
		return stats;
	}

	public void setStats(List<Stat> stats) {
		this.stats = stats;
	}

	public String getDateOfCreation() {
		return dateOfCreation;
	}

	public void setDateOfCreation(String dateOfCreation) {
		this.dateOfCreation = dateOfCreation;
	}
}
