package com.wfa.parser.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import com.wfa.parser.api.IParserOrchestrator;
import com.wfa.parser.spi.IParserPlugin;
import com.wfa.parser.spi.ParserPlugin;

@Component
public class ParserOrchestrator implements IParserOrchestrator {

	ConfigurableApplicationContext ctx;
	Map<String, IParserPlugin> parsers;
	
	@Autowired
	ParserOrchestrator(ConfigurableApplicationContext ctx) {
		this.ctx = ctx;
		parsers = new HashMap<String, IParserPlugin>();	
	}

	@Override
	public void conductParsing() {
		prepareBeansForParsing();
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
