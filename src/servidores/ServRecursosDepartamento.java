package servidores;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.common.error.OAuthError.ResourceResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.ParameterStyle;
import org.apache.oltu.oauth2.rs.request.OAuthAccessResourceRequest;
import org.apache.oltu.oauth2.rs.response.OAuthRSResponse;

@WebServlet("/ServRecursos")
public class ServRecursosDepartamento extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static HashMap<String, String> scope1 = new HashMap<String, String>();

	final private static String scope2 = "Nombre del departamento: DIIC\n" + "CIF: G932304\n" + "Telefono: 968756849\n"
			+ "Numero de investigadores: 22\n" + "Proyectos realizados: 231\n" + "Articulos publicados: 178";
	final private static String scope3 = "Desarrollo de OAuth3.0\n" + "Ataques con exito al DNI electronico\n"
			+ "Vulnerabilidades en Red Iris\n" + "Estandarizacion del protocolo IOT-s";
	final private static String SCOPE_LEVEL_1 = "email";
	final private static String SCOPE_LEVEL_2 = "departament_data";
	final private static String SCOPE_LEVEL_3 = "current_projects";
	private static final String SUBVENCIONES_STANFORD_CLIENTWEB = "127.0.0.1";
	private static final String SUBVENCIONES_STANFORD_CLIENT_REDIRECT = "http://localhost:8081/redirect";

	@Override
	public void init() throws ServletException {
		scope1.put("adrian", "adrian@telematics.edu");
		scope1.put("javier", "javier@telematics.edu");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		boolean isValid = false;
		OAuthAccessResourceRequest resourceRequest;
		String access_token = null;
		OAuthResponse resp = null;

		try {

			try {
				resourceRequest = new OAuthAccessResourceRequest(request, ParameterStyle.QUERY);
				access_token = resourceRequest.getAccessToken();
				isValid = isAccessTokenValid(access_token);
			} catch (OAuthSystemException | OAuthProblemException e1) {

				e1.printStackTrace();
			}

			if (isValid) {
				String scope = ServAutorizacionTelematics.ACCESS_TOKENS_SCOPE.get(access_token);
				
				

				if (scope.equals(SCOPE_LEVEL_1)) {
					String user = ServAutorizacionTelematics.ACCESS_TOKENS_USER.get(access_token);
					String email = scope1.get(user);
					resp = OAuthRSResponse.status(200).setScope(email).location(SUBVENCIONES_STANFORD_CLIENT_REDIRECT)
							.buildQueryMessage();
				} else if (scope.equals(SCOPE_LEVEL_2) && request.getRemoteHost().equals(SUBVENCIONES_STANFORD_CLIENTWEB))  {
					resp = OAuthRSResponse.status(200).setScope(scope2).location(SUBVENCIONES_STANFORD_CLIENT_REDIRECT)
							.buildQueryMessage();
				} else if (scope.equals(SCOPE_LEVEL_3) && request.getRemoteHost().equals(SUBVENCIONES_STANFORD_CLIENTWEB)) {
					resp = OAuthRSResponse.status(200).setScope(scope3).location(SUBVENCIONES_STANFORD_CLIENT_REDIRECT)
							.buildQueryMessage();
				}

			} else {
				resp = OAuthRSResponse.errorResponse(403).setError(ResourceResponse.INVALID_TOKEN).buildQueryMessage();

			}

		} catch (OAuthSystemException e) {
			e.printStackTrace();
		}

		response.setStatus(resp.getResponseStatus());
		PrintWriter pw = response.getWriter();
		pw.print(resp.getLocationUri());
		pw.flush();
		pw.close();

	}

	private boolean isAccessTokenValid(String access_token) {

		Date current_time = Calendar.getInstance().getTime();
		Long this_moment = current_time.getTime();
		Long expiration = ServAutorizacionTelematics.ACCESS_TOKENS_VALIDATION.get(access_token);

		boolean expired;
		if (expiration != null)
			expired = this_moment > expiration;
		else
			expired = false;

		if (expired) {
			return false;
		}

		return true;
	}
}
