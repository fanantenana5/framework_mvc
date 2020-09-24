package perso.servlet;

import java.util.ArrayList;
import java.util.List;

public class Result {
	List<String> errors = new ArrayList<>();
	
	public boolean hasError() {
		return !this.getErrors().isEmpty();
	}

	public void addError(String error) {
		this.getErrors().add(error);
	}
	
	public List<String> getErrors() {
		return errors;
	}
}
