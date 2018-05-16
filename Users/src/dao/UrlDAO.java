package dao;

import java.util.ArrayList;
import java.util.List;


import dominio.Url;
import dominio.User;


public interface UrlDAO {
	void create(Url url);
	boolean delete(Url url);
	Url get(String propiedad, String valor);
	List<Url> getAll();
	boolean containsShortUrl(String key);
	/*ArrayList<Url> getAll(String email);*/
	ArrayList<Url> getUrls(User u);
	//ArrayList<Url> getAll(String email);
	List<Url> getAll(List<String> url_keys);
}
