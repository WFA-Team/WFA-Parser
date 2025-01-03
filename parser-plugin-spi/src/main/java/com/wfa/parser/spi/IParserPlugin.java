package com.wfa.parser.spi;

import java.util.Map;

import com.wfa.middleware.utils.DataType;
import com.wfa.middleware.utils.Pair;


/**
 * This is the plug-in spi, so basically the plug-in is expected to implement
 * the following interface and then it can help tokenize the log lines
 * @author = tortoiseDev
 */

public interface IParserPlugin {
	String getComponentName();
	String getComponentSource();
	Map<String, Pair<Object, DataType>> TokenizeLine();
}
