package generalisation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ResponseInterface {
	public void executeResponse(HttpServletRequest request, HttpServletResponse response, Object result) throws Exception;
}
