package com.wfa.parser.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import com.wfa.middleware.utils.beans.api.IUserConfigExtractor;
import com.wfa.parser.api.IParserOrchestrator;
import com.wfa.parser.engine.commons.Constants;
import com.wfa.parser.spi.IParserPlugin;
import com.wfa.parser.spi.ParserPlugin;

@Component
public class ParserOrchestrator implements IParserOrchestrator {

	private final ConfigurableApplicationContext ctx;
	private final Map<String, IParserPlugin> parsers;
	private final IUserConfigExtractor configs;
	
	private String rootDir;
	
	@Autowired
	ParserOrchestrator(ConfigurableApplicationContext ctx, IUserConfigExtractor configs) {
		this.ctx = ctx;
		this.parsers = new HashMap<String, IParserPlugin>();
		this.configs = configs;
		}
 
	@Override
	public void conductParsing() {
		// Below is a stepwise recipe for parsing conduction
		prepareBeansForParsing();
		readConfigs();
	}
	
	private void readConfigs() {
		this.rootDir = configs.getStringConfig(Constants.Configs.ROOT_DIRECTORY);
	}
	
	private void prepareBeansForParsing() {
		Map<String, Object> parserBeans = ctx.getBeansWithAnnotation(ParserPlugin.class);
		for (Object bean : parserBeans.values()) {
			if (bean instanceof IParserPlugin) {
				IParserPlugin plugin = (IParserPlugin)bean;
				String pluginId = bean.getClass().getAnnotation(ParserPlugin.class)
											.getId();
				parsers.put(pluginId, plugin);
			}
		}	
	}
}
