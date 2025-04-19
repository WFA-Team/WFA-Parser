package com.wfa.parser.tasks.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wfa.middleware.taskexecutor.api.ATaskElement;
import com.wfa.middleware.taskexecutor.api.ITaskElement;
import com.wfa.middleware.taskexecutor.api.ITaskExecutorEngine;
import com.wfa.middleware.utils.JoinVoid;
import com.wfa.parser.tasks.api.IShutdownSequenceTaskProvider;

@Component
public class ShutdownSequenceTaskProvider implements IShutdownSequenceTaskProvider {

	private final ITaskExecutorEngine taskEngine;
	
	@Autowired
	public ShutdownSequenceTaskProvider(ITaskExecutorEngine taskEngine) {
		this.taskEngine = taskEngine;
	}
	
	@Override
	public ITaskElement<JoinVoid> getTask() {
		return new ATaskElement<JoinVoid>(this.taskEngine) {

			@Override
			public void preexecute() { try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}}

			@Override
			public void execute() {
				System.out.println("Attempting to gracefully shutdown.");
				System.exit(0);
				
				// unreachable, but still
				setResult(JoinVoid.JoinVoidInstance);
				succeed = true;
			}
			
		};
	}

}
