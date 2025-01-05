package com.wfa.parser.spi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Types annotated with this would be considered as plug-ins
 * @author = tortoiseDev
 */

@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.TYPE) 
public @interface ParserPlugin {
	String getId();
	String[] getComponentTypes(); // Define always at compile-time what you can parse
	String getVersion();
}
