package com.wfa.parser.spi;

import com.wfa.parser.spi.api.IComponent;


/**
 * This is the plug-in spi, so basically the plug-in is expected to implement
 * the following interface and then it can help tokenize the log lines
 * @author = tortoiseDev
 */

public interface IParserPlugin {
	IComponent[] getComponents();
	IFileCompatibilityEvaluator getFileCompatibilityEvaluator();
	IFileTokenizer getFileTokenizer();
	String[] getFileNameRegexes(); // to discover files that can be parsed by this plugin
}
