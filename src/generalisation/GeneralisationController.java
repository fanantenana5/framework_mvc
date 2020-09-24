package generalisation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import perso.servlet.annotations.Request;
import perso.servlet.annotations.RequestBody;
import perso.servlet.annotations.RequestParameter;
import perso.servlet.annotations.validation.Choice;
import perso.servlet.annotations.validation.Email;
import perso.servlet.annotations.validation.Max;
import perso.servlet.annotations.validation.Messages;
import perso.servlet.annotations.validation.Min;
import perso.servlet.annotations.validation.NotBlank;
import perso.servlet.annotations.validation.NotNull;
import perso.servlet.annotations.validation.Regex;
import perso.servlet.Result;
import perso.servlet.annotations.Authorization;
import perso.servlet.annotations.Controller;

public class GeneralisationController {
	
	public boolean isValid(Result resultBinding, Field field, String requestValue) {
		boolean result = true;
		if(field.getDeclaredAnnotation(Choice.class) != null) {
			String[] choices = field.getDeclaredAnnotation(Choice.class).value();
			boolean exist = false;
			StringBuilder sb = new StringBuilder();
			for(String choice: choices) {
				if(choice.equals(requestValue)) {
					exist = true;
					break;
				}
				sb.append(choice);
				sb.append(",");
			}
			if(!exist) {
				String message = (field.getDeclaredAnnotation(Choice.class).message().equals("")) ? Messages.choiceMessage+sb.toString() : field.getDeclaredAnnotation(Choice.class).message();
				resultBinding.addError(message);
				result = false;
			}
		}
		else if(field.getDeclaredAnnotation(Max.class) != null) {
			try {
				double d = Double.valueOf(requestValue);
				if(d > field.getDeclaredAnnotation(Max.class).value()) {
					String message = (field.getDeclaredAnnotation(Max.class).message().equals("")) ? Messages.maxMessage : field.getDeclaredAnnotation(Max.class).message();
					resultBinding.addError(message);
					result =  false;
				}
			}catch (NumberFormatException e) {
				resultBinding.addError(e.getMessage());
				return false;
			}
		}
		else if(field.getDeclaredAnnotation(Min.class) != null) {
			try{
				double d = Double.valueOf(requestValue);
				if(d < field.getDeclaredAnnotation(Min.class).value()) {
					String message = (field.getDeclaredAnnotation(Min.class).message().equals("")) ? Messages.minMessage : field.getDeclaredAnnotation(Min.class).message();
					resultBinding.addError(message);
					result =  false;
				}
			}catch (NumberFormatException e) {
				resultBinding.addError(e.getMessage());
				return false;
			}
			
		}
		else if(field.getDeclaredAnnotation(NotBlank.class) != null) {
			if(requestValue.equals("")) {
				String message = (field.getDeclaredAnnotation(NotBlank.class).message().equals("")) ? Messages.notBlankMessage : field.getDeclaredAnnotation(NotBlank.class).message();
				resultBinding.addError(message);
				result = false;
			}
		}
		else if(field.getDeclaredAnnotation(NotNull.class) != null) {
			if(requestValue.equals("")) {
				String message = (field.getDeclaredAnnotation(NotNull.class).message().equals("")) ? Messages.notNullMessage : field.getDeclaredAnnotation(NotNull.class).message();
				resultBinding.addError(message);
				result = false;
			}
		}
		else if(field.getDeclaredAnnotation(Regex.class) != null) {
			Matcher matcher = Pattern.compile(field.getDeclaredAnnotation(Regex.class).value()).matcher(requestValue);
			if(!matcher.find()) {
				String message = (field.getDeclaredAnnotation(Regex.class).message().equals("")) ? Messages.regexMessage : field.getDeclaredAnnotation(Regex.class).message();
				resultBinding.addError(message);
				result = false;
			}
		}
		else if(field.getDeclaredAnnotation(Email.class) != null) {
			Matcher matcher = Pattern.compile("^[a-z0-9._-]+@[a-z0-9._-]{2,}\\.[a-z]{2,4}$").matcher(requestValue);
			if(!matcher.find()) {
				String message = (field.getDeclaredAnnotation(Email.class).message().equals("")) ? Messages.emailMessage : field.getDeclaredAnnotation(Email.class).message();
				resultBinding.addError(message);
				result = false;
			}
		}
		return result;
	}
	
