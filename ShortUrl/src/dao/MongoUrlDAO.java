package dao;


import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.addToSet;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Request;

import org.apache.http.HttpRequest;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import dominio.Stat;
import dominio.Url;

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
	public Url get(String propiedad, String valor) {
		Url encontrado = urls.find(eq(propiedad,valor)).first();
		return encontrado;
	}


	@Override
	public void addStat(String shortUrl) {
		DateFormat dateFormat = new SimpleDateFormat("d MMM yyyy");
	    Date today = Calendar.getInstance().getTime();
	    
        String fechaAcceso = dateFormat.format(today); 
        String navegador = "Aun sin recuperar";
        String ipOrigen = "Aun sin recuperar";
        	
        	
		Stat s = new Stat(fechaAcceso, navegador, ipOrigen);
		urls.updateOne(eq("shortURL",shortUrl), addToSet("stats",s));
	}
	
	
}
