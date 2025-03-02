package com.wfa.parser.tasks.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import com.wfa.middleware.taskexecutor.api.IGroupedTaskElementProvider;
import com.wfa.middleware.taskexecutor.api.TaskProvider;
import com.wfa.parser.tasks.api.IFileToPluginMapperTaskProvider;
import com.wfa.parser.tasks.api.IParserTaskProviderRepository;
import com.wfa.parser.tasks.api.IPluginLoaderTaskProvider;

@Component
public class ParserTaskProviderRepository implements IParserTaskProviderRepository {
	@SuppressWarnings("rawtypes")
	private final Map<Class, Object> taskRepo;
	
	@SuppressWarnings("rawtypes")
	@Autowired
	ParserTaskProviderRepository(ConfigurableApplicationContext ctx) {
		taskRepo = new HashMap<Class, Object>();
		Map<String, Object> taskProviders = ctx.getBeansWithAnnotation(TaskProvider.class);
		for (Entry<String, Object> entry : taskProviders.entrySet()) {
			if (entry.getValue() instanceof IPluginLoaderTaskProvider)
				taskRepo.put(IPluginLoaderTaskProvider.class, entry.getValue());
			else if (entry.getValue() instanceof IFileToPluginMapperTaskProvider)
				taskRepo.put(IFileToPluginMapperTaskProvider.class, entry.getValue());
			else if (entry.getValue() instanceof IGroupedTaskElementProvider)
				taskRepo.put(IGroupedTaskElementProvider.class, entry.getValue());
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getTaskProvider(Class<T> type) {
		if (type.isInstance(taskRepo.get(type)))
			return (T)(taskRepo.get(type));
		return null;
	}
}
