package com.wfa.parser.tasks.api;

import java.util.Map;
import com.wfa.parser.spi.IParserPlugin;

public interface IParserTaskResultRepo {
	Map<String, IParserPlugin> getPlugins();
	void setPlugins(Map<String, IParserPlugin> plugins);
}
