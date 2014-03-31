package com.handwin.web.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FormField {
	public enum FieldType {
		 PARAM, COOKIE, HEAD,COOKIE_OR_HEAD
	};
	boolean required();
	String  name();
	FieldType type() default FieldType.PARAM;
	String key() default "";
}
