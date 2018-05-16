package dao;

public class UsersFactoriaDAO  extends FactoriaDAO{

	@Override
	public UrlDAO getUrlDAO() {
		return new MongoUrlDAO();
	}

	@Override
	public UserDAO getUserDAO() {
		return new MongoUserDAO();
	}
	
	
}
