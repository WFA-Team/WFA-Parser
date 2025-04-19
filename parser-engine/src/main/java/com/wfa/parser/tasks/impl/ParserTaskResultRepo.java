package com.wfa.parser.tasks.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.wfa.middleware.utils.beans.data.impl.FileMeta;
import com.wfa.parser.spi.IParserPlugin;
import com.wfa.parser.tasks.api.IParserTaskResultRepo;

@Component
public class ParserTaskResultRepo implements IParserTaskResultRepo {
	private Map<String, IParserPlugin> plugins;
	private List<FileMeta> files;
	private Map<FileMeta, List<IParserPlugin>> fileToPluginsMap;

	@Override
	public Map<String, IParserPlugin> getPlugins() {
		return plugins;
	}

	@Override
	public void setPlugins(Map<String, IParserPlugin> plugins) {
		this.plugins = plugins;
	}

	@Override
	public List<FileMeta> getFiles() {
		return files;
	}

	@Override
	public void setFiles(List<FileMeta> files) {
		this.files = files;
	}

	@Override
	public Map<FileMeta, List<IParserPlugin>> getFileToPluginsMap() {
		return fileToPluginsMap;
	}

	@Override
	public void setFileToPluginsMap(Map<FileMeta, List<IParserPlugin>> fileToPluginsMap) {
		this.fileToPluginsMap = fileToPluginsMap;
	}
}
