package com.wfa.parser.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import com.wfa.parser.api.IParserOrchestrator;
import com.wfa.parser.spi.IParserPlugin;
import com.wfa.parser.spi.ParserPlugin;

@Component
public class Orchestrator implements IParserOrchestrator {

	ConfigurableApplicationContext ctx;
	Map<String, IParserPlugin> parsers;
	
	@Autowired
	Orchestrator(ConfigurableApplicationContext ctx) {
		this.ctx = ctx;
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
