package com.wfa.parser.tasks.api;

import java.util.Map;

import com.wfa.middleware.taskexecutor.api.ITaskElement;
import com.wfa.middleware.taskexecutor.api.TaskProvider;
import com.wfa.parser.spi.IParserPlugin;

@TaskProvider
public interface IPluginLoaderTaskProvider {
	ITaskElement<Map<String, IParserPlugin>> getTask();
}
