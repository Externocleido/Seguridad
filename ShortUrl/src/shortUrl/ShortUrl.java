package shortUrl;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.http.HttpRequest;

import dao.DAOException;
import dao.FactoriaDAO;
import dao.UrlDAO;
import dominio.Url;

@Path("/shortURL")
public class ShortUrl {
	private FactoriaDAO factoria;
	private UrlDAO urlDAO;

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/r/{shorturl}")
	public Response redirectUrl(@javax.ws.rs.PathParam("shorturl") String shortUrl) {
		try {
			factoria = FactoriaDAO.getInstancia();
		} catch (DAOException e1) {
			e1.printStackTrace();
		}
		urlDAO = factoria.getUrlDAO();
		// Buscamos en la base de datos la short url
		System.out.println("Consulto " + shortUrl);
		Url url = urlDAO.get("shortURL", shortUrl);
		System.out.println("Consulta de " + shortUrl + " -> " + url.getLongURL());
		
		if (url != null) {
				urlDAO.addStat(shortUrl);
				try {
					return Response.status(Response.Status.TEMPORARY_REDIRECT).location(new URI("http://"+url.getLongURL())).build();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
		}
		
		return null;

	}

}
