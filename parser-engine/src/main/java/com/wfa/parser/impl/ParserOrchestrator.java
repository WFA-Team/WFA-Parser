package com.wfa.parser.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wfa.middleware.taskexecutor.api.ITaskElement;
import com.wfa.middleware.taskexecutor.api.ITaskExecutorEngine;
import com.wfa.middleware.utils.JoinVoid;
import com.wfa.middleware.utils.api.IAsyncCallback;
import com.wfa.parser.api.IParserOrchestrator;
import com.wfa.parser.tasks.api.IParserTaskProviderRepository;
import com.wfa.parser.tasks.api.IPluginLoaderTaskProvider;

@Component
public class ParserOrchestrator implements IParserOrchestrator {
	private final IParserTaskProviderRepository taskRepo;
	private final ITaskExecutorEngine taskEngine;
	
	@Autowired
	ParserOrchestrator(IParserTaskProviderRepository taskRepo,
			ITaskExecutorEngine taskEngine) {
		this.taskRepo = taskRepo;
		this.taskEngine = taskEngine;
	}
 
	@Override
	public void conductParsing() {
		this.taskEngine.<JoinVoid>schedule(getTaskGraph())
			.appendCallback(getCallbackForParsingDone());
		
		this.taskEngine.startEngine();
	}
		
	private IAsyncCallback<JoinVoid> getCallbackForParsingDone() {
		return new IAsyncCallback<JoinVoid>() {

			@Override
			public void onSuccess(JoinVoid result) {
				// Should be the case for graceful exit requested by user
				System.out.println("Parsing Done, Good Bye");
				System.exit(0);
			}

			@Override
			public void onFailure(JoinVoid result) {
				System.err.println("Failure during parsing..... exiting");
				System.exit(1);
			}			
		};
	}
	
	private ITaskElement<JoinVoid> getTaskGraph() {
		ITaskElement<JoinVoid> root = getPluginLoader();
		// TODO-> Create task graph with main loop as end node in a task
		return root;
		
	}

	private ITaskElement<JoinVoid> getPluginLoader() {
		return this.taskRepo.<IPluginLoaderTaskProvider>getTaskProvider(IPluginLoaderTaskProvider.class).getTask();
	}	
}
