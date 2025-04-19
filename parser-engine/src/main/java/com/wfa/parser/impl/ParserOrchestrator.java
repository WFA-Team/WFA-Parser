package com.wfa.parser.impl;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wfa.middleware.taskexecutor.api.IGroupedTaskElementProvider;
import com.wfa.middleware.taskexecutor.api.ITaskElement;
import com.wfa.middleware.taskexecutor.api.ITaskExecutorEngine;
import com.wfa.middleware.utils.JoinVoid;
import com.wfa.middleware.utils.PlayType;
import com.wfa.middleware.utils.api.IAsyncCallback;
import com.wfa.middleware.utils.beans.api.IUserConfigExtractor;
import com.wfa.parser.api.IParserOrchestrator;
import com.wfa.parser.engine.commons.Constants;
import com.wfa.parser.tasks.api.IFileScannerTaskProvider;
import com.wfa.parser.tasks.api.IFileToPluginsMapperTaskProvider;
import com.wfa.parser.tasks.api.IFilesTokenizerTaskProvider;
import com.wfa.parser.tasks.api.IParserTaskProviderRepository;
import com.wfa.parser.tasks.api.IPluginLoaderTaskProvider;
import com.wfa.parser.tasks.api.IShutdownSequenceTaskProvider;

@Component
public class ParserOrchestrator implements IParserOrchestrator {
	private final IParserTaskProviderRepository taskRepo;
	private final ITaskExecutorEngine taskEngine;
	private final IGroupedTaskElementProvider parallelTaskMaker;
	
	@Autowired
	ParserOrchestrator(IParserTaskProviderRepository taskRepo,
			ITaskExecutorEngine taskEngine, IGroupedTaskElementProvider parallelTaskMaker,
			IUserConfigExtractor configs) {
		this.taskRepo = taskRepo;
		this.taskEngine = taskEngine;
		
		if (configs.isConfigSet(Constants.Configs.MAX_PARALLELISM))
			this.taskEngine.setMaxParallelism(configs.getIntConfig(Constants.Configs.MAX_PARALLELISM));
		
		this.parallelTaskMaker = parallelTaskMaker;
	}
 
	@Override
	public void conductParsing() {
		this.taskEngine.<JoinVoid>schedule(getTaskGraph())
			.appendCallback(getCallbackForParsingDone());
		
		if (!taskEngine.getState().equals(PlayType.STARTED))
			this.taskEngine.startEngine();
	}
		
	private IAsyncCallback<JoinVoid> getCallbackForParsingDone() {
		return new IAsyncCallback<JoinVoid>() {

			@Override
			public void onSuccess(JoinVoid result) {
				System.out.println("Parsing Done");
			}

			@Override
			public void onFailure(JoinVoid result) {
				System.err.println("Failure during parsing..... exiting");
				System.exit(1);
			}			
		};
	}
	
	private ITaskElement<JoinVoid> getTaskGraph() {
		ITaskElement<JoinVoid> root = this.parallelTaskMaker.getGroupedTaskElement(
				Arrays.asList(getPluginLoader(), getFileScanner()));
		 
		ITaskElement<JoinVoid> fileToPluginsMapperTask = getFileToPluginsMapper();
		root.setNext(fileToPluginsMapperTask);
		
		ITaskElement<JoinVoid> filesParsingTask = getFilesParsingTask();
		fileToPluginsMapperTask.setNext(filesParsingTask);
		
		// TODO-> Create task graph with main loop as end node in a task
		return root;		
	}

	private ITaskElement<JoinVoid> getPluginLoader() {
		return this.taskRepo.<IPluginLoaderTaskProvider>getTaskProvider(IPluginLoaderTaskProvider.class).getTask();
	}
	
	private ITaskElement<JoinVoid> getFileScanner() {
		return this.taskRepo.<IFileScannerTaskProvider>getTaskProvider(IFileScannerTaskProvider.class).getTask();
	}
	
	private ITaskElement<JoinVoid> getFileToPluginsMapper() {
		return this.taskRepo.<IFileToPluginsMapperTaskProvider>getTaskProvider(IFileToPluginsMapperTaskProvider.class).getTask();
	}
	
	private ITaskElement<JoinVoid> getFilesParsingTask() {
		return this.taskRepo.<IFilesTokenizerTaskProvider>getTaskProvider(IFilesTokenizerTaskProvider.class).getTask();
	}
	
	private ITaskElement<JoinVoid> getShutdownTask() {
		return this.taskRepo.<IShutdownSequenceTaskProvider>getTaskProvider(IShutdownSequenceTaskProvider.class).getTask();
		
	}

	@Override
	public void exit() {
		this.taskEngine.schedule(getShutdownTask());
	}
}
