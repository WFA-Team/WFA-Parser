package com.wfa.parser.tasks.api;

public interface IParserTaskProviderRepository {
	<T> T getTaskProvider(Class<T> type);
}
