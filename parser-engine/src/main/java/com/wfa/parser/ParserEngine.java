package com.wfa.parser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.wfa.parser.api.IParserOrchestrator;

/**
 * This is the parsing engine. Its responsibilities are:-
 * @parsing parsing log lines and converting them into a common DataModel
 * @persistence to serialize and persist that DataModel
 * @publish to publish the data onto the bus
 * 
 * For Parsing, it has been kept away from the parsing logic of a particular component
 * For that it has a runtime support for plugin. You can write a plugin and integrate
 * with this engine. Just implement the SPI.
 */

@SpringBootApplication(scanBasePackages = {"com.wfa"})
public class ParserEngine {
	
	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(ParserEngine.class, args);
		IParserOrchestrator bn = ctx.getBean(IParserOrchestrator.class);
		bn.conductParsing();
	}
}
