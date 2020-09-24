package exceptions;

public class NotAuthorizedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1833161882484909800L;

	public NotAuthorizedException() {
		super("You are not authorized to call this method");
	}

}
