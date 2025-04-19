package com.wfa.parser.tasks.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wfa.middleware.taskexecutor.api.IExecutable;
import com.wfa.middleware.taskexecutor.api.ITaskElement;
import com.wfa.middleware.taskexecutor.api.ITaskExecutorEngine;
import com.wfa.middleware.utils.AsyncPromise;
import com.wfa.middleware.utils.JoinVoid;
import com.wfa.middleware.utils.beans.api.IFileReader;
import com.wfa.middleware.utils.beans.data.impl.FileMeta;
import com.wfa.middleware.utils.visitors.api.ILineVisitor;
import com.wfa.parser.spi.IFileVisitorStub;
import com.wfa.parser.tasks.api.IFileVisitorTaskProvider;

@Component
public class FileVisitorTaskProvider implements IFileVisitorTaskProvider {

	private final IFileReader fileReader;
	private final ITaskExecutorEngine taskEngine;
	
	@Autowired
	public FileVisitorTaskProvider(ITaskExecutorEngine taskEngine, IFileReader fileReader) {
		this.fileReader = fileReader;
		this.taskEngine = taskEngine;
	}
	
	@Override
	public ITaskElement<JoinVoid> getTask(FileMeta file, IFileVisitorStub visitor) {
		return new ITaskElement<JoinVoid>() {
			private ITaskElement<JoinVoid> nextTask = null;
			private int priority = 0;
			
			@Override
			public void preexecute() { /* do nothing */}

			@Override
			public void execute() {
				fileReader.readFile(file.getFilePath(), true, new ILineVisitor() {

					@Override
					public boolean visitLine(String line) {
						return visitor.visitLine(line);
					}
					
				});
			}

			@Override
			public void postexecute(AsyncPromise<JoinVoid> promise) {
				if (promise != null) {
					if (nextTask != null) {
						taskEngine.schedule(nextTask, promise);
					} else {
						promise.succeed(JoinVoid.JoinVoidInstance);
					}
				} else if (nextTask != null) {
					nextTask.preexecute();
					nextTask.execute();
					nextTask.postexecute(promise);
				} 
			}

			@Override
			public void setPriorityWeight(int priority) {
				this.priority = priority;
			}

			@Override
			public int getPriorityWeight() {
				return priority;
			}

			@Override
			public int compareTo(IExecutable<?> o) {
				return this.getPriorityWeight() - o.getPriorityWeight();
			}

			@Override
			public ITaskElement<JoinVoid> next() {
				return nextTask;
			}

			@Override
			public void setNext(ITaskElement<JoinVoid> childTask) {
				this.nextTask = childTask;
			}
			
		};
	}

}
