package cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import java.util.HashMap;
import java.util.Scanner;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.OAuth.HttpMethod;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.common.message.types.ResponseType;

public class SubvencionesStanford {

	final private static String ID_SUBVENCIONES_STANFORD = "sjdc23u0x2";
	final private static String SECRET_SUBVENCIONES_STANFORD = "asd9uas90d";
	private static final String SUBVENCIONES_STANFORD_CLIENT_REDIRECT = "http://localhost:8081/redirect";

	final private static String SCOPE_LEVEL_1 = "email";
	final private static String SCOPE_LEVEL_2 = "departament_data";
	final private static String SCOPE_LEVEL_3 = "current_projects";

	final private static String AUTHORIZATION_SERVER = "http://localhost:8081/AsigSeguridad/ServAutorizacion";
	final private static String RESOURCE_SERVER = "http://localhost:8081/AsigSeguridad/ServRecursos";

	//// USUARIO - AUTHORIZATION CODE ///
	final private static HashMap<String, String> authorization_codes = new HashMap<String, String>();

	//// SCOPE - ACCESS_TOKEN ///
	final private static HashMap<String, String> access_tokens = new HashMap<String, String>();

	/// ACCESS TOKEN - REFRESH TOKEN ///
	final private static HashMap<String, String> refresh_tokens = new HashMap<String, String>();

	private static Scanner sc;

	public static void main(String[] args) {

		System.out.println("¡Bienvenido al servicio de subvenciones de Stanford!?");

		String opcion;
		String password = null;
		String username = null;
		sc = new Scanner(System.in);

		do {
			System.out.println("\n¿qué desea hacer?");
			System.out.println("(1) Iniciar sesión con el correo electrónico");
			System.out.println("(2) Registrar al departamento en el sistema de notificaciones");
			System.out.println("(3) Iniciar proceso de solicitud de subvención");
			System.out.println("(4) Completar solicitudes y enviar");
			System.out.println("(5) Salir");

			opcion = sc.nextLine();

			while (!(Integer.valueOf(opcion) == 1 || Integer.valueOf(opcion) == 2 || Integer.valueOf(opcion) == 3
					|| Integer.valueOf(opcion) == 4 || Integer.valueOf(opcion) == 5)) {
				System.out.println("Opción no válida. Introduzca una de las cuatro disponibles.");
				opcion = sc.nextLine();
			}

			String scope = null;
			String access_token = null;
			String authorization_code = null;
			String refresh_token = null;

			switch (Integer.valueOf(opcion)) {

			/// RECUPERAR CORREO ELECTRÓNICO ///
			case 1:
				scope = SCOPE_LEVEL_1;
				access_token = access_tokens.get(scope);
				if (access_token == null) {
					System.out.println("Username:");
					username = sc.nextLine();
					System.out.println("Password: ");
					password = sc.nextLine();
					access_token = getResourceOwnerPasswordCredentialsAccessToken(username, password, scope);

					if (access_token == null)
						break;
					else
						access_tokens.put(scope, access_token);
				}

				getResource(access_token);

				break;

			/// OBTENER INFO DEL DEPARTAMENTO ///
			case 2:
				scope = SCOPE_LEVEL_2;
				access_token = access_tokens.get(scope);
				if (access_token == null) {
					System.out.println("Username:");
					username = sc.nextLine();
					System.out.println("Password: ");
					password = sc.nextLine();
					access_token = getResourceOwnerPasswordCredentialsAccessToken(username, password, scope);
					access_tokens.put(scope, access_token);

					if (access_token == null)
						break;
					else
						access_tokens.put(scope, access_token);

				}

				getResource(access_token);

				break;

			/// ADJUNTAR PROYECTOS ACTUALES ///
			case 3:
				scope = SCOPE_LEVEL_3;
				try {
					access_token = access_tokens.get(SCOPE_LEVEL_3);

					if (access_token == null) {

						authorization_code = authorization_codes.get(username);

						if (authorization_code == null) {
							System.out.println("Redirecting to Telematics...");
							System.out.println("Username:");
							username = sc.nextLine();
							System.out.println("Password: ");
							password = sc.nextLine();
							authorization_code = getAuthorizationCode(scope, username + " " + password);

							if (authorization_code == null) {
								System.out.println(
										"-- Authorization Response: Usted no está autorizado para realizar esa acción");
								break;
							} else {
								authorization_codes.put(username, authorization_code);
							}
						}

						access_token = getAccessToken(authorization_code);
						String[] response = access_token.split(" ");
						access_token = response[0];
						refresh_token = response[1];
						access_tokens.put(SCOPE_LEVEL_3, access_token);
						refresh_tokens.put(access_token, refresh_token);
						authorization_codes.remove(username);
					}

					getResource(access_token);

				} catch (OAuthSystemException e) {
					System.out.println("-- Authorization Response: Usted no está autorizado para realizar esa acción");
					e.getCause();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					System.out.println("-- Authorization Response: Usted no está autorizado para realizar esa acción");
					e.printStackTrace();
				}
				break;

			/// Enviar la subvencion y finalizar. Eliminamos access_token y refresh token
			/// ///
			case 4:
				System.out.println("Solicitud de subvenciones realizadas. Sesión cerrada.");
				String access_token_to_remove = access_tokens.get(SCOPE_LEVEL_3);
				access_tokens.remove(SCOPE_LEVEL_3);
				refresh_tokens.remove(access_token_to_remove);
				break;
			/// SALIR ///
			case 5:
				System.exit(0);
			}
		} while (true);
	}

