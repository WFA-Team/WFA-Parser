package com.wfa.parser.spi;

/**
 * Types annotated with this would be considered as plug-ins
 * @author = tortoiseDev
 */
public @interface ParserPlugin {
	String getComponentType();
	String getVersion();
}
