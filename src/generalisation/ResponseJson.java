package generalisation;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ResponseJson implements ResponseInterface {
	
	@Override
	public void executeResponse(HttpServletRequest request, HttpServletResponse response, Object result) throws Exception {
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		GsonBuilder builder =new GsonBuilder();
		builder.setPrettyPrinting();
		Gson gson =builder.create();
		out.println(gson.toJson(result));
		//out.println(GeneralisationController.jsonParse(result));
	}
}
