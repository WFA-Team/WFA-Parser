package com.wfa.parser.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import com.wfa.middleware.utils.beans.api.IUserConfigExtractor;
import com.wfa.parser.api.IParserOrchestrator;
import com.wfa.parser.spi.IParserPlugin;
import com.wfa.parser.spi.ParserPlugin;

@Component
public class ParserOrchestrator implements IParserOrchestrator {

	private final ConfigurableApplicationContext ctx;
	private final Map<String, IParserPlugin> parsers;
	private final IUserConfigExtractor configs;
	
	@Autowired
	ParserOrchestrator(ConfigurableApplicationContext ctx, IUserConfigExtractor configs) {
		this.ctx = ctx;
		this.parsers = new HashMap<String, IParserPlugin>();
		this.configs = configs;
		}

	@Override
	public void conductParsing() {
		prepareBeansForParsing();
		
		// KDEBUG
		configs.parseConfigFile();
		String dir = configs.getStringConfig("input_directory_root");
	}
	
	private void prepareBeansForParsing() {
		Map<String, Object> parserBeans = ctx.getBeansWithAnnotation(ParserPlugin.class);
		for (Object bean : parserBeans.values()) {
			if (bean instanceof IParserPlugin) {
				IParserPlugin plugin = (IParserPlugin)bean;
				String componentType = bean.getClass().getAnnotation(ParserPlugin.class)
											.getComponentType();
				parsers.put(componentType, plugin);
			}
		}	
	}
}
