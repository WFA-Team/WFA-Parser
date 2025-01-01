package com.wfa.parser.spi;

import java.util.Map;

/**
 * This is the plug-in spi, so basically the plug-in is expected to implement
 * the following interface and then it can help tokenize the log lines
 * @author = tortoiseDev
 */

public interface IParserPlugin {
	String getComponentName();
	String getComponentSource();
	Map<String, String> TokenizeLine();
}
