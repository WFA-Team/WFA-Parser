package com.wfa.parser.engine.commons;

import java.util.regex.Pattern;

public class HelperMethods {
	public static boolean isRegexFound(String candidate, String regex) {
        return Pattern.compile(regex).matcher(candidate).find();
	}
}