	public Object[] instanceParameter(HttpServletRequest request, Method m) throws Exception{
		Result resultBinding = new Result();
		if(!this.isAuthorized(request, m)) {
			resultBinding.addError("Vous n' ete pas authorise a appele la methode "+m.getName());
		}
		Class<?>[] parameterTypes = m.getParameterTypes();
		Annotation[][] annotations = m.getParameterAnnotations();
		Object[] results = new Object[annotations.length];
		//Autowiring
		for(int i=0;i<parameterTypes.length;i++) {
			if(parameterTypes[i].equals(HttpSession.class)) {
				results[i] = request.getSession();
			}else if(parameterTypes[i].equals(Result.class)) {
				results[i] = resultBinding;
			}
		}
		for(int i=0;i<annotations.length;i++) {
			for(int j=0;j<annotations[i].length;j++) {
				if(annotations[i][j].annotationType() == RequestBody.class) {
					Field[] fields = parameterTypes[i].getDeclaredFields();
					Object param = parameterTypes[i].newInstance();
					for(int k=0;k<fields.length;k++) {
						String attName = fields[k].getName();
						String requestValue = request.getParameter(attName);
						//validation formulaire avec annotation
						if(!isValid(resultBinding, fields[k], requestValue)) continue;
						if(requestValue == null) continue;
						String nF = FName("set", attName);
						Method attMethod = null;
						try {
							attMethod = parameterTypes[i].getDeclaredMethod(nF, String.class);
						}catch(NoSuchMethodException e) {
							throw new Exception("Vous devez creer la fonction "+nF+"(String "+attName+") in the class "+param.getClass().getName());
						}
						attMethod.invoke(param, requestValue);
					}
					results[i] = param;
				}else if(annotations[i][j].annotationType() == RequestParameter.class) {
					results[i] = request.getParameter(((RequestParameter)annotations[i][j]).name());
				}
			}
		}
		return results;
	}
	
	public List<Class> getControllerClasses() throws Exception{
		List<Class> controllers = new ArrayList<>(50);
		Class[] classes = ClassScan.getClasses("controllers");
		for(Class c: classes) {
			if(c.getAnnotation(Controller.class) != null) {
				controllers.add(c);
			}
		}
		return controllers;
	}
	
	public boolean isAuthorized(HttpServletRequest request, Method method) {
		Authorization auth = method.getAnnotation(Authorization.class);
		if(auth == null) {
			return true;
		}else{
			HttpSession session = request.getSession();
			String role = (String)session.getAttribute(auth.name());
			if(role == null) {
				return false;
			}
			String[] roles = role.split(",");
			for(int i = 0; i < roles.length; i++) {
				if(auth.value().equals(roles[i])) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void getResponse(HttpServletRequest request, HttpServletResponse response) throws Exception{
		//Response result = null;
		List<Class> controllers = getControllerClasses();
		for(Class c: controllers) {
			Method[] methods = c.getDeclaredMethods();
			for(Method m: methods) {
				if(m.getAnnotation(Request.class) == null) continue;
				if(m.getAnnotation(Request.class).url().equals(request.getAttribute("servletPath"))) {
					if(m.getAnnotation(Request.class).method().equals("") || m.getAnnotation(Request.class).method().equals(request.getMethod())) {
						Object[] params = instanceParameter(request, m);
						Object controller = c.newInstance();
						//result = (Response)(m.invoke(controller, params));
						//break;
						Object result = m.invoke(controller, params);
						if(m.getAnnotation(Request.class).headers().equals("")) {
							ResponseInterface ri = new ResponseRenderHtml();
							ri.executeResponse(request, response, result);
							return;
						}else if(m.getAnnotation(Request.class).headers().equals("json")) {
							ResponseInterface ri = new ResponseJson();
							ri.executeResponse(request, response, result);
							return;
						}
					}
				}
			}
		}
		//return result;
	}
	
	public static String jsonParse(Object obj) throws Exception {
		StringBuilder sb = new StringBuilder();
		Class c = obj.getClass();
		if(!c.isArray()) {
			sb.append("{");
			Field[] fields = c.getFields();
			for(int i=0;i<fields.length;i++) {
				String attName = fields[i].getName();
				String nF = FName("get", attName);
				Method attMethod = c.getDeclaredMethod(nF);
				if(!fields[i].getClass().isArray()) {
					sb.append("\"");
					sb.append(nF);
					sb.append("\":");
					sb.append("\"");
					sb.append(attMethod.invoke(obj));
					sb.append("\"");
				}else{
					
				}
			}
			sb.append("}");
		}else {
			Object[] objects = (Object[])obj;
			for(int i=0;i<objects.length;i++) {
				sb.append(jsonParse(objects[i]));
			}
		}
		return sb.toString();
	}
	
	public static String FName(String type, String att) {
	    String nomF = "";
	    String[] table = att.split("\\.");
	    att = table[table.length - 1];
	    if (type != null) {
	        nomF += type;
	    }
	    nomF += att.substring(0, 1).toUpperCase() + att.substring(1);
	    return nomF;
	}
}
