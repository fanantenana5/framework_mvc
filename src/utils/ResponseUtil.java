package utils;

import perso.servlet.Response;

public class ResponseUtil {
	public static Response handleResponse(String viewName, String key,Object value) {
		Response p = new Response(viewName);
		p.addData(key, value);
		return p;
	}
}
