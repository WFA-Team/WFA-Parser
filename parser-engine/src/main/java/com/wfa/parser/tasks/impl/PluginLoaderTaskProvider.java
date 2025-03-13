package com.wfa.parser.tasks.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import com.wfa.middleware.taskexecutor.api.ATaskElement;
import com.wfa.middleware.taskexecutor.api.ITaskElement;
import com.wfa.middleware.taskexecutor.api.ITaskExecutorEngine;
import com.wfa.middleware.utils.JoinVoid;
import com.wfa.parser.spi.IParserPlugin;
import com.wfa.parser.spi.ParserPlugin;
import com.wfa.parser.tasks.api.IParserTaskResultRepo;
import com.wfa.parser.tasks.api.IPluginLoaderTaskProvider;

@Component
public class PluginLoaderTaskProvider implements IPluginLoaderTaskProvider{
	private final ConfigurableApplicationContext ctx;
	private final ITaskExecutorEngine taskEngine;
	private final IParserTaskResultRepo resultRepo;
	
	@Autowired
	public PluginLoaderTaskProvider(ConfigurableApplicationContext ctx, ITaskExecutorEngine taskEngine,
			IParserTaskResultRepo resultRepo) {
		this.ctx = ctx;
		this.taskEngine = taskEngine;
		this.resultRepo = resultRepo;
	}
	
	@Override
	public ITaskElement<JoinVoid> getTask() {
		return new ATaskElement<JoinVoid>(taskEngine) {
			@Override
			public void preexecute() {
				// do nothing
			}
			
			@Override
			public void execute() {
				try {
					Map<String, IParserPlugin> parsers = new HashMap<String, IParserPlugin>();
					Map<String, Object> parserBeans = ctx.getBeansWithAnnotation(ParserPlugin.class);
					for (Object bean : parserBeans.values()) {
						if (bean instanceof IParserPlugin) {
							IParserPlugin plugin = (IParserPlugin)bean;
							String pluginId = bean.getClass().getAnnotation(ParserPlugin.class)
														.getId();
							parsers.put(pluginId, plugin);
						}
					}
					
					resultRepo.setPlugins(parsers);
					setResult(JoinVoid.JoinVoidInstance);
					succeed = true;
				} catch(BeansException e) {
					System.err.println("Problem while instantiating plugin " + e.getStackTrace().toString());
				}
			}
		};
	}
}
