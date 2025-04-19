package com.wfa.parser.tasks.api;

import java.util.List;
import java.util.Map;

import com.wfa.middleware.utils.beans.data.impl.FileMeta;
import com.wfa.parser.spi.IParserPlugin;

public interface IParserTaskResultRepo {
	Map<String, IParserPlugin> getPlugins();
	void setPlugins(Map<String, IParserPlugin> plugins);
	
	List<FileMeta> getFiles();
	void setFiles(List<FileMeta> files);
	
	Map<FileMeta, List<IParserPlugin>> getFileToPluginsMap();
	void setFileToPluginsMap(Map<FileMeta, List<IParserPlugin>> fileToPluginsMap);
}
