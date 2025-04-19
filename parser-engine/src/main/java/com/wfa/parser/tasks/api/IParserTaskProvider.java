package com.wfa.parser.tasks.api;

import com.wfa.middleware.taskexecutor.api.ITaskElement;
import com.wfa.middleware.utils.JoinVoid;

public interface IParserTaskProvider {
	ITaskElement<JoinVoid> getTask();
}
