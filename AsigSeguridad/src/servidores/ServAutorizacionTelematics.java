package servidores;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.common.message.types.ResponseType;

@WebServlet("/ServAutorizacion")
public class ServAutorizacionTelematics extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/// AUTHORIZATION CODE - USER ///
	public static HashMap<String, String> AUTHORIZATION_CODES = new HashMap<String, String>();

	/// ACCESS_TOKENS - SCOPE ///
	public static HashMap<String, String> ACCESS_TOKENS_SCOPE = new HashMap<String, String>();

	/// ACCESS_TOKENS - LIFE LIMIT ///
	public static HashMap<String, Long> ACCESS_TOKENS_VALIDATION = new HashMap<String, Long>();

	/// ACCESS_TOKENS - USER ///
	public static HashMap<String, String> ACCESS_TOKENS_USER = new HashMap<String, String>();

	/// REFRESH TOKEN - ACCESS TOKEN //
	private static HashMap<String, String> REFRESH_TOKENS = new HashMap<String, String>();
	
	
	private String last_refresh_token = null;
	private String last_access_token_scope_3 = null;


	private HashMap<String, String> TELEMATIC_CREDENTIALS = new HashMap<String, String>();
	private HashMap<String, String> TELEMATIC_ROLS = new HashMap<String, String>();

	final private static String ID_SUBVENCIONES_STANFORD = "sjdc23u0x2";
	final private static String SCOPE_LEVEL_1 = "email";
	final private static String SCOPE_LEVEL_2 = "departament_data";
	final private static String SCOPE_LEVEL_3 = "current_projects";

	private SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy");
	private Date START_DATE;
	private Date FINISH_DATE;

	public ServAutorizacionTelematics() {
		super();
		TELEMATIC_CREDENTIALS.put("adrian", "123456");
		TELEMATIC_CREDENTIALS.put("javier", "123456");
		TELEMATIC_ROLS.put("adrian", "jefe_departamento");
		TELEMATIC_ROLS.put("javier", "investigador");
		try {
			START_DATE = sdf.parse("01-03-2018");
			FINISH_DATE = sdf.parse("01-06-2018");
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {

			OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(request);
			String redirectURI = oauthRequest.getParam(OAuth.OAUTH_REDIRECT_URI);

			// Aqu� hay que implementar la validaci�n de que la aplicaci�n cliente ha sido
			// registrada previamente

			////////////////// AUTHORIZATION CODE /////////////////

			String responseType = oauthRequest.getParam(OAuth.OAUTH_RESPONSE_TYPE);
			OAuthResponse resp;
			
			String subvenciones_id = oauthRequest.getClientId();

			/// COMPROBAMOS QUE LA PETICIÓN VIENE DESDE SUBVENCIONES.STANFORD.EDU
			if (subvenciones_id.equals(ID_SUBVENCIONES_STANFORD)) {

				if (responseType.equals(ResponseType.CODE.toString())) {
					String state = oauthRequest.getState();
					String[] state_split = state.split(" ");
					String user_received = state_split[0];
					String password_received = state_split[1];

					/// AUTENTICAMOS AL CLIENTE //
					if (TELEMATIC_CREDENTIALS.get(user_received).equals(password_received)) {

						// SI ES JEFE DE DEPARTAMENTO //
						if (TELEMATIC_ROLS.get(user_received).equals("jefe_departamento")) {
							OAuthIssuerImpl oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
							String authorization_code = oauthIssuerImpl.authorizationCode();
							AUTHORIZATION_CODES.put(authorization_code, user_received);

							
							/// SI HAY UNA NUEVA PETICION AUTHORIZATION GRANT > NUEVA SESIÓN
							if (last_access_token_scope_3 != null) {
								ACCESS_TOKENS_VALIDATION.remove(last_access_token_scope_3);
								ACCESS_TOKENS_USER.remove(last_access_token_scope_3);
								ACCESS_TOKENS_SCOPE.remove(last_access_token_scope_3);
							}
							
							/// SI HAY UNA NUEVA PETICION AUTHORIZATION GRANT > NUEVA SESIÓN
							if (last_access_token_scope_3 != null) {
								REFRESH_TOKENS.remove(last_refresh_token);
							}
							
							resp = OAuthASResponse.authorizationResponse(request, 200).location(redirectURI)
									.setCode(authorization_code).buildQueryMessage();

						} else {
							resp = OAuthASResponse.authorizationResponse(request, 403).location(redirectURI)
									.buildQueryMessage();
						}
					} else {
						resp = OAuthASResponse.authorizationResponse(request, 403).location(redirectURI)
								.buildQueryMessage();
					}
				} else {
					resp = OAuthASResponse.authorizationResponse(request, 403).location(redirectURI)
							.buildQueryMessage();
				}
			} else {
				resp = OAuthASResponse.authorizationResponse(request, 403).location(redirectURI).buildQueryMessage();
			}

			response.setStatus(resp.getResponseStatus());
			PrintWriter pw = response.getWriter();
			pw.print(resp.getLocationUri());
			pw.flush();
			pw.close();

		} catch (OAuthProblemException | OAuthSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			OAuthTokenRequest oauthRequest = new OAuthTokenRequest(request);
			String redirectURI = oauthRequest.getParam(OAuth.OAUTH_REDIRECT_URI);

			// Aqu� hay que implementar la validaci�n de que la aplicaci�n cliente ha sido
			// registrada previamente

			OAuthResponse resp = null;
			OAuthIssuerImpl oauthIssuerImpl = null;
			
			String subvenciones_id = oauthRequest.getClientId();


			/// COMPROBAMOS QUE LA PETICIÓN VIENE DESDE SUBVENCIONES.STANFORD.EDU
			if (subvenciones_id.equals(ID_SUBVENCIONES_STANFORD)) {

				String tipoGrant = oauthRequest.getGrantType();

				/// SI SE UTILIZA PASSWORD, ES PARA SCOPE 1 Y 2
				if (tipoGrant.equals(GrantType.PASSWORD.toString())) {

					String user_received = oauthRequest.getParam(OAuth.OAUTH_USERNAME);
					String password_received = oauthRequest.getParam(OAuth.OAUTH_PASSWORD);
					String correct_password = TELEMATIC_CREDENTIALS.get(user_received);

					/// Autenticamos ///
					if (correct_password != null && password_received.equals(correct_password)) {
						
						String requested_scope = oauthRequest.getParam(OAuth.OAUTH_SCOPE);

						/// SCOPE 1 (correo) ///
						if (requested_scope.equals(SCOPE_LEVEL_1)) {
							oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
							String access_token = oauthIssuerImpl.accessToken();
							resp = OAuthASResponse.authorizationResponse(request, 200).location(redirectURI)
									.setAccessToken(access_token).buildQueryMessage();

							ACCESS_TOKENS_SCOPE.put(access_token, requested_scope);
							ACCESS_TOKENS_VALIDATION.put(access_token, Long.MAX_VALUE);
							ACCESS_TOKENS_USER.put(access_token, user_received);
						}

						/// SCOPE 2 (datos del departamento) ///
						else if (requested_scope.equals(SCOPE_LEVEL_2)) {

							// SI ES JEFE DE DEPARTAMENTO //
							if (TELEMATIC_ROLS.get(user_received).equals("jefe_departamento")) {
								oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
								String access_token = oauthIssuerImpl.accessToken();
								resp = OAuthASResponse.authorizationResponse(request, 200).location(redirectURI)
										.setAccessToken(access_token).buildQueryMessage();

								ACCESS_TOKENS_SCOPE.put(access_token, requested_scope);
								ACCESS_TOKENS_VALIDATION.put(access_token, Long.MAX_VALUE);
								ACCESS_TOKENS_USER.put(access_token, user_received);

							} else {
								resp = OAuthASResponse.authorizationResponse(request, 403).location(redirectURI)
										.buildQueryMessage();
							}

						}

					} else {
						resp = OAuthASResponse.authorizationResponse(request, 403).location(redirectURI)
								.buildQueryMessage();
					}
		
				}

				/// SI SE UTILIZA AUTHORIZATION CODE, ES PARA SCOPE 3 POR PRIMERA VEZ
				else if (tipoGrant.equals(GrantType.AUTHORIZATION_CODE.toString())) {

					String authorization_code_received = oauthRequest.getParam(OAuth.OAUTH_CODE);
					String user_asociated = AUTHORIZATION_CODES.get(authorization_code_received);

					/// VERIFICAMOS QUE EL CODE ES VÁLIDO && ESTÁ ASOCIADO A UN JEFE DE DEPARTAMENTO///
					if (user_asociated != null && TELEMATIC_ROLS.get(user_asociated).equals("jefe_departamento") ) {

						/// COMPROBAMOS SI ESTAMOS EN UN PLAZO VÁLIDO DE PEDIR SUBVENCIONES //
						Calendar calendar = Calendar.getInstance();
						Date now = calendar.getTime();
						if (now.after(START_DATE) && now.before(FINISH_DATE)) {

							/// COMPROBAMOS SI SE ESTÁ EN HORARIO DE TRABAJO //
							if (now.getDay() != 0 && now.getDay() != 6 && now.getHours() > 8 && now.getHours() < 21) {

								oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
								String refresh_token = oauthIssuerImpl.refreshToken();
								String access_token = oauthIssuerImpl.accessToken();
								resp = OAuthASResponse.authorizationResponse(request, 200).location(redirectURI)
										.setAccessToken(access_token).setExpiresIn("60").setCode(refresh_token)
										.buildQueryMessage();

								ACCESS_TOKENS_SCOPE.put(access_token, SCOPE_LEVEL_3);
								REFRESH_TOKENS.put(refresh_token, access_token);
								last_refresh_token = refresh_token;
								last_access_token_scope_3 = access_token;
								Date current_time = Calendar.getInstance().getTime();
								Long invalidation_moment = current_time.getTime() + 60000;
								ACCESS_TOKENS_VALIDATION.put(access_token, invalidation_moment);
								ACCESS_TOKENS_USER.put(access_token, user_asociated);
								
								// AUTORIZATION USAR SOLO UNA VEZ //
								AUTHORIZATION_CODES.remove(authorization_code_received);

							}
							else {
								resp = OAuthASResponse.authorizationResponse(request, 403).location(redirectURI)
										.buildQueryMessage();
							}
						}
						else {
							resp = OAuthASResponse.authorizationResponse(request, 403).location(redirectURI)
									.buildQueryMessage();
						}
					} else {
						resp = OAuthASResponse.authorizationResponse(request, 403).location(redirectURI)
								.buildQueryMessage();
					}
				}
				//// NOS PIDEN OTRO ACCESS TOKEN CON UN REFRESH ///
				else if (tipoGrant.equals(GrantType.REFRESH_TOKEN.toString())) {

					String refresh_token = oauthRequest.getParam(OAuth.OAUTH_REFRESH_TOKEN);

					/// SI EL REFRESH TOKEN ES VÁLIDO
					String asociated_access_token = REFRESH_TOKENS.get(refresh_token);
					if (asociated_access_token != null) {

						// Y ESTÁ ASOCIADO AL SCOPE 3 //
						String asociated_scope = ACCESS_TOKENS_SCOPE.get(asociated_access_token);
						if (asociated_scope.equals(SCOPE_LEVEL_3)) {
							oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
							String new_access_token = oauthIssuerImpl.accessToken();
							resp = OAuthASResponse.authorizationResponse(request, 200).location(redirectURI)
									.setAccessToken(new_access_token).setExpiresIn("60").buildQueryMessage();

							REFRESH_TOKENS.put(refresh_token, new_access_token);
							ACCESS_TOKENS_SCOPE.remove(asociated_access_token);
							ACCESS_TOKENS_SCOPE.put(new_access_token, SCOPE_LEVEL_3);
							last_access_token_scope_3 = new_access_token;
							Date current_time = Calendar.getInstance().getTime();
							Long invalidation_moment = current_time.getTime() + 60000;
							ACCESS_TOKENS_VALIDATION.remove(asociated_access_token);
							ACCESS_TOKENS_VALIDATION.put(new_access_token, invalidation_moment);
							String user_asociated = ACCESS_TOKENS_USER.get(asociated_access_token);
							ACCESS_TOKENS_USER.remove(asociated_access_token);
							ACCESS_TOKENS_USER.put(new_access_token, user_asociated);

						} else {
							resp = OAuthASResponse.authorizationResponse(request, 403).location(redirectURI)
									.buildQueryMessage();
						}

					} else {
						resp = OAuthASResponse.authorizationResponse(request, 403).location(redirectURI)
								.buildQueryMessage();
					}

				} else {
					resp = OAuthASResponse.authorizationResponse(request, 403).location(redirectURI)
							.buildQueryMessage();
				}
			} else {
				resp = OAuthASResponse.authorizationResponse(request, 403).location(redirectURI).buildQueryMessage();

			}

			// Envia la respuesta
			response.setStatus(resp.getResponseStatus());
			PrintWriter pw = response.getWriter();
			pw.print(resp.getLocationUri());
			pw.flush();
			pw.close();

		} catch (OAuthProblemException | OAuthSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
