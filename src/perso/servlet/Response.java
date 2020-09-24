package perso.servlet;

import java.util.HashMap;

public class Response {
	String url;
	HashMap<String,Object> data = new HashMap<String,Object>();
	
	public Response(String url) {
		this.url = url;
	}

	public void addData(String key, Object value) {
		this.data.put(key, value);
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public HashMap getData() {
		return data;
	}

	public void setData(HashMap data) {
		this.data = data;
	}
}
