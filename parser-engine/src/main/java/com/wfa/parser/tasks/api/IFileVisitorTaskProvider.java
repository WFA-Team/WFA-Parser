package com.wfa.parser.tasks.api;

import com.wfa.middleware.taskexecutor.api.ITaskElement;
import com.wfa.middleware.taskexecutor.api.TaskProvider;
import com.wfa.middleware.utils.JoinVoid;
import com.wfa.middleware.utils.beans.data.impl.FileMeta;
import com.wfa.parser.spi.IFileVisitorStub;

@TaskProvider
public interface IFileVisitorTaskProvider {
	ITaskElement<JoinVoid> getTask(FileMeta file, IFileVisitorStub visitor); // To just let plugin visit file
}
