package com.wfa.parser.tasks.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wfa.middleware.taskexecutor.api.ATaskElement;
import com.wfa.middleware.taskexecutor.api.IGroupedTaskElementProvider;
import com.wfa.middleware.taskexecutor.api.ITaskElement;
import com.wfa.middleware.taskexecutor.api.ITaskExecutorEngine;
import com.wfa.middleware.utils.DataType;
import com.wfa.middleware.utils.JoinVoid;
import com.wfa.middleware.utils.Pair;
import com.wfa.middleware.utils.beans.api.IFileReader;
import com.wfa.middleware.utils.beans.data.impl.FileMeta;
import com.wfa.middleware.utils.visitors.api.ILineVisitor;
import com.wfa.parser.spi.IParserPlugin;
import com.wfa.parser.tasks.api.IFilesTokenizerTaskProvider;
import com.wfa.parser.tasks.api.IParserTaskResultRepo;

@Component
public class FilesTokenizerTaskProvider implements IFilesTokenizerTaskProvider {

	private final ITaskExecutorEngine taskEngine;
	private final IParserTaskResultRepo taskResultRepo;
	private final IGroupedTaskElementProvider parallelTaskMaker;
	private final IFileReader fileReader;
	
	@Autowired
	public FilesTokenizerTaskProvider(ITaskExecutorEngine taskEngine, IParserTaskResultRepo taskResultRepo,
			IGroupedTaskElementProvider parallelTaskMaker, IFileReader fileReader) {
		this.taskEngine = taskEngine;
		this.taskResultRepo = taskResultRepo;
		this.parallelTaskMaker = parallelTaskMaker;
		this.fileReader = fileReader;
	}
	
	@Override
	public ITaskElement<JoinVoid> getTask() {
		return new ATaskElement<JoinVoid>(taskEngine) {
			@Override
			public void preexecute() { /* do nothing */ }
			
			@Override
			public void execute() {
				List<ITaskElement<JoinVoid>> parsingTasks = new ArrayList<ITaskElement<JoinVoid>>();
				
				for(Map.Entry<FileMeta, List<IParserPlugin>> entry :taskResultRepo.getFileToPluginsMap().entrySet()) {
					parsingTasks.add(getTaskForParsingFile(entry.getKey(), entry.getValue()));
				}
				
				this.setNext(parallelTaskMaker.getGroupedTaskElement(parsingTasks));
				
				setResult(JoinVoid.JoinVoidInstance);
				succeed = true;
			}
		};
	}
	
	private ITaskElement<JoinVoid> getTaskForParsingFile(FileMeta file, List<IParserPlugin>plugins) {
		return new ATaskElement<JoinVoid>(taskEngine) {
			
			@Override
			public void preexecute() { /* do nothing */ }
			
			@Override
			public void execute() {
				fileReader.readFile(file.getFilePath(), true, new ILineVisitor() {
					Map<String, Pair<Object, DataType>> tokenizedLine = new HashMap<String, Pair<Object, DataType>>();
					
					@Override
					public boolean visitLine(String line) {
						for (IParserPlugin plugin: plugins) {
							if (plugin.getFileTokenizer() != null &&
									plugin.getFileTokenizer().tokenizeLine(line, tokenizedLine)) {
								// TODO-> publish tokenized line on bus
								return true; // Move on to next line
							}
						}
						return true;
					}
					
				});
				
				setResult(JoinVoid.JoinVoidInstance);
				succeed = true;
			}
		};
	}

}