	private static void getResource(String access_token) {
		OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
		OAuthClientRequest bearerClientRequest;
		OAuthResourceResponse resourceResponse = null;
		String refresh_token;

		try {
			bearerClientRequest = new OAuthBearerClientRequest(RESOURCE_SERVER).setAccessToken(access_token)
					.buildQueryMessage();

			try {
				System.out.println("++ Resource request: " + new URL(bearerClientRequest.getLocationUri()).toString());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			resourceResponse = oAuthClient.resource(bearerClientRequest, OAuth.HttpMethod.GET,
					OAuthResourceResponse.class);
		} catch (OAuthSystemException e) {
			System.out.println("-- Resource response: " + e.getCause());
			////// REFRRESCARRRR ///

			refresh_token = refresh_tokens.get(access_token);
			try {
				access_token = getRefreshedAccessToken(refresh_token);
			} catch (OAuthSystemException | IOException e1) {
				e1.printStackTrace();
			}

			// ACTUALIZAMOS ACCESS TOKEN //
			access_tokens.put(SCOPE_LEVEL_3, access_token);
			refresh_tokens.put(access_token, refresh_token);
			getResource(access_token);
			// e.printStackTrace();
			return;
		} catch (OAuthProblemException e) {
			System.out.println("-- Resource response: " + e.getDescription());
			e.getError();
			//// REFRRESCARRRR ///
			refresh_token = refresh_tokens.get(access_token);
			try {
				access_token = getRefreshedAccessToken(refresh_token);
			} catch (OAuthSystemException | IOException e1) {
				e1.printStackTrace();
			}

			// ACTUALIZAMOS ACCESS TOKEN //
			access_tokens.put(SCOPE_LEVEL_3, access_token);
			refresh_tokens.put(refresh_token, access_token);
			getResource(access_token);
			e.printStackTrace();
			return;
		}

		String response = resourceResponse.getBody();
		System.out.println("-- Resource response: " + response);

		String[] response_splited = response.split("scope=");
		System.out.println("\n" + response_splited[1]);

	}

	private static String getAuthorizationCode(String scope, String state)
			throws OAuthSystemException, MalformedURLException {
		OAuthClientRequest request = OAuthClientRequest.authorizationLocation(AUTHORIZATION_SERVER)
				.setRedirectURI(SUBVENCIONES_STANFORD_CLIENT_REDIRECT).setClientId(ID_SUBVENCIONES_STANFORD)
				.setResponseType(ResponseType.CODE.toString()).setScope(scope).setState(state).buildQueryMessage();

		URL url = new URL(request.getLocationUri());

		System.out.println("++ Authorization Request: " + url.toString());

		StringBuffer response = new StringBuffer();
		BufferedReader in = null;
		String[] split_response;

		try {
			HttpURLConnection c = (HttpURLConnection) url.openConnection();
			in = new BufferedReader(new InputStreamReader(c.getInputStream()));

			/* LEEMOS LA RESPUESTA DEL SERVIDOR DE AUTORIZACIÓN */
			String inputLine;
			while (in != null && (inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}

			System.out.println("-- Authorization Response: " + response);

			split_response = response.toString().split("code=");
			in.close();
		} catch (IOException e) {
			split_response = null;
		}

		/// SI HAY UN AUTHORIZATION CODE, NOS HEMOS AUTENTICADO ///
		if (split_response != null && split_response.length > 1) {
			String[] split_response2 = split_response[1].split("&state=");
			return split_response2[0];
		}

		return null;
	}

	private static String getAccessToken(String authorization_code) throws OAuthSystemException, IOException {
		OAuthClientRequest request2 = OAuthClientRequest.tokenLocation(AUTHORIZATION_SERVER)
				.setRedirectURI(SUBVENCIONES_STANFORD_CLIENT_REDIRECT).setClientId(ID_SUBVENCIONES_STANFORD)
				.setClientSecret(SECRET_SUBVENCIONES_STANFORD).setGrantType(GrantType.AUTHORIZATION_CODE)
				.setCode(authorization_code).buildQueryMessage();

		URL url2 = new URL(request2.getLocationUri());

		System.out.println("++ Access Token Request: " + url2.toString());

		HttpURLConnection c2 = (HttpURLConnection) url2.openConnection();
		c2.setRequestMethod(HttpMethod.POST);
		c2.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		// Recibimos la respuesta con el access token
		BufferedReader in2 = new BufferedReader(new InputStreamReader(c2.getInputStream()));
		String inputLine2;
		StringBuffer response2 = new StringBuffer();

		while ((inputLine2 = in2.readLine()) != null) {
			response2.append(inputLine2);
		}

		System.out.println("-- Access Token Response: " + response2);

		in2.close();

		String[] split_response = response2.toString().split("access_token=");

		if (split_response.length > 1) {
			String[] params = split_response[1].split("&");
			String access_token = params[0];
			String refresh_token = params[1].split("code=")[1];
			return access_token + " " + refresh_token;
		} else
			return split_response[0];
	}

	private static String getResourceOwnerPasswordCredentialsAccessToken(String username, String password,
			String scope) {

		OAuthClientRequest request = null;
		try {
			request = OAuthClientRequest.tokenLocation(AUTHORIZATION_SERVER)
					.setRedirectURI(SUBVENCIONES_STANFORD_CLIENT_REDIRECT).setClientId(ID_SUBVENCIONES_STANFORD)
					.setClientSecret(SECRET_SUBVENCIONES_STANFORD).setGrantType(GrantType.PASSWORD)
					.setUsername(username).setPassword(password).setScope(scope).buildQueryMessage();
		} catch (OAuthSystemException e) {
			e.printStackTrace();
		}

		URL url = null;
		try {
			url = new URL(request.getLocationUri());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}

		System.out.println("++ Access Token Request: " + url.toString());

		HttpURLConnection c = null;
		try {
			c = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		try {
			c.setRequestMethod(HttpMethod.POST);
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		c.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		// Recibimos la respuesta con el access token
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(c.getInputStream()));
		} catch (IOException e) {
			System.out.println("-- Access Token Response: " + e.getMessage());
			return null;
		}
		String inputLine;
		StringBuffer response = new StringBuffer();

		try {
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		System.out.println("-- Access Token Response: " + response);

		try {
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		String[] split_response = response.toString().split("access_token=");

		if (split_response.length > 1) {
			String[] params = split_response[1].split("&");
			String access_token = params[0];

			return access_token;
		} else
			return split_response[0];
	}

	private static String getRefreshedAccessToken(String refresh_token) throws OAuthSystemException, IOException {

		OAuthClientRequest request2 = OAuthClientRequest.tokenLocation(AUTHORIZATION_SERVER)
				.setRedirectURI(SUBVENCIONES_STANFORD_CLIENT_REDIRECT).setClientId(ID_SUBVENCIONES_STANFORD).setClientSecret(SECRET_SUBVENCIONES_STANFORD)
				.setGrantType(GrantType.REFRESH_TOKEN).setRefreshToken(refresh_token).buildQueryMessage();

		URL url2 = new URL(request2.getLocationUri());

		System.out.println("++ Refreshed Access Token Request: " + url2.toString());

		HttpURLConnection c2 = (HttpURLConnection) url2.openConnection();
		c2.setRequestMethod(HttpMethod.POST);
		c2.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		// Recibimos la respuesta con el access token
		BufferedReader in2 = new BufferedReader(new InputStreamReader(c2.getInputStream()));
		String inputLine2;
		StringBuffer response2 = new StringBuffer();

		while ((inputLine2 = in2.readLine()) != null) {
			response2.append(inputLine2);
		}

		System.out.println("-- Refreshed Access Token Response: " + response2);

		in2.close();

		String[] split_response = response2.toString().split("access_token=");

		if (split_response.length > 1) {
			String[] params = split_response[1].split("&");
			String access_token = params[0];
			return access_token;
		} else
			return split_response[0];
	}

}
