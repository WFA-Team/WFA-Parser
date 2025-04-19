package com.wfa.parser.tasks.impl;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wfa.middleware.taskexecutor.api.ATaskElement;
import com.wfa.middleware.taskexecutor.api.ITaskElement;
import com.wfa.middleware.taskexecutor.api.ITaskExecutorEngine;
import com.wfa.middleware.utils.JoinVoid;
import com.wfa.middleware.utils.beans.api.IDirectoryTraverser;
import com.wfa.middleware.utils.beans.api.IUserConfigExtractor;
import com.wfa.middleware.utils.beans.data.impl.FileMeta;
import com.wfa.parser.engine.commons.Constants;
import com.wfa.parser.tasks.api.IFileScannerTaskProvider;
import com.wfa.parser.tasks.api.IParserTaskResultRepo;

@Component
public class FileScannerTaskProvider implements IFileScannerTaskProvider {

	private final IUserConfigExtractor configs;
	private final IDirectoryTraverser dirWalker;
	private final ITaskExecutorEngine taskEngine;
	private final IParserTaskResultRepo resultRepo;
	
	@Autowired
	public FileScannerTaskProvider(IUserConfigExtractor configs, IDirectoryTraverser dirWalker, ITaskExecutorEngine taskEngine,
			IParserTaskResultRepo resultRepo) {
		this.configs = configs;
		this.dirWalker = dirWalker;
		this.taskEngine = taskEngine;
		this.resultRepo = resultRepo;
	}
	
	@Override
	public ITaskElement<JoinVoid> getTask() {
		return new ATaskElement<JoinVoid>(taskEngine) {
			
			@Override
			public void preexecute() { /* do nothing */ }
			
			@Override
			public void execute() {
				List<FileMeta> files = new ArrayList<FileMeta>();
				dirWalker.traverseDirectory(configs.getStringConfig(Constants.Configs.ROOT_DIRECTORY), 
						new FileVisitor<Path>() {

							@Override
							public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
									throws IOException {
								
								return FileVisitResult.CONTINUE;
							}

							@Override
							public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
								if (attrs.isRegularFile()) {
									try {
										files.add(new FileMeta(file.getFileName().toString(), file.toString()));
									} catch(NullPointerException e) {
										return FileVisitResult.SKIP_SUBTREE;
									}
								}
								
								return FileVisitResult.CONTINUE;
							}

							@Override
							public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
								return FileVisitResult.SKIP_SUBTREE;
							}

							@Override
							public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
								if (exc == null)
									return FileVisitResult.CONTINUE;
								
								return FileVisitResult.SKIP_SUBTREE;
							}
					
				});
				
				resultRepo.setFiles(files);
				setResult(JoinVoid.JoinVoidInstance);
				succeed = true;
			}	
		};
	}

}
