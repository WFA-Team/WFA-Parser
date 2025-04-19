package com.wfa.parser.api;

import com.wfa.middleware.utils.AsyncPromise;
import com.wfa.middleware.utils.JoinVoid;
import com.wfa.middleware.utils.beans.data.impl.FileMeta;
import com.wfa.parser.spi.IFileVisitorStub;

// Parser Engine api for plugins
public interface IParserEngine {
	void visitFile(FileMeta file, IFileVisitorStub visitor, AsyncPromise<JoinVoid> promise);
}
