package com.wfa.parser.tasks.impl;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.wfa.parser.spi.IParserPlugin;
import com.wfa.parser.tasks.api.IParserTaskResultRepo;

@Component
public class ParserTaskResultRepo implements IParserTaskResultRepo{
	private Map<String, IParserPlugin> plugins;

	@Override
	public Map<String, IParserPlugin> getPlugins() {
		return plugins;
	}

	@Override
	public void setPlugins(Map<String, IParserPlugin> plugins) {
		this.plugins = plugins;
	}
}
