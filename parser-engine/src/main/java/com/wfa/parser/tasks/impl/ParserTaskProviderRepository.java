package com.wfa.parser.tasks.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import com.wfa.middleware.taskexecutor.api.IGroupedTaskElementProvider;
import com.wfa.middleware.taskexecutor.api.TaskProvider;
import com.wfa.parser.tasks.api.IFileVisitorTaskProvider;
import com.wfa.parser.tasks.api.IFileScannerTaskProvider;
import com.wfa.parser.tasks.api.IFileToPluginsMapperTaskProvider;
import com.wfa.parser.tasks.api.IFilesTokenizerTaskProvider;
import com.wfa.parser.tasks.api.IParserTaskProviderRepository;
import com.wfa.parser.tasks.api.IPluginLoaderTaskProvider;
import com.wfa.parser.tasks.api.IShutdownSequenceTaskProvider;

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
			else if (entry.getValue() instanceof IFileToPluginsMapperTaskProvider)
				taskRepo.put(IFileToPluginsMapperTaskProvider.class, entry.getValue());
			else if (entry.getValue() instanceof IGroupedTaskElementProvider)
				taskRepo.put(IGroupedTaskElementProvider.class, entry.getValue());
			else if (entry.getValue() instanceof IFileScannerTaskProvider)
				taskRepo.put(IFileScannerTaskProvider.class, entry.getValue());
			else if (entry.getValue() instanceof IFileVisitorTaskProvider)
				taskRepo.put(IFileVisitorTaskProvider.class, entry.getValue());
			else if (entry.getValue() instanceof IFilesTokenizerTaskProvider)
				taskRepo.put(IFilesTokenizerTaskProvider.class, entry.getValue());
			else if (entry.getValue() instanceof IShutdownSequenceTaskProvider)
				taskRepo.put(IShutdownSequenceTaskProvider.class, entry.getValue());
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
