package dao;



import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Request;

import org.apache.http.HttpRequest;

import dominio.Url;


public interface UrlDAO {
	Url get(String propiedad, String valor);
	void addStat(String shortUrl);
}
