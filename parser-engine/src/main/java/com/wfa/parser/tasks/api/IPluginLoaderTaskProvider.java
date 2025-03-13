package com.wfa.parser.tasks.api;

import com.wfa.middleware.taskexecutor.api.ITaskElement;
import com.wfa.middleware.taskexecutor.api.TaskProvider;
import com.wfa.middleware.utils.JoinVoid;

@TaskProvider
public interface IPluginLoaderTaskProvider {
	ITaskElement<JoinVoid> getTask();
}
