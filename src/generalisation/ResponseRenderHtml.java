package generalisation;

import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import perso.servlet.Response;

public class ResponseRenderHtml implements ResponseInterface {
	
	@Override
	public void executeResponse(HttpServletRequest request, HttpServletResponse response, Object result) throws Exception {
		//Response r = this.getResponse(request, response);
		Response r = (Response)result;
		if(r.getUrl().startsWith("redirect:")) {
			String url = r.getUrl().split(":")[1];
			response.sendRedirect(url);
		}else {
			HashMap<String,Object> data = r.getData();
			Iterator<String> keys = data.keySet().iterator();
			while(keys.hasNext()) {
				String key = keys.next();
				request.setAttribute(key, data.get(key));
			}
			RequestDispatcher dispatch = request.getRequestDispatcher(r.getUrl());
			dispatch.forward(request, response);
		}
	}
}
