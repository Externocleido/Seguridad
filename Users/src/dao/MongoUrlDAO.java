package dao;

import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.google.gson.Gson;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;

import dominio.Url;
import dominio.User;

public class MongoUrlDAO implements UrlDAO {	
	
	private MongoClient mongoClient;
	private MongoDatabase db;
	private MongoCollection<Url> urls;
	private CodecRegistry pojoCodecRegistry;

	
	public MongoUrlDAO() {
		this.mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
		this.pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().automatic(true).build()));
		this.db = mongoClient.getDatabase("SSDD").withCodecRegistry(pojoCodecRegistry);
		this.urls = db.getCollection("urls", Url.class);
	}
	
	@Override
	public void create(Url u) {
		urls.insertOne(u);
		System.out.println("("+u.getDateOfCreation()+") Url "+ u.toString() + " insertada en la BBDD");
	}
	
	@Override
	public Url get(String propiedad, String valor) {
		Url encontrado = urls.find(eq(propiedad,valor)).first();
		return encontrado;
	}

	
	@Override
	public  boolean containsShortUrl(String key) {
		
		Url encontrada = get("shortUrl",key);
		if (encontrada!=null){
			System.out.println("@@ Key "+ key + " encontrada en la BBDD");
			return true;
		}
		else {
			return false;
		}
	}
	
	@Override
	public List<Url> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	/*@Override
	public ArrayList<Url> getAll(String email) {
		ArrayList<Url> urls = new ArrayList<Url>();

		Block<Url> getUrl = new Block<Url>() {
		    @Override
		    public void apply(final Url url) {
		        urls.add(url);
		    }
		};
		
		this.urls.find(eq("user", email)).forEach(getUrl);

		return urls;
	}*/

	@Override
	public boolean delete(Url url) {
		return false;
	}

	@Override
	public ArrayList<Url> getUrls(User u) {
		ArrayList<Url> urls = new ArrayList<Url>();

		for(String url : u.getLongUrls()) {
			urls.add((Url) this.urls.find(eq("longURL", url)));
		}

		return urls;
	}

	@Override
	public List<Url> getAll(List<String> url_keys) {
		ArrayList<Url> urls = new ArrayList<Url>();

		for(String url : url_keys) {
			urls.add((Url) this.urls.find(eq("longURL", url)).first());
		}

		return urls;
	}

}
