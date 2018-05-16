package base70converter;

import java.util.Random;

import dao.UrlDAO;
import dao.DAOException;
import dao.FactoriaDAO;
import dominio.Url;

/*
 * URL Shortener
 */
public class URLShortener {

	// already entered in our system
	private char myChars[]; // This array is used for character to number
							// mapping
	private Random myRand; // Random object used to generate random integers
	private int keyLength; // the key length in URL defaults to 8
	private FactoriaDAO factoria;
	private UrlDAO urlDAO;

	// Default Constructor
	URLShortener() {
		myRand = new Random();
		keyLength = 8;
		myChars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@$+&¬=".toCharArray();
	}

	// Constructor which enables you to define tiny URL key length and base URL
	// name
	public URLShortener(int length) {
		this();
		this.keyLength = length;
	}

	// shortenURL
	// the public method which can be called to shorten a given URL
	public String shortenURL(String longURL) {
		String shortURL = "";
		shortURL = generateKey();
		return shortURL;
	}

	// generateKey
	private String generateKey() {
		String key = "";
		for (int i = 0; i <= keyLength; i++) {
 			key += myChars[myRand.nextInt(69)];
 		}
		return key;
	}

}
