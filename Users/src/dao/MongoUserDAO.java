package dao;

import java.util.List;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.WriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;
import static java.util.Arrays.asList;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import dominio.User;

public class MongoUserDAO implements UserDAO {

	private MongoClient mongoClient;
	private MongoDatabase db;
	private MongoCollection<User> users;
	private CodecRegistry pojoCodecRegistry;

	public MongoUserDAO() {
		this.mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
		this.pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().automatic(true).build()));
		this.db = mongoClient.getDatabase("SSDD").withCodecRegistry(pojoCodecRegistry);
		this.users = db.getCollection("usuarios", User.class);
	}

	@Override
	public boolean create(User u) {

		User somebody = get("email",u.getEmail());

		if (somebody != null) {
			System.out.println("Usuario ya registrado");
			return false;
		} else {
			System.out.println("Creando usuario" + u.getEmail());
			users.insertOne(u);
			return true;
		}
	}

	
	@Override
	public boolean delete(User u) {
		DeleteResult resultado = users.deleteOne(eq("email", u.getEmail()));
		return resultado.wasAcknowledged();
	}

	@Override
	public User get(String propiedad, String valor) {
		User encontrado = users.find(eq(propiedad,valor)).first();
		return encontrado;
	}

	
	@Override
	public List<User> getAll() {
		return null;
	}

	@Override
	public void addUrl(String user, String longUrl) {
		UpdateResult ur = users.updateOne(eq("email",user), addToSet("longUrls",longUrl));
	}

	@Override
	public List<String> getUrls(User u) {
		User encontrado = users.find(eq("email",u.getEmail())).first();
		return encontrado.getLongUrls();
	}
	
	

}
