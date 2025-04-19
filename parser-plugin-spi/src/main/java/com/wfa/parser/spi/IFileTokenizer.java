package com.wfa.parser.spi;

import java.util.Map;

import com.wfa.middleware.utils.DataType;
import com.wfa.middleware.utils.Pair;

public interface IFileTokenizer {
	boolean tokenizeLine(String line, Map<String, Pair<Object, DataType>> tokenizedLine); // This is where infra wants to tokenize a line
}
