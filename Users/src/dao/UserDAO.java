package dao;

import java.util.List;

import org.bson.types.ObjectId;

import dominio.User;

public interface UserDAO {

	boolean create(User u);
	boolean delete(User u);
	User get(String propiedad, String valor);
	void addUrl(String user, String url);
	List<String> getUrls(User u);
	List<User> getAll();
}

