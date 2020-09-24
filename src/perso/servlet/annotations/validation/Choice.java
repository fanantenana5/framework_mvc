package perso.servlet.annotations.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory.Default;

@Retention(RetentionPolicy.RUNTIME)
@Target(value=ElementType.FIELD)
public @interface Choice{
	String[] value();
	String message() default "";
}
