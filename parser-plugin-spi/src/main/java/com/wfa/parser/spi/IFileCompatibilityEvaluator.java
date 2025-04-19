package com.wfa.parser.spi;

import com.wfa.middleware.utils.beans.data.impl.FileMeta;

public interface IFileCompatibilityEvaluator {
	boolean canParse(FileMeta file);
}
