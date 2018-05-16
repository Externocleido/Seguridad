package dao;

public class URLFactoriaDAO  extends FactoriaDAO{

	@Override
	public UrlDAO getUrlDAO() {
		return new MongoUrlDAO();
	}

}
