package com.wfa.parser.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wfa.middleware.taskexecutor.api.ITaskElement;
import com.wfa.middleware.taskexecutor.api.ITaskExecutorEngine;
import com.wfa.middleware.utils.AsyncPromise;
import com.wfa.middleware.utils.JoinVoid;
import com.wfa.middleware.utils.beans.data.impl.FileMeta;
import com.wfa.parser.api.IParserEngine;
import com.wfa.parser.spi.IFileVisitorStub;
import com.wfa.parser.tasks.api.IFileVisitorTaskProvider;
import com.wfa.parser.tasks.api.IParserTaskProviderRepository;

@Component
public class ParserEngine implements IParserEngine {

	private final IParserTaskProviderRepository taskRepo;
	private final ITaskExecutorEngine taskEngine;
	
	@Autowired
	public ParserEngine(ITaskExecutorEngine taskEngine, IParserTaskProviderRepository taskRepo) {
		this.taskRepo = taskRepo;
		this.taskEngine = taskEngine;
	}
	
	@Override
	public void visitFile(FileMeta file, IFileVisitorStub visitor, AsyncPromise<JoinVoid> promise) {
		ITaskElement<JoinVoid> fileVisitorTask = taskRepo.<IFileVisitorTaskProvider>getTaskProvider(IFileVisitorTaskProvider.class).
				getTask(file, visitor);
		
		if (promise == null)
			executeTaskOnThisThread(fileVisitorTask);
		else
			taskEngine.schedule(fileVisitorTask, promise);
	}
	
	private void executeTaskOnThisThread(ITaskElement<JoinVoid> fileVisitorTask) {
			fileVisitorTask.preexecute();
			fileVisitorTask.execute();
			fileVisitorTask.postexecute(null);		
	}
}
