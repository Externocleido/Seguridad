package users;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.google.gson.Gson;

import dao.DAOException;
import dao.FactoriaDAO;
import dao.UrlDAO;
import dao.UserDAO;
import dominio.Url;
import dominio.User;

@Path("/users")
public class Users {

	private FactoriaDAO factoria;
	private UserDAO userDao;
	private UrlDAO urlDao;


	@POST
	@Path("/create")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String create(User u) throws DAOException {
		factoria = FactoriaDAO.getInstancia();
		userDao = factoria.getUserDAO();
		boolean exito = userDao.create(u);
		if (!exito) {
			return "existe";
		}
		return "OK";
	}

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String login(User u) throws DAOException {
		factoria = FactoriaDAO.getInstancia();
		userDao = factoria.getUserDAO();
		User user = userDao.get("email", u.getEmail());
		
		//System.out.println(user.toString());
		
		if (user == null)
			return "FAIL";

		if (user.getPassword().equals(u.getPassword())) {
			System.out.println("CONTRASEÑA CORRECTA");
			
			userDao = factoria.getUserDAO();
			List<String> url_keys = userDao.getUrls(user);
			
			urlDao = factoria.getUrlDAO();
			List<Url> urls = urlDao.getAll(url_keys);
			String json = new Gson().toJson(urls);
			System.out.println(json);
	
		
			return json;
		} else {
			System.out.println("CONSTRASEÑA INCORRECTA");
			return "FAIL";
		}
	}
	
	@GET
	@Path("/urls")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String getUrls(User u)  throws DAOException{
		factoria = FactoriaDAO.getInstancia();
		userDao = factoria.getUserDAO();
		List<String> url_keys = userDao.getUrls(u);
		
		urlDao = factoria.getUrlDAO();
		List<Url> urls = urlDao.getAll(url_keys);
		String json = new Gson().toJson(urls);
		System.out.println(json);
		return json;
	}
}
