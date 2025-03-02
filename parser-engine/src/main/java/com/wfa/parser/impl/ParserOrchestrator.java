package com.wfa.parser.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wfa.middleware.taskexecutor.api.ITaskElement;
import com.wfa.middleware.taskexecutor.api.ITaskExecutorEngine;
import com.wfa.middleware.utils.api.IAsyncCallback;
import com.wfa.parser.api.IParserOrchestrator;
import com.wfa.parser.spi.IParserPlugin;
import com.wfa.parser.tasks.api.IParserTaskProviderRepository;
import com.wfa.parser.tasks.api.IPluginLoaderTaskProvider;

@Component
public class ParserOrchestrator implements IParserOrchestrator {
	private final IParserTaskProviderRepository taskRepo;
	private final ITaskExecutorEngine taskEngine;
	private Map<String, IParserPlugin> parsers;
	
	@Autowired
	ParserOrchestrator(IParserTaskProviderRepository taskRepo,
			ITaskExecutorEngine taskEngine) {
		this.taskRepo = taskRepo;
		this.taskEngine = taskEngine;
	}
 
	@Override
	public void conductParsing() {
		this.taskEngine.<Map<String, IParserPlugin>>schedule(getPluginLoader())
			.appendCallback(getCallbackForPluginsLoaded());
		this.taskEngine.startEngine();
	}
	
	private ITaskElement<Map<String, IParserPlugin>> getPluginLoader() {
		return this.taskRepo.<IPluginLoaderTaskProvider>getTaskProvider(IPluginLoaderTaskProvider.class).getTask();
	}
	
	private IAsyncCallback<Map<String, IParserPlugin>> getCallbackForPluginsLoaded() {
		return new IAsyncCallback<Map<String, IParserPlugin>>() {

			@Override
			public void onSuccess(Map<String, IParserPlugin> result) {
				parsers = result;
			}

			@Override
			public void onFailure(Map<String, IParserPlugin> result) {
				System.err.println("Some problem while loading parser plugins");
			}
			
		};
	}
}
