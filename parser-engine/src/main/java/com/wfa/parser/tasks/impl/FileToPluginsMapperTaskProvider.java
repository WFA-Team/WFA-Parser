package com.wfa.parser.tasks.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wfa.middleware.taskexecutor.api.ATaskElement;
import com.wfa.middleware.taskexecutor.api.ITaskElement;
import com.wfa.middleware.taskexecutor.api.ITaskExecutorEngine;
import com.wfa.middleware.utils.JoinVoid;
import com.wfa.middleware.utils.beans.api.IUserConfigExtractor;
import com.wfa.middleware.utils.beans.data.impl.FileMeta;
import com.wfa.parser.engine.commons.Constants;
import com.wfa.parser.engine.commons.FileToPluginsMappingPolicy;
import com.wfa.parser.engine.commons.HelperMethods;
import com.wfa.parser.spi.IParserPlugin;
import com.wfa.parser.spi.ParserPlugin;
import com.wfa.parser.tasks.api.IFileToPluginsMapperTaskProvider;
import com.wfa.parser.tasks.api.IParserTaskResultRepo;

@Component
class FileToPluginsMapper implements IFileToPluginsMapperTaskProvider {
	
	private final IUserConfigExtractor configs;
	private final ITaskExecutorEngine taskEngine;
	private final IParserTaskResultRepo taskResultRepo;
	private FileToPluginsMappingPolicy mappingPolicy;
	
	@Autowired
	public FileToPluginsMapper(IUserConfigExtractor configs, ITaskExecutorEngine taskEngine,
			IParserTaskResultRepo taskResultRepo) {
		this.configs = configs;
		this.taskEngine = taskEngine;
		this.taskResultRepo = taskResultRepo;
	}
	
	@Override
	public ITaskElement<JoinVoid> getTask() {
		return new ATaskElement<JoinVoid>(taskEngine) {
			@Override
			public void preexecute() {
				if (configs.isConfigSet(Constants.Configs.FILE_MAPPING_PLUGIN_DECIDE)
						&& configs.getIntConfig(Constants.Configs.FILE_MAPPING_PLUGIN_DECIDE) != 0) {
					mappingPolicy = FileToPluginsMappingPolicy.PLUGIN_DECIDE;
				} else {
					mappingPolicy = FileToPluginsMappingPolicy.INFRA_DECIDE;
				}	
			}
			
			@Override
			public void execute() {
				Map<FileMeta, List<IParserPlugin>> map;
				if (mappingPolicy.equals(FileToPluginsMappingPolicy.PLUGIN_DECIDE)) {
					map = fileMappingPluginDecide();
				} else {
					map = fileMappingInfraDecide();
				}
				
				taskResultRepo.setFileToPluginsMap(map);
				setResult(JoinVoid.JoinVoidInstance);
				succeed = true;				
			}
		};
	}
	
	private Map<FileMeta, List<IParserPlugin>> fileMappingInfraDecide() {
		Map<FileMeta, List<IParserPlugin>> fileToPluginsMap = new HashMap<FileMeta, List<IParserPlugin>>();
		
		// TODO-> Optimize the below code
 		for (FileMeta file : taskResultRepo.getFiles()) {
			for (IParserPlugin plugin: taskResultRepo.getPlugins().values()) {
				boolean thisPluginCanParseThisFile = false;
				for (String regex: plugin.getFileNameRegexes()) {
					if (HelperMethods.isRegexFound(file.getFileName(), regex)) {
						thisPluginCanParseThisFile = true;
						break;
					}				
				}
				
				// If plugin regexes did not help, try startsWith by component type
				if (!thisPluginCanParseThisFile) {
					String[] compTypes = plugin.getClass().getAnnotation(ParserPlugin.class).
							getComponentTypes();
					
					for (String compType : compTypes) {
						if (HelperMethods.isRegexFound(file.getFileName(), compType)) {
							thisPluginCanParseThisFile = true;
							break;
						}
							
					}
				}
						
				if (thisPluginCanParseThisFile) {
					if (!fileToPluginsMap.containsKey(file)) {
						fileToPluginsMap.put(file, new ArrayList<IParserPlugin>());
					}
					
					fileToPluginsMap.get(file).add(plugin);
				}
			}
		}
		
		return fileToPluginsMap;
	}
	
	private Map<FileMeta, List<IParserPlugin>> fileMappingPluginDecide() {
		Map<FileMeta, List<IParserPlugin>> fileToPluginsMap = new HashMap<FileMeta, List<IParserPlugin>>();
		
		for (FileMeta file: taskResultRepo.getFiles()) {
			for (IParserPlugin plugin: taskResultRepo.getPlugins().values()) {
				if (plugin.getFileCompatibilityEvaluator() != null 
						&& plugin.getFileCompatibilityEvaluator().canParse(file)) {
					
					if (!fileToPluginsMap.containsKey(file)) {
						fileToPluginsMap.put(file, new ArrayList<IParserPlugin>());
					}
					
					fileToPluginsMap.get(file).add(plugin);					
				}
			}
		}
		
		return fileToPluginsMap;
	}
}