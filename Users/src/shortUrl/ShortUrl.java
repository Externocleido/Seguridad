package shortUrl;

import javax.ws.rs.GET;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import base70converter.URLShortener;
import dao.DAOException;
import dao.FactoriaDAO;
import dao.UrlDAO;
import dao.UserDAO;
import dominio.Url;
import dominio.User;

@Path("/shortURL")
public class ShortUrl {
	private FactoriaDAO factoria;
	private UrlDAO urlDAO;
	private UserDAO userDAO;


	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("{session}/{url: [\\w\\-]+(\\.[\\w\\-]+)+[/#?]?.*$}") 
	public String longToshortUrl(@javax.ws.rs.PathParam("session") String email, @javax.ws.rs.PathParam("url") String url) {
			
		System.out.println(email);
		try {
			factoria = FactoriaDAO.getInstancia();
			urlDAO = factoria.getUrlDAO();
			userDAO = factoria.getUserDAO();
		} catch (DAOException e) {
			e.printStackTrace();
		}
		
		
		Url consultada = urlDAO.get("longURL", url);
		
		if (consultada == null) {
			URLShortener u = new URLShortener(5);
			String shortUrl = "";
			Url nueva = null;
			
			do {
				shortUrl = u.shortenURL(url);
				nueva = new Url(url, shortUrl,email);
			} while (urlDAO.containsShortUrl(nueva.getShortURL()));
			
			urlDAO.create(nueva);
			userDAO.addUrl(email, url);
		
			
			System.out.println("Short URL Creada: " + shortUrl);
			return "http://localhost:8081/ShortUrl/jaxrs/shortURL/r/" + shortUrl;
		} 
		else {
			//System.out.println(consultada.toString());
			System.out.println("Short URL Existente: " + consultada.getShortURL());
			return "http://localhost:8081/ShortUrl/jaxrs/shortURL/r/" + consultada.getShortURL();
		}
	}

}