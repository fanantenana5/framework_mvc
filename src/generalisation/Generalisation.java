package generalisation;

import java.lang.reflect.Method;

public class Generalisation {
	public static Object valueOf(Object obj, String stringValue) throws Exception {
		Object result = null;
		try {
			Method m = obj.getClass().getDeclaredMethod("valueOf", String.class);
			result = m.invoke(obj, stringValue);
		}catch(NoSuchMethodException e){
			throw new Exception("Vous devez creer la fonction valueOf dans la classe "+obj.getClass().getName());
		}catch(Exception e) {
			throw e;
		}
		return result;
	}
}
